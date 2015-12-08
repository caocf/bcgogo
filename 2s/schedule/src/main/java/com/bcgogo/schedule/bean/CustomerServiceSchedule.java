package com.bcgogo.schedule.bean;

import com.bcgogo.enums.RemindEventType;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerServiceJobDTO;
import com.bcgogo.user.model.CustomerServiceJob;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.UserConstant;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-7
 * Time: 下午4:59
 * To change this template use File | Settings | File Templates.
 */
public class CustomerServiceSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceSchedule.class);
  private static boolean lock = false;
  private static int bxDays = 15;
  private static int ycDays = 10;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
   
    if (lock) {
      return;
    } else {
      lock = true;
      try {
        IUserService userService = ServiceManager.getService(IUserService.class);
        Long remindDate = DateUtil.convertDateDateShortToDateLong(DateUtil.MONTH_DATE,new Date());
        List<CustomerDTO> customerDTOList = userService.getCustomerByBirth(remindDate);
//          List<CustomerServiceJob>  customerServiceJobList = userService.getCustomerServiceJobByRemindType(UserConstant.BIRTH_TIME);
//          userService.deleteCustomerBirthdayRemind(customerServiceJobList);
        List<CustomerServiceJobDTO> customerServiceJobDTOs = new ArrayList<CustomerServiceJobDTO>();
        for(CustomerDTO customerDTO : customerDTOList){
          CustomerServiceJobDTO customerServiceJobDTO = new CustomerServiceJobDTO();
          Long shopId = customerDTO.getShopId();
          customerServiceJobDTO.setShopId(shopId);
          customerServiceJobDTO.setCustomerId(customerDTO.getId());
          customerServiceJobDTO.setStatus(UserConstant.Status.ACTIVITY);
          customerServiceJobDTO.setRemindType(UserConstant.BIRTH_TIME);
          String birth1=DateUtil.convertDateLongToDateString(DateUtil.MONTH_DATE,customerDTO.getBirthday());
          String birth2=String.valueOf(Calendar.getInstance().get(Calendar.YEAR))+"-"+birth1;
          customerServiceJobDTO.setRemindTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,birth2));
          customerServiceJobDTOs.add(customerServiceJobDTO);
          if(customerServiceJobDTOs.size()>=100){
            userService.saveCustomerBirthdayRemind(customerServiceJobDTOs);
            for (CustomerServiceJobDTO customerServiceJobDTO1 : customerServiceJobDTOs) {
              CustomerDTO customerDTO1 = ServiceManager.getService(ICustomerService.class).getCustomerById(customerServiceJobDTO1.getCustomerId());
              if(customerDTO1!=null){
                //加入提醒总表
                ServiceManager.getService(ITxnService.class).saveRemindEvent(new CustomerServiceJob().fromDTO(customerServiceJobDTO1), customerDTO1.getName(), customerDTO1.getMobile(), null);
                //add by WLF 更新缓存
                ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, customerServiceJobDTO1.getShopId());
              }
            }
            customerServiceJobDTOs.clear();
          }
        }
        userService.saveCustomerBirthdayRemind(customerServiceJobDTOs);
        for (CustomerServiceJobDTO customerServiceJobDTO2 : customerServiceJobDTOs) {
          CustomerDTO customerDTO = ServiceManager.getService(ICustomerService.class).getCustomerById(customerServiceJobDTO2.getCustomerId());
          if(customerDTO!=null){
            //加入提醒总表
            ServiceManager.getService(ITxnService.class).saveRemindEvent(new CustomerServiceJob().fromDTO(customerServiceJobDTO2), customerDTO.getName(), customerDTO.getMobile(), null);
            //add by WLF 更新缓存
            ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, customerServiceJobDTO2.getShopId());
          }
        }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      } finally {
        lock = false;
      }
    }
  }

  private static Calendar getCalendarFromLong(Long remindDate) {
    if (remindDate == null) {
      return null;
    }
    Date d = new Date(remindDate);
    Calendar c = Calendar.getInstance();
    c.setTime(d);
    return c;
  }

}
