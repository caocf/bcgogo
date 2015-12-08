package com.bcgogo.notification.client.yimei;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.notification.client.SmsClient;
import com.bcgogo.notification.client.SmsParam;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.XMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * User: ZhangJuntao
 * Date: 13-5-7
 * Time: 下午2:10
 */
public class YimeiHttpSmsClient implements SmsClient {
  private static final Logger LOG = LoggerFactory.getLogger(YimeiHttpSmsClient.class);

  @Override
  public String sendSMS(SmsParam smsSendParam) throws SmsException {
    YimeiSmsSendParam param = (YimeiSmsSendParam) smsSendParam;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String url;
    if (SmsChannel.INDUSTRY == param.getSmsChannel() && ConfigUtils.isSmsIndustryTagOn()) {
      param.setCdkey(configService.getConfig("YI_MEI_INDUSTRY_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_INDUSTRY_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_INDUSTRY_SEND_SMS_URL", ShopConstant.BC_SHOP_ID);
    } else {
      param.setCdkey(configService.getConfig("YI_MEI_MARKETING_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_MARKETING_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_MARKETING_SEND_SMS_URL", ShopConstant.BC_SHOP_ID);
      param.setSmsChannel(SmsChannel.MARKETING);
    }
    String postData = "";
    postData += "cdkey=" + param.getCdkey();
    postData += "&password=" + param.getPassword();
    try {
      postData += "&message=" + java.net.URLEncoder.encode(param.getMessage(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOG.error(e.getMessage(), e);
    }
    postData += "&phone=" + param.getPhone();
    postData += "&seqid=" + param.getSeqid();
    postData += "&smspriority=" + param.getSmspriority();
    return post(url, postData, 0);
  }


  @Override
  public void register(SmsParam yimeiSmsParam) throws Exception {
    YimeiSmsParam param = (YimeiSmsParam) yimeiSmsParam;
    String url;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (SmsChannel.INDUSTRY == param.getSmsChannel()) {
      param.setCdkey(configService.getConfig("YI_MEI_INDUSTRY_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_INDUSTRY_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_INDUSTRY_REGIST_URL", ShopConstant.BC_SHOP_ID);
    } else {
      param.setCdkey(configService.getConfig("YI_MEI_MARKETING_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_MARKETING_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_MARKETING_REGIST_URL", ShopConstant.BC_SHOP_ID);
    }
    String postData = "";
    postData += "cdkey=" + param.getCdkey();
    postData += "&password=" + param.getPassword();
    post(url, postData, 0);
  }

  @Override
  public String queryBalance(SmsParam yimeiSmsParam) throws Exception {
    YimeiSmsParam param = (YimeiSmsParam) yimeiSmsParam;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String url;
    if (SmsChannel.INDUSTRY == param.getSmsChannel()) {
      param.setCdkey(configService.getConfig("YI_MEI_INDUSTRY_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_INDUSTRY_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_INDUSTRY_QUERY_BALANCE_URL", ShopConstant.BC_SHOP_ID);
    } else {
      param.setCdkey(configService.getConfig("YI_MEI_MARKETING_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_MARKETING_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_MARKETING_QUERY_BALANCE_URL", ShopConstant.BC_SHOP_ID);
    }
    String postData = "";
    postData += "cdkey=" + param.getCdkey();
    postData += "&password=" + param.getPassword();
    return XMLParser.getRootElement(post(url, postData, 0), "message");
  }

  @Override
  public void logout(SmsParam yimeiSmsParam) throws Exception {
    YimeiSmsParam param = (YimeiSmsParam) yimeiSmsParam;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String url;
    if (SmsChannel.INDUSTRY == param.getSmsChannel()) {
      param.setCdkey(configService.getConfig("YI_MEI_INDUSTRY_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_INDUSTRY_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_INDUSTRY_LOGOUT_URL", ShopConstant.BC_SHOP_ID);
    } else {
      param.setCdkey(configService.getConfig("YI_MEI_MARKETING_SERIAL_NUMBER", ShopConstant.BC_SHOP_ID));
      param.setPassword(configService.getConfig("YI_MEI_MARKETING_PASSWORD", ShopConstant.BC_SHOP_ID));
      url = configService.getConfig("YI_MEI_MARKETING_LOGOUT_URL", ShopConstant.BC_SHOP_ID);
    }
    String postData = "";
    postData += "cdkey=" + param.getCdkey();
    postData += "&password=" + param.getPassword();
    post(url, postData, 0);
  }

  private String post(String url, String content, Integer count) throws SmsException {
    HttpURLConnection httpURLConnection = null;
    OutputStream out = null;
    count++;
    try {
      if (count >= 10) {
        throw new SmsException("UnknownHostException sms post count is larger than 10!");
      }
      long start = System.currentTimeMillis();
      httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setDoOutput(true);
      out = httpURLConnection.getOutputStream();
      out.write(content.getBytes("UTF-8"));
      out.flush();
      BufferedReader bufferedReader = null;
      bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
      StringBuilder stringBuffer = new StringBuilder();
      int ch;
      while ((ch = bufferedReader.read()) > -1) {
        stringBuffer.append((char) ch);
      }
      bufferedReader.close();
      LOG.info("Yimei http post costs time {}ms.", System.currentTimeMillis() - start);
      return stringBuffer.toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      if (e instanceof UnknownHostException) {
        close(out);
        try {
          Thread.sleep(5000l);
          LOG.warn("sleep 5000ms");
        } catch (Exception slp) {
          LOG.error(slp.getMessage(), slp);
        }
        return post(url, content, count);
      } else {
        throw new SmsException(e);
      }
    } finally {
      close(out);
    }
  }

  private void close(OutputStream out) {
    if (out != null) {
      try {
        out.close();
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
      }
    }
  }

}
