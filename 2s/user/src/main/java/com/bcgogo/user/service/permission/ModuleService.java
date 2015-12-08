package com.bcgogo.user.service.permission;

import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.RoleType;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.ModuleDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.Module;
import com.bcgogo.user.model.permission.Role;
import com.bcgogo.user.permission.ModuleResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:17
 */
@Component
public class ModuleService implements IModuleService {
  private static final Logger LOG = LoggerFactory.getLogger(ModuleService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public ModuleResult getModulesBySystemType(Long shopId, Long userId, SystemType systemType) {
    UserWriter writer = userDaoManager.getWriter();
    List<Module> modules = writer.getModule(systemType);
    ModuleResult moduleResult = new ModuleResult();
    moduleResult.setTotalRows(modules.size());
    if (modules.size() == 0) return moduleResult;
    for (Module module : modules) {
      moduleResult.getResults().add(module.toDTO());
    }
    return moduleResult;
  }

  private Node buildModuleTree(List<Module> moduleList, Map<Long, List<Node>> roleMap) {
    Node root = new Node();
    if (CollectionUtils.isEmpty(moduleList)) return root;
    List<Node> nodes;
    Node node;
    List<Node> nodeList = new ArrayList<Node>();
    for (Module module : moduleList) {
      nodes = roleMap.get(module.getId());
      node = module.toNode();
      if (module.getParentId() == null) root = node;
      if (CollectionUtils.isNotEmpty(nodes))
        node.setChildren(nodes);
      nodeList.add(node);
    }
    root.mergeAndBuildTree(root, nodeList);
    return root;
  }

  private Node buildCheckModuleTree(List<Module> moduleList, Map<Long, List<Node>> roleMap) {
    Node root = new Node();
    if (CollectionUtils.isEmpty(moduleList)) return root;
    List<Node> nodes;
    Node node;
    List<Node> nodeList = new ArrayList<Node>();
    for (Module module : moduleList) {
      nodes = roleMap.get(module.getId());
      node = module.toCheckNode();
      if (module.getParentId() == null) root = node;
      if (CollectionUtils.isNotEmpty(nodes))
        node.setChildren(nodes);
      nodeList.add(node);
    }
    root.mergeAndBuildTree(root, nodeList);
    return root;
  }

  @Override
  public Node getTreeModuleRolesForUserConfig(Long shopId, Long shopVersionId, Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Module> moduleList = writer.getAllModule(SystemType.SHOP);
    List<Role> roleList = writer.getRolesByShopVersionId(shopVersionId);
    String roleIds = "";
    if (userGroupId != null) {
      roleIds = writer.getRoleIdsByUserGroupId(userGroupId);
    }
    Map<Long, List<Node>> roleMap = new HashMap<Long, List<Node>>();
    List<Node> nodes;
    Node node;
    for (Role role : roleList) {
      if (RoleType.isBaseRole(role.getName())) continue;
      if (userGroupId != null) {
        role.setHasThisRole(roleIds.contains(role.getId().toString()));
      }
      if (roleMap.get(role.getModuleId()) == null) {
        nodes = new ArrayList<Node>();
        roleMap.put(role.getModuleId(), nodes);
      }
      node = role.toNode();
      roleMap.get(role.getModuleId()).add(node);
    }
    Node root = buildModuleTree(moduleList, roleMap);
    root.reBuildTreeForRemoveEmptyModule();
    root.reBuildTreeForChecked();
    root.reBuildTreeForSort();
    return root;
  }

  @Override
  public List<RoleDTO> getRolesByModuleId(Long moduleId, SystemType systemType) {
    UserWriter writer = userDaoManager.getWriter();
    List<Module> moduleList = writer.getAllModule(systemType);
    List<Role> roleList = writer.getRoles(systemType);
    Map<Long, List<Node>> roleMap = new HashMap<Long, List<Node>>();
    List<Node> nodes;
    Node node;
    for (Role role : roleList) {
      if (RoleType.isBaseRole(role.getName())) continue;
      if (roleMap.get(role.getModuleId()) == null) {
        nodes = new ArrayList<Node>();
        roleMap.put(role.getModuleId(), nodes);
      }
      node = role.toNode();
      roleMap.get(role.getModuleId()).add(node);
    }
    Node root = buildModuleTree(moduleList, roleMap);
    List<Node> children = new ArrayList<Node>();
    root.findNodeInTree(moduleId).getAllChildren(children);
    List<RoleDTO> roleDTOList = new ArrayList<RoleDTO>();
    RoleDTO roleDTO;
    for (Node n : children) {
      roleDTO = n.toRoleDTO();
      if (Node.Type.ROLE.equals(n.getType())) roleDTOList.add(roleDTO);
    }
    return roleDTOList;
  }

  @Override
  public Node getTreeModuleRolesForBcgogoConfig(Long shopId, SystemType systemType) {
    UserWriter writer = userDaoManager.getWriter();
    List<Module> moduleList = writer.getAllModule(systemType);
    List<Role> roleList = writer.getRoles(systemType);
    Map<Long, List<Node>> roleMap = new HashMap<Long, List<Node>>();
    List<Node> nodes;
    Node node;
    for (Role role : roleList) {
      if (roleMap.get(role.getModuleId()) == null) {
        nodes = new ArrayList<Node>();
        roleMap.put(role.getModuleId(), nodes);
      }
      node = role.toNode();
      roleMap.get(role.getModuleId()).add(node);
    }
    return buildModuleTree(moduleList, roleMap);
  }

  @Override
  public Node getTreeModuleRolesForShopVersion(Long shopId, Long shopVersionId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Module> moduleList = writer.getAllModule(SystemType.SHOP);
    String roleIds = writer.getRoleIdByShopVersionId(shopVersionId);
    List<Role> roleList = writer.getRoles(SystemType.SHOP);
    Map<Long, List<Node>> roleMap = new HashMap<Long, List<Node>>();
    List<Node> nodes;
    Node node;
    for (Role role : roleList) {
      if (RoleType.isBaseRole(role.getName())) continue;
      role.setHasThisRole(roleIds.contains(role.getId().toString()));
      if (roleMap.get(role.getModuleId()) == null) {
        nodes = new ArrayList<Node>();
        roleMap.put(role.getModuleId(), nodes);
      }
      node = role.toCheckNode();
      roleMap.get(role.getModuleId()).add(node);
    }
    CheckNode root = (CheckNode) buildCheckModuleTree(moduleList, roleMap);
    root.reBuildTreeForChecked();
    return root;
  }

  @Override
  public Node getTreeModuleRolesForUserGroup(Long shopId, Long shopVersionId, Long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Module> moduleList = writer.getAllModule(SystemType.SHOP);
    String roleIds = writer.getRoleIdsByUserGroupId(userGroupId);
    List<Role> roleList = writer.getRolesByShopVersionId(shopVersionId);
    Map<Long, List<Node>> roleMap = new HashMap<Long, List<Node>>();
    List<Node> nodes;
    Node node;
    for (Role role : roleList) {
      //base role 跳过
      if (RoleType.isBaseRole(role.getName())) continue;
      role.setHasThisRole(roleIds.contains(role.getId().toString()));
      if (roleMap.get(role.getModuleId()) == null) {
        nodes = new ArrayList<Node>();
        roleMap.put(role.getModuleId(), nodes);
      }
      node = role.toCheckNode();
      roleMap.get(role.getModuleId()).add(node);
    }
    CheckNode root = (CheckNode) buildCheckModuleTree(moduleList, roleMap);
    root.reBuildTreeForRemoveEmptyModule();
    root.reBuildTreeForChecked();
    return root;
  }

  @Override
  public String getChainModuleNamesByRoleId(Long roleId) {
    List<String> moduleValueList = new ArrayList<String>();
    UserWriter writer = userDaoManager.getWriter();
    Role role = writer.getById(Role.class, roleId);
    getChainModule(moduleValueList,role.getModuleId(),writer,0);
    String moduleValues = "";
    for (int max = moduleValueList.size(), i = max; i > 0; i--) {
      moduleValues += moduleValueList.get(i - 1);
      if (i != 1) {
        moduleValues += ",";
      }
    }
    return moduleValues;
  }

  private void getChainModule(List<String> moduleValueList, Long moduleId, UserWriter writer, int timeout) {
    if (moduleId == null) return;
    Module module = writer.getById(Module.class, moduleId);
    if (module == null) return;
    timeout++;
    if (timeout > 10) {
      LOG.error("getChainModule loop timeout,moduleId:{}", moduleId);
      return;
    }
    moduleValueList.add(module.getValue());
    getChainModule(moduleValueList, module.getParentId(), writer, timeout);
  }

  @Override
  public boolean checkModule(ModuleDTO moduleDTO) {
    return userDaoManager.getWriter().checkModule(moduleDTO);
  }

  @Override
  public ModuleDTO updateModule(ModuleDTO moduleDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Module module;
    try {
      if (moduleDTO.getId() != null) {
        module = writer.getById(Module.class, moduleDTO.getId());
      } else {
        module = new Module();
      }
      module.fromDTO(moduleDTO);
      writer.save(module);
      moduleDTO.setId(module.getId());
      writer.commit(status);
      return moduleDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateModule(Long id, Long parentId, SystemType type) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Module module;
    try {
      module = writer.getById(Module.class, id);
      if (module == null) {
        return;
      } else {
        module.setType(type);
        module.setParentId(parentId);
      }
      writer.update(module);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean deleteModule(Long moduleId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.delete(Module.class, moduleId);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }
}
