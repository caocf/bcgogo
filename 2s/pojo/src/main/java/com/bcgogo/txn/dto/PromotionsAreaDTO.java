package com.bcgogo.txn.dto;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-1
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
public class PromotionsAreaDTO {
  private Long shopId;
  private Long areaNo;
  private Long promotionsId;
  private PromotionsEnum.PostType postType;
  private DeletedType deleted;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getAreaNo() {
    return areaNo;
  }

  public void setAreaNo(Long areaNo) {
    this.areaNo = areaNo;
  }

  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  public PromotionsEnum.PostType getPostType() {
    return postType;
  }

  public void setPostType(PromotionsEnum.PostType postType) {
    this.postType = postType;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
