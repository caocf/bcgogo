package com.bcgogo.socketReceiver.service;

import com.bcgogo.socketReceiver.model.GsmVehicleInfo;
import com.bcgogo.socketReceiver.service.base.IBaseService;

/**
 * User: ZhangJuntao
 * Date: 14-3-6
 * Time: 下午5:51
 * saveVehicleInfo 处理联华盈科 dtu数据
 *
 */
public interface IGsmVehicleInfoService extends IBaseService<GsmVehicleInfo> {
  //saveVehicleInfo 处理联华盈科 dtu数据
  String saveVehicleInfo(String orgInfo);

  //saveZHYSVehicleInfo 处理刘工 智慧云赏的数据
  String saveZHYSVehicleInfo(String orgInfo);
}
