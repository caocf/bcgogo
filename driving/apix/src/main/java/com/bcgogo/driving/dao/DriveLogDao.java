package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.DriveLog;
import com.bcgogo.pojox.enums.DriveLogStatus;
import com.bcgogo.pojox.util.CollectionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-21
 * Time: 下午2:36
 */
@Repository
public class DriveLogDao extends BaseDao<DriveLog> {

  public DriveLogDao() {
    super(DriveLog.class);
  }

  public DriveLog getDrivingDriveLog(String appUserNo) {
    Session session = this.getSession();
    String sql = "select * from drive_log where status='DRIVING' and app_user_no=:app_user_no";
    Query query = session.createSQLQuery(sql)
      .addEntity(DriveLog.class)
      .setString("app_user_no", appUserNo);
    return (DriveLog) CollectionUtil.getFirst(query.list());

  }

public DriveLog getDriveLogByUuid(String uuid) {
    Session session = this.getSession();
    String sql = "select * from drive_log where  app_drive_log_id=:uuid";
    Query query = session.createSQLQuery(sql)
      .addEntity(DriveLog.class)
      .setString("uuid", uuid);
    return (DriveLog) CollectionUtil.getFirst(query.list());

  }


}
