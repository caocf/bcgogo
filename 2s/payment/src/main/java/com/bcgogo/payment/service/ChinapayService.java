package com.bcgogo.payment.service;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.ChinaPayConstants;
import com.bcgogo.constant.TransactionConstants;
import com.bcgogo.payment.PaymentException;
import com.bcgogo.payment.chinapay.ChinaPay;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.dto.PaymentServiceJobDTO;
import com.bcgogo.payment.dto.SequenceNoDTO;
import com.bcgogo.payment.dto.TransactionDTO;
import com.bcgogo.payment.model.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

@Component
public class ChinapayService implements IChinapayService {
  private static final Logger LOG = LoggerFactory.getLogger(ChinapayService.class);

  @Override
  public ChinapayDTO pay(Long referenceId, long amount, long payerId, String desc, String bgRetUrl, String pageRetUrl) {
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    //创建一个初始化的TransactionDTO
    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setTransactionType(TransactionConstants.TransactionType.TRANSACTION_TYPE_PAY);
    transactionDTO.setReferenceType(TransactionConstants.ReferenceType.REFERENCE_TYPE_SMS_RECHARGE);
    transactionDTO.setReferenceId(referenceId);    //smsRecharge id
    transactionDTO.setAmount(amount);
    transactionDTO.setCurrency(TransactionConstants.Currency.CURRENCY_RMB);
    transactionDTO.setPayMethod(TransactionConstants.PayMethod.PAY_METHOD_CHINAPAY);
    transactionDTO.setPayerId(payerId);  //shopId
    transactionDTO.setStatus(Status.PENDING.toString());   //pending:待定
    paymentService.createTransaction(transactionDTO);   //创建Transaction Status：待定
    //创建一个SequenceNoDTO
    SequenceNoDTO sequenceNoDTO = new SequenceNoDTO();
    sequenceNoDTO.setTransId(transactionDTO.getId());
    paymentService.createSequence(sequenceNoDTO);
    //Init Chinapay
    ChinapayDTO chinapayDTO = new ChinapayDTO();
    chinapayDTO.setMerId(configService.getConfig("MerId", -1L));//商户号
    chinapayDTO.setOrdId(sequenceNoDTO.getSequenceNo());//交易订号
    chinapayDTO.setOrdAmt(String.valueOf(transactionDTO.getAmount()));//订单金额
    chinapayDTO.setCuryId(configService.getConfig("CurId", -1L));
    chinapayDTO.setInterfaceVersion(configService.getConfig("Version", -1L));
    String url = configService.getConfig("CHINA_PAY_BACK_URL", -1L);
    chinapayDTO.setBgRetUrl(url + bgRetUrl);//后台接收地址
    chinapayDTO.setPageRetUrl(url + pageRetUrl);//前台交易地址
    chinapayDTO.setGateId(configService.getConfig("GateId", -1L));//支付网关
    chinapayDTO.setOrdDesc(desc);                                   //订单描述
    chinapayDTO.setShareType(configService.getConfig("ShareType", -1L));//分账类型
    chinapayDTO.setShareData(configService.getConfig("ShareA", -1L) + String.valueOf(transactionDTO.getAmount()) + ";");//分账数据
    String data = ChinaPay.getSignData(chinapayDTO);
    String chkValue = null;
    boolean mockFlag = true;
    try {
      String mock = configService.getConfig("MOCK_PAYMENT", -1L);
      if (StringUtils.isNotBlank(mock) && mock.equals("on")) {
        chkValue = "this is mock data from chinaPay! and value is create by pay.";
        mockFlag = false;
      } else {
        chkValue = ChinaPay.sign(chinapayDTO.getMerId(), configService.getConfig("MerPriKeyPath", -1L), data);
      }
    } catch (Exception e) {
      LOG.error("银联支付时签名失败！" + e.getMessage(), e);
      return null;
    }
    if ((StringUtil.isEmpty(chkValue) || chkValue.length() != ChinaPay.CHECK_VALUE_LENGTH) && mockFlag) {
      LOG.warn("银联支付时签名验证失败！");
      return null;
    }
    chinapayDTO.setChkValue(chkValue);
    chinapayDTO.setTransId(transactionDTO.getId());
    chinapayDTO.setChinapayType(ChinaPayConstants.ChinaPayType.CHINA_PAY_TYPE_FIRST);
    return paymentService.createChinapay(chinapayDTO);
  }

  /**
   * 后台交易接收
   *
   * @param chinapayDTO 前端通过fromForm方法，可将request中的信息封装成ChinapayDTO对象
   * @return 成功返回TransactionDTO 失败返回null
   */
  @Override
  public TransactionDTO receive(ChinapayDTO chinapayDTO) {
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    if (chinapayDTO == null) return null;

    TransactionDTO transactionDTO = null;
    String data =ChinaPay.getSignData(chinapayDTO) ;
    if (StringUtils.isBlank(chinapayDTO.getChkValue()) || StringUtils.isBlank(data)) {
       LOG.warn("后端接收银联支付返回时,银联交易数据为空！");
      return null;
    }
    if (!ChinaPay.check(data, configService.getConfig("ChinaPayPubKeyPath", -1L), chinapayDTO.getChkValue())) {
      LOG.warn("后端接收银联支付返回时银联交易订单号：[" + chinapayDTO.getOrdId() + "]验证签名失败1！");
    } else if (!ChinaPay.isPaySuccess(chinapayDTO.getPayStat())) {
      LOG.warn("后端接收银联支付返回时银联交易订单号：[" + chinapayDTO.getOrdId() + "]交易失败1！[PayStat=" + chinapayDTO.getPayStat() + "]");
    } else {
      LOG.warn("后端接收银联支付返回时银联交易订单号：[" + chinapayDTO.getOrdId() + "]交易成功1！");
      //根据银联交易订单号获取交易transId
      List<SequenceNoDTO> listSequenceNoDTO = paymentService.getSequenceNoByNo(chinapayDTO.getOrdId());
      if (listSequenceNoDTO == null || listSequenceNoDTO.isEmpty() || listSequenceNoDTO.get(0).getTransId() == null) {
        LOG.warn("后端接收银联支付返回时银联交易订单号：[" + chinapayDTO.getOrdId() + "]获取transactionId失败1！");
      } else {
        chinapayDTO.setTransId(listSequenceNoDTO.get(0).getTransId());
        //获取原交易信息
        transactionDTO = paymentService.successTransaction(listSequenceNoDTO.get(0).getTransId());
        if (transactionDTO == null) {
          LOG.warn("后端接收银联支付返回时银联交易订单号：[" + chinapayDTO.getOrdId() + "]获取交易支付信息失败1！");
        }
      }
    }
    chinapayDTO.setChinapayType(ChinaPayConstants.ChinaPayType.CHINA_PAY_TYPE_THIRD);
    paymentService.createChinapay(chinapayDTO);
    return transactionDTO;
  }

  /**
   * 前台交易接收
   *
   * @param chinapayDTO 前端通过fromForm方法，可将request中的信息封装成ChinapayDTO对象
   * @return 成功返回ChinapayDTO对象，失败返回null
   */
  @Override
  public ChinapayDTO pgReceive(ChinapayDTO chinapayDTO) {
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    if (chinapayDTO == null) return null;

    chinapayDTO.setChinapayType(3L);

    //读取交易表transId
    List<SequenceNoDTO> listSequenceNoDTO = paymentService.getSequenceNoByNo(chinapayDTO.getOrdId());
    if (listSequenceNoDTO.size() == 0 || listSequenceNoDTO.get(0).getTransId() == null) {
      LOG.warn("前端接收银联支付返回时获取transactionId失败！");
      return null;
    } else {
      //查询出成功的记录
      List<TransactionDTO> listTransactionDTO = paymentService.getTransactionByBaseId(listSequenceNoDTO.get(0).getTransId(), Status.COMPLETED.toString());
      if (listTransactionDTO.size() == 0 || listTransactionDTO.get(0).getId() == null) {
        LOG.warn("前端接收银联支付返回时获取Transaction交易成功信息失败！");
        //交易失败，加入Job表,准备进行查询操作
        PaymentServiceJobDTO paymentServiceJobDTO = new PaymentServiceJobDTO();
        paymentServiceJobDTO.setTransactionId(listSequenceNoDTO.get(0).getTransId());
        paymentServiceJobDTO.setQueryTimes(Long.parseLong(configService.getConfig("SmsRechargeQueryTimes", -1L)));
        paymentService.createPaymentServiceJob(paymentServiceJobDTO);
      }

      chinapayDTO.setTransId(listSequenceNoDTO.get(0).getTransId());
    }
    chinapayDTO.setChinapayType(ChinaPayConstants.ChinaPayType.CHINA_PAY_TYPE_SECOND);
    return paymentService.createChinapay(chinapayDTO);
  }


  @Override
  public ChinapayDTO bgReceiveCheck(Long referenceId, long amount, long payerId, String desc) throws PaymentException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String returnData = "";
    //得到 ChinapayDTO
    List<ChinapayDTO> chinapayDTOList = this.query(referenceId, amount, payerId, desc);
    if(CollectionUtils.isEmpty(chinapayDTOList))return null;
    for (ChinapayDTO chinapayDTO : chinapayDTOList) {
      LOG.info("ChinaPay before query data <ChinapayDTO> details " + chinapayDTO.toString());
      String mock = configService.getConfig("MOCK_PAYMENT", -1L);
      //发送
      if (StringUtils.isNotBlank(mock) && mock.equals("on")) {
        returnData = this.mockPost(chinapayDTO);
      } else {
        returnData = this.post(chinapayDTO);
      }
      //接受
      chinapayDTO = this.postBack(returnData);
      if (chinapayDTO != null) return chinapayDTO;
    }
    return null;
  }

  /**
   * 银联查询方法
   *
   * @param referenceId 引用ID (短信充值单方支付时表示：充值单ID)
   * @param amount      支付金额，单位：分  不超过12位长度
   * @param payerId     支付人ID(短信充值单支付时表示：店面ID)
   * @param desc        订单描述 长度不超过256位
   * @return 成功返回ChinapayDTO 失败返回null
   */
  private List<ChinapayDTO> query(Long referenceId, long amount, long payerId, String desc) {
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<TransactionDTO> transactionDTOList = paymentService.getTransactionByRechargeId(referenceId);
    if (CollectionUtils.isEmpty(transactionDTOList)) {
      LOG.warn("method=query,Transaction is empty！");
      return null;
    }
    List<SequenceNoDTO> sequenceNoDTOList = null;
    SequenceNoDTO sequenceNoDTO = null;
    List<ChinapayDTO> chinapayDTOList = new ArrayList<ChinapayDTO>();
    ChinapayDTO chinapayDTO = null;
    for (TransactionDTO transactionDTO : transactionDTOList) {
      sequenceNoDTOList = paymentService.getSequenceNoByTransId(transactionDTO.getId());
      if (CollectionUtils.isEmpty(sequenceNoDTOList)) {
        LOG.warn("银联支付时查找Transaction失败！");
        continue;
      }
      sequenceNoDTO = sequenceNoDTOList.get(0);
      chinapayDTO = new ChinapayDTO();
      chinapayDTO.setMerId(configService.getConfig("MerId", -1L));//商户号
      chinapayDTO.setOrdId(sequenceNoDTO.getSequenceNo());//交易订号
      chinapayDTO.setOrdAmt(String.valueOf(transactionDTO.getAmount()));//订单金额
      chinapayDTO.setCuryId(configService.getConfig("CurId", -1L));
      chinapayDTO.setInterfaceVersion(configService.getConfig("Version", -1L));
      chinapayDTO.setBgRetUrl(configService.getConfig("ReturnMerBgUrl", -1L));//后台接收地址
      chinapayDTO.setPageRetUrl(configService.getConfig("ReturnMerPgUrl", -1L));//前台交易地址
      chinapayDTO.setGateId(configService.getConfig("GateId", -1L));//支付网关
      chinapayDTO.setOrdDesc(desc);//订单描述
      chinapayDTO.setShareType(configService.getConfig("ShareType", -1L));//分账类型
      chinapayDTO.setShareData(configService.getConfig("ShareA", -1L) + String.valueOf(transactionDTO.getAmount()) + ";");//分账数据
      String data = "";
      data = ChinaPay.getQuerySignData(chinapayDTO);
      String chkValue = null;
      boolean mockFlag = true;
      try {
        String mock = configService.getConfig("MOCK_PAYMENT", -1L);
        if (StringUtils.isNotBlank(mock) && mock.equals("on")) {
          chkValue = "this is mock data from chinaPay! and value is create by query!";
          mockFlag = false;
        } else {
          chkValue = ChinaPay.sign(chinapayDTO.getMerId(), configService.getConfig("MerPriKeyPath", -1L), data);
        }
      } catch (Exception e) {
        LOG.error("银联支付时签名失败！" + e.getMessage(), e);
        continue;
      }
      if ((StringUtil.isEmpty(chkValue) || chkValue.length() != ChinaPay.CHECK_VALUE_LENGTH) && mockFlag) {
        LOG.warn("银联支付时签名验证失败！");
        continue;
      }
      chinapayDTO.setChkValue(chkValue);
      chinapayDTO.setTransId(transactionDTO.getId());
      chinapayDTO.setChinapayType(ChinaPayConstants.ChinaPayType.CHINA_PAY_TYPE_FIRST);
      chinapayDTO = paymentService.createChinapay(chinapayDTO);
      chinapayDTOList.add(chinapayDTO);
    }
    return chinapayDTOList;
  }

  //post back check data
  private ChinapayDTO postBack(String returnData) {
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    if (StringUtils.isEmpty(returnData)) {
      LOG.warn("银联查询时返回为空！");
      return null;
    }
    Map map = this.decodeForMap(returnData);
    if (MapUtils.isEmpty(map)) {
      LOG.warn("银联查询时返回无效！");
      return null;
    }
    //去银联查看状态
    ChinapayDTO chinapayDTO = new ChinapayDTO();
    if ("0".equals(map.get("ResponseCode")) && map.get("PayStat").equals(ChinaPay.PAY_STAT_SUCCESS)) {
      chinapayDTO.setMerId((String) map.get("MerId"));
      chinapayDTO.setOrdId((String) map.get("OrdId"));
      chinapayDTO.setOrdAmt((String) map.get("OrdAmt"));
      chinapayDTO.setCuryId((String) map.get("CuryId"));
      chinapayDTO.setGateId((String) map.get("GateId"));
      chinapayDTO.setInterfaceVersion((String) map.get("Version"));
      chinapayDTO.setOrdDesc((String) map.get("OrdDesc"));
      chinapayDTO.setShareType((String) map.get("ShareType"));
      chinapayDTO.setShareData((String) map.get("ShareData"));
      chinapayDTO.setPriv1((String) map.get("Priv1"));
      chinapayDTO.setPayStat((String) map.get("PayStat"));  //1111未支付，1001支付成功，其余失败
      chinapayDTO.setPayTime((String) map.get("PayTime"));
      chinapayDTO.setRefNum((String) map.get("RefNum"));
      chinapayDTO.setRefAmt((String) map.get("RefAmt"));
      chinapayDTO.setRefTime((String) map.get("RefTime"));
      chinapayDTO.setChkValue((String) map.get("ChkValue"));
      chinapayDTO.setChinapayType(ChinaPayConstants.ChinaPayType.CHINA_PAY_TYPE_FOUR);
      chinapayDTO.setMessage("schedule check, ChinaPay post to us.");
      LOG.info("ChinaPay after query data <ChinapayDTO> details  ：" + chinapayDTO.toString());
      return paymentService.createChinapay(chinapayDTO);
    } else {
      return null;
    }

  }

  // 向银联发送请求
  private String post(ChinapayDTO chinapayDTO) throws PaymentException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    //测试环境：http://bianmin-test.chinapay.com/cpeduinterface/QueryGet.do
    //生产环境：http://apps.chinapay.com/cpeduinterface/QueryGet.do
    String url = configService.getConfig("QueryBgUrl", -1L);
    if (url == null) {
      LOG.warn("银联查询时获取查询路径配置失败！");
      return null;
    }
    String returnData = "";
    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    try {
      httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setConnectTimeout(5000);  //（单位：毫秒）连接超时
      httpURLConnection.setReadTimeout(5000);     //（单位：毫秒）读操作超时
      httpURLConnection.setDoOutput(true);
      OutputStream out = httpURLConnection.getOutputStream();
      out.write(postData(chinapayDTO).getBytes("UTF-8"));
      out.flush();
      out.close();
      bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
      StringBuffer stringBuffer = new StringBuffer();
      int ch;
      while ((ch = bufferedReader.read()) > -1) {
        stringBuffer.append((char) ch);
      }
      bufferedReader.close();
      returnData = stringBuffer.toString();
      LOG.info("ChinaPay after query original data <ChinapayDTO> details " + returnData);
    } catch (IOException e) {
      LOG.error("提交数据时失败！异常信息：" + e.getMessage() + "[URL：" + url + ";POSTDATA：" + chinapayDTO + "]", e);
    }
    return returnData;
  }

  //银联postMock
  private String mockPost(ChinapayDTO chinapayDTO) {
    // test 中控制发送的内容
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String payStat = configService.getConfig("MOCK_PAYMENT_PAYSTAT", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    String returnData = "";
    if (StringUtils.isNotBlank(payStat)) {
      returnData = "ResponseCode=0&MerId=" + chinapayDTO.getMerId() + "&BusiId=" + chinapayDTO.getBusiId() + "&OrdId=" + chinapayDTO.getOrdId() + "&Param1=''" +
          "&Param2=''&Param3=''&Param4=''&Param5=''" + "&Param6=''&Param7=''&Param8=''&Param9=''" + "&Param10=''&OrdAmt=100" +
          "&CuryId=" + chinapayDTO.getCuryId() + "&Version=" + chinapayDTO.getInterfaceVersion() + "&GateId=" + chinapayDTO.getGateId() +
          "&OrdDesc=" + chinapayDTO.getOrdDesc() + "&ShareType=" + chinapayDTO.getShareType() + "&ShareData=" + chinapayDTO.getShareData() +
          "&Priv1=" + chinapayDTO.getPriv1() + "&PayStat=" + payStat + "&PayTime=" + chinapayDTO.getPayTime() + "& RefNum=" + chinapayDTO.getRefNum() +
          "&RefAmt=" + chinapayDTO.getRefAmt() + "&RefTime=" + chinapayDTO.getRefTime() + "& ChkValue =" + chinapayDTO.getChkValue();
    } else {
      returnData = "ResponseCode=-1&Message=fail";
    }
    return returnData;
  }

  //组织待提交的参数
  private String postData(ChinapayDTO chinapayDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append("MerId=");
    sb.append(chinapayDTO.getMerId() == null ? "" : chinapayDTO.getMerId());
    sb.append("&OrdId=");
    sb.append(chinapayDTO.getOrdId() == null ? "" : chinapayDTO.getOrdId());
    sb.append("&Priv1=");
    sb.append(chinapayDTO.getPriv1() == null ? "" : chinapayDTO.getPriv1());
    sb.append("&ChkValue=");
    sb.append(chinapayDTO.getChkValue() == null ? "" : chinapayDTO.getChkValue());
    return sb.toString();
  }

  /**
   * 将响应信息字符串转化为MAP
   *
   * @param input <input name="" value=""></input> &param1=value1&param2=value2
   * @return Map
   */
  private Map<String, String> decodeForMap(String input) {

    Pattern pSplitReturn = Pattern.compile("\\&");
    Pattern pSplitEqual = Pattern.compile("\\=");

    Map<String, String> map = new TreeMap<String, String>();
    String[] rows = pSplitReturn.split(input);
    for (int i = 0; i < rows.length; i++) {
      String[] nvPair = pSplitEqual.split(rows[i], 2);
      map.put(nvPair[0], nvPair[1]);
    }
    return map;
  }

}
