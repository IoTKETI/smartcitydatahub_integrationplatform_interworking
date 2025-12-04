package kr.re.keti.sc.interworking.integrationplatform.service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import kr.re.keti.sc.interworking.integrationplatform.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IntegrationPlatformAuthenticationService {
	
	@Value("${datahub-to-integrationPlatform.apiKey}")
	private String apiKey;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${integrationPlatform.authenticationUri}")
	private String authenticationUri;
	
	public String authenticate() {
		return onApplicationEvent(new AuthenticationRequestEvent());
	}

	@EventListener
	public String onApplicationEvent(AuthenticationRequestEvent event) {
		//1. Initialize Session Key 
		CommonVariables.setSessionKey(null);
		
		//2. Compose Authentication Message
		IntegrationPlatformAuthenticationMessage integrationPlatformAuthentication = IntegrationPlatformAuthenticationMessage.obtainDefaultIntegrationPlatformAuthentication();
		IntegrationPlatformAuthenticationMessage.AuthenticationBody authenticationBody = new IntegrationPlatformAuthenticationMessage.AuthenticationBody();
		authenticationBody.setApiKey(apiKey);
		integrationPlatformAuthentication.setBody(authenticationBody);
		log.info(String.format("Authentication Message that will be sent: %s",  integrationPlatformAuthentication.toString()));

		//3. Sending HTTP Authentication Message
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(IntegrationPlatformAuthenticationMessage.HttpHeader.Name.X_SCSNS_SVCTY, integrationPlatformAuthentication.getServiceType());
		headers.set(IntegrationPlatformAuthenticationMessage.HttpHeader.Name.X_SCSNS_SENDCD, integrationPlatformAuthentication.getSendCode());
		headers.set(IntegrationPlatformAuthenticationMessage.HttpHeader.Name.X_SCSNS_RCVCD, integrationPlatformAuthentication.getReceiveCode());
		headers.set(IntegrationPlatformAuthenticationMessage.HttpHeader.Name.X_SCSNS_SYSCD, integrationPlatformAuthentication.getSystemCode());
		headers.set(IntegrationPlatformAuthenticationMessage.HttpHeader.Name.X_SCSNS_ENCAT, integrationPlatformAuthentication.getEncryptionYn());
		headers.set(IntegrationPlatformAuthenticationMessage.HttpHeader.Name.X_SCSNS_SECRET, "");
		headers.set(IntegrationPlatformAuthenticationMessage.HttpHeader.Name.X_SCSNS_TYPE, "sync");

		HttpEntity<IntegrationPlatformAuthenticationMessage> requestEntity = new HttpEntity<>(integrationPlatformAuthentication, headers);

		ResponseEntity<IntegrationPlatformResponse> responseEntity = restTemplate.postForEntity(authenticationUri, requestEntity, IntegrationPlatformResponse.class);
		if (responseEntity != null && responseEntity.getBody() != null && IntegrationPlatformResponse.SUCCESS_RESULT.equals(responseEntity.getBody().getResult())) {// Success Case
			log.info(String.format("Authentication success message received. Received response: %s", responseEntity));
			CommonVariables.setSessionKey(responseEntity.getBody().getMessage());
			return responseEntity.getBody().getMessage();
		} else {
			log.error(String.format("Authentication failure message received. Received response: %s", responseEntity));
			return null;
		}
	}

}