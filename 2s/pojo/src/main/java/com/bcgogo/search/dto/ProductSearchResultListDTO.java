package com.bcgogo.search.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.user.dto.SupplierDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-26
 * Time: 下午2:54
 * To change this template use File | Settings | File Templates.
 */
public class ProductSearchResultListDTO {
  private long numFound;          //total result
  private long inventoryCount;    //入库种类
  private double inventoryAmount;   //入库总数量
  private double totalPurchasePrice;   //入库总价值
  private long relatedCustomerShopId;  //相关联的客户的shop id
  private List<ProductDTO> products = new ArrayList<ProductDTO>();
  private Pager pager;


  public long getRelatedCustomerShopId() {
    return relatedCustomerShopId;
  }

  public void setRelatedCustomerShopId(long relatedCustomerShopId) {
    this.relatedCustomerShopId = relatedCustomerShopId;
  }

  public long getInventoryCount() {
    return inventoryCount;
  }

  public void setInventoryCount(long inventoryCount) {
    this.inventoryCount = inventoryCount;
  }

  public double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }

  public double getTotalPurchasePrice() {
    return totalPurchasePrice;
  }

  public void setTotalPurchasePrice(double totalPurchasePrice) {
    this.totalPurchasePrice = totalPurchasePrice;
  }

  public List<ProductDTO> getProducts() {
    return products;
  }

  public void setProducts(List<ProductDTO> products) {
    this.products = products;
  }

  public long getNumFound() {
    return numFound;
  }

  public void setNumFound(long numFound) {
    this.numFound = numFound;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public List<Long> getProductLocalInfoIdList() {
    List<Long> result = new ArrayList<Long>();
    if(CollectionUtils.isNotEmpty(this.getProducts())){
      for(ProductDTO productDTO : this.getProducts()){
        result.add(productDTO.getProductLocalInfoId());
      }
    }
    return result;
  }
}
