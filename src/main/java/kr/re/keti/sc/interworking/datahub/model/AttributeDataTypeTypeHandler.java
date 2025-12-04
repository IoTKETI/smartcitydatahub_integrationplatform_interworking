package kr.re.keti.sc.interworking.datahub.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class AttributeDataTypeTypeHandler extends BaseTypeHandler<AttributeDataType> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, AttributeDataType attributeDataType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,attributeDataType.name());
    }

    @Override
    public AttributeDataType getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String attributeDataTypeString = resultSet.getString(s);
        if(attributeDataTypeString == null) {
            return null;
        }
        AttributeDataType processStatus = AttributeDataType.resolve(attributeDataTypeString);
        if(processStatus == null) {
            throw new IllegalArgumentException("No matching constant for [" + attributeDataTypeString + "]");
        }
        return processStatus;
    }

    @Override
    public AttributeDataType getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String attributeDataTypeString = resultSet.getString(i);
        if(attributeDataTypeString == null) {
            return null;
        }
        AttributeDataType resolveMethod = AttributeDataType.resolve(attributeDataTypeString);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + attributeDataTypeString + "]");
        }
        return resolveMethod;
    }

    @Override
    public AttributeDataType getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String attributeDataTypeString = callableStatement.getString(i);
        if(attributeDataTypeString == null) {
            return null;
        }
        AttributeDataType resolveMethod = AttributeDataType.resolve(attributeDataTypeString);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + attributeDataTypeString + "]");
        }
        return resolveMethod;
    }
}
