package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.*;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.config.service.RecentlyUsedDataService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.autoaccessoryonline.SupplyDemandConstant;
import com.bcgogo.constant.crm.productCategory.ProductCategoryConstant;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.productManage.PromotionSearchCondition;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.dto.PreBuyOrderSearchCondition;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.recommend.ShopProductMatchResultDTO;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 供求中心首页
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-06-27
 * Time: 下午5:33
 * 订单中心
 */
@Controller
@RequestMapping("/supplyDemand.do")
public class SupplyDemandController {
  private static final Logger LOG = LoggerFactory.getLogger(SupplyDemandController.class);

 //查询根据求购为我推荐
 @RequestMapping(params = "method=getRecommendProductsFromNormal")
 @ResponseBody
 public Object getRecommendProductsFromNormal(HttpServletRequest request, ModelMap modelMap) throws Exception {
   Long shopId=WebUtil.getShopId(request);
   try{
     ProductSearchCondition condition=new ProductSearchCondition();
     condition.setShopId(shopId);
     condition.setLimit(SupplyDemandConstant.PRODUCTS_FROM_NORMAL_NUM);
     condition.setProductRecommendType(ProductRecommendType.FromNormalPreBuyOrder);
     List<ProductDTO> productsFromNormal=ServiceManager.getService(IPreciseRecommendService.class).getRecommendProductDetailDTOs(condition);
     if(CollectionUtil.isNotEmpty(productsFromNormal)){
       List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
       imageSceneList.add(ImageScene.PRODUCT_RECOMMEND_LIST_IMAGE_SMALL);
       ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList, true, productsFromNormal.toArray(new ProductDTO[productsFromNormal.size()]));
       ServiceManager.getService(IPromotionsService.class).addPromotionInfoToProductDTO(productsFromNormal.toArray(new ProductDTO[productsFromNormal.size()]));
     }
     return productsFromNormal;
   }catch (Exception e){
     LOG.error(e.getMessage(),e);
     return null;
   }
 }

  @RequestMapping(params = "method=toSupplyDemand")
  public String toSupplyDemand(HttpServletRequest request, ModelMap modelMap) throws Exception {
    try {
      Long shopVersionId = WebUtil.getShopVersionId(request);
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
      modelMap.put("orderCenterDTO",orderCenterService.getOrderCenterStatistics(shopId));

      List<ProductCategoryDTO> productCategoryDTOList = ProductCategoryCache.getProductCategoryDTOListByType(ProductCategoryType.THIRD_CATEGORY);
      ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
      productCategoryDTO.setCategoryType(ProductCategoryType.SECOND_CATEGORY);
      productCategoryDTO.setParentId(-1l);
      productCategoryDTO.setFirstCategoryId(ShopConstant.BC_ADMIN_SHOP_PRODUCT_CATEGORY_OTHER_QUERY);
      productCategoryDTO.setFirstCategoryName(ProductCategoryConstant.CUSTOM_FIRST_CATEGORY_NAME);
      productCategoryDTO.setSecondCategoryId(ShopConstant.BC_ADMIN_SHOP_PRODUCT_CATEGORY_OTHER_QUERY);
      productCategoryDTO.setSecondCategoryName(ProductCategoryConstant.CUSTOM_FIRST_CATEGORY_NAME);
      productCategoryDTOList.add(productCategoryDTO);
      modelMap.addAttribute("thirdProductCategoryDTOsJson", JsonUtil.listToJson(productCategoryDTOList));
      modelMap.put("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));

      IImageService imageService=ServiceManager.getService(IImageService.class);
      IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
      IProductService productService=ServiceManager.getService(IProductService.class);
      IPreBuyOrderService preBuyOrderService=ServiceManager.getService(IPreBuyOrderService.class);
      if (ConfigUtils.isWholesalerVersion(shopVersionId)){
        //精品配件--关联供应商商品
        Set<Long> wholesalerShopIds = ServiceManager.getService(IConfigService.class).getRelationWholesalerShopIds(shopId, RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST);
        if(CollectionUtil.isNotEmpty(wholesalerShopIds)){
          ProductSearchCondition conditionDTO=new ProductSearchCondition();
          conditionDTO.setLimit(SupplyDemandConstant.WHOLE_SALER_PRODUCTDTOS);
          conditionDTO.setWholesalerShopIds(ArrayUtil.toLongArr(wholesalerShopIds));
          conditionDTO.setSalesStatus(ProductStatus.InSales);
          List<ProductDTO> wholesalerProductDTOs= productService.getProductInfo(conditionDTO);
          if(CollectionUtil.isNotEmpty(wholesalerProductDTOs)){
            List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
            imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
            imageService.addImageInfoToProductDTO(imageSceneList,true,wholesalerProductDTOs.toArray(new ProductDTO[wholesalerProductDTOs.size()]));
          }
          modelMap.put("wholesalerProductDTOs", wholesalerProductDTOs);
        }
        //查询最新求购
        modelMap.put("latestPreBuyOrderItems",preBuyOrderService.getLatestPreBuyOrderItemDTO(shopId,0,SupplyDemandConstant.LATEST_PRE_BUY_ORDER_ITEMS));
        //统计信息
        modelMap.put("inSalesProductNum",productService.countProductInSales(shopId));

        ProductSearchCondition conditionDTO=new ProductSearchCondition();
        conditionDTO.setSalesStatus(ProductStatus.InSales);
        conditionDTO.setShopId(shopId);
        List<ProductDTO> allInSalesProductDTOs= productService.getProductInfo(conditionDTO);
        List<Long> allInSalesProductIds=new ArrayList<Long>();
        if(CollectionUtil.isNotEmpty(allInSalesProductDTOs)){
          for(ProductDTO productDTO:allInSalesProductDTOs){
            allInSalesProductIds.add(productDTO.getProductLocalInfoId());
          }
        }

        modelMap.put("viewedProductNum", ServiceManager.getService(IRecentlyUsedDataService.class).statAllRecentlyUsedDataCountByDataId(RecentlyUsedDataType.VISITED_PRODUCT, ArrayUtil.toLongArr(allInSalesProductIds)));
        modelMap.addAttribute("ordersFromQuotedPreBuyOrderCount", ServiceManager.getService(IPreBuyOrderService.class).countOrdersFromQuotedPreBuyOrder(shopId));
        return "autoaccessoryonline/supplyDemandWholesalerIndex";
      } else {
        IPreciseRecommendService recommendService=ServiceManager.getService(IPreciseRecommendService.class);
        IRecentlyUsedDataService recentlyUsedDataService=ServiceManager.getService(IRecentlyUsedDataService.class);
        //查询您求购的商品
        PreBuyOrderSearchCondition preBuyOrderSearchCondition=new PreBuyOrderSearchCondition();
        preBuyOrderSearchCondition.setShopId(shopId);
        preBuyOrderSearchCondition.setValid(true);
        preBuyOrderSearchCondition.setSort(new Sort("creationDate","desc"));
        preBuyOrderSearchCondition.setBusinessChanceType(BusinessChanceType.Normal);
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOs=preBuyOrderService.getPreBuyOrderItemDetailDTO(preBuyOrderSearchCondition);
        if(CollectionUtil.isNotEmpty(preBuyOrderItemDTOs)){
          for (PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOs){
            itemDTO.setProductNameBrand(StringUtil.truncValue(itemDTO.getProductName())+StringUtil.truncValue(itemDTO.getBrand()));
            Map<Long,Long> statMap=recentlyUsedDataService.statRecentlyUsedDataCountByDataId(null,RecentlyUsedDataType.VISITED_BUSINESS_CHANCE,itemDTO.getId());
            itemDTO.setViewedCount(NumberUtil.doubleValue(statMap.get(itemDTO.getId()),0));
          }
        }
        modelMap.put("preBuyOrderItems",preBuyOrderItemDTOs);
       //查询推荐的精品配件
        ProductSearchCondition condition=new ProductSearchCondition();
        condition.setShopId(shopId);
        condition.setProductRecommendType(ProductRecommendType.FromOther);
        condition.setLimit(SupplyDemandConstant.PRODUCTS_FROM_OTHER_NUM);
        List<ProductDTO> productsFromOther=recommendService.getRecommendProductDetailDTOs(condition);
        if(CollectionUtil.isNotEmpty(productsFromOther)){
          List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.PRODUCT_RECOMMEND_LIST_IMAGE_SMALL);
          imageService.addImageInfoToProductDTO(imageSceneList,true,productsFromOther.toArray(new ProductDTO[productsFromOther.size()]));
          promotionsService.addPromotionInfoToProductDTO(productsFromOther.toArray(new ProductDTO[productsFromOther.size()]));
        }
        modelMap.put("productsFromOther",productsFromOther);
        //查询促销中的商品
        PromotionSearchCondition pCondition=new PromotionSearchCondition();
        pCondition.setPromotionStatus(PromotionsEnum.PromotionStatus.USING);
        pCondition.setSort(new Sort("startTime","desc"));
        pCondition.setMaxRows(SupplyDemandConstant.PROMOTION_PRODUCTS_NUM);
        pCondition.setShopKind(ServiceManager.getService(IConfigService.class).getShopById(shopId).getShopKind());
        List<PromotionsProductDTO> promotionsProductDTOs=ServiceManager.getService(IPromotionsService.class).getPromotionsProductDTO(pCondition);
        List<ProductDTO> promotingProducts=new ArrayList<ProductDTO>();
        if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
          ProductSearchCondition searchConditionDTO=new ProductSearchCondition();
          for(PromotionsProductDTO pp:promotionsProductDTOs){
            searchConditionDTO.setProductId(pp.getProductLocalInfoId());
            ProductDTO promotingProduct=CollectionUtil.getFirst(productService.getProductInfo(searchConditionDTO));
            if(promotingProduct!=null){
                  promotingProducts.add(promotingProduct);
            }
          }
        }
        if(CollectionUtil.isNotEmpty(promotingProducts)){
          List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
          imageService.addImageInfoToProductDTO(imageSceneList,true,promotingProducts.toArray(new ProductDTO[promotingProducts.size()]));
          promotionsService.addPromotionInfoToProductDTO(promotingProducts.toArray(new ProductDTO[promotingProducts.size()]));
        }
        modelMap.put("promotingProducts",promotingProducts);
        //最新配件报价(求购报价)
        modelMap.put("latestQuotedPreBuyOrderItems",preBuyOrderService.getLatestQuotedPreBuyOrderItemByCustomerShopId(shopId,null));
        // 最新上架配件
        ProductSearchCondition searchConditionDTO=new ProductSearchCondition();
        searchConditionDTO.setLimit(SupplyDemandConstant.LATEST_IN_SALING_PRODUCT_NUM);
        searchConditionDTO.setSortCondition(new Sort("lastInSalesTime","desc"));  //todo improve
        List<ProductDTO> lastInSalesProductDTOs=productService.getProductInfo(searchConditionDTO);
        if(CollectionUtil.isNotEmpty(lastInSalesProductDTOs)){
          List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.PRODUCT_RECOMMEND_LIST_IMAGE_SMALL);
          ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,lastInSalesProductDTOs.toArray(new ProductDTO[lastInSalesProductDTOs.size()]));
          promotionsService.addPromotionInfoToProductDTO(lastInSalesProductDTOs.toArray(new ProductDTO[lastInSalesProductDTOs.size()]));
        }
        modelMap.put("lastInSalesProductDTOs",lastInSalesProductDTOs);
        //最近浏览的商品
        List<ProductDTO> recentlyViewedProductDTOList = productService.getRecentlyViewedProductDTOList(shopId,userId);
        if(CollectionUtil.isNotEmpty(recentlyViewedProductDTOList)){
          List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
          imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
          ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,recentlyViewedProductDTOList.toArray(new ProductDTO[recentlyViewedProductDTOList.size()]));
          promotionsService.addPromotionInfoToProductDTO(recentlyViewedProductDTOList.toArray(new ProductDTO[recentlyViewedProductDTOList.size()]));
        }
        modelMap.put("recentlyViewedProductDTOList",recentlyViewedProductDTOList);
        //统计信息
        modelMap.put("allPreBuyOrderCount",String.valueOf(preBuyOrderService.countValidPreBuyOrderItems(shopId)));
        modelMap.put("allQuotedCount",String.valueOf(preBuyOrderService.countQuotedPreBuyOrderItems(shopId)));
        preBuyOrderSearchCondition=new PreBuyOrderSearchCondition();
        preBuyOrderSearchCondition.setShopId(shopId);
        preBuyOrderSearchCondition.setPageSize(null);
        List<PreBuyOrderItemDTO> allPreBuyOrderItemDTOs=preBuyOrderService.getPreBuyOrderItemDetailDTO(preBuyOrderSearchCondition);
        Long totalViewedBusinessChance=0l;
        if(CollectionUtil.isNotEmpty(allPreBuyOrderItemDTOs)){
          List<Long> preBuyOrderItemIds=new ArrayList<Long>();
          for (PreBuyOrderItemDTO itemDTO:allPreBuyOrderItemDTOs){
            preBuyOrderItemIds.add(itemDTO.getId());
          }
          Map<Long,Long> statMap=recentlyUsedDataService.statRecentlyUsedDataCountByDataId(null, RecentlyUsedDataType.VISITED_BUSINESS_CHANCE,ArrayUtil.toLongArr(preBuyOrderItemIds));
          if(MapUtils.isNotEmpty(statMap)){
            for (Long count:statMap.values()){
              totalViewedBusinessChance= NumberUtil.additionLong(totalViewedBusinessChance,count);
            }
          }
        }
        modelMap.put("totalViewedBusinessChance",NumberUtil.roundInt(totalViewedBusinessChance));
        return "autoaccessoryonline/supplyDemandIndex";
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return "/";
    }
  }

  /**
   * 最新上架配件
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getInSalingProductForSupplyDemand")
  @ResponseBody
  public Object getInSalingProductForSupplyDemand(HttpServletRequest request) {
    try {
      Long shopId=WebUtil.getShopId(request);
      List<ProductDTO> inSalingProducts=ServiceManager.getService(IProductService.class).getInSalingProductForSupplyDemand(shopId,SupplyDemandConstant.LATEST_IN_SALING_PRODUCT_NUM);
      if(CollectionUtil.isNotEmpty(inSalingProducts)){
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_RECOMMEND_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,inSalingProducts.toArray(new ProductDTO[inSalingProducts.size()]));
        ServiceManager.getService(IPromotionsService.class).addPromotionInfoToProductDTO(inSalingProducts.toArray(new ProductDTO[inSalingProducts.size()]));
        List<Long> productIds=new ArrayList<Long>();
        for(ProductDTO productDTO:inSalingProducts){
          productIds.add(productDTO.getProductLocalInfoId());
        }
        Map<Long, Long> viewMap=ServiceManager.getService(IRecentlyUsedDataService.class).statRecentlyUsedDataCountByDataId(null,RecentlyUsedDataType.VISITED_PRODUCT,ArrayUtil.toLongArr(productIds));
        if(MapUtils.isNotEmpty(viewMap)){
          for(ProductDTO productDTO:inSalingProducts){
            productDTO.setViewedCount(NumberUtil.doubleValue(viewMap.get(productDTO.getProductLocalInfoId()),0));
          }
        }
      }
      return inSalingProducts;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取明星供应商
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getBcgogoRecommendSupplierShop")
  @ResponseBody
  public Object getBcgogoRecommendSupplierShop(HttpServletRequest request) {
    Result result = new Result();
    result.setSuccess(false);

    try {
      Long shopId = WebUtil.getShopId(request);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      List<ShopDTO> shopDTOList = configService.getBcgogoRecommendSupplierShop(shopId);
      result.setData(shopDTOList);
      result.setSuccess(true);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setData(null);
    }
    return result;
  }

  /**
   * 获取推荐的供应商或者客户
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getRecommendShopByShopId")
  @ResponseBody
  public Object getRecommendShopByShopId(HttpServletRequest request,Integer pageSize) {
    Result result = new Result();
    result.setSuccess(false);
    try {
      Long shopId = WebUtil.getShopId(request);
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      IImageService imageService=ServiceManager.getService(IImageService.class);
      Pager pager = new Pager(1);
      pager.setPageSize(NumberUtil.intValue(pageSize,50));
      List<ShopDTO> shopDTOList = preciseRecommendService.getRecommendShopByShopId(shopId,pager);
      if(CollectionUtil.isNotEmpty(shopDTOList)){
        //增加图片
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.SHOP_IMAGE_60X60_SMALL);
        ServiceManager.getService(IImageService.class).addImageToShopDTO(imageSceneList,true,shopDTOList.toArray(new ShopDTO[shopDTOList.size()]));
      }
      for(ShopDTO shopDTO:shopDTOList){
        shopDTO.setLicensed(imageService.isExistDataImageRelation(shopDTO.getId(), ImageType.SHOP_BUSINESS_LICENSE_IMAGE, DataType.SHOP, shopDTO.getId(),1));
      }
      result.setSuccess(true);
      result.setData(shopDTOList);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      result.setSuccess(false);
      result.setData(null);
    }
    return result;
  }


  /**
   *
   * 供求中心首页获取推荐信息(汽修版)
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getProductRecommendByShopId")
  @ResponseBody
  public Object getProductRecommendByShopId(HttpServletRequest request,Integer pageSize) {
    try {
      Long shopId = WebUtil.getShopId(request);
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      ProductSearchCondition condition=new ProductSearchCondition();
      condition.setLimit(NumberUtil.intValue(pageSize, 100));
      condition.setShopId(shopId);
      List<ProductDTO> productDTOList = preciseRecommendService.getRecommendProductDetailDTOs(condition);
      if(CollectionUtil.isNotEmpty(productDTOList)){
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_RECOMMEND_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,productDTOList.toArray(new ProductDTO[productDTOList.size()]));
      }
      return productDTOList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   *
   * 供求中心首页获取推荐信息(汽配版)
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getWholesalerPreBuyOrderRecommendByShopId")
  @ResponseBody
  public Object getWholesalerPreBuyOrderRecommendByShopId(HttpServletRequest request) {
    List resultList = new ArrayList();
    try {
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      Long shopId = WebUtil.getShopId(request);
      List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = null;
      preBuyOrderItemDTOList = preciseRecommendService.getWholesalerProductRecommendByPager(shopId, DeletedType.FALSE, null);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_RECOMMEND_LIST_IMAGE_SMALL);
      ServiceManager.getService(IImageService.class).addImageToPreBuyOrderItemDTO(preBuyOrderItemDTOList,imageSceneList,true);
      resultList.add(preBuyOrderItemDTOList);
      return resultList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return resultList;
  }


  /**
   *
   * 供求中心首页获取上月销量信息(汽配版)
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getLastWeekSalesInventoryStatByShopId")
  @ResponseBody
  public Object getLastWeekSalesInventoryStat(HttpServletRequest request,ProductSearchCondition productSearchCondition) {
    List resultList = new ArrayList();
    try {
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      productSearchCondition.setStart(productSearchCondition.getStart() == null ? 1 : productSearchCondition.getStart());
      productSearchCondition.setLimit(productSearchCondition.getLimit() == null ? 8 : productSearchCondition.getLimit());

      Long shopId = WebUtil.getShopId(request);
      int statYear = DateUtil.getCurrentYear();
      int statMonth = DateUtil.getCurrentMonth();
      int statDay = DateUtil.getCurrentDay();

      Calendar calendar = Calendar.getInstance();
      calendar.setFirstDayOfWeek(Calendar.MONDAY);
      int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
      if (dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.WEDNESDAY) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        statDay = DateUtil.getDay(calendar.getTimeInMillis());
        statMonth =DateUtil.getMonth(calendar.getTimeInMillis());
        statYear = DateUtil.getYear(calendar.getTimeInMillis());
      } else if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        statDay = DateUtil.getDay(calendar.getTimeInMillis());
        statMonth = DateUtil.getMonth(calendar.getTimeInMillis());
        statYear = DateUtil.getYear(calendar.getTimeInMillis());
      }
      List<ShopProductMatchResultDTO> resultDTOList = preciseRecommendService.getLastMonthSalesInventoryStatByShopId(shopId, statYear, statMonth, statDay, null);
      resultList.add(resultDTOList);
      return resultList;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return resultList;
  }


}
