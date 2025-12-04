package kr.re.keti.sc.interworking.datahub.model;

import java.util.Date;

import kr.re.keti.sc.interworking.integrationplatform.model.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistory {
	private Integer sequence;
	private Notification notification;
	private ProcessStatus processStatus;
	private String receivedData;//body String
	private Date creationTime;
}