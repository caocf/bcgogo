package com.bcgogo.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Zhangjie
 * Date: 14-11-21
 * Time: 下午5:13
 */
public class StringHandleUtil {
  public static final Logger LOG = LoggerFactory.getLogger(StringHandleUtil.class);   //slf4j 日志

  //截取一个字符串中的某一段字符
  //s为要处理的字符串；str为需要截取的数据格式; 如：“abc”字符串中获取其中的"b" ,则s="abc" ;str="a(\\w+)c"
  public static String getData(String s,String str){
    String a ="";
    Pattern p=Pattern.compile(s);
    Matcher m=p.matcher(str);
    while(m.find()){
      a =m.group(1);
    }
    return a;
  }

  //去字符串中的换行空格等
  public static String replaceBlank (String a) {
    String dest = "";
    if (a!=null) {
      Pattern p = Pattern.compile("\\s*|\t|\r|\n");
      Matcher m = p.matcher(a);
      dest = m.replaceAll("");
    }
    return dest;
  }

}
