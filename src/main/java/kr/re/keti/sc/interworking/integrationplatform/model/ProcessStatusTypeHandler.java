package kr.re.keti.sc.interworking.integrationplatform.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class ProcessStatusTypeHandler extends BaseTypeHandler<ProcessStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, ProcessStatus processStatus, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i,processStatus.name());
    }

    @Override
    public ProcessStatus getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String processStatusString = resultSet.getString(s);
        if(processStatusString == null) {
            return null;
        }
        ProcessStatus processStatus = ProcessStatus.resolve(processStatusString);
        if(processStatus == null) {
            throw new IllegalArgumentException("No matching constant for [" + processStatusString + "]");
        }
        return processStatus;
    }

    @Override
    public ProcessStatus getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String processStatusString = resultSet.getString(i);
        if(processStatusString == null) {
            return null;
        }
        ProcessStatus resolveMethod = ProcessStatus.resolve(processStatusString);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + processStatusString + "]");
        }
        return resolveMethod;
    }

    @Override
    public ProcessStatus getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String processStatusString = callableStatement.getString(i);
        if(processStatusString == null) {
            return null;
        }
        ProcessStatus resolveMethod = ProcessStatus.resolve(processStatusString);
        if(resolveMethod == null) {
            throw new IllegalArgumentException("No matching constant for [" + processStatusString + "]");
        }
        return resolveMethod;
    }
}