package kr.re.keti.sc.interworking.datahub.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.re.keti.sc.interworking.datahub.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.re.keti.sc.interworking.common.HttpConstants;
import kr.re.keti.sc.interworking.datahub.mapper.DatahubMapper;
import kr.re.keti.sc.interworking.datahub.service.DatahubService;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformResponse;
import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule;
import kr.re.keti.sc.interworking.integrationplatform.model.ProcessStatus;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RestController
public class DatahubController {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private DatahubService datahubService;
	
	@Autowired
    ApplicationEventPublisher eventPublisher;
	
	@Autowired
	private DatahubMapper datahubMapper;
	
	@RequestMapping(value = "/datahubNotification", method = RequestMethod.POST)
	@ResponseBody
	private IntegrationPlatformResponse receiveNotification (
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue,
			@RequestBody Notification notification)
			throws Exception {
		log.info(String.format("Datahub Notification Received: %s", notification));

		try {
			String bodyJson = objectMapper.writeValueAsString(notification);
			log.info("### Received notification body: {}", bodyJson);
		} catch (JsonProcessingException e) {
			log.warn("### Failed to convert notification to json", e);
			log.info("### notification: {}", notification); // toString() fallback
		}
		
		NotificationHistory notificationHistory = NotificationHistory.builder()
			.notification(notification)
			.processStatus(ProcessStatus.NOT_PROCESSED)
			.build();
		
		//TODO: insert notification history (향후 비동기 처리)
		//datahubService.createNotificationHistory(notificationHistory);

		for (CommonEntityVO commonEntityVO : notification.getData()) {//Notification에 여러 Entities가 들어올 수 있음
			String type = (String) commonEntityVO.get(DatahubConstants.DatahubAttributeName.TYPE);

			if (type == null) {
				log.error("Message Error Found. Message = {}", commonEntityVO);
				continue;
			}

			List<MappingRule> mappingRuleList = datahubMapper.selectMappingRule(QueryValue.builder().datahubEntityType(type).build());
			
			if (mappingRuleList == null || mappingRuleList.size() == 0 ) {
				log.error("No mapping rule found for type = {}", type);
				continue;
			}
			
			IntegrationPlatformRequestEvent integrationPlatformRequestEvent = new IntegrationPlatformRequestEvent();
			integrationPlatformRequestEvent.setMappingRule(mappingRuleList.get(0));
			integrationPlatformRequestEvent.setNotificationProcessingData(commonEntityVO);
			integrationPlatformRequestEvent.setNotification(notification);

			eventPublisher.publishEvent(integrationPlatformRequestEvent);
		}
	
		return IntegrationPlatformResponse.obtainSuccessResponse();
	}
}
