package kr.re.keti.sc.interworking;

import java.text.SimpleDateFormat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.re.keti.sc.interworking.datahub.model.DatahubConstants;

@SpringBootApplication
//@ComponentScan(basePackages = {"kr.re.keti.sc.interworking.integrationplatform", "kr.re.keti.sc.interworking.datahub", "kr.re.keti.sc.common"})
@MapperScan({"kr.re.keti.sc.interworking.integrationplatform.*", "kr.re.keti.sc.interworking.datahub.*"})
@EnableScheduling
public class Application 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Primary
    @Bean
    public ObjectMapper objectMapper() {
    	ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat(DatahubConstants.CONTENT_DATE_FORMAT));
        return objectMapper;
    }
}
