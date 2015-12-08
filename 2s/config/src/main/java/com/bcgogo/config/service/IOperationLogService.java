package com.bcgogo.config.service;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.model.OperationLog;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.search.dto.OrderIndexDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
public interface IOperationLogService {
  //保存用户的操作日志，单据的操作记录已不用该方法，改用在TxnService里的saveOperationLogTxnService方法
  public void saveOperationLog(OperationLogDTO... operationLogDTO) throws Exception;
  //获取某个单据的用户操作日志
  public List<OperationLogDTO> getOprationLogByObjectId(ObjectTypes type, Long orderId);

  public List<OperationLog> getOperationLogByPager(int startPageNo, int pageSize);

  public void updateOperationLog(List<OperationLog> operationLogs);
  //得到单据对应的操作人和操作人Id
  public void setOrderOperators(Long orderId, String orderTypeStr, OrderIndexDTO orderIndexDTO);

  public void initOperationLogType();

}
