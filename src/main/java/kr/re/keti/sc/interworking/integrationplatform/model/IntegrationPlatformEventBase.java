package kr.re.keti.sc.interworking.integrationplatform.model;

import java.util.Date;

import lombok.Data;

@Data public class IntegrationPlatformEventBase {
	private IntegrationPlatformEventMessage integrationPlatformEventMessage;
	private Date creationTime;
	private Date modificationTime;
}