package kr.re.keti.sc.interworking.integrationplatform.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.re.keti.sc.interworking.common.HttpConstants;
import kr.re.keti.sc.interworking.common.exception.BadRequestException;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformAbstractMessage.ApplicationSequence;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformAbstractMessage.TransactionSystemHistoryElement;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformAbstractMessage;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformConstants;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventDTO;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventHistory;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformResponse;
import kr.re.keti.sc.interworking.integrationplatform.model.ProcessStatus;
import kr.re.keti.sc.interworking.integrationplatform.service.IntegrationPlatformEventService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RestController
public class IntegrationPlatformEventController {
	
	@Autowired
	private IntegrationPlatformEventService integrationPlatformEventService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@RequestMapping(value = "/integrationPlatformEvent", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
	@ResponseBody
	private IntegrationPlatformResponse receiveEvent (
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.AUTHORIZATION, required = false) String authorizationHeaderValue,
			IntegrationPlatformEventDTO integrationPlatformEventDTO)
			throws Exception {
		log.info(String.format("Event Received: %s", integrationPlatformEventDTO));
		IntegrationPlatformEventMessage integrationPlatformEvent = convertDTOtoVO (integrationPlatformEventDTO);
		
		IntegrationPlatformEventHistory integrationPlatformEventProcessed = new IntegrationPlatformEventHistory ();
		integrationPlatformEventProcessed.setProcessStatus(ProcessStatus.NOT_PROCESSED);
		integrationPlatformEventProcessed.setIntegrationPlatformEventMessage(integrationPlatformEvent);
		integrationPlatformEventProcessed.setReceivedBodyData(integrationPlatformEventDTO.getBody());
		integrationPlatformEventService.createIntegrationPlatformEventHistory(integrationPlatformEventProcessed);
		
		return IntegrationPlatformResponse.obtainSuccessResponse();
	}
	
	
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.SERVICE_TYPE) 
	protected String serviceType;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.HEADER_COUNT) 
	protected String headerCount;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.DATA_COUNT) 
	protected String dataCount;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.APPLICATION_SEQUENCE) 
	protected ApplicationSequence applicationSequence;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.COMMUNICATION_METHOD) 
	protected String communicationMethod;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.SEND_CODE) 
	protected String sendCode;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.RECEIVE_CODE) 
	protected String receiveCode;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.ENCRYPTION_YN) 
	protected String encryptionYn;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.TRANSACTION_TYPE) 
	protected String transactionType;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.RRKEY) 
	protected String rrKey;
	@JsonFormat(pattern = IntegrationPlatformConstants.CONTENT_DATE_FORMAT, timezone = IntegrationPlatformConstants.CONTENT_DATE_TIMEZONE)
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.REQUEST_DATE) 
	protected Date requestDate;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.BODY_LENGTH) 
	protected String bodyLength;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.SYSTEM_CODE) 
	protected String systemCode;
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.TRANSACTION_SYSTEM_HISTORY) 
	protected List<TransactionSystemHistoryElement> transactionSystemHistory;

	
	
	@RequestMapping(value = "/integrationPlatformEvent", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	private IntegrationPlatformResponse receiveEventJson (
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT, required = false) String accept,
			@RequestHeader(value = HttpConstants.HeaderFieldName.ACCEPT_CHARSET, required = false) String acceptCharset,
			@RequestHeader(value = HttpConstants.HeaderFieldName.SCSNS_SVCTY, required = false) String serviceType,
			@RequestHeader(value = HttpConstants.HeaderFieldName.SCSNS_SENDCD, required = false) String sendCode,
			@RequestHeader(value = HttpConstants.HeaderFieldName.SCSNS_RCVCD, required = false) String receiveCode,
			@RequestHeader(value = HttpConstants.HeaderFieldName.SCSNS_SYSCD, required = false) String systemCode,
			@RequestHeader(value = HttpConstants.HeaderFieldName.SCSNS_ENCAT, required = false) String encryptionYn,
			@RequestHeader(value = HttpConstants.HeaderFieldName.SCSNS_SECRET, required = false) String authorizationHeaderValue,
			@RequestHeader(value = HttpConstants.HeaderFieldName.SCSNS_TYPE, required = false) String messageType,
			@RequestBody IntegrationPlatformEventMessage integrationPlatformEvent)
			throws Exception {
		log.info(String.format("Event Received: %s", integrationPlatformEvent));
		
		IntegrationPlatformEventHistory integrationPlatformEventProcessed = new IntegrationPlatformEventHistory ();
		integrationPlatformEventProcessed.setProcessStatus(ProcessStatus.NOT_PROCESSED);
		integrationPlatformEventProcessed.setIntegrationPlatformEventMessage(integrationPlatformEvent);
		integrationPlatformEventProcessed.setReceivedBodyData(integrationPlatformEvent.getBody().toString());//
		integrationPlatformEventService.createIntegrationPlatformEventHistory(integrationPlatformEventProcessed);
		
		return IntegrationPlatformResponse.obtainSuccessResponse();
	}
	
	private IntegrationPlatformEventMessage convertDTOtoVO(IntegrationPlatformEventDTO integrationPlatformEventDTO) throws Exception {

		//1. Configure attribute of primitive type
		IntegrationPlatformEventMessage integrationPlatformEvent = IntegrationPlatformEventMessage.builder()
			.serviceType(integrationPlatformEventDTO.getSvcTy())
			.headerCount(integrationPlatformEventDTO.getHdCnt())
			.dataCount(integrationPlatformEventDTO.getDtCnt())
			.communicationMethod(integrationPlatformEventDTO.getCmnMtd())
			.sendCode(integrationPlatformEventDTO.getSendCd())
			.receiveCode(integrationPlatformEventDTO.getRcvCd())
			.encryptionYn(integrationPlatformEventDTO.getEncAt())
			.transactionType(integrationPlatformEventDTO.getTranTy())
			.rrKey(integrationPlatformEventDTO.getRrKey())
			.bodyLength(integrationPlatformEventDTO.getBodyLen())
			.systemCode(integrationPlatformEventDTO.getSysCd())
			.build();
		
		//2. Configure attribute of complex type
		if(integrationPlatformEventDTO.getApSeq() != null) {
			try {
				integrationPlatformEvent.setApplicationSequence(objectMapper.readValue(integrationPlatformEventDTO.getApSeq(), ApplicationSequence.class));
			} catch (Exception e) {
				log.error("ApplicationSequence of IntegrationPlatformEvent parsing error.", e);
				throw new BadRequestException ("'apSeq' header parsing error");
			}
		}
		
		if (integrationPlatformEventDTO.getReqDate() != null) {
			if (integrationPlatformEventDTO.getReqDate().length() != IntegrationPlatformConstants.CONTENT_DATE_FORMAT.length()) {
				log.error("RequestDate format error.");
				throw new BadRequestException ("'reqDate' header format error.");
			}
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(IntegrationPlatformConstants.CONTENT_DATE_FORMAT);
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone(IntegrationPlatformConstants.CONTENT_DATE_TIMEZONE));
			try {
				Date requestDate = simpleDateFormat.parse(integrationPlatformEventDTO.getReqDate());
				integrationPlatformEvent.setRequestDate(requestDate);
			} catch (ParseException e) {
				log.error("RequestDate of IntegrationPlatformEvent parsing error.", e);
				throw new BadRequestException ("'reqDate' header parsing error.");
			}
		}
				
		if(integrationPlatformEventDTO.getTranSysHis() != null) {
			try {
				integrationPlatformEvent.setTransactionSystemHistory(objectMapper.readValue(integrationPlatformEventDTO.getTranSysHis(), new TypeReference<List<TransactionSystemHistoryElement>>(){}));
			} catch (Exception e) {
				log.error ("TransactionSystemHistory of IntegrationPlatformEvent parsing error.", e);
				throw new BadRequestException ("'tranSysHis' header parsing error.");
			}
			
		}
		
		return integrationPlatformEvent;
	}
}
