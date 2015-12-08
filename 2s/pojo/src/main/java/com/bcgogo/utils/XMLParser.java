package com.bcgogo.utils;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-4-1
 * Time: 下午2:47
 * 解析xml
 */
public class XMLParser {
  public static final Logger LOG = LoggerFactory.getLogger(XMLParser.class);
  private static int count = 1;

  public static Map<String, String> parseXml(String context) throws Exception {
    Map<String, String> eMap = new HashMap<String, String>();
    if (StringUtil.isEmpty(context)) {
      return eMap;
    }
    Document doc = DocumentHelper.parseText(context.replace("\n", "").replace("\r", ""));
    Element root_elem = doc.getRootElement();
    List elements = root_elem.elements();
    if (elements.size() == 0) {
      return eMap;
    }
    Iterator it = elements.iterator();
    while (it.hasNext()) {
      Element elem = (Element) it.next();
      eMap.put(elem.getName(), elem.getText());

    }
    return eMap;
  }

  public static String objectToXml(Object obj) {
    JsonUtil.objectToJson(obj);
    return null;
  }


  public static String getRootElement(String context, String name) {
    if (context == null) {
      return "";
    }
    try {
      Document doc = DocumentHelper.parseText(context.replace("\n", "").replace("\r", ""));
      Element elem = doc.getRootElement();

      return getElementList(elem, name, "");
    } catch (DocumentException e) {
      LOG.warn(e.getMessage(), e);
    }
    return "";
  }

  /**
   * 递归遍历方法
   *
   * @param element
   */
  private static String getElementList(Element element, String name, String temp) {
    List elements = element.elements();
//    LOG.info("call function times:{}", count++);
    if (elements.size() == 0) {
      return temp;
    } else {
      Iterator it = elements.iterator();
      if (it.hasNext()) {
        do {
          Element elem = (Element) it.next();
          if (elem.getName().equals(name)) {
//            LOG.info("find it [{}:{}]", name, elem.getText());
            return elem.getText();
          } else {
            //递归遍历
            temp = getElementList(elem, name, temp);
          }
        } while (it.hasNext() && StringUtils.isBlank(temp));
      }
    }
    return temp;
  }
}
