package com.bcgogo.notification.client.lianyu;

import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.XMLParser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * User: zhangjie
 * Date: 14-12-02
 * Time: 下午
 */
public class LianYuHttpSmsClient implements LianYuSmsClient  {
  private static final Logger LOG = LoggerFactory.getLogger(LianYuHttpSmsClient.class);

  //短信发送
  @Override
  public String sendSMS(LianYuSmsParam smsSendParam) throws SmsException {
    String result = lianYuInterface(SmsConstant.SmsLianYuConstant.smsSend, smsSendParam);
    return result;
  }

  //余额查询
  @Override
  public String balanceInquery(LianYuSmsParam smsSendParam) throws SmsException {
    String result = lianYuInterface(SmsConstant.SmsLianYuConstant.balanceInquery, smsSendParam);
    return result;
  }

  //检测屏蔽词
  @Override
  public  String detectionStopWords(LianYuSmsParam smsSendParam)throws SmsException{
    String result = lianYuInterface(SmsConstant.SmsLianYuConstant.detectionStopWords,smsSendParam);
    return  result;
  }

  //获取状态报告
  @Override
  public  String returnStatus(LianYuSmsParam smsSendParam)throws SmsException{
    String result = lianYuInterface(SmsConstant.SmsLianYuConstant.returnStatus,smsSendParam);
    return  result;
  }



 //1. smsSend            短信发送接口
 //2. balanceInquery     余额查询接口
 //3. detectionStopWords 检测屏蔽词接口
 //4. returnStatus       获取状态报告接口
 //5. smsReply           短信回复接口
 //name 分别为以上5种情况时分别调用不同的接口
  public static String lianYuInterface(String name, LianYuSmsParam smsSendParam) {
    String result="";
    try {
      HttpClient client = new HttpClient();
      client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
      PostMethod post = new PostMethod(name);
      NameValuePair username = new NameValuePair("username", smsSendParam.getUserName());
      NameValuePair password = new NameValuePair("password", smsSendParam.getPassword());
      NameValuePair mobile = new NameValuePair("mobile",smsSendParam.getMobile());
      NameValuePair content = new NameValuePair("content", smsSendParam.getContent());
      //测试调用状态报告时使用
      //NameValuePair report = new NameValuePair("report", "f39dac3d49fa8c15014a14232f7e652b{||}15895632263{||}0{||}2014-12-04");
      post.setRequestBody(new NameValuePair[]{username, password, mobile, content});
      client.executeMethod(post);
      result = post.getResponseBodyAsString();
      post.releaseConnection();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }


   //接口测试
  public static void main(String[] args){
    LianYuSmsParam smsSendParam = new LianYuSmsParam();
    smsSendParam.setUserName(SmsConstant.SmsLianYuConstant.userName);
    smsSendParam.setPassword(SmsConstant.SmsLianYuConstant.password);
    smsSendParam.setMobile("15895632263");
    smsSendParam.setContent("你好！NO.3");
    String result = lianYuInterface(SmsConstant.SmsLianYuConstant.balanceInquery, smsSendParam);
    String code = XMLParser.getRootElement(result, "resultcode");
    String message = XMLParser.getRootElement(result, "errordescription");
    System.out.println("接口返回结果如下：");
    System.out.println("result="+result);
    System.out.println("———————————————————————————————————————————");
    System.out.println("code="+code);
    System.out.println("message="+message);



  }
}

