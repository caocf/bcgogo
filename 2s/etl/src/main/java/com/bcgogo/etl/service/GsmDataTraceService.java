package com.bcgogo.etl.service;

import com.bcgogo.common.Pair;
import com.bcgogo.etl.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by XinyuQiu on 2015-03-24.
 */
@Component
public class GsmDataTraceService implements IGsmDataTraceService {
  private static final Logger LOG = LoggerFactory.getLogger(GsmDataTraceService.class);
  @Autowired
  private EtlDaoManager etlDaoManager;


  @Override
  public void traceObdGsmPointData() {
    int groupLimit = 1;//多少个imei号一起回收
    int searchLimit = 1000;//判断是否回收最少数据
    int tracePageSize = 1000;//每次回收数据大小
    int traceLimit = 1000;//回收剩余数据量
    //回收剩余数据时间，比如回收一个月前的数据，
    Long beforeTime = System.currentTimeMillis() - 60L * 60L * 24L * 7L * 1000L;  //todo 改成7天
    //时间和剩余最小数量同时满足的就回收,先找到要回收的imei号，再确定回收最后一条数据的上传时间
    EtlWriter writer = etlDaoManager.getWriter();
    while (true) {
      List<Pair<String, Long>> iMeiCountList = writer.getGsmPointTraceGroup(groupLimit, searchLimit, beforeTime);
      if (CollectionUtils.isEmpty(iMeiCountList)) {
        break;
      }
      for (Pair<String, Long> pair : iMeiCountList) {
        if (pair == null || pair.getKey() == null || pair.getValue() == null) {
          continue;
        }
        LOG.info("imei:【{}】,有【{}】条gsmPoint数据需要回收，开始回收",pair.getKey(),pair.getValue());
        int gsmPointCount = 0;
        GsmPoint lastTraceGsmPoint = writer.lastTraceGsmPoint(pair.getKey(), beforeTime, traceLimit);

        while (true) {
          Object status = writer.begin();
          try {
            if (lastTraceGsmPoint == null) {
              break;
            }
            List<GsmPoint> gsmPoints = writer.getGsmPointsByImeiAndBeforeTimeAndLimit(pair.getKey(), lastTraceGsmPoint.getUploadServerTime(), tracePageSize);
            if (CollectionUtils.isNotEmpty(gsmPoints)) {
              for (GsmPoint gsmPoint : gsmPoints) {
                GsmPointTrace gsmPointTrace = new GsmPointTrace();
                gsmPointTrace.setGsmpoint(gsmPoint);
                writer.save(gsmPointTrace);
                writer.delete(gsmPoint);
                gsmPointCount++;
              }
            } else {
              break;
            }
            writer.commit(status);
            LOG.info("imei:【{}】,成功回收【{}】gsmPoint条数据",pair.getKey(),gsmPointCount);
          } finally {
            writer.rollback(status);
          }
        }

      }

    }
  }

  @Override
  public void traceObdGsmVehicleData() {
    int groupLimit = 1;//多少个imei号一起回收
    int searchLimit = 1000;//判断是否回收最少数据
    int tracePageSize = 1000;//每次回收数据大小
    int traceLimit = 1000;//回收剩余数据量
    //回收剩余数据时间，比如回收一个月前的数据，
    Long beforeTime = System.currentTimeMillis() - 60L * 60L * 24L * 7L * 1000L;
    //时间和剩余最小数量同时满足的就回收,先找到要回收的imei号，再确定回收最后一条数据的上传时间
    EtlWriter writer = etlDaoManager.getWriter();
    while (true) {
      List<Pair<String, Long>> iMeiCountList = writer.getGsmVehicleInfoTraceGroup(groupLimit, searchLimit, beforeTime);
      if (CollectionUtils.isEmpty(iMeiCountList)) {
        break;
      }
      for (Pair<String, Long> pair : iMeiCountList) {
        if (pair == null || pair.getKey() == null || pair.getValue() == null) {
          continue;
        }
        LOG.info("imei:【{}】,有【{}】条gsmVehicleInfo数据需要回收，开始回收",pair.getKey(),pair.getValue());
        int gsmVehicleCount = 0;
        GsmVehicleInfo lastTraceGsmVehicleInfo = writer.lastTraceGsmVehicleInfo(pair.getKey(), beforeTime, traceLimit);
        while (true) {
          Object status = writer.begin();
          try {
            if (lastTraceGsmVehicleInfo == null) {
              break;
            }
            List<GsmVehicleInfo> gsmVehicleInfos = writer.getGsmVehicleInfosByImeiAndBeforeTimeAndLimit(pair.getKey(), lastTraceGsmVehicleInfo.getUploadServerTime(), tracePageSize);
            if (CollectionUtils.isNotEmpty(gsmVehicleInfos)) {
              for (GsmVehicleInfo gsmVehicleInfo : gsmVehicleInfos) {
                GsmVehicleInfoTrace gsmVehicleInfoTrace = new GsmVehicleInfoTrace();
                gsmVehicleInfoTrace.setGsmvehicleInfo(gsmVehicleInfo);
                writer.save(gsmVehicleInfoTrace);
                writer.delete(gsmVehicleInfo);
                gsmVehicleCount ++;
              }
            } else {
              break;
            }
            writer.commit(status);
            LOG.info("imei:【{}】,成功回收【{}】gsmVehicleInfo条数据",pair.getKey(),gsmVehicleCount);
          } finally {
            writer.rollback(status);
          }
        }
      }

    }
  }
}
