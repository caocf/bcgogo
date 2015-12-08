package com.bcgogo.driving.controller;


import com.bcgogo.driving.service.IDriveLogService;
import com.bcgogo.pojox.api.ApiResponse;
import com.bcgogo.pojox.api.GsmTBoxDataDTO;
import com.bcgogo.pojox.api.GsmVehicleDataDTO;
import com.bcgogo.pojox.api.response.HttpResponse;
import com.bcgogo.pojox.constant.GSMConstant;
import com.bcgogo.pojox.constant.XConstant;
import com.bcgogo.pojox.enums.app.MessageCode;
import com.bcgogo.driving.service.IGSMVehicleDataService;
import com.bcgogo.driving.service.etl.XSessionUtil;
import com.bcgogo.pojox.util.HttpUtils;
import com.bcgogo.pojox.util.JsonUtil;
import com.bcgogo.pojox.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class GSMVehicleDataController {
  private static final Logger LOG = LoggerFactory.getLogger(GSMVehicleDataController.class);


  @Autowired
  private IGSMVehicleDataService gsmVehicleDataService;
  @Autowired
  private IDriveLogService driveLogService;
  @Autowired
  private XSessionUtil sessionUtil;


  @ResponseBody
  @RequestMapping(value = "/gsm/vehicle/getLastGsmVehicleData/{appUserNo}", method = RequestMethod.GET)
  public Object getLastGsmVehicleData(@PathVariable("appUserNo") String appUserNo) {
    return gsmVehicleDataService.getLastGsmVehicleData(appUserNo);
  }

  @ResponseBody
  @RequestMapping(value = "/gsm/vehicle/getIllegalCityByAppUserNo/{appUserNo}", method = RequestMethod.GET)
  public Object getIllegalCityByAppUserNo(@PathVariable("appUserNo") String appUserNo) {
    return gsmVehicleDataService.getIllegalCityByAppUserNo(appUserNo);
  }


  /**
   * 后视镜--上传车况信息
   *
   * @param data
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/gsm/vehicle/data", method = RequestMethod.PUT)
  public ApiResponse data(HttpServletRequest request, HttpServletResponse response, @RequestBody GsmVehicleDataDTO data) {

    try {
      String uuid = data.getUuid();
      if (StringUtil.isEmpty(uuid)) {
        return MessageCode.toApiResponse(MessageCode.VEHICLE_DATA_UUID_NO_EMPTY);
      }
      String appUserNo = sessionUtil.getAppUserNo(request, response);
      if (StringUtil.isEmpty(appUserNo)) {
        return MessageCode.toApiResponse(MessageCode.LOGIN_USER_NOT_EXIST);
      }
      LOG.info("mirror-collect vehicle_data,appUserNo={}", appUserNo);
      //对上传的错误时间进行纠错
      data.correctUploadTime();
      data.setAppUserNo(appUserNo);
      data.setUploadServerTime(System.currentTimeMillis());
      gsmVehicleDataService.saveOrUpdateGsmVehicleDataDTO(data);
      if (GSMConstant.CUTOFF.equals(data.getVehicleStatus())) {
        //生成行车轨迹
//        Executor executor = ThreadPool.getInstance();
//        executor.execute(new DriveCutOffListener(data));
        driveLogService.saveDriveLog(data);
      } else if (GSMConstant.AFTER_CUTOFF.equals(data.getVehicleStatus())) {
        //车门未关提醒
        System.out.println("AFTER_CUTOFF");
      }
      //更新车辆当前里程
//      IAppUserService iAppUserService = ServiceManager.getService(IAppUserService.class);
//      if(StringUtil.isNotEmpty(data.getAppUserNo())&&StringUtil.isNotEmpty(data.getCurMil())){
//        iAppUserService.updateVehicle(data.getAppUserNo(),Double.valueOf(data.getCurMil()));
//      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
    //如果存在故障码则保存故障码，发送消息
    try {
      if (StringUtil.isNotEmpty(data.getRdtc())) {
        String url = XConstant.URL_OPEN_SEND_FAULT_CODE;
        HttpResponse tmpResponse = HttpUtils.sendPost(url, data);
        String tmpResponseJson = tmpResponse.getContent();
        if (StringUtil.isNotEmpty(tmpResponseJson)) {
          ApiResponse apiResponse = JsonUtil.jsonToObj(tmpResponseJson, ApiResponse.class);
          if (apiResponse == null || !MessageCode.SUCCESS.toString().equals(apiResponse.getStatus())) {
            LOG.error("发送故障码异常。");
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return MessageCode.toApiResponse(MessageCode.VEHICLE_DATA_SUCCESS);

  }

  /**
   * 2s--上传车况信息
   *
   * @param data
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/gsm/tbox/vehicle/data", method = RequestMethod.PUT)
  public ApiResponse data2(HttpServletRequest request, HttpServletResponse response, @RequestBody GsmTBoxDataDTO data) {

    try {
      LOG.info("2s-collect vehicle_data,data={}", JsonUtil.objectToJson(data));
      String uuid = data.getUuid();
      if (StringUtil.isEmpty(uuid)) {
        return MessageCode.toApiResponse(MessageCode.VEHICLE_DATA_UUID_NO_EMPTY);
      }
      String appUserNo = sessionUtil.getAppUserNo(request, response);
      if (StringUtil.isEmpty(appUserNo)) {
        return MessageCode.toApiResponse(MessageCode.LOGIN_USER_NOT_EXIST);
      }
      data.correctUploadTime();
      data.setAppUserNo(appUserNo);
      data.setUploadServerTime(System.currentTimeMillis());
      gsmVehicleDataService.saveOrUpdateGsmTBoxDataDTO(data);
      //接收到熄火信号
      if (GSMConstant.CUTOFF.equals(data.getVehicleStatus())) {
        driveLogService.generationDriveLog(data);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }

    //如果存在故障码则保存故障码，发送消息
    try {
      if (StringUtil.isNotEmpty(data.getRdtc())) {
        String url = XConstant.URL_OPEN_SEND_FAULT_CODE;
        HttpResponse tmpResponse = HttpUtils.sendPost(url, data);
        String tmpResponseJson = tmpResponse.getContent();
        if (StringUtil.isNotEmpty(tmpResponseJson)) {
          ApiResponse apiResponse = JsonUtil.jsonToObj(tmpResponseJson, ApiResponse.class);
          if (apiResponse == null || !MessageCode.SUCCESS.toString().equals(apiResponse.getStatus())) {
            LOG.error("发送故障码异常。");
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return MessageCode.toApiResponse(MessageCode.VEHICLE_DATA_SUCCESS);
  }

}
