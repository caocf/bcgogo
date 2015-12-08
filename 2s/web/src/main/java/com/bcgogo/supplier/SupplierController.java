package com.bcgogo.supplier;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Area;
import com.bcgogo.config.model.MergeRecord;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.enums.user.VehicleBrandModelDataType;
import com.bcgogo.generator.SupplierDTOGenerator;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.dto.JoinSearchConditionDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.dto.supplierComment.*;
import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.WashBeautyOrder;
import com.bcgogo.txn.service.IMergeService;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.supplierComment.IAppUserCommentService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.merge.MergeRecordDTO;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.MergeSupplierSnap;
import com.bcgogo.user.merge.SearchMergeResult;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
 * User: ZhangJuntao
 * Date: 12-9-1
 * Time: 下午2:55
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/supplier.do")
public class SupplierController {
  public static final Logger LOG = LoggerFactory.getLogger(SupplierController.class);

  @Autowired
  private ISupplierService supplierService;

  @ResponseBody
  @RequestMapping(params = "method=searchSupplierDataAction")
  public Object searchSupplierDataAction(HttpServletRequest request, CustomerSupplierSearchConditionDTO searchConditionDTO, JoinSearchConditionDTO joinSearchConditionDTO) {
    Long shopId = WebUtil.getShopId(request);
    LOG.info("查询供应商列表开始!shopId:{}", shopId);
    List<Object> returnList = new ArrayList<Object>();
    Pager pager = null;
    long begin = System.currentTimeMillis();
    long current = begin;
    CustomerSupplierSearchResultListDTO searchResultListDTO = null;
    try {

      searchConditionDTO.setSearchFieldStrategies(new CustomerSupplierSearchConditionDTO.SearchFieldStrategy[]{CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo, CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo});
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      searchConditionDTO.setStart((searchConditionDTO.getStartPageNo() - 1) * searchConditionDTO.getMaxRows());
      searchConditionDTO.setRows(searchConditionDTO.getMaxRows());

      ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
      if (StringUtils.isBlank(searchConditionDTO.getSearchWord()) && StringUtils.isBlank(searchConditionDTO.getSort())) {
        searchConditionDTO.setSort("last_inventory_time desc");
      }
      if (!joinSearchConditionDTO.isEmptyOfProductInfo()) {
        joinSearchConditionDTO.setShopId(WebUtil.getShopId(request));
        joinSearchConditionDTO.setFromColumn("customer_or_supplier_id");
        joinSearchConditionDTO.setToColumn("id");
        joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.ORDER_ITEM_CORE.getValue());
        joinSearchConditionDTO.setItemTypes(ItemTypes.MATERIAL);
        joinSearchConditionDTO.setOrderTypes(new String[]{OrderTypes.INVENTORY.toString(), OrderTypes.RETURN.toString()});
        joinSearchConditionDTO.setOrderStatus(new String[]{OrderStatus.PURCHASE_INVENTORY_DONE.toString(), OrderStatus.SETTLED.toString()});

        searchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
      }

      //
      searchResultListDTO = searchService.querySupplierWithUnknownField(searchConditionDTO);
      if (CollectionUtils.isNotEmpty(searchResultListDTO.getCustomerSuppliers())) {
        List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
        for (CustomerSupplierSearchResultDTO searchResultDTO : searchResultListDTO.getCustomerSuppliers()) {
          supplierDTOList.add(new SupplierDTO(searchResultDTO));
        }
        searchResultListDTO.setSupplierDTOs(supplierDTOList);
      }
      pager = new Pager(NumberUtil.intValue(String.valueOf(searchResultListDTO.getNumFound())), searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      LOG.debug("供应商列表页面--阶段1。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();

      ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
      supplierCommentService.setSupplierCommentStat(searchResultListDTO.getSupplierDTOs(), null);
      returnList.add(searchResultListDTO);
      returnList.add(pager);


      //标识本店是否为批发商，用于控制客户列表的显示列
      if (CollectionUtils.isEmpty(searchResultListDTO.getSupplierDTOs())) {
        return returnList;
      }


    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      returnList = new ArrayList<Object>();
      returnList.add(searchResultListDTO);
      try {
        if (pager == null) {
          pager = new Pager(0, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
        }
      } catch (Exception ex) {
        LOG.error(ex.getMessage(), ex);
      }
      returnList.add(pager);
    }
    LOG.debug("供应商列表页面--阶段2。执行时间: {} ms", System.currentTimeMillis() - current);
    LOG.debug("供应商列表页面。总时间: {} ms", System.currentTimeMillis() - begin);
    return returnList;
  }

  @RequestMapping(params = "method=updateSupplierScore")
  @ResponseBody
  public Object updateSupplierScore(HttpServletRequest request) {
    try {
      IUserService userService = ServiceManager.getService(IUserService.class);
      String rateStr = request.getParameter("rate");
      String idBoxStr = String.valueOf(request.getParameter("idBox"));
      if (StringUtil.isNotEmpty(rateStr) && StringUtil.isNotEmpty(idBoxStr)) {
        Long supplierId = Long.valueOf(idBoxStr);
        SupplierDTO supplierDTO = userService.getSupplierById(supplierId);
        if (supplierDTO != null) {
          supplierDTO.setScore(rateStr);
          userService.updateSupplier(supplierDTO);
        }
      }
    } catch (Exception e) {
      LOG.error("SupplierController.updateSupplierScore");
      LOG.error(e.getMessage(), e);
      return "";
    }
    return "";
  }

  @RequestMapping(params = "method=deleteSupplier")
  @ResponseBody
  public Result deleteSupplier(HttpServletRequest request, SupplierDTO supplierDTO) {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    Result result = new Result();
    result.setSuccess(true);
    if (com.bcgogo.common.StringUtil.isEmpty(supplierDTO.getIdStr())) {
      result.setSuccess(false);
      result.setMsg("供应商信息异常！");
      return result;
    }
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    supplierDTO.setShopId(WebUtil.getShopId(request));
    supplierDTO.setId(NumberUtil.longValue(supplierDTO.getIdStr()));
    String alsoDeleteCustomer = request.getParameter("alsoDeleteCustomer");
    SupplierDTO dbSupplierDTO = supplierService.getSupplierById(supplierDTO.getId(), shopId);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    try {
      Long relateCustomerId = dbSupplierDTO.getCustomerId();
      supplierService.deleteSupplier(result, dbSupplierDTO);
      if (!result.isSuccess()) {
        return result;
      }
      //删除关联关系
      applyService.customerShopDeleteSupplierUpdateRelation(dbSupplierDTO, userId);

      //批发商版本出入库打通逻辑：
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersion(request).getId())) {
        ServiceManager.getService(IProductThroughService.class).updateSupplierInventoryStatusBySupplierId(WebUtil.getShopId(request), supplierDTO.getId(), YesNo.YES);
      }
      // 非关联删除的情况
      if (relateCustomerId == null) {
        ServiceManager.getService(IContactService.class).disabledContactsByIdAndType(dbSupplierDTO.getId(), "supplier", null);
      }

      if (relateCustomerId != null && StringUtils.isNotBlank(alsoDeleteCustomer) && alsoDeleteCustomer.equals("true") && dbSupplierDTO.getPermanentDualRole()) {
        customerService.deleteCustomer(supplierDTO.getShopId(), relateCustomerId);
        ServiceManager.getService(IContactService.class).deleteRelatedCusSupContacts(relateCustomerId, supplierDTO.getId(), "customerAndSupplier", null); // add by zhuj 同时删除的情况
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(relateCustomerId);

        //删除关联关系
        CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(relateCustomerId, shopId);
        applyService.wholesalerShopDeleteCustomerUpdateRelation(customerDTO, userId);
      } else {
        if (relateCustomerId != null) {
          ServiceManager.getService(IContactService.class).deleteRelatedCusSupContacts(relateCustomerId, dbSupplierDTO.getId(), "supplier", null); // add by zhuj 关联只删除供应商的情况
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(relateCustomerId);
        }
      }
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      return result;
    } catch (Exception e) {
      LOG.error("删除供应商出现异常！");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("删除供应商出现异常！");
      return result;
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getMergedSuppliers")
  public Object getMergedSuppliers(ModelMap modelMap, HttpServletRequest request, String[] supplierIds) {
    SearchMergeResult<SupplierDTO> result = new SearchMergeResult<SupplierDTO>();
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    try {
      List<Long> supplierIdList = new ArrayList<Long>();
      if (StringUtil.hasEmptyVal(supplierIds)) {
        result.setSuccess(false);
        result.setMsg("供应商id有误 ！");
        return result;
      }
      if (supplierIds.length != 2) {
        result.setSuccess(false);
        result.setMsg("请选择两个要合并的供应商！");
        return result;
      }
      for (String supplierId : supplierIds) {
        supplierIdList.add(Long.valueOf(supplierId));
      }
      result.setShopId(shopId);
      supplierService.getMergedSuppliers(result, supplierIdList);
      if (!result.isSuccess() || CollectionUtil.isEmpty(result.getResults())) {
        return result;
      }
      if (result.getResults().size() != 2) {
        result.setMsg("选择供应商信息异常！");
        return result;
      }

      for (SupplierDTO supplierDTO : result.getResults()) {
        supplierPayableService.fillSupplierTradeInfo(supplierDTO);
      }

      SupplierDTO parent = result.getResults().get(0);
      Set areaNos = parent.buildAreaNoSet();
      Map<Long, AreaDTO> areaMap = ServiceManager.getService(IConfigService.class).getAreaByAreaNo(areaNos);
      parent.setAreaByAreaNo(areaMap);
      SupplierDTO child = result.getResults().get(1);
      Set childAreaNos = child.buildAreaNoSet();
      Map<Long, AreaDTO> childAreaMap = ServiceManager.getService(IConfigService.class).getAreaByAreaNo(childAreaNos);
      child.setAreaByAreaNo(childAreaMap);

      result.setResults(null);
      //默认以关联客户为保留客户
      if (child.getSupplierShopId() != null) {
        result.setParent(child);
        result.setChild(parent);
      } else {
        result.setParent(parent);
        result.setChild(child);
      }
      if (parent.getSupplierShopId() != null || child.getSupplierShopId() != null) {
        result.setMergeRelatedFlag(true);
      }
      result.setMergeRelatedFlag(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=mergeSupplierHandler")
  @ResponseBody
  public Object mergeSupplierHandler(ModelMap modelMap, HttpServletRequest request, String parentIdStr, String[] chilIdStrs) {
    IMergeService mergeService = ServiceManager.getService(IMergeService.class);
    String key = "mergeSupplierHandler_" + WebUtil.getShopId(request);
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.SUPPLIER, key)) {
        return null;
      }
      MergeResult<SupplierDTO, MergeSupplierSnap> mergeResult = new MergeResult<SupplierDTO, MergeSupplierSnap>();
      mergeResult.setShopId(WebUtil.getShopId(request));
      mergeResult.setUserId(WebUtil.getUserId(request));
      mergeResult.setLocale(request.getLocale());
      mergeResult.setMergeType(MergeType.MERGE_SUPPLIER);
      mergeService.validateMergeSupplier(mergeResult, parentIdStr, chilIdStrs);
      if (!mergeResult.isSuccess()) {
        return mergeResult;
      }
      List<Long> childIds = mergeResult.getChildIds();
      mergeService.mergeSupplierHandler(mergeResult, NumberUtil.longValue(parentIdStr), childIds.toArray(new Long[childIds.size()]));
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(NumberUtil.longValue(parentIdStr));
      return mergeResult;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.SUPPLIER, key);
    }
  }

  @RequestMapping(params = "method=toMergeRecord")
  public String toMergeRecord(ModelMap modelMap) {
    modelMap.addAttribute("endTime", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    return "customer/merge/mergeSupplierRecord";
  }

  @ResponseBody
  @RequestMapping(params = "method=getSupplierMergeRecords")
  public Object getSupplierMergeRecords(HttpServletRequest request, MergeRecordDTO mergeRecordIndex) {
    IMergeService mergeService = ServiceManager.getService(IMergeService.class);
    try {
      mergeRecordIndex.convertRequestParams();
      mergeRecordIndex.setShopId(WebUtil.getShopId(request));
      mergeRecordIndex.setMergeType(MergeType.MERGE_SUPPLIER);
      return mergeService.getMergeRecords(mergeRecordIndex);
    } catch (Exception e) {
      LOG.error("查询客户合并记录异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getMergeSupplierSnap")
  public Object getMergeSupplierSnap(ModelMap modelMap, HttpServletRequest request, String parentIdStr, String childIdStr) {
    IMergeService mergeService = ServiceManager.getService(IMergeService.class);
    SearchMergeResult<MergeRecord> result = new SearchMergeResult<MergeRecord>();
    if (StringUtil.isEmpty(parentIdStr) || StringUtil.isEmpty(childIdStr)) {
      result.setMsg(false, "供应商信息异常！");
      return result;
    }
    try {
      result.setShopId(WebUtil.getShopId(request));
      mergeService.getMergeSnap(result, NumberUtil.longValue(parentIdStr), NumberUtil.longValue(childIdStr));
      result.setMergeRelatedFlag(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=selfShopComment")
  public String selfShopComment(ModelMap model, HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    model.addAttribute("selfShopComment", "selfShopComment");
    return redirectSupplierComment(model, request, shopId);
  }

  /**
   * 跳转到供应商评价详细页面
   *
   * @param model
   * @param request
   * @return
   */
  @RequestMapping(params = "method=redirectSupplierComment")
  public String redirectSupplierComment(ModelMap model, HttpServletRequest request, Long paramShopId) {
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);

    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (paramShopId == null) {
        throw new Exception("method=redirectSupplierComment paramShopId is null");
      }
      Map<Long, ShopDTO> shopDTOs = configService.getShopByShopId(paramShopId);
      ShopDTO shopDTO = shopDTOs.get(paramShopId);
      shopDTO.setBusinessScope(shopDTO.fromBusinessScopes());

      shopDTO.resetBusinessScope();
      shopDTO.setOperationMode(shopDTO.fromOperationModes());
      Set<Long> areaNos = new HashSet<Long>();
      areaNos.add(shopDTO.getProvince());
      areaNos.add(shopDTO.getCity());
      areaNos.add(shopDTO.getRegion());
      Map<Long, AreaDTO> areaMap = ServiceManager.getService(IConfigService.class).getAreaByAreaNo(areaNos);
      shopDTO.setAreaNameByAreaNo(areaMap);

      if (shopId.longValue() != paramShopId.longValue()) {//别人看
        IUserService userService = ServiceManager.getService(IUserService.class);
        SupplierDTO supplierDTO = userService.getSupplierDTOBySupplierShopIdAndShopId(shopId, paramShopId);
        if (supplierDTO == null) {//显示部分信息
          shopDTO.generatePartShopInfo();
        } else {
          model.addAttribute("supplierId", supplierDTO.getId());
        }
      }
      Set<Long> shopIdSet = new HashSet<Long>();
      shopIdSet.add(shopDTO.getId());
      Map<Long, String> businessScopeMap = preciseRecommendService.getSecondCategoryByShopId(shopIdSet);
      if (MapUtils.isNotEmpty(businessScopeMap) && StringUtils.isNotEmpty(businessScopeMap.get(shopDTO.getId()))) {
        shopDTO.setBusinessScopeStr(businessScopeMap.get(shopDTO.getId()));
      }

      if (StringUtils.isEmpty(shopDTO.getRegistrationDateStr())) {
        shopDTO.setRegistrationDateStr(shopDTO.getCreationDateStr());
      }

      model.addAttribute("shopDTO", shopDTO);

      CommentStatDTO commentStatDTO = supplierCommentService.getCommentStatByShopId(paramShopId);
      if (commentStatDTO == null) {
        commentStatDTO = new CommentStatDTO();
      }
      commentStatDTO.calculate();
      model.addAttribute("supplierCommentStatDTO", commentStatDTO);
      model.addAttribute("paramShopId", paramShopId);
    } catch (Exception e) {
      LOG.error("supplier.redirectSupplierComment,paramShopId:" + paramShopId);
      LOG.error(e.getMessage(), e);
    }

    return "customer/supplierCommentDetail";
  }


  /**
   * 获取已经统计进去的供应商评价记录
   *
   * @param request
   * @param startPageNo
   * @param maxRows
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=getSupplierCommentRecord")
  public Object getSupplierCommentRecord(HttpServletRequest request, Integer startPageNo, Integer maxRows, Long paramShopId) {
    List<Object> objectList = new ArrayList<Object>();//返回值
    CommentSearchResultDTO commentSearchResultDTO = new CommentSearchResultDTO();
    try {

      startPageNo = NumberUtil.intValue(startPageNo);
      startPageNo = startPageNo <= 0 ? 1 : startPageNo;

      maxRows = maxRows == null ? 5 : maxRows.intValue();

      Pager pager = new Pager(0, startPageNo, maxRows);

      ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      if (paramShopId == null) {
        LOG.error("supplier.getSupplierCommentRecord,paramShopId is error:" + paramShopId);
        objectList.add(commentSearchResultDTO);
        objectList.add(pager);
        return objectList;
      }


      int count = supplierCommentService.countSupplierCommentRecord(paramShopId, CommentStatus.STAT);
      if (count <= 0) {
        objectList.add(commentSearchResultDTO);
        objectList.add(pager);
        return objectList;
      }

      pager = new Pager(count, startPageNo, maxRows);
      Sort sort = new Sort("commentTime", " desc ");

      List<SupplierCommentRecordDTO> supplierCommentRecordDTOList = supplierCommentService.getSupplierCommentByPager(paramShopId, CommentStatus.STAT, pager, sort);
      if (CollectionUtils.isEmpty(supplierCommentRecordDTOList)) {
        LOG.error("supplier.getSupplierCommentRecord,query result is error:" + pager.toJson() + ",paramShopId:" + paramShopId);
        objectList.add(commentSearchResultDTO);
        objectList.add(new Pager(0, startPageNo, maxRows));
        return objectList;
      }
      List<Area> areaList = null;
      for (SupplierCommentRecordDTO supplierCommentRecordDTO : supplierCommentRecordDTOList) {
        StringBuilder areaStr = new StringBuilder();
        supplierCommentRecordDTO.setCustomer(null);
        Long customerShopId = supplierCommentRecordDTO.getCustomerShopId();
        ShopDTO shopDTO = configService.getShopById(customerShopId);
        if (shopDTO == null || shopDTO.getCity() == null) {
          continue;
        }
        areaList = configService.getArea(shopDTO.getCity());
        if (CollectionUtil.isEmpty(areaList)) {
          continue;
        }
        for (Area area : areaList) {
          areaStr.append(area.getName());
        }
        supplierCommentRecordDTO.setCustomer(CommentConstant.CUSTOMER_BEGIN + areaStr + CommentConstant.CUSTOMER_END);
      }

      if (CollectionUtil.isEmpty(areaList)) {
      }

      commentSearchResultDTO.setRecordDTOList(supplierCommentRecordDTOList);
      objectList = new ArrayList<Object>();
      objectList.add(commentSearchResultDTO);
      objectList.add(pager);
      return objectList;

    } catch (Exception e) {
      LOG.error("supplier.getSupplierCommentRecord,paramShopId:" + paramShopId + ",startPageNo:" + startPageNo + ",maxRows:" + maxRows);
      LOG.error(e.getMessage(), e);
      return null;
    }

  }

  @ResponseBody
  @RequestMapping(params = "method=getAppUserCommentRecord")
  public Object getAppUserCommentRecord(HttpServletRequest request, Integer startPageNo, Integer maxRows, Long paramShopId) {
    IAppUserCommentService appUserCommentService = ServiceManager.getService(IAppUserCommentService.class);
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    AppUserCommentSearchResultDTO appUserCommentSearchResultDTO = new AppUserCommentSearchResultDTO();
    if (paramShopId == null) {
      LOG.error("supplier.getAppUserCommentRecord,paramShopId is error:" + paramShopId);
      return appUserCommentSearchResultDTO;
    }
    startPageNo = NumberUtil.intValue(startPageNo);
    startPageNo = startPageNo <= 0 ? 1 : startPageNo;

    maxRows = maxRows == null ? 15 : maxRows.intValue();
    Pager pager = null;
    try {
      pager = new Pager(0, startPageNo, maxRows);
      int count = supplierCommentService.countSupplierCommentRecord(paramShopId, CommentStatus.STAT);
      if (count <= 0) {
        appUserCommentSearchResultDTO.setPager(pager);
        return appUserCommentSearchResultDTO;
      }
      pager = new Pager(count, startPageNo, maxRows);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    Sort sort = new Sort("commentTime", " desc ");
    appUserCommentSearchResultDTO = appUserCommentService.getAppUserCommentRecordByShopId(paramShopId, pager, sort);
    appUserCommentSearchResultDTO.setPager(pager);
    return appUserCommentSearchResultDTO;
  }

  @ResponseBody
  @RequestMapping(params = "method=getSupplierById")
  public Object getSupplierById(HttpServletRequest request, Long supplierId) {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    if (supplierId == null) {
      LOG.info("supplierId cannot be null");
      return null;
    }
    Long shopId = WebUtil.getShopId(request);
    SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId, shopId);

    if (supplierDTO != null) {
      StringBuilder areaInfo = new StringBuilder();
      if (supplierDTO.getProvince() != null) {
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getProvince());
        if (areaDTO != null) {
          areaInfo.append(areaDTO.getName());
        }
      }
      if (supplierDTO.getCity() != null) {
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getCity());
        if (areaDTO != null) {
          areaInfo.append(areaDTO.getName());
        }
      }
      if (supplierDTO.getRegion() != null) {
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getRegion());
        if (areaDTO != null) {
          areaInfo.append(areaDTO.getName());
        }
      }
      supplierDTO.setAreaInfo(areaInfo.toString());

      List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
      supplierDTOList.add(supplierDTO);
      //供应商的经营范围
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      preciseRecommendService.setCustomerSupplierBusinessScope(null, supplierDTOList);
      IVehicleBrandModelRelationService vehicleBrandModelRelationService = ServiceManager.getService(IVehicleBrandModelRelationService.class);
      List<VehicleBrandModelRelationDTO> vehicleBrandModelRelationDTOList = vehicleBrandModelRelationService.getVehicleBrandModelRelationDTOByDataId(shopId, supplierDTO.getId(), VehicleBrandModelDataType.SUPPLIER);
      supplierDTO.setVehicleBrandModelRelationInfo(vehicleBrandModelRelationDTOList);
    }

    return supplierDTO;
  }

  /**
   * 一组手机号校验，看有没有修改过的手机号被其他客户占用了，
   *
   * @param model
   * @param request
   * @param response
   * @param mobiles
   * @return
   */
  @RequestMapping(params = "method=validateSupplierMobiles")
  @ResponseBody
  public Object validateSupplierMobiles(ModelMap model, HttpServletRequest request, HttpServletResponse response, String[] mobiles, Long supplierId) {
    Long shopId = WebUtil.getShopId(request);
    try {
      return supplierService.validateSupplierMobiles(shopId, supplierId, mobiles);
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error("/supplier.do?method=validateSupplierMobiles");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return new Result("网络异常", false);
    }
  }

  @RequestMapping(params = "method=cancelSupplierBindingCustomer")
  @ResponseBody
  public String cancelSupplierBindingCustomer(HttpServletRequest request, Long supplierId) {
    String result = "success";
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null !!");
      if (supplierId == null) throw new Exception("supplierId is null !!");
      IUserService userService = ServiceManager.getService(IUserService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      SupplierDTO supplierDTO = CollectionUtil.getFirst(userService.getSupplierById(shopId, supplierId));
      if (supplierDTO == null) throw new Exception("can`t find supplierDTO by supplierId[" + supplierId + "]!!");

      if (supplierDTO.getCustomerId() != null) {
        CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(supplierDTO.getCustomerId(), shopId);
        CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerDTO.getId());
        customerDTO.setSupplierId(null);
        customerDTO.setIdentity(null);
        customerDTO.setCancel(true);
        userService.updateCustomer(customerDTO);
        if (customerRecordDTO == null) {
          customerRecordDTO = new CustomerRecordDTO();
        }
        customerRecordDTO.fromCustomerDTO(customerDTO);
        userService.updateCustomerRecord(customerRecordDTO);

        supplierDTO.setIdentity(null);
        supplierDTO.setCustomerId(null);
        supplierDTO.setCancel(true);
        userService.updateSupplier(supplierDTO);
        //更新remind_event 更新欠款提醒remind_event,将原来既是客户又是供应商的入库退货单的那条记录中的CustomerId清空，supplierId设置值
        txnService.updateRemindEvent(customerDTO.getShopId(), customerDTO.getId(), supplierDTO.getId());

        // add by zhuj 最后cancel
        ServiceManager.getService(IContactService.class).cancelRelatedCusSupContacts(customerDTO.getId(), supplierDTO.getId(), shopId);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      }

    } catch (Exception e) {
      LOG.error("/supplier.do?method=cancelSupplierBindingCustomer");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      result = "fail";
    }

    return result;
  }

  /**
   * 客户管理-供应商资料-供应商信息-直接编辑
   *
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=updateSupplier")
  @ResponseBody
  public Object updateSupplier(HttpServletRequest request, SupplierDTO supplierDTO) throws Exception {
    Result result = new Result();
    IUserService userService = ServiceManager.getService(IUserService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!!");
      if (supplierDTO.getId() == null) throw new Exception("supplierId is null!!");
      supplierDTO.setShopId(shopId);
      boolean supplierChangeArea = false;
      SupplierDTO dbSupplierDTO = supplierService.getSupplierById(supplierDTO.getId(), shopId);
      if (dbSupplierDTO == null)
        throw new Exception("can`t find supplerDTO by supplierId[" + supplierDTO.getId() + "]!!");
      if (!StringUtil.compareSame(supplierDTO.getProvince(), dbSupplierDTO.getProvince())
        || !StringUtil.compareSame(supplierDTO.getCity(), dbSupplierDTO.getCity())
        || !StringUtil.compareSame(supplierDTO.getRegion(), dbSupplierDTO.getRegion())) {
        supplierChangeArea = true;
      }
      supplierDTO.filledUnEditinfo(dbSupplierDTO);

      // 填充联系人列表 add by zhuj
      ContactDTO[] contactDTOs = SupplierDTOGenerator.fillContactArray(request);
      if (supplierDTO.getCustomerId() != null) {
        supplierDTO.setIdentity("isCustomer");
        if (!ArrayUtils.isEmpty(contactDTOs)) {
          for (ContactDTO contactDTO : contactDTOs) {
            if (contactDTO != null && contactDTO.isValidContact()) {
              contactDTO.setCustomerId(supplierDTO.getCustomerId());
            }
          }
        }
      }
      supplierDTO.setContacts(contactDTOs);
      supplierDTO.setFromManagePage(true);
      supplierDTO.compositeLandline();
      userService.updateSupplier(supplierDTO);
      supplierService.saveSupplierVehicleBrandModelRelation(shopId, supplierDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
      CustomerDTO customerDTO = null;
      //既是客户又是供应商
      if (supplierDTO.getCustomerId() != null) {
        String isMergeContact = request.getParameter("mergeContact");
        customerDTO = processCustomerInfo(shopId, supplierDTO, isMergeContact);
      }
      //设置供应商的经营范围
      userService.saveOrUpdateCustomerSupplierBusinessScope(customerDTO, supplierDTO, supplierDTO.getThirdCategoryIdStr());
      preciseRecommendService.getCustomerSupplierBusinessScopeForAdd(customerDTO, supplierDTO);
      userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);
      //当改变供应商所属区域时，同时更新供应商的单据
      if (supplierChangeArea) {
        userService.generateSupplierOrderIndexScheduleDTO(shopId, supplierDTO);
      }

      //reindex supplier in solr  包含了既是客户又是供应商的 索引重做
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      supplierService.addAreaInfoToSupplierDTO(supplierDTO);

      //结算方式
      Map<String, String> settlementTypeMap = TxnConstant.getSettlementTypeMap(request.getLocale());
      //发票类型
      Map<String, String> invoiceCatagoryMap = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
      supplierDTO.setSettlementType(settlementTypeMap.get(String.valueOf(supplierDTO.getSettlementTypeId())));
      supplierDTO.setInvoiceCategory(invoiceCatagoryMap.get(String.valueOf(supplierDTO.getInvoiceCategoryId())));
      supplierDTO.fillingContacts();
      result.setData(supplierDTO);
    } catch (Exception e) {
      LOG.debug("/supplier.do?method=updateSupplier");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
    }
    return result;
  }

  private CustomerDTO processCustomerInfo(Long shopId, SupplierDTO supplierDTO, String isMergeContact) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IContactService contactService = ServiceManager.getService(IContactService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    boolean customerChangeArea = false;
    CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(supplierDTO.getCustomerId(), shopId);
    if (!StringUtil.compareSame(supplierDTO.getProvince(), customerDTO.getProvince())
      || !StringUtil.compareSame(supplierDTO.getCity(), customerDTO.getCity())
      || !StringUtil.compareSame(supplierDTO.getRegion(), customerDTO.getRegion())) {
      customerChangeArea = true;
    }

    customerDTO.fromSupplierDTO(supplierDTO);
    customerDTO.setIdentity("isSupplier");
    customerDTO.setSupplierId(supplierDTO.getId());
    userService.updateCustomer(customerDTO);
    userService.saveCustomerVehicleBrandModelRelation(shopId, customerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
    CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerDTO.getId());
    if (customerRecordDTO == null) {
      customerRecordDTO = new CustomerRecordDTO();
    }
    customerRecordDTO.fromCustomerDTO(customerDTO);
    userService.updateCustomerRecord(customerRecordDTO);

    //建立联系时更新remind_event
    txnService.updateRemindEvent2(shopId, customerDTO.getId(), supplierDTO.getId());

    if (StringUtils.isNotBlank(isMergeContact) && StringUtils.equals(isMergeContact, "noMerge")) {
      // 不需要合并的情况 目前对客户和供应商的联系人都不进行处理 noMerge这个标识 和 modifyClient.js里面的赋值耦合
    } else {
      // add by zhuj 供应客户联系人信息 页面详情跳转过来...
      contactService.updateContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, supplierDTO.getContacts());
      contactService.addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(), supplierDTO.getId(), shopId, supplierDTO.getContacts());
    }
    if (customerChangeArea) { //当改变客户所属区域时，插入一条schedule任务
      userService.generateCustomerOrderIndexScheduleDTO(shopId, customerDTO);
    }

    return customerDTO;
  }

  @RequestMapping(params = "method=checkSupplierStatus")
  @ResponseBody
  public Object checkCustomerStatus(HttpServletRequest request, Long supplierId) throws Exception {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    try {
      SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId, WebUtil.getShopId(request));
      if (supplierDTO == null || CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) {
        return new Result(false);
      } else {
        return new Result();
      }
    } catch (Exception e) {
      LOG.error("method=checkSupplierStatus");
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=showAppShopCommentList")
  public String showAppShopCommentList(ModelMap model, HttpServletRequest request) {
    model.put("paramShopId", WebUtil.getShopId(request));
    return "/autoaccessoryonline/shopData/appShopComment";
  }

  @ResponseBody
  @RequestMapping(params = "method=getAppCommentRecordByKeyword")
  public Object getAppCommentRecordByKeyword(HttpServletRequest request, Integer startPageNo, Integer maxRows, Long paramShopId, CommentRecordDTO commentRecordDTO) {
    try {
      IAppUserCommentService appUserCommentService = ServiceManager.getService(IAppUserCommentService.class);
      ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
      ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);
      AppUserCommentSearchResultDTO appUserCommentSearchResultDTO = new AppUserCommentSearchResultDTO();
      int maxRowAmount = 15;
      if (paramShopId == null) {
        LOG.error("supplier.getAppCommentRecordByKeyword,paramShopId is error:" + paramShopId);
        return appUserCommentSearchResultDTO;
      }
      startPageNo = NumberUtil.intValue(startPageNo);
      startPageNo = startPageNo <= 0 ? 1 : startPageNo;

      maxRows = maxRows == null ? maxRowAmount : maxRows.intValue();
      Pager pager = null;
      try {
        pager = new Pager(0, startPageNo, maxRows);
        //判断用户是否输入客户信息
        if (StringUtils.isNotBlank(commentRecordDTO.getCustomerName())) {
          CustomerSupplierSearchConditionDTO searchConditionDTO = new CustomerSupplierSearchConditionDTO();
          searchConditionDTO.setSearchWord(commentRecordDTO.getCustomerName());
          searchConditionDTO.setShopId(paramShopId);
          List<Long> customerIdList = new ArrayList<Long>();
          CustomerSupplierSearchResultListDTO customerSupplierSearchResultListDTO = searchCustomerSupplierService.queryCustomerSupplierWithUnknownField(searchConditionDTO);
          for (int i = 0; i < customerSupplierSearchResultListDTO.getCustomerSuppliers().size(); i++) {
            CustomerSupplierSearchResultDTO customerSupplierSearchResultDTO = customerSupplierSearchResultListDTO.getCustomerSuppliers().get(i);
            customerIdList.add(customerSupplierSearchResultDTO.getId());
          }
          commentRecordDTO.setCustomerIds(customerIdList);
        }
        //判断用户是否输入了单据号
        if (StringUtils.isNotBlank(commentRecordDTO.getReceiptNo())) {
          commentRecordDTO.setReceiptNo(commentRecordDTO.getReceiptNo().toUpperCase());
        }
        //判断用户是否在生成评价list之后，再点击表格上方的评价类型超链接
        if (StringUtils.isNotBlank(commentRecordDTO.getAddGoodCommentScoreStr())) {
          Double[] addGoodComments = {4.0, 5.0};
          List<Double> ld = Arrays.asList(addGoodComments);
          commentRecordDTO.setAddGoodCommentScores(ld);
        }
        if (StringUtils.isNotBlank(commentRecordDTO.getAddMediumCommentScoreStr())) {
          Double[] addMediumComments = {3.0};
          List<Double> ld = Arrays.asList(addMediumComments);
          commentRecordDTO.setAddMediumCommentScores(ld);
        }
        if (StringUtils.isNotBlank(commentRecordDTO.getAddBadCommentScoreStr())) {
          Double[] addBadComments = {1.0, 2.0};
          List<Double> ld = Arrays.asList(addBadComments);
          commentRecordDTO.setAddBadCommentScores(ld);
        }
        //判断用户是否勾选评价类型
        if (StringUtils.isNotBlank(commentRecordDTO.getCommentScoreStr())) {
          String[] commentScores = commentRecordDTO.getCommentScoreStr().split(",");
          List<String> ls = Arrays.asList(commentScores);
          commentRecordDTO.StringsToDoubles(ls);
        }
        //判断用户是否勾选单据类型
        if (StringUtils.isNotBlank(commentRecordDTO.getOrderTypeStr())) {
          String[] orderTypes = commentRecordDTO.getOrderTypeStr().split(",");
          List<String> ls = Arrays.asList(orderTypes);
          commentRecordDTO.StringsToOrderTypes(ls);
        }
        int count = supplierCommentService.countSupplierCommentRecordByKeyword(paramShopId, commentRecordDTO);
        pager = new Pager(count, startPageNo, maxRows);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      appUserCommentSearchResultDTO = appUserCommentService.getAppCommentRecordByShopIdAndKeyword(paramShopId, pager, commentRecordDTO);
      appUserCommentSearchResultDTO.setPager(pager);
      return appUserCommentSearchResultDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

}
