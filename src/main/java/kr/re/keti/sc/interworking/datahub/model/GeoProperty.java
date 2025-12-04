package kr.re.keti.sc.interworking.datahub.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data public class GeoProperty {
	String type = AttributeType.GEOPROPERTY.getCode();
	GeoPropertyValue value;
	@JsonFormat(pattern = DatahubConstants.CONTENT_DATE_FORMAT, timezone = DatahubConstants.CONTENT_DATE_TIMEZONE)
	Date observedAt;
	
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@RequiredArgsConstructor
	@Data public static class GeoPropertyValue {
		String type = "Point";
		@NonNull
		List <Double> coordinates;
	}
}

