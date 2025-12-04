package kr.re.keti.sc.interworking.datahub.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Data public class DateProperty {
	@Builder.Default
	String type = AttributeType.PROPERTY.getCode();
	@NonNull
	@JsonFormat(pattern = DatahubConstants.CONTENT_DATE_FORMAT, timezone = DatahubConstants.CONTENT_DATE_TIMEZONE)
	Date value;
	@JsonFormat(pattern = DatahubConstants.CONTENT_DATE_FORMAT, timezone = DatahubConstants.CONTENT_DATE_TIMEZONE)
	Date observedAt;
}