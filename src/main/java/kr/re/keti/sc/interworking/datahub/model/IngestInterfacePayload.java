package kr.re.keti.sc.interworking.datahub.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data public class IngestInterfacePayload {
	String datasetId;
	List<Map<String, Object>> entities;
}