package com.bcgogo.report.model;

import com.bcgogo.report.ReportServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

@Component
public class ReportDaoManager extends GenericDaoManager<ReportReader, ReportWriter> {

  public ReportWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public ReportReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected ReportWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ReportServiceFactory.class);
    return new ReportWriter(factory.createResourceTransactionManager("report", nodeId));
  }

  @Override
  protected ReportReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ReportServiceFactory.class);
    return new ReportReader(factory.createSessionFactory("report", nodeId));
  }
}
