package kr.re.keti.sc.interworking.integrationplatform.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventBase;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformEventHistory;
import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule;

@Repository
public interface IntegrationPlatformEventMapper {
	List<MappingRule> selectMappingRule (IntegrationPlatformEventHistory integrationPlatformEventHistory);
	
	void insertIntegrationPlatformEventBase (IntegrationPlatformEventBase integrationPlatformEventBase);
	void updateIntegrationPlatformEventBase (IntegrationPlatformEventBase integrationPlatformEventBase);
	IntegrationPlatformEventBase selectIntegrationPlatformEventBase (IntegrationPlatformEventHistory integrationPlatformEventHistory);
	
	void insertIntegrationPlatformEventHistory (IntegrationPlatformEventHistory integrationPlatformEventHistory);
	void updateIntegrationPlatformEventHistory (IntegrationPlatformEventHistory integrationPlatformEventHistory);
	List<IntegrationPlatformEventHistory> selectNotProcessedIntegrationPlatformEventHistory();
	List<IntegrationPlatformEventHistory> selectAlreadyProcessedIntegrationPlatformEventHistory (IntegrationPlatformEventHistory integrationPlatformEventHistory);
}