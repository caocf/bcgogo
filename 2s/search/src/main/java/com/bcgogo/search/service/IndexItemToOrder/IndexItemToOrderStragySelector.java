package com.bcgogo.search.service.IndexItemToOrder;

import com.bcgogo.enums.OrderTypes;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略选择器
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-20
 * Time: 下午5:35
 * To change this template use File | Settings | File Templates.
 */
@Component
public class IndexItemToOrderStragySelector {

    private static Map<OrderTypes, IndexItemToOrderStragy> stragyMap;

    static{
        stragyMap = new HashMap<OrderTypes, IndexItemToOrderStragy>();
        stragyMap.put(OrderTypes.SALE, new SaleIndexItemToOrderStragy());
        stragyMap.put(OrderTypes.REPAIR, new RepairIndexItemToOrderStragy());
        stragyMap.put(OrderTypes.WASH, new WashIndexItemToOrderStragy());
        stragyMap.put(OrderTypes.RECHARGE, new WashIndexItemToOrderStragy());
        stragyMap.put(OrderTypes.WASH_MEMBER, new WashIndexItemToOrderStragy());
        stragyMap.put(OrderTypes.MEMBER_BUY_CARD, new MemberCardSaleItemToOrderStragy());
        stragyMap.put(OrderTypes.WASH_BEAUTY,new WashBeautyOrderItemDTOToOrderStragy());
        stragyMap.put(OrderTypes.MEMBER_RETURN_CARD, new MemberCardReturnToOrderStragy());
        stragyMap.put(OrderTypes.SALE_RETURN, new SaleReturnIndexItemToOrderStragy());
    }

    public IndexItemToOrderStragy selectStragy(OrderTypes orderType){
        return stragyMap.get(orderType);
    }
}
