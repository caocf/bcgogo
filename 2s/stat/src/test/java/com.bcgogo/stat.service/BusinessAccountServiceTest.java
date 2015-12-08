package com.bcgogo.stat.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.BusinessAccountEnum;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-25
 * Time: 下午4:53
 * To change this template use File | Settings | File Templates.
 */
public class BusinessAccountServiceTest extends AbstractTest {


  IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);

  @Test
  public void testSaveBusinessAccount() throws Exception {

    BusinessAccountDTO businessAccountDTO = this.createBusinessAccountDTO();


    BusinessAccountDTO savedBusinessAccountDTO = businessAccountService.saveBusinessAccount(businessAccountDTO);
    Assert.assertEquals(3d, savedBusinessAccountDTO.getCash().doubleValue(), 0.00001);
    Assert.assertEquals(4d, savedBusinessAccountDTO.getCheck().doubleValue(), 0.00001);
    Assert.assertNotNull(savedBusinessAccountDTO.getId());

  }

  @Test
  public void testGetBusinessAccountById() throws Exception {

    BusinessAccountDTO businessAccountDTO = this.createBusinessAccountDTO();

    BusinessAccountDTO savedBusinessAccountDTO = businessAccountService.saveBusinessAccount(businessAccountDTO);

    BusinessAccountDTO getBusinessAccountDTO = businessAccountService.getBusinessAccountById(savedBusinessAccountDTO.getId());

    Assert.assertNotNull(getBusinessAccountDTO);

    Assert.assertEquals(3d, getBusinessAccountDTO.getCash().doubleValue(), 0.00001);
    Assert.assertEquals(4d, getBusinessAccountDTO.getCheck().doubleValue(), 0.00001);

  }

  @Test
  public void testUpdateBusinessAccount() throws Exception {

    BusinessAccountDTO businessAccountDTO = this.createBusinessAccountDTO();

    BusinessAccountDTO savedBusinessAccountDTO = businessAccountService.saveBusinessAccount(businessAccountDTO);

    savedBusinessAccountDTO.setDocNo("bcgogogo");
    businessAccountService.updateBusinessAccount(savedBusinessAccountDTO);

    BusinessAccountDTO getBusinessAccountDTO = businessAccountService.getBusinessAccountById(savedBusinessAccountDTO.getId());

    Assert.assertNotNull(getBusinessAccountDTO);

    Assert.assertEquals("bcgogogo", getBusinessAccountDTO.getDocNo());


  }

  @Test
  public void testDeleteBusinessAccount() throws Exception {

    BusinessAccountDTO businessAccountDTO = this.createBusinessAccountDTO();

    BusinessAccountDTO savedBusinessAccountDTO = businessAccountService.saveBusinessAccount(businessAccountDTO);

    BusinessAccountDTO deletedBusinessAccountDTO = businessAccountService.deleteBusinessAccountById(savedBusinessAccountDTO.getId());


    Assert.assertEquals("delete", deletedBusinessAccountDTO.getStatus());


  }

  @Test
  public void testCountBusinessAccountsBySearchCondition() throws Exception {

    BusinessAccountDTO businessAccountDTO = this.createBusinessAccountDTO();
    businessAccountDTO.setPerson("john");
    BusinessAccountDTO savedBusinessAccountDTO = businessAccountService.saveBusinessAccount(businessAccountDTO);

    BusinessAccountSearchConditionDTO searchConditionDTO = new BusinessAccountSearchConditionDTO();
    searchConditionDTO.setAccountEnum(BusinessAccountEnum.STATUS_SAVE);
    searchConditionDTO.setDept("it");
    searchConditionDTO.setPerson("john");

    List<String> strings = businessAccountService.countBusinessAccountsBySearchCondition(0l, searchConditionDTO);

    Assert.assertEquals(1, Integer.valueOf(strings.get(0)).intValue());

  }

  @Test
  public void testGetSumBySearchCondition() throws Exception {

    BusinessAccountDTO businessAccountDTO = this.createBusinessAccountDTO();
    businessAccountDTO.setPerson("ROMNEY");
    BusinessAccountDTO savedBusinessAccountDTO = businessAccountService.saveBusinessAccount(businessAccountDTO);
    BusinessAccountSearchConditionDTO searchConditionDTO = new BusinessAccountSearchConditionDTO();
    searchConditionDTO.setAccountEnum(BusinessAccountEnum.STATUS_SAVE);
    searchConditionDTO.setDept("it");
    searchConditionDTO.setPerson("ROMNEY");
    double sum = businessAccountService.getSumBySearchCondition(savedBusinessAccountDTO.getShopId(), searchConditionDTO);

    Assert.assertEquals(12, sum, 0.00001);

  }


  BusinessAccountDTO createBusinessAccountDTO() {
    BusinessAccountDTO businessAccountDTO = new BusinessAccountDTO();
    businessAccountDTO.setCash(3d);
    businessAccountDTO.setCheck(4d);
    businessAccountDTO.setUnionpay(5d);
    businessAccountDTO.setTotal(12d);
    businessAccountDTO.setContent("BCGOGOGO");
    businessAccountDTO.setDept("it");
    businessAccountDTO.setBusinessCategory("ddd");
    businessAccountDTO.setDocNo("nbm");
    businessAccountDTO.setShopId(0l);
    businessAccountDTO.setStatus("save");
    businessAccountDTO.setPerson("gggg");
    businessAccountDTO.setMoneyCategory(MoneyCategory.income);
    return businessAccountDTO;

  }


}
