package com.bcgogo.userreport.service;

import com.bcgogo.userreport.model.UserReportDaoManager;
import com.bcgogo.userreport.model.UserReportWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

@Component
public class UserReportService implements IUserReportService {

  @Autowired
  private UserReportDaoManager userReportDaoManager;

  @Override
  public long countShopCustomer(long shopId) {
    UserReportWriter writer = userReportDaoManager.getWriter();

    return writer.countShopCustomer(shopId);
  }

  @Override
  public long countShopCustomerMobile(long shopId) {
    UserReportWriter writer = userReportDaoManager.getWriter();

    return writer.countShopCustomerMobile(shopId);
  }

  @Override
  public long countShopCustomerInsurance(long shopId) {
    UserReportWriter writer = userReportDaoManager.getWriter();

    return writer.countShopCustomerInsurance(shopId);
  }

  @Override
  public long countShopCustomerInspection(long shopId) {
    UserReportWriter writer = userReportDaoManager.getWriter();

    return writer.countShopCustomerInspection(shopId);
  }

  @Override
  public long countShopCustomerBirthday(long shopId) {
    UserReportWriter writer = userReportDaoManager.getWriter();

    return writer.countShopCustomerBirthday(shopId);
  }
}
