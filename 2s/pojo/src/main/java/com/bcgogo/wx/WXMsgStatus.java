package com.bcgogo.wx;

import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-16
 * Time: 下午5:13
 * To change this template use File | Settings | File Templates.
 */
public enum WXMsgStatus {
  AUDITING("审核中"),
  AUDITING_FAILED("审核不通过"),
  SENT("已发送"),
  LOCAL_FAILED("本地服务器的未发送成功"),
  SUCCESS("发送成功"),
  FAILED("发送成功"),
  USER_BLOCK("用户拒收"),
  SYSTEM_FAILED("他原因失败"),
  ;

  private String name;

  WXMsgStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public static WXMsgStatus getStatusEnum(String statusStr){
    if(StringUtil.isEmpty(statusStr)) return null;
    if("success".equals(statusStr)||"send success".equals(statusStr)){
      return SUCCESS;
    }
//    else if("failed:user block".equals(statusStr)){
//      return SUCCESS;
//    }
    return null;
  }
}
