package com.bcgogo.utils;

import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-27
 * Time: 下午3:35
 */
public class CollectionUtil extends CollectionUtils{

  /**
   * 从一个string类型的list中移出没有实际内容（包括只含空格内容）的元素
   * 返回一个新的list
   *
   * @param list
   * @return
   */
  public static List<String> filterBlankElements(List<String> list) {
    List<String> result = new ArrayList<String>();
    if (list == null || list.isEmpty()) {
      return result;
    }
    for (String element : list) {
      if (StringUtil.isEmpty(element) || StringUtil.isEmpty(element.trim())) {
        continue;
      }
      result.add(element);
    }
    return result;
  }

  /**
   * 返回集合类的第一个元素, 如果集合为空返回Null
   *
   * @param list 集合类
   */
  public static <T> T getFirst(Collection<T> list) {
    if (list == null || list.isEmpty())
      return null;
    return list.iterator().next();
  }

  /**
   * 将List中的Null元素去除.
   *
   * @param list 集合类
   */
  public static <T> void removeNullElements(Collection<T> list) {
    List<T> nullList = new ArrayList<T>();
    for (T item : list) {
      if (item == null) {
        nullList.add(item);
      }
    }
    list.removeAll(nullList);
  }

  public static <T> String collectionToCommaString(Collection<T> list) {
    if (CollectionUtils.isEmpty(list))
      return "";
    StringBuffer result = new StringBuffer();
    for (T item : list) {
      if (item != null) {
        result.append(item.toString() + ",");
      }
    }
    if (result.length() >= 1) {
      result.setLength(result.length() - 1);
    }
    return result.toString();
  }

  public static <T> Set<T> listToSet(Collection<T> list) {
    Set<T> set = new HashSet<T>();
    if (list == null || list.isEmpty())
      return null;
    for (T t : list) {
      set.add(t);
    }
    return set;
  }

  public static <T> boolean hasUniqueElement(Collection<T> list){
    if(null!=list&&list.size()==1){
      return true;
    }
    return false;
  }

  public static <T> T uniqueResult(Collection<T> list){
    if(hasUniqueElement(list)){
      return list.iterator().next();
    }
    return null;
  }

  public static <T> boolean isNotEmpty(Collection<T> list) {
    return (list != null&&!list.isEmpty());
  }


  public static <T> Collection<T> add(Collection<T> list1,Collection<T> list2){
    if(list1==null||list2==null) return list1;
    for(T elem:list2){
      list1.add(elem);
    }
    return list1;
  }

  public static boolean isSameShopBusinessScopeDTOs(List<ShopBusinessScopeDTO> listA, List<ShopBusinessScopeDTO> listB) {
    if (isEmpty(listA) && isEmpty(listB)) {
      return true;
    }
    if (isNotEmpty(listA) && isNotEmpty(listB)) {
      if (listA.size() != listB.size()) {
        return false;
      } else {
        Map<String,Integer> mapA = getShopBusinessScopeMap(listA);
        Map<String,Integer> mapB = getShopBusinessScopeMap(listB);
        if (mapA.size() != mapB.size()) {
          return false;
        } else {
          Iterator it = mapA.keySet().iterator();
          while (it.hasNext()) {
            Object obj = it.next();
            Integer countA = mapA.get(obj);
            if(countA == null){countA = 0;}
            Integer countB = mapB.get(obj);
            if(countB == null){countB = 0;}
            if (!countA.equals(countB)) {
              return false;
            }
          }
          return true;
        }
      }
    } else {
      return false;
    }
  }

  public static Map<String,Integer> getShopBusinessScopeMap(final List<ShopBusinessScopeDTO> listA) {
    Map<String,Integer> count = new HashMap<String, Integer>();
    if(isEmpty(listA)){
      return count;
    }
      for (Iterator it = listA.iterator(); it.hasNext();) {
        ShopBusinessScopeDTO obj = (ShopBusinessScopeDTO)it.next();
        StringBuffer sb = new StringBuffer();
        sb.append(obj.getShopId()).append("_").append(obj.getProductCategoryId());
          Integer c = count.get(sb.toString());
          if (c == null) {
              count.put(sb.toString(),1);
          } else {
              count.put(sb.toString(),new Integer(c.intValue() + 1));
          }
      }
      return count;
  }


}
