package com.bcgogo.schedule.bean;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.CustomerServiceJob;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.UserConstant;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-25
 * Time: 下午3:10
 */
public class VehicleMaintainRemindSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(VehicleMaintainRemindSchedule.class);
  private static final int pageSize = 50;  //todo测试的时候小一点能测出一些边界问题来，发布的时候记得改成50

  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    try {
      processUpdateVehicleOBDMileage();
      processUpdateMaintainRemind();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      lock = false;
    }
  }

  //更新vehicle上OBDMil 的里程和更新时间
  public void processUpdateVehicleOBDMileage(){
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
     Long startVehicleId = 0L;
    while (true){
      List<VehicleOBDMileageDTO> vehicleOBDMileageDTOs = vehicleService.getVehicleOBDMileageByStartVehicleId(startVehicleId,pageSize);
      if(CollectionUtils.isEmpty(vehicleOBDMileageDTOs)){
        break;
      }

      Set<Long> shopIdSet = new HashSet<Long>();

      Map<Long,VehicleOBDMileageDTO> vehicleOBDMileageDTOMap = new HashMap<Long, VehicleOBDMileageDTO>();
      for(VehicleOBDMileageDTO vehicleOBDMileageDTO : vehicleOBDMileageDTOs){

         if(vehicleOBDMileageDTO.getShopId() != null){
           shopIdSet.add(vehicleOBDMileageDTO.getShopId());
         }

         if(vehicleOBDMileageDTO != null && vehicleOBDMileageDTO.getVehicleId() != null){
             if(vehicleOBDMileageDTO.getVehicleId()> startVehicleId){
               startVehicleId =  vehicleOBDMileageDTO.getVehicleId();
             }
           VehicleOBDMileageDTO validatedDTO = vehicleOBDMileageDTOMap.get(vehicleOBDMileageDTO.getVehicleId());
           if (validatedDTO == null
               || NumberUtil.longValue(vehicleOBDMileageDTO.getMileageLastUpdateTime()) > NumberUtil.longValue(validatedDTO.getMileageLastUpdateTime())
               && NumberUtil.doubleVal(vehicleOBDMileageDTO.getObdMileage()) > 0) {
             vehicleOBDMileageDTOMap.put(vehicleOBDMileageDTO.getVehicleId(),vehicleOBDMileageDTO);
           }
         }
      }
      vehicleOBDMileageDTOs =  new ArrayList<VehicleOBDMileageDTO>(vehicleOBDMileageDTOMap.values()) ;

      Map<Long, ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
      if (CollectionUtils.isNotEmpty(shopIdSet)) {
        shopDTOMap = configService.getShopByShopId(shopIdSet.toArray(new Long[shopIdSet.size()]));
      }

      if(CollectionUtils.isNotEmpty(vehicleOBDMileageDTOs)){
        for(VehicleOBDMileageDTO vehicleOBDMileageDTO :vehicleOBDMileageDTOs){

          ShopDTO shopDTO = shopDTOMap.get(vehicleOBDMileageDTO.getShopId());
          if (shopDTO != null && ConfigUtils.isFourSShopVersion(shopDTO.getShopVersionId())) {
            continue;
          }
          vehicleService.updateVehicleOBDMileage(vehicleOBDMileageDTO);
        }
      }
    }
  }


  public void processUpdateMaintainRemind(){
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
         ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
         ITxnService txnService = ServiceManager.getService(ITxnService.class);
         Double[] intervals = ConfigUtils.getAppVehicleMaintainMileageIntervals();
         //1.处理下次保养里程与当前保养里程在设置的范围内的时候  包括新增提醒，以及activity的提醒中需要更新保养里程
         Long lastCustomerVehicleId = 0L;
         while (true) {
           List<CustomerVehicleDTO> customerVehicleDTOs = vehicleService.getMatchCustomerVehicleByVehicleOBDMileage(pageSize, intervals,lastCustomerVehicleId);
           if (CollectionUtils.isEmpty(customerVehicleDTOs)) {
             break;
           }
           Set<Long> vehicleIds = new HashSet<Long>();
           Set<Long> customerIds = new HashSet<Long>();
           Set<Long> toUpdateCustomerServiceCountShopIds = new HashSet<Long>();
           List<CustomerServiceJob> toSaveOrUpdateRemindEvent = new ArrayList<CustomerServiceJob>();
           for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOs) {
             if (customerVehicleDTO != null && customerVehicleDTO.getCustomerId() != null) {
               customerIds.add(customerVehicleDTO.getCustomerId());
             }
             if (customerVehicleDTO != null && customerVehicleDTO.getVehicleId() != null) {
               vehicleIds.add(customerVehicleDTO.getVehicleId());
             }
             if(customerVehicleDTO != null && NumberUtil.longValue(customerVehicleDTO.getId())>NumberUtil.longValue(lastCustomerVehicleId)){
               lastCustomerVehicleId =  NumberUtil.longValue(customerVehicleDTO.getId());
             }
           }
           Map<Long,CustomerDTO> customerDTOMap = customerService.getCustomerByIdSet(null,customerIds);
           Map<Long,VehicleDTO> vehicleDTOMap = vehicleService.getVehicleByVehicleIdSet(null, vehicleIds);
           Set<Long> remindTypes = new HashSet<Long>();
           remindTypes.add(UserConstant.MAINTAIN_MILEAGE);
           UserWriter userWriter = ServiceManager.getService(UserDaoManager.class).getWriter();
           Object userStatus = userWriter.begin();
           try{
             Map<Long,List<CustomerServiceJob>> customerServiceJobMap = userWriter.getCustomerServiceJobByVehicleIds(vehicleIds,remindTypes);
             for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOs) {
               if(customerVehicleDTO != null && customerVehicleDTO.getCustomerId() != null && customerVehicleDTO.getVehicleId() != null){
                 List<CustomerServiceJob> customerServiceJobs = customerServiceJobMap.get(customerVehicleDTO.getVehicleId());
                 VehicleDTO vehicleDTO = vehicleDTOMap.get(customerVehicleDTO.getVehicleId());
                 boolean isNeedToSaveCustomerServiceJob = true;
                 if(CollectionUtils.isNotEmpty(customerServiceJobs)){
                   for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
                     if(UserConstant.MAINTAIN_MILEAGE.equals(customerServiceJob.getRemindType())){
                       isNeedToSaveCustomerServiceJob = false;
                       if (NumberUtil.longValue(customerVehicleDTO.getMaintainMileage()) != NumberUtil.longValue(customerServiceJob.getRemindMileage())) {
                         if (!UserConstant.Status.ACTIVITY.equals(customerServiceJob.getStatus()) && vehicleDTO != null && vehicleDTO.getShopId() != null) {
                           toUpdateCustomerServiceCountShopIds.add(vehicleDTO.getShopId());
                         }
                         customerServiceJob.setRemindMileage(customerVehicleDTO.getMaintainMileage());
                         customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
                         userWriter.update(customerServiceJob);
                         toSaveOrUpdateRemindEvent.add(customerServiceJob);
                       }
                     }
                   }
                 }

                 //需要新增的customerServiceJob
                 if (isNeedToSaveCustomerServiceJob) {
                   CustomerServiceJob customerServiceJob = new CustomerServiceJob();
                   if (vehicleDTO != null) {
                     customerServiceJob.setShopId(vehicleDTO.getShopId());
                     toUpdateCustomerServiceCountShopIds.add(vehicleDTO.getShopId());
                   }
                   customerServiceJob.setCustomerId(customerVehicleDTO.getCustomerId());
                   customerServiceJob.setVehicleId(customerVehicleDTO.getVehicleId());
                   customerServiceJob.setRemindType(UserConstant.MAINTAIN_MILEAGE);
                   customerServiceJob.setAppointName(UserConstant.CustomerRemindType.MAINTAIN_TIME);
                   customerServiceJob.setRemindMileage(customerVehicleDTO.getMaintainMileage());
                   customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
                   userWriter.save(customerServiceJob);
                   if(customerServiceJobs == null){
                     customerServiceJobs = new ArrayList<CustomerServiceJob>();
                   }
                   customerServiceJobs.add(customerServiceJob);
                   customerServiceJobMap.put(customerVehicleDTO.getVehicleId(), customerServiceJobs);
                   toSaveOrUpdateRemindEvent.add(customerServiceJob);
                 }
               }
             }
             userWriter.commit(userStatus);
           } finally {
             userWriter.rollback(userStatus);
           }
           //保存remind_event
           if(CollectionUtils.isNotEmpty(toSaveOrUpdateRemindEvent)) {
             for (CustomerServiceJob customerServiceJob : toSaveOrUpdateRemindEvent) {
               if (customerServiceJob != null) {
                 CustomerDTO customerDTO = customerDTOMap.get(customerServiceJob.getCustomerId());
                 VehicleDTO vehicleDTO = vehicleDTOMap.get(customerServiceJob.getVehicleId());
                 txnService.saveRemindEvent(customerServiceJob, customerDTO.getName(), customerDTO.getMobile(), vehicleDTO.getLicenceNo());
               }
             }
           }
           //更新缓存
           if(CollectionUtils.isNotEmpty(toUpdateCustomerServiceCountShopIds)){
             for(Long shopId :toUpdateCustomerServiceCountShopIds){
               if(shopId != null) {
                 txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, shopId);
               }
             }
           }
         }

         //2.处理下次保养里程与当前保养里程在设置的范围外并且不是cancel的提醒
         Long lastCustomerServiceJobId = 0L;
         while (true) {
           UserWriter userWriter = ServiceManager.getService(UserDaoManager.class).getWriter();
           Object userStatus = userWriter.begin();
           Set<Long> toUpdateCustomerServiceCountShopIds = new HashSet<Long>();
           Set<Long> vehicleIds = new HashSet<Long>();
           Set<Long> customerIds = new HashSet<Long>();
           Map<Long,CustomerDTO> customerDTOMap = new HashMap<Long, CustomerDTO>();
           Map<Long,VehicleDTO> vehicleDTOMap = new HashMap<Long, VehicleDTO>();
           List<CustomerServiceJob> customerServiceJobs = new ArrayList<CustomerServiceJob>();
           try{
             customerServiceJobs = userWriter.getOverMaintainMileageWithOBDMileage(pageSize,intervals,lastCustomerServiceJobId);
             if(CollectionUtils.isEmpty(customerServiceJobs)){
               break;
             }
             for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
               if (customerServiceJob != null && customerServiceJob.getCustomerId() != null) {
                 customerIds.add(customerServiceJob.getCustomerId());
               }
               if (customerServiceJob != null && customerServiceJob.getVehicleId() != null) {
                 vehicleIds.add(customerServiceJob.getVehicleId());
               }
               if (customerServiceJob != null && NumberUtil.longValue(customerServiceJob.getId()) > NumberUtil.longValue(lastCustomerServiceJobId)) {
                 lastCustomerServiceJobId = NumberUtil.longValue(customerServiceJob.getId());
               }
             }
             customerDTOMap = customerService.getCustomerByIdSet(null,customerIds);
             vehicleDTOMap = vehicleService.getVehicleByVehicleIdSet(null, vehicleIds);
             Map<Long,CustomerVehicleDTO> customerVehicleDTOMap = vehicleService.getCustomerVehicleDTOMapByVehicleIds(vehicleIds.toArray(new Long[vehicleIds.size()]));
             for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
               if (customerServiceJob != null) {
                 CustomerVehicleDTO customerVehicleDTO = customerVehicleDTOMap.get(customerServiceJob.getVehicleId());
                 if (customerVehicleDTO != null) {
                   customerServiceJob.setRemindMileage(customerVehicleDTO.getMaintainMileage());
                 } else {
                   customerServiceJob.setRemindMileage(null);
                 }
                 customerServiceJob.setStatus(UserConstant.Status.CANCELED);
                 userWriter.update(customerServiceJob);
               }
             }
             userWriter.commit(userStatus);
           } finally {
             userWriter.rollback(userStatus);
           }
           //更新remindEvent
           for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
             if (customerServiceJob != null) {
               CustomerDTO customerDTO = customerDTOMap.get(customerServiceJob.getCustomerId());
               VehicleDTO vehicleDTO = vehicleDTOMap.get(customerServiceJob.getVehicleId());
               String customerName =  customerDTO != null ? customerDTO.getName():null;
               String mobile =  customerDTO != null ? customerDTO.getMobile():null;
               String licenceNo =  vehicleDTO != null ? vehicleDTO.getLicenceNo():null;
               txnService.saveRemindEvent(customerServiceJob, customerName, mobile, licenceNo);
             }
           }
           //更新缓存
           if(CollectionUtils.isNotEmpty(toUpdateCustomerServiceCountShopIds)){
             for(Long shopId :toUpdateCustomerServiceCountShopIds){
               if(shopId != null) {
                 txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, shopId);
               }
             }
           }
         }
  }

}
