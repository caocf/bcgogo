package com.bcgogo.config.service;

import com.bcgogo.config.model.SmsDonationLog;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-2-28
 * Time: 下午5:23
 */
public interface ISmsDonationLogService {
  SmsDonationLog createSmsDonationLog(SmsDonationLog smsDonationLog);

  List<SmsDonationLog> getSmsDonationLogByShopId(Long shopId);
}
