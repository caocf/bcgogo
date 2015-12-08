package com.bcgogo.user.model;

import com.bcgogo.BooleanEnum;
import com.bcgogo.api.*;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Sort;
import com.bcgogo.enums.*;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.user.*;
import com.bcgogo.enums.user.userGuide.SosStatus;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.impact.ImpactInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.mileage.MileageInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.sos.SosInfoSearchConditionDTO;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.*;
import com.bcgogo.user.model.app.AppUser;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.model.app.AppVehicle;
import com.bcgogo.user.model.app.OBD;
import com.bcgogo.user.model.permission.Menu;
import com.bcgogo.user.model.permission.Resource;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXArticleTemplateDTO;
import com.bcgogo.wx.user.WXUserDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Xiao Jian
 * Date: 9/15/11
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQL {
  public static Query getUserByUserName(Session session, String userName) {
    return session.createQuery("select u from User as u where u.userName = :userName")
      .setString("userName", userName);
  }

  public static Query getUserByUserNo(Session session, String userNo) {
    return session.createQuery("select u from User as u where u.userNo = :userNo and u.status != :status")
      .setString("userNo", userNo).setString("status", Status.deleted.toString());
  }

  public static Query getAllStateUserByUserNo(Session session, String userNo) {
    return session.createQuery("select u from User as u where u.userNo = :userNo ")
      .setString("userNo", userNo);
  }

  public static Query getUserByFuzzyUserNo(Session session, String userNo, int maxResults) {
    return session.createQuery("select u from User as u where u.userNo like :userNo")
      .setString("userNo", userNo + "%").setMaxResults(maxResults);
  }

  public static Query getUserByEmail(Session session, String email) {
    return session.createQuery("select u from User as u where u.email = :email")
      .setString("email", email);
  }

  public static Query getUserByMobile(Session session, String mobile) {
    return session.createQuery("select u from User as u where u.mobile = :mobile")
      .setString("mobile", mobile);
  }

  public static Query getUserByUserGroupId(Session session, Long userGroupId) {
    return session.createQuery("select ugu from UserGroupUser as ugu where ugu.userGroupId = :userGroupId")
      .setLong("userGroupId", userGroupId);
  }

  public static Query countUserByUserGroupId(Session session, Long userGroupId) {
    return session.createQuery("select count(ugu) from UserGroupUser as ugu,User u where ugu.userGroupId = :userGroupId and u.id=ugu.userId and (u.status != :status or u.status is null or u.status = '')")
      .setLong("userGroupId", userGroupId).setString("status", Status.deleted.toString());
  }

  public static Query countSaleMansByUserGroupId(Session session, Long userGroupId) {
    return session.createQuery("select count(s) from SalesMan as s where s.userGroupId = :userGroupId and (s.status != :status or s.status is null or s.status = '')")
      .setLong("userGroupId", userGroupId).setString("status", SalesManStatus.DELETED.toString());
  }

  public static Query getUserGroupByUserId(Session session, Long userId) {
//    return session.createQuery("select distinct ugu from UserGroupUser as ugu where ugu.userId = :userId")
//        .setLong("userId", userId);
    return session.createQuery("select ug from UserGroupUser ugu,UserGroup ug where ug.id=ugu.userGroupId and ugu.userId=:userId")
      .setLong("userId", userId);
  }

  public static Query getUserGroupUser(Session session, Long userId) {
    return session.createQuery("select ugu from UserGroupUser as ugu where ugu.userId = :userId")
      .setLong("userId", userId);
  }

  public static Query getRoleByUserGroupId(Session session, Long userGroupId) {
    return session.createQuery("select distinct ugr from UserGroupRole as ugr where ugr.userGroupId = :userGroupId")
      .setLong("userGroupId", userGroupId);
  }

  public static Query getShopUser(Session session, Long shopId) {
    return session.createQuery("select u from User as u where u.shopId = :shopId")
      .setLong("shopId", shopId);
  }

  public static Query getSystemCreatedUser(Session session, Long shopId) {
    return session.createQuery("select u from User as u where u.shopId =:shopId and u.userType = :userType").setLong("shopId", shopId).setString("userType", UserType.SYSTEM_CREATE.toString());
  }

  public static Query getUser(Session session, Long shopId) {
    return session.createQuery("select u from User as u where u.shopId =:shopId").setLong("shopId", shopId);
  }

  public static Query getUsersByDepartmentIds(Session session, Set<Long> departmentIds) {
    return session.createQuery("select u from User as u where u.departmentId in :departmentIds")
      .setParameterList("departmentIds", departmentIds);
  }

  public static Query getUsersByIds(Session session, Set<Long> ids) {
    return session.createQuery("select u from User as u where u.id in :ids")
      .setParameterList("ids", ids);
  }

  public static Query getUserByShopIDAndMobile(Session session, Long shopId, String mobile) {
    return session.createQuery("select u from User as u where u.shopId = :shopId and u.mobile = :mobile")
      .setLong("shopId", shopId).setString("mobile", mobile);
  }


  public static Query getShopUserGroup(Session session, Long shopId) {
    return session.createQuery("select ug from UserGroup as ug where ug.shopId = :shopId")
      .setLong("shopId", shopId);
  }

  public static Query getVehicleByLicenceNo(Session session, Long shopId, String licenceNo) {
    StringBuilder sb = new StringBuilder("select v from Vehicle as v where  v.licenceNo = :licenceNo and (v.status is null or v.status = :status)");
    if (shopId != null) {
      sb.append(" and v.shopId =:shopId");
    }

    Query query = session.createQuery(sb.toString())
      .setString("licenceNo", licenceNo)
      .setString("status", VehicleStatus.ENABLED.toString());

    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getVehicleByLicenceNo(Session session, String... licenceNo) {
    String hql = "select v.* from vehicle as v left join customer_vehicle cv on cv.vehicle_id=v.id where v.licence_no in(:licenceNo) and (v.status is null or v.status = :status)";
    Query q = session.createSQLQuery(hql)
      .addEntity(Vehicle.class)
      .setParameterList("licenceNo", licenceNo).setString("status", VehicleStatus.ENABLED.toString());
    return q;
  }

  public static Query getVehicleByShopIdOrLicenceNo(Session session, Long shopId, String licenceNo, Integer limit) {
    String hql = "select v from Vehicle as v where v.licenceNo like :licenceNo and (v.status is null or v.status = :status) ";
    if (shopId != null) {
      hql += " and v.shopId = :shopId ";
    }
    Query q = session.createQuery(hql)
      .setString("licenceNo", "%" + licenceNo + "%").setString("status", VehicleStatus.ENABLED.toString());
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    if (limit != null) {
      q.setMaxResults(limit);
    }
    return q;
  }

  public static Query getAppointServiceByCustomerVehicle(Session session, Long shopId, Long vehicleId, Long customerId) {
    return session.createQuery("from AppointService  where shopId = :shopId and vehicleId = :vehicleId and customerId=:customerId and status = :status")
      .setLong("shopId", shopId).setLong("vehicleId", vehicleId)
      .setLong("customerId", customerId).setString("status", AppointService.AppointServiceStatus.ENABLED.toString());
  }

  public static Query getAppointServiceByIds(Session session, Long shopId, Set<Long> ids) {
    StringBuffer sb = new StringBuffer();
    sb.append("from AppointService  where shopId = :shopId and id in (:ids) and status = :status");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("ids", ids)
      .setParameter("status", AppointService.AppointServiceStatus.ENABLED);
  }

  public static Query getCustomerByName(Session session, long shopId, String name) {
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.name = :name and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId)
      .setString("name", name).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getCustomerByMatchedName(Session session, long shopId, String name) {
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.name like :name and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId)
      .setString("name", "%" + name + "%").setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getRelatedCustomerByMatchedName(Session session, Long shopId, String name) {
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.name like :name  and c.customerShopId is not null and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId)
      .setString("name", "%" + name + "%").setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getAllCustomerByName(Session session, long shopId, String name) {
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.name = :name ")
      .setLong("shopId", shopId).setString("name", name);
  }


  public static Query getShopCustomerById(Session session, long shopId, long customerId) {
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.id = :customerId")
      .setLong("shopId", shopId)
      .setLong("customerId", customerId);
  }


  public static Query getCustomerByMobile(Session session, long shopId, String mobile) {
    return session.createQuery("select distinct c from Customer c, Contact con " +
      "where c.id = con.customerId and con.disabled = :disabled and c.shopId = :shopId and con.mobile = :mobile  and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId)
      .setString("mobile", mobile).setString("status", CustomerStatus.DISABLED.toString()).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getCustomerByVehicleMobile(Session session, long shopId, String mobile) {
    return session.createQuery("select cv from CustomerVehicle as cv,Vehicle as v where cv.vehicleId = v.id and v.shopId=:shopId and v.mobile=:mobile")
      .setLong("shopId", shopId).setString("mobile", mobile);
  }

  public static Query getCompleteCustomerByMobile(Session session, long shopId, String mobile) {
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.mobile = :mobile")
      .setLong("shopId", shopId)
      .setString("mobile", mobile);
  }

  public static Query getCustomerByNameAndMobile(Session session, long shopId, String name, String mobile) {
    if (mobile == null)
      return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.mobile is null and name=:name and (c.status is null or c.status != :status)")
        .setLong("shopId", shopId).setString("name", name).setString("status", CustomerStatus.DISABLED.toString());
    else
      return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.mobile = :mobile and name=:name and (c.status is null or c.status != :status)")
        .setLong("shopId", shopId).setString("mobile", mobile).setString("name", name).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query countShopCustomer(Session session, long shopId) {
    return session.createQuery("select count(*) from Customer as c where c.shopId = :shopId and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query countShopCustomerByKey(Session session, long shopId, String key) {
    if (StringUtil.isEmpty(key)) {
      return session.createQuery("select count(*) from Customer as c where c.shopId = :shopId and (c.status is null or c.status != :status)")
        .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
    }
    return session.createQuery("select count(distinct c.id) from Customer as c, Contact con " +
      "where c.shopId = :shopId and c.id = con.customerId and " +
      "(c.name like :key  or con.mobile like :key  or c.company like :key or c.landLine like :key or con.name like:key " +
      "or c.id in (select m.customerId from Member as m where m.memberNo = :memberNo)) and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId).setString("key", "%" + key + "%").setString("memberNo", key).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopCustomerByKey(Session session, long shopId, String key) {
    if (StringUtil.isEmpty(key)) {
      return session.createQuery("from Customer as c where  c.shopId = :shopId and (c.status is null or c.status != :status)")
        .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
    }
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and " +
      "(c.name like :key or c.mobile like :key or c.company like :key or c.landLine like :key or c.contact like:key " +
      "or c.id in (select m.customerId from Member as m where m.memberNo = :memberNo)) and (c.status is null or c.status != :status) ")
      .setLong("shopId", shopId).setString("key", "%" + key + "%").setString("memberNo", key).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query countShopSupplier(Session session, long shopId) {
    return session.createQuery("select count(*) from Supplier as s where s.shopId = :shopId")
      .setLong("shopId", shopId);
  }

  public static Query countShopSupplierByKey(Session session, long shopId, String key) {
    if (StringUtil.isEmpty(key)) {
      return session.createSQLQuery("select count(distinct s.id) from supplier s LEFT JOIN contact con on s.id = con.supplier_id where (con.disabled = :disabled or con.disabled is null)  " +
        "and s.shop_id = :shopId " +
        "and (s.status is null or s.status != :status)")
        .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString()).setInteger("disabled", ContactConstant.ENABLED);
    }


    return session.createSQLQuery("select count(distinct s.id) from supplier s LEFT JOIN contact con on s.id = con.supplier_id where (con.disabled = :disabled or con.disabled is null)  " +
      "and s.shop_id = :shopId and (con.mobile like :key or s.name like :key or con.name like :key or s.landline like :key) " +
      "and (s.status is null or s.status != :status)")
      .setLong("shopId", shopId).setString("key", "%" + key + "%").setString("status", CustomerStatus.DISABLED.toString()).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getShopSupplierByKey(Session session, long shopId, String key) {
    return session.createSQLQuery("select s.* from supplier s LEFT JOIN contact con on s.id = con.supplier_id where (con.disabled = :disabled or con.disabled is null) " +
      "and s.shop_id = :shopId and (con.mobile like :key or s.name like :key or con.name like :key or s.landline like :key) " +
      "and (s.status is null or s.status != :status)").addEntity(Supplier.class)
      .setLong("shopId", shopId).setString("key", "%" + key + "%").setParameter("status", CustomerStatus.DISABLED).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query countShopCustomerRecord(Session session, long shopId) {
    return session.createQuery("select count(*) from CustomerRecord as c where c.shopId = :shopId and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query countShopArrearsCustomerRecord(Session session, long shopId) {
    return session.createQuery("select count(*) from CustomerRecord as c where c.shopId = :shopId and c.totalReceivable>0 and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
  }


  public static Query getCustomerVehicleByVehicleId(Session session, Long vehicleId) {
    return session.createQuery("select distinct cv from CustomerVehicle as cv where cv.vehicleId = :vehicleId and (status is NULL or status =:status)")
      .setLong("vehicleId", vehicleId).setParameter("status", VehicleStatus.ENABLED);
  }

  public static Query getCustomerByVehicleNo(Session session, String vehicleNo) {
    return session.createSQLQuery("select  c.* from customer c " +
      "join customer_vehicle cv on c.id=cv.customer_id and (cv.status is NULL or cv.status ='ENABLED')" +
      "join vehicle v on cv.vehicle_id=v.id and (v.status is NULL or v.status ='ENABLED')" +
      "where (c.status is NULL or c.status ='ENABLED') and  v.licence_no = :vehicleNo ")
      .addEntity(Customer.class)
      .setParameter("vehicleNo", vehicleNo)
      ;
  }

  public static Query getCustomerInfoByVehicleId(Session session, Long shopId, Long vehicleId) {
    return session.createQuery("from Customer c, CustomerVehicle cv where cv.customerId = c.id and cv.vehicleId = :vehicleId and (cv.status is NULL or cv.status = 'ENABLED') and (c.status is NULL or c.status = 'ENABLED') and c.shopId = :shopId")
      .setLong("vehicleId", vehicleId).setLong("shopId", shopId);
  }

  public static Query getCustomersByVehicleIds(Session session, Long shopId, Set<Long> vehicleIds) {
    return session.createQuery("select c,cv from Customer c, CustomerVehicle cv where cv.customerId = c.id and cv.vehicleId in(:vehicleIds) and (cv.status is NULL or cv.status = 'ENABLED') and (c.status is NULL or c.status = 'ENABLED') and c.shopId = :shopId")
      .setParameterList("vehicleIds", vehicleIds).setLong("shopId", shopId);
  }

  public static Query getCustomerByVehicleIds(Session session, Long... vehicleIds) {
    return session.createQuery("select distinct cv from CustomerVehicle as cv where cv.vehicleId in(:vehicleIds) ")
      .setParameterList("vehicleIds", vehicleIds);
  }

  public static Query getCustomerVehicleDTOByVehicleIdAndCustomerId(Session session, Long vehicleId, Long customerId) {
    return session.createQuery("select cv from CustomerVehicle as cv where cv.vehicleId =:vehicleId " +
      "and cv.customerId =:customerId and (status is NULL or status =:status)")
      .setLong("vehicleId", vehicleId).setLong("customerId", customerId).setParameter("status", VehicleStatus.ENABLED);
  }

  public static Query getCustomerVehicle(Session session, Long... vehicleIds) {
    return session.createQuery("select cv from CustomerVehicle as cv where cv.vehicleId in (:vehicleIds) and (status is NULL or status =:status)")
      .setParameterList("vehicleIds", vehicleIds).setParameter("status", VehicleStatus.ENABLED);
  }

  public static Query getVehicleByCustomerId(Session session, Long customerId) {
    return session.createQuery("select distinct cv from CustomerVehicle as cv where cv.customerId = :customerId and (status is NULL or status=:status)")
      .setLong("customerId", customerId).setParameter("status", VehicleStatus.ENABLED);
  }

  public static Query getVehicleAppointment(Session session, Long customerId, Long vehicleId) {
    return session.createQuery("select distinct cv from CustomerVehicle as cv where cv.customerId = :customerId and cv.vehicleId=:vehicleId")
      .setLong("customerId", customerId).setLong("vehicleId", vehicleId);
  }

  public static Query getVehiclesByCustomerId(Session session, Long shopId, Long customerId) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT v.id,v.shop_id,v.licence_no,v.brand,v.model,v.year,v.engine,v.car_date,v.chassis_number,v.engine_no,v.contact,v.mobile,v.color,v.start_mileage,v.obd_mileage,v.gsm_obd_imei,v.gsm_obd_imei_mobile,cv.maintain_mileage_period");
    sb.append(" FROM vehicle v,customer_vehicle cv where v.id=cv.vehicle_id and v.shop_id=:shopId and (v.status is null OR v.status != :status)  ");
    sb.append(" and cv.customer_id=:customerId and (cv.status is null OR cv.status != :status) order by id");
//    sb.append("(SELECT vehicle_id FROM customer_vehicle cv WHERE cv.customer_id=:customerId and (cv.status is null OR cv.status != :status))  order by id");
    return session.createSQLQuery(sb.toString()).setLong("shopId", shopId).setString("status", VehicleStatus.DISABLED.toString()).setLong("customerId", customerId).setString("status", VehicleStatus.DISABLED.toString());
  }

  public static Query getCustomerIdByCustomerMobileAndVehicleNo(Session session, Long shopId, String mobile, String vehicleNo) {
    return session.createSQLQuery("select c.id as customerId " +
      " from customer c left join contact con on c.id=con.customer_id " +
      " left join customer_vehicle cv on c.id=cv.customer_id " +
      " left join vehicle v on v.id=cv.vehicle_id " +
      " where con.disabled = :disabled and c.shop_id = :shopId and v.shop_id = :shopId and con.mobile = :mobile and v.licence_no=:vehicleNo " +
      " and (c.status is null or c.status != :cstatus) and (cv.status is null or cv.status != :cvstatus) and (v.status != :vstatus or v.status is null )")
      .addScalar("customerId", StandardBasicTypes.LONG)
      .setLong("shopId", shopId)
      .setString("vehicleNo", vehicleNo)
      .setString("mobile", mobile)
      .setString("cstatus", CustomerStatus.DISABLED.toString())
      .setString("vstatus", VehicleStatus.DISABLED.toString())
      .setString("cvstatus", VehicleStatus.DISABLED.toString())
      .setInteger("disabled", ContactConstant.ENABLED)
      .setMaxResults(1);
  }

  public static Query getSupplierByName(Session session, long shopId, String name) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId and s.name like :name")
      .setLong("shopId", shopId)
      .setString("name", name + "%");
  }

  public static Query getSupplierByMatchedName(Session session, long shopId, String name) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId and s.name like :name")
      .setLong("shopId", shopId).setString("name", "%" + name + "%");
  }

  public static Query getSupplierById(Session session, long shopId, long supplierId) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId and s.id =:id")
      .setLong("shopId", shopId)
      .setLong("id", supplierId);
  }

  //zhangchuanlong
  public static Query getSupplierByNameAndShopId(Session session, long shopId, String name) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId and s.name =:name and (s.status=:status or s.status is null) order by s.lastOrderTime desc ")
      .setLong("shopId", shopId).setString("name", name).setString("status", CustomerStatus.ENABLED.toString());
  }

  public static Query getSpecialSupplierByName(Session session, long shopId, String name) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId and s.name =:name and s.supplierShopId is not null ")
      .setLong("shopId", shopId)
      .setString("name", name);
  }

  public static Query getSupplierByMobile(Session session, long shopId, String mobile) {
    return session.createQuery("select distinct s from Supplier s, Contact c " +
      "where s.id = c.supplierId and c.disabled =:disabled and s.shopId = :shopId and c.mobile = :mobile and (s.status =:status or s.status is null)")
      .setLong("shopId", shopId).setString("mobile", mobile).setString("status", CustomerStatus.ENABLED.toString()).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getSuppliersByKey(Session session, long shopId, String key, int pageNo, int pageSize) {

//    return session.createQuery("select distinct s from Supplier s, Contact c where s.id = c.supplierId and c.disabled =:disabled " +
//        "and s.shopId = :shopId and (c.mobile like :key or s.name like :key or c.name like :key or s.landLine like :key)")
    return session.createSQLQuery("select distinct s.* from supplier s left join contact c on s.id = c.supplier_id where s.id = c.supplier_id and c.disabled =:disabled " +
      "and s.shop_id = :shopId and (c.mobile like :key or s.name like :key or c.name like :key or s.landline like :key)").addEntity(Supplier.class)
      .setLong("shopId", shopId)
      .setString("key", "%" + key + "%")
      .setInteger("disabled", ContactConstant.ENABLED)
      .setFirstResult(pageNo * pageSize)
      .setMaxResults(pageSize);
  }

  public static Query getRecentlyTradeSupppliers(Session session, long shopId, String searchKey, int currentPage, int pageSize) {
    String sql = "select distinct s.* from supplier s left join contact con on s.id=con.supplier_id where s.shop_id = :shopId and (s.status is null or s.status != :status) and ( con.disabled =:disabled or con.disabled is null ) ";
    if (!StringUtil.isEmpty(searchKey)) {
      sql += " and (s.mobile like :searchKey or s.name like :searchKey or con.name like :searchKey or s.landline like :searchKey) ";
    }
    sql += " order by s.last_order_time desc ";
    if (StringUtil.isEmpty(searchKey)) {
      return session.createSQLQuery(sql).addEntity(Supplier.class).setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString()).setInteger("disabled", ContactConstant.ENABLED)
        .setFirstResult((currentPage - 1) * pageSize)
        .setMaxResults(pageSize);
    }
    return session.createSQLQuery(sql).addEntity(Supplier.class).setLong("shopId", shopId).setString("searchKey", "%" + searchKey + "%").setString("status", CustomerStatus.DISABLED.toString()).setInteger("disabled", ContactConstant.ENABLED)
      .setFirstResult((currentPage - 1) * pageSize)
      .setMaxResults(pageSize);
  }

  public static Query getShopSupplier(Session session, Long shopId, int pageNo, int pageSize) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId")
      .setLong("shopId", shopId).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize);
  }

  public static Query getShopCustomerRecord(Session session, Long shopId, int pageNo, int pageSize) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and (cr.status is null or cr.status != :status) order by cr.lastDate desc")
      .setLong("shopId", shopId)
      .setFirstResult(pageNo * pageSize)
      .setMaxResults(pageSize).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopArrearsCustomerRecord(Session session, Long shopId, int pageNo, int pageSize) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and cr.totalReceivable > 0 and (cr.status is null or cr.status = :status) order by cr.lastAmount desc")
      .setLong("shopId", shopId)
      .setFirstResult(pageNo * pageSize)
      .setMaxResults(pageSize).setString("status", CustomerStatus.ENABLED.toString());
  }

  public static Query countShopArrearsCustomerRecord1(Session session, Long shopId) {
    return session.createQuery("select count(*) from CustomerRecord as cr where cr.shopId = :shopId and cr.totalReceivable > 0 and (cr.status is null or cr.status != :status) order by cr.lastAmount desc")
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());

  }


  public static Query countCustomerServiceJobByShopId(Session session, long shopId, long remindTime, List<String> status) {
    return session.createQuery("select count(csj) from CustomerServiceJob csj, Customer c where csj.customerId = c.id and csj.shopId = :shopId and csj.remindTime >= :remindTime" +
      " and csj.status in(:status) and (c.status is null or c.status != :cstatus)")
      .setLong("shopId", shopId).setLong("remindTime", remindTime).setParameterList("status", status).setString("cstatus", CustomerStatus.DISABLED.toString());
  }


  public static Query getShopTotalArrears(Session session, Long shopId) {
    return session.createQuery("select sum(cr.totalReceivable) from CustomerRecord cr where cr.shopId = :shopId and cr.totalReceivable > 0 and (cr.status is null or cr.status != :status)")
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getCustomerRecordByCustomerId(Session session, Long customerId) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.customerId = :customerId ")
      .setLong("customerId", customerId);
  }

  public static Query getCustomers(Session session, CustomerDTO customerDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append("from Customer as c where c.shopId =:shopId and (c.status =:status or c.status is NULL )");
    if (customerDTO.getId() != null) {
      sb.append(" and c.id =:customerId");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", customerDTO.getShopId()).setString("status", CustomerStatus.ENABLED.toString());
    if (customerDTO.getId() != null) {
      query.setLong("customerId", customerDTO.getId());
    }

    return query;
  }

  public static Query getCustomerRecord(Session session, CustomerRecordDTO customerRecordDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append("from CustomerRecord as cr where cr.shopId =:shopId and (cr.status =:status or cr.status is NULL )");
    if (customerRecordDTO.getCustomerId() != null) {
      sb.append(" and cr.customerId =:customerId");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", customerRecordDTO.getShopId()).setString("status", CustomerStatus.ENABLED.toString());
    if (customerRecordDTO.getCustomerId() != null) {
      query.setLong("customerId", customerRecordDTO.getCustomerId());
    }

    return query;
  }

  public static Query getCustomerRecordByName(Session session, String name) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.name = :name and (cr.status is null or cr.status != :status)")
      .setString("name", name).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopCustomerRecordByName(Session session, Long shopId, String name) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and cr.name = :name and (cr.status is null or cr.status != :status)")
      .setLong("shopId", shopId)
      .setString("name", name).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopCustomerRecordByMobile(Session session, Long shopId, String mobile) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and cr.mobile = :mobile and (cr.status is null or cr.status != :status)")
      .setLong("shopId", shopId)
      .setString("mobile", mobile).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopCustomerRecordByMobile(Session session, Long shopId, String name, String mobile) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and cr.mobile = :mobile and cr.name=:name and (cr.status is null or cr.status != :status)")
      .setLong("shopId", shopId)
      .setString("mobile", mobile).setString("name", name).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopCustomerRecordByLicenceNo(Session session, Long shopId, String licenceNo) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and cr.licenceNo = :licenceNo and (cr.status is null or cr.status != :status)")
      .setLong("shopId", shopId)
      .setString("licenceNo", licenceNo).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopCustomerRecord(Session session, Long shopId, String name, String licenceNo) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and cr.name = :name and cr.licenceNo = :licenceNo and cr.totalReceivable>0 and (cr.status is null or cr.status != :status)")
      .setLong("shopId", shopId)
      .setString("name", name)
      .setString("licenceNo", licenceNo).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getShopCustomerRecordByCustomerId(Session session, Long customerId) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.customerId=:customerId").setLong("customerId", customerId);
  }

  public static Query getShopCustomerRecordByCustomerId(Session session, Long shopId, Long customerId) {
    return session.createQuery("select cr from CustomerRecord as cr where cr.shopId = :shopId and cr.customerId = :customerId ")
      .setLong("shopId", shopId)
      .setLong("customerId", customerId);
  }

  public static Query getCustomerServiceJobByCustomerIdAndVehicleId(Session session, Long shopId, Long customerId, Long vehicleId) {
    String hql = "select csj from CustomerServiceJob as csj where csj.shopId=:shopId and csj.customerId=:customerId and csj.vehicleId=:vehicleId";
    Query query = session.createQuery(hql).setLong("shopId", shopId).setLong("customerId", customerId).setLong("vehicleId", vehicleId);
    return query;
  }

  public static Query getCustomerServiceJobByCustomerVehicleRemindTypes(Session session, Long shopId, Long customerId,
                                                                        Long vehicleId, Set<Long> remindTypes) {
    StringBuffer sb = new StringBuffer();
    sb.append("select csj from CustomerServiceJob as csj where csj.shopId=:shopId and csj.customerId=:customerId ");
    sb.append(" and csj.vehicleId=:vehicleId and csj.remindType in (:remindTypes)");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId)
      .setLong("customerId", customerId)
      .setLong("vehicleId", vehicleId).setParameterList("remindTypes", remindTypes);
    return query;
  }

  public static Query getCustomerServiceJobByAppointServiceId(Session session, Long shopId, Long appointServiceId) {
    String hql = "select csj from CustomerServiceJob as csj where csj.shopId=:shopId and csj.appointServiceId=:appointServiceId";
    return session.createQuery(hql).setLong("shopId", shopId).setLong("appointServiceId", appointServiceId);
  }

  public static Query getCustomerServiceJobByAppointServiceIds(Session session, Long shopId, Set<Long> appointServiceIds) {
    String hql = "select csj from CustomerServiceJob as csj where csj.shopId=:shopId and csj.appointServiceId in (:appointServiceIds)";
    return session.createQuery(hql).setLong("shopId", shopId).setParameterList("appointServiceIds", appointServiceIds);
  }

  public static Query getCustomerServiceJobByVehicleIds(Session session, Set<Long> vehicleIds, Set<Long> remindTypes) {
    StringBuilder sb = new StringBuilder();
    sb.append("select csj from CustomerServiceJob as csj where  csj.vehicleId in (:vehicleIds) ");
    if (CollectionUtils.isNotEmpty(remindTypes)) {
      sb.append(" and csj.remindType in(:remindTypes)");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("vehicleIds", vehicleIds);
    if (CollectionUtils.isNotEmpty(remindTypes)) {
      query.setParameterList("remindTypes", remindTypes);
    }
    return query;
  }

  public static Query getCustomerServiceJobByRemindType(Session session, Long remindType) {
    return session.createQuery("select csj from CustomerServiceJob as csj where csj.remindType=:remindType").setLong("remindType", remindType);
  }

  public static Query getCustomerServiceJobByStateAndPageNoAndPageSize(Session session, Long shopId, List<String> status, Long remindTime, int pageNo, int pageSize) {
    return session.createQuery("select csj from CustomerServiceJob as csj, Customer as c where csj.customerId = c.id and csj.shopId = :shopId " +
      "and csj.remindTime >= :remindTime and csj.status in (:status) and (c.status is null or c.status != :cstatus)order by csj.remindTime")
      .setLong("shopId", shopId)
      .setParameterList("status", status)
      .setLong("remindTime", remindTime)
      .setString("cstatus", CustomerStatus.DISABLED.toString())
      .setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query getCustomerServiceRemindByCondition(Session session, Long shopId, Boolean isOverdue, Boolean hasRemind, Long currentTime, int pageNo, int pageSize) {
    StringBuffer sb = new StringBuffer();
    //数据展示顺序，1、今明过期的，2、已过期的，3、过期时间在明天以后的，4、已提醒的
    if ((hasRemind == null && isOverdue == null) || (isOverdue != null && isOverdue == false)) {
      //今明过期的
      sb.append("select ut.id, ut.customer_id, ut.licence_no, ut.remind_time, ut.status, ut.remind_type, ut.appoint_name, ut.name, ut.contact, ut.mobile ");
      sb.append("from (");
      sb.append("select * from (");
      sb.append("select csj.id, csj.customer_id, v.licence_no, csj.remind_time, csj.status, csj.remind_type, csj.appoint_name, c.name, c.contact, c.mobile ");
      sb.append("from customer_service_job csj, vehicle v, customer c ");
      sb.append("where csj.vehicle_id = v.id ");
      sb.append("and csj.customer_id = c.id ");
      sb.append("and csj.remind_time is not null ");
      sb.append("and (v.status <> 'DISABLED'  or v.status is null) ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append("and csj.shop_id = :shopId ");
      sb.append("and csj.status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("union all ");
      sb.append("select ms.id, c.id, ms.vehicles, ms.deadline, ms.remind_status, " + UserConstant.MEMBER_SERVICE + ", '" + UserConstant.CustomerRemindType.MEMBER_SERVICE + "', c.name, c.contact, c.mobile ");
      sb.append("from member_service ms, member m, customer c ");
      sb.append("where ms.member_id = m.id ");
      sb.append("and m.customer_id = c.id ");
      sb.append("and ms.status = 'ENABLED' ");
      sb.append("and ms.remind_status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("and m.shop_id = :shopId ");
      sb.append("and ms.deadline > 0 ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append(") xt order by xt.remind_time ");
      sb.append(") ut where 1=1 ");
      sb.append("and ut.status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and ut.remind_time >= " + currentTime);
      sb.append(" and ut.remind_time < " + (currentTime + 2 * 24 * 3600 * 1000));
    }

    if ((hasRemind == null && isOverdue == null) || (isOverdue != null && isOverdue == true)) {
      //已过期的
      if (hasRemind == null && isOverdue == null) {
        sb.append(" union all ");
      }
      sb.append(" select ut.id, ut.customer_id, ut.licence_no, ut.remind_time, ut.status, ut.remind_type, ut.appoint_name, ut.name, ut.contact, ut.mobile ");
      sb.append("from (");
      sb.append("select * from (");
      sb.append("select csj.id, csj.customer_id, v.licence_no, csj.remind_time, csj.status, csj.remind_type, csj.appoint_name, c.name, c.contact, c.mobile ");
      sb.append("from customer_service_job csj, vehicle v, customer c ");
      sb.append("where csj.vehicle_id = v.id ");
      sb.append("and csj.customer_id = c.id ");
      sb.append("and csj.remind_time is not null ");
      sb.append("and (v.status <> 'DISABLED'  or v.status is null) ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append("and csj.shop_id = :shopId ");
      sb.append("and csj.status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("union all ");
      sb.append("select ms.id, c.id, ms.vehicles, ms.deadline, ms.remind_status, " + UserConstant.MEMBER_SERVICE + ", '" + UserConstant.CustomerRemindType.MEMBER_SERVICE + "', c.name, c.contact, c.mobile ");
      sb.append("from member_service ms, member m, customer c ");
      sb.append("where ms.member_id = m.id ");
      sb.append("and m.customer_id = c.id ");
      sb.append("and ms.status = 'ENABLED' ");
      sb.append("and ms.remind_status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("and m.shop_id = :shopId ");
      sb.append("and ms.deadline > 0 ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append(") xt order by xt.remind_time ");
      sb.append(") ut where 1=1 ");
      sb.append("and ut.status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and ut.remind_time < " + currentTime);
    }

    if ((hasRemind == null && isOverdue == null) || (isOverdue != null && isOverdue == false)) {
      sb.append(" union all ");
      //过期时间在明天以后的
      sb.append("select ut.id, ut.customer_id, ut.licence_no, ut.remind_time, ut.status, ut.remind_type, ut.appoint_name, ut.name, ut.contact, ut.mobile ");
      sb.append("from (");
      sb.append("select * from (");
      sb.append("select csj.id, csj.customer_id, v.licence_no, csj.remind_time, csj.status, csj.remind_type, csj.appoint_name, c.name, c.contact, c.mobile ");
      sb.append("from customer_service_job csj, vehicle v, customer c ");
      sb.append("where csj.vehicle_id = v.id ");
      sb.append("and csj.customer_id = c.id ");
      sb.append("and csj.remind_time is not null ");
      sb.append("and (v.status <> 'DISABLED'  or v.status is null) ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append("and csj.shop_id = :shopId ");
      sb.append("and csj.status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("union all ");
      sb.append("select ms.id, c.id, ms.vehicles, ms.deadline, ms.remind_status, " + UserConstant.MEMBER_SERVICE + ", '" + UserConstant.CustomerRemindType.MEMBER_SERVICE + "', c.name, c.contact, c.mobile ");
      sb.append("from member_service ms, member m, customer c ");
      sb.append("where ms.member_id = m.id ");
      sb.append("and m.customer_id = c.id ");
      sb.append("and ms.status = 'ENABLED' ");
      sb.append("and ms.remind_status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("and m.shop_id = :shopId ");
      sb.append("and ms.deadline > 0 ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append(") xt order by xt.remind_time ");
      sb.append(") ut where 1=1 ");
      sb.append("and ut.status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and ut.remind_time > " + (currentTime + 2 * 24 * 3600 * 1000));
    }

    if ((hasRemind == null && isOverdue == null) || (hasRemind != null && hasRemind == true)) {
      //已提醒
      if (hasRemind == null && isOverdue == null) {
        sb.append(" union all ");
      }
      sb.append(" select ut.id, ut.customer_id, ut.licence_no, ut.remind_time, ut.status, ut.remind_type, ut.appoint_name, ut.name, ut.contact, ut.mobile ");
      sb.append("from (");
      sb.append("select * from (");
      sb.append("select csj.id, csj.customer_id, v.licence_no, csj.remind_time, csj.status, csj.remind_type, csj.appoint_name, c.name, c.contact, c.mobile ");
      sb.append("from customer_service_job csj, vehicle v, customer c ");
      sb.append("where csj.vehicle_id = v.id ");
      sb.append("and csj.customer_id = c.id ");
      sb.append("and csj.remind_time is not null ");
      sb.append("and (v.status <> 'DISABLED'  or v.status is null) ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append("and csj.shop_id = :shopId ");
      sb.append("and csj.status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("union all ");
      sb.append("select ms.id, c.id, ms.vehicles, ms.deadline, ms.remind_status, " + UserConstant.MEMBER_SERVICE + ", '" + UserConstant.CustomerRemindType.MEMBER_SERVICE + "', c.name, c.contact, c.mobile ");
      sb.append("from member_service ms, member m, customer c ");
      sb.append("where ms.member_id = m.id ");
      sb.append("and m.customer_id = c.id ");
      sb.append("and ms.status = 'ENABLED' ");
      sb.append("and ms.remind_status <> '" + UserConstant.Status.CANCELED + "' ");
      sb.append("and m.shop_id = :shopId ");
      sb.append("and ms.deadline > 0 ");
      sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
      sb.append(") xt order by xt.remind_time ");
      sb.append(") ut where 1=1 ");
      sb.append("and ut.status = '" + UserConstant.Status.REMINDED + "' ");
    }
    return session.createSQLQuery(sb.toString()).setLong("shopId", shopId).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query countCustomerServiceRemindByCondition(Session session, Long shopId, Boolean isOverdue, Boolean hasRemind, Long currentTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(ut.id) ");
    sb.append("from (");
    sb.append("select csj.id, csj.customer_id, v.licence_no, csj.remind_time, csj.status, csj.remind_type, csj.appoint_name, c.name, c.contact, c.mobile ");
    sb.append("from customer_service_job csj, vehicle v, customer c ");
    sb.append("where csj.vehicle_id = v.id ");
    sb.append("and csj.customer_id = c.id ");
    sb.append("and csj.remind_time is not null ");
    sb.append("and (v.status <> 'DISABLED'  or v.status is null) ");
    sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
    sb.append("and csj.shop_id = :shopId ");
    sb.append("and csj.status <> '" + UserConstant.Status.CANCELED + "' ");
    sb.append("union all ");
    sb.append("select ms.id, c.id, ms.vehicles, ms.deadline, ms.remind_status, " + UserConstant.MEMBER_SERVICE + ", '" + UserConstant.CustomerRemindType.MEMBER_SERVICE + "', c.name, c.contact, c.mobile ");
    sb.append("from member_service ms, member m, customer c ");
    sb.append("where ms.member_id = m.id ");
    sb.append("and m.customer_id = c.id ");
    sb.append("and ms.status = 'ENABLED' ");
    sb.append("and ms.remind_status <> '" + UserConstant.Status.CANCELED + "' ");
    sb.append("and m.shop_id = :shopId ");
    sb.append("and ms.deadline > 0 ");
    sb.append("and (c.status <> 'DISABLED'  or c.status is null) ");
    sb.append(") ut where 1=1 ");
    if (hasRemind != null) {
      if (hasRemind == true) {
        sb.append("and ut.status = '" + UserConstant.Status.REMINDED + "' ");
      } else {
        sb.append("and ut.status = '" + UserConstant.Status.ACTIVITY + "' ");
        if (isOverdue == true) {
          sb.append("and ut.remind_time < " + currentTime);
        } else {
          sb.append("and ut.remind_time >= " + currentTime);
        }
      }
    }
    return session.createSQLQuery(sb.toString()).setLong("shopId", shopId);
  }

  public static Query getCustomerServiceJobByCustomerIdAndRemindType(Session session, Long customerId, Long remindType) {
    return session.createQuery("select csj from CustomerServiceJob as csj where csj.customerId = :customerId and csj.remindType = :remindType")
      .setLong("customerId", customerId)
      .setLong("remindType", remindType);
  }

  public static Query getAllCustomerServiceJobByCustomerId(Session session, Long shopId, Long customerId) {
    return session.createQuery("from CustomerServiceJob where shopId=:shopId and customerId = :customerId").setLong("shopId", shopId)
      .setLong("customerId", customerId);

  }

  public static Query getCustomerCardByCustomerIdAndCardType(Session session, long shopId, Long customerId, Long cardType) {
    return session.createQuery("select cc from CustomerCard as cc where cc.shopId=:shopId and cc.customerId = :customerId and cc.cardType = :cardType")
      .setLong("shopId", shopId)
      .setLong("customerId", customerId)
      .setLong("cardType", cardType);
  }

  public static Query getCustomerByCustomerId(Session session, Long customerId) {
    return session.createQuery("select c from Customer c where c.id=:customerId")
      .setLong("customerId", customerId);
  }

  public static Query getCustomerByCustomerIdAndShopId(Session session, Long customerId, Long shopId) {
    return session.createQuery("select c from Customer c where c.id=:customerId and c.shopId =:shopId")
      .setLong("customerId", customerId).setLong("shopId", shopId);
  }

  public static Query getUserVercodeByUserno(Session session, String userno) {

    return session.createQuery("select u from UserVercode as u where u.userNo = :userNo").setString("userNo", userno);

  }

  //客户智能搜索匹配 （汉字）           zhanghcuanlong
  public static Query getCustomer(Session session, String keyword, Long shopId) {
    return session.createSQLQuery("select distinct c.* from customer c left join contact con on c.id = con.customer_id and con.disabled =:disabled " +
      "where (c.status is null or c.status != :status) " +
      "and (c.name like :keyword or con.name like :keyword or con.mobile like:keyword or c.landline like:keyword)  " +
      "and c.shop_id=:shopId order by c.last_update desc")
      .addEntity(Customer.class)
      .setInteger("disabled", ContactConstant.ENABLED)
      .setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setLong("shopId", shopId).setFirstResult(0).setMaxResults(10)
      .setString("status", CustomerStatus.DISABLED.toString());//.setResultTransformer(Transformers.aliasToBean(CustomerDTO.class)).setFirstResult(0).setMaxResults(10));

  }

  // 供应商智能匹配  （汉字）              zhangchuanlong
  public static Query getSupplier(Session session, String keyword, Long shopId) {
//    return session.createQuery("select  s from Supplier  as s where (s.name like '"+keyword+"%' or s.contact like'"+keyword+"%')  and shopId=:shopId").setLong("shopId",shopId).setFirstResult(0).setMaxResults(10);//.setString("name",keyword+"%").setString("contract",keyword+"%").setLong("shopId",shopId);
    return session.createSQLQuery("select distinct s.* from supplier s left join contact con on s.id = con.supplier_id " +
      "where (s.name like:keyword or con.name like :keyword or con.mobile like :keyword or s.landline like:keyword) " +
      "and s.shop_id =:shopId and (s.status=:status or s.status is null) and con.disabled =:disabled order by s.last_update desc")
      .addEntity(Supplier.class)
      .setInteger("disabled", ContactConstant.ENABLED)
      .setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%")
      .setLong("shopId", shopId).setString("status", CustomerStatus.ENABLED.toString()).setFirstResult(0).setMaxResults(10);//.setString("name",keyword+"%").setString("contract",keyword+"%").setLong("shopId",shopId);
  }

  //客户智能搜索匹配 （字母）           zhanghcuanlong
  public static Query getCustomerByZiMu(Session session, String keyword, Long shopId) {
    return session.createSQLQuery("select distinct c.* from customer c left join contact con on c.id = con.customer_id " +
      "where (c.status is null or c.status != :status) and (con.disabled is null or con.disabled =:disabled) " +
      "and (c.name like :keyword or c.first_letters like :keyword or con.name like :keyword or con.mobile like :keyword or c.landline like :keyword)  " +
      "and c.shop_id=:shopId order by c.last_update desc")
      .addEntity(Customer.class)
      .setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setLong("shopId", shopId).setFirstResult(0).setMaxResults(10)
      .setString("status", CustomerStatus.DISABLED.toString())
      .setInteger("disabled", ContactConstant.ENABLED);
  }

  // 供应商智能匹配  （字母）              zhangchuanlong
  public static Query getSupplierByZiMu(Session session, String keyword, Long shopId) {
    return session.createSQLQuery("select distinct s.* from supplier s left join contact con on s.id = con.supplier_id " +
      "where (s.first_letters like:keyword or con.name like:keyword or con.mobile like :keyword or s.landline like:keyword) " +
      "and s.shop_id =:shopId and (s.status is null or s.status != :status) and con.disabled =:disabled order by s.last_update desc")
      .addEntity(Supplier.class)
      .setInteger("disabled", ContactConstant.ENABLED)
      .setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setString("status", CustomerStatus.DISABLED.toString())
      .setString("keyword", "%" + keyword + "%").setString("keyword", "%" + keyword + "%").setLong("shopId", shopId).setFirstResult(0).setMaxResults(10);
  }

  public static Query getAllAgentsByNameORByAgentCode(Session session, String name, String agentCode, int pageNo, int PageSize) {
    StringBuffer hql = new StringBuffer();
    hql.append("from Agents as a ");
    if (name != null && name != "") {
      hql.append("where a.name like :name ");
      return session.createQuery(hql.toString()).setString("name", "%" + name + "%").setFirstResult(pageNo * PageSize).setMaxResults(PageSize);
    } else if (agentCode != null && agentCode != "") {
      hql.append(" where a.agentCode like :agentCode");
      return session.createQuery(hql.toString()).setString("agentCode", "%" + agentCode + "%").setFirstResult(pageNo * PageSize).setMaxResults(PageSize);
    }
    return session.createQuery(hql.toString()).setFirstResult(pageNo * PageSize).setMaxResults(PageSize);
  }

  public static Query getAllSalesManByAgentIdSalesManNameSalesManCode(Session session, Long agentId, String salesManCode, String salesManName, int pageNo, int pageSize) {
    StringBuffer hql = new StringBuffer();
    hql.append("from SalesMan as s where s.agentId=:agentId ");
    if (salesManName != null && salesManName != "") {
      hql.append(" and s.name like :salesManName ");
      return session.createQuery(hql.toString()).setLong("agentId", agentId).setString("salesManName", salesManName).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
    } else if (salesManCode != null && salesManCode != "") {
      hql.append(" and s.salesManCode like :salesManCode ");
      return session.createQuery(hql.toString()).setLong("agentId", agentId).setString("salesManCode", salesManCode).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
    }
    return session.createQuery(hql.toString()).setLong("agentId", agentId).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query countAllAgentsByNameORByAgentCode(Session session, String name, String agentCode) {
    StringBuffer hql = new StringBuffer();
    hql.append("select count(*) from Agents a");
    if (name != null && name != "") {
      hql.append("where a.name like :name");
      return session.createQuery(hql.toString()).setString("name", "%" + name + "%");
    } else if (agentCode != null && agentCode != "") {
      hql.append("where a.agentCode like :agentCode");
      return session.createQuery(hql.toString()).setString("agentCode", "%" + agentCode + "%");
    }
    return session.createQuery(hql.toString());
  }

  public static Query countSalesManByAgentIdSalesManNameSalesManCode(Session session, Long agentId, String salesManCode, String salesManName) {
    StringBuffer hql = new StringBuffer();
    hql.append("select count(*) from SalesMan as s where s.agentId=:agentId ");
    if (salesManName != null && salesManName != "") {
      hql.append(" and s.name like :salesManName ");
      return session.createQuery(hql.toString()).setLong("agentId", agentId).setString("salesManName", salesManName);
    } else if (salesManCode != null && salesManCode != "") {
      hql.append(" and s.salesManCode like :salesManCode ");
      return session.createQuery(hql.toString()).setLong("agentId", agentId).setString("salesManCode", salesManCode);
    }

    return session.createQuery(hql.toString()).setLong("agentId", agentId);
  }

  public static Query getAgentTargetByMonthAndYearAndAgentId(Session session, int month, String year, Long agentId) {
    String hql = "from AgentTargt a where a.month=:month and a.year=:year and a.agentId=:agentId";
    return session.createQuery(hql).setInteger("month", month).setString("year", year).setLong("agentId", agentId);
  }

  public static Query getSalesManTargetByMonthYearSalesManId(Session session, int month, String year, Long salesManId) {
    String hql = "from SalesManTarget sm where sm.month=:month and sm.year=:year and sm.salesManId=:salesManId";
    return session.createQuery(hql).setInteger("month", month).setString("year", year).setLong("salesManId", salesManId);
  }

  public static SQLQuery monthTarget(Session session, String year) {
    String sql = "SELECT a.year YEAR,a.month MONTH,SUM(a.month_target) totalMonthTarget FROM agent_targt a GROUP BY a.month ,a.year HAVING a.year=" + year;
    return (SQLQuery) session.createSQLQuery(sql)
      .addScalar("year", StandardBasicTypes.STRING)
      .addScalar("month", StandardBasicTypes.INTEGER)
      .addScalar("totalMonthTarget", StandardBasicTypes.DOUBLE)
      .setResultTransformer(Transformers.aliasToBean(totalMonthTarget.class));
  }

  public static Query getMonthTargetByAgentIdAndYear(Session session, String year, Long agentId) {
    String hql = "from AgentTargt a where a.year=:year and a.agentId=:agentId";
    return session.createQuery(hql).setString("year", year).setLong("agentId", agentId);
  }

  public static Query getCustomerRecordByKey(Session session, Long shopId, String key, int rowStart, int pageSize) {
    String sql = "SELECT DISTINCT cr.* FROM customer_record cr LEFT JOIN contact c ON cr.customer_id = c.customer_id " +
      "WHERE cr.shop_id = :shopId AND (cr.status IS NULL OR cr.status <> :status) ";
    if (key != null && key.length() > 0) {
      sql += " and (cr.name like :key or c.mobile like :key or cr.company like :key or c.name like :key " +
        "or cr.customer_id in (select customer_id from member as m where m.member_no = :memberNo))";
    }
    sql += " ORDER BY last_date DESC limit :rowStart, :pageSize ";
    Query query = session.createSQLQuery(sql).addEntity(CustomerRecord.class)
      .setLong("shopId", shopId).setInteger("rowStart", rowStart).setInteger("pageSize", pageSize).setString("status", CustomerStatus.DISABLED.toString());
    if (key != null && key.length() > 0) {
      query.setString("key", "%" + key + "%").setString("memberNo", key);
    }
    return query;
  }


  public static Query countCustomerRecordByKey(Session session, Long shopId, String key) {
    String sql = "SELECT count(DISTINCT cr.id) FROM customer_record cr LEFT JOIN contact c ON cr.customer_id = c.customer_id " +
      "WHERE cr.shop_id = :shopId AND (cr.status IS NULL OR cr.status <> :status) ";
    if (key != null && key.length() > 0) {
      sql += " and (cr.name like :key or c.mobile like :key or cr.company like :key or c.name like :key " +
        "or cr.customer_id in (select customer_id from member as m where m.member_no = :memberNo))";
    }
    Query query = session.createSQLQuery(sql)
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
    if (key != null && key.length() > 0) {
      query.setString("key", "%" + key + "%").setString("memberNo", key);
    }
    return query;
  }

  public static SQLQuery getCustomerVehicleCount(Session session, List<Long> customerIds) {
    String sql = "SELECT customer_id customerId, COUNT(vehicle_id) COUNT FROM customer_vehicle WHERE customer_id IN (:customerIds) GROUP BY customer_id ";
    SQLQuery query = (SQLQuery) session.createSQLQuery(sql).setParameterList("customerIds", customerIds)
      .setResultTransformer(Transformers.aliasToBean(CustomerVehicleNumber.class));
    return query;
  }

  public static Query getCustomerByIds(Session session, List<Long> customerIds) {
    String sql = "select cus From Customer as cus where id in (:customerIds) ";
    Query query = session.createQuery(sql).setParameterList("customerIds", customerIds);
    return query;
  }

  public static Query getCustomerByIds(Session session, Long... customerIds) {
    String sql = "select cus From Customer as cus where id in (:customerIds) ";
    Query query = session.createQuery(sql).setParameterList("customerIds", customerIds);
    return query;
  }

  public static Query getCustomerById(Session session, Long shopId, Long customerId) {
    String sql = "from Customer  where shopId=:shopId and id =:customerId and (status = :status or status is NULL) ";
    return session.createQuery(sql).setLong("shopId", shopId).setLong("customerId", customerId).setString("status", CustomerStatus.ENABLED.toString());
  }

  public static Query getCustomerCardByCustomerIdsAndCardType(Session session, Long shopId, List<Long> customerIds, Long cardType) {
    String sql = "select cc From CustomerCard as cc where shopId = :shopId and cardType = :cardType and customerId in (:customerIds) ";
    Query query = session.createQuery(sql).setLong("shopId", shopId).setLong("cardType", cardType)
      .setParameterList("customerIds", customerIds);
    return query;
  }

  public static Query getCustomerByTelephone(Session session, Long shopId, String telephone) {
    return session.createQuery("select c from Customer as c where c.shopId = :shopId and (c.landLine = :landLine or c.landLineSecond = :landLine or c.landLineThird = :landLine) and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId)
      .setString("landLine", telephone).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getSupplierByTelephone(Session session, Long shopId, String telephone) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId and (s.landLine = :landLine or s.landLineSecond = :landLine or s.landLineThird = :landLine) and (s.status is null or s.status != :status)")
      .setLong("shopId", shopId)
      .setString("landLine", telephone).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getAgentByAgentCode(Session session, String agentCode) {
    return session.createQuery("select s from Agents as s where s.agentCode=:agentCode").setString("agentCode", agentCode);
  }

  public static Query getCustomerByBirth(Session session, Long birth) {
    return session.createQuery("select c from Customer as c where c.birthday=:birth and (c.status is null or c.status != :status)").setLong("birth", birth).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getPlans(Session session, Long shopId, int pageNo, int pageSize, Long now, String type) {

    String sql = "";
    if (StringUtils.isBlank(type) || TxnConstant.SHOP_PLAN_TOTALROWS.equals(type)) {
      sql = "SELECT * FROM (SELECT * FROM shop_plan AS s WHERE s.shop_id =:shopId AND s.status =:activity AND s.remind_time >=:remindTime ORDER BY s.remind_time ASC) AS a" +
        " UNION SELECT * FROM (SELECT * FROM shop_plan AS s1 WHERE s1.shop_id =:shopId AND s1.status =:activity AND s1.remind_time <:remindTime ORDER BY s1.remind_time DESC) AS b" +
        " UNION SELECT * FROM (SELECT * FROM shop_plan AS s2 WHERE s2.shop_id =:shopId AND s2.status =:reminded ORDER BY s2.remind_time DESC) AS C";
    } else if (TxnConstant.SHOP_PLAN_REMINDED.equals(type)) {
      sql = "SELECT * FROM shop_plan AS s WHERE s.shop_id =:shopId AND s.status =:reminded ORDER BY s.remind_time DESC";
    } else if (TxnConstant.SHOP_PLAN_ACTIVITY_EXPIRED.equals(type)) {
      sql = "SELECT * FROM shop_plan AS s WHERE s.shop_id =:shopId AND s.status =:activity AND s.remind_time <:remindTime ORDER BY s.remind_time DESC";
    } else if (TxnConstant.SHOP_PLAN_ACTIVITY_NO_EXPIRED.equals(type)) {
      sql = "SELECT * FROM shop_plan AS s WHERE s.shop_id =:shopId AND s.status =:activity AND s.remind_time >=:remindTime ORDER BY s.remind_time ASC";
    }

    Query q = session.createSQLQuery(sql).addEntity(ShopPlan.class)
      .setLong("shopId", shopId);
    if (StringUtils.isBlank(type) || TxnConstant.SHOP_PLAN_TOTALROWS.equals(type)) {
      q.setString("activity", PlansRemindStatus.activity.toString()).setString("reminded", PlansRemindStatus.reminded.toString())
        .setLong("remindTime", now);
    } else if (TxnConstant.SHOP_PLAN_REMINDED.equals(type)) {
      q.setString("reminded", PlansRemindStatus.reminded.toString());
    } else if (TxnConstant.SHOP_PLAN_ACTIVITY_EXPIRED.equals(type)) {
      q.setString("activity", PlansRemindStatus.activity.toString())
        .setLong("remindTime", now);
    } else if (TxnConstant.SHOP_PLAN_ACTIVITY_NO_EXPIRED.equals(type)) {
      q.setString("activity", PlansRemindStatus.activity.toString())
        .setLong("remindTime", now);
    }

    return q.setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query countPlans(Session session, Long shopId, List<PlansRemindStatus> stat, Long remindTime, String type) {

    String sql = "";

    if (StringUtils.isBlank(type) || TxnConstant.SHOP_PLAN_TOTALROWS.equals(type)) {
      sql = "select count(*) from ShopPlan s where s.shopId=:shopId and s.status in (:status)";
    } else if (TxnConstant.SHOP_PLAN_REMINDED.equals(type)) {
      sql = "select count(*) from ShopPlan s where s.shopId=:shopId and s.status =:status";
    } else if (TxnConstant.SHOP_PLAN_ACTIVITY_EXPIRED.equals(type)) {
      sql = "select count(*) from ShopPlan s where s.shopId=:shopId and s.status=:status and s.remindTime<:remindTime";
    } else if (TxnConstant.SHOP_PLAN_ACTIVITY_NO_EXPIRED.equals(type)) {
      sql = "select count(*) from ShopPlan s where s.shopId=:shopId and s.status=:status and s.remindTime>=:remindTime";
    }
    Query q = session.createQuery(sql).setLong("shopId", shopId);

    if (StringUtils.isBlank(type) || TxnConstant.SHOP_PLAN_TOTALROWS.equals(type)) {
      q.setParameterList("status", stat);
    } else if (TxnConstant.SHOP_PLAN_REMINDED.equals(type)) {
      q.setParameter("status", PlansRemindStatus.reminded);
    } else if (TxnConstant.SHOP_PLAN_ACTIVITY_EXPIRED.equals(type) || TxnConstant.SHOP_PLAN_ACTIVITY_NO_EXPIRED.equals(type)) {
      q.setParameter("status", PlansRemindStatus.activity).setLong("remindTime", remindTime);
    }

    return q;
  }

  public static Query getCustomersMobilesByShopId(Session session, Long shopId) {
    return session.createQuery("select c.mobile from Customer as c where c.shopId=:shopId and (c.status is null or c.status != :status)").setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getCustomersByShopIdSendInvitationCode(Session session, Long shopId, long startId, int pageSize, Long createTime) {
    StringBuilder sql = new StringBuilder();
    sql.append("select c from Customer as c ,Contact con where con.customerId =c.id and c.shopId=:shopId and ( c.relationType =:relationType or c.relationType is null) ");
    sql = SQLBuilder.isNotEmpty(sql.append(" and "), "con.mobile");
    sql.append(" and c.invitationCodeSendDate is null and (c.status is null or c.status != :status) and con.disabled =:disabled and c.id>:startId ");
    if (createTime != null) {
      sql.append(" and c.creationDate <:createTime");
    }
    sql.append(" order by c.id asc ");

    Query query = session.createQuery(sql.toString());
    if (createTime != null) {
      query.setLong("createTime", createTime);
    }
    query.setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setString("status", CustomerStatus.DISABLED.toString()).setParameter("relationType", RelationTypes.UNRELATED).setMaxResults(pageSize);
    return query.setLong("startId", startId);
  }

  public static Query getSuppliersByShopIdSendInvitationCode(Session session, Long shopId, long startId, int pageSize, Long createTime) {
    StringBuilder sql = new StringBuilder();
    sql.append("select s from Supplier as s ,Contact c where c.supplierId =s.id and s.shopId=:shopId and (s.relationType =:relationType or s.relationType is null)");
    sql = SQLBuilder.isNotEmpty(sql.append(" and "), "c.mobile");
    sql.append(" and s.invitationCodeSendDate is null and (s.status is null or s.status != :status) and c.disabled =:disabled and s.id>:startId ");
    if (createTime != null) {
      sql.append(" and s.creationDate <:createTime");
    }
    sql.append(" order by s.id asc ");
    Query query = session.createQuery(sql.toString());
    if (createTime != null) {
      query.setLong("createTime", createTime);
    }
    query.setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setString("status", CustomerStatus.DISABLED.toString()).setParameter("relationType", RelationTypes.UNRELATED).setMaxResults(pageSize);
    return query.setLong("startId", startId);
  }

  public static Query getCustomersMobilesByCustomerIds(Session session, Long shopId, List<Long> customerIds) {
    return session.createQuery("select mobile From Customer c where c.id in (:customerIds) and c.shopId=:shopId").setParameterList("customerIds", customerIds)
      .setLong("shopId", shopId);
  }

  /**
   * 按时间区间查询customer数量
   *
   * @param session
   * @param shopId
   * @return
   * @author zhangchuanlong
   */
  public static Query countShopCustomerByTime(Session session, Long shopId, Long startTime, Long endTime) {
    return session.createQuery("select count(*) from Customer as c where c.shopId = :shopId and c.creationDate>=:startTime and c.creationDate<=:endTime and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime).setString("status", CustomerStatus.DISABLED.toString());
  }


  /**
   * 对当天，昨天，当月客户记录查询按照属性排序
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @param pager
   * @param sort      排序对象
   * @return
   * @author zhangchuanlong
   */
  public static Query getCustomerRecordByTime(Session session, Long shopId, Long startTime, Long endTime, Pager pager, Sort sort) {
    StringBuffer hql = new StringBuffer();
    hql.append("from  CustomerRecord as c where c.shopId=:shopId and c.creationDate>=:startTime and c.creationDate<=:endTime and (cr.status is null or cr.status != :status)");
    if (sort != null) {
      hql.append(sort.toOrderString());
    }
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime).setString("status", CustomerStatus.DISABLED.toString()).setFirstResult((pager.getCurrentPage() - 1) * pager.getPageSize()).setMaxResults(pager.getPageSize());
  }

  /**
   * 对所有客户记录查询按照属性排序
   *
   * @param session
   * @param shopId
   * @param pager
   * @param sort
   * @return
   * @author zhangchuanlong
   */
  public static Query getShopCustomerRecord(Session session, Long shopId, Pager pager, Sort sort) {
    StringBuffer hql = new StringBuffer();
    hql.append("select cr from CustomerRecord as cr where cr.shopId = :shopId and (cr.status is null or cr.status != :status)");
    if (sort != null) {
      hql.append(sort.toOrderString());
    }
    return session.createQuery(hql.toString())
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString())
      .setFirstResult((pager.getCurrentPage() - 1) * pager.getPageSize())
      .setMaxResults(pager.getPageSize());
  }

  /**
   * 一段时间区间内车牌号
   *
   * @param session
   * @param shopId
   * @param fromTime
   * @param endTime
   * @return
   * @author zhangchuanlong
   */
  public static Query getVehicleLicenceNos(Session session, Long shopId, Long fromTime, Long endTime) {
    StringBuffer hql = new StringBuffer();
    hql.append("select v from  Vehicle as v where v.shopId=:shopId and v.creationDate>=:fromTime and v.creationDate<=:endTime and (v.status is null or v.status != :status)");
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("fromTime", fromTime).setLong("endTime", endTime).setString("status", VehicleStatus.DISABLED.toString());
  }

  public static Query getRoleByName(Session session, String name) {
    return session.createQuery("select r from Role as r where r.name=:name order by r.lastModified desc").setString("name", name);
  }

  public static Query getResource(Session session, String name, String type) {
    return session.createQuery("select r from Resource as r where r.name=:name and r.type=:type order by r.lastModified desc")
      .setString("name", name).setString("type", type);
  }

  public static Query getResourceByValue(Session session, String value) {
    return session.createQuery("select r from Resource as r where r.value=:value ")
      .setString("value", value);
  }

  public static Query getResourceByValue(Session session, String value, Long resourceId) {
    String hql = "from Resource where value=:value ";
    if (resourceId != null) {
      hql += " and id !=:resourceId";
    }
    Query q = session.createQuery(hql).setString("value", value);
    if (resourceId != null) {
      q.setLong("resourceId", resourceId);
    }
    return q;
  }

  public static Query getResourceByName(Session session, String name, Long resourceId) {
    String hql = "from Resource where name=:name ";
    if (resourceId != null) {
      hql += " and id !=:resourceId";
    }
    Query q = session.createQuery(hql).setString("name", name);
    if (resourceId != null) {
      q.setLong("resourceId", resourceId);
    }
    return q;
  }


  public static Query getModuleByName(Session session, String name) {
    return session.createQuery("select m from Module m where m.name=:name").setString("name", name);
  }

  public static Query getUniqueUserGroupByName(Session session, String name, Long shopId, Long shopVersionId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ug from UserGroup ug,UserGroupShop ugs where ugs.userGroupId=ug.id and ug.name =:name");
    if (shopId != null) {
      sql.append(" and ugs.shopId=:shopId ");
    } else {
      sql.append(" and ugs.shopVersionId = :shopVersionId ");
    }
    Query query = session.createQuery(sql.toString()).setString("name", name);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    } else {
      query.setLong("shopVersionId", shopVersionId);
    }
    return query;
  }

  public static Query getUserGroupByName(Session session, String name, boolean isFuzzyMatching, Long shopId, Long shopVersionId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ug from UserGroup ug,UserGroupShop ugs where ugs.userGroupId=ug.id and (ugs.shopId=:shopId or ugs.shopVersionId = :shopVersionId) and ug.status != :status");
    if (isFuzzyMatching) {
      sql.append(" and ug.name like:name ");
    } else {
      sql.append(" and ug.name =:name ");
    }
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId).setLong("shopVersionId", shopVersionId).setString("status", Status.deleted.name());
    if (isFuzzyMatching) {
      query.setString("name", "%" + name + "%");
    } else {
      query.setString("name", name);
    }
    return query;
  }

  public static Query getUserByShopId(Session session, Long shopId) {
    return session.createQuery("select new User(u,ugu.userGroupId) from User u,UserGroupUser ugu where ugu.userId=u.id and u.shopId=:shopId").setLong("shopId", shopId);
  }

  public static Query getUserIdsByShopId(Session session, Long shopId) {
    return session.createQuery("select u.id from User u where u.shopId=:shopId").setLong("shopId", shopId);
  }

  public static Query getUser(Session session, Long shopId, Long userId) {
    return session.createQuery("select new User(u,ugu.userGroupId) from User u,UserGroupUser ugu where ugu.userId=u.id and u.shopId=:shopId and u.id=:userId")
      .setLong("userId", userId).setLong("shopId", shopId);
  }

  public static Query getUser(Session session, Long shopId, String name) {
    return session.createQuery("select u from User u where u.shopId=:shopId and u.name=:name")
      .setString("name", name).setLong("shopId", shopId);
  }

  public static Query getModule(Session session, SystemType systemType) {
    return session.createQuery("select m from Module m where m.type =:type and m.parentId is not null order by m.creationDate").setString("type", systemType.toString());
  }

  public static Query getAllModule(Session session, SystemType systemType) {
    return session.createQuery("select m from Module m where m.type =:type").setString("type", systemType.toString());
  }

  public static Query getRolesByModuleIds(Session session, Long... ids) {
    return session.createQuery("from Role r where r.moduleId in:ids").setParameterList("ids", ids);
  }

  public static Query getUserGroupRole(Session session, Long userGroupId) {
    return session.createQuery("select ugr from UserGroupRole ugr where ugr.userGroupId=:userGroupId order by ugr.lastModified desc")
      .setLong("userGroupId", userGroupId);
  }

  public static Query getResourceByRoleId(Session session, Long roleId) {
    StringBuilder builder = new StringBuilder();
    builder.append("select re from RoleResource r,Resource re where r.resourceId=re.id ");
    if (roleId != null)
      builder.append(" and r.roleId=:roleId");
    Query query = session.createQuery(builder.toString());
    if (roleId != null)
      query.setLong("roleId", roleId);
    return query;
  }

  public static Query getRoleResourceByRoleId(Session session, Long roleId) {
    StringBuilder builder = new StringBuilder();
    builder.append("select r from RoleResource r");
    if (roleId != null)
      builder.append(" where r.roleId=:roleId");
    builder.append(" order by r.lastModified desc ");
    Query query = session.createQuery(builder.toString());
    if (roleId != null)
      query.setLong("roleId", roleId);
    return query;
  }

  public static Query getRoleResourceByRoleIdAndResourceId(Session session, Long roleId, Long resourceId) {
    return session.createQuery("select rr from RoleResource rr where rr.resourceId=:resourceId and rr.roleId=:roleId")
      .setLong("roleId", roleId).setLong("resourceId", resourceId);
  }

  public static Query deleteUserGroupUser(Session session, Long userId, Long userGroupId) {
    return session.createQuery("delete from UserGroupUser u  where u.userGroupId=:userGroupId and u.userId=:userId")
      .setLong("userId", userId).setLong("userGroupId", userGroupId);
  }

  public static Query deleteUserGroupRole(Session session, Long userGroupId, Long roleId) {
    return session.createQuery("delete from UserGroupRole u  where u.userGroupId=:userGroupId and u.roleId=:roleId")
      .setLong("roleId", roleId).setLong("userGroupId", userGroupId);
  }

  public static Query deleteUserGroupRole(Session session, Long userGroupId) {
    return session.createQuery("delete from UserGroupRole u  where u.userGroupId=:userGroupId").setLong("userGroupId", userGroupId);
  }

  public static Query countUserByUserNo(Session session, String userNo) {
    return session.createQuery("select count(*) from User u  where u.userNo=:userNo and u.status != :status").setString("userNo", userNo).setString("status", Status.deleted.name());
  }

  public static Query countUserByUserNo(Session session, String userNo, Long userId) {
    return session.createQuery("select count(*) from User u  where u.userNo=:userNo and u.id != :userId and (u.status != :status or u.status is null or u.status='')").setString("userNo", userNo).setString("status", Status.deleted.name()).setLong("userId", userId);
  }

  public static Query getResourceByUserGroupId(Session session, Long userGroupId, String type) {
    return session.createQuery("select r from Resource r,RoleResource rr,UserGroupRole ugr where r.id=rr.resourceId and rr.roleId=ugr.roleId and ugr.userGroupId=:userGroupId and r.type=:type")
      .setLong("userGroupId", userGroupId).setString("type", type);
  }

  public static Query getResourceByUserGroupId(Session session, Long userGroupId) {
    return session.createQuery("select distinct(r.id),r from Resource r,RoleResource rr,UserGroupRole ugr where r.id=rr.resourceId and rr.roleId=ugr.roleId and ugr.userGroupId=:userGroupId")
      .setLong("userGroupId", userGroupId);
  }

  public static Query getUserByUserGroupName(Session session, String name) {
    return session.createQuery("select new User(u,ug.id) from User u,UserGroup ug,UserGroupUser ugu where u.id=ugu.userId and ugu.userGroupId=ug.id and ug.name=:name")
      .setString("name", name);
  }

  public static Query getUserGroupRoleByUserGroupId(Session session, Long userGroupId) {
    return session.createQuery("select u from UserGroupRole u where u.userGroupId=:userGroupId").setLong("userGroupId", userGroupId);
  }


  //add by weilf 2012-08-16
  public static Query deleteVehicleCustomer(Session session, Long vehicleId) {
    return session.createQuery("delete from CustomerVehicle cv  where cv.vehicleId=:vehicleId").setLong("vehicleId", vehicleId);
  }

  public static Query getCustomerCardByCardName(Session session, Long shopId, String name) {
    //TODO 以后存入solr中，性能问题
    return session.createQuery("select mc from MemberCard mc where mc.shopId =:shopId and upper(REPLACE(mc.name, ' ','')) =:name")
      .setLong("shopId", shopId).setString("name", StringUtil.toTrimAndUpperCase(name));
  }

  public static Query deleteMemberCardServiceByMemberCardId(Session session, Long memberCardId) {
    return session.createQuery("delete from MemberCardService mcs where mcs.memberCardId =:memberCardId")
      .setLong("memberCardId", memberCardId);
  }

  public static Query getMemberCardByShopIdAndNames(Session session, Long shopId, List<String> names) {
    return session.createQuery("select mc from MemberCard mc where mc.shopId =:shopId and mc.name in :names and mc.status =:status")
      .setLong("shopId", shopId).setParameterList("names", names).setString("status", TxnConstant.CARD_STATUS_ENABLED);
  }

  public static Query getMemberCardByShopId(Session session, Long shopId) {
    return session.createQuery("select mc from MemberCard mc where mc.shopId =:shopId and mc.status =:status")
      .setLong("shopId", shopId).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getMemberCardByShopIdAndName(Session session, Long shopId, String name) {
    return session.createQuery("select mc from MemberCard mc where mc.shopId =:shopId and mc.name =:name")
      .setLong("shopId", shopId).setString("name", name);
  }

  public static Query getMemberCardServiceByMemberCardId(Session session, Long memberCardId) {
    return session.createQuery("select mcs from MemberCardService mcs where mcs.memberCardId =:memberCardId")
      .setLong("memberCardId", memberCardId);
  }

  public static Query getMemberCardServiceDTOByMemberCardIdAndStatus(Session session, Long memberCardId) {
    return session.createQuery("select mcs from MemberCardService mcs where mcs.memberCardId =:memberCardId and mcs.status= :status")
      .setLong("memberCardId", memberCardId).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getMemberCardServiceByMemberCardIdAndServiceIdAndStatus(Session session, Long memberCardId, Long serviceId) {
    return session.createQuery("select mcs from MemberCardService mcs where mcs.memberCardId =:memberCardId and mcs.status= :status and mcs.serviceId =:serviceId")
      .setLong("memberCardId", memberCardId).setParameter("status", MemberStatus.ENABLED).setLong("serviceId", serviceId);
  }

  public static Query getMemberByCustomerId(Session session, Long shopId, Long customerId) {
    return session.createQuery("select m from Member m where m.shopId =:shopId and m.customerId =:customerId and m.status ='ENABLED'")
      .setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getMemberServicesByMemberId(Session session, Long memberId) {
    return session.createQuery("select ms from MemberService ms where ms.memberId =:memberId and ms.status=:status")
      .setLong("memberId", memberId).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getMemberService(Session session, Long memberId, Long serviceId) {
    return session.createQuery("select ms from MemberService ms where ms.memberId =:memberId and ms.serviceId =:serviceId")
      .setLong("memberId", memberId).setLong("serviceId", serviceId);
  }

  public static Query getMemberCardById(Session session, Long shopId, Long id) {
    return session.createQuery("select mc from MemberCard mc where mc.id =:id and mc.shopId =:shopId")
      .setLong("id", id).setLong("shopId", shopId);
  }

  public static Query getMemberServicesByMemberIdAndStatusAndDeadline(Session session, Long memberId) throws Exception {
    Long date = System.currentTimeMillis();
    String dateStr = DateUtil.dateLongToStr(date, DateUtil.YEAR_MONTH_DATE);
    date = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, dateStr);
    return session.createQuery("select ms from MemberService ms where ms.memberId =:memberId and (ms.deadline > :date or ms.deadline = -1) and ms.status =:status")
      .setLong("memberId", memberId).setLong("date", date).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getMemberServiceByMemberIdAndServiceIdAndStatus(Session session, Long memberId, Long serviceId) throws Exception {
    return session.createQuery("select ms from MemberService ms where ms.memberId =:memberId and ms.status =:status and ms.serviceId =:serviceId")
      .setLong("memberId", memberId).setLong("serviceId", serviceId).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getMemberByShopIdAndMemberNo(Session session, Long shopId, String memberNo) {
    return session.createQuery("select m from Member m where m.shopId = :shopId and m.memberNo = :memberNo order by m.status desc")
      .setLong("shopId", shopId).setString("memberNo", memberNo);
  }

  public static Query getEnabledMemberByShopIdAndMemberNo(Session session, Long shopId, String memberNo) {
    return session.createQuery("select m from Member m where m.shopId = :shopId and m.memberNo = :memberNo and m.status =:status order by m.status desc")
      .setLong("shopId", shopId).setString("memberNo", memberNo).setParameter("status", MemberStatus.ENABLED);
  }


  public static Query getSaleManByShopIdAndOnJob(Session session, Long shopId, String keyWord) {
    StringBuffer sb = new StringBuffer("select sm from SalesMan sm where sm.shopId = :shopId and sm.status <> :status and sm.status <> :deleteStatus");
    if (StringUtils.isNotBlank(keyWord)) {
      sb.append(" and sm.name like :keyWord");
    }
    sb.append(" group by name");
    Query q = session.createQuery(sb.toString());
    q.setLong("shopId", shopId).setParameter("status", SalesManStatus.DEMISSION).setParameter("deleteStatus", SalesManStatus.DELETED);
    if (StringUtils.isNotBlank(keyWord)) {
      q.setString("keyWord", "%" + keyWord.trim() + "%");
    }
    return q;
  }

  public static Query getSaleManDTOById(Session session, Long shopId, Long id) {
    return session.createQuery("select sm from SalesMan sm where sm.shopId = :shopId and sm.id=:id and sm.status <> :status")
      .setLong("shopId", shopId).setLong("id", id).setParameter("status", SalesManStatus.DEMISSION);
  }

  public static Query getAllWashCard(Session session) {
    return session.createQuery("from CustomerCard c");
  }

  public static Query getMemberDTOByMemberNo(Session session, Long shopId, String memberNo) {
    return session.createQuery("select m from Member m where m.shopId =:shopId and m.memberNo =:memberNo")
      .setLong("shopId", shopId).setString("memberNo", memberNo);
  }

  public static Query getSalesManDTOListByShopId(Session session, long shopId, SalesManStatus status, Pager pager) {
    Query query = null;
    if (status == null) {
      query = session.createQuery("from SalesMan s where s.shopId =:shopId ")
        .setLong("shopId", shopId);
    } else {
      query = session.createQuery("from SalesMan s where s.shopId =:shopId and s.status =:status")
        .setLong("shopId", shopId).setParameter("status", status);
    }
    if (pager != null) {
      query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }
    return query;
  }

  @Deprecated
  public static Query getAllSalesManForInitPermission(Session session) {
    return session.createQuery("select s from SalesMan s where s.department is not empty and s.department is not null");
  }

  @Deprecated
  public static Query getAllSalesMan(Session session) {
    return session.createQuery("select s from SalesMan s ");
  }

  public static Query countSalesManByShopIdAndStatus(Session session, long shopId, SalesManStatus salesManStatus) {
    if (salesManStatus != null) {
      return session.createQuery(" select count(*) from SalesMan s where s.shopId =:shopId and s.status =:status")
        .setLong("shopId", shopId).setParameter("status", salesManStatus);
    } else {
      return session.createQuery(" select count(*) from SalesMan s where s.shopId =:shopId")
        .setLong("shopId", shopId);
    }

  }

  public static Query getSalesManDTOByCodeOrName(Session session, String salesManCode, String name, long shopId) {
    //TODO 以后存入solr中，性能问题
    if (!StringUtil.isEmpty(salesManCode)) {
      return session.createQuery(" from SalesMan s where s.shopId =:shopId and upper(REPLACE(s.salesManCode,' ','')) =:salesManCode and (status is null or status != :status)")
        .setLong("shopId", shopId).setString("salesManCode", StringUtil.toTrimAndUpperCase(salesManCode)).setParameter("status", SalesManStatus.DELETED);
    } else if (!StringUtil.isEmpty(name)) {
      return session.createQuery(" from SalesMan s where s.shopId =:shopId and upper(REPLACE(s.name,' ','')) =:name and (status is null or status != :status) ")
        .setLong("shopId", shopId).setString("name", StringUtil.toTrimAndUpperCase(name)).setParameter("status", SalesManStatus.DELETED);
    } else {
      return null;
    }

  }

  public static Query getAllUser(Session session) {
    return session.createQuery("from User u order by u.creationDate ");
  }

  public static Query getAllMemberServiceByMemberId(Session session, Long memberId) throws Exception {
    return session.createQuery("select ms from MemberService ms where ms.memberId =:memberId and ms.status =:status")
      .setLong("memberId", memberId).setParameter("status", MemberStatus.ENABLED);
  }


  public static Query getMemberDTOById(Session session, Long shopId, Long memberId) {
    return session.createQuery("from Member m where m.shopId = :shopId and m.id = :memberId")
      .setLong("shopId", shopId).setLong("memberId", memberId);
  }

  public static Query countMemberCardByShopId(Session session, Long shopId) {
    return session.createQuery("select count(mc) from MemberCard mc where mc.shopId = :shopId and mc.status = :status")
      .setLong("shopId", shopId).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getAllShopUser(Session session) {
    return session.createQuery("from User u where u.shopId is not null ");
  }

  public static Query getMemberServiceByServiceId(Session session, Long serviceId) {
    return session.createQuery("from MemberService ms where ms.serviceId = :serviceId and ms.status = :status")
      .setLong("serviceId", serviceId).setParameter("status", ServiceStatus.ENABLED);
  }

  public static Query getMemberCardServiceByServiceId(Session session, Long serviceId) {
    return session.createQuery("from MemberCardService msc where msc.serviceId = :serviceId and msc.status=:status")
      .setLong("serviceId", serviceId).setParameter("status", ServiceStatus.ENABLED);
  }

  public static Query getMemberByIds(Session session, Long shopId, Set<Long> customerIds) {
    if (shopId == null) {
      return session.createQuery("from Member m where m.customerId in (:customerIds) ").setParameterList("customerIds", customerIds);
    }
    return session.createQuery("from Member m where m.customerId in (:customerIds) and m.shopId =:shopId").setLong("shopId", shopId).setParameterList("customerIds", customerIds);
  }

  public static Query getMemberCardService(Session session, Long memberCardId, Long serviceId) {
    return session.createQuery("from MemberCardService msc where msc.serviceId = :serviceId and msc.memberCardId =:memberCardId ")
      .setLong("serviceId", serviceId).setLong("memberCardId", memberCardId);
  }

  public static Query getCustomerByIdSet(Session session, Long shopId, Set<Long> customerIds) {
    StringBuffer sb = new StringBuffer("from Customer where id in (:customerIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("customerIds", customerIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getMemberByCustomerIdSet(Session session, Long shopId, Set<Long> customerIds) {
    StringBuffer sb = new StringBuffer("from Member where customerId in (:customerIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    sb.append(" and status =:memberStatus");
    Query query = session.createQuery(sb.toString())
      .setParameterList("customerIds", customerIds)
      .setParameter("memberStatus", MemberStatus.ENABLED);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getSupplierByIdSet(Session session, Long shopId, Set<Long> supplierIds) {
    StringBuffer sb = new StringBuffer("from Supplier where id in (:supplierIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("supplierIds", supplierIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getSupplierBySupplierShopId(Session session, Long shopId, Long... supplierShopId) {
    StringBuffer sb = new StringBuffer("from Supplier where supplierShopId in (:supplierShopId) and (status <>:status or status is null)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("supplierShopId", supplierShopId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    query.setParameter("status", CustomerStatus.DISABLED);
    return query;
  }

  public static Query getSupplierByNativeShopIds(Session session, Long supplierShopId, Long... nativeShopIds) {
    StringBuffer sb = new StringBuffer("from Supplier where supplierShopId =:supplierShopId and shopId in (:nativeShopIds)");
    Query query = session.createQuery(sb.toString()).setParameterList("nativeShopIds", nativeShopIds);
    query.setLong("supplierShopId", supplierShopId);
    return query;
  }

  public static Query getCustomerByCustomerShopId(Session session, Long shopId, Long... customerShopId) {
    StringBuffer sb = new StringBuffer("from Customer where customerShopId in (:customerShopId)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("customerShopId", customerShopId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getVehicleByVehicleIdSet(Session session, Long shopId, Set<Long> vehicleIds) {
    StringBuffer sb = new StringBuffer("from Vehicle where id in (:vehicleIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("vehicleIds", vehicleIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getUserByIdSet(Session session, Long shopId, Set<Long> userIds) {
    StringBuffer sb = new StringBuffer("from User where id in (:userIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("userIds", userIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getMemberCardByIds(Session session, Long shopId, Set<Long> cardIds) {
    StringBuffer sb = new StringBuffer("from MemberCard where id in (:cardIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("cardIds", cardIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getCustomerRecordForReindex(Session session, Long shopId, long startId, int pageSize) {
    StringBuffer sb = new StringBuffer("from CustomerRecord where customerId>:startId and customerId is not null");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setLong("startId", startId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    sb.append(" order by customerId asc");
    return query.setMaxResults(pageSize);
  }

  public static Query getSupplierForReindex(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("from Supplier");
    if (shopId != null) {
      sb.append(" where shopId=:shopId");
    }
    sb.append(" order by id asc");
    Query query = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(pageSize);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getMemberForReindex(Session session, Long shopId, long startId, int pageSize) {
    StringBuffer sb = new StringBuffer("from Member where id>:startId and customerId is not null");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setLong("startId", startId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    sb.append(" order by customerId asc");
    return query.setMaxResults(pageSize);
  }

  public static Query countVehicleByCustomerId(Session session, Long customerId) {
    return session.createQuery("select count(*) from CustomerVehicle c where c.customerId=:customerId").setLong("customerId", customerId);
  }

  public static Query getCustomerRecordForReindex(Session session, Long shopId, Long... customerId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from CustomerRecord cr where cr.customerId in :customerId");
    if (shopId != null) {
      sb.append(" and cr.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString())
      .setParameterList("customerId", customerId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getCustomerMemberForReindex(Session session, Long shopId, Long... customerId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from Member m where m.customerId in :customerId");
    if (shopId != null) {
      sb.append(" and m.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString())
      .setParameterList("customerId", customerId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getCustomerForReindex(Session session, Long shopId, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from Customer c where c.id in :ids");
    if (shopId != null) {
      sb.append(" and c.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString())
      .setParameterList("ids", ids);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getCustomerDetailsForReindex(Session session, Long shopId, List<Long> ids) {
    StringBuffer sb = new StringBuffer("select c.id,c.shop_id,c.mobile,c.name,c.address,c.contact,c.created,cr.total_amount,cr.total_receivable,cr.last_date,m.member_no,m.type ");
    sb.append(" from customer c ");
    sb.append(" inner join customer_record cr on c.id=cr.customer_id ");
    sb.append(" left join member m on c.id=m.customer_id ");
    sb.append(" where c.id in :ids");
    if (shopId != null) {
      sb.append(" and c.shop_id=:shopId");
    }
    sb.append(" order by c.id asc");
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("id", StandardBasicTypes.LONG)
      .addScalar("shop_id", StandardBasicTypes.LONG)
      .addScalar("mobile", StandardBasicTypes.STRING)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("address", StandardBasicTypes.STRING)
      .addScalar("contact", StandardBasicTypes.STRING)
      .addScalar("created", StandardBasicTypes.LONG)
      .addScalar("total_amount", StandardBasicTypes.DOUBLE)
      .addScalar("total_receivable", StandardBasicTypes.DOUBLE)
      .addScalar("last_date", StandardBasicTypes.LONG)
      .addScalar("member_no", StandardBasicTypes.STRING)
      .addScalar("type", StandardBasicTypes.STRING)
      .setParameterList("ids", ids);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getCustomerLicenseNosForReindex(Session session, Long shopId, List<Long> ids) {
    StringBuilder sb = new StringBuilder("select cv.customer_id,v.* ");
    sb.append(" from customer_vehicle cv inner join vehicle v on cv.vehicle_id=v.id ");
    sb.append(" where cv.customer_id in :ids");
    if (shopId != null) {
      sb.append(" and v.shop_id=:shopId");
    }
    sb.append(" order by cv.customer_id asc");
    Query query = session.createSQLQuery(sb.toString()).addScalar("customer_id", StandardBasicTypes.LONG).addEntity(Vehicle.class)
      .setParameterList("ids", ids);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getCustomerIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select id  from Customer");
    if (shopId != null) {
      sb.append(" where shopId=:shopId");
    }
    sb.append(" order by id asc");
    Query query = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(pageSize);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getVehicleByIds(Session session, Long shopId, Long... vehicleId) {
    return session.createQuery("from Vehicle where shopId =:shopId and id in(:vehicleId)")
      .setLong("shopId", shopId).setParameterList("vehicleId", vehicleId);
  }

  public static Query getShopSuppliers(Session session, Long shopId) {
    return session.createQuery("from Supplier where shopId=:shopId").setLong("shopId", shopId);
  }

  public static Query getMemberCardTypeByShopId(Session session, Long shopId) {
    return session.createQuery("select  m.name from MemberCard m where shopId=:shopId and m.status=:status")
      .setLong("shopId", shopId).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getAllUserGroup(Session session) {
    return session.createQuery("select u from UserGroup u order by u.lastModified desc");
  }

  public static Query getAllUserGroupIds(Session session) {
    return session.createQuery("select u.id from UserGroup u");
  }

  public static Query getAllRoles(Session session) {
    return session.createQuery("select r from Role r order by r.lastModified desc");
  }

  public static Query countPlansByStatus(Session session, Long shopId, PlansRemindStatus status) {
    return session.createQuery("select count(*) from ShopPlan s where s.shopId = :shopId and s.status = :status")
      .setLong("shopId", shopId).setParameter("status", status);
  }

  public static Query countActivityPlansExpired(Session session, Long shopId, Long now) {
    return session.createQuery("select count(*) from ShopPlan s where s.shopId=:shopId and s.remindTime < :now and s.status = :status")
      .setLong("shopId", shopId).setLong("now", now).setParameter("status", PlansRemindStatus.activity);
  }

  public static Query getPlan(Session session, Long shopId, Long id) {
    return session.createQuery("from ShopPlan s where s.shopId = :shopId and s.id = :id")
      .setLong("shopId", shopId).setLong("id", id);
  }

  public static Query countPlans(Session session) {
    return session.createQuery("select count(*) from ShopPlan s where s.userInfo is Null or s.userInfo = ''");
  }

  public static Query getHundredShopPlans(Session session) {
    return session.createQuery("from ShopPlan s where s.userInfo is Null or s.userInfo = ''").setMaxResults(100);
  }

  public static Query getVehicleIds(Session session, Long shopId, int start, int rows) {
    StringBuffer sb = new StringBuffer(" select id from Vehicle");
    if (shopId != null) {
      sb.append(" where shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setFirstResult(start).setMaxResults(rows);
  }

  public static Query updateUserPassword(Session session, UserDTO userDTO) {
    return session.createQuery("update User u set u.password = :password where u.id =:id")
      .setLong("id", userDTO.getId())
      .setString("password", userDTO.getPassword());
  }

  public static Query getMemberByShopId(Session session, Long shopId) {
    return session.createQuery("from Member where shopId = :shopId").setLong("shopId", shopId);
  }

  public static Query updateMemberPassword(Session session, Member member) {
    return session.createQuery("update Member m set m.password = :password where m.id=:id")
      .setLong("id", member.getId()).setString("password", member.getPassword());
  }

  public static Query updateCustomerVehicleCustomerId(Session session, Long id, Long customerId) {
    return session.createQuery("update CustomerVehicle cv set cv.customerId = :customerId where cv.id=:id")
      .setLong("id", id).setLong("customerId", customerId);
  }

  public static Query updateMemberCustomerId(Session session, Long id, Long customerId) {
    return session.createQuery("update Member set customerId = :customerId where id=:id")
      .setLong("id", id).setLong("customerId", customerId);
  }

//  public static Query updateMemberCustomerId(Session session,Long id,Long customerId) {
//    return session.createQuery("update Member  set customerId = :customerId where id=:id")
//          .setLong("id", id).setLong("customerId", customerId);
//  }

  public static Query getCustomerRecord(Session session, Long shopId, Long customerId) {
    return session.createQuery("from CustomerRecord cr where cr.shopId = :shopId and cr.customerId = :customerId")
      .setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getVehicleById(Session session, Long shopId, Long vehicleId) {
    return session.createQuery("from Vehicle v where v.shopId = :shopId and v.id= :id and(v.status =:status or v.status is NULL)")
      .setLong("shopId", shopId).setLong("id", vehicleId).setString("status", VehicleStatus.ENABLED.toString());
  }

  public static Query getUserLimitTags(Session session) {
    return session.createQuery("from UserLimit ul where ul.necessary =:necessary").setParameter("necessary", MessageSendNecessaryType.NECESSARY);
  }

  public static Query getUserSwitchByShopIdAndScene(Session session, Long shopId, String scene) {
    return session.createQuery("from UserSwitch us where us.shopId =:shopId and us.scene =:scene").setParameter("shopId", shopId).setParameter("scene", scene);
  }

  public static Query getUserSwitchListByShopId(Session session, Long shopId) {
    return session.createQuery("from UserSwitch us where us.shopId =:shopId").setLong("shopId", shopId);
  }

  public static Query getMemberServiceForInitService(Session session, int size, int page) {
    return session.createSQLQuery("SELECT * FROM member_service ms ORDER BY ms.created ASC").addEntity(MemberService.class)
      .setFirstResult(page * size).setMaxResults(size);
  }

  public static Query countMemberService(Session session) {
    return session.createQuery("select count(*) from MemberService");
  }

  public static Query getSalesManListByIds(Session session, Set<Long> salesManIds) {
    return session.createQuery("from SalesMan s where s.id in(:ids)").setParameterList("ids", salesManIds);
  }

  public static Query getMergeTaskOneByOne(Session session) {
    String sql = "from MergeTask c where c.exeStatus=:exeStatus order by createdTime asc";
    return session.createQuery(sql).setString("exeStatus", ExeStatus.READY.toString()).setMaxResults(1);
  }

  public static Query getVehicleModifyLogByStatus(Session session, StatProcessStatus[] statProcessStatuses) {
    return session.createQuery("from VehicleModifyLog where statProcessStatus in (:statProcessStatuses) order by creationDate")
      .setParameterList("statProcessStatuses", statProcessStatuses);
  }

  public static Query batchUpdateVehicleModifyLogStatus(Session session, List<Long> ids, StatProcessStatus done) {
    return session.createQuery("update VehicleModifyLog set statProcessStatus=:done where id in (:ids)")
      .setParameterList("ids", ids).setParameter("done", done);
  }

  public static Query getCustomerRecordDTOByCustomerId(Session session, Long shopId, Long customerId) {
    return session.createQuery("from CustomerRecord cr where cr.shopId=:shopId and cr.customerId = :customerId")
      .setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getCustomerIdByLicenceNo(Session session, long shopId, String licenceNo) {
    return session.createQuery("select c.customerId from CustomerVehicle c , Vehicle v  where v.shopId = :shopId and c.vehicleId = v.id  and v.licenceNo = :licenceNo")
      .setLong("shopId", shopId).setString("licenceNo", licenceNo);
  }

  public static Query getCustomerVehicleIdByVehicleIds(Session session, Set<Long> vehicleIds) {
    return session.createQuery("from CustomerVehicle where vehicleId in (:vehicleIds)")
      .setParameterList("vehicleIds", vehicleIds);
  }

  public static Query getCustomerRecordDTOByCustomerIdAndShopId(Session session, Long shopId, Long customerId) {
    return session.createQuery("from CustomerRecord cr where cr.shopId=:shopId and cr.customerId = :customerId")
      .setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getCustomerByCustomerShopIdAndShopId(Session session, Long shopId, Long customerShopId) {
    return session.createQuery("from Customer c where c.shopId =:shopId and c.customerShopId =:customerShopId and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId)
      .setLong("customerShopId", customerShopId)
      .setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getCustomerByCustomerShopId(Session session, Long customerShopId) {
    return session.createQuery("from Customer c where c.customerShopId =:customerShopId and (c.status is null or c.status != :status)")
      .setLong("customerShopId", customerShopId)
      .setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getSupplierBySupplierShopIdAndShopId(Session session, Long shopId, Long supplierShopId) {
    return session.createQuery("from Supplier s where s.shopId =:shopId and s.supplierShopId =:supplierShopId and (s.status is null or s.status != :status)")
      .setLong("shopId", shopId)
      .setLong("supplierShopId", supplierShopId)
      .setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getSupplierBySupplierShopId(Session session, Long supplierShopId) {
    return session.createQuery("from Supplier s where s.supplierShopId =:supplierShopId and (s.status is null or s.status != :status)")
      .setLong("supplierShopId", supplierShopId)
      .setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getCustomerIdsByNameWithFuzzyQuery(Session session, Long shopId, String customerName) {
    return session.createQuery("select c.id from Customer c where c.shopId =:shopId and c.name like :customerName").setLong("shopId", shopId).setString("customerName", "%" + customerName + "%");
  }

  public static Query getSupplierIdsByNameWithFuzzyQuery(Session session, Long shopId, String supplierName) {
    return session.createQuery("select c.id from Supplier c where c.shopId =:shopId and c.name like :supplierName").setLong("shopId", shopId).setString("supplierName", "%" + supplierName + "%");
  }

  public static Query getWholeSalersByCustomerShopId(Session session, Long customerShopId) {
    return session.createQuery("select s from Supplier as s where s.shopId = :customerShopId and (s.status is null or s.status = :status) and supplierShopId is not null ")
      .setLong("customerShopId", customerShopId).setParameter("status", CustomerStatus.ENABLED);
  }

  public static Query getRelatedCustomerByShopId(Session session, Long wholeSalerShopId) {
    return session.createQuery("select s from Customer as s where s.shopId = :wholeSalerShopId and (s.status is null or s.status = :status)  and s.customerShopId is not null ")
      .setLong("wholeSalerShopId", wholeSalerShopId).setParameter("status", CustomerStatus.ENABLED);
  }

  public static Query getShopRelatedCustomer(Session session, Long wholeSalerShopId, Long... customerIds) {
    return session.createQuery("select s from Customer as s where s.shopId = :wholeSalerShopId  and s.customerShopId is not null and id in (:customerIds)")
      .setLong("wholeSalerShopId", wholeSalerShopId).setParameterList("customerIds", customerIds);
  }

  public static Query countRelatedCustomersByShopId(Session session, Long wholeSalerShopId) {
    return session.createQuery("select count(s) from Customer as s where s.shopId = :wholeSalerShopId  and s.customerShopId is not null ")
      .setLong("wholeSalerShopId", wholeSalerShopId);
  }

  public static Query getRelatedSuppliersByShopId(Session session, Long shopId) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId  and s.supplierShopId is not null and (s.status is null or s.status = :status)")
      .setLong("shopId", shopId).setParameter("status", CustomerStatus.ENABLED);
  }

  public static Query getWholesalerByFuzzyName(Session session, Long shopId, String wholesalerName) {
    return session.createQuery("select s from Supplier as s where s.shopId = :shopId  and s.name like :name and supplierShopId is not null")
      .setLong("shopId", shopId).setString("name", "%" + wholesalerName + "%");
  }


  public static Query getTreeMenuByParentId(Session session, Long treeId, List<Long> roleIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select t from TreeMenu t ");
    if (treeId != null) {
      sb.append("where t.parentId=:treeId and t.roleId in(:roleIds)");
    } else {
      sb.append("where t.parentId is null ");
    }
    sb.append("order by t.sort asc ");
    Query query = session.createQuery(sb.toString());
    if (treeId != null) {
      query.setLong("treeId", treeId).setParameterList("roleIds", roleIds);
    }
    return query;
  }

  public static Query getUserGroupByCondition(Session session, UserGroupSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select u ");
    return splittingUserGroupByConditionSql(session, condition, sql).setFirstResult(condition.getStart()).setMaxResults(condition.getLimit());
  }

  private static Query splittingUserGroupByConditionSql(Session session, UserGroupSearchCondition condition, StringBuilder sql) {
    sql.append("from UserGroup u, UserGroupShop ugs ");
    sql.append("where (ugs.shopId =:shopId or ugs.shopVersionId=:shopVersionId) and ugs.userGroupId=u.id");
    boolean findByStatus = condition.getStatus() != null && !Status.all.equals(condition.getStatus());
    if (findByStatus) {
      sql.append(" and u.status =:status");
    }
    if (StringUtils.isNotBlank(condition.getName())) {
      sql.append(" and u.name like :name");
    }
    if (StringUtils.isNotBlank(condition.getMemo())) {
      sql.append(" and u.memo like :memo");
    }
    sql.append(" order by u.lastModified desc");
    Query query = session.createQuery(sql.toString());
    if (findByStatus) query.setParameter("status", condition.getStatus());
    if (StringUtils.isNotBlank(condition.getName())) query.setString("name", "%" + condition.getName() + "%");
    if (StringUtils.isNotBlank(condition.getMemo())) query.setString("memo", "%" + condition.getMemo() + "%");
    query.setLong("shopId", condition.getShopId()).setLong("shopVersionId", condition.getShopVersionId());
    return query;
  }

  public static Query countUserGroupByCondition(Session session, UserGroupSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(u) ");
    return splittingUserGroupByConditionSql(session, condition, sql);
  }

  public static Query getUserGroupByIds(Session session, Long... ids) {
    return session.createQuery("from UserGroup u where u.id in(:ids)").setParameterList("ids", ids);
  }

  public static Query getRoles(Session session, SystemType systemType) {
    return session.createQuery("select r from Role r where r.type=:type order by r.lastModified desc").setString("type", systemType.toString());
  }

  public static Query countUserByCondition(Session session, UserSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(*) ");
    return spiltQueryForUserByCondition(session, condition, sql);
  }

  private static SQLQuery spiltQueryForUserByCondition(Session session, UserSearchCondition condition, StringBuilder sql) {
    sql.append(" from user u ");
    sql.append("left join department d on d.id=u.department_id ");
    sql.append("left join occupation o on o.id=u.occupation_id ");
    sql.append("join user_group_user ugu join user_group ug on u.id=ugu.user_id and ug.id=ugu.user_group_id ");
    sql.append("where u.shop_id=:shopId ");
    if (condition.getDepartmentResponsibility() == DepartmentResponsibility.LEADER) {
      sql.append(" and u.department_responsibility =:departmentResponsibility ");
    } else if (condition.getDepartmentResponsibility() == DepartmentResponsibility.MEMBER) {
      sql.append(" and (u.department_responsibility !=:departmentResponsibility OR u.department_responsibility is null ) ");
    }
    if (condition.getDepartmentId() != null) sql.append(" and u.department_id=:departmentId ");
    if (StringUtils.isNotBlank(condition.getDepartmentName())) sql.append("and d.name like:departmentName ");
    if (StringUtils.isNotBlank(condition.getOccupationName())) sql.append("and o.name like:occupationName ");
    if (StringUtils.isNotBlank(condition.getRoleName())) sql.append("and ug.name like:roleName ");
    if (StringUtils.isNotBlank(condition.getName())) sql.append("and u.name like:name ");
    if (StringUtils.isNotBlank(condition.getUserNo())) sql.append("and u.user_no like:userNo ");
    if (condition.getStatus() != null && !condition.getStatus().equals(Status.all)) {
      sql.append("and u.status=:status ");
    } else {
      sql.append(" and ( u.status!=:status or u.status is null or u.status ='')");
    }
    sql.append(" order by u.created desc ");
    SQLQuery query = (SQLQuery) session.createSQLQuery(sql.toString()).setLong("shopId", condition.getShopId());
    if (StringUtils.isNotBlank(condition.getDepartmentName()))
      query.setString("departmentName", "%" + condition.getDepartmentName() + "%");
    if (StringUtils.isNotBlank(condition.getOccupationName()))
      query.setString("occupationName", "%" + condition.getOccupationName() + "%");
    if (StringUtils.isNotBlank(condition.getRoleName()))
      query.setString("roleName", "%" + condition.getRoleName() + "%");
    if (condition.getStatus() != null && !condition.getStatus().equals(Status.all)) {
      query.setParameter("status", condition.getStatus().toString());
    } else {
      query.setParameter("status", Status.deleted.toString());
    }
    if (condition.getDepartmentResponsibility() == DepartmentResponsibility.LEADER) {
      query.setString("departmentResponsibility", condition.getDepartmentResponsibility().name());
    } else if (condition.getDepartmentResponsibility() == DepartmentResponsibility.MEMBER) {
      query.setString("departmentResponsibility", DepartmentResponsibility.LEADER.name());
    }
    if (condition.getDepartmentId() != null) query.setLong("departmentId", condition.getDepartmentId());
    if (StringUtils.isNotBlank(condition.getName()))
      query.setParameter("name", "%" + condition.getName() + "%");
    if (StringUtils.isNotBlank(condition.getUserNo()))
      query.setParameter("userNo", "%" + condition.getUserNo() + "%");
    return query;
  }


  public static SQLQuery getUserByCondition(Session session, UserSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select u.id as id,u.shop_id as shopId,u.user_no as userNo,u.user_name as userName,u.department_responsibility as departmentResponsibilityStr," +
      "u.password as password,u.name as name,u.email as email,u.mobile as mobile,u.qq as qq,u.memo as memo," +
      "ug.id as userGroupId,ug.name as userGroupName,d.id as departmentId,d.name as departmentName," +
      "u.status as status,o.id as occupationId,o.name as occupationName,u.created as creationDate ");
    SQLQuery query = spiltQueryForUserByCondition(session, condition, sql);
    if (condition.isHasPager()) {
      query.setLong("shopId", condition.getShopId()).setFirstResult(condition.getStart()).setMaxResults(condition.getLimit());
    }
    query.addScalar("id", StandardBasicTypes.LONG)
      .addScalar("shopId", StandardBasicTypes.LONG)
      .addScalar("userGroupId", StandardBasicTypes.LONG)
      .addScalar("departmentId", StandardBasicTypes.LONG)
      .addScalar("occupationId", StandardBasicTypes.LONG)
      .addScalar("creationDate", StandardBasicTypes.LONG)
      .addScalar("userNo", StandardBasicTypes.STRING)
      .addScalar("userName", StandardBasicTypes.STRING)
      .addScalar("departmentResponsibilityStr", StandardBasicTypes.STRING)
      .addScalar("password", StandardBasicTypes.STRING)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("userGroupName", StandardBasicTypes.STRING)
      .addScalar("departmentName", StandardBasicTypes.STRING)
      .addScalar("occupationName", StandardBasicTypes.STRING)
      .addScalar("email", StandardBasicTypes.STRING)
      .addScalar("mobile", StandardBasicTypes.STRING)
      .addScalar("qq", StandardBasicTypes.STRING)
      .addScalar("memo", StandardBasicTypes.STRING)
      .addScalar("status", StandardBasicTypes.STRING)
      .addScalar("shopId", StandardBasicTypes.LONG);
    return query;
  }

  public static Query getDepartmentsByShopId(Session session, Long shopId) {
    return session.createQuery("from Department d where d.shopId =:shopId").setLong("shopId", shopId);
  }

  public static Query checkDepartmentBeforeDelete(Session session, Long departmentId, Long shopId) {
    return session.createQuery("select count(u) from User u where u.shopId =:shopId and u.departmentId=:departmentId and u.status != :status")
      .setLong("shopId", shopId).setLong("departmentId", departmentId).setParameter("status", Status.deleted);
  }


  public static Query getOccupationsByShopId(Session session, Long shopId) {
    return session.createQuery("from Occupation o where o.shopId =:shopId").setLong("shopId", shopId);
  }

  public static Query checkOccupationBeforeDelete(Session session, Long occupationId, Long shopId) {
    return session.createQuery("select count(u) from User u where u.shopId =:shopId and u.occupationId=:occupationId and u.status != :status")
      .setLong("shopId", shopId).setLong("occupationId", occupationId).setParameter("status", Status.deleted);
  }

  public static Query maxParentDepartmentChildren(Session session, Long parentId, Long shopId) {
    return session.createQuery("select max(d.id) from Department d where d.shopId =:shopId and d.parentId=:parentId")
      .setLong("shopId", shopId).setLong("parentId", parentId);
  }

  public static Query maxParentOccupationChildren(Session session, Long departmentId, Long shopId) {
    return session.createQuery("select max(o.id) from Occupation o where o.shopId =:shopId and o.departmentId=:departmentId")
      .setLong("shopId", shopId).setLong("departmentId", departmentId);
  }

  public static Query checkOccupation(Session session, OccupationDTO occupationDTO) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(o) from Occupation o where o.departmentId=:departmentId and o.name=:name");
    if (occupationDTO.getId() != null) {
      sql.append(" and o.id !=:id ");
    }
    Query query = session.createQuery(sql.toString())
      .setString("name", occupationDTO.getName()).setLong("departmentId", occupationDTO.getDepartmentId());
    if (occupationDTO.getId() != null) {
      query.setParameter("id", occupationDTO.getId());
    }
    return query;
  }

  public static Query checkDepartment(Session session, DepartmentDTO departmentDTO) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(d) from Department d where d.parentId=:parentId and d.name=:name");
    if (departmentDTO.getId() != null) {
      sql.append(" and d.id !=:id ");
    }
    Query query = session.createQuery(sql.toString())
      .setString("name", departmentDTO.getName()).setLong("parentId", departmentDTO.getParentId());
    if (departmentDTO.getId() != null) {
      query.setParameter("id", departmentDTO.getId());
    }
    return query;
  }

  public static Query getSalesManByName(Session session, Long shopId, String name) {
    return session.createQuery("select s from SalesMan s where s.shopId=:shopId and s.name=:name")
      .setString("name", name).setLong("shopId", shopId);
  }

  public static Query getSalesManByName(Session session, Long shopId, Set<String> names) {
    return session.createQuery("select s from SalesMan s where s.shopId=:shopId and s.name in(:names)")
      .setParameterList("names", names).setLong("shopId", shopId);
  }

  public static Query getEnableMemberCardDTOByShopId(Session session, Long shopId) {
    return session.createQuery("select mc from MemberCard mc where mc.shopId = :shopId and mc.status =:status")
      .setLong("shopId", shopId).setString("status", MemberStatus.ENABLED.toString());
  }

  public static Query checkModule(Session session, ModuleDTO moduleDTO) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(m) from Module m where m.value=:value and m.type=:type");
    if (moduleDTO.getId() != null) {
      sql.append(" and m.id !=:id ");
    }
    if (moduleDTO.getParentId() != null) {
      sql.append(" and m.parentId=:parentId ");
    } else {
      sql.append(" and m.parentId is null ");
    }
    Query query = session.createQuery(sql.toString()).setString("type", moduleDTO.getType().toString())
      .setString("value", moduleDTO.getValue());
    if (moduleDTO.getId() != null) {
      query.setParameter("id", moduleDTO.getId());
    }
    if (moduleDTO.getParentId() != null) {
      query.setLong("parentId", moduleDTO.getParentId());
    }
    return query;
  }

  public static Query checkRole(Session session, RoleDTO roleDTO) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(r) from Role r where r.moduleId=:moduleId and r.value=:value");
    if (roleDTO.getId() != null) {
      sql.append(" and r.id !=:id ");
    }
    Query query = session.createQuery(sql.toString())
      .setString("value", roleDTO.getValue()).setLong("moduleId", roleDTO.getModuleId());
    if (roleDTO.getId() != null) {
      query.setParameter("id", roleDTO.getId());
    }
    return query;
  }

  public static Query checkRoleBeforeDelete(Session session, Long roleId, Long shopId) {
    return session.createQuery("select count(r) from RoleResource r where  r.roleId=:roleId")
      .setLong("roleId", roleId);
  }

  public static Query checkResourceBeforeDelete(Session session, Long resourceId, Long shopId) {
    return session.createQuery("select count(r) from RoleResource r where  r.resourceId=:resourceId")
      .setLong("resourceId", resourceId);
  }

  public static Query deleteRoleResource(Session session, Long roleId, Long resourceId) {
    return session.createQuery("delete from RoleResource r  where r.roleId=:roleId and r.resourceId=:resourceId")
      .setLong("roleId", roleId).setLong("resourceId", resourceId);
  }

  public static Query getResourcesByCondition(Session session, ResourceSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select re.*,re.id as resourceId,menu.id as menuId,menu.* from resource re ");
    sql.append(" left join menu menu on re.id=menu.resource_id ");
    sql.append(" left join role_resource rr on re.id=rr.resource_id ");
    sql.append(" left join role ro on ro.id = rr.role_id  ");
    sql.append(" where re.system_type=:systemType ");
    if (StringUtils.isNotBlank(condition.getRoleName())) sql.append("and ro.name like:roleName ");
    if (StringUtils.isNotBlank(condition.getName())) sql.append("and re.name like:name ");
    if (StringUtils.isNotBlank(condition.getMemo())) sql.append("and re.memo like:memo ");
    if (StringUtils.isNotBlank(condition.getValue())) sql.append("and re.value like:value ");
    if (StringUtils.isNotBlank(condition.getType())) sql.append("and re.type =:type ");
    sql.append(" group by re.id order by re.created desc ");
    SQLQuery query = (SQLQuery) session.createSQLQuery(sql.toString())
      .addEntity(Menu.class)
      .addEntity(Resource.class)
      .addScalar("menuId", StandardBasicTypes.LONG)
      .addScalar("resourceId", StandardBasicTypes.LONG)
      .setString("systemType", condition.getSystemType().toString());
    if (StringUtils.isNotBlank(condition.getRoleName()))
      query.setString("roleName", "%" + condition.getRoleName() + "%");
    if (StringUtils.isNotBlank(condition.getName()))
      query.setString("name", "%" + condition.getName() + "%");
    if (StringUtils.isNotBlank(condition.getMemo()))
      query.setString("memo", "%" + condition.getMemo() + "%");
    if (StringUtils.isNotBlank(condition.getValue()))
      query.setParameter("value", "%" + condition.getValue() + "%");
    if (StringUtils.isNotBlank(condition.getType()))
      query.setParameter("type", condition.getType());
    return query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
  }

  public static Query countResourcesByCondition(Session session, ResourceSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(distinct(re.id)) from resource re ");
    sql.append(" left join role_resource rr on re.id=rr.resource_id ");
    sql.append(" left join role ro on ro.id = rr.role_id  ");
    sql.append(" where re.system_type=:systemType ");
    if (StringUtils.isNotBlank(condition.getRoleName())) sql.append("and ro.value like:roleName ");
    if (StringUtils.isNotBlank(condition.getName())) sql.append("and re.name like:name ");
    if (StringUtils.isNotBlank(condition.getMemo())) sql.append("and re.memo like:memo ");
    if (StringUtils.isNotBlank(condition.getValue())) sql.append("and re.value like:value ");
    if (StringUtils.isNotBlank(condition.getType())) sql.append("and re.type =:type ");
    sql.append(" order by re.created desc ");
    SQLQuery query = (SQLQuery) session.createSQLQuery(sql.toString()).setString("systemType", condition.getSystemType().toString());
    if (StringUtils.isNotBlank(condition.getRoleName()))
      query.setString("roleName", "%" + condition.getRoleName() + "%");
    if (StringUtils.isNotBlank(condition.getName()))
      query.setString("name", "%" + condition.getName() + "%");
    if (StringUtils.isNotBlank(condition.getMemo()))
      query.setString("memo", "%" + condition.getMemo() + "%");
    if (StringUtils.isNotBlank(condition.getValue()))
      query.setParameter("value", "%" + condition.getValue() + "%");
    if (StringUtils.isNotBlank(condition.getType()))
      query.setParameter("type", condition.getType());
    return query;
  }

  public static Query getRolesByResourceId(Session session, Long resourceId) {
    return session.createQuery("select r from RoleResource r where r.resourceId=:resourceId order by r.lastModified desc ")
      .setLong("resourceId", resourceId);
  }

  public static Query getAllShopVersion(Session session) {
    return session.createQuery("select s from ShopVersion s");
  }

  public static Query getRoleIdByShopVersionId(Session session, Long shopVersionId) {
    return session.createQuery("select s.roleId from ShopRole s where  s.shopVersionId =:shopVersionId")
      .setLong("shopVersionId", shopVersionId);
  }

  public static Query getRoleIdsByUserGroupId(Session session, Long userGroupId) {
    return session.createQuery("select u.roleId from UserGroupRole u where  u.userGroupId =:userGroupId")
      .setLong("userGroupId", userGroupId);
  }

  public static Query getRolesByShopVersionId(Session session, Long shopVersionId) {
    return session.createQuery("select r from ShopRole s,Role r where r.id=s.roleId and s.shopVersionId =:shopVersionId")
      .setLong("shopVersionId", shopVersionId);
  }

  public static Query deleteShopRole(Session session, Long shopVersionId, Long roleId) {
    return session.createQuery("delete from ShopRole s where s.roleId=:roleId and s.shopVersionId=:shopVersionId")
      .setLong("shopVersionId", shopVersionId).setLong("roleId", roleId);
  }

  public static Query deleteRoleByShopVersions(Session session, Long shopVersionId) {
    return session.createQuery("delete from ShopRole s where s.shopVersionId=:shopVersionId")
      .setLong("shopVersionId", shopVersionId);
  }

  public static Query getUserGroupsByShopVersionId(Session session, Long shopVersionId) {
    return session.createQuery("select u from UserGroupShop ugs, UserGroup u where ugs.userGroupId=u.id and  ugs.shopVersionId =:shopVersionId")
      .setLong("shopVersionId", shopVersionId);
  }

  public static Query deleteUserGroupShop(Session session, Long userGroupId, Long shopVersionId) {
    return session.createQuery("delete from UserGroupShop u  where u.userGroupId=:userGroupId and u.shopVersionId=:shopVersionId")
      .setLong("shopVersionId", shopVersionId).setLong("userGroupId", userGroupId);
  }

  public static Query getResourceByRoleType(Session session, RoleType roleType) {
    return session.createQuery("select re from RoleResource rr,Resource re,Role ro where rr.resourceId=re.id and rr.roleId=ro.id and ro.name =:roleType")
      .setString("roleType", roleType.name());
  }

  public static Query checkUserGroupName(Session session, Long shopVersionId, Long shopId, Long userGroupId, String name) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(ug) from UserGroup ug,UserGroupShop ugs where (ugs.shopId=:shopId or ugs.shopVersionId=:shopVersionId) and ug.id=ugs.userGroupId and ug.name =:name and (ug.status !=:status or ug.status is null)");
    if (userGroupId != null) {
      sql.append(" and ug.id != :userGroupId");
    }
    Query query = session.createQuery(sql.toString()).setString("name", name).setLong("shopId", shopId).setLong("shopVersionId", shopVersionId);
    if (userGroupId != null) {
      query.setLong("userGroupId", userGroupId);
    }
    return query.setParameter("status", Status.deleted);
  }

  private static Query splittingGetUserGroupsByConditionSql(Session session, Long shopVersionId, Long shopId, UserGroupSearchCondition condition, StringBuilder sql) {
    sql.append(" from UserGroup ug,UserGroupShop ugs where ug.id=ugs.userGroupId and ( ugs.shopId=:shopId or ugs.shopVersionId=:shopVersionId) ");
    if (StringUtils.isNotBlank(condition.getName())) {
      sql.append(" and ug.name like :userGroupName");
    }
    if (StringUtils.isNotBlank(condition.getUserGroupNo())) {
      sql.append(" and ug.userGroupNo like :userGroupNo");
    }
    if (StringUtils.isNotBlank(condition.getVariety())) {
      sql.append(" and ug.variety = :variety");
    }
    if (condition.getStatus() != null) {
      sql.append(" and ug.status = :status");
    }

    sql.append(" order by ug.variety desc,ug.userGroupNo asc ");
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId).setLong("shopVersionId", shopVersionId);
    if (StringUtils.isNotBlank(condition.getName())) {
      query.setString("userGroupName", "%" + condition.getName() + "%");
    }
    if (StringUtils.isNotBlank(condition.getUserGroupNo())) {
      query.setString("userGroupNo", "%" + condition.getUserGroupNo() + "%");
    }
    if (StringUtils.isNotBlank(condition.getVariety())) {
      query.setString("variety", condition.getVariety());
    }
    if (condition.getStatus() != null) {
      query.setString("status", condition.getStatus().toString());
    }

    return query;
  }

  public static Query getUserGroupsByCondition(Session session, Long shopVersionId, Long shopId, UserGroupSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ug ");
    Query query = splittingGetUserGroupsByConditionSql(session, shopVersionId, shopId, condition, sql);
    return query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
  }

  public static Query countUserGroupsByCondition(Session session, Long shopVersionId, Long shopId, UserGroupSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(ug) ");
    return splittingGetUserGroupsByConditionSql(session, shopVersionId, shopId, condition, sql);
  }

  private static SQLQuery splittingGetStaffByCondition(Session session, StaffSearchCondition condition, StringBuilder sql) {
    sql.append(" from sales_man sm ");
    sql.append("left join user u on sm.id=u.sales_man_id ");
    sql.append("left join department d on d.id=sm.department_id ");
    sql.append("left join user_group_user ugu on u.id=ugu.user_id ");
    sql.append("left join user_group ug on ug.id=sm.user_group_id ");
    sql.append("where sm.shop_id=:shopId ");
    if (StringUtils.isNotBlank(condition.getDepartmentName())) sql.append(" and d.name like:departmentName ");
    if (StringUtils.isNotBlank(condition.getUserGroupName())) sql.append(" and ug.name like:userGroupName ");
    if (condition.getUserGroupId() != null) sql.append(" and ug.id =:userGroupId ");
    if (StringUtils.isNotBlank(condition.getName())) sql.append(" and sm.name like:name ");
    if (condition.getSex() != null) sql.append(" and sm.sex =:sex ");
    if (condition.getStatus() != null) {
      sql.append(" and sm.status=:status ");
    } else {
      sql.append(" and ( sm.status != :status )");
    }
    if (condition.getUserStatus() != null && !Status.all.equals(condition.getUserStatus())) {
      sql.append(" and u.status=:userStatus ");
    }

    if (CollectionUtils.isNotEmpty(condition.getSalesManIdSet())) {
      sql.append(" and sm.id in(:ids) ");
    }

    if (condition.getSortStr() != null) {
      if ("nameSortAscending".equals(condition.getSortStr())) {
        sql.append("order by sm.name asc");
      } else if ("nameSortDescending".equals(condition.getSortStr())) {
        sql.append("order by sm.name desc");
      } else if ("userNoSortDescending".equals(condition.getSortStr())) {
        sql.append("order by u.user_no desc");
      } else if ("userNoSortAscending".equals(condition.getSortStr())) {
        sql.append("order by u.user_no asc");
      }
    } else {
      sql.append("order by sm.created desc");
    }

    SQLQuery query = (SQLQuery) session.createSQLQuery(sql.toString()).setLong("shopId", condition.getShopId());
    if (StringUtils.isNotBlank(condition.getDepartmentName()))
      query.setString("departmentName", "%" + condition.getDepartmentName() + "%");
    if (StringUtils.isNotBlank(condition.getUserGroupName()))
      query.setString("userGroupName", "%" + condition.getUserGroupName() + "%");
    if (StringUtils.isNotBlank(condition.getName()))
      query.setString("name", "%" + condition.getName() + "%");
    if (condition.getStatus() != null) {
      query.setParameter("status", condition.getStatus().toString());
    } else {
      query.setParameter("status", SalesManStatus.DELETED.toString());
    }
    if (condition.getUserStatus() != null && !Status.all.equals(condition.getUserStatus())) {
      query.setParameter("userStatus", condition.getUserStatus().toString());
    }
    if (condition.getUserGroupId() != null) query.setLong("userGroupId", condition.getUserGroupId());
    if (condition.getSex() != null) query.setParameter("sex", condition.getSex().name());

    if (CollectionUtils.isNotEmpty(condition.getSalesManIdSet())) {
      query.setParameterList("ids", condition.getSalesManIdSet());
    }
    return query;
  }


  public static Query getStaffByCondition(Session session, StaffSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select sm.id as id,sm.shop_id as shopId,ug.id as userGroupId,ug.name as userGroupName,d.id as departmentId,d.name as departmentName,u.user_type as userType," +
      "sm.sex as sex,sm.career_date as careerDate,sm.name as name,sm.memo as memo,sm.identity_card as identityCard,sm.salary as salary,sm.allowance as allowance,sm.mobile as mobile," +
      "sm.status as statusStr,u.user_no as userNo,u.status as userStatusStr,sm.sales_man_code as salesManCode,u.id as userId");
    SQLQuery query = splittingGetStaffByCondition(session, condition, sql);
    query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
    query.addScalar("id", StandardBasicTypes.LONG)
      .addScalar("shopId", StandardBasicTypes.LONG)
      .addScalar("userId", StandardBasicTypes.LONG)
      .addScalar("userGroupId", StandardBasicTypes.LONG)
      .addScalar("departmentId", StandardBasicTypes.LONG)
      .addScalar("userGroupId", StandardBasicTypes.LONG)
      .addScalar("userGroupName", StandardBasicTypes.STRING)
      .addScalar("departmentName", StandardBasicTypes.STRING)
      .addScalar("careerDate", StandardBasicTypes.LONG)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("identityCard", StandardBasicTypes.STRING)
      .addScalar("mobile", StandardBasicTypes.STRING)
      .addScalar("salary", StandardBasicTypes.DOUBLE)
      .addScalar("allowance", StandardBasicTypes.DOUBLE)
      .addScalar("userNo", StandardBasicTypes.STRING)
      .addScalar("userStatusStr", StandardBasicTypes.STRING)
      .addScalar("userType", StandardBasicTypes.STRING)
      .addScalar("memo", StandardBasicTypes.STRING)
      .addScalar("salesManCode", StandardBasicTypes.STRING)
      .addScalar("sex", StandardBasicTypes.STRING)
      .addScalar("statusStr", StandardBasicTypes.STRING);
    return query;
  }

  public static Query countStaffByCondition(Session session, StaffSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(*) ");
    return splittingGetStaffByCondition(session, condition, sql);
  }

  public static Query checkSalesManCode(Session session, String salesManCode, Long shopId, Long salesManId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(*) from SalesMan s where s.salesManCode=:salesManCode and s.shopId= :shopId and s.status != :status");
    if (salesManId != null) {
      sql.append(" and s.id != :salesManId ");
    }
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId).setString("salesManCode", salesManCode).setString("status", SalesManStatus.DELETED.toString());
    if (salesManId != null) {
      query.setLong("salesManId", salesManId);
    }
    return query;
  }

  public static Query getDepartmentByName(Session session, Long shopId, String name) {
    return session.createQuery("select d from Department d where d.shopId= :shopId and d.status != :status and d.name like :name order by d.creationDate desc ")
      .setLong("shopId", shopId).setString("name", "%" + name + "%").setString("status", Status.deleted.toString());
  }

  public static Query checkDepartmentNameByShopId(Session session, String name, Long shopId) {
    return session.createQuery("select count(d) from Department d where d.shopId=:shopId and d.name=:name ")
      .setString("name", name).setLong("shopId", shopId);
  }

  public static Query checkDepartmentForDelete(Session session, Long departmentId, Long shopId) {
    return session.createQuery("select count(s) from SalesMan s where s.shopId=:shopId and s.departmentId=:departmentId ")
      .setLong("departmentId", departmentId).setLong("shopId", shopId);
  }

  public static Query countUserGroupByShopId(Session session, Long shopId) {
    return session.createQuery("select count(u) from UserGroupShop u where u.shopId=:shopId").setLong("shopId", shopId);
  }

  public static Query getUserByStaffId(Session session, Long staffId) {
    return session.createQuery("select u from User u where u.salesManId=:staffId").setLong("staffId", staffId);
  }

  public static Query getUserSuggestionByName(Session session, Set<Long> departmentIds, String name, Long shopId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select u from User u where u.shopId=:shopId and u.name like :name and (u.status != :status or u.status is null or u.status = '') ");
    if (CollectionUtils.isNotEmpty(departmentIds)) {
      sql.append(" and u.departmentId in :departmentIds ");
    }
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId).setString("status", Status.deleted.toString()).setLong("shopId", shopId).setString("name", "%" + name + "%");
    if (CollectionUtils.isNotEmpty(departmentIds)) {
      query.setParameterList("departmentIds", departmentIds);
    }
    return query;
  }

  public static Query getAllInsuranceCompanyDTOs(Session session) {
    return session.createQuery("from InsuranceCompany order by sort asc");
  }

  public static Query getAllCustomerByNameAndMobile(Session session, long shopId, String name, String mobile) {
    if (mobile == null) {
      return session.createQuery("select c from Customer as c where c.shopId = :shopId and name=:name")
        .setLong("shopId", shopId).setString("name", name);
    } else if (StringUtils.isEmpty(name)) {
      return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.mobile = :mobile")
        .setLong("shopId", shopId).setString("mobile", mobile);
    } else {
      return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.mobile = :mobile and name=:name ")
        .setLong("shopId", shopId).setString("mobile", mobile).setString("name", name);
    }
  }


  public static Query getAllSupplierByNameAndMobile(Session session, long shopId, String name, String mobile) {
    if (mobile == null) {
      return session.createQuery("select c from Supplier as c where c.shopId = :shopId and c.name=:name")
        .setLong("shopId", shopId).setString("name", name);
    } else if (StringUtils.isEmpty(name)) {
      return session.createQuery("select distinct c from Supplier c, Contact con where c.id = con.supplierId and con.disabled =:disabled and c.shopId = :shopId and con.mobile = :mobile ")
        .setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setString("mobile", mobile);
    } else {
      return session.createQuery("select distinct c from Supplier c, Contact con where c.id = con.supplierId and con.disabled =:disabled and c.shopId = :shopId and con.mobile = :mobile and c.name=:name ")
        .setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setString("mobile", mobile).setString("name", name);
    }
  }

  public static Query getCustomerByShopId(Session session, Long shopId, int start, int pageSize) {
    return session.createQuery("from Customer where shopId=:shopId").setLong("shopId", shopId).setFirstResult(start).setMaxResults(pageSize);
  }

  public static Query getAllCustomerServiceJob(Session session) {
    String sql = "select c from CustomerServiceJob c where c.remindTime is not null";
    return session.createQuery(sql);
  }

  public static Query getAllMemberService(Session session) {
    String sql = "select m from MemberService m";
    return session.createQuery(sql);
  }

  public static Query getSimilarCustomer(Session session, CustomerDTO customerDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append("select distinct c from Customer c, Contact con where c.id = con.customerId and con.disabled =:disabled and c.shopId =:shopId and c.id<>:id ");
    if (StringUtils.isNotBlank(customerDTO.getMobile()) || StringUtils.isNotBlank(customerDTO.getLandLine())) {
      sb.append("and (");
      if (StringUtils.isNotBlank(customerDTO.getMobile())) {
        sb.append(" con.mobile =:mobile ");
      }
      if (StringUtils.isNotBlank(customerDTO.getLandLine())) {
        sb.append(" or c.landLine =:landLine");
      }
      sb.append(")");
    }
    sb.append(" and ( c.status is null or c.status =:status)");
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", customerDTO.getShopId())
      .setLong("id", customerDTO.getId())
      .setInteger("disabled", ContactConstant.ENABLED)
      .setParameter("status", CustomerStatus.ENABLED);

    if (StringUtils.isNotBlank(customerDTO.getMobile())) {
      query.setString("mobile", customerDTO.getMobile());
    }
    if (StringUtils.isNotBlank(customerDTO.getLandLine())) {
      query.setString("landLine", customerDTO.getLandLine());
    }
    query.setMaxResults(20);
    return query;
  }

  public static Query getMatchCustomerByContactMobiles(Session session, Set<String> mobiles, Long shopId) {
    return session.createQuery("select distinct c from Customer c,Contact ct where ct.customerId=c.id and ct.disabled =:disabled and ct.mobile in (:mobiles) and c.shopId=:shopId and (c.status is null or c.status =:status) ")
      .setParameterList("mobiles", mobiles).setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setParameter("status", CustomerStatus.ENABLED).setMaxResults(1);
  }

  public static Query getRelatedCustomerByContactMobiles(Session session, Set<String> mobiles, Long shopId) {
    return session.createQuery("select distinct c from Customer c,Contact ct where ct.customerId=c.id and ct.disabled =:disabled and  ct.mobile in (:mobiles) and c.shopId=:shopId and (c.status is null or c.status =:status) and c.customerShopId is not null ")
      .setParameterList("mobiles", mobiles).setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setParameter("status", CustomerStatus.ENABLED).setMaxResults(1);
  }

  public static Query getMatchSupplierByContactMobiles(Session session, Set<String> mobiles, Long shopId) {
    return session.createQuery("select distinct s from Supplier s,Contact ct where ct.supplierId=s.id and ct.disabled =:disabled and ct.mobile in (:mobiles) and s.shopId=:shopId and (s.status is null or s.status =:status) ")
      .setParameterList("mobiles", mobiles).setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setParameter("status", CustomerStatus.ENABLED).setMaxResults(1);
  }

  public static Query getCustomerSupplierContactByMobile(Session session, Long shopId, String mobile) {
    return session.createQuery("select ct from Contact ct where ct.shopId=:shopId and ct.disabled =:disabled and ct.mobile=:mobile and (ct.customerId is not null or ct.supplierId is not null) ")
      .setParameter("mobile", mobile).setLong("shopId", shopId).setParameter("disabled", ContactConstant.ENABLED);
  }

  public static Query getRelatedSupplierByContactMobiles(Session session, Set<String> mobiles, Long shopId) {
    return session.createQuery("select distinct s from Supplier s,Contact ct where ct.supplierId=s.id and ct.disabled =:disabled and ct.mobile in (:mobiles) and s.shopId=:shopId and (s.status is null or s.status =:status) and s.supplierShopId is not null ")
      .setParameterList("mobiles", mobiles).setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setParameter("status", CustomerStatus.ENABLED).setMaxResults(1);
  }

  public static Query getMatchCustomer(Session session, CustomerDTO customerDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append("select distinct c from Customer c, Contact con where c.id=con.customerId and con.disabled =:disabled and c.shopId =:shopId ");
    sb.append(" and c.name =:name ");
    if (StringUtils.isNotBlank(customerDTO.getMobile()) || StringUtils.isNotBlank(customerDTO.getLandLine())) {
      sb.append(" and (");
      if (StringUtils.isNotBlank(customerDTO.getMobile())) {
        sb.append(" con.mobile =:mobile ");
      }
      if (StringUtils.isNotBlank(customerDTO.getLandLine())) {
        sb.append(" or c.landLine =:landLine");
      }
      sb.append(")");
    }
    sb.append(" and ( c.status is null or c.status =:status)");
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", customerDTO.getShopId())
      .setString("name", customerDTO.getName())
      .setParameter("status", CustomerStatus.ENABLED)
      .setInteger("disabled", ContactConstant.ENABLED);

    if (StringUtils.isNotBlank(customerDTO.getMobile())) {
      query.setString("mobile", customerDTO.getMobile());
    }
    if (StringUtils.isNotBlank(customerDTO.getLandLine())) {
      query.setString("landLine", customerDTO.getLandLine());
    }
    query.setMaxResults(20);
    return query;
  }

  public static Query getSimilarSupplier(Session session, SupplierDTO supplierDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append("select distinct s from Supplier s, Contact con where s.id=con.supplierId and con.disabled =:disabled and s.shopId =:shopId and s.id<>:id ");
    if (StringUtils.isNotBlank(supplierDTO.getMobile()) || StringUtils.isNotBlank(supplierDTO.getLandLine())) {
      sb.append("and (");
      if (StringUtils.isNotBlank(supplierDTO.getMobile())) {
        sb.append(" con.mobile =:mobile ");
      }
      if (StringUtils.isNotBlank(supplierDTO.getLandLine())) {
        sb.append(" or s.landLine =:landLine");
      }
      sb.append(")");
    }

    sb.append(" and ( s.status is null or s.status =:status)");
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", supplierDTO.getShopId())
      .setLong("id", supplierDTO.getId())
      .setParameter("status", CustomerStatus.ENABLED)
      .setInteger("disabled", ContactConstant.ENABLED);
    if (StringUtils.isNotBlank(supplierDTO.getMobile())) {
      query.setString("mobile", supplierDTO.getMobile());
    }
    if (StringUtils.isNotBlank(supplierDTO.getLandLine())) {
      query.setString("landLine", supplierDTO.getLandLine());
    }
    query.setMaxResults(20);
    return query;
  }

  public static Query getMatchSupplier(Session session, SupplierDTO supplierDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append("select distinct s from Supplier s, Contact con where s.id=con.supplierId and con.disabled =:disabled and s.shopId =:shopId ");
    sb.append(" and s.name =:name ");
    if (StringUtils.isNotBlank(supplierDTO.getMobile()) || StringUtils.isNotBlank(supplierDTO.getLandLine())) {
      sb.append(" and (");
      if (StringUtils.isNotBlank(supplierDTO.getMobile())) {
        sb.append(" con.mobile =:mobile ");
      }
      if (StringUtils.isNotBlank(supplierDTO.getLandLine())) {
        sb.append(" or s.landLine =:landLine");
      }
      sb.append(")");
    }

    sb.append(" and ( s.status is null or s.status =:status)");
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", supplierDTO.getShopId())
      .setString("name", supplierDTO.getName())
      .setParameter("status", CustomerStatus.ENABLED)
      .setInteger("disabled", ContactConstant.ENABLED);
    if (StringUtils.isNotBlank(supplierDTO.getMobile())) {
      query.setString("mobile", supplierDTO.getMobile());
    }
    if (StringUtils.isNotBlank(supplierDTO.getLandLine())) {
      query.setString("landLine", supplierDTO.getLandLine());
    }
    query.setMaxResults(20);
    return query;
  }

  public static Query getShopVersionByIds(Session session, Long... ids) {
    return session.createQuery("from ShopVersion  where id in(:ids)").setParameterList("ids", ids);
  }

  public static Query getShopVersionByName(Session session, String name) {
    return session.createQuery("from ShopVersion  where name =:name").setParameter("name", name);
  }

  public static Query checkCustomerWithoutSendInvitationCodeSms(Session session, Long shopId, List<String> mobiles) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(c.id) from Customer as c,Contact as con where c.shopId=:shopId and c.relationType =:relationType ");
    sql = SQLBuilder.isNotEmpty(sql.append(" and "), "con.mobile");
    sql.append(" and con.customerId = c.id and c.invitationCodeSendDate is null and (c.status is null or c.status != :status) and con.disabled =:disabled and con.mobile not in (:mobiles)");
    Query query = session.createQuery(sql.toString()).setParameterList("mobiles", mobiles);
    query.setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString()).setInteger("disabled", ContactConstant.ENABLED).setParameter("relationType", RelationTypes.UNRELATED);
    return query;
  }

  public static Query checkSupplierWithoutSendInvitationCodeSms(Session session, Long shopId, List<String> mobiles) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(s.id) from Supplier as s,Contact as con where s.shopId=:shopId and s.relationType =:relationType ");
    sql = SQLBuilder.isNotEmpty(sql.append(" and "), "con.mobile");
    sql.append(" and con.supplierId = s.id and s.invitationCodeSendDate is null and (s.status is null or s.status != :status) and con.disabled =:disabled and con.mobile not in (:mobiles) ");
    Query query = session.createQuery(sql.toString()).setParameterList("mobiles", mobiles);
    query.setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setString("status", CustomerStatus.DISABLED.toString()).setParameter("relationType", RelationTypes.UNRELATED);
    return query;
  }

  public static Query getCustomerByShopId(Session session, Long shopId) {
    return session.createQuery("from Customer c where c.shopId=:shopId and (c.status is null or c.status != :status)")
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
  }

  public static Query getVehicleByShopId(Session session, Long shopId) {
    return session.createQuery("from Vehicle v where v.shopId=:shopId and (v.status is null or v.status != :status)")
      .setLong("shopId", shopId).setString("status", VehicleStatus.DISABLED.toString());
  }

  public static Query getUserClientInfoByUserNo(Session session, Long shopId, String userNo, String finger) {
    return session.createQuery("from UserClientInfo  where shopId =:shopId and userNo =:userNo and finger=:finger")
      .setLong("shopId", shopId)
      .setString("userNo", userNo)
      .setString("finger", finger)
      ;
  }

  public static Query getUserClientInfoByFinger(Session session, String finger) {
    return session.createQuery("from UserClientInfo  where finger=:finger")
      .setString("finger", finger)
      ;
  }

  public static Query getUserLoginLogByUserNo(Session session, String sessionId, String userNo, String finger) {
    StringBuilder sb = new StringBuilder("from UserLoginLog  where sessionId =:sessionId and userNo =:userNo");
    if (StringUtil.isNotEmpty(finger)) {
      sb.append(" and finger =:finger");
    }
    Query query = session.createQuery(sb.toString())
      .setString("sessionId", sessionId)
      .setString("userNo", userNo);
    if (StringUtil.isNotEmpty(finger)) {
      query.setString("finger", finger);
    }
    return query;
  }

  public static Query getUserClientLoginLog(Session session, Long startDateTime, Long endDateTime) {
    return session.createQuery("from UserLoginLog u  where u.loginTime>=:startDateTime and u.loginTime <=:endDateTime and finger is not null")
      .setLong("startDateTime", startDateTime)
      .setLong("endDateTime", endDateTime)
      ;
  }

  public static Query getUrlMonitorConfig(Session session, String url) {
    if (StringUtil.isEmpty(url)) {
      return session.createQuery(" from UrlMonitorConfig ");
    } else {
      return session.createQuery(" from UrlMonitorConfig where url=:url").setString("url", url);
    }
  }

  public static Query getSuppliersByShopIdLongs(Session session, Long shopId) {
    return session.createQuery("from Supplier s where s.shopId=:shopId and (s.status is null or s.status != :status)")
      .setLong("shopId", shopId).setString("status", CustomerStatus.DISABLED.toString());
  }


  public static Query getUserGuideFlowByName(Session session, String flowName) {
    StringBuffer sb = new StringBuffer();
    sb.append("from UserGuideFlow where name =:name and isEnabled=:isEnabled");
    return session.createQuery(sb.toString()).setString("name", flowName).setParameter("isEnabled", BooleanEnum.TRUE);
  }

  public static Query getUserGuideHistoryByFlowName(Session session, long userId, String flowName) {
    return session.createQuery("from UserGuideHistory where flowName =:flowName and userId =:userId")
      .setString("flowName", flowName).setLong("userId", userId);
  }

  public static Query getAllUserGuideFlows(Session session) {
    StringBuffer sb = new StringBuffer();
    sb.append("from UserGuideFlow where enabled=:isEnabled");
    return session.createQuery(sb.toString()).setParameter("isEnabled", BooleanEnum.TRUE);
  }

  public static Query getUserGuideStepsByFlowName(Session session, String flowName) {
    StringBuffer sb = new StringBuffer();
    sb.append("from UserGuideStep where flowName =:flowName ");
    return session.createQuery(sb.toString()).setString("flowName", flowName);
  }

  public static Query getUserGuideStepByName(Session session) {
    return session.createQuery("from UserGuideStep");
  }

  public static Query getWaitingUserGuideHistory(Session session, long userId) {
    return session.createQuery("from UserGuideHistory where userId =:userId and status =:status")
      .setLong("userId", userId).setParameter("status", com.bcgogo.enums.user.userGuide.Status.WAITING);
  }

  public static Query getMenuByResourceIds(Session session, Long... resourceIds) {
    return session.createQuery("select m,r from Menu m,Resource r where m.resourceId in (:resourceIds) and r.id=m.resourceId")
      .setParameterList("resourceIds", resourceIds);
  }

  public static Query getMenu(Session session, MenuType menu) {
    return session.createQuery("from Menu where menuName =:menuName").setString("menuName", menu.getName());
  }

  public static Query getUserSwitchMenuIdNotNull(Session session, long shopId) {
    return session.createQuery("from UserSwitch where shopId =:shopId and menuId is not null and status=:status")
      .setLong("shopId", shopId).setString("status", "OFF");
  }

  public static Query getContact(Session session, Long shopId, Long customerId, Long supplierId, String mobile) {
    StringBuilder stringBuilder = new StringBuilder(" from Contact where disabled =:disabled ");
    if (shopId != null) {
      stringBuilder.append(" and shopId=:shopId ");
    }
    if (customerId != null) {
      stringBuilder.append(" and customerId=:customerId ");
    }
    if (supplierId != null) {
      stringBuilder.append(" and supplierId=:supplierId ");
    }
    if (StringUtil.isNotEmpty(mobile)) {
      stringBuilder.append(" and mobile=:mobile");
    }
    Query query = session.createQuery(stringBuilder.toString()).setInteger("disabled", ContactConstant.ENABLED);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    if (StringUtil.isNotEmpty(mobile)) {
      query.setString("mobile", mobile);
    }
    return query;
  }

  // add by zhuj
  public static Query getContactByCusOrSupOrNameOrMobile(Session session, Long customerId, Long supplierId, Long shopId, String name, String... mobiles) {
    StringBuilder stringBuilder = new StringBuilder(" from Contact where disabled =:disabled ");
    if (shopId != null) {
      stringBuilder.append(" and shopId=:shopId ");
    }
    if (customerId != null) {
      stringBuilder.append(" and customerId=:customerId ");
    } else if (supplierId != null) {
      stringBuilder.append(" and supplierId=:supplierId ");
    } else if (StringUtils.isNotBlank(name)) {
      stringBuilder.append(" and name=:name ");
    } else if (ArrayUtil.isNotEmpty(mobiles)) {
      stringBuilder.append(" and mobile in(:mobiles) ");
    }
    stringBuilder.append(" order by level asc ");
    Query query = session.createQuery(stringBuilder.toString());
    query.setInteger("disabled", ContactConstant.ENABLED);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    if (customerId != null) {
      query.setLong("customerId", customerId);
    } else if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    } else if (StringUtils.isNotBlank(name)) {
      query.setString("name", name);
    } else if (ArrayUtil.isNotEmpty(mobiles)) {
      query.setParameterList("mobiles", mobiles);
    }
    return query;
  }

  public static Query getContactByCusAndSup(Session session, Long customerId, Long supplierId) {
    StringBuilder stringBuilder = new StringBuilder(" from Contact where disabled = :disabled and customerId=:customerId and supplierId=:supplierId  order by level asc ");
    Query query = session.createQuery(stringBuilder.toString());
    query.setInteger("disabled", ContactConstant.ENABLED);
    query.setLong("customerId", customerId);
    query.setLong("supplierId", supplierId);
    return query;
  }

  public static Query getContactBySupId(Session session, Long supplierId) {
    return session.createQuery("from Contact where disabled =:disabled and supplierId=:supplierId  order by level asc")
      .setLong("supplierId", supplierId).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getCustomerByIds(Session session, Long shopId, Long... customerIds) {
    return session.createQuery("from Customer c where c.shopId=:shopId and c.id in (:customerIds) and (c.status is null or c.status != :status)").setParameter("shopId", shopId)
      .setParameterList("customerIds", customerIds).setParameter("status", CustomerStatus.ENABLED);
  }

  public static Query getSupplierDTOByIds(Session session, Long shopId, Long... supplierIds) {
    return session.createQuery("from Supplier s where s.shopId=:shopId and s.id in (:supplierIds) and (s.status is null or s.status != :status)").setParameter("shopId", shopId)
      .setParameterList("supplierIds", supplierIds).setParameter("status", CustomerStatus.ENABLED);
  }

  public static Query getContactByCusId(Session session, Long customerId) {
    return session.createQuery("from Contact where disabled =:disabled and customerId=:customerId order by level asc")
      .setLong("customerId", customerId).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getContactByIds(Session session, Long... ids) {
    return session.createQuery("from Contact where disabled =:disabled and id in (:ids )")
      .setParameterList("ids", ids).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getMainContactByCusId(Session session, Long... customerIds) {
    return session.createQuery("from Contact where disabled =:disabled and customerId in (:customerIds ) and isMainContact=1 ")
      .setParameterList("customerIds", customerIds).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getContactsByIds(Session session, Long shopId, Long... ids) {
    StringBuilder sb = new StringBuilder("from Contact where disabled =:disabled and id in (:ids )");
    if (!ArrayUtil.isNotEmpty(ids)) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("ids", ids).setInteger("disabled", ContactConstant.ENABLED);
    if (!ArrayUtil.isNotEmpty(ids)) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getOtherContactsIds(Session session, Long shopId, int start, int pageSize) {
    Query query = session.createQuery("select distinct id from Contact where disabled =:disabled and shopId=:shopId and customerId is null and supplierId is null")
      .setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED).setFirstResult(start).setMaxResults(pageSize);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  // add by zhuj
  public static Query getContactsByCusIds(Session session, List<Long> customerIds) {
    StringBuilder stringBuilder = new StringBuilder(" from Contact  where disabled = :disabled ");
    if (!CollectionUtils.isEmpty(customerIds)) {
      stringBuilder.append(" and customerId in(:customerIds) ");
    }
    stringBuilder.append(" order by level asc ");
    Query query = session.createQuery(stringBuilder.toString());
    query.setInteger("disabled", ContactConstant.ENABLED);
    if (!CollectionUtils.isEmpty(customerIds)) {
      query.setParameterList("customerIds", customerIds);
    }
    return query;
  }

  // add by zhuj
  public static Query getContactsBySupIds(Session session, List<Long> supplierIds) {
    StringBuilder stringBuilder = new StringBuilder(" from Contact where disabled =:disabled ");
    if (!CollectionUtils.isEmpty(supplierIds)) {
      stringBuilder.append(" and supplierId in(:supplierIds) ");
    }
    stringBuilder.append(" order by level asc ");
    Query query = session.createQuery(stringBuilder.toString());
    query.setInteger("disabled", ContactConstant.ENABLED);
    if (!CollectionUtils.isEmpty(supplierIds)) {
      query.setParameterList("supplierIds", supplierIds);
    }
    return query;
  }

  // add by zhuj  用来迁移customer表中的联系人信息
  public static Query getCustomerByPagesizeAndPageNum(Session session, int pageSize, int pageNum) {
    StringBuilder stringBuilder = new StringBuilder(" from Customer ");
    Query query = session.createQuery(stringBuilder.toString());
    query.setMaxResults(pageSize);
    query.setFirstResult(pageNum);
    return query;
  }

  public static Query getAllContactGroup(Session session) {
    return session.createQuery("from ContactGroup");
  }

  public static Query getContactGroupByIds(Session session, Long[] groupIds) {
    return session.createQuery("from ContactGroup where id in (:groupIds)").setParameterList("groupIds", groupIds);
  }

  public static Query countCustomer(Session session) {
    StringBuilder stringBuilder = new StringBuilder("select count(*) from Customer ");
    Query query = session.createQuery(stringBuilder.toString());
    return query;
  }

  // add by zhuj 用来迁移supplier表中的联系人信息
  public static Query getSupplierByPagesizeAndPageNum(Session session, int pageSize, int pageNum) {
    StringBuilder stringBuilder = new StringBuilder(" from Supplier ");
    Query query = session.createQuery(stringBuilder.toString());
    query.setMaxResults(pageSize);
    query.setFirstResult(pageNum);
    return query;
  }

  public static Query countSupplier(Session session) {
    StringBuilder stringBuilder = new StringBuilder("select count(*) from Supplier ");
    Query query = session.createQuery(stringBuilder.toString());
    return query;
  }

  public static Query countClientBindingLog(Session session, Long shopId, String userNo, String mac) {
    return session.createQuery("select count(*) from ClientBindingLog where shopId=:shopId and userNo=:userNo and mac=:mac")
      .setLong("shopId", shopId).setString("userNo", userNo).setString("mac", mac);
  }

  public static Query getSupplierByIds(Session session, Set<Long> supplierIds) {
    return session.createQuery("from Supplier where id in (:supplierIds)")
      .setParameterList("supplierIds", supplierIds);
  }

  public static Query getCustomerToInitContact(Session session) {
    StringBuffer sb = new StringBuffer();
    sb.append("select c.* from customer c LEFT JOIN contact t on c.id = t.customer_id ")
      .append("where t.id is null and (LENGTH(c.contact)>1 or LENGTH(c.mobile)>1 or LENGTH(c.email) >1 or LENGTH(c.qq)>1) ");
    return session.createSQLQuery(sb.toString()).addEntity(Customer.class);
  }

  public static Query getSupplierToInitContact(Session session) {
    StringBuffer sb = new StringBuffer();
    sb.append("select c.* from supplier c LEFT JOIN contact t on c.id = t.supplier_id ")
      .append("where t.id is null and (LENGTH(c.contact)>1 or LENGTH(c.mobile)>1 or LENGTH(c.email) >1 or LENGTH(c.qq)>1) ");
    return session.createSQLQuery(sb.toString()).addEntity(Supplier.class);
  }

  /*public static Query getCustomerByNameMobilePhone(Session session, Long shopId, String name, String mobile,String phone) {
    if (StringUtils.isNotBlank(name) && StringUtils.isBlank(mobile) && StringUtils.isBlank(phone)){
      return session.createQuery("select c from Customer as c where c.shopId = :shopId and name=:name and (c.status is null or c.status != :status)")
          .setLong("shopId", shopId).setString("name", name).setString("status",CustomerStatus.DISABLED.toString());
    }else if(StringUtils.isNotBlank(name)){

    }
    if (mobile == null)
      return session.createQuery("select c from Customer as c where c.shopId = :shopId and c.mobile is null and name=:name and (c.status is null or c.status != :status)")
          .setLong("shopId", shopId).setString("name", name).setString("status",CustomerStatus.DISABLED.toString());
    else

  }*/

  public static Query deleteCustomerSupplierBusinessScope(Session session, Long shopId, Long customerId, Long supplierId) {
    StringBuffer sb = new StringBuffer(" delete from BusinessScope where 1=1 ");
    if (shopId != null) {
      sb.append(" and shopId =:shopId ");
    }
    if (customerId != null) {
      sb.append(" and customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append(" and supplierId =:supplierId ");
    }
    Query query = session.createQuery(sb.toString());

    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    return query;
  }

  public static Query getCustomerSupplierBusinessScope(Session session, Long shopId, Long customerId, Long supplierId) {
    StringBuffer sb = new StringBuffer(" from BusinessScope where 1=1 ");
    if (shopId != null) {
      sb.append(" and shopId =:shopId ");
    }
    if (customerId != null) {
      sb.append(" and customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append(" and supplierId =:supplierId ");
    }
    Query query = session.createQuery(sb.toString());

    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    return query;
  }

  public static Query getCustomerSupplierBusinessScope(Session session, Long shopId, Set<Long> customerIdSet, Set<Long> supplierIdSet) {
    StringBuffer sb = new StringBuffer(" from BusinessScope where 1=1 ");
    if (shopId != null) {
      sb.append(" and shopId =:shopId ");
    }
    if (CollectionUtils.isNotEmpty(customerIdSet)) {
      sb.append(" and customerId in(:customerId) ");
    }
    if (CollectionUtils.isNotEmpty(supplierIdSet)) {
      sb.append(" and supplierId in(:supplierId) ");
    }
    Query query = session.createQuery(sb.toString());

    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    if (CollectionUtils.isNotEmpty(customerIdSet)) {
      query.setParameterList("customerId", customerIdSet);
    }
    if (CollectionUtils.isNotEmpty(supplierIdSet)) {
      query.setParameterList("supplierId", supplierIdSet);
    }
    return query;
  }

  public static Query countUserNo(Session session, String userNo) {
    return session.createQuery("select count(*) from User where  userNo=:userNo").setString("userNo", userNo);
  }

  public static Query countAppUserMobile(Session session, String mobile, AppUserType appUserType) {
    return session.createQuery("select count(*) from AppUser where  mobile=:mobile and status !=:status and appUserType =:appUserType ")
      .setString("mobile", mobile)
      .setParameter("status", Status.deleted).setParameter("appUserType", appUserType);
  }

  public static Query countAppUserNo(Session session, String appUserNo, AppUserType appUserType) {
    return session.createQuery("select count(*) from AppUser where  appUserNo=:appUserNo and status !=:status and appUserType =:appUserType ")
      .setString("appUserNo", appUserNo)
      .setParameter("status", Status.deleted).setParameter("appUserType", appUserType);
  }

  public static Query countCustomerByName(Session session, String name, Long customerId, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(c.id) from Customer c where c.shopId=:shopId and (c.status is null or c.status != :status) and c.name =:name");
    if (customerId != null) {
      sb.append(" and c.id <>:customerId ");
    }
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", shopId)
      .setString("status", CustomerStatus.DISABLED.toString())
      .setString("name", name);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    return query;
  }

  public static Query countCustomerByNameAndMobile(Session session, String name, String mobile, Long customerId, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(distinct c.id) amount from customer c left join contact con on c.id = con.customer_id where " +
      "c.shop_id=:shopId and (c.status is null or c.status != :status) and c.name =:name ");
    if (customerId != null) {
      sb.append(" and c.id <>:customerId ");
    }
    if (StringUtils.isEmpty(mobile)) {
      SQLBuilder.isEmpty(sb, "con.mobile");
    } else {
      sb.append(" and con.mobile =:mobile");
    }
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("amount", StandardBasicTypes.INTEGER)
      .setLong("shopId", shopId)
      .setString("status", CustomerStatus.DISABLED.toString())
      .setString("name", name);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (StringUtils.isNotEmpty(mobile)) {
      query.setString("mobile", mobile);
    }
    return query;
  }

  public static Query countSupplierBySupplierShopId(Session session, Long supplierShopId) {
    String sql = "select count(*) from Supplier where supplierShopId =:supplierShopId and status=:disabled ";
    return session.createQuery(sql).setLong("supplierShopId", supplierShopId).setParameter("disabled", CustomerStatus.ENABLED);
  }


  public static Query getVehicleMapByLicenceNos(Session session, Long shopId, Set<String> licenceNos) {
    return session.createQuery("from Vehicle where shopId = :shopId and (status is null or status!=:status) and licenceNo in (:licenceNo)")
      .setLong("shopId", shopId).setParameterList("licenceNo", licenceNos).setParameter("status", VehicleStatus.DISABLED);
  }

  public static Query getCustomerShopIds(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select c.customerShopId from Customer c where c.shopId=:shopId and (c.status is null or c.status != :status) ");
    sb.append(" and c.customerShopId is not null");
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", shopId)
      .setString("status", CustomerStatus.DISABLED.toString());
    return query;
  }

  public static Query getRelatedShopUpdateTaskByShopId(Session session, Long shopId, ExeStatus exeStatus) {
    StringBuffer sb = new StringBuffer();
    sb.append("from RelatedShopUpdateTask r where r.shopId=:shopId ");
    if (exeStatus != null) {
      sb.append(" and r.exeStatus =:exeStatus ");
    }
    sb.append(" order by r.createdTime asc");
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", shopId);
    if (exeStatus != null) {
      query.setParameter("exeStatus", exeStatus);
    }
    return query;
  }

  public static Query getFirstRelatedShopUpdateTask(Session session, ExeStatus exeStatus) {
    StringBuffer sb = new StringBuffer();
    sb.append("from RelatedShopUpdateTask r where 1=1 ");
    if (exeStatus != null) {
      sb.append(" and r.exeStatus =:exeStatus ");
    }
    sb.append(" order by r.createdTime asc");
    Query query = session.createQuery(sb.toString()).setMaxResults(1);
    if (exeStatus != null) {
      query.setParameter("exeStatus", exeStatus);
    }
    return query;
  }

  public static Query getContactsByCustomerMobile(Session session, Long shopId, String mobile, int limit) {
    StringBuffer hql = new StringBuffer();
    hql.append("select con.* from contact con  left join customer c on c.id = con.customer_id ");
    hql.append(" where (c.status is null or c.status != :status) and con.mobile like :mobile ");
    if (shopId != null) {
      hql.append(" and shopId =:shopId ");
    }
    Query query = session.createSQLQuery(hql.toString())
      .addEntity(Contact.class)
      .setString("status", CustomerStatus.DISABLED.toString())
      .setString("mobile", "%" + mobile + "%");
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getContactByCustomerMobiles(Session session, Long shopId, Set<String> mobiles, Set<Long> excludeCustomerIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select con.* from contact con  left join customer c on c.id = con.customer_id ");
    sb.append(" where c.shop_id=:shopId and (c.status is null or c.status != :status) ");
    sb.append(" and con.mobile in(:mobiles) ");
    if (CollectionUtils.isNotEmpty(excludeCustomerIds)) {
      sb.append(" and c.id not in(:excludeCustomerIds) ");
    }
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(Contact.class)
      .setLong("shopId", shopId)
      .setString("status", CustomerStatus.DISABLED.toString())
      .setParameterList("mobiles", mobiles);
    if (CollectionUtils.isNotEmpty(excludeCustomerIds)) {
      query.setParameterList("excludeCustomerIds", excludeCustomerIds);
    }
    return query;
  }

  public static Query getContactsBySupplierMobiles(Session session, Long shopId, Set<String> mobiles, Set<Long> excludeSupplierIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("select con.* from contact con  left join supplier s on s.id = con.supplier_id ");
    sb.append(" where s.shop_id=:shopId and (s.status is null or s.status != :status) ");
    sb.append(" and con.mobile in(:mobiles) ");
    if (CollectionUtils.isNotEmpty(excludeSupplierIds)) {
      sb.append(" and s.id not in(:excludeSupplierIds) ");
    }
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(Contact.class)
      .setLong("shopId", shopId)
      .setString("status", CustomerStatus.DISABLED.toString())
      .setParameterList("mobiles", mobiles);
    if (CollectionUtils.isNotEmpty(excludeSupplierIds)) {
      query.setParameterList("excludeSupplierIds", excludeSupplierIds);
    }
    return query;
  }

  public static Query getCusOrSupOrderIndexScheduleDTOs(Session session) {
    StringBuffer sb = new StringBuffer();
    sb.append("select s from CusOrSupOrderIndexSchedule as s where s.exeStatus =:exeStatus");
    Query query = session.createQuery(sb.toString());
    query.setParameter("exeStatus", ExeStatus.READY);
    return query;
  }

  public static Query getCusOrSupOrderIndexScheduleDTOByCusOrSupId(Session session, Long shopId, Long customerId, Long supplierId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select s from CusOrSupOrderIndexSchedule as s where s.shopId =:shopId ");
    if (customerId != null) {
      sb.append(" and s.customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append(" and s.supplierId =:supplierId ");
    }
    sb.append(" and s.exeStatus =:exeStatus");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("exeStatus", ExeStatus.READY);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    return query;
  }

  public static Query getCustomerByKey(Session session, Long shopId, String key, int rowStart, int pageSize) {
    Query q = session.createQuery("select distinct c from Customer c, Contact con " +
      "where c.id = con.customerId and con.disabled = :disabled and c.shopId = :shopId and (c.status is null or c.status != :status) " +
      "and (con.mobile like :key or c.name like :key or con.name like :key or c.landLine like :key or c.company like :key " +
      "or c.id in (select m.customerId from Member as m where m.memberNo = :memberNo))");
    q.setLong("shopId", shopId).setString("key", "%" + key + "%").setString("memberNo", key)
      .setString("status", CustomerStatus.DISABLED.toString()).setInteger("disabled", ContactConstant.ENABLED)
      .setFirstResult(rowStart).setMaxResults(pageSize);
    return q;
  }


  public static Query getBindingAppVehicleByVinUserNo(Session session, String vin, String appUserNo) {
    return session.createSQLQuery("select av.* from app_vehicle av where vehicle_vin=:vehicleVin and app_user_no=:app_user_no and status =:status ")
      .addEntity(AppVehicle.class)
      .setString("vehicleVin", vin).setString("app_user_no", appUserNo).setString("status", Status.active.toString());
  }

  public static Query getVehicleBasicInfoByVin(Session session, String vin) {
    return session.createQuery("from VehicleBasicInfo where vehicleVin =:vehicleVin").setString("vehicleVin", vin);
  }

  public static Query getAppUserByUserNo(Session session, String appUserNo, String mobile) {
    StringBuilder sb = new StringBuilder();
    sb.append(" from AppUser as u where 1=1 ");
    if (StringUtils.isNotEmpty(appUserNo)) {
      sb.append(" and appUserNo =:appUserNo");
    }
    if (StringUtils.isNotEmpty(mobile)) {
      sb.append(" and mobile =:mobile");
    }

    Query query = session.createQuery(sb.toString());
    if (StringUtils.isNotEmpty(mobile)) {
      query.setString("mobile", mobile);
    }
    if (StringUtils.isNotEmpty(appUserNo)) {
      query.setString("appUserNo", appUserNo);
    }
    return query;
  }

  public static Query getAppUserByUserNos(Session session, Set<String> appUserNos) {
    StringBuilder sb = new StringBuilder();
    sb.append(" from AppUser as u where  appUserNo in(:appUserNos)");
    Query query = session.createQuery(sb.toString());
    query.setParameterList("appUserNos", appUserNos);
    return query;
  }

  public static Query getBundlingObdUserVehicleByObdId(Session session, Long obdId) {
    return session.createQuery("from ObdUserVehicle where obdId =:obdId and status =:status")
      .setLong("obdId", obdId).setParameter("status", ObdUserVehicleStatus.BUNDLING);
  }

  public static Query getBundlingObdUserVehicleByUserNo(Session session, String appUserNo) {
    return session.createQuery("from ObdUserVehicle where appUserNo =:appUserNo and status =:status")
      .setString("appUserNo", appUserNo).setParameter("status", ObdUserVehicleStatus.BUNDLING);
  }

  public static Query getBundlingObdUserVehicleByUserNos(Session session, Set<String> appUserNos) {
    return session.createQuery("from ObdUserVehicle where appUserNo in(:appUserNos) and status =:status")
      .setParameterList("appUserNos", appUserNos).setParameter("status", ObdUserVehicleStatus.BUNDLING);
  }

  public static Query getObdUserVehicle(Session session, String appUserNo, Long vehicleId) {
    return session.createQuery("from ObdUserVehicle where appUserNo =:appUserNo and status !=:status and appVehicleId=:vehicleId")
      .setString("appUserNo", appUserNo).setLong("vehicleId", vehicleId).setParameter("status", ObdUserVehicleStatus.DELETED);
  }

  public static Query getUnBundlingObdUserVehicle(Session session, String userNo, Long vehicleId, Long obdId) {
    return session.createQuery("from ObdUserVehicle where appUserNo =:appUserNo and status =:status and obdId=:obdId and appVehicleId=:appVehicleId")
      .setString("appUserNo", userNo)
      .setLong("obdId", obdId)
      .setLong("appVehicleId", vehicleId)
      .setParameter("status", ObdUserVehicleStatus.UN_BUNDLING);
  }

  public static Query getObdUserVehicle(Session session, Long vehicleId, Long obdId, ObdUserVehicleStatus status) {
    StringBuilder sb = new StringBuilder("from ObdUserVehicle where obdId=:obdId and appVehicleId=:appVehicleId");
    if (status != null) {
      sb.append(" and status =:status");
    }
    Query query = session.createQuery(sb.toString())
      .setLong("obdId", obdId)
      .setLong("appVehicleId", vehicleId);

    if (status != null) {
      query.setParameter("status", status);
    }
    return query;
  }

  public static Query getAppVehicleByUserNoVehicleVin(Session session, String appUserNo, String vehicleVin) {
    return session.createQuery("from AppVehicle where appUserNo =:appUserNo and vehicleVin=:vehicleVin and status =:status")
      .setString("appUserNo", appUserNo)
      .setString("vehicleVin", vehicleVin)
      .setParameter("status", Status.active)
      .setMaxResults(1);
  }

  public static Query getAppVehicleByUserNoVehicleNo(Session session, String appUserNo, String vehicleNo) {
    StringBuilder sb = new StringBuilder();
    sb.append(" from AppVehicle as u where status =:status");
    if (StringUtils.isNotEmpty(appUserNo)) {
      sb.append(" and appUserNo =:appUserNo");
    }
    if (StringUtils.isNotEmpty(vehicleNo)) {
      sb.append(" and vehicleNo =:vehicleNo");
    }

    Query query = session.createQuery(sb.toString());
    if (StringUtils.isNotEmpty(vehicleNo)) {
      query.setString("vehicleNo", vehicleNo);
    }
    if (StringUtils.isNotEmpty(appUserNo)) {
      query.setString("appUserNo", appUserNo);
    }
    return query.setParameter("status", Status.active);
  }


  public static Query getAppVehicleByAppUserNo(Session session, String appUserNo) {
    return session.createQuery("from AppVehicle where appUserNo =:appUserNo and status =:status")
      .setString("appUserNo", appUserNo).setParameter("status", Status.active);
  }

  public static Query getAppVehicleByAppUserNos(Session session, Set<String> appUserNos) {
    return session.createQuery("from AppVehicle where appUserNo in(:appUserNos) and status =:status")
      .setParameterList("appUserNos", appUserNos).setParameter("status", Status.active);
  }

  public static Query getAppVehicleByIds(Session session, Set<Long> ids) {
    return session.createQuery("from AppVehicle where id in(:ids) and status =:status")
      .setParameterList("ids", ids).setParameter("status", Status.active);
  }

  public static Query getAppVehicleByAppUserNoAndVehicleNo(Session session, String appUserNo, String vehicleNo, Status status) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppVehicle where appUserNo =:appUserNo  and vehicleNo =:vehicleNo ");
    if (status != null) {
      sb.append("and status =:status ");
    }
    Query query = session.createQuery(sb.toString());
    query.setString("appUserNo", appUserNo).setParameter("vehicleNo", vehicleNo);
    if (status != null) {
      query.setParameter("status", status);
    }
    return query;
  }

  public static Query getAppVehicleNosByAppUserNo(Session session, String appUserNo) {
    return session.createQuery("select vehicleNo from AppVehicle where appUserNo =:appUserNo and status =:status")
      .setString("appUserNo", appUserNo).setParameter("status", Status.active);
  }

  public static Query getAppUserLoginInfoBySessionId(Session session, String sessionId) {
    return session.createQuery("from AppUserLoginInfo  where sessionId=:sessionId and status=:status")
      .setString("sessionId", sessionId)
      .setParameter("status", Status.active);
  }

  public static Query getAppUserLoginInfoByMqSessionId(Session session, String mqSessionId, AppUserType appUserType) {
    return session.createQuery("from AppUserLoginInfo  where mqSessionId=:mqSessionId and appUserType=:appUserType and status=:status")
      .setString("mqSessionId", mqSessionId)
      .setParameter("appUserType", appUserType)
      .setParameter("status", Status.active);
  }

  public static Query getAppUserLoginInfoByUserNo(Session session, String appUserNo, AppUserType appUserType) {
    StringBuilder sb = new StringBuilder();
    sb.append(" from AppUserLoginInfo  where appUserNo=:appUserNo and status=:status ");
    if (appUserType != null) {
      sb.append(" and appUserType=:appUserType ");
    }

    Query query = session.createQuery(sb.toString())
      .setString("appUserNo", appUserNo)
      .setParameter("status", Status.active);
    if (appUserType != null) {
      query.setParameter("appUserType", appUserType);
    }
    return query;
  }

  public static Query getObdUserVehicleByAppUserNo(Session session, String appUserNo) {
    return session.createQuery("from ObdUserVehicle  where appUserNo=:appUserNo and status!=:status order by lastModified desc")
      .setString("appUserNo", appUserNo)
      .setParameter("status", ObdUserVehicleStatus.DELETED);
  }


  public static Query getAppUserCustomer(Session session, String userNo, Long shopId) {
    return session.createSQLQuery("select auc.* from app_user_customer auc left join customer cus on cus.id=auc.customer_id where auc.app_user_no=:userNo and auc.shop_id =:shopId and (cus.status is null or cus.status = :status) ")
      .addEntity(AppUserCustomer.class)
      .setParameter("status", CustomerStatus.ENABLED.name())
      .setString("userNo", userNo)
      .setLong("shopId", shopId);
  }


  public static Query getAppUserCustomer(Session session, Long shopId) {
    return session.createSQLQuery("select auc.* from app_user_customer auc left join customer cus on cus.id=auc.customer_id where  auc.shop_id =:shopId and (cus.status is null or cus.status = :status) ")
      .addEntity(AppUserCustomer.class)
      .setParameter("status", CustomerStatus.ENABLED.name())
      .setLong("shopId", shopId);
  }

  public static Query countAppUserCustomerUpdateTask(Session session, ExeStatus exeStatus) {
    return session.createQuery("select count(*) from AppUserCustomerUpdateTask as cr where cr.exeStatus = :exeStatus ")
      .setParameter("exeStatus", exeStatus);
  }

  public static Query getAppUserCustomerUpdateTask(Session session, Set<Long> operatorIdSet, OperatorType operatorType, ExeStatus exeStatus) {
    return session.createQuery(" from AppUserCustomerUpdateTask as cr where cr.exeStatus = :exeStatus " +
      " and cr.operatorId in(:operatorIdSet) and operatorType=:operatorType ")
      .setParameter("exeStatus", exeStatus).setParameterList("operatorIdSet", operatorIdSet).setParameter("operatorType", operatorType);
  }

  public static Query getAppUserCustomerUpdateTask(Session session, ExeStatus exeStatus, Pager pager) {
    return session.createQuery(" from AppUserCustomerUpdateTask as cr where cr.exeStatus = :exeStatus ")
      .setParameter("exeStatus", exeStatus).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query getCustomerVehicleByCustomerIds(Session session, Set<Long> customerIdSet) {
    return session.createQuery("select cv from CustomerVehicle as cv where " +
      " cv.customerId in(:customerId) and (status is NULL or status =:status)")
      .setParameterList("customerId", customerIdSet).setParameter("status", VehicleStatus.ENABLED);
  }

  public static Query getAppUserCustomerByAppUserNo(Session session, Set<String> appUserNos) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from app_user_customer as u left join customer cus on cus.id=u.customer_id where (cus.status is null or cus.status = :status) " +
      "and u.app_user_no in (:appUserNos)");
    Query query = session.createSQLQuery(sb.toString()).addEntity(AppUserCustomer.class).setParameter("status", CustomerStatus.ENABLED.name())
      .setParameterList("appUserNos", appUserNos);
    return query;
  }

  public static Query getAppUserCustomerByAppUserNo(Session session, Set<String> appUserNos, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from app_user_customer as u left join customer cus on cus.id=u.customer_id where (cus.status is null or cus.status = :status) " +
      "and u.app_user_no in (:appUserNos) and u.shop_id =:shopId");
    Query query = session.createSQLQuery(sb.toString()).addEntity(AppUserCustomer.class).setParameter("status", CustomerStatus.ENABLED.name())
      .setParameter("shopId", shopId)
      .setParameterList("appUserNos", appUserNos);
    return query;
  }

  public static Query getCustomerByObdIds(Session session, Long shopId, Set<Long> obdIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select ouv.obd_id,cus.* from obd_sim_bind osb " +
      "join obd_user_vehicle ouv on osb.obd_id=ouv.obd_id  " +
      "join app_user_customer auc on ouv.app_user_no=auc.app_user_no  " +
      "join customer cus on cus.id=auc.customer_id " +
      "where (cus.status is null or cus.status = :status) " +
      "and ouv.obd_id in (:obdIds) and auc.shop_id =:shopId");
    Query query = session.createSQLQuery(sb.toString()).addScalar("obd_id", StandardBasicTypes.LONG)
      .addEntity(Customer.class).setParameter("status", CustomerStatus.ENABLED.name())
      .setParameter("shopId", shopId)
      .setParameterList("obdIds", obdIds);
    return query;
  }

  public static Query getVehicleDTOMapByObdIds(Session session, Long shopId, Set<Long> obdIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select ouv.obd_id,v.* from obd_sim_bind osb " +
      "join obd_user_vehicle ouv on osb.obd_id=ouv.obd_id  " +
      "join vehicle v on ouv.app_vehicle_id=v.id  " +
      "where (v.status is null or v.status = :status) " +
      "and ouv.obd_id in (:obdIds)");
    Query query = session.createSQLQuery(sb.toString()).addScalar("obd_id", StandardBasicTypes.LONG)
      .addEntity(Vehicle.class).setParameter("status", CustomerStatus.ENABLED.name())
      .setParameterList("obdIds", obdIds);
    return query;
  }

  public static Query getObdSimBindByShopExact(Session session, String imei, String mobile) {
    StringBuilder sb = new StringBuilder();
    sb.append("select os.mobile,o.imei,o.sell_shop_id,v.id as vehicle_id,v.licence_no,o.id as obd_id,os.id as sim_id ,ouv.app_vehicle_id as app_vehicle_id ,o.obd_type as obd_type" +
      " from obd_sim_bind osb" +
      " join obd_sim os on osb.sim_id=os.id" +
      " join obd o on o.id=osb.obd_id" +
      " left join obd_user_vehicle ouv on ouv.obd_id=o.id and (ouv.status='BUNDLING' or ouv.status='UN_BUNDLING')" +
      " left join vehicle v on v.gsm_obd_imei=o.imei and (v.status is null or v.status ='ENABLED')");
    sb.append(" where osb.status='ENABLED' and o.imei=:imei and os.mobile=:mobile and o.obd_status in (:obdStatusList) and os.status in (:obdStatusList)  ");
    return session.createSQLQuery(sb.toString())
      .addScalar("mobile", StandardBasicTypes.STRING)
      .addScalar("imei", StandardBasicTypes.STRING)
      .addScalar("sell_shop_id", StandardBasicTypes.LONG)
      .addScalar("vehicle_id", StandardBasicTypes.LONG)
      .addScalar("licence_no", StandardBasicTypes.STRING)
      .addScalar("obd_id", StandardBasicTypes.LONG)
      .addScalar("sim_id", StandardBasicTypes.LONG)
      .addScalar("app_vehicle_id", StandardBasicTypes.LONG)
      .addScalar("obd_type", StandardBasicTypes.STRING)
//      .setParameter("status",ObdSimBindStatus.ENABLED)
      .setParameter("imei", imei).setParameter("mobile", mobile)
      .setParameterList("obdStatusList", OBDStatus.EnabledStatusStrArr);
  }

  public static Query getObdSimBindByShop(Session session, ObdSimSearchCondition condition) throws ParseException {
    StringBuilder sb = new StringBuilder();
    sb.append("select o.storage_time,os.mobile,o.imei,os.use_date,os.use_period,o.sell_time,o.obd_status,o.id as obd_id,cus.id as cus_id," +
      "cus.name,cus.mobile as cus_mobile,v.id as vehicle_id,v.licence_no,v.model,v.brand,o.sell_shop_id ,ouv.app_vehicle_id as app_vehicle_id,o.obd_sim_type as obdSimType " +
      "from obd_sim_bind osb " +
      "join obd_sim os on osb.sim_id=os.id " +
      "join obd o on o.id=osb.obd_id " +
      "left join obd_user_vehicle ouv on o.id=ouv.obd_id and (ouv.status='BUNDLING' or ouv.status='UN_BUNDLING') " +
      "left join vehicle v on v.gsm_obd_imei=o.imei and (v.status is null or v.status ='ENABLED') " +
      "left join customer_vehicle cv on cv.vehicle_id=v.id and (cv.status is null or cv.status ='ENABLED') " +
      "left join customer cus on cus.id=cv.customer_id and  (cus.status is null or cus.status = :status) ");
    sb.append(" where osb.status=:status  and o.obd_status in (:obdStatusList) and os.status in (:obdStatusList) and (ouv.status is null or ouv.status='BUNDLING' or ouv.status='UN_BUNDLING')");
    if (condition.getObdId() != null) {
      sb.append(" and o.id =:obdId");
    }
    if (condition.getSellShopId() != null) {
      sb.append(" and o.sell_shop_id =:shopId");
      sb.append(" and (v.shop_id is null or v.shop_id =:shopId)");
    }
    if (StringUtil.isNotEmpty(condition.getLicenceNo())) {
      sb.append(" and v.licence_no like :licenceNo");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleBrand())) {
      sb.append(" and v.brand like :vehicleBrand");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleModel())) {
      sb.append(" and v.model like :vehicleModel");
    }
    if (StringUtil.isNotEmpty(condition.getEngineNo())) {
      sb.append(" and v.engine_no like :engineNo");
    }
    if (StringUtil.isNotEmpty(condition.getChassisNumber())) {
      sb.append(" and v.chassis_number like :chassisNumber");
    }
    if (StringUtil.isNotEmpty(condition.getImei())) {
      sb.append(" and o.imei like :imei");
    }
    if (StringUtil.isNotEmpty(condition.getMobile())) {
      sb.append(" and os.mobile like :mobile");
    }
    if (StringUtil.isNotEmpty(condition.getStartTimeStr())) {
      if (ArrayUtil.contains(condition.getObdStatusList(), "SOLD")) {
        sb.append(" and o.sell_time>=:startTime");
      } else {
        sb.append(" and o.storage_time>=:startTime");
      }
    }
    if (StringUtil.isNotEmpty(condition.getEndTimeStr())) {
      if (ArrayUtil.contains(condition.getObdStatusList(), "SOLD")) {
        sb.append(" and o.sell_time<=:endTime");
      } else {
        sb.append(" and o.storage_time<=:endTime");
      }
    }
    if (ArrayUtil.isNotEmpty(condition.getObdStatusList())) {
      sb.append(" and o.obd_status in (:obdStatuses) ");
    }
    if (ArrayUtil.isNotEmpty(condition.getUserTypes()) && condition.getUserTypes().length == 1) {
      ObdSimSearchCondition.UserType userType = condition.getUserTypes()[0];
      if (ObdSimSearchCondition.UserType.FREE.equals(userType)) {
        sb.append(" and (os.use_date+").append(DateUtil.YEAR_MILLION_SECONDS).append("*os.use_period)>=").append(System.currentTimeMillis());
      } else if (ObdSimSearchCondition.UserType.NOT_FREE.equals(userType)) {
        sb.append(" and (os.use_date+").append(DateUtil.YEAR_MILLION_SECONDS).append("*os.use_period)<=").append(System.currentTimeMillis());
      }
    }
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("storage_time", StandardBasicTypes.LONG)
      .addScalar("mobile", StandardBasicTypes.STRING)
      .addScalar("imei", StandardBasicTypes.STRING)
      .addScalar("use_date", StandardBasicTypes.LONG)
      .addScalar("use_period", StandardBasicTypes.INTEGER)
      .addScalar("sell_time", StandardBasicTypes.LONG)
      .addScalar("obd_status", StandardBasicTypes.STRING)
      .addScalar("obd_id", StandardBasicTypes.LONG)
      .addScalar("cus_id", StandardBasicTypes.LONG)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("cus_mobile", StandardBasicTypes.STRING)
      .addScalar("vehicle_id", StandardBasicTypes.LONG)
      .addScalar("licence_no", StandardBasicTypes.STRING)
      .addScalar("model", StandardBasicTypes.STRING)
      .addScalar("brand", StandardBasicTypes.STRING)
      .addScalar("sell_shop_id", StandardBasicTypes.LONG)
      .addScalar("app_vehicle_id", StandardBasicTypes.LONG)
      .addScalar("obdSimType", StandardBasicTypes.STRING)  //新增后视镜和OBD类型
      .setParameter("status", CustomerStatus.ENABLED.name())
      .setParameterList("obdStatusList", OBDStatus.EnabledStatusStrArr);
    Pager pager = condition.getPager();
    if (pager != null) {
      query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize())
      ;
    }
    if (condition.getObdId() != null) {
      query.setParameter("obdId", condition.getObdId());
    }
    if (condition.getSellShopId() != null) {
      query.setParameter("shopId", condition.getSellShopId());
    }
    if (StringUtil.isNotEmpty(condition.getLicenceNo())) {
      query.setParameter("licenceNo", "%" + condition.getLicenceNo() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleBrand())) {
      query.setParameter("vehicleBrand", "%" + condition.getVehicleBrand() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleModel())) {
      query.setParameter("vehicleModel", "%" + condition.getVehicleModel() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getEngineNo())) {
      query.setParameter("engineNo", "%" + condition.getEngineNo() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getChassisNumber())) {
      query.setParameter("chassisNumber", "%" + condition.getChassisNumber() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getImei())) {
      query.setParameter("imei", "%" + condition.getImei() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getMobile())) {
      query.setParameter("mobile", "%" + condition.getMobile() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getStartTimeStr())) {
      query.setParameter("startTime", DateUtil.getStartTimeOfDate(condition.getStartTimeStr()));
    }
    if (StringUtil.isNotEmpty(condition.getEndTimeStr())) {
      query.setParameter("endTime", DateUtil.getEndTimeOfDate(condition.getEndTimeStr()));
    }
    if (ArrayUtil.isNotEmpty(condition.getObdStatusList())) {
      query.setParameterList("obdStatuses", condition.getObdStatusList());
    }
    return query;
  }

  public static Query getAppUserMapByCustomerIds(Session session, Set<Long> customerIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select cus.id as customerId,au.* from app_user_customer as u inner join customer cus on cus.id=u.customer_id " +
      "inner join app_user au on u.app_user_no=au.app_user_no where (cus.status is null or cus.status != :status) ");
    if (CollectionUtil.isNotEmpty(customerIds)) {
      sb.append(" and cus.id in (:customerIds)");
    }
    Query query = session.createSQLQuery(sb.toString()).addScalar("customerId", StandardBasicTypes.LONG)
      .addEntity(AppUser.class).setParameter("status", CustomerStatus.DISABLED.name());
    if (CollectionUtil.isNotEmpty(customerIds)) {
      query.setParameterList("customerIds", customerIds);
    }
    return query;
  }

  public static Query getAppUserCustomerIds(Session session, Long... customerIds) {
    return session.createQuery("from AppUserCustomer where customerId in (:customerIds)").setParameterList("customerIds", customerIds);
  }


  public static Query getAppUserCustomer(Session session, String appUserNo, Long customerId, Long shopId) {

    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from app_user_customer as u left join customer cus on cus.id=u.customer_id where (cus.status is null or cus.status != :status)");
    if (StringUtils.isNotEmpty(appUserNo)) {
      sb.append(" and u.app_user_no =:appUserNo");
    }
    if (customerId != null) {
      sb.append(" and u.customer_id=:customerId ");
    }
    if (shopId != null) {
      sb.append(" and u.shop_id=:shopId ");
    }

    Query query = session.createSQLQuery(sb.toString()).addEntity(AppUserCustomer.class).setParameter("status", CustomerStatus.DISABLED.name());
    if (StringUtils.isNotEmpty(appUserNo)) {
      query.setString("appUserNo", appUserNo);
    }
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    return query;
  }

  public static Query getAppUserCustomerAndVehicle(Session session, String appUserNo, Long customerId, Long shopId,
                                                   Long appVehicleId, Long shopVehicleId, AppUserCustomerMatchType matchType) {
    StringBuilder sb = new StringBuilder();
    sb.append(" from AppUserCustomer as u ");
    sb.append(" where u.appUserNo =:appUserNo");
    sb.append(" and u.shopId=:shopId ");
    sb.append(" and u.matchType=:matchType ");
    if (customerId != null) {
      sb.append(" and u.customerId=:customerId ");
    } else {
      sb.append(" and u.customerId is null ");
    }
    if (appVehicleId != null) {
      sb.append(" and u.appVehicleId=:appVehicleId ");
    } else {
      sb.append(" and u.appVehicleId is null ");
    }
    if (shopVehicleId != null) {
      sb.append(" and u.shopVehicleId=:shopVehicleId ");
    } else {
      sb.append(" and u.shopVehicleId is null ");
    }
    Query query = session.createQuery(sb.toString())
      .setString("appUserNo", appUserNo)
      .setLong("shopId", shopId)
      .setParameter("matchType", matchType);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (appVehicleId != null) {
      query.setLong("appVehicleId", appVehicleId);
    }
    if (shopVehicleId != null) {
      query.setLong("shopVehicleId", shopVehicleId);
    }
    return query;
  }


  public static Query getMemberCardShopIdsOfAppUser(Session session, String appUserNo) {
    return session.createSQLQuery("select distinct(m.shop_id) as shopId from app_user_customer auc,member m where auc.app_user_no=:appUserNo and auc.customer_id=m.customer_id ")
      .addScalar("shopId", StandardBasicTypes.LONG)
      .setString("appUserNo", appUserNo);
  }

  public static Query getAppUserMapByUserNo(Session session, Set<String> appUserNoSet) {
    return session.createQuery("from AppUser where appUserNo in (:appUserNoSet) and status!=:status")
      .setParameterList("appUserNoSet", appUserNoSet)
      .setParameter("status", Status.deleted);
  }

  public static Query getMaintainMileageApproachingAppVehicle(Session session, Double[] intervals, int start, int limit, int remindTimesLimit) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a.* from app_vehicle a inner join app_user b on a.app_user_no = b.app_user_no ");
    sb.append("where b.user_type = 'BLUE_TOOTH' and a.current_mileage-a.next_maintain_mileage between :fromMileage and :toMileage ");
    sb.append("and a.status!=:status and a.next_maintain_mileage_push_message_remind_times<=:remindTimesLimit ");
    sb.append("and a.next_maintain_mileage>0 ");
    sb.append("union all ");
    sb.append("select a.* from app_vehicle a inner join app_user b on a.app_user_no = b.app_user_no where (b.user_type = 'GSM' or b.user_type = 'MIRROR') ");
    sb.append("and (mod(( a.current_mileage - a.last_maintain_mileage),a.maintain_period) between :fromMileage and :toMileage ");
    sb.append("or a.maintain_period - mod(( a.current_mileage - a.last_maintain_mileage),a.maintain_period) between :fromMileage and :toMileage) ");
    sb.append("and a.current_mileage - a.maintain_period - a.last_maintain_mileage>=:fromMileage ");
    sb.append("and a.status!=:status and a.maintain_period > 0  and a.next_maintain_mileage_push_message_remind_times<=:remindTimesLimit ");

    return session.createSQLQuery(sb.toString()).addEntity(AppVehicle.class)
      .setParameter("remindTimesLimit", remindTimesLimit)
      .setParameter("status", Status.deleted.toString())
      .setParameter("fromMileage", intervals[0])
      .setParameter("toMileage", intervals[1])
      .setFirstResult(start).setMaxResults(limit);
  }

  public static Query getMaintainTimeApproachingAppVehicle(Session session, Long nextMaintainTimeUp, Long nextMaintainTimeDown, int start, int limit) {
    return session.createQuery("from AppVehicle where nextMaintainTime between :nextMaintainTimeDown and :nextMaintainTimeUp  and status!=:status")
      .setParameter("status", Status.deleted)
      .setParameter("nextMaintainTimeDown", nextMaintainTimeDown)
      .setParameter("nextMaintainTimeUp", nextMaintainTimeUp)
      .setFirstResult(start).setMaxResults(limit);
  }

  public static Query getInsuranceTimeApproachingAppVehicle(Session session, Long nextInsuranceTimeUp, Long nextInsuranceTimeDown, int start, int limit) {
    return session.createQuery("from AppVehicle where nextInsuranceTime between :nextInsuranceTimeDown and :nextInsuranceTimeUp  and status!=:status")
      .setParameter("status", Status.deleted)
      .setParameter("nextInsuranceTimeDown", nextInsuranceTimeDown)
      .setParameter("nextInsuranceTimeUp", nextInsuranceTimeUp)
      .setFirstResult(start).setMaxResults(limit);
  }

  public static Query getExamineTimeApproachingAppVehicle(Session session, Long nextExamineTimeUp, Long nextExamineTimeDown, int start, int limit) {
    return session.createQuery("from AppVehicle where nextExamineTime between :nextExamineTimeDown and :nextExamineTimeUp  and status!=:status")
      .setParameter("status", Status.deleted)
      .setParameter("nextExamineTimeDown", nextExamineTimeDown)
      .setParameter("nextExamineTimeUp", nextExamineTimeUp)
      .setFirstResult(start).setMaxResults(limit);
  }

  public static Query getClientUserLoginInfo(Session session, Long userId) {
    return session.createQuery("from ClientUserLoginInfo where userId =:userId")
      .setParameter("userId", userId);
  }

  public static Query countAppUser(Session session) {
    return session.createQuery("select count(*) from AppUser as cr ");
  }

  public static Query getAppUserByPager(Session session, Pager pager) {
    return session.createQuery(" from AppUser as cr").setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query getServiceCategoryRelationsById(Session session, Long shopId, Long dataId, ServiceCategoryDataType dataType) {
    return session.createQuery("from ServiceCategoryRelation s where s.shopId =:shopId and s.dataId =:dataId and s.dataType =:dataType and s.deleted =:isDeleted")
      .setLong("shopId", shopId).setLong("dataId", dataId).setParameter("dataType", dataType).setParameter("isDeleted", DeletedType.FALSE);
  }

  public static Query deleteServiceCategoryRelation(Session session, Long shopId, Long dataId) {
    StringBuffer sb = new StringBuffer(" delete from ServiceCategoryRelation where shopId =:shopId and dataId =:dataId ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setLong("dataId", dataId);
    return query;
  }

  public static Query getVehicleBrandModelRelationsById(Session session, Long shopId, Long dataId, VehicleBrandModelDataType dataType) {
    return session.createQuery("from VehicleBrandModelRelation s where s.shopId =:shopId and s.dataId =:dataId and s.dataType =:dataType")
      .setLong("shopId", shopId).setLong("dataId", dataId).setParameter("dataType", dataType);
  }

  public static Query deleteVehicleBrandModelRelation(Session session, Long shopId, Long dataId) {
    StringBuffer sb = new StringBuffer(" delete from VehicleBrandModelRelation where shopId =:shopId and dataId =:dataId ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setLong("dataId", dataId);
    return query;
  }

  public static Query updateNextMaintainMileagePushMessageRemindLimit(Session session, Set<Long> vehicleIds) {
    return session.createQuery("update AppVehicle set nextMaintainMileagePushMessageRemindTimes=nextMaintainMileagePushMessageRemindTimes+1 where id in (:ids) ")
      .setParameterList("ids", vehicleIds);
  }

  public static Query getAppVehicleDetail(Session session, String appUserNo, String vehicleVin) {
    return session.createQuery("from AppVehicle where appUserNo =:appUserNo and vehicleVin =:vehicleVin and status =:status")
      .setParameter("appUserNo", appUserNo).setParameter("vehicleVin", vehicleVin).setParameter("status", Status.active);
  }

  public static Query getAppVehicleDetailByVehicleNo(Session session, String appUserNo, String vehicleNo, YesNo isDefault) {
    String hql = "from AppVehicle where appUserNo =:appUserNo and status =:status ";
    if (isDefault != null) {
      hql += " and isDefault=:isDefault";
    }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      hql += " and vehicleNo =:vehicleNo";
    }
    Query q = session.createQuery(hql)
      .setParameter("appUserNo", appUserNo)
      .setParameter("status", Status.active);
    if (isDefault != null) {
      q.setParameter("isDefault", isDefault);
    }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      q.setParameter("vehicleNo", vehicleNo);
    }
    return q;
  }

  public static Query getMatchMaintainMileageCustomerVehicle(Session session, int pageSize, Double[] intervals, Long lastCustomerVehicleId) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT cv.* FROM customer_vehicle cv ")
      .append(" LEFT JOIN vehicle v ON cv.vehicle_id = v.id ")
      .append(" LEFT JOIN app_user_customer auc ON auc.customer_id = cv.customer_id ")
      .append(" LEFT JOIN app_vehicle av ON av.app_user_no = auc.app_user_no AND av.vehicle_no = v.licence_no ")
      .append(" LEFT JOIN customer_service_job csj ON csj.customer_id = auc.customer_id AND csj.vehicle_id = v.id AND csj.remind_type = :remindType ")
      .append(" WHERE v.id IS  NOT NULL ")
      .append(" AND auc.id IS NOT NULL ")
      .append(" AND av.id IS NOT NULL ")
      .append(" AND (csj.id IS NULL OR  cv.maintain_mileage != csj.remind_mileage) ")
      .append(" AND av.current_mileage > 0 ")
      .append(" AND cv.maintain_mileage > 0 ")
      .append(" AND av.current_mileage - cv.maintain_mileage BETWEEN :lowerMileage AND :upperMileage ")
      .append(" AND cv.id >:lastCustomerVehicleId ")
      .append(" ORDER BY cv.id ASC ");
    Query query = session.createSQLQuery(sb.toString()).addEntity(CustomerVehicle.class)
      .setParameter("remindType", UserConstant.MAINTAIN_MILEAGE)
      .setParameter("lowerMileage", intervals[0])
      .setParameter("upperMileage", intervals[1])
      .setParameter("lastCustomerVehicleId", lastCustomerVehicleId)
      .setMaxResults(pageSize);
    return query;
  }

  public static Query getMatchCustomerVehicleByVehicleOBDMileage(Session session, int pageSize, Double[] intervals, Long lastCustomerVehicleId) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT cv.* FROM customer_vehicle cv ")
      .append(" LEFT JOIN vehicle v ON cv.vehicle_id = v.id ")
      .append(" LEFT JOIN customer_service_job csj ON csj.vehicle_id = v.id AND csj.remind_type = :remindType ")
      .append(" WHERE v.id IS  NOT NULL ")
      .append(" AND (csj.id IS NULL OR  cv.maintain_mileage != csj.remind_mileage) ")
      .append(" AND cv.maintain_mileage > 0 ")
      .append(" AND v.obd_mileage - cv.maintain_mileage BETWEEN :lowerMileage AND :upperMileage ")
      .append(" AND cv.id >:lastCustomerVehicleId ")
      .append(" AND (v.status is null or v.status =:vStatus)")
      .append(" ORDER BY cv.id ASC ");
    Query query = session.createSQLQuery(sb.toString()).addEntity(CustomerVehicle.class)
      .setParameter("remindType", UserConstant.MAINTAIN_MILEAGE)
      .setParameter("vStatus", VehicleStatus.ENABLED.name())
      .setParameter("lowerMileage", intervals[0])
      .setParameter("upperMileage", intervals[1])
      .setParameter("lastCustomerVehicleId", lastCustomerVehicleId)
      .setMaxResults(pageSize);
    return query;
  }

  public static Query getOverMaintainMileageCustomerServiceJob(Session session, int pageSize, Double[] intervals, Long startCustomerServiceJobId) {
    Set<String> customerServiceJobStatuses = new HashSet<String>();
    customerServiceJobStatuses.add(UserConstant.Status.REMINDED);
    customerServiceJobStatuses.add(UserConstant.Status.ACTIVITY);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT csj.* FROM customer_vehicle cv ")
      .append(" LEFT JOIN vehicle v ON cv.vehicle_id = v.id ")
      .append(" LEFT JOIN app_user_customer auc ON auc.customer_id = cv.customer_id ")
      .append(" LEFT JOIN app_vehicle av ON av.app_user_no = auc.app_user_no AND av.vehicle_no = v.licence_no ")
      .append(" LEFT JOIN customer_service_job csj ON csj.customer_id = auc.customer_id AND csj.vehicle_id = v.id AND csj.remind_type = :remindType ")
      .append(" WHERE csj.id IS NOT NULL ")
//           .append(" WHERE v.id IS  NOT NULL ")
//           .append(" AND auc.id IS NOT NULL ")
//           .append(" AND av.id IS NOT NULL ")
//           .append(" AND csj.id IS NOT NULL ")
//           .append(" AND (av.current_mileage IS NULL OR cv.maintain_mileage IS NULL OR av.current_mileage )")
//           .append(" AND av.current_mileage > 0 ")
//           .append(" AND cv.maintain_mileage > 0 ")
      .append(" AND !(av.current_mileage - cv.maintain_mileage BETWEEN :lowerMileage AND :upperMileage )")
      .append(" AND (csj.remind_mileage != cv.maintain_mileage OR csj.status in(:customerServiceJobStatuses))")
      .append(" AND csj.id >:startCustomerServiceJobId ")
      .append(" ORDER BY csj.id ASC ");
    Query query = session.createSQLQuery(sb.toString()).addEntity(CustomerServiceJob.class)
      .setParameter("remindType", UserConstant.MAINTAIN_MILEAGE)
      .setParameter("lowerMileage", intervals[0])
      .setParameter("upperMileage", intervals[1])
      .setParameter("startCustomerServiceJobId", startCustomerServiceJobId)
      .setParameterList("customerServiceJobStatuses", customerServiceJobStatuses)
      .setMaxResults(pageSize);
    return query;
  }

  public static Query getOverMaintainMileageWithOBDMileage(Session session, int pageSize, Double[] intervals, Long startCustomerServiceJobId) {
    Set<String> customerServiceJobStatuses = new HashSet<String>();
    customerServiceJobStatuses.add(UserConstant.Status.REMINDED);
    customerServiceJobStatuses.add(UserConstant.Status.ACTIVITY);
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT DISTINCT csj.* FROM customer_vehicle cv ")
      .append(" LEFT JOIN vehicle v ON cv.vehicle_id = v.id ")
      .append(" LEFT JOIN customer_service_job csj ON  csj.vehicle_id = v.id AND csj.remind_type = :remindType ")
      .append(" WHERE csj.id IS NOT NULL ")
      .append(" AND !(v.obd_mileage - cv.maintain_mileage BETWEEN :lowerMileage AND :upperMileage )")
      .append(" AND (csj.remind_mileage != cv.maintain_mileage OR csj.status in(:customerServiceJobStatuses))")
      .append(" AND csj.id >:startCustomerServiceJobId ")
      .append(" ORDER BY csj.id ASC ");
    Query query = session.createSQLQuery(sb.toString()).addEntity(CustomerServiceJob.class)
      .setParameter("remindType", UserConstant.MAINTAIN_MILEAGE)
      .setParameter("lowerMileage", intervals[0])
      .setParameter("upperMileage", intervals[1])
      .setParameter("startCustomerServiceJobId", startCustomerServiceJobId)
      .setParameterList("customerServiceJobStatuses", customerServiceJobStatuses)
      .setMaxResults(pageSize);
    return query;
  }

  public static Query isOBDCustomer(Session session, Long customerId) {
    return session.createSQLQuery("select count(1) from app_user_customer a join obd_user_vehicle v on a.app_user_no = v.app_user_no where v.status = 'BUNDLING' and a.customer_id = :customerId")
      .setParameter("customerId", customerId);
  }


  public static Query isOBDCustomer(Session session, Long[] customerId) {
    return session.createSQLQuery("select a.customer_id, count(1) obd_count from app_user_customer a join obd_user_vehicle v on a.app_user_no = v.app_user_no where v.status = 'BUNDLING' and a.customer_id in " + Arrays.toString(customerId).replace("[", "(").replace("]", ")") + " group by a.customer_id");
  }

  public static Query getObdUserVehicles(Session session, Long... vehicleId) {
    return session.createQuery("from ObdUserVehicle where status = 'BUNDLING' and appVehicleId in (:vehicleId)").setParameterList("vehicleId", vehicleId);
  }

  public static Query getAppVehicleIdBindingOBDMappingByAppUserNo(Session session, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("select ouv.app_vehicle_id as appVehicleId,obd.* from obd_user_vehicle ouv,obd obd ");
    sb.append("where obd.id=ouv.obd_id and ouv.status=:status and ouv.app_user_no=:appUserNo and obd.obd_status in (:obdStatus) ");
    sb.append("group by ouv.app_vehicle_id");

    return session.createSQLQuery(sb.toString())
      .addScalar("appVehicleId", StandardBasicTypes.LONG)
      .addEntity(OBD.class)
      .setParameter("status", ObdUserVehicleStatus.BUNDLING.name())
      .setParameterList("obdStatus", OBDStatus.EnabledStatusStrArr)
      .setParameter("appUserNo", appUserNo);
  }

  public static Query getAppUserCustomersByCustomerIds(Session session, Long[] customerIds) {
    return session.createSQLQuery("select auc.* from app_user_customer auc left join customer cus on cus.id=auc.customer_id where auc.customer_id in (:customerIds) and (cus.status is null or cus.status != :status)")
      .addEntity(AppUserCustomer.class)
      .setParameter("status", CustomerStatus.DISABLED.name())
      .setParameterList("customerIds", customerIds);
  }

  public static Query getCustomerIdInAppUserCustomer(Session session, String[] appUserNos) {
    return session.createSQLQuery("select auc.customer_id as customerId from app_user_customer auc left join customer cus on cus.id=auc.customer_id where auc.app_user_no in (:appUserNos) and (cus.status is null or cus.status != :status)")
      .addScalar("customerId", StandardBasicTypes.LONG)
      .setParameter("status", CustomerStatus.DISABLED.name())
      .setParameterList("appUserNos", appUserNos);
  }

  public static Query getCustomerByAppUserId(Session session, Long appUserId) {
    return session.createSQLQuery("select cus from customer cus" +
      " left join app_user_customer auc on cus.id=auc.customer_id" +
      " left join app_user a on a.app_user_no=auc.app_user_no" +
      " where a.id=:appUserId and (cus.status is null or cus.status != :status)")
      .addEntity(Customer.class)
      .setParameter("appUserId", appUserId)
      .setParameter("status", CustomerStatus.DISABLED.name())
      ;
  }

  public static Query getAppUserCustomerByCustomerId(Session session, Long shopId, Long customerId) {
    return session.createQuery("from AppUserCustomer where customerId =:customerId and shopId =:shopId")
      .setLong("customerId", customerId).setLong("shopId", shopId);
  }

  public static Query getAppUserCustomersByAppUserNoAndAppVehicleId(Session session, String appUserNo, Long appVehicleId, AppUserCustomerMatchType matchType) {
    return session.createQuery("from AppUserCustomer where appUserNo =:appUserNo and appVehicleId =:appVehicleId and matchType =:matchType")
      .setParameter("appUserNo", appUserNo).setLong("appVehicleId", appVehicleId).setParameter("matchType", matchType);
  }

  public static Query getAppUserCustomersByShopVehicleIds(Session session, Set<Long> shopVehicleIds, Set<AppUserCustomerMatchType> matchTypes) {
    return session.createQuery("from AppUserCustomer where shopVehicleId in(:shopVehicleIds) and matchType in(:matchTypes) ")
      .setParameterList("matchTypes", matchTypes).setParameterList("shopVehicleIds", shopVehicleIds);
  }

  public static Query getVehicleByLicenceNo(Session session, Long shopId, Set<String> LicenceNoSet) {
    return session.createQuery("from Vehicle where shopId = :shopId and licenceNo in (:LicenceNoSet) and (status is null or status = :status)").setLong("shopId", shopId).setParameterList("LicenceNoSet", LicenceNoSet).setString("status", VehicleStatus.ENABLED.toString());
  }

  public static Query getSalesManByDepartmentId(Session session, Long shopId, Long departmentId) {
    if (departmentId == null) {
      return session.createQuery("select s from SalesMan s where s.shopId=:shopId ").setLong("shopId", shopId);
    }

    return session.createQuery("select s from SalesMan s where s.shopId=:shopId and s.departmentId=:departmentId and s.status != :status ")
      .setLong("departmentId", departmentId).setLong("shopId", shopId).setParameter("status", SalesManStatus.DELETED);
  }

  public static Query getDepartmentNameByShopIdName(Session session, Long shopId, String name) {
    return session.createQuery(" from Department d where d.shopId=:shopId and d.name=:name ")
      .setString("name", name).setLong("shopId", shopId);
  }

  public static Query getMatchAppUserCustomerDTOByVehicleAndMobile(Session session, Long startAppUserId, int matchSize) {
    StringBuilder sb = new StringBuilder();
    sb.append("select au.app_user_no as appUserNo,c.customer_id as customerId,c.shop_id as shopId,c.id as contactId,au.id as appUserId");
    sb.append(" FROM app_user au LEFT JOIN app_vehicle av ON au.app_user_no = av.app_user_no ");
    sb.append(" LEFT JOIN contact c ON au.mobile = c.mobile ");
    sb.append(" LEFT JOIN vehicle v ON av.vehicle_no = v.licence_no ");
    sb.append(" LEFT JOIN customer_vehicle cv ON v.id = cv.vehicle_id AND c.customer_id = cv.customer_id  ");
    sb.append(" LEFT JOIN app_user_customer auc ON au.app_user_no = auc.app_user_no ");

    sb.append(" AND c.customer_id = auc.customer_id AND c.shop_id = auc.shop_id ");
    sb.append(" LEFT JOIN config.shop cs ON c.shop_id = cs.id AND au.data_kind = cs.shop_kind ");
    sb.append(" WHERE c.id IS NOT NULL AND v.id IS NOT NULL AND cv.id IS NOT NULL AND auc.id IS NULL ");
    sb.append(" AND au.mobile IS NOT NULL  AND au.status =:appUserStatus ");
    sb.append(" AND c.disabled =:contactStatus AND (v.status =:vehicleStatus or v.status is null) AND cs.shop_state =:shopState ");
    sb.append(" AND au.id >=:startAppUserId ");
    sb.append(" ORDER BY au.id asc ");
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("appUserNo", StandardBasicTypes.STRING)
      .addScalar("customerId", StandardBasicTypes.BIG_INTEGER)
      .addScalar("shopId", StandardBasicTypes.BIG_INTEGER)
      .addScalar("contactId", StandardBasicTypes.BIG_INTEGER)
      .addScalar("appUserId", StandardBasicTypes.BIG_INTEGER)
      .setMaxResults(matchSize)
      .setParameter("appUserStatus", Status.active.name())
      .setParameter("contactStatus", ContactConstant.ENABLED)
      .setParameter("vehicleStatus", VehicleStatus.ENABLED.name())
      .setParameter("shopState", ShopState.ACTIVE.name())
      .setParameter("startAppUserId", startAppUserId);
    return query;
  }

  public static Query getEnabledMemberLikeMemberNo(Session session, Long shopId, String memberNo) {
    return session.createQuery("select m from Member m where m.shopId = :shopId and m.memberNo like :memberNo and m.status =:status ")
      .setLong("shopId", shopId).setString("memberNo", "%" + memberNo + "%").setParameter("status", MemberStatus.ENABLED);
  }


  public static Query getDefaultOBDSellerShopId(Session session, String appUserNo) {
    return session.createSQLQuery("select obd.sell_shop_id as shopId from obd_user_vehicle ouv left join app_vehicle av on av.id=ouv.app_vehicle_id left join obd obd on obd.id=ouv.obd_id where ouv.app_user_no = :appUserNo and av.is_default=:isDefault and av.status=:avStatus and ouv.status=:ouvStatus")
      .addScalar("shopId", StandardBasicTypes.LONG)
      .setString("appUserNo", appUserNo)
      .setString("isDefault", YesNo.YES.name())
      .setString("avStatus", Status.active.name())
      .setString("ouvStatus", ObdUserVehicleStatus.BUNDLING.name())
      .setMaxResults(1);
  }

  public static Query getOBDSellerShopIdByVehicleId(Session session, Long vehicleId) {
    return session.createSQLQuery("select obd.sell_shop_id as shopId from obd_user_vehicle ouv left join app_vehicle av on av.id=ouv.app_vehicle_id left join obd obd on obd.id=ouv.obd_id where ouv.app_vehicle_id = :vehicleId and av.is_default=:isDefault and av.status=:avStatus and ouv.status=:ouvStatus")
      .addScalar("shopId", StandardBasicTypes.LONG)
      .setLong("vehicleId", vehicleId)
      .setString("isDefault", YesNo.YES.name())
      .setString("avStatus", Status.active.name())
      .setString("ouvStatus", ObdUserVehicleStatus.BUNDLING.name())
      .setMaxResults(1);
  }

  public static Query getVehiclesByCustomerMobile(Session session, String mobile, String vehicleNo) {
    String sql = "select v.* from vehicle v inner join customer_vehicle cv on cv.vehicle_id=v.id inner join contact con on con.customer_id=cv.customer_id " +
      " where (v.status is null or v.status = :status) and con.mobile =:mobile ";
    if (StringUtil.isNotEmpty(vehicleNo)) {
      sql += " union select v.* from vehicle v where  v.licence_no=:vehicleNo and (v.status is null or v.status = :status) ";
    }
    Query q = session.createSQLQuery(sql)
      .addEntity(Vehicle.class)
      .setString("status", VehicleStatus.ENABLED.toString())
      .setString("mobile", mobile);
    if (StringUtil.isNotEmpty(vehicleNo)) {
      q.setString("vehicleNo", vehicleNo);
    }
    return q;
  }

  public static Query getAppVehicleFaultInfo(Session session, String appUserNo, Long appVehicleId,
                                             Set<String> errorCodes, Set<ErrorCodeTreatStatus> statuses) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppVehicleFaultInfo where appUserNo =:appUserNo and errorCode in(:errorCodes) ");
    if (appVehicleId != null) {
      sb.append(" and appVehicleId =:appVehicleId ");
    }
    if (CollectionUtils.isNotEmpty(statuses)) {
      sb.append(" and status in(:statuses) ");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameterList("errorCodes", errorCodes);
    if (CollectionUtils.isNotEmpty(statuses)) {
      query.setParameterList("statuses", statuses);
    }
    if (appVehicleId != null) {
      query.setParameter("appVehicleId", appVehicleId);
    }
    return query;
  }

  public static Query getAppVehicleFaultInfoByIds(Session session, Set<Long> appVehicleFaultInfoIds, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppVehicleFaultInfo where appUserNo =:appUserNo ");
    sb.append(" and id in(:appVehicleFaultInfoIds) ");
    Query query = session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameterList("appVehicleFaultInfoIds", appVehicleFaultInfoIds);
    return query;
  }

  public static Query searchAppVehicleFaultInfoList(Session session, String appUserNo, Long defaultAppVehicleId, Pager pager,
                                                    ErrorCodeTreatStatus[] status) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppVehicleFaultInfo where appUserNo =:appUserNo ");
    if (defaultAppVehicleId != null) {
      sb.append(" and appVehicleId =:defaultAppVehicleId ");
    }
    if (!ArrayUtils.isEmpty(status)) {
      sb.append(" and status in(:status) ");
    }
    sb.append(" order by lastOperateTime desc,reportTime desc ");
    Query query = session.createQuery(sb.toString()).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize())
      .setParameter("appUserNo", appUserNo);
    if (defaultAppVehicleId != null) {
      query.setParameter("defaultAppVehicleId", defaultAppVehicleId);
    }
    if (!ArrayUtils.isEmpty(status)) {
      query.setParameterList("status", status);
    }
    return query;
  }

  public static Query countAppVehicleFaultInfoList(Session session, String appUserNo, Long defaultAppVehicleId,
                                                   ErrorCodeTreatStatus[] status) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(id) from AppVehicleFaultInfo where appUserNo =:appUserNo ");
    if (defaultAppVehicleId != null) {
      sb.append(" and appVehicleId =:defaultAppVehicleId ");
    }
    if (!ArrayUtils.isEmpty(status)) {
      sb.append(" and status in(:status) ");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo);
    if (defaultAppVehicleId != null) {
      query.setParameter("defaultAppVehicleId", defaultAppVehicleId);
    }
    if (!ArrayUtils.isEmpty(status)) {
      query.setParameterList("status", status);
    }
    return query;
  }

  public static Query getAppUserShopVehicle(Session session, String appUserNo, Long vehicleId, Long shopId, Long obdId, AppUserShopVehicleStatus status) {
    StringBuilder hql = new StringBuilder();
    hql.append("from AppUserShopVehicle where 1=1");
    if (StringUtil.isNotEmpty(appUserNo)) {
      hql.append(" and appUserNo=:appUserNo");
    }
    if (status != null) {
      hql.append(" and status=:status");
    }
    if (vehicleId != null) {
      hql.append(" and appVehicleId =:vehicleId ");
    }
    if (shopId != null) {
      hql.append(" and shopId =:shopId ");
    }
    if (obdId != null) {
      hql.append(" and obdId =:obdId ");
    }
    Query query = session.createQuery(hql.toString());
    if (StringUtil.isNotEmpty(appUserNo)) {
      query.setParameter("appUserNo", appUserNo);
    }
    if (status != null) {
      query.setParameter("status", status);
    }
    if (vehicleId != null) {
      query.setParameter("vehicleId", vehicleId);
    }
    if (shopId != null) {
      query.setParameter("shopId", shopId);
    }
    if (obdId != null) {
      query.setParameter("obdId", obdId);
    }
    return query;
  }


  public static Query getOBDUserVehicle(Session session, Set<Long> obdIds) {
    return session.createQuery("from ObdUserVehicle where obdId in (:obdIds) and status =:status")
      .setParameterList("obdIds", obdIds).setParameter("status", ObdUserVehicleStatus.BUNDLING);
  }

  public static Query getOBDUserVehicleByObdIds(Session session, Set<Long> obdIds) {
    return session.createQuery("from ObdUserVehicle where obdId in (:obdIds) and status =:status")
      .setParameterList("obdIds", obdIds)
      .setParameter("status", ObdUserVehicleStatus.BUNDLING);
  }

  public static Query getOBDUserVehicleByImeis(Session session, Set<String> imeis) {
    StringBuilder sb = new StringBuilder();
    sb.append("select o.imei,ouv from OBD o,ObdUserVehicle ouv where o.imei in (:imeis) and o.obdType =:obdType ");
    sb.append("and o.obdStatus in(:obdStatus) and o.id = ouv.obdId and ouv.status =:status ");
    return session.createQuery(sb.toString())
      .setParameterList("imeis", imeis)
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
      .setParameter("status", ObdUserVehicleStatus.BUNDLING)
      .setParameter("obdType", ObdType.GSM);
  }


  public static Query getBindingShopId(Session session, String userNo, Long... vehicleId) {
    String hql = "from AppUserShopVehicle where appUserNo=:userNo ";
    if (ArrayUtil.isNotEmpty(vehicleId)) {
      hql += "and appVehicleId in (:vehicleId)";
    }
    Query q = session.createQuery(hql).setParameter("userNo", userNo);
    if (ArrayUtil.isNotEmpty(vehicleId)) {
      q.setParameterList("vehicleId", vehicleId);
    }
    return q;
  }


  public static Query getMatchAppUserCustomerDTOByVehicle(Session session, Long startAppUserId, Long assignAppUserId, int matchSize, List<Long> shopVersionIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select au.app_user_no as appUserNo,cv.customer_id as customerId,v.shop_id as shopId,au.id as appUserId,");
    sb.append("av.id as appVehicleId,v.id as shopVehicleId");
    sb.append(" FROM app_user au ");
    sb.append(" INNER JOIN app_vehicle av ON au.app_user_no = av.app_user_no AND au.status =:appUserStatus AND av.status =:appVehicleStatus");
    sb.append(" INNER JOIN vehicle v ON av.vehicle_no = v.licence_no AND (v.status =:vehicleStatus or v.status is null)");
    sb.append(" INNER JOIN customer_vehicle cv ON v.id = cv.vehicle_id ");
    sb.append(" INNER JOIN config.shop cs ON v.shop_id = cs.id AND au.data_kind = cs.shop_kind AND cs.shop_state =:shopState ");
    if (CollectionUtils.isNotEmpty(shopVersionIds)) {
      sb.append(" AND cs.shop_version_id in(:shopVersionIds)");
    }
    sb.append(" LEFT JOIN app_user_customer auc ON au.app_user_no = auc.app_user_no AND av.id = auc.app_vehicle_id");
    sb.append(" AND av.id = auc.app_vehicle_id AND v.id = auc.shop_vehicle_id");
    sb.append(" AND cv.customer_id = auc.customer_id AND v.shop_id = auc.shop_id ");

    sb.append(" WHERE auc.id IS NULL and au.user_type = 'BLUE_TOOTH'  ");
    sb.append(" AND au.id >=:startAppUserId ");
    if (assignAppUserId != null) {
      sb.append(" AND au.id =:assignAppUserId ");
    }
    sb.append(" ORDER BY au.id asc ");
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("appUserNo", StandardBasicTypes.STRING)
      .addScalar("customerId", StandardBasicTypes.LONG)
      .addScalar("shopId", StandardBasicTypes.LONG)
      .addScalar("appUserId", StandardBasicTypes.LONG)
      .addScalar("appVehicleId", StandardBasicTypes.LONG)
      .addScalar("shopVehicleId", StandardBasicTypes.LONG)
      .setMaxResults(matchSize)
      .setParameter("appUserStatus", Status.active.name())
      .setParameter("appVehicleStatus", Status.active.name())
      .setParameter("vehicleStatus", VehicleStatus.ENABLED.name())
      .setParameter("shopState", ShopState.ACTIVE.name())
      .setParameter("startAppUserId", startAppUserId);
    if (assignAppUserId != null) {
      query.setParameter("assignAppUserId", assignAppUserId);
    }
    if (CollectionUtils.isNotEmpty(shopVersionIds)) {
      query.setParameterList("shopVersionIds", shopVersionIds);
    }
    return query;
  }

  public static Query getMatchAppUserCustomerDTOByMobile(Session session, Long startAppUserId, Long assignAppUserId, int matchSize, List<Long> shopVersionIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select au.app_user_no as appUserNo,c.customer_id as customerId,c.shop_id as shopId,c.id AS contactId,au.id as appUserId");
    sb.append(" FROM app_user au ");
    sb.append(" INNER JOIN contact c ON au.mobile = c.mobile AND c.mobile IS NOT NULL AND c.mobile != ''");
    sb.append(" AND c.disabled =:contactStatus AND au.status =:appUserStatus  AND c.customer_id IS NOT NULL ");
    sb.append(" INNER JOIN config.shop cs ON c.shop_id = cs.id AND au.data_kind = cs.shop_kind AND cs.shop_state =:shopState ");
    if (CollectionUtils.isNotEmpty(shopVersionIds)) {
      sb.append(" AND cs.shop_version_id in(:shopVersionIds)");
    }
    sb.append(" LEFT JOIN app_user_customer auc ON au.app_user_no = auc.app_user_no AND auc.match_type = :matchType");
    sb.append(" AND c.customer_id = auc.customer_id AND c.shop_id = auc.shop_id ");
    sb.append(" WHERE auc.id IS NULL and au.user_type = 'BLUE_TOOTH' ");
    sb.append(" AND au.id >=:startAppUserId ");
    if (assignAppUserId != null) {
      sb.append(" AND au.id =:assignAppUserId ");
    }
    sb.append(" ORDER BY au.id asc ");
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("appUserNo", StandardBasicTypes.STRING)
      .addScalar("customerId", StandardBasicTypes.LONG)
      .addScalar("shopId", StandardBasicTypes.LONG)
      .addScalar("contactId", StandardBasicTypes.LONG)
      .addScalar("appUserId", StandardBasicTypes.LONG)
      .setMaxResults(matchSize)
      .setParameter("appUserStatus", Status.active.name())
      .setParameter("contactStatus", ContactConstant.ENABLED)
      .setParameter("shopState", ShopState.ACTIVE.name())
      .setParameter("matchType", AppUserCustomerMatchType.CONTACT_MOBILE_MATCH.name())
      .setParameter("startAppUserId", startAppUserId);
    if (CollectionUtils.isNotEmpty(shopVersionIds)) {
      query.setParameterList("shopVersionIds", shopVersionIds);
    }
    if (assignAppUserId != null) {
      query.setParameter("assignAppUserId", assignAppUserId);
    }
    return query;
  }

  public static Query getMatchAppUserCustomerDTOByVehicleMobile(Session session, Long startAppUserId, Long assignAppUserId, int matchSize, List<Long> shopVersionIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select au.app_user_no as appUserNo,cv.customer_id as customerId,v.shop_id as shopId,au.id as appUserId,");
    sb.append("v.id as shopVehicleId");
    sb.append(" FROM app_user au ");
    sb.append(" INNER JOIN vehicle v ON au.status =:appUserStatus AND au.mobile = v.mobile AND v.mobile IS NOT NULL AND v.mobile != '' AND (v.status =:vehicleStatus or v.status is null)");
    sb.append(" INNER JOIN customer_vehicle cv ON v.id = cv.vehicle_id ");
    sb.append(" INNER JOIN config.shop cs ON v.shop_id = cs.id AND au.data_kind = cs.shop_kind AND cs.shop_state =:shopState ");
    if (CollectionUtils.isNotEmpty(shopVersionIds)) {
      sb.append(" AND cs.shop_version_id in(:shopVersionIds)");
    }
    sb.append(" LEFT JOIN app_user_customer auc ON au.app_user_no = auc.app_user_no ");
    sb.append(" AND v.id = auc.shop_vehicle_id");
    sb.append(" AND cv.customer_id = auc.customer_id AND v.shop_id = auc.shop_id ");
    sb.append(" WHERE auc.id IS NULL  and au.user_type = 'BLUE_TOOTH' ");
    sb.append(" AND au.id >=:startAppUserId ");
    if (assignAppUserId != null) {
      sb.append(" AND au.id =:assignAppUserId ");
    }
    sb.append(" ORDER BY au.id asc ");
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("appUserNo", StandardBasicTypes.STRING)
      .addScalar("customerId", StandardBasicTypes.LONG)
      .addScalar("shopId", StandardBasicTypes.LONG)
      .addScalar("appUserId", StandardBasicTypes.LONG)
      .addScalar("shopVehicleId", StandardBasicTypes.LONG)
      .setMaxResults(matchSize)
      .setParameter("appUserStatus", Status.active.name())
      .setParameter("vehicleStatus", VehicleStatus.ENABLED.name())
      .setParameter("shopState", ShopState.ACTIVE.name())
      .setParameter("startAppUserId", startAppUserId);
    if (assignAppUserId != null) {
      query.setParameter("assignAppUserId", assignAppUserId);
    }
    if (CollectionUtils.isNotEmpty(shopVersionIds)) {
      query.setParameterList("shopVersionIds", shopVersionIds);
    }
    return query;
  }


  public static Query filterMatchVehicleCustomerIds(Session session, Set<String> vehicleNos, Set<Long> customerIds, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT DISTINCT(c.id) AS customerId ");
    sb.append(" FROM customer c INNER JOIN customer_vehicle cv ON c.id = cv.customer_id");
    sb.append(" INNER JOIN vehicle v ON v.id = cv.vehicle_id");
    sb.append(" WHERE c.id IN(:customerIds) AND c.shop_id =:shopId AND v.licence_no IN (:vehicleNos)");
    sb.append(" AND (c.status IS NULL OR c.status =:cStatus ) AND (cv.status IS NULL OR cv.status =:cvStatus)");
    sb.append(" AND (v.status IS NULL OR v.status =:vStatus)");
    return session.createSQLQuery(sb.toString())
      .addScalar("customerId", StandardBasicTypes.LONG)
      .setParameter("shopId", shopId)
      .setParameterList("customerIds", customerIds)
      .setParameterList("vehicleNos", vehicleNos)
      .setParameter("cStatus", CustomerStatus.ENABLED.name())
      .setParameter("cvStatus", VehicleStatus.ENABLED.name())
      .setParameter("vStatus", VehicleStatus.ENABLED.name());

  }

  public static Query isAppUserByCustomerId(Session session, List<Long> customerIdList) {
    return session.createQuery("select a.customerId, count(id) from AppUserCustomer a where a.customerId  in(:customerIdList) group by customerId").setParameterList("customerIdList", customerIdList);
  }


  public static Query getCustomerOrSupplierId(Session session, String[] customerOrSupplierIds) {
    return session.createSQLQuery("select supplier_id from customer where identity='isSupplier' and id in (:ids)  union select customer_id from supplier where identity='isCustomer'  and id in (:ids)").setParameterList("ids", customerOrSupplierIds);
  }


  public static Query getVehicleAvailableMemberServicesByLicenceNo(Session session, Long shopId, String licenceNo) throws ParseException {
    return session.createQuery("select ms from MemberService ms,Member m where ms.vehicles like :licenceNo and ms.memberId =m.id and m.shopId=:shopId and m.deadline>:date and (ms.deadline > :date or ms.deadline = -1) and ms.status =:status and m.status =:status")
      .setString("licenceNo", "%" + licenceNo + "%").setLong("shopId", shopId).setLong("date", DateUtil.getTheDayTime()).setParameter("status", MemberStatus.ENABLED);
  }

  public static Query getMembersByMemberIds(Session session, Long shopId, Set<Long> memberIdSet) {
    return session.createQuery("from Member m where m.shopId = :shopId and m.id in(:memberId)")
      .setLong("shopId", shopId).setParameterList("memberId", memberIdSet);
  }

  public static Query getVehicleOBDMileageByStartVehicleId(Session session, Long startVehicleId, int pageSize) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT v.id vehicleId ,av.current_mileage_last_update_time updateTime,av.current_mileage currentMileage,v.shop_id shopId  ");
    sb.append(" FROM vehicle v JOIN app_vehicle av ON v.licence_no = av.vehicle_no ");
    sb.append(" AND av.current_mileage_last_update_time > v.mileage_last_update_time");
    sb.append(" AND v.id >= :startVehicleId");
    sb.append(" AND av.current_mileage > 0");
    sb.append(" AND (v.status is null or v.status =:status)");
    sb.append(" ORDER BY v.id ASC");
    Query sqlQuery = session.createSQLQuery(sb.toString())
      .addScalar("vehicleId", StandardBasicTypes.LONG)
      .addScalar("updateTime", StandardBasicTypes.LONG)
      .addScalar("currentMileage", StandardBasicTypes.DOUBLE)
      .addScalar("shopId", StandardBasicTypes.LONG)
      .setParameter("startVehicleId", startVehicleId)
      .setParameter("status", VehicleStatus.ENABLED.name())
      .setMaxResults(pageSize);
    return sqlQuery;
  }


  public static Query getDriveLogByAppId(Session session, String appUserNo, String appDriveLogId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and appDriveLogId =:appDriveLogId");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("appDriveLogId", appDriveLogId);
  }

  public static Query getDriveLogByAppUserNoAndId(Session session, String appUserNo, Long driveLogId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and id =:driveLogId");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("driveLogId", driveLogId);
  }


  public static Query getDriveLogById(Session session, Long driveLogId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where id =:driveLogId and status!=:status");
    return session.createQuery(sb.toString())
      .setParameter("driveLogId", driveLogId)
      .setParameter("status", DriveLogStatus.DISABLED)
      ;
  }

  public static Query getDriveLogPlaceNoteByLogId(Session session, String appUserNo, Long driveLogId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLogPlaceNote where appUserNo =:appUserNo and driveLogId =:driveLogId");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("driveLogId", driveLogId);
  }

  public static Query getDriveLogContents(Session session, String appUserNo, Long startTime, Long endTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("select id,app_user_no,app_drive_log_id,last_update_time,status from drive_log");
    sb.append(" where app_user_no =:appUserNo and status=:status ");
    if (startTime != null) {
      sb.append(" and last_update_time >=:startTime ");
    }
    if (endTime != null) {
      sb.append(" and last_update_time <=:endTime ");
    }
    Query sqlQuery = session.createSQLQuery(sb.toString())
      .addScalar("id", StandardBasicTypes.LONG)
      .addScalar("app_user_no", StandardBasicTypes.STRING)
      .addScalar("app_drive_log_id", StandardBasicTypes.STRING)
      .addScalar("last_update_time", StandardBasicTypes.LONG)
      .addScalar("status", StandardBasicTypes.STRING)
      .setParameter("appUserNo", appUserNo)
      .setParameter("status", DriveLogStatus.ENABLED.name());
    if (startTime != null) {
      sqlQuery.setParameter("startTime", startTime);
    }
    if (endTime != null) {
      sqlQuery.setParameter("endTime", endTime);
    }
    return sqlQuery;
  }

  public static Query getDriveLogByStartTime(Session session, String appUserNo, Long startTime, Long endTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and status in(:statuses)");
    if (startTime != null) {
      sb.append(" and startTime >=:startTime ");
    }
    if (endTime != null) {
      sb.append(" and startTime <=:endTime ");
    }
    sb.append(" order by startTime desc");
    Set<DriveLogStatus> statuses = new HashSet<DriveLogStatus>();
    statuses.add(DriveLogStatus.ENABLED);
    statuses.add(DriveLogStatus.DRIVING);
    Query query = session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameterList("statuses", statuses);
    if (startTime != null) {
      query.setParameter("startTime", startTime);
    }
    if (endTime != null) {
      query.setParameter("endTime", endTime);
    }
    return query;
  }

  public static Query getDriveLogByStartTime_wx(Session session, String appUserNo, Long startTime, Long endTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and status in(:statuses)");
//    if (startTime != null) {
//      sb.append(" and startTime >=:startTime ");
//    }
//    if (endTime != null) {
//      sb.append(" and startTime <=:endTime ");
//    }
    sb.append(" order by startTime desc");
    Set<DriveLogStatus> statuses = new HashSet<DriveLogStatus>();
    statuses.add(DriveLogStatus.ENABLED);
    statuses.add(DriveLogStatus.DRIVING);
    Query query = session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameterList("statuses", statuses).setMaxResults(10);
//    if (startTime != null) {
//      query.setParameter("startTime", startTime);
//    }
//    if (endTime != null) {
//      query.setParameter("endTime", endTime);
//    }
    return query;
  }

  public static Query getLastDriveLog(Session session, String appUserNo, int limit) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and status =:status order by startTime desc");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("status", DriveLogStatus.ENABLED)
      .setFirstResult(0).setMaxResults(limit)
      ;
  }


  public static Query getAppUserConfigByAppUserNo(Session session, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppUserConfig where appUserNo =:appUserNo ");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo);
  }

  public static Query getAppUserConfigByName(Session session, String appUserNo, String name) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppUserConfig where appUserNo =:appUserNo and name =:name");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("name", name);
  }

  public static Query getDriveLogByIds(Session session, String appUserNo, Set<Long> driveLogIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and id in(:driveLogIds)");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameterList("driveLogIds", driveLogIds);
  }

  public static Query getDriveLogPlaceNoteByLogIds(Session session, String appUserNo, Set<Long> driveLogIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLogPlaceNote where 1=1 and driveLogId in(:driveLogIds)");
    if (StringUtil.isNotEmpty(appUserNo)) {
      sb.append(" and appUserNo =:appUserNo ");
    }

    Query query = session.createQuery(sb.toString())
      .setParameterList("driveLogIds", driveLogIds);
    if (StringUtil.isNotEmpty(appUserNo)) {
      query.setParameter("appUserNo", appUserNo);
    }
    return query;
  }

  public static Query checkIsAppVehicle(Session session, Long... vehicleIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select auc.shop_vehicle_id as vehicleId,count(auc.id) appUserCount from app_user_customer auc ");
    sb.append(" where auc.shop_vehicle_id in(:vehicleIds) GROUP BY auc.shop_vehicle_id");
    return session.createSQLQuery(sb.toString())
      .addScalar("vehicleId", StandardBasicTypes.LONG)
      .addScalar("appUserCount", StandardBasicTypes.LONG)
      .setParameterList("vehicleIds", vehicleIds);
  }

  public static Query getUserBySalesManId(Session session, Long shopId, Long salesManId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from User where shopId=:shopId and salesManId=:salesManId");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("salesManId", salesManId);
  }

  public static Query getVehicleByGsmObdImei(Session session, String gsmObdImei) {
    return session.createQuery("from Vehicle where gsmObdImei =:gsmObdImei and (status is null or status =:status)")
      .setString("gsmObdImei", gsmObdImei)
      .setParameter("status", VehicleStatus.ENABLED)
      ;
  }

  public static Query getVehicleByGsmObdImeis(Session session, Set<String> imeis) {
    return session.createQuery("from Vehicle where gsmObdImei in(:imeis) and (status is null or status =:status)")
      .setParameterList("imeis", imeis)
      .setParameter("status", VehicleStatus.ENABLED)
      ;
  }

  public static Query getAllOBD(Session session) {
    return session.createQuery("from OBD where sellShopId is not null ");
  }

  public static Query getObdById(Session session, Long... ids) {
    return session.createQuery("from OBD where id in (:ids)")
      .setParameterList("ids", ids);
  }

  public static Query getObdBySn(Session session, String sn) {
    return session.createQuery("from OBD where sn =:sn").setString("sn", sn);
  }

  public static Query getObdByImei(Session session, String imei) {
    return session.createQuery("from OBD where imei =:imei and obdStatus in(:obdStatus)")
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
      .setString("imei", imei);
  }


  public static Query getObdByObdImei(Session session, String imei, ObdType obdType) {
    StringBuilder sb = new StringBuilder("from OBD where imei =:imei and obdStatus in(:obdStatus)");
    if (obdType != null) {
      sb.append(" and obdType =:obdType");
    }
    Query query = session.createQuery(sb.toString())
      .setString("imei", imei)
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
      ;
    if (obdType != null) {
      query.setParameter("obdType", obdType);
    }
    return query;
  }

  public static Query getObdByImeisObdType(Session session, Set<String> imeis, ObdType obdType) {
    StringBuilder sb=new StringBuilder();
    sb.append("from OBD where imei in(:imeis) and obdStatus in(:obdStatus)");
    if(obdType!=null){
         sb.append(" and obdType =:obdType");
    }
    Query query= session.createQuery(sb.toString())
      .setParameterList("imeis", imeis)
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
     ;
     if(obdType!=null){
         query .setParameter("obdType", obdType);
    }
    return query;
  }

  public static Query getAppUserByUserType(Session session, String mobile) {
    return session.createQuery(" from AppUser where  mobile=:mobile and status !=:status ")
      .setString("mobile", mobile)
      .setParameter("status", Status.deleted);
  }

  public static Query getAppUserByUserType(Session session, AppUserType appUserType, int start, int limit) {
    return session.createQuery(" from AppUser where status !=:status and appUserType =:appUserType ")
      .setParameter("status", Status.deleted).setParameter("appUserType", appUserType)
      .setFirstResult(start)
      .setMaxResults(limit)
      ;
  }

  public static Query getAppUserByImei(Session session, String imei, AppUserType userType) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from app_user u " +
      "join obd_user_vehicle v on u.app_user_no=v.app_user_no " +
      "join obd o on v.obd_id=o.id " +
      "where u.status =:uStatus and v.status =:vStatus and o.imei =:imei and o.obd_status in (:obdStatus)");

    if (userType != null) {
      sb.append(" and u.user_type=:userType");
    }
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(AppUser.class)
      .setString("uStatus", Status.active.name())
      .setString("vStatus", ObdUserVehicleStatus.BUNDLING.name())
      .setString("imei", imei)
      .setParameterList("obdStatus", OBDStatus.EnabledStatusStrArr);

    if (userType != null) {
      query.setString("userType", userType.name());
    }
    return query;
  }


  public static Query getAppUserMobileByContactMobile(Session session, Long shopId, String mobile) {
    return session.createSQLQuery("select au.mobile from app_user au left join app_user_customer auc on auc.app_user_no = au.app_user_no" +
      " left join contact c on c.customer_id = auc.customer_id where c.mobile=:mobile and c.shop_id=:shopId and au.status=:appUserStatus and c.disabled =:contactStatus")
      .setString("mobile", mobile).setLong("shopId", shopId).setString("appUserStatus", Status.active.name()).setInteger("contactStatus", ContactConstant.ENABLED);
  }

  public static Query getDriveLogByStatusOrderByEndTimeAsc(Session session, String appUserNo, DriveLogStatus status, int limit) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and status =:status order by startTime asc");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("status", status)
      .setMaxResults(limit);
  }

  public static Query getDriveLogsByStatusAndStatStatus(Session session, String appUserNo, Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:appUserNo and status in(:driveLogStatus) and driveStatStatus=:statStatus order by startTime asc");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameterList("driveLogStatus", driveLogStatus)
      .setParameter("statStatus", statStatus);
  }

  public static Query getDriveLogsByStatusAndStatStatus(Session session, Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus, int limit) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where status in(:driveLogStatus) and driveStatStatus=:statStatus order by startTime asc");
    return session.createQuery(sb.toString())
      .setParameterList("driveLogStatus", driveLogStatus)
      .setParameter("statStatus", statStatus)
      .setMaxResults(limit);
  }

  public static Query getDriveLogByAppUserNosAndStatusOrderByEndTimeAsc(Session session, Set<String> appUserNos, DriveLogStatus status) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo in(:appUserNos) and status =:status order by startTime asc");
    return session.createQuery(sb.toString())
      .setParameterList("appUserNos", appUserNos)
      .setParameter("status", status);
  }

  public static Query getVehicleByGsmObdImei(Session session, Long vehicleId, String gsmObdImei) {
    String hql = "select v.* from vehicle as v " +
      "left join customer_vehicle cv on cv.vehicle_id=v.id " +
      "where v.gsm_obd_imei =:gsmObdImei and (v.status is null or v.status = :status)";
    if (vehicleId != null) {
      hql += " and v.id<>:vehicleId";
    }
    Query q = session.createSQLQuery(hql)
      .addEntity(Vehicle.class)
      .setString("gsmObdImei", gsmObdImei).setString("status", VehicleStatus.ENABLED.toString());
    if (vehicleId != null) {
      q.setLong("vehicleId", vehicleId);
    }
    return q;
  }

  public static Query getAppUserByDeviceToken(Session session, String deviceToken) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppUser where  deviceToken =:deviceToken");
    return session.createQuery(sb.toString())
      .setParameter("deviceToken", deviceToken)
      .setMaxResults(20);
  }

  public static Query getAppUserByUMDeviceToken(Session session, String umDeviceToken) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppUser where  umDeviceToken =:umDeviceToken");
    return session.createQuery(sb.toString())
      .setParameter("umDeviceToken", umDeviceToken)
      .setMaxResults(20);
  }

  public static Query getUserByDeviceToken(Session session, String deviceToken) {
    StringBuilder sb = new StringBuilder();
    sb.append("from User where  deviceToken =:deviceToken");
    return session.createQuery(sb.toString())
      .setParameter("deviceToken", deviceToken)
      .setMaxResults(20);
  }

  public static Query getUserByUMDeviceToken(Session session, String umDeviceToken) {
    StringBuilder sb = new StringBuilder();
    sb.append("from User where  umDeviceToken =:umDeviceToken");
    return session.createQuery(sb.toString())
      .setParameter("umDeviceToken", umDeviceToken)
      .setMaxResults(20);
  }


  public static Query getAppUserNoByVehicleId(Session session, Long... vehicleIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select auc.appUserNo from AppUserCustomer auc");
    sb.append(" where auc.shopVehicleId in(:vehicleIds) GROUP BY auc.shopVehicleId");
    return session.createQuery(sb.toString()).setParameterList("vehicleIds", vehicleIds);
  }


  public static Query getVehicleImeiByShopId(Session session, Long shopId) {
    return session.createQuery("select distinct gsmObdImei from Vehicle as v where v.shopId = :shopId and (v.status is null or v.status = :status)")
      .setLong("shopId", shopId).setString("status", VehicleStatus.ENABLED.toString());
  }

  public static Query getDriveStatsByStatDate(Session session, String appUserNo, Long startTime, Long endTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveStat where appUserNo =:appUserNo and statDate>=:startTime and statDate<=:endTime order by statDate asc");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("startTime", startTime)
      .setParameter("endTime", endTime);
  }

  public static Query getDriveStatByYearAndMonth(Session session, String appUserNo, int year, int month) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveStat where appUserNo =:appUserNo and statYear=:year and statMonth=:month ");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("year", year)
      .setParameter("month", month);
  }

  public static Query getGsmAppVehicle(Session session, Pager pager) {
    AppUserType[] userTypes = {AppUserType.GSM, AppUserType.MIRROR};
    return session.createQuery(" select a from AppVehicle a ,AppUser b  where  a.appUserNo = b.appUserNo  and a.status =:status and a.juheCityCode is not null  and b.appUserType in(:userTypes) ")
      .setParameter("status", Status.active).setParameterList("userTypes", userTypes).setMaxResults(pager.getPageSize()).setFirstResult(pager.getRowStart());
  }

  public static Query countGsmAppVehicle(Session session) {
    return session.createQuery(" select count(a) from AppVehicle a ,AppUser b  where  a.appUserNo = b.appUserNo  and a.status =:status and a.juheCityCode is not null   and b.appUserType =:userType ")
      .setParameter("status", Status.active).setParameter("userType", AppUserType.GSM);
  }

  public static Query getDriveLogDTOsByImeiTime(Session session, String imei, Long startTime, Long endTime, Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DriveLog where appUserNo =:imei and status in(:statuses) ");
    if (startTime != null) {
      sb.append(" and startTime >=:startTime ");
    }
    if (endTime != null) {
      sb.append(" and startTime <=:endTime ");
    }
    sb.append(" order by endTime desc");
    Set<DriveLogStatus> statuses = new HashSet<DriveLogStatus>();
    statuses.add(DriveLogStatus.ENABLED);
    statuses.add(DriveLogStatus.DRIVING);
    Query query = session.createQuery(sb.toString())
      .setParameter("imei", imei)
      .setParameterList("statuses", statuses).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    if (startTime != null) {
      query.setParameter("startTime", startTime);
    }
    if (endTime != null) {
      query.setParameter("endTime", endTime);
    }
    return query;
  }

  public static Query countDriveLogDTOsByImeiTime(Session session, String imei, Long startTime, Long endTime) {
    StringBuilder sb = new StringBuilder();
    sb.append(" select count(*) from DriveLog where appUserNo =:imei and status in(:statuses) ");
    if (startTime != null) {
      sb.append(" and startTime >=:startTime ");
    }
    if (endTime != null) {
      sb.append(" and startTime <=:endTime ");
    }
    sb.append(" order by endTime desc");
    Set<DriveLogStatus> statuses = new HashSet<DriveLogStatus>();
    statuses.add(DriveLogStatus.ENABLED);
    statuses.add(DriveLogStatus.DRIVING);
    Query query = session.createQuery(sb.toString())
      .setParameter("imei", imei)
      .setParameterList("statuses", statuses);
    if (startTime != null) {
      query.setParameter("startTime", startTime);
    }
    if (endTime != null) {
      query.setParameter("endTime", endTime);
    }
    return query;
  }


  public static Query getVehicleByCondition(Session session, VehicleSearchConditionDTO vehicleSearchConditionDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append(" from Vehicle where (status is null or status =:status) and shopId=:shopId and obdId is not null ");
    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getLicenceNo())) {
      sb.append(" and licenceNo like:licenceNo ");
    }

    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getEngineNo())) {
      sb.append(" and engineNo like:engineNo ");
    }

    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getChassisNumber())) {
      sb.append(" and chassisNumber like:chassisNumber ");
    }

    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getGsmObdImei())) {
      sb.append(" and gsmObdImei like:gsmObdImei ");
    }
    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getGsmObdImeiMoblie())) {
      sb.append(" and gsmObdImeiMoblie like:gsmObdImeiMoblie ");
    }

    Query query = session.createQuery(sb.toString()).setParameter("status", VehicleStatus.ENABLED).setLong("shopId", vehicleSearchConditionDTO.getShopId());
    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getLicenceNo())) {
      query.setString("licenceNo", "%" + vehicleSearchConditionDTO.getLicenceNo() + "%");
    }

    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getEngineNo())) {
      query.setString("engineNo", "%" + vehicleSearchConditionDTO.getEngineNo() + "%");
    }

    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getChassisNumber())) {
      query.setString("chassisNumber", "%" + vehicleSearchConditionDTO.getChassisNumber() + "%");
    }

    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getGsmObdImei())) {
      query.setString("gsmObdImei", "%" + vehicleSearchConditionDTO.getGsmObdImei() + "%");
    }
    if (StringUtil.isNotEmpty(vehicleSearchConditionDTO.getGsmObdImeiMoblie())) {
      query.setString("gsmObdImeiMoblie", "%" + vehicleSearchConditionDTO.getGsmObdImeiMoblie() + "%");
    }
    query.setMaxResults(2);
    return query;
  }


  public static Query searchObdSimBindDTOByAdmin(Session session, ObdSimSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    boolean isHaveObdSingle = true;
    boolean isHaveSimSingle = true;
    boolean isHaveObdSimCombine = true;
    //新增后视镜单品，成品
    boolean isHaveMirrorSingle = true;
    boolean isHaveMirrorObdSimCombine = true;
    boolean isStartUnion = false;
    boolean isHaveImeiField = false;
    boolean isHaveObdVersionField = false;
    boolean isHaveOwnerNameField = false;
    boolean isHaveObdStatusesField = false;
    boolean isHaveMobileField = false;
    boolean isHaveStartUseDate = false;
    boolean isHaveEndUseDate = false;
    boolean isClient = !condition.isAdmin();

    if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimTypes())) {
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.SINGLE_GSM_OBD)) {
        isHaveObdSingle = false;
      }
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.SINGLE_SIM)) {
        isHaveSimSingle = false;
      }
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.COMBINE_GSM_OBD_SIM)) {
        isHaveObdSimCombine = false;
      }
      //新增后视镜单品，成品
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.SINGLE_MIRROR_OBD)) {
        isHaveMirrorSingle = false;
      }
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.COMBINE_MIRROR_OBD_SIM)) {
        isHaveMirrorObdSimCombine = false;
      }
    }
    //只有IMIE号查询时，过滤掉SIM单品
    if (StringUtil.isNotEmpty(condition.getImei())) {
      isHaveSimSingle = false;
    }
    //只用手机号查询时，过滤掉OBD单品和后视镜单品
    if (StringUtil.isNotEmpty(condition.getMobile())) {
      isHaveObdSingle = false;
      isHaveMirrorSingle = false;
    }
    //既有IMIE号又有手机号时，过滤掉OBD单品，后视镜单品和SIM单品
    if (StringUtil.isNotEmpty(condition.getImei()) && StringUtil.isNotEmpty(condition.getMobile())) {
      isHaveObdSingle = false;
      isHaveMirrorSingle = false;
      isHaveSimSingle = false;
    }
    //勾选OBD单品和后视镜单品时
    if (isHaveObdSingle == true && isHaveMirrorSingle == true && isHaveSimSingle == false && isHaveObdSimCombine == false && isHaveMirrorObdSimCombine == false) {
      sb.append("(");
      sb.append("SELECT o.last_update ,o.obd_sim_type ,o.obd_status ,o.imei ,o.obd_version ,o.spec ,o.color ,o.pack ,");
      sb.append("o.open_crash ,o.open_shake ,'' as sim_no, '' as mobile,'' as use_date,'' as use_period,o.owner_name, ");
      sb.append("o.id ,'' ");
      sb.append("FROM obd o WHERE (o.obd_sim_type = 'SINGLE_GSM_OBD' or o.obd_sim_type = 'SINGLE_MIRROR_OBD' or o.obd_sim_type = 'SINGLE_GSM_POBD' or o.obd_sim_type = 'SINGLE_GSM_SOBD')   ");
      if (isClient) {
        sb.append("AND o.storage_id in(:clientIds) ");
      }
      if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
        sb.append("AND o.imei like :imei ");
        isHaveImeiField = true;
      }
      if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
        sb.append("AND o.obd_version like :obdVersion ");
        isHaveObdVersionField = true;
      }
      if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
        sb.append("AND o.owner_name LIKE :ownerName ");
        isHaveOwnerNameField = true;
      }
      if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
        sb.append("AND o.obd_status IN (:obdStatuses) ");
        isHaveObdStatusesField = true;
      }

      sb.append(") ");
    } else {
      if (isHaveObdSingle) {
        sb.append("(");
        sb.append("SELECT o.last_update ,o.obd_sim_type ,o.obd_status ,o.imei ,o.obd_version ,o.spec ,o.color ,o.pack ,");
        sb.append("o.open_crash ,o.open_shake ,'' as sim_no, '' as mobile,'' as use_date,'' as use_period,o.owner_name, ");
        sb.append("o.id ,'' ");
        sb.append("FROM obd o WHERE (o.obd_sim_type = 'SINGLE_GSM_OBD'or o.obd_sim_type = 'SINGLE_GSM_SOBD') ");
        if (isClient) {
          sb.append("AND o.storage_id in(:clientIds) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }

        sb.append(") ");
        isStartUnion = true;
      }
      if (isHaveObdSimCombine) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT o.last_update ,o.obd_sim_type ,o.obd_status ,o.imei ,o.obd_version ,o.spec ,o.color ,o.pack , ");
        sb.append("o.open_crash ,o.open_shake , s.sim_no , s.mobile ,s.use_date ,s.use_period ,o.owner_name ,");
        sb.append("o.id ,s.id   ");
        sb.append("FROM obd o LEFT JOIN obd_sim_bind osb on o.id = osb.obd_id ");
        sb.append("LEFT JOIN obd_sim s ON osb.sim_id = s.id ");
        sb.append("WHERE (o.obd_sim_type = 'COMBINE_GSM_OBD_SIM' or o.obd_sim_type = 'COMBINE_GSM_POBD_SIM' or o.obd_sim_type = 'COMBINE_GSM_OBD_SSIM') and osb.status = 'ENABLED' ");
        if (isClient) {
          sb.append("AND (o.storage_id in(:clientIds) OR o.agent_id in(:clientIds) OR o.seller_id in(:clientIds)) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getMobile())) {
          sb.append("AND s.mobile like :mobile ");
          isHaveMobileField = true;
        }
        if (condition != null && condition.getStartUserDate() != null) {
          sb.append("AND s.use_date >= :startUseDate ");
          isHaveStartUseDate = true;
        }
        if (condition != null && condition.getEndUserDate() != null) {
          sb.append("AND s.use_date <= :endUseDate ");
          isHaveEndUseDate = true;
        }
        sb.append(") ");
      }
      if (isHaveSimSingle) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT s.last_update ,s.obd_sim_type ,s.`status` ,'' ,'' ,'' ,'' ,'' ,'' ,'' ,s.sim_no ,");
        sb.append("s.mobile ,s.use_date ,s.use_period ,s.owner_name,'',s.id  ");
        sb.append("FROM obd_sim s WHERE s.obd_sim_type = 'SINGLE_SIM' ");
        if (isClient) {
          sb.append("AND s.storage_id in(:clientIds) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getMobile())) {
          sb.append("AND s.mobile like :mobile ");
          isHaveMobileField = true;
        }
        if (condition != null && condition.getStartUserDate() != null) {
          sb.append("AND s.use_date >= :startUseDate ");
          isHaveStartUseDate = true;
        }
        if (condition != null && condition.getEndUserDate() != null) {
          sb.append("AND s.use_date <= :endUseDate ");
          isHaveEndUseDate = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND s.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND s.`status` IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }
        sb.append(")");
      }
      // 新增后视镜单品查询
      if (isHaveMirrorSingle) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT o.last_update ,o.obd_sim_type ,o.obd_status ,o.imei ,o.obd_version ,o.spec ,o.color ,o.pack ,");
        sb.append("o.open_crash ,o.open_shake ,'' as sim_no, '' as mobile,'' as use_date,'' as use_period,o.owner_name, ");
        sb.append("o.id ,'' ");
        sb.append("FROM obd o WHERE o.obd_sim_type = 'SINGLE_MIRROR_OBD' ");
        if (isClient) {
          sb.append("AND o.storage_id in(:clientIds) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }

        sb.append(") ");
      }
      // 新增后视镜成品查询
      if (isHaveMirrorObdSimCombine) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT o.last_update ,o.obd_sim_type ,o.obd_status ,o.imei ,o.obd_version ,o.spec ,o.color ,o.pack , ");
        sb.append("o.open_crash ,o.open_shake , s.sim_no , s.mobile ,s.use_date ,s.use_period ,o.owner_name ,");
        sb.append("o.id ,s.id   ");
        sb.append("FROM obd o LEFT JOIN obd_sim_bind osb on o.id = osb.obd_id ");
        sb.append("LEFT JOIN obd_sim s ON osb.sim_id = s.id ");
        sb.append("WHERE o.obd_sim_type = 'COMBINE_MIRROR_OBD_SIM' and osb.status = 'ENABLED' ");
        if (isClient) {
          sb.append("AND (o.storage_id in(:clientIds) OR o.agent_id in(:clientIds) OR o.seller_id in(:clientIds)) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getMobile())) {
          sb.append("AND s.mobile like :mobile ");
          isHaveMobileField = true;
        }
        if (condition != null && condition.getStartUserDate() != null) {
          sb.append("AND s.use_date >= :startUseDate ");
          isHaveStartUseDate = true;
        }
        if (condition != null && condition.getEndUserDate() != null) {
          sb.append("AND s.use_date <= :endUseDate ");
          isHaveEndUseDate = true;
        }
        sb.append(") ");
      }
    }

    //
    sb.append("ORDER BY last_update DESC");
    Query query = session.createSQLQuery(sb.toString())
      .setFirstResult(condition.getStart())
      .setMaxResults(condition.getLimit());
    if (isClient) {
      query.setParameterList("clientIds", condition.getUserIds());
    }
    if (isHaveImeiField) {
      query.setParameter("imei", "%" + condition.getImei() + "%");
    }
    if (isHaveObdVersionField) {
      query.setParameter("obdVersion", "%" + condition.getObdVersion() + "%");
    }
    if (isHaveOwnerNameField) {
      query.setParameter("ownerName", "%" + condition.getOwnerName() + "%");
    }
    if (isHaveObdStatusesField) {
      query.setParameterList("obdStatuses", condition.getObdSimStatusStrArr());
    }
    if (isHaveMobileField) {
      query.setParameter("mobile", "%" + condition.getMobile() + "%");
    }
    if (isHaveStartUseDate) {
      query.setParameter("startUseDate", condition.getStartUserDate());
    }
    if (isHaveEndUseDate) {
      query.setParameter("endUseDate", condition.getEndUserDate());
    }
    return query;
  }

  public static Query countObdSimBindDTOsByAdmin(Session session, ObdSimSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    boolean isHaveObdSingle = true;
    boolean isHaveSimSingle = true;
    boolean isHaveObdSimCombine = true;
    //新增后视镜单品，成品
    boolean isHaveMirrorSingle = true;
    boolean isHaveMirrorObdSimCombine = true;
    boolean isStartUnion = false;
    boolean isHaveImeiField = false;
    boolean isHaveObdVersionField = false;
    boolean isHaveOwnerNameField = false;
    boolean isHaveObdStatusesField = false;
    boolean isHaveMobileField = false;
    boolean isHaveStartUseDate = false;
    boolean isHaveEndUseDate = false;
    boolean isClient = !condition.isAdmin();

    if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimTypes())) {
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.SINGLE_GSM_OBD)) {
        isHaveObdSingle = false;
      }
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.SINGLE_SIM)) {
        isHaveSimSingle = false;
      }
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.COMBINE_GSM_OBD_SIM)) {
        isHaveObdSimCombine = false;
      }
      //新增后视镜单品，成品
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.SINGLE_MIRROR_OBD)) {
        isHaveMirrorSingle = false;
      }
      if (!ArrayUtils.contains(condition.getObdSimTypes(), OBDSimType.COMBINE_MIRROR_OBD_SIM)) {
        isHaveMirrorObdSimCombine = false;
      }
    }

    //只有IMIE号查询时，过滤掉SIM单品
    if (StringUtil.isNotEmpty(condition.getImei())) {
      isHaveSimSingle = false;
    }
    //只用手机号查询时，过滤掉OBD单品和后视镜单品
    if (StringUtil.isNotEmpty(condition.getMobile())) {
      isHaveObdSingle = false;
      isHaveMirrorSingle = false;
    }
    //既有IMIE号又有手机号时，过滤掉OBD单品，后视镜单品和SIM单品
    if (StringUtil.isNotEmpty(condition.getImei()) && StringUtil.isNotEmpty(condition.getMobile())) {
      isHaveObdSingle = false;
      isHaveMirrorSingle = false;
      isHaveSimSingle = false;
    }

    sb.append("select SUM(t2.total) FROM (");

    if (isHaveObdSingle == true && isHaveMirrorSingle == true && isHaveSimSingle == false && isHaveObdSimCombine == false && isHaveMirrorObdSimCombine == false) {
      sb.append("(");
      sb.append("SELECT COUNT(o.id) as total ");
      sb.append("FROM obd o WHERE (o.obd_sim_type = 'SINGLE_GSM_OBD' or o.obd_sim_type = 'SINGLE_MIRROR_OBD') ");
      if (isClient) {
        sb.append("AND o.storage_id in(:clientIds) ");
      }
      if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
        sb.append("AND o.imei like :imei ");
        isHaveImeiField = true;
      }
      if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
        sb.append("AND o.obd_version like :obdVersion ");
        isHaveObdVersionField = true;
      }
      if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
        sb.append("AND o.owner_name LIKE :ownerName ");
        isHaveOwnerNameField = true;
      }
      if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
        sb.append("AND o.obd_status IN (:obdStatuses) ");
        isHaveObdStatusesField = true;
      }

      sb.append(") ");
    } else {
      if (isHaveObdSingle) {
        sb.append("(");
        sb.append("SELECT COUNT(o.id) as total ");
        sb.append("FROM obd o WHERE o.obd_sim_type = 'SINGLE_GSM_OBD' ");
        if (isClient) {
          sb.append("AND o.storage_id in(:clientIds) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }

        sb.append(") ");
        isStartUnion = true;
      }
      if (isHaveSimSingle) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT COUNT(s.id) as total ");
        sb.append("FROM obd_sim s WHERE s.obd_sim_type = 'SINGLE_SIM' ");
        if (isClient) {
          sb.append("AND s.storage_id in(:clientIds) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getMobile())) {
          sb.append("AND s.mobile like :mobile ");
          isHaveMobileField = true;
        }
        if (condition != null && condition.getStartUserDate() != null) {
          sb.append("AND s.use_date >= :startUseDate ");
          isHaveStartUseDate = true;
        }
        if (condition != null && condition.getEndUserDate() != null) {
          sb.append("AND s.use_date <= :endUseDate ");
          isHaveEndUseDate = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND s.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND s.`status` IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }
        sb.append(")");
      }
      if (isHaveObdSimCombine) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT COUNT(o.id) as total ");
        sb.append("FROM obd o LEFT JOIN obd_sim_bind osb on o.id = osb.obd_id ");
        sb.append("LEFT JOIN obd_sim s ON osb.sim_id = s.id ");
        sb.append("WHERE o.obd_sim_type = 'COMBINE_GSM_OBD_SIM' and osb.status = 'ENABLED' ");
        if (isClient) {
          sb.append("AND (o.storage_id in(:clientIds) OR o.agent_id in(:clientIds) OR o.seller_id in(:clientIds)) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getMobile())) {
          sb.append("AND s.mobile like :mobile ");
          isHaveMobileField = true;
        }
        if (condition != null && condition.getStartUserDate() != null) {
          sb.append("AND s.use_date >= :startUseDate ");
          isHaveStartUseDate = true;
        }
        if (condition != null && condition.getEndUserDate() != null) {
          sb.append("AND s.use_date <= :endUseDate ");
          isHaveEndUseDate = true;
        }
        sb.append(") ");
      }
      //新增后视镜单品查询
      if (isHaveMirrorSingle) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT COUNT(o.id) as total ");
        sb.append("FROM obd o WHERE o.obd_sim_type = 'SINGLE_MIRROR_OBD' ");
        if (isClient) {
          sb.append("AND o.storage_id in(:clientIds) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }
        sb.append(") ");
      }
      //
      //新增后视镜成品查询
      if (isHaveMirrorObdSimCombine) {
        if (isStartUnion) {
          sb.append("UNION ALL ");
        } else {
          isStartUnion = true;
        }
        sb.append("(");
        sb.append("SELECT COUNT(o.id) as total ");
        sb.append("FROM obd o LEFT JOIN obd_sim_bind osb on o.id = osb.obd_id ");
        sb.append("LEFT JOIN obd_sim s ON osb.sim_id = s.id ");
        sb.append("WHERE o.obd_sim_type = 'COMBINE_MIRROR_OBD_SIM' and osb.status = 'ENABLED' ");
        if (isClient) {
          sb.append("AND (o.storage_id in(:clientIds) OR o.agent_id in(:clientIds) OR o.seller_id in(:clientIds)) ");
        }
        if (condition != null && StringUtils.isNotBlank(condition.getImei())) {
          sb.append("AND o.imei like :imei ");
          isHaveImeiField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getObdVersion())) {
          sb.append("AND o.obd_version like :obdVersion ");
          isHaveObdVersionField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getOwnerName())) {
          sb.append("AND o.owner_name LIKE :ownerName ");
          isHaveOwnerNameField = true;
        }
        if (condition != null && !ArrayUtils.isEmpty(condition.getObdSimStatusStrArr())) {
          sb.append("AND o.obd_status IN (:obdStatuses) ");
          isHaveObdStatusesField = true;
        }
        if (condition != null && StringUtils.isNotBlank(condition.getMobile())) {
          sb.append("AND s.mobile like :mobile ");
          isHaveMobileField = true;
        }
        if (condition != null && condition.getStartUserDate() != null) {
          sb.append("AND s.use_date >= :startUseDate ");
          isHaveStartUseDate = true;
        }
        if (condition != null && condition.getEndUserDate() != null) {
          sb.append("AND s.use_date <= :endUseDate ");
          isHaveEndUseDate = true;
        }
        sb.append(") ");
      }
    }


    //
    sb.append(") as t2");
    Query query = session.createSQLQuery(sb.toString());
    if (isClient) {
      query.setParameterList("clientIds", condition.getUserIds());
    }
    if (isHaveImeiField) {
      query.setParameter("imei", "%" + condition.getImei() + "%");
    }
    if (isHaveObdVersionField) {
      query.setParameter("obdVersion", "%" + condition.getObdVersion() + "%");
    }
    if (isHaveOwnerNameField) {
      query.setParameter("ownerName", "%" + condition.getOwnerName() + "%");
    }
    if (isHaveObdStatusesField) {
      query.setParameterList("obdStatuses", condition.getObdSimStatusStrArr());
    }
    if (isHaveMobileField) {
      query.setParameter("mobile", "%" + condition.getMobile() + "%");
    }
    if (isHaveStartUseDate) {
      query.setParameter("startUseDate", condition.getStartUserDate());
    }
    if (isHaveEndUseDate) {
      query.setParameter("endUseDate", condition.getEndUserDate());
    }
    return query;
  }

  public static Query countObdSimBindByShop(Session session, ObdSimSearchCondition condition) throws ParseException {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(osb.id) from obd_sim_bind osb " +
      "join obd_sim os on osb.sim_id=os.id " +
      "join obd o on o.id=osb.obd_id " +
      "left join obd_user_vehicle ouv on osb.obd_id=ouv.obd_id and (ouv.status='BUNDLING' or ouv.status='UN_BUNDLING') " +
      "left join vehicle v on v.gsm_obd_imei=o.imei  and (v.status is null or v.status ='ENABLED') " +
      "left join customer_vehicle cv on cv.vehicle_id=v.id and (cv.status IS NULL OR cv.status = 'ENABLED') " +
      "left join customer cus on cus.id=cv.customer_id and (cus.status is null or cus.status = :status) ");
    sb.append(" where osb.status=:status and o.obd_status in (:obdStatusList) and os.status in (:obdStatusList) and (ouv.status is null or ouv.status='BUNDLING' or ouv.status='UN_BUNDLING')");
    if (condition.getObdId() != null) {
      sb.append(" and o.id =:obdId");
    }
    if (condition.getSellShopId() != null) {
      sb.append(" and o.sell_shop_id =:shopId");
      sb.append(" and (v.shop_id is null or v.shop_id =:shopId)");
    }
    if (StringUtil.isNotEmpty(condition.getLicenceNo())) {
      sb.append(" and v.licence_no like :licenceNo");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleBrand())) {
      sb.append(" and v.brand like :vehicleBrand");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleModel())) {
      sb.append(" and v.model like :vehicleModel");
    }
    if (StringUtil.isNotEmpty(condition.getEngineNo())) {
      sb.append(" and v.engine_no like :engineNo");
    }
    if (StringUtil.isNotEmpty(condition.getChassisNumber())) {
      sb.append(" and v.chassis_number like :chassisNumber");
    }
    if (StringUtil.isNotEmpty(condition.getImei())) {
      sb.append(" and o.imei like :imei");
    }
    if (StringUtil.isNotEmpty(condition.getMobile())) {
      sb.append(" and os.mobile like :mobile");
    }
    if (StringUtil.isNotEmpty(condition.getStartTimeStr())) {
      sb.append(" and o.storage_time>=:startTime");
    }
    if (StringUtil.isNotEmpty(condition.getEndTimeStr())) {
      sb.append(" and o.storage_time<=:endTime");
    }
    if (ArrayUtil.isNotEmpty(condition.getObdStatusList())) {
      sb.append(" and o.obd_status in(:obdStatuses)");
    }
    if (ArrayUtil.isNotEmpty(condition.getUserTypes()) && condition.getUserTypes().length == 1) {
      ObdSimSearchCondition.UserType userType = condition.getUserTypes()[0];
      if (ObdSimSearchCondition.UserType.FREE.equals(userType)) {
        sb.append(" and (os.use_date+").append(DateUtil.YEAR_MILLION_SECONDS).append("*os.use_period)>=").append(System.currentTimeMillis());
      } else if (ObdSimSearchCondition.UserType.NOT_FREE.equals(userType)) {
        sb.append(" and (os.use_date+").append(DateUtil.YEAR_MILLION_SECONDS).append("*os.use_period)<=").append(System.currentTimeMillis());
      }
    }
    Query query = session.createSQLQuery(sb.toString())
      .setParameter("status", CustomerStatus.ENABLED.name())
      .setParameterList("obdStatusList", OBDStatus.EnabledStatusStrArr);
    if (condition.getObdId() != null) {
      query.setParameter("obdId", condition.getObdId());
    }
    if (condition.getSellShopId() != null) {
      query.setParameter("shopId", condition.getSellShopId());
    }
    if (StringUtil.isNotEmpty(condition.getLicenceNo())) {
      query.setParameter("licenceNo", "%" + condition.getLicenceNo() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleBrand())) {
      query.setParameter("vehicleBrand", "%" + condition.getVehicleBrand() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleModel())) {
      query.setParameter("vehicleModel", "%" + condition.getVehicleModel() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getEngineNo())) {
      query.setParameter("engineNo", "%" + condition.getEngineNo() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getChassisNumber())) {
      query.setParameter("chassisNumber", "%" + condition.getChassisNumber() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getImei())) {
      query.setParameter("imei", "%" + condition.getImei() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getMobile())) {
      query.setParameter("mobile", "%" + condition.getMobile() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getStartTimeStr())) {
      query.setParameter("startTime", DateUtil.getStartTimeOfDate(condition.getStartTimeStr()));
    }
    if (StringUtil.isNotEmpty(condition.getEndTimeStr())) {
      query.setParameter("endTime", DateUtil.getEndTimeOfDate(condition.getEndTimeStr()));
    }
    if (ArrayUtil.isNotEmpty(condition.getObdStatusList())) {
      query.setParameterList("obdStatuses", condition.getObdStatusList());
    }
    return query;
  }


  public static Query getObdSimByMobiles(Session session, Set<String> mobileSet) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdSim where mobile in(:mobileSet) and status in(:status)");
    Query query = session.createQuery(sb.toString())
      .setParameterList("mobileSet", mobileSet)
      .setParameterList("status", OBDStatus.EnabledStatusSet);
    return query;
  }

  public static Query getObdSimByIds(Session session, Set<Long> ids) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdSim where id in(:ids) and status in(:status)");
    Query query = session.createQuery(sb.toString())
      .setParameterList("ids", ids)
      .setParameterList("status", OBDStatus.EnabledStatusSet);
    return query;
  }

  public static Query getObdSimByMobile(Session session, String mobile) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdSim where mobile =:mobile and status in(:status)");
    Query query = session.createQuery(sb.toString())
      .setParameter("mobile", mobile)
      .setParameterList("status", OBDStatus.EnabledStatusSet);
    return query;
  }

  public static Query getObdSimBySimNos(Session session, Set<String> simNoSet) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdSim where simNo in(:simNoSet) and status in(:status)");
    Query query = session.createQuery(sb.toString())
      .setParameterList("simNoSet", simNoSet)
      .setParameterList("status", OBDStatus.EnabledStatusSet);
    return query;
  }

  public static Query getLastObdHistoryByObdId(Session session, Long id) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdHistory where obdId =:id order by creationDate desc");
    Query query = session.createQuery(sb.toString())
      .setParameter("id", id).setMaxResults(1);
    return query;
  }

  public static Query getLastObdSimHistoryBySimId(Session session, Long id) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdSimHistory where obdSimId =:id order by creationDate desc");
    Query query = session.createQuery(sb.toString())
      .setParameter("id", id).setMaxResults(1);
    return query;
  }

  public static Query getObdSimOperationLogs(Session session, OBDSimOperationLogDTOSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("from OBDSimOperationLog where ");
    if (condition.getObdId() != null) {
      sb.append("obdId =:obdId ");
    }
    if (condition.getObdId() != null && condition.getSimId() != null) {
      sb.append(" or ");
    }
    if (condition.getSimId() != null) {
      sb.append("simId =:simId ");
    }
    sb.append("order by operationDate desc ");
    Query query = session.createQuery(sb.toString())
      .setFirstResult(condition.getStart())
      .setMaxResults(condition.getLimit());
    if (condition.getObdId() != null) {
      query.setParameter("obdId", condition.getObdId());
    }
    if (condition.getSimId() != null) {
      query.setParameter("simId", condition.getSimId());
    }
    return query;
  }

  public static Query countObdSimOperationLogs(Session session, OBDSimOperationLogDTOSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(o.id) from OBDSimOperationLog o where ");
    if (condition.getObdId() != null) {
      sb.append("o.obdId =:obdId ");
    }
    if (condition.getObdId() != null && condition.getSimId() != null) {
      sb.append(" or ");
    }
    if (condition.getSimId() != null) {
      sb.append("o.simId =:simId ");
    }
    Query query = session.createQuery(sb.toString());
    if (condition.getObdId() != null) {
      query.setParameter("obdId", condition.getObdId());
    }
    if (condition.getSimId() != null) {
      query.setParameter("simId", condition.getSimId());
    }
    return query;
  }

  public static Query getObdSimBindByObdIdAndSimId(Session session, Long obdId, Long simId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdSimBind where obdId =:obdId and simId =:simId and status =:status");
    Query query = session.createQuery(sb.toString())
      .setParameter("obdId", obdId)
      .setParameter("simId", simId)
      .setParameter("status", ObdSimBindStatus.ENABLED);
    return query;
  }

  public static Query getObdSimBindsByObdIds(Session session, Set<Long> obdIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ObdSimBind where obdId in(:obdIds) and status =:status");
    Query query = session.createQuery(sb.toString())
      .setParameterList("obdIds", obdIds)
      .setParameter("status", ObdSimBindStatus.ENABLED);
    return query;
  }

  public static Query getObdImeiSuggestion(Session session, ObdImeiSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select o.imei from OBD o where obdSimType in(:obdSimType)  and obdStatus in(:obdStatus)");
    if (StringUtils.isNotBlank(suggestion.getQueryImei())) {
      sb.append("and o.imei like :queryImei ");
    }

    Query query = session.createQuery(sb.toString())
      .setParameterList("obdSimType", suggestion.getObdSimTypes())
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
//        .setParameter("obdType", ObdType.GSM)
      .setFirstResult(suggestion.getStart())
      .setMaxResults(suggestion.getLimit());
    if (StringUtils.isNotBlank(suggestion.getQueryImei())) {
      query.setParameter("queryImei", new StringBuilder("%").append(suggestion.getQueryImei()).append("%").toString());
    }

    return query;
  }

  public static Query countObdImeiSuggestion(Session session, ObdImeiSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(o.imei) from OBD o where obdSimType in(:obdSimType) and o.obdType =:obdType and obdStatus in(:obdStatus)");
    if (StringUtils.isNotBlank(suggestion.getQueryImei())) {
      sb.append("and o.imei like :queryImei ");
    }

    Query query = session.createQuery(sb.toString())
      .setParameterList("obdSimType", suggestion.getObdSimTypes())
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
      .setParameter("obdType", ObdType.GSM);
    if (StringUtils.isNotBlank(suggestion.getQueryImei())) {
      query.setParameter("queryImei", new StringBuilder("%").append(suggestion.getQueryImei()).append("%").toString());
    }
    return query;
  }

  public static Query countObdSimMobileSuggestion(Session session, SimMobileSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(o.mobile) from ObdSim o where obdSimType in(:obdSimType) and status in(:obdStatus)");
    if (StringUtils.isNotBlank(suggestion.getQueryMobile())) {
      sb.append("and o.mobile like :queryMobile ");
    }

    Query query = session.createQuery(sb.toString())
      .setParameterList("obdSimType", suggestion.getObdSimTypes())
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet);
    if (StringUtils.isNotBlank(suggestion.getQueryMobile())) {
      query.setParameter("queryMobile", new StringBuilder("%").append(suggestion.getQueryMobile()).append("%").toString());
    }
    return query;
  }

  public static Query getObdSimMobileSuggestion(Session session, SimMobileSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select o.mobile from ObdSim o where obdSimType in(:obdSimType) and status in(:obdStatus)");
    if (StringUtils.isNotBlank(suggestion.getQueryMobile())) {
      sb.append("and o.mobile like :queryMobile ");
    }

    Query query = session.createQuery(sb.toString())
      .setParameterList("obdSimType", suggestion.getObdSimTypes())
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
      .setFirstResult(suggestion.getStart())
      .setMaxResults(suggestion.getLimit());
    if (StringUtils.isNotBlank(suggestion.getQueryMobile())) {
      query.setParameter("queryMobile", new StringBuilder("%").append(suggestion.getQueryMobile()).append("%").toString());
    }

    return query;
  }

  public static Query countObdVersionSuggestion(Session session, ObdVersionSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(distinct o.obdVersion) from OBD o where o.obdType =:obdType and obdStatus in(:obdStatus) and obdVersion is not null ");
    if (StringUtils.isNotBlank(suggestion.getQueryObdVersion())) {
      sb.append("and o.imei like :queryObdVersion ");
    }

    Query query = session.createQuery(sb.toString())
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
      .setParameter("obdType", ObdType.GSM);
    if (StringUtils.isNotBlank(suggestion.getQueryObdVersion())) {
      query.setParameter("queryObdVersion", new StringBuilder("%").append(suggestion.getQueryObdVersion()).append("%").toString());
    }
    return query;
  }

  public static Query getObdVersionSuggestion(Session session, ObdVersionSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select distinct o.obdVersion from OBD o where o.obdType =:obdType and obdStatus in(:obdStatus) and obdVersion is not null ");
    if (StringUtils.isNotBlank(suggestion.getQueryObdVersion())) {
      sb.append("and o.imei like :queryObdVersion ");
    }

    Query query = session.createQuery(sb.toString())
      .setParameterList("obdStatus", OBDStatus.EnabledStatusSet)
      .setParameter("obdType", ObdType.GSM)
      .setFirstResult(suggestion.getStart())
      .setMaxResults(suggestion.getLimit());
    if (StringUtils.isNotBlank(suggestion.getQueryObdVersion())) {
      query.setParameter("queryObdVersion", new StringBuilder("%").append(suggestion.getQueryObdVersion()).append("%").toString());
    }

    return query;
  }

  public static Query countAgentNameSuggestion(Session session, AgentNameSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(id) from User where departmentId in(:departmentIds) and status =:status and shopId = :shopId ");
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      sb.append("and (name like :queryWord or userNo like :queryWord) ");
    }
    Query q = session.createQuery(sb.toString())
      .setParameterList("departmentIds", suggestion.getDepartmentIds())
      .setParameter("shopId", suggestion.getShopId())
      .setParameter("status", Status.active);
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      q.setParameter("queryWord", new StringBuilder("%").append(suggestion.getQueryWord()).append("%").toString());
    }
    return q;
  }

  public static Query getAgentNameSuggestion(Session session, AgentNameSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("from User where departmentId in(:departmentIds) and status =:status and shopId = :shopId ");
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      sb.append("and (name like :queryWord or userNo like :queryWord) ");
    }
    Query q = session.createQuery(sb.toString())
      .setParameterList("departmentIds", suggestion.getDepartmentIds())
      .setParameter("shopId", suggestion.getShopId())
      .setParameter("status", Status.active)
      .setFirstResult(suggestion.getStart())
      .setMaxResults(suggestion.getLimit());
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      q.setParameter("queryWord", new StringBuilder("%").append(suggestion.getQueryWord()).append("%").toString());
    }
    return q;
  }

  public static Query countStaffNameSuggestion(Session session, AgentNameSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(id) from User where status =:status and shopId = :shopId ");
    if (CollectionUtils.isNotEmpty(suggestion.getDepartmentIds())) {
      sb.append("and departmentId not in(:departmentIds) ");
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      sb.append("and (name like :queryWord or userNo like :queryWord) ");
    }
    Query q = session.createQuery(sb.toString())
      .setParameter("shopId", suggestion.getShopId())
      .setParameter("status", Status.active);
    if (CollectionUtils.isNotEmpty(suggestion.getDepartmentIds())) {
      q.setParameterList("departmentIds", suggestion.getDepartmentIds());
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      q.setParameter("queryWord", new StringBuilder("%").append(suggestion.getQueryWord()).append("%").toString());
    }
    return q;
  }

  public static Query getStaffNameSuggestion(Session session, AgentNameSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("from User where status =:status and shopId = :shopId ");
    if (CollectionUtils.isNotEmpty(suggestion.getDepartmentIds())) {
      sb.append("and departmentId not in(:departmentIds) ");
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      sb.append("and (name like :queryWord or userNo like :queryWord) ");
    }
    Query q = session.createQuery(sb.toString())
      .setParameter("shopId", suggestion.getShopId())
      .setParameter("status", Status.active)
      .setFirstResult(suggestion.getStart())
      .setMaxResults(suggestion.getLimit());
    if (CollectionUtils.isNotEmpty(suggestion.getDepartmentIds())) {
      q.setParameterList("departmentIds", suggestion.getDepartmentIds());
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      q.setParameter("queryWord", new StringBuilder("%").append(suggestion.getQueryWord()).append("%").toString());
    }
    return q;
  }


  /**
   * 微信素材列表
   */
  public static Query getWXArticleJobs(Session session, WXArticleTemplateDTO wxArticleDTO, Pager pager) {
    StringBuffer hql = new StringBuffer();
    hql.append("select wxa from WXArticleTemplate wxa where 1=1 and wxa.deleted='FALSE' ");
    if (wxArticleDTO == null) {
      return null;
    }
    if (StringUtils.isNotBlank(wxArticleDTO.getDescription())) {
      hql.append(" and wxa.description like:description ");
    }
    if (StringUtils.isNotBlank(wxArticleDTO.getTitle())) {
      hql.append(" and wxa.title like:title ");
    }
    hql.append(" order by wxa.shopId ");
    Query query = session.createQuery(hql.toString());

    if (StringUtils.isNotBlank(wxArticleDTO.getDescription())) {
      query.setString("description", "%" + wxArticleDTO.getDescription() + "%");
    }
    if (StringUtils.isNotBlank(wxArticleDTO.getTitle())) {
      query.setString("title", "%" + wxArticleDTO.getTitle() + "%");
    }
    return query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  /**
   * 查询微信素材数量
   */
  public static Query getCountWXArticleJob(Session session, WXArticleTemplateDTO wxArticleDTO) {
    StringBuffer hql = new StringBuffer();
    hql.append("select count(*) from WXArticleTemplate wxa where 1=1 and wxa.deleted='FALSE'");
    if (wxArticleDTO == null) {
      return null;
    }
    if (StringUtils.isNotBlank(wxArticleDTO.getDescription())) {
      hql.append(" and wxa.description like:description");
    }
    if (StringUtils.isNotBlank(wxArticleDTO.getTitle())) {
      hql.append(" and wxa.title like:title");
    }
    Query query = session.createQuery(hql.toString());
    if (StringUtils.isNotBlank(wxArticleDTO.getDescription())) {
      query.setString("description", "%" + wxArticleDTO.getDescription() + "%");
    }
    if (StringUtils.isNotBlank(wxArticleDTO.getTitle())) {
      query.setString("title", "%" + wxArticleDTO.getTitle() + "%");
    }
    return query;
  }


  public static Query getShopWXUserListByShopIdAndOpenIds(Session session, Long shopId, String[] openIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ShopWXUser  where shopId = :shopId and deleted =:deleted  and openId in (:openIds)");
    Query query = session.createQuery(sb.toString())
      .setParameter("shopId", shopId)
      .setParameterList("openIds", openIds)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  /**
   * 微信user列表
   */
  public static Query getWXUserJobs(Session session, WXUserDTO wxUserDTO, Pager pager) {
    StringBuffer hql = new StringBuffer();
    hql.append("select wxa from WXUser wxa where 1=1 and wxa.deleted='FALSE' ");
    if (wxUserDTO == null) {
      return null;
    }
    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
      hql.append(" and wxa.nickName like:nickName ");
    }
    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
      hql.append(" and wxa.publicNo like:publicNo ");
    }
    hql.append(" order by wxa.subscribeTime desc ");
    Query query = session.createQuery(hql.toString());

    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
      query.setString("nickName", "%" + wxUserDTO.getNickname() + "%");
    }
    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
      query.setString("publicNo", "%" + wxUserDTO.getPublicNo() + "%");
    }
    return query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  /**
   * 查询微信user数量
   */
  public static Query getCountWXUserJob(Session session, WXUserDTO wxUserDTO) {
    StringBuffer hql = new StringBuffer();
    hql.append("select count(*) from WXUser wxa where 1=1 and wxa.deleted='FALSE'");
    if (wxUserDTO == null) {
      return null;
    }
    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
      hql.append(" and wxa.nickName like:nickName ");
    }
    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
      hql.append(" and wxa.publicNo like:publicNo ");
    }
    Query query = session.createQuery(hql.toString());
    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
      query.setString("nickName", "%" + wxUserDTO.getNickname() + "%");
    }
    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
      query.setString("publicNo", "%" + wxUserDTO.getPublicNo() + "%");
    }
    return query;
  }


  /**
   * 微信WXUserVehicle列表
   */
  public static Query getWXUserVehicleJobs(Session session, WXUserDTO wxUserDTO) {
    StringBuffer hql = new StringBuffer();
    hql.append("select wxa from WXUserVehicle wxa where 1=1 and wxa.deleted='FALSE' and wxa.openId= '" + wxUserDTO.getOpenid() + "'");
    if (wxUserDTO == null) {
      return null;
    }
//    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
//      hql.append(" and wxa.nickName like:nickName ");
//    }
//    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
//      hql.append(" and wxa.publicNo like:publicNo ");
//    }
    hql.append(" order by wxa.openId ");
    Query query = session.createQuery(hql.toString());

//    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
//      query.setString("nickName","%"+wxUserDTO.getNickname() + "%");
//    }
//    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
//      query.setString("publicNo",  "%"+wxUserDTO.getPublicNo() + "%");
//    }
    return query;
  }

  /**
   * 查询微信WXUserVehicle数量
   */
  public static Query getCountWXUserVehicleJob(Session session, WXUserDTO wxUserDTO) {
    StringBuffer hql = new StringBuffer();
    hql.append("select count(*) from WXUserVehicle wxa where 1=1 and wxa.deleted='FALSE'and wxa.openId= '" + wxUserDTO.getOpenid() + "'");
    if (wxUserDTO == null) {
      return null;
    }
//    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
//      hql.append(" and wxa.nickName like:nickName ");
//    }
//    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
//      hql.append(" and wxa.publicNo like:publicNo ");
//    }
    Query query = session.createQuery(hql.toString());
//    if (StringUtils.isNotBlank(wxUserDTO.getNickname())) {
//      query.setString("nickName","%"+wxUserDTO.getNickname() + "%");
//    }
//    if (StringUtils.isNotBlank(wxUserDTO.getPublicNo())) {
//      query.setString("publicNo",  "%"+wxUserDTO.getPublicNo() + "%");
//    }
    return query;
  }


  /**
   * 获取area
   */
  public static Query getAreaJobs(Session session, String no) {
    StringBuffer hql = new StringBuffer();
    hql.append("select wxa from Area wxa where 1=1 and wxa.no= '" + no + "'");
    if (no == null) {
      return null;
    }
    Query query = session.createQuery(hql.toString());
    return query;
  }


  /**
   * 根据publicNo获取公众号名称
   */
  public static Query getPublicNameByPublicNo(Session session, String publicNo) {
    StringBuffer hql = new StringBuffer();
    hql.append("select wxa from WXAccount wxa where 1=1 and wxa.publicNo= '" + publicNo + "'");
    if (publicNo == null) {
      return null;
    }
    Query query = session.createQuery(hql.toString());
    return query;
  }


  public static Query getShopInternalVehicleByShopId(Session session, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ShopInternalVehicle where shopId =:shopId and deleted =:deleted ");
    Query query = session.createQuery(sb.toString())
      .setParameter("shopId", shopId)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query countShopInternalVehicleGroupByShopId(Session session) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(t.shop_id) from (SELECT s.shop_id FROM `shop_internal_vehicle` s where s.deleted = 'FALSE'  GROUP BY s.shop_id ) as t");
    Query query = session.createSQLQuery(sb.toString());
    return query;
  }

  public static Query getShopInternalVehicleShopIds(Session session, Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT s.shop_id as shopId FROM `shop_internal_vehicle` s where s.deleted = 'FALSE'  GROUP BY s.shop_id ORDER BY s.last_update DESC ");
    Query query = session.createSQLQuery(sb.toString()).addScalar("shopId", StandardBasicTypes.LONG);
    query.setFirstResult(pager.getRowStart());
    query.setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query getShopInternalVehicleByShopIds(Session session, List<Long> shopIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ShopInternalVehicle where shopId in(:shopIds) and deleted =:deleted ");
    Query query = session.createQuery(sb.toString())
      .setParameterList("shopIds", shopIds)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query getQueryInternalVehicleNo(Session session, Long shopId, String vehicleNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT v.licence_no,v.id FROM shop_internal_vehicle siv LEFT JOIN vehicle v ON siv.vehicle_id = v.id ")
      .append("where siv.shop_id = :shopId AND siv.deleted = 'FALSE' AND (v.`status` IS NULL OR v.`status` = 'ENABLED') ");
    if (StringUtils.isNotBlank(vehicleNo)) {
      sb.append("AND v.licence_no LIKE :vehicleNo");
    }
    Query query = session.createSQLQuery(sb.toString())
      .setParameter("shopId", shopId);
    if (StringUtils.isNotBlank(vehicleNo)) {
      query.setParameter("vehicleNo", "%" + vehicleNo.trim().toUpperCase() + "%");
    }
    return query;
  }

  public static Query getShopDriveLogStat(Session session, ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT  siv.vehicle_id AS vehicleId, SUM(d.distance) AS distance,SUM(d.oil_cost) as oilWear,");
    sb.append("IF(SUM(d.distance)>0,SUM(d.oil_cost)/SUM(d.distance)*100,0) AS avgOilWear,");
    sb.append("SUM(d.travel_time) AS travelTimeStr,COUNT(d.id) AS driveCount ");
    sb.append("FROM shop_internal_vehicle siv ");
    sb.append("LEFT JOIN app_user_customer auc ON auc.shop_vehicle_id = siv.vehicle_id AND siv.deleted = 'FALSE' ");
    sb.append("LEFT JOIN drive_log d ON auc.app_user_no = d.app_user_no AND d.`status` IN ('ENABLED','DRIVING') ");
    if (shopInternalVehicleRequestDTO.getStartDate() != null) {
      sb.append("AND d.start_time >=:startTime  ");
    }
    if (shopInternalVehicleRequestDTO.getEndDate() != null) {
      sb.append("AND d.end_time <=:endTime ");
    }

    sb.append("where siv.shop_id = :shopId AND siv.id IS NOT NULL  AND siv.deleted = 'FALSE' ");
    if (CollectionUtils.isNotEmpty(shopInternalVehicleRequestDTO.getVehicleIds())) {
      sb.append("AND siv.vehicle_id in(:vehicleIds)  ");
    }
    sb.append("GROUP BY siv.vehicle_id ");
    if (StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getSort())
      && StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getOrder())) {
      sb.append("ORDER BY ")
        .append(shopInternalVehicleRequestDTO.getSort())
        .append(" ")
        .append(shopInternalVehicleRequestDTO.getOrder());
    }

    Query query = session.createSQLQuery(sb.toString())
      .addScalar("vehicleId", StandardBasicTypes.LONG)
      .addScalar("distance", StandardBasicTypes.DOUBLE)
      .addScalar("oilWear", StandardBasicTypes.DOUBLE)
      .addScalar("avgOilWear", StandardBasicTypes.DOUBLE)
      .addScalar("travelTimeStr", StandardBasicTypes.LONG)
      .addScalar("driveCount", StandardBasicTypes.INTEGER)
      .setParameter("shopId", shopInternalVehicleRequestDTO.getShopId());
    if (CollectionUtils.isNotEmpty(shopInternalVehicleRequestDTO.getVehicleIds())) {
      query.setParameterList("vehicleIds", shopInternalVehicleRequestDTO.getVehicleIds());
    }
    if (shopInternalVehicleRequestDTO.getStartDate() != null) {
      query.setParameter("startTime", shopInternalVehicleRequestDTO.getStartDate());
    }
    if (shopInternalVehicleRequestDTO.getEndDate() != null) {
      query.setParameter("endTime", shopInternalVehicleRequestDTO.getEndDate());
    }
    query.setFirstResult((shopInternalVehicleRequestDTO.getPage() - 1) * shopInternalVehicleRequestDTO.getRows());
    query.setMaxResults(shopInternalVehicleRequestDTO.getRows());
    return query;
  }

  public static Query countShopDriveLogStat(Session session, ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    StringBuilder sb = new StringBuilder();

    sb.append("SELECT  COUNT(DISTINCT siv.vehicle_id) ");
    sb.append("FROM shop_internal_vehicle siv ");
    sb.append("where  siv.shop_id = :shopId AND siv.id IS NOT NULL AND siv.deleted = 'FALSE' ");
    if (CollectionUtils.isNotEmpty(shopInternalVehicleRequestDTO.getVehicleIds())) {
      sb.append("AND siv.vehicle_id in(:vehicleIds)  ");
    }
    Query query = session.createSQLQuery(sb.toString())
      .setParameter("shopId", shopInternalVehicleRequestDTO.getShopId());
    if (CollectionUtils.isNotEmpty(shopInternalVehicleRequestDTO.getVehicleIds())) {
      query.setParameterList("vehicleIds", shopInternalVehicleRequestDTO.getVehicleIds());
    }
    return query;
  }

  public static Query countShopDriveLog(Session session, ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    StringBuilder sb = new StringBuilder();

    sb.append("SELECT COUNT(d.id) ");
    sb.append("FROM app_user_customer auc LEFT JOIN drive_log d ON auc.app_user_no = d.app_user_no ");
    sb.append("LEFT JOIN shop_internal_vehicle siv ON siv.vehicle_id = auc.shop_vehicle_id AND siv.deleted = 'FALSE' ");
    sb.append("where auc.shop_id = :shopId AND auc.shop_vehicle_id = :vehicleId ");
    sb.append("AND d.`status` IN ('ENABLED','DRIVING') AND auc.match_type = 'IMEI_MATCH'");
    sb.append("AND siv.id IS NOT NULL ");
    if (shopInternalVehicleRequestDTO.getStartDate() != null) {
      sb.append("AND d.start_time >=:startTime  ");
    }
    if (shopInternalVehicleRequestDTO.getEndDate() != null) {
      sb.append("AND d.end_time <=:endTime ");
    }
    Long vehicleId = null;
    for (Long vTemp : shopInternalVehicleRequestDTO.getVehicleIds()) {
      if (vTemp != null) {
        vehicleId = vTemp;
        break;
      }
    }
    Query query = session.createSQLQuery(sb.toString())
      .setParameter("shopId", shopInternalVehicleRequestDTO.getShopId())
      .setParameter("vehicleId", vehicleId);
    if (shopInternalVehicleRequestDTO.getStartDate() != null) {
      query.setParameter("startTime", shopInternalVehicleRequestDTO.getStartDate());
    }
    if (shopInternalVehicleRequestDTO.getEndDate() != null) {
      query.setParameter("endTime", shopInternalVehicleRequestDTO.getEndDate());
    }
    return query;
  }

  public static Query getShopDriveLogDTOs(Session session, ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT d.id,d.start_place,d.end_place,d.start_time,d.end_time,d.travel_time,d.distance,d.oil_cost,d.oil_wear ");
    sb.append("FROM app_user_customer auc ");
    sb.append("LEFT JOIN drive_log d ON auc.app_user_no = d.app_user_no ");
    sb.append("LEFT JOIN shop_internal_vehicle siv ON siv.vehicle_id = auc.shop_vehicle_id AND siv.deleted = 'FALSE' ");
    sb.append("WHERE auc.shop_id = :shopId AND auc.shop_vehicle_id = :vehicleId  ");
    sb.append("AND d.`status` IN ('ENABLED','DRIVING') AND auc.match_type = 'IMEI_MATCH'  ");
    sb.append("AND siv.id IS NOT NULL  ");


    if (shopInternalVehicleRequestDTO.getStartDate() != null) {
      sb.append("AND d.start_time >=:startTime  ");
    }
    if (shopInternalVehicleRequestDTO.getEndDate() != null) {
      sb.append("AND d.end_time <=:endTime ");
    }
    sb.append("ORDER BY d.start_time DESC  ");

//    if (StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getSort())
//        && StringUtils.isNotBlank(shopInternalVehicleRequestDTO.getOrder())) {
//      sb.append("ORDER BY ")
//          .append(shopInternalVehicleRequestDTO.getOrder())
//          .append(" ")
//          .append(shopInternalVehicleRequestDTO.getSort());
//    }

    Long vehicleId = null;
    for (Long vTemp : shopInternalVehicleRequestDTO.getVehicleIds()) {
      if (vTemp != null) {
        vehicleId = vTemp;
        break;
      }
    }
    Query query = session.createSQLQuery(sb.toString())
//        .addScalar("vehicleId", StandardBasicTypes.LONG)
//        .addScalar("distance", StandardBasicTypes.DOUBLE)
//        .addScalar("oilWear", StandardBasicTypes.DOUBLE)
//        .addScalar("avgOilWear", StandardBasicTypes.DOUBLE)
//        .addScalar("travelTimeStr", StandardBasicTypes.LONG)
//        .addScalar("driveCount", StandardBasicTypes.INTEGER)
      .setParameter("shopId", shopInternalVehicleRequestDTO.getShopId())
      .setParameter("vehicleId", vehicleId);

    if (shopInternalVehicleRequestDTO.getStartDate() != null) {
      query.setParameter("startTime", shopInternalVehicleRequestDTO.getStartDate());
    }
    if (shopInternalVehicleRequestDTO.getEndDate() != null) {
      query.setParameter("endTime", shopInternalVehicleRequestDTO.getEndDate());
    }
    query.setFirstResult((shopInternalVehicleRequestDTO.getPage() - 1) * shopInternalVehicleRequestDTO.getRows());
    query.setMaxResults(shopInternalVehicleRequestDTO.getRows());
    return query;
  }


  public static Query findAppVehicleFaultInfoList(Session session, String appUserNo,
                                                  String status) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppVehicleFaultInfo where appUserNo =:appUserNo and status =:status ");
    sb.append(" order by lastOperateTime desc,reportTime desc ");
    Query query = session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo).setParameter("status", ErrorCodeTreatStatus.valueOf(status));
    return query;
  }

  public static Query countGetImpactVideoExpDTOs_page(Session session, String shopId, ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(*) ");
    sb.append("FROM impact i ");
    sb.append("JOIN impact_video iv ON i.uuid = iv.uuid  ");
    if (impactInfoSearchConditionDTO.getStatus() != null) {
      sb.append("AND iv.deleted = :deleted  ");
    } else {
      sb.append(" AND iv.deleted <> 'DELETED'   ");
    }
      if (impactInfoSearchConditionDTO.getUploadStatus() != null) {
      sb.append("AND iv.upload_status = :upload_status  ");
    }
    sb.append("WHERE i.shop_id = :shopId    ");

    if (impactInfoSearchConditionDTO.getTimeStart() != null) {
      sb.append("AND i.upload_time >=:startTime  ");
    }
    if (impactInfoSearchConditionDTO.getTimeEnd() != null) {
      sb.append("AND i.upload_time <=:endTime  ");
    }
    Query query = session.createSQLQuery(sb.toString()).setParameter("shopId", shopId);
    if (impactInfoSearchConditionDTO.getStatus() != null) {
      query.setParameter("deleted", impactInfoSearchConditionDTO.getStatus());
    }
     if (impactInfoSearchConditionDTO.getUploadStatus() != null) {
      query.setParameter("upload_status", impactInfoSearchConditionDTO.getUploadStatus().toString());
    }
    if (impactInfoSearchConditionDTO.getTimeStart() != null) {
      query.setParameter("startTime", impactInfoSearchConditionDTO.getTimeStart());
    }
    if (impactInfoSearchConditionDTO.getTimeEnd() != null) {
      query.setParameter("endTime", impactInfoSearchConditionDTO.getTimeEnd());
    }
    return query;
  }

  public static Query getImpactVideoExpDTOs_page(Session session, String shopId, ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT i.id,i.uuid,iv.app_user_no,i.upload_time,i.lon,i.lat,iv.id as impactVideoId,iv.path,iv.deleted  ");
    sb.append("FROM impact i ");
    sb.append("JOIN impact_video iv ON i.uuid = iv.uuid  ");
    if (impactInfoSearchConditionDTO.getStatus() != null) {
      sb.append("AND iv.deleted = :deleted  ");
    } else {
      sb.append("AND iv.deleted <> 'DELETED'   ");
    }
    if (impactInfoSearchConditionDTO.getUploadStatus() != null) {
      sb.append("AND iv.upload_status = :upload_status  ");
    }
    sb.append("WHERE i.shop_id = :shopId   ");

    if (impactInfoSearchConditionDTO.getTimeStart() != null) {
      sb.append("AND i.upload_time >=:startTime  ");
    }
    if (impactInfoSearchConditionDTO.getTimeEnd() != null) {
      sb.append("AND i.upload_time <=:endTime  ");
    }

    sb.append("ORDER BY i.upload_time DESC  ");
    Query query = session.createSQLQuery(sb.toString())
      .addScalar("id", StandardBasicTypes.LONG)
      .addScalar("uuid", StandardBasicTypes.STRING)
      .addScalar("app_user_no", StandardBasicTypes.STRING)
      .addScalar("upload_time", StandardBasicTypes.LONG)
      .addScalar("lon", StandardBasicTypes.DOUBLE)
      .addScalar("lat", StandardBasicTypes.DOUBLE)
      .addScalar("impactVideoId", StandardBasicTypes.LONG)
      .addScalar("path", StandardBasicTypes.STRING)
      .addScalar("deleted", StandardBasicTypes.STRING)
      .setParameter("shopId", shopId);
    if (impactInfoSearchConditionDTO.getStatus() != null) {
      query.setParameter("deleted", impactInfoSearchConditionDTO.getStatus());
    }
    if (impactInfoSearchConditionDTO.getUploadStatus() != null) {
      query.setParameter("upload_status", impactInfoSearchConditionDTO.getUploadStatus().toString());
    }
    if (impactInfoSearchConditionDTO.getTimeStart() != null) {
      query.setParameter("startTime", impactInfoSearchConditionDTO.getTimeStart());
    }
    if (impactInfoSearchConditionDTO.getTimeEnd() != null) {
      query.setParameter("endTime", impactInfoSearchConditionDTO.getTimeEnd());
    }
    if (impactInfoSearchConditionDTO != null) {
      query.setFirstResult((impactInfoSearchConditionDTO.getStartPageNo() - 1) * impactInfoSearchConditionDTO.getMaxRows()).setMaxResults(impactInfoSearchConditionDTO.getMaxRows());
    }
    return query;
  }

  public static Query countGetImpactVideoExpDTOs(Session session, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(*) ");
    sb.append("FROM impact i ");
    sb.append("JOIN impact_video iv ON i.uuid = iv.uuid AND iv.deleted = 'FALSE' ");
    sb.append("WHERE i.app_user_no = :appUserNo   ");
    sb.append("ORDER BY i.upload_time DESC  ");
    Query query = session.createSQLQuery(sb.toString()).setParameter("appUserNo", appUserNo);
    return query;
  }


  public static Query getImpactVideoExpDTOs(Session session, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT i.uuid,i.app_user_no,i.upload_time,i.lon,i.lat,iv.id,iv.path,iv.upload_status ");
    sb.append("FROM impact i ");
    sb.append("JOIN impact_video iv ON i.uuid = iv.uuid AND iv.deleted = 'FALSE' ");
    sb.append("WHERE i.app_user_no = :appUserNo   ");
    sb.append("ORDER BY i.upload_time DESC  ");
    Query query = session.createSQLQuery(sb.toString()).setParameter("appUserNo", appUserNo);
    return query;
  }

  public static Query getImpactByUUID(Session session, String uuid) {
    StringBuilder sb = new StringBuilder();
    sb.append("from Impact where uuid =:uuid");
    Query query = session.createQuery(sb.toString())
      .setParameter("uuid", uuid);
    return query;
  }


  public static Query getRescueDTOs_page(Session session, Long shopId, SosInfoSearchConditionDTO sosInfoSearchConditionDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT r.id,r.shop_id,r.app_user_no,r.lon,r.lat,r.upload_time,r.upload_server_time,r.addr,r.addr_short,r.sos_status  ");
    sb.append("FROM rescue r  ");
    sb.append("WHERE r.shop_id = :shopId   ");
    if (sosInfoSearchConditionDTO.getTimeStart() != null) {
      sb.append("AND r.upload_time >=:startTime  ");
    }
    if (sosInfoSearchConditionDTO.getTimeEnd() != null) {
      sb.append("AND r.upload_time <=:endTime  ");
    }
    if (sosInfoSearchConditionDTO.getStatus() != null) {
      sb.append("AND r.sos_status = :sosStatus  ");
    } else {
      sb.append("AND r.sos_status <> 'DELETED'   ");
    }
    sb.append("ORDER BY r.upload_time DESC  ");
    Query query = session.createSQLQuery(sb.toString()).setParameter("shopId", shopId);
    if (sosInfoSearchConditionDTO.getStatus() != null) {
      query.setParameter("sosStatus", sosInfoSearchConditionDTO.getStatus());
    }
    if (sosInfoSearchConditionDTO.getTimeStart() != null) {
      query.setParameter("startTime", sosInfoSearchConditionDTO.getTimeStart());
    }
    if (sosInfoSearchConditionDTO.getTimeEnd() != null) {
      query.setParameter("endTime", sosInfoSearchConditionDTO.getTimeEnd());
    }
    if (sosInfoSearchConditionDTO != null) {
      query.setFirstResult((sosInfoSearchConditionDTO.getStartPageNo() - 1) * sosInfoSearchConditionDTO.getMaxRows()).setMaxResults(sosInfoSearchConditionDTO.getMaxRows());
    }
    return query;
  }

  public static Query countRescueDTOs(Session session, Long shopId, SosInfoSearchConditionDTO sosInfoSearchConditionDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(*) ");
    sb.append("FROM rescue r  ");
    sb.append("WHERE r.shop_id = :shopId   ");
    if (sosInfoSearchConditionDTO.getTimeStart() != null) {
      sb.append("AND r.upload_time >=:startTime  ");
    }
    if (sosInfoSearchConditionDTO.getTimeEnd() != null) {
      sb.append("AND r.upload_time <=:endTime  ");
    }
    if (sosInfoSearchConditionDTO.getStatus() != null) {
      sb.append("AND r.sos_status = :sosStatus  ");
    } else {
      sb.append("AND r.sos_status <> 'DELETED'   ");
    }
    Query query = session.createSQLQuery(sb.toString()).setParameter("shopId", shopId);
    if (sosInfoSearchConditionDTO.getStatus() != null) {
      query.setParameter("sosStatus", sosInfoSearchConditionDTO.getStatus());
    }
    if (sosInfoSearchConditionDTO.getTimeStart() != null) {
      query.setParameter("startTime", sosInfoSearchConditionDTO.getTimeStart());
    }
    if (sosInfoSearchConditionDTO.getTimeEnd() != null) {
      query.setParameter("endTime", sosInfoSearchConditionDTO.getTimeEnd());
    }

    return query;
  }


  public static Query getAppVehicleDTOs_page(Session session, Long shopId, MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT av.id,av.mobile,av.contact,av.vehicle_no,av.next_maintain_mileage,av.current_mileage,(av.next_maintain_mileage-av.current_mileage) as toNext_maintain_mileage,av.app_user_no  ");
    sb.append("FROM app_vehicle av  ");
    sb.append("JOIN app_user_customer auc on av.app_user_no = auc.app_user_no ");
    sb.append("WHERE auc.shop_id = :shopId   ");
    if (mileageInfoSearchConditionDTO.getMileageType() != null) {
      if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_500) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 500  ");
      } else if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_500_1000) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) >= 500  ");
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 1000  ");
      } else if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_1000_1500) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) >= 1000  ");
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 1500  ");
      } else if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_1500_2000) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) >= 1500  ");
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 2000  ");
      }
    }
    if (StringUtil.isNotEmpty(mileageInfoSearchConditionDTO.getVehicleNo())) {
      sb.append("AND av.vehicle_no =:vehicleNo  ");
    }
    sb.append("ORDER BY (av.next_maintain_mileage-av.current_mileage) asc    ");
    Query query = session.createSQLQuery(sb.toString()).setParameter("shopId", shopId);
    if (StringUtil.isNotEmpty(mileageInfoSearchConditionDTO.getVehicleNo())) {
      query.setParameter("vehicleNo", mileageInfoSearchConditionDTO.getVehicleNo());
    }
    if (mileageInfoSearchConditionDTO != null) {
      query.setFirstResult((mileageInfoSearchConditionDTO.getStartPageNo() - 1) * mileageInfoSearchConditionDTO.getMaxRows()).setMaxResults(mileageInfoSearchConditionDTO.getMaxRows());
    }
    return query;
  }

  public static Query countAppVehicleDTOs(Session session, Long shopId, MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(*) ");
    sb.append("FROM app_vehicle av  ");
    sb.append("JOIN app_user_customer auc on av.app_user_no = auc.app_user_no ");
    sb.append("WHERE auc.shop_id = :shopId   ");
    if (mileageInfoSearchConditionDTO.getMileageType() != null) {
      if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_500) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 500  ");
      } else if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_500_1000) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) >= 500  ");
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 1000  ");
      } else if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_1000_1500) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) >= 1000  ");
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 1500  ");
      } else if (mileageInfoSearchConditionDTO.getMileageType() == MileageType.MILEAGE_1500_2000) {
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) >= 1500  ");
        sb.append("AND (av.next_maintain_mileage-av.current_mileage) < 2000  ");
      }
    }
    if (StringUtil.isNotEmpty(mileageInfoSearchConditionDTO.getVehicleNo())) {
      sb.append("AND av.vehicle_no =:vehicleNo  ");
    }
    Query query = session.createSQLQuery(sb.toString()).setParameter("shopId", shopId);
    if (StringUtil.isNotEmpty(mileageInfoSearchConditionDTO.getVehicleNo())) {
      query.setParameter("vehicleNo", mileageInfoSearchConditionDTO.getVehicleNo());
    }
    return query;
  }

  public static Query getImpact_detail(Session session, long shopId, String impactId, long upLoadTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT i.id,i.uuid,i.upload_time,i.lon,i.lat,g.rdtc,g.rpm,g.vss  ");
    sb.append("FROM impact i ");
    sb.append("JOIN gsm_vehicle_data g ON i.uuid = g.uuid  ");
    sb.append("WHERE  i.upload_time<= :upLoadTime and i.id = :id and i.shop_id=:shopId   ");  //i.upload_time<= :upLoadTime and
    sb.append("ORDER BY g.uploadTime DESC  ");
    Query query = session.createSQLQuery(sb.toString())
      .setLong("shopId", shopId)
      .setParameter("id", impactId)
      .setLong("upLoadTime", upLoadTime);
    return query;
  }

  static Query getImpactAndVideo(Session session, long vehicleId, long datetime) {
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT i.addr,i.created,i.type,v.id,v.id,v.upload_status ");
    sb.append(" FROM impact AS i, impact_video AS v, app_vehicle AS a ");
    sb.append(" WHERE a.id =:vehicleId AND a.app_user_no = i.app_user_no AND  a.app_user_no = v.app_user_no AND i.uuid = v.uuid  AND i.created <:datetime");
    sb.append(" GROUP BY v.id ORDER BY i.created DESC");
    Query query = session.createSQLQuery(sb.toString())
            .setLong("vehicleId",vehicleId)
            .setLong("datetime",datetime);
    return query;
  }


  public static Query getInnodbTrx(Session session) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT trx_mysql_thread_id,trx_id,unix_timestamp(trx_started)*1000 as trx_started" +
      " FROM information_schema.innodb_trx ");
    Query query = session.createSQLQuery(sb.toString());
    return query;
  }

  public static Query killTrxMysqlThread(Session session, Long threadId) {
    StringBuilder sb = new StringBuilder();
    sb.append("kill :threadId");
    Query query = session.createSQLQuery(sb.toString())
      .setParameter("threadId", threadId);
    return query;
  }

  public static Query findConsumingRecordByShopId(Session session,long shopId , int start , int size) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT  c.coupon AS coupon,c.order_id AS orderId, c.order_types AS orderTypes, c.consumer_time AS consumerTime,");
    sb.append("  u.name AS userName, u.name AS customerName,v.vehicle_no AS vehicleNo, c.id AS id, c.order_status AS orderStatus ");
    sb.append(" FROM  consuming_record c LEFT JOIN bcuser.app_vehicle v ON v.app_user_no=c.app_user_no LEFT JOIN bcuser.app_user u ON u.app_user_no=c.app_user_no ");
    sb.append(" WHERE c.shop_id =:shopId  AND c.order_id IS NULL AND order_types = 'APP_ONFIELD_ORDER' AND (order_status IS NULL OR order_status != 'REPEAL')  ");
    sb.append(" GROUP BY c.id ");
    Query query = session.createSQLQuery(sb.toString())
            .addScalar("coupon", StandardBasicTypes.FLOAT)
            .addScalar("orderId", StandardBasicTypes.LONG)
            .addScalar("orderTypes",StandardBasicTypes.STRING)
            .addScalar("consumerTime",StandardBasicTypes.LONG)
            .addScalar("userName", StandardBasicTypes.STRING)
            .addScalar("customerName", StandardBasicTypes.STRING)
            .addScalar("vehicleNo", StandardBasicTypes.STRING)
            .addScalar("id", StandardBasicTypes.LONG)
            .addScalar("orderStatus",StandardBasicTypes.STRING)
            .setParameter("shopId",shopId)
            .setFirstResult(start).setMaxResults(size);
    return query;
  }

  public static Query findConsumingRecordCountByShopId(Session session , long shopId){
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(*)  FROM ConsumingRecord WHERE shopId =:shopId AND order_id IS NULL AND orderTypes = 'APP_ONFIELD_ORDER' AND (order_status IS NULL OR order_status != 'REPEAL') ");
    Query query = session.createQuery(sb.toString()).setParameter("shopId", shopId);
    return query;
  }
  public static Query getConsumingRecordListByPagerTimeArrayType(Session session, Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO,String arrayType, Pager pager){
    StringBuilder sb=new StringBuilder();
    Long shopId=couponConsumeRecordDTO.getShopId();
    String product=couponConsumeRecordDTO.getProduct();
    String orderType=couponConsumeRecordDTO.getOrderTypes();
    String customerInfo=couponConsumeRecordDTO.getCustomerInfo();
    String incomeType=couponConsumeRecordDTO.getIncomeType();
    if (StringUtil.isEmpty(arrayType)) {
      arrayType = " order by c.created desc ";
    }
    sb.append("SELECT c.app_user_no AS appUserNo, c.receipt_no AS receiptNo, c.coupon AS coupon, ");
    sb.append(" c.shop_id AS shopId, c.order_id AS orderId, c.order_types AS orderTypes, ");
    sb.append(" c.consumer_time AS consumerTime,c.product AS product, c.product_num AS productNum, ");
    sb.append(" c.income_type AS incomeType, c.sum_money AS sumMoney, u.name AS appUserName, v.vehicle_no AS appVehicleNo, c.id AS id, c.customer_info AS customerInfo, c.order_status AS orderStatus");
    sb.append(" FROM consuming_record c ");
    sb.append(" LEFT JOIN app_user u ON u.app_user_no=c.app_user_no ");
    sb.append(" LEFT JOIN app_vehicle v ON v.app_user_no=c.app_user_no ");
    sb.append(" WHERE c.shop_id = :shopId ");
    if(!StringUtil.isEmpty(incomeType)){
      sb.append(" AND c.income_type = :incomeType ");
    }
    if(startTime!=null){
      sb.append(" AND c.consumer_time >= :startTime ");
    }
    if(endTime!=null){
      sb.append(" AND c.consumer_time <= :endTime ");
    }
    if(!StringUtil.isEmpty(orderType)){
      sb.append(" AND (c.order_types = :orderType) ");
    }
    if(!StringUtil.isEmpty(product)){
      sb.append(" AND (c.product = :product) ");
    }
    if(!StringUtil.isEmpty(customerInfo)){
      sb.append(" AND (c.customer_info LIKE :customerInfo OR u.name LIKE :customerInfo OR v.vehicle_no LIKE :customerInfo) ");
    }
    sb.append(" GROUP BY c.id ");
    sb.append(arrayType);

    Query q = session.createSQLQuery(sb.toString())
            .addScalar("appUserNo", StandardBasicTypes.STRING)
            .addScalar("receiptNo",StandardBasicTypes.STRING)
            .addScalar("coupon",StandardBasicTypes.DOUBLE)
            .addScalar("shopId",StandardBasicTypes.LONG)
            .addScalar("orderId", StandardBasicTypes.LONG)
            .addScalar("orderTypes",StandardBasicTypes.STRING)
            .addScalar("consumerTime",StandardBasicTypes.LONG)
            .addScalar("product",StandardBasicTypes.STRING)
            .addScalar("productNum",StandardBasicTypes.INTEGER)
            .addScalar("incomeType",StandardBasicTypes.STRING)
            .addScalar("sumMoney",StandardBasicTypes.FLOAT)
            .addScalar("appUserName",StandardBasicTypes.STRING)
            .addScalar("appVehicleNo",StandardBasicTypes.STRING)
            .addScalar("id", StandardBasicTypes.LONG)
            .addScalar("customerInfo", StandardBasicTypes.STRING)
            .addScalar("orderStatus", StandardBasicTypes.STRING);
    if(shopId!=null){
      q.setLong("shopId", shopId);
    }
    else{
      q.setLong("shopId",0L);
    }
    if(!StringUtil.isEmpty(incomeType)) {
      q.setString("incomeType", incomeType);
    }
    if(startTime!=null) {
      q.setLong("startTime", startTime);
    }
    if(endTime!=null) {
      q.setLong("endTime", endTime);
    }
    if(!StringUtil.isEmpty(orderType)){
      q.setString("orderType", orderType);
    }
    if(!StringUtil.isEmpty(product)){
      q.setString("product", product);
    }
    if(!StringUtil.isEmpty(customerInfo)) {
      q.setString("customerInfo", "%"+customerInfo+"%");
    }
    if(pager!=null) {
      q.setFirstResult(pager.getRowStart())
              .setMaxResults(pager.getPageSize());
    }
    return q;
  }
  public static Query countConsumingRecordAndSumCoupon(Session session, Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO){
    StringBuilder sb=new StringBuilder();
    Long shopId=couponConsumeRecordDTO.getShopId();
    String product=couponConsumeRecordDTO.getProduct();
    String orderType=couponConsumeRecordDTO.getOrderTypes();
    String customerInfo=couponConsumeRecordDTO.getCustomerInfo();
    String incomeType=couponConsumeRecordDTO.getIncomeType();
    sb.append("SELECT COUNT(id) AS count,SUM(coupon) AS sumCoupon FROM consuming_record WHERE id IN ( ");
    sb.append(" SELECT c.id FROM consuming_record c ");
    sb.append(" LEFT JOIN app_user u ON u.app_user_no=c.app_user_no ");
    sb.append(" LEFT JOIN app_vehicle v ON v.app_user_no=c.app_user_no ");
    sb.append(" WHERE c.shop_id = :shopId ");
    if(!StringUtil.isEmpty(incomeType)){
      sb.append(" AND c.income_type = :incomeType ");
    }
    if(startTime!=null){
      sb.append(" AND c.consumer_time >= :startTime ");
    }
    if(endTime!=null){
      sb.append(" AND c.consumer_time <= :endTime ");
    }
    if(!StringUtil.isEmpty(orderType)){
      sb.append(" AND (c.order_types = :orderType) ");
    }
    if(!StringUtil.isEmpty(product)){
      sb.append(" AND (c.product = :product) ");
    }
    if(!StringUtil.isEmpty(customerInfo)){
      sb.append(" AND (u.name LIKE :customerInfo OR v.vehicle_no LIKE :customerInfo) ");
    }
    sb.append(" ) ");
    Query q = session.createSQLQuery(sb.toString());
    if(shopId!=null){
      q.setLong("shopId",shopId);
    }
    else{
      q.setLong("shopId",0L);
    }
    if(!StringUtil.isEmpty(incomeType)) {
      q.setString("incomeType", incomeType);
    }
    if(startTime!=null) {
      q.setLong("startTime", startTime);
    }
    if(endTime!=null) {
      q.setLong("endTime", endTime);
    }
    if(!StringUtil.isEmpty(orderType)){
      q.setString("orderType", orderType);
    }
    if(!StringUtil.isEmpty(product)){
      q.setString("product", product);
    }
    if(!StringUtil.isEmpty(customerInfo)) {
      q.setString("customerInfo", "%"+customerInfo+"%");
    }
    return q;
  }

  /**
   * 通过代金券消费记录id查询对应的记录
   * @param session
   * @param id
   * @return 代金券消费记录
   */
  public static Query getCouponConsumeRecordById(Session session,Long id){
    Query q = session.createQuery("SELECT c FROM ConsumingRecord c WHERE  c.id= :id");
    q.setLong("id",id);
    return q;
  }

  /**
   * 通过shopId和代金券消费记录id查询对应的记录
   * @param session
   * @param shopId
   * @param id
   * @return 代金券消费记录
   */
  public static Query getCouponConsumeRecordByShopIdAndId(Session session,Long shopId,Long id){
    Query q = session.createQuery("FROM ConsumingRecord c WHERE  c.id= :id AND c.shopId= :shopId");
    q.setLong("id",id);
    q.setLong("shopId", shopId);
    return q;
  }

  public static Query getCoupon(Session session , String appUserNo){
    StringBuilder sb = new StringBuilder();
    sb.append("FROM Coupon c WHERE c.appUserNo =:appUserNo");
    Query q = session.createQuery(sb.toString()).setParameter("appUserNo",appUserNo);
    return q;
  }

  /**
   * 根据Order中的信息更新ConsumingRecord
   * @param session
   * @param shopId
   * @return
   */
  public static Query updateConsumingRecordFromOrderInfo(Session session,Long shopId,CouponConsumeRecordDTO couponConsumeRecordDTO) throws ParseException {
    Long id=couponConsumeRecordDTO.getId();
    Long orderId=couponConsumeRecordDTO.getOrderId();
    String orderStatus=null;
    if(couponConsumeRecordDTO.getOrderStatus()!=null) {
      orderStatus = couponConsumeRecordDTO.getOrderStatus().name();
    }
    Long consumerTime=couponConsumeRecordDTO.getConsumerTimeStamp();
    String product=couponConsumeRecordDTO.getProduct();
    Float sumMoney=couponConsumeRecordDTO.getSumMoney();
    String customerInfo=couponConsumeRecordDTO.getCustomerInfo();
    Double coupon=couponConsumeRecordDTO.getCoupon();

    StringBuilder sb = new StringBuilder();
    sb.append("UPDATE bcuser.consuming_record SET created=created ");
    if(orderId!=null){
      sb.append(" , order_id = :orderId ");
    }
    if(!StringUtil.isEmpty(orderStatus)){
      sb.append(" , order_status = :orderStatus ");
    }
//    if(consumerTime!=null){
//      sb.append(" , consumer_time = :consumerTime ");
//    }
    if(!StringUtil.isEmpty(product)){
      sb.append(" , product = :product ");
    }
    if(sumMoney!=null){
      sb.append(" , sum_money = :sumMoney ");
    }
    if(!StringUtil.isEmpty(customerInfo)){
      sb.append(" , customer_info = :customerInfo ");
    }
    if(coupon!=null){
      sb.append(" , coupon = :coupon ");
    }
    sb.append(" WHERE id = :id AND shop_id = :shopId ");
    Query q = session.createSQLQuery(sb.toString()).setLong("shopId", shopId).setLong("id", id);
    if(orderId!=null){
      q.setLong("orderId",orderId);
    }
    if(!StringUtil.isEmpty(orderStatus)){
      q.setString("orderStatus", orderStatus);
    }
//    if(consumerTime!=null){
//      q.setLong("consumerTime", consumerTime);
//    }
    if(!StringUtil.isEmpty(product)){
      q.setString("product", product);
    }
    if(sumMoney!=null){
      q.setFloat("sumMoney", sumMoney);
    }
    if(!StringUtil.isEmpty(customerInfo)){
      q.setString("customerInfo", customerInfo);
    }
    if(null!=coupon){
      q.setDouble("coupon", coupon);
    }

    return q;
  }

  public static Query getConsumingRecord(Session session , String appUserNo , long dateTime , int count){
    StringBuilder sb = new StringBuilder();
    sb.append("select c.id as id , c.coupon as coupon , c.consumer_time as consumerTime ," +
            " c.receipt_no as receiptNo, c.order_types as orderTypes , c.order_status as orderStatus ," +
            " c.product_id as productId, c.product as product , c.sum_money as sumMoney" +
            "  from consuming_record c where c.app_user_no =:appUserNo AND c.consumer_time <:dateTime order by c.consumer_time desc");
    Query q = session.createSQLQuery(sb.toString()).
              addScalar("id",StandardBasicTypes.LONG).
              addScalar("coupon",StandardBasicTypes.DOUBLE).
              addScalar("consumerTime",StandardBasicTypes.LONG).
              addScalar("receiptNo",StandardBasicTypes.STRING).
              addScalar("orderTypes",StandardBasicTypes.STRING).
              addScalar("orderStatus", StandardBasicTypes.STRING).
              addScalar("productId",StandardBasicTypes.LONG).
              addScalar("product",StandardBasicTypes.STRING).
              addScalar("sumMoney",StandardBasicTypes.DOUBLE).
              setParameter("appUserNo",appUserNo).
              setParameter("dateTime",dateTime).
              setFirstResult(0).
              setMaxResults(count);
    return q;
  }
  public static Query getConsumingRecord(Session session , long consumingId){
    StringBuilder sb = new StringBuilder();
    sb.append("FROM ConsumingRecord WHERE id =:consumingId");
    Query q = session.createQuery(sb.toString()).setParameter("consumingId",consumingId);
    return  q;
  }

  public static Query toOrderListCount (Session session, String customerName , String telNumber, String vehicleNumber,
                                        String orderNumber,String goodsName,String orderStatus) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT  c.receipt_no AS receiptNo , c.admin_status AS adminStatus , c.sum_money as " +
            "sumMoney , c.coupon AS coupon , c.product as product , u.name as userName ,u.mobile as mobile ,v.vehicle_no as vehicleNo " +
            " FROM consuming_record c , app_user u , app_vehicle v " +
            "WHERE c.app_user_no = u.app_user_no AND u.app_user_no = v.app_user_no AND c.order_types = 'APP_ONLINE_ORDER' ");
    if ( customerName != null && !customerName.trim().isEmpty())
      sb.append("AND u.name ="+customerName);
    if (telNumber != null && !telNumber.trim().isEmpty())
      sb.append(" AND u.mobile ="+telNumber);
    if (vehicleNumber != null && !vehicleNumber.trim().isEmpty())
      sb.append(" AND v.vehicle_no =:vehicleNumber");
    if (orderNumber != null && !orderNumber.trim().isEmpty())
      sb.append(" AND c.receipt_no = "+orderNumber );
    if(goodsName != null && !goodsName.trim().isEmpty())
      sb.append(" AND c.product LIKE :goodsName ");
    if (orderStatus != null && !orderStatus.trim().isEmpty())
      sb.append(" AND c.admin_status =:orderStatus");
    sb.append(" GROUP BY c.id ORDER BY c.consumer_time DESC");

    Query q = session.createSQLQuery(sb.toString()).
            addScalar("receiptNo", StandardBasicTypes.STRING).
            addScalar("adminStatus" , StandardBasicTypes.STRING).
            addScalar("sumMoney",StandardBasicTypes.DOUBLE).
            addScalar("coupon" , StandardBasicTypes.DOUBLE).
            addScalar("product", StandardBasicTypes.STRING).
            addScalar("userName",StandardBasicTypes.STRING).
            addScalar("mobile", StandardBasicTypes.LONG).
            addScalar("vehicleNo", StandardBasicTypes.STRING);
    if (vehicleNumber != null && !vehicleNumber.trim().isEmpty())
      q.setParameter("vehicleNumber" , vehicleNumber);
    if(goodsName != null && !goodsName.trim().isEmpty())
      q.setParameter("goodsName","%"+goodsName+"%");
    if (orderStatus != null && !orderStatus.trim().isEmpty())
      q.setParameter("orderStatus",orderStatus);
    return  q;
  }

  public static Query toOrderList(Session session , String customerName , String telNumber, String vehicleNumber,
                                  String orderNumber,String goodsName,String orderStatus , Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT  c.receipt_no AS receiptNo , c.admin_status AS adminStatus , c.sum_money as " +
            "sumMoney , c.coupon AS coupon , c.product as product , u.name as userName ,u.mobile as mobile ,v.vehicle_no as vehicleNo " +
            " FROM consuming_record c , app_user u , app_vehicle v " +
            "WHERE c.app_user_no = u.app_user_no AND u.app_user_no = v.app_user_no AND c.order_types = 'APP_ONLINE_ORDER' ");
    if ( customerName != null && !customerName.trim().isEmpty())
      sb.append("AND u.name ="+customerName);
    if (telNumber != null && !telNumber.trim().isEmpty())
      sb.append(" AND u.mobile ="+telNumber);
    if (vehicleNumber != null && !vehicleNumber.trim().isEmpty())
      sb.append(" AND v.vehicle_no =:vehicleNumber");
    if (orderNumber != null && !orderNumber.trim().isEmpty())
      sb.append(" AND c.receipt_no = "+orderNumber );
    if(goodsName != null && !goodsName.trim().isEmpty())
      sb.append(" AND c.product LIKE :goodsName ");
    if (orderStatus != null && !orderStatus.trim().isEmpty())
      sb.append(" AND c.admin_status =:orderStatus");
    sb.append(" GROUP BY c.id ORDER BY c.consumer_time DESC");

    Query q = session.createSQLQuery(sb.toString()).
              addScalar("receiptNo", StandardBasicTypes.STRING).
              addScalar("adminStatus" , StandardBasicTypes.STRING).
              addScalar("sumMoney",StandardBasicTypes.DOUBLE).
              addScalar("coupon" , StandardBasicTypes.DOUBLE).
              addScalar("product", StandardBasicTypes.STRING).
              addScalar("userName",StandardBasicTypes.STRING).
              addScalar("mobile", StandardBasicTypes.LONG).
              addScalar("vehicleNo", StandardBasicTypes.STRING);
    if (vehicleNumber != null && !vehicleNumber.trim().isEmpty())
      q.setParameter("vehicleNumber" , vehicleNumber);
    if(goodsName != null && !goodsName.trim().isEmpty())
      q.setParameter("goodsName","%"+goodsName+"%");
    if (orderStatus != null && !orderStatus.trim().isEmpty())
      q.setParameter("orderStatus",orderStatus);
      q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    return q;
  }

  /**
   * 更新代金券(coupon)表的余额(balance)字段
   * @param session
   * @param coupon  coupon对象
   * @return
   */
  public static Query updateCouponBalance(Session session ,Coupon coupon){
    Long id=coupon.getId();
    Double balance=coupon.getBalance();
    Query q=session.createSQLQuery("UPDATE coupon SET balance = :balance  WHERE id = :id ")
            .setLong("id",id).setDouble("balance",balance);
    return q;
  }
  public static Query getRecommendPhone (Session session , String appUserNo ){

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT recommendPhone FROM Coupon WHERE appUserNo =:appUserNo");
    Query q = session.createQuery(sb.toString()).setParameter("appUserNo" , appUserNo);
    return q;
  }

  public static Query saveRecommendPhone (Session session , String appUserNo , long phone ,double coupon){

    StringBuilder sb = new StringBuilder();
    sb.append("update Coupon set recommendPhone =:phone ,  balance =:coupon WHERE appUserNo =:appUserNo");
    Query q = session.createQuery(sb.toString()).
              setParameter("phone", phone).
              setParameter("coupon", coupon).
              setParameter("appUserNo", appUserNo);
    return q;
  }

  public static Query getIsShared (Session session , String appUserNo){

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT isShared FROM Coupon WHERE appUserNo =:appUserNo");
    Query q = session.createQuery(sb.toString()).setParameter("appUserNo" , appUserNo);
    return q;
  }


  public static Query saveIsShared(Session session , String appUserNo , int isShared ,double coupon){

    StringBuilder sb = new StringBuilder();
    sb.append("update Coupon set isShared =:isShared , balance =:coupon WHERE appUserNo =:appUserNo");
    Query q = session.createQuery(sb.toString()).
              setParameter("isShared", isShared).
              setParameter("coupon", coupon).
              setParameter("appUserNo", appUserNo);
    return q;
  }

  public static Query saveCoupon(Session session , String appUserNo ,double coupon){

    StringBuilder sb = new StringBuilder();
    sb.append("update Coupon set balance =:coupon WHERE appUserNo =:appUserNo");
    Query q = session.createQuery(sb.toString()).
            setParameter("coupon", coupon).
            setParameter("appUserNo" , appUserNo);
    return q;
  }


  public static Query getAppUserByPhone(Session session , long phone){

    StringBuilder sb = new StringBuilder();
    sb.append("FROM AppUser where mobile =:phone");
    Query q = session.createQuery(sb.toString()).setParameter("phone",String.valueOf(phone));
    return q;
  }

  public static Query updateAdminStatus(Session session , String receiptNo){

    StringBuilder sb = new StringBuilder();
    sb.append("update ConsumingRecord set adminStatus =:adminStatus WHERE receiptNo =:receiptNo");
    Query query = session.createQuery(sb.toString()).setParameter("adminStatus", OrderStatus.ADMIN_ORDER_CONFIRM).setParameter("receiptNo",receiptNo);
    return query;
  }

  public static Query getCouponsByImei(Session session, String imei){
    Query query=session.createQuery("from Coupon c where c.imei =:imei").setString("imei",imei);
    return query;
  }
  /**
   * 获取逾期未处理的空白单据(代金券消费记录)
   * 空白单据的orderTypes = 'APP_ONFIELD_ORDER', orderId为空
   * orderStatus为REPEAL的是已作废的记录
   * @param session
   * @param overdueTime
   * @param start
   * @param size
   * @return
   */
  public static Query getOverdueConsumingRecord(Session session, Long overdueTime, int start, int size){
    StringBuilder sb = new StringBuilder();
    if(null==overdueTime){
      overdueTime=System.currentTimeMillis();
    }
    sb.append("from ConsumingRecord where orderTypes = 'APP_ONFIELD_ORDER' and orderId is null and (orderStatus is null or orderStatus != 'REPEAL') and consumerTime < :overdueTime ");
    Query query = session.createQuery(sb.toString()).setLong("overdueTime", overdueTime).setFirstResult(start).setMaxResults(size);
    return query;
  }

  public static Query wxShareInfo(Session session , String appUserNo){
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT s.name AS company , s.address AS address , s.qq AS qq , s.mobile AS phone , s.email AS email , s.coordinate_lat as lat , s.coordinate_lon as lon " +
            "FROM bcuser.app_user u ,config.shop s" +
            " WHERE u.app_user_no =:appUserNo AND u.registration_shop_id = s.id");
    Query q = session.createSQLQuery(sb.toString()).
            addScalar("company", StandardBasicTypes.STRING).
            addScalar("address" , StandardBasicTypes.STRING).
            addScalar("qq",StandardBasicTypes.STRING).
            addScalar("phone" , StandardBasicTypes.STRING).
            addScalar("email", StandardBasicTypes.STRING).
            addScalar("lat", StandardBasicTypes.STRING).
            addScalar("lon",StandardBasicTypes.STRING).
            setParameter("appUserNo",appUserNo);
    return q;
  }
}

