package kr.re.keti.sc.interworking.datahub.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.re.keti.sc.interworking.datahub.model.NotificationHistory;
import kr.re.keti.sc.interworking.datahub.model.QueryValue;
import kr.re.keti.sc.interworking.integrationplatform.model.MappingRule;

@Repository
public interface DatahubMapper {
	List<MappingRule> selectMappingRule(QueryValue queryValue);
}