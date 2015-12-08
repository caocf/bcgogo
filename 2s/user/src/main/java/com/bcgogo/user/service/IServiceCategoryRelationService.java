package com.bcgogo.user.service;

import com.bcgogo.enums.user.ServiceCategoryDataType;
import com.bcgogo.user.dto.ServiceCategoryRelationDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
public interface IServiceCategoryRelationService {
  List<ServiceCategoryRelationDTO> getServiceCategoryDTOByDataId(Long shopId, Long dataId, ServiceCategoryDataType dataType);

}
