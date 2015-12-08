package com.bcgogo.product.model;

import com.bcgogo.product.ProductServiceFactory;
import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductDaoManager extends GenericDaoManager<ProductReader, ProductWriter> {
  public ProductReader getReader() {
    return getReaderByNodeId("100");
  }

  public ProductWriter getWriter() {
    return getWriterByNodeId("100");
  }

  protected ProductWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ProductServiceFactory.class);
    return new ProductWriter(factory.createResourceTransactionManager("product", nodeId));
  }

  protected ProductReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(ProductServiceFactory.class);
    return new ProductReader(factory.createSessionFactory("product", nodeId));
  }
}
