package com.bcgogo.schedule.service;

import com.bcgogo.AbstractTest;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-19
 * Time: 上午10:58
 * To change this template use File | Settings | File Templates.
 */


public class XmlTest extends AbstractTest {

  @Test
  public void string2Document() throws Exception {
    String result = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        + "<persons>"
        + "<person id=\"001\">"
        + "<name>张三</name>"
        + "<age>20</age>"
        + "</person>"
        + "<person id=\"002\">"
        + "<name>李四</name>"
        + "<age>30</age>"
        + "</person>"
        + "</persons>";
    StringReader reader = new StringReader(result);
    InputSource source = new InputSource(reader);
    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document doc = documentBuilder.parse(source);

    String str = ">-8fdsfdsfdsfdsfdd</";
    String s1 = str.substring(1, str.length() - 2);
    System.out.print(s1);
  }

}
