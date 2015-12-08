package com.bcgogo.notification.model;

import com.bcgogo.notification.NotificationServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;

@Component
public class NotificationDaoManager extends GenericDaoManager<NotificationReader, NotificationWriter> {

  public NotificationWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public NotificationReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected NotificationWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(NotificationServiceFactory.class);
    return new NotificationWriter(factory.createResourceTransactionManager("notification", nodeId));
  }

  @Override
  protected NotificationReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(NotificationServiceFactory.class);
    return new NotificationReader(factory.createSessionFactory("notification", nodeId));
  }

}
