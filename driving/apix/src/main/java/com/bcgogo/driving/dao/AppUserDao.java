package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.AppUser;
import com.bcgogo.pojox.enums.AppUserStatus;
import com.bcgogo.pojox.enums.ObdUserVehicleStatus;
import com.bcgogo.pojox.enums.app.AppUserType;
import com.bcgogo.pojox.enums.app.OBDStatus;
import com.bcgogo.pojox.util.CollectionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-21
 * Time: 下午3:23
 */
@Repository
public class AppUserDao extends BaseDao<AppUser> {

  public AppUserDao() {
    super(AppUser.class);
  }

  public AppUser getAppUserByImei(String imei) {
    Session session = this.getSession();
    String sql = "select u.* from app_user u " +
      "join obd_user_vehicle v on u.app_user_no=v.app_user_no " +
      "join obd o on v.obd_id=o.id " +
      "where u.status =:uStatus and v.status =:vStatus and o.imei =:imei and o.obd_status in (:obdStatus)";
    Query query = session.createSQLQuery(sql)
      .addEntity(AppUser.class)
      .setString("uStatus", AppUserStatus.active.name())
      .setString("vStatus", ObdUserVehicleStatus.BUNDLING.name())
      .setString("imei", imei)
      .setParameterList("obdStatus", OBDStatus.EnabledStatusStrArr);
    return (AppUser) query.uniqueResult();
  }

}
