package com.bcgogo.other;

import com.bcgogo.utils.XMLParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-25
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class XMLTest {
  public static final Logger LOG = LoggerFactory.getLogger(XMLTest.class);
  @Test
  public void myXMLTest() {
    String str = "c8fbbf3cbd416d8fd1940ca7c387a8b239c688c2</accessToken><domainLevel>0</domainLevel><email>test@test.com</email><userLevel>1</userLevel><userState>ST002</userState><userStateDescr>已激活</userStateDescr><userid>80000053</userid><username>test</username></loginUser></dataView><executeResult><error>1</error><errorDescr>操作成功</errorDescr><errorParamsDescr/><result>1</result></executeResult></dataRsp>";
//        String str = "<dataRsp><dataView><loginUser><accessToken>c8fbbf3cbd416d8fd1940ca7c387a8b239c688c2</accessToken><domainLevel>0</domainLevel><email>test@test.com</email><userLevel>1</userLevel><userState>ST002</userState><userStateDescr>已激活</userStateDescr><userid>80000053</userid><username>test</username></loginUser></dataView><executeResult><error>1</error><errorDescr>操作成功</errorDescr><errorParamsDescr/><result>1</result></executeResult></dataRsp>";
//        String str= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<result>\n" +
//                "<response>" + 10 + "</response><sms><phone>130982922</phone><smsID>1000000000000</smsID></sms></result>";
    str="\n" +
        "\n" +
        "\n" +
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><error>0</error><message></message></response>\n" +
        "\n";
    try {
      System.out.println("response:" + XMLParser.getRootElement(str, "error"));
      System.out.println("response:" + XMLParser.getRootElement(str, "response"));
      System.out.println("phone:" + XMLParser.getRootElement(str, "phone"));
      System.out.println("smsID:" + XMLParser.getRootElement(str, "smsID"));
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
  }
}
