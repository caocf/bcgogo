package com.bcgogo.txn.service;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.dto.ProductHistoryDTO;
import com.bcgogo.txn.model.ProductHistory;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-12
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
public interface IProductHistoryService {

  void saveProductHistoryForOrder(Long shopId,BcgogoOrderDto bcgogoOrderDto);

  ProductHistoryDTO getProductHistoryById(Long productHistoryId, Long shopId);

  Map<Long, ProductHistoryDTO> getOrSaveProductHistoryByLocalInfoId(Long shopId, Long... productLocalInfoIds);

  /**
   * 通过产品id查询对应产品历史信息
   *
   * @param shopId
   * @param productIds
   * @return
   */
  public Map<Long, List<ProductHistoryDTO>> getProductHistoryDTOsByProductId(Long shopId, Long... productIds);

  Map<Long,ProductHistoryDTO> getProductHistoryDTOMapByProductHistoryIds(Set<Long> productHistoryIds);

  boolean compareProductSameWithHistory(Map<Long,Long> localInfoIdAndHistoryIdMap, Long shopId);

  boolean compareProductSameWithHistory(Long productLocalInfoId, Long productHistoryId, Long shopId);

  boolean compareSupplierProductSameWithCustomerProductHistory(Map<Long,Long> productLocalInfoIdAndCustomerHistoryIdMap, Long supplierShopId,Long customerShopId);

  boolean compareSupplierProductSameWithCustomerProductHistory(Long supplierShopId,Long supplierProductLocalInfoId,Long customerShopId, Long customerProductHistoryId);

  void batchSaveProductHistory(Map<Long, ProductHistory> productHistoryMap,TxnWriter writer) throws Exception;

  /**
   * 商品快照是否是最新的商品
   * <p/>
   * 比较的字段列表
   * <p/>
   * <pre>
   * 1.产品基本属性：品名、品牌、型号、规格、车辆品牌、车辆型号、商品编码
   *   商品标准分类
   *   商品详细说明,商品图片
   *   商品种类
   * 2.价格、采购价、批发价
   * 3.促销信息
   * </pre>
   *
   * @param productHistoryDTO
   * @param productDTO
   * @return
   */
  public boolean isProductSnapShotBeLastVersionProductByKeyFields(ProductHistoryDTO productHistoryDTO, ProductDTO productDTO);

}
