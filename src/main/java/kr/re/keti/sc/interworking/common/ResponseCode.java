package kr.re.keti.sc.interworking.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseCode {

	OK ("2000", "OK", HttpStatus.OK),
	CREATED ( "2001", "Created", HttpStatus.CREATED),
	DELETED ("2002", "OK", HttpStatus.OK),
	CHANGE ("2004", "OK", HttpStatus.OK),
	BAD_REQUEST  ( "4000", "Bad Request", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED  ( "4001", "Unauthorized", HttpStatus.UNAUTHORIZED),
	NOT_FOUND  ( "4004", "Not Found", HttpStatus.NOT_FOUND),
	METHOD_NOT_ALLOWED  ( "4005", "Method Not Allowed", HttpStatus.METHOD_NOT_ALLOWED),
	NOT_ACCEPTABLE  ( "4006", "Not Acceptable", HttpStatus.NOT_ACCEPTABLE),
	UNSUPPORTED_MEDIA_TYPE  ( "4015", "Unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
	ALEADY_EXISTS  ( "4100", "Already Exists", HttpStatus.BAD_REQUEST),
	MANDATORY_PARAMETER_MISSING  ( "4101", "Mandatory Parameter Missing", HttpStatus.BAD_REQUEST),
	INVAILD_PARAMETER_TYPE  ("4102", "Invaild Parameter Type", HttpStatus.BAD_REQUEST),
	TOO_MANY_REQUETS  ( "4209", "Too Many Requests", HttpStatus.TOO_MANY_REQUESTS),
	INTERNAL_SERVER_ERROR  ( "5000", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
	SERVICE_UNAVAILABLE  ( "5003", "Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE),
	GATEWAY_TIMEOUT  ( "5004", "GATEWAY TIMEOUT", HttpStatus.GATEWAY_TIMEOUT),
	NOT_REACHABLE  ("5100", "Not Reachable", HttpStatus.NOT_FOUND),
	;

	private final String detailResponseCode;
	private final String detailDescription;
	private final HttpStatus httpStatusCode;
	
	private ResponseCode(String detailResponseCode, String detailDescription,  HttpStatus httpStatusCode ) {
		this.detailResponseCode = detailResponseCode;
		this.detailDescription = detailDescription;
		this.httpStatusCode = httpStatusCode;
	}
	
	private static final Map<String, ResponseCode> valueMap = new HashMap<>(ResponseCode.values().length);
	
	static {
		for (ResponseCode it : values()) {
			valueMap.put(it.getDetailCode(), it);
		}
	}

	@JsonValue
	public String getDetailCode() {
		return this.detailResponseCode;
	}

	public String getDetailDescription() {
		return detailDescription;
	}
	
	public HttpStatus getHttpStatusCode() {
		return httpStatusCode;
	}
	
	@JsonCreator
	public static ResponseCode fromDetailResponseCode(String detailResponseCode) {
		return valueMap.get(detailResponseCode);
	}
}