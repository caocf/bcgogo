package com.bcgogo.payment.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.SessionFactory;

public class PaymentReader extends GenericReaderDao {

  public PaymentReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
