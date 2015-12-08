package com.bcgogo.product.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午4:44
 */
public class ProductReader extends GenericReaderDao {
  public ProductReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Licenseplate getLicenseplateByCarno(String carno) {
    Session session = getSession();
    try {
      return (Licenseplate) SQL.getLicenseplateByCarno(session, carno).uniqueResult();
    } finally {
      release(session);
    }
  }
}
