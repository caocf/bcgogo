package com.bcgogo.config.service.camera;

import com.bcgogo.camera.CameraConfigDTO;
import com.bcgogo.camera.CameraDTO;
import com.bcgogo.camera.CameraRecordDTO;
import com.bcgogo.camera.CameraSearchCondition;
import com.bcgogo.common.Pager;
import com.bcgogo.config.model.*;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: zhangjie
 * Date: 15-01-06
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CameraService implements ICameraService {
  private static final Logger LOG = LoggerFactory.getLogger(CameraService.class);

  @Autowired
  private ConfigDaoManager configDaoManager;

  //添加或修改camera
  public void saveOrUpdateCameraRef(CameraDTO cameraDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (StringUtil.isEmpty(cameraDTO.getId())) {
        //新增摄像头基本参数
        Camera camera = saveOrUpdateCamera(cameraDTO);
        if (StringUtil.isNotEmpty(cameraDTO.getCamera_shop_id())) {
          //新增（绑定）摄像头中间表参数
          saveOrUpdateCameraShop(cameraDTO, camera);
        }
      } else {
        //更新摄像头基本参数
        Camera camera = updateCamera(cameraDTO);
        //查询中间表是否有该摄像头记录
        CameraShop cameraShop = writer.getCameraShop(cameraDTO);
        if (cameraShop != null) {
          //更新中间表参数
          updateCameraShop(cameraDTO, cameraShop);
        } else {
          if (StringUtil.isNotEmpty(cameraDTO.getCamera_shop_id())) {
            //新增（绑定）摄像头中间表参数
            saveOrUpdateCameraShop(cameraDTO, camera);
          }
        }
      }
      writer.commit(status);
    } catch (ParseException e) {
      e.printStackTrace();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CameraShop getCameraShop(String id) {
    ConfigWriter writer = configDaoManager.getWriter();
    CameraDTO cameraDTO = new CameraDTO();
    cameraDTO.setId(id);
    CameraShop cameraShop = writer.getCameraShop(cameraDTO);
    return cameraShop;
  }


  //新增（绑定）cameraShop
  public void saveOrUpdateCameraShop(CameraDTO cameraDTO, Camera cameraf) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    CameraShop cameraShop = new CameraShop();
    try {
      //根据摄像头序列号查询camera
      //待续
//      Camera camera = writer.getCamera(cameraf);
      cameraf.setStatus("binding");
      writer.update(cameraf);
      //新增（绑定）中间表信息
      cameraShop.setCamera_id(NumberUtil.longValue(cameraf.getId()));
      cameraShop.setShop_id(NumberUtil.longValue(cameraDTO.getCamera_shop_id()));
      if (StringUtil.isEmpty(cameraDTO.getInstall_date())) {
        cameraShop.setInstall_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, formatter.format(System.currentTimeMillis())));
      } else {
        cameraShop.setInstall_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraDTO.getInstall_date() + " 00:00:00"));
      }

//      cameraShop.setWhite_vehicle_nos(cameraDTO.getWhite_vehicle_nos());
//      if("已绑定".equals(cameraDTO.getStatus())){
      cameraShop.setStatus("binding");
//      } else{
//        cameraShop.setStatus("nobinding");
//      }
//      writer.update(camera);
      writer.saveOrUpdate(cameraShop);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  //新增camera
  public Camera saveOrUpdateCamera(CameraDTO cameraDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Camera camera = new Camera();
    try {
      camera = camera.fromCameraDTO(cameraDTO);
      writer.saveOrUpdate(camera);
      writer.commit(status);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return camera;
  }

  //新增camera
  public Camera saveOrUpdateCameraVLP(CameraDTO cameraDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Camera camera = new Camera();
    try {
      camera = camera.fromCameraDTOVLPR(cameraDTO);
      writer.saveOrUpdate(camera);
      writer.commit(status);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return camera;
  }

  //更新camera
  public Camera updateCamera(CameraDTO cameraDTO) throws ParseException {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Camera camera = writer.getById(Camera.class, NumberUtil.longValue(cameraDTO.getId()));
    camera.setSerial_no(cameraDTO.getSerial_no().equals("") ? null : cameraDTO.getSerial_no());
    camera.setLast_heart_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraDTO.getLast_heart_date()));
    camera.setLan_ip(cameraDTO.getLan_ip().equals("") ? null : cameraDTO.getLan_ip());
    camera.setLan_port(cameraDTO.getLan_port().equals("") ? null : cameraDTO.getLan_port());
    camera.setUsername(cameraDTO.getUsername().equals("") ? null : cameraDTO.getUsername());
    camera.setPassword(cameraDTO.getPassword().equals("") ? null : cameraDTO.getPassword());
    camera.setDomain_username(cameraDTO.getDomain_username().equals("") ? null : cameraDTO.getDomain_username());
    camera.setDomain_password(cameraDTO.getDomain_password().equals("") ? null : cameraDTO.getDomain_password());
    if ("已绑定".equals(cameraDTO.getStatus()) || cameraDTO.getStatus() == null) {
      camera.setStatus("binding");
    } else {
      camera.setStatus("nobinding");
    }
    camera.setRemark(cameraDTO.getRemark().equals("") ? null : cameraDTO.getRemark());
    camera.setExternal_address(cameraDTO.getExternal_address().equals("") ? null : cameraDTO.getExternal_address());
    writer.update(camera);
    writer.commit(status);
    return camera;
  }

  //更新camera
  public Camera updateCameraVLPR(CameraDTO cameraDTO) throws ParseException {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Camera camera = writer.getById(Camera.class, NumberUtil.longValue(cameraDTO.getId()));
    camera.setSerial_no(cameraDTO.getSerial_no());
    camera.setLast_heart_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraDTO.getLast_heart_date()));
    camera.setLan_ip(cameraDTO.getLan_ip());
    camera.setLan_port(cameraDTO.getLan_port());
    camera.setUsername(cameraDTO.getUsername());
    camera.setPassword(cameraDTO.getPassword());
    writer.update(camera);
    writer.commit(status);
    return camera;
  }

  //更新aameraShop
  public void updateCameraShop(CameraDTO cameraDTO, CameraShop cameraShop) throws ParseException {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
//    CameraShop cameraShop = writer.getById(CameraShop.class, NumberUtil.longValue(cameraDTO.getCamera_shop_id()));
    cameraShop.setStatus("binding");
    cameraShop.setShop_id(NumberUtil.longValue(cameraDTO.getCamera_shop_id()));
//    cameraShop.setWhite_vehicle_nos(cameraDTO.getWhite_vehicle_nos());
    if (StringUtil.isEmpty(cameraDTO.getInstall_date())) {
      cameraShop.setInstall_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, formatter.format(System.currentTimeMillis())));
    } else {
      cameraShop.setInstall_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraDTO.getInstall_date() + " 00:00:00"));
    }
    writer.update(cameraShop);
    writer.commit(status);
  }


  @Override
  public List<CameraDTO> getCameraDTOList(Pager pager, String shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<CameraDTO> cameraDTOs = writer.getCameraDTOList(pager, shopId);
    if (CollectionUtil.isEmpty(cameraDTOs)) return null;
    return cameraDTOs;
  }


  @Override
  public int getCameraDTOListAccount(String shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.countCameraDTOList(shopId);
  }

  @Override
  public void unBandShop(CameraDTO cameraDTO) {
    String cameraIds = cameraDTO.getIds().substring(0, cameraDTO.getIds().length() - 1);
    if (cameraIds.indexOf(",") != -1) {
      String[] cameraId = cameraIds.split(",");
      for (String id : cameraId) {
        unBandShopDetail(id);
      }
    } else {
      unBandShopDetail(cameraIds);
    }

  }


  public void unBandShopDetail(String id) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    //摄像头camera表状态更新
    Camera camera = writer.getById(Camera.class, NumberUtil.longValue(id));
    camera.setStatus("nobinding");
    writer.update(camera);
    //中间表记录逻辑删除
    CameraDTO cameraDTONew = new CameraDTO();
    cameraDTONew.setId(id);
    CameraShop cameraShop = writer.getCameraShop(cameraDTONew);
    cameraShop.setStatus("nobinding");
    cameraShop.setDeleted(DeletedType.TRUE);
    writer.update(cameraShop);
    writer.commit(status);
  }

  @Override
  public List<CameraRecordDTO> getCameraRecordDTOList(Pager pager, String id) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<CameraRecordDTO> cameraRecordDTOs = writer.getCameraRecordDTOList(pager, id);
    if (CollectionUtil.isEmpty(cameraRecordDTOs)) return null;
    return cameraRecordDTOs;
  }


  @Override
  public int getCameraRecordDTOListAccount(String id) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.countCameraRecordDTOList(id);
  }

  //添加或修改camera
  public Camera saveOrUpdateCameraVLPR(CameraDTO cameraDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Camera camera = new Camera();
    try {
      if (StringUtil.isEmpty(cameraDTO.getId())) {
        //新增摄像头基本参数
        camera = saveOrUpdateCameraVLP(cameraDTO);
      } else {
        //更新摄像头基本参数
        camera = updateCameraVLPR(cameraDTO);
      }
      writer.commit(status);
    } catch (ParseException e) {
      e.printStackTrace();
    } finally {
      writer.rollback(status);
    }
    return camera;
  }

  @Override
  public Camera getCamera(Camera camera) {
    ConfigWriter writer = configDaoManager.getWriter();
    Camera camera_ = writer.getCamera(camera);
    if (camera_ == null) return null;
    return camera_;
  }

  @Override
  public Camera getCameraBySerialNo(String serialNo) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getCameraBySerialNo(serialNo);
  }

  @Override
  public void saveCameraRecordDTO(CameraRecordDTO cameraRecordDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    CameraRecord cameraRecord = new CameraRecord();
    try {
      cameraRecord = cameraRecord.fromCameraRecordDTO(cameraRecordDTO);
      writer.saveOrUpdate(cameraRecord);
      writer.commit(status);
    } catch (ParseException e) {
      e.printStackTrace();
    }

  }


//  @Override
//  public CameraConfig getCameraConfig(String name, Long shopId){
//    ConfigWriter writer = configDaoManager.getWriter();
//    CameraConfig cameraConfig = writer.getCameraConfig(name,shopId);
//        return cameraConfig;
//
//  }

  @Override
  public String getShopNameByCameraId(Long camera_id) {
    ConfigWriter writer = configDaoManager.getWriter();
    String shopName = writer.getShopNameByCameraId(camera_id);
    return shopName;
  }

  @Override
  public CameraRecord getCameraRecordByVehicle_no(String vehicle_no) {
    ConfigWriter writer = configDaoManager.getWriter();
    CameraRecord cameraRecord = writer.getCameraRecordByVehicle_no(vehicle_no);
    if (cameraRecord == null) return null;
    return cameraRecord;
  }

  @Override
  public CameraConfigDTO getCameraConfigByCameraId(String CameraId) {
    ConfigWriter writer = configDaoManager.getWriter();
    CameraConfig cameraConfig = writer.getCameraConfigByCameraId(CameraId);
    if (cameraConfig != null) {
      CameraConfigDTO cameraConfigDTO = cameraConfig.toCameraConfigDTO();
      if (cameraConfigDTO == null) return null;
      return cameraConfigDTO;
    }
    return null;
  }

  @Override
  public CameraConfigDTO getCameraConfigBySerialNo(String cameraSerialNo) {
    Camera camera = getCameraBySerialNo(cameraSerialNo);
    if (camera == null) return null;
    return getCameraConfigByCameraId(StringUtil.valueOf(camera.getId()));
  }


  @Override
  public List<CameraConfigDTO> getCameraConfigByShopId(String shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<CameraConfigDTO> cameraConfigDTOList = writer.getCameraConfigByShopId(shopId);
    return cameraConfigDTOList;
  }

  @Override
  public void updateCameraConfig(CameraConfigDTO cameraConfigDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    CameraConfig cameraConfig = writer.getCameraConfigByCameraId(cameraConfigDTO.getCamera_id());
    try {
      cameraConfig.setInterval_time_warn(NumberUtil.longValue(cameraConfigDTO.getInterval_time_warn()));
      cameraConfig.setShop_id(-1L);
      if ("".equals(cameraConfigDTO.getWhite_vehicle_nos())) {
        cameraConfig.setWhite_vehicle_nos("无");
      } else {
        cameraConfig.setWhite_vehicle_nos(cameraConfigDTO.getWhite_vehicle_nos());
      }
      cameraConfig.setOrder_type(cameraConfigDTO.getOrder_type());
      cameraConfig.setMember_card(cameraConfigDTO.getMember_card());
      cameraConfig.setConstruction_project_text(cameraConfigDTO.getConstruction_project_text().replace(",", " "));

      int count = cameraConfigDTO.getConstruction_project_value().length() - cameraConfigDTO.getConstruction_project_value().replace(",", "").length();
      if (count == 1) {
        cameraConfig.setConstruction_project_value(cameraConfigDTO.getConstruction_project_value());
      } else {
        String construction_project_value = cameraConfigDTO.getConstruction_project_value().substring(0, cameraConfigDTO.getConstruction_project_value().length() - 1);
        cameraConfig.setConstruction_project_value(construction_project_value);
      }
      writer.update(cameraConfig);
      writer.commit(status);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void updateCameraConfig_admin(CameraConfigDTO cameraConfigDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    CameraConfig cameraConfig = writer.getCameraConfigByCameraId(cameraConfigDTO.getCamera_id());
    if (cameraConfig != null) {
      try {
        cameraConfig.setInterval_time_warn(NumberUtil.longValue(cameraConfigDTO.getInterval_time_warn()));
        cameraConfig.setShop_id(-1L);
        cameraConfig.setWhite_vehicle_nos(cameraConfigDTO.getWhite_vehicle_nos());
        cameraConfig.setOrder_type(cameraConfigDTO.getOrder_type());
        cameraConfig.setMember_card(cameraConfigDTO.getMember_card());
        cameraConfig.setPrinter_serial_no(cameraConfigDTO.getPrinter_serial_no());
        writer.update(cameraConfig);
        writer.commit(status);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      cameraConfig = new CameraConfig();
      cameraConfig.setCamera_id(NumberUtil.longValue(cameraConfigDTO.getCamera_id()));
      cameraConfig.setInterval_time_warn(NumberUtil.longValue(cameraConfigDTO.getInterval_time_warn()));
      cameraConfig.setShop_id(-1L);
      cameraConfig.setWhite_vehicle_nos(cameraConfigDTO.getWhite_vehicle_nos());
      cameraConfig.setOrder_type(cameraConfigDTO.getOrder_type());
      cameraConfig.setMember_card(cameraConfigDTO.getMember_card());
      writer.saveOrUpdate(cameraConfig);
      writer.commit(status);
    }

  }

  @Override
  public List<CameraRecordDTO> getCameraRecordListByShopId(Pager pager, CameraSearchCondition condition) {
    ConfigWriter writer = configDaoManager.getWriter();
    generateSearchInfo(condition);
    List<CameraRecordDTO> cameraRecordDTOs = writer.getCameraRecordListByShopId(pager, condition);
    if (cameraRecordDTOs == null) return null;
    return cameraRecordDTOs;
  }

  @Override
  public List<CameraSearchCondition> getVehicle_nos(CameraSearchCondition condition) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<CameraSearchCondition> cameraSearchConditions = writer.getVehicle_nos(condition.getVehicle_nos());
    if (cameraSearchConditions == null) return null;
    return cameraSearchConditions;
  }


  public void generateSearchInfo(CameraSearchCondition condition) {
    Long startDateTemp = null;
    Long endDateTemp = null;
    if (StringUtils.isNotBlank(condition.getStartDateStr())) {
      try {
        startDateTemp = DateUtil.getStartTimeOfDate(condition.getStartDateStr());
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }

    if (StringUtils.isNotBlank(condition.getEndDateStr())) {
      try {
        endDateTemp = DateUtil.getEndTimeOfDate(condition.getEndDateStr());
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }

    if (NumberUtil.longValue(startDateTemp) > NumberUtil.longValue(endDateTemp) && NumberUtil.longValue(endDateTemp) > 0) {
      if (StringUtils.isNotBlank(condition.getStartDateStr())) {
        try {
          endDateTemp = DateUtil.getStartTimeOfDate(condition.getStartDateStr());
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }

      if (StringUtils.isNotBlank(condition.getEndDateStr())) {
        try {
          startDateTemp = DateUtil.getEndTimeOfDate(condition.getEndDateStr());
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }
    }
    condition.setStartDate(startDateTemp);
    condition.setEndDate(endDateTemp);
  }

  @Override
  public int getCameraRecordDTOListAccountByShopId(CameraSearchCondition condition) {
    generateSearchInfo(condition);
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.countCameraRecordDTOListByShopId(condition);
  }

  @Override
  public int getCountCameraConfigByShopId(String shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getCountCameraConfigByShopId(shopId);
  }


  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}


