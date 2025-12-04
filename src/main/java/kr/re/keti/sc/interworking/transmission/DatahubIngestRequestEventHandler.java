package kr.re.keti.sc.interworking.transmission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.re.keti.sc.interworking.datahub.model.AttributeDataType;
import kr.re.keti.sc.interworking.datahub.model.DatahubConstants;
import kr.re.keti.sc.interworking.datahub.model.DateProperty;
import kr.re.keti.sc.interworking.datahub.model.GeoProperty;
import kr.re.keti.sc.interworking.datahub.model.IngestInterfacePayload;
import kr.re.keti.sc.interworking.datahub.model.Property;
import kr.re.keti.sc.interworking.datahub.model.Response;
import kr.re.keti.sc.interworking.integrationplatform.model.DatahubIngestRequestEvent;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventBase;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventHistory;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventMessage.EventBody.OccurrenceEventItem;
import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule;
import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule.Attribute;
import kr.re.keti.sc.interworking.integrationplatform.model.ProcessStatus;
import kr.re.keti.sc.interworking.integrationplatform.service.IntegrationPlatformEventService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DatahubIngestRequestEventHandler {
	
	@Autowired
	IntegrationPlatformEventService integrationPlatformEventService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${datahub.ingestInterfaceUri}")
	private String ingestInterfaceUri;
	
	@Autowired
	private ObjectMapper objectMapper;

	@EventListener
    public void onMyEvent(DatahubIngestRequestEvent event) {
		try {
			ingestFullUpsertRequest(event.getIntegrationPlatformEventHistory(), event.getMappingRule());
		} catch (Exception e) {
			log.error(String.format("Exception occurs while sending event history sequence(#%s) data to datahub.", event.getIntegrationPlatformEventHistory().getSequence()), e);
			event.getIntegrationPlatformEventHistory().setProcessStatus(ProcessStatus.DATAHUB_INTERWORKING_ERROR);
			integrationPlatformEventService.updateIntegrationPlatformEventHistory(event.getIntegrationPlatformEventHistory());
			return;
		}
    }
	
	private static IngestInterfacePayload obtainIngestInterfacePayload (IntegrationPlatformEventHistory integrationPlatformEventHistory, MappingRule mappingRule) {
		IngestInterfacePayload ingestInterfacePayload = new IngestInterfacePayload();
		ingestInterfacePayload.setDatasetId(mappingRule.getDatahubDatasetId());
		ingestInterfacePayload.setEntities(new ArrayList<Map<String, Object>> ());
		
		Map<String, Object> entity = new HashMap<String, Object> ();
		entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.ID, obtainEntityId(integrationPlatformEventHistory, mappingRule));
		entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.TYPE, mappingRule.getDatahubEntityType());
		
		//TODO: db에서 값 가지고 오기
		entity.put(DatahubConstants.DatahubAttributeName.CONTEXT, Arrays.asList("http://uri.citydatahub.kr/ngsi-ld/v1/integrationPlatformEvent.jsonld", "http://uri.citydatahub.kr/ngsi-ld/v1/ngsi-ld-core-context-v1.3.jsonld"));
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getEventId() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.EVENT_ID, new Property(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getEventId()));
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getEventName() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.EVENT_NAME, new Property(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getEventName()));
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getEventGrade() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.EVENT_GRADE, new Property(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getEventGrade()));
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceNumber() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_NUMBER, new Property(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceNumber()));
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceStatus() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_STATUS, 
					Property.builder()
					.value(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceStatus())
					.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
					.build());
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceLocation() != null &&
			integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceLocation().getCoordinateX() != null &&
			integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceLocation().getCoordinateY() != null) {
			
			GeoProperty geoProperty = new GeoProperty();
			Double locationX = Double.parseDouble(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceLocation().getCoordinateX());
			Double locationY = Double.parseDouble(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceLocation().getCoordinateY());
			
			List<Double> location = new ArrayList <Double>();
			location.add(locationX);
			location.add(locationY);
			
			geoProperty.setValue(new GeoProperty.GeoPropertyValue(location));
			
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime() != null) {
				geoProperty.setObservedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime());
			}
			
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_LOCATION, geoProperty);
		}
		
		if(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrencePlace() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_PLACE, 
					Property.builder()
					.value(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrencePlace())
					.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
					.build());
		}
		
		if(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventContent() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_CONTENT, 
					Property.builder()
					.value(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventContent())
					.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
					.build());
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventTime() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_TIME, 
					DateProperty.builder()
					.value(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventTime())
					.build());
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatus() != null) {
			Map<String, Object> occurrenceEventStatusContent = new HashMap<String, Object> ();
			
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatus().getOccurrenceEventStatusCode() != null) {
				occurrenceEventStatusContent.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_STATUS_CODE, integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatus().getOccurrenceEventStatusCode());
			}
			
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatus().getOccurrenceEventStatusDetail() != null) {
				occurrenceEventStatusContent.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_STATUS_DETAIL, integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatus().getOccurrenceEventStatusDetail());
			}
			
			if (occurrenceEventStatusContent.size() > 0) {
				entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_STATUS, 
						Property.builder()
						.value(occurrenceEventStatusContent)
						.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
						.build());
			}
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusUser() != null) {
			Map<String, Object> occurrenceEventStatusUser = new HashMap<String, Object> ();
			
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusUser().getOccurrenceEventUserId() != null) {
				occurrenceEventStatusUser.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_STATUS_USER_ID, integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusUser().getOccurrenceEventUserId());
			}
			
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusUser().getOccurrenceEventUserName() != null) {
				occurrenceEventStatusUser.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_STATUS_USER_NAME, integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusUser().getOccurrenceEventUserName());
			}
			
			if (occurrenceEventStatusUser.size() > 0) {
				entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_STATUS_USER, 
						Property.builder()
						.value(occurrenceEventStatusUser)
						.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
						.build());
			}
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_STATUS_TIME, 
					DateProperty.builder()
					.value(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
					.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
					.build());
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventEndTime() != null) {
			entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_END_TIME, 
					Property.builder()
					.value(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventEndTime())
					.build());
		}
		
		if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventDetail() != null) {
			Map<String, Object> occurrenceEventDetailContent = new HashMap<String, Object> ();
			
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventDetail().getOccurrenceEventDetailCode() != null) {
				occurrenceEventDetailContent.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_DETAIL_CODE, integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventDetail().getOccurrenceEventDetailCode());
			}
			
			if (integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventDetail().getOccurrenceEventDetailCodeDetail() != null) {
				occurrenceEventDetailContent.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_DETAIL_CODE_DETAIL, integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventDetail().getOccurrenceEventDetailCodeDetail());
			}
			
			if (occurrenceEventDetailContent.size() > 0) {
				entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_DETAIL, 
						Property.builder()
						.value(occurrenceEventDetailContent)
						.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
						.build());
			}
		}
		
		if(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventItemList() != null && integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventItemList().size() > 0) {
			
			Map<String, Object> occurrenceEventItemContent = new HashMap<String, Object> ();
			
			for(OccurrenceEventItem elem : integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventItemList()){
				Attribute attribute = obtainMappingRuleAttribute (mappingRule, elem.getKey());
				
				if (attribute == null) {
					log.warn (String.format("Not Matched Attribute. Attribute Name: %s", elem.getKey()));
					continue;
				}
				
				Object value = null;
				
				if (!attribute.getDatahubAttributeDataType().equals(attribute.getIntegrationPlatformAttributeDataType())) {
					if (AttributeDataType.STRING.equals(attribute.getIntegrationPlatformAttributeDataType())) {
						if (AttributeDataType.DOUBLE.equals(attribute.getDatahubAttributeDataType())) {
							value = Double.parseDouble(elem.getValue());
						} else if (AttributeDataType.INTEGER.equals(attribute.getDatahubAttributeDataType())) {
							value = Integer.parseInt(elem.getValue());
						} else if (AttributeDataType.BOOLEAN.equals(attribute.getDatahubAttributeDataType())) {
							value = Boolean.parseBoolean(elem.getValue());
						} else {
							log.error(String.format("Unhandled Data Type of Attribute: %s. Integration Platform Data Type: %s, Datahub Data Type: %s", attribute, attribute.getIntegrationPlatformAttributeDataType(), attribute.getDatahubAttributeDataType()));
						}
					} else {
						log.error(String.format("Unhandled Data Type of Attribute: %s. Integration Platform Data Type: %s, Datahub Data Type: %s", attribute, attribute.getIntegrationPlatformAttributeDataType(), attribute.getDatahubAttributeDataType()));
					}
				} else {
					value = elem.getValue();
				}
				
				if (value != null) {
					occurrenceEventItemContent.put(attribute.getDatahubAttributeName(), value);
				}
				
			}
			
			if (occurrenceEventItemContent.size() > 0) {
				entity.put(DatahubConstants.IntegrityPlatformEventAttributeName.OCCURRENCE_EVENT_ITEM, 
						Property.builder()
						.value(occurrenceEventItemContent)
						.observedAt(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceEventStatusTime())
						.build());
			}
		}
		
		ingestInterfacePayload.getEntities().add(entity);
		
		return ingestInterfacePayload;
	}
	
	private static String obtainEntityId (IntegrationPlatformEventHistory integrationPlatformEventHistory, MappingRule mappingRule) {
		return new StringBuilder()
				.append(mappingRule.getDatahubEntityType())
				.append(":")
				.append(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getSendCode())//TODO: 확인 필요
				.append(":")
				.append(integrationPlatformEventHistory.getIntegrationPlatformEventMessage().getBody().getOccurrenceNumber())
				.toString();
	}
	
	private static String dateToString (Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DatahubConstants.CONTENT_DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone(DatahubConstants.CONTENT_DATE_TIMEZONE));
		return sdf.format(date);
	}
	
	private static Attribute obtainMappingRuleAttribute (MappingRule mappingRule, String integrationPlatformAttributeName) {
		for (Attribute attribute : mappingRule.getAttributes()) {
			if (attribute.getIntegrationPlatformAttributeName().contentEquals(integrationPlatformAttributeName)) {
				return attribute;
			}
		}
		
		return null;
	}
	
	private IntegrationPlatformEventBase makeIntegrationPlatformEvent(IntegrationPlatformEventHistory integrationPlatformEventHistory) {
		IntegrationPlatformEventBase integrationPlatformEventBase = new IntegrationPlatformEventBase();
		integrationPlatformEventBase.setIntegrationPlatformEventMessage(integrationPlatformEventHistory.getIntegrationPlatformEventMessage());
		return integrationPlatformEventBase;
	}
	
	public void ingestFullUpsertRequest (IntegrationPlatformEventHistory integrationPlatformEventHistory, MappingRule mappingRule) throws Exception {
		
		IngestInterfacePayload interfacePayload = obtainIngestInterfacePayload(integrationPlatformEventHistory, mappingRule);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/ld+json");
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		try {
			log.info(String.format("Sending message to datahub: %s", interfacePayload));
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(ingestInterfaceUri, interfacePayload, String.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				Response response = objectMapper.readValue(responseEntity.getBody(), Response.class);
				if ((response.getErrors() == null || response.getErrors().size() == 0) && response.getSuccess().size() > 0) {
					log.info(String.format("Event history sequence(#%s) successfully interworked. response payload: %s", integrationPlatformEventHistory.getSequence(), responseEntity.getBody()));
					integrationPlatformEventHistory.setProcessStatus(ProcessStatus.PROCESSED);
					integrationPlatformEventService.createIntegrationPlatformEventBase(makeIntegrationPlatformEvent(integrationPlatformEventHistory));
					integrationPlatformEventService.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
					
					return;
				}
			} 
			log.error(String.format("Event history sequence(#%s) interworking failed. response payload: %s", integrationPlatformEventHistory.getSequence(), responseEntity.getBody()));
			integrationPlatformEventHistory.setProcessStatus(ProcessStatus.DATAHUB_INTERWORKING_ERROR);
			integrationPlatformEventService.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
		} catch (RestClientException e) {
			log.error("Interworking failed. Exception:", e);
			throw e;			
		}
	}

}
