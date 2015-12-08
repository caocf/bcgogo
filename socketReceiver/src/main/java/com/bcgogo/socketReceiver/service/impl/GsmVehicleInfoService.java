package com.bcgogo.socketReceiver.service.impl;

import com.bcgogo.socketReceiver.constant.AUTConstant;
import com.bcgogo.socketReceiver.constant.CommonConstant;
import com.bcgogo.socketReceiver.constant.DTUConstant;
import com.bcgogo.socketReceiver.constant.ZHYSConstant;
import com.bcgogo.socketReceiver.dao.BaseDao;
import com.bcgogo.socketReceiver.dao.GsmPointDao;
import com.bcgogo.socketReceiver.dao.GsmVehicleInfoDao;
import com.bcgogo.socketReceiver.enums.GsmPointType;
import com.bcgogo.socketReceiver.model.GsmPoint;
import com.bcgogo.socketReceiver.model.GsmVehicleInfo;
import com.bcgogo.socketReceiver.rmi.IBcgogoApiSocketRmiServer;
import com.bcgogo.socketReceiver.service.IGsmVehicleInfoService;
import com.bcgogo.socketReceiver.service.base.BaseService;
import com.bcgogo.socketReceiver.util.DateUtil;
import com.bcgogo.socketReceiver.util.NumberUtil;
import com.bcgogo.socketReceiver.util.SocketMessageUtils;
import com.bcgogo.socketReceiver.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
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
 * Time: 下午5:51
 */
@Service
public class GsmVehicleInfoService extends BaseService<GsmVehicleInfo> implements IGsmVehicleInfoService {
  private static final Logger LOGGER = LoggerFactory.getLogger(GsmVehicleInfoService.class);

  @Autowired
  GsmVehicleInfoDao gsmVehicleInfoDao;


  @Autowired
  IBcgogoApiSocketRmiServer bcgogoApiSocketRmiServer;

  @Override
  public BaseDao<GsmVehicleInfo> getDAO() {
    return gsmVehicleInfoDao;
  }

  // #356824206007887#170314#103312#0#1234#DTU#<00:13.41,01:92�C,02:1699,03:2752,04:18,05:87,06:1,0,08:0,0,0,11:0.219,12:7.3,13:0.87,14:866.4,15:11.8,16:11.8,17:11.8,18:3.931,19:1093,20:207,21:RDTC:000,,22:RPDTC:000,>##
  @Override
  public String saveVehicleInfo(String orgInfo) {
//    orgInfo =
//        "#356824206007887" +
//        "#170314" +
//        "#103312" +
//        "#0" +
////        "#1234" +
//        "#DTU" +
//        "#<00:13.41,01:92,02:1699,03:2752,04:18,05:87,06:1,0,08:0,0,0,11:0.219,12:7.3,13:0.87,14:866.4,15:11.8,16:11.8,17:11.8,18:3.931,19:1093,20:207,21:0,22:0,>" +
//        "##";
    String iMei = null;
    if (StringUtils.isNotEmpty(orgInfo)) {
      GsmVehicleInfo info = new GsmVehicleInfo();
      info.setOrgInfo(orgInfo);
      try {
        orgInfo = SocketMessageUtils.trim(orgInfo);
        if (StringUtils.isBlank(orgInfo)){
          return null;
        }
        String[] orgInfoArray = orgInfo.split("#");
        if (orgInfoArray.length != 6) {
          LOGGER.error("vehicle info attributes is invalidate!" + orgInfo);
        } else {
          iMei = orgInfoArray[DTUConstant.IDX_DTU_IMEI];
          info.setEmi(orgInfoArray[DTUConstant.IDX_DTU_IMEI]);
          try{
            info.setUploadTime(DateUtil.convertDMYHMSStr2Long(orgInfoArray[DTUConstant.IDX_DTU_DAY],
                orgInfoArray[DTUConstant.IDX_DTU_TIME], CommonConstant.GSM_TIME_ZONE_CORRECTION));
          }catch (Exception e){
            LOGGER.error(iMei);
            LOGGER.error(e.getMessage(),e);
          }

          info.setUploadServerTime(System.currentTimeMillis());
          if (StringUtils.isNumeric(orgInfoArray[DTUConstant.IDX_DTU_STATE])) {
            info.setState(Integer.valueOf(orgInfoArray[DTUConstant.IDX_DTU_STATE]));
          }
          String infoDetail = orgInfoArray[DTUConstant.IDX_DTU_INFO];
          if (StringUtils.isNotBlank(infoDetail)) {
            infoDetail = infoDetail.replace("<", "").replace(">", "");
            String[] infoDetailArray = infoDetail.split(",");
            if (!ArrayUtils.isEmpty(infoDetailArray) && infoDetailArray.length == 23) {
              info.setSpwr(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_SPWR]));
              info.setEct(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_ECT]));
              info.setRpm(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_RPM]));
              info.setMaxr(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_MAX_R]));
              info.setVss(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_VSS]));
              info.setMaxs(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_MAX_S]));
              info.setBadh(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_BAD_H]));
              info.setRuntime(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_RUN_TIME]));
              info.setMilDist(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_MIL_DIST]));
              info.setFuelLvl(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_FUEL_LVL]));
              info.setVpwr(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_VPWR]));
              info.setIfe(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_IFE]));
              info.setCacafe(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_CAC_AFE]));
              info.setCactfe(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_CAC_TFE]));
              info.setCactrfe(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_CAC_TRFE]));
              info.setAdMil(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_AD_MIL]));
              info.setDemil(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_DE_MIL]));
              info.setTrmil(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_TR_MIL]));
              info.setAdFeh(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_AD_FEH]));
              info.setDrit(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_DRI_T]));
              info.setIdlet(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_IDLE_T]));
              info.setRdtc(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_RDTC]));
              info.setRpdtc(extractInfoVal(infoDetailArray[DTUConstant.IDX_DTU_RPDTC]));
            } else {
              LOGGER.error("暂时未处理该种类型的DTU：" + orgInfo);
            }
          }
          super.save(info);
          String faultCode = parseFaultCode(info.getRdtc(),info.getRpdtc());
          if(StringUtils.isNotEmpty(faultCode)) {
            bcgogoApiSocketRmiServer.sendFaultCode(iMei, faultCode, info.getUploadTime());
          }
        }
      } catch (Exception e) {
        LOGGER.debug("save vehicle info failed!");
        LOGGER.error(e.getMessage(), e);
      }
    }
    return iMei;
  }

  private String parseFaultCode(String rdtc, String rpdtc) {
    if (StringUtils.isNotEmpty(rdtc) || StringUtils.isNotEmpty(rpdtc)) {
      Set<String> faultCodeSet = new HashSet<String>();
      if (StringUtils.isNotEmpty(rdtc)) {
        String[] faultCodeArr = rdtc.split("&");
        for (String faultCode : faultCodeArr) {
          if (StringUtils.isNotEmpty(faultCode)) {
            faultCodeSet.add(faultCode);
          }
        }
      }
      if (StringUtils.isNotEmpty(rpdtc)) {
        String[] faultCodeArr = rpdtc.split("&");
        for (String faultCode : faultCodeArr) {
          if (StringUtils.isNotEmpty(faultCode)) {
            faultCodeSet.add(faultCode);
          }
        }
      }
      if (CollectionUtils.isNotEmpty(faultCodeSet)) {
        StringBuilder sb = new StringBuilder();
        for (String faultCode : faultCodeSet) {
          sb.append(faultCode).append(",");
        }
        return sb.toString();
      }
    }
    return null;
  }


  private String trim(String defaultVal, String info, String name, String... removes) throws Exception {
    String value = defaultVal;
    if (info.contains(name)) {
      String[] loadArray = info.split(":");
      if (loadArray.length != 2) {
        throw new Exception("vehicle info " + name + " attribute is invalidate!");
      }
      value = StringUtils.trim(loadArray[1]);
      if (!ArrayUtils.isEmpty(removes)) {
        for (String rem : removes) {
          value = StringUtils.remove(value, rem);
        }
      }
    }
    return value;
  }

  //info 可能是00:13.41 或者没有冒号， 只提取有冒号后面的数据
  private String extractInfoVal(String info) throws Exception {
    String value = null;
    String[] loadArray = info.split(":");
    if (loadArray.length == 2) {
      value = StringUtils.trim(loadArray[1]);
    }
    return value;
  }

   //info 可能是00:13.41 或者没有冒号， 只提取有冒号后面的数据
  private String extractInfoFaultCode(String info) throws Exception {
    String value = null;
    String[] loadArray = info.split(":");
    if (loadArray.length == 2) {
      value = StringUtils.trim(loadArray[1]);
    }
      if("0".equals(value)){
        return null;
    }

    return value;
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
  public String saveZHYSVehicleInfo(String orgInfo) {
    String iMei = null;
    if (StringUtils.isNotEmpty(orgInfo)) {
      GsmVehicleInfo info = new GsmVehicleInfo();
      info.setOrgInfo(orgInfo);
      orgInfo = SocketMessageUtils.trim(orgInfo);
      if (StringUtils.isBlank(orgInfo)){
        return null;
      }
      String[] orgInfoArray  = orgInfo.split("#");
      try {
        if (orgInfoArray.length != 11) {
          LOGGER.error("vehicle info attributes is invalidate!" + orgInfo);
        } else {
          iMei = orgInfoArray[ZHYSConstant.IDX_DTU_IMEI];
          info.setEmi(orgInfoArray[ZHYSConstant.IDX_DTU_IMEI]);
          try{
            info.setUploadTime(DateUtil.convertDMYHMSStr2Long(orgInfoArray[ZHYSConstant.IDX_DTU_DAY],
                orgInfoArray[ZHYSConstant.IDX_DTU_TIME], CommonConstant.GSM_TIME_ZONE_CORRECTION));
          }catch (Exception e){
            LOGGER.error(iMei);
            LOGGER.error(e.getMessage(),e);
          }

          info.setUploadServerTime(System.currentTimeMillis());
          if (StringUtils.isNumeric(orgInfoArray[ZHYSConstant.IDX_DTU_STATE])) {
            info.setState(Integer.valueOf(orgInfoArray[ZHYSConstant.IDX_DTU_STATE]));
          }

          if(ZHYSConstant.AUT_TYPE_ACCON.equals(orgInfoArray[ZHYSConstant.IDX_AUT_TYPE])){
            info.setState(ZHYSConstant.AUT_TYPE_ACCON_STATE);
          }else if(ZHYSConstant.AUT_TYPE_ACCOFF.equals(orgInfoArray[ZHYSConstant.IDX_AUT_TYPE])){
            info.setState(ZHYSConstant.AUT_TYPE_ACCOFF_STATE);
          }
          String infoDetail = orgInfoArray[ZHYSConstant.IDX_DTU_INFO];
          if (StringUtils.isNotBlank(infoDetail)) {
            infoDetail = infoDetail.replace("<", "").replace(">", "");
            String[] infoDetailArray = infoDetail.split(",");
            if (!ArrayUtils.isEmpty(infoDetailArray) && infoDetailArray.length == 24) {
              info.setSpwr(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_SPWR]));
              info.setEct(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_ECT]));
              info.setRpm(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_RPM]));
              info.setMaxr(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_MAX_R]));
              info.setVss(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_VSS]));
              info.setMaxs(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_MAX_S]));
              Integer badH =NumberUtil.intValue( extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_BAD_H]))+
                  NumberUtil.intValue( extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_BAD_L]));
              info.setBadh(StringUtil.valueOf(badH));

              info.setRuntime(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_RUN_TIME]));
              info.setMilDist(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_MIL_DIST]));
              info.setFuelLvl(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_FUEL_LVL]));
              info.setVpwr(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_VPWR]));
//              info.setIfe(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_IFE]));
              info.setCacafe(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_CAC_AFE]));
              info.setCactfe(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_CAC_TFE]));
              info.setCactrfe(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_CAC_TRFE]));
              info.setAdMil(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_AD_MIL]));
              info.setDemil(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_DE_MIL]));
              info.setTrmil(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_TR_MIL]));
              info.setAdFeh(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_AD_FEH]));
              info.setDrit(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_DRI_T]));
              info.setIdlet(extractInfoVal(infoDetailArray[ZHYSConstant.IDX_DTU_IDLE_T]));
              info.setRdtc(extractInfoFaultCode(infoDetailArray[ZHYSConstant.IDX_DTU_RDTC]));
//              info.setRpdtc(extractInfoFaultCode(infoDetailArray[ZHYSConstant.IDX_DTU_RPDTC]));
            } else {
              LOGGER.error("暂时未处理该种类型的DTU：" + orgInfo);
            }
          }
          super.save(info);


          String faultCode = parseFaultCode(info.getRdtc(),info.getRpdtc());
          if(StringUtils.isNotEmpty(faultCode)) {
            bcgogoApiSocketRmiServer.sendFaultCode(iMei, faultCode, info.getUploadTime());
          }
        }
      } catch (Exception e) {
        LOGGER.debug("save vehicle info failed!");
        LOGGER.error(e.getMessage(), e);
      }



    }
    return iMei;
  }

  public static void main(String[] args) {
    GsmVehicleInfoService gsmVehicleInfoService = new GsmVehicleInfoService();
   String s= gsmVehicleInfoService.parseFaultCode("&P3002&P3102&P3401&C163B","&C163B&B24A1&P020C&P1400&U3C00&P0000");
   System.out.println(s);
  }
}
