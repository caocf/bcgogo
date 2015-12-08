package com.bcgogo.userreport.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.SessionFactory;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class UserReportReader extends GenericReaderDao {

  public UserReportReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
