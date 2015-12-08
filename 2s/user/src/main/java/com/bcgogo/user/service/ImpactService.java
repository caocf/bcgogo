package com.bcgogo.user.service;

import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.api.ImpactDetailDTO;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.AppConstant;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.UploadStatus;
import com.bcgogo.etl.ImpactDTO;
import com.bcgogo.etl.ImpactVideoExpDTO;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.impact.ImpactInfoSearchConditionDTO;
import com.bcgogo.user.ImpactAndVideoDTO;
import com.bcgogo.user.ImpactVideoDTO;
import com.bcgogo.user.model.Impact;
import com.bcgogo.user.model.ImpactVideo;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.OBD;
import com.bcgogo.user.model.app.ObdUserVehicle;
import com.bcgogo.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-30
 * Time: 15:47
 */
@Component
public class ImpactService implements IImpactService {
  private static final Logger LOG = LoggerFactory.getLogger(ImpactService.class);

  @Autowired
  private UserDaoManager daoManager;

  @Override
  public ImpactVideo getImpactVideoById(Long impactVideoId) {
    UserWriter writer = daoManager.getWriter();
    return writer.getById(ImpactVideo.class, impactVideoId);
  }

  @Override
  public ImpactVideoDTO getImpactVideoDTOById(Long impactVideoId) {
    ImpactVideo video = getImpactVideoById(impactVideoId);
    return video != null ? video.toDTO() : null;
  }

  @Override
  public ImpactVideoDTO getImpactVideoDTOByUUID(String uuid) {
    UserWriter writer = daoManager.getWriter();
    ImpactVideo video = writer.getImpactVideoDTOByUUID(uuid);
    return video != null ? video.toDTO() : null;
  }

  @Override
  public List<ImpactVideoDTO> getImpactVideoDTOByAppUserNo(String appUserNo) {
    UserWriter writer = daoManager.getWriter();
    List<ImpactVideo> impactVideos = writer.getImpactVideoDTOByAppUserNo(appUserNo);
    if (CollectionUtil.isEmpty(impactVideos)) return null;
    List<ImpactVideoDTO> impactVideoDTOs = new ArrayList<ImpactVideoDTO>();
    for (ImpactVideo video : impactVideos) {
      impactVideoDTOs.add(video.toDTO());
    }
    return impactVideoDTOs;
  }


  @Override
  public int statImpactVideo(String appUserNo, Long startTime, Long endTime) {
    UserWriter writer = daoManager.getWriter();
    return writer.statImpactVideo(appUserNo, startTime, endTime);
  }

  @Override
  public void saveOrUpdateImpactVideo(ImpactVideoDTO impactVideoDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    ImpactVideo impactVideo = null;
    try {
      if (impactVideoDTO.getId() != null) {
        impactVideo = getImpactVideoById(impactVideoDTO.getId());
      } else {
        impactVideo = new ImpactVideo();

      }
      impactVideo.fromDTO(impactVideoDTO);
      writer.saveOrUpdate(impactVideo);
      writer.commit(status);
      impactVideoDTO.setId(impactVideo.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateImpact(ImpactDTO impactDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    Impact impact = null;
    try {
      if (impactDTO.getId() != null) {
        impact = writer.getById(Impact.class, impactDTO.getId());
      } else {
        impact = new Impact();
      }
      impact.fromDTO(impactDTO);
      writer.saveOrUpdate(impact);
      writer.commit(status);
      impactDTO.setId(impact.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveImpactCollectData(ImpactDTO impactDTO) {
    //取第一个
    GsmVehicleDataDTO gsmVehicleDataDTO = ArrayUtil.getFirst(impactDTO.getData());
    if(gsmVehicleDataDTO!=null){
      impactDTO.setUploadTime(gsmVehicleDataDTO.getUploadTime());
      impactDTO.setLat(gsmVehicleDataDTO.getLat());
      impactDTO.setLon(gsmVehicleDataDTO.getLon());
    }
    impactDTO.setUploadServerTime(System.currentTimeMillis());
    IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
    AddressComponent addressComponent = geocodingService.gpsToAddress(impactDTO.getLat(), impactDTO.getLon());
    if (addressComponent != null) {
      impactDTO.setAddr(addressComponent.getDistrict() + addressComponent.getStreet() + addressComponent.getStreetNumber()); //碰撞地址
    }
    saveOrUpdateImpact(impactDTO);
    //保存车况
    GsmVehicleDataDTO[] dataDTOs = impactDTO.getData();
    String appUserNo = impactDTO.getAppUserNo();
    if (ArrayUtil.isNotEmpty(dataDTOs)) {
      try {
        for (GsmVehicleDataDTO dataDTO : dataDTOs) {
          dataDTO.setAppUserNo(appUserNo);
          dataDTO.setUuid(impactDTO.getUuid());
          dataDTO.setUploadServerTime(System.currentTimeMillis());
          dataDTO.setVehicleStatus(GSMConstant.GV_IMPACT);
        }
        IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
        gsmVehicleDataService.saveOrUpdateGsmVehicleDataDTO(dataDTOs);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
  }


  @Override
  public ImpactDTO getImpactDTOByUUID(String uuid) {
    UserWriter writer = daoManager.getWriter();
    Impact impact = writer.getImpactByUUID(uuid);
    return impact != null ? impact.toDTO() : null;
  }


  @Override
  public ObdUserVehicle getObdUserVehicle(String appUserNo) {
    UserWriter writer = daoManager.getWriter();
    List<ObdUserVehicle> obdUserVehicleList = writer.getObdUserVehicleByAppUserNo(appUserNo);
    return CollectionUtil.getFirst(obdUserVehicleList);
  }

  @Override
  public String getImpactVideoUrl(Long impactVideoId) {
    if (impactVideoId == null) return null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String host = configService.getConfig("video_host", ShopConstant.BC_SHOP_ID);
    int port = NumberUtil.intValue(configService.getConfig("video_host_play_port", ShopConstant.BC_SHOP_ID), 88);
    String playPath = configService.getConfig("video_play_path", ShopConstant.BC_SHOP_ID);
    String fileName = impactVideoId + AppConstant.VIDEO_FORMAT;
    //拼接视频地址，例如：http://42.121.113.26/impact/10000010193939292.mp4
    String url = ("http://" + host + ":" + port + playPath + fileName);
    return url;
  }

  @Override
  public OBD getObdById(Long id) {
    UserWriter writer = daoManager.getWriter();
    OBD obd = writer.getById(OBD.class, id);
    return obd;
  }

  @Override
  public List<ImpactVideoExpDTO> getImpactVideoExpDTOByAppUserNo(String appUserNo) {
    UserWriter writer = daoManager.getWriter();
    return writer.getImpactVideoExpDTOs(appUserNo);
  }

  @Override
  public List<ImpactVideoExpDTO> getImpactVideoExpDTOByAppUserNo_page(String shopId, ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO) {
    UserWriter writer = daoManager.getWriter();
    if (impactInfoSearchConditionDTO.getIsDeleted() != null && impactInfoSearchConditionDTO.getIsUntreated() != null) {
      impactInfoSearchConditionDTO.setStatus(null);
    } else if (impactInfoSearchConditionDTO.getIsUntreated() != null && impactInfoSearchConditionDTO.getIsDeleted() == null) {
      impactInfoSearchConditionDTO.setStatus("FALSE");
    } else if (impactInfoSearchConditionDTO.getIsUntreated() == null && impactInfoSearchConditionDTO.getIsDeleted() != null) {
      impactInfoSearchConditionDTO.setStatus("TRUE");
    } else {
      impactInfoSearchConditionDTO.setStatus(null);
    }
    return writer.getImpactVideoExpDTOs_page(shopId, impactInfoSearchConditionDTO);
  }

  @Override
  public int countGetImpactVideoExpDTOs_page(String shopId, ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO) {
    UserWriter writer = daoManager.getWriter();
    if (impactInfoSearchConditionDTO.getIsDeleted() != null && impactInfoSearchConditionDTO.getIsUntreated() != null) {
      impactInfoSearchConditionDTO.setStatus(null);
    } else if (impactInfoSearchConditionDTO.getIsUntreated() != null && impactInfoSearchConditionDTO.getIsDeleted() == null) {
      impactInfoSearchConditionDTO.setStatus("FALSE");
    } else if (impactInfoSearchConditionDTO.getIsUntreated() == null && impactInfoSearchConditionDTO.getIsDeleted() != null) {
      impactInfoSearchConditionDTO.setStatus("TRUE");
    } else {
      impactInfoSearchConditionDTO.setStatus(null);
    }
    return writer.countGetImpactVideoExpDTOs_page(shopId, impactInfoSearchConditionDTO);
  }

  @Override
  public int countGetImpactVideoExpDTOs(String appUserNo) {
    UserWriter writer = daoManager.getWriter();
    return writer.countGetImpactVideoExpDTOs(appUserNo);
  }

  @Override
  public void deleImpactVideoExpDTOByAppUserNo(String impactVideoId) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      ImpactVideo impactVideo = writer.getById(ImpactVideo.class, Long.valueOf(impactVideoId));
      if (impactVideo != null) {
        impactVideo.setDeleted(DeletedType.TRUE);
        writer.update(impactVideo);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteShopImpactInfo(Long... ids) {
    if (ArrayUtil.isEmpty(ids)) return;
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Long id : ids) {
        ImpactVideo entity = writer.getById(ImpactVideo.class, id);
        if (entity != null) {
          entity.setDeleted(DeletedType.DELETED);
          writer.saveOrUpdate(entity);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ImpactDetailDTO> getImpact_detail(Long shopId, String impactId, long uploadTime) {
    UserWriter writer = daoManager.getWriter();
    return writer.getImpact_detail(shopId, impactId, uploadTime);
  }

  @Override
  public ImpactDetailDTO getImpact_detailByIdAndTime(Long shopId, String impactId, long uploadTime) {
    ImpactDetailDTO impactDetailDTO = null;
    IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
    UserWriter writer = daoManager.getWriter();
    Impact impact = writer.getById(Impact.class, Long.valueOf(impactId));
    if (impact != null) {
      GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getGsmVehicleDataByUUidAndUpdateTime(impact.getUuid(), impact.getUploadTime());
      if (gsmVehicleDataDTO != null) {
        impactDetailDTO = new ImpactDetailDTO();
        impactDetailDTO.setUploadTime(gsmVehicleDataDTO.getUploadTime().toString());
        impactDetailDTO.setVss(gsmVehicleDataDTO.getVss());
        impactDetailDTO.setRpm(gsmVehicleDataDTO.getRpm());
        impactDetailDTO.setRdtc(gsmVehicleDataDTO.getRdtc());
//           impactDetailDTO.setAddress();
//           impactDetailDTO.setId();
//           impactDetailDTO.setIdStr();
        impactDetailDTO.setLat(gsmVehicleDataDTO.getLat());
        impactDetailDTO.setLon(gsmVehicleDataDTO.getLon());
        impactDetailDTO.setUuid(gsmVehicleDataDTO.getUuid());
        return impactDetailDTO;
      }
    }
    return null;
  }

  /**
   * 获取碰撞视频和图片
   * @param vehicleId
   * @param datetime
   * @param count
   * @return
   */
  @Override
  public List<ImpactAndVideoDTO> getImpactAndVideo(long vehicleId, long datetime, int count) {
    UserWriter writer = daoManager.getWriter();
    List<ImpactAndVideoDTO> impactAndVideoDTOs = writer.getImpactAndVideo(vehicleId,datetime,count);
//    for (ImpactAndVideoDTO objects:impactAndVideoDTOs){
//      objects.setVideo(getImpactVideoUrl(Long.valueOf(objects.getVideo())));
//    }
    return impactAndVideoDTOs;
  }

  @Override
  public void saveOneImpact(String appUserNo) {
    Impact impact = new Impact();
    impact.setAppUserNo(appUserNo);
    impact.setType(2);
    String uuid = UUID.randomUUID().toString();
    impact.setUuid(uuid);

    ImpactVideo impactVideo = new ImpactVideo();
    impactVideo.setUuid(uuid);
    impactVideo.setAppUserNo(appUserNo);
    impactVideo.setUploadTime(System.currentTimeMillis());
    impactVideo.setUploadStatus(UploadStatus.UPLOADING);

    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(impact);
      writer.save(impactVideo);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

}
