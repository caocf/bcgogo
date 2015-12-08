package com.bcgogo.user.model;

import com.bcgogo.api.*;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Sort;
import com.bcgogo.config.model.Area;
import com.bcgogo.enums.*;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.user.*;
import com.bcgogo.etl.ImpactVideoExpDTO;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.txn.dto.ConsumingRecordDTO;
import com.bcgogo.txn.dto.pushMessage.impact.ImpactInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.mileage.MileageInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.sos.SosInfoSearchConditionDTO;
import com.bcgogo.user.ImpactAndVideoDTO;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.*;
import com.bcgogo.user.model.SystemMonitor.ClientUserLoginInfo;
import com.bcgogo.user.model.SystemMonitor.UrlMonitorConfig;
import com.bcgogo.user.model.SystemMonitor.UserClientInfo;
import com.bcgogo.user.model.SystemMonitor.UserLoginLog;
import com.bcgogo.user.model.app.*;
import com.bcgogo.user.model.permission.*;
import com.bcgogo.user.model.task.CusOrSupOrderIndexSchedule;
import com.bcgogo.user.model.task.MergeTask;
import com.bcgogo.user.model.task.RelatedShopUpdateTask;
import com.bcgogo.user.model.userGuide.UserGuideFlow;
import com.bcgogo.user.model.userGuide.UserGuideHistory;
import com.bcgogo.user.model.userGuide.UserGuideStep;
import com.bcgogo.user.model.wx.*;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXAccountType;
import com.bcgogo.wx.WXArticleTemplateDTO;
import com.bcgogo.wx.WXShareDTO;
import com.bcgogo.wx.WXShopAccountSearchCondition;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeSearchCondition;
import com.bcgogo.wx.user.WXUserDTO;
import com.bcgogo.wx.user.WXUserSearchCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/12/11
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserWriter extends GenericWriterDao {

  public UserWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public List<User> getUserByUserInfo(String userInfo) {
    Session session = this.getSession();
    if (userInfo == null || userInfo.equals("")) return null;

    try {
//      Query q;
//      if (userInfo.matches("\\d++")) {
//        if (userInfo.charAt(0) == '1' && userInfo.length() == 11) {
//          q = SQL.getUserByMobile(session, userInfo);
//        } else {
//          q = SQL.getUserByUserNo(session, userInfo);
//        }
//      } else {
//        if (userInfo.indexOf((int) '@') > 0) {
//          q = SQL.getUserByEmail(session, userInfo);
//        } else {
//          q = SQL.getUserByUserName(session, userInfo);
//        }
//      }
      Query q = SQL.getUserByUserNo(session, userInfo);
      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }

  public List<User> getUserByMobile(String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getUserByMobile(session, mobile);

      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }

  public List<User> getUserByFuzzyUserNo(String userNo, int maxResults) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByFuzzyUserNo(session, userNo, maxResults);
      return q.list();
    } finally {
      release(session);
    }
  }

  public UserGroup getUserGroupByUserId(long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupByUserId(session, userId);
      return (UserGroup) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<UserGroupUser> getUserByUserGroupId(long userGroupId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getUserByUserGroupId(session, userGroupId);

      return (List<UserGroupUser>) q.list();
    } finally {
      release(session);
    }
  }

  public long countUserByUserGroupId(Long userGroupId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUserByUserGroupId(session, userGroupId);
      Object o = q.uniqueResult();
      return (Long) o;
    } finally {
      release(session);
    }
  }

  public long countSaleMansByUserGroupId(Long userGroupId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSaleMansByUserGroupId(session, userGroupId);
      Object o = q.uniqueResult();
      return (Long) o;
    } finally {
      release(session);
    }
  }

  public List<UserGroupUser> getUserGroupUser(long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupUser(session, userId);
      return (List<UserGroupUser>) q.list();
    } finally {
      release(session);
    }
  }


  public List<UserGroupRole> getRoleByUserGroupId(long userGroupId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getRoleByUserGroupId(session, userGroupId);

      return (List<UserGroupRole>) q.list();
    } finally {
      release(session);
    }
  }

  public List<User> getShopUser(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getUserByShopId(session, shopId);

      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }

  public List<UserGroup> getShopUserGroup(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopUserGroup(session, shopId);

      return (List<UserGroup>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByLicenceNo(Long shopId, String licenceNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByLicenceNo(session, shopId, licenceNo);
      return (List<Vehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByShopIdOrLicenceNo(Long shopId, String licenceNo, Integer limit) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByShopIdOrLicenceNo(session, shopId, licenceNo, limit);
      return (List<Vehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByLicenceNo(String... licenceNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByLicenceNo(session, licenceNo);
      return (List<Vehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<AppointService> getAppointServiceByCustomerVehicle(Long shopId, Long vehicleId, Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppointServiceByCustomerVehicle(session, shopId, vehicleId, customerId);
      return (List<AppointService>) q.list();
    } finally {
      release(session);
    }
  }

  public List<User> getUserByShopIDAndMobile(Long shopId, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getUserByShopIDAndMobile(session, shopId, mobile);

      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }


  public List<Object[]> getVehiclesByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehiclesByCustomerId(session, shopId, customerId);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public Long getCustomerIdByCustomerMobileAndVehicleNo(Long shopId, String mobile, String vehicleNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerIdByCustomerMobileAndVehicleNo(session, shopId, mobile, vehicleNo);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByName(Long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByName(session, shopId, name);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getAllCustomerByName(long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllCustomerByName(session, shopId, name);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByMatchedName(long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByMatchedName(session, shopId, name);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getRelatedCustomerByMatchedName(Long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRelatedCustomerByMatchedName(session, shopId, name);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getShopCustomerById(long shopId, long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerById(session, shopId, customerId);

      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<String> getCustomersMobilesByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomersMobilesByShopId(session, shopId);

      return (List<String>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomersByShopIdSendInvitationCode(Long shopId, long startId, int pageSize, Long createTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomersByShopIdSendInvitationCode(session, shopId, startId, pageSize, createTime);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSuppliersByShopIdSendInvitationCode(Long shopId, long startId, int pageSize, Long createTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSuppliersByShopIdSendInvitationCode(session, shopId, startId, pageSize, createTime);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }


  public Customer getCustomerByMobile(long shopId, String mobile) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByMobile(session, shopId, mobile);
      List<Customer> customers = (List<Customer>) q.list();
      if (customers.size() > 1) {
        LOG.error("customer mobile [{}] is not unique in shop {}", mobile, shopId);
      }
      return CollectionUtil.getFirst(customers);
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getCustomerByVehicleMobile(long shopId, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerByVehicleMobile(session, shopId, mobile);
      List<Object> customerVehicles = q.list();
      if (customerVehicles.size() > 0) {
        return (List<CustomerVehicle>) q.list();
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }


  public List<Customer> getCompleteCustomerByMobile(long shopId, String mobile) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCompleteCustomerByMobile(session, shopId, mobile);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByNameAndMobile(long shopId, String name, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerByNameAndMobile(session, shopId, name, mobile);

      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }


  /*public Customer getCustomerByNameMobilePhone(Long shopId, String name, String mobile, String phone) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerByNameMobilePhone(session, shopId, name, mobile, phone);
      return (Customer)query.uniqueResult();
    }finally {
      release(session);
    }
  }*/

  public long countShopCustomer(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countShopCustomer(session, shopId);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public int countShopCustomerByKey(long shopId, String key) {
    Session session = getSession();
    try {
      Query q = SQL.countShopCustomerByKey(session, shopId, key);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<Customer> getShopCustomerByKey(long shopId, String key) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopCustomerByKey(session, shopId, key);
      return q.list();
    } finally {
      release(session);
    }

  }

  public long countShopCustomerRecord(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countShopCustomerRecord(session, shopId);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public long countShopArrearsCustomerRecord(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countShopArrearsCustomerRecord(session, shopId);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public int countCustomerServiceJobByShopId(long shopId, long remindTime, List<String> status) {
    Session session = getSession();
    try {
      Query q = SQL.countCustomerServiceJobByShopId(session, shopId, remindTime, status);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public Customer getCustomerInfoByVehicleId(Long shopId, Long vehicleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerInfoByVehicleId(session, shopId, vehicleId);
      Object object = q.uniqueResult();
      if (object != null) {
        Object[] array = (Object[]) object;
        return (Customer) array[0];
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<Pair<Customer, CustomerVehicle>> getCustomersByVehicleIds(Long shopId, Set<Long> vehicleIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomersByVehicleIds(session, shopId, vehicleIds);
      List<Object[]> list = q.list();
      List<Pair<Customer, CustomerVehicle>> pairs = new ArrayList<Pair<Customer, CustomerVehicle>>();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] objects : list) {
          if (!ArrayUtils.isEmpty(objects)) {
            Customer customer = (Customer) objects[0];
            CustomerVehicle customerVehicle = (CustomerVehicle) objects[1];
            pairs.add(new Pair<Customer, CustomerVehicle>(customer, customerVehicle));
          }
        }
      }
      return pairs;
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getCustomerVehicleByVehicleId(Long vehicleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerVehicleByVehicleId(session, vehicleId);
      return (List<CustomerVehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByVehicleNo(String vehicleNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByVehicleNo(session, vehicleNo);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getCustomerByVehicleIds(Long... vehicleIds) {
    List<CustomerVehicle> customerVehicles = new ArrayList<CustomerVehicle>();
    if (ArrayUtils.isEmpty(vehicleIds)) {
      return customerVehicles;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByVehicleIds(session, vehicleIds);
      return (List<CustomerVehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getCustomerVehicleDTOByVehicleIdAndCustomerId(Long vehicleId, Long customerId) {
    if (vehicleId == null || customerId == null) {
      return new ArrayList<CustomerVehicle>();
    }
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerVehicleDTOByVehicleIdAndCustomerId(session, vehicleId, customerId);

      return (List<CustomerVehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getCustomerVehicle(Long... vehicleIds) {
    if (ArrayUtil.isEmpty(vehicleIds)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerVehicle(session, vehicleIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<CustomerVehicle> getVehicleByCustomerId(long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByCustomerId(session, customerId);
      return (List<CustomerVehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleDTO> getOwnedVehiclesByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleByCustomerId(session, customerId);
      List<CustomerVehicle> customerVehicles = (List<CustomerVehicle>) query.list();
      if (CollectionUtils.isEmpty(customerVehicles)) {
        return null;
      }
      List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
      Vehicle vehicle = null;
      for (CustomerVehicle customerVehicle : customerVehicles) {
        if (customerVehicle == null) {
          continue;
        }
        if (customerVehicle.getVehicleId() != null) {
          vehicle = getVehicleById(shopId, customerVehicle.getVehicleId());
        }
        if (vehicle != null)
          vehicleDTOs.add(vehicle.toDTO());
      }
      return vehicleDTOs;
    } finally {
      release(session);
    }
  }


  public List<Supplier> getSupplierByName(long shopId, String name) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierByName(session, shopId, name);

      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierDTOByIds(Long shopId, Long... supplierIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierDTOByIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierByMatchedName(long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierByMatchedName(session, shopId, name);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierById(long shopId, long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierById(session, shopId, supplierId);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  //zhanghcuanlong
  public List<Supplier> getSupplierByNameAndShopId(long shopId, String name) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierByNameAndShopId(session, shopId, name);

      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSpecialSupplierByName(long shopId, String name) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSpecialSupplierByName(session, shopId, name);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierByMobile(long shopId, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierByMobile(session, shopId, mobile);

      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSuppliersByKey(long shopId, String key, int pageNo, int pageSize) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSuppliersByKey(session, shopId, key, pageNo, pageSize);

      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getShopSupplier(long shopId, int pageNo, int pageSize) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopSupplier(session, shopId, pageNo, pageSize);

      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getShopCustomerRecord(long shopId, int pageNo, int pageSize) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerRecord(session, shopId, pageNo, pageSize);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getShopArrearsCustomerRecord(long shopId, int pageNo, int pageSize) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopArrearsCustomerRecord(session, shopId, pageNo, pageSize);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public int countShopArrearsCustomerRecord1(long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopArrearsCustomerRecord1(session, shopId);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public double getShopTotalArrears(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopTotalArrears(session, shopId);
      Double sum = (Double) q.uniqueResult();
      if (sum == null) {
        return 0.0;
      }
      return sum.doubleValue();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getCustomerRecordByName(String name) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerRecordByName(session, name);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getCustomerRecordByCustomerId(long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerRecordByCustomerId(session, customerId);
      // TODO zhuj 这里要查找 contactList??
      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomers(CustomerDTO customerIndex) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomers(session, customerIndex);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getCustomerRecord(CustomerRecordDTO customerRecordDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerRecord(session, customerRecordDTO);
      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public CustomerRecord getCustomerRecordByCustId(long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerRecordByCustomerId(session, customerId);
      List<CustomerRecord> customerRecordList = q.list();
      if (customerRecordList != null && !customerRecordList.isEmpty()) {
        return customerRecordList.get(0);
      }
    } finally {
      release(session);
    }
    return null;
  }

  public List<CustomerRecord> getShopCustomerRecordByName(long shopId, String name) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerRecordByName(session, shopId, name);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getShopCustomerRecordByMobile(long shopId, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerRecordByMobile(session, shopId, mobile);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getShopCustomerRecordByMobile(long shopId, String name, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerRecordByMobile(session, shopId, name, mobile);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getShopCustomerRecordByLicenceNo(long shopId, String licenceNo) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerRecordByLicenceNo(session, shopId, licenceNo);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getShopCustomerRecord(long shopId, String name, String licenceNo) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerRecord(session, shopId, name, licenceNo);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getShopCustomerRecordByCustomerId(Long customerId) {
    if (customerId == null) {
      return new ArrayList<CustomerRecord>();
    }
    Session session = this.getSession();

    try {
      Query q = SQL.getShopCustomerRecordByCustomerId(session, customerId);

      return (List<CustomerRecord>) q.list();
    } finally {
      release(session);
    }
  }


  public CustomerRecord getShopCustomerRecordByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopCustomerRecordByCustomerId(session, shopId, customerId);
      List<CustomerRecord> customerRecords = (List<CustomerRecord>) q.list();
      if (CollectionUtils.isNotEmpty(customerRecords)) {
        return customerRecords.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<CustomerServiceJob> getCustomerServiceJobByCustomerIdAndVehicleId(Long shopId, Long customerId, Long vehicleId) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:UserWriter:getCustomerServiceJobByCustomerIdAndVehicleId");
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceJobByCustomerIdAndVehicleId(session, shopId, customerId, vehicleId);
      return q.list();
    } catch (Exception e) {
      LOG.info(e.getMessage());
      return new ArrayList<CustomerServiceJob>();
    } finally {
      LOG.debug("AOP_SQL end:UserWriter:getCustomerServiceJobByCustomerIdAndVehicleId 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public List<CustomerServiceJob> getCustomerServiceJobByCustomerVehicleRemindTypes(Long shopId, Long customerId,
                                                                                    Long vehicleId, Set<Long> remindTypes) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:UserWriter:getCustomerServiceJobByCustomerVehicleRemindTypes");
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceJobByCustomerVehicleRemindTypes(session, shopId, customerId, vehicleId, remindTypes);
      return q.list();
    } finally {
      LOG.debug("AOP_SQL end:UserWriter:getCustomerServiceJobByCustomerVehicleRemindTypes 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public List<CustomerServiceJob> getCustomerServiceJobByAppointServiceId(Long shopId, Long appointServiceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceJobByAppointServiceId(session, shopId, appointServiceId);
      return q.list();
    } finally {
      release(session);
    }
  }

  //key 是预约服务的appointServiceId
  public Map<Long, CustomerServiceJob> getCustomerServiceJobByAppointServiceIds(Long shopId, Set<Long> appointServiceId) {
    Map<Long, CustomerServiceJob> customerServiceJobMap = new HashMap<Long, CustomerServiceJob>();
    if (shopId == null || CollectionUtil.isEmpty(appointServiceId)) {
      return customerServiceJobMap;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceJobByAppointServiceIds(session, shopId, appointServiceId);
      List<CustomerServiceJob> customerServiceJobs = q.list();
      if (CollectionUtils.isNotEmpty(customerServiceJobs)) {
        for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
          if (customerServiceJob.getAppointServiceId() != null) {
            customerServiceJobMap.put(customerServiceJob.getAppointServiceId(), customerServiceJob);
          }
        }
      }
      return customerServiceJobMap;
    } finally {
      release(session);
    }
  }

  //key 是预约服务的vehicleId
  public Map<Long, List<CustomerServiceJob>> getCustomerServiceJobByVehicleIds(Set<Long> vehicleIds, Set<Long> remindTypes) {
    Map<Long, List<CustomerServiceJob>> customerServiceJobMap = new HashMap<Long, List<CustomerServiceJob>>();
    if (CollectionUtil.isEmpty(vehicleIds)) {
      return customerServiceJobMap;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceJobByVehicleIds(session, vehicleIds, remindTypes);
      List<CustomerServiceJob> customerServiceJobs = q.list();
      if (CollectionUtils.isNotEmpty(customerServiceJobs)) {
        for (CustomerServiceJob customerServiceJob : customerServiceJobs) {
          List<CustomerServiceJob> customerServiceJobList = null;
          if (customerServiceJob.getVehicleId() != null) {
            customerServiceJobList = customerServiceJobMap.get(customerServiceJob.getVehicleId());
          }
          if (customerServiceJobList == null) {
            customerServiceJobList = new ArrayList<CustomerServiceJob>();
          }
          customerServiceJobList.add(customerServiceJob);
          customerServiceJobMap.put(customerServiceJob.getVehicleId(), customerServiceJobList);
        }
      }
      return customerServiceJobMap;
    } finally {
      release(session);
    }
  }


  public List<CustomerServiceJob> getCustomerServiceJobByStateAndPageNoAndPageSize(long shopId, List<String> status, Long remindTime, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceJobByStateAndPageNoAndPageSize(session, shopId, status, remindTime, pageNo, pageSize);
      if (q == null) {
        return null;
      }
      List result = q.list();
      if (CollectionUtils.isNotEmpty(result)) {
        return (List<CustomerServiceJob>) result;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public int countCustomerServiceRemindByCondition(Long shopId, Boolean isOverdue, Boolean hasRemind, Long remindTime) {
    Session session = getSession();
    try {
      Query q = SQL.countCustomerServiceRemindByCondition(session, shopId, isOverdue, hasRemind, remindTime);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((BigInteger) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getCustomerServiceRemindByCondition(long shopId, Boolean isOverdue, Boolean hasRemind, Long currentTime, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceRemindByCondition(session, shopId, isOverdue, hasRemind, currentTime, pageNo, pageSize);
      if (q == null) {
        return null;
      }
      List result = q.list();
      if (CollectionUtils.isNotEmpty(result)) {
        return (List<Object[]>) result;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<CustomerServiceJob> getCustomerServiceJobByCustomerIdAndRemindType(long customerId, long remindType) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerServiceJobByCustomerIdAndRemindType(session, customerId, remindType);

      return (List<CustomerServiceJob>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 包括被逻辑删除的也查询出来
   *
   * @param shopId
   * @param customerId
   * @return
   */
  public List<CustomerServiceJob> getAllCustomerServiceJobByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getAllCustomerServiceJobByCustomerId(session, shopId, customerId);
      return (List<CustomerServiceJob>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerCard> getCustomerCardByCustomerIdAndCardType(long shopId, long customerId, long cardType) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerCardByCustomerIdAndCardType(session, shopId, customerId, cardType);

      return (List<CustomerCard>) q.list();
    } finally {
      release(session);
    }
  }

  public int countShopSupplier(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopSupplier(session, shopId);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countShopSupplierByKey(long shopId, String key) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopSupplierByKey(session, shopId, key);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<Supplier> getShopSupplierByKey(long shopId, String key) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopSupplierByKey(session, shopId, key);
      return (List<Supplier>) q.list();
    } finally {
        release(session);
    }
  }

  public Customer getCustomerByCustomerId(Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByCustomerId(session, customerId);
      return ((Customer) q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public Customer getCustomerByCustomerIdAndShopId(Long customerId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByCustomerIdAndShopId(session, customerId, shopId);
      return ((Customer) q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public UserVercode getUserVercodeByUserNo(String userNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserVercodeByUserno(session, userNo);
      return ((UserVercode) q.uniqueResult());
    } finally {
      release(session);
    }

  }

  //客户智能搜索匹配   (汉字)         zhanghcuanlong
  public List getCustomer(String keyword, Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getCustomer(session, keyword, shopId);
//      query.addScalar("id", Hibernate.LONG);
//      query.addScalar("name", Hibernate.STRING);
//      query.addScalar("contact", Hibernate.STRING);
//      query.addScalar("mobile", Hibernate.STRING);
//      List customers = query.list();
      return query.list();

    } finally {
      release(session);
    }
  }

  // 供应商智能匹配       （汉字）         zhangchuanlong
  public List getSupplier(String keyword, Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplier(session, keyword, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  //客户智能搜索匹配   (字母)         zhanghcuanlong
  public List getCustomerByZiMu(String keyword, Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerByZiMu(session, keyword, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  // 供应商智能匹配  （字母）              zhangchuanlong
  public List getSupplierByZiMu(String keyword, Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplierByZiMu(session, keyword, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerRecordDTO> getCustomerRecordByKey(Long shopId, String key, int rowStart, int pageSize) {
    List<CustomerRecordDTO> customerRecordDTOList = new ArrayList<CustomerRecordDTO>();
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerRecordByKey(session, shopId, key, rowStart, pageSize);
      List<CustomerRecord> customerRecordList = query.list();
      if (customerRecordList == null || customerRecordList.isEmpty()) {
        return customerRecordDTOList;
      }
      for (CustomerRecord customerRecord : customerRecordList) {
        if (customerRecord == null) {
          continue;
        }
        customerRecordDTOList.add(customerRecord.toDTO());
      }
    } finally {
      release(session);
    }
    return customerRecordDTOList;
  }


  public int countCustomerRecordByKey(Long shopId, String key) {
    List<CustomerRecordDTO> customerRecordDTOList = new ArrayList<CustomerRecordDTO>();
    Session session = this.getSession();
    try {
      Query query = SQL.countCustomerRecordByKey(session, shopId, key);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public List<CustomerVehicleNumberDTO> getCustomerVehicleCount(List<Long> customerIds) {
    List<CustomerVehicleNumberDTO> customerVehicleNumberDTOList = new ArrayList<CustomerVehicleNumberDTO>();
    Session session = this.getSession();
    try {
      SQLQuery query = SQL.getCustomerVehicleCount(session, customerIds);
      query.addScalar("customerId", StandardBasicTypes.LONG);
      query.addScalar("count", StandardBasicTypes.INTEGER);
      List<CustomerVehicleNumber> customerVehicleNumberList = query.list();
      if (customerVehicleNumberList == null || customerVehicleNumberList.isEmpty()) {
        return customerVehicleNumberDTOList;
      }
      for (CustomerVehicleNumber customerVehicleNumber : customerVehicleNumberList) {
        if (customerVehicleNumber == null) {
          continue;
        }
        customerVehicleNumberDTOList.add(customerVehicleNumber.toDTO());
      }
    } finally {
      release(session);
    }
    return customerVehicleNumberDTOList;
  }

  /**
   * 根据客户ID列表获取客户列表
   *
   * @param customerIds
   * @return
   */
  public List<CustomerDTO> getCustomerByIds(List<Long> customerIds) {
    List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
    if (CollectionUtils.isEmpty(customerIds)) {
      return customerDTOList;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerByIds(session, customerIds);
      List<Customer> customerList = query.list();
      if (customerList == null || customerList.isEmpty()) {
        return customerDTOList;
      }
      for (Customer customer : customerList) {
        if (customer == null) {
          continue;
        }
        customerDTOList.add(customer.toDTO());
      }
    } finally {
      release(session);
    }
    return customerDTOList;
  }

  public List<Customer> getCustomerByIds(Long... customerIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerByIds(session, customerIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, CustomerDTO> getCustomerByIdSet(Set<Long> customerIds) {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(customerIds)) return new HashMap<Long, CustomerDTO>();
      Query query = SQL.getCustomerByIds(session, customerIds.toArray(new Long[customerIds.size()]));
      List<Customer> customerList = query.list();
      if (CollectionUtils.isEmpty(customerList)) return new HashMap<Long, CustomerDTO>();
      Map<Long, CustomerDTO> map = new HashMap<Long, CustomerDTO>();
      for (Customer customer : customerList) {
        map.put(customer.getId(), customer.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  /**
   * 根据客户ID列表获取客户列表
   *
   * @param customerIds
   * @return
   */
  public List<Customer> getCustomerEntityByIds(List<Long> customerIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerByIds(session, customerIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Customer getCustomerById(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerById(session, shopId, customerId);
      return (Customer) query.uniqueResult();
    } finally {
      release(session);
    }
  }

//  public Customer getCustomerByName(Long shopId,Long customerName) {
//    List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
//    Session session = this.getSession();
//    try {
//      Query query = SQL.getCustomerById(session,shopId,customerId);
//      return (Customer)query.uniqueResult();
//    } finally {
//      release(session);
//    }
//  }


  public List<CustomerCardDTO> getCustomerCardByCustomerIdsAndCardType(Long shopId, List<Long> customerIds, Long cardType) {
    List<CustomerCardDTO> customerCardDTOList = new ArrayList<CustomerCardDTO>();
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerCardByCustomerIdsAndCardType(session, shopId, customerIds, cardType);
      List<CustomerCard> customerCardList = query.list();
      if (customerCardList == null || customerCardList.isEmpty()) {
        return customerCardDTOList;
      }
      for (CustomerCard customerCard : customerCardList) {
        if (customerCard == null) {
          continue;
        }
        customerCardDTOList.add(customerCard.toDTO());
      }
    } finally {
      release(session);
    }
    return customerCardDTOList;
  }

  public List<Agents> getAllAgentsByNameORByAgentCode(String name, String agentCode, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getAllAgentsByNameORByAgentCode(session, name, agentCode, pageNo, pageSize);
      return hql.list();
    } finally {
      release(session);
    }
  }

  public List<Agents> getAgentByAgentCode(String agentCode) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getAgentByAgentCode(session, agentCode);
      return hql.list();
    } finally {
      release(session);
    }
  }

  public List<SalesMan> getAllSalesManByAgentIdSalesManNameSalesManCode(Long agentId, String salesManCode, String salesManName, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getAllSalesManByAgentIdSalesManNameSalesManCode(session, agentId, salesManCode, salesManName, pageNo, pageSize);
      return hql.list();
    } finally {
      release(session);
    }
  }

  public int countAllAgentsByNameORByAgentCode(String name, String agentCode) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countAllAgentsByNameORByAgentCode(session, name, agentCode);
      return Integer.parseInt(hql.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countSalesManByAgentIdSalesManNameSalesManCode(Long agentId, String salesManCode, String salesManName) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countSalesManByAgentIdSalesManNameSalesManCode(session, agentId, salesManCode, salesManName);
      return Integer.parseInt(hql.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public AgentTargt getAgentTargetByMonthAndYearAndAgentId(int month, String year, Long agentId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getAgentTargetByMonthAndYearAndAgentId(session, month, year, agentId);
      return (AgentTargt) hql.uniqueResult();
    } finally {
      release(session);
    }
  }

  public SalesManTarget getSalesManTargetByMonthYearSalesManId(int month, String year, Long salesManId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getSalesManTargetByMonthYearSalesManId(session, month, year, salesManId);
      return (SalesManTarget) hql.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AgentTargt> getMonthTargetByAgentIdAndYear(String year, Long agentId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getMonthTargetByAgentIdAndYear(session, year, agentId);
      return (List<AgentTargt>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<totalMonthTarget> monthTarget(String year) {
    Session session = this.getSession();
    try {
      SQLQuery sql = SQL.monthTarget(session, year);
      return (List<totalMonthTarget>) sql.list();
    } finally {
         release(session);
    }
  }

  public List<Supplier> getSupplierByTelephone(Long shopId, String telephone) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierByTelephone(session, shopId, telephone);

      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByTelephone(Long shopId, String telephone) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerByTelephone(session, shopId, telephone);

      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 获取指定店铺最近交易过的供应商信息列表,按最后交易时间倒序排序
   *
   * @param shopId
   * @param searchKey
   * @param currentPage
   * @param pageSize
   * @return
   */
  public List<SupplierDTO> getRecentlyTradeSupppliers(Long shopId, String searchKey, int currentPage, int pageSize) {
    List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
    Session session = this.getSession();
    try {
      Query q = SQL.getRecentlyTradeSupppliers(session, shopId, searchKey, currentPage, pageSize);
      List<Supplier> supplierList = q.list();
      if (supplierList != null && !supplierList.isEmpty()) {
        for (Supplier supplier : supplierList) {
          if (supplier == null) {
            continue;
          }
          supplierDTOList.add(supplier.toDTO());
        }
      }
    } finally {
      release(session);
    }
    return supplierDTOList;
  }

  public CustomerVehicle getVehicleAppointment(Long customerId, Long vehicleId) {
    CustomerVehicle customerVehicle = null;
    Session session = getSession();

    try {
      Query q = SQL.getVehicleAppointment(session, customerId, vehicleId);
      customerVehicle = (CustomerVehicle) q.uniqueResult();
    } finally {
      release(session);
    }
    return customerVehicle;
  }

  public List<Customer> getCustomerByBirth(Long birth) {
    Session session = this.getSession();
    List<Customer> customerList = new ArrayList<Customer>();
    try {
      Query q = SQL.getCustomerByBirth(session, birth);
      customerList = q.list();
    } finally {
      release(session);
    }
    return customerList;
  }

  public List<CustomerServiceJob> getCustomerServiceJobByRemindType(Long remindType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerServiceJobByRemindType(session, remindType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopPlan> getPlans(Long shopId, int pageNo, int pageSize, Long now, String type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPlans(session, shopId, pageNo, pageSize, now, type);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countPlans(Long shopId, List<PlansRemindStatus> status, Long remindTime, String type) {
    Session session = this.getSession();
    try {
      Query q = SQL.countPlans(session, shopId, status, remindTime, type);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<String> getCustomersMobilesByCustomerIds(Long shopId, List<Long> customerIds) {
    List<String> customers = null;
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomersMobilesByCustomerIds(session, shopId, customerIds);
      customers = q.list();
    } finally {
      release(session);
    }
    customers = CollectionUtil.filterBlankElements(customers);
    return customers;
  }

  /**
   * 查询当天，昨天，当月新增customer数量
   *
   * @param shopId
   * @return
   * @author zhangchuanlong
   */
  public Long countShopCustomerByTime(Long shopId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.countShopCustomerByTime(session, shopId, startTime, endTime);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }


  /**
   * 查询当天，昨天，当月客户记录 按照指定属性排序
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param pager
   * @param sort
   * @return
   * @author zhangchuanlong
   */
  public List<CustomerRecord> getCustomerRecordByTime(Long shopId, Long startTime, Long endTime, Pager pager, Sort sort) {
    Session session = getSession();
    try {
      Query q = SQL.getCustomerRecordByTime(session, shopId, startTime, endTime, pager, sort);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 查询所有客户记录按照指定属性排序
   *
   * @param shopId
   * @param pager
   * @param sort
   * @return
   * @author zhangchuanlong
   */
  public List<CustomerRecord> getShopCustomerRecord(Long shopId, Pager pager, Sort sort) {
    Session session = getSession();
    try {
      Query q = SQL.getShopCustomerRecord(session, shopId, pager, sort);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 获取一段时间区间内车辆车牌号
   *
   * @param fromTime
   * @param endTime
   * @return
   */
  public List<Vehicle> getVehicleLicenceNos(Long shopId, Long fromTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getVehicleLicenceNos(session, shopId, fromTime, endTime);
      return (List<Vehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public Role getRoleByName(String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRoleByName(session, name);
      List<Role> roleList = (List<Role>) q.list();
      if (CollectionUtils.isNotEmpty(roleList)) {
        return roleList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public Resource getResource(String name, String type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getResource(session, name, type);
      List<Resource> resourceList = (List<Resource>) q.list();
      if (CollectionUtils.isNotEmpty(resourceList)) {
        return resourceList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public Resource getResourceByValue(String value) {
    Session session = this.getSession();
    try {
      Query q = SQL.getResourceByValue(session, value);
      return (Resource) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Module getModuleByName(String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getModuleByName(session, name);
      List<Module> moduleList = (List<Module>) q.list();
      if (CollectionUtils.isNotEmpty(moduleList)) {
        return moduleList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<UserGroup> getUniqueUserGroupByName(String name, Long shopId, Long shopVersionId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUniqueUserGroupByName(session, name, shopId, shopVersionId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserGroup> getUserGroupByName(String name, boolean isFuzzyMatching, Long shopId, Long shopVersionId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupByName(session, name, isFuzzyMatching, shopId, shopVersionId);
      return q.list();
    } finally {
      release(session);
    }
  }

  //返回的userdto 带有usergroupId
  public UserDTO getUserDTO(Long shopId, Long userId) {
    UserDTO userDTO = null;
    Session session = this.getSession();
    try {
      Query q = SQL.getUser(session, shopId, userId);
      List<User> userList = (List<User>) q.list();
      if (CollectionUtils.isNotEmpty(userList)) {
        userDTO = userList.get(0).toDTO();
      }
      return userDTO;
    } finally {
      release(session);
    }
  }


  public User getUserById(Long shopId, Long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUser(session, shopId, userId);
      List<User> userList = (List<User>) q.list();
      return CollectionUtil.getFirst(userList);

    } finally {
      release(session);
    }
  }

  public List<User> getUsersByIds(Set<Long> ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUsersByIds(session, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getUserIdsByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      return SQL.getUserIdsByShopId(session, shopId).list();
    } finally {
      release(session);
    }
  }


  public UserDTO getUser(Long shopId, String name) {
    UserDTO userDTO = null;
    Session session = this.getSession();
    try {
      Query q = SQL.getUser(session, shopId, name);
      List<User> userList = (List<User>) q.list();
      if (CollectionUtils.isNotEmpty(userList)) {
        userDTO = userList.get(0).toDTO();
      }
      return userDTO;
    } finally {
      release(session);
    }
  }

  public List<User> getUsersByDepartmentIds(Set<Long> departmentIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUsersByDepartmentIds(session, departmentIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserGroupRoleDTO> getUserGroupRole(Long userGroupId) {
    Session session = this.getSession();
    List<UserGroupRoleDTO> userGroupRoleDTOList = new ArrayList<UserGroupRoleDTO>();
    try {
      Query q = SQL.getUserGroupRole(session, userGroupId);
      List<UserGroupRole> userGroupRoleList = (List<UserGroupRole>) q.list();
      if (CollectionUtils.isEmpty(userGroupRoleList)) return userGroupRoleDTOList;
      for (UserGroupRole userGroupRole : userGroupRoleList) {
        userGroupRoleDTOList.add(userGroupRole.toDTO());
      }
      return userGroupRoleDTOList;
    } finally {
      release(session);
    }
  }

  public List<RoleResource> getRoleResourceByRoleId(Long roleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRoleResourceByRoleId(session, roleId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RoleResource> getRolesByResourceId(Long resourceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRolesByResourceId(session, resourceId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Resource> getResourceByRoleId(Long roleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getResourceByRoleId(session, roleId);
      return (List<Resource>) q.list();
    } finally {
      release(session);
    }
  }

  public RoleResourceDTO getRoleResourceByRoleIdAndResourceId(Long roleId, Long resourceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRoleResourceByRoleIdAndResourceId(session, roleId, resourceId);
      RoleResource roleResource = (RoleResource) q.uniqueResult();
      if (roleResource != null) {
        return roleResource.toDTO();
      }
      LOG.info("getRoleResourceByRoleIdAndResourceId from db [" + roleResource + "]");
      return null;
    } finally {
      release(session);
    }
  }

  public void deleteUserGroupUser(Long userId, Long userGroupId) throws BcgogoException {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteUserGroupUser(session, userId, userGroupId);
      int i = q.executeUpdate();
      if (i <= 0) {
        throw new BcgogoException("delete UserGroupUser[userId:" + userId + "userGroupId:" + userGroupId + " ] fail.");
      }
    } finally {
      release(session);
    }
  }

  public int countUserByUserNo(String userNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUserByUserNo(session, userNo);
      return Integer.parseInt(String.valueOf(q.uniqueResult()));
    } finally {
      release(session);
    }
  }

  public User getUserByUserNo(String userNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByUserNo(session, userNo);
      return (User) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<User> getAllStateUserByUserNo(String userNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllStateUserByUserNo(session, userNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Resource> getResourceByUserGroupId(Long userGroupId, String type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getResourceByUserGroupId(session, userGroupId, type);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Resource> getResourceByUserGroupId(Long userGroupId) {
    Session session = this.getSession();
    try {
      List<Resource> resourceList = new ArrayList<Resource>();
      Query q = SQL.getResourceByUserGroupId(session, userGroupId);
      List<Object[]> objects = q.list();
      for (Object[] oArray : objects) {
        resourceList.add((Resource) oArray[1]);
      }
      return resourceList;
    } finally {
      release(session);
    }
  }

  public List<User> getUserByUserGroupName(String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByUserGroupName(session, name);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserGroupRole> getUserGroupRoleByUserGroupId(Long userGroupId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupRoleByUserGroupId(session, userGroupId);
      return q.list();
    } finally {
      release(session);
    }
  }


  //add by weilf 2012-08-16
  public void deleteCustomerLicenceNo(Long vehicleId) throws BcgogoException {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteVehicleCustomer(session, vehicleId);
      int i = q.executeUpdate();
      if (i <= 0) {
        throw new BcgogoException("delete VehicleCustomer[vehicleId:" + vehicleId + " ] fail.");
      }
    } finally {
      release(session);
    }
  }

  public MemberCard getCustomerCardByCardName(Long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerCardByCardName(session, shopId, name);

      List<MemberCard> memberCardList = q.list();

      if (null != memberCardList && memberCardList.size() > 0) {
        return memberCardList.get(0);
      }

      return null;

    } finally {
      release(session);
    }
  }

  public List<Member> getMemberDTOByMemberNo(Long shopId, String memberNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberDTOByMemberNo(session, shopId, memberNo);

      List<Member> members = (List<Member>) q.list();
      if (CollectionUtils.isNotEmpty(members)) {
        return members;
      }
      return null;

    } finally {
      release(session);
    }
  }

  public void deleteMemberCardServiceByMemberCardId(Long memberCardId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteMemberCardServiceByMemberCardId(session, memberCardId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<MemberCard> getMemberCardByShopIdAndNames(Long shopId, List<String> names) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardByShopIdAndNames(session, shopId, names);

      return (List<MemberCard>) q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberCard> getMemberCardByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardByShopId(session, shopId);

      return (List<MemberCard>) q.list();
    } finally {
      release(session);
    }
  }

  public MemberCard getMemberCardByShopIdAndName(Long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardByShopIdAndName(session, shopId, name);

      List<MemberCard> memberCards = q.list();

      if (null != memberCards && memberCards.size() > 0) {
        return memberCards.get(0);
      }

      return null;

    } finally {
      release(session);
    }
  }


  public List<MemberCardService> getMemberCardServiceByMemberCardId(Long memberCardId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardServiceByMemberCardId(session, memberCardId);

      return (List<MemberCardService>) q.list();
    } finally {
      release(session);
    }
  }

  public Member getMemberByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberByCustomerId(session, shopId, customerId);
      List<Member> members = (List<Member>) q.list();
      if (CollectionUtils.isNotEmpty(members)) {
        return members.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<MemberCardService> getMemberCardServiceDTOByMemberCardIdAndStatus(Long memberCardId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardServiceDTOByMemberCardIdAndStatus(session, memberCardId);

      return (List<MemberCardService>) q.list();
    } finally {
      release(session);
    }
  }

  public MemberCardService getMemberCardServiceByMemberCardIdAndServiceIdAndStatus(Long memberCardId, Long serviceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardServiceByMemberCardIdAndServiceIdAndStatus(session, memberCardId, serviceId);

      List<MemberCardService> memberCardServices = q.list();

      if (CollectionUtils.isNotEmpty(memberCardServices)) {
        return memberCardServices.get(0);
      }
      return null;

    } finally {
      release(session);
    }
  }

  public List<MemberService> getMemberServicesByMemberId(Long memberId) {
    if (memberId == null) {
      return new ArrayList<MemberService>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberServicesByMemberId(session, memberId);
      return (List<MemberService>) q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberService> getMemberServicesByMemberIdAndStatusAndDeadline(Long memberId) throws Exception {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberServicesByMemberIdAndStatusAndDeadline(session, memberId);
      return (List<MemberService>) q.list();
    } finally {
      release(session);
    }
  }

  public MemberService getMemberServiceByMemberIdAndServiceIdAndStatus(Long memberId, Long serviceId) throws Exception {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberServiceByMemberIdAndServiceIdAndStatus(session, memberId, serviceId);
      List<MemberService> memberServices = q.list();
      if (null != memberServices && memberServices.size() > 0) {
        return memberServices.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<CustomerCard> getAllWashCard() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllWashCard(session);
      return (List<CustomerCard>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesMan> getSalesManDTOListByShopId(long shopId, SalesManStatus salesManStatus, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesManDTOListByShopId(session, shopId, salesManStatus, pager);
      List<SalesMan> salesMans = (List<SalesMan>) q.list();
      if (CollectionUtils.isNotEmpty(salesMans)) {
        return salesMans;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  @Deprecated
  public List<SalesMan> getAllSalesManForInitPermission() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllSalesManForInitPermission(session);
      List<SalesMan> salesMans = (List<SalesMan>) q.list();
      return salesMans;
    } finally {
      release(session);
    }
  }

  @Deprecated
  public List<SalesMan> getAllSalesMan() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllSalesMan(session);
      List<SalesMan> salesMans = (List<SalesMan>) q.list();
      return salesMans;
    } finally {
      release(session);
    }
  }

  public int countSalesManByShopIdAndStatus(long shopId, SalesManStatus salesManStatus) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSalesManByShopIdAndStatus(session, shopId, salesManStatus);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public List<SalesMan> getSalesManDTOByCodeOrName(String salesManCode, String name, long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesManDTOByCodeOrName(session, salesManCode, name, shopId);
      List<SalesMan> salesMans = (List<SalesMan>) q.list();
      if (CollectionUtils.isNotEmpty(salesMans)) {
        return salesMans;
      }
      return null;
    } finally {
      release(session);
    }
  }


  public MemberCard getMemberCardById(Long shopId, Long id) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardById(session, shopId, id);
      List<MemberCard> memberCards = q.list();
      if (null != memberCards && memberCards.size() != 0) {
        return memberCards.get(0);
      }

      return null;
    } finally {
      release(session);
    }
  }


  public MemberService getMemberService(Long memberCardId, Long serviceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberService(session, memberCardId, serviceId);
      List<MemberService> memberServices = q.list();
      if (null != memberServices && memberServices.size() != 0) {
        return memberServices.get(0);
      }

      return null;
    } finally {
      release(session);
    }
  }

  public Member getMemberByShopIdAndMemberNo(Long shopId, String memberNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberByShopIdAndMemberNo(session, shopId, memberNo);
      List<Member> members = q.list();
      if (CollectionUtil.isEmpty(members)) {
        return null;
      }
      Member member = null;
      for (Member memberIndex : members) {
        if (memberIndex == null) {
          continue;
        }
        if (!MemberStatus.DISABLED.equals(memberIndex.getStatus())) {
          member = memberIndex;
          break;
        }
      }
      return member;
    } finally {
      release(session);
    }
  }

  //包括退卡后和相同卡号两种情况
  public Member RFGetMemberByShopIdAndMemberNo(Long shopId, String memberNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberByShopIdAndMemberNo(session, shopId, memberNo);
      List<Member> members = q.list();
      if (CollectionUtil.isEmpty(members)) {
        return null;
      }
      Member member = null;
      for (Member memberIndex : members) {
        if (memberIndex == null) {
          continue;
        }
        if (memberIndex.getStatus() != null) {
          member = memberIndex;
          break;
        }
      }
      return member;
    } finally {
      release(session);
    }
  }

  public Member getEnabledMemberByShopIdAndMemberNo(Long shopId, String memberNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getEnabledMemberByShopIdAndMemberNo(session, shopId, memberNo);
      return CollectionUtil.getFirst((List<Member>) q.list());
    } finally {
      release(session);
    }
  }

  public List<SalesMan> getSaleManByShopIdAndOnJob(Long shopId, String keyWord) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSaleManByShopIdAndOnJob(session, shopId, keyWord);

      return (List<SalesMan>) q.list();
    } finally {
      release(session);
    }
  }

  public SalesMan getSaleManDTOById(Long shopId, Long id) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSaleManDTOById(session, shopId, id);
      List result = q.list();

      if (CollectionUtils.isNotEmpty(result)) {
        return (SalesMan) result.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<User> getAllUser() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllUser(session);
      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberService> getAllMemberServiceByMemberId(Long memberId) throws Exception {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllMemberServiceByMemberId(session, memberId);
      return (List<MemberService>) q.list();
    } finally {
      release(session);
    }
  }

  public Member getMemberDTOById(Long shopId, Long memberId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getMemberDTOById(session, shopId, memberId);

      List<Member> members = (List<Member>) q.list();

      if (CollectionUtils.isNotEmpty(members)) {
        return members.get(0);
      }

      return null;

    } finally {
      release(session);
    }
  }

  public int countMemberCardByShopId(Long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countMemberCardByShopId(session, shopId);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<User> getAllShopUser() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllShopUser(session);
      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberService> getMemberServiceByServiceId(Long serviceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberServiceByServiceId(session, serviceId);
      return (List<MemberService>) q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberCardService> getMemberCardServiceByServiceId(Long serviceId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getMemberCardServiceByServiceId(session, serviceId);
      return (List<MemberCardService>) q.list();
    } finally {
      release(session);
    }
  }


  public MemberCardService getMemberCardService(Long memberCardId, Long serviceId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getMemberCardService(session, memberCardId, serviceId);
      return (MemberCardService) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Member> getMemberByIds(Long shopId, Set<Long> customerIds) {
    Session session = getSession();
    try {
      Query q = SQL.getMemberByIds(session, shopId, customerIds);
      return (List<Member>) q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, CustomerDTO> getCustomerByIdSet(Long shopId, Set<Long> customerIds) {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(customerIds)) return new HashMap<Long, CustomerDTO>();
      Query query = SQL.getCustomerByIdSet(session, shopId, customerIds);
      List<Customer> customerList = query.list();
      if (CollectionUtils.isEmpty(customerList)) return new HashMap<Long, CustomerDTO>();
      Map<Long, CustomerDTO> map = new HashMap<Long, CustomerDTO>();
      for (Customer customer : customerList) {
        map.put(customer.getId(), customer.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, MemberDTO> getMemberByCustomerIdSet(Long shopId, Set<Long> customerIds) {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(customerIds)) return new HashMap<Long, MemberDTO>();
      Query query = SQL.getMemberByCustomerIdSet(session, shopId, customerIds);
      List<Member> memberList = query.list();
      if (CollectionUtils.isEmpty(memberList)) return new HashMap<Long, MemberDTO>();
      Map<Long, MemberDTO> map = new HashMap<Long, MemberDTO>();
      for (Member member : memberList) {
        map.put(member.getCustomerId(), member.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, SupplierDTO> getSupplierByIdSet(Long shopId, Set<Long> supplierIds) {
    Session session = this.getSession();
    try {
      Map<Long, SupplierDTO> map = new HashMap<Long, SupplierDTO>();
      if (CollectionUtils.isEmpty(supplierIds)) return map;
      Query query = SQL.getSupplierByIdSet(session, shopId, supplierIds);
      List<Supplier> supplierList = query.list();
      if (CollectionUtils.isEmpty(supplierList)) return new HashMap<Long, SupplierDTO>();
      for (Supplier supplier : supplierList) {
        map.put(supplier.getId(), supplier.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, CustomerDTO> getCustomerByCustomerShopId(Long shopId, Long... customerShopId) {
    Session session = this.getSession();
    try {
      Map<Long, CustomerDTO> map = new HashMap<Long, CustomerDTO>();
      if (ArrayUtil.isEmpty(customerShopId)) return map;
      Query query = SQL.getCustomerByCustomerShopId(session, shopId, customerShopId);
      List<Customer> customerList = query.list();
      if (CollectionUtils.isEmpty(customerList)) return new HashMap<Long, CustomerDTO>();
      for (Customer customer : customerList) {
        map.put(customer.getCustomerShopId(), customer.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, SupplierDTO> getSupplierBySupplierShopId(Long shopId, Long... supplierShopId) {
    Session session = this.getSession();
    try {
      Map<Long, SupplierDTO> map = new HashMap<Long, SupplierDTO>();
      if (ArrayUtil.isEmpty(supplierShopId)) return map;
      Query query = SQL.getSupplierBySupplierShopId(session, shopId, supplierShopId);
      List<Supplier> supplierList = query.list();
      if (CollectionUtils.isEmpty(supplierList)) return new HashMap<Long, SupplierDTO>();
      List<Long> supplierIds = new ArrayList<Long>();
      for (Supplier supplier : supplierList) {
        if (supplier != null && supplier.getId() != null) {
          supplierIds.add(supplier.getId());
        }
      }
      Map<Long, List<Contact>> contactsMap = getContactsBySupIds(supplierIds);
      for (Supplier supplier : supplierList) {
        List<Contact> contacts = contactsMap.get(supplier.getId());
        SupplierDTO supplierDTO = supplier.toDTO();

        if (CollectionUtils.isNotEmpty(contacts)) {
          List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
          for (Contact contact : contacts) {
            contactDTOs.add(contact.toDTO());
          }
          supplierDTO.setContacts(contactDTOs.toArray(new ContactDTO[contactDTOs.size()]));
        }
        map.put(supplier.getSupplierShopId(), supplierDTO);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierByNativeShopIds(Long supplierShopId, Long... nativeShopIds) {
    Session session = this.getSession();
    try {
      if (supplierShopId == null || ArrayUtil.isEmpty(nativeShopIds)) return null;
      Query query = SQL.getSupplierByNativeShopIds(session, supplierShopId, nativeShopIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierBySupplierShopIds(Long shopId, Long... supplierShopIds) {
    Session session = this.getSession();
    try {
      List<Supplier> suppliers = new ArrayList<Supplier>();
      if (shopId == null || ArrayUtil.isEmpty(supplierShopIds)) return suppliers;
      Query query = SQL.getSupplierBySupplierShopId(session, shopId, supplierShopIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, VehicleDTO> getVehicleByVehicleIdSet(Long shopId, Set<Long> vehicleIds) {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(vehicleIds)) return new HashMap<Long, VehicleDTO>();
      Query query = SQL.getVehicleByVehicleIdSet(session, shopId, vehicleIds);
      List<Vehicle> memberList = query.list();
      if (CollectionUtils.isEmpty(memberList)) return new HashMap<Long, VehicleDTO>();
      Map<Long, VehicleDTO> map = new HashMap<Long, VehicleDTO>();
      for (Vehicle vehicle : memberList) {
        map.put(vehicle.getId(), vehicle.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, UserDTO> getExecutorByIdSet(Long shopId, Set<Long> executorIds) {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(executorIds)) return new HashMap<Long, UserDTO>();
      Query query = SQL.getUserByIdSet(session, shopId, executorIds);
      List<User> userList = query.list();
      if (CollectionUtils.isEmpty(userList)) return new HashMap<Long, UserDTO>();
      Map<Long, UserDTO> map = new HashMap<Long, UserDTO>();
      for (User user : userList) {
        map.put(user.getId(), user.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, MemberCardDTO> getMemberCardByIds(Long shopId, Set<Long> carIds) {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(carIds)) return new HashMap<Long, MemberCardDTO>();
      Query query = SQL.getMemberCardByIds(session, shopId, carIds);
      List<MemberCard> userList = query.list();
      if (CollectionUtils.isEmpty(userList)) return new HashMap<Long, MemberCardDTO>();
      Map<Long, MemberCardDTO> map = new HashMap<Long, MemberCardDTO>();
      for (MemberCard memberCard : userList) {
        map.put(memberCard.getId(), memberCard.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }


  public List<CustomerRecordDTO> getCustomerRecordForReindex(Long shopId, long startId, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerRecordForReindex(session, shopId, startId, pageSize);
      List<CustomerRecord> customerRecordList = query.list();
      if (CollectionUtils.isEmpty(customerRecordList)) return null;
      List<CustomerRecordDTO> customerRecordDTOList = new ArrayList<CustomerRecordDTO>();
      for (CustomerRecord customerRecord : customerRecordList) {
        customerRecordDTOList.add(customerRecord.toDTO());
      }
      return customerRecordDTOList;
    } finally {
      release(session);
    }
  }

  public List<SupplierDTO> getSupplierForReindex(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplierForReindex(session, shopId, start, pageSize);
      List<Supplier> supplierList = query.list();
      if (CollectionUtils.isEmpty(supplierList)) return null;
      List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
      for (Supplier supplier : supplierList) {
        supplierDTOList.add(supplier.toDTO());
      }
      return supplierDTOList;
    } finally {
      release(session);
    }
  }

  public int countVehicleByCustomerId(Long customerId) {
    Session session = this.getSession();
    try {
      Query query = SQL.countVehicleByCustomerId(session, customerId);
      return Integer.valueOf(String.valueOf(query.uniqueResult()));
    } finally {
      release(session);
    }
  }

  public List<CustomerRecord> getCustomerRecordByCustomerId(Long shopId, Long... customerId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerRecordForReindex(session, shopId, customerId);
      return (List<CustomerRecord>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Member> getCustomerMemberByCustomerId(Long shopId, Long... customerId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerMemberForReindex(session, shopId, customerId);
      return (List<Member>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerListById(Long shopId, Long... id) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerForReindex(session, shopId, id);
      return (List<Customer>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getCustomerIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      List<Long> ids;
      Query query = SQL.getCustomerIds(session, shopId, start, pageSize);
      ids = query.list();
      return ids;
    } finally {
      release(session);
    }
  }

  public List<Object> getCustomerLicenseNosForReindex(Long shopId, List<Long> ids) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerLicenseNosForReindex(session, shopId, ids);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehiclesByIds(Long shopId, Long... vehicleId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleByIds(session, shopId, vehicleId);
      return (List<Vehicle>) query.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierDTO> getShopSuppliers(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getShopSuppliers(session, shopId);
      List<Supplier> suppliers = query.list();
      if (CollectionUtils.isEmpty(suppliers)) return null;
      List<SupplierDTO> list = new ArrayList<SupplierDTO>();
      for (Supplier supplier : suppliers) {
        list.add(supplier.toDTO());
      }
      return list;
    } finally {
      release(session);
    }
  }

  public List<String> getMemberCardTypeByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getMemberCardTypeByShopId(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List<UserGroupDTO> getAllUserGroup() {
    Session session = getSession();
    try {
      List<UserGroupDTO> userGroupDTOList = new ArrayList<UserGroupDTO>();
      Query query = SQL.getAllUserGroup(session);
      List<UserGroup> userGroupList = query.list();
      for (UserGroup userGroup : userGroupList) {
        userGroupDTOList.add(userGroup.toDTO());
      }
      return userGroupDTOList;
    } finally {
      release(session);
    }
  }

  public List<Long> getAllUserGroupIds() {
    Session session = getSession();
    try {
      Query query = SQL.getAllUserGroupIds(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<RoleDTO> getAllRoles() {
    List<RoleDTO> roleDTOList = new ArrayList<RoleDTO>();
    Session session = getSession();
    try {
      Query query = SQL.getAllRoles(session);
      List<Role> roleList = query.list();
      for (Role role : roleList) {
        roleDTOList.add(role.toDTO());
      }
      return roleDTOList;
    } finally {
      release(session);
    }
  }

  public List<Role> getRoles(SystemType systemType) {
    Session session = getSession();
    try {
      Query query = SQL.getRoles(session, systemType);
      return query.list();
    } finally {
      release(session);
    }
  }


  public int countPlansByStatus(Long shopId, PlansRemindStatus status) {
    Session session = this.getSession();

    try {
      Query q = SQL.countPlansByStatus(session, shopId, status);

      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countActivityPlansExpired(Long shopId, Long now) {
    Session session = this.getSession();

    try {
      Query q = SQL.countActivityPlansExpired(session, shopId, now);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public ShopPlan getPlan(Long shopId, Long id) {
    Session session = this.getSession();

    try {
      Query q = SQL.getPlan(session, shopId, id);

      List<ShopPlan> shopPlanList = q.list();

      if (CollectionUtils.isEmpty(shopPlanList)) {
        return null;
      }

      return shopPlanList.get(0);
    } finally {
      release(session);
    }
  }

  //本地计划初始化
  public int countPlans() {
    Session session = this.getSession();

    try {
      Query q = SQL.countPlans(session);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  //本地计划初始化
  public List<ShopPlan> getHundredShopPlans() {
    Session session = this.getSession();

    try {
      Query q = SQL.getHundredShopPlans(session);

      return (List<ShopPlan>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getVehicleIds(Long shopId, int start, int rows) {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleIds(session, shopId, start, rows);
      return query.list();
    } finally {
      release(session);
    }

  }


  public void updateUserPassword(UserDTO userDTO) {
    Session session = getSession();

    try {
      Query q = SQL.updateUserPassword(session, userDTO);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<Member> getMemberByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getMemberByShopId(session, shopId);
      return (List<Member>) q.list();
    } finally {
      release(session);
    }

  }

  public void updateMemberPassword(Member member) {
    Session session = getSession();
    try {
      Query q = SQL.updateMemberPassword(session, member);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public CustomerRecord getCustomerRecord(Long shopId, Long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerRecord(session, shopId, customerId);
      List<CustomerRecord> customerRecordList = (List<CustomerRecord>) q.list();

      if (CollectionUtils.isEmpty(customerRecordList)) {
        return null;
      }
      return customerRecordList.get(0);
    } finally {
      release(session);
    }
  }

  public Vehicle getVehicleById(Long shopId, Long vehicleId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getVehicleById(session, shopId, vehicleId);

      List<Vehicle> vehicleList = (List<Vehicle>) q.list();

      if (CollectionUtils.isEmpty(vehicleList)) {
        return null;
      }

      return vehicleList.get(0);
    } finally {
      release(session);
    }
  }

  public List<UserLimit> getUserNecessaryLimitTags() {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserLimitTags(session);
      List<UserLimit> userLimitList = (List<UserLimit>) q.list();
      if (CollectionUtils.isEmpty(userLimitList)) {
        return null;
      }
      return userLimitList;
    } finally {
      release(session);
    }
  }

  public UserSwitch getUserSwitchByShopIdAndScene(Long shopId, String scene) {
    UserSwitch userSwitch = null;
    Session session = this.getSession();
    try {
      Query q = SQL.getUserSwitchByShopIdAndScene(session, shopId, scene);
      List<UserSwitch> userLimitList = (List<UserSwitch>) q.list();
      if (CollectionUtils.isEmpty(userLimitList)) {
        return null;
      }
      userSwitch = userLimitList.get(0);
      return userSwitch;
    } finally {
      release(session);
    }
  }

  public List<UserSwitch> getUserSwitchListByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserSwitchListByShopId(session, shopId);
      List<UserSwitch> userSwitchList = (List<UserSwitch>) q.list();
      if (CollectionUtils.isEmpty(userSwitchList)) {
        return null;
      }
      return userSwitchList;
    } finally {
      release(session);
    }
  }

  public List<MemberService> getMemberServiceForInitService(int size, int page) {
    Session session = this.getSession();

    try {
      Query q = SQL.getMemberServiceForInitService(session, size, page);

      return (List<MemberService>) q.list();
    } finally {
      release(session);
    }
  }

  public int countMemberService() {
    Session session = this.getSession();

    try {
      Query q = SQL.countMemberService(session);

      return Integer.valueOf(q.uniqueResult().toString());

    } finally {
      release(session);
    }
  }

  public List<SalesMan> getSalesManListByIds(Long shopId, Set<Long> salesManIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesManListByIds(session, salesManIds);
      return (List<SalesMan>) q.list();
    } finally {
      release(session);
    }
  }

  public MergeTask getMergeTaskOneByOne() {
    Session session = getSession();
    try {
      Query q = SQL.getMergeTaskOneByOne(session);
      return (MergeTask) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Long> getCustomerIdByLicenceNo(long shopId, String licenceNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerIdByLicenceNo(session, shopId, licenceNo);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<TreeMenu> getTreeMenuByParentId(Long treeId, List<Long> roleIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getTreeMenuByParentId(session, treeId, roleIds);
      return (List<TreeMenu>) q.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleModifyLog> getVehicleModifyLogByStatus(StatProcessStatus[] statProcessStatuses) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleModifyLogByStatus(session, statProcessStatuses);
      return (List<VehicleModifyLog>) q.list();
    } finally {
      release(session);
    }
  }

  public void batchUpdateVehicleModifyLogStatus(List<Long> ids, StatProcessStatus done) {
    Session session = getSession();
    try {
      Query q = SQL.batchUpdateVehicleModifyLogStatus(session, ids, done);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public CustomerRecord getCustomerRecordDTOByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCustomerRecordDTOByCustomerId(session, shopId, customerId);

      List<CustomerRecord> customerRecordList = (List<CustomerRecord>) q.list();

      if (CollectionUtils.isEmpty(customerRecordList)) {
        return null;
      }

      return customerRecordList.get(0);

    } finally {
      release(session);
    }
  }


  public CustomerRecord getCustomerRecordDTOByCustomerIdAndShopId(Long shopId, Long customerId) {
    Session session = getSession();

    try {
      Query q = SQL.getCustomerRecordDTOByCustomerIdAndShopId(session, shopId, customerId);

      List<CustomerRecord> customerRecordList = (List<CustomerRecord>) q.list();

      if (CollectionUtils.isEmpty(customerRecordList)) {
        return null;
      }

      return customerRecordList.get(0);

    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByCustomerShopIdAndShopId(Long shopId, Long customerShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByCustomerShopIdAndShopId(session, shopId, customerShopId);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByCustomerShopId(Long customerShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByCustomerShopId(session, customerShopId);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierBySupplierShopIdAndShopId(Long shopId, Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierBySupplierShopIdAndShopId(session, shopId, supplierShopId);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierBySupplierShopId(Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierBySupplierShopId(session, supplierShopId);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }


  public List<Long> getCustomerIdsByNameWithFuzzyQuery(Long shopId, String customerName) {
    Session session = getSession();
    try {
      Query q = SQL.getCustomerIdsByNameWithFuzzyQuery(session, shopId, customerName);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getWholeSalersByCustomerShopId(Long customerShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWholeSalersByCustomerShopId(session, customerShopId);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getRelatedCustomersByShopId(Long wholeSalerShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRelatedCustomerByShopId(session, wholeSalerShopId);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getShopRelatedCustomer(Long wholeSalerShopId, Long... customerIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopRelatedCustomer(session, wholeSalerShopId, customerIds);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public int countRelatedCustomersByShopId(Long wholeSalerShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countRelatedCustomersByShopId(session, wholeSalerShopId);
      return NumberUtil.intValue(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<Supplier> getRelatedSuppliersByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRelatedSuppliersByShopId(session, shopId);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getSupplierIdsByNameWithFuzzyQuery(Long shopId, String supplierName) {
    Session session = getSession();
    try {
      Query q = SQL.getSupplierIdsByNameWithFuzzyQuery(session, shopId, supplierName);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getWholesalerByFuzzyName(Long shopId, String wholesalerName) {
    Session session = getSession();
    try {
      Query q = SQL.getWholesalerByFuzzyName(session, shopId, wholesalerName);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }


  public List<UserGroup> getUserGroupByCondition(UserGroupSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupByCondition(session, condition);
      return (List<UserGroup>) q.list();
    } finally {
      release(session);
    }
  }

  public Long countUserGroupByCondition(UserGroupSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUserGroupByCondition(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0l : (Long) o;
    } finally {
      release(session);
    }
  }

  public List<User> getUserSuggestionByName(Set<Long> departmentIds, String name, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserSuggestionByName(session, departmentIds, name, shopId);
      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }

  public List<UserDTO> getUserByCondition(UserSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByCondition(session, condition).setResultTransformer(Transformers.aliasToBean(UserDTO.class));
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long countUserByCondition(UserSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUserByCondition(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0l : Long.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<UserGroup> getUserGroupByIds(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupByIds(session, ids);
      return (List<UserGroup>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Module> getModule(SystemType systemType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getModule(session, systemType);
      return (List<Module>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Module> getAllModule(SystemType systemType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllModule(session, systemType);
      return (List<Module>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Role> getRolesByModuleIds(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRolesByModuleIds(session, ids);
      return (List<Role>) q.list();
    } finally {
      release(session);
    }
  }

  public boolean deleteUserGroupRole(Long userGroupId, Long roleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteUserGroupRole(session, userGroupId, roleId);
      return q.executeUpdate() > 0;
    } finally {
      release(session);
    }
  }

  public List<Department> getDepartmentsByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDepartmentsByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Boolean checkDepartmentBeforeDelete(Long departmentId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkDepartmentBeforeDelete(session, departmentId, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public List<Occupation> getOccupationsByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOccupationsByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }


  public Boolean checkOccupationBeforeDelete(Long occupationId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkOccupationBeforeDelete(session, occupationId, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public Long maxParentDepartmentChildren(Long parentId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.maxParentDepartmentChildren(session, parentId, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return o == null ? 0l : Long.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public Long maxParentOccupationChildren(Long departmentId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.maxParentOccupationChildren(session, departmentId, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return o == null ? 0l : Long.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public boolean checkOccupation(OccupationDTO occupationDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkOccupation(session, occupationDTO);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public boolean checkDepartment(DepartmentDTO departmentDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkDepartment(session, departmentDTO);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public SalesMan getSalesManByName(Long shopId, String name) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesManByName(session, shopId, name);

      List<SalesMan> salesManList = (List<SalesMan>) q.list();

      if (CollectionUtils.isEmpty(salesManList)) {
        return null;
      }

      return salesManList.get(0);
    } finally {
      release(session);
    }
  }

  public List<SalesMan> getSalesManByNames(Long shopId, Set<String> names) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesManByName(session, shopId, names);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberCard> getEnableMemberCardDTOByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getEnableMemberCardDTOByShopId(session, shopId);

      return (List<MemberCard>) q.list();
    } finally {
      release(session);
    }
  }

  public boolean checkModule(ModuleDTO moduleDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkModule(session, moduleDTO);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public boolean checkRole(RoleDTO roleDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkRole(session, roleDTO);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public boolean checkRoleBeforeDelete(Long roleId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkRoleBeforeDelete(session, roleId, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public boolean deleteRoleResource(Long roleId, Long resourceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteRoleResource(session, roleId, resourceId);
      int i = q.executeUpdate();
      return i > 0;
    } finally {
      release(session);
    }

  }

  public List<MenuDTO> getResourcesByCondition(ResourceSearchCondition condition) {
    Session session = this.getSession();
    try {
      List<MenuDTO> list = new ArrayList<MenuDTO>();
      MenuDTO menuDTO;
      Query q = SQL.getResourcesByCondition(session, condition);
      List<Object[]> objects = q.list();
      for (Object[] objectArray : objects) {
        menuDTO = ((Menu) objectArray[0]).toDTO();
        menuDTO.fromResourceDTO(((Resource) objectArray[1]).toDTO());
        menuDTO.setMenuId((Long) objectArray[2]);
        menuDTO.setResourceId((Long) objectArray[3]);
        list.add(menuDTO);
      }
      return list;
    } finally {
      release(session);
    }
  }

  public Integer countResourcesByCondition(ResourceSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countResourcesByCondition(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public boolean checkResourceBeforeDelete(Long resourceId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkResourceBeforeDelete(session, resourceId, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public List<ShopVersion> getAllShopVersion() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllShopVersion(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public String getRoleIdByShopVersionId(Long shopVersionId) {
    Session session = this.getSession();
    try {
      StringBuilder ids = new StringBuilder();
      Query q = SQL.getRoleIdByShopVersionId(session, shopVersionId);
      List<Long> roleIds = q.list();
      for (Long id : roleIds) {
        ids.append(id).append(",");
      }
      return ids.toString();
    } finally {
      release(session);
    }
  }

  public String getRoleIdsByUserGroupId(Long userGroupId) {
    Session session = this.getSession();
    try {
      StringBuilder ids = new StringBuilder();
      Query q = SQL.getRoleIdsByUserGroupId(session, userGroupId);
      List<Long> roleIds = q.list();
      for (Long id : roleIds) {
        ids.append(id).append(",");
      }
      return ids.toString();
    } finally {
      release(session);
    }
  }

  public List<Role> getRolesByShopVersionId(Long shopVersionId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRolesByShopVersionId(session, shopVersionId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public boolean deleteShopRole(Long shopVersionId, Long roleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteShopRole(session, shopVersionId, roleId);
      Object o = q.executeUpdate();
      Integer number = (Integer) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public boolean deleteRoleByShopVersions(Long shopVersionId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteRoleByShopVersions(session, shopVersionId);
      Object o = q.executeUpdate();
      Integer number = (Integer) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public List<UserGroup> getUserGroupsByShopVersionId(Long shopVersionId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupsByShopVersionId(session, shopVersionId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public boolean deleteUserGroupShop(Long userGroupId, Long shopVersionId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteUserGroupShop(session, userGroupId, shopVersionId);
      return q.executeUpdate() > 0;
    } finally {
      release(session);
    }
  }

  public boolean deleteUserGroupRole(Long userGroupId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteUserGroupRole(session, userGroupId);
      return q.executeUpdate() > 0;
    } finally {
      release(session);
    }
  }

  public List<Resource> getResourceByRoleType(RoleType roleType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getResourceByRoleType(session, roleType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public boolean checkUserGroupName(String name, Long shopVersionId, Long shopId, Long userGroupId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkUserGroupName(session, shopVersionId, shopId, userGroupId, name);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public List<UserGroup> getUserGroupsByCondition(Long shopVersionId, Long shopId, UserGroupSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGroupsByCondition(session, shopVersionId, shopId, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long countUserGroupsByCondition(Long shopVersionId, Long shopId, UserGroupSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUserGroupsByCondition(session, shopVersionId, shopId, condition);
      Object o = q.uniqueResult();
      return o == null ? 0l : Long.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public long countStaffByCondition(StaffSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countStaffByCondition(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0l : Long.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<SalesManDTO> getStaffByCondition(StaffSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getStaffByCondition(session, condition).setResultTransformer(Transformers.aliasToBean(SalesManDTO.class));
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<User> getUserByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int checkSalesManCode(String salesManCode, Long shopId, Long salesManId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkSalesManCode(session, salesManCode, shopId, salesManId);
      return Integer.parseInt(String.valueOf(q.uniqueResult()));
    } finally {
      release(session);
    }
  }

  public List<Department> getDepartmentByName(Long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDepartmentByName(session, shopId, name);
      return q.list();
    } finally {
      release(session);
    }
  }

  public boolean checkDepartmentNameByShopId(String name, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkDepartmentNameByShopId(session, name, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public boolean checkDepartmentForDelete(Long departmentId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkDepartmentForDelete(session, departmentId, shopId);
      Object o = q.uniqueResult();
      Long number = (Long) o;
      return number > 0;
    } finally {
      release(session);
    }
  }

  public Long countUserGroupByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUserGroupByShopId(session, shopId);
      Object o = q.uniqueResult();
      return (Long) o;
    } finally {
      release(session);
    }
  }

  public UserDTO getUserByStaffId(Long staffId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByStaffId(session, staffId);
      User user = (User) q.uniqueResult();
      if (user != null) return user.toDTO();
      return null;
    } finally {
      release(session);
    }
  }

  public Long countUserByUserNo(String userNo, Long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUserByUserNo(session, userNo, userId);
      Object o = q.uniqueResult();
      return (Long) o;
    } finally {
      release(session);
    }
  }

  public List<InsuranceCompanyDTO> getAllInsuranceCompanyDTOs() {
    Session session = this.getSession();
    try {
      List<InsuranceCompanyDTO> insuranceCompanyDTOs = new ArrayList<InsuranceCompanyDTO>();
      Query q = SQL.getAllInsuranceCompanyDTOs(session);
      List<InsuranceCompany> insuranceCompanies = q.list();
      if (CollectionUtils.isNotEmpty(insuranceCompanies)) {
        for (InsuranceCompany insuranceCompany : insuranceCompanies) {
          insuranceCompanyDTOs.add(insuranceCompany.toDTO());
        }
      }
      return insuranceCompanyDTOs;
    } finally {
      release(session);
    }
  }

  public List<Customer> getAllCustomerByNameAndMobile(long shopId, String name, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getAllCustomerByNameAndMobile(session, shopId, name, mobile);

      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getAllSupplierByNameAndMobile(long shopId, String name, String mobile) {
    Session session = this.getSession();

    try {
      Query q = SQL.getAllSupplierByNameAndMobile(session, shopId, name, mobile);

      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }


  public List<Customer> getCustomerByShopId(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByShopId(session, shopId, start, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerServiceJob> getAllCustomerServiceJob() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllCustomerServiceJob(session);
      return (List<CustomerServiceJob>) q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberService> getAllMemberService() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllMemberService(session);
      return (List<MemberService>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getSimilarCustomer(CustomerDTO customerDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSimilarCustomer(session, customerDTO);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getCustomerSupplierContactByMobile(Long shopId, String mobile) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerSupplierContactByMobile(session, shopId, mobile);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Customer getMatchCustomerByContactMobiles(Set<String> mobiles, Long shopId) {
    if (CollectionUtil.isEmpty(mobiles)) return null;
    Session session = this.getSession();
    try {
      Query q = SQL.getMatchCustomerByContactMobiles(session, mobiles, shopId);
      return (Customer) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Customer getRelatedCustomerByContactMobiles(Set<String> mobiles, Long shopId) {
    if (CollectionUtil.isEmpty(mobiles)) return null;
    Session session = this.getSession();
    try {
      Query q = SQL.getRelatedCustomerByContactMobiles(session, mobiles, shopId);
      return (Customer) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Supplier getMatchSupplierByContactMobiles(Set<String> mobiles, Long shopId) {
    if (CollectionUtil.isEmpty(mobiles)) return null;
    Session session = this.getSession();
    try {
      Query q = SQL.getMatchSupplierByContactMobiles(session, mobiles, shopId);
      return (Supplier) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Supplier getRelatedSupplierByContactMobiles(Set<String> mobiles, Long shopId) {
    if (CollectionUtil.isEmpty(mobiles)) return null;
    Session session = this.getSession();
    try {
      Query q = SQL.getRelatedSupplierByContactMobiles(session, mobiles, shopId);
      return (Supplier) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Customer> getMatchCustomer(CustomerDTO customerDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMatchCustomer(session, customerDTO);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSimilarSupplier(SupplierDTO supplierDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSimilarSupplier(session, supplierDTO);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getMatchSupplier(SupplierDTO supplierDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMatchSupplier(session, supplierDTO);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopVersion> getShopVersionByIds(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopVersionByIds(session, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long checkCustomerWithoutSendInvitationCodeSms(Long shopId, List<String> mobiles) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkCustomerWithoutSendInvitationCodeSms(session, shopId, mobiles);
      Object o = q.uniqueResult();
      return (Long) o;
    } finally {
      release(session);
    }
  }

  public Long checkSupplierWithoutSendInvitationCodeSms(Long shopId, List<String> mobiles) {
    Session session = this.getSession();
    try {
      Query q = SQL.checkSupplierWithoutSendInvitationCodeSms(session, shopId, mobiles);
      Object o = q.uniqueResult();
      return (Long) o;
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByShopId(session, shopId);
      return (List<Customer>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByShopId(Long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getVehicleByShopId(session, shopId);

      return (List<Vehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public UserClientInfo getUserClientInfoByUserNo(Long shopId, String userNo, String finger) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserClientInfoByUserNo(session, shopId, userNo, finger);
      return (UserClientInfo) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<UserClientInfo> getUserClientInfoByFinger(String finger) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserClientInfoByFinger(session, finger);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserLoginLog> getUserLoginLogByUserNo(String sessionId, String userNo, String finger) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserLoginLogByUserNo(session, sessionId, userNo, finger);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserLoginLog> getUserClientLoginLog(Long startDateTime, Long endDateTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserClientLoginLog(session, startDateTime, endDateTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UrlMonitorConfig> getUrlMonitorConfig(String url) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUrlMonitorConfig(session, url);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSuppliersByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSuppliersByShopIdLongs(session, shopId);
      return (List<Supplier>) q.list();
    } finally {
      release(session);
    }
  }


  public UserGuideFlow getUserGuideFlowByName(String flowName) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGuideFlowByName(session, flowName);
      return (UserGuideFlow) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<UserGuideFlow> getAllUserGuideFlows() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllUserGuideFlows(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserGuideStep> getUserGuideStepsByFlowName(String flowName) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGuideStepsByFlowName(session, flowName);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserGuideStep> getUserGuideStepByName() {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGuideStepByName(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<UserGuideHistory> getWaitingUserGuideHistory(long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWaitingUserGuideHistory(session, userId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public UserGuideHistory getUserGuideHistoryByFlowName(long userId, String flowName) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserGuideHistoryByFlowName(session, userId, flowName);
      return (UserGuideHistory) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<User> getAllUserByShopId(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getShopUser(session, shopId);

      return (List<User>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getMenuByResourceIds(Set<Long> resourceIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMenuByResourceIds(session, resourceIds.toArray(new Long[resourceIds.size()]));
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Menu> getMenuByResourceId(long resourceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMenuByResourceIds(session, resourceId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public User getSystemCreatedUser(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSystemCreatedUser(session, shopId);
      List<User> users = (List<User>) q.list();
      if (users != null && users.size() > 0) {
        return users.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<User> getUser(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUser(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Menu getMenu(MenuType menu) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMenu(session, menu);
      return (Menu) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<UserSwitch> getUserSwitchMenuIdNotNull(long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUserSwitchMenuIdNotNull(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public boolean isResourceValueDuplicated(String value, Long resourceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getResourceByValue(session, value, resourceId);
      List<Resource> resourceList = (List<Resource>) q.list();
      return CollectionUtil.isNotEmpty(resourceList);
    } finally {
      release(session);
    }
  }

  public boolean isResourceNameDuplicated(String name, Long resourceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getResourceByName(session, name, resourceId);
      List<Resource> resourceList = (List<Resource>) q.list();
      return CollectionUtil.isNotEmpty(resourceList);
    } finally {
      release(session);
    }
  }


  public Map<Long, AppointService> getAppointServiceMapByIds(Long shopId, Set<Long> appointServiceIds) {
    Map<Long, AppointService> appointServiceMap = new HashMap<Long, AppointService>();
    if (shopId == null || CollectionUtils.isEmpty(appointServiceIds)) {
      return appointServiceMap;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppointServiceByIds(session, shopId, appointServiceIds);
      List<AppointService> appointServices = q.list();
      if (CollectionUtils.isNotEmpty(appointServices)) {
        for (AppointService appointService : appointServices) {
          appointServiceMap.put(appointService.getId(), appointService);
        }
      }
      return appointServiceMap;
    } finally {
      release(session);
    }
  }

  // add by zhuj
  public List<Contact> getContactByCusOrSupOrNameOrMobile(Long customerId, Long supplierId, Long shopId, String name, String... mobiles) {
    //方法不接受全部参数为空的情况，否则性能问题
    if (customerId == null && supplierId == null && shopId == null && StringUtils.isBlank(name) && ArrayUtil.isEmpty(mobiles)) {
      return new ArrayList<Contact>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getContactByCusOrSupOrNameOrMobile(session, customerId, supplierId, shopId, name, mobiles);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getContact(Long shopId, Long customerId, Long supplierId, String mobile) {
    Session session = this.getSession();
    try {
      Query q = SQL.getContact(session, shopId, customerId, supplierId, mobile);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getContactByCusAndSup(Long customerId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getContactByCusAndSup(session, customerId, supplierId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByIds(Long shopId, Long... customerIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByIds(session, shopId, customerIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getContactByCusId(Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getContactByCusId(session, customerId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ContactDTO> getContactDTOByIds(Long shopId, Long... contactIds) {
    Session session = this.getSession();
    List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
    try {
      if (ArrayUtil.isEmpty(contactIds))
        return contactDTOs;
      Query q = SQL.getContactsByIds(session, shopId, contactIds);
      List<Contact> contacts = q.list();
      for (Contact c : contacts) {
        contactDTOs.add(c.toDTO());
      }
      return contactDTOs;
    } finally {
      release(session);
    }
  }

  public Map<Long, ContactDTO> getMainContactByCusId(Long... customerIds) {
    Map<Long, ContactDTO> map = new HashMap<Long, ContactDTO>();
    if (ArrayUtil.isEmpty(customerIds)) {
      return map;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getMainContactByCusId(session, customerIds);
      List<Contact> contactList = q.list();
      for (Contact c : contactList) {
        map.put(c.getCustomerId(), c.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<Contact> getContactsByids(Long shopId, Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getContactsByIds(session, shopId, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getContactBySupId(Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getContactBySupId(session, customerId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getOtherContactsIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOtherContactsIds(session, shopId, start, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  // add by zhuj
  public Map<Long, List<Contact>> getContactsByCusIds(List<Long> customerIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getContactsByCusIds(session, customerIds);
      List<Contact> contacts = q.list();
      if (!CollectionUtils.isEmpty(contacts)) {
        Map<Long, List<Contact>> contactMap = new HashMap<Long, List<Contact>>();
        List<Contact> contactList = null;
        for (Contact contact : contacts) {
          contactList = contactMap.get(contact.getCustomerId());
          if (contactList == null) {
            contactList = new ArrayList<Contact>();
          }
          contactList.add(contact);
          contactMap.put(contact.getCustomerId(), contactList);
        }
        return contactMap;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public Map<Long, List<ContactDTO>> getContactDTOsByCusIds(List<Long> customerIds) {
    Map<Long, List<ContactDTO>> contactMap = new HashMap<Long, List<ContactDTO>>();
    if (CollectionUtil.isEmpty(customerIds)) return contactMap;
    Session session = this.getSession();
    try {
      Query q = SQL.getContactsByCusIds(session, customerIds);
      List<Contact> contacts = q.list();
      if (!CollectionUtils.isEmpty(contacts)) {
        List<ContactDTO> contactList = null;
        for (Contact contact : contacts) {
          contactList = contactMap.get(contact.getCustomerId());
          if (contactList == null) {
            contactList = new ArrayList<ContactDTO>();
          }
          contactList.add(contact.toDTO());
          contactMap.put(contact.getCustomerId(), contactList);
        }
        return contactMap;
      }
      return null;
    } finally {
      release(session);
    }
  }

  // add by zhuj
  public Map<Long, List<Contact>> getContactsBySupIds(List<Long> supplierIds) {
    Map<Long, List<Contact>> contactMap = new HashMap<Long, List<Contact>>();
    if (CollectionUtils.isEmpty(supplierIds)) {
      return contactMap;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getContactsBySupIds(session, supplierIds);
      List<Contact> contacts = q.list();
      if (!CollectionUtils.isEmpty(contacts)) {
        List<Contact> contactList = null;
        for (Contact contact : contacts) {
          contactList = contactMap.get(contact.getSupplierId());
          if (contactList == null) {
            contactList = new ArrayList<Contact>();
          }
          contactList.add(contact);
          contactMap.put(contact.getSupplierId(), contactList);
        }
      }
      return contactMap;
    } finally {
      release(session);
    }
  }

  public List<ContactGroup> getAllContactGroup() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllContactGroup(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ContactGroup> getContactGroupByIds(Long[] groupIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getContactGroupByIds(session, groupIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  //   分页查询用户
  public List<Customer> getCustomersByPage(int pageSize, int start) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerByPagesizeAndPageNum(session, pageSize, start);
      return query.list();
    } finally {
      release(session);
    }
  }

  // 分页查询供应商
  public List<Supplier> getSuppliersByPage(int pageSize, int start) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplierByPagesizeAndPageNum(session, pageSize, start);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Long countCustomer() {
    Session session = this.getSession();
    try {
      Query query = SQL.countCustomer(session);
      Object result = query.uniqueResult();
      return (Long) result;
    } finally {
      release(session);
    }
  }

  public Long countSupplier() {
    Session session = this.getSession();
    try {
      Query query = SQL.countSupplier(session);
      Object result = query.uniqueResult();
      return (Long) result;
    } finally {
      release(session);
    }
  }

  public boolean isClientBinding(Long shopId, String userNo, String mac) {
    Session session = this.getSession();
    try {
      Query query = SQL.countClientBindingLog(session, shopId, userNo, mac);
      return (Long) query.uniqueResult() > 0;
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierByIds(Set<Long> supplierIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplierByIds(session, supplierIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerToInitContact() {
    Session session = this.getSession();
    try {
      Query query = SQL.getCustomerToInitContact(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Supplier> getSupplierToInitContact() {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplierToInitContact(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public void deleteCustomerSupplierBusinessScope(Long shopId, Long customerId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteCustomerSupplierBusinessScope(session, shopId, customerId, supplierId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<BusinessScope> getCustomerSupplierBusinessScope(Long shopId, Long customerId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerSupplierBusinessScope(session, shopId, customerId, supplierId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BusinessScope> getCustomerSupplierBusinessScope(Long shopId, Set<Long> customerIdSet, Set<Long> supplierIdSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerSupplierBusinessScope(session, shopId, customerIdSet, supplierIdSet);
      return q.list();
    } finally {
      release(session);
    }
  }


  public boolean isUserNoExistedInSystem(String userNo) {
    Session session = this.getSession();
    try {
      Query query = SQL.countUserNo(session, userNo);
      return (Long) query.uniqueResult() > 0;
    } finally {
      release(session);
    }
  }

  public boolean isAppUserMobileExisted(String mobile, AppUserType appUserType) {
    Session session = this.getSession();
    try {
      Query q = SQL.countAppUserMobile(session, mobile, appUserType);
      return (Long) q.uniqueResult() > 0;
    } finally {
      release(session);
    }
  }

  public boolean isAppUserNoExisted(String appUserNo, AppUserType appUserType) {
    Session session = this.getSession();
    try {
      Query q = SQL.countAppUserNo(session, appUserNo, appUserType);
      return (Long) q.uniqueResult() > 0;
    } finally {
      release(session);
    }
  }

  public int countCustomerByName(String name, Long customerId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countCustomerByName(session, name, customerId, shopId);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countCustomerByNameAndMobile(String name, String mobile, Long customerId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countCustomerByNameAndMobile(session, name, mobile, customerId, shopId);
      return NumberUtil.intValue((Integer) q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public int countSupplierBySupplierShopId(Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSupplierBySupplierShopId(session, supplierShopId);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }


  public List<Vehicle> getVehicleMapByLicenceNos(Long shopId, Set<String> licenceNos) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleMapByLicenceNos(session, shopId, licenceNos);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 找到自己收藏或者关联的客户店铺shopId
   *
   * @param shopId
   * @return
   */
  public List<Long> getCustomerShopIds(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerShopIds(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RelatedShopUpdateTask> getRelatedShopUpdateTaskByShopId(Long shopId, ExeStatus exeStatus) {
    if (shopId == null) {
      return new ArrayList<RelatedShopUpdateTask>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getRelatedShopUpdateTaskByShopId(session, shopId, exeStatus);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RelatedShopUpdateTask> getFirstRelatedShopUpdateTask(ExeStatus exeStatus) {

    Session session = this.getSession();
    try {
      Query q = SQL.getFirstRelatedShopUpdateTask(session, exeStatus);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getContactsByCustomerMobiles(Long shopId, Set<String> mobiles, Set<Long> excludeCustomerIds) {
    if (shopId == null || CollectionUtils.isEmpty(mobiles)) {
      return new ArrayList<Contact>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getContactByCustomerMobiles(session, shopId, mobiles, excludeCustomerIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getContactsBySupplierMobiles(Long shopId, Set<String> mobiles, Set<Long> excludeSupplierIds) {
    if (shopId == null || CollectionUtils.isEmpty(mobiles)) {
      return new ArrayList<Contact>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getContactsBySupplierMobiles(session, shopId, mobiles, excludeSupplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<CusOrSupOrderIndexSchedule> getCusOrSupOrderIndexScheduleDTOs() {
    Session session = this.getSession();
    try {
      Query q = SQL.getCusOrSupOrderIndexScheduleDTOs(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<CusOrSupOrderIndexSchedule> getCusOrSupOrderIndexScheduleDTOByCusOrSupId(Long shopId, Long customerId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCusOrSupOrderIndexScheduleDTOByCusOrSupId(session, shopId, customerId, supplierId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByKey(Long shopId, String key, int rowStart, int pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getCustomerByKey(session, shopId, key, rowStart, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public OBD getObdBySn(String sn) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdBySn(session, sn);
      return (OBD) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getBindingAppVehicleByVinUserNo(String vin, String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBindingAppVehicleByVinUserNo(session, vin, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public VehicleBasicInfo getVehicleBasicInfoByVin(String vin) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleBasicInfoByVin(session, vin);
      return (VehicleBasicInfo) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AppUser> getAppUserByUserNo(String appUserNo, String mobile) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByUserNo(session, appUserNo, mobile);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUser> getAppUserByUserNos(Set<String> appUserNos) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByUserNos(session, appUserNos);
      return q.list();
    } finally {
      release(session);
    }
  }

  public ObdUserVehicle getBundlingObdUserVehicleByObdId(Long obdId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBundlingObdUserVehicleByObdId(session, obdId);
      return (ObdUserVehicle) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<ObdUserVehicle> getBundlingObdUserVehicleByUserNo(String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBundlingObdUserVehicleByUserNo(session, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ObdUserVehicle> getBundlingObdUserVehicleByUserNos(Set<String> appUserNos) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBundlingObdUserVehicleByUserNos(session, appUserNos);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ObdUserVehicle> getObdUserVehicle(String appUserNo, Long vehicleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdUserVehicle(session, appUserNo, vehicleId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ObdUserVehicle> getUnBundlingObdUserVehicle(String userNo, Long vehicleId, Long obdId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUnBundlingObdUserVehicle(session, userNo, vehicleId, obdId);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<ObdUserVehicle> getObdUserVehicleByAppUserNo(String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdUserVehicleByAppUserNo(session, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Impact getImpactByUUID(String uuid) {
    Session session = this.getSession();
    try {
      Query q = SQL.getImpactByUUID(session, uuid);
      return (Impact) q.uniqueResult();
    } finally {
      release(session);
    }
  }


  public List<AppVehicle> getAppVehicleByUserNoVehicleNo(String appUserNo, String vehicleNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleByUserNoVehicleNo(session, appUserNo, vehicleNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public AppVehicle getAppVehicleByUserNoVehicleVin(String appUserNo, String vehicleVin) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleByUserNoVehicleVin(session, appUserNo, vehicleVin);
      return (AppVehicle) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getAppVehicleByAppUserNo(String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleByAppUserNo(session, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getAppVehicleByAppUserNos(Set<String> appUserNos) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleByAppUserNos(session, appUserNos);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getAppVehicleByIds(Set<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return new ArrayList<AppVehicle>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleByIds(session, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getAppVehicleByAppUserNoAndVehicleNo(String appUserNo, String vehicleNo, Status status) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleByAppUserNoAndVehicleNo(session, appUserNo, vehicleNo, status);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Set<String> getAppVehicleNoByAppUserNo(String appUserNo) {
    Set<String> vehicleNos = new HashSet<String>();
    if (StringUtils.isNotEmpty(appUserNo)) {
      Session session = this.getSession();
      try {
        Query q = SQL.getAppVehicleNosByAppUserNo(session, appUserNo);
        List<String> result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          for (String vehicleNo : result) {
            if (StringUtils.isNotEmpty(vehicleNo)) {
              vehicleNos.add(vehicleNo);
            }
          }
        }
      } finally {
        release(session);
      }
    }
    return vehicleNos;
  }


  public AppUserLoginInfo getAppUserLoginInfoBySessionId(String sessionId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserLoginInfoBySessionId(session, sessionId);
      return (AppUserLoginInfo) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public AppUserLoginInfo getAppUserLoginInfoByMqSessionId(String mqSessionId, AppUserType userType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserLoginInfoByMqSessionId(session, mqSessionId, userType);
      return (AppUserLoginInfo) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public AppUserLoginInfo getAppUserLoginInfoByUserNo(String userNo, AppUserType appUserType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserLoginInfoByUserNo(session, userNo, appUserType);
      return (AppUserLoginInfo)CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<OBD> getObdById(Long... ids) {
    if (ArrayUtil.isEmpty(ids)) {
      return new ArrayList<OBD>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getObdById(session, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public OBD getObdByImei(String imei) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdByImei(session, imei);
      return (OBD) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomer(String userNo, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomer(session, userNo, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomer(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomer(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countAppUserCustomerUpdateTask(ExeStatus exeStatus) {
    Session session = this.getSession();
    try {
      Query q = SQL.countAppUserCustomerUpdateTask(session, exeStatus);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public List<AppUserCustomerUpdateTask> getAppUserCustomerUpdateTask(Set<Long> operatorIdSet, OperatorType operatorType, ExeStatus exeStatus) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomerUpdateTask(session, operatorIdSet, operatorType, exeStatus);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomerUpdateTask> getAppUserCustomerUpdateTask(ExeStatus exeStatus, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomerUpdateTask(session, exeStatus, pager);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<CustomerVehicle> getCustomerVehicleByCustomerIds(Set<Long> customerIdSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerVehicleByCustomerIds(session, customerIdSet);
      return (List<CustomerVehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<UserClientInfo> getProbableUserDTOByFinger(String finger) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getProbableUserDTOByFinger(session, finger);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomer(String userNo, Long customerId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomer(session, userNo, customerId, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomerAndVehicle(String userNo, Long customerId, Long shopId, Long appVehicleId,
                                                            Long shopVehicleId, AppUserCustomerMatchType matchType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomerAndVehicle(session, userNo, customerId, shopId, appVehicleId, shopVehicleId, matchType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomerByAppUserNo(Set<String> appUserNos) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomerByAppUserNo(session, appUserNos);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomerByAppUserNo(Set<String> appUserNos, Long shopId) {
    if (CollectionUtils.isEmpty(appUserNos) || shopId == null) {
      return new ArrayList<AppUserCustomer>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomerByAppUserNo(session, appUserNos, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getCustomerByObdIds(Long shopId, Set<Long> obdIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByObdIds(session, shopId, obdIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getVehicleDTOMapByObdIds(Long shopId, Set<Long> obdIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleDTOMapByObdIds(session, shopId, obdIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<Object[]> getAppUserMapByCustomerIds(Set<Long> customerIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserMapByCustomerIds(session, customerIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomerIds(Long... customerIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomerIds(session, customerIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<Long> getMemberCardShopIdsOfAppUser(String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardShopIdsOfAppUser(session, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUser> getAppUserMapByUserNo(Set<String> appUserNoSet) {
    if (CollectionUtils.isEmpty(appUserNoSet)) {
      return new ArrayList<AppUser>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserMapByUserNo(session, appUserNoSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getMaintainMileageApproachingAppVehicle(Double[] intervals, int start, int limit, int remindTimesLimit) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMaintainMileageApproachingAppVehicle(session, intervals, start, limit, remindTimesLimit);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getMaintainTimeApproachingAppVehicle(Long[] intervals, int start, int limit) {
    Session session = this.getSession();
    try {
      Long currentTime = System.currentTimeMillis();
      Query q = SQL.getMaintainTimeApproachingAppVehicle(session, currentTime + intervals[1], currentTime + intervals[0], start, limit);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getInsuranceTimeApproachingAppVehicle(Long[] intervals, int start, int limit) {
    Session session = this.getSession();
    try {
      Long currentTime = System.currentTimeMillis();
      Query q = SQL.getInsuranceTimeApproachingAppVehicle(session, currentTime + intervals[1], currentTime + intervals[0], start, limit);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getExamineTimeApproachingAppVehicle(Long[] intervals, int start, int limit) {
    Session session = this.getSession();
    try {
      Long currentTime = System.currentTimeMillis();
      Query q = SQL.getExamineTimeApproachingAppVehicle(session, currentTime + intervals[1], currentTime + intervals[0], start, limit);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ServiceCategoryRelation> getServiceCategoryRelationsById(Long shopId, Long dataId, ServiceCategoryDataType dataType) {
    Session session = getSession();
    try {
      Query query = SQL.getServiceCategoryRelationsById(session, shopId, dataId, dataType);
      return query.list();
    } finally {
      release(session);
    }
  }

  public void deleteServiceCategoryRelation(Long shopId, Long dataId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteServiceCategoryRelation(session, shopId, dataId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<VehicleBrandModelRelation> getVehicleBrandModelRelationsById(Long shopId, Long dataId, VehicleBrandModelDataType dataType) {
    Session session = getSession();
    try {
      Query query = SQL.getVehicleBrandModelRelationsById(session, shopId, dataId, dataType);
      return query.list();
    } finally {
      release(session);
    }
  }

  public void deleteVehicleBrandModelRelation(Long shopId, Long dataId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteVehicleBrandModelRelation(session, shopId, dataId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public ClientUserLoginInfo getClientUserLoginInfo(Long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getClientUserLoginInfo(session, userId);
      return (ClientUserLoginInfo) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public int countAppUser() {
    Session session = this.getSession();
    try {
      Query q = SQL.countAppUser(session);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<AppUser> getAppUserByPager(Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByPager(session, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public void updateNextMaintainMileagePushMessageRemindLimit(Set<Long> vehicleIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.updateNextMaintainMileagePushMessageRemindLimit(session, vehicleIds);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public AppVehicle getAppVehicleDetail(String appUserNo, String vehicleVin) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleDetail(session, appUserNo, vehicleVin);
      return (AppVehicle) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getMatchMaintainMileageCustomerVehicle(int pageSize, Double[] intervals, Long startCustomerVehicleId) {
    List<CustomerVehicle> customerVehicles = new ArrayList<CustomerVehicle>();
    if (startCustomerVehicleId == null) {
      return customerVehicles;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getMatchMaintainMileageCustomerVehicle(session, pageSize, intervals, startCustomerVehicleId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getMatchCustomerVehicleByVehicleOBDMileage(int pageSize, Double[] intervals, Long startCustomerVehicleId) {
    List<CustomerVehicle> customerVehicles = new ArrayList<CustomerVehicle>();
    if (startCustomerVehicleId == null) {
      return customerVehicles;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getMatchCustomerVehicleByVehicleOBDMileage(session, pageSize, intervals, startCustomerVehicleId);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<CustomerServiceJob> getOverMaintainMileageCustomerServiceJob(int pageSize, Double[] intervals, Long startCustomerServiceJobId) {
    List<CustomerServiceJob> customerServiceJobs = new ArrayList<CustomerServiceJob>();
    if (startCustomerServiceJobId == null) {
      return customerServiceJobs;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getOverMaintainMileageCustomerServiceJob(session, pageSize, intervals, startCustomerServiceJobId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<CustomerServiceJob> getOverMaintainMileageWithOBDMileage(int pageSize, Double[] intervals, Long startCustomerServiceJobId) {
    List<CustomerServiceJob> customerServiceJobs = new ArrayList<CustomerServiceJob>();
    if (startCustomerServiceJobId == null) {
      return customerServiceJobs;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getOverMaintainMileageWithOBDMileage(session, pageSize, intervals, startCustomerServiceJobId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public boolean isOBDCustomer(Long customerId) {
    boolean result = false;
    Session session = this.getSession();
    try {
      Query query = SQL.isOBDCustomer(session, customerId);
      Object resultObject = query.uniqueResult();
      if (resultObject != null) {
        int resultInt = ((BigInteger) resultObject).intValue();
        result = resultInt > 0;
      }
    } finally {
      release(session);
    }
    return result;
  }

  public List isOBDCustomer(Long[] customerId) {
    List<Map<String, Object>> result = null;
    Session session = this.getSession();
    try {
      Query query = SQL.isOBDCustomer(session, customerId);
      result = query.list();
    } finally {
      release(session);
    }
    return result;
  }

  public List<ObdUserVehicle> getObdUserVehicles(Long... vehicleId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getObdUserVehicles(session, vehicleId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getAppVehicleIdBindingOBDMappingByAppUserNo(String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleIdBindingOBDMappingByAppUserNo(session, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomersByCustomerIds(Long[] customerIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomersByCustomerIds(session, customerIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomersByShopVehicleIds(Set<Long> shopVehicleIds, Set<AppUserCustomerMatchType> matchTypes) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomersByShopVehicleIds(session, shopVehicleIds, matchTypes);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getCustomerIdInAppUserCustomer(String[] appUserNos) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerIdInAppUserCustomer(session, appUserNos);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Customer> getCustomerByAppUserId(Long appUserId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerByAppUserId(session, appUserId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomerByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomerByCustomerId(session, shopId, customerId);
      List<AppUserCustomer> appUserCustomerList = q.list();
      if (CollectionUtil.isNotEmpty(appUserCustomerList)) {
        return appUserCustomerList;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomersByAppUserNoAndAppVehicleId(String appUserNo, Long appVehicleId, AppUserCustomerMatchType matchType) {
    if (StringUtils.isEmpty(appUserNo) || appVehicleId == null || matchType == null) {
      return new ArrayList<AppUserCustomer>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserCustomersByAppUserNoAndAppVehicleId(session, appUserNo, appVehicleId, matchType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByLicenceNo(Long shopId, Set<String> LicenceNoSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByLicenceNo(session, shopId, LicenceNoSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List getCustomerOrSupplierId(String[] customerOrSupplierIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerOrSupplierId(session, customerOrSupplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesMan> getSalesManByDepartmentId(Long shopId, Long departmentId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesManByDepartmentId(session, shopId, departmentId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Department> getDepartmentNameByShopIdName(Long shopId, String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDepartmentNameByShopIdName(session, shopId, name);
      return q.list();
    } finally {
      release(session);
    }
  }

  //根据车牌号+手机号去匹配
  public List<AppUserCustomerDTO> getMatchAppUserCustomerDTOByVehicleNoAndMobile(final Long startAppUserId, final int matchSize) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (startAppUserId != null && matchSize > 0) {
      Session session = this.getSession();
      try {
        Query q = SQL.getMatchAppUserCustomerDTOByVehicleAndMobile(session, startAppUserId, matchSize);
        List<Object[]> list = q.list();
        if (CollectionUtils.isNotEmpty(list)) {
          for (Object[] objects : list) {
            if (!ArrayUtils.isEmpty(objects)) {
              AppUserCustomerDTO appUserCustomerDTO = new AppUserCustomerDTO();
              appUserCustomerDTO.setAppUserNo(StringUtil.valueOf(objects[0]));
              appUserCustomerDTO.setCustomerId(NumberUtil.longValue(objects[1]));
              appUserCustomerDTO.setShopId(NumberUtil.longValue(objects[2]));
              appUserCustomerDTO.setContactId(NumberUtil.longValue(objects[3]));
              AppUserDTO appUserDTO = new AppUserDTO();
              appUserDTO.setId(NumberUtil.longValue(objects[4]));
              appUserCustomerDTO.setAppUserDTO(appUserDTO);
              appUserCustomerDTOs.add(appUserCustomerDTO);
            }
          }
        }
        return appUserCustomerDTOs;
      } finally {
        release(session);
      }
    }
    return appUserCustomerDTOs;
  }

  public List<Member> getEnabledMemberLikeMemberNo(Long shopId, String memberNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getEnabledMemberLikeMemberNo(session, shopId, memberNo);
      return q.list();
    } finally {
      release(session);
    }
  }


  public ShopVersion getShopVersionByName(String shopVersionName) {
    if (StringUtils.isEmpty(shopVersionName)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getShopVersionByName(session, shopVersionName);
      List<ShopVersion> shopVersions = q.list();
      if (CollectionUtil.isNotEmpty(shopVersions)) {
        return shopVersions.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<AppVehicleFaultInfo> getAppVehicleFaultInfo(String userNo, Long vehicleId, Set<String> codes,
                                                          Set<ErrorCodeTreatStatus> statuses) {
    if (StringUtils.isEmpty(userNo) || CollectionUtils.isEmpty(codes)) {
      return new ArrayList<AppVehicleFaultInfo>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleFaultInfo(session, userNo, vehicleId, codes, statuses);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicleFaultInfo> getAppVehicleFaultInfo(String userNo, Long vehicleId, String code, ErrorCodeTreatStatus status) {
    if (StringUtils.isEmpty(userNo) || vehicleId == null || StringUtils.isEmpty(code) || status == null) {
      return new ArrayList<AppVehicleFaultInfo>();
    }
    Set<String> codes = new HashSet<String>();
    codes.add(code);
    Set<ErrorCodeTreatStatus> statuses = new HashSet<ErrorCodeTreatStatus>();
    statuses.add(status);
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleFaultInfo(session, userNo, vehicleId, codes, statuses);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicleFaultInfo> getAppVehicleFaultInfoByIds(Set<Long> appVehicleFaultInfoIds, String appUserNo) {
    if (CollectionUtils.isEmpty(appVehicleFaultInfoIds) || StringUtils.isEmpty(appUserNo)) {
      return new ArrayList<AppVehicleFaultInfo>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleFaultInfoByIds(session, appVehicleFaultInfoIds, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicleFaultInfo> searchAppVehicleFaultInfoList(String appUserNo, Long defaultAppVehicleId, Pager pager,
                                                                 ErrorCodeTreatStatus[] status) {
    if (StringUtils.isEmpty(appUserNo) || pager == null) {
      return new ArrayList<AppVehicleFaultInfo>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.searchAppVehicleFaultInfoList(session, appUserNo, defaultAppVehicleId, pager, status);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countAppVehicleFaultInfoList(String appUserNo, Long defaultAppVehicleId, ErrorCodeTreatStatus[] status) {
    if (StringUtils.isEmpty(appUserNo)) {
      return 0;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.countAppVehicleFaultInfoList(session, appUserNo, defaultAppVehicleId, status);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<AppUserShopVehicle> getAppUserShopVehicle(String appUserNo, Long vehicleId, Long shopId) {
    if (StringUtil.isEmpty(appUserNo)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserShopVehicle(session, appUserNo, vehicleId, shopId, null, AppUserShopVehicleStatus.BUNDLING);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUserShopVehicle> getAppUserShopVehicle(Long vehicleId, Long obdId, AppUserShopVehicleStatus status) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserShopVehicle(session, null, vehicleId, null, obdId, status);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ObdUserVehicle> getOBDUserVehicle(Set<Long> obdIds) {
    if (CollectionUtils.isEmpty(obdIds)) {
      return new ArrayList<ObdUserVehicle>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getOBDUserVehicle(session, obdIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ObdUserVehicle> getObdUserVehicle(Long vehicleId, Long obdId, ObdUserVehicleStatus status) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdUserVehicle(session, vehicleId, obdId, status);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ObdUserVehicle> getOBDUserVehicleByObdIds(Set<Long> obdIds) {
    if (CollectionUtils.isEmpty(obdIds)) {
      return new ArrayList<ObdUserVehicle>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getOBDUserVehicleByObdIds(session, obdIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getOBDUserVehicleByObdImeis(Set<String> imeis) {
    if (CollectionUtils.isEmpty(imeis)) {
      return new ArrayList<Object[]>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getOBDUserVehicleByImeis(session, imeis);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, Long> getOBDIdSellShopMap() {
    Map<Long, Long> map = new HashMap<Long, Long>();
    Session session = this.getSession();
    try {
      Query q = SQL.getAllOBD(session);
      List<OBD> list = q.list();
      for (OBD obd : list) {
        map.put(obd.getId(), obd.getSellShopId());
      }
    } finally {
      release(session);
    }
    return map;
  }

  //根据车牌号去匹配
  public List<AppUserCustomerDTO> getMatchAppUserCustomerDTOByVehicleNo(final Long startAppUserId, final Long assignAppUserId, final int matchSize, final List<Long> shopVersionIds) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (startAppUserId != null && matchSize > 0) {
      Session session = this.getSession();
      try {
        Query q = SQL.getMatchAppUserCustomerDTOByVehicle(session, startAppUserId, assignAppUserId, matchSize, shopVersionIds);
        List<Object[]> list = q.list();
        if (CollectionUtils.isNotEmpty(list)) {
          for (Object[] objects : list) {
            if (!ArrayUtils.isEmpty(objects)) {
              AppUserCustomerDTO appUserCustomerDTO = new AppUserCustomerDTO();
              appUserCustomerDTO.setAppUserNo(StringUtil.valueOf(objects[0]));
              appUserCustomerDTO.setCustomerId(NumberUtil.longValue(objects[1]));
              appUserCustomerDTO.setShopId(NumberUtil.longValue(objects[2]));
              AppUserDTO appUserDTO = new AppUserDTO();
              appUserDTO.setId(NumberUtil.longValue(objects[3]));
              appUserCustomerDTO.setAppVehicleId(NumberUtil.longValue(objects[4]));
              appUserCustomerDTO.setShopVehicleId(NumberUtil.longValue(objects[5]));
              appUserCustomerDTO.setAppUserDTO(appUserDTO);
              appUserCustomerDTO.setIsVehicleNoMatch(YesNo.YES);
              appUserCustomerDTO.setIsMobileMatch(YesNo.NO);
              appUserCustomerDTO.setMatchType(AppUserCustomerMatchType.VEHICLE_MATCH);
              appUserCustomerDTO.setMatchTime(System.currentTimeMillis());
              appUserCustomerDTOs.add(appUserCustomerDTO);
            }
          }
        }
        return appUserCustomerDTOs;
      } finally {
        release(session);
      }
    }
    return appUserCustomerDTOs;
  }

  //根据手机号去匹配
  public List<AppUserCustomerDTO> getMatchAppUserCustomerDTOByMobile(final Long startAppUserId, final Long assignAppUserId,
                                                                     final int matchSize, final List<Long> shopVersionIds) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (startAppUserId != null && matchSize > 0) {
      Session session = this.getSession();
      try {
        Query q = SQL.getMatchAppUserCustomerDTOByMobile(session, startAppUserId, assignAppUserId, matchSize, shopVersionIds);
        List<Object[]> list = q.list();
        if (CollectionUtils.isNotEmpty(list)) {
          for (Object[] objects : list) {
            if (!ArrayUtils.isEmpty(objects)) {
              AppUserCustomerDTO appUserCustomerDTO = new AppUserCustomerDTO();
              appUserCustomerDTO.setAppUserNo(StringUtil.valueOf(objects[0]));
              appUserCustomerDTO.setCustomerId(NumberUtil.longValue(objects[1]));
              appUserCustomerDTO.setShopId(NumberUtil.longValue(objects[2]));
              appUserCustomerDTO.setContactId(NumberUtil.longValue(objects[3]));
              AppUserDTO appUserDTO = new AppUserDTO();
              appUserDTO.setId(NumberUtil.longValue(objects[4]));
              appUserCustomerDTO.setAppUserDTO(appUserDTO);
              appUserCustomerDTO.setMatchTime(System.currentTimeMillis());
              appUserCustomerDTO.setMatchType(AppUserCustomerMatchType.CONTACT_MOBILE_MATCH);
              appUserCustomerDTO.setIsMobileMatch(YesNo.YES);
              appUserCustomerDTO.setIsVehicleNoMatch(YesNo.NO);
              appUserCustomerDTOs.add(appUserCustomerDTO);
            }
          }
        }
        return appUserCustomerDTOs;
      } finally {
        release(session);
      }
    }
    return appUserCustomerDTOs;
  }

  //根据车主手机号去匹配
  public List<AppUserCustomerDTO> getMatchAppUserCustomerDTOByVehicleMobile(final Long startAppUserId, final Long assignAppUserId,
                                                                            final int matchSize, final List<Long> shopVersionIds) {
    List<AppUserCustomerDTO> appUserCustomerDTOs = new ArrayList<AppUserCustomerDTO>();
    if (startAppUserId != null && matchSize > 0) {
      Session session = this.getSession();
      try {
        Query q = SQL.getMatchAppUserCustomerDTOByVehicleMobile(session, startAppUserId, assignAppUserId, matchSize, shopVersionIds);
        List<Object[]> list = q.list();
        if (CollectionUtils.isNotEmpty(list)) {
          for (Object[] objects : list) {
            if (!ArrayUtils.isEmpty(objects)) {
              AppUserCustomerDTO appUserCustomerDTO = new AppUserCustomerDTO();
              appUserCustomerDTO.setAppUserNo(StringUtil.valueOf(objects[0]));
              appUserCustomerDTO.setCustomerId(NumberUtil.longValue(objects[1]));
              appUserCustomerDTO.setShopId(NumberUtil.longValue(objects[2]));
              AppUserDTO appUserDTO = new AppUserDTO();
              appUserDTO.setId(NumberUtil.longValue(objects[3]));
//               appUserCustomerDTO.setAppVehicleId(NumberUtil.longValue(objects[4]));
              appUserCustomerDTO.setShopVehicleId(NumberUtil.longValue(objects[4]));
              appUserCustomerDTO.setAppUserDTO(appUserDTO);
              appUserCustomerDTO.setIsMobileMatch(YesNo.YES);
              appUserCustomerDTO.setIsVehicleNoMatch(YesNo.NO);
              appUserCustomerDTO.setMatchType(AppUserCustomerMatchType.VEHICLE_MATCH);
              appUserCustomerDTO.setMatchTime(System.currentTimeMillis());
              appUserCustomerDTOs.add(appUserCustomerDTO);
            }
          }
        }
        return appUserCustomerDTOs;
      } finally {
        release(session);
      }
    }
    return appUserCustomerDTOs;
  }

  public Set<Long> filterMatchVehicleCustomerIds(Set<String> vehicleNos, Set<Long> customerIds, Long shopId) {
    Set<Long> matchedCustomerIdSet = new HashSet<Long>();
    Session session = this.getSession();
    try {
      Query q = SQL.filterMatchVehicleCustomerIds(session, vehicleNos, customerIds, shopId);
      List<Long> matchedCustomerIdList = q.list();
      if (CollectionUtils.isNotEmpty(matchedCustomerIdList)) {
        for (Long customerId : matchedCustomerIdList) {
          if (customerId != null) {
            matchedCustomerIdSet.add(customerId);
          }
        }
      }
      return matchedCustomerIdSet;
    } finally {
      release(session);
    }
  }

  public List isAppUserByCustomerId(List<Long> customerIdList) {
    Session session = this.getSession();
    try {
      Query query = SQL.isAppUserByCustomerId(session, customerIdList);
      List list = query.list();
      return list;
    } finally {
      release(session);
    }
  }

  public List<MemberService> getVehicleAvailableMemberServicesByLicenceNo(Long shopId, String licenceNo) {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleAvailableMemberServicesByLicenceNo(session, shopId, licenceNo);
      return query.list();
    } catch (ParseException e) {
      LOG.error(e.getMessage());
    } finally {
      release(session);
    }
    return null;
  }

  public List<Member> getMembersByMemberIds(Long shopId, Set<Long> memberIdSet) {
    Session session = this.getSession();
    try {
      Query query = SQL.getMembersByMemberIds(session, shopId, memberIdSet);
      return query.list();
    } finally {
      release(session);
    }
  }

  public DriveLog getDriveLogByAppId(String appUserNo, String appDriveLogId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogByAppId(session, appUserNo, appDriveLogId);
      List<DriveLog> list = query.list();
      return CollectionUtil.getFirst(list);
    } finally {
      release(session);
    }
  }

  public List<String> getAppUserMobileByContactMobile(Long shopId, String mobile) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAppUserMobileByContactMobile(session, shopId, mobile);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLogDTO> getDriveLogContents(String appUserNo, Long startTime, Long endTime) {
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogContents(session, appUserNo, startTime, endTime);
      List<Object[]> result = query.list();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] obj : result) {
          DriveLogDTO driveLogDTO = new DriveLogDTO();
          driveLogDTO.setId((Long) obj[0]);
          driveLogDTO.setAppUserNo((String) obj[1]);
          driveLogDTO.setAppDriveLogId((String) obj[2]);
          driveLogDTO.setLastUpdateTime((Long) obj[3]);
          driveLogDTO.setStatus(DriveLogStatus.parseValue((String) obj[4]));
          driveLogDTOs.add(driveLogDTO);
        }
      }
    } finally {
      release(session);
    }
    return driveLogDTOs;
  }

  public List<DriveLog> getDriveLogByStartTime(String appUserNo, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogByStartTime(session, appUserNo, startTime, endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLog> getDriveLogByStartTime_wx(String appUserNo, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogByStartTime_wx(session, appUserNo, startTime, endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLog> getLastDriveLog(String appUserNo, int limit) {
    Session session = this.getSession();
    try {
      Query query = SQL.getLastDriveLog(session, appUserNo, limit);
      return query.list();
    } finally {
      release(session);
    }
  }

  public DriveLog getDriveLogById(Long driveLogId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogById(session, driveLogId);
      List<DriveLog> list = query.list();
      return CollectionUtil.getFirst(list);
    } finally {
      release(session);
    }
  }

  public DriveLog getDriveLogByAppUserNoAndId(String appUserNo, Long driveLogId) {
    if (StringUtils.isEmpty(appUserNo) || driveLogId == null) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogByAppUserNoAndId(session, appUserNo, driveLogId);
      List<DriveLog> list = query.list();
      return CollectionUtil.getFirst(list);
    } finally {
      release(session);
    }
  }

  public DriveLogPlaceNote getDriveLogPlaceNoteByLogId(String appUserNo, Long driveLogId) {
    if (StringUtils.isEmpty(appUserNo) || driveLogId == null) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogPlaceNoteByLogId(session, appUserNo, driveLogId);
      List<DriveLogPlaceNote> list = query.list();
      return CollectionUtil.getFirst(list);
    } finally {
      release(session);
    }
  }

  public List<AppUserConfig> getAppUserConfigByAppUserNo(String appUserNo) {
    if (StringUtils.isEmpty(appUserNo)) {
      return new ArrayList<AppUserConfig>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getAppUserConfigByAppUserNo(session, appUserNo);
      return query.list();
    } finally {
      release(session);
    }
  }

  public AppUserConfig getAppUserConfigByName(String appUserNo, String name) {
    if (StringUtils.isEmpty(appUserNo) || StringUtils.isEmpty(name)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getAppUserConfigByName(session, appUserNo, name);
      List<AppUserConfig> appUserConfigs = query.list();
      return CollectionUtil.getFirst(appUserConfigs);
    } finally {
      release(session);
    }
  }

  public List<DriveLog> getDriveLogByIds(String appUserNo, Set<Long> driveLogIds) {
    if (StringUtils.isEmpty(appUserNo) || CollectionUtils.isEmpty(driveLogIds)) {
      return new ArrayList<DriveLog>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogByIds(session, appUserNo, driveLogIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLogPlaceNote> getDriveLogPlaceNoteByLogIds(String appUserNo, Set<Long> driveLogIds) {
    if (StringUtils.isEmpty(appUserNo) && CollectionUtils.isEmpty(driveLogIds)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogPlaceNoteByLogIds(session, appUserNo, driveLogIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, Boolean> checkIsAppVehicle(Long... vehicleIds) {
    Map<Long, Boolean> isAppVehicleMap = new HashMap<Long, Boolean>();
    if (ArrayUtils.isEmpty(vehicleIds)) {
      return isAppVehicleMap;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.checkIsAppVehicle(session, vehicleIds);
      List<Object[]> list = query.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] objects : list) {
          Long vehicleId = (Long) objects[0];
          if (ArrayUtils.contains(vehicleIds, vehicleId)) {
            isAppVehicleMap.put(vehicleId, (Long) objects[1] > 0);
          } else {
            isAppVehicleMap.put(vehicleId, false);
          }
        }
      }
      return isAppVehicleMap;
    } finally {
      release(session);
    }
  }

  public List<User> getUserBySalesManId(Long shopId, Long salesManId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getUserBySalesManId(session, shopId, salesManId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByGsmObdImei(String gsmObdImei) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByGsmObdImei(session, gsmObdImei);
      return (List<Vehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehiclesByGsmObdImeis(Set<String> imeis) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByGsmObdImeis(session, imeis);
      return (List<Vehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public OBD getObdByImeiObdType(String imei, ObdType obdType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdByObdImei(session, imei, obdType);
      return (OBD) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<OBD> getObdByImeisObdType(Set<String> imeis, ObdType obdType) {
    if (CollectionUtils.isEmpty(imeis) && obdType == null) {
      return new ArrayList<OBD>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getObdByImeisObdType(session, imeis, obdType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Map<String, OBD> getimeiObdMapByImeisObdType(Set<String> imeis, ObdType obdType) {
    if (CollectionUtils.isEmpty(imeis) && obdType == null) {
      return new HashMap<String, OBD>();
    }
    Map<String, OBD> imeiOBDMap = new HashMap<String, OBD>();
    List<OBD> obds = getObdByImeisObdType(imeis, obdType);
    if (CollectionUtils.isNotEmpty(obds)) {
      for (OBD obd : obds) {
        if (obd != null && StringUtils.isNotBlank(obd.getImei())) {
          imeiOBDMap.put(obd.getImei(), obd);
        }
      }
    }
    return imeiOBDMap;
  }


  public List<AppUser> getAppUserByUserType(String mobile ) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByUserType(session, mobile);
      return q.list();
    } finally {
      release(session);
    }
  }

  public AppUser getAppUserByImei(String imei, AppUserType userType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByImei(session, imei, userType);
      return (AppUser) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AppUser> getAppUserByUserType(AppUserType appUserType, int start, int limit) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByUserType(session, appUserType, start, limit);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLog> getDriveLogByStatusOrderByEndTimeAsc(String appUserNo, DriveLogStatus status, int limit) {
    if (StringUtils.isEmpty(appUserNo) || status == null || limit <= 0) {
      return new ArrayList<DriveLog>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogByStatusOrderByEndTimeAsc(session, appUserNo, status, limit);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLog> getDriveLogsByStatusAndStatStatus(String appUserNo, Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus) {
    if (StringUtils.isEmpty(appUserNo) || CollectionUtils.isEmpty(driveLogStatus) || statStatus == null) {
      return new ArrayList<DriveLog>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogsByStatusAndStatStatus(session, appUserNo, driveLogStatus, statStatus);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLog> getDriveLogsByStatusAndStatStatus(Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus, int limit) {
    if (CollectionUtils.isEmpty(driveLogStatus) || statStatus == null || limit <= 0) {
      return new ArrayList<DriveLog>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogsByStatusAndStatStatus(session, driveLogStatus, statStatus, limit);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveLog> getDriveLogByAppUserNosAndStatusOrderByEndTimeAsc(Set<String> appUserNos, DriveLogStatus status) {
    if (CollectionUtils.isEmpty(appUserNos) || status == null) {
      return new ArrayList<DriveLog>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogByAppUserNosAndStatusOrderByEndTimeAsc(session, appUserNos, status);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByGsmObdImei(Long vehicleId, String gsmObdImei) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByGsmObdImei(session, vehicleId, gsmObdImei);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<AppUser> getAppUserByDeviceToken(String deviceToken) {
    if (StringUtils.isEmpty(deviceToken)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByDeviceToken(session, deviceToken);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppUser> getAppUserByUMDeviceToken(String umDeviceToken) {
    if (StringUtils.isEmpty(umDeviceToken)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppUserByUMDeviceToken(session, umDeviceToken);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<User> getUsersByDeviceToken(String deviceToken) {
    if (StringUtils.isEmpty(deviceToken)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByDeviceToken(session, deviceToken);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<User> getUsersByUMDeviceToken(String umDeviceToken) {
    if (StringUtils.isEmpty(umDeviceToken)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getUserByUMDeviceToken(session, umDeviceToken);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<String> getAppUserNoByVehicleId(Long... vehicleIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAppUserNoByVehicleId(session, vehicleIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<DriveStat> getDriveStatsByStatDate(String appUserNo, Long startTime, Long endTime) {
    if (StringUtils.isEmpty(appUserNo) || startTime == null || endTime == null) {
      return new ArrayList<DriveStat>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveStatsByStatDate(session, appUserNo, startTime, endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public DriveStat getDriveStatByYearAndMonth(String appUserNo, int year, int month) {
    if (StringUtils.isNotBlank(appUserNo) && year > 0 && month > 0) {
      Session session = this.getSession();
      try {
        Query query = SQL.getDriveStatByYearAndMonth(session, appUserNo, year, month);
        return CollectionUtil.getFirst((List<DriveStat>) query.list());
      } finally {
        release(session);
      }
    }
    return null;
  }

  public List<DriveLog> getDriveLogDTOsByImeiTime(String imei, Long startTime, Long endTime, Pager pager) {
    Session session = this.getSession();
    try {
      Query query = SQL.getDriveLogDTOsByImeiTime(session, imei, startTime, endTime, pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countDriveLogDTOsByImeiTime(String imei, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.countDriveLogDTOsByImeiTime(session, imei, startTime, endTime);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<ObdSimBindDTO> searchObdSimBindDTOsByAdmin(ObdSimSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchObdSimBindDTOByAdmin(session, condition);
      List<Object[]> results = query.list();
      List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
      if (CollectionUtils.isNotEmpty(results)) {
        for (Object[] objects : results) {
          ObdSimBindDTO obdSimBindDTO = new ObdSimBindDTO();
          obdSimBindDTO.setObdSimType(OBDSimType.convertObdSimType(StringUtil.valueOf(objects[1])));
          obdSimBindDTO.setObdStatus(OBDStatus.convertOBDStatus(StringUtil.valueOf(objects[2])));
          obdSimBindDTO.setObdStatusStr(obdSimBindDTO.getObdStatus() != null ? obdSimBindDTO.getObdStatus().getName() : "");
          obdSimBindDTO.setImei(StringUtil.valueOf(objects[3]));
          obdSimBindDTO.setObdVersion(StringUtil.valueOf(objects[4]));
          obdSimBindDTO.setSpec(StringUtil.valueOf(objects[5]));
          obdSimBindDTO.setColor(StringUtil.valueOf(objects[6]));
          obdSimBindDTO.setPack(StringUtil.valueOf(objects[7]));
          obdSimBindDTO.setOpenCrash(YesNo.convertYesNo(StringUtil.valueOf(objects[8])));
          obdSimBindDTO.setOpenShake(YesNo.convertYesNo(StringUtil.valueOf(objects[9])));
          obdSimBindDTO.setSimNo(StringUtil.valueOf(objects[10]));
          obdSimBindDTO.setMobile(StringUtil.valueOf(objects[11]));
          obdSimBindDTO.setUseDate(StringUtils.isBlank(StringUtil.valueOf(objects[12])) ? null : NumberUtil.longValue(objects[12]));
          if (obdSimBindDTO.getUseDate() != null) {
            obdSimBindDTO.setUseDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_YEAR_MON, obdSimBindDTO.getUseDate()));
          }
          obdSimBindDTO.setUsePeriod(StringUtils.isBlank(StringUtil.valueOf(objects[13])) ? null : NumberUtil.intValue(objects[13]));
          obdSimBindDTO.setOwnerName(StringUtil.valueOf(objects[14]));
          Long obdId = objects[15] != null && StringUtils.isNotBlank(objects[15].toString()) ? NumberUtil.longValue(objects[15]) : null;
          obdSimBindDTO.setObdId(obdId);
          Long simId = objects[16] != null && StringUtils.isNotBlank(objects[16].toString()) ? NumberUtil.longValue(objects[16]) : null;
          obdSimBindDTO.setSimId(simId);
          obdSimBindDTOs.add(obdSimBindDTO);
        }
      }
      return obdSimBindDTOs;
    } finally {
      release(session);
    }
  }

  public int countObdSimBindDTOsByAdmin(ObdSimSearchCondition condition) {
    Session session = this.getSession();
    try {
      if (StringUtils.isNotEmpty(condition.getImei()) && ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.SINGLE_SIM)) {
        return 0;
      } else {
        Query query = SQL.countObdSimBindDTOsByAdmin(session, condition);
        return NumberUtil.intValue(query.uniqueResult());
      }
    } finally {
      release(session);
    }
  }

  public ObdSimBindDTO getObdSimBindByShopExact(String imei, String mobile) throws BcgogoException {
    Session session = this.getSession();
    try {
      Query query = SQL.getObdSimBindByShopExact(session, imei, mobile);
      List list = query.list();
      if (CollectionUtil.isEmpty(list)) {
        return null;
      }
      if (list.size() > 1) {
        LOG.error("obd info error,no unique,imei={},mobile={}", imei, mobile);
        throw new BcgogoException(BcgogoExceptionType.OBDNotUnique);
      }
      Object obj = CollectionUtil.getFirst(list);
      Object[] result = (Object[]) obj;
      ObdSimBindDTO obdSimBindDTO = new ObdSimBindDTO();
      obdSimBindDTO.setMobile(StringUtil.valueOf(result[0]));
      obdSimBindDTO.setImei(StringUtil.valueOf(result[1]));
      obdSimBindDTO.setSellShopId(NumberUtil.longValue(result[2]));
      obdSimBindDTO.setVehicleId(NumberUtil.longValue(result[3]));
      obdSimBindDTO.setLicenceNo(StringUtil.valueOf(result[4]));
      obdSimBindDTO.setObdId(NumberUtil.longValue(result[5]));
      obdSimBindDTO.setSimId(NumberUtil.longValue(result[6]));
      obdSimBindDTO.setAppVehicleId(NumberUtil.longValue(result[7]));
      obdSimBindDTO.setObdType(ObdType.getObdTypeByStr(StringUtil.valueOf(result[8])));
      return obdSimBindDTO;
    } finally {
      release(session);
    }
  }

  public List<ObdSimBindDTO> getObdSimBindDTOByShop(ObdSimSearchCondition condition) throws ParseException {
    Session session = this.getSession();
    try {
      Query query = SQL.getObdSimBindByShop(session, condition);
      List<Object[]> results = query.list();
      List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
      if (CollectionUtils.isNotEmpty(results)) {
        for (Object[] object : results) {
          ObdSimBindDTO obdSimBindDTO = new ObdSimBindDTO();
          obdSimBindDTO.setStorageTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT, NumberUtil.longValue(object[0])));
          obdSimBindDTO.setMobile(StringUtil.valueOf(object[1]));
          obdSimBindDTO.setImei(StringUtil.valueOf(object[2]));
          obdSimBindDTO.setUseDate(NumberUtil.longValue(object[3]));
          obdSimBindDTO.setUseDateStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT, obdSimBindDTO.getUseDate()));
          obdSimBindDTO.setUsePeriod(NumberUtil.intValue(object[4]));
          obdSimBindDTO.setSellTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT, NumberUtil.longValue(object[5])));
          OBDStatus obdStatus = OBDStatus.convertOBDStatus(StringUtil.valueOf(object[6]));
          if (null != obdStatus) {
            obdSimBindDTO.setObdStatus(obdStatus);
            obdSimBindDTO.setObdStatusStr(obdStatus.getName());
          }
          obdSimBindDTO.setObdId(NumberUtil.longValue(object[7]));
          obdSimBindDTO.setCustomerId(NumberUtil.longValue(object[8]));
          obdSimBindDTO.setCustomerName(StringUtil.valueOf(object[9]));
          obdSimBindDTO.setCustomerMobile(StringUtil.valueOf(object[10]));
          obdSimBindDTO.setVehicleId(NumberUtil.longValue(object[11]));
          obdSimBindDTO.setLicenceNo(StringUtil.valueOf(object[12]));
          obdSimBindDTO.setVehicleModel(StringUtil.valueOf(object[13]));
          obdSimBindDTO.setVehicleBrand(StringUtil.valueOf(object[14]));
          obdSimBindDTO.setSellShopId(NumberUtil.longValue(object[15]));
          obdSimBindDTO.setAppVehicleId(NumberUtil.longValue(object[16]));
          //区别OBD和后视镜
          String obdSimTypeStr=StringUtil.valueOf(object[17]);
          if ("COMBINE_MIRROR_OBD_SIM".equals(obdSimTypeStr) || "SINGLE_MIRROR_OBD".equals(obdSimTypeStr)) {
            obdSimBindDTO.setObdSimType(OBDSimType.COMBINE_MIRROR_OBD_SIM);
          }else if ("COMBINE_GSM_OBD_SSIM".equals(obdSimTypeStr) || "SINGLE_GSM_SOBD".equals(obdSimTypeStr)) {
            obdSimBindDTO.setObdSimType(OBDSimType.COMBINE_GSM_OBD_SSIM);
          }else {
            obdSimBindDTO.setObdSimType(OBDSimType.COMBINE_GSM_OBD_SIM);
          }
          obdSimBindDTOs.add(obdSimBindDTO);
        }
      }
      return obdSimBindDTOs;
    } finally {
      release(session);
    }
  }

  public int countObdSimBindByShop(ObdSimSearchCondition condition) throws ParseException {
    Session session = this.getSession();
    try {
      Query query = SQL.countObdSimBindByShop(session, condition);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }


  public List<ObdSim> getObdSimByMobiles(Set<String> mobileSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimByMobiles(session, mobileSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ObdSim> getObdSimByIds(Set<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return new ArrayList<ObdSim>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimByIds(session, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, ObdSim> getIdObdSimMapByIds(Set<Long> ids) {
    Map<Long, ObdSim> idObdSimMap = new HashMap<Long, ObdSim>();
    List<ObdSim> obdSims = getObdSimByIds(ids);
    if (CollectionUtils.isNotEmpty(obdSims)) {
      for (ObdSim obdSim : obdSims) {
        if (obdSim != null && obdSim.getId() != null) {
          idObdSimMap.put(obdSim.getId(), obdSim);
        }
      }
    }
    return idObdSimMap;
  }

  public ObdSim getObdSimByMobile(String mobile) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimByMobile(session, mobile);
      return (ObdSim) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<ObdSim> getObdSimBySimNos(Set<String> simNoSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimBySimNos(session, simNoSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public ObdHistory getLastObdHistoryByObdId(Long id) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastObdHistoryByObdId(session, id);
      return (ObdHistory) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public ObdSimHistory getLastObdSimHistoryBySimId(Long id) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastObdSimHistoryBySimId(session, id);
      return (ObdSimHistory) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<OBDSimOperationLog> getObdSimOperationLogs(OBDSimOperationLogDTOSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimOperationLogs(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countObdSimOperationLogs(OBDSimOperationLogDTOSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countObdSimOperationLogs(session, condition);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public ObdSimBind getObdSimBindByObdIdAndSimId(Long obdId, Long simId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimBindByObdIdAndSimId(session, obdId, simId);
      return (ObdSimBind) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<ObdSimBind> getObdSimBindsByObdIds(Set<Long> obdIds) {
    if (CollectionUtils.isEmpty(obdIds)) {
      return new ArrayList<ObdSimBind>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimBindsByObdIds(session, obdIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public ObdSimBind getObdSimBindsByObdId(Long obdId) {
    if (obdId == null) {
      return null;
    }
    Set<Long> obdIds = new HashSet<Long>();
    obdIds.add(obdId);
    return CollectionUtil.getFirst(getObdSimBindsByObdIds(obdIds));
  }


  public Map<Long, ObdSimBind> getObdSimBindMapByObdIds(Set<Long> obdIds) {
    Map<Long, ObdSimBind> obdSimBindMap = new HashMap<Long, ObdSimBind>();
    if (CollectionUtils.isEmpty(obdIds)) {
      return obdSimBindMap;
    }
    List<ObdSimBind> obdSimBinds = getObdSimBindsByObdIds(obdIds);
    if (CollectionUtils.isNotEmpty(obdSimBinds)) {
      for (ObdSimBind obdSimBind : obdSimBinds) {
        obdSimBindMap.put(obdSimBind.getObdId(), obdSimBind);
      }
    }
    return obdSimBindMap;
  }


  public List<String> getObdImeiSuggestion(ObdImeiSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdImeiSuggestion(session, suggestion);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countObdImeiSuggestion(ObdImeiSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.countObdImeiSuggestion(session, suggestion);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public int countObdSimMobileSuggestion(SimMobileSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.countObdSimMobileSuggestion(session, suggestion);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<String> getObdSimMobileSuggestion(SimMobileSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdSimMobileSuggestion(session, suggestion);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countObdVersionSuggestion(ObdVersionSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.countObdVersionSuggestion(session, suggestion);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<String> getObdVersionSuggestion(ObdVersionSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.getObdVersionSuggestion(session, suggestion);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countAgentNameSuggestion(AgentNameSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.countAgentNameSuggestion(session, suggestion);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<User> getAgentNameSuggestion(AgentNameSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAgentNameSuggestion(session, suggestion);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countStaffNameSuggestion(AgentNameSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.countStaffNameSuggestion(session, suggestion);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<User> getStaffNameSuggestion(AgentNameSuggestion suggestion) {
    Session session = this.getSession();
    try {
      Query q = SQL.getStaffNameSuggestion(session, suggestion);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WXUser> getWXUserByPager(WXAccountType accountType, Pager pager) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getWXUserByPager(session, accountType, pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<WXUser> getWXUserByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getWXUserByShopId(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<WXUser> getWXUserDTOByPublicNo(String publicNo) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getWXUserDTOByPublicNo(session, publicNo);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<WXUser> getWXUserByVehicleNo(String vehicleNo) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getWXUserByVehicleNo(session, vehicleNo);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<WXUser> getWXUserByOpenId(String... openIds) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXUserByOpenId(session, openIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public int countWXUser(WXAccountType accountType) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.countWXUser(session, accountType);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }


  public List<WXAccount> getAllWXAccount() {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getAllWXAccount(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WXAccount> getWXAccountByCondition(WXShopAccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXAccountByCondition(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WXAccount> getWXAccount(WXShopAccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXAccount(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WXShopAccount> getWXShopAccount(WXShopAccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXShopAccount(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WXShopAccount> getWXShopAccount(Long shopId, Long accountId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXShopAccount(session, shopId, accountId);
      return q.list();
    } finally {
      release(session);
    }
  }


  public WXAccount getWXAccountByOpenId(String openId) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getWXAccountByOpenId(session, openId);
      return (WXAccount) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public int countWXAccount(WXShopAccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.countWXAccount(session, condition);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public int countWXShopAccount(WXShopAccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.countWXShopAccount(session, condition);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public WXAccount getWXAccountByPublicNo(String publicNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXAccountByPublicNo(session, publicNo);
      return (WXAccount) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public WXKWTemplate getWXKWTemplate(String publicNo, String title) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXKWTemplate(session, publicNo, title);
      return (WXKWTemplate) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<ShopWXUser> getShopWXUserByOpenId(String openId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getShopWXUserByOpenId(session, openId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopWXUser> getShopWXUserByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getShopWXUserByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public ShopWXUser getShopWXUser(Long shopId, String openId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getShopWXUser(session, shopId, openId);
      return (ShopWXUser) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public Object[] getShopWXUserInfo(String openId, String publicNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getShopWXUserInfo(session, openId, publicNo);
      return (Object[]) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<WXQRCode> getWXQRCode(WXQRCodeSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXQRCode(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public WXQRCode getWXQRCodeByShopId(String publicNo, Long shopId, QRScene scene) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXQRCodeDTOByShopId(session, publicNo, shopId, scene);
      Object object = CollectionUtil.getFirst(q.list());
      return (WXQRCode) object;
    } finally {
      release(session);
    }
  }

  public WXQRCode getUnExpireWXQRCode(String publicNo, String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getUnExpireWXQRCode(session, publicNo, appUserNo);
      Object object = CollectionUtil.getFirst(q.list());
      return (WXQRCode) object;
    } finally {
      release(session);
    }
  }

  public WXQRCode getUnExpireWXQRCodeDTOByShopId(Long shopId, QRScene scene) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getUnExpireWXQRCodeDTOByShopId(session, shopId, scene);
      Object object = CollectionUtil.getFirst(q.list());
      return (WXQRCode) object;
    } finally {
      release(session);
    }
  }

  public AppUserWXQRCode getAppUserWXQRCode(String publicNo, String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getAppUserWXQRCode(session, publicNo, appUserNo);
      Object object = CollectionUtil.getFirst(q.list());
      return (AppUserWXQRCode) object;
    } finally {
      release(session);
    }
  }

  public List<AccidentSpecialist> getAccidentSpecialistByOpenId(Long shopId, String openId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getAccidentSpecialistByOpenId(session, shopId, openId);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<AppWXUser> getAppWXUser(String appUserNo, String openId) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getAppWXUser(session, appUserNo, openId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public AppUserWXQRCode getAppUserWXQRCodeDTOBySceneId(Long sceneId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getAppUserWXQRCodeDTOBySceneId(session, sceneId);
      Object object = CollectionUtil.getFirst(q.list());
      return (AppUserWXQRCode) object;
    } finally {
      release(session);
    }
  }

  public WXQRCode getUnAssignedWXQRCode(String publicNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getUnAssignedWXQRCode(session, publicNo);
      Object object = CollectionUtil.getFirst(q.list());
      return (WXQRCode) object;
    } finally {
      release(session);
    }
  }

  public int getWXQRCodeMaxScene(String publicNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXQRCodeMaxScene(session, publicNo);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<Object[]> getWXAccountStat() {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getWXAccountStat(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getWXUserGrowth() {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getWXUserGrowth(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<WXUserVehicle> getWXUserVehicle(String openId, String vehicleNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXUserVehicle(session, openId, vehicleNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WXUserVehicle> getWXUserVehicleByOpenId(String... openId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXUserVehicleByOpenId(session, openId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public WXUserVehicle getWXUserVehicleById(Long userVehicleId) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXUserVehicleById(session, userVehicleId);
      return (WXUserVehicle) q.uniqueResult();
    } finally {
      release(session);
    }
  }


  public EvaluateRecord getLastEvaluateRecordDTOByVehicleNo(String vehicleNo) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getLastEvaluateRecordDTOByVehicleNo(session, vehicleNo);
      return (EvaluateRecord) q.uniqueResult();
    } finally {
      release(session);
    }
  }


  /**
   * 查询微信列表
   */
  public List<WXArticleTemplate> getWXArticleJobs(WXArticleTemplateDTO wxArticleDTO, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWXArticleJobs(session, wxArticleDTO, pager);
      return (List<WXArticleTemplate>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 查询微信总数
   */
  public int getCountWXArticleJob(WXArticleTemplateDTO wxArticleDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCountWXArticleJob(session, wxArticleDTO);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<WXUser> getWxUsersBySearchCondition(WXUserSearchCondition searchCondition, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWxUsersBySearchCondition(session, searchCondition, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WXUser> getMyFans(WXUserSearchCondition searchCondition, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getMyFans(session, searchCondition, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countWxUsersBySearchCondition(WXUserSearchCondition searchCondition) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.countWxUsersBySearchCondition(session, searchCondition);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public int countMyFans(WXUserSearchCondition searchCondition) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.countMyFans(session, searchCondition);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }


  //店铺拿到并且校验自己的opendId
  public List<ShopWXUser> getShopWXUserListByShopIdAndOpenIds(Long shopId, String[] openIds) {
    if (shopId == null || ArrayUtils.isEmpty(openIds)) {
      return new ArrayList<ShopWXUser>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getShopWXUserListByShopIdAndOpenIds(session, shopId, openIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  /**
   * 查询微信user列表
   */
  public List<WXUser> getWXUserJobs(WXUserDTO wXUserDTO, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWXUserJobs(session, wXUserDTO, pager);
      return (List<WXUser>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 查询微信user总数
   */
  public int getCountWXUserJob(WXUserDTO wXUserDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCountWXUserJob(session, wXUserDTO);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  /**
   * 微信WXUserVehicle列表
   */
  public List<WXUserVehicle> getWXUserVehicleJobs(WXUserDTO wXUserDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWXUserVehicleJobs(session, wXUserDTO);
      return (List<WXUserVehicle>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 查询微信WXUserVehicle总数
   */
  public int getCountWXUserVehicleJob(WXUserDTO wXUserDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCountWXUserVehicleJob(session, wXUserDTO);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  /**
   * 查询area
   */
  public Area getAreaByNo(String no) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAreaJobs(session, no);
      return (Area) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  /**
   * 查询area
   */
  public WXAccount getPublicNameByPublicNo(String no) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPublicNameByPublicNo(session, no);
      return (WXAccount) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  /**
   * 查询微信粉丝相关信息
   */
  public Object[] getWXFanDTOByLicence_no(String licence_no) {
    Session session = this.getSession();
    try {
      Query q = UserSQL.getWXFanDTOByLicence_no(session, licence_no);
      return (Object[]) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<WXFanDTO> getShopWxUserVehicleInfo(Long shopId, Set<String> vehicleNos) {
    List<WXFanDTO> wxFanDTOs = new ArrayList<WXFanDTO>();
    if (shopId == null || CollectionUtils.isEmpty(vehicleNos)) {
      return wxFanDTOs;
    }
    Session session = this.getSession();
    try {
      Query query = UserSQL.getShopWxUserVehicleInfo(session, shopId, vehicleNos);
      List<Object[]> result = query.list();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          if (!ArrayUtils.isEmpty(objects)) {
            WXFanDTO wxFanDTO = new WXFanDTO();
            wxFanDTO.setLicenceNo(StringUtil.valueOf(objects[0]));
            wxFanDTO.setModel(StringUtil.valueOf(objects[1]));
            wxFanDTO.setBrand(StringUtil.valueOf(objects[2]));
            wxFanDTO.setName(StringUtil.valueOf(objects[3]));
            wxFanDTO.setMobile(StringUtil.valueOf(objects[4]));
            wxFanDTO.setVehicleId(StringUtil.valueOf(objects[5]));
            wxFanDTO.setCustomerId(StringUtil.valueOf(objects[6]));
            wxFanDTOs.add(wxFanDTO);
          }
        }
      }
      return wxFanDTOs;
    } finally {
      release(session);
    }
  }


  public List<ShopInternalVehicle> getShopInternalVehicleByShopId(Long shopId) {
    if (shopId == null) {
      return new ArrayList<ShopInternalVehicle>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getShopInternalVehicleByShopId(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countShopInternalVehicleGroupByShopId() {
    Session session = this.getSession();
    try {
      Query query = SQL.countShopInternalVehicleGroupByShopId(session);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<Long> getShopInternalVehicleShopIds(Pager pager) {
    Session session = this.getSession();
    try {
      Query query = SQL.getShopInternalVehicleShopIds(session, pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ShopInternalVehicle> getShopInternalVehicleByShopIds(List<Long> shopIds) {
    if (CollectionUtils.isEmpty(shopIds)) {
      return new ArrayList<ShopInternalVehicle>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getShopInternalVehicleByShopIds(session, shopIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ShopInternalVehicleDTO> getQueryInternalVehicleNo(Long shopId, String vehicleNo) {
    if (shopId == null) {
      return new ArrayList<ShopInternalVehicleDTO>();
    }
    Session session = this.getSession();
    try {
      List<ShopInternalVehicleDTO> shopInternalVehicleDTOs = new ArrayList<ShopInternalVehicleDTO>();
      Query query = SQL.getQueryInternalVehicleNo(session, shopId, vehicleNo);
      List<Object[]> result = query.list();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          if (!ArrayUtils.isEmpty(objects)) {
            ShopInternalVehicleDTO shopInternalVehicleDTO = new ShopInternalVehicleDTO();
            shopInternalVehicleDTO.setVehicleNo(StringUtil.valueOf(objects[0]));
            shopInternalVehicleDTO.setVehicleId(NumberUtil.longValue(objects[1]));
            shopInternalVehicleDTOs.add(shopInternalVehicleDTO);
          }
        }
      }
      return shopInternalVehicleDTOs;
    } finally {
      release(session);
    }
  }

  public int countShopDriveLogStat(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.countShopDriveLogStat(session, shopInternalVehicleRequestDTO);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<ShopInternalVehicleDriveStatDTO> getShopDriveLogStat(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.getShopDriveLogStat(session, shopInternalVehicleRequestDTO);
      List<Object[]> result = query.list();
      List<ShopInternalVehicleDriveStatDTO> shopInternalVehicleDriveStatDTOs = new ArrayList<ShopInternalVehicleDriveStatDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          ShopInternalVehicleDriveStatDTO shopInternalVehicleDriveStatDTO = new ShopInternalVehicleDriveStatDTO();
          shopInternalVehicleDriveStatDTO.setVehicleId(NumberUtil.longValue(objects[0]));
          shopInternalVehicleDriveStatDTO.setDistance(NumberUtil.round(objects[1]));
          shopInternalVehicleDriveStatDTO.setOilWear(NumberUtil.round(objects[2]));
          shopInternalVehicleDriveStatDTO.setAvgOilWear(NumberUtil.round(objects[3]));
          shopInternalVehicleDriveStatDTO.setTravelTime(NumberUtil.round(objects[4]));
          shopInternalVehicleDriveStatDTO.setTravelTimeStr(DateUtil.convertSecondTimeStr(shopInternalVehicleDriveStatDTO.getTravelTime()));
          shopInternalVehicleDriveStatDTO.setDriveCount(NumberUtil.intValue(objects[5]));
          shopInternalVehicleDriveStatDTOs.add(shopInternalVehicleDriveStatDTO);
        }
      }
      return shopInternalVehicleDriveStatDTOs;
    } finally {
      release(session);
    }
  }

  public int countShopDriveLog(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.countShopDriveLog(session, shopInternalVehicleRequestDTO);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<DriveLogDTO> getShopDriveLogDTOs(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.getShopDriveLogDTOs(session, shopInternalVehicleRequestDTO);
      List<Object[]> result = query.list();
      List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          DriveLogDTO driveLogDTO = new DriveLogDTO();
          driveLogDTO.setId(NumberUtil.longValue(objects[0]));
          driveLogDTO.setStartPlace(StringUtil.valueOf(objects[1]));
          driveLogDTO.setEndPlace(StringUtil.valueOf(objects[2]));
          driveLogDTO.setStartTime(NumberUtil.longValue(objects[3]));
          driveLogDTO.setEndTime(NumberUtil.longValue(objects[4]));
          driveLogDTO.setTravelTime(NumberUtil.longValue(objects[5]));
          driveLogDTO.setTravelTimeStr(DateUtil.convertSecondTimeStr(driveLogDTO.getTravelTime()));
          driveLogDTO.setDistance(NumberUtil.round(objects[6]));
          driveLogDTO.setOilCost(NumberUtil.round(objects[7]));
          driveLogDTO.setOilWear(NumberUtil.round(objects[8]));
          driveLogDTOs.add(driveLogDTO);
        }
      }
      return driveLogDTOs;
    } finally {
      release(session);
    }
  }

  public List<ImpactVideo> getImpactVideoDTOByAppUserNo(String appUserNo) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getImpactVideoDTOByAppUserNo(session, appUserNo);
      return query.list();
    } finally {
      release(session);
    }
  }



  public int statImpactVideo(String appUserNo, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.statImpactVideo(session, appUserNo, startTime, endTime);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public ImpactVideo getImpactVideoDTOByUUID(String uuid) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getImpactVideoDTOByUUID(session, uuid);
      return (ImpactVideo) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public AppUserCustomer getAppUserCustomerByAppUserNo(String appUserNo) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getAppUserCustomerByAppUserNo(session, appUserNo);
      return (AppUserCustomer) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AppUserCustomer> getAppUserCustomersByAppUserNo(String appUserNo) {
    Session session = this.getSession();
    try {
      Query query = UserSQL.getAppUserCustomerByAppUserNo(session, appUserNo);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<AppVehicleFaultInfo> findAppVehicleFaultInfoList(String appUserNo, String status) {
    if (StringUtils.isEmpty(appUserNo)) {
      return new ArrayList<AppVehicleFaultInfo>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.findAppVehicleFaultInfoList(session, appUserNo, status);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ImpactVideoExpDTO> getImpactVideoExpDTOs(String appUserNo) {
    Session session = this.getSession();
    try {
      Query query = SQL.getImpactVideoExpDTOs(session, appUserNo);
      List<Object[]> result = query.list();
      List<ImpactVideoExpDTO> impactVideoExpDTOList = new ArrayList<ImpactVideoExpDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          ImpactVideoExpDTO driveLogDTO = new ImpactVideoExpDTO();
          driveLogDTO.setUuid(StringUtil.valueOf(objects[0]));
          driveLogDTO.setAppUserNo(StringUtil.valueOf(objects[1]));
          driveLogDTO.setUploadTime(NumberUtil.longValue(objects[2]));
          driveLogDTO.setLongitude(StringUtil.valueOf(objects[3]));
          driveLogDTO.setLatitude(StringUtil.valueOf(objects[4]));
          driveLogDTO.setImpactVideoId(NumberUtil.longValue(objects[5]));
          driveLogDTO.setPath(StringUtil.valueOf(objects[6]));
          if ("SUCCESS".equals(StringUtil.valueOf(objects[7]))) {
            driveLogDTO.setUploadStatus("上传成功");
          } else if ("EXCEPTION".equals(StringUtil.valueOf(objects[7]))) {
            driveLogDTO.setUploadStatus("上传异常");
          } else if ("UPLOADING".equals(StringUtil.valueOf(objects[7]))) {
            driveLogDTO.setUploadStatus("上传中");
          } else {
            driveLogDTO.setUploadStatus("暂无上传状态");
          }
          impactVideoExpDTOList.add(driveLogDTO);
        }
      }
      return impactVideoExpDTOList;
    } finally {
      release(session);
    }
  }

  public List<ImpactVideoExpDTO> getImpactVideoExpDTOs_page(String shopId, ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.getImpactVideoExpDTOs_page(session, shopId, impactInfoSearchConditionDTO);
      List<Object[]> result = query.list();
      List<ImpactVideoExpDTO> impactVideoExpDTOList = new ArrayList<ImpactVideoExpDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          ImpactVideoExpDTO driveLogDTO = new ImpactVideoExpDTO();
          driveLogDTO.setImpactId(NumberUtil.longValue(objects[0]));
          driveLogDTO.setUuid(StringUtil.valueOf(objects[1]));
          driveLogDTO.setAppUserNo(StringUtil.valueOf(objects[2]));
          driveLogDTO.setUploadTime(NumberUtil.longValue(objects[3]));
          driveLogDTO.setLongitude(StringUtil.valueOf(objects[4]));
          driveLogDTO.setLatitude(StringUtil.valueOf(objects[5]));
          driveLogDTO.setImpactVideoId(NumberUtil.longValue(objects[6]));
          driveLogDTO.setPath(StringUtil.valueOf(objects[7]));
          driveLogDTO.setStatus(StringUtil.valueOf(objects[8]));
          impactVideoExpDTOList.add(driveLogDTO);
        }
      }
      return impactVideoExpDTOList;
    } finally {
      release(session);
    }
  }

  public int countGetImpactVideoExpDTOs_page(String shopId, ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO) {
    Session session = getSession();
    try {
      Query query = SQL.countGetImpactVideoExpDTOs_page(session, shopId, impactInfoSearchConditionDTO);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public int countGetImpactVideoExpDTOs(String appUserNo) {
    Session session = getSession();
    try {
      Query query = SQL.countGetImpactVideoExpDTOs(session, appUserNo);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }


  public List<RescueDTO> getRescueDTOs_page(Long shopId, SosInfoSearchConditionDTO sosInfoSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.getRescueDTOs_page(session, shopId, sosInfoSearchConditionDTO);
      List<Object[]> result = query.list();
      List<RescueDTO> rescueDTOList = new ArrayList<RescueDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          RescueDTO rescueDTO = new RescueDTO();
          rescueDTO.setId(NumberUtil.longValue(objects[0]));
          rescueDTO.setShopId(NumberUtil.longValue(objects[1]));
          rescueDTO.setAppUserNo(StringUtil.valueOf(objects[2]));
          rescueDTO.setLon(StringUtil.valueOf(objects[3]));
          rescueDTO.setLat(StringUtil.valueOf(objects[4]));
          rescueDTO.setUploadTime(NumberUtil.longValue(objects[5]));
          rescueDTO.setUploadServerTime(NumberUtil.longValue(objects[6]));
          rescueDTO.setAddr(StringUtil.valueOf(objects[7]));
          rescueDTO.setAddrShort(StringUtil.valueOf(objects[8]));
          rescueDTO.setState(StringUtil.valueOf(objects[9]));
          rescueDTOList.add(rescueDTO);
        }
      }
      return rescueDTOList;
    } finally {
      release(session);
    }
  }

  public int countGetRescueDTOs(Long shopId, SosInfoSearchConditionDTO sosInfoSearchConditionDTO) {
    Session session = getSession();
    try {
      Query query = SQL.countRescueDTOs(session, shopId, sosInfoSearchConditionDTO);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<MileageDTO> getMileageDTOs_page(Long shopId, MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAppVehicleDTOs_page(session, shopId, mileageInfoSearchConditionDTO);
      List<Object[]> result = query.list();
      List<MileageDTO> mileageDTOList = new ArrayList<MileageDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          MileageDTO mileageDTO = new MileageDTO();
          mileageDTO.setAppVehicleId(NumberUtil.longValue(objects[0]));
          mileageDTO.setMobile(StringUtil.valueOf(objects[1]));
          mileageDTO.setContact(StringUtil.valueOf(objects[2]));
          mileageDTO.setVehicleNo(StringUtil.valueOf(objects[3]));
          mileageDTO.setNextMaintainMileage(StringUtil.valueOf(objects[4]));
          mileageDTO.setCurrentMileage(StringUtil.valueOf(objects[5]));
          String nextMaintainMileage = StringUtil.valueOf(objects[6]);
          if(!nextMaintainMileage.isEmpty() && Double.parseDouble(nextMaintainMileage) <0){
            nextMaintainMileage = "--";
          }
          mileageDTO.setToNextMaintainMileage(nextMaintainMileage);
          mileageDTO.setAppUserNo(StringUtil.valueOf(objects[7]));
          mileageDTOList.add(mileageDTO);
        }
      }
      return mileageDTOList;
    } finally {
      release(session);
    }
  }

  public int countMileageDTOs(Long shopId, MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO) {
    Session session = getSession();
    try {
      Query query = SQL.countAppVehicleDTOs(session, shopId, mileageInfoSearchConditionDTO);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }


  public List<ImpactDetailDTO> getImpact_detail(Long shopId, String impactId, long uploadTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.getImpact_detail(session, shopId, impactId, uploadTime);
      List<Object[]> result = query.list();
      List<ImpactDetailDTO> impactDetailDTOs = new ArrayList<ImpactDetailDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          ImpactDetailDTO impactDetailDTO = new ImpactDetailDTO();
          impactDetailDTO.setId(NumberUtil.longValue(objects[0]));
          impactDetailDTO.setUuid(StringUtil.valueOf(objects[1]));
          impactDetailDTO.setUploadTime(StringUtil.valueOf(objects[2]));
          impactDetailDTO.setLon(StringUtil.valueOf(objects[3]));
          impactDetailDTO.setLat(StringUtil.valueOf(objects[4]));
          impactDetailDTO.setRdtc(StringUtil.valueOf(objects[5]));
          impactDetailDTO.setRpm(StringUtil.valueOf(objects[6]));
          impactDetailDTO.setVss(StringUtil.valueOf(objects[7]));
          impactDetailDTOs.add(impactDetailDTO);
        }
      }
      return impactDetailDTOs;
    } finally {
      release(session);
    }
  }

  public List<ImpactAndVideoDTO> getImpactAndVideo ( long vehicleId, long datetime , int count){
    Session session = this.getSession();
    try{
      Query query = SQL.getImpactAndVideo(session,vehicleId,datetime);
      query.setFirstResult(0);
      query.setMaxResults(count);
      List<Object[]> result = query.list();
      List<ImpactAndVideoDTO> impactAndVideoDTOs = new ArrayList<ImpactAndVideoDTO>();
      List<String> listS = new ArrayList<String>();
      listS.add("http://f.hiphotos.baidu.com/zhidao/pic/item/10dfa9ec8a1363275f711d38958fa0ec08fac719.jpg");
      listS.add("http://pic.962.net/up/2014-4/2014043015462712415.jpg");
      listS.add("http://a.hiphotos.baidu.com/zhidao/pic/item/d000baa1cd11728b976c4c8fc9fcc3cec2fd2cde.jpg");
      listS.add("http://att.bbs.duowan.com/forum/201403/16/083548xr292mjea1x2dqqa.jpg");
      listS.add("http://img1.imgtn.bdimg.com/it/u=2900838637,1481829910&fm=21&gp=0.jpg");
      listS.add("http://d.hiphotos.baidu.com/image/w%3D310/sign=59cd659ea60f4bfb8cd09855334e788f/29381f30e924b89972ce73396c061d950b7bf6c5.jpg");
      String[] picture = (String[]) listS.toArray(new String[listS.size()]);
      List<String> video = new ArrayList<String>();
      video.add("http://42.121.98.170:88/impact/test/10000010202100109.mp4");
      video.add("http://61.160.204.15/youku/6775A5EEAD54D8318ECD6032B7/0300080100562F3D77765D003E88033845D89C-E579-69F3-EF80-A67B4F93ADDC.mp4");
      String[] videoS = (String[]) video.toArray(new String[video.size()]);
      if (CollectionUtil.isNotEmpty(result)){
        for (Object[] objects : result){
          ImpactAndVideoDTO impactAndVideoDTO = new ImpactAndVideoDTO();
          impactAndVideoDTO.setPlace(StringUtil.valueOf(objects[0]));
          impactAndVideoDTO.setDate(NumberUtil.longValue(objects[1]));
          impactAndVideoDTO.setType(NumberUtil.intValue(objects[2]));
          impactAndVideoDTO.setVid(NumberUtil.longValue(objects[3]));
//          impactAndVideoDTO.setVideo(StringUtil.valueOf(objects[4]));
          impactAndVideoDTO.setPicture(picture);
          impactAndVideoDTO.setVideo(videoS);
          if (StringUtil.valueOf(objects[5]).equals("SUCCESS")){
            impactAndVideoDTO.setProgress(0);
          }
          impactAndVideoDTO.setProgress(1);

          impactAndVideoDTOs.add(impactAndVideoDTO);
        }
      }
      return  impactAndVideoDTOs;
    }finally {
      release(session);
    }
  }


  public List<Map> getInnodbTrx() {
    Session session = this.getSession();
    try {
      Query query = SQL.getInnodbTrx(session);
      List<Object[]> result = query.list();
      List<Map> trxList = new ArrayList<Map>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          Map trxMap = new HashMap();
          trxMap.put("trx_mysql_thread_id", NumberUtil.longValue(objects[0]));
          trxMap.put("trx_id", objects[1]);
          trxMap.put("trx_started", NumberUtil.longValue(objects[2]));
          trxList.add(trxMap);
        }
      }
      return trxList;
    } finally {
      release(session);
    }
  }

  public void killTrxMysqlThread(Long threadId) {
    if (threadId == null) return;
    Session session = this.getSession();
    try {
      Query query = SQL.killTrxMysqlThread(session, threadId);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }
  public List<ConsumingRecordDTO> findConsumingRecordByShopId(long shopId , int start , int size){
    Session session = getSession();
    try {
      Query q = SQL.findConsumingRecordByShopId(session, shopId , start , size);
      List<ConsumingRecordDTO> consumingRecordDTOs = new ArrayList<ConsumingRecordDTO>();
      List<Object[]> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] o : list) {
          if (o != null && o.length > 0) {
            ConsumingRecordDTO consumingRecordDTO = new ConsumingRecordDTO();
            consumingRecordDTO.setCoupon((Float) o[0]);
            consumingRecordDTO.setOrderId((Long) o[1]);
            consumingRecordDTO.setOrderType((String) o[2]);
            consumingRecordDTO.setTime((Long) o[3]);
            consumingRecordDTO.setUserName((String) o[4]);
            consumingRecordDTO.setCustomerName((String) o[5]);
            consumingRecordDTO.setVehicleNo((String) o[6]);
            consumingRecordDTO.setId((Long) o[7]);
            String orderStatus=null!=o[8]?(String) o[8]:null;
            if(StringUtil.isNotEmpty(orderStatus)){
              consumingRecordDTO.setOrderStatus(OrderStatus.parseEnum(orderStatus));
            }
            consumingRecordDTOs.add(consumingRecordDTO);
          }
        }
      }
      return consumingRecordDTOs;
    }finally {
      release(session);
    }
  }

  public Long findConsumingRecordCountByShopId(long shopId){
    Session session = getSession();
    try {
      Query q = SQL.findConsumingRecordCountByShopId(session, shopId );
      return (Long) q.uniqueResult();
    }finally {
      release(session);
    }
  }

  /**
   * 代金券交易总数和代金券金额总和
   * @param startTime 开始时间
   * @param endTime 截止时间
   * @return  包含代金券交易总数和代金券金额总和的object数组
   * 第0个为交易记录总数，第1个为金额总和
   */
  public List<String> countConsumingRecordAndSumCoupon(Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.countConsumingRecordAndSumCoupon(session, startTime, endTime, couponConsumeRecordDTO);
      List<String> stringList = new ArrayList<String>();
      if (q == null) {
        return stringList;
      }
      List<Object> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
        }
      }
      return stringList;
    } finally {
      release(session);
    }
  }

  /**
   * 获取代金券交易记录
   * @param startTime 开始时间
   * @param endTime 截止时间
   * @param arrayType 排序方式
   * @param pager 分页信息
   * @return  返回代金券交易记录列表
   */
  public List<CouponConsumeRecordDTO> getConsumingRecordListByPagerTimeArrayType(Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO,String arrayType, Pager pager){
    Session session = getSession();
    List<CouponConsumeRecordDTO> couponConsumeRecordDTOs=new ArrayList<CouponConsumeRecordDTO>();

    try{
      Query q = SQL.getConsumingRecordListByPagerTimeArrayType(session, startTime, endTime, couponConsumeRecordDTO, arrayType, pager);
      List<Object[]> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] o : list) {
          if (o != null && o.length > 0) {
            CouponConsumeRecordDTO dto = new CouponConsumeRecordDTO();
            dto.setAppUserNo((String)o[0]);    //用户ID
            dto.setReceiptNo((String) o[1]);    //订单号
            dto.setCoupon((Double) o[2]);       //使用代金券金额
            dto.setShopId((Long) o[3]);         //消费店铺
            dto.setOrderId((Long) o[4]);        //单据ID
            dto.setOrderTypes((String) o[5]);   //单据类型
            dto.setConsumerTime((Long) o[6]);   //消费时间
            dto.setProduct((String) o[7]);      //购买的商品
            dto.setProductNum((Integer) o[8]);  //商品数量
            dto.setIncomeType((String) o[9]);   //收入支出类型
            dto.setSumMoney((Float) o[10]);     //总金额
            dto.setAppUserName((String) o[11]); //客户名
            dto.setAppVehicleNo((String) o[12]);//车牌号
            dto.setId((Long) o[13]);//代金券消费记录id
            dto.setCustomerInfo((String) o[14]); //客户信息
            dto.setOrderStatus((String) o[15]); //客户信息
            couponConsumeRecordDTOs.add(dto);
          }
        }
      }
      return couponConsumeRecordDTOs;
    } finally {
      release(session);
    }
  }

  /**
   * 通过代金券消费记录id查询对应的记录列表
   * @param id
   * @return 代金券消费记录列表
   */
  public List<ConsumingRecord> getCouponConsumeRecordById(Long id){
    Session session = getSession();
    try{
      Query q=SQL.getCouponConsumeRecordById(session,id);
      return q.list();
    } catch (Exception e){
      LOG.info(e.getMessage());
      return new ArrayList<ConsumingRecord>();
    } finally {
      release(session);
    }
  }

  /**
   * 通过代金券消费记录id查询对应的记录列表
   * @param id
   * @return 代金券消费记录列表
   */
  public List<ConsumingRecord> getCouponConsumeRecordByShopIdAndId(Long shopId, Long id){
    Session session = getSession();
    try{
      Query q=SQL.getCouponConsumeRecordByShopIdAndId(session, shopId, id);
      return q.list();
    } catch (Exception e){
      LOG.info(e.getMessage());
      return new ArrayList<ConsumingRecord>();
    } finally {
      release(session);
    }
  }

  public Coupon getCoupon(String appUserNo){
    Session session = this.getSession();
    try {
      Query query = SQL.getCoupon(session,appUserNo);
      return (Coupon) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  /**
   * 单据操作过程中，保存对应的代金券交易记录到数据库
   * @param shopId
   * @param couponConsumeRecordDTO  保存有单据相关信息的代金券交易记录DTO
   * @throws ParseException
   */
  public void updateConsumingRecordFromOrderInfo(Long shopId,CouponConsumeRecordDTO couponConsumeRecordDTO) throws ParseException {
    Session session = getSession();
    int resultCount;
    try {
      Query q = SQL.updateConsumingRecordFromOrderInfo( session, shopId, couponConsumeRecordDTO);
      resultCount = q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<ConsumingPageDTO> getConsumingRecord(String appUserNo , long dateTime , int count){
    Session session = this.getSession();
    List<ConsumingPageDTO> consumingPageDTOs = new ArrayList<ConsumingPageDTO>();
    try {
      Query query = SQL.getConsumingRecord(session , appUserNo , dateTime , count);
      List<Object[]> list = query.list();
      for (Object[] o : list) {
        ConsumingPageDTO consumingPageDTO = new ConsumingPageDTO();
        consumingPageDTO.setId((Long) o[0]);
        consumingPageDTO.setCoupon((Double) o[1]);
        consumingPageDTO.setConsumerTime((Long) o[2]);
        consumingPageDTO.setReceiptNo((String) o[3]);
        consumingPageDTO.setOrderTypes((String) o[4]);
        consumingPageDTO.setOrderStatus((String) o[5]);
        consumingPageDTO.setProductId((Long) o[6]);
        consumingPageDTO.setProduct((String) o[7]);
        consumingPageDTO.setSumMoney((Double) o[8]);
        consumingPageDTOs.add(consumingPageDTO);
      }
      return consumingPageDTOs;
    }finally {
      release(session);
    }
  }

  public ConsumingRecord getConsumingRecord(long consumingId){
    Session session = this.getSession();
    try {
      Query query = SQL.getConsumingRecord(session , consumingId);
      return (ConsumingRecord) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long toOrderListCount(String customerName , String telNumber, String vehicleNumber,
                               String orderNumber,String goodsName,String orderStatus) {
    Session session = this.getSession();
    try {
      Query query = SQL.toOrderListCount(session, customerName,telNumber, vehicleNumber ,orderNumber ,goodsName, orderStatus);
      List<Object[]> objects = query.list();
      return Long.valueOf(objects.size());
    } finally {
      release(session);
    }
  }

  public List<ConsumingAdminDTO> toOrderList( String customerName , String telNumber, String vehicleNumber,
                                              String orderNumber,String goodsName,String orderStatus,Pager pager){
    Session session = this.getSession();
    List<ConsumingAdminDTO> consumingAdminDTOs = new ArrayList<ConsumingAdminDTO>();
    try {
      Query query = SQL.toOrderList(session, customerName,telNumber, vehicleNumber ,orderNumber ,goodsName, orderStatus, pager);
      List<Object[]> objects = query.list();
      for (Object[] o : objects){
        ConsumingAdminDTO consumingAdminDTO = new ConsumingAdminDTO();
        consumingAdminDTO.setReceiptNo( (String) o[0]);
        consumingAdminDTO.setAdminStatus((String) o[1]);
        consumingAdminDTO.setSumMoney((Double) o[2]);
        consumingAdminDTO.setCoupon((Double) o[3]);
        consumingAdminDTO.setProduct((String) o[4]);
        consumingAdminDTO.setUserName((String) o[5]);
        consumingAdminDTO.setMobile((Long) o[6]);
        consumingAdminDTO.setVehicleNo((String) o[7]);
        consumingAdminDTO.setActuallyPay(consumingAdminDTO.getSumMoney() - consumingAdminDTO.getCoupon());
        consumingAdminDTOs.add(consumingAdminDTO);
      }
      return consumingAdminDTOs;
    } finally {
      release(session);
    }
  }


  public void updateCouponBalance(Coupon coupon){
    Session session = getSession();
    try {
      Query q = SQL.updateCouponBalance(session, coupon);
      q.executeUpdate();
    }catch (Exception e){
      e.printStackTrace();
    }
    finally {
      release(session);
    }
  }

  public Long getRecommendPhone(String appUserNo){
    Session session = this.getSession();
    try {
      Query query = SQL.getRecommendPhone(session , appUserNo);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public void saveRecommendPhone( String appUserNo ,long phone ,double coupon ){
    Session session = this.getSession();
    try {
      session.beginTransaction();
      Query query = SQL.saveRecommendPhone(session , appUserNo , phone,coupon);
      query.executeUpdate();
      session.getTransaction().commit();
    } finally {
      release(session);
    }
  }

  public Long getIsShared(String appUserNo){
    Session session = this.getSession();
    try {
      Query query = SQL.getIsShared(session, appUserNo);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public void saveIsShared( String appUserNo ,int isShared ,double coupon ){
    Session session = this.getSession();
    try {
      session.beginTransaction();
      Query query = SQL.saveIsShared(session, appUserNo, isShared, coupon);
      query.executeUpdate();
      session.getTransaction().commit();
    } finally {
      release(session);
    }
  }

  public void saveCoupon( String appUserNo ,double coupon ){
    Session session = this.getSession();
    try {
      session.beginTransaction();
      Query query = SQL.saveCoupon(session, appUserNo, coupon);
      query.executeUpdate();
      session.getTransaction().commit();
    } finally {
      release(session);
    }
  }

  public AppUser getAppUserByPhone (long phone) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAppUserByPhone(session , phone);
      return (AppUser) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public void updateAdminStatus (String receiptNo){
    Session session = this.getSession();
    try {
      session.beginTransaction();
      Query query = SQL.updateAdminStatus(session, receiptNo);
      query.executeUpdate();
      session.getTransaction().commit();
    }finally {
      release(session);
    }
  }

  public List<Coupon> getCouponsByImei(String imei){
    Session session = this.getSession();
    try{
      Query q = SQL.getCouponsByImei(session,imei);
      return q.list();
    }finally {
      release(session);
    }
  }

  public List<Coupon> getCouponsByAppUserNo(String appUserNo){
    Session session = this.getSession();
    try{
      Query q = SQL.getCoupon(session,appUserNo);
      return q.list();
    }finally {
      release(session);
    }
  }
  /**
   * 获取逾期未处理的空白单据(代金券消费记录)
   * @param overdueTime
   * @param start
   * @param size
   */
  public List<ConsumingRecord> getOverdueConsumingRecord(Long overdueTime, int start, int size){
    if (size<0||start<0) {
      return new ArrayList<ConsumingRecord>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getOverdueConsumingRecord(session, overdueTime, start, size);
      return q.list();
    } finally {
      release(session);
    }
  }

  public WXShareDTO wxShareInfo( String appUserNo){
    Session session = this.getSession();
    try {
      Query q = SQL.wxShareInfo(session , appUserNo);
      Object[] o = (Object[]) q.uniqueResult();
      WXShareDTO shareDTO = new WXShareDTO();
      shareDTO.setCompany((String)o[0]);
      shareDTO.setAddress((String) o[1]);
      shareDTO.setQq((String) o[2]);
      shareDTO.setPhone((String) o[3]);
      shareDTO.setEmail((String) o[4]);
      shareDTO.setLat((String) o[5]);
      shareDTO.setLon((String) o[6]);
      return shareDTO;
    }finally {
      release(session);
    }
  }
}


