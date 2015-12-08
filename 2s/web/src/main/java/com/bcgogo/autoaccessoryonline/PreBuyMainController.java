package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.ShoppingCartItemDTO;
import com.bcgogo.txn.dto.recommend.ProductRecommendDTO;
import com.bcgogo.txn.dto.remind.OrderCenterDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.IShoppingCartService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PromotionsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 我要买配件主页
 * User: terry
 * Date: 13-8-14
 * Time: 下午4:36
 */
@Controller
@RequestMapping("/preBuyMain.do")
public class PreBuyMainController {

  private static final Logger LOG = LoggerFactory.getLogger(PreBuyMainController.class);
  private static final int START = 0;
  private static final int DEFAULT_LIMIT = 2;
  private static final int SUPPLIER_LIMIT = 6;

  @RequestMapping(params = "method=toPreBuyMain")
  public String toPreBuyMain(HttpServletRequest request, ModelMap modelMap) {

    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    try {
      // 查询订单中心相关
      IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
      OrderCenterDTO orderCenterDTO = orderCenterService.getOrderCenterStatistics(shopId);
      modelMap.addAttribute("orderCenterDTO", orderCenterDTO);

      // 购物车相关
      IShoppingCartService iShoppingCartService = ServiceManager.getService(IShoppingCartService.class);
      IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
      IProductService productService = ServiceManager.getService(IProductService.class);
      List<ShoppingCartItemDTO> shoppingCartItemDTOs =  subListForSize2(iShoppingCartService.getShopCarItemList(shopId, userId), DEFAULT_LIMIT);
      if(CollectionUtil.isNotEmpty(shoppingCartItemDTOs)){
        for (ShoppingCartItemDTO itemDTO:shoppingCartItemDTOs){
          Map<Long,ProductDTO> pMap=promotionsService.getProductPromotionDetail(itemDTO.getSupplierShopId(), itemDTO.getProductLocalInfoId());
          ProductDTO productDTOTemp=null;
          if(pMap!=null){
            productDTOTemp=pMap.get(itemDTO.getProductLocalInfoId());
          }
          if(productDTOTemp==null){
            continue;
          }
          ProductDTO productDTO=productService.getProductByProductLocalInfoId(itemDTO.getProductLocalInfoId(),itemDTO.getSupplierShopId());
          if(productDTO==null){
            continue;
          }
          List<PromotionsDTO> promotionsDTOs=productDTOTemp.getPromotionsDTOs();
          itemDTO.setPromotionsDTOList(promotionsDTOs);
          itemDTO.setInSalesPrice(productDTO.getInSalesPrice());
          itemDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotionsDTOs,productDTO.getInSalesPrice()));
          String[] titles = PromotionsUtils.genPromotionTypesStr(promotionsDTOs);
          itemDTO.setPromotionTypesShortStr(titles[0]);
          itemDTO.setPromotionTypesStr(titles[1]);
        }
      }
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      ServiceManager.getService(IImageService.class).addImageToShoppingCartItemDTO(shoppingCartItemDTOs,imageSceneList,true);

      modelMap.addAttribute("shoppingCarItemDTOs", shoppingCartItemDTOs);

      // 查询我的求购
      IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
      Long preBuyOrderServiceCount = preBuyOrderService.countPreBuyOrderItems(shopId);
      modelMap.addAttribute("preBuyOderServiceCount",preBuyOrderServiceCount.longValue());
      Long quotedPreBuyOderServiceCount = preBuyOrderService.countQuotedPreBuyOrderItems(shopId);
      modelMap.addAttribute("quotedPreBuyOderServiceCount",quotedPreBuyOderServiceCount.longValue());
      List<PreBuyOrderDTO> preBuyOrderDTOs = preBuyOrderService.getPreBuyOrdersByShopId(shopId, START, DEFAULT_LIMIT);
      addImageToPreBuyOrderDTO(preBuyOrderDTOs);
      modelMap.addAttribute("preBuyOrderDTOs", preBuyOrderDTOs);

      // 查询关联的供应商列表
      ICustomerService iCustomerService = ServiceManager.getService(ICustomerService.class);
      List<SupplierDTO> supplierDTOs = iCustomerService.getWholeSalersByCustomerShopId(shopId);
      modelMap.addAttribute("relatedSupplierCount", supplierDTOs.size());
      modelMap.addAttribute("supplierDTOs", subListForSize2(supplierDTOs, SUPPLIER_LIMIT));


      // 查询推荐配件信息
      IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
      int preciseRecommendCount = preciseRecommendService.countProductRecommendByShopId(shopId, DeletedType.FALSE);
      modelMap.addAttribute("preciseRecommendCount", preciseRecommendCount);
      ProductSearchCondition condition=new ProductSearchCondition();
      condition.setShopId(shopId);
      condition.setLimit(2);
      List<ProductDTO> productDTOList = preciseRecommendService.getRecommendProductDetailDTOs(condition);
      imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,productDTOList.toArray(new ProductDTO[productDTOList.size()]));
      modelMap.addAttribute("productList", productDTOList);
      OnlinePurchaseReturnOrderController.setDateToModel(modelMap);

    } catch (Exception e) {
      LOG.error("/preBuyMain.do?method=toPreBuyMain", e);
    }
    return "autoaccessoryonline/preBuyMain";
  }

  /**
   * 需求比较特殊 随便找个有图片的显示在 单据上 而不是 item上
   * @param preBuyOrderDTOs
   */
  private void addImageToPreBuyOrderDTO(List<PreBuyOrderDTO> preBuyOrderDTOs){
    if(CollectionUtils.isNotEmpty(preBuyOrderDTOs)){
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      Set<Long> shopIdSet = new HashSet<Long>();
      List<Long> productIdList = new ArrayList<Long>();
      for(PreBuyOrderDTO preBuyOrderDTO:preBuyOrderDTOs){
        if(CollectionUtils.isNotEmpty(preBuyOrderDTO.getProductIdList())){
          shopIdSet.add(preBuyOrderDTO.getShopId());
          productIdList.addAll(preBuyOrderDTO.getProductIdList());
          Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = imageService.getDataImageDetailDTO(shopIdSet, imageSceneList, DataType.PRODUCT, productIdList.toArray(new Long[productIdList.size()]));
          if(MapUtils.isNotEmpty(imageMap)){
            preBuyOrderDTO.setItemImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(CollectionUtils.get(imageMap.keySet(),0)), true));
          }else {
            preBuyOrderDTO.setItemImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList,null, true));
          }
        }
      }
    }
  }

  private <T> List<T> subListForSize2(List<T> list, int limit) {
    int size = list.size();
    if (size > limit) {
      return list.subList(START, limit);
    }
    return list;
  }

}
