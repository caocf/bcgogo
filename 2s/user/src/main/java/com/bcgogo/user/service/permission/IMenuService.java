package com.bcgogo.user.service.permission;

import com.bcgogo.user.dto.permission.ResourceDTO;

import java.util.Collection;

/**
 * User: ZhangJuntao
 * Date: 13-4-11
 * Time: 下午5:00
 */
public interface IMenuService {

  //根据Menu获得resource
  String buildMenu(long shopVersionId, long shopId, long userId, long userGroupId) throws Exception;
}
