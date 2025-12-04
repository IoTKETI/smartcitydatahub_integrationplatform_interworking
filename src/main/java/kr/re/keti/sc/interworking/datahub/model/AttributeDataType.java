package kr.re.keti.sc.interworking.datahub.model;

import java.util.HashMap;
import java.util.Map;

public enum AttributeDataType {
	STRING,
	INTEGER,
	DOUBLE,
	BOOLEAN,
	DATE,
	GEOJSON,
	DOUBLE_ARRAY
	;
	
	private static final Map<String, AttributeDataType> valueMap = new HashMap<>(AttributeDataType.values().length);

	static {
		for (AttributeDataType it : values()) {
			valueMap.put(it.name(), it);
		}
	}
		
	public static AttributeDataType resolve (String attributeDataTypeString) {
		return valueMap.get(attributeDataTypeString);
	}
	
}
