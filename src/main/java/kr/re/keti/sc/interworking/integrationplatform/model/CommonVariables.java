package kr.re.keti.sc.interworking.integrationplatform.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
public class CommonVariables {
	@Getter
	@Setter
	private static String sessionKey;
	
	@Getter
	private static String sendingCityCode;
	
	@Getter
	private static String receivingCityCode;
	
	@Getter
	private static String systemCode;
	
	@Value("${datahub-to-integrationPlatform.sendingCityCode}")
    private void setSendingCityCode(String sendingCityCode){
		CommonVariables.sendingCityCode = sendingCityCode;
    }
	
	@Value("${datahub-to-integrationPlatform.receivingCityCode}")
    private void setReceivingCityCode(String receivingCityCode){
		CommonVariables.receivingCityCode = receivingCityCode;
    }
	
	@Value("${datahub-to-integrationPlatform.SystemCode}")
    private void setSystemCode(String systemCode){
		CommonVariables.systemCode = systemCode;
    }
}
