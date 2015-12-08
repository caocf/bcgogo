package com.bcgogo.product;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.Product.BcgogoProductScene;
import com.bcgogo.enums.txn.finance.PaymentType;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.*;


public class BcgogoProductDTO implements Serializable {
  private Long id;
  private String idStr;
  private String name;  // 品名
  private String text;  // 显示用的
  private String description; // 商品描述
  private Double defaultPrice;
  private String unit;
  private PaymentType paymentType;
  private String showToShopVersions;
  private String imagePath;//主图 相对路径
  private ImageCenterDTO imageCenterDTO;
  private BcgogoProductScene productScene;
  private List<BcgogoProductPropertyDTO> propertyDTOList;

  private Map<String,String> propertyKindMap;

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) idStr = id.toString();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<BcgogoProductPropertyDTO> getPropertyDTOList() {
    return propertyDTOList;
  }

  public void setPropertyDTOList(List<BcgogoProductPropertyDTO> propertyDTOList) {
    this.propertyDTOList = propertyDTOList;
    if(CollectionUtils.isNotEmpty(propertyDTOList)){
      String value = null;
      propertyKindMap = new HashMap<String, String>();
      for(BcgogoProductPropertyDTO propertyDTO : propertyDTOList){
        if(propertyDTO.getId()!=null){
          value = propertyKindMap.get(propertyDTO.getKind());
          if(value==null){
            propertyKindMap.put(propertyDTO.getKind(),propertyDTO.getId().toString());
          }else{
            propertyKindMap.put(propertyDTO.getKind(),value+","+propertyDTO.getId().toString());
          }
          if(this.getDefaultPrice()==null || this.getDefaultPrice()>propertyDTO.getPrice())
            this.setDefaultPrice(propertyDTO.getPrice());
        }
      }
    }
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getShowToShopVersions() {
    return showToShopVersions;
  }

  public void setShowToShopVersions(String showToShopVersions) {
    this.showToShopVersions = showToShopVersions;
  }

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public Double getDefaultPrice() {
    return defaultPrice;
  }

  public void setDefaultPrice(Double defaultPrice) {
    this.defaultPrice = defaultPrice;
  }

  public Map<String, String> getPropertyKindMap() {
    return propertyKindMap;
  }

  public void setPropertyKindMap(Map<String, String> propertyKindMap) {
    this.propertyKindMap = propertyKindMap;
  }

  public BcgogoProductScene getProductScene() {
    return productScene;
  }

  public void setProductScene(BcgogoProductScene productScene) {
    this.productScene = productScene;
  }
}
