package com.bcgogo.wx.message.template;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-18
 * Time: 上午10:34
 * To change this template use File | Settings | File Templates.
 */
public class WXKWMsgTemplate extends WXMsgTemplate{
  private String first;
  private String firstColor;
  private String keyword1Color;
  private String keyword2Color;
  private String keyword3Color;

  public void setKeyword1(String keyword1,String color){
    Map<String,String> keyword1Map=new HashMap<String, String>();
    super.getData().put("keyword1", keyword1Map);
    keyword1Map.put("value",keyword1);
    keyword1Map.put("color",color);
  }

   public void setKeyword2(String keyword2,String color){
    Map<String,String> keyword2Map=new HashMap<String, String>();
    super.getData().put("keyword2", keyword2Map);
    keyword2Map.put("value",keyword2);
    keyword2Map.put("color",color);
  }

   public void setKeyword3(String keyword3,String color){
    Map<String,String> keyword3Map=new HashMap<String, String>();
    super.getData().put("keyword3", keyword3Map);
    keyword3Map.put("value",keyword3);
    keyword3Map.put("color",color);
  }

  public void setFirst(String first,String color) {
    Map<String,String> firstMap=new HashMap<String, String>();
    super.getData().put("first", firstMap);
    firstMap.put("value",first);
    firstMap.put("color",color);
  }

  public void setRemark(String remark,String color) {
    Map<String,String> remarkMap=new HashMap<String, String>();
    super.getData().put("remark", remarkMap);
    remarkMap.put("value",remark);
    remarkMap.put("color",color);
  }

  public String getFirst() {
    return first;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  public String getFirstColor() {
    return firstColor;
  }

  public void setFirstColor(String firstColor) {
    this.firstColor = firstColor;
  }

  public String getKeyword1Color() {
    return keyword1Color;
  }

  public void setKeyword1Color(String keyword1Color) {
    this.keyword1Color = keyword1Color;
  }

  public String getKeyword2Color() {
    return keyword2Color;
  }

  public void setKeyword2Color(String keyword2Color) {
    this.keyword2Color = keyword2Color;
  }

  public String getKeyword3Color() {
    return keyword3Color;
  }

  public void setKeyword3Color(String keyword3Color) {
    this.keyword3Color = keyword3Color;
  }
}
