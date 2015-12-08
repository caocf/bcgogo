package com.bcgogo.wx.action;

import com.bcgogo.wx.MsgType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-3
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
public class WXUserAction implements Serializable{

  private static final long serialVersionUID = -5843852821468789628L;

  private WXActionType actionType;
  private WXActionName actionName;
  private Map<String,String> data=new HashMap<String, String>();

  public WXUserAction(WXActionType actionType){
    this.actionType=actionType;
  }

  public WXUserAction(WXActionType actionType,WXActionName actionName){
    this.actionType=actionType;
    this.actionName=actionName;
  }


  public WXActionType getActionType() {
    return actionType;
  }

  public void setActionType(WXActionType actionType) {
    this.actionType = actionType;
  }

  public WXActionName getActionName() {
    return actionName;
  }

  public void setActionName(WXActionName actionName) {
    this.actionName = actionName;
  }

  public Map<String, String> getData() {
    return data;
  }

  public void setData(Map<String, String> data) {
    this.data = data;
  }
}
