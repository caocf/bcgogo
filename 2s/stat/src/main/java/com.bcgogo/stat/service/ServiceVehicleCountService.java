package com.bcgogo.stat.service;

import com.bcgogo.stat.dto.ServiceVehicleCountDTO;
import com.bcgogo.stat.model.ServiceVehicleCount;
import com.bcgogo.stat.model.StatDaoManager;
import com.bcgogo.stat.model.StatWriter;
import com.bcgogo.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 001
 * Date: 12-2-9
 * Time: 下午7:15
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ServiceVehicleCountService implements IServiceVehicleCountService{
     private static final Logger LOG = LoggerFactory.getLogger(BizStatService.class);

    @Autowired
    private StatDaoManager statDaoManager;

      /**
   * 根据日期查询服务车辆数
   * @author zhangchuanlong
   * @param shopId
   * @param serviceTime
   * @return
   */
   @Override
   public List<ServiceVehicleCount>  getServiceVehicleCountByTime(long shopId,long serviceTime)
   {
       StatWriter writer = statDaoManager.getWriter();
       List<ServiceVehicleCount> times=writer.getServiceVehicleCountByTime(shopId,serviceTime);

       if(times!=null)
       {
           return times;
       }
       else
       {
        return null;
       }
   }
      /**
      * 更新服务车辆数
      * @author zhangchuanlong
      * @param
      * @return
      */
//根据充值序号更新payTime
  public void  updateServiceVehicleCountByTime(ServiceVehicleCount svc)
    {
        StatWriter writer = statDaoManager.getWriter();
        Object status = writer.begin();
        try{
            writer.update(svc);
            writer.commit(status);
           }
         finally {
            writer.rollback(status);
       }
    }
   /**
      * 保存服务车辆数
      * @author zhangchuanlong
      * @param  svcDTO
      * @return
      */
  public void saveServiceVehicleCount(ServiceVehicleCountDTO svcDTO)
  {
      ServiceVehicleCount svc=new ServiceVehicleCount(svcDTO);
      StatWriter writer = statDaoManager.getWriter();
        Object status = writer.begin();
        try{
              writer.save(svc);
              writer.commit(status);
         }
         finally {
            writer.rollback(status);
       }
  }

  /**查询有没有服务记录 没有就创建 有就更新
   * @param shopId
   * @param serviceTime
   */
  @Override
  public void saveOrUpdateServiceVehicleCount(Long shopId, Long serviceTime) {
    if (shopId == null) {
      return;
    }
    if (serviceTime == null) {
      serviceTime = System.currentTimeMillis();
    }
    serviceTime = Long.parseLong(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY3, serviceTime));
    List<ServiceVehicleCount> serviceTimes = getServiceVehicleCountByTime(shopId, serviceTime);
    if (serviceTimes == null) {
      ServiceVehicleCountDTO svcDTO = new ServiceVehicleCountDTO(shopId, serviceTime, 1l);
      saveServiceVehicleCount(svcDTO);
    } else {
      for (ServiceVehicleCount s : serviceTimes) {
        s.setCount(s.getCount() + 1);
        updateServiceVehicleCountByTime(s);
      }
    }
  }
}
