package com.bcgogo.notification.client.lianyu;

/**
 * User: zhangjie
 * Date: 14-12-02
 * Time: 下午
 *  发送参数
 */
public class LianYuSmsParam {
  private String userName;  //用户名
  private String password;  //密码
  private String mobile;    //电话号码
  private String content;   //内容

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
