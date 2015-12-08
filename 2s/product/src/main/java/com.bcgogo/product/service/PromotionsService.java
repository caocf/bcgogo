package com.bcgogo.product.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.PromotionOrderRecordQuery;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.*;
import com.bcgogo.product.productManage.PromotionSearchCondition;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.utils.*;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-1-7
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PromotionsService implements IPromotionsService {
  private static final Logger LOG = LoggerFactory.getLogger(IPromotionsService.class);
  @Autowired
  private ProductDaoManager productDaoManager;



  @Override
  public Map<Long,List<PromotionsDTO>> getPromotionsDTOMapByProductLocalInfoId(Long shopId,boolean unexpired, Long... productLocalInfoId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    if(ArrayUtils.isEmpty(productLocalInfoId) || shopId==null) return new HashMap<Long,List<PromotionsDTO>>();

    List<Object[]> result = writer.getPromotionsByProductLocalInfoIds(shopId, productLocalInfoId);
    if(CollectionUtils.isEmpty(result)) return new HashMap<Long, List<PromotionsDTO>>();

    List<PromotionsRule> promotionsRuleList = writer.getPromotionsRulesByProductLocalInfoIds(shopId, productLocalInfoId);
    Map<Long, List<PromotionsRuleDTO>> promotionsRuleDTOMap =  new HashMap<Long, List<PromotionsRuleDTO>>();

    if(CollectionUtils.isNotEmpty(promotionsRuleList)){
      List<PromotionsRuleDTO> promotionsRuleDTOList = null;
      for(PromotionsRule promotionsRule : promotionsRuleList){
        promotionsRuleDTOList = promotionsRuleDTOMap.get(promotionsRule.getPromotionsId());
        if(promotionsRuleDTOList == null){
          promotionsRuleDTOList = new ArrayList<PromotionsRuleDTO>();
        }
        PromotionsRuleDTO ruleDTO=promotionsRule.toDTO();
        List<PromotionsRuleMJS> ruleMJSs=getPromotionsRuleMJSByRuleIds(shopId, promotionsRule.getId());
        List<PromotionsRuleMJSDTO> mjsdtos=new ArrayList<PromotionsRuleMJSDTO>();
        if(CollectionUtil.isNotEmpty(ruleMJSs)){
          for(PromotionsRuleMJS ruleMJS:ruleMJSs){
            if(ruleMJS==null) continue;
            if(PromotionsEnum.GiftType.GIFT.equals(ruleMJS.getGiftType())){
              ruleDTO.setGiveGiftFlag(true);
            }else if(PromotionsEnum.GiftType.DEPOSIT.equals(ruleMJS.getGiftType())){
              ruleDTO.setGiveDepositFlag(true);
            }
            mjsdtos.add(ruleMJS.toDTO());
          }
        }
        ruleDTO.setPromotionsRuleMJSDTOs(mjsdtos);
        promotionsRuleDTOList.add(ruleDTO);
        promotionsRuleDTOMap.put(promotionsRule.getPromotionsId(),promotionsRuleDTOList);
      }
    }
    Map<Long, List<PromotionsProductDTO>> pMap =  new HashMap<Long, List<PromotionsProductDTO>>();
    for (Object[] obj : result) {
      PromotionsProduct promotionsProduct = (PromotionsProduct) obj[1];
      List<PromotionsProductDTO> promotionsProductDTOs=pMap.get(promotionsProduct.getPromotionsId());
      if(promotionsProductDTOs==null){
        promotionsProductDTOs = new ArrayList<PromotionsProductDTO>();
        pMap.put(promotionsProduct.getPromotionsId(),promotionsProductDTOs);
      }
      promotionsProductDTOs.add(promotionsProduct.toDTO());
    }

    Map<Long,List<PromotionsDTO>> promotionsDTOMap =  new HashMap<Long,List<PromotionsDTO>>();
    Promotions promotions = null;
    PromotionsProduct promotionsProduct = null;
    for (Object[] obj : result) {
      if (obj != null && obj.length == 2) {
        promotions = (Promotions) obj[0];
        // endtime为空的是无限期的   这里过滤过期的
//        if (unexpired && promotions.getEndTime() != null && promotions.getEndTime() < DateUtil.getToday(DateUtil.DATE_STRING_FORMAT_DAY, new Date())) {
//          continue;
//        }
        if(PromotionsEnum.PromotionStatus.EXPIRE.equals(promotions.getStatus())||      //过期了
          ( promotions.getEndTime() != null && promotions.getEndTime() < DateUtil.getToday(DateUtil.DATE_STRING_FORMAT_DAY, new Date()))){
          continue;
        }
        promotionsProduct = (PromotionsProduct) obj[1];
        if(promotionsProduct!=null&&DeletedType.TRUE.equals(promotionsProduct.getDeleted())){
          continue;
        }
        PromotionsDTO promotionsDTO = promotions.toDTO();
        promotionsDTO.setPromotionsRuleDTOList(promotionsRuleDTOMap.get(promotions.getId()));
        List<PromotionsProductDTO> promotionsProductDTOs=pMap.get(promotions.getId());
        if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
          promotionsDTO.setPromotionsProductDTOList(promotionsProductDTOs.toArray(new PromotionsProductDTO[promotionsProductDTOs.size()]));
        }
        if(promotionsDTOMap.get(promotionsProduct.getProductLocalInfoId())==null){
          List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
          promotionsDTOMap.put(promotionsProduct.getProductLocalInfoId(),promotionsDTOs);
        }
        promotionsDTOMap.get(promotionsProduct.getProductLocalInfoId()).add(promotionsDTO);
      }
    }
    //过滤掉多余的
    for(Long productId:promotionsDTOMap.keySet()){
      List<PromotionsDTO> promotionsDTOList= promotionsDTOMap.get(productId);
      for(PromotionsDTO dto:promotionsDTOList){
        List<PromotionsProductDTO>  promotionsProductDTOList=new ArrayList<PromotionsProductDTO>();
        for(PromotionsProductDTO promotionsProductDTO:dto.getPromotionsProductDTOList()){
          if(productId.equals(promotionsProductDTO.getProductLocalInfoId())){
            promotionsProductDTOList.add(promotionsProductDTO);
          }
        }
        dto.setPromotionsProductDTOList(promotionsProductDTOList.toArray(new PromotionsProductDTO[promotionsProductDTOList.size()]));
      }
    }

    return promotionsDTOMap;
  }

  public Map<Long,ProductDTO>  getProductPromotionDetail(Long shopId,Long[] productIdArr) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    if(ArrayUtils.isEmpty(productIdArr) || shopId==null) {
      return null;
    }
    List<Object[]> result = writer.getPromotionsByProductLocalInfoIds(shopId, productIdArr);
    if(CollectionUtils.isEmpty(result)) return null;
    //组装pp
    List<Long> ppIdList=new ArrayList<Long>();
    Map<Long, List<PromotionsProductDTO>> ppMap =  new HashMap<Long, List<PromotionsProductDTO>>();
    for (Object[] obj : result) {
      PromotionsProduct promotionsProduct = (PromotionsProduct) obj[1];
      List<PromotionsProductDTO> promotionsProductDTOs=ppMap.get(promotionsProduct.getPromotionsId());
      if(promotionsProductDTOs==null){
        promotionsProductDTOs = new ArrayList<PromotionsProductDTO>();
        ppMap.put(promotionsProduct.getPromotionsId(),promotionsProductDTOs);
      }
      promotionsProductDTOs.add(promotionsProduct.toDTO());
    }
    //组装rule
    List<PromotionsRule> promotionsRuleList = writer.getPromotionsRulesByProductLocalInfoIds(shopId,productIdArr);
    Map<Long, List<PromotionsRuleDTO>> promotionsRuleDTOMap =  new HashMap<Long, List<PromotionsRuleDTO>>();
    if(CollectionUtils.isNotEmpty(promotionsRuleList)){
      List<PromotionsRuleDTO> promotionsRuleDTOList = null;
      for(PromotionsRule promotionsRule : promotionsRuleList){
        promotionsRuleDTOList = promotionsRuleDTOMap.get(promotionsRule.getPromotionsId());
        if(promotionsRuleDTOList == null){
          promotionsRuleDTOList = new ArrayList<PromotionsRuleDTO>();
          promotionsRuleDTOMap.put(promotionsRule.getPromotionsId(),promotionsRuleDTOList);
        }
        PromotionsRuleDTO ruleDTO=promotionsRule.toDTO();
        promotionsRuleDTOList.add(ruleDTO);
        List<PromotionsRuleMJS> ruleMJSs=getPromotionsRuleMJSByRuleIds(shopId, promotionsRule.getId());
        List<PromotionsRuleMJSDTO> mjsdtos=new ArrayList<PromotionsRuleMJSDTO>();
        if(CollectionUtil.isNotEmpty(ruleMJSs)){
          for(PromotionsRuleMJS ruleMJS:ruleMJSs){
            if(ruleMJS==null) continue;
            mjsdtos.add(ruleMJS.toDTO());
          }
        }
        ruleDTO.setPromotionsRuleMJSDTOs(mjsdtos);
      }
    }
    //组装promotion
    Map<Long,List<PromotionsDTO>> promotionsDTOMap =  new HashMap<Long,List<PromotionsDTO>>();
    Promotions promotions = null;
    PromotionsProduct promotionsProduct = null;
    PromotionsDTO promotionsDTO = null;
    for (Object[] obj : result) {
      if (obj == null || obj.length != 2) {
        continue;
      }
      promotions = (Promotions) obj[0];
      if(PromotionsEnum.PromotionStatus.EXPIRE.equals(promotions.getStatus())||      //过期了
        ( promotions.getEndTime() != null && promotions.getEndTime() < DateUtil.getToday(DateUtil.DATE_STRING_FORMAT_DAY, new Date()))){
        continue;
      }
      promotionsProduct = (PromotionsProduct) obj[1];
      if(promotionsProduct!=null&&DeletedType.TRUE.equals(promotionsProduct.getDeleted())){
        continue;
      }
      promotionsDTO = promotions.toDTO();
      promotionsDTO.setPromotionsRuleDTOList(promotionsRuleDTOMap.get(promotions.getId()));
      List<PromotionsProductDTO> promotionsProductDTOs=ppMap.get(promotions.getId());
      if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
        promotionsDTO.setPromotionsProductDTOList(promotionsProductDTOs.toArray(new PromotionsProductDTO[promotionsProductDTOs.size()]));
      }
      List<PromotionsDTO> promotionsDTOs= promotionsDTOMap.get(promotionsProduct.getProductLocalInfoId());
      if(promotionsDTOs==null){
        promotionsDTOs=new ArrayList<PromotionsDTO>();
        promotionsDTOMap.put(promotionsProduct.getProductLocalInfoId(),promotionsDTOs);
      }
      promotionsDTOs.add(promotionsDTO);
    }
    //过滤掉多余的
    List<PromotionsProductDTO>  promotionsProductDTOList=null;
    for(Long key:promotionsDTOMap.keySet()){
      List<PromotionsDTO> promotionsDTOList= promotionsDTOMap.get(key);
      for(PromotionsDTO dto:promotionsDTOList){
        for(PromotionsProductDTO promotionsProductDTO:dto.getPromotionsProductDTOList()){
          promotionsProductDTOList=new ArrayList<PromotionsProductDTO>();
          if(key.equals(promotionsProductDTO.getProductLocalInfoId())){
            promotionsProductDTOList.add(promotionsProductDTO);
          }
          dto.setPromotionsProductDTOList(promotionsProductDTOList.toArray(new PromotionsProductDTO[promotionsProductDTOList.size()]));
        }
      }
    }

    Map<Long,ProductDTO> pMap=new HashMap<Long, ProductDTO>();
    for(Long productId:promotionsDTOMap.keySet()){
      ProductDTO productDTO=new ProductDTO();
      productDTO.setProductLocalInfoId(productId);
      productDTO.setPromotionsDTOs(promotionsDTOMap.get(productId));
      pMap.put(productId,productDTO);
    }
    return pMap;
  }

  @Override
  public ProductDTO[] addPromotionInfoToProductDTO(ProductDTO... productDTOs) throws Exception {
    List<PromotionsEnum.PromotionStatus> statusList=new ArrayList<PromotionsEnum.PromotionStatus>();
    statusList.add(PromotionsEnum.PromotionStatus.USING);
    statusList.add(PromotionsEnum.PromotionStatus.UN_STARTED);
    return addPromotionToProductDTO(statusList,productDTOs);
  }

  @Override
  public ProductDTO[] addUsingPromotionToProductDTO(ProductDTO... productDTOs) throws Exception {
    List<PromotionsEnum.PromotionStatus> statusList=new ArrayList<PromotionsEnum.PromotionStatus>();
    statusList.add(PromotionsEnum.PromotionStatus.USING);
    return addPromotionToProductDTO(statusList,productDTOs);
  }

  private ProductDTO[] addPromotionToProductDTO(List<PromotionsEnum.PromotionStatus> statusList,ProductDTO... productDTOs) throws Exception {
    if(CollectionUtil.isEmpty(statusList)||ArrayUtil.isEmpty(productDTOs)){
      return productDTOs;
    }
    Map<Long,List<Long>> productIdMap=new HashMap<Long, List<Long>>();
    Map<Long,ProductDTO> productDTOMap=new HashMap<Long, ProductDTO>();
    List<Long> productIds=null;
    for(ProductDTO productDTO:productDTOs){
      if(productDTO==null||productDTO.getProductLocalInfoId()==null){
        continue;
      }
      productDTOMap.put(productDTO.getProductLocalInfoId(),productDTO);
      productIds=productIdMap.get(productDTO.getShopId());
      if(productIds==null){
        productIds=new ArrayList<Long>();
        productIdMap.put(productDTO.getShopId(),productIds);
      }
      productIds.add(productDTO.getProductLocalInfoId());
    }
    Map<Long,List<PromotionsDTO>> pMap=null;
    ProductDTO productDTO=null;
    for (Long shopId:productIdMap.keySet()){      //分店铺组装
      pMap=getPromotionsDTOMapByProductLocalInfoId(shopId, true, ArrayUtil.toLongArr(productIdMap.get(shopId)));
      if(pMap==null||pMap.keySet().isEmpty()) continue;
      for (Long productId:pMap.keySet()){
        productDTO=productDTOMap.get(productId);
       List<PromotionsDTO> promotionsDTOs= pMap.get(productDTO.getProductLocalInfoId());
       if(CollectionUtil.isEmpty(promotionsDTOs)) {
         continue;
       }
        List<PromotionsDTO> sPromotionsDTOs=new ArrayList<PromotionsDTO>();
        for (PromotionsDTO promotionsDTO:promotionsDTOs){
             if(statusList.contains(promotionsDTO.getStatus())){
               sPromotionsDTOs.add(promotionsDTO);
             }
        }
        productDTO.setPromotionsDTOs(sPromotionsDTOs);
//        String[] titles = PromotionsUtils.genPromotionTypesStr(promotionsDTOs);
//        productDTO.setPromotionTypesShortStr(titles[0]);
//        productDTO.setPromotionTypesStr(titles[1]);
        productDTO.setPromotionTypesShortStr(PromotionsUtils.genPromotionShortTitle(sPromotionsDTOs));
        if(PromotionsUtils.hasBargain(sPromotionsDTOs)){
          productDTO.setHasBargain(true);
          productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(sPromotionsDTOs,productDTO.getInSalesPrice()));
        }
      }
    }
    return productDTOs;
  }

  public List<PromotionsDTO> getPromotionDetail(Long shopId,Long ... promotionsId){
    if(ArrayUtil.isEmpty(promotionsId)){
      return null;
    }
    PromotionIndex condition=new PromotionIndex();
    condition.setShopId(shopId);
    condition.setPromotionsIdList(promotionsId);
    return getPromotionDetail(condition);
  }

  public List<PromotionsDTO> getPromotionDetail(PromotionIndex condition){
    Long shopId=condition.getShopId();
    if(shopId==null) return null;
    List<PromotionsDTO> promotionsDTOs=getPromotionsDTO(condition);
    if(CollectionUtil.isEmpty(promotionsDTOs)){
      return null;
    }
    List<Long> promotionsIdList=new ArrayList<Long>();
    List<Long> mjsIdList=new ArrayList<Long>();
    for(PromotionsDTO dto:promotionsDTOs){
      promotionsIdList.add(dto.getId());
      if(PromotionsEnum.PromotionsTypes.MJS.equals(dto.getType())){
        mjsIdList.add(dto.getId());
      }
    }
    Map<Long,List<PromotionsRuleDTO>> ruleMap=getPromotionsRuleDTOMap(shopId,ArrayUtil.toLongArr(promotionsIdList));
    if(CollectionUtil.isNotEmpty(mjsIdList)){
      for (Long key:mjsIdList){
        List<PromotionsRuleDTO> ruleDTOs=ruleMap.get(key);
        if(CollectionUtil.isNotEmpty(ruleDTOs)){
          for(PromotionsRuleDTO ruleDTO:ruleDTOs){
            List<PromotionsRuleMJS> ruleMJSs=getPromotionsRuleMJSByRuleIds(shopId,ruleDTO.getId());
            List<PromotionsRuleMJSDTO> mjsdtos=new ArrayList<PromotionsRuleMJSDTO>();
            if(CollectionUtil.isNotEmpty(ruleMJSs)){
              for(PromotionsRuleMJS ruleMJS:ruleMJSs){
                if(ruleMJS==null) continue;
                if(PromotionsEnum.GiftType.GIFT.equals(ruleMJS.getGiftType())){
                  ruleDTO.setGiveGiftFlag(true);
                }else if(PromotionsEnum.GiftType.DEPOSIT.equals(ruleMJS.getGiftType())){
                  ruleDTO.setGiveDepositFlag(true);
                }
                mjsdtos.add(ruleMJS.toDTO());
              }
            }
            ruleDTO.setPromotionsRuleMJSDTOs(mjsdtos);
          }
        }
      }
    }
    if(ruleMap!=null&&CollectionUtil.isNotEmpty(ruleMap.keySet())){
      for(PromotionsDTO dto:promotionsDTOs){
        dto.setPromotionsRuleDTOList(ruleMap.get(dto.getId()));
      }
    }
    return promotionsDTOs;
  }

  @Override
  public void saveAllPromotionsDTO(Long shopId, PromotionsDTO promotionsDTO) throws Exception {
    if (promotionsDTO == null)
      throw new BcgogoException("promotionsDTO is null");
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    OperationTypes operationType = null;
    try {
      if (promotionsDTO.getId() != null) {//删除promotion  和 promotion_product
        Promotions promotions = writer.getById(Promotions.class, promotionsDTO.getId());
        promotions.setDeleted(DeletedType.TRUE);
        writer.update(promotions);
        writer.cancelPromotionsByPromotionsId(shopId,promotions.getId());
        promotionsDTO.setId(null);
        operationType = OperationTypes.UPDATE;
      } else {
        operationType = OperationTypes.CREATE;
      }
      Promotions promotions = new Promotions();
      promotions.fromDTO(promotionsDTO);
      writer.save(promotions);
      promotionsDTO.setId(promotions.getId());

      savePromotionsRuleDTO(writer,promotionsDTO);

      List<ProductLocalInfo> productLocalInfoList = writer.getProductInSalesAndNoPromotions(shopId);
      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
          savePromotionsProductDTO(writer, promotionsDTO, productLocalInfo.getId());
        }
      }
      writer.commit(status);
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      operationLogService.saveOperationLog(new OperationLogDTO(shopId, promotionsDTO.getUserId(), promotionsDTO.getId(), ObjectTypes.PROMOTIONS, operationType));
    } finally {
      writer.rollback(status);
    }
  }

  private void savePromotionsRuleDTO(ProductWriter writer,PromotionsDTO promotionsDTO) {
    int level = 1;
    for (PromotionsRuleDTO promotionsRuleDTO : promotionsDTO.getPromotionsRuleDTOList()) {
      if (promotionsRuleDTO.getMinAmount() != null && promotionsRuleDTO.getDiscountAmount() != null) {
        promotionsRuleDTO.setLevel(level);
        level++;
        promotionsRuleDTO.setPromotionsId(promotionsDTO.getId());
        PromotionsRule promotionsRule = new PromotionsRule();
        writer.save(promotionsRule.fromDTO(promotionsRuleDTO));
        promotionsRuleDTO.setId(promotionsRule.getId());
      }
    }
  }

  @Override
  public void saveSingleProductSinglePromotionsDTO(Long shopId, PromotionsDTO promotionsDTO,Long productLocalInfoId) throws Exception{
    if (promotionsDTO == null)
      throw new BcgogoException("promotionsDTO is null");
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    OperationTypes operationType = null;
    try {
      if (promotionsDTO.getId() != null) {
        Promotions promotions = writer.getById(Promotions.class, promotionsDTO.getId());
        promotions.setDeleted(DeletedType.TRUE);
        writer.update(promotions);
        PromotionsProduct promotionsProduct = writer.getPromotionsProductByPromotionsIdAndProductLocalInfoId(shopId,promotionsDTO.getId(),productLocalInfoId);
        promotionsProduct.setDeleted(DeletedType.TRUE);
        writer.update(promotionsProduct);
        promotionsDTO.setId(null);
        operationType = OperationTypes.UPDATE;
      } else {
        operationType = OperationTypes.CREATE;
      }
      Promotions promotions = new Promotions();
      promotions.fromDTO(promotionsDTO);
      writer.save(promotions);
      promotionsDTO.setId(promotions.getId());

      savePromotionsRuleDTO(writer, promotionsDTO);

      savePromotionsProductDTO(writer, promotionsDTO, productLocalInfoId);

      writer.commit(status);
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      operationLogService.saveOperationLog(new OperationLogDTO(shopId, promotionsDTO.getUserId(), promotionsDTO.getId(), ObjectTypes.PROMOTIONS, operationType));
    } finally {
      writer.rollback(status);
    }
  }
//  @Override
//  public void saveMultipleProductSinglePromotionsDTO(Long shopId, PromotionsDTO promotionsDTO,Long... productLocalInfoIds) throws Exception{
//    if (promotionsDTO == null)
//      throw new BcgogoException("promotionsDTO is null");
//    if(ArrayUtils.isEmpty(productLocalInfoIds)) return;
//
//    ProductWriter writer = productDaoManager.getWriter();
//    Object status = writer.begin();
//    OperationTypes operationType = null;
//    try {
//      List<Object[]> result = writer.getPromotionsByProductLocalInfoIds(shopId, productLocalInfoIds);
//      Map<Long, Promotions> promotionsMap = new HashMap<Long, Promotions>();
//      if (CollectionUtils.isNotEmpty(result)) {
//        PromotionsProduct promotionsProduct = null;
//        for (Object[] obj : result) {
//          if (obj != null && obj.length == 2) {
//            promotionsProduct = (PromotionsProduct) obj[1];
//            promotionsMap.put(promotionsProduct.getProductLocalInfoId(), (Promotions) obj[0]);
//          }
//        }
//      }
//      Promotions promotions = null;
//      for (Long productLocalInfoId : productLocalInfoIds) {
//        promotions = promotionsMap.get(productLocalInfoId);
//        if (promotions != null && PromotionsEnum.PromotionsRanges.ALL.equals(promotions.getRange())) {
//          writer.cancelPromotionsByProductLocalInfoIds(shopId, productLocalInfoId);
//          writer.deletePromotionsNoProductUsed(shopId);
//          promotions = null;
//        }
//        if (promotions != null) {
//          promotions.setDeleted(DeletedType.TRUE);
//          writer.update(promotions);
//          PromotionsProduct promotionsProduct = writer.getPromotionsProductByPromotionsIdAndProductLocalInfoId(shopId,promotionsDTO.getId(),productLocalInfoId);
//          promotionsProduct.setDeleted(DeletedType.TRUE);
//          writer.update(promotionsProduct);
//          promotionsDTO.setId(null);
//          operationType = OperationTypes.UPDATE;
//        } else {
//          operationType = OperationTypes.CREATE;
//        }
//        promotions = new Promotions();
//        promotions.fromDTO(promotionsDTO);
//        writer.save(promotions);
//        promotionsDTO.setId(promotions.getId());
//
//        savePromotionsRuleDTO(writer, promotionsDTO);
//
//        savePromotionsProductDTO(writer, promotionsDTO, productLocalInfoId);
//      }
//      writer.commit(status);
//      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
//      operationLogService.saveOperationLog(new OperationLogDTO(shopId, promotionsDTO.getUserId(), promotionsDTO.getId(), ObjectTypes.PROMOTIONS, operationType));
//    } finally {
//      writer.rollback(status);
//    }
//  }

  private void savePromotionsProductDTO(ProductWriter writer, PromotionsDTO promotionsDTO, Long productLocalInfoId) {
    PromotionsProduct promotionsProduct = new PromotionsProduct();
    promotionsProduct.setProductLocalInfoId(productLocalInfoId);
    promotionsProduct.setPromotionsId(promotionsDTO.getId());
    promotionsProduct.setShopId(promotionsDTO.getShopId());
    writer.save(promotionsProduct);
  }

  @Override
  public List<PromotionsDTO> getPromotionsDTOByRange(Long shopId,PromotionsEnum.PromotionsRanges range) throws Exception{
    ProductWriter writer = productDaoManager.getWriter();
    List<PromotionsDTO> promotionsDTOList = new ArrayList<PromotionsDTO>();
    List<Promotions> promotionsList = writer.getPromotionsByRange(shopId, range);
    if(CollectionUtils.isNotEmpty(promotionsList)){
      for(Promotions promotions : promotionsList){
        promotionsDTOList.add(promotions.toDTO());
      }
    }
    return promotionsDTOList;
  }

  @Override
  public List<PromotionsDTO> getPromotionsDTODetailById(Long shopId, Long... ids) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<Promotions> promotionsList = writer.getPromotionsById(shopId, ids);
    List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
    if(CollectionUtils.isNotEmpty(promotionsList)) {
      for(Promotions promotions:promotionsList){
        PromotionsDTO promotionsDTO = promotionsList.get(0).toDTO();
        List<PromotionsRuleDTO> promotionsRuleDTOList = new ArrayList<PromotionsRuleDTO>();
        List<PromotionsRule> promotionsRuleList = writer.getPromotionsRuleByPromotionsId(promotionsDTO.getId());
        for (PromotionsRule promotionsRule : promotionsRuleList) {
          promotionsRuleDTOList.add(promotionsRule.toDTO());
        }
        promotionsDTO.setPromotionsRuleDTOList(promotionsRuleDTOList);
        promotionsDTOs.add(promotionsDTO);
      }
    }
    return promotionsDTOs;
  }

  @Override
  public void updatePromotionsForGoodsInOff(Long shopId, Long... productLocalInfoId) throws Exception {
    if(ArrayUtils.isEmpty(productLocalInfoId)) return;
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {

      List<PromotionsProductDTO> promotionsProductDTOs=getPromotionsProductDTOByProductIds(shopId,productLocalInfoId);
      Set<Long> promotionsIdList=new HashSet<Long>();
      if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
        for(PromotionsProductDTO pp:promotionsProductDTOs){
          if(pp.getPromotionsId()==null){
            continue;
          }
          promotionsIdList.add(pp.getPromotionsId());
        }
      }
      Map<Long,List<PromotionsProductDTO>> pMap=new HashMap<Long, List<PromotionsProductDTO>>();
      List<PromotionsProductDTO> allPromotionsProduct=getPromotionsProductDTOByPromotionsId(shopId,promotionsIdList.toArray(new Long[promotionsIdList.size()]));
      if(CollectionUtil.isNotEmpty(allPromotionsProduct)){
        for(PromotionsProductDTO p:allPromotionsProduct){
          if(p.getPromotionsId()==null){
            continue;
          }
          List<PromotionsProductDTO>  pList= pMap.get(p.getPromotionsId());
          if(pList==null){
            pList=new ArrayList<PromotionsProductDTO>();
            pMap.put(p.getPromotionsId(),pList);
          }
          pList.add(p);
        }
      }
      for(Long key:pMap.keySet()){
        if(pMap.get(key).size()==1){
          Promotions promotions=CollectionUtil.getFirst(getPromotionsById(shopId,key));
          if(promotions==null||DeletedType.TRUE.equals(promotions)
            || PromotionsEnum.PromotionStatus.EXPIRE.equals(promotions.getStatus())){
            continue;
          }
          promotions.setStatus(PromotionsEnum.PromotionStatus.UN_USED);
          writer.update(promotions);
        }
      }
      //删除promotionsProduct
      writer.cancelPromotionsByProductLocalInfoIds(shopId,productLocalInfoId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void deletePromotionsByPromotionsId(Long shopId, Long promotionsId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Promotions> promotionsList = writer.getPromotionsById(shopId,promotionsId);
      if(CollectionUtils.isNotEmpty(promotionsList)) {
        Promotions promotions = promotionsList.get(0);
        promotions.setDeleted(DeletedType.TRUE);
        writer.saveOrUpdate(promotions);
      }
      writer.cancelPromotionsByPromotionsId(shopId,promotionsId);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }


  @Override
  public List<PromotionsProductDTO> getPromotionsProductDTOByPromotionsId(Long shopId, Long... promotionsIds) throws Exception {
    if(ArrayUtil.isEmpty(promotionsIds)){
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<PromotionsProduct> promotionsProductList = writer.getPromotionsProductByPromotionsId(shopId, promotionsIds);
    if(CollectionUtils.isNotEmpty(promotionsProductList)) {
      List<PromotionsProductDTO> promotionsProductDTOList = new ArrayList<PromotionsProductDTO>();
      for (PromotionsProduct promotionsProduct : promotionsProductList) {
        promotionsProductDTOList.add(promotionsProduct.toDTO());
      }
      return promotionsProductDTOList;
    }
    return null;
  }

  @Override
  public List<PromotionsProduct> getPromotionsProductByPromotionsId(Long shopId, Long... promotionsIds) throws Exception {
    if(ArrayUtil.isEmpty(promotionsIds)){
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getPromotionsProductByPromotionsId(shopId, promotionsIds);
  }

  @Override
  public List<PromotionsProductDTO> getPromotionsProductDTOByProductIds(Long shopId, Long... productIds) {
    if(ArrayUtil.isEmpty(productIds)){
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<PromotionsProduct> promotionsProductList = writer.getPromotionsProductDTOByProductIds(shopId, productIds);
    if(CollectionUtils.isNotEmpty(promotionsProductList)) {
      List<PromotionsProductDTO> promotionsProductDTOList = new ArrayList<PromotionsProductDTO>();
      for (PromotionsProduct promotionsProduct : promotionsProductList) {
        promotionsProductDTOList.add(promotionsProduct.toDTO());
      }
      return promotionsProductDTOList;
    }
    return null;
  }

  @Override
  public List<PromotionsProductDTO> getPromotionsProductDTO(PromotionSearchCondition condition){
    if(condition==null){
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<PromotionsProduct> promotionsProductList = writer.getPromotionsProductDTO(condition);
    if(CollectionUtils.isNotEmpty(promotionsProductList)) {
      List<PromotionsProductDTO> promotionsProductDTOList = new ArrayList<PromotionsProductDTO>();
      for (PromotionsProduct promotionsProduct : promotionsProductList) {
        promotionsProductDTOList.add(promotionsProduct.toDTO());
      }
      return promotionsProductDTOList;
    }
    return null;
  }

//  @Override
//  public List<PromotionsProductDTO> getPromotionsProductDTO(Long shopId,Long productId,Long startTime,Long endTime,Long...promotionsIdList) {
//    if( productId==null){
//      return null;
//    }
//    ProductWriter writer = productDaoManager.getWriter();
//    List<PromotionsProduct> promotionsProductList = writer.getPromotionsProductDTO(shopId, productId,startTime,endTime,promotionsIdList);
//    if(CollectionUtils.isNotEmpty(promotionsProductList)) {
//      List<PromotionsProductDTO> promotionsProductDTOList = new ArrayList<PromotionsProductDTO>();
//      for (PromotionsProduct promotionsProduct : promotionsProductList) {
//        promotionsProductDTOList.add(promotionsProduct.toDTO());
//      }
//      return promotionsProductDTOList;
//    }
//    return null;
//  }





  @Override
  public void addGoodsToPromotions(Long shopId, Long promotionsId, Long... productLocalInfoIds) throws Exception {
    if(ArrayUtils.isEmpty(productLocalInfoIds) ||shopId==null || promotionsId==null ) return;

    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      PromotionsProduct promotionsProduct = null;
      for(Long productLocalInfoId : productLocalInfoIds){
        promotionsProduct = writer.getPromotionsProductByPromotionsIdAndProductLocalInfoId(shopId,promotionsId,productLocalInfoId);
        if(promotionsProduct==null){
          promotionsProduct = new PromotionsProduct();
          promotionsProduct.setProductLocalInfoId(productLocalInfoId);
          promotionsProduct.setPromotionsId(promotionsId);
          promotionsProduct.setShopId(shopId);
          writer.save(promotionsProduct);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<PromotionsDTO> getPromotionsDTODetailByProductLocalInfoId(Long shopId, Long productLocalInfoId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<Object[]> result = writer.getPromotionsByProductLocalInfoIds(shopId, productLocalInfoId);
    List<Promotions> promotionList = new ArrayList<Promotions>();
    if (CollectionUtils.isNotEmpty(result)) {
      for (Object[] obj : result) {
        if (obj != null && obj.length == 2) {
          promotionList.add((Promotions) obj[0]);
        }
      }
    }
    List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
    if(CollectionUtil.isNotEmpty(promotionList)){
      for(Promotions promotions:promotionList)  {
        PromotionsDTO promotionsDTO = promotions.toDTO();
        List<PromotionsRuleDTO> promotionsRuleDTOList = new ArrayList<PromotionsRuleDTO>();
        List<PromotionsRule> promotionsRuleList = writer.getPromotionsRuleByPromotionsId(promotionsDTO.getId());
        for (PromotionsRule promotionsRule : promotionsRuleList) {
          promotionsRuleDTOList.add(promotionsRule.toDTO());
        }
        promotionsDTO.setPromotionsRuleDTOList(promotionsRuleDTOList);
        promotionsDTOs.add(promotionsDTO);
      }
    }
    return promotionsDTOs;
  }

  @Override
  public List<PromotionsDTO> getSimplePromotionsDTOByProductLocalInfoId(Long shopId, Long productId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<Object[]> result = writer.getPromotionsByProductLocalInfoIds(shopId, productId);
    List<Promotions> promotionList = new ArrayList<Promotions>();
    if (CollectionUtils.isNotEmpty(result)) {
      for (Object[] obj : result) {
        if (obj != null && obj.length == 2) {
          promotionList.add((Promotions) obj[0]);
        }
      }
    }
    List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
    if(CollectionUtil.isNotEmpty(promotionList)){
      for(Promotions promotions:promotionList)  {
        promotionsDTOs.add(promotions.toDTO());
      }
    }
    return promotionsDTOs;
  }

  @Override
  public Map<Long,List<PromotionsDTO>> getSimplePromotionsDTO(Set<Long> shopIdSet,Long... productIds){
    Map<Long,List<PromotionsDTO>> pMap=new HashMap<Long, List<PromotionsDTO>>();
    if(CollectionUtil.isEmpty(shopIdSet)||ArrayUtil.isEmpty(productIds)){
      return pMap;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<Object[]> result = writer.getPromotionsByProductIds(shopIdSet,productIds);
    if(CollectionUtils.isEmpty(result)) {
      return pMap;
    }
    for (Object[] obj : result){
      if (obj==null||obj.length!= 2) {
        continue;
      }
      Promotions promotions=(Promotions) obj[0];
      PromotionsProduct promotionsProduct=(PromotionsProduct) obj[1];
      List<PromotionsDTO> promotionsDTOs=pMap.get(promotionsProduct.getProductLocalInfoId());
      if(promotionsDTOs==null){
        promotionsDTOs=new ArrayList<PromotionsDTO>();
        pMap.put(promotionsProduct.getProductLocalInfoId(),promotionsDTOs);
      }
      promotionsDTOs.add(promotions.toDTO());
    }
    return pMap;
  }

  public Result updatePromotionStatus(Result result,PromotionsDTO promotionsDTO) throws Exception {
    Long promotionsId=promotionsDTO.getId();
    Long shopId=promotionsDTO.getShopId();
    if(shopId==null||promotionsId==null){
      return result.LogErrorMsg("error info");
    }
    ProductWriter writer=productDaoManager.getWriter();
    Object status=writer.begin();
    try{
      Promotions promotions=CollectionUtil.getFirst(getPromotionsById(shopId,promotionsId));
      if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
        return result.LogErrorMsg("促销不存在，或已被删除，更新失败！");
      }
      PromotionsEnum.PromotionStatus pStatus=promotionsDTO.getStatus();
      if(pStatus==null){
        return result.LogErrorMsg("error info");
      }
      if(PromotionsEnum.PromotionStatus.USING.equals(pStatus)){
        if(promotionsDTO.getStartTime()==null){
          return result.LogErrorMsg("促销开始时间不应为空！");
        }
        List<Long> overlappingProductIds=getOverlappingProductIdByRange(promotionsDTO.getShopId(),promotionsDTO,false);
        List<PromotionsProductDTO> promotionsProductDTOs=getPromotionsProductDTOByPromotionsId(shopId,promotionsId);
        if(CollectionUtil.isEmpty(promotionsProductDTOs)){
          return result.LogErrorMsg("您还未添加任何促销商品！");
        }
        if(CollectionUtil.isNotEmpty(overlappingProductIds)){
          for(PromotionsProductDTO pp:promotionsProductDTOs){
            if(promotions.getId().equals(pp.getPromotionsId())){
              continue;
            }
            if(overlappingProductIds.contains(pp.getProductLocalInfoId())){
              ProductDTO productDTO= ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(CollectionUtil.getFirst(overlappingProductIds),shopId);
              Promotions pTemp=CollectionUtil.getFirst(getPromotionsById(shopId,pp.getPromotionsId()));
              if(productDTO!=null&&pTemp!=null)
                return result.LogErrorMsg("该促销的商品"+productDTO.getName()+"同一时间段已参加 "+pTemp.getName()+",请重新选择促销时间。");
            }
          }
        }
        if(NumberUtil.subtraction(promotionsDTO.getStartTime(),System.currentTimeMillis())>0){
          pStatus= PromotionsEnum.PromotionStatus.UN_STARTED;
        }
        promotions.setStartTime(promotionsDTO.getStartTime());
        promotions.setEndTime(promotionsDTO.getEndTime());
      }
      promotions.setStatus(pStatus);
      writer.update(promotions);
      writer.commit(status);
      promotionsDTO.setStatus(promotions.getStatus());
      return result;
    }finally {
      writer.rollback(status);
    }

  }

  public Result batchUpdatePromotionStatus(Result result,List<Long> promotionsIdList,PromotionsEnum.PromotionStatus promotionStatus) throws Exception {
    if(CollectionUtil.isEmpty(promotionsIdList)||promotionStatus==null){
      return result.LogErrorMsg("error info");
    }
    ProductWriter writer=productDaoManager.getWriter();
    Object status=writer.begin();
    try{
      for(Long promotionsId:promotionsIdList){
        Promotions promotions=writer.getById(Promotions.class,promotionsId);
        if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
          continue;
        }
        if(PromotionsEnum.PromotionStatus.USING.equals(promotionStatus)){
          List<PromotionsProductDTO> dtoList=getPromotionsProductDTOByPromotionsId(promotions.getShopId(),promotionsId);
          if(CollectionUtil.isEmpty(dtoList)){
            continue;
          }
        }
        promotions.setStatus(promotionStatus);
        writer.update(promotions);
      }
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  public Result handleExpirePromotions(Result result,Long shopId,List<Long> promotionsIdList) throws Exception {
    if(shopId==null||CollectionUtil.isEmpty(promotionsIdList)){
      return result;
    }
    ProductWriter writer=productDaoManager.getWriter();
    Object status=writer.begin();
    try{
      List<Promotions> promotionsList=getPromotionsById(shopId,ArrayUtil.toLongArr(promotionsIdList));
      if(CollectionUtil.isNotEmpty(promotionsList)){
        for(Promotions promotions:promotionsList){
          if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
            continue;
          }
          promotions.setStatus(PromotionsEnum.PromotionStatus.EXPIRE);
          writer.update(promotions);
        }
      }
      List<PromotionsProduct> ppList=getPromotionsProductByPromotionsId(shopId,ArrayUtil.toLongArr(promotionsIdList));
      if(CollectionUtil.isNotEmpty(ppList)){
        for(PromotionsProduct pp:ppList){
          if(pp==null){
            continue;
          }
          pp.setDeleted(DeletedType.TRUE);
          writer.update(pp);
        }
      }
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }


  //同一时段存在商品参加其他促销，不可以添加商品
  private Boolean validatePromotionsRangeOfAll(Long shopId,PromotionsDTO promotionsDTO) throws Exception {
//    List<Promotions> promotionsList=getCurrentPromotions(shopId);
//    if(CollectionUtil.isEmpty(promotionsList)){
//      return true;
//    }
//    List<Long> cPromotionsIdList=new ArrayList<Long>();
//    for(Promotions promotions:promotionsList){
//      if(promotions==null||PromotionsEnum.PromotionStatus.EXPIRE.equals(promotions.getStatus())){
//        continue;
//      }
//      cPromotionsIdList.add(promotions.getId());
//    }
//    List<PromotionsProductDTO> promotionsProductDTOs=getPromotionsProductDTOByPromotionsId(shopId,cPromotionsIdList.toArray(new Long[cPromotionsIdList.size()]));
//    if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
//      PromotionsProductDTO promotionsProductDTO=CollectionUtil.getFirst(promotionsProductDTOs);  //todo 取商品名
//      return false;
//    }
    Map<Long,Long> ppMap=getOverlappingProductIdMapByRange(shopId,promotionsDTO,false);
    if(!ppMap.keySet().isEmpty()){
      return false;
    }
//          for (Long key:ppMap.keySet()){
//            if(p.getProductLocalInfoId().equals(ppMap.get(key))){
//              writer.delete(PromotionsProduct.class,key);
//            }
//          }
    return true;
  }

  //添加全场参与的product
  private Result addAllRangPromotionsProduct(Result result,Promotions promotions,ProductWriter writer){
    List<ProductLocalInfo> productLocalInfoList=ServiceManager.getService(IProductService.class).getAllProductInSales(promotions.getShopId());
    if(CollectionUtil.isEmpty(productLocalInfoList)){
      return result.LogErrorMsg("您还没有上架商品，无法创建促销!");
    }
    List<Long> productIds=new ArrayList<Long>();
    int count=1;
    for(ProductLocalInfo productLocalInfo:productLocalInfoList){
      PromotionsProduct promotionsProduct = new PromotionsProduct();
      promotionsProduct.setProductLocalInfoId(productLocalInfo.getId());
      promotionsProduct.setPromotionsId(promotions.getId());
      promotionsProduct.setPromotionsType(promotions.getType());
      promotionsProduct.setShopId(promotions.getShopId());
      writer.save(promotionsProduct);
      productIds.add(productLocalInfo.getId());
      count++;
      if(count/1000==0){
        writer.flush();
      }
    }
    result.setDataList(productIds.toArray(new Object[productIds.size()]));
    return result;
  }

  private Result validateSavePromotions(Result result,PromotionsDTO promotionsDTO) throws Exception {
    Long shopId=promotionsDTO.getShopId();
    if(shopId==null||promotionsDTO.getUserId()==null){
      return result.LogErrorMsg("参数异常！");
    }
    if(StringUtil.isEmpty(promotionsDTO.getStartTimeStr())){
      return result.LogErrorMsg("促销开始时间不应为空。");
    }
    PromotionsEnum.PromotionsTypes type=promotionsDTO.getType();
    PromotionsEnum.PromotionsRanges range=promotionsDTO.getRange();
    if(type==null){
      return result.LogErrorMsg("参数异常！");
    }
    if(PromotionsEnum.PromotionsRanges.ALL.equals(range)){
      if(!validatePromotionsRangeOfAll(shopId,promotionsDTO)){
        return result.LogErrorMsg("部分商品已经参加其他促销，不可重复参加全场参与。");
      }
    }
    //过滤掉空的规则 todo 临时解决方案 应在前台处理
    if(PromotionsEnum.PromotionsTypes.MLJ.equals(type)){
      List<PromotionsRuleDTO> ruleDTOs=promotionsDTO.getPromotionsRuleDTOList();
      List<PromotionsRuleDTO> ruleDTOsNew=new ArrayList<PromotionsRuleDTO>();
      if(CollectionUtil.isNotEmpty(ruleDTOs)){
        for(PromotionsRuleDTO ruleDTO:ruleDTOs){
          if(ruleDTO.getPromotionsRuleType()==null){
            continue;
          }
          ruleDTOsNew.add(ruleDTO);
        }
      }
      promotionsDTO.setPromotionsRuleDTOList(ruleDTOsNew);
    }
    return result;
  }

  public Result savePromotionsForInSales(Result result,PromotionIndex promotionsDTO) throws Exception {
    Long shopId=promotionsDTO.getShopId();
    if(shopId==null||promotionsDTO.getType()==null){
      return result.LogErrorMsg("参数异常。");
    }
    promotionsDTO.setEndTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, promotionsDTO.getEndTimeStr()));
    promotionsDTO.setStartTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, promotionsDTO.getStartTimeStr()));
    PromotionsProductDTO [] promotionsProductDTOs=promotionsDTO.getPromotionsProductDTOList();
//    if(ArrayUtil.isEmpty(promotionsProductDTOs)){
//      return result.LogErrorMsg("参数异常。");
//    }
    if(ArrayUtil.isNotEmpty(promotionsProductDTOs)){
      Long [] productIdList=new Long[promotionsProductDTOs.length];
      for(int i=0;i<promotionsProductDTOs.length;i++){
        productIdList[i]=promotionsProductDTOs[i].getProductLocalInfoId();
      }
      result.setDataList(productIdList);
    }
    //覆盖掉时间段重叠的促销商品
    if(ArrayUtil.isNotEmpty(promotionsProductDTOs)){
      ProductWriter writer=productDaoManager.getWriter();
      Object status=writer.begin();
      try{
        for(PromotionsProductDTO p:promotionsProductDTOs){
          if(p==null||p.getProductLocalInfoId()==null) {
            continue;
          }
          Map<Long,Long> ppMap=getOverlappingProductIdMapByRange(shopId,promotionsDTO,false);
          for (Long key:ppMap.keySet()){
            if(p.getProductLocalInfoId().equals(ppMap.get(key))){
              writer.delete(PromotionsProduct.class,key);
            }
          }
        }
        writer.commit(status);
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }finally {
        writer.rollback(status);
      }
    }
    //保存促销
    Long promotionsId=promotionsDTO.getId();
    if(promotionsId==null){
      savePromotions(result,promotionsDTO);
      promotionsId=NumberUtil.longValue(result.getData());
      promotionsDTO.setId(promotionsId);
    }
    if(!result.isSuccess()){
      return result;
    }
    //判断是否添加商品到促销
    if(Boolean.TRUE.equals(promotionsDTO.isAddPromotionsProductFlag())){
      Map<Long,PromotionsProductDTO[]> promotionsProductDTOMap=new HashMap<Long, PromotionsProductDTO[]>();
      promotionsProductDTOMap.put(promotionsDTO.getId(),promotionsProductDTOs);
      addPromotionsProduct(result,shopId,promotionsProductDTOMap);
      promotionsId=promotionsDTO.getId();
    }
    result.setData(promotionsId);
    return result;
  }

  public Result savePromotions(Result result,PromotionsDTO promotionsDTO) throws Exception {
    validateSavePromotions(result,promotionsDTO);
    if(!result.isSuccess()){
      return result;
    }
    ProductWriter writer=productDaoManager.getWriter();
    Object status=writer.begin();
    try{
      Long shopId=promotionsDTO.getShopId();
      Long userId=promotionsDTO.getUserId();
      //save or update promotion
      Promotions promotions=new Promotions();
      PromotionsEnum.PromotionsRanges range=promotionsDTO.getRange();
      PromotionsEnum.PromotionsTypes type=promotionsDTO.getType();

      if(promotionsDTO.getId()==null){
        promotions.fromDTO(promotionsDTO);
        promotions.setStatus(PromotionsEnum.PromotionStatus.UN_USED);
        promotions.setSaveTime(System.currentTimeMillis());
        promotions.setShopKind(ServiceManager.getService(IConfigService.class).getShopById(shopId).getShopKind());
        promotions.setStartTime(promotions.getStartTime());
        writer.save(promotions);
      }else {
        promotions= CollectionUtil.getFirst(getPromotionsById(shopId, promotionsDTO.getId()));
        if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
          return result.LogErrorMsg("促销不存在，或已被删除，更新失败！");
        }
        PromotionsEnum.PromotionStatus pStatus=promotions.getStatus();
        promotions.fromDTO(promotionsDTO);
        if(PromotionsEnum.PromotionStatus.UN_STARTED.equals(pStatus)){
          promotions.setStatus(pStatus);
        }else {
          promotions.setStatus(PromotionsEnum.PromotionStatus.UN_USED);
        }
        writer.update(promotions);
      }
      if(PromotionsEnum.PromotionsRanges.ALL.equals(range)&&ArrayUtil.contains(PromotionsUtils.getAllRangePromotionsType(),type)){
        addAllRangPromotionsProduct(result,promotions,writer);
        if(result.isSuccess()){
          promotions.setStatus(PromotionsEnum.PromotionStatus.USING);
          writer.update(promotions);
        }else {
          return result;
        }
      }
      AreaDTO [] areaDTOs=promotionsDTO.getAreaDTOs();
      if(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(type)){
        Map<Long, List<PromotionsArea>> pAreaMap=getPromotionsAreaByPromotionsId(promotions.getId());
        List<PromotionsArea> pAreaList = pAreaMap.get(promotions.getId());
        if(CollectionUtil.isNotEmpty(pAreaList)){
          for(PromotionsArea pArea:pAreaList){
            writer.delete(pArea);
          }
        }
        if(!ArrayUtil.isEmpty(areaDTOs)){
          PromotionsEnum.PromotionsAreaType areaType=promotionsDTO.getPromotionsAreaType();
          areaType=areaType==null?PromotionsEnum.PromotionsAreaType.OTHER:areaType;
          switch (areaType){
            case COUNTRY:
            case PROVINCE:
              String as;
            case CITY:
              String ee;
            default:{
              for(AreaDTO areaDTO:areaDTOs){
                if(areaDTO==null) continue;
                PromotionsArea pArea=new PromotionsArea();
                pArea.setShopId(shopId);
                pArea.setAreaNo(areaDTO.getNo());
                pArea.setPromotionsId(promotions.getId());
                pArea.setPostType(promotionsDTO.getPostType());
                writer.save(pArea);
              }

            }
          }
        }
      }

      //delete promotionsRule
      List<PromotionsRuleDTO> promotionsRuleDTOs=promotionsDTO.getPromotionsRuleDTOList();
      List<PromotionsRule> promotionsRules=getPromotionsRuleByPromotionsIds(shopId,promotions.getId());
      List<Long> ruleIds=new ArrayList<Long>();
      if(CollectionUtil.isNotEmpty(promotionsRules)){
        for(PromotionsRule rule:promotionsRules){
          if(rule==null) continue;
          writer.delete(PromotionsRule.class, rule.getId());
          ruleIds.add(rule.getId());
        }
      }

      //delete PromotionsRuleMJS
      if(PromotionsEnum.PromotionsTypes.MJS.equals(type)&&CollectionUtil.isNotEmpty(ruleIds)){
        List<PromotionsRuleMJS> ruleMJSs=getPromotionsRuleMJSByRuleIds(shopId,ruleIds.toArray(new Long[ruleIds.size()]));
        if(CollectionUtil.isNotEmpty(ruleMJSs)){
          for(PromotionsRuleMJS ruleMJS:ruleMJSs){
            if(ruleMJS!=null){
              writer.delete(PromotionsRuleMJS.class,ruleMJS.getId());
            }
          }
        }
      }

      //save  promotionsRule
      if(CollectionUtil.isNotEmpty(promotionsRuleDTOs)){
        if(PromotionsEnum.PromotionsTypes.MLJ.equals(type)){
          PromotionsRuleDTO ruleDTO=CollectionUtil.getFirst(promotionsRuleDTOs);
          PromotionsEnum.PromotionsRuleType ruleType=ruleDTO.getPromotionsRuleType();
          if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_AMOUNT.equals(ruleType)||PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_AMOUNT.equals(ruleType)){
            promotions.setPromotionsLimiter(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT);
          }else if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_MONEY.equals(ruleType)||PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_MONEY.equals(ruleType)){
            promotions.setPromotionsLimiter(PromotionsEnum.PromotionsLimiter.OVER_MONEY);
          }
          writer.update(promotions);
        }
        for(PromotionsRuleDTO ruleDTO:promotionsRuleDTOs){
          if(ruleDTO==null) {
            continue;
          }
          if(PromotionsEnum.PromotionsTypes.MLJ.equals(type)
            &&NumberUtil.addition(ruleDTO.getDiscountAmount(),ruleDTO.getMinAmount())<0.001){  //minAmount 和 discountAmount不同时为0
            continue;
          }
          if(PromotionsEnum.PromotionsTypes.MJS.equals(type)&&CollectionUtil.isEmpty(ruleDTO.getPromotionsRuleMJSDTOs())){
            continue;
          }
          PromotionsRule rule=new PromotionsRule();
          rule.fromDTO(ruleDTO);
          rule.setShopId(promotionsDTO.getShopId());
          rule.setUserId(promotionsDTO.getUserId());
          rule.setPromotionsId(promotions.getId());
          writer.save(rule);
          //save  PromotionsRuleMJS
          List<PromotionsRuleMJSDTO> ruleMJSDTOs=ruleDTO.getPromotionsRuleMJSDTOs();
          if(CollectionUtil.isNotEmpty(ruleMJSDTOs)){
            for(PromotionsRuleMJSDTO ruleMJSDTO:ruleMJSDTOs){
              PromotionsRuleMJS ruleMJS=new PromotionsRuleMJS();
              ruleMJS.fromDTO(ruleMJSDTO);
              ruleMJS.setShopId(shopId);
              ruleMJS.setUserId(userId);
              ruleMJS.setGiftType(PromotionsEnum.GiftType.GIFT);
              ruleMJS.setPromotionsRuleId(rule.getId());
              writer.save(ruleMJS);
            }
          }
        }
      }
      writer.commit(status);
      result.setData(StringUtil.valueOf(promotions.getId()));
      result.setDataStr(StringUtil.valueOf(promotions.getId()));
    }finally {
      writer.rollback(status);
    }
    return result;
  }


  public Map<Long, List<PromotionsArea>> getPromotionsAreaByPromotionsId(Long... promotionsIdList){
    if(ArrayUtil.isEmpty(promotionsIdList)){
      return new HashMap<Long, List<PromotionsArea>>();
    }
    ProductWriter writer=productDaoManager.getWriter();
    List<PromotionsArea> areaList = writer.getPromotionsAreaByPromotionsIds(promotionsIdList);
    Map<Long, List<PromotionsArea>> result = new HashMap<Long, List<PromotionsArea>>();
    for(PromotionsArea area: areaList){
      if(result.get(area.getPromotionsId())==null){
        result.put(area.getPromotionsId(), new ArrayList<PromotionsArea>());
      }
      result.get(area.getPromotionsId()).add(area);
    }
    return result;
  }


  public  Result deletePromotions(Result result,Long shopId,Long promotionsId) throws Exception {
    Set<Long> productIds=new HashSet<Long>();
    Promotions promotions=CollectionUtil.getFirst(getPromotionsById(shopId, promotionsId));
    if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
      return result.LogErrorMsg("促销不存在，或已被删除，更新失败！");
    }
    if(PromotionsEnum.PromotionStatus.USING.equals(promotions.getStatus())){
      return result.LogErrorMsg("促销正在进行中，无法删除！");
    }
    ProductWriter writer=productDaoManager.getWriter();
    List<PromotionsRule> promotionsRules=getPromotionsRuleByPromotionsIds(shopId,promotions.getId());
    List<PromotionsProduct> promotionsProductList = writer.getPromotionsProductByPromotionsId(shopId, promotionsId);
    Object status=writer.begin();
    try{
      promotions.setDeleted(DeletedType.TRUE);
      writer.update(promotions);
      if(CollectionUtil.isNotEmpty(promotionsRules)){
        for(PromotionsRule rule:promotionsRules){
          rule.setDeleted(DeletedType.TRUE);
          writer.update(rule);
        }
      }
      if(CollectionUtil.isNotEmpty(promotionsProductList)){
        for(PromotionsProduct promotionsProduct:promotionsProductList){
          productIds.add(promotionsProduct.getProductLocalInfoId());
          promotionsProduct.setDeleted(DeletedType.TRUE);
          writer.update(promotionsProduct);
        }
      }
      if(productIds.size()>0)
        result.setDataList(productIds.toArray(new Long[productIds.size()]));
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  public  Result deleteSpecialCustomer(Result result,Long shopId,Long promotionsId) throws Exception {
    Set<Long> productIds=new HashSet<Long>();
    Promotions promotions=CollectionUtil.getFirst(getPromotionsById(shopId, promotionsId));
    if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
      return result.LogErrorMsg("促销不存在，或已被删除，更新失败！");
    }
    ProductWriter writer=productDaoManager.getWriter();
    Object status=writer.begin();
    try{
      promotions.setDeleted(DeletedType.TRUE);
      writer.update(promotions);
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  public int countPromotions(PromotionIndex condition){
    ProductWriter writer=productDaoManager.getWriter();
    return writer.countPromotions(condition);
  }

  public List<Promotions> getPromotionsById(Long shopId,Long... promotionsId){
    if(ArrayUtil.isEmpty(promotionsId)) return null;
    ProductWriter writer=productDaoManager.getWriter();
    return writer.getPromotionsById(shopId, promotionsId);
  }

  public List<PromotionsDTO> getPromotionsDTOById(Long shopId,Long... promotionsId){
    if(ArrayUtil.isEmpty(promotionsId)) return null;
    List<Promotions> promotionsList=getPromotionsById(shopId,promotionsId);
    List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
    if(CollectionUtil.isNotEmpty(promotionsList)){
      for(Promotions promotions:promotionsList){
        promotionsDTOs.add(promotions.toDTO());
      }
    }
    return promotionsDTOs;
  }

  public List<Promotions> getPromotionsByPromotionsType(Long shopId,PromotionsEnum.PromotionsTypes type){
    if(shopId==null) return null;
    ProductWriter writer=productDaoManager.getWriter();
    return writer.getPromotionsByPromotionsType(shopId, type);
  }

  public List<Promotions> getCurrentPromotions(Long shopId){
    if(shopId==null) return null;
    ProductWriter writer=productDaoManager.getWriter();
    return writer.getCurrentPromotions(shopId);
  }

  public  List<PromotionsRule> getPromotionsRuleByPromotionsIds(Long shopId,Long... promotionsIds){
    if(ArrayUtil.isEmpty(promotionsIds)){
      return null;
    }
    ProductWriter writer=productDaoManager.getWriter();
    return writer.getPromotionsRuleByPromotionsIds(shopId, promotionsIds);
  }

  public List<PromotionsRuleDTO> getPromotionsRuleDTOByPromotionsIds(Long shopId,Long... promotionsIds){
    List<PromotionsRule> promotionsRules = getPromotionsRuleByPromotionsIds(shopId, promotionsIds);
    if(CollectionUtils.isEmpty(promotionsRules)){
      return null;
    }
    List<PromotionsRuleDTO> dtos = new ArrayList<PromotionsRuleDTO>();
    for(PromotionsRule rule : promotionsRules){
      dtos.add(rule.toDTO());
    }
    return dtos;
  }

  public Map<Long,List<PromotionsRuleDTO>> getPromotionsRuleDTOMap(Long shopId,Long... promotionsIds){
    if(ArrayUtil.isEmpty(promotionsIds)) return null;
    List<PromotionsRule> rules=getPromotionsRuleByPromotionsIds(shopId,promotionsIds);
    Map<Long,List<PromotionsRuleDTO>> ruleMap=new HashMap<Long,List<PromotionsRuleDTO>>();
    if(CollectionUtil.isNotEmpty(rules)){
      for(PromotionsRule rule:rules){
        if(rule==null){
          continue;
        }
        List<PromotionsRuleDTO> ruleDTOList=ruleMap.get(rule.getPromotionsId());
        if(ruleDTOList==null){
          ruleDTOList=new ArrayList<PromotionsRuleDTO>();
          ruleMap.put(rule.getPromotionsId(),ruleDTOList);
        }
        ruleDTOList.add(rule.toDTO());
      }
    }
    return ruleMap;
  }

  public  List<PromotionsRuleMJS> getPromotionsRuleMJSByRuleIds(Long shopId,Long... ruleIds){
    if(ArrayUtil.isEmpty(ruleIds)) return null;
    ProductWriter writer=productDaoManager.getWriter();
    return writer.getPromotionsRuleMJSByRuleIds(shopId, ruleIds);
  }

  public List<Promotions> getPromotions(PromotionIndex condition){
    ProductWriter writer=productDaoManager.getWriter();
    return writer.getPromotions(condition);
  }

  public List<PromotionsDTO> getPromotionsDTO(PromotionIndex condition){
    List<Promotions> promotions=getPromotions(condition);
    List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
    if(CollectionUtil.isNotEmpty(promotions)){
      for(Promotions pro:promotions){
        promotionsDTOs.add(pro.toDTO());
      }
    }
    return promotionsDTOs;
  }



  public List<Long> getOverlappingProductIdByRange(Long shopId,PromotionsDTO target,Boolean includeTargetFlag) throws Exception {
    Map<Long,Long> ppMap=getOverlappingProductIdMapByRange(shopId,target,includeTargetFlag);
    List<Long> productIdList=new ArrayList<Long>();
    if(ppMap.isEmpty()){
      return productIdList;
    }
    for (Long key:ppMap.keySet()){
      productIdList.add(ppMap.get(key));
    }
    return productIdList;
  }


  public Map<Long,Long> getOverlappingProductIdMapByRange(Long shopId,PromotionsDTO target,Boolean includeTargetFlag) throws Exception {
    ProductWriter writer= productDaoManager.getWriter();
    Map<Long,Long> ppMap=new HashMap<Long, Long>();
    if(target==null||target.getStartTime()==null){
      return ppMap;
    }
    Long startTime=target.getStartTime();
    Long endTime=target.getEndTime();
    List<Promotions> pOverlapping=new ArrayList<Promotions>();
    //1.查询无限期的促销
    List<Promotions> unLimitedPromotions= writer.getUnlimitedPromotions(shopId);
    if(CollectionUtil.isNotEmpty(unLimitedPromotions)){
      for(Promotions p:unLimitedPromotions){
        if(endTime==null||endTime>=NumberUtil.longValue(p.getStartTime())){
          pOverlapping.add(p);
        }
      }
    }
    //2.时间上重叠的促销
    List<Promotions> lapPromotions=writer.getPromotionsByRange(shopId,startTime,endTime);
    if(CollectionUtil.isNotEmpty(lapPromotions)){
      for(Promotions p:lapPromotions){
        pOverlapping.add(p);
      }
    }

    Set<Long> promotionsIdSet=new HashSet<Long>();
    if(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(target.getType())){
      for(Promotions p:pOverlapping){
        if(Boolean.FALSE.equals(includeTargetFlag)&&p.getId().equals(target.getId())){
          continue;
        }
        if(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(p.getType())){  //同时段的送货上门
          promotionsIdSet.add(p.getId());
        }
      }
    }else{
      for(Promotions p:pOverlapping){
        if(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(p.getType())){
          continue;
        }
        if(Boolean.FALSE.equals(includeTargetFlag)&&p.getId().equals(target.getId())){
          continue;     //不过滤当前
        }
        if(!promotionsIdSet.contains(p.getId())){
          promotionsIdSet.add(p.getId());
        }
      }
    }

//    List<PromotionsProductDTO> promotionsProductDTOs=getPromotionsProductDTOByPromotionsId(shopId,promotionsIdSet.toArray(new Long[promotionsIdSet.size()]));
    List<PromotionsProductDTO> promotionsProductDTOs=getPromotionsProductDTOByPromotionsId(shopId,ArrayUtil.toLongArr(promotionsIdSet));
    if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
      for(PromotionsProductDTO pp:promotionsProductDTOs){
        if(pp.getProductLocalInfoId()==null){
          continue;
        }
        ppMap.put(pp.getId(),pp.getProductLocalInfoId());
      }
    }
    return ppMap;
  }

  private Result validateAddPromotionsProduct(Result result,Long shopId,Map<Long,PromotionsProductDTO[]> promotionsProductDTOMap) throws Exception {
    if(promotionsProductDTOMap==null||CollectionUtil.isEmpty(promotionsProductDTOMap.keySet())){
      return result.LogErrorMsg("参数异常！");
    }
    for(Long promotionsId:promotionsProductDTOMap.keySet()){
      if(promotionsId==null){
        return result.LogErrorMsg("促销id不能为空！");
      }
      ProductWriter writer= productDaoManager.getWriter();
      Promotions promotions=CollectionUtil.getFirst(writer.getPromotionsById(shopId, promotionsId));
      if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
        return result.LogErrorMsg("促销不存在，或已经被删除！");
      }
      //1.获取该促销时间段的其他促销
      PromotionIndex condition=new PromotionIndex();
      condition.setShopId(shopId);
      condition.setStartTime(promotions.getStartTime());
      condition.setEndTime(promotions.getEndTime());
      condition.setPromotionStatusList(new PromotionsEnum.PromotionStatus[]{
        PromotionsEnum.PromotionStatus.UN_STARTED,
        PromotionsEnum.PromotionStatus.USING,
        PromotionsEnum.PromotionStatus.SUSPEND
      });
      List<Promotions> promotionsList=getPromotions(condition);
      List<Long> promotionsIdList=new ArrayList<Long>();
      if(CollectionUtil.isNotEmpty(promotionsList)){
        for(Promotions p:promotionsList){
          if(promotions.getId().equals(p.getId())){
            continue;
          }
          promotionsIdList.add(p.getId());
        }
      }
      if(CollectionUtil.isEmpty(promotionsIdList)){
        continue;
      }
      List<PromotionsProductDTO> promotionsProductDTOsInDB=getPromotionsProductDTOByPromotionsId(shopId, promotionsIdList.toArray(new Long[promotionsIdList.size()]));
      Map<Long,PromotionsProductDTO> pMap=new HashMap<Long, PromotionsProductDTO>();
      if(CollectionUtil.isNotEmpty(promotionsProductDTOsInDB)){
        for(PromotionsProductDTO p:promotionsProductDTOsInDB){
          pMap.put(p.getProductLocalInfoId(),p);
        }
      }
      PromotionsProductDTO [] promotionsProductDTOs=promotionsProductDTOMap.get(promotionsId);
      if(ArrayUtil.isEmpty(promotionsProductDTOs)){
        return result.LogErrorMsg("选择的商品应为空！");
      }
      for(PromotionsProductDTO p:promotionsProductDTOs){
        if(p==null||p.getProductLocalInfoId()==null) continue;
        PromotionsProductDTO promotionsProductDTO=pMap.get(p.getProductLocalInfoId());
        if(promotionsProductDTO==null) continue;
        Promotions promotionsTemp=CollectionUtil.getFirst(writer.getPromotionsById(shopId, promotionsProductDTO.getPromotionsId()));
        if(promotionsTemp==null) continue;
        if(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promotions.getType())&&      //送货上门叠加
          PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promotionsTemp.getType())){
          ProductDTO productDTO= ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(p.getProductLocalInfoId(),shopId);
          return result.LogErrorMsg("商品"+productDTO.getName()+"已参加送货上门"+promotionsTemp.getName()+"，加入活动失败");
        }
        if(promotionsTemp.getId().equals(promotions.getId())
          ||(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promotionsTemp.getType()))
          || PromotionsEnum.PromotionStatus.EXPIRE.equals(promotionsTemp.getStatus())){
          continue;
        }
        List<Long> productIdList=getOverlappingProductIdByRange(shopId,promotions.toDTO(),false);
        if(CollectionUtil.isNotEmpty(promotionsList)&&productIdList.contains(p.getProductLocalInfoId())){
          ProductDTO productDTO= ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(p.getProductLocalInfoId(),shopId);
          return result.LogErrorMsg("商品"+productDTO.getName()+"已参加促销"+promotionsTemp.getName()+"，加入活动失败");
        }
      }
    }
    return result;
  }

  public Promotions getSpecialCustomer(Long shopId){
    PromotionIndex condition=new PromotionIndex();
    condition.setShopId(shopId);
    condition.setType(PromotionsEnum.PromotionsTypes.SPECIAL_CUSTOMER);
    return  CollectionUtil.getFirst(getPromotions(condition));
  }

  public Result calculateSpecialCustomer(Long shopId,Long supplierShopId,Double total) throws Exception {
    Result result=new Result();
    if(shopId==null||supplierShopId==null||NumberUtil.doubleVal(total)==0){
      return result.LogErrorMsg("error info!");
    }
    Promotions promotions=getSpecialCustomer(supplierShopId);
    if(promotions==null){
      return result.LogErrorMsg("促销不存在或已经删除！");
    }
    Long startTime=promotions.getStartTime();
    Long endTime=promotions.getEndTime();
    if(startTime==null||endTime==null){
      return result.LogErrorMsg("error info!");
    }
    Long l_now=System.currentTimeMillis();
    if(l_now<startTime||l_now>endTime){
      return result.LogErrorMsg("客户优惠过期!");
    }
    List<PromotionsRule> ruleList=getPromotionsRuleByPromotionsIds(supplierShopId,promotions.getId());
    if(CollectionUtil.isEmpty(ruleList)){
      return result.LogErrorMsg("无促销规则！");
    }

    ISearchOrderService searchOrderService=ServiceManager.getService(ISearchOrderService.class);
    OrderSearchConditionDTO conditionDTO=new OrderSearchConditionDTO();
    conditionDTO.setShopId(supplierShopId);
    conditionDTO.setStartTime(startTime);
    conditionDTO.setEndTime(l_now);
    conditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET});
    conditionDTO.setOrderType(OrderTypes.getCustomerConsumeOrder());
    conditionDTO.setStatsFields(new String[]{OrderSearchResultListDTO.ORDER_TOTAL_AMOUNT, OrderSearchResultListDTO.ORDER_DEBT_AMOUNT, OrderSearchResultListDTO.ORDER_SETTLED_AMOUNT});
    conditionDTO.setFacetFields(new String[]{"order_type"});
    OrderSearchResultListDTO resultListDTO=searchOrderService.queryOrders(conditionDTO);
    Map<String, Double> totalAmount= resultListDTO.getTotalAmounts();
    Double totalConsume=total;
    if(totalAmount!=null)
      totalConsume+=NumberUtil.round(totalAmount.get("DEBT_AND_SETTLED_AMOUNT_ORDER_TYPE_SALE"));
    Collections.sort(ruleList, PromotionsRule.SORT_BY_LEVEL);
    Collections.reverse(ruleList);
    for(PromotionsRule rule:ruleList){
      if(rule==null) continue;
      Double minAmount=NumberUtil.doubleVal(rule.getMinAmount());
      Double discountAmount=NumberUtil.doubleVal(rule.getDiscountAmount());
      PromotionsEnum.PromotionsRuleType type=rule.getPromotionsRuleType();
      if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_MONEY.equals(type)&&(totalConsume-minAmount>-0.001)){
        total=NumberUtil.round(total * discountAmount/10);
        break;
      }else if(PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_MONEY.equals(type)&&(totalConsume-minAmount>-0.001)){
        total=NumberUtil.subtraction(total,discountAmount);
        break;
      }
    }
    result.setData(total);
    return result;
  }

  public List<ProductDTO> getUnPromotionsProduct(Long shopId) throws Exception {
    SearchConditionDTO condition=new SearchConditionDTO();
    condition.setShopId(shopId);
    condition.setSalesStatus(ProductStatus.InSales);
    condition.setPromotionsType(PromotionsEnum.PromotionsTypes.NONE.toString());
    ProductSearchResultListDTO resultListDTO = ServiceManager.getService(ISearchProductService.class).queryProductWithStdQuery(condition);
    return resultListDTO.getProducts();
  }

  public Result addPromotionsProductForInSales(Result result,Long shopId,ProductDTO... productDTOs) throws Exception{
    if(ArrayUtil.isEmpty(productDTOs)){
      return result.LogErrorMsg("参数异常。");
    }
    Map<Long,List<PromotionsProductDTO>> pMap=new HashMap<Long,List<PromotionsProductDTO>>();
    for(ProductDTO productDTO:productDTOs){
      PromotionsDTO promotionsDTO=productDTO.getPromotionsDTO();
      if(promotionsDTO==null||promotionsDTO.getId()==null){
        LOG.info("未参加促销。");
        continue;
      }
      List<PromotionsProductDTO> promotionsProductDTOs=pMap.get(promotionsDTO.getId());
      if(CollectionUtil.isEmpty(promotionsProductDTOs)){
        promotionsProductDTOs=new ArrayList<PromotionsProductDTO>();
        pMap.put(promotionsDTO.getId(),promotionsProductDTOs);
      }
      promotionsProductDTOs.add(promotionsDTO.getPromotionsProductDTO());
    }
    Map<Long,PromotionsProductDTO[]> promotionsProductDTOMap=new HashMap<Long, PromotionsProductDTO[]>();
    for(Long promotionId:pMap.keySet()){
      List<PromotionsProductDTO> promotionsProductDTOs=pMap.get(promotionId);
      promotionsProductDTOMap.put(promotionId,promotionsProductDTOs.toArray(new PromotionsProductDTO[promotionsProductDTOs.size()]));
    }
    if(promotionsProductDTOMap.keySet().size()>0)
      addPromotionsProduct(result,shopId,promotionsProductDTOMap);
    return result;
  }

  public Result addPromotionsProduct(Result result,Long shopId,Map<Long,PromotionsProductDTO[]> promotionsProductDTOMap) throws Exception {
    ProductWriter writer= productDaoManager.getWriter();
    validateAddPromotionsProduct(result,shopId,promotionsProductDTOMap);
    if(!result.isSuccess()){
      return result;
    }
    //save promotionsProduct
    Object status= writer.begin();
    List<Long> productIds=new ArrayList<Long>();
    try{
      for(Long promotionsId:promotionsProductDTOMap.keySet()){
        Promotions promotions=CollectionUtil.getFirst(writer.getPromotionsById(shopId,promotionsId));
        if(promotions==null){
          return result.LogErrorMsg("促销已经删除！无法添加商品");
        }
        PromotionsEnum.PromotionsTypes type=promotions.getType();
        PromotionsEnum.PromotionsRanges range=promotions.getRange();
        PromotionsEnum.PromotionStatus promotionStatus=null;
        Long now= System.currentTimeMillis();
        Long endTime= promotions.getEndTime();
        if(endTime!=null&&now>endTime){
          promotionStatus=PromotionsEnum.PromotionStatus.EXPIRE;
        }else if(NumberUtil.subtraction(now,promotions.getStartTime())>=0){
          promotionStatus=PromotionsEnum.PromotionStatus.USING;
        }else {
          promotionStatus=PromotionsEnum.PromotionStatus.UN_STARTED;
        }
        if(PromotionsEnum.PromotionsRanges.PARTLY.equals(range)||PromotionsEnum.PromotionsRanges.ALL.equals(range)
          || PromotionsEnum.PromotionsTypes.BARGAIN.equals(type)){
          Map<Long,PromotionsProduct> pMap=new HashMap<Long,PromotionsProduct>();
          List<PromotionsProduct>  promotionsProductList = writer.getPromotionsProductByPromotionsId(shopId, promotionsId);
          PromotionsProductDTO[] dtos=promotionsProductDTOMap.get(promotionsId);
          if(ArrayUtil.isNotEmpty(dtos)){
            if(CollectionUtil.isNotEmpty(promotionsProductList)){
              for(PromotionsProduct pp:promotionsProductList){
                if(pp==null||DeletedType.TRUE.equals(pp.getDeleted())) {
                  continue;
                }
                pMap.put(pp.getProductLocalInfoId(), pp);
              }
            }
            for(PromotionsProductDTO dto:dtos){
              productIds.add(dto.getProductLocalInfoId());
              if(pMap.keySet().contains(dto.getProductLocalInfoId())){
                if(PromotionsEnum.PromotionsTypes.BARGAIN.equals(type)){
                  PromotionsProduct promotionsProduct=pMap.get(dto.getProductLocalInfoId());
                  promotionsProduct.fromDTO(dto);
                  writer.update(promotionsProduct);
                }
                continue;
              }
              PromotionsProduct promotionsProduct = new PromotionsProduct();
              promotionsProduct.setProductLocalInfoId(dto.getProductLocalInfoId());
              promotionsProduct.setPromotionsId(promotionsId);
              promotionsProduct.setBargainType(dto.getBargainType());
              promotionsProduct.setDiscountAmount(dto.getDiscountAmount());
              promotionsProduct.setPromotionsType(promotions.getType());
              if(Boolean.TRUE.equals(dto.isLimitFlag())){
                promotionsProduct.setLimitAmount(dto.getLimitAmount());
              }else {
                promotionsProduct.setLimitAmount(-1d);
              }
              promotionsProduct.setShopId(shopId);
              writer.save(promotionsProduct);
            }
          }
        }
        promotions.setStatus(promotionStatus);
        writer.update(promotions);
      }
      writer.commit(status);
      result.setDataList(productIds.toArray(new Long[productIds.size()]));
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  public Result deletePromotionsProduct(Result result,Long shopId,Long promotionsId,Long... productIds) throws Exception {
    if(promotionsId==null||ArrayUtil.isEmpty(productIds)){
      return result.LogErrorMsg("促销信息异常！");
    }
    ProductWriter writer= productDaoManager.getWriter();
    List<PromotionsProduct> promotionsProducts=writer.getPromotionsProduct(shopId,promotionsId,productIds);
    //update promotionsProduct
    Object status=writer.begin();
    try{
      if(CollectionUtil.isNotEmpty(promotionsProducts)){
        for(PromotionsProduct promotionsProduct:promotionsProducts){
          if(promotionsProduct==null) continue;
          promotionsProduct.setDeleted(DeletedType.TRUE);
          writer.update(promotionsProduct);
        }
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
    //update promotions
    status=writer.begin();
    try{
      if(CollectionUtil.isNotEmpty(promotionsProducts)){
        Promotions promotionsTemp=CollectionUtil.getFirst(getPromotionsById(shopId,promotionsId));
        List<PromotionsProduct> pp=writer.getPromotionsProductByPromotionsId (shopId,promotionsId);
        if(CollectionUtil.isEmpty(pp)){
          promotionsTemp.setStatus(PromotionsEnum.PromotionStatus.UN_USED);
        }
        writer.update(promotionsTemp);
        writer.commit(status);
      }
    }finally {
      writer.rollback(status);
    }
    return result;
  }

  private void savePromotionOrderRecordForOrder(PurchaseOrderDTO orderDTO, Map<Long,ProductDTO> pMap){
    if(orderDTO==null||ArrayUtil.isEmpty(orderDTO.getItemDTOs())){
      return;
    }
    for(PurchaseOrderItemDTO itemDTO : orderDTO.getItemDTOs()){
      ProductDTO productDTO = pMap.get(itemDTO.getSupplierProductId());
      List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
      if(CollectionUtil.isNotEmpty(promotionsDTOs)){
        List<PromotionOrderRecordDTO> recordDTOList=new ArrayList<PromotionOrderRecordDTO>();
        for(PromotionsDTO promotionsDTO:promotionsDTOs){
          if(promotionsDTO==null||!PromotionsEnum.PromotionStatus.USING.equals(promotionsDTO.getStatus())){
            continue;
          }
          PromotionOrderRecordDTO recordDTO=new PromotionOrderRecordDTO();
          recordDTO.setPromotionsType(promotionsDTO.getType());
          recordDTO.setPromotionsId(promotionsDTO.getId());
          recordDTO.setPromotionsJson(JsonUtil.objectToJson(promotionsDTO));
          recordDTOList.add(recordDTO);
          if(PromotionsEnum.PromotionsTypes.BARGAIN.equals(promotionsDTO.getType())){
            PromotionsProductDTO promotionsProductDTO= ArrayUtil.getFirst(promotionsDTO.getPromotionsProductDTOList());
            Double limitAmount=0d;
            if(promotionsProductDTO!=null){
              limitAmount=NumberUtil.doubleVal(promotionsProductDTO.getLimitAmount());
            }
            if(!itemDTO.getCustomPriceFlag()&&itemDTO.getQuotedPreBuyOrderItemId()==null&&limitAmount>0){
              Double limitRecordAmount=getPromotionOrderRecordUsedAmount(productDTO.getProductLocalInfoId(),promotionsDTO.getId(),orderDTO.getShopId(), orderDTO.getId());
              if(NumberUtil.addition(limitRecordAmount,itemDTO.getAmount())<=limitAmount){
                recordDTO.setAmount(itemDTO.getAmount());
              }
            }
          }
        }
        itemDTO.setPromotionOrderRecordDTOs(recordDTOList);
      }
    }

  }

  private void addItemPromotionsId(PurchaseOrderItemDTO itemDTO,Long promotionsId){
    if(promotionsId==null) return;
    Set<Long> promotionsIds=itemDTO.getPromotionsIds();
    if(CollectionUtil.isEmpty(promotionsIds)){
      promotionsIds=new HashSet<Long>();
      itemDTO.setPromotionsIds(promotionsIds);
    }
    promotionsIds.add(promotionsId);
  }

  public Map<String,String> calculateOrderTotal(PurchaseOrderDTO orderDTO, Map<Long,ProductDTO> pMap){
    if(orderDTO==null||ArrayUtil.isEmpty(orderDTO.getItemDTOs())){
      return null;
    }
    Long shopId=orderDTO.getShopId();
    //1.保存单据的促销历史信息
    savePromotionOrderRecordForOrder(orderDTO,pMap);
    //2.对单据的item进行分类
    List<PurchaseOrderItemDTO> itemDTOs=new ArrayList<PurchaseOrderItemDTO>();  //未参加任何促销
    List<PurchaseOrderItemDTO> mljItems=new ArrayList<PurchaseOrderItemDTO>();
    List<PurchaseOrderItemDTO> mjsItems=new ArrayList<PurchaseOrderItemDTO>();
    List<PurchaseOrderItemDTO> bargainItems=new ArrayList<PurchaseOrderItemDTO>();
    List<PurchaseOrderItemDTO> freeShippingItems=new ArrayList<PurchaseOrderItemDTO>();
    for (PurchaseOrderItemDTO itemDTO : orderDTO.getItemDTOs()){
      ProductDTO productDTO = pMap.get(itemDTO.getSupplierProductId());
      if(productDTO==null){
        LOG.info("un_promotions");
        continue;
      }
      boolean otherItem = true;
      if(PromotionsUtils.hasPromotionsWithPriceFlag(productDTO, itemDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING)){
        freeShippingItems.add(itemDTO);
        otherItem = false;
      }
      if(PromotionsUtils.hasPromotionsWithPriceFlag(productDTO, itemDTO, PromotionsEnum.PromotionsTypes.BARGAIN)){
        bargainItems.add(itemDTO);
        otherItem = false;
      }else if(PromotionsUtils.hasPromotionsWithPriceFlag(productDTO, itemDTO, PromotionsEnum.PromotionsTypes.MLJ)){
        mljItems.add(itemDTO);
        otherItem = false;
      }else  if(PromotionsUtils.hasPromotionsWithPriceFlag(productDTO, itemDTO, PromotionsEnum.PromotionsTypes.MJS)){
        mjsItems.add(itemDTO);
        otherItem = false;
      }
      if(otherItem){
        itemDTOs.add(itemDTO);
      }
      itemDTO.setQuotedPrice(productDTO.getInSalesPrice());
      if(!itemDTO.getCustomPriceFlag()){
        itemDTO.setPrice(productDTO.getInSalesPrice());
      }
//      itemDTO.setPromotionsIds(PromotionsUtils.generatePromotionsIds(productDTO));
      itemDTO.setTotal(NumberUtil.round(itemDTO.getAmount() * itemDTO.getPrice()));
    }
    //3.按照 freeShipping->Bargain->mlj->mjs 的顺序进行计算
    Map<String,String>  promotionsInfoMap=new HashMap<String, String>();
    //calculate freeShipping
    if(CollectionUtil.isNotEmpty(freeShippingItems)){
      Map<Long,List<PurchaseOrderItemDTO>> freeShippingItemMap=null;
      Boolean satisfyFreeShippingFlag=false;
      for(PurchaseOrderItemDTO freeShippingItemDTO:freeShippingItems){
        ProductDTO productDTO = pMap.get(freeShippingItemDTO.getSupplierProductId());
        PromotionsDTO promotionsDTO=PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
        if(promotionsDTO==null){
          LOG.warn("error info!");
          continue;
        }
        Map<Long, Boolean> isPromotionsArea=judgePromotionsAreaByShopId(shopId,promotionsDTO.getId());
        if(!isPromotionsArea.get(promotionsDTO.getId())){
          freeShippingItemDTO.setPromotionsId(null);
          continue;
        }
        addItemPromotionsId(freeShippingItemDTO,promotionsDTO.getId());
        PromotionsEnum.PromotionsLimiter limiter=promotionsDTO.getPromotionsLimiter();
        if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(limiter)){
          satisfyFreeShippingFlag=PromotionsUtils.satisfyFreeShipping(limiter, promotionsDTO.getPromotionsRuleDTOList(),0,freeShippingItemDTO.getAmount());
        }else if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)){    //相当于对OVER_MONEY类型再次分类
          if(freeShippingItemMap==null){
            freeShippingItemMap=new HashMap<Long,List<PurchaseOrderItemDTO>>();
          }
          List<PurchaseOrderItemDTO> freeShippingItemDTOs=freeShippingItemMap.get(promotionsDTO.getId());
          if(freeShippingItemDTOs==null){
            freeShippingItemDTOs=new ArrayList<PurchaseOrderItemDTO>();
            freeShippingItemMap.put(promotionsDTO.getId(),freeShippingItemDTOs);
          }
          freeShippingItemDTOs.add(freeShippingItemDTO);
        }
      }

      if(freeShippingItemMap!=null&&freeShippingItemMap.keySet().size()>0){
        for(Long key:freeShippingItemMap.keySet()){
          List<PurchaseOrderItemDTO> freeShippingItemDTOs=freeShippingItemMap.get(key);
          double freeShippingTotal=0d;
          if(CollectionUtil.isNotEmpty(freeShippingItemDTOs)){
            for(PurchaseOrderItemDTO fItemDTO:freeShippingItemDTOs){
              freeShippingTotal=NumberUtil.addition(freeShippingTotal,fItemDTO.getPrice() * fItemDTO.getAmount());
            }
          }
          PromotionsDTO promotionsDTO=PromotionsUtils.getPromotions(freeShippingItemDTOs, PromotionsEnum.PromotionsTypes.FREE_SHIPPING,pMap);
          if(promotionsDTO==null){
            continue;
          }
          satisfyFreeShippingFlag=PromotionsUtils.satisfyFreeShipping(PromotionsEnum.PromotionsLimiter.OVER_MONEY, promotionsDTO.getPromotionsRuleDTOList(), freeShippingTotal, 0);
          if(satisfyFreeShippingFlag){
            break;
          }
        }
      }
      if(satisfyFreeShippingFlag){
        promotionsInfoMap.put("FREE_SHIPPING","送货上门");
      }
    }
    //calculate bargain
    if(CollectionUtil.isNotEmpty(bargainItems)){
      for (PurchaseOrderItemDTO bargainItem : bargainItems){
        ProductDTO productDTO = pMap.get(bargainItem.getSupplierProductId());
        PromotionsDTO promotionsDTO=PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.BARGAIN);
        if(promotionsDTO==null){
          continue;
        }
        PromotionsProductDTO promotionsProductDTO= ArrayUtil.getFirst(promotionsDTO.getPromotionsProductDTOList());
        Double limitAmount=0d;
        if(promotionsProductDTO!=null){
          limitAmount=NumberUtil.doubleVal(promotionsProductDTO.getLimitAmount());
        }
        Double limitRecordAmount=0d;
        if(limitAmount>0){
          limitRecordAmount=getPromotionOrderRecordUsedAmount(productDTO.getProductLocalInfoId(),promotionsDTO.getId(),shopId, orderDTO.getId());
          if(NumberUtil.addition(limitRecordAmount,bargainItem.getAmount())>limitAmount){
            LOG.info("超过限购数量！");
            continue;

          }
        }
        Double quotedPrice=bargainItem.getQuotedPrice();
        PromotionOrderRecordDTO recordDTO=PromotionsUtils.calculateBargainPrice(productDTO, bargainItem.getQuotedPrice(), bargainItem.getAmount());
        if(NumberUtil.subtraction(quotedPrice,recordDTO.getNewPrice())>0){
          bargainItem.setPrice(recordDTO.getNewPrice());
          bargainItem.setProductDTO(productDTO);
          addItemPromotionsId(bargainItem,promotionsDTO.getId());
        }
      }
      double bTotal=0d;
      for (PurchaseOrderItemDTO bargainItem : bargainItems){
        bTotal += NumberUtil.subtraction(bargainItem.getQuotedPrice(), bargainItem.getPrice()) * bargainItem.getAmount();
      }
      if(bTotal>0){
        promotionsInfoMap.put("BARGAIN",StringUtil.valueOf(bTotal));
      }
    }
    //calculate MLJ
    Double mljTotal=0d;
    if(CollectionUtil.isNotEmpty(mljItems)){
      Map<Long,List<PurchaseOrderItemDTO>> mljItemMap=null;
      for(PurchaseOrderItemDTO mljItemDTO:mljItems){
        ProductDTO productDTO = pMap.get(mljItemDTO.getSupplierProductId());
        PromotionsDTO promotionsDTO=PromotionsUtils.getPromotionsDTO(productDTO,PromotionsEnum.PromotionsTypes.MLJ);
        if(mljItemDTO.getPrice() == null){
          mljItemDTO.setPrice(productDTO.getTradePrice());
        }
        if(promotionsDTO==null){
          LOG.warn("error info!");
          continue;
        }
        addItemPromotionsId(mljItemDTO,promotionsDTO.getId());
        PromotionsEnum.PromotionsLimiter limiter=promotionsDTO.getPromotionsLimiter();
        if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(limiter)){
          List<PromotionsRuleDTO> ruleDTOs=promotionsDTO.getPromotionsRuleDTOList();
          double newTotalPrice=NumberUtil.round(PromotionsUtils.calculateMLJPrice(limiter, ruleDTOs,mljItemDTO.getQuotedPrice()*mljItemDTO.getAmount(),mljItemDTO.getAmount()));
          Double subVal=NumberUtil.subtraction(mljItemDTO.getQuotedPrice()*mljItemDTO.getAmount(),newTotalPrice);
          if(subVal>0){
            mljTotal=NumberUtil.addition(mljTotal,subVal);
          }
        }else if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)){    //相当于对OVER_MONEY类型再次分类
          if(mljItemMap==null){
            mljItemMap=new HashMap<Long,List<PurchaseOrderItemDTO>>();
          }
          List<PurchaseOrderItemDTO> mljItemDTOs=mljItemMap.get(promotionsDTO.getId());
          if(mljItemDTOs==null){
            mljItemDTOs=new ArrayList<PurchaseOrderItemDTO>();
            mljItemMap.put(promotionsDTO.getId(),mljItemDTOs);
          }
          mljItemDTOs.add(mljItemDTO);
        }
      }
      if(mljItemMap!=null&&mljItemMap.keySet().size()>0){
        for(Long key:mljItemMap.keySet()){
          List<PurchaseOrderItemDTO> mljItemDTOs=mljItemMap.get(key);
          if(CollectionUtil.isEmpty(mljItemDTOs)){
            continue;
          }
          Double mTotal=0d; //单条item的小计(单价*数量)
          for(PurchaseOrderItemDTO jItemDTO:mljItemDTOs){
            mTotal=NumberUtil.addition(mTotal,jItemDTO.getPrice() * jItemDTO.getAmount());
          }
          PromotionsDTO promotionsDTO=PromotionsUtils.getPromotions(mljItemDTOs, PromotionsEnum.PromotionsTypes.MLJ,pMap);
          if(promotionsDTO==null){
            continue;
          }

          mljTotal=NumberUtil.addition(mljTotal, PromotionsUtils.calculateMLJOrderPrice(mljItemDTOs, PromotionsEnum.PromotionsLimiter.OVER_MONEY,promotionsDTO.getPromotionsRuleDTOList(),mTotal,0));
        }
      }
      if(mljTotal>0){
        promotionsInfoMap.put("MLJ",StringUtil.valueOf(mljTotal));
      }
    }
    //calculate MJS
    if(CollectionUtil.isNotEmpty(mjsItems)){
      Map<Long,List<PurchaseOrderItemDTO>> mjsItemMap=null;
      List<PromotionsRuleMJSDTO> giftList=new ArrayList<PromotionsRuleMJSDTO>();
      for(PurchaseOrderItemDTO mjsItemDTO:mjsItems){
        ProductDTO productDTO = pMap.get(mjsItemDTO.getSupplierProductId());
        PromotionsDTO promotionsDTO=PromotionsUtils.getPromotionsDTO(productDTO,PromotionsEnum.PromotionsTypes.MJS);
        if(promotionsDTO==null){
          LOG.warn("error info!");
          continue;
        }
        addItemPromotionsId(mjsItemDTO,promotionsDTO.getId());
        PromotionsEnum.PromotionsLimiter limiter=promotionsDTO.getPromotionsLimiter();
        if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(limiter)){
          List<PromotionsRuleDTO> ruleDTOs=promotionsDTO.getPromotionsRuleDTOList();
          CollectionUtil.add(giftList,PromotionsUtils.getMJSGift(limiter, ruleDTOs, 0, mjsItemDTO.getAmount()));
        }else if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)){    //相当于对OVER_MONEY类型再次分类
          if(mjsItemMap==null){
            mjsItemMap=new HashMap<Long,List<PurchaseOrderItemDTO>>();
          }
          List<PurchaseOrderItemDTO> mjsItemDTOs=mjsItemMap.get(promotionsDTO.getId());
          if(mjsItemDTOs==null){
            mjsItemDTOs=new ArrayList<PurchaseOrderItemDTO>();
            mjsItemMap.put(promotionsDTO.getId(),mjsItemDTOs);
          }
          mjsItemDTOs.add(mjsItemDTO);
        }
      }

      if(mjsItemMap!=null&&mjsItemMap.keySet().size()>0){
        for(Long key:mjsItemMap.keySet()){
          List<PurchaseOrderItemDTO> mjsItemDTOs=mjsItemMap.get(key);
          if(CollectionUtil.isEmpty(mjsItemDTOs)){
            continue;
          }
          double mjsTotal=0d;   //单条小计
          for(PurchaseOrderItemDTO mItemDTO:mjsItemDTOs){
            mjsTotal=NumberUtil.addition(mjsTotal,mItemDTO.getPrice()*mItemDTO.getAmount());
          }
          PromotionsDTO promotionsDTO=PromotionsUtils.getPromotions(mjsItemDTOs, PromotionsEnum.PromotionsTypes.MJS,pMap);
          if(promotionsDTO==null){
            continue;
          }
          CollectionUtil.add(giftList,PromotionsUtils.getMJSGift(PromotionsEnum.PromotionsLimiter.OVER_MONEY, promotionsDTO.getPromotionsRuleDTOList(), mjsTotal, 0));
        }
      }

      Double depositTotal=0d;
      StringBuffer gift=new StringBuffer();
      for(PromotionsRuleMJSDTO ruleMJS:giftList){
        PromotionsEnum.GiftType giftType=ruleMJS.getGiftType();
        String giftName=ruleMJS.getGiftName();
        Double amount=ruleMJS.getAmount();
        if(PromotionsEnum.GiftType.GIFT.equals(giftType)){
          gift.append("送礼物").append(giftName);
          if(NumberUtil.doubleVal(amount)>0){
            gift.append(",").append(amount).append("个;");
          }
        }else if(PromotionsEnum.GiftType.DEPOSIT.equals(giftType)){
          depositTotal+=NumberUtil.addition(depositTotal,amount);
          gift.append("送预收款").append(amount).append("元;");
        }
      }
      if(!StringUtil.isEmpty(gift.toString())){
        promotionsInfoMap.put("MJS",gift.toString());
      }
      if(depositTotal>0){
        promotionsInfoMap.put("MJS_DEPOSIT",StringUtil.valueOf(depositTotal));
      }
    }

    double total=0d;
    for(PurchaseOrderItemDTO itemDTO:orderDTO.getItemDTOs()){
      itemDTO.setTotal(NumberUtil.round(itemDTO.getAmount() * itemDTO.getPrice(), 2));
      total+=NumberUtil.doubleVal(itemDTO.getPrice() * itemDTO.getAmount());
    }
    orderDTO.setTotal(NumberUtil.subtraction(total,mljTotal));
    return promotionsInfoMap;
  }

  //当前店铺在送货上门范围内的，返回true，否则false
  public Map<Long, Boolean> judgePromotionsAreaByShopId(Long shopId,Long... promotionsIdList){
    ShopDTO shopDTO= ServiceManager.getService(IShopService.class).getShopAreaInfo(shopId);
    Long cityNo=shopDTO.getCity();
    Map<Long, Boolean> result = new HashMap<Long, Boolean>();
    for(Long promotionsId:promotionsIdList){
      result.put(promotionsId, false);
    }
    if(cityNo==null){
      return result;
    }
    Map<Long, List<PromotionsArea>> pAreaMap=getPromotionsAreaByPromotionsId(promotionsIdList);
    if(pAreaMap.isEmpty()){
      return result;
    }
    for(Long promotionsId : pAreaMap.keySet()){
      List<PromotionsArea> pAreaList = pAreaMap.get(promotionsId);
      if(CollectionUtils.isEmpty(pAreaList)){
        continue;
      }
      PromotionsArea firstArea = pAreaList.get(0);
      PromotionsEnum.PostType postType=firstArea.getPostType();  //同一个促销包邮类型相同
      if(PromotionsEnum.PostType.POST.equals(postType)){
        boolean promotionResult = false;
        for(PromotionsArea area:pAreaList){
          if(area==null) {
            continue;
          }
          if(area.getAreaNo()==-1l){
            promotionResult = true;
            break;
          }
          if(cityNo.equals(area.getAreaNo())){
            promotionResult = true;
            break;
          }
        }
        result.put(promotionsId, promotionResult);
      }else if(PromotionsEnum.PostType.UN_POST.equals(postType)){
        boolean promotionResult = true;
        for(PromotionsArea area:pAreaList){
          if(area==null) continue;
          if(area.getAreaNo()==-1l){
            promotionResult = false;
            break;
          }
          if(cityNo.equals(area.getAreaNo())){
            promotionResult = false;
            break;
          }
        }
        result.put(promotionsId, promotionResult);
      }
    }
    return result;
  }

  public List<PromotionOrderRecord> getPromotionOrderRecord(Long purchaseOrderId){
    if(purchaseOrderId==null){
      return null;
    }
    ProductWriter writer= productDaoManager.getWriter();
    return writer.getPromotionOrderRecord(purchaseOrderId);
  }

  @Override
  public List<PromotionOrderRecordDTO> getPromotionOrderRecordDTO(Long purchaseOrderId){
    if(purchaseOrderId==null){
      return null;
    }
    List<PromotionOrderRecord> records=getPromotionOrderRecord(purchaseOrderId);
    List<PromotionOrderRecordDTO> recordDTOs=new ArrayList<PromotionOrderRecordDTO>();
    if(CollectionUtil.isNotEmpty(records)){
      for(PromotionOrderRecord record:records){
        recordDTOs.add(record.toDTO());
      }
    }
    return recordDTOs;
  }

  @Override
  public Map<Long,PromotionOrderRecordDTO> getPromotionOrderRecordDTOMap(Long purchaseOrderId){
    List<PromotionOrderRecordDTO> recordDTOList=getPromotionOrderRecordDTO(purchaseOrderId);
    Map<Long,PromotionOrderRecordDTO> recordMap=new HashMap<Long,PromotionOrderRecordDTO>();
    if(CollectionUtil.isNotEmpty(recordDTOList)){
      for(PromotionOrderRecordDTO recordDTO:recordDTOList){
        recordMap.put(recordDTO.getProductId(),recordDTO);
      }
    }
    return recordMap;
  }

  private List<PromotionOrderRecord> getPromotionOrderRecord(Long supplierShopId,Long promotionsId,Long supplierProductId){
    ProductWriter writer= productDaoManager.getWriter();
    return writer.getPromotionOrderRecord(supplierShopId,promotionsId,supplierProductId);
  }

  public boolean savePromotionOrderRecord(PurchaseOrderDTO orderDTO){
    ProductWriter writer= productDaoManager.getWriter();
    if(ArrayUtil.isEmpty(orderDTO.getItemDTOs())){
      return false;
    }
    Object status=writer.begin();
    try{
      //保存单据促销记录
      List<PromotionOrderRecord> orderRecords=getPromotionOrderRecord(orderDTO.getId());
      Map<Long,List<PromotionOrderRecord>> recordMap=new HashMap<Long,List<PromotionOrderRecord>>();
      if(CollectionUtil.isNotEmpty(orderRecords)){
        for(PromotionOrderRecord record:orderRecords){
          List<PromotionOrderRecord> recordList= recordMap.get(record.getItemId());
          if(recordList==null){
            recordList=new ArrayList<PromotionOrderRecord>();
            recordMap.put(record.getProductId(),recordList);
          }
          recordList.add(record);
        }
      }
      for (PurchaseOrderItemDTO itemDTO : orderDTO.getItemDTOs()) {
        List<PromotionOrderRecord> recordList=recordMap.get(itemDTO.getSupplierProductId());
        if(CollectionUtil.isEmpty(recordList)){ //第一次下单
          List<PromotionOrderRecordDTO> recordDTOs=itemDTO.getPromotionOrderRecordDTOs();
          if(CollectionUtil.isEmpty(recordDTOs)){
            continue;
          }
          for(PromotionOrderRecordDTO recordDTO:recordDTOs){
            recordDTO.setProductId(itemDTO.getSupplierProductId());
            recordDTO.setCustomerShopId(orderDTO.getShopId());
            recordDTO.setSupplierShopId(orderDTO.getSupplierShopId());
            recordDTO.setOrderId(orderDTO.getId());
            recordDTO.setItemId(itemDTO.getId());
            recordDTO.setOrderType(OrderTypes.PURCHASE);
            recordDTO.setOrderStatus(orderDTO.getStatus());
            PromotionOrderRecord record=new PromotionOrderRecord();
            record.fromDTO(recordDTO);
            writer.save(record);
          }
        }else {        //改单
          PromotionsDTO promotionsDTO=PromotionsUtils.getPromotionsDTO(itemDTO.getProductDTO(), PromotionsEnum.PromotionsTypes.BARGAIN);
          if(promotionsDTO==null){
            continue;
          }
          List<PromotionOrderRecordDTO> recordDTOs=itemDTO.getPromotionOrderRecordDTOs();
          if(CollectionUtil.isNotEmpty(recordDTOs)){
            for(PromotionOrderRecordDTO recordDTO:recordDTOs){
              if(PromotionsEnum.PromotionsTypes.BARGAIN.equals(recordDTO.getPromotionsType())){
                recordDTO.setAmount(recordDTO.getAmount());
                PromotionOrderRecord record=new PromotionOrderRecord();
                writer.save(record.fromDTO(recordDTO));
              }
            }
          }
        }
      }
      writer.commit(status);
      return true;
    }finally {
      writer.rollback(status);
    }
  }

  public PromotionsDTO getPromotionsDetail(Long shopId,Long promotionsId) {
    PromotionsDTO promotionsDTO=null;
    Promotions promotions=CollectionUtil.getFirst(getPromotionsById(shopId,promotionsId));
    if(promotions==null||DeletedType.TRUE.equals(promotions.getDeleted())){
      return null;
    }
    promotionsDTO=promotions.toDTO();
    List<PromotionsRule> rules=getPromotionsRuleByPromotionsIds(shopId,promotions.getId());
    List<PromotionsRuleDTO> ruleDTOs=new ArrayList<PromotionsRuleDTO>();
    promotionsDTO.setPromotionsRuleDTOList(ruleDTOs);
    if(CollectionUtil.isNotEmpty(rules)){
      for (PromotionsRule rule:rules){
        ruleDTOs.add(rule.toDTO());
      }
    }
    return promotionsDTO;
  }

  @Override
  public void updatePromotionOrderRecordStatus(Long shopId, Long orderId, OrderStatus orderStatus) {
    ProductWriter writer = productDaoManager.getWriter();
    List<PromotionOrderRecord> promotionOrderRecords = writer.getPromotionOrderRecord(orderId);
    if(CollectionUtils.isEmpty(promotionOrderRecords)){
      return;
    }
    Object status = writer.begin();
    try{
      for(PromotionOrderRecord record : promotionOrderRecords){
        record.setOrderStatus(orderStatus);
        writer.update(record);
      }
      writer.commit(status);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public double getPromotionOrderRecordUsedAmount(Long productId, Long promotionsId, Long shopId, Long orderId) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getPromotionOrderRecordUsedAmount(productId, promotionsId, shopId, orderId);
  }

  public boolean cancelPromotionsByProductLocalInfoIds(Long shopId,Long[] productIds,ProductWriter writer){
    if (!ArrayUtils.isEmpty(productIds) ) {
      return false;
    }
    List<PromotionsProductDTO> promotionsProductDTOs=getPromotionsProductDTOByProductIds(shopId,productIds);
    if(CollectionUtil.isEmpty(promotionsProductDTOs)){
      return false;
    }
    writer.cancelPromotionsByProductLocalInfoIds(shopId,productIds);
    List<Long> promotionsIdList=new ArrayList<Long>();
    for(PromotionsProductDTO p:promotionsProductDTOs){
      promotionsIdList.add(p.getPromotionsId());
    }
    List<Promotions> promotionsList= getPromotionsById(shopId,promotionsIdList.toArray(new Long[promotionsIdList.size()]));
    for(Promotions promotions:promotionsList){
      Long endTime=promotions.getEndTime();
      if(endTime!=null&&endTime<=System.currentTimeMillis()){
        promotions.setStatus(PromotionsEnum.PromotionStatus.EXPIRE);
      }else {
        promotions.setStatus(PromotionsEnum.PromotionStatus.UN_STARTED);
      }
    }
    return true;
  }

  @Override
  public List<PromotionOrderRecordDTO> getPromotionOrderRecordByShopIdProductIdOrderId(Long orderId, Long productLocalInfoId, Long shopId, OrderTypes orderTypes) {

    List<PromotionOrderRecordDTO> recordDTOs = new ArrayList<PromotionOrderRecordDTO>();
    if (orderId == null || orderId == 0L) {
      LOG.warn("getPromotionOrderRecordByShopIdProductIdOrderId,orderId is null or 0L.");
      return recordDTOs;
    }
    if (productLocalInfoId == null || productLocalInfoId == 0L) {
      LOG.warn("getPromotionOrderRecordByShopIdProductIdOrderId,productLocalInfoId is null or 0L.");
      return recordDTOs;
    }
    if (shopId == null || shopId == 0L) {
      LOG.warn("getPromotionOrderRecordByShopIdProductIdOrderId,shopId is null or 0L.");
      return recordDTOs;
    }

    ProductWriter productWriter = productDaoManager.getWriter();
    PromotionOrderRecordQuery query = new PromotionOrderRecordQuery();
    query.setOrderId(orderId);
    query.setProductId(productLocalInfoId);
    //TODO zhuj 这个地方不确定
    if (orderTypes == OrderTypes.SALE || orderTypes == OrderTypes.SALE_RETURN) {
      query.setSupplierShopId(shopId);
    } else if (orderTypes == OrderTypes.RETURN || orderTypes == OrderTypes.PURCHASE) {
      query.setShopId(shopId);
    } else {
      throw new RuntimeException("getPromotionOrderRecordByShopIdProductIdOrderId,orderTypes is illegal.orderType: " + orderTypes);
    }
    List<PromotionOrderRecord> promotionOrderRecords = productWriter.getPromotionOrderRecordsByCondition(query);
    if (CollectionUtils.isNotEmpty(promotionOrderRecords)) {
      for (PromotionOrderRecord promotionOrderRecord : promotionOrderRecords) {
        recordDTOs.add(promotionOrderRecord.toDTO());
      }
    }
    return recordDTOs;
  }

  @Override
  public List<PromotionOrderRecordDTO> getPromotionOrderRecordsById(Long... id) {
    List<PromotionOrderRecordDTO> records = new ArrayList<PromotionOrderRecordDTO>();
    if (ArrayUtils.isEmpty(id))
      return records;
    ProductWriter productWriter = productDaoManager.getWriter();
    List<PromotionOrderRecord> promotionOrderRecords = productWriter.getPromotionOrderRecordsById(id);
    if (CollectionUtils.isNotEmpty(promotionOrderRecords)) {
      for (PromotionOrderRecord promotionOrderRecord : promotionOrderRecords) {
        records.add(promotionOrderRecord.toDTO());
      }
    }
    return records;
  }

}
