package com.bcgogo.utils;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-11-18
 * Time: 上午9:21
 * To change this template use File | Settings | File Templates.
 */
public class ArrayUtil extends ArrayUtils {

  public static boolean isNotEmpty(Object[] array) {
    return !isEmpty(array);
  }


//   /**
//   * 返回集合类的第一个元素, 如果集合为空返回Null
//   *
//   * @param list 集合类
//   */
//  public static <T> T getFirst(Collection<T> list) {
//    if (list == null || list.isEmpty())
//      return null;
//    return list.iterator().next();
//  }

  /**
   * 将两个数组组装成一个
   *
   * @param array1
   * @param array2
   * @return
   */
  public static String[] addAll(String[] array1, String[] array2) {

    if (array1 == null || array2 == null) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    for (String str : array1) {
      if (StringUtil.isEmpty(str)) {
        continue;
      }
      sb.append(str);
      sb.append(",");
    }
    for (String str : array2) {
      if (StringUtil.isEmpty(str)) {
        continue;
      }
      sb.append(str);
      sb.append(",");
    }
    String all = sb.toString();
    if (StringUtil.isEmpty(all)) {
      return null;
    }
    return all.split("\\,");
  }

  public static Long[] convertToLong(String[] array) {
    if (array == null) return null;
    Long[] longs = new Long[array.length];
    for (int i = 0; i < array.length; i++) {
      if (StringUtil.isEmpty(array[i])) continue;
      longs[i] = Long.valueOf(array[i]);
    }
    return longs;
  }

  public static String[] toStringArr(Object[] arr) {
    if (isEmpty(arr)) return null;
    String[] arrString = new String[arr.length];
    for (int i = 0; i < arr.length; i++) {
      arrString[i] = StringUtil.valueOf(arr[i]);
    }
    return arrString;
  }

  public static  String[] toStringArr(Collection list) {
    if(list==null||list.size()==0){
      return new String[0];
    }
    return toStringArr(list.toArray());
  }

  public static  Long[] toLongArr(Collection list) {
    if(list==null||list.size()==0){
      return new Long[0];
    }
    return toLongArr(list.toArray(new Long[list.size()]));
  }

  public static Long[] toLongArr(Object[] arr) {
    if (isEmpty(arr)) return null;
    Long[] arrString = new Long[arr.length];
    for (int i = 0; i < arr.length; i++) {
      arrString[i] = NumberUtil.longValue(arr[i]);
    }
    return arrString;
  }

//  public static <T> T [] toArray(Collection<T> list,Class clazz) {
//    if (CollectionUtil.isEmpty(list)) return null;
//     return list.toArray(new Object<T>[list.size()]);
//  }

    public static <T> T getFirst(T [] list) {
        if (isEmpty(list)) return null;
        return list[0];
    }

  /**
   * 转换Id数组变成一个String字段
   * @param list
   * @return
   */
  public static String toStringFromIdArray(Long[] list){
    if (isEmpty(list)) return null;
    StringBuilder sb=new StringBuilder();
    for (Long elem:list){
      sb.append(StringUtil.valueOf(elem));
      sb.append(",");
    }
    return sb.toString().substring(0,sb.length()-1);
  }

  /**
   * @param array
   * @return
   */
  public static boolean isEmpty(Object[] array){
    if(ArrayUtils.isEmpty(array)) return true;
    //元素有length无元素的
    for(Object o:array){
        if(o!=null) return false;
    }
    return true;
  }


}
