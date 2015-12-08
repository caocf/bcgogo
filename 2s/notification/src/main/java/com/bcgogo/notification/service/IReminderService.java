package com.bcgogo.notification.service;

import com.bcgogo.notification.model.Reminder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午2:07
 * To change this template use File | Settings | File Templates.
 */
public interface IReminderService {

  void saveOrUpdateReminder(Reminder reminder);

  Map isTrialExpired(Long shopId) throws UnsupportedEncodingException;

}
