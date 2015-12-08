package com.bcgogo.txn.service;

import com.bcgogo.enums.RemindEventType;
import com.bcgogo.txn.service.DebtRemindEventStrategy;
import com.bcgogo.txn.service.RemindEventStrategy;
import com.bcgogo.txn.service.RepairRemindEventStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-9
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RemindEventStrategySelector {
  
  private static Map<RemindEventType,RemindEventStrategy> remindEventStrategyMap;
  
  static {
    remindEventStrategyMap = new HashMap<RemindEventType,RemindEventStrategy>();
    remindEventStrategyMap.put(RemindEventType.REPAIR, new RepairRemindEventStrategy());
    remindEventStrategyMap.put(RemindEventType.DEBT, new DebtRemindEventStrategy());
    remindEventStrategyMap.put(RemindEventType.TXN, new TxnRemindEventStrategy());
    remindEventStrategyMap.put(RemindEventType.CUSTOMER_SERVICE, new CustomerServiceRemindEventStrategy());
  }

  public RemindEventStrategy selectStrategy(RemindEventType type) {
    return remindEventStrategyMap.get(type);
  }
}
