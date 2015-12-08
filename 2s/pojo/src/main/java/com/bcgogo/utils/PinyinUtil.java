package com.bcgogo.utils;

import com.bcgogo.product.dto.PingyinInfo;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-2-22
 * Time: 下午1:36
 * To change this template use File | Settings | File Templates.
 */
public class PinyinUtil {
  public static final Logger LOG = LoggerFactory.getLogger(PinyinUtil.class);
  private static int maxCount=5;

  public static PingyinInfo getPingyinInfo(String name){
    PingyinInfo pingyinInfo = new PingyinInfo();
    try{
      List<String> nameTokens = MMSegUtil.getTocken(name);
      StringBuffer nameFirstLetter = new StringBuffer();
      StringBuffer namePy = new StringBuffer();
      nameFirstLetter.append(converterToFirstSpells(name));
      namePy.append(converterToPingyins(name));
      for (int i = 0; i < nameTokens.size(); i++) {
        if (i > 0 && nameTokens.get(i) != null && nameTokens.get(i).length() > 1) {
          StringBuffer combinedToken = new StringBuffer();
          for (int j = i; j < nameTokens.size(); j++) {
            combinedToken.append(nameTokens.get(j));
          }
          nameFirstLetter.append(" ").append(converterToFirstSpells(combinedToken.toString()));
          namePy.append(" ").append(converterToPingyins(combinedToken.toString()));
        }
      }

      //只记录起始字符是汉字的首字母
      pingyinInfo.firstLetter = converterToFirstSpell(name.substring(0, 1));//
      pingyinInfo.firstLetters = nameFirstLetter.toString();
      pingyinInfo.pingyin = namePy.toString();
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
      LOG.error("pinyin error String:"+name);
    }
    return pingyinInfo;
  }

  //汉字转拼音首字母
  public static String converterToFirstSpell(String str) {
    if (StringUtil.isEmpty(str)) return "";
    String pinyin = "";
    char[] chars = str.toCharArray();
    String[] strings = null;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] > 128) {
        try {
          strings = toMyHanyuPinyinStringArray(chars[i]);
          if (strings != null && strings.length > 0
              && strings[0] != null && strings[0].length() > 0) {
            pinyin += strings[0].charAt(0);
          }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
          LOG.error(e.getMessage(), e);
        }
      } else {
        pinyin += Character.toLowerCase(chars[i]);
      }
    }
    return pinyin;
  }

  //汉字转拼音
  public static String converterToPingyin(String string) {
    StringBuffer pinyin = new StringBuffer();
    char[] chars = string.toCharArray();
    String[] strings = null;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] > 128) {
        try {
          strings = toMyHanyuPinyinStringArray(chars[i]);
          if (strings != null && strings.length > 0
              && strings[0] != null && strings[0].length() > 0) {
            pinyin.append(strings[0]);
          }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
          LOG.debug(e.getMessage(), e);
        }
      }
    }
    return pinyin.toString();
  }

  //汉字转拼音首字母 所有多音字组合 空格分隔
  public static String converterToFirstSpells(String str) {
    if (StringUtil.isEmpty(str)) return "";
    int count =0;
    List<String> temp = new ArrayList<String>();
    char[] chars = str.toCharArray();
    String[] strings = null;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] > 128) {
        try {
          strings = toMyHanyuPinyinStringArray(chars[i]);
          if (!ArrayUtils.isEmpty(strings)) {
            if(strings.length>1){
              count++;
            }
            for(int j =0;j<strings.length;j++){
              strings[j] = String.valueOf(strings[j].charAt(0));
            }
            if(count<=maxCount){
              generatePinyins(temp, strings);
            }else{
              generatePinyins(temp, new String[]{strings[0]});
            }

          }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
          LOG.error(e.getMessage(), e);
        }
      } else {
        appendEnglishString(temp, chars[i]);
      }
    }
    Set<String> result = new HashSet<String>(temp);
    return StringUtil.arrayToStr(" ",result.toArray(new String[result.size()]));
  }

  //汉字转拼音 所有多音字组合 空格分隔
  public static String converterToPingyins(String string) {
    if (StringUtil.isEmpty(string)) return "";
    List<String> temp = new ArrayList<String>();
    int count =0;
    char[] chars = string.toCharArray();

    String[] strings = null;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] > 128) {
        try {
          strings = toMyHanyuPinyinStringArray(chars[i]);
          if (!ArrayUtils.isEmpty(strings)) {
            if(strings.length>1){
              count++;
            }
            if(count<=maxCount){
              generatePinyins(temp, strings);
            }else{
              generatePinyins(temp, new String[]{strings[0]});
            }
          }

        } catch (BadHanyuPinyinOutputFormatCombination e) {
          LOG.debug(e.getMessage(), e);
        }
      } else {
        appendEnglishString(temp,chars[i]);
      }
    }
    Set<String> result = new HashSet<String>(temp);
    return StringUtil.arrayToStr(" ", result.toArray(new String[result.size()]));
  }


  private static void appendEnglishString(List<String> result, char enChar) {
    List<String> temp = new ArrayList<String>();
    if (CollectionUtils.isNotEmpty(result)) {
      for (String stringPinyin : result) {
        temp.add(stringPinyin+Character.toLowerCase(enChar));
      }
    } else {
      temp.add(String.valueOf(Character.toLowerCase(enChar)));
    }
    result.clear();
    result.addAll(temp);
  }

  private static void generatePinyins(List<String> result, String[] pinyins) {
    List<String> temp = new ArrayList<String>();
    if (CollectionUtils.isNotEmpty(result)) {
      for (String stringPinyin : result) {
        for (String charPinyin : pinyins) {
          temp.add(stringPinyin + charPinyin);
        }
      }
    } else {
      if (!ArrayUtils.isEmpty(pinyins)) {
        CollectionUtils.addAll(temp, pinyins);
      }
    }
    result.clear();
    result.addAll(temp);
  }

  //取字符串首字母
  public static String getFirstLetter(String str) {
    if (str == null || "".equals(str)) {
      return null;
    } else {
      if (StringUtil.isEmpty(converterToFirstSpell(str))) {
        return null;
      } else {
        return converterToFirstSpell(str).charAt(0) + "";
      }
    }
  }

  //检查字符串的第一个字符是否是以中文或字母开头
  public static boolean checkChs(String str) {
    Pattern pattern = Pattern.compile("[\u4E00-\u9FA5[a-z][A-Z]]");
    if (getFirstChar(str) == null) return false;
    Matcher matcher = pattern.matcher(getFirstChar(str).toString());
    return matcher.matches();
  }

  //读取第一个字符
  public static Character getFirstChar(String str) {
    if (org.apache.commons.lang.StringUtils.isEmpty(str)) return null;
    try {
      return str.charAt(0);
    } catch (Exception e) {
      LOG.error(str + " get first char error!", e);
      return null;
    }
  }

  public static boolean isChinese(char c) {
    return c >= 0x0391 && c <= 0xFFE5;
  }

  private static String[] toMyHanyuPinyinStringArray(char c) throws BadHanyuPinyinOutputFormatCombination {
    HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
    hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    return PinyinHelper.toHanyuPinyinStringArray(c, hanyuPinyinOutputFormat);
  }
}
