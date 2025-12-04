package kr.re.keti.sc.interworking.integrationplatform.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegrationPlatformEventTypeHandler extends BaseTypeHandler<IntegrationPlatformEventMessage> {
	
	private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, IntegrationPlatformEventMessage integrationPlatformEvent, JdbcType jdbcType) throws SQLException {
        try {
			preparedStatement.setString(i, objectMapper.writeValueAsString(integrationPlatformEvent));
		} catch (JsonProcessingException e) {
			log.error("Serialize IntegrationPlatformEvent error.", e);
		}
    }

    @Override
    public IntegrationPlatformEventMessage getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String integrationPlatformEventString = resultSet.getString(s);
        if(integrationPlatformEventString == null) {
            return null;
        }
        IntegrationPlatformEventMessage integrationPlatformEvent = null;
		try {
			integrationPlatformEvent = objectMapper.readValue(integrationPlatformEventString, IntegrationPlatformEventMessage.class);
		} catch (JsonProcessingException e) {
			log.error("Deserialize IntegrationPlatformEvent error.", e);
		}
		
        if(integrationPlatformEvent == null) {
            throw new IllegalArgumentException("No matching constant for [" + integrationPlatformEventString + "]");
        }
        return integrationPlatformEvent;
    }

    @Override
    public IntegrationPlatformEventMessage getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String integrationPlatformEventString = resultSet.getString(i);
        if(integrationPlatformEventString == null) {
            return null;
        }
        IntegrationPlatformEventMessage integrationPlatformEvent = null;
        try {
			integrationPlatformEvent = objectMapper.readValue(integrationPlatformEventString, IntegrationPlatformEventMessage.class);
		} catch (JsonProcessingException e) {
			log.error("Deserialize IntegrationPlatformEvent error.", e);
		}
        if(integrationPlatformEvent == null) {
            throw new IllegalArgumentException("No matching constant for [" + integrationPlatformEventString + "]");
        }
        return integrationPlatformEvent;
    }

    @Override
    public IntegrationPlatformEventMessage getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String integrationPlatformEventString = callableStatement.getString(i);
        if(integrationPlatformEventString == null) {
            return null;
        }
        IntegrationPlatformEventMessage integrationPlatformEvent = null;
        try {
			integrationPlatformEvent = objectMapper.readValue(integrationPlatformEventString, IntegrationPlatformEventMessage.class);
		} catch (JsonProcessingException e) {
			log.error("Deserialize IntegrationPlatformEvent error.", e);
		}
        if(integrationPlatformEvent == null) {
            throw new IllegalArgumentException("No matching constant for [" + integrationPlatformEventString + "]");
        }
        return integrationPlatformEvent;
    }
}