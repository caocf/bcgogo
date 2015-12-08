package com.bcgogo.txn.dto;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;

/**
 * 库存信息封装类，适用于新的入库产品信息封装
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-19
 * Time: 下午1:25
 * To change this template use File | Settings | File Templates.
 */
public class InventoryInfoDTO {

  /* 库存 */
  private InventoryDTO inventoryDTO;

  /* 公共产品 */
    private ProductDTO productDTO;

  /* 本店产品 */
  private ProductLocalInfoDTO productLocalInfoDTO;

  /* 库存查询表 */
  private InventorySearchIndexDTO inventorySearchIndexDTO;

  /* 入库价格 */
  private PurchasePriceDTO purchasePriceDTO;

  private String storeHouse;

  public InventoryDTO getInventoryDTO() {
    return inventoryDTO;
  }

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public ProductLocalInfoDTO getProductLocalInfoDTO() {
    return productLocalInfoDTO;
  }

  public InventorySearchIndexDTO getInventorySearchIndexDTO() {
    return inventorySearchIndexDTO;
  }

  public PurchasePriceDTO getPurchasePriceDTO() {
    return purchasePriceDTO;
  }

  public void setInventoryDTO(InventoryDTO inventoryDTO) {
    this.inventoryDTO = inventoryDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public void setProductLocalInfoDTO(ProductLocalInfoDTO productLocalInfoDTO) {
    this.productLocalInfoDTO = productLocalInfoDTO;
  }

  public void setInventorySearchIndexDTO(InventorySearchIndexDTO inventorySearchIndexDTO) {
    this.inventorySearchIndexDTO = inventorySearchIndexDTO;
  }

  public void setPurchasePriceDTO(PurchasePriceDTO purchasePriceDTO) {
    this.purchasePriceDTO = purchasePriceDTO;
  }

  public String getStoreHouse() {
    return storeHouse;
  }

  public void setStoreHouse(String storeHouse) {
    this.storeHouse = storeHouse;
  }
}
