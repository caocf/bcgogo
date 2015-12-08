package com.bcgogo.config.service.Apns;

import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.DeliveryError;

/**
 * Created by XinyuQiu on 14-4-10.
 */
public class GsmAPNSFeedBack implements ApnsDelegate {
  @Override
  public void messageSent(ApnsNotification message, boolean resent) {

  }

  @Override
  public void messageSendFailed(ApnsNotification message, Throwable e) {

  }

  @Override
  public void connectionClosed(DeliveryError e, int messageIdentifier) {

  }

  @Override
  public void cacheLengthExceeded(int newCacheLength) {

  }

  @Override
  public void notificationsResent(int resendCount) {

  }
}
