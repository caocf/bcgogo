package com.bcgogo.config.dto.juhe;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-10-23
 * Time: 下午1:58
 */
public class ViolateRegulationCitySearchConditionResponse {
  private String resultcode;
  private String reason;
  private Map<String, ViolateRegulationCitySearchConditionProvince> result;

  public boolean isSuccess() {
    return "200".equals(resultcode);
  }


  public String getResultcode() {
    return resultcode;
  }

  public void setResultcode(String resultcode) {
    this.resultcode = resultcode;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Map<String, ViolateRegulationCitySearchConditionProvince> getResult() {
    return result;
  }

  public void setResult(Map<String, ViolateRegulationCitySearchConditionProvince> result) {
    this.result = result;
  }

}
