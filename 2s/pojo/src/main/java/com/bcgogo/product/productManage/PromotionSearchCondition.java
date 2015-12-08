package com.bcgogo.product.productManage;

import com.bcgogo.common.Sort;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.shop.ShopKind;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-8-6
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
public class PromotionSearchCondition {
  private Long shopId;
  private PromotionsEnum.PromotionStatus promotionStatus;
  private List<PromotionsEnum.PromotionStatus> promotionStatusList;
  private ShopKind shopKind;
  private Long[] productIds;
  private int startPageNo=1;
  private int maxRows = 0;//默认0
  private Sort sort;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public PromotionsEnum.PromotionStatus getPromotionStatus() {
    return promotionStatus;
  }

  public void setPromotionStatus(PromotionsEnum.PromotionStatus promotionStatus) {
    this.promotionStatus = promotionStatus;
  }

  public Long[] getProductIds() {
    return productIds;
  }

  public void setProductIds(Long[] productIds) {
    this.productIds = productIds;
  }

  public List<PromotionsEnum.PromotionStatus> getPromotionStatusList() {
    return promotionStatusList;
  }

  public void setPromotionStatusList(List<PromotionsEnum.PromotionStatus> promotionStatusList) {
    this.promotionStatusList = promotionStatusList;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public Sort getSort() {
    return sort;
  }

  public void setSort(Sort sort) {
    this.sort = sort;
  }
}
