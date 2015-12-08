package com.bcgogo.txn.model;

import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.TxnServiceFactory;
import org.springframework.stereotype.Component;


@Component
public class TxnDaoManager extends GenericDaoManager<TxnReader, TxnWriter> {

  public TxnWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public TxnReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected TxnWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(TxnServiceFactory.class);
    return new TxnWriter(factory.createResourceTransactionManager("txn", nodeId));
  }

  @Override
  protected TxnReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(TxnServiceFactory.class);
    return new TxnReader(factory.createSessionFactory("txn", nodeId));
  }

}
