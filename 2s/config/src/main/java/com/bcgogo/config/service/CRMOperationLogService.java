package com.bcgogo.config.service;

import com.bcgogo.config.CRMOperationLogCondition;
import com.bcgogo.config.CRMOperationLogResult;
import com.bcgogo.config.dto.CRMOperationLogDTO;
import com.bcgogo.config.model.CRMOperationLog;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午8:18
 * 日志 service
 */
@Component
public class CRMOperationLogService implements ICRMOperationLogService {
  private static final Logger LOG = LoggerFactory.getLogger(CRMOperationLogService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public CRMOperationLogResult getLogsByCondition(CRMOperationLogCondition condition) {
    CRMOperationLogResult result = new CRMOperationLogResult();
    ConfigWriter writer = configDaoManager.getWriter();
    long count = writer.countCRMOperationLogsByCondition(condition);
    List<CRMOperationLogDTO> logDTOs = writer.getCRMOperationLogsByCondition(condition);
    result.setResults(logDTOs);
    result.setTotalRows(count);
    return result;
  }

  @Override
  public CRMOperationLogDTO saveOrUpdateLogs(CRMOperationLogDTO logDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    logDTO.setOperateTime(System.currentTimeMillis());
    CRMOperationLog log;
    try {
      if (logDTO.getId() != null) {
        log = writer.getById(CRMOperationLog.class, logDTO.getId());
        log.fromDTO(logDTO);
      } else {
        log = new CRMOperationLog(logDTO);
      }
      writer.save(log);
      logDTO.setId(log.getId());
      writer.commit(status);
      return logDTO;
    } finally {
      writer.rollback(status);
    }
  }
}
