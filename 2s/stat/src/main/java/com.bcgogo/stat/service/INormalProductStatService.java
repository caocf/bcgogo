package com.bcgogo.stat.service;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.product.ProductCategory.NormalProductStatSearchResult;
import com.bcgogo.product.dto.NormalProductDTO;
import com.bcgogo.txn.dto.NormalProductInventoryStatDTO;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;

import java.util.List;
import java.util.Map;

/**
 * 后台CRM采购分析统计专用
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-2
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public interface INormalProductStatService {
  /**
   * 根据标准产品id和入库记录统计采购数据
   * @param shopId
   * @param normalProductId
   * @param purchaseInventoryItemDTOList
   * @return
   */
  public Map<NormalProductStatType,NormalProductInventoryStatDTO> getNormalProductInventoryStatByTime(Long shopId, Long normalProductId, List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList);

  /**
   * 获得某个店铺采购统计数据
   * @param normalProductId
   * @param shopId
   * @return
   * @throws Exception
   */
  public Map<NormalProductStatType,NormalProductInventoryStatDTO> countStatDateByNormalProductId(Long normalProductId,Long shopId) throws Exception;

  /**
   * 根据标准产品组 获得采购统计数据
   * @param shopIds
   * @param normalProductIds
   * @param normalProductStatType
   * @param pager
   * @return
   */
  public NormalProductStatSearchResult getStatDateByCondition(Long[] shopIds,Long[] normalProductIds,NormalProductStatType normalProductStatType,Pager pager,Map<Long,NormalProductDTO> normalProductDTOMap);

  public NormalProductStatSearchResult getStatDetailByCondition(Long[] shopIds, Long normalProductIds, NormalProductStatType normalProductStatType, Pager pager) throws Exception;

}
