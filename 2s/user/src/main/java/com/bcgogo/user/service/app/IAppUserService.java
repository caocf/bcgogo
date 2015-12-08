package com.bcgogo.user.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.api.response.ApiGsmLoginResponse;
import com.bcgogo.api.response.ApiMirrorLoginResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.enums.app.AppUserCustomerMatchType;
import com.bcgogo.enums.app.AppUserShopVehicleStatus;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ObdUserVehicleStatus;
import com.bcgogo.etl.model.GsmVehicleInfo;
import com.bcgogo.mq.message.MQLoginMessageDTO;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.app.AppUser;
import com.bcgogo.user.model.app.AppUserConfig;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.model.app.AppVehicle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-8-22
 * Time: 上午10:42
 */
public interface IAppUserService {
  /**
   * 手机端用户注册
   *
   * @param registrationDTO RegistrationDTO
   * @return ApiResponse
   */
  Pair<ApiResponse, AppUserDTO> registerAppUser(RegistrationDTO registrationDTO);

  /**
   * 根据userNo、mobile获取手机端用户
   */
  public AppUserDTO getAppUserByUserNo(String appUserNo, String mobile);

  /**
   * 根据appUserId去获取appUser的信息
   */
  public AppUserDTO getAppUserDTOById(Long appUserId);

  /**
   * appUser修改个人资料
   *
   * @param appUserDTO
   * @return
   */
  Result updateAppUserInfo(AppUserDTO appUserDTO);

  /**
   * 保存用户反馈
   *
   * @param appUserFeedbackDTO
   */
  void saveAppUserFeedback(AppUserFeedbackDTO appUserFeedbackDTO);

  /**
   * 手机端用户更改密码
   *
   * @param appUserDTO
   */
  Result updatePassword(AppUserDTO appUserDTO);

  AppUserLoginInfoDTO getAppUserLoginInfoBySessionId(String sessionId);

  boolean updateAppUserLoginInfoSuccess(String sessionId, String newSessionId);

  ApiResponse login(LoginDTO loginDTO);

  AppConfig login(AppGuestLoginInfo loginDTO);

  ApiResponse messageCenterLogin(MQLoginMessageDTO loginMessageDTO);

  //获取appUserConfig
  Map<String,String> getAppUserConfig(String appUserNo,String defaultVehicleNo);



  ApiResponse logout(String appUserNo,AppUserType appUserType);

  void saveOrUpdateAppUserLoginInfo(AppUserLoginInfoDTO loginInfoDTO);

  void updateVehicle(String appUserNo, Double curMil);

  ApiResponse retrievePassword(String appUserNo) throws SmsException;

  /**
   * 根据手机端用户账号获得
   *
   * @param userNo
   * @return
   */
  List<AppVehicle> getAppVehicleByAppUserNo(String userNo);

  public List<AppVehicleDTO> getAppVehicleDTOByAppUserNo(String userNo);

  AppVehicleDTO getDefaultAppVehicleByAppUserNo(String userNo);

  List<AppVehicleDTO> getAppVehicleByAppUserNoAndVehicleNo(String userNo,String vehicleNo);

  Set<String> getAppVehicleNosByAppUserNo(String userNo);

  Long getCustomerIdInAppUserCustomer(Long shopId, String userNo, String mobile);

  List<Long> getCustomerIdInAppUserCustomer(String... userNo);

  List<CustomerDTO> getCustomerByAppUserId(Long shopId,Long appUserId);

  AppUserLoginInfoDTO getAppUserLoginInfoByUserNo(String appUserNo,AppUserType appUserType);

  AppUserLoginInfoDTO getAppUserLoginInfoByMqSessionId(String mqSessionId);

  /**
   * 更新最后消费店铺
   *
   * @param shopId    最后消费 店铺ID
   * @param appUserNo app用户账号
   */
  void updateAppUserLastExpenseShopId(Long shopId, String appUserNo);

  Map<String, AppUserDTO> getAppUserMapByUserNo(Set<String> appUserNoSet);

  AppUserDTO getAppUserByUserNo(String appUserNo);

  Map<String, AppUserDTO> getAppUserMapByImeis(Set<String> imeis);

  //保养里程接近的车辆
  List<AppVehicleDTO> getMaintainMileageApproachingAppVehicle(Double[] intervals, int start, int limit, int remindTimesLimit);

  //保养时间到期的车辆
  List<AppVehicleDTO> getMaintainTimeApproachingAppVehicle(Long[] intervals, int start, int limit);

  //保险时间到期的车辆
  List<AppVehicleDTO> getInsuranceTimeApproachingAppVehicle(Long[] intervals, int start, int limit);

  //验车时间到期的车辆
  List<AppVehicleDTO> getExamineTimeApproachingAppVehicle(Long[] intervals, int start, int limit);

  List<AppUserCustomerDTO> getAppUserCustomersByCustomerIds(Long[] customerIds);

  List<AppUserCustomer> getAppUserCustomerByCustomerId(Long shopId, Long customerId);

  Long getDefaultOBDSellerShopId(String appUserNo);

  Long getOBDSellerShopIdByVehicleId(Long vehicleId);

  Map<Long, AppUserDTO> getAppUserInfoListFromCustomer(Long shopId, String keyword);

  List<AppUserCustomerDTO> getAppUserCustomerByAppUserNoAndShopId(String appUserNo, Long shopId);

  public List<AppUserCustomerDTO> getAppUserCustomerByAppUserNo(String appUserNo);

  Map<String,List<AppUserCustomerDTO>> getAppUserCustomerMapByAppUserNosAndShopId(Set<String> appUserNos,Long shopId);

  Map<Long,CustomerDTO> getCustomerDTOMapByObdIds(Long shopId,Set<Long> obdIds);

  Map<Long,VehicleDTO> getVehicleDTOMapByObdIds(Long shopId,Set<Long> obdIds);

  ApiResponse validateUpdateAppUserConfigByAppUser(AppUserConfigUpdateRequest appUserConfigUpdateRequest);

  ApiResponse updateAppUserConfig(AppUserConfigUpdateRequest appUserConfigUpdateRequest);

  Result gsmAllocateAppUser(GSMRegisterDTO gsmRegisterDTO);

  public List gsmRegisterAppUser(GSMRegisterDTO gsmRegisterDTO);

  public ApiGsmLoginResponse validateGsmRegister(GSMRegisterDTO gsmRegisterDTO);

  public ApiGsmLoginResponse gsmLogin(LoginDTO loginDTO);

  ApiMirrorLoginResponse platLogin(LoginDTO loginDTO);

  AppUserDTO getAppUserByImei(String imei,AppUserType userType);

  List<AppUserDTO> getAppUserByUserType(AppUserType appUserType,int start, int limit);

  AppVehicleDTO getAppVehicleDTOById(Long id);

  AppUserConfig getAppUserConfigByName(String appUserNo);

  void updateAppVehicleOilWearAndPosition(String appUserNo, String vehicleNo, double currentOilWear, double totalOilWear, String lat, String lon);

  void updateAppVehicleInfoByGsmVehicleInfo(AppVehicleDTO appVehicleDTO, List<GsmVehicleInfo> gsmVehicleInfoList);

  ApiResponse gsmRetrievePassword(String mobile) throws SmsException;

  AppUserConfigDTO saveOrUpdateAppUserConfig(AppUserConfigDTO appUserConfigDTO);

  Map<String,AppVehicleDTO> getAppVehicleMapByImeis(Set<String> imeis);

  Map<Long, Boolean> isAppUser(Long shopId,Long... contactIds);

  List<String> getAppUserNoByVehicleId(Long ...vehicleIds);

  List<String> getAppUserNoByContactId(Long ...contactIds);

  List<AppUserCustomerDTO> getAppUserCustomerDTOs(Long... customerIds);

  List<String> getAppUserNoByVehicleIdOrContactId(Long ...ids);

  void generateAppInfo(List<CustomerSupplierSearchResultDTO> customerSuppliers);

  void generateVehicleAppInfo(List<VehicleDTO> vehicleDTOList);

  List<AppUserCustomerDTO> getAppUserCustomerDTOsByShopVehicleIds(Set<Long> vehicleIds, Set<AppUserCustomerMatchType> matchTypes);

  AppUserCustomerDTO getAppUserCustomerDTOByAppUserNoAndAppVehicleId(String appUserNo,Long appVehicleId,AppUserCustomerMatchType matchType);

  public int countGsmAppVehicle();

  public List<AppVehicleDTO> getGsmAppVehicle(Pager pager);

  ApiResponse bcgogoAppLogin(LoginDTO loginDTO);

  boolean hasShopFaultRight(Long shopVersionId, Long userGroupId);

  boolean hasRemindTodoRight(Long shopVersionId, Long userGroupId);

  boolean hasAppointRight(Long shopVersionId, Long userGroupId);

  List<ObdUserVehicleDTO> getOBDUserVehicleByObdIds(Set<Long> obdIds);

  List<ObdUserVehicleDTO> getOBDUserVehicleByObdIds(Long obdId);

  AppUserShopVehicleDTO getAppUserShopVehicleDTO(Long vehicleId, Long obdId,AppUserShopVehicleStatus status);

  ObdUserVehicleDTO getObdUserVehicle(Long vehicleId, Long obdId,ObdUserVehicleStatus status);

  void saveOrUpdateAppUserShopVehicle(AppUserShopVehicleDTO appUserShopVehicleDTO);

  void saveOrUpdateObdUserVehicle(ObdUserVehicleDTO obdUserVehicleDTO);

  List<AppUserCustomerDTO> getAppUserCustomerByShopId(Long shopId);

  AppUserDTO getAppUserDTOByMobileUserType(String mobile, AppUserType appUserType);

  public Map<String,AppUser> getAppUserByUserId(Set<String> userIdSet);

  public Map<String,AppVehicle> getAppVehicleByUserId(Set<String> userIdSet);

  AppUser getAppUserByPhone (long phone);
}
