package com.bcgogo.report.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.SessionFactory;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class ReportReader extends GenericReaderDao {

  public ReportReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
