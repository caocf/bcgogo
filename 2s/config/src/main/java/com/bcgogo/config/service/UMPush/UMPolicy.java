package com.bcgogo.config.service.UMPush;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public class UMPolicy {
  // 可选 定时发送时间，默认为立即发送。发送时间不能小于当前时间。
  //      格式: "YYYY-MM-DD hh-mm-ss"。 注意, start_time只对broadcast,
  //      groupcast以及customizedcast且file_id不为空的情况生效, 对单播不生效。
  private String start_time;

  // 可选 消息过期时间,其值不可小于发送时间,默认为3天后过期。格式同start_time
  private String expire_time;

  // 可选 发送限速，每秒发送的最大条数。整数值。
  // 开发者发送的消息体如果有请求自己服务器的资源，可以考虑此参数。
  private Integer max_send_num ;

  public String getStart_time() {
    return start_time;
  }

  public void setStart_time(String start_time) {
    this.start_time = start_time;
  }

  public String getExpire_time() {
    return expire_time;
  }

  public void setExpire_time(String expire_time) {
    this.expire_time = expire_time;
  }

  public Integer getMax_send_num() {
    return max_send_num;
  }

  public void setMax_send_num(Integer max_send_num) {
    this.max_send_num = max_send_num;
  }
}
