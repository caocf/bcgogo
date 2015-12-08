package com.bcgogo.txn.service.ShopRelation;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-26
 * Time: 下午7:10
 */
public interface IShopRelationService {

  /**
   * 客户收藏供应商店铺
   */
  SupplierDTO collectSupplierShop(ShopDTO customerShopDTO,ShopDTO supplierShopDTO) throws Exception;

  /**
    * 供应商收藏客户店铺
    */
  CustomerDTO collectCustomerShop(ShopDTO supplierShopDTO,ShopDTO customerShopDTO) throws Exception;
}
