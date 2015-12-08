package com.bcgogo.search.dto;

import com.bcgogo.user.dto.VehicleDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-28
 * Time: 下午11:49
 * Vehicle 搜索出来的结果集
 */
public class VehicleSearchResultDTO {
  //customer
  private List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();
  private long numFound;//total Size
  private Map<String, Long> statCounts;
  private Map<String, Long> statNotNullCounts;
  private Map<String, Double> statAmounts;

  public Map<String, Long> getStatNotNullCounts() {
    return statNotNullCounts;
  }

  public void setStatNotNullCounts(Map<String, Long> statNotNullCounts) {
    this.statNotNullCounts = statNotNullCounts;
  }

  public List<VehicleDTO> getVehicleDTOList() {
    return vehicleDTOList;
  }

  public void setVehicleDTOList(List<VehicleDTO> vehicleDTOList) {
    this.vehicleDTOList = vehicleDTOList;
  }

  public Map<String, Long> getStatCounts() {
    return statCounts;
  }

  public void setStatCounts(Map<String, Long> statCounts) {
    this.statCounts = statCounts;
  }

  public Map<String, Double> getStatAmounts() {
    return statAmounts;
  }

  public void setStatAmounts(Map<String, Double> statAmounts) {
    this.statAmounts = statAmounts;
  }

  public long getNumFound() {
    return numFound;
  }

  public void setNumFound(long numFound) {
    this.numFound = numFound;
  }
}