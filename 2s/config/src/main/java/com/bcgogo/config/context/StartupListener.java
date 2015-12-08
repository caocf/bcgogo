package com.bcgogo.config.context;

import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-22
 * Time: 下午12:35
 * To change this template use File | Settings | File Templates.
 */
public class StartupListener extends ContextLoaderListener implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Autowired
  private IConfigService configService;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    String localIp = ConfigUtils.getLocalIpWithConfigTomcats();
    if (StringUtils.isNotBlank(localIp)) {
      System.setProperty(ConfigConstant.CONFIG_RMI_SERVER_HOST, localIp);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {

  }
}
