package com.bcgogo.stat.service;

import com.bcgogo.stat.dto.CustomerStatDTO;
import com.bcgogo.stat.model.CustomerStat;
import com.bcgogo.stat.model.StatDaoManager;
import com.bcgogo.stat.model.StatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

@Component
public class CustomerStatService implements ICustomerStatService {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerStatService.class);

  @Autowired
  private StatDaoManager statDaoManager;

  @Override
  public CustomerStatDTO saveCustomerStat(CustomerStatDTO customerStatDTO) {
    if (customerStatDTO == null) return null;

    StatWriter writer = statDaoManager.getWriter();
    Object status = writer.begin();

    try {
      CustomerStat customerStat = new CustomerStat(customerStatDTO);

      writer.deleteCustomerStatByShopAndType(
          customerStat.getShopId(),
          customerStat.getCustomerType()
      );

      writer.save(customerStat);

      writer.commit(status);

      customerStatDTO.setId(customerStat.getId());

      return customerStatDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CustomerStatDTO getCustomerStatById(long customerStatId) {
    StatWriter writer = statDaoManager.getWriter();

    CustomerStat customerStat = writer.getById(CustomerStat.class, customerStatId);

    if (customerStat == null) return null;
    return customerStat.toDTO();
  }

  @Override
  public List<CustomerStatDTO> getShopCustomerStatByType(long shopId, String customerType) {
    StatWriter writer = statDaoManager.getWriter();

    List<CustomerStatDTO> customerStatDTOList = new ArrayList<CustomerStatDTO>();
    for (CustomerStat customerStat : writer.getShopCustomerStatByType(shopId, customerType)) {
      customerStatDTOList.add(customerStat.toDTO());
    }

    return customerStatDTOList;
  }

  @Override
  public List<CustomerStatDTO> getShopCustomerStat(long shopId) {
    StatWriter writer = statDaoManager.getWriter();

    List<CustomerStatDTO> customerStatDTOList = new ArrayList<CustomerStatDTO>();
    for (CustomerStat customerStat : writer.getShopCustomerStat(shopId)) {
      customerStatDTOList.add(customerStat.toDTO());
    }

    return customerStatDTOList;
  }
}
