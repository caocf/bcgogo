package com.bcgogo.user.service.intenernalVehicle;

import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.user.dto.ShopInternalVehicleDTO;
import com.bcgogo.user.dto.ShopInternalVehicleDriveStatDTO;
import com.bcgogo.user.dto.ShopInternalVehicleGroupDTO;
import com.bcgogo.user.dto.ShopInternalVehicleRequestDTO;

import java.util.List;

/**
 * Created by XinyuQiu on 14-12-11.
 */
public interface IInternalVehicleService {
  Result saveOrUpdateShopInternalVehicles(ShopInternalVehicleGroupDTO shopInternalVehicleGroupDTO);

  int countShopInternalVehicleGroupByShopId();

  List<ShopInternalVehicleGroupDTO> getShopInternalVehicleGroupDTOs(Pager pager);

  List<ShopInternalVehicleDTO> getQueryInternalVehicleNo(Long shopId, String vehicleNo);

  int countShopDriveLogStat(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO);

  void generateSearchInfo(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO);

  List<ShopInternalVehicleDriveStatDTO> getShopDriveLogStat(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO);

  int countShopDriveLog(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO);

  List<DriveLogDTO> getShopDriveLogDTOs(ShopInternalVehicleRequestDTO shopInternalVehicleRequestDTO);


}
