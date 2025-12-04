package kr.re.keti.sc.interworking.transmission;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.re.keti.sc.interworking.datahub.model.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.re.keti.sc.interworking.integrationplatform.model.AuthenticationRequestEvent;
import kr.re.keti.sc.interworking.integrationplatform.model.CommonVariables;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformAuthenticationMessage;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformConstants;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage.EventBody;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage.EventBody.OccurrenceEventDetail;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage.EventBody.OccurrenceEventItem;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage.EventBody.OccurrenceEventStatus;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage.EventBody.OccurrenceEventStatusUser;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage.EventBody.OccurrenceLocation;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformResponse;
import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule.Attribute;
import kr.re.keti.sc.interworking.integrationplatform.service.IntegrationPlatformAuthenticationService;
import kr.re.keti.sc.interworking.integrationplatform.utils.AriaCipher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IntegrationPlatformSendingEventHandler {
	@Value("${datahub-to-integrationPlatform.encryptionKey}")
	private String encryptionKey;
	
	@Value("${integrationPlatform.eventUri}")
	private String eventUri;

	@Value("${datahub-to-integrationPlatform.encryptionYn}")
	private String encryptionYn;

	@Autowired
    ApplicationEventPublisher eventPublisher;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	IntegrationPlatformAuthenticationService integrationPlatformAuthenticationService;

	private SimpleDateFormat datahubDateformat = new SimpleDateFormat(DatahubConstants.CONTENT_DATE_FORMAT);
	
	@EventListener
    public synchronized void onMyEvent(IntegrationPlatformRequestEvent event) throws ParseException, JsonProcessingException {

		String sessionKey = integrationPlatformAuthenticationService.authenticate();
		
		IntegrationPlatformEventMessage integrationPlatformEvent = IntegrationPlatformEventMessage.obtainDefaultIntegrationPlatformEvent(encryptionYn);

		
		List<OccurrenceEventItem> occurrenceEventItemList = new ArrayList<>();

		Date currentDate = new Date();

		EventBody eventBody = EventBody.builder()
			.eventId(event.getMappingRule().getIntegrationPlatformEventId())//entity id
			.eventName(event.getMappingRule().getIntegrationPlatformEventName())
			.eventGrade(event.getMappingRule().getIntegrationPlatformEventGrade())//DB
			.occurrenceNumber(currentDate)//TODO: Need to check tracking
			.occurrenceEventStatusTime(currentDate)//Default Setting
			.occurrenceStatus(IntegrationPlatformEventMessage.Body.OccurenceStatusCode.OCCURRED)//데이터허브에서는 이벤트 발생만 수행
			.occurrenceEventStatus(OccurrenceEventStatus.builder().occurrenceEventStatusCode(IntegrationPlatformEventMessage.Body.OccurenceStatusCode.OCCURRED).occurrenceEventStatusDetail(IntegrationPlatformEventMessage.Body.OccurenceStatusName.OCCURRED).build())
			.occurrenceEventStatusUser(OccurrenceEventStatusUser.builder().occurrenceEventUserId(IntegrationPlatformEventMessage.Body.OccurenceStatusUser.ID).occurrenceEventUserName(IntegrationPlatformEventMessage.Body.OccurenceStatusUser.NAME).build())
			.occurrencePlace("")//TODO: Location to Address
			.occurrenceEventContent(event.getMappingRule().getIntegrationPlatformOccurenceEventContent())
			.occurrenceEventDetail(OccurrenceEventDetail.builder().occurrenceEventDetailCode(event.getMappingRule().getIntegrationPlatformEventDetailCode()).occurrenceEventDetailCodeDetail(event.getMappingRule().getIntegrationPlatformEventDetailCodeDetail()).build())
			.build();

		for (Attribute attribute : event.getMappingRule().getAttributes()) {
			if (IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_TIME.equals(attribute.getIntegrationPlatformAttributeName())) {
				Object result = obtainMappingValue(attribute, event.getNotificationProcessingData());
				if (result != null) {
					eventBody.setOccurrenceEventTime((Date) result);
				}
			} else if (IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS_TIME.equals(attribute.getIntegrationPlatformAttributeName())) {
				Object result = obtainMappingValue(attribute, event.getNotificationProcessingData());
				if (result != null) {
					eventBody.setOccurrenceEventStatusTime((Date) result);
				}
			} else if (IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_END_TIME.equals(attribute.getIntegrationPlatformAttributeName())) {
				Object result = obtainMappingValue(attribute, event.getNotificationProcessingData());
				if (result != null) {
					eventBody.setOccurrenceEventEndTime((Date) result);
				}
			} else if (IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_LOCATION.equals(attribute.getIntegrationPlatformAttributeName())) {
				Object result = obtainMappingValue(attribute, event.getNotificationProcessingData());
				if (result != null) {
					eventBody.setOccurrenceLocation((OccurrenceLocation) result);
				}
			} else {
				Object result = obtainMappingValue(attribute, event.getNotificationProcessingData());
				if (result != null) {
					occurrenceEventItemList.add(OccurrenceEventItem.builder().key(attribute.getIntegrationPlatformAttributeName()).value((String) result).build());
				}
			}
		}
		
		eventBody.setOccurrenceEventItemList(occurrenceEventItemList);
		eventBody.setOccurrenceEventItemCount(String.valueOf(occurrenceEventItemList.size()));
		integrationPlatformEvent.setBody(eventBody);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set(IntegrationPlatformEventMessage.HttpHeader.Name.X_SCSNS_SVCTY, integrationPlatformEvent.getServiceType());
		headers.set(IntegrationPlatformEventMessage.HttpHeader.Name.X_SCSNS_SENDCD, integrationPlatformEvent.getSendCode());
		headers.set(IntegrationPlatformEventMessage.HttpHeader.Name.X_SCSNS_RCVCD, integrationPlatformEvent.getReceiveCode());
		headers.set(IntegrationPlatformEventMessage.HttpHeader.Name.X_SCSNS_SYSCD, integrationPlatformEvent.getSystemCode());
		headers.set(IntegrationPlatformEventMessage.HttpHeader.Name.X_SCSNS_ENCAT, integrationPlatformEvent.getEncryptionYn());
		headers.set(IntegrationPlatformEventMessage.HttpHeader.Name.X_SCSNS_SECRET, sessionKey);
		headers.set(IntegrationPlatformEventMessage.HttpHeader.Name.X_SCSNS_TYPE, "sync");

//		String eventBodyStringNotEncypted = objectMapper.writeValueAsString(integrationPlatformEvent.getBody());
//
//		try {
//			if ("Y".equals(integrationPlatformEvent.getEncryptionYn())) {
//				eventBodyString = new String(new AriaCipher (encryptionKey).encrypt(eventBodyStringNotEncypted.getBytes()));
//			} else {
//				eventBodyString = eventBodyStringNotEncypted;
//			}
//
//			integrationPlatformEvent.setBodyLength(String.valueOf(eventBodyString.length()));
//			applicationSequenceString = objectMapper.writeValueAsString(integrationPlatformEvent.getApplicationSequence());
//			transactionSystemHistoryString = objectMapper.writeValueAsString(integrationPlatformEvent.getTransactionSystemHistory());
//		}


		String integrationPlatformEventBodyString = objectMapper.writeValueAsString(integrationPlatformEvent.getBody());

		integrationPlatformEvent.setBodyLength(String.valueOf(integrationPlatformEventBodyString.length()));

		String integrationPlatformEventString = objectMapper.writeValueAsString(integrationPlatformEvent);
		log.info(String.format("Event message sent. request: %s", integrationPlatformEventString));
		HttpEntity<IntegrationPlatformEventMessage> requestEntity = new HttpEntity<>(integrationPlatformEvent, headers);

		//4. Sending HTTP Authentication Message
		ResponseEntity<IntegrationPlatformResponse> responseEntity = restTemplate.postForEntity(eventUri, requestEntity, IntegrationPlatformResponse.class);
		if (responseEntity != null && responseEntity.getBody() != null && IntegrationPlatformResponse.SUCCESS_RESULT.equals(responseEntity.getBody().getResult())) {// Success Case
			log.info(String.format("Event success message received. Received response: %s", responseEntity));
		} else {
			log.error(String.format("Event failure message received. Received response: %s", responseEntity));
		}
	}

	private PropertyInfo obtainPropertyInfo(String datahubAttributeName) {

		List<String> result = new ArrayList<>();
		String propertyMemberName = null;

		Pattern QUOTED_PATTERN = Pattern.compile("[\"“”]([^\"“”]*)[\"“”]");
		Matcher matcher = QUOTED_PATTERN.matcher(datahubAttributeName);

		while (matcher.find()) {
			String part = matcher.group(1);
			if (!part.isEmpty()) {
				result.add(part);
			}
		}

		if(DatahubConstants.DatahubAttributeName.OBSERVED_AT.equals(result.get(result.size() -1))) {
			result.remove(result.get(result.size() -1));
			propertyMemberName = DatahubConstants.DatahubAttributeName.OBSERVED_AT;
		}

		return PropertyInfo.builder().propertyNames(result).propertyMemberName(propertyMemberName).build();
	}

	@SuppressWarnings("unchecked")
	private Object obtainMappingValue (Attribute attribute, CommonEntityVO notificationData) throws ParseException {

		PropertyInfo propertyInfo = obtainPropertyInfo(attribute.getDatahubAttributeName());

		List<String> datahubPropertyNames = propertyInfo.getPropertyNames();
		String datahubPropertyMemberName = propertyInfo.getPropertyMemberName();
		String propertyName = datahubPropertyNames.get(0);//현재 1 level만 고려함 TODO: 2레벨 이상 고려

		//handle reserved attribute
		if(DatahubConstants.DatahubAttributeName.ID.equals(propertyName)) {
			return notificationData.getId();
		} else if (DatahubConstants.DatahubAttributeName.DATASET_ID.equals(propertyName)) {
			return notificationData.getDatasetId();
		}

		//Handle Property/Geoproperty Member
		if(notificationData.get(propertyName) instanceof Map) {
			if (datahubPropertyMemberName != null) {//observedAt 제공
				if (((Map) notificationData.get(propertyName)).get(datahubPropertyMemberName) == null) {
					return null;
				}
				if (AttributeDataType.DATE.equals(attribute.getIntegrationPlatformAttributeDataType())) {
					try {
						return datahubDateformat.parse((String) ((Map) notificationData.get(propertyName)).get(datahubPropertyMemberName));
					} catch (ParseException e) {
						log.error("error.", e);
						throw e;
					}
				} else {
					return ((Map) notificationData.get(propertyName)).get(datahubPropertyMemberName);
				}
			}
		}

		//Handle Property
		if (AttributeType.PROPERTY.getCode().equals(attribute.getDatahubAttributeType().getCode())) {
			if (((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE) == null) {
				return null;
			}

			//현재는 primitive 타입만 지원함. TODO: complex type 지원 검토
			if(AttributeDataType.DATE.equals(attribute.getIntegrationPlatformAttributeDataType())) {
				try {
					return datahubDateformat.parse((String) ((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE));
				} catch (ParseException e) {
					log.error("error.", e);
					throw e;
				}
			}

			if(!attribute.getIntegrationPlatformAttributeDataType().equals(attribute.getDatahubAttributeDataType())) {
				if(AttributeDataType.STRING.equals(attribute.getDatahubAttributeDataType())) {
					if(AttributeDataType.INTEGER.equals(attribute.getIntegrationPlatformAttributeDataType())) {
						return Integer.parseInt((String) ((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE));
					}
					if(AttributeDataType.DOUBLE.equals(attribute.getIntegrationPlatformAttributeDataType())) {
						return Double.parseDouble((String) ((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE));
					}
				}

				if(AttributeDataType.INTEGER.equals(attribute.getDatahubAttributeDataType()) || AttributeDataType.DOUBLE.equals(attribute.getDatahubAttributeDataType())) {
					if(AttributeDataType.STRING.equals(attribute.getIntegrationPlatformAttributeDataType())) {
						return String.valueOf(((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE));
					}
				}
			} else {
				return ((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE);
			}
			log.error("Unhandled exception. Datahub Data Type: {}, Integration Platform Data Type: {}, Value: {}", attribute.getDatahubAttributeDataType(), attribute.getIntegrationPlatformAttributeDataType(), ((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE));
		}

		//Handle GeoProperty
		if (AttributeType.GEOPROPERTY.getCode().equals(attribute.getDatahubAttributeType().getCode())) {
			if (((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE) == null) {
				return null;
			}
			if (((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE) instanceof Map) {
				Map valueMap = (Map) ((Map) notificationData.get(propertyName)).get(DatahubConstants.DatahubAttributeName.VALUE);
				if (DatahubConstants.DatahubAttributeName.POINT.equals(valueMap.get(DatahubConstants.DatahubAttributeName.TYPE))) {//안전망 서비스 Point만 허용
					List<Double> coordinates = (List<Double>) valueMap.get(DatahubConstants.DatahubAttributeName.COORDINATES);
					return EventBody.OccurrenceLocation.builder().coordinateX(coordinates.get(0).toString()).coordinateY(coordinates.get(1).toString()).coordinateZ("0").build();
				}
				return null;
			}
		}

		return null;
	}
}
