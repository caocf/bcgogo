package com.bcgogo.api;

import com.bcgogo.enums.app.OBDSimType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by XinyuQiu on 14-7-9.
 */
public class SimMobileSuggestion {
  private String queryMobile;
  private String scene;
  private Set<OBDSimType> obdSimTypes ;
  private int start = 0;
  private int limit = 10;

  public void generateSearchInfo(){
    if(StringUtils.isNotBlank(scene)){
      if("ALL".equals(scene)){
        obdSimTypes = new HashSet<OBDSimType>();
        obdSimTypes.add(OBDSimType.SINGLE_SIM);
        obdSimTypes.add(OBDSimType.COMBINE_GSM_OBD_SIM);
      }else if("SINGLE_SIM".equals(scene)){
        obdSimTypes = new HashSet<OBDSimType>();
        obdSimTypes.add(OBDSimType.SINGLE_SIM);
      }
    }
    if(CollectionUtils.isEmpty(obdSimTypes)){
      obdSimTypes = new HashSet<OBDSimType>();
      obdSimTypes.add(OBDSimType.SINGLE_SIM);
      obdSimTypes.add(OBDSimType.COMBINE_GSM_OBD_SIM);
    }
  }

  public String getQueryMobile() {
    return queryMobile;
  }

  public void setQueryMobile(String queryMobile) {
    this.queryMobile = queryMobile;
  }

  public String getScene() {
    return scene;
  }

  public void setScene(String scene) {
    this.scene = scene;
  }

  public Set<OBDSimType> getObdSimTypes() {
    return obdSimTypes;
  }

  public void setObdSimTypes(Set<OBDSimType> obdSimTypes) {
    this.obdSimTypes = obdSimTypes;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
