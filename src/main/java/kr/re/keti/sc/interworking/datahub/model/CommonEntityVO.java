package kr.re.keti.sc.interworking.datahub.model;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@SuppressWarnings("serial")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CommonEntityVO extends HashMap<String, Object> {

	public String getId() {
		return (String) super.get(DatahubConstants.DatahubAttributeName.ID);
	}
	public void setId(String id) {
		super.put(DatahubConstants.DatahubAttributeName.ID, id);
	}

	public String getDatasetId() {
		return (String) super.get(DatahubConstants.DatahubAttributeName.DATASET_ID);
	}
	public void setDatasetId(String datasetId) {
		super.put(DatahubConstants.DatahubAttributeName.DATASET_ID, datasetId);
	}

	public Date getCreatedAt() {
		return (Date) super.get(DatahubConstants.DatahubAttributeName.CREATED_AT);
	}
	public void setCreatedAt(Date createdAt) {
		super.put(DatahubConstants.DatahubAttributeName.CREATED_AT, createdAt);
	}

	public Date getModifiedAt() {
		return (Date) super.get(DatahubConstants.DatahubAttributeName.MODIFIED_AT);
	}
	public void setModifiedAt(Date modifiedAt) {
		super.put(DatahubConstants.DatahubAttributeName.MODIFIED_AT, modifiedAt);
	}
}
