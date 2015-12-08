package com.bcgogo.api;

import com.bcgogo.enums.app.AppUserBillStatus;
import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.utils.CollectionUtil;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-10-17
 * Time: 上午11:29
 */
public class AppUserBillDTO {
  private Long id;
  private String appUserNo;
  private String content;
  private AppUserBillStatus status = AppUserBillStatus.SAVED;  //状态
  private List<AppUserImageDTO> imageList;

  public AppUserBillDTO() {
  }

  public AppUserBillDTO(Long id, String content, List<AppUserImageDTO> imageList) {
    this.id = id;
    this.content = content;
    this.imageList = imageList;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<AppUserImageDTO> getImageList() {
    return imageList;
  }

  public void setImageList(List<AppUserImageDTO> imageList) {
    this.imageList = imageList;
  }

  @Override
  public String toString() {
    return "AppUserBillDTO{" +
        "id=" + id +
        ", content='" + content + '\'' +
        ", imageList=" + imageList +
        '}';
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public AppUserBillStatus getStatus() {
    return status;
  }

  public void setStatus(AppUserBillStatus status) {
    this.status = status;
  }

  public EnquiryDTO toEnquiryDTO() {
    EnquiryDTO dto = new EnquiryDTO();
    dto.setAppUserNo(getAppUserNo());
    dto.setBillId(getId());
    dto.setStatus(EnquiryStatus.SAVED);
    dto.setDescription(getContent());
    if (CollectionUtil.isNotEmpty(getImageList())) {
      dto.setEnquiryImages(getImageList().toArray(new AppUserImageDTO[getImageList().size()]));
    }
    return dto;
  }
}
