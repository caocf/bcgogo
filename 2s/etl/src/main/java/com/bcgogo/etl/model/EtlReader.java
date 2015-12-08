package com.bcgogo.etl.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-25
 * Time: 下午7:04
 * To change this template use File | Settings | File Templates.
 */
public class EtlReader extends GenericReaderDao {

  public EtlReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public GsmVehicleInfo getGsmVehicleInfoByEmi(String emi,int limit) {
    Session session = this.getSession();
    try {
      Query q = SQL.getGsmVehicleInfoByEmi(session, emi, limit);
      return (GsmVehicleInfo) q.uniqueResult();
    } finally {
      release(session);
    }

  }
}
