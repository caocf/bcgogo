package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;

/**
 * IndexItem 合并到OrderDTO的策略
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-20
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public interface IndexItemToOrderStragy {

    public static final String JOIN_STR = ",";

    public void indexItemToOrder(ItemIndexDTO itemIndexDTO, OrderDTO orderDTO);

}
