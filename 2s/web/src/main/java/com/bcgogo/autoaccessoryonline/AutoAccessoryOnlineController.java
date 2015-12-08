package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.*;
import com.bcgogo.common.CookieUtil;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.dto.ShopAdAreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.autoaccessoryonline.SupplyDemandConstant;
import com.bcgogo.constant.crm.productCategory.ProductCategoryConstant;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.ProductAdStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.product.service.ProductService;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
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
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 下午5:30
 */
@Controller
@RequestMapping("/autoAccessoryOnline.do")
public class AutoAccessoryOnlineController {
  private static final Logger LOG = LoggerFactory.getLogger(AutoAccessoryOnlineController.class);
  private static final int TOP_AD_SHOP_NUMBER=50;

  @RequestMapping(params = "method=toCommodityQuotations")
  public String toCommodityQuotations(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, SearchConditionDTO searchConditionDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      if(StringUtils.isNotBlank(request.getParameter("productIds"))) {
        searchConditionDTO.setProductIds(request.getParameter("productIds"));
      }
      if(StringUtils.isNotBlank(request.getParameter("fromPage"))) {
        modelMap.addAttribute("fromPage", request.getParameter("fromPage"));
      }
      modelMap.addAttribute("searchConditionDTO", searchConditionDTO);
      IProductService productService = ServiceManager.getService(IProductService.class);
      List<ProductDTO> recentlyViewedProductDTOList = productService.getRecentlyViewedProductDTOList(shopId,WebUtil.getUserId(request));
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,recentlyViewedProductDTOList.toArray(new ProductDTO[recentlyViewedProductDTOList.size()]));
      modelMap.addAttribute("recentlyViewedProductDTOList",recentlyViewedProductDTOList);
      productService.filterInvalidPromotions(recentlyViewedProductDTOList);
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, new String[]{"PRODUCT_PRICE_GUIDE_PURCHASE_CENTER"});

      modelMap.addAttribute("isWholesaler", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));

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
      if(ArrayUtil.isNotEmpty(searchConditionDTO.getProductCategoryDTOs())){
        modelMap.addAttribute("productCategoryDTOsJson", JsonUtil.listToJson(Arrays.asList(searchConditionDTO.getProductCategoryDTOs())));
      }
      return "/autoaccessoryonline/commodityQuotations";
    } catch (Exception e) {
      LOG.debug("/autoAccessoryOnline.do");
      LOG.debug("method=toCommodityQuotations");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error("转向商品报价页面异常！！！");
      return null;
    }
  }

  /**
   * 之前的逻辑是非关联供应商不能立即采购。现在改成可以立即采购了
   * @param request
   * @param productLocalInfoIds
   * @param amounts  参数传递作用
   * @return
   */
  @RequestMapping(params = "method=validatorPurchaseProduct")
  @ResponseBody
  public Object validatorPurchaseProduct(HttpServletRequest request,Long[] productLocalInfoIds,Double[] amounts) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      if(!ArrayUtils.isEmpty(productLocalInfoIds)){
        Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(productLocalInfoIds);
        ProductLocalInfoDTO productLocalInfoDTO = null;
        List<String> paramStringList = new ArrayList<String>();
        for(int i=0;i<productLocalInfoIds.length;i++){
          productLocalInfoDTO = productLocalInfoDTOMap.get(productLocalInfoIds[i]);
          if(productLocalInfoDTO!=null){
            paramStringList.add(productLocalInfoDTO.getShopId()+"_"+productLocalInfoIds[i]+"_"+amounts[i]);
          }
        }
        return new Result(true,paramStringList);
      }
    } catch (Exception e) {
      LOG.debug("/autoAccessoryOnline.do?method=validatorPurchaseProduct");
      LOG.error(e.getMessage(), e);
    }
    return new Result();
  }

  /**
   * 1.如果供应商条件不为空  只查这个供应商的上架商品
   * 2.如果供应商条件为空   先查本店关联的供应商商品  如果为空   查省内   再为空 查省外
   * @param request
   * @param searchConditionDTO
   * @return
   */
  @RequestMapping(params = "method=getCommodityQuotationsList")
  @ResponseBody
  public Object getAllWholeSalerStock(HttpServletRequest request,String fromSource,SearchConditionDTO searchConditionDTO){
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ProductSearchResultListDTO searchResultListDTO=null;
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(StringUtils.isNotBlank(searchConditionDTO.getSortStatus())){
        searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSortStatus().replaceAll(" ","")));
      }else if (StringUtils.isBlank(searchConditionDTO.getSortStatus()) && searchConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
        searchConditionDTO.setSort("last_in_sales_time desc");
      }
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      searchConditionDTO.setIncludeBasic(false);
      ShopDTO shopDTO = configService.getShopById(shopId);
      searchConditionDTO.setShopKind(shopDTO.getShopKind());

      if ("messageCenter".equals(fromSource)||"batchGoodsInSalesEditor".equals(fromSource)) {//消息中心跳转 或者批量上架那边跳转
        if (StringUtils.isBlank(searchConditionDTO.getProductIds())) {
          searchResultListDTO = new ProductSearchResultListDTO();
        }else{
          searchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
        }
      }else if("goodsBuy".equals(fromSource)||"shopMsgDetail".equals(fromSource)){//采购单添加商品
        searchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
      }else if("AdSearch".equals(fromSource)){    //广告商品搜索
        List<Long> adShopIds=configService.getAdShopIds(TOP_AD_SHOP_NUMBER);
        if(CollectionUtil.isNotEmpty(adShopIds)){
          searchConditionDTO.setShopIds(adShopIds.toArray(new Long[adShopIds.size()]));
          searchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
        }else {
          searchResultListDTO = new ProductSearchResultListDTO();
        }
      }else{
        searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
        if(BcgogoShopLogicResourceUtils.isDisableUnrelatedSupplierCommodityQuotations(WebUtil.getShopVersionId(request))){
          List<SupplierDTO> supplierDTOList = ServiceManager.getService(IUserService.class).getRelatedSuppliersByShopId(shopId);
          Set<Long> supplierShopIdSet = new HashSet<Long>();
          if (CollectionUtils.isNotEmpty(supplierDTOList)) {
            for (SupplierDTO supplierDTO : supplierDTOList) {
              supplierShopIdSet.add(supplierDTO.getSupplierShopId());
            }
          }
          if(!CollectionUtils.isEmpty(supplierShopIdSet)){
            searchConditionDTO.setShopIds(supplierShopIdSet.toArray(new Long[supplierShopIdSet.size()]));//只显示关联的
            searchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
          }
        }else{
          searchConditionDTO.setExcludeShopIds(new Long[]{shopId});//除去自己店铺的
          searchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
        }
      }

      if(searchResultListDTO!=null && CollectionUtils.isNotEmpty(searchResultListDTO.getProducts())){
        Set<Long> resultSupplierShopIdSet = new HashSet<Long>();
        Set<Long> resultProductIdSet = new HashSet<Long>();
        for(ProductDTO productDTO : searchResultListDTO.getProducts()){
          resultSupplierShopIdSet.add(productDTO.getShopId());
          resultProductIdSet.add(productDTO.getProductLocalInfoId());
        }

        //供应商评分
        ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
        Map<Long, CommentStatDTO> supplierCommentStatDTOMap =supplierCommentService.getCommentStatByShopIds(resultSupplierShopIdSet);
        for (CommentStatDTO commentStatDTO : supplierCommentStatDTOMap.values()){
          if(commentStatDTO != null) {
            commentStatDTO.calculate();
          }
        }

        Map<Long,ShopDTO> shopDTOMap = configService.getShopByShopId(resultSupplierShopIdSet.toArray(new Long[resultSupplierShopIdSet.size()]));
        Map<Long,String[]> shopQQsMap = new HashMap<Long, String[]>();
        ContactDTO[] contactDTOs = null;
        for(ShopDTO sDTO:shopDTOMap.values()){
          contactDTOs = sDTO.getContacts();
          if(!ArrayUtils.isEmpty(contactDTOs)){
            List<String> qqList = new ArrayList<String>();
            for(ContactDTO contactDTO:contactDTOs){
              if(contactDTO!=null && StringUtils.isNotBlank(contactDTO.getQq()))
                qqList.add(contactDTO.getQq());
            }
            if(CollectionUtils.isNotEmpty(qqList)){
              shopQQsMap.put(sDTO.getId(),qqList.toArray(new String[qqList.size()]));
            }
          }
        }
        //去掉不满足送货上门
        List<Long> promotionsIdList = new ArrayList<Long>();
        for (ProductDTO productDTO : searchResultListDTO.getProducts()) {
          PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
          if (promotionsDTO != null) {
            promotionsIdList.add(promotionsDTO.getId());
          }
        }
        if (promotionsIdList.size() > 0) {
          IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
          Map<Long, Boolean> isInPromotionsArea = promotionsService.judgePromotionsAreaByShopId(shopId, promotionsIdList.toArray(new Long[promotionsIdList.size()]));
          for (ProductDTO productDTO : searchResultListDTO.getProducts()) {
            PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
            if (promotionsDTO != null && !isInPromotionsArea.get(promotionsDTO.getId())) {
              List<PromotionsDTO> promotionsDTOs = productDTO.getPromotionsDTOs();
              Iterator<PromotionsDTO> iterator = promotionsDTOs.iterator();
              while (iterator.hasNext()) {
                PromotionsDTO promDTO = iterator.next();
                if (PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promDTO.getType())) {
                  iterator.remove();
                }
              }
            }
          }
        }
        for(ProductDTO productDTO:searchResultListDTO.getProducts()){
          productDTO.setCommentStatDTO(supplierCommentStatDTOMap.get(productDTO.getShopId()));
          productDTO.setShopContactQQs(shopQQsMap.get(productDTO.getShopId()));
          PromotionsUtils.setPromotionsProductToProductDTO(productDTO);
        }

        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,searchResultListDTO.getProducts().toArray(new ProductDTO[searchResultListDTO.getProducts().size()]));
      }

      Map<String,Object> data = new HashMap<String, Object>();
      data.put("data",searchResultListDTO);
      if(StringUtils.isNotBlank(searchConditionDTO.getProductIds()) && (searchResultListDTO==null || CollectionUtils.isEmpty(searchResultListDTO.getProducts()))){
        data.put("errorMsg","友情提示：您查看的商品已经下架!");
      }

      List<Object> result = new ArrayList<Object>();
      Pager pager = new Pager(searchResultListDTO==null?0:Integer.valueOf(searchResultListDTO.getNumFound() + ""), searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      result.add(data);
      result.add(pager);

      return result;
    } catch (Exception e) {
      LOG.debug("/autoAccessoryOnline.do?method=getCommodityQuotationsList");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  //不带搜索条件的广告商品展示
  //前台就传这两个参数：
//  startPageNo=1;第几页
 // maxRows = 5;
  @RequestMapping(params = "method=getCommodityAd")
  @ResponseBody
  public Object getCommodityAd(HttpServletRequest request,SearchConditionDTO searchConditionDTO){
    try{
      //根据分页情况找出当前分页下的默认广告商品
      //默认广告商品由两部分组成 用union 方法。
      // 1.找出最新的两个adSopIds 中的productLocalInfoIds
      //2，productlocalinfo 连表shop 找出adStatus 为ENABLED order by adPricePerMonth DESC.
      IConfigService configService=ServiceManager.getService(IConfigService.class);
      ShopDTO shopDTO =configService.getShopById(WebUtil.getShopId(request));
      searchConditionDTO.setShopKind(shopDTO.getShopKind());
      IProductService productService=ServiceManager.getService(IProductService.class);
      List<Long> adShopIds=productService.getAdShopIdByShopArea(shopDTO.getProvince(), shopDTO.getCity(), shopDTO.getRegion());
      Pager pager = new Pager(productService.countCommodityAdProduct(adShopIds.toArray(new Long[adShopIds.size()])),searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      List<Long> pageProductLocalInfoIds=productService.getCommodityAdProductIds(pager,adShopIds.toArray(new Long[adShopIds.size()]));
      //根据productLocalInfoId组装product信息
      List<ProductDTO> productDTOs=null;
      if(CollectionUtil.isNotEmpty(pageProductLocalInfoIds)){
        productDTOs=productService.getProductDTOByIds(pageProductLocalInfoIds.toArray(new Long[pageProductLocalInfoIds.size()]));
        Map<Long,ProductDTO> productDTOMap=new HashMap<Long, ProductDTO>();
        for(ProductDTO productDTO:productDTOs){
          productDTOMap.put(productDTO.getProductLocalInfoId(),productDTO);
        }
        List<ProductDTO> sortProductDTOs=new ArrayList<ProductDTO>();
         for(Long productId:pageProductLocalInfoIds){
           sortProductDTOs.add(productDTOMap.get(productId));
         }
        productDTOs=sortProductDTOs;
      }

      if(CollectionUtils.isNotEmpty(productDTOs)){
        //去掉不满足送货上门
        List<Long> promotionsIdList = new ArrayList<Long>();
        for (ProductDTO productDTO : productDTOs) {
          PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
          if (promotionsDTO != null) {
            promotionsIdList.add(promotionsDTO.getId());
          }
        }
        //add promotion info
        if (promotionsIdList.size() > 0) {
          IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
          Map<Long, Boolean> isInPromotionsArea = promotionsService.judgePromotionsAreaByShopId(WebUtil.getShopId(request), promotionsIdList.toArray(new Long[promotionsIdList.size()]));
          for (ProductDTO productDTO : productDTOs) {
            PromotionsDTO promotionsDTO = PromotionsUtils.getPromotionsDTO(productDTO, PromotionsEnum.PromotionsTypes.FREE_SHIPPING);
            if (promotionsDTO != null && !isInPromotionsArea.get(promotionsDTO.getId())) {
              List<PromotionsDTO> promotionsDTOs = productDTO.getPromotionsDTOs();
              Iterator<PromotionsDTO> iterator = promotionsDTOs.iterator();
              while (iterator.hasNext()) {
                PromotionsDTO promDTO = iterator.next();
                if (PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promDTO.getType())) {
                  iterator.remove();
                }
              }
            }
          }
        }
        for(ProductDTO productDTO:productDTOs){
          PromotionsUtils.setPromotionsProductToProductDTO(productDTO);
        }
        //add image info
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,productDTOs.toArray(new ProductDTO[productDTOs.size()]));
      }
      ProductSearchResultListDTO searchResultListDTO= new ProductSearchResultListDTO();
      searchResultListDTO.setProducts(productDTOs);
      Map<String,Object> data = new HashMap<String, Object>();
      data.put("data",searchResultListDTO);
      if(StringUtils.isNotBlank(searchConditionDTO.getProductIds()) && (searchResultListDTO==null || CollectionUtils.isEmpty(searchResultListDTO.getProducts()))){
        data.put("errorMsg","暂时无推荐商品!");
      }
      List<Object> result = new ArrayList<Object>();
      result.add(data);
      result.add(pager);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }

  }

  @RequestMapping(params = "method=toRelatedCustomerStock")
  public String toCustomerStock(ModelMap modelMap, HttpServletRequest request,SearchConditionDTO searchConditionDTO){
    ICustomerService customerService=ServiceManager.getService(ICustomerService.class);

    try{
      modelMap.addAttribute("searchConditionDTO", searchConditionDTO);
      modelMap.addAttribute("customerDTOs",customerService.getRelatedCustomersByShopId(WebUtil.getShopId(request)));
    }catch (Exception e){
      LOG.debug("/autoAccessoryOnline.do");
      LOG.debug("method=toRelatedCustomerStock");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error("转向客户库存页面异常！！！");
    }
    return "/autoaccessoryonline/customerStock";
  }


  /**
   * 查询批发商所有关联客户库存信息
   * @param request
   * @param searchProductConditionDTO
   * @return
   */
  @RequestMapping(params = "method=getAllRelatedCustomerStock")
  @ResponseBody
  public Object getAllRelatedCustomerStock(HttpServletRequest request,SearchConditionDTO searchProductConditionDTO){
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    ICustomerService customerService=ServiceManager.getService(ICustomerService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    List result=new ArrayList();
    Map<String,List<Object>> resultMap=new HashMap<String, List<Object>>();
    List<CustomerDTO> customerDTOs=new ArrayList<CustomerDTO>();
    try {
      if ( searchProductConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchProductConditionDTO.getSearchWord())) {
        searchProductConditionDTO.setSort("inventory_amount desc");
      }
      Long shopId = WebUtil.getShopId(request);
      Map<String,CustomerDTO> customerDTOMap=new HashMap<String,CustomerDTO>();
      if(StringUtil.isNotEmpty(searchProductConditionDTO.getRelatedCustomerId())){
        customerDTOs.add(customerService.getCustomerById(NumberUtil.longValue(searchProductConditionDTO.getRelatedCustomerId())));
      }else if(StringUtil.isEmpty(searchProductConditionDTO.getRelatedCustomerId())&&StringUtil.isNotEmpty(searchProductConditionDTO.getRelatedCustomerName())) {
        customerDTOs=customerService.getRelatedCustomerByMatchedName(shopId, searchProductConditionDTO.getRelatedCustomerName());
      }else{
        customerDTOs=customerService.getRelatedCustomersByShopId(shopId);
      }
      if(CollectionUtils.isNotEmpty(customerDTOs)){
        for(CustomerDTO customerDTO:customerDTOs){
          if(customerDTO==null){
            continue;
          }
          customerDTOMap.put(String.valueOf(customerDTO.getCustomerShopId()),customerDTO);
        }
      }

      if(CollectionUtil.isEmpty(customerDTOMap.keySet())){
        return result;
      }
      searchProductConditionDTO.setWholesalerShopId(shopId);
      searchProductConditionDTO.setRelatedCustomerShopIds(customerDTOMap.keySet().toArray(new String[customerDTOMap.keySet().size()]));
      List<Object> data=null;
      CustomerDTO customerDTO=null;
      ProductMappingDTO productMappingDTO = null;
      ProductSearchResultGroupListDTO searchResultGroupListDTO=searchProductService.queryCustomerShopInventory(searchProductConditionDTO);

      for(ProductSearchResultListDTO productResult:searchResultGroupListDTO.getProductSearchResultList()){
        Map<Long, ProductMappingDTO> productMappingDTOMap = productService.getCustomerProductMappingDTOMap(productResult.getRelatedCustomerShopId(),shopId,productResult.getProductLocalInfoIdList().toArray(new Long[productResult.getProductLocalInfoIdList().size()]));

        for(ProductDTO productDTO : productResult.getProducts()){
          productMappingDTO = productMappingDTOMap.get(productDTO.getProductLocalInfoId());
          if(productMappingDTO!=null){
            productDTO.setLastPurchaseAmount(productMappingDTO.getCustomerLastPurchaseAmount());
            productDTO.setLastPurchaseDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY,productMappingDTO.getCustomerLastPurchaseDate()));
            productDTO.setLastPurchasePrice(productMappingDTO.getCustomerLastPurchasePrice());
          }
        }
        customerDTO= customerDTOMap.get(String.valueOf(productResult.getRelatedCustomerShopId()));
        if(customerDTO==null){
          continue;
        }

        data=new ArrayList<Object>();
        data.add(customerDTO);
        data.add(productResult);
        resultMap.put(String.valueOf(customerDTO.getCustomerShopId()),data);
      }
      result.add(resultMap);
      result.add(searchResultGroupListDTO.getNumberGroups());
      return  result;
    } catch (Exception e) {
      LOG.debug("/autoAccessoryOnline.do?method=getAllRelatedCustomerStock");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(params = "method=getRelatedCustomerStock")
  @ResponseBody
  public Object getRelatedCustomerStock(HttpServletRequest request,SearchConditionDTO searchProductConditionDTO){
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    try {
      searchProductConditionDTO.setWholesalerShopId(WebUtil.getShopId(request));
      if ( searchProductConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchProductConditionDTO.getSearchWord())) {
        searchProductConditionDTO.setSort("inventory_amount desc");
      }
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryCustomerInventoryByCustomerShopId(searchProductConditionDTO);
      Map<Long, ProductMappingDTO> productMappingDTOMap = productService.getCustomerProductMappingDTOMap(productSearchResultListDTO.getRelatedCustomerShopId(),WebUtil.getShopId(request),productSearchResultListDTO.getProductLocalInfoIdList().toArray(new Long[productSearchResultListDTO.getProductLocalInfoIdList().size()]));
      ProductMappingDTO productMappingDTO = null;
      for(ProductDTO productDTO : productSearchResultListDTO.getProducts()){
        productMappingDTO = productMappingDTOMap.get(productDTO.getProductLocalInfoId());
        if(productMappingDTO!=null){
          productDTO.setLastPurchaseAmount(productMappingDTO.getCustomerLastPurchaseAmount());
          productDTO.setLastPurchaseDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY,productMappingDTO.getCustomerLastPurchaseDate()));
          productDTO.setLastPurchasePrice(productMappingDTO.getCustomerLastPurchasePrice());
        }
      }
      return productSearchResultListDTO;
    } catch (Exception e) {
      LOG.debug("/autoAccessoryOnline.do?method=getRelatedCustomerStock");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getShopRelatedCustomer")
  @ResponseBody
  public Object getShopRelatedCustomer(ModelMap modelMap, HttpServletRequest request){
    ICustomerService customerService=ServiceManager.getService(ICustomerService.class);
    try{
      PagingListResult<CustomerDTO> result=new PagingListResult<CustomerDTO>();
      Pager pager=new Pager(customerService.countRelatedCustomersByShopId(WebUtil.getShopId(request)),1);
      result.setPager(pager);
      result.setResults(customerService.getRelatedCustomersByShopId(WebUtil.getShopId(request)));
      return result;
    }catch (Exception e){
      LOG.debug("/autoAccessoryOnline.do?method=getShopRelatedCustomer");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取上架商品 (本店的)
   * @param request
   * @param fromSource
   * @param searchConditionDTO
   * @return
   */
  @RequestMapping(params = "method=getProductInSales")
  @ResponseBody
  public Object getPromotionsProduct(HttpServletRequest request,String fromSource,SearchConditionDTO searchConditionDTO) {
    Long shopId=WebUtil.getShopId(request);
    try {
      searchConditionDTO.setShopId(shopId);
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      if(searchConditionDTO.isEmptyOfProductInfo()){
        searchConditionDTO.setSort("last_in_sales_time desc");//默认上架时间排序
      }
      PagingListResult<ProductDTO> result = new PagingListResult<ProductDTO>();
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithStdQuery(searchConditionDTO);
      Pager pager = new Pager(Integer.valueOf(productSearchResultListDTO.getNumFound() + ""), searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());
      result.setResults(productSearchResultListDTO.getProducts());
      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/autoAccessoryOnline.do?method=getProductInSales");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }
  @RequestMapping(params = "method=saveRecentlyViewedProduct")
  @ResponseBody
  public Object saveRecentlyViewedProduct(HttpServletRequest request,Long viewedProductLocalInfoId, Long productShopId) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      if (viewedProductLocalInfoId == null) throw new Exception("viewedProductLocalInfoId can't be null.");
      IProductService productService = ServiceManager.getService(IProductService.class);
      if (productShopId != null && productShopId.longValue() != shopId.longValue()) {
        productService.saveOrUpdateRecentlyViewedProduct(shopId,WebUtil.getUserId(request), viewedProductLocalInfoId);
      }
      return new Result();
    } catch (Exception e) {
      LOG.debug("/autoAccessoryOnline.do?method=saveRecentlyViewedProduct");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveViewedBusinessChance")
  @ResponseBody
  public Object saveViewedBusinessChance(HttpServletRequest request,Long viewedPreBuyOrderItemId) {
    try {
      Result result=new Result();
      ServiceManager.getService(IRecentlyUsedDataService.class).saveOrUpdateRecentlyUsedData(WebUtil.getShopId(request),WebUtil.getUserId(request),RecentlyUsedDataType.VISITED_BUSINESS_CHANCE,viewedPreBuyOrderItemId);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getViewedBusinessChance")
  @ResponseBody
  public Object getViewedBusinessChance(HttpServletRequest request,Integer maxSize) {
    try {
      Result result=new Result();
      maxSize=NumberUtil.intValue(maxSize,SupplyDemandConstant.LATEST_VIEWED_BUSINESS_CHANGE_NUM);
      List<RecentlyUsedDataDTO> usedDataDTOs=ServiceManager.getService(IRecentlyUsedDataService.class).getRecentlyUsedDataDTOList(WebUtil.getShopId(request), WebUtil.getUserId(request), RecentlyUsedDataType.VISITED_BUSINESS_CHANCE,maxSize);
      if(CollectionUtil.isEmpty(usedDataDTOs)){
        return result;
      }
      List<Long> itemIds=new ArrayList<Long>();
      for (RecentlyUsedDataDTO usedDataDTO:usedDataDTOs){
        itemIds.add(usedDataDTO.getDataId());
      }
      PreBuyOrderSearchCondition condition=new PreBuyOrderSearchCondition();
      condition.setPreBuyOrderItemIds(itemIds.toArray(new Long[itemIds.size()]));
      result.setData(ServiceManager.getService(IPreBuyOrderService.class).getPreBuyOrderItemDetailDTO(condition));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }

  }

}
