package kr.re.keti.sc.interworking.common.exception;

import kr.re.keti.sc.interworking.common.ResponseCode;

@ResponseCodeType(ResponseCode.NOT_FOUND)
public class NotFoundException extends BaseException {

	private static final long serialVersionUID = 3681821950534703062L;

	public NotFoundException(String detailDescription) {
		super(detailDescription);
	}
}