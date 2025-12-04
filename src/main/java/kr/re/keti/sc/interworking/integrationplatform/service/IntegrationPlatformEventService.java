package kr.re.keti.sc.interworking.integrationplatform.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.re.keti.sc.interworking.integrationplatform.mapper.IntegrationPlatformEventMapper;
import kr.re.keti.sc.interworking.integrationplatform.model.DatahubIngestRequestEvent;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventBase;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventHistory;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage;
import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule;
import kr.re.keti.sc.interworking.integrationplatform.model.ProcessStatus;
import kr.re.keti.sc.interworking.integrationplatform.utils.AriaCipher;
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@Service
public class IntegrationPlatformEventService {
	
	@Value("${integrationPlatform-to-datahub.encryptionKey}")
	private String encryptionKey;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IntegrationPlatformEventMapper integrationPlatformEventMapper;
	
	@Autowired
    ApplicationEventPublisher eventPublisher;
	
	public void createIntegrationPlatformEventHistory(IntegrationPlatformEventHistory integrationPlatformEventHistory) {
		integrationPlatformEventMapper.insertIntegrationPlatformEventHistory(integrationPlatformEventHistory);
		log.info("successfully created IntegrationPlatformEventHistory: " + integrationPlatformEventHistory);
	}
	
	public void updateIntegrationPlatformEventHistory(IntegrationPlatformEventHistory integrationPlatformEventHistory) {
		integrationPlatformEventMapper.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
		log.info("successfully updated IntegrationPlatformEventHistory: " + integrationPlatformEventHistory);
	}
	
	public void createIntegrationPlatformEventBase(IntegrationPlatformEventBase integrationPlatformEventBase) {
		integrationPlatformEventMapper.insertIntegrationPlatformEventBase(integrationPlatformEventBase);
		log.info("successfully created IntegrationPlatformEventBase: " + integrationPlatformEventBase);
	}
	
	@Scheduled(fixedDelayString = "${integrationPlatform-to-datahub.scheduleMillisecond}")
	private void processReceivedIntegrationPlatformEvent() {
		//TODO: mandatory field 체크
		
		log.info("processReceivedIntegrationPlatformEvent called");
		
		//IntegrationPlatformEventHistory 가져오기
		List <IntegrationPlatformEventHistory> integrationPlatformEventHistoryList = integrationPlatformEventMapper.selectNotProcessedIntegrationPlatformEventHistory();
		
		for (IntegrationPlatformEventHistory integrationPlatformEventHistory : integrationPlatformEventHistoryList) {
			
			//1. Event Message Parsing Error 확인
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage() == null) {//Parsing Error
				log.error(String.format("Error while parsing integration platform event history sequence(#%s) data.", integrationPlatformEventHistory.getSequence()));
				integrationPlatformEventHistory.setProcessStatus(ProcessStatus.PARSING_ERROR);
				integrationPlatformEventMapper.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);				
				continue;
			}
			
			//2. 복호화 데이터 획득
			String decryptedBodyData = obtainDecryptedData(integrationPlatformEventHistory);
			if (decryptedBodyData == null) {
				log.error(String.format("Failed to decrypt data. data: %s", integrationPlatformEventHistory.getReceivedBodyData()));
				integrationPlatformEventHistory.setProcessStatus(ProcessStatus.DECRYPTION_ERROR);
				integrationPlatformEventMapper.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
				continue;
			}
			
			//3. Event Message Body 복호화 데이터 획득
			IntegrationPlatformEventMessage.EventBody body = parseIntegrationPlatformEventBody(decryptedBodyData);
			if (body == null) {//Parsing Error
				log.error(String.format("Error while parsing integration platform event history sequence(#%s) data.", integrationPlatformEventHistory.getSequence()));
				integrationPlatformEventHistory.setProcessStatus(ProcessStatus.PARSING_ERROR);				
				integrationPlatformEventMapper.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
				continue;
			}
			
			integrationPlatformEventHistory.getIntegrationPlatformEventMessage().setBody(body);
			log.info("processing event history data: " + integrationPlatformEventHistory);
			
			//4. Mapping 서비스 존재 확인
			List<MappingRule> mappingRuleList = integrationPlatformEventMapper.selectMappingRule(integrationPlatformEventHistory);
			if (mappingRuleList == null || mappingRuleList.size() == 0 ) {
				log.error(String.format("Mapping rule not found for integration platform event history sequence(#%d) data.", integrationPlatformEventHistory.getSequence()));
				integrationPlatformEventHistory.setProcessStatus(ProcessStatus.MAPPING_RULE_NOT_FOUND);
				integrationPlatformEventMapper.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
				continue;
			}
			
			//5. 이전 전송하다 오류난 동일 이벤트 존재 확인
			ProcessStatus beforeEventProcessStatus = obtainProcessStatusBeforeEvent(integrationPlatformEventHistory);
			if(!ProcessStatus.PROCESSED.equals(beforeEventProcessStatus)) {
				log.error(String.format("Previously processed event exists regarding this event history sequence(#%s) data.", integrationPlatformEventHistory.getSequence()));
				integrationPlatformEventHistory.setProcessStatus(beforeEventProcessStatus);
				integrationPlatformEventMapper.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
				continue;
			}
			
			//6. Mapping Rule이 다수 존재할 시 1개 선정 
			MappingRule mappingRule = obtainProperMappingRule(mappingRuleList);
			
			//7. Publish Event
			eventPublisher.publishEvent(new DatahubIngestRequestEvent(integrationPlatformEventHistory, mappingRule));
		}
	}
	
	private IntegrationPlatformEventMessage.EventBody parseIntegrationPlatformEventBody (String integrationPlatformEventString) {
		try {
			return objectMapper.readValue(integrationPlatformEventString, IntegrationPlatformEventMessage.EventBody.class);
		} catch (Exception e) {
			log.error(String.format("Exception occurs while parsing received integration platform event body. received integration platform event data: %s", integrationPlatformEventString), e);
			return null;
		}
	}
	
	private static MappingRule obtainProperMappingRule (List <MappingRule> mappingRuleList) {
		if (mappingRuleList.size() >= 2) {
			for (MappingRule mappingRule : mappingRuleList) {
				if (!MappingRule.INTEGRATION_PLATFORM_EVENT_ID_DETAIL_WILDCARD.equals(mappingRule.getIntegrationPlatformEventIdDetail())) {
					return mappingRule;
				}
			}
		}
		return mappingRuleList.get(0);
	}
	
	private ProcessStatus obtainProcessStatusBeforeEvent (IntegrationPlatformEventHistory currentIntegrationPlatformEventHistory) {
		
		List <IntegrationPlatformEventHistory> alreadyProcessedItegrationPlatformEventHistory = integrationPlatformEventMapper.selectAlreadyProcessedIntegrationPlatformEventHistory(currentIntegrationPlatformEventHistory);
		
		for (IntegrationPlatformEventHistory integrationPlatformEventHistory : alreadyProcessedItegrationPlatformEventHistory) {
			if(!ProcessStatus.PROCESSED.equals(integrationPlatformEventHistory.getProcessStatus())) {
				return integrationPlatformEventHistory.getProcessStatus();
			}
		}
		return ProcessStatus.PROCESSED;
	}
	
	private Boolean isEncypted (IntegrationPlatformEventHistory integrationPlatformEventHistory) {
		if (integrationPlatformEventHistory == null || integrationPlatformEventHistory.getIntegrationPlatformEventMessage() == null) return false;
		return IntegrationPlatformEventMessage.Header.Value.ENCRYPTION_Y.equals(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getEncryptionYn());
	}
	
	private String obtainDecryptedData (IntegrationPlatformEventHistory integrationPlatformEventHistory) {
		if (isEncypted(integrationPlatformEventHistory)) {
			return new String(new AriaCipher (encryptionKey).decrypt(integrationPlatformEventHistory.getReceivedBodyData().getBytes()));
		} else {
			return integrationPlatformEventHistory.getReceivedBodyData();
		}
	}
}
