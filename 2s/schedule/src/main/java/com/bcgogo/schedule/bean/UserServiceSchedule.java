package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-3
 * Time: 下午5:05
 * To change this template use File | Settings | File Templates.
 */
public class UserServiceSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(UserServiceSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } /*else {
      lock = true;
      try {

        IUserService userService = ServiceManager.getService(IUserService.class);
        ITxnService txnService = ServiceManager.getService(ITxnService.class);

        txnService.deleteUserToDo();

        //生日提醒
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);

        List<CustomerDTO> customerDTOList = null;
        try {
          //customerDTOList = userService.getCustomerByBirth(month,day);
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
        if (customerDTOList != null) {
          for (CustomerDTO customerDTO : customerDTOList) {
            UserToDoDTO userToDoDTO = new UserToDoDTO();
            userToDoDTO.setRemindType("生日");
            userToDoDTO.setEstimateTime(year + "-" + month + "-" + day);
            String name = customerDTO.getName();
            userToDoDTO.setName(name);
            userToDoDTO.setMobile(customerDTO.getMobile());

            //上一次消费内容
            List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByName(name);
            if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty()) {
              CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(0);
              if (customerRecordDTO != null) {
                userToDoDTO.setBillContent(customerRecordDTO.getLastBill());
                Long lastDate = customerRecordDTO.getLastDate();
                String lastDateStr = getDateFromLongToString(lastDate);
                userToDoDTO.setComeTime(lastDateStr);
                double lastAmount = customerRecordDTO.getLastAmount();
                userToDoDTO.setTotalMoney(lastAmount);
              }
            }

            Long shopId = customerDTO.getShopId();
            if (shopId != null) {
              userToDoDTO.setShopId(shopId);
            }
            Long customerId = customerDTO.getId();
            if (customerId != null) {
              //车牌号
              List<CustomerVehicleDTO> customerVehicleDTOList = userService.getVehicleByCustomerId(customerId);
              if (customerVehicleDTOList != null && !customerVehicleDTOList.isEmpty()) {
                CustomerVehicleDTO customerVehicleDTO = customerVehicleDTOList.get(0);
                Long vehicleId = customerVehicleDTO.getVehicleId();
                if (vehicleId != null) {
                  VehicleDTO vehicleDTO = userService.getVehicleById(vehicleId);
                  if (vehicleDTO != null) {
                    userToDoDTO.setLicenceNo(vehicleDTO.getLicenceNo());
                  }
                }
              }
            }
            //生日提醒
            txnService.createUserToDo(userToDoDTO);

          }
        }

        //保险 验车


      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      } finally {
        lock = false;
      }
    }  */
  }

  private static String getDateFromLongToString(Long time) {
    if (time == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    Date date = new Date(time);
    return sdf.format(date);
  }

}
