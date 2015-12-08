package com.bcgogo.search.model;

import com.bcgogo.search.SearchServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 12/31/11
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SearchDaoManager extends GenericDaoManager<SearchReader, SearchWriter> {

  public SearchWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public SearchReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected SearchWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(SearchServiceFactory.class);
    return new SearchWriter(factory.createResourceTransactionManager("search", nodeId));
  }

  @Override
  protected SearchReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(SearchServiceFactory.class);
    return new SearchReader(factory.createSessionFactory("search", nodeId));
  }

}

