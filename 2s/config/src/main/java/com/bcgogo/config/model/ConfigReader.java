package com.bcgogo.config.model;

import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.service.GenericReaderDao;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.StringUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.*;


public class ConfigReader extends GenericReaderDao {

  public ConfigReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Set<Long> getShopProvinceAreaNoGroupByAreaId() {
    Session session = getSession();
    try {
      Query q = SQL.getShopProvinceAreaNoGroupByAreaId(session);
      return new HashSet<Long>(q.list());
    } finally {
      release(session);
    }
  }

  public Set<Long> getShopCityAreaNoGroupByAreaId(Long provinceId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopCityAreaNoGroupByAreaId(session, provinceId);
      return new HashSet<Long>(q.list());
    } finally {
      release(session);
    }
  }


  public Set<String> getJuheCityCodeByBaiduCityCode(Integer[] baiduCityCodes) {
    Session session = getSession();
    try {
      Query q = SQL.getJuheCityCodeByBaiduCityCode(session, baiduCityCodes);
      return new HashSet<String>(q.list());
    } finally {
      release(session);
    }
  }

  public Set<Long> getAreaNoByJuheCityCode(String... juheCityCodes) {
    Session session = getSession();
    try {
      Query q = SQL.getAreaNoByJuheCityCode(session, juheCityCodes);
      return new HashSet<Long>(q.list());
    } finally {
      release(session);
    }
  }

  public List<JuheViolateRegulationCitySearchCondition> getJuheViolateRegulationCitySearchCondition(String[] juheCityCodes) {
    if (ArrayUtil.isEmpty(juheCityCodes)) return new ArrayList<JuheViolateRegulationCitySearchCondition>();
    Session session = getSession();
    try {
      Query q = SQL.getJuheViolateRegulationCitySearchCondition(session, juheCityCodes);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Set<String> getActiveJuheArea() {
    Session session = getSession();
    try {
      Query q = SQL.getActiveJuheArea(session);
      return new HashSet<String>(q.list());
    } finally {
      release(session);
    }
  }

  public List<JuheViolateRegulationCitySearchCondition> getActiveJuheViolateRegulationCitySearchCondition() {
    Session session = getSession();
    try {
      Query q = SQL.getActiveJuheViolateRegulationCitySearchCondition(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Area> getAreaListByJuheCityCode(Set<String> cityCodes) {
    Session session = getSession();
    try {
      Query q = SQL.getAreaListByJuheCityCode(session, cityCodes);
      return (List<Area>) q.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleViolateRegulationQueryRecord> getVehicleViolateRegulationQueryRecord(String city,String vehicleNo,Long recordDate,String resultCode) {
    Session session = getSession();
    try {
      Query q = SQL.getVehicleViolateRegulationQueryRecord(session,city,vehicleNo,recordDate,resultCode);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleViolateRegulationRecord> getVehicleViolateRegulationRecord(String city, String vehicleNo, Long recordDate) {
    Session session = getSession();
    try {
      Query q = SQL.getVehicleViolateRegulationRecord(session, city, vehicleNo, recordDate);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<JuheViolateRegulationCitySearchCondition> getJuheViolateRegulationCitySearchCondition(String juheCityCode, JuheStatus status) {
    if (StringUtil.isEmpty(juheCityCode) || status == null)
      return new ArrayList<JuheViolateRegulationCitySearchCondition>();
    Session session = getSession();
    try {
      Query q = SQL.getJuheViolateRegulationCitySearchCondition(session, juheCityCode, status);
      return q.list();
    } finally {
      release(session);
    }
  }

  public JuheViolateRegulationCitySearchCondition getJuheViolateRegulationCitySearchConditionByCityName(String cityName) {
    Session session = this.getSession();
    try {
      Query query = SQL.getJuheViolateRegulationCitySearchConditionByCityName(session, cityName);
      return (JuheViolateRegulationCitySearchCondition) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public BaseStation findStationByMncAndLacAndCi(Map map){
    Session session = this.getSession();
    try{
      Query query = SQL.findStationByMncAndLacAndCi(session,map);
      return (BaseStation) query.uniqueResult();
    }finally {
      release(session);
    }
  }

}
