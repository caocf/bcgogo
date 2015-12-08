package com.bcgogo.user.service;

import com.bcgogo.common.TreeMenuDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-9
 * Time: 下午5:21
 * viewer service
 */
public interface IViewerService {
  List<TreeMenuDTO> getTreeMenuByParentId(Long shopId, Long userId, Long treeId);
}
