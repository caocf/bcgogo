package com.bcgogo.search.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-4-18
 * Time: 下午1:39
 * To change this template use File | Settings | File Templates.
 */
public class ProductThroughSearchDTO {
  private static final Logger LOG = LoggerFactory.getLogger(ProductThroughSearchDTO.class);
  private static final Long ONE_DAY = 86400000L;

  private Long shopId;
  //日期
  private Long startDate;
  private String startDateStr;
  private Long endDate;
  private String endDateStr;

  //仓库
  private Long[] storeHouses;

  private String[] orderType;    // 单据类型
  private OrderStatus[] orderStatus;

  //客户或者供应商
  private String customerName;   // 客户或供应商名字
  private String customerId;   // 客户或供应商id
  private String supplierName;   // 客户或供应商名字
  private String supplierId;   // 客户或供应商id

  //商品相关信息
  private String productId;
  private String commodityCode;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String productVehicleBrand;
  private String productVehicleModel;

  private String[] storehouseIds;

  private String[] itemType;

  //pager分页
  private int maxRows = 15;
  private int startPageNo = 1;
  private String sort;                    //排序规则

    private String[] statsFields;//计算范围
    private String[] pageStatsFields;
    // 查询策略
    private String[] searchStrategy;
    private String[] facetFields;

  //商品分类
  private String productKind;//按商品分类
  private String productKindId;
  private String[] productIds;


    public String[] getPageStatsFields() {
        return pageStatsFields;
    }

    public void setPageStatsFields(String[] pageStatsFields) {
        this.pageStatsFields = pageStatsFields;
    }

    public String[] getStatsFields() {
        return statsFields;
    }

    public void setStatsFields(String[] statsFields) {
        this.statsFields = statsFields;
    }

    public String[] getSearchStrategy() {
        return searchStrategy;
    }

    public void setSearchStrategy(String[] searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public String[] getFacetFields() {
        return facetFields;
    }

    public void setFacetFields(String[] facetFields) {
        this.facetFields = facetFields;
    }

    public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    if (StringUtils.isBlank(startDateStr)) return;
    startDate = DateUtil.parseInquiryCenterDate(startDateStr);
    this.startDateStr = startDateStr;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    if (StringUtils.isBlank(endDateStr)) return;
    endDate = DateUtil.parseInquiryCenterDate(endDateStr);
    this.endDateStr = endDateStr;
  }

  public Long[] getStoreHouses() {
    return storeHouses;
  }

  public void setStoreHouses(Long[] storeHouses) {
    this.storeHouses = storeHouses;
  }

  public String[] getOrderType() {
    return orderType;
  }

  public void setOrderType(String[] orderType) {
    this.orderType = orderType;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(String supplierId) {
    this.supplierId = supplierId;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public OrderStatus[] getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus[] orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }
  public void verificationQueryTime() throws Exception {
    if (this.startDate != null && this.endDate != null) {
      if (this.startDate > endDate) {
        Long temp = endDate;
        endDate = startDate;
        startDate = temp;
      }
      endDate += ONE_DAY - 1000;
      LOG.debug("query order time:" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", startDate) + "--" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", startDate));
    }
  }
  public void setDefaultInfo() {
    try {
      if (startDateStr == null) {
        this.setStartDate(DateUtil.getFirstDayDateTimeOfMonth());
      } else {
        this.setStartDate(DateUtil.getStartTimeOfDate(startDateStr));
      }

      if (endDateStr == null) {
        this.setEndDate(DateUtil.getNextMonthTime(System.currentTimeMillis()));
      } else {
        this.setStartDate(DateUtil.getEndTimeOfDate(endDateStr));
      }
    } catch (ParseException e) {
      LOG.error(e.getMessage(), e);
    }

  }

  public String[] getStorehouseIds() {
    return storehouseIds;
  }

  public void setStorehouseIds(String[] storehouseIds) {
    this.storehouseIds = storehouseIds;
  }

  public String[] getItemType() {
    return itemType;
  }

  public void setItemType(String[] itemType) {
    this.itemType = itemType;
  }

  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }

  public String[] getProductIds() {
    return productIds;
  }

  public void setProductIds(String[] productIds) {
    this.productIds = productIds;
  }

  public String getProductKindId() {
    return productKindId;
  }

  public void setProductKindId(String productKindId) {
    this.productKindId = productKindId;
  }

}
