package com.bcgogo.etl.model;


import com.bcgogo.model.LongIdentifier;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-25
 * Time: 下午6:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="gsm_obd_status")
public class GsmObdStatus extends LongIdentifier {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
