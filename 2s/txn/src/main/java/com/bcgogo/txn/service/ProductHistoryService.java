package com.bcgogo.txn.service;

import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.product.dto.KindDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StopWatchUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-12
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductHistoryService implements IProductHistoryService{
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductHistoryService.class);
  
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void saveProductHistoryForOrder(Long shopId,BcgogoOrderDto bcgogoOrderDto) {
    if(bcgogoOrderDto == null || bcgogoOrderDto.getShopId() == null || ArrayUtils.isEmpty(bcgogoOrderDto.getItemDTOs())){
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      Long[] productIds = new Long[0];
      for (BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()) {
        if (bcgogoOrderDto instanceof PurchaseOrderDTO && itemDTO instanceof PurchaseOrderItemDTO
            && ((PurchaseOrderDTO) bcgogoOrderDto).getSupplierShopId() != null) {
          if (((PurchaseOrderItemDTO) itemDTO).getSupplierProductId() != null) {
            productIds = (Long[]) ArrayUtils.add(productIds, ((PurchaseOrderItemDTO) itemDTO).getSupplierProductId());
          }
        } else {
          if (itemDTO.getProductId() != null) {
            productIds = (Long[]) ArrayUtils.add(productIds, itemDTO.getProductId());
          }
        }
      }
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getOrSaveProductHistoryByLocalInfoId(shopId, productIds);

      for(BcgogoOrderItemDto itemDTO:bcgogoOrderDto.getItemDTOs()){
        if(itemDTO.getProductId() == null ){
          if(bcgogoOrderDto instanceof PurchaseOrderDTO && ((PurchaseOrderDTO) bcgogoOrderDto).getSupplierShopId() !=null
              && itemDTO instanceof PurchaseOrderItemDTO && ((PurchaseOrderItemDTO) itemDTO).getSupplierProductId() != null){
          }else {
            continue;
          }
        }
        ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductId());
        itemDTO.setProductHistoryId(productHistoryDTO==null?null:productHistoryDTO.getId());

        if(itemDTO instanceof PurchaseInventoryItemDTO){
          PurchaseInventoryItemDTO purchaseInventoryItemDTO = (PurchaseInventoryItemDTO) itemDTO;
          PurchaseInventoryItem item = writer.getById(PurchaseInventoryItem.class, purchaseInventoryItemDTO.getId());
          item.setProductHistoryId(itemDTO.getProductHistoryId());
          writer.update(item);
        }else if(itemDTO instanceof PurchaseOrderItemDTO){
          PurchaseOrderItemDTO purchaseOrderItemDTO = (PurchaseOrderItemDTO) itemDTO;
          PurchaseOrderItem item = writer.getById(PurchaseOrderItem.class, purchaseOrderItemDTO.getId());
          //在线采购记录对方的productHistory Id
          if (itemDTO.getProductHistoryId() == null) {
            ProductHistoryDTO supplierProductHistoryDTO = productHistoryDTOMap.get(((PurchaseOrderItemDTO) itemDTO).getSupplierProductId());
            itemDTO.setProductHistoryId(supplierProductHistoryDTO == null ? null : supplierProductHistoryDTO.getId());
          }
          item.setProductHistoryId(itemDTO.getProductHistoryId());
          writer.update(item);
        }else if(itemDTO instanceof SalesOrderItemDTO){
          SalesOrderItemDTO salesOrderItemDTO = (SalesOrderItemDTO) itemDTO;
          SalesOrderItem item = writer.getById(SalesOrderItem.class, salesOrderItemDTO.getId());
          item.setProductHistoryId(itemDTO.getProductHistoryId());
          writer.update(item);
        }else if(itemDTO instanceof SalesReturnItemDTO){
          SalesReturnItemDTO salesReturnItemDTO = (SalesReturnItemDTO) itemDTO;
          SalesReturnItem item = writer.getById(SalesReturnItem.class, salesReturnItemDTO.getId());
          item.setProductHistoryId(itemDTO.getProductHistoryId());
          writer.update(item);
        }else if(itemDTO instanceof PurchaseReturnItemDTO){
          PurchaseReturnItemDTO purchaseReturnItemDTO = (PurchaseReturnItemDTO) itemDTO;
          PurchaseReturnItem item = writer.getById(PurchaseReturnItem.class, purchaseReturnItemDTO.getId());
          item.setProductHistoryId(itemDTO.getProductHistoryId());
          writer.update(item);
        }else if(itemDTO instanceof RepairOrderItemDTO){
          RepairOrderItemDTO repairOrderItemDTO = (RepairOrderItemDTO) itemDTO;
          if(repairOrderItemDTO.getId()==null){
            continue;
          }
          RepairOrderItem item = writer.getById(RepairOrderItem.class, repairOrderItemDTO.getId());
          if(item != null) {
            item.setProductHistoryId(itemDTO.getProductHistoryId());
            writer.update(item);
          }
        }
      }
      writer.commit(status);
    }catch(Exception e){
      LOGGER.error("ProductHistoryService.saveProductHistoryForOrder出错.");
      LOGGER.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  private ProductHistory getOrSaveProductHistory(ProductHistory productHistory) {
    TxnWriter writer = txnDaoManager.getWriter();
    ProductHistory productHistoryExist = writer.getProductHistoryByProductLocalInfoIdAndVersions(productHistory.getProductLocalInfoId(), productHistory.getShopId(), productHistory.getProductVersion(), productHistory.getProductLocalInfoVersion(), productHistory.getInventoryVersion());
    if(productHistoryExist!=null){
      return productHistoryExist;
    }
    writer.save(productHistory);
    IImageService imageService = ServiceManager.getService(IImageService.class);
    imageService.copyProductHistoryImageDTOs(productHistory.toDTO());
    return productHistory;
  }

  @Override
  public ProductHistoryDTO getProductHistoryById(Long productHistoryId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    if(productHistoryId==null) return null;
    ProductHistory productHistory = writer.getProductHistoryById(productHistoryId, shopId);
    return productHistory==null?null:productHistory.toDTO();
  }

  /**
   * 根据productLocalInfoIds获取每个产品对应的ProductHistoryDTO, 不存在就新建。‘
   * 在线采购单的商品历史是通过对方供应商的本地商品数据  来记录的  所以传 supplierShopId
   * 不带事务
   * @param shopId
   * @param productLocalInfoIds
   * @return productLocalInfoId为key, ProductHistoryDTO为value的Map
   */
  @Override
  public Map<Long, ProductHistoryDTO> getOrSaveProductHistoryByLocalInfoId(Long shopId, Long... productLocalInfoIds) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    if(productLocalInfoIds == null || ArrayUtils.isEmpty(productLocalInfoIds)){
      return new HashMap<Long, ProductHistoryDTO>();
    }
    Map<Long, ProductLocalInfoDTO> productLocalInfoMap = productService.getProductLocalInfoMap(shopId, productLocalInfoIds);
    Set<Long> productIds = new HashSet<Long>();
    Long[] productDTOIds = new Long[productLocalInfoMap.size()];
    Set<Long> bussinessCategoryIds = new HashSet<Long>();
    for(Map.Entry<Long, ProductLocalInfoDTO> entry : productLocalInfoMap.entrySet()){
      ProductLocalInfoDTO productLocalInfoDTO = entry.getValue();
      productIds.add(productLocalInfoDTO.getProductId());
      if(productLocalInfoDTO.getBusinessCategoryId()!=null){
        bussinessCategoryIds.add(productLocalInfoDTO.getBusinessCategoryId());
      }
    }


    //build bussinessCategoryName
    if(CollectionUtils.isNotEmpty(bussinessCategoryIds)) {
      Map<Long, CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapById(shopId, bussinessCategoryIds);
      for(Map.Entry<Long, ProductLocalInfoDTO> entry : productLocalInfoMap.entrySet()){
        ProductLocalInfoDTO productLocalInfoDTO = entry.getValue();
        if(productLocalInfoDTO.getBusinessCategoryId()!=null){
          CategoryDTO categoryDTO = categoryDTOMap.get(productLocalInfoDTO.getBusinessCategoryId());
          productLocalInfoDTO.setBusinessCategoryName(categoryDTO==null?null:categoryDTO.getCategoryName());
        }
      }
    }

    Map<Long, Long> productCategoryRelationMap = productCategoryService.getProductCategoryRelationMap(shopId, productLocalInfoIds);

    productDTOIds = productIds.toArray(new Long[productIds.size()]);
    Map<Long, ProductDTO> productMap = null;
    try{
      productMap = productService.getProductDTOMapByIds(shopId, productDTOIds);
    }catch(Exception e){
      LOGGER.error(e.getMessage(), e);
    }
    //build kindName
    Set<Long> productKindIds = new HashSet<Long>();
    for(Map.Entry<Long, ProductDTO> entry : productMap.entrySet()){
      ProductDTO productDTO = entry.getValue();
      if(productDTO.getKindId()!=null){
        productKindIds.add(productDTO.getKindId());
        productDTO.setKindName(productService.getKindNameById(productDTO.getKindId()));
      }
    }
    if(CollectionUtils.isNotEmpty(productKindIds)){
      Map<Long,KindDTO> kindDTOMap = productService.getKindDTOMap(shopId,productKindIds);
      for(Map.Entry<Long, ProductDTO> entry : productMap.entrySet()){
        ProductDTO productDTO = entry.getValue();
        if(productDTO.getKindId()!=null){
          KindDTO kindDTO= kindDTOMap.get(productDTO.getKindId());
          productDTO.setKindName(kindDTO==null? null:kindDTO.getName());
        }
      }
    }

    List<InventoryDTO> inventoryDTOList = null;
    Map<Long, InventoryDTO> inventoryMap = new HashMap<Long, InventoryDTO>();
    try{
      inventoryMap = inventoryService.getInventoryDTOMap(shopId,new HashSet<Long>(Arrays.asList(productLocalInfoIds)));
//      inventoryDTOList = txnService.getInventoryByShopIdAndProductIds(shopId, productLocalInfoIds);
    }catch(Exception e){
      LOGGER.error(e.getMessage(), e);
    }
    if(MapUtils.isEmpty(inventoryMap)){
      LOGGER.error("ProductHistoryService.getOrSaveProductHistoryByLocalInfoId出错.无法找到Inventory. productLocalInfoIds:{}, shopId:{}", productLocalInfoIds, shopId);
    }

    Map<Long, ProductHistoryDTO> productHistoryDTOMap = new HashMap<Long, ProductHistoryDTO>();
    for(Map.Entry<Long, ProductLocalInfoDTO> productLocalInfoDTOEntry : productLocalInfoMap.entrySet()){
      ProductHistory productHistory = new ProductHistory();
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOEntry.getValue();
      ProductDTO productDTO = productMap.get(productLocalInfoDTO.getProductId());
      InventoryDTO inventoryDTO = inventoryMap.get(productLocalInfoDTO.getId());
      if(productDTO == null){
        LOGGER.error("ProductHistoryService.getOrSaveProductHistoryByLocalInfoId出错.无法找到Product. productId:{}", productLocalInfoDTO.getProductId());
        continue;
      }
      if(inventoryDTO == null){
        LOGGER.error("ProductHistoryService.getOrSaveProductHistoryByLocalInfoId出错.无法找到Inventory. localInfoId:{}", productLocalInfoDTO.getId());
      }

      productHistory.setProductDTO(productDTO);
      productHistory.setProductLocalInfoDTO(productLocalInfoDTO);
      if(inventoryDTO != null){
        productHistory.setInventoryDTO(inventoryDTO);
      }
      if(productCategoryRelationMap.get(productLocalInfoDTO.getId()) != null){
        productHistory.setProductCategoryId(productCategoryRelationMap.get(productLocalInfoDTO.getId()));
      }

      productHistory = getOrSaveProductHistory(productHistory);
      productHistoryDTOMap.put(productLocalInfoDTO.getId(), productHistory.toDTO());
    }
    return productHistoryDTOMap;
  }

  @Override
  public Map<Long, List<ProductHistoryDTO>> getProductHistoryDTOsByProductId(Long shopId, Long... productIds) {
    Map<Long, List<ProductHistoryDTO>> productHistoryDTOMap = new HashMap<Long, List<ProductHistoryDTO>>();
    if (shopId == null || ArrayUtils.isEmpty(productIds))
      return productHistoryDTOMap;
    TxnWriter writer = txnDaoManager.getWriter();
    Set<Long> ids = new HashSet<Long>();
    for (Long id : productIds) {
      ids.add(id);
    }
    List<ProductHistory> productHistories = writer.getProductHistoryByProductIds(ids);
    if (!CollectionUtils.isEmpty(productHistories)) {
      for (Long id : ids) {
        for (ProductHistory productHistory : productHistories) {
          if (id == productHistory.getProductLocalInfoId()) {
            List<ProductHistoryDTO> productHistoryDTOList = productHistoryDTOMap.get(id);
            if (productHistoryDTOList == null) {
              productHistoryDTOList = new ArrayList<ProductHistoryDTO>();
            }
            productHistoryDTOList.add(productHistory.toDTO());
          }
        }
      }
    }
    return productHistoryDTOMap;
  }

  @Override
  public Map<Long, ProductHistoryDTO> getProductHistoryDTOMapByProductHistoryIds(Set<Long> productHistoryIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    if(CollectionUtils.isEmpty(productHistoryIds)) return new HashMap<Long, ProductHistoryDTO>();
    List<ProductHistory> productHistories = writer.getProductHistoryByProductHistoryIds(productHistoryIds);
    Map<Long, ProductHistoryDTO> result = new HashMap<Long, ProductHistoryDTO>();
    if(CollectionUtils.isEmpty(productHistories)){
      return result;
    }
    for(ProductHistory productHistory : productHistories){
      result.put(productHistory.getId(), productHistory.toDTO());
    }
    return result;
  }

  /**
   * 比对产品与历史单据中相应产品信息是否一致. 比较字段包括：
   * name, brand, model, spec, vehicle_brand, vehicle_model
   * @param localInfoIdAndHistoryIdMap  Map<productLocalInfoId:Long, productHistoryId:Long>
   * @param shopId
   * @return
   */
  @Override
  public boolean compareProductSameWithHistory(Map<Long, Long> localInfoIdAndHistoryIdMap, Long shopId) {
    if(localInfoIdAndHistoryIdMap == null || localInfoIdAndHistoryIdMap.isEmpty()){
      return true;
    }
    for(Map.Entry<Long, Long> entry : localInfoIdAndHistoryIdMap.entrySet()){
      Long productLocalInfoId = entry.getKey();
      Long productHistoryId = entry.getValue();
      if(productLocalInfoId == null || productHistoryId == null){
        continue;
      }
      if(!compareProductSameWithHistory(productLocalInfoId, productHistoryId, shopId)){
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean compareProductSameWithHistory(Long productLocalInfoId, Long productHistoryId, Long shopId) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    ProductDTO productDTO = null;
    try{
      productDTO = productService.getProductByProductLocalInfoId(productLocalInfoId, shopId);
    }catch(Exception e){
      LOGGER.error("ProductHistoryService.compareProductSameWithHistory 比较产品信息时出错.");
      LOGGER.error(e.getMessage(), e);
    }
    ProductHistory productHistory = writer.getProductHistoryById(productHistoryId, shopId);
    if(productDTO == null || productHistory == null){
      return false;
    }
    ProductHistoryDTO productHistoryDTO = productHistory.toDTO();
    return productHistoryDTO.compareSame(productDTO);
  }

  @Override
  public boolean compareSupplierProductSameWithCustomerProductHistory(Map<Long, Long> productLocalInfoIdAndCustomerHistoryIdMap, Long supplierShopId, Long customerShopId) {
    if(productLocalInfoIdAndCustomerHistoryIdMap == null || productLocalInfoIdAndCustomerHistoryIdMap.isEmpty()){
      return true;
    }
    for(Map.Entry<Long, Long> entry : productLocalInfoIdAndCustomerHistoryIdMap.entrySet()){
      Long supplierProductLocalInfoId = entry.getKey();
      Long customerProductHistoryId = entry.getValue();
      if(supplierProductLocalInfoId == null || customerProductHistoryId == null){
        continue;
      }
      if(!compareSupplierProductSameWithCustomerProductHistory(supplierShopId,supplierProductLocalInfoId,customerShopId, customerProductHistoryId)){
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean compareSupplierProductSameWithCustomerProductHistory(Long supplierShopId, Long supplierProductLocalInfoId, Long customerShopId, Long customerProductHistoryId) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    ProductDTO supplierProductDTO = null;
    try{
      supplierProductDTO = productService.getProductByProductLocalInfoId(supplierProductLocalInfoId, supplierShopId);
    }catch(Exception e){
      LOGGER.error("ProductHistoryService.compareSupplierProductSameWithCustomerProductHistory 比较产品信息时出错.");
      LOGGER.error(e.getMessage(), e);
    }
    ProductHistory productHistory = writer.getProductHistoryById(customerProductHistoryId, customerShopId);
    if(supplierProductDTO == null || productHistory == null){
      return false;
    }
    ProductHistoryDTO productHistoryDTO = productHistory.toDTO();
    return productHistoryDTO.compareSame(supplierProductDTO);
  }

  @Override
  public void batchSaveProductHistory(Map<Long, ProductHistory> productHistoryMap, TxnWriter writer) throws Exception {
    LOGGER.debug("batchSaveProductHistory");
    StopWatchUtil sw = new StopWatchUtil("batchSaveProductHistory", "start");
    if (MapUtils.isEmpty(productHistoryMap)) {
      LOGGER.debug("productHistoryMap is empty");
      return;
    }
    Iterator iterator = productHistoryMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      ProductHistory productHistory = (ProductHistory) entry.getValue();
      if (productHistory == null || productHistory.getProductLocalInfoId() == null
          || productHistory.getInventoryVersion() == null || productHistory.getProductVersion() == null) {
        LOGGER.error("batchSaveProductHistory 需要处理的productHistory必须的字段有值为空{}", productHistory);
        iterator.remove();
      }
    }
    List<ProductHistory> productHistories = writer.getProductHistoryByProductHistoryDTOs(productHistoryMap.values());
    if (CollectionUtils.isNotEmpty(productHistories)) {
      for (ProductHistory productHistory : productHistories) {
        productHistoryMap.put(productHistory.getProductLocalInfoId(), productHistory);
      }
    }
    sw.stopAndStart("step_r");
    List<ProductHistoryDTO> newProductHistoryDTOList = new ArrayList<ProductHistoryDTO>();
    for (ProductHistory productHistory : productHistoryMap.values()) {
      if (productHistory == null || productHistory.getProductLocalInfoId() == null) {
        continue;
      }
      if (productHistory.getId() == null) {
        writer.save(productHistory);
        newProductHistoryDTOList.add(productHistory.toDTO());
        productHistoryMap.put(productHistory.getProductLocalInfoId(), productHistory);
      }
    }
    IImageService imageService = ServiceManager.getService(IImageService.class);
    imageService.copyProductHistoryImageDTOs(newProductHistoryDTOList.toArray(new ProductHistoryDTO[newProductHistoryDTOList.size()]));
//    sw.stopAndPrintLog();
    LOGGER.debug(sw.toString());
  }



  @Override
  public boolean isProductSnapShotBeLastVersionProductByKeyFields(ProductHistoryDTO productHistoryDTO, ProductDTO productDTO) {

    boolean result = false;
    if (productHistoryDTO == null || productDTO == null) {
      return result;
    }

    /* 比较基础属性 */
    if (!StringUtil.compareSame(productHistoryDTO.getName(), productDTO.getName())) { // 品名
      return result;
    }
    if (!StringUtil.compareSame(productHistoryDTO.getBrand(), productDTO.getBrand())) { // 品牌
      return result;
    }
    if (!StringUtil.compareSame(productHistoryDTO.getModel(), productDTO.getModel())) { // 型号
      return result;
    }
    if (!StringUtil.compareSame(productHistoryDTO.getSpec(), productDTO.getSpec())) { // 规格
      return result;
    }
    if (!StringUtil.compareSame(productHistoryDTO.getProductVehicleBrand(), productDTO.getProductVehicleBrand())) { // 车辆品牌
      return result;
    }
    if (!StringUtil.compareSame(productHistoryDTO.getProductVehicleModel(), productDTO.getProductVehicleModel())) { // 车辆型号
      return result;
    }
    if (!StringUtil.compareSame(productHistoryDTO.getCommodityCode(),productDTO.getCommodityCode())){ // 商品编码
      return result;
    }
    if (!StringUtil.compareSame(productHistoryDTO.getKindName(), productDTO.getKindName()) || !NumberUtil.isEqualIgnoreNull(productHistoryDTO.getKindId(), productDTO.getKindId())) { // 商品种类
      return result;
    }

    /* 价格相关 只比较上架价格*/
//    if ((productHistoryDTO.getSalesPrice() == null && productDTO.getSalesPrice() != null) || (productHistoryDTO.getSalesPrice() != null && productDTO.getSalesPrice() == null) || !productHistoryDTO.getSalesPrice().equals(productDTO.getSalesPrice())) {  // 销售价
//      return result;
//    }
//    if ((productHistoryDTO.getTradePrice() == null && productDTO.getTradePrice() != null) || (productHistoryDTO.getTradePrice() != null && productDTO.getTradePrice() == null) || !productHistoryDTO.getTradePrice().equals(productDTO.getTradePrice())) { // 批发价
//      return result;
//    }
//    if ((productHistoryDTO.getPurchasePrice() == null && productDTO.getPurchasePrice() != null) || (productHistoryDTO.getPurchasePrice() != null && productDTO.getPurchasePrice() == null) || !productHistoryDTO.getPurchasePrice().equals(productDTO.getPurchasePrice())) { // 采购价
//      return result;
//    }
    if ((productHistoryDTO.getInSalesPrice() == null && productDTO.getInSalesPrice() != null) || (productHistoryDTO.getInSalesPrice() != null && productDTO.getInSalesPrice() == null) || !productHistoryDTO.getInSalesPrice().equals(productDTO.getInSalesPrice())) {
      return result;
    }

    //商品标准分类 比较
    if ((productHistoryDTO.getBusinessCategoryId() == null && productDTO.getBusinessCategoryId() != null) || (productHistoryDTO.getBusinessCategoryId() != null && productDTO.getBusinessCategoryId() == null) || !NumberUtil.isEqualIgnoreNull(productHistoryDTO.getBusinessCategoryId(), productDTO.getBusinessCategoryId())) {
      return result;
    }

    //商品详细描述
    if (!StringUtil.compareSame(productDTO.getDescription(),productHistoryDTO.getDescription())){
      return result;
    }

    if(!StringUtil.compareSame(productDTO.getGuaranteePeriod(), productHistoryDTO.getGuaranteePeriod())){
      return result;
    }

    if(!StringUtil.compareSame(productDTO.getSellUnit(), productHistoryDTO.getSellUnit())){
      return result;
    }
    if(!StringUtil.compareSame(productDTO.getStorageUnit(), productHistoryDTO.getStorageUnit())){
      return result;
    }

    // 比较促销 由于一旦上架 促销被关联就不能进行修改 这里就比较产品关联的促销id列表是否一致
    if (!isPromotionListSame(productHistoryDTO, productDTO)) {
      return false;
    }
    // 比较商品图片
    if (!isProductImageListSame(productHistoryDTO, productDTO)) {
      return false;
    }
    return true;

  }

  private boolean isPromotionListSame(ProductHistoryDTO productHistoryDTO, ProductDTO productDTO) {
    if (productHistoryDTO == null || productDTO == null) {
      return false;
    }
    List<PromotionsDTO> promotionsDTOs = productDTO.getPromotionsDTOs();
    List<PromotionOrderRecordDTO> promotionOrderRecordDTOs = productHistoryDTO.getPromotionOrderRecordDTOs();
    if (CollectionUtils.isEmpty(promotionsDTOs) && CollectionUtils.isEmpty(promotionOrderRecordDTOs)) {
      return true;
    }
    if (CollectionUtils.isNotEmpty(promotionsDTOs) && CollectionUtils.isNotEmpty(promotionOrderRecordDTOs)) {

      if (promotionsDTOs.size() != promotionOrderRecordDTOs.size()) {
        return false;
      }
      Set<Long> promotionIds = new HashSet<Long>();
      for (PromotionsDTO promotionsDTO : promotionsDTOs) {
        promotionIds.add(promotionsDTO.getId());
      }
      Set<Long> recordPromotionIds = new HashSet<Long>();
      for (PromotionOrderRecordDTO promotionOrderRecordDTO : promotionOrderRecordDTOs) {
        recordPromotionIds.add(((PromotionsDTO) JsonUtil.jsonToObject(promotionOrderRecordDTO.getPromotionsJson(), PromotionsDTO.class)).getId());
      }

      return promotionIds.equals(recordPromotionIds);

    }
    return false;
  }

  private boolean isProductImageListSame(ProductHistoryDTO productHistoryDTO, ProductDTO productDTO) {
    if (productHistoryDTO == null || productDTO == null) {
      return false;
    }
    IImageService imageService = ServiceManager.getService(IImageService.class);
    List<ImageInfoDTO> productHistoryImageInfoDTOList = imageService.getImageInfoDTO(productHistoryDTO.getShopId(), ImageType.getAllProductImageTypeSet(), DataType.PRODUCT_HISTORY, productHistoryDTO.getId());
    List<ImageInfoDTO> productImageInfoDTOList = imageService.getImageInfoDTO(productDTO.getShopId(), ImageType.getAllProductImageTypeSet(), DataType.PRODUCT, productDTO.getProductLocalInfoId());

    if (CollectionUtils.isEmpty(productHistoryImageInfoDTOList) && CollectionUtils.isEmpty(productImageInfoDTOList)) {
      return true;
    }
    if (CollectionUtils.isNotEmpty(productHistoryImageInfoDTOList) && CollectionUtils.isNotEmpty(productImageInfoDTOList)) {
      Set<String> productHistoryImageURLSet = new HashSet<String>();
      for(ImageInfoDTO imageInfoDTO:productHistoryImageInfoDTOList){
        productHistoryImageURLSet.add(imageInfoDTO.getPath());
      }
      Set<String> productImageURLSet = new HashSet<String>();
      for(ImageInfoDTO imageInfoDTO:productImageInfoDTOList){
        productImageURLSet.add(imageInfoDTO.getPath());
      }
      return CollectionUtils.isEqualCollection(productHistoryImageURLSet,productImageURLSet);
    }
    return false;
  }

}
