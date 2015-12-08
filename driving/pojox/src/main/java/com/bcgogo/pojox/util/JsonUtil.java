package com.bcgogo.pojox.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-12
 * Time: 下午12:58
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtil {

  private static Gson gson = new Gson();

  public static final String EMPTY_JSON_STRING = "[]";

  public static String listToJson(List list) {
    if (CollectionUtil.isEmpty(list)) {
      return EMPTY_JSON_STRING;
    }
    GsonBuilder builder = new GsonBuilder();
    builder.serializeNulls();
    Gson gson = builder.create();
    return gson.toJson(list).replaceAll(":null,", ":\"\",").replaceAll(":null}", ":\"\"}"); //todo 临时排除null
  }

  public static Map<String, String> jsonToStringMap(String json) {
    if (StringUtil.isEmpty(json)) {
      return new HashMap<String, String>();
    }
    Type type = new TypeToken<Map<String, String>>() {
    }.getType();
    Map<String, String> result = gson.fromJson(json, type);
    return result;
  }

  //public static String listToJson(List list) {
//    if (list == null) {
//      return EMPTY_JSON_STRING;
//    }
//    Gson gson = new Gson();
//    return gson.toJson(list);
//    GsonBuilder builder = new GsonBuilder();
//    builder.serializeNulls();
//    Gson gson = builder.create();
//    return gson.toJson(list).replaceAll(":null,", ":\"\","); //todo 临时排除null
//  }
  public static String mapToJson(Map map) {
    if (map == null || map.isEmpty()) {
      return EMPTY_JSON_STRING;
    }
    return gson.toJson(map);
  }

  //转换json结果 没有引号
  public static String listToJsonNoQuote(List list) {
    if (list == null) {
      return EMPTY_JSON_STRING;
    }
    Gson gson = new Gson();
    return gson.toJson(list);
  }


  public static <T> T fromJson(String json, Class<T> clazz) {
    if (StringUtil.isEmpty(json) || EMPTY_JSON_STRING.equals(json)) {
      return null;
    }
    return gson.<T>fromJson(jsonStandardizing(json), clazz);
  }

  //排除无用字符 规范json
  public static String jsonStandardizing(String json) {
    return json.replace("\n", "").replace("\r", "").replace(" ", "").replace("[]", "{}");
  }

  public static String objectToJson(Object object) {
    if (object == null) {
      return EMPTY_JSON_STRING;
    }
    return gson.toJson(object);
  }

  /**
   * 对象里包含html元素。contain html
   * @param object
   * @return
   */
  public static String objectCHToJson(Object object) {
    if (object == null) {
      return EMPTY_JSON_STRING;
    }
    GsonBuilder gb =new GsonBuilder();
    gb.disableHtmlEscaping();
    return gb.create().toJson(object);
//    return gson.toJson(object);
  }

  public static Object jsonToObject(String json, Class clazz) {
    if (StringUtil.isEmpty(json) || EMPTY_JSON_STRING.equals(json)) {
      return null;
    }
    return gson.fromJson(json,clazz);
  }

   public static <T> T jsonToObj(String json, Class<T> clazz) {
    if (StringUtil.isEmpty(json) || EMPTY_JSON_STRING.equals(json)) {
      return null;
    }
    return gson.fromJson(json,clazz);
  }

  public static <T> List<T> jsonArrayToList(String jsonStr, Class<T> clazz, List<T> results) {
    if(results==null){
      results=new ArrayList<T>();
    }
    List<T> list = gson.fromJson(jsonStr, new TypeToken<List<T>>() {}.getType());
    if (CollectionUtil.isNotEmpty(list)) {
      for (T t : list) {
        results.add(gson.<T>fromJson(gson.toJson(t), clazz));
      }
    }
    return results;
  }

   public static boolean isEmpty(String jsonStr){
         return "[]".equals(jsonStr.trim());
    }

}
