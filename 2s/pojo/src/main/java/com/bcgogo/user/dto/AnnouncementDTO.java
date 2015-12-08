package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-20
 * Time: 上午2:48
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementDTO extends ReminderDTO{

  public enum AnnouncementStatus {
    UNANNOUNCE("未发布"),
    ANNOUNCEMENTTING("发布中"),
    ANNOUNCED("已过期"),
    ENABLED("存在"),
    DISABLED("删除");

    private final String name;
    private AnnouncementStatus(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
