package com.bcgogo.userreport.model;

import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.userreport.UserReportServiceFactory;
import org.springframework.stereotype.Component;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

@Component
public class UserReportDaoManager extends GenericDaoManager<UserReportReader, UserReportWriter> {

  public UserReportWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public UserReportReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected UserReportWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(UserReportServiceFactory.class);
    return new UserReportWriter(factory.createResourceTransactionManager("userreport", nodeId));
  }

  @Override
  protected UserReportReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(UserReportServiceFactory.class);
    return new UserReportReader(factory.createSessionFactory("userreport", nodeId));
  }
}
