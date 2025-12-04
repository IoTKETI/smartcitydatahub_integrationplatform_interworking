package kr.re.keti.sc.interworking.datahub.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Notification {
	String id;
	String type;
	String subscriptionId;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = DatahubConstants.CONTENT_DATE_TIMEZONE)
	Date notifiedAt;
	List<CommonEntityVO> data;
}