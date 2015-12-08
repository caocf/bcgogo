package com.bcgogo.socketReceiver.service;

import com.bcgogo.socketReceiver.model.GsmPoint;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-22
 * Time: 上午10:17
 */
public interface IGsmAlertService {

  GsmPoint saveAlert(String info);

  GsmPoint saveZHYSAlert(String info);
}
