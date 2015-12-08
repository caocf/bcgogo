package com.bcgogo.product.service;

import com.bcgogo.common.Result;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.*;
import com.bcgogo.product.productManage.PromotionSearchCondition;
import com.bcgogo.txn.dto.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-8
 * Time: 上午5:15
 * To change this template use File | Settings | File Templates.
 */
public interface IPromotionsService {

  /**
   *
   * 目前情况 促销和商品的一对一  此方法适用   key 为 productLocalInfoId
   * @param unexpired 排除过期传入true, 不排除过期传false
   * @return
   * @throws Exception
   */
  public Map<Long,List<PromotionsDTO>> getPromotionsDTOMapByProductLocalInfoId(Long shopId,boolean unexpired, Long... productLocalInfoId) throws Exception;

  Map<Long,ProductDTO>  getProductPromotionDetail(Long shopId,Long... productIdArr) throws Exception;


  /**
   * 新增或者编辑 全场促销   编辑==删除+新增
   * @param shopId
   * @param promotionsDTO
   * @throws Exception
   */
  public void saveAllPromotionsDTO(Long shopId, PromotionsDTO promotionsDTO) throws Exception;

  /**
   * 针对单个商品 新增或者编辑  个性促销
   * @param shopId
   * @param promotionsDTO
   * @throws Exception
   */
  public void saveSingleProductSinglePromotionsDTO(Long shopId, PromotionsDTO promotionsDTO,Long productLocalInfoId) throws Exception;
  /**
   * 针对多个商品 新增或者编辑  个性促销
   * @param shopId
   * @param promotionsDTO
   * @throws Exception
   */
//  public void saveMultipleProductSinglePromotionsDTO(Long shopId, PromotionsDTO promotionsDTO,Long... productLocalInfoIds) throws Exception;

  /**
   *
   * @param shopId
   * @param range
   * @throws Exception
   */
  public List<PromotionsDTO> getPromotionsDTOByRange(Long shopId, PromotionsEnum.PromotionsRanges range) throws Exception;

  /**
   *
   * @param shopId
   * @param ids
   * @throws Exception
   */
  public List<PromotionsDTO> getPromotionsDTODetailById(Long shopId, Long... ids) throws Exception;

  public void updatePromotionsForGoodsInOff(Long shopId, Long... productLocalInfoId) throws Exception;

  public void deletePromotionsByPromotionsId(Long shopId, Long promotionsId) throws Exception;

  public List<PromotionsProductDTO> getPromotionsProductDTOByPromotionsId(Long shopId, Long... promotionsIds) throws Exception;

  public List<PromotionsProduct> getPromotionsProductByPromotionsId(Long shopId, Long... promotionsIds) throws Exception;

  List<PromotionsProductDTO> getPromotionsProductDTOByProductIds(Long shopId, Long... productIds);

  List<PromotionsProductDTO> getPromotionsProductDTO(PromotionSearchCondition condition);

  public void addGoodsToPromotions(Long shopId, Long promotionsId, Long... productLocalInfoIds) throws Exception;

  /**
   *
   * @param shopId
   * @param productLocalInfoId
   * @throws Exception
   */
  public List<PromotionsDTO> getPromotionsDTODetailByProductLocalInfoId(Long shopId, Long productLocalInfoId) throws Exception;

  List<PromotionsDTO> getSimplePromotionsDTOByProductLocalInfoId(Long shopId, Long productId) throws Exception;

  Result updatePromotionStatus(Result result,PromotionsDTO promotionsDTO) throws Exception;

  Map<Long,List<PromotionsDTO>> getSimplePromotionsDTO(Set<Long> shopIdSet,Long... productIds);

  Result batchUpdatePromotionStatus(Result result,List<Long> promotionsIdList,PromotionsEnum.PromotionStatus promotionStatus) throws Exception;

  Result handleExpirePromotions(Result result,Long shopId,List<Long> promotionsIdList) throws Exception;

  Result savePromotions(Result result,PromotionsDTO promotionsDTO) throws Exception;

  Result savePromotionsForInSales(Result result,PromotionIndex promotionsDTO) throws Exception;

  Result deletePromotions(Result result,Long shopId,Long promotionsId) throws Exception;

  Map<Long, List<PromotionsArea>> getPromotionsAreaByPromotionsId(Long... promotionsIdList);

//   Result updatePromotions(PromotionsDTO promotionsDTO,Result result) throws ParseException;

  int countPromotions(PromotionIndex condition);

  List<Promotions> getPromotionsById(Long shopId,Long... promotionsId);

  List<PromotionsDTO> getPromotionsDTOById(Long shopId,Long... promotionsId);

  List<Promotions> getPromotionsByPromotionsType(Long shopId,PromotionsEnum.PromotionsTypes type);

  List<Promotions> getCurrentPromotions(Long shopId);

  List<PromotionsRule> getPromotionsRuleByPromotionsIds(Long shopId,Long... promotionsIds);

  List<PromotionsRuleDTO> getPromotionsRuleDTOByPromotionsIds(Long shopId,Long... promotionsIds);

  Map<Long,List<PromotionsRuleDTO>> getPromotionsRuleDTOMap(Long shopId,Long... promotionsIds);

  List<PromotionsRuleMJS> getPromotionsRuleMJSByRuleIds(Long shopId,Long... ruleIds);

  List<Promotions> getPromotions(PromotionIndex condition);

  List<PromotionsDTO> getPromotionsDTO(PromotionIndex condition);

  List<PromotionsDTO> getPromotionDetail(Long shopId,Long ... promotionsId);


  List<PromotionsDTO> getPromotionDetail(PromotionIndex condition);

  /**
   * 查询时间段重叠的促销的商品id
   * @param shopId
   * @param target
   * @param includeTargetFlag  是否过滤当前
   * @return
   * @throws Exception
   */
  List<Long> getOverlappingProductIdByRange(Long shopId,PromotionsDTO target,Boolean includeTargetFlag) throws Exception;

  public Map<Long,Long> getOverlappingProductIdMapByRange(Long shopId,PromotionsDTO target,Boolean includeTargetFlag) throws Exception;

  //店铺只有一个客户优惠
  Promotions getSpecialCustomer(Long shopId);

  Result calculateSpecialCustomer(Long shopId,Long supplierShopId,Double total) throws Exception;

  Result addPromotionsProductForInSales(Result result,Long shopId,ProductDTO... productDTOs) throws Exception;

  Result addPromotionsProduct(Result result,Long shopId,Map<Long,PromotionsProductDTO[]> promotionsProductDTOMap) throws Exception;

  ProductDTO[] addPromotionInfoToProductDTO(ProductDTO... productDTOs) throws Exception;

  ProductDTO[] addUsingPromotionToProductDTO(ProductDTO... productDTOs) throws Exception;

  Result deletePromotionsProduct(Result result,Long shopId,Long promotionsId,Long... productIds) throws Exception;

  Map<String,String> calculateOrderTotal(PurchaseOrderDTO purchaseOrderDTO, Map<Long,ProductDTO> pMap);

  List<PromotionOrderRecordDTO> getPromotionOrderRecordDTO(Long purchaseOrderId);

  Map<Long,PromotionOrderRecordDTO> getPromotionOrderRecordDTOMap(Long purchaseOrderId);

  Map<Long, Boolean> judgePromotionsAreaByShopId(Long customerShopId, Long... promotionsIdList);

  boolean savePromotionOrderRecord(PurchaseOrderDTO purchaseOrderDTO);

  PromotionsDTO getPromotionsDetail(Long shopId,Long promotionsId);

  void updatePromotionOrderRecordStatus(Long shopId, Long orderId, OrderStatus orderStatus);

  boolean cancelPromotionsByProductLocalInfoIds(Long shopId,Long[] productIds,ProductWriter writer);

  double getPromotionOrderRecordUsedAmount(Long productId, Long promotionsId, Long shopId, Long orderId);

  /**
   * 通过orderId 和 productLocalInfoId 查询 某个单据下单个产品的促销记录
   *
   * @param orderId
   * @param productLocalInfoId
   * @param shopId
   * @param orderTypes
   * @return
   */
  public List<PromotionOrderRecordDTO> getPromotionOrderRecordByShopIdProductIdOrderId(Long orderId, Long productLocalInfoId, Long shopId, OrderTypes orderTypes);

  public List<PromotionOrderRecordDTO> getPromotionOrderRecordsById(Long... id);


/*
  public boolean isPromotionOrderRecordTheLast(PromotionOrderRecordDTO promotionOrderRecordDTO,PromotionsDTO promotionsDTO);*/

}
