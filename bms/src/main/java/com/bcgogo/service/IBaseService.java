package com.bcgogo.service;


import com.bcgogo.model.base.LongIdentifier;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public interface IBaseService<T extends LongIdentifier> {
  void save(T t);

  void delete(T t);

  void merge(T t);

  void update(T t);

  T getById(Long id);
}
