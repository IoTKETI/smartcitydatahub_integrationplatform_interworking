package kr.re.keti.sc.interworking.datahub.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryValue {
	String datahubEntityType;
	String datahubDatasetId;
}
