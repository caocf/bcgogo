package com.bcgogo.service.imp;

import com.bcgogo.dao.BaseDao;
import com.bcgogo.model.base.LongIdentifier;
import com.bcgogo.service.IBaseService;

/**
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午6:06
 */
public abstract class BaseService<T extends LongIdentifier> implements IBaseService<T> {

  public abstract BaseDao<T> getDAO();

  @Override
  public void save(T t) {
    getDAO().save(t);
  }

  @Override
  public void delete(T t) {
    getDAO().delete(t);
  }

  @Override
  public void merge(T t) {
    getDAO().merge(t);
  }

  @Override
  public void update(T t) {
    getDAO().update(t);
  }

  @Override
  public T getById(Long id) {
    return getDAO().getById(id);
  }
}
