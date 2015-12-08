package com.bcgogo.common;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-17
 * Time: 下午2:49
 * 后台返回消息提示 JSON
 */
public class PopMessage<T> {
  private T message;

  public T getMessage() {
    return message;
  }

  public void setMessage(T message) {
    this.message = message;
  }
}
