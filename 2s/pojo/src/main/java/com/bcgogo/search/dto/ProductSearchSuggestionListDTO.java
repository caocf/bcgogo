package com.bcgogo.search.dto;

import com.bcgogo.product.dto.ProductDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/23/12
 * Time: 12:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProductSearchSuggestionListDTO {
  public List<SearchSuggestionDTO> getSuggestionDTOs() {
    return suggestionDTOs;
  }

  public void setSuggestionDTOs(List<SearchSuggestionDTO> suggestionDTOs) {
    this.suggestionDTOs = suggestionDTOs;
  }

  public List<ProductDTO> getProductDetailResultDTOs() {
    return productDetailResultDTOs;
  }

  public void setProductDetailResultDTOs(List<ProductDTO> productDetailResultDTOs) {
    this.productDetailResultDTOs = productDetailResultDTOs;
  }

  private List<SearchSuggestionDTO> suggestionDTOs;
  private List<ProductDTO>     productDetailResultDTOs;

  private long productDetailTotalCount;

  public long getProductDetailTotalCount() {
    return productDetailTotalCount;
  }

  public void setProductDetailTotalCount(long productDetailTotalCount) {
    this.productDetailTotalCount = productDetailTotalCount;
  }
}
