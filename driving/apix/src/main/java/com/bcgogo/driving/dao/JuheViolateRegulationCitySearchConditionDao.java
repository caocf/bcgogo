package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.JuheViolateRegulationCitySearchCondition;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-13
 * Time: 下午5:21
 */
@Repository
public class JuheViolateRegulationCitySearchConditionDao extends BaseDao<JuheViolateRegulationCitySearchCondition> {
  public JuheViolateRegulationCitySearchConditionDao() {
    super(JuheViolateRegulationCitySearchCondition.class);
  }

  public JuheViolateRegulationCitySearchCondition getJuheViolateRegulationCitySearchConditionByCityName(String cityName) {
    Session session = this.getSession();
    StringBuilder sb = new StringBuilder();
    sb.append("from JuheViolateRegulationCitySearchCondition where cityName =:cityName ");
    Query query = session.createQuery(sb.toString())
      .setParameter("cityName", cityName);
    return (JuheViolateRegulationCitySearchCondition) query.uniqueResult();
  }


}
