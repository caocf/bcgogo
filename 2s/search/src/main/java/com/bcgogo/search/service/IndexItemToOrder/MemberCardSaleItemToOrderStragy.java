package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-27
 * Time: 下午4:41
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardSaleItemToOrderStragy implements IndexItemToOrderStragy{
  @Override
  public void indexItemToOrder(ItemIndexDTO itemIndexDTO, OrderDTO orderDTO)
  {
    if (itemIndexDTO == null || orderDTO == null) {
      return;
    }
    orderDTO.setOrderType(itemIndexDTO.getOrderType());
    if(itemIndexDTO.getArrears()!=null){
      orderDTO.setArrears(itemIndexDTO.getArrears());
    }else{
      orderDTO.setArrears(0d);
    }
//    orderDTO.setCompletedDate(itemIndexDTO.getLastUpdate());
    orderDTO.setConsumeDate(itemIndexDTO.getOrderTimeCreated());
    orderDTO.setContent(OrderTypes.MEMBER_BUY_CARD.getName());
    orderDTO.setServices(StringUtils.isBlank(itemIndexDTO.getItemName())?"快捷购卡":itemIndexDTO.getItemName());
    if(itemIndexDTO.getItemType() == ItemTypes.SALE_MEMBER_CARD){
      orderDTO.setTotalMoney(itemIndexDTO.getOrderTotalAmount() ==null?0d:itemIndexDTO.getOrderTotalAmount());
    }
    orderDTO.setOrderId(itemIndexDTO.getOrderId());
    if (orderDTO.getArrears() != 0) {
      if(ItemTypes.SALE_MEMBER_CARD.equals(itemIndexDTO.getItemType()))
      {
        orderDTO.setPaymentTime(itemIndexDTO.getPaymentTime());
      }

    }
    orderDTO.setServices(StringUtil.formateStr(orderDTO.getServices()) + (StringUtil.isEmpty(orderDTO.getServices()) ? "" : JOIN_STR) + StringUtil.formateStr(itemIndexDTO.getServices()));

    if(OrderStatus.MEMBERCARD_ORDER_STATUS == itemIndexDTO.getOrderStatus())
    {
      orderDTO.setStatus(OrderStatus.MEMBERCARD_ORDER_STATUS);
    }
    orderDTO.setVehicle(itemIndexDTO.getVehicle());
  }
}
