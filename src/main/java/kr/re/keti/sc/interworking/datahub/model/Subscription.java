package kr.re.keti.sc.interworking.datahub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Subscription {
	@JsonIgnore
	String id;
	
	String type;
	List<EntityInfo> entities;
	String subscriptionName;
	String description;
//	List<String> watchedAttributes;
//	Integer timeInterval;
	String q;
//	GeoQuery geoQ;
//	String csf;
//	Boolean isActive;
//	Date expiresAt;
//	Integer throttling;
	String datasetId;
//	TemporalQuery temporalQ;
//	String lang;
	String subscriptionEntityId;
	
	public static class EntityInfo {
//		String id;
//		String idPattern;
		String type;
	}
	
//	public static class GeoQuery {
//		String geometry;
//		String coordinates;
//		String georel;
//		String geoproperty;
//	}
	
//	public static class NotificationParams {
//		List<String> attributes;
//		String format;
//		Endpoint endpoint;	
//	}
}
