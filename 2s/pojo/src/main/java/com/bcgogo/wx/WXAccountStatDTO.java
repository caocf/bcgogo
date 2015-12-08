package com.bcgogo.wx;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-1-28
 * Time: 10:45
 */
public class WXAccountStatDTO {
  private Long id;
  private String name;
  private int userNum;
  private int userGrowth;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getUserNum() {
    return userNum;
  }

  public void setUserNum(int userNum) {
    this.userNum = userNum;
  }

  public int getUserGrowth() {
    return userGrowth;
  }

  public void setUserGrowth(int userGrowth) {
    this.userGrowth = userGrowth;
  }
}
