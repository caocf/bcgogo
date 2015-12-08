package com.bcgogo.utils;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-11
 * Time: 上午10:10
 */
public class ReflectUtil {


  /**
   *  返回 字段名--字段值
   * @param obj
   * @return
   * @throws IllegalAccessException
   */
  public static Map<String,Object> getFiledValMap(Object obj) throws IllegalAccessException {
    if (obj == null) return null;
    Field[] fields = obj.getClass().getDeclaredFields();
    if(ArrayUtil.isEmpty(fields)) return null;
    Map<String,Object> filedValMap=new HashMap<String, Object>();
    for (int j = 0; j < fields.length; j++) {
      fields[j].setAccessible(true);
      filedValMap.put(fields[j].getName(),fields[j].get(obj));
    }
    return filedValMap;
  }

}
