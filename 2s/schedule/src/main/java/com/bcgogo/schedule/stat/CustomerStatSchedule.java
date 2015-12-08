package com.bcgogo.schedule.stat;

import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.CustomerStatDTO;
import com.bcgogo.stat.model.CustomerStatType;
import com.bcgogo.stat.service.ICustomerStatService;
import com.bcgogo.userreport.service.IUserReportService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xiaojian
 * Date: 12-1-5
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
public class CustomerStatSchedule extends BcgogoQuartzJobBean {

  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      try {
        IConfigService configService = ServiceManager.getService(IConfigService.class);

        List<Shop> shopList = configService.getShop();

        for (Shop shop : shopList) {
          long shopId = shop.getId();

          this.count(shopId);
        }


      } finally {
        lock = false;
      }
    }

  }

  private void count(long shopId) {
    IUserReportService userReportService = ServiceManager.getService(IUserReportService.class);

    this.saveCustomerStat(shopId, CustomerStatType.TOTAL, userReportService.countShopCustomer(shopId));

    this.saveCustomerStat(shopId, CustomerStatType.PHONE, userReportService.countShopCustomer(shopId));

    this.saveCustomerStat(shopId, CustomerStatType.INSURANCE, userReportService.countShopCustomer(shopId));

    this.saveCustomerStat(shopId, CustomerStatType.INSPECTION, userReportService.countShopCustomer(shopId));

    this.saveCustomerStat(shopId, CustomerStatType.BIRTHDAY, userReportService.countShopCustomer(shopId));

  }

  private void saveCustomerStat(long shopId, CustomerStatType customerStatType, long statAmount) {
    ICustomerStatService customerStatService = ServiceManager.getService(ICustomerStatService.class);

    CustomerStatDTO customerStatDTO = new CustomerStatDTO();
    customerStatDTO.setShopId(shopId);
    customerStatDTO.setCustomerType(customerStatType.toString());
    customerStatDTO.setAmount(statAmount);

    customerStatService.saveCustomerStat(customerStatDTO);

  }
}
