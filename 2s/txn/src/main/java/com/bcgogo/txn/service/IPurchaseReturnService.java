package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.txn.dto.PurchaseOrderItemDTO;
import com.bcgogo.txn.dto.PurchaseReturnDTO;
import com.bcgogo.txn.dto.PurchaseReturnItemDTO;
import com.bcgogo.user.dto.SupplierDTO;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Qiuxinyu
 * Date: 12-7-16
 * Time: 上午10:29
 * To change this template use File | Settings | File Templates.
 */
public interface IPurchaseReturnService {
  //从itemIndexDTO中获得合并选中的退货记录
  public List<PurchaseReturnItemDTO> getSelectPurchaseReturnItemDTOs(ItemIndexDTO itemIndexDTO);

  //根据选中的项目生成退货单dto
  public PurchaseReturnDTO createPurchaseReturnDTO(Long shopId, List<PurchaseReturnItemDTO> selectList) throws Exception;

  //根据purchaseReturenId 查到退货单信息
  public void getGoodsReturnInfo(ModelMap model,Long shopId, String purchaseReturnId);

  //将选中的purchaseReturenItem中的数量赋值给查处的相同项目
  public void initAmountForPurchaseReturnItem(List<PurchaseReturnItemDTO> selectList,
                                              List<PurchaseReturnItemDTO> purchaseReturnItemDTOs);

  PurchaseReturnDTO getPurchaseReturnById(long orderId, Long shopId);

  /**
   *  根据产品Id获得供应商信息
   * @param productId    Product_Local_Info 表里的Id
   * @param shopId
   * @return
   */
  public List<SupplierDTO> getSupplierDTOsByProductId(Long productId,Long shopId) ;


  /**
   * 退货单复制
   * @param purchaseReturnDTO
   * @return
   * @throws Exception
   */
  public PurchaseReturnDTO copyPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO) throws Exception;

  /**
   * 退货单作废
   * @param purchaseReturnDTO
   * @param inventorySearchIndexList
   * @return
   * @throws Exception
   */
  public PurchaseReturnDTO repealPurchaseReturn(PurchaseReturnDTO purchaseReturnDTO,List<InventorySearchIndex> inventorySearchIndexList) throws Exception;

  public PurchaseReturnDTO repealPurchaseReturnInTxn(PurchaseReturnDTO purchaseReturnDTO, Long storehouseId) throws Exception;


  PurchaseReturnDTO fillPurchaseReturnItemDTOsDetailInfo(PurchaseReturnDTO purchaseReturnDTO) throws Exception;

  //采购单生成退货单的时候校验
  Result validatePurchaseReturnByPurchaseOrderId(Long shopId,Long purchaseOrderId);

  //采购单生成退货单
  PurchaseReturnDTO createOnlinePurchaseReturnByPurchaseOrderId(Long shopId, Long purchaseOrderId,Set<Long> itemIds) throws Exception;

  PurchaseReturnDTO createPurchaseReturnDTOByProductIds(Long shopId, Long[] productIdArray) throws Exception;

  PurchaseReturnDTO createPurchaseReturnDTOBySupplierId(Long shopId, Long aLong) throws Exception;

  /**
   * 通过id和shopId查询purchaseReturnItemDTO
   *
   * @param id
   * @return
   */
  public PurchaseReturnItemDTO getPurchaseReturnItemDTOById(Long id);

  /**
   * 通过purchaseReturnItemId查询对应的PurchaseOrderItemDTO.
   * 只针对在线退货单！
   * @param id
   * @return
   */
  public PurchaseOrderItemDTO getPurchaseOrderItemDTOByPurchaseReturnItemId(Long id);

  public List<PurchaseReturnDTO> getSimpleProcessingRelatedPurchaseReturnDTOs(Long customerShopId, Long supplierShopId);
}
