package com.bcgogo.product.model;

import com.bcgogo.BooleanEnum;import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PromotionsAreaDTO;

import javax.persistence.*;

/** 这里areaNo 到二级 市
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-6-30
 * Time: 下午9:21
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "promotions_area")
public class PromotionsArea extends LongIdentifier {

  private Long shopId;
  private Long areaNo;
  private Long promotionsId;
  private PromotionsEnum.PostType postType;     //指包邮地区类型
  private DeletedType deleted;

  public PromotionsArea(){
    this.deleted=DeletedType.FALSE;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "area_no")
  public Long getAreaNo() {
    return areaNo;
  }

  public void setAreaNo(Long areaNo) {
    this.areaNo = areaNo;
  }

  @Column(name = "promotions_id")
  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  @Column(name = "post_type")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PostType getPostType() {
    return postType;
  }

  public void setPostType(PromotionsEnum.PostType postType) {
    this.postType = postType;
  }



  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public PromotionsAreaDTO toDTO(){
    PromotionsAreaDTO pAreaDTO = new PromotionsAreaDTO();
    pAreaDTO.setAreaNo(this.getAreaNo());
    pAreaDTO.setPostType(this.getPostType());
    pAreaDTO.setPromotionsId(this.getPromotionsId());
    return pAreaDTO;
  }
}
