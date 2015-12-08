package com.bcgogo.socketReceiver.service.impl;

import com.bcgogo.socketReceiver.constant.AUTConstant;
import com.bcgogo.socketReceiver.constant.AlertConstant;
import com.bcgogo.socketReceiver.constant.CommonConstant;
import com.bcgogo.socketReceiver.constant.ZHYSConstant;
import com.bcgogo.socketReceiver.dao.BaseDao;
import com.bcgogo.socketReceiver.dao.GsmPointDao;
import com.bcgogo.socketReceiver.enums.GsmPointType;
import com.bcgogo.socketReceiver.model.GsmPoint;
import com.bcgogo.socketReceiver.rmi.IBcgogoApiSocketRmiServer;
import com.bcgogo.socketReceiver.service.IGsmAlertService;
import com.bcgogo.socketReceiver.service.base.BaseService;
import com.bcgogo.socketReceiver.util.DateUtil;
import com.bcgogo.socketReceiver.util.NumberUtil;
import com.bcgogo.socketReceiver.util.SocketMessageUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-22
 * Time: 上午10:55
 *
 * // 0#356824200008009
 // 1#0
 // 2#JX
 // 3#1
 // 4#46000
 // 5#A
 // 6#12043.5859,E,3115.9094,N,000.00,000
 // 7#250314
 // 8#094703
 V1.1版本修改了数据结构:增加了碰撞脉冲P0000
 //356824200008008#0#JX#1#46000#A#12043.8182,E,3116.0385,N,000.00,000#240314#084250#P0000

 */
@Service
public class GsmAlertService extends BaseService<GsmPoint> implements IGsmAlertService {
  private static final Logger LOGGER = LoggerFactory.getLogger(GsmAlertService.class);

  @Autowired
  GsmPointDao dao;
  @Autowired
  IBcgogoApiSocketRmiServer bcgogoApiSocketRmiServer;

  @Override
  public BaseDao<GsmPoint> getDAO() {
    return dao;
  }

  //#008613717052335#3717052335#1#1234#JX#1#46001#A#11400.4268,E,2233.1050,N,000.00,000#030314#160131##


  @Override
  public GsmPoint saveAlert(String orgInfo) {
//    String iMei = null;
    GsmPoint gsmPoint = new GsmPoint();
     if (StringUtils.isNotEmpty(orgInfo)) {
       gsmPoint.setOrgInfo(orgInfo);
       try {
         orgInfo = SocketMessageUtils.trim(orgInfo);
         String[] orgInfoArray = orgInfo.split("#");
         if (orgInfoArray.length == 9 || orgInfoArray.length == 10) {
           String iMei = orgInfoArray[AlertConstant.IDX_ALERT_IMEI];
           gsmPoint.setEmi(iMei);
           if (StringUtils.isNumeric(orgInfoArray[AlertConstant.IDX_ALERT_STATE])) {
             gsmPoint.setState(Integer.valueOf(orgInfoArray[AlertConstant.IDX_ALERT_STATE]));
           }
           gsmPoint.setGsmPointType(GsmPointType.parseValue(orgInfoArray[AlertConstant.IDX_ALERT_TYPE]));
           gsmPoint.setGroup(orgInfoArray[AlertConstant.IDX_ALERT_GROUP]);
           gsmPoint.setCellPos(orgInfoArray[AlertConstant.IDX_ALERT_CELLPOS]);
           if (StringUtils.isNumeric(orgInfoArray[AlertConstant.IDX_ALERT_DAY])) {
             gsmPoint.setDate(orgInfoArray[AlertConstant.IDX_ALERT_DAY]);
           }
           if (StringUtils.isNumeric(orgInfoArray[AlertConstant.IDX_ALERT_TIME])) {
             gsmPoint.setTime(orgInfoArray[AlertConstant.IDX_ALERT_TIME]);
           }
           gsmPoint.setUploadTime(DateUtil.convertDMYHMSStr2Long(orgInfoArray[AlertConstant.IDX_ALERT_DAY],
               orgInfoArray[AlertConstant.IDX_ALERT_TIME], CommonConstant.GSM_TIME_ZONE_CORRECTION));
           gsmPoint.setUploadServerTime(System.currentTimeMillis());
           String coordinateInfo = orgInfoArray[AlertConstant.IDX_ALERT_INFO];
           if (StringUtils.isNotBlank(coordinateInfo)) {
             String[] coordinateInfoArray = coordinateInfo.split(",");
             if (coordinateInfoArray.length == 6) {
               gsmPoint.setLon(coordinateInfoArray[AlertConstant.IDX_ALERT_LON]);
               gsmPoint.setLonDir(coordinateInfoArray[AlertConstant.IDX_ALERT_LONDIR]);
               gsmPoint.setLat(coordinateInfoArray[AlertConstant.IDX_ALERT_LAT]);
               gsmPoint.setLatDir(coordinateInfoArray[AlertConstant.IDX_ALERT_LATDIR]);
               gsmPoint.setVelocity(coordinateInfoArray[AlertConstant.IDX_ALERT_VELOCITY]);
               gsmPoint.setHeading(coordinateInfoArray[AlertConstant.IDX_ALERT_HEADING]);
             } else {
               LOGGER.error("报警坐标数据格式异常"+coordinateInfo);
             }
           }
           if(orgInfoArray.length == 10){
             gsmPoint.setImpactStrength(NumberUtil.convertImpactStrength(orgInfoArray[AlertConstant.IDX_ALERT_IMPACT_STRENGTH]));
           }
           super.save(gsmPoint);
//           bcgogoApiSocketRmiServer.sendAlert(gsmPoint.getEmi(), gsmPoint.getLat(), gsmPoint.getLon(),
//               gsmPoint.getGsmPointType().name(), gsmPoint.getUploadTime().toString());

         } else {
           LOGGER.error("报警数据格式异常"+orgInfo);
         }
       } catch (Exception e) {
         LOGGER.debug("save gsmPoint failed!");
         LOGGER.error(e.getMessage(), e);
       }
     }
     return gsmPoint;
  }

//  碰撞报警：
//      #356824200008074#1#PZ#1#46000#A#11351.8405,E,2233.8851,N,000.00,100#00:12.41,01:46,02:9386,03:9414,04:0,05:0,06:0,07:2610,08:80,09:33.7,10:12.65,11:123237.4,12:0.0,13:123.34,14:123388.6,15:0.0,16:0.0,17:0.0,18:0,19:0.000,20:0,21:2565, 22:0,23:0#030714#085455#2##
//  位移报警：
//      #356824200008074#1#WY#1#46000#A#11351.8405,E,2233.8851,N,000.00,100#00:12.41,01:46,02:9386,03:9414,04:0,05:0,06:0,07:2610,08:80,09:33.7,10:12.65,11:123237.4,12:0.0,13:123.34,14:123388.6,15:0.0,16:0.0,17:0.0,18:0,19:0.000,20:0,21:2565, 22:0,23:0#030714#085455#2##
//
//  断电报警:
//      #356824200008074#1#JX#1#46000#A#11351.8405,E,2233.8851,N,000.00,100#00:12.41,01:46,02:9386,03:9414,04:0,05:0,06:0,07:2610,08:80,09:33.7,10:12.65,11:123237.4,12:0.0,13:123.34,14:123388.6,15:0.0,16:0.0,17:0.0,18:0,19:0.000,20:0,21:2565, 22:0,23:0#030714#085455#2##
//  低电压报警：
//  当电压低于10V
//  #356824200008074#1#LPD#1#46000#A#11351.8405,E,2233.8851,N,000.00,100#00:12.41,01:46,02:9386,03:9414,04:0,05:0,06:0,07:2610,08:80,09:33.7,10:12.65,11:123237.4,12:0.0,13:123.34,14:123388.6,15:0.0,16:0.0,17:0.0,18:0,19:0.000,20:0,21:2565, 22:0,23:0#030714#085455#2##
//  震动：
//      #356824200008039#1#ZD#1#46000#V#11317.7603,E,2308.8224,N,000.00,000#00:13.54,01:73,02:813,03:1235,04:0,05:0,06:0,07:105,08:0,0,10:14.18,11:68.9,12:0.0,13:0.07,14:70.4,15:0.0,16:0.0,17:0.0,18:0,19:0.000,20:0,21:115, 22:0,23:0#030714#085455#2##
  @Override
  public GsmPoint saveZHYSAlert(String orgInfo) {
    GsmPoint gsmPoint = new GsmPoint();
    String iMei = null;
    if (StringUtils.isNotEmpty(orgInfo)) {
      gsmPoint.setOrgInfo(orgInfo);
      try {
        orgInfo = SocketMessageUtils.trim(orgInfo);
        String[] orgInfoArray = orgInfo.split("#");
        if (orgInfoArray.length == 11|| orgInfoArray.length == 12) {
          iMei = orgInfoArray[ZHYSConstant.IDX_DTU_IMEI];
          gsmPoint.setEmi(iMei);
          if (StringUtils.isNumeric(orgInfoArray[ZHYSConstant.IDX_DTU_STATE])) {
            gsmPoint.setState(Integer.valueOf(orgInfoArray[ZHYSConstant.IDX_DTU_STATE]));
          }
          gsmPoint.setGsmPointType(GsmPointType.parseValue(orgInfoArray[ZHYSConstant.IDX_AUT_TYPE]));
          gsmPoint.setGroup(orgInfoArray[ZHYSConstant.IDX_AUT_GROUP]);
          gsmPoint.setCellPos(orgInfoArray[ZHYSConstant.IDX_AUT_CELLPOS]);
          if (StringUtils.isNumeric(orgInfoArray[ZHYSConstant.IDX_DTU_DAY])) {
            gsmPoint.setDate(orgInfoArray[ZHYSConstant.IDX_DTU_DAY]);
          }
          if (StringUtils.isNumeric(orgInfoArray[ZHYSConstant.IDX_DTU_TIME])) {
            gsmPoint.setTime(orgInfoArray[ZHYSConstant.IDX_DTU_TIME]);
          }
          try{
            gsmPoint.setUploadTime(DateUtil.convertDMYHMSStr2Long(orgInfoArray[ZHYSConstant.IDX_DTU_DAY],
                orgInfoArray[ZHYSConstant.IDX_DTU_TIME], CommonConstant.GSM_TIME_ZONE_CORRECTION));
          }catch (Exception e){
            LOGGER.error(iMei);
            LOGGER.error(e.getMessage(),e);
          }
          gsmPoint.setUploadServerTime(System.currentTimeMillis());
          String coordinateInfo = orgInfoArray[ZHYSConstant.IDX_AUT_INFO];
          if (StringUtils.isNotBlank(coordinateInfo)) {
            String[] coordinateInfoArray = coordinateInfo.split(",");
            if (coordinateInfoArray.length == 6) {
              gsmPoint.setLon(coordinateInfoArray[ZHYSConstant.IDX_AUT_LON]);
              gsmPoint.setLonDir(coordinateInfoArray[ZHYSConstant.IDX_AUT_LONDIR]);
              gsmPoint.setLat(coordinateInfoArray[ZHYSConstant.IDX_AUT_LAT]);
              gsmPoint.setLatDir(coordinateInfoArray[ZHYSConstant.IDX_AUT_LATDIR]);
              gsmPoint.setVelocity(coordinateInfoArray[ZHYSConstant.IDX_AUT_VELOCITY]);
              gsmPoint.setHeading(coordinateInfoArray[ZHYSConstant.IDX_AUT_HEADING]);
            } else {
              LOGGER.error("AUT坐标数据格式异常"+coordinateInfo);
            }
          }
//          if(orgInfoArray.length == 12){
//            gsmPoint.setImpactStrength(NumberUtil.convertImpactStrength(orgInfoArray[AlertConstant.IDX_ALERT_IMPACT_STRENGTH]));
//          }
          super.save(gsmPoint);
//          bcgogoApiSocketRmiServer.sendAlert(gsmPoint.getEmi(), gsmPoint.getLat(), gsmPoint.getLon(),
//              gsmPoint.getGsmPointType().name(), gsmPoint.getUploadTime().toString());

        } else {
          LOGGER.error("ZHYS报警数据格式异常"+orgInfo);
        }
      } catch (Exception e) {
        LOGGER.debug("save gsmPoint failed!");
        LOGGER.error(e.getMessage(), e);
      }
    }
    return gsmPoint;
  }
}
