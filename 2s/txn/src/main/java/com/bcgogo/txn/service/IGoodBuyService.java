package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.PurchaseOrderItemDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.model.PurchaseOrderItem;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-11-14
 * Time: 上午10:44
 * To change this template use File | Settings | File Templates.
 */
public interface IGoodBuyService {

	void setLocalInfoWithProductMapping(PurchaseOrderDTO purchaseOrderDTO) throws Exception;

	SupplierDTO handleSupplierForPurchase(PurchaseOrderDTO purchaseOrderDTO)throws Exception;

	void updateSupplierAfterPurchaseOrder(SupplierDTO supplierDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception;

	Result verifyPurchaseModify(PurchaseOrderDTO purchaseOrderDTO )throws Exception;

	void handleProductForPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO)throws Exception;

	void repealPurchaseSaleOrderDTO(SalesOrderDTO salesOrderDTO)throws Exception;

	PurchaseOrderDTO getSimplePurchaseOrderDTO(Long shopId,Long purchaseOrderId);

  /**
   * 同步供应商上架量
   * @param purchaseOrderDTO
   */
	void synSupplierInSalesAmount(PurchaseOrderDTO purchaseOrderDTO);

  Result validateCopy(Long id, Long shopId);

  /**
   * 通过id和shopId获取purchaseOrderItem
   *
   * @param id
   * @return
   */
  public PurchaseOrderItemDTO getPurchaseOrderItemById(Long id);

  //没有供应商的通过供应商店铺生成供应商
  void createOnlineSupplier(PurchaseOrderDTO purchaseOrderDTO) throws Exception;

 public List<PurchaseOrderDTO> getSimpleProcessingRelatedPurchaseOrders(Long customerShopId, Long supplierShopId);
}
