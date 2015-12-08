package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.api.response.*;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.AppConstant;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.Switch;
import com.bcgogo.enums.UploadStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.etl.ImpactDTO;
import com.bcgogo.etl.common.XConstant;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IHandleAppVehicleFaultCodeService;
import com.bcgogo.user.ImpactAndVideoDTO;
import com.bcgogo.user.ImpactVideoBlockFilter;
import com.bcgogo.user.ImpactVideoDTO;
import com.bcgogo.user.model.app.OBD;
import com.bcgogo.user.model.app.ObdUserVehicle;
import com.bcgogo.user.service.IImpactService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.user.service.wx.IWXAccountService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.utils.ftp.SFTPHelper;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import com.bcgogo.wx.user.AppWXUserDTO;
import com.jcraft.jsch.JSchException;
import com.sshtools.j2ssh.net.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;


/**
 * 车辆碰撞处理
 * <p/>
 * Author: ndong
 * Date: 2015-4-17
 * Time: 16:57
 */
@Controller
public class ImpactController {
  private static final Logger LOG = LoggerFactory.getLogger(ImpactController.class);

  private Map<String , Long> map = new HashMap<String, Long>();
  /**
   * 保存碰撞数据
   *
   * @param request
   * @param response
   * @param uFile
   * @param param
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/impact/collect", method = RequestMethod.POST)
  public ApiResponse collect(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("file") MultipartFile uFile, String param) {
    try {
      if (StringUtil.isEmpty(param)) {
        return MessageCode.toApiResponse(MessageCode.UPLOAD_PARAM_EMPTY);
      }
      ImpactDTO impactDTO = JsonUtil.fromJson(param, ImpactDTO.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      LOG.info("collect impact data,appUserNo={},uuid={}", appUserNo, impactDTO.getUuid());
      boolean needUpload = true;
      GsmVehicleDataDTO[] dataDTOs = impactDTO.getData();
      if (ArrayUtil.isNotEmpty(dataDTOs)) {
        if (impactDTO.getType() == 0) {
          //1.加速度值是否满足条件
          for (int i = 0; i < dataDTOs.length; i++) {
            GsmVehicleDataDTO dataDTO = dataDTOs[i];
            double x = NumberUtil.addition(dataDTO.getGx(), dataDTO.getGy(), dataDTO.getGz());
            LOG.info("碰撞条件不足,total x:{}", x);
            if (x < 30.0) {
              needUpload = false;
              break;
            }
          }
          //2.速度是否递减
          if (needUpload && dataDTOs.length > 2) {
            double begin = Double.valueOf(dataDTOs[0].getVss());
            double end = Double.valueOf(dataDTOs[dataDTOs.length - 1].getVss());
            if (end > begin) {
              needUpload = false;
            }
          }
        } else if (impactDTO.getType() == 1) {
          //停车监控模式下判断最后一次熄火时间距离当前时间是否超过2分钟
          IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
          GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(appUserNo);
          Long interval = 2 * 60 * 1000L;//todo 改成可配置的
          if (GSMConstant.CUTOFF.equals(gsmVehicleDataDTO.getVehicleStatus()) && (System.currentTimeMillis() - gsmVehicleDataDTO.getUploadTime()) < interval) {
            needUpload = false;
          }
        }
      }
      if (!needUpload) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_CONDITION_NOT_ENOUGH);
      }
      impactDTO.setAppUserNo(appUserNo);
      IImpactService impactService = ServiceManager.getService(IImpactService.class);
      String uuid = impactDTO.getUuid();
      if (StringUtil.isEmpty(uuid)) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_UUID_EMPTY);
      }
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
      impactDTO.setShopId(appUserDTO.getRegistrationShopId());
      //纠正上传时间
      if (impactDTO.getUploadTime() != null) {
        Long diffTime = System.currentTimeMillis() - impactDTO.getUploadTime();
        if (diffTime > XConstant.ERROR_DELAY_UPLOAD_TIME || diffTime < XConstant.ERROR_EARLIER_UPLOAD_TIME) {
          LOG.info("fix the uploadTime:{}s", diffTime / 1000);
          impactDTO.setUploadTime(System.currentTimeMillis());
        }
      } else {
        impactDTO.setUploadTime(System.currentTimeMillis());
      }
      //保存碰撞数据
      impactService.saveImpactCollectData(impactDTO);
      //save image
      String backupPath = ServiceManager.getService(IConfigService.class).getConfig("video_backup_path", ShopConstant.BC_SHOP_ID);
      String filePath = backupPath + uuid;
      File dFile = new File(filePath);
      if (!dFile.isDirectory()) {
        LOG.info("mirror:mkdir,path:{}", filePath);
        dFile.mkdir();
      }
      //块号做文件名
      String fileName = uuid + AppConstant.IMAGE_FORMAT;
      String blockPath = (dFile.getPath() + FileUtil.getOSDisk() + fileName);
      LOG.info("blockPath is {}", blockPath);
      uFile.transferTo(new File(blockPath));
      //保存故障码
      if (ArrayUtil.isNotEmpty(dataDTOs)) {
        StringBuilder sb = new StringBuilder();
        int len = dataDTOs.length;
        for (int i = 0; i < len; i++) {
          GsmVehicleDataDTO dataDTO = dataDTOs[i];
          sb.append(dataDTO.getRdtc());
          if (i < len - 1) {
            sb.append(",");
          }
        }
        String rdtc = sb.toString();
        IHandleAppVehicleFaultCodeService iHandleAppVehicleFaultCodeService = ServiceManager.getService(IHandleAppVehicleFaultCodeService.class);
        ObdUserVehicle obdUserVehicle = impactService.getObdUserVehicle(impactDTO.getAppUserNo());
        OBD obd = impactService.getObdById(obdUserVehicle.getObdId());
        iHandleAppVehicleFaultCodeService.sendFaultCode(obd.getImei(), rdtc, impactDTO.getUploadTime());
      }
      //发送微信碰撞报警通知
//      String impactSwitch = ServiceManager.getService(IConfigService.class).getConfig("impact_notify_switch", ShopConstant.BC_SHOP_ID);
//      LOG.info("impactSwitch= {} ", impactSwitch);
//      if (Switch.ON.toString().equals(impactSwitch)) {
//        LOG.info("sendMirrorImpactRemindTemplate");
//        IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
//        List<AppWXUserDTO> userDTOs = wxUserService.getAppWXUserDTO(appUserNo, null);
//        if (CollectionUtil.isNotEmpty(userDTOs)) {
//          sendMirrorImpactRemindTemplate(impactDTO, userDTOs.toArray(new AppWXUserDTO[userDTOs.size()]));
//        } else {
//          LOG.info("send AppWXUserDTO size is empty");
//        }
//      }
      return MessageCode.toApiResponse(MessageCode.IMPACT_DATA_SAVE_SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED, e.getMessage());
    }
  }

  /**
   * 发送微信碰撞报警通知
   *
   * @param impactDTO
   * @param userDTOs
   * @throws Exception
   */
  private void sendMirrorImpactRemindTemplate(ImpactDTO impactDTO, AppWXUserDTO... userDTOs) throws Exception {
    if (ArrayUtil.isEmpty(userDTOs)) {
      return;
    }
    IWXAccountService accountService = ServiceManager.getService(IWXAccountService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IGeocodingService iGeocodingService = ServiceManager.getService(IGeocodingService.class);
    AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(impactDTO.getAppUserNo()));
    String category;
    if (impactDTO.getType() == 1) {
      category = "普通碰撞";
    } else {
      category = "停车监控";
    }
    for (AppWXUserDTO userDTO : userDTOs) {
      String address = iGeocodingService.gpsCoordinate2FullAddress(StringUtil.valueOf(impactDTO.getLat()), StringUtil.valueOf(impactDTO.getLon()));
      String date = DateUtil.convertDateLongToDateString(DateUtil.ALL, impactDTO.getUploadTime());
      String publicNo = accountService.getWXAccountByOpenId(userDTO.getOpenId()).getPublicNo();
      WXMsgTemplate template = WXHelper.getMirrorImpactRemindTemplate(publicNo, userDTO.getOpenId(), appVehicleDTO.getVehicleNo(), category, address, date);
      if (template != null) {
        template.setUrl(WXHelper.mirrorVideoUrl(userDTO.getOpenId()));
        Result result = ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(publicNo, template);
        if (!result.isSuccess()) {
          LOG.error("send mirrorImpactRemindTemplate failed,msg is", result.getMsg());
        }
      }
    }
  }


  /**
   * 上传碰撞视频初始化
   *
   * @param request
   * @param response
   * @param pFile
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/impact/uploadInit", method = RequestMethod.PUT)
  public ApiResponse uploadInit(HttpServletRequest request, HttpServletResponse response, @RequestBody PFile pFile) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    try {
      String uuid = pFile.getUuid();
      if (StringUtil.isEmpty(uuid)) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_UUID_EMPTY);
      }
      ImpactDTO impactDTO = impactService.getImpactDTOByUUID(uuid);
      if (impactDTO == null) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_DATA_NOT_EXIST);
      }
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      int count = impactService.statImpactVideo(impactDTO.getAppUserNo(), DateUtil.getStartTimeOfMonth(0), System.currentTimeMillis());
      if (!"2cbe40296497ad4804226fbf9c831b2c".equals(appUserNo) && count > ConfigUtils.getImpactVideoUploadLimit()) {
        return MessageCode.toApiResponse(MessageCode.VIDEO_UPLOAD_OVER_THE_MOUTH_LIMIT);
      }
      ImpactVideoDTO impactVideoDTO = new ImpactVideoDTO();
      String uploadFlag = GSMConstant.UPLOAD_FLAG_YES;
      UploadStatus uploadStatus = UploadStatus.UPLOADING;
      String msg = null;
      String uploadSwitch = ServiceManager.getService(IConfigService.class).getConfig("impact_video_upload_switch", ShopConstant.BC_SHOP_ID);
      LOG.info("mirror:impact_video_upload_switch={}", uploadSwitch);
      if (Switch.OFF.toString().equals(uploadSwitch)) {
        uploadFlag = GSMConstant.UPLOAD_FLAG_NO;
        uploadStatus = UploadStatus.UPLOAD_SWITCH_OFF;
        msg = UploadStatus.UPLOAD_SWITCH_OFF.getStatus();
      }
      impactVideoDTO.setUuid(uuid);
      impactVideoDTO.setSize(pFile.getTotalLength());
      impactVideoDTO.setBlockNum(pFile.getBlockNumber());
      String name = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.ALL) + " 碰撞视频";
      impactVideoDTO.setName(name);
      impactVideoDTO.setAppUserNo(appUserNo);
      impactVideoDTO.setUploadTime(System.currentTimeMillis());
      impactVideoDTO.setUploadStatus(uploadStatus);
      impactVideoDTO.setDeleted(DeletedType.FALSE);
      impactService.saveOrUpdateImpactVideo(impactVideoDTO);
      //发送微信碰撞报警通知
      String impactSwitch = ServiceManager.getService(IConfigService.class).getConfig("impact_notify_switch", ShopConstant.BC_SHOP_ID);
      LOG.info("impactSwitch= {} ", impactSwitch);
      if (Switch.ON.toString().equals(impactSwitch)) {
        LOG.info("sendMirrorImpactRemindTemplate");
        IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
        List<AppWXUserDTO> userDTOs = wxUserService.getAppWXUserDTO(appUserNo, null);
        if (CollectionUtil.isNotEmpty(userDTOs)) {
          sendMirrorImpactRemindTemplate(impactDTO, userDTOs.toArray(new AppWXUserDTO[userDTOs.size()]));
        } else {
          LOG.info("send AppWXUserDTO size is empty");
        }
      }
      return new ApiVideoInitResponse(uploadFlag, msg, impactDTO.getId());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VIDEO_UPLOAD_FAIL);
    }
  }

  /**
   * 上传碰撞视频
   *
   * @param request
   * @param uFile
   * @param param
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/impact/uploadVideo", method = RequestMethod.POST)
  public ApiResponse fileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile uFile, String param) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    ImpactVideoDTO impactVideoDTO = null;
    try {
      if (StringUtil.isEmpty(param)) {
        return MessageCode.toApiResponse(MessageCode.UPLOAD_PARAM_EMPTY);
      }
      if (uFile.isEmpty()) {
        return MessageCode.toApiResponse(MessageCode.VIDEO_SIZE_EMPTY);
      }
      PFileBlock block = JsonUtil.fromJson(param, PFileBlock.class);
      String uuid = block.getUuid();
      LOG.info("uploadVideo uuid={}", uuid);
      LOG.info("uploadVideo,Seq1={},Seq2={}", block.getSeq1(), block.getSeq2());
      if (StringUtil.isEmpty(uuid)) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_UUID_EMPTY);
      }
      impactVideoDTO = impactService.getImpactVideoDTOByUUID(uuid);
      if (impactVideoDTO == null) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_INIT_DATA_NOT_EXIST);
      }
      if (block.getCrc() == null) {
        return MessageCode.toApiResponse(MessageCode.FILE_CRC_IS_EMPTY);
      }
      if (!block.getCrc().equals(FileUtil.calcCRC32CheckSum(uFile.getBytes()))) {
        return MessageCode.toApiResponse(MessageCode.FILE_CRC_CHECK_SUM_EXCEPTION);
      }
      impactVideoDTO.setSize(uFile.getSize());
      String backupPath = ServiceManager.getService(IConfigService.class).getConfig("video_backup_path", ShopConstant.BC_SHOP_ID);
      impactVideoDTO.setBackupPath(backupPath);
      String filePath = backupPath + uuid;
      File dFile = new File(filePath);
      if (!dFile.isDirectory()) {
        LOG.info("mirror:mkdir,path:{}", filePath);
        dFile.mkdir();
      }
      //块号做文件名
      int blockName = block.getSeq1();
      String blockPath = (dFile.getPath() + FileUtil.getOSDisk() + blockName);
      LOG.info("blockPath is {}", blockPath);
      uFile.transferTo(new File(blockPath));
      //传输结束后，组装块文件并上传到视频服务器
      if ((block.getSeq2() + 1) >= impactVideoDTO.getBlockNum()) {
        uploadFile(impactVideoDTO);
        LOG.info("uploadFile finished");
      }
      return MessageCode.toApiResponse(MessageCode.SUCCESS);
    } catch (Exception e) {
      if (impactVideoDTO != null) {
        impactVideoDTO.setUploadStatus(UploadStatus.EXCEPTION);
        impactService.saveOrUpdateImpactVideo(impactVideoDTO);
      }
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VIDEO_UPLOAD_FAIL);
    }
  }

  /**
   * 保存碰撞数据-判断gx,gy,gz的值判断要不要上传碰撞视频
   *
   * @param device:设备类型1--一代，2--二代，2S--2S
   * @param vid                           :车辆ID
   * @param cid                           :碰撞ID
   * @param uFile                         :碰撞产生的图片
   * @param param                         :碰撞采集的数据
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/dev/{device}/vehicle/{vid}/collision/{cid}", method = RequestMethod.POST)
  public ApiResponse collectV2(@PathVariable String device, @PathVariable String vid, @PathVariable String cid,
                               @RequestParam("file") MultipartFile uFile, String param,
                               HttpServletRequest request, HttpServletResponse response) {
    try {
      if (StringUtil.isEmpty(param)) {
        return MessageCode.toApiResponse(MessageCode.UPLOAD_PARAM_EMPTY);
      }
      //1.是测试账号直接上传
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      if ("2cbe40296497ad4804226fbf9c831b2c".equals(appUserNo)) {
        return new ApiVideoInitResponse("0", "测试账号，无限上传", null);
      }
      String uploadFlag = "1";
      String msg = "";
      boolean needUpload = true;    //默認不上傳
      ImpactDTO impactDTO = JsonUtil.fromJson(param, ImpactDTO.class);
      GsmVehicleDataDTO[] dataDTO_list = impactDTO.getData();

      //2.该账号本月已经超出最大上传限制，不上传
      IImpactService impactService = ServiceManager.getService(IImpactService.class);
      int count = impactService.statImpactVideo(impactDTO.getAppUserNo(), DateUtil.getStartTimeOfMonth(0), System.currentTimeMillis());
      if (count > ConfigUtils.getImpactVideoUploadLimit()) {
        return MessageCode.toApiResponse(MessageCode.VIDEO_UPLOAD_OVER_THE_MOUTH_LIMIT);
      }
      //3.对正常行驶状态的碰撞做逻辑判断
      if (impactDTO.getType() == 0 && ArrayUtil.isNotEmpty(dataDTO_list)) {
        //1.加速度值是否满足条件
        boolean flag = false;
        for (int i = 0; i < dataDTO_list.length; i++) {
          GsmVehicleDataDTO dataDTO = dataDTO_list[i];
          double x = 0.0;
          if (StringUtil.isNotEmpty(dataDTO.getGx())) {
            x = Double.valueOf(dataDTO.getGx());
          }
          if (StringUtil.isNotEmpty(dataDTO.getGy())) {
            x += Double.valueOf(dataDTO.getGy());
          }
          if (StringUtil.isNotEmpty(dataDTO.getGz())) {
            x += Double.valueOf(dataDTO.getGz());
          }
          LOG.info("x is small,x:{}",x);
          if (x > 30.0) {
            flag = true;
            break;
          }
        }
        needUpload = flag;
        if (!flag) {
          msg = "GSensor不满足条件,不需上传";
        }
        //2.速度是否递减
        if (needUpload && dataDTO_list.length > 2) {
          double begin = Double.valueOf(dataDTO_list[0].getVss());
          double end = Double.valueOf(dataDTO_list[dataDTO_list.length - 1].getVss());
          if (end > begin) {
            LOG.info("不满足速度递减,begin:{},end:{}", begin, end);
            msg = "车速不满足条件,不需上传";
            needUpload = false;
          }
        }
      } else if (impactDTO.getType() == 1 && ArrayUtil.isNotEmpty(dataDTO_list)) {
        //1.加速度值是否满足条件
        boolean flag = false;
        for (int i = 0; i < dataDTO_list.length; i++) {
          GsmVehicleDataDTO dataDTO = dataDTO_list[i];
          double x = 0.0;
          if (StringUtil.isNotEmpty(dataDTO.getGx())) {
            x = Double.valueOf(dataDTO.getGx());
          }
          if (StringUtil.isNotEmpty(dataDTO.getGy())) {
            x += Double.valueOf(dataDTO.getGy());
          }
          if (StringUtil.isNotEmpty(dataDTO.getGz())) {
            x += Double.valueOf(dataDTO.getGz());
          }
          LOG.info("x is small,x:{}", x);
          if (x > 130.0) {
            flag = true;
            break;
          }
        }

        needUpload = false;//flag;
        if (!flag) {
          msg = "停车监控状态下GSensor不满足条件,不需上传";
        }

        if (needUpload) {
          //停车监控模式下判断最后一次熄火时间距离当前时间是否超过2分钟
          IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
          GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(appUserNo);
          Long interval = 5 * 60 * 1000L;//todo 改成可配置的
          if (GSMConstant.CUTOFF.equals(gsmVehicleDataDTO.getVehicleStatus()) && (System.currentTimeMillis() - gsmVehicleDataDTO.getUploadTime()) < interval) {
            needUpload = false;
            msg = "停车监控在熄火2分钟内不上传";
          }
        }

      }

      if (needUpload) {
        impactDTO.setAppUserNo(appUserNo);
        //String uuid = impactDTO.getUuid();
        if (StringUtil.isEmpty(cid)) {
          return MessageCode.toApiResponse(MessageCode.IMPACT_UUID_EMPTY);
        }
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
        impactDTO.setShopId(appUserDTO.getRegistrationShopId());
        impactDTO.setUploadTime(System.currentTimeMillis());
        //保存碰撞数据
        impactService.saveImpactCollectData(impactDTO);
        //save image
        String backupPath = ServiceManager.getService(IConfigService.class).getConfig("video_backup_path", ShopConstant.BC_SHOP_ID);
        String filePath = backupPath + cid;
        File dFile = new File(filePath);
        if (!dFile.isDirectory()) {
          LOG.info("mirror:mkdir,path:{}", filePath);
          dFile.mkdir();
        }
        //块号做文件名
        String fileName = cid + AppConstant.IMAGE_FORMAT;
        String blockPath = (dFile.getPath() + FileUtil.getOSDisk() + fileName);
        LOG.info("blockPath is {}", blockPath);
        uFile.transferTo(new File(blockPath));
        //保存故障码
        GsmVehicleDataDTO[] dataDTOs = impactDTO.getData();
        if (ArrayUtil.isNotEmpty(dataDTOs)) {
          StringBuilder sb = new StringBuilder();
          int len = dataDTOs.length;
          for (int i = 0; i < len; i++) {
            GsmVehicleDataDTO dataDTO = dataDTOs[i];
            sb.append(dataDTO.getRdtc());
            if (i < len - 1) {
              sb.append(",");
            }
          }
          String rdtc = sb.toString();
          IHandleAppVehicleFaultCodeService iHandleAppVehicleFaultCodeService = ServiceManager.getService(IHandleAppVehicleFaultCodeService.class);
          ObdUserVehicle obdUserVehicle = impactService.getObdUserVehicle(impactDTO.getAppUserNo());
          OBD obd = impactService.getObdById(obdUserVehicle.getObdId());
          iHandleAppVehicleFaultCodeService.sendFaultCode(obd.getImei(), rdtc, impactDTO.getUploadTime());
        }
        //发送微信碰撞报警通知
        String impactSwitch = ServiceManager.getService(IConfigService.class).getConfig("impact_notify_switch", ShopConstant.BC_SHOP_ID);
        LOG.info("impactSwitch= {} ", impactSwitch);
        if (Switch.ON.toString().equals(impactSwitch)) {
          LOG.info("sendMirrorImpactRemindTemplate");
          IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
          List<AppWXUserDTO> userDTOs = wxUserService.getAppWXUserDTO(appUserNo, null);
          if (CollectionUtil.isNotEmpty(userDTOs)) {
            sendMirrorImpactRemindTemplate(impactDTO, userDTOs.toArray(new AppWXUserDTO[userDTOs.size()]));
          } else {
            LOG.info("send AppWXUserDTO size is empty");
          }
        }
        uploadFlag = GSMConstant.UPLOAD_FLAG_YES;
        msg = "碰撞确认有效,碰撞数据保存成功，请上传碰撞视频！";
      } else {
        uploadFlag = GSMConstant.UPLOAD_FLAG_NO;
      }
      return new ApiVideoInitResponse(uploadFlag, msg, null);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED, e.getMessage());
    }
  }


  /**
   * 上传碰撞视频
   *
   * @param device:设备类型1--一代，2--二代，2S--2S
   * @param vid                           :车辆ID
   * @param cid                           :碰撞ID
   * @param uFile
   * @param param
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/dev/{device}/vehicle/{vid}/collision/{cid}/video", method = RequestMethod.POST)
  public ApiResponse fileUploadV2(@PathVariable String device, @PathVariable String vid, @PathVariable String cid,
                                  @RequestParam("file") MultipartFile uFile, String param,
                                  HttpServletRequest request, HttpServletResponse response) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);

    try {
      if (StringUtil.isEmpty(param)) {
        return MessageCode.toApiResponse(MessageCode.UPLOAD_PARAM_EMPTY);
      }
      if (uFile.isEmpty()) {
        return MessageCode.toApiResponse(MessageCode.VIDEO_SIZE_EMPTY);
      }
      FileCollisionVideo block = JsonUtil.fromJson(param, FileCollisionVideo.class);
      //String uuid = block.getUuid();
      LOG.info("uploadVideo uuid={}", cid);
      LOG.info("uploadVideo,upload={},total={}", block.getOffset(), block.getTotal());
      if (StringUtil.isEmpty(cid)) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_UUID_EMPTY);
      }
      if (block.getCrc() != FileUtil.calcCRC32CheckSum(uFile.getBytes())) {
        return MessageCode.toApiResponse(MessageCode.FILE_CRC_CHECK_SUM_EXCEPTION);
      }
      //是测试账号的视频，直接返回成功，不记录，不更新数据库
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      if ("2cbe40296497ad4804226fbf9c831b2c".equals(appUserNo)) {
        return MessageCode.toApiResponse(MessageCode.SUCCESS);
      }

      //创建文件夹
      String backupPath = ServiceManager.getService(IConfigService.class).getConfig("video_backup_path", ShopConstant.BC_SHOP_ID);
      String filePath = backupPath + cid;
      File dFile = new File(filePath);
      if (!dFile.isDirectory()) {
        LOG.info("mirror:mkdir,path:{}", filePath);
        dFile.mkdir();
      }
      //创建文件
      String file = dFile.getPath() + FileUtil.getOSDisk() + cid + AppConstant.VIDEO_FORMAT;
      File mp4 = new File(file);
      //文件不存在，创建文件
      if (mp4.exists()) {
        mp4.createNewFile();
      }
      //追加到文件尾部
      FileOutputStream writer = new FileOutputStream(mp4, true);
      writer.write(uFile.getBytes());
      writer.close();

      ImpactVideoDTO impactVideoDTO = null;
      //第一块，记录上传状态
      if (block.getOffset() == 0) {
        impactVideoDTO = new ImpactVideoDTO();
        impactVideoDTO.setUuid(cid);
        impactVideoDTO.setSize(block.getTotal());
        impactVideoDTO.setBlockNum(block.getOffset() + block.getBlock());
        String name = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.ALL) + " 碰撞视频";
        impactVideoDTO.setName(name);
        impactVideoDTO.setAppUserNo(appUserNo);
        impactVideoDTO.setUploadTime(System.currentTimeMillis());
        impactVideoDTO.setUploadStatus(UploadStatus.UPLOADING);
        impactVideoDTO.setDeleted(DeletedType.FALSE);
        impactVideoDTO.setBackupPath(backupPath);
      } else if ((block.getOffset() + block.getBlock()) >= block.getTotal()) {
        //全部传输完成
        IConfigService configService = ServiceManager.getService(IConfigService.class);
        String host = configService.getConfig("video_host", ShopConstant.BC_SHOP_ID);
        int port = NumberUtil.intValue(configService.getConfig("video_host_port", ShopConstant.BC_SHOP_ID));
        String username = configService.getConfig("video_host_username", ShopConstant.BC_SHOP_ID);
        String password = configService.getConfig("video_host_password", ShopConstant.BC_SHOP_ID);
        String hostPath = configService.getConfig("video_host_path", ShopConstant.BC_SHOP_ID);
        String videoPlayPath = configService.getConfig("video_play_path", ShopConstant.BC_SHOP_ID);
        impactVideoDTO = impactService.getImpactVideoDTOByUUID(cid);
        String remoteFile = (hostPath + videoPlayPath + impactVideoDTO.getId() + AppConstant.VIDEO_FORMAT);
        SFTPHelper.notifySFTPUploadListener(host, username, password, port, file, remoteFile);
        //更新视频状态
        impactVideoDTO.setPath(videoPlayPath);
        impactVideoDTO.setBlockNum(block.getOffset() + block.getBlock());
        impactVideoDTO.setUploadStatus(UploadStatus.SUCCESS);
        //impactService.saveOrUpdateImpactVideo(impactVideoDTO);
      } else {
        //中间阶段
        impactVideoDTO = impactService.getImpactVideoDTOByUUID(cid);
        impactVideoDTO.setBlockNum(block.getOffset() + block.getBlock());
        impactVideoDTO.setBackupPath(backupPath);
      }
      //更新中间状态
      impactService.saveOrUpdateImpactVideo(impactVideoDTO);
      return MessageCode.toApiResponse(MessageCode.SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VIDEO_UPLOAD_FAIL);
    }
  }


  /**
   * 获取碰撞视频列表
   *
   * @param request
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/impact/videoList", method = RequestMethod.GET)
  public ApiResponse videoList(HttpServletRequest request, HttpServletResponse response) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      List<ImpactVideoDTO> videoDTOs = impactService.getImpactVideoDTOByAppUserNo(appUserNo);
      ApiVideoListResponse apiVideoListResponse = new ApiVideoListResponse();
      apiVideoListResponse.setVideoDTOs(videoDTOs);
      return apiVideoListResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   *获取碰撞信息及视频URL
   * @param request
   * @param response
   * @param vehicleId  汽车编号
   * @param datetime
   * @param count
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/{vehicleId}/collision/from/{datetime}/limit/{count}",method = RequestMethod.GET)
  public ApiVehicleToVideoResponse vehicleToVideo (HttpServletRequest request, HttpServletResponse response
          ,@PathVariable long vehicleId , @PathVariable long datetime , @PathVariable int count) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);

    try {
//      10000010101750074L,1438920179537L,10
      List<ImpactAndVideoDTO> list = impactService.getImpactAndVideo(vehicleId,datetime,count);
      ApiVehicleToVideoResponse apiVehicleToVideoResponse = new ApiVehicleToVideoResponse();
      apiVehicleToVideoResponse.setData(list);
      return apiVehicleToVideoResponse;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  /**
   * 远程监控拍照
   * @param request
   * @param response
   * @param session
   * @return
   * @throws ServletException
   * @throws BcgogoException
   * @throws IOException
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/{vehicleId}/monitor" , method = RequestMethod.GET)
  public ApiVehicleMonitor saveOneImpact ( HttpServletRequest request , HttpServletResponse response , HttpSession session) throws ServletException, BcgogoException, IOException {
    String appUserNo = SessionUtil.getAppUserNo(request, response);
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    ApiVehicleMonitor monitor = null;
    try {
      if( !map.isEmpty() || map == null){
        for (String keys : map.keySet()){
          if( keys.equals(appUserNo)){
            if ( System.currentTimeMillis() - Long.valueOf(map.get(appUserNo)) >= 10*60*1000){
              impactService.saveOneImpact(appUserNo);
              monitor = new ApiVehicleMonitor(MessageCode.toApiResponse(MessageCode.APP_MONITOR_SUCCESS));
              monitor.setData(1); //成功
              map.remove(keys);
            }else {
              monitor = new ApiVehicleMonitor(MessageCode.toApiResponse(MessageCode.App_MONITOR_FAIL));
              monitor.setData(0); //失败
            }
          }
        }
      }else{
        map.put(appUserNo,System.currentTimeMillis());
        impactService.saveOneImpact(appUserNo);
        monitor = new ApiVehicleMonitor(MessageCode.toApiResponse(MessageCode.APP_MONITOR_SUCCESS));
        monitor.setData(1); //成功
      }
      return monitor;
    }catch (Exception e) {
      LOG.error(e.getMessage());
      monitor = new ApiVehicleMonitor(MessageCode.toApiResponse(MessageCode.APP_MONITOR_EXCEPTION));
      return monitor;
    }
  }

  private void uploadFile(ImpactVideoDTO impactVideoDTO) throws IOException, JSchException {
    String uuid = impactVideoDTO.getUuid();
    String backupPath = impactVideoDTO.getBackupPath();
    String videoPath = backupPath + uuid;
    //排序文件块
    File file = new File(videoPath);
    File[] blockFiles = file.listFiles(new ImpactVideoBlockFilter());
    Arrays.sort(blockFiles, new Comparator<File>() {
      @Override
      public int compare(File o1, File o2) {
        return NumberUtil.subtract(o1.getName(), o2.getName()).intValue();
      }
    });
    //创建视频文件
    File videoFile = new File(videoPath + FileUtil.getOSDisk() + uuid + AppConstant.VIDEO_FORMAT);
    videoFile.createNewFile();
    //组装文件
    RandomAccessFile fileReader = null;
    RandomAccessFile fileWrite = null;
    long alreadyWrite = 0;
    int len = 0;
    byte[] buf = new byte[1024 * 10];
    try {
      fileWrite = new RandomAccessFile(videoFile, "rw");
      for (int i = 0; i < blockFiles.length; i++) {
        fileWrite.seek(alreadyWrite);
        fileReader = new RandomAccessFile(blockFiles[i], "r");
        // 写入
        while ((len = fileReader.read(buf)) != -1) {
          fileWrite.write(buf, 0, len);
        }
        alreadyWrite += blockFiles[i].length();
        fileReader.close();
      }
    } finally {
      if (fileWrite != null) {
        fileWrite.close();
      }
      if (fileReader != null) {
        fileReader.close();
      }
    }
    if (videoFile != null && videoFile.exists()) {
      LOG.info("uploadVideo....");
      //文件上传到视频服务器 video_local_backup_path ==> remote  video_path
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String host = configService.getConfig("video_host", ShopConstant.BC_SHOP_ID);
      int port = NumberUtil.intValue(configService.getConfig("video_host_port", ShopConstant.BC_SHOP_ID));
      String username = configService.getConfig("video_host_username", ShopConstant.BC_SHOP_ID);
      String password = configService.getConfig("video_host_password", ShopConstant.BC_SHOP_ID);
      String hostPath = configService.getConfig("video_host_path", ShopConstant.BC_SHOP_ID);
      String videoPlayPath = configService.getConfig("video_play_path", ShopConstant.BC_SHOP_ID);
      String remoteFile = (hostPath + videoPlayPath + impactVideoDTO.getId() + AppConstant.VIDEO_FORMAT);
      SFTPHelper.notifySFTPUploadListener(host, username, password, port, videoFile.getPath(), remoteFile);
      //更新视频状态
      IImpactService impactService = ServiceManager.getService(IImpactService.class);
      impactVideoDTO.setPath(videoPlayPath);
      impactVideoDTO.setUploadStatus(UploadStatus.SUCCESS);
      impactService.saveOrUpdateImpactVideo(impactVideoDTO);
    }

  }

  /**
   * 碰撞视频上传进度查询
   *
   * @param request
   * @param response
   * @param uuid
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/impact/vProgress/{uuid}", method = RequestMethod.GET)
  public ApiResponse VideoProgress(HttpServletRequest request, HttpServletResponse response, @PathVariable("uuid") String uuid) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    try {
      if (StringUtil.isEmpty(uuid)) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_UUID_EMPTY);
      }
      ApiVideoProgressResponse apiResponse = new ApiVideoProgressResponse();
      ImpactVideoDTO impactVideoDTO = impactService.getImpactVideoDTOByUUID(uuid);
      if (impactVideoDTO == null) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_DATA_NOT_EXIST);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VIDEO_UPLOAD_PROGRESS_QUERY_FAIL);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/vehicle/impact/video/{uuid}", method = RequestMethod.GET)
  public ApiResponse getImpactVideoUrl(HttpServletRequest request, HttpServletResponse response, @PathVariable("uuid") String uuid) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    try {
      if (StringUtil.isEmpty(uuid)) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_UUID_EMPTY);
      }
      ImpactVideoDTO impactVideoDTO = impactService.getImpactVideoDTOByUUID(uuid);
      if (impactVideoDTO == null) {
        return MessageCode.toApiResponse(MessageCode.IMPACT_DATA_NOT_EXIST);
      }
      ApiResultResponse<String> resultResponse = new ApiResultResponse<String>();
      resultResponse.setResult(impactService.getImpactVideoUrl(impactVideoDTO.getId()));
      return resultResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.VIDEO_UPLOAD_PROGRESS_QUERY_FAIL);
    }
  }

}
