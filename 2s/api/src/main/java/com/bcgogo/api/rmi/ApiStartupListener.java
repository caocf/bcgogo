package com.bcgogo.api.rmi;

import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.utils.ConfigConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-26
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
public class ApiStartupListener extends ContextLoaderListener implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(ApiStartupListener.class);

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Autowired
  private IConfigService configService;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    String localIp = ConfigUtils.getLocalIpWithConfigApiTomcats();
    if (StringUtils.isNotBlank(localIp)) {
      System.setProperty(ConfigConstant.CONFIG_RMI_SERVER_HOST, localIp);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {

  }
}