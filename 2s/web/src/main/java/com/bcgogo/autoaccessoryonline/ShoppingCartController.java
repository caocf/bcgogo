package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.config.service.ApplyService;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.dto.ProductSearchResultGroupListDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.ShoppingCartDTO;
import com.bcgogo.txn.dto.ShoppingCartItemDTO;
import com.bcgogo.txn.service.IShoppingCartService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bcgogo.config.util.ConfigUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 下午5:30
 */
@Controller
@RequestMapping("/shoppingCart.do")
public class ShoppingCartController {
  private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartController.class);

  @Autowired
  private IShoppingCartService shoppingCartService;

  @RequestMapping(params = "method=shoppingCartManage")
  public String shoppingCart(ModelMap model, HttpServletRequest request) {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      ShoppingCartDTO shoppingCartDTO = shoppingCartService.generateShoppingCartDTO(shopId, userId);
      model.addAttribute("shoppingCartDTO", shoppingCartDTO);
      if (shoppingCartDTO.getShoppingCartItemCount() <= 0) {
        boolean isWholesaler = ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request));
        IProductService productService = ServiceManager.getService(IProductService.class);
        List<ProductDTO> recentlyViewedProductDTOList = productService.getRecentlyViewedProductDTOList(shopId,userId);//浏览过的配件信息
        if (recentlyViewedProductDTOList.size() > 4) {
          recentlyViewedProductDTOList = recentlyViewedProductDTOList.subList(0, 4);
        }
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,recentlyViewedProductDTOList.toArray(new ProductDTO[recentlyViewedProductDTOList.size()]));

        IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
        ProductSearchCondition condition=new ProductSearchCondition();
        condition.setLimit(4);
        condition.setShopId(shopId);
        List<ProductDTO> productDTOList = null;//推荐配件信息
        if (isWholesaler) {
          productDTOList = productService.getProductDTOByRelationSupplier(shopId);//气配版
        } else {
          productDTOList = preciseRecommendService.getRecommendProductDetailDTOs(condition);//气修版
        }
        if (productDTOList != null && productDTOList.size() > 4) {
          productDTOList = productDTOList.subList(0, 4);
        }
        imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_RECOMMEND_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList, true, productDTOList.toArray(new ProductDTO[productDTOList.size()]));
        productService.filterInvalidPromotions(recentlyViewedProductDTOList);
        productService.filterInvalidPromotions(productDTOList);
        model.addAttribute("recentlyViewedProductDTOList", recentlyViewedProductDTOList);
        model.addAttribute("productDTOList", productDTOList);
        model.addAttribute("isWholesaler", isWholesaler);
        return "/autoaccessoryonline/emptyshopViewed";
      }
    } catch (Exception e) {
      LOG.debug("/shoppingCart.do");
      LOG.debug("method=shoppingCart");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/shoppingCartList";
  }

  @RequestMapping(params = "method=getShoppingCartData")
  @ResponseBody
  public Object goodsInSalesList(HttpServletRequest request) {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    ShoppingCartDTO shoppingCartDTO = null;
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      shoppingCartDTO = shoppingCartService.generateShoppingCartDTO(shopId, userId);
      if (shoppingCartDTO.getShoppingCartItemCount() > 0) {
        IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
        Set<SupplierDTO> supplierDTOSet = shoppingCartDTO.getShoppingCartDetailMap().keySet();
        for (SupplierDTO supplierDTO : supplierDTOSet) {
          List<ShoppingCartItemDTO> shoppingCartItemDTOList = shoppingCartDTO.getShoppingCartDetailMap().get(supplierDTO);
          List<Long> productIdList = new ArrayList<Long>();
          for (ShoppingCartItemDTO shoppingCartItemDTO : shoppingCartItemDTOList) {
            productIdList.add(shoppingCartItemDTO.getProductLocalInfoId());
          }
          Long[] productId = new Long[productIdList.size()];
          productId = productIdList.toArray(productId);
          Map<Long, List<PromotionsDTO>> promotionMap = promotionsService.getPromotionsDTOMapByProductLocalInfoId(supplierDTO.getSupplierShopId(), true, productId);
          for (ShoppingCartItemDTO shoppingCartItemDTO : shoppingCartItemDTOList) {
            List<PromotionsDTO> promotionsDTOList = promotionMap.get(shoppingCartItemDTO.getProductLocalInfoId());
            List<PromotionsDTO> promotionsDTOListTemp = new ArrayList<PromotionsDTO>();
            if (CollectionUtils.isNotEmpty(promotionsDTOList)) {
              for (PromotionsDTO pd : promotionsDTOList) {
                if (PromotionsEnum.PromotionStatus.USING.equals(pd.getStatus())) {
                  promotionsDTOListTemp.add(pd);
                }
              }
              promotionsDTOList = promotionsDTOListTemp;
              if (CollectionUtils.isNotEmpty(promotionsDTOList)) {
                shoppingCartItemDTO.setPromotionsDTOList(promotionsDTOList);
                String[] promotionsTitle = PromotionsUtils.genPromotionTypesStr(promotionsDTOList);
                shoppingCartItemDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotionsDTOList, shoppingCartItemDTO.getPrice()));
                shoppingCartItemDTO.setPromotionsTitle(promotionsTitle);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.debug("/shoppingCart.do");
      LOG.debug("method=getShoppingCartData");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }
    return shoppingCartDTO;
  }

  /**
   * 更新指定购物车商品的数量
   * @param request
   * @param shoppingCartItemId
   * @param amount
   * @return
   */
  @RequestMapping(params = "method=updateShoppingCartItemAmount")
  @ResponseBody
  public Object updateShoppingCartItemAmount(HttpServletRequest request,Long shoppingCartItemId,Double amount) {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      shoppingCartService.updateShoppingCartItemAmount(shopId,shoppingCartItemId, amount);
      return new Result();
    } catch (Exception e) {
      LOG.debug("/shoppingCart.do");
      LOG.debug("method=updateShoppingCartItemAmount");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 删除购物车内的商品
   * @param request
   * @param shoppingCartItemId
   * @return
   */
  @RequestMapping(params = "method=deleteShoppingCartItemById")
  @ResponseBody
  public Object deleteShoppingCartItemById(HttpServletRequest request,Long... shoppingCartItemId) {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      shoppingCartService.deleteShoppingCartItemById(shopId,userId,shoppingCartItemId);
      Map<String,Integer> map = new HashMap<String, Integer>();
      map.put("shoppingCartItemCount",shoppingCartService.getShoppingCartItemCountInMemCache(shopId,userId));
      map.put("shoppingCartMaxCapacity",shoppingCartService.getShoppingCartMaxCapacity());
      return new Result(true,map);
    } catch (Exception e) {
      LOG.debug("/shoppingCart.do");
      LOG.debug("method=deleteShoppingCartItemById");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 获取购物内的商品种数
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getShoppingCartItemCount")
  @ResponseBody
  public Object getShoppingCartItemCount(HttpServletRequest request) {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      return shoppingCartService.getShoppingCartItemCountInMemCache(shopId,userId);
    } catch (Exception e) {
      LOG.debug("/shoppingCart.do");
      LOG.debug("method=getShoppingCartItemCount");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 清除失效商品：下架的    跟供应商取消关联的
   * @param request
   * @return
   */
  @RequestMapping(params = "method=clearInvalidShoppingCartItems")
  @ResponseBody
  public Object clearInvalidShoppingCartItems(HttpServletRequest request) {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      shoppingCartService.clearInvalidShoppingCartItems(shopId,userId);
      return new Result();
    } catch (Exception e) {
      LOG.debug("/shoppingCart.do");
      LOG.debug("method=getShoppingCartItemCount");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 添加商品到购物车
   * @param request
   * @param paramString    supplierShopId_productLocalInfoId_amount
   * @return
   */
  @RequestMapping(params = "method=addProductToShoppingCart")
  @ResponseBody
  public Object addProductToShoppingCart(HttpServletRequest request,String... paramString) {
    Long shopId= WebUtil.getShopId(request);
    Long userId= WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      int maxCapacity = shoppingCartService.getShoppingCartMaxCapacity();
      int warnCapacity = shoppingCartService.getShoppingCartWarnCapacity();
      Map<String,Object> data = new HashMap<String, Object>();
      if(!ArrayUtils.isEmpty(paramString)){
        ShoppingCartItemDTO shoppingCartItemDTO = null;;
        List<ShoppingCartItemDTO> shoppingCartItemDTOList = new ArrayList<ShoppingCartItemDTO>();
        for(String param : paramString){
          String[] params = param.split("_");
          if(NumberUtil.isLongNumber(params[0]) && NumberUtil.isLongNumber(params[1])){
            shoppingCartItemDTO = new ShoppingCartItemDTO();
            shoppingCartItemDTO.setShopId(shopId);
            shoppingCartItemDTO.setAmount(NumberUtil.doubleValue(params[2],0d));
            shoppingCartItemDTO.setSupplierShopId(Long.parseLong(params[0]));
            shoppingCartItemDTO.setProductLocalInfoId(Long.parseLong(params[1]));
            shoppingCartItemDTO.setUserId(userId);
            shoppingCartItemDTO.setEditDate(System.currentTimeMillis());
            shoppingCartItemDTOList.add(shoppingCartItemDTO);
          }
        }
        if(shoppingCartItemDTOList.size()+shoppingCartService.getShoppingCartItemCountInMemCache(shopId,userId)>=maxCapacity){
          ShoppingCartDTO shoppingCartDTO = shoppingCartService.generateShoppingCartDTO(shopId, userId);
          data.put("resultMsg","添加失败！");
          data.put("warnMsg","购物车已到达上限"+maxCapacity+"种！");
          data.put("goShoppingCartBtnName","整理购物车");
          data.put("shoppingCartItemCount",shoppingCartDTO.getShoppingCartItemCount());
          data.put("shoppingCartTotal",shoppingCartDTO.getTotal());
          return new Result(false,data);
        }else{
          shoppingCartService.saveOrUpdateShoppingCartItems(shopId,userId,shoppingCartItemDTOList.toArray(new ShoppingCartItemDTO[shoppingCartItemDTOList.size()]));
          data.put("resultMsg","已成功添加"+shoppingCartItemDTOList.size()+"种商品到购物车！");
          ShoppingCartDTO shoppingCartDTO = shoppingCartService.generateShoppingCartDTO(shopId, userId);
          data.put("shoppingCartItemCount",shoppingCartDTO.getShoppingCartItemCount());
          data.put("shoppingCartTotal",shoppingCartDTO.getTotal());
          if(shoppingCartDTO.getShoppingCartItemCount()>=maxCapacity){
            data.put("warnMsg","购物车已满！");
            data.put("goShoppingCartBtnName","整理购物车");
          }else if(shoppingCartDTO.getShoppingCartItemCount()>=warnCapacity && shoppingCartDTO.getShoppingCartItemCount()<maxCapacity){
            data.put("warnMsg","购物车快满了！");
            data.put("goShoppingCartBtnName","去购物车结算");
          }else{
            data.put("warnMsg","");
            data.put("goShoppingCartBtnName","去购物车结算");
          }
          return new Result(true,data);
        }
      }
    } catch (Exception e) {
      LOG.debug("/shoppingCart.do");
      LOG.debug("method=addProductToShoppingCart");
      LOG.debug("shopId:" + shopId + ",userId:" + userId);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
}
