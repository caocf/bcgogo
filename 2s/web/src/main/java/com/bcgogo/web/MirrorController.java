package com.bcgogo.web;

import com.bcgogo.PageErrorMsg;
import com.bcgogo.api.*;
import com.bcgogo.api.response.AppGsmVehicleResponse;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.api.response.MessageResponse;
import com.bcgogo.api.response.OneKeyRescueResponse;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.service.IMQClientService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.Constant;
import com.bcgogo.constant.MQConstant;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.etl.ImpactVideoExpDTO;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.mq.message.MQTalkMessageDTO;
import com.bcgogo.product.service.app.IAppDictionaryService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.txn.dto.AppointOrderSearchCondition;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.TalkMessageCondition;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.service.IAppointOrderService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.app.IAppOrderService;
import com.bcgogo.txn.service.app.IAppPushMessageService;
import com.bcgogo.txn.service.app.IAppVehicleService;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.Coordinate;
import com.bcgogo.user.dto.InsuranceCompanyDTO;
import com.bcgogo.user.model.InsuranceCompany;
import com.bcgogo.user.model.app.AppVehicleFaultInfo;
import com.bcgogo.user.service.IImpactService;
import com.bcgogo.user.service.IRescueService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.app.IAppVehicleFaultCodeService;
import com.bcgogo.user.service.app.IDriveLogService;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXJsApiTicketSign;
import com.bcgogo.wx.user.AppWXUserDTO;
import com.bcgogo.wx.user.OneKeyRescueDTO;
import com.bcgogo.wx.user.WXUserDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * 后视镜controller
 * Author: ndong
 * Date: 2015-5-15
 * Time: 15:11
 */
@Controller
@RequestMapping("/mirror")
public class MirrorController {
  private static final Logger LOG = LoggerFactory.getLogger(MirrorController.class);

  @Autowired
  private IWXUserService wxUserService;
  @Autowired
  private IAppUserService appUserService;

  //行车轨迹
  private static final String PAGE_DRIVE_LOG = "/wx/wx_drive_log";
  //轨迹详情
  private static final String PAGE_DRIVE_DETAIL = "/wx/wx_drive_detail";
  //在线预约（mirror）
  private static final String PAGE_MIRROR_APPOINT = "/wx/wx_mirror_appoint";
  //碰撞视频
  private static final String PAGE_VIDEO = "/wx/wx_video";

  private static final String PAGE_VIDEO_PLAY = "/wx/wx_video_play";
  //车辆定位
  private static final String PAGE_VEHICLE_LOCATION = "/wx/wx_vehicle_location";
  //人车对话
  private static final String PAGE_WX_TALK = "/wx/wx_talk";
  //我的消息
  private static final String PAGE_MY_MESSAGE = "/wx/wx_my_message";
  //我的车辆
  private static final String PAGE_VEHICLE_LIST = "/wx/wx_vehicle_list";
  //故障查询
  private static final String PAGE_VEHICLE_FAULT_CODE = "/wx/wx_vehicle_fault_code";
  //违章查询
  private static final String PAGE_ILLEGAL_QUERY = "/wx/wx_illegal_query";
  //我的预约
  private static final String PAGE_MYAPPOINT = "/wx/wx_my_appoint";
  //车况检查
  private static final String PAGE_GVDATA = "/wx/wx_gvData";
  //救援电话
  private static final String PAGE_MOBILE = "/wx/wx_mobile";
  //故障码背景知识
  private static final String PAGE_BACKGROUNDINFO = "/wx/wx_backgroundInfo";
  //修改车辆
  private static final String PAGE_VEHICLE = "/wx/wx_vehicle";

  /**
   * 我的车辆
   *
   * @param request
   * @param modelMap
   * @param openId
   * @return
   */
  @RequestMapping(value = "/vehicleList/{openId}", method = RequestMethod.GET)
  public String toVehicleList(HttpServletRequest request, ModelMap modelMap, @PathVariable("openId") String openId) {
    try {
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      modelMap.put("openId", openId);
      //      测试
//      AppWXUserDTO appWXUserDTO1 = new AppWXUserDTO();
//      appWXUserDTO1.setAppUserNo("534a0cf81597be55127ad21433db4b71");
//      appWXUserDTOs.add(appWXUserDTO1);
//
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        return PAGE_VEHICLE_LIST;
      }
      List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();
      IAppUserVehicleObdService iAppUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
      IUserService iUserService = ServiceManager.getService(IUserService.class);
      ShopDTO shopDTO = null;
      AppUserCustomerDTO appUserCustomerDTO = null;
      IAppUserService iAppUserService = ServiceManager.getService(IAppUserService.class);
      IConfigService iConfigService = ServiceManager.getService(IConfigService.class);
      IImpactService iImpactService = ServiceManager.getService(IImpactService.class);
      IJuheService juheService = ServiceManager.getService(IJuheService.class);
      IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
      InsuranceCompany insuranceCompany = null;
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        AppGsmVehicleResponse apiResponse = iAppUserVehicleObdService.gsmUserGetAppVehicle(appWXUserDTO.getAppUserNo());
        if (apiResponse.getVehicleInfo() != null) {
          //里程更新，取最新的车况数据中的里程数
          GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(appWXUserDTO.getAppUserNo());
          if (gsmVehicleDataDTO != null && StringUtil.isNotEmpty(gsmVehicleDataDTO.getCurMil())) {
            apiResponse.getVehicleInfo().setCurrentMileage(Double.valueOf(gsmVehicleDataDTO.getCurMil()));
          }
          //保险公司名字
          insuranceCompany = iUserService.getInsuranceCompanyDTOById(apiResponse.getVehicleInfo().getInsuranceCompanyId());
          if (insuranceCompany != null) {
            apiResponse.getVehicleInfo().setInsuranceCompanyName(insuranceCompany.getName());
          }
          //店铺名称
          appUserCustomerDTO = iAppUserService.getAppUserCustomerDTOByAppUserNoAndAppVehicleId(appWXUserDTO.getAppUserNo(), apiResponse.getVehicleInfo().getVehicleId(), AppUserCustomerMatchType.MIRROR_MATCH);
          if (appUserCustomerDTO != null) {
            shopDTO = iConfigService.getShopById(appUserCustomerDTO.getShopId());
            apiResponse.getVehicleInfo().setShopName(shopDTO.getName());
          }
          //Imei号
          apiResponse.getVehicleInfo().setImei(iImpactService.getObdById(iImpactService.getObdUserVehicle(appWXUserDTO.getAppUserNo()).getObdId()).getImei());
          //当前油价
          IConfigService configService = ServiceManager.getService(IConfigService.class);
          String gasoline_price = configService.getConfig("gasoline_price", ShopConstant.BC_SHOP_ID);
          apiResponse.getVehicleInfo().setGasoline_price(gasoline_price);
          //违章查询省市
          List<JuheViolateRegulationCitySearchConditionDTO> conditionDTOs = juheService.getJuheViolateRegulationCitySearchCondition(apiResponse.getVehicleInfo().getJuheCityCode(), JuheStatus.ACTIVE);
          if (CollectionUtil.isNotEmpty(conditionDTOs)) {
            JuheViolateRegulationCitySearchConditionDTO conditionDTO = CollectionUtil.getFirst(conditionDTOs);
            apiResponse.getVehicleInfo().setJuheCityName(conditionDTO.getCityName());
            apiResponse.getVehicleInfo().setJuheCityCode(conditionDTO.getCityCode());
          }
          //时间处理
          if (apiResponse.getVehicleInfo().getNextMaintainTime() != null) {
            apiResponse.getVehicleInfo().setNextMaintainTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, Long.valueOf(apiResponse.getVehicleInfo().getNextMaintainTime())));
          } else {
            apiResponse.getVehicleInfo().setNextMaintainTimeStr("");
          }
          if (apiResponse.getVehicleInfo().getNextExamineTime() != null) {
            apiResponse.getVehicleInfo().setNextExamineTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, Long.valueOf(apiResponse.getVehicleInfo().getNextExamineTime())));
          } else {
            apiResponse.getVehicleInfo().setNextExamineTimeStr("");
          }

          //appUserNo
          apiResponse.getVehicleInfo().setAppUserNo(appWXUserDTO.getAppUserNo());
          appVehicleDTOList.add(apiResponse.getVehicleInfo());
        }
      }
      modelMap.put("appVehicleDTOList", appVehicleDTOList); //车辆列表
      modelMap.put("appVehicleDTONum", appVehicleDTOList.size()); //车辆数量
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return PAGE_VEHICLE_LIST;
  }

  /**
   * 在线预约
   *
   * @param modelMap
   * @param openId
   * @param appUserNo
   * @return
   */
  @RequestMapping(value = "/2Appoint/{openId}/{appUserNo}", method = RequestMethod.GET)
  public String toMirrorAppoint(ModelMap modelMap, @PathVariable("openId") String openId, @PathVariable("appUserNo") String appUserNo) {
    try {

      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        return PAGE_MIRROR_APPOINT;
      }
      AppVehicleDTO appVehicleDTO = null;
      ShopDTO shopDTO = null;
      AppUserCustomerDTO appUserCustomerDTO = null;
      IAppUserService iAppUserService = ServiceManager.getService(IAppUserService.class);
      IConfigService iConfigService = ServiceManager.getService(IConfigService.class);
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      }
      //多辆车的时候下拉列表车辆显示排序问题
      List<AppWXUserDTO> appWXUserDTO_list = new ArrayList<AppWXUserDTO>();  //使用的下拉列表
      List<AppWXUserDTO> appWXUserDTO_list_delete = new ArrayList<AppWXUserDTO>(); //待删除的
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        if (appWXUserDTO.getAppUserNo().equals(appUserNo)) {
          appWXUserDTO_list.add(appWXUserDTO);
          appWXUserDTO_list_delete.add(appWXUserDTO);
        }
      }
      appWXUserDTOs.removeAll(appWXUserDTO_list_delete);
      appWXUserDTO_list.addAll(appWXUserDTOs);
      if (CollectionUtil.isNotEmpty(appWXUserDTO_list)) {
        modelMap.put("appWXUserDTOs", appWXUserDTO_list); //下拉列表
      }
      if (StringUtil.isEmptyAppGetParameter(appUserNo)) {
        appUserNo = CollectionUtil.getFirst(appWXUserDTOs).getAppUserNo();
      }
      appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
      modelMap.put("vehicleNo", appVehicleDTO.getVehicleNo()); //车牌号
      appUserCustomerDTO = iAppUserService.getAppUserCustomerDTOByAppUserNoAndAppVehicleId(appUserNo, appVehicleDTO.getVehicleId(), AppUserCustomerMatchType.IMEI_MATCH);
      if (appUserCustomerDTO != null) {
        shopDTO = iConfigService.getShopById(appUserCustomerDTO.getShopId());
      }
      if (shopDTO != null) {
        modelMap.put("shopName", shopDTO.getName()); //店铺名称
        modelMap.put("shopId", shopDTO.getId()); //店铺id
      }

      modelMap.put("appUserNo", appUserNo);
      modelMap.put("openId", openId);
      return PAGE_MIRROR_APPOINT;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 在线预约(“我的预约”)
   *
   * @param request
   * @param modelMap
   * @param openId
   * @param appUserNo
   * @return
   */
  @RequestMapping(value = "/myAppoint/{openId}/{appUserNo}", method = RequestMethod.GET)
  public String toMyAppoint(HttpServletRequest request, ModelMap modelMap, @PathVariable("openId") String openId, @PathVariable("appUserNo") String appUserNo) {
    List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
    modelMap.put("openId", openId);  //oCFjjt2gpABhzgNAjkR1qsB_r6B8
    modelMap.put("appUserNo", appUserNo);
    //      测试
//    AppWXUserDTO appWXUserDTO1 = new AppWXUserDTO();
//    appWXUserDTO1.setAppUserNo("534a0cf81597be55127ad21433db4b71");
//    appWXUserDTOs.add(appWXUserDTO1);
//
    AppVehicleDTO appVehicle_list = null;
    ShopDTO shopDTO = null;
    IAppUserService iAppUserService = ServiceManager.getService(IAppUserService.class);
    IConfigService iConfigService = ServiceManager.getService(IConfigService.class);
    IAppUserVehicleObdService iAppUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    IAppointOrderService iAppointOrderService = ServiceManager.getService(IAppointOrderService.class);
    AppUserCustomerDTO appUserCustomerDTO = new AppUserCustomerDTO();
    AppointOrderSearchCondition searchCondition = new AppointOrderSearchCondition();
    for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
      appVehicle_list = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
      appWXUserDTO.setVehicleNo(appVehicle_list.getVehicleNo());
    }
    LOG.info("appWXUserDTOs size is {}" + appWXUserDTOs.size());
    //多辆车的时候下拉列表车辆显示排序问题
    List<AppWXUserDTO> appWXUserDTO_list = new ArrayList<AppWXUserDTO>();  //使用的下拉列表
    List<AppWXUserDTO> appWXUserDTO_list_delete = new ArrayList<AppWXUserDTO>(); //待删除的
    for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
      if (appWXUserDTO.getAppUserNo().equals(appUserNo)) {
        appWXUserDTO_list.add(appWXUserDTO);
        appWXUserDTO_list_delete.add(appWXUserDTO);
      }
    }
    appWXUserDTOs.removeAll(appWXUserDTO_list_delete);
    appWXUserDTO_list.addAll(appWXUserDTOs);
    LOG.info("appWXUserDTO_list size is {}" + appWXUserDTO_list.size());
    if (CollectionUtil.isNotEmpty(appWXUserDTO_list)) {
      modelMap.put("appWXUserDTOs", appWXUserDTO_list); //下拉列表
    }
    if (StringUtil.isEmpty(appUserNo)) {
      appUserNo = CollectionUtil.getFirst(appWXUserDTOs).getAppUserNo();
    }

    List<AppointOrderDTO> appointOrderDTOs = new ArrayList<AppointOrderDTO>();
    try {
      String[] appUserNo_ = new String[]{appUserNo};
      AppointOrderStatus[] appointOrderStatus = new AppointOrderStatus[]{AppointOrderStatus.PENDING, AppointOrderStatus.ACCEPTED, AppointOrderStatus.TO_DO_REPAIR, AppointOrderStatus.HANDLED, AppointOrderStatus.REFUSED};
      AppGsmVehicleResponse apiResponse = iAppUserVehicleObdService.gsmUserGetAppVehicle(appUserNo);
      if (apiResponse != null && apiResponse.getVehicleInfo() != null) {
        appUserCustomerDTO = iAppUserService.getAppUserCustomerDTOByAppUserNoAndAppVehicleId(appUserNo, apiResponse.getVehicleInfo().getVehicleId(), AppUserCustomerMatchType.IMEI_MATCH);
        if (appUserCustomerDTO != null && appUserCustomerDTO.getShopId() != null) {
          searchCondition.setShopId(appUserCustomerDTO.getShopId());
          searchCondition.setAppUserNos(appUserNo_);
          searchCondition.setAppointWay(null);
          searchCondition.setVehicleNo(apiResponse.getVehicleInfo().getVehicleNo());
          searchCondition.setAppointOrderStatus(appointOrderStatus);
          appointOrderDTOs = iAppointOrderService.searchAppointOrderDTOs(searchCondition);
          if (CollectionUtil.isNotEmpty(appointOrderDTOs)) {
            for (AppointOrderDTO appointOrderDTO : appointOrderDTOs) {
              shopDTO = iConfigService.getShopById(appointOrderDTO.getShopId());
              appointOrderDTO.setShopName(shopDTO.getName());
              appointOrderDTO.setShopAddress(shopDTO.getAddress());
              appointOrderDTO.setShopMobile(shopDTO.getMobile());
            }
          }
        }
      }
      modelMap.put("appointOrderDTOs", appointOrderDTOs); //我的预约
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return PAGE_MYAPPOINT;
  }

  /**
   * 在线预约(添加预约信息)
   *
   * @param appServiceDTO
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/saveAppoint", method = RequestMethod.POST)
  public Result saveMirrorAppoint(AppServiceDTO appServiceDTO) {
    try {
      if (StringUtil.isEmpty(appServiceDTO.getOpenId())) {
        return new Result(false, "用户信息异常。");
      }
      Result result = new Result();
//      String errMsg = appServiceDTO.validate("wx");
//      if (!appServiceDTO.isSuccess(errMsg)) {
//        return result.LogErrorMsg(errMsg);
//      }
      appServiceDTO.setAppointTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, appServiceDTO.getAppointTimeStr()));
      String receiptNo = ServiceManager.getService(ITxnService.class).getReceiptNo(appServiceDTO.getShopId(), OrderTypes.APPOINT_ORDER, null);
      if (StringUtil.isEmpty(receiptNo)) return result.LogErrorMsg("单据号生成错误");
      appServiceDTO.setReceiptNo(receiptNo);
      appServiceDTO.setAppointWay(AppointWay.WECHAT);
      IAppOrderService appOrderService = ServiceManager.getService(IAppOrderService.class);
      result = appOrderService.saveWXAppointOrder(appServiceDTO);
      if (!result.isSuccess()) return result;
      String openId = appServiceDTO.getOpenId();
      //更新WXUser的  mobile
      WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
      userDTO.setMobile(appServiceDTO.getMobile());
      wxUserService.saveOrUpdateWXUser(userDTO);
      //给店铺推送预约消息
      IAppointPushMessageService pushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
      AppointOrder appointOrder = (AppointOrder) result.getData();
      AppointOrderDTO appointOrderDTO = appointOrderService.getAppointOrderById(appointOrder.getShopId(), appointOrder.getId());
      List<AppAppointParameter> appAppointParameterList = appointOrderService.createAppAppointParameter(appointOrderDTO);
      if (CollectionUtil.isNotEmpty(appAppointParameterList)) {
        for (AppAppointParameter appAppointParameter : appAppointParameterList) {
          pushMessageService.createAppApplyAppointMessage(appAppointParameter);
        }
      }
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("预约服务暂不可用。");
    }
  }

  /**
   * 在线预约(“取消预约”)
   *
   * @param request
   * @param modelMap
   * @param openId
   * @param shopId
   * @param appointOrderId
   * @return
   */
  @RequestMapping(value = "/deleteAppoint/{openId}/{shopId}/{appointOrderId}/{appUserNo}", method = RequestMethod.GET)
  public String deleteAppoint(HttpServletRequest request, ModelMap modelMap, @PathVariable("openId") String openId, @PathVariable("shopId") String shopId, @PathVariable("appointOrderId") String appointOrderId, @PathVariable("appUserNo") String appUserNo) {
    IAppointOrderService iAppointOrderService = ServiceManager.getService(IAppointOrderService.class);
    try {
      AppointOrderDTO appointOrderDTO = iAppointOrderService.getAppointOrderById(Long.valueOf(shopId), Long.valueOf(appointOrderId));
      appointOrderDTO.setStatus(AppointOrderStatus.CANCELED);
      iAppointOrderService.updateAppointOrder_status(appointOrderDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return toMyAppoint(request, modelMap, openId, appUserNo);
  }

  /**
   * 跳转到轨迹详细
   *
   * @param modelMap
   * @param driveLogId
   * @return
   */
  @RequestMapping(value = "/wx/toDriveLogDetail/{driveLogId}/{openId}/{startTime}/{endTime}")
  public String toDriveLogDetail(ModelMap modelMap, @PathVariable("driveLogId") Long driveLogId, @PathVariable("openId") String openId, @PathVariable("startTime") Long startTime, @PathVariable("endTime") Long endTime) {
    try {
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      DriveLogDTO driveLogDTO = driveLogService.getDriveLogDTOWidthPlaceNoteById(driveLogId);
      if (driveLogDTO == null) {
        modelMap.put("result", new PageErrorMsg("行车轨迹不存在", "请刷新后再试"));
        return Constant.PAGE_ERROR;
      }
      driveLogDTO.setDistance(driveLogDTO.getDistance() == null ? 0 : driveLogDTO.getDistance());
      if (driveLogDTO.getDistance() < 0 || driveLogDTO.getDistance() > 10000) {
        driveLogDTO.setDistance(0.0);
      }
      driveLogDTO.setOilCost(driveLogDTO.getOilCost() == null ? 0 : driveLogDTO.getOilCost());
      if (driveLogDTO.getOilCost() < 0 || driveLogDTO.getOilCost() > 10000) {
        driveLogDTO.setOilCost(0.0);
      }
      driveLogDTO.setTotalOilMoney(driveLogDTO.getTotalOilMoney() == null ? 0 : driveLogDTO.getTotalOilMoney());
      if (driveLogDTO.getTotalOilMoney() < 0 || driveLogDTO.getTotalOilMoney() > 10000) {
        driveLogDTO.setTotalOilMoney(0.0);
      }
      if (driveLogDTO.getTravelTime() < 0 || driveLogDTO.getTravelTime() > 10000) {
        driveLogDTO.setTravelTimeStr("0");
      }
      AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(driveLogDTO.getAppUserNo()));
      if (appVehicleDTO != null) {
        appVehicleDTO.setWorstOilWear(appVehicleDTO.getWorstOilWear() == null ? 0 : appVehicleDTO.getWorstOilWear());
        if (appVehicleDTO.getWorstOilWear() < 0 || appVehicleDTO.getWorstOilWear() > 10000) {
          appVehicleDTO.setWorstOilWear(0.0);
        }
        appVehicleDTO.setAvgOilWear(appVehicleDTO.getAvgOilWear() == null ? 0 : appVehicleDTO.getAvgOilWear());
        if (appVehicleDTO.getAvgOilWear() < 0 || appVehicleDTO.getAvgOilWear() > 10000) {
          appVehicleDTO.setAvgOilWear(0.0);
        }
        appVehicleDTO.setBestOilWear(appVehicleDTO.getBestOilWear() == null ? 0 : appVehicleDTO.getBestOilWear());
        if (appVehicleDTO.getBestOilWear() < 0 || appVehicleDTO.getBestOilWear() > 10000) {
          appVehicleDTO.setBestOilWear(0.0);
        }
      }
      modelMap.put("driveLogDTO", driveLogDTO);
      modelMap.put("ak", ServiceManager.getService(IConfigService.class).getConfig("baidu_map_ak", ShopConstant.BC_SHOP_ID));
      modelMap.put("appVehicle", appVehicleDTO);
      modelMap.put("startTime", startTime);
      modelMap.put("endTime", endTime);
      modelMap.put("openId", openId);
      modelMap.put("appUserNo", driveLogDTO.getAppUserNo());
      return PAGE_DRIVE_DETAIL;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 删除行车日志
   *
   * @param modelMap
   * @param driveLogId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/wx/driveLog/delete/{driveLogId}", method = RequestMethod.POST)
  public Object deleteDriveLog(ModelMap modelMap, @PathVariable("driveLogId") Long driveLogId) {
    try {
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      DriveLogDTO driveLogDTO = driveLogService.getDriveLogDTOById(driveLogId);
      if (driveLogDTO == null) {
        return new Result(true, "行车日志不存在或已经删除。");
      }
      driveLogDTO.setStatus(DriveLogStatus.DISABLED);
      LOG.info("driveLog delete start,id:{}", driveLogId);
      driveLogService.saveOrUpdateDriveLog(driveLogDTO);
      LOG.info("driveLog delete end");
      return new Result(true, "删除成功。");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(true, "删除出现异常。");
    }
  }

  /**
   * 查询行车轨迹
   *
   * @param openId
   * @param startTime
   * @param endTime
   * @return
   */
  @RequestMapping(value = "/2DriveLog/{openId}/{startTime}/{endTime}/{appUserNo}", method = RequestMethod.GET)
  public String getDriveLog(ModelMap modelMap,
                            @PathVariable("openId") String openId,
                            @PathVariable("startTime") String startTime,
                            @PathVariable("endTime") String endTime,
                            @PathVariable("appUserNo") String appUserNo) {
    try {
      LOG.info("2DriveLog,openId:{},appUserNo:{}", openId, appUserNo);
      Long _startTime = null;
      Long _endTime = null;
      LOG.info("startTime:{},endTime:{}", startTime, endTime);
      if (StringUtil.isEmptyAppGetParameter(startTime)
        && StringUtil.isEmptyAppGetParameter(endTime)) {
        _startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm:ss", DateUtil.getNowWeekBegin());
        _endTime = DateUtil.getAfterNDaysDate(DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm:ss", DateUtil.getNowWeekBegin()), 7);
      } else {
        _startTime = NumberUtil.longValue(startTime);
        _endTime = NumberUtil.longValue(endTime);
      }
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      LOG.info("getAppWXUserDTO,openId:{}", openId);
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      LOG.info("appWXUserDTOs is {}", JsonUtil.listToJson(appWXUserDTOs));
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        LOG.info("appWXUserDTOs is empty!");
        modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("您当前还没关注任何车辆！", "请刷新后再试")));
        return PAGE_DRIVE_LOG;
      }
      AppVehicleDTO appVehicleDTO = null;
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      }
      if (StringUtil.isEmptyAppGetParameter(appUserNo)) {
        appUserNo = CollectionUtil.getFirst(appWXUserDTOs).getAppUserNo();
      }
      LOG.info("step3");
      //多辆车的时候下拉列表车辆显示排序问题
      List<AppWXUserDTO> appWXUserDTO_list = new ArrayList<AppWXUserDTO>();  //使用的下拉列表
      List<AppWXUserDTO> appWXUserDTO_list_delete = new ArrayList<AppWXUserDTO>(); //待删除的
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        if (appWXUserDTO.getAppUserNo().equals(appUserNo)) {
          appWXUserDTO_list.add(appWXUserDTO);
          appWXUserDTO_list_delete.add(appWXUserDTO);
        }
      }
      appWXUserDTOs.removeAll(appWXUserDTO_list_delete);
      appWXUserDTO_list.addAll(appWXUserDTOs);
      if (CollectionUtil.isNotEmpty(appWXUserDTO_list)) {
        modelMap.put("appWXUserDTOs", appWXUserDTO_list); //下拉列表
      }
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      List<DriveLogDTO> driveLogDTOList = driveLogService.getDriveLogDTOList(appUserNo, _startTime, _endTime);
      modelMap.put("driveLogDTOList", driveLogDTOList);
      modelMap.put("driveLogDTOList_num", driveLogDTOList.size());
      modelMap.put("startTime", _startTime);
      modelMap.put("endTime", _endTime);
      modelMap.put("openId", openId);
      return PAGE_DRIVE_LOG;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("error", e.getMessage(), "请刷新后再试")));
      return PAGE_DRIVE_LOG;
    }
  }


  /**
   * 车辆定位
   *
   * @param request
   * @param modelMap
   * @param openId
   * @param appUserNo
   * @return
   */
  @RequestMapping(value = "/2VLocation/{openId}/{appUserNo}", method = RequestMethod.GET)
  public String toVehicleLocation(HttpServletRequest request, ModelMap modelMap,
                                  @PathVariable("openId") String openId,
                                  @PathVariable("appUserNo") String appUserNo) {
    try {

      LOG.info("mirror 2VLocation,openId is {},appUserNo is {}", openId, appUserNo);
      modelMap.put("openId", openId);
      modelMap.put("ak", ServiceManager.getService(IConfigService.class).getConfig("baidu_map_ak", ShopConstant.BC_SHOP_ID));
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        LOG.warn("appWXUserDTOs is empty!");
        modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("您当前还没关注任何车辆！", "请刷新后再试")));
        return PAGE_VEHICLE_LOCATION;
      }
      AppVehicleDTO appVehicleDTO = null;
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      }
      if (StringUtil.isEmptyAppGetParameter(appUserNo)) {
        appUserNo = CollectionUtil.getFirst(appWXUserDTOs).getAppUserNo();
      }
      //多辆车的时候下拉列表车辆显示排序问题
      List<AppWXUserDTO> appWXUserDTO_list = new ArrayList<AppWXUserDTO>();  //使用的下拉列表
      List<AppWXUserDTO> appWXUserDTO_list_delete = new ArrayList<AppWXUserDTO>(); //待删除的
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        if (appWXUserDTO.getAppUserNo().equals(appUserNo)) {
          appWXUserDTO_list.add(appWXUserDTO);
          appWXUserDTO_list_delete.add(appWXUserDTO);
        }
      }
      appWXUserDTOs.removeAll(appWXUserDTO_list_delete);
      appWXUserDTO_list.addAll(appWXUserDTOs);
      if (CollectionUtil.isNotEmpty(appWXUserDTO_list)) {
        modelMap.put("appWXUserDTOs", appWXUserDTO_list); //下拉列表
      }
      GsmVehicleDataDTO gsmVehicleDataDTO = ServiceManager.getService(IGSMVehicleDataService.class).getLastGsmVehicleData(appUserNo);
      if (gsmVehicleDataDTO == null) {
        modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("您当前还没有任何车况信息！", "请刷新后再试")));
        return PAGE_VEHICLE_LOCATION;
      }
      Coordinate coordinate = ServiceManager.getService(IGeocodingService.class).coordinateGspToBaiDu(gsmVehicleDataDTO.getLon(), gsmVehicleDataDTO.getLat());
      if (coordinate != null) {
        gsmVehicleDataDTO.setLat(coordinate.getLat());
        gsmVehicleDataDTO.setLon(coordinate.getLng());
      }
      modelMap.put("gsmVehicleDataDTO", gsmVehicleDataDTO);
      //生成jsapi_ticket的签名
      WXUserDTO wxUserDTO = wxUserService.getWXUserDTOByOpenId(openId);
      StringBuilder sb = new StringBuilder();
      sb.append(request.getRequestURL().toString());
      LOG.info("sign url:{}", sb.toString());
      WXJsApiTicketSign ticketSign = ServiceManager.getService(IWXUserService.class).getWXJsApiTicketSign(wxUserDTO.getPublicNo(), sb.toString());
      String ticketSignJson = JsonUtil.objectToJson(ticketSign);
      LOG.info("ticketSignJson:{}", ticketSignJson);
      modelMap.put("ticketSignJson", JsonUtil.objectToJson(ticketSign));
      return PAGE_VEHICLE_LOCATION;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("error", e.getMessage(), "请刷新后再试")));
      return PAGE_VEHICLE_LOCATION;
    }
  }

  /**
   * 故障查询(点击“已处理”)
   *
   * @param modelMap
   * @param openId
   * @param id
   * @return
   */
  @RequestMapping(value = "/updateFaultCode/{openId}/{id}", method = RequestMethod.GET)
  public String updateVehicleFaultCode(ModelMap modelMap, @PathVariable("openId") String openId, @PathVariable("id") String id) {
    LOG.info("wx:updateVehicleFaultCode idStr is {}", id);
    IAppVehicleFaultCodeService iAppVehicleFaultCodeService = ServiceManager.getService(IAppVehicleFaultCodeService.class);
    AppVehicleFaultInfo appVehicleFaultInfo = null;
    try {
      appVehicleFaultInfo = iAppVehicleFaultCodeService.getAppVehicleFaultInfoById(Long.valueOf(id)); //该方法获取故障码详细信息（）
      //修改状态为"已处理"
      appVehicleFaultInfo.setStatus(ErrorCodeTreatStatus.FIXED);
      iAppVehicleFaultCodeService.updateAppVehicleFaultInfo(appVehicleFaultInfo);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return toVehicleFaultCode(null, modelMap, appVehicleFaultInfo.getAppUserNo(), openId, "UNTREATED");
  }


  /**
   * 故障查询
   *
   * @param request
   * @param modelMap
   * @param appUserNo
   * @param openId
   * @param status
   * @return
   */
  @RequestMapping(value = "/faultCode/{appUserNo}/{openId}/{status}", method = RequestMethod.GET)
  public String toVehicleFaultCode(HttpServletRequest request, ModelMap modelMap,
                                   @PathVariable("appUserNo") String appUserNo,
                                   @PathVariable("openId") String openId,
                                   @PathVariable("status") String status) {//UNTREATED("未处理"),FIXED("已修复")
    AppVehicleDTO appVehicleDTO = null;
    String flag = "";
    String button_flag = "";
    IAppDictionaryService iAppDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
    try {
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        if (appVehicleDTO != null) {
          appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
        }
      }
      //多辆车的时候下拉列表车辆显示排序问题
      List<AppWXUserDTO> appWXUserDTO_list = new ArrayList<AppWXUserDTO>();  //使用的下拉列表
      List<AppWXUserDTO> appWXUserDTO_list_delete = new ArrayList<AppWXUserDTO>(); //待删除的
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        if (appWXUserDTO.getAppUserNo().equals(appUserNo)) {
          appWXUserDTO_list.add(appWXUserDTO);
          appWXUserDTO_list_delete.add(appWXUserDTO);
        }
      }
      appWXUserDTOs.removeAll(appWXUserDTO_list_delete);
      appWXUserDTO_list.addAll(appWXUserDTOs);
      if (CollectionUtil.isNotEmpty(appWXUserDTO_list)) {
        modelMap.put("appWXUserDTOs", appWXUserDTO_list); //下拉列表
      }

      if (StringUtil.isEmpty(status) || "UNTREATED".equals(status)) {
        status = "UNTREATED";
        flag = "UNTREATED";
        button_flag = "UNTREATED";
      }
      if (StringUtil.isEmptyAppGetParameter(appUserNo)) {
        appUserNo = CollectionUtil.getFirst(appWXUserDTOs).getAppUserNo();
      }
      IAppVehicleFaultCodeService iAppVehicleFaultCodeService = ServiceManager.getService(IAppVehicleFaultCodeService.class);
      Set<String> codes = new HashSet<String>();
      //根据appUserNo和status查故障码列表
      List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOList = iAppVehicleFaultCodeService.findAppVehicleFaultInfoDTOs(appUserNo, status);
      for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : appVehicleFaultInfoDTOList) {
        codes.add(appVehicleFaultInfoDTO.getErrorCode());
      }
      appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
      if (appVehicleDTO != null) {
        Map<String, List<DictionaryFaultInfoDTO>> dictionaryFaultInfoMap = iAppDictionaryService.getDictionaryFaultInfoMapByBrandIdAndCodes(appVehicleDTO.getVehicleBrandId(), codes);
        for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : appVehicleFaultInfoDTOList) {
          appVehicleFaultInfoDTO.setReportTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, appVehicleFaultInfoDTO.getReportTime()));
          appVehicleFaultInfoDTO.setIdStr(String.valueOf(appVehicleFaultInfoDTO.getId()));
          DictionaryFaultInfoDTO dictionaryFaultInfoDTO = CollectionUtil.getFirst(dictionaryFaultInfoMap.get(appVehicleFaultInfoDTO.getErrorCode()));
          appVehicleFaultInfoDTO.setFlag(flag);
//        DictionaryFaultInfoDTO dictionaryFaultInfoDTO = iAppDictionaryService.getDictionaryFaultInfoDTOByFaultCode(errorCode); //该方法获取故障码详细信息（）
          if (dictionaryFaultInfoDTO != null) {
            appVehicleFaultInfoDTO.setCategory(dictionaryFaultInfoDTO.getCategory());  //故障类型
            appVehicleFaultInfoDTO.setBackgroundInfo(dictionaryFaultInfoDTO.getBackgroundInfo());  //背景知识
          }
        }
      }
      List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOList_del = new ArrayList<AppVehicleFaultInfoDTO>();
      for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO_del : appVehicleFaultInfoDTOList) {
        if (StringUtil.isEmpty(appVehicleFaultInfoDTO_del.getContent())) {
          appVehicleFaultInfoDTOList_del.add(appVehicleFaultInfoDTO_del);
        }
      }
      appVehicleFaultInfoDTOList.removeAll(appVehicleFaultInfoDTOList_del);

      //故障码暂时不使用
      List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOList_null = new ArrayList<AppVehicleFaultInfoDTO>();

      modelMap.put("appVehicleFaultInfoDTOList", appVehicleFaultInfoDTOList_null);
      modelMap.put("appVehicleFaultInfoDTO_num", appVehicleFaultInfoDTOList_null.size());
      modelMap.put("status", status);
      modelMap.put("openId", openId);
      modelMap.put("button_flag", button_flag);
      return PAGE_VEHICLE_FAULT_CODE;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }


  /**
   * 违章查询
   *
   * @param request
   * @param openId
   * @param appUserNo
   * @return
   */
  @RequestMapping(value = "/violate/{openId}/{appUserNo}", method = RequestMethod.GET)
  public String toVehicleViolate(HttpServletRequest request,
                                 ModelMap modelMap,
                                 @PathVariable("openId") String openId,
                                 @PathVariable("appUserNo") String appUserNo) {
    try {
      LOG.info("wx:toVehicleViolate openId is {},appUserNo is {}", openId, appUserNo);
      modelMap.put("openId", openId);
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        LOG.info("appWXUserDTOs size is empty");
        return PAGE_ILLEGAL_QUERY;
      }
      Long fen = 0L;
      Long money = 0L;
      AppVehicleDTO appVehicleDTO = null;
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      }
      if (StringUtil.isEmptyAppGetParameter(appUserNo)) {
        appUserNo = CollectionUtil.getFirst(appWXUserDTOs).getAppUserNo();
      }
      LOG.info("appUserNo value is {}" + appUserNo);
      LOG.info("appWXUserDTOs size is {}" + appWXUserDTOs.size());
      //多辆车的时候下拉列表车辆显示排序问题
      List<AppWXUserDTO> appWXUserDTO_list = new ArrayList<AppWXUserDTO>();  //使用的下拉列表
      List<AppWXUserDTO> appWXUserDTO_list_delete = new ArrayList<AppWXUserDTO>(); //待删除的
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        if (appWXUserDTO.getAppUserNo().equals(appUserNo)) {
          appWXUserDTO_list.add(appWXUserDTO);
          appWXUserDTO_list_delete.add(appWXUserDTO);
        }
      }
      appWXUserDTOs.removeAll(appWXUserDTO_list_delete);
      appWXUserDTO_list.addAll(appWXUserDTOs);
      LOG.info("appWXUserDTO_list size is {}" + appWXUserDTO_list.size());
      if (CollectionUtil.isNotEmpty(appWXUserDTO_list)) {
        modelMap.put("appWXUserDTOs", appWXUserDTO_list); //下拉列表
      }
      IAppVehicleService vehicleService = ServiceManager.getService(IAppVehicleService.class);
      Result result = vehicleService.getVRegulationRecordDTO(appUserNo);
      if (result.isSuccess()) {
        List<VehicleViolateRegulationRecordDTO> recordDTOs = (List<VehicleViolateRegulationRecordDTO>) result.getData();
        if (CollectionUtils.isNotEmpty(recordDTOs)) {
          for (VehicleViolateRegulationRecordDTO vehicleViolateRegulationRecordDTO : recordDTOs) {
            fen += Long.valueOf(vehicleViolateRegulationRecordDTO.getFen());
            money += Long.valueOf(vehicleViolateRegulationRecordDTO.getMoney());
          }
        }
        modelMap.put("recordDTOs", recordDTOs);
        modelMap.put("recordDTOs_size", recordDTOs == null ? 0 : recordDTOs.size());
        modelMap.put("recordDTOs_fen", fen);
        modelMap.put("recordDTOs_money", money);
      } else {
        modelMap.put("reason", result.getMsg());
      }
      return PAGE_ILLEGAL_QUERY;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  @RequestMapping(value = "/2VideoP/{impact_video_id}", method = RequestMethod.GET)
  public String toImpactVideo(ModelMap modelMap, @PathVariable("impact_video_id") Long impact_video_id) {
    try {
      IImpactService impactService = ServiceManager.getService(IImpactService.class);
      String url = impactService.getImpactVideoUrl(impact_video_id);
      modelMap.put("url", url);
      return PAGE_VIDEO_PLAY;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }


  /**
   * 碰撞视频
   *
   * @param modelMap
   * @param openId
   * @return
   */
  @RequestMapping(value = "/2Video/{openId}", method = RequestMethod.GET)
  public String toImpactVideo(ModelMap modelMap, @PathVariable("openId") String openId) {
    try {
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        return PAGE_VIDEO;
      }
      AppVehicleDTO appVehicleDTO = null;
      AddressComponent addressComponent = null;
      List<ImpactVideoExpDTO> impactVideoExpDTOs = new ArrayList<ImpactVideoExpDTO>();
      IGeocodingService iGeocodingService = ServiceManager.getService(IGeocodingService.class);
      IImpactService impactService = ServiceManager.getService(IImpactService.class);
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        IImpactService iImpactService = ServiceManager.getService(IImpactService.class);
        List<ImpactVideoExpDTO> impactVideoExpDTOList = iImpactService.getImpactVideoExpDTOByAppUserNo(appWXUserDTO.getAppUserNo());
        if (CollectionUtil.isNotEmpty(impactVideoExpDTOList)) {
          for (ImpactVideoExpDTO impactVideoExpDTO : impactVideoExpDTOList) {
            appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
            impactVideoExpDTO.setVehicleNo(appVehicleDTO.getVehicleNo());//碰撞车牌号
            addressComponent = iGeocodingService.gpsToAddress(impactVideoExpDTO.getLatitude(), impactVideoExpDTO.getLongitude());
            if (addressComponent != null) {
              impactVideoExpDTO.setAddress(addressComponent.getDistrict() + addressComponent.getStreet()); //碰撞地址
            }
            impactVideoExpDTO.setUploadTimeDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, impactVideoExpDTO.getUploadTime()));
            impactVideoExpDTO.setUrl(impactService.getImpactVideoUrl(NumberUtil.longValue(impactVideoExpDTO.getImpactVideoId())));
            impactVideoExpDTOs.add(impactVideoExpDTO);
          }
        }
      }
      modelMap.put("impactVideoExpDTOs", impactVideoExpDTOs);
      modelMap.put("impact_num", impactVideoExpDTOs.size());//碰撞视频总数，方便界面统计
      return PAGE_VIDEO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 救援电话
   *
   * @param request
   * @param modelMap
   * @param openId
   * @return
   */
  @RequestMapping(value = "/aMobile/{openId}", method = RequestMethod.GET)
  public String toAccidentMobile(HttpServletRequest request, ModelMap modelMap, @PathVariable("openId") String openId) {
    try {
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
      AppVehicleDTO appVehicleDTO = null;
      OneKeyRescueDTO oneKeyRescueDTO = null;
      List<OneKeyRescueDTO> oneKeyRescueDTOList = new ArrayList<OneKeyRescueDTO>();
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        oneKeyRescueDTO = new OneKeyRescueDTO();
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        oneKeyRescueDTO.setVehicleNo(appVehicleDTO.getVehicleNo()); //车牌号
        OneKeyRescueResponse oneKeyRescueResponse = iRescueService.findOneKeyRescueDetails(appWXUserDTO.getAppUserNo());
        oneKeyRescueDTO.setMirror_mobile(oneKeyRescueResponse.getMirror_mobile()); //后视镜问题反映电话
        oneKeyRescueDTO.setAccident_mobile(oneKeyRescueResponse.getAccident_mobile()); //事故专员电话
        oneKeyRescueDTO.setInsuranceCompanyDTO(oneKeyRescueResponse.getInsuranceCompanyDTO()); //放在第一位的保险公司和电话
        List<InsuranceCompanyDTO> insuranceCompanyDTOList = new ArrayList<InsuranceCompanyDTO>();
        int n = 0;
        if (CollectionUtil.isNotEmpty(oneKeyRescueResponse.getInsuranceCompanyDTOs())) {
          for (InsuranceCompanyDTO insuranceCompanyDTO : oneKeyRescueResponse.getInsuranceCompanyDTOs()) {
            if (n < 2) {
              insuranceCompanyDTOList.add(insuranceCompanyDTO);
              n++;
            }
          }
        }
        oneKeyRescueDTO.setInsuranceCompanyDTOs(insuranceCompanyDTOList);  //其他保险公司和电话（2个）
        oneKeyRescueDTOList.add(oneKeyRescueDTO);
      }
      modelMap.put("oneKeyRescueDTOList", oneKeyRescueDTOList);
      modelMap.put("openId", openId);
      return PAGE_MOBILE;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 发送消息
   *
   * @param openId
   * @param appUserNo
   * @param content
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/wx/msg/send", method = RequestMethod.POST)
  public Object sendToMirror(String openId, String appUserNo, String content) {
    try {
      LOG.info("receive msg from wx,content is {}", content);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
      AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
      List<AppVehicleDTO> appVehicleDTOs = new ArrayList<AppVehicleDTO>();
      appVehicleDTOs.add(appVehicleDTO);
      appUserDTO.setAppVehicleDTOs(appVehicleDTOs);
      ServiceManager.getService(IAppointPushMessageService.class).saveTalkMessage2App(openId, appUserDTO, content, PushMessageType.MSG_FROM_WX_USER_TO_MIRROR);
      return new Result(true, "发送成功");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false, "发送异常");
    }
  }


  /**
   * 我的消息
   *
   * @param request
   * @param modelMap
   * @param openId
   * @return
   */
  @RequestMapping(value = "/myMsg/{openId}", method = RequestMethod.GET)
  public String toMyMessage(HttpServletRequest request, ModelMap modelMap, @PathVariable("openId") String openId) {
    try {
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        return PAGE_MY_MESSAGE;
      }
      AppVehicleDTO appVehicleDTO = null;
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      }
      modelMap.put("appWXUserDTOs", appWXUserDTOs);
      AppWXUserDTO wxUserDTO = CollectionUtil.getFirst(appWXUserDTOs);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(wxUserDTO.getAppUserNo());
      MessageResponse response = ServiceManager.getService(IAppPushMessageService.class)
        .getPollingMessage(appUserDTO.getId(), 100, PushMessageType.getAppUserPushMessageWithOutTalk());
      modelMap.put("messageList", response.getMessageList());
      WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
      modelMap.put("userDTO", userDTO);
      modelMap.put("appUserNo", appUserDTO.getUserNo());
      modelMap.put("type", "myMessage");
      return PAGE_MY_MESSAGE;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 查询最近10条对话信息
   *
   * @param condition
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/msg/list", method = RequestMethod.POST)
  public Object getTalkList(TalkMessageCondition condition) {
    try {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(condition.getAppUserNo());
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      List<PushMessageType> types = new ArrayList<PushMessageType>();
      String type = condition.getType();
      if ("talkWithCat".equals(type)) {
        types.add(PushMessageType.MSG_FROM_WX_USER_TO_MIRROR);
        types.add(PushMessageType.MSG_FROM_MIRROR_TO_WX_USER);
      } else if ("talkWith4S".equals(type)) {
        types.add(PushMessageType.MSG_FROM_SHOP_TO_WX_USER);
        types.add(PushMessageType.MSG_FROM_WX_USER_TO_SHOP);
      }
      condition.setReceiverId(appUserDTO.getId());
      condition.setTypes(types.toArray(new PushMessageType[types.size()]));
      List<PushMessageDTO> pushMessages = pushMessageService.getTalkMessageList(condition);
      PagingListResult listResult = new PagingListResult<PushMessageDTO>();
      listResult.setResults(pushMessages);
//      int nextStart = start + (CollectionUtil.isNotEmpty(pushMessages) ? pushMessages.size() : 0);
//      listResult.setData(nextStart);
      return listResult;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false, "发送异常");
    }
  }


  /**
   * 对话
   *
   * @param request
   * @param modelMap
   * @param openId
   * @param appUserNo
   * @return
   */
  @RequestMapping(value = "/to_talk/{type}/{openId}/{appUserNo}", method = RequestMethod.GET)
  public String toTalk(HttpServletRequest request, ModelMap modelMap, @PathVariable("type") String type,
                       @PathVariable("openId") String openId, @PathVariable("appUserNo") String appUserNo) {
    try {
      LOG.info("to_talk,openId={},appUserNo={}", openId, appUserNo);
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        return PAGE_WX_TALK;
      }
      AppVehicleDTO appVehicleDTO = null;
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      }
      modelMap.put("appWXUserDTOs", appWXUserDTOs);
      //缺省显示最近三条对话消息
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
      List<PushMessageType> types = new ArrayList<PushMessageType>();
      if (PushMessageType.MSG_FROM_WX_USER_TO_MIRROR.toString().equals(type)) {  //与车对话
        types.add(PushMessageType.MSG_FROM_WX_USER_TO_MIRROR);
        types.add(PushMessageType.MSG_FROM_MIRROR_TO_WX_USER);
        modelMap.put("onLine",
          ServiceManager.getService(IMQClientService.class).isOnLine(appUserNo) ? "none" : "block");
      } else {     //与4s店对话
        types.add(PushMessageType.MSG_FROM_SHOP_TO_WX_USER);
        types.add(PushMessageType.MSG_FROM_WX_USER_TO_SHOP);
      }
      TalkMessageCondition condition = new TalkMessageCondition();
      condition.setReceiverId(appUserDTO.getId());
      condition.setStart(0);
      condition.setLimit(3);
      condition.setTypes(types.toArray(new PushMessageType[types.size()]));
      List<PushMessageDTO> pushMessages = pushMessageService.getTalkMessageList(condition);
      modelMap.put("pushMessages", pushMessages);
      modelMap.put("userDTO", wxUserService.getWXUserDTOByOpenId(openId));
      modelMap.put("appUserNo", appUserNo);
      modelMap.put("openId", openId);
      modelMap.put("shopId", appUserDTO.getRegistrationShopId());
      modelMap.put("type", type);
      modelMap.put("wsUrl", ConfigUtils.getWSUrl());
      return PAGE_WX_TALK;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 车况检查
   *
   * @param request
   * @param modelMap
   * @param openId
   * @param appUserNo
   * @return
   */
  @RequestMapping(value = "/gvData/{openId}/{appUserNo}", method = RequestMethod.GET)
  public String toGsmVehicleData(HttpServletRequest request, ModelMap modelMap,
                                 @PathVariable("openId") String openId,
                                 @PathVariable("appUserNo") String appUserNo) {
    try {
      LOG.info("wx:gvData openId is {},appUserNo is {}", openId, appUserNo);
      List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isEmpty(appWXUserDTOs)) {
        LOG.warn("appWXUserDTOs is empty!");
        modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("您当前还没关注任何车辆！", "请刷新后再试")));
        return PAGE_VEHICLE_LOCATION;
      }
      int i = 0;
      AppVehicleDTO appVehicleDTO = null;
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appWXUserDTO.getAppUserNo()));
        appWXUserDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
      }

      //多辆车的时候下拉列表车辆显示排序问题
      List<AppWXUserDTO> appWXUserDTO_list = new ArrayList<AppWXUserDTO>();  //使用的下拉列表
      List<AppWXUserDTO> appWXUserDTO_list_delete = new ArrayList<AppWXUserDTO>(); //待删除的
      for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
        if (appWXUserDTO.getAppUserNo().equals(appUserNo)) {
          appWXUserDTO_list.add(appWXUserDTO);
          appWXUserDTO_list_delete.add(appWXUserDTO);
        }
      }
      appWXUserDTOs.removeAll(appWXUserDTO_list_delete);
      appWXUserDTO_list.addAll(appWXUserDTOs);
      if (CollectionUtil.isNotEmpty(appWXUserDTO_list)) {
        modelMap.put("appWXUserDTOs", appWXUserDTO_list); //下拉列表
      }
      IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
      if (StringUtil.isEmptyAppGetParameter(appUserNo)) {
        appUserNo = CollectionUtil.getFirst(appWXUserDTOs).getAppUserNo();
      }
      GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(appUserNo);
      if (gsmVehicleDataDTO == null) {
        modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("您当前还没有任何车况信息！", "请刷新后再试")));
        return PAGE_GVDATA;
      }
      if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getThrottlePosition()) && gsmVehicleDataDTO.getThrottlePosition().length() > 5) {
        gsmVehicleDataDTO.setThrottlePosition(gsmVehicleDataDTO.getThrottlePosition().substring(0, 4));
      }
      //油耗问题处理
      if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getrOilMass()) && gsmVehicleDataDTO.getrOilMass().length() > 4) {
        gsmVehicleDataDTO.setrOilMass(gsmVehicleDataDTO.getrOilMass().substring(0, 3));
      }
//      if(StringUtil.isNotEmpty(gsmVehicleDataDTO.getrOilMassType())){
//              if("1".equals(gsmVehicleDataDTO.getrOilMassType())){
//                gsmVehicleDataDTO.setrOilMass(gsmVehicleDataDTO.getrOilMass()+"L");
//              }else{
//                gsmVehicleDataDTO.setrOilMass(gsmVehicleDataDTO.getrOilMass()+"%");
//              }
//      }else{
//        gsmVehicleDataDTO.setrOilMass(gsmVehicleDataDTO.getrOilMass()+"L");
//      }
      gsmVehicleDataDTO.setUpLoadTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, gsmVehicleDataDTO.getUploadTime()));
      String sPowerStr = "";
      String sFlowStr = "";
      String sCoolingStr = "";
      String sBlowoffStr = "";
      String door = "";
      if (gsmVehicleDataDTO != null) {
        //车门判断
//        if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getDoor())) {
//          if (gsmVehicleDataDTO.getDoor().indexOf("1") != -1) {
//            i++;
//            door = "0";
//            modelMap.put("door", door);
//          }
//        }
        if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getSpwr())) {   //电源系统
          if (Double.valueOf(gsmVehicleDataDTO.getSpwr()) < 13.2 || Double.valueOf(gsmVehicleDataDTO.getSpwr()) > 14.8) {
            i++;
            sPowerStr = "0";
            modelMap.put("sPowerStr", sPowerStr);
          }
        }
        if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getThrottlePosition())) {    //进气系统
          if (Double.valueOf(gsmVehicleDataDTO.getThrottlePosition()) < 0 || Double.valueOf(gsmVehicleDataDTO.getThrottlePosition()) > 100) {
            i++;
            sFlowStr = "0";
            modelMap.put("sFlowStr", sFlowStr);
          }
        }
        if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getWbtm())) {   //冷却系统
          if (Double.valueOf(gsmVehicleDataDTO.getWbtm()) < 0 || Double.valueOf(gsmVehicleDataDTO.getWbtm()) > 120.00) {
            i++;
            sCoolingStr = "0";
            modelMap.put("sCoolingStr", sCoolingStr);
          }
        }
        if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getVoltageForOxygenSensor())) {   //排放系统
          if (Double.valueOf(gsmVehicleDataDTO.getVoltageForOxygenSensor()) < 0 || Double.valueOf(gsmVehicleDataDTO.getVoltageForOxygenSensor()) > 1) {
            i++;
            sBlowoffStr = "0";
            modelMap.put("sBlowoffStr", sBlowoffStr);
          }
        }
      }

      modelMap.put("gsmVehicleDataDTO", gsmVehicleDataDTO == null ? new GsmVehicleDataDTO() : gsmVehicleDataDTO);
      modelMap.put("openId", openId);
      modelMap.put("i", i);  //统计故障
      return PAGE_GVDATA;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("eResult", JsonUtil.objectToJson(new PageErrorMsg("error", e.getMessage(), "请刷新后再试")));
      return PAGE_GVDATA;
    }
  }


  /**
   * 故障查询(点击“背景知识”)
   *
   * @param request
   * @param modelMap
   * @param errorCode
   * @return
   */
  @RequestMapping(value = "/backgroundInfo/{errorCode}/{appUserNo}", method = RequestMethod.GET)
  public String toVehicleFaultCodeBackground(HttpServletRequest request, ModelMap modelMap,
                                             @PathVariable("errorCode") String errorCode,
                                             @PathVariable("appUserNo") String appUserNo) {
    LOG.info("wx:toVehicleFaultCodeBackground errorCode is {}", errorCode);
    IAppDictionaryService iAppDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppVehicleDTO appVehicle = null;
    try {
      appVehicle = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
      Set<String> codes = new HashSet<String>();
      codes.add(errorCode);
      Map<String, List<DictionaryFaultInfoDTO>> dictionaryFaultInfoMap = iAppDictionaryService.getDictionaryFaultInfoMapByBrandIdAndCodes(appVehicle.getVehicleBrandId(), codes);
      DictionaryFaultInfoDTO dictionaryFaultInfoDTO = CollectionUtil.getFirst(dictionaryFaultInfoMap.get(errorCode));            //获取故障码详细信息
      modelMap.put("errorCode", errorCode);
      if (dictionaryFaultInfoDTO != null && StringUtil.isNotEmpty(dictionaryFaultInfoDTO.getBackgroundInfo())) {
        modelMap.put("backgroundInfo", dictionaryFaultInfoDTO.getBackgroundInfo()); //背景知识
      } else {
        modelMap.put("backgroundInfo", "该故障码暂无背景知识！"); //背景知识
      }
      return PAGE_BACKGROUNDINFO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 跳转到修改车辆界面
   *
   * @param request
   * @param modelMap
   * @return
   */
  @RequestMapping(value = "/vehicle/{openId}/{appUserNo}", method = RequestMethod.GET)
  public String toVehicle(HttpServletRequest request, ModelMap modelMap,
                          @PathVariable("openId") String openId,
                          @PathVariable("appUserNo") String appUserNo) {
    try {
      IAppUserVehicleObdService iAppUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
      IImpactService iImpactService = ServiceManager.getService(IImpactService.class);
      IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
      AppGsmVehicleResponse apiResponse = iAppUserVehicleObdService.gsmUserGetAppVehicle(appUserNo);
      if (apiResponse.getVehicleInfo() != null) {
        //里程更新，取最新的车况数据中的里程数
        GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(appUserNo);
        if (gsmVehicleDataDTO != null && StringUtil.isNotEmpty(gsmVehicleDataDTO.getCurMil())) {
          apiResponse.getVehicleInfo().setCurrentMileage(Double.valueOf(gsmVehicleDataDTO.getCurMil()));
        }
        //Imei号
        apiResponse.getVehicleInfo().setImei(iImpactService.getObdById(iImpactService.getObdUserVehicle(appUserNo).getObdId()).getImei());
        //当前油价
        IConfigService configService = ServiceManager.getService(IConfigService.class);
        String gasoline_price = configService.getConfig("gasoline_price", ShopConstant.BC_SHOP_ID);
        apiResponse.getVehicleInfo().setGasoline_price(gasoline_price);
        //时间处理
        if (apiResponse.getVehicleInfo().getNextMaintainTime() != null) {
          apiResponse.getVehicleInfo().setNextMaintainTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE_2, Long.valueOf(apiResponse.getVehicleInfo().getNextMaintainTime())));
        } else {
          apiResponse.getVehicleInfo().setNextMaintainTimeStr("");
        }
        if (apiResponse.getVehicleInfo().getNextExamineTime() != null) {
          apiResponse.getVehicleInfo().setNextExamineTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE_2, Long.valueOf(apiResponse.getVehicleInfo().getNextExamineTime())));
        } else {
          apiResponse.getVehicleInfo().setNextExamineTimeStr("");
        }
        modelMap.put("appVehicleDTO", apiResponse.getVehicleInfo()); //车辆信息
        modelMap.put("appUserNo", appUserNo); //appUserNo
        modelMap.put("vehicleId", apiResponse.getVehicleInfo().getVehicleId()); //车辆Id
      }
      modelMap.put("openId", openId);
      return PAGE_VEHICLE;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      modelMap.put("result", new PageErrorMsg(e.getMessage(), "请刷新后再试"));
      return Constant.PAGE_ERROR;
    }
  }

  /**
   * 修改车辆
   *
   * @param request
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/updateAppVehicleDTO", method = RequestMethod.POST)
  public Result updateAppVehicleDTO(HttpServletRequest request, AppVehicleDTO appVehicleDTO) {
    try {
      IAppUserVehicleObdService iAppUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
      AppVehicleDTO appVehicleDTO_ = iAppUserVehicleObdService.getAppVehicleById(appVehicleDTO.getVehicleId());
      appVehicleDTO_.setMaintainPeriod(appVehicleDTO.getMaintainPeriod());
      appVehicleDTO_.setMobile(appVehicleDTO.getMobile());
      appVehicleDTO_.setNextMaintainMileage(appVehicleDTO.getNextMaintainMileage());
      appVehicleDTO_.setNextMaintainTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE_2, appVehicleDTO.getNextMaintainTimeStr()));
      appVehicleDTO_.setNextExamineTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE_2, appVehicleDTO.getNextExamineTimeStr()));
      Result result = iAppUserVehicleObdService.saveOrUpdateVehicle(appVehicleDTO_);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("编辑车辆信息暂不可用！");
    }
  }


  @ResponseBody
  @RequestMapping(value = "/msg/send", method = RequestMethod.POST)
  public ApiResponse send(MQTalkMessageDTO talkMessageDTO) throws Exception {
    try {
      LOG.info("receive msg from wx,data is {}", JsonUtil.objectCHToJson(talkMessageDTO));
      String mq_ip = ServiceManager.getService(IConfigService.class).getConfig("MQ_IP_INTERNET", ShopConstant.BC_SHOP_ID);
      if (StringUtil.isEmpty(mq_ip)) {
        LOG.error("config MQ_IP_INTERNET is empty!");
        return MessageCode.toApiResponse(MessageCode.FAILED);
      }
      String url = (mq_ip + MQConstant.URL_MQ_HTTP_PUSH);
      HttpResponse response = HttpUtils.sendPost(url, talkMessageDTO);
      String appVehicleDTOJson = response.getContent();
      return JsonUtil.jsonToObj(appVehicleDTOJson, ApiResponse.class);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
  }


//  @ResponseBody
//  @RequestMapping(value = "/disconnect/{name}", method = RequestMethod.GET)
//  public void disconnect(@PathVariable("name") String name) {
//    RmiClientPushTools.disconnect(name);
//  }


  @ResponseBody
  @RequestMapping(value = "/isOnLine/{name}", method = RequestMethod.GET)
  public boolean isOnLine(@PathVariable("name") String name) {
    try {
      IMQClientService clientService = ServiceManager.getService(IMQClientService.class);
      return clientService.isOnLine(name);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return false;
    }

  }


}
