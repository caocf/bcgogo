package com.bcgogo.etl.model;

import com.bcgogo.etl.model.mongodb.XLongIdentifier;
import com.bcgogo.etl.model.mongodb.XObjectId;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午3:16
 */
public class XConfig extends XLongIdentifier {

  private String name;
  private String value;
  private String description;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
