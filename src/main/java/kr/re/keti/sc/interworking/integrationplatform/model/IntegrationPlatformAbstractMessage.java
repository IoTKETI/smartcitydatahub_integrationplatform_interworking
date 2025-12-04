package kr.re.keti.sc.interworking.integrationplatform.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data public abstract class IntegrationPlatformAbstractMessage {
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
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.CALL_BACK)
	protected CallBack callback;

	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Data public static class CallBack {
		@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.USE_YN)
		private String useYn;
		@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.TARGET_URL)
		private String targetUrl;
	}

	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Data public static class TransactionSystemHistoryElement {
		@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.TRANSACTION_SYSTEM_HISTORY_ELEMENT)
		String transactionSystemHistoryElement;
	}
	
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Data public static class ApplicationSequence {
		@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.APPLICATION_SEQUENCE_APPLICATION_CURRENT) 
		private String applicationCurrent;
		@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.APPLICATION_SEQUENCE_APPLICATION_TOTAL) 
		private String applicationTotal;
	}
	
	protected static List<TransactionSystemHistoryElement> obtainTransactionSystemHistory() {
		List<TransactionSystemHistoryElement> transactionSystemHistoryElementList = new ArrayList<TransactionSystemHistoryElement> ();
		transactionSystemHistoryElementList.add(new TransactionSystemHistoryElement (new StringBuilder().append(CommonVariables.getSendingCityCode()).append("_").append(CommonVariables.getSystemCode()).toString()));
		transactionSystemHistoryElementList.add(new TransactionSystemHistoryElement (new StringBuilder().append(CommonVariables.getReceivingCityCode()).append("_").append("SCSNS").toString()));
		
		return transactionSystemHistoryElementList; 
	}

	protected static CallBack obtainDefaultCallback() {
		CallBack callBack = new CallBack();
		callBack.setTargetUrl("http://127.0.0.1:8080/callBack.do");
		callBack.setUseYn("N");
		return callBack;
	}
	
	protected static ApplicationSequence obtainApplicationSequence() {
		return new IntegrationPlatformAuthenticationMessage.ApplicationSequence(IntegrationPlatformAbstractMessage.Header.Value.ApplicationSequence.AP_CURRENT, IntegrationPlatformAbstractMessage.Header.Value.ApplicationSequence.AP_TOTAL);
	}

	public static final class HttpHeader {
		public static final class Name {
			public static final String X_SCSNS_SVCTY = "X-Scsns-svcTy";
			public static final String X_SCSNS_SENDCD = "X-Scsns-sendCd";
			public static final String X_SCSNS_RCVCD = "X-Scsns-rcvCd";
			public static final String X_SCSNS_SYSCD = "X-Scsns-sysCd";
			public static final String X_SCSNS_ENCAT = "X-Scsns-encAt";
			public static final String X_SCSNS_SECRET = "X-Scsns-Secret";
			public static final String X_SCSNS_TYPE = "X-Scsns-type";
		}
	}

	
	public static final class Header {
		public static final class Name {
			public static final String SERVICE_TYPE = "svcTy";
			public static final String HEADER_COUNT = "hdCnt";
			public static final String DATA_COUNT = "dtCnt";
			public static final String APPLICATION_SEQUENCE = "apSeq";
			public static final String APPLICATION_SEQUENCE_APPLICATION_CURRENT = "apCurrent";
			public static final String APPLICATION_SEQUENCE_APPLICATION_TOTAL = "apTotal";
			public static final String COMMUNICATION_METHOD = "cmnMtd";
			public static final String SEND_CODE = "sendCd";
			public static final String RECEIVE_CODE = "rcvCd";
			public static final String ENCRYPTION_YN = "encAt";
			public static final String TRANSACTION_TYPE = "tranTy";
			public static final String RRKEY = "rrKey";
			public static final String REQUEST_DATE = "reqDate";
			public static final String BODY_LENGTH = "bodyLen";
			public static final String SYSTEM_CODE = "sysCd";
			public static final String TRANSACTION_SYSTEM_HISTORY = "tranSysHis";
			public static final String TRANSACTION_SYSTEM_HISTORY_ELEMENT = "tranSysHis";
			public static final String CALL_BACK = "callBack";
			public static final String USE_YN = "useYn";
			public static final String TARGET_URL = "targetUrl";
			public static final String BODY = "body";
		}
		public static final class Value {
			public static final class ServiceType {
				public static final String AUTH = "AUTH";
				public static final String EVENT = "EVENT";
				public static final String PREEVENT = "PREEVT";
			}
			public static final String HEADER_COUNT = "14";
			public static final String DATA_COUNT = "16";
			public static final class ApplicationSequence {
				public static final String AP_CURRENT = "1";
				public static final String AP_TOTAL = "1";
			}
			public static final String COMMUNICATION_METHOD = "1";
			public static final String ENCRYPTION_N = "N";
			public static final String ENCRYPTION_Y = "Y";
			public static final String TRANSACTION_TYPE = "HTTP";
		}
	}
}
