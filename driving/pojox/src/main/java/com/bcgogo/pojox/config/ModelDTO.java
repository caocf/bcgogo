package com.bcgogo.pojox.config;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-6
 * Time: 上午10:14
 */
public class ModelDTO {
  private Long modelId;
  private String modelName;

  public ModelDTO() {
  }

  public ModelDTO(Long modelId, String modelName) {
    setModelId(modelId);
    setModelName(modelName);
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }
}
