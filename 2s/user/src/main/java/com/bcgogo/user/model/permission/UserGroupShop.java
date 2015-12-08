package com.bcgogo.user.model.permission;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.UserGroupShopDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:52
 */

@Entity
@Table(name = "user_group_shop")
public class UserGroupShop extends LongIdentifier {
  private Long userGroupId;
  private Long shopVersionId;
  private Long shopId;

  public UserGroupShop() {
  }

  public UserGroupShop(UserGroupShopDTO userGroupShopDTO) {
    this.setId(userGroupShopDTO.getId());
    this.setShopId(userGroupShopDTO.getShopId());
    this.setShopVersionId(userGroupShopDTO.getShopVersionId());
    this.setUserGroupId(userGroupShopDTO.getUserGroupId());
  }

  public UserGroupShop fromDTO(UserGroupShopDTO userGroupShopDTO) {
    this.setId(userGroupShopDTO.getId());
    this.setUserGroupId(userGroupShopDTO.getUserGroupId());
    this.setShopVersionId(userGroupShopDTO.getShopVersionId());
    this.setShopId(this.getShopId());
    return this;
  }

  public UserGroupShopDTO toDTO() {
    UserGroupShopDTO userGroupShopDTO = new UserGroupShopDTO();
    userGroupShopDTO.setId(this.getId());
    userGroupShopDTO.setUserGroupId(this.getUserGroupId());
    userGroupShopDTO.setShopId(this.getShopId());
    userGroupShopDTO.setShopVersionId(this.getShopVersionId());
    return userGroupShopDTO;
  }

  @Column(name = "user_group_id")
  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "shop_version_id")
  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }
}
