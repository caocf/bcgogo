package com.bcgogo.config.service.UMPush;

import com.bcgogo.config.service.UMPush.UMPushConstant.UMAfterOpen;

/**
 * Created by XinyuQiu on 14-4-29.
 * 通知展现内容:
 */
public class UMPayLoadBody {
  private String ticker;// 必填 通知栏提示文字
  private String title; // 必填 通知标题
  private String text; // 必填 通知文字描述
  // 可选 状态栏图标ID, R.drawable.[smallIcon],如果没有, 默认使用应用图标。
  //      图片要求为24*24dp的图标,或24*24px放在drawable-mdpi下。
  //      注意四周各留1个dp的空白像素
  private String icon;
  // 可选 通知栏拉开后左侧图标ID, R.drawable.[largeIcon].
  //      图片要求为64*64dp的图标,可设计一张64*64px放在drawable-mdpi下,
  //      注意图片四周留空，不至于显示太拥挤
  private String largeIcon;
  // 通知到达设备后的提醒方式
  private boolean play_vibrate = true; // 可选 收到通知是否震动,默认为"true".注意，"true/false"为字符串
  private boolean play_lights = true;  // 可选 收到通知是否闪灯,默认为"true"
  private boolean play_sound = true;   // 可选 收到通知是否发出声音,默认为"true"
  private UMAfterOpen after_open;
  private String url;
  private String activity;
  // 可选 display_type=message, 或者display_type=notification且"after_open"为"go_custom"时，
  //      该字段必填。用户自定义内容, 可以为字符串或者JSON格式。
  private String custom;

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getLargeIcon() {
    return largeIcon;
  }

  public void setLargeIcon(String largeIcon) {
    this.largeIcon = largeIcon;
  }

  public boolean isPlay_vibrate() {
    return play_vibrate;
  }

  public void setPlay_vibrate(boolean play_vibrate) {
    this.play_vibrate = play_vibrate;
  }

  public boolean isPlay_lights() {
    return play_lights;
  }

  public void setPlay_lights(boolean play_lights) {
    this.play_lights = play_lights;
  }

  public boolean isPlay_sound() {
    return play_sound;
  }

  public void setPlay_sound(boolean play_sound) {
    this.play_sound = play_sound;
  }

  public UMAfterOpen getAfter_open() {
    return after_open;
  }

  public void setAfter_open(UMAfterOpen after_open) {
    this.after_open = after_open;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public String getCustom() {
    return custom;
  }

  public void setCustom(String custom) {
    this.custom = custom;
  }
}
