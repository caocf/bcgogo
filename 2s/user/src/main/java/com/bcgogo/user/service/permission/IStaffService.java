package com.bcgogo.user.service.permission;

import com.bcgogo.common.PagingDetailResult;
import com.bcgogo.exception.PageException;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.permission.StaffSearchCondition;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-18
 * Time: 下午11:00
 * 员工管理
 */
public interface IStaffService {

  //员工搜索
  PagingDetailResult<SalesManDTO,Long> getStaffByCondition(StaffSearchCondition condition) throws PageException;

  //搜索总数
  Long countStaffByCondition(StaffSearchCondition condition);

  //检查员工号是否有重复
  boolean checkSalesManCode(String salesManCode, Long shopId,Long salesManId);

  //删除salesMan and user
  boolean deleteStaff(Long salesManId, Long userId);

  //根据ID获得一个员工
  SalesManDTO getSalesManById(Long salesManId);
}
