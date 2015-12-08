package com.bcgogo.product.model;

import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.util.spring.hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-6-17
 * Time: 上午11:50
 * To change this template use File | Settings | File Templates.
 */
public class GenericWriterDaoTest extends GenericWriterDao {
  public GenericWriterDaoTest(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }
  protected void release(Session session) {
    HibernateUtil.releaseSession(session, getSessionFactory());
    sqlTime(session);
  }

  public void sqlTime(Session session){
    Statistics statistics = session.getSessionFactory().getStatistics();
    Object []  objects = new String[2];
    objects[0] = String.valueOf(statistics.getQueryExecutionMaxTime());
    objects[1] = String.valueOf(statistics.getEntityLoadCount());
    LOG.debug("AOP_SQL:执行时间：{}ms,entityLoadCount:{}", objects);
    statistics.clear();
  }
}
