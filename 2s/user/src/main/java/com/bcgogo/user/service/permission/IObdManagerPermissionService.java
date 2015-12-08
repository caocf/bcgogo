package com.bcgogo.user.service.permission;

import com.bcgogo.api.ObdOperationPermissionDTO;

/**
 * Created by XinyuQiu on 14-7-10.
 */
public interface IObdManagerPermissionService {

  ObdOperationPermissionDTO getObdOperationPermissionDTO(Long userGroupId) throws Exception;



}
