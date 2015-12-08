package com.bcgogo.notification.service;

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.notification.dto.MessageSwitchDTO;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-3
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MessageTemplateService implements IMessageTemplateService{
    private static final Logger LOG = LoggerFactory.getLogger(MessageTemplateService.class);

  @Autowired
  private NotificationDaoManager notificationDaoManager;

  public MessageSwitchStatus getMessageSwitchStatus(Long shopId,MessageScene scene){
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageSwitchStatus switchStatus = (MessageSwitchStatus) MemCacheAdapter.get(MemcachePrefix.messageSwitch.toString()+
        scene.toString() + shopId);
    if( null != switchStatus){
      return switchStatus;
    }

    MessageSwitchDTO messageSwitchDTO = notificationService.getMessageSwitchDTOByShopIdAndScene(shopId,scene);
    if(null == messageSwitchDTO || null == messageSwitchDTO.getStatus()){
      return null;
    }
    MemCacheAdapter.set(MemcachePrefix.messageSwitch.toString()+messageSwitchDTO.getScene().toString()+shopId,
        messageSwitchDTO.getStatus());
    return messageSwitchDTO.getStatus();
  }
}
