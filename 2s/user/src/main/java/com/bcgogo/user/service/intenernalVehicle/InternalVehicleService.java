package com.bcgogo.user.service.intenernalVehicle;

import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.ShopInternalVehicle;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.sshtools.j2ssh.util.Hash;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by XinyuQiu on 14-12-11.
 */
@Component
public class InternalVehicleService implements IInternalVehicleService {
  private static final Logger LOG = LoggerFactory.getLogger(InternalVehicleService.class);

  @Autowired
  private UserDaoManager userDaoManager;


  @Override
  public Result saveOrUpdateShopInternalVehicles(ShopInternalVehicleGroupDTO shopInternalVehicleGroupDTO) {
    UserWriter writer = userDaoManager.getWriter();
    boolean isSuccess = true;
    String validateMsg = "";
    if (shopInternalVehicleGroupDTO == null || shopInternalVehicleGroupDTO.getShopId() == null) {
      isSuccess = false;
      validateMsg += "未选店铺，请选择店铺";
    }
    if (isSuccess) {
      Set<String> newVehicleNos = new HashSet<String>();
      Set<Long> newVehicleIds = new HashSet<Long>();
      if (StringUtils.isNotBlank(shopInternalVehicleGroupDTO.getVehicleNos())) {
        String vehicleNosStr = shopInternalVehicleGroupDTO.getVehicleNos();
        vehicleNosStr = vehicleNosStr.toUpperCase();
        vehicleNosStr = vehicleNosStr.replaceAll("，", ",");
        vehicleNosStr = vehicleNosStr.replaceAll("，", ",");
        for (String vehicleNo : vehicleNosStr.split(",")) {
          if (StringUtils.isNotBlank(vehicleNo)) {
            newVehicleNos.add(vehicleNo.trim());
          }
        }
      }
      if (CollectionUtils.isNotEmpty(newVehicleNos)) {
        List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(shopInternalVehicleGroupDTO.getShopId(), newVehicleNos);
        if (CollectionUtils.isNotEmpty(vehicleList)) {
          for (Vehicle vehicle : vehicleList) {
            newVehicleIds.add(vehicle.getId());
          }
        }
      }

      Object status = writer.begin();
      try {
        List<ShopInternalVehicle> shopInternalVehicleList = writer.getShopInternalVehicleByShopId(shopInternalVehicleGroupDTO.getShopId());
        Set<Long> existVehicleIds = new HashSet<Long>();
        if (CollectionUtils.isNotEmpty(shopInternalVehicleList)) {
          for (ShopInternalVehicle shopInternalVehicle : shopInternalVehicleList) {
            if (!newVehicleIds.contains(shopInternalVehicle.getVehicleId())) {
              shopInternalVehicle.setDeleted(DeletedType.TRUE);
              writer.update(shopInternalVehicle);
            } else {
              existVehicleIds.add(shopInternalVehicle.getVehicleId());
            }
          }
        }
        if (CollectionUtils.isNotEmpty(newVehicleIds)) {
          for (Long vehicleId : newVehicleIds) {
            if (!existVehicleIds.contains(vehicleId)) {
              ShopInternalVehicle shopInternalVehicle = new ShopInternalVehicle();
              shopInternalVehicle.setShopId(shopInternalVehicleGroupDTO.getShopId());
              shopInternalVehicle.setVehicleId(vehicleId);
              shopInternalVehicle.setDeleted(DeletedType.FALSE);
              writer.save(shopInternalVehicle);
            }
          }
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }

    return new Result(validateMsg, isSuccess);
  }

  @Override
  public int countShopInternalVehicleGroupByShopId() {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopInternalVehicleGroupByShopId();
  }

  @Override
  public List<ShopInternalVehicleGroupDTO> getShopInternalVehicleGroupDTOs(Pager pager) {
    UserWriter writer = userDaoManager.getWriter();
    List<Long> shopIds = writer.getShopInternalVehicleShopIds(pager);
    List<ShopInternalVehicleGroupDTO> shopInternalVehicleGroupDTOs = new ArrayList<ShopInternalVehicleGroupDTO>();
    if(CollectionUtils.isNotEmpty(shopIds)){
      List<ShopInternalVehicle> shopInternalVehicles = writer.getShopInternalVehicleByShopIds(shopIds);
      if(CollectionUtils.isNotEmpty(shopInternalVehicles)){
        Map<Long,Set<Long>> shopIdVehicleIdsMap = new HashMap<Long, Set<Long>>();
        for(ShopInternalVehicle shopInternalVehicle : shopInternalVehicles){
          if(shopInternalVehicle!=null && shopInternalVehicle.getShopId() != null){
            Set<Long> vehicleIds = shopIdVehicleIdsMap.get(shopInternalVehicle.getShopId());
            if(vehicleIds == null){
              vehicleIds = new HashSet<Long>();
            }
            vehicleIds.add(shopInternalVehicle.getVehicleId());
            shopIdVehicleIdsMap.put(shopInternalVehicle.getShopId(),vehicleIds);
          }
        }
        if(MapUtils.isNotEmpty(shopIdVehicleIdsMap)){
          IConfigService configService = ServiceManager.getService(IConfigService.class);
          IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
          Map<Long,ShopDTO> shopDTOMap =  configService.getShopByShopId(shopIdVehicleIdsMap.keySet().toArray(new Long[shopIdVehicleIdsMap.keySet().size()]));
          for(Long shopId : shopIdVehicleIdsMap.keySet()){
            ShopDTO shopDTO = shopDTOMap.get(shopId);
            if(shopDTO != null){
             Map<Long,VehicleDTO> vehicleDTOMap = vehicleService.getVehicleByVehicleIdSet(shopId,shopIdVehicleIdsMap.get(shopId));
              ShopInternalVehicleGroupDTO shopInternalVehicleGroupDTO = new ShopInternalVehicleGroupDTO();
              shopInternalVehicleGroupDTO.setShopName(shopDTO.getName());
              shopInternalVehicleGroupDTO.setShopId(shopDTO.getId());
              shopInternalVehicleGroupDTO.setVehicleCount(vehicleDTOMap.size());
              StringBuilder sb = new StringBuilder();
              for(VehicleDTO vehicleDTO :vehicleDTOMap.values()){
                if(sb.length() > 0){
                  sb.append(",");
                }
                sb.append(vehicleDTO.getLicenceNo());
              }
              shopInternalVehicleGroupDTO.setVehicleNos(sb.toString());
              shopInternalVehicleGroupDTOs.add(shopInternalVehicleGroupDTO);
            }
          }
        }
      }
    }
    return shopInternalVehicleGroupDTOs;
  }

  @Override
  public List<ShopInternalVehicleDTO> getQueryInternalVehicleNo(Long shopId, String vehicleNo) {
    List<ShopInternalVehicleDTO> shopInternalVehicleDTOs = new ArrayList<ShopInternalVehicleDTO>();
    if(shopId == null){
      return shopInternalVehicleDTOs;
    }
    UserWriter writer = userDaoManager.getWriter();

    shopInternalVehicleDTOs = writer.getQueryInternalVehicleNo(shopId,vehicleNo);
    return shopInternalVehicleDTOs;
  }

  @Override
  public void generateSearchInfo(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    Long startDateTemp = null;
    Long endDateTemp = null;
    if(StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getStartDateStr())){
      try{
        startDateTemp= DateUtil.getStartTimeOfDate(shopInternalVehicleRequestDTO.getStartDateStr());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
    }

    if(StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getEndDateStr())){
      try{
        endDateTemp= DateUtil.getEndTimeOfDate(shopInternalVehicleRequestDTO.getEndDateStr());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
    }

    if(NumberUtil.longValue(startDateTemp)>NumberUtil.longValue(endDateTemp) && NumberUtil.longValue(endDateTemp) >0){
      if(StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getStartDateStr())){
        try{
          endDateTemp= DateUtil.getStartTimeOfDate(shopInternalVehicleRequestDTO.getStartDateStr());
        }catch (Exception e){
          LOG.error(e.getMessage(),e);
        }
      }

      if(StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getEndDateStr())){
        try{
          startDateTemp= DateUtil.getEndTimeOfDate(shopInternalVehicleRequestDTO.getEndDateStr());
        }catch (Exception e){
          LOG.error(e.getMessage(),e);
        }
      }
    }
    shopInternalVehicleRequestDTO.setStartDate(startDateTemp);
    shopInternalVehicleRequestDTO.setEndDate(endDateTemp);
    Set<String> vehicleNoSet = new HashSet<String>();
    if(!ArrayUtils.isEmpty(shopInternalVehicleRequestDTO.getVehicleNos())){
      for(int i =0;i<shopInternalVehicleRequestDTO.getVehicleNos().length;i++){
        if(StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getVehicleNos()[i])){
          vehicleNoSet.add(shopInternalVehicleRequestDTO.getVehicleNos()[i].trim().toUpperCase());
        }
      }
    }
    shopInternalVehicleRequestDTO.setVehicleNoSet(vehicleNoSet);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);

    Map<String,VehicleDTO> vehicleNoVehicleDTOMap =vehicleService.getVehicleDTOMapByLicenceNo(shopInternalVehicleRequestDTO.getShopId(),
        shopInternalVehicleRequestDTO.getVehicleNoSet());
    Map<Long,VehicleDTO> idVehicleDTOMap = new HashMap<Long, VehicleDTO>();
    if(MapUtils.isNotEmpty(vehicleNoVehicleDTOMap)){
      for(VehicleDTO vehicleDTO : vehicleNoVehicleDTOMap.values()){
        if(vehicleDTO != null){
          idVehicleDTOMap.put(vehicleDTO.getId(),vehicleDTO);
        }
      }
    }
    shopInternalVehicleRequestDTO.setVehicleDTOMap(idVehicleDTOMap);
    shopInternalVehicleRequestDTO.setVehicleIds(idVehicleDTOMap.keySet());
  }

  @Override
  public int countShopDriveLogStat(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    if(shopInternalVehicleRequestDTO.getShopId() == null ){
      return 0;
    }
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopDriveLogStat(shopInternalVehicleRequestDTO);
  }

  @Override
  public List<ShopInternalVehicleDriveStatDTO> getShopDriveLogStat(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    if(shopInternalVehicleRequestDTO.getShopId() == null ){
      return new ArrayList<ShopInternalVehicleDriveStatDTO>();
    }
    UserWriter writer = userDaoManager.getWriter();
    List<ShopInternalVehicleDriveStatDTO> shopInternalVehicleDriveStatDTOs = writer.getShopDriveLogStat(shopInternalVehicleRequestDTO);
    Map<Long,VehicleDTO> vehicleDTOMap = shopInternalVehicleRequestDTO.getVehicleDTOMap();

    if(CollectionUtils.isNotEmpty(shopInternalVehicleDriveStatDTOs)){
      Set<Long> vehicleIds = new HashSet<Long>();
      for(ShopInternalVehicleDriveStatDTO shopInternalVehicleDriveStatDTO :shopInternalVehicleDriveStatDTOs){
        VehicleDTO vehicleDTO = null;
        if(MapUtils.isNotEmpty(vehicleDTOMap)){
          vehicleDTO = vehicleDTOMap.get(shopInternalVehicleDriveStatDTO.getVehicleId());
          if(vehicleDTO != null){
            shopInternalVehicleDriveStatDTO.setVehicleNo(vehicleDTO.getLicenceNo());
            shopInternalVehicleDriveStatDTO.setVehicleInfo(vehicleDTO.getBrand()+" " + vehicleDTO.getModel());
          }
        }
        if(vehicleDTO == null){
          vehicleIds.add(shopInternalVehicleDriveStatDTO.getVehicleId());
        }
      }
      if(CollectionUtils.isNotEmpty(vehicleIds)){
        IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
        Map<Long,VehicleDTO> newVehicleDTOMap = vehicleService.getVehicleByVehicleIdSet(shopInternalVehicleRequestDTO.getShopId(),vehicleIds);
        for(ShopInternalVehicleDriveStatDTO shopInternalVehicleDriveStatDTO :shopInternalVehicleDriveStatDTOs){
          if(MapUtils.isNotEmpty(newVehicleDTOMap)){
           VehicleDTO vehicleDTO = newVehicleDTOMap.get(shopInternalVehicleDriveStatDTO.getVehicleId());
            if(vehicleDTO!=null){
              shopInternalVehicleDriveStatDTO.setVehicleNo(vehicleDTO.getLicenceNo());
              shopInternalVehicleDriveStatDTO.setVehicleInfo(vehicleDTO.getBrand()+" " + vehicleDTO.getModel());
            }
          }
        }
      }

    }
    return shopInternalVehicleDriveStatDTOs;
  }

  @Override
  public int countShopDriveLog(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    if(shopInternalVehicleRequestDTO == null || shopInternalVehicleRequestDTO.getShopId() == null ||
        CollectionUtils.isEmpty(shopInternalVehicleRequestDTO.getVehicleIds())){
      return 0;
    }
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopDriveLog(shopInternalVehicleRequestDTO);
  }

  @Override
  public List<DriveLogDTO> getShopDriveLogDTOs(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    if(shopInternalVehicleRequestDTO == null || shopInternalVehicleRequestDTO.getShopId() == null ||
        CollectionUtils.isEmpty(shopInternalVehicleRequestDTO.getVehicleIds())){
      return driveLogDTOs;
    }
    UserWriter writer = userDaoManager.getWriter();
    driveLogDTOs = writer.getShopDriveLogDTOs(shopInternalVehicleRequestDTO);
    return driveLogDTOs;
  }
}
