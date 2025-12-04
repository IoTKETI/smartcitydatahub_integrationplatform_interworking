package kr.re.keti.sc.interworking.integrationplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import kr.re.keti.sc.interworking.common.ResponseCode;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationPlatformResponse {
	public static final String SUCCESS_RESULT = "0";
	public static final String FAILURE_RESULT = "1";
	
	String startTag = "$DA";
	String endTag = "$DB";
	String finishTag = "$DF";
	String result;
	String message;
	@JsonIgnore
	private ResponseCode type;
	
	public static IntegrationPlatformResponse obtainSuccessResponse() {
		IntegrationPlatformResponse response = new IntegrationPlatformResponse();
		response.setResult(SUCCESS_RESULT);
		response.setMessage("");
		response.setType(ResponseCode.OK);
		return response;
	}
	
	public static IntegrationPlatformResponse obtainFailureResponse(ResponseCode type, String message) {
		IntegrationPlatformResponse response = new IntegrationPlatformResponse();
		response.setResult(FAILURE_RESULT);
		response.setType(type);
		response.setMessage(message);
		return response;
	}
}
