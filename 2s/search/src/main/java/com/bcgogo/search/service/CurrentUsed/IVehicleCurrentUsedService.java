package com.bcgogo.search.service.CurrentUsed;

import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.model.CurrentUsedVehicle;
import com.bcgogo.txn.dto.BcgogoOrderDto;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-29
 * Time: 上午9:27
 */
public interface IVehicleCurrentUsedService {

  List<CurrentUsedVehicle> getCurrentUsedVehiclesFromMemory(SearchConditionDTO searchConditionDTO);

  public void currentUsedVehicleSaved(BcgogoOrderDto bcgogoOrderDto);
}
