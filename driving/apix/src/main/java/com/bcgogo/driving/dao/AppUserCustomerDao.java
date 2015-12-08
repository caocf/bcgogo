package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.AppUserCustomer;
import com.bcgogo.driving.model.AppVehicle;
import com.bcgogo.pojox.util.CollectionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-11-26
 * Time: 10:21
 */
@Repository
public class AppUserCustomerDao extends BaseDao<AppUserCustomer> {
  public AppUserCustomerDao() {
    super(AppUserCustomer.class);
  }

  public AppUserCustomer getAppUserCustomerByAppUserNo(String appUserNo){
       Session session = this.getSession();
       String sql = "select * from app_user_customer where  app_user_no=:app_user_no";
       Query query = session.createSQLQuery(sql)
         .addEntity(AppUserCustomer.class)
         .setString("app_user_no", appUserNo);
       return (AppUserCustomer) CollectionUtil.getFirst(query.list());
  }

}
