package kr.re.keti.sc.interworking.datahub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import kr.re.keti.sc.interworking.datahub.mapper.DatahubMapper;
import kr.re.keti.sc.interworking.datahub.model.NotificationHistory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DatahubService {
	@Autowired
	private DatahubMapper datahubMapper;
	
	@Autowired
    ApplicationEventPublisher eventPublisher;
	
	public void createNotificationHistory(NotificationHistory NotificationHistory) {
//		datahubMapper.insertNotificationHistory(NotificationHistory);
	}
//	
//	public void updateIntegrationPlatformEventHistory(IntegrationPlatformEventHistory integrationPlatformEventHistory) {
//		integrationPlatformEventMapper.updateIntegrationPlatformEventHistory(integrationPlatformEventHistory);
//	}

}
