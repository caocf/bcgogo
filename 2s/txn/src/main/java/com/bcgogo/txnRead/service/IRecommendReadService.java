package com.bcgogo.txnRead.service;

import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.recommend.PreBuyOrderItemRecommendDTO;
import com.bcgogo.txn.dto.recommend.ProductRecommendDTO;
import com.bcgogo.txn.dto.recommend.ShopRecommendDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:17
 * To change this template use File | Settings | File Templates.
 */
public interface IRecommendReadService {
  List<PreBuyOrderItemDTO> getValidPreBuyOrderItemDTOByShopId(Long shopId,BusinessChanceType... businessChanceType) throws Exception;

  /**
   * 获取上一月某个店铺销量前十的商品
   * @param shopId
   * @return
   */
  public List<ProductDTO> getLastMonthTopTenSalesByShopId(Long shopId);


}
