package com.bcgogo.product.ProductCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类 查询结果封装类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-19
 * Time: 下午4:46
 * To change this template use File | Settings | File Templates.
 */
public class ProdCategorySearchResult {

  private List<ProductCategoryDTO> results = new ArrayList<ProductCategoryDTO>();
  private long totalRows = 0;
  private boolean success = true;


  public List<ProductCategoryDTO> getResults() {
    return results;
  }

  public void setResults(List<ProductCategoryDTO> results) {
    this.results = results;
  }

  public long getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(long totalRows) {
    this.totalRows = totalRows;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
