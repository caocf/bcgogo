package com.bcgogo.vehicle.evalute.car360;

import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleSeriesDTO;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-14
 * Time: 上午9:47
 */
public class Car360Series {
  private String series_id;
  private String series_name;
  private String series_group_name;

   public StandardVehicleSeriesDTO toStandardVehicleSeriesDTO(){
    StandardVehicleSeriesDTO seriesDTO=new StandardVehicleSeriesDTO();
    seriesDTO.setId(NumberUtil.longValue(getSeries_id()));
    seriesDTO.setName(getSeries_name());
    seriesDTO.setGroupName(getSeries_group_name());
    return seriesDTO;
  }

  public String getSeries_id() {
    return series_id;
  }

  public void setSeries_id(String series_id) {
    this.series_id = series_id;
  }

  public String getSeries_name() {
    return series_name;
  }

  public void setSeries_name(String series_name) {
    this.series_name = series_name;
  }

  public String getSeries_group_name() {
    return series_group_name;
  }

  public void setSeries_group_name(String series_group_name) {
    this.series_group_name = series_group_name;
  }
}
