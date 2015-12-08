package com.bcgogo.wx;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-10
 * Time: 下午2:26
 * To change this template use File | Settings | File Templates.
 */
public class GetShortUrlResult extends ErrCode{
  private String short_url;

  public String getShort_url() {
    return short_url;
  }

  public void setShort_url(String short_url) {
    this.short_url = short_url;
  }
}
