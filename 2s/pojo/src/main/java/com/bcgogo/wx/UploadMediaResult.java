package com.bcgogo.wx;

import com.bcgogo.wx.ErrCode;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-23
 * Time: 下午3:32
 * To change this template use File | Settings | File Templates.
 */
public class UploadMediaResult extends ErrCode{
  private String type;
  private String media_id;
  private String created_at;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMedia_id() {
    return media_id;
  }

  public void setMedia_id(String media_id) {
    this.media_id = media_id;
  }

  public String getCreated_at() {
    return created_at;
  }

  public void setCreated_at(String created_at) {
    this.created_at = created_at;
  }
}
