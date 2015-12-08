package com.bcgogo.txn.service.app;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigReader;
import com.bcgogo.config.model.VehicleViolateRegulationQueryRecord;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.app.VRegulationRecordQueryType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.wx.IWXAccountService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXAccountType;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import com.bcgogo.wx.user.AppWXUserDTO;
import com.bcgogo.wx.user.WXUserDTO;
import com.bcgogo.wx.user.WXUserVehicleDTO;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * User: lw
 * Date: 14-4-29
 * Time: 下午3:42
 */
@Component
public class SendVRegulationMsgToAppService implements ISendVRegulationMsgToAppService {
  private static final Logger LOG = LoggerFactory.getLogger(SendVRegulationMsgToAppService.class);

  private static final String JUHE_RESULT_CODE = "200";

  @Override
  public void sendVRegulationMsgToYiFaWXUser() throws Exception {
    IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
    int count = wxUserService.countWXUser(WXAccountType.YIFA);
    if (count <= 0) {
      return;
    }
    Pager pager = new Pager(count, 1, 100);
    while (true) {
      List<WXUserDTO> userDTOs = wxUserService.getWXUserDTOByPager(WXAccountType.YIFA, pager);
      if (CollectionUtil.isEmpty(userDTOs)) {
        break;
      }
      List<String> openIds = new ArrayList<String>();
      for (WXUserDTO userDTO : userDTOs) {
        openIds.add(userDTO.getOpenid());
      }
      List<WXUserVehicleDTO> userVehicleDTOs = wxUserService.getWXUserVehicleByOpenId(openIds.toArray(new String[openIds.size()]));
      doSendVRegulationMsgToYiFaWXUser(userVehicleDTOs);
      if (!pager.hasNextPage()) {
        break;
      }
      pager.gotoNextPage();
    }
  }

  @Override
  public void sendVRegulationMsgToApp() throws Exception {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    int count = appUserService.countGsmAppVehicle();
    if (count <= 0) {
      return;
    }
    Pager pager = new Pager(count, 1, 100);
    while (true) {
      List<AppVehicleDTO> appVehicleDTOList = appUserService.getGsmAppVehicle(pager);
      if (CollectionUtil.isEmpty(appVehicleDTOList)) {
        break;
      }
      //发送给app用户
//      this.createPushMessageByVehicle(appVehicleDTOList);
      //发送给后视镜微信用户
      this.sendMirrorWXUser(appVehicleDTOList);
      if (pager.hasNextPage()) {
        pager.gotoNextPage();
      } else {
        break;
      }
    }
  }

  private void doSendVRegulationMsgToYiFaWXUser(List<WXUserVehicleDTO> userVehicleDTOs) throws Exception {
    if (CollectionUtil.isEmpty(userVehicleDTOs)) {
      return;
    }
    IJuheService juheService = ServiceManager.getService(IJuheService.class);
    Map<String, JuheViolateRegulationCitySearchConditionDTO> juheSearchMap = juheService.getJuheSearchCondition();
    if (MapUtils.isEmpty(juheSearchMap)) {
      return;
    }
    ConfigReader configReader = ServiceManager.getService(ConfigDaoManager.class).getReader();
    List<VehicleViolateRegulationRecordDTO> regulationRecordDTOs = new ArrayList<VehicleViolateRegulationRecordDTO>();
    for (WXUserVehicleDTO userVehicleDTO : userVehicleDTOs) {
      String juheCityCode = AreaCacheManager.getJuheCodeByCityCode(userVehicleDTO.getCity());
      if (StringUtil.isEmpty(juheCityCode) || StringUtil.isEmpty(userVehicleDTO.getVin())) {
        continue;
      }
      JuheViolateRegulationCitySearchConditionDTO condition = juheSearchMap.get(juheCityCode);
      if (condition == null) {
        continue;
      }
      if (!validateQueryBySearchCondition(userVehicleDTO, condition)) {
        continue;
      }
      String engineno = userVehicleDTO.getEngineNo();
      String classno = userVehicleDTO.getVin();
//        String registno = userVehicleDTO.getRegistNo();
      String registno = null;
      if (condition.getEngine() != 0 && condition.getEngineNo() != 0 && engineno != null && condition.getEngineNo() < engineno.length()) {
        engineno = engineno.substring(engineno.length() - condition.getEngineNo(), engineno.length());
      }
      if (condition.getClassa() != 0 && condition.getClassNo() != 0 && classno != null && condition.getClassNo() < classno.length()) {
        classno = classno.substring(classno.length() - condition.getClassNo(), classno.length());
      }
      if (condition.getRegist() != 0 && condition.getRegistNo() != 0 && registno != null && condition.getRegistNo() < registno.length()) {
        registno = registno.substring(registno.length() - condition.getRegistNo(), registno.length());
      }
      List list = juheService.queryVRegulationFromJuhe(juheCityCode, userVehicleDTO.getVehicleNo(), "02", engineno, classno, registno, ConfigUtils.getJuheViolateRegulationKey(), VRegulationRecordQueryType.SCHEDULE);
      boolean hasNewVRegulationRecord = (Boolean) list.get(1);
      // hasNewVRegulationRecord 如果之前查过的记录,返回false ,true代表有新违章记录出现
      if (!hasNewVRegulationRecord) {
        continue;
      }
      //2.再从db里查
      Long queryDate = DateUtil.getLastWeekStartTime();//推送一周之内的
      List<VehicleViolateRegulationQueryRecord> queryRecordList = configReader.getVehicleViolateRegulationQueryRecord(juheCityCode, userVehicleDTO.getVehicleNo(), queryDate, JUHE_RESULT_CODE);
      if (CollectionUtil.isEmpty(queryRecordList)) {
        continue;
      }
      List<VehicleViolateRegulationRecordDTO> recordDTOs = juheService.getVehicleViolateRegulationRecord(juheCityCode, userVehicleDTO.getVehicleNo(), null);
      if (CollectionUtil.isNotEmpty(recordDTOs)) regulationRecordDTOs.addAll(recordDTOs);
    }
    //发送违章模版消息
    ServiceManager.getService(IWXUserService.class).sendVRegulationTemplateMsg(regulationRecordDTOs);

  }


  public void createPushMessageByVehicle(List<AppVehicleDTO> appVehicleDTOList) {

    try {
      if (CollectionUtil.isEmpty(appVehicleDTOList)) {
        return;
      }
      IJuheService juheService = ServiceManager.getService(IJuheService.class);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      Map<String, JuheViolateRegulationCitySearchConditionDTO> juheSearchMap = juheService.getJuheSearchCondition();
      if (MapUtils.isEmpty(juheSearchMap)) {
        return;
      }

      Set<String> appUserNoSet = new HashSet<String>();

      for (AppVehicleDTO appVehicleDTO : appVehicleDTOList) {
        if (StringUtil.isEmpty(appVehicleDTO.getJuheCityCode())) {
          continue;
        }

        JuheViolateRegulationCitySearchConditionDTO condition = juheSearchMap.get(appVehicleDTO.getJuheCityCode());
        if (condition == null) {
          continue;
        }

        if (!validateQueryBySearchCondition(appVehicleDTO, condition)) {
          continue;
        }

        String engineno = appVehicleDTO.getEngineNo();
        String classno = appVehicleDTO.getVehicleVin();
        String registno = appVehicleDTO.getRegistNo();

        if (condition.getEngine() != 0 && condition.getEngineNo() != 0 && engineno != null && condition.getEngineNo() < engineno.length()) {
          engineno = engineno.substring(engineno.length() - condition.getEngineNo(), engineno.length());
        }
        if (condition.getClassa() != 0 && condition.getClassNo() != 0 && classno != null && condition.getClassNo() < classno.length()) {
          classno = classno.substring(classno.length() - condition.getClassNo(), classno.length());
        }
        if (condition.getRegist() != 0 && condition.getRegistNo() != 0 && registno != null && condition.getRegistNo() < registno.length()) {
          registno = registno.substring(registno.length() - condition.getRegistNo(), registno.length());
        }


        List list = juheService.queryVRegulationFromJuhe(appVehicleDTO.getJuheCityCode(), appVehicleDTO.getVehicleNo(), "02", engineno, classno, registno, ConfigUtils.getJuheViolateRegulationKey(), VRegulationRecordQueryType.SCHEDULE);
        boolean hasNewVRegulationRecord = (Boolean) list.get(1);
        if (!hasNewVRegulationRecord) {
          continue;
        }
        appUserNoSet.add(appVehicleDTO.getUserNo());
      }

      Map<String, AppUserDTO> appUserDTOMap = appUserService.getAppUserMapByUserNo(appUserNoSet);

      if (MapUtils.isEmpty(appUserDTOMap)) {
        return;
      }

      for (AppVehicleDTO appVehicleDTO : appVehicleDTOList) {

        AppUserDTO appUserDTO = appUserDTOMap.get(appVehicleDTO.getUserNo());
        if (appUserDTO == null) {
          continue;
        }

        IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
        appointPushMessageService.sendVRegulationRecordMessage2App(appVehicleDTO, appUserDTO);

      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void sendMirrorWXUser(List<AppVehicleDTO> appVehicleDTOList) {
    try {
      if (CollectionUtil.isEmpty(appVehicleDTOList)) {
        return;
      }
      IJuheService juheService = ServiceManager.getService(IJuheService.class);
      Map<String, JuheViolateRegulationCitySearchConditionDTO> juheSearchMap = juheService.getJuheSearchCondition();
      if (MapUtils.isEmpty(juheSearchMap)) {
        return;
      }
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      IWXAccountService accountService = ServiceManager.getService(IWXAccountService.class);
      ConfigReader configReader = ServiceManager.getService(ConfigDaoManager.class).getReader();
      for (AppVehicleDTO appVehicleDTO : appVehicleDTOList) {
        String juheCityCode = appVehicleDTO.getJuheCityCode();
        if (StringUtil.isEmpty(juheCityCode) || StringUtil.isEmpty(appVehicleDTO.getVehicleVin())) {
          continue;
        }
        JuheViolateRegulationCitySearchConditionDTO condition = juheSearchMap.get(juheCityCode);
        if (condition == null) {
          continue;
        }
        if (!validateQueryBySearchCondition(appVehicleDTO, condition)) {
          continue;
        }
        String engineno = appVehicleDTO.getEngineNo();
        String classno = appVehicleDTO.getVehicleVin();
        //        String registno = userVehicleDTO.getRegistNo();
        String registno = null;
        if (condition.getEngine() != 0 && condition.getEngineNo() != 0 && engineno != null && condition.getEngineNo() < engineno.length()) {
          engineno = engineno.substring(engineno.length() - condition.getEngineNo(), engineno.length());
        }
        if (condition.getClassa() != 0 && condition.getClassNo() != 0 && classno != null && condition.getClassNo() < classno.length()) {
          classno = classno.substring(classno.length() - condition.getClassNo(), classno.length());
        }
        if (condition.getRegist() != 0 && condition.getRegistNo() != 0 && registno != null && condition.getRegistNo() < registno.length()) {
          registno = registno.substring(registno.length() - condition.getRegistNo(), registno.length());
        }
        List list = juheService.queryVRegulationFromJuhe(juheCityCode, appVehicleDTO.getVehicleNo(), "02", engineno, classno, registno, ConfigUtils.getJuheViolateRegulationKey(), VRegulationRecordQueryType.SCHEDULE);
        boolean hasNewVRegulationRecord = (Boolean) list.get(1);
        //1.hasNewVRegulationRecord 如果之前查过的记录,返回false ,true代表有新违章记录出现
        if (!hasNewVRegulationRecord) {
          continue;
        }
        //2.再从db里查
        Long queryDate = DateUtil.getLastWeekStartTime();//推送一周之内的
        List<VehicleViolateRegulationQueryRecord> queryRecordList = configReader.getVehicleViolateRegulationQueryRecord(juheCityCode, appVehicleDTO.getVehicleNo(), queryDate, JUHE_RESULT_CODE);
        if (CollectionUtil.isEmpty(queryRecordList)) {
          continue;
        }
        List<VehicleViolateRegulationRecordDTO> recordDTOs = juheService.getVehicleViolateRegulationRecord(juheCityCode, appVehicleDTO.getVehicleNo(), null);
        if (CollectionUtil.isEmpty(recordDTOs)) {
          continue;
        }
        //发送违章模版消息
        List<AppWXUserDTO> appWXUserDTOs = wxUserService.getAppWXUserDTOByAppUserNo(appVehicleDTO.getAppUserNo());
        if (CollectionUtil.isEmpty(appWXUserDTOs)) {
          continue;
        }
        for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
          String publicNo = accountService.getWXAccountByOpenId(appWXUserDTO.getOpenId()).getPublicNo();
          for (VehicleViolateRegulationRecordDTO recordDTO : recordDTOs) {
            WXMsgTemplate template = WXHelper.getMirrorVRegulationTemplate(publicNo, appWXUserDTO.getOpenId(), recordDTO);
            if (template != null) {
              LOG.info("mock send wx msg,openId is {},recordDTO vehicle is {}",appWXUserDTO.getOpenId(),recordDTO.getVehicleNo());
//                Result result = ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(publicNo,template);
//                if(!result.isSuccess()){
//                    LOG.error("后视镜发送违章提醒异常，{}",result.getMsg());
//                }
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  public boolean validateQueryBySearchCondition(AppVehicleDTO appVehicleDTO, JuheViolateRegulationCitySearchConditionDTO searchConditionDTO) {

    if (searchConditionDTO.getEngine() != 0 && StringUtil.isEmpty(appVehicleDTO.getEngineNo())) {
      return false;
    }

    if (searchConditionDTO.getClassa() != 0 && StringUtil.isEmpty(appVehicleDTO.getVehicleVin())) {
      return false;
    }
    if (searchConditionDTO.getRegist() != 0 && StringUtil.isEmpty(appVehicleDTO.getRegistNo())) {
      return false;
    }

    if (searchConditionDTO.getEngine() != 0 && StringUtil.isNotEmpty(appVehicleDTO.getEngineNo())
      && searchConditionDTO.getEngineNo() > appVehicleDTO.getEngineNo().length()) {
      return false;
    }

    if (searchConditionDTO.getClassa() != 0 && StringUtil.isNotEmpty(appVehicleDTO.getVehicleVin())
      && searchConditionDTO.getClassNo() > appVehicleDTO.getVehicleVin().length()) {
      return false;
    }

    if (searchConditionDTO.getRegist() != 0 && StringUtil.isNotEmpty(appVehicleDTO.getRegistNo())
      && searchConditionDTO.getRegistNo() > appVehicleDTO.getRegistNo().length()) {
      return false;
    }
    return true;

  }

  public boolean validateQueryBySearchCondition(WXUserVehicleDTO userVehicleDTO, JuheViolateRegulationCitySearchConditionDTO searchConditionDTO) {
    if (searchConditionDTO.getEngine() != 0 && StringUtil.isEmpty(userVehicleDTO.getEngineNo())) {
      return false;
    }

    if (searchConditionDTO.getClassa() != 0 && StringUtil.isEmpty(userVehicleDTO.getVin())) {
      return false;
    }
//    if (searchConditionDTO.getRegist() != 0 && StringUtil.isEmpty(userVehicleDTO.getRegistNo())) {
//      return false;
//    }
    if (searchConditionDTO.getEngine() != 0 && StringUtil.isNotEmpty(userVehicleDTO.getEngineNo())
      && searchConditionDTO.getEngineNo() > userVehicleDTO.getEngineNo().length()) {
      return false;
    }
    if (searchConditionDTO.getClassa() != 0 && StringUtil.isNotEmpty(userVehicleDTO.getVin())
      && searchConditionDTO.getClassNo() > userVehicleDTO.getVin().length()) {
      return false;
    }
//    if (searchConditionDTO.getRegist() != 0 && StringUtil.isNotEmpty(appVehicleDTO.getRegistNo())
//      && searchConditionDTO.getRegistNo() > appVehicleDTO.getRegistNo().length()) {
//      return false;
//    }
    return true;

  }

}
