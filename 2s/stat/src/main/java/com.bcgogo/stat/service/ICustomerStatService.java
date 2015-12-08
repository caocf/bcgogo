package com.bcgogo.stat.service;

import com.bcgogo.stat.dto.CustomerStatDTO;

import java.util.List;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

public interface ICustomerStatService {

  public CustomerStatDTO saveCustomerStat(CustomerStatDTO customerStatDTO);

  public CustomerStatDTO getCustomerStatById(long customerStatId);

  public List<CustomerStatDTO> getShopCustomerStat(long shopId);

  public List<CustomerStatDTO> getShopCustomerStatByType(long shopId, String statType);
}
