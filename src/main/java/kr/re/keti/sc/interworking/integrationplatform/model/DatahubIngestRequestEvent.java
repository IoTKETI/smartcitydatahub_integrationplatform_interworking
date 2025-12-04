package kr.re.keti.sc.interworking.integrationplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatahubIngestRequestEvent {
	private IntegrationPlatformEventHistory integrationPlatformEventHistory;
	private MappingRule mappingRule;
}
