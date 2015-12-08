package com.bcgogo.cache;

import java.io.Serializable;

/**
 * 缓存中的用户信息
 * User: ndong
 * Date: 12-12-28
 * Time: 上午3:46
 * To change this template use File | Settings | File Templates.
 */
public class UserReadRecordDTO implements Serializable{

  private Long lastReadDate;   // 对于一天中有多个同一类型的提醒

  public Long getLastReadDate() {
    return lastReadDate;
  }

  public void setLastReadDate(Long lastReadDate) {
    this.lastReadDate = lastReadDate;
  }
}
