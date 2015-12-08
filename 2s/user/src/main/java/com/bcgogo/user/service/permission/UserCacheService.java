package com.bcgogo.user.service.permission;

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.user.DepartmentResponsibility;
import com.bcgogo.enums.user.Status;
import com.bcgogo.enums.user.UserType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.UserGroupUserDTO;
import com.bcgogo.user.dto.permission.UserSearchCondition;
import com.bcgogo.user.model.SalesMan;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.Department;
import com.bcgogo.user.model.permission.Occupation;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.model.permission.UserGroupUser;
import com.bcgogo.user.permission.UserSearchResult;
import com.bcgogo.user.service.IUserGuideService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RegexUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:20
 */
@Component
public class UserCacheService implements IUserCacheService {
  private static final Logger LOG = LoggerFactory.getLogger(UserCacheService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public List<Long> getUserIdsByShopId(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getUserIdsByShopId(shopId);
  }
  @Override
  public UserDTO getUserById(Long userId) {
    UserWriter writer = userDaoManager.getWriter();
    User user = writer.getById(User.class,userId);
    if(user!=null)
      return user.toDTO();
    return null;
  }
  @Override
  public UserDTO getUser(Long shopId, Long userId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getUserDTO(shopId, userId);
  }

  @Override
  public UserDTO getUser(String name, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getUser(shopId, name);
  }

  @Override
  public Map<Long, UserDTO> getUserMap(Set<Long> userIds) {
    Map<Long, UserDTO> userDTOMap = new HashMap<Long, UserDTO>();
    if (CollectionUtils.isEmpty(userIds)) return userDTOMap;
    UserWriter writer = userDaoManager.getWriter();
    List<User> userList = writer.getUsersByIds(userIds);
    for(User user:userList){
      userDTOMap.put(user.getId(),user.toDTO());
    }
    return userDTOMap;
  }


  public UserDTO setUser(UserDTO userDTO) {
    if (userDTO == null || userDTO.getShopId() == null || userDTO.getUserGroupId() == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      userDTO = this.setUser(userDTO, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return userDTO;
  }

  private UserDTO setUser(UserDTO userDTO, UserWriter writer) {
    User user = null;
      if (userDTO.getId() != null) {
        user = writer.getById(User.class, userDTO.getId());
        if (user != null) {
          user.fromDTO(userDTO);
        } else {
          user = new User(userDTO);
        }
      } else {
        user = new User(userDTO);
      }
      writer.saveOrUpdate(user);
    userDTO.setId(user.getId());
    return userDTO;
  }

  public UserDTO createUserAndStaff(UserDTO userDTO) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //创建user userGroup
      userDTO = createUser(userDTO, writer);
      SalesManDTO salesManDTO = userDTO.toSalesManDTO();
      salesManDTO.setDepartmentName(SalesManDTO.defaultDepartment);
      salesManDTO.setStatus(SalesManStatus.INSERVICE);
      salesManDTO = ServiceManager.getService(IUserService.class).saveOrUpdateSalesMan(salesManDTO, writer);
      userDTO.setSalesManId(salesManDTO.getId());
      userDTO.setHasUserGuide(YesNo.YES);
      userDTO.setFinishUserGuide(YesNo.NO);
      userDTO = this.setUser(userDTO);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    //建立用户引导
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    //初始化新手指引
      ServiceManager.getService(IUserGuideService.class).addUserGuideToNewUser(userDTO.getId());
    return userDTO;
  }

  private UserDTO createUser(UserDTO userDTO, UserWriter writer) throws BcgogoException {
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    if (userDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    if (userDTO.getPassword() == null)
      throw new BcgogoException(BcgogoExceptionType.EmptyPasswordNotAllowed);
    String mobile = userDTO.getMobile();
    if (StringUtils.isNotBlank(mobile)) {
      if (!RegexUtils.isMobile(mobile)) {
        LOG.warn("Invalid mobile number..");
    }
    }
    String email = userDTO.getEmail();
    if (StringUtils.isNotBlank(email)) {
      if (!RegexUtils.isEmail(email)) {
        LOG.warn("Invalid email address.");
    }
    }
    if (StringUtils.isNotBlank(mobile)) userDTO.setMobile(mobile.trim());
    if (StringUtils.isNotBlank(email)) userDTO.setEmail(email.trim());
    //if (userDTO.getUserGroupId() == null) throw new BcgogoException("user group id is empty!");
    //保存user
      userDTO = this.setUser(userDTO, writer);
    //保存userGroupUser
    UserGroupUserDTO userGroupUserDTO = new UserGroupUserDTO();
    if(userDTO.getUserGroupId() != null) {
      userGroupUserDTO.setUserGroupId(userDTO.getUserGroupId());
    }
    userGroupUserDTO.setUserId(userDTO.getId());
      userGroupService.setUserGroupUser(userGroupUserDTO, writer);
    return userDTO;
  }

  @Override
  public UserDTO createUser(UserDTO userDTO) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      userDTO = createUser(userDTO, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return userDTO;
  }

  @Override
  public void updateUser(UserDTO userDTO) throws BcgogoException {
    if (userDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    if (userDTO.getUserGroupId() == null) throw new BcgogoException("userGroupId can not be null.");
    if (userDTO.getPassword() == null)
      throw new BcgogoException(BcgogoExceptionType.EmptyPasswordNotAllowed);
    UserDTO oldUserDTO = this.getUser(userDTO.getShopId(), userDTO.getId());
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //判断 用户组是否改变
      if (!NumberUtil.isEqual(oldUserDTO.getUserGroupId(), userDTO.getUserGroupId())) {
        IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
        //UserGroupUser
        userGroupService.deleteUserGroupUser(oldUserDTO.getId(), oldUserDTO.getUserGroupId(), writer);
        LOG.warn("shopId is {} delete user[userId:{}] from db.", oldUserDTO.getShopId(), oldUserDTO.getId());
        UserGroupUserDTO userGroupUserDTO = new UserGroupUserDTO();
        userGroupUserDTO.setUserGroupId(userDTO.getUserGroupId());
        userGroupUserDTO.setUserId(userDTO.getId());
        userGroupService.setUserGroupUser(userGroupUserDTO, writer);
      }
      this.setUser(userDTO, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteUser(Long shopId, Long userId, Long userGroupId) throws BcgogoException {
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    String key = getUserKey(shopId, userId);
    if (StringUtils.isBlank(key)) return;
    //删除memcache user
    boolean isSuccess = MemCacheAdapter.delete(key);
    if (isSuccess) {
      LOG.warn("shopId is {}, delete user [userId:{}] from memcache.", shopId, userId);
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
    //UserGroupUser
     userGroupService.deleteUserGroupUser(userId, userGroupId, writer);
    //删除user db
    writer.delete(User.class, userId);
      LOG.warn("shopId is {} delete user[userId:{}] from db.", shopId, userId);
    writer.commit(status);
    } finally {
      writer.rollback(status);
  }
  }

  @Override
  public boolean checkUserNo(String userNo) {
    UserWriter writer = userDaoManager.getWriter();
    int num = writer.countUserByUserNo(userNo);
    return num > 0;
  }

  public boolean checkUserNo(String userNo, Long staffId) {
    UserWriter writer = userDaoManager.getWriter();
    UserDTO userDTO = writer.getUserByStaffId(staffId);
    if (userDTO == null) return false;
    Long num = writer.countUserByUserNo(userNo, userDTO.getId());
    return num > 0;
  }

  @Override
  public UserSearchResult getUsersByCondition(UserSearchCondition condition) {
    UserSearchResult result = new UserSearchResult();
    UserWriter writer = userDaoManager.getWriter();
    long count = writer.countUserByCondition(condition);
    List<UserDTO> userDTOList = writer.getUserByCondition(condition);
    result.setResults(userDTOList);
    result.setTotalRows(count);
    return result;
  }

  @Override
  public UserSearchResult getUserSuggestionByName(Set<Long> departmentIds, String name, Long shopId) {
    UserSearchResult result = new UserSearchResult();
    UserWriter writer = userDaoManager.getWriter();
    List<User> userList = writer.getUserSuggestionByName(departmentIds, name, shopId);
    List<UserDTO> userDTOList = new ArrayList<UserDTO>();
    for (User u : userList) {
      userDTOList.add(u.toDTO());
    }
    result.setResults(userDTOList);
    return result;
  }

  @Override
  public void updateUsersStatusByIds(Status s, Long shopId, Long... userIds) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    UserDTO userDTO;
    try {
      for (Long userId : userIds) {
        userDTO = writer.getUserDTO(shopId, userId);
        userDTO.setStatusEnum(s);
        this.setUser(userDTO, writer);
      }
      writer.commit(status);
    } catch (Exception e) {
      throw e;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteUserByLogic(Long shopId, Long... userIds) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    User user;
    try {
      for (Long userId : userIds) {
        user = writer.getById(User.class, userId);
        user.setStatus(Status.deleted);
        writer.update(user);
      }
      writer.commit(status);
      for (Long userId : userIds) {
        //删除memCache
        String key = getUserKey(shopId, userId);
        if (StringUtils.isBlank(key)) return;
        //删除memcache user
        boolean isSuccess = MemCacheAdapter.delete(key);
        if (!isSuccess) {
          LOG.warn("shopId is {}, delete user [userId:{}] from memcache failed.", shopId, userId);
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public OccupationDTO getOccupationDTO(Long id){
    UserWriter writer = userDaoManager.getWriter();
    Occupation occupation = writer.getById(Occupation.class,id);
    if(occupation!=null){
      return occupation.toDTO();
    }
    return null;
  }
  @Override
  public Node getDepartmentsAndOccupations(Long shopId) {
    Node root = new Node();
    UserWriter writer = userDaoManager.getWriter();
    List<Department> departmentList = writer.getDepartmentsByShopId(shopId);
    List<Occupation> occupationList = writer.getOccupationsByShopId(shopId);
    Map<Long, List<Node>> occupationMap = new HashMap<Long, List<Node>>();
    List<Node> nodes;
    Node node;
    for (Occupation occupation : occupationList) {
      if (occupationMap.get(occupation.getDepartmentId()) == null) {
        nodes = new ArrayList<Node>();
        occupationMap.put(occupation.getDepartmentId(), nodes);
      }
      node = occupation.toNode();
      occupationMap.get(occupation.getDepartmentId()).add(node);
    }
    if (CollectionUtils.isEmpty(departmentList)) return root;
    List<Node> nodeList = new ArrayList<Node>();
    for (Department department : departmentList) {
      nodes = occupationMap.get(department.getId());
      if (department.getParentId() == null) root = department.toNode();
      node = department.toNode();
      if (CollectionUtils.isNotEmpty(nodes))
        node.setChildren(nodes);
      nodeList.add(node);
    }
    root.mergeAndBuildTree(root, nodeList);
    return root;
  }

  //获得子部门id
  private Set<Long> getSubDepartmentIds(Long departmentId, Long shopId, Node root) {
    List<Node> children = new ArrayList<Node>();
    root.findNodeInTree(departmentId).getAllChildren(children);
    Set<Long> departmentIds = new HashSet<Long>();
    departmentIds.add(departmentId);
    for (Node n : children) {
      if (n.getType() == Node.Type.DEPARTMENT) {
        if (n.getId() != null)
          departmentIds.add(n.getId());
      }
    }
    return departmentIds;
  }

  @Override
  public Set<Long> getSubordinateIdsByUserId(Long shopId, Long userId) {
    UserDTO dto = this.getUser(shopId, userId);
    Set<Long> ids = new HashSet<Long>();
    ids.add(userId);
    if (dto.getDepartmentResponsibility() == null || dto.getDepartmentResponsibility() == DepartmentResponsibility.MEMBER)
      return ids;
    Node root = this.getDepartmentsAndOccupations(shopId);
    Set<Long> departmentIds = this.getSubDepartmentIds(dto.getDepartmentId(), shopId, root);
    if (CollectionUtils.isEmpty(departmentIds)) return ids;
    UserWriter writer = userDaoManager.getWriter();
    List<User> userList = writer.getUsersByDepartmentIds(departmentIds);
    for (User user : userList) {
      ids.add(user.getId());
    }
    return ids;
  }

  @Override
  public Set<Long> getHeadOfficeByUserId(Long shopId, Long userId) {
    UserDTO dto = this.getUser(shopId, userId);
    Node root = this.getDepartmentsAndOccupations(shopId);
    Set<Long> ids;
    for (Node node : root.getChildren()) {
      if (node.findNodeInTree(dto.getDepartmentId()) != null) {
        ids = this.getSubDepartmentIds(dto.getDepartmentId(), shopId, root);
        return ids;
      }
    }
    ids = new HashSet<Long>();
    ids.add(dto.getDepartmentId());
    return ids;
  }

  @Override
  public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Department department;
    try {
      Long sort = writer.maxParentDepartmentChildren(departmentDTO.getParentId(), departmentDTO.getShopId()) + 1;
      if (departmentDTO.getId() != null) {
        department = writer.getById(Department.class, departmentDTO.getId());
        departmentDTO.setSort(department.getSort());
        department.fromDTO(departmentDTO);
      } else {
        if (sort.toString().length() < 6) {
          departmentDTO.setSort(sort);
        } else {
          departmentDTO.setSort(Long.valueOf(StringUtils.substring(sort.toString(), sort.toString().length() - 5, sort.toString().length() - 1)));
        }
        department = new Department(departmentDTO);
      }
      writer.save(department);
      departmentDTO.setId(department.getId());
      writer.commit(status);
      return departmentDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean updateDepartmentName(String name, Long departmentId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Department department;
    try {
      department = writer.getById(Department.class, departmentId);
      department.setName(name);
      writer.save(department);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public OccupationDTO updateOccupation(OccupationDTO occupationDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Occupation occupation;
    try {
      Long sort = writer.maxParentOccupationChildren(occupationDTO.getDepartmentId(), occupationDTO.getShopId()) + 1;
      if (occupationDTO.getId() != null) {
        occupation = writer.getById(Occupation.class, occupationDTO.getId());
        occupationDTO.setSort(occupation.getSort());
        occupation.fromDTO(occupationDTO);
      } else {
        if (sort.toString().length() < 6) {
          occupationDTO.setSort(sort);
        } else {
          occupationDTO.setSort(Long.valueOf(StringUtils.substring(sort.toString(), sort.toString().length() - 5, sort.toString().length() - 1)));
        }
        occupation = new Occupation(occupationDTO);
      }
      writer.save(occupation);
      occupationDTO.setId(occupation.getId());
      writer.commit(status);
      return occupationDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Boolean checkDepartmentBeforeDelete(Long departmentId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.checkDepartmentBeforeDelete(departmentId, shopId);
  }

  @Override
  public Boolean checkOccupationBeforeDelete(Long occupationId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.checkOccupationBeforeDelete(occupationId, shopId);
  }

  @Override
  public Boolean deleteDepartment(Long departmentId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.delete(Department.class, departmentId);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Boolean deleteOccupation(Long occupationId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.delete(Occupation.class, occupationId);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public UserSearchResult getUsersByDepartmentId(UserSearchCondition condition) {
    UserSearchResult result = new UserSearchResult();
    UserWriter writer = userDaoManager.getWriter();
    List<UserDTO> userDTOList = writer.getUserByCondition(condition);
    result.setResults(userDTOList);
    result.setTotalRows(userDTOList.size());
    return result;
  }

  @Override
  public void updateUserDepartmentResponsibility(Long shopId, Long userId, DepartmentResponsibility departmentResponsibility) {
    UserDTO userDTO = this.getUser(shopId, userId);
    userDTO.setDepartmentResponsibility(departmentResponsibility);
    this.setUser(userDTO);
  }

  private String getUserKey(Long shopId, Long userId) {
    if (shopId == null && userId == null) return "";
    return MemcachePrefix.user.getValue() + String.valueOf(shopId) + "_" + String.valueOf(userId);
  }

  @Override
  public boolean checkOccupation(OccupationDTO occupationDTO) {
    return userDaoManager.getWriter().checkOccupation(occupationDTO);
  }

  @Override
  public boolean checkDepartment(DepartmentDTO departmentDTO) {
    return userDaoManager.getWriter().checkDepartment(departmentDTO);
  }

  @Override
  public boolean checkDepartmentNameByShopId(String name, Long shopId) {
    return userDaoManager.getWriter().checkDepartmentNameByShopId(name, shopId);
  }

  @Override
  public List<DepartmentDTO> getDepartmentByName(Long shopId, String name) {
    List<Department> departmentList = userDaoManager.getWriter().getDepartmentByName(shopId, name);
    List<DepartmentDTO> departmentDTOList = new ArrayList<DepartmentDTO>();
    for (Department d : departmentList) {
      departmentDTOList.add(d.toDTO());
    }
    return departmentDTOList;
  }

  @Override
  public boolean checkDepartmentForDelete(Long departmentId, Long shopId) {
    return userDaoManager.getWriter().checkDepartmentForDelete(departmentId, shopId);
  }

  @Override
  public Map<String, Object> allocatedUserNoByStaff(Long shopId, Long salesManId, String userNo, Long userGroupId, Long userId) throws BcgogoException {
    Map<String, Object> result = new HashMap<String, Object>();
    UserWriter writer = userDaoManager.getWriter();
    SalesMan salesMan = writer.getById(SalesMan.class, salesManId);
    Object status = writer.begin();
    try {
      if (userGroupId != null) {
        salesMan.setUserGroupId(userGroupId);
        writer.update(salesMan);
      }
      UserDTO userDTO = salesMan.toUserDTO();
      userDTO.setUserNo(userNo);
      userDTO.setUserGroupId(userGroupId);
      String passwordWithEncrypt = EncryptionUtil.encryptPassword("123456", shopId);
      userDTO.setPassword(passwordWithEncrypt);
      userDTO.setUserType(UserType.NORMAL);
      if (userId != null) {
        userDTO.setId(userId);
      }
      this.createUser(userDTO,writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    result.put("success", true);
    return result;
  }

  @Override
  public boolean updateUserNoByStaffId(Long id, String userNo, Long userGroupId, UserWriter writer) {
    UserDTO userDTO = writer.getUserByStaffId(id);
    if (userDTO == null) return false;
    Long num = writer.countUserByUserNo(userNo, userDTO.getId());
    if (num > 0) return false;
    userDTO.setUserNo(userNo);
    this.setUser(userDTO, writer);
    //rebuilt userGroupUser
    UserGroupUser userGroupUser;
    List<UserGroupUser> userGroupUserList = writer.getUserGroupUser(userDTO.getId());
    if (CollectionUtils.isNotEmpty(userGroupUserList)) {
      userGroupUser = userGroupUserList.get(0);
      userGroupUser.setUserGroupId(userGroupId);
      writer.update(userGroupUser);
    }
    return true;
  }

}
