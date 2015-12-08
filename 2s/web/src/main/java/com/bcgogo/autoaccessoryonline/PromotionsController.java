package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.*;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.*;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.product.service.PromotionsService;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;
import com.bcgogo.txn.model.PurchaseOrder;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-6-6
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/promotions.do")
public class PromotionsController {
  private static final Logger LOG = LoggerFactory.getLogger(PromotionsController.class);
  private static final String PROMOTIONS_LIST="redirect:promotions.do?method=toPromotionsList";
  private static final String TO_PRODUCT_IN_PROMOTIONS="redirect:promotions.do?method=toProductInPromotion";
  @Autowired
  private IPromotionsService promotionsService;
  @Autowired
  private ITradePushMessageService tradePushMessageService;

  @RequestMapping(params = "method=toPromotionsList")
  public String toPromotionsList(ModelMap modelMap, HttpServletRequest request) {
    modelMap.addAttribute("startTime",DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,System.currentTimeMillis()));
    return "/autoaccessoryonline/promotions/promotionList";
  }

  @RequestMapping(params = "method=toPromotionsManager")
  public String toPromotionsManager(ModelMap modelMap, HttpServletRequest request){
    try {

      return "/autoaccessoryonline/promotions/promotionManager";
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PROMOTIONS_LIST;
    }
  }

  @RequestMapping(params = "method=toManageMLJ")
  public String toManageMLJ(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    try {
      PromotionsDTO promotionsDTO=null;
      if(promotionsId!=null){
        Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),promotionsId));
        if(promotions==null|| DeletedType.TRUE.equals(promotions.getDeleted())){
          return PROMOTIONS_LIST;
        }
        promotionsDTO=promotions.toDTO();
        List<PromotionsRuleDTO> ruleDTOs=new ArrayList<PromotionsRuleDTO>();
        List<PromotionsRule> promotionsRules= promotionsService.getPromotionsRuleByPromotionsIds(WebUtil.getShopId(request), promotionsId);
        if(CollectionUtil.isNotEmpty(promotionsRules)){
          for(PromotionsRule promotionsRule:promotionsRules){
            if(promotionsRule==null) continue;
            ruleDTOs.add(promotionsRule.toDTO());
          }
        }
        promotionsDTO.setPromotionsRuleDTOList(ruleDTOs);
      }else {
        promotionsDTO=new PromotionsDTO();
        promotionsDTO.setType(PromotionsEnum.PromotionsTypes.MLJ);
      }
      modelMap.put("promotionsDTO",promotionsDTO);
      modelMap.addAttribute("startTime",DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,System.currentTimeMillis()));
      return "/autoaccessoryonline/promotions/manageMLJ";
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PROMOTIONS_LIST;
    }
  }

  @RequestMapping(params = "method=toManageMJS")
  public String toManageMJS(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    try {
      PromotionsDTO promotionsDTO=null;
      if(promotionsId!=null){
        Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),promotionsId));
        if(promotions==null|| DeletedType.TRUE.equals(promotions.getDeleted())){
          return PROMOTIONS_LIST;
        }
        promotionsDTO=promotions.toDTO();
      }else {
        promotionsDTO=new PromotionsDTO();
        promotionsDTO.setType(PromotionsEnum.PromotionsTypes.MJS);
      }
      modelMap.put("promotionsDTO",promotionsDTO);
      modelMap.addAttribute("startTime",DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,System.currentTimeMillis()));
      return "/autoaccessoryonline/promotions/manageMJS";
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PROMOTIONS_LIST;
    }
  }

  @RequestMapping(params = "method=getPromotionsMJSDetail")
  @ResponseBody
  public Object getPromotionsMJSDetail(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    if(promotionsId==null){
      return null;
    }
    try {
      Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),promotionsId));
      if(promotions==null|| DeletedType.TRUE.equals(promotions.getDeleted())){
        return null;
      }
      PromotionsDTO promotionsDTO=promotions.toDTO();
      List<PromotionsRuleDTO> ruleDTOs=new ArrayList<PromotionsRuleDTO>();
      List<PromotionsRule> promotionsRules= promotionsService.getPromotionsRuleByPromotionsIds(WebUtil.getShopId(request), promotionsId);
      if(CollectionUtil.isNotEmpty(promotionsRules)){
        for(PromotionsRule promotionsRule:promotionsRules){
          if(promotionsRule==null) continue;
          PromotionsRuleDTO ruleDTO=promotionsRule.toDTO();
          List<PromotionsRuleMJS> ruleMJSs=promotionsService.getPromotionsRuleMJSByRuleIds(WebUtil.getShopId(request), promotionsRule.getId());
          List<PromotionsRuleMJSDTO> mjsdtos=new ArrayList<PromotionsRuleMJSDTO>();
          if(CollectionUtil.isNotEmpty(ruleMJSs)){
            for(PromotionsRuleMJS ruleMJS:ruleMJSs){
              if(ruleMJS==null) continue;
              if(PromotionsEnum.GiftType.GIFT.equals(ruleMJS.getGiftType())){
                ruleDTO.setGiveGiftFlag(true);
              }else if(PromotionsEnum.GiftType.DEPOSIT.equals(ruleMJS.getGiftType())){
                ruleDTO.setGiveDepositFlag(true);
              }
              mjsdtos.add(ruleMJS.toDTO());
            }
          }
          ruleDTO.setPromotionsRuleMJSDTOs(mjsdtos);
          ruleDTOs.add(ruleDTO);
        }
      }
      promotionsDTO.setPromotionsRuleDTOList(ruleDTOs);
      return promotionsDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=toManageBargain")
  public String toManageBargain(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    try {
      PromotionsDTO promotionsDTO=null;
      if(promotionsId!=null){
        Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),promotionsId));
        if(promotions==null|| DeletedType.TRUE.equals(promotions.getDeleted())){
          return PROMOTIONS_LIST;
        }
        promotionsDTO=promotions.toDTO();
      }else {
        promotionsDTO=new PromotionsDTO();
        promotionsDTO.setType(PromotionsEnum.PromotionsTypes.BARGAIN);
      }
      modelMap.put("promotionsDTO",promotionsDTO);
      modelMap.addAttribute("startTime",DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,System.currentTimeMillis()));
      return "/autoaccessoryonline/promotions/manageBargain";
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PROMOTIONS_LIST;
    }
  }

  @RequestMapping(params = "method=toManageFreeShipping")
  public String toManageFreeShipping(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    try {
      PromotionsDTO promotionsDTO=null;
      if(promotionsId!=null){
        Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),promotionsId));
        List<PromotionsRuleDTO> promotionRules = promotionsService.getPromotionsRuleDTOByPromotionsIds(WebUtil.getShopId(request), promotionsId);
        if(promotions==null|| DeletedType.TRUE.equals(promotions.getDeleted())){
          return PROMOTIONS_LIST;
        }
        promotionsDTO=promotions.toDTO();
        if(CollectionUtils.isNotEmpty(promotionRules)){
          promotionsDTO.setPromotionsRuleDTOList(promotionRules);
          modelMap.put("limitAmount", promotionRules.get(0).getMinAmountStr());
        }
      }else {
        promotionsDTO=new PromotionsDTO();
        promotionsDTO.setType(PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
      }
      modelMap.put("shop",ServiceManager.getService(IShopService.class).getShopAreaInfo(WebUtil.getShopId(request)));
      modelMap.addAttribute("startTime",DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,System.currentTimeMillis()));
      modelMap.put("promotionsDTO",promotionsDTO);
      modelMap.put("promotionsDTOJson",JsonUtil.objectToJson(promotionsDTO));
      return "/autoaccessoryonline/promotions/manageFreeShipping";
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PROMOTIONS_LIST;
    }
  }

  @RequestMapping(params = "method=getFreeShippingDetail")
  @ResponseBody
  public Object getFreeShippingDetail(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    if(promotionsId==null){
      return null;
    }
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    try {
      PromotionsDTO promotionsDTO=new PromotionsDTO();
      Map<Long, List<PromotionsArea>> pAreaMap=promotionsService.getPromotionsAreaByPromotionsId(WebUtil.getShopId(request),promotionsId);
      List<PromotionsArea> pAreaList = pAreaMap.get(promotionsId);
      List<AreaDTO>  areaDTOs=new ArrayList<AreaDTO>();
      if(CollectionUtil.isNotEmpty(pAreaList)){
        for(PromotionsArea pArea:pAreaList){
          AreaDTO areaDTO=new AreaDTO();
          areaDTO.setNo(pArea.getAreaNo());
          areaDTOs.add(areaDTO);
        }
        promotionsDTO.setPostType(CollectionUtil.getFirst(pAreaList).getPostType());
      }
      promotionsDTO.setAreaDTOs(areaDTOs.toArray(new AreaDTO[areaDTOs.size()]));
      List<PromotionsRuleDTO> promotionRules = promotionsService.getPromotionsRuleDTOByPromotionsIds(WebUtil.getShopId(request), promotionsId);
      promotionsDTO.setPromotionsRuleDTOList(promotionRules);
      return promotionsDTO;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=getSpecialCustomerDetail")
  @ResponseBody
  public Result getSpecialCustomerDetail(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    Result result=new Result();
    if(promotionsId==null){
      return result.LogErrorMsg("参数异常！");
    }
    try{
      IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
      Promotions promotions=promotionsService.getSpecialCustomer(WebUtil.getShopId(request));
      if(promotions==null){
        return result.LogErrorMsg("促销不存在或已经删除！");
      }
      PromotionsDTO promotionsDTO=promotions.toDTO();
      promotionsDTO.setUserName(WebUtil.getUserName(request));
      List<PromotionsRule> ruleList=promotionsService.getPromotionsRuleByPromotionsIds(WebUtil.getShopId(request),promotions.getId());
      List<PromotionsRuleDTO> ruleDTOs=new ArrayList<PromotionsRuleDTO>();
      if(CollectionUtil.isNotEmpty(ruleList)){
        for(PromotionsRule rule:ruleList){
          if(rule==null) continue;
          ruleDTOs.add(rule.toDTO());
        }
      }
      promotionsDTO.setPromotionsRuleDTOList(ruleDTOs);
      promotionsDTO.setPromotionsContent(promotionsDTO.generatePromotionsContent());
      result.setData(promotionsDTO);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=toSpecialCustomerList")
  public String toSpecialCustomerList(ModelMap modelMap, HttpServletRequest request) {
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    Promotions promotions=promotionsService.getSpecialCustomer(WebUtil.getShopId(request));
    PromotionsDTO promotionsDTO=null;
    if(promotions!=null){
      promotionsDTO=promotions.toDTO();
    }
    modelMap.put("promotions",promotionsDTO);
    return "/autoaccessoryonline/promotions/specialCustomerList";
  }

  @RequestMapping(params = "method=toManageSpecialCustomer")
  public String toManageSpecialCustomer(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    try {
      PromotionsDTO promotionsDTO=null;
      if(promotionsId!=null){
        Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),promotionsId));
        if(promotions==null|| DeletedType.TRUE.equals(promotions.getDeleted())){
          return PROMOTIONS_LIST;
        }
        promotionsDTO=promotions.toDTO();
        List<PromotionsRuleDTO> ruleDTOs=new ArrayList<PromotionsRuleDTO>();
        List<PromotionsRule> promotionsRules=promotionsService.getPromotionsRuleByPromotionsIds(promotionsDTO.getShopId(),promotions.getId());
        if(CollectionUtil.isNotEmpty(promotionsRules)){
          for(PromotionsRule rule:promotionsRules){
            if(rule==null||DeletedType.TRUE.equals(rule.getDeleted())){
              continue;
            }
            ruleDTOs.add(rule.toDTO());
          }
        }
        promotionsDTO.setPromotionsRuleDTOList(ruleDTOs);
      }else {
        promotionsDTO=new PromotionsDTO();
        promotionsDTO.setType(PromotionsEnum.PromotionsTypes.SPECIAL_CUSTOMER);
      }
      modelMap.put("promotionsDTO",promotionsDTO);
      return "/autoaccessoryonline/promotions/manageSpecialCustomer";
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return PROMOTIONS_LIST;
    }
  }

  @RequestMapping(params = "method=toAddPromotionsProduct")
  public String toAddPromotionsProduct(ModelMap modelMap,HttpServletRequest request,String promotionsId) {
    if(StringUtil.isEmpty(promotionsId)){
      return PROMOTIONS_LIST;
    }
    IPromotionsService promotionsService= ServiceManager.getService(IPromotionsService.class);
    Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),NumberUtil.longValue(promotionsId)));
    PromotionsDTO dto=null;
    try{
      if(promotions!=null){
        dto=promotions.toDTO();
        modelMap.put("promotions",dto);
      }else {
        dto=new PromotionsDTO();
      }
      modelMap.put("promotions",dto);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    if(PromotionsEnum.PromotionsTypes.BARGAIN.equals(dto.getType())){
      return "/autoaccessoryonline/promotions/addBargainProduct";
    }else{
      return "/autoaccessoryonline/promotions/addPromotionsProduct";
    }
  }


  @RequestMapping(params = "method=getAddedBargainProduct")
  @ResponseBody
  public Object getAddedPromotionsProduct(ModelMap modelMap,HttpServletRequest request,Long promotionsId) {
    if(promotionsId==null){
      return null;
    }
    IPromotionsService promotionsService= ServiceManager.getService(IPromotionsService.class);
    try{
      return promotionsService.getPromotionsProductDTOByPromotionsId(WebUtil.getShopId(request),promotionsId);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=getProductToAddPromotions")
  public String getProductToAddPromotions(ModelMap modelMap, HttpServletRequest request) {
    return "/autoaccessoryonline/promotions/addPromotionsProduct";
  }

  @RequestMapping(params = "method=toProductInPromotion")
  public String toProductInPromotion(ModelMap modelMap, HttpServletRequest request,Long promotionsId) {
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    if(promotionsId==null){
      return PROMOTIONS_LIST;
    }
    Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(WebUtil.getShopId(request),promotionsId));
    if(promotions!=null)
      modelMap.put("promotions",promotions.toDTO());
    return "/autoaccessoryonline/promotions/productInPromotion";
  }

  @RequestMapping(params = "method=toPromotionsProductList")
  public String toPromotionsProductList(ModelMap modelMap, HttpServletRequest request) {
    return "/autoaccessoryonline/promotions/promotionsProductList";
  }

  @RequestMapping(params = "method=toSendPromotionMsg")
  public String toSendPromotionMsg(ModelMap modelMap, HttpServletRequest request,String promotionsIdStr,String pContent) {
    modelMap.put("pContent",pContent);
    modelMap.put("promotionsId",promotionsIdStr);
    modelMap.addAttribute("sendTime",DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,System.currentTimeMillis()));
    return "/autoaccessoryonline/promotions/sendPromotionMsg";
  }

  @RequestMapping(params = "method=savePromotions")
  @ResponseBody
  public  Result savePromotions(HttpServletRequest request,PromotionsDTO promotionsDTO) {
    Result result=new Result();
    Long shopId = null;
    try{
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      promotionsDTO.setShopId(WebUtil.getShopId(request));
      promotionsDTO.setUserId(WebUtil.getUserId(request));
      promotionsDTO.setUserName(WebUtil.getUserName(request));
      promotionsDTO.setEndTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,promotionsDTO.getEndTimeStr()));
      promotionsDTO.setStartTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, promotionsDTO.getStartTimeStr()));
      promotionsService.savePromotions(result,promotionsDTO);
      if (result.isSuccess()) {
        ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId,ArrayUtil.toLongArr(result.getDataList()));
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      result.LogErrorMsg("保存促销出现异常！");
    }
    return result;
  }

  @RequestMapping(params = "method=savePromotionsForInSales")
  @ResponseBody
  public  Result savePromotionsForInSales(HttpServletRequest request,PromotionIndex promotionsDTO) {
    Result result=new Result();
    Long shopId = null;
    try{
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      promotionsDTO.setShopId(WebUtil.getShopId(request));
      promotionsDTO.setUserId(WebUtil.getUserId(request));
      promotionsDTO.setUserName(WebUtil.getUserName(request));
      promotionsService.savePromotionsForInSales(result, promotionsDTO);
      PromotionIndex condition=new PromotionIndex();
      condition.setShopId(WebUtil.getShopId(request));
      condition.setId(NumberUtil.longValue(result.getData()));
      PromotionsDTO newPromotionsDTO=CollectionUtil.getFirst(promotionsService.getPromotionDetail(condition));
      if(newPromotionsDTO!=null){
        result.setData(newPromotionsDTO);
      }
      if (result.isSuccess() &&ArrayUtil.isNotEmpty(result.getDataList())){
        ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId,ArrayUtil.toLongArr(result.getDataList()));
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      result.LogErrorMsg("保存促销出现异常！");
    }
    return result;
  }

  @RequestMapping(params = "method=validateSavePromotionsForInSales")
  @ResponseBody
  public  Result validateSavePromotionsForInSales(HttpServletRequest request,PromotionsDTO promotionsDTO) {
    Result result=new Result();
    try{
      promotionsDTO.setEndTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,promotionsDTO.getEndTimeStr()));
      promotionsDTO.setStartTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, promotionsDTO.getStartTimeStr()));
      if(promotionsDTO.getStartTime()==null||promotionsDTO.getType()==null){
        return result.LogErrorMsg("参数异常。");
      }
      PromotionsProductDTO [] promotionsProductDTOs=promotionsDTO.getPromotionsProductDTOList();
      if(!ArrayUtil.isEmpty(promotionsProductDTOs)){
        IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
        Long shopId=WebUtil.getShopId(request);
        Map<String,PromotionsDTO> lappingMap=new HashMap<String, PromotionsDTO>();
        for(PromotionsProductDTO pp:promotionsProductDTOs){
          if(pp==null||pp.getProductLocalInfoId()==null) continue;
          List<Long> productIdList=promotionsService.getOverlappingProductIdByRange(shopId, promotionsDTO, false);
          if(productIdList.contains(pp.getProductLocalInfoId())){
            ProductDTO productDTO= ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(pp.getProductLocalInfoId(),shopId);
            if(productDTO==null){
              LOG.error("商品不存在，id={}",pp.getProductLocalInfoId());
            }
            Map<Long,List<PromotionsDTO>> promotionsMap=promotionsService.getPromotionsDTOMapByProductLocalInfoId(shopId,false,pp.getProductLocalInfoId());
            if(promotionsMap!=null&&!promotionsMap.keySet().isEmpty()){
              List<PromotionsDTO> promotionsDTOs= promotionsMap.get(pp.getProductLocalInfoId());
              if(CollectionUtil.isNotEmpty(promotionsDTOs)){
                for(PromotionsDTO pTemp:promotionsDTOs){
                  if(pTemp==null){
                    continue;
                  }
                  if(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(pTemp.getType())&& !PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promotionsDTO.getType())){
                    continue;
                  }
                  lappingMap.put(productDTO.getProductLocalInfoIdStr(), pTemp);
                  break;
                }
              }
            }
          }
        }
        result.setData(lappingMap);
      }
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=updatePromotionStatus")
  @ResponseBody
  public  Result updatePromotionStatus(HttpServletRequest request,PromotionsDTO promotionsDTO){
    Long shopId=WebUtil.getShopId(request);
    Result result=new Result();
    try{
      promotionsDTO.setShopId(WebUtil.getShopId(request));
      promotionsDTO.setUserId(WebUtil.getUserId(request));
      if("reStartPromotion".equals(StringUtil.valueOf(request.getParameter("from")))){
        try{
          promotionsDTO.setStartTime (DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,promotionsDTO.getStartTimeStr()));
          promotionsDTO.setEndTime (DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,promotionsDTO.getEndTimeStr()));
        }catch (Exception e){
          LOG.error(e.getMessage(),e);
        }
      }
      promotionsService.updatePromotionStatus(result,promotionsDTO);
      if(result.isSuccess()){
        List<PromotionsProductDTO> promotionsProductDTOList = promotionsService.getPromotionsProductDTOByPromotionsId(WebUtil.getShopId(request),promotionsDTO.getId());
        if(PromotionsEnum.PromotionStatus.USING.equals(promotionsDTO.getStatus())){
          List<Long> productIdList = new ArrayList<Long>();
          if(CollectionUtil.isNotEmpty(promotionsProductDTOList)){
            for(PromotionsProductDTO promotionsProductDTO : promotionsProductDTOList){
              productIdList.add(promotionsProductDTO.getProductLocalInfoId());
            }
            ServiceManager.getService(ITradePushMessageService.class).generatePromotionMsgTask(shopId,ArrayUtil.toLongArr(productIdList));
          }
        }
        if(CollectionUtil.isNotEmpty(promotionsProductDTOList)){
          List<Long> productIdList = new ArrayList<Long>();
          for(PromotionsProductDTO promotionsProductDTO : promotionsProductDTOList){
            productIdList.add(promotionsProductDTO.getProductLocalInfoId());
          }
          IProductSolrWriterService solrWriter=ServiceManager.getService(IProductSolrWriterService.class);
          solrWriter.createProductSolrIndex(shopId, productIdList.toArray(new Long[productIdList.size()]));
        }
      }
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      result.LogErrorMsg("保存促销出现异常！");
    }
    return result;
  }

  @RequestMapping(params = "method=deletePromotions")
  @ResponseBody
  public  Result deletePromotions(HttpServletRequest request,Long promotionsId) {
    Result result=new Result();
    Long shopId = null;
    try{
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      if(promotionsId==null){
        return result.LogErrorMsg("参数异常！");
      }
      promotionsService.deletePromotions(result,WebUtil.getShopId(request),promotionsId);
      Long [] productIds=ArrayUtil.toLongArr(result.getDataList());
      if (result.isSuccess() &&ArrayUtil.isNotEmpty(result.getDataList())){
        ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productIds);
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      result.LogErrorMsg("删除促销出现异常！");
    }
    return result;
  }

  @RequestMapping(params = "method=deleteSpecialCustomer")
  @ResponseBody
  public  Result deleteSpecialCustomer(HttpServletRequest request,Long promotionsId) {
    Result result=new Result();
    try{
      if(promotionsId==null){
        return result.LogErrorMsg("参数异常！");
      }
      promotionsService.deletePromotions(result,WebUtil.getShopId(request),promotionsId);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      result.LogErrorMsg("客户优惠出现异常！");
    }
    return result;
  }

  @RequestMapping(params = "method=getPromotionsDTO")
  @ResponseBody
  public  Result getPromotionsDTO(HttpServletRequest request,PromotionIndex condition) {
    PagingListResult<PromotionsDTO> result=new PagingListResult<PromotionsDTO>();
    try{
      if(condition.getType()==null){    //过滤掉客户优惠
        PromotionsEnum.PromotionsTypes[] types=new PromotionsEnum.PromotionsTypes[]{
          PromotionsEnum.PromotionsTypes.MJS,
          PromotionsEnum.PromotionsTypes.MLJ,
          PromotionsEnum.PromotionsTypes.BARGAIN,
          PromotionsEnum.PromotionsTypes.FREE_SHIPPING
        };
        condition.setTypes(types);
      }
      condition.setShopId(WebUtil.getShopId(request));
      Pager pager=new Pager(promotionsService.countPromotions(condition),condition.getStartPageNo());
      condition.setPager(pager);
      List<PromotionsDTO> dtos=promotionsService.getPromotionsDTO(condition);
      List<Long> promotionsIdList=new ArrayList<Long>();
      List<Long> mjsIdList=new ArrayList<Long>();
      if(CollectionUtil.isNotEmpty(dtos)){
        for(PromotionsDTO dto:dtos){
          promotionsIdList.add(dto.getId());
          if(PromotionsEnum.PromotionsTypes.MJS.equals(dto.getType())){
            mjsIdList.add(dto.getId());
          }
        }
      }
      Map<Long,List<PromotionsRuleDTO>> ruleMap=promotionsService.getPromotionsRuleDTOMap(WebUtil.getShopId(request),
        promotionsIdList.toArray(new Long[promotionsIdList.size()]));
      if(CollectionUtil.isNotEmpty(mjsIdList)){
        for (Long key:mjsIdList){
          List<PromotionsRuleDTO> ruleDTOs=ruleMap.get(key);
          if(CollectionUtil.isNotEmpty(ruleDTOs)){
            for(PromotionsRuleDTO ruleDTO:ruleDTOs){
              List<PromotionsRuleMJS> ruleMJSs=promotionsService.getPromotionsRuleMJSByRuleIds(WebUtil.getShopId(request),ruleDTO.getId());
              List<PromotionsRuleMJSDTO> mjsdtos=new ArrayList<PromotionsRuleMJSDTO>();
              if(CollectionUtil.isNotEmpty(ruleMJSs)){
                for(PromotionsRuleMJS ruleMJS:ruleMJSs){
                  if(ruleMJS==null) continue;
                  if(PromotionsEnum.GiftType.GIFT.equals(ruleMJS.getGiftType())){
                    ruleDTO.setGiveGiftFlag(true);
                  }else if(PromotionsEnum.GiftType.DEPOSIT.equals(ruleMJS.getGiftType())){
                    ruleDTO.setGiveDepositFlag(true);
                  }
                  mjsdtos.add(ruleMJS.toDTO());
                }
              }
              ruleDTO.setPromotionsRuleMJSDTOs(mjsdtos);
            }
          }
        }
      }

      if(ruleMap!=null&&CollectionUtil.isNotEmpty(ruleMap.keySet())&&
        CollectionUtil.isNotEmpty(dtos)){
        for(PromotionsDTO dto:dtos){
          dto.setPromotionsRuleDTOList(ruleMap.get(dto.getId()));
        }
      }
      result.setResults(dtos);
      result.setPager(pager);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      result.LogErrorMsg("保存促销出现异常！");
    }
    return result;
  }

  @RequestMapping(params = "method=getPromotionsSuggestion")
  @ResponseBody
  public  Object getPromotionsSuggestion(HttpServletRequest request,String keyword) {
    try{
      PromotionIndex condition=new PromotionIndex();
      condition.setName(keyword);
      condition.setShopId(WebUtil.getShopId(request));
      condition.setPager(new Pager(15,1));
      List<PromotionsDTO> promotionsDTOs=promotionsService.getPromotionsDTO(condition);
      List<Map<String,String>> mapList = new ArrayList<Map<String, String>>();
      if(CollectionUtil.isNotEmpty(promotionsDTOs)){
        for(PromotionsDTO promotionsDTO:promotionsDTOs){
          Map<String,String> pNameMap = new HashMap<String,String>();
          pNameMap.put("label",promotionsDTO.getName());
          mapList.add(pNameMap);
        }
      }
      Map<String,Object> resultMap = new HashMap<String,Object>();
      resultMap.put("data",mapList);
      resultMap.put("uuid",request.getParameter("uuid"));
      return resultMap;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  //查solr 同 getProductPromotionDetail
  @RequestMapping(params = "method=getOrderPromotionsDetail")
  @ResponseBody
  public Object getOrderPromotionsDetail(HttpServletRequest request,SearchConditionDTO searchConditionDTO) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      searchConditionDTO.setMaxRows(500);
      return txnService.getOrderPromotionsDetail(WebUtil.getShopId(request),searchConditionDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  //查商品详细的促销信息
  @RequestMapping(params = "method=getProductPromotionDetail")
  @ResponseBody
  public Object getProductPromotionDetail(HttpServletRequest request,Long[] productIds) {
    try {
      return promotionsService.getProductPromotionDetail(WebUtil.getShopId(request),productIds);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  //查询促销列表信息
  @RequestMapping(params = "method=getPromotionDetail")
  @ResponseBody
  public Object getPromotionDetail(HttpServletRequest request,PromotionIndex condition) {
    try {
      condition.setShopId(WebUtil.getShopId(request));
      return promotionsService.getPromotionDetail(condition);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getPromotionsProduct")
  @ResponseBody
  public Object getPromotionsProduct(HttpServletRequest request,String fromSource,SearchConditionDTO searchConditionDTO) {
    Long shopId=WebUtil.getShopId(request);
    try {
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      if(StringUtil.isEmpty(searchConditionDTO.getSort())&&searchConditionDTO.isEmptyOfProductInfo()){
        searchConditionDTO.setSort("last_in_sales_time desc");//默认上架时间排序
      }else {
        searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSort()));
      }
      PagingListResult<ProductDTO> result = new PagingListResult<ProductDTO>();
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      if("PurchaseOnline".equals(fromSource)){
        searchConditionDTO.setIncludeBasic(false);
        List<SupplierDTO> supplierDTOList = ServiceManager.getService(IUserService.class).getRelatedSuppliersByShopId(shopId);
        Set<Long> supplierShopIdSet = new HashSet<Long>();
        if (CollectionUtils.isNotEmpty(supplierDTOList)) {
          for (SupplierDTO supplierDTO : supplierDTOList) {
            supplierShopIdSet.add(supplierDTO.getSupplierShopId());
          }
        }
        searchConditionDTO.setShopId(null);
        searchConditionDTO.setShopIds(supplierShopIdSet.toArray(new Long[supplierShopIdSet.size()]));
      }
      if (PromotionsUtils.ADD_PROMOTIONS_PRODUCT_CURRENT.equals(searchConditionDTO.getPromotionsFilter())){
        Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(shopId,searchConditionDTO.getPromotionsId()));
        if(promotions!=null){
          List<Long> productIdList= promotionsService.getOverlappingProductIdByRange(shopId,promotions.toDTO(),false);
          searchConditionDTO.setOverlappingProductIds(productIdList);
        }
      }else  if (PromotionsUtils.ADD_PROMOTIONS_PRODUCT.equals(searchConditionDTO.getPromotionsFilter())){
        Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(shopId,searchConditionDTO.getPromotionsId()));
        if(promotions!=null){
          List<Long> productIdList= promotionsService.getOverlappingProductIdByRange(shopId,promotions.toDTO(),true);
          searchConditionDTO.setOverlappingProductIds(productIdList);
        }
      }
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithStdQuery(searchConditionDTO);
      if(CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())){
        for(ProductDTO productDTO : productSearchResultListDTO.getProducts()){
          PromotionsUtils.setPromotionsProductToProductDTO(productDTO);
        }
      }

      Pager pager = new Pager(Integer.valueOf(productSearchResultListDTO.getNumFound() + ""), searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      result.setResults(productSearchResultListDTO.getProducts());

      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(params = "method=addPromotionsProduct")
  @ResponseBody
  public Object addPromotionsProduct(HttpServletRequest request,PromotionsDTO promotionsDTO) {
    Result result=new Result(true);
    Long shopId=WebUtil.getShopId(request);
    try {
      Map<Long,PromotionsProductDTO[]> promotionsProductDTOMap=new HashMap<Long, PromotionsProductDTO[]>();
      promotionsProductDTOMap.put(promotionsDTO.getId(),promotionsDTO.getPromotionsProductDTOList());
      promotionsService.addPromotionsProduct(result, shopId,promotionsProductDTOMap);
      if(result.isSuccess()){
        ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId,ArrayUtil.toLongArr(result.getDataList()));
        ServiceManager.getService(ITradePushMessageService.class).generatePromotionMsgTask(shopId,ArrayUtil.toLongArr(result.getDataList()));
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.LogErrorMsg("添加商品异常。");
    }
    result.setData(StringUtil.valueOf(promotionsDTO.getId()));
    return result;
  }

//    @RequestMapping(params = "method=batchAddPromotionsProduct")
//    @ResponseBody
//    public Object batchAddPromotionsProduct(HttpServletRequest request,PromotionsDTO promotionsDTO,Long lappingPromotionsId) {
//        Result result=new Result(true);
//        Long shopId=WebUtil.getShopId(request);
//        try {
//            Long promotionsId=promotionsDTO.getId();
//            PromotionsProductDTO[] promotionsProductDTOs=promotionsDTO.getPromotionsProductDTOList();
//            if(promotionsId==null||ArrayUtil.isEmpty(promotionsProductDTOs)){
//                return result.LogErrorMsg("参数异常。");
//            }
//            for (PromotionsProductDTO pp:promotionsProductDTOs){
//                if(pp==null){
//                    continue;
//                }
//                ServiceManager.getService(IPromotionsService.class).deletePromotionsProduct(result,shopId,lappingPromotionsId,pp.getProductLocalInfoId());
//            }
//            Map<Long,PromotionsProductDTO[]> promotionsProductDTOMap=new HashMap<Long, PromotionsProductDTO[]>();
//            promotionsProductDTOMap.put(promotionsId,promotionsDTO.getPromotionsProductDTOList());
//            promotionsService.addPromotionsProduct(result, shopId,promotionsProductDTOMap);
//            if(result.isSuccess()){
//                ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId,ArrayUtil.toLongArr(result.getDataList()));
//                ServiceManager.getService(ITradePushMessageService.class).generatePromotionMsgTask(shopId,ArrayUtil.toLongArr(result.getDataList()));
//            }
//            PromotionIndex condition=new PromotionIndex();
//            condition.setShopId(WebUtil.getShopId(request));
//            condition.setId(NumberUtil.longValue(result.getData()));
//            PromotionsDTO newPromotionsDTO=CollectionUtil.getFirst(promotionsService.getPromotionDetail(condition));
//            if(newPromotionsDTO!=null){
//                result.setData(newPromotionsDTO);
//            }
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//            result.LogErrorMsg("添加商品异常。");
//        }
//        result.setData(StringUtil.valueOf(promotionsDTO.getId()));
//        return result;
//    }

  @RequestMapping(params = "method=deletePromotionsProduct")
  @ResponseBody
  public Object deletePromotionsProduct(HttpServletRequest request,Long []productIds,Long promotionsId) {
    Result result=new Result(true);
    Long shopId=WebUtil.getShopId(request);
    try {
      promotionsService.deletePromotionsProduct(result,WebUtil.getShopId(request),promotionsId,productIds);
      if(result.isSuccess()){
        IProductSolrWriterService solrWriter=ServiceManager.getService(IProductSolrWriterService.class);
        if(ArrayUtil.isNotEmpty(productIds)){
          solrWriter.createProductSolrIndex(shopId,productIds);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.LogErrorMsg("添加商品异常！");
    }
    result.setData(StringUtil.valueOf(promotionsId));
    return result;
  }

  /**
   * 促销限购，返回当前Shop对应此商品、促销使用了多少数量
   * @param request
   * @param productId
   * @param promotionsId
   * @param orderId 当前OrderId，返回的使用数字过滤掉当前单据的
   * @return
   */
  @RequestMapping(params = "method=getBargainLimitHistoryAmount")
  @ResponseBody
  public Result getBargainLimitHistoryAmount(HttpServletRequest request, Long productId, Long promotionsId, Long orderId){
    Long shopId = WebUtil.getShopId(request);
    if(shopId == null || productId == null || promotionsId == null){
      return new Result(false);
    }
    try{
      Result result = new Result(true);
      double amount = promotionsService.getPromotionOrderRecordUsedAmount(productId, promotionsId, shopId, orderId);
      result.setData(amount);
      return result;
    }catch(Exception e){
      LOG.error("promotions.do?method=getBargainLimitHistoryAmount error.", e);
      return new Result(false);
    }
  }




}
