package com.bcgogo.txn.model.app;

import com.bcgogo.api.AppUserBillDTO;
import com.bcgogo.enums.app.AppUserBillStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * app user 账单
 * User: ZhangJuntao
 * Date: 13-10-25
 * Time: 上午11:05
 */
@Entity
@Table(name = "app_user_bill")
public class AppUserBill extends LongIdentifier {
  private String appUserNo;
  private String content; //描述
  private AppUserBillStatus status;  //状态

  public AppUserBill() {
  }

  public AppUserBill(AppUserBillDTO dto) {
    this.setAppUserNo(dto.getAppUserNo());
    this.setContent(dto.getContent());
    this.setAppUserNo(dto.getAppUserNo());
    this.setStatus(AppUserBillStatus.SAVED);
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public AppUserBillStatus getStatus() {
    return status;
  }

  public void setStatus(AppUserBillStatus status) {
    this.status = status;
  }

  public AppUserBillDTO toDTO() {
    AppUserBillDTO dto = new AppUserBillDTO();
    dto.setId(getId());
    dto.setContent(getContent());
    dto.setAppUserNo(getAppUserNo());
    dto.setStatus(getStatus());
    return dto;
  }

  public void fromDTO(AppUserBillDTO dto) {
    if (dto != null) {
      this.setId(dto.getId());
      this.setContent(dto.getContent());
      this.setAppUserNo(dto.getAppUserNo());
      this.setStatus(dto.getStatus());
    }
  }
}
