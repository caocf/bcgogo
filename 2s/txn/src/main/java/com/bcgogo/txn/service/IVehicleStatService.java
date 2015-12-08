package com.bcgogo.txn.service;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.stat.dto.VehicleServeMonthStatDTO;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.txn.dto.StatementAccountOrderDTO;
import com.bcgogo.txn.model.VehicleServeMonthStat;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-13
 * Time: 上午11:17
 */
public interface IVehicleStatService {
  void vehicleServeStat(BcgogoOrderDto bcgogoOrderDto, boolean isRepeal);

  List<VehicleServeMonthStatDTO> queryTopVehicleServeMonthStat(Long shopId, int year, int month, boolean allYear, int topLimit);

  int queryVehicleServeTotal(Long shopId, int year, int month, boolean allYear);

  List<VehicleServeMonthStat> getVehicleServeMonthStatByBrandModel(Long shopId, String brand, String model);

  List<VehicleServeMonthStat> vehicleServeMonthStatForVehicleInRange(Long shopId, Long vehicleId, long begin, long end);

  void batchSaveOrUpdateVehicleServeMonthStat(List<VehicleServeMonthStat> stats);

  VehicleServeMonthStat getVehicleServeMonthStatByBrandModelYearMonth(Long shopId, String oldBrand, String oldModel, int year, int month);

  /**
   * 客户车辆消费统计
   *
   * @param bcgogoOrderDto
   * @param orderType
   * @param isRepeal
   */
  public void customerVehicleConsumeStat(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, boolean isRepeal,ReceivableDTO receivableDTO);

}
