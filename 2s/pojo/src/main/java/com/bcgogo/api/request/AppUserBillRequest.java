package com.bcgogo.api.request;

import com.bcgogo.api.AppUserBillDTO;
import com.bcgogo.api.AppUserImageDTO;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-10-22
 * Time: 下午4:58
 */
public class AppUserBillRequest {
  private Long id;
  private String content;
  private List<String> imageList;

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

  public List<String> getImageList() {
    return imageList;
  }

  public void setImageList(List<String> imageList) {
    this.imageList = imageList;
  }

  public AppUserBillDTO toAppUserBillDTO(String appUserNo) {
    AppUserBillDTO dto = new AppUserBillDTO();
    dto.setContent(getContent());
    dto.setId(getId());
    dto.setAppUserNo(appUserNo);
    if (CollectionUtil.isNotEmpty(getImageList())) {
      List<AppUserImageDTO> imageList = new ArrayList<AppUserImageDTO>();
      int i = 1;
      for (String str : getImageList()) {
        imageList.add(new AppUserImageDTO(null, str, null, i, null, null, (i == 1 ? ImageType.APP_USER_BILL_MAIN_IMAGE : ImageType.APP_USER_BILL_AUXILIARY_IMAGE)));
        i++;
      }
      dto.setImageList(imageList);
    }
    return dto;
  }

  @Override
  public String toString() {
    return "AppUserBillRequest{" +
        "id=" + id +
        ", content='" + content + '\'' +
        ", imageList=" + imageList +
        '}';
  }
}
