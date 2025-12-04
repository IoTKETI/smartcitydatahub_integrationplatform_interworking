package kr.re.keti.sc.interworking.datahub.model;

import java.util.HashMap;
import java.util.Map;

public enum AttributeType {
	PROPERTY("Property"),
	GEOPROPERTY("GeoProperty"),
	;
	
	private String code;
	private static final Map<String, AttributeType> valueMap = new HashMap<>(AttributeType.values().length);
	
	private AttributeType(String attributeTypeString) {
		this.code = attributeTypeString;
	}
	
	public String getCode() {
		return code;
	}
	static {
		for (AttributeType it : values()) {
			valueMap.put(it.name(), it);
		}
	}
		
	public static AttributeType resolve (String attributeTypeString) {
		return valueMap.get(attributeTypeString);
	}
}
