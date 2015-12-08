package com.bcgogo.userreport.service;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

public interface IUserReportService {

  public long countShopCustomer(long shopId);

  public long countShopCustomerMobile(long shopId);

  public long countShopCustomerInsurance(long shopId);

  public long countShopCustomerInspection(long shopId);

  public long countShopCustomerBirthday(long shopId);
}
