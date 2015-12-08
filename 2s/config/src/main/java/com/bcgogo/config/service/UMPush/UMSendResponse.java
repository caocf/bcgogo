package com.bcgogo.config.service.UMPush;

import com.bcgogo.config.service.UMPush.UMPushConstant.UMRet;

/**
 * Created by XinyuQiu on 14-4-29.
 * {
 "ret":"SUCCESS/FAIL", // 返回结果，"SUCCESS"或者"FAIL"
 "data":
 {
 // 当"ret"为"SUCCESS"时,包含如下参数:
 // 当type为unicast、customizedcast且alias不为空时:
 "msg_id":"xx" // 多个msg_id以逗号分隔。
 // 当type为于broadcast、groupcast、filecast、customizedcast且file_id不为空的情况
 "task_id":"xx"

 // 当"ret"为"FAIL"时,包含如下参数:
 "error_code":xx
 }
 }
 */
public class UMSendResponse {
  private UMRet ret;
  private UMSendResponseData data;

  public UMRet getRet() {
    return ret;
  }

  public void setRet(UMRet ret) {
    this.ret = ret;
  }

  public UMSendResponseData getData() {
    return data;
  }

  public void setData(UMSendResponseData data) {
    this.data = data;
  }
}
