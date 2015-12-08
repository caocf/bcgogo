package com.bcgogo.txn.service.messageCenter;

import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.user.service.permission.IUserCacheService;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-23
 * Time: 上午11:46
 */
@Component
public class AbstractMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageService.class);
  protected static final String VELOCITY_PARAMETER = "context";

  @Autowired
  private TxnDaoManager txnDaoManager;

  //todo zhangjuntao 目前是针对所有用户 可能以后改成配置用户
  protected List<Long> getUserIds(Long shopId) {
    return ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopId);
  }
  public String getMemCacheKey(Long shopId, Long userId,PushMessageCategory pushMessageCategory, MemcachePrefix prefix) {
    return prefix.getValue()+pushMessageCategory.toString()+"_" + shopId + "_" + userId;
  }

  protected String generateMsgUsingVelocity(VelocityContext context, String content, String templateName) throws Exception {
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
    ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
    ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
    ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
    ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
    try {
      ve.init();
    } catch (Exception e) {
      LOG.error("Velocity初始化时出错", e);
    }
    StringResourceRepository repo = StringResourceLoader.getRepository();
    repo.putStringResource(templateName, content);
    Template t = ve.getTemplate(templateName, "UTF-8");
    StringWriter writer = new StringWriter();
    t.merge(context, writer);
    return writer.toString();
  }
}
