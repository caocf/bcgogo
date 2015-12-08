package com.bcgogo.config.service;

import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ServiceCategory;
import com.bcgogo.config.model.ShopServiceCategory;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午10:11
 */
@Component
public class ServiceCategoryService implements IServiceCategoryService{
  private static final Logger LOG = LoggerFactory.getLogger(ServiceCategoryService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;


//  @Override
//  public ServiceCategoryDTO findParentServiceCategoryByChildrenCompletely(Set<Long> children) {
//    if (CollectionUtil.isEmpty(children)) return null;
//    ConfigWriter writer = configDaoManager.getWriter();
//    Iterator<Long> iterator = children.iterator();
//    ServiceCategory serviceCategory = writer.getById(ServiceCategory.class, iterator.next());
//    if (serviceCategory == null) return null;
//    ServiceCategory parent = writer.getById(ServiceCategory.class, serviceCategory.getParentId());
//    if (parent == null) return null;
//    List<Long> allChildren = writer.getServiceCategoryChildren(serviceCategory.getParentId());
//    for (Long id : allChildren) {
//      if (!children.contains(id)) {
//        return null;
//      }
//    }
//    return parent.toDTO();
//  }

  @Override
  public List<Long> getServiceCategoryChildrenIds(Set<Long> parentId) {
    return configDaoManager.getWriter().getServiceCategoryChildrenIds(parentId);
  }

  @Override
  public List<Long> getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope scope) {
    return configDaoManager.getWriter().getServiceCategoryChildrenIdsByParentServiceScope(scope);
  }

  public List<ServiceCategoryDTO> getServiceCategoryDTO(Long shopId) {
    List<ServiceCategory> serviceCategoryList=getServiceCategory(shopId);
    List<ServiceCategoryDTO> categoryDTOs=new ArrayList<ServiceCategoryDTO>();
    if(CollectionUtil.isNotEmpty(serviceCategoryList)){
      for (ServiceCategory category:serviceCategoryList){
        categoryDTOs.add(category.toDTO());
      }
    }
    return categoryDTOs;
  }

  public List<ServiceCategory> getServiceCategory(Long shopId){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getServiceCategory(shopId);
  }

  @Override
  public List<ShopServiceCategoryDTO> getShopServiceCategoryDTOByShopId(Long shopId) {
    List<ShopServiceCategoryDTO> shopServiceCategoryDTOs = new ArrayList<ShopServiceCategoryDTO>();
    if (shopId == null) {
      return shopServiceCategoryDTOs;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopServiceCategory> shopServiceCategories = writer.getShopServiceCategoriesById(shopId);
    if (CollectionUtils.isNotEmpty(shopServiceCategories)) {
      for (ShopServiceCategory shopServiceCategory : shopServiceCategories) {
        ShopServiceCategoryDTO shopServiceCategoryDTO = shopServiceCategory.toDTO();
        ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(shopServiceCategoryDTO.getServiceCategoryId());
        if(serviceCategoryDTO != null){
          shopServiceCategoryDTO.setServiceCategoryName(serviceCategoryDTO.getName());
        }else {
          LOG.error("shopServiceCategoryDTO 中的serviceCategoryId：{}，找不到对应的ServiceCategoryDTO",shopServiceCategoryDTO.getServiceCategoryId());
        }

        shopServiceCategoryDTOs.add(shopServiceCategoryDTO);
      }
    }
    return shopServiceCategoryDTOs;
  }

  @Override
  public Map<Long, String> getShopServiceCategoryIdNameMap(Long shopId) {
    Map<Long, String> shopServiceCategoryMap = new LinkedHashMap<Long, String>();
    if(shopId == null){
      return shopServiceCategoryMap;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopServiceCategory> shopServiceCategories = writer.getShopServiceCategoriesById(shopId);
    List<ShopServiceCategoryDTO> shopServiceCategoryDTOs = getShopServiceCategoryDTOByShopId(shopId);
    if(CollectionUtils.isNotEmpty(shopServiceCategoryDTOs)){
      for(ShopServiceCategory shopServiceCategory : shopServiceCategories){
        ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(shopServiceCategory.getServiceCategoryId());
        if(serviceCategoryDTO != null){
          shopServiceCategoryMap.put(shopServiceCategory.getServiceCategoryId(),serviceCategoryDTO.getName());
        }
      }
    }
    return shopServiceCategoryMap;
  }



  @Override
  public Map<Long, List<Long>> getShopServiceCategoryMap(Long... shopIds) {
    ConfigWriter writer = configDaoManager.getWriter();
    Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();
    if(ArrayUtil.isEmpty(shopIds)){
      return map;
    }
    List<ShopServiceCategory> serviceCategoryList = writer.getShopServiceCategoryByShopIds(shopIds);
    List<Long> categoryIds;
    for (ShopServiceCategory category : serviceCategoryList) {
      categoryIds = map.get(category.getShopId());
      if (categoryIds == null) {
        categoryIds = new ArrayList<Long>();
        map.put(category.getShopId(), categoryIds);
      }
      map.get(category.getShopId()).add(category.getServiceCategoryId());
    }
    return map;
  }

  @Override
  public void saveOrUpdateShopServiceCategory(ShopServiceCategoryDTO shopServiceCategoryDTO) {
    if(shopServiceCategoryDTO == null) {
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();

      if(shopServiceCategoryDTO.getId() != null) {
        ShopServiceCategory shopServiceCategory = writer.getById(ShopServiceCategory.class,shopServiceCategoryDTO.getId());
        if(shopServiceCategory != null) {
          shopServiceCategory.setDeleted(shopServiceCategoryDTO.getDeleted());
        }
        writer.update(shopServiceCategory);
      } else {
        ShopServiceCategory shopServiceCategory = new ShopServiceCategory();
        shopServiceCategory.fromDTO(shopServiceCategoryDTO);
        writer.save(shopServiceCategory);
      }
  }

  public Boolean saveShopServiceCategory(Long shopId,Long ... serviceCategoryIds){
    if(ArrayUtil.isEmpty(serviceCategoryIds)){
      return false;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status=writer.begin();
    try{
      for(Long categoryId:serviceCategoryIds){
        ShopServiceCategory shopServiceCategory=new ShopServiceCategory();
        shopServiceCategory.setServiceCategoryId(categoryId);
        shopServiceCategory.setShopId(shopId);
        shopServiceCategory.setDeleted(DeletedType.FALSE);
        writer.save(shopServiceCategory);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
    return true;
  }

  @Override
  public boolean isWashServiceScope(Long serviceScopeId) {
    ConfigWriter writer = configDaoManager.getWriter();
    ServiceCategory category = writer.getById(ServiceCategory.class, serviceScopeId);
    return category != null && ServiceCategoryDTO.WASH.equals(category.getName());
  }

  @Override
  public List<Node> getCheckedServiceCategory(Long shopId) {
    List<Node> nodes = ServiceCategoryCache.getAllTreeLeafNode();
    if(CollectionUtils.isNotEmpty(nodes)) {
      if(shopId != null) {
        Map<Long,String> shopServiceCategoryIdNameMap = ServiceManager.getService(IServiceCategoryService.class).getShopServiceCategoryIdNameMap(shopId);
        if(shopServiceCategoryIdNameMap != null && shopServiceCategoryIdNameMap.size() > 0) {
          for(Node node : nodes) {
            if(shopServiceCategoryIdNameMap.get(node.getId()) != null) {
              node.setHasThisNode(true);
            }
          }
        }
      }
    }
    return nodes;
  }

  @Override
  public void updateShopServiceCategory(Long shopId, String shopServiceCategoryIds) {
    ConfigWriter writer = configDaoManager.getWriter();
    if(StringUtil.isEmpty(shopServiceCategoryIds)) {
      return;
    }
    Set<Long> serviceCategoryIdSetPage = new HashSet<Long>();
    Set<Long> serviceCategoryIdSetDb = new HashSet<Long>();
    String[] serviceCategoryIds = shopServiceCategoryIds.split(",");
    for(String serviceCategoryIdStr : serviceCategoryIds) {
      serviceCategoryIdSetPage.add(NumberUtil.longValue(serviceCategoryIdStr.toString().trim()));
    }
    List<ShopServiceCategoryDTO> shopServiceCategoryDTOList =  getShopServiceCategoryDTOByShopId(shopId);
    Object status = writer.begin();
    try {
      //设置disable的
      if(CollectionUtil.isNotEmpty(shopServiceCategoryDTOList)) {
        for(ShopServiceCategoryDTO shopServiceCategoryDTO : shopServiceCategoryDTOList) {
          if(!serviceCategoryIdSetPage.contains(shopServiceCategoryDTO.getServiceCategoryId())) {
            shopServiceCategoryDTO.setDeleted(DeletedType.TRUE);
            saveOrUpdateShopServiceCategory(shopServiceCategoryDTO);
          }
          serviceCategoryIdSetDb.add(shopServiceCategoryDTO.getServiceCategoryId());
        }
      }
      //需要save的
      for(Long serviceCategoryPageId: serviceCategoryIdSetPage) {
        if(!serviceCategoryIdSetDb.contains(serviceCategoryPageId)) {
          ShopServiceCategoryDTO shopServiceCategoryDTO = new ShopServiceCategoryDTO();
          shopServiceCategoryDTO.setServiceCategoryId(serviceCategoryPageId);
          ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(serviceCategoryPageId);
          if(serviceCategoryDTO != null) {
            shopServiceCategoryDTO.setServiceCategoryName(serviceCategoryDTO.getName());
          }
          shopServiceCategoryDTO.setShopId(shopId);
          shopServiceCategoryDTO.setDeleted(DeletedType.FALSE);
          saveOrUpdateShopServiceCategory(shopServiceCategoryDTO);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }
}
