package com.bcgogo.user.service;

import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.enums.user.ServiceCategoryDataType;
import com.bcgogo.enums.user.VehicleBrandModelDataType;
import com.bcgogo.user.dto.ServiceCategoryRelationDTO;
import com.bcgogo.user.dto.VehicleBrandModelRelationDTO;
import com.bcgogo.user.model.ServiceCategoryRelation;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.VehicleBrandModelRelation;
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
public class VehicleBrandModelRelationService implements IVehicleBrandModelRelationService{
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public List<VehicleBrandModelRelationDTO> getVehicleBrandModelRelationDTOByDataId(Long shopId,Long dataId,VehicleBrandModelDataType dataType) {
    List<VehicleBrandModelRelationDTO> vehicleBrandModelRelationDTOList = new ArrayList<VehicleBrandModelRelationDTO>();
    if (shopId == null || dataId==null || dataType==null) {
      return vehicleBrandModelRelationDTOList;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<VehicleBrandModelRelation> vehicleBrandModelRelationList = writer.getVehicleBrandModelRelationsById(shopId, dataId, dataType);
    if (CollectionUtils.isNotEmpty(vehicleBrandModelRelationList)) {
      for (VehicleBrandModelRelation vehicleBrandModelRelation : vehicleBrandModelRelationList) {
        vehicleBrandModelRelationDTOList.add(vehicleBrandModelRelation.toDTO());
      }
    }
    return vehicleBrandModelRelationDTOList;
  }

}
