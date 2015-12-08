package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.common.Pager;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.PreBuyOrderSearchCondition;
import com.bcgogo.search.dto.QuotedPreBuyOrderSearchConditionDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.PreBuyOrderItem;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:44
 */
public interface IPreBuyOrderService {

  /**
   * 改造后每个item都会生成一个order
   * @param shopId
   * @param preBuyOrderDTO
   * @throws Exception
   */
  void savePreBuyOrder(Long shopId, PreBuyOrderDTO... preBuyOrderDTO) throws Exception;

  List<PreBuyOrderDTO> getPreBuyOrdersByShopId(Long shopId, int pageStart, int pageSize);

  List<PreBuyOrderItemDTO> getPreBuyOrderItemDetailDTO(PreBuyOrderSearchCondition condition) throws ParseException;

  List<PreBuyOrderItemDTO> getLatestPreBuyOrderItemDTO(Long shopId,int pageStart, int pageSize);

  PreBuyOrderDTO getPreBuyOrderDTOById(Long shopId, Long preBuyOrderId) throws Exception;

  List<PreBuyOrderItemDTO>  filterPreBuyOrderItemByShopKind(Long shopId,PreBuyOrderItemDTO ...preBuyOrderItemDTOs);

  PreBuyOrderDTO getSimplePreBuyOrderDTOById(Long preBuyOrderId) throws Exception;

  PreBuyOrderDTO getSimplePreBuyOrderDTOByQuotedPreBuyOrderItemId(Long quotedPreBuyOrderItemId) throws Exception;

  List<QuotedPreBuyOrderItemDTO> getLatestQuotedPreBuyOrderItemByCustomerShopId(Long customerShopId,Integer limit);

  PreBuyOrderItemDTO getPreBuyOrderItemDTOById(Long preBuyOrderItemId) throws Exception;

  Map<Long,PreBuyOrderItemDTO> getPreBuyOrderItemDTOMapByIds(Long shopId,Long... preBuyOrderItemIds);

  List<PreBuyOrderItemDTO> getPreBuyOrderItemDTOByIds(Long shopId,Long... preBuyOrderItemIds);

  List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByPreBuyOrderItemId(Long shopId,Long preBuyOrderItemId) throws Exception;

  List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByPreBuyOrderId(Long shopId,Long preBuyOrderId) throws Exception;


  Long countPreBuyOrderItems(Long shopId) throws Exception;

  Long countValidPreBuyOrderItems(Long shopId) throws Exception;

  Long countQuotedPreBuyOrders(Long shopId) throws Exception;

  /**
   * 卖家给shopId的报价次数
   * @param shopId
   * @return
   * @throws Exception
   */
  Long countQuotedPreBuyOrderItems(Long shopId) throws Exception;

  /**
   * 通过报价 下单的次数
   * @param shopId
   * @return
   * @throws Exception
   */
  Long countOrdersFromQuotedPreBuyOrder(Long shopId) throws Exception;

  int countQuotedPreBuyOrderSupplier(QuotedPreBuyOrderSearchConditionDTO conditionDTO);

  List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByItemId(Long... quotedPreBuyOrderItemId) throws Exception;

  QuotedPreBuyOrderDTO getQuotedPreBuyOrderDTO(Long orderId);

  List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrderIdsByItemId(Long shopId,Long... quotedBuyOrderItemId) throws Exception;

  List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrderDtoList(Long shopId, int rowStart, int pageSize) throws Exception;

  void simpleUpdateQuotedPreBuyOrderItem(QuotedPreBuyOrderItemDTO itemDTO);

  void saveOrUpdateQuotedPreBuyOrder(Long shopId, QuotedPreBuyOrderDTO quotedPreBuyOrderDTO) throws Exception;

  QuotedPreBuyOrderItemDTO getQuotedPreBuyOrderItemDTOById(Long quotedPreBuyOrderItemId) throws Exception;

  List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByIds(Long shopId,Long... quotedPreBuyOrderItemId);

  List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItem(QuotedPreBuyOrderSearchConditionDTO conditionDTO);

  void addMyQuotedToPreBuyOrderItemDTO(Long shopId,PreBuyOrderItemDTO ...preBuyOrderItemDTOs);

  int countSupplierOtherQuotedItems(Long quoterShopId,Long preBuyerShopId,Long quotedPreBuyOrderItemId);

  List<QuotedPreBuyOrderItemDTO> getSupplierOtherQuotedItems(Long quoterShopId,Long preBuyerShopId,Long quotedPreBuyOrderItemId,Pager pager);

  Long countQuotedPreBuyOrdersByPreBuyOrderId(Long shopId,Long preBuyOrderId) throws Exception;

  List<ProductDTO> preBuyOrderFilter(List<ProductDTO> productDTOs,Long shopId) throws Exception;

  List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrdersByQuotePreBuyOrderIds(Set<Long> quotedPreBuyOrderIds);

  List<PreBuyOrderDTO> createPreBuyOrderByLackRepairOrderDTO(RepairOrderDTO repairOrderDTO) throws Exception;

  List<PreBuyOrderDTO> createPreBuyOrderByProductDTO(Long shopId,BusinessChanceType businessChanceType,ProductDTO... productDTO) throws Exception;

  Long countValidPreBuyOrderItemsByType(Long shopId, BusinessChanceType type);

  List<PreBuyOrderDTO> getValidPreBuyOrderDTOsByShopIdWithoutSelf(Long shopId, Long preBuyOrderId);  //用于求购商品详情页面，该买家其他商机，排除该条商机

  Map<String,Object> getValidPreBuyOrderInfo(PreBuyOrderSearchCondition condition); //用于求购商品详情页面,该买家其他商机

  List<PreBuyOrderDTO> getOtherShopPreBuyOrders(PreBuyOrderSearchCondition condition); //用于求购商品详情页面,除了该买家之外的其他买家商机

  List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByPager(Long preBuyOrderItemId, int pageStart, int pageSize) throws Exception;

  Long countOtherShopPreBuyOrders(Long shopId,Long noneShopId);

  void processLackAutoPreBuy() throws Exception;

}
