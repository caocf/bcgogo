package com.bcgogo.txn;


import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.InventoryCheck;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static java.lang.String.valueOf;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-19
 * Time: 下午1:57
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/inventoryCheck.do")
public class InventoryCheckController {

  private static final Logger LOG = LoggerFactory.getLogger(InventoryCheckController.class);


  @RequestMapping(params = "method=saveInventoryCheck")
  public String saveInventoryCheckOrder(HttpServletRequest request, ModelMap model, InventoryCheckDTO inventoryCheckDTO) {
    Long shopId = WebUtil.getShopId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    try {
      if (shopVersionId == null) throw new Exception("shop version is null!");
      inventoryCheckDTO.setShopVersionId(shopVersionId);
      IInventoryCheckService inventoryCheckService = ServiceManager.getService(IInventoryCheckService.class);
      inventoryCheckDTO.setShopId(shopId);
      inventoryCheckDTO.setEditor(WebUtil.getUserName(request));
      inventoryCheckDTO.setEditorId(WebUtil.getUserId(request));
      inventoryCheckDTO.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, inventoryCheckDTO.getEditDateStr()));
      if (StringUtils.isBlank(inventoryCheckDTO.getReceiptNo())) {
        inventoryCheckDTO.setReceiptNo(ServiceManager.getService(ITxnService.class).getReceiptNo(shopId, OrderTypes.INVENTORY_CHECK, null));
      }
      if (!ArrayUtils.isEmpty(inventoryCheckDTO.getItemDTOs())) {
        InventoryCheckItemDTO[] itemDTOs = inventoryCheckDTO.getItemDTOs();
        List<InventoryCheckItemDTO> itemDTOsWithoutBlank = new ArrayList<InventoryCheckItemDTO>();
        //去除空白行
        for (InventoryCheckItemDTO itemDTO : itemDTOs) {
          if (itemDTO.getProductId() != null) {
            itemDTOsWithoutBlank.add(itemDTO);
          }
        }
        inventoryCheckDTO.setItemDTOs(itemDTOsWithoutBlank.toArray(new InventoryCheckItemDTO[itemDTOsWithoutBlank.size()]));
      }
      rfiTxnService.updateProductsForInventoryCheckDTO(inventoryCheckDTO);
      inventoryCheckService.saveInventoryCheckOrder(inventoryCheckDTO);
      List<Long> productIdList = inventoryCheckDTO.getProductIdList();
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(inventoryCheckDTO, OrderTypes.INVENTORY_CHECK);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

    } catch (Exception e) {
      LOG.debug("/inventoryCheck.do");
      LOG.debug("method=saveInventoryCheck");
      LOG.debug("shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return "redirect:/inventoryCheck.do?method=getInventoryCheck&inventoryCheckId=" + inventoryCheckDTO.getIdStr();
  }

  @RequestMapping(params = "method=getInventoryCheck")
  public String getInventoryCheck(HttpServletRequest request, ModelMap modelMap, String inventoryCheckId) {
    if (StringUtil.isEmpty(inventoryCheckId)) {
      return "redirect:/inventoryCheck.do?method=createInventoryCheckByProductIds";
    }
    IInventoryCheckService checkService = ServiceManager.getService(IInventoryCheckService.class);
    try {
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        modelMap.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
      InventoryCheckDTO inventoryCheckDTO = checkService.getInventoryCheckById(WebUtil.getShopId(request), NumberUtil.longValue(inventoryCheckId));
      InventoryCheckItemDTO[] itemDTOs = inventoryCheckDTO.getItemDTOs();
      inventoryCheckDTO.setItemDTOs(itemDTOs);
      if (itemDTOs != null && itemDTOs.length > 0) {
        double inventoryAmount = 0.0, modifyInventoryAmount = 0.0;
        for (int i = 0; i < itemDTOs.length; i++) {
          inventoryAmount += (itemDTOs[i].getInventoryAmount() == null ? 0 : itemDTOs[i].getInventoryAmount());
          modifyInventoryAmount += (itemDTOs[i].getActualInventoryAmount() == null ? 0 : itemDTOs[i].getActualInventoryAmount());
        }
        inventoryCheckDTO.setInventoryAmount(inventoryAmount);
        inventoryCheckDTO.setModifyInventoryAmount(modifyInventoryAmount);
      }
      if (inventoryCheckDTO != null) {
        modelMap.addAttribute("inventoryCheckDTO", inventoryCheckDTO);
        modelMap.addAttribute("receipt", inventoryCheckDTO.getReceiptNo());
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    return "/txn/inventoryCheck";
  }

  @RequestMapping(params = "method=printInventoryCheckOrder")
  public String printInventoryCheckOrder() {
    return "/txn/inventoryCheck";
  }

  @RequestMapping(params = "method=createInventoryCheckByProductIds")
  public String createInventoryCheckByProductIds(ModelMap model, HttpServletRequest request) {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    String userName = WebUtil.getUserName(request);
    Long userId = WebUtil.getUserId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if (shopVersionId == null) throw new Exception("shop version is null!");

      InventoryCheckDTO inventoryCheckDTO = new InventoryCheckDTO();
      inventoryCheckDTO.setShopId(shopId);
      inventoryCheckDTO.setEditor(userName);
      inventoryCheckDTO.setEditorId(userId);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
      IProductService productService = ServiceManager.getService(IProductService.class);
      RFITxnService rfTxnService = ServiceManager.getService(RFITxnService.class);
      List<InventoryCheckItemDTO> inventoryCheckOrderItemDTOList = new ArrayList<InventoryCheckItemDTO>();
      String productIds = request.getParameter("productIds");
      if (productIds != null) {
        String[] productIdsArray = productIds.split(",");
        for (String productId : productIdsArray) {
          if (StringUtils.isNotBlank(productId)) {
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(Long.valueOf(productId), shopId);
            InventoryDTO inventoryDTO = rfTxnService.getInventoryByShopIdAndProductId(shopId, Long.valueOf(productId));

            if (productDTO != null) {
              InventoryCheckItemDTO inventoryCheckOrderItemDTO = new InventoryCheckItemDTO();
              inventoryCheckOrderItemDTO.setProductId(productDTO.getProductLocalInfoId());
              inventoryCheckOrderItemDTO.setProductName(productDTO.getName());
              inventoryCheckOrderItemDTO.setCommodityCode(productDTO.getCommodityCode());
              inventoryCheckOrderItemDTO.setBrand(productDTO.getBrand());
              inventoryCheckOrderItemDTO.setSpec(productDTO.getSpec());
              inventoryCheckOrderItemDTO.setModel(productDTO.getModel());
              inventoryCheckOrderItemDTO.setUnit(productDTO.getSellUnit());
              inventoryCheckOrderItemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
              inventoryCheckOrderItemDTO.setVehicleModel(productDTO.getProductVehicleModel());

              if (inventoryDTO != null) {
                inventoryCheckOrderItemDTO.setInventoryAmount(NumberUtil.round(inventoryDTO.getAmount(), 2));
                inventoryCheckOrderItemDTO.setActualInventoryAmount(NumberUtil.round(inventoryDTO.getAmount(), 2));
                inventoryCheckOrderItemDTO.setInventoryAveragePrice(NumberUtil.round(inventoryDTO.getInventoryAveragePrice(), NumberUtil.MONEY_PRECISION));
                inventoryCheckOrderItemDTO.setActualInventoryAveragePrice(NumberUtil.round(inventoryDTO.getInventoryAveragePrice(), NumberUtil.MONEY_PRECISION));
              }
              StringBuffer amountUnitSB = new StringBuffer();
              amountUnitSB.append(NumberUtil.doubleVal(inventoryCheckOrderItemDTO.getInventoryAmount())).append(productDTO.getSellUnit() == null ? "" : productDTO.getSellUnit());
              inventoryCheckOrderItemDTO.setInventoryAmountUnit(amountUnitSB.toString());
              inventoryCheckOrderItemDTOList.add(inventoryCheckOrderItemDTO);
            }

          }
        }
      }
      if (CollectionUtils.isEmpty(inventoryCheckOrderItemDTOList)) {
        inventoryCheckOrderItemDTOList.add(new InventoryCheckItemDTO());
      }
      InventoryCheckItemDTO[] itemDTOs = inventoryCheckOrderItemDTOList.toArray(new InventoryCheckItemDTO[inventoryCheckOrderItemDTOList.size()]);
      inventoryCheckDTO.setItemDTOs(itemDTOs);
      if (itemDTOs != null && itemDTOs.length > 0) {
        double inventoryAmount = 0.0, modifyInventoryAmount = 0.0;
        for (int i = 0; i < itemDTOs.length; i++) {
          inventoryAmount += (itemDTOs[i].getInventoryAmount() == null ? 0 : itemDTOs[i].getInventoryAmount());
        }
        inventoryCheckDTO.setInventoryAmount(inventoryAmount);
        inventoryCheckDTO.setModifyInventoryAmount(inventoryAmount);
      }

      inventoryCheckDTO.setEditDateStr(DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.STANDARD));

      model.addAttribute("inventoryCheckDTO", inventoryCheckDTO);
      model.addAttribute("editor", WebUtil.getUserName(request));
    } catch (Exception e) {
      LOG.debug("/inventoryCheck.do");
      LOG.debug("method=createInventoryCheckByProductIds");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }

    return "/txn/inventoryCheck";
  }

  @RequestMapping(params = "method=validateInventoryCheck")
  @ResponseBody
  public Result validateInventoryCheck(HttpServletRequest request, Long storehouseId) {
    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || storehouseId == null) {
        LOG.warn("inventoryCheck.do?method=validateInventoryCheck, shopId:{}, storehouseId:{}", shopId, storehouseId);
        return new Result("验证失败", "请选择盘点仓库！", false);
      }
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, storehouseId);
        if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
          return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
        }
      }
      return new Result();
    } catch (Exception e) {
      LOG.error("inventoryCheck.do?method=validateInventoryCheck. shopId:{}, userId:{}", shopId, userId);
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }

  @RequestMapping(params = "method=toInventoryCheckRecord")
  public String toInventoryCheckRecord(ModelMap model, HttpServletRequest request) {

    try {
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = null;
        storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(WebUtil.getShopId(request));
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
      model.addAttribute("currentTime", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.STANDARD));
      model.addAttribute("startTime","2013-10-13");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "/txn/inventoryCheckRecord";
  }

  @ResponseBody
  @RequestMapping(params = "method=getInventoryChecks")
  public List getInventoryChecks(HttpServletRequest request, InventoryCheckDTO inventoryCheckIndex) {
    if (StringUtil.isEmpty(inventoryCheckIndex.getStartPageNo())) {
      return null;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    inventoryCheckIndex.setShopId(WebUtil.getShopId(request));
    try {
      inventoryCheckIndex.convertSearchCondition();
      Pager pager = new Pager(txnService.getInventoryCheckCount(inventoryCheckIndex), NumberUtil.intValue(inventoryCheckIndex.getStartPageNo()));
      List<InventoryCheckDTO> inventoryCheckDTOs = new ArrayList<InventoryCheckDTO>();
      inventoryCheckIndex.setPager(pager);
      for (InventoryCheck inventoryCheck : txnService.getInventoryChecks(inventoryCheckIndex)) {
        if (inventoryCheck == null) continue;
        inventoryCheckDTOs.add(inventoryCheck.toDTO());
      }
      List result = new ArrayList();
      List datas = new ArrayList();
      datas.add(inventoryCheckDTOs);
      datas.add(pager.getTotalRows());
      datas.add(txnService.getStockAdjustPriceTotal(WebUtil.getShopId(request)));
      result.add(datas);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("查询盘点库存数量出现异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getInventoryCheckByProductId")
  public Object getInventoryCheckByProductId(HttpServletRequest request, Long productId, String startPageNo) {
    PagingListResult<InventoryCheckDTO> result = new PagingListResult<InventoryCheckDTO>();
    try {
      if (StringUtil.isEmpty(startPageNo) || productId == null) {
        result.setPager(new Pager(0, 1));
        return result.LogErrorMsg("参数异常");
      }
      Long shopId = WebUtil.getShopId(request);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Pager pager = new Pager(txnService.getInventoryCheckItemCountByProductIds(WebUtil.getShopId(request), productId), Integer.valueOf(startPageNo));
      Map<Long, List<InventoryCheckItemDTO>> itemDTOMap = txnService.getInventoryCheckItemByProductIds(WebUtil.getShopId(request), pager, productId);
      List<InventoryCheckItemDTO> itemDTOs = null;
      if (itemDTOMap != null && !itemDTOMap.keySet().isEmpty()) {
        itemDTOs = itemDTOMap.get(productId);
      }
      Map<Long, InventoryCheckItemDTO> checkMap = new HashMap<Long, InventoryCheckItemDTO>();
      List<InventoryCheckDTO> inventoryCheckDTOs = new ArrayList<InventoryCheckDTO>();
      if (CollectionUtil.isNotEmpty(itemDTOs)) {
        for (InventoryCheckItemDTO itemDTO : itemDTOs) {
          if (itemDTO == null) continue;
          if (!checkMap.containsKey(itemDTO.getInventoryCheckId())) {
            checkMap.put(itemDTO.getInventoryCheckId(), itemDTO);
          }
        }
        List<InventoryCheck> inventoryChecks = txnService.getInventoryCheckByIds(shopId, checkMap.keySet(), pager);
        if (CollectionUtil.isNotEmpty(inventoryChecks)) {
          InventoryCheckDTO orderDTO = null;
          InventoryCheckItemDTO itemDTO = null;
          for (InventoryCheck inventoryCheck : inventoryChecks) {
            if (inventoryCheck == null) continue;
            orderDTO = inventoryCheck.toDTO();
            inventoryCheckDTOs.add(orderDTO);
            itemDTO = checkMap.get(inventoryCheck.getId());
            if (itemDTO != null) {
              orderDTO.setAmountTotal(itemDTO.getInventoryAmountAdjustment());
              orderDTO.setAmountTotalStr(StringUtil.valueOf(itemDTO.getInventoryAmountAdjustment()) + StringUtil.valueOf(itemDTO.getUnit()));
              orderDTO.setAdjustPriceTotal(NumberUtil.doubleVal(itemDTO.getInventoryAveragePrice()) * NumberUtil.doubleVal(itemDTO.getInventoryAmountAdjustment()));
            }
          }
        }
      }
      result.setSuccess(true);
      result.setResults(inventoryCheckDTOs);
      result.setData(productId);
      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.error("查询盘点库存数量出现异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(params = "method=print")
  public void print(HttpServletRequest request, HttpServletResponse response, Long id) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    if (null == shopId || null == id) {
      return;
    }

    IInventoryCheckService checkService = ServiceManager.getService(IInventoryCheckService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    InventoryCheckDTO inventoryCheckDTO = checkService.getInventoryCheckById(shopId, id);

    InventoryCheckItemDTO[] itemDTOs = inventoryCheckDTO.getItemDTOs();
    inventoryCheckDTO.setItemDTOs(itemDTOs);
    if (itemDTOs != null && itemDTOs.length > 0) {
      double inventoryAmount = 0.0, modifyInventoryAmount = 0.0;
      for (int i = 0; i < itemDTOs.length; i++) {
        inventoryAmount += (itemDTOs[i].getInventoryAmount() == null ? 0 : itemDTOs[i].getInventoryAmount());
        modifyInventoryAmount += (itemDTOs[i].getActualInventoryAmount() == null ? 0 : itemDTOs[i].getActualInventoryAmount());
      }
      inventoryCheckDTO.setInventoryAmount(inventoryAmount);
      inventoryCheckDTO.setModifyInventoryAmount(modifyInventoryAmount);
    }

    if (null == inventoryCheckDTO) {
      return;
    }
    PrintWriter out = response.getWriter();
    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INVENTORY_CHECK);

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

        String myTemplateName = "INVENTORY_CHECK" + valueOf(WebUtil.getShopId(request));

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
        context.put("inventoryCheckDTO", inventoryCheckDTO);

        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

    } catch (Exception e) {
      LOG.error("库存盘点method=print");
      LOG.error(e.getMessage(), e);
      LOG.error("inventoryCheckDTO{}", inventoryCheckDTO);
    } finally {
      out.close();
    }
  }

  /**
   * 库存页面，商品出入库打通的盘点
   *
   * @param request
   * @param response
   * @param model
   * @param inventoryCheckDTO
   * @return
   */

  @RequestMapping(params = "method=saveProductThroughSingleInventoryCheck")
  @ResponseBody
  public Object saveProductThroughSingleInventoryCheck(HttpServletRequest request, HttpServletResponse response,
                                                       ModelMap model, InventoryCheckDTO inventoryCheckDTO) {
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Long shopId = WebUtil.getShopId(request);
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {

      inventoryCheckDTO.setShopId(shopId);
      initProductThroughSingleInventoryCheck(inventoryCheckDTO);
      saveInventoryCheckOrder(request, model, inventoryCheckDTO);
      MemcacheLimitDTO memcacheLimitDTO = inventoryService.getMemcacheLimitDTO(shopId);
      returnMap.put("memcacheLimitDTO", memcacheLimitDTO);
      returnMap.put("result", new Result(true));
      return returnMap;
    } catch (Exception e) {
      returnMap.put("result", new Result("网络异常", false));
      return returnMap;
    }

  }

  private void initProductThroughSingleInventoryCheck(InventoryCheckDTO inventoryCheckDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    if (inventoryCheckDTO == null) {
      return;
    }
    inventoryCheckDTO.setSelectSupplier(true);
    inventoryCheckDTO.setEditDateStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD, System.currentTimeMillis()));
    if (!ArrayUtils.isEmpty(inventoryCheckDTO.getItemDTOs())) {
      Long productId = inventoryCheckDTO.getItemDTOs()[0].getProductId();
      Long shopId = inventoryCheckDTO.getShopId();
      Long storehouseId = inventoryCheckDTO.getStorehouseId();
      InventoryCheckItemDTO itemDTO = inventoryCheckDTO.getItemDTOs()[0];
      ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);
      StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(storehouseId, productId);
      InventoryDTO inventoryDTO = inventoryService.getInventoryDTOByProductId(productId);
      if (productDTO != null) {
        itemDTO.setProductDTOWithOutUnit(productDTO);
        itemDTO.setUnit(productDTO.getSellUnit());
      }
      if (storeHouseInventoryDTO != null) {
        itemDTO.setInventoryAmount(storeHouseInventoryDTO.getAmount());
      }
      double inventoryAveragePrice = 0d;
      if (inventoryDTO != null) {
        inventoryAveragePrice = NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice());
        itemDTO.setInventoryAveragePrice(inventoryAveragePrice);
      }
      double changeAmount = NumberUtil.doubleVal(itemDTO.getActualInventoryAmount()) - NumberUtil.doubleVal(itemDTO.getInventoryAmount());
      inventoryCheckDTO.setAdjustPriceTotal(NumberUtil.round(changeAmount * inventoryAveragePrice, 2));
    }

  }


}
