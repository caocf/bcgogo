package com.bcgogo.txn.service.productThrough;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品出入库打通单据处理 专用service
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-19
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductThroughOrderService implements IProductThroughOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(ProductThroughOrderService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IItemIndexService itemIndexService;
  private IProductService productService;
  private ITxnService txnService;
  private IProductThroughService productThroughService;

  public ITxnService getTxnService() {
    return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
  }

  public IProductService getProductService() {
    return productService == null ? ServiceManager.getService(IProductService.class) : productService;
  }


  public IItemIndexService getItemIndexService() {
    return itemIndexService == null ? ServiceManager.getService(IItemIndexService.class) : itemIndexService;
  }

  public IProductThroughService getProductThroughService() {
    return productThroughService == null ? ServiceManager.getService(IProductThroughService.class) : productThroughService;
  }

  /**
   * 商品库存减少的单据作废处理流程  只接受施工单和销售单 入库退货单  作废
   *
   * @param bcgogoOrderDto
   * @param orderType
   */
  public List<SupplierInventoryDTO> inventoryReduceOrderRepeal(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType) {

    List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
    try {
      if (!(orderType == OrderTypes.SALE || orderType == OrderTypes.REPAIR || orderType == OrderTypes.RETURN)) {
        return supplierInventoryDTOList;
      }

      //归还单据结算时所使用的库存
      SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
      supplierInventoryDTO.setShopId(bcgogoOrderDto.getShopId());
      supplierInventoryDTO.setStorehouseId(bcgogoOrderDto.getStorehouseId());

      OutStorageRelationDTO[] outStorageRelationDTOs = null;

      for (BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()) {
        List<OutStorageRelationDTO> outStorageRelationDTOList = getProductThroughService().getOutStorageRelation(bcgogoOrderDto.getShopId(), bcgogoOrderDto.getId(), OrderTypes.SALE, bcgogoOrderItemDto.getId(), bcgogoOrderItemDto.getProductId());
        if (CollectionUtil.isEmpty(outStorageRelationDTOList)) {
          OutStorageRelationDTO relationDTO = new OutStorageRelationDTO();
          relationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
          relationDTO.setUseRelatedAmount(bcgogoOrderItemDto.getAmount());
          outStorageRelationDTOs = new OutStorageRelationDTO[1];
          outStorageRelationDTOs[0] = relationDTO;
          bcgogoOrderItemDto.setOutStorageRelationDTOs(outStorageRelationDTOs);
        } else {
          outStorageRelationDTOs = outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]);
          bcgogoOrderItemDto.setOutStorageRelationDTOs(outStorageRelationDTOs);
        }
        supplierInventoryDTOList.addAll(getProductThroughService().getSupplierInventoryList(bcgogoOrderItemDto, outStorageRelationDTOs, supplierInventoryDTO));
      }

      for (SupplierInventoryDTO inventoryDTO : supplierInventoryDTOList) {
        inventoryDTO.setChangeAmount(0 - inventoryDTO.getChangeAmount());
      }
    } catch (Exception e) {
      LOG.error("productThroughOrderService.inventoryAddOrderRepeal,repeal error:" + bcgogoOrderDto.toString());
      LOG.error(e.getMessage(), e);
    }
    return supplierInventoryDTOList;
  }

  /**
   * 商品库存增加的单据作废处理流程  只接受 入库单和销售退货单 作废  暂时没有小说退货单作废
   *
   * @param bcgogoOrderDto
   * @param orderType
   */
  public List<SupplierInventoryDTO> inventoryAddOrderRepeal(BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType) {
    List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();

    try {
      if (!(orderType == OrderTypes.INVENTORY || orderType == OrderTypes.SALE_RETURN)) {
        return supplierInventoryDTOList;
      }


      //归还单据结算时所使用的库存
      SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
      supplierInventoryDTO.setShopId(bcgogoOrderDto.getShopId());
      supplierInventoryDTO.setStorehouseId(bcgogoOrderDto.getStorehouseId());
      OutStorageRelationDTO[] outStorageRelationDTOs = null;

      //入库单作废
      if (orderType == OrderTypes.INVENTORY) {
        PurchaseInventoryDTO purchaseInventoryDTO = (PurchaseInventoryDTO) bcgogoOrderDto;

        List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList = getTxnService().getPurchaseInventoryItemByOrderIds(bcgogoOrderDto.getId());
        if (CollectionUtil.isEmpty(purchaseInventoryItemDTOList)) {
          return supplierInventoryDTOList;
        }

        for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryItemDTOList) {
          supplierInventoryDTO.setProductId(purchaseInventoryItemDTO.getProductId());
          supplierInventoryDTO.setSupplierType(OutStorageSupplierType.NATIVE_SUPPLIER);
          supplierInventoryDTO.setChangeAmount(-purchaseInventoryItemDTO.getAmount());
          supplierInventoryDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
          supplierInventoryDTOList.add(supplierInventoryDTO);

          //如果剩余库存 和 入库数量 一样 说明 这个item没有被使用 就不需要再指向新的库存
          if (NumberUtil.doubleVal(purchaseInventoryItemDTO.getRemainAmount()) == purchaseInventoryItemDTO.getAmount()) {
            continue;
          }

          List<OutStorageRelationDTO> outStorageRelationDTOList = getProductThroughService().getOutStorageRelationByRelated(bcgogoOrderDto.getShopId(),
              purchaseInventoryDTO.getId(), OrderTypes.INVENTORY, purchaseInventoryItemDTO.getId(), purchaseInventoryItemDTO.getProductId());

          if (CollectionUtil.isEmpty(outStorageRelationDTOList)) {
            OutStorageRelationDTO relationDTO = new OutStorageRelationDTO();
            relationDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
            relationDTO.setUseRelatedAmount(purchaseInventoryItemDTO.getAmount() - purchaseInventoryItemDTO.getRemainAmount());
            outStorageRelationDTOs = new OutStorageRelationDTO[1];
            outStorageRelationDTOs[0] = relationDTO;
            purchaseInventoryItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOs);
          } else {
            outStorageRelationDTOs = outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]);
            purchaseInventoryItemDTO.setOutStorageRelationDTOs(outStorageRelationDTOs);
          }
        }
      }
    } catch (Exception e) {
      LOG.error("productThroughOrderService.inventoryAddOrderRepeal,repeal error:" + bcgogoOrderDto.toString());
      LOG.error(e.getMessage(), e);
    }
    return supplierInventoryDTOList;
  }


  /**
   * 单据作废时 根据出入库关系记录 回滚每个item的剩余库存
   *
   * @param outStorageRelationDTOs
   * @param writer
   * @throws Exception
   */
  public void updateItemRemainAmount(BcgogoOrderDto bcgogoOrderDto,OutStorageRelationDTO[] outStorageRelationDTOs, TxnWriter writer) throws Exception {
    if (ArrayUtil.isEmpty(outStorageRelationDTOs)) {
      return;
    }
    OrderTypes orderType = null;

    for (OutStorageRelationDTO outStorageRelationDTO : outStorageRelationDTOs) {
      if (outStorageRelationDTO.getSupplierType() == OutStorageSupplierType.UNDEFINED_SUPPLIER ||
          outStorageRelationDTO.getSupplierType() == OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER) {
        continue;
      }
      orderType = outStorageRelationDTO.getRelatedOrderType();
      if (orderType == null) {
        continue;
      }

      //入库单
      if (orderType == OrderTypes.INVENTORY) {

        bcgogoOrderDto.addItemId(OrderTypes.INVENTORY,outStorageRelationDTO.getRelatedItemId());

        PurchaseInventory purchaseInventory = writer.getById(PurchaseInventory.class, outStorageRelationDTO.getRelatedOrderId());

        if (purchaseInventory == null) {
          LOG.error("productThroughOrderService.updateItemRemainAmount,purchaseInventory is null " + outStorageRelationDTO.toString());
          continue;
        }
        if (purchaseInventory.getStatusEnum() == OrderStatus.PURCHASE_INVENTORY_REPEAL) {
          List<OutStorageRelationDTO> outStorageRelationDTOList = getProductThroughService().getOutStorageRelation(purchaseInventory.getShopId(), purchaseInventory.getId(),
              OrderTypes.INVENTORY, outStorageRelationDTO.getOutStorageItemId(), outStorageRelationDTO.getProductId());
          if (CollectionUtil.isEmpty(outStorageRelationDTOList)) {
            continue;
          }
          this.updateItemRemainAmount(bcgogoOrderDto,outStorageRelationDTOList.toArray(new OutStorageRelationDTO[outStorageRelationDTOList.size()]), writer);
        } else {
          PurchaseInventoryItem purchaseInventoryItem = writer.getById(PurchaseInventoryItem.class, outStorageRelationDTO.getOutStorageItemId());

          writer.update(purchaseInventoryItem);
        }
      }

      if (orderType == OrderTypes.INNER_RETURN) {

        bcgogoOrderDto.addItemId(OrderTypes.INNER_RETURN,outStorageRelationDTO.getRelatedItemId());

        InnerReturnItem innerReturnItem = writer.getById(InnerReturnItem.class, outStorageRelationDTO.getOutStorageItemId());
        writer.update(innerReturnItem);
      }

      if (orderType == OrderTypes.INVENTORY_CHECK) {

        bcgogoOrderDto.addItemId(OrderTypes.INNER_RETURN,outStorageRelationDTO.getRelatedItemId());

        InventoryCheckItem inventoryCheckItem = writer.getById(InventoryCheckItem.class, outStorageRelationDTO.getOutStorageItemId());
      }

    }
  }


  public void updateItemRemainAmountByOrder(BcgogoOrderDto bcgogoOrderDto,OrderTypes orderType) throws Exception {

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {

      for (BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()) {
        updateItemRemainAmount(bcgogoOrderDto,bcgogoOrderItemDto.getOutStorageRelationDTOs(), writer);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

}
