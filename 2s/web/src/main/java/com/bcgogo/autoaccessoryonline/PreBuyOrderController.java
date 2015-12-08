package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.*;
import com.bcgogo.common.StringUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.*;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.RelationMidStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.preBuyOrder.PreBuyOrderValidDate;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductCategoryRelationDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.request.ProductsRequest;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderItemDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.ShopRelation.IShopRelationService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 下午5:30
 */
@Controller
@RequestMapping("/preBuyOrder.do")
public class PreBuyOrderController {
  private static final Logger LOG = LoggerFactory.getLogger(PreBuyOrderController.class);

  private static final String REDIRECT_SHOW = "redirect:preBuyOrder.do?method=showPreBuyOrderById";
  private static final String REDIRECT_PRE_BUY_ORDER_MANAGER = "redirect:preBuyOrder.do?method=preBuyOrderManage";
  @Autowired
  private IPreBuyOrderService preBuyOrderService;

  @RequestMapping(params = "method=createPreBuyOrder")
  public String createPreBuyOrder(ModelMap modelMap, HttpServletRequest request,PreBuyOrderDTO preBuyOrderDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      modelMap.addAttribute("preBuyOrderValidDate", PreBuyOrderValidDate.values());

      if(preBuyOrderDTO == null){
        preBuyOrderDTO = new PreBuyOrderDTO();
      }
      modelMap.addAttribute("preBuyOrderDTO",preBuyOrderDTO);
      modelMap.put("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));
      return "/autoaccessoryonline/preBuyOrder";
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=createPreBuyOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error("转向发布求购页面异常！！！");
      return null;
    }
  }

  @RequestMapping(params = "method=createPreBuyOrderByProductIdInfos")
  public String createPreBuyOrderByProductIdInfos(ModelMap modelMap, HttpServletRequest request,String... productIdInfos) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      modelMap.addAttribute("preBuyOrderValidDate", PreBuyOrderValidDate.values());

      PreBuyOrderDTO preBuyOrderDTO = new PreBuyOrderDTO();
      if(!ArrayUtils.isEmpty(productIdInfos)){
        PreBuyOrderItemDTO[] itemDTOs = null;
        Map<Long,Double> amountMap = new HashMap<Long, Double>();
        String[] arr = null;
        for (String productIdInfo : productIdInfos) {
          arr = productIdInfo.split("_");
          amountMap.put(NumberUtil.longValue(arr[0]), arr.length > 1 ? NumberUtil.doubleValue(arr[1], 1d) : 1d);
        }
        IProductService productService = ServiceManager.getService(IProductService.class);
        Map<Long,ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId,amountMap.keySet());
        for(Map.Entry<Long, ProductDTO> entry:productDTOMap.entrySet()){
          PreBuyOrderItemDTO preBuyOrderItemDTO = new PreBuyOrderItemDTO();
          preBuyOrderItemDTO.setProductDTOWithOutUnit(entry.getValue());
          preBuyOrderItemDTO.setUnit(preBuyOrderItemDTO.getSellUnit());
          preBuyOrderItemDTO.setAmount(amountMap.get(entry.getKey()));
          itemDTOs= (PreBuyOrderItemDTO[]) ArrayUtils.add(itemDTOs, preBuyOrderItemDTO);
        }
        preBuyOrderDTO.setItemDTOs(itemDTOs);
      }
      modelMap.addAttribute("preBuyOrderDTO",preBuyOrderDTO);

      return "/autoaccessoryonline/preBuyOrder";
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=createPreBuyOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error("转向发布求购页面异常！！！");
      return null;
    }
  }

  @RequestMapping(params = "method=preBuyOrderManage")
  public String preBuyOrderManage(ModelMap modelMap, HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      modelMap.addAttribute("orderSearchConditionDTO",orderSearchConditionDTO);
      return "/autoaccessoryonline/preBuyOrderList";
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=preBuyOrderManage");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error("转向求购管理页面异常！！！");
      return null;
    }
  }

  @RequestMapping(params ="method=ajaxSavePreBuyOrder")
  @ResponseBody
  public Object ajaxSavePreBuyOrder(ModelMap model, HttpServletRequest request, PreBuyOrderDTO preBuyOrderDTO) {
    preBuyOrderDTO.setBusinessChanceType(BusinessChanceType.Lack);
    preBuyOrderDTO.setPreBuyOrderValidDate(PreBuyOrderValidDate.SEVEN_DAY);
    savePreBuyOrder(model, request, preBuyOrderDTO);
    return new Result("发布求购成功！共求购" + preBuyOrderDTO.getItemDTOs().length + "种商品，此求购将推送给匹配的供应商！", true,preBuyOrderDTO);
  }

  @RequestMapping(params = "method=savePreBuyOrder")
  public String savePreBuyOrder(ModelMap model,HttpServletRequest request,PreBuyOrderDTO preBuyOrderDTO) {
    Long shopId=WebUtil.getShopId(request);
    Long userId=WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if(ArrayUtil.isEmpty(preBuyOrderDTO.getItemDTOs())){
        throw new IllegalArgumentException("item can't be empty!");
      }
      removeNullProductRow(preBuyOrderDTO);
      List<PreBuyOrderDTO> orderDTOs=new ArrayList<PreBuyOrderDTO>();
      PreBuyOrderItemDTO[] itemDTOs=preBuyOrderDTO.getItemDTOs();
      for(PreBuyOrderItemDTO itemDTO:itemDTOs){
        PreBuyOrderDTO orderDTO=new PreBuyOrderDTO();
        orderDTO.setUserId(userId);
        orderDTO.setShopId(shopId);
        orderDTO.setEditDate(System.currentTimeMillis());
        orderDTO.setEditor(WebUtil.getUserName(request));
        orderDTO.setEditorId(userId);
        //归属时间
        orderDTO.setVestDate(DateUtil.getTheDayTime());
        orderDTO.setPreBuyOrderValidDate(preBuyOrderDTO.getPreBuyOrderValidDate());
        orderDTO.setEndDate(DateUtil.getTheDayTime()+DateUtil.DAY_MILLION_SECONDS*(orderDTO.getPreBuyOrderValidDate().getValue()-1));
        PreBuyOrderItemDTO [] preBuyOrderItemDTOs=new PreBuyOrderItemDTO[1];
        preBuyOrderItemDTOs[0]=itemDTO;
        orderDTO.setItemDTOs(preBuyOrderItemDTOs);
        orderDTOs.add(orderDTO);
      }
      preBuyOrderService.savePreBuyOrder(shopId,orderDTOs.toArray(new PreBuyOrderDTO[orderDTOs.size()]));
      //保存商品主图辅图到商品
      IImageService imageService=ServiceManager.getService(IImageService.class);
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
      imageTypeSet.add(ImageType.PRODUCT_AUXILIARY_IMAGE);
      for(PreBuyOrderItemDTO itemDTO : itemDTOs){
        if(itemDTO.getImageCenterDTO()==null||CollectionUtils.isEmpty(itemDTO.getImageCenterDTO().getProductInfoImagePaths())||itemDTO.getProductId()==null){
          continue;
        }
        List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();
        List<String> imageUrlList = itemDTO.getImageCenterDTO().getProductInfoImagePaths();
        DataImageRelationDTO dataImageRelationDTO = null;
        int i=0;
        for(String imageUrl:imageUrlList){
          if(StringUtils.isNotBlank(imageUrl)){
            dataImageRelationDTO = new DataImageRelationDTO(shopId,itemDTO.getProductId(), DataType.PRODUCT,i==0?ImageType.PRODUCT_MAIN_IMAGE:ImageType.PRODUCT_AUXILIARY_IMAGE,i);
            dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,imageUrl));
            dataImageRelationDTOList.add(dataImageRelationDTO);
            i++;
          }
        }
        imageService.saveOrUpdateDataImageDTOs(shopId,imageTypeSet,DataType.PRODUCT ,itemDTO.getProductId(),dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
      }
      //推送消息
      ITradePushMessageService tradePushMessageService = ServiceManager.getService(ITradePushMessageService.class);
      List<PushMessageBuildTaskDTO> pushMessageBuildTaskDTOList = new ArrayList<PushMessageBuildTaskDTO>();
      for(PreBuyOrderItemDTO preBuyOrderItemDTO : itemDTOs){
        PushMessageBuildTaskDTO pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
        pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
        pushMessageBuildTaskDTO.setScene(PushMessageScene.PRE_BUY_ORDER_INFORMATION);
        pushMessageBuildTaskDTO.setSeedId(preBuyOrderItemDTO.getId());
        pushMessageBuildTaskDTO.setShopId(shopId);
        pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);

        pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
        pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
        pushMessageBuildTaskDTO.setScene(PushMessageScene.PRE_BUY_ORDER_ACCESSORY);
        pushMessageBuildTaskDTO.setSeedId(preBuyOrderItemDTO.getId());
        pushMessageBuildTaskDTO.setShopId(shopId);
        pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);
      }
      tradePushMessageService.savePushMessageBuildTaskDTO(pushMessageBuildTaskDTOList.toArray(new PushMessageBuildTaskDTO[pushMessageBuildTaskDTOList.size()]));
      List<Long> orderIds=new ArrayList<Long>();
      for(PreBuyOrderDTO orderDTO:orderDTOs){
        orderIds.add(orderDTO.getId());
      }
      ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(
        ServiceManager.getService(IConfigService.class).getShopById(shopId),OrderTypes.PRE_BUY_ORDER, ArrayUtil.toLongArr(orderIds));
//      model.addAttribute("preBuyOrderId",preBuyOrderDTO.getId());
//      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
//      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(preBuyOrderDTO,OrderTypes.PRE_BUY_ORDER);
//      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      return REDIRECT_PRE_BUY_ORDER_MANAGER;
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do");
      LOG.debug("method=savePreBuyOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "preBuyOrder.do?method=createPreBuyOrder";
  }

  private void removeNullProductRow(PreBuyOrderDTO preBuyOrderDTO) {
    if (!ArrayUtils.isEmpty(preBuyOrderDTO.getItemDTOs())) {
      PreBuyOrderItemDTO[] preBuyOrderItemDTOs = preBuyOrderDTO.getItemDTOs();
      List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
      for (int i = 0; i < preBuyOrderItemDTOs.length; i++) {
        if (StringUtils.isNotBlank(preBuyOrderItemDTOs[i].getProductName())) {
          preBuyOrderItemDTOList.add(preBuyOrderItemDTOs[i]);
        }
      }
      if (CollectionUtils.isNotEmpty(preBuyOrderItemDTOList)) {
        preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOList.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOList.size()]));
      }
    }
  }

//  @RequestMapping(params = "method=getPreBuyOrderList")
//  @ResponseBody
//  public Object getPreBuyOrderList(HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO){
//    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
//    Long shopId = null;
//    try {
//      shopId = WebUtil.getShopId(request);
//      if (shopId == null) throw new Exception("shopId can't be null.");
//      orderSearchConditionDTO.verificationQueryTime();
//      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.PRE_BUY_ORDER.toString()});
//      orderSearchConditionDTO.setShopId(shopId);
//      if(StringUtils.isBlank(orderSearchConditionDTO.getSearchWord()) && orderSearchConditionDTO.isEmptyOfProductInfo()){
//        orderSearchConditionDTO.setSort("created_time desc");
//      }
//      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
//      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());
//      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);
//      Map<String,String> otherDataMap = new HashMap<String, String>();
//
//      otherDataMap.put("allPreBuyOrderCount",String.valueOf(preBuyOrderService.countPreBuyOrders(shopId)));
//      otherDataMap.put("allQuotedCount",String.valueOf(preBuyOrderService.countQuotedPreBuyOrderItems(shopId)));
//      orderSearchResultListDTO.setOtherDataMap(otherDataMap);
//
//      Pager pager = new Pager(Integer.parseInt(orderSearchResultListDTO.getNumFound()+""), orderSearchConditionDTO.getStartPageNo(),orderSearchConditionDTO.getMaxRows());
//      List<Object> result = new ArrayList<Object>();
//      result.add(orderSearchResultListDTO);
//      result.add(pager);
//      return result;
//    } catch (Exception e) {
//      LOG.debug("/preBuyOrder.do?method=getPreBuyOrderList");
//      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
//      LOG.error(e.getMessage(), e);
//      return null;
//    }
//  }

  @RequestMapping(params = "method=getPreBuyOrderItem")
  @ResponseBody
  public Object getPreBuyOrderItem(HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO){
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.PRE_BUY_ORDER.toString()});
      orderSearchConditionDTO.setItemTypes(new String[]{ItemTypes.MATERIAL.toString()});
//      orderSearchConditionDTO.setPreBuyOrderStatus(OrderSearchConditionDTO.PreBuyOrderStatus.VALID);

      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      orderSearchConditionDTO.setShopKind(shopDTO.getShopKind());
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
      if(StringUtils.isBlank(orderSearchConditionDTO.getSearchWord()) && orderSearchConditionDTO.isEmptyOfProductInfo()){
        orderSearchConditionDTO.setSort("order_created_time desc");
      }
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setBusinessChanceType(BusinessChanceType.Normal);
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrderItems(orderSearchConditionDTO);
      if(CollectionUtil.isNotEmpty(orderSearchResultListDTO.getOrderItems())){
        List<Long> preBuyOrderItemIds=new ArrayList<Long>();
        for (OrderItemSearchResultDTO resultDTO:orderSearchResultListDTO.getOrderItems()){
          preBuyOrderItemIds.add(resultDTO.getItemId());
        }
        IPreBuyOrderService preBuyOrderService=ServiceManager.getService(IPreBuyOrderService.class);
        Map<Long,PreBuyOrderItemDTO> preBuyOrderItemDTOMap= preBuyOrderService.getPreBuyOrderItemDTOMapByIds(shopId,ArrayUtil.toLongArr(preBuyOrderItemIds));
        for (OrderItemSearchResultDTO resultDTO:orderSearchResultListDTO.getOrderItems()){
          PreBuyOrderItemDTO itemDTO= preBuyOrderItemDTOMap.get(resultDTO.getItemId());
          if(itemDTO!=null){
            resultDTO.setQuotedCount(itemDTO.getQuotedCount());
          }
        }
      }
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      ServiceManager.getService(IImageService.class).addImageToPreBuyOrderItemSearchResultDTO(orderSearchResultListDTO.getOrderItems(),imageSceneList,true);
      Map<String,String> otherDataMap = new HashMap<String, String>();
      otherDataMap.put("allPreBuyOrderCount",String.valueOf(preBuyOrderService.countPreBuyOrderItems(shopId)));
      otherDataMap.put("allQuotedCount",String.valueOf(preBuyOrderService.countQuotedPreBuyOrderItems(shopId)));
      orderSearchResultListDTO.setOtherDataMap(otherDataMap);
      Pager pager = new Pager(Integer.parseInt(orderSearchResultListDTO.getItemNumFound()+""), orderSearchConditionDTO.getStartPageNo(),orderSearchConditionDTO.getMaxRows());
      List<Object> result = new ArrayList<Object>();
      result.add(orderSearchResultListDTO);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=preBuyOrderFilter")
  @ResponseBody
  public Object preBuyOrderFilter(HttpServletRequest request, ProductsRequest productsRequest) {
    try {
      return preBuyOrderService.preBuyOrderFilter(productsRequest.getProductDTOs(),WebUtil.getShopId(request));
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=preBuyOrderFilter");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return JsonUtil.EMPTY_JSON_STRING;
    }
  }

  /**
   *
   * 我的求购详细信息
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showPreBuyOrderById")
  public String showPreBuyOrderById(ModelMap model, HttpServletRequest request, Long preBuyOrderId) throws Exception {
    try {
      if (preBuyOrderId==null)
        throw new Exception("showPreBuyOrderById preBuyOrderId is empty!");
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      PreBuyOrderDTO preBuyOrderDTO = preBuyOrderService.getPreBuyOrderDTOById(shopId,preBuyOrderId);
//      ServiceManager.getService(IImageService.class).addImageToPreBuyOrderFromProduct(preBuyOrderDTO);
//     preBuyOrderDTO.setId(null);
//      preBuyOrderService.savePreBuyOrder(shopId,preBuyOrderDTO);
      model.addAttribute("preBuyOrderDTO", preBuyOrderDTO);
      //图片信息
      List<PreBuyOrderItemDTO> preBuyOrderItemDTOs=new ArrayList<PreBuyOrderItemDTO>();
      preBuyOrderItemDTOs.add(preBuyOrderDTO.getItemDTO());
      List<ImageScene> imageSceneList1 = new ArrayList<ImageScene>();
      imageSceneList1.add(ImageScene.PRODUCT_INFO_IMAGE_BIG);
      ServiceManager.getService(IImageService.class).addImageToPreBuyOrderItemDTO(preBuyOrderItemDTOs,imageSceneList1,true);
    } catch (Exception e) {
      LOG.error("/preBuyOrder.do?method=showPreBuyOrderById");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/preBuyOrderFinish2";
  }
  /**
   *
   *查看求购资讯 详细
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showBuyInformationDetailByPreBuyOrderId")
  public String showBuyInformationDetailByPreBuyOrderId(ModelMap model, HttpServletRequest request, Long preBuyOrderId) throws Exception {
    try {
      if (preBuyOrderId==null)
        throw new Exception("showBuyInformationDetailByPreBuyOrderId preBuyOrderId is empty!");
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      PreBuyOrderDTO preBuyOrderDTO = preBuyOrderService.getSimplePreBuyOrderDTOById(preBuyOrderId);
      if(!ArrayUtils.isEmpty(preBuyOrderDTO.getItemDTOs())){
        List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = null;
        for(PreBuyOrderItemDTO preBuyOrderItemDTO:preBuyOrderDTO.getItemDTOs()){
          quotedPreBuyOrderItemDTOList = preBuyOrderService.getQuotedPreBuyOrderItemDTOsByPreBuyOrderItemId(shopId,preBuyOrderItemDTO.getId());
          if(CollectionUtils.isNotEmpty(quotedPreBuyOrderItemDTOList)){
            preBuyOrderItemDTO.setMyQuotedPreBuyOrderItemDTO(quotedPreBuyOrderItemDTOList.get(0));
          }
        }
      }

      model.addAttribute("preBuyOrderDTO",preBuyOrderDTO);

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(preBuyOrderDTO.getShopId());
      CustomerDTO customerDTO = new CustomerDTO();
      customerDTO.setAreaInfo(configService.getShopAreaInfoByShopDTO(shopDTO));

      CustomerDTO myCustomerDTO = ServiceManager.getService(IUserService.class).getCustomerByCustomerShopIdAndShopId(shopId,preBuyOrderDTO.getShopId());
      RelationMidStatus relationMidStatus = RelationMidStatus.UN_APPLY_RELATED;
      customerDTO.setPartShopDTOInfo(shopDTO, true);
      if(myCustomerDTO!=null){
        relationMidStatus = RelationMidStatus.RELATED;
        customerDTO.setId(myCustomerDTO.getId());
      }else{
        IApplyService applyService = ServiceManager.getService(IApplyService.class);

        Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByInvitedShopIds(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, ApplyService.EXPIRED_TIME, customerDTO.getCustomerShopId());
        if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty() && shopRelationInviteDTOMap.size()>=1) {
          relationMidStatus = RelationMidStatus.APPLY_RELATED;
        }
        //校验对方是否邀请过
        Map<Long, ShopRelationInviteDTO> customerShopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByOriginShopId(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId,null, customerDTO.getCustomerShopId());
        if (customerShopRelationInviteDTOMap != null && !customerShopRelationInviteDTOMap.isEmpty() && customerShopRelationInviteDTOMap.size() >= 1) {
          relationMidStatus = RelationMidStatus.BE_APPLY_RELATED;
          model.addAttribute("inviteId", customerShopRelationInviteDTOMap.values().iterator().next().getId());
        }
      }

      model.addAttribute("relationMidStatus",relationMidStatus);
      model.addAttribute("customerDTO",customerDTO);

      model.addAttribute("allValidPreBuyOrderItemsCount", preBuyOrderService.countValidPreBuyOrderItems(preBuyOrderDTO.getShopId()));

    } catch (Exception e) {
      LOG.error("/preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/preBuyInformationDetail";
  }


  /**
   *
   *查看求购资讯 详细
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showBuyInformationDetailByPreBuyOrderItemId")
  public String showBuyInformationDetailByPreBuyOrderItemId(ModelMap model, HttpServletRequest request, Long preBuyOrderItemId) throws Exception {
    try {
      if (preBuyOrderItemId==null)
        throw new Exception("showBuyInformationDetailByPreBuyOrderItemId preBuyOrderItemId is empty!");
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderService.getPreBuyOrderItemDTOById(preBuyOrderItemId);
      PreBuyOrderDTO preBuyOrderDTO = preBuyOrderService.getSimplePreBuyOrderDTOById(preBuyOrderItemDTO.getPreBuyOrderId());
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = preBuyOrderService.getQuotedPreBuyOrderItemDTOsByPreBuyOrderItemId(shopId,preBuyOrderItemDTO.getId());
      if(CollectionUtils.isNotEmpty(quotedPreBuyOrderItemDTOList)){
        preBuyOrderItemDTO.setMyQuotedPreBuyOrderItemDTO(quotedPreBuyOrderItemDTOList.get(0));
      }
      Map<Long,Long> statMap=ServiceManager.getService(IRecentlyUsedDataService.class).statRecentlyUsedDataCountByDataId(null,RecentlyUsedDataType.VISITED_BUSINESS_CHANCE, preBuyOrderItemDTO.getId());
      preBuyOrderItemDTO.setViewedCount(NumberUtil.doubleValue(statMap.get(preBuyOrderItemDTO.getId()),0));
      List<PreBuyOrderItemDTO> preBuyOrderItemDTOs=new ArrayList<PreBuyOrderItemDTO>();
      preBuyOrderItemDTOs.add(preBuyOrderItemDTO);
      //图片信息
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_BIG);
      ServiceManager.getService(IImageService.class).addImageToPreBuyOrderItemDTO(preBuyOrderItemDTOs,imageSceneList,true);
      model.addAttribute("preBuyOrderDTO",preBuyOrderDTO);
      model.addAttribute("currentPreBuyOrderItemDTO",preBuyOrderItemDTO);

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(preBuyOrderDTO.getShopId());
      CustomerDTO customerDTO = new CustomerDTO();
      customerDTO.setAreaInfo(configService.getShopAreaInfoByShopDTO(shopDTO));

      CustomerDTO myCustomerDTO = ServiceManager.getService(IUserService.class).getCustomerByCustomerShopIdAndShopId(shopId,preBuyOrderDTO.getShopId());
      RelationMidStatus relationMidStatus = RelationMidStatus.UN_APPLY_RELATED;
      customerDTO.setPartShopDTOInfo(shopDTO, true);//显示全部信息不要带*
      if(myCustomerDTO!=null){
        relationMidStatus = RelationMidStatus.RELATED;
        customerDTO.setId(myCustomerDTO.getId());
        customerDTO.setQqArray(myCustomerDTO.getQqArray());
      }else{
        IApplyService applyService = ServiceManager.getService(IApplyService.class);
        Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByInvitedShopIds(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, ApplyService.EXPIRED_TIME, customerDTO.getCustomerShopId());
        if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty() && shopRelationInviteDTOMap.size()>=1) {
          relationMidStatus = RelationMidStatus.APPLY_RELATED;
        }
        //校验对方是否邀请过
        Map<Long, ShopRelationInviteDTO> customerShopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByOriginShopId(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId,null, customerDTO.getCustomerShopId());
        if (customerShopRelationInviteDTOMap != null && !customerShopRelationInviteDTOMap.isEmpty() && customerShopRelationInviteDTOMap.size() >= 1) {
          relationMidStatus = RelationMidStatus.BE_APPLY_RELATED;
          model.addAttribute("inviteId", customerShopRelationInviteDTOMap.values().iterator().next().getId());
        }
      }

      Set<Long> shopIdSet = new HashSet<Long>();
      shopIdSet.add(customerDTO.getCustomerShopId());
      Map<Long,String> stringMap = preciseRecommendService.getSecondCategoryByShopId(shopIdSet);
      if(MapUtils.isNotEmpty(stringMap) && StringUtils.isNotEmpty(stringMap.get(customerDTO.getCustomerShopId()))){
        customerDTO.setBusinessScopeStr(stringMap.get(customerDTO.getCustomerShopId()));
      }
      //主营车型
      List<ShopVehicleBrandModelDTO> bmDTOs= ServiceManager.getService(IProductService.class).getShopVehicleBrandModelByShopId(customerDTO.getCustomerShopId());
      shopDTO.generateShopVehicleBrandModelStr(bmDTOs);
      customerDTO.setVehicleModelContent(shopDTO.getShopVehicleBrandModelStr());
      //本店面的服务
      String serviceCategoryStr = "";
      Map<Long,String> shopServiceCategoryIdNameMap = ServiceManager.getService(IServiceCategoryService.class).getShopServiceCategoryIdNameMap(customerDTO.getCustomerShopId());
      if(shopServiceCategoryIdNameMap != null && shopServiceCategoryIdNameMap.size() > 0) {
        for(Long serviceCategoryId : shopServiceCategoryIdNameMap.keySet()) {
          serviceCategoryStr += shopServiceCategoryIdNameMap.get(serviceCategoryId).toString() + ",";
        }
        customerDTO.setServiceCategoryRelationContent(serviceCategoryStr.substring(0,serviceCategoryStr.length() - 1));
      }
      // 收藏该店铺的supplier数量
      int beFavouredCnt = configService.countBeFavoured(customerDTO.getCustomerShopId());
      customerDTO.setBeStored(beFavouredCnt);
      model.addAttribute("relationMidStatus",relationMidStatus);
      model.addAttribute("customerDTO",customerDTO);
      model.put("usedProductCategoryDTOList", ServiceManager.getService(IProductCategoryService.class).getRecentlyUsedProductCategoryDTOList(shopId,userId));
      model.addAttribute("allValidPreBuyOrderItemsCount", preBuyOrderService.countValidPreBuyOrderItems(preBuyOrderDTO.getShopId()));
      model.addAttribute("pre_shop_id",preBuyOrderDTO.getShopId());
      //该买家其他商机
      PreBuyOrderSearchCondition condition = new PreBuyOrderSearchCondition();
      condition.setShopId(preBuyOrderDTO.getShopId());
      condition.setNonePreBuyOrderId(preBuyOrderDTO.getId());
      Map<String,Object> otherPrebuyOrderInfo = preBuyOrderService.getValidPreBuyOrderInfo(condition);
      List<PreBuyOrderDTO> preBuyOrderDTOList=(List<PreBuyOrderDTO>)otherPrebuyOrderInfo.get("preBuyOrderDTOList");
      if(CollectionUtil.isNotEmpty(preBuyOrderDTOList)){
        List<PreBuyOrderItemDTO> validPreBuyOrderItemDTOs=new ArrayList<PreBuyOrderItemDTO>();
        for (PreBuyOrderDTO orderDTO:preBuyOrderDTOList){
          PreBuyOrderItemDTO itemDTO=orderDTO.getItemDTO();
          if(itemDTO==null){
            continue;
          }
          validPreBuyOrderItemDTOs.add(itemDTO);
        }
        preBuyOrderService.addMyQuotedToPreBuyOrderItemDTO(shopId,validPreBuyOrderItemDTOs.toArray(new PreBuyOrderItemDTO[validPreBuyOrderItemDTOs.size()]));
      }
      model.addAttribute("otherPrebuyOrderInfo",otherPrebuyOrderInfo);
      //除了该买家之外的其他店铺商机
      condition.setNoneShopId(preBuyOrderDTO.getShopId());
      condition.setPageSize(5);
      model.addAttribute("otherShopPrebuyOrderInfo",preBuyOrderService.getOtherShopPreBuyOrders(condition));
    } catch (Exception e) {
      LOG.error("/preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderItemId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/preBuyInformationItemDetail";
  }


  @RequestMapping(params = "method=preBuyInformation")
  public String preBuyInformation(ModelMap modelMap, HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      PreBuyOrderSearchCondition conditionDTO=new PreBuyOrderSearchCondition();
      conditionDTO.setStartTime(DateUtil.getInnerDayTime(DateUtil.getEndTimeOfToday(),-7));
      conditionDTO.setEndTime(DateUtil.getEndTimeOfToday());
      int normalBusinessChanceNum=0;
      int sellWellBusinessChanceNum=0;
      int lackBusinessChanceNum=0;
      List<PreBuyOrderItemDTO> preBuyOrderItemDTOs=preBuyOrderService.getPreBuyOrderItemDetailDTO(conditionDTO);
      if(CollectionUtil.isNotEmpty(preBuyOrderItemDTOs)){
        for(PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOs){
          if(itemDTO==null)continue;
          switch (itemDTO.getBusinessChanceType()){
            case Normal:
              normalBusinessChanceNum++;
              break;
            case SellWell:
              sellWellBusinessChanceNum++;
              break;
            case Lack:
              lackBusinessChanceNum++;
              break;
          }
        }
      }
      modelMap.put("normalBusinessChanceNum",normalBusinessChanceNum);
      modelMap.put("sellWellBusinessChanceNum",sellWellBusinessChanceNum);
      modelMap.put("lackBusinessChanceNum",lackBusinessChanceNum);
      QuotedPreBuyOrderSearchConditionDTO qConditionDTO=new QuotedPreBuyOrderSearchConditionDTO();
      qConditionDTO.setStartTime(DateUtil.getInnerDayTime(DateUtil.getEndTimeOfToday(),-7));
      qConditionDTO.setEndTime(DateUtil.getEndTimeOfToday());
      modelMap.put("quotedPreBuyOrderNum",preBuyOrderService.countQuotedPreBuyOrderSupplier(qConditionDTO));
      modelMap.addAttribute("orderSearchConditionDTO", orderSearchConditionDTO);
      modelMap.put("orderCenterDTO",ServiceManager.getService(IOrderCenterService.class).getOrderCenterStatistics(shopId));
      return "/autoaccessoryonline/preBuyInformationList";
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=preBuyInformation");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error("转向求购资讯页面异常！！！");
      return null;
    }
  }

  @RequestMapping(params = "method=getPreBuyInformationList")
  @ResponseBody
  public Object getPreBuyInformationList(HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO){
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.PRE_BUY_ORDER.toString()});
      orderSearchConditionDTO.setItemTypes(new String[]{ItemTypes.MATERIAL.toString()});
      orderSearchConditionDTO.setPreBuyOrderStatus(OrderSearchConditionDTO.PreBuyOrderStatus.VALID);

      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      orderSearchConditionDTO.setShopKind(shopDTO.getShopKind());
//      orderSearchConditionDTO.setExcludeShopIds(new Long[]{shopId});
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
      if(StringUtils.isBlank(orderSearchConditionDTO.getSearchWord()) && orderSearchConditionDTO.isEmptyOfProductInfo()){
        orderSearchConditionDTO.setSort("order_created_time desc");
      }
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrderItems(orderSearchConditionDTO);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      List<OrderItemSearchResultDTO> resultDTOs= orderSearchResultListDTO.getOrderItems();
      if(CollectionUtil.isNotEmpty(resultDTOs)){
        ServiceManager.getService(IImageService.class).addImageToPreBuyOrderItemSearchResultDTO(orderSearchResultListDTO.getOrderItems(),imageSceneList,true);
        List<Long> preBuyOrderItemIds=new ArrayList<Long>();
        for(OrderItemSearchResultDTO resultDTO :resultDTOs){
          preBuyOrderItemIds.add(resultDTO.getItemId());
          resultDTO.setFuzzyAmountStr(PreBuyOrderItemDTO.genFuzzyAmount(resultDTO.getItemCount()));
        }
        QuotedPreBuyOrderSearchConditionDTO conditionDTO=new QuotedPreBuyOrderSearchConditionDTO();
        conditionDTO.setShopId(shopId);
        conditionDTO.setPreBuyOrderItemIds(ArrayUtil.toLongArr(preBuyOrderItemIds));
        List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOs=preBuyOrderService.getQuotedPreBuyOrderItem(conditionDTO);
        if(CollectionUtil.isNotEmpty(quotedPreBuyOrderItemDTOs)){
          Map<Long,QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOMap=new HashMap<Long, QuotedPreBuyOrderItemDTO>();
          for(QuotedPreBuyOrderItemDTO itemDTO:quotedPreBuyOrderItemDTOs){
            quotedPreBuyOrderItemDTOMap.put(itemDTO.getPreBuyOrderItemId(),itemDTO);
          }
          for(OrderItemSearchResultDTO resultDTO :resultDTOs){
            resultDTO.setMyQuotedPreBuyOrderItemDTO(quotedPreBuyOrderItemDTOMap.get(resultDTO.getItemId()));
          }
        }
      }
      Pager pager = new Pager(Integer.parseInt(orderSearchResultListDTO.getItemNumFound()+""), orderSearchConditionDTO.getStartPageNo(),orderSearchConditionDTO.getMaxRows());
      List<Object> result = new ArrayList<Object>();
      result.add(orderSearchResultListDTO);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=getPreBuyInformationList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(params = "method=quotedPreBuyOrderManage")
  public String quotedPreBuyOrderManage(ModelMap modelMap, HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      modelMap.addAttribute("orderSearchConditionDTO",orderSearchConditionDTO);
      return "/autoaccessoryonline/quotedPreBuyOrderList";
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=quotedPreBuyOrderManage");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error("转向求购管理页面异常！！！");
      return null;
    }
  }


  @RequestMapping(params = "method=getQuotedPreBuyOrderList")
  @ResponseBody
  public Object getQuotedPreBuyOrderList(HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO){
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.QUOTED_PRE_BUY_ORDER.toString()});
      orderSearchConditionDTO.setItemTypes(new String[]{ItemTypes.MATERIAL.toString()});
      orderSearchConditionDTO.setShopId(shopId);
      if(StringUtils.isBlank(orderSearchConditionDTO.getSearchWord()) && orderSearchConditionDTO.isEmptyOfProductInfo()){
        orderSearchConditionDTO.setSort("order_created_time desc");
      }
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());

      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrderItems(orderSearchConditionDTO);
      Map<String,String> otherDataMap = new HashMap<String, String>();
      otherDataMap.put("allOrdersCount", String.valueOf(preBuyOrderService.countOrdersFromQuotedPreBuyOrder(shopId)));
      otherDataMap.put("allQuotedPreBuyOrderCount",String.valueOf(preBuyOrderService.countQuotedPreBuyOrders(shopId)));
      orderSearchResultListDTO.setOtherDataMap(otherDataMap);

      // add by zhuj 通过采购单id查询采购单
      setCustomerOrSupplierShopId(orderSearchResultListDTO);

      Pager pager = new Pager(Integer.parseInt(orderSearchResultListDTO.getItemNumFound()+""), orderSearchConditionDTO.getStartPageNo(),orderSearchConditionDTO.getMaxRows());
      List<Object> result = new ArrayList<Object>();
      result.add(orderSearchResultListDTO);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=getQuotedPreBuyOrderList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  private void setCustomerOrSupplierShopId(OrderSearchResultListDTO orderSearchResultListDTO) {
    Set<Long> quotedPreBuyOrderIds = new HashSet<Long>();
    List<OrderItemSearchResultDTO> orderItemSearchResultDTOs = orderSearchResultListDTO.getOrderItems();
    if (CollectionUtils.isNotEmpty(orderItemSearchResultDTOs)) {
      for (OrderItemSearchResultDTO orderItemSearchResultDTO : orderItemSearchResultDTOs) {
        quotedPreBuyOrderIds.add(orderItemSearchResultDTO.getOrderId());
      }
    }
    List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOs = ServiceManager.getService(IPreBuyOrderService.class).getQuotedPreBuyOrdersByQuotePreBuyOrderIds(quotedPreBuyOrderIds);
    for (OrderItemSearchResultDTO orderItemSearchResultDTO : orderItemSearchResultDTOs) {
      for (QuotedPreBuyOrderDTO quotedPreBuyOrderDTO : quotedPreBuyOrderDTOs) {
        if (orderItemSearchResultDTO.getOrderId().equals(quotedPreBuyOrderDTO.getId()) && !orderItemSearchResultDTO.getOrderId().equals(new Long(0)) && !quotedPreBuyOrderDTO.getId().equals(new Long(0))) {
          orderItemSearchResultDTO.setCustomerOrSupplierShopId(quotedPreBuyOrderDTO.getCustomerShopId());
          orderItemSearchResultDTO.setCustomerOrSupplierShopIdStr(String.valueOf(quotedPreBuyOrderDTO.getCustomerShopId()));
        }
      }
    }
  }

  /**
   *
   * 报价者  根据报价条目  查看 对应整个求购单 所包含自己报价的详细信息
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showBuyInformationDetailByQuotedPreBuyOrderItemId")
  public String showBuyInformationDetailByQuotedPreBuyOrderItemId(ModelMap model, HttpServletRequest request, Long quotedPreBuyOrderItemId) throws Exception {
    try {
      if (quotedPreBuyOrderItemId==null)
        throw new Exception("showBuyInformationDetailByQuotedPreBuyOrderItemId quotedPreBuyOrderItemId is empty!");
      Long shopId=WebUtil.getShopId(request);
      QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO=CollectionUtil.getFirst(preBuyOrderService.getQuotedPreBuyOrderItemDTOsByIds(shopId,quotedPreBuyOrderItemId));
      if(quotedPreBuyOrderItemDTO==null){
        LOG.error("can't get data of QuotedPreBuyOrderItem,quotedPreBuyOrderItemId is {}",quotedPreBuyOrderItemId);
        throw new Exception();
      }
      quotedPreBuyOrderItemDTO.setProductDTOWithOutUnit(ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(quotedPreBuyOrderItemDTO.getProductId(),shopId));
      quotedPreBuyOrderItemDTO.setQuotedPreBuyOrderDTO(preBuyOrderService.getQuotedPreBuyOrderDTO(quotedPreBuyOrderItemDTO.getQuotedPreBuyOrderId()));
      PreBuyOrderItemDTO preBuyOrderItemDTO=preBuyOrderService.getPreBuyOrderItemDTOById(quotedPreBuyOrderItemDTO.getPreBuyOrderItemId());
      if(preBuyOrderItemDTO==null){
        LOG.error("can't get data of PreBuyOrderItem,preBuyOrderItemId is {}", quotedPreBuyOrderItemDTO.getPreBuyOrderId());
        throw new Exception();
      }
      PreBuyOrderDTO preBuyOrderDTO=preBuyOrderService.getPreBuyOrderDTOById(shopId,preBuyOrderItemDTO.getPreBuyOrderId());
      preBuyOrderItemDTO.setPreBuyOrderDTO(preBuyOrderDTO);
      List<PreBuyOrderItemDTO> preBuyOrderItemDTOs=new ArrayList<PreBuyOrderItemDTO>();
      preBuyOrderItemDTOs.add(preBuyOrderItemDTO);
      //图片信息
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_BIG);
      ServiceManager.getService(IImageService.class).addImageToPreBuyOrderItemDTO(preBuyOrderItemDTOs,imageSceneList,true);
      Map<Long,Long> statMap=ServiceManager.getService(IRecentlyUsedDataService.class).statRecentlyUsedDataCountByDataId(null,RecentlyUsedDataType.VISITED_BUSINESS_CHANCE, preBuyOrderItemDTO.getId());
      preBuyOrderItemDTO.setViewedCount(NumberUtil.doubleValue(statMap.get(preBuyOrderItemDTO.getId()),0));
      model.put("preBuyOrderItemDTO",preBuyOrderItemDTO);
      model.put("quotedPreBuyOrderItemDTO",quotedPreBuyOrderItemDTO);
      //该买家其他商机
      PreBuyOrderSearchCondition condition = new PreBuyOrderSearchCondition();
      condition.setShopId(preBuyOrderDTO.getShopId());
      condition.setNonePreBuyOrderId(preBuyOrderDTO.getId());
      Map<String,Object> otherPreBuyOrderInfo = preBuyOrderService.getValidPreBuyOrderInfo(condition);
      List<PreBuyOrderDTO> preBuyOrderDTOList=(List<PreBuyOrderDTO>)otherPreBuyOrderInfo.get("preBuyOrderDTOList");
      List<Long> preBuyOrderItemIds=new ArrayList<Long>();
      List<PreBuyOrderItemDTO> validPreBuyOrderItemDTOs=new ArrayList<PreBuyOrderItemDTO>();
      if(CollectionUtil.isNotEmpty(preBuyOrderDTOList)){
        for (PreBuyOrderDTO orderDTO:preBuyOrderDTOList){
          PreBuyOrderItemDTO itemDTO=orderDTO.getItemDTO();
          if(itemDTO==null){
            continue;
          }
          preBuyOrderItemIds.add(itemDTO.getId());
          validPreBuyOrderItemDTOs.add(itemDTO);
        }
      }
      preBuyOrderService.addMyQuotedToPreBuyOrderItemDTO(shopId,validPreBuyOrderItemDTOs.toArray(new PreBuyOrderItemDTO[validPreBuyOrderItemDTOs.size()]));
      model.addAttribute("otherPreBuyOrderInfo",otherPreBuyOrderInfo);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/quotedPreBuyInformationDetail";
  }
  /**
   *
   *求购者查看单个求购商品 指定的报价详细
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showSupplierQuotedItemDetailByItemId")
  public String showSupplierQuotedItemDetailByItemId(ModelMap model, HttpServletRequest request, Long quotedPreBuyOrderItemId) throws Exception {
    try {
      if (quotedPreBuyOrderItemId==null)
        throw new Exception("showSupplierQuotedItemDetailByItemId quotedPreBuyOrderItemId is empty!");
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");

      QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO = preBuyOrderService.getQuotedPreBuyOrderItemDTOById(quotedPreBuyOrderItemId);

      PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderService.getPreBuyOrderItemDTOById(quotedPreBuyOrderItemDTO.getPreBuyOrderItemId());

      IProductService productService = ServiceManager.getService(IProductService.class);
      IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);

      ProductDTO productDTO = productService.getProductByProductLocalInfoId(quotedPreBuyOrderItemDTO.getProductId(),quotedPreBuyOrderItemDTO.getShopId());
      productDTO.setPromotionsDTOs(promotionsService.getPromotionsDTODetailByProductLocalInfoId(quotedPreBuyOrderItemDTO.getShopId(),quotedPreBuyOrderItemDTO.getProductId()));

      quotedPreBuyOrderItemDTO.setProductDTOWithOutUnit(productDTO);
      quotedPreBuyOrderItemDTO.setProductDTO(productDTO);
      model.addAttribute("quotedPreBuyOrderItemDTO",quotedPreBuyOrderItemDTO);
      model.addAttribute("preBuyOrderItemDTO",preBuyOrderItemDTO);

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(quotedPreBuyOrderItemDTO.getShopId());
      SupplierDTO supplierDTO = new SupplierDTO();
      supplierDTO.setAreaInfo(configService.getShopAreaInfoByShopDTO(shopDTO));

      SupplierDTO mySupplierDTO = ServiceManager.getService(IUserService.class).getSupplierDTOBySupplierShopIdAndShopId(shopId,quotedPreBuyOrderItemDTO.getShopId());
      RelationMidStatus relationMidStatus = RelationMidStatus.UN_APPLY_RELATED;
      supplierDTO.setPartShopDTOInfo(shopDTO,true);   //显示全部信息不要带*
      if(mySupplierDTO!=null){
        relationMidStatus = RelationMidStatus.RELATED;
        supplierDTO.setId(mySupplierDTO.getId());
        supplierDTO.setQqArray(mySupplierDTO.getQqArray());
      }else{
        IApplyService applyService = ServiceManager.getService(IApplyService.class);

        Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByInvitedShopIds(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, ApplyService.EXPIRED_TIME, supplierDTO.getSupplierShopId());
        if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty() && shopRelationInviteDTOMap.size()>=1) {
          relationMidStatus = RelationMidStatus.APPLY_RELATED;
        }
        //校验对方是否邀请过
        Map<Long, ShopRelationInviteDTO> supplierShopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByOriginShopId(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, null, supplierDTO.getSupplierShopId());
        if (supplierShopRelationInviteDTOMap != null && !supplierShopRelationInviteDTOMap.isEmpty() && supplierShopRelationInviteDTOMap.size() >= 1) {
          relationMidStatus = RelationMidStatus.BE_APPLY_RELATED;
          model.addAttribute("inviteId",supplierShopRelationInviteDTOMap.values().iterator().next().getId());
        }
      }

      Set<Long> shopIdSet = new HashSet<Long>();
      shopIdSet.add(supplierDTO.getSupplierShopId());
      Map<Long,String> stringMap = preciseRecommendService.getSecondCategoryByShopId(shopIdSet);
      if(MapUtils.isNotEmpty(stringMap) && StringUtils.isNotEmpty(stringMap.get(supplierDTO.getSupplierShopId()))){
        supplierDTO.setBusinessScope(stringMap.get(supplierDTO.getSupplierShopId()));
      }

      model.addAttribute("relationMidStatus",relationMidStatus);
      model.addAttribute("supplierDTO",supplierDTO);

      model.addAttribute("currentPreBuyOrderQuotedCount", preBuyOrderService.countQuotedPreBuyOrdersByPreBuyOrderId(supplierDTO.getSupplierShopId(),quotedPreBuyOrderItemDTO.getPreBuyOrderId()));

    } catch (Exception e) {
      LOG.error("/preBuyOrder.do?method=showSupplierQuotedItemDetailByItemId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/quotedPreBuyOrderItemDetail";
  }

  @RequestMapping(params = "method=showSupplierOtherQuotedItems")
  public String showSupplierOtherQuotedItems(ModelMap model, HttpServletRequest request,Long quotedPreBuyOrderItemId){
    try{
      QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO=preBuyOrderService.getQuotedPreBuyOrderItemDTOById(quotedPreBuyOrderItemId);
      if(quotedPreBuyOrderItemDTO==null){
        LOG.error("can't get data of QuotedPreBuyOrderItem,quotedPreBuyOrderItemId is {}",quotedPreBuyOrderItemId);
        return REDIRECT_PRE_BUY_ORDER_MANAGER;
      }
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(quotedPreBuyOrderItemDTO.getShopId());
      model.put("shopDTO",shopDTO);
      ContactDTO[] contactDTOs=shopDTO.getContacts();
      if(ArrayUtil.isNotEmpty(contactDTOs)){
        model.put("contactsJson",JsonUtil.listToJson(Arrays.asList(contactDTOs)));
      }
      model.put("quotedPreBuyOrderItemId",quotedPreBuyOrderItemId);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    return "/autoaccessoryonline/supplierOtherQuotedItems";
  }

  @RequestMapping(params = "method=getSupplierOtherQuotedItems")
  @ResponseBody
  public Object getSupplierOtherQuotedItems(ModelMap model, HttpServletRequest request,
                                            Long quotedPreBuyOrderItemId,int startPageNo,int maxRows){
    Long shopId=WebUtil.getShopId(request);
    try{
      if(quotedPreBuyOrderItemId==null){
        throw new BcgogoException("illegal parameter.");
      }
      QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO=preBuyOrderService.getQuotedPreBuyOrderItemDTOById(quotedPreBuyOrderItemId);
      if(quotedPreBuyOrderItemDTO==null){
        LOG.error("can't get data of QuotedPreBuyOrderItem,quotedPreBuyOrderItemId is {}",quotedPreBuyOrderItemId);
        return null;
      }
      PreBuyOrderItemDTO preBuyOrderItemDTO= preBuyOrderService.getPreBuyOrderItemDTOById(quotedPreBuyOrderItemDTO.getPreBuyOrderItemId());
      if(preBuyOrderItemDTO==null){
        LOG.error("can't get data of PreBuyOrderItem,preBuyOrderItemId is {}",quotedPreBuyOrderItemDTO.getPreBuyOrderId());
        return null;
      }
      Pager pager = new Pager(Integer.valueOf(preBuyOrderService.countSupplierOtherQuotedItems(quotedPreBuyOrderItemDTO.getShopId(),preBuyOrderItemDTO.getShopId(),quotedPreBuyOrderItemId)),startPageNo,maxRows);
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOs = preBuyOrderService.getSupplierOtherQuotedItems(quotedPreBuyOrderItemDTO.getShopId(),preBuyOrderItemDTO.getShopId(),quotedPreBuyOrderItemId,pager);
      List<Long> preBuyOrderItemIds=new ArrayList<Long>();
      List<Long> productIds=new ArrayList<Long>();
      for(QuotedPreBuyOrderItemDTO itemDTO:quotedPreBuyOrderItemDTOs){
        preBuyOrderItemIds.add(itemDTO.getPreBuyOrderItemId());
        productIds.add(itemDTO.getProductId());
      }
      Map<Long,ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(quotedPreBuyOrderItemDTO.getShopId(),ArrayUtil.toLongArr(productIds));
      if(productDTOMap!=null&&productDTOMap.keySet().size()>0){
        for(QuotedPreBuyOrderItemDTO itemDTO:quotedPreBuyOrderItemDTOs){
          itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));
          itemDTO.setProductDTO(productDTOMap.get(itemDTO.getProductId()));
          itemDTO.setProductInfo(itemDTO.getProductInfo());
        }
      }
      Map<Long,PreBuyOrderItemDTO> preBuyOrderItemDTOMap = preBuyOrderService.getPreBuyOrderItemDTOMapByIds(shopId,ArrayUtil.toLongArr(preBuyOrderItemIds));
      for(QuotedPreBuyOrderItemDTO itemDTO:quotedPreBuyOrderItemDTOs){
        itemDTO.setPreBuyOrderItemDTO(preBuyOrderItemDTOMap.get(itemDTO.getPreBuyOrderItemId()));
      }
      PagingListResult<QuotedPreBuyOrderItemDTO> result = new PagingListResult<QuotedPreBuyOrderItemDTO>();
      result.setResults(quotedPreBuyOrderItemDTOs);
      result.setPager(pager);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  /**
   *只显示 指定求购单  有  指定 供应商报价的  求购信息和报价信息
   *
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=showSupplierQuotedDetailByPreBuyOrderIdAndSupplierShopId")
  public String showSupplierQuotedDetailByPreBuyOrderIdAndSupplierShopId(ModelMap model, HttpServletRequest request, Long preBuyOrderId,Long supplierShopId) throws Exception {
    try {
      if (preBuyOrderId==null)
        throw new Exception("showSupplierQuotedDetailByPreBuyOrderIdAndSupplierShopId preBuyOrderId is empty!");
      if (supplierShopId==null)
        throw new Exception("showSupplierQuotedDetailByPreBuyOrderIdAndSupplierShopId supplierShopId is empty!");
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");


      PreBuyOrderDTO preBuyOrderDTO = preBuyOrderService.getSimplePreBuyOrderDTOById(preBuyOrderId);

      if(!ArrayUtils.isEmpty(preBuyOrderDTO.getItemDTOs())){

        IProductService productService = ServiceManager.getService(IProductService.class);
        IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);

        List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
        CollectionUtils.addAll(preBuyOrderItemDTOList,preBuyOrderDTO.getItemDTOs());
        Iterator<PreBuyOrderItemDTO> iterator = preBuyOrderItemDTOList.iterator();
        List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = null;
        QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO = null;
        while(iterator.hasNext()){
          PreBuyOrderItemDTO preBuyOrderItemDTO = iterator.next();
          quotedPreBuyOrderItemDTOList = preBuyOrderService.getQuotedPreBuyOrderItemDTOsByPreBuyOrderItemId(supplierShopId,preBuyOrderItemDTO.getId());
          if(CollectionUtils.isNotEmpty(quotedPreBuyOrderItemDTOList)){
            quotedPreBuyOrderItemDTO = quotedPreBuyOrderItemDTOList.get(0);

            ProductDTO productDTO = productService.getProductByProductLocalInfoId(quotedPreBuyOrderItemDTO.getProductId(),quotedPreBuyOrderItemDTO.getShopId());
            productDTO.setPromotionsDTOs(promotionsService.getPromotionsDTODetailByProductLocalInfoId(quotedPreBuyOrderItemDTO.getShopId(),quotedPreBuyOrderItemDTO.getProductId()));
            quotedPreBuyOrderItemDTO.setProductDTOWithOutUnit(productDTO);
            quotedPreBuyOrderItemDTO.setProductDTO(productDTO);
            preBuyOrderItemDTO.setMyQuotedPreBuyOrderItemDTO(quotedPreBuyOrderItemDTO);
          }else{
            iterator.remove();
          }
        }
        preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOList.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOList.size()]));
      }
      model.addAttribute("preBuyOrderDTO",preBuyOrderDTO);

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(supplierShopId);
      SupplierDTO supplierDTO = new SupplierDTO();
      supplierDTO.setAreaInfo(configService.getShopAreaInfoByShopDTO(shopDTO));

      SupplierDTO mySupplierDTO = ServiceManager.getService(IUserService.class).getSupplierDTOBySupplierShopIdAndShopId(shopId,supplierShopId);
      RelationMidStatus relationMidStatus = RelationMidStatus.UN_APPLY_RELATED;
      supplierDTO.setPartShopDTOInfo(shopDTO,true);    //显示全部信息不要带*
      if(mySupplierDTO!=null){
        relationMidStatus = RelationMidStatus.RELATED;
        supplierDTO.setId(mySupplierDTO.getId());
      }else{
        IApplyService applyService = ServiceManager.getService(IApplyService.class);

        Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByInvitedShopIds(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, ApplyService.EXPIRED_TIME, supplierDTO.getSupplierShopId());
        if (shopRelationInviteDTOMap != null && !shopRelationInviteDTOMap.isEmpty() && shopRelationInviteDTOMap.size()>=1) {
          relationMidStatus = RelationMidStatus.APPLY_RELATED;
        }
        //校验对方是否邀请过
        Map<Long, ShopRelationInviteDTO> supplierShopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByOriginShopId(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId,null, supplierDTO.getSupplierShopId());
        if (supplierShopRelationInviteDTOMap != null && !supplierShopRelationInviteDTOMap.isEmpty() && supplierShopRelationInviteDTOMap.size() >= 1) {
          relationMidStatus = RelationMidStatus.BE_APPLY_RELATED;
          model.addAttribute("inviteId",supplierShopRelationInviteDTOMap.values().iterator().next().getId());
        }
      }

      model.addAttribute("relationMidStatus",relationMidStatus);
      model.addAttribute("supplierDTO",supplierDTO);

    } catch (Exception e) {
      LOG.error("/preBuyOrder.do?method=showSupplierQuotedDetailByPreBuyOrderIdAndSupplierShopId");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/moreQuotedPreBuyOrderItemDetail";
  }


  @RequestMapping(params = "method=quotedSelectProduct")
  @ResponseBody
  public Object quotedSelectProduct(HttpServletRequest request,SearchConditionDTO searchConditionDTO) {
    Long shopId=WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      searchConditionDTO.setShopId(shopId);
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      if(StringUtils.isBlank(searchConditionDTO.getSearchWord()) && searchConditionDTO.isEmptyOfProductInfo()){
        searchConditionDTO.setSort("last_in_sales_time desc");//默认上架时间排序
      }
      IProductCategoryService productCategoryService=ServiceManager.getService(IProductCategoryService.class);
      List<Object> result = new ArrayList<Object>();
//      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      searchConditionDTO.setIncludeBasic(false);
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithStdQuery(searchConditionDTO);
      if(CollectionUtil.isNotEmpty(productSearchResultListDTO.getProducts())){
        for(ProductDTO productDTO:productSearchResultListDTO.getProducts()){
          ProductCategoryRelationDTO productCategoryRelationDTO = CollectionUtil.getFirst(productCategoryService.productCategoryRelationDTOQuery(shopId, productDTO.getProductLocalInfoId()));
          if(productCategoryRelationDTO!=null){
            ProductCategoryDTO productCategoryDTO = CollectionUtil.getFirst(productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(Arrays.asList(new Long[]{productCategoryRelationDTO.getProductCategoryId()}))));
            productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
            productDTO.setProductCategoryDTO(productCategoryDTO);
          }
        }
      }
      Pager pager = new Pager(Integer.valueOf(productSearchResultListDTO.getNumFound() + ""), searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      result.add(productSearchResultListDTO);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do");
      LOG.debug("method=quotedSelectProduct");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  public void prepareForSaveQuotedPreBuyOrder(QuotedPreBuyOrderItemDTO itemDTO){
    String unit=itemDTO.getUnit();
    itemDTO.setSellUnit(unit);
    if(StringUtil.isEmpty(itemDTO.getStorageUnit())){
      itemDTO.setStorageUnit(unit);
      itemDTO.setRate(1l);
    }
  }

  private void saveProductCategoryAndRelation(Long shopId,Long userId,QuotedPreBuyOrderItemDTO itemDTO) throws Exception {
    if(itemDTO.getProductId()==null||StringUtil.isEmpty(itemDTO.getProductCategoryName())){
      return;
    }
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    ProductDTO productDTO=new ProductDTO();
    productDTO.setProductLocalInfoId(itemDTO.getProductId());
    productDTO.setProductCategoryId(itemDTO.getProductCategoryId());
    productDTO.setProductCategoryName(itemDTO.getProductCategoryName());
    txnService.saveProductCategoryAndRelation(shopId,userId,productDTO);
  }

  @RequestMapping(params = "method=saveQuotedPreBuyOrder")
  @ResponseBody
  public Object saveQuotedPreBuyOrder(HttpServletRequest request,QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO) {
    Long shopId=WebUtil.getShopId(request);
    Long userId=WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      if (quotedPreBuyOrderItemDTO == null) throw new Exception("quotedPreBuyOrderItemDTO id is null!");
//      if (quotedPreBuyOrderItemDTO.getPreBuyOrderId() == null) throw new Exception("preBuyOrderId id is null!");
      if (quotedPreBuyOrderItemDTO.getPreBuyOrderItemId() == null) throw new Exception("preBuyOrderItemId id is null!");
     PreBuyOrderItemDTO preBuyOrderItemDTO=preBuyOrderService.getPreBuyOrderItemDTOById(quotedPreBuyOrderItemDTO.getPreBuyOrderItemId());
      if(preBuyOrderItemDTO==null){
          throw new Exception("preBuyOrderItemDTO don't exist");
      }
      ITxnService txnService=ServiceManager.getService(ITxnService.class);
      Result result=new Result();
      prepareForSaveQuotedPreBuyOrder(quotedPreBuyOrderItemDTO);
      Long productLocalInfoId=quotedPreBuyOrderItemDTO.getProductId();
      //判断是否新商品
      if(productLocalInfoId==null){
        ProductDTO productDTO=new ProductDTO(shopId,quotedPreBuyOrderItemDTO);
        result= txnService.validateSaveNewProduct(productDTO);
        if(!result.isSuccess()){
          return result;
        }
        productLocalInfoId= CollectionUtil.getFirst(txnService.batchSaveProduct(shopId, null, new ProductDTO[]{productDTO}));
        quotedPreBuyOrderItemDTO.setProductId(productLocalInfoId);
      }
      //判断是否已经上架
      IProductService productService=ServiceManager.getService(IProductService.class);
      ProductLocalInfoDTO productLocalInfoDTO=productService.getProductLocalInfoById(productLocalInfoId,shopId);
      if(productLocalInfoDTO==null){
        return result.LogErrorMsg("商品不存在或者已经删除。");
      }
      saveProductCategoryAndRelation(shopId,userId,quotedPreBuyOrderItemDTO);
      if(!ProductStatus.InSales.equals(productLocalInfoDTO.getSalesStatus())){
        productLocalInfoDTO.setInSalesPrice(quotedPreBuyOrderItemDTO.getPrice());
        productLocalInfoDTO.setInSalesAmount(quotedPreBuyOrderItemDTO.getInSalesAmount());
        productService.updateProductLocalInfo(productLocalInfoDTO);
        productService.updateProductSalesStatus(shopId, ProductStatus.InSales, productLocalInfoId);
      }
      //生成报价
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      QuotedPreBuyOrderDTO quotedPreBuyOrderDTO = new QuotedPreBuyOrderDTO();
      quotedPreBuyOrderDTO.setUserId(userId);
      quotedPreBuyOrderDTO.setShopId(shopId);
      quotedPreBuyOrderDTO.setEditDate(System.currentTimeMillis());
      quotedPreBuyOrderDTO.setEditor(WebUtil.getUserName(request));
      quotedPreBuyOrderDTO.setEditorId(userId);
      quotedPreBuyOrderDTO.setVestDate(System.currentTimeMillis());
      quotedPreBuyOrderDTO.setPreBuyOrderId(preBuyOrderItemDTO.getPreBuyOrderId());
      PreBuyOrderDTO preBuyOrderDTO = preBuyOrderService.getSimplePreBuyOrderDTOById(preBuyOrderItemDTO.getPreBuyOrderId());
      quotedPreBuyOrderDTO.setCustomerShopId(preBuyOrderDTO.getShopId());
      ShopDTO customerShopDTO = configService.getShopById(preBuyOrderDTO.getShopId());
      quotedPreBuyOrderDTO.setCustomerShopName(customerShopDTO.getName());
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = new ArrayList<QuotedPreBuyOrderItemDTO>();
      quotedPreBuyOrderItemDTOList.add(quotedPreBuyOrderItemDTO);
      quotedPreBuyOrderDTO.setItemDTOs(quotedPreBuyOrderItemDTOList.toArray(new QuotedPreBuyOrderItemDTO[quotedPreBuyOrderItemDTOList.size()]));
      preBuyOrderService.saveOrUpdateQuotedPreBuyOrder(shopId,quotedPreBuyOrderDTO);
      //推送消息
      ShopDTO shopDTO = configService.getShopById(shopId);
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      PushMessageType pushMessageType = null;
      if(BusinessChanceType.Normal.equals(preBuyOrderDTO.getBusinessChanceType())){
        pushMessageType = PushMessageType.QUOTED_BUYING_INFORMATION;
      }else{
        pushMessageType = PushMessageType.RECOMMEND_ACCESSORY_BY_QUOTED;
      }
      for(QuotedPreBuyOrderItemDTO dbQuotedPreBuyOrderItemDTO : quotedPreBuyOrderDTO.getItemDTOs()){
        pushMessageService.createPushMessageDTOByQuotedPreBuyOrderItemDTO(shopDTO,preBuyOrderDTO.getEndDate(),dbQuotedPreBuyOrderItemDTO,quotedPreBuyOrderDTO,pushMessageType);
      }

      SolrHelper.doProductReindex(shopId, productLocalInfoId);
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(quotedPreBuyOrderDTO,OrderTypes.QUOTED_PRE_BUY_ORDER);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      bcgogoEventPublisher = new BcgogoEventPublisher();
      bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(preBuyOrderDTO,OrderTypes.PRE_BUY_ORDER);
      bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);

      //求购报价的时候，供应商自动收藏客户店铺
      IShopRelationService shopRelationService = ServiceManager.getService(IShopRelationService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      CustomerDTO customerDTO = userService.getCustomerByCustomerShopIdAndShopId(shopId, customerShopDTO.getId());
      if (customerDTO == null) {
        customerDTO =  shopRelationService.collectCustomerShop(shopDTO,customerShopDTO);
        ICustomerOrSupplierSolrWriteService supplierSolrWriteService = ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
        if(customerDTO != null && customerDTO.getId() != null){
          supplierSolrWriteService.reindexCustomerByCustomerId(customerDTO.getId());
        }
      }
      if(quotedPreBuyOrderDTO.getId()!=null && !ArrayUtils.isEmpty(quotedPreBuyOrderDTO.getItemDTOs())){
        return new Result(true,quotedPreBuyOrderDTO.getItemDTOs()[0]);
      }else{
        return new Result(false);
      }

    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=saveQuotedPreBuyOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getQuotedPreBuyOrders")
  @ResponseBody
  public Object getQuotedPreBuyOrders(HttpServletRequest request, Long preBuyOrderItemId, int startPageNo, int pageSize) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if(shopId == null) throw new Exception("shop id is null!");
      PagingListResult<QuotedPreBuyOrderItemDTO> result = new PagingListResult<QuotedPreBuyOrderItemDTO>();
      if(preBuyOrderItemId == null) throw new Exception("preBuyOrderItemId is null!");
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = preBuyOrderService.getQuotedPreBuyOrderItemDTOsByPager(preBuyOrderItemId, (startPageNo - 1)*pageSize, pageSize);
      result.setResults(quotedPreBuyOrderItemDTOList);
      PreBuyOrderItemDTO preBuyOrderItemDTO = preBuyOrderService.getPreBuyOrderItemDTOById(preBuyOrderItemId);
      Long total = preBuyOrderService.countQuotedPreBuyOrdersByPreBuyOrderId(null, preBuyOrderItemDTO.getPreBuyOrderId());
      Pager pager = new Pager(NumberUtil.toInteger(total.toString()), startPageNo,pageSize);
      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/preBuyOrder.do?method=getQuotedPreBuyOrders");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }

  }

  @RequestMapping(params = "method=getOtherShopPreBuyOrders")
  @ResponseBody
  public Object getOtherShopPreBuyOrders(HttpServletRequest request,PreBuyOrderSearchCondition condition) {
    try {
      PagingListResult<PreBuyOrderDTO> result = new PagingListResult<PreBuyOrderDTO>();
      condition.setShopId(WebUtil.getShopId(request));
      List<PreBuyOrderDTO> otherShopPreBuyOrders = preBuyOrderService.getOtherShopPreBuyOrders(condition);
      result.setResults(otherShopPreBuyOrders);
      Long total = preBuyOrderService.countOtherShopPreBuyOrders(WebUtil.getShopId(request),condition.getNoneShopId());
      Pager pager = new Pager(NumberUtil.toInteger(total.toString()), condition.getStartPageNo(),condition.getPageSize());
      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return null;
  }

}
