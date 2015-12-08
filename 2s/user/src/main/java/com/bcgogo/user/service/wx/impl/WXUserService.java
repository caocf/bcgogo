package com.bcgogo.user.service.wx.impl;

import com.bcgogo.api.WXFanDTO;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.cache.ShopConfigCacheManager;
import com.bcgogo.config.dto.ConfigDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.config.model.WXImageLib;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYun;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.*;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.model.WXMsgReceiver;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.notification.service.WXService;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.wx.*;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.utils.*;
import com.bcgogo.wx.*;
import com.bcgogo.wx.message.WXMCategory;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import com.bcgogo.wx.qr.*;
import com.bcgogo.wx.user.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-13
 * Time: 上午9:41
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WXUserService implements IWXUserService {

  public static final Logger LOG = LoggerFactory.getLogger(WXUserService.class);
  @Autowired
  private UserDaoManager daoManager;

  public List<WXQRCodeDTO> batchCreateLimitQRCode(String publicNo, Long shopId, int maxScene, QRScene scene) throws Exception {
    if (maxScene == 0) {
      LOG.warn("wx:batchCreateLimitQRCode,maxScene is {}", maxScene);
    }
    maxScene++;
    List<WXQRCodeDTO> qrCodeDTOs = new ArrayList<WXQRCodeDTO>();
    int batchLen = maxScene + WXConstant.QR_LIMIT_SCENE_MAX_SIZE;
    for (; maxScene <= batchLen; maxScene++) {
      GetQRResult result = WXHelper.createLimitQRCode(publicNo, String.valueOf(maxScene));
      if (result == null) {
        throw new Exception("create qr code error");
      }
      WXQRCodeDTO qrCodeDTO = new WXQRCodeDTO();
      qrCodeDTO.setPublicNo(publicNo);
      qrCodeDTO.setSceneId(NumberUtil.longValue(maxScene));
      qrCodeDTO.setTicket(result.getTicket());
      qrCodeDTO.setType(QRType.QR_LIMIT_SCENE);
      qrCodeDTO.setScene(scene);
      qrCodeDTO.setCreateTime(System.currentTimeMillis());
      qrCodeDTO.setUrl(result.getUrl());
      qrCodeDTOs.add(qrCodeDTO);
    }
    return qrCodeDTOs;
  }

  /**
   * 获取数据库最大场景值
   *
   * @return
   * @throws Exception
   */
  private int getWXQRCodeMaxScene(String publicNo) throws Exception {
    UserWriter writer = daoManager.getWriter();
    return writer.getWXQRCodeMaxScene(publicNo);
  }


  /**
   * create WXQRCode
   *
   * @param shopId
   * @return
   * @throws Exception
   */
  @Override
  public List<WXQRCodeDTO> createLimitQRCode(String publicNo, Long shopId, QRScene scene) throws Exception {
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_BATCH_CREATE_LIMIT_QR_CODE, shopId)) {
        return null;
      }
      int maxScene = getWXQRCodeMaxScene(publicNo);
      LOG.info("wx:get WXQRCode MaxScene,max scene is {}", maxScene);
      List<WXQRCodeDTO> qrCodeDTOs = batchCreateLimitQRCode(publicNo, shopId, maxScene, scene);
      if (CollectionUtil.isEmpty(qrCodeDTOs)) return null;
      saveOrUpdateWXQRCodeDTOs(qrCodeDTOs.toArray(new WXQRCodeDTO[qrCodeDTOs.size()]));
      return qrCodeDTOs;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_BATCH_CREATE_LIMIT_QR_CODE, shopId);
    }
  }


  /**
   * 创建临时的二维码
   *
   * @param publicNo
   * @param scene
   * @return
   * @throws Exception
   */
  @Override
  public WXQRCodeDTO createTempQRCode(String publicNo, Long shopId, QRScene scene) throws Exception {
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_BATCH_CREATE_LIMIT_QR_CODE, publicNo)) {
        return null;
      }
      int maxScene = getWXQRCodeMaxScene(publicNo);
      LOG.info("wx:createTempQRCode,scene={},maxScene={}", scene, maxScene);
      maxScene++;
      Long expireSeconds = WXConstant.TEMP_QR_CODE_MAX_EXPIRE_SECONDS;
      GetQRResult result = WXHelper.createTempQRCode(publicNo, expireSeconds, String.valueOf(maxScene));
      if (result == null) {
        throw new Exception("create qr code error");
      }
      WXQRCodeDTO qrCodeDTO = new WXQRCodeDTO();
      qrCodeDTO.setPublicNo(publicNo);
      qrCodeDTO.setShopId(shopId);
      qrCodeDTO.setSceneId(NumberUtil.longValue(maxScene));
      qrCodeDTO.setTicket(result.getTicket());
      qrCodeDTO.setType(QRType.QR_SCENE);
      qrCodeDTO.setScene(scene);
      Long time = System.currentTimeMillis();
      qrCodeDTO.setCreateTime(time);
      qrCodeDTO.setExpireSeconds(expireSeconds);
      qrCodeDTO.setExpireTime(time + NumberUtil.longValue(result.getExpire_seconds()) * 1000);
      qrCodeDTO.setUrl(result.getUrl());
      saveOrUpdateWXQRCodeDTOs(qrCodeDTO);
      return qrCodeDTO;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_BATCH_CREATE_LIMIT_QR_CODE, publicNo);
    }
  }

  /**
   * 查询没过期的临时二维码
   *
   * @param appUserNo
   * @return
   */
  @Override
  public WXQRCodeDTO getUnExpireWXQRCodeDTO(String publicNo,String appUserNo) {
    UserWriter writer = daoManager.getWriter();
    WXQRCode qrCode = writer.getUnExpireWXQRCode(publicNo,appUserNo);
    return qrCode != null ? qrCode.toDTO() : null;
  }

  /**
   * 查询没过期的临时二维码
   *
   * @param shopId
   * @return
   */
  @Override
  public WXQRCodeDTO getUnExpireWXQRCodeDTOByShopId(Long shopId, QRScene scene) {
    UserWriter writer = daoManager.getWriter();
    WXQRCode qrCode = writer.getUnExpireWXQRCodeDTOByShopId(shopId, scene);
    return qrCode != null ? qrCode.toDTO() : null;
  }


  public AppUserWXQRCode getAppUserWXQRCodeById(Long id) {
    UserWriter writer = daoManager.getWriter();
    return writer.getById(AppUserWXQRCode.class, id);
  }

  @Override
  public AppUserWXQRCodeDTO getAppUserWXQRCodeDTOByAppUserNo(String appUserNo) {
    if (StringUtil.isEmpty(appUserNo)) {
      return null;
    }
    return getAppUserWXQRCodeDTO(null, appUserNo);
  }

  @Override
  public AppUserWXQRCodeDTO getAppUserWXQRCodeDTO(String publicNo, String appUserNo) {
    UserWriter writer = daoManager.getWriter();
    AppUserWXQRCode code = writer.getAppUserWXQRCode(publicNo, appUserNo);
    return code != null ? code.toDTO() : null;
  }

  @Override
  public AppUserWXQRCodeDTO getAppUserWXQRCodeDTOBySceneId(Long sceneId) {
    UserWriter writer = daoManager.getWriter();
    AppUserWXQRCode code = writer.getAppUserWXQRCodeDTOBySceneId(sceneId);
    return code != null ? code.toDTO() : null;
  }

  @Override
  public void saveOrUpdateAppUserWXQRCodeDTO(AppUserWXQRCodeDTO codeDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    AppUserWXQRCode code = null;
    try {
      if (codeDTO.getId() != null) {
        code = getAppUserWXQRCodeById(codeDTO.getId());
      } else {
        code = new AppUserWXQRCode();
      }
      code.fromDTO(codeDTO);
      writer.saveOrUpdate(code);
      writer.commit(status);
      codeDTO.setId(code.getId());
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public void saveOrUpdateWXQRCodeDTOs(WXQRCodeDTO... codeDTOs) {
    if (ArrayUtil.isEmpty(codeDTOs)) return;
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (WXQRCodeDTO codeDTO : codeDTOs) {
        WXQRCode code = new WXQRCode();
        if (codeDTO.getId() != null) {
          code = writer.getById(WXQRCode.class, codeDTO.getId());
        }
        code.fromDTO(codeDTO);
        writer.saveOrUpdate(code);
        codeDTO.setId(code.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public WXQRCodeDTO getWXQRCodeDTOBySceneId(String publicNo, Long sceneId) {
    if (StringUtil.isEmpty(publicNo) || sceneId == null) return null;
    WXQRCodeSearchCondition condition = new WXQRCodeSearchCondition();
    condition.setPublicNo(publicNo);
    condition.setSceneId(sceneId);
    return CollectionUtil.getFirst(getWXQRCodeDTO(condition));
  }

  @Override
  public List<WXQRCodeDTO> getWXQRCodeDTO(WXQRCodeSearchCondition condition) {
    UserWriter writer = daoManager.getWriter();
    List<WXQRCode> codeList = writer.getWXQRCode(condition);
    if (CollectionUtil.isEmpty(codeList)) return null;
    List<WXQRCodeDTO> codeDTOs = new ArrayList<WXQRCodeDTO>();
    for (WXQRCode code : codeList) {
      codeDTOs.add(code.toDTO());
    }
    return codeDTOs;
  }


  @Override
  public WXQRCodeDTO getWXQRCodeDTOByShopId(String publicNo, Long shopId, QRScene scene) throws Exception {
    UserWriter writer = daoManager.getWriter();
    WXQRCode qrCode = writer.getWXQRCodeByShopId(publicNo, shopId, scene);
    return qrCode != null ? qrCode.toDTO() : null;
  }


  /**
   * 店铺添加粉丝。获取未分配的二维码
   *
   * @param publicNo
   * @param shopId
   * @return
   * @throws Exception
   */
  @Override
  public WXQRCodeDTO getUnAssignedWXQRCode(String publicNo, Long shopId) throws Exception {
    UserWriter writer = daoManager.getWriter();
    WXQRCode code = writer.getUnAssignedWXQRCode(publicNo);
    if (code == null) {
      createLimitQRCode(publicNo, shopId, QRScene.SHOP_USER);
      code = writer.getUnAssignedWXQRCode(publicNo);
    }
    return code != null ? code.toDTO() : null;
  }

  @Override
  public boolean buildWXImageLib(String folder) throws Exception {
    LOG.info("wx:ready to build WX ImageLib");
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String path = configService.getConfig("wx_img_lib_path", ShopConstant.BC_SHOP_ID);
    if (StringUtil.isEmpty(path)) {
      LOG.error("wx:wx_img_lib_path is empty");
      return false;
    }
    path = path + folder;
    List<WXImageLib> imageLibs = new ArrayList<WXImageLib>();
    Map<String, String> params = new HashMap<String, String>();
    params.put(UpYun.PARAMS.KEY_X_GMKERL_THUMBNAIL.getValue(), ImageScene.IMAGE_AUTO.getImageVersion());
    //读取原始文件
    File file = new File(path);
    if (!file.isDirectory()) {
      LOG.error("wx:image path directory is error");
      return false;
    }
    LOG.info("wx:buildWXImageLib to upload to upYun");
    String[] fileList = file.list();
    String upYun_Domain_Url = configService.getConfig("upYun_Domain_Url", ShopConstant.BC_SHOP_ID);
    for (int i = 0; i < fileList.length; i++) {
      File readFile = new File(path + System.getProperty("file.separator") + fileList[i]);
      String imageName = folder + "_" + i;
      byte[] imageBytes = null;
      BufferedImage image = ImageIO.read(readFile);
      ByteArrayOutputStream bs = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", ImageIO.createImageOutputStream(bs));
      imageBytes = bs.toByteArray();
      if (ArrayUtil.isEmpty(imageBytes)) {
        LOG.error("wx:can't read image,file name is {}", imageName);
        continue;
      }
      //上传到upyun
      String photoPath = UpYunManager.getInstance().generateUploadImagePath(Long.valueOf(i), imageName + "-zd.png");
      if (UpYunManager.getInstance().writeFile(photoPath, imageBytes, true, params)) {
        WXImageLib imageLib = new WXImageLib();
        imageLib.setName(imageName);
        imageLib.setUrl(upYun_Domain_Url + photoPath);
        imageLibs.add(imageLib);
      }
    }

    if (CollectionUtil.isEmpty(imageLibs)) {
      return false;
    }
    //生成短号
    LOG.info("wx:buildWXImageLib to getShortUrl");
    String publicNo = WXHelper.getDefaultPublicNo();
    for (WXImageLib imageLib : imageLibs) {
      String url = imageLib.getUrl();
      String shortUrl = WXHelper.getShortUrl(publicNo, url);
      imageLib.setShortUrl(shortUrl);
    }
    //保存WXImagelib
    LOG.info("wx:buildWXImageLib to saveOrUpdateConfig");
    configService.saveWXImageLib(imageLibs.toArray(new WXImageLib[imageLibs.size()]));
    //更新config
    ConfigDTO configDTO = new ConfigDTO();
    configDTO.setName(WXConstant.PREFIX_WX_LIB_IMAGE_NUM + folder);
    configDTO.setShopId(ShopConstant.BC_SHOP_ID);
    configDTO.setDescription(folder);
    configDTO.setValue(StringUtil.valueOf(imageLibs.size()));
    configService.saveOrUpdateConfig(configDTO);
    return true;
  }

  @Override
  public String getVehicleBindRemindMsgXml(String publicNo, String openId) throws IOException {
    if (StringUtil.isEmpty(publicNo) || StringUtil.isEmpty(openId)) return null;
    String content = WXConstant.CONTENT_VEHICLE_BIND.replace("{B_URL}", WXHelper.vehicleBindUrl(openId));
    return ServiceManager.getService(IWXMsgSender.class).getTextMsgXml(openId, publicNo, content);
  }

  @Override
  public String getVehicleBindRemindMsg(String publicNo, String openId) throws IOException {
    if (StringUtil.isEmpty(publicNo) || StringUtil.isEmpty(openId)) return null;
    return WXConstant.CONTENT_VEHICLE_BIND.replace("{B_URL}", WXHelper.vehicleBindUrl(openId));
  }

  @Override
  public String getHistoryBill(String publicNo, String openId) throws Exception {
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    List<WXUserVehicleDTO> userVehicleDTOs = getWXUserVehicle(openId, null);
    if (CollectionUtil.isEmpty(userVehicleDTOs)) {
      return getVehicleBindRemindMsgXml(publicNo, openId);
    }
    List<WXArticleDTO> articleDTOs = getHistoryBillArticleDTO(openId);
    if (CollectionUtil.isEmpty(articleDTOs)) {
      return sender.getTextMsgXml(openId, publicNo, "您还没有消费记录账单。");
    }
    return sender.getNewsMessage(openId, publicNo, articleDTOs.toArray(new WXArticleDTO[articleDTOs.size()]));
  }


  @Override
  public List<WXArticleDTO> getHistoryBillArticleDTO(String openId) throws Exception {
    List<WXUserVehicleDTO> userVehicleDTOs = getWXUserVehicle(openId, null);
    if (CollectionUtil.isEmpty(userVehicleDTOs)) {
      return null;
    }
    List<String> vehicleNos = new ArrayList<String>();
    for (WXUserVehicleDTO userVehicleDTO : userVehicleDTOs) {
      vehicleNos.add(userVehicleDTO.getVehicleNo());
    }
    OrderSearchConditionDTO conditionDTO = new OrderSearchConditionDTO();
    conditionDTO.setVehicleList(vehicleNos.toArray(new String[vehicleNos.size()]));
    conditionDTO.setSort("created_time desc");
    conditionDTO.setRowStart(0);
    conditionDTO.setPageRows(5);
//    conditionDTO.setStartTime(DateUtil.getStartTimeOfMonth(-1));
//    conditionDTO.setEndTime(DateUtil.getEndTimeOfToday());
    String[] searchStrategy = {OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT, OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET};
    conditionDTO.setSearchStrategy(searchStrategy);
    String[] orderTypes = {OrderTypes.SALE.toString(), OrderTypes.REPAIR.toString(), OrderTypes.WASH_BEAUTY.toString()};
    conditionDTO.setOrderType(orderTypes);
    String[] orderStatus = new String[]{OrderStatus.SALE_DONE.toString(),
      OrderStatus.WASH_SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString()};
    conditionDTO.setOrderStatus(orderStatus);
    conditionDTO.setStatsFields(new String[]{OrderSearchResultListDTO.ORDER_TOTAL_AMOUNT});
    conditionDTO.setFacetFields(new String[]{"order_type"});
    List<WXArticleDTO> articles = new ArrayList<WXArticleDTO>();
    OrderSearchResultListDTO orderSearchResultListDTO = ServiceManager.getService(ISearchOrderService.class).queryOrders(conditionDTO);
    List<OrderSearchResultDTO> orders = orderSearchResultListDTO.getOrders();
    if (CollectionUtil.isEmpty(orders)) return null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    Map<String, Double> totalMap=orderSearchResultListDTO.getTotalAmounts();
//    double total=NumberUtil.addition(totalMap.get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_SALE"),
//      totalMap.get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_REPAIR"),
//      totalMap.get("ORDER_TOTAL_AMOUNT_ORDER_TYPE_WASH_BEAUTY"));
    double total = 0d;
    for (OrderSearchResultDTO order : orders) {
      total += order.getAmount();
    }
    Random random = new Random();
    WXArticleDTO firstArt = new WXArticleDTO();
    firstArt.setTitle("您最近有" + orders.size() + "条消费，累计" + NumberUtil.round(total) + "元。点击查看更多历史账单");
    firstArt.setPicUrl("http://w.url.cn/s/A33r2vt");
    firstArt.setUrl(WXHelper.getEvnDomain() + "/web/wxTxn.do?method=oList&_i=" + openId);
    articles.add(firstArt);
    for (OrderSearchResultDTO order : orders) {
      WXArticleDTO article = new WXArticleDTO();
      String orderTypeStr = "消费";
      if (OrderTypes.REPAIR.toString().equals(order.getOrderType())) {
        orderTypeStr = "施工消费";
      }
      if (OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType())) {
        orderTypeStr = "洗车消费";
      }
      article.setTitle(DateUtil.convertDateLongToString(order.getVestDate(), DateUtil.DATE_STRING_FORMAT_CN4) + "," + order.getShopName() + "," + orderTypeStr + order.getAmount() + "元,点击查看详情");
      int lib_img_num = NumberUtil.intValue(configService.getConfig(WXConstant.PREFIX_WX_LIB_IMAGE_NUM + WXConstant.IMAGE_FILE_LIB_BILL, ShopConstant.BC_SHOP_ID));
      String name = WXConstant.IMAGE_FILE_LIB_BILL + "_" + random.nextInt(lib_img_num);
      String shortUrl = WXHelper.getWXImageLibShortUrl(name);
      article.setPicUrl(shortUrl);
      article.setUrl(WXHelper.orderDetailUrl(order.getOrderId(), order.getOrderType(), order.getVehicle()));
      articles.add(article);
    }
    return articles;
  }


  private List<WXArticleDTO> getMemberCardArticleDTO(String openId, List<MemberDTO> memberDTOs) throws Exception {
    if (CollectionUtil.isEmpty(memberDTOs)) return null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<WXArticleDTO> articleDTOs = new ArrayList<WXArticleDTO>();
    Random random = new Random();
    int count = 0;
    for (MemberDTO memberDTO : memberDTOs) {
      if (count > 9) return articleDTOs;
      WXArticleDTO articleDTO = new WXArticleDTO();
      articleDTO.setTitle("【" + memberDTO.getShopName() + "】 " + memberDTO.getType() + "。点击查看详情。");
      int lib_img_num = NumberUtil.intValue(configService.getConfig(WXConstant.PREFIX_WX_LIB_IMAGE_NUM + WXConstant.IMAGE_FILE_LIB_MEMBER, ShopConstant.BC_SHOP_ID));
      String name = WXConstant.IMAGE_FILE_LIB_MEMBER + "_" + random.nextInt(lib_img_num);
      String shortUrl = WXHelper.getWXImageLibShortUrl(name);
      articleDTO.setPicUrl(shortUrl);
      articleDTO.setUrl(WXHelper.memberDetailUrl(openId, memberDTO.getId()));
      articleDTOs.add(articleDTO);
      count++;
    }
    return articleDTOs;
  }

  @Override
  public String getMemberCardNews(String publicNo, String openId) throws Exception {
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    List<WXUserVehicleDTO> userVehicleDTOs = getWXUserVehicle(openId, null);
    if (CollectionUtil.isEmpty(userVehicleDTOs)) {
      return getVehicleBindRemindMsgXml(publicNo, openId);
    }
    List<String> vehicleNos = new ArrayList<String>();
    for (WXUserVehicleDTO userVehicleDTO : userVehicleDTOs) {
      vehicleNos.add(userVehicleDTO.getVehicleNo());
    }
    List<MemberDTO> memberDTOs = getMemberDTO(vehicleNos);
    if (CollectionUtil.isEmpty(memberDTOs)) {
      return sender.getTextMsgXml(openId, publicNo, "您还没有办理会员卡。");
    }
    List<WXArticleDTO> articleDTOs = getMemberCardArticleDTO(openId, memberDTOs);
    return sender.getNewsMessage(openId, publicNo, articleDTOs.toArray(new WXArticleDTO[articleDTOs.size()]));
  }

  /**
   * 微信会员卡详细页面最近5条消费记录
   *
   * @param conditionDTO
   * @return
   * @throws Exception
   */
  @Override
  public List<OrderSearchResultDTO> getWOrdersByConditionDTO(OrderSearchConditionDTO conditionDTO) throws Exception {
    if (conditionDTO == null) {
      return null;
    }
    conditionDTO.setSort("created_time desc");
    conditionDTO.setRowStart(0);
    conditionDTO.setPageRows(5);
    conditionDTO.setStartTime(DateUtil.getStartTimeOfMonth(-1));
    conditionDTO.setEndTime(DateUtil.getEndTimeOfToday());
    String[] searchStrategy = {OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT, OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET};
    conditionDTO.setSearchStrategy(searchStrategy);
    String[] orderTypes = {OrderTypes.SALE.toString(), OrderTypes.REPAIR.toString(), OrderTypes.WASH_BEAUTY.toString()};
    conditionDTO.setOrderType(orderTypes);
    String[] orderStatus = new String[]{OrderStatus.SALE_DONE.toString(),
      OrderStatus.WASH_SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString()};
    conditionDTO.setOrderStatus(orderStatus);
    OrderSearchResultListDTO orderSearchResultListDTO = ServiceManager.getService(ISearchOrderService.class).queryOrders(conditionDTO);
    List<OrderSearchResultDTO> orders = orderSearchResultListDTO.getOrders();
    return orders;
  }

  @Override
  public String getVehicleNews(String publicNo, String openId) throws Exception {
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<WXUserVehicleDTO> userVehicleDTOs = getWXUserVehicle(openId, null);
    if (CollectionUtil.isEmpty(userVehicleDTOs)) {
      return getVehicleBindRemindMsgXml(publicNo, openId);
    }
    int lib_img_num = NumberUtil.intValue(configService.getConfig(WXConstant.PREFIX_WX_LIB_IMAGE_NUM + WXConstant.IMAGE_FILE_LIB_VEHICLE, ShopConstant.BC_SHOP_ID));
    Random random = new Random();
    List<WXArticleDTO> articleDTOs = new ArrayList<WXArticleDTO>();
    for (WXUserVehicleDTO userVehicleDTO : userVehicleDTOs) {
      WXUserVehicleDTO uVehicleDTO = CollectionUtil.getFirst(getWXUserVehicle(openId, userVehicleDTO.getVehicleNo()));
      WXArticleDTO articleDTO = new WXArticleDTO();
      articleDTO.setTitle("车牌号：" + uVehicleDTO.getVehicleNo() + "。点击查看编辑信息。");
      String name = WXConstant.IMAGE_FILE_LIB_VEHICLE + "_" + random.nextInt(lib_img_num);
      String shortUrl = WXHelper.getWXImageLibShortUrl(name);
      articleDTO.setPicUrl(shortUrl);
      articleDTO.setUrl(WXHelper.vehicleEditUrl(uVehicleDTO.getId()));
      articleDTOs.add(articleDTO);
    }
    return sender.getNewsMessage(openId, publicNo, articleDTOs.toArray(new WXArticleDTO[articleDTOs.size()]));
  }

  /**
   * 根据车牌号获取会员卡
   *
   * @param vehicleNos
   * @return
   * @throws Exception
   */
  private List<MemberDTO> getMemberDTO(List<String> vehicleNos) throws Exception {
    if (CollectionUtil.isEmpty(vehicleNos)) return null;
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<MemberDTO> memberDTOs = new ArrayList<MemberDTO>();
    Set<Long> customerIds = new HashSet<Long>();
    for (String vehicleNo : vehicleNos) {
      List<CustomerDTO> customerDTOs = userService.getCustomerByVehicleNo(vehicleNo);
      if (CollectionUtil.isEmpty(customerDTOs)) continue;
      for (CustomerDTO customerDTO : customerDTOs) {
        if (customerIds.contains(customerDTO.getId())) {
          continue;
        }
        customerIds.add(customerDTO.getId());
        MemberDTO memberDTO = membersService.getMemberByCustomerId(customerDTO.getShopId(), customerDTO.getId());
        if (memberDTO == null) continue;
        ShopDTO shopDTO = configService.getShopById(memberDTO.getShopId());
        memberDTO.setShopName(shopDTO.getName());
        memberDTOs.add(memberDTO);
      }
    }
    return memberDTOs;
  }


  @Override
  public String getVRegulationMsg(String openId) throws Exception {
    List<WXUserVehicleDTO> userVehicleDTOs = getWXUserVehicle(openId, null);
    if (CollectionUtil.isEmpty(userVehicleDTOs)) {
      return null;
    }
    IJuheService juheService = ServiceManager.getService(IJuheService.class);
    StringBuffer sb = new StringBuffer();
    int count = 0;
    for (WXUserVehicleDTO userVehicleDTO : userVehicleDTOs) {
      count++;
      if (userVehicleDTO.getCity() == null) {
        sb.append(count).append(") ").append(userVehicleDTO.getVehicleNo()).append(" 查询城市未填写 ")
          .append("<a href='").append(WXHelper.vehicleEditUrl(userVehicleDTO.getId())).append("'>立即编辑</a>").append("\n");
        continue;
      }
      String juheCode = AreaCacheManager.getJuheCodeByCityCode(userVehicleDTO.getCity());
      if (StringUtil.isEmpty(juheCode)) {
        sb.append(count).append(") ").append(userVehicleDTO.getVehicleNo()).append(" 查询城市正在维护或未开通查询").append("\n");
        continue;
      }
      Result result = juheService.queryUnHandledVehicleViolateRegulation(juheCode, userVehicleDTO.getVehicleNo(), "02", userVehicleDTO.getEngineNo(), userVehicleDTO.getVin(), null);
      if (result.isSuccess()) {
        List<VehicleViolateRegulationRecordDTO> recordDTOs = (List<VehicleViolateRegulationRecordDTO>) result.getData();
        sb.append(count).append(") ").append(userVehicleDTO.getVehicleNo());
        if (CollectionUtil.isNotEmpty(recordDTOs)) {
          sb.append(" 共").append(recordDTOs.size()).append("条违章记录").append(" <a href='").append(WXHelper.vehicleRegulation(userVehicleDTO.getId())).append("'>点击查看详细</a>");
        } else {
          sb.append(" 没有违章记录");
        }
      } else {
        sb.append(count).append(") ").append(userVehicleDTO.getVehicleNo()).append(" ").append(result.getMsg())
          .append("<a href='").append(WXHelper.vehicleEditUrl(userVehicleDTO.getId())).append("'>立即编辑</a>");
      }
      sb.append("\n");
    }
    String content = sb.toString();
    return StringUtil.isEmpty(content) ? content = "您没有违章记录。" : content;
  }

  @Override
  public void sendMirrorVRegulationTemplateMsg(List<VehicleViolateRegulationRecordDTO> recordDTOs) throws Exception {
    if (CollectionUtil.isEmpty(recordDTOs)) return;
    IWXService wxService = ServiceManager.getService(WXService.class);
    for (VehicleViolateRegulationRecordDTO recordDTO : recordDTOs) {
      String vehicleNo = recordDTO.getVehicleNo();
      List<WXUserDTO> userDTOs = getWXUserDTOByVehicleNo(vehicleNo);
      if (CollectionUtil.isEmpty(userDTOs)) continue;
      for (WXUserDTO userDTO : userDTOs) {
        WXMsgTemplate template = WXHelper.getVRegulationTemplate(userDTO.getPublicNo(), userDTO.getOpenid(), recordDTO);
        if (template == null) continue;
        WXUserVehicleDTO userVehicleDTO = CollectionUtil.getFirst(getWXUserVehicle(userDTO.getOpenid(), vehicleNo));
        template.setUrl(WXHelper.vehicleRegulation(userVehicleDTO.getId()));
        Result result = ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(userDTO.getPublicNo(), template);
        //记录消息
        WXMsgDTO msg = new WXMsgDTO();
        msg.setMsgId(StringUtil.valueOf(result.getData()));
        msg.setOpenId(userDTO.getOpenid());
        msg.setSendTime(System.currentTimeMillis());
        msg.setRemark(result.getMsg());
        if (result.isSuccess()) {
          msg.setStatus(WXMsgStatus.SENT);
        } else {
          msg.setStatus(WXMsgStatus.LOCAL_FAILED);
        }
        msg.setCategory(WXMCategory.TEMPLATE);
        wxService.saveOrUpdateWXMsg(msg);
        wxService.saveWXMsgReceiver(userDTO.getOpenid(), msg.getId());
//        //save WXShopBill
//        WXShopBillDTO billDTO=new WXShopBillDTO();
//        billDTO.setMsgId(msg.getId());
//        billDTO.setShopId(msg.getFromShopId());
//        billDTO.setVestDate(System.currentTimeMillis());
//        billDTO.setScene(SmsSendScene.WX_APPOINT_REMIND_TEMPLATE);
//        billDTO.setTotal(WXConstant.MSG_PRICE);
//        wxService.saveOrUpdateWXShopBill(billDTO);
      }
    }
  }


  @Override
  public void sendVRegulationTemplateMsg(List<VehicleViolateRegulationRecordDTO> recordDTOs) throws Exception {
    if (CollectionUtil.isEmpty(recordDTOs)) return;
    IWXService wxService = ServiceManager.getService(WXService.class);
    for (VehicleViolateRegulationRecordDTO recordDTO : recordDTOs) {
      String vehicleNo = recordDTO.getVehicleNo();
      List<WXUserDTO> userDTOs = getWXUserDTOByVehicleNo(vehicleNo);
      if (CollectionUtil.isEmpty(userDTOs)) continue;
      for (WXUserDTO userDTO : userDTOs) {
        WXMsgTemplate template = WXHelper.getVRegulationTemplate(userDTO.getPublicNo(), userDTO.getOpenid(), recordDTO);
        if (template == null) continue;
        WXUserVehicleDTO userVehicleDTO = CollectionUtil.getFirst(getWXUserVehicle(userDTO.getOpenid(), vehicleNo));
        template.setUrl(WXHelper.vehicleRegulation(userVehicleDTO.getId()));
        Result result = ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(userDTO.getPublicNo(), template);
        //记录消息
        WXMsgDTO msg = new WXMsgDTO();
        msg.setMsgId(StringUtil.valueOf(result.getData()));
        msg.setOpenId(userDTO.getOpenid());
        msg.setSendTime(System.currentTimeMillis());
        msg.setRemark(result.getMsg());
        if (result.isSuccess()) {
          msg.setStatus(WXMsgStatus.SENT);
        } else {
          msg.setStatus(WXMsgStatus.LOCAL_FAILED);
        }
        msg.setCategory(WXMCategory.TEMPLATE);
        wxService.saveOrUpdateWXMsg(msg);
        wxService.saveWXMsgReceiver(userDTO.getOpenid(), msg.getId());
//        //save WXShopBill
//        WXShopBillDTO billDTO=new WXShopBillDTO();
//        billDTO.setMsgId(msg.getId());
//        billDTO.setShopId(msg.getFromShopId());
//        billDTO.setVestDate(System.currentTimeMillis());
//        billDTO.setScene(SmsSendScene.WX_APPOINT_REMIND_TEMPLATE);
//        billDTO.setTotal(WXConstant.MSG_PRICE);
//        wxService.saveOrUpdateWXShopBill(billDTO);
      }
    }
  }

  @Override
  public WXSubscribeRecord getWXSubscribeRecordById(Long id) {
    UserWriter writer = daoManager.getWriter();
    return writer.getById(WXSubscribeRecord.class, id);
  }

  @Override
  public void saveOrUpdateWXSubscribeRecord(WXSubscribeRecordDTO recordDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    WXSubscribeRecord record = null;
    try {
      if (recordDTO.getId() != null) {
        record = getWXSubscribeRecordById(recordDTO.getId());
      } else {
        record = new WXSubscribeRecord();
      }
      record.fromDTO(recordDTO);
      writer.saveOrUpdate(record);
      writer.commit(status);
      recordDTO.setId(record.getId());
    } finally {
      writer.rollback(status);
    }
  }


  private WXKWTemplate getWXKWTemplate(String publicNo, String title) {
    if (StringUtil.isEmpty(publicNo) || StringUtil.isEmpty(title)) return null;
    UserWriter writer = daoManager.getWriter();
    return writer.getWXKWTemplate(publicNo, title);
  }

  private WXKWTemplateDTO getWXKWTemplateDTO(String publicNo, String title) {
    if (StringUtil.isEmpty(publicNo) || StringUtil.isEmpty(title)) return null;
    WXKWTemplate template = getWXKWTemplate(publicNo, title);
    return template != null ? template.toDTO() : null;
  }

//  @Override
//  public WXKWTemplateDTO getWXKWTemplate(String publicNo, String title) throws Exception {
//
//    return getCachedWXKWTemplate(publicNo, title);
//    //todo 店铺定制模版
////    return null;
//  }

  @Override
  public WXKWTemplateDTO getCachedWXKWTemplate(String publicNo, String title) throws Exception {
    if (StringUtil.isEmpty(title)) return null;
    String mKey = WXConstant.TEMPLATE_KEY_APPOINT_REMIND_PREFIX + publicNo + title;
    WXKWTemplateDTO templateDTO = (WXKWTemplateDTO) MemCacheAdapter.get(mKey);
    if (templateDTO == null) {
      templateDTO = getWXKWTemplateDTO(publicNo, title);
      if (templateDTO != null) {
        MemCacheAdapter.set(mKey, templateDTO);
      }
    }
    return templateDTO;
  }

  /**
   * 审核通过,正式发送
   */
  @Override
  public Result sendShopMassNewsMsg(Long shopId, WXArticleDTO articleDTO) throws Exception {
    Result result = new Result();
    if (StringUtil.isAllEmpty(shopId, articleDTO)) return result.LogErrorMsg("illegal param");
    //上传素材
    String shopName = ServiceManager.getService(IConfigService.class).getShopById(shopId).getName();
    articleDTO.setAuthor(shopName);
    result = ServiceManager.getService(IWXMediaManager.class).uploadArticles(shopId, articleDTO);
    if (result == null || !result.isSuccess()) {
      LOG.error("wx:uploadArticles failed,errMsg is {}", result.getMsg());
      return result;
    }
    //发送消息
    String mediaId = StringUtil.valueOf(result.getData());
    IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
    IWXService wxService = ServiceManager.getService(IWXService.class);
    List<WXMsgReceiver> receivers = wxService.getWXMsgReceiverByMsgLocalId(articleDTO.getWxMsgLocalId());
    if (CollectionUtil.isEmpty(receivers)) {
      return result.LogErrorMsg("收信微信用户为空，停止发送。");
    }
    List<String> openIds = new ArrayList<String>();
    for (WXMsgReceiver receiver : receivers) {
      openIds.add(receiver.getOpenId());
    }
    String[] tousers = openIds.toArray(new String[openIds.size()]);
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    WXAccountDTO accountDTO = accountService.getDecryptedWXAccountByShopId(shopId);
    if (accountDTO == null) accountDTO = accountService.getDefaultWXAccount();
    result = sender.sendMassNewsMsg(accountDTO.getPublicNo(), mediaId, tousers);
    //after send news
    if (result != null && result.isSuccess()) {
      //update wx msg
      WXMsgDTO msgDTO = wxService.getWXMsgDTOById(articleDTO.getWxMsgLocalId());
      msgDTO.setMediaId(mediaId);
      msgDTO.setMsgId(StringUtil.valueOf(result.getData()));
      msgDTO.setStatus(WXMsgStatus.SENT);
      msgDTO.setSendTime(System.currentTimeMillis());
      wxService.saveOrUpdateWXMsg(msgDTO);
      //save msg receiver
      List<WXMsgReceiverDTO> receiverDTOs = new ArrayList<WXMsgReceiverDTO>();
      for (String openId : tousers) {
        WXMsgReceiverDTO receiverDTO = new WXMsgReceiverDTO();
        receiverDTO.setOpenId(openId);
        receiverDTO.setMsgId(msgDTO.getId());
        receiverDTO.setDeleted(DeletedType.FALSE);
        receiverDTOs.add(receiverDTO);
      }
      wxService.saveOrUpdateWXMsgReceiver(receiverDTOs.toArray(new WXMsgReceiverDTO[receiverDTOs.size()]));
      //upload article img
      uploadShopArticleImg(articleDTO);
      //save WXShopBill
      WXShopBillDTO billDTO = new WXShopBillDTO();
      billDTO.setMsgId(msgDTO.getId());
      billDTO.setShopId(msgDTO.getFromShopId());
      billDTO.setVestDate(System.currentTimeMillis());
      billDTO.setScene(SmsSendScene.WX_SEND_MASS_MSG);
      billDTO.setTotal(WXConstant.MSG_PRICE * tousers.length);
      billDTO.setAmount(tousers.length);
      WXShopAccountDTO shopAccount = accountService.getWXShopAccountDTOByShopId(msgDTO.getFromShopId());
      if (shopAccount != null && shopAccount.getBalance() > 0) {
        billDTO.setTotal(WXConstant.MSG_PRICE * tousers.length);
        shopAccount.setBalance(NumberUtil.subtract(shopAccount.getBalance(), WXConstant.MSG_PRICE * tousers.length));
      } else {
        billDTO.setTotal(0D);
      }
      wxService.saveOrUpdateWXShopBill(billDTO);
      accountService.saveOrUpdateWXShopAccountDTO(shopAccount);
    } else {
      if ("system error".equals(result.getMsg())) {

      }
      //update wx msg
      WXMsgDTO msgDTO = wxService.getWXMsgDTOById(articleDTO.getWxMsgLocalId());
      msgDTO.setMediaId(mediaId);
      msgDTO.setStatus(WXMsgStatus.LOCAL_FAILED);
      msgDTO.setRemark(result.getMsg() + "==>有可能是该超过用户每个月推送的4条的限制");
      wxService.saveOrUpdateWXMsg(msgDTO);
      return result;

    }
    return result;
  }

  /**
   * 保存微信素材
   *
   * @param publicNo
   * @param articleDTO
   * @return
   * @throws Exception
   */
  @Override
  public Result saveMassNewsMsg(String publicNo, WXArticleDTO articleDTO) {
    return null;
  }

  /**
   * 提交审核
   *
   * @param articleDTO
   * @throws Exception
   */
  @Override
  public void saveArticle(WXArticleDTO articleDTO) throws Exception {
    //save msg
    WXMsgDTO wxMsgDTO = new WXMsgDTO();
    wxMsgDTO.fromWxArticleDTO(articleDTO);
    wxMsgDTO.setSubmitReviewTime(System.currentTimeMillis());
    wxMsgDTO.setSendTime(System.currentTimeMillis());
    wxMsgDTO.setStatus(WXMsgStatus.AUDITING);
    wxMsgDTO.setCategory(WXMCategory.MASS);
    ServiceManager.getService(WXService.class).saveOrUpdateWXMsg(wxMsgDTO);
    articleDTO.setWxMsgLocalId(wxMsgDTO.getId());
    //save msg receiver
    IWXService wxService = ServiceManager.getService(WXService.class);
    String[] receiverOpenIds = articleDTO.getReceiverOpenIds();
    if (ArrayUtil.isNotEmpty(receiverOpenIds)) {
      articleDTO.setReceiverOpenIds(receiverOpenIds);
      List<WXMsgReceiver> receivers = new ArrayList<WXMsgReceiver>();
      for (String openId : receiverOpenIds) {
        WXMsgReceiver wxMsgReceiver = new WXMsgReceiver();
        wxMsgReceiver.setOpenId(openId);
        wxMsgReceiver.setMsgId(wxMsgDTO.getId());
        receivers.add(wxMsgReceiver);
      }
      wxService.saveWXMsgReceiver(receivers.toArray(new WXMsgReceiver[receivers.size()]));
    } else { //选了全部粉丝
      List<WXUserDTO> userDTOs = getWXUserDTOByShopId(articleDTO.getFromShopId());
      if (CollectionUtil.isNotEmpty(userDTOs)) {
        List<WXMsgReceiver> receivers = new ArrayList<WXMsgReceiver>();
        for (WXUserDTO userDTO : userDTOs) {
          WXMsgReceiver wxMsgReceiver = new WXMsgReceiver();
          wxMsgReceiver.setOpenId(userDTO.getOpenid());
          wxMsgReceiver.setMsgId(wxMsgDTO.getId());
          receivers.add(wxMsgReceiver);
        }
        wxService.saveWXMsgReceiver(receivers.toArray(new WXMsgReceiver[receivers.size()]));
      }
    }
  }


  @Override
  public Result saveAndSendCustomNewsMsg(WXArticleDTO articleDTO) throws Exception {
    //save msg
    WXMsgDTO wxMsgDTO = new WXMsgDTO();
    wxMsgDTO.fromWxArticleDTO(articleDTO);
    wxMsgDTO.setStatus(WXMsgStatus.SUCCESS);
    wxMsgDTO.setCategory(WXMCategory.SERVICE);
    wxMsgDTO.setSubmitReviewTime(System.currentTimeMillis());
    wxMsgDTO.setSendTime(System.currentTimeMillis());
    IWXService wxService = ServiceManager.getService(WXService.class);
    wxService.saveOrUpdateWXMsg(wxMsgDTO);
    //save msg receiver
    String[] receiverOpenIds = articleDTO.getReceiverOpenIds();
    if (ArrayUtil.isNotEmpty(receiverOpenIds)) {
      articleDTO.setReceiverOpenIds(receiverOpenIds);
      List<WXMsgReceiver> receivers = new ArrayList<WXMsgReceiver>();
      for (String openId : receiverOpenIds) {
        WXMsgReceiver wxMsgReceiver = new WXMsgReceiver();
        wxMsgReceiver.setOpenId(openId);
        wxMsgReceiver.setMsgId(wxMsgDTO.getId());
        receivers.add(wxMsgReceiver);
      }
      wxService.saveWXMsgReceiver(receivers.toArray(new WXMsgReceiver[receivers.size()]));
    }
    //upload article img
    articleDTO.setWxMsgLocalId(wxMsgDTO.getId());
    uploadShopArticleImg(articleDTO);
    //do send custom msg
    articleDTO.setUrl(WXHelper.articleDetail(wxMsgDTO.getId()));
    return doSendCustomNewsMsg(articleDTO);
  }


  private Result doSendCustomNewsMsg(WXArticleDTO articleDTO) throws Exception {
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    List<WXUserDTO> userDTOs = getWXUserDTOByOpenIds(articleDTO.getReceiverOpenIds());
    StringBuffer errorMsg = new StringBuffer();
    for (WXUserDTO userDTO : userDTOs) {
      Result sendResult = sender.sendCustomNewsMsg(userDTO.getPublicNo(), userDTO.getOpenid(), articleDTO.toCustomArticle());
      if (sendResult == null || !sendResult.isSuccess()) {
        errorMsg.append("给微信用户 ").append(userDTO.getNickname()).append(" 发送失败，").append("请确认该用户已发送“演示”关键字到公共号\n");
      }
    }
    if (errorMsg.length() > 0) {
      return new Result(errorMsg.toString(), false, null);
    }
    return new Result("发送成功", true, null);
  }

  /**
   * 上传店铺发送消息的图片
   *
   * @param articleDTO
   * @throws Exception
   */
  @Override
  public void uploadShopArticleImg(WXArticleDTO articleDTO) throws Exception {
    if (articleDTO.getImgFile() == null || articleDTO.getWxMsgLocalId() == null) return;
    IImageService imageService = ServiceManager.getService(IImageService.class);
    String imagePath = UpYunManager.getInstance().generateUploadImagePath(articleDTO.getFromShopId(),
      articleDTO.getImgFile().getOriginalFilename());
    if (UpYunManager.getInstance().writeFile(imagePath, articleDTO.getImgFile().getBytes(), true,
      UpYunManager.getInstance().generateDefaultUpYunParams())) {
      Set<ImageType> imageTypes = new HashSet<ImageType>();
      imageTypes.add(ImageType.SHOP_WX_MSG_IMAGE);
      DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(articleDTO.getFromShopId(), articleDTO.getWxMsgLocalId(),
        DataType.SHOP_WX_MSG_IMAGE, ImageType.SHOP_WX_MSG_IMAGE, 1);
      dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(articleDTO.getFromShopId(), imagePath));
      imageService.saveOrUpdateDataImageDTOs(articleDTO.getFromShopId(), imageTypes, DataType.SHOP_WX_MSG_IMAGE,
        articleDTO.getWxMsgLocalId(), dataImageRelationDTO);
      UserWriter writer = daoManager.getWriter();
      Object status = writer.beginNew();
      try {
        IWXService wxService = ServiceManager.getService(WXService.class);
        WXMsgDTO wxMsgDTO = wxService.getWXMsgByIdAndShopId(articleDTO.getFromShopId(), articleDTO.getWxMsgLocalId());
        if (wxMsgDTO != null) {
          wxMsgDTO.setPicUrl(ConfigUtils.getUpYunDomainUrl() + dataImageRelationDTO.getImageInfoDTO().getPath());
          wxService.saveOrUpdateWXMsg(wxMsgDTO);
          writer.commit(status);
          articleDTO.setPicUrl(wxMsgDTO.getPicUrl());
        }
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public String getShopWelcomeWord(Long shopId, String openId) {
    if (shopId == null) {
      return WXConstant.CONTENT_SUBSCRIBE.replace("{B_URL}", WXHelper.vehicleBindUrl(openId));
    }
    ShopConfig shopConfig = ShopConfigCacheManager.getConfig(shopId, ShopConfigScene.WX_WELCOME_WORD);
    String content = shopConfig != null ? shopConfig.getValue() : null;
    //如果店铺未配置，使用系统默认欢迎词
    if (StringUtil.isEmpty(content)) {
      String shopName = ServiceManager.getService(IConfigService.class).getShopById(shopId).getName();
      return WXConstant.CONTENT_SCENE_SUBSCRIBE.replace("{NAME}", shopName).replace("{B_URL}", WXHelper.vehicleBindUrl(openId));
    }
    return content;
  }

  @Override
  public WXJsApiTicketSign getWXJsApiTicketSign(String publicNo, String url) throws Exception {
    String ticket = WXHelper.getJsApiTicketByPublicNo(publicNo);
    if (StringUtil.isEmpty(ticket)) {
      LOG.error("jsApiTicket is empty!");
      return null;
    }
    String noncestr = StringUtil.getRandomString(16);
    String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
    WXJsApiTicketSign ticketSign = new WXJsApiTicketSign(ticket, noncestr, timestamp, url);
    ticketSign.makeSignature();
    IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
    WXAccountDTO accountDTO = accountService.getCachedWXAccount(publicNo);
    ticketSign.setAppId(accountDTO.getAppId());
    return ticketSign;
  }

  @Override
  public List<WXUserVehicleDTO> getWXUserVehicleByOpenId(String... openId) throws Exception {
    UserWriter writer = daoManager.getWriter();
    List<WXUserVehicle> userVehicles = writer.getWXUserVehicleByOpenId(openId);
    if (CollectionUtil.isEmpty(userVehicles)) return null;
    List<WXUserVehicleDTO> userVehicleDTOs = new ArrayList<WXUserVehicleDTO>();
    for (WXUserVehicle userVehicle : userVehicles) {
      userVehicleDTOs.add(userVehicle.toDTO());
    }
    return userVehicleDTOs;
  }

  @Override
  public Map<String, List<WXUserVehicleDTO>> getWXUserVehicleMapByOpenIds(Set<String> openIds) throws Exception {
    Map<String, List<WXUserVehicleDTO>> openIdWxUserVehicleDTOsMap = new HashMap<String, List<WXUserVehicleDTO>>();
    if (CollectionUtils.isNotEmpty(openIds)) {
      UserWriter writer = daoManager.getWriter();
      List<WXUserVehicle> userVehicles = writer.getWXUserVehicleByOpenId(openIds.toArray(new String[openIds.size()]));
      if (CollectionUtils.isNotEmpty(userVehicles)) {
        for (WXUserVehicle wxUserVehicle : userVehicles) {
          if (wxUserVehicle != null && StringUtils.isNotBlank(wxUserVehicle.getOpenId())) {
            List<WXUserVehicleDTO> wxUserVehicleDTOs = openIdWxUserVehicleDTOsMap.get(wxUserVehicle.getOpenId());
            if (wxUserVehicleDTOs == null) {
              wxUserVehicleDTOs = new ArrayList<WXUserVehicleDTO>();
            }
            wxUserVehicleDTOs.add(wxUserVehicle.toDTO());
            openIdWxUserVehicleDTOsMap.put(wxUserVehicle.getOpenId(), wxUserVehicleDTOs);
          }
        }
      }
    }
    return openIdWxUserVehicleDTOsMap;
  }

  @Override
  public List<WXUserVehicleDTO> getWXUserVehicleByVehicleNo(String vehicleNo) throws Exception {
    if (StringUtil.isEmpty(vehicleNo)) return null;
    return getWXUserVehicle(null, vehicleNo);
  }

  @Override
  public List<WXUserVehicleDTO> getWXUserVehicle(String openId, String vehicleNo) throws Exception {
    if (StringUtil.isEmpty(openId) && StringUtil.isEmpty(vehicleNo)) {   //不能同时为空
      throw new Exception("illegal parameter");
    }
    UserWriter writer = daoManager.getWriter();
    List<WXUserVehicle> userVehicles = writer.getWXUserVehicle(openId, vehicleNo);
    if (CollectionUtil.isEmpty(userVehicles)) return null;
    List<WXUserVehicleDTO> userVehicleDTOs = new ArrayList<WXUserVehicleDTO>();
    for (WXUserVehicle userVehicle : userVehicles) {
      userVehicleDTOs.add(userVehicle.toDTO());
    }
    return userVehicleDTOs;
  }

  @Override
  public WXUserVehicle getWXUserVehicleById(Long userVehicleId) {
    if (userVehicleId == null) return null;
    UserWriter writer = daoManager.getWriter();
    WXUserVehicle uVehicle = writer.getWXUserVehicleById(userVehicleId);
    return uVehicle;
  }

  @Override
  public WXUserVehicleDTO getWXUserVehicleDTOById(Long userVehicleId) {
    if (userVehicleId == null) return null;
    WXUserVehicle uVehicle = getWXUserVehicleById(userVehicleId);
    return uVehicle != null ? uVehicle.toDTO() : null;
  }

  @Override
  public WXUserDTO getWXUserFromPlat(String publicNo, String openId) {
    try {
      if (StringUtil.isEmpty(openId)) return null;
      String accessToken = WXHelper.getAccessTokenByPublicNo(publicNo);
      if (StringUtil.isEmpty(accessToken)) {
        return null;
      }
      String url = WXConstant.URL_GET_WX_USER_INFO;
      url = url.replace("{ACCESS_TOKEN}", accessToken);
      HttpResponse response = HttpUtils.sendPost(url.replace("{OPENID}", openId));
      return JsonUtil.jsonToObj(response.getContent(), WXUserDTO.class);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public boolean synALLWXUserDTOs() throws Exception {
    List<WXAccountDTO> accountDTOs = ServiceManager.getService(WXAccountService.class).getAllWXAccount();
    if (CollectionUtil.isEmpty(accountDTOs)) return false;
    for (WXAccountDTO accountDTO : accountDTOs) {
      if (StringUtil.isEmpty(accountDTO.getPublicNo())) {
        LOG.error("wx:publicNo is empty");
        continue;
      }
      synUserDTOList(accountDTO.getPublicNo());
    }
    return true;
  }

  /**
   * 微信平台关注的用户同步到本地数据库
   *
   * @throws Exception
   */
  @Override
  public void synUserDTOList(String publicNo) throws Exception {
    List<WXUserDTO> userDTOs = getPlatFormWXUserList(publicNo);
    if (CollectionUtil.isEmpty(userDTOs)) return;
    LOG.info("wx:synUserDTOList publicNo is {},wxUser's size is {}", publicNo, userDTOs.size());
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (WXUserDTO userDTO : userDTOs) {
        WXUser user = CollectionUtil.getFirst(writer.getWXUserByOpenId(userDTO.getOpenid()));
        if (user == null) {
          user = new WXUser();
          user.setPublicNo(publicNo);
          user.fromDTO(userDTO);
          user.setDeleted(DeletedType.FALSE);
          writer.save(user);
        } else {
          userDTO.setId(user.getId());
          userDTO.setPublicNo(publicNo);
          userDTO.setDeleted(user.getDeleted());
          //订阅后取消订阅
          if ("0".equals(user.getSubscribe())) {
            continue;
          }
          user.fromDTO(userDTO);
          writer.update(user);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CreateWXGroupResult createGroup(Long shopId, String name) throws Exception {
    String accessToken = WXHelper.getAccessTokenByShopId(shopId);
    if (StringUtil.isEmpty(accessToken)) return null;
    String url = WXConstant.URL_CREATE_WX_GROUP.replace("{ACCESS_TOKEN}", accessToken);
    HttpResponse response = HttpUtils.sendPost(url, JsonUtil.objectToJson(new WXGroupJson(name)));
    CreateWXGroupResult result = JsonUtil.jsonToObj(response.getContent(), CreateWXGroupResult.class);
    if (StringUtil.isEmpty(result.getErrcode()) || ErrCode.SUCCESS.equals(result.getErrcode())) {
      return result;
    } else {
      LOG.error("wx:createGroup failed,errMsg is {}", result.getErrmsg());
      return null;
    }
  }


  public ErrCode addUserToGroup(Long shopId, String groupId, String openId) throws Exception {
    String accessToken = WXHelper.getAccessTokenByShopId(shopId);
    if (StringUtil.isEmpty(accessToken)) return null;
    String url = WXConstant.URL_UPDATE_WX_GROUP_USER.replace("{ACCESS_TOKEN}", accessToken);
    Map<String, String> data = new HashMap<String, String>();
    data.put("openid", openId);
    data.put("to_groupid", groupId);
    HttpResponse response = HttpUtils.sendPost(url, JsonUtil.objectToJson(data));
    CreateWXGroupResult result = JsonUtil.jsonToObj(response.getContent(), CreateWXGroupResult.class);
    if (ErrCode.SUCCESS.equals(result.getErrcode())) {
      return result;
    } else {
      LOG.error("wx:addUserToGroup failed,errMsg is {}", result.getErrmsg());
      return null;
    }
  }


  /**
   * 获取微信平台关注的粉丝
   *
   * @return
   * @throws Exception
   */
  @Override
  public List<WXUserDTO> getPlatFormWXUserList(String publicNo) throws Exception {
    List<String> openidList = getPlatFormWXUserOpenIds(publicNo);
    //组装详细信息
    if (CollectionUtil.isEmpty(openidList)) return null;
    String accessToken = WXHelper.getAccessTokenByPublicNo(publicNo);
    if (StringUtil.isEmpty(accessToken)) {
      return null;
    }
    List<WXUserDTO> userDTOs = new ArrayList<WXUserDTO>();
    String url = WXConstant.URL_GET_WX_USER_INFO;
    url = url.replace("{ACCESS_TOKEN}", accessToken);
    for (String openid : openidList) {
      String urlTemp = url.replace("{OPENID}", openid);
      HttpResponse response = HttpUtils.sendPost(urlTemp);
      WXUserDTO userDTO = JsonUtil.jsonToObj(response.getContent(), WXUserDTO.class);
      userDTO.setPublicNo(publicNo);
      userDTOs.add(userDTO);
    }
    return userDTOs;
  }

  public List<String> getPlatFormWXUserOpenIds(String publicNo) throws Exception {
    String accessToken = WXHelper.getAccessTokenByPublicNo(publicNo);
    if (StringUtil.isEmpty(accessToken)) {
      return null;
    }
    String url = WXConstant.URL_GET_WX_USERS;
    url = url.replace("{ACCESS_TOKEN}", accessToken);
    //获取openId list
    String next_openid = "";
    List<String> openidList = new ArrayList<String>();
    do {
      HttpResponse response = HttpUtils.sendPost(url + "&next_openid=" + next_openid);
      String content = response.getContent();
      GetWXUserListResult result = JsonUtil.jsonToObj(content, GetWXUserListResult.class);
      List<String> openids = result.getData().get("openid");
      next_openid = result.getNext_openid();
      if (CollectionUtil.isNotEmpty(openids)) {
        openidList.addAll(openids);
      }
    } while (StringUtil.isNotEmpty(next_openid));
    return openidList;
  }

  @Override
  public Result saveOrUpdateWXUserVehicle(WXUserVehicleDTO userVehicleDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      WXUserVehicle userVehicle = null;
      if (userVehicleDTO.getId() != null) {
        userVehicle = getWXUserVehicleById(userVehicleDTO.getId());
        if (userVehicle == null) {
          LOG.error("wx:saveOrUpdateWXUserVehicle,vehicle isn't exist,and id is {}", userVehicle.getId());
          return new Result(false, "车辆信息异常。");
        }
      } else {
        userVehicle = new WXUserVehicle();
      }
      userVehicle.fromDTO(userVehicleDTO);
      writer.saveOrUpdate(userVehicle);
      writer.commit(status);
      return new Result(true, "success");
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public Long saveOrUpdateWXUser(WXUserDTO userDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      WXUser user = null;
      if (userDTO.getId() == null) {
        user = new WXUser();
      } else {
        user = getWXUserById(userDTO.getId());
      }
      user.fromDTO(userDTO);
      writer.saveOrUpdate(user);
      writer.commit(status);
      userDTO.setId(user.getId());
      return user.getId();
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public void encryptAccount(String publicNo, String appSecret) throws Exception {
    if (StringUtil.isEmpty(publicNo)) return;
    byte[] key = WXHelper.getSecretKey();
    byte[] encryptData = EncryptionUtil.encrypt(appSecret.getBytes(), key);
    byte[] decryptData = EncryptionUtil.decrypt(encryptData, key);
    System.out.println("解密后数据: string:" + new String(decryptData));
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      WXAccount account = writer.getWXAccountByPublicNo(publicNo);
      account.setAppSecret(null);
      //      account.setAppSecret(encryptData);
      writer.update(account);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void encryptAllAccount() throws Exception {
    byte[] key = WXHelper.getSecretKey();
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      List<WXAccount> accounts = writer.getAllWXAccount();
      if (CollectionUtil.isNotEmpty(accounts)) {
        for (WXAccount account : accounts) {
          //          byte[] encryptData = EncryptionUtil.encrypt(account.getAppSecret(),key);
          account.setAppSecret(null);
          writer.update(account);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public int countWXUser(WXAccountType accountType) {
    UserWriter writer = daoManager.getWriter();
    return writer.countWXUser(accountType);
  }

  @Override
  public List<WXUserDTO> getWXUserDTOByPager(WXAccountType accountType, Pager pager) {
    UserWriter writer = daoManager.getWriter();
    List<WXUser> users = writer.getWXUserByPager(accountType, pager);
    if (CollectionUtil.isEmpty(users)) return null;
    List<WXUserDTO> userDTOs = new ArrayList<WXUserDTO>();
    for (WXUser user : users) {
      userDTOs.add(user.toDTO());
    }
    return userDTOs;
  }

  @Override
  public List<WXUserDTO> getWXUserDTOByVehicleNo(String vehicleNo) {
    UserWriter writer = daoManager.getWriter();
    List<WXUser> users = writer.getWXUserByVehicleNo(vehicleNo);
    if (CollectionUtil.isEmpty(users)) return null;
    List<WXUserDTO> userDTOs = new ArrayList<WXUserDTO>();
    for (WXUser user : users) {
      userDTOs.add(user.toDTO());
    }
    return userDTOs;
  }

  @Override
  public List<WXUserDTO> getWXUserDTOByShopId(Long shopId) {
    UserWriter writer = daoManager.getWriter();
    List<WXUser> users = writer.getWXUserByShopId(shopId);
    if (CollectionUtil.isEmpty(users)) return null;
    List<WXUserDTO> userDTOs = new ArrayList<WXUserDTO>();
    for (WXUser user : users) {
      userDTOs.add(user.toDTO());
    }
    return userDTOs;
  }

  @Override
  public List<WXUserDTO> getWXUserDTOByPublicNo(String publicNo) {
    if (StringUtil.isEmpty(publicNo)) return null;
    UserWriter writer = daoManager.getWriter();
    List<WXUser> users = writer.getWXUserDTOByPublicNo(publicNo);
    if (CollectionUtil.isEmpty(users)) return null;
    List<WXUserDTO> userDTOs = new ArrayList<WXUserDTO>();
    for (WXUser user : users) {
      userDTOs.add(user.toDTO());
    }
    return userDTOs;
  }

  @Override
  public List<WXUserDTO> getWXUserDTOByOpenIds(String... openIds) {
    if (ArrayUtil.isEmpty(openIds)) return null;
    UserWriter writer = daoManager.getWriter();
    List<WXUser> users = writer.getWXUserByOpenId(openIds);
    if (CollectionUtil.isEmpty(users)) return null;
    List<WXUserDTO> userDTOs = new ArrayList<WXUserDTO>();
    for (WXUser user : users) {
      userDTOs.add(user.toDTO());
    }
    return userDTOs;
  }

  @Override
  public WXUserDTO getWXUserDTOByOpenId(String openId) {
    if (StringUtil.isEmpty(openId)) return null;
    return CollectionUtil.getFirst(getWXUserDTOByOpenIds(openId));
  }

  private WXUser getWXUserById(Long id) {
    UserWriter writer = daoManager.getWriter();
    return writer.getById(WXUser.class, id);
  }

  @Override
  public WXUserDTO getWXUserDTOById(Long id) {
    WXUser wxUser = getWXUserById(id);
    return wxUser != null ? wxUser.toDTO() : null;
  }

  @Override
  public boolean isUserExist(String openId) {
    return getWXUserDTOByOpenId(openId) != null ? true : false;
  }


  private byte[] blobToBytes(Blob blob) throws IOException {

    BufferedInputStream is = null;

    try {
      is = new BufferedInputStream(blob.getBinaryStream());
      byte[] bytes = new byte[(int) blob.length()];
      int len = bytes.length;
      int offset = 0;
      int read = 0;

      while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
        offset += read;
      }
      return bytes;
    } catch (Exception e) {
      return null;
    } finally {
      try {
        is.close();
        is = null;
      } catch (IOException e) {
        return null;
      }
    }
  }


  /**
   * 已经关联的店铺
   *
   * @param openId
   * @return
   */
  @Override
  public List<ShopWXUserDTO> getShopWXUserByOpenId(String openId) {
    UserWriter writer = daoManager.getWriter();
    List<ShopWXUser> shopWXUsers = writer.getShopWXUserByOpenId(openId);
    if (CollectionUtil.isEmpty(shopWXUsers)) return null;
    List<ShopWXUserDTO> dtoList = new ArrayList<ShopWXUserDTO>();
    for (ShopWXUser shopWXUser : shopWXUsers) {
      dtoList.add(shopWXUser.toDTO());
    }
    return dtoList;
  }

  @Override
  public List<ShopWXUserDTO> getShopWXUserByShopId(Long shopId) {
    UserWriter writer = daoManager.getWriter();
    List<ShopWXUser> shopWXUsers = writer.getShopWXUserByShopId(shopId);
    if (CollectionUtil.isEmpty(shopWXUsers)) return null;
    List<ShopWXUserDTO> dtoList = new ArrayList<ShopWXUserDTO>();
    for (ShopWXUser shopWXUser : shopWXUsers) {
      dtoList.add(shopWXUser.toDTO());
    }
    return dtoList;
  }

  @Override
  public ShopWXUserDTO getShopWXUserInfo(String openId, String publicNo) {
    UserWriter writer = daoManager.getWriter();
    Object[] objects = writer.getShopWXUserInfo(openId, publicNo);
    if (ArrayUtil.isEmpty(objects)) return null;
    WXUser user = (WXUser) objects[0];
    ShopWXUser shopWXUser = (ShopWXUser) objects[1];
    ShopWXUserDTO shopWXUserDTO = shopWXUser.toDTO();
    shopWXUserDTO.setOpenId(user.getOpenId());
    return shopWXUserDTO;
  }

  @Override
  public ShopWXUserDTO getShopWXUser(Long shopId, String openId) {
    UserWriter writer = daoManager.getWriter();
    ShopWXUser user = writer.getShopWXUser(shopId, openId);
    return user != null ? user.toDTO() : null;
  }


  @Override
  public Long saveOrUpdateShopWXUser(ShopWXUserDTO shopWXUserDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      if (shopWXUserDTO.getShopId() == null || StringUtil.isEmpty(shopWXUserDTO.getOpenId())) {
        return null;
      }
      ShopWXUser shopWXUser = null;
      if (shopWXUserDTO.getId() == null) {
        shopWXUser = new ShopWXUser();
      } else {
        shopWXUser = writer.getById(ShopWXUser.class, shopWXUserDTO.getId());
      }
      shopWXUser.fromDTO(shopWXUserDTO);
      writer.saveOrUpdate(shopWXUser);
      writer.commit(status);
      shopWXUserDTO.setId(shopWXUser.getId());
      return shopWXUser.getId();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateAppWXUser(AppWXUserDTO appWXUserDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      if (appWXUserDTO == null || StringUtil.isEmpty(appWXUserDTO.getAppUserNo())) {
        return;
      }
      AppWXUser appWXUser = null;
      if (appWXUserDTO.getId() == null) {
        appWXUser = new AppWXUser();
      } else {
        appWXUser = writer.getById(AppWXUser.class, appWXUserDTO.getId());
      }
      appWXUser.fromDTO(appWXUserDTO);
      writer.saveOrUpdate(appWXUser);
      writer.commit(status);
      appWXUserDTO.setId(appWXUser.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<AppWXUserDTO> getAppWXUserDTO(String appUserNo, String openId) {
    UserWriter writer = daoManager.getWriter();
    List<AppWXUser> appWXUsers = writer.getAppWXUser(appUserNo, openId);
    List<AppWXUserDTO> appWXUserDTOs = new ArrayList<AppWXUserDTO>();
    if (CollectionUtil.isNotEmpty(appWXUsers)) {
      for (AppWXUser user : appWXUsers) {
        appWXUserDTOs.add(user.toDTO());
      }
    }
    return appWXUserDTOs;
  }

  @Override
  public List<AppWXUserDTO> getAppWXUserDTOByOpenId(String openId) {
    return getAppWXUserDTO(null, openId);
  }


  @Override
  public List<AppWXUserDTO> getAppWXUserDTOByAppUserNo(String appUserNo) {
    if(StringUtil.isEmpty(appUserNo)) return null;
    return getAppWXUserDTO(appUserNo, null);
  }

  /**
   * 保存店铺和粉丝的关系
   *
   * @param publicNo
   * @param openId
   * @param shopId
   * @return
   */
  @Override
  public Result saveShopWXUser(String publicNo, String openId, Long shopId) {
    //同步和店铺的关系
    if (shopId == null) return new Result("illegal param", false);
    ShopWXUserDTO sUserDTO = getShopWXUser(shopId, openId);
    if (sUserDTO != null) return new Result("shop wx_user is existed", false);
    ShopWXUserDTO shopWXUserDTO = new ShopWXUserDTO();
    shopWXUserDTO.setOpenId(openId);
    shopWXUserDTO.setPublicNo(publicNo);
    shopWXUserDTO.setShopId(shopId);
    shopWXUserDTO.setDeleted(DeletedType.FALSE);
    saveOrUpdateShopWXUser(shopWXUserDTO);
    return new Result();
  }

  @Override
  public Result doUnBindVehicle(String openId, String vehicleNo) throws Exception {
    Result result = new Result();
    if (!RegexUtils.isVehicleNo(vehicleNo)) {
      return result.LogErrorMsg("抱歉，您输入的车牌号“" + vehicleNo + "”不符合车牌号格式，请检查后重新输入。");
    }
    //解除车的关联
    WXUserVehicleDTO userVehicleDTO = CollectionUtil.getFirst(getWXUserVehicle(openId, vehicleNo));
    if (userVehicleDTO == null) {
      return result.LogErrorMsg("您输入的车牌号“" + vehicleNo + "”不存在或已经解绑。");
    }
    userVehicleDTO.setDeleted(DeletedType.TRUE);
    saveOrUpdateWXUserVehicle(userVehicleDTO);
    result.setMsg("您输入的车牌号“" + vehicleNo + "”,解绑成功。");
    return result;
  }

  @Override
  public int countWxUserBySearchCondition(WXUserSearchCondition condition) {
    if (condition == null) {
      return 0;
    }
    UserWriter writer = daoManager.getWriter();
    return writer.countWxUsersBySearchCondition(condition);

  }

  @Override
  public int countMyFans(WXUserSearchCondition condition) {
    if (condition == null) {
      return 0;
    }
    UserWriter writer = daoManager.getWriter();
    return writer.countMyFans(condition);
  }


  @Override
  public List<WXUserDTO> getWxUserDTOsBySearchCondition(WXUserSearchCondition condition, Pager pager) {
    if (condition == null || (pager != null && pager.getTotalRows() == 0)) {
      return new ArrayList<WXUserDTO>();
    }
    UserWriter writer = daoManager.getWriter();
    List<WXUser> wxUsers = writer.getMyFans(condition, pager);
    List<WXUserDTO> wxUserDTOs = new ArrayList<WXUserDTO>();
    for (WXUser shopWXUser : wxUsers) {
      wxUserDTOs.add(shopWXUser.toDTO());
    }
    return wxUserDTOs;
  }

  @Override
  public List<WXUserDTO> getMyFans(WXUserSearchCondition condition, Pager pager) {
    if (condition == null || (pager != null && pager.getTotalRows() == 0)) {
      return new ArrayList<WXUserDTO>();
    }
    UserWriter writer = daoManager.getWriter();
    List<WXUser> wxUsers = writer.getMyFans(condition, pager);
    List<WXUserDTO> wxUserDTOs = new ArrayList<WXUserDTO>();
    for (WXUser shopWXUser : wxUsers) {
      wxUserDTOs.add(shopWXUser.toDTO());
    }
    return wxUserDTOs;
  }

  @Override
  public void generateVehicleInfo(Long shopId, List<WXUserDTO> wxUserDTOs) throws Exception {
    if (shopId == null || CollectionUtils.isEmpty(wxUserDTOs)) {
      return;
    }
    Set<String> wxUserOpenIds = new HashSet<String>();
    for (WXUserDTO wxUserDTO : wxUserDTOs) {
      if (wxUserDTO != null && StringUtils.isNotBlank(wxUserDTO.getOpenid())) {
        wxUserOpenIds.add(wxUserDTO.getOpenid());
      }
    }
    Set<String> vehicleNos = new HashSet<String>();
    Map<String, List<WXUserVehicleDTO>> openIdWxUserVehicleDTOsMap = getWXUserVehicleMapByOpenIds(wxUserOpenIds);
    if (MapUtils.isNotEmpty(openIdWxUserVehicleDTOsMap)) {
      for (List<WXUserVehicleDTO> wxUserVehicleDTOs : openIdWxUserVehicleDTOsMap.values()) {
        if (CollectionUtils.isNotEmpty(wxUserVehicleDTOs)) {
          for (WXUserVehicleDTO wxUserVehicleDTO : wxUserVehicleDTOs) {
            if (wxUserVehicleDTO != null && StringUtils.isNotBlank(wxUserVehicleDTO.getVehicleNo())) {
              vehicleNos.add(wxUserVehicleDTO.getVehicleNo());
            }
          }
        }
      }
    }
    Map<String, WXFanDTO> vehicleNoWxFanDTO = getWxShopVehicleMapByVehicleNos(shopId, vehicleNos);
    for (WXUserDTO wxUserDTO : wxUserDTOs) {
      if (wxUserDTO != null && StringUtils.isNotBlank(wxUserDTO.getOpenid())) {
        List<WXUserVehicleDTO> wxUserVehicleDTOs = openIdWxUserVehicleDTOsMap.get(wxUserDTO.getOpenid());
        List<WXFanDTO> wxFanDTOs = new ArrayList<WXFanDTO>();
        if (CollectionUtils.isNotEmpty(wxUserVehicleDTOs)) {
          for (WXUserVehicleDTO wxUserVehicleDTO : wxUserVehicleDTOs) {
            if (StringUtils.isNotBlank(wxUserVehicleDTO.getVehicleNo())) {
              WXFanDTO wxFanDTO = vehicleNoWxFanDTO.get(wxUserVehicleDTO.getVehicleNo());
              if (wxFanDTO == null) {
                wxFanDTO = new WXFanDTO();
                wxFanDTO.setLicenceNo(wxUserVehicleDTO.getVehicleNo());
              }
              wxFanDTOs.add(wxFanDTO);
            }
          }
        }
        wxUserDTO.setWxFanDTOs(wxFanDTOs);
      }
    }
  }

  @Override
  public Map<String, WXFanDTO> getWxShopVehicleMapByVehicleNos(Long shopId, Set<String> vehicleNos) {
    Map<String, WXFanDTO> vehicleNoWXFanDTOMap = new HashMap<String, WXFanDTO>();
    if (shopId == null || CollectionUtils.isEmpty(vehicleNos)) {
      return vehicleNoWXFanDTOMap;
    }
    UserWriter writer = daoManager.getWriter();
    List<WXFanDTO> wxFanDTOs = writer.getShopWxUserVehicleInfo(shopId, vehicleNos);
    if (CollectionUtils.isNotEmpty(wxFanDTOs)) {
      for (WXFanDTO wxFanDTO : wxFanDTOs) {
        if (wxFanDTO != null && StringUtils.isNotBlank(wxFanDTO.getLicenceNo())) {
          vehicleNoWXFanDTOMap.put(wxFanDTO.getLicenceNo(), wxFanDTO);
        }
      }
    }
    return vehicleNoWXFanDTOMap;
  }

}