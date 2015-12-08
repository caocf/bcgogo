package com.bcgogo.wx.user;

import com.bcgogo.wx.ErrCode;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-3
 * Time: 下午3:05
 */
public class CreateWXGroupResult extends ErrCode{
   private Group group;

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  private class Group{
    private String id;
    private String name;
  }
}
