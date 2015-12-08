package com.bcgogo.driving.service.impl;


import com.bcgogo.driving.dao.JuheViolateRegulationCitySearchConditionDao;
import com.bcgogo.driving.model.JuheViolateRegulationCitySearchCondition;
import com.bcgogo.driving.service.IJuheService;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-10-24
 * Time: 下午3:08
 */
@Component
public class JuheService implements IJuheService {
  private static final Logger LOG = LoggerFactory.getLogger(JuheService.class);

  private static final String JUHE_RESULT_REASON = "查询成功";
  private static final String JUHE_RESULT_CODE = "200";

  @Autowired
  private JuheViolateRegulationCitySearchConditionDao searchConditionDao;


  @Override
  public JuheViolateRegulationCitySearchCondition getJuheViolateRegulationCitySearchConditionByCityName(String cityName) {
    return searchConditionDao.getJuheViolateRegulationCitySearchConditionByCityName(cityName);
  }

  public static void test(){

  }

}
