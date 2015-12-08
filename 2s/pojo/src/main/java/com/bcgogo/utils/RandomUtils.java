package com.bcgogo.utils;

import org.apache.commons.lang.RandomStringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-11
 * Time: 上午11:18
 */
public class RandomUtils extends RandomStringUtils {
  private static final Random RANDOM = new Random();

  private static final char[] CHARACTER_RANGE = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

  public static List<String> randomAlphabeticList(int size) {
    Set<String> codeSet = new HashSet<String>();
    while (codeSet.size() < size) {
      codeSet.add(RandomUtils.random(6, CHARACTER_RANGE));
    }
    List<String> codeList = new ArrayList<String>();
    codeList.addAll(codeSet);
    return codeList;
  }

  public static String random(int count, char[] chars) {
    if (count == 0) {
      return "";
    } else if (count < 0) {
      throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
    } else if (ArrayUtil.isEmpty(chars)) {
      throw new IllegalArgumentException("Requested random chars is empty.");
    }
    char[] buffer = new char[count];
    int gap = chars.length;

    while (count-- != 0) {
      buffer[count] = chars[RANDOM.nextInt(gap)];
    }
    return new String(buffer).toLowerCase();
  }

}
