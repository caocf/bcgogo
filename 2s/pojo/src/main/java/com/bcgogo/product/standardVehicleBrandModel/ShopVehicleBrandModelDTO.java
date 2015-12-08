package com.bcgogo.product.standardVehicleBrandModel;


import com.bcgogo.enums.DeletedType;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.VehicleBrandModelRelationDTO;

/**
 * 店铺注册时填写的车辆品牌、车型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public class ShopVehicleBrandModelDTO {

  private Long id;
  private Long shopId;
  private String firstLetter;//车辆品牌首字母（不一定是首个汉字的首字母）
  private String brandName;  //车辆品牌
  private String brandPy;  //车辆品牌
  private String brandFl;  //车辆品牌
  private String modelName; //车型
  private String modelPy; //车型
  private String modelFl; //车型
  private Long brandId;  //车辆品牌id
  private Long modelId; //车型id
  private Boolean checked=false;
  private DeletedType deleted = DeletedType.FALSE;
  public ShopVehicleBrandModelDTO() {
  }

  public ShopVehicleBrandModelDTO(VehicleBrandModelRelationDTO vehicleBrandModelRelationDTO) {
    this.firstLetter = vehicleBrandModelRelationDTO.getFirstLetter();
    this.brandName = vehicleBrandModelRelationDTO.getBrandName();
    this.modelName = vehicleBrandModelRelationDTO.getModelName();
    this.brandId = vehicleBrandModelRelationDTO.getBrandId();
    this.modelId = vehicleBrandModelRelationDTO.getModelId();
  }

  public CheckNode toBrandCheckNode() {
    CheckNode node = new CheckNode();
    node.setId(this.getBrandId());
    node.setText(this.getBrandName());
    node.setValue(this.getBrandName());
    node.setParentId(-1L);
    node.setShopId(this.getShopId());
    node.setType(Node.Type.FIRST_CATEGORY);
    node.setLeaf(false);
    node.setChecked(this.getChecked());
    return node;
  }

  public CheckNode toModelCheckNode() {
    CheckNode node = new CheckNode();
    node.setId(this.getModelId());
    node.setText(this.getModelName());
    node.setValue(this.getModelName());
    node.setParentId(this.getBrandId());
    node.setShopId(this.getShopId());
    node.setType(Node.Type.SECOND_CATEGORY);
    node.setLeaf(true);
    node.setChecked(this.getChecked());
    return node;
  }

    public String getBrandFl() {
        return brandFl;
    }

    public void setBrandFl(String brandFl) {
        this.brandFl = brandFl;
    }

    public String getModelFl() {
        return modelFl;
    }

    public void setModelFl(String modelFl) {
        this.modelFl = modelFl;
    }

    public String getBrandPy() {
        return brandPy;
    }

    public void setBrandPy(String brandPy) {
        this.brandPy = brandPy;
    }

    public String getModelPy() {
        return modelPy;
    }

    public void setModelPy(String modelPy) {
        this.modelPy = modelPy;
    }

    public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public Boolean getChecked() {
    return checked;
  }

  public void setChecked(Boolean checked) {
    this.checked = checked;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
