package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.CategoryStatus;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CategoryDTO;
import com.bcgogo.txn.dto.CategoryServiceSearchDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.model.Category;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.RFTxnService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.model.MemberCardService;
import com.bcgogo.user.model.MemberService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 下午2:12
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/category.do")
public class CategoryController {
  private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);
  public static final long PAGE_SIZE = 20;//默认分页条数

  @RequestMapping(params = "method=toSetCategoryPage")
  public String toSetCategoryPage(ModelMap model, HttpServletRequest request,CategoryServiceSearchDTO categoryServiceSearchDTO) {
    model.addAttribute("categoryServiceSearchDTO",categoryServiceSearchDTO);
    return "/txn/setConstructions";
  }

  @ResponseBody
  @RequestMapping(params = "method=getCategoryItemSearch")
    public Object getCategoryItemSearch(ModelMap model, HttpServletRequest request, CategoryServiceSearchDTO categoryServiceSearchDTO) {
      RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
      Long shopId = WebUtil.getShopId(request);
      Long startPageNo = 1l;
      if (StringUtils.isNotBlank(request.getParameter("startPageNo")) && NumberUtil.isNumber(request.getParameter("startPageNo"))) {
        startPageNo = NumberUtil.longValue(request.getParameter("startPageNo"));
      }
    Map<String,Object> returnMap = new HashMap<String, Object>();
      try {
        if (categoryServiceSearchDTO == null) {
          categoryServiceSearchDTO = new CategoryServiceSearchDTO();
        }
        Pager pager = null;
        //全部
        if (categoryServiceSearchDTO.getCategoryServiceType() == null) {
          categoryServiceSearchDTO.setServiceDTOs(txnService.getServicesByCategory(shopId, categoryServiceSearchDTO.getServiceName(),
              categoryServiceSearchDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION, startPageNo, PAGE_SIZE));
          pager = new Pager(txnService.countServiceByCategory(shopId, categoryServiceSearchDTO.getServiceName(),
              categoryServiceSearchDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION), startPageNo.intValue(), (int) PAGE_SIZE);
          //已分类
        } else if (CategoryServiceSearchDTO.CategoryServiceType.HAS_CATEGORY_SERVICE.equals(categoryServiceSearchDTO.getCategoryServiceType())) {
          categoryServiceSearchDTO.setServiceDTOs(txnService.getServiceHasCategory(shopId, categoryServiceSearchDTO.getServiceName(),
              categoryServiceSearchDTO.getCategoryName(), startPageNo, PAGE_SIZE));
          pager = new Pager(txnService.countServiceHasCategory(shopId, categoryServiceSearchDTO.getServiceName(),
              categoryServiceSearchDTO.getCategoryName()), startPageNo.intValue(), (int) PAGE_SIZE);
          //未分类
        } else if (CategoryServiceSearchDTO.CategoryServiceType.NO_CATEGORY_SERVICE.equals(categoryServiceSearchDTO.getCategoryServiceType())) {
          categoryServiceSearchDTO.setServiceDTOs(txnService.getServiceNoCategory(shopId,categoryServiceSearchDTO.getServiceName(), startPageNo, PAGE_SIZE));
          pager = new Pager(txnService.countServiceNoCategory(shopId,categoryServiceSearchDTO.getServiceName()), startPageNo.intValue(), (int) PAGE_SIZE);
        }
        categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
        returnMap.put("pager", pager);
        returnMap.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
        returnMap.put("result", new Result(true));
        return returnMap;
      } catch (Exception e) {
        LOG.debug("/category.do");
        LOG.debug("method=getCategoryItemSearch");
        LOG.error(e.getMessage(), e);
        returnMap.put("result", new Result(false));
        return returnMap;
      }
    }

  @RequestMapping(params = "method=getServiceNoPercentage")
    public String getServiceNoPercentage(ModelMap model, HttpServletRequest request) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
        Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long pageNo = 1l;
        if (request.getParameter("pageNo") != null && !"".equals(request.getParameter("pageNo"))) {
      pageNo = Long.parseLong(request.getParameter("pageNo"));
    }
        try {
      CategoryServiceSearchDTO categoryServiceSearchDTO = new CategoryServiceSearchDTO();
            categoryServiceSearchDTO.setServiceDTOs(txnService.getServiceNoPercentage(shopId, pageNo, PAGE_SIZE));
//      categoryServiceSearchDTO.setHiddenServiceDTOs(categoryServiceSearchDTO.getServiceDTOs());
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
      categoryServiceSearchDTO.setUrl("category.do?method=getServiceNoPercentage");
            Pager pager = new Pager(txnService.countServiceNoPercentage(shopId), pageNo.intValue(), (int) PAGE_SIZE);
            model.addAttribute("pager", pager);
            model.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
    } catch (Exception e) {
      LOG.debug("/category.do");
      LOG.debug("method=getServiceNoPercentage");
      e.printStackTrace();
    }
    return "/txn/setConstruction";
  }

  @RequestMapping(params = "method=updateServiceSingle")
  @ResponseBody
  public Object updateServiceSingle(ModelMap model, HttpServletRequest request, ServiceDTO serviceDTO) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    result.setSuccess(false);
    try {
      txnService.updateServiceSingle(shopId, serviceDTO);

      HashSet<Long> idList = new HashSet<Long>();
      idList.add(serviceDTO.getId());
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

      assistantStatService.saveOrUpdateServiceAchievementHistory(shopId, serviceDTO.getAchievementType(), serviceDTO.getAchievementAmount(),
          WebUtil.getUserId(request), idList, serviceDTO.getStandardHours(), serviceDTO.getStandardUnitPrice());
      result.setSuccess(true);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.SERVICE);
      result.setData(totalShopAchievementConfig);
      return result;
    } catch (Exception e) {
      LOG.debug("/category.do");
      LOG.error("method=updateServiceSingle" + e.getMessage(), e);
      return result;
    }
  }

  @RequestMapping(params = "method=createNewService")
    public String createNewService(ModelMap model, HttpServletRequest request) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    ITxnService service = ServiceManager.getService(ITxnService.class);
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        try {
      CategoryServiceSearchDTO categoryServiceSearchDTO = new CategoryServiceSearchDTO();
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
      List<ServiceDTO> serviceDTOs = service.getAllServiceDTOOfTimesByShopId(shopId);
      ServiceDTO[] serviceDTOArrays = null;
            if (serviceDTOs != null) {
        serviceDTOArrays = new ServiceDTO[serviceDTOs.size()];
                for (int i = 0; i < serviceDTOs.size(); i++) {
          serviceDTOArrays[i] = serviceDTOs.get(i);
        }
      }
      categoryServiceSearchDTO.setServiceDTOs(serviceDTOArrays);
            model.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
        } catch (Exception e) {
      LOG.debug("/category.do");
      LOG.debug("method=createNewService");
      e.printStackTrace();
    }
    return "/txn/addConstruction";
  }

  @RequestMapping(params = "method=addNewService")
  @ResponseBody
  public Object addNewService(ModelMap model, HttpServletRequest request, ServiceDTO serviceDTO) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      txnService.updateServiceSingle(shopId, serviceDTO);

      HashSet<Long> idList = new HashSet<Long>();
      idList.add(serviceDTO.getId());
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      if (serviceDTO.getAchievementAmount() != null ||  serviceDTO.getStandardHours() != null|| serviceDTO.getStandardUnitPrice() != null) {
        assistantStatService.saveOrUpdateServiceAchievementHistory(shopId, serviceDTO.getAchievementType(), serviceDTO.getAchievementAmount(),
            WebUtil.getUserId(request), idList, serviceDTO.getStandardHours(), serviceDTO.getStandardUnitPrice());
      }

      return new Result(true);
    } catch (Exception e) {
      LOG.debug("/category.do");
      LOG.debug("method=addNewService" + e.getMessage(), e);
      return new Result(true);
    }
  }

  @RequestMapping(params = "method=updateServicePercentage")
    public String updateServicePercentage(ModelMap model, HttpServletRequest request, CategoryServiceSearchDTO categoryServiceSearchDTO) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
        Long shopId = (Long) request.getSession().getAttribute("shopId");
    int pageNo = Integer.parseInt(request.getParameter("pageNo"));
    int totalRows = Integer.parseInt(request.getParameter("totalRows"));
    Double percentageAmount = null;
        if (request.getParameter("percentageAmount") != null || !"".equals(request.getParameter("percentageAmount"))) {
      percentageAmount = Double.parseDouble(request.getParameter("percentageAmount"));
    }
        try {
            categoryServiceSearchDTO.setServiceDTOs(txnService.updateServicePercentage(categoryServiceSearchDTO.getServiceDTOs(), percentageAmount));
//      categoryServiceSearchDTO.setHiddenServiceDTOs(categoryServiceSearchDTO.getServiceDTOs());
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
            Pager pager = new Pager(totalRows, pageNo, (int) PAGE_SIZE);
            model.addAttribute("pager", pager);
            model.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
        } catch (Exception e) {
      LOG.debug("/category.do");
      LOG.debug("method=updateServicePercentage");
            LOG.error(e.getMessage(), e);
      e.printStackTrace();
    }
    return "/txn/setConstruction";
  }

  @RequestMapping(params = "method=updateServiceCategory")
    public String updateServiceCategory(ModelMap model, HttpServletRequest request, CategoryServiceSearchDTO categoryServiceSearchDTO) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
        Long shopId = (Long) request.getSession().getAttribute("shopId");
    int pageNo = Integer.parseInt(request.getParameter("pageNo"));
    int totalRows = Integer.parseInt(request.getParameter("totalRows"));
        try {
      Long categoryId = null;
            if (request.getParameter("categoryId") != null && !"".equals(request.getParameter("categoryId").trim())) {
        categoryId = Long.parseLong(request.getParameter("categoryId"));
      }
      String categoryName = null;
            if (request.getParameter("name") != null && !"".equals(request.getParameter("name").trim())) {
        categoryName = request.getParameter("name");
      }
            categoryServiceSearchDTO.setServiceDTOs(txnService.updateServiceCategory(shopId, categoryId, categoryName, categoryServiceSearchDTO.getServiceDTOs()));
//      categoryServiceSearchDTO.setHiddenServiceDTOs(categoryServiceSearchDTO.getServiceDTOs());
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
            Pager pager = new Pager(totalRows, pageNo, (int) PAGE_SIZE);
            model.addAttribute("pager", pager);
            model.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
        } catch (Exception e) {
      LOG.debug("/category.do");
      LOG.debug("method=updateServiceCategory");
      e.printStackTrace();
    }
    return "/txn/setConstruction";
  }

  @RequestMapping(params = "method=getObscureCategoryByName")
    public void getObscureCategoryByName(HttpServletRequest request, HttpServletResponse response, String name) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    PrintWriter out = response.getWriter();
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        try {
      List<Category> categories = null;
            if (StringUtils.isNotBlank(name)) {
        categories = txnService.getObscureCategoryByName(shopId, name);
      }

      out.write(JsonUtil.listToJson(categories));
        } catch (Exception e) {
      LOG.error("method=getObscureCategoryByName");
            LOG.error("shopId", shopId);
            LOG.error("categoryName", name);
            LOG.error(e.getMessage(), e);
        } finally {
      out.close();
    }
  }

  @RequestMapping(params = "method=checkServiceNameRepeat")
  @ResponseBody
  public Map checkServiceNameRepeat(HttpServletRequest request,HttpServletResponse response,String serviceName) throws Exception
  {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    Map map = new HashMap();
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String serviceIdStr = request.getParameter("serviceId");

    if("add".equals(request.getParameter("insert")))
    {
      if(shopId == null){
        map.put("resu", "error");
        return map;
      }
    }
    else
    {
      if(shopId == null || StringUtils.isNotEmpty(serviceIdStr)&&!NumberUtil.isNumber(serviceIdStr)){
        map.put("resu", "error");
        return map;
      }
    }
    Long serviceId = null;
    if (StringUtils.isBlank(serviceName)) {
      serviceName = "";
    }
    try {
      if (StringUtils.isNotBlank(serviceIdStr)) {
        serviceId = Long.valueOf(serviceIdStr);
      }

      List<Service> serviceDTOList = txnService.getServiceByShopIdAndNameRemovalTrimAndUpper(shopId, serviceName, serviceId);

      if (CollectionUtils.isNotEmpty(serviceDTOList)) {
        map.put("resu", "error");
        return map;
      }

      if(serviceId == null){
        map.put("resu", "success");
        return map;
      }
      Service service = txnService.getServiceById(shopId, serviceId);
      if(service!=null && !service.getName().equals(serviceName)){
        List<OrderIndexDTO> unsettledOrders = rfiTxnService.getUnsettledOrdersByServiceId(shopId, serviceId);
        if(CollectionUtils.isEmpty(unsettledOrders)){
          map.put("resu", "success");
          return map;
        }else{
          map.put("resu", "inUse");
          map.put("data", unsettledOrders);
          return map;
        }
      }else{
        map.put("resu", "success");
        return map;
      }
    } catch (Exception e) {
      LOG.error("method=checkServiceNameRepeat");
      LOG.error("shopId", shopId);
      LOG.error("serviceName", serviceName);
      LOG.error(e.getMessage(), e);
      map.put("resu", "error");
      return map;
    }
  }

  @RequestMapping(params = "method=checkServiceUsed")
  public void checkServiceUsed(HttpServletRequest request, HttpServletResponse response, Long serviceId) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    try {
      List<MemberService> memberServiceList = membersService.getMemberServiceByServiceId(serviceId);

      if (CollectionUtils.isNotEmpty(memberServiceList)) {
        jsonStr = "error";
        return;
      }

      List<MemberCardService> memberCardServiceList = membersService.getMemberCardServiceByServiceId(serviceId);

      if (CollectionUtils.isNotEmpty(memberCardServiceList)) {
        jsonStr = "error";
      }

    } catch (Exception e) {
      LOG.error("method=checkServiceUsed");
      LOG.error("shopId", shopId);
      LOG.error("serviceId", serviceId);
      LOG.error(e.getMessage(), e);
    } finally {

      Map<String, String> map = new HashMap<String, String>();
      map.put("resu", jsonStr);
      out.write(JsonUtil.mapToJson(map));
      out.flush();
      out.close();
    }

  }


  @RequestMapping(params = "method=deleteService")
  @ResponseBody
  public Object deleteService(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long serviceId) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    try {

      txnService.deleteService(shopId, serviceId);
      return new Result(true);
    } catch (Exception e) {
      LOG.error("method=deleteService");
      LOG.error("shopId", shopId);
      LOG.error("serviceId", serviceId);
      LOG.error(e.getMessage(), e);
      return new Result(true);
    }
  }

  @RequestMapping(params = "method=checkServiceDisabled")
    public void checkServiceDisabled(HttpServletRequest request, HttpServletResponse response, String serviceName) throws Exception {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);

    PrintWriter out = response.getWriter();
        Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";

        try {
            Service service = txnService.getRFServiceByServiceNameAndShopId(shopId, serviceName);
            Map<String, String> map = new HashMap<String, String>();
            if (null == service) {
        jsonStr = "noService";
            } else if (ServiceStatus.DISABLED == service.getStatus()) {
        jsonStr = "serviceDisabled";
                map.put("serviceId", service.getId().toString());
            } else if (ServiceStatus.ENABLED == service.getStatus()) {
        jsonStr = "serviceEnabled";
      }

            map.put("resu", jsonStr);

      out.write(JsonUtil.mapToJson(map));

        } catch (Exception e) {
      LOG.error("method=checkServiceDisabled");
            LOG.error("shopId", shopId);
            LOG.error("serviceName", serviceName);
            LOG.error(e.getMessage(), e);
        } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=updateServiceStatus")
    public String updateServiceStatus(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long serviceId) {
    RFTxnService txnService = ServiceManager.getService(RFTxnService.class);
        Long shopId = (Long) request.getSession().getAttribute("shopId");

        try {
            Service service = txnService.changeServiceStatus(shopId, serviceId, ServiceStatus.ENABLED);

            Set<Long> set = new HashSet<Long>();
            set.add(service.getId());
            if (set.size() > 0) {
                IOrderSolrWriterService orderSolrWriterService = ServiceManager.getService(IOrderSolrWriterService.class);
                orderSolrWriterService.createRepairServiceSolrIndex(shopId, set);
            }
      CategoryServiceSearchDTO  categoryServiceSearchDTO = new CategoryServiceSearchDTO();
      categoryServiceSearchDTO.setServiceName(service.getName());
            categoryServiceSearchDTO.setServiceDTOs(txnService.getServicesByCategory(shopId, service.getName(),
                    null, CategoryType.BUSINESS_CLASSIFICATION, 1L, PAGE_SIZE));
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
            Pager pager = new Pager(txnService.countServiceByCategory(shopId, service.getName(),
                    null, CategoryType.BUSINESS_CLASSIFICATION), 1, (int) PAGE_SIZE);
            model.addAttribute("pager", pager);
            model.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
        } catch (Exception e) {
      LOG.error("method=updateServiceStatus");
            LOG.error("shopId", shopId);
            LOG.error("serviceId", serviceId);
            LOG.error(e.getMessage(), e);
    }
    return "/txn/setConstruction";
  }


  @RequestMapping(params = "method=getServiceCategory")
    public String getServiceCategory(ModelMap model, HttpServletRequest request) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
        Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long pageNo = 1l;
        if (request.getParameter("pageNo") != null && !"".equals(request.getParameter("pageNo"))) {
      pageNo = Long.parseLong(request.getParameter("pageNo"));
    }
        try {
      CategoryServiceSearchDTO categoryServiceSearchDTO = new CategoryServiceSearchDTO();
            categoryServiceSearchDTO.setServiceDTOs(txnService.getServiceCategory(shopId, pageNo, PAGE_SIZE));
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
      categoryServiceSearchDTO.setUrl("category.do?method=getServiceCategory");
            Pager pager = new Pager(txnService.countServiceCategory(shopId), pageNo.intValue(), (int) PAGE_SIZE);
            model.addAttribute("pager", pager);
            model.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
    } catch (Exception e) {
      LOG.error("/category.do");
      LOG.error("method=getServiceNoCategory");
            LOG.error(e.getMessage(), e);
    }
    return "/txn/setConstruction";
  }

  @RequestMapping(params = "method=setAllCategory")
    public String setAllCategory(ModelMap model, HttpServletRequest request) {

    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    ITxnService service = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      CategoryServiceSearchDTO categoryServiceSearchDTO = new CategoryServiceSearchDTO();
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
      List<ServiceDTO> serviceDTOs = service.getAllServiceDTOOfTimesByShopId(shopId);
      ServiceDTO[] serviceDTOArrays = null;
      if (serviceDTOs != null) {
        serviceDTOArrays = new ServiceDTO[serviceDTOs.size()];
        for (int i = 0; i < serviceDTOs.size(); i++) {
          serviceDTOArrays[i] = serviceDTOs.get(i);
        }
      }
      categoryServiceSearchDTO.setServiceDTOs(serviceDTOArrays);
      model.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
    } catch (Exception e) {
      LOG.error("/category.do method=createNewService ");
      LOG.error(e.getMessage(), e);
    }
    return "/txn/setCategory";
  }

  @RequestMapping(params = "method=getCategory")
  @ResponseBody
    private Map getCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = WebUtil.getShopId(request);

    List<CategoryDTO> categoryDTOList = rfiTxnService.vagueGetCategoryByShopIdAndName(shopId, (String) request.getParameter("keyWord"));

    Map map = new HashMap();
        map.put("uuid", (String) request.getParameter("uuid"));
        map.put("data", categoryDTOList);

    return map;
  }

  @RequestMapping(params = "method=setServiceAssistant")
  public String setServiceAssistant(ModelMap model, HttpServletRequest request) {
    return "/txn/setServiceAssistant";
  }

  @RequestMapping(params = "method=updateCategoryName")
  @ResponseBody
    public Map updateCategoryName(HttpServletRequest request, HttpServletResponse response) {
    String categoryName = request.getParameter("categoryName");

    String categoryIdStr = request.getParameter("categoryId");

    Long categoryId = null;

    Map map = new HashMap();

        if (StringUtils.isNotBlank(categoryIdStr)) {
      categoryId = Long.valueOf(categoryIdStr);
    }

        if (StringUtils.isBlank(categoryName) || null == categoryId) {
            map.put("resu", "error");
            map.put("msg", "no categoryName or no categoryId");
      return map;
    }

    Long shopId = WebUtil.getShopId(request);

    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);

        List<CategoryDTO> categoryDTOList = rfiTxnService.getCategoryByShopIdAndName(shopId, categoryName);

        if (CollectionUtils.isEmpty(categoryDTOList)) {
            rfiTxnService.updateCategory(shopId, categoryId, categoryName);
            map.put("resu", "success");
        } else if (categoryDTOList.size() == 1) {
      CategoryDTO categoryDTO = categoryDTOList.get(0);
            if (CategoryStatus.DISABLED.equals(categoryDTO.getStatus())) {
                if (categoryDTO.getId().equals(categoryId)) {
                    rfiTxnService.changeCategoryStatus(shopId, categoryId, CategoryStatus.ENABLED);
                    map.put("resu", "success");
                    map.put("msg", "it is self");
                } else {
                    rfiTxnService.changeCategoryStatus(shopId, categoryDTO.getId(), CategoryStatus.ENABLED);
                    rfiTxnService.changeCategoryStatus(shopId, categoryId, CategoryStatus.DISABLED);
                    map.put("resu", "success");
                    map.put("msg", "change status for two category");
        }
            } else {
                if (categoryDTO.getId().equals(categoryId)) {

                    map.put("resu", "success");
                    map.put("msg", "it is self");
                } else {
                    map.put("resu", "error");
                    map.put("msg", "already has categoryNames like this");
        }
        }
        } else if (categoryDTOList.size() > 1) {
            map.put("resu", "error");
            map.put("msg", "already has categoryNames like this");
      }

    return map;

  }

  @RequestMapping(params = "method=getCategoryByServiceId")
  @ResponseBody
    private Map getCategoryByServiceId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = WebUtil.getShopId(request);

    String serviceIdStr = request.getParameter("serviceId");

    Long serviceId = null;

        if (StringUtils.isNotBlank(serviceIdStr)) {
      serviceId = Long.valueOf(serviceIdStr);
    }

        CategoryDTO categoryDTO = rfiTxnService.getCateGoryByServiceId(shopId, serviceId);

    Map map = new HashMap();

        map.put("data", categoryDTO);

    return map;
  }

  @RequestMapping(params = "method=deleteCategory")
  @ResponseBody
    private Map deleteCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = WebUtil.getShopId(request);

    String categoryIdStr = request.getParameter("categoryId");

    Map map = new HashMap();

        if (StringUtils.isBlank(categoryIdStr)) {
      return map;
    }

    Long categoryId = Long.valueOf(categoryIdStr);

        try {
      //1,把category状态改为disabled
            rfiTxnService.changeCategoryStatus(shopId, categoryId, CategoryStatus.DISABLED);

      //2,把service和category关联表数据删除
      rfiTxnService.deleteCategoryRelationItemByCategoryId(categoryId);

      //把productlocalinfo中关联此营业分类的商品更新
            productService.deleteProductLocalInfoCategoryId(shopId, categoryId);

            map.put("resu", "success");
        } catch (Exception e) {
            map.put("resu", "error");
            LOG.error(e.getMessage(), e);
            LOG.error("method=deleteCategory  categoryId=" + categoryIdStr);
    }

    return map;
  }

    @RequestMapping(params = "method=ajaxUpdateServiceName")
    @ResponseBody
    public Result ajaxUpdateServiceName(ModelMap model, HttpServletRequest request, String serviceId, String serviceName) {
        if (StringUtils.isBlank(serviceId) || StringUtils.isBlank(serviceName) || !NumberUtil.isNumber(serviceId)) {
            return new Result("修改失败", "参数异常，请重试！", false);
        }
        Long shopId = WebUtil.getShopId(request);
        Long id = Long.parseLong(serviceId);

        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
//      IOrderSolrWriterService orderSolrWriterService = ServiceManager.getService(IOrderSolrWriterService.class);

        try {
            Service service = txnService.getServiceById(shopId, id);
            if (service == null) {
                return new Result("修改失败", "该服务不存在！", false);
            }
            service.setName(serviceName);
            rfiTxnService.updateService(service);
//        orderSolrWriterService.reCreateOrderSolrIndexAll(importContext.getShopId(),2000);
            return new Result("修改成功", "修改已成功！", true);
        } catch (Exception e) {
            LOG.error("CategoryController.ajaxUpdateService异常!");
            LOG.error(e.getMessage(), e);
            return new Result("修改失败", "修改时出错，请重试！", false);
        }
    }

    @RequestMapping(params = "method=ajaxDeleteService")
    @ResponseBody
    public Result ajaxDeleteService(ModelMap model, HttpServletRequest request, String serviceId) {
        if (StringUtils.isBlank(serviceId) || !NumberUtil.isNumber(serviceId)) {
            return new Result("删除失败", "参数异常，请重试！", false);
        }
        Long shopId = WebUtil.getShopId(request);
        Long id = Long.parseLong(serviceId);

        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        try {
            Service service = txnService.getServiceById(shopId, id);
            if (service == null) {
                return new Result("删除失败", "该服务不存在！", false);
            }
            service.setStatus(ServiceStatus.DISABLED);
            rfiTxnService.updateService(service);
            return new Result("删除成功", "删除已成功！", true);
        } catch (Exception e) {
            LOG.error("CategoryController.ajaxDeleteService异常!");
            LOG.error(e.getMessage(), e);
            return new Result("删除失败", "删除时出错，请重试！", false);
        }
    }


  @RequestMapping(params = "method=batchUpdateServiceCategory")
  @ResponseBody
  public Object batchUpdateServiceCategory(ModelMap model, HttpServletRequest request) {
    Result result = new Result();
    result.setSuccess(false);
    try {

      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Long shopId = WebUtil.getShopId(request);

      String ids = request.getParameter("ids");
      String categoryName = request.getParameter("categoryName");
      String categoryIdStr = request.getParameter("categoryId");
      if (StringUtils.isEmpty(ids) || StringUtil.isEmpty(categoryName) || ArrayUtil.isEmpty(ids.split(","))) {
        return result;
      }
      Set<Long> serviceIdSet = new HashSet<Long>();

      for (String id : ids.split(",")) {
        if (!NumberUtil.isLongNumber(id)) {
          continue;
        }
        serviceIdSet.add(Long.valueOf(id));
      }

      if(CollectionUtils.isEmpty(serviceIdSet)){
        return result;
      }

      Map<Long, ServiceDTO> serviceDTOMap = txnService.getServiceByServiceIdSet(shopId, serviceIdSet);
      Map<Long, CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapByServiceIds(shopId, serviceIdSet);

      CategoryServiceSearchDTO categoryServiceSearchDTO = new CategoryServiceSearchDTO();
      List<ServiceDTO> serviceDTOList = new ArrayList<ServiceDTO>();
      if (MapUtils.isNotEmpty(serviceDTOMap)) {
        for (ServiceDTO serviceDTO : serviceDTOMap.values()) {
          serviceDTOList.add(serviceDTO);
          CategoryDTO categoryDTO = categoryDTOMap.get(serviceDTO.getId());
          if (categoryDTO != null) {
            serviceDTO.setCategoryId(categoryDTO.getId());
            serviceDTO.setCategoryName(categoryDTO.getCategoryName());
            serviceDTO.setCategoryType(categoryDTO.getCategoryType());
          }
        }
      }
      categoryServiceSearchDTO.setServiceDTOs(serviceDTOList.toArray(new ServiceDTO[serviceDTOList.size()]));


      Long categoryId = NumberUtil.isLongNumber(categoryIdStr) ? Long.valueOf(categoryIdStr) : null;
      categoryServiceSearchDTO.setServiceDTOs(rfiTxnService.updateServiceCategory(shopId, categoryId, categoryName, categoryServiceSearchDTO.getServiceDTOs()));
      result.setSuccess(true);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result;
    }
  }
}
