package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppShopCommentDTO;
import com.bcgogo.api.AppShopDTO;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:08
 */
public class ApiShopResponse extends ApiResponse {
  private AppShopDTO shop;
  private int commentCount;
  private List<AppShopCommentDTO> appShopCommentDTOs;

  public ApiShopResponse() {
    super();
  }

  public ApiShopResponse(ApiResponse response) {
    super(response);
  }

  public AppShopDTO getShop() {
    return shop;
  }

  public void setShop(AppShopDTO shop) {
    this.shop = shop;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public List<AppShopCommentDTO> getAppShopCommentDTOs() {
    return appShopCommentDTOs;
  }

  public void setAppShopCommentDTOs(List<AppShopCommentDTO> appShopCommentDTOs) {
    this.appShopCommentDTOs = appShopCommentDTOs;
  }
}
