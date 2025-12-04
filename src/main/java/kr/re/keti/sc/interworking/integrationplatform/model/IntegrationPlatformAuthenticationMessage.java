package kr.re.keti.sc.interworking.integrationplatform.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Data public class IntegrationPlatformAuthenticationMessage extends IntegrationPlatformAbstractMessage {
	
	@Builder
	public IntegrationPlatformAuthenticationMessage(String serviceType, String headerCount, String dataCount,
			ApplicationSequence applicationSequence, String communicationMethod, String sendCode, String receiveCode,
			String encryptionYn, String transactionType, String rrKey, Date requestDate, String bodyLength,
			String systemCode, List<TransactionSystemHistoryElement> transactionSystemHistory, CallBack callback, AuthenticationBody body) {
		super(serviceType, headerCount, dataCount, applicationSequence, communicationMethod, sendCode, receiveCode, encryptionYn, transactionType, rrKey, requestDate, bodyLength, systemCode, transactionSystemHistory, callback);
		this.body = body;
	}
	
	@JsonProperty(IntegrationPlatformAbstractMessage.Header.Name.BODY)
	private AuthenticationBody body;
	
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data public static class AuthenticationBody {
		private String startTag = "$DA";
		private String endTag = "$DB";
		private String finishTag = "$DF";
		@JsonProperty(IntegrationPlatformAuthenticationMessage.Body.Name.APIKEY) 
		private String apiKey;
	}
	
	public static IntegrationPlatformAuthenticationMessage obtainDefaultIntegrationPlatformAuthentication() {
		return IntegrationPlatformAuthenticationMessage.builder()
			.serviceType(Header.Value.ServiceType.AUTH)
			.headerCount(Header.Value.HEADER_COUNT)
			.dataCount(Header.Value.DATA_COUNT)
			.applicationSequence(IntegrationPlatformAuthenticationMessage.obtainApplicationSequence())
			.communicationMethod(Header.Value.COMMUNICATION_METHOD)
			.sendCode(CommonVariables.getSendingCityCode())
			.receiveCode(CommonVariables.getReceivingCityCode())
			.encryptionYn(Header.Value.ENCRYPTION_N)
			.transactionType(Header.Value.TRANSACTION_TYPE)
			.requestDate(new Date())
			.systemCode(CommonVariables.getSystemCode())
			.transactionSystemHistory(IntegrationPlatformAuthenticationMessage.obtainTransactionSystemHistory())
				.callback(IntegrationPlatformAuthenticationMessage.obtainDefaultCallback())
			.build();
	}
	

	public static final class Body {
		public static final class Name {
			public static final String APIKEY = "apiKey";
		}
	}
}
