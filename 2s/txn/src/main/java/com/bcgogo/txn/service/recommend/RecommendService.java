package com.bcgogo.txn.service.recommend;

import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.ProductRecommendType;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductCategory;
import com.bcgogo.productRead.service.IProductReadService;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.util.SolrUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.recommend.PreBuyOrderItemRecommendDTO;
import com.bcgogo.txn.dto.recommend.ProductRecommendDTO;
import com.bcgogo.txn.dto.recommend.ShopRecommendDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.recommend.*;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txnRead.service.IRecommendReadService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:18
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RecommendService implements IRecommendService{
  private static final Logger LOG = LoggerFactory.getLogger(RecommendService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;


  private void saveProductRecommendDTOList(Long shopId,List<ProductRecommendDTO> productRecommendDTOList,ProductRecommendType productRecommendType) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteOldProductRecommend(shopId,productRecommendType);
      if(CollectionUtils.isNotEmpty(productRecommendDTOList)){
        for(ProductRecommendDTO productRecommendDTO:productRecommendDTOList){
          ProductRecommend productRecommend = new ProductRecommend();
          productRecommend.fromDTO(productRecommendDTO);
          writer.save(productRecommend);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  private void savePreBuyOrderItemRecommendDTOList(Long shopId,List<PreBuyOrderItemRecommendDTO> preBuyOrderItemRecommendDTOList) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteOldPreBuyOrderItemRecommend(shopId);
      if(CollectionUtils.isNotEmpty(preBuyOrderItemRecommendDTOList)){
        for(PreBuyOrderItemRecommendDTO preBuyOrderItemRecommendDTO:preBuyOrderItemRecommendDTOList){
          PreBuyOrderItemRecommend preBuyOrderItemRecommend = new PreBuyOrderItemRecommend();
          preBuyOrderItemRecommend.fromDTO(preBuyOrderItemRecommendDTO);
          writer.save(preBuyOrderItemRecommend);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private void saveShopRecommendDTOList(Long shopId,List<ShopRecommendDTO> shopRecommendDTOList) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if(CollectionUtils.isNotEmpty(shopRecommendDTOList)){
        writer.deleteOldShopRecommend(shopId);
        for(ShopRecommendDTO shopRecommendDTO:shopRecommendDTOList){
          ShopRecommend shopRecommend = new ShopRecommend();
          shopRecommend.fromDTO(shopRecommendDTO);
          writer.save(shopRecommend);
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void moveProductRecommendToTrace() throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    int start = 0;
    int pageSize = 5000;
    while (true) {
      Object status = writer.begin();
      try {
        List<ProductRecommend> productRecommendList = writer.getMoveProductRecommendList(start, pageSize);
        if (CollectionUtils.isEmpty(productRecommendList)) {
          break;
        }
        ProductRecommendTrace productRecommendTrace = null;
        for (ProductRecommend productRecommend : productRecommendList) {
          productRecommendTrace = new ProductRecommendTrace();
          productRecommendTrace.fromProductRecommendDTO(productRecommend.toDTO());
          writer.save(productRecommendTrace);
          writer.delete(productRecommend);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }

  }

  @Override
  public void movePreBuyOrderItemRecommendToTrace() throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    int start = 0;
    int pageSize = 5000;
    while (true) {
      Object status = writer.begin();
      try {
        List<PreBuyOrderItemRecommend> preBuyOrderItemRecommendList = writer.getMovePreBuyOrderItemRecommendList(start, pageSize);
        if (CollectionUtils.isEmpty(preBuyOrderItemRecommendList)) {
          break;
        }
        PreBuyOrderItemRecommendTrace preBuyOrderItemRecommendTrace = null;
        for (PreBuyOrderItemRecommend preBuyOrderItemRecommend : preBuyOrderItemRecommendList) {
          preBuyOrderItemRecommendTrace = new PreBuyOrderItemRecommendTrace();
          preBuyOrderItemRecommendTrace.fromPreBuyOrderItemRecommendDTO(preBuyOrderItemRecommend.toDTO());
          writer.save(preBuyOrderItemRecommendTrace);
          writer.delete(preBuyOrderItemRecommend);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }

  }

  @Override
  public void moveShopRecommendToTrace() throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    int start = 0;
    int pageSize = 5000;
    while (true) {
      Object status = writer.begin();
      try {
        List<ShopRecommend> shopRecommendList = writer.getMoveShopRecommendList(start, pageSize);
        if (CollectionUtils.isEmpty(shopRecommendList)) {
          break;
        }
        ShopRecommendTrace shopRecommendTrace = null;
        for (ShopRecommend shopRecommend : shopRecommendList) {
          shopRecommendTrace = new ShopRecommendTrace();
          shopRecommendTrace.fromShopRecommendDTO(shopRecommend.toDTO());
          writer.save(shopRecommendTrace);
          writer.delete(shopRecommend);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }

  }

  @Override
  public void processShopRecommend() throws Exception {

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    int shopRecommendCount = NumberUtil.intValue(configService.getConfig(ConfigConstant.SHOP_RECOMMEND_COUNT, ConfigConstant.CONFIG_SHOP_ID), 0);
    if(shopRecommendCount>0){
      List<ShopDTO> shopDTOList = configService.getActiveShop();
      List<ShopDTO> sourceShopDTOList = null;
      List<Long> productCategoryIdList = null;
      List<Long> sourceProductCategoryIdList = null;

      for(ShopDTO shopDTO : shopDTOList){
        productCategoryIdList = configService.getShopBusinessScopeProductCategoryIdListByShopId(shopDTO.getId());

        if(ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())){//找客户
          sourceShopDTOList = findCustomerShopDTO(shopDTOList, shopDTO);
        }else{//供应商
          sourceShopDTOList = findSupplierShopDTO(shopDTOList, shopDTO);
        }

        if(CollectionUtils.isNotEmpty(sourceShopDTOList)){
          for(ShopDTO sourceShopDTO: sourceShopDTOList){
            sourceShopDTO.setCustomScore(NumberUtil.round(SolrUtil.getAreaMatchScore(shopDTO, sourceShopDTO),2));
            sourceProductCategoryIdList = configService.getShopBusinessScopeProductCategoryIdListByShopId(sourceShopDTO.getId());
            if(CollectionUtils.isNotEmpty(sourceProductCategoryIdList) && CollectionUtils.isNotEmpty(productCategoryIdList)){//有交集
              sourceProductCategoryIdList.retainAll(productCategoryIdList);
              sourceShopDTO.setCustomScore(sourceShopDTO.getCustomScore()+4*sourceProductCategoryIdList.size());
            }
          }
          Collections.sort(sourceShopDTOList, new Comparator<ShopDTO>() {
            public int compare(ShopDTO arg0, ShopDTO arg1) {
              return arg1.getCustomScore().compareTo(arg0.getCustomScore());
            }
          });

          int count = 0;
          List<ShopRecommendDTO> shopRecommendDTOList = new ArrayList<ShopRecommendDTO>();
          for(ShopDTO sourceShopDTO:sourceShopDTOList){
            shopRecommendDTOList.add(new ShopRecommendDTO(shopDTO.getId(),sourceShopDTO,DateUtil.getTheDayTime()));
            count++;
            if(count>=shopRecommendCount) break;
          }
          this.saveShopRecommendDTOList(shopDTO.getId(),shopRecommendDTOList);
        }
      }
    }
  }

  private List<ShopDTO> findSupplierShopDTO(List<ShopDTO> shopDTOList, ShopDTO shopDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Set<Long> excludeShopIdSet = new HashSet<Long>();
    List<ShopDTO> sourceShopDTOList = new ArrayList<ShopDTO>();
    List<SupplierDTO> supplierDTOList = userService.getRelatedSuppliersByShopId(shopDTO.getId());
    if(CollectionUtils.isNotEmpty(supplierDTOList)){
      for(SupplierDTO supplierDTO : supplierDTOList){
        excludeShopIdSet.add(supplierDTO.getSupplierShopId());
      }
    }
    for(ShopDTO sourceShopDTO : shopDTOList){
      if(!excludeShopIdSet.contains(sourceShopDTO.getId())
          && !sourceShopDTO.getId().equals(shopDTO.getId())
          && ConfigUtils.isWholesalerVersion(sourceShopDTO.getShopVersionId())
          && sourceShopDTO.getShopKind().equals(shopDTO.getShopKind())){
        sourceShopDTOList.add(sourceShopDTO);
      }
    }
    return sourceShopDTOList;
  }

  private List<ShopDTO>  findCustomerShopDTO(List<ShopDTO> shopDTOList, ShopDTO shopDTO) {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Set<Long> excludeShopIdSet = new HashSet<Long>();
    List<ShopDTO> sourceShopDTOList = new ArrayList<ShopDTO>();
    List<CustomerDTO> customerDTOList = customerService.getRelatedCustomersByShopId(shopDTO.getId());
    if(CollectionUtils.isNotEmpty(customerDTOList)){
      for(CustomerDTO customerDTO : customerDTOList){
        excludeShopIdSet.add(customerDTO.getCustomerShopId());
      }
    }
    for(ShopDTO sourceShopDTO : shopDTOList){
      if(!excludeShopIdSet.contains(sourceShopDTO.getId())
          && !sourceShopDTO.getId().equals(shopDTO.getId())
          && !ConfigUtils.isWholesalerVersion(sourceShopDTO.getShopVersionId())
          && sourceShopDTO.getShopKind().equals(shopDTO.getShopKind())){
        sourceShopDTOList.add(sourceShopDTO);
      }
    }
    return sourceShopDTOList;
  }


  @Override
  public void processAccessoryRecommend() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IRecommendReadService recommendReadService = ServiceManager.getService(IRecommendReadService.class);

    int accessorySeedCount = NumberUtil.intValue(configService.getConfig(ConfigConstant.ACCESSORY_SEED_COUNT, ConfigConstant.CONFIG_SHOP_ID), 0);
    int accessoryRecommendCount = NumberUtil.intValue(configService.getConfig(ConfigConstant.ACCESSORY_RECOMMEND_COUNT, ConfigConstant.CONFIG_SHOP_ID), 0);
    int accessoryRecommendByPreBuyOrderCount = NumberUtil.intValue(configService.getConfig(ConfigConstant.ACCESSORY_RECOMMEND_BY_PRE_BUY_ORDER_COUNT, ConfigConstant.CONFIG_SHOP_ID), 0);

    List<ShopDTO> shopDTOList = configService.getActiveShop();
    Map<Long,ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
    for(ShopDTO shopDTO : shopDTOList){
      shopDTOMap.put(shopDTO.getId(),shopDTO);
    }

    if(accessorySeedCount>0 && accessoryRecommendByPreBuyOrderCount>0){
      Map<String,SearchConditionDTO> normalPreBuyOrderAccessorySeedMap = null;
      for(ShopDTO seedShopDTO : shopDTOList){
        if(ConfigUtils.isWholesalerVersion(seedShopDTO.getShopVersionId())) continue;
        //获取种子并且去重复
        normalPreBuyOrderAccessorySeedMap = new HashMap<String, SearchConditionDTO>();
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = recommendReadService.getValidPreBuyOrderItemDTOByShopId(seedShopDTO.getId(), BusinessChanceType.Normal);
        for(PreBuyOrderItemDTO preBuyOrderItemDTO : preBuyOrderItemDTOList){
          normalPreBuyOrderAccessorySeedMap.put(preBuyOrderItemDTO.generateAccessorySeedKey(), new SearchConditionDTO(preBuyOrderItemDTO));
        }
        List<ProductRecommendDTO> productRecommendDTOList = getProductRecommendDTOs(accessoryRecommendByPreBuyOrderCount, normalPreBuyOrderAccessorySeedMap, shopDTOMap, seedShopDTO,ProductRecommendType.FromNormalPreBuyOrder);
        this.saveProductRecommendDTOList(seedShopDTO.getId(), productRecommendDTOList,ProductRecommendType.FromNormalPreBuyOrder);
      }
    }
    if(accessorySeedCount>0 && accessoryRecommendCount>0){
      Map<String,SearchConditionDTO> accessorySeedMap = null;
      for(ShopDTO seedShopDTO : shopDTOList){
        if(ConfigUtils.isWholesalerVersion(seedShopDTO.getShopVersionId())) continue;
        //获取种子并且去重复
        accessorySeedMap = new HashMap<String, SearchConditionDTO>();
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = recommendReadService.getValidPreBuyOrderItemDTOByShopId(seedShopDTO.getId(), BusinessChanceType.Lack,BusinessChanceType.SellWell);
        for(PreBuyOrderItemDTO preBuyOrderItemDTO : preBuyOrderItemDTOList){
          accessorySeedMap.put(preBuyOrderItemDTO.generateAccessorySeedKey(), new SearchConditionDTO(preBuyOrderItemDTO));
        }

        if(accessorySeedMap.size()<accessorySeedCount){
          //找销量
          List<ProductDTO> topSaleProductList = recommendReadService.getLastMonthTopTenSalesByShopId(seedShopDTO.getId());
          if(CollectionUtils.isNotEmpty(topSaleProductList)){
            for(ProductDTO productDTO : topSaleProductList){
              accessorySeedMap.put(productDTO.generateDataResourceKey(), new SearchConditionDTO(productDTO));
            }
          }
        }
        if(accessorySeedMap.size()<accessorySeedCount){
          //找注册时填写的
          IProductReadService productReadService = ServiceManager.getService(IProductReadService.class);
          List<ProductDTO> registerProductList = productReadService.getShopRegisterProductList(seedShopDTO.getId());
          if(CollectionUtils.isNotEmpty(registerProductList)){
            for(ProductDTO productDTO : registerProductList){
              accessorySeedMap.put(productDTO.generateDataResourceKey(), new SearchConditionDTO(productDTO));
            }
          }
        }
        List<ProductRecommendDTO> productRecommendDTOList = getProductRecommendDTOs(accessoryRecommendCount, accessorySeedMap, shopDTOMap, seedShopDTO, ProductRecommendType.FromOther);
        this.saveProductRecommendDTOList(seedShopDTO.getId(), productRecommendDTOList,ProductRecommendType.FromOther);
      }
    }
  }

  private List<ProductRecommendDTO> getProductRecommendDTOs(int recommendCount,Map<String, SearchConditionDTO> seedMap, Map<Long, ShopDTO> shopDTOMap, ShopDTO seedShopDTO,ProductRecommendType productRecommendType) throws Exception {
    ITradePushMessageService tradePushMessageService = ServiceManager.getService(ITradePushMessageService.class);
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    double productMatchScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_MATCH_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    double productPriceScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_PRICE_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    double productAreaScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_AREA_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    String matchingRule = String.format("%s*x+%s*y+%s*z",productMatchScale,productPriceScale,productAreaScale);

    //匹配数据结果集
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSalesStatus(ProductStatus.InSales);
    searchConditionDTO.setShopKind(seedShopDTO.getShopKind());
    searchConditionDTO.setExcludeShopIds(new Long[]{seedShopDTO.getId()});//除去自己店铺的
    searchConditionDTO.setMaxRows(recommendCount);

    Map<String,ProductDTO> tempAccessoryMap = new HashMap<String,ProductDTO>();
    for(Map.Entry<String,SearchConditionDTO> entry: seedMap.entrySet()){
      searchConditionDTO.setCustomMatchPContent(entry.getValue().generateCustomMatchPContent());
      searchConditionDTO.setCustomMatchPVContent(entry.getValue().generateCustomMatchPVContent());
      ProductSearchResultListDTO searchResultListDTO = searchProductService.queryAccessoryRecommend(false,searchConditionDTO);

      //自定义排序规则
      tradePushMessageService.filterCustomMatchAccessoryList(productMatchScale, productPriceScale, productAreaScale, searchResultListDTO, searchConditionDTO, seedShopDTO, shopDTOMap);
      ProductDTO existProductDTO = null;
      for(ProductDTO productDTO:searchResultListDTO.getProducts()){
        productDTO.setSearchConditionDTO(entry.getValue());
        existProductDTO = tempAccessoryMap.get(productDTO.generateShopDataResultKey());
        if(existProductDTO==null || (existProductDTO!=null && existProductDTO.getCustomScore()<productDTO.getCustomScore())){
          tempAccessoryMap.put(productDTO.generateShopDataResultKey(),productDTO);
        }
      }
    }
    List<ProductRecommendDTO> productRecommendDTOList = new ArrayList<ProductRecommendDTO>();
    if(MapUtils.isNotEmpty(tempAccessoryMap)){
      List<ProductDTO> resultList = new ArrayList<ProductDTO>(tempAccessoryMap.values());
      Collections.sort(resultList, new Comparator<ProductDTO>() {
        public int compare(ProductDTO arg0, ProductDTO arg1) {
          return arg1.getCustomScore().compareTo(arg0.getCustomScore());
        }
      });
      int count = 0;
      for(ProductDTO productDTO:resultList){
        productRecommendDTOList.add(new ProductRecommendDTO(seedShopDTO.getId(),matchingRule,productDTO,System.currentTimeMillis(),productRecommendType));
        count++;
        if(count>=recommendCount) break;
      }
      //保存到db
    }
    return productRecommendDTOList;
  }

  @Override
  public void processPreBuyOrderInformationRecommend() throws Exception{
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    IRecommendReadService recommendReadService = ServiceManager.getService(IRecommendReadService.class);
    int preBuyRecommendCount = NumberUtil.intValue(configService.getConfig(ConfigConstant.PRE_BUY_RECOMMEND_COUNT, ConfigConstant.CONFIG_SHOP_ID), 0);
    double preBuyMatchScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRE_BUY_MATCH_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    double preBuyAreaScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRE_BUY_AREA_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    String matchingRule = String.format("%s*x+%s*y",preBuyMatchScale,preBuyAreaScale);
    LOG.info("processPreBuyOrderInformationRecommend begin:---------------------------------------");
    Map<String,OrderSearchConditionDTO> preBuyOrderInformationSeedMap = null;
    List<ShopDTO> shopDTOList = configService.getActiveShop();
    LOG.info("processPreBuyOrderInformationRecommend activeShop size=:"+shopDTOList.size());
    Map<Long,ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
    for(ShopDTO shopDTO : shopDTOList){
      shopDTOMap.put(shopDTO.getId(),shopDTO);
    }
    LOG.info("processPreBuyOrderInformationRecommend init shopDTOMap size=:"+shopDTOMap.size());
    for(ShopDTO seedShopDTO : shopDTOList){
      if(!ConfigUtils.isWholesalerVersion(seedShopDTO.getShopVersionId())) continue;

      //获取种子并且去重复
      preBuyOrderInformationSeedMap = new HashMap<String, OrderSearchConditionDTO>();

      //找注册时填写的
      IProductReadService productReadService = ServiceManager.getService(IProductReadService.class);
      List<ProductDTO> registerProductList = productReadService.getShopRegisterProductList(seedShopDTO.getId());
      if(CollectionUtils.isNotEmpty(registerProductList)){
        for(ProductDTO productDTO : registerProductList){
          preBuyOrderInformationSeedMap.put(productDTO.generateDataResourceKey(),new OrderSearchConditionDTO(productDTO));
        }
      }
      //经营范围
      Set<Long> shopIdSet = new HashSet<Long>();
      shopIdSet.add(seedShopDTO.getId());
      List<ShopBusinessScopeDTO> shopBusinessScopeDTOList = configService.getShopBusinessScopeByShopId(shopIdSet);

      if (CollectionUtils.isNotEmpty(shopBusinessScopeDTOList)) {
        List<Long> productCategoryIdList = new ArrayList<Long>();
        for (ShopBusinessScopeDTO shopBusinessScopeDTO : shopBusinessScopeDTOList) {
          productCategoryIdList.add(shopBusinessScopeDTO.getProductCategoryId());
        }
        List<ProductCategory> productCategoryList = productReadService.getCategoryListByIds(productCategoryIdList);

        if (CollectionUtils.isNotEmpty(productCategoryList)) {
          Map<String, String> shopVehicleBrandModelStrMap =  productReadService.joinShopVehicleBrandModelStr(seedShopDTO.getId());
          ProductDTO productDTO = null;
          if(MapUtils.isNotEmpty(shopVehicleBrandModelStrMap)){
            for (ProductCategory productCategory : productCategoryList) {
              for(Map.Entry<String,String> entry:shopVehicleBrandModelStrMap.entrySet()) {
                productDTO = new ProductDTO();
                productDTO.setName(productCategory.getName());
                productDTO.setProductVehicleBrand(entry.getKey());
                productDTO.setProductVehicleModel(entry.getValue());
                preBuyOrderInformationSeedMap.put(productDTO.generateDataResourceKey(),new OrderSearchConditionDTO(productDTO));
              }
            }
          }else{
            for (ProductCategory productCategory : productCategoryList) {
              productDTO = new ProductDTO();
              productDTO.setName(productCategory.getName());
              preBuyOrderInformationSeedMap.put(productDTO.generateDataResourceKey(),new OrderSearchConditionDTO(productDTO));
            }
          }
        }
      }

      //匹配数据结果集
      OrderSearchConditionDTO orderSearchConditionDTO = new OrderSearchConditionDTO();
      orderSearchConditionDTO.setShopKind(seedShopDTO.getShopKind());
      orderSearchConditionDTO.setExcludeShopIds(new Long[]{seedShopDTO.getId()});//除去自己店铺的
      orderSearchConditionDTO.setPreBuyOrderStatus(OrderSearchConditionDTO.PreBuyOrderStatus.VALID);
      orderSearchConditionDTO.setMaxRows(preBuyRecommendCount);

      Map<String,OrderItemSearchResultDTO> tempPreBuyOrderInformationMap = new HashMap<String, OrderItemSearchResultDTO>();
      OrderItemSearchResultDTO existOrderItemSearchResultDTO = null;
      for(Map.Entry<String,OrderSearchConditionDTO> entry: preBuyOrderInformationSeedMap.entrySet()){
        orderSearchConditionDTO.setCustomMatchPContent(entry.getValue().generateCustomMatchPContent());
        orderSearchConditionDTO.setCustomMatchPVContent(entry.getValue().generateCustomMatchPVContent());
        OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryPreBuyRecommend(orderSearchConditionDTO);
        for(OrderItemSearchResultDTO orderItemSearchResultDTO:orderSearchResultListDTO.getOrderItems()){
          orderItemSearchResultDTO.setOrderSearchConditionDTO(entry.getValue());
          this.filterCustomMatchPreBuyItem(preBuyMatchScale,preBuyAreaScale,orderItemSearchResultDTO, entry.getValue(), seedShopDTO, shopDTOMap);
          if(orderItemSearchResultDTO.getCustomScore()>0d){
            existOrderItemSearchResultDTO = tempPreBuyOrderInformationMap.get(orderItemSearchResultDTO.generateShopDataResultKey());
            if(existOrderItemSearchResultDTO==null || (existOrderItemSearchResultDTO!=null && existOrderItemSearchResultDTO.getCustomScore()<orderItemSearchResultDTO.getCustomScore())){
              tempPreBuyOrderInformationMap.put(orderItemSearchResultDTO.generateShopDataResultKey(),orderItemSearchResultDTO);
            }
          }
        }
      }

      //反向 搜索  根据 上面未匹配上的重新 匹配
      for(ShopDTO shopDTO : shopDTOList){
        if(ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId()) || !shopDTO.getShopKind().equals(seedShopDTO.getShopKind()) || seedShopDTO.getId()==shopDTO.getId()) continue;
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = recommendReadService.getValidPreBuyOrderItemDTOByShopId(shopDTO.getId(),BusinessChanceType.SellWell,BusinessChanceType.Normal,BusinessChanceType.Lack);

        if(CollectionUtils.isNotEmpty(preBuyOrderItemDTOList)){
          for(PreBuyOrderItemDTO preBuyOrderItemDTO : preBuyOrderItemDTOList){
            if(!tempPreBuyOrderInformationMap.containsKey(preBuyOrderItemDTO.generateShopDataResultKey(shopDTO.getId()))){
              SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
              searchConditionDTO.setShopKind(seedShopDTO.getShopKind());
              searchConditionDTO.setShopId(seedShopDTO.getId());
              searchConditionDTO.setMaxRows(1);
              searchConditionDTO.setCustomMatchPContent(preBuyOrderItemDTO.generateCustomMatchPContent());
              searchConditionDTO.setCustomMatchPVContent(preBuyOrderItemDTO.generateCustomMatchPVContent());
              ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryAccessoryRecommend(false,searchConditionDTO);
              if(CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())){
                OrderItemSearchResultDTO orderItemSearchResultDTO = new OrderItemSearchResultDTO(shopDTO.getId(),preBuyOrderItemDTO);
                orderSearchConditionDTO = new OrderSearchConditionDTO(productSearchResultListDTO.getProducts().get(0));
                orderItemSearchResultDTO.setOrderSearchConditionDTO(orderSearchConditionDTO);
                this.filterCustomMatchPreBuyItem(preBuyMatchScale,preBuyAreaScale,orderItemSearchResultDTO,orderSearchConditionDTO, seedShopDTO, shopDTOMap);
                existOrderItemSearchResultDTO = tempPreBuyOrderInformationMap.get(orderItemSearchResultDTO.generateShopDataResultKey());
                if(existOrderItemSearchResultDTO==null || (existOrderItemSearchResultDTO!=null && existOrderItemSearchResultDTO.getCustomScore()<orderItemSearchResultDTO.getCustomScore())){
                  tempPreBuyOrderInformationMap.put(orderItemSearchResultDTO.generateShopDataResultKey(),orderItemSearchResultDTO);
                }
              }
            }
          }
        }
      }

      List<PreBuyOrderItemRecommendDTO> preBuyOrderItemRecommendDTOList = new ArrayList<PreBuyOrderItemRecommendDTO>();
      if(MapUtils.isNotEmpty(tempPreBuyOrderInformationMap)){
        List<OrderItemSearchResultDTO> resultList = new ArrayList<OrderItemSearchResultDTO>(tempPreBuyOrderInformationMap.values());
        Collections.sort(resultList, new Comparator<OrderItemSearchResultDTO>() {
          public int compare(OrderItemSearchResultDTO arg0, OrderItemSearchResultDTO arg1) {
            return arg1.getCustomScore().compareTo(arg0.getCustomScore());
          }
        });
        int count = 0;

        for(OrderItemSearchResultDTO orderItemSearchResultDTO:resultList){
          preBuyOrderItemRecommendDTOList.add(new PreBuyOrderItemRecommendDTO(seedShopDTO.getId(),matchingRule,orderItemSearchResultDTO,System.currentTimeMillis()));
          count++;
          if(count>=preBuyRecommendCount) break;
        }
        //保存到db
      }
      this.savePreBuyOrderItemRecommendDTOList(seedShopDTO.getId(), preBuyOrderItemRecommendDTOList);
    }
  }

  private void filterCustomMatchPreBuyItem(double preBuyMatchScale,double preBuyAreaScale,OrderItemSearchResultDTO orderItemSearchResultDTO, OrderSearchConditionDTO orderSearchConditionDTO, ShopDTO seedShopDTO, Map<Long, ShopDTO> shopDTOMap) throws Exception {
    double scoreTotal = 0d,score=0d;
    score = SolrUtil.getImitateSolrMatchScore(orderItemSearchResultDTO.generateCustomMatchPContent(), orderSearchConditionDTO.generateCustomMatchPContent(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
    if(score==0d){
      orderItemSearchResultDTO.setCustomScore(0d);
      return;
    }
    scoreTotal+=score;
    score = SolrUtil.getImitateSolrMatchScore(orderItemSearchResultDTO.generateCustomMatchPVContent(), orderSearchConditionDTO.generateCustomMatchPVContent(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
    if(score==0d){
      orderItemSearchResultDTO.setCustomScore(0d);
      return;
    }
    scoreTotal+=score;
    ShopDTO itemShopDTO = shopDTOMap.get(orderItemSearchResultDTO.getShopId());
    if(itemShopDTO==null){
      LOG.error("now shopDTOMap size is "+shopDTOMap.size());
      LOG.error("filterCustomMatchPreBuyItem orderItemSearchResultDTO shop_id:"+orderItemSearchResultDTO.getShopId()+",and preOrderItemId is"+orderItemSearchResultDTO.getItemId());
    }
    double areaScore = SolrUtil.getAreaMatchScore(seedShopDTO, itemShopDTO);
    orderItemSearchResultDTO.setCustomScore(NumberUtil.round((scoreTotal * preBuyMatchScale + areaScore * preBuyAreaScale),2));
  }
}
