package kr.re.keti.sc.interworking.common.exception;

import kr.re.keti.sc.interworking.common.ResponseCode;

@ResponseCodeType(ResponseCode.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends BaseException {

	private static final long serialVersionUID = -1598584115757128682L;

	public InternalServerErrorException(String detailDescription) {
		super(detailDescription);
	}
}