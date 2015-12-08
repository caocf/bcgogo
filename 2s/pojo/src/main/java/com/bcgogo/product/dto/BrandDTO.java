package com.bcgogo.product.dto;

import com.bcgogo.product.BrandRequest;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
public class BrandDTO implements Serializable {
  private String name;
  private String nameEn;
  private String firstLetter;
  private Long state;
  private String memo;
  private Long id;
  private Long shopId;
  private List<ModelDTO> modelDTOList;
  public BrandDTO() {
  }

  public BrandDTO(BrandRequest request) {
    setName(request.getName());
    setNameEn(request.getNameEn());
    setFirstLetter(request.getFirstLetter());
    setState(request.getState());
    setMemo(request.getMemo());
    setShopId(request.getShopId());
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<ModelDTO> getModelDTOList() {
    return modelDTOList;
  }

  public void setModelDTOList(List<ModelDTO> modelDTOList) {
    this.modelDTOList = modelDTOList;
  }

  public static Map<Long,BrandDTO> ListToMap(List<BrandDTO> list)
  {
    Map<Long,BrandDTO> map = new HashMap<Long, BrandDTO>();

    if(CollectionUtils.isEmpty(list))
    {
      return map;
    }

    for(BrandDTO brandDTO : list)
    {
      map.put(brandDTO.getId(),brandDTO);
    }

    return map;
  }
}
