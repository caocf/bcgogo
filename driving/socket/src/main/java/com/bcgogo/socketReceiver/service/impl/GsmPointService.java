package com.bcgogo.socketReceiver.service.impl;

import com.bcgogo.pojox.util.DateUtil;
import com.bcgogo.pojox.util.NumberUtil;
import com.bcgogo.pojox.util.SocketMessageUtils;
import com.bcgogo.socketReceiver.constant.AUTConstant;
import com.bcgogo.socketReceiver.constant.CommonConstant;
import com.bcgogo.socketReceiver.constant.ZHYSConstant;
import com.bcgogo.socketReceiver.dao.BaseDao;
import com.bcgogo.socketReceiver.dao.GsmPointDao;
import com.bcgogo.socketReceiver.enums.GsmPointType;
import com.bcgogo.socketReceiver.model.GsmPoint;
import com.bcgogo.socketReceiver.service.IGsmPointService;
import com.bcgogo.socketReceiver.service.base.BaseService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: ZhangJuntao
 * Date: 14-3-6
 * Time: 下午5:51
 *
 #IMEI#状态#密码#AUT#组数#基站信息#经度,经度方向,纬度,纬度方向,速度,航向#日期#时间##
 #356824206007887#0#AUT#1#46001#V#12043.8033,E,3116.0262,N,000.00,000#180314#074051##
 P0000碰撞强度 加了碰撞强度，gsm设备升级之后才有这个参数
 #356824200008013#0#AUT#1#46000#V#11351.7123,E,2234.0169,N,003.00,053#310314#054941#P0000##
 需要再次约定
 */
@Service
public class GsmPointService extends BaseService<GsmPoint> implements IGsmPointService {
  private static final Logger LOGGER = LoggerFactory.getLogger(GsmPointService.class);
  @Autowired
  GsmPointDao dao;

  @Override
  public BaseDao<GsmPoint> getDAO() {
    return dao;
  }


  @Override
  public String savePoint(String orgInfo) {
    String iMei = null;
    if (StringUtils.isNotEmpty(orgInfo)) {
//      orgInfo =
//          "#356824206007887" +
//              "#0" +
//              "#AUT" +
//              "#1" +
//              "#46001" +
//              "#V" +
//              "#12043.8033,E,3116.0262,N,000.00,000" +
//              "#180314" +
//              "#074051##";
      GsmPoint point = new GsmPoint();
      point.setOrgInfo(orgInfo);
      point.setGsmPointType(GsmPointType.AUT);
      try {
        orgInfo = SocketMessageUtils.trim(orgInfo);
        String[] orgInfoArray = orgInfo.split("#");
        if (orgInfoArray.length == 9 || orgInfoArray.length == 10) {
          iMei = orgInfoArray[AUTConstant.IDX_AUT_IMEI];
          point.setEmi(orgInfoArray[AUTConstant.IDX_AUT_IMEI]);
          if (StringUtils.isNumeric(orgInfoArray[AUTConstant.IDX_AUT_STATE])) {
            point.setState(Integer.valueOf(orgInfoArray[AUTConstant.IDX_AUT_STATE]));
          }
          point.setGroup(orgInfoArray[AUTConstant.IDX_AUT_GROUP]);
          point.setCellPos(orgInfoArray[AUTConstant.IDX_AUT_CELLPOS]);
          if (StringUtils.isNumeric(orgInfoArray[AUTConstant.IDX_AUT_DAY])) {
            point.setDate(orgInfoArray[AUTConstant.IDX_AUT_DAY]);
          }
          if (StringUtils.isNumeric(orgInfoArray[AUTConstant.IDX_AUT_TIME])) {
            point.setTime(orgInfoArray[AUTConstant.IDX_AUT_TIME]);
          }
          try{
            point.setUploadTime(DateUtil.convertDMYHMSStr2Long(orgInfoArray[AUTConstant.IDX_AUT_DAY],
              orgInfoArray[AUTConstant.IDX_AUT_TIME], CommonConstant.GSM_TIME_ZONE_CORRECTION));
          }catch (Exception e){
            LOGGER.error(iMei);
            LOGGER.error(e.getMessage(),e);
          }

          point.setUploadServerTime(System.currentTimeMillis());
          String coordinateInfo = orgInfoArray[AUTConstant.IDX_AUT_INFO];
          if (StringUtils.isNotBlank(coordinateInfo)) {
            String[] coordinateInfoArray = coordinateInfo.split(",");
            if (coordinateInfoArray.length == 6) {
              point.setLon(coordinateInfoArray[AUTConstant.IDX_AUT_LON]);
              point.setLonDir(coordinateInfoArray[AUTConstant.IDX_AUT_LONDIR]);
              point.setLat(coordinateInfoArray[AUTConstant.IDX_AUT_LAT]);
              point.setLatDir(coordinateInfoArray[AUTConstant.IDX_AUT_LATDIR]);
              point.setVelocity(coordinateInfoArray[AUTConstant.IDX_AUT_VELOCITY]);
              point.setHeading(coordinateInfoArray[AUTConstant.IDX_AUT_HEADING]);
            } else {
              LOGGER.error("AUT坐标数据格式异常"+coordinateInfo);
            }
          }
          if(orgInfoArray.length == 10){
            point.setImpactStrength(NumberUtil.convertImpactStrength(orgInfoArray[AUTConstant.IDX_AUT_IMPACT_STRENGTH]));
          }
          super.save(point);
        } else {
          LOGGER.error("AUT数据格式异常"+orgInfo);
        }
      } catch (Exception e) {
        LOGGER.debug("save point failed!");
        LOGGER.error(e.getMessage(), e);
      }
    }
    return iMei;
  }

  //0//#356824200008039
  //1// #1
  //2// #AUT
  //3// #1
  //4// #46000
  //5// #V
  //6// #11317.7603,E,2308.8224,N,000.00,000
  //7// #00:13.54,01:73,02:813,03:1235,04:0,05:0,06:0,07:105,08:0,0,10:14.18,11:68.9,12:0.0,13:0.07,14:70.4,15:0.0,16:0.0,17:0.0,18:0,19:0.000,20:0,21:115,22:0,23:0
  //8// #030714
  //9// #085455
  //10// #2##

  @Override
  public String saveZHYSPoint(String orgInfo) {

    String iMei = null;
    if (StringUtils.isNotEmpty(orgInfo)) {
      GsmPoint point = new GsmPoint();
      point.setOrgInfo(orgInfo);
      point.setGsmPointType(GsmPointType.AUT);
      try {
        orgInfo = SocketMessageUtils.trim(orgInfo);
        String[] orgInfoArray = orgInfo.split("#");

        if (orgInfoArray.length == 11 ) {
          iMei = orgInfoArray[ZHYSConstant.IDX_DTU_IMEI];
          point.setEmi(iMei);
          if (StringUtils.isNumeric(orgInfoArray[ZHYSConstant.IDX_DTU_STATE])) {
            point.setState(Integer.valueOf(orgInfoArray[ZHYSConstant.IDX_DTU_STATE]));
          }

          if(ZHYSConstant.AUT_TYPE_ACCON.equals(orgInfoArray[ZHYSConstant.IDX_AUT_TYPE])){
            point.setState(ZHYSConstant.AUT_TYPE_ACCON_STATE);
          }else if(ZHYSConstant.AUT_TYPE_ACCOFF.equals(orgInfoArray[ZHYSConstant.IDX_AUT_TYPE])){
            point.setState(ZHYSConstant.AUT_TYPE_ACCOFF_STATE);
          }
          point.setGroup(orgInfoArray[ZHYSConstant.IDX_AUT_GROUP]);
          point.setCellPos(orgInfoArray[ZHYSConstant.IDX_AUT_CELLPOS]);
          if (StringUtils.isNumeric(orgInfoArray[ZHYSConstant.IDX_DTU_DAY])) {
            point.setDate(orgInfoArray[ZHYSConstant.IDX_DTU_DAY]);
          }
          if (StringUtils.isNumeric(orgInfoArray[ZHYSConstant.IDX_DTU_TIME])) {
            point.setTime(orgInfoArray[ZHYSConstant.IDX_DTU_TIME]);
          }
          try{
            point.setUploadTime(DateUtil.convertDMYHMSStr2Long(orgInfoArray[ZHYSConstant.IDX_DTU_DAY],
                orgInfoArray[ZHYSConstant.IDX_DTU_TIME], CommonConstant.GSM_TIME_ZONE_CORRECTION));
          }catch (Exception e){
            LOGGER.error(iMei);
            LOGGER.error(e.getMessage(),e);
          }

          point.setUploadServerTime(System.currentTimeMillis());
          String coordinateInfo = orgInfoArray[ZHYSConstant.IDX_AUT_INFO];
          //11317.7603,E,2308.8224,N,000.00,000
          if (StringUtils.isNotBlank(coordinateInfo)) {
            String[] coordinateInfoArray = coordinateInfo.split(",");
            if (coordinateInfoArray.length == 6) {
              point.setLon(coordinateInfoArray[ZHYSConstant.IDX_AUT_LON]);
              point.setLonDir(coordinateInfoArray[ZHYSConstant.IDX_AUT_LONDIR]);
              point.setLat(coordinateInfoArray[ZHYSConstant.IDX_AUT_LAT]);
              point.setLatDir(coordinateInfoArray[ZHYSConstant.IDX_AUT_LATDIR]);
              point.setVelocity(coordinateInfoArray[ZHYSConstant.IDX_AUT_VELOCITY]);
              point.setHeading(coordinateInfoArray[ZHYSConstant.IDX_AUT_HEADING]);
            } else {
              LOGGER.error("AUT坐标数据格式异常"+coordinateInfo);
            }
          }
//          if(orgInfoArray.length == 10){
//            point.setImpactStrength(NumberUtil.convertImpactStrength(orgInfoArray[AUTConstant.IDX_AUT_IMPACT_STRENGTH]));
//          }
          super.save(point);
        } else {
          LOGGER.error("ZHYS AUT数据格式异常"+orgInfo);
        }
      } catch (Exception e) {
        LOGGER.debug("save point failed!");
        LOGGER.error(e.getMessage(), e);
      }
    }
    return iMei;
  }
}
