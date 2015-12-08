package com.bcgogo.user.service;


import com.bcgogo.base.BaseDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopAuditLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.enums.user.MenuType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.ContactGroupDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.permission.Menu;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.model.task.CusOrSupOrderIndexSchedule;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/12/11
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */

public interface IUserService {
  public String getNameByUserId(Long userId);

  public String getNameBySupplierId(Long supplierId);

  public UserDTO createUser(UserDTO userDTO) throws BcgogoException;

  public UserDTO updateUser(UserDTO userDTO) throws BcgogoException;

  public List<UserDTO> getUserByFuzzyUserNo(String userNo, int maxResults) throws Exception;

  public String generatePassword();

  public UserDTO getSystemCreatedUser(Long shopId);

  public UserDTO getUserByUserId(Long userId);

  List<User> getUser(Long shopId);

  public UserDTO getUserByUserInfo(String userInfo);

  public List<UserDTO> getShopUser(long shopId);

  public List<UserDTO> getUserByMobile(String mobile);

  public List<UserDTO> getUserByShopIDAndMobile(Long shopId, String mobile);


  //--methods related relationship between Group and Role---------------------------------------
  public void assignRoleToGroup(long roleId, long userGroupId);

  //--methods related Vehicle----------------------------------------------
  public VehicleDTO createVehicle(VehicleDTO vehicleDTO) throws BcgogoException;

  public VehicleDTO updateVehicle(VehicleDTO vehicleDTO) throws BcgogoException;

  public VehicleDTO getVehicleById(long vehicleId);

  public List<VehicleDTO> getVehicleByIds(Long shopId,Long... vehicleId);

  public List<VehicleDTO> getVehicleByMobile(long shopId, String mobile);

  List<VehicleDTO> getCompleteVehicleByMobile(long shopId, String mobile);

  public List<VehicleDTO> getVehicleByLicenceNo(Long shopId, String licenceNo);

  //--methods related Customer----------------------------------------------
  public CustomerDTO createCustomer(CustomerDTO customerDTO) throws BcgogoException;

  public CustomerDTO createCustomer(UserWriter writer, CustomerDTO customerDTO) throws BcgogoException;

  public CustomerDTO updateCustomer(CustomerDTO customerDTO) throws BcgogoException;

  List<ContactGroupDTO> getContactGroupByIds(Long[] groupIds);

  List<ContactGroupDTO> getContactGroup();

  List<Contact> getCustomerSupplierContactByMobile(Long shopId,String mobile);

  Long saveOrUpdateContact(ContactDTO contactDTO);

  List<String> getMobilesFromSmsDTO(Long shopId,SmsDTO smsDTO) throws Exception;

  List<String> filterGetMobilesFromSmsDTO(Long shopId,SmsDTO smsDTO) throws Exception;



  public CustomerDTO updateCustomer(UserWriter writer, CustomerDTO customerDTO) throws BcgogoException;

  public CustomerDTO getCustomerById(Long customerId);

  List<CustomerDTO> getCustomerDTOByIds(Long shopId,Long... customerIds);

  void setCustomerContacts(Long customerId, UserWriter writer, CustomerDTO customerDTO);

  public List<CustomerDTO> getShopCustomerById(long shopId, long customerId);

  public List<CustomerDTO> getCustomerByName(long shopId, String name);

  public List<CustomerDTO> getCustomerByMobile(long shopId, String mobile);

  public List<CustomerDTO> getCustomerByMobile2(long shopId, String mobile);

  public List<CustomerDTO> getCustomerByVehicleMobile(long shopId, String mobile);

  List<CustomerDTO> getCompleteCustomerByMobile(long shopId, String mobile);

  public List<CustomerDTO> getCustomerByLicenceNo(long shopId, String licenceNo);

  public List<CarDTO> getVehiclesByCustomerId(Long shopId, Long customerId);

  public CustomerRecord getShopCustomerRecordByCustomerId(Long shopId, Long customerId);

  public long countShopCustomer(long shopId);

  public int countShopCustomerByKey(long shopId, String key);    //lijie 2012-1-9

  public List<Customer> getShopCustomerByKey(long shopId, String key);

  public long countShopCustomerRecord(long customerId);             //lijie 2011-12-29

  public long countShopArrearsCustomerRecord(long shopId);      //lijie 2011-12-29

  //--methods related relationship between Customer and Vehicle -----------------
  public void addVehicleToCustomer(long vehicleId, long customerId);

  public List<CustomerVehicleDTO> getCustomerVehicleByVehicleId(long vehicleId);

  List<CustomerDTO> getCustomerByVehicleNo(String vehicleNo);

  public CustomerDTO getCustomerInfoByVehicleId(Long shopId, Long vehicleId);

  public Map<Long,CustomerDTO> getVehicleIdCustomerMapByVehicleIds(Long shopId, Set<Long> vehicleIds);

  public List<CustomerVehicleDTO> getVehicleByCustomerId(long customerId);

  //--methods related Supplier----------------------------------------------

  public SupplierDTO createSupplier(SupplierDTO supplierDTO) throws BcgogoException;
  public SupplierDTO createSupplier(UserWriter writer, SupplierDTO supplierDTO) throws BcgogoException;

  public SupplierDTO updateSupplier(SupplierDTO supplierDTO) throws BcgogoException;
  public SupplierDTO updateSupplier(UserWriter writer, SupplierDTO supplierDTO) throws BcgogoException;

  public SupplierDTO updateSupplier(SupplierDTO supplierDTO, Long orderId, OrderTypes orderType,
                                    String products, Double amount) throws BcgogoException;

  public SupplierDTO getSupplierById(long supplierId);

  List<SupplierDTO> getSupplierDTOByIds(Long shopId, Long... supplierIds);

  public List<SupplierDTO> getSupplierById(Long shopId, long supplierId);

  public List<SupplierDTO> getSupplierByName(long shopId, String name);

  public List<BaseDTO> getSupplierByMatchedName(Long shopId,String name);

  public List<SupplierDTO> getSupplierByNameAndShopId(long shopId, String name);//zhangchuanlong

  public SupplierDTO getSpecialSupplierByName(long shopId, String name);

  public List<SupplierDTO> getSupplierByMobile(long shopId, String mobile);

  public List<SupplierDTO> getSupplierByMobile2(long shopId, String mobile);

  public List<SupplierDTO> getSuppliersByKey(long shopId, String key, int pageNo, int pageSize);

  public List<SupplierDTO> getShopSupplier(long shopId, int pageNo, int pageSize);

  public int countShopSupplier(long shopId);   //lijie 2011-12-30

  public int countShopSupplierByKey(long shopId, String key);   //lijie 2012-1-9

  public List<Supplier> getShopSupplierByKey(long shopId, String key);

  //--methods related CustomerRecord----------------------------------------------

  public CustomerRecordDTO createCustomerRecord(CustomerRecordDTO customerRecordDTO) throws BcgogoException;

  CustomerRecordDTO createCustomerRecord(CustomerRecordDTO customerRecordDTO,UserWriter writer) throws BcgogoException;

  public CustomerRecordDTO updateCustomerRecord(CustomerRecordDTO customerRecordDTO) throws Exception;

  public List<CustomerRecordDTO> getCustomerRecordByCustomerId(long customerId);

  public List<CustomerRecordDTO> getCustomerRecordByName(String name);

  public List<CustomerRecordDTO> getShopArrearsCustomerRecord(long shopId, int pageNo, int pageSize);   //lijie 2011-12-29

  public int countShopArrearsCustomerRecord1(long shopId);

  public double getShopTotalArrears(long shopId);  //lijie 2011-12-29

  public List<CustomerRecordDTO> getShopCustomerRecord(long shopId, int pageNo, int pageSize);

  public List<CustomerRecordDTO> getShopCustomerRecordDTO(long shopId, int pageNo, int pageSize);

  public List<CustomerRecordDTO> getShopCustomerRecordByName(long shopId, String name);

  public List<CustomerRecordDTO> getShopCustomerRecordByMobile(long shopId, String mobile);

  public List<CustomerRecordDTO> getShopCustomerRecordByLicenceNo(long shopId, String licenceNo);

  public List<CustomerRecordDTO> getShopCustomerRecord(long shopId, String name, String licenceNo);

  public List<CustomerRecordDTO> getShopCustomerRecordByMobile(long shopId, String name, String mobile);

  //--methods related CustomerServiceJob----------------------------------------------
  public List<CustomerServiceJobDTO> getCustomerServiceJobByCustomerIdAndVehicleId(Long shopId, Long customerId, Long vehicleId);//邵磊

  public List<CustomerServiceJobDTO> getCustomerServiceJobByStateAndPageNoAndPageSize(long shopId, List<String> status, Long remindTime, int pageNo, int pageSize) throws Exception; //邵磊

  public List<CustomerServiceJobDTO> getCustomerServiceJobByCustomerIdAndRemindType(long customerId, long remindType);

  public int countCustomerServiceJobByShopId(Long shopId, long remindTime, List<String> status);

  //--methods related CustomerCard----------------------------------------------

  public CustomerCardDTO createCustomerCard(CustomerCardDTO customerCardDTO) throws BcgogoException;

  public CustomerCardDTO updateCustomerCard(CustomerCardDTO customerCardDTO) throws BcgogoException;


  public List<CustomerCardDTO> getCustomerCardByCustomerIdAndCardType(long shopId, long customerId, long cardType);

  @Deprecated
  public Customer getCustomerByCustomerId(Long customerId);

  public Customer getCustomerByCustomerId(Long customerId, Long shopId);

  public CustomerDTO getCustomerDTOByCustomerId(Long customerId, Long shopId);

  public UserVercodeDTO createUserVercode(String userno, String vercode);

  public UserVercode getUserVercodeByUserNo(String userno);

  //客户智能搜索匹配    (汉字)        zhanghcuanlong
  public List<CustomerDTO> getCustomer(String keyword, Long shopId);

  //供应商智能匹配        （汉字）     zhangchuanlong
  public List<SupplierDTO> getSupplier(String keyword, Long shopId);

  //客户智能搜索匹配   (字母)         zhanghcuanlong
  public List<CustomerDTO> getCustomerByZiMu(String keyword, Long shopId);

  // 供应商智能匹配  （字母）              zhangchuanlong
  public List<SupplierDTO> getSupplierByZiMu(String keyword, Long shopId);

  public List<CustomerDTO> getCustomerByTelephone(Long shopId, String telephone);

  public List<SupplierDTO> getSupplierByTelephone(Long shopId, String telephone);

  public List<CustomerCardDTO> getCustomerCardByCustomerIdsAndCardType(Long shopId, List<Long> customerIds, Long cardType);

  public List<CustomerRecordDTO> getSmsCustomerInfoList(long shopId, int currentPage, int pageSize) throws BcgogoException;

  //crateUser，UserGrop and GroupRole

//  public void activateShopUserUseGroup(ShopDTO shopDTO, UserDTO userDTO) throws BcgogoException;

  public boolean batchCreateCustomerAndVehicle(List<CustomerWithVehicleDTO> customerWithVehicleDTOList)  throws BcgogoException;

  List<ContactDTO> getContactDTOByIds(Long shopId,Long[] contactIds);

  ContactDTO getContactDTOById(Long shopId,Long contactId);

  Contact getContactById(Long contactId);

  @Deprecated
  public void saveOrUpdateCarsWithCustomerId(Long customerId, Long shopId, CarDTO[] vehicles) throws BcgogoException;

  //更新或者保存客户的车辆信息
  public List<VehicleDTO> saveOrUpdateCustomerVehicles(Long customerId, Long shopId, Long userId, List<VehicleDTO> vehicleDTOs) throws BcgogoException;

  public List<VehicleDTO> saveOrUpdateCustomerVehicles(Long customerId, Long shopId, Long userId, List<VehicleDTO> vehicleDTOs, UserWriter writer) throws BcgogoException;

  CustomerVehicleDTO addYuyueToCustomerVehicle(AppointServiceDTO appointServiceDTO) throws Exception;

  public List<CustomerDTO> getCustomerByBirth(Long birth);

  public void saveCustomerBirthdayRemind(List<CustomerServiceJobDTO> customerServiceJobDTOList);

  public void deleteCustomerBirthdayRemind(List<CustomerServiceJob> customerServiceJobList);

  public List<CustomerServiceJob> getCustomerServiceJobByRemindType(Long remindType);

  public void dropCustomerRemind(Long shopId, Long id);

  public void updateCustomerRemind(Long shopId, Long id);

  public CustomerRecord getRecordByCustomerId(Long customerId);

  public void updateCustomerRecord(CustomerRecord customerRecord);

  public void updateCustomerRecordByMigration(CustomerRecordDTO customerRecordDTO);

  /**
   * 对当天或昨天或当月新增客户数 根据属性进行排序
   *
   * @param shopId
   * @param timeType    //时间类型：当天，昨天，当月
   * @param page
   * @param orderByName 属性名称
   * @param orderByType 排序方式
   * @return
   */
  List<CustomerRecordDTO> getShopCustomerRecordDTO(Long shopId, String timeType, Pager page, String orderByName, String orderByType);

  //add by weilf 2012-08-16
  public void deleteCustomerLicenceNo(Long shopId,Long vehicleId);

  /**
   *  根据shop_id 员工状态 分页 读取数据
   *
   * @param shopId
   * @param salesManStatus
   * @param pager
   * @return
   */
  public List<SalesManDTO> getSalesManDTOListByShopId(long shopId, SalesManStatus salesManStatus, Pager pager);

  SalesManDTO saveOrUpdateSalesMan(SalesManDTO salesManDTO, UserWriter writer) throws Exception;

	/**
	 * 保存或者更新数据
   *
	 * @param salesManDTO
	 * @return
	 */
	public SalesManDTO saveOrUpdateSalesMan(SalesManDTO salesManDTO) throws Exception;

  /**
   * 根据员工状态获得当前状态的员工总数
   *
   * @param shopId
   * @param salesManStatus
   * @return
   */
  public int countSalesManByShopIdAndStatus(long shopId, SalesManStatus salesManStatus);

  /**
   *  根据员工id获得该员工
   *
   * @param salesManId
   * @return
   */
  public SalesManDTO getSalesManDTOById(long salesManId);

  /**
   * 根据员工姓名 或者 编号 shop_id获得员工信息
   *
   * @param salesManCode
   * @param Name
   * @param shopId
   * @return
   */
  public List<SalesManDTO> getSalesManDTOByCodeOrName(String salesManCode, String Name, long shopId);

  public List<User> getAllUser() throws Exception;

  public String changeMemberPassword(Long memberId, String oldPw, String newPw) throws Exception;

  public SalesManDTO[] getSalesManList(Long shopId) throws Exception;

  public void rollBackMemberInfo(Long shopId, String accountMemberNo, Double memberAmount) throws Exception;

  public List<User> getAllShopUser() throws Exception;

  public CustomerDTO getCustomerWithMemberByMemberNoShopId(String memberNo, Long shopId) throws Exception;


  /**
   * 查询当天新增客户【车辆】历史记录数量
   *
   * @param shopId
   * @param vehicle
   * @param services
   * @param itemName
   * @param endDateLong
   * @param endDateLong2
   * @return
   * @author zhangchuanlong
   */
  public int countRepairOrderHistoryByNewVehicle(Long shopId, String vehicle, String services, String itemName, Long endDateLong, Long endDateLong2) throws ParseException;

  /**
   * 查询当天新增客户【车辆】历史记录
   *
   * @param shopId
   * @param vehicle       车牌号
   * @param services      施工项目
   * @param itemName      材料品名
   * @param startDateTime
   * @param endDateTime
   * @param pager
   * @return
   * @author zhangchuanlong
   */
  public List<ItemIndexDTO> getRepairOrderHistoryByNewVehicle(Long shopId, String vehicle, String services, String itemName, Long startDateTime, Long endDateTime, Pager pager) throws Exception;

  List<CustomerRecordDTO> getShopCustomerRecordDTOBySolrSearchResult(List<CustomerSupplierSearchResultDTO> customerSuppliers);

  void batchUpdateUserPassword(List<UserDTO> users);
  public List<Vehicle> deleteVehicle(Long shopId,List<CustomerVehicleDTO> customerVehicleDTOList);

  /**
   * 根据shop_id 会员id 和会员消费 在作废时回退会员消费金额
   * @param shopId
   * @param memberId
   * @param memberAmount
   * @throws Exception
   */
  public void rollBackMemberInfo(Long shopId, Long memberId, Double memberAmount) throws Exception;

  /**
   * 获取必要的店铺的用户组操作权限开关
   * @param shopId
   * @throws Exception
   */
  public List<UserLimitDTO> getUserNecessaryLimitTags(Long shopId);

  public UserSwitchDTO getUserSwitchByShopIdAndScene(Long shopId,String scene);

  public List<UserSwitchDTO> getUserSwitchListByShopId(Long shopId);

  Menu getMenu(MenuType m);

  List<UserSwitch> getUserSwitch(long shopId);
  public void addUserSwitch(Long shopId,UserLimitDTO userLimitDTO,Boolean isWholesalerShopVersion);

  public UserSwitchDTO saveOrUpdateUserSwitch(Long shopId,String scene,String status);

  public boolean isMobileSwitchOn(Long shopId);

  public boolean isRepairPickingSwitchOn(Long shopId);

  public List<BaseDTO> getCustomerByMatchedName(long shopId, String name);


  Map<Long,SalesManDTO> getSalesManByIdSet(Long shopId, Set<Long> salesManIds);

  /**
   * 包含 被删除的客户
   * @param shopId
   * @param name
   * @return
   */
  List<CustomerDTO> getAllCustomerByName(long shopId, String name);

  public List<Long> getCustomerIdByLicenceNo( long shopId, String licenceNo);

  public CustomerRecordDTO getCustomerRecordDTOByCustomerIdAndShopId(Long shopId,Long customerId);

  CustomerDTO getCustomerByCustomerShopIdAndShopId(Long shopId,Long customerShopId);

  //通过客户名称模糊匹配出全部ID
  public List<Long> getCustomerIdsByNameWithFuzzyQuery(Long shopId,String customerName);

  //通过供应商名称模糊匹配全部ID
  public List<Long> getSupplierIdsByNameWithFuzzyQuery(Long shopId,String supplierName);

  //获得本店全部关联供应商
  public List<SupplierDTO> getRelatedSuppliersByShopId(Long shopId);

  Set<Long> getRelatedSuppliersIdsByShopId(Long shopId);

  //获得本店全部关联供应商的ID
  public List<Long> getRelatedSupplierIdListByShopId(Long shopId);

  public SalesMan getSalesManByName(Long shopId,String name);

  public Map<String,SalesManDTO> getSalesManDTOMap(Long shopId,Set<String> names);

  //add by weilingfeng
  public List<CustomerServiceJobDTO> getCustomerServiceRemindByCondition(long shopId, Boolean isOverdue, Boolean hasRemind, Long currentTime, int pageNo, int pageSize) throws Exception;

  public int countCustomerServiceRemindByCondition(long shopId, Boolean isOverdue, Boolean hasRemind, Long currentTime) throws Exception;

  Long saveOrUpdateApointServices(AppointServiceDTO appointServiceDTOs) throws BcgogoException, ParseException;

  List<AppointServiceDTO> getAppointServiceByCustomerVehicle(Long shopId,Long vehicleId,Long customerId);

  List<InsuranceCompanyDTO> getAllInsuranceCompanyDTOs();

  InsuranceCompany getInsuranceCompanyDTOById(Long id);

  List<CustomerVehicleDTO> getCustomerVehicleDTO(Long... vehicleIds);

  CustomerVehicleDTO getCustomerVehicleDTOByVehicleIdAndCustomerId(Long vehicleId, Long customerId);

  public Map<Long, MemberDTO> getMemberByIds(Long shopId, Set<Long> orderId);


  public List<CustomerServiceJob> getAllCustomerServiceJob();

  public CustomerServiceJobDTO getCustomerServiceJobById(Long id);

  public AppointServiceDTO getAppointServiceById(Long id);

   //在supplierShopDTO中创建一个customer
  CustomerDTO createRelationCustomer(ShopDTO supplierShopDTO, ShopDTO customerShopDTO, RelationTypes relationType);

  public Map<String,VehicleDTO> getVehicleDTOMap(Long shopId);

  public Map<String,MemberDTO> getMemberMap(Long shopId);

  public List<UserDTO> getAllUserByShopId(Long shopId);

  public void saveUserSwitch(Long shopId, String scene,String status);

  SupplierDTO getSupplierDTOBySupplierShopIdAndShopId(Long shopId, Long supplierShopId);
  public List<DepartmentDTO> getAllDepartmentsByShopId(Long shopId);

  //多联系人初始化bugFix
  void initContactBugFix();

  /**
   * 关联成功后创建客户和供应商的经营范围
   * @param customerDTO
   * @param supplierDTO
   */
  public void createCustomerSupplierBusinessScope(CustomerDTO customerDTO,SupplierDTO supplierDTO);

  /**
   * 更新客户或者供应商的经营范围
   * @param customerDTO
   * @param supplierDTO
   */
  public void updateCustomerSupplierBusinessScope(CustomerDTO customerDTO,SupplierDTO supplierDTO);

  /**
   * 保存或者更改客户或者供应商的经营范围（只保存三级分类）
   * @param customerDTO
   * @param supplierDTO
   * @param thirdCategoryIdStr
   */
  public void saveOrUpdateCustomerSupplierBusinessScope(CustomerDTO customerDTO, SupplierDTO supplierDTO, String thirdCategoryIdStr);


  /**
   * 根据客户或者供应商的id获取经营范围
   * @param shopId
   * @param customerId
   * @param supplierId
   * @return
   */
  public List<BusinessScopeDTO> getCustomerSupplierBusinessScope(Long shopId, Long customerId, Long supplierId);


    /**
   * 根据客户或者供应商的id获取经营范围
   * @param shopId
   * @param customerIdSet
   * @param supplierIdSet
   * @return
   */
  public List<BusinessScopeDTO> getCustomerSupplierBusinessScope(Long shopId, Set<Long> customerIdSet, Set<Long> supplierIdSet);

  /**
   * 重置系统账号的密码
   * @param shopId 店铺Id
   * @return Result{}
   */
  Result resetSystemCreatedPassword(Long shopId) throws Exception;

  /**
   * 重置系统账号的密码
   * @param shopId 店铺Id
   * @param managerUserNo 系统创建用户
   * @return Result{}
   */
  Result changeSystemCreatedUserNo(Long shopId, String managerUserNo) throws Exception;
  /**
   * 根据车牌号得到车辆信息
   * @param shopId
   * @param licenceNos
   * @return Map, 车牌号为key, VehicelDTO为Value
   */
  Map<String,VehicleDTO> getVehicleMapByLicenceNos(Long shopId, Set<String> licenceNos);

  void deleteCustomerVehicles(Long shopId, Set<Long> toDeleteVehicleIds);

  /**
   * 根据传入的CarDTOs（通常在更多客户信息中），与DB相比较，将不存在的Vehicle删除。
   * @param shopId
   * @param customerId
   * @param carDTOs
   */
  void deleteVehiclesByCarDTOs(Long shopId, Long customerId, CarDTO[] carDTOs);

  //当客户或者供应商改变区域时，插入一条schedule任务
  void saveCusOrSupOrderIndexSchedule(CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO);

  List<CusOrSupOrderIndexSchedule> getCusOrSupOrderIndexScheduleDTOByCusOrSupId(Long shopId, Long customerId, Long supplierId);//得到该客户或者供应商下是否有READY状态的任务

  void generateCustomerOrderIndexScheduleDTO(Long shopId,CustomerDTO customerDTO);

  void generateSupplierOrderIndexScheduleDTO(Long shopId,SupplierDTO supplierDTO);

  List<CusOrSupOrderIndexScheduleDTO> getCusOrSupOrderIndexScheduleDTOs();  //得到所有的READY状态的任务计划

  void updateCusOrSupOrderIndexScheduleStatusById(Long id, ExeStatus exeStatus);  //更新状态

  public int countSupplierBySupplierShopId(Long supplierShopId);

  public void saveCustomerServiceCategoryRelation(Long shopId,CustomerDTO customerDTO);

  public void saveCustomerVehicleBrandModelRelation(Long shopId,CustomerDTO customerDTO,Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap);

  public boolean isOBDCustomer(Long customerId);

  public Map<Long,Boolean> isOBDCustomer(List<Long> customerId);

  void saveCustomerVehicleBrandModelRelation(UserWriter writer, Long shopId, CustomerDTO customerDTO, Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap);

  List<ShopAuditLogDTO> getShopAuditLogDTOListByShopIdAndStatus(Long shopId, AuditStatus auditStatus);

  public List<SalesManDTO> getSalesManByDepartmentId(Long shopId,Long departmentId);

  public DepartmentDTO getDepartmentById(Long departmentId);

  public List<DepartmentDTO> getDepartmentNameByShopIdName(Long shopId,String name);

  public Map<Long,ContactDTO> getContactDTOMapByIdFormContactVehicle(Long shopId,Long... id);

  List<ContactDTO> getContactDTOByIdFormContactVehicle(Long shopId, Long... ids);

  public Map<Long, Boolean> isAppUserByCustomerId(List<Long> customerIdList);

  boolean hasMobileDumplicated(Long shopId,SmsDTO smsDTO) throws Exception;

  public void saveOrUpdateCustomerVehicle(List<CustomerVehicleDTO> customerVehicleDTOs);

  public List<CustomerVehicleDTO> getCustomerVehicleDTOByCustomerId(Set<Long> customerIdSet);

  User getUserBySalesManId(Long shopId, Long salesManId);

  Long getProbableShopByFinger(String finger);

  Customer saveCustomer(CustomerDTO customerDTO);

  void saveCustomerVehicle(CustomerVehicle customerVehicle);

  Vehicle saveVehicle(VehicleDTO vehicleDTO);

  void saveOrUpdateAccidentSpecialist(AccidentSpecialistDTO... specialistDTOs) ;

  List<AccidentSpecialistDTO> getAccidentSpecialistByOpenId(Long shopId,String openId);

  void handleDeadLock();


}
