package kr.re.keti.sc.interworking.datahub.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PropertyInfo {
    private List<String> propertyNames;
    private String propertyMemberName;
}
