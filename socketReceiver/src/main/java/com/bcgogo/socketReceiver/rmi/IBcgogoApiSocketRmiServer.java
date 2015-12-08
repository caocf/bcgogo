package com.bcgogo.socketReceiver.rmi;

import com.bcgogo.common.Result;
import com.bcgogo.socketReceiver.model.GsmPoint;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-10
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
public interface IBcgogoApiSocketRmiServer {
  //发送故障码
  Result sendFaultCode(String iMei,String faultCodes,Long reportTime);

  Result sendAlert(String imei, String lat, String lon, String gsmPointTypeStr, String uploadTimeStr);
}
