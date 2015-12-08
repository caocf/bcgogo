package com.bcgogo.stat.service;

import com.bcgogo.stat.dto.ServiceVehicleCountDTO;
import com.bcgogo.stat.model.ServiceVehicleCount;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 001
 * Date: 12-2-9
 * Time: 下午7:17
 * To change this template use File | Settings | File Templates.
 */
public interface IServiceVehicleCountService {
    /**
   * 根据日期查询服务车辆数
   * @author zhangchuanlong
   * @param shopId
   * @param serviceTime
   * @return
   */
   public List<ServiceVehicleCount> getServiceVehicleCountByTime(long shopId,long serviceTime) ;

//  public void  updateServiceVehicleCountByTime(long shopId, long serviceTime,long count) ;
   public void  updateServiceVehicleCountByTime(ServiceVehicleCount svc);

   public void saveServiceVehicleCount(ServiceVehicleCountDTO svcDTO);

  public void saveOrUpdateServiceVehicleCount(Long shopId,Long serviceTime);
}
