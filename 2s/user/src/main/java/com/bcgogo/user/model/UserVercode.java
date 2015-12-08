package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UserVercodeDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-2-9
 * Time: 上午9:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="user_vercode")
public class UserVercode extends LongIdentifier{
  String userNo = null;
  String vercode = null;


  public UserVercodeDTO toDTO(){
    UserVercodeDTO dto = new UserVercodeDTO();
    dto.setUserNo(this.userNo);
    dto.setVercode(this.vercode);
    dto.setCreated(this.getCreationDate());
    dto.setId(this.getId());

    return dto;
  }
  public UserVercode fromDTO(UserVercodeDTO dto){
    this.setUserNo(dto.getUserNo());
    this.setVercode(dto.getVercode());
    this.setCreationDate(dto.getCreated());
    this.setId(dto.getId());
    this.setLastModified(dto.getUpdate());
    return this;
  }
   public UserVercode(){

   }
  @Column(name="userno")
  public String getUserNo() {
    return userNo;
  }
  @Column(name="vercode")
  public String getVercode() {
    return vercode;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public void setVercode(String vercode) {
    this.vercode = vercode;
  }
}
