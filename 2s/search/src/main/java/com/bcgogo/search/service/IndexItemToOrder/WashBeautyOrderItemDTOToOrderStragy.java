package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-27
 * Time: 上午1:43
 * To change this template use File | Settings | File Templates.
 */
public class WashBeautyOrderItemDTOToOrderStragy implements IndexItemToOrderStragy{
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

    orderDTO.setOrderId(itemIndexDTO.getOrderId());
    if (orderDTO.getArrears() != 0) {
      orderDTO.setPaymentTime(itemIndexDTO.getPaymentTime());
    }
    String appendServices = getServicesByItemType(itemIndexDTO);
    orderDTO.setServices(StringUtil.formateStr(orderDTO.getServices()) + (StringUtil.isEmpty(orderDTO.getServices()) || StringUtil.isEmpty(appendServices) ? "" : JOIN_STR) + appendServices);
    orderDTO.setStatus(itemIndexDTO.getOrderStatus());
    orderDTO.setTotalMoney(itemIndexDTO.getOrderTotalAmount());     //+orderDTO.getTotalMoney()
    orderDTO.setVehicle(itemIndexDTO.getVehicle());
    orderDTO.setUrl("washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + itemIndexDTO.getOrderId().toString());
  }

  private String getServicesByItemType(ItemIndexDTO itemIndexDTO) {
    if (itemIndexDTO == null || itemIndexDTO.getItemType() == null) {
      return StringUtil.EMPTY_STRING;
    }
    if (ItemTypes.WASH == itemIndexDTO.getItemType()) {
      return StringUtil.formateStr(itemIndexDTO.getItemName());
    }
    return StringUtil.EMPTY_STRING;
  }
}
