package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.api.DriveStatDTO;
import com.bcgogo.api.response.ApiDriveStatResponse;
import com.bcgogo.enums.app.DriveLogStatus;
import com.bcgogo.enums.app.DriveStatStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.DriveLog;
import com.bcgogo.user.model.app.DriveStat;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DriveStatService implements IDriveStatService {

  @Autowired
  private UserDaoManager userDaoManager;

  private static final Logger LOG = LoggerFactory.getLogger(DriveStatService.class);

  /**
   * 获取最近一年的行车轨迹统计
   * @param appUserNo appUserNo
   * @return ApiResponse
   */
  @Override
  public ApiResponse getYearDriveStat(String appUserNo) {
    ApiDriveStatResponse apiDriveStatResponse = new ApiDriveStatResponse(MessageCode.toApiResponse(MessageCode.DRIVE_STAT_LIST_SUCCESS));
    if (StringUtils.isNotEmpty(appUserNo)) {
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      Long endTime = System.currentTimeMillis();
      Long startTime = DateUtil.getOneYearBeforeStartTime();
      List<DriveStatDTO> driveStatDTOs = getDriveStatDTOsByStatDate(appUserNo, startTime, endTime);
      Set<DriveLogStatus> driveLogStatuses = new HashSet<DriveLogStatus>();
      driveLogStatuses.add(DriveLogStatus.DRIVING);
      driveLogStatuses.add(DriveLogStatus.ENABLED);
      List<DriveLogDTO> driveLogDTOs = driveLogService.getDriveLogContentsByStatusAndStatStatus(appUserNo, driveLogStatuses, DriveStatStatus.UN_STATISTIC);

      if (CollectionUtils.isNotEmpty(driveLogDTOs)) {
        for (DriveLogDTO driveLogDTO : driveLogDTOs) {
          if (driveLogDTO != null && driveLogDTO.getStartTime() != null) {
            if (driveStatDTOs == null) {
              driveStatDTOs = new ArrayList<DriveStatDTO>();
            }
            int year = DateUtil.getYear(driveLogDTO.getStartTime());
            int month = DateUtil.getMonth(driveLogDTO.getStartTime());
            boolean isStated = false;
            for (DriveStatDTO driveStatDTO : driveStatDTOs) {
              if (driveStatDTO != null && driveStatDTO.getStatYear() != null && driveStatDTO.getStatMonth() != null) {
                if (driveStatDTO.getStatYear().equals(year) && driveStatDTO.getStatMonth().equals(month)) {
                  isStated = true;
                  driveStatDTO.addDriveLogStat(driveLogDTO);
                  break;
                }
              }
            }
            if (!isStated) {
              DriveStatDTO driveStatDTO = new DriveStatDTO();
              driveStatDTO.setDriveLogDTO(driveLogDTO);
              driveStatDTOs.add(driveStatDTO);
            }

          }
        }
      }
      if (CollectionUtils.isNotEmpty(driveStatDTOs)) {
        Collections.sort(driveStatDTOs, new Comparator<DriveStatDTO>() {
          @Override
          public int compare(DriveStatDTO o1, DriveStatDTO o2) {
            if (NumberUtil.intValue(o1.getStatYear(), 0) > NumberUtil.intValue(o2.getStatYear(), 0)) {
              return 1;
            } else if (NumberUtil.intValue(o1.getStatYear(), 0) < NumberUtil.intValue(o2.getStatYear(), 0)) {
              return -1;
            }
            if (NumberUtil.intValue(o1.getStatMonth(), 0) > NumberUtil.intValue(o2.getStatMonth(), 0)) {
              return 1;
            } else if (NumberUtil.intValue(o1.getStatMonth(), 0) < NumberUtil.intValue(o2.getStatMonth(), 0)) {
              return -1;
            }
            return 0;
          }
        });
      }

      apiDriveStatResponse.setYearStat(cacYearStat(driveStatDTOs));
      apiDriveStatResponse.setMonthStats(generateStat(driveStatDTOs));
      return apiDriveStatResponse;
    }
    return apiDriveStatResponse;
  }

  //补充没有行程月份的数据
  private List<DriveStatDTO> generateStat(List<DriveStatDTO> driveStatDTOs){
    if(CollectionUtils.isNotEmpty(driveStatDTOs) && driveStatDTOs.size()<12){
      List<DriveStatDTO> driveStatDTOList = new ArrayList<DriveStatDTO>();
      DriveStatDTO firstDriveStatDTO = CollectionUtil.getFirst(driveStatDTOs);
      Long startTime = DateUtil.getDateByYearMonth(firstDriveStatDTO.getStatYear(), firstDriveStatDTO.getStatMonth());
      Long endTime = System.currentTimeMillis();
      Long currentStatTime = startTime;
      int currentStatYear = firstDriveStatDTO.getStatYear(),currentStatMonth = firstDriveStatDTO.getStatMonth();
      for (int i = 0; i < 12 && currentStatTime <= endTime; i++) {
        boolean isHaveStat = false;
        for(DriveStatDTO driveStatDTO : driveStatDTOs){
          if(driveStatDTO != null && NumberUtil.intValue(driveStatDTO.getStatYear())== currentStatYear
              && NumberUtil.intValue(driveStatDTO.getStatMonth())== currentStatMonth){
            isHaveStat = true;
            driveStatDTOList.add(driveStatDTO);
            break;
          }
        }
        if(!isHaveStat){
          DriveStatDTO driveStatDTO = new DriveStatDTO();
          driveStatDTO.setStatYear(currentStatYear);
          driveStatDTO.setStatMonth(currentStatMonth);
          driveStatDTO.setDistance(0d);
          driveStatDTO.setOilCost(0d);
          driveStatDTO.setOilWear(0d);
          driveStatDTO.setOilMoney(0d);
          driveStatDTOList.add(driveStatDTO);
        }
        currentStatTime = DateUtil.getNextMonthTime(currentStatTime);
        currentStatYear = DateUtil.getYear(currentStatTime);
        currentStatMonth = DateUtil.getMonth(currentStatTime);
      }
      return driveStatDTOList;
    }
    return driveStatDTOs;
  }

  @Override
  public List<DriveStatDTO> getDriveStatDTOsByStatDate(String appUserNo, Long startTime, Long endTime) {
    List<DriveStatDTO> driveStatDTOs = new ArrayList<DriveStatDTO>();
    if (StringUtils.isNotEmpty(appUserNo) && startTime != null && endTime != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<DriveStat> driveStats = writer.getDriveStatsByStatDate(appUserNo, startTime, endTime);
      if (CollectionUtils.isNotEmpty(driveStats)) {
        for (DriveStat driveStat : driveStats) {
          if (driveStat != null) {
            driveStatDTOs.add(driveStat.toDTO());
          }
        }
      }
    }
    return driveStatDTOs;
  }

  private DriveStatDTO cacYearStat(List<DriveStatDTO> driveStatDTOs) {
    if(CollectionUtils.isNotEmpty(driveStatDTOs)){
      DriveStatDTO yearDriveStat = new DriveStatDTO();
      boolean setYearMonth = false;
      double totalDistance = 0,totalOilCost = 0 ,totalOilMoney = 0;
      for(DriveStatDTO driveStatDTO :driveStatDTOs){
        if(!setYearMonth){
          setYearMonth = true;
          yearDriveStat.setStatYear(driveStatDTO.getStatYear());
          yearDriveStat.setStatMonth(driveStatDTO.getStatMonth());
        }
        totalDistance += NumberUtil.doubleVal(driveStatDTO.getDistance());
        totalOilCost += NumberUtil.doubleVal(driveStatDTO.getOilCost());
        totalOilMoney += NumberUtil.doubleVal(driveStatDTO.getOilMoney());
      }
      yearDriveStat.setDistance(NumberUtil.round(totalDistance, 1));
      yearDriveStat.setOilCost(NumberUtil.round(totalOilCost, 1));
      yearDriveStat.setOilMoney(NumberUtil.round(totalOilMoney, 1));
      yearDriveStat.cacOilWear();
      return yearDriveStat;
    }
    return null;
  }

  @Override
  public void statDriveLog(int limit) {
    IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
    IDriveStatService driveStatService = ServiceManager.getService(IDriveStatService.class);
    Set<DriveLogStatus> driveLogStatuses = new HashSet<DriveLogStatus>();
    driveLogStatuses.add(DriveLogStatus.ENABLED);
    while (true){
      List<DriveLogDTO> driveLogDTOs = driveLogService.getDriveLogContentsByStatusAndStatStatus(driveLogStatuses,DriveStatStatus.UN_STATISTIC,limit);
      if(CollectionUtils.isEmpty(driveLogDTOs)){
        break;
      }
      for(DriveLogDTO driveLogDTO :driveLogDTOs){
        if (driveLogDTO == null) {
          continue;
        }
        try {
          if (StringUtils.isNotEmpty(driveLogDTO.getAppUserNo())
              && driveLogDTO.getStartTime() != null) {
            DriveStatDTO driveStatDTO = new DriveStatDTO();
            driveStatDTO.setDriveLogDTO(driveLogDTO);
            driveStatService.saveOrUpdateDriveStatServiceDTO(driveStatDTO);
            driveStatService.updateDriveLogStatStatus(driveLogDTO.getId(), DriveStatStatus.STATISTIC);
          } else {
            driveStatService.updateDriveLogStatStatus(driveLogDTO.getId(), DriveStatStatus.FAILED);
          }
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
          driveStatService.updateDriveLogStatStatus(driveLogDTO.getId(), DriveStatStatus.FAILED);
        }

      }
    }
  }

  @Override
  public DriveStatDTO getDriveStatDTOByYearAndMonth(String appUserNo, int year, int month) {
    if(StringUtils.isNotBlank(appUserNo) && year>0 && month >0){
      UserWriter writer = userDaoManager.getWriter();
      DriveStat driveStat = writer.getDriveStatByYearAndMonth(appUserNo,year,month);
      if(driveStat != null){
        return driveStat.toDTO();
      }
    }
    return null;
  }

  @Override
  public void saveOrUpdateDriveStatServiceDTO(DriveStatDTO driveStatDTO) {
    if (driveStatDTO != null && StringUtils.isNotBlank(driveStatDTO.getAppUserNo())
        && driveStatDTO.getStatYear() > 0 && driveStatDTO.getStatMonth() > 0) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        DriveStat driveStat = writer.getDriveStatByYearAndMonth(driveStatDTO.getAppUserNo(), driveStatDTO.getStatYear(), driveStatDTO.getStatMonth());
        if (driveStatDTO.getStatDate() == null) {
          driveStatDTO.setStatDate(DateUtil.getDateByYearMonth(driveStatDTO.getStatYear(), driveStat.getStatMonth()));
        }
        if (driveStat == null) {
          driveStat = new DriveStat();
          driveStat.fromDTO(driveStatDTO);
          writer.save(driveStat);
        } else {
          driveStat.addFromDTO(driveStatDTO);
          writer.update(driveStat);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    } else {
      LOG.error("保存车况统计数据出错：driveStatDTO:{}", JsonUtil.objectToJson(driveStatDTO));
    }
  }

  @Override
  public void updateDriveLogStatStatus(Long id, DriveStatStatus statistic) {
    if(id!=null && statistic != null){
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try{
        DriveLog driveLog = writer.getById(DriveLog.class, id);
        if(driveLog != null){
          driveLog.setDriveStatStatus(statistic);
          writer.update(driveLog);
          writer.commit(status);
        }
      }finally {
        writer.rollback(status);
      }
    }
  }
}
