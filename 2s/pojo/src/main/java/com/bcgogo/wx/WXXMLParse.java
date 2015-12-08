package com.bcgogo.wx;

import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.message.resp.BaseMsg;
import com.bcgogo.wx.message.resp.NewsMsg;
import com.bcgogo.wx.message.resp.TextMsg;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-15
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class WXXMLParse {

  public static String getReplyMsgXML(String encrypt, String signature, String timestamp, String nonce) throws IOException {
    Document document= DocumentHelper.createDocument();
    //设置XML文档的元素
    Element rootElement=document.addElement("xml");
    Element elem1=rootElement.addElement("Encrypt");
    elem1.addCDATA(encrypt);
    Element elem2=rootElement.addElement("MsgSignature");
    elem2.addCDATA(signature);
    Element elem3=rootElement.addElement("TimeStamp");
    elem3.setText(timestamp);
    Element elem4=rootElement.addElement("Nonce");
    elem4.addCDATA(nonce);
    return writeDoc(document);
  }



  private static Document getBaseMsgDocument(BaseMsg baseMsg){
    Document document= DocumentHelper.createDocument();
    //设置XML文档的元素
    Element rootElement=document.addElement("xml");
    Element elem1=rootElement.addElement("ToUserName");
    elem1.addCDATA(baseMsg.getToUserName());
    Element elem2=rootElement.addElement("FromUserName");
    elem2.addCDATA(baseMsg.getFromUserName());
    Element elem3=rootElement.addElement("CreateTime");
    elem3.setText(StringUtil.valueOf(baseMsg.getCreateTime()));
    Element elem4=rootElement.addElement("MsgType");
    elem4.addCDATA(baseMsg.getMsgType().toString());
    return document;
  }

  public static String toTextMessageXMl(TextMsg textMsg) throws IOException {
    if(textMsg==null){
      return null;
    }
    Document document=getBaseMsgDocument(textMsg);
    Element rootElement=document.getRootElement();
    if(MsgType.text.equals(textMsg.getMsgType())){
      Element elem5=rootElement.addElement("Content");
      elem5.addCDATA(textMsg.getContent());
    }
    return  writeDoc(document);
  }

  public static String getTransferCustomMsgXml(BaseMsg baseMsg) throws IOException {
    if(baseMsg==null){
      return null;
    }
    Document document=getBaseMsgDocument(baseMsg);
    return  writeDoc(document);
  }

  public static String toNewsMessageXMl(NewsMsg message) throws IOException {
    if(message==null){
      return null;
    }
    Document document= DocumentHelper.createDocument();
    //设置XML文档的元素
    Element rootElement=document.addElement("xml");
    Element elem1=rootElement.addElement("ToUserName");
    elem1.addCDATA(message.getToUserName());
    Element elem2=rootElement.addElement("FromUserName");
    elem2.addCDATA(message.getFromUserName());
    Element elem3=rootElement.addElement("CreateTime");
    elem3.setText(StringUtil.valueOf(message.getCreateTime()));
    Element elem4=rootElement.addElement("MsgType");
    elem4.addCDATA(message.getMsgType().toString());
    Element elem5=rootElement.addElement("ArticleCount");
    elem5.setText(message.getArticleCount());
    Element elem6=rootElement.addElement("Articles");
    for(WXArticleTemplateDTO article:message.getArticles()){
      Element elem_item=elem6.addElement("item");
      Element elem_title=elem_item.addElement("Title");
      elem_title.addCDATA(article.getTitle());
      Element elem_description=elem_item.addElement("Description");
      elem_description.addCDATA(article.getDescription());
      Element elem_picUrl=elem_item.addElement("PicUrl");
      elem_picUrl.addCDATA(article.getPicUrl());
      Element elem_url=elem_item.addElement("Url");
      elem_url.addCDATA(article.getUrl());
    }
    return writeDoc(document);
  }

  //生成字符串内容
  private static String writeDoc(Document document) throws IOException {
    StringWriter out = new StringWriter(1024);
    OutputFormat format = OutputFormat.createPrettyPrint();
    format.setEncoding("UTF-8");
//    format.setIndent(" ");
    XMLWriter  xmlWriter = new XMLWriter(out,format);
    xmlWriter.write(document);
    return out.toString();
  }


}
