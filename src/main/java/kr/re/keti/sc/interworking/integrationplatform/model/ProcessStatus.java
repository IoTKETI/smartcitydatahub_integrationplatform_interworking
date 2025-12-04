package kr.re.keti.sc.interworking.integrationplatform.model;

import java.util.HashMap;
import java.util.Map;

public enum ProcessStatus {
	PROCESSED ("PROCESSED"),
	PROCESSING ( "PROCESSING"),//CURRENTLY NOT USED
	PARSING_ERROR ( "PARSING_ERROR"),
	DECRYPTION_ERROR ( "DECRYPTION_ERROR"),
	DATAHUB_INTERWORKING_ERROR ("DATAHUB_INTERWORKING_ERROR"),
	DATAHUB_NOT_REACHABLE ("DATAHUB_NOT_REACHABLE"),
	MAPPING_RULE_NOT_FOUND ("MAPPING_RULE_NOT_FOUND"),
	NOT_PROCESSED ("NOT_PROCESSED"),
	;
	
	private final String processStatus;
	
	private static final Map<String, ProcessStatus> valueMap = new HashMap<>(ProcessStatus.values().length);
	
	static {
		for (ProcessStatus it : values()) {
			valueMap.put(it.getProcessStatus(), it);
		}
	}
	
	private ProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getProcessStatus() {
		return processStatus;
	}
	
	public static ProcessStatus resolve (String processStatusString) {
		return valueMap.get(processStatusString);
	}
}