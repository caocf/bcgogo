package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.Vehicle;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-11-26
 * Time: 09:54
 */
@Repository
public class VehicleDao extends BaseDao<Vehicle> {

  public VehicleDao() {
     super(Vehicle.class);
   }



}
