package com.bcgogo.config.service;

import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.config.model.ServiceCategory;
import com.bcgogo.user.dto.Node;
import com.bcgogo.enums.app.ServiceScope;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
public interface IServiceCategoryService {
  //完全包含
//  ServiceCategoryDTO findParentServiceCategoryByChildrenCompletely(Set<Long> children);

  List<Long> getServiceCategoryChildrenIds(Set<Long> parentId);

  List<Long> getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope scope);

  List<ServiceCategoryDTO> getServiceCategoryDTO(Long shopId);

  List<ServiceCategory> getServiceCategory(Long shopId);

  List<ShopServiceCategoryDTO> getShopServiceCategoryDTOByShopId(Long shopId);

  /**
   * 根据shopId 拿到本店 服务内容key是ServiceCategoryId，value是ServiceCategoryName
   * @param shopId
   * @return
   */
  Map<Long,String> getShopServiceCategoryIdNameMap(Long shopId);

  Map<Long,List<Long>> getShopServiceCategoryMap(Long... shopIds);


  void saveOrUpdateShopServiceCategory(ShopServiceCategoryDTO shopServiceCategoryDTO);

  Boolean saveShopServiceCategory(Long shopId,Long ... serviceCategoryIds);

  List<Node> getCheckedServiceCategory(Long shopId);

  void updateShopServiceCategory(Long shopId, String shopServiceCategoryIds);

  boolean isWashServiceScope(Long serviceScopeId);
}
