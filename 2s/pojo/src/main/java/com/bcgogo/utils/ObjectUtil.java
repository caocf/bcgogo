package com.bcgogo.utils;

import org.springframework.util.ObjectUtils;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-31
 * Time: 上午6:28
 * To change this template use File | Settings | File Templates.
 */
public class ObjectUtil extends ObjectUtils{

  /**
   * 不是空的字符串对象
   * @param obj
   * @return
   */
  public static boolean isNotEmptyStr(Object obj) {
    return (obj != null && obj.toString().trim() !="");
  }

  public static String generateKey(Object... key_words){
    StringBuffer sb=new StringBuffer();
    if(ArrayUtil.isEmpty(key_words)){
      return "";
    }
    for(Object word:key_words){
      if(word==null){
        continue;
      }
      sb.append(String.valueOf(word));
    }
    return sb.toString();
  }

public static String generateIntervalKey(Object... key_words){
    StringBuffer sb=new StringBuffer();
    if(ArrayUtil.isEmpty(key_words)){
      return "";
    }
    int count=0;
    for(Object word:key_words){
      if(word==null){
        continue;
      }
      if(count==0){
        sb.append(String.valueOf(word));
      }
      sb.append("_").append(String.valueOf(word));
      count++;
    }
    return sb.toString();
  }

}
