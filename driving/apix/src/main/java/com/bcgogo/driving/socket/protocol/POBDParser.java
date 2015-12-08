package com.bcgogo.driving.socket.protocol;

import com.bcgogo.driving.model.DriveLog;
import com.bcgogo.driving.service.IAppUserService;
import com.bcgogo.driving.service.IDriveLogService;
import com.bcgogo.pojox.api.AppUserDTO;
import com.bcgogo.pojox.api.DriveLogDTO;
import com.bcgogo.pojox.api.GsmVehicleDataDTO;
import com.bcgogo.pojox.api.response.HttpResponse;
import com.bcgogo.pojox.cache.MemCacheAdapter;
import com.bcgogo.pojox.common.ThreadPool;
import com.bcgogo.pojox.constant.GSMConstant;
import com.bcgogo.pojox.constant.XConstant;
import com.bcgogo.pojox.enums.DriveLogStatus;
import com.bcgogo.pojox.enums.DriveStatStatus;
import com.bcgogo.pojox.enums.app.AppUserType;
import com.bcgogo.driving.service.IGSMVehicleDataService;
import com.bcgogo.driving.service.IGeocodingService;
import com.bcgogo.driving.service.listener.DriveCutOffListener;
import com.bcgogo.driving.socket.SocketHelper;
import com.bcgogo.driving.socket.XSocketSessionManager;
import com.bcgogo.pojox.util.*;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-18
 * Time: 上午11:07
 */
@Component
public class POBDParser implements IProtocolParser {
  private static final Logger LOG = LoggerFactory.getLogger(POBDParser.class);
  @Autowired
  private IAppUserService appUserService;
  @Autowired
  private IDriveLogService driveLogService;
  @Autowired
  private IGSMVehicleDataService gsmVehicleDataService;
  @Autowired
  private IGeocodingService geocodingService;

  @Override
  public void doParse(IoSession session, String hexString) throws Exception {
    //去包头包尾
    hexString = hexString.substring(2, hexString.length() - 2);
    //转义还原
    hexString = hexString.replaceAll("7D01", "7D").replaceAll("7D02", "7E");
    //去截取校验结果
    char srcChecksum = BinaryUtil.hexString2String(hexString.substring(hexString.length() - 2, hexString.length())).charAt(0);
    //hexString = hexString.substring(0, hexString.length() - 2);
    //验证校验码
    char checksum = SocketHelper.hexChecksum(hexString);
    if (checksum != srcChecksum) {
//      LOG.warn("check failed, srcChecksum:{},checksum:{}", srcChecksum, checksum);
//        return;
    }
    parseProtocol(session, hexString);

  }

  /**
   * 解析协议
   *
   * @param session
   * @param hexString
   * @throws Exception
   */
  private void parseProtocol(IoSession session, String hexString) throws Exception {
    String imei = null;
    String m_type = null;
    if (hexString.startsWith(XConstant.CMD_HEADER_HQ)) {
      String[] strArr = BinaryUtil.hexString2String(hexString).split(",");
      m_type = strArr[2];
      if ("V1".equals(m_type)) {
        PLogin login = new PLogin(hexString);
        imei = login.getImei();
        LOG.debug("imei:{},登录V1:\n{}", imei, JsonUtil.objectToJson(login));
      } else if ("V9".equals(m_type)) { //行程开始
        PGsmVehicleDataStart dataStart = new PGsmVehicleDataStart(hexString);
        imei = dataStart.getImei();
        AppUserDTO appUserDTO = appUserService.getAppUserByImei(dataStart.getImei());
        if (appUserDTO == null || StringUtil.isEmpty(appUserDTO.getUserNo())) {
          LOG.info("imei:{},对应appUserNo不存在，或未注册",imei);
          return;
        }
        LOG.debug("行程开始,上传服务器原始数据：{}",JsonUtil.objectToJson(dataStart));
        dataStart.setUuid(UUID.randomUUID().toString());
        savePGsmVehicleDataStart(dataStart);
        XSocketSessionManager.addSession(imei, session);
        session.setAttribute("imei", imei);
      } else if ("V10".equals(m_type)) {  //行程结束
        PGsmVehicleDataEnd dataEnd = new PGsmVehicleDataEnd(hexString);
        imei = dataEnd.getImei();
        AppUserDTO appUserDTO = appUserService.getAppUserByImei(dataEnd.getImei());
        if (appUserDTO == null || StringUtil.isEmpty(appUserDTO.getUserNo())) {
          LOG.info("imei:{},对应appUserNo不存在，或未注册",imei);
          return;
        }
        LOG.debug("行程结束,上传服务器原始数据：{}",JsonUtil.objectToJson(dataEnd));
        String cacheVal = gsmVehicleDataService.getCachePGDataUUID(imei);
        MemCacheAdapter.set(GSMConstant.KEY_PREFIX_P_GSM_VEHICLE_DATA_START + imei, "");
        if (StringUtil.isNotEmpty(cacheVal)) {
          String[] cacheVals = cacheVal.split("_");
          dataEnd.setUuid(cacheVals[0]);
          dataEnd.setAppUserNo(cacheVals[1]);
          GsmVehicleDataDTO cutOffData = dataEnd.toGVDataDTO();
          cutOffData.setUserType(AppUserType.GSM);
          gsmVehicleDataService.saveOrUpdateGsmVehicleDataDTO(cutOffData);
          driveLogService.handleDriveLog(cutOffData);
          LOG.debug("imei:{},行程结束:\n{}", imei, JsonUtil.objectToJson(cutOffData));
          LOG.info("行程结束地址:{}", geocodingService.gpsCoordinate2FullAddress(cutOffData.getLat(), cutOffData.getLon()));
        }
      } else if ("V8".equals(m_type)) {
        LOG.debug("心跳包");
      }
    } else if (hexString.startsWith(XConstant.CMD_HEADER_RQ)) {
      PGsmVehicleData vehicleData = new PGsmVehicleData(hexString);
//       LOG.debug("上传服务器原始车况数据：{}",JsonUtil.objectToJson(vehicleData));
      imei = vehicleData.getImei();
      AppUserDTO appUserDTO = appUserService.getAppUserByImei(vehicleData.getImei());
      if (appUserDTO == null || StringUtil.isEmpty(appUserDTO.getUserNo())) {
        LOG.info("imei:{},对应appUserNo不存在，或未注册",imei);
        return;
      }
      String appUserNo = null;
      String uuid;
      String cacheVal = gsmVehicleDataService.getCachePGDataUUID(imei);
      LOG.debug("cacheVal:{}",cacheVal);
      if (StringUtil.isEmpty(cacheVal)) {
        LOG.error("纠错漏传发动时的车况,imei:{}", imei);
        PGsmVehicleDataStart dataStart = gsmVehicleDataService.generatePGsmVehicleDataStart(vehicleData);
        savePGsmVehicleDataStart(dataStart);
        uuid = dataStart.getUuid();
        appUserNo = dataStart.getAppUserNo();
      } else {
        String[] cacheVals = cacheVal.split("_");
        uuid = cacheVals[0];
        appUserNo = cacheVals[1];
      }
      vehicleData.setUuid(uuid);
      vehicleData.setAppUserNo(appUserNo);
      m_type = "V1";
      GsmVehicleDataDTO dataDTO = vehicleData.toGVDataDTO();
      dataDTO.setUserType(AppUserType.POBD);
      gsmVehicleDataService.saveOrUpdateGsmVehicleDataDTO(dataDTO);
      LOG.debug("定时上报:\n{},车况地址：{}", JsonUtil.objectToJson(dataDTO), geocodingService.gpsCoordinate2FullAddress(dataDTO.getLat(), dataDTO.getLon()));
    } else {
      LOG.error("未知:{}", hexString);
    }
    StringBuilder sb = new StringBuilder();
    sb.append("*HQ,")
      .append(imei)
      .append(",V4,")
      .append(m_type)
      .append(",").append(DateUtil.convertDateLongToString(System.currentTimeMillis(), "yyyyMMddHHmmss"))
      .append("#");
    sb.append(SocketHelper.checksum(sb.toString())).append("~");
    sb.insert(0, "~");
    LOG.debug("服务器应答包:{}",sb.toString());
    session.write(sb.toString());
  }

  private void savePGsmVehicleDataStart(PGsmVehicleDataStart dataStart) throws ParseException, IOException {
    if (dataStart == null) {
      LOG.error("generatePGsmVehicleDataStart failed");
      return;
    }
    GsmVehicleDataDTO dataDTO = dataStart.toGVDataDTO();
    LOG.debug("POBD:getAppUserByImei,imei={}", dataStart.getImei());
    AppUserDTO appUserDTO = appUserService.getAppUserByImei(dataStart.getImei());
    if (appUserDTO == null || StringUtil.isEmpty(appUserDTO.getUserNo())) {
      LOG.info("imei:{},对应appUserNo不存在，或未注册",dataStart.getImei());
      return;
    }
    String appUserNo = appUserDTO.getUserNo();
    dataDTO.setAppUserNo(appUserNo);
    dataStart.setAppUserNo(appUserNo);
    dataDTO.setUserType(AppUserType.GSM);
    gsmVehicleDataService.saveOrUpdateGsmVehicleDataDTO(dataDTO);
    driveLogService.handleDriveLog(dataDTO);
    String cacheVal = dataStart.getUuid() + "_" + dataDTO.getAppUserNo();
    MemCacheAdapter.set(GSMConstant.KEY_PREFIX_P_GSM_VEHICLE_DATA_START + dataStart.getImei(), cacheVal, new Date(System.currentTimeMillis() + GSMConstant.M_EXPIRE_P_GSM_VEHICLE_DATA_START));
    LOG.debug("imei:{},行程开始:\n{}", dataStart.getImei(), JsonUtil.objectToJson(dataDTO));
    LOG.debug("行程开始地址:{}", geocodingService.gpsCoordinate2FullAddress(dataDTO.getLat(), dataDTO.getLon()));
  }


}
