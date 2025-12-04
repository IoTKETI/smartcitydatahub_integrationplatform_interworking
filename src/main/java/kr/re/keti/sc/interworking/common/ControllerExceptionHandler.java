package kr.re.keti.sc.interworking.common;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import kr.re.keti.sc.interworking.common.exception.BaseException;
import kr.re.keti.sc.interworking.common.exception.ResponseCodeType;
import kr.re.keti.sc.interworking.integrationplatform.model.IntegrationPlatformResponse;

@RestControllerAdvice
public class ControllerExceptionHandler {
    
    @ExceptionHandler(value = {BaseException.class})
    protected ResponseEntity<Object> handleBaseException(BaseException ex, WebRequest request) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        ResponseCodeType responseCodeType = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseCodeType.class);
        
        return new ResponseEntity<>(IntegrationPlatformResponse.obtainFailureResponse(responseCodeType.value(), ((BaseException)ex).getDetailDescription()), headers, ResponseCode.OK.getHttpStatusCode());
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
    	return new ResponseEntity<>(IntegrationPlatformResponse.obtainFailureResponse(ResponseCode.NOT_FOUND, "No handler found."), headers, ResponseCode.NOT_FOUND.getHttpStatusCode());    	
    }
}