package com.bcgogo.txn.dto;

import com.bcgogo.user.dto.SupplierDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:44
 */
public class ShoppingCartDTO implements Serializable {
  private Long shopId;
  private int shoppingCartItemCount;
  private int shoppingCartMaxCapacity;

  private Map<SupplierDTO,List<ShoppingCartItemDTO>> shoppingCartDetailMap;

  private Double total;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Map<SupplierDTO, List<ShoppingCartItemDTO>> getShoppingCartDetailMap() {
    return shoppingCartDetailMap;
  }

  public void setShoppingCartDetailMap(Map<SupplierDTO, List<ShoppingCartItemDTO>> shoppingCartDetailMap) {
    this.shoppingCartDetailMap = shoppingCartDetailMap;
  }

  public int getShoppingCartMaxCapacity() {
    return shoppingCartMaxCapacity;
  }

  public void setShoppingCartMaxCapacity(int shoppingCartMaxCapacity) {
    this.shoppingCartMaxCapacity = shoppingCartMaxCapacity;
  }

  public int getShoppingCartItemCount() {
    return shoppingCartItemCount;
  }

  public void setShoppingCartItemCount(int shoppingCartItemCount) {
    this.shoppingCartItemCount = shoppingCartItemCount;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }
}
