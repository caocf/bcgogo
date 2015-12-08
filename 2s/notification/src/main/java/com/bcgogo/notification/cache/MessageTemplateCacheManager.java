package com.bcgogo.notification.cache;

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.notification.model.MessageTemplate;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Hashtable;

/**
 * 管理本地MessageTemplate缓存
 * Created by IntelliJ IDEA.
 * User: dongnan
 * Date: 12-7-19
 * Time: 上午11:17
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MessageTemplateCacheManager {
  private static final Logger LOG = LoggerFactory.getLogger(MessageTemplateCacheManager.class);

  private static final String CACHE_KEY_INVERVAL = "_";
  private static final String CACHE_KEY_PREFIX = "msgTemplate_";
  public static Long SYNC_INTERVAL = 60000L;

  private static Hashtable<String, MessageTemplate> local_cached = new Hashtable();

  /**
   * 根据type和shopId获得MessageTemplate配置项
   *
   * @param type
   * @param shopId
   * @return
   */
  public static MessageTemplate getMessageTemplate(String type, Long shopId) {
    String key = getKey(type, shopId);
    MessageTemplate msgTemplate = local_cached.get(key);
    if (LOG.isDebugEnabled()) {
      if (msgTemplate == null)
        LOG.debug("Local cache messageTemplate is null.");
      else
        LOG.debug("Local cache messageTemplate " + msgTemplate.getType() + " value is " + msgTemplate.getContent() + ".");
    }
    if (msgTemplate == null) {
      refreshSingle(type,shopId);
      return local_cached.get(key);
    }
    if (System.currentTimeMillis() - msgTemplate.getSyncTime() >= SYNC_INTERVAL) {
      String value = (String) MemCacheAdapter.get(key);
      if (NumberUtil.longValue(value, 0L) > msgTemplate.getSyncTime()) {
        refreshSingle(type,shopId);
        msgTemplate = local_cached.get(key);
      }
    }
    return msgTemplate;
  }






  /**
   * 本地内存刷新一个短信模板
   *
   * @param type
   * @return
   */
  public static boolean refreshSingle(String type,Long shopId) {
    if (StringUtil.isEmpty(type)) {
      return false;
    }
    NotificationDaoManager notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
    NotificationWriter writer = notificationDaoManager.getWriter();
    MessageTemplate msgTemplate = writer.getMsgTemplateByType(type);
    msgTemplate.setSyncTime(System.currentTimeMillis());
    local_cached.put(getKey(type, shopId), msgTemplate);
    return true;
  }

  private static String getKey(String type, Long shopId) {
    return CACHE_KEY_PREFIX + StringUtil.truncValue(type) + CACHE_KEY_INVERVAL + StringUtil.truncValue(String.valueOf(shopId));
  }
}
