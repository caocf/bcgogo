package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.AppVehicle;
import com.bcgogo.pojox.util.CollectionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-21
 * Time: 下午4:52
 */
@Repository
public class AppVehicleDao extends BaseDao<AppVehicle> {
  public AppVehicleDao() {
    super(AppVehicle.class);
  }

  public AppVehicle getAppVehicleByAppUserNo(String appUserNo) {
    Session session = this.getSession();
    String sql = "select * from app_vehicle where  app_user_no=:app_user_no";
    Query query = session.createSQLQuery(sql)
      .addEntity(AppVehicle.class)
      .setString("app_user_no", appUserNo);
    return (AppVehicle) CollectionUtil.getFirst(query.list());
  }
}
