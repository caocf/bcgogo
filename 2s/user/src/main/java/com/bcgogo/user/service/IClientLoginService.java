package com.bcgogo.user.service;

/**
 * User: ZhangJuntao
 * Date: 13-6-21
 * Time: 下午5:39
 */
public interface IClientLoginService {
  //binding
  void createClientBindingLog(Long shopId, String userNo, String mac);

  //login
  void createOrUpdateClientLoginInfo(String userNo, String clientVersion);

  //logout
  void createOrUpdateClientLogoutInfo(String userNo);


}
