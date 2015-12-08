package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-30
 * Time: 上午10:23
 */
public class SaleReturnIndexItemToOrderStragy implements IndexItemToOrderStragy {
  @Override
  public void indexItemToOrder(ItemIndexDTO itemIndexDTO, OrderDTO orderDTO) {
    if (itemIndexDTO == null || orderDTO == null) {
      return;
    }
    orderDTO.setOrderType(itemIndexDTO.getOrderType());
    orderDTO.setCompletedDate(itemIndexDTO.getLastUpdate());
    orderDTO.setConsumeDate(itemIndexDTO.getOrderTimeCreated());
    orderDTO.setContent(itemIndexDTO.getOrderType()==null?"":itemIndexDTO.getOrderType().getName());
    orderDTO.setMaterial(StringUtil.formateStr(orderDTO.getMaterial()) + (StringUtil.isEmpty(orderDTO.getMaterial()) ? "" : JOIN_STR) + StringUtil.formateStr(itemIndexDTO.getItemName()));
    orderDTO.setOrderId(itemIndexDTO.getOrderId());
    orderDTO.setStatus(itemIndexDTO.getOrderStatus());
    orderDTO.setStatusStr(itemIndexDTO.getOrderStatusStr());
    orderDTO.setTotalMoney(itemIndexDTO.getOrderTotalAmount());
    orderDTO.setUrl("salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + itemIndexDTO.getOrderId().toString());
  }
}
