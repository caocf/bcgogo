package com.bcgogo.user.model;

import com.bcgogo.service.GenericDaoManager;
import com.bcgogo.service.ServiceFactory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.UserServiceFactory;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/12/11
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class UserDaoManager extends GenericDaoManager<UserReader, UserWriter> {

  public UserWriter getWriter() {
    return getWriterByNodeId("100");
  }

  public UserReader getReader() {
    return getReaderByNodeId("100");
  }

  @Override
  protected UserWriter createWriter(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(UserServiceFactory.class);
    return new UserWriter(factory.createResourceTransactionManager("user", nodeId));
  }

  @Override
  protected UserReader createReader(String nodeId) {
    ServiceFactory factory = ServiceManager.getServiceFactory(UserServiceFactory.class);
    return new UserReader(factory.createSessionFactory("user", nodeId));
  }

}
