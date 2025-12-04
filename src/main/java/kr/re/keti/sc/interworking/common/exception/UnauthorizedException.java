package kr.re.keti.sc.interworking.common.exception;

import kr.re.keti.sc.interworking.common.ResponseCode;

@ResponseCodeType(ResponseCode.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {

	private static final long serialVersionUID = 3800354356842792724L;

	public UnauthorizedException(String detailDescription) {
		super(detailDescription);
	}
}