package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.pushMessage.PushMessageContentTemplate;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.constant.pushMessage.PushMessagePromptTemplate;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.notification.velocity.PushMessageVelocityContext;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductCategory;
import com.bcgogo.product.model.PromotionsRule;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.productRead.service.IProductReadService;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.ISolrMatchStopWordService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.util.SolrUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.PromotionsRuleDTO;
import com.bcgogo.txn.dto.pushMessage.*;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessageBuildTask;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.messageCenter.AbstractMessageService;
import com.bcgogo.txnRead.service.IRecommendReadService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-5-14
 * Time: 下午2:30
 */
@Service
public class TradePushMessageService extends AbstractMessageService implements ITradePushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(TradePushMessageService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public PushMessageBuildTaskDTO getLatestPushMessageBuildTaskDTO(PushMessageScene... scene) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    PushMessageBuildTask pushMessageBuildTask = writer.getLatestPushMessageBuildTask(scene);
    if(pushMessageBuildTask!=null){
      return pushMessageBuildTask.toDTO();
    }
    return null;
  }

  @Override
  public void savePushMessageBuildTaskDTO(PushMessageBuildTaskDTO... pushMessageBuildTaskDTOs) throws Exception {
    if(ArrayUtils.isEmpty(pushMessageBuildTaskDTOs)) return;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for(PushMessageBuildTaskDTO pushMessageBuildTaskDTO : pushMessageBuildTaskDTOs){
        PushMessageBuildTask pushMessageBuildTask = new PushMessageBuildTask();
        pushMessageBuildTask.fromDTO(pushMessageBuildTaskDTO);
        writer.save(pushMessageBuildTask);
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updatePushMessageBuildTask(PushMessageBuildTaskDTO pushMessageBuildTaskDTO) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      PushMessageBuildTask pushMessageBuildTask = writer.getById(PushMessageBuildTask.class,pushMessageBuildTaskDTO.getId());
      pushMessageBuildTask.fromDTO(pushMessageBuildTaskDTO);
      writer.update(pushMessageBuildTask);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private void addDataResourceByPreBuyOrderItem(Map<String, ProductDTO> dataResourceMap, ShopDTO shopDTO) throws Exception {
    IRecommendReadService recommendReadService = ServiceManager.getService(IRecommendReadService.class);
    List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = recommendReadService.getValidPreBuyOrderItemDTOByShopId(shopDTO.getId(),BusinessChanceType.SellWell,BusinessChanceType.Normal,BusinessChanceType.Lack);

    ProductDTO dataResource = null;
    if(CollectionUtils.isNotEmpty(preBuyOrderItemDTOList)){
      for(PreBuyOrderItemDTO preBuyOrderItemDTO : preBuyOrderItemDTOList){
        dataResource = new ProductDTO(shopDTO.getId(),preBuyOrderItemDTO);
        dataResourceMap.put(dataResource.generateDataResourceKey(),dataResource);
      }
    }
  }

  private void addDataResourceByShopBusinessScope(Map<String, ProductDTO> dataResourceMap, ShopDTO shopDTO) throws Exception {
    IProductReadService productReadService = ServiceManager.getService(IProductReadService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    //经营范围
    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(shopDTO.getId());
    List<ShopBusinessScopeDTO> shopBusinessScopeDTOList = configService.getShopBusinessScopeByShopId(shopIdSet);

    if (CollectionUtils.isNotEmpty(shopBusinessScopeDTOList)) {
      List<Long> productCategoryIdList = new ArrayList<Long>();
      for (ShopBusinessScopeDTO shopBusinessScopeDTO : shopBusinessScopeDTOList) {
        productCategoryIdList.add(shopBusinessScopeDTO.getProductCategoryId());
      }
      List<ProductCategory> productCategoryList = productReadService.getCategoryListByIds(productCategoryIdList);
      if (CollectionUtils.isNotEmpty(productCategoryList)) {
        Map<String, String> shopVehicleBrandModelStrMap =  productReadService.joinShopVehicleBrandModelStr(shopDTO.getId());
        ProductDTO productDTO = null;
        if(MapUtils.isNotEmpty(shopVehicleBrandModelStrMap)){
          for (ProductCategory productCategory : productCategoryList) {
            for(Map.Entry<String,String> entry:shopVehicleBrandModelStrMap.entrySet()) {
              productDTO = new ProductDTO();
              productDTO.setName(productCategory.getName());
              productDTO.setShopId(shopDTO.getId());
              productDTO.setProductVehicleBrand(entry.getKey());
              productDTO.setProductVehicleModel(entry.getValue());
              dataResourceMap.put(productDTO.generateDataResourceKey(), productDTO);
            }
          }
        }else{
          for (ProductCategory productCategory : productCategoryList) {
            productDTO = new ProductDTO();
            productDTO.setName(productCategory.getName());
            productDTO.setShopId(shopDTO.getId());
            dataResourceMap.put(productDTO.generateDataResourceKey(),productDTO);
          }
        }
      }
    }
  }

  private void addDataResourceByLastWeekTopTenSales(Map<String, ProductDTO> dataResourceMap, ShopDTO shopDTO) throws Exception {
    IRecommendReadService recommendReadService = ServiceManager.getService(IRecommendReadService.class);
    List<ProductDTO> topSaleProductList = recommendReadService.getLastMonthTopTenSalesByShopId(shopDTO.getId());
    if(CollectionUtils.isNotEmpty(topSaleProductList)){
      for(ProductDTO productDTO : topSaleProductList){
        dataResourceMap.put(productDTO.generateDataResourceKey(),productDTO);
      }
    }
  }

  private void addDataResourceByRegisterProduct(Map<String, ProductDTO> dataResourceMap,ShopDTO shopDTO) {
    IProductReadService productReadService = ServiceManager.getService(IProductReadService.class);
    //找注册时填写的
    List<ProductDTO> registerProductList = productReadService.getShopRegisterProductList(shopDTO.getId());
    if(CollectionUtils.isNotEmpty(registerProductList)){
      for(ProductDTO productDTO : registerProductList){
        dataResourceMap.put(productDTO.generateDataResourceKey(),productDTO);
      }
    }
  }

  @Override
  public void processPushMessageBuildTask() throws Exception{
    PushMessageBuildTaskDTO taskDTO = null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<ShopDTO> shopDTOList = configService.getActiveShop();

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
    Set<Long> needUpdateShopIdSet= new HashSet<Long>();
    do{
//      int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//      if(currentHour>=1 && currentHour<=5){
        taskDTO = this.getLatestPushMessageBuildTaskDTO(PushMessageScene.values());
//      }else{
//        taskDTO = this.getLatestPushMessageBuildTaskDTO(PushMessageScene.PRE_BUY_ORDER_INFORMATION);
//      }

      if(taskDTO == null){
        break;
      }
      taskDTO.setExecuteTime(System.currentTimeMillis());
      if (taskDTO.getScene() != null && taskDTO.getShopId() != null && taskDTO.getSeedId() != null) {
        taskDTO.setExeStatus(ExeStatus.START);
        this.updatePushMessageBuildTask(taskDTO);

        try{
          if (PushMessageScene.ACCESSORY_PROMOTIONS.equals(taskDTO.getScene())) {
//            this.generateAccessoryPushMessage(taskDTO,shopDTOMap);
          } else if (PushMessageScene.ACCESSORY_SALES.equals(taskDTO.getScene())) {
//            this.generateAccessoryPushMessage(taskDTO,shopDTOMap);
          } else if (PushMessageScene.PRE_BUY_ORDER_ACCESSORY.equals(taskDTO.getScene())) {
//            this.generatePreBuyMatchAccessoryPushMessageDTO(taskDTO, shopDTOMap);
          } else if (PushMessageScene.PRE_BUY_ORDER_INFORMATION.equals(taskDTO.getScene())) {
            PushMessageDTO pushMessageDTO = this.generatePreBuyPushMessage(taskDTO,shopDTOMap);
            if(pushMessageDTO!=null && CollectionUtils.isNotEmpty(pushMessageDTO.getPushMessageReceiverDTOList())){
              for (PushMessageReceiverDTO pushMessageReceiverDTO : pushMessageDTO.getPushMessageReceiverDTOList()) {
                needUpdateShopIdSet.add(pushMessageReceiverDTO.getShopId());
              }
            }
          }
          taskDTO.setExeStatus(ExeStatus.FINISHED);
          this.updatePushMessageBuildTask(taskDTO);
        }catch (Exception e){
          LOG.error("processPushMessageBuildTask error task id:"+taskDTO.getId());
          LOG.error(e.getMessage());
          taskDTO.setExeStatus(ExeStatus.EXCEPTION);
          this.updatePushMessageBuildTask(taskDTO);
        }

      }else if(taskDTO.getId() != null){
        taskDTO.setExeStatus(ExeStatus.EXCEPTION);
        this.updatePushMessageBuildTask(taskDTO);
      }
    }while (taskDTO!=null);

    if(CollectionUtils.isNotEmpty(needUpdateShopIdSet)){
      for (Long shopId : needUpdateShopIdSet) {
        List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopId);
        if(CollectionUtils.isNotEmpty(userIds)){
          for(Long userId : userIds){
            ServiceManager.getService(IPushMessageService.class).updatePushMessageCategoryStatNumberInMemCache(shopId, userId, PushMessageCategory.values());
          }
        }
      }
    }
  }

  @Override
  public void filterCustomMatchAccessoryList(double productMatchScale, double productPriceScale, double productAreaScale, ProductSearchResultListDTO searchResultListDTO, SearchConditionDTO searchConditionDTO, ShopDTO seedShopDTO, Map<Long, ShopDTO> shopDTOMap) throws Exception {
    double totalPrice = 0d;
    double scoreTotal = 0d;
    Map<Long,Double> scoreMap = new HashMap<Long, Double>();
    Iterator<ProductDTO> iterator = searchResultListDTO.getProducts().iterator();
    while (iterator.hasNext()){
      ProductDTO productDTO = iterator.next();
      scoreTotal = SolrUtil.getImitateSolrMatchScore(productDTO.generateCustomMatchPContent(), searchConditionDTO.getCustomMatchPContent(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
      if(scoreTotal==0d){
        iterator.remove();
        continue;
      }
      scoreTotal += SolrUtil.getImitateSolrMatchScore(productDTO.generateCustomMatchPVContent(), searchConditionDTO.getCustomMatchPVContent(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
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
        if(Math.abs(averagePrice)<0.0001){
          LOG.warn("averagePrice 太小："+averagePrice+",seedShopDTO id:"+seedShopDTO.getId());
          productDTO.setCustomScore(NumberUtil.round((productScore * productMatchScale + areaScore * productAreaScale),2));
        }else{
          productDTO.setCustomScore(NumberUtil.round((productScore * productMatchScale + (averagePrice-NumberUtil.doubleVal(productDTO.getTradePrice())) * productPriceScale / averagePrice + areaScore * productAreaScale),2));
        }
      }
    }
  }

  /**
   * 种子匹配中的数据店铺  代表需要推送消息的店铺  然后把种子做为消息的数据源生成消息
   * @param taskDTO
   * @param shopDTOMap
   * @throws Exception
   */
  private void generateAccessoryPushMessage(PushMessageBuildTaskDTO taskDTO,Map<Long,ShopDTO> shopDTOMap) throws Exception{
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    ShopDTO seedShopDTO = shopDTOMap.get(taskDTO.getShopId());

    ProductDTO seedDTO = productService.getProductByProductLocalInfoId(taskDTO.getSeedId(), seedShopDTO.getId());
    PromotionsDTO currentPromotionsDTO = null;
    if(PushMessageScene.ACCESSORY_PROMOTIONS.equals(taskDTO.getScene())){
      List<PromotionsDTO> promotionsDTOList = promotionsService.getSimplePromotionsDTOByProductLocalInfoId(seedDTO.getShopId(),seedDTO.getProductLocalInfoId());
      if(CollectionUtils.isNotEmpty(promotionsDTOList)){
        for(PromotionsDTO promotionsDTO : promotionsDTOList){
          if(PromotionsEnum.PromotionStatus.USING.equals(promotionsDTO.getStatus())){
            if(currentPromotionsDTO==null || PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(currentPromotionsDTO.getType())){
              currentPromotionsDTO = promotionsDTO;
            }
          }
        }
      }
      if(currentPromotionsDTO==null){
        LOG.error("在生成促销推送消息的时候没有获取有效的促销信息,taskDTO id:"+taskDTO.getId());
        return;
      }

    }


    Map<String,ProductDTO> dataResourceMap = null;
    List<PushMessageReceiverDTO> pushMessageReceiverDTOList = new ArrayList<PushMessageReceiverDTO>();
    for(ShopDTO pushShopDTO : shopDTOMap.values()){
      if(seedShopDTO.getId().equals(pushShopDTO.getId()) || ConfigUtils.isWholesalerVersion(pushShopDTO.getShopVersionId()) || !seedShopDTO.getShopKind().equals(pushShopDTO.getShopKind())) continue;

      dataResourceMap = new HashMap<String, ProductDTO>();
      //求购
      addDataResourceByPreBuyOrderItem(dataResourceMap, pushShopDTO);
      addDataResourceByLastWeekTopTenSales(dataResourceMap,pushShopDTO);
      addDataResourceByShopBusinessScope(dataResourceMap,pushShopDTO);
      addDataResourceByRegisterProduct(dataResourceMap,pushShopDTO);

      ProductDTO matchedProductDTO = matchDataResourceColl(seedDTO, dataResourceMap.values());
      if(matchedProductDTO!=null){
        PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO(pushShopDTO.getId(),pushShopDTO.getShopKind(),pushShopDTO.getId(), OperatorType.SHOP);
        pushMessageReceiverDTO.setPushMessageReceiverMatchRecordDTO(new PushMessageReceiverMatchRecordDTO(System.currentTimeMillis(), null, null, seedDTO, matchedProductDTO));
        pushMessageReceiverDTOList.add(pushMessageReceiverDTO);
      }
    }
    if(CollectionUtil.isNotEmpty(pushMessageReceiverDTOList)){
      PushMessageDTO pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setShopId(taskDTO.getShopId());//消息的触发者
      pushMessageDTO.setCreatorType(OperatorType.SHOP);
      pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
      pushMessageDTO.setRelatedObjectId(taskDTO.getSeedId());
      pushMessageDTO.setCreateTime(taskDTO.getCreateTime());

      if(PushMessageScene.ACCESSORY_PROMOTIONS.equals(taskDTO.getScene())){
        pushMessageDTO.setType(PushMessageType.ACCESSORY_PROMOTIONS);
        pushMessageDTO.setLevel(PushMessageLevel.LOW);
        pushMessageDTO.setTitle(PushMessagePromptTemplate.ACCESSORY_PROMOTIONS_TITLE);
        pushMessageDTO.setPromptContent(String.format(PushMessagePromptTemplate.ACCESSORY_PROMOTIONS_PROMPT_CONTENT, seedDTO.getProductInfo(), StringUtils.defaultIfEmpty(currentPromotionsDTO.getName(), "促销"), seedShopDTO.getAreaName(),seedShopDTO.getName()));
        pushMessageDTO.setEndDate((currentPromotionsDTO.getEndTime()==null || currentPromotionsDTO.getEndTime()>ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()))?ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()):currentPromotionsDTO.getEndTime());
        Map<String,String> params = new HashMap<String, String>();
        params.put(PushMessageParamsKeyConstant.ProductShopId, seedShopDTO.getId().toString());
        params.put(PushMessageParamsKeyConstant.ProductLocalInfoId, seedDTO.getProductLocalInfoId().toString());
        params.put(PushMessageParamsKeyConstant.ShopId, seedShopDTO.getId().toString());
        pushMessageDTO.setParams(JsonUtil.mapToJson(params));

        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setShopDTO(seedShopDTO);
        pushMessageVelocityContext.setProductDTO(seedDTO);
        List<PromotionsRule> ruleList=promotionsService.getPromotionsRuleByPromotionsIds(seedDTO.getShopId(),currentPromotionsDTO.getId());
        List<PromotionsRuleDTO> ruleDTOs=new ArrayList<PromotionsRuleDTO>();
        if(CollectionUtil.isNotEmpty(ruleList)){
          for(PromotionsRule rule:ruleList){
            if(rule==null) continue;
            ruleDTOs.add(rule.toDTO());
          }
        }
        currentPromotionsDTO.setName(StringUtils.defaultIfEmpty(currentPromotionsDTO.getName(), "促销"));
        currentPromotionsDTO.setPromotionsRuleDTOList(ruleDTOs);
        currentPromotionsDTO.setPromotionsContent(currentPromotionsDTO.generatePromotionsContent());
        pushMessageVelocityContext.setPromotionsDTO(currentPromotionsDTO);

        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_PROMOTIONS_CONTENT, "ACCESSORY_PROMOTIONS_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_PROMOTIONS_CONTENT_TEXT, "ACCESSORY_PROMOTIONS_CONTENT_TEXT");
        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);

      }else if(PushMessageScene.ACCESSORY_SALES.equals(taskDTO.getScene())){
        pushMessageDTO.setLevel(PushMessageLevel.LOW);
        pushMessageDTO.setTitle(PushMessagePromptTemplate.ACCESSORY_SALES_TITLE);
        pushMessageDTO.setPromptContent(String.format(PushMessagePromptTemplate.ACCESSORY_SALES_PROMPT_CONTENT, seedShopDTO.getAreaName(),seedShopDTO.getName(), seedDTO.getProductInfo()));
        pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
        pushMessageDTO.setType(PushMessageType.ACCESSORY);
        Map<String,String> params = new HashMap<String, String>();
        params.put(PushMessageParamsKeyConstant.ProductShopId, seedShopDTO.getId().toString());
        params.put(PushMessageParamsKeyConstant.ProductLocalInfoId, seedDTO.getProductLocalInfoId().toString());
        params.put(PushMessageParamsKeyConstant.ShopId, seedShopDTO.getId().toString());
        pushMessageDTO.setParams(JsonUtil.mapToJson(params));

        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setShopDTO(seedShopDTO);
        pushMessageVelocityContext.setProductDTO(seedDTO);
        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_SALES_CONTENT, "ACCESSORY_SALES_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_SALES_CONTENT_TEXT, "ACCESSORY_SALES_CONTENT_TEXT");
        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);

      }

      PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
      pushMessageSourceDTO.setSourceId(seedDTO.getProductLocalInfoId());
      pushMessageSourceDTO.setCreateTime(taskDTO.getCreateTime());
      pushMessageSourceDTO.setShopId(seedShopDTO.getId());//消息内容来源
      pushMessageSourceDTO.setType(PushMessageSourceType.PRODUCT);
      pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

      pushMessageDTO.setPushMessageReceiverDTOList(pushMessageReceiverDTOList);

      Long pushMessageId = pushMessageService.createPushMessage(pushMessageDTO,false);

      //创建匹配结果信息
      pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setShopId(taskDTO.getShopId());//消息的触发者
      pushMessageDTO.setCreatorType(OperatorType.SHOP);
      pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
      pushMessageDTO.setRelatedObjectId(taskDTO.getSeedId());
      pushMessageDTO.setType(PushMessageType.ACCESSORY_MATCH_RESULT);
      Map<String,String> params = new HashMap<String, String>();
      params.put(PushMessageParamsKeyConstant.ProductShopId,seedDTO.getShopId().toString());
      params.put(PushMessageParamsKeyConstant.ProductLocalInfoId,seedDTO.getProductLocalInfoId().toString());
      pushMessageDTO.setParams(JsonUtil.mapToJson(params));

      pushMessageDTO.setLevel(PushMessageLevel.HIGH);
      if(PushMessageScene.ACCESSORY_PROMOTIONS.equals(taskDTO.getScene())){
        pushMessageDTO.setTitle(PushMessagePromptTemplate.ACCESSORY_PROMOTIONS_MATCH_RESULT_TITLE);
        pushMessageDTO.setPromptContent(String.format(PushMessagePromptTemplate.ACCESSORY_PROMOTIONS_MATCH_RESULT_PROMPT_CONTENT, seedDTO.getProductInfo(), StringUtils.defaultIfEmpty(currentPromotionsDTO.getName(), "促销"), pushMessageReceiverDTOList.size()));

        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setProductDTO(seedDTO);
        List<PromotionsRule> ruleList=promotionsService.getPromotionsRuleByPromotionsIds(seedDTO.getShopId(),currentPromotionsDTO.getId());
        List<PromotionsRuleDTO> ruleDTOs=new ArrayList<PromotionsRuleDTO>();
        if(CollectionUtil.isNotEmpty(ruleList)){
          for(PromotionsRule rule:ruleList){
            if(rule==null) continue;
            ruleDTOs.add(rule.toDTO());
          }
        }
        currentPromotionsDTO.setName(StringUtils.defaultIfEmpty(currentPromotionsDTO.getName(), "促销"));
        currentPromotionsDTO.setPromotionsRuleDTOList(ruleDTOs);
        currentPromotionsDTO.setPromotionsContent(currentPromotionsDTO.generatePromotionsContent());
        pushMessageVelocityContext.setPromotionsDTO(currentPromotionsDTO);
        pushMessageVelocityContext.setPushCount(pushMessageReceiverDTOList.size());
        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_PROMOTIONS_MATCH_RESULT_CONTENT, "ACCESSORY_PROMOTIONS_MATCH_RESULT_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_PROMOTIONS_MATCH_RESULT_CONTENT_TEXT, "ACCESSORY_PROMOTIONS_MATCH_RESULT_CONTENT_TEXT");

        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);

      }else if(PushMessageScene.ACCESSORY_SALES.equals(taskDTO.getScene())){
        pushMessageDTO.setTitle(PushMessagePromptTemplate.ACCESSORY_MATCH_RESULT_TITLE);
        pushMessageDTO.setPromptContent(String.format(PushMessagePromptTemplate.ACCESSORY_MATCH_RESULT_PROMPT_CONTENT, seedDTO.getProductInfo(), pushMessageReceiverDTOList.size()));

        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setProductDTO(seedDTO);
        pushMessageVelocityContext.setPushCount(pushMessageReceiverDTOList.size());
        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_MATCH_RESULT_CONTENT, "ACCESSORY_MATCH_RESULT_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.ACCESSORY_MATCH_RESULT_CONTENT_TEXT, "ACCESSORY_MATCH_RESULT_CONTENT_TEXT");

        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);
      }

      pushMessageDTO.setCreateTime(taskDTO.getCreateTime());
      pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));


      pushMessageSourceDTO = new PushMessageSourceDTO();
      pushMessageSourceDTO.setSourceId(pushMessageId);
      pushMessageSourceDTO.setCreateTime(taskDTO.getCreateTime());
      pushMessageSourceDTO.setShopId(seedShopDTO.getId());//消息内容来源
      pushMessageSourceDTO.setType(PushMessageSourceType.PUSH_MESSAGE);
      pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

      pushMessageDTO.setCurrentPushMessageReceiverDTO(new PushMessageReceiverDTO(seedShopDTO.getId(),seedShopDTO.getShopKind(),seedShopDTO.getId(), OperatorType.SHOP));
      pushMessageService.createPushMessage(pushMessageDTO,false);
    }
  }


  private ProductDTO matchDataResourceColl(ProductDTO seedDTO, Collection<ProductDTO> dataResourceColl) throws Exception {
    if(CollectionUtils.isNotEmpty(dataResourceColl)){
      double score =0;
      for(ProductDTO data :dataResourceColl){
        score = SolrUtil.getImitateSolrMatchScore(data.getName(), seedDTO.getName(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
        if(score==0d) continue;
        score = SolrUtil.getImitateSolrMatchScore(data.getBrand(), seedDTO.getBrand(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
        if(score==0d) continue;
        score = SolrUtil.getImitateSolrMatchScore(data.generateCustomMatchPVContent(), seedDTO.generateCustomMatchPVContent(), SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE);
        if(score==0d) continue;
        return data;
      }
    }
    return null;
  }

  /**
   * 种子匹配中的数据进行 自定义的过滤排序 后 得到推送消息的数据源，生成消息推送给种子店铺
   * @param taskDTO
   * @param shopDTOMap
   * @throws Exception
   */
  private void generatePreBuyMatchAccessoryPushMessageDTO(PushMessageBuildTaskDTO taskDTO, Map<Long, ShopDTO> shopDTOMap) throws Exception {
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO seedShopDTO = shopDTOMap.get(taskDTO.getShopId());

    IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
    PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderService.getPreBuyOrderItemDTOById(taskDTO.getSeedId());

    int preBuyHardMatchAccessoryPushMessageCount = NumberUtil.intValue(configService.getConfig(ConfigConstant.PRE_BUY_HARD_MATCH_ACCESSORY_PUSH_MESSAGE_COUNT, ConfigConstant.CONFIG_SHOP_ID),0);

    if(preBuyHardMatchAccessoryPushMessageCount<=0) return;

    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSalesStatus(ProductStatus.InSales);
    searchConditionDTO.setShopKind(seedShopDTO.getShopKind());
    searchConditionDTO.setExcludeShopIds(new Long[]{seedShopDTO.getId()});//除去自己店铺的
    searchConditionDTO.setMaxRows(preBuyHardMatchAccessoryPushMessageCount * 2);
//    searchConditionDTO.setCustomMatchPContent(preBuyOrderItemDTO.generateCustomMatchPContent());
    ISolrMatchStopWordService solrMatchStopWordService = ServiceManager.getService(ISolrMatchStopWordService.class);
    List<String> solrMatchStopWordList = solrMatchStopWordService.getSolrMatchStopWordList();

    searchConditionDTO.setProductName(StringUtil.filerStopWords(preBuyOrderItemDTO.getProductName(),solrMatchStopWordList));
    searchConditionDTO.setProductBrand(StringUtil.filerStopWords(preBuyOrderItemDTO.getBrand(),solrMatchStopWordList));

    searchConditionDTO.setCustomMatchPVContent(preBuyOrderItemDTO.generateCustomMatchPVContent());
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    ProductSearchResultListDTO searchResultListDTO = searchProductService.queryAccessoryRecommend(true,searchConditionDTO);

    double productMatchScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_MATCH_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    double productPriceScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_PRICE_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    double productAreaScale = NumberUtil.doubleValue(configService.getConfig(ConfigConstant.PRODUCT_AREA_SCALE, ConfigConstant.CONFIG_SHOP_ID), 0d);
    this.filterCustomMatchAccessoryList(productMatchScale, productPriceScale, productAreaScale, searchResultListDTO, searchConditionDTO, seedShopDTO, shopDTOMap);


    String matchingRule = String.format("%s*x+%s*y+%s*z", productMatchScale, productPriceScale, productAreaScale);


    if(CollectionUtils.isNotEmpty(searchResultListDTO.getProducts())){
      Collections.sort(searchResultListDTO.getProducts(), new Comparator<ProductDTO>() {
        public int compare(ProductDTO arg0, ProductDTO arg1) {
          return arg1.getCustomScore().compareTo(arg0.getCustomScore());
        }
      });
      int count =0;
      ShopDTO sourceShopDTO = null;
      for(ProductDTO productDTO:searchResultListDTO.getProducts()){
        if(count>=preBuyHardMatchAccessoryPushMessageCount){
          break;
        }
        sourceShopDTO = shopDTOMap.get(productDTO.getShopId());
        PushMessageDTO pushMessageDTO = new PushMessageDTO();
        pushMessageDTO.setShopId(taskDTO.getShopId());//消息的触发者
        pushMessageDTO.setCreatorType(OperatorType.SHOP);
        pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
        pushMessageDTO.setRelatedObjectId(taskDTO.getSeedId());

        Map<String,String> params = new HashMap<String, String>();
        params.put(PushMessageParamsKeyConstant.ProductShopId, sourceShopDTO.getId().toString());
        params.put(PushMessageParamsKeyConstant.ProductLocalInfoId, productDTO.getProductLocalInfoId().toString());
        params.put(PushMessageParamsKeyConstant.ShopId, sourceShopDTO.getId().toString());
        pushMessageDTO.setParams(JsonUtil.mapToJson(params));

        pushMessageDTO.setType(PushMessageType.BUYING_MATCH_ACCESSORY);

        pushMessageDTO.setLevel(PushMessageLevel.NORMAL);
        pushMessageDTO.setTitle(PushMessagePromptTemplate.PRE_BUY_ORDER_ACCESSORY_TITLE);
        pushMessageDTO.setPromptContent(String.format(PushMessagePromptTemplate.PRE_BUY_ORDER_ACCESSORY_PROMPT_CONTENT, sourceShopDTO.getAreaName(), productDTO.getProductInfo()));

        PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setShopDTO(sourceShopDTO);
        pushMessageVelocityContext.setProductDTO(productDTO);
        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.PRE_BUY_ORDER_ACCESSORY_CONTENT, "PRE_BUY_ORDER_ACCESSORY_CONTENT");
        String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.PRE_BUY_ORDER_ACCESSORY_CONTENT_TEXT, "PRE_BUY_ORDER_ACCESSORY_CONTENT_TEXT");

        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);

        pushMessageDTO.setCreateTime(taskDTO.getCreateTime());
        pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));


        PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
        pushMessageSourceDTO.setSourceId(productDTO.getProductLocalInfoId());
        pushMessageSourceDTO.setCreateTime(taskDTO.getCreateTime());
        pushMessageSourceDTO.setShopId(sourceShopDTO.getId());//消息内容来源
        pushMessageSourceDTO.setType(PushMessageSourceType.PRODUCT);
        pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

        PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO(seedShopDTO.getId(), seedShopDTO.getShopKind(),seedShopDTO.getId(), OperatorType.SHOP);

        PushMessageReceiverMatchRecordDTO pushMessageReceiverMatchRecordDTO = new PushMessageReceiverMatchRecordDTO(System.currentTimeMillis(),matchingRule,productDTO.getCustomScore(),new ProductDTO(seedShopDTO.getId(),preBuyOrderItemDTO),productDTO);
        pushMessageReceiverDTO.setPushMessageReceiverMatchRecordDTO(pushMessageReceiverMatchRecordDTO);
        pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);
        pushMessageService.createPushMessage(pushMessageDTO,false);
      }
    }else{
      LOG.debug("PushMessageBuildTaskDTO["+taskDTO.getId()+"] 根据种子没有匹配到数据,所以没有生成推送消息!");
    }
  }

  /**
   * 种子匹配中的数据店铺  代表需要推送消息的店铺  然后把种子做为消息的数据源生成消息
   * @param taskDTO
   * @param shopDTOMap
   * @throws Exception
   */
  private PushMessageDTO generatePreBuyPushMessage(PushMessageBuildTaskDTO taskDTO, Map<Long, ShopDTO> shopDTOMap) throws Exception {
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
    ShopDTO seedShopDTO = shopDTOMap.get(taskDTO.getShopId());
    PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderService.getPreBuyOrderItemDTOById(taskDTO.getSeedId());
    PreBuyOrderDTO preBuyOrderDTO = preBuyOrderService.getSimplePreBuyOrderDTOById(preBuyOrderItemDTO.getPreBuyOrderId());

    ProductDTO seedDTO = new ProductDTO(seedShopDTO.getId(),preBuyOrderItemDTO);

    Map<String,ProductDTO> dataResourceMap = null;
    List<PushMessageReceiverDTO> pushMessageReceiverDTOList = new ArrayList<PushMessageReceiverDTO>();
    //配件库中商品
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);

    Map<Long,ProductDTO> matchedProductDTOMap = new HashMap<Long, ProductDTO>();
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setMaxRows(1);
//    searchConditionDTO.setCustomMatchPContent(preBuyOrderItemDTO.generateCustomMatchPContent());
    ISolrMatchStopWordService solrMatchStopWordService = ServiceManager.getService(ISolrMatchStopWordService.class);
    List<String> solrMatchStopWordList = solrMatchStopWordService.getSolrMatchStopWordList();

    searchConditionDTO.setProductName(StringUtil.filerStopWords(preBuyOrderItemDTO.getProductName(),solrMatchStopWordList));
    searchConditionDTO.setProductBrand(StringUtil.filerStopWords(preBuyOrderItemDTO.getBrand(),solrMatchStopWordList));
    searchConditionDTO.setCustomMatchPVContent(preBuyOrderItemDTO.generateCustomMatchPVContent());

    //先用solr  如果返回为空  再 根据 注册填写的  和 经营范围  匹配
    for(ShopDTO pushShopDTO : shopDTOMap.values()){
      if(seedShopDTO.getId().equals(pushShopDTO.getId()) || !ConfigUtils.isWholesalerVersion(pushShopDTO.getShopVersionId()) || !seedShopDTO.getShopKind().equals(pushShopDTO.getShopKind())) continue;

      dataResourceMap = new HashMap<String, ProductDTO>();
      addDataResourceByShopBusinessScope(dataResourceMap, pushShopDTO);
      addDataResourceByRegisterProduct(dataResourceMap, pushShopDTO);

      ProductDTO matchedProductDTO = matchDataResourceColl(seedDTO, dataResourceMap.values());
      if(matchedProductDTO==null){
        searchConditionDTO.setShopId(pushShopDTO.getId());
        ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryAccessoryRecommend(true,searchConditionDTO);
        if(CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())){
          matchedProductDTO = productSearchResultListDTO.getProducts().get(0);
          matchedProductDTOMap.put(matchedProductDTO.getShopId(),matchedProductDTO);
        }
      }
      if(matchedProductDTO!=null){
        matchedProductDTOMap.put(matchedProductDTO.getShopId(),matchedProductDTO);
      }
    }

    ShopDTO pushShopDTO = null;
    for(Map.Entry<Long,ProductDTO> entry : matchedProductDTOMap.entrySet()){
      pushShopDTO = shopDTOMap.get(entry.getKey());
      PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO(pushShopDTO.getId(),pushShopDTO.getShopKind(),pushShopDTO.getId(), OperatorType.SHOP);
      pushMessageReceiverDTO.setPushMessageReceiverMatchRecordDTO(new PushMessageReceiverMatchRecordDTO(System.currentTimeMillis(), null, null, seedDTO, entry.getValue()));
      pushMessageReceiverDTOList.add(pushMessageReceiverDTO);
    }

    if(CollectionUtils.isNotEmpty(pushMessageReceiverDTOList)){
      PushMessageType pushMessageType = null;
      String title = null,promptContent=null,content=null,contentText=null;
      PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
      pushMessageVelocityContext.setShopDTO(seedShopDTO);
      pushMessageVelocityContext.setPreBuyOrderItemDTO(preBuyOrderItemDTO);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);

      if (BusinessChanceType.Normal.equals(preBuyOrderDTO.getBusinessChanceType())) {
        pushMessageType = PushMessageType.BUYING_INFORMATION;
        title = PushMessagePromptTemplate.BUYING_INFORMATION_TITLE;
        promptContent = String.format(PushMessagePromptTemplate.BUYING_INFORMATION_PROMPT_CONTENT, seedShopDTO.getAreaName(), seedShopDTO.getName(), preBuyOrderItemDTO.getProductInfo() + " " + preBuyOrderItemDTO.getAmount() + StringUtil.formateStr(preBuyOrderItemDTO.getUnit()));
        content = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUYING_INFORMATION_CONTENT, "BUYING_INFORMATION_CONTENT");
        contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUYING_INFORMATION_CONTENT_TEXT, "BUYING_INFORMATION_CONTENT_TEXT");
      } else if (BusinessChanceType.Lack.equals(preBuyOrderDTO.getBusinessChanceType())) {
        pushMessageType = PushMessageType.BUSINESS_CHANCE_LACK;
        title = PushMessagePromptTemplate.BUSINESS_CHANCE_LACK_TITLE;
        promptContent = String.format(PushMessagePromptTemplate.BUSINESS_CHANCE_LACK_PROMPT_CONTENT, seedShopDTO.getAreaName(), seedShopDTO.getName(), preBuyOrderItemDTO.getProductInfo());
        content = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_LACK_CONTENT, "BUSINESS_CHANCE_LACK_CONTENT");
        contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_LACK_CONTENT_TEXT, "BUSINESS_CHANCE_LACK_CONTENT_TEXT");
      } else if (BusinessChanceType.SellWell.equals(preBuyOrderDTO.getBusinessChanceType())) {
        pushMessageType = PushMessageType.BUSINESS_CHANCE_SELL_WELL;
        title = PushMessagePromptTemplate.BUSINESS_CHANCE_SELL_WELL_TITLE;
        promptContent = String.format(PushMessagePromptTemplate.BUSINESS_CHANCE_SELL_WELL_PROMPT_CONTENT, seedShopDTO.getAreaName(), seedShopDTO.getName(), preBuyOrderItemDTO.getProductInfo(),preBuyOrderItemDTO.getFuzzyAmountStr() + StringUtil.formateStr(preBuyOrderItemDTO.getUnit()));
        content = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_SELL_WELL_CONTENT, "BUSINESS_CHANCE_SELL_WELL_CONTENT");
        contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUSINESS_CHANCE_SELL_WELL_CONTENT_TEXT, "BUSINESS_CHANCE_SELL_WELL_CONTENT_TEXT");
      }
      PushMessageDTO pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setShopId(taskDTO.getShopId());
      pushMessageDTO.setCreatorType(OperatorType.SHOP);
      pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
      pushMessageDTO.setRelatedObjectId(taskDTO.getSeedId());
      pushMessageDTO.setType(pushMessageType);

      Map<String,String> params = new HashMap<String, String>();
      params.put(PushMessageParamsKeyConstant.PreBuyOrderId,preBuyOrderItemDTO.getPreBuyOrderId().toString());
      params.put(PushMessageParamsKeyConstant.PreBuyOrderItemId,preBuyOrderItemDTO.getId().toString());
      params.put(PushMessageParamsKeyConstant.ShopId,seedShopDTO.getId().toString());
      pushMessageDTO.setParams(JsonUtil.mapToJson(params));

      pushMessageDTO.setLevel(PushMessageLevel.LOW);
      pushMessageDTO.setTitle(title);
      pushMessageDTO.setPromptContent(promptContent);
      pushMessageDTO.setEndDate(preBuyOrderDTO.getEndDate());

      pushMessageDTO.setContent(content);
      pushMessageDTO.setContentText(contentText);

      pushMessageDTO.setCreateTime(taskDTO.getCreateTime());

      PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
      pushMessageSourceDTO.setSourceId(preBuyOrderItemDTO.getId());
      pushMessageSourceDTO.setCreateTime(taskDTO.getCreateTime());
      pushMessageSourceDTO.setShopId(seedShopDTO.getId());
      pushMessageSourceDTO.setType(PushMessageSourceType.PRE_BUY_ORDER_ITEM);
      pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

      pushMessageDTO.setPushMessageReceiverDTOList(pushMessageReceiverDTOList);

      Long pushMessageId = pushMessageService.createPushMessage(pushMessageDTO,false);

      if(BusinessChanceType.Normal.equals(preBuyOrderDTO.getBusinessChanceType())) {
        //创建匹配结果信息
        pushMessageDTO = new PushMessageDTO();
        pushMessageDTO.setShopId(taskDTO.getShopId());//消息的触发者
        pushMessageDTO.setCreatorType(OperatorType.SHOP);
        pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
        pushMessageDTO.setRelatedObjectId(taskDTO.getSeedId());
        pushMessageDTO.setType(PushMessageType.BUYING_INFORMATION_MATCH_RESULT);
        params = new HashMap<String, String>();
        params.put(PushMessageParamsKeyConstant.PreBuyOrderId, preBuyOrderItemDTO.getPreBuyOrderId().toString());
        pushMessageDTO.setParams(JsonUtil.mapToJson(params));
        pushMessageDTO.setLevel(PushMessageLevel.HIGH);
        pushMessageDTO.setTitle(PushMessagePromptTemplate.BUYING_INFORMATION_MATCH_RESULT_TITLE);
        pushMessageDTO.setPromptContent(String.format(PushMessagePromptTemplate.BUYING_INFORMATION_MATCH_RESULT_PROMPT_CONTENT, seedDTO.getProductInfo(), pushMessageReceiverDTOList.size()));

        pushMessageVelocityContext = new PushMessageVelocityContext();
        pushMessageVelocityContext.setPreBuyOrderItemDTO(preBuyOrderItemDTO);
        pushMessageVelocityContext.setPushCount(pushMessageReceiverDTOList.size());
        context = new VelocityContext();
        context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
        content = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUYING_INFORMATION_MATCH_RESULT_CONTENT, "BUYING_INFORMATION_MATCH_RESULT_CONTENT");
        contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.BUYING_INFORMATION_MATCH_RESULT_CONTENT_TEXT, "BUYING_INFORMATION_MATCH_RESULT_CONTENT_TEXT");

        pushMessageDTO.setContent(content);
        pushMessageDTO.setContentText(contentText);

      pushMessageDTO.setCreateTime(taskDTO.getCreateTime());
      pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));


        pushMessageSourceDTO = new PushMessageSourceDTO();
        pushMessageSourceDTO.setSourceId(pushMessageId);
        pushMessageSourceDTO.setCreateTime(taskDTO.getCreateTime());
        pushMessageSourceDTO.setShopId(seedShopDTO.getId());//消息内容来源
        pushMessageSourceDTO.setType(PushMessageSourceType.PUSH_MESSAGE);
        pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

        pushMessageDTO.setCurrentPushMessageReceiverDTO(new PushMessageReceiverDTO(seedShopDTO.getId(), seedShopDTO.getShopKind(), seedShopDTO.getId(), OperatorType.SHOP));
        pushMessageService.createPushMessage(pushMessageDTO, false);
      }
      return pushMessageDTO;
    }
    return null;
  }

  @Override
  public void createQuotedOrderIgnoredPushMessage(Long sourceShopId, Long pushShopId, Long sourceId) throws Exception {
    if (sourceId == null) return;

    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Map<Long, ShopDTO> shopDTOMap = shopService.getShopByShopIds(sourceShopId, pushShopId);
    ShopDTO sourceShop = shopDTOMap.get(sourceShopId);
    ShopDTO pushShop = shopDTOMap.get(pushShopId);

    if (sourceShop == null || pushShop == null) {
      LOG.error("get shop by ids[{},{}] is null.", sourceShop, pushShop);
      return;
    }
    sourceShop.setAreaName(configService.getShopAreaInfoByShopDTO(sourceShop));
    pushShop.setAreaName(configService.getShopAreaInfoByShopDTO(pushShop));
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setShopId(sourceShop.getId());
    pushMessageDTO.setCreatorType(OperatorType.SHOP);
    pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.setLevel(PushMessageLevel.NORMAL);
    pushMessageDTO.setType(PushMessageType.QUOTED_BUYING_IGNORED);
    pushMessageDTO.setTitle(PushMessagePromptTemplate.QUOTED_BUYING_INFORMATION_IGNORED_TITLE);
    pushMessageDTO.setPromptContent(String.format(PushMessagePromptTemplate.QUOTED_BUYING_INFORMATION_IGNORED_PROMPT_CONTENT, sourceShop.getAreaName(), sourceShop.getName()));

    Map<String,String> params = new HashMap<String, String>();
    params.put(PushMessageParamsKeyConstant.QuotedPreBuyOrderItemId,sourceId.toString());
    params.put(PushMessageParamsKeyConstant.ShopId,sourceShop.getId().toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(params));

    PushMessageVelocityContext pushMessageVelocityContext = new PushMessageVelocityContext();
    pushMessageVelocityContext.setShopDTO(sourceShop);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, pushMessageVelocityContext);
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.QUOTED_BUYING_INFORMATION_IGNORED_CONTENT, "BUYING_INFORMATION_MATCH_RESULT_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.QUOTED_BUYING_INFORMATION_IGNORED_CONTENT_TEXT, "BUYING_INFORMATION_MATCH_RESULT_CONTENT_TEXT");

    pushMessageDTO.setContent(content);
    pushMessageDTO.setContentText(contentText);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setShopId(pushShop.getId());
    pushMessageReceiverDTO.setReceiverId(pushShop.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    pushMessageReceiverDTO.setShopKind(pushShop.getShopKind());
    pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(sourceId);
    pushMessageSourceDTO.setShopId(sourceShop.getId());
    pushMessageSourceDTO.setType(PushMessageSourceType.QUOTED_PRE_BUY_ORDER_ITEM);
    pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

    pushMessageService.createPushMessage(pushMessageDTO,true);
  }

  @Override
  public boolean generatePromotionMsgTask(Long shopId, Long... productIdArr){
    if(ArrayUtil.isEmpty(productIdArr)){
      return false;
    }
    try {
      List<PushMessageBuildTaskDTO> pushMessageBuildTaskDTOList = new ArrayList<PushMessageBuildTaskDTO>();
      PushMessageBuildTaskDTO pushMessageBuildTaskDTO = null;
      for(Long productId : productIdArr){
        pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
        pushMessageBuildTaskDTO.setSeedId(productId);
        pushMessageBuildTaskDTO.setShopId(shopId);
        pushMessageBuildTaskDTO.setScene(PushMessageScene.ACCESSORY_PROMOTIONS);
        pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
        pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);
      }
      this.savePushMessageBuildTaskDTO(pushMessageBuildTaskDTOList.toArray(new PushMessageBuildTaskDTO[pushMessageBuildTaskDTOList.size()]));
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return true;
  }

  @Override
  public boolean generateSalesMsgTask(Long shopId,Long... productIdArr){
    if(ArrayUtil.isEmpty(productIdArr)){
      return false;
    }
    try {
      List<PushMessageBuildTaskDTO> pushMessageBuildTaskDTOList = new ArrayList<PushMessageBuildTaskDTO>();
      PushMessageBuildTaskDTO pushMessageBuildTaskDTO = null;
      for(Long productId : productIdArr){
        pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
        pushMessageBuildTaskDTO.setSeedId(productId);
        pushMessageBuildTaskDTO.setShopId(shopId);
        pushMessageBuildTaskDTO.setScene(PushMessageScene.ACCESSORY_SALES);
        pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
        pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);
      }
      this.savePushMessageBuildTaskDTO(pushMessageBuildTaskDTOList.toArray(new PushMessageBuildTaskDTO[pushMessageBuildTaskDTOList.size()]));
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return true;
  }
}
