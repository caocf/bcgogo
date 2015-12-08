package com.bcgogo.txn.service.remind;

import com.bcgogo.enums.RemindEventType;
import com.bcgogo.notification.SmsHelper;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.AppointServiceDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerServiceJobDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.UserConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by XinyuQiu on 14-5-26.
 */
@Component
public class CustomerRemindService implements ICustomerRemindService {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerRemindService.class);

  @Override
  public List<CustomerServiceJobDTO> generateCustomerServiceJob(Long shopId, List<RemindEventDTO> txnRemindEventDTOList) throws Exception {
    IContactService contactService = ServiceManager.getService(IContactService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<CustomerServiceJobDTO> customerServiceJobDTOList = new ArrayList<CustomerServiceJobDTO>();
    Set<Long> customerIds = new HashSet<Long>();
    for(RemindEventDTO remindEventDTO : txnRemindEventDTOList){
      customerIds.add(remindEventDTO.getCustomerId());
    }
//    Map<Long, CustomerDTO> customerMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(shopId, customerIds);
    Map<Long,List<ContactDTO>> customerContactDTOsMap = contactService.getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerIds),"customer");

    Set<String> LicenceNoSet = new HashSet<String>();
    for(RemindEventDTO remindEventDTO : txnRemindEventDTOList){
      if (StringUtils.isNotEmpty(remindEventDTO.getLicenceNo())) {
        LicenceNoSet.add(remindEventDTO.getLicenceNo());
      }
      CustomerServiceJobDTO customerServiceJobDTO = new CustomerServiceJobDTO();
      customerServiceJobDTO.setCustomerId(remindEventDTO.getCustomerId());
      customerServiceJobDTO.setCustomerName(remindEventDTO.getCustomerName());
      customerServiceJobDTO.setRemindTime(remindEventDTO.getRemindTime());
      customerServiceJobDTO.setStatus(remindEventDTO.getRemindStatus());
      customerServiceJobDTO.setLicenceNo(remindEventDTO.getLicenceNo());
      customerServiceJobDTO.setId(remindEventDTO.getId());
      customerServiceJobDTO.setRemindMileage(remindEventDTO.getRemindMileage());
      //保养里程
      if(remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.MAINTAIN_TIME) && NumberUtil.longValue(remindEventDTO.getRemindMileage()) > 0) {
        customerServiceJobDTO.setRemindType(UserConstant.MAINTAIN_MILEAGE);
      }else if(remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.MAINTAIN_TIME)){
        customerServiceJobDTO.setRemindType(UserConstant.MAINTAIN_TIME);
      }else if(remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.EXAMINE_TIME)){
        customerServiceJobDTO.setRemindType(UserConstant.EXAMINE_TIME);
      }else if(remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.INSURE_TIME)){
        customerServiceJobDTO.setRemindType(UserConstant.INSURE_TIME);
      }else if(remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.BIRTH_TIME)){
        customerServiceJobDTO.setRemindType(UserConstant.BIRTH_TIME);
      }else if(remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.APPOINT_SERVICE)){
        customerServiceJobDTO.setRemindType(UserConstant.APPOINT_SERVICE);
      }else if(remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.MEMBER_SERVICE)){
        customerServiceJobDTO.setRemindType(UserConstant.MEMBER_SERVICE);
      }
      ContactDTO contactDTO = SmsHelper.getFirstHasMobileContactDTO(customerContactDTOsMap.get(remindEventDTO.getCustomerId()));
      if(contactDTO!=null){
        customerServiceJobDTO.setMobile(contactDTO.getMobile());
        customerServiceJobDTO.setContact(contactDTO.getName());
        customerServiceJobDTO.setContactId(contactDTO.getId());
      }
      //客户预约提醒
      if(RemindEventType.CUSTOMER_SERVICE.toString().equals(remindEventDTO.getEventType())){
        //自定义服务，需要从预约服务表读取
        if(UserConstant.CustomerRemindType.APPOINT_SERVICE.equals(remindEventDTO.getEventStatus())){
          AppointServiceDTO appointServiceDTO = userService.getAppointServiceById(remindEventDTO.getAppointServiceId());
          if(appointServiceDTO!=null){
            customerServiceJobDTO.setAppointName(appointServiceDTO.getAppointName());
          }
        }else{
          customerServiceJobDTO.setAppointName(remindEventDTO.getEventStatus());
        }
      }
      //会员服务到期提醒
      else if(RemindEventType.MEMBER_SERVICE.toString().equals(remindEventDTO.getEventType())){
        ServiceDTO serviceDTO = txnService.getServiceById(remindEventDTO.getServiceId());
        if(serviceDTO!=null){
          customerServiceJobDTO.setAppointName(serviceDTO.getName());
        }
      }
      customerServiceJobDTOList.add(customerServiceJobDTO);
    }
    if (CollectionUtils.isNotEmpty(LicenceNoSet)) {
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      Map<String, VehicleDTO> map = vehicleService.getVehicleDTOMapByLicenceNo(shopId, LicenceNoSet);
      if (map != null) {
        for (CustomerServiceJobDTO customerServiceJobDTO : customerServiceJobDTOList) {
          if (StringUtils.isNotEmpty(customerServiceJobDTO.getLicenceNo())) {
            VehicleDTO vehicleDTO = map.get(customerServiceJobDTO.getLicenceNo());
            if (vehicleDTO != null) {
              customerServiceJobDTO.setVehicleMobile(vehicleDTO.getMobile());
              customerServiceJobDTO.setVehicleCustomerName(vehicleDTO.getContact());
              customerServiceJobDTO.setCurrentMileage(NumberUtil.compareDouble(vehicleDTO.getStartMileage(), vehicleDTO.getObdMileage())?vehicleDTO.getStartMileage():vehicleDTO.getObdMileage());
              customerServiceJobDTO.setVehicleId(vehicleDTO.getId());
            }
          }
        }
      }
    }
    return customerServiceJobDTOList;
  }


}
