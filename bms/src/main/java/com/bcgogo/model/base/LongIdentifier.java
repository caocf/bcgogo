package com.bcgogo.model.base;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午5:48
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false, updatable = false))
public abstract class LongIdentifier extends GeneratedIdentifier<Long> {
  protected LongIdentifier() {
  }

  protected LongIdentifier(long createTime) {
    super(createTime);
  }

  public boolean onSave(Session s) throws CallbackException {
    return false;
  }

  public String toString() {
    return new ToStringBuilder(this).appendSuper(super.toString()).toString();
  }
}