package com.bcgogo.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * CookieUtil Tester.
 *
 * @author zhangjuntao
 * @version 1.0
 * @since <pre>8,30, 2013</pre>
 */
public class CookieUtilTest {

  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: getCookieByName(HttpServletRequest request, String name)
   */
  @Test
  public void testGetCookieByName() throws Exception {
//TODO: Test goes here... 
  }

  /**
   * Method: getCookieMapByCookies(Cookie[] cookies)
   */
  @Test
  public void testGetCookieMapByCookies() throws Exception {
//TODO: Test goes here... 
  }

  /**
   * Method: genPermissionKey()
   * Method: getSessionIdCreatedTime(String key)
   */
  @Test
  public void testGenPermissionKey() throws Exception {
    //  System.out.println(System.currentTimeMillis());
    //  System.out.println(RandomUtils.randomAlphabetic(10));
    //  System.out.println(RandomUtils.randomAlphanumeric(10));
    //  System.out.println(RandomUtils.randomNumeric(10));
    //
    //
    //  Long time = System.currentTimeMillis();
    //  String key = RandomUtils.randomNumeric(8) + time + RandomUtils.randomAlphanumeric(8) + ".app";
    //  System.out.println(key);
    //  System.out.println(key.substring(8,8+String.valueOf(time).length()));
    //
    //  System.out.println(time);
    //  System.out.println(8+String.valueOf(time).length());
    //  System.out.println("8D80FBEA7963B6748C7313FAEC9FB7E5.app");
    String key = CookieUtil.genPermissionKey();
    Long time = CookieUtil.getSessionIdCreatedTime(key);
    Assert.isTrue(System.currentTimeMillis() - time < 100);
  }


  /**
   * Method: setCookie(HttpServletResponse response, String key, String value, int maxAge)
   */
  @Test
  public void testSetCookie() throws Exception {
//TODO: Test goes here... 
  }


} 
