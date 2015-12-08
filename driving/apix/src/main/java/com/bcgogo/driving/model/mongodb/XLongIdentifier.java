package com.bcgogo.driving.model.mongodb;


import com.bcgogo.pojox.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午8:54
 */
public abstract class XLongIdentifier {

  private XObjectId _id;
  private XNumberLong created;
  private XNumberLong lastUpdate;

  public XLongIdentifier() {
    created = new XNumberLong(System.currentTimeMillis());
    lastUpdate = created;
  }

  public XObjectId get_id() {
    return _id;
  }

  public void set_id(XObjectId _id) {
    this._id = _id;
  }

  public void set_id(String id) {
    if (StringUtil.isNotEmpty(id)) {
      this.set_id(new XObjectId(id));
    }
  }

  public void beforeUpdate() {
    this._id = null;
    lastModified();
  }

  public XNumberLong getCreated() {
    return created;
  }

  public void setCreated(XNumberLong created) {
    this.created = created;
  }

  public XNumberLong getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(XNumberLong lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void lastModified() {
    setLastUpdate(new XNumberLong(System.currentTimeMillis()));
  }

}
