package com.bcgogo.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-25
 * Time: 下午12:38
 */
public class XMLParserTest {
  @Test
  public void getRootElementTest() {
    String str ="<note>" +
                  "<to>" +
                      "<name>George</name>" +
                      "<address>SuZhou</address>" +
                  "</to>" +
                  "<from>" +
                      "<name>ZhangJuntao</name>" +
                      "<address>ChangZhou</address>" +
                  "</from>" +
                  "<heading>Reminder</heading>" +
                  "<body>Don't forget the meeting!</body>" +
                "</note>";
    long  start=System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      String result = XMLParser.getRootElement(str, "address");
      Assert.assertEquals("SuZhou", result);
    }
    long  end=System.currentTimeMillis();
    System.out.println("time  lasts  "+(end-start)+"ms");


  }
}
