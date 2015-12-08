package com.bcgogo.socketReceiver.model.base;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午5:33
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class RecordTimestamp {
  private long _creationDate;
  private long _lastModified;

  protected RecordTimestamp() {
  }

  protected RecordTimestamp(long createTion) {
    if (createTion > 0L)
      setCreationDate(createTion);
  }

  @Column(name = "created", nullable = false, updatable = false)
  public long getCreationDate() {
    return this._creationDate;
  }

  protected void setCreationDate(long creationDate) {
    this._creationDate = creationDate;
    setLastModified(creationDate);
  }

  @Column(name = "last_update", nullable = false)
  public long getLastModified() {
    return this._lastModified;
  }

  protected void setLastModified(long lastModified) {
    this._lastModified = lastModified;
  }

  @PrePersist
  public void creationDate() {
    if (this._creationDate == 0L)
      setCreationDate(System.currentTimeMillis());
  }

  @PreUpdate
  public void lastModified() {
    setLastModified(System.currentTimeMillis());
  }

  public String toString() {
    return new ToStringBuilder(this).appendSuper(super.toString()).append("created", this._creationDate).append("last_modified", this._lastModified).toString();
  }
}