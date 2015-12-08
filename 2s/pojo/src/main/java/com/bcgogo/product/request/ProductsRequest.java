package com.bcgogo.product.request;

import com.bcgogo.product.dto.ProductDTO;

import java.util.ArrayList;

/**
 * User: ZhangJuntao
 * Date: 13-7-23
 * Time: 下午6:07
 */
public class ProductsRequest {
  private ArrayList<ProductDTO> productDTOs;

  public ArrayList<ProductDTO> getProductDTOs() {
    return productDTOs;
  }

  public void setProductDTOs(ArrayList<ProductDTO> productDTOs) {
    this.productDTOs = productDTOs;
  }
}
