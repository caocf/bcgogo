package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-7
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "agents")
public class Agents extends LongIdentifier {
  private String agentCode;
  private String name;
  private String personInCharge;
  private String address;
  private String mobile;
  private String respArea;
  private int state;

  public Agents() {
  }
  @Column(name = "state")
  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  @Column(name = "agent_code")
  public String getAgentCode() {
    return agentCode;
  }

  public void setAgentCode(String agentCode) {
    this.agentCode = agentCode;
  }
  @Column(name = "person_in_charge")
  public String getPersonInCharge() {
    return personInCharge;
  }

  public void setPersonInCharge(String personInCharge) {
    this.personInCharge = personInCharge;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
 @Column(name = "address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
  @Column(name = "resp_area")
  public String getRespArea() {
    return respArea;
  }

  public void setRespArea(String respArea) {
    this.respArea = respArea;
  }
}
