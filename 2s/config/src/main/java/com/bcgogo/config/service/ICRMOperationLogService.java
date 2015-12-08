package com.bcgogo.config.service;

import com.bcgogo.config.CRMOperationLogCondition;
import com.bcgogo.config.CRMOperationLogResult;
import com.bcgogo.config.dto.CRMOperationLogDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午8:18
 */
public interface ICRMOperationLogService {
  //根据条件搜索日志
  CRMOperationLogResult getLogsByCondition(CRMOperationLogCondition condition);

  //保存日志
  CRMOperationLogDTO saveOrUpdateLogs(CRMOperationLogDTO logDTO);
}
