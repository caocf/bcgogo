package com.bcgogo.config.dto.upYun;

import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class UpYunFilePolicyDTO {
	private Date expiration;
	private List<UpYunPolicyDTO> conditions = new ArrayList<UpYunPolicyDTO>();

	public UpYunFilePolicyDTO(Date expiration) {
		super();
		this.expiration = expiration;
	}

	public Date getExpiration() {
		return expiration;
	}

	public List<UpYunPolicyDTO> getConditions() {
		return conditions;
	}

	public UpYunFilePolicyDTO addCondition(UpYunPolicyDTO condition) {
		conditions.add(condition);
		return this;
	}

  public String toJsonString() {
    if(CollectionUtils.isNotEmpty(conditions)){
      Map<String,Object> properties = new HashMap<String, Object>();
      properties.put(UpYunPolicyDTO.EXPIRATION, expiration.getTime());
      for(UpYunPolicyDTO upYunPolicyDTO: conditions){
        properties.put(upYunPolicyDTO.getName(), upYunPolicyDTO.getValue());
      }
      return JsonUtil.objectToJson(properties);
    }
    return null;
  }
}