package kr.re.keti.sc.interworking.integrationplatform.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data public class IntegrationPlatformEventMessage extends IntegrationPlatformAbstractMessage {
	@JsonProperty("body")
	private EventBody body;
	
	@Builder
	public IntegrationPlatformEventMessage(String serviceType, String headerCount, String dataCount,
			ApplicationSequence applicationSequence, String communicationMethod, String sendCode, String receiveCode,
			String encryptionYn, String transactionType, String rrKey, Date requestDate, String bodyLength,
			String systemCode, List<TransactionSystemHistoryElement> transactionSystemHistory, CallBack callback, EventBody body) {
		super(serviceType, headerCount, dataCount, applicationSequence, communicationMethod, sendCode, receiveCode, encryptionYn, transactionType, rrKey, requestDate, bodyLength, systemCode, transactionSystemHistory, callback);
		this.body = body;
	}
	
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Data public static class EventBody {
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.EVENT_ID)
		private String eventId;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.EVENT_NAME) 
		private String eventName;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.EVENT_GRADE) 
		private String eventGrade;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_NUMBER)
		@JsonFormat(pattern = IntegrationPlatformConstants.CONTENT_DATE_MILLI_FORMAT, timezone = IntegrationPlatformConstants.CONTENT_DATE_TIMEZONE)
		private Date occurrenceNumber;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_STATUS) 
		private String occurrenceStatus;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_LOCATION)
		private OccurrenceLocation occurrenceLocation;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_PLACE) 
		private String occurrencePlace;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_CONTENT) 
		private String occurrenceEventContent;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_TIME) 
		@JsonFormat(pattern = IntegrationPlatformConstants.CONTENT_DATE_FORMAT, timezone = IntegrationPlatformConstants.CONTENT_DATE_TIMEZONE)
		private Date occurrenceEventTime;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS) 
		private OccurrenceEventStatus occurrenceEventStatus; 
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS_USER)
		private OccurrenceEventStatusUser occurrenceEventStatusUser;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS_TIME) 
		@JsonFormat(pattern = IntegrationPlatformConstants.CONTENT_DATE_FORMAT, timezone = IntegrationPlatformConstants.CONTENT_DATE_TIMEZONE) 
		private Date occurrenceEventStatusTime;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_END_TIME) 
		@JsonFormat(pattern = IntegrationPlatformConstants.CONTENT_DATE_FORMAT, timezone = IntegrationPlatformConstants.CONTENT_DATE_TIMEZONE) 
		private Date occurrenceEventEndTime;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_ITEM_COUNT) 
		private String occurrenceEventItemCount;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_ITEM_LIST) 
		private List<OccurrenceEventItem> occurrenceEventItemList;
		@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_DETAIL) 
		private OccurrenceEventDetail occurrenceEventDetail;
		
		@JsonInclude(Include.NON_NULL)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@Data public static class OccurrenceLocation {
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_LOCATION_COORDINATE_X)
			private String coordinateX;
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_LOCATION_COORDINATE_Y)
			private String coordinateY;
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_LOCATION_COORDINATE_Z)
			private String coordinateZ;
		}
		
		@JsonInclude(Include.NON_NULL)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@Data public static class OccurrenceEventStatus {
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS_CODE)
			private String occurrenceEventStatusCode;
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS_DETAIL)
			private String occurrenceEventStatusDetail;
		}
		
		@JsonInclude(Include.NON_NULL)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@Data public static class OccurrenceEventStatusUser {
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS_USER_ID)
			private String occurrenceEventUserId;
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_STATUS_USER_NAME)
			private String occurrenceEventUserName;
		}
		
		@JsonInclude(Include.NON_NULL)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@Data public static class OccurrenceEventDetail {
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_DETAIL_CODE)
			private String occurrenceEventDetailCode;
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_DETAIL_CODE_DETAIL)
			private String occurrenceEventDetailCodeDetail;
		}
		
		@JsonInclude(Include.NON_NULL)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@Data public static class OccurrenceEventItem {
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_ITEM_KEY)
			private String key;
			@JsonProperty(IntegrationPlatformEventMessage.Body.Name.OCCURRENCE_EVENT_ITEM_VALUE)
			private String value;
		}
	}
	
	public static final class Body {
		public static final class Name {
			public static final String EVENT_ID = "evtId";
			public static final String EVENT_NAME = "evtNm";
			public static final String EVENT_GRADE = "evtGrad";
			public static final String OCCURRENCE_NUMBER = "ocrNbr";
			public static final String OCCURRENCE_STATUS = "ocrSts";
			public static final String OCCURRENCE_LOCATION = "ocrLc";
			public static final String OCCURRENCE_PLACE = "ocrPlc";
			public static final String OCCURRENCE_EVENT_CONTENT = "ocrEvtCn";
			public static final String OCCURRENCE_EVENT_TIME = "ocrEvtTime";
			public static final String OCCURRENCE_EVENT_STATUS = "ocrEvtSts";
			public static final String OCCURRENCE_EVENT_STATUS_USER = "ocrEvtStsUsr";
			public static final String OCCURRENCE_EVENT_STATUS_TIME = "ocrEvtStsTime";
			public static final String OCCURRENCE_EVENT_END_TIME = "ocrEvtEndTime";
			public static final String OCCURRENCE_EVENT_ITEM_COUNT = "ocrEvtItemCnt";
			public static final String OCCURRENCE_EVENT_ITEM_LIST = "ocrEvtItem";
			public static final String OCCURRENCE_EVENT_DETAIL = "ocrEvtDtl";
			
			public static final String OCCURRENCE_LOCATION_COORDINATE_X = "crdntX";
			public static final String OCCURRENCE_LOCATION_COORDINATE_Y = "crdntY";
			public static final String OCCURRENCE_LOCATION_COORDINATE_Z = "crdntZ";
			public static final String OCCURRENCE_EVENT_STATUS_CODE = "ocrEvtStsCd";
			public static final String OCCURRENCE_EVENT_STATUS_DETAIL = "ocrEvtStsDtl";
			public static final String OCCURRENCE_EVENT_STATUS_USER_ID = "ocrEvtUsrId";
			public static final String OCCURRENCE_EVENT_STATUS_USER_NAME = "ocrEvtUsrNm";
			public static final String OCCURRENCE_EVENT_DETAIL_CODE = "ocrEvtDtlCd";
			public static final String OCCURRENCE_EVENT_DETAIL_CODE_DETAIL = "ocrEvtDtlCdDtl";
			public static final String OCCURRENCE_EVENT_ITEM_KEY = "key";
			public static final String OCCURRENCE_EVENT_ITEM_VALUE = "val";
		}

		public static final class OccurenceStatusCode {
			public static final String OCCURRED = "10"; //발생
			public static final String INFORMATION_UPDATED = "40"; //정보변경
			public static final String CLEARED = "50";//해제
			public static final String CANCELED = "90";//취소
			public static final String COMPLETED = "91";//완료
			public static final String AUTHMATICALLY_TERMINATED = "91";//자동종료

		}

		public static final class OccurenceStatusName {
			public static final String OCCURRED = "발생"; //발생
			public static final String INFORMATION_UPDATED = "변경"; //정보변경
			public static final String CLEARED = "해제";//해제
			public static final String CANCELED = "취소";//취소
			public static final String COMPLETED = "종료";//완료

		}

		public static final class OccurenceStatusUser {
			public static final String ID = "datahub";
			public static final String NAME = "datahub";

		}
	}
	
	public static IntegrationPlatformEventMessage obtainDefaultIntegrationPlatformEvent(String encryptionYn) {
		return IntegrationPlatformEventMessage.builder()
			.serviceType(Header.Value.ServiceType.EVENT)
			.headerCount(Header.Value.HEADER_COUNT)
			.dataCount(Header.Value.DATA_COUNT)
			.applicationSequence(IntegrationPlatformEventMessage.obtainApplicationSequence())
			.communicationMethod(Header.Value.COMMUNICATION_METHOD)
			.sendCode(CommonVariables.getSendingCityCode())
			.receiveCode(CommonVariables.getReceivingCityCode())
			.encryptionYn(encryptionYn != null ? encryptionYn : Header.Value.ENCRYPTION_N)
			.transactionType(Header.Value.TRANSACTION_TYPE)
			.rrKey(CommonVariables.getSessionKey())
			.requestDate(new Date())
			.systemCode(CommonVariables.getSystemCode())
			.transactionSystemHistory(IntegrationPlatformEventMessage.obtainTransactionSystemHistory())
			.callback(IntegrationPlatformEventMessage.obtainDefaultCallback())
			.build();
	}
	
}