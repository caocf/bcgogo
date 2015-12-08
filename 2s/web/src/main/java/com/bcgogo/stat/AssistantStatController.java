package com.bcgogo.stat;

import com.bcgogo.common.*;
import com.bcgogo.common.StringUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.*;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.assistantStat.*;
import com.bcgogo.txn.model.assistantStat.*;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 员工业绩统计 新controller
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-25
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/assistantStat.do")
public class AssistantStatController {
  private static final Log LOG = LogFactory.getLog(AssistantStatController.class);
  public static final long PAGE_SIZE = 25;                                                //页面显示条数

  /**
   * 查询施工项目
   *
   * @param request
   * @param categoryServiceSearchDTO
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=searchServiceConfig")
  public Object searchServiceConfig(HttpServletRequest request, CategoryServiceSearchDTO categoryServiceSearchDTO) {

    List returnList = new ArrayList<Object>();
    Pager pager = null;
    try {
      RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long pageNo = 1l;
      if (request.getParameter("startPageNo") != null && !"".equals(request.getParameter("startPageNo"))) {
        pageNo = Long.parseLong(request.getParameter("startPageNo"));
      }

      if (categoryServiceSearchDTO == null) {
        categoryServiceSearchDTO = new CategoryServiceSearchDTO();
      }
      categoryServiceSearchDTO.setServiceDTOs(txnService.getServicesByCategory(shopId, categoryServiceSearchDTO.getServiceName(),
          categoryServiceSearchDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION, pageNo, PAGE_SIZE));
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
      pager = new Pager(txnService.countServiceByCategory(shopId, categoryServiceSearchDTO.getServiceName(),
          categoryServiceSearchDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION), pageNo.intValue(), (int) PAGE_SIZE);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }

    returnList.add(categoryServiceSearchDTO);
    returnList.add(pager);

    return returnList;
  }

  @RequestMapping(params = "method=redirectSalesManConfig")
  public String redirectSalesManConfig(HttpServletRequest request, ModelMap model) throws IOException {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    try {
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.SERVICE);
      model.addAttribute("totalShopAchievementConfig", totalShopAchievementConfig);

      RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
      int totalNum = txnService.countServiceByCategory(shopId, null, null, CategoryType.BUSINESS_CLASSIFICATION);
      model.addAttribute("totalNum", totalNum);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "/stat/assistantStat/allServiceConfig";
  }

  @RequestMapping(params = "method=redirectProductConfig")
  public String redirectProductConfig(HttpServletRequest request, ModelMap model) throws IOException {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) {
        return "/";
      }

      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.PRODUCT);
      model.addAttribute("totalShopAchievementConfig", totalShopAchievementConfig);

      if (BcgogoShopLogicResourceUtils.isMemberStoredValue(WebUtil.getShopVersionId(request))) {
        List<MemberAchievementHistory> newList = assistantStatService.getMemberAchievementHistory(shopId, System.currentTimeMillis(), MemberOrderType.NEW);
        List<MemberAchievementHistory> renewList = assistantStatService.getMemberAchievementHistory(shopId, System.currentTimeMillis(), MemberOrderType.RENEW);


        MemberAchievementHistoryDTO memberAchievementHistoryDTO = CollectionUtil.getFirst(newList) == null ? null : CollectionUtil.getFirst(newList).toDTO();
        if (memberAchievementHistoryDTO != null) {
          model.addAttribute("memberNewSelect", memberAchievementHistoryDTO.getAchievementMemberType().getName());
          model.addAttribute("memberNewAchievement", memberAchievementHistoryDTO.getAchievementType().getName());
          model.addAttribute("memberNewAmount", memberAchievementHistoryDTO.getAchievementAmount());
        }
        memberAchievementHistoryDTO = CollectionUtil.getFirst(renewList) == null ? null : CollectionUtil.getFirst(renewList).toDTO();
        if (memberAchievementHistoryDTO != null) {
          model.addAttribute("memberRenewSelect", memberAchievementHistoryDTO.getAchievementMemberType().getName());
          model.addAttribute("memberRenewAchievement", memberAchievementHistoryDTO.getAchievementType().getName());
          model.addAttribute("memberRenewAmount", memberAchievementHistoryDTO.getAchievementAmount());
        }
      }

      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
      model.addAttribute("unConfig", request.getParameter("type"));

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    return "/stat/assistantStat/allProductConfig";
  }


  @RequestMapping(params = "method=searchSalesManConfig")
  public String searchSalesManConfig(HttpServletRequest request, ModelMap model) throws IOException {
    String userGroupName = request.getParameter("userGroupName");
    String userGroupId = request.getParameter("userGroupId");
    model.addAttribute("configUserGroupName", userGroupName);
    model.addAttribute("configUserGroupId", userGroupId);

    Long shopId = WebUtil.getShopId(request);
    IUserService userService = ServiceManager.getService(IUserService.class);
    int totalNum = userService.countSalesManByShopIdAndStatus(shopId, null);
    int totalNumDelete = userService.countSalesManByShopIdAndStatus(shopId, SalesManStatus.DELETED);
    model.addAttribute("totalNum", totalNum - totalNumDelete);

    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.ASSISTANT_DEPARTMENT);
    model.addAttribute("totalShopAchievementConfig", totalShopAchievementConfig);
    model.addAttribute("unConfig", request.getParameter("type"));
    return "/stat/assistantStat/allSalesManConfig";
  }

  @RequestMapping(params = "method=setProductAchievement")
  public String setProductAchievement() {

    return "stat/assistantStat/setProductConfig";
  }

  @RequestMapping(params = "method=updateProductAchievement")
  @ResponseBody
  public Object updateProductAchievement(HttpServletRequest request) {
    Long shopId = null;
    Result result = new Result();
    result.setSuccess(false);
    try {

      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return result;
      }
      String idListStr = request.getParameter("idList");
      String achievementTypeStr = request.getParameter("salesTotalAchievementType");
      String achievementAmountStr = request.getParameter("salesTotalAchievementAmount");
      if(StringUtil.isEmpty(achievementAmountStr)){
        return result;
      }else{
        achievementAmountStr = achievementAmountStr.replace("%","");
      }

      if (StringUtils.isEmpty(idListStr)) {
        return result;
      }
      if (StringUtil.isEmpty(achievementTypeStr) || !NumberUtil.isNumber(achievementAmountStr)) {
        return result;
      }

      String[] tempIdList = idListStr.split(",");
      Set<Long> idList = new HashSet<Long>();

      for (int i = 0; i < tempIdList.length; i++) {
        idList.add(Long.parseLong(tempIdList[i]));
      }
      if (CollectionUtils.isEmpty(idList)) {
        return result;
      }

      AchievementType achievementType = null;
      if (AchievementType.AMOUNT.name().equals(achievementTypeStr)) {
        achievementType = AchievementType.AMOUNT;
      } else if (AchievementType.RATIO.name().equals(achievementTypeStr)) {
        achievementType = AchievementType.RATIO;
      } else {
        return result;
      }

      assistantStatService.saveOrUpdateProductAchievement(shopId, achievementType, Double.valueOf(achievementAmountStr), WebUtil.getUserId(request), idList);

      result.setSuccess(true);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.PRODUCT);
      result.setData(totalShopAchievementConfig);

      return result;

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result;
    }
  }


  @RequestMapping(params = "method=updateMemberAchievement")
  @ResponseBody
  public Object updateMemberAchievement(HttpServletRequest request) {
    Long shopId = null;
    Result result = new Result();
    result.setSuccess(false);
    try {

      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return result;
      }
      String memberNewSelect = request.getParameter("memberNewSelect");
      String memberNewAchievement = request.getParameter("memberNewAchievement");
      String memberNewAmount = request.getParameter("memberNewAmount");
      String memberOrderTypeStr = request.getParameter("memberOrderType");

      if (StringUtils.isEmpty(memberNewSelect) || StringUtil.isEmpty(memberOrderTypeStr)) {
        return result;
      }
      if(StringUtil.isEmpty(memberNewAmount)){
        return result;
      }else{
        memberNewAmount = memberNewAmount.replace("%","");
      }



      if (StringUtil.isEmpty(memberNewAchievement) || !NumberUtil.isNumber(memberNewAmount)) {
        return result;
      }
      MemberAchievementHistoryDTO memberAchievementHistoryDTO = new MemberAchievementHistoryDTO();

      if (AchievementMemberType.CARD_AMOUNT.getName().equals(memberNewSelect)) {
        memberAchievementHistoryDTO.setAchievementMemberType(AchievementMemberType.CARD_AMOUNT);
      } else if (AchievementMemberType.CARD_TOTAL.getName().equals(memberNewSelect)) {
        memberAchievementHistoryDTO.setAchievementMemberType(AchievementMemberType.CARD_TOTAL);
      } else {
        return result;
      }
      if (AchievementType.AMOUNT.getName().equals(memberNewAchievement)) {
        memberAchievementHistoryDTO.setAchievementType(AchievementType.AMOUNT);
      } else if (AchievementType.RATIO.getName().equals(memberNewAchievement)) {
        memberAchievementHistoryDTO.setAchievementType(AchievementType.RATIO);
      } else {
        return result;
      }

      if (MemberOrderType.NEW.getName().equals(memberOrderTypeStr)) {
        memberAchievementHistoryDTO.setMemberOrderType(MemberOrderType.NEW);
      } else if (MemberOrderType.RENEW.getName().equals(memberOrderTypeStr)) {
        memberAchievementHistoryDTO.setMemberOrderType(MemberOrderType.RENEW);
      } else {
        return result;
      }

      memberAchievementHistoryDTO.setAchievementAmount(Double.valueOf(memberNewAmount));
      memberAchievementHistoryDTO.setShopId(shopId);
      memberAchievementHistoryDTO.setChangeTime(System.currentTimeMillis());
      memberAchievementHistoryDTO.setChangeUserId(WebUtil.getUserId(request));

      assistantStatService.saveMemberAchievementHistory(memberAchievementHistoryDTO);
      result.setSuccess(true);
      return result;

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result;
    }
  }


  @RequestMapping(params = "method=getProductAchievementByPager")
  public void getProductAchievementByPager(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                           SearchConditionDTO searchConditionDTO) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    searchConditionDTO.setShopId(shopId);
    ProductSearchResultListDTO productSearchResultListDTO = new ProductSearchResultListDTO();
    try {
      PrintWriter writer = response.getWriter();
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.PRODUCT);
      Pager pager = new Pager(totalShopAchievementConfig, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      if (totalShopAchievementConfig <= 0) {
        productSearchResultListDTO.setPager(pager);
        String jsonStr = JsonUtil.objectToJson(productSearchResultListDTO);
        writer.write(jsonStr);
        writer.close();
        return;
      }

      List<ShopAchievementConfigDTO> shopAchievementConfigDTOList = assistantStatService.getShopAchievementConfigByPager(shopId, null, AssistantRecordType.PRODUCT, pager);
      if (CollectionUtils.isEmpty(shopAchievementConfigDTOList)) {
        productSearchResultListDTO.setPager(pager);
        String jsonStr = JsonUtil.objectToJson(productSearchResultListDTO);
        writer.write(jsonStr);
        writer.close();
        return;
      }

      Set<Long> productIdSet = new HashSet<Long>();
      StringBuffer productIdStr = new StringBuffer();
      for (ShopAchievementConfigDTO shopAchievementConfigDTO : shopAchievementConfigDTOList) {
        productIdSet.add(shopAchievementConfigDTO.getAchievementRecordId());
        productIdStr.append(shopAchievementConfigDTO.getAchievementRecordId()).append(",");
      }
      String productId = productIdStr.substring(0, productIdStr.length() - 1);
      searchConditionDTO.setProductIds(productId);

      searchConditionDTO.setMaxRows(totalShopAchievementConfig);
      if (StringUtils.isBlank(searchConditionDTO.getSort()) && searchConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
        searchConditionDTO.setSort("storage_time desc,inventory_amount desc");
      } else {
        searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSort()));
      }
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_STATS});
      if (searchConditionDTO.getStorehouseId() != null) {
        searchConditionDTO.setStatsFields(new String[]{searchConditionDTO.getStorehouseId() + "_storehouse_inventory_amount", searchConditionDTO.getStorehouseId() + "_storehouse_inventory_price"});
      } else {
        searchConditionDTO.setStatsFields(new String[]{"inventory_amount", "inventory_price"});
      }
      //配合ajaxPaging.tag 接口的数据封装
      searchConditionDTO.setRows(searchConditionDTO.getMaxRows());
      searchConditionDTO.setStart(0);
      //对应field库存查询
      //不知道field的情况下
      productSearchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
      productSearchResultListDTO.setPager(pager);
      productSearchResultListDTO.setNumFound(pager.getTotalRows());
      String jsonStr = JsonUtil.objectToJson(productSearchResultListDTO);

      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("/stockSearch.do method=searchProductForStockSearch");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("searchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
  }



  /**
   * 修改员工部门
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=updateSalesManInfoDepartment")
  @ResponseBody
  public Result updateSalesManInfoDepartment(HttpServletRequest request, String idListStr, String departmentIdStr, String departmentName) {

    Result result = new Result();
    result.setSuccess(false);
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      if (shopId == null || userId == null || StringUtil.isEmpty(idListStr) || StringUtil.isEmpty(departmentIdStr) || StringUtil.isEmpty(departmentName)) {
        return result;
      }

      String[] idList = idListStr.split(",");
      if (ArrayUtils.isEmpty(idList)) {
        return result;
      }
      List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();

      for (String id : idList) {
        SalesManDTO salesManDTO = new SalesManDTO();
        salesManDTO.setShopId(shopId);
        salesManDTO.setId(Long.valueOf(id));
        salesManDTO.setDepartmentId(Long.valueOf(departmentIdStr));
        salesManDTO.setDepartmentName(departmentName);
        salesManDTOList.add(salesManDTO);
      }


      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      assistantStatService.updateSalesManDepartment(shopId, salesManDTOList, userId);

      result.setSuccess(true);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.ASSISTANT_DEPARTMENT);
      result.setData(totalShopAchievementConfig);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
    }

    return result;
  }


  @RequestMapping(params = "method=getAssistantAchievementByPager")
  @ResponseBody
  public PagingDetailResult<SalesManDTO, Long> getAssistantAchievementByPager(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                               SearchConditionDTO searchConditionDTO) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    searchConditionDTO.setShopId(shopId);
    PagingDetailResult<SalesManDTO, Long> result = new PagingDetailResult<SalesManDTO, Long>();
    result.setSuccess(false);

    try {
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);

      result.setSuccess(true);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.ASSISTANT_DEPARTMENT);
      Pager pager = new Pager(totalShopAchievementConfig, searchConditionDTO.getStartPageNo(), (int) PAGE_SIZE);

      result.setPager(pager);
      if (totalShopAchievementConfig <= 0) {
        return result;
      }

      List<ShopAchievementConfigDTO> shopAchievementConfigDTOList = assistantStatService.getShopAchievementConfigByPager(shopId, null, AssistantRecordType.ASSISTANT_DEPARTMENT, pager);
      if (CollectionUtils.isEmpty(shopAchievementConfigDTOList)) {
        return result;
      }

      Set<Long> assistantIdSet = new HashSet<Long>();
      for (ShopAchievementConfigDTO shopAchievementConfigDTO : shopAchievementConfigDTOList) {
        assistantIdSet.add(shopAchievementConfigDTO.getAchievementRecordId());
      }

      if (CollectionUtils.isEmpty(assistantIdSet)) {
        return result;
      }

      Map<Long, SalesManDTO> salesManDTOMap = userService.getSalesManByIdSet(shopId, assistantIdSet);

      List<SalesManDTO> salesManDTOList = new ArrayList<SalesManDTO>();
      if (MapUtils.isNotEmpty(salesManDTOMap)) {
        for (SalesManDTO salesManDTO : salesManDTOMap.values()) {
          salesManDTOList.add(salesManDTO);
        }
      }

      result.setResults(salesManDTOList);
      result.setSuccess(true);
      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.error("/assistantStat.do method=getAssistantAchievementByPager");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("searchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
      return result;
    }
  }





  @RequestMapping(params = "method=getServiceAchievementByPager")
  @ResponseBody
  public Object getServiceAchievementByPager(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                             SearchConditionDTO searchConditionDTO) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    searchConditionDTO.setShopId(shopId);
    Map<String, Object> returnMap = new HashMap<String, Object>();

    try {
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);

      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.SERVICE);
      Pager pager = new Pager(totalShopAchievementConfig, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());

      if (totalShopAchievementConfig <= 0) {
        returnMap.put("pager", pager);
        returnMap.put("categoryServiceSearchDTO", new CategoryServiceSearchDTO());
        returnMap.put("result", new Result(true));
        return returnMap;
      }

      List<ShopAchievementConfigDTO> shopAchievementConfigDTOList = assistantStatService.getShopAchievementConfigByPager(shopId, null, AssistantRecordType.SERVICE, pager);
      if (CollectionUtils.isEmpty(shopAchievementConfigDTOList)) {
        returnMap.put("pager", pager);
        returnMap.put("categoryServiceSearchDTO", new CategoryServiceSearchDTO());
        returnMap.put("result", new Result(true));;
        return returnMap;
      }

      Set<Long> serviceIdSet = new HashSet<Long>();
      for (ShopAchievementConfigDTO shopAchievementConfigDTO : shopAchievementConfigDTOList) {
        serviceIdSet.add(shopAchievementConfigDTO.getAchievementRecordId());
      }

      if (CollectionUtils.isEmpty(serviceIdSet)) {
        returnMap.put("pager", pager);
        returnMap.put("categoryServiceSearchDTO", new CategoryServiceSearchDTO());
        returnMap.put("result", new Result(true));
        return returnMap;
      }

      Map<Long, ServiceDTO> serviceDTOMap = txnService.getServiceByServiceIdSet(shopId, serviceIdSet);

      Map<Long,CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapByServiceIds(shopId,serviceIdSet);


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


      returnMap.put("pager", pager);
      returnMap.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
      returnMap.put("result", new Result(true));
      return returnMap;

    } catch (Exception e) {
      LOG.error("/assistantStat.do method=getServiceAchievementByPager");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("searchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
      returnMap.put("result", new Result(false));
      return returnMap;
    }
  }


  @RequestMapping(params = "method=updateServiceAchievement")
  @ResponseBody
  public Object updateServiceAchievement(HttpServletRequest request) {
    Long shopId = null;
    Result result = new Result();
    result.setSuccess(false);
    try {

      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return result;
      }
      String idListStr = request.getParameter("idList");
      String achievementTypeStr = request.getParameter("achievementType");
      String achievementAmountStr = request.getParameter("achievementAmount");
      if (StringUtils.isEmpty(idListStr)) {
        return result;
      }
      if (StringUtil.isEmpty(achievementTypeStr) || !NumberUtil.isNumber(achievementAmountStr)) {
        return result;
      }

      String[] tempIdList = idListStr.split(",");
      Set<Long> idList = new HashSet<Long>();

      for (int i = 0; i < tempIdList.length; i++) {
        idList.add(Long.parseLong(tempIdList[i]));
      }
      if (CollectionUtils.isEmpty(idList)) {
        return result;
      }

      AchievementType achievementType = null;
      if (AchievementType.AMOUNT.name().equals(achievementTypeStr)) {
        achievementType = AchievementType.AMOUNT;
      } else if (AchievementType.RATIO.name().equals(achievementTypeStr)) {
        achievementType = AchievementType.RATIO;
      } else {
        return result;
      }

      assistantStatService.saveOrUpdateServiceAchievementHistory(shopId, achievementType, Double.valueOf(achievementAmountStr), WebUtil.getUserId(request), idList,null,null);

      result.setSuccess(true);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.PRODUCT);
      result.setData(totalShopAchievementConfig);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result;
    }
  }


  @RequestMapping(params = "method=redirectAssistantStat")
  public String redirectAssistantStat(HttpServletRequest request, ModelMap model) throws IOException {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }


    if (NumberUtil.isNumber(request.getParameter("startTime"))) {
      model.addAttribute("startYearHidden", DateUtil.getYear(Long.valueOf(request.getParameter("startTime"))));
      model.addAttribute("startMonthHidden", DateUtil.getMonth(Long.valueOf(request.getParameter("startTime"))));

    } else {
      model.addAttribute("startYearHidden", DateUtil.getYear(System.currentTimeMillis()));
      model.addAttribute("startMonthHidden", DateUtil.getMonth(System.currentTimeMillis()));

    }

    if (NumberUtil.isNumber(request.getParameter("endTime"))) {
      model.addAttribute("endYearHidden", DateUtil.getYear(Long.valueOf(request.getParameter("endTime")) - DateUtil.DAY_MILLION_SECONDS));
      model.addAttribute("endMonthHidden", DateUtil.getMonth(Long.valueOf(request.getParameter("endTime")) - DateUtil.DAY_MILLION_SECONDS));

    } else {
      model.addAttribute("endYearHidden", DateUtil.getYear(System.currentTimeMillis()));
      model.addAttribute("endMonthHidden", DateUtil.getMonth(System.currentTimeMillis()));

    }

    if (StringUtil.isEmpty(request.getParameter("achievementStatType"))) {
      model.addAttribute("achievementStatTypeHidden", "ASSISTANT");
    } else {
      model.addAttribute("achievementStatTypeHidden", request.getParameter("achievementStatType"));
    }
    if (StringUtil.isEmpty(request.getParameter("startPageNoHiddenHidden"))) {
      model.addAttribute("startPageNoHiddenHidden", 1);

    } else {
      model.addAttribute("startPageNoHiddenHidden", request.getParameter("startPageNoHiddenHidden"));
    }

    if (StringUtil.isEmpty(request.getParameter("achievementCalculateWay"))) {
      model.addAttribute("achievementCalculateWayHidden","CALCULATE_BY_DETAIL");
    } else {
      model.addAttribute("achievementCalculateWayHidden", request.getParameter("achievementCalculateWay"));
    }

    if (StringUtil.isEmpty(request.getParameter("assistantOrDepartmentIdStrHidden"))) {
      model.addAttribute("assistantOrDepartmentIdStrHidden", "");
    } else {
      model.addAttribute("assistantOrDepartmentIdStrHidden", request.getParameter("assistantOrDepartmentIdStrHidden"));
    }

    if (StringUtil.isEmpty(request.getParameter("achievementOrderTypeStrHidden"))) {
      model.addAttribute("achievementOrderTypeStrHidden", "ALL");
    } else {
      model.addAttribute("achievementOrderTypeStrHidden", request.getParameter("achievementOrderTypeStrHidden"));
    }

    if (StringUtil.isEmpty(request.getParameter("serviceIdStrHidden"))) {
      model.addAttribute("serviceIdStrHidden", "");
    } else {
      model.addAttribute("serviceIdStrHidden", request.getParameter("serviceIdStrHidden"));
    }

    return "/stat/assistantStat/assistantStat";
  }

  @RequestMapping(params = "method=printAssistantStat")
  @ResponseBody
  public void printAssistantStat(ModelMap modelMap,HttpServletRequest request, HttpServletResponse response, AssistantStatSearchDTO assistantStatSearchDTO){
    List result=getAssistantStatByPage(request,assistantStatSearchDTO);
    if(result==null) return;
    try{
      assistantStatSearchDTO=(AssistantStatSearchDTO)result.get(0);

      if(CollectionUtils.isNotEmpty(assistantStatSearchDTO.getAssistantAchievementStatDTOList())) {
        for (AssistantAchievementStatDTO statDTO : assistantStatSearchDTO.getAssistantAchievementStatDTOList()) {
          if (AchievementCalculateWay.CALCULATE_BY_ASSISTANT.name().equals(assistantStatSearchDTO.getAchievementCalculateWayStr())) {
            statDTO.setServiceAchievement(statDTO.getServiceAchievementByAssistant());
            statDTO.setSaleAchievement(statDTO.getSalesAchievementByAssistant());
            statDTO.setSalesProfitAchievement(statDTO.getSalesProfitAchievementByAssistant());
            statDTO.setWashAchievement(statDTO.getWashAchievementByAssistant());
            statDTO.setMemberAchievement(statDTO.getMemberAchievementByAssistant());
            statDTO.setMemberRenewAchievement(statDTO.getMemberRenewAchievementByAssistant());
            statDTO.setAchievementSum(statDTO.getAchievementSumByAssistant());
          }

          if (com.bcgogo.utils.StringUtil.isEmpty(assistantStatSearchDTO.getServiceIdStr())) {
            statDTO.setMemberAchievement(NumberUtil.toReserve(NumberUtil.doubleVal(statDTO.getMemberAchievement()) + NumberUtil.doubleVal(statDTO.getMemberRenewAchievement()), NumberUtil.MONEY_PRECISION));
            statDTO.setMember(NumberUtil.toReserve(NumberUtil.doubleVal(statDTO.getMember()) + NumberUtil.doubleVal(statDTO.getMemberRenew()), NumberUtil.MONEY_PRECISION));
          } else if (MemberOrderType.RENEW.name().equals(assistantStatSearchDTO.getServiceIdStr())) {
            statDTO.setMemberAchievement(NumberUtil.toReserve(NumberUtil.doubleVal(statDTO.getMemberRenewAchievement()), NumberUtil.MONEY_PRECISION));
            statDTO.setMember(NumberUtil.toReserve(NumberUtil.doubleVal(statDTO.getMemberRenew()), NumberUtil.MONEY_PRECISION));
          }
        }
      }

      IPrintService printService = ServiceManager.getService(IPrintService.class);
       IConfigService configService = ServiceManager.getService(IConfigService.class);
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.ASSISTENT_STAT);
       ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      byte templateHtmlBytes[]=printTemplateDTO.getTemplateHtml();
      //创建资源库
      String myTemplateName = "ASSISTENT_STAT"+ String.valueOf(WebUtil.getShopId(request));
      VelocityContext context = new VelocityContext();
      context.put("assistantStat", assistantStatSearchDTO);
      context.put("type",assistantStatSearchDTO.getAchievementOrderTypeStr());
      context.put("shopDTO",shopDTO);
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
      context.put("queryStr",assistantStatSearchDTO.getQueryConditionStr());

      String printDateStr  = DateUtil.dateLongToStr(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_CN);
      context.put("printDateStr",printDateStr);

      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=getAssistantStatByPage")
  @ResponseBody
  public List getAssistantStatByPage(HttpServletRequest request, AssistantStatSearchDTO assistantStatSearchDTO) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return null;
      }
      assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
      assistantStatSearchDTO.setShopId(shopId);
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      List returnList = new ArrayList();

      if (StringUtil.isEmpty(assistantStatSearchDTO.getAchievementStatTypeStr())) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }
      assistantStatSearchDTO.setTime();

      List<Long> resultList = assistantStatService.countAssistantStatByCondition(assistantStatSearchDTO);
      int totalNum = CollectionUtils.isEmpty(resultList) ? 0 : resultList.size();

      if (totalNum <= 0) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Pager pager = new Pager(totalNum, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows());

      Set<Long> ids = new HashSet<Long>();

      for (int index = 0; index < assistantStatSearchDTO.getMaxRows(); index++) {
        if (index + pager.getRowStart() < resultList.size()) {
          ids.add(resultList.get(index + pager.getRowStart()));
        }
      }

      if (CollectionUtils.isEmpty(ids)) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      List<AssistantAchievementStatDTO> assistantAchievementStatDTOList = assistantStatService.getAssistantStatByIds(assistantStatSearchDTO, ids);


      assistantStatSearchDTO.setAssistantAchievementStatDTOList(assistantAchievementStatDTOList);

      returnList.add(assistantStatSearchDTO);
      returnList.add(pager);
      return returnList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      List returnList = new ArrayList();
      returnList.add(assistantStatSearchDTO);
      returnList.add(new Pager());
      return returnList;
    }
  }


  @RequestMapping(params = "method=getAssistantServiceByPage")
  @ResponseBody
  public List getAssistantServiceByPage(HttpServletRequest request, AssistantStatSearchDTO assistantStatSearchDTO, String orderType) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return null;
      }

      assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      List returnList = new ArrayList();

      assistantStatSearchDTO.setShopId(shopId);
      assistantStatSearchDTO.setTime();
      assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.SERVICE);

      if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
      if (StringUtils.isNotEmpty(orderType) && "washBeauty".equals(orderType)) {
        orderTypesSet.add(OrderTypes.WASH_BEAUTY);
      } else {
        orderTypesSet.add(OrderTypes.REPAIR);
      }

      int totalNum = assistantStatService.countAssistantRecordByCondition(assistantStatSearchDTO, orderTypesSet);
      if (totalNum <= 0) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Pager pager = new Pager(totalNum, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows());

      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, pager);

      List<AssistantServiceRecordDTO> assistantServiceRecordDTOList = new ArrayList<AssistantServiceRecordDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantServiceRecord assistantServiceRecord = (AssistantServiceRecord) object;
          assistantServiceRecordDTOList.add(assistantServiceRecord.toDTO());
        }
      }
      assistantStatSearchDTO.setServiceRecordDTOList(assistantServiceRecordDTOList);

      returnList.add(assistantStatSearchDTO);
      returnList.add(pager);
      return returnList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      List returnList = new ArrayList();
      returnList.add(assistantStatSearchDTO);
      returnList.add(new Pager());
      return returnList;
    }
  }


  @RequestMapping(params = "method=redirectServiceRecord")
  public String redirectServiceRecord(HttpServletRequest request, ModelMap model, AssistantStatSearchDTO assistantStatSearchDTO) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
    assistantStatSearchDTO.setTime();

    model.addAttribute("startTime", assistantStatSearchDTO.getStartTime());
    model.addAttribute("endTime", assistantStatSearchDTO.getEndTime());
    model.addAttribute("orderType","repair");
    model.addAttribute("achievementStatTypeStr", assistantStatSearchDTO.getAchievementStatTypeStr());
    model.addAttribute("assistantOrDepartmentId", assistantStatSearchDTO.getAssistantOrDepartmentIdStr());
    model.addAttribute("startPageNoHiddenHidden", request.getParameter("startPageNo"));
    model.addAttribute("achievementOrderTypeStrHidden", assistantStatSearchDTO.getAchievementOrderTypeStr());
    model.addAttribute("serviceIdStrHidden", assistantStatSearchDTO.getServiceIdStr());
    model.addAttribute("achievementCalculateWayHidden", assistantStatSearchDTO.getAchievementCalculateWayStr());
    model.addAttribute("assistantStatSearchDTO", assistantStatSearchDTO);
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
    return "/stat/assistantStat/assistantStatDetail";
  }

  @RequestMapping(params = "method=redirectWashRecord")
  public String redirectWashRecord(HttpServletRequest request, ModelMap model, AssistantStatSearchDTO assistantStatSearchDTO) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
    assistantStatSearchDTO.setTime();
    model.addAttribute("orderType","washBeauty");
    model.addAttribute("startTime", assistantStatSearchDTO.getStartTime());
    model.addAttribute("endTime", assistantStatSearchDTO.getEndTime());
    model.addAttribute("achievementStatTypeStr", assistantStatSearchDTO.getAchievementStatTypeStr());
    model.addAttribute("assistantOrDepartmentId", assistantStatSearchDTO.getAssistantOrDepartmentIdStr());
    model.addAttribute("startPageNoHiddenHidden", request.getParameter("startPageNo"));
    model.addAttribute("achievementOrderTypeStrHidden", assistantStatSearchDTO.getAchievementOrderTypeStr());
    model.addAttribute("serviceIdStrHidden", assistantStatSearchDTO.getServiceIdStr());
    model.addAttribute("achievementCalculateWayHidden", assistantStatSearchDTO.getAchievementCalculateWayStr());
    model.addAttribute("assistantStatSearchDTO", assistantStatSearchDTO);
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);

    return "/stat/assistantStat/assistantWashRecord";
  }

  @RequestMapping(params = "method=getAssistantProductByPage")
  @ResponseBody
  public List getAssistantProductByPage(HttpServletRequest request, AssistantStatSearchDTO assistantStatSearchDTO) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return null;
      }

      assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      List returnList = new ArrayList();
      assistantStatSearchDTO.setShopId(shopId);

      assistantStatSearchDTO.setTime();
      assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.PRODUCT);

      Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
      orderTypesSet.add(OrderTypes.REPAIR);
      orderTypesSet.add(OrderTypes.SALE);
      orderTypesSet.add(OrderTypes.SALE_RETURN);

      if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      int totalNum = assistantStatService.countAssistantRecordByCondition(assistantStatSearchDTO, orderTypesSet);
      if (totalNum <= 0) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Pager pager = new Pager(totalNum, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows());

      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, pager);

      List<AssistantProductRecordDTO> assistantProductRecordDTOList = new ArrayList<AssistantProductRecordDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantProductRecord assistantProductRecord = (AssistantProductRecord) object;
          assistantProductRecordDTOList.add(assistantProductRecord.toDTO());
        }
      }
      assistantStatSearchDTO.setProductRecordDTOList(assistantProductRecordDTOList);

      returnList.add(assistantStatSearchDTO);
      returnList.add(pager);
      return returnList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      List returnList = new ArrayList();
      returnList.add(assistantStatSearchDTO);
      returnList.add(new Pager());
      return returnList;
    }
  }


  @RequestMapping(params = "method=redirectProductRecord")
  public String redirectProductRecord(HttpServletRequest request, ModelMap model, AssistantStatSearchDTO assistantStatSearchDTO) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
    assistantStatSearchDTO.setTime();

    model.addAttribute("startTime", assistantStatSearchDTO.getStartTime());
    model.addAttribute("endTime", assistantStatSearchDTO.getEndTime());
    model.addAttribute("achievementStatTypeStr", assistantStatSearchDTO.getAchievementStatTypeStr());
    model.addAttribute("assistantOrDepartmentId", assistantStatSearchDTO.getAssistantOrDepartmentIdStr());
    model.addAttribute("startPageNoHiddenHidden", request.getParameter("startPageNo"));
    model.addAttribute("achievementOrderTypeStrHidden", assistantStatSearchDTO.getAchievementOrderTypeStr());
    model.addAttribute("serviceIdStrHidden", assistantStatSearchDTO.getServiceIdStr());
    model.addAttribute("achievementCalculateWayHidden", assistantStatSearchDTO.getAchievementCalculateWayStr());
    model.addAttribute("assistantStatSearchDTO", assistantStatSearchDTO);
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
    return "/stat/assistantStat/assistantProductRecord";
  }


  @RequestMapping(params = "method=getAssistantMemberByPage")
  @ResponseBody
  public List getAssistantMemberByPage(HttpServletRequest request, AssistantStatSearchDTO assistantStatSearchDTO) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return null;
      }

      assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      List returnList = new ArrayList();

      assistantStatSearchDTO.setShopId(shopId);
      assistantStatSearchDTO.setTime();
      assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.MEMBER_NEW);

      if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
      orderTypesSet.add(OrderTypes.MEMBER_BUY_CARD);
      orderTypesSet.add(OrderTypes.MEMBER_RETURN_CARD);


      int totalNum = assistantStatService.countAssistantRecordByCondition(assistantStatSearchDTO, orderTypesSet);
      if (totalNum <= 0) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Pager pager = new Pager(totalNum, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows());

      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, pager);

      List<AssistantMemberRecordDTO> assistantMemberRecordDTOList = new ArrayList<AssistantMemberRecordDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantMemberRecord assistantMemberRecord = (AssistantMemberRecord) object;
          assistantMemberRecordDTOList.add(assistantMemberRecord.toDTO());
        }
      }
      assistantStatSearchDTO.setMemberRecordDTOList(assistantMemberRecordDTOList);

      returnList.add(assistantStatSearchDTO);
      returnList.add(pager);
      return returnList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      List returnList = new ArrayList();
      returnList.add(assistantStatSearchDTO);
      returnList.add(new Pager());
      return returnList;
    }
  }

  @RequestMapping(params = "method=printAssistantMember")
  @ResponseBody
  public void printAssistantMember(ModelMap modelMap,HttpServletRequest request, HttpServletResponse response, AssistantStatSearchDTO assistantStatSearchDTO){
    List result=getAssistantMemberByPage(request,assistantStatSearchDTO);
    if(result==null) return;
    try{
      assistantStatSearchDTO=(AssistantStatSearchDTO)result.get(0);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.ASSISTENT_MEMBER_CARD_STAT);
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      byte templateHtmlBytes[]=printTemplateDTO.getTemplateHtml();
      //创建资源库
      String myTemplateName = "ASSISTENT_MEMBER_CARD_STAT"+ String.valueOf(WebUtil.getShopId(request));
      VelocityContext context = new VelocityContext();
      context.put("assistantStat", assistantStatSearchDTO);
      context.put("achievementCalculateWayStr", assistantStatSearchDTO.getAchievementCalculateWayStr());
      context.put("shopDTO",shopDTO);
      String printDateStr  = DateUtil.dateLongToStr(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_CN);
      context.put("printDateStr",printDateStr);
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
      context.put("queryStr",assistantStatSearchDTO.getQueryConditionStr());
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=printAssistantService")
  @ResponseBody
  public void printAssistantService(HttpServletRequest request, HttpServletResponse response, AssistantStatSearchDTO assistantStatSearchDTO,String orderTypeStr){
    List result=getAssistantServiceByPage(request,assistantStatSearchDTO,orderTypeStr);
    if(result==null) return;
    try{
      assistantStatSearchDTO=(AssistantStatSearchDTO)result.get(0);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      OrderTypes orderTypes=null;
       if (StringUtils.isNotEmpty(orderTypeStr) && "washBeauty".equals(orderTypeStr)) {
        orderTypes=OrderTypes.ASSISTENT_WASH_STAT;
       } else {
         orderTypes=OrderTypes.ASSISTENT_SERVICE_STAT;
       }
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request),orderTypes);
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      byte templateHtmlBytes[]=printTemplateDTO.getTemplateHtml();
      //创建资源库
      String myTemplateName ="";
      if (StringUtils.isNotEmpty(orderTypeStr) && "washBeauty".equals(orderTypeStr)) {
        myTemplateName = "ASSISTENT_WASH_STAT"+ String.valueOf(WebUtil.getShopId(request));
      } else {
        myTemplateName = "ASSISTENT_SERVICE_STAT"+ String.valueOf(WebUtil.getShopId(request));
      }
      VelocityContext context = new VelocityContext();
      context.put("assistantStat", assistantStatSearchDTO);
      context.put("achievementCalculateWayStr", assistantStatSearchDTO.getAchievementCalculateWayStr());
      context.put("shopDTO",shopDTO);
      String printDateStr  = DateUtil.dateLongToStr(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_CN);
      context.put("printDateStr",printDateStr);
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
      context.put("queryStr",assistantStatSearchDTO.getQueryConditionStr());
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=printAssistantProduct")
  @ResponseBody
  public void printAssistantProduct(HttpServletRequest request, HttpServletResponse response, AssistantStatSearchDTO assistantStatSearchDTO){
    List result=getAssistantProductByPage(request,assistantStatSearchDTO);
    if(result==null) return;
    try{
      assistantStatSearchDTO=(AssistantStatSearchDTO)result.get(0);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request),OrderTypes.ASSISTENT_PRODUCT_STAT);
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      byte templateHtmlBytes[]=printTemplateDTO.getTemplateHtml();
      //创建资源库
      String myTemplateName ="ASSISTENT_PRODUCT_STAT"+ String.valueOf(WebUtil.getShopId(request));
      VelocityContext context = new VelocityContext();
      context.put("assistantStat", assistantStatSearchDTO);
      context.put("achievementCalculateWayStr", assistantStatSearchDTO.getAchievementCalculateWayStr());
      context.put("shopDTO",shopDTO);
      String printDateStr  = DateUtil.dateLongToStr(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_CN);
      context.put("printDateStr",printDateStr);
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
      context.put("queryStr",assistantStatSearchDTO.getQueryConditionStr());
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=redirectMemberRecord")
  public String redirectMemberRecord(HttpServletRequest request, ModelMap model, AssistantStatSearchDTO assistantStatSearchDTO) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
    assistantStatSearchDTO.setTime();
    model.addAttribute("startTime", assistantStatSearchDTO.getStartTime().toString());
    model.addAttribute("endTime", assistantStatSearchDTO.getEndTime().toString());
    model.addAttribute("achievementStatTypeStr", assistantStatSearchDTO.getAchievementStatTypeStr());
    model.addAttribute("assistantOrDepartmentId", assistantStatSearchDTO.getAssistantOrDepartmentIdStr());
    model.addAttribute("startPageNoHiddenHidden", request.getParameter("startPageNo"));
    model.addAttribute("assistantStatSearchDTO", assistantStatSearchDTO);

    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);


    return "/stat/assistantStat/assistantMemberRecord";
  }

  @RequestMapping(params = "method=assistantStatByShopId")
  public String redirectMemberRecord(HttpServletRequest request,ModelMap model) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    assistantStatService.assistantStat(shopId);
    IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
    businessAccountService.assistantStatBusinessAccountStat(shopId);


    model.addAttribute("startYearHidden", DateUtil.getYear(System.currentTimeMillis()));
    model.addAttribute("startMonthHidden", DateUtil.getMonth(System.currentTimeMillis()));
    model.addAttribute("endYearHidden", DateUtil.getYear(System.currentTimeMillis()));
    model.addAttribute("endMonthHidden", DateUtil.getMonth(System.currentTimeMillis()));

    model.addAttribute("achievementStatTypeHidden", "ASSISTANT");

    model.addAttribute("startPageNoHiddenHidden", 1);

    return "/stat/assistantStat/assistantStat";
  }

  @RequestMapping(params = "method=getSalesManConfigNum")
  @ResponseBody
  public Object getSalesManConfigNum(HttpServletRequest request, ModelMap model) {
    Result result = new Result();
    try {
      result.setSuccess(false);
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.ASSISTANT_DEPARTMENT);
      result.setSuccess(true);
      result.setData(totalShopAchievementConfig);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getProductConfigNum")
  @ResponseBody
  public Object getProductConfigNum(HttpServletRequest request, ModelMap model) {
    Result result = new Result();
    try {
      result.setSuccess(false);
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "/";
      }
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.PRODUCT);
      result.setSuccess(true);
      result.setData(totalShopAchievementConfig);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=updateProductProfitAchievement")
  @ResponseBody
  public Object updateProductProfitAchievement(HttpServletRequest request) {
    Long shopId = null;
    Result result = new Result();
    result.setSuccess(false);
    try {

      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return result;
      }
      String idListStr = request.getParameter("idList");
      String salesProfitAchievementTypeStr = request.getParameter("salesProfitAchievementType");
      String salesProfitAchievementAmountStr = request.getParameter("salesProfitAchievementAmount");
      if(StringUtil.isEmpty(salesProfitAchievementAmountStr)){
        return result;
      }else{
        salesProfitAchievementAmountStr = salesProfitAchievementAmountStr.replace("%","");
      }

      if (StringUtils.isEmpty(idListStr)) {
        return result;
      }
      if (StringUtil.isEmpty(salesProfitAchievementTypeStr) || !NumberUtil.isNumber(salesProfitAchievementAmountStr)) {
        return result;
      }

      String[] tempIdList = idListStr.split(",");
      Set<Long> idList = new HashSet<Long>();

      for (int i = 0; i < tempIdList.length; i++) {
        idList.add(Long.parseLong(tempIdList[i]));
      }
      if (CollectionUtils.isEmpty(idList)) {
        return result;
      }

      AchievementType achievementType = null;
      if (AchievementType.AMOUNT.name().equals(salesProfitAchievementTypeStr)) {
        achievementType = AchievementType.AMOUNT;
      } else if (AchievementType.RATIO.name().equals(salesProfitAchievementTypeStr)) {
        achievementType = AchievementType.RATIO;
      } else {
        return result;
      }

      assistantStatService.saveProductSalesProfitAchievement(shopId, achievementType, Double.valueOf(salesProfitAchievementAmountStr), WebUtil.getUserId(request), idList);

      result.setSuccess(true);
      int totalShopAchievementConfig = assistantStatService.countShopAchievementConfig(shopId, null, AssistantRecordType.PRODUCT);
      result.setData(totalShopAchievementConfig);

      return result;

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result;
    }
  }

  @RequestMapping(params = "method=getAssistantStatList")
  @ResponseBody
  public Object getSalesManOrDepartment(HttpServletRequest request,HttpServletResponse response) {

    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return null;
    }
    Map<String, Object> map = new HashMap<String, Object>();


    List<DepartmentDTO> departmentDTOList = ServiceManager.getService(IUserCacheService.class).getDepartmentByName(shopId, "");
    map.put(AchievementStatType.DEPARTMENT.name(), departmentDTOList);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SalesManDTO> salesManDTOList = userService.getSalesManByDepartmentId(shopId, null);
    map.put(AchievementStatType.ASSISTANT.name(), salesManDTOList);
    IAssistantStatService statService = ServiceManager.getService(IAssistantStatService.class);
    map.put("service", statService.getShopAllStatServiceByShopId(shopId));
    return map;
  }

  @RequestMapping(params = "method=getShopAllServiceByShopId")
  @ResponseBody
  public Object getShopAllServiceByShopId(HttpServletRequest request, HttpServletResponse response) {

    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return null;
    }
    IAssistantStatService statService = ServiceManager.getService(IAssistantStatService.class);
    return statService.getShopAllStatServiceByShopId(shopId);

  }

  @RequestMapping(params = "method=redirectBusinessAccountRecord")
  public String redirectBusinessAccountRecord(HttpServletRequest request, ModelMap model, AssistantStatSearchDTO assistantStatSearchDTO) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
    assistantStatSearchDTO.setTime();

    model.addAttribute("startTime", assistantStatSearchDTO.getStartTime());
    model.addAttribute("endTime", assistantStatSearchDTO.getEndTime());
    model.addAttribute("orderType","repair");
    model.addAttribute("achievementStatTypeStr", assistantStatSearchDTO.getAchievementStatTypeStr());
    model.addAttribute("assistantOrDepartmentId", assistantStatSearchDTO.getAssistantOrDepartmentIdStr());
    model.addAttribute("startPageNoHiddenHidden", request.getParameter("startPageNo"));
    model.addAttribute("achievementOrderTypeStrHidden", assistantStatSearchDTO.getAchievementOrderTypeStr());
    model.addAttribute("serviceIdStrHidden", assistantStatSearchDTO.getServiceIdStr());
    model.addAttribute("achievementCalculateWayHidden", assistantStatSearchDTO.getAchievementCalculateWayStr());
    model.addAttribute("assistantStatSearchDTO", assistantStatSearchDTO);
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
    return "/stat/assistantStat/assistantBusinessAccountRecord";
  }


  @RequestMapping(params = "method=getAssistantBusinessAccountByPage")
  @ResponseBody
  public List getAssistantBusinessAccountByPage(HttpServletRequest request, AssistantStatSearchDTO assistantStatSearchDTO) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return null;
      }

      assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      List returnList = new ArrayList();

      assistantStatSearchDTO.setShopId(shopId);
      assistantStatSearchDTO.setTime();
      assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.BUSINESS_ACCOUNT);

      if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
      int totalNum = assistantStatService.countAssistantRecordByCondition(assistantStatSearchDTO, orderTypesSet);
      if (totalNum <= 0) {
        returnList.add(assistantStatSearchDTO);
        returnList.add(new Pager(0, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows()));
        return returnList;
      }

      Pager pager = new Pager(totalNum, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows());

      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, pager);

      List<AssistantBusinessAccountRecordDTO> recordDTOs = new ArrayList<AssistantBusinessAccountRecordDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantBusinessAccountRecord businessAccountRecord = (AssistantBusinessAccountRecord) object;
          recordDTOs.add(businessAccountRecord.toDTO());
        }
      }
      assistantStatSearchDTO.setBusinessAccountRecordDTOList(recordDTOs);

      returnList.add(assistantStatSearchDTO);
      returnList.add(pager);
      return returnList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      List returnList = new ArrayList();
      returnList.add(assistantStatSearchDTO);
      returnList.add(new Pager());
      return returnList;
    }
  }

  @RequestMapping(params = "method=printAssistantBusinessAccount")
  @ResponseBody
  public void printAssistantBusinessAccount(HttpServletRequest request, HttpServletResponse response, AssistantStatSearchDTO assistantStatSearchDTO){
    List result=getAssistantBusinessAccountByPage(request,assistantStatSearchDTO);
    if(result==null) return;
    try{
      assistantStatSearchDTO=(AssistantStatSearchDTO)result.get(0);
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request),OrderTypes.ASSISTANT_BUSINESS_ACCOUNT_STAT);
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      byte templateHtmlBytes[]=printTemplateDTO.getTemplateHtml();
      //创建资源库
      String myTemplateName =OrderTypes.ASSISTANT_BUSINESS_ACCOUNT_STAT.name();
      VelocityContext context = new VelocityContext();
      context.put("assistantStat", assistantStatSearchDTO);
      context.put("achievementCalculateWayStr", assistantStatSearchDTO.getAchievementCalculateWayStr());
      context.put("shopDTO",shopDTO);
      String printDateStr  = DateUtil.dateLongToStr(System.currentTimeMillis(),DateUtil.DATE_STRING_FORMAT_CN);
      context.put("printDateStr",printDateStr);
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
      assistantStatService.getAssistantOrDepartmentName(assistantStatSearchDTO);
      context.put("queryStr",assistantStatSearchDTO.getQueryConditionStr());
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

}