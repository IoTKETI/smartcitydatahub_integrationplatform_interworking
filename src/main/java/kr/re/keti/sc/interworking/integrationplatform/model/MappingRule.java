package kr.re.keti.sc.interworking.integrationplatform.model;

import java.util.List;

import kr.re.keti.sc.interworking.datahub.model.AttributeDataType;
import kr.re.keti.sc.interworking.datahub.model.AttributeType;
import lombok.Data;

@Data public class MappingRule {
	public static final String INTEGRATION_PLATFORM_EVENT_ID_DETAIL_WILDCARD = "*" ;
	
	private String integrationPlatformEventId;
	private String integrationPlatformEventIdDetail;
	private String integrationPlatformEventName;
	private String integrationPlatformEventGrade;
	private String integrationPlatformEventDetailCode;
	private String integrationPlatformEventDetailCodeDetail;
	private String integrationPlatformOccurenceEventContent;
	private String datahubEntityType;
	private String datahubDatasetId;
	private List <Attribute> attributes;
	
	@Data public static class Attribute {
		private String integrationPlatformAttributeName;
		private AttributeDataType integrationPlatformAttributeDataType;
		private String datahubAttributeName;
		private AttributeType datahubAttributeType;
		private AttributeDataType datahubAttributeDataType;
		private Boolean datahubAttributeHasObservedAt;
	}
}
