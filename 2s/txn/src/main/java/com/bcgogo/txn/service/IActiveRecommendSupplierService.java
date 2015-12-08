package com.bcgogo.txn.service;

import com.bcgogo.enums.Product.RecommendSupplierType;
import com.bcgogo.txn.dto.ActiveRecommendSupplierDTO;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-5-10
 * Time: 上午9:32
 * 供应商主动推荐
 */
public interface IActiveRecommendSupplierService {
  /**
   * 根据商品Id获得推荐的供应商
   *
   *
   * @param productId     long
   * @param shopVersionId
   *@param shopId        long
   * @param comparePrice  Double
   * @param isRepairOrder Boolean    @return Map<推荐供应商类型,供应商>
   */
  Map<RecommendSupplierType, ActiveRecommendSupplierDTO> obtainActiveRecommendSupplierByProductId(Long productId, Long shopVersionId, Long shopId, Double comparePrice, Boolean isRepairOrder) throws Exception;

  /**
   *
   * @param shopId                   long
   * @param isRepairOrder            Boolean
   * @param productIdAndComparePrice Map<Long, Double>  productId,comparePrice
   * @param shopVersionId
   * @return productId, Map<推荐供应商类型,供应商>
   * @throws Exception
   */
  Map<Long, Map<RecommendSupplierType, ActiveRecommendSupplierDTO>> obtainActiveRecommendSuppliersByProductIds(Long shopId, Boolean isRepairOrder, Map<Long, Double> productIdAndComparePrice, Long shopVersionId) throws Exception;

}
