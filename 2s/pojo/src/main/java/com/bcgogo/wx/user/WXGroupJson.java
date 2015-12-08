package com.bcgogo.wx.user;

import com.bcgogo.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建用户分组时用于生成json对象
 * Author: ndong
 * Date: 14-11-3
 * Time: 下午3:00
 */
public class WXGroupJson {
  private Map<String,String> group;

  public WXGroupJson(String name){
    if(StringUtil.isNotEmpty(name)){
      group=new HashMap<String, String>();
      group.put("name",name);
    }
  }
}
