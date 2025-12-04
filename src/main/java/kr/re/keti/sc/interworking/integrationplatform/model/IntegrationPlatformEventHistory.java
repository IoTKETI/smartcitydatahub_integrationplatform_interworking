package kr.re.keti.sc.interworking.integrationplatform.model;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor 
public class IntegrationPlatformEventHistory {
	private Integer sequence;
	private IntegrationPlatformEventMessage integrationPlatformEventMessage;
	private ProcessStatus processStatus;
	private String receivedBodyData;//Event body String (Either plain or encrypted)
	private Date creationTime;
}