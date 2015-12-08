package com.bcgogo.user.model.wx;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-30
 * Time: 上午2:25
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_user_group")
public class WXUserGroup extends LongIdentifier {
  private Long userId;
  private Long groupId;

   @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

   @Column(name = "group_id")
  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }
}
