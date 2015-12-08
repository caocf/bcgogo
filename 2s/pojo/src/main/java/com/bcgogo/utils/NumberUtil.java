package com.bcgogo.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-6
 * Time: 上午10:55
 * To change this template use File | Settings | File Templates.
 */
public class NumberUtil {
  private static final Logger LOG = LoggerFactory.getLogger(NumberUtil.class);
  public static final int DAY_TIMEMILLIS = 86400000;  //一天=86400000毫秒
  public static final int PRECISION = 2;              //precision  精度
  public static final double doubleCompareResult = 0.000001;//两个duoble值的比较结果

  public static final int MONEY_PRECISION = 2;

  public static final String SPLIT_REGEX = ",";
  public static final int AMOUNT_PRECISION = 2;

  public static double doubleVal(Object value) {
    return value == null?0:Double.valueOf(value.toString());
  }

  public static double doubleVal(Double value) {
    return value == null ? 0 : value;
  }

  public static boolean isEqual(Long value1, Long value2) {
    if (value1 == null || value2 == null) {
      return false;
    }
    return value1.equals(value2);
  }

  /**
   * 都为Null时return true.
   * @param value1
   * @param value2
   * @return
   */
  public static boolean isEqualIgnoreNull(Long value1, Long value2) {
    if(value1 == null && value2 == null){
      return true;
    }else if (value1 == null || value2 == null) {
      return false;
    }
    return value1.equals(value2);
  }

  public static boolean isEqual(Double value1, Double value2) {
    if (value1 == null || value2 == null) {
      return false;
    }
    return value1.equals(value2);
  }

  public static boolean isEqual(Double value1, Double value2, Double precision) {
    if (precision == null) return isEqual(value1, value2);
    return isEqual(value1, value2) || Math.abs(value1 - value2) < precision;
  }

  public static boolean isEqual(Integer value1, Integer value2) {
    if(value1 == null || value2 == null){
      return false;
    }
    return value1.equals(value2);
  }

  public static boolean isZero(Double val) {
    return val == null ||  Math.abs(val) <= 0;
  }

  public static boolean isZero(Double val, double precision) {
    return Math.abs(val) <= precision;
  }

  public static long yuanToFen(double yuan) {
    return (long) yuan * 100;
  }

  public static Double subtraction(Object... params){
    if(params==null||params[0]==null||params.length==0) return 0d;
    Double total=Double.valueOf(params[0].toString())*2;
    for(Object param:params){
      if(param==null) param=0d;
      total-=Double.valueOf(param.toString());
    }
    return NumberUtil.round(total);
  }

  public static Double addition(Object... params){
    if(params==null) return 0d;
    Double total=0d;
    for(Object param:params){
      if(param==null) continue;
      total+=Double.valueOf(param.toString());

    }
    return NumberUtil.round(total);
  }
  public static Long additionLong(Object... params){
    if(params==null) return 0l;
    Long total=0l;
    for(Object param:params){
      if(param==null) continue;
      total+=Long.valueOf(param.toString());

    }
    return total;
  }

  /**
   * 保证不丢失精准度
   * @param param1
   * @param param2
   * @return
   */
  public static Double subtract(Double param1,Double param2){
    return subtract(String.valueOf(param1),String.valueOf(param2));
  }

  public static Double subtract(String param1, String param2) {
    param1 = StringUtil.isEmpty(param1) ? "0" : param1;
    param2 = StringUtil.isEmpty(param2) ? "0" : param2;
    BigDecimal a = new BigDecimal(param1);
    BigDecimal b = new BigDecimal(param2);
    return a.subtract(b).doubleValue();
  }

  public static Double add(Double param1,Double param2){
    BigDecimal a=new BigDecimal(String.valueOf(param1));
    BigDecimal b=new BigDecimal(String.valueOf(param2));
    return a.add(b).doubleValue();
  }

  //30000000->30,000,000
  public static String formatDoubleWithComma(Double num, String defVal) {
    if (num == null) return defVal;
    try {
      return String.valueOf(NumberFormat.getInstance().format(NumberUtil.round(num, 2)));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return defVal;
  }

  /**
   * 如果传入参数无小位数，则格式化后亦无小数位。如果有，则返回最大2位小数
   * @param num
   * @return 最大两位小数
   */
  public static String formatDouble(Double num){
    if(num == null) return "";
    try{
      DecimalFormat df = new DecimalFormat("#.##");
      return df.format(num);
    }catch(Exception e){
      LOG.error(e.getMessage(), e);
      return "N/A";
    }
  }


  public static long longValue(String numStr, long defVal) {
    if (StringUtil.isEmpty(numStr)) {
      return defVal;
    }
    try {
      return Long.parseLong(numStr);
    } catch (NumberFormatException nfe) {
      return defVal;
    }
  }

  public static Double numberValue(Double t, double defVal) {
    if (t == null) {
      return defVal;
    }
    return t;
  }

  public static Double numberValue(Float t, double defVal) {
    if (t == null) {
      return defVal;
    }
    return t.doubleValue();
  }
  public static Long longValue(Object numObject) {
    if (numObject==null) {
      return null;
    }
    return longValue(numObject.toString());
  }

  public static Long longValue(String numStr) {
    if (StringUtil.isEmpty(numStr)) {
      return null;
    }
    try {
      return Long.parseLong(numStr);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

 public static boolean isEmptyAppGetParameter(Long obj){
    return obj == null  || "NULL".equals(obj) || "null".equals(obj);
 }

  public static double doubleValue(Object numStr, double defVal) {
    if (StringUtil.isEmpty(StringUtil.valueOf(numStr))) {
      return defVal;
    }
    try {
      return Double.parseDouble(StringUtil.valueOf(numStr));
    } catch (NumberFormatException nfe) {
      return defVal;
    }
  }

  public static int intValue(String numStr, int defVal) {
    if (StringUtil.isEmpty(numStr)) {
      return defVal;
    }
    try {
      return Integer.parseInt(numStr);
    } catch (NumberFormatException nfe) {
      return defVal;
    }
  }
  public static int intValue(Integer numStr, int defVal) {
    if (numStr==null) {
      return defVal;
    }
    try {
      return intValue(numStr);
    } catch (NumberFormatException nfe) {
      return defVal;
    }
  }
  public static Integer intValue(Object numObj) {
    if (numObj==null||!isNumber(numObj.toString())) {
      return 0;
    }
    return Integer.parseInt(StringUtil.valueOf(numObj));
  }

  public static int intValue(Long num) {
    if (num == null) {
      return 0;
    }
    return num.intValue();
  }

  public static List<Long> parseLongValues(String str) {
    List<Long> result = new ArrayList<Long>();
    if (StringUtil.isEmpty(str)) {
      return result;
    }
    String[] values = str.split(SPLIT_REGEX);
    if (values == null || values.length == 0) {
      return result;
    }
    for (String value : values) {
      if (StringUtil.isEmpty(value)) {
        continue;
      }
      result.add(longValue(value, 0L));
    }
    return result;
  }

  public static Double[] parseDoubleValueArray(String str) {
    if (StringUtil.isEmpty(str)) {
      return null;
    }
    String[] values = str.split(SPLIT_REGEX);
    if (values == null || values.length == 0) {
      return null;
    }
    List<Double> list = new ArrayList<Double>();
    for (String value : values) {
      if (StringUtil.isEmpty(value)) {
        continue;
      }
      list.add(Double.valueOf(value));
    }
    return list.toArray(new Double[list.size()]);
  }

  public static Long[] parseLongValueArray(String str) {
    List<Long> list = parseLongValues(str);
    return list.toArray(new Long[list.size()]);
  }

  //string split 并且去重 并且按照str顺序
  public static Long[] parseLongValuesToArray(String str, String regex) {
    if (StringUtils.isBlank(str)) {
      return new Long[0];
    }
    String[] values = str.split(regex);
    Set<Long> result = new HashSet<Long>();
    if (ArrayUtil.isEmpty(values)) {
      return new Long[0];
    }
    for (String value : values) {
      if (StringUtil.isEmpty(value)) {
        continue;
      }
      Long resultValue = longValue(value);
      if (resultValue != null) {
        result.add(resultValue);
      }
    }
    Long[] returnVal = new Long[values.length];
    Set<Long> checkVal = new HashSet<Long>();
    for (String temp : values) {
      for (Long val : result) {
        if (temp.equals(String.valueOf(val))) {
          int checkValLength = checkVal.size();
          checkVal.add(val);
          if (checkValLength < checkVal.size()) {
            returnVal[checkVal.size() - 1] = val;
            break;
          }
        }
      }
    }
    return returnVal;
  }

  //string split 并且去重 并且按照str顺序
  public static Set<Long> parseLongValuesToSet(String str, String regex) {
    if (StringUtils.isBlank(str)) {
      return new HashSet<Long>();
    }
    String[] values = str.split(regex);
    Set<Long> result = new HashSet<Long>();
    if (ArrayUtil.isEmpty(values)) {
      return new HashSet<Long>();
    }
    for (String value : values) {
      if (StringUtil.isEmpty(value)) {
        continue;
      }
      Long resultValue = longValue(value);
      if (resultValue != null) {
        result.add(resultValue);
      }
    }
    return result;
  }

  /**
   * 科学计数法转成十进制数字串
   *
   * @param value
   * @return
   */
  public static String scientificToDecimal(double value) {
    DecimalFormat decimalFormat = new DecimalFormat("#");
    return decimalFormat.format(value);
  }

  /**
   * 判断字符串能否转为数值
   *
   * @param value
   * @return
   */
  public static boolean isNumber(String value) {
    if (StringUtil.isEmpty(value)) {
      return false;
    }
    try {
      Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
  /**
   * 判断字符串能否转为Long
   *
   * @param value
   * @return
   */
  public static boolean isLongNumber(String value) {
    if (StringUtil.isEmpty(value)) {
      return false;
    }
    try {
      Long.parseLong(value);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
  public static boolean isPositiveNumber(String value) {
    if (StringUtil.isEmpty(value)) {
      return false;
    }
    try {
      return Double.parseDouble(value) >= 0l;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * 计算从字节到兆的转换
   *
   * @param byteNumber
   * @return
   */
  public static double byteToMillion(int byteNumber) {
    return byteNumber / 1048576;
  }

  public static boolean isEqualNegativeOne(Integer num) {
    if (Integer.valueOf(-1).equals(num)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 四舍五入 保留小数
   *
   * @param value 需要保留小数的double值
   * @param num   需要保留的位数
   * @return
   */
  public static double round(Double value, int num) {
    if(value==null){
      return 0d;
    }
    BigDecimal bigDecimal = new BigDecimal(String.valueOf(value));
    value = bigDecimal.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
    return value;
  }

  public static int roundInt(Object value) {
    if(value==null||!isNumber(value.toString())){
      return 0;
    }

    BigDecimal bigDecimal = new BigDecimal(Double.parseDouble(value.toString()));
    return bigDecimal.setScale(NumberUtil.MONEY_PRECISION, BigDecimal.ROUND_HALF_UP).intValue();
  }

  /**
   * double 转换成long
   * @param value
   * @return
   */
  public static Long roundLong(Object value) {
    if(value==null||!isNumber(value.toString())){
      return 0l;
    }

    BigDecimal bigDecimal = new BigDecimal(Double.parseDouble(value.toString()));
    return bigDecimal.setScale(NumberUtil.MONEY_PRECISION, BigDecimal.ROUND_HALF_UP).longValue();
  }

  public static double round(Object value) {
    if(value==null||!isNumber(value.toString())){
      return 0d;
    }

    BigDecimal bigDecimal = new BigDecimal(Double.parseDouble(value.toString()));
    return bigDecimal.setScale(NumberUtil.MONEY_PRECISION, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  /**
   * @param value 要保留小数位数的double值
   * @param num   要保留的小数位数
   * @return String类型显示
   */
  public static String roundToString(double value, int num) {
    BigDecimal bd = new BigDecimal(value);
    BigDecimal bd1 = new BigDecimal(1);
    return bd.divide(bd1, num, BigDecimal.ROUND_HALF_UP).toString();
  }

  /**
   * 四舍五入 保留小数
   *
   * @param value 需要保留小数的double值
   * @param num   需要保留的位数
   * @return
   */
  public static double toReserve(Double value, int num) {
    if (value == null) {
      value = 0.0;
    }
    BigDecimal bigDecimal = new BigDecimal(value);
    value = bigDecimal.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
    return value;
  }

  public static long longValue(Long value) {
    if (value == null) {
      return 0;
    }
    return value.longValue();
  }

  public static boolean compareDouble(Double a, Double b) {
    if (null == a && null == b) {
      return true;
    }

    if (null != a && null != b) {
      double ab = Math.abs(a - b);
      if (ab <= 0.0001) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }

  }

  public static boolean isEqualOrGreater(Double a, Double b) {
    if (null == a && null == b) {
      return true;
    }
    a = doubleVal(a);
    b = doubleVal(b);
    double ab = a - b;
    if (ab > -0.0001) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isGreater(Double a, Double b) {
    if (null == a && null == b) {
      return false;
    }
    a = doubleVal(a);
    b = doubleVal(b);
    double ab = a - b;
    if (ab > 0.0001) {
      return true;
    } else {
      return false;
    }
  }

  public static Integer toInteger(String value) {
    if (StringUtils.isBlank(value)) return null;
    else return Integer.valueOf(value);
  }

  public static Long toLong(String value) {
    if (StringUtils.isBlank(value) || !NumberUtil.isNumber(value)) return null;
    else return Long.valueOf(value);
  }

  public static boolean hasEmptyVal(List<Long> nums) {
    if (nums == null || nums.size() == 0) return true;
    for (Long num : nums) {
      if (num == null) {
        return true;
      }
    }
    return false;
  }


  public static boolean compare(Long oldVal, Long newVal) {
    if (oldVal == null && newVal == null) {
      return true;
    }
    if (oldVal == null && newVal != null) {
      return false;
    }
    if (oldVal != null && newVal == null) {
      return false;
    }
    return oldVal.equals(newVal);
  }

  /**
   * 四舍五入 保留小数
   *
   * @param value 需要保留小数的double值 默认保留一位小数
   * @return
   */
  public static double toReserve(Double value) {
    if (value == null) {
      value = 0.0;
    }
    BigDecimal bigDecimal = new BigDecimal(value);
    value = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    return value;
  }

  /**
   * 在base中挑出other中也有的，顺序以base一致
   *
   * @param base
   * @param other
   * @return
   */
  public static Long[] getSameIds(Long[] base, Long[] other) {
    if (ArrayUtil.isEmpty(base) || ArrayUtil.isEmpty(other)) {
      return new Long[0];
    }
    List<Long> returnList = new ArrayList<Long>();
    for (Long tempBase : base) {
      for (Long tempOther : other) {
        if (tempBase != null && tempBase.equals(tempOther)) {
          returnList.add(tempBase);
          break;
        }
      }
    }
    return returnList.toArray(new Long[returnList.size()]);
  }

  private static Random random = null;

   private static Random getRandomInstance() {

     if (random == null) {
       random = new Random(new Date().getTime());
     }
     return random;
   }

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

   public static String getFixedLengthChinese(int length) {
     String str = "";
     for (int i = length; i > 0; i--) {
       str = str + getChinese();
     }
     return str;
   }

   public static String getRandomLengthChiness(int length) {
     String str = "";
     length = new Random().nextInt(length + 1);
       for (int i = 0; i < length; i++) {
         str = str + getChinese();
       }
     return str;
   }

   //随机字符数字，字母混合
   public static String getCharacterAndNumber(int length)
 {
     String val = "";

     Random random = new Random();
     for(int i = 0; i < length; i++)
     {
         String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

         if("char".equalsIgnoreCase(charOrNum)) // 字符串
         {
             int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
             val += (char) (choice + random.nextInt(26));
         }
         else if("num".equalsIgnoreCase(charOrNum)) // 数字
         {
             val += String.valueOf(random.nextInt(10));
         }
     }

     return val;
 }

   public static String getNumberStr(int length){
     String str = "";
     for(int i=0;i<length;i++){
       str += new Random(i).nextInt(10);
     }
     return str;
   }

  public static Long minus(Long subtrahend, Long minuend) {
    return (subtrahend == null ? 0L : subtrahend) - (minuend == null ? 0L : minuend);
  }

  public static Double minus(Double subtrahend, Double minuend) {
    return (subtrahend == null ? 0d : subtrahend) - (minuend == null ? 0d : minuend);
  }

    //3116.0426 ->31.267377 =31+16.0426/60
    public static String convertGPSLat(String lat) {
      if (StringUtils.isNotEmpty(lat)) {
        return roundToString(intValue(lat.substring(0, 2)) + doubleVal(lat.substring(2, lat.length())) / 60, 6);
      }
      return null;
    }

  //12043.8117 -> 120.730195 = 120+43.8117/60
  public static String convertGPSLot(String lon) {
    if (StringUtils.isNotEmpty(lon)) {
      return roundToString(intValue(lon.substring(0, 3)) + doubleVal(lon.substring(3, lon.length())) / 60, 6);
    }
    return null;
  }

  public static void  main(String[] args){
    System.out.println(NumberUtil.convertGPSLat("3203.8561"));
    System.out.println(NumberUtil.convertGPSLot("11856.5679"));
  }

  public static boolean compareSame(Long oldVal, Long newVal) {
    if (oldVal != null && newVal != null) {
      return oldVal.equals(newVal);
    } else if (oldVal == null && newVal == null) {
      return true;
    }
    return false;
  }

  public static boolean compareSame(Integer oldVal, Integer newVal) {
    if (oldVal != null && newVal != null) {
      return oldVal.equals(newVal);
    } else if (oldVal == null && newVal == null) {
      return true;
    }
    return false;
  }
}
