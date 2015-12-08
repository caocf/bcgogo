package com.bcgogo.customer;

import com.bcgogo.api.*;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.common.*;
import com.bcgogo.enums.app.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.obd.ObdManagerService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-6-25
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/OBDManager.do")
public class OBDManagerController {
  private static final Logger LOG = LoggerFactory.getLogger(OBDManagerController.class);

  @Autowired
  IObdManagerService obdManagerService;

  @RequestMapping(params = "method=toOBDManager")
  public String OBDManager(HttpServletRequest request, ModelMap modelMap) {
    modelMap.addAttribute("obdSimSearchCondition", new ObdSimSearchCondition());
    return "/customer/obd/obdManager";
  }

  @RequestMapping(params = "method=getOBDList")
  @ResponseBody
  public Object getOBDList(HttpServletRequest request, ObdSimSearchCondition obdSimSearchCondition) {
    try {
      if (obdSimSearchCondition.getStartPageNo() == 0) {
        obdSimSearchCondition.setStartPageNo(1);
      }
      obdSimSearchCondition.setSellShopId(WebUtil.getShopId(request));
      PagingListResult<ObdSimBindDTO> result = new PagingListResult<ObdSimBindDTO>();
      Pager pager = new Pager(obdManagerService.countObdSimBindByShop(obdSimSearchCondition), obdSimSearchCondition.getStartPageNo(), obdSimSearchCondition.getLimit());
      obdSimSearchCondition.setPager(pager);
      result.setResults(obdManagerService.getObdSimBindDTOByShop(obdSimSearchCondition));
      Map data = new HashMap();
      String[] obdStatusList = new String[1];
      obdStatusList[0] = OBDStatus.ON_SELL.toString();
      obdSimSearchCondition.setObdStatusList(obdStatusList);
      data.put("shop_obd_on_sell", obdManagerService.countObdSimBindByShop(obdSimSearchCondition));
      obdStatusList[0] = OBDStatus.SOLD.toString();
      obdSimSearchCondition.setObdStatusList(obdStatusList);
      data.put("shop_obd_sold", obdManagerService.countObdSimBindByShop(obdSimSearchCondition));
      obdSimSearchCondition.setObdStatusList(null);
      data.put("shop_obd_total", obdManagerService.countObdSimBindByShop(obdSimSearchCondition));
      result.setData(data);
      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getOBDByImeiAndSimNo")
  @ResponseBody
  public Object getOBDByImeiAndSimNo(HttpServletRequest request, ObdSimSearchCondition obdSimSearchCondition) {
    try {
      ObdSimBindDTO obdSimBindDTO = CollectionUtil.getFirst(obdManagerService.getObdSimBindDTOByShop(obdSimSearchCondition));
      return obdSimBindDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getShopOBDSuggestion")
  @ResponseBody
  public Object getShopOBDSuggestion(HttpServletRequest request, ObdSimSearchCondition condition) {
    try {
      condition.setSellShopId(WebUtil.getShopId(request));
      String[] obdStatusList = {OBDStatus.ON_SELL.toString()};
      condition.setObdStatusList(obdStatusList);
      condition.setStart(0);
      condition.setLimit(10);
      return obdManagerService.getObdSimBindDTOByShop(condition);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=unInstallGsmOBD")
  @ResponseBody
  public Object unInstallGsmOBD(HttpServletRequest request, String imei, String mobile) {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      String shopName = WebUtil.getShopName(request);
      Result result = new Result();
      ObdSimBindDTO obdSimBindDTO = obdManagerService.getObdSimBindByShopExact(imei, mobile);
      if (obdSimBindDTO == null || obdSimBindDTO.getVehicleId() == null) {
        return result.LogErrorMsg("数据异常！");
      }
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOById(obdSimBindDTO.getVehicleId());
      if (vehicleDTO == null) {
        return result.LogErrorMsg("车辆信息异常!");
      }
      vehicleDTO.setObdId(null);
      vehicleDTO.setGsmObdImei(null);
      vehicleDTO.setGsmObdImeiMoblie(null);
      vehicleService.updateVehicle(vehicleDTO);
      obdSimBindDTO.setOperateUserId(WebUtil.getUserId(request));
      obdSimBindDTO.setOperateUserName(WebUtil.getUserName(request));
      obdSimBindDTO.setOperateShopId(WebUtil.getShopId(request));
      //记录卸载时的信息
      obdManagerService.createOBDSimOperationLog(obdSimBindDTO, OBDSimOperationType.UN_INSTALL);
      SolrHelper.doVehicleReindex(WebUtil.getShopId(request), vehicleDTO.getId());
      ObdDTO obdDTO = obdManagerService.getObdDTOById(obdSimBindDTO.getObdId());
      if (obdDTO != null) {
        obdDTO.setObdStatus(OBDStatus.ON_SELL);
        obdDTO.setOwnerId(shopId);
        obdDTO.setOwnerName(shopName);
        obdDTO.setOwnerType(ObdSimOwnerType.SHOP);
        obdDTO.setSellTime(null);
        obdManagerService.updateOBD(obdDTO);
      }
      //判断是否存在appuser
      if (obdSimBindDTO.getAppVehicleId() != null) {
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        AppUserShopVehicleDTO appUserShopVehicleDTO = appUserService.getAppUserShopVehicleDTO(obdSimBindDTO.getAppVehicleId(), obdSimBindDTO.getObdId(), AppUserShopVehicleStatus.BUNDLING);
        if (appUserShopVehicleDTO != null) {
          appUserShopVehicleDTO.setStatus(ObdUserVehicleStatus.DELETED);
          appUserService.saveOrUpdateAppUserShopVehicle(appUserShopVehicleDTO);
        }
        ObdUserVehicleDTO obdUserVehicleDTO = appUserService.getObdUserVehicle(obdSimBindDTO.getAppVehicleId(), obdSimBindDTO.getObdId(), ObdUserVehicleStatus.BUNDLING);
        if (obdUserVehicleDTO != null) {
          obdUserVehicleDTO.setStatus(ObdUserVehicleStatus.DELETED);
          appUserService.saveOrUpdateObdUserVehicle(obdUserVehicleDTO);
        }
      }
      return result;
    } catch (BcgogoException e) {
      LOG.error(e.getMessage(), e);
      return new Result("您输入的OBD信息异常，请联系客服!", false);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 客户管理--绑定OBD/更换设备
   * @param request
   * @param bindDTO
   * @return
   */
  @RequestMapping(params = "method=gsmOBDBind")
  @ResponseBody
  public Object gsmOBDBind(HttpServletRequest request,OBDBindDTO bindDTO) {
    try {
      Long shopId = WebUtil.getShopId(request);
      String imei=bindDTO.getImei();
      ObdDTO obdDTO = obdManagerService.getObdByImei(imei);
      if (obdDTO == null) return new Result(false, "OBD不存在");
      bindDTO.setShopId(shopId);
      bindDTO.setShopName(WebUtil.getShopName(request));
      bindDTO.setUserName(WebUtil.getUserName(request));
      Result result = obdManagerService.gsmOBDBind(bindDTO);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserDTO appUserDTO = appUserService.getAppUserDTOByMobileUserType(bindDTO.getMobile(),AppUserType.MIRROR);
      if(appUserDTO==null){
        //后视镜自动分配帐号
        if (ObdType.MIRROR.equals(obdDTO.getObdType())) {
          GSMRegisterDTO gsmRegisterDTO = new GSMRegisterDTO();
          gsmRegisterDTO.setImei(imei);
          ServiceManager.getService(IAppUserService.class).gsmAllocateAppUser(gsmRegisterDTO);
        }
      }
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * OBD管理--车辆安装
   *
   * @param request
   * @param obdId
   * @param licenceNo
   * @return
   */
  @RequestMapping(params = "method=OBDInstall")
  @ResponseBody
  public Object gsmOBDInstall(HttpServletRequest request, Long obdId, String licenceNo) {
    try {
      Result result = new Result();
      if (obdId == null || StringUtil.isEmpty(licenceNo)) {
        return result.LogErrorMsg("数据异常!");
      }
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByLicenceNo(WebUtil.getShopId(request), licenceNo);
      if (vehicleDTO == null) {
        return result.LogErrorMsg("输入的安装车牌号不存在!");
      }
      ObdSimSearchCondition condition = new ObdSimSearchCondition();
      condition.setSellShopId(WebUtil.getShopId(request));
      condition.setObdId(obdId);
      ObdSimBindDTO obdSimBindDTO = CollectionUtil.getFirst(ServiceManager.getService(ObdManagerService.class).getObdSimBindDTOByShop(condition));
      if (obdSimBindDTO == null) {
        return result.LogErrorMsg("输入的安装车牌号不存在!");
      }
      ObdDTO obdDTO = obdManagerService.getObdDTOById(obdSimBindDTO.getObdId());
      vehicleDTO.setObdId(obdSimBindDTO.getObdId());
      vehicleDTO.setGsmObdImei(obdSimBindDTO.getImei());
      vehicleDTO.setGsmObdImeiMoblie(obdSimBindDTO.getMobile());
      vehicleService.updateVehicle(vehicleDTO);
      SolrHelper.doVehicleReindex(WebUtil.getShopId(request), vehicleDTO.getId());

      if (obdDTO != null) {
        obdDTO.setObdStatus(OBDStatus.SOLD);
        obdDTO.setOwnerId(vehicleDTO.getId());
        obdDTO.setOwnerName(vehicleDTO.getLicenceNo());
        obdDTO.setOwnerType(ObdSimOwnerType.SHOP_VEHICLE);
        obdDTO.setSellTime(System.currentTimeMillis());
        obdManagerService.updateOBD(obdDTO);
      }
      //log
      obdSimBindDTO.setOperateUserId(WebUtil.getUserId(request));
      obdSimBindDTO.setOperateShopId(WebUtil.getShopId(request));
      obdSimBindDTO.setOperateUserName(WebUtil.getShopName(request));
      obdManagerService.createOBDSimOperationLog(obdSimBindDTO, OBDSimOperationType.INSTALL);
      //判断是否和appuser关联过
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      if (obdSimBindDTO.getAppVehicleId() != null) {
        AppUserShopVehicleDTO appUserShopVehicleDTO = appUserService.getAppUserShopVehicleDTO(obdSimBindDTO.getAppVehicleId(), obdSimBindDTO.getObdId(), AppUserShopVehicleStatus.DELETED);
        if (appUserShopVehicleDTO != null) {
          appUserShopVehicleDTO.setStatus(ObdUserVehicleStatus.BUNDLING);
          appUserService.saveOrUpdateAppUserShopVehicle(appUserShopVehicleDTO);
        }
        LOG.info("obd install,appvehicleId={}", obdSimBindDTO.getAppVehicleId());
        ObdUserVehicleDTO obdUserVehicleDTO = appUserService.getObdUserVehicle(obdSimBindDTO.getAppVehicleId(), obdSimBindDTO.getObdId(), ObdUserVehicleStatus.DELETED);
        if (obdUserVehicleDTO != null) {
          obdUserVehicleDTO.setStatus(ObdUserVehicleStatus.BUNDLING);
          appUserService.saveOrUpdateObdUserVehicle(obdUserVehicleDTO);
        }
      }
      AppUserDTO appUserDTO = appUserService.getAppUserDTOByMobileUserType(obdSimBindDTO.getMobile(),AppUserType.MIRROR);
      if(appUserDTO==null){
      //后视镜自动分配帐号
      if (ObdType.MIRROR.equals(obdDTO.getObdType())) {
        GSMRegisterDTO gsmRegisterDTO = new GSMRegisterDTO();
        gsmRegisterDTO.setImei(obdDTO.getImei());
        ServiceManager.getService(IAppUserService.class).gsmAllocateAppUser(gsmRegisterDTO);
      }
      }
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(params = "method=OBDStorage")
  @ResponseBody
  public Object obdStorage(HttpServletRequest request, StorageObdSimBindList obdSimBindList) {
    try {
      ObdSimBindDTO[] obdSimBindDTOs = obdSimBindList.getObdSimBindDTOs();
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      String shopName = WebUtil.getShopName(request);
      String userName = WebUtil.getUserName(request);
      Result result = validateStorageOBD(shopId, obdSimBindDTOs);
      if (!result.isSuccess()) {
        return result;
      }
      if (!ArrayUtils.isEmpty(obdSimBindDTOs)) {
        for (ObdSimBindDTO obdSimBindDTO : obdSimBindDTOs) {
          if (obdSimBindDTO != null) {
            obdSimBindDTO.setOperateShopId(shopId);
            obdSimBindDTO.setOperateUserName(userName);
            obdSimBindDTO.setOperateUserId(userId);
          }
        }
      }
      return obdManagerService.doStorageOBD(shopId, shopName, obdSimBindDTOs);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  private Result validateStorageOBD(Long shopId, ObdSimBindDTO[] obdSimBindDTOs) throws ParseException {
    List<ObdSimBindDTO> obdSimBindDTOList = new ArrayList<ObdSimBindDTO>();
    Result result = new Result();
    if (ArrayUtil.isEmpty(obdSimBindDTOs)) {
      return result.LogErrorMsg("obd或后视镜列表不存在！");
    }
    for (ObdSimBindDTO bindDTO : obdSimBindDTOs) {
      if (StringUtil.isEmpty(bindDTO.getImei()) || StringUtil.isEmpty(bindDTO.getMobile())) {
        continue;
      }
      if (StringUtil.isEmpty(bindDTO.getImei())) {
        return result.LogErrorMsg("IMEI号不应为空！");
      }
      if (StringUtil.isEmpty(bindDTO.getMobile())) {
        return result.LogErrorMsg("SIM卡号不应为空！");
      }
      obdSimBindDTOList.add(bindDTO);
    }
    if (CollectionUtil.isEmpty(obdSimBindDTOList)) {
      return result.LogErrorMsg("obd或后视镜列表不存在！");
    }
    ObdSimSearchCondition condition = new ObdSimSearchCondition();
    ObdSimBindDTO obdSimBindDTO = null;
    for (ObdSimBindDTO bindDTO : obdSimBindDTOList) {
      //校验店铺是否已经入库该obd
      condition.setSellShopId(shopId);
      condition.setImei(bindDTO.getImei());
      condition.setMobile(bindDTO.getMobile());
      obdSimBindDTO = CollectionUtil.getFirst(obdManagerService.getObdSimBindDTOByShop(condition));
      if (obdSimBindDTO != null) {
        return result.LogErrorMsg("IMEI为" + bindDTO.getImei() + "的OBD或后视镜已入库，请修改!");
      }
      //校验要入库的obd是否存在
      condition.setSellShopId(null);
      obdSimBindDTO = CollectionUtil.getFirst(obdManagerService.getObdSimBindDTOByShop(condition));
      if (obdSimBindDTO == null) {
        return result.LogErrorMsg("obd或后视镜:" + bindDTO.getImei() + "不存在，请联系客服，电话：0512-66733331");
      }
    }
    return result;
  }

}
