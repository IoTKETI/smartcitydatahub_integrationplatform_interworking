/**
 * <PRE>
 *  Project 3MP.master-api
 *  Package com.kt.iot.base.exception
 * </PRE>
 * @brief
 * @file UnauthorizedException.java
 * @date 2015. 12. 22. 오전 10:21:23
 * @author kim.seokhun@kt.com
 *  변경이력
 *        이름     : 일자          : 근거자료   : 변경내용
 *       ------------------------------------
 *        kim.seokhun@kt.com  : 2015. 12. 22.       :            : 신규 개발.
 *
 * Copyright © 2013 kt corp. all rights reserved.
 */
package kr.re.keti.sc.interworking.common.exception;

public abstract class BaseException extends RuntimeException {

	private static final long serialVersionUID = 6697553987008675632L;
	
	private String detailDescription;
	
	public BaseException (String detailDescription) {
		this.detailDescription = detailDescription;
		
	}

	public String getDetailDescription() {
		return detailDescription;
	}

	public void setDetailDescription(String detailDescription) {
		this.detailDescription = detailDescription;
	}
}
