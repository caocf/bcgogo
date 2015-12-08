package com.bcgogo.user.service.permission;

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.user.Status;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;
import com.bcgogo.user.dto.permission.UserGroupSearchCondition;
import com.bcgogo.user.dto.permission.UserGroupUserDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.model.permission.UserGroupRole;
import com.bcgogo.user.model.permission.UserGroupShop;
import com.bcgogo.user.model.permission.UserGroupUser;
import com.bcgogo.user.permission.UserGroupSearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:16
 * 用户组读写操作服务类
 */
@Component
public class UserGroupService implements IUserGroupService {
  private static final Logger LOG = LoggerFactory.getLogger(UserGroupService.class);
  private static final String SIMPLIFY_CHINESE_OF_OCCUPATION = "ZW";
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public UserGroup getUserGroup(Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getById(UserGroup.class, userGroupId);
  }

  @Override
  public UserGroupDTO getUserGroupDTO(Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    UserGroup userGroup = writer.getById(UserGroup.class, userGroupId);
    if (userGroup != null) {
      return userGroup.toDTO();
    }
    return null;
  }

  @Override
  public UserGroupDTO setUserGroup(UserGroupDTO userGroupDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGroupDTO dto;
      if (userGroupDTO.getId() != null) {
        dto = setUserGroup(userGroupDTO, writer);
      } else {
        long userGroupId = createUserGroup(userGroupDTO, userGroupDTO.getShopId(), writer);
        userGroupDTO.setId(userGroupId);
        dto = userGroupDTO;
      }
      writer.commit(status);
      return dto;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long createUserGroup(UserGroupDTO userGroupDTO, Long shopId, UserWriter writer) {
    if (userGroupDTO == null || shopId == null || writer == null) return null;
    UserGroup userGroup = new UserGroup();
    userGroup.fromDTO(userGroupDTO);
    writer.save(userGroup);

    UserGroupShop userGroupShop = new UserGroupShop();
    userGroupShop.setShopId(shopId);
    userGroupShop.setUserGroupId(userGroup.getId());
    writer.save(userGroupShop);

    return userGroup.getId();
  }

  private UserGroupDTO setUserGroup(UserGroupDTO userGroupDTO, UserWriter writer) {
    UserGroup userGroup = null;
    if (userGroupDTO.getId() == null) {
      userGroup = new UserGroup(userGroupDTO);
    } else {
      userGroup = writer.getById(UserGroup.class, userGroupDTO.getId());
      userGroup = userGroup.fromDTO(userGroupDTO);
    }
    writer.saveOrUpdate(userGroup);
    return userGroup.toDTO();
  }

  @Override
  public UserGroupDTO getUniqueUserGroupByName(String name, Long shopId, Long shopVersionId) {
    UserWriter writer = userDaoManager.getWriter();
    List<UserGroup> userGroupList = writer.getUniqueUserGroupByName(name, shopId, shopVersionId);
    if (CollectionUtils.isNotEmpty(userGroupList)) {
      return userGroupList.get(0).toDTO();
    }
    return null;
  }

  @Override
  public List<UserGroupDTO> getUserGroupByName(String name, boolean isFuzzyMatching, Long shopId, Long shopVersionId) {
    List<UserGroupDTO> userGroupDTOList = new ArrayList<UserGroupDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<UserGroup> userGroupList = writer.getUserGroupByName(name, isFuzzyMatching, shopId, shopVersionId);
    for (UserGroup userGroup : userGroupList) {
      userGroupDTOList.add(userGroup.toDTO());
    }
    return userGroupDTOList;
  }

  public List<UserGroupDTO> getAllUserGroup() {
    List<UserGroupDTO> userGroupDTOList;
    UserWriter writer = userDaoManager.getWriter();
    userGroupDTOList = writer.getAllUserGroup();
    return userGroupDTOList;
  }

  public List<Long> getAllUserGroupIds() {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getAllUserGroupIds();
  }

  @Override
  public UserGroupUserDTO setUserGroupUser(UserGroupUserDTO userGroupUserDTO, UserWriter writer) {
    if (userGroupUserDTO == null || userGroupUserDTO.getUserId() == null || userGroupUserDTO.getUserGroupId() == null) {
      LOG.warn("userGroupUser is null.");
      return null;
    }
    UserGroupUser userGroupUser = null;
    if (userGroupUserDTO.getId() != null) {
      userGroupUser = writer.getById(UserGroupUser.class, userGroupUserDTO.getId());
      if (userGroupUser != null) {
        userGroupUser.fromDTO(userGroupUserDTO);
      } else {
        userGroupUser = new UserGroupUser(userGroupUserDTO);
      }
    } else {
      userGroupUser = new UserGroupUser(userGroupUserDTO);
    }
    //保存数据库
    writer.saveOrUpdate(userGroupUser);
    String key = getUserGroupUserKey(userGroupUserDTO.getUserId(), userGroupUserDTO.getUserGroupId());
    if (StringUtils.isBlank(key)) return null;
    //保存memcache
    userGroupUserDTO = userGroupUser.toDTO();
    MemCacheAdapter.set(key, userGroupUserDTO);
    return userGroupUserDTO;
  }


  @Override
  public UserGroupUserDTO setUserGroupUser(UserGroupUserDTO userGroupUserDTO) {
    if (userGroupUserDTO == null || userGroupUserDTO.getUserId() == null || userGroupUserDTO.getUserGroupId() == null)
      return null;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      userGroupUserDTO = this.setUserGroupUser(userGroupUserDTO, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return userGroupUserDTO;
  }

  @Override
  public UserGroupSearchResult getUserGroupByCondition(UserGroupSearchCondition condition) {
    UserGroupSearchResult result = new UserGroupSearchResult();
    UserWriter writer = userDaoManager.getWriter();
    List<UserGroupDTO> userGroupDTOList = new ArrayList<UserGroupDTO>();
    long count = writer.countUserGroupByCondition(condition);
    List<UserGroup> userGroups = writer.getUserGroupByCondition(condition);
    for (UserGroup userGroup : userGroups) {
      userGroupDTOList.add(userGroup.toDTO());
    }
    result.setResults(userGroupDTOList);
    result.setTotalRows(count);
    return result;
  }

  @Override
  public Boolean updateUserGroupsByIds(Status userGroupStatus, Long... ids) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<UserGroup> userGroups = writer.getUserGroupByIds(ids);
      for (UserGroup u : userGroups) {
        u.setStatus(userGroupStatus);
        writer.save(u);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return true;
  }


  @Override
  public boolean deleteUserGroupUser(Long userId, Long userGroupId, UserWriter writer) throws BcgogoException {
    String key = getUserGroupUserKey(userId, userGroupId);
    if (StringUtils.isBlank(key)) return false;
    boolean isSuccess = MemCacheAdapter.delete(key);
    if (isSuccess) {
      LOG.warn("delete UserGroupUser [userId:{}, userGroupId {}] from memcache.", userId, userGroupId);
    }
    writer.deleteUserGroupUser(userId, userGroupId);
    LOG.warn("delete UserGroupUser [userId:{}, userGroupId {}] from DB.", userId, userGroupId);
    return true;
  }

  @Override
  public boolean checkBeforeDeleteUserGroup(Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countUserByUserGroupId(userGroupId) > 0l || writer.countSaleMansByUserGroupId(userGroupId) > 0l;
  }

  private String getUserGroupUserKey(Long userId, Long userGroupId) {
    if (userId == null && userGroupId == null) return "";
    return MemcachePrefix.userGroupUser.getValue() + String.valueOf(userId) + "_" + String.valueOf(userGroupId);
  }

  @Override
  public List<UserGroupDTO> getUserGroupsByShopVersionId(Long shopVersionId) {
    List<UserGroupDTO> userGroupDTOList = new ArrayList<UserGroupDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<UserGroup> userGroupList = writer.getUserGroupsByShopVersionId(shopVersionId);
    for (UserGroup userGroup : userGroupList) {
      userGroupDTOList.add(userGroup.toDTO());
    }
    return userGroupDTOList;
  }

  @Override
  public void saveUserGroupForShopVersion(UserGroupDTO dto, Long shopVersionId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //save userGroup
      UserGroupDTO userGroupDTO = setUserGroup(dto, writer);
      //save UserGroupShop
      UserGroupShop userGroupShop = new UserGroupShop();
      userGroupShop.setShopVersionId(shopVersionId);
      userGroupShop.setUserGroupId(userGroupDTO.getId());
      writer.save(userGroupShop);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteUserGroup(Long userGroupId, Long shopVersionId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.delete(UserGroup.class, userGroupId);
      writer.deleteUserGroupShop(userGroupId, shopVersionId);
      writer.deleteUserGroupRole(userGroupId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean saveRolesConfigForUserGroup(List<UserGroupRoleDTO> userGroupRoleDTOList, Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGroupRole userGroupRole;
      writer.deleteUserGroupRole(userGroupId);
      for (UserGroupRoleDTO userGroupRoleDTO : userGroupRoleDTOList) {
        userGroupRole = new UserGroupRole(userGroupRoleDTO);
        writer.save(userGroupRole);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return true;
  }

  @Override
  public UserGroupDTO getUserGroupByUserId(Long userId) {
    UserWriter writer = userDaoManager.getWriter();
    UserGroup userGroup = writer.getUserGroupByUserId(userId);
    if (userGroup == null) return new UserGroupDTO();
    return userGroup.toDTO();
  }

  @Override
  public boolean checkUserGroupName(String name, Long shopVersionId, Long shopId, Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.checkUserGroupName(name, shopVersionId, shopId, userGroupId);
  }

  @Override
  public UserGroupSearchResult getUserGroupsByCondition(Long shopId, Long shopVersionId, UserGroupSearchCondition condition) {
    UserGroupSearchResult result = new UserGroupSearchResult();
    List<UserGroupDTO> userGroupDTOList = new ArrayList<UserGroupDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<UserGroup> userGroupList = new ArrayList<UserGroup>();
    if("SYSTEM_DEFAULT".equals(condition.getVariety())) {
      result.setCountSystemDefault(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
      result.setCountCustom(0L);
      result.setTotalRows(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
      userGroupList = writer.getUserGroupsByCondition(shopVersionId, shopId, condition);
    } else if("CUSTOM".equals(condition.getVariety())) {
      result.setCountCustom(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
      result.setCountSystemDefault(0L);
      result.setTotalRows(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
      userGroupList = writer.getUserGroupsByCondition(shopVersionId, shopId, condition);
    } else {
      condition.setVariety("SYSTEM_DEFAULT");
      result.setCountSystemDefault(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
      condition.setVariety("CUSTOM");
      result.setCountCustom(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));

      if("custom".equals(condition.getVariety2())) {
        condition.setVariety("CUSTOM");
        result.setTotalRows(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
        userGroupList = writer.getUserGroupsByCondition(shopVersionId, shopId, condition);
      } else if("countSystemDefault".equals(condition.getVariety2())) {
        condition.setVariety("SYSTEM_DEFAULT");
        result.setTotalRows(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
        userGroupList = writer.getUserGroupsByCondition(shopVersionId, shopId, condition);
      } else {
        condition.setVariety("");
        result.setTotalRows(writer.countUserGroupsByCondition(shopVersionId, shopId, condition));
        userGroupList = writer.getUserGroupsByCondition(shopVersionId, shopId, condition);
      }
    }

    for (UserGroup userGroup : userGroupList) {
      userGroupDTOList.add(userGroup.toDTO());
    }
    result.setResults(userGroupDTOList);
    return result;
  }

  @Override
  public String initUserGroupNo(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Long num = writer.countUserGroupByShopId(shopId);
    return SIMPLIFY_CHINESE_OF_OCCUPATION + new DecimalFormat("000").format(num);
  }

  @Override
  public void deleteUserGroupLogical(Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //delete memCache
      ServiceManager.getService(IPrivilegeService.class).deleteResourcesFromCache(userGroupId);
      UserGroup userGroup = writer.getById(UserGroup.class, userGroupId);
      userGroup.setStatus(Status.deleted);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<UserGroup> getUserGroupByIds(Long... ids){
    UserWriter writer = userDaoManager.getWriter();
    List<UserGroup> userGroupList = writer.getUserGroupByIds(ids);
    if (CollectionUtils.isNotEmpty(userGroupList)) {
      return userGroupList;
    }
    return null;
  }
}
