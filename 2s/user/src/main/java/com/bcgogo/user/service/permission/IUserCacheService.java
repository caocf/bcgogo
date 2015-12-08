package com.bcgogo.user.service.permission;

import com.bcgogo.enums.user.DepartmentResponsibility;
import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.OccupationDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserSearchCondition;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.permission.DepartmentResponse;
import com.bcgogo.user.permission.UserSearchResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-13
 * Time: 上午10:58
 */
public interface IUserCacheService {

  /**
   * 获得userIds
   */
  List<Long> getUserIdsByShopId(Long shopId);

  //获得user userGroupId
  UserDTO getUser(Long shopId, Long userId);

  /**
   * 获得唯一user
   */
  UserDTO getUser(String name, Long shopId);

  Map<Long,UserDTO> getUserMap(Set<Long> userIds);

  //update or add only User
  UserDTO setUser(UserDTO userDTO);

  UserDTO createUserAndStaff(UserDTO userDTO) throws Exception;

  //创建user userGroupUser userGroupId 不能为空
  UserDTO createUser(UserDTO userDTO) throws BcgogoException;

  //update user userGroupUser
  void updateUser(UserDTO userDTO) throws BcgogoException;

  //删除user userGroupUser
  void deleteUser(Long shopId, Long userId, Long userGroupId) throws BcgogoException;

  //检车用户是否重复   存在 userNo 返回 true，否则返回false
  boolean checkUserNo(String userNo);

  //根据员工Id    检车用户是否重复
  boolean checkUserNo(String userNo, Long staffId);

  //用户搜索 for crm
  UserSearchResult getUsersByCondition(UserSearchCondition condition);

  //用户下拉建议
  UserSearchResult getUserSuggestionByName(Set<Long> departmentIds, String name, Long shopId);

  //逻辑删除用户
  void deleteUserByLogic(Long shopId, Long... userIds) throws Exception;

  //修改状态
  void updateUsersStatusByIds(Status status, Long shopId, Long... userIds) throws Exception;

  //获得部门
  Node getDepartmentsAndOccupations(Long shopId);

  //获得所有下属的id
  Set<Long> getSubordinateIdsByUserId(Long shopId, Long userId);

  //获得部门总部 --前提销售部是bcgogo平台的直属机构
  Set<Long> getHeadOfficeByUserId(Long shopId, Long userId);

  DepartmentDTO updateDepartment(DepartmentDTO departmentDTO);

  boolean updateDepartmentName(String name, Long departmentId);

  OccupationDTO updateOccupation(OccupationDTO occupationDTO);

  //删除部门之前查询是否被使用
  Boolean checkDepartmentBeforeDelete(Long departmentId, Long shopId);

  Boolean checkOccupationBeforeDelete(Long occupationId, Long shopId);

  Boolean deleteDepartment(Long departmentId, Long shopId);

  Boolean deleteOccupation(Long occupationId, Long shopId);

  UserSearchResult getUsersByDepartmentId(UserSearchCondition condition);

  void updateUserDepartmentResponsibility(Long shopId, Long userId, DepartmentResponsibility departmentResponsibility);

  boolean checkOccupation(OccupationDTO occupationDTO);

  //CRM check department name
  boolean checkDepartment(DepartmentDTO departmentDTO);

  boolean checkDepartmentNameByShopId(String name, Long shopId);

  List<DepartmentDTO> getDepartmentByName(Long shopId, String name);

  //check department has been used by user
  boolean checkDepartmentForDelete(Long departmentId, Long shopId);

  //为员工分配账号
  Map<String, Object> allocatedUserNoByStaff(Long shopId, Long salesManId, String userNo, Long userGroupId, Long userId) throws BcgogoException;

  //根据员工Id 修改账号
  boolean updateUserNoByStaffId(Long id, String userNo, Long userGroupId, UserWriter writer);

  OccupationDTO getOccupationDTO(Long id);

  UserDTO getUserById(Long userId);
}
