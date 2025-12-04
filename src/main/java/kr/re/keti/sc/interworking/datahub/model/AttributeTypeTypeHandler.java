package kr.re.keti.sc.interworking.datahub.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class AttributeTypeTypeHandler extends BaseTypeHandler<AttributeType> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, AttributeType attributeType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,attributeType.name());
    }

    @Override
    public AttributeType getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String attributeTypeString = resultSet.getString(s);
        if(attributeTypeString == null) {
            return null;
        }
        AttributeType processStatus = AttributeType.resolve(attributeTypeString);
        if(processStatus == null) {
            throw new IllegalArgumentException("No matching constant for [" + attributeTypeString + "]");
        }
        return processStatus;
    }

    @Override
    public AttributeType getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String attributeTypeString = resultSet.getString(i);
        if(attributeTypeString == null) {
            return null;
        }
        AttributeType resolveMethod = AttributeType.resolve(attributeTypeString);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + attributeTypeString + "]");
        }
        return resolveMethod;
    }

    @Override
    public AttributeType getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String attributeTypeString = callableStatement.getString(i);
        if(attributeTypeString == null) {
            return null;
        }
        AttributeType resolveMethod = AttributeType.resolve(attributeTypeString);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + attributeTypeString + "]");
        }
        return resolveMethod;
    }
}
