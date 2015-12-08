package com.bcgogo.stat.model;

import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.StatServiceFactory;
import org.springframework.stereotype.Component;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

@Component
public class StatDaoManager extends GenericDaoManager<StatReader, StatWriter> {

  public StatWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public StatReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected StatWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(StatServiceFactory.class);
    return new StatWriter(factory.createResourceTransactionManager("stat", nodeId));
  }

  @Override
  protected StatReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(StatServiceFactory.class);
    return new StatReader(factory.createSessionFactory("stat", nodeId));
  }
}
