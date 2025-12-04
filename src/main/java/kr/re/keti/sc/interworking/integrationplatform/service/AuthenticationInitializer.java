package kr.re.keti.sc.interworking.integrationplatform.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import kr.re.keti.sc.interworking.integrationplatform.model.AuthenticationRequestEvent;

@Component
@DependsOn(value = {"integrationPlatformAuthenticationService"})
public class AuthenticationInitializer {
	
	@Autowired
    ApplicationEventPublisher eventPublisher;
	
	@PostConstruct
	public void init(){
		eventPublisher.publishEvent(new AuthenticationRequestEvent());
	}	
}
