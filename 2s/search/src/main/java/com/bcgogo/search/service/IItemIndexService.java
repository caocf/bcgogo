package com.bcgogo.search.service;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.ItemIndex;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-12
 * Time: 下午12:57
 * To change this template use File | Settings | File Templates.
 */
public interface IItemIndexService {
  public void updateItemIndexArrearsAndPaymentTime(long orderId, Double arrears, Long paymentTime);

  public void updateItemIndexPurchaseOrderStatus(Long shopId, OrderTypes orderType,Long purchaseOrderId, OrderStatus orderStatus);

  /**
   * 从itemindex中获得维修单相关单据
   */
  public List<ItemIndexDTO> getRepairOrderHistory(
      long shopId, String licenceNo, String services, String materialName, Long fromTime, Long toTime,
      int startNo, int maxResult) throws Exception;

  /**
   * 获取该单据下Item内容的字符串组合，已逗号分割
   *
   *
   * @param orderId
   * @param itemType 具体类型见SearchConstant
   * @return
   * @throws BcgogoException
   * @author wjl
   */
  public String getItemInfo(Long orderId, ItemTypes itemType) throws BcgogoException;


	/**
	 * 分页获取洗车单item_index列表
	 * @param shopId
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @param pager 分页组件
	 * @return
	 */
  public List<ItemIndexDTO> getWashItemIndexListByPager(long shopId, long startTime, long endTime,Pager pager);

	/** 根据shop_id获取开始时间到结束时间这一段时间之内的洗车单数量
	 * @param shopId
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
  public int countWashItemIndexByShopId(long shopId, long startTime, long endTime);

  /**
   * 通过 orderId 获得items
   * @param shopId
   * @param orderId
   * @return
   * @throws Exception
   */
  OrderIndexDTO getItemIndexesByOrderId(long shopId, long orderId) throws Exception;

  /**
   * 根据shop_id和order_id获得itemIndex列表
   * @param shopId
   * @param orderId
   * @return
   */
  public List<ItemIndex> getItemIndexDTOByOrderId(long shopId, long orderId);


  /**
   * 根据productIds获得入库
   * @param shopId
   * @param productIdSet
   * @return
   */
  public List<ItemIndexDTO> getInventoryItemIndexDTOByProductIds(Long shopId,Set<Long> productIdSet);
}
