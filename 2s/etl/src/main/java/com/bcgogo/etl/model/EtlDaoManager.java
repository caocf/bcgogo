package com.bcgogo.etl.model;

import com.bcgogo.etl.EtlServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-25
 * Time: 下午7:04
 * To change this template use File | Settings | File Templates.
 */
@Component
public class EtlDaoManager extends GenericDaoManager<EtlReader, EtlWriter> {

  public EtlWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public EtlReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected EtlWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(EtlServiceFactory.class);
    return new EtlWriter(factory.createResourceTransactionManager("etl", nodeId));
  }

  @Override
  protected EtlReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(EtlServiceFactory.class);
    return new EtlReader(factory.createSessionFactory("etl", nodeId));
  }
}
