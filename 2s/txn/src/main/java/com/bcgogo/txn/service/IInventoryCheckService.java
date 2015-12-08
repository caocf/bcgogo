package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.InventoryCheckDTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-21
 * Time: 下午3:27
 * To change this template use File | Settings | File Templates.
 */
public interface IInventoryCheckService {

  public InventoryCheckDTO saveInventoryCheckOrder(InventoryCheckDTO inventoryCheckOrderDTO)throws Exception;

  public InventoryCheckDTO saveInventoryCheckOrderWithoutUpdateInventoryInfo(InventoryCheckDTO inventoryCheckOrderDTO);

  InventoryCheckDTO getInventoryCheckById(Long shopId,Long inventoryCheckId) throws Exception;

}
