package com.bcgogo.user.model;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.YesNo;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.service.GenericReaderDao;
import com.bcgogo.user.model.app.AppUserShopVehicle;
import com.bcgogo.user.model.app.AppVehicle;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/12/11
 * Time: 9:55 PM
 */
public class UserReader extends GenericReaderDao {

  public UserReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Long getDefaultOBDSellerShopId(String appUserNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDefaultOBDSellerShopId(session, appUserNo);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long getOBDSellerShopIdByVehicleId(Long vehicleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOBDSellerShopIdByVehicleId(session, vehicleId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public AppVehicle getAppVehicleDetailByVehicleNo(String appUserNo, String vehicleNo, YesNo isDefault) {
    if (StringUtil.isEmpty(appUserNo) || (StringUtil.isEmpty(vehicleNo) && isDefault == null)) return null;
    Session session = this.getSession();
    try {
      Query q = SQL.getAppVehicleDetailByVehicleNo(session, appUserNo, vehicleNo, isDefault);
      return (AppVehicle) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<CustomerVehicle> getCustomerVehicleIdByVehicleIds(Set<Long> vehicleIds) {
    if (CollectionUtil.isEmpty(vehicleIds)) return new ArrayList<CustomerVehicle>();
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomerVehicleIdByVehicleIds(session, vehicleIds);
      return (List<CustomerVehicle>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Contact> getContactsByCustomerMobile(Long shopId, String keyword, int limit) {
    if (StringUtils.isEmpty(keyword)) {
      return new ArrayList<Contact>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getContactsByCustomerMobile(session, shopId, keyword, limit);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<Vehicle> getVehiclesByCustomerMobile(String mobile, String vehicleNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehiclesByCustomerMobile(session, mobile, vehicleNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, Long> getVehicleIdBindingShopIdMap(String userNo, Long... vehicleId) {
    Session session = this.getSession();
    try {
      Map<Long, Long> map = new HashMap<Long, Long>();
      Query q = SQL.getBindingShopId(session, userNo, vehicleId);
      List<AppUserShopVehicle> list = q.list();
      for (AppUserShopVehicle entity : list) {
        map.put(entity.getAppVehicleId(), entity.getShopId());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Set<Long> getAppUserBindingShopIds(String userNo) {
    Session session = this.getSession();
    try {
      Set<Long> shopIds = new HashSet<Long>();
      Query q = SQL.getBindingShopId(session, userNo);
      List<AppUserShopVehicle> list = q.list();
      for (AppUserShopVehicle entity : list) {
        shopIds.add(entity.getShopId());
      }
      return shopIds;
    } finally {
      release(session);
    }
  }

  public List<AppUserShopVehicle> getAppUserShopVehicleByUserNo(String userNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBindingShopId(session, userNo, null);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getVehicleOBDMileageByStartVehicleId(Long startVehicleId, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleOBDMileageByStartVehicleId(session, startVehicleId, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<String> getVehicleImeiByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleImeiByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countGsmAppVehicle() {
    Session session = getSession();
    try {
      Query q = SQL.countGsmAppVehicle(session);
      return Integer.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<AppVehicle> getGsmAppVehicle(Pager pager) {
    Session session = getSession();
    try {
      Query q =SQL.getGsmAppVehicle(session, pager);
      return (List<AppVehicle>)q.list();
    } finally {
      release(session);
    }
  }

  public List<Vehicle> getVehicleByCondition(VehicleSearchConditionDTO vehicleSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getVehicleByCondition(session, vehicleSearchConditionDTO);
      return q.list();
    } finally {
      release(session);
    }
  }

}
