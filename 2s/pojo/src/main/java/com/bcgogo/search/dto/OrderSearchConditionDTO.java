package com.bcgogo.search.dto;

import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/2/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderSearchConditionDTO {
  private static final Logger LOG = LoggerFactory.getLogger(OrderSearchConditionDTO.class);
  private static final Long ONE_DAY = 86400000L;

  public final static String PRODUCT_NAME = "product_name";
  public final static String PRODUCT_BRAND = "product_brand";

  //
  public final static String SEARCHSTRATEGY_MORE = "more";
  public final static String SEARCHSTRATEGY_PREV = "prev";
  public final static String SEARCHSTRATEGY_STATS = "stats";
  public final static String SEARCHSTRATEGY_STATS_FACET = "stats.facet";

  public final static String SEARCHSTRATEGY_NO_SHOP_RESTRICT = "no_shop_restrict";
  //当前页小计
  public final static String SEARCHSTRATEGY_CURRENT_PAGE_STATS = "page.stats";
  //查在线单据
  public final static String SEARCHSTRATEGY_ONLINE_ORDERS = "online_order";

  public final static String ORDER_IS_CANCEL = "YES";
  public final static String ORDER_NOT_CANCEL = "NO";

  public final static String SYSTEM_ORDER = "systemOrder";
  public final static String IMPORTED_ORDER = "importedOrder";

  public enum PreBuyOrderStatus {
    VALID("有效"),EXPIRED("过期");

    String name;

    public String getName() {
      return name;
    }

    private PreBuyOrderStatus(String name) {
      this.name = name;
    }
  }

  //通过比较uuid 来保证 商品建议和商品历史建议 的请求一致性
  private String uuid;

  private Long shopId;         //店面ID
  private ShopKind shopKind;
  private Long[] excludeShopIds;
  private Long orderId;        //单子ID
  private String[] orderType;    // 单据类型
  private String orderTypeStr;    // 字符串的单据类型
  private String orderStatusRepeal;     //  状态是否包含作废
  private String[] orderStatus;     //  状态
  //单据总额范围
  private Double amountLower;
  private Double amountUpper;
  private String vehicle;      // 车牌号
  private String[] vehicleList;
  private String customerOrSupplierName;   // 客户或供应商名字
  private String customerOrSupplierId;   // 客户或供应商id
  private String customerName;   // 客户名字
  private String customerId;   // 客户id
  private String customerInfo;   // 客户信息 （名字或车牌号）
  private String memberNo;//会员账号
  private String memberType;//会员类型
  private String payPerProject;//计次收费项目
  private String supplierName;   // 供应商名字
  private String supplierId;   // 供应商id
  private String service;
  private Boolean notPaid; //欠款；
  private Long paymentTimeStart;//还款时间
  private Long paymentTimeEnd;//还款时间
  private String contactNum;//联系方式

  private String mobile;
  private String contact; //联系人
  private String customerOrSupplierInfo;//客户或者供应商 名字，联系人，手机号关键字

  private String[] serviceWorker; //维修美容单中的施工人 或者洗车单中的洗车人
  private String[] salesman;
  private String operator;
  private Long operatorId;
  //分项统计
  private String[] itemTypes;
  private String businessCategory;
  private String businessCategoryId;
  private String productKind;
  private String productKindId;
  private String statType;
  private String[] productIds;
  private String[] supplierProductIds;    //在线采购单接受前，保存的是供应商productId
  private String[] serviceIds;
  private String[] customerOrSupplierIds;
  private String[] customerOrSupplierShopIds;
  private Boolean includeDisabledCustomer;

  private String searchWord;
  private String searchField;
  //商品
  private String commodityCode;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String productVehicleBrand;
  private String productVehicleModel;

  private String pvBrand;
  private String pvModel;
  private String vModel; // repair order's vehicle
  private String vBrand;
  private String vColor;
  private String[] payMethod;//支付方式
  private String couponType; //消费券类型

  private String consumeType;

  private String queryPageType;//用以区分是客户、供应商查询 还是 查询中心查询

  //日期
  private Long startTime;
  private String startTimeStr;
  private Long endTime;
  private String endTimeStr;
  private String sort;
  //分页
  private int rowStart;
  private int pageRows = 15;

  //在线退货前台传过来的pager分页  与单据查询中心不兼容
  private int maxRows = 15;
  private int startPageNo = 1;


  //stats.fields
  private String[] statsFields;
  private String[] facetFields;
  private String[] pageStatsFields;
  private String[] pageFacetFields;

  // 查询策略
  private String[] searchStrategy;
  //查询准确度
  private String searchAccuracy;

  private String searchType;

  private Boolean excludeOnlineOrder;     //排除在线单据

  //会员消费统计
  private Long[] memberIds;
  private List customerIds;
  private String accountMemberNo;

  private PreBuyOrderStatus preBuyOrderStatus;
  //对账单
  private String receiptNo;

  //应收应付统计
  private String debtType;
  private Long provinceNo;
  private Long cityNo;
  private Long regionNo;
  private Long province;   //客户的省份
  private Long city;       //客户的城市
  private Long region;    //客户的区

  private String customMatchPContent;
  private String customMatchPVContent;

  //导出时需要用到
  private Boolean vehicleConstructionPermission;
  private Boolean memberStoredValuePermission;
  private Boolean isWholesaler;

  private String inventoryVestStartDateStr; //目前只有采购单会用，采购单入库时间
  private Long inventoryVestStartDate; //目前只有采购单会用，采购单入库时间
  private String inventoryVestEndDateStr; //目前只有采购单会用，采购单入库时间
  private Long inventoryVestEndDate; //目前只有采购单会用，采购单入库时间

  private Long[] customerShopIds;    //在线销售单，新订单，客户的shop_id
  private String shopName;            //在线销售单，新订单，订单中的店铺名
  private Boolean exactSearch = false;       //在线销售单，新订单,客户名搜索时，是否是精确搜索
  private BusinessChanceType businessChanceType;

  public BusinessChanceType getBusinessChanceType() {
    return businessChanceType;
  }

  public void setBusinessChanceType(BusinessChanceType businessChanceType) {
    this.businessChanceType = businessChanceType;
  }

  public Boolean getExactSearch() {
    return exactSearch;
  }

  public void setExactSearch(Boolean exactSearch) {
    this.exactSearch = exactSearch;
  }

  public Long[] getCustomerShopIds() {
    return customerShopIds;
  }

  public void setCustomerShopIds(Long[] customerShopIds) {
    this.customerShopIds = customerShopIds;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Boolean getWholesaler() {
        return isWholesaler;
    }

    public void setWholesaler(Boolean wholesaler) {
        isWholesaler = wholesaler;
    }

    public Boolean getMemberStoredValuePermission() {
        return memberStoredValuePermission;
    }

    public void setMemberStoredValuePermission(Boolean memberStoredValuePermission) {
        this.memberStoredValuePermission = memberStoredValuePermission;
    }

    public Boolean getVehicleConstructionPermission() {

        return vehicleConstructionPermission;
    }

    public void setVehicleConstructionPermission(Boolean vehicleConstructionPermission) {
        this.vehicleConstructionPermission = vehicleConstructionPermission;
    }

    public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  public Long getCity() {

    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public Long getProvince() {

    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  //打通的那个客户或者供应商的Id
  private Long relatedCustomerOrSupplierId;

  private JoinSearchConditionDTO joinSearchConditionDTO;

  private int totalExportNum;

  private String statisticsInfo;   //用于导出单据时的excel头上面的统计信息

    public int getTotalExportNum() {
        return totalExportNum;
    }

    public void setTotalExportNum(int totalExportNum) {
        this.totalExportNum = totalExportNum;
    }

  public String getStatisticsInfo() {
    return statisticsInfo;
  }

  public void setStatisticsInfo(String statisticsInfo) {
    this.statisticsInfo = statisticsInfo;
  }

  public OrderSearchConditionDTO(){

  }
  public OrderSearchConditionDTO(ProductDTO productDTO) {
    this.setProductName(productDTO.getName());
    this.setProductBrand(productDTO.getBrand());
    this.setProductSpec(productDTO.getSpec());
    this.setProductModel(productDTO.getModel());
    this.setProductVehicleBrand(productDTO.getProductVehicleBrand());
    this.setProductVehicleModel(productDTO.getProductVehicleModel());
  }
  public JoinSearchConditionDTO getJoinSearchConditionDTO() {
    return joinSearchConditionDTO;
  }

  public void setJoinSearchConditionDTO(JoinSearchConditionDTO joinSearchConditionDTO) {
    this.joinSearchConditionDTO = joinSearchConditionDTO;
  }

  public Long getRelatedCustomerOrSupplierId() {
    return relatedCustomerOrSupplierId;
  }

  public void setRelatedCustomerOrSupplierId(Long relatedCustomerOrSupplierId) {
    this.relatedCustomerOrSupplierId = relatedCustomerOrSupplierId;
  }
  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public Long[] getExcludeShopIds() {
    return excludeShopIds;
  }

  public void setExcludeShopIds(Long[] excludeShopIds) {
    this.excludeShopIds = excludeShopIds;
  }

  public Long getProvinceNo() {
    return provinceNo;
  }

  public void setProvinceNo(Long provinceNo) {
    this.provinceNo = provinceNo;
  }

  public Long getCityNo() {
    return cityNo;
  }

  public void setCityNo(Long cityNo) {
    this.cityNo = cityNo;
  }

  public Long getRegionNo() {
    return regionNo;
  }

  public void setRegionNo(Long regionNo) {
    this.regionNo = regionNo;
  }

  public PreBuyOrderStatus getPreBuyOrderStatus() {
    return preBuyOrderStatus;
  }

  public void setPreBuyOrderStatus(PreBuyOrderStatus preBuyOrderStatus) {
    this.preBuyOrderStatus = preBuyOrderStatus;
  }

  public String getDebtType() {
    return debtType;
  }

  public void setDebtType(String debtType) {
    this.debtType = debtType;
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

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getSearchWord() {
    return searchWord;
  }

  public void setSearchWord(String searchWord) {
    this.searchWord = searchWord;
  }

  public String getSearchField() {
    return searchField;
  }

  public void setSearchField(String searchField) {
    this.searchField = searchField;
  }

  public List getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(List customerIds) {
    this.customerIds = customerIds;
  }

  public String getAccountMemberNo() {
    return accountMemberNo;
  }

  public void setAccountMemberNo(String accountMemberNo) {
    this.accountMemberNo = accountMemberNo;
  }

  public Long[] getMemberIds() {
    return memberIds;
  }

  public void setMemberIds(Long[] memberIds) {
    this.memberIds = memberIds;
  }

  public String getSearchAccuracy() {
    return searchAccuracy;
  }

  public void setSearchAccuracy(String searchAccuracy) {
    this.searchAccuracy = searchAccuracy;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public String getPayPerProject() {
    return payPerProject;
  }

  public void setPayPerProject(String payPerProject) {
    this.payPerProject = payPerProject;
  }

  public String[] getProductIds() {
    return productIds;
  }

  public void setProductIds(String[] productIds) {
    this.productIds = productIds;
  }

  public String[] getSupplierProductIds() {
    return supplierProductIds;
  }

  public void setSupplierProductIds(String[] supplierProductIds) {
    this.supplierProductIds = supplierProductIds;
  }

  public String getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(String businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public String getProductKindId() {
    return productKindId;
  }

  public void setProductKindId(String productKindId) {
    this.productKindId = productKindId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }


  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String[] getVehicleList() {
    return vehicleList;
  }

  public void setVehicleList(String[] vehicleList) {
    this.vehicleList = vehicleList;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.setCustomerOrSupplierName(customerName);
    this.customerName = customerName;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.setCustomerOrSupplierName(supplierName);
    this.supplierName = supplierName;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public String getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(String customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  public Long getPaymentTimeStart() {
    return paymentTimeStart;
  }

  public void setPaymentTimeStart(Long paymentTimeStart) {
    this.paymentTimeStart = paymentTimeStart;
  }

  public Long getPaymentTimeEnd() {
    return paymentTimeEnd;
  }

  public void setPaymentTimeEnd(Long paymentTimeEnd) {
    this.paymentTimeEnd = paymentTimeEnd;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getContactNum() {
    return contactNum;
  }

  public void setContactNum(String contactNum) {
    this.contactNum = contactNum;
  }

  public String[] getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String[] serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  public String[] getSalesman() {
    return salesman;
  }

  public void setSalesman(String[] salesman) {
    this.salesman = salesman;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
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

  public String getPvBrand() {
    return pvBrand;
  }

  public void setPvBrand(String pvBrand) {
    this.pvBrand = pvBrand;
  }

  public String getPvModel() {
    return pvModel;
  }

  public void setPvModel(String pvModel) {
    this.pvModel = pvModel;
  }

  public String getvModel() {
    return vModel;
  }

  public void setvModel(String vModel) {
    this.vModel = vModel;
  }

  public String getvBrand() {
    return vBrand;
  }

  public void setvBrand(String vBrand) {
    this.vBrand = vBrand;
  }

  public String[] getOrderType() {
    return orderType;
  }

  public void setOrderType(String[] orderType) {
    this.orderType = orderType;
  }

  public String getOrderStatusRepeal() {
    return orderStatusRepeal;
  }

  public void setOrderStatusRepeal(String orderStatusRepeal) {
    this.orderStatusRepeal = orderStatusRepeal;
  }

  public String[] getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String[] orderStatus) {
    this.orderStatus = orderStatus;
  }

  public Double getAmountLower() {
    return amountLower;
  }

  public void setAmountLower(Double amountLower) {
    this.amountLower = amountLower;
  }

  public Double getAmountUpper() {
    return amountUpper;
  }

  public void setAmountUpper(Double amountUpper) {
    this.amountUpper = amountUpper;
  }

  public String[] getPayMethod() {
    return payMethod;
  }

  public void setPayMethod(String[] payMethod) {
    this.payMethod = payMethod;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    LOG.debug(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, startTime));
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    LOG.debug(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, endTime));
    this.endTime = endTime;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) throws ParseException {
    if (StringUtils.isBlank(startTimeStr)) return;
    startTime = DateUtil.parseInquiryCenterDate(startTimeStr);
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    if (StringUtils.isBlank(endTimeStr)) return;
    endTime = DateUtil.parseInquiryCenterDate(endTimeStr);
    endTime += (24*3600*1000 - 1);   //结束日期应在当天末。
    this.endTimeStr = endTimeStr;
  }

  public String getInventoryVestStartDateStr() {
    return inventoryVestStartDateStr;
  }

  public void setInventoryVestStartDateStr(String inventoryVestStartDateStr) {
    this.inventoryVestStartDateStr = inventoryVestStartDateStr;
    if (StringUtils.isNotBlank(inventoryVestStartDateStr)){
      inventoryVestStartDate = DateUtil.parseInquiryCenterDate(inventoryVestStartDateStr);
    }else{
      inventoryVestStartDate = null;
    }
  }

  public Long getInventoryVestStartDate() {
    return inventoryVestStartDate;
  }

  public void setInventoryVestStartDate(Long inventoryVestStartDate) {
    this.inventoryVestStartDate = inventoryVestStartDate;
  }

  public String getInventoryVestEndDateStr() {
    return inventoryVestEndDateStr;
  }

  public void setInventoryVestEndDateStr(String inventoryVestEndDateStr) {
    this.inventoryVestEndDateStr = inventoryVestEndDateStr;
    if (StringUtils.isNotBlank(inventoryVestEndDateStr)){
      inventoryVestEndDate = DateUtil.parseInquiryCenterDate(inventoryVestEndDateStr);
      inventoryVestEndDate += (24*3600*1000 - 1);   //结束日期应在当天末。
    }else{
      inventoryVestEndDate = null;
    }
  }

  public Long getInventoryVestEndDate() {
    return inventoryVestEndDate;
  }

  public void setInventoryVestEndDate(Long inventoryVestEndDate) {
    this.inventoryVestEndDate = inventoryVestEndDate;
  }

  public Boolean getNotPaid() {
    return notPaid;
  }

  public void setNotPaid(Boolean notPaid) {
    this.notPaid = notPaid;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public void verificationQueryTime() throws Exception {

    if (this.startTime != null && this.endTime != null) {
      if (this.startTime > endTime) {
        Long temp = endTime;
        endTime = startTime;
        startTime = temp;
      }
      LOG.debug("query order time:" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", startTime) + "--" + DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", endTime));

    }
  }

  public int getRowStart() {
    return rowStart;
  }

  public void setRowStart(int rowStart) {
    this.rowStart = rowStart;
  }

  public int getPageRows() {
    return pageRows;
  }

  public void setPageRows(int pageRows) {
    this.pageRows = pageRows;
  }

  public String[] getSearchStrategy() {
    return searchStrategy;
  }

  public void setSearchStrategy(String[] searchStrategy) {
    this.searchStrategy = searchStrategy;
  }

  public String getBusinessCategory() {
    return businessCategory;
  }

  public void setBusinessCategory(String businessCategory) {
    this.businessCategory = businessCategory;
  }

  public String getProductKind() {
     return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }

  public String[] getFacetFields() {
    return facetFields;
  }

  public void setFacetFields(String[] facetFields) {
    this.facetFields = facetFields;
  }

  public String[] getStatsFields() {
    return statsFields;
  }

  public void setStatsFields(String[] statsFields) {
    this.statsFields = statsFields;
  }

  public String[] getPageStatsFields() {
    return pageStatsFields;
  }

  public void setPageStatsFields(String[] pageStatsFields) {
    this.pageStatsFields = pageStatsFields;
  }

  public String[] getPageFacetFields() {
    return pageFacetFields;
  }

  public void setPageFacetFields(String[] pageFacetFields) {
    this.pageFacetFields = pageFacetFields;
  }

  public String getStatType() {
    return statType;
  }

  public void setStatType(String statType) {
    this.statType = statType;
  }

  public String[] getItemTypes() {
    return itemTypes;
  }

  public void setItemTypes(String[] itemTypes) {
    this.itemTypes = itemTypes;
  }

  public String[] getCustomerOrSupplierIds() {
    return customerOrSupplierIds;
  }

  public void setCustomerOrSupplierIds(String[] customerOrSupplierIds) {
    this.customerOrSupplierIds = customerOrSupplierIds;
  }

  public Boolean getIncludeDisabledCustomer() {
    return includeDisabledCustomer;
  }

  public void setIncludeDisabledCustomer(Boolean includeDisabledCustomer) {
    this.includeDisabledCustomer = includeDisabledCustomer;
  }

  public String getSearchType() {
    return searchType;
  }

  public void setSearchType(String searchType) {
    this.searchType = searchType;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(String supplierId) {
    this.supplierId = supplierId;
  }

  public String[] getServiceIds() {
    return serviceIds;
  }

  public void setServiceIds(String[] serviceIds) {
    this.serviceIds = serviceIds;
  }

  public String getCustomerOrSupplierInfo() {
    return customerOrSupplierInfo;
  }

  public void setCustomerOrSupplierInfo(String customerOrSupplierInfo) {
    this.customerOrSupplierInfo = customerOrSupplierInfo;
  }

  public boolean gotoMemCacheFunction() {
    if ((searchField != null && StringUtils.isBlank(searchWord) && searchField.equals(PRODUCT_NAME))
        || (searchField != null && StringUtils.isBlank(searchWord) && searchField.equals(PRODUCT_BRAND) && StringUtils.isBlank(productName))
        ) {
      return true;
    }
    return false;
  }

  public boolean isEmptyOfProductInfo() {
    return StringUtil.isAllEmpty(commodityCode,productName, productBrand, productSpec, productModel, productVehicleBrand, productVehicleModel);
  }

  public void validateBeforeQuery() {
    if (StringUtils.isNotBlank(getCustomerOrSupplierName()) && "客户名".equals(getCustomerOrSupplierName())) {
      setCustomerOrSupplierName(null);
    }
    if (StringUtils.isNotBlank(getCustomerName()) && "客户名".equals(getCustomerName())) {
      setCustomerName(null);
    }
    if (StringUtils.isNotBlank(getSupplierName()) && "供应商名".equals(getSupplierName())) {
      setSupplierName(null);
    }
    if (StringUtils.isNotBlank(getCustomerOrSupplierName()) && "供应商名".equals(getCustomerOrSupplierName())) {
      setCustomerOrSupplierName(null);
    }
    if ((StringUtils.isNotBlank(getMobile()) && "手机号".equals(getMobile())) || StringUtil.isEmpty(getMobile())) {
      setMobile(null);
    }
    if ((StringUtils.isNotBlank(getBusinessCategory()) && "---所有商品分类---".equals(getBusinessCategory())) || StringUtil.isEmpty(getBusinessCategory())) {
      setBusinessCategory(null);
    }

    if (StringUtils.isEmpty(this.getEndTimeStr())) {
      this.setEndTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY));
    }
    if (this.getStartTime() == null) {
      try {
        this.setStartTime(DateUtil.getFirstDayDateTimeOfMonth());
      } catch (ParseException e) {
        this.setStartTime(System.currentTimeMillis());
      }
    }
    if (!StringUtils.isEmpty(this.getCustomerName())) {
      this.setCustomerName(this.getCustomerName().trim());
    }
    if (!StringUtils.isEmpty(this.getMobile())) {
      this.setMobile(this.getMobile().trim());
    }
    if (!StringUtils.isEmpty(this.getSupplierName())) {
      this.setSupplierName(this.getSupplierName().trim());
    }
    if (!StringUtils.isEmpty(this.getBusinessCategory())) {
      this.setBusinessCategory(this.getBusinessCategory().trim());
    }
  }

  public String[] getCustomerOrSupplierShopIds() {
    return customerOrSupplierShopIds;
  }

  public void setCustomerOrSupplierShopIds(String[] customerOrSupplierShopIds) {
    this.customerOrSupplierShopIds = customerOrSupplierShopIds;
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

  public String getCouponType() {
    return couponType;
  }

  public void setCouponType(String couponType) {
    this.couponType = couponType;
  }

  public String getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(String consumeType) {
    this.consumeType = consumeType;
  }

  public String getCustomMatchPContent() {
    return customMatchPContent;
  }

  public void setCustomMatchPContent(String customMatchPContent) {
    this.customMatchPContent = customMatchPContent;
  }

  public String getCustomMatchPVContent() {
    return customMatchPVContent;
  }

  public void setCustomMatchPVContent(String customMatchPVContent) {
    this.customMatchPVContent = customMatchPVContent;
  }

  public Boolean getExcludeOnlineOrder() {
    return excludeOnlineOrder;
  }

  public void setExcludeOnlineOrder(Boolean excludeOnlineOrder) {
    this.excludeOnlineOrder = excludeOnlineOrder;
  }

  public String generateCustomMatchPContent(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getProductName())){
      sb.append(this.getProductName()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductBrand())){
      sb.append(this.getProductBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductSpec())){
      sb.append(this.getProductSpec()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductModel())){
      sb.append(this.getProductModel()).append(" ");
    }
    return sb.toString().trim();
  }
  public String generateCustomMatchPVContent(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getProductVehicleBrand())){
      sb.append(this.getProductVehicleBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductVehicleModel())){
      sb.append(this.getProductVehicleModel()).append(" ");
    }
    return sb.toString().trim();
  }

  public String getvColor() {
    return vColor;
  }

  public void setvColor(String vColor) {
    this.vColor = vColor;
  }

  public String getQueryPageType() {
    return queryPageType;
  }

  public void setQueryPageType(String queryPageType) {
    this.queryPageType = queryPageType;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public String getCustomerInfo() {
    return customerInfo;
  }

  public void setCustomerInfo(String customerInfo) {
    this.customerInfo = customerInfo;
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }
}
