package com.bcgogo.user.service;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.CustomerResponse;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.merge.MergeCustomerSnap;
import com.bcgogo.user.merge.MergeLogUtil;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.SearchMergeResult;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.model.task.MergeTask;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.excelimport.customer.CustomerImporter;
import com.bcgogo.user.service.obd.IObdManagerService;
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

import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-7
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CustomerService implements ICustomerService {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

  /* public void saveSmsJob(ShopPlanDTO shopPlanDTO) {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    if (UserConstant.CustomerType.ALL_CUSTOMER.equals(shopPlanDTO.getCustomerType())) {
      //保存一条job到定时钟 ExecuteType为all
      SmsJobDTO smsJobDTO = shopPlanDTO.toSmsJobDTO();
      smsJobDTO.setExecuteType(UserConstant.CustomerType.ALL_CUSTOMER);   //区分全体顾客
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setSender(SenderType.Shop);
      notificationService.sendSmsAsync(smsJobDTO);
      return;
    }
    //通过shopPlanDTO 得到  mobiles 保存在定时钟  40一条保存在job中
    UserWriter writer = userDaoManager.getWriter();
    if (StringUtil.isEmpty(shopPlanDTO.getCustomerIds())) return;
    String[] customerIdsArray = shopPlanDTO.getCustomerIds().split(",");
    List<Long> customerIdsList = new ArrayList<Long>();
    for (String id : customerIdsArray) {
      customerIdsList.add(Long.valueOf(id));
    }
    List<String> mobiles = writer.getCustomersMobilesByCustomerIds(shopPlanDTO.getShopId(), customerIdsList);
    StringBuffer mobile = new StringBuffer();
    int size = mobiles.size();
    for (int i = 0; i < size; i++) {
      if (smsJobMobileIsFull(i, size, mobile.toString())) {
        mobile.append(mobiles.get(i));
        SmsJobDTO smsJobDTO = shopPlanDTO.toSmsJobDTO();
        smsJobDTO.setExecuteType(UserConstant.CustomerType.NORM_CUSTOMER);
        smsJobDTO.setReceiveMobile(mobile.toString());
        smsJobDTO.setStartTime(System.currentTimeMillis());
        smsJobDTO.setSender(SenderType.Shop);
        notificationService.sendSmsAsync(smsJobDTO);
        mobile = new StringBuffer();
      } else
        mobile.append(mobiles.get(i)).append(",");
    }
  }*/

  //判断手机长度不超过500
  private boolean smsJobMobileIsFull(int i, int size, String mobiles) {
    if ((mobiles.toString().length() > SmsConstant.SMS_SEND_MOBILES_MAX_LENGHT) || i == size - 1) {
      return true;
    }
    return false;
  }

  //通过shopId得到所有moblies
  public List<String> getCustomersPhonesByShopId(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    List<String> mobiles = writer.getCustomersMobilesByShopId(shopId);
    mobiles = CollectionUtil.filterBlankElements(mobiles);
    return mobiles;
  }

  @Override
  public List<CustomerDTO> getCustomersByShopIdSendInvitationCode(Long shopId, long startId, int pageSize, Long createTime) {
    List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customerList = writer.getCustomersByShopIdSendInvitationCode(shopId, startId, pageSize, createTime);
    if(CollectionUtils.isEmpty(customerList)){
      return new ArrayList<CustomerDTO>();
    }
    Set<Long> ids = new HashSet<Long>();
    for (Customer customer : customerList) {
      ids.add(customer.getId());
      customerDTOList.add(customer.toDTO());
    }
    List<Contact> contactList;
    List<ContactDTO> contactDTOList;
    Map<Long, List<Contact>> contactMap = writer.getContactsByCusIds(new ArrayList<Long>(ids));
    for (CustomerDTO dto : customerDTOList) {
      contactList = contactMap.get(dto.getId());
      if (CollectionUtil.isNotEmpty(contactList)) {
        contactDTOList = new ArrayList<ContactDTO>();
        for (Contact contact : contactList) {
          contactDTOList.add(contact.toDTO());
        }
        dto.setContacts(contactDTOList.toArray(new ContactDTO[contactDTOList.size()]));
      }
    }
    return customerDTOList;
  }

  public void updateVehicleBySalesOrder(UserWriter writer, Long shopId, SalesOrderDTO salesOrderDTO) {
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    if (salesOrderDTO.getVehicleId() == null && StringUtils.isNotEmpty(salesOrderDTO.getLicenceNo())) {
      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByLicenceNo(shopId, salesOrderDTO.getLicenceNo());
      if (vehicleDTO == null) { //新车
        vehicleDTO = new VehicleDTO();
        vehicleDTO.setLicenceNo(salesOrderDTO.getLicenceNo());
        vehicleDTO.setContact(salesOrderDTO.getVehicleContact());
        vehicleDTO.setMobile(salesOrderDTO.getVehicleMobile());
        vehicleDTO.setStatus(VehicleStatus.ENABLED);
        vehicleDTO.setShopId(shopId);
        Vehicle vehicle = new Vehicle(vehicleDTO);
        writer.save(vehicle);
        CustomerVehicle customerVehicle = new CustomerVehicle();
        customerVehicle.setCustomerId(salesOrderDTO.getCustomerId());
        customerVehicle.setVehicleId(vehicle.getId());
        vehicleDTO.setStatus(VehicleStatus.ENABLED);
        writer.save(customerVehicle);
        salesOrderDTO.setVehicleId(vehicle.getId());
      }
    } else if (salesOrderDTO.getVehicleId() != null) { //车辆信息修改
      Vehicle vehicle = writer.findById(Vehicle.class, salesOrderDTO.getVehicleId());
      if (vehicle != null) {
        vehicle.setMobile(salesOrderDTO.getVehicleMobile());
        vehicle.setContact(salesOrderDTO.getVehicleContact());
        writer.saveOrUpdate(vehicle);
      }
    }
  }

  @Override
  public void updateVehicleAndCustomer(SalesOrderDTO salesOrderDTO, Long shopId, String contact, Long payTime) throws Exception {
    IContactService contactService = ServiceManager.getService(IContactService.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    CustomerDTO customerDTO = null;
    try {
      if (salesOrderDTO.getCustomerId() != null || CustomerConstant.DEFAULT_CUSTOMER_NAME.equals(salesOrderDTO.getCustomer())) {
        if (salesOrderDTO.getCustomerId() != null) {
          customerDTO = getCustomerById(salesOrderDTO.getCustomerId(), salesOrderDTO.getShopId());
        } else {
          customerDTO = isCustomerExist(shopId, CustomerConstant.DEFAULT_CUSTOMER_NAME, null, null);
          if (customerDTO != null && customerDTO.getId() != null) {
            salesOrderDTO.setCustomerId(customerDTO.getId());
          }
        }
      }
      if (customerDTO != null) {
        boolean isHaveRelatedSupplier = customerDTO.getSupplierId() != null;
        customerDTO.updateFromSaleOrderDTO(salesOrderDTO);
        getUserService().updateCustomer(writer, customerDTO);
        if (isHaveRelatedSupplier) {
          SupplierDTO supplierDTO = CollectionUtil.getFirst(getUserService().getSupplierById(salesOrderDTO.getShopId(), customerDTO.getSupplierId()));
          if (supplierDTO != null) {
            supplierDTO.updateFromSaleOrderDTO(salesOrderDTO);
            getUserService().updateSupplier(writer, supplierDTO);

//            contactService.updateContactsBelongCustomerAndSupplier(writer, customerDTO.getId(), customerDTO.getSupplierId(), shopId, customerDTO.getContacts());

            if (customerDTO.isAddContacts()) {
              contactService.addContactsBelongCustomerAndSupplier(writer, customerDTO.getId(), customerDTO.getSupplierId(), shopId, customerDTO.getContacts());
            }
          }
        }


        boolean isHaveSameContact = false;
        if (salesOrderDTO.getContactId() != null && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
          for (ContactDTO contactDTO : customerDTO.getContacts()) {
            if (contactDTO != null && salesOrderDTO.getContactId().equals(contactDTO.getId())) {
              isHaveSameContact = true;
              break;
            }
          }
        }
        if (!isHaveSameContact && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
          ContactDTO contactDTO = customerDTO.getContacts()[0];
          if (contactDTO != null) {
            salesOrderDTO.setContactId(contactDTO.getId());
          }
        }
        //更新客户消费记录表 累计消费,最近消费
        List<CustomerRecord> customerRecords = writer.getShopCustomerRecordByCustomerId(salesOrderDTO.getCustomerId());
        if (null != customerRecords && customerRecords.size() > 0) {
          customerRecords.get(0).setRepayDate(payTime);
          if (customerRecords.get(0).getLastDate() == null || customerRecords.get(0).getLastDate() - salesOrderDTO.getVestDate() < 0) {
            customerRecords.get(0).setLastDate(salesOrderDTO.getVestDate() == null ? System.currentTimeMillis() : salesOrderDTO.getVestDate());
            customerRecords.get(0).setLastAmount(salesOrderDTO.getAfterMemberDiscountTotal() == null ? salesOrderDTO.getTotal() : salesOrderDTO.getAfterMemberDiscountTotal());
            customerRecords.get(0).setLastBill(this.combinateSaleOrderForCustomerLastBill(salesOrderDTO));
            customerRecords.get(0).setLastBillShort(this.combinateSaleOrderForCustomerLastBillShort(customerRecords.get(0).getLastBill()));
          }
          if (salesOrderDTO.getDebt() > 0.001) {
            customerRecords.get(0).setTotalReceivable(
                customerRecords.get(0).getTotalReceivable() + salesOrderDTO.getDebt());
            customerRecords.get(0).setRepayDate(payTime);
          }
          customerRecords.get(0).setTotalAmount(customerRecords.get(0).getTotalAmount() + salesOrderDTO.getSettledAmount() + salesOrderDTO.getDebt());
          customerRecords.get(0).setConsumeTimes(NumberUtil.longValue(customerRecords.get(0).getConsumeTimes()) + 1);
          if(NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()) > 0 || salesOrderDTO.getMemberDiscountRatio() != null) {
            customerRecords.get(0).setMemberConsumeTimes(NumberUtil.longValue(customerRecords.get(0).getMemberConsumeTimes()) + 1);
            customerRecords.get(0).setMemberConsumeTotal(NumberUtil.doubleVal(customerRecords.get(0).getMemberConsumeTotal()) + NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()));
          }

          customerRecords.get(0).setContact(contact);
          customerRecords.get(0).setName(salesOrderDTO.getCustomer());
          customerRecords.get(0).setMobile(salesOrderDTO.getMobile());
          writer.save(customerRecords.get(0));
        }
      }
      if (customerDTO == null) {
        customerDTO = new CustomerDTO();
        customerDTO.createFromSaleOrderDTO(salesOrderDTO);
        getUserService().createCustomer(writer, customerDTO);
        salesOrderDTO.setCustomerId(customerDTO.getId());
        if (!ArrayUtils.isEmpty(customerDTO.getContacts())
            && customerDTO.getContacts()[0] != null
            && customerDTO.getContacts()[0].getId() != null) {
          salesOrderDTO.setContactId(customerDTO.getContacts()[0].getId());
        }

        //创建客户消费记录表
        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setCustomerId(customerDTO.getId());
        customerRecord.setShopId(shopId);
        customerRecord.setName(salesOrderDTO.getCustomer());
        customerRecord.setMobile(salesOrderDTO.getMobile());
        customerRecord.setLicenceNo(salesOrderDTO.getLicenceNo());
        customerRecord.setBrand(salesOrderDTO.getBrand());
        customerRecord.setModel(salesOrderDTO.getModel());
        customerRecord.setYear(salesOrderDTO.getYear());
        customerRecord.setEngine(salesOrderDTO.getEngine());
        customerRecord.setLastDate(salesOrderDTO.getVestDate() == null ? System.currentTimeMillis() : salesOrderDTO.getVestDate());
        customerRecord.setLastAmount(salesOrderDTO.getAfterMemberDiscountTotal() == null ? salesOrderDTO.getTotal() : salesOrderDTO.getAfterMemberDiscountTotal());
        customerRecord.setTotalAmount(NumberUtil.doubleVal(salesOrderDTO.getSettledAmount()) + NumberUtil.doubleVal(salesOrderDTO.getDebt()));
        customerRecord.setConsumeTimes(1L);

        if (NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()) > 0 || salesOrderDTO.getMemberDiscountRatio() != null) {
          customerRecord.setMemberConsumeTimes(NumberUtil.longValue(customerRecord.getMemberConsumeTimes()) + 1);
          customerRecord.setMemberConsumeTotal(NumberUtil.doubleVal(customerRecord.getMemberConsumeTotal()) + salesOrderDTO.getMemberAmount());
        }
        customerRecord.setContact(contact);
        customerRecord.setLastBill(this.combinateSaleOrderForCustomerLastBill(salesOrderDTO));
        customerRecord.setLastBillShort(this.combinateSaleOrderForCustomerLastBillShort(customerRecord.getLastBill()));
        if (salesOrderDTO.getDebt() > 0.001) {
          customerRecord.setTotalReceivable(salesOrderDTO.getDebt());
          customerRecord.setRepayDate(payTime);
        }
        writer.save(customerRecord);
      }
      updateVehicleBySalesOrder(writer, shopId, salesOrderDTO);
      writer.commit(status);
      if (customerDTO != null) {
        salesOrderDTO.setCustomerDTO(customerDTO);
      }
      MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(shopId, salesOrderDTO.getCustomerId());
      if (memberDTO != null) {
        salesOrderDTO.setMemberNo(memberDTO.getMemberNo());
        salesOrderDTO.setMemberStatus(memberDTO.getStatus());
        salesOrderDTO.setMemberType(memberDTO.getType());
      }
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 在购卡时候更新客户和记录，和保存会员信息放在一个事物中，所以这里不加事物。会员购卡不算累计消费和累计消费次数
   *
   * @param writer
   * @param memberCardOrderDTO
   * @throws Exception
   */
  public void updateCustomerAndCustomerRecord(UserWriter writer, MemberCardOrderDTO memberCardOrderDTO) throws Exception {
    if (memberCardOrderDTO.getCustomerId() != null) {
      CustomerDTO customerDTO = getUserService().getCustomerDTOByCustomerId(memberCardOrderDTO.getCustomerId(), memberCardOrderDTO.getShopId());

      if (customerDTO != null && StringUtil.isEmpty(customerDTO.getMobile()) && ArrayUtil.isNotEmpty(customerDTO.getContacts())
          && StringUtil.isNotEmpty(memberCardOrderDTO.getMobile())) {
        customerDTO.setMobile(memberCardOrderDTO.getMobile());
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setMobile(memberCardOrderDTO.getMobile());
        contactDTO.setShopId(memberCardOrderDTO.getShopId());
        contactDTO.setLevel(ContactConstant.LEVEL_0);
        contactDTO.setDisabled(ContactConstant.ENABLED);
        contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
        contactDTO.setIsShopOwner(ContactConstant.NOT_SHOP_OWNER);
        ContactDTO[] contactDTOs = new ContactDTO[1];
        contactDTOs[0] = contactDTO;
        customerDTO.setContacts(contactDTOs);
      }

      if (!ArrayUtils.isEmpty(customerDTO.getContacts()) && customerDTO.hasValidContact()) {
        for (ContactDTO contactDTO : customerDTO.getContacts()) {
          if (contactDTO != null && contactDTO.getIsMainContact() != null && contactDTO.getIsMainContact() == 1) {
            contactDTO.setMobile(memberCardOrderDTO.getMobile());
            break;
          }
        }
      }
      getUserService().updateCustomer(writer, customerDTO);

      //更新客户消费记录表 累计消费,最近消费
      List<CustomerRecord> customerRecords = writer.getShopCustomerRecordByCustomerId(memberCardOrderDTO.getCustomerId());
      if (CollectionUtils.isNotEmpty(customerRecords)) {
        customerRecords.get(0).setLastDate(System.currentTimeMillis());
        customerRecords.get(0).setLastAmount(memberCardOrderDTO.getTotal());
        customerRecords.get(0).setMobile(memberCardOrderDTO.getMobile());
        customerRecords.get(0).setLastBill(memberCardOrderDTO.getMemberCardName());
        customerRecords.get(0).setLastBillShort(this.combinateSaleOrderForCustomerLastBillShort(customerRecords.get(0).getLastBill()));
        if (memberCardOrderDTO.getReceivableDTO().getDebt() > 0.001) {
          customerRecords.get(0).setTotalReceivable(
              customerRecords.get(0).getTotalReceivable() + memberCardOrderDTO.getReceivableDTO().getDebt());
          customerRecords.get(0).setRepayDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, memberCardOrderDTO.getRepayTime()));

        }
        writer.update(customerRecords.get(0));
      } else {
        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setCustomerId(customerDTO.getId());
        customerRecord.setShopId(memberCardOrderDTO.getShopId());
        customerRecord.setName(customerDTO.getName());
        customerRecord.setMobile(customerDTO.getMobile());
        customerRecord.setLastDate(System.currentTimeMillis());
        customerRecord.setLastAmount(memberCardOrderDTO.getTotal());
        customerRecord.setLastBill(memberCardOrderDTO.getMemberCardName());
        customerRecord.setLastBillShort(this.combinateSaleOrderForCustomerLastBillShort(customerRecord.getLastBill()));
        if (memberCardOrderDTO.getReceivableDTO().getDebt() > 0.001) {
          customerRecord.setTotalReceivable(memberCardOrderDTO.getReceivableDTO().getDebt());
          customerRecord.setRepayDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, memberCardOrderDTO.getRepayTime()));
        }
        writer.save(customerRecord);
      }
    }
  }

  public String combinateSaleOrderForCustomerLastBillShort(String lastBill) {
    if (null == lastBill) {
      return null;
    }
    String content = lastBill;
    if (content.length() > 7) {
      content = content.substring(0, 6) + RfTxnConstant.TextSymbol.ETC;
    }
    return content;
  }

  public String combinateRepairOrderForCustomerLastBillShort(String lastBill) {
    String content = lastBill;
    if (content.length() > 7) {
      content = content.substring(0, 6) + RfTxnConstant.TextSymbol.ETC;
    }
    return content;
  }

  public String combinateSaleOrderForCustomerLastBill(SalesOrderDTO salesOrderDTO) {
    StringBuffer content = new StringBuffer();
    SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
    if ((salesOrderItemDTOs == null || salesOrderItemDTOs.length == 0)) return "";
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderItemDTOs) {
      content.append(salesOrderItemDTO.getProductName()).append("(").append(salesOrderItemDTO.getBrand()).append(")").append(RfTxnConstant.TextSymbol.PAUSE_MARK);
    }
    content.setLength(content.length() - 1);
    return content.toString();
  }


  public String combinateSaleOrderForCustomerLastBill(SalesReturnDTO salesReturnDTO) {
    StringBuffer content = new StringBuffer();
    SalesReturnItemDTO[] salesReturnItemDTOs = salesReturnDTO.getItemDTOs();
    if ((salesReturnItemDTOs == null || salesReturnItemDTOs.length == 0)) return "";
    for (SalesReturnItemDTO salesReturnItemDTO : salesReturnItemDTOs) {
      content.append(salesReturnItemDTO.getProductName()).append("(").append(salesReturnItemDTO.getBrand()).append(")").append(RfTxnConstant.TextSymbol.PAUSE_MARK);
    }
    content.setLength(content.length() - 1);
    return content.toString();
  }

  public String combinateRepairOrderForCustomerLastBill(RepairOrderDTO repairOrderDTO) {
    StringBuffer content = new StringBuffer();
    RepairOrderServiceDTO[] repairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
    RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
    boolean flag = false;
    if ((repairOrderServiceDTOs != null && repairOrderServiceDTOs.length != 0)) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
        if (repairOrderServiceDTO.getService() != null && !"".equals(repairOrderServiceDTO.getService())) {
          content.append(repairOrderServiceDTO.getService()).append(RfTxnConstant.TextSymbol.PAUSE_MARK);
          flag = true;
        }
      }
      if (content.length() == 0) {
        content.setLength(0);
      } else {
        content.setLength(content.length() - 1);
      }

      if (flag) {
        content.append(RfTxnConstant.TextSymbol.SEMICOLON);
      }
    }
    if (repairOrderItemDTOs == null || repairOrderItemDTOs.length == 0) return content.toString();
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOs) {
      if (repairOrderItemDTO.getProductName() != null && !"".equals(repairOrderItemDTO.getProductName())) {
        if (repairOrderItemDTO.getBrand() != "" && repairOrderItemDTO.getBrand() != null) {
          content.append(repairOrderItemDTO.getProductName()).append("(").append(repairOrderItemDTO.getBrand()).append(")");
        } else {
          content.append(repairOrderItemDTO.getProductName());
        }
        content.append(RfTxnConstant.TextSymbol.PAUSE_MARK);
      }
    }
    if (content.length() > 0) {
      content.setLength(content.length() - 1);
    }
    return content.toString();
  }

  @Override
  public void handleCustomerForRepairOrder(RepairOrderDTO repairOrderDTO, Long shopId, Long userId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Vehicle vehicle = null;
      boolean newVehicle = false;
      List<Vehicle> vehicles = writer.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
      if (vehicles.size() == 0) {    //车牌号码不存在
        vehicle = new Vehicle(repairOrderDTO);
        vehicle.setShopId(shopId);
        writer.save(vehicle);
        newVehicle = true;
      } else {        //车牌号码存在 如果车子一开始是洗车的可能没有车辆信息，这儿也要添加后更新车辆信息
        vehicle = vehicles.get(0);
        VehicleModifyLogDTO oldLog = new VehicleModifyLogDTO();
        oldLog.setBrand(vehicle.getBrand());
        oldLog.setModel(vehicle.getModel());
        vehicle.setBrand(repairOrderDTO.getBrand());
        vehicle.setModel(repairOrderDTO.getModel());
        vehicle.setYear(repairOrderDTO.getYear());
        vehicle.setMemo(repairOrderDTO.getMemo());
        vehicle.setEngine(repairOrderDTO.getEngine());
        vehicle.setBrandId(repairOrderDTO.getBrandId());
        vehicle.setModelId(repairOrderDTO.getModelId());
        vehicle.setYearId(repairOrderDTO.getYearId());
        vehicle.setEngineId(repairOrderDTO.getEngineId());
       //取进厂时间比当前里程时间大的值更新当前里程和时间
        vehicle.setStartMileage(repairOrderDTO.getStartMileage());
        if(repairOrderDTO.getStartDate() != null
            && repairOrderDTO.getStartDate() >= NumberUtil.longValue(vehicle.getMileageLastUpdateTime())
            && NumberUtil.doubleVal(repairOrderDTO.getStartMileage()) > 0){
          vehicle.setMileageLastUpdateTime(repairOrderDTO.getStartDate());
          vehicle.setObdMileage(repairOrderDTO.getStartMileage());
        }

        vehicle.setContact(repairOrderDTO.getVehicleContact());
        vehicle.setMobile(repairOrderDTO.getVehicleMobile());
        vehicle.setColor(repairOrderDTO.getVehicleColor());
        vehicle.setEngineNo(repairOrderDTO.getVehicleEngineNo());
        vehicle.setChassisNumber(repairOrderDTO.getVehicleChassisNo());
        writer.update(vehicle);
        //保存车辆信息变更日志
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
        batchCreateVehicleModifyLog(logDTOs);
      }
      repairOrderDTO.setVehicleDTO(vehicle.toDTO());
//      Customer customer = null;
      CustomerDTO customerDTO = null;
      CustomerVehicle customerVehicle = null;
      if (!newVehicle) {   //车牌id已经存在  更新客户信息
        List<CustomerVehicle> customerVehicles = writer.getCustomerVehicleByVehicleId(vehicle.getId());
        if (null != customerVehicles && customerVehicles.size() > 0 && customerVehicles.get(0) != null && customerVehicles.get(0).getCustomerId() != null) {
          customerVehicle = customerVehicles.get(0);
          customerDTO = getCustomerById(customerVehicles.get(0).getCustomerId(), shopId);
        }
        if (customerDTO != null) {    //车牌id对应的客户存在
          customerDTO.fromRepairOrderDTO(repairOrderDTO);
          userService.updateCustomer(customerDTO);
          repairOrderDTO.setCustomerId(customerDTO.getId());
        }
      } else {   //车牌不存在
        //find existing customer
        List<CustomerDTO> customers = new ArrayList<CustomerDTO>();
        //先根据customerId找客户
        if (repairOrderDTO.getCustomerId() != null) {
//          Customer idCustomer = writer.getCustomerByCustomerIdAndShopId(repairOrderDTO.getCustomerId(), shopId);
          customerDTO = getCustomerById(repairOrderDTO.getCustomerId(), shopId);
          if (null != customerDTO) {
            customers.add(customerDTO);
          }
        }
        //如果没有再根据名字和手机找
        if (CollectionUtils.isEmpty(customers) && StringUtils.isNotBlank(repairOrderDTO.getMobile())) {
          customers = ServiceManager.getService(IUserService.class).getCustomerByMobile(shopId, repairOrderDTO.getMobile());
        }
        //还没有就增加新客户
        if (CollectionUtils.isEmpty(customers)) {  //新客户
          customerDTO = new CustomerDTO();
          customerDTO.fromRepairOrderDTO(repairOrderDTO);
          userService.createCustomer(customerDTO);
          CustomerRecord customerRecord = new CustomerRecord();
          customerRecord.setShopId(shopId);
          customerRecord.setCustomerId(customerDTO.getId());
          customerRecord.setName(repairOrderDTO.getCustomerName());
          customerRecord.setContact(repairOrderDTO.getContact());
          customerRecord.setMobile(repairOrderDTO.getMobile());
          customerRecord.setLicenceNo(repairOrderDTO.getLicenceNo());
          customerRecord.setBrand(repairOrderDTO.getBrand());
          customerRecord.setModel(repairOrderDTO.getModel());
          customerRecord.setYear(repairOrderDTO.getYear());
          customerRecord.setEngine(repairOrderDTO.getEngine());
          writer.save(customerRecord);
        } else {
          customerDTO = customers.get(0);
          customerDTO.fromRepairOrderDTO(repairOrderDTO);
          userService.updateCustomer(writer, customerDTO);
          repairOrderDTO.setCustomerId(customerDTO.getId());
        }
      }
      repairOrderDTO.setCustomerDTO(customerDTO);
      Long maintainTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", StringUtil.replaceBlankStr(repairOrderDTO.getMaintainTimeStr()));
      Long insureTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", StringUtil.replaceBlankStr(repairOrderDTO.getInsureTimeStr()));
      Long examineTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", StringUtil.replaceBlankStr(repairOrderDTO.getExamineTimeStr()));
      Long maintainMileage = repairOrderDTO.getMaintainMileage();

      Double[] intervals = ConfigUtils.getAppVehicleMaintainMileageIntervals();

      Double obdMileage = repairOrderDTO.getStartMileage();

      boolean maintainMileageFlag = true;
      if (obdMileage != null && maintainMileage != null && obdMileage - maintainMileage >= intervals[0] && obdMileage - maintainMileage <= intervals[1]) {
        maintainMileageFlag = false;
      }

      //新增预约服务的flag
      boolean maintainTimeFlag = true;
      boolean insureTimeFlag = true;
      boolean examineTimeFlag = true;
      //保存或者更新customer vehicle
      if (customerVehicle == null) {
        customerVehicle = new CustomerVehicle();
        customerVehicle.setVehicleId(vehicle.getId());
        customerVehicle.setCustomerId(repairOrderDTO.getCustomerId());
        customerVehicle.setMaintainTime(maintainTime);
        customerVehicle.setInsureTime(insureTime);
        customerVehicle.setExamineTime(examineTime);
        customerVehicle.setMaintainMileage(maintainMileage);
        customerVehicle.setStatus(VehicleStatus.ENABLED);
        writer.save(customerVehicle);
      } else {
        customerVehicle.setMaintainTime(maintainTime);
        customerVehicle.setInsureTime(insureTime);
        customerVehicle.setExamineTime(examineTime);
        customerVehicle.setMaintainMileage(maintainMileage);
        customerVehicle.setStatus(VehicleStatus.ENABLED);
        writer.update(customerVehicle);
      }
      //处理保险，保养验车服务
      List<CustomerServiceJob> customerServiceJobList = writer.getCustomerServiceJobByCustomerVehicleRemindTypes(
          shopId, repairOrderDTO.getCustomerId(), repairOrderDTO.getVechicleId(), UserConstant.VEHICLE_REMINDS);
      if (CollectionUtils.isNotEmpty(customerServiceJobList)) {
        for (CustomerServiceJob customerServiceJob : customerServiceJobList) {
          if (UserConstant.MAINTAIN_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(maintainTime);
            customerServiceJob.setStatus((maintainTime == null && maintainMileageFlag) ? UserConstant.Status.CANCELED : UserConstant.Status.ACTIVITY);
            writer.update(customerServiceJob);
            maintainTimeFlag = false;
          } else if (UserConstant.INSURE_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(insureTime);
            customerServiceJob.setStatus(insureTime == null ? UserConstant.Status.CANCELED : UserConstant.Status.ACTIVITY);
            writer.update(customerServiceJob);
            insureTimeFlag = false;
          } else if (UserConstant.EXAMINE_TIME.equals(customerServiceJob.getRemindType())) {
            customerServiceJob.setRemindTime(examineTime);
            customerServiceJob.setStatus(examineTime == null ? UserConstant.Status.CANCELED : UserConstant.Status.ACTIVITY);
            writer.update(customerServiceJob);
            examineTimeFlag = false;
          }
//          else if (UserConstant.MAINTAIN_MILEAGE.equals(customerServiceJob.getRemindType())) {
//            customerServiceJob.setRemindMileage(maintainMileage);
//            customerServiceJob.setStatus(NumberUtil.longValue(maintainMileage) == 0 ? UserConstant.Status.CANCELED : UserConstant.Status.ACTIVITY);
//            writer.update(customerServiceJob);
//            maintainMileageFlag = true;
//          }
        }
      }
      if (maintainTimeFlag && (maintainTime != null || (!maintainMileageFlag))) {
        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
        customerServiceJob.setCustomerId(repairOrderDTO.getCustomerId());
        customerServiceJob.setVehicleId(repairOrderDTO.getVechicleId());
        customerServiceJob.setRemindType(UserConstant.MAINTAIN_TIME);
        customerServiceJob.setAppointName(UserConstant.CustomerRemindType.MAINTAIN_TIME);
        customerServiceJob.setRemindTime(maintainTime);
        customerServiceJob.setShopId(shopId);
        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        writer.save(customerServiceJob);
      }
      if (insureTimeFlag && insureTime != null) {
        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
        customerServiceJob.setCustomerId(repairOrderDTO.getCustomerId());
        customerServiceJob.setVehicleId(repairOrderDTO.getVechicleId());
        customerServiceJob.setRemindType(UserConstant.INSURE_TIME);
        customerServiceJob.setAppointName(UserConstant.CustomerRemindType.INSURE_TIME);
        customerServiceJob.setRemindTime(insureTime);
        customerServiceJob.setShopId(shopId);
        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        writer.save(customerServiceJob);
      }
      if (examineTimeFlag && examineTime != null) {
        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
        customerServiceJob.setCustomerId(repairOrderDTO.getCustomerId());
        customerServiceJob.setVehicleId(repairOrderDTO.getVechicleId());
        customerServiceJob.setRemindType(UserConstant.EXAMINE_TIME);
        customerServiceJob.setAppointName(UserConstant.CustomerRemindType.EXAMINE_TIME);
        customerServiceJob.setRemindTime(examineTime);
        customerServiceJob.setShopId(shopId);
        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
        writer.save(customerServiceJob);
      }
//      if (maintainMileageFlag && NumberUtil.longValue(maintainMileage)>0) {
//        CustomerServiceJob customerServiceJob = new CustomerServiceJob();
//        customerServiceJob.setCustomerId(repairOrderDTO.getCustomerId());
//        customerServiceJob.setVehicleId(repairOrderDTO.getVechicleId());
//        customerServiceJob.setRemindType(UserConstant.MAINTAIN_MILEAGE);
//        customerServiceJob.setAppointName(UserConstant.CustomerRemindType.MAINTAIN_TIME);
//        customerServiceJob.setRemindMileage(maintainMileage);
//        customerServiceJob.setShopId(shopId);
//        customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
//        writer.save(customerServiceJob);
//      }
      //处理预约服务
      this.saveCustomAppointServiceDTO(writer, shopId, repairOrderDTO.getCustomerId(), vehicle.getId(), repairOrderDTO.getAppointServiceDTOs());
      writer.commit(status);
      if (customerDTO != null) {
        repairOrderDTO.setCustomer(customerDTO);
        Member member = writer.getMemberByCustomerId(shopId, customerDTO.getId());
        if (member != null) {
          repairOrderDTO.setCustomerMemberNo(member.getMemberNo());
          repairOrderDTO.setCustomerMemberStatus(member.getStatus());
          repairOrderDTO.setCustomerMemberType(member.getType());
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void saveCustomAppointServiceDTO(Long shopId, Long customerId, Long vehicleId, AppointServiceDTO[] appointServiceDTOs) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      saveCustomAppointServiceDTO(writer, shopId, customerId, vehicleId, appointServiceDTOs);
      writer.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long createOrMatchingCustomerByAppUserNo(String appUserNo, Long shopId) throws BcgogoException {
    if (shopId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Long customerId = createOrMatchingCustomerByAppUserNo(appUserNo, shopId, writer);
      writer.commit(status);
      return customerId;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long createOrMatchingCustomerByAppUserNo(String appUserNo, Long shopId, UserWriter writer) throws BcgogoException {
    AppUserDTO appUserDTO = getAppUserDetailByUserNo(appUserNo);
    if (appUserDTO.getMobile() == null) return null;
    if (shopId == null) return null;
    Long customerId = null;
    if (!checkMatchingCustomer(appUserDTO, writer, shopId)) {
      customerId = createNewCustomerByAppUser(appUserDTO, writer, shopId);
    }
    return customerId;
  }

  //检查是否有匹配客户，有的话返回true，没有false，有匹配客户的情况下如果没有车辆，就新增车辆vehicle
  private Boolean checkMatchingCustomer(AppUserDTO appUserDTO, UserWriter writer, Long shopId) throws BcgogoException {
    Long customerId = null;
    //有车的情况
    boolean isMatch = false;
    if (CollectionUtil.isNotEmpty(appUserDTO.getAppVehicleDTOs())) {
      AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(sortList(appUserDTO.getAppVehicleDTOs()));
      customerId = writer.getCustomerIdByCustomerMobileAndVehicleNo(shopId, appUserDTO.getMobile(), appVehicleDTO.getVehicleNo());
      if (customerId == null) {
        Customer customer = writer.getCustomerByMobile(shopId, appUserDTO.getMobile());
        if (customer != null) {
          List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(shopId, appVehicleDTO.getVehicleNo());
          if (CollectionUtil.isEmpty(vehicleList))  {
            List<VehicleDTO> vehicleDTOs = getAppVehicleByAppUserNo(appUserDTO);
            ServiceManager.getService(IUserService.class)
                .saveOrUpdateCustomerVehicles(customer.getId(), customer.getShopId(), null, vehicleDTOs, writer);
          }
          isMatch = true;
        }
      }else{
        isMatch = true;
      }
    } else {
      Customer customer = writer.getCustomerByMobile(shopId, appUserDTO.getMobile());
      return customer != null;
    }
    return isMatch;
//    //建立关联
//    if (customerId != null) {
//      ServiceManager.getService(IAppUserCustomerMatchService.class)
//          .saveAppUserCustomer(writer, shopId, customerId, appUserDTO.getUserNo());
//      return true;
//    }
  }


  private List<AppVehicleDTO> sortList(List<AppVehicleDTO> appVehicleDTOs) {
    Collections.sort(appVehicleDTOs, new Comparator<AppVehicleDTO>() {
      @Override
      public int compare(AppVehicleDTO av1, AppVehicleDTO av2) {
        try {
          return av2.getCreatedTime().compareTo(av1.getCreatedTime());
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
          return -1;
        }
      }
    });
    return appVehicleDTOs;
  }

  private AppUserDTO getAppUserDetailByUserNo(String appUserNo) {
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(appUserNo, null);
    appUserDTO.setAppVehicleDTOs(ServiceManager.getService(IAppUserService.class).getAppVehicleDTOByAppUserNo(appUserDTO.getUserNo()));
    return appUserDTO;
  }

  private Long createNewCustomerByAppUser(AppUserDTO appUserDTO, UserWriter writer, Long shopId) throws BcgogoException {
    List<VehicleDTO> vehicleDTOs = getAppVehicleByAppUserNo(appUserDTO);
    //任何一种满足条件（不包含全部满足）中断
    if (!appUserValidator(appUserDTO, writer, vehicleDTOs, shopId)) return null;
    CustomerDTO customerDTO = appUserDTO.toCustomerDTO(shopId);
    IUserService userService = ServiceManager.getService(IUserService.class);
    // create customer contact
    customerDTO = userService.createCustomer(writer, customerDTO);
    // create customer_record
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO.fromCustomerDTO(customerDTO);
    userService.createCustomerRecord(customerRecordDTO, writer);
    //create vehicle
    userService.saveOrUpdateCustomerVehicles(customerDTO.getId(), customerDTO.getShopId(), null, vehicleDTOs, writer);

    return customerDTO.getId();
  }

  private boolean appUserValidator(AppUserDTO appUserDTO, UserWriter writer, List<VehicleDTO> vehicleDTOs, Long shopId) {
    //vehicle validator
    if (CollectionUtil.isNotEmpty(vehicleDTOs)) {
      for (VehicleDTO vehicleDTO : vehicleDTOs) {
        List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(shopId, vehicleDTO.getLicenceNo());
        if (CollectionUtil.isNotEmpty(vehicleList)) {
          return false;
        }
      }
    }
    //mobile validator
    Customer customer = writer.getCustomerByMobile(shopId, appUserDTO.getMobile());
    return customer == null;
  }

  private List<VehicleDTO> getAppVehicleByAppUserNo(AppUserDTO appUserDTO) {
    List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
    List<AppVehicleDTO> appVehicleDTOList = appUserDTO.getAppVehicleDTOs();
    if (CollectionUtil.isNotEmpty(appVehicleDTOList)) {
      for (AppVehicleDTO dto : appVehicleDTOList) {
        dto.setAppUserDTO(appUserDTO);
        vehicleDTOs.add(dto.toVehicleDTO());
      }
    }
    return vehicleDTOs;
  }


  private void saveCustomAppointServiceDTO(UserWriter writer, Long shopId, Long customerId, Long vehicleId, AppointServiceDTO[] appointServiceDTOs) throws ParseException {
    Set<Long> appointServiceIds = new HashSet<Long>();
    if (!ArrayUtil.isEmpty(appointServiceDTOs)) {
      for (AppointServiceDTO appointServiceDTO : appointServiceDTOs) {
        if (StringUtils.isNotBlank(appointServiceDTO.getIdStr()) && StringUtils.isNumeric(appointServiceDTO.getIdStr())) {
          appointServiceIds.add(NumberUtil.longValue(appointServiceDTO.getIdStr()));
        }
        appointServiceDTO.setShopId(shopId);
        appointServiceDTO.setCustomerId(String.valueOf(customerId));
        appointServiceDTO.setVehicleId(String.valueOf(vehicleId));
      }
    }

    if (!ArrayUtil.isEmpty(appointServiceDTOs)) {
      Map<Long, AppointService> appointServiceMap = writer.getAppointServiceMapByIds(shopId, appointServiceIds);
      Map<Long, CustomerServiceJob> customerServiceJobMap = writer.getCustomerServiceJobByAppointServiceIds(shopId, appointServiceIds);
      for (AppointServiceDTO appointServiceDTO : appointServiceDTOs) {
        if (StringUtils.isEmpty(appointServiceDTO.getAppointName()) || StringUtils.isEmpty(appointServiceDTO.getAppointDate())) {
          continue;
        }
        AppointService appointService = null;
        CustomerServiceJob customerServiceJob = null;
        if (StringUtils.isNotBlank(appointServiceDTO.getIdStr()) && StringUtils.isNumeric(appointServiceDTO.getIdStr())) {
          appointService = appointServiceMap.get(NumberUtil.longValue(appointServiceDTO.getIdStr()));
          customerServiceJob = customerServiceJobMap.get(NumberUtil.longValue(appointServiceDTO.getIdStr()));
        }
        if (appointService != null) {
          appointService.setAppointName(appointService.getAppointName());
          appointService.setAppointDate(appointService.getAppointDate());

          if (StringUtil.isNotEmpty(appointServiceDTO.getOperateType()) && OperateType.LOGIC_DELETE.toString().equals(appointServiceDTO.getOperateType())) {
            appointService.setStatus(AppointService.AppointServiceStatus.DISABLED);
          } else {
            appointService.setStatus(AppointService.AppointServiceStatus.ENABLED);
          }

          writer.update(appointService);
        } else {
          appointServiceDTO.setIdStr(null);
          appointService = AppointService.fromDTO(appointServiceDTO);
          writer.save(appointService);
        }
        appointServiceDTO.setId(appointService.getId());
        if (customerServiceJob != null) {
          customerServiceJob.setAppointName(appointService.getAppointName());
          customerServiceJob.setRemindTime(appointService.getAppointDate());
          customerServiceJob.setRemindType(UserConstant.APPOINT_SERVICE);

          if (appointService.getStatus() == AppointService.AppointServiceStatus.DISABLED) {
            customerServiceJob.setStatus(UserConstant.Status.CANCELED);
          } else {
            customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
          }

          customerServiceJob.setAppointServiceId(appointServiceDTO.getId());
          writer.update(customerServiceJob);
        } else if(appointService.getStatus() != AppointService.AppointServiceStatus.DISABLED) {
          customerServiceJob = new CustomerServiceJob();
          customerServiceJob.setAppointServiceId(appointService.getId());
          customerServiceJob.setCustomerId(appointService.getCustomerId());
          customerServiceJob.setVehicleId(appointService.getVehicleId());
          customerServiceJob.setRemindTime(appointService.getAppointDate());
          customerServiceJob.setRemindType(UserConstant.APPOINT_SERVICE);
          customerServiceJob.setAppointName(appointService.getAppointName());
          customerServiceJob.setShopId(appointService.getShopId());
          customerServiceJob.setStatus(UserConstant.Status.ACTIVITY);
          writer.save(customerServiceJob);
        }
      }
    }
  }

  @Override
  public void handleCustomerForAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (StringUtils.isBlank(appointOrderDTO.getCustomer()) && StringUtils.isNotBlank(appointOrderDTO.getVehicleNo())) {
        appointOrderDTO.setCustomer(appointOrderDTO.getVehicleNo());
      }

      Vehicle vehicle = null;
      boolean newVehicle = false;
      if (StringUtils.isNotEmpty(appointOrderDTO.getVehicleNo())) {
        List<Vehicle> vehicles = writer.getVehicleByLicenceNo(appointOrderDTO.getShopId(), appointOrderDTO.getVehicleNo());
        if (vehicles.size() == 0) {    //车牌号码不存在
          vehicle = new Vehicle(appointOrderDTO);
          writer.save(vehicle);
          appointOrderDTO.setAddVehicleLicenceNoToSolr(true);
          appointOrderDTO.setVehicleId(vehicle.getId());
          newVehicle = true;
        } else {        //车牌号码存在 如果车子一开始是洗车的可能没有车辆信息，这儿也要添加后更新车辆信息
          vehicle = vehicles.get(0);
          VehicleModifyLogDTO oldLog = new VehicleModifyLogDTO();
          oldLog.setBrand(vehicle.getBrand());
          oldLog.setModel(vehicle.getModel());
          vehicle.updateFromAppointOrderDTO(appointOrderDTO);
          writer.update(vehicle);
          //保存车辆信息变更日志
          VehicleModifyLogDTO newLog = new VehicleModifyLogDTO();
          newLog.setBrand(vehicle.getBrand());
          newLog.setModel(vehicle.getModel());
          List<VehicleModifyLogDTO> logDTOs = VehicleModifyLogDTO.compare(oldLog, newLog);
          for (VehicleModifyLogDTO dto : logDTOs) {
            dto.setShopId(appointOrderDTO.getShopId());
            dto.setUserId(appointOrderDTO.getUserId());
            dto.setVehicleId(vehicle.getId());
            dto.setOperationType(VehicleModifyOperations.APPOINT_ORDER);
          }
          batchCreateVehicleModifyLog(logDTOs);
        }
      }

//      Customer customer = null;
      CustomerDTO customerDTO = null;
      CustomerVehicle customerVehicle = null;
      if (!newVehicle) {   //车牌id已经存在  更新客户信息
        List<CustomerVehicle> customerVehicles = writer.getCustomerVehicleByVehicleId(vehicle.getId());
        if (null != customerVehicles && customerVehicles.size() > 0 && customerVehicles.get(0) != null && customerVehicles.get(0).getCustomerId() != null) {
          customerVehicle = customerVehicles.get(0);
          customerDTO = getCustomerById(customerVehicles.get(0).getCustomerId(), appointOrderDTO.getShopId());
        }
        if (customerDTO != null) {    //车牌id对应的客户存在
          customerDTO.fromAppointOrderDTO(appointOrderDTO);
          userService.updateCustomer(customerDTO);
          appointOrderDTO.setCustomerId(customerDTO.getId());
        }
      } else {   //车牌不存在
        //find existing customer
        List<CustomerDTO> customers = new ArrayList<CustomerDTO>();
        //先根据customerId找客户
        if (appointOrderDTO.getCustomerId() != null) {
//          Customer idCustomer = writer.getCustomerByCustomerIdAndShopId(repairOrderDTO.getCustomerId(), shopId);
          customerDTO = getCustomerById(appointOrderDTO.getCustomerId(), appointOrderDTO.getShopId());
          if (null != customerDTO) {
            customers.add(customerDTO);
          }
        }
        //如果没有再根据名字和手机找
        if (CollectionUtils.isEmpty(customers) && StringUtils.isNotBlank(appointOrderDTO.getCustomerMobile())) {
          customers = ServiceManager.getService(IUserService.class).getCustomerByMobile(appointOrderDTO.getShopId()
              , appointOrderDTO.getCustomerMobile());
        }
        //还没有就增加新客户
        if (CollectionUtils.isEmpty(customers)) {  //新客户
          customerDTO = new CustomerDTO();
          customerDTO.fromAppointOrderDTO(appointOrderDTO);
          userService.createCustomer(customerDTO);
          CustomerRecord customerRecord = new CustomerRecord();
          customerRecord.setShopId(appointOrderDTO.getShopId());
          customerRecord.setCustomerId(customerDTO.getId());
          customerRecord.setName(appointOrderDTO.getCustomer());
          customerRecord.setMobile(appointOrderDTO.getCustomerMobile());
          customerRecord.setLicenceNo(appointOrderDTO.getVehicleNo());
          customerRecord.setBrand(appointOrderDTO.getVehicleBrand());
          customerRecord.setModel(appointOrderDTO.getVehicleModel());
          writer.save(customerRecord);
        } else {
          customerDTO = customers.get(0);
          customerDTO.fromAppointOrderDTO(appointOrderDTO);
          userService.updateCustomer(writer, customerDTO);
          appointOrderDTO.setCustomerId(customerDTO.getId());
        }
      }
      appointOrderDTO.setCustomerDTO(customerDTO);
      if (customerVehicle == null && vehicle != null) {
        customerVehicle = new CustomerVehicle();
        customerVehicle.setVehicleId(vehicle.getId());
        customerVehicle.setCustomerId(appointOrderDTO.getCustomerId());
        customerVehicle.setStatus(VehicleStatus.ENABLED);
        writer.save(customerVehicle);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }

  }

  public void batchCreateVehicleModifyLog(List<VehicleModifyLogDTO> vehicleModifyLogDTOs) {
    UserWriter writer = userDaoManager.getWriter();
    if (CollectionUtils.isEmpty(vehicleModifyLogDTOs))
      return;
    Long id = 0L;
    for (int i = 0; i < vehicleModifyLogDTOs.size(); i++) {
      VehicleModifyLogDTO logDTO = vehicleModifyLogDTOs.get(i);
      VehicleModifyLog log = new VehicleModifyLog(logDTO, false);
      log.setStatProcessStatus(StatProcessStatus.NEW);
      if (i > 0) {
        log.setOperationId(id);
      }
      writer.save(log);
      if (i == 0) {
        id = log.getId();
        log.setOperationId(id);
        writer.update(log);
      }
    }
  }


  /**
   * 根据客户id获取客户详细信息
   *
   * @param customerId
   * @return
   * @throws Exception
   */
  public CustomerResponse findCustomerById(Long customerId, Long shopId) throws Exception {
    CustomerResponse customerResponse = new CustomerResponse();
    UserWriter writer = userDaoManager.getWriter();
    Customer customer = writer.getCustomerByCustomerIdAndShopId(customerId, shopId);
    if (customer == null) {
      return customerResponse;
    }
    CustomerDTO customerDTO = customer.toDTO();
    if (customerId != null) {
      if (customerDTO != null) {
        customerResponse.setShopId(customerDTO.getShopId());
        customerResponse.setCustomerId(customerDTO.getId());   //客户id
        customerResponse.setName(customerDTO.getName());    //单位 --> 客户名
        customerResponse.setContact(customerDTO.getContact());       //联系人
        customerResponse.setLandLine(customerDTO.getLandLine());  //固定电话
        customerResponse.setFax(customerDTO.getFax());            //传真
        customerResponse.setQq(customerDTO.getQq());              //qq
        customerResponse.setAddress(customerDTO.getAddress());    //地址
        customerResponse.setEmail(customerDTO.getEmail());        //email
        customerResponse.setBirthDay(customerDTO.getBirthday());  //生日
        customerResponse.setMobile(customerDTO.getMobile());      //手机
        customerResponse.setShortName(customerDTO.getShortName());//简称
      }

      //add by zhuj 查询客户的联系人列表
      ContactDTO[] contactDTOs = new ContactDTO[3];
      List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(customerId, null, shopId, null, null);
      if (!CollectionUtils.isEmpty(contactList)) {
        int size = contactList.size();
        if (contactList.size() > 3) { // 当前db中联系人最多为3个
          LOG.error("contact num in db is over 3");
          size = 3;
        }
        for (int i = 0; i < size; i++) {
          contactDTOs[i] = contactList.get(i).toDTO();
        }
      }
      customerResponse.setContacts(contactDTOs);

      List<CustomerRecordDTO> customerRecordDTOList = new ArrayList<CustomerRecordDTO>();
      for (CustomerRecord customerRecord : writer.getCustomerRecordByCustomerId(customerId)) {
        customerRecordDTOList.add(customerRecord.toDTO());
      }

      CustomerRecordDTO customerRecordDTO = null;
      if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty()) {
        customerRecordDTO = customerRecordDTOList.get(0);
        if (customerRecordDTO != null) {
          customerResponse.setMemberNumber(customerRecordDTO.getMemberNumber());      //会员号
          customerResponse.setTotalAmount(customerRecordDTO.getTotalAmount());        //累计消费
          customerResponse.setTotalArrears(customerRecordDTO.getTotalReceivable());   //累计欠款
          customerResponse.setTotalReturnAmount(customerRecordDTO.getTotalReturnAmount());
          customerResponse.setTotalPayable(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
        }
      }
    }
    return customerResponse;
  }

  /**
   * 关键词查询客户，并按最后消费时间倒序排列
   *
   * @param shopId
   * @param key
   * @param rowStart
   * @param pageSize
   * @return
   */
  public List<CustomerRecordDTO> getCustomerRecordByKey(Long shopId, String key, int rowStart, int pageSize) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerRecordDTO> customerRecordDTOList = writer.getCustomerRecordByKey(shopId, key, rowStart, pageSize);
    if (customerRecordDTOList == null || customerRecordDTOList.isEmpty()) {
      return customerRecordDTOList;
    }
    List<Long> customerIds = new ArrayList<Long>();
    for (CustomerRecordDTO customerRecordDTO : customerRecordDTOList) {
      if (customerRecordDTO == null || customerRecordDTO.getCustomerId() == null) {
        continue;
      }
      customerIds.add(customerRecordDTO.getCustomerId());
    }

    return customerRecordDTOList;
  }

  /**
   * 根据搜索关键词获取客户相关的静态信息和基本消费信息
   *
   * @param shopId
   * @param key
   * @param rowStart
   * @param pageSize
   * @return
   * @throws BcgogoException
   */
  public List<CustomerRecordDTO> getCustomerRecordInfoByKey(Long shopId, String key, int rowStart, int pageSize) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerRecordDTO> customerRecordDTOList = getCustomerRecordByKey(shopId, key, rowStart, pageSize);
    if (customerRecordDTOList == null || customerRecordDTOList.isEmpty()) {
      return customerRecordDTOList;
    }
    List<Long> customerIds = new ArrayList<Long>();
    for (CustomerRecordDTO customerRecordDTO : customerRecordDTOList) {
      if (customerRecordDTO == null || customerRecordDTO.getCustomerId() == null) {
        continue;
      }
      List<Contact> contacts = writer.getContactByCusId(customerRecordDTO.getCustomerId());
      if (CollectionUtils.isNotEmpty(contacts)) {
        Contact contact = getContactByKey(contacts, key);
        if (contact != null) {
          customerRecordDTO.setMobile(contact.getMobile());
          customerRecordDTO.setContact(contact.getName());
          customerRecordDTO.setQq(contact.getQq());
          customerRecordDTO.setEmail(contact.getEmail());
        }
      }
      customerIds.add(customerRecordDTO.getCustomerId());
    }

    //再统计客户的车辆数量

    List<CustomerVehicleNumberDTO> customerVehicleNumberDTOList = writer.getCustomerVehicleCount(customerIds);
    if (customerVehicleNumberDTOList == null || customerVehicleNumberDTOList.isEmpty()) {
      return customerRecordDTOList;
    }
    //遍历所以客户的车辆数信息，匹配到每一个客户，并把值赋给客户对象
    for (CustomerVehicleNumberDTO customerVehicleNumberDTO : customerVehicleNumberDTOList) {
      if (customerVehicleNumberDTO == null || customerVehicleNumberDTO.getCount() == null) {
        continue;
      }
      for (CustomerRecordDTO customerRecordDTO : customerRecordDTOList) {
        if (customerRecordDTO == null) {
          continue;
        }
        if (customerRecordDTO.getCustomerId().equals(customerVehicleNumberDTO.getCustomerId())) {
          customerRecordDTO.setVehicleCount(customerVehicleNumberDTO.getCount());
          break;
        }
      }
    }
    return customerRecordDTOList;
  }

  private Contact getContactByKey(List<Contact> contacts, String key) {
    if (CollectionUtils.isEmpty(contacts)) {
      return null;
    }
    if (StringUtils.isBlank(key)) {
      return contacts.get(0);
    }
    for (Contact contact : contacts) {
      if ((StringUtils.isNotEmpty(contact.getName()) && contact.getName().contains(key)) || (StringUtil.isNotEmpty(contact.getMobile()) && contact.getMobile().contains(key))) {
        return contact;
      }
    }
    return contacts.get(0);
  }

  /**
   * 根据客户ID列表获取客户列表
   *
   * @param customerIds
   * @return
   */
  @Override
  public List<CustomerDTO> getCustomerByIds(List<Long> customerIds) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    if (customerIds == null || customerIds.isEmpty()) {
      return new ArrayList<CustomerDTO>();
    }
    List<CustomerDTO> customerDTOs = writer.getCustomerByIds(customerIds);
    if (!CollectionUtils.isEmpty(customerDTOs)) {
      for (CustomerDTO customerDTO : customerDTOs) {
        List<Contact> contacts = writer.getContactByCusId(customerDTO.getId());
        List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
        if (!contacts.isEmpty()) {
          for (Contact c : contacts) {
            contactDTOList.add(c.toDTO());
            if (c.getIsMainContact() != null && c.getIsMainContact() == 1) {
              customerDTO.setContact(c.getName());
              customerDTO.setMobile(c.getMobile());
            }
          }
          customerDTO.setContacts(contactDTOList.toArray(new ContactDTO[contactDTOList.size()]));
        }
      }
    }
    return customerDTOs;
  }

  @Override
  public CustomerDTO getCustomerById(Long customerId) {
    if (customerId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    Customer customer = writer.getById(Customer.class, customerId);
    CustomerDTO dto = null;
    if (customer != null) {
      dto = customer.toDTO();
      List<Contact> contacts = writer.getContactByCusId(customerId);
      List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
      if (!contacts.isEmpty()) {
        for (Contact c : contacts) {
          contactDTOList.add(c.toDTO());
          if (c.getIsMainContact() != null && c.getIsMainContact() == 1) {
            customer.setContact(c.getName());
            customer.setMobile(c.getMobile());
          }
        }
        dto.setContacts(contactDTOList.toArray(new ContactDTO[contactDTOList.size()]));
      }
    }
    return dto;
  }

  public CustomerDTO getCustomerById(Long customerId, Long shopId) {
    if (customerId == null || shopId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    Customer customer = writer.getCustomerById(shopId, customerId);

    if (customer != null) {
      CustomerDTO customerDTO = customer.toDTO();
      ServiceManager.getService(IUserService.class).setCustomerContacts(customerId, writer, customerDTO);
      return customerDTO;
    }
    return null;
  }

  public List<CustomerDTO> getRelatedCustomerByMatchedName(Long shopId, String customerName) {
    if (StringUtil.isEmpty(customerName)) return null;
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    for (Customer customer : writer.getRelatedCustomerByMatchedName(shopId, customerName)) {
      if (customer == null) {
        continue;
      }
      customerDTOs.add(customer.toDTO());
    }
    return customerDTOs;
  }

  /**
   * 保存车辆预约服务信息
   * 客户ID与车辆ID是必须的，否则抛出异常
   *
   * @param customerId   //客户ID 必需
   * @param vehicleId    //车辆ID 必需
   * @param maintainTime //预约保养时间
   * @param insureTime   //预约保险时间
   * @param examineTime  //预约验车时间
   * @throws BcgogoException
   * @author wjl
   */
  @Override
  public void saveVehicleAppointment(
      Long customerId, Long vehicleId, Long maintainTime, Long insureTime, Long examineTime) throws BcgogoException {
    if (customerId == null || vehicleId == null) throw new BcgogoException("缺少参数,customerId或vehicleId为空!");

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      CustomerVehicle customerVehicle = getVehicleAppointment(customerId, vehicleId);
      if (customerVehicle == null) throw new BcgogoException("找不到对应的客户车辆信息!");
      customerVehicle.setMaintainTime(maintainTime);
      customerVehicle.setExamineTime(examineTime);
      customerVehicle.setInsureTime(insureTime);
      writer.saveOrUpdate(customerVehicle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 查询车辆预约服务信息
   * 客户ID与车辆ID是必须的，否则抛出异常
   *
   * @param customerId //客户ID 必需
   * @param vehicleId  //车辆ID 必需
   * @return
   * @throws BcgogoException
   * @author wjl
   */
  @Override
  public CustomerVehicle getVehicleAppointment(Long customerId, Long vehicleId) throws BcgogoException {
    if (customerId == null || vehicleId == null) throw new BcgogoException("参数不正确,customerId或vehicleId为空!");
    return userDaoManager.getWriter()
        .getVehicleAppointment(customerId, vehicleId);
  }

  /**
   * 从excel中导入客户
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public ImportResult importCustomerFromExcel(ImportContext importContext) throws Exception {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.parseData(importContext);

    //2.校验数据
    CheckResult checkResult = customerImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = customerImporter.importData(importContext);

    return importResult;

  }

  /**
   * 当月新增客户数
   *
   * @param shopId
   * @return
   * @author zhangchuanlong
   */
  @Override
  public Long getCountOfCustomerByMonth(Long shopId) throws ParseException {
    Long startTime = DateUtil.getFirstDayDateTimeOfMonth();
    Long endTime = DateUtil.getLastDayDateTimeOfMonth();
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopCustomerByTime(shopId, startTime, endTime);
  }

  /**
   * 当天新增客户数
   *
   * @param shopId
   * @return
   * @author zhang chuanlong
   */
  @Override
  public Long getCountOfCustomerByToay(Long shopId) throws ParseException {
    Long startTime = DateUtil.getStartTimeOfToday();
    Long endTime = DateUtil.getEndTimeOfToday();
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopCustomerByTime(shopId, startTime, endTime);
  }

  /**
   * 昨日新增客户数
   *
   * @param shopId
   * @return
   * @auther zhangchuanlong
   */
  @Override
  public Long getCountOfCustomerByYesterDay(Long shopId) throws ParseException {
    Long startTime = DateUtil.getStartTimeOfYesterday();
    Long endTime = DateUtil.getEndTimeOfYesterday();
    UserWriter writer = userDaoManager.getWriter();
    return writer.countShopCustomerByTime(shopId, startTime, endTime);
  }

  @Override
  public Map<Long, CustomerDTO> getCustomerBySupplierShopId(Long shopId, Long... customerShopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCustomerByCustomerShopId(shopId, customerShopId);
  }

  @Override
  public List<CustomerOrSupplierDTO> getCustomerOrSupplierSuggestion(Long shopId, String keyWord) {
    List<CustomerOrSupplierDTO> customerOrSupplierDTOs = new ArrayList<CustomerOrSupplierDTO>();
    CustomerOrSupplierDTO customerOrSupplierDTO = null;
    List<SupplierDTO> supplierDTOList = null;
    List<CustomerDTO> customerDTOList = null;
    if (keyWord == null && shopId == null) return null;
    if (PinyinUtil.isChinese(keyWord.charAt(0))) {
      supplierDTOList = getUserService().getSupplier(keyWord.trim(), shopId);
      customerDTOList = getUserService().getCustomer(keyWord.trim(), shopId);
    } else {
      keyWord = PinyinUtil.converterToFirstSpell(keyWord.trim());
      supplierDTOList = getUserService().getSupplierByZiMu(keyWord, shopId);
      customerDTOList = getUserService().getCustomerByZiMu(keyWord.trim(), shopId);
    }
    if (CollectionUtils.isNotEmpty(supplierDTOList)) {
      for (SupplierDTO supplierDTO : supplierDTOList) {
        customerOrSupplierDTO = new CustomerOrSupplierDTO();
        customerOrSupplierDTO.fromSupplierDTO(supplierDTO);
        customerOrSupplierDTOs.add(customerOrSupplierDTO);
      }
    }
    if (CollectionUtils.isNotEmpty(customerDTOList)) {
      for (CustomerDTO customerDTO : customerDTOList) {
        customerOrSupplierDTO = new CustomerOrSupplierDTO();
        customerOrSupplierDTO.fromCustomerDTO(customerDTO);
        customerOrSupplierDTOs.add(customerOrSupplierDTO);
      }
    }
    return customerOrSupplierDTOs;
  }

  @Override
  public Map<Long, CustomerDTO> getCustomerByIdSet(Long shopId, Set<Long> customerIds) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCustomerByIdSet(shopId, customerIds);
  }

  @Override
  public Map<Long, UserDTO> getExecutorByIdSet(Long shopId, Set<Long> executorIds) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getExecutorByIdSet(shopId, executorIds);
  }

  @Override
  public Map<Long, MemberCardDTO> getMemberCardByIds(Long shopId, Set<Long> carIds) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getMemberCardByIds(shopId, carIds);
  }

  @Override
  public int countVehicleByCustomerId(Long customerId) {
    if (customerId == null) {
      LOG.error("customerId can't be null.");
      return 0;
    }
    UserWriter writer = userDaoManager.getWriter();
    return writer.countVehicleByCustomerId(customerId);
  }

  @Override
  public void deleteCustomer(Long shopId, Long customerId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Customer customer = writer.getCustomerByCustomerIdAndShopId(customerId, shopId);
      Long relateSupplierId = null;
      if (null != customer) {
        relateSupplierId = customer.getSupplierId();
        customer.setStatus(CustomerStatus.DISABLED);
        writer.update(customer);
      }
      CustomerRecord customerRecord = writer.getCustomerRecord(shopId, customerId);

      if (null != customerRecord) {
        customerRecord.setStatus(CustomerStatus.DISABLED);
        writer.update(customerRecord);
      }
      List<CustomerVehicle> customerVehicleList = writer.getVehicleByCustomerId(customerId);

      List<Long> vehicleIdList = new ArrayList<Long>();
      for (CustomerVehicle customerVehicle : customerVehicleList) {
        if (null == customerVehicle.getVehicleId()) {
          continue;
        }

        Vehicle vehicle = writer.getVehicleById(shopId, customerVehicle.getVehicleId());

        if (null != vehicle) {
          vehicleIdList.add(vehicle.getId());
          vehicle.setStatus(VehicleStatus.DISABLED);
          writer.update(vehicle);
        }

      }
      if (relateSupplierId != null) {
        SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(relateSupplierId);
        supplierDTO.setIdentity(null);
        supplierDTO.setCustomerId(null);
        ServiceManager.getService(IUserService.class).updateSupplier(supplierDTO);
      }
      writer.commit(status);

      if (CollectionUtils.isNotEmpty(vehicleIdList)) {
        try {
          ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicleIdList.toArray(new Long[vehicleIdList.size()]));
        } catch (Exception e) {
          LOG.error("shopId:{}", shopId);
          LOG.error("vehicleId:{}", StringUtil.arrayToStr(",", vehicleIdList.toArray(new Long[vehicleIdList.size()])));
          LOG.error("createVehicleSolrIndex 失败！", e);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<VehicleModifyLog> getVehicleModifyLogByStatus(StatProcessStatus[] statProcessStatuses) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getVehicleModifyLogByStatus(statProcessStatuses);
  }

  @Override
  public void batchUpdateVehicleModifyLogStatus(List<VehicleModifyLog> toProcessLogs, StatProcessStatus done) {
    if (CollectionUtils.isEmpty(toProcessLogs)) {
      return;
    }
    List<Long> ids = new ArrayList<Long>();
    for (VehicleModifyLog dto : toProcessLogs) {
      ids.add(dto.getId());
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.batchUpdateVehicleModifyLogStatus(ids, done);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
  @Override
  public void updateCustomerAfterRepealWashOrder(WashBeautyOrderDTO washBeautyOrderDTO, ReceivableDTO receivableDTO) throws Exception {
    if (washBeautyOrderDTO == null || receivableDTO == null || washBeautyOrderDTO.getShopId() == null
        || washBeautyOrderDTO.getCustomerId() == null || washBeautyOrderDTO.getId() == null) {
      LOG.error("updateCustomerAfterRepealWashOrder error,washBeautyOrderDTO:{},receivableDTO:{}", washBeautyOrderDTO, receivableDTO);
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    String rollBackMemberKey = "";
    try {
      CustomerRecord customerRecord = writer.getCustomerRecord(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getCustomerId());
      if (customerRecord != null) {
        customerRecord.setTotalAmount(customerRecord.getTotalAmount() - receivableDTO.getSettledAmount() - receivableDTO.getDebt());
        customerRecord.setTotalReceivable(customerRecord.getTotalReceivable() - receivableDTO.getDebt());
        customerRecord.setConsumeTimes(NumberUtil.longValue(customerRecord.getConsumeTimes()) - 1);
        customerRecord.setMemberConsumeTotal(NumberUtil.toReserve(NumberUtil.doubleVal(customerRecord.getMemberConsumeTotal()) - NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()),NumberUtil.MONEY_PRECISION));

        if (NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()) > 0 || receivableDTO.getMemberId() != null) {
          customerRecord.setMemberConsumeTimes(NumberUtil.longValue(customerRecord.getMemberConsumeTimes()) - 1);
        }
        customerRecord.setTotalAmount(NumberUtil.round(customerRecord.getTotalAmount(), NumberUtil.MONEY_PRECISION));
        customerRecord.setTotalReceivable(NumberUtil.round(customerRecord.getTotalReceivable(), NumberUtil.MONEY_PRECISION));
        writer.update(customerRecord);
      }
      if (receivableDTO.getMemberId() != null && receivableDTO.getMemberBalancePay() != null) {
        Member member = writer.getMemberDTOById(washBeautyOrderDTO.getShopId(), receivableDTO.getMemberId());
        if (member != null) {
          rollBackMemberKey = "rollBackMemberInfo_" + washBeautyOrderDTO.getShopId() + "_" + member.getMemberNo();
          if (BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, rollBackMemberKey)) {
            member.setBalance(member.getBalance() + receivableDTO.getMemberBalancePay());
            writer.update(member);
          }
        }

      }
      if (receivableDTO.getMemberId() != null) {
        //更新划卡次数
        for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
          if (washBeautyOrderItemDTO.getPayType().equals(ConsumeType.TIMES)) {
            MemberService memberService = ServiceManager.getService(IMembersService.class).getMemberServiceByMemberIdAndServiceIdAndStatus(receivableDTO.getMemberId(), washBeautyOrderItemDTO.getServiceId());
            if (memberService.getTimes() == -1)
              continue;
            memberService.setTimes(memberService.getTimes() + 1);
            ServiceManager.getService(IMembersService.class).updateMemberService(memberService);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, rollBackMemberKey);
    }
  }

  /**
   * 由于脏数据的存在，作为取customerRecord的过度方法
   *
   * @param customerId
   * @return
   */
  public CustomerRecord getUniqueCustomerRecordByCustomerId(Long shopId, Long customerId) {
    if (shopId == null || customerId == null) return null;
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    CustomerRecordDTO customerRecordIndex = new CustomerRecordDTO();
    customerRecordIndex.setShopId(shopId);
    customerRecordIndex.setCustomerId(customerId);
    UserWriter userWriter = userDaoManager.getWriter();
    List<CustomerRecord> customerRecords = userWriter.getCustomerRecord(customerRecordIndex);
    if (CollectionUtils.isEmpty(customerRecords)) {
      return null;
    }
    return customerRecords.get(0);
  }

  public SearchMergeResult getMergedCustomers(SearchMergeResult result, List<Long> customerIdList) throws Exception {
    if (NumberUtil.hasEmptyVal(customerIdList)) {
      return null;
    }
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    Map settlementTypeMap = TxnConstant.getSettlementTypeMap(result.getLocale());
    Map inoiceCatagoryMap = TxnConstant.getInvoiceCatagoryMap(result.getLocale());
    Map<String, String> areaMap = TxnConstant.getAreaMap(result.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(result.getLocale());
    UserWriter userWriter = userDaoManager.getWriter();
    CustomerRecord customerRecord = null;
    Customer customer = null;
    CustomerDTO customerDTO = null;
    MemberDTO memberDTO = null;
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    for (Long customerId : customerIdList) {
      customer = userWriter.getCustomerById(result.getShopId(), customerId);
      if (customer == null) {
        result.setSuccess(false);
        result.setMsg("选择客户中存在被删除，合并的客户！！！");
        return result;
      }
      if ("isSupplier".equals(customer.getIdentity()) && customer.getSupplierId() != null) {
        result.setSuccess(false);
        result.setMsg("当前客户同时是供应商,不能进行合并！！！");
        return result;
      }
      customerDTO = customer.toDTO();
      // add by zhuj 设置联系人信息
      List<ContactDTO> contactDTOList = ServiceManager.getService(IContactService.class).getContactByCusOrSupOrShopIdOrName(customerId, null, null, null, null);
      ContactDTO[] contactDTOs = null;
      if (!CollectionUtils.isEmpty(contactDTOList)) {
        contactDTOs = new ContactDTO[3];
        for (int i = 0; i < contactDTOList.size(); i++) {
          if (i == 3) {
            break;
          }
          contactDTOs[i] = contactDTOList.get(i);
          if (contactDTOs[i].getIsMainContact() != null && contactDTOs[i].getIsMainContact() == 1) {
            customerDTO.setContact(contactDTOs[i].getName());
            customerDTO.setMobile(contactDTOs[i].getMobile());
            customerDTO.setEmail(contactDTOs[i].getEmail());
            customerDTO.setQq(contactDTOs[i].getQq());
            customerDTO.setContactId(contactDTOs[i].getId());
            customerDTO.setContactIdStr(contactDTOs[i].getId() != null ? String.valueOf(contactDTOs[i].getId()) : "");
          }
        }
      }
      customerDTO.setContacts(contactDTOs);
      customerDTO.setSettlementTypeStr(String.valueOf(settlementTypeMap.get(String.valueOf(customerDTO.getSettlementType()))));
      customerDTO.setInvoiceCategoryStr(String.valueOf(inoiceCatagoryMap.get(String.valueOf(customerDTO.getInvoiceCategory()))));
      customerDTO.setAreaStr(String.valueOf(areaMap.get(customerDTO.getArea())));
      customerDTO.setCustomerKindStr(String.valueOf(customerTypeMap.get(customerDTO.getCustomerKind())));
      customerDTO.setCustomerVehicleResponses(vehicleService.findVehicleInfoByCustomerId(customerId));
      customerDTO.setMemberDTO(membersService.getMemberByCustomerId(result.getShopId(), customerId));
      //是否是OBD客户
      customerDTO.setIsObd(ServiceManager.getService(IUserService.class).isOBDCustomer(customerDTO.getId()));
      customerRecord = getUniqueCustomerRecordByCustomerId(result.getShopId(), customerId);
      if (customerRecord != null) {
        CustomerRecordDTO customerRecordDTO = customerRecord.toDTO();
        customerRecordDTO.setCountCustomerReturn(searchService.countReturn(customerRecordDTO.getShopId(), customerRecordDTO.getCustomerId(), OrderTypes.SALE_RETURN, OrderStatus.SETTLED));
        customerDTO.setCustomerRecordDTO(customerRecordDTO);
      }
      customerDTOs.add(customerDTO);
    }
    result.setResults(customerDTOs);
    return result;
  }

  public MergeResult mergeCustomerInfo(MergeResult<CustomerDTO, MergeCustomerSnap> mergeResult, Long parentId, Long[] childIds) throws Exception {
    UserWriter userWriter = userDaoManager.getWriter();
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    CustomerDTO customerIndex = new CustomerDTO();
    customerIndex.setShopId(mergeResult.getShopId());
    customerIndex.setId(parentId);
    CustomerDTO parent = mergeResult.getCustomerOrSupplierDTO();
    CustomerRecord parentCustomerRecord = getUniqueCustomerRecordByCustomerId(mergeResult.getShopId(), parentId);
    if (parentCustomerRecord == null || CustomerStatus.DISABLED.equals(parentCustomerRecord.getStatus())) {
      mergeResult.setCustomerOrSupplierIdStr(String.valueOf(parentId));
      mergeResult.setSuccess(false);
      mergeResult.setMsg("客户记录不存在，已经被删除，或被合并！");
      return mergeResult;
    }
    Member parentMember = userWriter.getMemberByCustomerId(mergeResult.getShopId(), parentId);
    //to save parent info snap
    MergeCustomerSnap mergeSnap = new MergeCustomerSnap();
    mergeSnap.setShopId(mergeResult.getShopId());
    mergeSnap.setParentId(parent.getId());
    mergeSnap.setParentName(parent.getName());
    mergeSnap.setOperatorId(mergeResult.getUserId());
    UserDTO userDTO = userWriter.getUserDTO(mergeResult.getShopId(), mergeResult.getUserId());
    if (userDTO != null) {
      mergeSnap.setOperator(userDTO.getName());
    }
    mergeSnap.setParent(parent);
    CustomerRecordDTO parentRecordDTO = parentCustomerRecord.toDTO();
    parentRecordDTO.setCountCustomerReturn(searchService.countReturn(parentRecordDTO.getShopId(), parentRecordDTO.getCustomerId(), OrderTypes.SALE_RETURN, OrderStatus.SETTLED));
    mergeSnap.setParentRecord(parentCustomerRecord.toDTO());
    if (parentMember != null) {
      MemberDTO parentMemberDTO = parentMember.toDTO();
      List<MemberService> parentMemerServices = userWriter.getMemberServicesByMemberId(parentMember.getId());
      List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
      if (CollectionUtil.isNotEmpty(parentMemerServices)) {
        for (MemberService memberService : parentMemerServices) {
          if (memberService == null) continue;
          memberServiceDTOs.add(memberService.toDTO());
        }
      }
      parentMemberDTO.setMemberServiceDTOs(memberServiceDTOs);
      mergeSnap.setParentMember(parentMemberDTO);
    }
    Object status = userWriter.begin();
    try {
      LOG.info("begin merge customer info,time={}", new Date());
      MergeCustomerSnap mergeSnapClone = null;
      for (Long childId : childIds) {
        mergeSnapClone = mergeSnap.clone();
        mergeResult.getMergeSnapMap().put(childId, mergeSnapClone);
        mergeCustomerVehicle(mergeResult, parent, childId);
        mergeCustomerMember(mergeResult, parentMember, parentId, childId);
        mergeCustomerRecord(mergeResult, parentCustomerRecord, childId);

        if (!mergeResult.isSuccess()) {
          return mergeResult;
        }
        mergeCustomerServiceJobs(mergeResult, parent, childId);
        mergeCustomer(mergeResult, parent, childId);
        //更新app_user_customer中的customer_id
        if (ServiceManager.getService(IUserService.class).isOBDCustomer(childId) && !parent.getIsObd()) {
          List<AppUserCustomer> appUserCustomer = ServiceManager.getService(IAppUserService.class).getAppUserCustomerByCustomerId(mergeResult.getShopId(), childId);
          if (CollectionUtils.isNotEmpty(appUserCustomer)) {
            for(AppUserCustomer appUserCus : appUserCustomer){
              AppUserCustomer appUserCustomer2 = userWriter.getById(AppUserCustomer.class, appUserCus.getId());
              if (appUserCustomer2 != null) {
                appUserCustomer2.setCustomerId(parent.getId());
                userWriter.update(appUserCustomer2);
              }
            }
          }
        }
        if (!mergeResult.isSuccess()) {
          return mergeResult;
        }
        userWriter.save(MergeTask.createTask(mergeResult.getShopId(), parent.getId(), childId, MergeType.MERGE_CUSTOMER));
      }
      userWriter.commit(status);
      return mergeResult;
    } catch (Exception e) {
      LOG.error("合并客户出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    } finally {
      userWriter.rollback(status);
    }
  }

  private MergeResult mergeCustomerServiceJobs(MergeResult<CustomerDTO, MergeCustomerSnap> mergeResult, CustomerDTO parent, Long childId) throws BcgogoException {
    UserWriter userWriter = userDaoManager.getWriter();
    try {
      List<CustomerServiceJob> customerServiceJobs = userWriter.getAllCustomerServiceJobByCustomerId(mergeResult.getShopId(), childId);
      if (CollectionUtil.isEmpty(customerServiceJobs)) {
        return mergeResult;
      }
      for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
        if (customerServiceJob == null) {
          continue;
        }
        customerServiceJob.setCustomerId(parent.getId());
        userWriter.update(customerServiceJob);
      }
      return mergeResult;
    } catch (Exception e) {
      LOG.error("合并客户服务提醒异常！！！");
      throw new BcgogoException(e.getMessage());
    }
  }

  private MergeResult mergeCustomerRecord(MergeResult<CustomerDTO, MergeCustomerSnap> mergeResult, CustomerRecord parentRecord, Long childId) throws BcgogoException {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter userWriter = userDaoManager.getWriter();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    CustomerRecord childRecord = customerService.getUniqueCustomerRecordByCustomerId(mergeResult.getShopId(), childId);
    if (childRecord == null || CustomerStatus.DISABLED.equals(childRecord.getStatus())) {
      mergeResult.setSuccess(false);
      mergeResult.setMsg("被合并的客户记录不存在！");
      mergeResult.setCustomerOrSupplierIdStr(String.valueOf(childId));
      return mergeResult;
    }
    CustomerRecordDTO childRecordDTO = childRecord.toDTO();
    childRecordDTO.setCountCustomerReturn(searchService.countReturn(childRecordDTO.getShopId(), childRecordDTO.getCustomerId(), OrderTypes.SALE_RETURN, OrderStatus.SETTLED));
    mergeResult.getMergeSnapMap().get(childId).setChildRecord(childRecordDTO);
    childRecord.setStatus(CustomerStatus.DISABLED);
    mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_disable_customerRecord(mergeResult.getShopId(), mergeResult.getUserId(),
        childRecord.getId()));
    parentRecord.setTotalAmount(NumberUtil.doubleVal(parentRecord.getTotalAmount()) + NumberUtil.doubleVal(childRecord.getTotalAmount()));
    parentRecord.setTotalReceivable(NumberUtil.doubleVal(parentRecord.getTotalReceivable()) + NumberUtil.doubleVal(childRecord.getTotalReceivable()));
    parentRecord.setTotalReturnAmount(parentRecord.getTotalReturnAmount() + childRecord.getTotalReturnAmount());
    parentRecord.setTotalPayable(NumberUtil.doubleVal(parentRecord.getTotalPayable()) + NumberUtil.doubleVal(childRecord.getTotalPayable()));
    parentRecord.setConsumeTimes(NumberUtil.longValue(parentRecord.getConsumeTimes()) + NumberUtil.longValue(childRecord.getConsumeTimes()));
    parentRecord.setMemberConsumeTimes(NumberUtil.longValue(parentRecord.getMemberConsumeTimes()) + NumberUtil.longValue(childRecord.getMemberConsumeTimes()));
    parentRecord.setMemberConsumeTotal(NumberUtil.doubleVal(parentRecord.getMemberConsumeTotal()) + NumberUtil.doubleVal(childRecord.getMemberConsumeTotal()));

    Long parentLastDate = parentRecord.getLastDate();
    Long childLastDate = childRecord.getLastDate();
    //合并消费记录
    if (childLastDate != null) {
      if (parentLastDate == null || (parentLastDate != null && childLastDate > parentLastDate)) {
        parentRecord.setLastDate(childRecord.getLastDate());
        parentRecord.setLastBill(childRecord.getLastBill());
      }
    }
    userWriter.update(childRecord);
    userWriter.update(parentRecord);
    return mergeResult;
  }

  private MergeResult mergeCustomerVehicle(MergeResult<CustomerDTO, MergeCustomerSnap> mergeResult, CustomerDTO parent, Long childId) throws CloneNotSupportedException {
    UserWriter userWriter = userDaoManager.getWriter();
    List<CustomerVehicle> childVehicles = userWriter.getVehicleByCustomerId(childId);
    Map<Long, CustomerVehicle> childVehicleMap = CustomerVehicle.listToMap(childVehicles);
    List<CustomerVehicle> parentVehicles = userWriter.getVehicleByCustomerId(parent.getId());
    Map<Long, CustomerVehicle> parentVehicleMap = CustomerVehicle.listToMap(parentVehicles);
    List<CustomerVehicleDTO> customerVehicleDTOs = null;
    Vehicle vehicle = null;
    CustomerVehicleDTO customerVehicleDTO = null;
    if (CollectionUtil.isNotEmpty(parentVehicles)) {
      customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
      for (CustomerVehicle customerVehicle : parentVehicles) {
        vehicle = userWriter.getVehicleById(mergeResult.getShopId(), customerVehicle.getVehicleId());
        if (vehicle == null) {
          LOG.error("error vehicle info! vehicle不存在。customerVehicle的customerID:{}, vehicleID:{}", customerVehicle.getCustomerId(), customerVehicle.getVehicleId());
          continue;
        }
        customerVehicleDTO = customerVehicle.toDTO();
        customerVehicleDTO.setVehicleDTO(vehicle.toDTO());
        customerVehicleDTOs.add(customerVehicleDTO);
      }
    }
    mergeResult.getMergeSnapMap().get(childId).setParentVehicles(customerVehicleDTOs);
    customerVehicleDTOs = null;
    if (CollectionUtil.isNotEmpty(childVehicles)) {
      customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
      for (CustomerVehicle customerVehicle : childVehicles) {
        vehicle = userWriter.getVehicleById(mergeResult.getShopId(), customerVehicle.getVehicleId());
        if (vehicle == null) {
          LOG.error("error vehicle info! vehicle不存在。customerVehicle的customerID:{}, vehicleID:{}", customerVehicle.getCustomerId(), customerVehicle.getVehicleId());
          continue;
        }
        customerVehicleDTO = customerVehicle.toDTO();
        customerVehicleDTO.setVehicleDTO(vehicle.toDTO());
        customerVehicleDTOs.add(customerVehicleDTO);
      }
    }
    mergeResult.getMergeSnapMap().get(childId).setChildVehicles(customerVehicleDTOs);
    if (CollectionUtils.isEmpty(childVehicleMap.keySet())) {
      LOG.info("被合并的客户没有车辆，childId={}", childId);
      return mergeResult;
    }
    CustomerVehicle childVehicle = null;
    CustomerVehicle newVehicle = null;
    for (Long childVehicleId : childVehicleMap.keySet()) {
      childVehicle = childVehicleMap.get(childVehicleId);
      if (parentVehicleMap.get(childVehicleId) != null) {   //父子有同一辆车
        continue;
      }
      newVehicle = childVehicle.clone();
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_disable_customerVehicle(mergeResult.getShopId(), mergeResult.getUserId(),
          childVehicle.getId()));
      newVehicle.setCustomerId(parent.getId());
      userWriter.save(newVehicle);
      childVehicle.setStatus(VehicleStatus.DISABLED);
      userWriter.update(childVehicle);
    }
    return mergeResult;
  }

  private MergeResult mergeCustomerMember(MergeResult<CustomerDTO, MergeCustomerSnap> mergeResult, Member parentMember, Long parentId, Long childId) throws Exception {
    UserWriter userWriter = userDaoManager.getWriter();
    Member childMember = userWriter.getMemberByCustomerId(mergeResult.getShopId(), childId);
    if (childMember == null) {
      LOG.info("被合并客户非会员");
      return mergeResult;
    }
    List<MemberService> childMemerServices = userWriter.getMemberServicesByMemberId(childMember.getId());
    MemberDTO childMemberDTO = childMember.toDTO();
    List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
    if (CollectionUtil.isNotEmpty(childMemerServices)) {
      for (MemberService memberService : childMemerServices) {
        if (memberService == null) continue;
        memberServiceDTOs.add(memberService.toDTO());
      }
    }
    childMemberDTO.setMemberServiceDTOs(memberServiceDTOs);
    mergeResult.getMergeSnapMap().get(childId).setChildMember(childMemberDTO);
    childMemberDTO.setMemberServiceDTOs(memberServiceDTOs);
    mergeResult.getMergeSnapMap().get(childId).setChildMember(childMemberDTO);
    if (parentMember == null) {
      Member newMember = childMember.clone();
      newMember.setCustomerId(parentId);
      userWriter.save(newMember);
      for (MemberService membersService : childMemerServices) {
        membersService.setMemberId(newMember.getId());
        userWriter.update(membersService);
      }
      mergeResult.setChildMemberId(childMember.getId());
      mergeResult.setMergedMemberId(newMember.getId());
      childMember.setStatus(MemberStatus.DISABLED);
      userWriter.update(childMember);
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_disable_Member(mergeResult.getShopId(), mergeResult.getUserId(), childMember.getId()));
      return mergeResult;
    }
    //父子都是会员
    parentMember.setBalance(parentMember.getBalance() + childMember.getBalance());
    mergeResult.setChildMemberId(childMember.getId());
    mergeResult.setMergedMemberId(parentMember.getId());
    childMember.setStatus(MemberStatus.DISABLED);
    userWriter.update(parentMember);
    userWriter.update(childMember);
    Map<Long, MemberService> parentServiceMap = MemberService.listToMap(userWriter.getMemberServicesByMemberId(parentMember.getId()));
    Map<Long, MemberService> childServiceMap = MemberService.listToMap(childMemerServices);
    MemberService parentService = null;
    MemberService childService = null;
    Integer times = null;
    Long deadline = null;
    for (Long serviceId : childServiceMap.keySet()) {
      childService = childServiceMap.get(serviceId);
      parentService = parentServiceMap.get(serviceId);
      if (parentService == null) {
        childService.setMemberId(parentMember.getId());
        userWriter.update(childService);
        continue;
      }

      childService.setStatus(MemberStatus.DISABLED);
      userWriter.update(childService);
      userWriter.update(parentService);
    }
    return mergeResult;
  }

  public List<CustomerDTO> getCustomers(CustomerDTO customerIndex) {
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    UserWriter userWriter = userDaoManager.getWriter();
    List<Customer> customers = userWriter.getCustomers(customerIndex);
    if (!CollectionUtils.isEmpty(customers)) {
      for (Customer customer : customers) {
        CustomerDTO customerDTO = customer.toDTO();
        setCustomerContacts(customerDTO.getId(), userWriter, customerDTO); // add by zhuj
        customerDTOs.add(customerDTO);
      }

    }
    return customerDTOs;
  }


  private void setCustomerContacts(Long customerId, UserWriter writer, CustomerDTO customerDTO) {
    // add by zhuj 联系人列表
    ContactDTO[] contactDTOs = new ContactDTO[3];
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(customerId, null, customerDTO.getShopId(), null, null);
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
          }
        }
      }
    }
    customerDTO.setContacts(contactDTOs);
  }

  private MergeResult mergeCustomer(MergeResult<CustomerDTO, MergeCustomerSnap> mergeResult, CustomerDTO parent, Long childId) throws BcgogoException {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    UserWriter userWriter = userDaoManager.getWriter();
    CustomerDTO customerIndex = new CustomerDTO();
    customerIndex.setShopId(parent.getShopId());
    customerIndex.setId(childId);
    Customer child = CollectionUtil.uniqueResult(userWriter.getCustomers(customerIndex));
    if (child == null || CustomerStatus.DISABLED.equals(child.getStatus())) {
      mergeResult.setCustomerOrSupplierIdStr(String.valueOf(childId));
      mergeResult.setSuccess(false);
      mergeResult.setMsg("被合并的客户不存在，或被删除，或被合并！");
      return mergeResult;
    }
    MergeCustomerSnap mergeSnap = mergeResult.getMergeSnapMap().get(childId);
    mergeSnap.setChildId(child.getId());
    mergeSnap.setChildName(child.getName());
    Map settlementTypeMap = TxnConstant.getSettlementTypeMap(mergeResult.getLocale());
    Map inoiceCatagoryMap = TxnConstant.getInvoiceCatagoryMap(mergeResult.getLocale());
    Map<String, String> areaMap = TxnConstant.getAreaMap(mergeResult.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(mergeResult.getLocale());
    CustomerDTO childDTO = child.toDTO();
    setCustomerContacts(childDTO.getId(), userWriter, childDTO);
    childDTO.setSettlementTypeStr(String.valueOf(settlementTypeMap.get(String.valueOf(childDTO.getSettlementType()))));
    childDTO.setInvoiceCategoryStr(String.valueOf(settlementTypeMap.get(String.valueOf(childDTO.getInvoiceCategory()))));
    childDTO.setAreaStr(String.valueOf(settlementTypeMap.get(childDTO.getArea())));
    childDTO.setCustomerKindStr(String.valueOf(settlementTypeMap.get(childDTO.getCustomerKind())));
    mergeSnap.setChild(childDTO);
    child.setParentId(parent.getId());
    child.setStatus(CustomerStatus.DISABLED);
    userWriter.update(child);
    return mergeResult;
  }

  public MergeTask getMergeTaskOneByOne() {
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getMergeTaskOneByOne();
  }

  public void deleteMergeTask(Long taskId) throws BcgogoException {
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      MergeTask task = userWriter.findById(MergeTask.class, taskId);
      if (task == null) {
        return;
      }
      task.setExeStatus(ExeStatus.FINISHED);
      userWriter.update(task);
      userWriter.commit(status);
    } finally {
      userWriter.rollback(status);
    }
  }

  public void saveExceptionMergeTask(Long taskId) throws BcgogoException {
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      MergeTask task = userWriter.findById(MergeTask.class, taskId);
      if (task == null) {
        return;
      }
      task.setExeStatus(ExeStatus.EXCEPTION);
      userWriter.update(task);
      userWriter.commit(status);
    } finally {
      userWriter.rollback(status);
    }
  }

  @Override
  public List<CustomerDTO> getRelatedCustomersByShopId(Long wholeSalerShopId) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<Customer> customers = userWriter.getRelatedCustomersByShopId(wholeSalerShopId);
    if (CollectionUtil.isEmpty(customers)) {
      return new ArrayList<CustomerDTO>();
    }
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    for (Customer customer : customers) {
      if (null == customer) {
        continue;
      }
      customerDTOs.add(customer.toDTO());
    }
    return customerDTOs;
  }

  @Override
  public List<CustomerDTO> getShopRelatedCustomer(Long wholeSalerShopId, Long... customerIds) {
    if (ArrayUtil.isEmpty(customerIds)) {
      return null;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    List<Customer> customers = userWriter.getShopRelatedCustomer(wholeSalerShopId, customerIds);
    if (CollectionUtil.isEmpty(customers)) {
      return null;
    }
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    Set<Long> customerIdSet = new HashSet<Long>();
    for (Customer customer : customers) {
      if (null == customer) {
        continue;
      }
      customerDTOs.add(customer.toDTO());
      customerIdSet.add(customer.getId());
    }
    Map<Long, ContactDTO> map = userWriter.getMainContactByCusId(customerIdSet.toArray(new Long[customerIdSet.size()]));
    for (CustomerDTO customerDTO : customerDTOs) {
      ContactDTO dto = map.get(customerDTO.getId());
      if (dto != null) customerDTO.setMobile(dto.getMobile());
    }
    return customerDTOs;
  }

  @Override
  public int countRelatedCustomersByShopId(Long wholeSalerShopId) {
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.countRelatedCustomersByShopId(wholeSalerShopId);
  }

  @Override
  public List<Long> getRelatedCustomerIdListByShopId(Long wholeSalerShopId) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<Customer> customers = userWriter.getRelatedCustomersByShopId(wholeSalerShopId);
    if (CollectionUtil.isEmpty(customers)) {
      return null;
    }
    List<Long> idList = new ArrayList<Long>();
    for (Customer customer : customers) {
      if (null == customer) {
        continue;
      }
      idList.add(customer.getId());
    }
    return idList;
  }

  @Override
  public List<SupplierDTO> getWholeSalersByCustomerShopId(Long shopId) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<Supplier> suppliers = userWriter.getWholeSalersByCustomerShopId(shopId);
    if (CollectionUtils.isEmpty(suppliers)) {
      return new ArrayList<SupplierDTO>();
    }
    List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
    for (Supplier supplier : suppliers) {
      supplierDTOs.add(supplier.toDTO());
    }
    return supplierDTOs;
  }


  private IUserService userService;

  @Autowired
  private CustomerImporter customerImporter;

  @Autowired
  private UserDaoManager userDaoManager;

  public IUserService getUserService() {
    if (userService == null) {
      userService = ServiceManager.getService(IUserService.class);
    }
    return userService;
  }

  @Override
  public CustomerRecordDTO getCustomerRecordDTOByCustomerId(Long shopId, Long customerId) {
    UserWriter writer = userDaoManager.getWriter();

    CustomerRecord customerRecord = writer.getCustomerRecordDTOByCustomerId(shopId, customerId);

    if (null == customerRecord) {
      return null;
    }

    return customerRecord.toDTO();
  }


  public Result saveOrUpdateCustomerInfo(SalesReturnDTO salesReturnDTO) throws Exception {
    IContactService contactService = ServiceManager.getService(IContactService.class);
    if (salesReturnDTO == null || StringUtil.isEmpty(salesReturnDTO.getCustomer())) {
      throw new Exception("客户信息为空");
    }
    Result result = null;


    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    CustomerDTO customerDTO = null;
    try {
      if (salesReturnDTO.getCustomerId() != null) {
        customerDTO = getUserService().getCustomerDTOByCustomerId(salesReturnDTO.getCustomerId(), salesReturnDTO.getShopId());
      }

      if (customerDTO == null) {
        //新增
        customerDTO = new CustomerDTO();
        salesReturnDTO.setCustomerId(null);
        salesReturnDTO.setContactId(null);
        customerDTO.createFromSaleReturnDTO(salesReturnDTO);
        getUserService().createCustomer(customerDTO);
        salesReturnDTO.setCustomerId(customerDTO.getId());
        if (!ArrayUtils.isEmpty(customerDTO.getContacts())
            && customerDTO.getContacts()[0] != null
            && customerDTO.getContacts()[0].getId() != null) {
          salesReturnDTO.setContactId(customerDTO.getContacts()[0].getId());
        }
        //创建客户消费记录表
        CustomerRecord customerRecord = new CustomerRecord();
        customerRecord.setCustomerId(customerDTO.getId());
        customerRecord.setShopId(salesReturnDTO.getShopId());
        customerRecord.setName(salesReturnDTO.getCustomer());
        customerRecord.setMobile(salesReturnDTO.getMobile());
        customerRecord.setLastDate(System.currentTimeMillis());
        customerRecord.setLastAmount(salesReturnDTO.getTotal());
        customerRecord.setContact(salesReturnDTO.getContact());
        customerRecord.setLastBill(this.combinateSaleOrderForCustomerLastBill(salesReturnDTO));
        customerRecord.setLastBillShort(this.combinateSaleOrderForCustomerLastBillShort(customerRecord.getLastBill()));
        customerRecord.setTotalReturnAmount(salesReturnDTO.getSettledAmount() + NumberUtil.doubleVal(salesReturnDTO.getAccountDebtAmount()));
        customerRecord.setTotalPayable(salesReturnDTO.getAccountDebtAmount());
        if (salesReturnDTO.getStrikeAmount() > 0.001) {
          result = new Result("该客户为新客户，没有欠款，不能冲账", false);
        } else {
          writer.save(customerRecord);
          result = new Result("客户信息更新成功", true);
        }
      } else {
        //customer = customers.get(0);
        // modified by zhuj
        // TODO 联系人怎么处理

        customerDTO.updateFromSaleReturnDTO(salesReturnDTO);
        getUserService().updateCustomer(writer, customerDTO);
        salesReturnDTO.setCustomerId(customerDTO.getId());

        boolean isHaveRelatedSupplier = customerDTO.getSupplierId() != null;
        if (isHaveRelatedSupplier) {
          SupplierDTO supplierDTO = CollectionUtil.getFirst(getUserService().getSupplierById(salesReturnDTO.getShopId(), customerDTO.getSupplierId()));
          if (supplierDTO != null) {
            supplierDTO.updateFromSaleReturnDTO(salesReturnDTO);
            getUserService().updateSupplier(writer, supplierDTO);
            contactService.updateContactsBelongCustomerAndSupplier(writer, customerDTO.getId(), customerDTO.getSupplierId(), customerDTO.getShopId(), customerDTO.getContacts());
            contactService.addContactsBelongCustomerAndSupplier(writer, customerDTO.getId(), customerDTO.getSupplierId(), customerDTO.getShopId(), customerDTO.getContacts());
          }
        }

        boolean isHaveSameContact = false;
        if (salesReturnDTO.getContactId() != null && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
          for (ContactDTO contactDTO : customerDTO.getContacts()) {
            if (contactDTO != null && salesReturnDTO.getContactId().equals(contactDTO.getId())) {
              isHaveSameContact = true;
              salesReturnDTO.setContactId(contactDTO.getId());
              break;
            }
          }
        }
        if (!isHaveSameContact && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
          ContactDTO contactDTO = customerDTO.getContacts()[0];
          if (contactDTO != null) {
            salesReturnDTO.setContactId(contactDTO.getId());
          }
        }

        //更新客户消费记录表 累计消费,最近消费
        List<CustomerRecord> customerRecords = writer.getShopCustomerRecordByCustomerId(salesReturnDTO.getCustomerId());
        if (null != customerRecords && customerRecords.size() > 0) {

          customerRecords.get(0).setLastDate(System.currentTimeMillis());
          customerRecords.get(0).setLastAmount(salesReturnDTO.getTotal());
          customerRecords.get(0).setContact(salesReturnDTO.getContact());
          customerRecords.get(0).setLastBill(this.combinateSaleOrderForCustomerLastBill(salesReturnDTO));
          customerRecords.get(0).setLastBillShort(this.combinateSaleOrderForCustomerLastBillShort(customerRecords.get(0).getLastBill()));
          customerRecords.get(0).setTotalReturnAmount(customerRecords.get(0).getTotalReturnAmount() + salesReturnDTO.getSettledAmount() + NumberUtil.doubleVal(salesReturnDTO.getAccountDebtAmount()));
          salesReturnDTO.setAccountDebtAmount(null == salesReturnDTO.getAccountDebtAmount() ? 0D : salesReturnDTO.getAccountDebtAmount());
          customerRecords.get(0).setTotalPayable(null == customerRecords.get(0).getTotalPayable() ? 0D : customerRecords.get(0).getTotalPayable());
          customerRecords.get(0).setTotalPayable(customerRecords.get(0).getTotalPayable() + salesReturnDTO.getAccountDebtAmount());

          if (salesReturnDTO.getStrikeAmount() > 0.001) {
            customerRecords.get(0).setTotalReceivable(
                customerRecords.get(0).getTotalReceivable() - salesReturnDTO.getStrikeAmount());
            customerRecords.get(0).setRepayDate(null);
          }
          writer.save(customerRecords.get(0));
          result = new Result("客户信息更新成功", true);
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    return result;
  }


  public List<Customer> getCustomerByNameAndMobile(long shopId, String name, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    return writer.getCustomerByNameAndMobile(shopId, name, mobile);
  }

  public void updateCustomerRecordForRepairOrder(RepairOrderDTO repairOrderDTO) {
    userService = ServiceManager.getService(IUserService.class);
    Long shopId = repairOrderDTO.getShopId();
    CustomerRecord customerRecord = userService.getShopCustomerRecordByCustomerId(
        shopId, repairOrderDTO.getCustomerId());
    CustomerRecordDTO customerRecordDTO = null;
    if (customerRecord != null) {
      customerRecordDTO = customerRecord.toDTO();
    } else {     //    BCSHOP-3161
      LOG.warn("customerId 为{},shopId 为{},的CustomerRecord 信息缺失：{}", repairOrderDTO.getCustomerId(), shopId);
    }
    if (null != customerRecordDTO) {
      customerRecordDTO.setLastDate(System.currentTimeMillis());
      customerRecordDTO.setLastAmount(repairOrderDTO.getAfterMemberDiscountTotal() == null ? repairOrderDTO.getTotal() : repairOrderDTO.getAfterMemberDiscountTotal());
      customerRecordDTO.setBrand(repairOrderDTO.getBrand());
      customerRecordDTO.setModel(repairOrderDTO.getModel());
      customerRecordDTO.setYear(repairOrderDTO.getYear());
      customerRecordDTO.setEngine(repairOrderDTO.getEngine());
      customerRecordDTO.setMobile(repairOrderDTO.getMobile());
      customerRecordDTO.setLicenceNo(repairOrderDTO.getLicenceNo());
      customerRecordDTO.setContact(repairOrderDTO.getContact());
      customerRecordDTO.setName(repairOrderDTO.getCustomerName());
      //保存顾客最后一次消费情况
      customerRecordDTO.setLastBill(combinateRepairOrderForCustomerLastBill(repairOrderDTO));
      customerRecordDTO.setLastBillShort(combinateRepairOrderForCustomerLastBillShort(customerRecordDTO.getLastBill()));
      if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
        customerRecordDTO.setTotalReceivable(
            customerRecordDTO.getTotalReceivable() + (repairOrderDTO.getDebt() - repairOrderDTO.getDebtHid()));
        customerRecordDTO.setTotalAmount(
            customerRecordDTO.getTotalAmount() + repairOrderDTO.getSettledAmount() + repairOrderDTO.getDebt());

        customerRecordDTO.setConsumeTimes(NumberUtil.longValue(customerRecordDTO.getConsumeTimes()) + 1);
        customerRecordDTO.setMemberConsumeTotal(NumberUtil.doubleVal(customerRecordDTO.getMemberConsumeTotal()) + NumberUtil.doubleVal(repairOrderDTO.getMemberAmount()));

        if(NumberUtil.doubleVal(repairOrderDTO.getMemberAmount()) > 0 || null != repairOrderDTO.getMemberDiscountRatio()) {
          customerRecordDTO.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()) + 1);
        }

        try {
          if (null != repairOrderDTO.getHuankuanTime() && !"".equals(repairOrderDTO.getHuankuanTime()))
            customerRecordDTO.setRepayDate(
                DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getHuankuanTime()));
        } catch (ParseException e) {
          LOG.error(e.getMessage(), e);
        }
      }
      try {
        userService.updateCustomerRecord(customerRecordDTO);
      } catch (Exception e) {
        LOG.error("CustomerService.updateCustomerRecordForRepairOrder 更新customerRecord失败.");
        LOG.error(e.getMessage(), e);
      }
    }
  }

  /**
   * 比对Customer与历史单据中的Customer是否一致. 比对字段：
   * name, company, contact, mobile, landline, address
   *
   * @param historyCustomerDTO
   * @param shopId
   * @return
   */
  @Override
  public boolean compareCustomerSameWithHistory(CustomerDTO historyCustomerDTO, Long shopId) {
    if (historyCustomerDTO == null) {
      return false;
    }
    CustomerDTO customerDTO = getCustomerById(historyCustomerDTO.getId());
    if (customerDTO == null) {
      return false;
    }
    return customerDTO.compareHistory(historyCustomerDTO);
  }

  @Override
  public void saveOrUpdateCustomerVehicle(InsuranceOrderDTO insuranceOrderDTO) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Vehicle vehicle = null;
      boolean newVehicle = false;
      List<Vehicle> vehicles = writer.getVehicleByLicenceNo(insuranceOrderDTO.getShopId(), insuranceOrderDTO.getLicenceNo());
      if (vehicles.size() == 0) {    //车牌号码不存在
        vehicle = new Vehicle(insuranceOrderDTO);
        writer.save(vehicle);
        newVehicle = true;
      } else {        //车牌号码存在
        vehicle = vehicles.get(0);
        VehicleModifyLogDTO oldLog = new VehicleModifyLogDTO();
        oldLog.setBrand(vehicle.getBrand());
        oldLog.setModel(vehicle.getModel());
        vehicle.fromInsuranceOrderDTO(insuranceOrderDTO);
        writer.update(vehicle);

        VehicleModifyLogDTO newLog = new VehicleModifyLogDTO();
        newLog.setBrand(vehicle.getBrand());
        newLog.setModel(vehicle.getModel());
        List<VehicleModifyLogDTO> logDTOs = VehicleModifyLogDTO.compare(oldLog, newLog);
        if (CollectionUtils.isNotEmpty(logDTOs)) {
          for (VehicleModifyLogDTO dto : logDTOs) {
            dto.setShopId(insuranceOrderDTO.getShopId());
            dto.setUserId(insuranceOrderDTO.getUserId());
            dto.setVehicleId(vehicle.getId());
            dto.setOperationType(VehicleModifyOperations.INSURANCE);
          }
        }
        batchCreateVehicleModifyLog(logDTOs);
      }
      insuranceOrderDTO.setVehicleId(vehicle.getId());
      Customer customer = null;
      if (!newVehicle) {   //车牌id已经存在
        List<CustomerVehicle> customerVehicles = writer.getCustomerVehicleByVehicleId(vehicle.getId());
        if (null != customerVehicles && customerVehicles.size() > 0 && customerVehicles.get(0) != null && customerVehicles.get(0).getCustomerId() != null) {
          customer = writer.getById(Customer.class, customerVehicles.get(0).getCustomerId());
        }
        if (customer != null) {    //车牌id对应的客户存在
          if (customer.updateFromInsurance(insuranceOrderDTO)) {
            writer.update(customer);
            insuranceOrderDTO.setCustomerId(customer.getId());
          }
        }
      } else {   //车牌不存在
        //find existing customer
        List<Customer> customers = new ArrayList<Customer>();
        //先根据customerId找客户
        if (insuranceOrderDTO.getCustomerId() != null) {
          Customer idCustomer = writer.getCustomerByCustomerIdAndShopId(insuranceOrderDTO.getCustomerId(), insuranceOrderDTO.getShopId());
          if (null != idCustomer) {
            customers.add(idCustomer);
          }
        }
        //如果没有再根据名字和手机找
        if (customers.size() == 0) {
          customers = writer.getCustomerByNameAndMobile(insuranceOrderDTO.getShopId(),
              insuranceOrderDTO.getCustomer(), insuranceOrderDTO.getMobile());
        }
        //还没有就增加新客户
        if (customers.size() == 0) {  //新客户
          customer = new Customer();
          customer.fromInsuranceOrderDTO(insuranceOrderDTO);
          writer.save(customer);
          insuranceOrderDTO.setCustomerId(customer.getId());
          CustomerRecord customerRecord = new CustomerRecord(insuranceOrderDTO);
          writer.save(customerRecord);

          CustomerVehicle cv = new CustomerVehicle();
          cv.setVehicleId(vehicle.getId());
          cv.setCustomerId(customer.getId());
          writer.save(cv);
        } else {
          customer = customers.get(0);
          if (customer.updateFromInsurance(insuranceOrderDTO)) {
            writer.update(customer);
            insuranceOrderDTO.setCustomerId(customer.getId());
          }
          // 新车 老顾客 要添加关联记录
          CustomerVehicle cv = new CustomerVehicle();
          cv.setVehicleId(vehicle.getId());
          cv.setCustomerId(customer.getId());
          writer.save(cv);
        }
        insuranceOrderDTO.setCustomerDTO(customer.toDTO());
      }

      writer.commit(status);

      try {
        ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(insuranceOrderDTO.getShopId(), vehicle.getId());
      } catch (Exception e) {
        LOG.error("shopId:{}", insuranceOrderDTO.getShopId());
        LOG.error("vehicleId:{}", StringUtil.arrayToStr(",", vehicle.getId()));
        LOG.error("createVehicleSolrIndex 失败！", e);
      }

    } finally {
      writer.rollback(status);
    }
  }

  public List<Customer> getAllCustomerByNameAndMobile(long shopId, String name, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    return writer.getAllCustomerByNameAndMobile(shopId, name, mobile);
  }

  public List<Supplier> getAllSupplierByNameAndMobile(long shopId, String name, String mobile) {
    UserWriter writer = userDaoManager.getWriter();

    return writer.getAllSupplierByNameAndMobile(shopId, name, mobile);
  }


  @Override
  public void batchUpdateCustomer(List<CustomerDTO> customerDTOList) {
    if (CollectionUtil.isEmpty(customerDTOList)) return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Customer customer;
      for (CustomerDTO dto : customerDTOList) {
        customer = writer.getById(Customer.class, dto.getId());
        customer.fromDTO(dto);
        writer.save(customer);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<CustomerDTO> getCustomerByShopId(Long shopId, int start, int pageSize) {
    List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customerList = writer.getCustomerByShopId(shopId, start, pageSize);
    for (Customer customer : customerList) {
      customerDTOList.add(customer.toDTO());
    }
    return customerDTOList;
  }

  @Override
  public List<CustomerDTO> getSimilarCustomer(CustomerDTO customerDTO) {
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    List<Customer> customers = userDaoManager.getWriter().getSimilarCustomer(customerDTO);
    if (CollectionUtil.isNotEmpty(customers)) {
      for (Customer customer : customers) {
        customerDTOs.add(customer.toDTO());
      }
    }
    return customerDTOs;
  }

  @Override
  public List<Long> cancelCustomerRelationAndReindex(Long customerShopId, Long supplierShopId) {
    List<Long> customerIds = new ArrayList<Long>();
    if (customerShopId == null || supplierShopId == null) {
      return customerIds;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Customer> customers = writer.getCustomerByCustomerShopIdAndShopId(supplierShopId, customerShopId);

      if (CollectionUtil.isNotEmpty(customers)) {
        for (Customer customer : customers) {
          customer.setRelationType(RelationTypes.UNRELATED);
          customer.setCustomerShopId(null);
          writer.update(customer);
          customerIds.add(customer.getId());
        }
        writer.commit(status);
      }
      return customerIds;
    } finally {
      writer.rollback(status);
    }
  }


  /**
   * 从excel中导入客户
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public ImportResult simpleImportCustomerFromExcel(ImportContext importContext) throws Exception {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.directParseData(importContext);

    //2.校验数据
    CheckResult checkResult = customerImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = customerImporter.importData(importContext);

    return importResult;

  }

  @Override
  public Map<String, CustomerDTO> getMobileCustomerMapOnlyForMobileCheck(Long shopId) {
    Map<String, CustomerDTO> map = new HashMap<String, CustomerDTO>();
    if (null == shopId) {
      return map;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customerList = writer.getCustomerByShopId(shopId);
    Set<Long> mobileSet = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(customerList)) {
      for (Customer customer : customerList) {
        mobileSet.add(customer.getId());
      }
      Map<Long, List<Contact>> contactMap = writer.getContactsByCusIds(new ArrayList<Long>(mobileSet));
      List<Contact> contactList = new ArrayList<Contact>();
      CustomerDTO customerDTO;
      for (Customer customer : customerList) {
        if (contactMap != null) {
          contactList = contactMap.get(customer.getId());
        }
        customerDTO = customer.toDTO();
        if (CollectionUtil.isNotEmpty(contactList)) {
          for (Contact c : contactList) {
            if (StringUtils.isBlank(c.getMobile())) {
              continue;
            }
            if (map.get(c.getMobile()) != null) {
              continue;
            }
            map.put(c.getMobile(), customerDTO);
          }
        }
      }
    }
    return map;
  }

  @Override
  public Map<String, CustomerDTO> getLandLineCustomerMap(Long shopId) {
    Map<String, CustomerDTO> map = new HashMap<String, CustomerDTO>();
    if (null == shopId) {
      return map;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customerList = writer.getCustomerByShopId(shopId);
    if (CollectionUtils.isNotEmpty(customerList)) {
      for (Customer customer : customerList) {
        if (StringUtils.isBlank(customer.getLandLine())) {
          continue;
        }
        if (map.get(customer.getLandLine()) != null) {
          continue;
        }
        map.put(customer.getLandLine(), customer.toDTO());
      }
    }
    return map;
  }

  public Result saveOrUpdateCustomerByCsDTO(Result result, CustomerOrSupplierDTO csDTO) throws BcgogoException {
    if (csDTO == null || csDTO.getShopId() == null || StringUtil.isEmpty(csDTO.getName())) {
      return result.LogErrorMsg("客户信息异常！");
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = null;
    if (csDTO.getCustomerOrSupplierId() == null) {
      customerDTO = new CustomerDTO();
    } else {
      customerDTO = userService.getCustomerDTOByCustomerId(csDTO.getCustomerOrSupplierId(), csDTO.getShopId());
      if (customerDTO == null) {
        customerDTO = new CustomerDTO();
      }
    }
    customerDTO.fromCustomerOrSupplierDTO(csDTO);
    if (customerDTO.getId() != null) {
      userService.updateCustomer(customerDTO);
    } else {
      userService.createCustomer(customerDTO);
    }


    if (csDTO.getCustomerOrSupplierId() == null) {
      CustomerRecordDTO recordDTO = new CustomerRecordDTO();
      recordDTO.setCustomerId(customerDTO.getId());
      recordDTO.setShopId(customerDTO.getShopId());
      userService.createCustomerRecord(recordDTO);
    }
    csDTO.setCustomerOrSupplierId(customerDTO.getId());
    return result;
  }

  @Override
  public Map<Long, CustomerRecordDTO> getCustomerRecordMap(Long shopId, Long... customerId) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    Map<Long, CustomerRecordDTO> customerRecordDTOMap = new HashMap<Long, CustomerRecordDTO>();
    if (ArrayUtil.isEmpty(customerId)) return customerRecordDTOMap;
    List<CustomerRecord> customerRecordList = writer.getCustomerRecordByCustomerId(shopId, customerId);
    if (CollectionUtils.isNotEmpty(customerRecordList)) {
      for (CustomerRecord customerRecord : customerRecordList) {
        customerRecordDTOMap.put(customerRecord.getCustomerId(), customerRecord.toDTO());
      }
    }
    return customerRecordDTOMap;
  }

  @Override
  public Map<Long, MemberDTO> getCustomerMemberMap(Long shopId, Long... customerId) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    Map<Long, MemberDTO> memberDTOMap = new HashMap<Long, MemberDTO>();
    if(ArrayUtils.isEmpty(customerId)) return memberDTOMap;
    List<Member> memberList = writer.getCustomerMemberByCustomerId(shopId, customerId);

    if (CollectionUtils.isNotEmpty(memberList)) {
      for (Member member : memberList) {
        memberDTOMap.put(member.getCustomerId(), member.toDTO());
      }
    }
    return memberDTOMap;
  }

  @Override
  public List<Long> getCustomerIdList(Long shopId, int start, int pageSize) {
    UserWriter writer = userDaoManager.getWriter();

    return writer.getCustomerIds(shopId, start, pageSize);
  }

  @Override
  public Map<Long, List<VehicleDTO>> getCustomerLicenseNosForReindex(Long shopId, List<Long> ids) {
    UserWriter writer = userDaoManager.getWriter();
    List<Object> list = writer.getCustomerLicenseNosForReindex(shopId, ids);
    Map<Long, List<VehicleDTO>> map = new HashMap<Long, List<VehicleDTO>>();
    Long customerId = null;
    Vehicle vehicle = null;
    List<VehicleDTO> vehicleDTOList = null;
    if (CollectionUtils.isEmpty(list)) return map;
    for (int i = 0, max = list.size(); i < max; i++) {
      Object[] o = (Object[]) list.get(i);
      customerId = (Long) o[0];
      vehicle = (Vehicle) o[1];
      if (!VehicleStatus.DISABLED.equals(vehicle.getStatus())) {
        vehicleDTOList = map.get(customerId);
        if (vehicleDTOList == null) {
          vehicleDTOList = new ArrayList<VehicleDTO>();
        }
        vehicleDTOList.add(vehicle.toDTO());
        map.put(customerId, vehicleDTOList);
      }
    }
    return map;
  }

  @Override
  public List<CustomerDTO> getCustomerByPageSizeAndStart(int pageSize, int start) {
    List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<Customer> customers = writer.getCustomersByPage(pageSize, start);
    if (!CollectionUtils.isEmpty(customers)) {
      for (Customer customer : customers) {
        customerDTOs.add(customer.toDTO());
      }
      return customerDTOs;
    }
    return customerDTOs;
  }

  @Override
  public CustomerDTO isCustomerExist(Long shopId, String name, String mobile, String phone) throws BcgogoException {

    if (StringUtils.isBlank(name)) {
      LOG.error("[isCustomerExist] name is all blank.");
      throw new BcgogoException(BcgogoExceptionType.NullException);
    }

    if (StringUtils.isBlank(name) && StringUtils.isBlank(mobile) && StringUtils.isBlank(phone)) {
      LOG.error("[isCustomerExist] input param is all blank.");
      throw new BcgogoException(BcgogoExceptionType.NullException);
    }

    UserWriter writer = userDaoManager.getWriter();

    CustomerDTO customerDTO = null;
    List<Customer> customers = null;
    // 名称存在 手机号、座机号不存在
    if (StringUtils.isBlank(mobile) && StringUtils.isBlank(phone)) {
      customers = writer.getCustomerByName(shopId, name);
    }

    // 座机存在 校验座机
    if (StringUtils.isNotBlank(phone)) {
      customers = writer.getCustomerByTelephone(shopId, phone);
    }

    // 手机存在校验手机
    if (StringUtils.isNotBlank(mobile)) {
      //手机校验
      List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, null, shopId, null, mobile);
      if (!CollectionUtils.isEmpty(contactList)) {
        List<Long> customerIds = new ArrayList<Long>();
        for (Contact contact : contactList) {
          if (!customerIds.contains(contact.getCustomerId())) {
            customerIds.add(contact.getCustomerId());
          }
        }
        if (!CollectionUtils.isEmpty(customerIds)) {
          customers = writer.getCustomerEntityByIds(customerIds); // 这里事实上只有一个吧
        }
      }
    }

    if (!CollectionUtils.isEmpty(customers)) {
      for (Customer customer : customers) {
        if (StringUtils.equals(customer.getName(), name)) {
          customerDTO = customer.toDTO();
        }
      }
    }

    if (customerDTO != null) {
      // 查询客户的联系人
      List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(customerDTO.getId(), null, shopId, null, null);
      if (!CollectionUtils.isEmpty(contactList)) {
        int size = contactList.size();
        if (size > 3) {
          LOG.warn("contactList size is over 3,customerId is" + customerDTO.getId());
          size = 3;
        }
        ContactDTO[] contactDTOs = new ContactDTO[3];
        for (int i = 0; i < size; i++) {
          ContactDTO contactDTO = contactList.get(i).toDTO();
          contactDTOs[i] = contactDTO;
          if (StringUtils.equals(contactDTO.getMobile(), mobile)) {
            customerDTO.setContactId(contactDTO.getId()); // 记录是哪个联系人查询到的客户
          }
        }
        customerDTO.setContacts(contactDTOs);
      }
    }

    return customerDTO;
  }

  @Override
  public void addCancelRecommendAssociatedCount(Set<Long> customerIds) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Customer> customers = writer.getCustomerByIds(customerIds.toArray(new Long[customerIds.size()]));
      if (CollectionUtil.isNotEmpty(customers)) {
        for (Customer supplier : customers) {
          supplier.addCancelRecommendAssociatedCount();
          writer.update(supplier);
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void cancelApplyRecommendAssociated(Set<Long> customerIds) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Customer> customers = writer.getCustomerByIds(customerIds.toArray(new Long[customerIds.size()]));
      if (CollectionUtil.isNotEmpty(customers)) {
        for (Customer supplier : customers) {
          supplier.setCancelRecommendAssociatedCount(-1);
          writer.update(supplier);
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long[] validateApplyCustomerContactMobile(Long shopId, Long... customerShopIds) {
    if (ArrayUtil.isEmpty(customerShopIds)) return null;
    UserWriter writer = userDaoManager.getWriter();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Long> customerShopIdList = new ArrayList<Long>(Arrays.asList(customerShopIds));
    Iterator<Long> iterator = customerShopIdList.iterator();
    Long customerShopId;
    ShopDTO shopDTO;
    while (iterator.hasNext()) {
      customerShopId = iterator.next();
      shopDTO = configService.getShopById(customerShopId);
      if (shopDTO == null) {
        iterator.remove();
        continue;
      }
      if (CollectionUtil.isNotEmpty(shopDTO.getContactMobiles())) {
        if (writer.getRelatedCustomerByContactMobiles(new HashSet<String>(shopDTO.getContactMobiles()), shopId) != null) {
          iterator.remove();
        }
      }
    }
    return customerShopIdList.toArray(new Long[customerShopIdList.size()]);
  }

  /**
   * 如果customerRecord 有customerId 并且customer的名字没有变化，不去校验同名客户
   * 批发商版本三个联系人中有一个联系人的用户名+联系人手机号重复 校验就失败
   * 如果批发上三个联系人都没手机号校验 客户名+空手机号 重复就校验失败
   * 汽配版本不存在多联系人，使用的是name+mobile校验
   *
   * @param customerRecordDTO
   * @param result
   */
  @Override
  public void validateAddCustomer(CustomerRecordDTO customerRecordDTO, Result result) throws BcgogoException {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);

    if (customerRecordDTO.getCustomerId() != null) {
      CustomerDTO customerDTO = getUserService().getCustomerDTOByCustomerId(customerRecordDTO.getCustomerId(), customerRecordDTO.getShopId());
      if (customerDTO != null && customerDTO.getName().equals(customerRecordDTO.getName())) {
        return;
      }
    }
    //校验同名客户是否存在，存在的话，再校验手机。
    int customerCount = customerService.countCustomerByName(customerRecordDTO.getName(), customerRecordDTO.getCustomerId(),
        customerRecordDTO.getShopId());
    if (customerCount > 0) {
      if (!ArrayUtils.isEmpty(customerRecordDTO.getContacts())) {
        boolean isHaveMobile = false;
        boolean isHaveSameMobile = false;
        for (ContactDTO contactDTO : customerRecordDTO.getContacts()) {
          if (contactDTO != null && StringUtils.isNotBlank(contactDTO.getMobile())) {
            isHaveMobile = true;
          }
        }
        if (isHaveMobile) {
          for (ContactDTO contactDTO : customerRecordDTO.getContacts()) {
            if (contactDTO != null && StringUtils.isNotBlank(contactDTO.getMobile())) {
              int customerMobileCount = customerService.countCustomerByNameAndMobile(customerRecordDTO.getName(),
                  contactDTO.getMobile(), customerRecordDTO.getCustomerId(), customerRecordDTO.getShopId());
              if (customerMobileCount > 0) {
                isHaveSameMobile = true;
                break;
              }
            }
          }
        } else {
          int customerMobileCount = customerService.countCustomerByNameAndMobile(customerRecordDTO.getName(),
              null, customerRecordDTO.getCustomerId(), customerRecordDTO.getShopId());
          if (customerMobileCount > 0) {
            isHaveSameMobile = true;
          }
        }

        if (isHaveSameMobile) {
          result.setSuccess(false);
          if (result.getMsg() != null) {
            result.setMsg(result.getMsg() + "已存在相同用户名的客户，请加手机予以区分。<br>");
          } else {
            result.setMsg("已存在相同用户名的客户，请加手机予以区分。<br>");
          }
        }
      } else {
        int customerMobileCount = customerService.countCustomerByNameAndMobile(customerRecordDTO.getName(),
          customerRecordDTO.getMobile(), customerRecordDTO.getCustomerId(), customerRecordDTO.getShopId());
        if (customerMobileCount > 0) {
          result.setSuccess(false);
          if (result.getMsg() != null) {
            result.setMsg(result.getMsg() + "已存在相同用户名的客户，请加手机予以区分。<br>");
          } else {
            result.setMsg("已存在相同用户名的客户，请加手机予以区分。<br>");
          }
        }
      }

    }
    //validate obd info
    CarDTO[] carDTOs=customerRecordDTO.getVehicles();
    if(ArrayUtil.isNotEmpty(carDTOs)){
      IObdManagerService obdManagerService=ServiceManager.getService(IObdManagerService.class);
      for(CarDTO carDTO:carDTOs){
        String imei=carDTO.getGsmObdImei();
        String mobile=carDTO.getGsmObdImeiMoblie();
        if(StringUtils.isNotBlank(imei) || StringUtils.isNotBlank(mobile)){
          try{
            ObdSimBindDTO obdSimBindDTO=obdManagerService.getObdSimBindByShopExact(imei, mobile);
            if(obdSimBindDTO!=null&&obdSimBindDTO.getVehicleId()!=null){
              result.LogErrorMsg("您输入的OBD已经安装在车辆"+obdSimBindDTO.getLicenceNo()+"上!");
            }else if(obdSimBindDTO == null){
              result.LogErrorMsg("您输入的OBD不存在，请联系客服，电话：0512-66733331！");
            }
          }catch (Exception e){
            LOG.error(e.getMessage(),e);
            result.LogErrorMsg("您输入的OBD已经安装其他车辆上!");
          }
        }


      }
    }
  }

  @Override
  public int countCustomerByName(String name, Long customerId, Long shopId) {
    if (StringUtils.isEmpty(name) || shopId == null) {
      return 0;
    }
    return userDaoManager.getWriter().countCustomerByName(name, customerId, shopId);
  }

  @Override
  public int countCustomerByNameAndMobile(String name, String mobile, Long customerId, Long shopId) {
    return userDaoManager.getWriter().countCustomerByNameAndMobile(name, mobile, customerId, shopId);
  }


  /**
   * 删除客户的时候，如果对方供应商还是关联关系，更新为收藏
   *
   * @param shopId         做出删除动作自己的shopId
   * @param customerShopId 关联对方的shopId
   * @return 被更新的supplierIds
   * @throws Exception
   */
  @Override
  public List<Long> deleteCustomerUpdateSupplierRelationStatus(Long shopId, Long customerShopId) throws Exception {
    List<Long> updatedSupplierIds = new ArrayList<Long>();
    if (customerShopId == null || shopId == null) {
      return updatedSupplierIds;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Supplier> suppliers = writer.getSupplierBySupplierShopIds(customerShopId, shopId);
      if (CollectionUtil.isNotEmpty(suppliers)) {
        for (Supplier supplier : suppliers) {
          if (RelationTypes.RELATED.equals(supplier.getRelationType())) {
            supplier.setRelationType(RelationTypes.CUSTOMER_COLLECTION);
            writer.update(supplier);
            updatedSupplierIds.add(supplier.getId());
          }
        }
        writer.commit(status);
      }
      return updatedSupplierIds;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Set<Long> getCustomerShopIds(Long shopId) {
    Set<Long> collectionShopIds = new HashSet<Long>();
    if (shopId == null) {
      return collectionShopIds;
    }
    List<Long> shopIdList = userDaoManager.getWriter().getCustomerShopIds(shopId);
    if (CollectionUtils.isNotEmpty(shopIdList)) {
      for (Long collectionShopId : shopIdList) {
        collectionShopIds.add(collectionShopId);
      }
    }
    return collectionShopIds;
  }

  @Override
  public Map<String, List<CustomerDTO>> getCustomerByMobiles(Long shopId, Set<String> mobiles, Set<Long> excludeCustomerIds) {
    Map<String, List<CustomerDTO>> customerDTOMap = new HashMap<String, List<CustomerDTO>>();
    if (shopId == null || CollectionUtils.isEmpty(mobiles)) {
      return customerDTOMap;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Contact> contacts = writer.getContactsByCustomerMobiles(shopId, mobiles, excludeCustomerIds);
    List<Long> allCustomerIds = new ArrayList<Long>();
    Map<String, Set<Long>> mobileCustomerIdsMap = new HashMap<String, Set<Long>>();
    if (CollectionUtils.isNotEmpty(contacts)) {
      for (Contact contact : contacts) {
        if (contact != null && contact.getCustomerId() != null && StringUtils.isNotEmpty(contact.getMobile())) {
          String mobile = contact.getMobile();
          Set<Long> customerIds = mobileCustomerIdsMap.get(mobile);
          if (customerIds == null) {
            customerIds = new HashSet<Long>();
          }
          customerIds.add(contact.getCustomerId());
          mobileCustomerIdsMap.put(mobile, customerIds);
          allCustomerIds.add(contact.getCustomerId());
        }
      }
    }
    List<CustomerDTO> customerDTOs = writer.getCustomerByIds(allCustomerIds);
    Map<Long, CustomerDTO> idCustomerDTOMap = new HashMap<Long, CustomerDTO>();
    if (CollectionUtils.isNotEmpty(customerDTOs)) {
      for (CustomerDTO customerDTO : customerDTOs) {
        idCustomerDTOMap.put(customerDTO.getId(), customerDTO);
      }
    }
    for (String mobile : mobileCustomerIdsMap.keySet()) {
      Set<Long> customerIds = mobileCustomerIdsMap.get(mobile);
      List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
      for (Long customerId : customerIds) {
        customerDTOList.add(idCustomerDTOMap.get(customerId));
      }
      if (CollectionUtils.isNotEmpty(customerDTOList)) {
        customerDTOMap.put(mobile, customerDTOList);
      }
    }
    return customerDTOMap;
  }

  @Override
  public Result validateCustomerMobiles(Long shopId, Long customerId, String... mobiles) {
    if (shopId == null || ArrayUtils.isEmpty(mobiles)) {
      return new Result();
    }
    CustomerDTO customerDTO = getUserService().getCustomerDTOByCustomerId(customerId, shopId);
    Set<String> toNeedCheckMobile = new HashSet<String>();
    if (customerDTO != null && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
      for (String mobile : mobiles) {
        if (StringUtils.isEmpty(mobile)) {
          continue;
        }
        boolean isNewMobile = true;
        for (ContactDTO contactDTO : customerDTO.getContacts()) {
          if (contactDTO != null) {
            if (mobile.equals(contactDTO.getMobile())) {
              isNewMobile = false;
              break;
            }
          }
        }
        if (isNewMobile) {
          toNeedCheckMobile.add(mobile);
        }
      }
    } else {
      for (String mobile : mobiles) {
        if (StringUtils.isEmpty(mobile)) {
          continue;
        }
        toNeedCheckMobile.add(mobile);
      }
    }

    Set<Long> excludeCustomerIds = new HashSet<Long>();
    if (customerDTO != null) {
      excludeCustomerIds.add(customerDTO.getId());
    }
    if (CollectionUtils.isNotEmpty(toNeedCheckMobile)) {
      Map<String, List<CustomerDTO>> customerDTOMaps = getCustomerByMobiles(shopId, toNeedCheckMobile, excludeCustomerIds);
      StringBuffer msg = new StringBuffer();
      for (String mobile : customerDTOMaps.keySet()) {
        List<CustomerDTO> customerDTOs = customerDTOMaps.get(mobile);
        for (CustomerDTO tempCustomerDTO : customerDTOs) {
          msg.append("手机号【").append(mobile).append("】与客户【").append(tempCustomerDTO.getName()).append("】的联系人手机号相同!<br>");
        }
      }
      if (msg.length() > 0) {
        msg.append("请重新输入!");
      }
      if (MapUtils.isNotEmpty(customerDTOMaps)) {
        return new Result(msg.toString(), false, customerDTOMaps);
      }
    }
    return new Result();
  }

  @Override
  public void addAreaInfoToCustomerDTO(CustomerDTO customerDTO) {
    StringBuilder areaInfo = new StringBuilder();
    if (customerDTO.getProvince() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getProvince());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    if (customerDTO.getCity() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getCity());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    if (customerDTO.getRegion() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getRegion());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    customerDTO.setAreaInfo(areaInfo.toString());
  }

  public int countCustomerRecordByKey(Long shopId, String key) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countCustomerRecordByKey(shopId, key);
  }

  public String[] getCustomerOrSupplierId(String[] customerOrSupplierIds) {
    String[] result = null;
    UserWriter writer = userDaoManager.getWriter();
    List list = writer.getCustomerOrSupplierId(customerOrSupplierIds);
    if (CollectionUtils.isNotEmpty(list)) {
      result = new String[list.size() + customerOrSupplierIds.length];
      int i = 0;
      for (Object o : list) {
        result[i++] = o.toString();
      }
      for (Object o : customerOrSupplierIds) {
        result[i++] = o.toString();
      }
    } else {
      result = customerOrSupplierIds;
    }
    return result;
  }

  @Override
  public List<String> getAppUserMobileByContactMobile(Long shopId, String mobile) {
    return userDaoManager.getWriter().getAppUserMobileByContactMobile(shopId, mobile);
  }




}
