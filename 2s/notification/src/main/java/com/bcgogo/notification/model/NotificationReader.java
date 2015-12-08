package com.bcgogo.notification.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.SessionFactory;

public class NotificationReader extends GenericReaderDao {

  public NotificationReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
