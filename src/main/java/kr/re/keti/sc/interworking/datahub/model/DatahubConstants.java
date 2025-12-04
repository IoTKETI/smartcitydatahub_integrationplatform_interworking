package kr.re.keti.sc.interworking.datahub.model;

public class DatahubConstants {
	public static final String CONTENT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static final String CONTENT_DATE_TIMEZONE = "Asia/Seoul";
	
	public static class IntegrityPlatformEventAttributeName {
		public static final String ID = "id";
		public static final String TYPE = "type";
		public static final String EVENT_ID = "evtId";
		public static final String EVENT_NAME = "evtNm";
		public static final String EVENT_GRADE = "evtGrad";
		public static final String OCCURRENCE_NUMBER = "ocrNbr";
		public static final String OCCURRENCE_STATUS = "ocrSts";
		public static final String OCCURRENCE_LOCATION = "ocrLc";
		public static final String OCCURRENCE_PLACE = "ocrPlc";
		public static final String OCCURRENCE_EVENT_CONTENT = "ocrEvtCn";
		public static final String OCCURRENCE_EVENT_TIME = "ocrEvtTime";
		public static final String OCCURRENCE_EVENT_STATUS = "ocrEvtSts";
		public static final String OCCURRENCE_EVENT_STATUS_CODE = "ocrEvtStsCd";
		public static final String OCCURRENCE_EVENT_STATUS_DETAIL = "ocrEvtStsDtl";
		
		public static final String OCCURRENCE_EVENT_STATUS_USER = "ocrEvtStsUsr";
		public static final String OCCURRENCE_EVENT_STATUS_USER_ID = "ocrEvtUsrId";
		public static final String OCCURRENCE_EVENT_STATUS_USER_NAME = "ocrEvtUsrNm";
		
		public static final String OCCURRENCE_EVENT_STATUS_TIME = "ocrEvtStsTime";
		public static final String OCCURRENCE_EVENT_END_TIME = "ocrEvtEndTime";
		
		public static final String OCCURRENCE_EVENT_ITEM = "ocrEvtItem";
		
		public static final String OCCURRENCE_EVENT_DETAIL = "ocrEvtDtl";
		public static final String OCCURRENCE_EVENT_DETAIL_CODE = "ocrEvtDtlCd";
		public static final String OCCURRENCE_EVENT_DETAIL_CODE_DETAIL = "ocrEvtDtlCdDtl";	
	}
	
	public static class DatahubAttributeName {
		public static final String ID = "id";
		public static final String TYPE = "type";

		public static final String VALUE = "value";
		public static final String CREATED_AT = "createdAt";
		public static final String MODIFIED_AT = "modifiedAt";
		public static final String OBSERVED_AT = "observedAt";
		public static final String DATASET_ID = "datasetId";
		public static final String CONTEXT = "@context";
		public static final String COORDINATES = "coordinates";
		public static final String POINT = "Point";
	}
}
