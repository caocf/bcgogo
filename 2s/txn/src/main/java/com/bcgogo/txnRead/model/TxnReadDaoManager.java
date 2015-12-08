package com.bcgogo.txnRead.model;

import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.TxnServiceFactory;
import com.bcgogo.txnRead.TxnReadServiceFactory;
import org.springframework.stereotype.Component;


@Component
public class TxnReadDaoManager extends GenericDaoManager<TxnReadReader, TxnReadWriter> {

  public TxnReadWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public TxnReadReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected TxnReadWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(TxnReadServiceFactory.class);
    return new TxnReadWriter(factory.createResourceTransactionManager("txnRead", nodeId));
  }

  @Override
  protected TxnReadReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(TxnReadServiceFactory.class);
    return new TxnReadReader(factory.createSessionFactory("txnRead", nodeId));
  }

}
