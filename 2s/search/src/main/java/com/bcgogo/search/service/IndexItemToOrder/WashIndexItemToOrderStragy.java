package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-20
 * Time: 下午5:26
 * To change this template use File | Settings | File Templates.
 */
public class WashIndexItemToOrderStragy implements IndexItemToOrderStragy {

  @Override
  public void indexItemToOrder(ItemIndexDTO itemIndexDTO, OrderDTO orderDTO) {
    if (itemIndexDTO == null || orderDTO == null) {
      return;
    }
    orderDTO.setOrderType(itemIndexDTO.getOrderType());
    Double arrears = itemIndexDTO.getArrears();
    orderDTO.setArrears(arrears == null ? 0 : arrears);
    //orderDTO.setArrears(orderDTO.getArrears() + itemIndexDTO.getArrears());
    orderDTO.setCompletedDate(itemIndexDTO.getLastUpdate());
    orderDTO.setConsumeDate(itemIndexDTO.getOrderTimeCreated());
    orderDTO.setContent(itemIndexDTO.getOrderType() == null ? "" : itemIndexDTO.getOrderType().getName());
    orderDTO.setOrderId(itemIndexDTO.getOrderId());
    if (orderDTO.getArrears() != 0) {
      orderDTO.setPaymentTime(itemIndexDTO.getPaymentTime());
    }
    orderDTO.setServices(StringUtil.formateStr(orderDTO.getServices()) + (StringUtil.isEmpty(orderDTO.getServices()) ? "" : JOIN_STR) + StringUtil.formateStr(itemIndexDTO.getServices()));
    orderDTO.setStatus(OrderStatus.WASH_SETTLED);
    orderDTO.setTotalMoney(itemIndexDTO.getOrderTotalAmount());
    orderDTO.setVehicle(itemIndexDTO.getVehicle());
//     当只有洗车的时候不显示  点击详情
//        orderDTO.setUrl("txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId="+itemIndexDTO.getOrderId().toString());
  }
}
