package com.bcgogo.user.dto;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-2-9
 * Time: 上午9:17
 * To change this template use File | Settings | File Templates.
 */
public class UserVercodeDTO implements Serializable {
  Long id = null;
  Long created = null;
  String userNo = null;
  String vercode = null;
  Long update = null;


  public UserVercodeDTO(){

  }
  public UserVercodeDTO(Long id, Long created, String userNo, String vercode) {
    this.id = id;
    this.created = created;
    this.userNo = userNo;
    this.vercode = vercode;
  }

  public UserVercodeDTO(Long created, String userNo, String vercode) {
    this.created = created;
    this.userNo = userNo;
    this.vercode = vercode;
  }

  public Long getId() {
    return id;
  }
  public Long getCreated() {
    return created;
  }
  public String getUserNo() {
    return userNo;
  }
  public String getVercode() {
    return vercode;
  }

  public void setUpdate(Long update) {
    this.update = update;
  }

  public Long getUpdate() {
    return update;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setCreated(Long created) {
    this.created = created;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public void setVercode(String vercode) {
    this.vercode = vercode;
  }
}
