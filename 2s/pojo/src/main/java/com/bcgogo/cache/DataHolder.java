package com.bcgogo.cache;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.txn.dto.ProductHistoryDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.dto.StoreHouseDTO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-5-31
 * Time: 上午9:50
 * To change this template use File | Settings | File Templates.
 * 用于在controller 存放数据用于基础数据
 */
public class DataHolder {
  private Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap;
  private Map<Long, ProductDTO> productDTOMap;
  private Map<Long, ProductHistoryDTO> productHistoryDTOMap;
  private Map<Long, ServiceDTO> serviceDTOMap;
  private Map<Long, StoreHouseDTO> storeHouseDTOMap;

  public Map<Long, ProductLocalInfoDTO> getProductLocalInfoDTOMap() {
    return productLocalInfoDTOMap;
  }

  public void setProductLocalInfoDTOMap(Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap) {
    this.productLocalInfoDTOMap = productLocalInfoDTOMap;
  }

  public Map<Long, ProductDTO> getProductDTOMap() {
    return productDTOMap;
  }

  public void setProductDTOMap(Map<Long, ProductDTO> productDTOMap) {
    this.productDTOMap = productDTOMap;
  }

  public Map<Long, ProductHistoryDTO> getProductHistoryDTOMap() {
    return productHistoryDTOMap;
  }

  public void setProductHistoryDTOMap(Map<Long, ProductHistoryDTO> productHistoryDTOMap) {
    this.productHistoryDTOMap = productHistoryDTOMap;
  }

  public Map<Long, ServiceDTO> getServiceDTOMap() {
    return serviceDTOMap;
  }

  public void setServiceDTOMap(Map<Long, ServiceDTO> serviceDTOMap) {
    this.serviceDTOMap = serviceDTOMap;
  }

  public Map<Long, StoreHouseDTO> getStoreHouseDTOMap() {
    return storeHouseDTOMap;
  }

  public void setStoreHouseDTOMap(Map<Long, StoreHouseDTO> storeHouseDTOMap) {
    this.storeHouseDTOMap = storeHouseDTOMap;
  }
}
