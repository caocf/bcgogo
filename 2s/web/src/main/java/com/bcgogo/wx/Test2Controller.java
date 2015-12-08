package com.bcgogo.wx;

import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.Constant;
import com.bcgogo.notification.model.WXMsg;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IWXTxnService;
import com.bcgogo.txn.service.PrintService;
import com.bcgogo.txn.service.WXTxnService;
import com.bcgogo.txn.service.app.SendVRegulationMsgToAppService;
import com.bcgogo.user.model.CustomerRecord;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.IRequestMonitorService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.wx.IWXMediaManager;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.message.WXMCategory;
import com.bcgogo.wx.qr.GetQRResult;
import com.bcgogo.wx.user.CreateWXGroupResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * 作为微信一些操作的入口
 * 比如创建菜单，同步用户信息等
 * User: ndong
 * Date: 14-8-29
 * Time: 下午5:29
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class Test2Controller {
  private static final Logger LOG = LoggerFactory.getLogger(Test2Controller.class);

  @Autowired
  private IWXUserService wxUserService;
  @Autowired
  private IWXService wxService;


  private Long shopId = 10000010009160816L;


  @RequestMapping(value = "/test2")
  @ResponseBody
  public void test2(HttpServletRequest request) throws Exception {
    String openId = "oCFjjt2gpABhzgNAjkR1qsB_r6B8";
    String result = wxUserService.getVRegulationMsg(openId);
    LOG.debug("result:{}", result);
  }

  @RequestMapping(value = "/test1")
  @ResponseBody
  public void test1(HttpServletRequest request) throws ParseException {
    Long startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm:ss", DateUtil.getNowWeekBegin());
    LOG.info("startTime:{}", startTime);
  }


  /**
   * 同定时钟，实时执行，发送违章消息
   *
   * @param request
   * @throws Exception
   */
  @RequestMapping(value = "/SendVRegulationMsgToAppService")
  public void SendVRegulationMsgToAppService(HttpServletRequest request) throws Exception {
    try {
      ServiceManager.getService(SendVRegulationMsgToAppService.class).sendVRegulationMsgToYiFaWXUser();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 同定时钟，实时执行，发送违章消息
   *
   * @param request
   * @throws Exception
   */
  @RequestMapping(value = "/sendMirrorVRegulationTemplateMsg")
  public void sendMirrorVRegulationTemplateMsg(HttpServletRequest request) throws Exception {
    try {
      ServiceManager.getService(SendVRegulationMsgToAppService.class).sendVRegulationMsgToApp();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 模拟发送客服文本消息
   *
   * @param request
   */
  @RequestMapping(value = "/sendCustomText")
  @ResponseBody
  public void sendCustomText(HttpServletRequest request) {
    try {
      String openId = null;
      if (Constant.EVN_MODE_OFFICIAL.equals(WXHelper.getDefaultPublicNo())) {
        openId = "o3yh5s51qzmF-OgD584nmwD2AWQI";      //统购车业--me
      } else {
        openId = "oCFjjt2gpABhzgNAjkR1qsB_r6B8";  //苏州统购--me
      }
      String content = "电话：18913109919，微信号：525866386";
      ServiceManager.getService(IWXMsgSender.class).sendCustomTextMsg(WXHelper.getDefaultPublicNo(), openId, content);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  /**
   * 模拟发送模版消息
   * @param request
   * @throws Exception
   */
//  @RequestMapping(value = "/sendTemplateMsg")
//  @ResponseBody
//  public Object sendTemplateMsg(HttpServletRequest request) throws Exception {
//    try {
//      String openId="oCFjjt2gpABhzgNAjkR1qsB_r6B8";  //苏州统购--me
//      if(Constant.EVN_MODE_OFFICIAL.equals(WXHelper.getPublicNoByEvnMode())){
//        openId="o3yh5s51qzmF-OgD584nmwD2AWQI";      //统购车业--me
//      }
//      String vehicleNo="粤S89U28";
//      String publicNo=WXHelper.getPublicNoByEvnMode();
//      WXMsgTemplate template= WXHelper.getOrderConsumerTemplate(publicNo, OrderTypes.SALE, vehicleNo, openId, "2014-9-18 12:26", "100", "苏州");
//      Result result=ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(publicNo, template);
//      return result;
//    } catch (Exception e) {
//      LOG.error(e.getMessage(),e);
//      return "error";
//    }
//  }

  /**
   * 模拟发送客服消息
   * @param request
   */
//  @RequestMapping(value = "/sendShopCustomMsg")
//  @ResponseBody
//  public void sendShopCustomMsg(HttpServletRequest request){
//    try {
//      String openId="oCFjjt2gpABhzgNAjkR1qsB_r6B8";  //苏州统购--me
//      if(Constant.EVN_MODE_OFFICIAL.equals(WXHelper.getPublicNoByEvnMode())){
//        openId="o3yh5s51qzmF-OgD584nmwD2AWQI";      //统购车业--me
//      }
//      WXArticleDTO articleDTO=new WXArticleDTO();
//      articleDTO.setFromShopId(10000010009160816l);
//      articleDTO.setUserId(10000010032683168l);
//      articleDTO.setUserName("TEST");
//      articleDTO.setPicUrl("http://bcgogo-check.b0.upaiyun.com/2014/09/30/-1/142407-f53ea2f612a9e42ea1cdd75f5726f9c855131412058247682.jpg");
//      articleDTO.setTitle("article title");
//      articleDTO.setDescription("article description");
//      articleDTO.setUrl(WXHelper.articleDetail(1413878286836l));
//      String[] receiverOpenIds={openId};
//      articleDTO.setReceiverOpenIds(receiverOpenIds);
//      ServiceManager.getService(IWXService.class).saveAndSendCustomNewsMsg(articleDTO);
//    } catch (Exception e) {
//      LOG.error(e.getMessage(),e);
//    }
//  }

  /**
   * 模拟群发消息
   * @return
   */
//  @RequestMapping(value = "/sendMassNewsMsgByGroup")
//  @ResponseBody
//  public Object sendMassNewsMsgByGroup() {
//    try {
//      IWXMsgSender sender=ServiceManager.getService(IWXMsgSender.class);
//      String publicNo=WXHelper.getPublicNoByEvnMode();
//      String mediaId="t8kfPcAhwifad_hUC9kBL3k4zltP6MbL-b3A0qOPGukipbw-UI1F60Q_l-Z4tEji";
//      String groupId="100";
//      Result result=sender.sendMassNewsMsg(publicNo,mediaId,groupId);
//      System.out.println(result.getMsg());
//      return result;
//    } catch (Exception e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//      return new Result(false,"sendMassNewsMsgByGroup failed");
//    }
//  }

  /**
   * 模拟群发消息
   * @return
   */
//  @RequestMapping(value = "/sendMassTextMsg")
//  @ResponseBody
//  public Object sendMassTextMsg() {
//    try {
//      IWXMsgSender sender=ServiceManager.getService(IWXMsgSender.class);
//      String publicNo=WXHelper.getPublicNoByEvnMode();
//      String[] tousers=new String[1];
//      String touser1="oCFjjt2gpABhzgNAjkR1qsB_r6B8";
//      tousers[0]=touser1;
//      Result result=sender.sendMassTextMsg(publicNo,"sendMassTextMsg",tousers);
//      System.out.println(result.getMsg());
//      return result;
//    } catch (Exception e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//      return new Result(false,"sendMassNewsMsgByGroup failed");
//    }
//  }

  /**
   * 模拟群发图文消息
   *
   * @return
   */
  @RequestMapping(value = "/sendMassNewsMsg")
  @ResponseBody
  public Object sendMassNewsMsg() {
    Result result = new Result();
    try {
      WXMsg wxMsg = wxService.getWXMsgById(10000010187278626l);
      if (wxMsg == null) return result.LogErrorMsg("素材不存在，或已经发送。");
      if (StringUtil.isEmpty(wxMsg.getPicUrl())) {
        return result.LogErrorMsg("素材图片不存在。");
      }
      int count = wxService.countShopMonthSuccessMassMsg(wxMsg.getFromShopId());
      if (count > 0) {
        LOG.warn("该店铺本月已群发" + count + "条信息");
      }
      WXArticleDTO articleDTO = new WXArticleDTO();
      articleDTO.setUserId(wxMsg.getUserId());
      articleDTO.setUserName(wxMsg.getUserName());
      articleDTO.setTitle(wxMsg.getTitle());
      articleDTO.setDescription(wxMsg.getDescription());
      articleDTO.setPicUrl(wxMsg.getPicUrl());
      articleDTO.setUrl(wxMsg.getUrl());
      articleDTO.setWxMsgLocalId(wxMsg.getId());

//      String publicNo=WXConstant.YI_FA_PUBLIC_ID;
      String publicNo = "gh_015116eb8fb1";
      String shopName = ServiceManager.getService(IConfigService.class).getShopById(wxMsg.getFromShopId()).getName();
      articleDTO.setAuthor(shopName);
      result = ServiceManager.getService(IWXMediaManager.class).uploadArticles(shopId, articleDTO);
      if (result == null || !result.isSuccess()) {
        LOG.error("wx:upload articles failed,errMsg is {}", result.getMsg());
        return result;
      }
      String mediaId = StringUtil.valueOf(result.getData());
      System.out.println("mediaId:" + mediaId);
      //发送消息
      String[] tousers = new String[2];
//      String touser1="oCFjjt2gpABhzgNAjkR1qsB_r6B8";
//      String touser2="oCFjjt069Ms1D-vzGeyojcFcwQK8";
      String touser1 = "oaPuHsyRNC05R-U-wcqzICU3Z44o";
      String touser2 = "oaPuHs-imYU7c62Z-N7uLDDduzdg";
      tousers[0] = touser1;
      tousers[1] = touser2;
      IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
      result = sender.sendMassNewsMsg(publicNo, mediaId, tousers);
      for (String openId : tousers) {
        //记录消息
        WXMsgDTO msg = new WXMsgDTO();
        msg.fromWxArticleDTO(articleDTO);
        msg.setId(articleDTO.getWxMsgLocalId());
        msg.setFromShopId(wxMsg.getFromShopId());
        msg.setMsgId(StringUtil.valueOf(result.getData()));
        msg.setMediaId(mediaId);
        msg.setOpenId(openId);
        msg.setMediaId(mediaId);
        msg.setSendTime(System.currentTimeMillis());
        msg.setRemark(result.getMsg());
        if (result.isSuccess()) {
          msg.setStatus(WXMsgStatus.SENT);
        } else {
          msg.setStatus(WXMsgStatus.LOCAL_FAILED);
          msg.setRemark(result.getMsg() + "==>有可能是该超过用户每个月推送的4条的限制");
        }
        msg.setCategory(WXMCategory.MASS);
        wxService.saveOrUpdateWXMsg(msg);
        wxService.saveWXMsgReceiver(openId, msg.getId());
      }
      return result;

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  /**
   * 模拟群发图片消息
   *
   * @return
   */
  @RequestMapping(value = "/sendMassImageMsg")
  @ResponseBody
  public Object sendMassImageMsg() {
    try {
      String picUrl = "http://bcgogo-check.b0.upaiyun.com/2014/10/23/-1/114143-a08d8088e8f18a10704702b8a2e5ced661381414035703776.jpg";
      String publicNo = "gh_015116eb8fb1";
      UploadMediaResult mediaResult = ServiceManager.getService(IWXMediaManager.class).uploadImage(shopId, picUrl);
      if (StringUtil.isNotEmpty(mediaResult.getErrcode()) && !ErrCode.SUCCESS.equals(mediaResult.getErrcode())) {
        LOG.error("wx:upload image failed,errMsg is {}", mediaResult.getErrmsg());
        return mediaResult;
      }
      String mediaId = mediaResult.getMedia_id();
      System.out.println("mediaId:" + mediaId);
      //发送消息
      String[] tousers = new String[2];
      String touser1 = "oaPuHsyRNC05R-U-wcqzICU3Z44o";
      String touser2 = "oaPuHs-imYU7c62Z-N7uLDDduzdg";
      tousers[0] = touser1;
      tousers[1] = touser2;
      IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
      Result result = sender.sendMassImageMsg(publicNo, mediaId, tousers);
      return "send success";
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(value = "/createGroup")
  @ResponseBody
  public Object createGroup(HttpServletRequest request) throws Exception {
    String name = "group1";
    CreateWXGroupResult result = wxUserService.createGroup(shopId, name);
    System.out.println(result.getGroup());
    return result;
  }

  @RequestMapping(value = "/addUserToGroup")
  @ResponseBody
  public Object addUserToGroup(HttpServletRequest request) throws Exception {
    String openId = null;
    if (Constant.EVN_MODE_OFFICIAL.equals("")) {
      openId = "o3yh5s51qzmF-OgD584nmwD2AWQI";      //统购车业--me
    } else {
      openId = "oCFjjt2gpABhzgNAjkR1qsB_r6B8";  //苏州统购--me
    }
    ErrCode result = wxUserService.addUserToGroup(shopId, "100", openId);
    System.out.println(result.getErrcode());
    return result;
  }

  @RequestMapping(value = "/downLoadArticles")
  @ResponseBody
  public Object downLoadArticles(HttpServletRequest request) throws Exception {
    IWXMediaManager mediaManager = ServiceManager.getService(IWXMediaManager.class);
    String mediaId = "rM_xa-Ttp-QxFjr2ktd65bnQdnPQwvaHhf6-KD-lRrjZ7NwYOTslRc2yl_9vca7I";
    String resp = mediaManager.downLoadArticles(shopId, mediaId);
    System.out.println(resp);
    return null;
  }

  @RequestMapping(value = "/createTempQRCode")
  @ResponseBody
  public Object createQRCode() {
    try {
      GetQRResult result = WXHelper.createTempQRCode(WXHelper.getDefaultPublicNo(), 1800l, "100050001");
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(value = "/doWXRemindEvent")
  @ResponseBody
  public void doWXRemindEvent() {
    IWXTxnService txnService = ServiceManager.getService(WXTxnService.class);
    try {
      txnService.doWXRemindEvent();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(value = "/toSample")
  public String toSample() throws ParseException {
    IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
    requestMonitorService.calcDeviceFingerScore();
    return "/wx/shareSample";
  }

  @RequestMapping(value = "/sendPrintCommand")
  @ResponseBody
  public String sendPrintCommand(String serialno, Long shopId, Long orderId) throws Exception {
    if (StringUtil.isEmpty(serialno))
      serialno = "3e893e146b92f9fc";
    if (shopId == null)
      shopId = 10000010001040004L;
    if (orderId == null)
      orderId = 10000010028032709L;
    ServiceManager.getService(PrintService.class).sendPrintCommand(shopId, orderId, serialno);
    return "good";
  }

  @RequestMapping(value = "/handleDeadLock")
  public String handleDeadLock() throws ParseException {
    ServiceManager.getService(IUserService.class).handleDeadLock();
    return "finished";
  }

  @RequestMapping(value = "/testTrx/{len}")
  @ResponseBody
  public String toBe(@PathVariable("len") Integer len) throws ParseException {
    UserDaoManager daoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter writer = daoManager.getWriter();
    for (int i = 0; i < len; i++) {
//      CustomerRecord record = writer.getById(CustomerRecord.class, 10000010001060007L);
      CustomerRecord record = writer.getCustomerRecordDTOByCustomerIdAndShopId(10000010001010001L, 10000010001050006L);

      Object status = writer.begin();
      try {
        record.setName("time-out" + DateUtil.convertDateLongToDateString(DateUtil.TIME, System.currentTimeMillis()));
        writer.update(record);
//        writer.commit(status);
        LOG.debug("record:{}", JsonUtil.objectToJson(record));
      } finally {
//           writer.rollback(status);
      }
    }
    CustomerRecord record = writer.getById(CustomerRecord.class, 10000010001060007L);
    Object status = writer.begin();
    try {
      record.setName("time-out" + DateUtil.convertDateLongToDateString(DateUtil.TIME, System.currentTimeMillis()));
      writer.update(record);
      writer.commit(status);
      LOG.debug("record:{}", JsonUtil.objectToJson(record));
    } finally {

    }


    LOG.info("finished");
    return "finished";
  }


}
