package com.bcgogo.product.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.NormalProductModifyRecordDTO;
import com.bcgogo.product.NormalProductModifyScene;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.model.*;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.standardVehicleBrandModel.NormalProductVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.VehicleDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * To change this template use File | Settings | File Templates.
 */
public interface IProductService {

  public KindDTO createKind(KindDTO kindDTO);

  public BrandDTO getBrand(Long brandId);

  public BrandDTO createBrand(BrandDTO brandDTO);

  public ModelDTO createModel(ModelDTO modelDTO);

  public ModelDTO getModel(Long modelId);

  public Product getProductById(Long productId);

  List<ProductLocalInfoDTO> getShopAdProductLocalInfoDTO(Long... shopIds);

  List<ProductLocalInfoDTO> getLastInSalesProductLocalInfo(Long shopId,int maxRows);

  public List<ProductDTO> getSimpleProductDTOListById(Long... id);

  public ProductDTO createProduct(ProductDTO productDTO);

  public ProductDTO getProductByProductLocalInfoId(Long productLocalInfoId, Long shopId) throws Exception;

  List<ProductDTO> getProductDTOByProductLocalInfoIds(Long shopId,Long... productLocalInfoId) throws Exception;

  List<ProductDTO> getProductDTOByIds(Long... productLocalInfoIds);

  Map<Long,ProductDTO> getProductDTOMapByProductLocalInfoIds(Long shopId,Long... productLocalInfoId) throws Exception;

  public List<ProductDTO> getProductDTOByRelationSupplier(Long shopId) throws Exception;

  public ProductDTO getProductById(Long productId, Long shopId) throws Exception;

  public ProductVehicleDTO createProductVehicle(ProductVehicleDTO productVehicleDTO);

  public ProductVehicleDTO getProductVehicle(Long productVehicleId);

  public TemplateDTO createTemplate(TemplateDTO templateDTO);

  public TemplateDTO getTemplate(Long templateId);

  public EngineDTO getEngine(Long engineId);

  public YearDTO createYear(YearDTO yearDTO);

  public YearDTO getYear(Long yearId);

  public <T> String getJsonWithList(List<T> list);

  public List getModelWithFirstLetter(String firstLetter, Long brandId);

  public List getBrandWithFirstLetter(String firstLetter);

  public void updateAllVehicleFirstLetter();

  public List getBrandByKeyword(String searchWord);

  public String getProductFirstLetterByWord(String chnChar);

  public List<ProductVehicleDTO> getProductVehicleByProductIdReturnList(Long productId);

  public BrandDTO getBrandByName(String name);

  public ModelDTO getModelByName(Long brandId, String name);

  public YearDTO getYearByNameAndOtherId(Integer year, Long modelId, Long brandId);

  public EngineDTO getEngineByName(String engine, Long yearId, Long modelId, Long brandId);

  public void readFormFile(Long shopId, String name, String readType);

  public void readFormFile(InputStream fis, String readType);

  public ProductLocalInfoDTO getProductLocalInfoById(Long productLocalInfoId, Long shopId);

  public ProductLocalInfoDTO getProductLocalInfoByProductId(Long productId, Long shopId) throws Exception;

  //根据shopid查找地区名称
  public String getAreaByShopId(Long shopId);

  // 根据地区ID 找到地区名称
  public String getCarNoByareaById(String areaid);

  //根据地区名称查找车牌
  public String getCarNoByAreaName(String area);

  public String getCarNoByAreaNo(Long areaNo);

  //根据车牌首汉字查找车牌

  public List getCarNosByFirstLetters(String carnoFirstLetter);

  //根据车牌的首英文字母查询

  public List getCarNosByAreaFirstLetters(String areaFirstLetter);
  // 根据车牌查找车牌号

  public List getCarsByCarNos(String carNo, Long shopId);

  //反向本地模糊查询
  public List getCarsByCarNosReverse(String carNoValue, String area, Long shopId);

  public boolean saveNewProduct(ProductDTO newProductDTO) throws Exception;

  public void addVehicleToProduct(Long shopId, ProductDTO productDTO) throws Exception;

  //测试车牌前缀
  public LicenseplateDTO caeateLicenseplateDTO(LicenseplateDTO licenseplateDTO);

  //根据获取车牌前缀
  public LicenseplateDTO getLicenseplate(Long lplateId);

  //后台产品管理ProductAdmin--------

  public List<ProductAdminDTO> getProductAdminList(int pageNo, int pageSize);

  public Integer getInventoryIndexPageNo(HttpServletRequest request, String pageStatus, String pageName);

  //根据首汉字找到对应的字母
  public List<ChnfirstletterDTO> getFirstLetterFromChnFirstLetter(String hanzi);

  public Long[] getVehicleIds(String brand, String model, String year, String engine) throws Exception;

  //更新productLocalInfo信息，同时保持单位的更新日志  todo 单位更新有问题
  public void updateProductLocalInfo(Long id, Double price, Double purchasePrice, String storageBin, Double tradePrice,
                                     String storageUnit, String sellUnit);

  //根据barcode查找product,productbarcode
  public ProductBarcode searchBarcode(Long shopId, String barcode);

  public Product getProductByBarcode(String barcode) throws BcgogoException;

  void updateProductBarCode(Long productId, String barcode) throws BcgogoException;

  //todo 更新商品信息的时候单位特别要注意
  public void updateProductLocalInfo(ProductLocalInfoDTO productLocalInfoDTO);

  void updateProductLocalInfo(ProductLocalInfoDTO... localInfoDTOs);

  public ProductLocalInfo getProductLocalInfoById(Long productLocalInfoId);

  Result updateProductForInSales(Result result,ProductDTO productDTO) throws Exception;

  List<InventorySearchIndexDTO> initInventorySearchIndexDTOWithUnit(List<InventorySearchIndexDTO> inventorySearchIndexDTOs);

  public void updateProductLocalInfoUnit(Long shopId, Long productId, String storageUnit, String sellUnit, Long rate);

  public List<ProductDTO> getProducts(Long shopId, Long start, int num) throws BcgogoException;

  /**
   * 通过条件查询产品列表
   * 需要增加条件修改query对象和对应的SQL
   *
   * @param productQueryCondition
   * @return
   */
  public List<ProductDTO> getProductsByCondition(ProductQueryCondition productQueryCondition) throws Exception;

  public void updateProduct(Long shopId, ProductDTO productDTO) throws Exception;

  /**
   * 在数据库中添加一个新商品。
   * 根据saveNewProduct修改来  去掉产品建索引操作 和 ProductVehicle
   *
   * @param productDTO
   * @return
   */
  public boolean addProduct(ProductDTO productDTO) throws Exception;

  //保存或者修改车辆信息，customerVehicle ,solr
  public List<VehicleDTO> saveOrUpdateVehicleInfo(Long shopId, Long userId, Long customerId, CarDTO[] carDTOs) throws Exception;

  public List<Object[]> getProductDataByProductLocalInfoId(Long shopId, Long... productLocalInfoId) throws Exception;

  public List<Long> getProductLocalInfoIdList(Long shopId,int start,int num) throws Exception;

  //仅用于数据初始化
  public void saveProductSupplier(List<ProductSupplierDTO> productSupplierDTOs);

  //单据saveOrUpdateProductSupplier
  @Deprecated
  public void saveOrUpdateProductSupplier(List<ProductSupplierDTO> productSupplierDTOs);

  @Deprecated
  public List<ProductSupplierDTO> getProductSupplierDTOs(Long productId, Long shopId);

  @Deprecated
  public int countProductSupplierDTOsByShopId(Long shopId);

  @Deprecated
  public List<ProductSupplierDTO> getProductSupplierDTOsByShopId(Long shopId,Pager pager);

  //从productLocalInfoId shopId 限制数量组装两个map，其中 productSupplierIdsMap
  @Deprecated
  public void getProductSupplierMap(Map<Long,List<ProductSupplierDTO>> productSupplierInfoMap,
                                    Map<Long,List<ProductSupplierDTO>> productSupplierIdsMap,Long[] productLocalInfoId, Long shopId,Integer limit);

  Map<Long,ProductDTO> getProductDTOMapByProductLocalInfoIds(Set<Long> productLocalInfoIds);

  Map<Long,ProductDTO> getProductDTOMapByProductLocalInfoIds(Long shopId,Set<Long> productLocalInfoIds);

  /**
   * @param shopId  shopId 为空表示查询全部
   * @param productLocalInfoIds
   * @return  第一个key是customerProductId，第二个key是supplierProductId
   */
  Map<Long,Map<Long,ProductMappingDTO>> getCustomerProductMappingDTOMapInMap(Long shopId, Long... productLocalInfoIds);


  /**
   *  supplierShopId, customerShopId,productLocalInfoIds 不能为空 productLocalInfoIds 是supplier的
   * @return  key是supplierProductId，
   */
  Map<Long,ProductMappingDTO> getSupplierProductMappingDTODetailMap(Long supplierShopId,Long customerShopId,Set<Long> productLocalInfoIds);

  /**
   *  supplierShopId, customerShopId,customerProductLocalInfoIds 不能为空
   * @return  key是customerProductId，
   */
  Map<Long, ProductMappingDTO> getCustomerProductMappingDTOMap(Long customerShopId, Long supplierShopId, Long... customerProductLocalInfoIds);

  List<ProductDTO> getProductInfo(ProductSearchCondition conditionDTO);

  /**
   * @return key是customerProductId，  ProductMappingDTO 中封装了对应的两个productDTO
   */
  Map<Long, ProductMappingDTO>getCustomerProductMappingDTODetailMap(Long customerShopId, Long supplierShopId, Long... productLocalInfoIds);


  void updateTradePrice(ProductDTO[] productDTOs, Long shopId)throws Exception;

  void updateStorageBin(ProductDTO[] productDTOs, Long shopId)throws Exception;

  void updateProductInSalesAmount(Long shopId,ProductDTO[] productDTOs)throws Exception;

  Result updateProductGuaranteePeriod(Result result,Long shopId,ProductDTO[] productDTOs) throws Exception;

  Result updateProductInSalesPrice(Result result,Long shopId,ProductDTO[] productDTOs) throws Exception;

  void mergeCacheProductDTO(Long shopId, List<Long> addedProductLocalInfoIdList, List<Long> updatedProductLocalInfoIdList,
                            List<ProductDTO> productDTOs);

  ProductDTO updateCommodityCodeByProductLocalInfoId(Long shopId, Long productLocalInfoId, String commodityCode);

  ProductDTO getProductDTOByCommodityCode(long shopId,String commodityCode) throws Exception;

  List<ProductDTO> getProductDTOsByCommodityCodes(Long shopId,String... commodityCodes) throws Exception;

  Map<String,ProductDTO> getProductDTOMapsByCommodityCodes(Long shopId,String... commodityCodes) throws Exception;

  //批量设定商品的状态
  void setProductStatus(Long shopId,ProductStatus status,UserDTO user,Long ...productId) throws Exception;

  /**
   * 根据商品分类名称查找ID，存在则返回ID，不存在则新增后返回ID
   * @param shopId
   * @param kindName
   * @return
   */
  public Long getProductKindId(Long shopId,String kindName);

  //根据商品分类名称查找id
  public Long getKindIdByName(Long shopId,String kindName);

  //批量保存并获得商品分类
  public Map<String,KindDTO> batchSaveAndGetProductKind(Long shopId,Set<String> kindNames)throws Exception;

  //根据ID查找商品分类
  public String getKindNameById(Long kindId);



  //查询最近使用的15条商品分类
  public List<String> getProductKindsRecentlyUsed(Long shopId);

  //根据ID更新商品分类
  public void updateKind(Long kindId, String newKindName);

  //商品分类模糊查询
  public List<String> getProductKindsWithFuzzyQuery(Long shopId, String keyword);

  //批量修改商品分类
  public void updateMultipleProductKind(Long shopId, Long[] idList, Long newKindId);

  //逻辑删除商品分类
  public void deleteProductKind(Long shopId, String kindName);

  public Map<Long,KindDTO> getProductKindById(Long... kindId);

  public Map<Long,KindDTO> getKindDTOMap(Long shopId,Set<Long> ids);

  public Map<Long,KindDTO> getAllEnabledProductKindByShop(Long shopId);


  List<ProductDTO> getProductDTOsByProductKindId(Long shopId,Long... productKindId) throws Exception;

  public ProductLocalInfoDTO updateProductLocalInfoCategory(Long shopId,Long productLocalInfoId,Long categoryId);

  public boolean checkSameProduct(Long shopId,ProductDTO productDTO);

  //单据的复制，草稿单的使用，查询商品分类，排除被删掉的
  public KindDTO getEnabledKindDTOById(Long shopId,Long id);

  //删除营业分类的时候同事更新此记录中的营业分类id
  public void deleteProductLocalInfoCategoryId(Long shopId,Long categoryId);

  void saveOrUpdateProductForPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO)throws Exception;

//  /**
//   * 新增或者更新产品信息，新增包括InventorySearchIndex，更新不包括InventorySearchIndex，不包括为老商品新增单位
//   * @param repairOrderDTO
//   * @return  返回需要添加单位的ItemDTOs
//   * @throws Exception
//   */
//  List<RepairOrderItemDTO> saveOrUpdateProductForRepairOrder(RepairOrderDTO repairOrderDTO)throws Exception;

  //用7属性去检查商品是否重复
  List<ProductDTO> getProductDTOsBy7P(Long shopId,ProductDTO searchCondition);


  public void updateProductWithPurchaseOrder(Long shopId, ProductDTO... productDTO) throws Exception;

  /**
   * @param shopId
   * @param deleteProductIds  记录被删除需要恢复的productIds
   * @param productDTO   传入需要更新的字段
   * @throws Exception
   */
  public void updateProductWithRepairOrder(Long shopId, Set<Long> deleteProductIds, ProductDTO... productDTO) throws Exception;

  /**
   * @param shopId
   * @param productLocalInfoIds
   * @return   key 是productLocalInfId
   */
  Map<Long,ProductLocalInfoDTO> getProductLocalInfoMap(Long shopId, Long... productLocalInfoIds);

  /**
   * @param shopId
   * @param productIds
   * @return   key productId   根据product表的id 查出productLocalInfoId 返回的key是ProductID
   */
  Map<Long,ProductLocalInfoDTO> getProductLocalInfoMapByProductIds(Long shopId, Set<Long> productIds);

  void saveOrUpdateProductMapping(PurchaseInventoryDTO purchaseInventoryDTO)throws Exception;

  public void updateProductMappingForSalesReturn(Long customerShopId, Long supplierShopId, Map<Long, Long> customerSupplierProductMapping);

  //供应商客户的productId 一起查询
  public List<ProductMapping> getProductMappings(Long shopId, Long supplierShopId, Long... productIds) ;

  public ProductMapping getProductMappingById(long id) ;

  public List<ProductMapping> getProductMappings(ProductMappingDTO productMappingIndex);

  Map<Long,ProductMappingDTO> getCustomerProductMappings(Long customerShopId,Long[] productIds);

  void updateProductMappings(List<ProductMappingDTO> productMappingDTOs) throws BcgogoException;

  void updateProductForSalesReturn(SalesReturnDTO salesReturnDTO) throws Exception ;

  public List<NormalProductDTO> getNormalProductDTO(ProductSearchCondition searchCondition);

  public int countNormalProductDTO(ProductSearchCondition searchCondition);

  public List<Product> getProductByNormalProductId(Long id);

  public String checkNormalProductCommodityCodeRepeat(Long id,String commodityCode);

  public String checkNormalProductRepeat(NormalProductDTO normalProductDTO);

  public NormalProductDTO saveOrUpdateNormalProduct(NormalProductDTO normalProductDTO);

  public void deleteNormalProduct(Long id,List<Long> shopProductIdList);

  public void updateProductRelevanceStatus(Long productId,ProductRelevanceStatus relevanceStatus);

  void saveNormalProductModifyRecord(Long userId,Long normalProductId,Long shopProductId,NormalProductModifyScene scene);

  void saveNormalProductModifyRecord(NormalProductModifyRecordDTO... recordDTOs);

  public List<ProductCategoryDTO> getFirstProductCategory();

  public List<ProductCategoryDTO> getThirdCategoryByCondition(ProductSearchCondition searchCondition);

  public List<String> getNormalProductByCondition(ProductSearchCondition searchCondition);

  public int countShopProductsByNormalProductId(Long id);

  public List<ProductDTO> getShopProductsByNormalProductId(Long id,int page,int limit);

  public void deleteRelevance(Long id);
  public void checkRelevance(Long id);

  public int countShopProductsByCondition(ProductSearchCondition searchCondition);

  public List<ProductDTO> getShopProductsByCondition(ProductSearchCondition searchCondition);

  public List<NormalBrandDTO> getNormalBrandByKeyWord(String keyWord);

  public List<NormalModelDTO> getNormalModelByBrandId(Long brandId);

  public void completeShopName(List<ProductDTO> productDTOList);

  public void relevanceProduct(List<Long> shopProductIds,Long normalProductId);

  boolean isNormalProductIdRelevanceInShop(long normalProductId,long shopId, long shopProductId);

  ProductDTO getProductByNormalProductId(Long shopId, Long normalProductId);

  public List<NormalProductDTO> getNormalProductDTOByIds(List<Long> ids);


  public List<ProductLocalInfo> getProductLocalInfoByNormalProductId(Long normalProductId);

  public List<NormalProduct> getAllNormalProducts();

  public NormalProductDTO getNormalProductById(Long normalProductId);

  public List<ModelDTO> getModelHasBrandId();

  public List<BrandDTO> getBrandList(Long... id);

  public void initNormalBrandAndModel(List<BrandDTO> brandDTOList);

  public List<NormalProductDTO> getNormalProductDTOByCategoryId(Long categoryId);

  List<ProductLocalInfo> getAllProductInSales(Long shopId);

  List<ProductLocalInfoDTO> getProductLocalInfoDTOById(Long shopId,Long... productLocalInfoIds);

  public List<Long> updateProductSalesStatus(Long shopId,ProductStatus productStatus,Long... productLocalInfoIds)throws Exception;

  List<Long> startSellAllProductsWithTradePrice(Long shopId)throws Exception;

  List<Long> stopSellAllProducts(Long shopId)throws Exception;

  boolean checkProductInSalesByProductLocalInfoId(Long shopId,Long... productLocalInfoIds) throws Exception;

  List<ProductLocalInfoDTO> getAllProductLocalInfoWithTradePriceAndNotInSales(Long shopId) throws Exception;

  /**
   *
   * 采购的时候选择多个供应商的商品 是用到
   * @param productLocalInfoIds
   * @return
   */
  Map<Long, ProductLocalInfoDTO> getProductLocalInfoMap(Long... productLocalInfoIds);

  boolean cancelProductRelation(Long customerShopId, Long supplierShopId) throws Exception;


  void handleInSalesAmountByOrder(BcgogoOrderDto orderDto,double symbolNumber) throws Exception;

  Map<Long,ProductDTO> getProductDTOMapByIds(Long shopId, Long[] productDTOIds);

  int countAllProductLocalInfoWithNotTradePriceAndNotInSales(Long shopId) throws Exception;

  public Map<String,ProductDTO> getProductDTOMaps(Long shopId);

  public List<String> getAllCommodityCode(Long shopId);

  int countProductNotInSales(Long shopId)throws Exception;

  public void updateOldProductMappingsData(PurchaseInventoryDTO purchaseInventoryDTO);

  Long getBcgogoRecommendProductIds(Long normalProductId, Double comparePrices, Long... shopIds);

  /**
   * 获取店铺注册的商品 商品信息拿的是最新的商品信息
   * @param shopId
   * @return
   */
  public List<ProductDTO> getShopRegisterProductList(Long shopId);

  /**
   * 根据id获取商品分类（经营范围）
   * @param productCategoryIds
   * @return
   */
  public List<ProductCategory> getCategoryListByIds(List<Long> productCategoryIds);


  /**
   * 根据营业分类的id查找父类id
   * @param ids
   * @return
   */
  public List<Long> getCategoryParentIds(List<Long> ids);

  /**
   * 保存注册时填写的商品
   * @param productDTOs
   */
  public void saveShopRegisterProduct(ProductDTO[] productDTOs);

  List<ShopRegisterProductDTO> getShopRegisterProductListByShopId(Long shopId);

  public void analyzeProductWord() throws Exception;

  //入库单 更新商品逻辑
  void updateProductForPurchaseInventory(Long shopId, Set<Long> deletedProductIds, ProductDTO[] productDTOs);

  /**
   * 注册时专用 保存注册时填写的车辆品牌、车型
   * @param shopDTO
   */
  public void saveShopVehicleBrandModel(ShopDTO shopDTO);

  int countProductInSales(Long shopId);

  int countProductByPromotions(Long shopId,Long[] productIds);

  int countAllStockProduct(Long shopId);

  List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelByShopId(Long shopId);

  void saveOrUpdateRecentlyViewedProduct(Long shopId,Long userId,Long viewedProductLocalInfoId);

  List<ProductDTO> getRecentlyViewedProductDTOList(Long shopId,Long userId) throws Exception;

  Node getVehicleBrandModelByShopId(Long shopId);

  Node getCheckedVehicleBrandModel(Set<Long> ids);

  List<ShopVehicleBrandModelDTO> generateVehicleBrandModelDTOByModelIds(Set<Long> ids);

  void updateShopVehicleBrandModel(Long shopId,Set<Long> vehicleModelIds);

  void collectDuplicateProductNameAndUnits() throws Exception;

  List<ProductUnitDTO> getAllProductUnitDTOList();

  List<ProductDTO> getInSalingProductForSupplyDemand(Long shopId,int maxSize);

  List<ProductDTO> filterProductByShopKind(Long shopId,ProductDTO... productDTOs);

  void filterInvalidPromotions(List<ProductDTO> list);

  Map<Long,List<NormalProductVehicleBrandModelDTO>> getNormalProductVehicleBrandModelDTOMapByNormalProductId(Long... normalProductId);

  Node getNormalProductVehicleBrandModelByNormalProductId(Long normalProductId);

  public Map<Long,NormalProductDTO> getNormalProductDTOMapByIds(List<Long> ids);

  public Map<Long,NormalProductDTO> getSimpleNormalProductDTO(ProductSearchCondition searchCondition);

  List<Long> getAdShopIdByShopArea(Long province,Long city,Long region);

  List<Long> getCommodityAdProductIds(Pager pager,Long... shopIds);

  int countCommodityAdProduct(Long... adShopIds);

  List<Long> getLackAutoPreBuyProductId(Long shopId) throws ParseException;

}