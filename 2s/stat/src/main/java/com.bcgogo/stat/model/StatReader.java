package com.bcgogo.stat.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.SessionFactory;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class StatReader extends GenericReaderDao {

  public StatReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
