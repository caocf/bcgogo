package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.RepairRemindEventTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-10-22
 * Time: 下午5:33
 * 仓库调拨单
 */
@Controller
@RequestMapping("/allocateRecord.do")
public class AllocateRecordController {
  private static final Logger LOG = LoggerFactory.getLogger(AllocateRecordController.class);
  private static final String REDIRECT_SHOW = "redirect:allocateRecord.do?method=showAllocateRecordByAllocateRecordId";

  @Autowired
  private IAllocateRecordService allocateRecordService;

  @RequestMapping(params = "method=allocateRecordList")
  public String allocateRecordList(ModelMap model, HttpServletRequest request) {
    Long shopId=WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");

      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项

      String startDateStr = DateUtil.getFirtDayOfMonth();
      String endDateStr = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY);
      model.addAttribute("startDateStr", startDateStr);
      model.addAttribute("endDateStr", endDateStr);
      model.addAttribute("editor", WebUtil.getUserName(request));
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=allocateRecordList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/allocateRecordList";
  }

  @RequestMapping(params = "method=getAllocateRecordList")
  @ResponseBody
  public Object getAllocateRecordList(HttpServletRequest request,AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      allocateRecordSearchConditionDTO.setShopId(shopId);
      if(StringUtils.isEmpty(allocateRecordSearchConditionDTO.getSortStatus())){
        allocateRecordSearchConditionDTO.setSortStatus("vestDate desc");
      }

      allocateRecordSearchConditionDTO.verificationQueryTime();
      int count = allocateRecordService.countAllocateRecords(allocateRecordSearchConditionDTO);
      Pager pager = new Pager(count, allocateRecordSearchConditionDTO.getStartPageNo(),allocateRecordSearchConditionDTO.getMaxRows());
      List<AllocateRecordDTO> allocateRecordDTOList = allocateRecordService.searchAllocateRecords(allocateRecordSearchConditionDTO);

      List<Object> result = new ArrayList<Object>();
      Map<String,Object> data=new HashMap<String,Object>();
      data.put("allocateRecordData",allocateRecordDTOList);
      data.put("allocateRecordDataCount",count);
      result.add(data);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=getAllocateRecordList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=createAllocateRecord")
  public String createAllocateRecord(ModelMap model, HttpServletRequest request,AllocateRecordDTO allocateRecordDTO) {
    Long shopId=WebUtil.getShopId(request);
    String userName=WebUtil.getUserName(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");

      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项

      if(allocateRecordDTO == null){
        allocateRecordDTO = new AllocateRecordDTO();
      }
      if(CollectionUtils.isNotEmpty(storeHouseDTOList)){
        for(StoreHouseDTO storeHouseDTO : storeHouseDTOList){
          if("默认仓库".equals(storeHouseDTO.getName())){
            allocateRecordDTO.setOutStorehouseId(storeHouseDTO.getId());
            break;
          }
        }
        if(allocateRecordDTO.getOutStorehouseId()==null){
          allocateRecordDTO.setOutStorehouseId(storeHouseDTOList.get(0).getId());
        }
      }

      allocateRecordDTO.setVestDateStr(DateUtil.getTodayStr(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
      allocateRecordDTO.setEditor(userName);
      allocateRecordDTO.setEditorId(WebUtil.getUserId(request));

      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId,allocateRecordDTO.getOutStorehouseId(),allocateRecordDTO);

      model.addAttribute("allocateRecordDTO", allocateRecordDTO);
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=createAllocateRecord");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/allocateRecord";
  }

  @RequestMapping(params = "method=createAllocateRecordByRepairOrder")
  public String createAllocateRecordByRepairOrder(ModelMap model, HttpServletRequest request,RepairOrderDTO repairOrderDTO,String returnType) {
    Long shopId=WebUtil.getShopId(request);
    AllocateRecordDTO allocateRecordDTO = null;
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if(repairOrderDTO != null) {
        allocateRecordDTO = generateAllocateRecordDTOByBcgogoOrderDto(repairOrderDTO, shopId);
        allocateRecordDTO.setReturnType(returnType);
        allocateRecordDTO.setUpdateLackOrderId(repairOrderDTO.getId());
        allocateRecordDTO.setUpdateLackOrderType(OrderTypes.REPAIR);
      }
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=createAllocateRecordByRepairOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return createAllocateRecord(model,request,allocateRecordDTO);
  }

  @RequestMapping(params = "method=createAllocateRecordByRepairPicking")
  public String createAllocateRecordByRepairPicking(ModelMap model, HttpServletRequest request,RepairPickingDTO repairPickingDTO) {
    Long shopId=WebUtil.getShopId(request);
    AllocateRecordDTO allocateRecordDTO = null;
    IPickingService pickingService = ServiceManager.getService(IPickingService.class);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      repairPickingDTO = pickingService.getRepairPickDTODById(shopId,repairPickingDTO.getId());
      if(repairPickingDTO!=null && CollectionUtils.isNotEmpty(repairPickingDTO.getPendingItemDTOs())){
        repairPickingDTO.setItemDTOs(repairPickingDTO.getPendingItemDTOs().toArray(
            new RepairPickingItemDTO[repairPickingDTO.getPendingItemDTOs().size()]));
      }
      if(repairPickingDTO != null) {
        allocateRecordDTO = generateAllocateRecordDTOByBcgogoOrderDto(repairPickingDTO, shopId);
      }
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=createAllocateRecordByRepairPicking");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return createAllocateRecord(model,request,allocateRecordDTO);
  }

  @RequestMapping(params = "method=createAllocateRecordBySaleOrderId")
  public String createAllocateRecordBySaleOrderId(ModelMap model, HttpServletRequest request,Long salesOrderId) {
    Long shopId=WebUtil.getShopId(request);
    AllocateRecordDTO allocateRecordDTO = null;
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if(salesOrderId != null) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(salesOrderId, shopId);
        allocateRecordDTO = generateAllocateRecordDTOByBcgogoOrderDto(salesOrderDTO, shopId);
        allocateRecordDTO.setUpdateLackOrderType(OrderTypes.SALE);
        allocateRecordDTO.setUpdateLackOrderId(salesOrderId);
      }
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=createAllocateRecordBySaleOrderId");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return createAllocateRecord(model,request,allocateRecordDTO);
  }

  @RequestMapping(params = "method=createAllocateRecordBySaleOrder")
  public String createAllocateRecordBySaleOrder(ModelMap model, HttpServletRequest request,SalesOrderDTO salesOrderDTO) {
    Long shopId=WebUtil.getShopId(request);
    AllocateRecordDTO allocateRecordDTO = null;
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if(salesOrderDTO != null) {
        allocateRecordDTO = generateAllocateRecordDTOByBcgogoOrderDto(salesOrderDTO, shopId);
      }
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=createAllocateRecordBySaleOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return createAllocateRecord(model,request,allocateRecordDTO);
  }

  private AllocateRecordDTO generateAllocateRecordDTOByBcgogoOrderDto(BcgogoOrderDto bcgogoOrderDto, Long shopId) throws Exception{
    AllocateRecordDTO allocateRecordDTO;
    allocateRecordDTO = new AllocateRecordDTO();
    allocateRecordDTO.setInStorehouseId(bcgogoOrderDto.getStorehouseId());
    Double totalCostPrice = 0d,totalAmount = 0d;
    if(!ArrayUtils.isEmpty(bcgogoOrderDto.getItemDTOs())){
      AllocateRecordItemDTO allocateRecordItemDTO = null;
      Set<Long> productIdSet = new HashSet<Long>();
      productIdSet.addAll(bcgogoOrderDto.getProductIdList());
      IProductService productService = ServiceManager.getService(IProductService.class);
      Map<Long,ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId,productIdSet);
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, bcgogoOrderDto.getStorehouseId(), productIdSet.toArray(new Long[productIdSet.size()]));
      ProductDTO productDTO = null;InventoryDTO inventoryDTO = null;StoreHouseInventoryDTO storeHouseInventoryDTO=null;
      List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
      for(BcgogoOrderItemDto bcgogoOrderItemDto : bcgogoOrderDto.getItemDTOs()){
        if(bcgogoOrderItemDto.getProductId()==null && StringUtils.isBlank(bcgogoOrderItemDto.getProductName())){
          continue;
        }
        if(bcgogoOrderItemDto.getAmount()- NumberUtil.numberValue(bcgogoOrderItemDto.getReserved(), 0d)<=0){
          continue;
        }
        productDTO = productDTOMap.get(bcgogoOrderItemDto.getProductId());
        storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(bcgogoOrderItemDto.getProductId());
        inventoryDTO = inventoryDTOMap.get(bcgogoOrderItemDto.getProductId());

        Double inventoryAmount = 0d,inventoryAveragePrice=0d;
        if (UnitUtil.isStorageUnit(bcgogoOrderItemDto.getUnit(), productDTO)) {
          inventoryAmount = (storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount())/productDTO.getRate();
          inventoryAveragePrice = NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice()) * productDTO.getRate();
        }else{
          inventoryAveragePrice = NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice());
          inventoryAmount = storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount();
        }
        double amount = (bcgogoOrderItemDto.getAmount() - NumberUtil.numberValue(bcgogoOrderItemDto.getReserved(), 0d)) - inventoryAmount;
        if (amount <0.0001) {
            continue;
        }
        allocateRecordItemDTO = new AllocateRecordItemDTO();
        if(productDTO!=null){
          allocateRecordItemDTO.setProductDTOWithOutUnit(productDTO);
        }else{
          allocateRecordItemDTO.setProductName(bcgogoOrderItemDto.getProductName());
          allocateRecordItemDTO.setBrand(bcgogoOrderItemDto.getBrand());
          allocateRecordItemDTO.setModel(bcgogoOrderItemDto.getModel());
          allocateRecordItemDTO.setSpec(bcgogoOrderItemDto.getSpec());
          allocateRecordItemDTO.setVehicleBrand(bcgogoOrderItemDto.getVehicleBrand());
          allocateRecordItemDTO.setVehicleModel(bcgogoOrderItemDto.getVehicleModel());
          allocateRecordItemDTO.setCommodityCode(bcgogoOrderItemDto.getCommodityCode());
        }

        allocateRecordItemDTO.setUnit(bcgogoOrderItemDto.getUnit());
        allocateRecordItemDTO.setCostPrice(inventoryDTO == null ? 0d : NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice()));
        allocateRecordItemDTO.setTotalCostPrice(amount * inventoryAveragePrice);
        allocateRecordItemDTO.setProductId(bcgogoOrderItemDto.getProductId());
        allocateRecordItemDTO.setAmount(amount);
//        allocateRecordItemDTO.setInStorageBin(bcgogoOrderItemDto.getStorageBin());
        totalCostPrice += NumberUtil.doubleVal(allocateRecordItemDTO.getTotalCostPrice());
        totalAmount += NumberUtil.doubleVal(allocateRecordItemDTO.getAmount());
        allocateRecordItemDTOList.add(allocateRecordItemDTO);
      }
      allocateRecordDTO.setItemDTOs(allocateRecordItemDTOList.toArray(new AllocateRecordItemDTO[allocateRecordItemDTOList.size()]));
    }
    allocateRecordDTO.setTotalAmount(totalAmount);
    allocateRecordDTO.setTotalCostPrice(totalCostPrice);
    return allocateRecordDTO;
  }

  @RequestMapping(params = "method=saveAllocateRecord")
  public String saveAllocateRecord(ModelMap model,HttpServletRequest request,AllocateRecordDTO allocateRecordDTO) {
    Long shopId=WebUtil.getShopId(request);
    Long userId=WebUtil.getUserId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");

      removeNullProductRow(allocateRecordDTO);

      allocateRecordDTO.setUserId(userId);
      allocateRecordDTO.setShopId(shopId);
      allocateRecordDTO.setShopVersionId(shopVersionId);
      allocateRecordDTO.setEditDate(System.currentTimeMillis());
      //归属时间
      allocateRecordDTO.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, allocateRecordDTO.getVestDateStr()));

      if(StringUtils.isBlank(allocateRecordDTO.getReceiptNo())){
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        allocateRecordDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.ALLOCATE_RECORD,null));
      }

      allocateRecordService.saveOrUpdateAllocateRecord(shopId,allocateRecordDTO);
//      if(OrderTypes.SALE.equals(allocateRecordDTO.getUpdateLackOrderType()) && allocateRecordDTO.getUpdateLackOrderId()!=null){
//        IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
//        inventoryService.updateSaleOrderLackByStoreHouse(shopId,allocateRecordDTO.getInStorehouseId(),allocateRecordDTO.getUpdateLackOrderId(),allocateRecordDTO.getProductIdList().toArray(new Long[allocateRecordDTO.getProductIdList().size()]));
//      }
      model.addAttribute("allocateRecordId",allocateRecordDTO.getId());


      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(allocateRecordDTO,OrderTypes.ALLOCATE_RECORD);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
      model.addAttribute("returnType", allocateRecordDTO.getReturnType());
      model.addAttribute("updateLackOrderType", allocateRecordDTO.getUpdateLackOrderType());
      model.addAttribute("updateLackOrderId", allocateRecordDTO.getUpdateLackOrderId());
      return REDIRECT_SHOW;
    } catch (Exception e) {
      LOG.debug("/allocateRecord.do");
      LOG.debug("method=saveAllocateRecord");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "allocateRecord.do?method=createAllocateRecord";
  }

  /**
   *
   *
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showAllocateRecordByAllocateRecordId")
  public String showAllocateRecordByAllocateRecordId(ModelMap model, HttpServletRequest request, Long allocateRecordId) throws Exception {
    try {
      if (allocateRecordId==null)
        throw new Exception("showAllocateRecordByAllocateRecordId allocateRecordId is empty!");
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      model.addAttribute("returnType", request.getParameter("returnType"));
      model.addAttribute("updateLackOrderType", request.getParameter("updateLackOrderType"));
      model.addAttribute("updateLackOrderId", request.getParameter("updateLackOrderId"));
      model.addAttribute("allocateRecordDTO", allocateRecordService.getAllocateRecordDTOById(shopId,allocateRecordId));
    } catch (Exception e) {
      LOG.error("/allocateRecord.do");
      LOG.error("method=showAllocateRecordByAllocateRecordId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/allocateRecordFinish";

  }

  /**
   * 根据前台ajax提交的dto进行验证 返回校验结果
   * @param request
   * @param allocateRecordDTO 仓库调拨
   */
  @RequestMapping(params = "method=validateAllocateRecord")
  @ResponseBody
  public Result validateAllocateRecord(HttpServletRequest request, AllocateRecordDTO allocateRecordDTO) {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      //去掉空行
      List<Long> productIdList = removeNullProductRow(allocateRecordDTO);
      if (ArrayUtil.isEmpty(allocateRecordDTO.getItemDTOs())) {
        return new Result(ValidatorConstant.ORDER_NULL_MSG, false);
      }else if(!ArrayUtil.isEmpty(allocateRecordDTO.getItemDTOs()) && allocateRecordDTO.getItemDTOs().length>productIdList.size()){
        return new Result(ValidatorConstant.ORDER_NEW_PRODUCT_ERROR, false);
      } else {
        //校验产品库存
        if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId)) {
          if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
            if(allocateRecordDTO.getInStorehouseId()!=null){
              IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
              StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId,allocateRecordDTO.getInStorehouseId());
              if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
                return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
              }
            }
            //通过仓库校验库存
            if (allocateRecordDTO.getOutStorehouseId() != null) {
              IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
              Map<String, String> data = new HashMap<String, String>();
              if (!inventoryService.checkBatchProductInventoryByStoreHouse(shopId, allocateRecordDTO.getOutStorehouseId(), allocateRecordDTO.getItemDTOs(), data, productIdList)) {
                return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
              }
            } else {
              return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error("/allocateRecord.do");
      LOG.error("method=validateAllocateRecord");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
    return new Result();
  }

  private List<Long> removeNullProductRow(AllocateRecordDTO allocateRecordDTO) {
    List<Long> productIdList = new ArrayList<Long>();
    if (allocateRecordDTO.getItemDTOs() != null) {
      AllocateRecordItemDTO[] allocateRecordItemDTOs = allocateRecordDTO.getItemDTOs();
      List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
      for (int i = 0; i < allocateRecordItemDTOs.length; i++) {
        if (allocateRecordItemDTOs[i].getProductId() != null && StringUtils.isNotBlank(allocateRecordItemDTOs[i].getProductName())) {
          allocateRecordItemDTOList.add(allocateRecordItemDTOs[i]);
          productIdList.add(allocateRecordItemDTOs[i].getProductId());
        }
      }
      if (CollectionUtils.isNotEmpty(allocateRecordItemDTOList)) {
        allocateRecordDTO.setItemDTOs(allocateRecordItemDTOList.toArray(new AllocateRecordItemDTO[allocateRecordItemDTOList.size()]));
      }
    }
    return productIdList;
  }

  @RequestMapping(params = "method=print")
  public void print(HttpServletRequest request,HttpServletResponse response,Long allocateRecordId) throws Exception {

    if (null == allocateRecordId) {
      return;
    }
    Long shopId = WebUtil.getShopId(request);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    AllocateRecordDTO allocateRecordDTO = allocateRecordService.getAllocateRecordDTOById(shopId, allocateRecordId);
    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.ALLOCATE_RECORD);

      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "ALLOCATE_RECORD" + String.valueOf(WebUtil.getShopId(request));

        String myTemplate = str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("allocateRecordDTO", allocateRecordDTO);
        context.put("shopDTO", shopDTO);

        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    } catch (Exception e) {
      LOG.debug("method=printInnerReturn");
      LOG.error(e.getMessage(), e);
    }
  }
}
