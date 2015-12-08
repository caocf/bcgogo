package com.bcgogo.txn.dto;

import com.bcgogo.product.dto.ProductDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-8-16
 * Time: 上午10:16
 * To change this template use File | Settings | File Templates.
 */
public class HeavyProductDTO {
    private ProductDTO[] productDTOs;

  public ProductDTO[] getProductDTOs() {
    return productDTOs;
  }

  public void setProductDTOs(ProductDTO[] productDTOs) {
    this.productDTOs = productDTOs;
  }
}
