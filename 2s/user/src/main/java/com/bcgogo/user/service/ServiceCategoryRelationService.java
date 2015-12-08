package com.bcgogo.user.service;

import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.enums.user.ServiceCategoryDataType;
import com.bcgogo.user.dto.ServiceCategoryRelationDTO;
import com.bcgogo.user.model.ServiceCategoryRelation;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午10:11
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ServiceCategoryRelationService implements IServiceCategoryRelationService{
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public List<ServiceCategoryRelationDTO> getServiceCategoryDTOByDataId(Long shopId, Long dataId, ServiceCategoryDataType dataType) {
    List<ServiceCategoryRelationDTO> serviceCategoryRelationDTOList = new ArrayList<ServiceCategoryRelationDTO>();
    if (shopId == null || dataId==null || dataType==null) {
      return serviceCategoryRelationDTOList;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<ServiceCategoryRelation> serviceCategoryRelationList = writer.getServiceCategoryRelationsById(shopId, dataId, dataType);
    if (CollectionUtils.isNotEmpty(serviceCategoryRelationList)) {
      for (ServiceCategoryRelation serviceCategoryRelation : serviceCategoryRelationList) {
        ServiceCategoryRelationDTO serviceCategoryRelationDTO = serviceCategoryRelation.toDTO();
        ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(serviceCategoryRelationDTO.getServiceCategoryId());
        serviceCategoryRelationDTO.setServiceCategoryName(serviceCategoryDTO.getName());
        serviceCategoryRelationDTOList.add(serviceCategoryRelationDTO);
      }
    }
    return serviceCategoryRelationDTOList;
  }

}
