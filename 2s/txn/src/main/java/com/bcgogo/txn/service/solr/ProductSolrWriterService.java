package com.bcgogo.txn.service.solr;

import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.model.Product;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ProductSolrIndexDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.util.SolrUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-28
 * Time: 下午1:06
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProductSolrWriterService implements IProductSolrWriterService {
  private static final Logger LOG = LoggerFactory.getLogger(IProductSolrWriterService.class);

  @Override
  public void createProductSolrIndex(Long shopId, Long... productLocalInfoId) throws Exception {
    if (productLocalInfoId != null && productLocalInfoId.length > 0) {
      long currentTime = System.currentTimeMillis();
      LOG.debug("product start time:"+currentTime+":"+ DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL,currentTime));
      IProductService productService = ServiceManager.getService(IProductService.class);
      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      IStoreHouseService storeHouseService =ServiceManager.getService(IStoreHouseService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
      IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
      IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
      ShopDTO shopDTO = configService.getShopById(shopId);
      Map<Long, Map<Long,StoreHouseInventoryDTO>> storeHouseInventoryDTOMapMap = null;
      List<StoreHouseDTO> storeHouseDTOList = null;
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopDTO.getShopVersionId())){
        storeHouseInventoryDTOMapMap = storeHouseService.getStoreHouseInventoryDTOMapMapByProductIds(shopId, productLocalInfoId);
        storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      }
      List<Object[]> productDataList = productService.getProductDataByProductLocalInfoId(shopId, productLocalInfoId);

      Set<Long> productIdSet = new HashSet<Long>();
      CollectionUtils.addAll(productIdSet,productLocalInfoId);
      Map<Long,InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productIdSet);

      Map<Long,List<SupplierInventoryDTO>> productSupplierMap = productThroughService.getSimpleSupplierInventoryMap(shopId,productIdSet);

      Map<Long,KindDTO> kindDTOMap = productService.getAllEnabledProductKindByShop(shopId);

      Map<Long,Map<Long,ProductMappingDTO>> pmDTOMAP = productService.getCustomerProductMappingDTOMapInMap(shopId, productLocalInfoId);

      Map<Long, Long> productCategoryRelationMap =productCategoryService.getProductCategoryRelationMap(shopId,productLocalInfoId);

      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      Long startTime = DateUtil.getInnerDayTime(-30);
      Long endTime = System.currentTimeMillis();
      Map<Long,Double> productLast30SalesAmountMap = preciseRecommendService.getSalesAmountMapByShopIdProductIdTime(shopId,startTime,endTime,productLocalInfoId);


      if (CollectionUtils.isNotEmpty(productDataList)) {
        ProductLocalInfo productLocalInfo = null;
        Map<Long,List<PromotionsDTO>> promotionsDTOMap = promotionsService.getPromotionsDTOMapByProductLocalInfoId(shopId, false,productLocalInfoId);

        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        ProductSolrIndexDTO productSolrIndexDTO = null;
        Product product = null;
        InventoryDTO inventoryDTO = null;
        KindDTO kindDTO = null;
        Double singleInventoryPrice = 0d;
        Map<Long,ProductMappingDTO> productMappingDTOMap = null;
        Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = null;
        StoreHouseInventoryDTO storeHouseInventoryDTO = null;
        List<String> wholesaleShopIdList = null;
        Map<String,Double> dynamicStorehouseInventoryAmountFieldData = null,dynamicStorehouseInventoryPriceFieldData = null;
        ProductDTO productDTO = null;

        for (Object[] obj : productDataList) {
          if (obj != null && obj.length == 2) {
            product = (Product) obj[0];
            if (product == null) continue;

            productDTO = product.toDTO();
            kindDTO = kindDTOMap.get(product.getKindId());
            if(kindDTO!=null){
              productDTO.setKindName(kindDTO.getName());
            }
            productLocalInfo = (ProductLocalInfo) obj[1];
            if (productLocalInfo != null) {
              productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
            }

            List<PromotionsDTO> promotionsDTOs=promotionsDTOMap.get(productLocalInfo.getId());
            List<String> promotionsTypeList=new ArrayList<String>();
            List<String> promotionsIdList=new ArrayList<String>();
            if(CollectionUtil.isNotEmpty(promotionsDTOs)){
              for(PromotionsDTO promotionsDTO:promotionsDTOs){
                if(promotionsDTO==null||promotionsDTO.getType()==null){
                  continue;
                }
                promotionsTypeList.add(promotionsDTO.getType().toString());
                promotionsIdList.add(promotionsDTO.getIdStr());
              }
            }
            productDTO.setPromotionsDTOs(promotionsDTOMap.get(productLocalInfo.getId()));
            productDTO.setPromotionsTypeList(promotionsTypeList);
            productDTO.setPromotionsIdList(promotionsIdList);
            productDTO.setProductCategoryId(productCategoryRelationMap.get(productLocalInfo.getId()));
            productSolrIndexDTO = new ProductSolrIndexDTO(productDTO);

            productSolrIndexDTO.setLast_30_sales(NumberUtil.doubleVal(productLast30SalesAmountMap.get(productLocalInfo.getId())));

            AreaDTO areaDTO = null;
            if(shopDTO.getRegion()!=null){
              areaDTO = AreaCacheManager.getAreaDTOByNo(shopDTO.getRegion());
            }else if(shopDTO.getCity()!=null){
              areaDTO = AreaCacheManager.getAreaDTOByNo(shopDTO.getCity());
            }else if(shopDTO.getProvince()!=null){
              areaDTO = AreaCacheManager.getAreaDTOByNo(shopDTO.getProvince());
            }
            productSolrIndexDTO.setShopInfo(shopDTO,areaDTO);

            inventoryDTO = inventoryDTOMap.get(productLocalInfo.getId());
            if (inventoryDTO != null) {
              productSolrIndexDTO.setInventory_amount(inventoryDTO.getAmount());
              productSolrIndexDTO.setStorage_time(inventoryDTO.getLastStorageTime()==null?System.currentTimeMillis():inventoryDTO.getLastStorageTime());
              productSolrIndexDTO.setRecommendedprice(inventoryDTO.getSalesPrice());
              productSolrIndexDTO.setInventory_average_price(inventoryDTO.getInventoryAveragePrice());
              productSolrIndexDTO.setLower_limit(inventoryDTO.getLowerLimit());
              productSolrIndexDTO.setUpper_limit(inventoryDTO.getUpperLimit());
            }

            if (inventoryDTO != null && inventoryDTO.getInventoryAveragePrice() != null){
              singleInventoryPrice = inventoryDTO.getInventoryAveragePrice();
            }else{
              singleInventoryPrice = 0d;
            }
            if (inventoryDTO != null && inventoryDTO.getAmount() != null) {
              productSolrIndexDTO.setInventory_price(singleInventoryPrice * inventoryDTO.getAmount());
            }
            productSolrIndexDTO.setLastmodified(System.currentTimeMillis());
            productSolrIndexDTO.setProductSupplierInfo(productSupplierMap.get(productLocalInfo.getId()));
            if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopDTO.getShopVersionId())){
              storeHouseInventoryDTOMap = storeHouseInventoryDTOMapMap.get(productLocalInfo.getId());
              if(CollectionUtils.isNotEmpty(storeHouseDTOList)){
                dynamicStorehouseInventoryAmountFieldData = new HashMap<String, Double>();
                dynamicStorehouseInventoryPriceFieldData = new HashMap<String, Double>();
                if(MapUtils.isNotEmpty(storeHouseInventoryDTOMap)){
                  for(StoreHouseDTO storeHouseDTO : storeHouseDTOList){
                    storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(storeHouseDTO.getId());
                    if(storeHouseInventoryDTO!=null){
                      dynamicStorehouseInventoryAmountFieldData.put(storeHouseDTO.getId().toString(),NumberUtil.doubleVal(storeHouseInventoryDTO.getAmount()));
                      dynamicStorehouseInventoryPriceFieldData.put(storeHouseDTO.getId().toString(),singleInventoryPrice*NumberUtil.doubleVal(storeHouseInventoryDTO.getAmount()));
                    }
                  }
                }
                productSolrIndexDTO.setDynamicStorehouseInventoryAmountFieldData(dynamicStorehouseInventoryAmountFieldData);
                productSolrIndexDTO.setDynamicStorehouseInventoryPriceFieldData(dynamicStorehouseInventoryPriceFieldData);
              }
            }
            //
            productMappingDTOMap = pmDTOMAP.get(productLocalInfo.getId());
            if(MapUtils.isNotEmpty(productMappingDTOMap)){
              wholesaleShopIdList = new ArrayList<String>();
              for(ProductMappingDTO productMappingDTO : productMappingDTOMap.values()){
                wholesaleShopIdList.add(productMappingDTO.getSupplierShopId() == null ? "" : productMappingDTO.getSupplierShopId().toString());
              }
            }
            productSolrIndexDTO.setWholesaler_shop_id(wholesaleShopIdList);

            docs.add(productSolrIndexDTO.toSolrInputDocument());
//            LOG.debug("单个Product SolrInputDocument 字节："+SolrUtil.getSolrInputDocumentSize(productSolrIndexDTO.toSolrInputDocument()));
          }
        }
        SolrClientHelper.getProductSolrClient().addDocs(docs);
      }
      LOG.debug("useTime:"+ (System.currentTimeMillis()-currentTime));
      LOG.debug("product end time:"+System.currentTimeMillis()+":"+ DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL,System.currentTimeMillis()));

    }
  }

  @Override
  public void reCreateProductSolrIndex(Long shopId, int pageSize) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    //
    StringBuffer query = new StringBuffer();
    query.append("shop_id:").append(shopId);
    //删除solr中shop信息
    ServiceManager.getService(ISearchService.class).deleteByQuery(query.toString(), "product");
    //
    int start = 0;
    while (true) {
      List<Long> productLocalInfoIdList = productService.getProductLocalInfoIdList(shopId, start, pageSize);
      if (CollectionUtils.isEmpty(productLocalInfoIdList)) break;
      int size = productLocalInfoIdList.size();
      start += size;
      this.createProductSolrIndex(shopId, productLocalInfoIdList.toArray(new Long[size]));
    }
  }

  @Override
  public void reCreateProductCategorySolrIndex(Long shopId, int pageSize) throws Exception {
    if(shopId==null) return;
    SolrClientHelper.getSuggestionClient().deleteByQuery("shop_id:" + shopId + " AND doc_type:" + SolrClientHelper.BcgogoSolrDocumentType.PRODUCT_CATEGORY_DOC_TYPE.getValue());
    int start = 0;
    while (true) {
      List<Long> ids = ServiceManager.getService(IProductCategoryService.class).getProductCategoryIdsByShopId(shopId, start, pageSize);
      if (CollectionUtils.isEmpty(ids)) break;
      start += ids.size();
      createProductCategorySolrIndex(shopId, ids.toArray(new Long[ids.size()]));
    }

  }

  @Override
  public void createProductCategorySolrIndex(Long shopId, Long... productCategoryIds) throws Exception {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(Arrays.asList(productCategoryIds)));
    if(CollectionUtils.isNotEmpty(productCategoryDTOList)){
      Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
      for (ProductCategoryDTO productCategoryDTO : productCategoryDTOList) {
        SolrInputDocument doc = new SolrInputDocument();

        doc.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.PRODUCT_CATEGORY_DOC_TYPE.getValue());
        doc.addField("id", productCategoryDTO.getId());
        doc.addField("shop_id", productCategoryDTO.getShopId());
        doc.addField("parent_id",productCategoryDTO.getParentId());
        doc.addField("product_category_type",productCategoryDTO.getCategoryType());
        doc.addField("name", productCategoryDTO.getName());
        if (StringUtils.isNotBlank(productCategoryDTO.getName())) {
          PingyinInfo pingyinInfo = PinyinUtil.getPingyinInfo(productCategoryDTO.getName());
          doc.addField("name_fl", pingyinInfo.firstLetters);
          doc.addField("name_py", pingyinInfo.pingyin);
        }
        doc.addField("status", productCategoryDTO.getStatus());
        docs.add(doc);
      }
      SolrClientHelper.getSuggestionClient().addDocs(docs);
    }
  }

  @Override
  public void productSolrAtomUpdate(Map<Long,Map<String,Object>> propertyMap) throws Exception {
    if(MapUtils.isEmpty(propertyMap)) return;

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    for(Map.Entry<Long,Map<String,Object>> entry:propertyMap.entrySet()){
      if(MapUtils.isEmpty(entry.getValue())) continue;
      SolrInputDocument doc = new SolrInputDocument();
      doc.addField("id", entry.getKey());
      for(Map.Entry<String,Object> property:entry.getValue().entrySet()){
        doc.addField(property.getKey(), SolrClientHelper.getSetOperation(property.getValue()));
      }
      docs.add(doc);
    }
    SolrClientHelper.getProductSolrClient().addDocs(docs);
  }

  @Override
  public void optimizeSolrProductCore() throws Exception {
    SolrClientHelper.getProductSolrClient().solrOptimize();
  }
}
