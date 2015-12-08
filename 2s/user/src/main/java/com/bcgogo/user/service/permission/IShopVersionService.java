package com.bcgogo.user.service.permission;

import com.bcgogo.user.dto.permission.ShopRoleDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-11
 * Time: 下午9:40
 */
public interface IShopVersionService {

  List<ShopVersionDTO> getAllShopVersion();

  List<ShopVersionDTO> getCommonShopVersion();

  Map<Long,ShopVersionDTO> getAllShopVersionMap();

  void saveOrUpdateShopVersion(ShopVersionDTO dto);

  boolean deleteShopVersion(Long shopVersionId, Long shopId);

  //先删除根据versionId 删除 然后在添加
  boolean saveRolesConfigForShopVersion(List<ShopRoleDTO> shopRoleDTOList, Long versionId);

  //通过ShopVersionId 获得 ShopVersionDTO
  ShopVersionDTO getShopVersionById(Long shopVersionId);

  //通过shopID 获得 该店铺具有分配权限的shopVersions
  List<ShopVersionDTO> getShopVersionsByShopId(Long shopId, ShopVersionDTO shopVersionDTO);

  List<ShopVersionDTO> getShopVersionByIds(Long...ids);

  //从shopVersionList中挑出要的shopVersionList,顺序与id顺序一致
  List<ShopVersionDTO> getShopVersionByIds(List<ShopVersionDTO> shopVersionDTOList, Long... ids);

  //根据shopVersion的name获取相应的shopVersion
  ShopVersionDTO getShopVersionByName(String shopVersionName);
}
