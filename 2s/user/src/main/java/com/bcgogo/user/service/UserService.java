package com.bcgogo.user.service;

import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.base.BaseDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.dto.ShopAuditLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.JuheViolateRegulationCitySearchCondition;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.service.IShopAuditLogService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.WashCardConstants;
import com.bcgogo.enums.*;
import com.bcgogo.enums.app.TaskType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.enums.user.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.notification.dto.ContactGroupDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.SystemMonitor.UserClientInfo;
import com.bcgogo.user.model.app.AppUserCustomerUpdateTask;
import com.bcgogo.user.model.permission.*;
import com.bcgogo.user.model.task.CusOrSupOrderIndexSchedule;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

/**
 * User: Xiao Jian
 * Date: 9/15/11
 * Time: 9:59 PM
 * todo notice  逐渐把 customer supplier user 分离出来 ，user的service先写在UserCacheService中.
 */
@Component
public class UserService implements IUserService {
  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  @Override
  public UserDTO createUser(UserDTO userDTO) throws BcgogoException {
    if (userDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();

    try {
      //passwrod
      if (userDTO.getPassword() == null)
        throw new BcgogoException(BcgogoExceptionType.EmptyPasswordNotAllowed);
      //mobile
      String mobile = userDTO.getMobile();
      if (mobile != null) {
        mobile = mobile.trim();
        if (!mobile.matches(User.VALID_MOBILE_REGEX))
          throw new BcgogoException(BcgogoExceptionType.InvalidMobileNumber);
      }
      //email
      String email = userDTO.getEmail();
      if (email != null) {
        email = email.trim();
        if (!email.matches(User.VALID_EMAIL_REGEX))
          throw new BcgogoException(BcgogoExceptionType.InvalidEmailAddress);
      }

      userDTO.setMobile(mobile);
      userDTO.setEmail(email);
      User user = new User(userDTO);
      writer.save(user);
      writer.commit(status);

      userDTO.setId(user.getId());

      return userDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public UserDTO updateUser(UserDTO userDTO) throws BcgogoException {
    if (userDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = userDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = userDTO.getId();
      if (id == null) throw new BcgogoException(BcgogoExceptionType.UserNotFound);
      User user = writer.getById(User.class, id);
      if (user == null) throw new BcgogoException(BcgogoExceptionType.UserNotFound);

      if (userDTO.getPassword() == null)
        throw new BcgogoException(BcgogoExceptionType.EmptyPasswordNotAllowed);

      String mobile = userDTO.getMobile();
      if (mobile != null) {
        mobile = mobile.trim();
        if (!mobile.matches(User.VALID_MOBILE_REGEX))
          throw new BcgogoException(BcgogoExceptionType.InvalidMobileNumber);
      }

      String email = userDTO.getEmail();
      if (email != null) {
        email = email.trim();
        if (!email.matches(User.VALID_EMAIL_REGEX))
          throw new BcgogoException(BcgogoExceptionType.InvalidEmailAddress);
      }
      userDTO.setMobile(mobile);
      userDTO.setEmail(email);

      user.fromDTO(userDTO);

      writer.save(user);
      writer.commit(status);

      return userDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public String generatePassword() {
    Random random = new Random();
    int pwd = random.nextInt(1000000) % (1000000 - 100000 + 1) + 100000;
    return String.valueOf(pwd);
  }

  @Override
  public UserDTO getSystemCreatedUser(Long shopId) {
    if (shopId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    User user = writer.getSystemCreatedUser(shopId);
    if (user == null) return null;
    return user.toDTO();
  }

  @Override
  public UserDTO getUserByUserId(Long userId) {
    if (userId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    User user = writer.getById(User.class, userId);
    if (user == null) return null;
    return user.toDTO();
  }

  @Override
  public List<User> getUser(Long shopId) {
    if (shopId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    return writer.getUser(shopId);
  }

  @Override
  public UserDTO getUserByUserInfo(String userInfo) {
    UserWriter writer = userDaoManager.getWriter();

    List<User> userList = writer.getUserByUserInfo(userInfo);

    if (CollectionUtils.isEmpty(userList)) return null;
    return userList.get(0).toDTO();
  }


  @Override
  public List<UserDTO> getShopUser(long shopId) {
    UserWriter writer = userDaoManager.getWriter();

    List<UserDTO> listUserDTO = new ArrayList<UserDTO>();
    for (User user : writer.getShopUser(shopId)) {
      listUserDTO.add(user.toDTO());
    }

    return listUserDTO;
  }

  @Override
  public List<UserDTO> getUserByMobile(String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    List<UserDTO> listUserDTO = new ArrayList<UserDTO>();
    for (User user : writer.getUserByMobile(mobile)) {
      listUserDTO.add(user.toDTO());
    }
    return listUserDTO;
  }

  public List<UserDTO> getUserByShopIDAndMobile(Long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    List<UserDTO> listUserDTO = new ArrayList<UserDTO>();
    for (User user : writer.getUserByShopIDAndMobile(shopId, mobile)) {
      listUserDTO.add(user.toDTO());
    }

    return listUserDTO;
  }


  /**
   * @param roleId
   * @param userGroupId
   * @deprecated
   */
  @Override
  public void assignRoleToGroup(long roleId, long userGroupId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGroupRole ugr = new UserGroupRole();
      ugr.setRoleId(roleId);
      ugr.setUserGroupId(userGroupId);
      writer.save(ugr);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public VehicleDTO createVehicle(VehicleDTO vehicleDTO) throws BcgogoException {
    if (vehicleDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);

    Long shopId = vehicleDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Vehicle vehicle = new Vehicle(vehicleDTO);
      writer.save(vehicle);
      writer.commit(status);
      vehicleDTO.setId(vehicle.getId());
      //做solr

      try {
        ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicle.getId());
      } catch (Exception e) {
        LOG.error("shopId:{}", shopId);
        LOG.error("vehicleId:{}", StringUtil.arrayToStr(",", vehicle.getId()));
        LOG.error("createVehicleSolrIndex 失败！", e);
      }

      return vehicleDTO;
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 更新车牌号
   *
   * @param vehicleDTO
   * @return
   * @throws BcgogoException
   */
  @Override
  public VehicleDTO updateVehicle(VehicleDTO vehicleDTO) throws BcgogoException {
    if (vehicleDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    if (vehicleDTO.getId() == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = vehicleDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Vehicle vehicle = writer.getById(Vehicle.class, vehicleDTO.getId());
      vehicle.fromDTO(vehicleDTO);
      writer.update(vehicle);
      writer.commit(status);
      vehicleDTO.setId(vehicle.getId());
      return vehicleDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public VehicleDTO getVehicleById(long vehicleId) {
    UserWriter writer = userDaoManager.getWriter();

    Vehicle vehicle = writer.getById(Vehicle.class, vehicleId);

    if (vehicle == null) return null;
    return vehicle.toDTO();
  }

  @Override
  public List<VehicleDTO> getVehicleByIds(Long shopId, Long... vehicleId) {
    if (shopId == null || ArrayUtil.isEmpty(vehicleId)) return null;
    UserWriter writer = userDaoManager.getWriter();
    List<Vehicle> vehicles = writer.getVehiclesByIds(shopId, vehicleId);
    List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();
    if (CollectionUtils.isNotEmpty(vehicles)) {
      for (Vehicle vehicle : vehicles) {
        if (vehicle != null) {
          vehicleDTOList.add(vehicle.toDTO());
        }
      }
    }
    return vehicleDTOList;
  }

  @Override
  public List<VehicleDTO> getVehicleByMobile(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    List<VehicleDTO> listVehicleDTO = new ArrayList<VehicleDTO>();
    Vehicle vehicle;
    for (CustomerDTO customerDTO : this.getCustomerByMobile(shopId, mobile)) {
      for (CustomerVehicle cv : writer.getVehicleByCustomerId(customerDTO.getId())) {
        vehicle = writer.getById(Vehicle.class, cv.getVehicleId());
        listVehicleDTO.add(vehicle.toDTO());
      }
    }
    return listVehicleDTO;
  }

  @Override
  public List<VehicleDTO> getCompleteVehicleByMobile(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    List<VehicleDTO> listVehicleDTO = new ArrayList<VehicleDTO>();
    Vehicle vehicle;
    List<CustomerDTO> customers = getCompleteCustomerByMobile(shopId, mobile);
    if (CollectionUtils.isNotEmpty(customers)) {
      for (CustomerDTO customerDTO : customers) {
        List<CustomerVehicle> customerVehicles = writer.getVehicleByCustomerId(customerDTO.getId());
        if (CollectionUtils.isNotEmpty(customerVehicles)) {
          for (CustomerVehicle cv : customerVehicles) {
            vehicle = writer.getById(Vehicle.class, cv.getVehicleId());
            listVehicleDTO.add(vehicle.toDTO());
          }
        }
      }
    }
    return listVehicleDTO;
  }

  @Override
  public List<VehicleDTO> getVehicleByLicenceNo(Long shopId, String licenceNo) {
    if (StringUtils.isBlank(licenceNo)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<VehicleDTO> listVehicleDTO = new ArrayList<VehicleDTO>();
    for (Vehicle vehicle : writer.getVehicleByLicenceNo(shopId, licenceNo)) {
      listVehicleDTO.add(vehicle.toDTO());
    }
    return listVehicleDTO;
  }

  public List<AppointServiceDTO> getAppointServiceByCustomerVehicle(Long shopId, Long vehicleId, Long customerId) {
    if (shopId == null || vehicleId == null || customerId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppointServiceDTO> appointServiceDTOs = new ArrayList<AppointServiceDTO>();
    for (AppointService appointService : writer.getAppointServiceByCustomerVehicle(shopId, vehicleId, customerId)) {
      appointServiceDTOs.add(appointService.toDTO());
    }
    return appointServiceDTOs;
  }

  @Override
  public CustomerDTO createCustomer(CustomerDTO customerDTO) throws BcgogoException {
    if (customerDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = customerDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      createCustomer(writer, customerDTO);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return customerDTO;
  }

  @Override
  public CustomerDTO createCustomer(UserWriter writer, CustomerDTO customerDTO) throws BcgogoException {
    if (customerDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = customerDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    Customer customer = new Customer(customerDTO);
    writer.save(customer);
    customerDTO.setId(customer.getId());
    // add by zhuj contacts不为空 保存contacts
    if (!ArrayUtils.isEmpty(customerDTO.getContacts())) {
      ContactDTO[] contactDTOs = customerDTO.getContacts();
      for (int i = 0; i < contactDTOs.length; i++) {
        if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) { // 数组可以存储null
          if (contactDTOs[i].getIsMainContact() == 1) { // 新增的时候主联系人写入customer表
            customer.setContact(contactDTOs[i].getName());
            customer.setMobile(contactDTOs[i].getMobile());
            customer.setEmail(contactDTOs[i].getEmail());
            customer.setQq(contactDTOs[i].getQq());
          }
        }
      }
      if (StringUtils.isNotBlank(customerDTO.getIdentity()) && StringUtils.equals(customerDTO.getIdentity(), "isSupplier")) { // 如果该客户既是供应商 则新增的时候不直接增加联系人 联系人信息的新增由外部统一控制
        // do nothing contacts add by independent method in contactService
      } else {
        for (int i = 0; i < contactDTOs.length; i++) {
          if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) { // 数组可以存储null
            contactDTOs[i].setCustomerId(customer.getId()); // 设置联系人的customerId
            contactDTOs[i].setDisabled(1); // 默认有效
            contactDTOs[i].setShopId(customerDTO.getShopId());
            Contact contact = new Contact();
            contact.fromDTO(contactDTOs[i]);
            writer.save(contact);
            contactDTOs[i].setId(contact.getId());
            if (contactDTOs[i].getIsMainContact() == 1) { // 新增的时候主联系人写入customer表
              customer.setContact(contactDTOs[i].getName());
              customer.setMobile(contactDTOs[i].getMobile());
              customer.setEmail(contactDTOs[i].getEmail());
              customer.setQq(contactDTOs[i].getQq());
            }
          }
        }
      }
    }
    //add by zhuj　新增完毕以后 设置联系人列表的customerId 以便supplierDTO 更新
    if (customerDTO.hasValidContact()) {
      for (ContactDTO contactDTO : customerDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          contactDTO.setCustomerId(customerDTO.getId());
        }
      }
    }
    return customerDTO;
  }

  /**
   * 批量新增客户车辆信息，所有数据放在一个事务中
   *
   * @param customerWithVehicleDTOList
   * @return
   */
  public boolean batchCreateCustomerAndVehicle(List<CustomerWithVehicleDTO> customerWithVehicleDTOList) throws BcgogoException {

    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try {
      Customer customer = null;
      Vehicle vehicle = null;
      CustomerVehicle customerVehicle = null;
      CustomerRecord customerRecord = null;
      Member member = null;
      Long shopId = null;
      for (CustomerWithVehicleDTO customerWithVehicleDTO : customerWithVehicleDTOList) {
        if (customerWithVehicleDTO == null || customerWithVehicleDTO.getCustomerDTO() == null || customerWithVehicleDTO.getCustomerRecordDTO() == null) {
          continue;
        }
        CustomerDTO customerDTO = customerWithVehicleDTO.getCustomerDTO();
        customer = new Customer(customerDTO);
        writer.save(customer);
        shopId = customer.getShopId();
        ContactDTO contactDTO = new ContactDTO(null, customerDTO.getContact(), customerDTO.getMobile(), customerDTO.getEmail(), customerDTO.getQq(),
          customer.getId(), null, shopId, 0, 1, 1, 0);
        Contact contact = new Contact().fromDTO(contactDTO);
        writer.save(contact);
        member = new Member(customerWithVehicleDTO.getMemberDTO());
        if (null != member.getMemberNo()) {
          if (null != membersService.getMemberByShopIdAndMemberNo(member.getShopId(), member.getMemberNo())) {
            LOG.warn("shopId{}", member.getShopId());
            LOG.warn("此会员已在数据库中存在，可能情况是上次导入的只有部分，这次又导入这部分内容{}", member.getMemberNo());
          } else {
            member.setCustomerId(customer.getId());
            if (null == member.getBalance()) {
              member.setBalance(Double.valueOf("0"));
            }
            writer.save(member);
          }
        }
        customerRecord = new CustomerRecord(customerWithVehicleDTO.getCustomerRecordDTO());
        customerRecord.setCustomerId(customer.getId());
        writer.save(customerRecord);
        List<VehicleDTO> vehicleDTOList = customerWithVehicleDTO.getVehicleDTOList();
        if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
          for (VehicleDTO vehicleDTO : vehicleDTOList) {
            if (null == vehicleDTO) {
              continue;
            }
            vehicle = new Vehicle(vehicleDTO);
            writer.save(vehicle);
            customerVehicle = new CustomerVehicle();
            customerVehicle.setCustomerId(customer.getId());
            customerVehicle.setVehicleId(vehicle.getId());
            writer.save(customerVehicle);


            if (StringUtil.isNotEmpty(customerWithVehicleDTO.getCustomerDTO().getMobile())) {
              AppUserCustomerUpdateTask appUserCustomerUpdateTask = new AppUserCustomerUpdateTask();
              appUserCustomerUpdateTask.setOperatorId(customer.getId());
              appUserCustomerUpdateTask.setOperatorType(OperatorType.CUSTOMER);
              appUserCustomerUpdateTask.setCreateTime(System.currentTimeMillis());
              appUserCustomerUpdateTask.setExeStatus(ExeStatus.READY);
              appUserCustomerUpdateTask.setTaskType(TaskType.WEB_IMPORT_CUSTOMER);
              writer.save(appUserCustomerUpdateTask);
            }

          }
        }
      }
      writer.commit(status);

      return true;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      throw new BcgogoException(e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ContactDTO> getContactDTOByIds(Long shopId, Long... contactIds) {
    if (shopId == null || ArrayUtil.isEmpty(contactIds)) return null;
    UserWriter writer = userDaoManager.getWriter();
    return writer.getContactDTOByIds(shopId, contactIds);
  }

  @Override
  public ContactDTO getContactDTOById(Long shopId, Long contactId) {
    return CollectionUtil.getFirst(getContactDTOByIds(shopId, contactId));
  }

  @Override
  public Contact getContactById(Long contactId) {
    if (contactId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    return writer.getById(Contact.class, contactId);
  }


  @Override
  public List<ContactGroupDTO> getContactGroupByIds(Long[] groupIds) {
    UserWriter writer = userDaoManager.getWriter();
    List<ContactGroup> contactGroups = writer.getContactGroupByIds(groupIds);
    List<ContactGroupDTO> contactGroupDTOs = new ArrayList<ContactGroupDTO>();
    if (CollectionUtil.isNotEmpty(contactGroups)) {
      for (ContactGroup contactGroup : contactGroups) {
        if (contactGroup == null) continue;
        contactGroupDTOs.add(contactGroup.toDTO());
      }
    }
    return contactGroupDTOs;
  }

  @Override
  public List<ContactGroupDTO> getContactGroup() {
    UserWriter writer = userDaoManager.getWriter();
    List<ContactGroup> contactGroups = writer.getAllContactGroup();
    List<ContactGroupDTO> contactGroupDTOs = new ArrayList<ContactGroupDTO>();
    if (CollectionUtil.isNotEmpty(contactGroups)) {
      for (ContactGroup group : contactGroups) {
        if (group == null || group.getContactGroupType() == null) {
          continue;
        }
        contactGroupDTOs.add(group.toDTO());
      }
    }
    return contactGroupDTOs;
  }

  @Override
  public CustomerDTO updateCustomer(CustomerDTO customerDTO) throws BcgogoException {
    if (customerDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = customerDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      updateCustomer(writer, customerDTO);
      writer.commit(status);
      return customerDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<Contact> getCustomerSupplierContactByMobile(Long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCustomerSupplierContactByMobile(shopId, mobile);
  }

  @Override
  public Long saveOrUpdateContact(ContactDTO contactDTO) {
    if (contactDTO == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {

      if (contactDTO.getId() == null) {
        Contact contact = new Contact(contactDTO);
        writer.save(contact);
        contactDTO.setId(contact.getId());
        writer.commit(status);
        return contact.getId();
      } else {
        Contact contact = getContactById(contactDTO.getId());
        if (contact == null) return null;
        contact.fromDTO(contactDTO);
        writer.update(contact);
        writer.commit(status);
        return contact.getId();
      }
    } finally {
      writer.rollback(status);
    }
  }

  public List<String> filterGetMobilesFromSmsDTO(Long shopId, SmsDTO smsDTO) throws Exception {
    List<String> mobiles = getMobilesFromSmsDTO(shopId, smsDTO);
    List<String> filterMobiles = new ArrayList<String>();
    for (String mobile : mobiles) {
      if (StringUtils.isEmpty(mobile) || filterMobiles.contains(mobile)) {
        continue;
      }
      filterMobiles.add(mobile);
    }
    return filterMobiles;
  }

  @Override
  public List<String> getMobilesFromSmsDTO(Long shopId, SmsDTO smsDTO) throws Exception {
    List<String> mobiles = new ArrayList<String>();
    if (StringUtil.isNotEmpty(smsDTO.getContactIds())) {
      String[] tContactIds = smsDTO.getContactIds().split(",");
      Set<Long> contactIds = new HashSet<Long>();
      for (String tContactId : tContactIds) {
        if (StringUtil.isEmpty(tContactId)) continue;
        Long contactId = null;
        if (tContactId.contains("_")) {
          contactId = NumberUtil.longValue(tContactId.split("_")[1]);
        } else {
          contactId = NumberUtil.longValue(tContactId);
        }
        if (contactIds.contains(contactId)) {
          continue;
        }
        contactIds.add(contactId);
      }
      List<ContactDTO> contactDTOs = ServiceManager.getService(IUserService.class).getContactDTOByIdFormContactVehicle(shopId, ArrayUtil.toLongArr(contactIds));
      if (CollectionUtil.isNotEmpty(contactDTOs)) {
        for (ContactDTO contactDTO : contactDTOs) {
          if (contactDTO == null) {
            continue;
          }
          mobiles.add(contactDTO.getMobile());
        }
      }
    }

    if (StringUtil.isNotEmpty(smsDTO.getContactGroupIds())) {
      Long[] contactGroupIds = ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
      smsDTO.setContactGroupDTOs(getContactGroupByIds(contactGroupIds));
    }
    if (CollectionUtil.isNotEmpty(smsDTO.getContactGroupDTOs())) {
      ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);
      CustomerSupplierSearchConditionDTO conditionDTO = new CustomerSupplierSearchConditionDTO();
      conditionDTO.setShopId(shopId);
      conditionDTO.setStart(0);
      conditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
      conditionDTO.setRows(Integer.MAX_VALUE);
      List<ContactGroupDTO> groupDTOs = smsDTO.getContactGroupDTOs();
      for (ContactGroupDTO groupDTO : groupDTOs) {
        conditionDTO.setContactGroupType(groupDTO.getContactGroupType());
        CustomerSupplierSearchResultListDTO resultListDTO = searchCustomerSupplierService.queryContact(conditionDTO);
        List<ContactDTO> contactDTOs = resultListDTO.getContactDTOList();
        if (CollectionUtil.isNotEmpty(contactDTOs)) {
          for (ContactDTO contactDTO : contactDTOs) {
            if (contactDTO == null) {
              continue;
            }
            mobiles.add(contactDTO.getMobile());
          }
        }
      }
    }
    return mobiles;
  }

  @Override
  public CustomerDTO updateCustomer(UserWriter writer, CustomerDTO customerDTO) throws BcgogoException {
    if (customerDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = customerDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    Long id = customerDTO.getId();
    if (id == null) throw new BcgogoException(BcgogoExceptionType.CustomerNotFound);
    Customer customer = writer.getById(Customer.class, id);
    if (customer == null) throw new BcgogoException(BcgogoExceptionType.CustomerNotFound);
    if (customerDTO.isMobileOnly()) {
      CustomerRecord customerRecord = null;
      customer.setMobile(customerDTO.getMobile());
      List<CustomerRecord> customerRecordList = writer.getCustomerRecordByCustomerId(customerDTO.getId());
      if (customerRecordList != null && customerRecordList.size() != 0) {
        customerRecord = customerRecordList.get(0);
        customerRecord.setMobile(customerDTO.getMobile());
        writer.save(customerRecord);
      }
    } else {
      customer.fromDTO(customerDTO);
    }

    updateContactFieldsInCustomer(customerDTO, customer); // add by zhuj 更新customer表里面的联系人信息
    writer.update(customer);

    if (StringUtils.isNotBlank(customerDTO.getIdentity()) && StringUtils.equals(customerDTO.getIdentity(), "isSupplier") || customerDTO.isCancel()) { // 如果该客户既是供应商 则新增的时候不直接增加联系人 联系人信息的新增由外部统一控制
      // 既是供应商又是客户的联系人处理 全部交由contactService处理
      //ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(customerDTO.getId(), null, shopId, customerDTO.getContacts());
    } else {
      updateCustomerContacts(writer, customerDTO, customer);
    }
    //add by zhuj　新增完毕以后 设置联系人列表的customerId 以便supplierDTO 更新
    if (customerDTO.hasValidContact()) {
      for (ContactDTO contactDTO : customerDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          contactDTO.setCustomerId(customerDTO.getId());
        }
      }
    }
    return customerDTO;
  }

  private void updateContactFieldsInCustomer(CustomerDTO customerDTO, Customer customer) {
    // customer　表里面contact信息
    if (!customerDTO.hasValidContact()) { // 如果页面没传入联系人信息 分为页面修改导致无信息和业务订单没填写联系人信息(这种情况目前不可能 页面入口封死 否则双重语义  必须新增加一个Flag标识符)

      customer.setContact(null);
      customer.setMobile(null);
      customer.setEmail(null);
      customer.setQq(null);
    } else {
      for (ContactDTO contactDTO : customerDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact() && contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
          customer.setContact(contactDTO.getName());
          customer.setMobile(contactDTO.getMobile());
          customer.setEmail(contactDTO.getEmail());
          customer.setQq(contactDTO.getQq());

          customerDTO.setContact(contactDTO.getName());
          customerDTO.setMobile(contactDTO.getMobile());
          customerDTO.setEmail(contactDTO.getEmail());
          customerDTO.setQq(contactDTO.getQq());
        }
      }
    }
  }

  private void updateContactFieldsInSupplier(SupplierDTO supplierDTO, Supplier supplier) {
    // customer　表里面contact信息
    if (!supplierDTO.hasValidContact()) { // 如果页面没传入联系人信息 分为页面修改导致无信息和业务订单没填写联系人信息(这种情况目前不可能 页面入口封死 否则双重语义  必须新增加一个Flag标识符)
      supplier.setContact(null);
      supplier.setMobile(null);
      supplier.setEmail(null);
      supplier.setQq(null);
    } else {
      for (ContactDTO contactDTO : supplierDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact() && contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
          supplier.setContact(contactDTO.getName());
          supplier.setMobile(contactDTO.getMobile());
          supplier.setEmail(contactDTO.getEmail());
          supplier.setQq(contactDTO.getQq());
        }
      }
    }
  }

  private void updateCustomerContacts(UserWriter writer, CustomerDTO customerDTO, Customer customer) {
    // add by zhuj 如果有联系人列表 更新联系人列表
    // 目前列表 与 DB列表 比较 差集新增(其实就是id不存在的集合)、删除（置为disabled） 交集更新
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(customerDTO.getId(), null, customerDTO.getShopId(), null, null);
    // 获取DB联系人id列表
    List<Long> dbIds = new ArrayList<Long>();
    if (!CollectionUtils.isEmpty(contactList)) {
      for (Contact contact : contactList) {
        if (contact.toDTO() != null && contact.toDTO().isValidContact()) {
          dbIds.add(contact.getId());
        }
      }
    }

    List<Long> toBeUpdatedIds = new ArrayList<Long>();
    if (!ArrayUtils.isEmpty(customerDTO.getContacts())) {
      for (ContactDTO contactDTO : customerDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          toBeUpdatedIds.add(contactDTO.getId());
        }
      }
    }

    if (customerDTO.isFromManagePage()) {
      // 对于db中存在 目前列表有效联系人列表中不存在的联系人 置为disabled
      for (Contact contact : contactList) {
        if (!toBeUpdatedIds.contains(contact.getId())) {
          contact.setDisabled(0);
          writer.update(contact);
        }
      }
    }

    if (!ArrayUtils.isEmpty(customerDTO.getContacts())) {
      // id在db中存在 更新
      for (ContactDTO contactDTO : customerDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact() && dbIds.contains(contactDTO.getId())) {
          contactDTO.setCustomerId(customer.getId());
          for (Contact contact : contactList) {
            if (contact.getId().equals(contactDTO.getId())) {
              contact.fromDTO(contactDTO);
              writer.update(contact);
            }
          }
        }
      }
    }
    // id不存在
    if (ArrayUtil.isNotEmpty(customerDTO.getContacts())) {
      for (ContactDTO contactDTO : customerDTO.getContacts()) {
        if (contactDTO != null && contactDTO.getId() == null && contactDTO.isValidContact()) {
          contactDTO.setDisabled(1);
          contactDTO.setShopId(customerDTO.getShopId());
          contactDTO.setCustomerId(customer.getId());
          Contact contact = new Contact();
          contact.fromDTO(contactDTO);
          writer.save(contact);
          contactDTO.setId(contact.getId());
        }
      }
    }

  }

  @Override
  public CustomerDTO getCustomerById(Long customerId) {
    if (customerId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    Customer customer = writer.getById(Customer.class, customerId);
    if (customer == null) return null;
    CustomerDTO customerDTO = customer.toDTO();
    setCustomerContacts(customerId, writer, customerDTO); // add by zhuj

    return customerDTO;
  }

  @Override
  public List<CustomerDTO> getCustomerDTOByIds(Long shopId, Long... customerIds) {
    if (shopId == null || ArrayUtil.isEmpty(customerIds)) return null;
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customers = writer.getCustomerByIds(shopId, customerIds);
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    if (CollectionUtil.isNotEmpty(customers)) {
      for (Customer customer : customers) {
        customerDTOs.add(customer.toDTO());
      }
    }
    return customerDTOs;
  }

  public void setCustomerContacts(Long customerId, UserWriter writer, CustomerDTO customerDTO) {
    // add by zhuj 联系人列表
    ContactDTO[] contactDTOs = new ContactDTO[3];
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(customerId, null, customerDTO.getShopId(), null, null);

    boolean hasMainContact = false;

    if (!CollectionUtils.isEmpty(contactList)) {
      int size = contactList.size();
      if (size > 3) {
        LOG.warn("customer's contactList size is over 3,customerId is " + customerId);
        size = 3;
      }
      for (int i = 0; i < size; i++) {
        if (contactList.get(i) != null) {
          ContactDTO contactDTO = contactList.get(i).toDTO();
          contactDTOs[i] = contactDTO;
          if (contactDTO.getIsMainContact() != null && NumberUtil.intValue(contactDTO.getIsMainContact()) == 1) {
            customerDTO.setContactId(contactDTO.getId());
            customerDTO.setContact(contactDTO.getName());
            customerDTO.setMobile(contactDTO.getMobile());
            customerDTO.setEmail(contactDTO.getEmail());
            customerDTO.setQq(contactDTO.getQq());
            hasMainContact = true;
          }
        }
      }
    }

//    if (!hasMainContact) {
//      if(contactDTOs[0] == null){
//        contactDTOs[0] = new ContactDTO();
//      }
//      contactDTOs[0].setIsMainContact(1);
//    }
    customerDTO.setContacts(contactDTOs);
  }

  public List<CustomerDTO> getShopCustomerById(long shopId, long customerId) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    for (Customer customer : writer.getShopCustomerById(shopId, customerId)) {
      CustomerDTO customerDTO = customer.toDTO();
      // 查询每个客户的联系人列表
      ContactDTO[] contactDTOs = new ContactDTO[3];
      List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(customer.getId(), null, shopId, null, null);
      if (!CollectionUtils.isEmpty(contactList)) {
        int size = contactList.size();
        if (size > 3) {
          LOG.warn("customer's contact size over 3,customerId is " + customer.getId());
          size = 3;
        }
        for (int i = 0; i < size; i++) {
          ContactDTO contactDTO = contactList.get(i).toDTO();
          contactDTOs[i] = contactDTO;
          if (contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) { // 主联系人
            customerDTO.setContactId(contactDTO.getId());
            customerDTO.setContact(contactDTO.getName());
            customerDTO.setEmail(contactDTO.getEmail());
            customerDTO.setMobile(contactDTO.getMobile());
            customerDTO.setQq(contactDTO.getQq());
          }
        }
        customerDTO.setContacts(contactDTOs);
      }
      listCustomerDTO.add(customerDTO);
    }
    return listCustomerDTO;
  }

  @Override
  public List<CustomerDTO> getCustomerByName(long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();

    List<Customer> customerList = writer.getCustomerByName(shopId, name);
    if (CollectionUtils.isEmpty(customerList)) {
      return null;
    }
    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    for (Customer customer : customerList) {
      CustomerDTO customerDTO = customer.toDTO();
      // add by zhuj 设置联系人信息
      setCustomerContacts(customerDTO.getId(), writer, customerDTO);
      listCustomerDTO.add(customerDTO);
    }

    return listCustomerDTO;
  }

  @Override
  public List<CustomerDTO> getAllCustomerByName(long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();

    List<Customer> customerList = writer.getAllCustomerByName(shopId, name);
    if (CollectionUtils.isEmpty(customerList)) {
      return null;
    }
    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    for (Customer customer : customerList) {
      CustomerDTO customerDTO = customer.toDTO();
      // add by zhuj 设置联系人信息
      setCustomerContacts(customerDTO.getId(), writer, customerDTO);
      listCustomerDTO.add(customerDTO);
    }

    return listCustomerDTO;
  }

  /**
   * 模糊匹配用户名
   *
   * @param shopId
   * @param name
   * @return
   */
  public List<BaseDTO> getCustomerByMatchedName(long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customerList = writer.getCustomerByMatchedName(shopId, name);
    if (CollectionUtils.isEmpty(customerList)) {
      return null;
    }
    List<BaseDTO> baseDTOs = new ArrayList<BaseDTO>();
    for (Customer customer : customerList) {
      CustomerDTO customerDTO = customer.toDTO();
      // add by zhuj 设置联系人信息
      setCustomerContacts(customerDTO.getId(), writer, customerDTO);
      baseDTOs.add(customerDTO);
    }
    return baseDTOs;
  }

  @Override
  public Map<Long, SalesManDTO> getSalesManByIdSet(Long shopId, Set<Long> salesManIds) {
    UserWriter writer = userDaoManager.getWriter();
    List<SalesMan> salesManList = writer.getSalesManListByIds(shopId, salesManIds);
    if (CollectionUtils.isEmpty(salesManList)) {
      return null;
    }
    Map<Long, SalesManDTO> salesManDTOMap = new HashMap<Long, SalesManDTO>();
    for (SalesMan salesMan : salesManList) {
      if (salesMan == null) {
        continue;
      }
      salesManDTOMap.put(salesMan.getId(), salesMan.toDTO());
    }
    return salesManDTOMap;
  }

  // TODO zhuj 返回值不用list。。。 目前兼容老代码返回list
  @Override
  public List<CustomerDTO> getCustomerByMobile(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    if (org.apache.commons.lang.StringUtils.isBlank(mobile)) {
      return null;
    }
    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    //modified by zhuj　通过手机号查询联系人信息
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, null, shopId, null, mobile);
    if (CollectionUtils.isNotEmpty(contactList)) {
      // 过滤掉customerId为空 或者 为0L的数据
      filterCustomerContactList(contactList);
      if (CollectionUtils.isNotEmpty(contactList)) {
        if (contactList.size() > 1) {
          LOG.warn("通过手机号[" + mobile + "],查询联系人不唯一!");
          //throw new RuntimeException("通过手机号[" + mobile + "],查询联系人不唯一!");
        }
        Contact contact = contactList.get(0);
        Long customerId = contact.getCustomerId();

        Customer customer = writer.getCustomerById(shopId, customerId);
        if (customer != null) {
          CustomerDTO customerDTO = customer.toDTO();
          ContactDTO[] contactDTOs = new ContactDTO[1];
          contactDTOs[0] = contact.toDTO();
          customerDTO.setContacts(contactDTOs);
          customerDTO.setContact(contact.getName());
          Long contactId = contact.getId();
          customerDTO.setContactId(contact.getId());
          customerDTO.setMobile(contact.getMobile());
          customerDTO.setEmail(contact.getEmail());
          customerDTO.setQq(contact.getQq());
          listCustomerDTO.add(customerDTO);
        }
      }

    }

    return listCustomerDTO;
  }

  @Override
  public List<CustomerDTO> getCustomerByMobile2(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    if (org.apache.commons.lang.StringUtils.isBlank(mobile)) {
      return null;
    }
    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, null, shopId, null, mobile);
    if (CollectionUtils.isNotEmpty(contactList)) {
      // 过滤掉customerId为空 或者 为0L的数据
      filterCustomerContactList(contactList);
      if (CollectionUtils.isNotEmpty(contactList)) {
        for (Contact contact : contactList) {
          Long customerId = contact.getCustomerId();

          Customer customer = writer.getCustomerById(shopId, customerId);
          if (customer != null) {
            CustomerDTO customerDTO = customer.toDTO();
            ContactDTO[] contactDTOs = new ContactDTO[1];
            contactDTOs[0] = contact.toDTO();
            customerDTO.setContacts(contactDTOs);
            customerDTO.setContact(contact.getName());
            customerDTO.setContactId(contact.getId());
            customerDTO.setMobile(contact.getMobile());
            customerDTO.setEmail(contact.getEmail());
            customerDTO.setQq(contact.getQq());
            listCustomerDTO.add(customerDTO);
          }
        }

      }

    }

    return listCustomerDTO;
  }


  private void filterCustomerContactList(List<Contact> contacts) {
    if (CollectionUtils.isEmpty(contacts)) {
      return;
    }
    Iterator<Contact> contactIterator = contacts.iterator();
    while (contactIterator.hasNext()) {
      Contact contact = contactIterator.next();
      if (contact.getCustomerId() == null || contact.getCustomerId() == 0L) {
        contactIterator.remove();
      }
    }
  }

  private void filterSupplierContactList(List<Contact> contacts) {
    if (CollectionUtils.isEmpty(contacts)) {
      return;
    }
    Iterator<Contact> contactIterator = contacts.iterator();
    while (contactIterator.hasNext()) {
      Contact contact = contactIterator.next();
      if (contact.getSupplierId() == null || contact.getSupplierId() == 0L) {
        contactIterator.remove();
      }
    }
  }

  ;

  @Override
  public List<CustomerDTO> getCustomerByVehicleMobile(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    if (org.apache.commons.lang.StringUtils.isBlank(mobile)) {
      return null;
    }
    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    List<CustomerVehicle> customerVehicles = writer.getCustomerByVehicleMobile(shopId, mobile);
    if (customerVehicles != null) {
      for (CustomerVehicle customerVehicle : customerVehicles) {
        Customer customer = writer.getCustomerById(shopId, customerVehicle.getCustomerId());
        if (customer != null) {
          CustomerDTO customerDTO = customer.toDTO();
          // add by zhuj 设置联系人信息
          setCustomerContacts(customerDTO.getId(), writer, customerDTO);
          listCustomerDTO.add(customerDTO);
        }
      }
    }
    return listCustomerDTO;
  }

  @Override
  public List<CustomerDTO> getCompleteCustomerByMobile(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    if (org.apache.commons.lang.StringUtils.isBlank(mobile)) {
      return null;
    }
    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    for (Customer customer : writer.getCompleteCustomerByMobile(shopId, mobile)) {
      listCustomerDTO.add(customer.toDTO());
    }

    return listCustomerDTO;
  }

  @Override
  public List<CustomerDTO> getCustomerByLicenceNo(long shopId, String licenceNo) {

    if (StringUtils.isBlank(licenceNo)) {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();

    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    Customer customer;

    for (VehicleDTO vehicleDTO : this.getVehicleByLicenceNo(shopId, licenceNo)) {
      for (CustomerVehicle cv : writer.getCustomerVehicleByVehicleId(vehicleDTO.getId())) {
        if (cv != null && cv.getCustomerId() != null) {
          customer = writer.getById(Customer.class, cv.getCustomerId());
          if (customer != null && customer.getStatus() != CustomerStatus.DISABLED) {
            CustomerDTO customerDTO = customer.toDTO();

            customerDTO.setIsObd((StringUtil.isNotEmpty(vehicleDTO.getGsmObdImei()) || vehicleDTO.getObdId() != null));

            setCustomerContacts(customerDTO.getId(), writer, customerDTO); // add by zhuj
            listCustomerDTO.add(customerDTO);
          }
        }
      }
    }

    return listCustomerDTO;
  }

  @Override
  public long countShopCustomer(long shopId) {
    UserWriter writer = userDaoManager.getWriter();

    return writer.countShopCustomer(shopId);
  }

  public int countShopCustomerByKey(long shopId, String key) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopCustomerByKey(shopId, key);
  }

  public List<Customer> getShopCustomerByKey(long shopId, String key) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getShopCustomerByKey(shopId, key);
  }

  public long countShopCustomerRecord(long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopCustomerRecord(shopId);
  }


  public long countShopArrearsCustomerRecord(long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopArrearsCustomerRecord(shopId);
  }

//  public List<String> getCustomersPhonesByShopId(ShopPlanDTO shopPlanDTO) {
//    UserWriter writer = userDaoManager.getWriter();
//    List<String> mobiles = null;
//    String receiveMobile = "";
//    //通过shopId得到所有moblies
//    if (shopPlanDTO.getCustomerType().equals("all")) {
//      mobiles = writer.getCustomersMobilesByShopId(shopPlanDTO.getShopId());
//    } else {
//      //通过customerId得到所有moblies
//      String customerIds = shopPlanDTO.getCustomerIds();
//      if (customerIds == null || "".equals(customerIds)) return null;
//      String[] customerIdsArray = customerIds.split(",");
//      List<String> customerIdsList = new ArrayList<String>();
//      for (String id : customerIdsArray) {
//        customerIdsList.add(id);
//      }
////      mobiles = writer.getCustomersMobilesByCustomerIds(shopPlanDTO.getShopId(), customerIdsList);
//    }
//    return mobiles;
//  }


  @Override
  public void addVehicleToCustomer(long vehicleId, long customerId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      CustomerVehicle cv = new CustomerVehicle();
      cv.setVehicleId(vehicleId);
      cv.setCustomerId(customerId);
      writer.save(cv);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<CustomerVehicleDTO> getCustomerVehicleByVehicleId(long vehicleId) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerVehicleDTO> listCustomerVehicleDTO = new ArrayList<CustomerVehicleDTO>();
    List<CustomerVehicle> customerVehicles = writer.getCustomerVehicleByVehicleId(vehicleId);
    if (CollectionUtil.isEmpty(customerVehicles)) return null;
    for (CustomerVehicle cv : customerVehicles) {
      listCustomerVehicleDTO.add(cv.toDTO());
    }
    return listCustomerVehicleDTO;
  }

  @Override
  public List<CustomerDTO> getCustomerByVehicleNo(String vehicleNo) {
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customers = writer.getCustomerByVehicleNo(vehicleNo);
    if (CollectionUtil.isEmpty(customers)) return null;
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    for (Customer customer : customers) {
      customerDTOs.add(customer.toDTO());
    }
    return customerDTOs;
  }

  @Override
  public CustomerDTO getCustomerInfoByVehicleId(Long shopId, Long vehicleId) {
    UserWriter writer = userDaoManager.getWriter();
    CustomerDTO customerDTO = null;
    Customer customer = writer.getCustomerInfoByVehicleId(shopId, vehicleId);
    if (customer != null) {
      customerDTO = customer.toDTO();
      Vehicle vehicle = writer.getVehicleById(shopId, vehicleId);
      if (vehicle != null) {
        List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();
        vehicleDTOList.add(vehicle.toDTO());
        customerDTO.setVehicleDTOList(vehicleDTOList);
      }
    }
    return customerDTO;
  }

  @Override
  public Map<Long, CustomerDTO> getVehicleIdCustomerMapByVehicleIds(Long shopId, Set<Long> vehicleIds) {
    if (shopId == null || CollectionUtils.isEmpty(vehicleIds)) {
      return new HashMap<Long, CustomerDTO>();
    }
    UserWriter writer = userDaoManager.getWriter();
    Map<Long, CustomerDTO> vehicleIdCustomerMap = new HashMap<Long, CustomerDTO>();
    List<Pair<Customer, CustomerVehicle>> customers = writer.getCustomersByVehicleIds(shopId, vehicleIds);
    List<Long> customerIds = new ArrayList<Long>();
    for (Pair<Customer, CustomerVehicle> pair : customers) {
      if (pair != null && pair.getKey() != null) {
        customerIds.add(pair.getKey().getId());
      }
    }
    IContactService contactService = ServiceManager.getService(IContactService.class);
    Map<Long, List<ContactDTO>> contactDTOsMap = contactService.getContactsByCustomerOrSupplierIds(customerIds, "customer");
    for (Pair<Customer, CustomerVehicle> pair : customers) {
      if (pair != null) {
        Customer customer = pair.getKey();
        CustomerVehicle customerVehicle = pair.getValue();
        if (customerVehicle != null && customer != null) {
          CustomerDTO customerDTO = customer.toDTO();
          List<ContactDTO> contactDTOs = contactDTOsMap.get(customer.getId());
          if (CollectionUtils.isNotEmpty(contactDTOs)) {
            customerDTO.setContacts(contactDTOs.toArray(new ContactDTO[contactDTOs.size()]));
          }
          vehicleIdCustomerMap.put(customerVehicle.getVehicleId(), customerDTO);
        }
      }
    }
    return vehicleIdCustomerMap;
  }

  @Override
  public List<CustomerVehicleDTO> getVehicleByCustomerId(long customerId) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerVehicleDTO> listCustomerVehicleDTO = new ArrayList<CustomerVehicleDTO>();
    for (CustomerVehicle cv : writer.getVehicleByCustomerId(customerId)) {
      listCustomerVehicleDTO.add(cv.toDTO());
    }

    return listCustomerVehicleDTO;
  }

  @Override
  public List<CarDTO> getVehiclesByCustomerId(Long shopId, Long customerId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Object[]> list = writer.getVehiclesByCustomerId(shopId, customerId);
    List<CarDTO> carDTOs = new ArrayList<CarDTO>();

    for (int i = 0; i < list.size(); i++) {
      CarDTO carDTO = new CarDTO();
      if (null != list.get(i)[0]) {
        carDTO.setId(list.get(i)[0].toString());
      }
      if (null != list.get(i)[1]) {
        carDTO.setShopId(list.get(i)[1].toString());
      }
      if (null != list.get(i)[2]) {
        carDTO.setLicenceNo(list.get(i)[2].toString());
      }
      if (null != list.get(i)[3] && !"\u0000".equals(list.get(i)[3].toString())) {
        carDTO.setBrand(list.get(i)[3].toString());
      }
      if (null != list.get(i)[4] && !"\u0000".equals(list.get(i)[4].toString())) {
        carDTO.setModel(list.get(i)[4].toString());
      }
      if (null != list.get(i)[5] && !"\u0000".equals(list.get(i)[5].toString())) {
        carDTO.setYear(list.get(i)[5].toString());
      }
      if (null != list.get(i)[6] && !"\u0000".equals(list.get(i)[6].toString())) {
        carDTO.setEngine(list.get(i)[6].toString());
      }
      if (null != list.get(i)[7] && !"".equals(list.get(i)[7].toString())) {
        carDTO.setCarDate(Long.valueOf(list.get(i)[7].toString()));
        carDTO.setDateString(DateUtil.convertDateLongToDateString("yyyy-MM-dd", Long.valueOf(list.get(i)[7].toString())));
      }
      if (null != list.get(i)[8] && !"\u0000".equals(list.get(i)[8].toString())) {
        carDTO.setChassisNumber(list.get(i)[8].toString());
      }
      if (null != list.get(i)[9] && !"\u0000".equals(list.get(i)[9].toString())) {
        carDTO.setEngineNo(list.get(i)[9].toString());
      }
      if (null != list.get(i)[10] && !"\u0000".equals(list.get(i)[10].toString())) {
        carDTO.setContact(list.get(i)[10].toString());
      }
      if (null != list.get(i)[11] && !"\u0000".equals(list.get(i)[11].toString())) {
        carDTO.setMobile(list.get(i)[11].toString());
      }

      if (null != list.get(i)[12] && !"\u0000".equals(list.get(i)[12].toString())) {
        carDTO.setColor(list.get(i)[12].toString());
      }
      if (null != list.get(i)[13]) {
        carDTO.setStartMileage(NumberUtil.doubleVal(list.get(i)[13]));
      }
      if (null != list.get(i)[14]) {
        carDTO.setObdMileage(NumberUtil.doubleVal(list.get(i)[14]));
      }
      if (null != list.get(i)[15] && !"\u0000".equals(list.get(i)[15].toString())) {
        carDTO.setGsmObdImei(StringUtil.valueOf(list.get(i)[15]));
      }
      if (null != list.get(i)[16] && !"\u0000".equals(list.get(i)[16].toString())) {
        carDTO.setGsmObdImeiMoblie(StringUtil.valueOf(list.get(i)[16]));
      }
      if (null != list.get(i)[17] && !"\u0000".equals(list.get(i)[17].toString())) {
        carDTO.setMaintainMileagePeriod(NumberUtil.doubleVal(list.get(i)[17]));
      }

      carDTOs.add(carDTO);
    }
    return carDTOs;
  }

  @Override
  public SupplierDTO createSupplier(SupplierDTO supplierDTO) throws BcgogoException {
    if (supplierDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);

    Long shopId = supplierDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();

    try {
      createSupplier(writer, supplierDTO);
      writer.commit(status);
      return supplierDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SupplierDTO createSupplier(UserWriter writer, SupplierDTO supplierDTO) {

    if (supplierDTO.getId() == null || CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) {
      Supplier supplier = new Supplier();
      supplier.setStatus(CustomerStatus.ENABLED); // add by zhuj 新增供应商 默认状态为可用
      supplierDTO.setId(null);
      supplier.fromDTO(supplierDTO);
      writer.save(supplier);
      if (!ArrayUtils.isEmpty(supplierDTO.getContacts())) {
        ContactDTO[] contactDTOs = supplierDTO.getContacts();
        // 主联系人相关的的信息设置到supplier表里面
        for (int i = 0; i < contactDTOs.length; i++) {
          if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) {
            if (contactDTOs[i].getIsMainContact() == 1) {
              supplier.setContact(contactDTOs[i].getName());
              supplier.setMobile(contactDTOs[i].getMobile());
              supplier.setEmail(contactDTOs[i].getEmail());
              supplier.setQq(contactDTOs[i].getQq());
            }
          }
        }
        if (StringUtils.isNotBlank(supplierDTO.getIdentity()) && StringUtils.equals(supplierDTO.getIdentity(), "isCustomer")) { // 如果该客户既是供应商 则新增的时候不直接增加联系人 联系人信息的新增由外部统一控制
          // do nothing contacts add by independent method in contactService
        } else {
          // add by zhuj contacts不为空 保存contacts
          for (int i = 0; i < contactDTOs.length; i++) {
            if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) { // 数组可以存储null
              contactDTOs[i].setSupplierId(supplier.getId()); // 设置联系人的supplierId
              contactDTOs[i].setDisabled(1); // 默认有效
              contactDTOs[i].setShopId(supplierDTO.getShopId());
              Contact contact = new Contact();
              contact.fromDTO(contactDTOs[i]);
              writer.save(contact);
              contactDTOs[i].setId(contact.getId());
              if (contactDTOs[i].getIsMainContact() == 1) { // 主联系人相关的的信息设置到supplier表里面
                supplier.setContact(contactDTOs[i].getName());
                supplier.setMobile(contactDTOs[i].getMobile());
                supplier.setEmail(contactDTOs[i].getEmail());
                supplier.setQq(contactDTOs[i].getQq());
              }
            }
          }
        }
      }

      supplierDTO.setId(supplier.getId());
    } else {
      Supplier supplier = writer.getById(Supplier.class, supplierDTO.getId());
      String oldName = supplier.getName();
      double totalInventoryAmount = (supplier.getTotalInventoryAmount() == null ? 0 : supplier.getTotalInventoryAmount());
      Long supplierShopId = supplier.getSupplierShopId();
      RelationTypes relationTypes = supplier.getRelationType();
      supplier.fromDTO(supplierDTO);
      //更新时不应修改这些数据
      supplier.setTotalInventoryAmount(totalInventoryAmount);
      supplier.setSupplierShopId(supplierShopId);
      supplier.setRelationType(relationTypes);

      updateContactFieldsInSupplier(supplierDTO, supplier); // add by zhuj　更新供应商表里面的联系人字段

      writer.save(supplier);

      // 既是供应商又是客户的联系人处理 全部交由contactService处理
      if (StringUtils.isNotBlank(supplierDTO.getIdentity()) && StringUtils.equals(supplierDTO.getIdentity(), "isCustomer")) { // 如果该客户既是供应商 则新增的时候不直接增加联系人 联系人信息的新增由外部统一控制
        // do nothing
      } else {
        updateSupplierContacts(supplierDTO, writer, supplier);
      }

      if (!oldName.equals(supplier.getName())) {
        supplierDTO.setUpdate(true);
      }
    }

    return supplierDTO;
  }

  public SupplierDTO updateSupplier(SupplierDTO supplierDTO) throws BcgogoException {
    if (supplierDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    if (supplierDTO.getShopId() == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    if (supplierDTO.getId() == null) throw new BcgogoException(BcgogoExceptionType.SupplierNotFound);

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      updateSupplier(writer, supplierDTO);
      writer.commit(status);
      return supplierDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SupplierDTO updateSupplier(UserWriter writer, SupplierDTO supplierDTO) throws BcgogoException {
    if (supplierDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    if (supplierDTO.getShopId() == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    if (supplierDTO.getId() == null) throw new BcgogoException(BcgogoExceptionType.SupplierNotFound);

    Supplier supplier = writer.getById(Supplier.class, supplierDTO.getId());
    supplier.fromDTO(supplierDTO);
    updateContactFieldsInSupplier(supplierDTO, supplier); // add by zhuj　更新供应商表里面的联系人字段
    writer.save(supplier);

    // 既是供应商又是客户的联系人处理 全部交由contactService处理
    if (StringUtils.isNotBlank(supplierDTO.getIdentity()) && StringUtils.equals(supplierDTO.getIdentity(), "isCustomer") || supplierDTO.isCancel()) { // 如果该客户既是供应商 则新增的时候不直接增加联系人 联系人信息的新增由外部统一控制
      //ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(null, supplierDTO.getId(), supplier.getShopId(), supplierDTO.getContacts());
    } else {
      updateSupplierContacts(supplierDTO, writer, supplier);
    }
    return supplierDTO;

  }

  private void updateSupplierContacts(SupplierDTO supplierDTO, UserWriter writer, Supplier supplier) {
    // add by zhuj 如果有联系人列表 更新联系人列表
    // 目前列表 与 DB列表 比较 差集新增(其实就是id不存在的集合)、删除（置为disabled） 交集更新
    //if (supplierDTO.hasValidContact()){
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, supplierDTO.getId(), supplierDTO.getShopId(), null, null);

    // 获取DB联系人id列表
    List<Long> dbIds = new ArrayList<Long>();
    if (!CollectionUtils.isEmpty(contactList)) {
      for (Contact contact : contactList) {
        if (contact.toDTO().isValidContact()) {
          dbIds.add(contact.getId());
        }
      }
    }

    List<Long> toBeUpdatedIds = new ArrayList<Long>();
    if (!ArrayUtils.isEmpty(supplierDTO.getContacts())) {
      for (ContactDTO contactDTO : supplierDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          contactDTO.setSupplierId(supplier.getId());
          toBeUpdatedIds.add(contactDTO.getId());
        }
      }
    }

    if (supplierDTO.isFromManagePage()) {
      // 对于db中存在 目前列表有效联系人列表中不存在的联系人 置为disabled
      for (Contact contact : contactList) {
        if (!toBeUpdatedIds.contains(contact.getId())) {
          contact.setDisabled(0);
          writer.update(contact);
        }
      }
    }

    // id在db中存在 更新
    if (!ArrayUtils.isEmpty(supplierDTO.getContacts())) {
      for (ContactDTO contactDTO : supplierDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact() && dbIds.contains(contactDTO.getId())) {
          for (Contact contact : contactList) {
            if (contact.getId().equals(contactDTO.getId())) {
              contact.fromDTO(contactDTO);
              writer.update(contact);
            }
          }
        }
      }
      // id不存在
      for (ContactDTO contactDTO : supplierDTO.getContacts()) {
        if (contactDTO != null && contactDTO.getId() == null && contactDTO.isValidContact()) {
          contactDTO.setDisabled(1);
          Contact contact = new Contact();
          contact.setShopId(supplierDTO.getShopId());
          contact.fromDTO(contactDTO);
          writer.save(contact);
          contactDTO.setId(contact.getId());
        }
      }
    }

  }

  @Override
  public SupplierDTO updateSupplier(SupplierDTO supplierDTO, Long orderId, OrderTypes orderType,
                                    String products, Double amount) throws BcgogoException {
    if (supplierDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);

    Long shopId = supplierDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    if (supplierDTO.getId() == null) throw new BcgogoException(BcgogoExceptionType.SupplierNotFound);

    UserWriter writer = userDaoManager.getWriter();
    Long id = supplierDTO.getId();
    Supplier supplier = writer.getById(Supplier.class, id);
    if (supplier == null) throw new BcgogoException(BcgogoExceptionType.SupplierNotFound);
    if (orderId != null) {
      supplierDTO.setLastOrderId(orderId);
      supplierDTO.setLastOrderType(orderType);
      if (supplier.getTotalInventoryAmount() != null) {
        supplierDTO.setTotalInventoryAmount(supplier.getTotalInventoryAmount() + amount);
      } else {
        supplierDTO.setTotalInventoryAmount(amount);
      }
    }
    supplier.fromDTO(supplierDTO);
    supplier.setTotalInventoryAmount(supplierDTO.getTotalInventoryAmount());

    updateSupplier(supplierDTO);

    supplierDTO.setUpdate(true);
    return supplierDTO;

  }

  @Override
  public SupplierDTO getSupplierById(long supplierId) {
    UserWriter writer = userDaoManager.getWriter();

    Supplier supplier = writer.getById(Supplier.class, supplierId);

    if (supplier == null) return null;
    SupplierDTO supplierDTO = supplier.toDTO();
    setSupplierContacts(supplierId, writer, supplierDTO);

    return supplierDTO;
  }

  private void setSupplierContacts(long supplierId, UserWriter writer, SupplierDTO supplierDTO) {
    // add by zhuj 联系人列表
    ContactDTO[] contactDTOs = new ContactDTO[3];
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, supplierId, supplierDTO.getShopId(), null, null);
    boolean hasMainContact = false;

    if (!CollectionUtils.isEmpty(contactList)) {
      int size = contactList.size();
      if (size > 3) {
        LOG.error("supplier's contactlist is over 3,supplierId is" + supplierId);
        size = 3;
      }
      for (int i = 0; i < size; i++) {
        ContactDTO contactDTO = contactList.get(i).toDTO();
        contactDTOs[i] = contactDTO;
        if (contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
          supplierDTO.setContactId(contactDTO.getId());
          supplierDTO.setContactIdStr(String.valueOf(contactDTO.getId()));
          supplierDTO.setContact(contactDTO.getName());
          supplierDTO.setMobile(contactDTO.getMobile());
          supplierDTO.setEmail(contactDTO.getEmail());
          supplierDTO.setQq(contactDTO.getQq());
          hasMainContact = true;
        }
      }

    }

//    if (!hasMainContact) {
//      if (contactDTOs[0] == null) {
//        contactDTOs[0] = new ContactDTO();
//      }
//      contactDTOs[0].setIsMainContact(1);
//    }

    supplierDTO.setContacts(contactDTOs);
  }

  public List<SupplierDTO> getSupplierById(Long shopId, long supplierId) {
    UserWriter writer = userDaoManager.getWriter();
    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    for (Supplier supplier : writer.getSupplierById(shopId, supplierId)) {
      // add by zhuj  查询supplier下面的联系人列表
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplierId, writer, supplierDTO);
      listSupplierDTO.add(supplierDTO);
    }
    return listSupplierDTO;
  }

  public List<SupplierDTO> getSupplierDTOByIds(Long shopId, Long... supplierIds) {
    if (shopId == null || ArrayUtil.isEmpty(supplierIds)) return null;
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> supplierList = writer.getSupplierDTOByIds(shopId, supplierIds);
    List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
    if (CollectionUtil.isNotEmpty(supplierList)) {
      for (Supplier supplier : supplierList) {
        supplierDTOs.add(supplier.toDTO());
      }
    }
    return supplierDTOs;
  }

  @Override
  public List<SupplierDTO> getSupplierByName(long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();

    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    for (Supplier supplier : writer.getSupplierByName(shopId, name)) {
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
      listSupplierDTO.add(supplierDTO);
    }

    return listSupplierDTO;
  }

  /**
   * 模糊匹配 supplier name
   *
   * @param shopId
   * @param name
   * @return
   */
  public List<BaseDTO> getSupplierByMatchedName(Long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();
    List<BaseDTO> listSupplierDTO = new ArrayList<BaseDTO>();
    for (Supplier supplier : writer.getSupplierByMatchedName(shopId, name)) {
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
      listSupplierDTO.add(supplierDTO);
    }
    return listSupplierDTO;
  }

  @Override
  public List<SupplierDTO> getSupplierByNameAndShopId(long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();

    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    for (Supplier supplier : writer.getSupplierByNameAndShopId(shopId, name)) {
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplier.getId(), writer, supplierDTO); // add by zhuj
      listSupplierDTO.add(supplierDTO);
    }
    return listSupplierDTO;
  }

  @Override
  public SupplierDTO getSpecialSupplierByName(long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> supplierList = writer.getSpecialSupplierByName(shopId, name);
    if (CollectionUtils.isNotEmpty(supplierList) && supplierList.size() == 1) {
      SupplierDTO supplierDTO = supplierList.get(0).toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
      return supplierDTO;
    }
    return null;
  }


  @Override
  public List<SupplierDTO> getSupplierByMobile(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    List<Supplier> suppliers = writer.getSupplierByMobile(shopId, mobile);
    for (Supplier supplier : suppliers) {
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO);
      listSupplierDTO.add(supplierDTO);
    }

    return listSupplierDTO;
  }

  @Override
  public List<SupplierDTO> getSupplierByMobile2(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    if (org.apache.commons.lang.StringUtils.isBlank(mobile)) {
      return null;
    }
    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, null, shopId, null, mobile);
    if (CollectionUtils.isNotEmpty(contactList)) {
      // 过滤掉customerId为空 或者 为0L的数据
      filterSupplierContactList(contactList);
      if (CollectionUtils.isNotEmpty(contactList)) {
        for (Contact contact : contactList) {
          Long supplierId = contact.getSupplierId();
          Supplier supplier = null;
          List<Supplier> suppliers = writer.getSupplierById(shopId, supplierId);
          if (CollectionUtils.isNotEmpty(suppliers)) {
            supplier = suppliers.get(0);
          }
          if (supplier != null) {
            SupplierDTO supplierDTO = supplier.toDTO();
            ContactDTO[] contactDTOs = new ContactDTO[1];
            contactDTOs[0] = contact.toDTO();
            supplierDTO.setContacts(contactDTOs);
            supplierDTO.setContact(contact.getName());
            supplierDTO.setContactId(contact.getId());
            supplierDTO.setMobile(contact.getMobile());
            supplierDTO.setEmail(contact.getEmail());
            supplierDTO.setQq(contact.getQq());
            listSupplierDTO.add(supplierDTO);
          }
        }

      }

    }

    return listSupplierDTO;
  }

  @Override
  public List<SupplierDTO> getSuppliersByKey(long shopId, String key, int pageNo, int pageSize) {
    UserWriter writer = userDaoManager.getWriter();

    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    for (Supplier supplier : writer.getSuppliersByKey(shopId, key, pageNo, pageSize)) {
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
      listSupplierDTO.add(supplierDTO);
    }

    return listSupplierDTO;
  }

  @Override
  public List<SupplierDTO> getShopSupplier(long shopId, int pageNo, int pageSize) {
    UserWriter writer = userDaoManager.getWriter();
    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    for (Supplier supplier : writer.getShopSupplier(shopId, pageNo, pageSize)) {
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
      listSupplierDTO.add(supplierDTO);
    }
    return listSupplierDTO;
  }

  @Override
  public CustomerRecordDTO createCustomerRecord(CustomerRecordDTO customerRecordDTO) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      customerRecordDTO = createCustomerRecord(customerRecordDTO, writer);
      writer.commit(status);
      return customerRecordDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CustomerRecordDTO createCustomerRecord(CustomerRecordDTO customerRecordDTO, UserWriter writer) throws BcgogoException {
    if (customerRecordDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = customerRecordDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    CustomerRecord customerRecord = new CustomerRecord(customerRecordDTO);
    writer.save(customerRecord);
    customerRecordDTO.setId(customerRecord.getId());
    return customerRecordDTO;
  }

  @Override
  public CustomerRecordDTO updateCustomerRecord(CustomerRecordDTO customerRecordDTO) throws Exception {
    if (customerRecordDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = customerRecordDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    Long customerId = customerRecordDTO.getCustomerId();
    if (customerId == null) throw new BcgogoException(BcgogoExceptionType.CustomerRecordNotFound);
    UserWriter writer = userDaoManager.getWriter();
    CustomerRecord customerRecord = writer.getCustomerRecordByCustId(customerId);
    if (customerRecord == null) {
      customerRecord = new CustomerRecord();
    }
    Object status = writer.begin();
    try {
      customerRecord.fromDTONotCopyId(customerRecordDTO);
      writer.saveOrUpdate(customerRecord);
      writer.commit(status);
      return customerRecord.toDTO();
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      throw e;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<CustomerRecordDTO> getShopCustomerRecord(long shopId, int pageNo, int pageSize) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    for (CustomerRecord customerRecord : writer.getShopCustomerRecord(shopId, pageNo, pageSize)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }
    return listCustomerRecordDTO;
  }

  @Override
  public List<CustomerRecordDTO> getShopCustomerRecordDTO(long shopId, int pageNo, int pageSize) {
    List<CustomerRecordDTO> customerRecordDTOs = getShopCustomerRecord(shopId, pageNo, pageSize);
    UserWriter writer = userDaoManager.getWriter();
    if (customerRecordDTOs.isEmpty())
      return null;
    List<Long> customerIds = new ArrayList<Long>();
    for (CustomerRecordDTO customerRecordDTO : customerRecordDTOs) {
      if (customerRecordDTO.getCustomerId() != null)
        customerIds.add(customerRecordDTO.getCustomerId());
    }
    if (CollectionUtils.isEmpty(customerIds)) return customerRecordDTOs;
    List<CustomerVehicleNumberDTO> customerVehicleNumberDTOList = writer.getCustomerVehicleCount(customerIds);
    if (customerVehicleNumberDTOList.isEmpty())
      return customerRecordDTOs;
    for (CustomerRecordDTO customerRecordDTO : customerRecordDTOs) {
      for (CustomerVehicleNumberDTO customerVehicleNumberDTO : customerVehicleNumberDTOList) {
        if (customerVehicleNumberDTO == null || customerVehicleNumberDTO.getCount() == null) {
          continue;
        }
        if (customerVehicleNumberDTO.getCustomerId().equals(customerRecordDTO.getCustomerId())) {
          customerRecordDTO.setVehicleCount(customerVehicleNumberDTO.getCount());
          customerVehicleNumberDTOList.remove(customerVehicleNumberDTO);
          break;
        }
      }
    }
    return customerRecordDTOs;
  }

  //补充进车辆数量的数据, 客户类型和是否VIP
  public List<CustomerRecordDTO> formatShopCustomerRecordDTO(List<CustomerRecordDTO> listCustomerRecordDTO) {
    UserWriter writer = userDaoManager.getWriter();
    if (listCustomerRecordDTO.isEmpty())
      return null;
    List<Long> customerIds = new ArrayList<Long>();
    for (CustomerRecordDTO customerRecordDTO : listCustomerRecordDTO) {
      if (customerRecordDTO.getCustomerId() != null) {
        customerIds.add(customerRecordDTO.getCustomerId());
      }
      //获得会员信息
      Member member = writer.getMemberByCustomerId(customerRecordDTO.getShopId(), customerRecordDTO.getCustomerId());
      if (member != null) {
        MemberDTO memberDTO = member.toDTO();
        customerRecordDTO.setMemberDTO(memberDTO);
      }
    }
    if (CollectionUtils.isEmpty(customerIds)) return listCustomerRecordDTO;
    List<CustomerVehicleNumberDTO> customerVehicleNumberDTOList = writer.getCustomerVehicleCount(customerIds);  //车辆数量
    Map<Long, CustomerVehicleNumberDTO> vehicleNumberDTOMap = new HashMap<Long, CustomerVehicleNumberDTO>();
    for (CustomerVehicleNumberDTO customerVehicleNumberDTO : customerVehicleNumberDTOList) {
      vehicleNumberDTOMap.put(customerVehicleNumberDTO.getCustomerId(), customerVehicleNumberDTO);
    }

    List<CustomerDTO> customers = writer.getCustomerByIds(customerIds);        //customer信息
    Map<Long, CustomerDTO> customersMap = new HashMap<Long, CustomerDTO>();
    for (CustomerDTO dto : customers) {
      customersMap.put(dto.getId(), dto);
    }

    for (CustomerRecordDTO customerRecordDTO : listCustomerRecordDTO) {
      CustomerVehicleNumberDTO vehicleNumberDTO = vehicleNumberDTOMap.get(customerRecordDTO.getCustomerId());
      customerRecordDTO.setVehicleCount(vehicleNumberDTO == null ? 0 : vehicleNumberDTO.getCount());
      CustomerDTO customerDTO = customersMap.get(customerRecordDTO.getCustomerId());
      customerRecordDTO.setCustomerKind(customerDTO == null ? "" : customerDTO.getCustomerKind());
    }
    vehicleNumberDTOMap = null;
    customersMap = null;
    return listCustomerRecordDTO;
  }

  /**
   * 获取给客户发短信时需要参考的客户信息
   *
   * @param shopId
   * @param currentPage
   * @param pageSize
   * @return
   */
  @Override
  public List<CustomerRecordDTO> getSmsCustomerInfoList(long shopId, int currentPage, int pageSize) throws BcgogoException {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    List<CustomerRecordDTO> customerRecordDTOList = getShopCustomerRecord(shopId, currentPage - 1, pageSize);
    if (customerRecordDTOList == null || customerRecordDTOList.isEmpty()) {
      return customerRecordDTOList;
    }
    List<Long> customerIds = new ArrayList<Long>();
    for (CustomerRecordDTO customerRecordDTO : customerRecordDTOList) {
      if (customerRecordDTO == null) {
        continue;
      }
      customerIds.add(customerRecordDTO.getCustomerId());
    }
    List<CustomerDTO> customerDTOList = customerService.getCustomerByIds(customerIds);
    if (customerDTOList != null && !customerDTOList.isEmpty()) {
      for (CustomerDTO customerDTO : customerDTOList) {
        if (customerDTO == null) {
          continue;
        }
        for (CustomerRecordDTO customerRecordDTO : customerRecordDTOList) {
          if (customerRecordDTO != null && NumberUtil.isEqual(customerRecordDTO.getCustomerId(), customerDTO.getId())) {
            customerRecordDTO.setBirthday(customerDTO.getBirthday());
            break;
          }
        }
      }
    }
    List<CustomerCardDTO> customerCardDTOList = getCustomerCardByCustomerIdsAndCardType(shopId, customerIds, WashCardConstants.CARD_TYPE_DEFAULT);
    if (customerCardDTOList != null && !customerCardDTOList.isEmpty()) {
      for (CustomerRecordDTO customerRecordDTO : customerRecordDTOList) {
        if (customerRecordDTO == null) {
          continue;
        }
        for (CustomerCardDTO customerCardDTO : customerCardDTOList) {
          if (customerCardDTO != null && NumberUtil.isEqual(customerCardDTO.getCustomerId(), customerRecordDTO.getCustomerId())) {
            customerRecordDTO.setWashRemain(customerCardDTO.getWashRemain());
            break;
          }
        }
      }
    }
    return customerRecordDTOList;
  }

  public List<CustomerRecordDTO> getShopArrearsCustomerRecord(long shopId, int pageNo, int pageSize) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    for (CustomerRecord customerRecord : writer.getShopArrearsCustomerRecord(shopId, pageNo, pageSize)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }
    return listCustomerRecordDTO;
  }

  public int countShopArrearsCustomerRecord1(long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopArrearsCustomerRecord1(shopId);
  }


  public double getShopTotalArrears(long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getShopTotalArrears(shopId);
  }

  @Override
  public List<CustomerRecordDTO> getCustomerRecordByName(String name) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    for (CustomerRecord customerRecord : writer.getCustomerRecordByName(name)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }
    return listCustomerRecordDTO;
  }

  @Override
  public List<CustomerRecordDTO> getCustomerRecordByCustomerId(long customerId) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    for (CustomerRecord customerRecord : writer.getCustomerRecordByCustomerId(customerId)) {
      CustomerRecordDTO customerRecordDTO = customerRecord.toDTO();
      setCustomerRecordContacts(customerId, writer, customerRecordDTO); // add by zhuj  查询联系人信息
      listCustomerRecordDTO.add(customerRecord.toDTO());

    }

    return listCustomerRecordDTO;
  }

  private void setCustomerRecordContacts(long customerId, UserWriter writer, CustomerRecordDTO customerRecordDTO) {
    // add by zhuj 联系人列表
    ContactDTO[] contactDTOs = new ContactDTO[3];
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(customerId, null, customerRecordDTO.getShopId(), null, null);
    if (!CollectionUtils.isEmpty(contactList)) {
      int size = contactList.size();
      if (size > 3) {
        LOG.warn("customer's contactList size is over 3,customerId is " + customerId);
        size = 3;
      }
      for (int i = 0; i < size; i++) {
        if (contactList.get(i) != null) {
          ContactDTO contactDTO = contactList.get(i).toDTO();
          contactDTOs[i] = contactDTO;
          if (contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
            customerRecordDTO.setContactId(contactDTO.getId());
            customerRecordDTO.setContactIdStr(String.valueOf(contactDTO.getId()));
            customerRecordDTO.setName(contactDTO.getName());
            customerRecordDTO.setMobile(contactDTO.getMobile());
            customerRecordDTO.setEmail(contactDTO.getEmail());
            customerRecordDTO.setQq(contactDTO.getQq());
          }
        }
      }
      customerRecordDTO.setContacts(contactDTOs);
    }
    customerRecordDTO.setContacts(contactDTOs); // 这个地方必须设置(页面要显示3个联系人)
  }


  @Override
  public List<CustomerRecordDTO> getShopCustomerRecordByName(long shopId, String name) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    for (CustomerRecord customerRecord : writer.getShopCustomerRecordByName(shopId, name)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }
    return listCustomerRecordDTO;
  }


  @Override
  public List<CustomerRecordDTO> getShopCustomerRecordByMobile(long shopId, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    /*for (CustomerRecord customerRecord : writer.getShopCustomerRecordByMobile(shopId, mobile)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }*/

    // modified by zhuj
    if (org.apache.commons.lang.StringUtils.isBlank(mobile)) {
      return null;
    }
    //modified by zhuj　通过手机号查询联系人信息
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, null, shopId, null, mobile);
    if (CollectionUtils.isNotEmpty(contactList)) {
      // 过滤掉customerId为空 或者 为0L的数据
      filterCustomerContactList(contactList);
      if (CollectionUtils.isNotEmpty(contactList)) {
        if (contactList.size() > 1) {
          LOG.error("通过手机号[" + mobile + "],查询联系人不唯一!");
          //throw new RuntimeException("通过手机号[" + mobile + "],查询联系人不唯一!");
        }
        Contact contact = contactList.get(0);
        Long customerId = contact.getCustomerId();

        CustomerRecord customerRecord = writer.getCustomerRecordByCustId(customerId);
        if (customerRecord != null) {
          CustomerRecordDTO customerRecordDTO = customerRecord.toDTO();
          ContactDTO[] contactDTOs = new ContactDTO[1];
          contactDTOs[0] = contact.toDTO();
          customerRecordDTO.setContacts(contactDTOs);
          customerRecordDTO.setContact(contact.getName());
          Long contactId = contact.getId();
          if (contactId != null) {
            customerRecordDTO.setContactIdStr(String.valueOf(contactId));
          }
          customerRecordDTO.setContactId(contact.getId());
          customerRecordDTO.setMobile(contact.getMobile());
          customerRecordDTO.setEmail(contact.getEmail());
          customerRecordDTO.setQq(contact.getQq());
          listCustomerRecordDTO.add(customerRecordDTO);
        }
      }

    }

    return listCustomerRecordDTO;
  }

  @Override
  public List<CustomerRecordDTO> getShopCustomerRecordByMobile(long shopId, String name, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    /*for (CustomerRecord customerRecord : writer.getShopCustomerRecordByMobile(shopId, name, mobile)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }*/

    // modified by zhuj
    if (org.apache.commons.lang.StringUtils.isBlank(mobile)) {
      return null;
    }
    //modified by zhuj　通过手机号查询联系人信息
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, null, shopId, null, mobile);
    if (CollectionUtils.isNotEmpty(contactList)) {
      // 过滤掉customerId为空 或者 为0L的数据
      filterCustomerContactList(contactList);
      if (CollectionUtils.isNotEmpty(contactList)) {
        if (contactList.size() > 1) {
          LOG.error("通过手机号[" + mobile + "],查询联系人不唯一!");
          //throw new RuntimeException("通过手机号[" + mobile + "],查询联系人不唯一!");
        }
        Contact contact = contactList.get(0);
        Long customerId = contact.getCustomerId();

        CustomerRecord customerRecord = writer.getCustomerRecordByCustId(customerId);
        if (customerRecord != null) {
          if (StringUtils.isNotBlank(name)) {
            if (!StringUtils.equals(customerRecord.getName(), name)) { // 如果有名字进行约束
              return listCustomerRecordDTO;
            }
          }
          CustomerRecordDTO customerRecordDTO = customerRecord.toDTO();
          ContactDTO[] contactDTOs = new ContactDTO[1];
          contactDTOs[0] = contact.toDTO();
          customerRecordDTO.setContacts(contactDTOs);
          customerRecordDTO.setContact(contact.getName());
          Long contactId = contact.getId();
          if (contactId != null) {
            customerRecordDTO.setContactIdStr(String.valueOf(contactId));
          }
          customerRecordDTO.setContactId(contact.getId());
          customerRecordDTO.setMobile(contact.getMobile());
          customerRecordDTO.setEmail(contact.getEmail());
          customerRecordDTO.setQq(contact.getQq());
          listCustomerRecordDTO.add(customerRecordDTO);
        }
      }
    }

    return listCustomerRecordDTO;
  }

  @Override
  public List<CustomerRecordDTO> getShopCustomerRecordByLicenceNo(long shopId, String licenceNo) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    for (CustomerRecord customerRecord : writer.getShopCustomerRecordByLicenceNo(shopId, licenceNo)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }

    return listCustomerRecordDTO;
  }


  @Override
  public List<CustomerRecordDTO> getShopCustomerRecord(long shopId, String name, String licenceNo) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    for (CustomerRecord customerRecord : writer.getShopCustomerRecord(shopId, name, licenceNo)) {
      listCustomerRecordDTO.add(customerRecord.toDTO());
    }

    return listCustomerRecordDTO;
  }

  @Override
  public CustomerRecord getShopCustomerRecordByCustomerId(Long shopId, Long customerId) {
    if (customerId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    return writer.getShopCustomerRecordByCustomerId(shopId, customerId);
  }

  public List<CustomerServiceJobDTO> getCustomerServiceJobByCustomerIdAndVehicleId(Long shopId, Long customerId, Long vehicleId) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerServiceJobDTO> customerServiceJobDTOList = new ArrayList<CustomerServiceJobDTO>();
    for (CustomerServiceJob customerServiceJob : writer.getCustomerServiceJobByCustomerIdAndVehicleId(shopId, customerId, vehicleId)) {
      customerServiceJobDTOList.add(customerServiceJob.toDTO());
    }
    return customerServiceJobDTOList;
  }

  //shao
  public List<CustomerServiceJobDTO> getCustomerServiceJobByStateAndPageNoAndPageSize(long shopId, List<String> status, Long remindTime, int pageNo, int pageSize) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    List<CustomerServiceJobDTO> listCustomerServiceJobDTO = new ArrayList<CustomerServiceJobDTO>();
    List<CustomerServiceJob> customerServiceJobList = writer.getCustomerServiceJobByStateAndPageNoAndPageSize(shopId, status, remindTime, pageNo, pageSize);
    if (customerServiceJobList != null && customerServiceJobList.size() > 0) {
      for (CustomerServiceJob customerServiceJob : customerServiceJobList) {
        CustomerServiceJobDTO customerServiceJobDTO = new CustomerServiceJobDTO();
        Customer customer = writer.getCustomerByCustomerId(customerServiceJob.getCustomerId());
        customerServiceJobDTO = customerServiceJob.toDTO();
        if (customer != null) {
          customerServiceJobDTO.setCustomerName(customer.getName());
          customerServiceJobDTO.setContact(customer.getContact());
          customerServiceJobDTO.setMobile(customer.getMobile());
        }
        if (customerServiceJob.getVehicleId() != null) {
          VehicleDTO vehicleDTO = vehicleService.findVehicleById(customerServiceJob.getVehicleId());
          customerServiceJobDTO.setLicenceNo(vehicleDTO == null ? null : vehicleDTO.getLicenceNo());
        }
        listCustomerServiceJobDTO.add(customerServiceJobDTO);
      }
    }
    return listCustomerServiceJobDTO;
  }


  public int countCustomerServiceJobByShopId(Long shopId, long remindTime, List<String> status) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countCustomerServiceJobByShopId(shopId, remindTime, status);
  }

  @Override
  public List<CustomerServiceJobDTO> getCustomerServiceJobByCustomerIdAndRemindType(long customerId, long remindType) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerServiceJobDTO> listCustomerServiceJobDTO = new ArrayList<CustomerServiceJobDTO>();
    for (CustomerServiceJob customerServiceJob : writer.getCustomerServiceJobByCustomerIdAndRemindType(customerId, remindType)) {
      listCustomerServiceJobDTO.add(customerServiceJob.toDTO());
    }

    return listCustomerServiceJobDTO;
  }

  @Override
  public CustomerCardDTO createCustomerCard(CustomerCardDTO customerCardDTO) throws BcgogoException {
    if (customerCardDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);

    UserWriter writer = userDaoManager.getWriter();
    String key = "createCustomerCard_" + (customerCardDTO.getCustomerId() == null ? "" : customerCardDTO.getCustomerId())
      + (customerCardDTO.getShopId() == null ? "" : customerCardDTO.getShopId());

    Object status = writer.begin();
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return null;
      CustomerCard customerCard = new CustomerCard(customerCardDTO);

      writer.save(customerCard);
      writer.commit(status);

      customerCardDTO.setId(customerCard.getId());

      return customerCardDTO;
    } finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Override
  public CustomerCardDTO updateCustomerCard(CustomerCardDTO customerCardDTO) throws BcgogoException {
    if (customerCardDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = customerCardDTO.getId();
      if (id == null) throw new BcgogoException(BcgogoExceptionType.CustomerCardNotFound);
      CustomerCard customerCard = writer.getById(CustomerCard.class, id);
      if (customerCard == null) throw new BcgogoException(BcgogoExceptionType.CustomerCardNotFound);

      customerCard.fromDTO(customerCardDTO);

      writer.save(customerCard);
      writer.commit(status);

      return customerCardDTO;
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public List<CustomerCardDTO> getCustomerCardByCustomerIdAndCardType(long shopId, long customerId, long cardType) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerCardDTO> listCustomerCardDTO = new ArrayList<CustomerCardDTO>();
    for (CustomerCard customerCard : writer.getCustomerCardByCustomerIdAndCardType(shopId, customerId, cardType)) {
      listCustomerCardDTO.add(customerCard.toDTO());
    }

    return listCustomerCardDTO;
  }

  @Override
  public List<CustomerCardDTO> getCustomerCardByCustomerIdsAndCardType(Long shopId, List<Long> customerIds, Long cardType) {
    if (shopId == null || customerIds == null || customerIds.isEmpty() || cardType == null) {
      return new ArrayList<CustomerCardDTO>();
    }
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerCardDTO> listCustomerCardDTO = writer.getCustomerCardByCustomerIdsAndCardType(shopId, customerIds, cardType);
    return listCustomerCardDTO;
  }


  public int countShopSupplier(long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopSupplier(shopId);
  }

  public int countShopSupplierByKey(long shopId, String key) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopSupplierByKey(shopId, key);
  }

  public List<Supplier> getShopSupplierByKey(long shopId, String key) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getShopSupplierByKey(shopId, key);
  }

  @Autowired
  private UserDaoManager userDaoManager;

  public Customer getCustomerByCustomerId(Long customerId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCustomerByCustomerId(customerId);
  }

  @Override
  public Customer getCustomerByCustomerId(Long customerId, Long shopId) {
    if (customerId == null || shopId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCustomerByCustomerIdAndShopId(customerId, shopId);
  }

  @Override
  public CustomerDTO getCustomerDTOByCustomerId(Long customerId, Long shopId) {
    if (customerId == null || shopId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    Customer customer = writer.getCustomerByCustomerIdAndShopId(customerId, shopId);
    if (customer == null) {
      return null;
    }
    CustomerDTO customerDTO = customer.toDTO();
    setCustomerContacts(customerDTO.getId(), writer, customerDTO); // add by zhuj
    return customerDTO;
  }

  public UserVercode getUserVercodeByUserNo(String userno) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getUserVercodeByUserNo(userno);
  }

  public UserVercodeDTO createUserVercode(String userno, String vercode) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserVercode uv = null;
      uv = writer.getUserVercodeByUserNo(userno);
      if (uv != null)
        writer.delete(uv);


      uv = new UserVercode();
      uv.setUserNo(userno);
      uv.setVercode(vercode);
      writer.save(uv);
      writer.commit(status);
      return uv.toDTO();
    } finally {
      writer.rollback(status);
    }


  }

  //客户智能搜索匹配         （汉字）   zhanghcuanlong
  public List<CustomerDTO> getCustomer(String keyword, Long shopId) {
    StopWatch sw = new StopWatch("汉字匹配");
    sw.start("query SQL");
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customers = null;
    if (keyword != null && !keyword.equals("") && shopId != null) {
      customers = writer.getCustomer(keyword, shopId);
    }
    sw.stop();
    sw.start("get result");
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    if (customers.size() > 0) {
      for (Customer c : customers) {
        CustomerDTO customerDTO = c.toDTO();
        setCustomerContacts(customerDTO.getId(), writer, customerDTO); // add by zhuj
        customerDTOs.add(customerDTO);
      }
    }
    sw.stop();
    LOG.debug(sw.toString());
    return customerDTOs;
  }

  //供应商智能匹配  （汉字）    zhangchuanlong
  public List<SupplierDTO> getSupplier(String keyword, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> suppliers = null;
    if (keyword != null && !keyword.equals("") && shopId != null) {
      suppliers = writer.getSupplier(keyword, shopId);
    }
    List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
    if (suppliers != null) {
      for (Supplier s : suppliers) {
        SupplierDTO supplierDTO = s.toDTO();
        setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
        supplierDTOs.add(s.toDTO());
      }
    }
    return supplierDTOs;
  }

  //客户智能搜索匹配   (字母)         zhanghcuanlong
  public List<CustomerDTO> getCustomerByZiMu(String keyword, Long shopId) {
    StopWatch sw = new StopWatch("汉字匹配");
    sw.start("query SQL");
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customers = null;
    if (keyword != null && !keyword.equals("") && shopId != null) {
      customers = writer.getCustomerByZiMu(keyword, shopId);
    }
    sw.stop();
    sw.start("return result");
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    if (customers != null) {
      for (Customer s : customers) {
        CustomerDTO customerDTO = s.toDTO();
        setCustomerContacts(customerDTO.getId(), writer, customerDTO); // add by zhuj
        customerDTOs.add(customerDTO);
      }
    }
    sw.stop();
    LOG.info(sw.toString());
    return customerDTOs;
  }


  // 供应商智能匹配  （字母）              zhangchuanlong
  public List<SupplierDTO> getSupplierByZiMu(String keyword, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> suppliers = null;
    if (keyword != null && !keyword.equals("") && shopId != null) {
      suppliers = writer.getSupplierByZiMu(keyword, shopId);
    }
    List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
    if (CollectionUtils.isNotEmpty(suppliers)) {
      for (Supplier s : suppliers) {
        SupplierDTO supplierDTO = s.toDTO();
        setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
        supplierDTOs.add(supplierDTO);
      }
    }
    return supplierDTOs;
  }

  @Override
  public List<CustomerDTO> getCustomerByTelephone(Long shopId, String telephone) {
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerDTO> listCustomerDTO = new ArrayList<CustomerDTO>();
    for (Customer customer : writer.getCustomerByTelephone(shopId, telephone)) {
      CustomerDTO customerDTO = customer.toDTO();
      setCustomerContacts(customerDTO.getId(), writer, customerDTO); // add by zhuj
      listCustomerDTO.add(customerDTO);
    }
    return listCustomerDTO;
  }

  @Override
  public List<SupplierDTO> getSupplierByTelephone(Long shopId, String telephone) {
    UserWriter writer = userDaoManager.getWriter();

    List<SupplierDTO> listSupplierDTO = new ArrayList<SupplierDTO>();
    for (Supplier supplier : writer.getSupplierByTelephone(shopId, telephone)) {
      SupplierDTO supplierDTO = supplier.toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); // add by zhuj
      listSupplierDTO.add(supplierDTO);
    }

    return listSupplierDTO;
  }

  @Deprecated
  @Override
  public void saveOrUpdateCarsWithCustomerId(Long customerId, Long shopId, CarDTO[] vehicles) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (vehicles != null && vehicles.length > 0) {
        for (CarDTO carDTO : vehicles) {
          if (carDTO.getLicenceNo() == null || "".equals(carDTO.getLicenceNo())) {
            continue;
          }
          Vehicle vehicle = new Vehicle();
          if (carDTO.getId() != null && !"".equals(carDTO.getId()) && !"0".equals(carDTO.getId())) {
            vehicle = writer.getById(Vehicle.class, Long.parseLong(carDTO.getId()));
          }
          try {
            vehicle.setLicenceNo(carDTO.getLicenceNo());
            vehicle.setBrand(carDTO.getBrand());
            vehicle.setModel(carDTO.getModel());
            vehicle.setYear(carDTO.getYear());
            vehicle.setEngine(carDTO.getEngine());
            vehicle.setChassisNumber(carDTO.getChassisNumber());
            vehicle.setCarDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, carDTO.getDateString()));
            vehicle.setShopId(shopId);
          } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
          }
          if (vehicle.getId() != null && vehicle.getId() != 0) {
            writer.update(vehicle);
          } else {
            writer.save(vehicle);
            CustomerVehicle customerVehicle = new CustomerVehicle();
            customerVehicle.setCustomerId(customerId);
            customerVehicle.setVehicleId(vehicle.getId());
            writer.save(customerVehicle);
          }
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<VehicleDTO> saveOrUpdateCustomerVehicles(Long customerId, Long shopId, Long userId, List<VehicleDTO> vehicleDTOs) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<VehicleDTO> result = saveOrUpdateCustomerVehicles(customerId, shopId, userId, vehicleDTOs, writer);
      writer.commit(status);
      return result;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<VehicleDTO> saveOrUpdateCustomerVehicles(Long customerId, Long shopId, Long userId, List<VehicleDTO> vehicleDTOs, UserWriter writer) throws BcgogoException {
    List<VehicleDTO> vehicleDTOsReturn = new ArrayList<VehicleDTO>();
    if (CollectionUtils.isNotEmpty(vehicleDTOs)) {
      List<Long> vehicleIdList = new ArrayList<Long>();
      for (VehicleDTO vehicleDTO : vehicleDTOs) {
        if (StringUtils.isBlank(vehicleDTO.getLicenceNo())) {
          continue;
        } else {
          List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(shopId, vehicleDTO.getLicenceNo());
          if (vehicleList != null && vehicleList.size() > 0) {
            Long vehicleId = vehicleList.get(vehicleList.size() - 1).getId();
            vehicleDTO.setId(vehicleId);
          }
        }
        Vehicle vehicle = new Vehicle();
        VehicleModifyLogDTO oldLog = new VehicleModifyLogDTO();
        if (vehicleDTO.getId() != null && !(new Long(0L)).equals(vehicleDTO.getId())) {
          vehicle = writer.getById(Vehicle.class, vehicleDTO.getId());
          oldLog.setBrand(vehicle.getBrand());
          oldLog.setModel(vehicle.getModel());
        }

        vehicle.setLicenceNo(vehicleDTO.getLicenceNo());
        vehicle.setLicenceNoRevert(new StringBuffer(vehicleDTO.getLicenceNo()).reverse().toString().toUpperCase());
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setYear(vehicleDTO.getYear());
        vehicle.setEngine(vehicleDTO.getEngine());
        vehicle.setStartMileage(vehicleDTO.getStartMileage());
        vehicle.setObdMileage(vehicleDTO.getObdMileage());
        if (vehicleDTO.getMileageLastUpdateTime() != null) {
          vehicle.setMileageLastUpdateTime(vehicleDTO.getMileageLastUpdateTime());
        }
        vehicle.setChassisNumber(vehicleDTO.getChassisNumber());
        vehicle.setEngineNo(vehicleDTO.getEngineNo());
        vehicle.setCarDate(vehicleDTO.getCarDate());
        vehicle.setShopId(shopId);
        vehicle.setContact(vehicleDTO.getContact());
        vehicle.setMobile(vehicleDTO.getMobile());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setGsmObdImei(vehicleDTO.getGsmObdImei());
        vehicle.setGsmObdImeiMoblie(vehicleDTO.getGsmObdImeiMoblie());
        if (vehicle.getId() != null && vehicle.getId() != 0) {
          writer.update(vehicle);
          VehicleModifyLogDTO newLog = new VehicleModifyLogDTO();
          newLog.setBrand(vehicle.getBrand());
          newLog.setModel(vehicle.getModel());
          List<VehicleModifyLogDTO> logDTOs = VehicleModifyLogDTO.compare(oldLog, newLog);
          for (VehicleModifyLogDTO dto : logDTOs) {
            dto.setShopId(shopId);
            dto.setUserId(userId);
            dto.setVehicleId(vehicle.getId());
            dto.setOperationType(VehicleModifyOperations.REPAIR_WASH);
          }
          ServiceManager.getService(ICustomerService.class).batchCreateVehicleModifyLog(logDTOs);
          vehicleDTO = vehicle.toDTO();
          vehicleDTOsReturn.add(vehicleDTO);
        } else {
          writer.save(vehicle);
          CustomerVehicle customerVehicle = new CustomerVehicle();
          customerVehicle.setMaintainMileage(vehicleDTO.getMaintainMileage());
          customerVehicle.setMaintainTime(vehicleDTO.getMaintainTime());
          customerVehicle.setInsureTime(vehicleDTO.getInsureTime());
          customerVehicle.setExamineTime(vehicleDTO.getExamineTime());
          customerVehicle.setMaintainMileagePeriod(vehicleDTO.getMaintainMileagePeriod());
          vehicleDTO = vehicle.toDTO();
          vehicleDTOsReturn.add(vehicleDTO);
          customerVehicle.setCustomerId(customerId);
          customerVehicle.setVehicleId(vehicle.getId());
          writer.save(customerVehicle);
        }
        vehicleIdList.add(vehicle.getId());
      }
      //做solr
      if (CollectionUtils.isNotEmpty(vehicleIdList)) {
        try {
          ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicleIdList.toArray(new Long[vehicleIdList.size()]));
        } catch (Exception e) {
          LOG.error("shopId:{}", shopId);
          LOG.error("vehicleId:{}", StringUtil.arrayToStr(",", vehicleIdList.toArray(new Long[vehicleIdList.size()])));
          LOG.error("createVehicleSolrIndex 失败！", e);
        }
      }
    }
    return vehicleDTOsReturn;
  }

  public Long saveOrUpdateApointServices(AppointServiceDTO appointServiceDTO) throws BcgogoException, ParseException {
    UserWriter userWriter = userDaoManager.getWriter();
    AppointService appointService = AppointService.fromDTO(appointServiceDTO);
    Object status = userWriter.begin();
    try {
      if (NumberUtil.isNumber(appointServiceDTO.getIdStr())) {
        AppointService service = userWriter.getById(AppointService.class, NumberUtil.longValue(appointServiceDTO.getIdStr()));
        if (service == null || AppointService.AppointServiceStatus.DISABLED.equals(service.getStatus())) {
          LOG.info("预约服务不存在！appointServiceId={}", appointServiceDTO.getIdStr());
          return null;
        }
        service.setAppointName(appointService.getAppointName());
        service.setAppointDate(appointService.getAppointDate());
        service.setStatus(appointService.getStatus());
        userWriter.update(service);
      } else {
        if (StringUtil.isEmpty(appointServiceDTO.getAppointName()) || "服务名称".equals(appointServiceDTO.getAppointName())
          || StringUtil.isEmpty(appointServiceDTO.getAppointDate())) {
          return null;
        }
        userWriter.save(appointService);
      }
      //to save customerServiceJob
      CustomerServiceJob customerServiceJob = CollectionUtil.getFirst(userWriter.getCustomerServiceJobByAppointServiceId(appointService.getShopId(), appointService.getId()));
      if (customerServiceJob != null) {
        customerServiceJob.setAppointName(appointService.getAppointName());
        customerServiceJob.setRemindTime(appointService.getAppointDate());
        customerServiceJob.setRemindType(UserConstant.APPOINT_SERVICE);
        if (AppointService.AppointServiceStatus.DISABLED.equals(appointService.getStatus())) {
          customerServiceJob.setStatus(UserConstant.Status.CANCELED);
        } else {
          customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        }
        userWriter.update(customerServiceJob);
      } else {
        customerServiceJob = new CustomerServiceJob();
        customerServiceJob.setAppointServiceId(appointService.getId());
        customerServiceJob.setCustomerId(appointService.getCustomerId());
        customerServiceJob.setVehicleId(appointService.getVehicleId());
        customerServiceJob.setRemindTime(appointService.getAppointDate());
        customerServiceJob.setRemindType(UserConstant.APPOINT_SERVICE);
        customerServiceJob.setAppointName(appointService.getAppointName());
        customerServiceJob.setShopId(appointService.getShopId());
        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        userWriter.save(customerServiceJob);
      }
      userWriter.commit(status);
      return appointService.getId();
    } catch (Exception e) {
      LOG.error("更新预约服务异常！", e);
      throw new BcgogoException(e);
    } finally {
      userWriter.rollback(status);
    }
  }

  public CustomerVehicleDTO addYuyueToCustomerVehicle(AppointServiceDTO appointServiceDTO) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Long customerId = NumberUtil.longValue(appointServiceDTO.getCustomerId());
    Long shopId = appointServiceDTO.getShopId();
    Long vehicleId = NumberUtil.longValue(appointServiceDTO.getVehicleId());
    Customer customer = getCustomerByCustomerId(customerId, shopId);
    if (customerId != null && customer == null) {
      LOG.error("添加预约服务出错, customerId为 {} 的customer不存在!", customerId);
      return null;
    }
    CustomerVehicle customerVehicle = writer.getVehicleAppointment(customerId, vehicleId);
    if (customerVehicle == null) return null;
    boolean maintainTimeFlag = false;//customerServiceJob 更新的flag，true表示已经更新了
    boolean insureTimeFlag = false;
    boolean examineTimeFlag = false;
    Object status = writer.begin();
    try {
      String maintainTimeStr = appointServiceDTO.getMaintainTimeStr();
      String insureTimeStr = appointServiceDTO.getInsureTimeStr();
      String examineTimeStr = appointServiceDTO.getExamineTimeStr();
      Long maintainMileage = appointServiceDTO.getMaintainMileage();
      customerVehicle.setMaintainTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", maintainTimeStr));
      customerVehicle.setInsureTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", insureTimeStr));
      customerVehicle.setExamineTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", examineTimeStr));
      customerVehicle.setMaintainMileage(maintainMileage);
      customerVehicle.setMaintainMileagePeriod(appointServiceDTO.getMaintainMileagePeriod());

      Double[] intervals = ConfigUtils.getAppVehicleMaintainMileageIntervals();

      Double obdMileage = appointServiceDTO.getObdMileage();

      boolean maintainMileageFlag = true;
      if (obdMileage != null && maintainMileage != null && obdMileage - maintainMileage >= intervals[0] && obdMileage - maintainMileage <= intervals[1]) {
        maintainMileageFlag = false;
      }

      writer.update(customerVehicle);
      List<CustomerServiceJob> customerServiceJobList = writer.getCustomerServiceJobByCustomerIdAndVehicleId(shopId, customerId, vehicleId);
      if (CollectionUtils.isNotEmpty(customerServiceJobList)) {
        for (CustomerServiceJob customerServiceJob : customerServiceJobList) {
          if (StringUtil.isEmpty(maintainTimeStr) && maintainMileageFlag && UserConstant.MAINTAIN_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", maintainTimeStr));     //为空时逻辑删除customerServiceJob
            customerServiceJob.setStatus(UserConstant.Status.CANCELED);
            customerServiceJob.setRemindMileage(maintainMileage);
            writer.update(customerServiceJob);
            maintainTimeFlag = true;
            continue;
          } else if (UserConstant.MAINTAIN_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", maintainTimeStr));
            customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
            customerServiceJob.setRemindMileage(maintainMileage);
            writer.update(customerServiceJob);
            maintainTimeFlag = true;
            continue;
          }
          if (StringUtil.isEmpty(insureTimeStr) && UserConstant.INSURE_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", maintainTimeStr));
            customerServiceJob.setStatus(UserConstant.Status.CANCELED);
            writer.update(customerServiceJob);
            insureTimeFlag = true;
            continue;
          } else if (UserConstant.INSURE_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", insureTimeStr));
            customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
            writer.update(customerServiceJob);
            insureTimeFlag = true;
            continue;
          }
          if (StringUtil.isEmpty(examineTimeStr) && UserConstant.EXAMINE_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", maintainTimeStr));
            customerServiceJob.setStatus(UserConstant.Status.CANCELED);
            writer.update(customerServiceJob);
            examineTimeFlag = true;
            continue;
          } else if (UserConstant.EXAMINE_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", examineTimeStr));
            customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
            writer.update(customerServiceJob);
            examineTimeFlag = true;
            continue;
          }
        }
      }

      if (!maintainTimeFlag && (StringUtil.isNotEmpty(maintainTimeStr) || (!maintainMileageFlag))) {
        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
        customerServiceJob.setCustomerId(customerId);
        customerServiceJob.setVehicleId(vehicleId);
        customerServiceJob.setRemindType(UserConstant.MAINTAIN_TIME);
        customerServiceJob.setAppointName(UserConstant.CustomerRemindType.MAINTAIN_TIME);
        customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", maintainTimeStr));
        customerServiceJob.setRemindMileage(maintainMileage);
        customerServiceJob.setShopId(shopId);
        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        writer.save(customerServiceJob);
      }
      if (!insureTimeFlag && StringUtil.isNotEmpty(insureTimeStr)) {
        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
        customerServiceJob.setCustomerId(customerId);
        customerServiceJob.setVehicleId(vehicleId);
        customerServiceJob.setRemindType(UserConstant.INSURE_TIME);
        customerServiceJob.setAppointName(UserConstant.CustomerRemindType.INSURE_TIME);
        customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", insureTimeStr));
        customerServiceJob.setShopId(shopId);
        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        writer.save(customerServiceJob);
      }
      if (!examineTimeFlag && StringUtil.isNotEmpty(examineTimeStr)) {
        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
        customerServiceJob.setCustomerId(customerId);
        customerServiceJob.setVehicleId(vehicleId);
        customerServiceJob.setRemindType(UserConstant.EXAMINE_TIME);
        customerServiceJob.setAppointName(UserConstant.CustomerRemindType.EXAMINE_TIME);
        customerServiceJob.setRemindTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", examineTimeStr));
        customerServiceJob.setShopId(shopId);
        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        writer.save(customerServiceJob);
      }
      writer.commit(status);
      return customerVehicle.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  public List<CustomerDTO> getCustomerByBirth(Long birth) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
    List<Customer> customerList = writer.getCustomerByBirth(birth);
    for (Customer customer : customerList) {
      CustomerDTO customerDTO = customer.toDTO();
      setCustomerContacts(customerDTO.getId(), writer, customerDTO); // add by zhuj
      customerDTOList.add(customerDTO);
    }
    return customerDTOList;
  }

  public void saveCustomerBirthdayRemind(List<CustomerServiceJobDTO> customerServiceJobDTOList) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (CustomerServiceJobDTO customerServiceJobDTO : customerServiceJobDTOList) {
        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
        writer.save(customerServiceJob.fromDTO(customerServiceJobDTO));
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    } finally {
      writer.rollback(status);
    }
  }

  public void deleteCustomerBirthdayRemind(List<CustomerServiceJob> customerServiceJobList) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (CustomerServiceJob customerServiceJob : customerServiceJobList) {
        customerServiceJob = writer.getById(CustomerServiceJob.class, customerServiceJob.getId());
        writer.delete(customerServiceJob);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    } finally {
      writer.rollback(status);
    }

  }

  public List<CustomerServiceJob> getCustomerServiceJobByRemindType(Long remindType) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCustomerServiceJobByRemindType(remindType);
  }

  public void dropCustomerRemind(Long shopId, Long id) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    CustomerServiceJob customerServiceJob = writer.getById(CustomerServiceJob.class, id);
    try {
      if (customerServiceJob != null) {
        customerServiceJob.setStatus(UserConstant.Status.CANCELED);
        writer.update(customerServiceJob);
        writer.commit(status);
      }
    } catch (Exception e) {
      LOG.info(e.getMessage());
    } finally {
      writer.rollback(status);
    }
  }

  public void updateCustomerRemind(Long shopId, Long id) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    CustomerServiceJob customerServiceJob = writer.getById(CustomerServiceJob.class, id);
    try {
      if (customerServiceJob != null) {
        customerServiceJob.setStatus(UserConstant.Status.REMINDED);
        writer.update(customerServiceJob);
        writer.commit(status);
      }
    } catch (Exception e) {
      LOG.info(e.getMessage());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CustomerRecord getRecordByCustomerId(Long customerId) {
    UserWriter writer = userDaoManager.getWriter();
    return CollectionUtil.getFirst(writer.getCustomerRecordByCustomerId(customerId));
  }

  @Override
  public void updateCustomerRecord(CustomerRecord customerRecord) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (customerRecord != null) {
        writer.update(customerRecord);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  public String getNameByUserId(Long userId) {
    UserWriter writer = userDaoManager.getWriter();
    User user = writer.getById(User.class, userId);
    return user == null ? "" : user.getName();
  }

  public String getNameBySupplierId(Long supplierId) {
    UserWriter writer = userDaoManager.getWriter();
    Supplier supplier = writer.getById(Supplier.class, supplierId);
    return supplier == null ? "" : supplier.getName();
  }


  /**
   * customer Record初始化专用
   *
   * @param customerRecordDTO
   */
  public void updateCustomerRecordByMigration(CustomerRecordDTO customerRecordDTO) {
    UserWriter writer = userDaoManager.getWriter();

    if (customerRecordDTO != null && customerRecordDTO.getId() != null) {
      CustomerRecord customerRecord = writer.getById(CustomerRecord.class, customerRecordDTO.getId());
      if (customerRecord != null) {
        Object status = writer.begin();
        try {
          double oldTotalAmount = customerRecord.getTotalAmount();
          double newTotalAmount = customerRecordDTO.getTotalAmount();

          double oldDebtTotal = customerRecord.getTotalReceivable();
          double newDebtTotal = customerRecordDTO.getTotalReceivable();
          customerRecord.setTotalAmount(newTotalAmount);
          customerRecord.setTotalReceivable(newDebtTotal);
          writer.update(customerRecord);
          writer.commit(status);
          LOG.info("店面shop_id:" + customerRecordDTO.getShopId() + "CustomRecord,id为" + customerRecord.getId() + "被更改");
          LOG.info("totalAmount从" + oldTotalAmount + "改为" + newTotalAmount + ",totalReceivable从" + oldDebtTotal + "改为" + newDebtTotal);
        } finally {
          writer.rollback(status);
        }
      }
    }
  }

  /**
   * 对当天，昨日，当月客户记录进行排序
   *
   * @param shopId
   * @param timeType    //当天，昨日，当月
   * @param pager
   * @param orderByName 属性名称
   * @param orderByType 属性排序方式:asc.desc
   * @return
   */
  @Override
  public List<CustomerRecordDTO> getShopCustomerRecordDTO(Long shopId, String timeType, Pager pager, String orderByName, String orderByType) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    Long startTime = null;
    Long endTime = null;
    Sort sort = null;
    if (!StringUtil.isEmpty(orderByName) && !StringUtil.isEmpty(orderByType)) {
      sort = new Sort(orderByName, orderByType);
    }
    //当天新增客户记录 时间设置
    try {
      if (timeType != null && !"".equals(timeType)) {
        if (timeType.equals(UserConstant.SearchDate.TODAY)) {
          startTime = DateUtil.getStartTimeOfToday();
          endTime = DateUtil.getEndTimeOfToday();
        }
        //昨日新增客户记录      时间设置
        else if (timeType.equals(UserConstant.SearchDate.YESTERDAY)) {
          startTime = DateUtil.getStartTimeOfYesterday();
          endTime = DateUtil.getEndTimeOfYesterday();
          //当月客户新增记录         时间设置
        } else if (timeType.equals(UserConstant.SearchDate.MONTH)) {
          startTime = DateUtil.getFirstDayDateTimeOfMonth();
          endTime = DateUtil.getLastDayDateTimeOfMonth();
        }
        List<CustomerRecord> customerRecords = writer.getCustomerRecordByTime(shopId, startTime, endTime, pager, sort);
        if (customerRecords != null && customerRecords.size() > 0) {
          for (CustomerRecord customerRecord : customerRecords) {
            listCustomerRecordDTO.add(customerRecord.toDTO());
          }
        }
      } else {
        //全部客户记录
        listCustomerRecordDTO = getShopCustomerRecord(shopId, pager, sort);
      }
    } catch (ParseException e) {
      LOG.error(e.getMessage(), e);
    }
    return formatShopCustomerRecordDTO(listCustomerRecordDTO);
  }

  /**
   * 全部客户记录按属性排序
   *
   * @param shopId
   * @param pager
   * @param sort
   * @return
   * @author zhangchuanlong
   */
  private List<CustomerRecordDTO> getShopCustomerRecord(Long shopId, Pager pager, Sort sort) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerRecordDTO> listCustomerRecordDTO = new ArrayList<CustomerRecordDTO>();
    List<CustomerRecord> customerRecords = writer.getShopCustomerRecord(shopId, pager, sort);
    for (CustomerRecord customerRecord : customerRecords) {
      CustomerRecordDTO customerRecordDTO = customerRecord.toDTO();
      listCustomerRecordDTO.add(customerRecordDTO);
    }
    return listCustomerRecordDTO;
  }


  @Override
  public List<UserDTO> getUserByFuzzyUserNo(String userNo, int maxResults) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    List<User> users = writer.getUserByFuzzyUserNo(userNo, maxResults);
    List<UserDTO> userDTOs = null;
    if (users != null && users.size() > 0) {
      userDTOs = new ArrayList<UserDTO>();
      for (User user : users) {
        userDTOs.add(user.toDTO());
      }
    }
    return userDTOs;
  }

  //add by weilf 2012-08-16
  @Override
  public void deleteCustomerLicenceNo(Long shopId, Long vehicleId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Vehicle vehicle = writer.getVehicleById(shopId, vehicleId);
      if (vehicle != null) {
        vehicle.setStatus(VehicleStatus.DISABLED);
        writer.update(vehicle);
      }
      //做solr
      try {
        ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicle.getId());
      } catch (Exception e) {
        LOG.error("shopId:{}", shopId);
        LOG.error("vehicleId:{}", StringUtil.arrayToStr(",", vehicle.getId()));
        LOG.error("createVehicleSolrIndex 失败！", e);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据shop_id 员工状态 分页 读取数据
   *
   * @param shopId
   * @param salesManStatus
   * @param pager
   * @return
   */
  @Override
  public List<SalesManDTO> getSalesManDTOListByShopId(long shopId, SalesManStatus salesManStatus, Pager pager) {
    UserWriter writer = userDaoManager.getWriter();
    List<SalesMan> salesManList = writer.getSalesManDTOListByShopId(shopId, salesManStatus, pager);
    if (CollectionUtils.isEmpty(salesManList)) {
      return null;
    }
    List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();
    for (SalesMan salesMan : salesManList) {
      if (salesMan == null) {
        continue;
      }
      salesManDTOList.add(salesMan.toDTO());
    }
    return salesManDTOList;
  }

  public SalesManDTO saveOrUpdateSalesMan(SalesManDTO salesManDTO, UserWriter writer) throws Exception {
    //部门逻辑
    salesManDTO = checkDepartmentNameBeforeSave(salesManDTO, writer);
    if (salesManDTO.getId() == null || salesManDTO.getId() <= 0) {
      SalesMan salesMan = new SalesMan();
      salesMan = salesMan.fromDTO(salesManDTO, false);
      writer.save(salesMan);
      salesManDTO.setId(salesMan.getId());
      if (StringUtils.isNotBlank(salesManDTO.getUserNo()) && salesManDTO.getId() != null) {
        if (salesManDTO.getUserGroupId() != null) {
          //分配账号
          ServiceManager.getService(IUserCacheService.class).allocatedUserNoByStaff(salesManDTO.getShopId(), salesManDTO.getId(), salesManDTO.getUserNo(), salesManDTO.getUserGroupId(), null);
        } else {
          UserDTO userDTO = salesMan.toUserDTO();
          userDTO.setUserType(UserType.NORMAL);
          userDTO.setStatusEnum(null);
          userDTO.setUserNo(salesManDTO.getUserNo());
          User user = new User(userDTO);
          writer.save(user);
          ServiceManager.getService(IUserCacheService.class).updateUserNoByStaffId(salesManDTO.getId(), salesManDTO.getUserNo(), salesManDTO.getUserGroupId(), writer);
        }

      }

    } else {
      ServiceManager.getService(IUserCacheService.class).updateUserNoByStaffId(salesManDTO.getId(), salesManDTO.getUserNo(), salesManDTO.getUserGroupId(), writer);
      SalesMan salesMan = writer.getById(SalesMan.class, salesManDTO.getId());
      salesMan.fromDTO(salesManDTO, false);
      writer.update(salesMan);
      salesManDTO.setId(salesMan.getId());
    }
    return salesManDTO;
  }

  //保存或者更新数据
  @Override
  public SalesManDTO saveOrUpdateSalesMan(SalesManDTO salesManDTO) throws Exception {

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      salesManDTO = saveOrUpdateSalesMan(salesManDTO, writer);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return salesManDTO;
  }

  private SalesManDTO checkDepartmentNameBeforeSave(SalesManDTO salesManDTO, UserWriter writer) throws Exception {
    if (salesManDTO.getDepartmentId() != null) return salesManDTO;
    if (StringUtils.isBlank(salesManDTO.getDepartmentName()))
      throw new Exception("department is empty!");
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    boolean isDuplicate = userCacheService.checkDepartmentNameByShopId(salesManDTO.getDepartmentName(), salesManDTO.getShopId());
    //新增department
    if (!isDuplicate) {
      Department department = new Department();
      department.setName(salesManDTO.getDepartmentName());
      department.setShopId(salesManDTO.getShopId());
      department.setStatus(Status.active);
      writer.save(department);
      salesManDTO.setDepartmentId(department.getId());
      return salesManDTO;
    }
    List<DepartmentDTO> departmentDTOList = userCacheService.getDepartmentByName(salesManDTO.getShopId(), salesManDTO.getDepartmentName());
    if (departmentDTOList.size() > 2) {
      LOG.warn("department[name:{}shopId:{}] dirty data.", salesManDTO.getDepartmentName(), salesManDTO.getShopId());
    }
    salesManDTO.setDepartmentId(departmentDTOList.get(0).getId());
    return salesManDTO;
  }


  /**
   * 根据员工状态获得当前状态的员工总数
   *
   * @param shopId
   * @param salesManStatus
   * @return
   */
  @Override
  public int countSalesManByShopIdAndStatus(long shopId, SalesManStatus salesManStatus) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countSalesManByShopIdAndStatus(shopId, salesManStatus);
  }

  /**
   * 根据员工id获得该员工
   *
   * @param salesManId
   * @return
   */
  @Override
  public SalesManDTO getSalesManDTOById(long salesManId) {
    UserWriter writer = userDaoManager.getWriter();
    SalesMan salesMan = writer.getById(SalesMan.class, salesManId);
    if (salesMan != null) {
      SalesManDTO salesManDTO = salesMan.toDTO();
      if (salesMan.getDepartmentId() != null) {
        Department department = writer.getById(Department.class, salesMan.getDepartmentId());
        salesManDTO.setDepartmentName(department.getName());
      }
      if (salesMan.getUserGroupId() != null) {
        UserGroup userGroup = writer.getById(UserGroup.class, salesMan.getUserGroupId());
        salesManDTO.setUserGroupName(userGroup.getName());
      }
      UserDTO userDTO = writer.getUserByStaffId(salesManId);
      if (userDTO != null) {
        salesManDTO.setUserNo(userDTO.getUserNo());
        salesManDTO.setUserType(userDTO.getUserType().name());
      }
      return salesManDTO;
    }
    return null;
  }

  /**
   * 根据员工姓名 或者 编号 shop_id获得员工信息
   *
   * @param salesManCode
   * @param name
   * @param shopId
   * @return
   */
  @Override
  public List<SalesManDTO> getSalesManDTOByCodeOrName(String salesManCode, String name, long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();
    List<SalesMan> salesManList = writer.getSalesManDTOByCodeOrName(salesManCode, name, shopId);
    if (CollectionUtils.isEmpty(salesManList)) {
      return null;
    }
    for (SalesMan salesMan : salesManList) {
      if (salesMan == null) {
        continue;
      }
      SalesManDTO salesManDTO = salesMan.toDTO();
      salesManDTOList.add(salesManDTO);

    }
    return salesManDTOList;
  }


  @Override
  public List<User> getAllUser() throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    List<User> users = writer.getAllUser();
    if (users.size() > 0) {
      return users;
    }
    return null;
  }

  @Override
  public String changeMemberPassword(Long memberId, String oldPw, String newPw) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getById(Member.class, memberId);

    if (StringUtils.isNotBlank(oldPw)) {
      oldPw = EncryptionUtil.encryptPassword(oldPw, member.getShopId());
    } else {
      oldPw = "";
    }

    if (StringUtils.isBlank(member.getPassword())) {
      member.setPassword("");
    }

    if (!oldPw.equals(member.getPassword())) {
      return MemberConstant.MEMBER_PASSWORD_ERROR;
    }

    String key = "changeMemberPassword_" + memberId;
    Object status = writer.begin();

    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return null;
      if (StringUtils.isBlank(newPw)) {
        member.setPasswordStatus(PasswordValidateStatus.UNVALIDATE);
        newPw = null;
      } else {
        member.setPasswordStatus(PasswordValidateStatus.VALIDATE);
        newPw = EncryptionUtil.encryptPassword(newPw, member.getShopId());
      }
      member.setPassword(newPw);
      writer.update(member);
      writer.commit(status);
      return MemberConstant.CHANGE_PASSWORD_SUCCESS;
    } finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Override
  public SalesManDTO[] getSalesManList(Long shopId) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    List<SalesMan> salesMans = writer.getSaleManByShopIdAndOnJob(shopId, null);
    if (salesMans.size() > 0) {
      SalesManDTO[] salesManDTOs = new SalesManDTO[salesMans.size()];
      for (int i = 0; i < salesMans.size(); i++) {
        salesManDTOs[i] = salesMans.get(i).toDTO();
      }
      return salesManDTOs;
    }
    return null;
  }

  @Override
  public void rollBackMemberInfo(Long shopId, String accountMemberNo, Double memberAmount) throws Exception {
    if (accountMemberNo == null || "".equals(accountMemberNo) || memberAmount == null || memberAmount <= 0) return;
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getMemberByShopIdAndMemberNo(shopId, accountMemberNo);
    if (member == null) return;
    String key = "rollBackMemberInfo_" + shopId + "_" + accountMemberNo;

    Object status = writer.begin();
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return;
      member.setBalance(member.getBalance() + memberAmount);
      writer.update(member);
      writer.commit(status);
    } finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Override
  public List<User> getAllShopUser() throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    List<User> users = writer.getAllShopUser();
    if (users.size() > 0) {
      return users;
    }
    return null;
  }

  /**
   * 查询当天新增客户【车辆】历史记录数
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
  @Override
  public int countRepairOrderHistoryByNewVehicle(Long shopId, String vehicle, String services, String itemName, Long endDateLong, Long endDateLong2) throws ParseException {
    StopWatchUtil sw = new StopWatchUtil("countTodayNewUser");
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
//    SearchWriter writer = searchDaoManager.getWriter();
    /*获取当天新增【车辆车牌号】*/
    sw.stopAndStart("list");
    List<String> licenceNoList = vehicleService.getVehicleLicenceNos(shopId, DateUtil.getStartTimeOfToday(), DateUtil.getEndTimeOfToday());
    /*判断查询的车牌号是否是当天新增车牌号*/
    boolean flag = false;
    sw.stopAndStart("process");
    int result = 0;
    if (licenceNoList != null && licenceNoList.size() > 0) {
      /*判断查询车牌是否为null*/
      if (vehicle != null && !"".equals(vehicle)) {
        //如果vehicle不为null
        for (String s : licenceNoList) { //循环遍历车牌号是否在当日新增车牌号中
          if (vehicle.equals(s)) {
            flag = true;
          }
        }
      } else {      //如果vehicle为null
        result = searchService.countRepairOrderHistoryByToDayNewVehicle(shopId, vehicle, services, itemName,
          endDateLong, endDateLong2, licenceNoList);
        sw.stopAndPrintLog();
        return result;
      }
      if (flag) {//如果查询车牌在当天新增车牌号中查询
        result = searchService.countRepairOrderHistoryByToDayNewVehicle(shopId, vehicle, services, itemName,
          endDateLong, endDateLong2, licenceNoList);
        sw.stopAndPrintLog();
        return result;
      } else {     //如果vehicle不在当天新增车牌号中就返回null
        sw.stopAndPrintLog();
        return 0;
      }
    } else {
      sw.stopAndPrintLog();
      return 0;
    }
  }

  /**
   * 查询当天新增客户【车辆】历史记录
   *
   * @param shopId
   * @param vehicle       车牌号
   * @param services      施工项目
   * @param itemName      材料品名
   * @param startDateTIme
   * @param endDateTime
   * @param pager
   * @return
   * @author zhangchuanlong
   */
  @Override
  public List<ItemIndexDTO> getRepairOrderHistoryByNewVehicle(Long shopId, String vehicle, String services, String itemName, Long startDateTIme, Long endDateTime, Pager pager) throws BcgogoException, InvocationTargetException, IllegalAccessException, ParseException {
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    /*获取当天新增【车辆车牌号】*/
    List<String> licenceNoList = vehicleService.getVehicleLicenceNos(shopId, DateUtil.getStartTimeOfToday(), DateUtil.getEndTimeOfToday());
    /*判断查询的车牌号vehicle是否是当天新增车牌号*/
    boolean flag = false;
    if (licenceNoList != null && licenceNoList.size() > 0) {
      /*判断查询车牌是否为null*/
      if (vehicle != null && !"".equals(vehicle)) {
        //如果vehicle不为null
        for (String s : licenceNoList) { //循环遍历车牌号是否在当日新增车牌号中
          if (vehicle.equals(s)) {
            flag = true;
          }
        }
      } else {      //如果vehicle为null
        return searchService
          .getRepairOrderHistoryByTodayNewCustomer(shopId, vehicle, services, itemName, startDateTIme, endDateTime, pager, licenceNoList);
      }
      if (flag) {//如果查询车牌在当天新增车牌号中查询
        return searchService
          .getRepairOrderHistoryByTodayNewCustomer(shopId, vehicle, services, itemName, startDateTIme, endDateTime, pager, licenceNoList);
      } else {     //如果vehicle不在当天新增车牌号中就返回null
        return null;
      }
    } else {
      return null;
    }
  }

  @Override
  public List<CustomerRecordDTO> getShopCustomerRecordDTOBySolrSearchResult(List<CustomerSupplierSearchResultDTO> customerSuppliers) {
    List<CustomerRecordDTO> customerRecordDTOList = new ArrayList<CustomerRecordDTO>();
    List<CustomerRecordDTO> customerRecordDTOs = null;
    if (CollectionUtils.isEmpty(customerSuppliers)) return customerRecordDTOs;
    CustomerRecordDTO customerRecordDTO = null;
    for (CustomerSupplierSearchResultDTO searchResultDTO : customerSuppliers) {
      customerRecordDTOs = this.getCustomerRecordByCustomerId((searchResultDTO.getId()));
      if (CollectionUtils.isNotEmpty(customerRecordDTOs)) {
        customerRecordDTO = customerRecordDTOs.get(0);
        if (StringUtils.isNotEmpty(searchResultDTO.getMobile())) {
          customerRecordDTO.setMobile(searchResultDTO.getMobile());
        }
        if (!NumberUtil.isEqual(customerRecordDTO.getTotalReceivable(), searchResultDTO.getTotalDebt())) {
          customerRecordDTO.setTotalReceivable(NumberUtil.doubleVal(searchResultDTO.getTotalDebt()));
          LOG.warn("data error[customerId:{}]:customer record totalReceivable is not equals with solr totalDebt.", customerRecordDTO.getCustomerId());
        }
        if (!NumberUtil.isEqual(customerRecordDTO.getTotalAmount(), searchResultDTO.getTotalAmount())) {
          customerRecordDTO.setTotalAmount(searchResultDTO.getTotalAmount());
          LOG.warn("data error[customerId:{}]:customer record totalAmount is not equals with solr totalAmount.", customerRecordDTO.getCustomerId());
        }
        customerRecordDTO.setName(searchResultDTO.getName());
        customerRecordDTO.setType(searchResultDTO.getMemberType());
        customerRecordDTO.setMemberNo(searchResultDTO.getMemberNo());
        customerRecordDTO.setContact(searchResultDTO.getContact());
        customerRecordDTO.setRelationType(searchResultDTO.getRelationType());
        customerRecordDTO.setCustomerShopId(NumberUtil.longValue(searchResultDTO.getCustomerOrSupplierShopId()));
        customerRecordDTOList.add(customerRecordDTO);
      } else {
        LOG.warn("get customerRecord by customerId [id:{}] is null.", searchResultDTO.getId());
      }
    }
    return customerRecordDTOList;
  }


  @Override
  public void batchUpdateUserPassword(List<UserDTO> users) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Long userId = 0L;
    try {
      for (UserDTO user : users) {
        userId = user.getId();
        writer.updateUserPassword(user);
      }
      writer.commit(status);
    } catch (Exception e) {
      writer.rollback(status);
      LOG.error("更新ID为{}的user密码时出错.", userId);
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public CustomerDTO getCustomerWithMemberByMemberNoShopId(String memberNo, Long shopId) throws Exception {
    if (StringUtils.isBlank(memberNo) || shopId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getMemberByShopIdAndMemberNo(shopId, memberNo);
    if (member == null) {
      return null;
    }
    Customer customer = writer.getCustomerByCustomerIdAndShopId(member.getCustomerId(), shopId);
    if (customer == null) {
      return null;
    }
    CustomerDTO customerDTO = customer.toDTO();
    customerDTO.setMemberDTO(member.toDTO());
    setCustomerContacts(customerDTO.getId(), writer, customerDTO); // add by zhuj
    return customerDTO;
  }

  @Override
  public List<Vehicle> deleteVehicle(Long shopId, List<CustomerVehicleDTO> customerVehicleDTOList) {
    UserWriter writer = userDaoManager.getWriter();

    if (CollectionUtils.isEmpty(customerVehicleDTOList)) {
      return null;
    }

    Object status = writer.begin();
    List<Vehicle> vehicleList = new ArrayList<Vehicle>();
    try {
      for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOList) {
        if (null == customerVehicleDTO.getVehicleId()) {
          continue;
        }

        Vehicle vehicle = writer.getVehicleById(shopId, customerVehicleDTO.getVehicleId());

        vehicle.setStatus(VehicleStatus.DISABLED);

        writer.update(vehicle);

        vehicleList.add(vehicle);
      }

      writer.commit(status);

      return vehicleList;
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据shop_id 会员id 和会员消费 在作废时回退会员消费金额
   *
   * @param shopId
   * @param memberId
   * @param memberAmount
   * @throws Exception
   */
  @Override
  public void rollBackMemberInfo(Long shopId, Long memberId, Double memberAmount) throws Exception {
    if (memberId == null || NumberUtil.doubleVal(memberAmount) <= 0) return;
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getMemberDTOById(shopId, memberId);

    if (member == null) {
      LOG.error("UserService.java method=rollBackMemberInfo");
      LOG.error("参数:shop_id:" + shopId + ",memberId:" + memberId + ",memberAmount:" + memberAmount);
      LOG.error("根据参数获得会员失败");
      return;
    }
    String key = "rollBackMemberInfo_" + shopId + "_" + memberId;

    Object status = writer.begin();
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return;
      member.setBalance(member.getBalance() + memberAmount);
      writer.update(member);
      writer.commit(status);
    } finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Override
  public List<UserLimitDTO> getUserNecessaryLimitTags(Long shopId) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<UserLimitDTO> userLimitDTOList = new ArrayList<UserLimitDTO>();
    if (shopId == null) {
      return null;
    }
    String repairPickingSwitchShopVersions = configService.getConfig("REPAIR_PICK_SWITCH_SHOP_VERSIONS", ShopConstant.BC_SHOP_ID);
    ShopDTO shopDTO = configService.getShopById(shopId);
    UserWriter writer = userDaoManager.getWriter();
    List<UserLimit> userLimitList = writer.getUserNecessaryLimitTags();
    if (userLimitList != null) {
      for (UserLimit userLimit : userLimitList) {
        if ("REPAIR_PICKING".equals(userLimit.getScene())) {
          if (StringUtil.isNotEmpty(repairPickingSwitchShopVersions) && shopDTO.getShopVersionId() != null &&
            repairPickingSwitchShopVersions.indexOf(shopDTO.getShopVersionId().toString()) > -1) {
            userLimitDTOList.add(userLimit.toDTO());
          }
        } else {
          userLimitDTOList.add(userLimit.toDTO());
        }
      }
    }
    return userLimitDTOList;
  }

  @Override
  public UserSwitchDTO getUserSwitchByShopIdAndScene(Long shopId, String scene) {
    UserSwitchDTO userSwitchDTO = null;
    UserWriter writer = userDaoManager.getWriter();
    UserSwitch userSwitch = writer.getUserSwitchByShopIdAndScene(shopId, scene);
    if (userSwitch != null) {
      userSwitchDTO = userSwitch.toDTO();
    }
    return userSwitchDTO;
  }

  @Override
  public List<UserSwitchDTO> getUserSwitchListByShopId(Long shopId) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<UserSwitchDTO> userSwitchDTOList = new ArrayList<UserSwitchDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<UserSwitch> userSwitchList = writer.getUserSwitchListByShopId(shopId);
    if (userSwitchList == null || userSwitchList.size() == 0) {
      return null;
    }
    String repairPickingSwitchShopVersions = configService.getConfig("REPAIR_PICK_SWITCH_SHOP_VERSIONS", ShopConstant.BC_SHOP_ID);
    ShopDTO shopDTO = configService.getShopById(shopId);
    for (UserSwitch userSwitch : userSwitchList) {
      if ("REPAIR_PICKING".equals(userSwitch.getScene())) {
        if (StringUtil.isNotEmpty(repairPickingSwitchShopVersions) && shopDTO.getShopVersionId() != null &&
          repairPickingSwitchShopVersions.indexOf(shopDTO.getShopVersionId().toString()) > -1) {
          userSwitchDTOList.add(userSwitch.toDTO());
        }
      } else {
        userSwitchDTOList.add(userSwitch.toDTO());
      }
    }
    return userSwitchDTOList;
  }

  public Menu getMenu(MenuType m) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getMenu(MenuType.TXN_INVENTORY_MANAGE_REPAIR_PICKING);
  }

  @Override
  public List<UserSwitch> getUserSwitch(long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getUserSwitchMenuIdNotNull(shopId);
  }

  @Override
  public void addUserSwitch(Long shopId, UserLimitDTO userLimitDTO, Boolean isWholesalerShopVersion) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserSwitch userSwitch = new UserSwitch();
      userSwitch.setShopId(shopId);
      userSwitch.setScene(userLimitDTO.getScene());
      if (UserSwitchType.SCANNING_CARD.toString().equals(userLimitDTO.getScene())) {
        userSwitch.setStatus("ON");
      } else {
        if (isWholesalerShopVersion && UserSwitchType.SCANNING_BARCODE.toString().equals(userLimitDTO.getScene())) {
          userSwitch.setStatus("ON");
        } else {
          userSwitch.setStatus("OFF");
        }

      }
      if (UserSwitchType.SETTLED_REMINDER.toString().equals(userLimitDTO.getScene())) {
        userSwitch.setStatus("ON");
      }

      if (UserSwitchType.REPAIR_PICKING.getType().equals(userSwitch.getScene())) {
        Menu menu = this.getMenu(MenuType.TXN_INVENTORY_MANAGE_REPAIR_PICKING);
        if (menu != null) {
          userSwitch.setMenuId(menu.getId());
        }
      }
      writer.save(userSwitch);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public UserSwitchDTO saveOrUpdateUserSwitch(Long shopId, String scene, String status) {
    UserWriter writer = userDaoManager.getWriter();
    Object newStatus = writer.begin();
    UserSwitch userSwitch = writer.getUserSwitchByShopIdAndScene(shopId, scene);
    try {
      if (null != userSwitch) {
        userSwitch.setStatus(status);
        writer.update(userSwitch);
      } else {
        userSwitch = new UserSwitch();
        userSwitch.setStatus(status);
        userSwitch.setScene(scene);
        userSwitch.setShopId(shopId);
        writer.save(userSwitch);
      }
      writer.commit(newStatus);
      return userSwitch.toDTO();
    } finally {
      writer.rollback(newStatus);
    }
  }

  public boolean isMobileSwitchOn(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    UserSwitch userSwitch = writer.getUserSwitchByShopIdAndScene(shopId, UserSwitchType.MOBILE_HIDDEN.name());
    if (userSwitch == null) {
      return false;
    }
    if ("ON".equals(userSwitch.getStatus())) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isRepairPickingSwitchOn(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    UserSwitch userSwitch = writer.getUserSwitchByShopIdAndScene(shopId, UserSwitchType.REPAIR_PICKING.name());
    if (userSwitch == null) {
      return false;
    }
    if ("ON".equals(userSwitch.getStatus())) {
      return true;
    } else {
      return false;
    }
  }

  public List<Long> getCustomerIdByLicenceNo(long shopId, String licenceNo) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCustomerIdByLicenceNo(shopId, licenceNo);
  }

  @Override
  public CustomerRecordDTO getCustomerRecordDTOByCustomerIdAndShopId(Long shopId, Long customerId) {
    if (shopId == null || customerId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();

    CustomerRecord customerRecord = writer.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerId);

    if (null == customerRecord) {
      return null;
    }

    return customerRecord.toDTO();
  }


  @Override
  public CustomerDTO getCustomerByCustomerShopIdAndShopId(Long shopId, Long customerShopId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customerList = writer.getCustomerByCustomerShopIdAndShopId(shopId, customerShopId);
    if (CollectionUtils.isNotEmpty(customerList)) {
      CustomerDTO customerDTO = customerList.get(0).toDTO();
      setCustomerContacts(customerDTO.getId(), writer, customerDTO); //add by zhuj
      return customerDTO;
    }
    return null;
  }

  @Override
  public SupplierDTO getSupplierDTOBySupplierShopIdAndShopId(Long shopId, Long supplierShopId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> supplierList = writer.getSupplierBySupplierShopIdAndShopId(shopId, supplierShopId);
    if (CollectionUtils.isNotEmpty(supplierList)) {
      SupplierDTO supplierDTO = supplierList.get(0).toDTO();
      setSupplierContacts(supplierDTO.getId(), writer, supplierDTO); //add by zhuj
      return supplierDTO;
    }
    return null;
  }

  @Override
  public List<Long> getCustomerIdsByNameWithFuzzyQuery(Long shopId, String customerName) {
    UserWriter writer = userDaoManager.getWriter();
    List<Long> idList = writer.getCustomerIdsByNameWithFuzzyQuery(shopId, customerName);
    return idList;
  }

  @Override
  public List<Long> getSupplierIdsByNameWithFuzzyQuery(Long shopId, String supplierName) {
    UserWriter writer = userDaoManager.getWriter();
    List<Long> idList = writer.getSupplierIdsByNameWithFuzzyQuery(shopId, supplierName);
    return idList;
  }

  @Override
  public List<SupplierDTO> getRelatedSuppliersByShopId(Long shopId) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<Supplier> suppliers = userWriter.getRelatedSuppliersByShopId(shopId);
    if (CollectionUtil.isEmpty(suppliers)) {
      return null;
    }
    List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
    for (Supplier supplier : suppliers) {
      if (null == supplier) {
        continue;
      }
      supplierDTOs.add(supplier.toDTO());
    }
    return supplierDTOs;
  }

  public Set<Long> getRelatedSuppliersIdsByShopId(Long shopId) {
    List<SupplierDTO> supplierDTOList = getRelatedSuppliersByShopId(shopId);
    Set<Long> supplierShopIdSet = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(supplierDTOList)) {
      for (SupplierDTO supplierDTO : supplierDTOList) {
        supplierShopIdSet.add(supplierDTO.getSupplierShopId());
      }
    }
    return supplierShopIdSet;
  }


  @Override
  public List<Long> getRelatedSupplierIdListByShopId(Long shopId) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<Supplier> suppliers = userWriter.getRelatedSuppliersByShopId(shopId);
    if (CollectionUtil.isEmpty(suppliers)) {
      return null;
    }
    List<Long> idList = new ArrayList<Long>();
    for (Supplier supplier : suppliers) {
      if (null == supplier) {
        continue;
      }
      idList.add(supplier.getId());
    }
    return idList;
  }

  public SalesMan getSalesManByName(Long shopId, String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();

    return writer.getSalesManByName(shopId, name);
  }

  @Override
  public Map<String, SalesManDTO> getSalesManDTOMap(Long shopId, Set<String> names) {
    if (shopId == null || CollectionUtils.isEmpty(names)) {
      return new HashMap<String, SalesManDTO>();
    }
    Map<String, SalesManDTO> salesManDTOMap = new HashMap<String, SalesManDTO>();
    List<SalesMan> salesManList = userDaoManager.getWriter().getSalesManByNames(shopId, names);
    if (CollectionUtils.isNotEmpty(salesManList)) {
      for (SalesMan salesMan : salesManList) {
        salesManDTOMap.put(salesMan.getName(), salesMan.toDTO());
      }
    }
    return salesManDTOMap;
  }

  @Override
  public List<CustomerServiceJobDTO> getCustomerServiceRemindByCondition(long shopId, Boolean isOverdue, Boolean hasRemind, Long currentTime, int pageNo, int pageSize) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerServiceJobDTO> customerServiceJobDTOList = new ArrayList<CustomerServiceJobDTO>();
    List<Object[]> resultList = writer.getCustomerServiceRemindByCondition(shopId, isOverdue, hasRemind, currentTime, pageNo, pageSize);
    if (CollectionUtils.isNotEmpty(resultList)) {
      for (int i = 0; i < resultList.size(); i++) {
        CustomerServiceJobDTO customerServiceJobDTO = new CustomerServiceJobDTO();
        customerServiceJobDTO.setId(((BigInteger) resultList.get(i)[0]).longValue());
        customerServiceJobDTO.setCustomerId(((BigInteger) resultList.get(i)[1]).longValue());
        customerServiceJobDTO.setLicenceNo((String) resultList.get(i)[2]);
        customerServiceJobDTO.setRemindTime(((BigInteger) resultList.get(i)[3]).longValue());
        customerServiceJobDTO.setStatus((String) resultList.get(i)[4]);
        customerServiceJobDTO.setRemindType(((BigInteger) resultList.get(i)[5]).longValue());
        customerServiceJobDTO.setRemindTypeStr((String) resultList.get(i)[6]);
        customerServiceJobDTO.setCustomerName((String) resultList.get(i)[7]);
        customerServiceJobDTO.setContact((String) resultList.get(i)[8]);
        customerServiceJobDTO.setMobile((String) resultList.get(i)[9]);
        customerServiceJobDTOList.add(customerServiceJobDTO);
      }
    }
    return customerServiceJobDTOList;
  }

  @Override
  public int countCustomerServiceRemindByCondition(long shopId, Boolean isOverdue, Boolean hasRemind, Long currentTime) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countCustomerServiceRemindByCondition(shopId, isOverdue, hasRemind, currentTime);
  }

  @Override
  public List<InsuranceCompanyDTO> getAllInsuranceCompanyDTOs() {
    return userDaoManager.getWriter().getAllInsuranceCompanyDTOs();
  }

  @Override
  public InsuranceCompany getInsuranceCompanyDTOById(Long id) {
    if (id == null) return null;
    return userDaoManager.getWriter().getById(InsuranceCompany.class, id);
  }

  @Override
  public List<CustomerVehicleDTO> getCustomerVehicleDTO(Long... vehicleIds) {
    List<CustomerVehicle> customerVehicles = userDaoManager.getWriter().getCustomerVehicle(vehicleIds);
    List<CustomerVehicleDTO> customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
    if (CollectionUtil.isNotEmpty(customerVehicles)) {
      for (CustomerVehicle customerVehicle : customerVehicles) {
        customerVehicleDTOs.add(customerVehicle.toDTO());
      }
    }
    return customerVehicleDTOs;
  }

  @Override
  public CustomerVehicleDTO getCustomerVehicleDTOByVehicleIdAndCustomerId(Long vehicleId, Long customerId) {
    List<CustomerVehicle> customerVehicles = userDaoManager.getWriter().getCustomerVehicleDTOByVehicleIdAndCustomerId(vehicleId, customerId);
    if (CollectionUtils.isNotEmpty(customerVehicles)) {
      return customerVehicles.get(0).toDTO();
    } else {
      return null;
    }
  }


  public Map<Long, MemberDTO> getMemberByIds(Long shopId, Set<Long> orderId) {
    UserWriter userWriter = userDaoManager.getWriter();

    List<Member> memberList = userWriter.getMemberByIds(shopId, orderId);
    if (CollectionUtils.isEmpty(memberList)) {
      return new HashMap<Long, MemberDTO>();
    }
    Map<Long, MemberDTO> map = new HashMap<Long, MemberDTO>();
    for (Member member : memberList) {
      map.put(member.getCustomerId(), member.toDTO());
    }
    return map;
  }


  @Override
  public List<CustomerServiceJob> getAllCustomerServiceJob() {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getAllCustomerServiceJob();
  }

  @Override
  public CustomerServiceJobDTO getCustomerServiceJobById(Long id) {
    UserWriter writer = userDaoManager.getWriter();
    CustomerServiceJob customerServiceJob = writer.getById(CustomerServiceJob.class, id);
    if (customerServiceJob != null) {
      return customerServiceJob.toDTO();
    } else {
      return null;
    }
  }

  @Override
  public AppointServiceDTO getAppointServiceById(Long id) {
    UserWriter writer = userDaoManager.getWriter();
    AppointService appointService = writer.getById(AppointService.class, id);
    if (appointService != null) {
      return appointService.toDTO();
    } else {
      return null;
    }
  }

  @Override
  public CustomerDTO createRelationCustomer(ShopDTO supplierShopDTO, ShopDTO customerShopDTO, RelationTypes relationType) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    CustomerDTO customerDTO = new CustomerDTO();
    try {
      CustomerDTO searchCondition = new CustomerDTO();
      searchCondition.fromCustomerShopDTO(customerShopDTO);
      searchCondition.setShopId(supplierShopDTO.getId());

      customerDTO.setShopId(supplierShopDTO.getId());
      customerDTO.fromCustomerShopDTO(customerShopDTO);
      customerDTO.setRelationType(relationType);
      Customer customer = new Customer(customerDTO);
      writer.save(customer);
      customerDTO.setId(customer.getId());

      if (!ArrayUtils.isEmpty(customerDTO.getContacts())) {
        for (ContactDTO contactDTO : customerDTO.getContacts()) {
          if (contactDTO == null) {
            continue;
          }
          contactDTO.setIsShopOwner(ContactConstant.NOT_SHOP_OWNER);
          contactDTO.setShopId(supplierShopDTO.getId());
          contactDTO.setCustomerId(customer.getId());
          writer.save(new Contact().fromDTO(contactDTO));
        }
      }

      CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
      customerRecordDTO.fromCustomerDTO(customerDTO);
      CustomerRecord customerRecord = new CustomerRecord();
      customerRecord.fromDTO(customerRecordDTO);
      writer.save(customerRecord);
      writer.commit(status);

    } catch (Exception e) {
      LOG.error("UserService.createRelationCustomer error" + e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
    return customerDTO;
  }

  @Override
  public Map<String, VehicleDTO> getVehicleDTOMap(Long shopId) {
    Map<String, VehicleDTO> vehicleDTOMap = new HashMap<String, VehicleDTO>();

    if (null == shopId) {
      return vehicleDTOMap;
    }

    UserWriter writer = userDaoManager.getWriter();

    List<Vehicle> vehicleList = writer.getVehicleByShopId(shopId);

    if (CollectionUtils.isNotEmpty(vehicleList)) {
      for (Vehicle vehicle : vehicleList) {
        if (StringUtils.isEmpty(vehicle.getLicenceNo())) {
          continue;
        }

        vehicleDTOMap.put(vehicle.getLicenceNo(), vehicle.toDTO());
      }
    }

    return vehicleDTOMap;
  }

  @Override
  public Map<String, MemberDTO> getMemberMap(Long shopId) {
    Map<String, MemberDTO> map = new HashMap<String, MemberDTO>();

    if (null == shopId) {
      return map;
    }

    UserWriter writer = userDaoManager.getWriter();

    List<Member> memberList = writer.getMemberByShopId(shopId);

    if (CollectionUtils.isNotEmpty(memberList)) {
      for (Member member : memberList) {
        if (StringUtils.isBlank(member.getMemberNo())) {
          continue;
        }

        map.put(member.getMemberNo(), member.toDTO());
      }
    }

    return map;
  }

  @Override
  public List<UserDTO> getAllUserByShopId(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();

    List<User> userList = writer.getAllUserByShopId(shopId);
    if (CollectionUtils.isEmpty(userList)) {
      return null;
    }
    List<UserDTO> listUserDTO = new ArrayList<UserDTO>();
    for (User user : userList) {
      listUserDTO.add(user.toDTO());
    }
    return listUserDTO;
  }

  @Override
  public void saveUserSwitch(Long shopId, String scene, String stat) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserSwitch userSwitch = new UserSwitch();
      userSwitch.setShopId(shopId);
      userSwitch.setScene(scene);
      userSwitch.setStatus(stat);
      writer.save(userSwitch);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  public List<DepartmentDTO> getAllDepartmentsByShopId(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    List<Department> departmentList = writer.getDepartmentsByShopId(shopId);
    if (CollectionUtils.isEmpty(departmentList)) {
      return null;
    }
    List<DepartmentDTO> departmentDTOList = new ArrayList<DepartmentDTO>();

    for (Department department : departmentList) {
      departmentDTOList.add(department.toDTO());
    }
    return departmentDTOList;

  }

  @Override
  public void initContactBugFix() {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Customer> customers = writer.getCustomerToInitContact();
      if (CollectionUtils.isNotEmpty(customers)) {
        for (Customer customer : customers) {
          Contact contact = new Contact();
          contact.setCustomerId(customer.getId());
          contact.setShopId(customer.getShopId());
          contact.setName(customer.getContact());
          contact.setMobile(customer.getMobile());
          contact.setEmail(customer.getEmail());
          contact.setQq(customer.getQq());
          contact.setDisabled(ContactConstant.ENABLED);
          contact.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          contact.setLevel(ContactConstant.LEVEL_0);
          contact.setSupplierId(customer.getSupplierId());
          writer.save(contact);
        }
      }
      List<Supplier> suppliers = writer.getSupplierToInitContact();
      if (CollectionUtils.isNotEmpty(suppliers)) {
        for (Supplier supplier : suppliers) {
          Contact contact = new Contact();
          contact.setSupplierId(supplier.getId());
          contact.setShopId(supplier.getShopId());
          contact.setName(supplier.getContact());
          contact.setMobile(supplier.getMobile());
          contact.setEmail(supplier.getEmail());
          contact.setQq(supplier.getQq());
          contact.setDisabled(ContactConstant.ENABLED);
          contact.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          contact.setLevel(ContactConstant.LEVEL_0);
          contact.setCustomerId(supplier.getCustomerId());
          writer.save(contact);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 关联成功后创建客户和供应商的经营范围
   *
   * @param customerDTO
   * @param supplierDTO
   */
  public void createCustomerSupplierBusinessScope(CustomerDTO customerDTO, SupplierDTO supplierDTO) {

    if ((customerDTO == null || customerDTO.getCustomerShopId() == null)
      && (supplierDTO == null || supplierDTO.getSupplierShopId() == null)) {
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {
      if (customerDTO != null && customerDTO.getCustomerShopId() != null) {
        writer.deleteCustomerSupplierBusinessScope(customerDTO.getShopId(), customerDTO.getId(), null);
        List<Long> customerShopCategoryIds = configService.getShopBusinessScopeProductCategoryIdListByShopId(customerDTO.getCustomerShopId());
        if (CollectionUtils.isNotEmpty(customerShopCategoryIds)) {
          for (Long productCategoryId : customerShopCategoryIds) {
            BusinessScope businessScope = new BusinessScope();
            businessScope.setCustomerId(customerDTO.getId());
            businessScope.setShopId(customerDTO.getShopId());
            businessScope.setProductCategoryId(productCategoryId);
            writer.save(businessScope);
          }
          customerDTO.setThirdCategoryIds(customerShopCategoryIds);
        }
      }

      if (supplierDTO != null && supplierDTO.getSupplierShopId() != null) {
        writer.deleteCustomerSupplierBusinessScope(supplierDTO.getShopId(), null, supplierDTO.getId());
        List<Long> supplierShopCategoryIds = configService.getShopBusinessScopeProductCategoryIdListByShopId(supplierDTO.getSupplierShopId());
        if (CollectionUtils.isNotEmpty(supplierShopCategoryIds)) {
          for (Long productCategoryId : supplierShopCategoryIds) {
            BusinessScope businessScope = new BusinessScope();
            businessScope.setSupplierId(supplierDTO.getId());
            businessScope.setShopId(supplierDTO.getShopId());
            businessScope.setProductCategoryId(productCategoryId);
            writer.save(businessScope);
          }
          supplierDTO.setThirdCategoryIds(supplierShopCategoryIds);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 更新客户或者供应商的经营范围
   *
   * @param customerDTO
   * @param supplierDTO
   */
  public void updateCustomerSupplierBusinessScope(CustomerDTO customerDTO, SupplierDTO supplierDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();

    try {
      if (customerDTO != null) {
        Customer customer = writer.getById(Customer.class, customerDTO.getId());
        if (customer != null) {
          customer.setBusinessScope(customerDTO.getBusinessScopeStr());
          writer.update(customer);
        }
      }
      if (supplierDTO != null) {
        Supplier supplier = writer.getById(Supplier.class, supplierDTO.getId());
        if (supplier != null) {
          supplier.setBusinessScope(supplierDTO.getBusinessScope());
          writer.update(supplier);
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);

    }


  }


  /**
   * 保存或者更改客户或者供应商的经营范围（只保存三级分类）
   *
   * @param customerDTO
   * @param supplierDTO
   * @param thirdCategoryIdStr
   */
  public void saveOrUpdateCustomerSupplierBusinessScope(CustomerDTO customerDTO, SupplierDTO supplierDTO, String thirdCategoryIdStr) {
    if (customerDTO == null && supplierDTO == null) {
      return;
    }
    Long shopId = customerDTO == null ? supplierDTO.getShopId() : customerDTO.getShopId();
    Long customerId = customerDTO == null ? null : customerDTO.getId();
    Long supplierId = supplierDTO == null ? null : supplierDTO.getId();

    if (customerId == null && supplierDTO != null && supplierDTO.getCustomerId() != null) {
      customerId = supplierDTO.getCustomerId();
    }

    if (supplierId == null && customerDTO != null && customerDTO.getSupplierId() != null) {
      supplierId = customerDTO.getSupplierId();
    }

    List<Long> thirdCategoryIds = new ArrayList<Long>();
    if (shopId == null) {
      return;
    }

    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      if (customerId != null) {
        userWriter.deleteCustomerSupplierBusinessScope(shopId, customerId, null);
      }
      if (supplierId != null) {
        userWriter.deleteCustomerSupplierBusinessScope(shopId, null, supplierId);
      }


      if (StringUtils.isNotEmpty(thirdCategoryIdStr)) {
        String[] thirdCategoryIdArray = thirdCategoryIdStr.split(",");
        if (!ArrayUtil.isEmpty(thirdCategoryIdArray)) {
          for (String categoryIdStr : thirdCategoryIdArray) {
            if (!NumberUtil.isLongNumber(categoryIdStr)) {
              continue;
            }
            if (customerId != null) {
              BusinessScope businessScope = new BusinessScope();
              businessScope.setShopId(shopId);
              businessScope.setCustomerId(customerId);
              businessScope.setSupplierId(null);
              businessScope.setProductCategoryId(Long.valueOf(categoryIdStr));
              userWriter.save(businessScope);
            }
            if (supplierId != null) {
              BusinessScope supplierBusinessScope = new BusinessScope();
              supplierBusinessScope.setShopId(shopId);
              supplierBusinessScope.setCustomerId(null);
              supplierBusinessScope.setSupplierId(supplierId);
              supplierBusinessScope.setProductCategoryId(Long.valueOf(categoryIdStr));
              userWriter.save(supplierBusinessScope);
            }

            thirdCategoryIds.add(Long.valueOf(categoryIdStr));
          }
        }
      }
      userWriter.commit(status);
    } finally {
      userWriter.rollback(status);
    }

    if (customerDTO != null) {
      customerDTO.setThirdCategoryIds(thirdCategoryIds);
    }

    if (supplierDTO != null) {
      supplierDTO.setThirdCategoryIds(thirdCategoryIds);
    }
  }

  /**
   * 根据客户或者供应商的id获取经营范围
   *
   * @param shopId
   * @param customerId
   * @param supplierId
   * @return
   */
  public List<BusinessScopeDTO> getCustomerSupplierBusinessScope(Long shopId, Long customerId, Long supplierId) {
    UserWriter userWriter = userDaoManager.getWriter();

    List<BusinessScopeDTO> businessScopeDTOList = new ArrayList<BusinessScopeDTO>();

    List<BusinessScope> businessScopeList = userWriter.getCustomerSupplierBusinessScope(shopId, customerId, supplierId);
    if (CollectionUtils.isEmpty(businessScopeList)) {
      return businessScopeDTOList;
    }

    for (BusinessScope businessScope : businessScopeList) {
      businessScopeDTOList.add(businessScope.toDTO());
    }
    return businessScopeDTOList;
  }

  /**
   * 根据客户或者供应商的id获取经营范围
   *
   * @param shopId
   * @param customerIdSet
   * @param supplierIdSet
   * @return
   */
  public List<BusinessScopeDTO> getCustomerSupplierBusinessScope(Long shopId, Set<Long> customerIdSet, Set<Long> supplierIdSet) {
    UserWriter userWriter = userDaoManager.getWriter();

    List<BusinessScopeDTO> businessScopeDTOList = new ArrayList<BusinessScopeDTO>();

    List<BusinessScope> businessScopeList = userWriter.getCustomerSupplierBusinessScope(shopId, customerIdSet, supplierIdSet);
    if (CollectionUtils.isEmpty(businessScopeList)) {
      return businessScopeDTOList;
    }

    for (BusinessScope businessScope : businessScopeList) {
      businessScopeDTOList.add(businessScope.toDTO());
    }
    return businessScopeDTOList;
  }

  @Override
  public Result resetSystemCreatedPassword(Long shopId) throws Exception {
    Result result = new Result(false);
    String password = null;
    if (shopId == null) {
      result.setMsg("shop id is null.");
      return result;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      User user = writer.getSystemCreatedUser(shopId);
      if (user == null) {
        result.setMsg("未找到系统用户！");
        return result;
      }
      if (!RegexUtils.isMobile(user.getMobile())) {
        result.setMsg("系统用户手机号不正确！");
        return result;
      }
      password = generatePassword();
      user.setPassword(EncryptionUtil.encryptPassword(password, shopId));
      writer.update(user);
      UserDTO userDTO = user.toDTO();
      userDTO.setPasswordWithoutEncrypt(password);
      ServiceManager.getService(ISmsService.class).sendResetPasswordSMS(shopId, userDTO);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    result.setMsg("密码修改成功！[" + password + "]");
    result.setSuccess(true);
    return result;
  }

  @Override
  public Result changeSystemCreatedUserNo(Long shopId, String managerUserNo) throws Exception {
    Result result = new Result(false);
    if (shopId == null) {
      result.setMsg("shop id is null.");
      return result;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      User user = writer.getSystemCreatedUser(shopId);
      if (user == null) {
        result.setMsg("未找到系统用户！");
        return result;
      }
      if (!RegexUtils.isMobile(user.getMobile())) {
        result.setMsg("系统用户手机号不正确！");
        return result;
      }
      if (user.getUserNo().equals(managerUserNo)) {
        result.setMsg("新账号与原始账号相同！");
        return result;
      }
      if (writer.isUserNoExistedInSystem(managerUserNo)) {
        result.setMsg("此账号已被注册！");
        return result;
      }
      String password = generatePassword();
      user.setPassword(EncryptionUtil.encryptPassword(password, shopId));
      user.setUserNo(managerUserNo);
      writer.update(user);
      UserDTO userDTO = user.toDTO();
      userDTO.setPasswordWithoutEncrypt(password);
      ServiceManager.getService(ISmsService.class).sendChangeUserNoSMS(shopId, userDTO);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    result.setMsg("账号修改成功！");
    result.setSuccess(true);
    return result;
  }

  @Override
  public Map<String, VehicleDTO> getVehicleMapByLicenceNos(Long shopId, Set<String> licenceNos) {
    UserWriter writer = userDaoManager.getWriter();
    List<Vehicle> vehicles = writer.getVehicleMapByLicenceNos(shopId, licenceNos);
    if (CollectionUtils.isEmpty(vehicles)) {
      return new HashMap<String, VehicleDTO>();
    }
    Map<String, VehicleDTO> vehicleDTOMap = new HashMap<String, VehicleDTO>();
    for (Vehicle vehicle : vehicles) {
      if (vehicleDTOMap.get(vehicle.getLicenceNo()) == null) {
        vehicleDTOMap.put(vehicle.getLicenceNo(), vehicle.toDTO());
      }
    }
    return vehicleDTOMap;
  }

  @Override
  public void deleteCustomerVehicles(Long shopId, Set<Long> toDeleteVehicleIds) {
    if (CollectionUtils.isEmpty(toDeleteVehicleIds)) {
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Long[] ids = new Long[toDeleteVehicleIds.size()];
      List<Vehicle> vehicles = writer.getVehiclesByIds(shopId, toDeleteVehicleIds.toArray(ids));
      for (Vehicle vehicle : vehicles) {
        vehicle.setStatus(VehicleStatus.DISABLED);
        writer.update(vehicle);
      }
      writer.commit(status);
      //做solr
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, toDeleteVehicleIds.toArray(ids));
    } catch (Exception e) {
      LOG.error("UserSErvice.delelteCustomerVehicles error.", e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteVehiclesByCarDTOs(Long shopId, Long customerId, CarDTO[] carDTOs) {
    List<CustomerVehicleDTO> dbCustomerVehicleDTOs = getVehicleByCustomerId(customerId);
    Set<Long> toDeleteVehicleIds = new HashSet<Long>();
    Set<Long> existVehicleIds = new HashSet<Long>();
    if (ArrayUtils.isEmpty(carDTOs)) {
      for (CustomerVehicleDTO customerVehicleDTO : dbCustomerVehicleDTOs) {
        toDeleteVehicleIds.add(customerVehicleDTO.getVehicleId());
      }
    } else {
      for (CustomerVehicleDTO customerVehicleDTO : dbCustomerVehicleDTOs) {
        toDeleteVehicleIds.add(customerVehicleDTO.getVehicleId());
      }

      for (CarDTO carDTO : carDTOs) {
        if (StringUtils.isNotBlank(carDTO.getId()) && NumberUtil.isNumber(carDTO.getId())) {
          existVehicleIds.add(Long.parseLong(carDTO.getId()));
        }
      }

      toDeleteVehicleIds.removeAll(existVehicleIds);
    }
    deleteCustomerVehicles(shopId, toDeleteVehicleIds);
  }

  @Override
  public void saveCusOrSupOrderIndexSchedule(CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO) {
    UserWriter writer = userDaoManager.getWriter();
    CusOrSupOrderIndexSchedule cusOrSupOrderIndexSchedule = new CusOrSupOrderIndexSchedule(cusOrSupOrderIndexScheduleDTO);
    Object status = writer.begin();
    try {
      writer.save(cusOrSupOrderIndexSchedule);
      cusOrSupOrderIndexScheduleDTO.setId(cusOrSupOrderIndexSchedule.getId());
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<CusOrSupOrderIndexSchedule> getCusOrSupOrderIndexScheduleDTOByCusOrSupId(Long shopId, Long customerId, Long supplierId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCusOrSupOrderIndexScheduleDTOByCusOrSupId(shopId, customerId, supplierId);
  }

  @Override
  public void updateCusOrSupOrderIndexScheduleStatusById(Long id, ExeStatus exeStatus) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      CusOrSupOrderIndexSchedule cusOrSupOrderIndexSchedule = writer.getById(CusOrSupOrderIndexSchedule.class, id);
      if (ExeStatus.FINISHED.equals(exeStatus)) {
        cusOrSupOrderIndexSchedule.setFinishedTime(System.currentTimeMillis());
      }
      cusOrSupOrderIndexSchedule.setExeStatus(exeStatus);
      writer.update(cusOrSupOrderIndexSchedule);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void generateCustomerOrderIndexScheduleDTO(Long shopId, CustomerDTO customerDTO) {
    //判断是否已经存在有ready状态的任务，如果有，则不插入，以此防止用户频繁的更改区域
    List<CusOrSupOrderIndexSchedule> cusOrSupOrderIndexSchedules = this.getCusOrSupOrderIndexScheduleDTOByCusOrSupId(shopId, customerDTO.getId(), null);
    if (CollectionUtils.isEmpty(cusOrSupOrderIndexSchedules)) {
      CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO = new CusOrSupOrderIndexScheduleDTO();
      cusOrSupOrderIndexScheduleDTO.setShopId(shopId);
      cusOrSupOrderIndexScheduleDTO.setCustomerId(customerDTO.getId());
      cusOrSupOrderIndexScheduleDTO.setCreatedTime(System.currentTimeMillis());
      cusOrSupOrderIndexScheduleDTO.setExeStatus(ExeStatus.READY);
      this.saveCusOrSupOrderIndexSchedule(cusOrSupOrderIndexScheduleDTO);
    }
  }

  @Override
  public void generateSupplierOrderIndexScheduleDTO(Long shopId, SupplierDTO supplierDTO) {
    List<CusOrSupOrderIndexSchedule> cusOrSupOrderIndexSchedules = this.getCusOrSupOrderIndexScheduleDTOByCusOrSupId(shopId, null, supplierDTO.getId());
    if (CollectionUtils.isEmpty(cusOrSupOrderIndexSchedules)) {
      CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO = new CusOrSupOrderIndexScheduleDTO();
      cusOrSupOrderIndexScheduleDTO.setShopId(shopId);
      cusOrSupOrderIndexScheduleDTO.setSupplierId(supplierDTO.getId());
      cusOrSupOrderIndexScheduleDTO.setCreatedTime(System.currentTimeMillis());
      cusOrSupOrderIndexScheduleDTO.setExeStatus(ExeStatus.READY);
      this.saveCusOrSupOrderIndexSchedule(cusOrSupOrderIndexScheduleDTO);
    }
  }

  @Override
  public List<CusOrSupOrderIndexScheduleDTO> getCusOrSupOrderIndexScheduleDTOs() {
    UserWriter writer = userDaoManager.getWriter();
    List<CusOrSupOrderIndexScheduleDTO> cusOrSupOrderIndexScheduleDTOs = new ArrayList<CusOrSupOrderIndexScheduleDTO>();
    List<CusOrSupOrderIndexSchedule> cusOrSupOrderIndexScheduleList = writer.getCusOrSupOrderIndexScheduleDTOs();
    if (CollectionUtils.isNotEmpty(cusOrSupOrderIndexScheduleList)) {
      for (CusOrSupOrderIndexSchedule cusOrSupOrderIndexSchedule : cusOrSupOrderIndexScheduleList) {
        cusOrSupOrderIndexScheduleDTOs.add(cusOrSupOrderIndexSchedule.toDTO());
      }
    }
    return cusOrSupOrderIndexScheduleDTOs;
  }


  @Override
  public int countSupplierBySupplierShopId(Long supplierShopId) {
    if (supplierShopId == null) {
      throw new RuntimeException("countSupplierBySupplierShopId：supplierShopId should not be null.");
    }
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.countSupplierBySupplierShopId(supplierShopId);  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void saveCustomerServiceCategoryRelation(Long shopId, CustomerDTO customerDTO) {
    if (shopId == null || customerDTO == null || customerDTO.getId() == null) return;
    if (StringUtils.isNotBlank(customerDTO.getServiceCategoryRelationIdStr())) {
      String[] serviceCategoryRelationIds = customerDTO.getServiceCategoryRelationIdStr().split(",");
      if (!ArrayUtils.isEmpty(serviceCategoryRelationIds)) {
        StringBuilder sb = new StringBuilder();
        UserWriter writer = userDaoManager.getWriter();
        Object status = writer.begin();
        try {
          //先删除
          writer.deleteServiceCategoryRelation(shopId, customerDTO.getId());
          //后保存
          for (String serviceCategoryRelationId : serviceCategoryRelationIds) {
            if (StringUtils.isNotBlank(serviceCategoryRelationId)) {
              ServiceCategoryRelation serviceCategoryRelation = new ServiceCategoryRelation();
              serviceCategoryRelation.setDataType(ServiceCategoryDataType.CUSTOMER);
              serviceCategoryRelation.setDataId(customerDTO.getId());
              serviceCategoryRelation.setServiceCategoryId(Long.valueOf(serviceCategoryRelationId));
              serviceCategoryRelation.setShopId(shopId);
              writer.save(serviceCategoryRelation);
              ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(serviceCategoryRelation.getServiceCategoryId());
              sb.append(serviceCategoryDTO.getName()).append(",");
            }
          }
          writer.commit(status);
        } finally {
          writer.rollback(status);
        }
        customerDTO.setServiceCategoryRelationContent(sb.length() > 1 ? sb.substring(0, sb.length() - 1) : null);

      }
    }
  }

  @Override
  public void saveCustomerVehicleBrandModelRelation(UserWriter writer, Long shopId, CustomerDTO customerDTO, Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap) {
    if (shopId == null || customerDTO == null || customerDTO.getId() == null || MapUtils.isEmpty(shopVehicleBrandModelDTOMap))
      return;
    if (VehicleSelectBrandModel.ALL_MODEL.equals(customerDTO.getSelectBrandModel())) {
      customerDTO.setVehicleModelContent("全部车型");
    }
    if (StringUtils.isNotBlank(customerDTO.getVehicleModelIdStr())) {
      String[] vehicleModelIds = customerDTO.getVehicleModelIdStr().split(",");
      if (!ArrayUtils.isEmpty(vehicleModelIds)) {
        StringBuilder sb = new StringBuilder();
        List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();

        Customer customer = writer.getById(Customer.class, customerDTO.getId());
        customer.setSelectBrandModel(customerDTO.getSelectBrandModel());
        writer.update(customer);
        //先删除
        writer.deleteVehicleBrandModelRelation(shopId, customerDTO.getId());
        //后保存
        if (VehicleSelectBrandModel.PART_MODEL.equals(customerDTO.getSelectBrandModel())) {
          for (String vehicleModelId : vehicleModelIds) {
            if (StringUtils.isNotBlank(vehicleModelId)) {
              ShopVehicleBrandModelDTO shopVehicleBrandModelDTO = shopVehicleBrandModelDTOMap.get(Long.valueOf(vehicleModelId));
              VehicleBrandModelRelation vehicleBrandModelRelation = new VehicleBrandModelRelation();
              vehicleBrandModelRelation.setDataType(VehicleBrandModelDataType.CUSTOMER);
              vehicleBrandModelRelation.setDataId(customerDTO.getId());
              vehicleBrandModelRelation.setModelId(Long.valueOf(vehicleModelId));
              vehicleBrandModelRelation.setShopId(shopId);
              vehicleBrandModelRelation.setModelId(shopVehicleBrandModelDTO.getModelId());
              vehicleBrandModelRelation.setModelName(shopVehicleBrandModelDTO.getModelName());
              vehicleBrandModelRelation.setBrandId(shopVehicleBrandModelDTO.getBrandId());
              vehicleBrandModelRelation.setBrandName(shopVehicleBrandModelDTO.getBrandName());
              vehicleBrandModelRelation.setFirstLetter(shopVehicleBrandModelDTO.getFirstLetter());
              writer.save(vehicleBrandModelRelation);
              sb.append(shopVehicleBrandModelDTO.getModelName()).append(",");
              shopVehicleBrandModelDTOList.add(shopVehicleBrandModelDTO);
            }
          }
          customerDTO.setVehicleModelContent(sb.length() > 1 ? sb.substring(0, sb.length() - 1) : null);
          customerDTO.setShopVehicleBrandModelDTOList(shopVehicleBrandModelDTOList);
        }
      }
    }

  }

  @Override
  public void saveCustomerVehicleBrandModelRelation(Long shopId, CustomerDTO customerDTO, Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap) {
    if (shopId == null || customerDTO == null || customerDTO.getId() == null || MapUtils.isEmpty(shopVehicleBrandModelDTOMap))
      return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      saveCustomerVehicleBrandModelRelation(writer, shopId, customerDTO, shopVehicleBrandModelDTOMap);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean isOBDCustomer(Long customerId) {
    boolean result = false;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      result = writer.isOBDCustomer(customerId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public Map<Long, Boolean> isOBDCustomer(List<Long> customerId) {
    Long[] ids = new Long[customerId.size()];
    ids = customerId.toArray(ids);
    Map<Long, Boolean> result = new HashMap<Long, Boolean>();
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Object[]> list = writer.isOBDCustomer(ids);
      for (Object[] array : list) {
        Long customer_id = ((BigInteger) array[0]).longValue();
        int obd_count = ((BigInteger) array[1]).intValue();
        result.put(customer_id, obd_count > 0);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public List<ShopAuditLogDTO> getShopAuditLogDTOListByShopIdAndStatus(Long shopId, AuditStatus auditStatus) {
    IShopAuditLogService shopAuditLogService = ServiceManager.getService(IShopAuditLogService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<ShopAuditLogDTO> shopAuditLogDTOList = shopAuditLogService.getShopAuditLogDTOListByShopIdAndStatus(shopId, auditStatus);
    if (CollectionUtil.isNotEmpty(shopAuditLogDTOList)) {
      for (ShopAuditLogDTO shopAuditLogDTO : shopAuditLogDTOList) {
        Long userId = shopAuditLogDTO.getAuditorId();
        if (userId != null) {
          UserDTO userDTO = userService.getUserByUserId(userId);
          if (userDTO != null) {
            shopAuditLogDTO.setAuditorName(userDTO.getName());
          }
        }
      }
    }
    return shopAuditLogDTOList;
  }

  public List<SalesManDTO> getSalesManByDepartmentId(Long shopId, Long departmentId) {
    List<SalesMan> salesMans = userDaoManager.getWriter().getSalesManByDepartmentId(shopId, departmentId);
    List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();
    if (CollectionUtils.isEmpty(salesMans)) {
      return salesManDTOList;
    }

    for (SalesMan salesMan : salesMans) {
      salesManDTOList.add(salesMan.toDTO());
    }
    return salesManDTOList;

  }

  public DepartmentDTO getDepartmentById(Long departmentId) {
    if (departmentId == null) {
      return null;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Department department = userWriter.getById(Department.class, departmentId);
    return department == null ? null : department.toDTO();
  }

  public List<DepartmentDTO> getDepartmentNameByShopIdName(Long shopId, String name) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<DepartmentDTO> departmentDTOs = new ArrayList<DepartmentDTO>();
    List<Department> departments = userWriter.getDepartmentNameByShopIdName(shopId, name);

    if (CollectionUtil.isEmpty(departments)) {
      return departmentDTOs;
    }

    for (Department department : departments) {
      departmentDTOs.add(department.toDTO());
    }

    return departmentDTOs;
  }

  @Override
  public Map<Long, Boolean> isAppUserByCustomerId(List<Long> customerIdList) {
    Map<Long, Boolean> result = new HashMap<Long, Boolean>();
    UserWriter writer = userDaoManager.getWriter();
    List<Object[]> list = writer.isAppUserByCustomerId(customerIdList);
    for (Object[] array : list) {
      Long customer_id = ((Long) array[0]).longValue();
      int obd_count = ((Long) array[1]).intValue();
      result.put(customer_id, obd_count > 0);
    }
    return result;
  }


  @Override
  public Map<Long, ContactDTO> getContactDTOMapByIdFormContactVehicle(Long shopId, Long... ids) {
    if (shopId == null || ArrayUtil.isEmpty(ids)) return null;
    Map<Long, ContactDTO> contactDTOMap = new HashMap<Long, ContactDTO>();
    List<ContactDTO> contactDTOs = getContactDTOByIdFormContactVehicle(shopId, ids);
    if (CollectionUtil.isNotEmpty(contactDTOs)) {
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO == null || StringUtil.isEmpty(contactDTO.getMobile())) {
          continue;
        }
        contactDTOMap.put(contactDTO.getId(), contactDTO);
      }
    }
    return contactDTOMap;
  }

  @Override
  public List<ContactDTO> getContactDTOByIdFormContactVehicle(Long shopId, Long... ids) {
    if (shopId == null || ArrayUtil.isEmpty(ids)) return null;
    List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
    List<Long> allIdList = new ArrayList(Arrays.asList(ids));
    List<ContactDTO> contactDTOList = this.getContactDTOByIds(shopId, ids);
    if (CollectionUtils.isNotEmpty(contactDTOList)) {
      String specialIdStr = null;
      for (ContactDTO contactDTO : contactDTOList) {
        if (contactDTO.getCustomerId() != null) {
          specialIdStr = SolrIdPrefix.CUSTOMER.toString() + "_" + contactDTO.getId();
        } else if (contactDTO.getSupplierId() != null) {
          specialIdStr = SolrIdPrefix.SUPPLIER.toString() + "_" + contactDTO.getId();
        } else {
          specialIdStr = SolrIdPrefix.OTHER.toString() + "_" + contactDTO.getId();
        }
        contactDTO.setSpecialIdStr(specialIdStr);
        contactDTOs.add(contactDTO);
        allIdList.remove(contactDTO.getId());
      }
    }
    if (CollectionUtils.isNotEmpty(allIdList)) {
      List<VehicleDTO> vehicleDTOList = this.getVehicleByIds(shopId, allIdList.toArray(new Long[allIdList.size()]));
      Map<Long, CustomerVehicleDTO> customerVehicleDTOMap = ServiceManager.getService(IVehicleService.class).getCustomerVehicleDTOMapByVehicleIds(allIdList.toArray(new Long[allIdList.size()]));
      if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
        for (VehicleDTO vehicleDTO : vehicleDTOList) {
          ContactDTO contactDTO = new ContactDTO(vehicleDTO.getContact(), vehicleDTO.getMobile());
          contactDTO.setId(vehicleDTO.getId());
          contactDTO.setSpecialIdStr(SolrIdPrefix.VEHICLE.toString() + "_" + vehicleDTO.getId());
          contactDTO.setVehicleContactFlag(true);
          CustomerVehicleDTO customerVehicleDTO = customerVehicleDTOMap.get(vehicleDTO.getId());
          if (customerVehicleDTO != null) contactDTO.setCustomerId(customerVehicleDTO.getCustomerId());
          contactDTOs.add(contactDTO);
        }
      }
    }
    return contactDTOs;
  }

  @Override
  public boolean hasMobileDumplicated(Long shopId, SmsDTO smsDTO) throws Exception {
    List<Long> contactIds = new ArrayList<Long>();
    List<String> mobiles = new ArrayList<String>();
    if (StringUtil.isNotEmpty(smsDTO.getContactIds())) {
      String[] tContactIds = smsDTO.getContactIds().split(",");
      for (String tContactId : tContactIds) {
        if (StringUtil.isEmpty(tContactId)) continue;
        Long contactId = null;
        if (tContactId.contains("_")) {
          contactId = NumberUtil.longValue(tContactId.split("_")[1]);
        } else {
          contactId = NumberUtil.longValue(tContactId);
        }
        if (contactIds.contains(contactId)) return true;
        contactIds.add(contactId);
      }
    }
    List<ContactDTO> contactDTOs = ServiceManager.getService(IUserService.class).getContactDTOByIdFormContactVehicle(shopId, ArrayUtil.toLongArr(contactIds));
    if (CollectionUtil.isNotEmpty(contactDTOs)) {
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO == null) {
          continue;
        }
        if (mobiles.contains(contactDTO.getMobile())) return true;
        mobiles.add(contactDTO.getMobile());
      }
    }
    if (StringUtil.isNotEmpty(smsDTO.getContactGroupIds())) {
      Long[] contactGroupIds = ArrayUtil.toLongArr(smsDTO.getContactGroupIds().split(","));
      smsDTO.setContactGroupDTOs(getContactGroupByIds(contactGroupIds));
    }
    if (CollectionUtil.isNotEmpty(smsDTO.getContactGroupDTOs())) {
      ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);
      CustomerSupplierSearchConditionDTO conditionDTO = new CustomerSupplierSearchConditionDTO();
      conditionDTO.setShopId(shopId);
      conditionDTO.setStart(0);
      conditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
      List<ContactGroupDTO> groupDTOs = smsDTO.getContactGroupDTOs();
      for (ContactGroupDTO groupDTO : groupDTOs) {
        conditionDTO.setContactGroupType(groupDTO.getContactGroupType());
        CustomerSupplierSearchResultListDTO resultListDTO = searchCustomerSupplierService.queryContact(conditionDTO);
        List<ContactDTO> contactDTOs2 = resultListDTO.getContactDTOList();
        if (CollectionUtil.isNotEmpty(contactDTOs2)) {
          for (ContactDTO contactDTO : contactDTOs2) {
            if (contactDTO == null) {
              continue;
            }
            if (contactIds.contains(contactDTO.getId()) || mobiles.contains(contactDTO.getMobile())) return true;
            contactIds.add(contactDTO.getId());
            mobiles.add(contactDTO.getMobile());
          }
        }
      }
    }
    return false;
  }

  @Override
  public User getUserBySalesManId(Long shopId, Long salesManId) {
    if (shopId == null || salesManId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    List<User> userList = writer.getUserBySalesManId(shopId, salesManId);
    if (CollectionUtils.isNotEmpty(userList)) {
      return userList.get(0);
    }
    return null;
  }

  public void saveOrUpdateCustomerVehicle(List<CustomerVehicleDTO> customerVehicleDTOs) {
    if (CollectionUtils.isEmpty(customerVehicleDTOs)) {
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOs) {
        CustomerVehicle customerVehicle = null;
        if (customerVehicleDTO.getId() != null) {
          customerVehicle = writer.getById(CustomerVehicle.class, customerVehicleDTO.getId());
          customerVehicle.fromDTO(customerVehicleDTO);
          writer.update(customerVehicle);
        } else {
          customerVehicle = new CustomerVehicle(customerVehicleDTO);
          writer.save(customerVehicle);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  public List<CustomerVehicleDTO> getCustomerVehicleDTOByCustomerId(Set<Long> customerIdSet) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerVehicle> customerVehicleList = writer.getCustomerVehicleByCustomerIds(customerIdSet);
    List<CustomerVehicleDTO> customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();

    if (CollectionUtils.isEmpty(customerVehicleList)) {
      return customerVehicleDTOs;
    }

    for (CustomerVehicle vehicle : customerVehicleList) {
      customerVehicleDTOs.add(vehicle.toDTO());
    }

    return customerVehicleDTOs;
  }

  /**
   * 不登录获取店铺信息
   *
   * @param finger
   * @return
   */
  @Override
  public Long getProbableShopByFinger(String finger) {
    UserWriter writer = userDaoManager.getWriter();
    List<UserClientInfo> clientInfos = writer.getProbableUserDTOByFinger(finger);
    if (CollectionUtil.isEmpty(clientInfos)) return null;
    Long shopId = null;
    for (UserClientInfo clientInfo : clientInfos) {
      if (shopId == null) {
        shopId = clientInfo.getShopId();
        continue;
      }
      if (!shopId.equals(clientInfo.getShopId())) {
        return null;
      }
    }
    return shopId;
  }


  @Override
  public Customer saveCustomer(CustomerDTO customerDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Customer customer = new Customer(customerDTO);
      writer.saveOrUpdate(customer);
      writer.commit(status);
      return customer;
   } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }finally {
      writer.rollback(status);
    }
    return null;
  }


  @Override
  public void saveCustomerVehicle(CustomerVehicle customerVehicle) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(customerVehicle);
      writer.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Vehicle saveVehicle(VehicleDTO vehicleDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Vehicle vehicle = new Vehicle(vehicleDTO);
      writer.saveOrUpdate(vehicle);
      writer.commit(status);
      return vehicle;
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    } finally {
      writer.rollback(status);
    }
    return null;
  }

  @Override
  public void saveOrUpdateAccidentSpecialist(AccidentSpecialistDTO... specialistDTOs) {
    if (ArrayUtil.isEmpty(specialistDTOs)) return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (AccidentSpecialistDTO specialistDTO : specialistDTOs) {
        AccidentSpecialist specialist = null;
        if (specialistDTO.getId() == null) {
          specialist = new AccidentSpecialist();
        } else {
          specialist = writer.getById(AccidentSpecialist.class, specialistDTO.getId());
        }
        specialist.fromDTO(specialistDTO);
        writer.saveOrUpdate(specialist);
        specialistDTO.setId(specialist.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<AccidentSpecialistDTO> getAccidentSpecialistByOpenId(Long shopId, String openId) {
    UserWriter writer = userDaoManager.getWriter();
    List<AccidentSpecialist> specialists = writer.getAccidentSpecialistByOpenId(shopId, openId);
    if (CollectionUtil.isEmpty(specialists)) return null;
    List<AccidentSpecialistDTO> specialistDTOs = new ArrayList<AccidentSpecialistDTO>();
    for (AccidentSpecialist specialist : specialists) {
      specialistDTOs.add(specialist.toDTO());
    }
    return specialistDTOs;
  }

  @Override
  public void handleDeadLock() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String trx_timeout_switch = configService.getConfig("trx_timeout_switch", ShopConstant.BC_SHOP_ID);
    LOG.info("trx_timeout_switch:{}", trx_timeout_switch);
    if (StringUtil.isEmpty(trx_timeout_switch) || "OFF".equals(trx_timeout_switch)) {
      return;
    }
    String trx_timeout_time = configService.getConfig("trx_timeout_time", ShopConstant.BC_SHOP_ID);
     LOG.info("trx_timeout_time:{}", trx_timeout_time);
    Long trx_timeout_time_stamp = Long.valueOf(trx_timeout_time) * 1000;
    Long timeoutTime = (System.currentTimeMillis() - trx_timeout_time_stamp);
    UserWriter writer = userDaoManager.getWriter();
    List<Map> trxList = writer.getInnodbTrx();
    for (Map trxMap : trxList) {
      Long trx_mysql_thread_id = NumberUtil.longValue(trxMap.get("trx_mysql_thread_id"));
      Long trx_started = NumberUtil.longValue(trxMap.get("trx_started"));
      LOG.info("trx_mysql_thread_id:{},trx_started:{}", trx_mysql_thread_id, DateUtil.convertDateLongToDateString(DateUtil.ALL, trx_started));
      if (trx_started > timeoutTime) {
        LOG.error("trx_mysql_thread_id:{} trx_timeout!", trx_mysql_thread_id);
        writer.killTrxMysqlThread(trx_mysql_thread_id);
        LOG.info("kill trx_mysql_thread_id:{} success;", trx_mysql_thread_id);
      }
    }

  }

}
