package com.bcgogo.productRead.model;

import com.bcgogo.product.ProductServiceFactory;
import com.bcgogo.productRead.ProductReadServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;


@Component
public class ProductReadDaoManager extends GenericDaoManager<ProductReadReader, ProductReadWriter> {

  public ProductReadWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public ProductReadReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected ProductReadWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ProductReadServiceFactory.class);
    return new ProductReadWriter(factory.createResourceTransactionManager("productRead", nodeId));
  }

  @Override
  protected ProductReadReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ProductReadServiceFactory.class);
    return new ProductReadReader(factory.createSessionFactory("productRead", nodeId));
  }

}
