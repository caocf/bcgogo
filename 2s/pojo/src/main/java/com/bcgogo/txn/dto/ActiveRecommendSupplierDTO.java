package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.Product.RecommendSupplierType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-5-20
 * Time: 上午9:50
 */
public class ActiveRecommendSupplierDTO {
  private ProductDTO productDTO;
  private SupplierDTO supplierDTO;
  private ShopDTO shopDTO;


  private Map<RecommendSupplierType, SupplierDTO> supplierMap = new HashMap<RecommendSupplierType, SupplierDTO>();

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public Map<RecommendSupplierType, SupplierDTO> getSupplierMap() {
    return supplierMap;
  }

  public void setSupplierMap(Map<RecommendSupplierType, SupplierDTO> supplierMap) {
    this.supplierMap = supplierMap;
  }

  public SupplierDTO getSupplierDTO() {
    return supplierDTO;
  }

  public void setSupplierDTO(SupplierDTO supplierDTO) {
    this.supplierDTO = supplierDTO;
  }

  public ShopDTO getShopDTO() {
    return shopDTO;
  }

  public void setShopDTO(ShopDTO shopDTO) {
    this.shopDTO = shopDTO;
  }
}
