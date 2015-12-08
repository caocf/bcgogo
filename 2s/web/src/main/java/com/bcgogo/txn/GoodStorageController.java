package com.bcgogo.txn;

import com.bcgogo.common.*;
import com.bcgogo.common.CookieUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.dto.KindDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.bcgogoListener.orderEvent.PurchaseInventorySavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.supplierComment.CommentConstant;
import com.bcgogo.txn.dto.supplierComment.SupplierCommentRecordDTO;
import com.bcgogo.txn.model.Category;
import com.bcgogo.txn.model.RepairRemindEvent;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.txn.service.web.IGoodsStorageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping("/storage.do")
public class GoodStorageController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(GoodStorageController.class);
	private static final String REDIRECT_SHOW = "redirect:storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE";
  private static final String FIRST_OPEN ="firstOpen";//判断是否是第一次进入入库单详情

	private IGoodBuyService goodBuyService;
	private IGoodSaleService goodSaleService;
  private IPickingService pickingService;
  private IPrivilegeService privilegeService;
  private IProductOutStorageService productOutStorageService;

  private static final String MODEL_SETTLEMENTTYPE_LIST = "settlementTypeList";
  private static final String MODEL_INVOICECATEGORY_LIST = "invoiceCategoryList";

	public IGoodBuyService getGoodBuyService() {
		return goodBuyService == null ? ServiceManager.getService(IGoodBuyService.class) : goodBuyService;
	}

	public IGoodSaleService getGoodSaleService() {
		return goodSaleService == null ? ServiceManager.getService(IGoodSaleService.class) : goodSaleService;
	}

  public IPickingService getPickingService() {
    return pickingService == null ?ServiceManager.getService(IPickingService.class) : pickingService;
  }
  public IPrivilegeService getPrivilegeService(){
    return privilegeService==null?ServiceManager.getService(IPrivilegeService.class):privilegeService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }

  /**
   * 根据入库单Id找入库单
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=getPurchaseInventoryToPrint")
  public void print(ModelMap model, HttpServletRequest request, HttpServletResponse response, @RequestParam("purchaseInventoryId") String purchaseInventoryId) throws Exception {
    getPurchaseInventory(model, request, purchaseInventoryId);
    ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INVENTORY);
    PurchaseInventoryDTO purchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    if(purchaseInventoryDTO!=null){
      if(StringUtils.isEmpty(purchaseInventoryDTO.getMobile())&&!StringUtils.isEmpty(purchaseInventoryDTO.getLandline())){
        purchaseInventoryDTO.setMobile(purchaseInventoryDTO.getLandline());
      }
    }
    purchaseInventoryDTO.setShopLandLine(shopDTO.getLandline());
    String myTemplateName = "goodsStoragePrint" + String.valueOf(WebUtil.getShopId(request));
    VelocityContext context = new VelocityContext();
    //把数据填入上下文
    context.put("purchaseInventoryDTO", purchaseInventoryDTO);
    context.put("storeManagerMobile", null == shopDTO.getStoreManagerMobile() ? "" : shopDTO.getStoreManagerMobile());
    PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
  }

  /**
   * 根据入库单Id找入库单
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=getPurchaseInventory")
  public String getPurchaseInventory(ModelMap model, HttpServletRequest request,String purchaseInventoryId) {
    Long shopId = WebUtil.getShopId(request);
    LOG.info("查看入库单开始!shopId:{}, purchaseInventoryId:{}",shopId,purchaseInventoryId);
    long begin = System.currentTimeMillis();
    long current = begin;
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    try {
      PurchaseInventoryDTO purchaseInventoryDTO = goodsStorageService.getPurchaseInventory(NumberUtils.toLong(purchaseInventoryId), WebUtil.getShopId(request));
      LOG.debug("查看入库单--阶段1。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      if (purchaseInventoryDTO == null) {
        LOG.error("can not find purchaseInventory with id" + purchaseInventoryId);
        List<StoreHouseDTO> storeHouseDTOList = null;
        if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
          model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        }
        return "/txn/goodsStorage";
      }
      purchaseInventoryDTO.setEditDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, purchaseInventoryDTO.getEditDate()));          // 制单日期
      purchaseInventoryDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, purchaseInventoryDTO.getVestDate()));          // 归属日期

      ShopDTO shopDTO = configService.getShopById(shopId);
      purchaseInventoryDTO.setShopName(shopDTO.getName());
      purchaseInventoryDTO.setShopAddress(shopDTO.getAddress());
      purchaseInventoryDTO.setShopLandLine(shopDTO.getLandline());
      //获得单据应付款信息

      List<PayableHistoryRecordDTO> payableHistoryRecordDTOList = supplierPayableService.getPayableHistoryRecord(shopId, purchaseInventoryDTO.getSupplierId(), NumberUtils.toLong(purchaseInventoryId),null);
      PayableDTO payableDTO = supplierPayableService.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
      LOG.debug("查看入库单--阶段2。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      if (payableDTO != null) {
        purchaseInventoryDTO.setStroageActuallyPaid(NumberUtil.round(NumberUtil.numberValue(payableDTO.getAmount(), 0D) - NumberUtil.numberValue(payableDTO.getCreditAmount(), 0D) - NumberUtil.numberValue(payableDTO.getDeduction(), 0D), NumberUtil.MONEY_PRECISION));
        purchaseInventoryDTO.setStroageCreditAmount(payableDTO.getCreditAmount());
        purchaseInventoryDTO.setStroageSupplierDeduction(payableDTO.getDeduction());

        purchaseInventoryDTO.setCash(NumberUtil.doubleVal(payableDTO.getCash()));
        purchaseInventoryDTO.setBankCardAmount(NumberUtil.doubleVal(payableDTO.getBankCard()));
        purchaseInventoryDTO.setCheckAmount(NumberUtil.doubleVal(payableDTO.getCheque()));
        purchaseInventoryDTO.setDepositAmount(NumberUtil.doubleVal(payableDTO.getDeposit()));

        purchaseInventoryDTO.setStrikeAmount(NumberUtil.doubleVal(payableDTO.getStrikeAmount()));
        purchaseInventoryDTO.setPayer(payableDTO.getLastPayer());

        purchaseInventoryDTO.setStatementAmount(NumberUtil.doubleVal(payableDTO.getStatementAccount()));

        if (purchaseInventoryDTO.getCheckAmount() > 0) {
          purchaseInventoryDTO.setCheckNo("");
          if (CollectionUtils.isNotEmpty(payableHistoryRecordDTOList)) {
            for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
              if (!StringUtils.isEmpty(payableHistoryRecordDTO.getCheckNo())) {
                purchaseInventoryDTO.setCheckNo(purchaseInventoryDTO.getCheckNo() + " " + payableHistoryRecordDTO.getCheckNo());
              }
            }
          }
        }
      }
      purchaseInventoryDTO.setReturnMoneyType("");

      LOG.debug("查看入库单--阶段3。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      if(null != purchaseInventoryDTO)
      {
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(purchaseInventoryDTO.getSupplierId(),purchaseInventoryDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
        SupplierDTO supplierDTO = userService.getSupplierById(purchaseInventoryDTO.getSupplierId());
        //应付款总额
        Double totalPayable = doubleList.get(0);
          if(supplierDTO.getCustomerId() != null) {
              Double payable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
              request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)- NumberUtil.doubleVal(payable)));
          } else {
              request.setAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)));
          }
          //应收款
          List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(purchaseInventoryDTO.getSupplierId(), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
          if(supplierDTO.getCustomerId() != null) {
              Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
              request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2) +NumberUtil.round(receivable,2)));

          }  else {
              request.setAttribute("totalReceivable", String.valueOf(NumberUtil.round(0-returnList.get(0), 2)));
          }

        SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(purchaseInventoryDTO.getShopId(),purchaseInventoryDTO.getSupplierId());
        if(null != supplierRecordDTO && null != supplierRecordDTO.getDebt())
        {
          supplierRecordDTO.setDebt(NumberUtil.round(supplierRecordDTO.getDebt(),NumberUtil.MONEY_PRECISION));
        }
        request.setAttribute("supplierRecordDTO",supplierRecordDTO);
        model.addAttribute("supplierDTO", supplierDTO);
      }
      LOG.debug("查看入库单--阶段4。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      model.addAttribute("purchaseInventoryDTO", purchaseInventoryDTO);
//          purchaseInventoryItemDTO.setBarcode(productDTO.getBarcode());
      if(CollectionUtils.isNotEmpty(purchaseInventoryDTO.getNotInSaleProductIds())){
        model.addAttribute("notInSalesIds", StringUtil.arrayToStr(",", purchaseInventoryDTO.getNotInSaleProductIds().toArray(new Long[purchaseInventoryDTO.getNotInSaleProductIds().size()])));
      }
      model.addAttribute("storeManagerMobile", null == shopDTO.getStoreManagerMobile() ? "" : shopDTO.getStoreManagerMobile());
      String print = request.getParameter("print");
      if(StringUtils.isNotBlank(print) && Boolean.valueOf(print)){
        model.addAttribute("print", Boolean.valueOf(print));
      }
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,purchaseInventoryDTO.getStorehouseId(),purchaseInventoryDTO);
      }
      /**
       * 单据结算记录
       */
      List<PayableHistoryRecordDTO> payableHistoryRecordDTOs = supplierPayableService.getSettledRecord(shopId,OrderTypes.INVENTORY,NumberUtils.toLong(purchaseInventoryId));
      model.addAttribute("payableHistoryRecordDTOs",payableHistoryRecordDTOs);
      model.addAttribute("receiveNo",ServiceManager.getService(ITxnService.class).getStatementAccountOrderNo(shopId, purchaseInventoryDTO.getStatementAccountOrderId()));


      //供应商评价
      String firstOpen = request.getParameter(FIRST_OPEN);
      if(StringUtils.isNotEmpty(firstOpen) && Boolean.TRUE.toString().equals(firstOpen)) {
        if (NumberUtil.longValue(purchaseInventoryDTO.getPurchaseOrderId()) > 0) {
          SupplierCommentRecordDTO commentRecordDTO = null;
          PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrderById(purchaseInventoryDTO.getPurchaseOrderId());
          if (purchaseOrderDTO != null && NumberUtil.longValue(purchaseOrderDTO.getSupplierShopId()) > 0) {
            ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
            commentRecordDTO = supplierCommentService.getCommentRecordByOrderId(purchaseOrderDTO.getId(), purchaseInventoryDTO.getShopId());
          }
          if (commentRecordDTO == null) {
            purchaseInventoryDTO.setPurchaseSupplierShopId(purchaseOrderDTO.getSupplierShopId());
            purchaseInventoryDTO.setPurchaseReceiptNo(purchaseOrderDTO.getReceiptNo());
            purchaseInventoryDTO.setPurchaseOrderIdStr(purchaseOrderDTO.getId().toString());
          }
        }
      }
      LOG.debug("查看入库单--阶段5。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      LOG.debug("查看入库单，总时间：{} ms", System.currentTimeMillis() - begin);
    } catch (Exception e) {
      LOG.debug("/storage.do");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("purchaseInventoryId:" + purchaseInventoryId);
      LOG.error(e.getMessage(), e);
    }
    return "/txn/goodsStorageFinish";
  }


  @RequestMapping(params = "method=getProducts")
  public String create(ModelMap model, HttpServletRequest request) throws Exception {
    String goodsIndexFlag = request.getParameter("goodsindexflag");
    String ids = request.getParameter("productIds");
    String purchaseOrderId = request.getParameter("purchaseOrderId");         //从采购单过来的入库
    String repairOrderId = request.getParameter("repairOrderId");
    String saleOrderIdStr = request.getParameter("salesOrderId");          //从批发商销售单缺料入库
    String id = request.getParameter("productId");          //在线销售单过来的单个ProductId
    Long defaultStorehouseId = null;
    Long salesOrderId = null,repairPickingId = null;
    Long shopId = WebUtil.getShopId(request);
    if(!StringUtils.isEmpty(saleOrderIdStr)){
      salesOrderId = Long.parseLong(saleOrderIdStr);
    }
    if(NumberUtils.isNumber(request.getParameter("repairPickingId"))){
      repairPickingId = NumberUtil.toLong(request.getParameter("repairPickingId"));     //从维修领料单缺料入库
    }
    List<StoreHouseDTO> storeHouseDTOList = null;
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
    }
    Map<Long,Double> productAmountMap = new HashMap<Long, Double>();

    if(salesOrderId!=null){
      StringBuffer idBuffer = new StringBuffer();
      SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(salesOrderId);
      if(salesOrderDTO!=null){
        defaultStorehouseId = salesOrderDTO.getStorehouseId();
      }

      SalesOrderItemDTO[] salesOrderItemDTOs = salesOrderDTO.getItemDTOs();
      Set<Long> productIds = new HashSet<Long>();
      for(SalesOrderItemDTO itemDTO : salesOrderItemDTOs){
        productIds.add(itemDTO.getProductId());
      }
      Map<Long, InventoryDTO> inventoryMap = inventoryService.getInventoryDTOMap(shopId, productIds);
      //item的缺料量是实时的，需要计算
      for(int j=0;j<salesOrderItemDTOs.length;j++){
        //组装缺料信息，缺料量 = 预留字段的采购量 - 批发商库存量
        double purchaseAmount = salesOrderItemDTOs[j].getAmount();
        double reserved = salesOrderItemDTOs[j].getReserved();
        InventoryDTO inventoryDTO = inventoryMap.get(salesOrderItemDTOs[j].getProductId());
        double inventory = inventoryDTO==null?0:inventoryDTO.getAmount();
        double shortageAmount = purchaseAmount - reserved - inventory;
        if(shortageAmount>0){
          productAmountMap.put(salesOrderItemDTOs[j].getProductId(),shortageAmount);
          idBuffer.append(salesOrderItemDTOs[j].getProductId()).append(",");
        }
      }
      ids = idBuffer.toString();
      model.addAttribute("saleOrderId",salesOrderId);
    }
    String[] pids = null;
	  Result result = new Result();
    if (ids != null && goodsIndexFlag != null && "gi".equals(goodsIndexFlag))
      pids = ids.split(",");

    if(StringUtils.isNotBlank(purchaseOrderId)){
      result = ServiceManager.getService(IGoodsStorageService.class).checkPurchaseOrderInventory(shopId,purchaseOrderId);
      if(!result.isSuccess()){
        model.addAttribute("result",result);
        model.addAttribute("purchaseInventoryDTO", new PurchaseInventoryDTO());
        return "/txn/goodsStorage";
      }
    }

    RepairPickingDTO repairPickingDTO = null;
    if (repairPickingId != null) {
      repairPickingDTO = getPickingService().getRepairPickDTODById(shopId, repairPickingId);
      result = checkRepairPickingInventory(repairPickingDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result", result);
        model.addAttribute("purchaseInventoryDTO", new PurchaseInventoryDTO());
        return "/txn/goodsStorage";
      }
    }

    if (repairPickingDTO != null) {
      defaultStorehouseId = repairPickingDTO.getStorehouseId();
      pids = new String[0];
      productAmountMap = new HashMap<Long, Double>();
      pids = prepareDataFromRepairPicking(repairPickingDTO, pids, productAmountMap);
    }
    PurchaseInventoryDTO purchaseInventoryDTO = null;
    if (!model.containsKey("purchaseInventoryDTO")) {
      purchaseInventoryDTO = new PurchaseInventoryDTO();
      long curTime = System.currentTimeMillis();
      String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, curTime);
      purchaseInventoryDTO.setEditDateStr(time);
      purchaseInventoryDTO.setEditDate(curTime);
      purchaseInventoryDTO.setVestDateStr(time); //归属时间
      purchaseInventoryDTO.setVestDate(curTime);
      purchaseInventoryDTO.setShopId(shopId);
      purchaseInventoryDTO.setEditor(WebUtil.getUserName(request));
      purchaseInventoryDTO.setEditorId(WebUtil.getUserId(request));
      purchaseInventoryDTO.setAcceptor(WebUtil.getUserName(request));
      purchaseInventoryDTO.setAcceptorId(WebUtil.getUserId(request));
    } else {
      purchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    }
    purchaseInventoryDTO.setStorehouseId(defaultStorehouseId);
    if (purchaseInventoryDTO.getId() != null) {
      //获得单据应付款信息
      PayableDTO payableDTO = supplierPayableService.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
      if (payableDTO != null) {
        purchaseInventoryDTO.setStroageActuallyPaid(NumberUtil.numberValue(payableDTO.getAmount(), 0D) - NumberUtil.numberValue(payableDTO.getCreditAmount(), 0D) - NumberUtil.numberValue(payableDTO.getDeduction(), 0D));
        purchaseInventoryDTO.setStroageCreditAmount(payableDTO.getCreditAmount());
        purchaseInventoryDTO.setStroageSupplierDeduction(payableDTO.getDeduction());
      }
    }

    if("noId".equals(request.getParameter("cancle"))){
      purchaseInventoryDTO.setReceiptNo(request.getParameter("receiptNo"));
    }
    model.addAttribute("purchaseInventoryDTO", purchaseInventoryDTO);
    try {
      //从在线销售单过来的单个缺料
      if(StringUtils.isNotEmpty(id)) {
        pids = new String[1];
        pids[0] = id;
      }
      prepareDataFromRepairOrder(repairOrderId, pids, shopId, purchaseInventoryDTO, productAmountMap);

      Long supplierId = prepareDataFromPurchaseOrder(purchaseOrderId, shopId, purchaseInventoryDTO);

      //从供应商联系人出过来
      if (supplierId == null){
        if (StringUtils.isNotBlank(request.getParameter("supplierId"))
            && NumberUtils.isNumber(request.getParameter("supplierId"))){
          supplierId = new Long(request.getParameter("supplierId"));
        }
      }
      //得到供应商信息
      if (supplierId == null || supplierId <= 0) {
        if (request.getParameter("supplierName") != null && !"".equals(request.getParameter("supplierName"))) {
          SupplierDTO supplierDTO = new SupplierDTO();
          supplierDTO.setName(request.getParameter("supplierName").trim());
          supplierDTO.setContact(request.getParameter("supplierName").trim());
          supplierDTO.setShopId((Long) request.getSession().getAttribute("shopId"));
          supplierDTO = userService.createSupplier(supplierDTO);
          ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
          supplierId = supplierDTO.getId();
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
        }
      }
      if (supplierId != null) {
        SupplierDTO supplierDTO = userService.getSupplierById(supplierId);
        purchaseInventoryDTO.setSupplierDTO(supplierDTO);
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierId,shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
        //应付款总额
        Double totalPayable = doubleList.get(0);
        if(supplierDTO.getCustomerId() != null) {
            Double payable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
            model.addAttribute("totalPayable", NumberUtil.formatDouble(totalPayable - NumberUtil.doubleVal(payable)));
        } else {
            model.addAttribute("totalPayable", NumberUtil.formatDouble(totalPayable));
        }
         //应收款
          List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierId), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
          if(supplierDTO.getCustomerId() != null) {
              Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
              request.setAttribute("totalReceivable", NumberUtil.formatDouble(NumberUtil.round(0-returnList.get(0), 2) +NumberUtil.round(receivable,2)));

          }  else {
              request.setAttribute("totalReceivable", NumberUtil.formatDouble(0-returnList.get(0)));
          }
        SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(shopId,supplierId);
        if(null != supplierRecordDTO && null != supplierRecordDTO.getDebt()) {
          supplierRecordDTO.setDebt(NumberUtil.round(supplierRecordDTO.getDebt(), NumberUtil.MONEY_PRECISION));
        }
        request.setAttribute("supplierRecordDTO",supplierRecordDTO);
      }else{
        model.addAttribute("totalPayable", 0);
      }

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        if(purchaseInventoryDTO.getStorehouseId()==null){
          if(defaultStorehouseId!=null){
            purchaseInventoryDTO.setStorehouseId(defaultStorehouseId);
          }else if( CollectionUtils.isNotEmpty(storeHouseDTOList) && storeHouseDTOList.size()==1){
            purchaseInventoryDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,purchaseInventoryDTO.getStorehouseId(), purchaseInventoryDTO);
      }
      purchaseInventoryDTO.initDefaultItemUnit(ProductUnitCache.getProductUnitMap());
      Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
      model.addAttribute(MODEL_SETTLEMENTTYPE_LIST, settlementTypeList);
      Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      model.addAttribute(MODEL_INVOICECATEGORY_LIST, invoiceCategoryList);
      model.addAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return "/txn/goodsStorage";

    } catch (Exception e) {
      LOG.debug("/storage.do");
      LOG.debug("method=getProducts");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getShortageProducts")
  public String getShortageProducts(ModelMap model, HttpServletRequest request) throws Exception{
    String ids = request.getParameter("productIds");
    String purchaseOrderIdStr = request.getParameter("purchaseOrderId");         //从采购单过来的入库
    String customerShopIdStr = request.getParameter("customerShopId");

    Long defaultStorehouseId = null;
    Long purchaseOrderId = null;
    Long customerShopId = null;
    if(StringUtils.isNotEmpty(customerShopIdStr)) {
      customerShopId = NumberUtil.toLong(customerShopIdStr);
    }
    Long shopId = WebUtil.getShopId(request);
    if(shopId == null) {
      throw new Exception("shopId is null");
    }
    if(!StringUtils.isEmpty(purchaseOrderIdStr)){
      purchaseOrderId = Long.parseLong(purchaseOrderIdStr);
    }
    List<StoreHouseDTO> storeHouseDTOList = null;
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
    }
    Map<Long,Double> productAmountMap = null;

    if(purchaseOrderId != null){
      StringBuffer idBuffer = new StringBuffer();
      productAmountMap = new HashMap<Long, Double>();

      PurchaseOrderDTO purchaseOrderDTO = ServiceManager.getService(RFITxnService.class).getPurchaseOrderDTOById(purchaseOrderId, customerShopId);
      if(purchaseOrderDTO != null){
        defaultStorehouseId = purchaseOrderDTO.getStorehouseId();
      }

      PurchaseOrderItemDTO[] purchaseOrderItemDTOs = purchaseOrderDTO.getItemDTOs();
      Set<Long> productIds = new HashSet<Long>();
      for(PurchaseOrderItemDTO itemDTO : purchaseOrderItemDTOs){
        productIds.add(itemDTO.getSupplierProductId());
      }
      Map<Long, InventoryDTO> inventoryMap = inventoryService.getInventoryDTOMap(shopId, productIds);
      //item的缺料量是实时的，需要计算
      for(int j=0;j<purchaseOrderItemDTOs.length;j++){
        //组装缺料信息，缺料量 = 预留字段的采购量 - 批发商库存量
        double purchaseAmount = NumberUtil.doubleVal(purchaseOrderItemDTOs[j].getAmount());
        double reserved = NumberUtil.doubleVal(purchaseOrderItemDTOs[j].getReserved());
        InventoryDTO inventoryDTO = inventoryMap.get(purchaseOrderItemDTOs[j].getProductId());
        double inventory = inventoryDTO==null?0:NumberUtil.doubleVal(inventoryDTO.getAmount());
        double shortageAmount = purchaseAmount - reserved - inventory;
        if(shortageAmount>0){
          productAmountMap.put(purchaseOrderItemDTOs[j].getSupplierProductId(),shortageAmount);
          idBuffer.append(purchaseOrderItemDTOs[j].getSupplierProductId()).append(",");
        }
      }
      ids = idBuffer.toString();
      model.addAttribute("purchaseOrderId",purchaseOrderIdStr);
    }

    String[] pids = null;
    Result result = new Result();

  if (ids != null)
    pids = ids.split(",");
    PurchaseInventoryDTO purchaseInventoryDTO = null;
    if (!model.containsKey("purchaseInventoryDTO")) {
      purchaseInventoryDTO = new PurchaseInventoryDTO();
      long curTime = System.currentTimeMillis();
      String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, curTime);
      purchaseInventoryDTO.setEditDateStr(time);
      purchaseInventoryDTO.setEditDate(curTime);
      purchaseInventoryDTO.setVestDateStr(time); //归属时间
      purchaseInventoryDTO.setVestDate(curTime);
      purchaseInventoryDTO.setShopId(shopId);
      purchaseInventoryDTO.setEditor(WebUtil.getUserName(request));
      purchaseInventoryDTO.setEditorId(WebUtil.getUserId(request));
      purchaseInventoryDTO.setAcceptor(WebUtil.getUserName(request));
      purchaseInventoryDTO.setAcceptorId(WebUtil.getUserId(request));
    } else {
      purchaseInventoryDTO = (PurchaseInventoryDTO) model.get("purchaseInventoryDTO");
    }
    purchaseInventoryDTO.setStorehouseId(defaultStorehouseId);
    model.addAttribute("purchaseInventoryDTO", purchaseInventoryDTO);
    try {
      if (pids != null) goodsStorageService.initProductList(pids, purchaseInventoryDTO.getItemDTOs(), purchaseInventoryDTO, shopId, productAmountMap);
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        if(purchaseInventoryDTO.getStorehouseId()==null){
          if(defaultStorehouseId!=null){
            purchaseInventoryDTO.setStorehouseId(defaultStorehouseId);
          }else if( CollectionUtils.isNotEmpty(storeHouseDTOList) && storeHouseDTOList.size()==1){
            purchaseInventoryDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,purchaseInventoryDTO.getStorehouseId(), purchaseInventoryDTO);
      }
      Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
      model.addAttribute(MODEL_SETTLEMENTTYPE_LIST, settlementTypeList);
      Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      model.addAttribute(MODEL_INVOICECATEGORY_LIST, invoiceCategoryList);
      model.addAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    } catch (Exception e) {
      LOG.debug("/storage.do");
      LOG.debug("method=getShortageProducts");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/goodsStorage";
  }

  private String[] prepareDataFromRepairPicking(RepairPickingDTO repairPickingDTO, String[] pIds, Map<Long, Double> productAmountMap) {
    if (repairPickingDTO == null || CollectionUtils.isEmpty(repairPickingDTO.getPendingItemDTOs())) {
      return pIds;
    }
    Set<String> idSet = new HashSet<String>();
    for (RepairPickingItemDTO repairPickingItemDTO : repairPickingDTO.getPendingItemDTOs()) {
      double amount = NumberUtil.doubleVal(repairPickingItemDTO.getAmount());
      double inventoryAmount = NumberUtil.doubleVal(repairPickingItemDTO.getInventoryAmount());
      if (OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItemDTO.getStatus())) {
        if (repairPickingItemDTO.getProductId() == null || inventoryAmount + 0.0001 > amount) {
          continue;
        }
        idSet.add(repairPickingItemDTO.getProductId().toString());
        productAmountMap.put(repairPickingItemDTO.getProductId(), amount - inventoryAmount);
      }
    }
    if (CollectionUtils.isNotEmpty(idSet)) {
      pIds = idSet.toArray(new String[idSet.size()]);
    }
    return pIds;
  }

  private Result checkRepairPickingInventory(RepairPickingDTO repairPickingDTO) {
    Result result = new Result();
    if (repairPickingDTO == null) {
      result.setSuccess(false);
      result.setMsg("当前的施工领料单不存在，无法入库！");
      result.setOperation(Result.Operation.ALERT.getValue());
      return result;
    }
    if (repairPickingDTO != null) {
      if (OrderStatus.REPEAL.equals(repairPickingDTO.getStatus())) {
        result.setSuccess(false);
        result.setMsg("当前的施工领料单关联的施工单已经作废，无法入库！");
        result.setOperation(Result.Operation.ALERT.getValue());
        return result;
      }
      //判断是否需要缺料入库
      boolean isNeedLackInventory = false;
      if (CollectionUtils.isNotEmpty(repairPickingDTO.getPendingItemDTOs())) {
        for (RepairPickingItemDTO repairPickingItemDTO : repairPickingDTO.getPendingItemDTOs()) {
          if (repairPickingItemDTO.getIsLack()) {
            isNeedLackInventory = true;
            break;
          }
        }
      }
      if (!isNeedLackInventory) {
        result.setSuccess(false);
        result.setMsg("当前的施工领料单无缺料项目，无法入库！");
        result.setOperation(Result.Operation.ALERT.getValue());
        return result;
      }
    } else {
      result.setSuccess(true);
    }
    return result;
  }

  /**
   * 采购单跳转过来的数据准备
   *
   * @param purchaseOrderId
   * @param shopId
   * @param purchaseInventoryDTO
   * @return
   * @throws Exception
   */
  private Long prepareDataFromPurchaseOrder(String purchaseOrderId, Long shopId, PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    Long supplierId = null;
    //得到采购单货品表
    if (StringUtils.isNotEmpty(purchaseOrderId)) {
      PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrder(Long.valueOf(purchaseOrderId), shopId);
      supplierId = purchaseOrderDTO.getSupplierId();
      purchaseInventoryDTO.setContactId(purchaseOrderDTO.getContactId());
      purchaseInventoryDTO.setTotal(purchaseOrderDTO.getTotal());
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[purchaseOrderDTO.getItemDTOs().length];

      if(purchaseOrderDTO.getSupplierShopId()!=null){
        double orderTotal = 0d;
        purchaseInventoryDTO.setItemDTOs(itemDTOs);
        purchaseInventoryDTO.setPromotionsInfoJson(purchaseOrderDTO.getPromotionsInfoJson());
        for (int i = 0; i < purchaseOrderDTO.getItemDTOs().length; i++) {
          PurchaseOrderItemDTO purchaseOrderItemDTO = purchaseOrderDTO.getItemDTOs()[i];
          itemDTOs[i] = new PurchaseInventoryItemDTO();
          itemDTOs[i].setSupplierProductId(purchaseOrderItemDTO.getSupplierProductId());
          itemDTOs[i].setAmount(purchaseOrderItemDTO.getAmount());
          itemDTOs[i].setMemo(purchaseOrderItemDTO.getMemo());
          itemDTOs[i].setPrice(purchaseOrderItemDTO.getPrice());
          itemDTOs[i].setPurchasePrice(purchaseOrderItemDTO.getPrice());
          itemDTOs[i].setTotal(NumberUtil.round(purchaseOrderItemDTO.getAmount() * NumberUtil.doubleVal(purchaseOrderItemDTO.getPrice()),2));
          orderTotal+=itemDTOs[i].getTotal();
          //
          List<ProductDTO> productDTOList = productService.getProductDTOsBy7P(shopId,new ProductDTO(shopId, purchaseOrderItemDTO));
          if(CollectionUtils.isNotEmpty(productDTOList)){
            ProductDTO productDTO = productService.getProductById(productDTOList.get(0).getId(), shopId);
            //填充 营业分类
            if (productDTO != null && productDTO.getBusinessCategoryId() != null) {
              Category category = getRfiTxnService().getCategoryById(shopId, productDTO.getBusinessCategoryId());
              if (category != null) {
                productDTO.setBusinessCategoryName(category.getCategoryName());
              }
            }
            if (StringUtils.isBlank(itemDTOs[i].getUnit()) && StringUtils.isNotBlank(productDTO.getSellUnit())) {
              itemDTOs[i].setUnit(productDTO.getSellUnit());
            }
            itemDTOs[i].setProductDTOWithOutUnit(productDTO);
            InventoryDTO inventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productDTO.getProductLocalInfoId());
            itemDTOs[i].setInventoryAmount(inventoryDTO.getAmount());
            itemDTOs[i].setRecommendedPrice(inventoryDTO.getSalesPrice());
            itemDTOs[i].setLowerLimit(inventoryDTO.getLowerLimit());
            itemDTOs[i].setUpperLimit(inventoryDTO.getUpperLimit());

          }else{
            //
            itemDTOs[i].setProductName(purchaseOrderItemDTO.getProductName());
            itemDTOs[i].setBrand(purchaseOrderItemDTO.getBrand());
            itemDTOs[i].setModel(purchaseOrderItemDTO.getModel());
            itemDTOs[i].setSpec(purchaseOrderItemDTO.getSpec());
            itemDTOs[i].setVehicleModel(purchaseOrderItemDTO.getVehicleModel());
            itemDTOs[i].setVehicleBrand(purchaseOrderItemDTO.getVehicleBrand());
            ProductDTO productDTO = productService.getProductDTOByCommodityCode(shopId,purchaseOrderItemDTO.getCommodityCode());
            if(productDTO==null){
              itemDTOs[i].setCommodityCode(purchaseOrderItemDTO.getCommodityCode());
            }
            itemDTOs[i].setUnit(purchaseOrderItemDTO.getUnit());
          }
        }
        orderTotal = NumberUtil.round(orderTotal, NumberUtil.MONEY_PRECISION); //保留两位小数
        purchaseInventoryDTO.setTotal(orderTotal);
        purchaseInventoryDTO.setStroageCreditAmount(orderTotal);
      }else{
        String[] prodIds = new String[purchaseOrderDTO.getItemDTOs().length];
        purchaseInventoryDTO.setItemDTOs(itemDTOs);
        for (int i = 0; i < purchaseOrderDTO.getItemDTOs().length; i++) {
          PurchaseOrderItemDTO item = purchaseOrderDTO.getItemDTOs()[i];
          itemDTOs[i] = new PurchaseInventoryItemDTO();
          itemDTOs[i].setAmount(item.getAmount());
          itemDTOs[i].setMemo(item.getMemo());
          itemDTOs[i].setPrice(item.getPrice());
          itemDTOs[i].setPurchasePrice(item.getPrice());
          if(item.getPrice()!=null){
            itemDTOs[i].setTotal(NumberUtil.round(item.getAmount() * item.getPrice()));
          }
          itemDTOs[i].setUnit(item.getUnit());
          prodIds[i] = String.valueOf(item.getProductId());
        }
        if (null != prodIds) {
          goodsStorageService.initProductList(prodIds, itemDTOs, purchaseInventoryDTO, shopId, null);
        }
      }

    }
    return supplierId;
  }

  /**
   * 维修单缺料跳转过来时的数据准备
   *
   * @param repairOrderId
   * @param pids
   * @param shopId
   * @param purchaseInventoryDTO
   * @param productAmountMap 缺料item的productId对应的缺料数量
   * @throws Exception
   */
  private void prepareDataFromRepairOrder(String repairOrderId, String[] pids, Long shopId, PurchaseInventoryDTO purchaseInventoryDTO, Map<Long,Double> productAmountMap) throws Exception {
    if (pids != null) goodsStorageService.initProductList(pids, null, purchaseInventoryDTO, shopId, productAmountMap);

      if (StringUtils.isNotEmpty(repairOrderId) && pids == null) {
        List<RepairRemindEvent> repairRemindEvents = txnService.getLackProductIdsByRepairOderId(
            Long.parseLong(repairOrderId), shopId, RepairRemindEventTypes.LACK);
        if (null != repairRemindEvents && repairRemindEvents.size() > 0) {
          String[] productIds1 = new String[repairRemindEvents.size()];
          for (int i = 0; i < repairRemindEvents.size(); i++) {
            productIds1[i] = String.valueOf(repairRemindEvents.get(i).getProductId());
          }
        LOG.debug("RepairRemindEvent Ids:{}", repairRemindEvents);
          goodsStorageService.initProductList(productIds1, null, purchaseInventoryDTO, shopId, null);
        }
        RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(Long.parseLong(repairOrderId));
        RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
        PurchaseInventoryItemDTO[] purchaseInventoryItemDTOs = purchaseInventoryDTO.getItemDTOs();

        double orderTotal = 0.0;
        //维修单缺料过来，根据维修单单位更新入库单显示状况
        if (repairOrderItemDTOs != null) {
          for (int i = 0; i < repairOrderItemDTOs.length; i++) {
            if (purchaseInventoryItemDTOs != null) {
              for (int j = 0; j < purchaseInventoryItemDTOs.length; j++) {
                if (repairOrderItemDTOs[i].getProductId().equals(purchaseInventoryItemDTOs[j].getProductId())) {
                  purchaseInventoryItemDTOs[j].setAmount(repairOrderItemDTOs[i].getAmount());
                  purchaseInventoryItemDTOs[j].setUnit(repairOrderItemDTOs[i].getUnit());


                if (purchaseInventoryItemDTOs[j].getAmount() != null && purchaseInventoryItemDTOs[j].getPurchasePrice() != null) {
                  double totalDouble = purchaseInventoryItemDTOs[j].getAmount() * purchaseInventoryItemDTOs[j].getPurchasePrice();
                  totalDouble = NumberUtil.round(totalDouble, NumberUtil.MONEY_PRECISION); //保留两位小数
                  purchaseInventoryItemDTOs[j].setTotal(totalDouble);
                  orderTotal += totalDouble;
                  }

                  ProductDTO productDTO = productService.getProductByProductLocalInfoId(repairOrderItemDTOs[i].getProductId(), shopId);

                  String kindName = null;

                  if(null != productDTO && null != productDTO.getKindId()) {
                    KindDTO kindDTO = productService.getEnabledKindDTOById(shopId, productDTO.getKindId());
                    if (null != kindDTO) {
                      kindName = kindDTO.getName();
                    }
                  }

                  purchaseInventoryItemDTOs[j].setProductKind(kindName);

                  if (UnitUtil.isStorageUnit(purchaseInventoryItemDTOs[j].getUnit(), productDTO)) {     //维修单单位为库存大单位时，需要更新采购价，入库价，库存数量,库存上下限
                  purchaseInventoryItemDTOs[j].setPurchasePrice(purchaseInventoryItemDTOs[j].getPurchasePrice() == null ? 0d : purchaseInventoryItemDTOs[j].getPurchasePrice() * productDTO.getRate());
                  purchaseInventoryItemDTOs[j].setInventoryAmount(purchaseInventoryItemDTOs[j].getInventoryAmount() == null ? 0d : purchaseInventoryItemDTOs[j].getInventoryAmount() / productDTO.getRate());
                  purchaseInventoryItemDTOs[j].setPrice(purchaseInventoryItemDTOs[j].getPrice() == null ? 0d : purchaseInventoryItemDTOs[j].getPrice() * productDTO.getRate());
                  purchaseInventoryItemDTOs[j].setRecommendedPrice(purchaseInventoryItemDTOs[j].getRecommendedPrice() == null ? 0d : purchaseInventoryItemDTOs[j].getRecommendedPrice() * productDTO.getRate());
                  purchaseInventoryItemDTOs[j].setLowerLimit(purchaseInventoryItemDTOs[j].getLowerLimit() == null ? null : purchaseInventoryItemDTOs[j].getLowerLimit() / productDTO.getRate());
                  purchaseInventoryItemDTOs[j].setUpperLimit(purchaseInventoryItemDTOs[j].getUpperLimit() == null ? null : purchaseInventoryItemDTOs[j].getUpperLimit() / productDTO.getRate());
                }
              }
            }
          }
        }
      }
      orderTotal = NumberUtil.round(orderTotal, NumberUtil.MONEY_PRECISION); //保留两位位小数
      purchaseInventoryDTO.setTotal(orderTotal);
      purchaseInventoryDTO.setStroageCreditAmount(orderTotal);
      purchaseInventoryDTO.setStorehouseId(repairOrderDTO.getStorehouseId());
    }
  }

  private PurchaseInventoryDTO removeBlankArrayOfPurchaseInventory(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    if (purchaseInventoryDTO == null) return purchaseInventoryDTO;
    PurchaseInventoryItemDTO[] purchaseInventoryItemDTOsOld = purchaseInventoryDTO.getItemDTOs();
    if (ArrayUtils.isEmpty(purchaseInventoryItemDTOsOld)) return purchaseInventoryDTO;
    List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList = new ArrayList<PurchaseInventoryItemDTO>();
    for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryItemDTOsOld) {
      if (StringUtils.isBlank(purchaseInventoryItemDTO.getProductName())) continue;
      purchaseInventoryItemDTOList.add(purchaseInventoryItemDTO);
    }
    purchaseInventoryDTO.setItemDTOs(purchaseInventoryItemDTOList.toArray(new PurchaseInventoryItemDTO[purchaseInventoryItemDTOList.size()]));
    return purchaseInventoryDTO;
  }

	@RequestMapping(params = "method=ajaxValidatorPurchaseInventoryDTOSave")
	@ResponseBody
	public Object ajaxValidatorPurchaseInventoryDTOSave(HttpServletRequest request, PurchaseInventoryDTO purchaseInventoryDTO) {
		try {
			Long shopId = WebUtil.getShopId(request);
			if (purchaseInventoryDTO == null) {
				return  new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
			}

      if (NumberUtil.doubleVal(purchaseInventoryDTO.getDepositAmount()) > 0 && NumberUtil.longValue(purchaseInventoryDTO.getSupplierId()) > 0) {
        DepositDTO depositDTO = getSupplierPayableService().getDepositBySupplierId(shopId, purchaseInventoryDTO.getSupplierId());
        if (depositDTO == null || NumberUtil.isGreater(purchaseInventoryDTO.getDepositAmount(), depositDTO.getActuallyPaid())) {
          Result result = new Result(false);
          result.setMsg(ValidatorConstant.DEPOSIT_LACK);
          result.setOperation("deposit_lack");
          return result;
        }
      }

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        if (purchaseInventoryDTO.getStorehouseId() != null) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,purchaseInventoryDTO.getStorehouseId());
          if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
            return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
          }
        }else{
          return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
        }
      }

			purchaseInventoryDTO.setShopId(shopId);
			return getRfiTxnService().getDeletedProductValidatorResult(purchaseInventoryDTO);
		} catch (Exception e) {
			LOG.error("ajaxValidatorPurchaseInventoryDTOSave 验证出错bcgogoOrderDto:{}" + e.getMessage(), purchaseInventoryDTO, e);
			return  new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
		}
	}

  @RequestMapping(params = "method=savePurchaseInventory")
  public String savePurchaseInventory(ModelMap model, PurchaseInventoryDTO purchaseInventoryDTO, HttpServletRequest request, HttpServletResponse response) {
    SalesOrderDTO salesOrderDTO = null;
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    LOG.info("保存入库单开始!,shopId:{}",shopId);
    try {
      //组装入库单的静态信息
      prepareDataForSavePurchaseInventory(request, purchaseInventoryDTO);
      //校验逻辑
      if (checkPurchaseInventoryDTO(purchaseInventoryDTO)) {
        LOG.warn("开始执行入库时，数据异常，入库信息：purchaseInventoryDTO 为空！");
        return create(model, request);
      }
      if (purchaseInventoryDTO.getPurchaseOrderId() != null) {
        salesOrderDTO = getGoodSaleService().getSimpleSalesOrderByPurchaseOrderId(purchaseInventoryDTO.getPurchaseOrderId());
        if (salesOrderDTO != null) {
          if (!(BcgogoConcurrentController.lock(ConcurrentScene.PURCHASE, purchaseInventoryDTO.getPurchaseOrderId()) &&
              BcgogoConcurrentController.lock(ConcurrentScene.SALE, salesOrderDTO.getId()))) {
            Result result = new Result("当前单据正在被操作，请稍候再试", false);
            model.addAttribute("result", result);
            return "/txn/goodsStorage";
          }
        }
        //校验入库单关联的采购单状态
        Result result = ServiceManager.getService(IGoodsStorageService.class).checkPurchaseOrderInventory(shopId, purchaseInventoryDTO.getPurchaseOrderId().toString());
        if (!result.isSuccess()) {
          model.addAttribute("result", result);
          model.addAttribute("purchaseInventoryDTO", purchaseInventoryDTO);
          return "/txn/goodsStorage";
        }
      }
      //单据号
      if (StringUtils.isBlank(purchaseInventoryDTO.getReceiptNo())) {
        purchaseInventoryDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.INVENTORY, null));
      }
      //处理商品信息
      goodsStorageService.handleProductForSavePurchaseInventory(purchaseInventoryDTO);

//      //入库单 所填车辆若为新车型，则新增，并将ID保存到此入库单
//      rfiTxnService.populatePurchaseInventoryDTO(purchaseInventoryDTO);

      //处理供应商信息
      SupplierDTO supplierDTO = goodsStorageService.handleSupplierForGoodsStorage(purchaseInventoryDTO);

      purchaseInventoryDTO.setStatus(OrderStatus.PURCHASE_INVENTORY_DONE);

      //校验缺料
      checkLackMaterial(purchaseInventoryDTO, shopId, purchaseInventoryDTO.getShopVersionId());

      //保存单据信息
      purchaseInventoryDTO = goodsStorageService.createOrUpdatePurchaseInventory(shopId,purchaseInventoryDTO.getShopVersionId(), purchaseInventoryDTO);

      //ad by WLF 保存入库单的创建日志
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(shopId,userId,
          purchaseInventoryDTO.getId(), ObjectTypes.INVENTORY_ORDER, OperationTypes.CREATE));

      //如果是从采购单过来的入库，那么要删除待入库
      if (purchaseInventoryDTO.getPurchaseOrderId() != null) {
        PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrder(purchaseInventoryDTO.getPurchaseOrderId(), shopId);
        if (purchaseOrderDTO.isWholesalerPurchase()) {
          purchaseInventoryDTO.setSupplierShopId(purchaseOrderDTO.getSupplierShopId());
          ServiceManager.getService(IProductService.class).saveOrUpdateProductMapping(purchaseInventoryDTO);
        }
        txnService.deleteInventoryRemindEventByShopIdAndPurchaseOrderId(shopId, purchaseInventoryDTO.getPurchaseOrderId());
        txnService.cancelRemindEventByOrderId(RemindEventType.TXN, purchaseInventoryDTO.getPurchaseOrderId());
        itemIndexService.updateItemIndexPurchaseOrderStatus(purchaseInventoryDTO.getShopId(), OrderTypes.PURCHASE,purchaseInventoryDTO.getPurchaseOrderId(), OrderStatus.PURCHASE_ORDER_DONE);
        txnService.updatePurchaseOrderStatus(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getPurchaseOrderId(),OrderStatus.PURCHASE_ORDER_DONE, userId, purchaseInventoryDTO.getVestDate());
        //add by WLF 保存采购单的入库操作票日志
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(shopId, userId,purchaseInventoryDTO.getPurchaseOrderId(), ObjectTypes.PURCHASE_ORDER, OperationTypes.STORAGE));
      }
      //入库后 草稿单作废
      if (StringUtils.isNotBlank(purchaseInventoryDTO.getDraftOrderIdStr())) {
        ServiceManager.getService(IDraftOrderService.class).deleteDraftOrder(purchaseInventoryDTO.getShopId(), NumberUtil.longValue(purchaseInventoryDTO.getDraftOrderIdStr()));
      }
      //更新供应商信息
      updateSupplier(supplierDTO, purchaseInventoryDTO);
      //todo:增加供应商应付款
      PayableDTO payableDTO = inventoryService.savePayableFromPurchaseInventoryDTO(purchaseInventoryDTO);
      //todo:对应付款进行结算
      supplierPayableService.payedToSupplier(payableDTO, purchaseInventoryDTO, PaymentTypes.INVENTORY);
      //更新supplier_record中应付款数额
      ServiceManager.getService(SupplierRecordService.class).updateSupplierRecordCreditAmount(purchaseInventoryDTO.getShopId(), supplierDTO);
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseInventorySavedEvent purchaseInventorySavedEvent = new PurchaseInventorySavedEvent(purchaseInventoryDTO);
      String isRunThread = request.getParameter("isRunThread");
      if (!"noRun".equals(isRunThread)) {
        bcgogoEventPublisher.publisherPurchaseInventorySaved(purchaseInventorySavedEvent);
      }
      //单元测试
      request.setAttribute("UNIT_TEST", purchaseInventorySavedEvent);
      purchaseInventorySavedEvent.setMainFlag(true);
      //用户引导
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, new String[]{"PRODUCT_ONLINE_GUIDE_INVENTORY"});
    } catch (Exception e) {
      LOG.debug("/storage.do");
      LOG.debug("method=savePurchaseInventory 出错：purchaseInventoryDTO：{}", purchaseInventoryDTO);
      LOG.debug("shopId:" + shopId+ ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    } finally {
      if (purchaseInventoryDTO.getPurchaseOrderId() != null) {
        BcgogoConcurrentController.release(ConcurrentScene.PURCHASE, purchaseInventoryDTO.getPurchaseOrderId());
      }
      if (salesOrderDTO != null && salesOrderDTO.getId() != null) {
        BcgogoConcurrentController.release(ConcurrentScene.SALE, salesOrderDTO.getId());
      }
    }

    model.addAttribute("purchaseInventoryDTO", purchaseInventoryDTO);
    if (StringUtils.isNotEmpty(purchaseInventoryDTO.getReturnType())) {
      if (null != purchaseInventoryDTO.getSupplierId()) {
        Double sumPayable = supplierPayableService.getSumDepositBySupplierId(purchaseInventoryDTO.getSupplierId(), purchaseInventoryDTO.getShopId());
        sumPayable = NumberUtil.toReserve(sumPayable, NumberUtil.MONEY_PRECISION);
        request.setAttribute("sumPayable", sumPayable);
      }
      model.addAttribute("returnType", purchaseInventoryDTO.getReturnType());
      model.addAttribute("purchaseInventoryId", purchaseInventoryDTO.getId());
      if (CollectionUtils.isNotEmpty(purchaseInventoryDTO.getReturnProductIds())) {
        model.addAttribute("productIds", purchaseInventoryDTO.getReturnProductIds().get(0));
      }
      model.addAttribute("repairOrderId", purchaseInventoryDTO.getRepairOrderId());
      return REDIRECT_SHOW + "&" + FIRST_OPEN + "=true";
    } else {
      model.addAttribute("purchaseInventoryId", purchaseInventoryDTO.getId());
      String print = request.getParameter("print");
      if (StringUtils.isNotBlank(print)) {
        model.addAttribute("print", print);
      }
      return REDIRECT_SHOW + "&" + FIRST_OPEN + "=true";
    }
  }

  //组装入库单的静态信息
  private void  prepareDataForSavePurchaseInventory(HttpServletRequest request,PurchaseInventoryDTO purchaseInventoryDTO)throws Exception{
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long shopVersionId = WebUtil.getShopVersionId(request);
    Long userId = WebUtil.getUserId(request);
    String username = WebUtil.getUserName(request);

    if (shopId == null) throw new Exception("shop Id is null");
    if (purchaseInventoryDTO == null) {
      purchaseInventoryDTO = new PurchaseInventoryDTO();
    }
    String repairOrderId = request.getParameter("repairOrderId");         //从缺料入单过来的入库
    String productAmount = request.getParameter("productAmount");         //维修单材料使用量
    purchaseInventoryDTO.setRepairOrderId(repairOrderId);
    purchaseInventoryDTO.setProductAmount(productAmount);
    purchaseInventoryDTO.setUserId(userId);
    purchaseInventoryDTO.setUserName(username);
    purchaseInventoryDTO.setShopId(shopId);
    purchaseInventoryDTO.setShopVersionId(shopVersionId);
    purchaseInventoryDTO.setInventoryLimitDTO(new InventoryLimitDTO());
    purchaseInventoryDTO.getInventoryLimitDTO().setShopId(purchaseInventoryDTO.getShopId());
    purchaseInventoryDTO = removeBlankArrayOfPurchaseInventory(purchaseInventoryDTO);
    //制单日期
    purchaseInventoryDTO.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, purchaseInventoryDTO.getEditDateStr()));
    //归属时间
    // 如果归属时间为空
    if (StringUtils.isBlank(purchaseInventoryDTO.getVestDateStr())) {
      purchaseInventoryDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, System.currentTimeMillis()));
    }
    purchaseInventoryDTO.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, purchaseInventoryDTO.getVestDateStr()));

    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
      StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, purchaseInventoryDTO.getStorehouseId());
      purchaseInventoryDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
    }
  }

  /**
   * 如果是有仓库的版本   校验对应仓库的
   * @param purchaseInventoryDTO
   * @param shopId
   * @param shopVersionId
   * @throws Exception
   */
  private void checkLackMaterial(PurchaseInventoryDTO purchaseInventoryDTO, Long shopId, Long shopVersionId) throws Exception {
    boolean hasFlag = false;
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    Set<Long> productIds = purchaseInventoryDTO.getProductIdSet();
    Map<Long,InventoryDTO> inventoryDTOMap = new HashMap<Long, InventoryDTO>();
    Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    Map<Long,List<LackMaterialDTO>> lackMaterialMap = new HashMap<Long, List<LackMaterialDTO>>();
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
      storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(
          shopId,purchaseInventoryDTO.getStorehouseId(),productIds.toArray(new Long[productIds.size()]));
      lackMaterialMap = txnService.getLackMaterialMapByProductIdsAndStorehouse(shopId,RepairRemindEventTypes.LACK,
          productIds,purchaseInventoryDTO.getStorehouseId());
    }else {
      inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId,productIds);
      lackMaterialMap = txnService.getLackMaterialMapByProductIds(shopId,RepairRemindEventTypes.LACK,productIds);
    }
    StoreHouseInventoryDTO storeHouseInventoryDTO = null;
    for (int i = 0; i < purchaseInventoryDTO.getItemDTOs().length; i++) {
      PurchaseInventoryItemDTO itemDTO = purchaseInventoryDTO.getItemDTOs()[i];
      if (itemDTO != null && itemDTO.getProductId() != null) {
        InventoryDTO inventoryDTO = inventoryDTOMap.get(itemDTO.getProductId());
        double totalAmountWithSellUnit = (inventoryDTO == null ? 0d : NumberUtil.doubleVal(inventoryDTO.getAmount()));
        List<LackMaterialDTO> lackMaterialDTOs = lackMaterialMap.get(itemDTO.getProductId());;
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
          storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(itemDTO.getProductId());
          totalAmountWithSellUnit = storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount();
        }
        if (CollectionUtils.isNotEmpty(lackMaterialDTOs)) {
          ProductDTO productDTO = productService.getProductByProductLocalInfoId(itemDTO.getProductId(), shopId);
          for (LackMaterialDTO lackMaterialDTO : lackMaterialDTOs) {
            double lackMaterialDTOAmountWithSellUnit = 0d;
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {   //itemDTO 单位为库存大单位
              totalAmountWithSellUnit = totalAmountWithSellUnit + itemDTO.getAmount() * productDTO.getRate();
            } else {
              totalAmountWithSellUnit = totalAmountWithSellUnit + itemDTO.getAmount();
            }

            if (UnitUtil.isStorageUnit(lackMaterialDTO.getUnit(), productDTO)) {     //lackMaterialDTO 单位为库存大单位
              lackMaterialDTOAmountWithSellUnit = lackMaterialDTO.getAmount() * productDTO.getRate();
            } else {
              lackMaterialDTOAmountWithSellUnit = lackMaterialDTO.getAmount();
            }

            if (lackMaterialDTOAmountWithSellUnit < totalAmountWithSellUnit + 0.0001) {
              hasFlag = true;
              purchaseInventoryDTO.getReturnProductIds().add(itemDTO.getProductId());
              purchaseInventoryDTO.setReturnIndex(String.valueOf(i));
              break;
            }
          }
        }
      }
    }
    if (hasFlag) {
      purchaseInventoryDTO.setReturnType("3");
    } else {
      purchaseInventoryDTO.setReturnType("");
    }
  }

  private boolean checkPurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO) {
    boolean isEmpty = true;
    if (purchaseInventoryDTO == null) {
      return isEmpty;
    }

    if (ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())) {
      return isEmpty;
    }
    for (PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()) {
      if (itemDTO == null) {
        continue;
      }
      if (StringUtils.isNotBlank(itemDTO.getProductName())) {
        isEmpty = false;
        break;
      }
    }
    return isEmpty;
  }

  private void updateSupplier(SupplierDTO supplierDTO, PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    String products = purchaseInventoryDTO.getItemDTOs()[0].getProductName();
    //如果 最后入库时间>结算时间 默认不变
    SupplierDTO supplier = userService.getSupplierById(supplierDTO.getId());
    supplierDTO.setSupplierShopId(supplier.getSupplierShopId());
    supplierDTO.setLastInventoryTime(System.currentTimeMillis());
    userService.updateSupplier(supplierDTO, purchaseInventoryDTO.getId(), OrderTypes.INVENTORY,
        products, purchaseInventoryDTO.getTotal());
    if (supplierDTO.getCustomerId() != null) {
      //同时更新客户的信息
      CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(supplierDTO.getCustomerId());
      customerDTO.fromSupplierDTO(supplierDTO);
      ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
      CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId()).get(0);
      customerRecordDTO.fromCustomerDTO(customerDTO);
      ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
    }
  }

  /*
  * 显示缺料的维修单根据productId
  */
  @RequestMapping(params = "method=showLackGoods")        //, double inventoryAmount, String unit
  public String showLackGoods(ModelMap model, HttpServletRequest request, Long productIds) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    try {
      List<LackMaterialDTO> lackMaterialDTOs = txnService.getLackMaterialByProductId(shopId, RepairRemindEventTypes.LACK, productIds);
      ProductDTO productDTO = productService.getProductByProductLocalInfoId(productIds, shopId);
	    InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopId,productIds);
      boolean hasFlag = false;
      double inventoryAmountBySellUnit = inventoryDTO.getAmount();
//      if (UnitUtil.isStorageUnit(unit, productDTO)) {
//        inventoryAmountBySellUnit = inventoryAmount * productDTO.getRate();
//      } else {
//        inventoryAmountBySellUnit = inventoryAmount;
//      }
      for (LackMaterialDTO lackMaterialDTO : lackMaterialDTOs) {
        double lackMaterialDTOBySellUnit = 0d;
        if (UnitUtil.isStorageUnit(lackMaterialDTO.getUnit(), productDTO)) {
          lackMaterialDTOBySellUnit = lackMaterialDTO.getAmount() * productDTO.getRate();
        } else {
          lackMaterialDTOBySellUnit = lackMaterialDTO.getAmount();
        }
        if (lackMaterialDTOBySellUnit < inventoryAmountBySellUnit + 0.0001d) {
          hasFlag = true;
          break;
        }

      }


      if (null != lackMaterialDTOs && lackMaterialDTOs.size() > 0 && hasFlag) {
        model.addAttribute("lackMaterialDTOs", lackMaterialDTOs);
        model.addAttribute("inventoryAmount", inventoryAmountBySellUnit);
        model.addAttribute("sellUnit", productDTO.getSellUnit());
        model.addAttribute("storageUnit", productDTO.getStorageUnit());
        model.addAttribute("rate", productDTO.getRate());
        return "/txn/incomingRepair";
      }
    } catch (Exception e) {
      LOG.debug("/storage.do");
      LOG.debug("method=showLackGoods");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("productId:" + productIds );
      LOG.error(e.getMessage(), e);
    }
    return "/txn/incomingRepair";
  }

  /*
  * 更新缺料入库 (页面进入)
  */
  @RequestMapping(params = "method=updateLackGood")
  @ResponseBody
  public Map updateLackGood(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                               Long[] repairOrderId, Long productIds,
                               Double[] productAmount, String returnType) throws Exception {
    if (null != repairOrderId && !ArrayUtils.isEmpty(productAmount)) {
      for (int i = 0; i < repairOrderId.length; i++) {
        doUpdateLackGood(request, repairOrderId[i], productIds, productAmount[i]);
      }
    }
    Map<String, String> result= new HashMap<String, String>();
    if (StringUtils.isNotEmpty(returnType) && returnType.equals("1") &&repairOrderId !=null && repairOrderId.length>0){
      result.put("result", "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + repairOrderId[0]);
      return result;
    } else {
      result.put("result", "success");
      return result;
    }
  }

  /*
  * 更新缺料入库
  */
  private void doUpdateLackGood(HttpServletRequest request, Long repairOrderId, Long productIds, Double productAmount) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    try {
      InventoryDTO inventory = txnService.getInventoryByShopIdAndProductId(shopId, productIds);
      if (null != inventory) {
        txnService.updateRepairRemindEventByShopIdAndTypeAndProductId(shopId, RepairRemindEventTypes.LACK,
            productIds, RepairRemindEventTypes.INCOMING, repairOrderId);
        //update inventory
        txnService.createOrUpdateInventory(inventory);
        //add by WLF 更新缓存中维修美容提醒数量
        txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR,shopId);
      }
    } catch (Exception e) {
      LOG.debug("/storage.do");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("repairOrderId:" + repairOrderId + ",productId:" + productIds + ",productAmount:" + productAmount);
      LOG.error(e.getMessage(), e);
    }
  }

  /*
  *入库单作废
   */
  @RequestMapping(params = "method=cancelPurchaseInventory")
  public String cancelPurchaseInventory(ModelMap model, HttpServletRequest request, Long purchaseInventoryId) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      if (purchaseInventoryId == null || ((Long) 0L).equals(purchaseInventoryId)) {
        return create(model, request);
      }
      PurchaseInventoryDTO purchaseInventoryDTO = goodsStorageService.getPurchaseInventory(purchaseInventoryId, shopId);
      if (purchaseInventoryDTO == null) {
        return create(model, request);
      }
      if(purchaseInventoryDTO.getStatementAccountOrderId() != null){
        LOG.error("storage.do cancelPurchaseInventory,单据已冲账" + purchaseInventoryId);
        return create(model,request);
      }
	    PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
      //得到供应商信息
      SupplierDTO supplierDTO = userService.getSupplierById(purchaseInventoryDTO.getSupplierId());

      purchaseInventoryDTO.setShopVersionId(shopVersionId);
      purchaseInventoryDTO.setSupplier(supplierDTO.getName());
      purchaseInventoryDTO.setContact(supplierDTO.getContact());
      purchaseInventoryDTO.setMobile(supplierDTO.getMobile());
      purchaseInventoryDTO.setAddress(supplierDTO.getAddress());

      purchaseInventoryDTO.setAccount(supplierDTO.getAccount());
      purchaseInventoryDTO.setBank(supplierDTO.getBank());
      purchaseInventoryDTO.setAccountName(supplierDTO.getAccountName());
      purchaseInventoryDTO.setCategory(StringUtil.nullToObject(supplierDTO.getCategory()));
      purchaseInventoryDTO.setAbbr(supplierDTO.getAbbr());
      purchaseInventoryDTO.setSettlementType(StringUtil.nullToObject(supplierDTO.getSettlementTypeId()));
      purchaseInventoryDTO.setLandline(supplierDTO.getLandLine());
      purchaseInventoryDTO.setFax(supplierDTO.getFax());
      purchaseInventoryDTO.setQq(supplierDTO.getQq());
      purchaseInventoryDTO.setInvoiceCategory(StringUtil.nullToObject(supplierDTO.getInvoiceCategoryId()));
      purchaseInventoryDTO.setBusinessScope(supplierDTO.getBusinessScope());

      purchaseInventoryDTO.setInventoryLimitDTO(new InventoryLimitDTO());
      purchaseInventoryDTO.getInventoryLimitDTO().setShopId(shopId);


      if (OrderStatus.PURCHASE_INVENTORY_REPEAL == purchaseInventoryDTO.getStatus()) {
        model.put("purchaseInventoryDTO", purchaseInventoryDTO);
	      model.addAttribute("purchaseInventoryId", purchaseInventoryDTO.getId());
	      return REDIRECT_SHOW;
      }
	    if (purchaseInventoryDTO.getPurchaseOrderId() != null) {
		    purchaseOrderDTO = txnService.getPurchaseOrder(purchaseInventoryDTO.getPurchaseOrderId(), shopId);
		    if (purchaseOrderDTO != null && OrderStatus.PURCHASE_ORDER_DONE == purchaseOrderDTO.getStatus()) {
			    SalesOrderDTO salesOrderDTO = getGoodSaleService().getSimpleSalesOrderByPurchaseOrderId(purchaseOrderDTO.getId());
			    if (salesOrderDTO == null) {
				    purchaseOrderDTO.setStatus(OrderStatus.PURCHASE_ORDER_WAITING);
			    } else if (OrderStatus.STOCKING.equals(salesOrderDTO.getStatus())) {
				    purchaseOrderDTO.setStatus(OrderStatus.SELLER_STOCK);
			    } else if (OrderStatus.DISPATCH.equals(salesOrderDTO.getStatus())) {
				    purchaseOrderDTO.setStatus(OrderStatus.SELLER_DISPATCH);
			    } else {
				    purchaseOrderDTO.setStatus(OrderStatus.PURCHASE_ORDER_WAITING);
			    }
		    }
	    }

      //checkInventoryAmount && setNewAmount
      List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
      List<StoreHouseInventoryDTO> storeHouseInventoryDTOList = new ArrayList<StoreHouseInventoryDTO>();
      String resultStr = null;
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
        resultStr = txnService.checkInventoryAmountByStoreHouse(model, shopId,purchaseInventoryDTO, inventorySearchIndexList,storeHouseInventoryDTOList);
      }else{
        resultStr = txnService.checkInventoryAmount(model, shopId, purchaseInventoryDTO, inventorySearchIndexList);
      }
      if ((RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_SHORTAGE).equals(resultStr)) {
        return getPurchaseInventory(model, request, purchaseInventoryId.toString());
      }
	    //更新已经被删除的商品，状态的值置空
	    rfiTxnService.updateDeleteProductsByOrderDTO(purchaseInventoryDTO);
      //update purchaseInventory status, amount, remind     更新库存告警信息
      //同时update purchaseOrder Status and InventoryRemindEvent
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
        goodsStorageService.cancelPurchaseInventoryInTxnDBByStoreHouse(shopId, purchaseInventoryDTO, inventorySearchIndexList,storeHouseInventoryDTOList, purchaseOrderDTO);
      }else{
        goodsStorageService.cancelPurchaseInventoryInTxnDB(shopId, purchaseInventoryDTO, inventorySearchIndexList, purchaseOrderDTO);
      }
      //更新出入库打通中的最后入库信息
      goodsStorageService.cancelPurchaseInventoryToUpdateAveragePriceAndLastInfo(purchaseInventoryDTO);
      //update inventory_search_index,Order_index,Item_index
      searchService.cancelPurchaseInventoryInSearchDB(shopId, purchaseInventoryDTO, inventorySearchIndexList, purchaseOrderDTO);
      //ad by WLF 保存施入库单的作废日志
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
          new OperationLogDTO(shopId, (Long)request.getSession().getAttribute("userId"), purchaseInventoryDTO.getId(), ObjectTypes.INVENTORY_ORDER, OperationTypes.INVALID));
      //update product solr
      PurchaseInventoryItemDTO[] itemDTOs = purchaseInventoryDTO.getItemDTOs();
      Long[] productLocalInfoIds = new Long[itemDTOs.length];
      for (int i = 0; i < itemDTOs.length; i++) {
        productLocalInfoIds[i] = itemDTOs[i].getProductId();
      }
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productLocalInfoIds);
//      productSolrService.reindexProductInventory(shopId, productLocalInfoIds, false);  modify by xzhu
      //更新供应商入库总金额
      userService.updateSupplier(supplierDTO, purchaseInventoryId, OrderTypes.INVENTORY,purchaseInventoryDTO.getItemDTOs()[0].getProductName(), (0 - purchaseInventoryDTO.getTotal()));

      rfiTxnService.saveRepealOrderByOrderIdAndOrderType(shopId, purchaseInventoryDTO.getId(), OrderTypes.INVENTORY);

      //单据作废 结算时如果有定金 归还定金
      //supplierPayableService.returnPayable(purchaseInventoryDTO);  comment by zhuj
      PayableDTO payableDTO = supplierPayableService.getInventoryPayable(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getId(), purchaseInventoryDTO.getSupplierId());
      if (payableDTO == null) {
        LOG.error("SupplierPayableService.returnPayable 无实付记录" + purchaseInventoryDTO.toString());
      }

      // add by zhuj  处理入库作废的预付款
      if (payableDTO.getDeposit() > 0.001) {
        DepositDTO depositDTO = supplierPayableService.getDepositBySupplierId(shopId, payableDTO.getSupplierId());
        DepositOrderDTO depositOrderDTO = supplierPayableService.getTotalDepositOrderByPurchaseInventoryInfo(shopId, payableDTO.getSupplierId(), purchaseInventoryDTO.getId());
        if (InOutFlag.getInOutFlagEnumByCode(depositOrderDTO.getInOut()) == InOutFlag.OUT_FLAG) {
          depositOrderDTO.setInOut(InOutFlag.IN_FLAG.getCode());
        }
        if (DepositType.getDepositTypeBySceneAndInOutFlag(depositOrderDTO.getDepositType(), InOutFlag.OUT_FLAG) != null) {
          depositOrderDTO.setDepositType(DepositType.INVENTORY_REPEAL.getScene());
        }
        depositDTO.setOperator(WebUtil.getUserName(request));
        depositDTO.setCash(depositOrderDTO.getCash());
        depositDTO.setBankCardAmount(depositOrderDTO.getBankCardAmount());
        depositDTO.setCheckAmount(depositOrderDTO.getCheckAmount());
        depositDTO.setActuallyPaid(depositOrderDTO.getActuallyPaid());
        supplierPayableService.supplierDepositUse(depositDTO, depositOrderDTO,null);
      }

      /*作废*/
      ServiceManager.getService(ISupplierPayableService.class).repealPayable(purchaseInventoryDTO);
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      PurchaseInventorySavedEvent purchaseInventorySavedEvent = new PurchaseInventorySavedEvent(purchaseInventoryDTO);
      bcgogoEventPublisher.publisherPurchaseInventorySaved(purchaseInventorySavedEvent);

      // send messageTo shopManager
      //todo by qxy maybe need later
      //更新memcach 中的库存告警信息
      inventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, purchaseInventoryDTO.getInventoryLimitDTO());
      model.put("purchaseInventoryDTO", purchaseInventoryDTO);
//      model.put("purchaseInventoryMessage", RfTxnConstant.PURCHASE_INVENTORY_MESSAGE_CANCELED);

	    model.addAttribute("purchaseInventoryId", purchaseInventoryDTO.getId());
	    return REDIRECT_SHOW;
    } catch (Exception e) {
      LOG.error("storage.do?method=purchaseInventoryCancel\n" +
          "purchaseInventoryOrderId=" + purchaseInventoryId + "\n" +
          e.getMessage());
      LOG.error(e.getMessage(), e);
    }
    return create(model, request);
  }

  /**
   * 验证历史单据中信息是否已变动，变动的话给出提示。
   * @param model
   * @param request
   * @param purchaseInventoryId
   * @return
   */
  @RequestMapping(params = "method=validateCopy")
  @ResponseBody
  public Result validateCopy(ModelMap model, HttpServletRequest request, Long purchaseInventoryId){
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null || purchaseInventoryId == null) {
        LOG.error("storage.do?method=validateCopy, shopId:{}, purchaseInventoryOrderId:{}", shopId, purchaseInventoryId);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      return goodsStorageService.validateCopy(purchaseInventoryId, shopId);
    } catch (Exception e) {
      LOG.error("storage.do?method=validateCopy, shopId:{}, purchaseInventoryOrderId:{}", shopId, purchaseInventoryId);
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }

  /**
   * 入库单复制功能
   *
   * @param model
   * @param request
   * @param response
   * @param purchaseInventoryId
   * @return
   */
  @RequestMapping(params = "method=copyPurchaseInventory")
  public String copyPurchaseInventory(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                         Long purchaseInventoryId) throws Exception {
    Long shopId = null;

    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null || purchaseInventoryId == null) {
        LOG.error("storage.do?method=copyPurchaseInventory, shopId = " + shopId + "purchaseInventoryOrderId=" + purchaseInventoryId + "\n");
        return create(model, request);
      }
      PurchaseInventoryDTO purchaseInventoryDTO = goodsStorageService.getPurchaseInventory(purchaseInventoryId, shopId);
      if (purchaseInventoryDTO == null) {
        LOG.error("storage.do?method=copyPurchaseInventory, PurchaseInventoryDTO not found, shopId = " + shopId +
            "purchaseInventoryOrderId=" + purchaseInventoryId + "\n");
        return create(model, request);
      }
      PurchaseInventoryDTO newPurchaseInventoryDTO = rfiTxnService.copyPurchaseInventory(shopId, purchaseInventoryDTO);

      //加上供应商信息
      if(purchaseInventoryDTO.getSupplierId()!=null){
        SupplierDTO supplierDTO = userService.getSupplierById(purchaseInventoryDTO.getSupplierId());
        newPurchaseInventoryDTO.setSupplierDTO(supplierDTO);
        if(supplierDTO!=null&&supplierDTO.getId()!=null){
          List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
          //应付款总额
          Double totalPayable = doubleList.get(0);
          model.addAttribute("totalPayable", String.valueOf(NumberUtil.round(totalPayable, NumberUtil.MONEY_PRECISION)));
        }else{
            model.addAttribute("totalPayable",0);
        }
      }
      newPurchaseInventoryDTO.setReceiptNo(null);
      newPurchaseInventoryDTO.setDraftOrderIdStr(null);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if(purchaseInventoryDTO.getStorehouseId()!=null){
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,purchaseInventoryDTO.getStorehouseId());
          if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
            purchaseInventoryDTO.setStorehouseId(null);
            purchaseInventoryDTO.setStorehouseName(null);
          }
        }
        if (CollectionUtils.isNotEmpty(storeHouseDTOList) && purchaseInventoryDTO.getStorehouseId() == null) {
          if(storeHouseDTOList.size()==1){
            newPurchaseInventoryDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,newPurchaseInventoryDTO.getStorehouseId(), newPurchaseInventoryDTO);
      }
      model.addAttribute("purchaseInventoryDTO", newPurchaseInventoryDTO);
      Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
      model.addAttribute(MODEL_SETTLEMENTTYPE_LIST, settlementTypeList);
      Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      model.addAttribute(MODEL_INVOICECATEGORY_LIST, invoiceCategoryList);
      return "/txn/goodsStorage";
    } catch (Exception e) {
      LOG.error("storage.do?method=copyPurchaseInventory,shopId = " + shopId + "purchaseInventoryOrderId=" + purchaseInventoryId + "\n" +e.getMessage());
      LOG.error(e.getMessage(), e);
      return create(model, request);
    }

  }

  @RequestMapping(params = "method=payDetail")
  public String payDetail(HttpServletRequest request,HttpServletResponse response)
  {
    Long shopId= WebUtil.getShopId(request);
    String supplierIdStr = request.getParameter("supplierId");
    String idStr = request.getParameter("id");

    String purchaseOrderIdStr = request.getParameter("purchaseOrderId");
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    if(StringUtils.isNotBlank(supplierIdStr))
    {

      Double sumPayable = supplierPayableService.getSumDepositBySupplierId(Long.valueOf(supplierIdStr), shopId);
      sumPayable = NumberUtil.toReserve(sumPayable,NumberUtil.MONEY_PRECISION);
      request.setAttribute("sumPayable",sumPayable);
    }else{
      // 新供应商
      request.setAttribute("sumPayable",0.00);
    }

    if(StringUtils.isNotBlank(idStr))
    {
      PayableDTO payableDTO= supplierPayableService.getInventoryPayable(shopId, Long.valueOf(idStr), Long.valueOf(supplierIdStr));
      request.setAttribute("chongzhang",null==payableDTO?"":payableDTO.getStrikeAmount());
    }

    //如果该入库单有采购单，而且该供应商是批发商版本中的供应商 不能使用定金进行结算
    /*IUserService userService = ServiceManager.getService(IUserService.class);
    if(StringUtils.isNotBlank(purchaseOrderIdStr) &&NumberUtils.isNumber(purchaseOrderIdStr)
        && StringUtils.isNotBlank(supplierIdStr) && NumberUtils.isNumber(supplierIdStr)) {
      SupplierDTO supplierDTO = userService.getSupplierById(Long.valueOf(supplierIdStr));
      if (supplierDTO != null && NumberUtil.longValue(supplierDTO.getSupplierShopId()) > 0) {
        request.setAttribute("depositDisplay", true);
      } else {
        request.setAttribute("depositDisplay", false);
      }
    }else{*/
     request.setAttribute("depositDisplay", false);
    //}

    return "txn/payDetail1";
  }

  @RequestMapping(params = "method=validateRepealStorageOrder")
  @ResponseBody
  public Result validateRepealStorageOrder(HttpServletRequest request, Long purchaseInventoryId) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      if (shopVersionId ==null || shopId == null || purchaseInventoryId == null) {
        LOG.error("storage.do?method=validateRepalStorageOrder, shopId:{}, purchaseInventoryOrderId:{}", shopId, purchaseInventoryId);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      PurchaseInventoryDTO purchaseInventoryDTO = goodsStorageService.getSimplePurchaseInventory(purchaseInventoryId,shopId);
      PayableDTO payableDTO = supplierPayableService.getInventoryPayable(shopId, purchaseInventoryId, purchaseInventoryDTO.getSupplierId());
      //入库作废时判断单据是否有冲账 有冲账不能作废
      if(NumberUtil.doubleVal(payableDTO.getStrikeAmount()) > 0){
        return new Result("单据含有冲账，不能作废！", false);
      }
      if(payableDTO.getStatementAccountOrderId() !=null) {
        return new Result("单据已对账，不能作废！", false);
      }
      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      List<Long> productIdList = new ArrayList<Long>();
      for(PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()){
        productIdList.add(itemDTO.getProductId());
      }
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
        //通过仓库校验库存
        Map<String, String> data = new HashMap<String, String>();
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, purchaseInventoryDTO.getStorehouseId());
        if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
          return new Result("对不起，当前单据仓库已经不存在，不能进行作废！", false);
        }
        if (!inventoryService.checkBatchProductInventoryByStoreHouse(shopId, purchaseInventoryDTO.getStorehouseId(), purchaseInventoryDTO.getItemDTOs(), data, productIdList)) {
          return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
        }
      }else {
        //校验产品库存
        Map<String, String> data = new HashMap<String, String>();
        if (!inventoryService.checkBatchPurchaseInventoryInventory(shopId, purchaseInventoryDTO, data, productIdList)) {
          return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
        }
      }


      if(BcgogoShopLogicResourceUtils.isThroughSelectSupplier(shopVersionId)) {
        boolean result = getProductOutStorageService().validateBeforeInventoryRepeal(purchaseInventoryDTO);
        if (!result) {
          return new Result(ValidatorConstant.SUPPLIER_INVENTORY_LACK, false, Result.Operation.UPDATE_SUPPLIER_INVENTORY.getValue());
        }
      }

    } catch (Exception e) {
      LOG.error("storage.do?method=validateRepealStorageOrder, shopId:{}, purchaseInventoryOrderId:{}", shopId, purchaseInventoryId);
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
    return new Result();
  }


  /**
   * 保存供应商点评记录（第一次）
   * @param request
   * @param supplierCommentRecordDTO
   * @return
   */
  @RequestMapping(params = "method=saveSupplierComment")
  @ResponseBody
  public Result saveSupplierComment(HttpServletRequest request,SupplierCommentRecordDTO supplierCommentRecordDTO) {
    Result result = null;
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      supplierCommentRecordDTO.setCustomerShopId(shopId);
      ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
      result = supplierCommentService.validateBeforeSupplierComment(supplierCommentRecordDTO);
      if (!result.isSuccess()) {
        return result;
      }
      supplierCommentService.saveOrUpdateSupplierCommentRecord(supplierCommentRecordDTO);
      return result;
    } catch (Exception e) {
      LOG.error("storage.do?method=saveSupplierComment, shopId:{}", shopId + ",supplierCommentRecordDTO:" + supplierCommentRecordDTO.toString());
      LOG.error(e.getMessage(), e);
      result = new Result();
      result.setSuccess(false);
      result.setMsg(CommentConstant.SUPPLIER_COMMENT_FAIL);
      return result;
    }
  }

  /**
   * 追加供应商点评（第二次）
   * @param request
   * @param supplierCommentRecordDTO
   * @return
   */
  @RequestMapping(params = "method=addSupplierComment")
  @ResponseBody
  public Result addSupplierComment(HttpServletRequest request,SupplierCommentRecordDTO supplierCommentRecordDTO) {
    Result result = null;
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      supplierCommentRecordDTO.setCustomerShopId(shopId);
      ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
      result = supplierCommentService.addSupplierCommentContent(supplierCommentRecordDTO);
      return result;

    } catch (Exception e) {
      LOG.error("storage.do?method=addSupplierComment, shopId:{}", shopId + ",supplierCommentRecordDTO:" + supplierCommentRecordDTO.toString());
      LOG.error(e.getMessage(), e);
      result = new Result();
      result.setSuccess(false);
      result.setMsg(CommentConstant.SUPPLIER_COMMENT_FAIL);
      return result;
    }
  }

  /**
   * 在线采购单入库的时候需先验证单据状态
   * @param modelMap
   * @param request
   * @param purchaseOrderId
   * @return
   */
  @RequestMapping(params = "method=validatePurchaseOrder")
  @ResponseBody
  public Result validatePurchaseOrder(ModelMap modelMap, HttpServletRequest request, String purchaseOrderId){
    if(StringUtils.isBlank(purchaseOrderId)){
      return new Result("验证无法通过！", false);
    }
    try{
      Long shopId = WebUtil.getShopId(request);
      return ServiceManager.getService(IGoodsStorageService.class).checkPurchaseOrderInventory(shopId, purchaseOrderId);
    }catch(Exception e){
      LOG.error("RFgoodbuycontroller.validatePurchaseOrder", e);
      return new Result(false);
    }
  }

}
