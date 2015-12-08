package com.bcgogo.txn.service;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.Product.RecommendSupplierType;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.util.SolrUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.ActiveRecommendSupplierDTO;
import com.bcgogo.txn.model.SupplierInventory;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-5-10
 * Time: 上午9:32
 * 主动推荐
 */
@Component
public class ActiveRecommendSupplierService implements IActiveRecommendSupplierService {
  private static final Logger LOG = LoggerFactory.getLogger(ActiveRecommendSupplierService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public Map<RecommendSupplierType, ActiveRecommendSupplierDTO> obtainActiveRecommendSupplierByProductId(Long productId, Long shopVersionId, Long shopId, Double comparePrice, Boolean isRepairOrder) throws Exception {
    Map<RecommendSupplierType, ActiveRecommendSupplierDTO> result = new HashMap<RecommendSupplierType, ActiveRecommendSupplierDTO>();
    if (BcgogoShopLogicResourceUtils.isNotActiveRecommendSupplier(shopVersionId)) {
      return result;
    }
    IProductService productService = ServiceManager.getService(IProductService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);
    if (productDTO == null) return result;
    //施工单才有 最近消费的供应商
    if (isRepairOrder) {
      TxnWriter writer = txnDaoManager.getWriter();
      SupplierInventory supplierInventory = writer.getLatestConsumeSupplier(productDTO.getProductLocalInfoId(), shopId);
      if (supplierInventory != null) {
        ActiveRecommendSupplierDTO recommendSupplierDTO = new ActiveRecommendSupplierDTO();
        recommendSupplierDTO.setSupplierDTO(supplierService.getSupplierById(supplierInventory.getSupplierId(),shopId));
        result.put(RecommendSupplierType.LEAST_CONSUME, recommendSupplierDTO);
      }
    }
    //查看是否是被标准化的商品
//    this.getBcgogoRecommendSupplier(productDTO, shopId, result, comparePrice);
    this.getAccessoryRecommendSupplier(productDTO, shopId, result, comparePrice);
    return result;
  }

  @Override
  public Map<Long, Map<RecommendSupplierType, ActiveRecommendSupplierDTO>> obtainActiveRecommendSuppliersByProductIds(Long shopId, Boolean isRepairOrder, Map<Long, Double> productIdAndComparePrice, Long shopVersionId) throws Exception {
    Map<Long, Map<RecommendSupplierType, ActiveRecommendSupplierDTO>> results = new HashMap<Long, Map<RecommendSupplierType, ActiveRecommendSupplierDTO>>();
    Map<RecommendSupplierType, ActiveRecommendSupplierDTO> result;
    //考虑到缺料不是很多
    for (Map.Entry<Long, Double> set : productIdAndComparePrice.entrySet()) {
      result = this.obtainActiveRecommendSupplierByProductId(set.getKey(), shopVersionId, shopId, set.getValue(), isRepairOrder);
      if (MapUtils.isNotEmpty(result)) {
        results.put(set.getKey(), result);
      }
    }
    return results;
  }

  // 推荐第一版 at version 15425
  private void getBcgogoRecommendSupplier(ProductDTO product, Long shopId, Map<RecommendSupplierType, ActiveRecommendSupplierDTO> result, Double comparePrice) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (product.getNormalProductId() == null) return;
    List<Long> shopIds = configService.getBcgogoRecommendSupplierShopIds(shopId);
    if (CollectionUtil.isEmpty(shopIds)) return;
    if (NumberUtil.isZero(comparePrice)) {
      comparePrice = null;
    }
    Long productId = productService.getBcgogoRecommendProductIds(product.getNormalProductId(), comparePrice, shopIds.toArray(new Long[shopIds.size()]));
    if (productId != null) {
      ActiveRecommendSupplierDTO recommendSupplierDTO = new ActiveRecommendSupplierDTO();
      ProductLocalInfo productLocalInfo = productService.getProductLocalInfoById(productId);
      recommendSupplierDTO.setShopDTO(configService.getShopById(productLocalInfo.getShopId()));
      ProductDTO dto = productService.getProductByProductLocalInfoId(productLocalInfo.getId(), productLocalInfo.getShopId());
      dto.setPrice(productLocalInfo.getTradePrice());
      recommendSupplierDTO.setProductDTO(dto);
      result.put(RecommendSupplierType.BCGOGO_RECOMMEND, recommendSupplierDTO);
    }
  }

  private void getAccessoryRecommendSupplier(ProductDTO productDTO, Long shopId, Map<RecommendSupplierType, ActiveRecommendSupplierDTO> result, Double comparePrice) throws Exception {
    StopWatchUtil sw = new StopWatchUtil("getAccessoryRecommendSupplier", "start");
    IProductService productService = ServiceManager.getService(IProductService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<ShopDTO> shopDTOList = configService.getActiveShopFromCache();

    sw.stopAndStart("get Area Nos");
    Map<Long,ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
    if(CollectionUtils.isEmpty(shopDTOList)){
      throw new Exception("ActiveShop is null!");
    }else{
      for(ShopDTO shopDTO:shopDTOList){
        Set<Long> areaNos = new HashSet<Long>();
        areaNos.add(shopDTO.getProvince());
        areaNos.add(shopDTO.getCity());
        areaNos.add(shopDTO.getRegion());

        Map<Long,AreaDTO> areaMap = configService.getAreaByAreaNo(areaNos);
        shopDTO.setAreaNameByAreaNo(areaMap);

        shopDTOMap.put(shopDTO.getId(),shopDTO);
      }
    }
    int preBuyHardMatchAccessoryPushMessageCount = NumberUtil.intValue(configService.getConfig(ConfigConstant.PRE_BUY_HARD_MATCH_ACCESSORY_PUSH_MESSAGE_COUNT, ConfigConstant.CONFIG_SHOP_ID),0);
    ShopDTO shopDTO = configService.getShopById(shopId);
    if (NumberUtil.isZero(comparePrice)) {
      comparePrice = null;
    }
    sw.stopAndStart("search solr");
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO(productDTO);
    searchConditionDTO.setSalesStatus(ProductStatus.InSales);
    searchConditionDTO.setShopKind(shopDTO.getShopKind());
    searchConditionDTO.setInSalesPriceEnd(comparePrice);
    searchConditionDTO.setExcludeShopIds(new Long[]{shopDTO.getId()});//除去自己店铺的
    searchConditionDTO.setMaxRows(preBuyHardMatchAccessoryPushMessageCount * 2);
    ProductSearchResultListDTO searchResultListDTO = searchProductService.queryAccessoryRecommend(false,searchConditionDTO);
    sw.stopAndStart("filter");

    double productMatchScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_MATCH_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    double productPriceScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_PRICE_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    double productAreaScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_AREA_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    this.filterCustomMatchAccessoryList(productMatchScale, productPriceScale, productAreaScale, searchResultListDTO, searchConditionDTO, shopDTO, shopDTOMap);

    sw.stopAndStart("getFirst");
    if(CollectionUtils.isNotEmpty(searchResultListDTO.getProducts())){//取第一个
      ActiveRecommendSupplierDTO recommendSupplierDTO = new ActiveRecommendSupplierDTO();
      ProductLocalInfo productLocalInfo = productService.getProductLocalInfoById(searchResultListDTO.getProducts().get(0).getProductLocalInfoId());
      recommendSupplierDTO.setShopDTO(configService.getShopById(productLocalInfo.getShopId()));
      ProductDTO dto = productService.getProductByProductLocalInfoId(productLocalInfo.getId(), productLocalInfo.getShopId());
      dto.setPrice(productLocalInfo.getTradePrice());
      recommendSupplierDTO.setProductDTO(dto);
      result.put(RecommendSupplierType.BCGOGO_RECOMMEND, recommendSupplierDTO);
    }
    sw.stopAndPrintLog();
  }

  private void filterCustomMatchAccessoryList(double productMatchScale, double productPriceScale, double productAreaScale, ProductSearchResultListDTO searchResultListDTO, SearchConditionDTO searchConditionDTO, ShopDTO seedShopDTO, Map<Long, ShopDTO> shopDTOMap) throws Exception {
    double totalPrice = 0d;
    double scoreTotal = 0d;
    Map<Long,Double> scoreMap = new HashMap<Long, Double>();
    Iterator<ProductDTO> iterator = searchResultListDTO.getProducts().iterator();
    while (iterator.hasNext()){
      ProductDTO productDTO = iterator.next();
      scoreTotal = SolrUtil.getImitateSolrMatchScore(productDTO.getName(), searchConditionDTO.getProductName(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
      if(scoreTotal==0d){
        iterator.remove();
        continue;
      }
      scoreTotal += SolrUtil.getImitateSolrMatchScore(productDTO.getBrand(), searchConditionDTO.getProductBrand(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
      if(scoreTotal==0d){
        iterator.remove();
        continue;
      }
      scoreTotal += SolrUtil.getImitateSolrMatchScore(productDTO.getModel(), searchConditionDTO.getProductModel(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
      if(scoreTotal==0d){
        iterator.remove();
        continue;
      }
      scoreTotal += SolrUtil.getImitateSolrMatchScore(productDTO.getSpec(), searchConditionDTO.getProductSpec(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
      if(scoreTotal==0d){
        iterator.remove();
        continue;
      }
      scoreTotal += SolrUtil.getImitateSolrMatchScore(productDTO.getProductVehicleModel(), searchConditionDTO.getProductVehicleModel(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
      if(scoreTotal==0d){
        iterator.remove();
        continue;
      }
      scoreTotal += SolrUtil.getImitateSolrMatchScore(productDTO.getProductVehicleBrand(), searchConditionDTO.getProductVehicleBrand(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
      if(scoreTotal==0d){
        iterator.remove();
        continue;
      }
      totalPrice+=NumberUtil.doubleVal(productDTO.getTradePrice());
      scoreMap.put(productDTO.getProductLocalInfoId(),scoreTotal);
    }

    if(CollectionUtils.isNotEmpty(searchResultListDTO.getProducts())){
      double averagePrice = totalPrice/searchResultListDTO.getProducts().size();
      ShopDTO productShopDTO = null;
      for(ProductDTO productDTO :searchResultListDTO.getProducts()){
        double productScore = scoreMap.get(productDTO.getProductLocalInfoId());
        productShopDTO = shopDTOMap.get(productDTO.getShopId());
        if(productShopDTO==null){
          LOG.error("filterCustomMatchAccessoryList productShopDTO shop_id:"+productDTO.getShopId()+" can't get shopDTO!");
        }
        double areaScore = SolrUtil.getAreaMatchScore(seedShopDTO, productShopDTO);
        if(averagePrice<=0.001){
          productDTO.setCustomScore(NumberUtil.round((productScore * productMatchScale + areaScore * productAreaScale),2));
        }else{
          productDTO.setCustomScore(NumberUtil.round((productScore * productMatchScale + (averagePrice-NumberUtil.doubleVal(productDTO.getTradePrice())) * productPriceScale / averagePrice + areaScore * productAreaScale),2));
        }
      }

      Collections.sort(searchResultListDTO.getProducts(), new Comparator<ProductDTO>() {
        public int compare(ProductDTO arg0, ProductDTO arg1) {
          return arg1.getCustomScore().compareTo(arg0.getCustomScore());
        }
      });
    }
  }
}
