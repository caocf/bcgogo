package com.bcgogo.user.service.permission;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingDetailResult;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.PageException;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.permission.StaffSearchCondition;
import com.bcgogo.user.model.SalesMan;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-18
 * Time: 下午11:00
 * 员工service
 */
@Component
public class StaffService implements IStaffService {

  private static final Logger LOG = LoggerFactory.getLogger(StaffService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public PagingDetailResult<SalesManDTO,Long> getStaffByCondition(StaffSearchCondition condition) throws PageException {
    PagingDetailResult<SalesManDTO,Long> result = new PagingDetailResult<SalesManDTO,Long>();
    Map<String,Long> map = new HashMap<String,Long>();
    UserWriter writer = userDaoManager.getWriter();
    Long totalRows = writer.countStaffByCondition(condition);
    List<SalesManDTO> salesManDTOList = writer.getStaffByCondition(condition);
    result.setResults(salesManDTOList);
    result.setPager(new Pager(Integer.valueOf(totalRows.toString()), condition.getStartPageNo(), condition.getLimit()));
    result.setSuccess(true);
    condition.setUserStatus(Status.all);
    Long countStaffAll = countStaffByCondition(condition);
    condition.setUserStatus(Status.active);
    Long countStaffActive = countStaffByCondition(condition);
    condition.setUserStatus(Status.inActive);
    Long countStaffInactive = countStaffByCondition(condition);
    map.put("countStaffAll",countStaffAll);
    map.put("countStaffActive",countStaffActive);
    map.put("countStaffInactive",countStaffInactive);
    result.setTotals(map);
    return result;
  }


  public Long countStaffByCondition(StaffSearchCondition condition) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countStaffByCondition(condition);
  }

  @Override
  public boolean checkSalesManCode(String salesManCode, Long shopId, Long salesManId) {
    UserWriter writer = userDaoManager.getWriter();
    int num = writer.checkSalesManCode(salesManCode, shopId, salesManId);
    return num > 0;
  }

  @Override
  public boolean deleteStaff(Long salesManId, Long userId) {
    if (salesManId == null) return false;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (userId != null) {
        User user = writer.getById(User.class, userId);
        user.setStatus(Status.deleted);
        writer.update(user);
      }
      SalesMan salesMan = writer.getById(SalesMan.class, salesManId);
      salesMan.setStatus(SalesManStatus.DELETED);
      writer.update(salesMan);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return true;
  }

  @Override
  public SalesManDTO getSalesManById(Long salesManId) {
    if(salesManId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    SalesMan salesMan = writer.getById(SalesMan.class,salesManId);
    return salesMan.toDTO();
  }
}
