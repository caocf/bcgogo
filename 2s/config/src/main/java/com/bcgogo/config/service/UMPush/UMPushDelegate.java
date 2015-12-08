package com.bcgogo.config.service.UMPush;

import com.notnoop.apns.DeliveryError;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public interface UMPushDelegate {

  public final static UMPushDelegate EMPTY = new UMPushDelegateAdapter();

  public void messageSent(UMNotification message, UMSendResponse sendResponse);

  public void messageSendFailed(UMNotification message, Throwable e);


}
