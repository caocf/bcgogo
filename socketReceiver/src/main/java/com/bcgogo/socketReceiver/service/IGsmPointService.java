package com.bcgogo.socketReceiver.service;

import com.bcgogo.socketReceiver.model.GsmPoint;
import com.bcgogo.socketReceiver.service.base.IBaseService;

/**
 * User: ZhangJuntao
 * Date: 14-3-6
 * Time: 下午5:51
 */
public interface IGsmPointService extends IBaseService<GsmPoint> {

  //联华盈科 aut数据
  String savePoint(String orgInfo);

  //刘工智慧云裳 aut数据（dtu和aut 合在一条）
  String saveZHYSPoint(String info);
}
