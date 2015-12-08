package com.bcgogo.txn.service;

import com.bcgogo.enums.RemindEventType;
import com.bcgogo.remind.dto.RemindEventDTO;

import java.util.List;

/**
 * 待办事项处理查询接口
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-9
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
public interface RemindEventStrategy {

  public List<RemindEventDTO> queryRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long deadline, Integer pageNo, Integer pageSize);

  public int countRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long deadline);

  public int RFCountRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long deadline);


}
