package com.bcgogo.search.dto;

import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.ProductAdStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.BcgogoOrderItemDto;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 12-5-11
 * Time: 上午9:15
 * 搜索条件DTO
 */
public class SearchConditionDTO {
  public final static String COMMODITY_CODE = "commodity_code";
  public final static String PRODUCT_INFO = "product_info";
  public final static String PRODUCT_NAME = "product_name";
  public final static String PRODUCT_BRAND = "product_brand";
  public final static String PRODUCT_SPEC = "product_spec";
  public final static String PRODUCT_MODEL = "product_model";
  public final static String PRODUCT_VEHICLE_BRAND = "product_vehicle_brand";
  public final static String PRODUCT_VEHICLE_MODEL = "product_vehicle_model";
  //
  public final static String SEARCHSTRATEGY_DETAIL = "detail";
  public final static String SEARCHSTRATEGY_SUGGESTION = "suggestion";
  public final static String SEARCHSTRATEGY_NO_SHOP_RESTRICT = "no_shop_restrict";
  public final static String SEARCHSTRATEGY_NORMAL_PRODUCT= "normalProduct";

  public final static String SEARCHSTRATEGY_STATS = "stats";

  //通过比较uuid 来保证 商品建议和商品历史建议 的请求一致性
  private String uuid;
  //
  private Long shopId;
  private Long orderId;
  private ShopKind shopKind;
  private ProductStatus productStatus;
  private Long[] shopIds;
  private Long[] orderIds;
  private Long provinceNo;
  private Long cityNo;
  private Long regionNo;

  private Long[] excludeShopIds;
  private String searchWord;
  private String searchField;
  private String[] searchFields;
  private String[] searchWords;
  private ProductDTO[] productDTOs;
  //
  private String orderType;
  private ProductCategoryDTO [] productCategoryDTOs;
  // product
  private Long productId;
  private String productIds;
  private String[] productIdArr;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String productVehicleModel;
  private String productVehicleBrand;
  private String commodityCode;//商品编码
  private String productKind;//商品分类
  private String productInfo;
  //vehicle
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleYear;
  private String vehicleEngine;

  private boolean includeBasic = true; //标准库
  private String sort;                    //排序规则

  private int start;
  private int rows = 15;//默认15

  //ajaxPaging.tag 对应的接口
  private int startPageNo=1;
  private int maxRows = 15;//默认15

  private String sortStatus;
  // 查询策略
  private String[] searchStrategy; //normalProduct 标准化场景

  //stats.fields
  private String[] statsFields;


  private String supplierKeyWord;    //产品供应商的key
  private String supplierId;    //供应商Id查找

  private String supplierName;    //供应商Id查找

  private String wholesalerId;//批发商id
  private String relatedCustomerId;
  private String wholesalerName;
  private String relatedCustomerName;
  private String[] relatedCustomerIds;
  private String[] relatedCustomerShopIds;
  private Long wholesalerShopId;//批发商shopId
  private Long[] wholesalerShopIds;//批发商shopId

  private ProductAdStatus adStatus;

  private Integer inventoryAmountUp;
  private Integer inventoryAmountDown;

  private Double inventoryAveragePriceUp;
  private Double inventoryAveragePriceDown;

  private Double tradePriceStart;
  private Double tradePriceEnd;

  private Double recommendedPriceStart;
  private Double recommendedPriceEnd;

  private Double inSalesPriceStart;
  private Double inSalesPriceEnd;


  private Boolean hasInventoryFlag;
  private Long normalProductId;
  private int page;
  private int limit;
  private String relevanceStatus; //（所有状态，已标准化，未标准化）
  private String[] relevanceStatuses;
  private String promotionsFilter;
  private PromotionsEnum.PromotionStatus[] promotionStatusList;
  private String shopName;
  private String fromSource;

  private Long storehouseId;
  private Boolean showAllStorehouseProducts = false;

  private ProductStatus salesStatus;
  private Long startLastInSalesTime;
  private Long endLastInSalesTime;
  private InventoryAlarm inventoryAlarm;

  private String customMatchPContent;
  private String customMatchPVContent;
  //促销信息
  private Long promotionsId;
  private String promotionsType;
  private String promotionsName;
  private PromotionsEnum.PromotionsRanges range;
  private String[] promotionsTypeList;
  private String[] promotionsTypeStatusList;
  private String[] promotionsIdList;
  private List<Long> overlappingProductIds;

//  private String productCategoryIds;   //上架商品分类
  private String[] productCategoryIds;

  private JoinSearchConditionDTO joinSearchConditionDTO;
  private Boolean mustQuerySolr;
  private int totalRows;   //导出的总行数


  //CRM->店铺业务统计-> 当前店铺采购统计
  private Long provinceId;
  private Long cityId;
  private Long regionId;
  private String shopVersion;
  private NormalProductStatType normalProductStatType;


  public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public SearchConditionDTO() {

  }
  public SearchConditionDTO(OrderItemSearchResultDTO orderItemSearchResultDTO) {
    this.setProductName(orderItemSearchResultDTO.getProductName());
    this.setProductBrand(orderItemSearchResultDTO.getProductBrand());
    this.setProductSpec(orderItemSearchResultDTO.getProductSpec());
    this.setProductModel(orderItemSearchResultDTO.getProductModel());
    this.setProductVehicleBrand(orderItemSearchResultDTO.getProductVehicleBrand());
    this.setProductVehicleModel(orderItemSearchResultDTO.getProductVehicleModel());
  }

  public SearchConditionDTO(BcgogoOrderItemDto bcgogoOrderItemDto) {
    this.setProductName(bcgogoOrderItemDto.getProductName());
    this.setProductBrand(bcgogoOrderItemDto.getBrand());
    this.setProductSpec(bcgogoOrderItemDto.getSpec());
    this.setProductModel(bcgogoOrderItemDto.getModel());
    this.setProductVehicleBrand(bcgogoOrderItemDto.getVehicleBrand());
    this.setProductVehicleModel(bcgogoOrderItemDto.getVehicleModel());
  }
  public SearchConditionDTO(ProductDTO productDTO) {
    this.setProductName(productDTO.getName());
    this.setProductBrand(productDTO.getBrand());
    this.setProductSpec(productDTO.getSpec());
    this.setProductModel(productDTO.getModel());
    this.setProductVehicleBrand(productDTO.getProductVehicleBrand());
    this.setProductVehicleModel(productDTO.getProductVehicleModel());
  }

  public String[] getRelevanceStatuses() {
    return relevanceStatuses;
  }

  public void setRelevanceStatuses(String[] relevanceStatuses) {
    this.relevanceStatuses = relevanceStatuses;
  }

  public String getCustomMatchPVContent() {
    return customMatchPVContent;
  }

  public void setCustomMatchPVContent(String customMatchPVContent) {
    this.customMatchPVContent = customMatchPVContent;
  }

  public String getCustomMatchPContent() {
    return customMatchPContent;
  }

  public void setCustomMatchPContent(String customMatchPContent) {
    this.customMatchPContent = customMatchPContent;
  }

  public Double getInSalesPriceEnd() {
    return inSalesPriceEnd;
  }

  public void setInSalesPriceEnd(Double inSalesPriceEnd) {
    this.inSalesPriceEnd = inSalesPriceEnd;
  }

  public Double getInSalesPriceStart() {
    return inSalesPriceStart;
  }

  public void setInSalesPriceStart(Double inSalesPriceStart) {
    this.inSalesPriceStart = inSalesPriceStart;
  }

  public Double getTradePriceStart() {
    return tradePriceStart;
  }

  public void setTradePriceStart(Double tradePriceStart) {
    this.tradePriceStart = tradePriceStart;
  }

  public Double getTradePriceEnd() {
    return tradePriceEnd;
  }

  public void setTradePriceEnd(Double tradePriceEnd) {
    this.tradePriceEnd = tradePriceEnd;
  }

  public Double getRecommendedPriceStart() {
    return recommendedPriceStart;
  }

  public void setRecommendedPriceStart(Double recommendedPriceStart) {
    this.recommendedPriceStart = recommendedPriceStart;
  }

  public Double getRecommendedPriceEnd() {
    return recommendedPriceEnd;
  }

  public void setRecommendedPriceEnd(Double recommendedPriceEnd) {
    this.recommendedPriceEnd = recommendedPriceEnd;
  }

  public Long getEndLastInSalesTime() {
    return endLastInSalesTime;
  }

  public void setEndLastInSalesTime(Long endLastInSalesTime) {
    this.endLastInSalesTime = endLastInSalesTime;
  }

  public Long getStartLastInSalesTime() {
    return startLastInSalesTime;
  }

  public void setStartLastInSalesTime(Long startLastInSalesTime) {
    this.startLastInSalesTime = startLastInSalesTime;
  }

  public ProductStatus getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(ProductStatus productStatus) {
    this.productStatus = productStatus;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long[] getOrderIds() {
    return orderIds;
  }

  public void setOrderIds(Long[] orderIds) {
    this.orderIds = orderIds;
  }

  public JoinSearchConditionDTO getJoinSearchConditionDTO() {
    return joinSearchConditionDTO;
  }

  public void setJoinSearchConditionDTO(JoinSearchConditionDTO joinSearchConditionDTO) {
    this.joinSearchConditionDTO = joinSearchConditionDTO;
  }

  public InventoryAlarm getInventoryAlarm() {
    return inventoryAlarm;
  }

  public void setInventoryAlarm(InventoryAlarm inventoryAlarm) {
    this.inventoryAlarm = inventoryAlarm;
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

  public String[] getStatsFields() {
    return statsFields;
  }

  public void setStatsFields(String[] statsFields) {
    this.statsFields = statsFields;
  }

  public Long[] getExcludeShopIds() {
    return excludeShopIds;
  }

  public void setExcludeShopIds(Long[] excludeShopIds) {
    this.excludeShopIds = excludeShopIds;
  }

  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }

  //下拉框建议 （商品&&为空）||（品牌&&为空&&商品不为空）|| （车辆品牌&&为空）
  public boolean gotoMemCacheFunction() {
    if(getMustQuerySolr() != null && getMustQuerySolr()){
      return false;
    }
    if(this.getShopId()==null){
      return false;
    }
    if (((searchField != null && StringUtils.isBlank(searchWord) && searchField.equals(PRODUCT_NAME))
      || (searchField != null && StringUtils.isBlank(searchWord) && searchField.equals(PRODUCT_INFO))
      || (searchField != null && StringUtils.isBlank(searchWord) && searchField.equals(PRODUCT_BRAND) && StringUtils.isBlank(productName))
    ) && (this.getSearchStrategy()==null || (!Arrays.asList(getSearchStrategy()).contains(SEARCHSTRATEGY_STATS) && !Arrays.asList(getSearchStrategy()).contains(SEARCHSTRATEGY_NO_SHOP_RESTRICT)))) {
      return true;
    }
    return false;
  }

  public boolean searchFieldEquals(String... strings) {
    boolean flag = false;
    if (strings == null || strings.length == 0) return flag;
    for (String str : strings) {
      if (searchField.equals(str)) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  public String getRelevanceStatus() {
    return relevanceStatus;
  }

  public void setRelevanceStatus(String relevanceStatus) {
    this.relevanceStatus = relevanceStatus;
  }

  public String getPromotionsFilter() {
    return promotionsFilter;
  }

  public void setPromotionsFilter(String promotionsFilter) {
    this.promotionsFilter = promotionsFilter;
  }

  public PromotionsEnum.PromotionStatus[] getPromotionStatusList() {
    return promotionStatusList;
  }

  public void setPromotionStatusList(PromotionsEnum.PromotionStatus[] promotionStatusList) {
    this.promotionStatusList = promotionStatusList;
  }

  public ProductDTO[] getProductDTOs() {
    return productDTOs;
  }

  public void setProductDTOs(ProductDTO[] productDTOs) {
    this.productDTOs = productDTOs;
  }

  public boolean isEmptyOfProductInfo() {
    return StringUtil.isAllEmpty(productName, productBrand, productSpec, productModel, productVehicleBrand, productVehicleModel,commodityCode);
  }

  public Boolean getHasInventoryFlag() {
    return hasInventoryFlag;
  }

  public void setHasInventoryFlag(Boolean hasInventoryFlag) {
    this.hasInventoryFlag = hasInventoryFlag;
  }

  public String getWholesalerId() {
    return wholesalerId;
  }

  public void setWholesalerId(String wholesalerId) {
    this.wholesalerId = wholesalerId;
  }

  public String getRelatedCustomerId() {
    return relatedCustomerId;
  }

  public void setRelatedCustomerId(String relatedCustomerId) {
    this.relatedCustomerId = relatedCustomerId;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getSearchWord() {
    if (this.searchWord == null) {
      this.searchWord = "";
    }
    return searchWord;
  }

  public void setSearchWord(String searchWord) {
    this.searchWord = searchWord;
  }

  public String getProductIds() {
    return productIds;
  }

  public void setProductIds(String productIds) {
    this.productIds = productIds;
  }

  public String[] getProductIdArr() {
    return productIdArr;
  }

  public void setProductIdArr(String[] productIdArr) {
    this.productIdArr = productIdArr;
  }

  public String getSearchField() {
    return searchField;
  }

  public void setSearchField(String searchField) {
    this.searchField = searchField;
  }

  public String[] getSearchWords() {
    return searchWords;
  }

  public void setSearchWords(String[] searchWords) {
    this.searchWords = searchWords;
  }

  public String getProductName() {
    if (productName == null) {
      productName = "";
    }
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    if (productBrand == null) {
      productBrand = "";
    }
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
  }

  public String getProductSpec() {
    if (productSpec == null) {
      productSpec = "";
    }
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getProductModel() {
    if (productModel == null) {
      productModel = "";
    }
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public boolean getIncludeBasic() {
    return includeBasic;
  }

  public void setIncludeBasic(boolean includeBasic) {
    this.includeBasic = includeBasic;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  public String[] getSearchFields() {
    return searchFields;
  }

  public void setSearchFields(String[] searchFields) {
    this.searchFields = searchFields;
  }

  public String[] getSearchStrategy() {
    return searchStrategy;
  }

  public void setSearchStrategy(String[] searchStrategy) {
    this.searchStrategy = searchStrategy;
  }

  public String test(String searchStrategy) {
    return searchStrategy;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.start = (startPageNo - 1) * maxRows;
    this.startPageNo = startPageNo;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.rows = maxRows;
    this.maxRows = maxRows;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getSupplierKeyWord() {
    return supplierKeyWord;
  }

  public void setSupplierKeyWord(String supplierKeyWord) {
    this.supplierKeyWord = supplierKeyWord;
  }

  public String getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(String supplierId) {
    this.supplierId = supplierId;
  }

  public String getProductInfo() {
    return productInfo;
  }

  public void setProductInfo(String productInfo) {
    this.productInfo = productInfo;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public ProductCategoryDTO[] getProductCategoryDTOs() {
    return productCategoryDTOs;
  }

  public void setProductCategoryDTOs(ProductCategoryDTO[] productCategoryDTOs) {
    this.productCategoryDTOs = productCategoryDTOs;
  }

  public String getWholesalerName() {
    return wholesalerName;
  }

  public void setWholesalerName(String wholesalerName) {
    this.wholesalerName = wholesalerName;
  }

  public String getRelatedCustomerName() {
    return relatedCustomerName;
  }

  public void setRelatedCustomerName(String relatedCustomerName) {
    this.relatedCustomerName = relatedCustomerName;
  }

  public String[] getRelatedCustomerIds() {
    return relatedCustomerIds;
  }

  public void setRelatedCustomerIds(String[] relatedCustomerIds) {
    this.relatedCustomerIds = relatedCustomerIds;
  }

  public Long getWholesalerShopId() {
    return wholesalerShopId;
  }

  public void setWholesalerShopId(Long wholesalerShopId) {
    this.wholesalerShopId = wholesalerShopId;
  }

  public Long[] getWholesalerShopIds() {
    return wholesalerShopIds;
  }

  public void setWholesalerShopIds(Long[] wholesalerShopIds) {
    this.wholesalerShopIds = wholesalerShopIds;
  }

  public ProductAdStatus getAdStatus() {
    return adStatus;
  }

  public void setAdStatus(ProductAdStatus adStatus) {
    this.adStatus = adStatus;
  }

  public Integer getInventoryAmountUp() {
    return inventoryAmountUp;
  }

  public void setInventoryAmountUp(Integer inventoryAmountUp) {
    this.inventoryAmountUp = inventoryAmountUp;
  }

  public Integer getInventoryAmountDown() {
    return inventoryAmountDown;
  }

  public void setInventoryAmountDown(Integer inventoryAmountDown) {
    this.inventoryAmountDown = inventoryAmountDown;
  }

  public Double getInventoryAveragePriceUp() {
    return inventoryAveragePriceUp;
  }

  public void setInventoryAveragePriceUp(Double inventoryAveragePriceUp) {
    this.inventoryAveragePriceUp = inventoryAveragePriceUp;
  }

  public Double getInventoryAveragePriceDown() {
    return inventoryAveragePriceDown;
  }

  public void setInventoryAveragePriceDown(Double inventoryAveragePriceDown) {
    this.inventoryAveragePriceDown = inventoryAveragePriceDown;
  }

  public String[] getRelatedCustomerShopIds() {
    return relatedCustomerShopIds;
  }

  public void setRelatedCustomerShopIds(String[] relatedCustomerShopIds) {
    this.relatedCustomerShopIds = relatedCustomerShopIds;
  }

  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Boolean getMustQuerySolr() {
    return mustQuerySolr;
  }

  public void setMustQuerySolr(Boolean mustQuerySolr) {
    this.mustQuerySolr = mustQuerySolr;
  }

  public void generateSearchConditionDTO(SearchConditionDTO searchConditionDTO, SearchSuggestionDTO firstResult) throws CloneNotSupportedException {
    this.setProductBrand(searchConditionDTO.getProductBrand());
    this.setProductSpec(searchConditionDTO.getProductSpec());
    this.setProductVehicleModel(searchConditionDTO.getProductVehicleModel());
    this.setProductModel(searchConditionDTO.getProductModel());
    this.setProductName(searchConditionDTO.getProductName());
    this.setProductVehicleBrand(searchConditionDTO.getProductVehicleBrand());
    this.setShopId(searchConditionDTO.getShopId());
    this.setRows(searchConditionDTO.getRows());

    if (firstResult != null && CollectionUtils.isNotEmpty(firstResult.suggestionEntry)) {
      for (String[] str : firstResult.suggestionEntry) {
        if (SearchConditionDTO.PRODUCT_NAME.equals(str[0])) {
          this.setProductName(str[1]);
        } else if (SearchConditionDTO.PRODUCT_BRAND.equals(str[0])) {
          this.setProductBrand(str[1]);
        } else if (SearchConditionDTO.PRODUCT_MODEL.equals(str[0])) {
          this.setProductModel(str[1]);
        } else if (SearchConditionDTO.PRODUCT_SPEC.equals(str[0])) {
          this.setProductSpec(str[1]);
        } else if (SearchConditionDTO.PRODUCT_VEHICLE_BRAND.equals(str[0])) {
          this.setProductVehicleBrand(str[1]);
        } else if (SearchConditionDTO.PRODUCT_VEHICLE_MODEL.equals(str[0])) {
          this.setProductVehicleModel(str[1]);
        }
      }
    }
  }

  @Override
  public String toString() {
    return "SearchConditionDTO{" +
      "shopId=" + shopId +
      ", productId=" + productId +
      ", searchWord='" + searchWord + '\'' +
      ", searchField='" + searchField + '\'' +
      ", searchFields=" + (searchFields == null ? null : Arrays.asList(searchFields)) +
      ", searchWords=" + (searchWords == null ? null : Arrays.asList(searchWords)) +
      ", productName='" + productName + '\'' +
      ", productBrand='" + productBrand + '\'' +
      ", productSpec='" + productSpec + '\'' +
      ", productModel='" + productModel + '\'' +
      ", vehicleBrand='" + vehicleBrand + '\'' +
      ", vehicleModel='" + vehicleModel + '\'' +
      ", vehicleYear='" + vehicleYear + '\'' +
      ", vehicleEngine='" + vehicleEngine + '\'' +
      ", includeBasic=" + includeBasic +
      ", sort='" + sort + '\'' +
      ", start=" + start +
      ", rows=" + rows +
      ", searchStrategy='" + ArrayUtil.toString(searchStrategy) + '\'' +
      '}';
  }

  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  public String getFromSource() {
    return fromSource;
  }

  public void setFromSource(String fromSource) {
    this.fromSource = fromSource;
  }

  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  public String getPromotionsType() {
    return promotionsType;
  }

  public void setPromotionsType(String promotionsType) {
    this.promotionsType = promotionsType;
  }

  public String getPromotionsName() {
    return promotionsName;
  }

  public void setPromotionsName(String promotionsName) {
    this.promotionsName = promotionsName;
  }

  public PromotionsEnum.PromotionsRanges getRange() {
    return range;
  }

  public void setRange(PromotionsEnum.PromotionsRanges range) {
    this.range = range;
  }

  public String[] getPromotionsTypeList() {
    return promotionsTypeList;
  }

  public void setPromotionsTypeList(String[] promotionsTypeList) {
    this.promotionsTypeList = promotionsTypeList;
  }

  public String[] getPromotionsTypeStatusList() {
    return promotionsTypeStatusList;
  }

  public void setPromotionsTypeStatusList(String[] promotionsTypeStatusList) {
    this.promotionsTypeStatusList = promotionsTypeStatusList;
  }

  public String[] getPromotionsIdList() {
    return promotionsIdList;
  }

  public void setPromotionsIdList(String[] promotionsIdList) {
    this.promotionsIdList = promotionsIdList;
  }

  public List<Long> getOverlappingProductIds() {
    return overlappingProductIds;
  }

  public void setOverlappingProductIds(List<Long> overlappingProductIds) {
    this.overlappingProductIds = overlappingProductIds;
  }

  public ProductStatus getSalesStatus() {
    return salesStatus;
  }

  public void setSalesStatus(ProductStatus salesStatus) {
    this.salesStatus = salesStatus;
  }

  public Long[] getShopIds() {
    return shopIds;
  }

  public void setShopIds(Long[] shopIds) {
    this.shopIds = shopIds;
  }

  public Boolean getShowAllStorehouseProducts() {
    return showAllStorehouseProducts;
  }

  public void setShowAllStorehouseProducts(Boolean showAllStorehouseProducts) {
    this.showAllStorehouseProducts = showAllStorehouseProducts;
  }

  public String[] getProductCategoryIds() {
    return productCategoryIds;
  }

  public void setProductCategoryIds(String[] productCategoryIds) {
    this.productCategoryIds = productCategoryIds;
  }

  public enum InventoryAlarm {
    UPPER_LIMIT("upperLimit"), LOWER_LIMIT("lowerLimit");

    String value;

    public String getValue() {
      return value;
    }

    private InventoryAlarm(String value) {
      this.value = value;
    }
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

  public Long getProvinceId() {
    return provinceId;
  }

  public void setProvinceId(Long provinceId) {
    this.provinceId = provinceId;
  }

  public Long getCityId() {
    return cityId;
  }

  public void setCityId(Long cityId) {
    this.cityId = cityId;
  }

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public String getShopVersion() {
    return shopVersion;
  }

  public void setShopVersion(String shopVersion) {
    this.shopVersion = shopVersion;
  }

  public NormalProductStatType getNormalProductStatType() {
    return normalProductStatType;
  }

  public void setNormalProductStatType(NormalProductStatType normalProductStatType) {
    this.normalProductStatType = normalProductStatType;
  }
}
