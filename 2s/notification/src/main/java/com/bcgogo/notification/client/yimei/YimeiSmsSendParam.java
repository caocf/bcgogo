package com.bcgogo.notification.client.yimei;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.notification.client.SmsParam;

/**
 * User: ZhangJuntao
 * Date: 13-5-7
 * Time: 下午2:33
 * 不包括业务数据 发送参数
 */
public class YimeiSmsSendParam extends YimeiSmsParam {
  //全局唯一
  private String uuid;
  private String addserial;
  private String seqid;
  private int smspriority;

  public String getAddserial() {
    return addserial;
  }

  public void setAddserial(String addserial) {
    this.addserial = addserial;
  }

  public String getSeqid() {
    return seqid;
  }

  public void setSeqid(String seqid) {
    this.seqid = seqid;
  }

  public int getSmspriority() {
    return smspriority;
  }

  public void setSmspriority(int smspriority) {
    this.smspriority = smspriority;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
}
