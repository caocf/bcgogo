package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.DriveLog;
import com.bcgogo.driving.model.DriveLogPlaceNote;
import com.bcgogo.pojox.util.CollectionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-21
 * Time: 下午4:24
 */
@Repository
public class DriveLogPlaceNoteDao extends BaseDao<DriveLogPlaceNote> {

  public DriveLogPlaceNoteDao() {
    super(DriveLogPlaceNote.class);
  }

  public DriveLogPlaceNote getDriveLogPlaceNoteByDriveLogId(Long driveLogId){
    Session session = this.getSession();
          String sql="select * from drive_log_place_note where drive_log_id=:drive_log_id";
        Query query = session.createSQLQuery(sql)
        .addEntity(DriveLogPlaceNote.class)
        .setLong("drive_log_id", driveLogId);
      return (DriveLogPlaceNote) CollectionUtil.getFirst(query.list());

  }

}
