package com.bcgogo.payment.model;

import com.bcgogo.payment.PaymentServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;


@Component
public class PaymentDaoManager extends GenericDaoManager<PaymentReader, PaymentWriter> {

  public PaymentWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public PaymentReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected PaymentWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(PaymentServiceFactory.class);
    return new PaymentWriter(factory.createResourceTransactionManager("payment", nodeId));
  }

  @Override
  protected PaymentReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(PaymentServiceFactory.class);
    return new PaymentReader(factory.createSessionFactory("payment", nodeId));
  }

}
