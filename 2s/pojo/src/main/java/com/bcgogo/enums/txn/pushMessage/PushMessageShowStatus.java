package com.bcgogo.enums.txn.pushMessage;

/**
 * 推送消息 初始状态是 UN_ACTIVE  这种状态的消息不出现在 接受者的消息维护模块
 */
public enum PushMessageShowStatus {
  ACTIVE,
  UN_ACTIVE
}
