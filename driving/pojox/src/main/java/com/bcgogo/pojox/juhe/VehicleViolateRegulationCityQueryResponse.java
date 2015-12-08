package com.bcgogo.pojox.juhe;

/**
 * User: lw
 * Date: 14-04-23
 * Time: 下午1:58
 */
public class VehicleViolateRegulationCityQueryResponse {
  private String resultcode;
  private String reason;
  private String error_code;

  private VehicleViolateRegulationCityQueryResult result;

  public VehicleViolateRegulationCityQueryResponse() {

  }

  public VehicleViolateRegulationCityQueryResponse(String resultcode, String reason, VehicleViolateRegulationCityQueryResult result) {
    this.setResultcode(resultcode);
    this.setReason(reason);
    this.setResult(result);
  }


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

  public VehicleViolateRegulationCityQueryResult getResult() {
    return result;
  }

  public void setResult(VehicleViolateRegulationCityQueryResult result) {
    this.result = result;
  }

  public String getError_code() {
    return error_code;
  }

  public void setError_code(String error_code) {
    this.error_code = error_code;
  }
}
