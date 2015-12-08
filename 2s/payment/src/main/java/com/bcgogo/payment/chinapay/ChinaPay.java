package com.bcgogo.payment.chinapay;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.Base64;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.service.ServiceManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-6
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
public class ChinaPay {

  private static final Logger LOG = LoggerFactory.getLogger(ChinaPay.class);

  /* 银联数字签名长度 */
  public static final int CHECK_VALUE_LENGTH = 256;

  /* 返回给银联的验证成功信息 */
  public static final String CHECK_SUCCESS_MESSAGE = "eduok";

  /* 交易成功状态 */
  public static final String PAY_STAT_SUCCESS = "1001";

  //判断是否支付成功
  public static boolean isPaySuccess(String payStat) {
    return PAY_STAT_SUCCESS.equals(payStat);
  }

  /**
   * 使用chinapayDTO生成提交给银联的表单
   *
   * @param chinapayDTO
   * @return
   */
  public static String commitFormOfDefray(ChinapayDTO chinapayDTO) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    StringBuilder sb = new StringBuilder();
    sb.append("<form action='" + configService.getConfig("PayPgUrl", -1L) + "' id=\'form_chinapay\' method=\'post\'>");
    sb.append(inputHidden("MerId", chinapayDTO.getMerId() == null ? "" : chinapayDTO.getMerId()));
    sb.append(inputHidden("OrdId", chinapayDTO.getOrdId() == null ? "" : chinapayDTO.getOrdId()));
    sb.append(inputHidden("OrdAmt", chinapayDTO.getOrdAmt() == null ? "" : chinapayDTO.getOrdAmt()));
    sb.append(inputHidden("CuryId", chinapayDTO.getCuryId() == null ? "" : chinapayDTO.getCuryId()));
    sb.append(inputHidden("Version", chinapayDTO.getInterfaceVersion() == null ? "" : chinapayDTO.getInterfaceVersion()));
    sb.append(inputHidden("BgRetUrl", chinapayDTO.getBgRetUrl() == null ? "" : chinapayDTO.getBgRetUrl()));
    sb.append(inputHidden("PageRetUrl", chinapayDTO.getPageRetUrl() == null ? "" : chinapayDTO.getPageRetUrl()));
    sb.append(inputHidden("GateId", chinapayDTO.getGateId() == null ? "" : chinapayDTO.getGateId()));
    sb.append(inputHidden("OrdDesc", chinapayDTO.getOrdDesc() == null ? "" : chinapayDTO.getOrdDesc()));
    sb.append(inputHidden("ShareType", chinapayDTO.getShareType() == null ? "" : chinapayDTO.getShareType()));
    sb.append(inputHidden("ShareData", chinapayDTO.getShareData() == null ? "" : chinapayDTO.getShareData()));
    sb.append(inputHidden("Priv1", chinapayDTO.getPriv1() == null ? "" : chinapayDTO.getPriv1()));
    sb.append(inputHidden("ChkValue", chinapayDTO.getChkValue() == null ? "" : chinapayDTO.getChkValue()));
    sb.append("</form>");
    return sb.toString();
  }

  private static String inputHidden(String name, String value) {
    return "<input type=\'hidden\' name=\'" + name + "\' value=\'" + value + "\'/>";
  }


  /**
   * 将页面返回的数据放到 ChinapayDTO中
   *
   * @param req
   * @return 将request中的信息封装成ChinapayDTO对象
   */
  public static ChinapayDTO gengerateChinapayDTO(HttpServletRequest req) {
    ChinapayDTO chinapayDTO = new ChinapayDTO();
    chinapayDTO.setMerId(req.getParameter("MerId"));
    chinapayDTO.setBusiId(req.getParameter("BusiId"));
    chinapayDTO.setOrdId(req.getParameter("OrdId"));
    chinapayDTO.setOrdAmt(req.getParameter("OrdAmt"));
    chinapayDTO.setCuryId(req.getParameter("CuryId"));
    chinapayDTO.setInterfaceVersion(req.getParameter("Version"));
    chinapayDTO.setGateId(req.getParameter("GateId"));
    chinapayDTO.setParam1(req.getParameter("Param1"));
    chinapayDTO.setParam2(req.getParameter("Param2"));
    chinapayDTO.setParam3(req.getParameter("Param3"));
    chinapayDTO.setParam4(req.getParameter("Param4"));
    chinapayDTO.setParam5(req.getParameter("Param5"));
    chinapayDTO.setParam6(req.getParameter("Param6"));
    chinapayDTO.setParam7(req.getParameter("Param7"));
    chinapayDTO.setParam8(req.getParameter("Param8"));
    chinapayDTO.setParam9(req.getParameter("Param9"));
    chinapayDTO.setParam10(req.getParameter("Param10"));
    chinapayDTO.setOrdDesc(req.getParameter("OrdDesc"));
    chinapayDTO.setShareType(req.getParameter("ShareType"));
    chinapayDTO.setShareData(req.getParameter("ShareData"));
    chinapayDTO.setPriv1(req.getParameter("Priv1"));
    chinapayDTO.setCustomIp(req.getParameter("CustomIp"));
    chinapayDTO.setPayStat(req.getParameter("PayStat"));
    chinapayDTO.setPayTime(req.getParameter("PayTime"));
    chinapayDTO.setChkValue(req.getParameter("ChkValue"));
    return chinapayDTO;
  }


  /**
   * 获取待签名数据
   *
   * @param chinapayDTO
   * @return
   */
  public static String getSignData(ChinapayDTO chinapayDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append(chinapayDTO.getMerId() == null ? "" : chinapayDTO.getMerId());
    sb.append(chinapayDTO.getBusiId() == null ? "" : chinapayDTO.getBusiId());
    sb.append(chinapayDTO.getOrdId() == null ? "" : chinapayDTO.getOrdId());
    sb.append(chinapayDTO.getOrdAmt() == null ? "" : chinapayDTO.getOrdAmt());
    sb.append(chinapayDTO.getCuryId() == null ? "" : chinapayDTO.getCuryId());
    sb.append(chinapayDTO.getInterfaceVersion() == null ? "" : chinapayDTO.getInterfaceVersion());
    sb.append(chinapayDTO.getBgRetUrl() == null ? "" : chinapayDTO.getBgRetUrl());
    sb.append(chinapayDTO.getPageRetUrl() == null ? "" : chinapayDTO.getPageRetUrl());
    sb.append(chinapayDTO.getGateId() == null ? "" : chinapayDTO.getGateId());
    sb.append(chinapayDTO.getParam1() == null ? "" : chinapayDTO.getParam1());
    sb.append(chinapayDTO.getParam2() == null ? "" : chinapayDTO.getParam2());
    sb.append(chinapayDTO.getParam3() == null ? "" : chinapayDTO.getParam3());
    sb.append(chinapayDTO.getParam4() == null ? "" : chinapayDTO.getParam4());
    sb.append(chinapayDTO.getParam5() == null ? "" : chinapayDTO.getParam5());
    sb.append(chinapayDTO.getParam6() == null ? "" : chinapayDTO.getParam6());
    sb.append(chinapayDTO.getParam7() == null ? "" : chinapayDTO.getParam7());
    sb.append(chinapayDTO.getParam8() == null ? "" : chinapayDTO.getParam8());
    sb.append(chinapayDTO.getParam9() == null ? "" : chinapayDTO.getParam9());
    sb.append(chinapayDTO.getParam10() == null ? "" : chinapayDTO.getParam10());
    sb.append(chinapayDTO.getShareType() == null ? "" : chinapayDTO.getShareType());
    sb.append(chinapayDTO.getShareData() == null ? "" : chinapayDTO.getShareData());
    sb.append(chinapayDTO.getPriv1() == null ? "" : chinapayDTO.getPriv1());
    sb.append(chinapayDTO.getCustomIp() == null ? "" : chinapayDTO.getCustomIp());
    sb.append(chinapayDTO.getPayStat() == null ? "" : chinapayDTO.getPayStat());
    sb.append(chinapayDTO.getPayTime() == null ? "" : chinapayDTO.getPayTime());
    return sb.toString();
  }


  /**
   * 给字符串签名
   *
   * @param merId          商户号
   * @param privateKeyPath 私钥路径
   * @param data           待签名字符串
   * @return 签名后的字符串
   */
  public static String sign(String merId, String privateKeyPath, String data) {
    chinapay.PrivateKey key = new chinapay.PrivateKey();
    if (key.buildKey(merId, 0, privateKeyPath) == false) {
      LOG.warn("签名失败！建立私钥失败！请检查商户号：[" + merId + "]和私钥路径：[" + privateKeyPath + "]是否配置正确。");
      return null;
    }
    String data0 = "";
    try {
      data0 = new String(Base64.encode(data.getBytes("UTF-8")));
    } catch (Exception e) {
      data0 = new String(Base64.encode(data.getBytes()));
    }
    chinapay.SecureLink t = new chinapay.SecureLink(key);
    return t.Sign(data0); // Value2为签名后的字符串
  }


  /**
   * 调用CP公钥，验证CP的响应信息
   *
   * @param data          待签名数据
   * @param publicKeyPath 公钥路径
   * @param chkValue      CP响应的签名数据
   * @return 成功true，失败false
   */
  public static boolean check(String data, String publicKeyPath, String chkValue) {
    //mock
    String mock = ServiceManager.getService(IConfigService.class).getConfig("MOCK_PAYMENT", -1L);
    if (StringUtils.isNotBlank(mock) && mock.equals("on")) return true;
    if (StringUtils.isBlank(chkValue) || StringUtils.isBlank(data)) return false;
    chinapay.PrivateKey key = new chinapay.PrivateKey();
    if (!key.buildKey("999999999999999", 0, publicKeyPath)) {
      LOG.warn("验证签名失败！建立公钥失败！请检查公钥路径：[" + publicKeyPath + "]是否配置正确。");
      return false;
    }
    String data0 = new String(Base64.encode(data.getBytes()));
    chinapay.SecureLink t = new chinapay.SecureLink(key);
    return t.verifyAuthToken(data0, chkValue);
  }

  public static String getQuerySignData(ChinapayDTO chinapayDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append(chinapayDTO.getMerId() == null ? "" : chinapayDTO.getMerId());
    sb.append(chinapayDTO.getBusiId() == null ? "" : chinapayDTO.getBusiId());
    sb.append(chinapayDTO.getOrdId() == null ? "" : chinapayDTO.getOrdId());
    sb.append(chinapayDTO.getParam1() == null ? "" : chinapayDTO.getParam1());
    sb.append(chinapayDTO.getParam2() == null ? "" : chinapayDTO.getParam2());
    sb.append(chinapayDTO.getParam3() == null ? "" : chinapayDTO.getParam3());
    sb.append(chinapayDTO.getParam4() == null ? "" : chinapayDTO.getParam4());
    sb.append(chinapayDTO.getParam5() == null ? "" : chinapayDTO.getParam5());
    sb.append(chinapayDTO.getParam6() == null ? "" : chinapayDTO.getParam6());
    sb.append(chinapayDTO.getParam7() == null ? "" : chinapayDTO.getParam7());
    sb.append(chinapayDTO.getParam8() == null ? "" : chinapayDTO.getParam8());
    sb.append(chinapayDTO.getParam9() == null ? "" : chinapayDTO.getParam9());
    sb.append(chinapayDTO.getParam10() == null ? "" : chinapayDTO.getParam10());
    return sb.toString();
  }
}
