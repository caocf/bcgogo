package com.bcgogo.admin;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ConfigDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.ISolrReindexJobService;
import com.bcgogo.constant.SolrReindexConstants;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.cache.MessageTemplateCacheManager;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.model.MessageTemplate;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.rmi.RmiClientGenerator;
import com.bcgogo.rmi.service.IRmiSolrService;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StopWatchUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-1-29
 * Time: 上午9:53
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/dataMaintenance.do")
public class DataMaintenanceController {
  private static final Logger LOG = LoggerFactory.getLogger(DataMaintenanceController.class);

  @RequestMapping(params = "method=createDM")
  public String createDM(ModelMap model, HttpServletRequest request, String clearFlag) {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    
    ProductDTO productDTO = new ProductDTO();
    if (clearFlag != null) {
      try {
      if ("product".equals(clearFlag)) {
          searchService.deleteByQuery("*:*", "product");
          LOG.info("solr clear product!");
      } else if ("vehicle".equals(clearFlag)) {
          searchService.deleteByQuery("*:*", "vehicle");
          LOG.info("solr clear vehicle!");
        } else if ("order".equals(clearFlag)) {
          searchService.deleteByQuery("*:*", "order");
          LOG.info("solr clear order!");
      } else if ("all".equals(clearFlag)) {
          LOG.info("solr clear all!");
          searchService.deleteByQuery("*:*", "vehicle");
          searchService.deleteByQuery("*:*", "product");
          searchService.deleteByQuery("*:*", "order");
      }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
    }
    }
    productDTO.setProductFile(null);
    model.addAttribute("command", productDTO);
    return "/dataMaintenance/dataMaintenance";
  }

  @RequestMapping(params = "method=insertproductdata", method = RequestMethod.POST)
  public String insertPradiobuttonroductData(ModelMap model, HttpServletRequest request, ProductDTO productDTO) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    try {
      MultipartFile typeFile = productDTO.getProductFile();
      productService.readFormFile(typeFile.getInputStream(), productDTO.getProductFileType());
      productDTO.setProductFile(null);
      model.addAttribute("command", productDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "/dataMaintenance/dataMaintenance";
  }


    /**
     *    根据传入参数name，value查询config表
   *
     * @param name    name为“”，不做查询条件，name不为空
     * @param value   value为“”，不做查询条件
     * @param shopId
     * @param pageNo 传入当前所在页数
     * @return
     */
    @RequestMapping(params = "method=searchConfig")
  public void searchConfig(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                           String name, String value, Long shopId, int pageNo) throws PageException {
      if (name == null || value == null || shopId == null) {
        return;
      }
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      Pager pager = new Pager(configService.countConfigs(name, value, shopId), NumberUtil.intValue(String.valueOf(pageNo), 1));
      List<ConfigDTO> configDTOs = configService.getConfig(name, value, shopId, pager);

      String jsonStr = "";
      jsonStr = JsonUtil.listToJson(configDTOs);
      jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
      if (!"[".equals(jsonStr.trim())) {
        jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
      } else {
        jsonStr = pager.toJson();
      }
      try {
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
      } catch (Exception e) {
        LOG.debug("/dataMaintenance.do");
        LOG.debug("method=searchConfig");
        LOG.error(e.getMessage(), e);
      }

    }


    @RequestMapping(params = "method=addConfig")
    public String addConfig(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        ConfigDTO configDTO = new ConfigDTO();
        model.addAttribute("configDTO", configDTO);
        return "/dataMaintenance/addConfig";
    }

  //增加一条短信模板
  @RequestMapping(params = "method=addMsgTemplate")
  public String addMsgTemplate(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    MessageTemplateDTO msgTemplateDTO = new MessageTemplateDTO();
    msgTemplateDTO.setMessageSceneList(MessageScene.getMessageSceneList());
    model.addAttribute("msgTemplateDTO", msgTemplateDTO);
    return "/dataMaintenance/addMsgTemplate";
  }

    //保存或更新config
  @RequestMapping(params = "method=saveOrUpdateConfig")
  public void saveOrUpdateConfig(ModelMap model, HttpServletRequest request, HttpServletResponse response, ConfigDTO configDTO) {
    if (configDTO == null) {
      return;
        }
        IConfigService configService = ServiceManager.getService(IConfigService.class);
        configService.saveOrUpdateConfig(configDTO);
        try {
            PrintWriter writer = response.getWriter();
            writer.write("succ");
            writer.close();
        } catch (Exception e) {
          LOG.debug("/dataMaintenance.do");
           LOG.debug("method=saveOrUpdateConfig");
           LOG.error(e.getMessage(), e);
        }

        model.addAttribute("configDTO", configDTO);
    }

  @RequestMapping(params = "method=modifyConfig")
  public String modifyConfig(ModelMap model, HttpServletRequest request, HttpServletResponse response,ConfigDTO configDTO) {
    if(configDTO.getShopId()==null) {
      return "/admin";
    }
    model.addAttribute("configDTO", configDTO);
    return "/dataMaintenance/modifyConfig";
  }



  /**
   * 弹出修改短信模板的界面，作为跳转到修改短信模板页面的controller，
   */
  @RequestMapping(params = "method=modifyMessageTemplate")
  public String modifyMessageTemplate(ModelMap model, HttpServletRequest request, HttpServletResponse response, MessageTemplateDTO msgTemplateDTO) {
    if (msgTemplateDTO.getShopId() == null) {
      return "/admin";
    }
    msgTemplateDTO.setMessageSceneList(MessageScene.getMessageSceneList());
    model.addAttribute("msgTemplateDTO", msgTemplateDTO);
    return "/dataMaintenance/modifyMsgTemplate";
  }

  /**
   * 保存短信模板
   *
   * @param model
   * @param request
   * @param response
   * @param msgTemplateDTO
   */
  @RequestMapping(params = "method=saveMsgTemplate")
  public void saveMsgTemplate(ModelMap model, HttpServletRequest request, HttpServletResponse response, MessageTemplateDTO msgTemplateDTO) {
    Long shopId= (Long)request.getSession().getAttribute("shopId");
    if (msgTemplateDTO == null||shopId==null) {
      return;
    }
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    msgTemplateDTO.setShopId(shopId);
    smsService.saveMsgTemplate(msgTemplateDTO);
    try {
      PrintWriter writer = response.getWriter();
      writer.write("succ");
      writer.close();
    } catch (Exception e) {
      LOG.debug("/dataMaintenance.do");
      LOG.debug("method=saveMsgTemplate");
      LOG.error(e.getMessage(), e);
    }

    model.addAttribute("msgTemplateDTO", msgTemplateDTO);
  }

  @RequestMapping(params = "method=updateMsgTemplate")
  public void updateMsgTemplate(ModelMap model, HttpServletRequest request, HttpServletResponse response, MessageTemplateDTO msgTemplateDTO) {
    if (msgTemplateDTO == null) {
      return;
    }
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    smsService.updateMsgTemplate(msgTemplateDTO);
    try {
      PrintWriter writer = response.getWriter();
      writer.write("succ");
      writer.close();
    } catch (Exception e) {
      LOG.debug("/dataMaintenance.do");
      LOG.debug("method=updateMsgTemplate");
      LOG.error(e.getMessage(), e);
    }

    model.addAttribute("msgTemplateDTO", msgTemplateDTO);
  }

  /**
   * 根据查询条件短信类型type，店铺的shopId查询短信模板，当type或shopId为空，自动去掉该查询条件
   *
   * @param type type为短信类型
   * @param shopId
   * @param pageNo  当前分页页号
   */
  @RequestMapping(params = "method=searchMessageTemplate")
  public void searchMessageTemplate(ModelMap model, HttpServletRequest request, HttpServletResponse response, String type, Long shopId, int pageNo) throws PageException {
    if (type == null || shopId == null) {
      return;
    }
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    Pager pager = new Pager(smsService.countMessageTemplate(type.trim(), shopId, pageNo), NumberUtil.intValue(String.valueOf(pageNo), 1));
    List<MessageTemplateDTO> msgTemplateDTOs = smsService.searchMessageTemplate(type.trim(), shopId, pager);
    List result = new ArrayList();
    result.add(msgTemplateDTOs);
    result.add(pager);
    String jsonStr = JsonUtil.listToJson(result);
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/dataMaintenance.do");
      LOG.debug("method=searchMessageTemplate");
      LOG.error(e.getMessage(), e);
    }

  }

  /**
   *  获取单条短信模板
   *
   * @param type   模板类型
   * @param shopId
   * @return
   */
  public MessageTemplateDTO getMessageTemplate(String type, Long shopId) {
    MessageTemplate messageTemplate = MessageTemplateCacheManager.getMessageTemplate(type, shopId);
    if (messageTemplate == null) {
      return new  MessageTemplateDTO();
    }
    return  messageTemplate.toDTO();
  }

  @RequestMapping(params = "method=updateSolrThesaurus")
  public void updateSolrThesaurus(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {

      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      List<MultipartFile> multipartFiles = multipartRequest.getFiles("stFile");
      if (multipartFiles.size() == 2) {
        String charEncoding = request.getCharacterEncoding();

        response.reset();
        response.setContentType("application/x-download");
        String filedisplay = multipartFiles.get(1).getOriginalFilename();
        filedisplay = URLEncoder.encode(filedisplay, charEncoding);
        response.addHeader("Content-Disposition", "attachment;filename=" + filedisplay);

        configService.updateSolrThesaurus(
            multipartFiles.get(0), multipartFiles.get(1), charEncoding, response.getOutputStream());
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=reindexsolr")
  @ResponseBody
  public Result reindexSolr(HttpServletRequest request, HttpServletResponse response, Long shopId, OrderTypes orderType, String command) {
    StopWatchUtil sw = new StopWatchUtil("reindexSolr");
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductSolrService productSolrService = ServiceManager.getService(IProductSolrService.class);
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    ISolrReindexJobService solrReindexJobService = ServiceManager.getService(ISolrReindexJobService.class);
    Result result = new Result();
    try {
      if (StringUtils.isNotBlank(command)) {
        LOG.info("执行reindex,命令:" + command + ";shopId:" + shopId + ",orderType:" + orderType + ",开始");
        if (SolrReindexConstants.CLEAR_ALL_PRODUCT.equals(command)) {
          searchService.deleteByQuery("*:*", "product");
          LOG.info("clear all product in solr!");
        } else if (SolrReindexConstants.CLEAR_ALL_VEHICLE.equals(command)) {
          searchService.deleteByQuery("*:*", "vehicle");
          LOG.info("clear all vehicle in solr!");
        } else if (SolrReindexConstants.CLEAR_ALL_ORDER.equals(command)) {
          searchService.deleteByQuery("*:*", "order");
          LOG.info("clear all order in solr!");
        } else if (SolrReindexConstants.CLEAR_CUSTOMER_SUPPLIER.equals(command)) {
          searchService.deleteByQuery("*:*", "customer_supplier");
          LOG.info("clear all user in solr!");
        } else if (SolrReindexConstants.REINDEX_COMMON_PRODUCT.equals(command)) {   //reindex 标准库
          productSolrService.reindexProductForSolr(1L);
        } else if (SolrReindexConstants.REINDEX_LOCAL_PRODUCT_SHOP.equals(command) && shopId != null) {  //reindex 店面库
          productSolrWriterService.reCreateProductSolrIndex(shopId, 2000);
//          productSolrWriterService.optimizeSolrProductCore();
        } else if (SolrReindexConstants.REINDEX_LOCAL_PRODUCT_ALL.equals(command)) {  //reindex 店面库
          List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
          List<IRmiSolrService> rmiSolrServices = new RmiClientGenerator().getRmiSolrService();
          if (CollectionUtils.isEmpty(rmiSolrServices) || 1==1) {
            for (ShopDTO shopDTO : shopDTOList) {
              productSolrWriterService.reCreateProductSolrIndex(shopDTO.getId(), 2000);
            }
          } else {
            Long batchId = solrReindexJobService.createSolrReindexJobs(shopDTOList, command, null);
            int size = rmiSolrServices.size();
            Map<Integer, List<ShopDTO>> shopDTOMap = getShopGroupMap(shopDTOList, size);

            for (Integer i : shopDTOMap.keySet()) {
              final IRmiSolrService rmiSolrService = rmiSolrServices.get(i);
              final Long finalBatchId = batchId;
              OrderThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                  try {
                    rmiSolrService.batchReCreateProductSolrIndex(finalBatchId, 2000);
                  } catch (Throwable e) {
                    LOG.error(e.getMessage(), e);
                  }
                }
              });
            }
            return new Result("命令发送成功，执行结果请查看solr_reindex_job. batchId:" + batchId, true);
          }
//          productSolrWriterService.optimizeSolrProductCore();
        } else if (SolrReindexConstants.REINDEX_FL_VEHICLE.equals(command)) {  //reindex 车型首字母
          productSolrService.reindexVehicleLetter();
        } else if (SolrReindexConstants.REINDEX_ORDER_DS.equals(command) && shopId != null) {
          ServiceManager.getService(IOrderIndexService.class).deleteOrderFromSolr(orderType, shopId);
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndexAll(ServiceManager.getService(IConfigService.class).getShopById(shopId), orderType, 1000);
//          ServiceManager.getService(IOrderSolrWriterService.class).optimizeSolrOrderCore();
        } else if (SolrReindexConstants.REINDEX_ORDER_ALL.equals(command)) {
          List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
          List<IRmiSolrService> rmiSolrServices = new RmiClientGenerator().getRmiSolrService();
          if (CollectionUtils.isEmpty(rmiSolrServices)) {
            for (ShopDTO shopDTO : shopDTOList) {
              ServiceManager.getService(IOrderIndexService.class).deleteOrderFromSolr(orderType, shopDTO.getId());
              ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndexAll(shopDTO, orderType, 1000);
            }
          } else {
            Long batchId = solrReindexJobService.createSolrReindexJobs(shopDTOList, command, orderType);
            int size = rmiSolrServices.size();

            final OrderTypes finalOrderType = orderType;
            for (int i = 0; i < size; i++) {
              final IRmiSolrService rmiSolrService = rmiSolrServices.get(i);
              final Long finalBatchId = batchId;
              OrderThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                  try {
                    rmiSolrService.batchReCreateOrderSolrIndex(finalOrderType, finalBatchId, 1000);
                  } catch (Throwable e) {
                    LOG.error(e.getMessage(), e);
                  }
                }
              });
            }
            return new Result("命令发送成功，执行结果请查看solr_reindex_job. batchId:" + batchId, true);
          }

//          ServiceManager.getService(IOrderSolrWriterService.class).optimizeSolrOrderCore();

        } else if (SolrReindexConstants.REINDEX_CUSTOMER_SUPPLIER_ALL.equals(command)) {
          searchService.deleteByQuery("*:*", "customer_supplier");
          List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
          //数据量不大，暂时不支持多台同时索引。
          List<IRmiSolrService> rmiSolrServices = new RmiClientGenerator().getRmiSolrService();
          if(CollectionUtils.isEmpty(rmiSolrServices)){
          for (ShopDTO shopDTO : shopDTOList) {
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerSupplierIndexList(shopDTO.getId(), 2000);
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactIndexList(shopDTO.getId(),2000);
          }
          }else{
            Long batchId = solrReindexJobService.createSolrReindexJobs(shopDTOList, command, orderType);
            int size = rmiSolrServices.size();

            for(int i = 0; i < size; i++){
              final IRmiSolrService rmiSolrService = rmiSolrServices.get(i);
              final Long finalBatchId = batchId;
              OrderThreadPool.getInstance().execute(new Runnable(){
                @Override
                public void run() {
                  try{
                    rmiSolrService.batchReindexCustomerSupplier(finalBatchId, 3000);
                  }catch(Throwable e){
                    LOG.error(e.getMessage(), e);
                  }
                }
              });
            }
            return new Result("命令发送成功，执行结果请查看solr_reindex_job. batchId:" + batchId, true);
          }
//          ServiceManager.getService(ISupplierSolrWriteService.class).optimizeSolrCustomerSupplierCore();
          LOG.error("执行reindex,命令REINDEX_CUSTOMER_SUPPLIER_ALL结束");
        } else if (SolrReindexConstants.REINDEX_CUSTOMER_ALL.equals(command)) {
          searchService.deleteByQuery("doc_type:customer_supplier AND customer_or_supplier:customer", "customer_supplier");
          List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
          for (ShopDTO shopDTO : shopDTOList) {
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerIndexList(shopDTO.getId(), 2000);
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactIndexList(shopDTO.getId(),5000);
          }

//          ServiceManager.getService(ISupplierSolrWriteService.class).optimizeSolrCustomerSupplierCore();
          LOG.error("执行reindex,命令REINDEX_CUSTOMER_ALL结束");
        } else if (SolrReindexConstants.REINDEX_SUPPLIER_ALL.equals(command)) {
          searchService.deleteByQuery("doc_type:customer_supplier AND customer_or_supplier:supplier", "customer_supplier");
          List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
          for (ShopDTO shopDTO : shopDTOList) {
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierIndexList(shopDTO.getId(), 2000);
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactIndexList(shopDTO.getId(),5000);
          }
//          ServiceManager.getService(ISupplierSolrWriteService.class).optimizeSolrCustomerSupplierCore();
          LOG.error("执行reindex,命令REINDEX_SUPPLIER_ALL结束");
        } else if (SolrReindexConstants.REINDEX_SUPPLIER_SHOP.equals(command) && shopId != null) {
          searchService.deleteByQuery("doc_type:customer_supplier AND customer_or_supplier:supplier AND shop_id:" + shopId, "customer_supplier");
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierIndexList(shopId, 2000);
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactIndexList(shopId,2000);
//          ServiceManager.getService(ISupplierSolrWriteService.class).optimizeSolrCustomerSupplierCore();
        } else if (SolrReindexConstants.REINDEX_CUSTOMER_SHOP.equals(command) && shopId != null) {
          searchService.deleteByQuery("doc_type:customer_supplier AND customer_or_supplier:customer AND shop_id:" + shopId, "customer_supplier");
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerIndexList(shopId, 2000);
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactIndexList(shopId,2000);
//          ServiceManager.getService(ISupplierSolrWriteService.class).optimizeSolrCustomerSupplierCore();
        } else if (SolrReindexConstants.REINDEX_SERVICE_ALL.equals(command)) {
          List<Shop> shopList = ServiceManager.getService(IConfigService.class).getShop();
          for (Shop shop : shopList) {
            ServiceManager.getService(IOrderSolrWriterService.class).reCreateRepairServiceSolrIndex(shop.getId(), 2000);
          }
        } else if (SolrReindexConstants.REINDEX_SERVICE_SHOP.equals(command) && shopId != null) {
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateRepairServiceSolrIndex(shopId, 2000);
        } else if (SolrReindexConstants.REINDEX_LICENCE_ALL.equals(command)) {
          List<Shop> shopList = ServiceManager.getService(IConfigService.class).getShop();
          for (Shop shop : shopList) {
            ServiceManager.getService(IVehicleSolrWriterService.class).reCreateVehicleSolrIndex(shop.getId(), 2000);
          }
        } else if (SolrReindexConstants.REINDEX_LICENCE_SHOP.equals(command) && shopId != null) {
          ServiceManager.getService(IVehicleSolrWriterService.class).reCreateVehicleSolrIndex(shopId, 2000);
        } else if ((SolrReindexConstants.REINDEX_SHOP_ALL).equals(command)) {
          searchService.deleteByQuery("*:*", "shop");
          ServiceManager.getService(IShopSolrWriterService.class).reCreateShopSolrIndexAll();
        } else if ((SolrReindexConstants.REINDEX_SHOP_SIGNAL).equals(command) && shopId != null) {
          ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(shopId);
        } else if (SolrReindexConstants.REINDEX_LICENCE_PRODUCT_CATEGORY.equals(command)) {
          List<Shop> shopList = ServiceManager.getService(IConfigService.class).getShop();
          for (Shop shop : shopList) {
            ServiceManager.getService(IProductSolrWriterService.class).reCreateProductCategorySolrIndex(shop.getId(), 5000);
          }
          ServiceManager.getService(IProductSolrWriterService.class).reCreateProductCategorySolrIndex(ShopConstant.BC_ADMIN_SHOP_ID,5000);
        }
        result.setSuccess(true);
        result.setMsg("操作成功！");
        LOG.info("执行reindex,命令:" + command + ";shopId:" + shopId + ",orderType:" + orderType + ",结束");
      } else {
        LOG.error("执行reindex,命令:" + command + ";shopId:" + shopId + ",orderType:" + orderType + ",失败");
        result.setSuccess(false);
        result.setMsg("执行出错，未接收到命令。");
      }
      sw.stopAndPrintLog();
    } catch (Exception e) {
      result.setSuccess(false);
      result.setMsg("出现异常！shopId:" + shopId);
      LOG.error("执行reindex,命令:" + command + ";shopId:" + shopId + ",orderType:" + orderType + ",失败");
      LOG.warn("/dataMaintenance.do");
      LOG.warn("method=reindexsolr");
      LOG.warn("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.warn("command:" + command);
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  public Map<Integer, List<ShopDTO>> getShopGroupMap(List<ShopDTO> shopDTOList, int size) {
    Map<Integer, List<ShopDTO>> shopDTOMap = new HashMap<Integer, List<ShopDTO>>();
    for(int i = 0 ; i < size; i++){
      shopDTOMap.put(i, new ArrayList<ShopDTO>());
    }

    for (int i = 0; i < shopDTOList.size(); i++) {
      ShopDTO shopDTO = shopDTOList.get(i);
      for (int j = 0; j < size; j++) {
        if (i % size == j) {
          shopDTOMap.get(j).add(shopDTO);
        }
      }
    }
    return shopDTOMap;
  }

  @RequestMapping(params = "method=searchuserbyfuzzyuserno")
  public void searchUserByFuzzyUserNo(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                      String searchWord, Integer maxResults) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<UserDTO> userDTOs = null;
    try {
      if (StringUtils.isNotBlank(searchWord))
        userDTOs = userService.getUserByFuzzyUserNo(searchWord, maxResults);
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.listToJson(userDTOs));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/dataMaintenance.do");
      LOG.debug("method=searchuserbyfuzzyuserno");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") +
          ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("searchWord:" + searchWord);
      LOG.error(e.getMessage(), e);
    }
  }
}
