package com.bcgogo.pinyin.util;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Word;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
  private static MaxWordSeg maxWordSeg = new MaxWordSeg(Dictionary.getInstance());

  public static void main(String[] args) {
    
  }

  public static boolean containHomophoneWord(String sourceWord, Map<String, String[]> homophones){
    if(StringUtils.isBlank(sourceWord)){
      return false;
    }
    if(homophones.isEmpty()){
      return false;
    }
    for(Map.Entry<String, String[]> entry : homophones.entrySet()){
      String homophone = entry.getKey();
      if(sourceWord.contains(homophone)){
        System.out.println(homophone + ":" + ArrayUtils.toString(entry.getValue(), "null"));
        return true;
      }
    }
    return false;
  }

  public static Map<String, String[]> getAllHomophoneWords(){
    int a = (int) (4 * Math.pow(16, 3) + 14 * Math.pow(16, 2)); // 汉字ASCII码最小值
    int b = (int) (9 * Math.pow(16, 3) + 15 * Math.pow(16, 2) + 10 * Math.pow(16, 1)) + 5;  // 汉字ASCII码最大值
    int j = 0;
    int countAll = 0;
    Map<String, String[]> homophones = new HashMap<String, String[]>();
    for (int i = a; i <= b; i++) {
      countAll++;
      j++;
      System.out.print((char) i); //ASCII码转换为字符（汉字）
      if (j % 30 == 0) {
        System.out.println();
        j = 0;
      }
      String[] pinyins = getPinyins((char) i);
      if(pinyins!= null && pinyins.length>1){
        homophones.put(String.valueOf((char)i), pinyins);
      }
    }
    System.out.println();
    System.out.println("汉字总数：" + countAll);
    System.out.println("多音字总数：" + homophones.size());
    return homophones;
  }

  public static String[] getPinyins(char ch){
    HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
    hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    String[] strings = null;
    if (ch > 128) {
      try {
        strings = PinyinHelper.toHanyuPinyinStringArray(ch,hanyuPinyinOutputFormat);
        if(strings== null || strings.length==0){
          return null;
        }
        Set<String> set = new HashSet<String>();
        for(String str : strings){
          set.add(str);
        }
        if(set.size()>1){
          return set.toArray(new String[set.size()]);
        }

      } catch (BadHanyuPinyinOutputFormatCombination e) {
        LOG.error(e.getMessage(), e);
      }
    }
    return null;
  }

  public static PingyinInfo getPingyinInfo(String name) throws IOException {
    List<String> nameTokens = getTocken(name);
    StringBuffer nameFirstLetter = new StringBuffer();
    StringBuffer namePy = new StringBuffer();
    nameFirstLetter.append(converterToFirstSpell(name));
    namePy.append(converterToPingyin(name));
    for (int i = 0; i < nameTokens.size(); i++) {
      if (i > 0 && nameTokens.get(i) != null && nameTokens.get(i).length() > 1) {
        StringBuffer combinedToken = new StringBuffer();
        for (int j = i; j < nameTokens.size(); j++) {
          combinedToken.append(nameTokens.get(j));
        }
        nameFirstLetter.append(" ").append(converterToFirstSpell(combinedToken.toString()));
        namePy.append(" ").append(converterToPingyin(combinedToken.toString()));
      }
    }
    PingyinInfo pingyinInfo = new PingyinInfo();
    //只记录起始字符是汉字的首字母
    pingyinInfo.firstLetter = converterToFirstSpell(name.substring(0, 1));//
    pingyinInfo.firstLetters = nameFirstLetter.toString();
    pingyinInfo.pingyin = namePy.toString();
    return pingyinInfo;
  }

  public static List<String> getTocken(String phrase) throws IOException {
    List<String> result = new ArrayList<String>();
    MMSeg mmSeg = new MMSeg(new StringReader(phrase), maxWordSeg);
    Word word = null;
    while ((word = mmSeg.next()) != null) {
      String w = word.getString();
      result.add(w);
    }
    return result;
  }

  //汉字转拼音首字母
  public static String converterToFirstSpell(String str) {
    if (StringUtils.isEmpty(str)) return "";
    String pinyin = "";
    char[] chars = str.toCharArray();
    HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
    hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    String[] strings = null;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] > 128) {
        try {
          strings = PinyinHelper.toHanyuPinyinStringArray(chars[i],hanyuPinyinOutputFormat);
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
    HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
    hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    String[] strings = null;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] > 128) {
        try {
          strings = PinyinHelper.toHanyuPinyinStringArray(chars[i], hanyuPinyinOutputFormat);
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


  //汉字转拼音 所有多音字组合
  public static Set<String> converterToPingyins(String string) {
    Set<String> result = new HashSet<String>();
    char[] chars = string.toCharArray();
    HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
    hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    String[] strings = null;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] > 128) {
        try {
          strings = PinyinHelper.toHanyuPinyinStringArray(chars[i], hanyuPinyinOutputFormat);
          if (!ArrayUtils.isEmpty(strings)) {
            generatePinyins(result, strings);
          }

        } catch (BadHanyuPinyinOutputFormatCombination e) {
          LOG.debug(e.getMessage(), e);
        }
      }
    }
    return result;
  }

  private static void generatePinyins(Set<String> result, String[] pinyins) {
    Set<String> temp = new HashSet<String>();
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
      if (StringUtils.isEmpty(converterToFirstSpell(str))) {
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
}
