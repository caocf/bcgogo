package com.bcgogo.search.service;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.model.OrderIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.OrderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-12
 * Time: 下午12:58
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ItemIndexService implements IItemIndexService {
  private static final Logger LOG = LoggerFactory.getLogger(ItemIndexService.class);
  @Override
  public void updateItemIndexArrearsAndPaymentTime(long orderId, Double arrears, Long paymentTime) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateItemIndexByOrderId(orderId, arrears, paymentTime);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 更新订单状态，如入库之后的订单状态改为2
   *
   * @param shopId
   * @param purchaseOrderId
   * @return
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  @Override    //add by qxy 2012-03-20
  public void updateItemIndexPurchaseOrderStatus(Long shopId, OrderTypes orderType, Long purchaseOrderId, OrderStatus orderStatus) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateItemIndexPurchaseOrderStatus(shopId, orderType, purchaseOrderId, orderStatus);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 从itemindex中获得维修单相关单据
   */
  @Override
  public List<ItemIndexDTO> getRepairOrderHistory(
      long shopId, String licenceNo, String services, String materialName, Long fromTime, Long toTime,
      int startNo, int maxResult) throws Exception {
    return searchDaoManager.getWriter()
        .getRepairOrderHistory(shopId, licenceNo, services, materialName, fromTime, toTime, startNo, maxResult);
  }

  /**
   * 获取该单据下Item内容的字符串组合，已逗号分割
   *
   * @param orderId
   * @param itemType 具体类型见SearchConstant
   * @return
   * @throws BcgogoException
   */
  @Override
  public String getItemInfo(Long orderId, ItemTypes itemType) throws BcgogoException {
    if (orderId == null) return null;
    Set<String> strSet = searchDaoManager.getWriter().getItemInfo(orderId, itemType);
    StringBuffer sb = null;
    if (strSet != null && strSet.size() > 0) {
      sb = new StringBuffer();
      for (String str : strSet) {
        sb.append(str);
        sb.append(",");
      }
      return sb.substring(0, sb.length() - 1);
    }
    return null;
  }


  /**
	 * 分页获取洗车单item_index列表
   *
	 * @param shopId
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @param pager 分页组件
	 * @return
	 */
	@Override
  public List<ItemIndexDTO> getWashItemIndexListByPager(long shopId, long startTime, long endTime, Pager pager) {
	  SearchWriter writer = searchDaoManager.getWriter();

    List<ItemIndex> itemIndexList = writer.getWashItemIndexListByPager(shopId, startTime, endTime, pager);
	  List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
	  if (itemIndexList != null) {
		  for (ItemIndex itemIndex : itemIndexList) {
        if (itemIndex == null) {
				  continue;
			  }
			  ItemIndexDTO itemIndexDTO = itemIndex.toDTO();
			  itemIndexDTOList.add(itemIndexDTO);
		  }
		  return itemIndexDTOList;
	  }
	  return null;
  }

  /**
   * 根据shop_id获取开始时间到结束时间这一段时间之内的洗车单数量
   *
	 * @param shopId
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
  @Override
  public int countWashItemIndexByShopId(long shopId, long startTime, long endTime) {
	  SearchWriter writer = searchDaoManager.getWriter();
    return writer.countWashItemIndexByShopId(shopId, startTime, endTime);
  }

  @Override
  public OrderIndexDTO getItemIndexesByOrderId(long shopId, long orderId) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    OrderIndexDTO orderIndexDTO = null;
    //通过orderId 得到 orderIndex
    List<OrderIndex> orderIndexList = writer.getOrderIndexDTOByOrderId(shopId, orderId);
    if (CollectionUtils.isEmpty(orderIndexList)) throw new Exception("orderIndex find by orderId[" + orderId + "] is null.");
    orderIndexDTO = orderIndexList.get(0).toDTO();
    //通过orderId 得到 itemIndexs
    List<ItemIndex> itemIndexes = writer.getItemIndexDTOByOrderId(shopId, orderId);
    List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
    ItemIndexDTO itemIndexDTO = null;
    for (ItemIndex itemIndex : itemIndexes) {
      if (itemIndex.getItemTypeEnum().name().equals(ItemTypes.SALE_MEMBER_CARD.name())) continue;
      itemIndexDTO = itemIndex.toDTO();
      itemIndexDTOs.add(itemIndexDTO);
    }
    orderIndexDTO.setItemIndexDTOList(itemIndexDTOs);
    return orderIndexDTO;
  }

  @Override
  public List<ItemIndex> getItemIndexDTOByOrderId(long shopId, long orderId) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getItemIndexDTOByOrderId(shopId, orderId);
  }

  @Autowired
  private SearchDaoManager searchDaoManager;


  /**
   * 根据productIds获得入库
   * @param shopId
   * @param productIdSet
   * @return
   */
  public List<ItemIndexDTO> getInventoryItemIndexDTOByProductIds(Long shopId,Set<Long> productIdSet) {
    SearchWriter writer = searchDaoManager.getWriter();

    Map<OrderTypes, List<OrderStatus>> settledStatusMap = new HashMap<OrderTypes, List<OrderStatus>>();
    settledStatusMap.put(OrderTypes.INVENTORY, OrderUtil.purchaseInventorySettled);
    settledStatusMap.put(OrderTypes.SALE_RETURN, OrderUtil.salesReturnSettled);

    List<ItemIndex> itemIndexList = writer.getItemIndexByProductIdOrderStatus(productIdSet, shopId, settledStatusMap);
    if (CollectionUtil.isEmpty(itemIndexList)) {
      return null;
    }
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    for (ItemIndex itemIndex : itemIndexList) {
      itemIndexDTOList.add(itemIndex.toDTO());
    }

    return itemIndexDTOList;
  }

}
