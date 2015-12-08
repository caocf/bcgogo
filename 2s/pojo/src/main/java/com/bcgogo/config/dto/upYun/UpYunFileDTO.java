package com.bcgogo.config.dto.upYun;

public class UpYunFileDTO {
  public String bucket;
  public String signature;
  public String policy;

  public UpYunFileDTO(String bucket, String signature, String policy) {
    this.policy = policy;
    this.signature = signature;
    this.bucket = bucket;
  }

  public String getSignature() {
    return signature;
  }

  public String getPolicy() {
    return policy;
  }

  public String getBucket() {
    return bucket;
  }
}
