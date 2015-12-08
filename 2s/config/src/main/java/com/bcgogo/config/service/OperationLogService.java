package com.bcgogo.config.service;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.OperationLog;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午5:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OperationLogService implements IOperationLogService {
    private static final Logger LOG = LoggerFactory.getLogger(OperationLogService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public void saveOperationLog(OperationLogDTO... operationLogDTOs) throws Exception {
    if(ArrayUtil.isEmpty(operationLogDTOs)){
      LOG.warn(BcgogoExceptionType.IllegalArgument.getMessage());
      return;
    }

    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
        for(OperationLogDTO operationLogDTO:operationLogDTOs){
          writer.save(new OperationLog(operationLogDTO));
        }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<OperationLogDTO> getOprationLogByObjectId(ObjectTypes type, Long objectId){
    List<OperationLogDTO> operationLogDTOList = new ArrayList<OperationLogDTO>();
    ConfigWriter writer = configDaoManager.getWriter();
    List<OperationLog> operationLogList = writer.getOprationLogByObjectId(type,objectId);
    if(CollectionUtil.isNotEmpty(operationLogList)){
      for(OperationLog operationLog : operationLogList){
        operationLogDTOList.add(operationLog.toDTO());
      }
    }
    return operationLogDTOList;
  }

  @Override
  public List<OperationLog> getOperationLogByPager(int startPageNo, int pageSize) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getOperationLogByPager(startPageNo,pageSize);
  }

  @Override
  public void updateOperationLog(List<OperationLog> operationLogs) {
    if(CollectionUtils.isEmpty(operationLogs)) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for(OperationLog operationLog : operationLogs) {
         writer.update(operationLog);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void setOrderOperators(Long orderId, String orderTypeStr, OrderIndexDTO orderIndexDTO) {
    if(orderId == null || StringUtils.isEmpty(orderTypeStr) || orderIndexDTO == null) return;
    ObjectTypes type = null;
    Set<String> operators = new HashSet<String>();
    Set<Long> operatorIds = new HashSet<Long>();
    if ("purchase".equals(orderTypeStr)) {
      type = ObjectTypes.PURCHASE_ORDER;
    } else if ("purchase_return".equals(orderTypeStr)) {
      type = ObjectTypes.PURCHASE_RETURN_ORDER;
    } else if ("sale".equals(orderTypeStr)) {
      type = ObjectTypes.SALE_ORDER;
    } else if ("sale_return".equals(orderTypeStr)) {
      type = ObjectTypes.SALE_RETURN_ORDER;
    } else if("inventory".equals(orderTypeStr)) {
      type = ObjectTypes.INVENTORY_ORDER;
    } else if("wash".equals(orderTypeStr)) {
      type = ObjectTypes.WASH_ORDER;
    } else if("repair".equals(orderTypeStr)) {
      type = ObjectTypes.REPAIR_ORDER;
    }
    List<OperationLogDTO> operationLogDTOList = getOprationLogByObjectId(type, orderId);
    if(!CollectionUtils.isEmpty(operationLogDTOList)) {
      for(OperationLogDTO operationLogDTO : operationLogDTOList) {
        if(operationLogDTO.getUserId() != null) {
          operatorIds.add(operationLogDTO.getUserId());
        }
        if(StringUtils.isNotEmpty(operationLogDTO.getUserName())) {
          operators.add(operationLogDTO.getUserName());
        }
      }
      orderIndexDTO.setOperators(operators);
      orderIndexDTO.setOperatorIds(operatorIds);
    }

  }

  @Override
  public void initOperationLogType() {
    List<String> objectTypesList = new ArrayList<String>();
    objectTypesList.add(ObjectTypes.SALE_ORDER.toString());
    objectTypesList.add(ObjectTypes.SALE_RETURN_ORDER.toString());
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateSalesOrderOperationLogType(objectTypesList);
      writer.updateRepairOrderOperationLogType();
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}
