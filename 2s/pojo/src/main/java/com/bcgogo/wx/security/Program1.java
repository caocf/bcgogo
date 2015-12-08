package com.bcgogo.wx.security;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class Program1 {

	public static void main(String[] args) throws Exception {

		//
		// 第三方回复公众平台
		//

		// 需要加密的明文
		String encodingAesKey = "XoqBFc9Iz6adq5ItLpZOOq60SR6A0feBGxybCUVc7eX";
		String token = "1b51f05aac9a79170110df3f4b3510cc";
		String appId = "wxbb680e8d91db399e";
		String replyMsg = "<xml><ToUserName><![CDATA[oia2TjjewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>";

		WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
		String mingwen = pc.encryptMsg(replyMsg);

        mingwen="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "\n" +
          "<xml>\n" +
          "  <Encrypt><![CDATA[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]]></Encrypt>\n" +
          "  <MsgSignature><![CDATA[5c914730e51cdad4e9c97043b2e3b0e0966ffb24]]></MsgSignature>\n" +
          "  <TimeStamp>1418626167532</TimeStamp>\n" +
          "  <Nonce><![CDATA[wqjrR5gqIYZ4jAuW]]></Nonce>\n" +
          "</xml>";
      System.out.println("加密后: " + mingwen);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		StringReader sr = new StringReader(mingwen);
		InputSource is = new InputSource(sr);
		Document document = db.parse(is);

		Element root = document.getDocumentElement();
		NodeList nodelist1 = root.getElementsByTagName("Encrypt");
		NodeList nodelist2 = root.getElementsByTagName("MsgSignature");
		NodeList nodelist3 = root.getElementsByTagName("TimeStamp");
		NodeList nodelist4 = root.getElementsByTagName("Nonce");

		String encrypt = nodelist1.item(0).getTextContent();
		String msgSignature = nodelist2.item(0).getTextContent();
		String timeStamp = nodelist3.item(0).getTextContent();
		String nonce = nodelist4.item(0).getTextContent();

		// 第三方收到公众号平台发送的消息
		String result2 = pc.decryptMsg(msgSignature, timeStamp, nonce, encrypt);
		System.out.println("解密后明文: \n" + result2);
		
		//pc.verifyUrl(null, null, null, null);
	}
}
