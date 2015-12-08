package com.bcgogo.txn;

import com.bcgogo.common.*;
import com.bcgogo.common.StringUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ReturnStatus;
import com.bcgogo.enums.TransferTypeEnum;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.BorrowOrder;
import com.bcgogo.txn.model.BorrowOrderItem;
import com.bcgogo.txn.service.IBorrowService;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 下午12:47
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/borrow.do")
public class BorrowController {
  private static final Logger LOG = LoggerFactory.getLogger(BorrowController.class);
  private static final String ADD_BORROW_ORDER = "/txn/borrowOrder/addBorrowOrder";
  private static final String BORROW_ORDER_LIST = "/txn/borrowOrder/borrowOrderList";
  private static final String BORROW_ORDER_DETAIL = "/txn/borrowOrder/borrowOrderDetail";
  @Autowired
  private IBorrowService borrowService;
  @Autowired
  private IStoreHouseService storeHouseService;
  @Autowired
  private ITxnService txnService;
  @Autowired
  private ICustomerService customerService;
  @Autowired
  private IProductService productService;

  @RequestMapping(params = "method=toBorrowOrderList")
  public String toBorrowOrderList(ModelMap model, HttpServletRequest request, HttpServletResponse response){
    try{
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        List<StoreHouseDTO> storeHouseDTOs = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOs",storeHouseDTOs);
        model.addAttribute("isHaveStoreHouse",true);
      }
      return BORROW_ORDER_LIST;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params ="method=toBorrowOrderDetail")
  public String toBorrowOrderDetail(ModelMap model, HttpServletRequest request,String borrowOrderId){
    try{
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        List<StoreHouseDTO> storeHouseDTOs = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOs",storeHouseDTOs);
        model.addAttribute("isHaveStoreHouse", true);
      }
      model.addAttribute("borrowOrderId",borrowOrderId);
      model.addAttribute("returnOrderDTO",new ReturnOrderDTO());
      return BORROW_ORDER_DETAIL;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params ="method=getBorrowOrderDetail")
  @ResponseBody
  public BorrowOrderDTO getBorrowOrderDetail(ModelMap model, HttpServletRequest request,String borrowOrderId){
    ISupplierService supplierService=ServiceManager.getService(ISupplierService.class);
    Long shopId = WebUtil.getShopId(request);
    if(StringUtil.isEmpty(borrowOrderId)) return null;
    try{
      List<BorrowOrderItemDTO> itemDTOs=new ArrayList<BorrowOrderItemDTO>();
      BorrowOrder borrowOrder= borrowService.getBorrowOrderById(WebUtil.getShopId(request),NumberUtil.longValue(borrowOrderId));
      if(borrowOrder==null){
        return null;
      }
      BorrowOrderDTO borrowOrderDTO=borrowOrder.toDTO();
      List<BorrowOrderItem> items=borrowService.getBorrowOrderItemByOrderId(WebUtil.getShopId(request), NumberUtil.longValue(borrowOrderId));
      if(CollectionUtil.isNotEmpty(items)){
        BorrowOrderItemDTO itemDTO=null;
        for(BorrowOrderItem item:items){
          itemDTO=item.toDTO();
          itemDTO.setProductDTOWithOutUnit(productService.getProductByProductLocalInfoId(itemDTO.getProductId(), itemDTO.getShopId()));
          itemDTOs.add(itemDTO);
        }
      }
      borrowOrderDTO.setItemDTOs(itemDTOs.toArray(new BorrowOrderItemDTO[itemDTOs.size()]));
      String phone="";
      if(UserConstant.CSType.CUSTOMER.equals(borrowOrderDTO.getBorrowerType())){
        CustomerDTO customerDTO = customerService.getCustomerById(borrowOrderDTO.getBorrowerId());
        if(customerDTO!=null){
          if(StringUtil.isEmpty(customerDTO.getMobile())){
            phone=customerDTO.getLandLine();
          }else{
            phone=customerDTO.getMobile();
          }
        }
      }else if(UserConstant.CSType.SUPPLIER.equals(borrowOrderDTO.getBorrowerType())){
        SupplierDTO supplierDTO= supplierService.getSupplierById(borrowOrderDTO.getBorrowerId(),shopId);
        if(supplierDTO!=null){
          if(StringUtil.isEmpty(supplierDTO.getMobile())){
            phone=supplierDTO.getLandLine();
          }else{
            phone=supplierDTO.getMobile();
          }
        }
      }
      borrowOrderDTO.setPhone(phone);
      return borrowOrderDTO;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params ="method=getBorrowOrderToReturn")
  @ResponseBody
  public Object getBorrowOrderToReturn(ModelMap model, HttpServletRequest request,String borrowOrderId,String[] itemIds){
    AllListResult<BorrowOrderDTO> result=new AllListResult<BorrowOrderDTO>();
    ISupplierService supplierService=ServiceManager.getService(ISupplierService.class);
    if(StringUtil.isEmpty(borrowOrderId)|| ArrayUtil.isEmpty(itemIds)){
      return result.LogErrorMsg("借调单信息异常！");
    }
    try{
      BorrowOrder borrowOrder= borrowService.getBorrowOrderById(WebUtil.getShopId(request),NumberUtil.longValue(borrowOrderId));
      if(borrowOrder==null){
        return result.LogErrorMsg("借调单不存在！");
      }
      List<Long> itemIdList=new ArrayList<Long>();
      for(String itemIdStr:itemIds){
        if(StringUtil.isEmpty(itemIdStr)){
          LOG.warn("borrowOrderItem Id is error,borrowOrderId={}!",borrowOrder.getId());
          continue;
        }
        itemIdList.add(NumberUtil.longValue(itemIdStr));
      }
      BorrowOrderDTO borrowOrderDTO=borrowOrder.toDTO();
      List<BorrowOrderItem> items=borrowService.getBorrowOrderItemByIds(WebUtil.getShopId(request),itemIdList);
      if(CollectionUtil.isEmpty(items)){
        return result.LogErrorMsg("借调单项目不存在！");
      }
      List<BorrowOrderItemDTO> itemDTOs=new ArrayList<BorrowOrderItemDTO>();
      BorrowOrderItemDTO itemDTO=null;
      for(BorrowOrderItem item:items){
        itemDTO=item.toDTO();
        itemDTO.setProductDTOWithOutUnit(productService.getProductByProductLocalInfoId(itemDTO.getProductId(), itemDTO.getShopId()));
        itemDTOs.add(itemDTO);
      }
      borrowOrderDTO.setItemDTOs(itemDTOs.toArray(new BorrowOrderItemDTO[itemDTOs.size()]));
      List<BorrowOrderDTO> borrowOrderDTOs=new ArrayList<BorrowOrderDTO>();
      borrowOrderDTOs.add(borrowOrderDTO);
      result.setResults(borrowOrderDTOs);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("获取借调单出现异常！");
    }
  }

  @RequestMapping(params = "method=getBorrowOrders")
  @ResponseBody
  public Object getBorrowOrders(ModelMap model,HttpServletRequest request,BorrowOrderDTO condition) throws Exception {
    try {
      condition.setShopId(WebUtil.getShopId(request));
      condition.initParams();
      Pager pager = new Pager(borrowService.countBorrowOrders(condition),condition.getStartPageNo(),condition.getPageSize());
      List<BorrowOrder> borrowOrders = borrowService.getBorrowOrders(condition);
      List<BorrowOrderDTO> borrowOrderDTOs=new ArrayList<BorrowOrderDTO>();
      if(CollectionUtil.isNotEmpty(borrowOrders)){
        for(BorrowOrder borrowOrder:borrowOrders){
          borrowOrderDTOs.add(borrowOrder.toDTO());
        }
      }
      List result=new ArrayList();
      List data=new ArrayList();
      data.add(borrowOrderDTOs);
      Map<String,Object> stat=new HashMap<String,Object>();
      stat.put("allBorrowOrderSize",pager.getTotalRows());
      stat.put("returnStatusStat",borrowService.getBorrowOrderStat(condition));
      data.add(stat);
      result.add(data);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("获取借调单列表出现异常！,method=getBorrowOrders;searchCondition{}"+e.getMessage(),condition,e);
      return null;
    }
  }

  @RequestMapping(params = "method=createBorrowOrder")
  public String  createBorrowOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response){
    BorrowOrderDTO borrowOrderDTO = new BorrowOrderDTO();
    try{
      Long shopId = WebUtil.getShopId(request);
      borrowOrderDTO.setOperator(WebUtil.getUserName(request));
      borrowOrderDTO.setOperatorId(WebUtil.getUserId(request));
      borrowOrderDTO.setShopId(WebUtil.getShopId(request));
      borrowOrderDTO.setVestDateStr(DateUtil.convertDateLongToString(System.currentTimeMillis(),DateUtil.STANDARD));
      borrowOrderDTO.setCustomerOrSupplierDTO(new CustomerOrSupplierDTO());
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        List<StoreHouseDTO> storeHouseDTOs = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOs",storeHouseDTOs);
        model.addAttribute("isHaveStoreHouse",true);
      }
      model.put("borrowOrderDTO", borrowOrderDTO);
      return ADD_BORROW_ORDER;
    } catch (Exception e){
      LOG.error("method=createBorrowOrder;" + e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveBorrowOrder")
  @ResponseBody
  public Object saveBorrowOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,BorrowOrderDTO borrowOrderDTO){
    ICustomerOrSupplierSolrWriteService supplierSolrWriteService= ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
    AllListResult result=new AllListResult();
    try{
      borrowOrderDTO.setShopId(WebUtil.getShopId(request));
      borrowOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      borrowOrderDTO.setOperator(WebUtil.getUserName(request));
      borrowOrderDTO.setOperatorId(WebUtil.getUserId(request));
      borrowOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      borrowOrderDTO.setTransferType(TransferTypeEnum.BORROW_TO_OTHER);
      borrowOrderDTO.setReturnStatus(ReturnStatus.RETURN_NONE);
      if (StringUtils.isBlank(borrowOrderDTO.getReceiptNo())) {
        borrowOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.BORROW_ORDER, null));
      }
      borrowService.initAndVerifySaveBorrowOrder(result, borrowOrderDTO);
      if(!result.isSuccess()){
        return result;
      }
      CustomerOrSupplierDTO csDTO=borrowOrderDTO.getCustomerOrSupplierDTO();
      csDTO.setShopId(WebUtil.getShopId(request));
      if(UserConstant.CSType.CUSTOMER.equals(csDTO.getCsType())){
        customerService.saveOrUpdateCustomerByCsDTO(result,csDTO);

        supplierSolrWriteService.reindexCustomerByCustomerId(csDTO.getCustomerOrSupplierId());
      }else if(UserConstant.CSType.SUPPLIER.equals(csDTO.getCsType())){
        borrowService.saveOrUpdateSupplierByCsDTO(result,csDTO);
        supplierSolrWriteService.reindexSupplierBySupplierId(csDTO.getCustomerOrSupplierId());
      }
      if(!result.isSuccess()){
        return result;
      }
      borrowOrderDTO.setBorrowerId(borrowOrderDTO.getCustomerOrSupplierDTO().getCustomerOrSupplierId());
      borrowOrderDTO.setBorrower(borrowOrderDTO.getCustomerOrSupplierDTO().getName());
      borrowOrderDTO.setBorrowerType(borrowOrderDTO.getCustomerOrSupplierDTO().getCsType());
      borrowService.saveBorrowOrder(borrowOrderDTO);
      result.setData(String.valueOf(borrowOrderDTO.getId()));

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(borrowOrderDTO,OrderTypes.BORROW_ORDER);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
      return result;
    } catch (Exception e){
      LOG.error("method=saveBorrowOrder;" + e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("网络异常");
      return result;
    }
  }

  @RequestMapping(params = "method=saveReturnOrder")
  @ResponseBody
  public Object  saveReturnOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,ReturnOrderDTO returnOrderDTO){
    AllListResult result=new AllListResult();
    try{
      returnOrderDTO.setShopId(WebUtil.getShopId(request));
      returnOrderDTO.setOperator(WebUtil.getUserName(request));
      returnOrderDTO.setOperatorId(WebUtil.getUserId(request));
      returnOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      if (StringUtils.isBlank(returnOrderDTO.getReceiptNo())) {
        returnOrderDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.BORROW_ORDER, null));
      }
      borrowService.verifyAndInitSaveReturnOrder(result,returnOrderDTO);
      if(!result.isSuccess()){
        return result;
      }
      borrowService.saveReturnOrder(result,returnOrderDTO);
      model.addAttribute("returnOrderId",returnOrderDTO.getId());

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(returnOrderDTO,OrderTypes.RETURN_ORDER);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      return result;
    } catch (Exception e){
      LOG.error("method=saveReturnOrder;" + e.getMessage(), e);
      return result.LogErrorMsg("保存归还单出现异常！");
    }
  }

  @RequestMapping(params = "method=getReturnRunningRecord")
  @ResponseBody
  public Object getReturnRunningRecord( HttpServletRequest request,ReturnOrderDTO condition){
    AllListResult<ReturnOrderDTO> result=new AllListResult<ReturnOrderDTO>();
    try {
      result.setResults(borrowService.getReturnRunningRecord(WebUtil.getShopId(request),NumberUtil.longValue(condition.getBorrowOrderId())));
      return result;
    } catch (Exception e) {
      LOG.error("method=getReturnRunningRecord;"+e.getMessage(),e);
      result.LogErrorMsg("获取商品归还流水记录出现异常！");
      return result;
    }
  }

  @RequestMapping(params = "method=getBorrowerList")
  @ResponseBody
  public Object getBorrowerList(HttpServletRequest request,String name){
    List<BorrowOrderDTO> borrowOrderDTOs=new ArrayList<BorrowOrderDTO>();
    try {
      List<BorrowOrder> borrowOrders= borrowService.getBorrowOrderByBorrower(WebUtil.getShopId(request),name);
      if(CollectionUtil.isNotEmpty(borrowOrders)){
        for(BorrowOrder borrowOrder:borrowOrders){
          borrowOrderDTOs.add(borrowOrder.toDTO());
        }
      }
      return borrowOrderDTOs;
    } catch (Exception e) {
      LOG.error("method=getBorrowerList;"+e.getMessage(),e);
      return borrowOrderDTOs;
    }
  }

  @RequestMapping(params = "method=printBorrowOrder")
  public void  printBorrowOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,String borrowOrderId){
    Long shopId = WebUtil.getShopId(request);
    BorrowOrderDTO borrowOrderDTO = null;
    try {
      borrowOrderDTO = this.getBorrowOrderDetail(model,request,borrowOrderId);
      if(StringUtil.isEmpty(borrowOrderDTO.getStorehouseName())){
        borrowOrderDTO.setStorehouseName("");
      }
      model.addAttribute("borrowOrderDTO", borrowOrderDTO);
    } catch (Exception e) {
      LOG.error("method=printBorrowOrder;borrowOrderId:{}" + e.getMessage(), borrowOrderId, e);
    }
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    try{
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BORROW_ORDER);
      byte templateHtmlBytes[]=printTemplateDTO.getTemplateHtml();
      //创建资源库
      String myTemplateName = "BORROW_ORDER"+ String.valueOf(WebUtil.getShopId(request));
      VelocityContext context = new VelocityContext();
      context.put("borrowOrderDTO", borrowOrderDTO);
      context.put("shopDTO",shopDTO);
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    } catch (Exception e) {
      LOG.debug("method=printBorrowOrder"+e.getMessage(),e);
    }
  }

}
