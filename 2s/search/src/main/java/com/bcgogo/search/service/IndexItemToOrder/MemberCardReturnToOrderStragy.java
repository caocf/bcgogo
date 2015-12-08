package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-22
 * Time: 上午9:51
 */
public class MemberCardReturnToOrderStragy implements IndexItemToOrderStragy {
  @Override
  public void indexItemToOrder(ItemIndexDTO itemIndexDTO, OrderDTO orderDTO) {
    if (itemIndexDTO == null || orderDTO == null) {
      return;
    }
    orderDTO.setOrderType(itemIndexDTO.getOrderType());
    orderDTO.setConsumeDate(itemIndexDTO.getOrderTimeCreated());
    orderDTO.setContent(OrderTypes.MEMBER_RETURN_CARD.getName());
    orderDTO.setTotalMoney(itemIndexDTO.getOrderTotalAmount());
    orderDTO.setOrderId(itemIndexDTO.getOrderId());
    if (orderDTO.getArrears() != 0) {
      orderDTO.setPaymentTime(itemIndexDTO.getPaymentTime());
    }
    orderDTO.setServices(StringUtil.formateStr(orderDTO.getServices()) + (StringUtil.isEmpty(orderDTO.getServices()) ? "" : JOIN_STR) + StringUtil.formateStr(itemIndexDTO.getItemName()));

    if (OrderStatus.MEMBERCARD_ORDER_STATUS == itemIndexDTO.getOrderStatus()) {
      orderDTO.setStatus(OrderStatus.MEMBERCARD_ORDER_STATUS);
    }
    orderDTO.setTotalMoney(itemIndexDTO.getOrderTotalAmount());
  }
}
