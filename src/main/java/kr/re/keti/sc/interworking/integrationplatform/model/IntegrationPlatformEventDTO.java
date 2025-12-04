package kr.re.keti.sc.interworking.integrationplatform.model;

import lombok.Data;

@Data public class IntegrationPlatformEventDTO {
	private String svcTy;
	private String hdCnt;
	private String dtCnt;
	private String apSeq;
	private String cmnMtd;
	private String sendCd;
	private String rcvCd;
	private String encAt;
	private String tranTy;
	private String rrKey;
	private String reqDate;
	private String bodyLen;
	private String sysCd;
	private String tranSysHis;
	private String Body;
}