package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.config.dto.upYun.UpYunFileDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-17
 * Time: 下午8:04
 */
public class UpYunParamResponse extends ApiResponse {
  private String bucket;
  private String signature;
  private String policy;
  private int expireTime;

    public UpYunParamResponse(){
      super();
    }

  public UpYunParamResponse(ApiResponse apiResponse){
    super(apiResponse);
  }

  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getPolicy() {
    return policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public int getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(int expireTime) {
    this.expireTime = expireTime;
  }

  public void setUpYunFileDTO(UpYunFileDTO upYunFileDTO) {
    if(upYunFileDTO != null){
      setPolicy(upYunFileDTO.getPolicy());
      setBucket(upYunFileDTO.getBucket());
      setSignature(upYunFileDTO.getSignature());
    }
  }
}
