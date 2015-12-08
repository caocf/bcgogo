package com.bcgogo.customer;

import com.bcgogo.common.*;
import com.bcgogo.common.CookieUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.AbstractTxnController;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserGuideService;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-18
 * Time: 下午5:00
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/apply.do")
public class ApplyController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(ApplyController.class);
  private static final String CUSTOMER_LIST = "/customer/applyCustomerList";
  private static final String SUPPLIER_LIST = "/customer/applySupplierList";
  private static final String SUPPLIER_INDEX_LIST = "/customer/applySupplierIndex";
  private static final String CUSTOMER_INDEX_LIST = "/customer/applyCustomerIndex";
  private static final int DEFAULT_MAX_ROWS = 10;

  @RequestMapping(params = "method=getApplyCustomersIndexPage")
  public String getApplyCustomersIndexPage(HttpServletRequest request, ModelMap modelMap) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(shopId==null) throw new Exception("shop id is null!");

      Node node = getShopCustomProductCategoryNode(shopId);
      modelMap.addAttribute("productCategoryNode",node);

    } catch (Exception e) {
      LOG.error("apply.do?method=getApplyCustomersIndexPage");
      LOG.error(e.getMessage(), e);
    }
    return CUSTOMER_INDEX_LIST;
  }

  @RequestMapping(params = "method=getApplySuppliersIndexPage")
  public String getApplySuppliersIndexPage(HttpServletRequest request, ModelMap modelMap) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(shopId==null) throw new Exception("shop id is null!");
      List<ShopDTO> shopDTOList = configService.getBcgogoRecommendSupplierShop(shopId);
      modelMap.addAttribute("starShopDTOList",shopDTOList);
      Node node = getShopCustomProductCategoryNode(shopId);

      modelMap.addAttribute("productCategoryNode",node);

    } catch (Exception e) {
      LOG.error("apply.do?method=getApplySuppliersIndexPage");
      LOG.error(e.getMessage(), e);
    }
    return SUPPLIER_INDEX_LIST;
  }

  private Node getShopCustomProductCategoryNode(Long shopId) {
    List<Long> shopProductCategoryIdList = configService.getShopBusinessScopeProductCategoryIdListByShopId(shopId);
    Set<Long> shopProductCategoryIdSet = new HashSet<Long>();

    if (CollectionUtils.isNotEmpty(shopProductCategoryIdList)) {
      ProductCategoryDTO thirdProductCategoryDTO = null;
      ProductCategoryDTO secondProductCategoryDTO = null;
      ProductCategoryDTO firstProductCategoryDTO = null;

      for (Long shopProductCategoryId : shopProductCategoryIdList) {
        shopProductCategoryIdSet.add(shopProductCategoryId);
        thirdProductCategoryDTO = ProductCategoryCache.getProductCategoryDTOById(shopProductCategoryId);
        if (thirdProductCategoryDTO != null) {
          if(!shopProductCategoryIdSet.contains(thirdProductCategoryDTO.getParentId())){
            shopProductCategoryIdSet.add(thirdProductCategoryDTO.getParentId());
          }
          secondProductCategoryDTO = ProductCategoryCache.getProductCategoryDTOById(thirdProductCategoryDTO.getParentId());
        }

        if (secondProductCategoryDTO!=null) {
          if(!shopProductCategoryIdSet.contains(secondProductCategoryDTO.getParentId())){
            shopProductCategoryIdSet.add(secondProductCategoryDTO.getParentId());
          }
          firstProductCategoryDTO = ProductCategoryCache.getProductCategoryDTOById(secondProductCategoryDTO.getParentId());
        }
        if (firstProductCategoryDTO!=null && !shopProductCategoryIdSet.contains(firstProductCategoryDTO.getParentId())) {
          shopProductCategoryIdSet.add(firstProductCategoryDTO.getParentId());
        }
      }
    }
    Node node = ProductCategoryCache.getNode();
    node.resetProductCategoryNodeSortByShopProductCategoryIdSet(shopProductCategoryIdSet);
    node.reBuildTreeForSort();
    return node;
  }


  @RequestMapping(params = "method=getApplyCustomersPage")
  public String getApplyCustomersPage(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
    try {
      //用户引导
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, "CONTRACT_CUSTOMER_GUIDE_RECOMMEND_CUSTOMER");
      String thirdCategoryIdStr = request.getParameter("thirdCategoryIdStr");
      String secondCategoryIdStr = request.getParameter("secondCategoryIdStr");
      if(StringUtils.isNotBlank(secondCategoryIdStr)){
        Node root = ProductCategoryCache.getNode();
        Node node = root.findNodeInTree(NumberUtil.longValue(secondCategoryIdStr));
        if(node!=null && CollectionUtils.isNotEmpty(node.getChildren())){
          thirdCategoryIdStr ="";
          for(Node n:node.getChildren()){
            thirdCategoryIdStr+=n.getId()+",";
          }
        }
      }
      modelMap.addAttribute("thirdCategoryIdStr",thirdCategoryIdStr);
      modelMap.addAttribute("provinceNo",request.getParameter("provinceNo"));
      modelMap.addAttribute("customerName", request.getParameter("customerName"));
      modelMap.addAttribute("pushMessageId", request.getParameter("pushMessageId"));
    } catch (Exception e) {
      LOG.warn("apply.do?method=getApplyCustomersPage");
      LOG.error(e.getMessage(), e);
    }
    return CUSTOMER_LIST;
  }

  @RequestMapping(params = "method=getApplySuppliersPage")
  public String getApplySuppliersPage(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
    try {
      //用户引导
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, "CONTRACT_SUPPLIER_GUIDE_RECOMMEND_SUPPLIER");
      String thirdCategoryIdStr = request.getParameter("thirdCategoryIdStr");
      String secondCategoryIdStr = request.getParameter("secondCategoryIdStr");
      String searchWord = request.getParameter("searchWord");
      if(StringUtils.isNotBlank(secondCategoryIdStr)){
        Node root = ProductCategoryCache.getNode();
        Node node = root.findNodeInTree(NumberUtil.longValue(secondCategoryIdStr));
        if(node!=null && CollectionUtils.isNotEmpty(node.getChildren())){
          thirdCategoryIdStr ="";
          for(Node n:node.getChildren()){
            thirdCategoryIdStr+=n.getId()+",";
          }
        }
      }
      modelMap.addAttribute("thirdCategoryIdStr",thirdCategoryIdStr);
      modelMap.addAttribute("provinceNo",request.getParameter("provinceNo"));
      modelMap.addAttribute("supplierName", request.getParameter("supplierName"));
      modelMap.addAttribute("pushMessageId", request.getParameter("pushMessageId"));
      modelMap.addAttribute("standardVehicleBrand", request.getParameter("standardVehicleBrand"));
      modelMap.addAttribute("standardVehicleModel", request.getParameter("standardVehicleModel"));
      String area_code=request.getParameter("area_code");
      modelMap.addAttribute("area_code",area_code );
      List<AreaDTO> proAreaDTOList=configService.getChildAreaDTOList(1l);
      modelMap.addAttribute("proAreaDTOList",proAreaDTOList );
      if(StringUtil.isNotEmpty(area_code)){
        if(area_code.length()>4){
          List<AreaDTO> cityAreaDTOList=configService.getChildAreaDTOList(NumberUtil.longValue(area_code.substring(0,4)));
          modelMap.addAttribute("cityAreaDTOList",cityAreaDTOList);
        }
      }
    } catch (Exception e) {
      LOG.warn("apply.do?method=getApplySuppliersPage");
      LOG.error(e.getMessage(), e);
    }
    return SUPPLIER_LIST;
  }


  @RequestMapping(params = "method=searchApplyCustomers")
  @ResponseBody
  public Object searchApplyCustomers(HttpServletRequest request, ModelMap modelMap, ApplyShopSearchCondition searchCondition, Long pushMessageId) {
    Long shopId = null;
    Map<String, Object> resultMap = new HashMap<String, Object>();
    Result result;
    try {
      if (pushMessageId != null) {
        searchCondition.setShopIds(applyPushMessageService.getApplyPushMessageShopIds(pushMessageId));
      }
      shopId = WebUtil.getShopId(request);
      ShopDTO shopDTO = configService.getShopById(shopId);
      searchCondition.setShopId(shopId);
      searchCondition.setShopAreaInfo(shopDTO);
      searchCondition.setShopAreaId(shopDTO.getAreaId());
      boolean isTestShop = applyService.isTestShop(shopDTO);
      //排除掉自己店铺，和已经关联或者收藏的店铺
      Set<Long> excludeShopIds = customerService.getCustomerShopIds(shopId);
      excludeShopIds.add(shopId);
      searchCondition.setExcludeShopIds(excludeShopIds);

      int currentPage = NumberUtil.intValue(request.getParameter("currentPage"),1);
      //系统推荐给客户的批发商店铺版本ID
      String shopVersionIdStr = configService.getConfig("ShopVersionRecommendedToWholesalers", ShopConstant.BC_SHOP_ID);
      Integer total = applyService.countApplyCustomerShop(searchCondition, shopVersionIdStr, isTestShop);
      Pager pager = new Pager(total, currentPage, DEFAULT_MAX_ROWS);
      List<ApplyShopSearchCondition> shopDTOs = applyService.searchApplyCustomerShop(searchCondition, shopVersionIdStr, pager, isTestShop);
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      shopDTOs = preciseRecommendService.getShopBusinessScopeForApply(shopDTOs);

      resultMap.put("shopDTOs", shopDTOs);
      resultMap.put("pager", pager);
      return resultMap;
    } catch (Exception e) {
      LOG.error("method=searchApplyCustomers,shopId :{}" + e.getMessage(), shopId, e);
      result = new Result("网络异常", false);
      resultMap.put("result", result);
      return resultMap;
    }
  }

  @RequestMapping(params = "method=searchApplySuppliers")
  @ResponseBody
  public Object searchApplySuppliers(HttpServletRequest request, ModelMap modelMap, ApplyShopSearchCondition searchCondition, Long pushMessageId) {
    Long shopId = null;
    Map<String, Object> resultMap = new HashMap<String, Object>();
    Result result = new Result();
    try {
      if (pushMessageId != null) {
        searchCondition.setShopIds(applyPushMessageService.getApplyPushMessageShopIds(pushMessageId));
      }
//      if(StringUtil.isNotEmpty(searchCondition.getKeyword())){
//        searchCondition.setThirdCategoryIdStr(ProductCategoryCache.searchCategoryNodeIds(searchCondition.getKeyword()));
//      }
      shopId = WebUtil.getShopId(request);
      ShopDTO shopDTO = configService.getShopById(shopId);
      searchCondition.setShopId(shopId);
      searchCondition.setShopAreaInfo(shopDTO);
      searchCondition.setShopAreaId(shopDTO.getAreaId());
      int currentPage = NumberUtil.intValue(request.getParameter("currentPage"),1);
//      boolean isTestShop = applyService.isIncludeTestShop(shopDTO);
      boolean isTestShop = applyService.isTestShop(shopDTO);
      //系统推荐给批发商的客户店铺版本ID
      String shopVersionIdStr = configService.getConfig("ShopVersionRecommendedToCustomers", ShopConstant.BC_SHOP_ID);
      Integer total = applyService.countApplySupplierShop(searchCondition, shopVersionIdStr, isTestShop);
      Pager pager = new Pager(total, currentPage, DEFAULT_MAX_ROWS);
      List<ApplyShopSearchCondition> shopDTOs = applyService.searchApplySupplierShop(searchCondition, shopVersionIdStr, pager, isTestShop);

      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      shopDTOs = preciseRecommendService.getShopBusinessScopeForApply(shopDTOs);

      ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
      supplierCommentService.setSupplierCommentStat(null, shopDTOs);
      if(CollectionUtil.isNotEmpty(shopDTOs)){
        //主营车型
        for(ApplyShopSearchCondition shop:shopDTOs){
          List<ShopVehicleBrandModelDTO> bmDTOs= productService.getShopVehicleBrandModelByShopId(shop.getShopId());
          shop.generateShopVehicleBrandModelStr(bmDTOs);
        }
      }
      resultMap.put("shopDTOs", shopDTOs);
      resultMap.put("pager", pager);
      return resultMap;
    } catch (Exception e) {
      LOG.error("method=searchApplySuppliers,shopId :{}" + e.getMessage(), shopId, e);
      result = new Result("网络异常", false);
      resultMap.put("result", result);
      return resultMap;
    }
  }

  //客户申请供应商关联
  @RequestMapping(params = "method=applySupplierRelation")
  @ResponseBody
  public Object applySupplierRelation(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Long... supplerShopId) {
    Long shopId = null;
    Result result = null;
    try {
      shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      result = applyService.validateApplySupplierRelation(shopId, supplerShopId);
      if (result != null && result.isSuccess()) {
        //by qxy 允许手机号重复了不需要校验了
//        Long[] supplerShopIds = supplierService.validateApplySupplierContactMobile(shopId, supplerShopId);
        Long[] supplerShopIds = supplerShopId;
        if (ArrayUtil.isNotEmpty(supplerShopIds)) {
          supplerShopIds = applyService.initApplySupplierRelationShopIds(shopId, supplerShopIds);
          // by qxy 现在允许手机号重复
//          for (Long id : supplerShopIds) {
//            Long[] ids = customerService.validateApplyCustomerContactMobile(id, shopId);
//            if (ArrayUtil.isEmpty(ids)) {
//              result.setMsg(false, "不允许申请关联，请确认您的联系人手机号是否在其他店铺使用？");
//              return result;
//            }
//          }
          List<ShopRelationInviteDTO> shopRelationInviteDTOList = applyService.batchSaveApplySupplierRelation(shopId, userId, supplerShopIds);
          for (ShopRelationInviteDTO dto : shopRelationInviteDTOList) {
            applyPushMessageService.createApplyRelatedPushMessage(shopId, dto.getInvitedShopId(), dto.getId(),dto.getInviteTime(), PushMessageSourceType.APPLY_CUSTOMER);
          }
          CookieUtil.rebuildCookiesForUserGuide(request, response, true, "CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY", "CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY", "PRODUCT_PRICE_GUIDE_MORE_PRODUCT");
        }
//        else {
//          ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(supplerShopId[0]);
//          result.setMsg(false, "已关联供应商：【" + shopDTO.getName() + "】存在相同联系人！");
//        }
      }
      return result;
    } catch (Exception e) {
      LOG.error("method=applySupplierRelation,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }

  //申请客户关联
  @RequestMapping(params = "method=applyCustomerRelation")
  @ResponseBody
  public Object applyCustomerRelation(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Long... customerShopId) {
    Long shopId = null;
    Result result = null;
    try {
      shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      result = applyService.validateApplyCustomerRelation(shopId, customerShopId);
      if (result != null && result.isSuccess()) {
         // by qxy 现在允许手机号重复
//        Long[] customerShopIds = customerService.validateApplyCustomerContactMobile(shopId, customerShopId);
        Long[] customerShopIds = customerShopId;
        if (ArrayUtil.isNotEmpty(customerShopIds)) {
          customerShopIds = applyService.initApplyCustomerRelationShopIds(shopId, customerShopIds);
          List<ShopRelationInviteDTO> shopRelationInviteDTOList = applyService.batchSaveApplyCustomerRelation(shopId, userId, customerShopIds);
          for (ShopRelationInviteDTO dto : shopRelationInviteDTOList) {
            applyPushMessageService.createApplyRelatedPushMessage(shopId, dto.getInvitedShopId(), dto.getId(),dto.getInviteTime(), PushMessageSourceType.APPLY_SUPPLIER);
          }
          CookieUtil.rebuildCookiesForUserGuide(request, response, true, "CONTRACT_CUSTOMER_GUIDE_SINGLE_APPLY", "CONTRACT_CUSTOMER_GUIDE_BATCH_APPLY");
        }
//        else {
//          ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(customerShopId[0]);
//          result.setMsg(false, "已关联客户：【" + shopDTO.getName() + "】存在相同联系人！");
//        }
      }
      return result;
    } catch (Exception e) {
      LOG.error("method=applyCustomerRelation,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }


  //供应商同意客户的申请
  @RequestMapping(params = "method=acceptCustomerApply")
  @ResponseBody
  public Object acceptCustomerApply(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Long inviteId) {
    Long shopId = null;
    Result result = null;
    try {

      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      shopId = WebUtil.getShopId(request);
      ShopDTO shopDTO = configService.getShopById(shopId);
      ShopRelationInviteDTO shopRelationInviteDTO = applyService.getShopRelationInviteDTOByInvitedShopIdAndId(shopId, inviteId);
      result = applyService.validateAcceptCustomerApply(shopDTO, shopRelationInviteDTO);
      if (result != null && !result.isSuccess()) {
        return result;
      }
      if (!BcgogoConcurrentController.lock(ConcurrentScene.INVITE, inviteId)) {
        return new Result("当前请求正在被处理，请稍候再试！");
      }
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, "CUSTOMER_APPLY_GUIDE_SHOW_APPLY");
      shopRelationInviteDTO.setOperationMan(WebUtil.getUserName(request));
      shopRelationInviteDTO.setOperationManId(WebUtil.getUserId(request));
      boolean isSuccess = applyService.acceptApply(shopRelationInviteDTO);
      if (isSuccess) {
        //更新 推送消息
        pushMessageService.readPushMessageReceiverBySourceId(shopRelationInviteDTO.getOriginShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInvitedShopId(),PushMessageSourceType.APPLY_CUSTOMER,PushMessageSourceType.APPLY_SUPPLIER);

        ShopDTO customerShopDTO = configService.getShopById(shopRelationInviteDTO.getOriginShopId());
        CustomerDTO customerDTO = userService.getCustomerByCustomerShopIdAndShopId(shopId, customerShopDTO.getId());
        if(customerDTO == null){
          //给供应商店铺下创建一个“被申请关联”客户，
          customerDTO = userService.createRelationCustomer(shopDTO, customerShopDTO, RelationTypes.RECOMMEND_RELATED);
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        }
        SupplierDTO supplierDTO = userService.getSupplierDTOBySupplierShopIdAndShopId(customerShopDTO.getId(), shopId);
        if(supplierDTO == null){
          //给客户店铺下创建一个“申请关联”供应商
          supplierDTO = rfiTxnService.createRelationSupplier(customerShopDTO, shopDTO, RelationTypes.APPLY_RELATED);
        }
        //保存客户和供应商的经营范围
        userService.createCustomerSupplierBusinessScope(customerDTO,supplierDTO);
        preciseRecommendService.getCustomerSupplierBusinessScope(customerDTO,supplierDTO);
        userService.updateCustomerSupplierBusinessScope(customerDTO,supplierDTO);

        //保存供应商主营车型
        List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList =  productService.getShopVehicleBrandModelByShopId(shopDTO.getId());
        if(VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel()) && CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)){
          StringBuilder vehicleModelIdStr = new StringBuilder();
          for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList){
            vehicleModelIdStr.append(shopVehicleBrandModelDTO.getModelId()).append(",");
          }
          supplierDTO.setVehicleModelIdStr(vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1));
          supplierService.saveSupplierVehicleBrandModelRelation(customerShopDTO.getId(),supplierDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
        }
        //保存客户主营车型
        shopVehicleBrandModelDTOList =  productService.getShopVehicleBrandModelByShopId(customerShopDTO.getId());
        if(VehicleSelectBrandModel.PART_MODEL.equals(customerShopDTO.getShopSelectBrandModel()) && CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)){
          StringBuilder vehicleModelIdStr = new StringBuilder();
          for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList){
            vehicleModelIdStr.append(shopVehicleBrandModelDTO.getModelId()).append(",");
          }
          customerDTO.setVehicleModelIdStr(vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1));
          userService.saveCustomerVehicleBrandModelRelation(shopDTO.getId(),customerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
        }
        //保存客户服务范围
        IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
        List<ShopServiceCategoryDTO> shopServiceCategoryDTOList = serviceCategoryService.getShopServiceCategoryDTOByShopId(customerShopDTO.getId());
        if(CollectionUtil.isNotEmpty(shopServiceCategoryDTOList)){
          StringBuilder serviceCategoryRelationIdStr = new StringBuilder();
          for(ShopServiceCategoryDTO shopServiceCategoryDTO : shopServiceCategoryDTOList){
            serviceCategoryRelationIdStr.append(shopServiceCategoryDTO.getServiceCategoryId()).append(",");
          }
          customerDTO.setServiceCategoryRelationIdStr(serviceCategoryRelationIdStr.substring(0,serviceCategoryRelationIdStr.length()-1));
          userService.saveCustomerServiceCategoryRelation(shopDTO.getId(),customerDTO);
        }
        //删除客户的供应商下的 与 本店 联系人通手机号的删除
        List<Long> shopRelationInviteIds = applyService.deleteOtherCustomerShopRelationInviteByInvitedShopContactMobile(customerShopDTO.getId(), shopId, customerDTO.getContactMobiles());
        //同理 删除供应商的客户下的 与 客户店 联系人通手机号的删除
        shopRelationInviteIds.addAll(applyService.deleteOtherSupplierShopRelationInviteByInvitedShopContactMobile(customerShopDTO.getId(), shopId, supplierDTO.getContactMobiles()));
        for (Long shopRelationInviteId : shopRelationInviteIds) {
          pushMessageService.disabledPushMessageReceiverBySourceId(null, shopRelationInviteId,null,PushMessageSourceType.APPLY_CUSTOMER,PushMessageSourceType.APPLY_SUPPLIER);
        }
        noticeService.createSupplierAcceptNoticeToSupplier(customerShopDTO, shopRelationInviteDTO, customerDTO);
        noticeService.createSupplierAcceptNoticeToCustomer(shopDTO, shopRelationInviteDTO, supplierDTO);
        shopRelationInviteDTO.setCustomerId(customerDTO.getId());
        shopRelationInviteDTO.setSupplierId(supplierDTO.getId());
        applyService.updateShopRelationInvite(shopRelationInviteDTO);

        customerDTO.setPartShopDTOInfo(customerShopDTO, true);
        result.setData(customerDTO);
      }
      return result;
    } catch (Exception e) {
      LOG.error("method=acceptCustomerApply,inviteId :{}" + e.getMessage(), inviteId, e);
      return new Result("网络异常", false);
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.INVITE, inviteId);
    }
  }

  //客户同意供应商的申请
  @RequestMapping(params = "method=acceptSupplierApply")
  @ResponseBody
  public Object acceptSupplierApply(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, Long inviteId) {
    Long shopId = null;
    Result result = null;
    try {
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);

      shopId = WebUtil.getShopId(request);
      ShopDTO shopDTO = configService.getShopById(shopId);
      ShopRelationInviteDTO shopRelationInviteDTO = applyService.getShopRelationInviteDTOByInvitedShopIdAndId(shopId, inviteId);
      result = applyService.validateAcceptSupplierApply(shopDTO, shopRelationInviteDTO);
      if (result != null && !result.isSuccess()) {
        return result;
      }
      if (!BcgogoConcurrentController.lock(ConcurrentScene.INVITE, inviteId)) {
        return new Result("当前请求正在被处理，请稍候再试！");
      }
      shopRelationInviteDTO.setOperationMan(WebUtil.getUserName(request));
      shopRelationInviteDTO.setOperationManId(WebUtil.getUserId(request));
      if (applyService.acceptApply(shopRelationInviteDTO)) {
        //更新 推送消息
        pushMessageService.readPushMessageReceiverBySourceId(shopRelationInviteDTO.getOriginShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInvitedShopId(),PushMessageSourceType.APPLY_CUSTOMER,PushMessageSourceType.APPLY_SUPPLIER);
      }

      ShopDTO supplierShopDTO = configService.getShopById(shopRelationInviteDTO.getOriginShopId());

      CustomerDTO customerDTO = userService.getCustomerByCustomerShopIdAndShopId(supplierShopDTO.getId(), shopId);
      if(customerDTO == null){
        //给供应商店铺下创建一个“申请关联”客户，
        customerDTO = userService.createRelationCustomer(supplierShopDTO, shopDTO, RelationTypes.APPLY_RELATED);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
      }

      SupplierDTO supplierDTO = userService.getSupplierDTOBySupplierShopIdAndShopId(shopId, supplierShopDTO.getId());
      if(supplierDTO == null){
        //给客户店铺下创建一个“推荐关联”供应商
        supplierDTO = rfiTxnService.createRelationSupplier(shopDTO, supplierShopDTO, RelationTypes.RECOMMEND_RELATED);
      }
      //保存客户和供应商的经营范围
      userService.createCustomerSupplierBusinessScope(customerDTO,supplierDTO);
      preciseRecommendService.getCustomerSupplierBusinessScope(customerDTO, supplierDTO);
      userService.updateCustomerSupplierBusinessScope(customerDTO, supplierDTO);

      //保存供应商主营车型
      List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList =  productService.getShopVehicleBrandModelByShopId(supplierShopDTO.getId());
      if(VehicleSelectBrandModel.PART_MODEL.equals(supplierShopDTO.getShopSelectBrandModel()) && CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)){
        StringBuilder vehicleModelIdStr = new StringBuilder();
        for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList){
          vehicleModelIdStr.append(shopVehicleBrandModelDTO.getModelId()).append(",");
        }
        supplierDTO.setVehicleModelIdStr(vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1));
        supplierService.saveSupplierVehicleBrandModelRelation(shopDTO.getId(),supplierDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
      }
      //保存客户主营车型
      shopVehicleBrandModelDTOList =  productService.getShopVehicleBrandModelByShopId(shopDTO.getId());
      if(VehicleSelectBrandModel.PART_MODEL.equals(shopDTO.getShopSelectBrandModel()) && CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)){
        StringBuilder vehicleModelIdStr = new StringBuilder();
        for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList){
          vehicleModelIdStr.append(shopVehicleBrandModelDTO.getModelId()).append(",");
        }
        customerDTO.setVehicleModelIdStr(vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1));
        userService.saveCustomerVehicleBrandModelRelation(supplierShopDTO.getId(),customerDTO, StandardBrandModelCache.getShopVehicleBrandModelDTOMap());
      }
      //保存客户服务范围
      IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
      List<ShopServiceCategoryDTO> shopServiceCategoryDTOList = serviceCategoryService.getShopServiceCategoryDTOByShopId(shopDTO.getId());
      if(CollectionUtil.isNotEmpty(shopServiceCategoryDTOList)){
        StringBuilder serviceCategoryRelationIdStr = new StringBuilder();
        for(ShopServiceCategoryDTO shopServiceCategoryDTO : shopServiceCategoryDTOList){
          serviceCategoryRelationIdStr.append(shopServiceCategoryDTO.getServiceCategoryId()).append(",");
        }
        customerDTO.setServiceCategoryRelationIdStr(serviceCategoryRelationIdStr.substring(0,serviceCategoryRelationIdStr.length()-1));
        userService.saveCustomerServiceCategoryRelation(supplierShopDTO.getId(),customerDTO);
      }

      //同一个邀请人 邀请了多个供应商 如果其中供应商与此供应商的联系人中的手机号 有相同的 逻辑删除其他关联请求和关联消息
      List<Long> shopRelationInviteIds = applyService.deleteOtherSupplierShopRelationInviteByInvitedShopContactMobile(shopId, shopRelationInviteDTO.getOriginShopId(), supplierDTO.getContactMobiles());
      //同理删除此供应商的客户下的 与 本店 联系人通手机号的删除
      shopRelationInviteIds.addAll(applyService.deleteOtherCustomerShopRelationInviteByInvitedShopContactMobile(shopId, shopRelationInviteDTO.getOriginShopId(), customerDTO.getContactMobiles()));
      for (Long shopRelationInviteId : shopRelationInviteIds) {
        pushMessageService.disabledPushMessageReceiverBySourceId(null, shopRelationInviteId,null,PushMessageSourceType.APPLY_CUSTOMER,PushMessageSourceType.APPLY_SUPPLIER);
      }
      noticeService.createCustomerAcceptNoticeToSupplier(shopDTO, shopRelationInviteDTO, customerDTO);
      noticeService.createCustomerAcceptNoticeToCustomer(supplierShopDTO, shopRelationInviteDTO, supplierDTO);
      shopRelationInviteDTO.setCustomerId(customerDTO.getId());
      shopRelationInviteDTO.setSupplierId(supplierDTO.getId());
      applyService.updateShopRelationInvite(shopRelationInviteDTO);
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, "SUPPLIER_APPLY_GUIDE_SHOW_APPLY");

      supplierDTO.setPartShopDTOInfo(supplierShopDTO, true);
      result.setData(supplierDTO);
      return result;
    } catch (Exception e) {
      LOG.error("method=acceptSupplierApply,inviteId :{}" + e.getMessage(), inviteId, e);
      return new Result("网络异常", false);
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.INVITE, inviteId);
    }
  }

  /**
   * @param request
   * @param modelMap
   * @param inviteId
   * @return
   */
  @RequestMapping(params = "method=refuseApply")
  @ResponseBody
  public Object refuseApply(HttpServletRequest request, ModelMap modelMap, Long inviteId, String refuseMsg) {
    Long shopId = null;
    Result result = null;
    Long a = System.currentTimeMillis();
    try {
      shopId = WebUtil.getShopId(request);
      ShopDTO shopDTO = configService.getShopById(shopId);
      IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
      ShopRelationInviteDTO shopRelationInviteDTO = applyService.getShopRelationInviteDTOByInvitedShopIdAndId(shopId, inviteId);
      result = applyService.validateRefuseApply(shopDTO, shopRelationInviteDTO);
      System.out.println("1:" + (System.currentTimeMillis() - a));
      if (result != null && !result.isSuccess()) {
        return result;
      }
      if (!BcgogoConcurrentController.lock(ConcurrentScene.INVITE, inviteId)) {
        return new Result("当前请求正在被处理，请稍候再试！");
      }
      shopRelationInviteDTO.setOperationMan(WebUtil.getUserName(request));
      shopRelationInviteDTO.setOperationManId(WebUtil.getUserId(request));
      shopRelationInviteDTO.setRefuseMsg(refuseMsg);
      applyService.refuseApply(shopRelationInviteDTO);
      //更新 推送消息
      pushMessageService.readPushMessageReceiverBySourceId(shopRelationInviteDTO.getOriginShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInvitedShopId(),PushMessageSourceType.APPLY_CUSTOMER,PushMessageSourceType.APPLY_SUPPLIER);

      noticeService.createRefuseNotice(shopDTO, shopRelationInviteDTO);
      userGuideService.finishedUserGuideFlow(WebUtil.getShopVersionId(request), WebUtil.getUserId(request), "SUPPLIER_APPLY_GUIDE");
      return result;
    } catch (Exception e) {
      LOG.error("method=refuseCustomerApply,inviteId :{}" + e.getMessage(), inviteId, e);
      return new Result("网络异常", false);
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.INVITE, inviteId);
      System.out.println("6:" +(System.currentTimeMillis() - a));
    }
  }

  /**
   * @param request
   * @param modelMap
   * @param inviteType
   * @return
   */
  @Deprecated
  @RequestMapping(params = "method=searchInvite")
  @ResponseBody
  public Object searchInvite(HttpServletRequest request, ModelMap modelMap, InviteType inviteType, String statusStr, Long originShopId) {
    Long shopId = null;
    Result result = null;
    try {
      shopId = WebUtil.getShopId(request);
      int currentPage = NumberUtil.intValue(request.getParameter("startPageNo"), 1);
      int pageSize = NumberUtil.intValue(request.getParameter("maxRows"), 10);
      Pager pager = new Pager(currentPage, pageSize, true);
      List<InviteStatus> inviteStatuses = InviteStatus.parseNameList(statusStr);
      PagingListResult<ShopRelationInviteDTO> pagingListResult = applyService.getShopRelationInvites(shopId, inviteType, inviteStatuses, originShopId, pager);

      Set<Long> shopIdSet = new HashSet();
      if(pagingListResult != null){
        for(ShopRelationInviteDTO shopRelationInviteDTO : pagingListResult.getResults()){
          shopIdSet.add(shopRelationInviteDTO.getOriginShopId());
        }
      }

      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      Map<Long,String> map = preciseRecommendService.getSecondCategoryByShopId(shopIdSet);

      if(pagingListResult != null) {
        for (ShopRelationInviteDTO shopRelationInviteDTO : pagingListResult.getResults()) {
          if (StringUtil.isNotEmpty(map.get(shopRelationInviteDTO.getOriginShopId()))) {
            shopRelationInviteDTO.setOriginBusinessScope(map.get(shopRelationInviteDTO.getOriginShopId()));
          }
        }
      }
      return pagingListResult;


    } catch (Exception e) {
      LOG.error("method=searchInvite,inviteId :{}" + e.getMessage(), inviteType, e);
      return new Result("网络异常", false);
    }
  }

  //客户取消供应商关联
  @RequestMapping(params = "method=validateCustomerCancelSupplierShopRelation")
  @ResponseBody
  public Object validateCustomerCancelSupplierShopRelation(HttpServletRequest request, Long supplierId) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId, shopId);
      return rfiTxnService.validateCustomerCancelSupplierShopRelation(shopId, supplierDTO);
    } catch (Exception e) {
      LOG.error("method=validateCustomerCancelSupplierShopRelation,supplierId :{}" + e.getMessage(), supplierId, e);
      return new Result("网络异常", false);
    }
  }

  @RequestMapping(params = "method=deleteShopRelationInvite")
  @ResponseBody
  public Object deleteShopRelationInvite(HttpServletRequest request, String shopRelationInviteIds) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      String[] shopRelationInviteIdsStr = StringUtil.isEmpty(shopRelationInviteIds) ? null : shopRelationInviteIds.split(",");
      if (!ArrayUtils.isEmpty(shopRelationInviteIdsStr)) {
        applyService.deleteShopRelationInvites(shopId, WebUtil.getUserId(request), WebUtil.getUserName(request), ArrayUtil.convertToLong(shopRelationInviteIdsStr));
      }
      return result;
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.error("method=updateReceiveStationMessageToRead,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }

  //客户取消供应商关联
  @RequestMapping(params = "method=customerCancelSupplierShopRelation")
  @ResponseBody
  public Object customerCancelSupplierShopRelation(HttpServletRequest request, ModelMap modelMap, Long supplierId, String cancelMsg) {
    Long shopId = null;
    Result result = null;
    List<Long> lockShopIds = new ArrayList<Long>();
    try {
      shopId = WebUtil.getShopId(request);
      SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId, shopId);
      result = rfiTxnService.validateCustomerCancelSupplierShopRelation(shopId, supplierDTO);
      if (result != null && !result.isSuccess()) {
        return result;
      }
      Long supplierShopId = supplierDTO.getSupplierShopId();
      lockShopIds.add(shopId);
      lockShopIds.add(supplierShopId);
      if (!BcgogoConcurrentController.lock(ConcurrentScene.CANCEL_SHOP_RELATION, lockShopIds)) {
        return new Result("您已提交取消关联请求，请勿重复提交!", false);
      }
      applyService.cancelShopRelation(shopId, supplierShopId, shopId, WebUtil.getUserId(request), cancelMsg);
      List<Long> customerIdList = customerService.cancelCustomerRelationAndReindex(shopId, supplierShopId);
      if (CollectionUtils.isNotEmpty(customerIdList)) {
        for (Long customerId : customerIdList) {
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerId);
        }
      }

      rfiTxnService.cancelSupplierRelationAndReindex(supplierShopId, shopId);
      productService.cancelProductRelation(shopId, supplierShopId);
      noticeService.createCancelNotice(shopId, supplierShopId, cancelMsg);
      return result;
    } catch (Exception e) {
      LOG.error("method=cancelSupplierShopRelation,supplierId :{}" + e.getMessage(), supplierId, e);
      return new Result("网络异常", false);
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.CANCEL_SHOP_RELATION, lockShopIds);
    }
  }

  //取消供应商关联
  @RequestMapping(params = "method=validateSupplierCancelCustomerShopRelation")
  @ResponseBody
  public Object validateSupplierCancelCustomerShopRelation(HttpServletRequest request, Long customerId) {
    Long shopId = null;
    Result result = null;
    try {
      shopId = WebUtil.getShopId(request);
      CustomerDTO customerDTO = customerService.getCustomerById(customerId, shopId);
      return rfiTxnService.validateSupplierCancelCustomerShopRelation(shopId, customerDTO);
    } catch (Exception e) {
      LOG.error("method=validateSupplierCancelCustomerShopRelation,customerId :{}" + e.getMessage(), customerId, e);
      return new Result("网络异常", false);
    }
  }

  //取消供应商关联
  @RequestMapping(params = "method=supplierCancelCustomerShopRelation")
  @ResponseBody
  public Object supplierCancelCustomerShopRelation(HttpServletRequest request, ModelMap modelMap, Long customerId, String cancelMsg) {
    Long shopId = null;
    Result result = null;
    List<Long> lockShopIds = new ArrayList<Long>();
    try {
      shopId = WebUtil.getShopId(request);
      CustomerDTO customerDTO = customerService.getCustomerById(customerId, shopId);
      result = rfiTxnService.validateSupplierCancelCustomerShopRelation(shopId, customerDTO);
      if (result != null && !result.isSuccess()) {
        return result;
      }
      Long customerShopId = customerDTO.getCustomerShopId();
      lockShopIds.add(shopId);
      lockShopIds.add(customerShopId);
      if (!BcgogoConcurrentController.lock(ConcurrentScene.CANCEL_SHOP_RELATION, lockShopIds)) {
        return new Result("您已提交取消关联请求，请勿重复提交!", false);
      }
      applyService.cancelShopRelation(customerShopId, shopId, shopId, WebUtil.getUserId(request), cancelMsg);
      List<Long> customerIdList = customerService.cancelCustomerRelationAndReindex(customerShopId, shopId);
      if (CollectionUtils.isNotEmpty(customerIdList)) {
        for (Long cId : customerIdList) {
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(cId);
        }
      }

      rfiTxnService.cancelSupplierRelationAndReindex(shopId, customerShopId);
      productService.cancelProductRelation(customerShopId, shopId);
      noticeService.createCancelNotice(shopId, customerShopId, cancelMsg);
      return result;
    } catch (Exception e) {
      LOG.error("method=supplierCancelCustomerShopRelation,customerId :{}" + e.getMessage(), customerId, e);
      return new Result("网络异常", false);
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.CANCEL_SHOP_RELATION, lockShopIds);
    }
  }
}