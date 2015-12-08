package com.bcgogo.txn.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.api.LoginDTO;
import com.bcgogo.api.RegistrationDTO;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.RandomUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-3
 * Time: 上午10:22
 */
public class AbstractPushMessageTest extends AbstractTest {
  protected Set<String> createAppUserAndVehicle(int number) {
    Set<String> userNos = new HashSet<String>();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    while (number > 0) {
      String mobile = "158" + RandomUtils.randomNumeric(8);
      if (userNos.add(mobile)) {
        number--;
        RegistrationDTO registrationDTO = new RegistrationDTO(mobile, "1", mobile, mobile);
        registrationDTO.setCurrentMileage(Double.valueOf(RandomUtils.randomNumeric(5)));
        registrationDTO.setVehicleNo("苏A" + RandomUtils.randomNumeric(5));
        if (number % 2 == 0) {
          registrationDTO.setNextMaintainMileage(registrationDTO.getCurrentMileage() + 50);
          registrationDTO.setNextMaintainTime(System.currentTimeMillis() - 172800000L + 3600000);
          registrationDTO.setNextExamineTime(System.currentTimeMillis() - 172800000L + 3600000);
          registrationDTO.setNextInsuranceTime(System.currentTimeMillis() - 172800000L + 3600000);
        } else {
          registrationDTO.setNextExamineTime(System.currentTimeMillis() + 86400000L);
          registrationDTO.setNextInsuranceTime(System.currentTimeMillis() + 86400000L);
          registrationDTO.setNextMaintainTime(System.currentTimeMillis() + 86400000L);
          registrationDTO.setNextMaintainMileage(registrationDTO.getCurrentMileage() - 50);
        }
        registerAppUser(appUserService, registrationDTO);
      }
    }
    return userNos;
  }

  protected void registerAppUser(IAppUserService appUserService, RegistrationDTO registrationDTO) {
    registrationDTO.setLoginInfo(new LoginDTO(AppPlatform.IOS, "1.0", "7.0.2", "640X960"));
    appUserService.registerAppUser(registrationDTO);
  }
}
