package kr.re.keti.sc.interworking.datahub.model;

import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationPlatformRequestEvent {
	private Notification notification;
	private CommonEntityVO notificationProcessingData;
	private MappingRule mappingRule;
}
