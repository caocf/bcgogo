package com.bcgogo.common;

import com.bcgogo.utils.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-11
 * Time: 上午11:32
 */
public class RandomUtilsTest {
  @Test
  public void testRandomAlphabeticList() {
    List<String> stringList = RandomUtils.randomAlphabeticList(10000);
    Assert.assertEquals(10000, stringList.size());
  }

  @Test
  public void testRandom6() {
    char[] a = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    int max = (23 * 22 * 21 * 20 * 19 * 18) / 720, i = max;
    Set<String> stringSet = new HashSet<String>();
    while (i-- > 0) {
      stringSet.add(RandomUtils.random(6, a));
    }
    System.out.println(max);
    System.out.println(stringSet.size());
  }
}
