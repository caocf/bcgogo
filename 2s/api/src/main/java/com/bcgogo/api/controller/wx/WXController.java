package com.bcgogo.api.controller.wx;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.IWXHandler;
import com.bcgogo.user.service.wx.impl.WXAccountService;
import com.bcgogo.user.service.wx.WXHandlerFactory;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.security.WXBizMsgCrypt;
import com.bcgogo.wx.user.WXAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-13
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/wx")
public class WXController {
  private static final Logger LOG = LoggerFactory.getLogger(WXController.class);

  /**
   * 服务器端 handler
   *
   * @param request
   * @param response
   */
  @RequestMapping(method = RequestMethod.POST)
  public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
    PrintWriter out = null;
    try {
      InputStream inputStream = request.getInputStream();
      StringWriter writer = new StringWriter();
      IOUtil.copy(inputStream, writer, "UTF-8");
      String reqContent = writer.toString();
      LOG.info("wx:receive req,content is \n" + reqContent);
      //根据配置判断是否转发请求
      if (ifDispatchPublicNo(request, response, out, reqContent)) return;
      //handle request
      WXRequestParam param = decryptMsg(request, reqContent);
      IWXHandler handler = WXHandlerFactory.getHandler(param);
      String respContent = handler.doHandle(param);
      respContent = encryptMsg(param.getPublicNo(), respContent, request.getParameter("encrypt_type"));
      LOG.info("wx:handle complete ,and resp content==> {}", respContent);
      out = response.getWriter();
      out.print(respContent);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      IOUtil.closeQuietly(out);
    }
  }

  /**
   * 验证消息体签名及解密消息
   *
   * @param request
   * @param reqContent
   * @return
   * @throws Exception
   */
  private WXRequestParam decryptMsg(HttpServletRequest request, String reqContent) throws Exception {
    Map<String, String> paramMap = XMLParser.parseXml(reqContent);
    //判断aes加密（暂时只有raw和aes两种值)
    if ("aes".equals(request.getParameter("encrypt_type"))) {
      reqContent = WXHelper.decryptMsg(paramMap.get("ToUserName"), paramMap.get("Encrypt"), request.getParameter("msg_signature"),
        request.getParameter("timestamp"), request.getParameter("nonce"));
      paramMap = XMLParser.parseXml(reqContent);
    }
    WXRequestParam param = new WXRequestParam();
    param.setReqContent(paramMap);
    return param;
  }

  private static String encryptMsg(String publicNo, String msg, String encrypt_type) throws Exception {
    if (!"aes".equals(encrypt_type)) {
      return msg;
    }
    if (StringUtil.isEmpty(publicNo) || StringUtil.isEmpty(msg)) {
      return msg;
    }
    WXAccountDTO accountDTO = ServiceManager.getService(WXAccountService.class).getDecryptedWXAccountByPublicNo(publicNo);
    WXBizMsgCrypt pc = new WXBizMsgCrypt(accountDTO);
    return pc.encryptMsg(msg);
  }

  /**
   * 根据配置判断是否转发请求
   *
   * @param request
   * @param response
   * @param out
   * @param reqContent
   * @return
   * @throws Exception
   */
  private boolean ifDispatchPublicNo(HttpServletRequest request, HttpServletResponse response, PrintWriter out, String reqContent) throws Exception {
    Map<String, String> paramMap = XMLParser.parseXml(reqContent);
    String publicNo = paramMap.get("ToUserName");
    if (StringUtil.isEmpty(publicNo)) return false;
    String dispatchKey = WXConstant.DISPATCH_KEY_PREFIX + publicNo;
    String dispatch_des_url = ServiceManager.getService(IConfigService.class).getConfig(dispatchKey, ShopConstant.BC_SHOP_ID);
    if (StringUtil.isEmpty(dispatch_des_url)) return false;
    //do dispatch request
    if ("aes".equals(request.getParameter("encrypt_type"))) {
      String msg_signature = request.getParameter("msg_signature");
      String timestamp = request.getParameter("timestamp");
      String nonce = request.getParameter("nonce");
      dispatch_des_url += "?encrypt_type=aes&msg_signature=" + msg_signature + "&timestamp=" + timestamp + "&nonce=" + nonce;
    }
    LOG.info("wx:dispatch req,dispatched publicNo is {},des url is {}", publicNo, dispatch_des_url);
    out = response.getWriter();
    out.print(HttpUtils.postData(dispatch_des_url, reqContent));
    return true;
  }

  @RequestMapping(method = RequestMethod.GET)
  public void checkSignature(HttpServletRequest request, HttpServletResponse response) throws Exception {
    LOG.info("wx:receive checkSignature request");
    PrintWriter out = null;
    try {
      //微信加密签名
      String signature = request.getParameter("signature");
      //时间戳
      String timestamp = request.getParameter("timestamp");
      //随机数
      String nonce = request.getParameter("nonce");
      //随机字符串
      String echostr = request.getParameter("echostr");
      if (!WXHelper.checkSignature(signature, timestamp, nonce)) {
        LOG.warn("wx:check signature failed");
        return;
      }
      out = response.getWriter();
      out.print(echostr);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      if (out != null) {
        out.flush();
        out.close();
      }
    }
  }


}
