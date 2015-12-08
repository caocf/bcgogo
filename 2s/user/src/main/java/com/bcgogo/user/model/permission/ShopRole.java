package com.bcgogo.user.model.permission;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.ShopRoleDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:52
 * 多类型店铺 与 资源
 */
@Entity
@Table(name = "shop_role")
public class ShopRole extends LongIdentifier {
  private Long roleId;
  private Long shopId;//保留 （对单个店面配置）
  private Long shopVersionId;

  public ShopRole() {
  }

  public ShopRole(ShopRoleDTO shopRoleDTO) {
    this.setId(shopRoleDTO.getId());
    this.setRoleId(shopRoleDTO.getRoleId());
    this.setShopId(shopRoleDTO.getShopId());
    this.setShopVersionId(shopRoleDTO.getShopVersionId());
  }


  public ShopRoleDTO toDTO() {
    ShopRoleDTO shopRoleDTO = new ShopRoleDTO();
    shopRoleDTO.setId(getId());
    shopRoleDTO.setRoleId(this.getRoleId());
    shopRoleDTO.setShopId(this.getShopId());
    shopRoleDTO.setShopVersionId(this.getShopVersionId());
    return shopRoleDTO;
  }

  @Column(name = "role_id")
  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  @Column(name = "shop_version_id")
  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
