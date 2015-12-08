package com.bcgogo.user.service.permission;

import com.bcgogo.api.ObdOperationPermissionDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.model.UserDaoManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by XinyuQiu on 14-7-10.
 */
@Component
public class ObdManagerPermissionService implements IObdManagerPermissionService {
  private static final Logger LOG = LoggerFactory.getLogger(ObdManagerPermissionService.class);
  @Override
  public ObdOperationPermissionDTO getObdOperationPermissionDTO(Long userGroupId) throws Exception{
    ObdOperationPermissionDTO permissionDTO = new ObdOperationPermissionDTO();
    if(userGroupId != null){
      IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
      List<ResourceDTO> resourceDTOList = privilegeService.getResourceDTOList(null, userGroupId);
      if(CollectionUtils.isNotEmpty(resourceDTOList)){
        for(ResourceDTO resourceDTO : resourceDTOList){


          if(resourceDTO!=null && StringUtils.isNotBlank(resourceDTO.getValue())){
            if("CRM.OBD_MANAGER_IMPORT".equals(resourceDTO.getValue())){
              permissionDTO.setImport(true);
            }
            if("CRM.OBD_MANAGER_EDIT".equals(resourceDTO.getValue())){
              permissionDTO.setEdit(true);
            }
            if("CRM.OBD_MANAGER_OUT_STORAGE".equals(resourceDTO.getValue())){
              permissionDTO.setOutStorage(true);
            }
            if("CRM.OBD_MANAGER_SELL".equals(resourceDTO.getValue())){
              permissionDTO.setSell(true);
            }
            if("CRM.OBD_MANAGER_COMBINE".equals(resourceDTO.getValue())){
              permissionDTO.setPackage(true);
            }
            if("CRM.OBD_MANAGER_DELETE".equals(resourceDTO.getValue())){
              permissionDTO.setDelete(true);
            }
            if("CRM.OBD_MANAGER_SPLIT".equals(resourceDTO.getValue())){
              permissionDTO.setSplit(true);
            }
            if("CRM.OBD_MANAGER_SHOW_LOG".equals(resourceDTO.getValue())){
              permissionDTO.setLog(true);
            }
            if("CRM.OBD_MANAGER_RETURN".equals(resourceDTO.getValue())){
              permissionDTO.setReturn(true);
            }
            if("CRM.OBD_MANAGER_VIEW_ALL".equals(resourceDTO.getValue())){
              permissionDTO.setAdmin(true);
            }
          }
        }
      }

    }
    return permissionDTO;
  }
}
