package com.bcgogo.socketReceiver.service.handler;

import com.bcgogo.socketReceiver.constant.AlertConstant;
import com.bcgogo.socketReceiver.constant.ZHYSConstant;
import com.bcgogo.socketReceiver.model.GsmPoint;
import com.bcgogo.socketReceiver.rmi.IBcgogoApiSocketRmiServer;
import com.bcgogo.socketReceiver.service.IGsmAlertService;
import com.bcgogo.socketReceiver.service.IGsmPointService;
import com.bcgogo.socketReceiver.service.IGsmVehicleInfoService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 14-3-6
 * Time: 上午10:22
 */
@Service
public class GsmMessageReceivedParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(GsmMessageReceivedParser.class);
  @Autowired
  private IGsmVehicleInfoService gsmVehicleInfoService;
  @Autowired
  private IGsmPointService gsmPointService;

  @Autowired
  private IGsmAlertService gsmAlertService;

  @Autowired
  IBcgogoApiSocketRmiServer bcgogoApiSocketRmiServer;

  public static Set<String> ZHYSVehicleInfoTpe;

  static {
    ZHYSVehicleInfoTpe = new HashSet<String>();
    ZHYSVehicleInfoTpe.add("AUT");
    ZHYSVehicleInfoTpe.add("ACCON");
    ZHYSVehicleInfoTpe.add("ACCOFF");
  }

  public String parser(String string) {
    String iMei = null;
    try {
      if (StringUtils.isNotEmpty(string)) {
        String[] gsmInfo = generateMultiGsmInfo(string);
        if (!ArrayUtils.isEmpty(gsmInfo)) {
          for (String info : gsmInfo) {
            if(StringUtils.isEmpty(info)){
              continue;
            }
            //联华盈科设备数据处理
            if (isLHYKGsmVehicleInfo(info)) {
              iMei = gsmVehicleInfoService.saveVehicleInfo(info);
            } else if (isLHYKGsmPoint(info)) {
              iMei = gsmPointService.savePoint(info);
            }else if(isLHYKAlert(info)){
            //处理剪线报警
            //处理位移报警
            //处理震动报警
            //低电压报警
              GsmPoint gsmPoint = gsmAlertService.saveAlert(info);
              if (gsmPoint != null && StringUtils.isNotBlank(gsmPoint.getEmi())) {
                iMei = gsmPoint.getEmi();
                bcgogoApiSocketRmiServer.sendAlert(gsmPoint.getEmi(), gsmPoint.getLat(), gsmPoint.getLon(),
                    gsmPoint.getGsmPointType().name(), gsmPoint.getUploadTime().toString());
              }
            }
            //处理其他供应商的车况数据
            else if (isBackupVehicleInfo(info)) {
              iMei = gsmVehicleInfoService.saveZHYSVehicleInfo(info);
              iMei = gsmPointService.saveZHYSPoint(info);
            } else if (isBackupAlert(info)) {
              GsmPoint gsmPoint = gsmAlertService.saveZHYSAlert(info);
              if (gsmPoint != null && StringUtils.isNotBlank(gsmPoint.getEmi())) {
                iMei = gsmPoint.getEmi();
                bcgogoApiSocketRmiServer.sendAlert(gsmPoint.getEmi(), gsmPoint.getLat(), gsmPoint.getLon(),
                    gsmPoint.getGsmPointType().name(), gsmPoint.getUploadTime().toString());
              }
            }else{
              LOGGER.warn("未识别的数据类型:" + info);
            }
          }
        } else {
          LOGGER.error("接受GSM数据分组出错:" + string);
        }
      } else {
        LOGGER.warn("parser string is empty!");
      }
    } catch (Exception e) {
      LOGGER.error("【" + string + "】" + e.getMessage(),e);
    }
    return iMei;

  }

  private boolean isBackupAlert(String info) {
    String[] strArray = StringUtils.split(info, "#");
    if(!ArrayUtils.isEmpty(strArray) && (strArray.length == 11|| strArray.length==12)
        && AlertConstant.AllAlertFlagList.contains(strArray[ZHYSConstant.IDX_AUT_TYPE])){
      return true;
    }
    return false;
  }

  private boolean isBackupVehicleInfo(String info) {
    String[] strArray = StringUtils.split(info, "#");
    if(!ArrayUtils.isEmpty(strArray) && strArray.length == 11 &&ZHYSVehicleInfoTpe.contains(strArray[ZHYSConstant.IDX_AUT_TYPE])){
      return true;
    }
    return false;
  }

  private boolean isAlert(String info) {
    if(StringUtils.isNotEmpty(info)){
      for(String alert : AlertConstant.AllAlertFlagList){
        if(info.contains(alert)){
          return true;
        }
      }
    }
    return false;
  }

  //#356823032255122#1584521547#1#0000#AUT#1#26280E58#11354.7034,E,2232.6869,N,000.45,345#090511#064052###356823032255122#1584521547#1#0000#AUT#1#26280E58#11354.7034,E,2232.6869,N,000.45,345#090511#064052##
  //数据分组
  private String[] generateMultiGsmInfo(String string) {
    if (StringUtils.isNotEmpty(string)) {
      return string.split("##");
    }
    return new String[0];
  }


  private String[] generateAUT(String str) {

    return new String[0];
  }


  private String[] generateDTU(String str) {
    return new String[0];
  }

  private boolean isGsmPoint(String string) {
    String[] strArray = StringUtils.split(string, "#");
    for (String str : strArray) {
      if (str.equals("AUT")) {
        return true;
      }
    }
    return false;
  }

  //判断是LHYK的DTU数据
  private boolean isLHYKGsmVehicleInfo(String string) {
    String[] strArray = StringUtils.split(string, "#");
    if(!ArrayUtils.isEmpty(strArray) && strArray.length == 6 &&"DTU".equals(strArray[4])){
      return true;
    }
//    for (String str : strArray) {
//      if (str.equals("DTU")) {
//        return true;
//      }
//    }
    return false;
  }

  //判断是LHYK的AUT数据

  private boolean isLHYKGsmPoint(String string) {
    String[] strArray = StringUtils.split(string, "#");
    if(!ArrayUtils.isEmpty(strArray) && strArray.length == 9 &&"AUT".equals(strArray[2])){
      return true;
    }
//    for (String str : strArray) {
//      if (str.equals("AUT")) {
//        return true;
//      }
//    }
    return false;
  }

  //判断是LHYK的报警数据
  private boolean isLHYKAlert(String info) {
    String[] strArray = StringUtils.split(info, "#");
    if(!ArrayUtils.isEmpty(strArray) && (strArray.length == 9|| strArray.length==10) && AlertConstant.AllAlertFlagList.contains(strArray[2])){
      return true;
    }

//    if(StringUtils.isNotEmpty(info)){
//      for(String alert : AlertConstant.AllAlertFlagList){
//        if(info.contains(alert)){
//          return true;
//        }
//      }
//    }
    return false;
  }



}
