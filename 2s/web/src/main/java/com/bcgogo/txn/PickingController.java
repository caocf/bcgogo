package com.bcgogo.txn;

import com.bcgogo.common.CommonUtil;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * User: XinyuQiu
 * Date: 12-12-10
 * Time: 下午9:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/pick.do")
public class PickingController {
  private static final Logger LOG = LoggerFactory.getLogger(PickingController.class);
  private static final String REPAIR_PICKING_LIST = "/txn/repairPickingList";
  private static final String REPAIR_PICKING_INFO = "/txn/repairPickingInfo";
  private static final String INNER_PICKING_INFO = "/txn/innerPickingInfo";
  private static final String INNER_RETURN_INFO = "/txn/innerReturnInfo";
  private static final String INNER_PICKING_FINISH = "/txn/innerPickingFinish";
  private static final String INNER_RETURN_FINISH = "/txn/innerReturnFinish";
  private static final String INNER_PICKING_LIST = "/txn/innerPickingList";
  private static final String INNER_RETURN_LIST = "/txn/innerReturnList";
  private static final String REDIRECT_SHOW_REPAIR_PICKING = "redirect:pick.do?method=showRepairPicking";
  private static final String REDIRECT_SHOW_INNER_PICKING = "redirect:pick.do?method=showInnerPicking";
  private static final String REDIRECT_SHOW_INNER_RETURN = "redirect:pick.do?method=showInnerReturn";

  private IRepairService repairService;
  private IPickingService pickingService;
  private IOrderSolrWriterService orderSolrWriterService;
  private IStoreHouseService storeHouseService;
  private ITxnService txnService;
  private IInventoryService inventoryService;


  public IRepairService getRepairService() {
    return repairService == null ? ServiceManager.getService(IRepairService.class) : repairService;
  }

  public IPickingService getPickingService() {
    return pickingService == null ? ServiceManager.getService(IPickingService.class) : pickingService;
  }

  public IOrderSolrWriterService getOrderSolrWriterService() {
    return orderSolrWriterService == null ?ServiceManager.getService(IOrderSolrWriterService.class) : orderSolrWriterService;
  }

  public IStoreHouseService getStoreHouseService() {
    return storeHouseService == null ? ServiceManager.getService(IStoreHouseService.class) : storeHouseService;
  }

  public ITxnService getTxnService() {
    return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
  }

  public IInventoryService getInventoryService() {
    return inventoryService == null ? ServiceManager.getService(IInventoryService.class) : inventoryService;
  }

  @RequestMapping(params = "method=showRepairPickingListPage")
  public String showRepairPickingListPage(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                          RepairPickingDTO searchCondition) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      if(searchCondition == null){
        searchCondition = new RepairPickingDTO();
      }
      searchCondition.setShopId(shopId);
      List<RepairPickingDTO> repairPickingDTOs = getPickingService().getRepairPickingDTODetails(searchCondition);
      int totalRows = getPickingService().countRepairPickDTOs(searchCondition);
      Pager pager = new Pager(totalRows,searchCondition.getPageNo(),searchCondition.getPageSize());
      List<StoreHouseDTO> storeHouseDTOs = getStoreHouseService().getAllStoreHousesByShopId(shopId);
      model.addAttribute("repairPickingDTOs",repairPickingDTOs);
      model.addAttribute("searchCondition",searchCondition);
      model.addAttribute("pager",pager);
      model.addAttribute("storeHouseDTOs",storeHouseDTOs);
    } catch (Exception e) {
      LOG.error("method=showRepairPickingListPage;searchCondition{}"+e.getMessage(),searchCondition,e);
    }
    return REPAIR_PICKING_LIST;
  }

  @RequestMapping(params = "method=showInnerPickingListPage")
  public String showInnerPickingListPage(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                          InnerPickingDTO searchCondition) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      if(searchCondition == null){
        searchCondition = new InnerPickingDTO();
      }
      searchCondition.setShopId(shopId);
      List<InnerPickingDTO> innerPickingDTOs = getPickingService().getInnerPickingDTOs(searchCondition);
      int totalRows = getPickingService().countInnerPickingDTOs(searchCondition);
      Pager pager = new Pager(totalRows,searchCondition.getPageNo(),searchCondition.getPageSize());
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        searchCondition.setIsHaveStoreHouse(true);
        List<StoreHouseDTO> storeHouseDTOs = getStoreHouseService().getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOs",storeHouseDTOs);
      }
      Integer totalAmount = getPickingService().sumInnerPickingDTOs(shopId);
      model.addAttribute("totalAmount",totalAmount);
      model.addAttribute("innerPickingDTOs",innerPickingDTOs);
      model.addAttribute("searchCondition",searchCondition);
      model.addAttribute("pager",pager);

    } catch (Exception e) {
      LOG.error("method=showInnerPickingListPage;searchCondition{}"+e.getMessage(),searchCondition,e);
    }
    return INNER_PICKING_LIST;
  }

  @RequestMapping(params = "method=showInnerReturnListPage")
  public String showInnerReturnListPage(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                          InnerReturnDTO searchCondition) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      if(searchCondition == null){
        searchCondition = new InnerReturnDTO();
      }
      searchCondition.setShopId(shopId);
      List<InnerReturnDTO> innerReturnDTOs = getPickingService().getInnerReturnDTOs(searchCondition);
      int totalRows = getPickingService().countInnerReturnDTOs(searchCondition);
      Pager pager = new Pager(totalRows,searchCondition.getPageNo(),searchCondition.getPageSize());
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        searchCondition.setIsHaveStoreHouse(true);
        List<StoreHouseDTO> storeHouseDTOs = getStoreHouseService().getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOs",storeHouseDTOs);
      }
      Integer totalAmount = getPickingService().sumInnerReturnDTOs(shopId);
      model.addAttribute("totalAmount",totalAmount);
      model.addAttribute("innerReturnDTOs",innerReturnDTOs);
      model.addAttribute("searchCondition",searchCondition);
      model.addAttribute("pager",pager);

    } catch (Exception e) {
      LOG.error("method=showInnerReturnListPage;searchCondition{}"+e.getMessage(),searchCondition,e);
    }
    return INNER_RETURN_LIST;
  }

  @RequestMapping(params = "method=ajaxHandleRepairPicking")
  @ResponseBody
  public Result ajaxHandleRepairPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                           RepairPickingDTO repairPickingDTO) throws Exception {
    RepairPickingDTO dbRepairPickingDTO = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      repairPickingDTO.setShopId(shopId);
      repairPickingDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      repairPickingDTO.setOperationMan(WebUtil.getUserName(request),WebUtil.getUserId(request));
      String operationType = request.getParameter("operationType");
      Result result = new Result();
      dbRepairPickingDTO = getPickingService().getRepairPickDTODById(shopId, repairPickingDTO.getId());
      //校验出库状态
      if (StringUtils.isNotBlank(operationType) && operationType.trim().equals(OperationTypes.OUT_STORAGE.toString())) {
        result = getPickingService().verifyOutStorage(dbRepairPickingDTO);
        if (!result.isSuccess()) {
          return result;
        }
      }

      //加锁
      if (!(BcgogoConcurrentController.lock(ConcurrentScene.REPAIR, dbRepairPickingDTO.getRepairOrderId())
          && BcgogoConcurrentController.lock(ConcurrentScene.REPAIR_PICKING, dbRepairPickingDTO.getId()))) {
        result.setSuccess(false);
        result.setMsg("当前领料单，或者施工单正在被操作，请稍候再试！");
        return result;
      }
      //出库逻辑
      initToHandleRepairPicking(repairPickingDTO, dbRepairPickingDTO, operationType);
      //执行出库，退料逻辑
      repairPickingDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      getPickingService().handleRepairPicking(repairPickingDTO);
      //reindex order solr 施工单 只更新预留，暂时不改
//      getOrderSolrWriterService().reCreateOrderSolrIndex(dbRepairPickingDTO.getShopId(), OrderTypes.REPAIR, dbRepairPickingDTO.getRepairOrderId());
      result.setData("pick.do?method=showRepairPicking&repairPickingId=" + dbRepairPickingDTO.getIdStr());

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(dbRepairPickingDTO,OrderTypes.REPAIR_PICKING);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      return result;
    } catch (Exception e) {
      LOG.error("method=outStorage;repairPickingDTO:{}" + e.getMessage(), repairPickingDTO, e);
      return null;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.REPAIR, dbRepairPickingDTO.getRepairOrderId());
      BcgogoConcurrentController.release(ConcurrentScene.REPAIR_PICKING, dbRepairPickingDTO.getId());
    }
  }

  private void initToHandleRepairPicking(RepairPickingDTO repairPickingDTO, RepairPickingDTO dbRepairPickingDTO, String operationType) {
    if (operationType != null) {
      operationType = operationType.trim().toUpperCase();
    }
    if(dbRepairPickingDTO != null){
      repairPickingDTO.setStorehouseId(dbRepairPickingDTO.getStorehouseId());
      repairPickingDTO.setRepairOrderId(dbRepairPickingDTO.getRepairOrderId());
    }
    if (repairPickingDTO != null && CollectionUtils.isNotEmpty(repairPickingDTO.getPendingItemDTOs())) {
      if (OperationTypes.OUT_STORAGE.toString().equals(operationType) || OperationTypes.RETURN_STORAGE.toString().equals(operationType)) {
        Iterator<RepairPickingItemDTO> iterator = repairPickingDTO.getPendingItemDTOs().iterator();
        while (iterator.hasNext()) {
          RepairPickingItemDTO repairPickingItemDTO = iterator.next();
          if (repairPickingItemDTO.getStatus() == null) {
            iterator.remove();
          }
          if (OperationTypes.OUT_STORAGE.toString().equals(operationType)
              && !OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItemDTO.getStatus())) {
            iterator.remove();
          }
          if (OperationTypes.RETURN_STORAGE.toString().equals(operationType)
              && !OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItemDTO.getStatus())) {
            iterator.remove();
          }
        }
      }
    }
  }


  @RequestMapping(params = "method=handleRepairPicking")
  public String handleRepairPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                    RepairPickingDTO repairPickingDTO) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      repairPickingDTO.setShopId(shopId);
      repairPickingDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      repairPickingDTO.setOperationMan(WebUtil.getUserName(request),WebUtil.getUserId(request));
      RepairPickingDTO dbRepairPickingDTO = getPickingService().getRepairPickDTODById(shopId, repairPickingDTO.getId());
      initToHandleRepairPicking(repairPickingDTO,dbRepairPickingDTO,null);
      //校验出库状态
      Result result = getPickingService().verifyRepairPicking(repairPickingDTO,dbRepairPickingDTO);
      if (!result.isSuccess()) {
        model.addAttribute("result",result);
        if(repairPickingDTO == null || repairPickingDTO.getId() == null){
          return showRepairPickingListPage(model,request,response,null);
        }else {
          return showRepairPicking(model,request,response,repairPickingDTO.getId());
        }
      }

      repairPickingDTO.setRepairOrderId(dbRepairPickingDTO.getRepairOrderId());
      repairPickingDTO.setStorehouseId(dbRepairPickingDTO.getStorehouseId());
      //加锁
      if (!(BcgogoConcurrentController.lock(ConcurrentScene.REPAIR, dbRepairPickingDTO.getRepairOrderId())
          && BcgogoConcurrentController.lock(ConcurrentScene.REPAIR_PICKING, dbRepairPickingDTO.getId()))) {
        result.setSuccess(false);
        result.setMsg("当前领料单，或者施工单正在被操作，请稍候再试！");
        result.setOperation(Result.Operation.ALERT.getValue());
        model.addAttribute("result",result);
        return showRepairPicking(model,request,response,repairPickingDTO.getId());
      }

      //执行出库，退料逻辑
      repairPickingDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      getPickingService().handleRepairPicking(repairPickingDTO);
      //reindex order solr 施工单只更新预留，暂时不做reindex
//      getOrderSolrWriterService().reCreateOrderSolrIndex(dbRepairPickingDTO.getShopId(), OrderTypes.REPAIR, dbRepairPickingDTO.getRepairOrderId());
      model.put("repairPickingId",repairPickingDTO.getId());

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(repairPickingDTO,OrderTypes.REPAIR_PICKING);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      return REDIRECT_SHOW_REPAIR_PICKING;
    } catch (Exception e) {
      LOG.error("method=handleRepairPicking;repairPickingDTO:{}" + e.getMessage(), repairPickingDTO, e);
      return showRepairPicking(model,request,response,repairPickingDTO.getId());
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.REPAIR, repairPickingDTO.getRepairOrderId());
      BcgogoConcurrentController.release(ConcurrentScene.REPAIR_PICKING, repairPickingDTO.getId());
    }
  }

  @RequestMapping(params = "method=showRepairPicking")
  public String  showRepairPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,Long repairPickingId){
    try{
      Long shopId = WebUtil.getShopId(request);
      RepairPickingDTO repairPickingDTO = getPickingService().getRepairPickDTODById(shopId,repairPickingId);
      model.addAttribute("repairPickingDTO",repairPickingDTO);
      return REPAIR_PICKING_INFO;
    } catch (Exception e){
      LOG.error("method=showRepairPicking;repairPickingId:{}" + e.getMessage(), repairPickingId, e);
      return null;
    }
  }

  @RequestMapping(params = "method=showInnerPicking")
  public String  showInnerPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,Long innerPickingId){
    try{
      Long shopId = WebUtil.getShopId(request);
      InnerPickingDTO innerPickingDTO = getPickingService().getInnerPickingById(shopId, innerPickingId);
      model.addAttribute("innerPickingDTO",innerPickingDTO);
      return INNER_PICKING_FINISH;
    } catch (Exception e){
      LOG.error("method=showInnerPicking;innerPickingId:{}" + e.getMessage(), innerPickingId, e);
      return createInnerPicking(model,request,response);
    }
  }

  @RequestMapping(params = "method=showInnerReturn")
  public String  showInnerReturn(ModelMap model, HttpServletRequest request, HttpServletResponse response,Long innerReturnId){
    try{
      Long shopId = WebUtil.getShopId(request);
      InnerReturnDTO innerReturnDTO = getPickingService().getInnerReturnById(shopId, innerReturnId);
      model.addAttribute("innerReturnDTO",innerReturnDTO);
      return INNER_RETURN_FINISH;
    } catch (Exception e){
      LOG.error("method=showInnerReturn;innerReturnId:{}" + e.getMessage(), innerReturnId, e);
      return createInnerReturn(model, request, response);
    }
  }

  @RequestMapping(params = "method=printRepairPicking")
  public void  printRepairPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,Long repairPickingId){
    RepairPickingDTO repairPickingDTO = null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Long shopId = WebUtil.getShopId(request);
    try{
      repairPickingDTO = getPickingService().getRepairPickDTODById(shopId,repairPickingId);
      if(null != repairPickingDTO && "未填写".equals(repairPickingDTO.getProductSeller()))
      {
        repairPickingDTO.setProductSeller(null);
      }
    } catch (Exception e){
      LOG.error("method=printRepairPicking;repairPickingId:{}" + e.getMessage(), repairPickingId, e);
    }

    ShopDTO shopDTO = configService.getShopById(shopId);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try{
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.REPAIR_PICKING);

      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO) {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

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

        String myTemplateName = "REPAIR_PICKING"+ String.valueOf(WebUtil.getShopId(request));

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("repairPickingDTO", repairPickingDTO);
        context.put("shopDTO",shopDTO);

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
      LOG.debug("method=printRepairPicking");
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=printInnerPicking")
  public void  printInnerPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,Long innerPickingId){
    Long shopId = WebUtil.getShopId(request);
    InnerPickingDTO innerPickingDTO = null;
    try {

      innerPickingDTO = getPickingService().getInnerPickingById(shopId, innerPickingId);
      model.addAttribute("innerPickingDTO", innerPickingDTO);
    } catch (Exception e) {
      LOG.error("method=printInnerPicking;innerPickingId:{}" + e.getMessage(), innerPickingId, e);
    }
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    try{
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INNER_PICKING);

      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO) {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

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

        String myTemplateName = "INNER_PICKING"+ String.valueOf(WebUtil.getShopId(request));

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("innerPickingDTO", innerPickingDTO);
        context.put("shopDTO",shopDTO);

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
      LOG.debug("method=printInnerPicking");
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=printInnerReturn")
  public void  printInnerReturn(ModelMap model, HttpServletRequest request, HttpServletResponse response,Long innerReturnId){
    Long shopId = WebUtil.getShopId(request);
    InnerReturnDTO innerReturnDTO = null;
    try {
      innerReturnDTO = getPickingService().getInnerReturnById(shopId, innerReturnId);
      model.addAttribute("innerReturnDTO", innerReturnDTO);
    } catch (Exception e) {
      LOG.error("method=printInnerReturn;innerReturnId:{}" + e.getMessage(), innerReturnId, e);
    }

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    try{
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INNER_RETURN);

      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO) {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

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

        String myTemplateName = "INNER_RETURN"+ String.valueOf(WebUtil.getShopId(request));

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("innerReturnDTO", innerReturnDTO);
        context.put("shopDTO",shopDTO);

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
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=validatorRepairPicking")
  @ResponseBody
  public Object  validatorRepairPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,RepairPickingDTO repairPickingDTO){
    try{
      Long shopId = WebUtil.getShopId(request);
      String operationType = request.getParameter("operationType");
      repairPickingDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      repairPickingDTO.setShopId(WebUtil.getShopId(request));
      RepairPickingDTO dbRepairPickingDTO = getPickingService().getRepairPickDTODById(shopId, repairPickingDTO.getId());
      initToHandleRepairPicking(repairPickingDTO, dbRepairPickingDTO,operationType);
      Result result = getPickingService().verifyRepairPicking(repairPickingDTO, dbRepairPickingDTO);
      return result;
    } catch (Exception e){
      LOG.error("method=validatorRepairPicking;validatorRepairPicking:repairPickingDTO{}" + e.getMessage(), repairPickingDTO, e);
      Result result = new Result("网络异常，请联系客服人员！",false, Result.Operation.ALERT.getValue(),null);
      return result;
    }
  }

  @RequestMapping(params = "method=validatorInnerPicking")
  @ResponseBody
  public Object validatorInnerPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response, InnerPickingDTO innerPickingDTO) {
    try {
      innerPickingDTO.setShopId(WebUtil.getShopId(request));
      innerPickingDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      return getPickingService().verifySaveInnerPicking(innerPickingDTO);
    } catch (Exception e) {
      LOG.error("method=validatorInnerPicking;:innerPickingDTO{}" + e.getMessage(), innerPickingDTO, e);
      Result result = new Result("网络异常，请联系客服人员！", false, Result.Operation.ALERT.getValue(), null);
      return result;
    }
  }

  @RequestMapping(params = "method=validatorInnerReturn")
  @ResponseBody
  public Object validatorInnerReturn(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                     InnerReturnDTO innerReturnDTO) {
    try {
      innerReturnDTO.setShopId(WebUtil.getShopId(request));
      innerReturnDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      return getPickingService().verifySaveInnerReturn(innerReturnDTO);
    } catch (Exception e) {
      LOG.error("method=validatorInnerReturn;:innerReturnDTO{}" + e.getMessage(), innerReturnDTO, e);
      Result result = new Result("网络异常，请联系客服人员！", false, Result.Operation.ALERT.getValue(), null);
      return result;
    }
  }

  @RequestMapping(params = "method=createInnerPicking")
  public String  createInnerPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response){
    InnerPickingDTO innerPickingDTO = new InnerPickingDTO();
    try{
      Long shopId = WebUtil.getShopId(request);
      Set<Long> productIds = CommonUtil.convertToLong(request.getParameter("productIds"), ",");
      innerPickingDTO.setIsHaveStoreHouse(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request)));
      innerPickingDTO.setOperationMan(WebUtil.getUserName(request));
      innerPickingDTO.setOperationManId(WebUtil.getUserId(request));
      innerPickingDTO.setShopId(shopId);
      innerPickingDTO.setVestDate(DateUtil.getTheDayTime2());
      if(innerPickingDTO.getIsHaveStoreHouse()){
        List<StoreHouseDTO> storeHouseDTOs = getStoreHouseService().getAllStoreHousesByShopId(shopId);
        if(CollectionUtils.isNotEmpty(storeHouseDTOs) && innerPickingDTO.getStorehouseId() == null){
          innerPickingDTO.setStorehouseId(storeHouseDTOs.get(0).getId());
        }
        model.put("storeHouseDTOs", storeHouseDTOs);
      }
      getPickingService().createInnerPickingDTO(innerPickingDTO, productIds);
      model.put("innerPickingDTO", innerPickingDTO);
      return INNER_PICKING_INFO;
    } catch (Exception e){
      LOG.error("method=createInnerPicking;" + e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=createInnerReturn")
  public String  createInnerReturn(ModelMap model, HttpServletRequest request, HttpServletResponse response){
    InnerReturnDTO innerReturnDTO = new InnerReturnDTO();
    try{
      Long shopId = WebUtil.getShopId(request);
      Set<Long> productIds = CommonUtil.convertToLong(request.getParameter("productIds"), ",");
      innerReturnDTO.setIsHaveStoreHouse(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request)));
      innerReturnDTO.setOperationMan(WebUtil.getUserName(request));
      innerReturnDTO.setOperationManId(WebUtil.getUserId(request));
      innerReturnDTO.setShopId(shopId);
      innerReturnDTO.setVestDate(DateUtil.getTheDayTime2());
      if(innerReturnDTO.getIsHaveStoreHouse()){
        List<StoreHouseDTO> storeHouseDTOs = getStoreHouseService().getAllStoreHousesByShopId(shopId);
        if(CollectionUtils.isNotEmpty(storeHouseDTOs) && innerReturnDTO.getStorehouseId() == null){
          innerReturnDTO.setStorehouseId(storeHouseDTOs.get(0).getId());
        }
        model.put("storeHouseDTOs", storeHouseDTOs);
      }
      getPickingService().createInnerReturnDTO(innerReturnDTO, productIds);
      model.put("innerReturnDTO", innerReturnDTO);
      return INNER_RETURN_INFO;
    } catch (Exception e){
      LOG.error("method=createInnerReturn;" + e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveInnerPicking")
  public String  saveInnerPicking(ModelMap model, HttpServletRequest request, HttpServletResponse response,InnerPickingDTO innerPickingDTO){
    try{
      initInnerPickingOnSave(innerPickingDTO,request);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerPickingDTO.getShopVersionId())) {
        List<StoreHouseDTO> storeHouseDTOs = getStoreHouseService().getAllStoreHousesByShopId(innerPickingDTO.getShopId());
        if (CollectionUtils.isNotEmpty(storeHouseDTOs) && innerPickingDTO.getStorehouseId() == null) {
          innerPickingDTO.setStorehouseId(storeHouseDTOs.get(0).getId());
        }
        model.put("storeHouseDTOs", storeHouseDTOs);
      }

      Result result = getPickingService().verifySaveInnerPicking(innerPickingDTO);
      if (!result.isSuccess()) {
        model.put("result", result);
        model.put("innerPickingDTO", innerPickingDTO);
        return INNER_PICKING_INFO;
      }
      getPickingService().saveInnerPicking(innerPickingDTO);
      model.addAttribute("innerPickingId",innerPickingDTO.getId());

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(innerPickingDTO,OrderTypes.INNER_PICKING);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      return REDIRECT_SHOW_INNER_PICKING;
    } catch (Exception e){
      LOG.error("method=createInnerPicking;" + e.getMessage(), e);
      Result result = new Result(null,"网络异常",false, Result.Operation.ALERT);
      model.put("result", result);
      model.put("innerPickingDTO", innerPickingDTO);
      return INNER_PICKING_INFO;
    }
  }

  @RequestMapping(params = "method=saveInnerReturn")
  public String  saveInnerReturn(ModelMap model, HttpServletRequest request, HttpServletResponse response,InnerReturnDTO innerReturnDTO){
    try{

      initInnerReturnOnSave(innerReturnDTO, request);

      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerReturnDTO.getShopVersionId())) {
        List<StoreHouseDTO> storeHouseDTOs = getStoreHouseService().getAllStoreHousesByShopId(innerReturnDTO.getShopId());
        if (CollectionUtils.isNotEmpty(storeHouseDTOs) && innerReturnDTO.getStorehouseId() == null) {
          innerReturnDTO.setStorehouseId(storeHouseDTOs.get(0).getId());
        }
        model.put("storeHouseDTOs", storeHouseDTOs);
      }
      Result result =  getPickingService().verifySaveInnerReturn(innerReturnDTO);
      if(result!=null && !result.isSuccess()){
        model.put("result",result);
        return INNER_RETURN_INFO;
      }
      getPickingService().saveInnerReturn(innerReturnDTO);
      model.addAttribute("innerReturnId",innerReturnDTO.getId());

      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(innerReturnDTO,OrderTypes.INNER_RETURN);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      return REDIRECT_SHOW_INNER_RETURN;
    } catch (Exception e){
      LOG.error("method=saveInnerReturn;" + e.getMessage(), e);
      Result result = new Result(null,"网络异常",false, Result.Operation.ALERT);
      model.put("result", result);
      model.put("innerReturnDTO", innerReturnDTO);
      return INNER_RETURN_INFO;
    }
  }

  private void initInnerPickingOnSave(InnerPickingDTO innerPickingDTO, HttpServletRequest request) {
    innerPickingDTO.setShopId(WebUtil.getShopId(request));
    innerPickingDTO.setOperationMan(WebUtil.getUserName(request));
    innerPickingDTO.setOperationManId(WebUtil.getUserId(request));
    innerPickingDTO.setShopVersionId(WebUtil.getShopVersionId(request));
    if (StringUtils.isBlank(innerPickingDTO.getReceiptNo())) {
      innerPickingDTO.setReceiptNo(getTxnService().getReceiptNo(WebUtil.getShopId(request), OrderTypes.INNER_PICKING, null));
    }
    Long vestDate = null;
    if (StringUtils.isNotBlank(innerPickingDTO.getVestDateStr())){
      try{
        vestDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, innerPickingDTO.getVestDateStr());
      }catch (Exception e){
        LOG.warn(e.getMessage(),e);
      }
    }
    if(vestDate == null){
      try{
        vestDate = DateUtil.getTheDayTime2();
      }catch (Exception e){
        LOG.warn(e.getMessage(),e);
        vestDate = System.currentTimeMillis();
      }
    }
    innerPickingDTO.setVestDate(vestDate);
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerPickingDTO.getShopVersionId())) {
      innerPickingDTO.setIsHaveStoreHouse(true);
    }
  }

  private void initInnerReturnOnSave(InnerReturnDTO innerReturnDTO, HttpServletRequest request) {
    innerReturnDTO.setShopId(WebUtil.getShopId(request));
    innerReturnDTO.setOperationMan(WebUtil.getUserName(request));
    innerReturnDTO.setOperationManId(WebUtil.getUserId(request));
    innerReturnDTO.setShopVersionId(WebUtil.getShopVersionId(request));
    if (StringUtils.isBlank(innerReturnDTO.getReceiptNo())) {
      innerReturnDTO.setReceiptNo(getTxnService().getReceiptNo(WebUtil.getShopId(request), OrderTypes.INNER_RETURN, null));
    }
    Long vestDate = null;
    if (StringUtils.isNotBlank(innerReturnDTO.getVestDateStr())) {
      try {
        vestDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, innerReturnDTO.getVestDateStr());
      } catch (Exception e) {
        LOG.warn(e.getMessage(), e);
      }
    }
    if (vestDate == null) {
      try {
        vestDate = DateUtil.getTheDayTime();
      } catch (Exception e) {
        LOG.warn(e.getMessage(), e);
        vestDate = System.currentTimeMillis();
      }
    }
    innerReturnDTO.setVestDate(vestDate);
    innerReturnDTO.setIsHaveStoreHouse(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerReturnDTO.getShopVersionId()));
  }

}
