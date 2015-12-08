package com.bcgogo.socketReceiver.model;

import com.bcgogo.socketReceiver.model.base.Identifiable;
import com.bcgogo.socketReceiver.model.base.LongIdentifier;

import javax.persistence.Column;
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
