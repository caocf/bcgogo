package com.bcgogo.notification.service;

import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-3
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public interface IMessageTemplateService {
  public MessageSwitchStatus getMessageSwitchStatus(Long shopId,MessageScene scene);
}
