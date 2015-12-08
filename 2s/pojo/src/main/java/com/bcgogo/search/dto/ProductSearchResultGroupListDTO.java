package com.bcgogo.search.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hans
 * Date: 12-12-4
 * Time: 上午9:19
 * solr 产品结果根据客户分组 result DTO
 */
public class ProductSearchResultGroupListDTO {
  private List<ProductSearchResultListDTO> productSearchResultList = new ArrayList<ProductSearchResultListDTO>();
  private int numberGroups;
  private int totalNumberFound;

  public List<ProductSearchResultListDTO> getProductSearchResultList() {
    return productSearchResultList;
  }

  public void setProductSearchResultList(List<ProductSearchResultListDTO> productSearchResultList) {
    this.productSearchResultList = productSearchResultList;
  }

  public int getNumberGroups() {
    return numberGroups;
  }

  public void setNumberGroups(int numberGroups) {
    this.numberGroups = numberGroups;
  }

  public int getTotalNumberFound() {
    return totalNumberFound;
  }

  public void setTotalNumberFound(int totalNumberFound) {
    this.totalNumberFound = totalNumberFound;
  }
}
