package com.bcgogo.user.service.permission;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ShopRoleDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.ShopRole;
import com.bcgogo.user.model.permission.ShopVersion;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 12-7-11
 * Time: 下午9:40
 */
@Component
public class ShopVersionService implements IShopVersionService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopVersionService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public List<ShopVersionDTO> getAllShopVersion() {
    UserWriter writer = userDaoManager.getWriter();
    List<ShopVersionDTO> shopVersionDTOList = new ArrayList<ShopVersionDTO>();
    List<ShopVersion> shopVersionList = writer.getAllShopVersion();
    for (ShopVersion shopVersion : shopVersionList) {
      if (shopVersion.getValue().contains("BCGOGO")) continue;
      shopVersionDTOList.add(shopVersion.toDTO());
    }
    return shopVersionDTOList;
  }

  @Override
  public List<ShopVersionDTO> getCommonShopVersion() {
    UserWriter writer = userDaoManager.getWriter();
    List<ShopVersionDTO> shopVersionDTOList = new ArrayList<ShopVersionDTO>();
    List<ShopVersion> shopVersionList = writer.getAllShopVersion();
    for (ShopVersion shopVersion : shopVersionList) {
      if (shopVersion.getValue().contains("BCGOGO") || "TXN_SHOP".equals(shopVersion.getName()) || "WASH_SHOP".equals(shopVersion.getName())) continue;
      shopVersionDTOList.add(shopVersion.toDTO());
    }
    return shopVersionDTOList;
  }

  @Override
  public Map<Long, ShopVersionDTO> getAllShopVersionMap() {
    Map<Long, ShopVersionDTO> map= new HashMap<Long, ShopVersionDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<ShopVersionDTO> shopVersionDTOList = new ArrayList<ShopVersionDTO>();
    List<ShopVersion> shopVersionList = writer.getAllShopVersion();
    for (ShopVersion shopVersion : shopVersionList) {
      if (shopVersion.getValue().contains("BCGOGO")) continue;
      map.put(shopVersion.getId(),shopVersion.toDTO());
    }
    return map;
  }

  public List<ShopVersionDTO> getShopVersions() {
    List<ShopVersionDTO> shopVersionDTOList = this.getAllShopVersion();
    ShopVersionDTO dto;
    Iterator iterator = shopVersionDTOList.iterator();
    while (iterator.hasNext()) {
      dto = (ShopVersionDTO) iterator.next();
      if (dto.getName().contains("BCGOGO")) {
        iterator.remove();
      }
    }
    return shopVersionDTOList;
  }

  @Override
  public void saveOrUpdateShopVersion(ShopVersionDTO dto) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopVersion shopVersion = null;
      if (dto.getId() != null){
        shopVersion = writer.getById(ShopVersion.class, dto.getId());
      }

      if (shopVersion == null || dto.getId() == null){
        shopVersion = new ShopVersion();
      }
      shopVersion.fromDTO(dto);
      writer.saveOrUpdate(shopVersion);
      writer.commit(status);
      dto.setId(shopVersion.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean deleteShopVersion(Long shopVersionId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.delete(ShopVersion.class, shopVersionId);
      writer.commit(status);
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean saveRolesConfigForShopVersion(List<ShopRoleDTO> shopRoleDTOList, Long versionId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteRoleByShopVersions(versionId);
      ShopRole shopRole;
      for (ShopRoleDTO shopRoleDTO : shopRoleDTOList) {
        shopRole = new ShopRole(shopRoleDTO);
        writer.save(shopRole);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return true;
  }

  @Override
  public ShopVersionDTO getShopVersionById(Long shopVersionId) {
    if (shopVersionId == null) {
      LOG.warn("shopVersionId is null.");
      return new ShopVersionDTO();
    }
    UserWriter writer = userDaoManager.getWriter();
    ShopVersion shopVersion = writer.getById(ShopVersion.class, shopVersionId);
    if (shopVersion == null) {
      LOG.warn("shopVersion [{}]get by id is null.", shopVersionId);
      return new ShopVersionDTO();
    }
    return shopVersion.toDTO();
  }


  @Override
  public List<ShopVersionDTO> getShopVersionsByShopId(Long shopId, ShopVersionDTO shopVersionDTO) {
    if (shopVersionDTO == null) {
      LOG.warn("shopVersion is null.");
      return new ArrayList<ShopVersionDTO>();
    }
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (shopVersionDTO.getName().contains("BCGOGO_SHOP")) {
        return getAllShopVersion();
    }
    String shopVersionIds=configService.getConfig("WHOLESALER_SHOP_REGISTER_SHOP_VERSIONS", ShopConstant.BC_SHOP_ID);
    if(StringUtil.isEmpty(shopVersionIds)){
      LOG.error("config WHOLESALER_SHOP_REGISTER_SHOP_VERSIONS is empty!");
      return new ArrayList<ShopVersionDTO>();
    }
    String[] shopVersionIdArray = shopVersionIds.split(",");
    return this.getShopVersionByIds(ArrayUtil.convertToLong(shopVersionIdArray));
  }

  @Override
  public List<ShopVersionDTO> getShopVersionByIds(Long... ids) {
    List<ShopVersionDTO> shopVersionDTOs = new ArrayList<ShopVersionDTO>();
    if(ArrayUtils.isEmpty(ids)){
      return shopVersionDTOs;
    }
    List<ShopVersion> shopVersions =  userDaoManager.getWriter().getShopVersionByIds(ids);
    if(CollectionUtils.isNotEmpty(shopVersions)){
      for(Long id : ids){
        for (ShopVersion shopVersion :shopVersions){
          if(id.equals(shopVersion.getId())){
            shopVersionDTOs.add(shopVersion.toDTO());
          }
        }
      }
    }
    return shopVersionDTOs;
  }

  @Override
  public List<ShopVersionDTO> getShopVersionByIds(List<ShopVersionDTO> shopVersionDTOList, Long... ids) {
    List<ShopVersionDTO> shopVersionDTOs = new ArrayList<ShopVersionDTO>();
    if(ArrayUtils.isEmpty(ids) || CollectionUtils.isEmpty(shopVersionDTOList)){
      return shopVersionDTOs;
    }
    for(Long id: ids){
      for(ShopVersionDTO shopVersionDTO : shopVersionDTOList){
        if(id.equals(shopVersionDTO.getId())){
          shopVersionDTOs.add(shopVersionDTO);
          break;
        }
      }
    }
    return shopVersionDTOs;
  }

  @Override
  public ShopVersionDTO getShopVersionByName(String shopVersionName) {
    if (StringUtils.isNotBlank(shopVersionName)) {
      UserWriter writer = userDaoManager.getWriter();

      ShopVersion shopVersion = writer.getShopVersionByName(shopVersionName);
      if (shopVersion != null) {
        return shopVersion.toDTO();
      }
    }
    return null;
  }
}
