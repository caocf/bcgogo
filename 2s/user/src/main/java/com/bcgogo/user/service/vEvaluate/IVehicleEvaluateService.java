package com.bcgogo.user.service.vEvaluate;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleSeriesDTO;
import com.bcgogo.vehicle.evalute.EvaluateRecordDTO;
import com.bcgogo.vehicle.evalute.EvaluateResult;
import com.bcgogo.vehicle.evalute.car360.Car360EvaluateCondition;

import java.util.List;

/**
 * 车价评估
 * Author: ndong
 * Date: 14-11-5
 * Time: 上午10:48
 */
public interface IVehicleEvaluateService {

  String getCarList();

  String getCarDetail();

  List<AreaDTO> getAreaDTOByNo(Long parentNo);

  List<StandardVehicleBrandDTO> getVehicleBrandDTOs();

  EvaluateResult validateEvaluateCondition(Car360EvaluateCondition condition);

  List<StandardVehicleModelDTO> getVehicleModelDTOs(String seriesId);

  List<StandardVehicleSeriesDTO> getVehicleSeriesDTOs(String brandId);

  public <T extends Result> T evaluate(Car360EvaluateCondition condition);

  void saveOrUpdateEvaluateRecord(EvaluateRecordDTO recordDTO);

  EvaluateRecordDTO getEvaluateRecordDTOById(Long id);

  EvaluateRecordDTO getLastEvaluateRecordDTOByVehicleNo(String vehicleNo);

//  boolean initAreaDTO();

}
