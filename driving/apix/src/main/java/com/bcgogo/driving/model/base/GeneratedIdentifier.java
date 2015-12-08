package com.bcgogo.driving.model.base;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.classic.Lifecycle;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class GeneratedIdentifier<T extends Serializable> extends RecordTimestamp
    implements Serializable, Lifecycle {
  private T _id;
  private Long _version;

  protected GeneratedIdentifier() {
  }

  protected GeneratedIdentifier(long createTime) {
    super(createTime);
  }

  @Id
//  @GeneratedValue(generator = "hib_hilo", strategy = GenerationType.TABLE)
//  @GenericGenerator(name = "hib_hilo", strategy = "com.bcgogo.util.hibernate.HiLoGenerator", parameters = {@org.hibernate.annotations.Parameter(name = "max_lo", value = "10000")})
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "hilo")
  @GenericGenerator(name="hilo", strategy = "hilo")
  @Column(name = "uuid", length = 42, updatable = false)
  public T getId() {
    return this._id;
  }

  protected void setId(T id) {
    this._id = id;
  }

  @Version
  protected Long getVersion() {
    return this._version;
  }

  private void setVersion(Long version) {
    this._version = version;
  }

  public boolean onSave(Session s) throws CallbackException {
    return false;
  }

  public boolean onUpdate(Session s) throws CallbackException {
    return false;
  }

  public boolean onDelete(Session s) throws CallbackException {
    return false;
  }

  public void onLoad(Session s, Serializable id) {
  }

  public String toString() {
    return new ToStringBuilder(this).appendSuper(super.toString()).append("id", this._id).append("version", this._version).toString();
  }
}