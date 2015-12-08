package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.preBuyOrder.PreBuyOrderValidDate;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class PreBuyOrderMessageDTO{
  private Long id;
  private String idStr;
  private Long preBuyOrderId;
  private String preBuyOrderIdStr;
  private Long shopId;
  private String shopName;
  private ShopKind shopKind;
  private Long shopProvince;     //省
  private Long shopCity;          //市
  private Long shopRegion;        //区域
  private Long editDate;
  private Long endDate;
  private Long vestDate;
  private String areaName;

  public PreBuyOrderMessageDTO() {

  }
  public PreBuyOrderMessageDTO(ShopDTO shopDTO,PreBuyOrderDTO preBuyOrderDTO) {
    this.setShopInfo(shopDTO);
    this.editDate = preBuyOrderDTO.getEditDate();
    this.vestDate = preBuyOrderDTO.getVestDate();
    this.endDate = preBuyOrderDTO.getEndDate();
    this.preBuyOrderId = preBuyOrderDTO.getId();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.idStr = id.toString();
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getPreBuyOrderIdStr() {
    return preBuyOrderIdStr;
  }

  public void setPreBuyOrderIdStr(String preBuyOrderIdStr) {
    this.preBuyOrderIdStr = preBuyOrderIdStr;
  }

  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
    this.preBuyOrderIdStr = preBuyOrderId==null?"":preBuyOrderId.toString();
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public Long getShopProvince() {
    return shopProvince;
  }

  public void setShopProvince(Long shopProvince) {
    this.shopProvince = shopProvince;
  }

  public Long getShopCity() {
    return shopCity;
  }

  public void setShopCity(Long shopCity) {
    this.shopCity = shopCity;
  }

  public Long getShopRegion() {
    return shopRegion;
  }

  public void setShopRegion(Long shopRegion) {
    this.shopRegion = shopRegion;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public void setShopInfo(ShopDTO shopDTO){
    this.setShopRegion(shopDTO.getRegion());
    this.setShopProvince(shopDTO.getProvince());
    this.setShopCity(shopDTO.getCity());
    this.setShopName(shopDTO.getName());
    this.setShopKind(shopDTO.getShopKind());
    this.setShopId(shopDTO.getId());
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public void setAreaNameByAreaNo(Map<Long, AreaDTO> areaMap) {
    StringBuffer sb = new StringBuffer();
    if (areaMap != null && !areaMap.isEmpty()) {
      if (this.getShopProvince() != null) {
        AreaDTO provinceArea = areaMap.get(this.getShopProvince());
        if (provinceArea != null) {
          sb.append(provinceArea.getName());
        }
      }
      if (this.getShopCity() != null) {
        AreaDTO cityArea = areaMap.get(this.getShopCity());
        if (cityArea != null) {
          sb.append(cityArea.getName());
        }
      }
      if (this.getShopRegion() != null) {
        AreaDTO regionArea = areaMap.get(this.getShopRegion());
        if (regionArea != null) {
          sb.append(regionArea.getName());
        }
      }
    }
    this.setAreaName(sb.toString());
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }


  public Map<String,String> getMessageContentData(){
    Map<String,String> data = new HashMap<String, String>();
    data.put("preBuyOrderId",this.getPreBuyOrderId().toString());
    data.put("messageId",this.getId().toString());
    data.put("vestDate",this.getVestDate().toString());
    data.put("endDate",this.getEndDate().toString());
    data.put("content","来自"+this.getAreaName()+"的买家"+this.getShopName()+"刚刚发布了一条求购信息！点击【<a>点击查看详情</a>】");//消息组件必须的
    return data;
  }
}
