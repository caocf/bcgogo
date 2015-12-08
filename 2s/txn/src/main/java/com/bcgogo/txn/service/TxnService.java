package com.bcgogo.txn.service;

import com.bcgogo.common.CommonUtil;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.constant.NewTodoConstants;
import com.bcgogo.constant.OrderReceiptNoPrefix;
import com.bcgogo.constant.ProductConstants;
import com.bcgogo.constant.crm.productCategory.ProductCategoryConstant;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.exception.PageException;
import com.bcgogo.payment.dto.RechargeSearchDTO;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.KindDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.model.Product;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.model.ProductWriter;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.model.OrderIndex;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BizStatPrintDTO;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.importexcel.InventoryImporter;
import com.bcgogo.txn.service.importexcel.memberService.MemberServiceImporter;
import com.bcgogo.txn.service.importexcel.order.OrderImporter;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.CustomerServiceJob;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.model.MemberService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TxnService implements ITxnService {

  private static final Logger LOG = LoggerFactory.getLogger(TxnService.class);
  private static final double zero = 0.0001d;//欠款常量
  private IOrderStatusChangeLogService orderStatusChangeLogService;
  private ProductWriter productHistoryService;
  private IServiceHistoryService serviceHistoryService;
  private IPrivilegeService privilegeService;
  private IProductThroughService productThroughService;
  private IProductOutStorageService productOutStorageService;
  private ICustomerDepositService customerDepositService;

  public IProductThroughService getProductThroughService() {
    if (productThroughService == null) {
      productThroughService = ServiceManager.getService(IProductThroughService.class);
    }
    return productThroughService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }

  public IOrderStatusChangeLogService getOrderStatusChangeLogService() {
    return orderStatusChangeLogService == null ?ServiceManager.getService(IOrderStatusChangeLogService.class):orderStatusChangeLogService;
  }

  @Override
  public PurchaseOrderDTO getPurchaseOrder(Long purchaseOrderId, Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseOrder purchaseOrder = new PurchaseOrder();
    List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderById(purchaseOrderId, shopId);
    if (purchaseOrders != null && purchaseOrders.size() > 0) {
      purchaseOrder = purchaseOrders.get(0);
    } else {
      return null;
    }
    if (purchaseOrder != null) {
      PurchaseOrderDTO purchaseOrderDTO = purchaseOrder.toDTO();
      List<PurchaseOrderItem> items = writer.getPurchaseOrderItemsByOrderId(purchaseOrderId);
      PurchaseOrderItemDTO[] itemDTOs = new PurchaseOrderItemDTO[items.size()];
      for (int i = 0; i < items.size(); i++) {
        PurchaseOrderItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
        if(purchaseOrderDTO.getSupplierShopId()!=null){
          if (item.getSupplierProductId() != null) {
            ProductHistoryDTO supplierProductHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(itemDTOs[i].getProductHistoryId(), purchaseOrderDTO.getSupplierShopId());
            ProductDTO supplierProductDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(itemDTOs[i].getSupplierProductId(),purchaseOrderDTO.getSupplierShopId());
            if(supplierProductHistoryDTO!=null){
              itemDTOs[i].setWholesalerProductHistoryDTO(supplierProductHistoryDTO);
            }else{
              itemDTOs[i].setWholesalerProductDTO(supplierProductDTO);
            }
          }
        }else{
          if (item.getProductId() != null && !(new Long(0l)).equals(item.getProductId())) {
            ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(itemDTOs[i].getProductHistoryId(), shopId);
            ProductDTO productDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(itemDTOs[i].getProductId(), purchaseOrderDTO.getShopId());
            if(productHistoryDTO!=null){
              itemDTOs[i].setProductHistoryDTO(productHistoryDTO);
              if(OrderUtil.purchaseOrderInProgress.contains(purchaseOrderDTO.getStatus()) && productDTO!=null){
                itemDTOs[i].setProductUnitRateInfo(productDTO);
              }
            }else{
              itemDTOs[i].setProductDTOWithOutUnit(productDTO);
            }
          }
        }
      }
      purchaseOrderDTO.setItemDTOs(itemDTOs);
      return purchaseOrderDTO;
    }
    return null;
  }

  @Override
  public PurchaseOrderDTO getSimplePurchaseOrder(Long purchaseOrderId, Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseOrder purchaseOrder = new PurchaseOrder();
    List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderById(purchaseOrderId, shopId);
    if (purchaseOrders != null && purchaseOrders.size() > 0) {
      purchaseOrder = purchaseOrders.get(0);
    } else {
      return null;
    }
    if (purchaseOrder != null) {
      PurchaseOrderDTO purchaseOrderDTO = purchaseOrder.toDTO();
      List<PurchaseOrderItem> items = writer.getPurchaseOrderItemsByOrderId(purchaseOrderId);
      PurchaseOrderItemDTO[] itemDTOs = new PurchaseOrderItemDTO[items.size()];
      for (int i = 0; i < items.size(); i++){
        itemDTOs[i] = items.get(i).toDTO();
      }
      purchaseOrderDTO.setItemDTOs(itemDTOs);
      return purchaseOrderDTO;
    }
    return null;
  }


  @Override
  public SalesOrderDTO createOrUpdateSalesOrder(SalesOrderDTO salesOrderDTO, String huankuanTime) throws Exception {
    long current = System.currentTimeMillis();
    SalesOrderItemDTO[] itemDTOs = salesOrderDTO.getItemDTOs();
    if (itemDTOs == null || itemDTOs.length == 0) return salesOrderDTO;
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Object status = writer.begin();
    repairOrderCostCaculator.calculate(salesOrderDTO,null,null);
    try {
      addInventoryForSpecialShopVersion(salesOrderDTO,salesOrderDTO.getShopId(),salesOrderDTO.getShopVersionId(),writer);
      SalesOrder order = null;
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())){
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(salesOrderDTO.getShopId(),salesOrderDTO.getStorehouseId());
        salesOrderDTO.setStorehouseName(storeHouseDTO==null?null:storeHouseDTO.getName());
      }
      if (null == salesOrderDTO.getId() || salesOrderDTO.getId() == 0) {
        order = new SalesOrder();
        order.fromDTO(salesOrderDTO);
        order.setStatusEnum(OrderStatus.SALE_DONE);
        salesOrderDTO.setStatus(OrderStatus.SALE_DONE);
        writer.save(order);
        salesOrderDTO.setId(order.getId());
      } else {
        order = writer.getById(SalesOrder.class, salesOrderDTO.getId());
        order.fromDTO(salesOrderDTO);
        order.setStatusEnum(OrderStatus.SALE_DONE);
        salesOrderDTO.setStatus(OrderStatus.SALE_DONE);
        writer.update(order);
      }
      //结算
      postProcessingForSavingSaleOrder(writer, salesOrderDTO, huankuanTime);
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();

      LOG.info("=======保存销售单--阶段4-1。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      Long newSalesOrderItemId = null;
      for (SalesOrderItemDTO itemDTO : itemDTOs) {
        if (null == itemDTO.getId() || itemDTO.getId() == 0) {
          SalesOrderItem item = new SalesOrderItem();
          if (itemDTO.getProductId() == null) {
            LOG.error("product id is null for when saving sales order");
            continue;
          }
          //更新商品编码
          productService.updateCommodityCodeByProductLocalInfoId(salesOrderDTO.getShopId(),itemDTO.getProductId(),itemDTO.getCommodityCode());
          //更新商品的营业分类
          productService.updateProductLocalInfoCategory(salesOrderDTO.getShopId(),itemDTO.getProductId(),itemDTO.getBusinessCategoryId());
          item.setShopId(salesOrderDTO.getShopId());//SalesOrderItem增加shopId
          item.setAmount(itemDTO.getAmount());
          item.setMemo(itemDTO.getMemo());
          item.setPrice(itemDTO.getPrice());
          item.setProductId(itemDTO.getProductId());
          item.setProductHistoryId(itemDTO.getProductHistoryId());
          item.setTotal(itemDTO.getTotal());
          item.setQuotedPrice(itemDTO.getQuotedPrice());
          item.setSalesOrderId(order.getId());
          item.setCostPrice(NumberUtil.doubleVal(itemDTO.getCostPrice()));
          item.setTotalCostPrice(NumberUtil.doubleVal(itemDTO.getTotalCostPrice()));
          item.setUnit(itemDTO.getUnit());
          item.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
          item.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
          writer.save(item);
          itemDTO.setCostPrice(item.getCostPrice());
          itemDTO.setTotalCostPrice(item.getTotalCostPrice());
          newSalesOrderItemId = item.getId();
        } else {
          SalesOrderItem salesOrderItem = writer.getById(SalesOrderItem.class, itemDTO.getId());
          salesOrderItem.fromDTO(itemDTO);
          salesOrderItem.setSalesOrderId(order.getId());
          salesOrderItem.setShopId(salesOrderDTO.getShopId());
          salesOrderItem.setCostPrice(NumberUtil.doubleVal(itemDTO.getCostPrice()));
          salesOrderItem.setTotalCostPrice(NumberUtil.doubleVal(itemDTO.getTotalCostPrice()));
          salesOrderItem.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
          salesOrderItem.setBusinessCategoryName(itemDTO.getBusinessCategoryName());
          writer.update(salesOrderItem);
          itemDTO.setCostPrice(salesOrderItem.getCostPrice());
          itemDTO.setTotalCostPrice(salesOrderItem.getTotalCostPrice());
          newSalesOrderItemId = salesOrderItem.getId();
        }
        itemDTO.setId(newSalesOrderItemId);
        itemDTO.setSalesOrderId(order.getId());
        //save inventory    getPurchasePrice check the unit

        Inventory inventory = writer.getById(Inventory.class, itemDTO.getProductId());
        iInventoryService.caculateBeforeLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), salesOrderDTO.getShopId());
        Double saleOrderItemAmout = 0d;
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {   //销售单单位是库存大单位
          saleOrderItemAmout = itemDTO.getAmount() * productLocalInfoDTO.getRate();
        } else {
          saleOrderItemAmout = itemDTO.getAmount();
        }
        if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(salesOrderDTO.getShopVersionId())){
          StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(salesOrderDTO.getStorehouseId(),itemDTO.getProductId(),null,saleOrderItemAmout*-1);
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
        }
        if (inventory != null) {
          inventory.setAmount(inventory.getAmount()-saleOrderItemAmout);
          if ((salesOrderDTO.getStatus() == OrderStatus.SALE_DONE || salesOrderDTO.getStatus() == OrderStatus.SALE_DEBT_DONE) && NumberUtil.longValue(inventory.getLastSalesTime()) < salesOrderDTO.getVestDate()) {
            inventory.setLastSalesTime(salesOrderDTO.getVestDate());
          }
          writer.update(inventory);
          iInventoryService.caculateAfterLimit(inventory.toDTO(), salesOrderDTO.getInventoryLimitDTO());
        } else {
          LOG.error("cannot find inventory: product id = " + itemDTO.getProductId());
        }
        ProductDTO productDTO = getProductService().getProductByProductLocalInfoId(inventory.getId(), inventory.getShopId());
        productDTO.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, salesOrderDTO.getVestDateStr()));
        inventorySearchIndexList.add(createInventorySearchIndex(inventory, productDTO));
      }

      LOG.info("=======保存销售单--阶段4-2。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      //此处已过滤空行
      if (CollectionUtils.isNotEmpty(salesOrderDTO.getOtherIncomeItemDTOList())) {
        for (SalesOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : salesOrderDTO.getOtherIncomeItemDTOList()) {
          if (null == orderOtherIncomeItemDTO.getId()) {
            SalesOrderOtherIncomeItem salesOrderOtherIncomeItem = new SalesOrderOtherIncomeItem(orderOtherIncomeItemDTO);
            salesOrderOtherIncomeItem.setShopId(salesOrderDTO.getShopId());
            salesOrderOtherIncomeItem.setOrderId(salesOrderDTO.getId());
            writer.save(salesOrderOtherIncomeItem);
            orderOtherIncomeItemDTO.setId(salesOrderOtherIncomeItem.getId());
            orderOtherIncomeItemDTO.setShopId(salesOrderDTO.getShopId());
          } else {
            SalesOrderOtherIncomeItem salesOrderOtherIncomeItem = writer.getById(SalesOrderOtherIncomeItem.class, orderOtherIncomeItemDTO.getId());
            salesOrderOtherIncomeItem.setMemo(orderOtherIncomeItemDTO.getMemo());
            salesOrderOtherIncomeItem.setName(orderOtherIncomeItemDTO.getName());
            salesOrderOtherIncomeItem.setPrice(orderOtherIncomeItemDTO.getPrice());
            writer.update(salesOrderOtherIncomeItem);
            orderOtherIncomeItemDTO.setShopId(salesOrderDTO.getShopId());
          }

        }
      }

      getProductOutStorageService().productThroughByOrder(salesOrderDTO,OrderTypes.SALE,salesOrderDTO.getStatus(),writer,null);

      writer.commit(status);
      LOG.info("=======保存销售单--阶段4-3。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      ServiceManager.getService(IInventoryService.class).updateInventorySearchIndexAmountWithList(salesOrderDTO.getShopId(), inventorySearchIndexList);
      salesOrderDTO.setId(order.getId());
      LOG.info("=======保存销售单--阶段4-4。执行时间: {} ms", System.currentTimeMillis()-current);
      return salesOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateSaleOrderAndItem(List<SalesOrderDTO> salesOrderDTOList) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    List<ItemIndex> itemIndexList = new ArrayList<ItemIndex>();
    Object status = writer.begin();

    try {
      if (null != salesOrderDTOList && 0 != salesOrderDTOList.size()) {
        for (SalesOrderDTO salesOrderDTO : salesOrderDTOList) {
          if (null != salesOrderDTO.getId()) {
            SalesOrder order = writer.getById(SalesOrder.class, salesOrderDTO.getId());
            order.fromDTO(salesOrderDTO);

            writer.update(order);

            if (null != salesOrderDTO.getItemDTOs() && 0 != salesOrderDTO.getItemDTOs().length) {
              for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
                if (null != salesOrderItemDTO.getId()) {
                  SalesOrderItem salesOrderItem = writer.getById(SalesOrderItem.class, salesOrderItemDTO.getId());
                  salesOrderItem.fromDTO(salesOrderItemDTO);

                  writer.update(salesOrderItem);

                  ItemIndex itemIndex = searchService.getItemIndexByOrderIdAndItemIdAndOrderType(salesOrderItem.getSalesOrderId(), salesOrderItem.getId(), OrderTypes.SALE);
                  if (null == itemIndex) {
                    continue;
                  }
                  itemIndex.setItemCostPrice(salesOrderItem.getCostPrice());
                  itemIndex.setTotalCostPrice(salesOrderItem.getTotalCostPrice());
                  itemIndexList.add(itemIndex);
                }
              }
            }

          }
        }

      }
      writer.commit(status);
      searchService.addOrUpdateItemIndexWithList(itemIndexList, null);


    } finally {
      writer.rollback(status);

    }
  }

  private void postProcessingForSavingSaleOrder(TxnWriter writer, SalesOrderDTO salesOrderDTO, String huankuanTime) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long remindTime = StringUtils.isNotBlank(huankuanTime) ? DateUtil.convertDateStringToDateLong("yyyy-MM-dd", huankuanTime) : null;
    Long shopId = salesOrderDTO.getShopId();
    ReceivableDTO receivableDTO = new ReceivableDTO();
    receivableDTO.setOrderType(OrderTypes.SALE);
    receivableDTO.setStatus(ReceivableStatus.FINISH);
    receivableDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
    receivableDTO.setRemindTime(remindTime);

    receivableDTO.setOrderId(salesOrderDTO.getId());
    receivableDTO.setShopId(shopId);
    receivableDTO.setSettledAmount(salesOrderDTO.getSettledAmount());
    receivableDTO.setDebt(salesOrderDTO.getDebt());
    receivableDTO.setDiscount(salesOrderDTO.getOrderDiscount());
    //实收表 销售单没有存total
    receivableDTO.setTotal(salesOrderDTO.getTotal());
    receivableDTO.setAfterMemberDiscountTotal(salesOrderDTO.getAfterMemberDiscountTotal());
    receivableDTO.setMemberDiscountRatio(salesOrderDTO.getMemberDiscountRatio());
    receivableDTO.setLastPayee(salesOrderDTO.getUserName());
    receivableDTO.setLastPayeeId(salesOrderDTO.getUserId());
    receivableDTO.setCustomerId(salesOrderDTO.getCustomerId());
    receivableDTO.setVestDate(salesOrderDTO.getVestDate());
    receivableDTO.setReceiptNo(salesOrderDTO.getReceiptNo());
    receivableDTO.setBankCheckNo(salesOrderDTO.getBankCheckNo());
    //如果含有会员结算信息 把member_id保存到receivable表中
    if (NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()) > 0 || null != receivableDTO.getMemberDiscountRatio()) {
      String memberNo = salesOrderDTO.getAccountMemberNo();
      Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
      if (member == null) {
        LOG.error("/TxnService.java method=postProcessingForSavingSaleOrder");
        LOG.error("shopId:" + shopId + ",memberNo:" + memberNo);
        LOG.error("会员查询出错");
      } else {
        receivableDTO.setMemberId(member.getId());
        receivableDTO.setMemberNo(member.getMemberNo());
      }
    }

    //添加会员相关
    receivableDTO.setMemberBalancePay(NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()));
    receivableDTO.setCash(NumberUtil.doubleVal(salesOrderDTO.getCashAmount()));  //现金
    receivableDTO.setBankCard(NumberUtil.doubleVal(salesOrderDTO.getBankAmount()));    //银行卡
    receivableDTO.setCheque(NumberUtil.doubleVal(salesOrderDTO.getBankCheckAmount()));    //支票
    receivableDTO.setDeposit(NumberUtil.doubleVal(salesOrderDTO.getCustomerDeposit())); // add by zhj


    ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
    ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
    writer.save(receivableHistory);
    receivableHistoryDTO.setId(receivableHistory.getId());


    if (salesOrderDTO.getSettledAmount() >= 0) {
      ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
      receptionRecordDTO.setReceivableHistoryId(receivableHistory.getId());
      receptionRecordDTO.setDayType(DayType.OTHER_DAY);
      receptionRecordDTO.setAmount(salesOrderDTO.getSettledAmount());
      receptionRecordDTO.setReceivableId(receivableDTO.getId());
      receptionRecordDTO.setOrderTotal(salesOrderDTO.getTotal());
      receptionRecordDTO.setMemberBalancePay(NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()));
      receptionRecordDTO.setChequeNo(salesOrderDTO.getBankCheckNo());
      receptionRecordDTO.setDeposit(NumberUtil.doubleVal(salesOrderDTO.getCustomerDeposit())); //add by zhuj
      receptionRecordDTO.setCash(NumberUtil.doubleVal(salesOrderDTO.getCashAmount()));
      receptionRecordDTO.setBankCard(NumberUtil.doubleVal(salesOrderDTO.getBankAmount()));
      receptionRecordDTO.setCheque(NumberUtil.doubleVal(salesOrderDTO.getBankCheckAmount()));
      receptionRecordDTO.setMemberId(receivableDTO.getMemberId());
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setOriginDebt(0d);
      receptionRecordDTO.setDiscount(salesOrderDTO.getOrderDiscount());
      receptionRecordDTO.setRemainDebt(salesOrderDTO.getDebt());
      receptionRecordDTO.setToPayTime(remindTime);

      receptionRecordDTO.setShopId(salesOrderDTO.getShopId());
      receptionRecordDTO.setOrderId(salesOrderDTO.getId());
      receptionRecordDTO.setReceptionDate(System.currentTimeMillis());
      receptionRecordDTO.setOrderTypeEnum(OrderTypes.SALE);
      receptionRecordDTO.setPayee(salesOrderDTO.getUserName());
      receptionRecordDTO.setPayeeId(salesOrderDTO.getUserId());
//      if(NumberUtil.doubleVal(salesOrderDTO.getDebt()) > 0) {
//        receptionRecordDTO.setOrderStatusEnum(OrderStatus.SALE_DEBT_DONE);
//      }else {
      receptionRecordDTO.setOrderStatusEnum(OrderStatus.SALE_DONE);
//      }
      receptionRecordDTO.setAfterMemberDiscountTotal(salesOrderDTO.getAfterMemberDiscountTotal());
      receptionRecordDTO.setMemberDiscountRatio(salesOrderDTO.getMemberDiscountRatio());
      ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
      receptionRecordDTOs[0] = receptionRecordDTO;
      receivableDTO.setRecordDTOs(receptionRecordDTOs);
    }
    createOrUpdateReceivable(writer, receivableDTO);
    if (receivableDTO.getDeposit() > 0.001){
      customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
      CustomerDepositDTO customerDepositDTO = new CustomerDepositDTO();
      customerDepositDTO.setOperator(salesOrderDTO.getUserName());
      customerDepositDTO.setShopId(salesOrderDTO.getShopId());
      customerDepositDTO.setActuallyPaid(receivableDTO.getDeposit());
      customerDepositDTO.setCustomerId(receivableDTO.getCustomerId());
      DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
      //基础字段在service方法中有填充
      depositOrderDTO.setDepositType(DepositType.SALES.getScene());
      depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
      depositOrderDTO.setRelatedOrderId(salesOrderDTO.getId()); // 记录销售单id
      depositOrderDTO.setRelatedOrderNo(salesOrderDTO.getReceiptNo()); // 记录销售单按单据号

      customerDepositService.customerDepositUse(customerDepositDTO, depositOrderDTO, writer);
    }

    //添加欠款信息
    if (salesOrderDTO.getDebt() > 0.001) {
      Long payTime = salesOrderDTO.getVestDate();
      StringBuffer materials = new StringBuffer();
      for (int i = 0; i < salesOrderDTO.getItemDTOs().length; i++) {
        if (salesOrderDTO.getItemDTOs()[i].getProductId() != null) {
          materials.append(salesOrderDTO.getItemDTOs()[i].getProductName()).append(",");
        } else {
          materials.append(salesOrderDTO.getItemDTOs()[i].getProductName());
        }
      }
      Debt debt = new Debt();
      debt.setOrderTypeEnum(OrderTypes.SALE);
      debt.setContent(BcgogoI18N.getMessageByKey("debt.type.sales"));
      debt.setCustomerId(salesOrderDTO.getCustomerId());
      debt.setDebt(receivableDTO.getDebt());
      debt.setMaterial(materials.toString());
      debt.setService(" ");
      debt.setOrderId(salesOrderDTO.getId());
      debt.setOrderTime(salesOrderDTO.getEditDate());
      debt.setRecievableId(receivableDTO.getId());
      debt.setSettledAmount(salesOrderDTO.getSettledAmount());
      debt.setShopId(shopId);
      debt.setTotalAmount(salesOrderDTO.getTotal());
      debt.setVehicleNumber(" ");
      debt.setPayTime(payTime);
      debt.setRemindTime(remindTime);
      debt.setStatusEnum(DebtStatus.ARREARS);
      debt.setReceiptNo(salesOrderDTO.getReceiptNo());
      debt.setRemindStatus(UserConstant.Status.ACTIVITY);
      writer.save(debt);
      // add by WLF 在提醒总表中保存提醒
      saveRemindEvent(writer, debt, salesOrderDTO.getCustomer(), salesOrderDTO.getMobile());
      // add by WLF 更新缓存
      updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, debt.getShopId());
    }

  }

  @Override
  public SalesOrderDTO getSalesOrder(Long salesOrderId, Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    SalesOrder salesOrder;
    List<SalesOrder> salesOrders = writer.getSalesOrderById(salesOrderId, shopId);
    if (salesOrders != null && salesOrders.size() > 0) {
      salesOrder = salesOrders.get(0);
    } else {
      return null;
    }
    if (salesOrder != null) {
      SalesOrderDTO salesOrderDTO = salesOrder.toDTO();

      List<SalesOrderItem> items = writer.getSalesOrderItemsByOrderId(salesOrderId);
      SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[items.size()];
      salesOrderDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        SalesOrderItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }

      List<SalesOrderOtherIncomeItem> salesOrderOtherIncomeItemList = writer.getSaleOtherIncomeItemByOrderId(shopId,salesOrderId);
      List<SalesOrderOtherIncomeItemDTO> otherIncomeItemDTOList = null;
      if(CollectionUtils.isNotEmpty(salesOrderOtherIncomeItemList)) {
        otherIncomeItemDTOList = new ArrayList<SalesOrderOtherIncomeItemDTO>();
        for (SalesOrderOtherIncomeItem orderOtherIncomeItem : salesOrderOtherIncomeItemList) {
          otherIncomeItemDTOList.add(orderOtherIncomeItem.toDTO());
        }
      }
      salesOrderDTO.setOtherIncomeItemDTOList(otherIncomeItemDTOList);

      return salesOrderDTO;
    }
    return null;
  }

  @Override
  public SalesOrderDTO getSalesOrderByPurchaseOrderId(Long purchaseOrderId, Long supplierShopId) throws Exception {
    if(purchaseOrderId == null || supplierShopId == null){
      return new SalesOrderDTO();
    }
    TxnWriter writer = txnDaoManager.getWriter();
    SalesOrder salesOrder;
    List<SalesOrder> salesOrders = writer.getSalesOrderByPurchaseOrderId(purchaseOrderId, supplierShopId);
    if (CollectionUtils.isNotEmpty(salesOrders)) {
      salesOrder = salesOrders.get(0);
    } else {
      return new SalesOrderDTO();
    }
    if (salesOrder != null) {
      SalesOrderDTO salesOrderDTO = salesOrder.toDTO();

      List<SalesOrderItem> items = writer.getSalesOrderItemsByOrderId(salesOrder.getId());
      SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[items.size()];
      salesOrderDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        SalesOrderItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      return salesOrderDTO;
    }
    return new SalesOrderDTO();
  }

  /**
   * 此方法仅用于店面初始化
   *
   * @param salesOrderId
   * @return
   * @throws Exception
   */
  @Override
  public SalesOrderDTO getSalesOrder(Long salesOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    SalesOrder salesOrder = writer.getById(SalesOrder.class, salesOrderId);
    if (salesOrder != null) {
      SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
      salesOrderDTO.setId(salesOrder.getId());

      List<SalesOrderItem> items = writer.getSalesOrderItemsByOrderId(salesOrderId);
      SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[items.size()];
      salesOrderDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        SalesOrderItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      return salesOrderDTO;
    }
    return null;
  }


  public void savePurchasePrice(TxnWriter txnWriter, PurchasePriceDTO purchasePriceDTO) {
    PurchasePrice purchasePrice = new PurchasePrice();
    purchasePrice.fromDTO(purchasePriceDTO);
    txnWriter.save(purchasePrice);
  }

  @Override
  public RepairOrderDTO getRepairOrder(Long repairOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    RepairOrder repairOrder = writer.getById(RepairOrder.class, repairOrderId);
    if (repairOrder == null) {
      return null;
    }
    RepairOrderDTO repairOrderDTO = repairOrder.toDTO();
    ReceivableDTO receivableDTO = getReceivableByShopIdOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
    if (receivableDTO != null) {
      repairOrderDTO.setDebt(receivableDTO.getDebt());
      repairOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
    } else {
      repairOrderDTO.setDebt(0.0);
      repairOrderDTO.setSettledAmount(repairOrderDTO.getTotal());
    }
    repairOrderDTO.setDiscount(NumberUtil.round(repairOrderDTO.getTotal() - repairOrderDTO.getSettledAmount() - repairOrderDTO.getDebt(), NumberUtil.MONEY_PRECISION));
    List<RepairOrderItem> items = writer.getRepairOrderItemsByOrderId(repairOrderId);
    RepairOrderItemDTO[] itemDTOs = new RepairOrderItemDTO[items.size()];
    repairOrderDTO.setItemDTOs(itemDTOs);
    for (int i = 0; i < items.size(); i++) {
      RepairOrderItem item = items.get(i);
      itemDTOs[i] = item.toDTO();
    }

    List<RepairOrderService> services = writer.getRepairOrderServicesByOrderId(repairOrderId);
    RepairOrderServiceDTO[] serviceDTOs = new RepairOrderServiceDTO[services.size()];
    repairOrderDTO.setServiceDTOs(serviceDTOs);
    for (int i = 0; i < services.size(); i++) {
      RepairOrderService service = services.get(i);
      serviceDTOs[i] = new RepairOrderServiceDTO();
      serviceDTOs[i] = service.toDTO();
    }
    return repairOrderDTO;
  }

  @Override
  public List<SalesOrderItem> getSaleOrderItemListByOrderId(Long saleOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesOrderItem> salesOrderItemList = writer.getSalesOrderItemsByOrderId(saleOrderId);

    return salesOrderItemList;
  }

  @Override
  public List<RepairOrderItem> getRepairOrderItemByRepairOrderId(Long repairOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderItem> repairOrderItemList = writer.getRepairOrderItemsByOrderId(repairOrderId);

    return repairOrderItemList;
  }

  public IProductService getProductService() {
    if (productService == null) {
      productService = ServiceManager.getService(IProductService.class);
    }
    return productService;
  }

  private IProductService productService = null;

  @Override
  public void updateRepairOrderAndItem(List<RepairOrderDTO> repairOrderDTOList) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    ISearchService searchService = ServiceManager.getService(SearchService.class);
    List<ItemIndex> itemIndexList = new ArrayList<ItemIndex>();
    Object status = writer.begin();
    try {
      if (null != repairOrderDTOList && 0 != repairOrderDTOList.size()) {
        for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
          if (null != repairOrderDTO.getId()) {
            RepairOrder order = writer.getById(RepairOrder.class, repairOrderDTO.getId());

            order.fromDTO(repairOrderDTO);

            writer.update(order);

            if (null != repairOrderDTO.getItemDTOs() && 0 != repairOrderDTO.getItemDTOs().length) {
              for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
                if (null != repairOrderItemDTO.getId()) {
                  RepairOrderItem repairOrderItem = writer.getById(RepairOrderItem.class, repairOrderItemDTO.getId());

                  repairOrderItem.fromDTO(repairOrderItemDTO);

                  writer.update(repairOrderItem);

                  ItemIndex itemIndex = searchService.getItemIndexByOrderIdAndItemIdAndOrderType(repairOrderItem.getRepairOrderId(), repairOrderItem.getId(), OrderTypes.REPAIR);
                  if (itemIndex == null) {
                    continue;
                  }
                  itemIndex.setItemCostPrice(repairOrderItem.getCostPrice());
                  itemIndex.setTotalCostPrice(repairOrderItem.getTotalCostPrice());
                  itemIndexList.add(itemIndex);
                }
              }
            }

          }
        }

      }

      writer.commit(status);

      searchService.addOrUpdateItemIndexWithList(itemIndexList, null);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public RepairOrderDTO getUnbalancedAccountRepairOrderByVehicleNumber(Long shopId, String vehicleNumber, Long orderId)
    throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    VehicleDTO vehicleDTO = CommonUtil.first(userService.getVehicleByLicenceNo(shopId, vehicleNumber));
    RepairOrder repairOrder = (vehicleDTO == null ? null : writer.getUnbalancedAccountRepairOrderByVehicleNumber(shopId, vehicleDTO.getId(), orderId));
    if (repairOrder != null) {
      RepairOrderDTO repairOrderDTO = repairOrder.toDTO();

      List<RepairOrderItem> items = writer.getRepairOrderItemsByOrderId(repairOrder.getId());

      Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(shopId,
        repairOrderDTO.getProductIdList().toArray(new Long[repairOrderDTO.getProductIdList().size()]));
      RepairOrderItemDTO[] itemDTOs = new RepairOrderItemDTO[items.size()];
      repairOrderDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        RepairOrderItem item = items.get(i);
        itemDTOs[i] = item.toDTO();

        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(item.getProductId());
        if (null != productLocalInfoDTO) {
          if (UnitUtil.isStorageUnit(itemDTOs[i].getUnit(), productLocalInfoDTO)) {
            itemDTOs[i].setPurchasePrice(productLocalInfoDTO.getPurchasePrice() == null ?
              0d : productLocalInfoDTO.getPurchasePrice() * productLocalInfoDTO.getRate());
          } else {
            itemDTOs[i].setPurchasePrice(productLocalInfoDTO.getPurchasePrice() == null ? 0d : productLocalInfoDTO.getPurchasePrice());
          }
        }
      }

      List<RepairOrderService> services = writer.getRepairOrderServicesByOrderId(repairOrder.getId());
      RepairOrderServiceDTO[] serviceDTOs = new RepairOrderServiceDTO[services.size()];
      repairOrderDTO.setServiceDTOs(serviceDTOs);
      for (int i = 0; i < services.size(); i++) {
        RepairOrderService service = services.get(i);
        serviceDTOs[i] = service.toDTO();
      }

      List<RepairOrderOtherIncomeItem> otherIncomeItemList = writer.getRepairOtherIncomeItemByOrderId(shopId,repairOrder.getId());

      if(CollectionUtils.isNotEmpty(otherIncomeItemList))
      {
        List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList = new ArrayList<RepairOrderOtherIncomeItemDTO>();
        for(RepairOrderOtherIncomeItem item : otherIncomeItemList)
        {
          otherIncomeItemDTOList.add(item.toDTO());
        }
        repairOrderDTO.setOtherIncomeItemDTOList(otherIncomeItemDTOList);
      }

      return repairOrderDTO;
    }
    return null;
  }


  public List<RepairOrderOtherIncomeItemDTO>  getRepairOtherIncomeItemByOrderId(Long shopId,Long orderId){
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderOtherIncomeItem> otherIncomeItemList = writer.getRepairOtherIncomeItemByOrderId(shopId,orderId);
    if(CollectionUtil.isEmpty(otherIncomeItemList)) return null;
    List<RepairOrderOtherIncomeItemDTO> itemDTOs=new ArrayList<RepairOrderOtherIncomeItemDTO>();
    for(RepairOrderOtherIncomeItem  item:otherIncomeItemList){
      itemDTOs.add(item.toDTO());
    }
    return itemDTOs;
  }

  public ReceivableDTO createOrUpdateReceivable(TxnWriter writer, ReceivableDTO receivableDTO) throws Exception {
    Receivable receivable = null;
    receivable = new Receivable();
    if (null == receivableDTO.getId()) {
      if (null == receivable.getAfterMemberDiscountTotal()) {
        receivable.setAfterMemberDiscountTotal(receivableDTO.getTotal());
      }
      receivable.fromDTO(receivableDTO);
      writer.save(receivable);
    } else {
      receivable = writer.getById(Receivable.class, receivableDTO.getId());
      receivable.setSettledAmount(receivableDTO.getSettledAmount());
      receivable.setDebt(receivableDTO.getDebt());
      receivable.setLastPayeeId(receivableDTO.getLastPayeeId());
      receivable.setLastPayee(receivableDTO.getLastPayee());
      if(null == receivable.getAfterMemberDiscountTotal())
      {
        receivable.setAfterMemberDiscountTotal(receivableDTO.getTotal());
      }
      if(receivableDTO.getStatus() != null){
        receivable.setStatusEnum(receivableDTO.getStatus());
      }
      writer.update(receivable);
    }
    ReceptionRecordDTO recordDTOs[] = receivableDTO.getRecordDTOs();
    if (recordDTOs != null) {
      for (ReceptionRecordDTO recordDTO : recordDTOs) {
        ReceptionRecord record = new ReceptionRecord();

        record.fromDTO(recordDTO);

        if(null == record.getAfterMemberDiscountTotal())
        {
          record.setAfterMemberDiscountTotal(record.getOrderTotal());
        }
        record.setReceivableId(receivable.getId());
        writer.save(record);
      }
    }
    receivableDTO.setId(receivable.getId());
    return receivableDTO;
  }


  @Override
  public ReceivableDTO getReceivableByShopIdAndOrderTypeAndOrderId(Long shopId, OrderTypes orderType, Long orderId)
    throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Receivable receivable = writer.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, orderType, orderId);
    if (null != receivable) {
      ReceivableDTO receivableDTO = receivable.toDTO();

      List<ReceptionRecord> records = writer.getReceptionRecordsByReceivalbeId(receivableDTO.getId());
      if(CollectionUtils.isNotEmpty(records)) {
        ReceptionRecordDTO[] recordDTOs = new ReceptionRecordDTO[records.size()];
        receivableDTO.setRecordDTOs(recordDTOs);
        for (int i = 0; i < records.size(); i++) {
          ReceptionRecord record = records.get(i);
          recordDTOs[i] = record.toDTO();
        }
      }
      return receivableDTO;
    }
    return null;
  }

  @Override
  public InventoryDTO getInventoryByShopIdAndProductId(Long shopId, Long productId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Inventory inventory = writer.getInventoryByIdAndshopId(productId, shopId);
    if (inventory != null) {
      InventoryDTO inventoryDTO = inventory.toDTO();
      return inventoryDTO;
    }
    return null;
  }

  @Override
  public List<InventoryDTO> getInventoryByShopIdAndProductIds(Long shopId, Long... productId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Inventory> inventoryList = writer.getInventoryByshopIdAndProductIds(shopId, productId);
    if (CollectionUtils.isNotEmpty(inventoryList)) {
      List<InventoryDTO> inventoryDTOList = new ArrayList<InventoryDTO>();
      for (Inventory inventory : inventoryList) {
        inventoryDTOList.add(inventory.toDTO());
      }
      return inventoryDTOList;
    }
    return null;
  }

  @Override
  public InventoryDTO createOrUpdateInventory(InventoryDTO inventoryDTO) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Inventory inventory = new Inventory();
      if (null == inventoryDTO.getId()) {
        inventory.fromDTO(inventoryDTO);
        writer.save(inventory);
      } else {
        inventory = writer.getById(Inventory.class, inventoryDTO.getId());
        if (inventory == null) {
          inventory = new Inventory();
        }
        inventory.setId(inventoryDTO.getId());
        inventory.setShopId(inventoryDTO.getShopId());
        inventory.setAmount(inventoryDTO.getAmount());
        inventory.setUnit(inventoryDTO.getUnit());
        inventory.setUpperLimit(inventoryDTO.getUpperLimit());
        inventory.setLowerLimit(inventoryDTO.getLowerLimit());
        inventory.setNoOrderInventory(inventoryDTO.getNoOrderInventory());
        inventory.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
        inventory.setSalesPrice(inventoryDTO.getSalesPrice());
        inventory.setLatestInventoryPrice(inventoryDTO.getLatestInventoryPrice());
        inventory.setLastStorageTime(inventoryDTO.getLastStorageTime());
        writer.saveOrUpdate(inventory);
      }
      writer.commit(status);
      inventoryDTO.setId(inventory.getId());
      return inventoryDTO;
    } catch (Exception e) {
      throw new BcgogoException(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public InventoryDTO getInventoryAmount(Long shopId, Long productId) throws Exception {
    if (shopId == null || productId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Inventory inventory = writer.getInventoryByIdAndshopId(productId, shopId);
    if (inventory != null) {
      return inventory.toDTO();
    }
    return null;
  }


  //ToDo: why use direct SQL to update?
  @Override
  public boolean updateRepairRemindEventByShopIdAndTypeAndProductId(Long shopId, RepairRemindEventTypes eventType, Long productId,
                                                                    RepairRemindEventTypes targetEventType, Long repairOrderId)
    throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      boolean bl = writer.updateRepairRemindEventByShopIdAndTypeAndProductId(shopId, eventType, productId,
        targetEventType, repairOrderId);
      writer.updateRemindEventByShopIdEventTypeObjectId(shopId, RemindEventType.REPAIR.toString(), RepairRemindEventTypes.INCOMING.toString(), productId);
      writer.commit(status);
      return bl;
    } finally {
      writer.rollback(status);
    }
  }



  @Override
  public List<RepairRemindEventDTO> getRepairRemindEvents(Long shopId, RepairRemindEventTypes eventType, Long pagNo, Long pageSize)
    throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEvents(shopId, eventType, pagNo, pageSize);

    if(CollectionUtils.isEmpty(repairRemindEvents))
    {
      return null;
    }

    List<RepairRemindEventDTO> repairRemindEventDTOs = new ArrayList<RepairRemindEventDTO>();
    Set<Long> repairOrderIds = new HashSet<Long>();
    for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
      if(repairRemindEvent.getRepairOrderId() != null){
        repairOrderIds.add(repairRemindEvent.getRepairOrderId());
      }
    }
    Map<Long,RepairOrderDTO> repairOrderDTOMap = getRepairOrderMapByShopIdAndOrderIds(shopId,
      repairOrderIds.toArray(new Long[repairOrderIds.size()]));
    for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
      RepairRemindEventDTO repairRemindEventDTO = repairRemindEvent.toDTO();
      RepairOrderDTO ro = repairOrderDTOMap.get(repairRemindEvent.getRepairOrderId());
      if(null == ro)
      {
        LOG.warn("repairRemindEvent中施工单id为"+repairRemindEvent.getRepairOrderId().toString()+" 在施工单中没找到对应单子");
      }
      repairRemindEventDTO.setReceiptNo(null==ro?"":ro.getReceiptNo());
      repairRemindEventDTO.setCreateTime(repairRemindEvent.getCreationDate());
      repairRemindEventDTOs.add(repairRemindEventDTO);
    }
    return repairRemindEventDTOs;
  }

  public int countRepairRemindEvents(Long shopId, RepairRemindEventTypes eventType) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countRepairRemindEvents(shopId, eventType);
  }

  @Override
  public List<RepairRemindEventDTO> getRepairRemindEventByShopIdAndOrderIdAndType(Long shopId, Long repairOrderId,
                                                                                  RepairRemindEventTypes eventType)
    throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByShopIdAndOrderIdAndType(shopId,
      repairOrderId, eventType);
    List<RepairRemindEventDTO> repairRemindEventDTOs = new ArrayList<RepairRemindEventDTO>();
    if (repairRemindEventDTOs != null) {
      for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
        RepairRemindEventDTO repairRemindEventDTO = repairRemindEvent.toDTO();
        repairRemindEventDTOs.add(repairRemindEventDTO);
      }
      return repairRemindEventDTOs;
    }

    return null;
  }


  @Override
  public boolean deleteInventoryRemindEventByShopIdAndPurchaseOrderId(Long shopId, Long purchaseOrderId)
    throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      boolean bl = writer.deleteInventoryRemindEventByShopIdAndPurchaseOrderId(shopId, purchaseOrderId);
      writer.commit(status);
      return bl;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<InventoryRemindEventDTO> getInventoryRemindEventDTOByPurchaseOrderId(Long shopId, Long purchaseOrderId) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventoryRemindEvent> inventoryRemindEvents = writer.getInventoryRemindEventByPurchaseOrderId(shopId, purchaseOrderId);
    List<InventoryRemindEventDTO> inventoryRemindEventDTOs = new ArrayList<InventoryRemindEventDTO>();
    if(CollectionUtils.isNotEmpty(inventoryRemindEvents)){
      for(InventoryRemindEvent inventoryRemindEvent :inventoryRemindEvents ){
        inventoryRemindEventDTOs.add(inventoryRemindEvent.toDTO());
      }
    }
    return inventoryRemindEventDTOs;
  }

  @Override
  public List<RepairRemindEvent> getRepairRemindEventByShopId(Long shopId, RepairRemindEventTypes eventType, Long[] productId, int
    pageNo, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getRepairRemindEventByShopId(shopId, eventType, productId, pageNo, pageSize);

  }


  @Override
  public int countRepairRemindEventByShopId(Long shopId, RepairRemindEventTypes eventType, Long[] productId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countRepairRemindEventByShopId(shopId, eventType, productId);
  }

  @Override
  public List<LackMaterialDTO> getLackMaterialByProductId(Long shopId, RepairRemindEventTypes eventType, Long productId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairRemindEvent> repairRemindEvents = writer.getLackMaterialByProductId(shopId, eventType, productId);
    List<LackMaterialDTO> lackMaterialDTOs = new ArrayList<LackMaterialDTO>();
    for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
      lackMaterialDTOs.add(repairRemindEvent.toLackMaterialDTO());
    }
    return lackMaterialDTOs;
  }

  @Override
  public List<LackMaterialDTO> getLackMaterialByProductIdAndStorehouse(Long shopId, RepairRemindEventTypes eventType, Long productId,Long storehouseId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairRemindEvent> repairRemindEvents = writer.getLackMaterialByProductIdAndStorehouse(shopId, eventType, productId,storehouseId);
    List<LackMaterialDTO> lackMaterialDTOs = new ArrayList<LackMaterialDTO>();
    for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
      lackMaterialDTOs.add(repairRemindEvent.toLackMaterialDTO());
    }
    return lackMaterialDTOs;
  }

  @Override
  public Map<Long, List<LackMaterialDTO>> getLackMaterialMapByProductIds(Long shopId, RepairRemindEventTypes eventType,
                                                                         Set<Long> productIds) throws Exception {
    Map<Long, List<LackMaterialDTO>> lackMaterialMap = new HashMap<Long, List<LackMaterialDTO>>();
    if (shopId == null || CollectionUtils.isEmpty(productIds)) {
      return lackMaterialMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairRemindEvent> repairRemindEvents = writer.getLackMaterialByProductIds(shopId, eventType, productIds);
    if (CollectionUtils.isNotEmpty(repairRemindEvents)) {
      for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
        if (repairRemindEvent.getProductId() != null) {
          List<LackMaterialDTO> lackMaterialDTOs = lackMaterialMap.get(repairRemindEvent.getProductId());
          if (lackMaterialDTOs == null) {
            lackMaterialDTOs = new ArrayList<LackMaterialDTO>();
          }
          lackMaterialDTOs.add(repairRemindEvent.toLackMaterialDTO());
          lackMaterialMap.put(repairRemindEvent.getProductId(), lackMaterialDTOs);
        }
      }
    }
    return lackMaterialMap;
  }

  @Override
  public Map<Long, List<LackMaterialDTO>> getLackMaterialMapByProductIdsAndStorehouse(Long shopId,
                                                                                      RepairRemindEventTypes eventType, Set<Long> productIds, Long storehouseId) throws Exception {
    Map<Long, List<LackMaterialDTO>> lackMaterialMap = new HashMap<Long, List<LackMaterialDTO>>();
    if (shopId == null || CollectionUtils.isEmpty(productIds)) {
      return lackMaterialMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairRemindEvent> repairRemindEvents = writer.getLackMaterialByProductIdsAndStorehouse(shopId, eventType, productIds,storehouseId);
    if (CollectionUtils.isNotEmpty(repairRemindEvents)) {
      for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
        if (repairRemindEvent.getProductId() != null) {
          List<LackMaterialDTO> lackMaterialDTOs = lackMaterialMap.get(repairRemindEvent.getProductId());
          if (lackMaterialDTOs == null) {
            lackMaterialDTOs = new ArrayList<LackMaterialDTO>();
          }
          lackMaterialDTOs.add(repairRemindEvent.toLackMaterialDTO());
          lackMaterialMap.put(repairRemindEvent.getProductId(), lackMaterialDTOs);
        }
      }
    }
    return lackMaterialMap;
  }

  @Override
  public List<ScheduleServiceEventDTO> getScheduleServiceEventByShopIdAndCustomerIdAndVehicleId(Long shopId,
                                                                                                Long customerId,
                                                                                                Long vehicleId)
    throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<ScheduleServiceEvent> scheduleServiceEvents =
        writer.getScheduleServiceEventByShopIdAndCustomerIdAndVehicleId(shopId, customerId, vehicleId);
      List<ScheduleServiceEventDTO> scheduleServiceEventDTOs = new ArrayList<ScheduleServiceEventDTO>();
      if (scheduleServiceEvents != null) {
        for (ScheduleServiceEvent scheduleServiceEvent : scheduleServiceEvents) {
          ScheduleServiceEventDTO scheduleServiceEventDTO = scheduleServiceEvent.toDTO();
          scheduleServiceEventDTOs.add(scheduleServiceEventDTO);
        }
        return scheduleServiceEventDTOs;
      }
      writer.commit(status);
    } catch (Exception e) {
      throw new BcgogoException(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
    return null;
  }


  @Override
  public ScheduleServiceEventDTO updateScheduleServiceEvent(ScheduleServiceEventDTO scheduleServiceEventDTO)
    throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ScheduleServiceEvent scheduleServiceEvent = null;
      if (scheduleServiceEvent == null) {
        scheduleServiceEvent = writer.getById(ScheduleServiceEvent.class, scheduleServiceEventDTO.getId());
        scheduleServiceEvent.fromDTO(scheduleServiceEventDTO);
        writer.update(scheduleServiceEvent);
      }
      writer.commit(status);
      return scheduleServiceEventDTO;
    } catch (Exception e) {
      throw new BcgogoException(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ScheduleServiceEventDTO createScheduleServiceEvent(ScheduleServiceEventDTO scheduleServiceEventDTO)
    throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ScheduleServiceEvent scheduleServiceEvent = null;
      if (scheduleServiceEvent == null) {
        scheduleServiceEvent = new ScheduleServiceEvent();
        scheduleServiceEvent.fromDTO(scheduleServiceEventDTO);
        writer.save(scheduleServiceEvent);
      }
      writer.commit(status);
      scheduleServiceEventDTO.setId(scheduleServiceEvent.getId());
      return scheduleServiceEventDTO;
    } catch (Exception e) {
      throw new BcgogoException(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ProductRemindEventDTO createOrUpdateProductRemindEvent(ProductRemindEventDTO productRemindEventDTO)
    throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ProductRemindEvent productRemindEvent = new ProductRemindEvent();
      if (null == productRemindEventDTO.getId()) {
        productRemindEvent.fromDTO(productRemindEventDTO);
        writer.save(productRemindEvent);
      } else {
        productRemindEvent = writer.getById(ProductRemindEvent.class, productRemindEventDTO.getId());
        productRemindEvent.setProductId(productRemindEventDTO.getProductId());
        writer.update(productRemindEvent);
      }
      writer.commit(status);
      productRemindEventDTO.setId(productRemindEvent.getId());
      return productRemindEventDTO;
    } catch (Exception e) {
      throw new BcgogoException(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  public List<DebtDTO> getDebtsByShopIdAndCustomerId(Long shopId, Long customerId) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    List<DebtDTO> debtDTOList = new ArrayList<DebtDTO>();
    if (customerId != null && shopId != null) {
      List<Debt> debtList = writer.getAllDebtsByCustomerIds(shopId, new Long[]{customerId});
      if (null != debtList && debtList.size() > 0) {
        for (int i = 0; i < debtList.size(); i++) {
          Debt debt = debtList.get(i);
          DebtDTO debtDTO = debt.toDTO();
          debtDTO.setId(debt.getId().toString() != null ? debt.getId().toString() : "");
        }
      }
    }
    return debtDTOList;
  }


  public DebtDTO getDebtByShopIdAndCustomerIdAndOrderId(Long shopId, Long customerId, Long orderId) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    if (customerId != null && shopId != null) {
      Debt debt = writer.getDebtByShopIdAndCustomerIdAndOrderId(shopId, customerId, orderId);
      if (debt != null) {
        DebtDTO debtDTO = debt.toDTO();
        debtDTO.setId(debt.getId().toString() != null ? debt.getId().toString() : "");
        if (null != debt.getOrderTime()) {
          SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
          Timestamp now = new Timestamp(debt.getOrderTime());
          debtDTO.setDate(fmt.format(now));
        }
        return debtDTO;
      }
    }
    return null;
  }

  @Override
  public WashOrderDTO createWashOrder(WashOrderDTO washOrderDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //创建洗车单
      WashOrder washOrder = new WashOrder();
      washOrder.fromDTO(washOrderDTO);
      writer.save(washOrder);
      washOrderDTO.setId(washOrder.getId());
      //判断洗车类型
      long orderType = washOrder.getOrderType();
      //如果不是会员卡洗车，保存收款纪录
      if (orderType != 1) {
        Receivable receivable = new Receivable();
        receivable.setOrderId(washOrder.getId());
        receivable.setOrderType(3l);
        receivable.setShopId(washOrder.getShopId());
        receivable.setSettledAmount(washOrder.getCashNum());
        //实收表 洗车单 不存在折扣和欠款 debet discount存为0 total存为洗车单金额
        receivable.setDebt(0.0);
        receivable.setDiscount(0.0);
        receivable.setTotal(washOrder.getCashNum());
        writer.save(receivable);
      }
      writer.commit(status);
      return washOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<WashOrderDTO> getCustomerWashOrders(long customerId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<WashOrder> washOrders = writer.getCustomerWashOrders(customerId);
    List<WashOrderDTO> washOrderDTOs = null;
    if (washOrders != null && washOrders.size() > 0) {
      washOrderDTOs = new ArrayList<WashOrderDTO>();
      for (int i = 0; i < washOrders.size(); i++) {
        WashOrderDTO washOrderDTO = washOrders.get(i).toDTO();
        washOrderDTO.setCreationDate(DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm", washOrders.get(i).getCreationDate()));
        washOrderDTOs.add(washOrderDTO);
      }
    }

    return washOrderDTOs;
  }

  /*
  查询客户当天洗车次数
   */
  @Override
  public int getTodayWashTimes(long customerId) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getTodayWashTimes(customerId);
  }


  @Override
  public List<DebtDTO> getDebtByShopIdAndOrderId(Long shopId, Long orderId)
    throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Debt> debts = writer.getDebtByShopIdAndOrderId(shopId, orderId);
    List<DebtDTO> DebtDTOs = new ArrayList<DebtDTO>();
    if (debts != null && debts.size() > 0) {
      for (Debt debt : debts) {
        DebtDTO debtDTO = debt.toDTO();
        DebtDTOs.add(debtDTO);
      }
      return DebtDTOs;
    }
    return null;
  }


  public int countNoSettlementRepairOrder(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    int count = writer.countNoSettlementRepairOrder(shopId);
    return count;
  }


  public int countInventoryRemindEventNumber(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    int count = writer.countInventoryRemindEventNumber(shopId);
    return count;
  }

  public int countInventoryNumber(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    int count = writer.countInventoryNumber(shopId);
    return count;
  }



  @Override
  public List<InventoryRemindEventDTO> getInventoryRemindEventByShopIdAndPageNoAndPageSize(Long shopId, Integer pageNo, Integer pageSize) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventoryRemindEvent> inventoryRemindEvents = writer.getInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId, pageNo, pageSize);
    List<InventoryRemindEventDTO> inventoryRemindEventDTOs = new ArrayList<InventoryRemindEventDTO>();
    if (inventoryRemindEvents != null) {
      for (InventoryRemindEvent inventoryRemindEvent : inventoryRemindEvents) {
        InventoryRemindEventDTO inventoryRemindEventDTO = inventoryRemindEvent.toDTO();
        inventoryRemindEventDTO.setCreatedTime(inventoryRemindEvent.getCreationDate());
        inventoryRemindEventDTOs.add(inventoryRemindEventDTO);
      }
      return inventoryRemindEventDTOs;
    }
    return null;
  }


  @Override
  public int countInventoryRemindEventByShopIdAndPageNoAndPageSize(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId);
  }

  @Override
  public List<ServiceDTO> getServiceByShopIdAndSearchKey(Long shopId, String searchKey) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.getServiceByShopIdAndSearchKey(shopId, searchKey);
    List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();
    if (services != null) {
      for (Service service : services) {
        ServiceDTO serviceDTO = service.toDTO();
        serviceDTOs.add(serviceDTO);
      }
    }
    return serviceDTOs;
  }

  @Override
  public ServiceDTO getServiceById(Long id) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if(null == id)
    {
      return null;
    }

    Service service = writer.getById(Service.class, id);
    ServiceDTO serviceDTO = null;

    if (null != service) {
      serviceDTO = service.toDTO();
    }
    return serviceDTO;
  }

  @Override
  public List<ServiceDTO> getServiceDTOById(Long shopId,Long... serviceIds){
    List<ServiceDTO> serviceDTOs=new ArrayList<ServiceDTO>();
    if(ArrayUtil.isEmpty(serviceIds)) return serviceDTOs;
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> serviceList = writer.getServiceListById(shopId, serviceIds);
    if(CollectionUtil.isEmpty(serviceList)) return serviceDTOs;
    for(Service service:serviceList){
      if(service==null) continue;
      serviceDTOs.add(service.toDTO());
    }
    return serviceDTOs;
  }

  @Override
  public Map<Long,ServiceDTO> getServiceDTOMapById(Long shopId,Long... serviceIds){
    Map<Long,ServiceDTO> serviceDTOMap=new HashMap<Long, ServiceDTO>();
    List<ServiceDTO> serviceDTOs= getServiceDTOById(shopId,serviceIds);
    if(CollectionUtil.isEmpty(serviceDTOs)) return serviceDTOMap;
    for(ServiceDTO serviceDTO:serviceDTOs){
      serviceDTOMap.put(serviceDTO.getId(),serviceDTO);
    }
    return serviceDTOMap;
  }

  @Override
  public List<MemberCardServiceDTO> saveServicesByMemberCardDTO(MemberCardDTO memberCardDTO) {
    if (null == memberCardDTO || null == memberCardDTO.getMemberCardServiceDTOs() || memberCardDTO.getMemberCardServiceDTOs().size() <= 0) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    List<MemberCardServiceDTO> memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();
    try {

      for (MemberCardServiceDTO memberCardServiceDTO : memberCardDTO.getMemberCardServiceDTOs()) {
        if (null == memberCardServiceDTO.getId()) {
          Service service = new Service();
          service.setName(memberCardServiceDTO.getServiceName());
          service.setShopId(memberCardDTO.getShopId());
          service.setStatus(ServiceStatus.ENABLED);
          writer.save(service);
          memberCardServiceDTO.setServiceId(service.getId());
        }
        memberCardServiceDTOs.add(memberCardServiceDTO);
      }

      writer.commit(status);

      return memberCardServiceDTOs;
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public Boolean payDebt(double totalAmount,
                         double payedAmount,
                         double owedAmount,
                         String receivableOrderIdsString,
                         String orderTotalsString,
                         String orderOwedsString,
                         String orderPayedsString,
                         String debtIdsString,
                         String huankuanTime, HttpServletRequest request, Long shopId) throws Exception {
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    //欠款结算信息
    String debtArrearsInfo = String.valueOf(request.getAttribute("debtArrearsInfo"));

    String[] receivableOrderIdsArray = receivableOrderIdsString.split(",");
    //每个欠款单的”消费总金额“数组
    String[] orderTotalsArray = orderTotalsString.split(",");
    // 每个欠款单的“欠款金额”数组
    String[] orderOwedsArray = orderOwedsString.split(",");
    // 每个欠款单的“实收金额”数组
    String[] orderPayedsArray = orderPayedsString.split(",");
    //欠款单ID数组
    String[] debtIdsArray = debtIdsString.split(",");
    //检查欠款前台后台是否一致
    if(isTheDebtDoublePay(totalAmount, shopId,receivableOrderIdsArray)){
      return false;
    }
    //    如果 totalDiscount>zero说明有优惠  ，否则totalDiscount <zero说明无优惠
    double totalDiscount = totalAmount - payedAmount - owedAmount;
    Long remindTime = StringUtils.isBlank(huankuanTime) ? null : DateUtil.convertDateStringToDateLong("yyyy-MM-dd", huankuanTime);
    Long payTime = System.currentTimeMillis();

    Long userId = (Long)request.getSession().getAttribute("userId");
    String username = (String)request.getSession().getAttribute("userName");
    Long customerId = Long.valueOf(request.getParameter("customerId"));

    //更新会员余额
    double memberAmount = NumberUtil.doubleValue(request.getParameter("memberAmount"), 0);
    Long memberId = null;
    String memberNo = null;
    if (memberAmount > 0) {
      memberNo = request.getParameter("accountMemberNo");
      Member member = membersService.getMemberByShopIdAndMemberNo((Long) (request.getSession().getAttribute("shopId")), memberNo);
      if (member == null) {
        LOG.error("TxnService.java method=payDebt getMemberByShopIdAndMemberNo 获取会员信息失败 " + debtArrearsInfo);
      } else {
        memberId = member.getId();
        memberNo = member.getMemberNo();
        MemberDTO memberDTO = member.toDTO();
        memberDTO.setBalance(member.getBalance() - memberAmount);
        membersService.updateMember(memberDTO);
      }
    }
    List<Receivable> receivableList = new ArrayList<Receivable>();

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //欠款逻辑重构 by zhangjuntao
      int count = receivableOrderIdsArray.length;
      Debt debt = null;
      Receivable receivable = null;
      Long receivableId = null;
      Long debtId = null;
      double thisDebt = 0d;               //每条欠款单 欠款金额
      double thisTotal = 0d;              //每条欠款单 总金额
      double thisPayedAmount = 0d;        //每条欠款单 实收金额
      double newDebt = 0d;                //每条欠款单 新欠款金额
      double newPayedAmount = 0d;         //每条欠款单 新实收金额
      double newDiscount = 0d;            //每条欠款单 新折扣金额

      double discount = NumberUtil.doubleValue(request.getParameter("discount"), 0);
      double cashAmount = NumberUtil.doubleValue(request.getParameter("cashAmount"), 0);
      double bankCardAmount = NumberUtil.doubleValue(request.getParameter("bankAmount"), 0);
      double chequeAmount = NumberUtil.doubleValue(request.getParameter("bankCheckAmount"), 0);
      double depositAmount = NumberUtil.doubleValue(request.getParameter("depositAmount"),0);


      String bankCheckNo = request.getParameter("bankCheckNo");

      ReceivableHistoryDTO receivableHistoryDTO = new ReceivableHistoryDTO();
      receivableHistoryDTO.setShopId(shopId);
      receivableHistoryDTO.setTotal(totalAmount);
      receivableHistoryDTO.setDiscount(totalDiscount);
      receivableHistoryDTO.setDebt(owedAmount);
      receivableHistoryDTO.setCash(cashAmount);
      receivableHistoryDTO.setBankCardAmount(bankCardAmount);
      receivableHistoryDTO.setCheckAmount(chequeAmount);
      receivableHistoryDTO.setCheckNo(bankCheckNo);
      receivableHistoryDTO.setDeposit(depositAmount); // add by zhuj
      receivableHistoryDTO.setMemberBalancePay(memberAmount);
      receivableHistoryDTO.setMemberId(memberId);
      receivableHistoryDTO.setMemberNo(memberNo);
      receivableHistoryDTO.setStrikeAmount(0D);
      receivableHistoryDTO.setSettledAmount(payedAmount);
      receivableHistoryDTO.setCustomerId(customerId);
      receivableHistoryDTO.setReceivableDate(System.currentTimeMillis());
      receivableHistoryDTO.setReceiver(username);
      receivableHistoryDTO.setReceiverId(userId);
      ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
      writer.save(receivableHistory);

      RunningStatDTO runningStatDTO = new RunningStatDTO();
      runningStatDTO.setShopId((Long) (request.getSession().getAttribute("shopId")));
      runningStatDTO.setStatYear((long) DateUtil.getCurrentYear());
      runningStatDTO.setStatMonth((long) DateUtil.getCurrentMonth());
      runningStatDTO.setStatDay((long) DateUtil.getCurrentDay());
      runningStatDTO.setCashIncome(cashAmount);
      runningStatDTO.setChequeIncome(chequeAmount);
      runningStatDTO.setUnionPayIncome(bankCardAmount);
      runningStatDTO.setMemberPayIncome(memberAmount);
      runningStatDTO.setCustomerDepositExpenditure(depositAmount);
      runningStatDTO.setDebtWithdrawalIncome(cashAmount + chequeAmount + bankCardAmount + memberAmount +depositAmount);
      runningStatDTO.setCustomerDebtDiscount(totalDiscount);
      runningStatDTO.setStatDate(System.currentTimeMillis());
      runningStatService.runningStat(runningStatDTO, false);

      for (int i = 0; i < count; i++) {

        double cash = 0;
        double bankCard = 0;
        double cheque = 0;
        double memberPayAmount = 0;
        double deposit = 0;
        double remainDebt = 0;
        double discountAmount = 0;


        receivableId = Long.valueOf(receivableOrderIdsArray[i]);
        debtId = Long.valueOf(debtIdsArray[i]);
        if (debtId == null || receivableId == null)
          throw new Exception("debtId[" + debtId + "] or receivableId[" + receivableId + "]  NullPointException!");
        debt = writer.getById(Debt.class, debtId);
        if (debt == null) throw new Exception("debt[debtId:" + debtId + "]  NullPointException!");
        receivable = writer.getById(Receivable.class, receivableId);
        if (receivable == null)
          throw new Exception("receivable[receivableId:" + receivableId + "]  NullPointException!");
        thisDebt = debt.getDebt();
        thisTotal = debt.getTotalAmount();
        thisPayedAmount = debt.getSettledAmount();
        //如果总实收和总折扣都< 0 只更新时间等等数据
//        if (totalDiscount < zero && payedAmount < zero) {
//          debt.setPayTime(payTime);
//          debt.setRemindTime(remindTime);
//          debt.setStatus(TxnConstant.DebtStatus.DEBT_STATUS_ARREARS);
//          debt.setStatusEnum(DebtStatus.ARREARS);
//          debt.setRemindStatus(UserConstant.Status.ACTIVITY);
//          writer.save(debt);
//          CustomerDTO customerDTO = ServiceManager.getService(ICustomerService.class).getCustomerById(debt.getCustomerId());
//          if(customerDTO!=null){
//            saveRemindEvent(debt,customerDTO.getName(),customerDTO.getMobile());
//            //更新缓存
//            updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, debt.getShopId());
//          }
//          continue;
//        }
        if (payedAmount - thisDebt > zero) {  //总实收>该单据欠款
          newDebt = 0;
          payedAmount = payedAmount - thisDebt;
          newPayedAmount = thisDebt + thisPayedAmount;
        } else {       //总实收+总折扣<该单据欠款
          newPayedAmount = payedAmount + thisPayedAmount;
          if (totalDiscount + payedAmount - thisDebt < zero) {
            newDebt = thisDebt - (totalDiscount + payedAmount);
            payedAmount = 0;
            totalDiscount = 0;
          } else {     //总实收+总折扣>该单据欠款
            totalDiscount = (totalDiscount + payedAmount) - thisDebt;
            newDebt = 0;
            payedAmount = 0;
          }
        }
        receivable.setDebt(newDebt);
        if (newDebt > 0) {
          receivable.setRemindTime(remindTime);
        }
        receivable.setLastPayee(username);
        receivable.setLastPayeeId(userId);
        newDiscount = thisTotal - newPayedAmount - newDebt;
        //更新历史记录    receivable.getCustomerId()
        itemIndexService.updateItemIndexArrearsAndPaymentTime(receivable.getOrderId(), newDebt, remindTime);
        receivable.setDiscount(newDiscount);
        receivable.setSettledAmount(newPayedAmount);

        if (newDebt == 0) {
          debt.setStatus(TxnConstant.DebtStatus.DEBT_STATUS_SETTLE);
          debt.setStatusEnum(DebtStatus.SETTLED);
          debt.setSettledAmount(newPayedAmount);
          cancelRemindEventByOldRemindEventId(RemindEventType.DEBT, debt.getId(), writer);
          //更新缓存
          updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, debt.getShopId());
        }else{
          debt.setSettledAmount(newPayedAmount);
          debt.setPayTime(payTime);
          debt.setRemindTime(remindTime);
          debt.setStatus(TxnConstant.DebtStatus.DEBT_STATUS_ARREARS);
          debt.setStatusEnum(DebtStatus.ARREARS);
        }
        debt.setDebt(newDebt);
        writer.update(debt);
        //更新remind_event的deleted_type
        writer.updateDebtRemindDeletedType(shopId,customerId,"customer",DeletedType.FALSE);
        // add by WLF 更新提醒总表中欠款提醒的余额和状态
        RemindEventDTO remindEventDTO = getRemindEventByOldRemindEventId(RemindEventType.DEBT,shopId, debt.getId());
        if(remindEventDTO!=null){
          remindEventDTO.setDebt(newDebt);
          remindEventDTO.setRemindTime(remindTime);
          updateRemindEvent(remindEventDTO);
        }

        //现金够付的
        if (cashAmount >= thisDebt) {
          cash = thisDebt;
          cashAmount = cashAmount - thisDebt;
        } else if (cashAmount + bankCardAmount > thisDebt) {
          cash = cashAmount;
          cashAmount = 0;
          bankCard = thisDebt - cash;
          bankCardAmount = bankCardAmount - bankCard;
        } else if (cashAmount + bankCardAmount + chequeAmount >= thisDebt) {

          cash = cashAmount;
          bankCard = bankCardAmount;
          cashAmount = 0;
          bankCardAmount = 0;

          cheque = thisDebt - cash - bankCard;
          chequeAmount = chequeAmount - cheque;
        } else if (cashAmount + bankCardAmount + chequeAmount + memberAmount >= thisDebt) {

          cash = cashAmount;
          bankCard = bankCardAmount;
          cheque = chequeAmount;

          cashAmount = 0;
          bankCardAmount = 0;
          chequeAmount = 0;

          memberPayAmount = thisDebt - cash - bankCard - cheque;
          memberAmount = memberAmount - memberPayAmount;

        }else if(cashAmount + bankCardAmount + chequeAmount + memberAmount+depositAmount >=thisDebt ){
          cash = cashAmount;
          bankCard = bankCardAmount;
          cheque = chequeAmount;
          memberPayAmount = memberAmount;

          cashAmount = 0;
          bankCardAmount = 0;
          chequeAmount = 0;
          memberAmount = 0;

          deposit = thisDebt - cash - bankCard - cheque - memberPayAmount;
          depositAmount = depositAmount - deposit;

        }
        else if (cashAmount + bankCardAmount + chequeAmount + memberAmount + depositAmount + owedAmount >= thisDebt) {

          cash = cashAmount;
          bankCard = bankCardAmount;
          cheque = chequeAmount;
          memberPayAmount = memberAmount;
          deposit = depositAmount;

          cashAmount = 0;
          bankCardAmount = 0;
          chequeAmount = 0;
          memberAmount = 0;
          depositAmount = 0;

          remainDebt = thisDebt - cash - bankCard - cheque - memberPayAmount - deposit;
          owedAmount = owedAmount - remainDebt;

        } else if (cashAmount + bankCardAmount + chequeAmount + memberAmount + depositAmount +owedAmount + discount >= thisDebt) {
          cash = cashAmount;
          bankCard = bankCardAmount;
          cheque = chequeAmount;
          memberPayAmount = memberAmount;
          deposit = depositAmount;
          remainDebt = owedAmount;

          cashAmount = 0;
          bankCardAmount = 0;
          chequeAmount = 0;
          depositAmount = 0;
          memberAmount = 0;
          owedAmount = 0;

          discountAmount = thisDebt - (cash + bankCard + cheque + memberPayAmount + depositAmount + remainDebt);
          discount = discount - discountAmount;
        }


        if (memberPayAmount > 0) {
          receivable.setMemberId(memberId);
          receivable.setMemberNo(memberNo);
          receivable.setMemberBalancePay(NumberUtil.doubleVal(receivable.getMemberBalancePay()) + memberPayAmount);
        }
        receivable.setCash(NumberUtil.doubleVal(receivable.getCash()) + cash);
        receivable.setBankCard(NumberUtil.doubleVal(receivable.getBankCard()) + bankCard);
        receivable.setCheque(NumberUtil.doubleVal(receivable.getCheque()) + cheque);
        receivable.setDeposit(NumberUtil.doubleVal(receivable.getDeposit())+ deposit);
        writer.save(receivable);

        receivableList.add(receivable);

        ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
        receptionRecordDTO.setShopId(receivable.getShopId());
        receptionRecordDTO.setReceptionDate(System.currentTimeMillis());
        receptionRecordDTO.setReceivableId(receivable.getId());
        receptionRecordDTO.setOrderId(receivable.getOrderId());
        receptionRecordDTO.setOrderTotal(receivable.getTotal());
        receptionRecordDTO.setAfterMemberDiscountTotal(receivable.getAfterMemberDiscountTotal());
        receptionRecordDTO.setPayee(username);
        receptionRecordDTO.setPayeeId(userId);
        receptionRecordDTO.setReceivableHistoryId(receivableHistory.getId());

        int recordNum = 0;
        List<ReceptionRecordDTO> receptionRecordDTOList = this.getReceptionRecordByOrderId(receivable.getShopId(), receivable.getOrderId(),null);
        if (CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
          recordNum = receptionRecordDTOList.get(0).getRecordNum();
          recordNum++;
          receptionRecordDTO.setRecordNum(recordNum);
        } else {
          receptionRecordDTO.setRecordNum(recordNum);
        }

        receptionRecordDTO.setAmount(cash + bankCard + cheque + memberPayAmount+ deposit);
        receptionRecordDTO.setMemberBalancePay(memberPayAmount);
        receptionRecordDTO.setCash(cash);
        receptionRecordDTO.setBankCard(bankCard);
        receptionRecordDTO.setCheque(cheque);
        receptionRecordDTO.setDeposit(deposit);
        if (cheque > 0) {
          receptionRecordDTO.setChequeNo(bankCheckNo);
        }

        if (memberPayAmount > 0) {
          receptionRecordDTO.setMemberId(memberId);
        }
        receptionRecordDTO.setToPayTime(remindTime);
        receptionRecordDTO.setOriginDebt(thisDebt);
        receptionRecordDTO.setDiscount(discountAmount);
        receptionRecordDTO.setRemainDebt(remainDebt);
        receptionRecordDTO.setOrderTypeEnum(OrderTypes.DEBT);
        receptionRecordDTO.setDayType(DayType.OTHER_DAY);
        ReceptionRecord receptionRecord = new ReceptionRecord();
        receptionRecord = receptionRecord.fromDTO(receptionRecordDTO);
        writer.save(receptionRecord);

        if (deposit > 0.001) { // add by zhuj
          if (receivable.getOrderTypeEnum() == OrderTypes.SALE) {
            SalesOrderDTO salesOrderDTO = this.getSalesOrder(receivable.getOrderId());
            customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
            CustomerDepositDTO customerDepositDTO = new CustomerDepositDTO();
            customerDepositDTO.setOperator(salesOrderDTO.getGoodsSaler());
            customerDepositDTO.setShopId(salesOrderDTO.getShopId());
            customerDepositDTO.setActuallyPaid(deposit); // 实际使用的预付款金额
            customerDepositDTO.setCustomerId(receivable.getCustomerId());
            DepositOrderDTO depositOrderDTO = new DepositOrderDTO();
            //基础字段在service方法中有填充
            depositOrderDTO.setDepositType(DepositType.SALES.getScene());
            depositOrderDTO.setInOut(InOutFlag.OUT_FLAG.getCode());
            depositOrderDTO.setRelatedOrderId(salesOrderDTO.getId()); // 记录销售单id
            depositOrderDTO.setRelatedOrderNo(salesOrderDTO.getReceiptNo()); // 记录销售单按单据号
            customerDepositService.customerDepositUse(customerDepositDTO, depositOrderDTO,writer);
          }
        }


        //如果某个单据有折扣，根据这个单据的结算日期，更应相应的营业额统计 会员不计算
        if(discountAmount > 0 && receivable.getOrderTypeEnum() != OrderTypes.MEMBER_BUY_CARD) {
          businessStatService.updateBusinessStatFromDebt(discountAmount, receivable, writer);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    //itemIndex orderIndex reindex
    if (CollectionUtils.isNotEmpty(receivableList)) {
      try {
        for (Receivable receivable : receivableList) {
          if (receivable.getOrderTypeEnum() == OrderTypes.SALE && receivable.getDebt() <= 0) {
            ServiceManager.getService(IItemIndexService.class).updateItemIndexPurchaseOrderStatus(shopId, receivable.getOrderTypeEnum(),
              receivable.getOrderId(), OrderStatus.SALE_DONE);
            ServiceManager.getService(ISearchService.class).updateOrderIndex(shopId, receivable.getOrderId(), receivable.getOrderTypeEnum(), OrderStatus.SALE_DONE);

          }
        }
      } catch (Exception e) {
        LOG.error("TxnService.java,debtArrearsInfo:" + debtArrearsInfo);
        LOG.error(e.getMessage(), e);
      }
    }

    return true;
  }

  private boolean isTheDebtDoublePay(double debt,Long shopId, String... receivableIds) {
    if (ArrayUtils.isEmpty(receivableIds) || debt <= 0) {
      LOG.error("pay debt error,debt id is empty or debt little than 0.");
      return true;
    }
    List<Long> ids = new ArrayList<Long>();
    int i = 0;
    for (String receivableId : receivableIds) {
      if (StringUtils.isBlank(receivableId)) continue;
      ids.add(Long.valueOf(receivableId));
    }
    if (CollectionUtils.isEmpty(ids)) return true;
    double totalDebt = txnDaoManager.getWriter().sumDebtByIds(ids, shopId);
    return zero <= (debt - totalDebt);
  }

  public List<RepairRemindEvent> getLackProductIdsByRepairOderId(Long repairOrderId, Long shopId, RepairRemindEventTypes eventType) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getLackProductIdsByRepairOderId(repairOrderId, shopId, eventType);
  }

  @Override
  public BusinessStatDTO saveBusinessStat(BusinessStatDTO businessStatDTO) {
    if (businessStatDTO == null) return null;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();

    try {
      BusinessStat businessStat = null;
      List<BusinessStatDTO> businessStatDTOList = this.getBusinessStatByYearMonthDay(businessStatDTO.getShopId(), businessStatDTO.getStatYear(), businessStatDTO.getStatMonth(), businessStatDTO.getStatDay());
      if(CollectionUtils.isEmpty(businessStatDTOList)) {
        businessStat = new BusinessStat();
        businessStat = businessStat.fromDTO(businessStatDTO,false);
        writer.save(businessStat);
        businessStatDTO = businessStat.toDTO();
      }else {
        BusinessStatDTO todayBusinessStatDTO = businessStatDTOList.get(0);
        businessStat = writer.getById(BusinessStat.class, todayBusinessStatDTO.getId());
        if (businessStat != null) {
          businessStat = businessStat.fromDTO(businessStatDTO,false);
          writer.update(businessStat);
          businessStatDTO = businessStat.toDTO();
        }
      }
      writer.commit(status);
      return businessStatDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ExpendDetailDTO saveExpendDetail(ExpendDetailDTO expendDetailDTO) {
    if (expendDetailDTO == null) return null;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();

    try {

      if (expendDetailDTO.getYear() != null && expendDetailDTO.getMonth() != null && expendDetailDTO.getShopId() != null) {
        writer.deleteExpendDetailByYearMonth(expendDetailDTO.getShopId(), expendDetailDTO.getYear(), expendDetailDTO.getMonth(), expendDetailDTO.getId());
      }

      ExpendDetail expendDetail = new ExpendDetail();
      if (expendDetailDTO.getId() != null) {
        expendDetail = writer.getById(ExpendDetail.class, expendDetailDTO.getId());
        if (expendDetail == null) {
          expendDetail = new ExpendDetail();
          expendDetail = expendDetail.fromDTO(expendDetailDTO, false);
          writer.save(expendDetail);
        } else {
          expendDetail = expendDetail.fromDTO(expendDetailDTO, false);
          writer.update(expendDetail);
        }

      }else{
        expendDetail = expendDetail.fromDTO(expendDetailDTO,false);
        writer.save(expendDetail);
      }

      writer.commit(status);

      expendDetailDTO.setId(expendDetail.getId());

      return expendDetailDTO;
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public BusinessStatDTO updateBusinessStat(BusinessStatDTO businessStatDTO) {
    if (businessStatDTO == null) return null;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();

    try {
      BusinessStat businessStat = new BusinessStat(businessStatDTO);
      writer.delete(BusinessStat.class, businessStat.getId());
      writer.save(businessStat);

      writer.commit(status);

      businessStatDTO.setId(businessStat.getId());

      return businessStatDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteExpendDetailByYearMonth(long shopId, long year, long month) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteExpendDetailByYearMonth(shopId, year, month, null);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public List<BusinessStatDTO> getBusinessStatByYearMonthDay(long shopId, long year, long month, long day) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStat> businessStatList = writer.getBusinessStatByYearMonthDay(shopId, year, month, day);
    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
    if (businessStatList != null) {
      for (BusinessStat businessStat : businessStatList) {
        BusinessStatDTO businessStatDTO = businessStat.toDTO();
        businessStatDTOList.add(businessStatDTO);
      }
      return businessStatDTOList;
    }
    return null;
  }

  @Override
  public List<ExpendDetailDTO> getExpendDetailByYearMonthDay(long shopId, long year, long month, long day) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ExpendDetail> expendDetailList = writer.getExpendDetailByYearMonthDay(shopId, year, month, day);
    List<ExpendDetailDTO> expendDetailDTOList = new ArrayList<ExpendDetailDTO>();
    if (expendDetailList != null) {
      for (ExpendDetail expendDetail : expendDetailList) {
        ExpendDetailDTO expendDetailDTO = expendDetail.toDTO();
        expendDetailDTOList.add(expendDetailDTO);
      }
      return expendDetailDTOList;
    }
    return null;
  }

  @Override
  public List<ExpendDetailDTO> getExpendDetailByYearMonth(long shopId, long year, long month, long day) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ExpendDetail> expendDetailList = writer.getExpendDetailByYearMonth(shopId, year, month, day);
    List<ExpendDetailDTO> expendDetailDTOList = new ArrayList<ExpendDetailDTO>();
    if (expendDetailList != null) {
      for (ExpendDetail expendDetail : expendDetailList) {
        ExpendDetailDTO expendDetailDTO = expendDetail.toDTO();
        expendDetailDTOList.add(expendDetailDTO);
      }
      return expendDetailDTOList;
    }
    return null;
  }

  @Override
  public List<ExpendDetailDTO> getExpendDetailByYearFromStartMonthToEndMonth(long shopId, long year, long startMonth, long endMonth) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ExpendDetail> expendDetailList = writer.getExpendDetailByYearFromStartMonthToEndMonth(shopId, year, startMonth, endMonth);
    List<ExpendDetailDTO> expendDetailDTOList = new ArrayList<ExpendDetailDTO>();
    if (expendDetailList != null) {
      for (ExpendDetail expendDetail : expendDetailList) {
        ExpendDetailDTO expendDetailDTO = expendDetail.toDTO();
        expendDetailDTOList.add(expendDetailDTO);
      }
      return expendDetailDTOList;
    }
    return null;
  }


  @Override
  public List<BusinessStatDTO> getLatestBusinessStat(long shopId, Long year, int size) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStat> businessStatList = writer.getLatestBusinessStat(shopId, year, size);
    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
    if (businessStatList != null) {
      for (BusinessStat businessStat : businessStatList) {
        BusinessStatDTO businessStatDTO = businessStat.toDTO();
        businessStatDTOList.add(businessStatDTO);
      }
      return businessStatDTOList;
    }
    return null;
  }

  @Override
  public List<BusinessStatDTO> getEarliestBusinessStat(long shopId, long year, int size) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStat> businessStatList = writer.getEarliestBusinessStat(shopId, year, size);
    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
    if (businessStatList != null) {
      for (BusinessStat businessStat : businessStatList) {
        BusinessStatDTO businessStatDTO = businessStat.toDTO();
        businessStatDTOList.add(businessStatDTO);
      }
      return businessStatDTOList;
    }
    return null;
  }

  @Override
  public List<BusinessStatDTO> getLatestBusinessStatMonth(long shopId, long year, long month, int size) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStat> businessStatList = writer.getLatestBusinessStatMonth(shopId, year, month, size);
    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
    if (businessStatList != null) {
      for (BusinessStat businessStat : businessStatList) {
        BusinessStatDTO businessStatDTO = businessStat.toDTO();
        businessStatDTOList.add(businessStatDTO);
      }
      return businessStatDTOList;
    }
    return null;
  }

  @Override
  public List<BusinessStatDTO> getBusinessStatMonth(long shopId, long year, String queryString) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStat> businessStatList = writer.getBusinessStatMonth(shopId, year, queryString);
    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
    if (businessStatList != null) {
      for (BusinessStat businessStat : businessStatList) {
        BusinessStatDTO businessStatDTO = businessStat.toDTO();
        businessStatDTOList.add(businessStatDTO);
      }
      return businessStatDTOList;
    }
    return null;

  }

  @Override
  public List<BusinessStatDTO> getBusinessStatMonthEveryDay(long shopId, long year, long month, long day) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStat> businessStatList = writer.getBusinessStatMonthEveryDay(shopId, year, month, day);
    List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
    if (businessStatList != null) {
      for (BusinessStat businessStat : businessStatList) {
        BusinessStatDTO businessStatDTO = businessStat.toDTO();
        businessStatDTOList.add(businessStatDTO);
      }
      return businessStatDTOList;
    }
    return null;
  }

  @Override
  public List<RepairOrderDTO> getRepairOrderDTOList(long shopId, long startTime, long endTime, int pageNo, int PageSize, String arrayType,OrderStatus orderStatus) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrderDTO> repairOrderDTOList = writer.getRepairOrderDTOList(shopId, startTime, endTime, pageNo, PageSize, arrayType,orderStatus);
    return repairOrderDTOList;
  }

  @Override
  public List<RepairOrderDTO> getHundredCostPriceNUllRepairOrderDTOList() throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrder> repairOrderList = writer.getHundredCostPriceNUllRepairOrderDTOList();
    List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
    if (repairOrderList != null) {
      for (RepairOrder repairOrder : repairOrderList) {
        RepairOrderDTO repairOrderDTO = repairOrder.toDTO();
        repairOrderDTOList.add(repairOrderDTO);
      }
      return repairOrderDTOList;
    }
    return null;
  }

  @Override
  public List<RepairOrderItemDTO> countShopRepairOrderSalesIncome(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrderItem> repairOrderItemList = writer.countShopRepairOrderSalesIncome(shopId, startTime, endTime);
    List<RepairOrderItemDTO> repairOrderItemDTOList = new ArrayList<RepairOrderItemDTO>();
    if (repairOrderItemList != null) {
      for (RepairOrderItem repairOrderItem : repairOrderItemList) {
        RepairOrderItemDTO repairOrderItemDTO = repairOrderItem.toDTO();
        repairOrderItemDTOList.add(repairOrderItemDTO);
      }
      return repairOrderItemDTOList;
    }
    return null;
  }

  @Override
  public List<RepairOrderServiceDTO> countShopRepairOrderServiceIncome(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrderService> repairOrderServiceList = writer.countShopRepairOrderServiceIncome(shopId, startTime, endTime);
    List<RepairOrderServiceDTO> repairOrderServiceDTOList = new ArrayList<RepairOrderServiceDTO>();
    if (repairOrderServiceList != null) {
      for (RepairOrderService repairOrderService : repairOrderServiceList) {
        RepairOrderServiceDTO repairOrderServiceDTO = repairOrderService.toDTO();
        repairOrderServiceDTOList.add(repairOrderServiceDTO);
      }
      return repairOrderServiceDTOList;
    }
    return null;
  }


  @Override
  public List<SalesOrderItemDTO> countShopSalesIncome(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrderItem> salesOrderItemList = writer.countShopSalesIncome(shopId, startTime, endTime);
    List<SalesOrderItemDTO> salesOrderItemDTOList = new ArrayList<SalesOrderItemDTO>();
    if (salesOrderItemList != null) {
      for (SalesOrderItem salesOrderItem : salesOrderItemList) {
        SalesOrderItemDTO salesOrderItemDTO = salesOrderItem.toDTO();
        salesOrderItemDTOList.add(salesOrderItemDTO);
      }
      return salesOrderItemDTOList;
    }
    return null;
  }

  @Override
  public List<SalesOrderItemDTO> countShopSalesIncomeByShopId(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrderItem> salesOrderItemList = writer.countShopSalesIncomeByShopId(shopId, startTime, endTime);
    List<SalesOrderItemDTO> salesOrderItemDTOList = new ArrayList<SalesOrderItemDTO>();
    if (salesOrderItemList != null) {
      for (SalesOrderItem salesOrderItem : salesOrderItemList) {
        SalesOrderItemDTO salesOrderItemDTO = salesOrderItem.toDTO();
        salesOrderItemDTOList.add(salesOrderItemDTO);
      }
      return salesOrderItemDTOList;
    }
    return null;
  }


  /**
   * 更新商品单位
   *
   * @param orderDTO
   * @throws Exception
   */
  @Override
  public void updateProductUnit(BcgogoOrderDto orderDTO) throws Exception {
    BcgogoOrderItemDto[] itemDTOs = orderDTO.getItemDTOs();
    if (itemDTOs == null) {
      return;
    }
    for (BcgogoOrderItemDto itemDTO : itemDTOs) {
      if (itemDTO.getProductId() == null) {
        continue;
      } else if (StringUtils.isEmpty(itemDTO.getUnit())) {
        continue;
      } else {
        updateProductUnit(orderDTO.getShopId(), itemDTO.getProductId(), itemDTO.getUnit(), itemDTO.getUnit(), null);
      }
    }
  }

  @Override
  public void updateProductUnit(Long shopId,BcgogoOrderItemDto[] itemDTOs)throws Exception{
    if(shopId == null || ArrayUtils.isEmpty(itemDTOs)){
      return;
    }
    for (BcgogoOrderItemDto itemDTO : itemDTOs) {
      if (itemDTO.getProductId() == null) {
        continue;
      } else if (StringUtils.isEmpty(itemDTO.getUnit())) {
        continue;
      } else {
        updateProductUnit(shopId, itemDTO.getProductId(), itemDTO.getUnit(), itemDTO.getUnit(), null);
      }
    }
  }

  @Override
  public Boolean validateCouponNoUsed(Long shopId, String couponType, String couponNo) {
    TxnWriter writer = txnDaoManager.getWriter();
    Long count = writer.validateCouponNoUsed(shopId,couponType,couponNo);
    if(count == null || count <= 0) {
      return false;
    } else {
      return true;
    }
  }


  //productLocalInfo 存在一个product 但是没有销售单位和库存单位 ，添加一个单位，更新productLocalInfo，inventory，inventorySearchIndex，solr
  @Override
  public void updateProductUnit(Long shopId, Long productId, String storageUnit, String sellUnit, Long rate) throws Exception {
    IProductService productService = getProductService();
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);
    if (productDTO == null) {
      LOG.error("product is null for when updating sales order product unit");
      return;
    } else {
      if (StringUtils.isEmpty(productDTO.getSellUnit()) && StringUtils.isEmpty(productDTO.getStorageUnit())) {
        //1. 更新productLocalInfo unit
        productService.updateProductLocalInfoUnit(shopId, productId, storageUnit, sellUnit, rate);
        //2.更新Inventory 中 unit
        Inventory inventory = writer.getById(Inventory.class, productId);
        inventory.setUnit(sellUnit);
        createOrUpdateInventory(inventory.toDTO());
        //更新InventorySearchIndex productSolr
        Long[] productIds = new Long[1];
        productIds[0] = productId;
        List<InventorySearchIndex> inventorySearchIndexList = searchService.searchInventorySearchIndexByProductIds(shopId, productIds);
        if (inventorySearchIndexList != null && inventorySearchIndexList.size() > 0) {
          InventorySearchIndex inventorySearchIndex = inventorySearchIndexList.get(0);
          inventorySearchIndex.setUnit(sellUnit);
          List<InventorySearchIndex> inventorySearchIndexs = new ArrayList<InventorySearchIndex>();
          inventorySearchIndexs.add(inventorySearchIndex);
          //3.更新InventorySearchIndex
          searchService.updateInventorySearchIndexAmountWithList(inventorySearchIndexs);
        }
        //5.更新待入库记录 来料提醒中不存商品id，只能靠商品四属性定位商品 鉴于之前客户几乎没用过采购，采购完成之后将productId保存到提醒事项content字段string类型
        updateProductUnitForInventoryRemindEvent(shopId, productId, sellUnit);
        //6.更新缺料待修，来料待修提醒
        updateProductUnitForRepairRemindEvent(shopId, productId, sellUnit);
        //7 更新SupplierInventory记录
        updateProductUnitForSupplierInventory(shopId,productId,sellUnit);
      }
    }
  }

  private void updateProductUnitForInventoryRemindEvent(Long shopId, Long productId, String sellUnit) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<InventoryRemindEvent> inventoryRemindEvents = writer.getInventoryRemindEventByProductId(shopId, productId);
      if (inventoryRemindEvents == null && inventoryRemindEvents.size() == 0) {
        return;
      } else {
        for (InventoryRemindEvent inventoryRemindEvent : inventoryRemindEvents) {
          List<PurchaseOrderItem> purchaseOrderItems = writer.getPurchaseOrderItemsByOrderIdAndProductId(inventoryRemindEvent.getPurchaseOrderId(),
            new Long(inventoryRemindEvent.getContent()));
          PurchaseOrderItem purchaseOrderItem;
          if (purchaseOrderItems != null && purchaseOrderItems.size() > 0) {
            purchaseOrderItem = purchaseOrderItems.get(0);
            purchaseOrderItem.setUnit(sellUnit);
            writer.update(purchaseOrderItem);
          }
          inventoryRemindEvent.setUnit(sellUnit);
          writer.update(inventoryRemindEvent);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  private void updateProductUnitForRepairRemindEvent(Long shopId, Long productId, String unit) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<RepairRemindEvent> repairRemindEvents = writer.getLackMaterialByProductId(shopId, null, productId);
      if (repairRemindEvents == null || repairRemindEvents.size() == 0) {
        return;
      } else {
        for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
          repairRemindEvent.setUnit(unit);
          writer.update(repairRemindEvent);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private void updateProductUnitForSupplierInventory(Long shopId, Long productId, String unit) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Set<Long> productIds = new HashSet<Long>();
      productIds.add(productId);
      List<SupplierInventory> supplierInventories = writer.getSupplierInventoriesByProductIds(shopId, productIds);
      if (CollectionUtils.isNotEmpty(supplierInventories)) {
        for (SupplierInventory supplierInventory : supplierInventories) {
          supplierInventory.setUnit(unit);
          writer.update(supplierInventory);
        }
      } else {
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  public List<OrderSearchResultDTO> getSalesOrderDTOList(long shopId, long startTime, long endTime, int pageNo, int pageSize, String arrayType) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<OrderSearchResultDTO> orderSearchResultDTOList = writer.getSalesOrderDTOList(shopId, startTime, endTime, pageNo, pageSize, arrayType);
    return orderSearchResultDTOList;
  }

  public List<SalesOrderDTO> getSalesOrderDTOListByVestDate(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrder> salesOrderList = writer.getSalesOrderDTOListByVestDate(shopId, startTime, endTime);
    List<SalesOrderDTO> salesOrderDTOList = new ArrayList<SalesOrderDTO>();
    if (salesOrderList != null && salesOrderList.size() > 0) {
      for (SalesOrder salesOrder : salesOrderList) {
        SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
        salesOrderDTOList.add(salesOrderDTO);
      }
      return salesOrderDTOList;
    }
    return null;
  }

  @Override
  public List<SmsRechargeDTO> getSmsRechargesByConditions(RechargeSearchDTO rechargeSearchDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SmsRechargeDTO> smsRechargeDTOList = new ArrayList<SmsRechargeDTO>();
    List<SmsRecharge> smsRechargeList = writer.getSmsRechargesByConditions(rechargeSearchDTO);
    for (SmsRecharge sr : smsRechargeList) {
      smsRechargeDTOList.add(sr.toDTO());
    }
    return smsRechargeDTOList;
  }

  @Override
  public List<Long> getSmsRechargesByStatus(Long shopId, int start, int pageSize, Long loanTransferTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSmsRechargesByStatus(shopId,start,pageSize,loanTransferTime);
  }

  @Override
  public List<SmsRechargeDTO> getSmsRechargesByIds(Long... ids) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SmsRechargeDTO> smsRechargeDTOList = new ArrayList<SmsRechargeDTO>();
    List<SmsRecharge> smsRechargeList = writer.getSmsRechargesByIds(ids);
    if (CollectionUtils.isEmpty(smsRechargeList)) return smsRechargeDTOList;
    for (SmsRecharge sr : smsRechargeList) {
      smsRechargeDTOList.add(sr.toDTO());
    }
    return smsRechargeDTOList;
  }

  @Override
  public int countSmsRechargesByConditions(RechargeSearchDTO rechargeSearchDTO) {
    return txnDaoManager.getWriter().countSmsRechargesByConditions(rechargeSearchDTO);
  }

  @Override
  public List<SalesOrderDTO> getHundredCostPriceNUllSalesOrderDTOList() throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrder> salesOrderList = writer.getHundredCostPriceNUllSalesOrderDTOList();

    List<SalesOrderDTO> salesOrderDTOList = new ArrayList<SalesOrderDTO>();
    if (salesOrderList != null) {
      for (SalesOrder salesOrder : salesOrderList) {
        SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
        salesOrderDTOList.add(salesOrderDTO);
      }
      return salesOrderDTOList;
    }
    return null;

  }

  @Override
  public List<WashOrderDTO> countWashOrderList(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<WashOrder> washOrderList = writer.countWashOrderList(shopId, startTime, endTime);
    List<WashOrderDTO> washOrderDTOList = new ArrayList<WashOrderDTO>();
    if (washOrderList != null) {
      for (WashOrder washOrder : washOrderList) {
        WashOrderDTO washOrderDTO = washOrder.toDTO();
        washOrderDTOList.add(washOrderDTO);
      }
      return washOrderDTOList;
    }
    return null;
  }


  @Override
  public List<String> getRepairOrderDTOListByVestDate(long shopId, long startTime, long endTime,OrderStatus orderStatus) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<String> stringList = writer.getRepairOrderDTOListByVestDate(shopId, startTime, endTime,orderStatus);
    return stringList;
  }

  @Override
  public int countSalesOrder(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countSalesOrder(shopId, startTime, endTime);
  }

  @Override
  public int countSalesOrder(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countSalesOrder(shopId);
  }

  @Override
  public int countRepairOrder(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countRepairOrder(shopId);
  }

  @Autowired
  private TxnDaoManager txnDaoManager;

  public List<WashOrder> countWashAgentAchievements(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countWashAgentAchievements(shopId, startTime, endTime);
  }

  public List<SalesOrder> countSalesAgentAchievements(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countSalesAgentAchievements(shopId, startTime, endTime);
  }

  public List<WashOrder> getWashOrderListByAssistantName(String assistantName, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<WashOrder> washOrderList = new ArrayList<WashOrder>();

    washOrderList = writer.getWashOrderListByAssistantName(assistantName, startTime, endTime);

    return washOrderList;
  }

  public List<RepairOrder> getRepairOrderListByAssistantName(String assistantName, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrder> repairOrderList = new ArrayList<RepairOrder>();

    repairOrderList = writer.getRepairOrderListByAssistantName(assistantName, startTime, endTime);

    return repairOrderList;
  }

  public List<SalesOrder> getSalesOrderListByAssistantName(String assistantName, long startTime, long endTime) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<SalesOrder> salesOrderList = txnWriter.getSalesOrderListByAssistantName(assistantName, startTime, endTime);
    return salesOrderList;
  }

  /**
   * 从excel中导入库存
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public ImportResult importInventoryFromExcel(ImportContext importContext) throws BcgogoException {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.parseData(importContext);

    //2.校验数据
    CheckResult checkResult = inventoryImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = inventoryImporter.importData(importContext);

    return importResult;
  }

  /**
   * 批量保存库存信息
   *
   * @param inventoryInfoDTOList
   * @return
   * @throws BcgogoException
   */
  @Override
  public boolean batchCreateInventory(List<InventoryInfoDTO> inventoryInfoDTOList, Long shopId) throws BcgogoException {
    productDaoManager = ServiceManager.getService(ProductDaoManager.class);
    if (inventoryInfoDTOList == null || inventoryInfoDTOList.isEmpty()) {
      return false;
    }
    ProductWriter productWriter = productDaoManager.getWriter();
    TxnWriter txnWriter = txnDaoManager.getWriter();
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    Map<String,StoreHouseDTO> storeHouseDTOMap = storeHouseService.getAllStoreHousesNameKeyMapByShopId(shopId);

    //1.保存公共产品和本店产品
    Object productStatus = productWriter.begin();
    try {
      String  commodityCode=null;
      Product product = null;
      ProductLocalInfo productLocalInfo = null;
      for (InventoryInfoDTO inventoryInfoDTO : inventoryInfoDTOList) {
        if (inventoryInfoDTO == null || inventoryInfoDTO.getProductDTO() == null) {
          continue;
        }
        //解决商品编码导入当是数字时会自动加“.0”
        commodityCode=inventoryInfoDTO.getProductDTO().getCommodityCode();
        if(NumberUtil.isNumber(commodityCode)){
          inventoryInfoDTO.getProductDTO().setCommodityCode(commodityCode.split("\\.")[0]);
        }
        product = new Product();
        product.fromDTO(inventoryInfoDTO.getProductDTO());
        productWriter.save(product);
        inventoryInfoDTO.getProductDTO().setId(product.getId());
        if (inventoryInfoDTO.getProductLocalInfoDTO() != null) {
          inventoryInfoDTO.getProductLocalInfoDTO().setProductId(inventoryInfoDTO.getProductDTO().getId());
          productLocalInfo = new ProductLocalInfo();
          productLocalInfo.fromProductLocalInfoDTO(inventoryInfoDTO.getProductLocalInfoDTO());
          productWriter.save(productLocalInfo);
          inventoryInfoDTO.getProductLocalInfoDTO().setId(productLocalInfo.getId());
        }
      }
      productWriter.commit(productStatus);
    } catch (Exception e) {
      LOG.error("保存库存信息时出现异常！");
      LOG.error("信息：" + e.getMessage(), e);
      return false;
    } finally {
      productWriter.rollback(productStatus);
    }

    //2.保存库存和入库价格，仓库
    Object txnStatus = txnWriter.begin();
    try {
      String dS = "默认仓库";
      if (MapUtils.isNotEmpty(storeHouseDTOMap) && storeHouseDTOMap.size() == 1) {
        dS = storeHouseDTOMap.keySet().iterator().next();
      }
      Inventory inventory = null;
      PurchasePrice purchasePrice = null;
      StoreHouseDTO storeHouseDTO = null;
      for (InventoryInfoDTO inventoryInfoDTO : inventoryInfoDTOList) {
        if (inventoryInfoDTO == null) {
          continue;
        }

        if (inventoryInfoDTO.getInventoryDTO() != null && inventoryInfoDTO.getProductLocalInfoDTO() != null) {
          inventoryInfoDTO.getInventoryDTO().setId(inventoryInfoDTO.getProductLocalInfoDTO().getId());
          inventory = new Inventory();
          inventory.fromDTO(inventoryInfoDTO.getInventoryDTO());
          inventory.setInventoryAveragePrice(inventoryInfoDTO.getInventoryDTO().getInventoryAveragePrice());
          txnWriter.saveOrUpdate(inventory);
          if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopDTO.getShopVersionId())) {
            if (StringUtils.isBlank(inventoryInfoDTO.getStoreHouse())) {
              inventoryInfoDTO.setStoreHouse(dS);
            }
            storeHouseDTO = storeHouseDTOMap.get(inventoryInfoDTO.getStoreHouse());
            if (storeHouseDTO == null) {
              StoreHouse storeHouse = new StoreHouse();
              storeHouse.setShopId(shopId);
              storeHouse.setName(inventoryInfoDTO.getStoreHouse());
              txnWriter.saveOrUpdate(storeHouse);
              storeHouseDTO = storeHouse.toDTO();
              storeHouseDTOMap.put(storeHouseDTO.getName(), storeHouseDTO);
            }
            StoreHouseInventory storeHouseInventory = new StoreHouseInventory();
            storeHouseInventory.setStorehouseId(storeHouseDTO.getId());
            storeHouseInventory.setAmount(inventory.getAmount());
            storeHouseInventory.setProductLocalInfoId(inventoryInfoDTO.getProductLocalInfoDTO().getId());
            storeHouseInventory.setStorageBin(inventoryInfoDTO.getProductLocalInfoDTO().getStorageBin());
            txnWriter.saveOrUpdate(storeHouseInventory);
          }

          SupplierInventory supplierInventory = new SupplierInventory();
          supplierInventory.setProductId(inventory.getId());
          supplierInventory.setShopId(shopId);
          supplierInventory.setStorehouseId(storeHouseDTO == null ? null : storeHouseDTO.getId());
          supplierInventory.setTotalInStorageAmount(inventory.getAmount());
          supplierInventory.setRemainAmount(inventory.getAmount());
          supplierInventory.setUnit(inventory.getUnit());
          supplierInventory.setMaxStoragePrice(inventoryInfoDTO.getInventoryDTO().getInventoryAveragePrice());
          supplierInventory.setMinStoragePrice(inventoryInfoDTO.getInventoryDTO().getInventoryAveragePrice());
          supplierInventory.setAverageStoragePrice(inventoryInfoDTO.getInventoryDTO().getInventoryAveragePrice());
          supplierInventory.setSupplierType(OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER);
          supplierInventory.setSupplierName(OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER.getName());
          supplierInventory.setDisabled(YesNo.NO);

          txnWriter.save(supplierInventory);

          InStorageRecord inStorageRecord = new InStorageRecord();
          inStorageRecord.setShopId(shopId);
          inStorageRecord.setProductId(inventory.getId());
          inStorageRecord.setInStorageItemAmount(inventory.getAmount());
          inStorageRecord.setInStorageUnit(inventory.getUnit());
          inStorageRecord.setRemainAmount(inventory.getAmount());
          inStorageRecord.setStorehouseId(storeHouseDTO == null ? null : storeHouseDTO.getId());
          inStorageRecord.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
          inStorageRecord.setPrice(inventoryInfoDTO.getInventoryDTO().getInventoryAveragePrice());
          inStorageRecord.setDisabled(YesNo.NO);
          inStorageRecord.setSupplierType(OutStorageSupplierType.IMPORT_PRODUCT_SUPPLIER);
          inStorageRecord.setSupplierRelatedAmount(inventory.getAmount());

          txnWriter.save(inStorageRecord);
        }

        if (inventoryInfoDTO.getPurchasePriceDTO() != null && inventoryInfoDTO.getProductLocalInfoDTO() != null) {
          inventoryInfoDTO.getPurchasePriceDTO().setProductId(inventoryInfoDTO.getProductLocalInfoDTO().getId());
          purchasePrice = new PurchasePrice();
          purchasePrice.fromDTO(inventoryInfoDTO.getPurchasePriceDTO());
          txnWriter.save(purchasePrice);
          inventoryInfoDTO.getPurchasePriceDTO().setId(purchasePrice.getId());
        }

      }
      txnWriter.commit(txnStatus);
    } catch (Exception e) {
      LOG.error("保存库存信息时出现异常！");
      LOG.error("信息：" + e.getMessage(), e);
      return false;
    } finally {
      txnWriter.rollback(txnStatus);
    }


    //3.保存库存查询表记录
    try {
      InventorySearchIndex inventorySearchIndex = null;
      for (InventoryInfoDTO inventoryInfoDTO : inventoryInfoDTOList) {
        if (inventoryInfoDTO == null) {
          continue;
        }

        if (inventoryInfoDTO.getInventorySearchIndexDTO() != null && inventoryInfoDTO.getProductLocalInfoDTO() != null && inventoryInfoDTO.getProductDTO() != null) {
          inventoryInfoDTO.getInventorySearchIndexDTO().setProductId(inventoryInfoDTO.getProductLocalInfoDTO().getId());
          inventoryInfoDTO.getInventorySearchIndexDTO().setParentProductId(inventoryInfoDTO.getProductDTO().getId());
          inventorySearchIndex = new InventorySearchIndex(inventoryInfoDTO.getInventorySearchIndexDTO());
          List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
          inventorySearchIndexList.add(inventorySearchIndex);
          searchService.addOrUpdateInventorySearchIndexWithList(inventorySearchIndexList);
        }

      }
    } catch (Exception e) {
      LOG.error("保存库存信息时出现异常！");
      LOG.error("信息：" + e.getMessage(), e);
      return false;
    }

    return true;
  }

  private ProductDaoManager productDaoManager;

  @Autowired
  private InventoryImporter inventoryImporter;

  public List<RepairOrder> countAgentAchievements(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countAgentAchievements(shopId, startTime, endTime);
  }

  public double countServiceAgentAchievements(long repair_order_id, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countServiceAgentAchievements(repair_order_id, startTime, endTime);

  }

  public double countItemAgentAchievements(long repair_order_id, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countItemAgentAchievements(repair_order_id, startTime, endTime);
  }

  @Override
  public void updatePurchaseInventoryStatus(PurchaseInventoryDTO purchaseInventoryDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    txnWriter.updatePurchaseInventoryStatus(purchaseInventoryDTO);
  }

  @Override
  public List<PurchaseInventory> getPurchaseInventoryByShopIdAndSupplierId(Long shopId, Long supplierId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getPurchaseInventoryByShopIdAndSupplierId(shopId, supplierId);
  }

  @Override
  public void updatePurchaseInventorySupplier(SupplierDTO supplierDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      List<PurchaseInventory> purchaseInventories = getPurchaseInventoryByShopIdAndSupplierId(supplierDTO.getShopId(), supplierDTO.getId());
      if (null != purchaseInventories && purchaseInventories.size() > 0) {
        for (PurchaseInventory purchaseInventory : purchaseInventories) {
          purchaseInventory.setSupplierId(supplierDTO.getId());
          txnWriter.update(purchaseInventory);
        }
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  @Override
  public List<PurchaseOrder> getPurchaseOrderByShopIdAndSupplierId(Long shopId, Long supplierId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getPurchaseOrderByShopIdAndSupplierId(shopId, supplierId);
  }

  @Override
  public void updatePurchaseOrderSupplier(SupplierDTO supplierDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      List<PurchaseOrder> purchaseOrders = getPurchaseOrderByShopIdAndSupplierId(supplierDTO.getShopId(), supplierDTO.getId());
      if (null != purchaseOrders && purchaseOrders.size() > 0) {
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
          purchaseOrder.setSupplierId(supplierDTO.getId());
          txnWriter.update(purchaseOrder);
        }
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  @Override
  public void updatePurchaseOrderStatus(Long shopId, Long purchaseOrderId, OrderStatus purchaseOrderTypeStatus, Long userId, Long inventoryVestDate) throws BcgogoException {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      OrderStatus lastOrderStatus = null;
      List<PurchaseOrder> purchaseOrders = txnWriter.getPurchaseOrderById(purchaseOrderId, shopId);
      if (CollectionUtils.isNotEmpty(purchaseOrders)) {
        lastOrderStatus = purchaseOrders.get(0).getStatusEnum();
        purchaseOrders.get(0).setStatusEnum(purchaseOrderTypeStatus);
        if(inventoryVestDate!=null){
          purchaseOrders.get(0).setInventoryVestDate(inventoryVestDate);
        }
        txnWriter.update(purchaseOrders.get(0));
        getOrderStatusChangeLogService().saveOrderStatusChangeLog(new OrderStatusChangeLogDTO(shopId, userId, purchaseOrderTypeStatus, lastOrderStatus, purchaseOrderId, OrderTypes.PURCHASE));
        txnWriter.commit(status);
      }
    } catch (Exception e) {
      LOG.error("updatePurchaseOrderStatus error: updatePurchaseOrderStatus= "
        + purchaseOrderId + "new Status" + purchaseOrderTypeStatus + e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }
  }

  @Override
  public void initPurchaseInventoryStatus(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO) throws BcgogoException {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    if (purchaseInventoryDTO.getStatus() == null || ((Long) 0L).equals(purchaseInventoryDTO.getStatus())) {
      List<OrderIndex> orderIndexList = searchService.getOrderIndexByOrderId(shopId, purchaseInventoryDTO.getId(),
        OrderTypes.INVENTORY, null, null);
      if (orderIndexList != null && orderIndexList.size() > 0) {
        OrderStatus orderStatus = orderIndexList.get(0).getOrderStatusEnum();
        purchaseInventoryDTO.setStatus(orderStatus);
        this.updatePurchaseInventoryStatus(purchaseInventoryDTO);
      } else {
        LOG.info("purchaseInventoryId = " + purchaseInventoryDTO.getId()
          + "exist in PurchaseInventory but not exist in OrderIndex");
      }
    }
  }

  @Override
  public String checkInventoryAmount(ModelMap model, Long shopId, PurchaseInventoryDTO purchaseInventoryDTO,
                                     List<InventorySearchIndex> inventorySearchIndexList) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    productService = this.getProductService();
    PurchaseInventoryItemDTO[] itemDTOs = purchaseInventoryDTO.getItemDTOs();
    Long[] productLocalInfoIds = new Long[itemDTOs.length];
    for (int i = 0; i < itemDTOs.length; i++) {
      productLocalInfoIds[i] = itemDTOs[i].getProductId();
    }
    Map<Long,InventorySearchIndex> inventorySearchIndexMap = searchService.getInventorySearchIndexMapByProductIds(shopId,productLocalInfoIds);
    InventorySearchIndex inventorySearchIndex = null;
    for (int i = 0; i < itemDTOs.length; i++) {
      inventorySearchIndex = inventorySearchIndexMap.get(itemDTOs[i].getProductId());
      if(inventorySearchIndex!=null){
        inventoryService.caculateBeforeLimit(inventorySearchIndex.toDTO(), purchaseInventoryDTO.getInventoryLimitDTO());
        double inventoryAmount = inventorySearchIndex.getAmount() == null ? 0d : inventorySearchIndex.getAmount();
        double itemAmount = itemDTOs[i].getAmount() == null ? 0d : itemDTOs[i].getAmount();
        double recommendPrice = inventorySearchIndex.getRecommendedPrice() == null ? 0d : inventorySearchIndex.getRecommendedPrice();
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTOs[i].getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTOs[i].getUnit(), productLocalInfoDTO)) {
          itemAmount = itemAmount * productLocalInfoDTO.getRate();
          recommendPrice = recommendPrice * productLocalInfoDTO.getRate();
        }
        if (itemAmount > inventoryAmount) {
          model.put("purchaseInventoryDTO", purchaseInventoryDTO);
          model.put("purchaseInventoryMessage", RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_SHORTAGE);
          model.put("purchaseInventoryMessageInfo", "【" + inventorySearchIndex.getProductName() + "】" + RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_SALE);
          LOG.debug(inventorySearchIndex.getProductName() + "Amount shortage can't cancel");
          return RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_SHORTAGE;
        } else {
          inventorySearchIndex.setAmount(inventoryAmount - itemAmount);
          inventorySearchIndexList.add(inventorySearchIndex);
          if (UnitUtil.isStorageUnit(itemDTOs[i].getUnit(), productLocalInfoDTO)) {   //作废大单位单据
            itemDTOs[i].setInventoryAmount((inventoryAmount - itemAmount) / productLocalInfoDTO.getRate());
            itemDTOs[i].setRecommendedPrice(recommendPrice * productLocalInfoDTO.getRate());
          } else {
            itemDTOs[i].setInventoryAmount(inventoryAmount - itemAmount);
            itemDTOs[i].setRecommendedPrice(recommendPrice);
          }
        }
        inventoryService.caculateAfterLimit(inventorySearchIndex.toDTO(), purchaseInventoryDTO.getInventoryLimitDTO());
      }
    }
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    return RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_CANCELED;
  }

  @Override
  public String checkInventoryAmountByStoreHouse(ModelMap model, Long shopId,PurchaseInventoryDTO purchaseInventoryDTO,
                                                 List<InventorySearchIndex> inventorySearchIndexList,List<StoreHouseInventoryDTO> storeHouseInventoryDTOList) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    PurchaseInventoryItemDTO[] itemDTOs = purchaseInventoryDTO.getItemDTOs();
    Long[] productLocalInfoIds = new Long[itemDTOs.length];
    for (int i = 0; i < itemDTOs.length; i++) {
      productLocalInfoIds[i] = itemDTOs[i].getProductId();
    }

    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    storeHouseInventoryDTOMap.putAll(storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, purchaseInventoryDTO.getStorehouseId(), productLocalInfoIds));

    Map<Long,InventorySearchIndex> inventorySearchIndexMap = searchService.getInventorySearchIndexMapByProductIds(shopId,productLocalInfoIds);
    StoreHouseInventoryDTO storeHouseInventoryDTO = null;
    InventorySearchIndex inventorySearchIndex = null;
    for (int i = 0; i < itemDTOs.length; i++) {
      storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTOs[i].getProductId());
      if (storeHouseInventoryDTO == null) {
        storeHouseInventoryDTO = new StoreHouseInventoryDTO(purchaseInventoryDTO.getStorehouseId(), itemDTOs[i].getProductId(), 0d);
      }
      inventorySearchIndex = inventorySearchIndexMap.get(itemDTOs[i].getProductId());
      if (inventorySearchIndex != null) {
        inventoryService.caculateBeforeLimit(inventorySearchIndex.toDTO(), purchaseInventoryDTO.getInventoryLimitDTO());
        double inventoryAmount = storeHouseInventoryDTO.getAmount();
        double itemAmount = itemDTOs[i].getAmount() == null ? 0d : itemDTOs[i].getAmount();
        double recommendPrice = inventorySearchIndex.getRecommendedPrice() == null ? 0d : inventorySearchIndex.getRecommendedPrice();
        ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(itemDTOs[i].getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTOs[i].getUnit(), productLocalInfoDTO)) {
          itemAmount = itemAmount * productLocalInfoDTO.getRate();
          recommendPrice = recommendPrice * productLocalInfoDTO.getRate();
        }
        if (itemAmount > inventoryAmount) {
          model.put("purchaseInventoryDTO", purchaseInventoryDTO);
          model.put("purchaseInventoryMessage", RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_SHORTAGE);
          model.put("purchaseInventoryMessageInfo", "【" + inventorySearchIndex.getProductName() + "】" + RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_SALE);
          LOG.debug(inventorySearchIndex.getProductName() + "Amount shortage can't cancel");
          return RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_SHORTAGE;
        } else {
          storeHouseInventoryDTO.setAmount(inventoryAmount - itemAmount);
          storeHouseInventoryDTOList.add(storeHouseInventoryDTO);
          inventorySearchIndex.setAmount(inventorySearchIndex.getAmount() - itemAmount);
          inventorySearchIndexList.add(inventorySearchIndex);
          if (UnitUtil.isStorageUnit(itemDTOs[i].getUnit(), productLocalInfoDTO)) {   //作废大单位单据
            itemDTOs[i].setInventoryAmount((inventoryAmount - itemAmount) / productLocalInfoDTO.getRate());
            itemDTOs[i].setRecommendedPrice(recommendPrice * productLocalInfoDTO.getRate());
          } else {
            itemDTOs[i].setInventoryAmount(inventoryAmount - itemAmount);
            itemDTOs[i].setRecommendedPrice(recommendPrice);
          }
        }
        inventoryService.caculateAfterLimit(inventorySearchIndex.toDTO(), purchaseInventoryDTO.getInventoryLimitDTO());
      }
    }
    purchaseInventoryDTO.setItemDTOs(itemDTOs);
    return RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_CANCELED;
  }




  @Override
  public List<String> getSalesOrderCountAndSum(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<String> stringList = writer.getSalesOrderCountAndSum(shopId, startTime, endTime);
    return stringList;
  }



  public List<SmsBalance> smsBalanceMigrate(Pager pager) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SmsBalance> balanceList = writer.getSmsBalance(pager);
    return balanceList;
  }

  @Override
  public Integer countSmsBalance() {
    TxnWriter writer = txnDaoManager.getWriter();
    Integer totalRows = writer.countSmsBalance();
    return totalRows;
  }


  @Override
  public WashOrderDTO getWashOrder(Long washOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    WashOrder washOrder = writer.getById(WashOrder.class, washOrderId);
    if (washOrder != null) {
      WashOrderDTO washOrderDTO = washOrder.toDTO();
      if (washOrderDTO != null) {
        return washOrderDTO;
      }
    }
    return null;
  }

  public List<SalesOrderDTO> getSalesOrderDTOListByCustomerId(long shopId, long customerId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrder> salesOrderList = writer.getSalesOrderDTOListByCustomerId(shopId, customerId);
    List<SalesOrderDTO> salesOrderDTOList = new ArrayList<SalesOrderDTO>();
    if (salesOrderList != null && salesOrderList.size() > 0) {
      for (SalesOrder salesOrder : salesOrderList) {
        if (salesOrder == null) {
          continue;
        }
        SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
        salesOrderDTOList.add(salesOrderDTO);
      }
      return salesOrderDTOList;
    }
    return null;
  }

  @Override
  public String updateReceivable(ReceivableDTO receivableDTO) throws Exception {

    StringBuffer stringBuffer = new StringBuffer();

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (receivableDTO != null && receivableDTO.getId() != null) {
        Receivable receivable = writer.getById(Receivable.class, receivableDTO.getId());

        Long orderId = receivable.getOrderId();
        if (orderId == null) {
          LOG.error("TxnService.java");
          LOG.error("method=updateReceivable");
          LOG.error("shopId:" + receivable.getShopId() + ",receivableId:" + receivable.getId() + "orderId为空");
          stringBuffer.append("shopId:" + receivable.getShopId() + ",receivableId:" + receivable.getId() + "orderId为空");
        }

        boolean isSetting = false;
        int orderNumByOrderId = 0;

        WashOrder washOrder = writer.getById(WashOrder.class, orderId);
        if (washOrder != null) {
          receivable.setOrderTypeEnum(OrderTypes.WASH);
          receivable.setStatusEnum(ReceivableStatus.FINISH);
          isSetting = true;
          orderNumByOrderId++;
        }

        if (!isSetting) {
          SalesOrder salesOrder = writer.getById(SalesOrder.class, orderId);
          if (salesOrder != null) {
            isSetting = true;
            orderNumByOrderId++;

            receivable.setOrderTypeEnum(OrderTypes.SALE);
            if (OrderStatus.SALE_DONE.equals(salesOrder.getStatusEnum()) || OrderStatus.SALE_DEBT_DONE == salesOrder.getStatusEnum()) {
              receivable.setStatusEnum(ReceivableStatus.FINISH);
            } else {
              receivable.setStatusEnum(ReceivableStatus.REPEAL);
            }
          }
        }
        if (!isSetting) {
          RepairOrder repairOrder = writer.getById(RepairOrder.class, orderId);
          if (repairOrder != null) {
            isSetting = true;
            orderNumByOrderId++;

            receivable.setOrderTypeEnum(OrderTypes.REPAIR);

            if (OrderStatus.REPAIR_REPEAL == repairOrder.getStatusEnum()) {
              receivable.setStatusEnum(ReceivableStatus.REPEAL);
            } else {
              receivable.setStatusEnum(ReceivableStatus.FINISH);
            }
          }
        }

        if (!isSetting) {
          MemberCardOrder memberCardOrder = writer.getById(MemberCardOrder.class, orderId);
          if (memberCardOrder != null) {
            isSetting = true;
            orderNumByOrderId++;
            receivable.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
            receivable.setStatusEnum(ReceivableStatus.FINISH);
          }
        }

        if (!isSetting) {
          WashBeautyOrder washBeautyOrder = writer.getById(WashBeautyOrder.class, orderId);
          if (washBeautyOrder != null) {
            isSetting = true;
            orderNumByOrderId++;
            receivable.setOrderTypeEnum(OrderTypes.WASH_BEAUTY);
            receivable.setStatusEnum(ReceivableStatus.FINISH);
          }
        }

        if (orderNumByOrderId > 1 || orderNumByOrderId == 0) {
          LOG.error("TxnService.java");
          LOG.error("method=updateReceivable");
          LOG.error("shopId:" + receivable.getShopId() + ",OrderId:" + orderId + "orderId在order表中有" + String.valueOf(orderNumByOrderId) + "条记录");
          stringBuffer.append("shopId:" + receivable.getShopId() + ",OrderId:" + orderId + "orderId在order表中有" + String.valueOf(orderNumByOrderId) + "条记录");
        }
        receivable.setDiscount(receivable.getDiscount() == null ? 0 : receivable.getDiscount());
        receivable.setTotal(receivable.getTotal() == null ? 0 : receivable.getTotal());
        if(receivable.getCash() == null && receivable.getBankCard() == null && receivable.getCheque() == null && receivable.getMemberBalancePay() == null){
          receivable.setCash(receivable.getSettledAmount());
        }
        receivable.setAccumulatePointsPay(0d);


        double total = receivable.getSettledAmount() + receivable.getDebt() + receivable.getDiscount();
        receivable.setTotal(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION));

        writer.saveOrUpdate(receivable);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
    return stringBuffer.toString();
  }

  public List<RepairOrderDTO> getRepairOrderDTOListByCustomerId(long shopId, long customerId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrder> repairOrderList = writer.getRepairOrderDTOListByCustomerId(shopId, customerId);
    List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
    if (repairOrderList != null && repairOrderList.size() > 0) {
      for (RepairOrder repairOrder : repairOrderList) {
        if (repairOrder == null) {
          continue;
        }
        RepairOrderDTO repairOrderDTO = repairOrder.toDTO();
        repairOrderDTOList.add(repairOrderDTO);
      }
      return repairOrderDTOList;
    }
    return null;
  }

  public long countReceivableDTOByShopId(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countReceivableDTOByShopId(shopId);
  }

  @Override
  public List<ReceivableDTO> getReceivableDTOList(Long shopId, int pageNo, int pageSize) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Receivable> receivableList = writer.getReceivableDTOList(shopId, pageNo, pageSize);
    List<ReceivableDTO> receivableDTOList = new ArrayList<ReceivableDTO>();

    if (receivableList != null && receivableList.size() > 0) {
      for (Receivable receivable : receivableList) {
        if (receivable == null) {
          continue;
        }
        ReceivableDTO receivableDTO = receivable.toDTO();
        receivableDTOList.add(receivableDTO);
      }
      return receivableDTOList;
    }
    return null;
  }

  @Override
  public ReceivableDTO getReceivableDTOByShopIdAndOrderId(long shopId, long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Receivable receivable = writer.getReceivableByShopIdAndOrderId(shopId, orderId);
    ReceivableDTO receivableDTO = null;
    if (receivable != null) {
      receivableDTO = receivable.toDTO();
    }
    return receivableDTO;
  }

  public List<PurchaseOrderDTO> getPurchaseOrderDTOListByShopId(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseOrder> purchaseOrderList = writer.getPurchaseOrderDTOListByShopId(shopId, startTime, endTime);
    List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();

    if (purchaseOrderList != null && purchaseOrderList.size() > 0) {
      for (PurchaseOrder purchaseOrder : purchaseOrderList) {
        if (purchaseOrder == null) {
          continue;
        }
        PurchaseOrderDTO purchaseOrderDTO = purchaseOrder.toDTO();
        purchaseOrderDTOList.add(purchaseOrderDTO);
      }
      return purchaseOrderDTOList;
    }
    return null;
  }

  public List<PurchaseInventoryDTO> getPurchaseInventoryDTOByShopId(long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventory> purchaseInventoryList = writer.getPurchaseInventoryByShopId(shopId, null);
    List<PurchaseInventoryDTO> purchaseInventoryDTOList = new ArrayList<PurchaseInventoryDTO>();

    if (purchaseInventoryList != null && purchaseInventoryList.size() > 0) {
      for (PurchaseInventory purchaseInventory : purchaseInventoryList) {
        if (purchaseInventory == null) {
          continue;
        }

        PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
        purchaseInventoryDTOList.add(purchaseInventoryDTO);
      }
      return purchaseInventoryDTOList;
    }
    return null;
  }


  @Override
  public List<RepairOrderDTO> getRepairOrderDTOListByCreated(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrder> repairOrderList = writer.getRepairOrderDTOListByCreated(shopId, startTime, endTime);
    List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
    if (repairOrderList != null) {
      for (RepairOrder repairOrder : repairOrderList) {
        RepairOrderDTO repairOrderDTO = repairOrder.toDTO();
        repairOrderDTOList.add(repairOrderDTO);
      }
      return repairOrderDTOList;
    }
    return null;
  }

  public long countPurchaseInventoryByShopId(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countPurchaseInventoryByShopId(shopId, startTime, endTime);
  }

  @Override
  public List<PurchaseInventoryDTO> getPurchaseInventoryDTOList(long shopId, int pageNo, int pageSize, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventory> purchaseInventoryList = writer.getPurchaseInventoryDTOList(shopId, pageNo, pageSize, startTime, endTime);
    List<PurchaseInventoryDTO> purchaseInventoryDTOList = new ArrayList<PurchaseInventoryDTO>();

    if (purchaseInventoryList != null && purchaseInventoryList.size() > 0) {
      for (PurchaseInventory purchaseInventory : purchaseInventoryList) {
        if (purchaseInventory == null) {
          continue;
        }
        PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
        purchaseInventoryDTOList.add(purchaseInventoryDTO);
      }
      return purchaseInventoryDTOList;
    }
    return null;
  }

  @Override
  public List<RepairOrderItem> getRepairOrderItemByProductId(long shopId, long productId, OrderStatus status) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if (status == null) {
      return writer.getRepairOrderItemsByProductId(shopId, productId);
    } else {
      List<RepairOrder> repairOrders = writer.getRepairOrdersByShopId(shopId, status);
      List<RepairOrderItem> repairOrderItems = new ArrayList<RepairOrderItem>();
      if (CollectionUtils.isNotEmpty(repairOrders)) {
        for (RepairOrder repairOrder : repairOrders) {
          List<RepairOrderItem> tempRepairOrderItems = new ArrayList<RepairOrderItem>();
          tempRepairOrderItems = writer.getRepairOrderItemsByOrderId(repairOrder.getId());
          if (CollectionUtils.isNotEmpty(tempRepairOrderItems)) {
            repairOrderItems.addAll(tempRepairOrderItems);
          }
        }
      }
      return repairOrderItems;
    }

  }

  @Override
  public List<SalesOrderItem> getSalesOrderItemByProductId(long shopId, long productId, OrderStatus status) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if (status == null) {
      return writer.getSalesOrderItemsByProductId(shopId, productId);
    } else {
      List<SalesOrder> salesOrders = writer.getSalesOrderByShopId(shopId, status);
      List<SalesOrderItem> SalesOrderItems = new ArrayList<SalesOrderItem>();
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        for (SalesOrder salesOrder : salesOrders) {
          List<SalesOrderItem> tempSalesOrderItems = new ArrayList<SalesOrderItem>();
          tempSalesOrderItems = writer.getSalesOrderItemsByOrderId(salesOrder.getId());
          if (CollectionUtils.isNotEmpty(tempSalesOrderItems)) {
            SalesOrderItems.addAll(tempSalesOrderItems);
          }
        }
      }
      return SalesOrderItems;
    }
  }


  @Override
  public List<PurchaseInventoryItem> getPurchaseInventoryItemByProductId(long shopId, long productId, OrderStatus status) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventory> purchaseInventories = writer.getPurchaseInventoryByShopId(shopId, status);
    List<PurchaseInventoryItem> returnPurchaseInventoryItems = new ArrayList<PurchaseInventoryItem>();
    if (CollectionUtils.isNotEmpty(purchaseInventories)) {
      for (PurchaseInventory purchaseInventory : purchaseInventories) {
        List<PurchaseInventoryItem> purchaseInventoryItems = writer.getPurchaseInventoryItemsByProductId(purchaseInventory.getId(), productId);
        if (CollectionUtils.isNotEmpty(purchaseInventoryItems)) {
          for (PurchaseInventoryItem purchaseInventoryItem : purchaseInventoryItems) {
            returnPurchaseInventoryItems.add(purchaseInventoryItem);
          }
        }
      }
    }
    return returnPurchaseInventoryItems;
  }

  @Override
  public List<PurchaseOrderItem> getPurchaseOrderItemByProductId(long shopId, long productId, Long status) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseOrder> purchaseOrders = writer.getPurchaseOrderByShopId(shopId, status);
    List<PurchaseOrderItem> returnPurchaseOrderItems = new ArrayList<PurchaseOrderItem>();
    if (CollectionUtils.isNotEmpty(purchaseOrders)) {
      for (PurchaseOrder purchaseOrder : purchaseOrders) {
        List<PurchaseOrderItem> purchaseOrderItems = writer.getPurchaseOrderItemsByProductId(purchaseOrder.getId(), productId);
        if (CollectionUtils.isNotEmpty(purchaseOrderItems)) {
          for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            returnPurchaseOrderItems.add(purchaseOrderItem);
          }
        }
      }
    }
    return returnPurchaseOrderItems;
  }

  @Override
  public List<PurchaseReturnItem> getPurchaseReturnItemByProdctId(long shopId, long productId, Long status) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseReturn> purchaseReturns = writer.getPurchaseReturnByShopId(shopId, status);
    List<PurchaseReturnItem> returnPurchaseReturnItems = new ArrayList<PurchaseReturnItem>();
    if (CollectionUtils.isNotEmpty(purchaseReturns)) {
      for (PurchaseReturn purchaseReturn : purchaseReturns) {
        List<PurchaseReturnItem> purchaseReturnItems = writer.getPurchaseReturnItemsByProdctId(purchaseReturn.getId(), productId);
        if (CollectionUtils.isNotEmpty(purchaseReturnItems)) {
          for (PurchaseReturnItem purchaseReturnItem : purchaseReturnItems) {
            returnPurchaseReturnItems.add(purchaseReturnItem);
          }
        }
      }
    }
    return returnPurchaseReturnItems;
  }

  @Override
  public List<RepairRemindEvent> getRepairRemindEventByProductId(long shopId, long productId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getRepairRemindEventsByProductId(shopId, null, productId);
  }

  @Override
  public List<InventoryRemindEvent> getInventoryRemindEventByProductId(Long shopId, Long productLocalInfoId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInventoryRemindEventByProductId(shopId, productLocalInfoId);
  }

  @Override
  public int countSalesOrderByVestDate(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countSalesOrderByVestDate(shopId, startTime, endTime);
  }


  public List<SalesOrderDTO> getSalesOrderListByPager(long shopId, long startTime, long endTime,
                                                      Pager pager) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrder> salesOrderList = writer.getSalesOrderListByPager(shopId, startTime, endTime, pager);
    if(CollectionUtils.isEmpty(salesOrderList)){
      return null;
    }

    List<SalesOrderDTO> salesOrderDTOList = new ArrayList<SalesOrderDTO>();
    for (SalesOrder salesOrder : salesOrderList) {
      if (salesOrder == null) {
        continue;
      }
      SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
      salesOrderDTOList.add(salesOrderDTO);
    }
    return salesOrderDTOList;
  }

  @Override
  public int countRepairOrderByVestDate(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countRepairOrderByVestDate(shopId, startTime, endTime);
  }

  public List<RepairOrderDTO> getRepairOrderListByPager(long shopId, long startTime, long endTime,
                                                        Pager pager) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrder> repairOrderList = writer.getRepairOrderListByPager(shopId, startTime, endTime, pager);
    List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
    if (repairOrderList != null && repairOrderList.size() > 0) {
      for (RepairOrder repairOrder : repairOrderList) {
        if (repairOrder == null) {
          continue;
        }
        RepairOrderDTO repairOrderDTO = repairOrder.toDTO();
        repairOrderDTOList.add(repairOrderDTO);
      }
      return repairOrderDTOList;
    }
    return null;
  }

  public List<RepealOrderDTO> getRepealOrderByShopIdAndOrderId(long shopId, long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepealOrder> repealOrderList = writer.getRepealOrderByShopIdAndOrderId(shopId, orderId);
    List<RepealOrderDTO> repealOrderDTOList = new ArrayList<RepealOrderDTO>();
    if (repealOrderList != null && repealOrderList.size() > 0) {
      for (RepealOrder repealOrder : repealOrderList) {
        if (repealOrder == null) {
          continue;
        }
        RepealOrderDTO repealOrderDTO = repealOrder.toDTO();
        repealOrderDTOList.add(repealOrderDTO);
      }
      return repealOrderDTOList;
    }
    return null;
  }


  /**
   * 从receivable表中计算出会员卡消费总额
   * @param shopId
   * @param memberId
   * @return
   */
  @Override
  public Double getMemberCardConsumeTotal(Long shopId, Long memberId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getMemberCardConsumeTotal(shopId, memberId);
  }

  /**
   * 保存欠款单
   *
   * @param debtDTO
   */
  public void saveDebtDTO(DebtDTO debtDTO) throws BcgogoException {
    if (debtDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);
    Long shopId = debtDTO.getShopId();
    if (shopId == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Debt debt = new Debt();
      debt.fromDTO(debtDTO, false);
      writer.save(debt);
      CustomerDTO customerDTO = ServiceManager.getService(ICustomerService.class).getCustomerById(debtDTO.getCustomerId());
      if(customerDTO!=null){
        saveRemindEvent(writer, debt,customerDTO.getName(), customerDTO.getMobile());
        //add by WLF 更新缓存
        updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, debt.getShopId());
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public WashOrderDTO getLastWashOrderDTO(Long shopId, Long customerId) {
    TxnWriter writer = txnDaoManager.getWriter();
    WashOrderDTO washOrderDTO = null;
    if (null != customerId) {
      WashOrder washOrder = writer.getLastWashOrderDTO(shopId, customerId);
      if (null != washOrder) {
        washOrderDTO = washOrder.toDTO();
      }
    }
    return washOrderDTO;
  }

  /**
   * 清空 repairOrderDTO 数组中的空数据
   *
   * @param repairOrderDTO
   * @return
   * @throws Exception
   */
  @Override
  public RepairOrderDTO removeBlankArrayOfRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO == null) return repairOrderDTO;
    RepairOrderItemDTO[] repairOrderItemDTOsOld = repairOrderDTO.getItemDTOs();
    if (!ArrayUtils.isEmpty(repairOrderItemDTOsOld)) {
      RepairOrderItemDTO repairOrderItemDTO = null;
      List<RepairOrderItemDTO> repairOrderItemDTOList = new ArrayList<RepairOrderItemDTO>();
      for (int i = 0; i < repairOrderItemDTOsOld.length; i++) {
        repairOrderItemDTO = repairOrderItemDTOsOld[i];
        if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) continue;
        repairOrderItemDTOList.add(repairOrderItemDTO);
      }
      repairOrderDTO.setItemDTOs(repairOrderItemDTOList.toArray(new RepairOrderItemDTO[repairOrderItemDTOList.size()]));
    }
    RepairOrderServiceDTO[] repairOrderServiceDTOsOld = repairOrderDTO.getServiceDTOs();
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      List<RepairOrderServiceDTO> repairOrderServiceDTOList = new ArrayList<RepairOrderServiceDTO>();
      RepairOrderServiceDTO repairOrderServiceDTO = null;
      for (int i = 0; i < repairOrderServiceDTOsOld.length; i++) {
        repairOrderServiceDTO = repairOrderServiceDTOsOld[i];
        if (StringUtils.isBlank(repairOrderServiceDTO.getService())) continue;
        repairOrderServiceDTOList.add(repairOrderServiceDTO);
      }
      repairOrderDTO.setServiceDTOs(repairOrderServiceDTOList.toArray(new RepairOrderServiceDTO[repairOrderServiceDTOList.size()]));
    }
    RepairRemindEventDTO[] repairRemindEventDTOsOld = repairOrderDTO.getRemindEventDTOs();
    if (!ArrayUtils.isEmpty(repairOrderDTO.getRemindEventDTOs())) {
      List<RepairRemindEventDTO> repairRemindEventDTOList = new ArrayList<RepairRemindEventDTO>();
      RepairRemindEventDTO repairRemindEventDTO = null;
      for (int i = 0; i < repairRemindEventDTOsOld.length; i++) {
        repairRemindEventDTO = repairRemindEventDTOsOld[i];
        if (StringUtils.isBlank(repairRemindEventDTO.getProductName())) continue;
        repairRemindEventDTOList.add(repairRemindEventDTO);
      }
      repairOrderDTO.setRemindEventDTOs(repairRemindEventDTOList.toArray(new RepairRemindEventDTO[repairRemindEventDTOList.size()]));
    }
    return repairOrderDTO;
  }


  @Override
  public int countServices(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countServices(shopId);
  }

  @Override
  public List<ServiceDTO> getServiceDTOByShopId(Long shopId, int pageNo, int maxPageSize) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<Service> services = writer.getServiceByShopId(shopId, pageNo, maxPageSize);

    if (null == services || services.size() == 0) {
      return null;
    }

    List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();

    for (Service service : services) {
      ServiceDTO serviceDTO = new ServiceDTO();
      BeanUtils.copyProperties(serviceDTO, service);
      serviceDTOs.add(serviceDTO);
    }

    return serviceDTOs;
  }

  @Override
  public List<Long> getAllServiceIdsByShopId(Long shopId, int start, int rows) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getAllServiceIdsByShopId(shopId, start, rows);
  }

  @Override
  public MemberCardOrderDTO saveMemberCardOrder(MemberCardOrderDTO memberCardOrderDTO) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(memberCardOrderDTO.getCustomerId());
    if(StringUtils.isNotBlank(memberCardOrderDTO.getMobile())){
      customerDTO.setMobile(memberCardOrderDTO.getMobile());
    }
    memberCardOrderDTO.setCustomerDTO(customerDTO);
    MemberDTO memberDTO = membersService.getMemberByCustomerId(memberCardOrderDTO.getShopId(), customerDTO.getId());
    if(memberDTO != null){
      memberCardOrderDTO.setOldMemberType(memberDTO.getType());
      memberCardOrderDTO.setOldMemberStatus(memberDTO.getStatus());
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      MemberCardOrder memberCardOrder = new MemberCardOrder(memberCardOrderDTO);
      writer.save(memberCardOrder);
      memberCardOrderDTO.setId(memberCardOrder.getId());

      if (CollectionUtils.isNotEmpty(memberCardOrderDTO.getMemberCardOrderItemDTOs())) {
        for (MemberCardOrderItemDTO memberCardOrderItemDTO : memberCardOrderDTO.getMemberCardOrderItemDTOs()) {
          memberCardOrderItemDTO.setShopId(memberCardOrderDTO.getShopId());
          memberCardOrderItemDTO.setMemberCardOrderId(memberCardOrderDTO.getId());

          memberCardOrderItemDTO.setAmount(memberCardOrderDTO.getTotal() - memberCardOrderDTO.getReceivableDTO().getDiscount());

          memberCardOrderItemDTO.setPrice(memberCardOrderDTO.getTotal());

          MemberCardOrderItem memberCardOrderItem = new MemberCardOrderItem(memberCardOrderItemDTO);

          writer.save(memberCardOrderItem);

          memberCardOrderItemDTO.setId(memberCardOrderItem.getId());
        }
      }

      List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = this.beforeSaveMemberCardOrderService(memberCardOrderDTO);
      if (CollectionUtils.isNotEmpty(memberCardOrderServiceDTOs)) {
        for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderServiceDTOs) {
          if (null == memberCardOrderServiceDTO.getServiceId()) {
            continue;
          }
          ServiceHistory serviceHistory = getServiceHistoryService().getOrSaveServiceHistoryByServiceId(memberCardOrderServiceDTO.getServiceId(), memberCardOrderDTO.getShopId());
          memberCardOrderServiceDTO.setServiceHistoryId(serviceHistory==null?null:serviceHistory.getId());
          if (null == memberCardOrderDTO.getMemberDTO().getId()) {
            MemberCardOrderService memberCardOrderService = new MemberCardOrderService(memberCardOrderServiceDTO);
            memberCardOrderService.setMemberCardOrderId(memberCardOrderDTO.getId());
            writer.save(memberCardOrderService);
            memberCardOrderServiceDTO.setId(memberCardOrderService.getId());
          } else {
            MemberService memberService = membersService.getMemberService(memberCardOrderDTO.getMemberDTO().getId(), memberCardOrderServiceDTO.getServiceId());
            if (!compareMemberCardOrderServiceDTOAndMemberService(memberService, memberCardOrderServiceDTO)) {
              MemberCardOrderService memberCardOrderService = new MemberCardOrderService(memberCardOrderServiceDTO);
              memberCardOrderService.setMemberCardOrderId(memberCardOrderDTO.getId());
              writer.save(memberCardOrderService);
              memberCardOrderServiceDTO.setId(memberCardOrderService.getId());
            }
          }
        }
      }
      memberCardOrderDTO.setNewMemberCardOrderServiceDTOs(memberCardOrderServiceDTOs);
      memberCardOrderDTO.getReceivableDTO().setOrderId(memberCardOrderDTO.getId());
      memberCardOrderDTO.getReceivableDTO().setOrderType(OrderTypes.MEMBER_BUY_CARD);
      memberCardOrderDTO.getReceivableDTO().setStatus(ReceivableStatus.FINISH);
      memberCardOrderDTO.getReceivableDTO().setShopId(memberCardOrderDTO.getShopId());
      memberCardOrderDTO.getReceivableDTO().setCustomerId(memberCardOrderDTO.getCustomerId());
      memberCardOrderDTO.getReceivableDTO().setVestDate(memberCardOrderDTO.getVestDate());

      Long payTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, memberCardOrderDTO.getRepayTime());
      //如果前台销售单有欠款没有添加还款时间，默认为系统时间三天后
      //BCSHOP-2040
      if (payTime == null || payTime.longValue() <= 0) {
        payTime = System.currentTimeMillis() + 72 * 3600000;
      }

      ReceivableDTO receivableDTO = memberCardOrderDTO.getReceivableDTO();
      receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getTotal());
      receivableDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
      receivableDTO.setRemindTime(payTime);
      ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
      ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
      if (!ArrayUtils.isEmpty(receivableDTO.getRecordDTOs())) {
        ReceptionRecordDTO[] recordDTOs = receivableDTO.getRecordDTOs();
        receivableHistory.setCheckNo(recordDTOs[0].getChequeNo());
      }
      writer.save(receivableHistory);

      ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
      receptionRecordDTO.setReceivableHistoryId(receivableHistory.getId());
      receptionRecordDTO.setDayType(DayType.OTHER_DAY);
      receptionRecordDTO.setAmount(receivableDTO.getSettledAmount());
      receptionRecordDTO.setPayeeId(memberCardOrderDTO.getCustomerId());
      receptionRecordDTO.setPayee(memberCardOrderDTO.getCustomerName());
      receptionRecordDTO.setReceiveTime(memberCardOrderDTO.getVestDate());
      receptionRecordDTO.setReceivableId(receivableDTO.getId());
      receptionRecordDTO.setMemberBalancePay(0.0);
      receptionRecordDTO.setAccumulatePoints(0);
      receptionRecordDTO.setAccumulatePointsPay(0.0);
      receptionRecordDTO.setCash(receivableDTO.getCash());
      receptionRecordDTO.setBankCard(receivableDTO.getBankCard());
      receptionRecordDTO.setCheque(receivableDTO.getCheque());

      if (!ArrayUtils.isEmpty(receivableDTO.getRecordDTOs())) {
        ReceptionRecordDTO[] recordDTOs = receivableDTO.getRecordDTOs();
        receptionRecordDTO.setChequeNo(recordDTOs[0].getChequeNo());
      }
      receptionRecordDTO.setMemberId(memberCardOrderDTO.getMemberDTO().getId());
      receptionRecordDTO.setShopId(memberCardOrderDTO.getShopId());
      receptionRecordDTO.setOrderId(memberCardOrderDTO.getId());
      receptionRecordDTO.setReceptionDate(System.currentTimeMillis());
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setOriginDebt(0.0);
      receptionRecordDTO.setRemainDebt(receivableDTO.getDebt());
      receptionRecordDTO.setDiscount(receivableDTO.getDiscount());
      receptionRecordDTO.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
      receptionRecordDTO.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
      receptionRecordDTO.setOrderTotal(memberCardOrderDTO.getTotal());
      ReceptionRecordDTO[] receptionRecordDTOs = new ReceptionRecordDTO[1];
      receptionRecordDTOs[0] = receptionRecordDTO;
      memberCardOrderDTO.getReceivableDTO().setRecordDTOs(receptionRecordDTOs);

      this.createOrUpdateReceivable(writer, receivableDTO);

      //添加欠款信息
      if (receivableDTO.getDebt() > 0.001) {
        Debt debt = new Debt();
        debt.setOrderType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);
        debt.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
        debt.setContent("会员卡购卡续卡");
        debt.setCustomerId(memberCardOrderDTO.getCustomerId());
        debt.setDebt(memberCardOrderDTO.getReceivableDTO().getDebt());
        debt.setOrderId(memberCardOrderDTO.getId());
        debt.setOrderTime(memberCardOrderDTO.getEditDate());
        debt.setRecievableId(memberCardOrderDTO.getReceivableDTO().getId());
        debt.setSettledAmount(memberCardOrderDTO.getReceivableDTO().getSettledAmount());
        debt.setShopId(memberCardOrderDTO.getShopId());
        debt.setTotalAmount(memberCardOrderDTO.getReceivableDTO().getTotal());
        debt.setPayTime(memberCardOrderDTO.getVestDate());
        debt.setRemindTime(payTime);
        debt.setStatus(TxnConstant.DebtStatus.DEBT_STATUS_ARREARS);
        debt.setMaterial(memberCardOrderDTO.getMemberCardName());
        debt.setRemindStatus(UserConstant.Status.ACTIVITY);
        writer.save(debt);
        saveRemindEvent(writer, debt,memberCardOrderDTO.getCustomerName(),memberCardOrderDTO.getMobile());
        //add by WLF 更新缓存
        updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, debt.getShopId());
      }
      writer.commit(status);
      return memberCardOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 数据库中的MemberService和需要保存的 MemberCardOrderService的数据是否相同
   *
   * @param memberService
   * @param memberCardOrderServiceDTO
   * @return
   */
  public boolean compareMemberCardOrderServiceDTOAndMemberService(MemberService memberService, MemberCardOrderServiceDTO memberCardOrderServiceDTO) {
    if (null == memberService || null == memberCardOrderServiceDTO) {
      return false;
    } else {
      if (memberService.getTimes().equals(memberCardOrderServiceDTO.getBalanceTimes()) &&
        memberService.getDeadline().equals(memberCardOrderServiceDTO.getDeadline()) &&
        (null == memberService.getVehicles() ? "" : memberService.getVehicles()).equals((
          null == memberCardOrderServiceDTO.getVehicles() ? "" : memberCardOrderServiceDTO.getVehicles()))) {
        return true;
      }
    }
    return false;
  }

  /**
   * 对变更的服务进行整合
   *
   * @param memberCardOrderDTO
   * @return
   * @throws Exception
   */
  public List<MemberCardOrderServiceDTO> beforeSaveMemberCardOrderService(MemberCardOrderDTO memberCardOrderDTO) throws Exception {
    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = new ArrayList<MemberCardOrderServiceDTO>();
    Map<Long, MemberServiceDTO> memberServiceDTOMap = new HashMap<Long, MemberServiceDTO>();
    Map<Long, MemberCardOrderServiceDTO> memberCardOrderServiceDTOMap = new HashMap<Long, MemberCardOrderServiceDTO>();
    if (null != memberCardOrderDTO && null != memberCardOrderDTO.getMemberDTO()) {
      memberServiceDTOMap = MemberServiceDTO.listToMap(memberCardOrderDTO.getMemberDTO().getMemberServiceDTOs());
    }
    //只是从页面返回来的service（里面可能不包括原来会员的service）
    if (null != memberCardOrderDTO && CollectionUtils.isNotEmpty(memberCardOrderDTO.getMemberCardOrderServiceDTOs())) {
      for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderDTO.getMemberCardOrderServiceDTOs()) {
        if (null == memberCardOrderServiceDTO.getServiceId()) {
          continue;
        }

        memberCardOrderServiceDTO.setShopId(memberCardOrderDTO.getShopId());
        memberCardOrderServiceDTO.setMemberCardOrderId(memberCardOrderDTO.getId());

        if(!NumberUtil.isEqualNegativeOne(memberCardOrderServiceDTO.getCardTimes()) && TimesStatus.UNLIMITED.getStatus().equals(memberCardOrderServiceDTO.getCardTimesStatus()))
        {
          memberCardOrderServiceDTO.setCardTimesLimitType(ServiceLimitTypes.LIMITED);
        }
        else if(NumberUtil.isEqualNegativeOne(memberCardOrderServiceDTO.getCardTimes()) && TimesStatus.UNLIMITED.getStatus().equals(memberCardOrderServiceDTO.getCardTimesStatus()))
        {
          memberCardOrderServiceDTO.setCardTimesLimitType(ServiceLimitTypes.UNLIMITED);
        }
        //设置member_card_order_service中times的值
        if(!NumberUtil.isEqualNegativeOne(memberCardOrderServiceDTO.getBalanceTimes()))
        {
          memberCardOrderServiceDTO.setBalanceTimesLimitType(ServiceLimitTypes.LIMITED);
          memberCardOrderServiceDTO.setIncreasedTimesLimitType(ServiceLimitTypes.LIMITED);
          if (!NumberUtil.isEqualNegativeOne(memberCardOrderServiceDTO.getOldTimes())) {
            if (null != memberServiceDTOMap.get(memberCardOrderServiceDTO.getServiceId())) {
              memberCardOrderServiceDTO.setOldTimesLimitType(ServiceLimitTypes.LIMITED);
            }

            memberCardOrderServiceDTO.setIncreasedTimes((null == memberCardOrderServiceDTO.getBalanceTimes() ? 0 : memberCardOrderServiceDTO.getBalanceTimes())
              - (null == memberCardOrderServiceDTO.getOldTimes() ? 0 : memberCardOrderServiceDTO.getOldTimes()));
          } else {
            memberCardOrderServiceDTO.setOldTimesLimitType(ServiceLimitTypes.UNLIMITED);
            memberCardOrderServiceDTO.setIncreasedTimes(memberCardOrderServiceDTO.getBalanceTimes());
          }
        } else {
          memberCardOrderServiceDTO.setBalanceTimesLimitType(ServiceLimitTypes.UNLIMITED);
          memberCardOrderServiceDTO.setBalanceTimes(TxnConstant.UNLIMITED);
          if (!NumberUtil.isEqualNegativeOne(memberCardOrderServiceDTO.getOldTimes())) {
            memberCardOrderServiceDTO.setIncreasedTimes(TxnConstant.UNLIMITED);
            memberCardOrderServiceDTO.setIncreasedTimesLimitType(ServiceLimitTypes.UNLIMITED);
            if (null != memberServiceDTOMap.get(memberCardOrderServiceDTO.getServiceId())) {
              memberCardOrderServiceDTO.setOldTimesLimitType(ServiceLimitTypes.LIMITED);
            }
          } else {
            memberCardOrderServiceDTO.setIncreasedTimes(0);
            memberCardOrderServiceDTO.setIncreasedTimesLimitType(ServiceLimitTypes.LIMITED);
            memberCardOrderServiceDTO.setOldTimesLimitType(ServiceLimitTypes.UNLIMITED);
          }

        }
        //设置member_card_order_service中deadline的值
        if(!"不限期".equals(memberCardOrderServiceDTO.getDeadlineStr()))
        {
          String deadlineStr = memberCardOrderServiceDTO.getDeadlineStr();
          Long deadline = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, deadlineStr);
          deadline = deadline.longValue()+86399000L;
          memberCardOrderServiceDTO.setDeadline(deadline);
        } else {
          memberCardOrderServiceDTO.setDeadline(Long.valueOf(TxnConstant.UNLIMITED.intValue()));
        }
      }
      memberCardOrderServiceDTOs.addAll(memberCardOrderDTO.getMemberCardOrderServiceDTOs());
    }

    if (null != memberCardOrderDTO) {
      memberCardOrderServiceDTOMap = MemberCardOrderServiceDTO.listToMap(memberCardOrderDTO.getMemberCardOrderServiceDTOs());
    }
    //获取那些被删掉的memberService记录进membercardOrderService
    if (null != memberCardOrderDTO && null != memberCardOrderDTO.getMemberDTO() &&
      CollectionUtils.isNotEmpty(memberCardOrderDTO.getMemberDTO().getMemberServiceDTOs())) {
      for (MemberServiceDTO memberServiceDTO : memberCardOrderDTO.getMemberDTO().getMemberServiceDTOs()) {
        if (null == memberCardOrderServiceDTOMap.get(memberServiceDTO.getServiceId())) {
          MemberCardOrderServiceDTO memberCardOrderServiceDTO = new MemberCardOrderServiceDTO();
          memberCardOrderServiceDTO.setVehicles(memberServiceDTO.getVehicles());
          memberCardOrderServiceDTO.setDeadline(memberServiceDTO.getDeadline());
          memberCardOrderServiceDTO.setDeadlineStr(memberServiceDTO.getDeadlineStr());
          memberCardOrderServiceDTO.setShopId(memberCardOrderDTO.getShopId());
          memberCardOrderServiceDTO.setServiceId(memberServiceDTO.getServiceId());
          ServiceDTO serviceDTO = getServiceById(memberServiceDTO.getServiceId());
          if (serviceDTO != null)
            memberCardOrderServiceDTO.setServiceName(serviceDTO.getName());
          memberCardOrderServiceDTO.setMemberCardOrderId(memberCardOrderDTO.getId());
          memberCardOrderServiceDTO.setOldTimes(memberServiceDTO.getTimes());
          if (NumberUtil.isEqualNegativeOne(memberCardOrderServiceDTO.getOldTimes())) {
            memberCardOrderServiceDTO.setOldTimesLimitType(ServiceLimitTypes.UNLIMITED);
          } else {
            memberCardOrderServiceDTO.setOldTimesLimitType(ServiceLimitTypes.LIMITED);
          }
          memberCardOrderServiceDTO.setCardTimes(0);
          memberCardOrderServiceDTO.setBalanceTimes(0);
          memberCardOrderServiceDTO.setBalanceTimesLimitType(ServiceLimitTypes.DELETE);
          memberCardOrderServiceDTO.setIncreasedTimes(0);
          memberCardOrderServiceDTO.setIncreasedTimesLimitType(ServiceLimitTypes.DELETE);
          memberCardOrderServiceDTOs.add(memberCardOrderServiceDTO);
        }
      }
    }

    return CollectionUtils.isNotEmpty(memberCardOrderServiceDTOs) ? memberCardOrderServiceDTOs : null;
  }

  /**
   * 根据shop_id和服务名称查找服务
   *
   * @param shopId      shop_id
   * @param serviceName 服务名称
   * @return
   */
  public List<ServiceDTO> getServiceByServiceNameAndShopId(long shopId, String serviceName) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> serviceList = writer.getServiceByServiceNameAndShopId(shopId, serviceName);
    if (serviceList != null && serviceList.size() > 0) {
      List<ServiceDTO> serviceDTOList = new ArrayList<ServiceDTO>();
      for (Service service : serviceList) {
        if (service == null) {
          continue;
        }
        serviceDTOList.add(service.toDTO());
      }
      return serviceDTOList;
    }
    return null;
  }

  /**
   * 获取所有洗车美容的项目
   *
   *
   * @param shopId
   * @return
   */
  @Override
  public List<ServiceDTO> getAllServiceDTOOfTimesByShopId(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.getServiceByWashBeauty(shopId, CategoryType.BUSINESS_CLASSIFICATION);
    List<ServiceDTO> serviceDTOs = null;
    if (CollectionUtils.isNotEmpty(services)) {
      serviceDTOs = new ArrayList<ServiceDTO>();
      for (Service service : services) {
        ServiceDTO serviceDTO = service.toDTO();
        serviceDTOs.add(serviceDTO);
      }
    }

    return serviceDTOs;
  }
  @Override
  public Map<Long, CategoryDTO> getServiceCategoryMapByServiceId(Long shopId, Long... serviceId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getServiceCategoryMapByServiceId(shopId, serviceId);
  }
  @Override
  public MemberCardOrderDTO getMemberCardOrderDTOById(Long shopId, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    if (null == orderId) {
      return null;
    }
    MemberCardOrder memberCardOrder = writer.getMemberCardOrderDTOById(shopId, orderId);

    if (null == memberCardOrder) {
      return null;
    }

    MemberCardOrderDTO memberCardOrderDTO = memberCardOrder.toDTO();

    List<MemberCardOrderItemDTO> memberCardOrderItemDTOs = getMemberCardOrderItemDTOByOrderId(shopId, orderId);
    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = getMemberCardOrderServiceDTOByOrderId(shopId, orderId);
    memberCardOrderDTO.setMemberCardOrderItemDTOs(memberCardOrderItemDTOs);
    memberCardOrderDTO.setMemberCardOrderServiceDTOs(memberCardOrderServiceDTOs);
    return memberCardOrderDTO;
  }

  @Override
  public List<MemberCardOrderItemDTO> getMemberCardOrderItemDTOByOrderId(Long shopId, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<MemberCardOrderItem> memberCardOrderItems = writer.getMemberCardOrderItemDTOByOrderId(shopId, orderId);

    List<MemberCardOrderItemDTO> memberCardOrderItemDTOs = null;
    if (CollectionUtils.isNotEmpty(memberCardOrderItems)) {
      memberCardOrderItemDTOs = new ArrayList<MemberCardOrderItemDTO>();
      for (MemberCardOrderItem memberCardOrderItem : memberCardOrderItems) {
        MemberCardOrderItemDTO memberCardOrderItemDTO = memberCardOrderItem.toDTO();
        memberCardOrderItemDTOs.add(memberCardOrderItemDTO);
      }
    }

    return memberCardOrderItemDTOs;
  }

  public List<MemberCardOrderServiceDTO> getMemberCardOrderServiceDTOByOrderId(Long shopId, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<MemberCardOrderService> memberCardOrderServices = writer.getMemberCardOrderServiceDTOByOrderId(shopId, orderId);

    List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs = null;
    if (CollectionUtils.isNotEmpty(memberCardOrderServices)) {
      memberCardOrderServiceDTOs = new ArrayList<MemberCardOrderServiceDTO>();
      for (MemberCardOrderService memberCardOrderService : memberCardOrderServices) {
        MemberCardOrderServiceDTO memberCardOrderServiceDTO = memberCardOrderService.toDTO();
        memberCardOrderServiceDTOs.add(memberCardOrderServiceDTO);
      }
    }

    return memberCardOrderServiceDTOs;
  }

  /**
   * 遍历每个service,把施工人保存到repairOrderDTO中，以满足现有的员工业绩统计
   *
   * @param repairOrderDTO
   */
  private void setServiceWorksAndProductSaler(RepairOrderDTO repairOrderDTO) {
    if (repairOrderDTO == null || repairOrderDTO.getServiceDTOs() == null ||
      repairOrderDTO.getServiceDTOs().length <= 0) {
      return;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    //施工人
    Set<String> allWorkers = new LinkedHashSet<String>();  //所有施工人

    for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
      if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
        continue;
      }
      if (StringUtil.isEmpty(repairOrderServiceDTO.getWorkers())) {
        continue;
      }
      Set<String> itemWorkersSet = new LinkedHashSet<String>();
      Set<Long> itemWorkerIdsSet = new LinkedHashSet<Long>();
      //遍历以逗号分隔的工人,如果没有就新增, 有的话setId到workerIds, 顺便去重.
      repairOrderServiceDTO.setWorkers(repairOrderServiceDTO.getWorkers().replace("，",","));
      for (String itemWorker : repairOrderServiceDTO.getWorkers().split(",")) {
        if (StringUtils.isBlank(itemWorker)) {
          continue;
        }
        itemWorker = itemWorker.trim().toLowerCase();
        List<SalesManDTO> salesManDTOs = userService.getSalesManDTOByCodeOrName(null, itemWorker, repairOrderDTO.getShopId());
        if (CollectionUtils.isNotEmpty(salesManDTOs)) {
          itemWorkerIdsSet.add(salesManDTOs.get(0).getId());
        } else {
          SalesManDTO newSalesMan = new SalesManDTO();
          newSalesMan.setName(itemWorker);
          newSalesMan.setShopId(repairOrderDTO.getShopId());
          newSalesMan.setStatus(SalesManStatus.ONTRIAL);
          try {
            userService.saveOrUpdateSalesMan(newSalesMan);
          } catch (Exception e) {
            LOG.warn("save or update salesMan fail.");
            LOG.error(e.getMessage(), e);
          }
          itemWorkerIdsSet.add(newSalesMan.getId());
        }
        itemWorkersSet.add(itemWorker);
        allWorkers.add(itemWorker);
      }
      String commaWorkers = CollectionUtil.collectionToCommaString(itemWorkersSet);
      String commaWorkerIds = CollectionUtil.collectionToCommaString(itemWorkerIdsSet);
      repairOrderServiceDTO.setWorkers(commaWorkers);
      repairOrderServiceDTO.setWorkerIds(commaWorkerIds);

    }
    if (CollectionUtils.isNotEmpty(allWorkers)) {
      repairOrderDTO.setServiceWorker(CollectionUtil.collectionToCommaString(allWorkers));
    } else {
      repairOrderDTO.setServiceWorker(TxnConstant.ASSISTANT_NAME);
    }

    //productSaler遍历, 没有的新增, 顺便去重
    if (StringUtils.isNotBlank(repairOrderDTO.getProductSaler())) {
      repairOrderDTO.setProductSaler(repairOrderDTO.getProductSaler().replace("，", ","));
      Set<String> salersSet = new LinkedHashSet<String>();
      for (String saler : repairOrderDTO.getProductSaler().split(",")) {
        if (StringUtils.isBlank(saler)) {
          continue;
        }
        saler = saler.trim().toLowerCase();
        List<SalesManDTO> salesManDTOs = userService.getSalesManDTOByCodeOrName(null, saler, repairOrderDTO.getShopId());
        if (CollectionUtils.isEmpty(salesManDTOs)) {
          SalesManDTO newSalesMan = new SalesManDTO();
          newSalesMan.setName(saler);
          newSalesMan.setShopId(repairOrderDTO.getShopId());
          newSalesMan.setStatus(SalesManStatus.ONTRIAL);
          try {
            userService.saveOrUpdateSalesMan(newSalesMan);
          } catch (Exception e) {
            LOG.warn("save or update salesMan fail.");
            LOG.error(e.getMessage(), e);
          }
        }
        salersSet.add(saler);
      }
      if (CollectionUtils.isNotEmpty(salersSet)) {
        repairOrderDTO.setProductSaler(CollectionUtil.collectionToCommaString(salersSet));
      } else {
        repairOrderDTO.setProductSaler(TxnConstant.ASSISTANT_NAME);
      }
    }else{
      repairOrderDTO.setProductSaler(TxnConstant.ASSISTANT_NAME);
    }
  }


  /**
   * 施工单结算完成后 更改会员相关信息, 并返回结算短信参数
   * 在提交时已做校验 这里只做部分校验
   *
   * @param repairOrderDTO
   */
  @Override
  public VelocityContext updateMemberInfo(RepairOrderDTO repairOrderDTO) {
    try {
      VelocityContext context = new VelocityContext();    //操作到此变量的都是为了提供会员结算短信内容。
      context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, 0);
      context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, 0);

      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);

      if (repairOrderDTO == null) {
        return context;
      }

      //如果施工单不包含会员结算信息 返回
      if (!memberCheckerService.containMemberAmount(repairOrderDTO) &&
        !memberCheckerService.containMemberCountConsume(repairOrderDTO)) {
        return context;
      }

      Member member = membersService.getMemberByShopIdAndMemberNo(repairOrderDTO.getShopId(), repairOrderDTO.getAccountMemberNo());
      if (member == null) {
        LOG.error("/TxnService.java");
        LOG.error("method=updateMemberInfo");
        LOG.error("shopId:" + repairOrderDTO.getShopId());
        LOG.error(MemberConstant.MEMBER_NOT_EXIST + "," + MemberConstant.AJAX_SUBMIT_FAILURE);
        LOG.info(repairOrderDTO.toString());
        return context;
      }

      MemberDTO memberDTO = member.toDTO();
      //如果使用会员储值金额进行结算 减去相应的金额
      if (memberCheckerService.containMemberAmount(repairOrderDTO)) {
        if (member.getBalance() == null || member.getBalance().doubleValue() <= 0 ||
          member.getBalance().doubleValue() < repairOrderDTO.getMemberAmount().doubleValue()) {
          LOG.error("/TxnService.java");
          LOG.error("method=updateMemberInfo");
          LOG.error("shopId:" + repairOrderDTO.getShopId());
          LOG.error(MemberConstant.MEMBER_BALANCE_NOT_ENOUGH + "," + MemberConstant.AJAX_SUBMIT_FAILURE);
          LOG.info(repairOrderDTO.toString());
          return context;
        }
        Double beforeBalance = memberDTO.getBalance();
        memberDTO.setBalance(memberDTO.getBalance() - repairOrderDTO.getMemberAmount());
        membersService.updateMember(memberDTO);
        member.fromDTO(memberDTO);
        Double afterBalance = member.getBalance();
        context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, NumberUtil.round(beforeBalance-afterBalance,NumberUtil.MONEY_PRECISION)); //2位小数问题
      }
      context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, member.getBalance());

      //如果不包含计次划卡项目 返回
      if (!memberCheckerService.containMemberCountConsume(repairOrderDTO)) {
        return context;
      }

      List<MemberServiceDTO> memberServiceDTOList = membersService.getMemberServiceEnabledByMemberId(repairOrderDTO.getShopId(), member.getId());
      if (CollectionUtils.isEmpty(memberServiceDTOList)) {
        LOG.error("/TxnService.java");
        LOG.error("method=updateMemberInfo");
        LOG.error("shopId:" + repairOrderDTO.getShopId());
        LOG.error(MemberConstant.SHOP_NO_CONTAIN_SERVICE + "," + MemberConstant.AJAX_SUBMIT_FAILURE);
        LOG.info(repairOrderDTO.toString());
        return context;
      }
      memberDTO.setMemberServiceDTOs(memberServiceDTOList);
      Map<String, String> remainItems = new LinkedHashMap<String, String>();    //初始化剩余项目次数
      for(MemberServiceDTO memberServiceDTO : memberServiceDTOList){
        Service service = getServiceById(repairOrderDTO.getShopId(), memberServiceDTO.getServiceId());
        if(service == null){
          LOG.error("/TxnService.java, method=updateMemberInfo, shopId:{}", repairOrderDTO.getShopId());
          LOG.error("会员member_id为:{}的客户具有的服务id:{}不存在", memberServiceDTO.getMemberId(), memberServiceDTO.getServiceId());
        }
        if(memberServiceDTO.getTimes()>0){  //有剩余次数才放进remainItems
          remainItems.put(service.getName(), String.valueOf(memberServiceDTO.getTimes()));
        }else if(memberServiceDTO.getTimes()==-1){
          remainItems.put(service.getName(), "无限");
        }
      }

      //遍历施工下的 每个 计次划卡项目 判断该会员的计次划卡服务是否含有
      Map<String, String> consumeItems = new LinkedHashMap<String, String>();
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (ConsumeType.MONEY == repairOrderServiceDTO.getConsumeType()) {
          continue;
        }
        List<ServiceDTO> serviceDTOList = this.getServiceByServiceNameAndShopId(repairOrderDTO.getShopId(), repairOrderServiceDTO.getService());
        if (CollectionUtils.isEmpty(serviceDTOList)) {
          LOG.error("/TxnService.java");
          LOG.error("method=updateMemberInfo");
          LOG.error("shopId:" + repairOrderDTO.getShopId());
          LOG.error(MemberConstant.SHOP_NO_CONTAIN_SERVICE + "," + MemberConstant.AJAX_SUBMIT_FAILURE);
          LOG.error(repairOrderDTO.toString());
          return context;
        }
        Long serviceId = serviceDTOList.get(0).getId();

        //获得该会员下的此施工项目 把次数减去1
        MemberService memberService = membersService.getMemberService(memberDTO.getId(), serviceId);
        if (memberService == null || memberService.getTimes() == null) {
          LOG.error("/TxnService.java");
          LOG.error("method=updateMemberInfo");
          LOG.error("shopId:" + repairOrderDTO.getShopId());
          LOG.error(MemberConstant.MEMBER_SERVICE_OUT_COUNT + "," + MemberConstant.AJAX_SUBMIT_FAILURE);
          LOG.info(repairOrderDTO.toString());
          return context;
        }
        consumeItems.put(serviceDTOList.get(0).getName(), "1");

        //如果是不限制次数的消费项目
        if (NumberUtil.isEqualNegativeOne(memberService.getTimes())) {
          remainItems.put(serviceDTOList.get(0).getName(), SmsConstant.VelocityMsgTemplateConstant.limitless);
          continue;
        }
        memberService.setTimes(memberService.getTimes().intValue() - 1);
        membersService.updateMemberService(memberService);
        remainItems.put(serviceDTOList.get(0).getName(), String.valueOf(memberService.getTimes()));
      }
      context.put(SmsConstant.VelocityMsgTemplateConstant.consumeItems, consumeItems);
      context.put(SmsConstant.VelocityMsgTemplateConstant.remainItems, remainItems);
      return context;
    } catch (Exception e) {
      LOG.error("/TxnService.java");
      LOG.error("method=updateMemberInfo");
      LOG.error("shopId:" + repairOrderDTO.getShopId());
      LOG.error(e.getMessage(), e);
      LOG.info(repairOrderDTO.toString());
      return null;
    }
  }

  @Override
  public WashBeautyOrderDTO getWashBeautyOrderDTOById(Long shopId, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    WashBeautyOrder washBeautyOrder = writer.getWashBeautyOrderDTOById(shopId, orderId);

    if (null == washBeautyOrder) {
      return null;
    }

    WashBeautyOrderDTO washBeautyOrderDTO = washBeautyOrder.toDTO();

    List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = this.getWashBeautyOrderItemDTOByOrderId(washBeautyOrder.getShopId(), washBeautyOrderDTO.getId());

    if (CollectionUtils.isNotEmpty(washBeautyOrderItemDTOs)) {
      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOArray = (WashBeautyOrderItemDTO[]) washBeautyOrderItemDTOs.toArray(new WashBeautyOrderItemDTO[0]);
      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOArray);
    }

    return washBeautyOrderDTO;
  }

  @Override
  public List<WashBeautyOrderItemDTO> getWashBeautyOrderItemDTOByOrderId(Long shopId, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<WashBeautyOrderItem> washBeautyOrderItems = writer.getWashBeautyOrderItemDTOByOrderId(shopId, orderId);

    if (CollectionUtils.isEmpty(washBeautyOrderItems)) {
      return null;
    }

    List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = new ArrayList<WashBeautyOrderItemDTO>();

    for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItems) {
      washBeautyOrderItemDTOs.add(washBeautyOrderItem.toDTO());
    }

    return washBeautyOrderItemDTOs;
  }

  @Override
  public List<ServiceDTO> getAllServiceDTOByShopId(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<Service> services = writer.getAllServiceDTOByShopId(shopId);

    if (CollectionUtils.isEmpty(services)) {
      return null;
    }

    List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();

    for (Service service : services) {
      serviceDTOs.add(service.toDTO());
    }

    return serviceDTOs;
  }

  public List<ServiceDTO> searchSuggestionForServices(Long shopId,String searchKey){
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.searchSuggestionForServices(shopId,searchKey);
    if(CollectionUtils.isEmpty(services)){
      return new ArrayList<ServiceDTO>();
    }
    List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();
    for(Service service : services) {
      serviceDTOs.add(service.toDTO());
    }
    return serviceDTOs;
  }
  //获取所有入库单 应付款表数据初始化
  public List<PurchaseInventoryDTO> getAllPurchaseInventory() throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventoryDTO> purchaseInventoryDTOList = new ArrayList<PurchaseInventoryDTO>();
    List<PurchaseInventory> purchaseInventories = writer.getPurchaseInventory();
    if (CollectionUtils.isEmpty(purchaseInventories)) {
      LOG.debug("库存没单据！");
      return null;
    }
    for (PurchaseInventory p : purchaseInventories) {
      PurchaseInventoryDTO purchaseInventoryDTO = p.toDTO();
      List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(p.getId());
      if (CollectionUtils.isEmpty(items)) {
        LOG.error("入库单据内没有此商品！！");
        continue;
      }
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[items.size()];
      if (itemDTOs == null || itemDTOs.length == 0) {
        LOG.error("入库单据内没有此商品！！");
        continue;
      }
      purchaseInventoryDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        PurchaseInventoryItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
        ProductDTO productDTO = ServiceManager.getService(IProductService.class)
          .getProductByProductLocalInfoId(item.getProductId(), p.getShopId());
        if (productDTO == null) {
          LOG.error("ProductLocalInfo内没有此商品！！");
          continue;
        }
        itemDTOs[i].setProductName(productDTO.getName());
      }
      purchaseInventoryDTOList.add(purchaseInventoryDTO);
    }
    return purchaseInventoryDTOList;
  }


  /**
   * 设定施工项目查询的下拉模糊查询
   * 获取服务名称
   *
   * @param shopId
   * @param serviceName
   * @return
   */
  @Override
  public List<ServiceDTO> getObscureServiceByName(Long shopId, String serviceName) {
    if (StringUtils.isBlank(serviceName)) {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    List<Service> serviceList = writer.getObscureServiceByName(shopId,serviceName);

    if(CollectionUtils.isEmpty(serviceList))
    {
      return null;
    }

    List<ServiceDTO> serviceDTOList = new ArrayList<ServiceDTO>();

    for(Service service : serviceList)
    {
      serviceDTOList.add(service.toDTO());
    }

    return serviceDTOList;
  }

  /**
   * 设定施工项目查询的下拉模糊查询
   * 获取施工类目
   */
  @Override
  public List<Category> getObscureCategoryByName(Long shopId, String categoryName) {
    if (StringUtils.isBlank(categoryName)) {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getObscureCategoryByName(shopId, categoryName);
  }

  /**
   * 根据shop_id获取一段时间内购卡续卡单据的条数 和总金额
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  @Override
  public List<String> getMemberOrderCountAndSum(long shopId, long startTime, long endTime,OrderSearchConditionDTO orderSearchConditionDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<String> stringList = writer.getMemberOrderCountAndSum(shopId, startTime, endTime,orderSearchConditionDTO);
    return stringList;
  }


  public List<MemberCardOrderDTO> getMemberOrderListByPagerTimeArrayType(long shopId, long startTime, long endTime, Pager pager, String arrayType,OrderSearchConditionDTO orderSearchConditionDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<MemberCardOrder> memberCardList = writer.getMemberOrderListByPagerTimeArrayType(shopId, startTime, endTime, pager, arrayType,orderSearchConditionDTO);
    if (CollectionUtils.isEmpty(memberCardList)) {
      return null;
    }
    List<MemberCardOrderDTO> memberCardOrderDTOList = new ArrayList<MemberCardOrderDTO>();
    for (MemberCardOrder memberCardOrder : memberCardList) {
      if (memberCardOrder == null) {
        continue;
      }
      memberCardOrderDTOList.add(memberCardOrder.toDTO());
    }
    return memberCardOrderDTOList;
  }

  /**
   * 根据汽修车饰单ID查询汽修车饰单服务项目表
   *
   * @param repairOrderId
   * @return
   */
  public List<RepairOrderService> getRepairOrderServicesByRepairOrderId(Long repairOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getRepairOrderServicesByRepairOrderId(repairOrderId);
  }


  /**
   * 根据shop_id 服务名称查找服务
   *
   * @param shopId
   * @param serviceName
   * @return
   */
  public List<Service> getServiceByShopIdAndName(Long shopId, String serviceName) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.getServiceByShopIdAndName(shopId, serviceName);
    //该店铺下 一个服务项目存在两条记录
    if (CollectionUtils.isNotEmpty(services) && services.size() > 1) {
      if(services.size() != 2 || services.get(1).getShopId() != 1) {    //如果有两条重复，且最后一条是shopId为1的，则不报错。
        LOG.error("/TxnService.java, method=getServiceByShopIdAndName");
        LOG.error("施工项目: {} 在service表中存在多条记录, shopId:{}", serviceName, shopId);
      }
    }
    return services;
  }

  public List<MemberCardOrderDTO> countMemberAgentAchievements(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<MemberCardOrder> memberCardList = writer.countMemberAgentAchievements(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(memberCardList)) {
      return null;
    }
    List<MemberCardOrderDTO> memberCardOrderDTOList = new ArrayList<MemberCardOrderDTO>();
    for (MemberCardOrder memberCardOrder : memberCardList) {
      if (memberCardOrder == null) {
        continue;
      }
      memberCardOrderDTOList.add(memberCardOrder.toDTO());
    }
    return memberCardOrderDTOList;
  }


  public List<WashBeautyOrderDTO> countWashBeautyAgentAchievements(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<WashBeautyOrder> washBeautyOrderList = writer.countWashBeautyAgentAchievements(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(washBeautyOrderList)) {
      return null;
    }
    List<WashBeautyOrderDTO> washBeautyOrderDTOList = new ArrayList<WashBeautyOrderDTO>();
    for (WashBeautyOrder washBeautyOrder : washBeautyOrderList) {
      if (washBeautyOrder == null) {
        continue;
      }
      washBeautyOrderDTOList.add(washBeautyOrder.toDTO());
    }
    return washBeautyOrderDTOList;
  }

  /**
   * 查询的时候去掉空格并且全部转为大写
   *
   * @param shopId
   * @param serviceName
   * @return
   */
  @Override
  public List<Service> getServiceByShopIdAndNameRemovalTrimAndUpper(Long shopId, String serviceName, Long serviceId) {
    if (StringUtils.isBlank(serviceName)) {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getServiceByShopIdAndNameRemovalTrimAndUpper(shopId, serviceName, serviceId);
  }

  /**
   * 获得对账单据号
   * @param shopId
   * @param statementAccountOrderId
   * @return
   */
  public  String getStatementAccountOrderNo(Long shopId, Long statementAccountOrderId) {
    if(statementAccountOrderId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getStatementAccountOrderNo(shopId, statementAccountOrderId);
  }

  /**
   * 根据订单类型和订单ID查询该单据历史结算记录
   * @param shopId
   * @param orderTypeEnum
   * @param orderId
   * @return
   * @throws Exception
   */
  @Override
  public List<ReceptionRecordDTO> getSettledRecord(Long shopId, OrderTypes orderTypeEnum, Long orderId){
    List<ReceptionRecordDTO> receptionRecordDTOs = new ArrayList<ReceptionRecordDTO>();
    if(orderTypeEnum == null || OrderTypes.RETURN.equals(orderTypeEnum) || OrderTypes.INVENTORY.equals(orderTypeEnum)) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReceptionRecord> receptionRecords = writer.getSettledRecord(shopId, orderTypeEnum, orderId);
    if(!CollectionUtil.isNotEmpty(receptionRecords)) {
      return null;
    }
    for(ReceptionRecord receptionRecord : receptionRecords) {
      if(OrderStatus.REPAIR_REPEAL.equals(receptionRecord.getOrderStatusEnum()) || OrderStatus.WASH_REPEAL.equals(receptionRecord.getOrderStatusEnum()) || OrderStatus.SALE_REPEAL.equals(receptionRecord.getOrderStatusEnum()) || OrderStatus.REPEAL.equals(receptionRecord.getOrderStatusEnum())) {
        continue;
      }
      ReceptionRecordDTO receptionRecordDTO = receptionRecord.toDTO();
      if(receptionRecordDTO.getMemberId() != null) {
        receptionRecordDTO.setMemberNo(ServiceManager.getService(IMembersService.class).getMemberById(receptionRecordDTO.getMemberId()).getMemberNo());
        receptionRecordDTO.setMemberDiscountRatio(ServiceManager.getService(IMembersService.class).getMemberById(receptionRecordDTO.getMemberId()).getMemberDiscount());
      }
      if(receptionRecordDTO.getToPayTime() != null) {
        receptionRecordDTO.setToPayTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT,receptionRecordDTO.getToPayTime()));
      }
      receptionRecordDTO.setReceptionDateStr(DateUtil.dateLongToStr(receptionRecordDTO.getReceptionDate()));
      receptionRecordDTOs.add(receptionRecordDTO);
    }
    return receptionRecordDTOs;
  }

  public int countRepairOrderByDate(Long shopId, Long startDate, Long endDate) {
    return txnDaoManager.getWriter().countRepairOrderByDate(shopId, startDate, endDate);
  }


  @Override
  public List<ReceptionRecordDTO> getReceptionByShopIdAndPager(long shopId,Pager pager) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReceptionRecord> receptionRecordList = writer.getReceptionByShopIdAndPager(shopId, pager);
    if (CollectionUtils.isEmpty(receptionRecordList)) {
      return null;
    }
    List<ReceptionRecordDTO> receptionRecordDTOList = new ArrayList<ReceptionRecordDTO>();

    for (ReceptionRecord receptionRecord : receptionRecordList) {
      if (receptionRecord == null) {
        continue;
      }
      ReceptionRecordDTO receptionRecordDTO = receptionRecord.toDTO();
      receptionRecordDTOList.add(receptionRecordDTO);
    }
    return receptionRecordDTOList;
  }

  @Override
  public void saveOrUpdateReceptionRecord(ReceptionRecordDTO receptionRecordDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    if (receptionRecordDTO == null) {
      return;
    }
    Object status = writer.begin();
    try {
      if (receptionRecordDTO.getId() != null) {
        ReceptionRecord receptionRecord = writer.getById(ReceptionRecord.class, receptionRecordDTO.getId());
        receptionRecord.fromDTO(receptionRecordDTO);
        writer.saveOrUpdate(receptionRecord);
      } else {
        ReceptionRecord receptionRecord = new ReceptionRecord();
        receptionRecord.fromDTO(receptionRecordDTO);
        writer.save(receptionRecord);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }




  @Override
  public WashBeautyOrderItemDTO getWashBeautyOrderItemByItemId(Long shopId, Long itemId) {
    WashBeautyOrderItemDTO washBeautyOrderItemDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    WashBeautyOrderItem washBeautyOrderItem = writer.getById(WashBeautyOrderItem.class, itemId);
    if (washBeautyOrderItem != null) {
      washBeautyOrderItemDTO = washBeautyOrderItem.toDTO();
      IUserService userService = ServiceManager.getService(IUserService.class);
      SalesManDTO salesManDTO = null;
      List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();
      if (StringUtils.isNotBlank(washBeautyOrderItem.getSalesManIds())) {
        for (String salesManId : washBeautyOrderItem.getSalesManIds().split(",")) {
          if (StringUtils.isBlank(salesManId)) continue;
          salesManDTO = userService.getSalesManDTOById(Long.valueOf(salesManId));
          if (salesManDTO != null) {
            salesManDTOList.add(salesManDTO);
          }
        }
      }
      if (CollectionUtils.isNotEmpty(salesManDTOList)) {
        washBeautyOrderItemDTO.setSalesManDTOList(salesManDTOList);
      }
    }
    return washBeautyOrderItemDTO;
  }

  @Override
  public PurchaseInventoryDTO getPurchaseInventoryById(long orderId, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseInventory purchaseInventory = writer.getById(PurchaseInventory.class, orderId);
    if (purchaseInventory != null) {
      return purchaseInventory.toDTO();
    }
    return null;
  }

  @Override
  public Service deleteService(Long shopId, Long serviceId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Service service = getServiceById(shopId, serviceId);
    if (null == service) {
      return null;
    }
    Object status = writer.begin();
    service.setStatus(ServiceStatus.DISABLED);
    try {
      writer.update(service);
      writer.commit(status);
      //做solr
      Set<Long> serviceIdSet = new HashSet<Long>();
      serviceIdSet.add(service.getId());
      try {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(shopId, serviceIdSet);
      } catch (Exception e) {
        LOG.error("shopId:{}", shopId);
        LOG.error("serviceId:{}", StringUtil.arrayToStr(",", serviceIdSet.toArray(new Long[serviceIdSet.size()])));
        LOG.error("createRepairServiceSolrIndex 失败！", e);
      }
      return service;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Service getServiceById(Long shopId,Long serviceId)
  {
    if(null == shopId || null == serviceId)
    {
      return null;
    }

    TxnWriter writer =txnDaoManager.getWriter();

    return writer.getServiceById(shopId,serviceId);
  }

  @Override
  public ServiceDTO saveOrUpdateService(Long shopId,String serviceName) throws Exception
  {
    if(StringUtils.isBlank(serviceName))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      Service service = writer.getRFServiceByServiceNameAndShopId(shopId,serviceName);

      if(null != service && ServiceStatus.DISABLED != service.getStatus())
      {
        return service.toDTO();
      }
      else if(null != service && ServiceStatus.DISABLED == service.getStatus())
      {
        service.setStatus(ServiceStatus.ENABLED);
        writer.update(service);
      }
      else if(null == service)
      {
        service = new Service();
        service.setName(serviceName);
        service.setShopId(shopId);
        service.setStatus(ServiceStatus.ENABLED);
        writer.save(service);
      }
      writer.commit(status);

      //做solr
      Set<Long> serviceIdSet = new HashSet<Long>();
      serviceIdSet.add(service.getId());
      try {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(shopId,serviceIdSet);
      } catch (Exception e) {
        LOG.error("shopId:{}", shopId);
        LOG.error("serviceId:{}", StringUtil.arrayToStr(",", serviceIdSet.toArray(new Long[serviceIdSet.size()])));
        LOG.error("createRepairServiceSolrIndex 失败！", e);
      }
      return service.toDTO();
    }finally {
      writer.rollback(status);
    }

  }

  @Override
  public Map<Long, ReceivableDTO> getReceivableDTOByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getReceivableDTOByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public Map<Long, PurchaseReturnDTO> getMapOfPurchaseReturnByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseReturnByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public Map<Long, MemberCardOrderDTO> getMapOfMemberCardOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getMemberCardOrderByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public Map<Long, SalesOrderDTO> getMapOfSalesOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSalesOrderByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public Map<Long, WashBeautyOrderDTO> getMapOfWashBeautyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getWashBeautyOrderByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public List<Long> getRepairOrderIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getRepairOrderIds(shopId, start, pageSize);
  }

  @Override
  public List<Long> getInventoryCheckOrderIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInventoryCheckOrderIds(shopId, start, pageSize);
  }

  @Override
  public List<Long> getAllocateRecordOrderIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getAllocateRecordOrderIds(shopId, start, pageSize);
  }

  @Override
  public List<Long> getInnerPickingOrderIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInnerPickingOrderIds(shopId, start, pageSize);
  }

  @Override
  public List<Long> getInnerReturnOrderIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInnerReturnOrderIds(shopId, start, pageSize);
  }

  @Override
  public List<Long> getBorrowOrderIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBorrowOrderIds(shopId, start, pageSize);
  }

  @Override
  public List<Long> getReturnBorrowOrderIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getReturnBorrowOrderIds(shopId, start, pageSize);
  }

  @Override
  public List<PurchaseInventoryDTO> getPurchaseInventoryByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseInventorysByShopIdAndOrderIds(shopId, orderIds);
  }

  @Override
  public List<PurchaseOrderDTO> getPurchaseOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseOrdersByShopIdAndOrderIds(shopId, orderIds);
  }

  @Override
  public List<PurchaseOrderDTO> getPurchaseOrdersWithItemAndProductByOrderIds(Long shopId, Long... ids) throws Exception{
    List<PurchaseOrderDTO> purchaseOrderDTOs = getPurchaseOrdersByShopIdAndOrderIds(shopId, ids);
    List<PurchaseOrderItemDTO> itemDTOs = getPurchaseOrderItemDTOs(ids);
    Set<Long> productHistoryIds = new HashSet<Long>();
    for(PurchaseOrderItemDTO itemDTO : itemDTOs){
      productHistoryIds.add(itemDTO.getProductHistoryId());
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class).getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    Map<Long, List<PurchaseOrderItemDTO>> purchaseOrderItemMap = new HashMap<Long, List<PurchaseOrderItemDTO>>();
    if (CollectionUtils.isNotEmpty(itemDTOs)) {
      for (PurchaseOrderItemDTO itemDTO : itemDTOs) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getProductHistoryId() != null) {
          itemDTO.setProductHistoryDTO(productHistoryDTOMap.get(itemDTO.getProductHistoryId()));
        }
        if (itemDTO.getPurchaseOrderId() != null) {
          List<PurchaseOrderItemDTO> itemDTOList = purchaseOrderItemMap.get(itemDTO.getPurchaseOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            purchaseOrderItemMap.put(itemDTO.getPurchaseOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<PurchaseOrderItemDTO>();
            itemDTOList.add(itemDTO);
            purchaseOrderItemMap.put(itemDTO.getPurchaseOrderId(), itemDTOList);
          }
        }
      }
    }

    for (PurchaseOrderDTO purchaseOrderDTO : purchaseOrderDTOs) {
      if (purchaseOrderDTO == null) {
        continue;
      }
      List<PurchaseOrderItemDTO> itemDTOList = purchaseOrderItemMap.get(purchaseOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        purchaseOrderDTO.setItemDTOs(itemDTOList.toArray(new PurchaseOrderItemDTO[itemDTOList.size()]));
      }
    }
    return purchaseOrderDTOs;
  }

  @Override
  public List<PurchaseReturnDTO> getPurchaseReturnByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseReturnByShopIdAndOrderIds(shopId, orderIds);
  }
  @Override
  public List<SalesReturnDTO> getSalesReturnByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSalesReturnByShopIdAndOrderIds(shopId, orderIds);
  }
  @Override
  public List<SalesOrderDTO> getSalesOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSalesOrdersByShopIdAndOrderIds(shopId, orderIds);
  }

  @Override
  public List<PreBuyOrderDTO> getPreBuyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrder> preBuyOrderList = writer.getPreBuyOrdersByShopIdAndOrderIds(shopId, orderIds);
    if (CollectionUtils.isEmpty(preBuyOrderList)) return new ArrayList<PreBuyOrderDTO>();
    List<PreBuyOrderDTO> preBuyOrderDTOList = new ArrayList<PreBuyOrderDTO>();
    for (PreBuyOrder preBuyOrder : preBuyOrderList) {
      if (preBuyOrder == null) continue;
      preBuyOrderDTOList.add(preBuyOrder.toDTO());
    }

    return preBuyOrderDTOList;
  }

  @Override
  public List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrder> quotedPreBuyOrderList = writer.getQuotedPreBuyOrdersByShopIdAndOrderIds(shopId, orderIds);
    if (CollectionUtils.isEmpty(quotedPreBuyOrderList)) return new ArrayList<QuotedPreBuyOrderDTO>();
    List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOList = new ArrayList<QuotedPreBuyOrderDTO>();
    for (QuotedPreBuyOrder quotedPreBuyOrder : quotedPreBuyOrderList) {
      if (quotedPreBuyOrder == null) continue;
      quotedPreBuyOrderDTOList.add(quotedPreBuyOrder.toDTO());
    }

    return quotedPreBuyOrderDTOList;
  }

  @Override
  public List<InventoryCheckDTO> getInventoryCheckDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventoryCheckDTO> inventoryCheckDTOList = new ArrayList<InventoryCheckDTO>();
    List<InventoryCheck> inventoryCheckList = writer.getInventoryChecksByShopIdAndOrderIds(shopId, orderIds);
    if(CollectionUtils.isNotEmpty(inventoryCheckList)){
      for(InventoryCheck inventoryCheck : inventoryCheckList){
        inventoryCheckDTOList.add(inventoryCheck.toDTO());
      }
    }
    return inventoryCheckDTOList;
  }

  @Override
  public List<AllocateRecordDTO> getAllocateRecordDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<AllocateRecordDTO> allocateRecordDTOList = new ArrayList<AllocateRecordDTO>();
    List<AllocateRecord> allocateRecordList = writer.getAllocateRecordsByShopIdAndOrderIds(shopId, orderIds);
    if(CollectionUtils.isNotEmpty(allocateRecordList)){
      for(AllocateRecord allocateRecord : allocateRecordList){
        allocateRecordDTOList.add(allocateRecord.toDTO());
      }
    }
    return allocateRecordDTOList;
  }

  @Override
  public List<InnerPickingDTO> getInnerPickingDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<InnerPickingDTO> innerPickingDTOList = new ArrayList<InnerPickingDTO>();
    List<InnerPicking> innerPickingList = writer.getInnerPickingsByShopIdAndOrderIds(shopId, orderIds);
    if(CollectionUtils.isNotEmpty(innerPickingList)){
      for(InnerPicking innerPicking : innerPickingList){
        innerPickingDTOList.add(innerPicking.toDTO());
      }
    }
    return innerPickingDTOList;
  }

  @Override
  public List<InnerReturnDTO> getInnerReturnDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<InnerReturnDTO> innerReturnDTOList = new ArrayList<InnerReturnDTO>();
    List<InnerReturn> innerReturnList = writer.getInnerReturnsByShopIdAndOrderIds(shopId, orderIds);
    if(CollectionUtils.isNotEmpty(innerReturnList)){
      for(InnerReturn innerReturn : innerReturnList){
        innerReturnDTOList.add(innerReturn.toDTO());
      }
    }
    return innerReturnDTOList;
  }

  @Override
  public List<BorrowOrderDTO> getBorrowOrderDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BorrowOrderDTO> borrowOrderDTOList = new ArrayList<BorrowOrderDTO>();
    List<BorrowOrder> borrowOrderList = writer.getBorrowOrdersByShopIdAndOrderIds(shopId, orderIds);
    if(CollectionUtils.isNotEmpty(borrowOrderList)){
      for(BorrowOrder borrowOrder : borrowOrderList){
        borrowOrderDTOList.add(borrowOrder.toDTO());
      }
    }
    return borrowOrderDTOList;

  }

  @Override
  public List<ReturnOrderDTO> getReturnBorrowOrderDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReturnOrderDTO> returnOrderDTOList = new ArrayList<ReturnOrderDTO>();
    List<ReturnOrder> returnOrderList = writer.getReturnBorrowOrdersByShopIdAndOrderIds(shopId, orderIds);
    if(CollectionUtils.isNotEmpty(returnOrderList)){
      for(ReturnOrder returnOrder : returnOrderList){
        returnOrderDTOList.add(returnOrder.toDTO());
      }
    }
    return returnOrderDTOList;
  }

  @Override
  public List<RepairOrderDTO> getRepairOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getRepairOrdersByShopIdAndOrderIds(shopId, orderIds);
  }

  @Override
  public Map<Long, RepairOrderDTO> getRepairOrderMapByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Map<Long, RepairOrderDTO> repairOrderDTOMap = new HashMap<Long, RepairOrderDTO>();
    if (shopId == null || orderIds == null || ArrayUtil.isEmpty(orderIds)) {
      return repairOrderDTOMap;
    }
    List<RepairOrderDTO> repairOrderDTOList = txnDaoManager.getWriter().getRepairOrdersByShopIdAndOrderIds(shopId, orderIds);
    if (CollectionUtils.isNotEmpty(repairOrderDTOList)) {
      for (RepairOrderDTO orderDTO : repairOrderDTOList) {
        repairOrderDTOMap.put(orderDTO.getId(), orderDTO);
      }
    }
    return repairOrderDTOMap;
  }

  @Override
  public Map<Long, Map<Long, WashBeautyOrderItemDTO>> getWashBeautyOrderItemByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getWashBeautyOrderItemByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public List<WashBeautyOrderDTO> getWashBeautyOrdersDetailByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getWashBeautyOrderDetailByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public List<MemberCardOrderDTO> getMemberCardOrdersDetailByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getMemberCardOrderDetailByShopIdAndArrayOrderId(shopId, orderIds);
  }

  @Override
  public List<MemberCardReturnDTO> getMemberReturnCardOrdersDetailByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<MemberCardReturnDTO> memberCardReturnDTOList = new ArrayList<MemberCardReturnDTO>();
    Set<Long> customerIds = new HashSet<Long>();
    Set<Long> executorIds = new HashSet<Long>();
    Set<Long> salesManIds = new HashSet<Long>();
    Set<Long> serviceIds = new HashSet<Long>();
    MemberCardReturnDTO memberCardReturnDTO;
    TxnWriter writer = txnDaoManager.getWriter();
    List<MemberCardReturn> memberCardReturnList = writer.getMemberCardReturnOrdersByIds(orderIds);
    List<MemberCardReturnItem> memberCardReturnItemList = writer.getMemberCardReturnItemsByIds(orderIds);
    List<MemberCardReturnService> memberCardReturnServiceList = writer.getMemberCardReturnServicesByIds(orderIds);
    List<ReceptionRecord> receptionRecordList = writer.getReceptionRecordsByIds(orderIds);
    Map<Long, ReceivableDTO> receivableDTOMap = this.getReceivableDTOByShopIdAndArrayOrderId(shopId, orderIds);
    //item
    Map<Long, List<MemberCardReturnItemDTO>> memberCardReturnItemMap= new HashMap<Long, List<MemberCardReturnItemDTO>>();
    List<MemberCardReturnItemDTO> memberCardReturnItemDTOList;
    for (MemberCardReturnItem item : memberCardReturnItemList) {
      if (item.getSalesId() != null) salesManIds.add(item.getSalesId());
      memberCardReturnItemDTOList = memberCardReturnItemMap.get(item.getMemberCardReturnId());
      if (CollectionUtils.isEmpty(memberCardReturnItemDTOList)) {
        memberCardReturnItemDTOList = new ArrayList<MemberCardReturnItemDTO>();
      }
      memberCardReturnItemDTOList.add(item.toDTO());
      memberCardReturnItemMap.put(item.getMemberCardReturnId(), memberCardReturnItemDTOList);
    }
    //service
    Map<Long, List<MemberCardReturnServiceDTO>> memberCardReturnServiceMap = new HashMap<Long, List<MemberCardReturnServiceDTO>>();
    List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOList;
    for (MemberCardReturnService service : memberCardReturnServiceList) {
      if(service.getServiceId()!=null)serviceIds.add(service.getServiceId());
    }
    Map<Long, ServiceDTO> serviceMap = new HashMap<Long, ServiceDTO>();
    if (CollectionUtils.isNotEmpty(serviceIds)) serviceMap = this.getServiceByServiceIdSet(shopId, serviceIds);
    for (MemberCardReturnService service : memberCardReturnServiceList) {
      memberCardReturnServiceDTOList = memberCardReturnServiceMap.get(service.getMemberCardReturnId());
      if (CollectionUtils.isEmpty(memberCardReturnServiceDTOList)) {
        memberCardReturnServiceDTOList = new ArrayList<MemberCardReturnServiceDTO>();
      }
      MemberCardReturnServiceDTO memberCardReturnServiceDTO = service.toDTO();
      //Service name
      ServiceDTO serviceDTO = serviceMap.get(memberCardReturnServiceDTO.getServiceId());
      if (serviceDTO != null) memberCardReturnServiceDTO.setServiceName(serviceDTO.getName());
      memberCardReturnServiceDTOList.add(memberCardReturnServiceDTO);
      memberCardReturnServiceMap.put(service.getMemberCardReturnId(), memberCardReturnServiceDTOList);
    }
    //ReceptionRecord
    Map<Long, ReceptionRecordDTO> receptionRecordMap = new HashMap<Long,ReceptionRecordDTO>();
    for (ReceptionRecord receptionRecord : receptionRecordList) {
      receptionRecordMap.put(receptionRecord.getOrderId(), receptionRecord.toDTO());
    }
    //销售人
    Map<Long, SalesManDTO> saleManMap = new HashMap<Long, SalesManDTO>();
    if (CollectionUtils.isNotEmpty(salesManIds)) {
      saleManMap = userService.getSalesManByIdSet(shopId, salesManIds);
    }
    for (MemberCardReturn memberCardReturn : memberCardReturnList) {
      memberCardReturnDTO = memberCardReturn.toDTO();
      if (memberCardReturn.getCustomerId() != null) customerIds.add(memberCardReturn.getCustomerId());
      if (memberCardReturn.getExecutorId() != null) executorIds.add(memberCardReturn.getExecutorId());
      List<MemberCardReturnItemDTO> itemDTOs = memberCardReturnItemMap.get(memberCardReturn.getId());
      if (CollectionUtils.isNotEmpty(itemDTOs)) {
        for (MemberCardReturnItemDTO itemDTO : itemDTOs) {
          SalesManDTO salesManDTO = saleManMap.get(itemDTO.getSalesId());
          if (salesManDTO != null) {
            itemDTO.setSalesMan(salesManDTO.getName());
          }
        }
      }
      memberCardReturnDTO.setMemberCardReturnItemDTOs(itemDTOs);
      memberCardReturnDTO.setMemberCardReturnServiceDTOs(memberCardReturnServiceMap.get(memberCardReturn.getId()));
      memberCardReturnDTO.setReceptionRecordDTO(receptionRecordMap.get(memberCardReturn.getId()));
      memberCardReturnDTOList.add(memberCardReturnDTO);
    }
    Map<Long, MemberCardDTO> memberCardDTOMap = new HashMap<Long, MemberCardDTO>();
    Map<Long, CustomerDTO> customerDTOMap = new HashMap<Long, CustomerDTO>();
    Map<Long, UserDTO> executorMap = new HashMap<Long, UserDTO>();

    Map<Long, MemberDTO> memberDTOMap = new HashMap<Long, MemberDTO>();
    if (CollectionUtils.isNotEmpty(customerIds)) {
      customerDTOMap = customerService.getCustomerByIdSet(shopId, customerIds);
      memberDTOMap = ServiceManager.getService(IMembersService.class).getMemberByCustomerIdSet(shopId, customerIds);
    }
    if (CollectionUtils.isNotEmpty(executorIds)) {
      executorMap = customerService.getExecutorByIdSet(shopId, executorIds);
    }
    for (MemberCardReturnDTO dto : memberCardReturnDTOList) {
      dto.setReceivableDTO(receivableDTOMap.get(dto.getId()));
      CustomerDTO customerDTO = customerDTOMap.get(dto.getCustomerId());
      MemberDTO memberDTO= memberDTOMap.get(dto.getCustomerId());
      if(memberDTO!=null)dto.setMemberCardType(memberDTO.getType());
      if (customerDTO != null) dto.setCustomerName(customerDTO.getName());
    }
    return memberCardReturnDTOList;
  }


  public  MemberDTO getMemberInfo(Long shopId,Long customerId) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId,customerId);
    if (memberDTO== null||MemberStatus.DISABLED.equals(memberDTO.getStatus())) {
      return null;
    }
    memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
    memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
    List<MemberServiceDTO> memberServiceDTOs = memberDTO.getMemberServiceDTOs();
    Set<Long> serviceIdSet = new HashSet<Long>();
    Map<Long, ServiceDTO> serviceDTOMap = new HashMap<Long, ServiceDTO>();
    if (CollectionUtil.isNotEmpty(memberServiceDTOs)) {
      for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
        serviceIdSet.add(memberServiceDTO.getServiceId());
      }
    }
    if (CollectionUtil.isNotEmpty(serviceIdSet)) {
      serviceDTOMap = getServiceByServiceIdSet(shopId, serviceIdSet);
    }
    if (CollectionUtil.isNotEmpty(memberServiceDTOs)){
      for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
        ServiceDTO serviceDTO = serviceDTOMap.get(memberServiceDTO.getServiceId());
        if (serviceDTO != null) {
          memberServiceDTO.setServiceName(serviceDTO.getName());
        }
      }
    }
    return memberDTO;
  }

  public  MemberDTO getMemberInfo(Long memberId) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Member member= membersService.getMemberById(memberId);
    if(member==null) return null;
    MemberDTO memberDTO = membersService.getMemberByCustomerId(member.getShopId(),member.getCustomerId());
    if (memberDTO== null||MemberStatus.DISABLED.equals(memberDTO.getStatus())) {
      return null;
    }
    memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
    memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
    List<MemberServiceDTO> memberServiceDTOs = memberDTO.getMemberServiceDTOs();
    Set<Long> serviceIdSet = new HashSet<Long>();
    Map<Long, ServiceDTO> serviceDTOMap = new HashMap<Long, ServiceDTO>();
    if (CollectionUtil.isNotEmpty(memberServiceDTOs)) {
      for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
        serviceIdSet.add(memberServiceDTO.getServiceId());
      }
    }
    if (CollectionUtil.isNotEmpty(serviceIdSet)) {
      serviceDTOMap = getServiceByServiceIdSet(member.getShopId(), serviceIdSet);
    }
    if (CollectionUtil.isNotEmpty(memberServiceDTOs)){
      for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
        ServiceDTO serviceDTO = serviceDTOMap.get(memberServiceDTO.getServiceId());
        if (serviceDTO != null) {
          memberServiceDTO.setServiceName(serviceDTO.getName());
        }
      }
    }
    return memberDTO;
  }


  @Override
  public List<Long> getPurchaseInventoryIds(Long shopId, int start, int size) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return  writer.getPurchaseInventoryIds(shopId, start, size);
  }

  @Override
  public List<PurchaseInventoryItemDTO> getPurchaseInventoryItemByOrderIds(Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventoryItem> purchaseInventoryItems = writer.getPurchaseInventoryItemByOrderIds(orderIds);
    List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOs = new ArrayList<PurchaseInventoryItemDTO>();
    if (CollectionUtils.isNotEmpty(purchaseInventoryItems)) {
      for (PurchaseInventoryItem item : purchaseInventoryItems) {
        if (item == null) {
          continue;
        }
        purchaseInventoryItemDTOs.add(item.toDTO());
      }
    }
    return purchaseInventoryItemDTOs;
  }

  @Override
  public Map<Long, ServiceDTO> getServiceByServiceIdSet(Long shopId, Set<Long> serviceIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getServiceByServiceIdSet(shopId, serviceIds);
  }

  @Override
  public List<Long> getPurchaseReturnIds(Long shopId, int start, int size) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseReturnIds(shopId, start, size);
  }

  @Override
  public List<Long> getSalesReturnIds(Long shopId, int start, int size) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSalesReturnIds(shopId, start, size);
  }

  @Override
  public List<PurchaseReturnItemDTO> getPurchaseReturnItemDTOs(Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseReturnItem> purchaseReturnItems = writer.getPurchaseReturnItemByOrderIds(orderIds);
    List<PurchaseReturnItemDTO> purchaseReturnItemDTOs = new ArrayList<PurchaseReturnItemDTO>();
    if (CollectionUtils.isNotEmpty(purchaseReturnItems)) {
      for (PurchaseReturnItem item : purchaseReturnItems) {
        if (item == null) {
          continue;
        }
        purchaseReturnItemDTOs.add(item.toDTO());
      }
    }
    return purchaseReturnItemDTOs;
  }
  @Override
  public List<SalesReturnItemDTO> getSalesReturnItemDTOs(Long shopId,Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesReturnItem> salesReturnItems = writer.getSalesReturnItemByOrderIds(orderIds);
    List<SalesReturnItemDTO> salesReturnItemDTOList = new ArrayList<SalesReturnItemDTO>();
    if (CollectionUtils.isNotEmpty(salesReturnItems)) {
      for (SalesReturnItem item : salesReturnItems) {
        if (item == null) {
          continue;
        }
        salesReturnItemDTOList.add(item.toDTO());
      }
    }
    return salesReturnItemDTOList;
  }
  @Override
  public List<Long> getPurchaseOrderIds(Long shopId, int start, int size) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseOrders(shopId, start, size);
  }

  @Override
  public List<PurchaseOrderItemDTO> getPurchaseOrderItemDTOs(Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseOrderItem> purchaseOrderItems = writer.getPurchaseOrderItems(orderIds);
    List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = new ArrayList<PurchaseOrderItemDTO>();
    if (CollectionUtils.isNotEmpty(purchaseOrderItems)) {
      for (PurchaseOrderItem item : purchaseOrderItems) {
        if (item == null) {
          continue;
        }
        purchaseOrderItemDTOList.add(item.toDTO());
      }
    }
    return purchaseOrderItemDTOList;
  }

  @Override
  public List<Long> getSalesOrderDTOs(Long shopId, int start, int size) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSalesOrderIds(shopId, start, size);
  }

  @Override
  public List<Long> getPreBuyOrderIds(Long shopId, int start, int size) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPreBuyOrderIds(shopId, start, size);
  }

  @Override
  public List<Long> getQuotedPreBuyOrderIds(Long shopId, int start, int size) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getQuotedPreBuyOrderIds(shopId, start, size);
  }


  @Override
  public List<SalesOrderItemDTO> getSalesOrderItemDTOs(Long shopId,Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesOrderItem> salesOrderItems = writer.getSalesOrderItems(orderIds);
    List<SalesOrderItemDTO> salesOrderItemDTOList = new ArrayList<SalesOrderItemDTO>();
    if (CollectionUtils.isNotEmpty(salesOrderItems)) {
      for (SalesOrderItem item : salesOrderItems) {
        if (item == null) {
          continue;
        }
        salesOrderItemDTOList.add(item.toDTO());
      }
    }
    return salesOrderItemDTOList;
  }

  @Override
  public List<PreBuyOrderItemDTO> getPreBuyOrderItemDTOs(Long shopId, Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrderItem> preBuyOrderItemList = writer.getPreBuyOrderItems(orderIds);
    List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
    if (CollectionUtils.isNotEmpty(preBuyOrderItemList)) {
      for (PreBuyOrderItem item : preBuyOrderItemList) {
        if (item == null) {
          continue;
        }
        preBuyOrderItemDTOList.add(item.toDTO());
      }
    }
    return preBuyOrderItemDTOList;
  }

  @Override
  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOs(Long shopId, Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrderItem> quotedPreBuyOrderItemList = writer.getQuotedPreBuyOrderItems(orderIds);
    List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = new ArrayList<QuotedPreBuyOrderItemDTO>();
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemList)) {
      for (QuotedPreBuyOrderItem item : quotedPreBuyOrderItemList) {
        if (item == null) {
          continue;
        }
        quotedPreBuyOrderItemDTOList.add(item.toDTO());
      }
    }
    return quotedPreBuyOrderItemDTOList;
  }

  @Override
  public Map<Long, List<QuotedPreBuyOrderItemDTO>> getQuotedPreBuyOrderItemDTOMap(Long shopId, Long... preBuyOrderIds) {
    if (shopId == null) {
      throw new RuntimeException("getQuotedPreBuyOrderItemDTOMap,shopId is null.");
    }
    Map<Long, List<QuotedPreBuyOrderItemDTO>> result = new HashMap<Long, List<QuotedPreBuyOrderItemDTO>>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrderItem> quotedPreBuyOrderItemList = writer.getQuotedPreBuyOrderItems(preBuyOrderIds);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemList)) {
      for (QuotedPreBuyOrderItem item : quotedPreBuyOrderItemList) {
        if (item == null) {
          continue;
        }
        List<QuotedPreBuyOrderItemDTO> itemDTOs = result.get(item.getPreBuyOrderId());
        if (CollectionUtils.isNotEmpty(itemDTOs)) {
          itemDTOs.add(item.toDTO());
        } else {
          List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOs = new ArrayList<QuotedPreBuyOrderItemDTO>();
          quotedPreBuyOrderItemDTOs.add(item.toDTO());
          result.put(item.getPreBuyOrderId(), quotedPreBuyOrderItemDTOs);
        }
      }
    }
    return result;
  }

  @Override
  public List<InventoryCheckItemDTO> getInventoryCheckItemDTOs(Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventoryCheckItem> inventoryCheckItemList = writer.getInventoryCheckItems(orderIds);
    List<InventoryCheckItemDTO> inventoryCheckItemDTOList = new ArrayList<InventoryCheckItemDTO>();
    if (CollectionUtils.isNotEmpty(inventoryCheckItemList)) {
      for (InventoryCheckItem item : inventoryCheckItemList) {
        if (item == null) {
          continue;
        }
        inventoryCheckItemDTOList.add(item.toDTO());
      }
    }
    return inventoryCheckItemDTOList;
  }

  @Override
  public List<AllocateRecordItemDTO> getAllocateRecordItemDTOs(Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<AllocateRecordItem> allocateRecordItemList = writer.getAllocateRecordItems(orderIds);
    List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
    if (CollectionUtils.isNotEmpty(allocateRecordItemList)) {
      for (AllocateRecordItem allocateRecordItem : allocateRecordItemList) {
        if (allocateRecordItem == null) {
          continue;
        }
        allocateRecordItemDTOList.add(allocateRecordItem.toDTO());
      }
    }
    return allocateRecordItemDTOList;
  }


  @Override
  public List<RepairOrderItemDTO> getRepairOrderItemDTOsByShopIdAndArrayOrderId(Long shopId, Long... orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderItem> repairOrderItemList = writer.getRepairOrderItemByShopIdAndArrayOrderId(shopId, orderId);
    List<RepairOrderItemDTO> repairOrderItemDTOList = new ArrayList<RepairOrderItemDTO>();
    if(CollectionUtils.isNotEmpty(repairOrderItemList)){
      for(RepairOrderItem repairOrderItem : repairOrderItemList){
        repairOrderItemDTOList.add(repairOrderItem.toDTO());
      }
    }
    return repairOrderItemDTOList;
  }

  @Override
  public Map<Long, List<RepairOrderServiceDTO>> getRepairOrderServiceDTOByShopIdAndArrayOrderId(Long shopId, Long... orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderService> repairOrderServiceList = writer.getRepairOrderServicesByShopIdAndArrayOrderId(shopId, orderId);

    Map<Long, List<RepairOrderServiceDTO>> repairOrderServiceDTOListMap = new HashMap<Long, List<RepairOrderServiceDTO>>();
    if (CollectionUtils.isNotEmpty(repairOrderServiceList)) {
      Set<Long> serviceHistoryIds = new HashSet<Long>();
      for (RepairOrderService repairOrderService : repairOrderServiceList) {
        serviceHistoryIds.add(repairOrderService.getServiceHistoryId());
      }
      //默认的2个service的shopId 为1  所以不能传shopId
      Map<Long, ServiceHistoryDTO> serviceHistoryMap = getServiceHistoryService().getServiceHistoryByServiceHistoryIdSet(null, serviceHistoryIds);
      List<RepairOrderServiceDTO> repairOrderServiceDTOList = null;
      ServiceHistoryDTO serviceHistory = null;
      RepairOrderServiceDTO repairOrderServiceDTO = null;
      for (RepairOrderService repairOrderService : repairOrderServiceList) {
        serviceHistory = serviceHistoryMap.get(repairOrderService.getServiceHistoryId());
        repairOrderServiceDTO = repairOrderService.toDTO();
        if (serviceHistory != null) {
          repairOrderServiceDTO.setService(serviceHistory.getName());
        }
        if (repairOrderServiceDTOListMap.get(repairOrderService.getRepairOrderId()) == null) {
          repairOrderServiceDTOList = new ArrayList<RepairOrderServiceDTO>();
          repairOrderServiceDTOList.add(repairOrderServiceDTO);
          repairOrderServiceDTOListMap.put(repairOrderService.getRepairOrderId(), repairOrderServiceDTOList);
        } else {
          repairOrderServiceDTOListMap.get(repairOrderService.getRepairOrderId()).add(repairOrderServiceDTO);
        }
      }
    }
    return repairOrderServiceDTOListMap;
  }



  @Override
  public void saveInventory(InventoryDTO inventoryDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Inventory inventory = new Inventory();
      inventory.fromDTO(inventoryDTO);
      writer.save(inventory);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  private void addInventoryForSpecialShopVersion(SalesOrderDTO salesOrderDTO, Long shopId, Long shopVersionId ,TxnWriter writer) throws Exception {
    if (shopVersionId == null || shopId == null || !BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId) || salesOrderDTO == null) {
      return;
    }
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    SalesOrderItemDTO[] itemDTOs = salesOrderDTO.getItemDTOs();
    if (itemDTOs == null) {
      return;
    }
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    for (SalesOrderItemDTO itemDTO : itemDTOs) {
      if (itemDTO == null || StringUtils.isBlank(itemDTO.getProductName())) {
        continue;
      }
      boolean isNewProduct = false;
      double supplierInventoryAmountChange = 0d;
      String supplierInventoryUnit = null;
      if (itemDTO.getProductId() == null) {
        ProductDTO productDTO = new ProductDTO(shopId, itemDTO);
        productDTO.setBusinessCategoryId(itemDTO.getBusinessCategoryId());
        isNewProduct = getProductService().saveNewProduct(productDTO);
        Long productLocalInfoId = productDTO.getProductLocalInfoId();
        if (null != productLocalInfoId && isNewProduct) {               //新商品
          Inventory inventory = new Inventory();
          inventory.setId(productLocalInfoId);
          inventory.setShopId(shopId);
          inventory.setAmount(itemDTO.getAmount());
          inventory.setNoOrderInventory(itemDTO.getAmount());
          inventory.setUnit(itemDTO.getUnit());
          writer.save(inventory);
          supplierInventoryAmountChange = NumberUtil.doubleVal(itemDTO.getAmount());
          supplierInventoryUnit = itemDTO.getUnit();
          InventorySearchIndexDTO inventorySearchIndexDTO = productDTO.toInventorySearchIndexDTO();
          inventorySearchIndexDTO.setUnit(itemDTO.getUnit());
          inventorySearchIndexDTO.setAmount(itemDTO.getAmount());
          InventorySearchIndex inventorySearchIndex = new InventorySearchIndex(inventorySearchIndexDTO);
          List<InventorySearchIndex> list = new ArrayList<InventorySearchIndex>();
          list.add(inventorySearchIndex);
          searchService.addOrUpdateInventorySearchIndexWithList(list);
          itemDTO.setProductId(productLocalInfoId);
        } else if (null != productLocalInfoId && !isNewProduct) {        //没填productId的老商品
          itemDTO.setProductId(productLocalInfoId);
        } else {
          LOG.error("addInventoryForSpecialShopVersion保存新商品失败：{}！", salesOrderDTO);
        }
      }

      if (!isNewProduct) {
        ProductLocalInfoDTO productLocalInfoDTO = getProductService().getProductLocalInfoById(itemDTO.getProductId(), shopId);
        Inventory inventory = writer.getById(Inventory.class,itemDTO.getProductId());
        double amount = itemDTO.getAmount();
        double inventoryAmount = inventory.getAmount();
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount =  amount * productLocalInfoDTO.getRate();
        }
        if (amount > inventoryAmount - 0.0001) {
          double noOrderInventoryAmount = amount - inventoryAmount;
          supplierInventoryAmountChange = noOrderInventoryAmount;
          supplierInventoryUnit = inventory.getUnit();
          inventory.setAmount(amount);
          inventory.setNoOrderInventory((inventory.getNoOrderInventory() == null ? 0D : inventory.getNoOrderInventory())
            + amount - inventoryAmount);
          writer.update(inventory);
//					InventorySearchIndexDTO inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId, itemDTO.getProductId());
//					inventorySearchIndexDTO.setAmount(inventory.getAmount());
//					searchService.updateInventorySearchIndex(inventorySearchIndexDTO);

        }
      }
      if (supplierInventoryAmountChange > 0.0001) {
        SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
        supplierInventoryDTO.setShopId(shopId);
        supplierInventoryDTO.setProductId(itemDTO.getProductId());
        supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        supplierInventoryDTO.setChangeAmount(supplierInventoryAmountChange);
        supplierInventoryDTO.setUnit(supplierInventoryUnit);
        supplierInventoryDTOs.add(supplierInventoryDTO);
      }
    }
    if(CollectionUtils.isNotEmpty(supplierInventoryDTOs)){
      productThroughService.saveOrUpdateSupplierInventory(writer,supplierInventoryDTOs);
    }
  }

  /**
   * 如果应该存在的inventorySearchIndex记录为空（脏数据），新建。
   */
  @Override
  public void checkAndInsertInventorySearchIndex(Long productLocalInfoId, Long shopId) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    InventorySearchIndexDTO inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId, productLocalInfoId);
    if(inventorySearchIndexDTO != null){
      return;
    }
    LOG.warn("productLocalInfoId为 {} 的inventorySearchIndex不存在，将新建。", productLocalInfoId);
    Inventory inventory = txnDaoManager.getWriter().getInventoryByIdAndshopId(productLocalInfoId, shopId);
    if(inventory == null){
      LOG.error("inventory 记录不存在, productLocalInfoId:{}", productLocalInfoId);
      inventory = new Inventory();
    }
    ProductDTO productDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(productLocalInfoId, shopId);
    InventorySearchIndex inventorySearchIndex = this.createInventorySearchIndex(inventory , productDTO.getId());
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    inventorySearchIndexList.add(inventorySearchIndex);
    searchService.addOrUpdateInventorySearchIndexWithList(inventorySearchIndexList);
  }

  @Override
  public void checkAndInsertInventorySearchIndexes(Long shopId, Long... productLocalInfoIds) throws Exception {
    if (productLocalInfoIds == null || productLocalInfoIds.length == 0) {
      return;
    }
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<Long> productLocalInfoIdList = new ArrayList<Long>(Arrays.asList(productLocalInfoIds));
    List<InventorySearchIndexDTO> inventorySearchIndexDTOs = searchService.getInventorySearchIndexDTOsByProductIds(shopId, productLocalInfoIds);
    List<Long> existList = new ArrayList<Long>();
    if (CollectionUtils.isNotEmpty(inventorySearchIndexDTOs)) {
      for (InventorySearchIndexDTO inventorySearchIndexDTO : inventorySearchIndexDTOs) {
        existList.add(inventorySearchIndexDTO.getId());
      }
    }
    if (CollectionUtils.isNotEmpty(existList)) {
      productLocalInfoIdList.removeAll(existList);
    }
    if (CollectionUtils.isEmpty(productLocalInfoIdList)) {
      return;
    }
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    LOG.warn("productLocalInfoId为 {} 的inventorySearchIndex不存在，将新建。", productLocalInfoIdList);
    for (Long productLocalInfoId : productLocalInfoIdList) {
      Inventory inventory = txnDaoManager.getWriter().getInventoryByIdAndshopId(productLocalInfoId, shopId);
      if (inventory == null) {
        LOG.error("inventory 记录不存在, productLocalInfoId:{}", productLocalInfoId);
        inventory = new Inventory();
      }
      ProductDTO productDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(productLocalInfoId, shopId);
      InventorySearchIndex inventorySearchIndex = this.createInventorySearchIndex(inventory, productDTO.getId());
      inventorySearchIndexList.add(inventorySearchIndex);
    }
    searchService.addOrUpdateInventorySearchIndexWithList(inventorySearchIndexList);
  }

  @Override
  public List<ServiceDTO> getServiceByShopId(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Service> services = writer.getServiceByShopId(shopId);
    List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();
    if (services != null) {
      for (Service service : services) {
        ServiceDTO serviceDTO = service.toDTO();
        serviceDTOs.add(serviceDTO);
      }
      return serviceDTOs;
    }
    return null;
  }
  /**
   * 根据单据种类获取自增单据号，time不传则为当天
   * @param shopId
   * @param types
   * @param time
   * @return
   */
  @Override
  public String getReceiptNo(Long shopId,OrderTypes types,Long time) {
    if (null == time) {
      time = System.currentTimeMillis();
    }

    String timeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, time);

    String shortTimeStr = timeStr.substring(2, 4) + timeStr.substring(5, 7) + timeStr.substring(8, 10);

    String receiptNoStr = "";
    String key = "";
    ConcurrentScene scene = null;
    OrderReceiptNoPrefix prefix = null;
    if (OrderTypes.SALE == types) {
      prefix = OrderReceiptNoPrefix.SALE;
      scene = ConcurrentScene.SALE;
      key = "createSaleReceiptNo_";
    } else if (OrderTypes.PURCHASE == types) {
      prefix = OrderReceiptNoPrefix.PURCHASE;
      scene = ConcurrentScene.PURCHASE;
      key = "createPurchaseReceiptNo_";
    } else if (OrderTypes.INVENTORY == types) {
      prefix = OrderReceiptNoPrefix.INVENTORY;
      scene = ConcurrentScene.INVENTORY;
      key = "createInventoryReceiptNo_";
    } else if (OrderTypes.REPAIR == types) {
      prefix = OrderReceiptNoPrefix.REPAIR;
      scene = ConcurrentScene.REPAIR;
      key = "createRepairReceiptNo_";
    } else if (OrderTypes.WASH_BEAUTY == types) {
      prefix = OrderReceiptNoPrefix.WASH_BEAUTY;
      scene = ConcurrentScene.WASH_BEAUTY;
      key = "createWashBeautyReceiptNo_";
    } else if (OrderTypes.RETURN == types) {
      prefix = OrderReceiptNoPrefix.PURCHASE_RETURN;
      scene = ConcurrentScene.PURCHASE_RETURN;
      key = "createPurchaseReturnReceiptNo_";
    }else if (OrderTypes.SALE_RETURN == types) {
      prefix = OrderReceiptNoPrefix.SALE_RETURN;
      scene = ConcurrentScene.SALE_RETURN;
      key = "createSaleReturnReceiptNo_";
    }else if (OrderTypes.REPAIR_PICKING == types) {
      prefix = OrderReceiptNoPrefix.REPAIR_PICKING;
      scene = ConcurrentScene.REPAIR_PICKING;
      key = "createRepairPickingReceiptNo_";
    }else if (OrderTypes.ALLOCATE_RECORD == types) {
      prefix = OrderReceiptNoPrefix.ALLOCATE_RECORD;
      scene = ConcurrentScene.ALLOCATE_RECORD;
      key = "createAllocateRecordReceiptNo_";
    }else if (OrderTypes.INNER_PICKING == types) {
      prefix = OrderReceiptNoPrefix.INNER_PICKING;
      scene = ConcurrentScene.INNER_PICKING;
      key = "createInnerPickingReceiptNo_";
    }else if (OrderTypes.INNER_RETURN == types) {
      prefix = OrderReceiptNoPrefix.INNER_RETURN;
      scene = ConcurrentScene.INNER_RETURN;
      key = "createInnerReturnReceiptNo_";
    }else if (OrderTypes.CUSTOMER_STATEMENT_ACCOUNT == types) {
      prefix = OrderReceiptNoPrefix.CUSTOMER_STATEMENT_ACCOUNT;
      scene = ConcurrentScene.CUSTOMER_STATEMENT_ACCOUNT;
      key = "createCustomerStatementAccountReceiptNo_";
    }else if (OrderTypes.SUPPLIER_STATEMENT_ACCOUNT == types) {
      prefix = OrderReceiptNoPrefix.SUPPLIER_STATEMENT_ACCOUNT;
      scene = ConcurrentScene.SUPPLIER_STATEMENT_ACCOUNT;
      key = "createSupplierStatementAccountReceiptNo_";
    }else if (OrderTypes.INVENTORY_CHECK == types) {
      prefix = OrderReceiptNoPrefix.INVENTORY_CHECK;
      scene = ConcurrentScene.INVENTORY_CHECK;
      key = "createInventoryCheckReceiptNo_";
    }else if (OrderTypes.BORROW_ORDER == types) {
      prefix = OrderReceiptNoPrefix.BORROW_ORDER;
      scene = ConcurrentScene.BORROW_ORDER;
      key = "createBorrowOrderReceiptNo_";
    } else if (OrderTypes.APPOINT_ORDER == types) {
      prefix = OrderReceiptNoPrefix.APPOINT_ORDER;
      scene = ConcurrentScene.APPOINT_ORDER;
      key = "createAppointOrderReceiptNo_";
    } else if (OrderTypes.ENQUIRY == types) {
      prefix = OrderReceiptNoPrefix.ENQUIRY;
      scene = ConcurrentScene.ENQUIRY;
      key = "enquiry_";
    }



    key += shopId.toString();

    if (!BcgogoConcurrentController.lock(scene, key)) {
      return null;
    }

    ReceiptNo receiptNoObj = getReceiptNOByShopIdAndType(shopId, types);

    if (null == receiptNoObj) {
      receiptNoStr = prefix.getPrefix() + shortTimeStr + "-001";
      receiptNoObj = new ReceiptNo();
      receiptNoObj.setReceiptNo(receiptNoStr);
      receiptNoObj.setTypes(types);
      receiptNoObj.setShopId(shopId);
      //保存
    } else {
      if ((prefix.getPrefix() + shortTimeStr).equals(receiptNoObj.getReceiptNo().substring(0, 8))) {
        int num = Integer.valueOf(receiptNoObj.getReceiptNo().split("-")[1]);

        num += 1;

        String numStr = "";
        if (num < 10) {
          numStr = "00" + String.valueOf(num);
        } else if (num >= 10 && num <= 99) {
          numStr = "0" + String.valueOf(num);
        } else {
          numStr = String.valueOf(num);
        }

        receiptNoObj.setReceiptNo(receiptNoObj.getReceiptNo().split("-")[0] + "-" + numStr);
        receiptNoStr = receiptNoObj.getReceiptNo();
      } else {
        receiptNoObj.setReceiptNo(prefix.getPrefix() + shortTimeStr + "-001");
        receiptNoStr = receiptNoObj.getReceiptNo();
      }
    }

    saveOrUpdateReceiptNo(receiptNoObj);
    BcgogoConcurrentController.release(scene, key);

    return receiptNoStr;
  }

  @Override
  public String getBcgogoOrderReceiptNo(Long shopId,OrderTypes type,Long time) {
    if (null == time) {
      time = System.currentTimeMillis();
    }

    String timeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY3, time);
    String receiptNoStr = "";
    String key = "";
    ConcurrentScene scene = null;
    OrderReceiptNoPrefix prefix = null;
    if (OrderTypes.BCGOGO_SOFTWARE_RECEIVABLE_ORDER == type) {
      prefix = OrderReceiptNoPrefix.BCGOGO_SOFTWARE_RECEIVABLE_ORDER;
      scene = ConcurrentScene.BCGOGO_SOFTWARE_RECEIVABLE_ORDER;
      key = "createBcgogoSoftwareReceivableOrderReceiptno_";
    } else if (OrderTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER == type) {
      prefix = OrderReceiptNoPrefix.BCGOGO_HARDWARE_RECEIVABLE_ORDER;
      scene = ConcurrentScene.BCGOGO_HARDWARE_RECEIVABLE_ORDER;
      key = "createBcgogoHardwarEReceivableOrderReceiptno_";
    } else if (OrderTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER == type) {
      prefix = OrderReceiptNoPrefix.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER;
      scene = ConcurrentScene.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER;
      key = "createBcgogoSmsReceivableOrderReceiptno_";
    }



    key += shopId.toString();

    if (!BcgogoConcurrentController.lock(scene, key)) {
      return null;
    }

    ReceiptNo receiptNoObj = getReceiptNOByShopIdAndType(shopId, type);
    String receiptNoPrefix = prefix.getPrefix() + timeStr;
    if (receiptNoObj == null) {
      receiptNoStr = receiptNoPrefix + "0001";
      receiptNoObj = new ReceiptNo();
      receiptNoObj.setReceiptNo(receiptNoStr);
      receiptNoObj.setTypes(type);
      receiptNoObj.setShopId(shopId);
      //保存
    } else {
      if(receiptNoObj.getReceiptNo().indexOf(receiptNoPrefix)>-1){
        int num = Integer.valueOf(receiptNoObj.getReceiptNo().replace(receiptNoPrefix,""));
        num++;
        receiptNoObj.setReceiptNo(receiptNoPrefix+String.format("%04d",num));
        receiptNoStr = receiptNoObj.getReceiptNo();
      }else{
        receiptNoObj.setReceiptNo(prefix.getPrefix() + timeStr + "0001");
        receiptNoStr = receiptNoObj.getReceiptNo();
      }
    }
    saveOrUpdateReceiptNo(receiptNoObj);
    BcgogoConcurrentController.release(scene, key);

    return receiptNoStr;
  }
  /**
   * 获取当天最后使用的单据号 ,
   * 现在不需要用了。
   * @param shopId
   * @param receiptNoNotNo 没有序号的单据号前缀
   * @return
   */
  @Override
  public String getLastOrderReceiptNo(Long shopId,OrderTypes types,String receiptNoNotNo)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getLastOrderReceiptNo(shopId,types,receiptNoNotNo);
  }

  /**
   * 每次获取没单据号的num条数据，初始化专用
   * @return
   */
  @Override
  public List getOrderDTONoReceiptNo(OrderTypes types,int num,int pageNo)
  {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getOrderDTONoReceiptNo(types,num,pageNo);
  }

  /**
   * 批量初始化更新单据号
   * @param OrderList
   */
  @Override
  public void updateOrderListReceiptNo(List OrderList,OrderTypes types)
  {

    if(CollectionUtils.isEmpty(OrderList))
    {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    String receiptNo = "";
    try{
      if(OrderTypes.SALE == types)
      {
        for(SalesOrder salesOrder : (List<SalesOrder>)OrderList)
        {
          writer.update(salesOrder);
        }
      }
      else if(OrderTypes.PURCHASE ==types)
      {
        for(PurchaseOrder purchaseOrder : (List<PurchaseOrder>)OrderList)
        {
          writer.update(purchaseOrder);
        }
      }
      else if(OrderTypes.INVENTORY == types)
      {
        for(PurchaseInventory purchaseInventory : (List<PurchaseInventory>)OrderList)
        {
          writer.update(purchaseInventory);
        }
      }
      else if(OrderTypes.REPAIR == types)
      {
        for(RepairOrder repairOrder : (List<RepairOrder>)OrderList)
        {
          writer.update(repairOrder);
        }
      }
      else if(OrderTypes.WASH_BEAUTY == types)
      {
        for(WashBeautyOrder washBeautyOrder : (List<WashBeautyOrder>)OrderList)
        {
          writer.update(washBeautyOrder);
        }
      }
      else if(OrderTypes.RETURN == types)
      {
        for(PurchaseReturn purchaseReturn : (List<PurchaseReturn>)OrderList)
        {
          writer.update(purchaseReturn);
        }
      }

      writer.commit(status);

    }finally {
      writer.rollback(status);
    }

  }

  @Override
  public int countOrderNoReceiptNo(Long shopId,OrderTypes types)
  {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countOrderNoReceiptNo(shopId,types);
  }

  @Override
  public ReceiptNo getReceiptNOByShopIdAndType(Long shopId,OrderTypes types)
  {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getReceiptNOByShopIdAndType(shopId,types);
  }



  public  String getOrderReceiptNO(ReceiptNoDTO receptNoDTO){
    OrderTypes types=receptNoDTO.getTypes();
    TxnWriter writer = txnDaoManager.getWriter();
    if(OrderTypes.SALE == types){
      SalesOrder salesOrder= writer.getById(SalesOrder.class,receptNoDTO.getOrderId());
      if(null!=salesOrder){
        return salesOrder.getReceiptNo();
      }
    }else if(OrderTypes.PURCHASE ==types) {
      PurchaseOrder purchaseOrder= writer.getById(PurchaseOrder.class,receptNoDTO.getOrderId());
      if(null!=purchaseOrder){
        return purchaseOrder.getReceiptNo();
      }
    }else if(OrderTypes.INVENTORY == types){
      PurchaseInventory purchaseInventory= writer.getById(PurchaseInventory.class,receptNoDTO.getOrderId());
      if(null!=purchaseInventory){
        return purchaseInventory.getReceiptNo();
      }
    }else if(OrderTypes.REPAIR == types){
      RepairOrder repairOrder= writer.getById(RepairOrder.class,receptNoDTO.getOrderId());
      if(null!=repairOrder){
        return repairOrder.getReceiptNo();
      }
    }else if(OrderTypes.WASH_BEAUTY == types){
      WashBeautyOrder washBeautyOrder= writer.getById(WashBeautyOrder.class,receptNoDTO.getOrderId());
      if(null!=washBeautyOrder){
        return washBeautyOrder.getReceiptNo();
      }
    }else if(OrderTypes.RETURN == types){
      PurchaseReturn purchaseReturn= writer.getById(PurchaseReturn.class,receptNoDTO.getOrderId());
      if(null!=purchaseReturn){
        return purchaseReturn.getReceiptNo();
      }
    }
    return null;
  }

  /**
   * 参数要么是新增的，要么是调用函数之前已经从数据库中拿到的对象，不能是从DTO转的带有id的对象
   * @param receiptNo
   * @return
   */
  @Override
  public ReceiptNo saveOrUpdateReceiptNo(ReceiptNo receiptNo)
  {
    if(null == receiptNo)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status= writer.begin();

    try{
      if(null == receiptNo.getId())
      {
        writer.save(receiptNo);
      }
      else
      {
        writer.update(receiptNo);
      }

      writer.commit(status);
      return receiptNo;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveBusinessStatChange(BusinessStatDTO businessStatDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BusinessStatChange businessStatChange = null;
      if (businessStatDTO.getId() != null) {
        businessStatChange = writer.getById(BusinessStatChange.class, businessStatDTO.getId());
      } else {
        businessStatChange = new BusinessStatChange();
      }
      businessStatChange.fromDTO(businessStatDTO);
      writer.saveOrUpdate(businessStatChange);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public BusinessStatDTO getBusinessStatChangeOfDay(Long shopId, long year, long month, long day) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStatChange> businessStatChangeList = writer.getBusinessStatChangeOfDay(shopId, year, month, day);
    if (CollectionUtils.isEmpty(businessStatChangeList)) return null;
    if (businessStatChangeList.size() > 1) {
      LOG.error("businessStatChange has duplicate date of the same day.");
    }
    return businessStatChangeList.get(0).toDTO();
  }

  @Override
  public BusinessStatDTO sumBusinessStatChangeForMonth(Long shopId, long year, long month) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.sumBusinessStatChangeOfStatSumForMonth(shopId, year, month);
  }

  @Override
  public BusinessStatDTO sumBusinessStatChangeForYear(Long shopId, long year) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.sumBusinessStatChangeOfStatSumForYear(shopId, year);
  }

  @Override
  public Map<Long, BusinessStatDTO> getDayBusinessStatChangeMap(Long shopId, long year, long month) {
    Map<Long, BusinessStatDTO> map = new HashMap<Long, BusinessStatDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStatChange> businessStats = writer.getDayBusinessStatChange(shopId, year, month);
    if (CollectionUtils.isEmpty(businessStats)) return null;
    for (BusinessStatChange businessStatChange : businessStats) {
      map.put(businessStatChange.getStatDay(), businessStatChange.toDTO());
    }
    return map;
  }

  @Override
  public Map<Long, BusinessStatDTO> getMonthBusinessStatChangeMap(Long shopId, long year) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getMonthBusinessStatChangeMap(shopId, year);
  }
  @Override
  public Map<String,BusinessStatDTO> getBusinessStatMapByYearMonth(Long shopId,String... yearMonth){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBusinessStatMapByYearMonth(shopId,yearMonth);
  }
  @Override
  public Map<String,BusinessStatDTO> getBusinessStatMapByYearMonthDay(Long shopId,String yearMonthDayStart,String yearMonthDayEnd){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBusinessStatMapByYearMonthDay(shopId,yearMonthDayStart,yearMonthDayEnd);
  }
  @Override
  public Map<String,BusinessStatDTO> getBusinessStatChangeMapByYearMonth(Long shopId,Long[] year, Long[] month){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBusinessStatChangeMapByYearMonth(shopId, year, month);
  }
  @Override
  public Map<String,BusinessStatDTO> getBusinessStatChangeMapByYearMonthDay(Long shopId,Long[] year, Long[] month, Long[] day){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBusinessStatChangeMapByYearMonthDay(shopId, year, month, day);
  }
  public List<PurchaseInventoryDTO> getInventoryOrderListByPager(long shopId, long startTime, long endTime,
                                                                 Pager pager) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<PurchaseInventory> purchaseInventoryList = writer.getInventoryOrderListByPager(shopId, startTime, endTime, pager);

    if (CollectionUtils.isEmpty(purchaseInventoryList)) {
      return null;
    }
    List<PurchaseInventoryDTO> purchaseInventoryDTOList = new ArrayList<PurchaseInventoryDTO>();

    for (PurchaseInventory purchaseInventory : purchaseInventoryList) {
      if (purchaseInventory == null) {
        continue;
      }
      PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();

      List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(purchaseInventory.getId());
      if (CollectionUtils.isEmpty(items)) {
        LOG.error("Txnservice.java");
        LOG.error("shopId:" + purchaseInventory.getShopId() + "purchaseInventoryId:" + purchaseInventory.getId());
        LOG.error("入库单据内没有商品！！");
        continue;
      }
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[items.size()];
      if (itemDTOs == null || itemDTOs.length == 0) {
        LOG.error("Txnservice.java");
        LOG.error("shopId:" + purchaseInventory.getShopId() + "purchaseInventoryId:" + purchaseInventory.getId());
        LOG.error("入库单据内没有商品！！");
        continue;
      }
      purchaseInventoryDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        PurchaseInventoryItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
        ProductDTO productDTO = ServiceManager.getService(IProductService.class)
          .getProductByProductLocalInfoId(item.getProductId(), purchaseInventory.getShopId());
        if (productDTO == null) {
          LOG.error("Txnservice.java");
          LOG.error("shopId:" + purchaseInventory.getShopId() + "purchaseInventoryId:" + purchaseInventory.getId());
          LOG.error("ProductLocalInfo内没有此商品！！" + item.getProductId());
          continue;
        }
        itemDTOs[i].setProductName(productDTO.getName());
      }


      purchaseInventoryDTOList.add(purchaseInventoryDTO);
    }
    return purchaseInventoryDTOList;
  }

  @Override
  public List<RepealOrder> getRepealOrderListByRepealDate(long shopId,long startTime,long endTime,Pager pager) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getRepealOrderListByRepealDate(shopId, startTime, endTime, pager);
  }


  public int countRepealOrderByRepealDate(long shopId,long startTime,long endTime) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countRepealOrderByRepealDate(shopId, startTime, endTime);
  }


  @Override
  public RunningStatDTO saveRunningStat(RunningStatDTO runningStatDTO) {
    if (runningStatDTO == null) return null;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Sort sort = new Sort(" stat_date ","desc" );
      RunningStat runningStat = null;
      if (runningStatDTO.getShopId() != null && runningStatDTO.getStatYear() != null
        && runningStatDTO.getStatMonth() != null && runningStatDTO.getStatDay() != null) {
        List<RunningStat> runningStatList = writer.getRunningStatByYearMonthDay(runningStatDTO.getShopId(), runningStatDTO.getStatYear().intValue(), runningStatDTO.getStatMonth().intValue(), runningStatDTO.getStatDay().intValue(), 1,sort);

        if (CollectionUtils.isEmpty(runningStatList)) {
          runningStat = new RunningStat();
          runningStat = runningStat.fromDTO(runningStatDTO, false);
          writer.save(runningStat);
        } else {
          runningStat = runningStatList.get(0);
          runningStat = runningStat.fromDTO(runningStatDTO, false);
          writer.update(runningStat);
        }
      }

      writer.commit(status);

      return runningStatDTO;
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public int countPurchaseInventoryOrderByCreated(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countPurchaseInventoryOrderByCreated(shopId, startTime, endTime);
  }

  @Override
  public int countPurchaseReturnOrderByCreated(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countPurchaseReturnOrderByCreated(shopId, startTime, endTime);
  }

  public List<PurchaseReturnDTO> getPurchaseReturnOrderListByPager(long shopId, long startTime, long endTime,
                                                                   Pager pager) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<PurchaseReturn> purchaseReturnList = writer.getPurchaseReturnOrderListByPager(shopId, startTime, endTime, pager);

    if (CollectionUtils.isEmpty(purchaseReturnList)) {
      return null;
    }
    List<PurchaseReturnDTO> purchaseReturnDTOList = new ArrayList<PurchaseReturnDTO>();

    for (PurchaseReturn purchaseReturn : purchaseReturnList) {
      if (purchaseReturn == null) {
        continue;
      }
      PurchaseReturnDTO purchaseReturnDTO = purchaseReturn.toDTO();
      purchaseReturnDTOList.add(purchaseReturnDTO);
    }
    return purchaseReturnDTOList;
  }

  @Override
  public List<RunningStatDTO> getRunningStatByYearMonthDay(long shopId,Integer year,Integer month,Integer day,Integer resultSize,Sort sort) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RunningStat> runningStatList = writer.getRunningStatByYearMonthDay(shopId, year, month, day, resultSize,sort);

    if (CollectionUtils.isEmpty(runningStatList)) {
      return null;
    }
    List<RunningStatDTO> runningStatDTOList = new ArrayList<RunningStatDTO>();

    for (RunningStat runningStat : runningStatList) {
      if (runningStat == null) {
        continue;
      }
      RunningStatDTO runningStatDTO = runningStat.toDTO();
      runningStatDTOList.add(runningStatDTO);
    }
    return runningStatDTOList;
  }

  @Override
  public List<RunningStatDTO> getRunningStatMonth(long shopId, long year, String queryString) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RunningStat> runningStatList = writer.getRunningStatMonth(shopId, year, queryString);

    if (CollectionUtils.isEmpty(runningStatList)) {
      return null;
    }

    List<RunningStatDTO> runningStatDTOList = new ArrayList<RunningStatDTO>();
    for (RunningStat runningStat : runningStatList) {
      RunningStatDTO runningStatDTO = runningStat.toDTO();
      runningStatDTOList.add(runningStatDTO);
    }
    return runningStatDTOList;

  }

  @Override
  public List<String> countReceptionRecordByReceptionDate(long shopId,long startTime,long endTime) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countReceptionRecordByReceptionDate(shopId,startTime,endTime);
  }

  @Override
  public List<ReceptionRecordDTO> getReceptionRecordByReceptionDate(long shopId,long startTime,long endTime,Pager pager) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReceptionRecord> receptionRecordList = writer.getReceptionRecordByReceptionDate(shopId, startTime, endTime,pager);
    if (CollectionUtils.isEmpty(receptionRecordList)) {
      return null;
    }

    List<ReceptionRecordDTO> receptionRecordDTOList = new ArrayList<ReceptionRecordDTO>();

    for (ReceptionRecord receptionRecord : receptionRecordList) {
      ReceptionRecordDTO receptionRecordDTO = receptionRecord.toDTO();
      receptionRecordDTOList.add(receptionRecordDTO);
    }
    return receptionRecordDTOList;
  }

  public List<ReceptionRecordDTO> getReceptionRecordByOrderId(long shopId, long orderId,OrderTypes orderTypes) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReceptionRecord> receptionRecordList = writer.getReceptionRecordByOrderId(shopId, orderId,orderTypes);
    if (CollectionUtils.isEmpty(receptionRecordList)) {
      return null;
    }

    List<ReceptionRecordDTO> receptionRecordDTOList = new ArrayList<ReceptionRecordDTO>();

    for (ReceptionRecord receptionRecord : receptionRecordList) {
      ReceptionRecordDTO receptionRecordDTO = receptionRecord.toDTO();
      receptionRecordDTOList.add(receptionRecordDTO);
    }
    return receptionRecordDTOList;
  }


  @Override
  public int countPayHistoryRecordByPayTime(long shopId, long startTime, long endTime) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countPayHistoryRecordByPayTime(shopId, startTime, endTime);
  }


  @Override
  public List<PayableHistoryRecordDTO> getPayHistoryRecordByPayTime(long shopId,long startTime,long endTime,Pager pager) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PayableHistoryRecord> payableHistoryRecordList = writer.getPayHistoryRecordByPayTime(shopId, startTime, endTime, pager);
    if (CollectionUtils.isEmpty(payableHistoryRecordList)) {
      return null;
    }

    List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = new ArrayList<PayableHistoryRecordDTO>();

    for (PayableHistoryRecord payableHistoryRecord : payableHistoryRecordList) {
      PayableHistoryRecordDTO payableHistoryRecordDTO = payableHistoryRecord.toDTO();
      payableHistoryRecordDTOList.add(payableHistoryRecordDTO);
    }
    return payableHistoryRecordDTOList;
  }

  /**
   * 根据收款单ID查询收款单记录
   *
   * @param receivableId
   * @return
   */
  public List<ReceptionRecord> getReceptionRecordsByReceivalbeId(Long receivableId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getReceptionRecordsByReceivalbeId(receivableId);
  }


  @Override
  public void saveBusinessStat(Long shopId,Long year,List<BusinessStatDTO> businessStatDTOList) {

    if (CollectionUtils.isEmpty(businessStatDTOList)) {
      return;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    List<BusinessStatChange> businessStatChangeList = writer.getBusinessStatChangeByYear(shopId, year);

    Object status = writer.begin();
    try {
      if (CollectionUtils.isNotEmpty(businessStatChangeList)) {
        for (BusinessStatChange businessStatChange : businessStatChangeList) {
          businessStatChange.clearBusinessStat();
          writer.update(businessStatChange);
        }
      }

      for (int index = 0; index < businessStatDTOList.size(); index++) {
        BusinessStatDTO businessStatDTO = businessStatDTOList.get(index);
        if (businessStatDTO == null) {
          continue;
        }
        if (index == 0) {
          BusinessStat businessStat = new BusinessStat(businessStatDTO);
          if (businessStatDTO.getShopId() != null && businessStatDTO.getStatYear() != null
            && businessStatDTO.getStatMonth() != null && businessStatDTO.getStatDay() != null) {
            writer.deleteBusinessStatByYearMonthDay(businessStatDTO.getShopId(), businessStatDTO.getStatYear(), businessStatDTO.getStatMonth(), businessStatDTO.getStatDay());
          }
          writer.save(businessStat);
        } else {
          BusinessStatDTO yesterdayDTO = businessStatDTOList.get(index - 1);
          businessStatDTO.setMemberIncome(businessStatDTO.getMemberIncome() + yesterdayDTO.getMemberIncome());
          businessStatDTO.setWash(businessStatDTO.getWash() + yesterdayDTO.getWash());
          businessStatDTO.setProductCost(businessStatDTO.getProductCost() + yesterdayDTO.getProductCost());
          businessStatDTO.setOrderOtherIncomeCost(businessStatDTO.getOrderOtherIncomeCost() + yesterdayDTO.getOrderOtherIncomeCost());
          businessStatDTO.setSales(businessStatDTO.getSales() + yesterdayDTO.getSales());
          businessStatDTO.setService(businessStatDTO.getService() + yesterdayDTO.getService());
          businessStatDTO.setStatSum(businessStatDTO.getWash() + businessStatDTO.getSales() + businessStatDTO.getMemberIncome() + businessStatDTO.getService());

          businessStatDTO.setOtherIncome(businessStatDTO.getOtherIncome() + yesterdayDTO.getOtherIncome());
          businessStatDTO.setRentExpenditure(businessStatDTO.getRentExpenditure() + yesterdayDTO.getRentExpenditure());
          businessStatDTO.setSalaryExpenditure(businessStatDTO.getSalaryExpenditure() + yesterdayDTO.getSalaryExpenditure());
          businessStatDTO.setUtilitiesExpenditure(businessStatDTO.getUtilitiesExpenditure() + yesterdayDTO.getUtilitiesExpenditure());
          businessStatDTO.setOtherExpenditure(businessStatDTO.getOtherExpenditure() + yesterdayDTO.getOtherExpenditure());

          BusinessStat businessStat = new BusinessStat(businessStatDTO);
          if (businessStatDTO.getShopId() != null && businessStatDTO.getStatYear() != null
            && businessStatDTO.getStatMonth() != null && businessStatDTO.getStatDay() != null) {
            writer.deleteBusinessStatByYearMonthDay(businessStatDTO.getShopId(), businessStatDTO.getStatYear(), businessStatDTO.getStatMonth(), businessStatDTO.getStatDay());
          }
          writer.save(businessStat);
        }
      }

      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

  /**
   *保存营业分类
   */
  public void saveCategoryFromDTO(CategoryDTO categoryDTO) {
    if (categoryDTO == null) {
      return;
    }
    Category category = new Category(categoryDTO);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(category);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  /**
   * sum营业外收入和支出的各字段的和（从startTime to endTime）
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  @Override
  public BizStatPrintDTO getBusinessChangeInfoToPrint(Long shopId,Long startTime,Long endTime)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    if(null == shopId || null == startTime || null == endTime)
    {
      return null;
    }

    return writer.getBusinessChangeInfoToPrint(shopId, startTime, endTime);
  }

  public Double getTotalPayable(RecOrPayIndexDTO recOrPayIndexDTO){
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return  txnWriter.getTotalPayable(recOrPayIndexDTO);
  }


  /**
   *
   * @param repairOrderId
   * @param startMileage
   */
  public boolean updateStartMileage(Long repairOrderId,double startMileage) {
    if (repairOrderId == null || startMileage <= 0) {
      return false;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();

    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, repairOrderId);
    if (repairOrder == null) {
      return false;
    }
    repairOrder.setStartMileage(startMileage);

    Object status = txnWriter.begin();
    try {
      txnWriter.update(repairOrder);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    return true;
  }

  @Override
  public MemberCardOrderDTO getLatestMemberCardOrder(Long shopId, Long customerId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    MemberCardOrder memberCardOrder = txnWriter.getLatestMemberCardOrder(shopId, customerId);
    if(memberCardOrder == null)
      return null;
    return memberCardOrder.toDTO();
  }

  /**
   * 查出没有结算的,没有作废的施工单的数量
   * @param shopId
   * @param customerId
   * @return
   */
  public int countRepairOrderOfNotSettled(Long shopId,Long customerId)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countRepairOrderOfNotSettled(shopId,customerId);
  }

  /**
   *  获取没有结算的， 没有作废的施工单的单据号
   * @param shopId
   * @param customerId
   * @return
   */
  public List<RepairOrderDTO> getRepairOrderReceiptNoOfNotSettled(Long shopId,Long customerId)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    List<RepairOrder> repairOrderList = writer.getRepairOrderReceiptNoOfNotSettled(shopId,customerId);

    if(CollectionUtils.isEmpty(repairOrderList))
    {
      return null;
    }

    List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
    for(RepairOrder ro:repairOrderList)
    {
      repairOrderDTOList.add(ro.toDTO());
    }

    return repairOrderDTOList;
  }

  /**
   * 从excel中导入会员服务
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public ImportResult importMemberServiceFromExcel(ImportContext importContext) throws Exception {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.parseData(importContext);

    //2.校验数据
    CheckResult checkResult = memberServiceImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = memberServiceImporter.importData(importContext);

    return importResult;

  }

  @Override
  public boolean batchCreateMemberServiceAndService(List<MemberServiceDTO> memberServiceDTOList)  throws BcgogoException
  {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    try {
      MemberService memberService = null;

      for (MemberServiceDTO memberServiceDTO : memberServiceDTOList) {
        if (memberServiceDTO == null || memberServiceDTO.getServiceDTO() == null) {
          continue;
        }
        if(StringUtils.isBlank(memberServiceDTO.getServiceDTO().getName()))
        {
          continue;
        }
        if(StringUtils.isBlank(memberServiceDTO.getMemberNo()))
        {
          continue;
        }

        Member member = membersService.getMemberByShopIdAndMemberNo(memberServiceDTO.getServiceDTO().getShopId(),memberServiceDTO.getMemberNo());

        if(null == member)
        {
          LOG.error("member表中没有此会员{}",memberServiceDTO.getMemberNo());
          continue;
        }

        ServiceDTO serviceDTO = null;
        try{
          serviceDTO = saveOrUpdateService(memberServiceDTO.getServiceDTO().getShopId(),memberServiceDTO.getServiceDTO().getName());
        }catch (Exception e)
        {
          LOG.error("保存更新服务失败");
          LOG.error("serviceDTO",memberServiceDTO.getServiceDTO());
        }

        if(null== serviceDTO || null == serviceDTO.getId())
        {
          continue;
        }

        memberServiceDTO.setServiceId(serviceDTO.getId());

        memberServiceDTO.setMemberId(member.getId());

        if(null == memberServiceDTO.getDeadline())
        {
          memberServiceDTO.setDeadline(Long.parseLong("-1"));
        }

        try{
          membersService.saveMemberService(memberServiceDTO);
        }catch (Exception e)
        {
          LOG.error("保存会员服务失败");
          LOG.error("memberService",memberService);
          LOG.error(e.getMessage(),e);
        }

        if(null == memberService || null == memberService.getId())
        {
          continue;
        }
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      throw new BcgogoException(e);
    }

    return true;
  }

  @Override
  public MemberCardReturnDTO saveMemberCardReturn(MemberCardReturnDTO memberCardReturnDTO) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Long shopId = memberCardReturnDTO.getShopId();
    Object status = writer.begin();
    try{
      MemberCardReturn memberCardReturn = new MemberCardReturn(memberCardReturnDTO);
      writer.save(memberCardReturn);
      memberCardReturnDTO.setId(memberCardReturn.getId());

      List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOs = memberCardReturnDTO.getMemberCardReturnServiceDTOs();
      if (CollectionUtils.isNotEmpty(memberCardReturnServiceDTOs)) {
        for (MemberCardReturnServiceDTO memberCardReturnServiceDTO : memberCardReturnServiceDTOs) {
          if (null == memberCardReturnServiceDTO.getServiceId()) {
            continue;
          }
          memberCardReturnServiceDTO.setMemberCardReturnId(memberCardReturn.getId());
          memberCardReturnServiceDTO.setShopId(memberCardReturn.getShopId());
          Service service = getServiceById(shopId, memberCardReturnServiceDTO.getServiceId());
          memberCardReturnServiceDTO.setServiceName(service==null?"":service.getName());
          ServiceHistory serviceHistory = serviceHistoryService.getOrSaveServiceHistoryByServiceId(service.getId(), memberCardReturnDTO.getShopId());
          memberCardReturnServiceDTO.setServiceHistoryId(serviceHistory.getId());
          MemberCardReturnService memberCardReturnService = new MemberCardReturnService(memberCardReturnServiceDTO);
          writer.save(memberCardReturnService);
          memberCardReturnServiceDTO.setId(memberCardReturnService.getId());
        }
      }

      List<MemberCardReturnItemDTO> memberCardReturnItemDTOs = memberCardReturnDTO.getMemberCardReturnItemDTOs();
      if(CollectionUtils.isNotEmpty(memberCardReturnItemDTOs)){
        for(MemberCardReturnItemDTO memberCardReturnItemDTO : memberCardReturnItemDTOs){
          memberCardReturnItemDTO.setMemberCardReturnId(memberCardReturn.getId());
          memberCardReturnItemDTO.setShopId(memberCardReturn.getShopId());
          MemberCardReturnItem memberCardReturnItem = new MemberCardReturnItem(memberCardReturnItemDTO);
          writer.save(memberCardReturnItem);
          memberCardReturnItemDTO.setId(memberCardReturnItem.getId());
        }
      }
      memberCardReturnDTO.setMemberCardReturnServiceDTOs(memberCardReturnServiceDTOs);

      ReceptionRecordDTO receptionRecordDTO = memberCardReturnDTO.getReceptionRecordDTO();
      receptionRecordDTO.setShopId(shopId);
      receptionRecordDTO.setMemberId(memberCardReturnDTO.getMemberDTO().getId());
      receptionRecordDTO.setOrderId(memberCardReturnDTO.getId());
      receptionRecordDTO.setReceptionDate(memberCardReturnDTO.getReturnDate());
      receptionRecordDTO.setPayeeId(memberCardReturnDTO.getExecutorId());
      receptionRecordDTO.setOrderTypeEnum(OrderTypes.MEMBER_RETURN_CARD);
      receptionRecordDTO.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
      receptionRecordDTO.setRecordNum(0);
      receptionRecordDTO.setAmount(0-NumberUtil.round(receptionRecordDTO.getAmount(), NumberUtil.MONEY_PRECISION));
      receptionRecordDTO.setCash(0-NumberUtil.round(receptionRecordDTO.getCash(), NumberUtil.MONEY_PRECISION));
      receptionRecordDTO.setBankCard(0-NumberUtil.round(receptionRecordDTO.getBankCard(), NumberUtil.MONEY_PRECISION));
      receptionRecordDTO.setCheque(0-NumberUtil.round(receptionRecordDTO.getCheque(), NumberUtil.MONEY_PRECISION));
      receptionRecordDTO.setAfterMemberDiscountTotal(receptionRecordDTO.getAmount());
      receptionRecordDTO.setOrderTotal(receptionRecordDTO.getAmount());
      receptionRecordDTO.setDayType(DayType.OTHER_DAY);

      ReceivableDTO receivableDTO = new ReceivableDTO();
      receivableDTO.setShopId(shopId);
      receivableDTO.setOrderType(OrderTypes.MEMBER_RETURN_CARD);
      receivableDTO.setOrderId(memberCardReturnDTO.getId());
      receivableDTO.setLastPayeeId(memberCardReturnDTO.getExecutorId());
      receivableDTO.setLastPayee(memberCardReturnDTO.getUserName());
      receivableDTO.setLastReceiveDate(memberCardReturnDTO.getReturnDate());
      receivableDTO.setStatus(ReceivableStatus.FINISH);

      receivableDTO.setSettledAmount(receptionRecordDTO.getAmount());
      receivableDTO.setTotal(receptionRecordDTO.getAmount());
      receivableDTO.setCash(receptionRecordDTO.getCash());
      receivableDTO.setBankCard(receptionRecordDTO.getBankCard());
      receivableDTO.setCheque(receptionRecordDTO.getCheque());
      receivableDTO.setCustomerId(memberCardReturnDTO.getCustomerId());
      receivableDTO.setVestDate(memberCardReturnDTO.getVestDate());
      receivableDTO.setAfterMemberDiscountTotal(receptionRecordDTO.getAmount());
      receivableDTO.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_PAYABLE);

      receivableDTO.setMemberId(receptionRecordDTO.getMemberId());
      receivableDTO.setMemberNo(memberCardReturnDTO.getMemberNo());

      ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
      ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
      receivableHistory.setMemberId(null);
      receivableHistory.setMemberNo(null);
      writer.save(receivableHistory);
      receivableHistoryDTO.setId(receivableHistory.getId());

      receptionRecordDTO.setReceivableHistoryId(receivableHistory.getId());
      ReceptionRecordDTO[] receptionRecordDTOs = {receptionRecordDTO};
      receivableDTO.setRecordDTOs(receptionRecordDTOs);
      createOrUpdateReceivable(writer,receivableDTO);

      writer.commit(status);
    }catch(Exception e){
      LOG.error("TxnService.saveMemberCardReturn方法出错.");
      LOG.error(e.getMessage(), e);
      writer.rollback(status);
      throw new Exception(e);
    } finally{
      writer.rollback(status);
    }

    return memberCardReturnDTO;
  }


  @Autowired
  private RepairOrderCostCaculator repairOrderCostCaculator;

  @Autowired
  private MemberServiceImporter memberServiceImporter;

  @Override
  public int countUndoneRepairOrderByVehicleId(Long shopId, Long vehicleId) throws Exception{
    String carStatus = "";
    TxnWriter writer = txnDaoManager.getWriter();
    try {
      return writer.countUndoneRepairOrderByVehicleId(shopId,vehicleId);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return 0;
  }
  public MemberCardReturnDTO getMemberCardReturnDTOById(Long shopId, Long id) {
    TxnWriter writer = txnDaoManager.getWriter();

    if (null == id) {
      return null;
    }
    MemberCardReturn memberCardReturn = writer.getMemberCardReturnById(shopId, id);

    if (null == memberCardReturn) {
      return null;
    }

    MemberCardReturnDTO memberCardReturnDTO = memberCardReturn.toDTO();

    List<MemberCardReturnItemDTO> memberCardReturnItemDTOs = getMemberCardReturnItemDTOByOrderId(shopId, id);
    List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOs = getMemberCardReturnServiceDTOByOrderId(shopId, id);
    memberCardReturnDTO.setMemberCardReturnItemDTOs(memberCardReturnItemDTOs);
    memberCardReturnDTO.setMemberCardReturnServiceDTOs(memberCardReturnServiceDTOs);
    return memberCardReturnDTO;
  }

  @Override
  public List<MemberCardReturnItemDTO> getMemberCardReturnItemDTOByOrderId(Long shopId, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<MemberCardReturnItem> memberCardReturnItems = writer.getMemberCardReturnItemByOrderId(shopId, orderId);

    List<MemberCardReturnItemDTO> memberCardReturnItemDTOs = null;
    if (CollectionUtils.isNotEmpty(memberCardReturnItems)) {
      memberCardReturnItemDTOs = new ArrayList<MemberCardReturnItemDTO>();
      for (MemberCardReturnItem memberCardReturnItem : memberCardReturnItems) {
        MemberCardReturnItemDTO memberCardReturnItemDTO = memberCardReturnItem.toDTO();
        memberCardReturnItemDTOs.add(memberCardReturnItemDTO);
      }
    }

    return memberCardReturnItemDTOs;
  }

  public List<MemberCardReturnServiceDTO> getMemberCardReturnServiceDTOByOrderId(Long shopId, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<MemberCardReturnService> memberCardReturnServices = writer.getMemberCardReturnServiceByOrderId(shopId, orderId);

    List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOs = null;
    if (CollectionUtils.isNotEmpty(memberCardReturnServices)) {
      memberCardReturnServiceDTOs = new ArrayList<MemberCardReturnServiceDTO>();
      for (MemberCardReturnService memberCardReturnService : memberCardReturnServices) {
        MemberCardReturnServiceDTO memberCardReturnServiceDTO = memberCardReturnService.toDTO();
        memberCardReturnServiceDTOs.add(memberCardReturnServiceDTO);
      }
    }

    return memberCardReturnServiceDTOs;
  }

  /**
   * 购卡时把服务设为可计次，用于在西车门美容中显示
   * @param shopId
   * @param serviceName
   * @return
   */
  @Override
  public ServiceDTO saveOrUpdateServiceForWashBeauty(Long shopId,String serviceName) throws Exception
  {
    if(StringUtils.isBlank(serviceName))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      Service service = writer.getRFServiceByServiceNameAndShopId(shopId,serviceName);
      Set<Long> set = new HashSet<Long>();
      if(null != service && ServiceStatus.DISABLED != service.getStatus() && ServiceTimeType.YES == service.getTimeType())
      {
        return service.toDTO();
      }
      else if(null != service && ServiceStatus.DISABLED == service.getStatus())
      {
        service.setStatus(ServiceStatus.ENABLED);
        service.setTimeType(ServiceTimeType.YES);
        writer.update(service);

        set.add(service.getId());
      }
      else if(null != service && ServiceTimeType.YES != service.getTimeType())
      {
        service.setTimeType(ServiceTimeType.YES);
        writer.update(service);
      }
      else if(null == service)
      {
        service = new Service();
        service.setName(serviceName);
        service.setShopId(shopId);
        service.setStatus(ServiceStatus.ENABLED);
        service.setTimeType(ServiceTimeType.YES);
        writer.save(service);
        set.add(service.getId());
      }

      writer.commit(status);

      if (CollectionUtils.isNotEmpty(set)) {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(shopId, set);
      }
      return service.toDTO();
    }finally {
      writer.rollback(status);
    }
  }

  /**
   * 改变服务是否计次
   * 购卡的时候用到，
   * 被删掉的服务在此情景不会有serviceId,以后谁用到此方法需考虑service 为DISABLED的情况
   * @param shopId
   * @param serviceId
   * @param timeType
   * @return
   */
  @Override
  public ServiceDTO changeServiceTimeType(Long shopId,Long serviceId,ServiceTimeType timeType)
  {
    if(null == shopId || null == serviceId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Service service = writer.getServiceById(shopId,serviceId);

    if(null ==service)
    {
      return null;
    }

    if(null == timeType)
    {
      return service.toDTO();
    }

    if(timeType == service.getTimeType())
    {
      return service.toDTO();
    }

    Object status = writer.begin();

    try{
      service.setTimeType(timeType);
      writer.update(service);
      writer.commit(status);

      return service.toDTO();
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void initServiceTimeType(List<Long> idList)
  {
    List<Service> serviceList = this.getServiceByIds(idList);

    if(CollectionUtils.isEmpty(serviceList))
    {
      return;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{

      for(Service service:serviceList)
      {

        service.setTimeType(ServiceTimeType.YES);
        writer.update(service);

      }

      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<Service> getServiceByIds(List<Long> idList)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    if(CollectionUtils.isEmpty(idList))
    {
      return null;
    }

    return writer.getServiceByIds(idList);
  }



  public List<MemberCardReturnDTO> getMemberReturnListByReturnDate(long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<MemberCardReturn> memberCardReturnList = writer.getMemberReturnListByReturnDate(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(memberCardReturnList)) {
      return null;
    }
    List<MemberCardReturnDTO> memberCardReturnDTOList = new ArrayList<MemberCardReturnDTO>();
    for (MemberCardReturn memberCardReturn : memberCardReturnList) {
      if (memberCardReturn == null) {
        continue;
      }
      memberCardReturnDTOList.add(memberCardReturn.toDTO());
    }
    return memberCardReturnDTOList;
  }

  /**
   * 批量新增导入单据，所有数据放在一个事务中
   *
   * @param orderTemps
   * @return
   */

  public boolean batchCreateImportedOrder(List<ImportedOrderTemp> orderTemps, TxnWriter txnWriter) throws BcgogoException {
    for(ImportedOrderTemp orderTemp:orderTemps){
      if(null==orderTemp){
        continue;
      }
      txnWriter.save(orderTemp);
    }
    return true;

  }

  /**
   * 从excel中导入客户
   *
   * @param importContext
   * @return
   */
  @Override
  public ImportResult importOrderFromExcel(ImportContext importContext) throws Exception {
    IImportService importService =ServiceManager.getService(IImportService.class);
    //2.校验数据
    CheckResult checkResult = orderImporter.checkData(importContext);
    ImportResult importResult = null;
    if (!checkResult.isPass()){
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }
    //3.保存数据
//    Object status = txnWriter.begin();
    try{
      importResult = orderImporter.importData(importContext);
      generateImportedOrder(importContext.getShopId());
//      txnWriter.commit(status);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }finally{
//      txnWriter.rollback(status);
    }
    return importResult;
  }

  /**
   * 生成imported_order imported_order_item
   * @param shopId
   * @return
   */
  public String generateImportedOrder(Long shopId) throws Exception {
    TxnWriter txnWriter= txnDaoManager.getWriter();
    ImportedOrderDTO importedOrderIndex=new ImportedOrderDTO();
    List<ImportedOrderTemp> importedOrderTemps= txnWriter.getAllImportedOrderTemp(shopId);
    if(CollectionUtils.isEmpty(importedOrderTemps)){
      return "";
    }
    ImportedOrder importedOrder=null;
    ImportedOrderItem orderItem=null;
    List<ImportedOrderTemp> orderTemps=null;
    //转换成 receipt--> order 的map结构
    Map<String,List<ImportedOrderTemp>> orderTempMap=new HashMap<String,List<ImportedOrderTemp>>();
    for(ImportedOrderTemp importedOrderTemp:importedOrderTemps){
      if(StringUtil.isEmpty(importedOrderTemp.getReceipt())){
        LOG.error("单据编号为空，importedOrderTemp={}",importedOrderTemp);
        continue;
      }
      if(orderTempMap.containsKey(importedOrderTemp.getReceipt())){
        orderTempMap.get(importedOrderTemp.getReceipt()).add(importedOrderTemp);
      }else{
        orderTemps=new ArrayList<ImportedOrderTemp>();
        orderTemps.add(importedOrderTemp);
        orderTempMap.put(importedOrderTemp.getReceipt(),orderTemps);
      }
    }
    //begin save order and orderItem
    Object status = txnWriter.begin();
    try{
      for(String receipt:orderTempMap.keySet()){
        orderTemps=orderTempMap.get(receipt);
        importedOrder=orderTemps.get(0).toImportedOrder();
        txnWriter.save(importedOrder);
        for(ImportedOrderTemp orderTemp:orderTemps){
          orderItem=orderTemp.toImportedOrderItem();
          orderItem.setOrderId(importedOrder.getId());
          orderItem.setShopId(shopId);
          txnWriter.save(orderItem);
          if("service".equals(orderItem.getItemType())&&StringUtil.isNotEmpty(orderItem.getProductName())){
            ImportedOrderItem item=orderItem.clone();
            item.setItemType(null);
            txnWriter.save(item);
          }
        }
      }
      txnWriter.commit(status);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      throw new BcgogoException(e);
    }finally {
      txnWriter.rollback(status);
    }

    status = txnWriter.begin();
    try{
      txnWriter.deleteImportedOrderTempByShopId(shopId);
      txnWriter.commit(status);
    }finally{
      txnWriter.rollback(status);
    }
    return null;
  }

  public void importSupplierRecord(Long shopId,List<SupplierRecordDTO> supplierRecordDTOList)
  {

    if(CollectionUtils.isEmpty(supplierRecordDTOList))
    {
      return;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{

      for(SupplierRecordDTO supplierRecordDTO : supplierRecordDTOList)
      {
        SupplierRecord supplierRecord = new SupplierRecord(supplierRecordDTO);
        if(null == supplierRecord || null == supplierRecord.getSupplierId())
        {
          continue;
        }

        supplierRecord.setShopId(shopId);

        writer.save(supplierRecord);
      }

      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }


  public ReceivableDTO saveReceivableDTO(ReceivableDTO receivableDTO) {
    if (receivableDTO == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (receivableDTO.getId() != null) {
        Receivable receivable = writer.getById(Receivable.class, receivableDTO.getId());
        if (receivable != null) {
          receivable = receivable.fromDTO(receivableDTO);
          writer.update(receivable);
        } else {
          receivable = new Receivable();
          receivable = receivable.fromDTO(receivableDTO);
          writer.save(receivable);
        }

        receivableDTO.setId(receivable.getId());
      } else {
        Receivable receivable = new Receivable();
        receivable = receivable.fromDTO(receivableDTO);
        writer.save(receivable);
        receivableDTO.setId(receivable.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return receivableDTO;
  }





  /**
   * 根据查询中心传过来的DTO，查导入表
   * @param searchConditionDTO
   * @return
   * @throws PageException
   */
  public OrderSearchResultListDTO getImportedOrderByConditions(OrderSearchConditionDTO searchConditionDTO) throws PageException {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    ImportedOrder importedOrderUtil = new ImportedOrder();
    ImportedOrderDTO importedOrderIndex=importedOrderUtil.fromOrderSearchConditionDTO(searchConditionDTO);
    importedOrderIndex.setPageSize(searchConditionDTO.getPageRows());
    importedOrderIndex.setStartPageNo(searchConditionDTO.getStartPageNo());
    Pager pager= new Pager(txnWriter.getImportedOrderCountByConditions(importedOrderIndex), importedOrderIndex.getStartPageNo(),importedOrderIndex.getPageSize());
    importedOrderIndex.setPager(pager);
    OrderSearchResultListDTO searchResultListDTO=new OrderSearchResultListDTO();
    List<OrderSearchResultDTO> searchResultDTOs= importedOrderUtil.toOrderSearchConditionDTO(txnWriter.getImportedOrderByConditions(importedOrderIndex));
    searchResultListDTO.setOrders(searchResultDTOs);
    List<ItemIndexDTO> itemIndexDTOs=null;
    if(CollectionUtils.isNotEmpty(searchResultDTOs)){
      for(OrderSearchResultDTO searchResultDTO:searchResultDTOs){
        itemIndexDTOs= txnWriter.getImportedOrderItemDTOByOrderId(searchConditionDTO.getShopId(),searchResultDTO.getOrderId());
        searchResultDTO.setItemIndexDTOs(itemIndexDTOs);
        searchResultDTO.setOrderContent(searchResultDTO.generateOrderContent());
      }

    }
    searchResultListDTO.setOrderTypeStat(txnWriter.getImportedOrderStatByOrderType(importedOrderIndex));
    searchResultListDTO.setNumFound(pager.getTotalRows());
    searchResultListDTO.setPager(pager);
//    searchResultListDTO.setTotalAmounts(totalAmounts);    //todo 统计导入单据金额
    return searchResultListDTO;
  }


  @Autowired
  private OrderImporter orderImporter;


  @Override
  public List<Long> getRepairOrderItemIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getRepairOrderItemIds(shopId, start, pageSize);
  }

  @Override
  public List<RepairOrderItemDTO> getRepairOrderItemDTOById(Long shopId, Long... repairOrderItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderItem> repairOrderItemList = writer.getRepairOrderItemsById(shopId,repairOrderItemId);
    List<RepairOrderItemDTO> repairOrderItemDTOList = new ArrayList<RepairOrderItemDTO>();
    if (CollectionUtils.isNotEmpty(repairOrderItemList)) {
      Set<Long> productLocalInfoIds = new HashSet<Long>();
      for (RepairOrderItem repairOrderItem : repairOrderItemList) {
        productLocalInfoIds.add(repairOrderItem.getProductId());
      }
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productLocalInfoIds);
      ProductDTO productDTO = null;
      RepairOrderItemDTO repairOrderItemDTO = null;
      for (RepairOrderItem repairOrderItem : repairOrderItemList) {
        repairOrderItemDTO = repairOrderItem.toDTO();
        productDTO = productDTOMap.get(repairOrderItem.getProductId());
        if (productDTO != null) {
          repairOrderItemDTO.setProductName(productDTO.getName());
          repairOrderItemDTO.setBrand(productDTO.getBrand());
          repairOrderItemDTO.setModel(productDTO.getModel());
          repairOrderItemDTO.setSpec(productDTO.getSpec());
          repairOrderItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
          repairOrderItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
          repairOrderItemDTO.setCommodityCode(productDTO.getCommodityCode());
          repairOrderItemDTO.setProductKind(productDTO.getKindName());
          repairOrderItemDTO.setProductKindId(productDTO.getKindId());
        }
        repairOrderItemDTOList.add(repairOrderItemDTO);
      }
    }
    return repairOrderItemDTOList;
  }

  @Override
  public List<Long> getPurchaseInventoryOrderItemIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseInventoryOrderItemIds(shopId, start, pageSize);
  }

  @Override
  public List<PurchaseInventoryItemDTO> getPurchaseInventoryOrderItemDTOById(Long shopId, Long... inventoryItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventoryItem> purchaseInventoryItemList = writer.getPurchaseInventoryOrderItemById(shopId, inventoryItemId);
    List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList = new ArrayList<PurchaseInventoryItemDTO>();
    if (CollectionUtils.isNotEmpty(purchaseInventoryItemList)) {
      Set<Long> productLocalInfoIds = new HashSet<Long>();
      for (PurchaseInventoryItem purchaseInventoryItem : purchaseInventoryItemList) {
        productLocalInfoIds.add(purchaseInventoryItem.getProductId());
      }
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productLocalInfoIds);
      ProductDTO productDTO = null;
      PurchaseInventoryItemDTO purchaseInventoryItemDTO = null;
      for (PurchaseInventoryItem purchaseInventoryItem : purchaseInventoryItemList) {
        purchaseInventoryItemDTO = purchaseInventoryItem.toDTO();
        productDTO = productDTOMap.get(purchaseInventoryItem.getProductId());
        if (productDTO != null) {
          purchaseInventoryItemDTO.setProductName(productDTO.getName());
          purchaseInventoryItemDTO.setBrand(productDTO.getBrand());
          purchaseInventoryItemDTO.setModel(productDTO.getModel());
          purchaseInventoryItemDTO.setSpec(productDTO.getSpec());
          purchaseInventoryItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
          purchaseInventoryItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
          purchaseInventoryItemDTO.setCommodityCode(productDTO.getCommodityCode());
          purchaseInventoryItemDTO.setProductKind(productDTO.getKindName());
          purchaseInventoryItemDTO.setProductKindId(productDTO.getKindId());
        }
        purchaseInventoryItemDTOList.add(purchaseInventoryItemDTO);
      }
    }
    return purchaseInventoryItemDTOList;
  }


  @Override
  public List<Long> getRepairOrderServiceItemIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getRepairOrderServiceItemIds(shopId, start, pageSize);
  }

  @Override
  public List<RepairOrderServiceDTO> getRepairOrderServiceDTOById(Long shopId, Long... repairOrderServiceItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderService> repairOrderServiceList = writer.getRepairOrderServiceItemsById(shopId,repairOrderServiceItemId);
    List<RepairOrderServiceDTO> repairOrderServiceDTOList = new ArrayList<RepairOrderServiceDTO>();
    if (CollectionUtils.isNotEmpty(repairOrderServiceList)) {
      for (RepairOrderService repairOrderService : repairOrderServiceList) {
        repairOrderServiceDTOList.add(repairOrderService.toDTO());
      }
    }
    return repairOrderServiceDTOList;
  }

  @Override
  public List<Long> getSaleOrderItemIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSaleOrderItemIds(shopId, start, pageSize);
  }

  @Override
  public List<SalesOrderItemDTO> getSaleOrderItemDTOById(Long shopId, Long... saleOrderItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesOrderItem> salesOrderItemList = writer.getSaleOrderItemsById(shopId,saleOrderItemId);
    List<SalesOrderItemDTO> salesOrderItemDTOList = new ArrayList<SalesOrderItemDTO>();
    if (CollectionUtils.isNotEmpty(salesOrderItemList)) {
      Set<Long> productLocalInfoIds = new HashSet<Long>();
      for (SalesOrderItem salesOrderItem : salesOrderItemList) {
        productLocalInfoIds.add(salesOrderItem.getProductId());
      }
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productLocalInfoIds);
      ProductDTO productDTO = null;
      SalesOrderItemDTO salesOrderItemDTO = null;
      for (SalesOrderItem salesOrderItem : salesOrderItemList) {
        salesOrderItemDTO = salesOrderItem.toDTO();
        productDTO = productDTOMap.get(salesOrderItem.getProductId());
        if (productDTO != null) {
          salesOrderItemDTO.setProductName(productDTO.getName());
          salesOrderItemDTO.setBrand(productDTO.getBrand());
          salesOrderItemDTO.setModel(productDTO.getModel());
          salesOrderItemDTO.setSpec(productDTO.getSpec());
          salesOrderItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
          salesOrderItemDTO.setVehicleModel(productDTO.getProductVehicleModel());
          salesOrderItemDTO.setCommodityCode(productDTO.getCommodityCode());
          salesOrderItemDTO.setProductKind(productDTO.getKindName());
          salesOrderItemDTO.setProductKindId(productDTO.getKindId());
        }
        salesOrderItemDTOList.add(salesOrderItemDTO);
      }
    }
    return salesOrderItemDTOList;
  }

  @Override
  public List<Long> getWashBeautyOrderItemIds(Long shopId, int start, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getWashBeautyOrderItemIds(shopId, start, pageSize);
  }

  @Override
  public List<WashBeautyOrderItemDTO> getWashBeautyOrderItemDTOById(Long shopId, Long... washItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<WashBeautyOrderItem> washBeautyOrderItemList = writer.getWashBeautyOrderItemsById(shopId,washItemId);
    List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOList = new ArrayList<WashBeautyOrderItemDTO>();
    if (CollectionUtils.isNotEmpty(washBeautyOrderItemList)) {
      for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItemList) {
        washBeautyOrderItemDTOList.add(washBeautyOrderItem.toDTO());
      }
    }
    return washBeautyOrderItemDTOList;
  }

  public List<RepairOrderService> getRepairOrderService()
  {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getRepairOrderService();
  }

  public List<RepairOrderService> initRepairOrderServiceCategory(List<RepairOrderService> repairOrderServiceList,Map<Long,Category> map)
  {
    if(CollectionUtils.isEmpty(repairOrderServiceList))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      for(RepairOrderService repairOrderService : repairOrderServiceList)
      {
        if(null == map.get(repairOrderService.getServiceId()))
        {
          continue;
        }

        Category category = map.get(repairOrderService.getServiceId());

        repairOrderService.setBusinessCategoryId(category.getId());

        repairOrderService.setBusinessCategoryName(category.getCategoryName());

        writer.update(repairOrderService);

      }
      writer.commit(status);
      return repairOrderServiceList;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public PurchaseInventory getFirstPurchaseInventoryByVestDate(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstPurchaseInventoryByVestDate(shopId);
  }

  @Override
  public List<PurchaseInventoryDTO> getPurchaseInventoryDTOByVestDate(Long shopId, long start, long end) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventory> purchaseInventoryList = writer.getPurchaseInventoryDTOByVestDate(shopId, start, end);
    List<PurchaseInventoryDTO> purchaseInventoryDTOs = new ArrayList<PurchaseInventoryDTO>();
    if(CollectionUtils.isEmpty(purchaseInventoryList)){
      return null;
    }
    for(PurchaseInventory purchaseInventory :purchaseInventoryList){
      PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
      List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(purchaseInventory.getId());
      if(CollectionUtils.isEmpty(items)){
        continue;
      }
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[items.size()];
      for(int i=0;i<items.size();i++){
        PurchaseInventoryItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      purchaseInventoryDTO.setItemDTOs(itemDTOs);
      purchaseInventoryDTOs.add(purchaseInventoryDTO);
    }
    return purchaseInventoryDTOs;
  }

  @Override
  public void batchCreateProductModifyLog(List<ProductModifyLogDTO> logResults) {
    TxnWriter writer = txnDaoManager.getWriter();
    if(CollectionUtils.isEmpty(logResults))
      return;
    Object status = writer.begin();
    try{
      Long id = 0L;
      for(int i = 0; i<logResults.size(); i++){
        ProductModifyLogDTO logDTO = logResults.get(i);
        ProductModifyLog log = new ProductModifyLog(logDTO, false);
        if(i>0){
          log.setOperationId(id);
        }
        writer.save(log);
        if(i == 0){
          id = log.getId();
          log.setOperationId(id);
          writer.update(log);
        }
      }
      writer.commit(status);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public List<ProductModifyLogDTO> getProductModifyLogByStatus(ProductModifyOperations productModifyOperation, StatProcessStatus[] status) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ProductModifyLog> logs = writer.getProductModifyLogByStatus(productModifyOperation, status);
    if(CollectionUtils.isEmpty(logs)){
      return null;
    }
    List<ProductModifyLogDTO> dtos = new ArrayList<ProductModifyLogDTO>();
    for(ProductModifyLog log : logs){
      dtos.add(log.toDTO());
    }
    return dtos;
  }
  @Override
  public void updateProductModifyLogDTORelevanceStatus(Long productLocalInfoId, ProductRelevanceStatus relevanceStatus) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ProductModifyLog> logs = writer.getProductModifyLogDTOByRelevanceStatus(null,productLocalInfoId);
    if(CollectionUtils.isEmpty(logs)){
      return;
    }
    Object status = writer.begin();
    try{
      for(ProductModifyLog log : logs){
        log.setRelevanceStatus(relevanceStatus);
        writer.update(log);
      }
      writer.commit(status);
    }finally{
      writer.rollback(status);
    }

  }
  @Override
  public List<ProductModifyLogDTO> getProductModifyLogDTOByRelevanceStatus(Long productLocalInfoId,ProductRelevanceStatus relevanceStatus) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ProductModifyLog> logs = writer.getProductModifyLogDTOByRelevanceStatus(relevanceStatus,productLocalInfoId);
    if(CollectionUtils.isEmpty(logs)){
      return null;
    }
    List<ProductModifyLogDTO> dtos = new ArrayList<ProductModifyLogDTO>();
    for(ProductModifyLog log : logs){
      dtos.add(log.toDTO());
    }
    return dtos;
  }

  public Map<Long,List<ProductModifyFields>> getRelevanceStatusUnCheckedProductModifiedFieldsMap(Long... productLocalInfoId){
    Map<Long,List<ProductModifyFields>> productModifyFieldsMap = new HashMap<Long, List<ProductModifyFields>>();
    if(ArrayUtils.isEmpty(productLocalInfoId)){
      return productModifyFieldsMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<ProductModifyLog> logs = writer.getProductModifyLogDTOByRelevanceStatus(ProductRelevanceStatus.UN_CHECKED,productLocalInfoId);
    if(CollectionUtils.isEmpty(logs)){
      return productModifyFieldsMap;
    }
    List<ProductModifyLogDTO> modifyLogDTOs = null;
    Map<Long,List<ProductModifyLogDTO>> productModifyLogsMap = new HashMap<Long, List<ProductModifyLogDTO>>();
    for(ProductModifyLog log : logs){
      modifyLogDTOs = productModifyLogsMap.get(log.getProductId());
      if(modifyLogDTOs==null){
        modifyLogDTOs = new ArrayList<ProductModifyLogDTO>();
      }
      modifyLogDTOs.add(log.toDTO());
      productModifyLogsMap.put(log.getProductId(),modifyLogDTOs);
    }

    for(Map.Entry<Long,List<ProductModifyLogDTO>> entry:productModifyLogsMap.entrySet()){
      productModifyFieldsMap.put(entry.getKey(), generateModifyFields(entry.getValue()));
    }
    return productModifyFieldsMap;
  }


  private List<ProductModifyFields> generateModifyFields(List<ProductModifyLogDTO> modifyLogDTOs) {
    List<ProductModifyLogDTO> commodityCodeLogDTOList=new ArrayList<ProductModifyLogDTO>();
    List<ProductModifyFields> modifyFields=new ArrayList<ProductModifyFields>();
    List<ProductModifyLogDTO> nameLogDTOList=new ArrayList<ProductModifyLogDTO>();
    List<ProductModifyLogDTO> brandLogDTOList=new ArrayList<ProductModifyLogDTO>();
    List<ProductModifyLogDTO> specLogDTOList=new ArrayList<ProductModifyLogDTO>();
    List<ProductModifyLogDTO> modelLogDTOList=new ArrayList<ProductModifyLogDTO>();
    List<ProductModifyLogDTO> vehicleBrandLogDTOList=new ArrayList<ProductModifyLogDTO>();
    List<ProductModifyLogDTO> vehicleModelLogDTOList=new ArrayList<ProductModifyLogDTO>();
    boolean existUnitModifyFlag=false;
    for(ProductModifyLogDTO modifyLogDTO:modifyLogDTOs){
      if(!ProductModifyTables.PRODUCT.equals(modifyLogDTO.getTableName())
        &&!ProductModifyTables.PRODUCT_LOCAL_INFO.equals(modifyLogDTO.getTableName())){
        continue;
      }
      if(ProductModifyFields.commodityCode.equals(modifyLogDTO.getFieldName())){
        commodityCodeLogDTOList.add(modifyLogDTO);
      }
      if(ProductModifyFields.name.equals(modifyLogDTO.getFieldName())){
        nameLogDTOList.add(modifyLogDTO);
      }
      if(ProductModifyFields.brand.equals(modifyLogDTO.getFieldName())){
        brandLogDTOList.add(modifyLogDTO);
      }
      if(ProductModifyFields.spec.equals(modifyLogDTO.getFieldName())){
        specLogDTOList.add(modifyLogDTO);
      }
      if(ProductModifyFields.model.equals(modifyLogDTO.getFieldName())){
        modelLogDTOList.add(modifyLogDTO);
      }
      if(ProductModifyFields.productVehicleBrand.equals(modifyLogDTO.getFieldName())){
        vehicleBrandLogDTOList.add(modifyLogDTO);
      }
      if(ProductModifyFields.productVehicleModel.equals(modifyLogDTO.getFieldName())){
        vehicleModelLogDTOList.add(modifyLogDTO);
      }
      if(ProductModifyFields.sellUnit.equals(modifyLogDTO.getFieldName())||ProductModifyFields.storageUnit.equals(modifyLogDTO.getFieldName())){
        existUnitModifyFlag=true;
      }
    }
    if(CollectionUtil.isNotEmpty(nameLogDTOList)){
      if(!StringUtil.compareSame(nameLogDTOList.get(0).getOldValue(), nameLogDTOList.get(nameLogDTOList.size() - 1).getNewValue())){
        modifyFields.add(ProductModifyFields.name);
      }
    }

    if(CollectionUtil.isNotEmpty(brandLogDTOList)){
      if(!StringUtil.compareSame(brandLogDTOList.get(0).getOldValue(),brandLogDTOList.get(brandLogDTOList.size()-1).getNewValue())){
        modifyFields.add(ProductModifyFields.brand);
      }
    }
    if(CollectionUtil.isNotEmpty(specLogDTOList)){
      if(!StringUtil.compareSame(specLogDTOList.get(0).getOldValue(),specLogDTOList.get(specLogDTOList.size()-1).getNewValue())){
        modifyFields.add(ProductModifyFields.spec);
      }
    }
    if(CollectionUtil.isNotEmpty(modelLogDTOList)){
      if(!StringUtil.compareSame(modelLogDTOList.get(0).getOldValue(),modelLogDTOList.get(modelLogDTOList.size()-1).getNewValue())){
        modifyFields.add(ProductModifyFields.model);
      }
    }
    if(CollectionUtil.isNotEmpty(vehicleBrandLogDTOList)){
      if(!StringUtil.compareSame(vehicleBrandLogDTOList.get(0).getOldValue(),vehicleBrandLogDTOList.get(vehicleBrandLogDTOList.size()-1).getNewValue())){
        modifyFields.add(ProductModifyFields.productVehicleBrand);
      }
    }
    if(CollectionUtil.isNotEmpty(vehicleModelLogDTOList)){
      if(!StringUtil.compareSame(vehicleModelLogDTOList.get(0).getOldValue(),vehicleModelLogDTOList.get(vehicleModelLogDTOList.size()-1).getNewValue())){
        modifyFields.add(ProductModifyFields.productVehicleModel);
      }
    }
    if(existUnitModifyFlag){
      modifyFields.add(ProductModifyFields.sellUnit);
    }
    return modifyFields;
  }

  @Override
  public void batchUpdateProductModifyLogStatus(List<ProductModifyLogDTO> toProcessLogs, StatProcessStatus doneStatus){
    if(CollectionUtils.isEmpty(toProcessLogs))
      return;
    List<Long> ids = new ArrayList<Long>();
    for(ProductModifyLogDTO dto:toProcessLogs){
      ids.add(dto.getId());
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      writer.batchUpdateProductModifyLogStatus(ids, doneStatus);
      writer.commit(status);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public PurchaseReturn getFirstPurchaseReturnByVestDate(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstPurchaseReturnByVestDate(shopId);
  }

  @Override
  public List<PurchaseReturnDTO> getPurchaseReturnDTOByVestDate(Long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseReturn> purchaseReturnList = writer.getPurchaseReturnByVestDate(shopId, startTime, endTime);
    if(CollectionUtils.isEmpty(purchaseReturnList)){
      return null;
    }
    List<PurchaseReturnDTO> purchaseReturnDTOs = new ArrayList<PurchaseReturnDTO>();
    for(PurchaseReturn purchaseReturn : purchaseReturnList){
      PurchaseReturnDTO dto = purchaseReturn.toDTO();
      List<PurchaseReturnItem> items = writer.getPurchaseReturnItemsByReturnId(purchaseReturn.getId());
      if(CollectionUtils.isEmpty(items)){
        continue;
      }
      PurchaseReturnItemDTO[] itemDTOs = new PurchaseReturnItemDTO[items.size()];
      for(int i= 0; i<items.size(); i++){
        PurchaseReturnItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      dto.setItemDTOs(itemDTOs);
      purchaseReturnDTOs.add(dto);
    }
    return purchaseReturnDTOs;
  }

  @Override
  public SalesOrder getFirstSalesOrderByVestDate(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstSalesOrderByVestDate(shopId);
  }

  @Override
  public RepairOrder getFirstRepairOrderByVestDate(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstRepairOrderByVestDate(shopId);
  }

  @Override
  public List<RepairOrderDTO> getRepairOrderListByVestDate(Long shopId,long startTime,long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrder> repairOrderList = writer.getRepairOrderListByVestDate(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(repairOrderList)) {
      return null;
    }
    List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
    for (RepairOrder repairOrder : repairOrderList) {
      if (repairOrder == null) {
        continue;
      }
      repairOrderDTOList.add(repairOrder.toDTO());
    }
    return repairOrderDTOList;
  }

  @Override
  public Long getFirstPurchaseInventoryCreationDateByProductIdShopId(Long shopId, Long productId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstPurchaseInventoryCreationDateByProductIdShopId(shopId, productId);
  }

  @Override
  public List<PurchaseInventoryDTO> getPurchaseInventoryDTOByProductIdCreationDate(Long shopId, Long productId, Long startTime, Long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventory> purchaseInventoryList = writer.getPurchaseInventoryByProductIdCreationDate(shopId, productId, startTime, endTime);
    List<PurchaseInventoryDTO> purchaseInventoryDTOs = new ArrayList<PurchaseInventoryDTO>();
    if(CollectionUtils.isEmpty(purchaseInventoryList)){
      return null;
    }
    for(PurchaseInventory purchaseInventory :purchaseInventoryList){
      PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
      List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(purchaseInventory.getId());
      if(CollectionUtils.isEmpty(items)){
        continue;
      }
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[items.size()];
      for(int i=0;i<items.size();i++){
        PurchaseInventoryItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      purchaseInventoryDTO.setItemDTOs(itemDTOs);
      purchaseInventoryDTOs.add(purchaseInventoryDTO);
    }
    return purchaseInventoryDTOs;
  }

  @Override
  public Long getFirstPurchaseReturnCreationDateByProductIdShopId(Long shopId, Long productId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstPurchaseReturnCreationDateByProductIdShopId(shopId, productId);
  }

  @Override
  public List<PurchaseReturnDTO> getPurchaseReturnDTOByProductIdCreationDate(Long shopId, Long productId, Long startTime, Long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseReturn> purchaseReturnList = writer.getPurchaseReturnByProductIdCreationDate(shopId, productId, startTime, endTime);
    if(CollectionUtils.isEmpty(purchaseReturnList)){
      return null;
    }
    List<PurchaseReturnDTO> purchaseReturnDTOs = new ArrayList<PurchaseReturnDTO>();
    for(PurchaseReturn purchaseReturn : purchaseReturnList){
      PurchaseReturnDTO dto = purchaseReturn.toDTO();
      List<PurchaseReturnItem> items = writer.getPurchaseReturnItemsByReturnId(purchaseReturn.getId());
      if(CollectionUtils.isEmpty(items)){
        continue;
      }
      PurchaseReturnItemDTO[] itemDTOs = new PurchaseReturnItemDTO[items.size()];
      for(int i= 0; i<items.size(); i++){
        PurchaseReturnItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      dto.setItemDTOs(itemDTOs);
      purchaseReturnDTOs.add(dto);
    }
    return purchaseReturnDTOs;
  }

  @Override
  public List<Long> getPurchaseInventoryIdFromPayableHistory(Long shopId, Long purchaseReturnId, PaymentTypes paymentType) {
    return txnDaoManager.getWriter().getPurchaseInventoryIdFromPayableHistory(shopId, purchaseReturnId, paymentType);
  }

  @Override
  public WashBeautyOrder getFirstWashBeautyOrderByVestDate(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstWashBeautyOrderByVestDate(shopId);
  }

  @Override
  public List<WashBeautyOrderDTO> getWashBeautyOrderDTOByVestDate(Long shopId, long startTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<WashBeautyOrder> washBeautyOrders = writer.getWashBeautyOrderByVestDate(shopId, startTime, endTime);
    if(CollectionUtils.isEmpty(washBeautyOrders)){
      return null;
    }
    List<WashBeautyOrderDTO> washBeautyOrderDTOs = new ArrayList<WashBeautyOrderDTO>();
    for(WashBeautyOrder order : washBeautyOrders){
      washBeautyOrderDTOs.add(order.toDTO());
    }
    return washBeautyOrderDTOs;
  }

  @Override
  public Long getFirstRepairOrderCreationTimeByVehicleId(Long shopId, Long vehicleId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstRepairOrderCreationTimeByVehicleId(shopId, vehicleId);
  }

  @Override
  public Long getFirstWashBeautyOrderCreationTimeByVehicleId(Long shopId, Long vehicleId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getFirstWashBeautyOrderCreationTimeByVehicleId(shopId, vehicleId);
  }

  @Override
  public List<PurchaseInventoryDTO> getPurchaseInventoryDTOByCreationDate(Long shopId, long begin, long end){
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventory> purchaseInventoryList = writer.getPurchaseInventoryDTOByCreationDate(shopId, begin, end);
    List<PurchaseInventoryDTO> purchaseInventoryDTOs = new ArrayList<PurchaseInventoryDTO>();
    if(CollectionUtils.isEmpty(purchaseInventoryList)){
      return null;
    }
    for(PurchaseInventory purchaseInventory :purchaseInventoryList){
      PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
      List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(purchaseInventory.getId());
      if(CollectionUtils.isEmpty(items)){
        continue;
      }
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[items.size()];
      for(int i=0;i<items.size();i++){
        PurchaseInventoryItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }
      purchaseInventoryDTO.setItemDTOs(itemDTOs);
      purchaseInventoryDTOs.add(purchaseInventoryDTO);
    }
    return purchaseInventoryDTOs;
  }

  /**
   * 根据shop_id获取一段时间内购卡续卡单据的条数 和总金额
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  @Override
  public List<String> getMemberReturnOrderCountAndSum(long shopId, long startTime, long endTime,OrderSearchConditionDTO orderSearchConditionDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<String> stringList = writer.getMemberReturnOrderCountAndSum(shopId, startTime, endTime,orderSearchConditionDTO);
    return stringList;
  }

  public List<MemberCardReturnDTO> getMemberReturnListByPagerTimeArrayType(long shopId, long startTime, long endTime, Pager pager, String arrayType,OrderSearchConditionDTO orderSearchConditionDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<MemberCardReturn> memberCardList = writer.getMemberReturnListByPagerTimeArrayType(shopId, startTime, endTime, pager, arrayType,orderSearchConditionDTO);
    if (CollectionUtils.isEmpty(memberCardList)) {
      return null;
    }
    List<MemberCardReturnDTO> memberCardOrderDTOList = new ArrayList<MemberCardReturnDTO>();
    for (MemberCardReturn memberCardOrder : memberCardList) {
      if (memberCardOrder == null) {
        continue;
      }
      memberCardOrderDTOList.add(memberCardOrder.toDTO());
    }
    return memberCardOrderDTOList;
  }


  @Override
  public Long getTodoSalesOrderCount(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus) {
    Long count = 0l;
    TxnWriter writer = txnDaoManager.getWriter();
    count = writer.getTodoSalesOrderCount(shopId,startTime,endTime,customerIdList,receiptNo,orderStatus).longValue();
    return count;
  }

  @Override
  public List<SalesOrderDTO> getTodoSalesOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrder> salesOrderList = writer.getTodoSalesOrderDTOListByCondition(shopId, startTime, endTime, customerIdList, receiptNo, orderStatus, pager);
    if(CollectionUtils.isEmpty(salesOrderList)){
      return null;
    }

    List<SalesOrderDTO> salesOrderDTOList = new ArrayList<SalesOrderDTO>();
    for (int i=0;i<salesOrderList.size();i++) {
      SalesOrder salesOrder = salesOrderList.get(i);
      if (salesOrder == null) {
        continue;
      }
      SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
      salesOrderDTOList.add(salesOrderDTO);
    }
    return salesOrderDTOList;
  }

  @Override
  public List<SalesOrderDTO> getAllTodoSalesOrderDTOList(Long shopId, List<Long> customerIdList) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesOrder> salesOrderList = writer.getAllTodoSalesOrderDTOList(shopId, customerIdList);
    if(CollectionUtils.isEmpty(salesOrderList)){
      return null;
    }

    List<SalesOrderDTO> salesOrderDTOList = new ArrayList<SalesOrderDTO>();
    for (int i=0;i<salesOrderList.size();i++) {
      SalesOrder salesOrder = salesOrderList.get(i);
      if (salesOrder == null) {
        continue;
      }
      SalesOrderDTO salesOrderDTO = salesOrder.toDTO();
      salesOrderDTOList.add(salesOrderDTO);
    }
    return salesOrderDTOList;
  }

  @Override
  public Long getTodoSalesReturnOrderCount(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus) {
    Long count = 0l;
    TxnWriter writer = txnDaoManager.getWriter();
    count = writer.getTodoSalesReturnOrderCount(shopId,startTime,endTime,customerIdList,receiptNo,orderStatus).longValue();
    return count;
  }

  @Override
  public List<SalesReturnDTO> getTodoSalesReturnOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SalesReturn> salesReturnOrderList = writer.getTodoSalesReturnOrderDTOListByCondition(shopId, startTime, endTime, customerIdList, receiptNo, orderStatus, pager);
    if(CollectionUtils.isEmpty(salesReturnOrderList)){
      return null;
    }

    List<SalesReturnDTO> salesReturnOrderDTOList = new ArrayList<SalesReturnDTO>();
    for (int i=0;i<salesReturnOrderList.size();i++) {
      SalesReturn salesReturn = salesReturnOrderList.get(i);
      if (salesReturn == null) {
        continue;
      }
      SalesReturnDTO salesReturnDTO = salesReturn.toDTO();
      salesReturnOrderDTOList.add(salesReturnDTO);
    }
    return salesReturnOrderDTOList;
  }

  @Override
  public Long getTodoPurchaseOrderCount(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, String timeField) {
    Long count = 0l;
    TxnWriter writer = txnDaoManager.getWriter();
    count = writer.getTodoPurchaseOrderCount(shopId,startTime,endTime,supplierIdList,receiptNo,orderStatus, timeField).longValue();
    return count;
  }

  @Override
  public List<PurchaseOrderDTO> getTodoPurchaseOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager, String timeField)throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseOrder> purchaseOrderList = writer.getTodoPurchaseOrderDTOListByCondition(shopId, startTime, endTime, supplierIdList, receiptNo, orderStatus, pager, timeField);
    if(CollectionUtils.isEmpty(purchaseOrderList)){
      return null;
    }

    List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();
    for (PurchaseOrder purchaseOrder : purchaseOrderList) {
      PurchaseOrderDTO purchaseOrderDTO = purchaseOrder.toDTO();
      if (purchaseOrder == null) {
        continue;
      }
      purchaseOrderDTO = purchaseOrder.toDTO();
      purchaseOrderDTO.setId(purchaseOrder.getId());
      purchaseOrderDTO.setEditDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, purchaseOrderDTO.getEditDate()));
      purchaseOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, purchaseOrderDTO.getVestDate()));
      purchaseOrderDTO.setDeliveryDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, purchaseOrderDTO.getDeliveryDate()));
      List<PurchaseOrderItem> purchaseOrderItemList = writer.getPurchaseOrderItemsByOrderId(purchaseOrder.getId());
      PurchaseOrderItemDTO[] purchaseOrderItemDTOs = new PurchaseOrderItemDTO[purchaseOrderItemList.size()];
      purchaseOrderDTO.setItemDTOs(purchaseOrderItemDTOs);
      for (int i = 0; i < purchaseOrderItemList.size(); i++) {
        purchaseOrderItemDTOs[i] = purchaseOrderItemList.get(i).toDTO();

        if(purchaseOrderDTO.getSupplierShopId()!=null){//在线采购单
          ProductHistoryDTO supplierProductHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(purchaseOrderItemDTOs[i].getProductHistoryId(), purchaseOrderDTO.getSupplierShopId());
          ProductDTO supplierProductDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(purchaseOrderItemDTOs[i].getSupplierProductId(), purchaseOrderDTO.getSupplierShopId());
          if(supplierProductHistoryDTO!=null){
            purchaseOrderItemDTOs[i].setWholesalerProductHistoryDTO(supplierProductHistoryDTO);
          }else{
            purchaseOrderItemDTOs[i].setWholesalerProductDTO(supplierProductDTO);
          }
        }else{
          ProductHistoryDTO productHistoryDTO = ServiceManager.getService(IProductHistoryService.class).getProductHistoryById(purchaseOrderItemDTOs[i].getProductHistoryId(), shopId);
          ProductDTO productDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(purchaseOrderItemDTOs[i].getProductId(), purchaseOrderDTO.getShopId());
          if(productHistoryDTO!=null){
            purchaseOrderItemDTOs[i].setProductHistoryDTO(productHistoryDTO);
            if(OrderUtil.purchaseOrderInProgress.contains(purchaseOrderDTO.getStatus())){
              purchaseOrderItemDTOs[i].setProductUnitRateInfo(productDTO);
            }
          }else{
            purchaseOrderItemDTOs[i].setProductDTOWithOutUnit(productDTO);
          }
        }
      }
      if(purchaseOrderDTO.getExpressId() != null){
        purchaseOrderDTO.setExpressDTO(ServiceManager.getService(RFITxnService.class).getExpressDTOById(purchaseOrderDTO.getExpressId()));
      }
      purchaseOrderDTOList.add(purchaseOrderDTO);
    }
    return purchaseOrderDTOList;
  }

  @Override
  public Long getTodoPurchaseReturnOrderCount(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus) {
    Long count = 0l;
    TxnWriter writer = txnDaoManager.getWriter();
    count = writer.getTodoPurchaseReturnOrderCount(shopId,startTime,endTime,supplierIdList,receiptNo,orderStatus).longValue();
    return count;
  }

  @Override
  public List<PurchaseReturnDTO> getTodoPurchaseReturnOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<PurchaseReturn> purchaseReturnOrderList = writer.getTodoPurchaseReturnOrderDTOListByCondition(shopId, startTime, endTime, supplierIdList, receiptNo, orderStatus, pager);
    if(CollectionUtils.isEmpty(purchaseReturnOrderList)){
      return null;
    }

    List<PurchaseReturnDTO> purchaseReturnOrderDTOList = new ArrayList<PurchaseReturnDTO>();
    for (int i=0;i<purchaseReturnOrderList.size();i++) {
      PurchaseReturn purchaseReturn = purchaseReturnOrderList.get(i);
      if (purchaseReturn == null) {
        continue;
      }
      PurchaseReturnDTO purchaseReturnDTO = purchaseReturn.toDTO();
      purchaseReturnOrderDTOList.add(purchaseReturnDTO);
    }
    return purchaseReturnOrderDTOList;
  }

  @Override
  public void saveOrUpdateOrderIndexByOrderIdAndOrderType(Long shopId, OrderTypes orderType, Long... orderId) throws Exception {
    if (orderId == null) throw new Exception("orderId is null");
    if (shopId == null) throw new Exception("shopId is null");
    if (orderType == null) throw new Exception("orderType is null");
    List<OrderIndexDTO> orderIndexDTOList = null;
    switch (orderType) {
      case RETURN:
        orderIndexDTOList = generatePurchaseReturnOrderIndex(shopId, orderId);
        break;
      case SALE_RETURN:
        orderIndexDTOList = generateSalesReturnOrderIndex(shopId, orderId);
        break;
      case PURCHASE:
        orderIndexDTOList = generatePurchaseOrderIndex(shopId, orderId);
        break;
      case SALE:
        orderIndexDTOList = generateSalesOrderIndex(shopId, orderId);
        break;
      default:
        LOG.error("saveOrUpdateOrderIndexByOrderIdAndOrderType shopId:{},orderId:{},orderType is not found!", shopId, orderId);
    }
    if(CollectionUtils.isNotEmpty(orderIndexDTOList)){
      IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
      for(OrderIndexDTO orderIndexDTO:orderIndexDTOList){
        orderIndexService.saveOrUpdateOrderIndex(orderIndexDTO);
      }
    }
  }

  private List<OrderIndexDTO> generateSalesReturnOrderIndex(Long shopId, Long[] orderId) throws Exception{
    List<SalesReturnDTO> salesReturnDTOList = getSalesReturnByShopIdAndOrderIds(shopId, orderId);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<SalesReturnItemDTO> salesReturnItemDTOList = getSalesReturnItemDTOs(shopId,orderId);
    Set<Long> productIds = new HashSet<Long>(salesReturnItemDTOList.size());
    Map<Long, List<SalesReturnItemDTO>> salesReturnItemMap = new HashMap<Long, List<SalesReturnItemDTO>>(salesReturnItemDTOList.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(salesReturnItemDTOList)) {
      for (SalesReturnItemDTO itemDTO : salesReturnItemDTOList) {
        if (itemDTO != null && itemDTO.getProductId() != null) {
          productIds.add(itemDTO.getProductId());
        }
      }
    }
    Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productIds);
    if (CollectionUtils.isNotEmpty(salesReturnItemDTOList)) {
      for (SalesReturnItemDTO itemDTO : salesReturnItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getProductId() != null) {
          itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));
        }
        if (itemDTO.getSalesReturnId() != null) {
          List<SalesReturnItemDTO> itemDTOList = salesReturnItemMap.get(itemDTO.getSalesReturnId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            salesReturnItemMap.put(itemDTO.getSalesReturnId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<SalesReturnItemDTO>();
            itemDTOList.add(itemDTO);
            salesReturnItemMap.put(itemDTO.getSalesReturnId(), itemDTOList);
          }
        }
      }
    }
    for (SalesReturnDTO salesReturnDTO : salesReturnDTOList) {
      if (salesReturnDTO == null) {
        continue;
      }
      List<SalesReturnItemDTO> itemDTOList = salesReturnItemMap.get(salesReturnDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        salesReturnDTO.setItemDTOs(itemDTOList.toArray(new SalesReturnItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = salesReturnDTO.toOrderIndexDTO();
      orderIndexDTOList.add(orderIndexDTO);
    }
    return orderIndexDTOList;
  }

  private List<OrderIndexDTO> generatePurchaseReturnOrderIndex(Long shopId, Long[] orderId) throws Exception {
    List<PurchaseReturnDTO> purchaseReturnDTOs = getPurchaseReturnByShopIdAndOrderIds(shopId, orderId);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    for (int i = 0, len = purchaseReturnDTOs.size(); i < len; i++) {
      if (purchaseReturnDTOs.get(i) == null) {
        continue;
      }
    }
    List<PurchaseReturnItemDTO> purchaseReturnItemDTOs = getPurchaseReturnItemDTOs(orderId);
    Set<Long> productIds = new HashSet<Long>(purchaseReturnItemDTOs.size());
    Map<Long, List<PurchaseReturnItemDTO>> purchaserReturnItemMap = new HashMap<Long, List<PurchaseReturnItemDTO>>(purchaseReturnItemDTOs.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(purchaseReturnItemDTOs)) {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnItemDTOs) {
        if (itemDTO != null && itemDTO.getProductId() != null) {
          productIds.add(itemDTO.getProductId());
        }
      }
    }
    Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productIds);
    if (CollectionUtils.isNotEmpty(purchaseReturnItemDTOs)) {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnItemDTOs) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getProductId() != null) {
          itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));
        }
        if (itemDTO.getPurchaseReturnId() != null) {
          List<PurchaseReturnItemDTO> itemDTOList = purchaserReturnItemMap.get(itemDTO.getPurchaseReturnId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            purchaserReturnItemMap.put(itemDTO.getPurchaseReturnId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<PurchaseReturnItemDTO>();
            itemDTOList.add(itemDTO);
            purchaserReturnItemMap.put(itemDTO.getPurchaseReturnId(), itemDTOList);
          }
        }
      }
    }
    for (PurchaseReturnDTO purchaseReturnDTO : purchaseReturnDTOs) {
      if (purchaseReturnDTO == null) {
        continue;
      }
      List<PurchaseReturnItemDTO> itemDTOList = purchaserReturnItemMap.get(purchaseReturnDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        purchaseReturnDTO.setItemDTOs(itemDTOList.toArray(new PurchaseReturnItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = purchaseReturnDTO.toOrderIndexDTO();
      orderIndexDTOList.add(orderIndexDTO);
    }
    return orderIndexDTOList;
  }

  private List<OrderIndexDTO> generateSalesOrderIndex(Long shopId, Long[] orderId) throws Exception{
    List<SalesOrderDTO> salesOrderDTOList = getSalesOrdersByShopIdAndOrderIds(shopId, orderId);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<SalesOrderItemDTO> salesOrderItemDTOList = getSalesOrderItemDTOs(shopId,orderId);
    Set<Long> productIds = new HashSet<Long>(salesOrderItemDTOList.size());
    Map<Long, List<SalesOrderItemDTO>> salesOrderItemMap = new HashMap<Long, List<SalesOrderItemDTO>>(salesOrderItemDTOList.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(salesOrderItemDTOList)) {
      for (SalesOrderItemDTO itemDTO : salesOrderItemDTOList) {
        if (itemDTO != null && itemDTO.getProductId() != null) {
          productIds.add(itemDTO.getProductId());
        }
      }
    }
    Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productIds);
    if (CollectionUtils.isNotEmpty(salesOrderItemDTOList)) {
      for (SalesOrderItemDTO itemDTO : salesOrderItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getProductId() != null) {
          itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));
        }
        if (itemDTO.getSalesOrderId() != null) {
          List<SalesOrderItemDTO> itemDTOList = salesOrderItemMap.get(itemDTO.getSalesOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            salesOrderItemMap.put(itemDTO.getSalesOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<SalesOrderItemDTO>();
            itemDTOList.add(itemDTO);
            salesOrderItemMap.put(itemDTO.getSalesOrderId(), itemDTOList);
          }
        }
      }
    }
    for (SalesOrderDTO salesOrderDTO : salesOrderDTOList) {
      if (salesOrderDTO == null) {
        continue;
      }
      List<SalesOrderItemDTO> itemDTOList = salesOrderItemMap.get(salesOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        salesOrderDTO.setItemDTOs(itemDTOList.toArray(new SalesOrderItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = salesOrderDTO.toOrderIndexDTO();
      orderIndexDTOList.add(orderIndexDTO);
    }
    return orderIndexDTOList;
  }

  private List<OrderIndexDTO> generatePurchaseOrderIndex(Long shopId, Long[] orderId) throws Exception{
    List<PurchaseOrderDTO> purchaseOrderDTOList = getPurchaseOrdersByShopIdAndOrderIds(shopId, orderId);
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    List<PurchaseOrderItemDTO> purchaseOrderItemDTOList = getPurchaseOrderItemDTOs(orderId);
    Set<Long> productIds = new HashSet<Long>(purchaseOrderItemDTOList.size());
    Map<Long, List<PurchaseOrderItemDTO>> purchaseOrderItemMap = new HashMap<Long, List<PurchaseOrderItemDTO>>(purchaseOrderItemDTOList.size() * 2, 0.75f);
    if (CollectionUtils.isNotEmpty(purchaseOrderItemDTOList)) {
      for (PurchaseOrderItemDTO itemDTO : purchaseOrderItemDTOList) {
        if (itemDTO != null && itemDTO.getProductId() != null) {
          productIds.add(itemDTO.getProductId());
        }
      }
    }
    Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(productIds);
    if (CollectionUtils.isNotEmpty(purchaseOrderItemDTOList)) {
      for (PurchaseOrderItemDTO itemDTO : purchaseOrderItemDTOList) {
        if (itemDTO == null) {
          continue;
        }
        if (itemDTO.getProductId() != null) {
          itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));
        }
        if (itemDTO.getPurchaseOrderId() != null) {
          List<PurchaseOrderItemDTO> itemDTOList = purchaseOrderItemMap.get(itemDTO.getPurchaseOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOList)) {
            itemDTOList.add(itemDTO);
            purchaseOrderItemMap.put(itemDTO.getPurchaseOrderId(), itemDTOList);
          } else {
            itemDTOList = new ArrayList<PurchaseOrderItemDTO>();
            itemDTOList.add(itemDTO);
            purchaseOrderItemMap.put(itemDTO.getPurchaseOrderId(), itemDTOList);
          }
        }
      }
    }
    for (PurchaseOrderDTO purchaseOrderDTO : purchaseOrderDTOList) {
      if (purchaseOrderDTO == null) {
        continue;
      }
      List<PurchaseOrderItemDTO> itemDTOList = purchaseOrderItemMap.get(purchaseOrderDTO.getId());
      if (CollectionUtils.isNotEmpty(itemDTOList)) {
        purchaseOrderDTO.setItemDTOs(itemDTOList.toArray(new PurchaseOrderItemDTO[itemDTOList.size()]));
      }
      OrderIndexDTO orderIndexDTO = purchaseOrderDTO.toOrderIndexDTO();
      orderIndexDTOList.add(orderIndexDTO);
    }
    return orderIndexDTOList;
  }
  @Override
  public List<String> getDebtFromReceivableByCustomerId(Long shopId, Long customerId, OrderDebtType orderDebtType, ReceivableStatus receivableStatus) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getDebtFromReceivableByCustomerId(shopId, customerId, orderDebtType, receivableStatus);
  }

  @Override
  public PurchaseInventoryDTO getPurchaseInventoryIdByPurchaseOrderId(Long shopId, Long purchaseOrderId) {
    PurchaseInventoryDTO purchaseInventoryDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseInventory pi = writer.getPurchaseInventoryIdByPurchaseOrderId(shopId,purchaseOrderId);
    if(pi!=null){
      purchaseInventoryDTO = pi.toDTO();
    }
    return purchaseInventoryDTO;
  }

  @Override
  public void updateSaleOrderItems(List<SalesOrderItem> salesOrderItemList){
    TxnWriter writer = txnDaoManager.getWriter();
    if (!CollectionUtils.isNotEmpty(salesOrderItemList)) {
      return;
    }
    Object status = writer.begin();
    try{
      for(SalesOrderItem salesOrderItem : salesOrderItemList){
        writer.update(salesOrderItem);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateDebtRemindStatus(Long debtId,Long shopId){
    TxnWriter writer = txnDaoManager.getWriter();
    Debt debt = writer.getById(Debt.class,debtId);
    if(debt==null){
      return;
    }
    Object status = writer.begin();
    try{
      debt.setRemindStatus(UserConstant.Status.REMINDED);
      writer.update(debt);
      writer.commit(status);
      // add by WLF 更新提醒总表中的状态
      RemindEventDTO remindEventDTO = getRemindEventByOldRemindEventId(RemindEventType.DEBT,shopId, debt.getId());
      if(remindEventDTO!=null){
        remindEventDTO.setRemindStatus(UserConstant.Status.REMINDED);
        updateRemindEvent(remindEventDTO);
      }
    }finally {
      writer.rollback(status);
    }
  }

  /**
   * REPEAL状态的已被去除.
   * @param shopId
   * @param orderId
   * @return
   */
  @Override
  public PayableDTO getPayableDTOByOrderId(Long shopId,Long orderId)
  {
    if(null == orderId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    Payable payable = writer.getPayableDTOByOrderId(shopId,orderId, false);
    if(null== payable)
    {
      return null;
    }

    return payable.toDTO();
  }

  @Override
  public DebtDTO getDebtByShopIdOrderId(Long shopId,Long orderId)
  {
    if(null == orderId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    List<Debt> debtList = writer.getDebtByShopIdAndOrderId(shopId,orderId);

    if(CollectionUtils.isEmpty(debtList))
    {
      return null;
    }

    return debtList.get(0).toDTO();
  }

  @Override
  public List<OtherIncomeKindDTO> vagueGetOtherIncomeKind(Long shopId,String keyWord)
  {
    if(null == shopId)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    List<OtherIncomeKind> otherIncomeKindList = writer.vagueGetOtherIncomeKind(shopId,keyWord);

    if(CollectionUtils.isEmpty(otherIncomeKindList))
    {
      return null;
    }

    List<OtherIncomeKindDTO> otherIncomeKindDTOList = new ArrayList<OtherIncomeKindDTO>();

    for(OtherIncomeKind otherIncomeKind : otherIncomeKindList)
    {
      otherIncomeKindDTOList.add(otherIncomeKind.toDTO());
    }

    return otherIncomeKindDTOList;
  }

  @Override
  public OtherIncomeKind changeOtherIncomeKindStatus(Long shopId,Long id,KindStatus status)
  {
    if(null == id)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object txnStatus = writer.begin();

    try{
      OtherIncomeKind otherIncomeKind = writer.getOtherIncomeKindById(shopId, id);
      if(null != otherIncomeKind && !status.equals(otherIncomeKind.getStatus()))
      {
        otherIncomeKind.setStatus(status);
        writer.update(otherIncomeKind);
      }
      writer.commit(txnStatus);
      return otherIncomeKind;
    }finally {
      writer.rollback(txnStatus);
    }
  }

  @Override
  public OtherIncomeKindDTO getOtherIncomeKindById(Long shopId,Long id)
  {
    if(null == id)
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    OtherIncomeKind otherIncomeKind = writer.getOtherIncomeKindById(shopId,id);

    if(null == otherIncomeKind)
    {
      return null;
    }

    return otherIncomeKind.toDTO();
  }

  @Override
  public List<OtherIncomeKind> getOtherIncomeKindByName(Long shopId,String name)
  {
    if(StringUtils.isBlank(name))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getOtherIncomeKindByName(shopId,name);
  }

  @Override
  public OtherIncomeKindDTO updateOtherIncomeKind(Long shopId,Long id,String name)
  {
    if(StringUtils.isBlank(name))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      OtherIncomeKind otherIncomeKind = writer.getOtherIncomeKindById(shopId,id);
      OtherIncomeKindDTO otherIncomeKindDTO= null;
      if(null != otherIncomeKind)
      {
        otherIncomeKind.setStatus(KindStatus.ENABLE);
        otherIncomeKind.setKindName(name);
        writer.update(otherIncomeKind);
        otherIncomeKindDTO = otherIncomeKind.toDTO();
      }
      writer.commit(status);
      return otherIncomeKindDTO;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public OtherIncomeKindDTO saveOrUpdateOtherIncomeKind(Long shopId,String name)
  {
    if(StringUtils.isBlank(name))
    {
      return null;
    }

    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      List<OtherIncomeKind> otherIncomeKindList =  writer.getOtherIncomeKindByName(shopId,name);
      OtherIncomeKindDTO otherIncomeKindDTO = null;
      if(CollectionUtils.isEmpty(otherIncomeKindList))
      {
        OtherIncomeKind otherIncomeKind = new OtherIncomeKind();
        otherIncomeKind.setShopId(shopId);
        otherIncomeKind.setKindName(name);
        otherIncomeKind.setStatus(KindStatus.ENABLE);
        writer.save(otherIncomeKind);
        otherIncomeKindDTO = otherIncomeKind.toDTO();
      }
      else
      {
        OtherIncomeKind otherIncomeKind= otherIncomeKindList.get(0);

        if(KindStatus.DISABLED.equals(otherIncomeKind.getStatus()))
        {
          otherIncomeKind.setStatus(KindStatus.ENABLE);
          writer.update(otherIncomeKind);
        }

        otherIncomeKindDTO = otherIncomeKind.toDTO();
      }
      writer.commit(status);
      return otherIncomeKindDTO;

    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<String, OtherIncomeKindDTO> batchSaveOrUpdateOtherIncomeKind(Long shopId, Set<String> kindNames) {
    if (shopId == null || CollectionUtils.isEmpty(
      kindNames)) {
      return new HashMap<String, OtherIncomeKindDTO>();
    }
    Map<String, OtherIncomeKindDTO> otherIncomeKindDTOMap = new HashMap<String, OtherIncomeKindDTO>((int) (kindNames.size() / 0.75f) + 1);
    Map<String, OtherIncomeKind> otherIncomeKindMap = new HashMap<String, OtherIncomeKind>((int) (kindNames.size() / 0.75f) + 1);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<OtherIncomeKind> otherIncomeKindList = writer.getOtherIncomeKindByNames(shopId, kindNames);
      if (CollectionUtils.isNotEmpty(otherIncomeKindList)) {
        for (OtherIncomeKind otherIncomeKind : otherIncomeKindList) {
          otherIncomeKindDTOMap.put(otherIncomeKind.getKindName(), otherIncomeKind.toDTO());
          otherIncomeKindMap.put(otherIncomeKind.getKindName(), otherIncomeKind);
        }
      }
      for (String kindName : kindNames) {
        OtherIncomeKind otherIncomeKind = otherIncomeKindMap.get(kindName);
        if (null != otherIncomeKind && KindStatus.ENABLE == otherIncomeKind.getStatus()) {
          otherIncomeKind.setUseTimes(NumberUtil.longValue(otherIncomeKind.getUseTimes()) + 1);
          otherIncomeKind.setStatus(KindStatus.ENABLE);
          writer.update(otherIncomeKind);
          otherIncomeKindDTOMap.put(kindName, otherIncomeKind.toDTO());
        } else if (otherIncomeKind == null) {
          otherIncomeKind = new OtherIncomeKind();
          otherIncomeKind.setShopId(shopId);
          otherIncomeKind.setKindName(kindName);
          otherIncomeKind.setStatus(KindStatus.ENABLE);
          otherIncomeKind.setUseTimes(NumberUtil.longValue(otherIncomeKind.getUseTimes()) + 1);
          writer.save(otherIncomeKind);
          otherIncomeKindMap.put(kindName, otherIncomeKind);
          otherIncomeKindDTOMap.put(kindName, otherIncomeKind.toDTO());
        }
      }
      writer.commit(status);
      return otherIncomeKindDTOMap;
    } finally {
      writer.rollback(status);
    }
  }

  public List<SalesOrderOtherIncomeItemDTO> getSalesOrderOtherIncomeItemDTOs(Long... orderIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SalesOrderOtherIncomeItem> otherIncomeItemList = writer.getSalesOrderOtherIncomeItems(orderIds);
    List<SalesOrderOtherIncomeItemDTO> otherIncomeItemDTOList = new ArrayList<SalesOrderOtherIncomeItemDTO>();
    if (CollectionUtils.isNotEmpty(otherIncomeItemList)) {
      for (SalesOrderOtherIncomeItem item : otherIncomeItemList) {
        if (item == null) {
          continue;
        }
        otherIncomeItemDTOList.add(item.toDTO());
      }
    }
    return otherIncomeItemDTOList;
  }

  @Override
  public Map<Long, List<RepairOrderOtherIncomeItemDTO>> getRepairOrderOtherIncomeItemDTOByShopIdAndArrayOrderId(Long shopId, Long... orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairOrderOtherIncomeItem> otherIncomeItemList = writer.getRepairOrderOtherIncomeItemDTOByShopIdAndArrayOrderId(shopId, orderId);

    Map<Long, List<RepairOrderOtherIncomeItemDTO>> otherIncomeItemDTOListMap = new HashMap<Long, List<RepairOrderOtherIncomeItemDTO>>();
    if (CollectionUtils.isNotEmpty(otherIncomeItemList)) {

      List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList = null;

      RepairOrderOtherIncomeItemDTO otherIncomeItemDTO =null;
      for (RepairOrderOtherIncomeItem otherIncomeItem : otherIncomeItemList) {

        otherIncomeItemDTO = otherIncomeItem.toDTO();

        if (otherIncomeItemDTOListMap.get(otherIncomeItem.getOrderId()) == null) {
          otherIncomeItemDTOList = new ArrayList<RepairOrderOtherIncomeItemDTO>();
          otherIncomeItemDTOList.add(otherIncomeItemDTO);
          otherIncomeItemDTOListMap.put(otherIncomeItem.getOrderId(), otherIncomeItemDTOList);
        } else {
          otherIncomeItemDTOListMap.get(otherIncomeItem.getOrderId()).add(otherIncomeItemDTO);
        }
      }
    }
    return otherIncomeItemDTOListMap;
  }

  public List<PurchaseOrder> getPurchaseOrderByShopIdAndSupplierId(SupplierDTO supplierDTO){
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getPurchaseOrderByShopIdAndSupplierId(supplierDTO.getShopId(),supplierDTO.getId());
  }

  public List<Object[]> getSupplierHistoryOrderList(Long supplierId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes, Pager pager) {
    List<String> orderTypeList = new ArrayList<String>();
    if(supplierId == null || shopId == null){
      return new ArrayList<Object[]>();
    }
    for(OrderTypes ot:orderTypes){
      orderTypeList.add(ot.toString());
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> idList = writer.getSupplierHistoryOrderList(supplierId, shopId, startTime, endTime, orderTypeList, pager);
    return idList;
  }

  public Double getSupplierTotalMoneyByTimeRangeAndOrderType(Long shopId, Long supplierId, Long startTime, Long endTime, String orderType){
    if(shopId == null || supplierId == null){
      return 0d;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Double totalMoney = writer.getSupplierTotalMoneyByTimeRangeAndOrderType(shopId, supplierId, startTime, endTime, orderType);
    return totalMoney;
  }
  public IProductHistoryService getProductHistoryService() {
    return ServiceManager.getService(IProductHistoryService.class);
  }

  public IPrivilegeService getPrivilegeService() {
    return ServiceManager.getService(IPrivilegeService.class);
  }

  public IServiceHistoryService getServiceHistoryService() {
    return ServiceManager.getService(IServiceHistoryService.class);
  }

  public InventorySearchIndex createInventorySearchIndex(Inventory inventory, Long parentProductId) throws Exception {
    ProductDTO productDTO = getProductService().getProductById(parentProductId, inventory.getShopId());
    return createInventorySearchIndex(inventory, productDTO);
  }


  public InventorySearchIndex createInventorySearchIndex(Inventory inventory, ProductDTO productDTO) throws Exception {
    productDTO.setRecommendedPrice(inventory.getSalesPrice());
    return createInventorySearchIndex(inventory.getAmount(), inventory.getId(), productDTO);
  }

  private InventorySearchIndex createInventorySearchIndex(double amount, Long localProductId, ProductDTO productDTO) throws Exception {
    ISearchService iSearchService = ServiceManager.getService(ISearchService.class);
    InventorySearchIndex inventorySearchIndex;
    inventorySearchIndex = iSearchService.getInventorySearchIndexByProductId(localProductId);
    if (inventorySearchIndex == null) {
      inventorySearchIndex = new InventorySearchIndex();
    }
    inventorySearchIndex.setAmount(amount);
    inventorySearchIndex.setBrand(productDTO.getProductVehicleBrand());
    inventorySearchIndex.setEditDate(productDTO.getEditDate());
    inventorySearchIndex.setEngine(productDTO.getProductVehicleEngine());
    inventorySearchIndex.setModel(productDTO.getProductVehicleModel());
    inventorySearchIndex.setProductBrand(productDTO.getBrand());
    inventorySearchIndex.setProductId(localProductId);
    inventorySearchIndex.setProductModel(productDTO.getModel());
    inventorySearchIndex.setProductName(productDTO.getName());
    inventorySearchIndex.setProductSpec(productDTO.getSpec());
    inventorySearchIndex.setProductVehicleStatus(productDTO.getProductVehicleStatus());
    inventorySearchIndex.setShopId(productDTO.getShopId());
    inventorySearchIndex.setYear(productDTO.getProductVehicleYear());
    inventorySearchIndex.setParentProductId(productDTO.getId());
    inventorySearchIndex.setUnit(productDTO.getSellUnit());
    inventorySearchIndex.setCommodityCode(productDTO.getCommodityCode());
    inventorySearchIndex.setRecommendedPrice(productDTO.getRecommendedPrice());
    inventorySearchIndex.setEditDate(DateUtil.convertDateDateShortToDateLong(DateUtil.YEAR_MONTH_DATE, new Date()));
    return inventorySearchIndex;
  }

  public Inventory saveInventoryAfterSaveNewProduct(Long shopId, Long productLocalInfoId, double amount, TxnWriter writer, String sellUnit) {
    Inventory inventory = new Inventory();
    inventory.setId(productLocalInfoId);
    inventory.setShopId(shopId);
    inventory.setAmount(amount);
    inventory.setUnit(sellUnit);
    writer.save(inventory);
    return inventory;
  }

  @Override
  public List<ReceivableDTO> getMemberCardConsumeByMemberId(Long memberId) {
    List<ReceivableDTO> receivableDTOList = new ArrayList<ReceivableDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<Receivable> receivableList = writer.getMemberCardConsumeByMemberId(memberId);
    for(Receivable receivable : receivableList){
      receivableDTOList.add(receivable.toDTO());
    }
    return receivableDTOList;
  }

  public List<PurchaseInventoryItemDTO> getPurchaseInventoryItemByProductIdVestDate(Long shopId,Long productId,Long vestDate) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getPurchaseInventoryItemByProductIdVestDate(shopId, productId,vestDate);
  }
  public void saveNormalProductStatList(Collection<NormalProductInventoryStatDTO> normalProductInventoryStatDTOList) {
    if (CollectionUtils.isEmpty(normalProductInventoryStatDTOList)) {
      return;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      for (NormalProductInventoryStatDTO normalProductInventoryStatDTO : normalProductInventoryStatDTOList) {
        NormalProductInventoryStat normalProductInventoryStat = new NormalProductInventoryStat(normalProductInventoryStatDTO);
        txnWriter.save(normalProductInventoryStat);
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  public List<Long> countStatDateByNormalProductIds(Long[] shopIds,Long[] normalProductIds,NormalProductStatType normalProductStatType) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.countStatDateByNormalProductIds(shopIds, normalProductIds, normalProductStatType);
  }
  public List<NormalProductInventoryStat> getStatDateByNormalProductIds(Long[] shopIds,Long[] normalProductIds,NormalProductStatType normalProductStatType) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getStatDateByNormalProductIds(shopIds, normalProductIds, normalProductStatType);
  }

  public void deleteAllNormalProductStat() {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      txnWriter.deleteAllNormalProductStat();
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  @Override
  public void moveSupplierReturnPayableToPayable(List<Long> ids,Long... orderIds)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      List<SupplierReturnPayable> supplierReturnPayableList = writer.getSupplierReturnPayableByIds(ids);
      Map<Long,PurchaseReturnDTO> map = writer.getPurchaseReturnByPurchaseReturnId(orderIds);
      for(SupplierReturnPayable supplierReturnPayable : supplierReturnPayableList)
      {
        Payable payable = new Payable(supplierReturnPayable);
        if(null != payable)
        {
          PurchaseReturnDTO purchaseReturnDTO = map.get(supplierReturnPayable.getPurchaseReturnId());
          if(null != purchaseReturnDTO)
          {
            payable.setReceiptNo(purchaseReturnDTO.getReceiptNo());
            payable.setPayTime(purchaseReturnDTO.getVestDate());
          }
          writer.save(payable);
        }
      }

      writer.updateMovedSupplierReturnPayable(ids);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public QualifiedCredentialsDTO getQualifiedCredentialsDTO(Long shopId,Long orderId)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    QualifiedCredentials qualifiedCredentials = writer.getQualifiedCredentialsDTO(shopId,orderId);

    if(null == qualifiedCredentials)
    {
      return null;
    }

    return qualifiedCredentials.toDTO();
  }

  @Override
  public RepairOrderDTO getRepairOrder(Long shopId,Long id)
  {
    TxnWriter writer = txnDaoManager.getWriter();
    RepairOrder repairOrder = writer.getRepairOrderById(id,shopId);
    if (repairOrder != null) {
      RepairOrderDTO repairOrderDTO = repairOrder.toDTO();
      List<RepairOrderItem> items = writer.getRepairOrderItemsByOrderId(id);
      RepairOrderItemDTO[] itemDTOs = new RepairOrderItemDTO[items.size()];
      repairOrderDTO.setItemDTOs(itemDTOs);
      for (int i = 0; i < items.size(); i++) {
        RepairOrderItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
      }

      List<RepairOrderService> services = writer.getRepairOrderServicesByOrderId(id);
      RepairOrderServiceDTO[] serviceDTOs = new RepairOrderServiceDTO[services.size()];
      repairOrderDTO.setServiceDTOs(serviceDTOs);
      for (int i = 0; i < services.size(); i++) {
        RepairOrderService service = services.get(i);
        serviceDTOs[i] = new RepairOrderServiceDTO();
        serviceDTOs[i] = service.toDTO();
      }
      return repairOrderDTO;
    }
    return null;
  }

  public void saveOrUpdateQualifiedCredentials(QualifiedCredentialsDTO qualifiedCredentialsDTO)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();

    try{
      QualifiedCredentials qualifiedCredentials = writer.getQualifiedCredentialsDTO(qualifiedCredentialsDTO.getShopId(),qualifiedCredentialsDTO.getOrderId());

      if(null == qualifiedCredentials)
      {
        qualifiedCredentials = new QualifiedCredentials(qualifiedCredentialsDTO);
        writer.save(qualifiedCredentials);
      }
      else
      {
        qualifiedCredentials.fromDTO(qualifiedCredentialsDTO,false);
        writer.update(qualifiedCredentials);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<Long,PurchaseReturnDTO> getPurchaseReturnByPurchaseReturnId(Long... id)
  {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseReturnByPurchaseReturnId(id);
  }

  @Override
  public int getInventoryCheckCount(InventoryCheckDTO inventoryCheckIndex){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInventoryCheckCount(inventoryCheckIndex);
  }

  @Override
  public List<InventoryCheck> getInventoryChecks(InventoryCheckDTO inventoryCheckIndex){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInventoryChecks(inventoryCheckIndex);
  }

  @Override
  public List<InventoryCheck> getInventoryCheckByIds(Long shopId,Set<Long> orderIds,Pager pager){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInventoryCheckByIds(shopId,orderIds,pager);
  }

  @Override
  public Map<Long,List<InventoryCheckItemDTO>> getInventoryCheckItemByProductIds(Long shopId,Pager pager,Long... productIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventoryCheckItem> inventoryCheckItemList = writer.getInventoryCheckItemByProductIds(shopId,pager,productIds);
    Map<Long,List<InventoryCheckItemDTO>> itemDTOMap = new HashMap<Long,List<InventoryCheckItemDTO>>();
    if (CollectionUtils.isNotEmpty(inventoryCheckItemList)) {
      List<InventoryCheckItemDTO> itemDTOs=null;
      for (InventoryCheckItem item : inventoryCheckItemList) {
        if (item == null) {
          continue;
        }
        itemDTOs=itemDTOMap.get(item.getProductId());
        if(itemDTOs==null){
          itemDTOs=new ArrayList<InventoryCheckItemDTO>();
          itemDTOs.add(item.toDTO());
          itemDTOMap.put(item.getProductId(),itemDTOs);
        }else {
          itemDTOs.add(item.toDTO());
        }
      }
    }
    return itemDTOMap;
  }

  @Override
  public int getInventoryCheckItemCountByProductIds(Long shopId,Long... productIds){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getInventoryCheckItemCountByProductIds(shopId,productIds);
  }

  @Override
  public Double getStockAdjustPriceTotal(Long shopId){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getStockAdjustPriceTotal(shopId);
  }



  @Override
  public List<StatementAccountOrderDTO> getStatementAccountOrderByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getStatementAccountOrderByShopIdAndOrderIds(shopId, orderIds);
  }


  @Override
  public List<RepairRemindEvent> getAllRepairRemindEvent(){
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairRemindEvent> repairRemindEventList = writer.getAllRepairRemindEvent();
    return repairRemindEventList;
  }

  @Override
  public List<Debt> getAllDebt(){
    TxnWriter writer = txnDaoManager.getWriter();
    List<Debt> debtList = writer.getAllDebt();
    return debtList;
  }

  @Override
  public List<InventoryRemindEvent> getAllInventoryRemindEvent(){
    TxnWriter writer = txnDaoManager.getWriter();
    List<InventoryRemindEvent> inventoryRemindEventList = writer.getAllInventoryRemindEvent();
    return inventoryRemindEventList;
  }

  @Override
  public List<RemindEventDTO> getWXRemindEvent(Long startTime,Long endTime){
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEventDTO> remindEventDTOs = writer.getWXRemindEvent(startTime,endTime);
    return remindEventDTOs;
  }

  @Override
  public RemindEventDTO getRemindEventById(Long id) {
    RemindEventDTO remindEventDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    RemindEvent remindEvent = writer.getById(RemindEvent.class, id);
    if(remindEvent!=null){
      remindEventDTO = remindEvent.toDTO();
    }
    return remindEventDTO;
  }

  @Override
  public List<RemindEventDTO> getRemindEventByOrderId(RemindEventType type,Long shopId, Long orderId) {
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEvent> remindEventList =  writer.getRemindEventByOrderId(type,shopId, orderId);
    if(CollectionUtil.isNotEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
      }
    }
    return remindEventDTOList;
  }

  @Override
  public List<RemindEventDTO> getRemindEventListByCustomerIdAndType(RemindEventType type, Long customerId){
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEvent> remindEventList =  writer.getRemindEventListByCustomerIdAndType(type, customerId);
    if(CollectionUtil.isNotEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
      }
    }
    return remindEventDTOList;
  }

  @Override
  public List<RemindEventDTO> getRemindEventListBySupplierIdAndType(RemindEventType type, Long supplierId){
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEvent> remindEventList =  writer.getRemindEventListBySupplierIdAndType(type, supplierId);
    if(CollectionUtil.isNotEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
      }
    }
    return remindEventDTOList;
  }

  @Override
  public List<RemindEventDTO> getRemindEventListByOrderIdAndObjectIdAndEventStatus(RemindEventType type, Long orderId, Long objectId, String eventStatus){
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEvent> remindEventList = writer.getRemindEventListByOrderIdAndObjectIdAndEventStatus(type, orderId, objectId, eventStatus);
    if(CollectionUtil.isNotEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
      }
    }
    return remindEventDTOList;
  }

  @Override
  public RemindEventDTO getRemindEventByOldRemindEventId(RemindEventType type,Long shopId, Long oldRemindEventId){
    RemindEventDTO remindEventDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEvent> remindEventList =  writer.getRemindEventByOldRemindEventId(type,shopId, oldRemindEventId);
    if(CollectionUtil.isNotEmpty(remindEventList)){
      remindEventDTO = remindEventList.get(0).toDTO();
    }
    return remindEventDTO;
  }

  @Override
  public void updateRemindEvent(RemindEventDTO remindEventDTO) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:TxnService:updateRemindEvent");
    TxnWriter writer = txnDaoManager.getWriter();
    RemindEvent remindEvent = writer.getById(RemindEvent.class, remindEventDTO.getId());
    if(remindEvent!=null){
      Object status = writer.begin();
      try{
        remindEvent.fromDTO(remindEventDTO);
        writer.update(remindEvent);
        writer.commit(status);
      }finally {
        LOG.debug("AOP_SQL end:TxnService:updateRemindEvent 用时：{}ms",System.currentTimeMillis() - begin);
        writer.rollback(status);
      }
    }
  }

  @Override
  public void cancelCustomerRemindEventById(Long remindEventId) {
    TxnWriter writer = txnDaoManager.getWriter();
    RemindEvent remindEvent = writer.getById(RemindEvent.class, remindEventId);
    if(remindEvent!=null){
      Object status = writer.begin();
      try{
        remindEvent.setRemindStatus(UserConstant.Status.CANCELED);
        writer.update(remindEvent);
        writer.commit(status);
      }finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public boolean cancelRemindEventByOrderId(RemindEventType type, Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      boolean result = writer.cancelRemindEventByOrderId(type, orderId);
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean cancelRemindEventByOrderIdAndStatus(RemindEventType type, Long orderId, RepairRemindEventTypes eventStatus) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      boolean result = writer.cancelRemindEventByOrderIdAndStatus(type, orderId, eventStatus);
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean cancelRemindEventByOrderTypeAndOrderId(TxnWriter writer, RemindEventType type, OrderTypes orderType, Long orderId){
    boolean result = writer.cancelRemindEventByOrderTypeAndOrderId(type, orderType, orderId);
    return result;
  }

  @Override
  public boolean cancelRemindEventByOrderIdAndObjectId(RemindEventType remindType, String eventStatus, Long orderId, Long objectId){
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      boolean result = writer.cancelRemindEventByOrderIdAndObjectId(remindType, eventStatus, orderId, objectId);
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean cancelRemindEventByOldRemindEventId(RemindEventType type, Long oldRemindEventId, TxnWriter writer){
    return writer.cancelRemindEventByOldRemindEventId(type, oldRemindEventId);
  }

  @Override
  public void saveRemindEvent(RepairRemindEvent repairRemindEvent, RepairOrderDTO repairOrderDTO, TxnWriter writer) {
    RemindEvent remindEvent = new RemindEvent();
    remindEvent.setShopId(repairRemindEvent.getShopId());
    remindEvent.setOrderId(repairRemindEvent.getRepairOrderId());
    remindEvent.setOrderType(OrderTypes.REPAIR.toString());
    remindEvent.setEventType(RemindEventType.REPAIR.toString());
    remindEvent.setEventStatus(repairRemindEvent.getEventTypeEnum().toString());
    remindEvent.setObjectId(repairRemindEvent.getProductId());  //商品ID
    remindEvent.setRemindTime(repairRemindEvent.getFinishTime());
    remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
    remindEvent.setCustomerName(repairRemindEvent.getCustomer());
    remindEvent.setMobile(repairRemindEvent.getMobile());
    remindEvent.setLicenceNo(repairRemindEvent.getVehicle());
    remindEvent.setServices(repairRemindEvent.getService());
    remindEvent.setOldRemindEventId(repairRemindEvent.getId());
    if(repairOrderDTO!=null){
      remindEvent.setCustomerId(repairOrderDTO.getCustomerId());
    }
    writer.updateDebtRemindDeletedType(remindEvent.getShopId(),remindEvent.getCustomerId(),"customer",DeletedType.FALSE);
    writer.saveOrUpdate(remindEvent);
  }

  @Override
  public void saveRemindEvent(TxnWriter writer, Debt debt, String customerName, String mobile) {
    RemindEvent remindEvent = new RemindEvent();
    remindEvent.setShopId(debt.getShopId());
    remindEvent.setOrderId(debt.getOrderId());
    remindEvent.setCustomerId(debt.getCustomerId());
    remindEvent.setCustomerName(customerName);
    remindEvent.setMobile(mobile);
    remindEvent.setOrderType(debt.getOrderTypeEnum().toString());
    remindEvent.setEventType(RemindEventType.DEBT.toString());
    remindEvent.setRemindTime(debt.getRemindTime());
    remindEvent.setRemindStatus(debt.getRemindStatus());
    remindEvent.setObjectId(debt.getId());
    remindEvent.setDebt(debt.getDebt());
    remindEvent.setOldRemindEventId(debt.getId());
    writer.saveOrUpdate(remindEvent);
  }

  @Override
  //新增对账单欠款提醒
  public void saveRemindEvent(TxnWriter txnWriter, StatementAccountOrderDTO statementAccountOrderDTO){
    RemindEvent remindEvent = new RemindEvent();
    remindEvent.setShopId(statementAccountOrderDTO.getShopId());
    remindEvent.setOrderId(statementAccountOrderDTO.getId());
    if(OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.equals(statementAccountOrderDTO.getOrderType())) {
      remindEvent.setCustomerId(statementAccountOrderDTO.getCustomerOrSupplierId());
      remindEvent.setCustomerName(statementAccountOrderDTO.getCustomerOrSupplier());
    } else if(OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.equals(statementAccountOrderDTO.getOrderType())){
      remindEvent.setSupplierId(statementAccountOrderDTO.getCustomerOrSupplierId());
      remindEvent.setSupplierName(statementAccountOrderDTO.getCustomerOrSupplier());
    }

    remindEvent.setMobile(statementAccountOrderDTO.getMobile());
    remindEvent.setOrderType(statementAccountOrderDTO.getOrderType().toString());
    remindEvent.setEventType(RemindEventType.DEBT.toString());
    remindEvent.setRemindTime(statementAccountOrderDTO.getPaymentTime());
    remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
    remindEvent.setDebt(statementAccountOrderDTO.getDebt());
    remindEvent.setOldRemindEventId(statementAccountOrderDTO.getId());
    txnWriter.saveOrUpdate(remindEvent);
  }

  @Override
  public void saveRemindEvent(InventoryRemindEvent inventoryRemindEvent) {
    //先根据采购单ID判断是否已有该单据的记录，不做重复保存
    List<RemindEventDTO> remindEventDTOList = getRemindEventByOrderId(RemindEventType.TXN,inventoryRemindEvent.getShopId(), inventoryRemindEvent.getPurchaseOrderId());
    if(CollectionUtil.isEmpty(remindEventDTOList)){
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try{
        PurchaseOrder purchaseOrder = writer.getById(PurchaseOrder.class, inventoryRemindEvent.getPurchaseOrderId());
        SupplierDTO supplierDTO = ServiceManager.getService(ISupplierService.class).getSupplierById(purchaseOrder.getSupplierId(),inventoryRemindEvent.getShopId());
        RemindEvent remindEvent = new RemindEvent();
        remindEvent.setShopId(inventoryRemindEvent.getShopId());
        remindEvent.setSupplierId(purchaseOrder.getSupplierId());
        remindEvent.setSupplierName(supplierDTO.getName());
        remindEvent.setMobile(supplierDTO.getMobile());
        remindEvent.setOrderId(inventoryRemindEvent.getPurchaseOrderId());
        remindEvent.setOrderType(OrderTypes.PURCHASE.toString());
        remindEvent.setEventType(RemindEventType.TXN.toString());
        remindEvent.setRemindTime(inventoryRemindEvent.getDeliverTime());
        remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
        remindEvent.setOldRemindEventId(inventoryRemindEvent.getId());
        writer.saveOrUpdate(remindEvent);
        writer.commit(status);
      }finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void saveRemindEvent(CustomerServiceJob customerServiceJob, String customerName, String mobile, String licenceNo) {
    if(customerServiceJob.getId()!=null){
      RemindEventDTO remindEventDTO = getRemindEventByOldRemindEventId(RemindEventType.CUSTOMER_SERVICE, customerServiceJob.getShopId(), customerServiceJob.getId());
      if(remindEventDTO==null){
        TxnWriter writer = txnDaoManager.getWriter();
        Object status = writer.begin();
        try{
          RemindEvent remindEvent = new RemindEvent();
          remindEvent.setShopId(customerServiceJob.getShopId());
          remindEvent.setEventType(RemindEventType.CUSTOMER_SERVICE.toString());
          remindEvent.setEventStatus(UserConstant.getCustomerRemindType(customerServiceJob.getRemindType()));
          remindEvent.setRemindTime(customerServiceJob.getRemindTime());
          remindEvent.setRemindStatus(customerServiceJob.getStatus());
          remindEvent.setCustomerId(customerServiceJob.getCustomerId());
          remindEvent.setCustomerName(customerName);
          remindEvent.setMobile(mobile);
          remindEvent.setAppointServiceId(customerServiceJob.getAppointServiceId());
          remindEvent.setLicenceNo(licenceNo);
          remindEvent.setOldRemindEventId(customerServiceJob.getId());
          remindEvent.setRemindMileage(customerServiceJob.getRemindMileage());
          writer.saveOrUpdate(remindEvent);
          writer.commit(status);
        }finally {
          writer.rollback(status);
        }
      }else{
        //customerServiceJob 对应的remindEvent 做更新逻辑，当remindEvent是已经提醒并且提醒内容没有修改的不更新提醒状态
        boolean isRemindNeedToUpdate = false;
        if(UserConstant.MAINTAIN_MILEAGE.equals(customerServiceJob.getRemindType())){
          if(NumberUtil.longValue(customerServiceJob.getRemindMileage())  !=  NumberUtil.longValue(remindEventDTO.getRemindMileage())){
            isRemindNeedToUpdate = true;
          }
        } else {
          if (remindEventDTO.getRemindStatus() != customerServiceJob.getStatus() || NumberUtil.longValue(customerServiceJob.getRemindTime()) != NumberUtil.longValue(remindEventDTO.getRemindTime())) {
            isRemindNeedToUpdate = true;
          }
        }
        if (isRemindNeedToUpdate) {
          remindEventDTO.setRemindTime(customerServiceJob.getRemindTime());
          remindEventDTO.setRemindStatus(customerServiceJob.getStatus());
          remindEventDTO.setCustomerId(customerServiceJob.getCustomerId());
          remindEventDTO.setCustomerName(customerName);
          remindEventDTO.setMobile(mobile);
          remindEventDTO.setAppointServiceId(customerServiceJob.getAppointServiceId());
          remindEventDTO.setLicenceNo(licenceNo);
          remindEventDTO.setOldRemindEventId(customerServiceJob.getId());
          remindEventDTO.setRemindMileage(customerServiceJob.getRemindMileage());
          updateRemindEvent(remindEventDTO);
        }
      }
    }
  }

  @Override
  public void saveRemindEvent(MemberService memberService, Long shopId, Long customerId, String customerName, String mobile){
    if(memberService.getId()!=null){
      RemindEventDTO remindEventDTO = getRemindEventByOldRemindEventId(RemindEventType.MEMBER_SERVICE,shopId, memberService.getId());
      if(remindEventDTO==null){
        TxnWriter writer = txnDaoManager.getWriter();
        Object status = writer.begin();
        try{
          RemindEvent remindEvent = new RemindEvent();
          remindEvent.setShopId(shopId);
          remindEvent.setEventType(RemindEventType.MEMBER_SERVICE.toString());
          remindEvent.setEventStatus(UserConstant.CustomerRemindType.MEMBER_SERVICE);
          remindEvent.setRemindTime(memberService.getDeadline());
          remindEvent.setRemindStatus(memberService.getRemindStatus());
          remindEvent.setCustomerId(customerId);
          remindEvent.setCustomerName(customerName);
          remindEvent.setMobile(mobile);
          remindEvent.setServiceId(memberService.getServiceId());
          remindEvent.setOldRemindEventId(memberService.getId());
          writer.saveOrUpdate(remindEvent);
          writer.commit(status);
        }finally {
          writer.rollback(status);
        }
      }else{
        remindEventDTO.setRemindTime(memberService.getDeadline());
        remindEventDTO.setRemindStatus(memberService.getRemindStatus());
        remindEventDTO.setCustomerId(customerId);
        remindEventDTO.setCustomerName(customerName);
        remindEventDTO.setMobile(mobile);
        remindEventDTO.setServiceId(memberService.getServiceId());
        updateRemindEvent(remindEventDTO);
      }
    }
  }

  /**
   * 微信是否提醒过标志
   * @param remindEventId
   * @param wxRemindStatus
   */
  @Override
  public void updateRemindEventWXRemindStatus(Long remindEventId, String wxRemindStatus){
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      RemindEvent remindEvent= writer.getById(RemindEvent.class,remindEventId);
      remindEvent.setWxRemindStatus(wxRemindStatus);
      writer.update(remindEvent);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }

  }

  @Override
  public void saveRemindEvent(SupplierDTO supplierDTO,PurchaseReturnDTO purchaseReturnDTO,Long payableId,TxnWriter writer) {
    RemindEvent remindEvent = new RemindEvent();
    remindEvent.setShopId(purchaseReturnDTO.getShopId());
    remindEvent.setOrderId(purchaseReturnDTO.getId());
    //如果既是客户又是供应商，则存客户的ID，如果只是供应商，则存供应商的ID(待办事项欠费提醒中，当既是客户又是供应商时，只显示客户的信息)
    if(supplierDTO.getCustomerId() != null) {
      remindEvent.setCustomerId(supplierDTO.getCustomerId());
    } else {
      remindEvent.setSupplierId(supplierDTO.getId());
    }
    remindEvent.setSupplierName(supplierDTO.getName());
    remindEvent.setMobile(supplierDTO.getMobile());
    remindEvent.setOrderType(OrderTypes.RETURN.toString());
    remindEvent.setEventType(RemindEventType.DEBT.toString());
    remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
    try {
      if(StringUtils.isNotBlank(purchaseReturnDTO.getHuankuanTime())) {
        remindEvent.setRemindTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY,purchaseReturnDTO.getHuankuanTime()));
      }
    } catch(Exception e) {
      LOG.error(e.getMessage(),e);
    }

    remindEvent.setDebt(purchaseReturnDTO.getAccountDebtAmount());
    remindEvent.setOldRemindEventId(payableId);
    writer.saveOrUpdate(remindEvent);
  }

  @Override
  public void saveRemindEvent(SupplierDTO supplierDTO,List<PayableDTO> payableDTOs) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if(CollectionUtils.isEmpty(payableDTOs)) {
        return;
      }
      for(PayableDTO payableDTO :payableDTOs) {
        RemindEvent remindEvent = new RemindEvent();
        remindEvent.setShopId(payableDTO.getShopId());
        remindEvent.setOrderId(payableDTO.getPurchaseInventoryId());
        remindEvent.setCustomerId(supplierDTO.getCustomerId());
        remindEvent.setCustomerName(supplierDTO.getName());
        remindEvent.setMobile(supplierDTO.getMobile());
        remindEvent.setOrderType(OrderTypes.RETURN.toString());
        remindEvent.setEventType(RemindEventType.DEBT.toString());
        remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
        remindEvent.setDebt(-payableDTO.getCreditAmount());
        remindEvent.setOldRemindEventId(payableDTO.getId());
        writer.saveOrUpdate(remindEvent);
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void saveRemindEvent(List<Payable> payables) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for(Payable payable : payables) {
        if(payable.getSupplierId() == null) continue;
        SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(payable.getSupplierId());
        if(supplierDTO == null) continue;
        RemindEvent remindEvent = new RemindEvent();
        remindEvent.setShopId(payable.getShopId());
        remindEvent.setOrderId(payable.getPurchaseInventoryId());
        remindEvent.setSupplierId(supplierDTO.getId());
        remindEvent.setSupplierName(supplierDTO.getName());
        remindEvent.setMobile(supplierDTO.getMobile());
        remindEvent.setOrderType(payable.getOrderType().toString());
        remindEvent.setEventType(RemindEventType.DEBT.toString());
        remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
        remindEvent.setDebt(Math.abs(payable.getCreditAmount()));
        remindEvent.setOldRemindEventId(payable.getId());
        writer.saveOrUpdate(remindEvent);
      }
      writer.commit(status);
    }   finally {
      writer.rollback(status);
    }

  }

  //合并客户提醒
  public void mergerCustmerRemindEvent(Long parentId, Long[] childIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      writer.mergerCustmerRemindEvent(parentId, childIds);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean updateCustomerBirthdayRemindEvent(Long customerId, Long newBirthday){
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      boolean result = writer.updateCustomerBirthdayRemindEvent(customerId, newBirthday);
      writer.commit(status);
      return result;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public int getRemindEventAmountByType(Long shopId, RemindEventType type, List<Long> customerOrSupplierIdList, Long flashTime) {
    int result = 0;
    RemindEventStrategySelector remindEventStrategySelector = new RemindEventStrategySelector();
    //维修美容提醒
    if(type.equals(RemindEventType.REPAIR)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.REPAIR_REMIND_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = remindEventStrategySelector.selectStrategy(RemindEventType.REPAIR).countRemindEvent(shopId,null,null,flashTime);
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.REPAIR_REMIND_AMOUNT + shopId, result);
      }
    }
    //欠款提醒
    else if(type.equals(RemindEventType.DEBT)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.DEBT_REMIND_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = remindEventStrategySelector.selectStrategy(RemindEventType.DEBT).countRemindEvent(shopId,null,null,flashTime);
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.DEBT_REMIND_AMOUNT + shopId, result);
      }
    }
    //进销存提醒
    else if(type.equals(RemindEventType.TXN)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TXN_REMIND_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = remindEventStrategySelector.selectStrategy(RemindEventType.TXN).countRemindEvent(shopId,null,null,flashTime);
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TXN_REMIND_AMOUNT + shopId, result);
      }
    }
    //客服提醒
    else if(type.equals(RemindEventType.CUSTOMER_SERVICE)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.CUSTOMER_REMIND_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = remindEventStrategySelector.selectStrategy(RemindEventType.CUSTOMER_SERVICE).countRemindEvent(shopId,null,null,flashTime);
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.CUSTOMER_REMIND_AMOUNT + shopId, result);
      }
    }
    //待办销售单
    else if(type.equals(RemindEventType.TODO_SALE_ORDER)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_SALE_ORDER_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = getTodoSalesOrderCount(shopId,null,null,customerOrSupplierIdList,null,"allTodo").intValue();
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_SALE_ORDER_AMOUNT + shopId, result);
      }
    }
    //待办销售退货单
    else if(type.equals(RemindEventType.TODO_SALE_RETURN_ORDER)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_SALE_RETURN_ORDER_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = getTodoSalesReturnOrderCount(shopId,null,null,customerOrSupplierIdList,null,"allTodo").intValue();
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_SALE_RETURN_ORDER_AMOUNT + shopId, result);
      }
    }
    //待办采购单
    else if(type.equals(RemindEventType.TODO_PURCHASE_ORDER)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_PURCHASE_ORDER_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = getTodoPurchaseOrderCount(shopId, null, null, customerOrSupplierIdList, null, "allTodo", "created").intValue();
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_PURCHASE_ORDER_AMOUNT + shopId, result);
      }
    }
    //待办入库退货单
    else if(type.equals(RemindEventType.TODO_PURCHASE_RETURN_ORDER)){
      Integer value = (Integer)MemCacheAdapter.get(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_PURCHASE_RETURN_ORDER_AMOUNT + shopId);
      if(value!=null && value>0){
        result = value.intValue();
      }else{
        result = getTodoPurchaseReturnOrderCount(shopId, null, null, customerOrSupplierIdList, null, "allTodo").intValue();
        MemCacheAdapter.set(MemcachePrefix.todoRemind.getValue() + NewTodoConstants.TODO_PURCHASE_RETURN_ORDER_AMOUNT + shopId, result);
      }
    }
    return result;
  }

  @Override
  public void updateRemindCountInMemcacheByTypeAndShopId(RemindEventType type, Long shopId){
    try{
      if(RemindEventType.MEMBER_SERVICE.equals(type)){
        type = RemindEventType.CUSTOMER_SERVICE;
      }
      RemindEventStrategySelector selector = new RemindEventStrategySelector();
      RemindEventStrategy strategy = selector.selectStrategy(type);
      Long flashTime = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()) - 1;
      int count = strategy.countRemindEvent(shopId, null, null, flashTime);
      MemCacheAdapter.set(NewTodoConstants.getNewTodoMemcacheKey(type,shopId), count);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

  @Override
  public void updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType type, Long shopId, List<Long> customerOrSupplierIdList) {
    try{
      if(RemindEventType.TODO_SALE_ORDER.equals(type)){
        int count = getTodoSalesOrderCount(shopId, null, null, customerOrSupplierIdList, null, "allTodo").intValue();
        MemCacheAdapter.set(NewTodoConstants.getNewTodoMemcacheKey(type,shopId), count);
      }else if(RemindEventType.TODO_SALE_RETURN_ORDER.equals(type)){
        int count = getTodoSalesReturnOrderCount(shopId, null, null, customerOrSupplierIdList, null, "allTodo").intValue();
        MemCacheAdapter.set(NewTodoConstants.getNewTodoMemcacheKey(type,shopId), count);
      }else if(RemindEventType.TODO_PURCHASE_ORDER.equals(type)){
        int count = getTodoPurchaseOrderCount(shopId, null, null, customerOrSupplierIdList, null, "allTodo", "created").intValue();
        MemCacheAdapter.set(NewTodoConstants.getNewTodoMemcacheKey(type,shopId), count);
      }else if(RemindEventType.TODO_PURCHASE_RETURN_ORDER.equals(type)){
        int count = getTodoPurchaseReturnOrderCount(shopId, null, null, customerOrSupplierIdList, null, "allTodo").intValue();
        MemCacheAdapter.set(NewTodoConstants.getNewTodoMemcacheKey(type,shopId), count);
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

  @Override
  public List<RemindEventDTO> getLackStorageRemind(Long shopId, Integer pageNo, Integer pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    List<RemindEvent> remindEventList = writer.getLackStorageRemind(shopId, pageNo, pageSize);
    if(!CollectionUtil.isEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
      }
    }
    return remindEventDTOList;
  }

  @Override
  public int countLackStorageRemind(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    int count = writer.countLackStorageRemind(shopId);
    return count;
  }

  public List<Debt> getAllDebtsByCustomerIds(Long shopId, Long[] customerIds) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getAllDebtsByCustomerIds(shopId, customerIds);
  }

  public Map<Long, Object[]> getTotalReturnAmountByCustomerIds(Long shopId, Long... customerIds){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getTotalReturnAmountByCustomerIds(shopId, customerIds);
  }

  public void saveRepairOrderRemindEvent(RepairOrderDTO repairOrderDTO){
    IUserService userService = ServiceManager.getService(IUserService.class);

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<CustomerServiceJobDTO> customerServiceJobDTOs = userService.getCustomerServiceJobByCustomerIdAndVehicleId(
        repairOrderDTO.getShopId(), repairOrderDTO.getCustomerId(), repairOrderDTO.getVechicleId());
      Set<Long> customerServiceJobIds = new HashSet<Long>();

      if (CollectionUtil.isNotEmpty(customerServiceJobDTOs)) {
        for (CustomerServiceJobDTO customerServiceJobDTO : customerServiceJobDTOs) {
          if (customerServiceJobDTO == null || customerServiceJobDTO.getId() == null) {
            continue;
          }
          customerServiceJobIds.add(customerServiceJobDTO.getId());
        }
      }
      Map<Long, RemindEvent> remindEventMap = writer.getRemindEventMapByOldRemindEventIds(repairOrderDTO.getShopId(), customerServiceJobIds);
      if (CollectionUtil.isNotEmpty(customerServiceJobDTOs)) {
        for (CustomerServiceJobDTO customerServiceJobDTO : customerServiceJobDTOs) {
          if (customerServiceJobDTO == null || customerServiceJobDTO.getId() == null
            ||UserConstant.MAINTAIN_MILEAGE.equals(customerServiceJobDTO.getRemindType())) { //保养里程定时钟会处理
            continue;
          }

          RemindEvent remindEvent = remindEventMap.get(customerServiceJobDTO.getId());
          if (remindEvent != null) {
            remindEvent.setEventType(RemindEventType.CUSTOMER_SERVICE.name());
            remindEvent.setCustomerServiceJob(customerServiceJobDTO);
            remindEvent.setCustomerName(repairOrderDTO.getCustomerName());
            remindEvent.setMobile(repairOrderDTO.getMobile());
            remindEvent.setLicenceNo(repairOrderDTO.getVechicle());
            writer.update(remindEvent);
          } else {
            remindEvent = new RemindEvent();
            remindEvent.setCustomerServiceJob(customerServiceJobDTO);
            remindEvent.setEventType(RemindEventType.CUSTOMER_SERVICE.name());
            remindEvent.setCustomerName(repairOrderDTO.getCustomerName());
            remindEvent.setMobile(repairOrderDTO.getMobile());
            remindEvent.setLicenceNo(repairOrderDTO.getVechicle());
            writer.save(remindEvent);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
//      updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE,customerDTO.getShopId());
  }

  @Override
  public ImportResult simpleImportInventoryFromExcel(ImportContext importContext) throws BcgogoException
  {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.directParseData(importContext);

    //2.校验数据
    CheckResult checkResult = inventoryImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = inventoryImporter.importData(importContext);

    return importResult;
  }

  @Override
  public ImportResult simpleImportMemberServiceFromExcel(ImportContext importContext) throws Exception
  {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.directParseData(importContext);

    //2.校验数据
    CheckResult checkResult = memberServiceImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = memberServiceImporter.importData(importContext);

    return importResult;
  }

  @Override
  public ImportResult simpleImportOrderFromExcel(ImportContext importContext) throws Exception
  {
    IImportService importService = ServiceManager.getService(IImportService.class);
    TxnWriter txnWriter=txnDaoManager.getWriter();
    ImportResult importResult = null;
    //1.解析数据
    importService.directParseData(importContext);
    //2.校验数据
    CheckResult checkResult = orderImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }
    //3.保存数据
    Object status = txnWriter.begin();
    try{
      importResult = orderImporter.importData(importContext);
      generateImportedOrder(importContext.getShopId());
      txnWriter.commit(status);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }finally{
      txnWriter.rollback(status);
    }
    return importResult;
  }

  public ReceivableDTO getReceivableByShopIdOrderId(Long shopId, Long orderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Receivable receivable = writer.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,null, orderId);
    if (null != receivable) {
      ReceivableDTO receivableDTO = receivable.toDTO();
      return receivableDTO;
    }
    return null;
  }

  public PurchaseOrderDTO getPurchaseOrderById(Long purchaseOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseOrder purchaseOrder = writer.getById(PurchaseOrder.class, purchaseOrderId);
    return purchaseOrder == null ? null : purchaseOrder.toDTO();
  }

  @Override
  public SalesOrderDTO getSalesOrderByPurchaseOrderIdShopId(Long purchaseOrderId, Long supplierShopId) throws Exception {
    if (purchaseOrderId == null || supplierShopId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    SalesOrder salesOrder;
    List<SalesOrder> salesOrders = writer.getSalesOrderByPurchaseOrderId(purchaseOrderId, supplierShopId);
    if (CollectionUtils.isNotEmpty(salesOrders)) {
      salesOrder = salesOrders.get(0);
      return salesOrder.toDTO();
    }
    return null;

  }

  public List<PurchaseOrder> getPurchaseOrderBySupplierShopId(Long supplierShopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseOrderBySupplierShopId(supplierShopId);
  }

  //计算来料待修 缺料待修 待交付的数量
  @Override
  public int countRemindEvent(Long shopId,RepairRemindEventTypes repairRemindEventTypes) {
    TxnWriter writer = txnDaoManager.getWriter();
    int count = writer.countRepairRemindEvent(shopId, repairRemindEventTypes);
    return count;
  }

  //根据提醒类型查找来料待修 缺料待修 待交付
  @Override
  public  List<RemindEventDTO> queryRepairRemindEvent(Long shopId, Long flashTime, RepairRemindEventTypes repairRemindEventTypes, Integer pageNo, Integer pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<RemindEvent> remindEventList = writer.queryRepairRemindEvent(shopId, flashTime, repairRemindEventTypes, pageNo, pageSize);
    List<RemindEventDTO> remindEventDTOList = new ArrayList<RemindEventDTO>();
    if(!CollectionUtil.isEmpty(remindEventList)){
      for(RemindEvent remindEvent : remindEventList){
        remindEventDTOList.add(remindEvent.toDTO());
      }
    }
    return remindEventDTOList;
  }

  //得到所有的代办事项数量
  @Override
  public int getTotalRemindAmount(Long shopId) {
    //待办事项提醒总数
    int todoRemindAmount = 0;
    try {
      //今天0点时刻前一豪秒，用于判断提醒是否过期
      Long startTime = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date())  - 1;

      //维修美容
      int todoRepairRemindAmount = ServiceManager.getService(ITxnService.class).countRemindEvent(shopId, null);
      //欠款提醒
      int todoArrearRemindAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.DEBT, null, startTime);
      //进销存
      int todoTxnRemindAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.TXN, null, startTime);
      //客户服务
      int todoCustomerServiceRemindAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.CUSTOMER_SERVICE, null, startTime);

      todoRemindAmount = todoRepairRemindAmount +  todoArrearRemindAmount + todoTxnRemindAmount + todoCustomerServiceRemindAmount;


    }catch(Exception e) {
      LOG.debug("method=getTotalRemindAmount");
      LOG.error(e.getMessage(), e);
    }
    return todoRemindAmount;
  }

  @Override
  public void initCustomerStock() throws Exception{
    List<PurchaseInventoryDTO> purchaseInventoryDTOList = new ArrayList<PurchaseInventoryDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> objectList = writer.getPurchaseInventoryOrderIdByOnline();
    IGoodsStorageService goodsStorageService = ServiceManager.getService(IGoodsStorageService.class);
    if(CollectionUtils.isNotEmpty(objectList)){

      for(Object[] objects : objectList){
        PurchaseInventoryDTO purchaseInventoryDTO = goodsStorageService.getPurchaseInventory(NumberUtil.longValue(objects[1]),NumberUtil.longValue(objects[0]));
        purchaseInventoryDTO.setSupplierShopId(NumberUtil.longValue(objects[2]));
        if (!(purchaseInventoryDTO != null && !ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())
          && purchaseInventoryDTO.getShopId() != null && purchaseInventoryDTO.getSupplierShopId() != null)) {
          continue;
        }
        ServiceManager.getService(IProductService.class).updateOldProductMappingsData(purchaseInventoryDTO);
      }

    }
  }

  /**
   *
   * @param shopId
   * @param customerId
   * @return double[0] 是总应付款，double[1]是总应收款
   */
  @Override
  public Double[] getPayableAndReceivable(Long shopId, Long customerId){
    if(customerId == null || shopId == null){
      return new Double[2];
    }
    Double[] result = new Double[2];
    //应付款总额
    Double totalPayable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(customerId, shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
    CustomerDTO customerDTO1 = ServiceManager.getService(IUserService.class).getCustomerById(customerId);
    if(customerDTO1.getSupplierId() != null) {
      List<Double> payables = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(customerDTO1.getSupplierId(),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
      Double supplierPayable = 0.0;
      if(payables != null) {
        supplierPayable = payables.get(0);
      }
      result[0] = NumberUtil.round(Math.abs(NumberUtil.doubleVal(totalPayable)) + Math.abs(NumberUtil.doubleVal(supplierPayable)),2);
    } else {
      result[0] = NumberUtil.round(( Math.abs(NumberUtil.doubleVal(totalPayable))));
    }
    //应收款总额
    Double receivable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(customerId, shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);

    if(customerDTO1.getSupplierId() != null) {
      List<Double> returnList = ServiceManager.getService(ISupplierPayableService.class).getSumPayableBySupplierId(Long.valueOf(customerDTO1.getSupplierId()), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
      if(returnList != null) {
        result[1] = NumberUtil.round(0-returnList.get(0), 2) +NumberUtil.round(receivable,2);
      } else {
        result[1] = NumberUtil.round(receivable,2);
      }
    } else {
      result[1] = NumberUtil.round(receivable,2);
    }
    return result;
  }

  @Override
  public void getPayableAndReceivableToModel(ModelMap model, Long shopId, Long customerId) {
    if(customerId == null || shopId == null){
      return;
    }
    Double[] result = getPayableAndReceivable(shopId, customerId);
    model.addAttribute("totalPayable", String.valueOf(result[0]));
    model.addAttribute("totalReceivable", String.valueOf(result[1]));
  }

  @Override
  public void updateRemindEvent(Long shopId, Long customerId, Long supplierId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateRemindEvent(shopId,customerId,supplierId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateRemindEvent2(Long shopId, Long customerId, Long supplierId) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateRemindEvent2(shopId,customerId,supplierId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<PayableDTO> getPayableDTOBySupplierIdAndOrderType(Long shopId, Long supplierId, OrderTypes orderType)
  {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PayableDTO> payableDTOs = new ArrayList<PayableDTO>();
    List<Payable> payables = writer.getPayableDTOBySupplierIdAndOrderType(shopId,supplierId, orderType);
    if(null== payables)
    {
      return null;
    }
    for(Payable payable : payables) {
      payableDTOs.add(payable.toDTO());
    }
    return payableDTOs;
  }

  @Override
  public List<Long> batchSaveProductWithReindex(Long shopId, Long storehouseId, ProductDTO[] productDTOs) throws Exception{
    List<Long> productIdList=batchSaveProduct(shopId,storehouseId,productDTOs);
    if(CollectionUtil.isNotEmpty(productIdList)){
      final Long[] finalProductIdArr = ArrayUtil.toLongArr(productIdList);
      final Long finalShopId = shopId;
      OrderThreadPool.getInstance().execute(new Runnable() {
        @Override
        public void run() {
          try {
            ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(finalShopId, finalProductIdArr);
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        }
      });
    }
    return productIdList;
  }

  @Override
  public List<Long> batchSaveProduct(Long shopId, Long storehouseId, ProductDTO[] productDTOs) throws Exception{
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);

    if (shopId == null || ArrayUtils.isEmpty(productDTOs)) {
      return null;
    }
    List<InventorySearchIndex> inventorySearchIndexes = new ArrayList<InventorySearchIndex>();
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    inventoryLimitDTO.setShopId(shopId);
    //保存category 商品分类
    Set<String> kindNames = new HashSet<String>();
    for (ProductDTO productDTO : productDTOs) {
      if (StringUtils.isBlank(productDTO.getName()) || StringUtils.isBlank(productDTO.getKindName())) {
        continue;
      }
      productDTO.setKindName(productDTO.getKindName().trim());
      kindNames.add(productDTO.getKindName());
    }
    Map<String,KindDTO> kindDTOMap = getProductService().batchSaveAndGetProductKind(shopId,kindNames);
    //保存车型基本信息
    List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
    List<Long> productIdList=new ArrayList<Long>();
    for (ProductDTO productDTO : productDTOs) {
      productDTO.setInventoryNum(NumberUtil.doubleVal(productDTO.getInventoryNum()));
      if (StringUtils.isBlank(productDTO.getName())) {
        continue;
      }
      productDTO.setShopId(shopId);
      if (StringUtils.isNotBlank(productDTO.getKindName())) {
        KindDTO kindDTO = kindDTOMap.get(productDTO.getKindName());
        if (kindDTO != null) {
          productDTO.setKindId(kindDTO.getId());
        }
      }
      //保存product，productLocalInfo
      getProductService().saveNewProduct(productDTO);
      productIdList.add(productDTO.getProductLocalInfoId());
      VehicleDTO vehicleDTO = new VehicleDTO(productDTO);
      if (vehicleDTO.getId() != null) {
        vehicleDTOs.add(vehicleDTO);
      }
      //保存Inventory，
      InventoryDTO inventoryDTO = new InventoryDTO();
      inventoryDTO.fromProductDTO(productDTO);
      createOrUpdateInventory(inventoryDTO);
      inventoryService.caculateAfterLimit(inventoryDTO,inventoryLimitDTO);
      //保存仓库库存  与当前产品有关的仓库信息封装到 productDTO.getStoreHouseInventoryDTOs()[0] 上
      if (NumberUtil.doubleVal(productDTO.getInventoryNum()) > 0 || StringUtils.isNotBlank(productDTO.getStorageBin())) {
        if (!ArrayUtils.isEmpty(productDTO.getStoreHouseInventoryDTOs()) && productDTO.getStoreHouseInventoryDTOs()[0] != null
          && productDTO.getStoreHouseInventoryDTOs()[0].getStorehouseId() != null) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(productDTO.getStoreHouseInventoryDTOs()[0].getStorehouseId(),
            productDTO.getProductLocalInfoId() , null, productDTO.getInventoryNum(), productDTO.getStorageBin());
          storeHouseService.saveOrUpdateStoreHouseInventoryDTO(storeHouseInventoryDTO);
        }
      }

      //保存供应商库存
      if (NumberUtil.doubleVal(productDTO.getInventoryNum()) > 0 ) {
        SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
        supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
        if (!ArrayUtils.isEmpty(productDTO.getStoreHouseInventoryDTOs()) && productDTO.getStoreHouseInventoryDTOs()[0] != null
          && productDTO.getStoreHouseInventoryDTOs()[0].getStorehouseId() != null) {
          supplierInventoryDTO.setStorehouseId(productDTO.getStoreHouseInventoryDTOs()[0].getStorehouseId());
        }
        supplierInventoryDTO.addStorageInventoryChange(productDTO.getSellUnit(), productDTO.getInventoryNum(), productDTO.getPurchasePrice());
        supplierInventoryDTOs.add(supplierInventoryDTO);
      }

      //组装  InventorySearchIndex
      InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
      inventorySearchIndex.createInventorySearchIndex(inventoryDTO, productDTO);
      inventorySearchIndexes.add(inventorySearchIndex);


    }
    //保存InventorySearchIndex  solr不做索引
//    inventoryService.addOrUpdateInventorySearchIndexWithList(shopId, inventorySearchIndexes);
    ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(shopId,inventorySearchIndexes);
    //保存产品上的车辆品牌
    ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOs);
    //保存库存上下限提醒
    inventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId,inventoryLimitDTO);
    return productIdList;
  }

  private Result validateBatchSaveGoodsInSales(Result result,Long shopId,ProductDTO... fromProductDTOs){
    if(ArrayUtil.isEmpty(fromProductDTOs)||shopId==null){
      return result.LogErrorMsg("请选择上架商品。");
    }
    for(ProductDTO fromProductDTO:fromProductDTOs){

    }
    return result;
  }

  @Override
  public Result batchSaveGoodsInSales(Result result,Long shopId,Long userId,ProductDTO... fromProductDTOs) throws Exception {
    validateBatchSaveGoodsInSales(result,shopId,fromProductDTOs);
    if(!result.isSuccess()){
      return result;
    }
    productService=getProductService();
    List<Long> productIds=new ArrayList<Long>();
    //更新商品
    for(ProductDTO fromProductDTO:fromProductDTOs){
      Long productLocalInfoId=fromProductDTO.getProductLocalInfoId();
      ProductModifyLogDTO newLogDTO = new ProductModifyLogDTO();
      ProductModifyLogDTO oldLogDTO = new ProductModifyLogDTO();
      ProductDTO oldProductDTO =  productService.getProductByProductLocalInfoId(productLocalInfoId, shopId);
      if(oldProductDTO==null||ProductStatus.DISABLED.equals(oldProductDTO.getStatus())){
        return result.LogErrorMsg("商品不存在。");
      }
      ProductDTO newProductDTO=oldProductDTO.clone();
      oldLogDTO.setProduct(oldProductDTO);
      newLogDTO.setProduct(newProductDTO);

      ProductLocalInfoDTO localInfoDTO=productService.getProductLocalInfoById(productLocalInfoId,shopId);
      if(localInfoDTO==null){
        return result.LogErrorMsg("商品不存在。");
      }
      oldLogDTO.setProductLocalInfo(localInfoDTO);
      localInfoDTO.setInSalesAmount(fromProductDTO.getInSalesAmount());
      localInfoDTO.setInSalesPrice(fromProductDTO.getInSalesPrice());
      localInfoDTO.setInSalesUnit(fromProductDTO.getUnit());
      localInfoDTO.setGuaranteePeriod(fromProductDTO.getGuaranteePeriod());
      productService.updateProductLocalInfo(localInfoDTO);
      newLogDTO.setProductLocalInfo(localInfoDTO);

      if (UnitUtil.isAddFirstUnit(localInfoDTO, fromProductDTO)) {
        updateProductUnit(shopId,productLocalInfoId, fromProductDTO.getSellUnit(),
          fromProductDTO.getSellUnit(), fromProductDTO.getRate());
      }
      if(fromProductDTO.getSellUnit()!=null){
        IInventoryService inventoryService=ServiceManager.getService(IInventoryService.class);
        InventoryDTO inventoryDTO =CollectionUtil.getFirst(inventoryService.getInventoryDTOById(shopId,productLocalInfoId));
        if(inventoryDTO!=null){
          inventoryDTO.setUnit(fromProductDTO.getUnit());
          inventoryService.updateInventory(result,inventoryDTO);
        }
      }
      List<ProductModifyLogDTO> logResults = ProductModifyLogDTO.compare(oldLogDTO,newLogDTO);
      if(CollectionUtil.isNotEmpty(logResults)){
        for(ProductModifyLogDTO logDTO : logResults){
          logDTO.setProductId(productLocalInfoId);
          logDTO.setShopId(shopId);
          logDTO.setUserId(userId);
          logDTO.setOperationType(ProductModifyOperations.INVENTORY_INDEX_UPDATE);
          logDTO.setStatProcessStatus(StatProcessStatus.NEW);
        }
        batchCreateProductModifyLog(logResults);
      }
      //update inventorySearchIndex
      ISearchService searchService=ServiceManager.getService(ISearchService.class);
      InventorySearchIndexDTO isiDTO=searchService.getInventorySearchIndexById(shopId,productLocalInfoId);
      if(isiDTO!=null){
        isiDTO.setProductDTO(newProductDTO);
        searchService.updateInventorySearchIndex(isiDTO);
      }
      productIds.add(productLocalInfoId);
      fromProductDTO.setProductLocalInfoId(productLocalInfoId);
    }
    //3.save or update unit
//    ServiceManager.getService(IConfigService.class).saveOrUpdateUnitSort(shopId,fromProductDTO.getSellUnit(),fromProductDTO.getStorageUnit());
    result.setDataList(ArrayUtil.toLongArr(productIds));
    return result;
  }

  @Override
  public Result saveOrUpdateProductInSales(Result result,ProductDTO fromProductDTO) throws Exception {
    Long shopId=fromProductDTO.getShopId();
    Long userId=fromProductDTO.getUserId();
    if(shopId==null||userId==null){
      throw new BcgogoException("参数异常。");
    }
    Long productLocalInfoId=fromProductDTO.getProductLocalInfoId();
    productService=getProductService();
    //1.save or update product
    if(productLocalInfoId==null){
      result= validateSaveNewProduct(fromProductDTO);
      if(!result.isSuccess()){
        return result;
      }
      fromProductDTO.setStorageUnit(fromProductDTO.getSellUnit());
      productLocalInfoId=CollectionUtil.getFirst(batchSaveProduct(shopId, null, new ProductDTO[]{fromProductDTO}));
      fromProductDTO.setProductLocalInfoId(productLocalInfoId);
      ServiceManager.getService(IConfigService.class).saveOrUpdateUnitSort(shopId,new ProductDTO[]{fromProductDTO});
    }else {
      ProductModifyLogDTO newLogDTO = new ProductModifyLogDTO();
      ProductModifyLogDTO oldLogDTO = new ProductModifyLogDTO();
      ProductDTO oldProductDTO =  productService.getProductByProductLocalInfoId(productLocalInfoId, shopId);
      if(oldProductDTO==null||ProductStatus.DISABLED.equals(oldProductDTO.getStatus())){
        return result.LogErrorMsg("商品不存在。");
      }
      ProductDTO productDTO=oldProductDTO.clone();
      productDTO.setName(fromProductDTO.getName());
      productDTO.setBrand(fromProductDTO.getBrand());
      productDTO.setSpec(fromProductDTO.getSpec());
      productDTO.setModel(fromProductDTO.getModel());
      productDTO.setProductVehicleBrand(fromProductDTO.getProductVehicleBrand());
      productDTO.setProductVehicleModel(fromProductDTO.getProductVehicleModel());
      productDTO.setCommodityCode(fromProductDTO.getCommodityCode());
      productDTO.setKindId(fromProductDTO.getKindId());
      productDTO.setDescription(fromProductDTO.getDescription());
      if (!oldProductDTO.checkSameBasicProperties(productDTO)&&!productService.checkSameProduct(shopId,productDTO)) {
        return result.LogErrorMsg("商品已经存在。");
      }
      oldLogDTO.setProduct(oldProductDTO);
      newLogDTO.setProduct(productDTO);

      ProductLocalInfoDTO localInfoDTO=productService.getProductLocalInfoById(productLocalInfoId,shopId);
      if(localInfoDTO==null){
        return result.LogErrorMsg("商品不存在。");
      }
      fromProductDTO.setId(localInfoDTO.getProductId());
      oldLogDTO.setProductLocalInfo(localInfoDTO);
      localInfoDTO.fromProductForGoodsInSales(fromProductDTO);
      productService.updateProductLocalInfo(localInfoDTO);
      newLogDTO.setProductLocalInfo(localInfoDTO);

      if (UnitUtil.isAddFirstUnit(localInfoDTO, fromProductDTO)) {
        updateProductUnit(shopId,productLocalInfoId, fromProductDTO.getSellUnit(),
          fromProductDTO.getSellUnit(), fromProductDTO.getRate());
      }
      productService.updateProduct(shopId, fromProductDTO);
      IInventoryService inventoryService=ServiceManager.getService(IInventoryService.class);
      InventoryDTO inventoryDTO =CollectionUtil.getFirst(inventoryService.getInventoryDTOById(shopId,productLocalInfoId));
      if(inventoryDTO!=null){
        if(fromProductDTO.getSellUnit()!=null)
          inventoryDTO.setUnit(fromProductDTO.getUnit());
        if(fromProductDTO.getPrice()!=null)
          inventoryDTO.setSalesPrice(fromProductDTO.getPrice());
        inventoryService.updateInventory(result,inventoryDTO);
      }
      List<ProductModifyLogDTO> logResults = ProductModifyLogDTO.compare(oldLogDTO,newLogDTO);
      if(CollectionUtil.isNotEmpty(logResults)){
        for(ProductModifyLogDTO logDTO : logResults){
          logDTO.setProductId(productLocalInfoId);
          logDTO.setShopId(shopId);
          logDTO.setUserId(userId);
          logDTO.setOperationType(ProductModifyOperations.INVENTORY_INDEX_UPDATE);
          logDTO.setStatProcessStatus(StatProcessStatus.NEW);
        }
        batchCreateProductModifyLog(logResults);
      }
      //update inventorySearchIndex
      ISearchService searchService=ServiceManager.getService(ISearchService.class);
      InventorySearchIndexDTO isiDTO=searchService.getInventorySearchIndexById(shopId,productLocalInfoId);
      if(isiDTO!=null){
        isiDTO.setProductDTO(productDTO);
        searchService.updateInventorySearchIndex(isiDTO);
      }
    }
    //4.save or update unit
//    ServiceManager.getService(IConfigService.class).saveOrUpdateUnitSort(shopId,fromProductDTO.getSellUnit(),fromProductDTO.getStorageUnit());
    result.setData(productLocalInfoId);
    return result;
  }

  @Override
  public Result validateSaveNewProduct(ProductDTO productDTO)throws Exception{
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Result result = new Result();
    if (productDTO == null || productDTO.getShopId() == null) {
      return new Result(ProductConstants.NO_PRODUCT_INFO, false);
    }
    if (StringUtils.isBlank(productDTO.getName())) {
      return new Result(ProductConstants.NO_PRODUCT_NAME, false);
    }

    if (StringUtils.isNotBlank(productDTO.getCommodityCode())) {
      ProductDTO commodityCodeProduct = getProductService().getProductDTOByCommodityCode(productDTO.getShopId(), productDTO.getCommodityCode());
      if (commodityCodeProduct != null) {
        return new Result(ProductConstants.EXIST_PRODUCT_COMMODITY_CODE, false);
      }
    }
    List<ProductDTO> propertyProducts = getProductService().getProductDTOsBy7P(productDTO.getShopId(), productDTO);
    if (CollectionUtils.isNotEmpty(propertyProducts)) {
      return new Result(ProductConstants.EXIST_PRODUCT, false);
    }

    if(ArrayUtil.isNotEmpty(productDTO.getStoreHouseInventoryDTOs())
      && productDTO.getStoreHouseInventoryDTOs()[0] != null
      && productDTO.getStoreHouseInventoryDTOs()[0].getStorehouseId() != null ){
      StoreHouseDTO storeHouseDTO =  storeHouseService.getStoreHouseDTOById(productDTO.getShopId(),productDTO.getStoreHouseInventoryDTOs()[0].getStorehouseId());
      if(storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
        return new Result(ProductConstants.NO_STOREHOUSE, false);
      }
    }
    return result;
  }

  public Map<String, ProductDTO> getOrderPromotionsDetail(Long shopId, SearchConditionDTO searchConditionDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    Set<Long> supplierShopIdSet = userService.getRelatedSuppliersIdsByShopId(shopId);
    supplierShopIdSet.add(shopId); //卖方也需要看到
    if (CollectionUtil.isEmpty(supplierShopIdSet)) {
      return new HashMap<String, ProductDTO>();
    }
    if (searchConditionDTO.isEmptyOfProductInfo()) {
      searchConditionDTO.setSort("last_in_sales_time desc");//默认上架时间排序
    }
    searchConditionDTO.setSalesStatus(ProductStatus.InSales);
    searchConditionDTO.setIncludeBasic(false);
    searchConditionDTO.setShopIds(supplierShopIdSet.toArray(new Long[supplierShopIdSet.size()]));
    ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithStdQuery(searchConditionDTO);
    List<ProductDTO> productDTOs = productSearchResultListDTO.getProducts();


    if (searchConditionDTO.getOrderId()!=null) {  //历史的促销从record里拿
      List<PromotionOrderRecordDTO> orderRecordDTOs = promotionsService.getPromotionOrderRecordDTO(searchConditionDTO.getOrderId());
      Map<Long, List<PromotionOrderRecordDTO>> recordMap = new HashMap<Long, List<PromotionOrderRecordDTO>>();
      if (CollectionUtil.isNotEmpty(orderRecordDTOs)) {
        for (PromotionOrderRecordDTO recordDTO : orderRecordDTOs) {
          List<PromotionOrderRecordDTO> recordDTOList = recordMap.get(recordDTO.getProductId());
          if (recordDTOList == null) {
            recordDTOList = new ArrayList<PromotionOrderRecordDTO>();
            recordMap.put(recordDTO.getProductId(), recordDTOList);
          }
          recordDTOList.add(recordDTO);
        }
      }
      if(CollectionUtil.isEmpty(productDTOs)&&ArrayUtil.isNotEmpty(searchConditionDTO.getProductIdArr())){
        if(productDTOs==null){
          productDTOs=new ArrayList<ProductDTO>();
        }
        for(String productId:searchConditionDTO.getProductIdArr()){
          ProductDTO productDTO=new ProductDTO();
          productDTO.setProductLocalInfoId(NumberUtil.longValue(productId));
          productDTO.setPromotionsDTOs(PromotionsUtils.generatePromotionsFromRecord(recordMap.get(NumberUtil.longValue(productId))));
          productDTOs.add(productDTO);
        }
      }else {
        for (ProductDTO productDTO : productDTOs) {
          productDTO.setPromotionsDTOs(PromotionsUtils.generatePromotionsFromRecord(recordMap.get(productDTO.getProductLocalInfoId())));
        }
      }

    }

    //去掉不满足送货上门包邮地区
    if (CollectionUtil.isNotEmpty(productDTOs)) {
      List<Long> promotionsIdList = new ArrayList<Long>();
      for (ProductDTO productDTO : productDTOs) {
        PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
        if (promotionsDTO != null) {
          promotionsIdList.add(promotionsDTO.getId());
        }
      }
      if (promotionsIdList.size() > 0) {
        Map<Long, Boolean> isInPromotionsArea = promotionsService.judgePromotionsAreaByShopId(shopId, promotionsIdList.toArray(new Long[promotionsIdList.size()]));
        for (ProductDTO productDTO : productDTOs) {
          PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
          if (promotionsDTO != null && !isInPromotionsArea.get(promotionsDTO.getId())) {
            List<PromotionsDTO> promotionsDTOs = productDTO.getPromotionsDTOs();
            Iterator<PromotionsDTO> iterator = promotionsDTOs.iterator();
            while (iterator.hasNext()) {
              PromotionsDTO promDTO = iterator.next();
              if (PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promDTO.getType())) {
                iterator.remove();
              }
            }
          }
        }
      }
    }
    return PromotionsUtils.covertToProductPromotions(productDTOs);
  }

  public Map<String, ProductDTO> getOrderPromotionsHistoryDetail(Long shopId, SearchConditionDTO searchConditionDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    Map<Long,List<PromotionsDTO>> promotionsDTOMap=promotionsService.getPromotionsDTOMapByProductLocalInfoId(shopId,false,ArrayUtil.toLongArr(searchConditionDTO.getProductIdArr()));

    List<ProductDTO> productDTOs = null;

    if (CollectionUtil.isNotEmpty(productDTOs)) {
      List<Long> promotionsIdList = new ArrayList<Long>();
      for (ProductDTO productDTO : productDTOs) {
        PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
        if (promotionsDTO != null) {
          promotionsIdList.add(promotionsDTO.getId());
        }
      }
      //去掉不满足送货上门
      if (promotionsIdList.size() > 0) {
        Map<Long, Boolean> isInPromotionsArea = promotionsService.judgePromotionsAreaByShopId(shopId, promotionsIdList.toArray(new Long[promotionsIdList.size()]));
        for (ProductDTO productDTO : productDTOs) {
          PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
          if (promotionsDTO != null && !isInPromotionsArea.get(promotionsDTO.getId())) {
            List<PromotionsDTO> promotionsDTOs = productDTO.getPromotionsDTOs();
            Iterator<PromotionsDTO> iterator = promotionsDTOs.iterator();
            while (iterator.hasNext()) {
              PromotionsDTO promDTO = iterator.next();
              if (PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promDTO.getType())) {
                iterator.remove();
              }
            }
          }
        }
      }
    }
    if (searchConditionDTO.getOrderId() != null) {  //历史的促销从record里拿
      List<PromotionOrderRecordDTO> orderRecordDTOs = promotionsService.getPromotionOrderRecordDTO(searchConditionDTO.getOrderId());
      Map<Long, List<PromotionOrderRecordDTO>> recordMap = new HashMap<Long, List<PromotionOrderRecordDTO>>();
      if (CollectionUtil.isNotEmpty(orderRecordDTOs)) {
        for (PromotionOrderRecordDTO recordDTO : orderRecordDTOs) {
          List<PromotionOrderRecordDTO> recordDTOList = recordMap.get(recordDTO.getProductId());
          if (recordDTOList == null) {
            recordDTOList = new ArrayList<PromotionOrderRecordDTO>();
            recordMap.put(recordDTO.getProductId(), recordDTOList);
          }
          recordDTOList.add(recordDTO);
        }
      }
      for (ProductDTO productDTO : productDTOs) {
        productDTO.setPromotionsDTOs(PromotionsUtils.generatePromotionsFromRecord(recordMap.get(productDTO.getProductLocalInfoId())));
      }
    }

    return PromotionsUtils.covertToProductPromotions(productDTOs);
  }

  @Override
  public List<Payable> getSupplierPayable() {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSupplierPayable();
  }

  @Override
  public void updateRemindEventStatus(Long shopId, Long customerOrSupplierId, String identity) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateRemindEventStatus(shopId, customerOrSupplierId, identity);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }


  @Override
  public List<OrderItemPromotionDTO> getOrderItemPromotionsByOrderItemId(Long orderItemId) {
    List<OrderItemPromotionDTO> orderItemPromotionDTOs = new ArrayList<OrderItemPromotionDTO>();
    if (orderItemId == null)
      return orderItemPromotionDTOs;
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<OrderItemPromotion> orderItemPromotions = txnWriter.getOrderItemPromotionsByOrderItemId(orderItemId);
    if (CollectionUtils.isNotEmpty(orderItemPromotions)){
      for (OrderItemPromotion orderItemPromotion:orderItemPromotions){
        orderItemPromotionDTOs.add(orderItemPromotion.toDTO());
      }
    }
    return orderItemPromotionDTOs;
  }

  @Override
  public Long getSalesNewOrderCountBySupplierShopId(Long supplierShopId, Long startTime, Long endTime, String orderStatus, String timeField) {
    Long count = 0l;
    TxnWriter writer = txnDaoManager.getWriter();
    count = writer.getSalesNewOrderCountBySupplierShopId(supplierShopId,startTime,endTime,orderStatus, timeField).longValue();
    return count;
  }

  @Override
  public void saveProductCategoryAndRelation(Long shopId,Long userId,ProductDTO... fromProductDTOs) throws Exception{
    if(ArrayUtils.isEmpty(fromProductDTOs)) return;
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    //保存商品分类关系
    Long firstProductCategoryId = null;
    for(ProductDTO fromProductDTO : fromProductDTOs){
      Long productCategoryId = null;
      if(fromProductDTO.getProductCategoryId()!=null){
        productCategoryId = fromProductDTO.getProductCategoryId();
      }else if(StringUtils.isNotBlank(fromProductDTO.getProductCategoryName())){
        List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getProductCategoryDTOByName(shopId, fromProductDTO.getProductCategoryName());
        if(CollectionUtils.isNotEmpty(productCategoryDTOList) && productCategoryDTOList.size()>1){
          for (ProductCategoryDTO pc : productCategoryDTOList){
            if(ProductCategoryType.SECOND_CATEGORY.equals(pc.getCategoryType()) && pc.getShopId().equals(shopId)){
              productCategoryId = pc.getId();
            }
          }
          for (ProductCategoryDTO pc : productCategoryDTOList){
            if(ProductCategoryType.SECOND_CATEGORY.equals(pc.getCategoryType()) && pc.getShopId().equals(ShopConstant.BC_ADMIN_SHOP_ID)){
              productCategoryId = pc.getId();
            }
          }
          for (ProductCategoryDTO pc : productCategoryDTOList){//自定义的没有三级  所以不考虑
            if(ProductCategoryType.THIRD_CATEGORY.equals(pc.getCategoryType())){
              productCategoryId = pc.getId();
            }
          }
        }else if(CollectionUtils.isNotEmpty(productCategoryDTOList) && productCategoryDTOList.size() == 1){
          productCategoryId = productCategoryDTOList.get(0).getId();
        }
        if(productCategoryId ==null){
          //新增 保存 先找 父  firstCategory
          productCategoryDTOList = productCategoryService.getProductCategoryDTOByName(shopId, ProductCategoryConstant.CUSTOM_FIRST_CATEGORY_NAME);
          if(CollectionUtils.isNotEmpty(productCategoryDTOList)){
            for(ProductCategoryDTO pc : productCategoryDTOList){
              if(pc.getShopId().equals(shopId)){
                firstProductCategoryId = pc.getId();
              }
            }
          }
          if(firstProductCategoryId==null){
            ProductCategoryDTO firstProductCategoryDTO = new ProductCategoryDTO();
            firstProductCategoryDTO.setCategoryType(ProductCategoryType.FIRST_CATEGORY);
            firstProductCategoryDTO.setShopId(shopId);
            firstProductCategoryDTO.setParentId(-1l);
            firstProductCategoryDTO.setName(ProductCategoryConstant.CUSTOM_FIRST_CATEGORY_NAME);
            firstProductCategoryDTO.setStatus(ProductCategoryStatus.ENABLED);
            firstProductCategoryDTO = productCategoryService.saveOrUpdateProductCategoryDTO(firstProductCategoryDTO);
            ServiceManager.getService(IProductSolrWriterService.class).createProductCategorySolrIndex(shopId,firstProductCategoryDTO.getId());
            firstProductCategoryId = firstProductCategoryDTO.getId();
          }
          ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
          productCategoryDTO.setCategoryType(ProductCategoryType.SECOND_CATEGORY);
          productCategoryDTO.setShopId(shopId);
          productCategoryDTO.setParentId(firstProductCategoryId);
          productCategoryDTO.setName(fromProductDTO.getProductCategoryName());
          productCategoryDTO.setStatus(ProductCategoryStatus.ENABLED);
          productCategoryDTO = productCategoryService.saveOrUpdateProductCategoryDTO(productCategoryDTO);
          ServiceManager.getService(IProductSolrWriterService.class).createProductCategorySolrIndex(shopId,productCategoryDTO.getId());
          productCategoryId = productCategoryDTO.getId();
        }
      }
      if(productCategoryId!=null){
        productCategoryService.saveProductCategoryRelation(shopId,productCategoryId,fromProductDTO.getProductLocalInfoId());
        productCategoryService.saveOrUpdateRecentlyUsedProductCategory(shopId,userId,productCategoryId);
      }else {
        LOG.error("保存商品分类出错!productLocalInfoId:["+fromProductDTO.getProductLocalInfoId()+"],productCategoryId:["+productCategoryId+"],productCategoryName:["+fromProductDTO.getProductCategoryName()+"]");
      }

    }
  }

  @Override
  public List<RepairAndDraftSearchResultDTO> getRepairAndDraftOrders(DraftOrderSearchDTO draftOrderSearchDTO) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairAndDraftSearchResultDTO> repairAndDraftSearchResultDTOs = new ArrayList<RepairAndDraftSearchResultDTO>();
    Long startTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getStartTime());
    Long endTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getEndTime());
    List fieldArrayList = writer.getRepairAndDraftOrders(draftOrderSearchDTO.getShopId(),draftOrderSearchDTO.getUserId(),draftOrderSearchDTO.getVehicleId(),draftOrderSearchDTO.getPager(),draftOrderSearchDTO.getOrderTypes(),startTime,endTime);
    if(CollectionUtil.isNotEmpty(fieldArrayList)) {
      for(Object object : fieldArrayList) {
        RepairAndDraftSearchResultDTO repairAndDraftSearchResultDTO = new RepairAndDraftSearchResultDTO();
        Object[] array = (Object[])object;
        repairAndDraftSearchResultDTO.setIdStr(array[0].toString());
        repairAndDraftSearchResultDTO.setReceiptNo(array[1] == null ? "" : array[1].toString());
        if(array[2] != null) {
          repairAndDraftSearchResultDTO.setSaveTimeStr(DateUtil.dateLongToStr(Long.valueOf(array[2].toString()),DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
        } else {
          repairAndDraftSearchResultDTO.setSaveTimeStr("");
        }
        repairAndDraftSearchResultDTO.setCustomerName(array[3] == null ? "" : array[3].toString());
        repairAndDraftSearchResultDTO.setVechicle(array[4] == null ? "" : array[4].toString());
        if(DraftOrderStatus.DRAFT_SAVED.toString().equals(array[7].toString())) {
          repairAndDraftSearchResultDTO.setMaterial(array[5] == null ? "" : array[5].toString());
          repairAndDraftSearchResultDTO.setServiceContent(array[6] == null ? "" : array[6].toString());
          repairAndDraftSearchResultDTO.setOrderType("DRAFT");
        } else {
          repairAndDraftSearchResultDTO.setOrderType("REPAIR");
          List<RepairOrderItem> repairOrderItemList = getRepairOrderItemByRepairOrderId(Long.valueOf(array[0].toString()));
          if(CollectionUtil.isNotEmpty(repairOrderItemList)) {
            Set<Long> productIds = new HashSet<Long>();
            for(RepairOrderItem repairOrderItem : repairOrderItemList) {
              if (repairOrderItem != null && repairOrderItem.getProductId() != null) {
                productIds.add(repairOrderItem.getProductId());
              }
            }
            Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(draftOrderSearchDTO.getShopId(), productIds);
            String material = "";
            for(Long productId : productDTOMap.keySet()) {
              material += productDTOMap.get(productId).getName() + ";";
            }
            repairAndDraftSearchResultDTO.setMaterial(StringUtil.isEmpty(material) ? "" : material.substring(0,material.length() - 1));
          } else {
            repairAndDraftSearchResultDTO.setMaterial("");
          }
          List<RepairOrderService> repairOrderServiceList = getRepairOrderServicesByRepairOrderId(Long.valueOf(array[0].toString()));
          if(CollectionUtil.isNotEmpty(repairOrderServiceList)) {
            Set<Long> serviceIds = new HashSet<Long>();
            for(RepairOrderService repairOrderService : repairOrderServiceList) {
              serviceIds.add(repairOrderService.getServiceId());
            }
            Map<Long, ServiceDTO> serviceDTOMap = getServiceByServiceIdSet(draftOrderSearchDTO.getShopId(), serviceIds);
            String serviceContent = "";
            for(Long serviceId : serviceDTOMap.keySet()) {
              serviceContent += serviceDTOMap.get(serviceId).getName() + ";";
            }
            repairAndDraftSearchResultDTO.setServiceContent(StringUtil.isEmpty(serviceContent) ? "" : serviceContent.substring(0,serviceContent.length() -1));
          } else {
            repairAndDraftSearchResultDTO.setServiceContent("");
          }
        }
        repairAndDraftSearchResultDTOs.add(repairAndDraftSearchResultDTO);
      }
    }
    return  repairAndDraftSearchResultDTOs;
  }

  @Override
  public int countRepairOrders(Long shopId, Long vechicleId) {
    if(shopId == null || vechicleId == null) return 0;
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countRepairOrders(shopId,vechicleId);
  }

  public List<ServiceDTO> getUseTimesMostService(Long shopId) {
    List<ServiceDTO> serviceDTOs = new ArrayList<ServiceDTO>();
    List<Service> services = txnDaoManager.getWriter().getUseTimesMostService(shopId);
    if (CollectionUtil.isEmpty(services)) {
      return serviceDTOs;
    }

    for (Service service : services) {
      serviceDTOs.add(service.toDTO());
    }

    return serviceDTOs;
  }

  @Override
  public void deleteTxnRemind(Long shopId, Long purchaseOrderId) {
    if(shopId == null || purchaseOrderId == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteTxnRemind(shopId, purchaseOrderId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateDebtRemindDeletedType(Long shopId, Long customerOrSupplierId, String identity,DeletedType deletedType) {
    if(customerOrSupplierId == null || StringUtils.isEmpty(identity)) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateDebtRemindDeletedType(shopId, customerOrSupplierId, identity,deletedType);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
  @Override
  public void saveOperationLogTxnService(OperationLogDTO... operationLogDTOs) throws Exception {
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    for(OperationLogDTO operationLogDTO : operationLogDTOs) {
      if(operationLogDTO.getUserId() != null) {
        UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserId(operationLogDTO.getUserId());
        if(userDTO != null) {
          operationLogDTO.setUserName(userDTO.getName());
        }
      }
    }
    operationLogService.saveOperationLog(operationLogDTOs);
  }

  /**
   * 记录提醒框是否发送的状态
   * @param shopId
   * @param isSend
   */
  public void updateRepairOrderMessageFlag(Long shopId,String isSend,Long repairId){
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      RepairOrder repairOrder = writer.getRepairOrderById(repairId,shopId);
      repairOrder.setIsSmsSend(isSend);
      writer.update(repairOrder);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public int countStatDetailByNormalProductIds(Long[] shopIds, Long normalProductId, NormalProductStatType normalProductStatType) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.countStatDetailByNormalProductIds(shopIds, normalProductId, normalProductStatType);
  }

  public List<NormalProductInventoryStat> getStatDetailByNormalProductIds(Long[] shopIds,Long normalProductIds,NormalProductStatType normalProductStatType, Pager pager) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getStatDetailByNormalProductIds(shopIds, normalProductIds, normalProductStatType,pager);
  }

  /**
   * 更新receivable的couponConsume字段
   * @param shopId
   * @param receivableId
   * @param couponConsume
   */
  public void updateReceivableCouponConsume(Long shopId, Long receivableId, Double couponConsume){
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateReceivableCouponConsume(shopId,receivableId,couponConsume);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}
