package com.bcgogo.wx;

import com.bcgogo.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-28
 * Time: 下午5:25
 */
public enum WXCommand {
  ADMIN("admin"),
  ;
  private String name;

  WXCommand(String name){
    this.name=name;
  }


  private static Map<String,WXCommand> commandMap=new HashMap<String, WXCommand>();

  static {
    commandMap.put(ADMIN.name,ADMIN);
  }

  public static WXCommand getCommand(String str){
    return commandMap.get(str);
  }

}
