package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.OrderUtil;
import com.bcgogo.utils.UnitUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: QiuXinYu
 * Date: 12-7-16
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PurchaseReturnService implements IPurchaseReturnService {
  private static final Logger LOG = LoggerFactory.getLogger(PurchaseReturnService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;
  private IProductInStorageService productInStorageService;
  public IProductInStorageService getProductInStorageService() {
    return productInStorageService == null ? ServiceManager.getService(IProductInStorageService.class) : productInStorageService;
  }

  @Override
  public List<PurchaseReturnItemDTO> getSelectPurchaseReturnItemDTOs(ItemIndexDTO itemIndexDTO) {
    if (itemIndexDTO == null) {
      return new ArrayList<PurchaseReturnItemDTO>();
    }
    PurchaseReturnItemDTO[] lastSelectItems = itemIndexDTO.getSelectItemDTOs();        //之前选中的item
    List<PurchaseReturnItemDTO> selectList = new ArrayList<PurchaseReturnItemDTO>();
    List<PurchaseReturnItemDTO> returnItemDTOList = new ArrayList<PurchaseReturnItemDTO>();
    if (itemIndexDTO != null) {
      PurchaseReturnItemDTO[] purchaseReturnItemDTOs = itemIndexDTO.getItemDTOs();       //当前页面选中的item
      //1. 将当前页面选中的放入selectList
      if (purchaseReturnItemDTOs != null && purchaseReturnItemDTOs.length > 0) {
        for (int i = 0; i < purchaseReturnItemDTOs.length; i++) {
          if (StringUtils.isNotBlank(purchaseReturnItemDTOs[i].getCheckId())) {
            selectList.add(purchaseReturnItemDTOs[i]);
          }
        }
      }
      //2.将以前选中的添加到selectList中去
      boolean isSelect = false;
      if (lastSelectItems != null && lastSelectItems.length > 0) {
        for (PurchaseReturnItemDTO purchaseReturnItemDTO : lastSelectItems) {
          isSelect = false;
          for (PurchaseReturnItemDTO selectItem : selectList) {
            if (purchaseReturnItemDTO.getCheckId().equals(selectItem.getCheckId())) {
              isSelect = true;
              break;
            }
          }
          if (!isSelect) {
            selectList.add(purchaseReturnItemDTO);
          }
        }
      }
    }
    //过滤重复项
    boolean isSame = false;
    if (CollectionUtils.isNotEmpty(selectList)) {
      for (int i = 0; i < selectList.size(); i++) {
        isSame = false;
        if (selectList.size() > 1) {
          for (int j = i + 1; j < selectList.size(); j++) {
            if (selectList.get(i).getCheckId().equals(selectList.get(j).getCheckId())) {
              isSame = true;
              break;
            }
          }
        }
        if (!isSame) {
          returnItemDTOList.add(selectList.get(i));
        }
      }
    }
    return returnItemDTOList;
  }

  @Override
  public PurchaseReturnDTO createPurchaseReturnDTO(Long shopId, List<PurchaseReturnItemDTO> selectList) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    PurchaseReturnDTO purchaseReturnDTO = new PurchaseReturnDTO();
    purchaseReturnDTO.setShopId(shopId);
    List<PurchaseReturnItemDTO> itemDTOs = new ArrayList<PurchaseReturnItemDTO>();
    //将selectList 封装成退货单中的item
    if (CollectionUtils.isNotEmpty(selectList)) {
      List<SupplierDTO> supplierDTOs = userService.getSupplierById(shopId, selectList.get(0).getSupplierId());
      if (CollectionUtils.isNotEmpty(supplierDTOs)) {
        purchaseReturnDTO.setSupplierDTO(supplierDTOs.get(0));
      }
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : selectList) {
        ProductDTO productDTO = productService.getProductByProductLocalInfoId(purchaseReturnItemDTO.getProductId(), shopId);
        if (productDTO != null) {
          purchaseReturnItemDTO.setProduct(productDTO);
          purchaseReturnItemDTO.setTotal(NumberUtil.round(purchaseReturnItemDTO.getAmount() * purchaseReturnItemDTO.getPrice(), NumberUtil.MONEY_PRECISION));
          itemDTOs.add(purchaseReturnItemDTO);
        } else {
          LOG.info("退货单选中的项目中productId 为{}的记录，找不到对应的productDTO，可能有bug或者脏数据!", purchaseReturnItemDTO.getProductId());
          continue;
        }
      }
    }
    if (CollectionUtils.isNotEmpty(itemDTOs)) {
      PurchaseReturnItemDTO[] purchaseReturnItemDTOs = new PurchaseReturnItemDTO[itemDTOs.size()];
      purchaseReturnDTO.setItemDTOs(itemDTOs.toArray(purchaseReturnItemDTOs));
    }
    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString("yyyy-MM-dd", curTime);
    purchaseReturnDTO.setEditDate(curTime);
    purchaseReturnDTO.setEditDateStr(time);
    purchaseReturnDTO.setVestDate(curTime);
    purchaseReturnDTO.setVestDateStr(time);
    return purchaseReturnDTO;
  }

  public PurchaseReturnDTO createPurchaseReturnDTOByProductIds(Long shopId, Long[] productIds) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseReturnDTO purchaseReturnDTO = new PurchaseReturnDTO();
    purchaseReturnDTO.setShopId(shopId);
    List<PurchaseReturnItemDTO> itemDTOs = new ArrayList<PurchaseReturnItemDTO>();
    Double totalReturnAmount = 0d;
    Double totalReturnSum = 0d;
    if (productIds != null) {
      for (Long productId : productIds) {
        if (productId != null) {
          ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);
          if (productDTO != null) {
            PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
            purchaseReturnItemDTO.setProduct(productDTO);
            Inventory inventory = writer.getById(Inventory.class, productId);
            if (inventory != null) {
              purchaseReturnItemDTO.setInventoryAmount(inventory.getAmount());
              purchaseReturnItemDTO.setAmount(1D);
              purchaseReturnItemDTO.setInventoryAveragePrice(inventory.getInventoryAveragePrice());
              totalReturnAmount += purchaseReturnItemDTO.getAmount();
            }
            if (purchaseReturnItemDTO.getAmount() != null) {
              Double itemSum = NumberUtil.round(purchaseReturnItemDTO.getAmount() * purchaseReturnItemDTO.getPrice(), NumberUtil.MONEY_PRECISION);
              purchaseReturnItemDTO.setTotal(itemSum);
              totalReturnSum += itemSum;
            }

            itemDTOs.add(purchaseReturnItemDTO);
          } else {
            LOG.info("退货单选中的项目中productId 为:{}的记录，找不到对应的productDTO，可能有bug或者脏数据!", shopId );
          }
        }
      }
    }
    if (CollectionUtils.isNotEmpty(itemDTOs)) {
      PurchaseReturnItemDTO[] purchaseReturnItemDTOs = new PurchaseReturnItemDTO[itemDTOs.size()];
      purchaseReturnDTO.setItemDTOs(itemDTOs.toArray(purchaseReturnItemDTOs));
    }
    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", curTime);
    purchaseReturnDTO.setEditDate(curTime);
    purchaseReturnDTO.setEditDateStr(time);
    purchaseReturnDTO.setVestDate(curTime);
    purchaseReturnDTO.setVestDateStr(time);
    purchaseReturnDTO.setTotal(totalReturnSum);
    purchaseReturnDTO.setTotalReturnAmount(totalReturnAmount);
    return purchaseReturnDTO;

  }

  public PurchaseReturnDTO createPurchaseReturnDTOBySupplierId(Long shopId, Long supplierId) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    PurchaseReturnDTO purchaseReturnDTO = new PurchaseReturnDTO();
    purchaseReturnDTO.setShopId(shopId);

    List<SupplierDTO> supplierDTOs = userService.getSupplierById(shopId, supplierId);
    if (CollectionUtils.isNotEmpty(supplierDTOs)) {
      purchaseReturnDTO.setSupplierDTO(supplierDTOs.get(0));
    }

    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
    purchaseReturnDTO.setEditDate(curTime);
    purchaseReturnDTO.setEditDateStr(time);
    purchaseReturnDTO.setVestDate(curTime);
    purchaseReturnDTO.setVestDateStr(time);
    return purchaseReturnDTO;

  }


  @Override
  public void getGoodsReturnInfo(ModelMap model, Long shopId, String purchaseReturnId) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    try {
      PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(new Long(purchaseReturnId));
      if (purchaseReturnDTO == null) {
        LOG.error("can not find purchaseReturn with id" + purchaseReturnId);
        return;
      }
      purchaseReturnDTO.setEditDateStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", purchaseReturnDTO.getEditDate()));          // 制单日期
      purchaseReturnDTO.setVestDateStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", purchaseReturnDTO.getVestDate()));          // 归属时间日期
      //得到供应商信息
      SupplierDTO supplierDTO = userService.getSupplierById(purchaseReturnDTO.getSupplierId());
      if (supplierDTO != null) {
        purchaseReturnDTO.setSupplierDTO(supplierDTO);
      }
      model.addAttribute("purchaseReturnDTO", purchaseReturnDTO);
      ShopDTO shopDTO = configService.getShopById(new Long(shopId));
      model.addAttribute("storeManagerMobile", shopDTO.getStoreManagerMobile());
      purchaseReturnDTO.setShopDTO(shopDTO);
      PurchaseReturnItemDTO[] purchaseReturnItemDTOs = purchaseReturnDTO.getItemDTOs();                             //得到材料列表
      for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnItemDTOs) {
        //得到产品信息
        ProductDTO productDTO = productService.getProductByProductLocalInfoId(purchaseReturnItemDTO.getProductId(), purchaseReturnDTO.getShopId());
        Long[] productLocalInfoId = new Long[1];
        productLocalInfoId[0] = purchaseReturnItemDTO.getProductId();
        ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(purchaseReturnItemDTO.getProductHistoryId(), shopId);
        if (null != productHistoryDTO) {
          purchaseReturnItemDTO.setProductHistoryDTO(productHistoryDTO);
          if(OrderUtil.purchaseReturnInProgress.contains(purchaseReturnDTO.getStatus())){
            purchaseReturnItemDTO.setProductUnitRateInfo(productDTO);
          }
        }else{
          purchaseReturnItemDTO.setProduct(productDTO);
        }
      }
    } catch (Exception e) {
      LOG.debug("/goodsReturn.do");
      LOG.debug("shopId:" + shopId + ",userId:");
      LOG.debug("purchaseReturnId:" + purchaseReturnId);
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void initAmountForPurchaseReturnItem(List<PurchaseReturnItemDTO> selectList,
                                              List<PurchaseReturnItemDTO> purchaseReturnItemDTOs) {
    if (CollectionUtils.isEmpty(selectList) || CollectionUtils.isEmpty(purchaseReturnItemDTOs)) {
      return;
    }
    for (PurchaseReturnItemDTO selectItem : selectList) {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnItemDTOs) {
        if (selectItem.getCheckId().equals(itemDTO.getSupplierId() + "_" + itemDTO.getProductId())) {
          itemDTO.setAmount(selectItem.getAmount());
        }
      }
    }

  }

  @Override
  public PurchaseReturnDTO getPurchaseReturnById(long orderId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class, orderId);
    if (purchaseReturn != null) {
      return purchaseReturn.toDTO();
    }
    return null;
  }


  /**
   * 根据产品Id获得供应商信息
   *
   * @param productId Product_Local_Info 表里的Id
   * @param shopId
   * @return
   */
  @Override
  public List<SupplierDTO> getSupplierDTOsByProductId(Long productId, Long shopId) {

    List<SupplierDTO> supplierDTOsList = null;
    if (productId != null && shopId != null) {
      List<SupplierInventoryDTO> supplierInventoryDTOs = null;
      IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
      Set<Long> productIds = new HashSet<Long>();
      productIds.add(productId);
      Map<Long,List<SupplierInventoryDTO>> supplierInventoryMap = productThroughService.getSupplierInventoryMap(shopId,productIds);
      supplierInventoryDTOs = supplierInventoryMap.get(productId);

      if (supplierInventoryDTOs != null) {
        if (!supplierInventoryDTOs.isEmpty()) {
          supplierDTOsList = new ArrayList<SupplierDTO>();
          ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
          for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
            SupplierDTO supplierDTO = supplierService.getSupplierById(supplierInventoryDTO.getSupplierId(),shopId);
            if (supplierDTO != null) {
              supplierDTOsList.add(supplierDTO);
            }
          }
        }
      }
    }
    return supplierDTOsList;
  }

  /**
   * 填充退货明细中的具体信息
   *
   * @param purchaseReturnDTO
   * @return
   * @throws Exception
   */
  @Override
  public PurchaseReturnDTO fillPurchaseReturnItemDTOsDetailInfo(PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    PurchaseOrderDTO purchaseOrderDTO = null;
    //key productLocalInfoId    关联采购单的采购价信息
    Map<Long ,PurchaseOrderItemDTO> purchaseOrderItemDTOMap = new HashMap<Long, PurchaseOrderItemDTO>();
    if(purchaseReturnDTO.getPurchaseOrderId() != null && purchaseReturnDTO.getShopId() != null){
       purchaseOrderDTO = rfiTxnService.getPurchaseOrderDTOById(purchaseReturnDTO.getPurchaseOrderId(), purchaseReturnDTO.getShopId());
       if(purchaseOrderDTO != null && !ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
         Set<Long> supplierProductIds = new HashSet<Long>();
         for(PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()){
           if(purchaseOrderItemDTO.getSupplierProductId() != null){
             supplierProductIds.add(purchaseOrderItemDTO.getSupplierProductId());
           }
         }
         Map<Long, ProductMappingDTO> productMappingDTOMap = productService.getSupplierProductMappingDTODetailMap(
             purchaseOrderDTO.getSupplierShopId(), purchaseOrderDTO.getShopId(), supplierProductIds);
         for(PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()){
           if(purchaseOrderItemDTO.getSupplierProductId() != null ){
             ProductMappingDTO productMappingDTO =  productMappingDTOMap.get(purchaseOrderItemDTO.getSupplierProductId());
             if(productMappingDTO != null){
               purchaseOrderItemDTOMap.put(productMappingDTO.getCustomerProductId(),purchaseOrderItemDTO);
             }
             supplierProductIds.add(purchaseOrderItemDTO.getSupplierProductId());
           }
         }
       }

    }
    TxnWriter writer = txnDaoManager.getWriter();
    if (purchaseReturnDTO != null) {
      Long shopId = purchaseReturnDTO.getShopId();
      PurchaseReturnItemDTO[] purchaseReturnItemDTOsArray = purchaseReturnDTO.getItemDTOs();
      double totalReturnAmount = 0d;
      if (purchaseReturnItemDTOsArray != null && shopId != null) {
        for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnItemDTOsArray) {
          Long productLocalInfoId = purchaseReturnItemDTO.getProductId();
          PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderItemDTOMap.get(productLocalInfoId);
          if(purchaseOrderItemDTO !=null){
            purchaseReturnItemDTO.setPurchasePrice(purchaseOrderItemDTO.getPrice());
            purchaseReturnItemDTO.setPurchaseUnit(purchaseOrderItemDTO.getUnit());
            purchaseReturnItemDTO.setPurchaseAmount(purchaseOrderItemDTO.getAmount());
          }
          ProductDTO productDTO = productService.getProductByProductLocalInfoId(productLocalInfoId, shopId);
          Inventory inventory = writer.getById(Inventory.class, productLocalInfoId);
          if (inventory != null) {
            Double inventoryAmount = inventory.getAmount();
            if(UnitUtil.isStorageUnit(purchaseReturnItemDTO.getUnit(),productDTO)){
              inventoryAmount = inventoryAmount/productDTO.getRate();
            }
            purchaseReturnItemDTO.setInventoryAmount(inventoryAmount);
          }
          ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(purchaseReturnItemDTO.getProductHistoryId(), shopId);
          if (null != productHistoryDTO) {
            purchaseReturnItemDTO.setProductHistoryDTO(productHistoryDTO);
            if(OrderUtil.purchaseReturnInProgress.contains(purchaseReturnDTO.getStatus())){
              purchaseReturnItemDTO.setProductUnitRateInfo(productDTO);
            }
          }else{
            purchaseReturnItemDTO.setProductDTOWithOutUnit(productDTO);
          }

          totalReturnAmount += purchaseReturnItemDTO.getAmount();
        }
      }
      purchaseReturnDTO.setTotalReturnAmount(NumberUtil.round(totalReturnAmount,NumberUtil.MONEY_PRECISION));
    }
    //关联销售单的单据号
    if(purchaseReturnDTO!=null && purchaseReturnDTO.getSupplierShopId() != null){
      SalesReturnDTO salesReturnDTO =  ServiceManager.getService(ISaleReturnOrderService.class)
          .getSimpleSalesReturnDTOByPurchaseReturnOrderId(purchaseReturnDTO.getSupplierShopId(),purchaseReturnDTO.getId());
      if(salesReturnDTO != null){
        purchaseReturnDTO.setSaleReturnReceiptNo(salesReturnDTO.getReceiptNo());
      }
    }
    if (purchaseReturnDTO.getOriginOrderId() != null){
      PurchaseInventory inventory = writer.getById(PurchaseInventory.class, purchaseReturnDTO.getOriginOrderId());
      if (inventory != null) {
        purchaseReturnDTO.setOriginReceiptNo(inventory.getReceiptNo());
      }
    }
    return purchaseReturnDTO;
  }



  public PurchaseReturnDTO copyPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO) throws Exception{
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    PurchaseReturnDTO newPurchaseReturnDTO = null;
    if (purchaseReturnDTO != null) {
      newPurchaseReturnDTO = new PurchaseReturnDTO();
      newPurchaseReturnDTO.setId(null);
      long curTime = System.currentTimeMillis();
      String time = DateUtil.convertDateLongToDateString("yyyy-MM-dd", curTime);
      newPurchaseReturnDTO.setEditDate(curTime);
      newPurchaseReturnDTO.setEditDateStr(time);
      newPurchaseReturnDTO.setVestDate(curTime);
      newPurchaseReturnDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime));
      newPurchaseReturnDTO.setStorehouseId(purchaseReturnDTO.getStorehouseId());
      newPurchaseReturnDTO.setContactId(purchaseReturnDTO.getContactId());
      Long shopId = purchaseReturnDTO.getShopId();
      SupplierDTO historySupplierDTO = purchaseReturnDTO.generateSupplierDTO();
      if(!supplierService.compareSupplierSameWithHistory(historySupplierDTO, purchaseReturnDTO.getShopId())){
        newPurchaseReturnDTO.clearSupplierDTO();
      }else{
        newPurchaseReturnDTO.setSupplierDTO(historySupplierDTO);
        SupplierDTO supplierDTO = ServiceManager.getService(ISupplierService.class).getSupplierById(newPurchaseReturnDTO.getSupplierId(),shopId);
        if(supplierDTO != null){
          newPurchaseReturnDTO.setSupplierShopId(supplierDTO.getSupplierShopId());
        }
      }
      Long purchaseReturnId = purchaseReturnDTO.getId();
      Double totalReturnAmount = 0d;
      Double totalMoney = 0d;
      if (purchaseReturnId != null) {
        TxnWriter writer = txnDaoManager.getWriter();
        IProductService productService = ServiceManager.getService(IProductService.class);
        List<PurchaseReturnItem> items = writer.getPurchaseReturnItemsByReturnId(purchaseReturnId);
        PurchaseReturnItemDTO[] resultItemDTOs = new PurchaseReturnItemDTO[0];
        for (int i = 0; i < items.size(); i++) {
          PurchaseReturnItem item = items.get(i);
          PurchaseReturnItemDTO purchaseReturnItemDTO = item.toDTO();
          Long productLocalInfoId = purchaseReturnItemDTO.getProductId();
          if(!productHistoryService.compareProductSameWithHistory(productLocalInfoId, purchaseReturnItemDTO.getProductHistoryId(), shopId)){
            continue;
          }
          ProductDTO productDTO = productService.getProductByProductLocalInfoId(productLocalInfoId, shopId);
          ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(purchaseReturnItemDTO.getProductHistoryId(), shopId);
          if(productHistoryDTO!=null){
            purchaseReturnItemDTO.setProductHistoryDTO(productHistoryDTO);
            if(productDTO!=null){
              purchaseReturnItemDTO.setSellUnit(productDTO.getSellUnit());
              purchaseReturnItemDTO.setStorageUnit(productDTO.getStorageUnit());
              purchaseReturnItemDTO.setRate(productDTO.getRate());
            }
          }else{
            purchaseReturnItemDTO.setProductDTOWithOutUnit(productDTO);
          }

          Inventory inventory = writer.getById(Inventory.class, productLocalInfoId);

          //如果退货是库存大单位
          if (UnitUtil.isStorageUnit(purchaseReturnItemDTO.getUnit(), productDTO)) {
            if (inventory != null) {
              purchaseReturnItemDTO.setInventoryAmount(NumberUtil.round(inventory.getAmount()/productDTO.getRate(),2));
            }
          }
          else {
            if (inventory != null) {
              purchaseReturnItemDTO.setInventoryAmount(inventory.getAmount());
            }
          }
          purchaseReturnItemDTO.setPrice(item.getPrice());

          totalReturnAmount += purchaseReturnItemDTO.getAmount();
          totalMoney += purchaseReturnItemDTO.getTotal();
          purchaseReturnItemDTO.setId(null);
          purchaseReturnItemDTO.setPurchaseReturnId(null);
          purchaseReturnItemDTO.setReserved(0d);
          resultItemDTOs = (PurchaseReturnItemDTO[])ArrayUtils.add(resultItemDTOs, purchaseReturnItemDTO);
        }
        newPurchaseReturnDTO.setItemDTOs(resultItemDTOs);
        newPurchaseReturnDTO.setTotalReturnAmount(totalReturnAmount);
        newPurchaseReturnDTO.setTotal(totalMoney);
      }

    }
    return newPurchaseReturnDTO;
  }



  /**
   * 退货单作废
   * @param purchaseReturnDTO
   * @param inventorySearchIndexList
   * @return
   * @throws Exception
   */
  public PurchaseReturnDTO repealPurchaseReturn(PurchaseReturnDTO purchaseReturnDTO, List<InventorySearchIndex> inventorySearchIndexList) throws Exception {
    if (purchaseReturnDTO != null) {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      TxnWriter writer = txnDaoManager.getWriter();
      Long shopId = purchaseReturnDTO.getShopId();
      Long purchaseReturnId = purchaseReturnDTO.getId();
      Object status = writer.begin();
      try {
        if (shopId != null && purchaseReturnId != null) {
          SupplierReturnPayableDTO supplierReturnPayableDTO = supplierPayableService.getSupplierReturnPayableByPurchaseReturnId(shopId, purchaseReturnId);
          if (supplierReturnPayableDTO != null) {
            Double cash = supplierReturnPayableDTO.getCash();    //TODO 退货单作废 退现金如何处理？
            Double deposit = supplierReturnPayableDTO.getDeposit();
            Double strikeAmount = supplierReturnPayableDTO.getStrikeAmount();

            List<PayableHistoryRecord> payableHistoryRecordListForReturn = writer.getPayableHistoryRecordListByPurchaseReturnId(shopId, purchaseReturnId, PaymentTypes.INVENTORY_RETURN);
            if (CollectionUtils.isNotEmpty(payableHistoryRecordListForReturn)) {
              for (PayableHistoryRecord payableHistoryRecord : payableHistoryRecordListForReturn) {
                payableHistoryRecord.setStatus(PayStatus.REPEAL);
                writer.update(payableHistoryRecord);
              }
            }

            Long supplierId = supplierReturnPayableDTO.getSupplierId();
            if (supplierId != null) {
              //退货单退定金 现在退货单作废将定金还原
              Deposit depositObj = writer.getDepositBySupplierId(shopId, supplierId);
              if (depositObj != null && deposit != null && deposit > 0) {
                double actuallyPaid = depositObj.getActuallyPaid() == null ? 0d : depositObj.getActuallyPaid();
                if (actuallyPaid < deposit) {
                  LOG.error("定金不足，无法作废退货单，PurchaseReturnId:" + purchaseReturnDTO.getId());
                  throw new Exception("定金不足，无法作废退货单");
                } else {
                  depositObj.setActuallyPaid(NumberUtil.round(actuallyPaid - deposit, NumberUtil.MONEY_PRECISION));
                  depositObj.setCash(NumberUtil.round(depositObj.getCash() - deposit, NumberUtil.MONEY_PRECISION));
                  writer.update(depositObj);
                }
              }
            }
            //退货单作废 将冲账还原
            if (strikeAmount != null && strikeAmount > 0) {
              List<PayableHistoryRecord> payableHistoryRecordList = writer.getPayableHistoryRecordListByPurchaseReturnId(shopId, purchaseReturnId, PaymentTypes.INVENTORY_DEBT);
              if (CollectionUtils.isNotEmpty(payableHistoryRecordList)) {
                for (PayableHistoryRecord payableHistoryRecord : payableHistoryRecordList) {
                  Long payableId = payableHistoryRecord.getPayableId();
                  Payable payable = writer.getById(Payable.class, payableId);
                  if (payable != null) {
                    PayStatus payStatus = payable.getStatus();
                    if ("作废".equals(payStatus.getType())) {
                      LOG.error("退货冲账入库单已作废，无法作废退货单，PurchaseReturnId:" + purchaseReturnDTO.getId());
                      throw new Exception("退货冲账入库单已作废，无法作废退货单");
                    }
                    Double payableStrikeAmount = payable.getStrikeAmount();
                    if (payableStrikeAmount != null && payableStrikeAmount > 0) {
                      payable.setPaidAmount(NumberUtil.round(payable.getPaidAmount() - payableStrikeAmount, NumberUtil.MONEY_PRECISION));
                      payable.setCreditAmount(NumberUtil.round(payable.getCreditAmount() + payableStrikeAmount, NumberUtil.MONEY_PRECISION));
                    }
                    payable.setStrikeAmount(0d);
                    writer.update(payable);
                  }

                  payableHistoryRecord.setStatus(PayStatus.REPEAL);
                  writer.update(payableHistoryRecord);
                }

                SupplierRecord supplierRecord = writer.getSupplierRecord(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getSupplierId());
                supplierRecord.setCreditAmount(NumberUtil.round(supplierRecord.getCreditAmount() + strikeAmount, NumberUtil.MONEY_PRECISION));
                writer.update(supplierRecord);
              } else {
                LOG.error("冲账付款记录不存在，PurchaseReturnId:" + purchaseReturnDTO.getId());
                throw new Exception("冲账付款记录不存在，无法作废退货单");
              }
            }
          }
        }
        //退货单作废库存增加
        PurchaseReturnItemDTO[] itemDTOs = purchaseReturnDTO.getItemDTOs();
        if (itemDTOs != null) {
          IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
          IProductService productService = ServiceManager.getService(IProductService.class);
          ISearchService searchService = ServiceManager.getService(SearchService.class);


          for (PurchaseReturnItemDTO purchaseReturnItemDTO : itemDTOs) {
            Inventory inventory = writer.getById(Inventory.class, purchaseReturnItemDTO.getProductId());
            inventoryService.caculateBeforeLimit(inventory.toDTO(), purchaseReturnDTO.getInventoryLimitDTO());

            //本地商品库
            ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(purchaseReturnItemDTO.getProductId(), purchaseReturnDTO.getShopId());
            //标准商品库
            ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), purchaseReturnDTO.getShopId());

            double purchaseReturnItemAmount = purchaseReturnItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(purchaseReturnItemDTO.getUnit(), productDTO)) {
              purchaseReturnItemAmount = purchaseReturnItemAmount * productDTO.getRate();
            }
            inventory.setAmount(inventory.getAmount() + purchaseReturnItemAmount);
            //更新库存
            writer.update(inventory);
            inventoryService.caculateAfterLimit(inventory.toDTO(), purchaseReturnDTO.getInventoryLimitDTO());

            //根据productId找到库存 SearchIndex
            InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(purchaseReturnItemDTO.getProductId());
            if (inventorySearchIndex != null) {
              //设置库存量
              inventorySearchIndex.setAmount(inventory.getAmount());
              inventorySearchIndexList.add(inventorySearchIndex);
            }
          }
        } else {
          LOG.error("退货明细不存在，无法作废，PurchaseReturnId:" + purchaseReturnDTO.getId());
          throw new Exception("退货明细不存在，无法作废");
        }

        PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class, purchaseReturnDTO.getId());
        purchaseReturn.setStatus(OrderStatus.REPEAL);
        purchaseReturnDTO.setStatus(OrderStatus.REPEAL);
        writer.update(purchaseReturn);

        writer.commit(status);

      } catch (Exception e) {
        purchaseReturnDTO.setStatus(OrderStatus.SETTLED);
        throw new Exception(e.getMessage());
      } finally {
        writer.rollback(status);
      }

    }
    return purchaseReturnDTO;
  }

  @Override
  public PurchaseReturnDTO repealPurchaseReturnInTxn(PurchaseReturnDTO purchaseReturnDTO, Long toStorehouseId) throws Exception {
    if (purchaseReturnDTO != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
        AllocateRecordDTO allocateRecordDTO = null;
        if((OrderStatus.SELLER_PENDING.equals(purchaseReturnDTO.getStatus()) && purchaseReturnDTO.getSupplierShopId()!=null)
            || (purchaseReturnDTO.getSupplierShopId() == null && purchaseReturnDTO.getStatus() == OrderStatus.SETTLED)){
          //退货单作废库存增加
          PurchaseReturnItemDTO[] itemDTOs = purchaseReturnDTO.getItemDTOs();
          if (itemDTOs != null) {
            Set<Long> productIdSet = new HashSet<Long>();
            IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
            IProductService productService = ServiceManager.getService(IProductService.class);
            ISearchService searchService = ServiceManager.getService(ISearchService.class);

            InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
            IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);

            for (PurchaseReturnItemDTO purchaseReturnItemDTO : itemDTOs) {
              productIdSet.add(purchaseReturnItemDTO.getProductId());
              Inventory inventory = writer.getById(Inventory.class, purchaseReturnItemDTO.getProductId());
              inventoryService.caculateBeforeLimit(inventory.toDTO(),inventoryLimitDTO);

              //本地商品库
              ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(purchaseReturnItemDTO.getProductId(), purchaseReturnDTO.getShopId());
              //标准商品库
              ProductDTO productDTO = productService.getProductById(productLocalInfoDTO.getProductId(), purchaseReturnDTO.getShopId());

              double purchaseReturnItemAmount = purchaseReturnItemDTO.getAmount();
              if (UnitUtil.isStorageUnit(purchaseReturnItemDTO.getUnit(), productDTO)) {
                purchaseReturnItemAmount = purchaseReturnItemAmount * productDTO.getRate();
              }
              inventory.setAmount(inventory.getAmount() + purchaseReturnItemAmount);
              //更新库存
              if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(purchaseReturnDTO.getShopVersionId())){
                StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(purchaseReturnDTO.getStorehouseId(),purchaseReturnItemDTO.getProductId(),null);
                storeHouseInventoryDTO.setChangeAmount(purchaseReturnItemAmount);
                storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
              }
              writer.update(inventory);
              inventoryService.caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
              //根据productId找到库存 SearchIndex
              InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(purchaseReturnItemDTO.getProductId());
              if (inventorySearchIndex != null) {
                inventorySearchIndex.setEditDate(purchaseReturnDTO.getEditDate());
                //设置库存量
                inventorySearchIndex.setAmount(inventory.getAmount());
                inventorySearchIndexList.add(inventorySearchIndex);
              }
            }
            //更新库存告警信息
            inventoryService.updateMemocacheLimitByInventoryLimitDTO(purchaseReturnDTO.getShopId(), inventoryLimitDTO);
            //草稿单作废
            ServiceManager.getService(IDraftOrderService.class).deleteDraftOrderByTxnOrderId(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getId());

            ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(purchaseReturnDTO.getShopId(), productIdSet.toArray(new Long[productIdSet.size()]));

          } else {
            LOG.error("退货明细不存在，无法作废，PurchaseReturnId:" + purchaseReturnDTO.getId());
            throw new Exception("退货明细不存在，无法作废");
          }
          //在线单据对应的销售退货单
          List<SalesReturn> salesReturnList = writer.getSalesReturnDTOByPurchaseReturnOrderId(purchaseReturnDTO.getId());
          if(CollectionUtils.isNotEmpty(salesReturnList)){
            SalesReturn salesReturn = salesReturnList.get(0);
            salesReturn.setStatus(OrderStatus.STOP);
            writer.update(salesReturn);
            IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
            ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(salesReturn.getShopId(), purchaseReturnDTO.getUserId(), salesReturn.getId(), ObjectTypes.SALE_RETURN_ORDER, OperationTypes.INVALID));

          }
            purchaseReturnDTO.setStatus(OrderStatus.REPEAL);
            getProductInStorageService().productThroughByOrder(purchaseReturnDTO, OrderTypes.RETURN, OrderStatus.REPEAL, writer);
            //自动生成调拨单

          if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(purchaseReturnDTO.getShopVersionId()) && toStorehouseId!=null) {
            allocateRecordDTO = createAllocateRecordByPurchaseReturnDTO(writer,toStorehouseId, purchaseReturnDTO);
          }
        }
        PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class, purchaseReturnDTO.getId());
        purchaseReturn.setStatus(OrderStatus.REPEAL);
        writer.update(purchaseReturn);
        purchaseReturnDTO.setStatus(OrderStatus.REPEAL);
        writer.commit(status);
        //更新supplier_record
        SupplierRecordDTO supplierRecordDTO =ServiceManager.getService(ISupplierRecordService.class).getSupplierRecordDTOBySupplierId(purchaseReturn.getShopId(),purchaseReturn.getSupplierId());
        List<Double> returnList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(purchaseReturn.getSupplierId(), purchaseReturn.getShopId(), OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
        if(returnList != null) {
          supplierRecordDTO.setDebt(NumberUtil.doubleVal(supplierRecordDTO.getDebt()) + NumberUtil.doubleVal(returnList.get(0)));
          ServiceManager.getService(ISupplierRecordService.class).saveOrUpdateSupplierRecord(supplierRecordDTO);
        }

        IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(purchaseReturnDTO.getShopId(), purchaseReturnDTO.getUserId(), purchaseReturnDTO.getId(), ObjectTypes.PURCHASE_RETURN_ORDER, OperationTypes.INVALID));
        if (allocateRecordDTO != null && allocateRecordDTO.getId() != null) {
          BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
          BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(allocateRecordDTO, OrderTypes.ALLOCATE_RECORD);
          bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
        }
        ServiceManager.getService(IInventoryService.class).addOrUpdateInventorySearchIndexWithList(purchaseReturnDTO.getShopId(), inventorySearchIndexList);
      } catch (Exception e) {
        throw new Exception(e.getMessage());
      } finally {
        writer.rollback(status);
      }
      purchaseReturnDTO.setStatus(OrderStatus.REPEAL);
      //更新remind_event
      ServiceManager.getService(ITxnService.class).cancelRemindEventByOrderId(RemindEventType.DEBT, purchaseReturnDTO.getId());
    }
    return purchaseReturnDTO;
  }

  private AllocateRecordDTO createAllocateRecordByPurchaseReturnDTO(TxnWriter writer,Long toStorehouseId, PurchaseReturnDTO purchaseReturnDTO) throws Exception {
    AllocateRecordDTO allocateRecordDTO = new AllocateRecordDTO();
    allocateRecordDTO.setOutStorehouseId(purchaseReturnDTO.getStorehouseId());
    allocateRecordDTO.setInStorehouseId(toStorehouseId);
    allocateRecordDTO.setOriginOrderId(purchaseReturnDTO.getId());
    allocateRecordDTO.setOriginOrderType(OrderTypes.RETURN);
    allocateRecordDTO.setEditorId(purchaseReturnDTO.getEditorId());
    allocateRecordDTO.setEditor(purchaseReturnDTO.getEditor());
    allocateRecordDTO.setEditDate(System.currentTimeMillis());
    allocateRecordDTO.setVestDate(System.currentTimeMillis());
    allocateRecordDTO.setShopId(purchaseReturnDTO.getShopId());
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    allocateRecordDTO.setReceiptNo(txnService.getReceiptNo(allocateRecordDTO.getShopId(), OrderTypes.ALLOCATE_RECORD, null));

    AllocateRecordItemDTO allocateRecordItemDTO = null;
    Double totalCostPrice = 0d,totalAmount = 0d;
    if(!ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())){
      List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
      for(PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()){
        allocateRecordItemDTO = new AllocateRecordItemDTO();
        allocateRecordItemDTO.setAmount(purchaseReturnItemDTO.getAmount());
        allocateRecordItemDTO.setTotalCostPrice(purchaseReturnItemDTO.getTotal());
        allocateRecordItemDTO.setCostPrice(purchaseReturnItemDTO.getPrice());
        allocateRecordItemDTO.setProductHistoryId(purchaseReturnItemDTO.getProductHistoryId());
        allocateRecordItemDTO.setProductId(purchaseReturnItemDTO.getProductId());
        allocateRecordItemDTO.setUnit(purchaseReturnItemDTO.getUnit());
        allocateRecordItemDTO.setStorageBin(purchaseReturnItemDTO.getStorageBin());
        totalCostPrice += NumberUtil.doubleVal(allocateRecordItemDTO.getTotalCostPrice());
        totalAmount += NumberUtil.doubleVal(allocateRecordItemDTO.getAmount());
        allocateRecordItemDTOList.add(allocateRecordItemDTO);
      }
      allocateRecordDTO.setItemDTOs(allocateRecordItemDTOList.toArray(new AllocateRecordItemDTO[allocateRecordItemDTOList.size()]));
    }

    allocateRecordDTO.setTotalCostPrice(totalCostPrice);
    allocateRecordDTO.setTotalAmount(totalAmount);
    IAllocateRecordService allocateRecordService = ServiceManager.getService(IAllocateRecordService.class);
    allocateRecordService.saveOrUpdateAllocateRecord(writer,purchaseReturnDTO.getShopId(),allocateRecordDTO);
    return allocateRecordDTO;
  }

  @Override
  public Result validatePurchaseReturnByPurchaseOrderId(Long shopId, Long purchaseOrderId) {
    IGoodBuyService goodBuyService = ServiceManager.getService(IGoodBuyService.class);

    PurchaseOrderDTO purchaseOrderDTO =  goodBuyService.getSimplePurchaseOrderDTO(shopId,purchaseOrderId);
    if(purchaseOrderDTO == null){
      return new Result(null,"当前采购单不存在，请重新选择在线采购单退货",false, Result.Operation.ALERT_REDIRECT);
    }
    if(purchaseOrderDTO.getSupplierShopId() == null){
      return new Result(null,"当前采购单不是在线采购，请重新选择在线采购单退货",false, Result.Operation.ALERT_REDIRECT);
    }
    if( !OrderStatus.PURCHASE_ORDER_DONE.equals(purchaseOrderDTO.getStatus())){
      return new Result(null,"当前采购单未入库无法生成在线采购单，请重新选择在线采购单退货",false, Result.Operation.ALERT_REDIRECT);
    }

    return new Result();
  }

  @Override
  public PurchaseReturnDTO createOnlinePurchaseReturnByPurchaseOrderId(Long shopId, Long purchaseOrderId,Set<Long> itemIds)throws Exception{
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    PurchaseReturnDTO purchaseReturnDTO = new PurchaseReturnDTO();
    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
    purchaseReturnDTO.setEditDate(curTime);
    purchaseReturnDTO.setEditDateStr(time);
    purchaseReturnDTO.setVestDate(curTime);
    purchaseReturnDTO.setVestDateStr(time);
    if(shopId == null || purchaseOrderId == null){
      return new PurchaseReturnDTO();
    }
    PurchaseOrderDTO purchaseOrderDTO = rfiTxnService.getPurchaseOrderDTOById(purchaseOrderId, shopId);
    if(purchaseOrderDTO == null){
      return new PurchaseReturnDTO();
    }
    if(!ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs()) && CollectionUtils.isNotEmpty(itemIds)){
      List<PurchaseOrderItemDTO> purchaseOrderItemDTOs = new ArrayList<PurchaseOrderItemDTO>();
      for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
        if (purchaseOrderItemDTO != null && itemIds.contains(purchaseOrderItemDTO.getId())) {
          purchaseOrderItemDTOs.add(purchaseOrderItemDTO);
        }
      }
      purchaseOrderDTO.setItemDTOs(purchaseOrderItemDTOs.toArray(new PurchaseOrderItemDTO[purchaseOrderItemDTOs.size()]));
    }
    Set<Long> supplierProductIds = new HashSet<Long>();
    if(!ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
      for(PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()){
        if(purchaseOrderItemDTO.getSupplierProductId() != null){
          supplierProductIds.add(purchaseOrderItemDTO.getSupplierProductId());
        }
      }
    }
    Map<Long,ProductMappingDTO> productMappingDTOMap  = productService.getSupplierProductMappingDTODetailMap(purchaseOrderDTO.getSupplierShopId(),shopId,supplierProductIds);
    Set<Long> localProductIds = new HashSet<Long>();

    if (!productMappingDTOMap.isEmpty() && !CollectionUtils.isEmpty(productMappingDTOMap.values())) {
      for (ProductMappingDTO productMappingDTO : productMappingDTOMap.values()) {
        if (productMappingDTO.getCustomerProductId() != null) {
          localProductIds.add(productMappingDTO.getCustomerProductId());
        }
      }
    }
    Map<Long,InventoryDTO> inventoryDTOMap =  inventoryService.getInventoryDTOMap(shopId,localProductIds);

    //采购单信息
    purchaseReturnDTO.setPurchaseOrderId(purchaseOrderDTO.getId());
    purchaseReturnDTO.setPurchaseOrderNo(purchaseOrderDTO.getReceiptNo());
    purchaseReturnDTO.setSupplierShopId(purchaseOrderDTO.getSupplierShopId());
    //供应商信息
    purchaseReturnDTO.setShopId(shopId);
    if (purchaseOrderDTO.getSupplierId() != null) {
      List<SupplierDTO> supplierDTOs = userService.getSupplierById(shopId, purchaseOrderDTO.getSupplierId());
      if (CollectionUtils.isNotEmpty(supplierDTOs)) {
        purchaseReturnDTO.setSupplierDTO(supplierDTOs.get(0));
      }
    }

    //item信息
    double totalReturnSum = 0d;
    List<PurchaseReturnItemDTO> purchaseReturnItemDTOs = new ArrayList<PurchaseReturnItemDTO>();
    if(!ArrayUtils.isEmpty(purchaseOrderDTO.getItemDTOs())){
      for(PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()){
        if(purchaseOrderItemDTO.getSupplierProductId() == null){
         continue;
        }
        PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
        ProductDTO productDTO =  productMappingDTOMap.get(purchaseOrderItemDTO.getSupplierProductId()) == null ?
            null :  productMappingDTOMap.get(purchaseOrderItemDTO.getSupplierProductId()).getCustomerProductDTO();

        if(productDTO != null){
          //本店产品信息
          purchaseReturnItemDTO.setProduct(productDTO);
          //采购量单位
          purchaseReturnItemDTO.setPurchaseAmount(purchaseOrderItemDTO.getAmount());
          purchaseReturnItemDTO.setPurchaseUnit(purchaseOrderItemDTO.getUnit());
          //采购价
          purchaseReturnItemDTO.setPrice(purchaseOrderItemDTO.getPrice());
          purchaseReturnItemDTO.setPurchasePrice(purchaseOrderItemDTO.getPrice());

          //库存
          String itemUnit = purchaseOrderItemDTO.getUnit();
          InventoryDTO inventoryDTO = inventoryDTOMap.get(productDTO.getProductLocalInfoId());
          double inventoryAmount = inventoryDTO == null ? null : inventoryDTO.getAmount();
          if(UnitUtil.isStorageUnit(purchaseOrderItemDTO.getUnit(),productDTO)){
            inventoryAmount = inventoryAmount / productDTO.getRate();
          } else {
            itemUnit = productDTO.getSellUnit();
          }
          purchaseReturnItemDTO.setUnit(itemUnit);
          purchaseReturnItemDTO.setInventoryAmount(inventoryAmount);
          purchaseReturnItemDTO.setAmount(1D);

          Double itemSum = NumberUtil.round(purchaseReturnItemDTO.getAmount() * purchaseReturnItemDTO.getPrice(), NumberUtil.MONEY_PRECISION);
          purchaseReturnItemDTO.setTotal(itemSum);
          totalReturnSum += itemSum;
          purchaseReturnItemDTOs.add(purchaseReturnItemDTO);
        }
      }
    }
    purchaseReturnDTO.setItemDTOs(purchaseReturnItemDTOs.toArray(new PurchaseReturnItemDTO[purchaseReturnItemDTOs.size()]));
    purchaseReturnDTO.setTotal(NumberUtil.round(totalReturnSum,2));

    return purchaseReturnDTO;
  }

  @Override
  public PurchaseReturnItemDTO getPurchaseReturnItemDTOById(Long id) {
    if (id == null || id == 0L) {
      throw new RuntimeException("getPurchaseReturnItemDTOByIdAndShopId,id is null or 0L.");
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    PurchaseReturnItem purchaseReturnItem = txnWriter.getById(PurchaseReturnItem.class,id);
    if (purchaseReturnItem == null) {
      return null;
    }
    return purchaseReturnItem.toDTO();
  }

  @Override
  public PurchaseOrderItemDTO getPurchaseOrderItemDTOByPurchaseReturnItemId(Long id) {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseReturnItem purchaseReturnItem = writer.getById(PurchaseReturnItem.class, id);
    if(purchaseReturnItem == null){
      return null;
    }
    PurchaseReturn purchaseReturn = writer.getById(PurchaseReturn.class, purchaseReturnItem.getPurchaseReturnId());
    if(purchaseReturn == null || purchaseReturn.getPurchaseOrderId() == null){
      return null;
    }
    PurchaseOrder purchaseOrder = writer.getById(PurchaseOrder.class, purchaseReturn.getPurchaseOrderId());
    if(purchaseOrder == null){
      return null;
    }
    Map<Long, ProductMappingDTO> mappingDTOMap = ServiceManager.getService(IProductService.class).
        getCustomerProductMappings(purchaseReturn.getShopId(), new Long[]{purchaseReturnItem.getProductId()});
    if(MapUtils.isEmpty(mappingDTOMap)){
      return null;
    }
    ProductMappingDTO mappingDTO = mappingDTOMap.get(purchaseReturnItem.getProductId());
    List<PurchaseOrderItem> purchaseOrderItems = writer.getOnlinePurchaseOrderItemsByOrderIdSupplierProductId(purchaseOrder.getId(), mappingDTO.getSupplierProductId());
    if(CollectionUtils.isEmpty(purchaseOrderItems)){
      return null;
    }
    return purchaseOrderItems.get(0).toDTO();
  }

  @Override
  public List<PurchaseReturnDTO> getSimpleProcessingRelatedPurchaseReturnDTOs(Long customerShopId, Long supplierShopId) {
    List<PurchaseReturnDTO> purchaseReturnDTOs = new ArrayList<PurchaseReturnDTO>();
    if(customerShopId == null || supplierShopId == null){
      return purchaseReturnDTOs;
    }
    List<PurchaseReturn> purchaseReturns = txnDaoManager.getWriter().getProcessingRelatedPurchaseReturnOrders(customerShopId,supplierShopId);
    if(CollectionUtils.isNotEmpty(purchaseReturns)){
      for(PurchaseReturn purchaseReturn : purchaseReturns){
        purchaseReturnDTOs.add(purchaseReturn.toDTO());
      }
    }
    return purchaseReturnDTOs;
  }
}