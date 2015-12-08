package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-20
 * Time: 下午5:27
 * To change this template use File | Settings | File Templates.
 */
public class RepairIndexItemToOrderStragy implements IndexItemToOrderStragy {
  @Override
  public void indexItemToOrder(ItemIndexDTO itemIndexDTO, OrderDTO orderDTO) {
    if (itemIndexDTO == null || orderDTO == null) {
      return;
    }
    orderDTO.setOrderType(itemIndexDTO.getOrderType());
    orderDTO.setArrears(itemIndexDTO.getArrears());     //orderDTO.getArrears() +
    orderDTO.setCompletedDate(itemIndexDTO.getLastUpdate());
    orderDTO.setConsumeDate(itemIndexDTO.getOrderTimeCreated());
    orderDTO.setContent(itemIndexDTO.getOrderType()==null?"":itemIndexDTO.getOrderType().getName());
    String appendMaterial = getMaterialByItemType(itemIndexDTO);
    orderDTO.setMaterial(StringUtil.formateStr(orderDTO.getMaterial()) + (StringUtil.isEmpty(orderDTO.getMaterial()) || StringUtil.isEmpty(appendMaterial) ? "" : JOIN_STR) + appendMaterial);
    orderDTO.setOrderId(itemIndexDTO.getOrderId());
    if (orderDTO.getArrears() != 0) {
      orderDTO.setPaymentTime(itemIndexDTO.getPaymentTime());
    }
    String appendServices = getServicesByItemType(itemIndexDTO);
    orderDTO.setServices(StringUtil.formateStr(orderDTO.getServices()) + (StringUtil.isEmpty(orderDTO.getServices()) || StringUtil.isEmpty(appendServices) ? "" : JOIN_STR) + appendServices);
    orderDTO.setStatus(itemIndexDTO.getOrderStatus());
    orderDTO.setTotalMoney(itemIndexDTO.getOrderTotalAmount());     //+orderDTO.getTotalMoney()
    orderDTO.setVehicle(itemIndexDTO.getVehicle());
    orderDTO.setUrl("txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + itemIndexDTO.getOrderId().toString());
  }

  /**
   * 根据item类型获取施工内容
   *
   * @param itemIndexDTO
   * @return
   */
  private String getServicesByItemType(ItemIndexDTO itemIndexDTO) {
    if (itemIndexDTO == null || itemIndexDTO.getItemType() == null) {
      return StringUtil.EMPTY_STRING;
    }
    if (ItemTypes.SERVICE == itemIndexDTO.getItemType()) {
      return StringUtil.formateStr(itemIndexDTO.getItemName());
    }
    return StringUtil.EMPTY_STRING;
  }

  private String getMaterialByItemType(ItemIndexDTO itemIndexDTO) {
    if (itemIndexDTO == null || itemIndexDTO.getItemType() == null) {
      return StringUtil.EMPTY_STRING;
    }
    if (ItemTypes.MATERIAL == itemIndexDTO.getItemType()) {
      return StringUtil.formateStr(itemIndexDTO.getItemName());
    }
    return StringUtil.EMPTY_STRING;
  }
}
