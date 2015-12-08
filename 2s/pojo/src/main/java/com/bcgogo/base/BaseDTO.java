package com.bcgogo.base;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-28
 * Time: 上午5:05
 * To change this template use File | Settings | File Templates.
 */
public class BaseDTO implements Serializable {
  protected Long id;
  protected String idStr;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(String.valueOf(id));
    }else {
      setIdStr("");
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

}
