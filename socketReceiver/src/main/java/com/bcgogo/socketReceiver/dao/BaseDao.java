package com.bcgogo.socketReceiver.dao;

import com.bcgogo.socketReceiver.model.base.LongIdentifier;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseDao<T extends LongIdentifier> {
  @Autowired
  protected SessionFactory sessionFactory;

  private Class currentClass;

  public BaseDao(Class currentClass) {
    this.currentClass = currentClass;
  }

  public Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  public void save(T t) {
    getSession().save(t);
  }

  public void delete(T t) {
    getSession().delete(t);
  }

  public void merge(T t) {
    getSession().merge(t);
  }

  public T getById(Long id) {
    return (T) getSession().get(currentClass, id);
  }

  public void update(T t) {
    getSession().update(t);
  }

  public void saveOrUpdate(T t) {
    getSession().saveOrUpdate(t);
  }
}
