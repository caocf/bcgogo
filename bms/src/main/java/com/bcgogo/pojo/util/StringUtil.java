package com.bcgogo.pojo.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-6
 * Time: 下午1:37
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {
  private static final Logger LOG = LoggerFactory.getLogger(StringUtil.class);
  public static final String EMPTY_STRING = "";
  public static final String APP_EMPTY_STRING = "NULL";
  public static final String SOLR_PLACEHOLDER_STRING = "*";

  public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  public static boolean isEmptyAppGetParameter(String str) {
    return str == null || str.length() == 0 || APP_EMPTY_STRING.equals(str) || "null".equals(str);
  }
  public static boolean isNotEmptyAppGetParameter(String str) {
    return !isEmptyAppGetParameter(str);
  }

  public static String StringValueOf(Object o) {
    if (o == null) return null;
    return String.valueOf(o);
  }

  public static boolean isNotEmpty(String str) {
    return str != null && str.length() != 0;
  }


  public static String formateStr(String str) {
    return str == null ? "" : str.trim();
  }

  public static boolean isAllEmpty(String... str) {
    if (ArrayUtils.isEmpty(str)) return true;
    for (String s : str) {
      if (StringUtils.isNotBlank(s)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isAllEmpty(Object... str) {
    if (ArrayUtils.isEmpty(str)) return true;
    for (Object s : str) {
      if (s!=null && StringUtils.isNotBlank(s.toString())) {
        return false;
      }
    }
    return true;
  }

  /**
   * String数组有空值
   *
   * @param str
   * @return
   */
  public static boolean hasEmptyVal(String... str) {
    if (ArrayUtils.isEmpty(str)) return true;
    for (String s : str) {
      if (isEmpty(s)) {
        return true;
      }
    }
    return false;
  }

  public static String convertinputStreamToString(InputStream ists) throws IOException {

    if (ists != null) {
      StringBuilder sb = new StringBuilder();
      String line;

      try {
        BufferedReader r1 = new BufferedReader(new InputStreamReader(ists, "UTF-8"));
        while ((line = r1.readLine()) != null) {
          sb.append(line).append("\n");
        }
      } finally {
        ists.close();
      }
      return sb.toString();
    } else {
      return "";
    }
  }

  public static String subString(String str) {
    if (StringUtils.isNotBlank(str)) {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }

  public static String subUpString(String str) {
    if (StringUtils.isNotBlank(str)) {
      str = str.substring(1, str.length());
    }
    return str;
  }

  /**
   * 获取唯一性字符串
   *
   * @return
   */
  public static String generateSoleString() {
    return UUID.randomUUID().toString();
  }

  public static String subString(String str, int begin, int end) {
    if (str != null && !"".equals(str)) {
      int size = str.length();
      str = str.substring(begin, end >= size ? size : end);
    }
    return str;
  }

  public static boolean judgeSpacesInStrArray(String strs[]) {
    if (strs == null) return true;
    String temp = "";
    for (String str : strs) {
      if (str != null && str.length() != 0) {
        temp += str;
      }
    }
    return temp.trim().length() == 0 ? true : false;
  }

  public static boolean isEqual(String a, String b) {
    if (a != null && b != null && a.equals(b)) {
      return true;
    }
    if (a == null && b == null) {
      return true;
    }
    return false;
  }

  /**
   * 空值与null认为相等。
   * @param a
   * @param b
   * @return
   */
  public static boolean isEqualIgnoreBlank(String a, String b) {
    if(StringUtils.isBlank(a) && StringUtils.isBlank(b)){
      return true;
    }else if(StringUtils.isBlank(a) || StringUtils.isBlank(b)){
      return false;
    }
    return a.equals(b);
  }

  public static String truncValue(String str) {
    if (str == null) {
      return EMPTY_STRING;
    }
    return str;
  }

  public static String replaceBlankStr(String str) {
    if (StringUtils.isBlank(str))
      return null;
    return str;
  }

  //String数组中，全部为空 返回true,否则返回false
  public static boolean strArrayIsBlank(String... q) {
    if (q == null) return true;
    for (String str : q) {
      if (StringUtils.isNotBlank(str))
        return false;
    }
    return true;
  }

  public static String arrayToStr(String separator, Object... objects) {
    if (ArrayUtils.isEmpty(objects)) return "";
    if (separator == null) separator = ",";
    StringBuffer buffer = new StringBuffer();
    for (Object o : objects) {
      buffer.append(o).append(separator);
    }
    return buffer.substring(0, buffer.length() - 1);
  }

  /**
   * join str by ,
   */
  public static String jointStrings(String str1, String str2,String separator) {
    if(separator == null) separator = ",";
    if (StringUtils.isBlank(str1)) return str2;
    if (StringUtils.isBlank(str2)) return str1;
    return str1 + separator + str2;
  }
  /**
   * join str by ,
   */
  public static String jointStrings(String str1, String str2) {
    if (StringUtils.isBlank(str1)) return str2;
    if (StringUtils.isBlank(str2)) return str1;
    return str1 + "," + str2;
  }
  /**
   * 将两个String合并成一个，例如，"a,b,c","b,c,d",合并成"a,b,c,d"
   */
  public static String joinStrings(String str1, String str2, String separator) {
    Set<String> stringSet = new HashSet<String>();
    if (StringUtils.isNotBlank(str1)) {
      stringSet.addAll(Arrays.asList(str1.split(separator)));
    }
    if (StringUtils.isNotBlank(str2)) {
      stringSet.addAll(Arrays.asList(str2.split(separator)));
    }
    StringBuffer sb = new StringBuffer();
    if (!stringSet.isEmpty()) {
      for (String str : stringSet) {
        sb.append(str).append(separator);
      }
      if (sb != null && sb.length() > 0) {
        return sb.substring(0, sb.length() - 1);
      }
    }
    return sb.toString();
  }

  public static String joinStrings(String separator, String defaultStr, String... strs) {
    StringBuffer sb = new StringBuffer();
    if (ArrayUtils.isEmpty(strs)) {
      return defaultStr;
    }
    for (String str : strs) {
      if(StringUtils.isEmpty(str)){
        continue;
      }
      if (sb.length() > 0) {
        sb.append(separator);
      }
      sb.append(str);
    }
    return sb.toString();
  }

  public static String doubleStringToIntegerString(String str) {
    if (StringUtils.isEmpty(str)) {
      return null;
    }
    if (str.indexOf(".") != -1) {
      String[] str2 = str.split("\\.");
      return str2[0];
    }
    return str;
  }

  public static Long[] parseLongArray(String[] strs) throws Exception {
    Long[] longs = new Long[strs.length];
    for (int i = 0; i < strs.length; i++)
      longs[i] = Long.parseLong(strs[i].trim());
    return longs;
  }

  public static Integer[] parseIntegerArray(String[] strs) throws Exception {
    Integer[] integers = new Integer[strs.length];
    for (int i = 0; i < strs.length; i++)
      integers[i] = Integer.parseInt(strs[i].trim());
    return integers;
  }

  public static String[] parseStringArray(Set<Long> ids) {
    if(CollectionUtil.isNotEmpty(ids)){
      String[] strings = new String[ids.size()];
      int i = 0;
      for (Long id : ids) {
        strings[i] = id == null ? null : id.toString();
        i++;
      }
      return strings;
    }else {
      return new String[0];
    }
  }

  public static <T> T nullToObject(T t) {
    if (t != null) {
      if ("null".equals(t.toString()))
        return null;
    }
    return t;
  }

  //ToDo: need better implementation.
  public static boolean isOverlap(String s1, String s2) {
    if (s1 == null || s2 == null) return false;
    for (int i = 0; i < s2.length(); i++) {
      if (s1.toLowerCase().contains(s2.substring(i, i + 1).toLowerCase())) return true;
    }
    return false;
  }

  public static boolean isContains(String s1, String s2) {
    if (s1 == null || s2 == null) return false;
    if (s2.toLowerCase().contains(s1.toLowerCase())) return true;
    return false;
  }
  /**
   * 把字符串的所有子串都拆出来
   * 例如:abcd  得到 a b c d ab bc cd abc bcd abcd
   *
   * @param s
   * @param result  //Set就去重复  List  全部返回
   * @param exclude
   */
  public static void parserToSubString(String s, Collection<String> result, Collection<String> exclude) {
    try {
      if (StringUtils.isBlank(s)) return;
      if (StringUtils.isNotBlank(s)) {
        s = s.replaceAll("\\s*", "");
        // 一上来先全串。
        if (!exclude.contains(s)) result.add(s);
        if (s.length() == 1) return;
        //从每个字符那把原串，切成两半，
        for (int i = 1; i < s.length(); i++) {// 下标从1开始
          if (!exclude.contains(s)) result.add(s.substring(0, i));
          if (!exclude.contains(s)) result.add(s.substring(i));
        }
        // 得到中间的子串， 如， abcde 则为： bcd
        String middleSubString = s.substring(1, s.length() - 1);
        int midLength = middleSubString.length();
        if (midLength > 1) {// 当中间的子串，长度大于1时 递归调用。
          parserToSubString(middleSubString, result, exclude);
        } else if (midLength == 1) {// 中间字串，长度为1时，直接获取。
          if (!exclude.contains(s)) result.add(middleSubString);
        }
      }
    } catch (Exception e) {
      LOG.error("{} parserToSubString  error!", s);
    }
  }

  public static boolean isPrefixOfWord(String s1, String s2) {
    if (s1 == null || s2 == null) return false;

    String[] words = s2.toLowerCase().split(" ");
    for (String word : words) {
      if (word.startsWith(s1.toLowerCase())) return true;
    }
    return false;
  }

  public static String toTrimAndUpperCase(String str) {
    if (StringUtils.isBlank(str)) {
      return "";
    }

    str = str.replaceAll(" ", "").toUpperCase();

    return str;
  }
  public static String toTrim(String str) {
    if(str==null) return "";
    return str.trim();
  }

  public static String toUpperCase(String str) {
    return str == null ? null : str.toUpperCase();
  }

  /**
   * 因为double会出现科学技术法，所以转换成字符串显示
   *
   * @param value 要改的值
   * @param num   保留几位小数
   */
  public static String doubleToString(Double value, int num) {
    return doubleToString(value, num, "0");
  }

  public static String doubleToString(Double value, int num, String nullDefault) {
    if (null == value) {
      return nullDefault;
    }

    DecimalFormat df = new DecimalFormat();

    df.setMinimumFractionDigits(num);

    df.setMaximumFractionDigits(num);

    return String.valueOf(df.format(value).replaceAll(",", ""));
  }

  /**
   * String 数组转换为byte
   */
  public static byte[] getListStringToByte(Set<String> words) {
    String word = "";
    if (CollectionUtil.isNotEmpty(words)) {
      for (String s : words) {
        word = word + s + " ";
      }
    }
    return word.getBytes();
  }

  public static String longToString(Long value, String def) {
    if (null == value) {
      return def;
    }
    return String.valueOf(value);
  }

  public static String getListStringToString(Set<String> words) {
    String word = "";
    for (String s : words) {
      word = word + s + " ";
    }
    return word;
  }

  public static List<String> getSetStringFromByte(byte[] bytes) {
    List<String> stringSet = new ArrayList<String>();
    String s = new String(bytes);
    String[] words = s.split(" ");
    for (String word : words) {
      stringSet.add(word);
    }
    return stringSet;
  }

  /**
   * String 数组转换为byte
   */
  public static byte[] stringSetToByteArray(Set<String> words) {
    String word = "";
    for (String s : words) {
      word += s + " ";
    }
    return word.getBytes();
  }

  /**
   * String List转换为byte
   */
  public static byte[] getListStringToByte(List<String> words) {
    String word = "";
    if (CollectionUtil.isNotEmpty(words)) {
      for (String s : words) {
        word += s + " ";
      }
    }
    return word.getBytes();
  }

  /**
   * 截取字符串 并返回 截取结果 + ...
   *
   * @param str
   * @param start
   * @param end
   * @return
   */
  public static String getShortString(String str, int start, int end) {
    if (StringUtils.isEmpty(str)) {
      return "";
    }
    int length = str.length();
    start = start > length ? length : start;
    if (end > length) {
      return str;
    }
    end = end > length ? length : end;
    return str.substring(start, end) + "...";
  }


  /**
   * 截取字符串 并返回 截取结果 + ...
   *
   * @param str
   * @param start
   * @param offset
   * @return
   */
  public static String getShortStringByNum(String str, int start, int offset) {
    if (StringUtils.isEmpty(str)) {
      return "";
    }
    int length = str.length();
    start = start > length ? length : start;
    offset = offset > length ? length : offset;
    return str.substring(start, offset);
  }

  public static String getShortStr(String str,int offset) {
    str=toTrim(str);
    if(StringUtil.isEmpty(str)) return "";
    if(offset<=0) return str;
    if (StringUtils.isEmpty(str)) {
      return "";
    }
    int length = str.length();
    if(offset>=length){
      return str;
    }
    offset = offset > length ? length : offset;
    return str.substring(0, offset)+"...";
  }

  /**
   * 两个字符串是否相同
   *
   * @param oldStr
   * @param newStr
   * @return
   */
  public static boolean compareSame(String oldStr, String newStr) {
    if (StringUtils.isBlank(oldStr) && StringUtils.isBlank(newStr)) {
      return true;
    }
    if (StringUtils.isBlank(oldStr) && StringUtils.isNotBlank(newStr)) {
      return false;
    }
    if (StringUtils.isNotBlank(oldStr) && StringUtils.isBlank(newStr)) {
      return false;
    }
    return oldStr.equals(newStr);
  }

  /**
   * 两个Long是否相同
   *
   * @param oldLong
   * @param newLong
   * @return
   */
  public static boolean compareSame(Long oldLong, Long newLong) {
    if (oldLong == null && newLong == null) {
      return true;
    } else if (oldLong != null && newLong != null) {
      return oldLong.equals(newLong);
    } else {
      return false;
    }
  }


  /**
   * @param str      :
   *                 source string
   * @param width    :
   *                 string's byte width
   * @param ellipsis :
   *                 a string added to abbreviate string bottom
   * @return String Object
   */
  public static String abbreviate(String str, int width, String ellipsis) {
    if (str == null || "".equals(str)) {
      return "";
    }

    int d = 0; // byte length
    int n = 0; // char length
    for (; n < str.length(); n++) {
      d = (int) str.charAt(n) > 256 ? d + 2 : d + 1;
      if (d > width) {
        break;
      }
    }

    if (d > width) {
      n = n - ellipsis.length() / 2;
      return str.substring(0, n > 0 ? n : 0) + ellipsis;
    }

    return str.substring(0, n);
  }

  /**
   *    string added to abbreviate string bottom
   * @return String Object
   */
  public static String Html2Text(String inputString) {
    if(StringUtils.isBlank(inputString)) return null;
    String htmlStr = inputString; // 含html标签的字符串
    String textStr = "";
    Pattern p_script;
    Matcher m_script;
    Pattern p_style;
    Matcher m_style;
    Pattern p_html;
    Matcher m_html;

    Pattern p_html1;
    Matcher m_html1;

    try {
      String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
      // }
      String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
      // }
      String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
      String regEx_html1 = "<[^>]+";
      p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
      m_script = p_script.matcher(htmlStr);
      htmlStr = m_script.replaceAll(""); // 过滤script标签

      p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
      m_style = p_style.matcher(htmlStr);
      htmlStr = m_style.replaceAll(""); // 过滤style标签

      p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
      m_html = p_html.matcher(htmlStr);
      htmlStr = m_html.replaceAll(""); // 过滤html标签

      p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
      m_html1 = p_html1.matcher(htmlStr);
      htmlStr = m_html1.replaceAll(""); // 过滤html标签

      textStr = htmlStr;
      textStr = textStr.replaceAll("&nbsp;"," ");
    } catch (Exception e) {
      System.err.println("Html2Text: " + e.getMessage());
    }

    return textStr;// 返回文本字符串
  }

  /**
   * 使用java正则表达式去掉多余的.与0
   * @param s
   * @return
   */
  public static String subZeroAndDot(String s){
    if(s.indexOf(".") > 0){
      s = s.replaceAll("0+?$", "");//去掉多余的0
      s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
    }
    return s;
  }

  private static Random random = null;

  private static Random getRandomInstance() {

    if (random == null) {
      random = new Random(new Date().getTime());
    }
    return random;
  }


  //获得一个随机汉字
  public static String getChinese() {
    String str = null;
    int highPos, lowPos;
    Random random = getRandomInstance();
    highPos = (176 + Math.abs(random.nextInt(39)));
    lowPos = 161 + Math.abs(random.nextInt(93));
    byte[] b = new byte[2];
    b[0] = (new Integer(highPos)).byteValue();
    b[1] = (new Integer(lowPos)).byteValue();
    try {
      str = new String(b, "GB2312");
    } catch (UnsupportedEncodingException e) {
      LOG.error(e.getMessage(), e);
    }
    return str;
  }

  //获得一个定长的随机汉字串
  public static String getFixedLengthChinese(int length) {
    String str = "";
    for (int i = length; i > 0; i--) {
      str = str + getChinese();
    }
    return str;
  }

  //获得一个最大为length长的随机汉字串
  public static String getRandomLengthChiness(int length) {
    String str = "";
    length = getRandomInstance().nextInt(length + 1);
    for (int i = 0; i < length; i++) {
      str = str + getChinese();
    }
    return str;
  }

  //随机字符数字，字母混合
  public static String getCharacterAndNumber(int length) {
    String val = "";
    for (int i = 0; i < length; i++) {
      Random random = getRandomInstance();
      String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

      if ("char".equalsIgnoreCase(charOrNum)) // 字符串
      {
        int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
        val += (char) (choice + random.nextInt(26));
      } else if ("num".equalsIgnoreCase(charOrNum)) // 数字
      {
        val += String.valueOf(random.nextInt(10));
      }
    }

    return val;
  }

  //随机字符数字，字母，汉字混合
  public static String getCharacterNumberOrChinese(int length) {
    String val = "";
    Random random = getRandomInstance();
    for (int i = 0; i < length; i++) {
      int randomVal = random.nextInt(3) % 3;
      if (randomVal == 0) {    // 字符串
        //取得大写字母还是小写字母
        int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
        val += (char) (choice + random.nextInt(26));
      } else if (randomVal == 1) {       // 数字
        val += String.valueOf(random.nextInt(10));
      } else if (randomVal == 2) {   //汉字
        val +=  getChinese();
      }
    }

    return val;
  }

  //随机数字
  public static String getRandomNumberStr(int length) {
    String str = "";
    for (int i = 0; i < length; i++) {
      Random random = getRandomInstance();
      str += random.nextInt(10);
    }
    return str;
  }

  public static String valueOf(Object obj){
    if(obj==null) return "";
    return String.valueOf(obj);
  }

  public static String filerStopWords(String word,List<String> solrMatchStopWordList){
    if(CollectionUtil.isNotEmpty(solrMatchStopWordList) && StringUtils.isNotBlank(word)){
      for(String stopWord:solrMatchStopWordList){
        word = word.replaceAll(stopWord,"");
      }
    }
    return word;
  }

  /**
   * 得到网页中图片的地址
   *   //重点在于正则表达式 <img.*src=(.*?)[^>]*?>
   //               src=\"?(.*?)(\"|>|\\s+)
   */
  public static List<String> getImgStr(String htmlStr) {
    String img = "";
    Pattern p_image;
    Matcher m_image;
    List<String> pics = new ArrayList<String>();
    if(StringUtils.isBlank(htmlStr)) return pics;
    String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
    p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
    m_image = p_image.matcher(htmlStr);
    while (m_image.find()) {
      img = img + "," + m_image.group();
      System.out.println(img);
      Matcher m = Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src
      while (m.find()) {
        pics.add(m.group(1));
      }
    }
    return pics;
  }





  //从一个字符串中提取最少min位数的数字
  public static String extractNumbers(String str, int min) {
    if (StringUtils.isBlank(str)) {
      return null;
    }
    String[] numberArray = str.replaceAll("[^0-9]", ",").split(",");
    String result = null;
    for (int i = 0, len = numberArray.length; i < len; i++) {
      if (numberArray[i] != null && numberArray[i].matches("^[0-9]+$") && numberArray[i].length() >= min) {
        if (result == null) {
          result = numberArray[i];
        } else if (numberArray[i].length() >= result.length()) {
          result = numberArray[i];
        }
      }
    }
    return result;
  }


  //比较当前app的版本和服务器版本，如果当前版本小于服务器版本 return true ，表示要更新
  public static boolean compareAppVersion(String currentVersion, String lastVersion) {
    if (StringUtils.isEmpty(lastVersion)) {
      return false;
    }
    if (StringUtils.isEmpty(currentVersion)) {
      return true;
    }
    Pattern pattern1 = Pattern.compile("\\(");
    Pattern pattern2 = Pattern.compile("\\.");

    String[] currentVersionArr = pattern1.split(currentVersion, 0);
    String[] lastVersionArr = pattern1.split(lastVersion, 0);
    String[] currentVersionNumberStrArr = pattern2.split(currentVersionArr[0], 0);
    String[] lastVersionNumberStrArr = pattern2.split(lastVersionArr[0], 0);

    int currentLen = currentVersionNumberStrArr.length;
    int lastLen = lastVersionNumberStrArr.length;
    for (int i = 0; i < currentLen || i < lastLen; i++) {
      int currentVal = 0;
      int lastVal = 0;
      if (i < currentLen && NumberUtils.isNumber(currentVersionNumberStrArr[i])) {
        currentVal = Integer.parseInt(currentVersionNumberStrArr[i]);
      }
      if (i < lastLen && NumberUtils.isNumber(lastVersionNumberStrArr[i])) {
        lastVal = Integer.parseInt(lastVersionNumberStrArr[i]);
      }

      if (currentVal < lastVal) {
        return true;
      } else if (currentVal > lastVal) {
        return false;
      }
    }
    int currentVersionBuild = 0;
    int lastVersionBuild = 0;
    if (currentVersionArr.length > 1) {
      currentVersionBuild = NumberUtil.intValue(currentVersionArr[1].split("\\)")[0]);
    }
    if (lastVersionArr.length > 1) {
      lastVersionBuild = NumberUtil.intValue(lastVersionArr[1].split("\\)")[0]);
    }
    return currentVersionBuild < lastVersionBuild;
  }

  /**
   * 将“123456789" 提取前i个字符，后面跟上 ellipsis
   * 如果 str 小于 i 则不需要
   * @param str
   * @param i
   * @param ellipsis
   * @return
   */
  public static Object shortStr(String str, int i, String ellipsis) {
    if (StringUtils.isEmpty(str) || str.length() < i || i < 1) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str.substring(0, i + 1));
    if (ellipsis != null) {
      sb.append(ellipsis);
    }
    return sb.toString();
  }

  // 全局数组
  private final static String[] strDigits = {"0", "1", "2", "3", "4", "5",
    "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

  /**
   * 返回形式为数字跟字符串
   */
  private static String byteToArrayString(byte bByte) {
    int iRet = bByte;
    if (iRet < 0) {
      iRet += 256;
    }
    int iD1 = iRet / 16;
    int iD2 = iRet % 16;
    return strDigits[iD1] + strDigits[iD2];
  }

  /*
   * convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
  * @param src byte[] data
  * @return hex string
  */
  public static String bytesToHexString(byte[] src){
    StringBuilder sb = new StringBuilder("");
    if (src == null || src.length <= 0) {
      return null;
    }
    for (int i = 0; i < src.length; i++) {
      int v = src[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2){
        sb.append(0);
      }
      sb.append(hv);
    }
    return sb.toString();
  }

  /**
   * Convert hex string to byte[]
   * @param hexString the hex string
   * @return byte[]
   */
  public static byte[] hexStringToBytes(String hexString) {
    if (hexString == null || hexString.equals("")) {
      return null;
    }
    hexString = hexString.toUpperCase();
    int length = hexString.length() / 2;
    char[] hexChars = hexString.toCharArray();
    byte[] d = new byte[length];
    for (int i = 0; i < length; i++) {
      int pos = i * 2;
      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
    }
    return d;
  }

  private static byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }

  public static String getRandomString(int length) { //length表示生成字符串的长度
      String base = "abcdefghijklmnopqrstuvwxyz0123456789";
      Random random = new Random();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < length; i++) {
          int number = random.nextInt(base.length());
          sb.append(base.charAt(number));
      }
      return sb.toString();
   }



  public static void main(String [] args){
    String org="test";
    String bStr=bytesToHexString(org.getBytes());
    System.out.println(bStr);
    System.out.println(new String(hexStringToBytes(bStr)));
  }



}
