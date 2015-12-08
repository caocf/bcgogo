package com.bcgogo.config.model;

import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;


@Component
public class ConfigDaoManager extends GenericDaoManager<ConfigReader, ConfigWriter> {

  public ConfigWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public ConfigReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected ConfigWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ConfigServiceFactory.class);
    return new ConfigWriter(factory.createResourceTransactionManager("config", nodeId));
  }

  @Override
  protected ConfigReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ConfigServiceFactory.class);
    return new ConfigReader(factory.createSessionFactory("config", nodeId));
  }

}
