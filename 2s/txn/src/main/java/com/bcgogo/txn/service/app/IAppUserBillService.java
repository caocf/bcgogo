package com.bcgogo.txn.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserBillDTO;
import com.bcgogo.common.AllListResult;
import com.bcgogo.enums.config.ImageScene;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-10-25
 * Time: 上午11:15
 */
public interface IAppUserBillService {
  boolean deleteAppUserBillService(Long id, String appUserNo, String failMsg);

  ApiResponse saveAppUserBill(AppUserBillDTO dto) throws Exception;

  AllListResult<AppUserBillDTO> getAppUserBillListByUserNo(String appUserNo, List<ImageScene> imageSceneList, int currentPage, int pageSize);

  AppUserBillDTO getAppUserBillById(Long id, List<ImageScene> imageSceneList);

}
