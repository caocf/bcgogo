package com.bcgogo.api;

import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-31
 * Time: 下午2:12
 */
public class ApiArea {
  private Long id;//数据库中的 no
  private String name;
  private Integer cityCode;//百度cityCode
  private String juheCityCode;
  private JuheViolateRegulationCitySearchConditionDTO juheViolateRegulationCitySearchCondition;
  private JuheStatus juheStatus;
  private List<ApiArea> children = new ArrayList<ApiArea>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }

  public List<ApiArea> getChildren() {
    return children;
  }

  public void setChildren(List<ApiArea> children) {
    this.children = children;
  }

  public JuheStatus getJuheStatus() {
    return juheStatus;
  }

  public void setJuheStatus(JuheStatus juheStatus) {
    this.juheStatus = juheStatus;
  }

  public JuheViolateRegulationCitySearchConditionDTO getJuheViolateRegulationCitySearchCondition() {
    return juheViolateRegulationCitySearchCondition;
  }

  public void setJuheViolateRegulationCitySearchCondition(JuheViolateRegulationCitySearchConditionDTO juheViolateRegulationCitySearchCondition) {
    this.juheViolateRegulationCitySearchCondition = juheViolateRegulationCitySearchCondition;
  }

  public ApiMirrorArea toApiMirrorArea() {
    ApiMirrorArea mirrorArea = new ApiMirrorArea();
    mirrorArea.setName(this.getName());
    mirrorArea.setJuheCityCode(this.getJuheCityCode());
    if (CollectionUtil.isNotEmpty(children)) {
      List<ApiMirrorArea> mirrorAreas = new ArrayList<ApiMirrorArea>();
      for (ApiArea apiArea : children) {
        ApiMirrorArea cMirrorArea = new ApiMirrorArea();
        cMirrorArea.setName(apiArea.getName());
        cMirrorArea.setJuheCityCode(apiArea.getJuheCityCode());
        mirrorAreas.add(cMirrorArea);
      }
      mirrorArea.setChildren(mirrorAreas);
    }
    return mirrorArea;
  }

}
