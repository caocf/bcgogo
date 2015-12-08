package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.*;
import com.bcgogo.common.CookieUtil;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.constant.crm.productCategory.ProductCategoryConstant;
import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.NormalProductDTO;
import com.bcgogo.product.dto.ProductCategoryRelationDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.model.PromotionsArea;
import com.bcgogo.product.productManage.PromotionSearchCondition;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.dto.CurrentUsedProductDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.supplier.ShopMsgHelper;
import com.bcgogo.txn.dto.HeavyProductDTO;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.PromotionsProductDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
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
 * User: xzhu
 * Date: 12-10-22
 * Time: 下午5:33
 * 商品上下架维护
 */
@Controller
@RequestMapping("/goodsInOffSales.do")
public class GoodsInOffSalesController {
  private static final Logger LOG = LoggerFactory.getLogger(GoodsInOffSalesController.class);
  private static final String OPERATE_IN = "in";
  private static final String OPERATE_OFF = "off";

  private static final String VALIDATE_TYPE_ALL_GOODS_IN_SALES ="all_goods_in_sales";
  private static final String VALIDATE_TYPE_SOME_GOODS_IN_SALES ="some_goods_in_sales";

  @Autowired
  private ShopMsgHelper shopMsgHelper;


  /**
   * 简单的上下架处理
   * @param request
   * @param response
   * @param productIdList
   * @param operate
   * @return
   */
  @RequestMapping(params = "method=goodsInOffSales")
  @ResponseBody
  public Object goodsInOffSales(HttpServletRequest request, HttpServletResponse response, Long[] productIdList,String operate) {
    Long shopId=WebUtil.getShopId(request);
    Result result = null;
    try {
      IProductService productService = ServiceManager.getService(IProductService.class);
      List<Long> productLocalInfoIdList = null;
      if(OPERATE_IN.equals(operate)){
        productLocalInfoIdList = productService.updateProductSalesStatus(shopId,ProductStatus.InSales,productIdList);
        if(productLocalInfoIdList.size()==productIdList.length){
          result = new Result("上架成功！",true);
          ServiceManager.getService(ITradePushMessageService.class).generateSalesMsgTask(shopId,ArrayUtil.toLongArr(productLocalInfoIdList));
        }else{
          result = new Result("友情提示：您选择的部分商品没有填写批发价，该商品没有上架！请您填写批发价后再次上架！",true);
        }
      }else if(OPERATE_OFF.equals(operate)){
        productLocalInfoIdList = productService.updateProductSalesStatus(shopId,ProductStatus.NotInSales,productIdList);
        ServiceManager.getService(IPromotionsService.class).updatePromotionsForGoodsInOff(shopId,ArrayUtil.toLongArr(productLocalInfoIdList));
        result = new Result("下架成功！",true);
      }
      //重做solr索引
      SolrHelper.doProductReindex(shopId,ArrayUtil.toLongArr(productLocalInfoIdList));
      CookieUtil.rebuildCookiesForUserGuide(request, response, true, new String[]{"PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO"});
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=batchSaveGoodsInSales")
  @ResponseBody
  public Object batchSaveGoodsInSales(HttpServletRequest request,HeavyProductDTO heavyProductDTO) {
    Long shopId=WebUtil.getShopId(request);
    Result result = new Result();
    try{
      //update product
      ITxnService txnService=ServiceManager.getService(ITxnService.class);
      txnService.batchSaveGoodsInSales(result,shopId,WebUtil.getUserId(request),heavyProductDTO.getProductDTOs());
      txnService.saveProductCategoryAndRelation(shopId,WebUtil.getUserId(request),heavyProductDTO.getProductDTOs());


      Long [] productIds=ArrayUtil.toLongArr(result.getDataList());
      ServiceManager.getService(IPromotionsService.class).addPromotionsProductForInSales(result,shopId,heavyProductDTO.getProductDTOs());
      //保存上架
      ServiceManager.getService(IProductService.class).updateProductSalesStatus(shopId,ProductStatus.InSales,productIds);
      SolrHelper.doProductReindex(shopId, productIds);
      ServiceManager.getService(ITradePushMessageService.class).generateSalesMsgTask(shopId,ArrayUtil.toLongArr(result.getDataList()));
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  private void prepareForSavePromotions(Result result,Long shopId,ProductDTO productDTO,Long lappingPromotionsId) throws Exception {
    PromotionsDTO promotionsDTO=productDTO.getPromotionsDTO();
    if(promotionsDTO==null){
      return;
    }
    PromotionsProductDTO promotionsProductDTO=promotionsDTO.getPromotionsProductDTO();
    if(promotionsProductDTO==null){
      return;
    }
    promotionsProductDTO.setProductLocalInfoId(productDTO.getProductLocalInfoId());
    if(lappingPromotionsId!=null){
      ServiceManager.getService(IPromotionsService.class).deletePromotionsProduct(result,shopId,lappingPromotionsId,promotionsProductDTO.getProductLocalInfoId());
    }
  }

  private void prepareForSaveProduct(ProductDTO productDTO){
    if (productDTO==null) {
      return;
    }
    productDTO.setStorageUnit(productDTO.getStorageUnit() == null ? null : productDTO.getStorageUnit().trim());
    productDTO.setSellUnit(productDTO.getSellUnit() == null ? null : productDTO.getSellUnit().trim());
    if (StringUtils.isNotBlank(productDTO.getStorageUnit()) && StringUtils.isEmpty(productDTO.getSellUnit())) {
      productDTO.setSellUnit(productDTO.getStorageUnit());
    } else if (StringUtils.isNotBlank(productDTO.getSellUnit()) && StringUtils.isEmpty(productDTO.getStorageUnit())) {
      productDTO.setStorageUnit(productDTO.getSellUnit());
    }
  }
  private Result validateSaveGoodsInSales(Result result,Long shopId,PromotionsDTO promotionsDTO) throws Exception {
    PromotionsProductDTO [] promotionsProductDTOs=promotionsDTO.getPromotionsProductDTOList();
    if(ArrayUtil.isEmpty(promotionsProductDTOs)){
      return result;
    }
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    StringBuffer sb=new StringBuffer();
    for(PromotionsProductDTO p:promotionsProductDTOs){
      if(p==null||p.getProductLocalInfoId()==null) continue;
      List<Long> productIdList=promotionsService.getOverlappingProductIdByRange(shopId, promotionsDTO, false);
      if(productIdList.contains(p.getProductLocalInfoId())){
        ProductDTO productDTO= ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(p.getProductLocalInfoId(),shopId);
        sb.append("商品"+productDTO.getName()+"已参加其他促销，加入活动失败");
        result.setData("lapping");
      }
    }
    if(sb.length()>0){
      result.LogErrorMsg(sb.toString());
    }
    return result;
  }

  @RequestMapping(params = "method=saveGoodsInSales")
  @ResponseBody
  public Object saveGoodsInSales(HttpServletRequest request,ProductDTO fromProductDTO,Long lappingPromotionsId) {
    IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
    Long shopId=WebUtil.getShopId(request);
    Result result = new Result();
    try {
      fromProductDTO.setUserId(WebUtil.getUserId(request));
      fromProductDTO.setShopId(shopId);
//      validateSaveGoodsInSales(result,shopId,fromProductDTO.getPromotionsDTO());
      if(!result.isSuccess()){
        return result;
      }
      //处理fromProductDTO.getDescription() 中的图片
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();
      String description = fromProductDTO.getDescription();
      if(StringUtils.isNotBlank(description)){
        List<String> imageUrlList = StringUtil.getImgStr(description);
        if(CollectionUtils.isNotEmpty(imageUrlList)){
          DataImageRelationDTO dataImageRelationDTO = null;
          String imageUrl = null;
          for(int i=0;i<imageUrlList.size();i++){
            imageUrl = imageUrlList.get(i);
            if(StringUtils.isNotBlank(imageUrl)){
              dataImageRelationDTO = new DataImageRelationDTO(shopId, null, DataType.PRODUCT, ImageType.PRODUCT_DESCRIPTION_IMAGE,i);
              dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,imageUrl.split(ConfigUtils.getUpYunSeparator())[0].replaceAll(ConfigUtils.getUpYunDomainUrl(), "")));//编辑的时候过滤 !version
              dataImageRelationDTOList.add(dataImageRelationDTO);
              description = description.replaceAll(imageUrl, ImageUtils.ImageSrcPlaceHolder+i);
            }
          }
          fromProductDTO.setDescription(description);
        }
      }

      //更新或保存商品
      prepareForSaveProduct(fromProductDTO);
      ITxnService txnService=ServiceManager.getService(ITxnService.class);
      result=txnService.saveOrUpdateProductInSales(result,fromProductDTO);
      if(!result.isSuccess()){
        return result;
      }
      Long productLocalInfoId = NumberUtil.longValue(result.getData());
      if(productLocalInfoId==null){
        return result.LogErrorMsg("保存商品异常。");
      }
      txnService.saveProductCategoryAndRelation(shopId,WebUtil.getUserId(request),fromProductDTO);
      //保存最近使用的分类
      List<CurrentUsedProductDTO> currentUsedProductDTOList = new ArrayList<CurrentUsedProductDTO>();
      CurrentUsedProductDTO cupDTO = new CurrentUsedProductDTO();
      cupDTO.setShopId(fromProductDTO.getShopId());
      cupDTO.setTimeOrder(System.currentTimeMillis());
      cupDTO.setProductName(fromProductDTO.getName());
      cupDTO.setBrand(fromProductDTO.getBrand());
      currentUsedProductDTOList.add(cupDTO);
      productCurrentUsedService.currentUsedProductSaved(shopId, currentUsedProductDTOList);
      prepareForSavePromotions(result,shopId,fromProductDTO, lappingPromotionsId);
      ServiceManager.getService(IPromotionsService.class).addPromotionsProductForInSales(result,shopId,fromProductDTO);
      //保存富文本中图片
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.PRODUCT_DESCRIPTION_IMAGE);
      if(CollectionUtils.isNotEmpty(dataImageRelationDTOList)){
        for(DataImageRelationDTO dataImageRelationDTO:dataImageRelationDTOList){
          dataImageRelationDTO.setDataId(productLocalInfoId);
        }
      }
      //保存商品主图辅图
      imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
      imageTypeSet.add(ImageType.PRODUCT_AUXILIARY_IMAGE);
      if(fromProductDTO.getImageCenterDTO()!=null && CollectionUtils.isNotEmpty(fromProductDTO.getImageCenterDTO().getProductInfoImagePaths())){
        List<String> imageUrlList = fromProductDTO.getImageCenterDTO().getProductInfoImagePaths();
        DataImageRelationDTO dataImageRelationDTO = null;
        int i=0;
        for(String imageUrl:imageUrlList){
          if(StringUtils.isNotBlank(imageUrl)){
            dataImageRelationDTO = new DataImageRelationDTO(shopId,productLocalInfoId, DataType.PRODUCT,i==0?ImageType.PRODUCT_MAIN_IMAGE:ImageType.PRODUCT_AUXILIARY_IMAGE,i);
            dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,imageUrl));
            dataImageRelationDTOList.add(dataImageRelationDTO);
            i++;
          }
        }
      }
      imageService.saveOrUpdateDataImageDTOs(shopId,imageTypeSet,DataType.PRODUCT,productLocalInfoId,dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
      //保存上架
      ServiceManager.getService(IProductService.class).updateProductSalesStatus(shopId,ProductStatus.InSales,productLocalInfoId);
      SolrHelper.doProductReindex(shopId,productLocalInfoId);
      //推送消息
      ServiceManager.getService(ITradePushMessageService.class).generateSalesMsgTask(shopId, productLocalInfoId);
      result.setDataStr(StringUtil.valueOf(productLocalInfoId));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }



  @RequestMapping(params = "method=allGoodsInOffSales")
  @ResponseBody
  public Object allGoodsInOffSales(HttpServletRequest request,String operate) {
    Long shopId=WebUtil.getShopId(request);
    Result result = null;
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      IProductService productService = ServiceManager.getService(IProductService.class);
      List<Long> productLocalInfoIdList = null;
      if(OPERATE_IN.equals(operate)){
        productLocalInfoIdList = productService.startSellAllProductsWithTradePrice(shopId);
        if(productService.countAllProductLocalInfoWithNotTradePriceAndNotInSales(shopId)>0){
          result = new Result("友情提示：部分商品没有填写批发价，该商品没有上架！请您填写批发价后再次上架！",true);
        }else{
          result = new Result("全部上架成功！",true);
        }
        List<PushMessageBuildTaskDTO> pushMessageBuildTaskDTOList = new ArrayList<PushMessageBuildTaskDTO>();
        PushMessageBuildTaskDTO pushMessageBuildTaskDTO = null;
        for(Long plId : productLocalInfoIdList){
          pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
          pushMessageBuildTaskDTO.setSeedId(plId);
          pushMessageBuildTaskDTO.setShopId(shopId);
          pushMessageBuildTaskDTO.setScene(PushMessageScene.ACCESSORY_SALES);
          pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
          pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);
        }
        ServiceManager.getService(ITradePushMessageService.class).savePushMessageBuildTaskDTO(pushMessageBuildTaskDTOList.toArray(new PushMessageBuildTaskDTO[pushMessageBuildTaskDTOList.size()]));

      }else if(OPERATE_OFF.equals(operate)){
        productLocalInfoIdList = productService.stopSellAllProducts(shopId);
        result = new Result("全部下架成功！",true);
      }
      //重做solr索引
      if(CollectionUtils.isNotEmpty(productLocalInfoIdList)){
        ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productLocalInfoIdList.toArray(new Long[productLocalInfoIdList.size()]));
      }
      return result;
    } catch (Exception e) {
      LOG.debug("/goodsInOffSales.do");
      LOG.debug("method=allGoodsInOffSales");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=toGoodsInSalesEditor")
  public String toGoodsInOffSalesEditor(ModelMap modelMap,HttpServletRequest request,Long productId) {
    try{
      Long shopId = WebUtil.getShopId(request);
      if(shopId==null) throw new Exception("shopId is null!");
      modelMap.put("productId",productId);
      modelMap.put("shop",ServiceManager.getService(IShopService.class).getShopAreaInfo(WebUtil.getShopId(request)));
      modelMap.put("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));
      modelMap.put("usedProductCategoryDTOList", ServiceManager.getService(IProductCategoryService.class).getRecentlyUsedProductCategoryDTOList(shopId,WebUtil.getUserId(request)));
    }catch (Exception e){
      LOG.error("/goodsInOffSales.do?method=toGoodsInSalesEditor");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/goodsInSalesEditor";
  }

  @RequestMapping(params = "method=getProductDetail")
  @ResponseBody
  public Object getProductDetail(ModelMap modelMap,HttpServletRequest request,Long productId) {
    IProductService productService=ServiceManager.getService(IProductService.class);
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    IProductCategoryService productCategoryService=ServiceManager.getService(IProductCategoryService.class);
    try {
      Long shopId=WebUtil.getShopId(request);
      ProductDTO productDTO=CollectionUtil.getFirst(productService.getProductDTOByProductLocalInfoIds(shopId,productId));
      if(productDTO==null){
        return null;
      }
      productDTO.setInventoryDTO(CollectionUtil.getFirst(txnService.getInventoryByShopIdAndProductIds(shopId, productId)));
      PromotionsProductDTO promotionsProductDTO=CollectionUtil.getFirst(promotionsService.getPromotionsProductDTOByProductIds(shopId,productDTO.getProductLocalInfoId()));
      if(promotionsProductDTO!=null){
        productDTO.setPromotionsId(promotionsProductDTO.getPromotionsId());
      }
      ProductCategoryRelationDTO productCategoryRelationDTO = CollectionUtil.getFirst(productCategoryService.productCategoryRelationDTOQuery(shopId, productDTO.getProductLocalInfoId()));
      if(productCategoryRelationDTO!=null){
        ProductCategoryDTO productCategoryDTO = CollectionUtil.getFirst(productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(Arrays.asList(new Long[]{productCategoryRelationDTO.getProductCategoryId()}))));
        productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
        productDTO.setProductCategoryDTO(productCategoryDTO);
      }
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_SMALL);
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_BIG);
      imageSceneList.add(ImageScene.PRODUCT_INFO_DESCRIPTION_IMAGE);
      imageService.addImageInfoToProductDTO(imageSceneList,false,productDTO);
      List<DataImageDetailDTO> productInfoDescriptionImageDetailDTOs = productDTO.getImageCenterDTO().getProductInfoDescriptionImageDetailDTOs();
      String description = productDTO.getDescription();
      if (CollectionUtils.isNotEmpty(productInfoDescriptionImageDetailDTOs) && StringUtils.isNotBlank(description)) {
        for (int i = 0; i < productInfoDescriptionImageDetailDTOs.size(); i++) {
          description = description.replaceAll(ImageUtils.ImageSrcPlaceHolder + i, productInfoDescriptionImageDetailDTOs.get(i).getImageURL());
        }
        productDTO.setDescription(description);
      }
      return productDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=toBatchGoodsInSalesEditor")
  public String toBatchGoodsInSalesEditor(ModelMap modelMap, HttpServletRequest request,Long[] productIdList) {
    IProductService productService=ServiceManager.getService(IProductService.class);
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    try {
      Long shopId=WebUtil.getShopId(request);
      List<ProductDTO> productDTOs=productService.getProductDTOByProductLocalInfoIds(shopId, productIdList);
      if(CollectionUtil.isNotEmpty(productDTOs)){

        Map<Long,Long> productCategoryRelationMap = productCategoryService.getProductCategoryRelationMap(shopId, productIdList);
        if(MapUtils.isNotEmpty(productCategoryRelationMap)){
          List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(productCategoryRelationMap.values()));
          productCategoryService.fillProductCategoryDTOListInfo(productCategoryDTOList);
          Map<Long,ProductCategoryDTO> productCategoryDTOMap = new HashMap<Long, ProductCategoryDTO>();
          if(CollectionUtils.isNotEmpty(productCategoryDTOList)){
            for(ProductCategoryDTO productCategoryDTO : productCategoryDTOList){
              productCategoryDTOMap.put(productCategoryDTO.getId(),productCategoryDTO);
            }
          }
          for(ProductDTO productDTO:productDTOs){
            Long productCategoryId = productCategoryRelationMap.get(productDTO.getProductLocalInfoId());
            if(productCategoryId!=null){
              productDTO.setProductCategoryDTO(productCategoryDTOMap.get(productCategoryId));
            }
          }
        }
        List<InventoryDTO> inventoryDTOs=txnService.getInventoryByShopIdAndProductIds(shopId, productIdList);
        Map<Long,InventoryDTO> inventoryDTOMap=new HashMap<Long, InventoryDTO>();
        if(CollectionUtil.isNotEmpty(inventoryDTOs)){
          for(InventoryDTO inventoryDTO:inventoryDTOs){
            inventoryDTOMap.put(inventoryDTO.getId(),inventoryDTO);
          }
          InventoryDTO inventoryDTOTemp=null;
          for(ProductDTO productDTO:productDTOs){
            inventoryDTOTemp=inventoryDTOMap.get(productDTO.getProductLocalInfoId());
            if(inventoryDTOTemp!=null){
              productDTO.setInventoryNum(inventoryDTOTemp.getAmount());
              productDTO.setRecommendedPrice(NumberUtil.round(inventoryDTOTemp.getSalesPrice()));
              productDTO.setInventoryAveragePrice(NumberUtil.round(inventoryDTOTemp.getInventoryAveragePrice()));
            }
          }
        }
        IImageService imageService = ServiceManager.getService(IImageService.class);
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        imageService.addImageInfoToProductDTO(imageSceneList,false,productDTOs.toArray(new ProductDTO[productDTOs.size()]));
      }
      modelMap.put("products",productDTOs);
      modelMap.put("shop",ServiceManager.getService(IShopService.class).getShopAreaInfo(WebUtil.getShopId(request)));
      modelMap.put("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));
      modelMap.put("usedProductCategoryDTOList", ServiceManager.getService(IProductCategoryService.class).getRecentlyUsedProductCategoryDTOList(shopId,WebUtil.getUserId(request)));
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return "/autoaccessoryonline/batchGoodsInSalesEditor";
  }

  private void generatePromotionAreaInfo(PromotionsDTO promotionsDTO){
    if(promotionsDTO==null){
      return;
    }
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    PromotionsEnum.PromotionsTypes promotionsTypes=promotionsDTO.getType();
    switch (promotionsTypes){
      case BARGAIN: LOG.info("bargain");
      case FREE_SHIPPING:{
        List<PromotionsArea> pAreaList=promotionsService.getPromotionsAreaByPromotionsId(promotionsDTO.getId()).get(promotionsDTO.getId());
        if(CollectionUtil.isEmpty(pAreaList)){
          return;
        }
        PromotionsArea fArea=CollectionUtil.getFirst(pAreaList);
        promotionsDTO.setPostType(fArea.getPostType());
        if(fArea.getAreaNo()==-1){
          AreaDTO areaDTO=new AreaDTO();
          areaDTO.setNo(-1l);
          areaDTO.setName("全国");
          AreaDTO[] areaDTOs=new AreaDTO[1];
          areaDTOs[0]=areaDTO;
          promotionsDTO.setAreaDTOs(areaDTOs);
          return;
        }
        List<Long> noList=new ArrayList<Long>();
        for (PromotionsArea pArea:pAreaList){
          noList.add(pArea.getAreaNo());
        }
        List<AreaDTO> areaDTOs=AreaCacheManager.getAreaDTOListByNo(ArrayUtil.toLongArr(noList));
        if(CollectionUtil.isNotEmpty(areaDTOs)){
          Set<Long> provinceSet=new HashSet<Long>();
          for (AreaDTO areaDTO:areaDTOs){
            if(areaDTO==null){
              continue;
            }
            if(provinceSet.contains(areaDTO.getParentNo())){
              continue;
            }
            provinceSet.add(areaDTO.getParentNo());
          }
          areaDTOs=AreaCacheManager.getAreaDTOListByNo(ArrayUtil.toLongArr(provinceSet));
          promotionsDTO.setAreaDTOs(areaDTOs.toArray(new AreaDTO[areaDTOs.size()]));
        }
      }
    }
  }

  @RequestMapping(params = "method=toPreviewShopProductDetail")
  public String toPreviewShopProductDetail(ModelMap modelMap,HttpServletRequest request,ProductDTO productDTO){
    Long shopId=WebUtil.getShopId(request);
    productDTO.generateProductInfo();
    modelMap.put("product", productDTO);

    modelMap.addAttribute("paramShopId", shopId);
    //店铺信息
    ShopDTO shopDTO=shopMsgHelper.getShopMsgBasic(shopId, shopId);
    shopDTO.setCommentStatDTO(ServiceManager.getService(ISupplierCommentService.class).getShopCommentStat(shopId));
    modelMap.addAttribute("shopDTO",shopDTO);
    // 分类信息
    if(productDTO.getProductCategoryId()!=null){
      IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
      ProductCategoryDTO productCategoryDTO = CollectionUtil.getFirst(productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(Arrays.asList(new Long[]{productDTO.getProductCategoryId()}))));
      productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
      productDTO.setProductCategoryDTO(productCategoryDTO);
    }else{
      productDTO.setProductCategoryInfo(ProductCategoryConstant.CUSTOM_FIRST_CATEGORY_NAME+" >> "+productDTO.getProductCategoryName());
    }
    //促销信息
    PromotionsDTO fromPromotionsDTO=productDTO.getPromotionsDTO();
    if(fromPromotionsDTO!=null&&fromPromotionsDTO.getId()!=null){
      IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
      PromotionsDTO promotionsDTO=CollectionUtil.getFirst(promotionsService.getPromotionDetail(shopId, fromPromotionsDTO.getId()));
      if(promotionsDTO!=null){
        promotionsDTO.setPromotionsProductDTO(fromPromotionsDTO.getPromotionsProductDTO());
        generatePromotionAreaInfo(promotionsDTO);
        List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
        promotionsDTOs.add(promotionsDTO);
        productDTO.setPromotionsDTOs(promotionsDTOs);
      }
    }
    //图片相关
    modelMap.addAttribute("notFindImageURL_200X200", ImageUtils.generateNotFindImageUrl() + ConfigUtils.getUpYunSeparator() + ImageScene.PRODUCT_INFO_IMAGE_BIG.getImageVersion());
    if(productDTO.getImageCenterDTO()!=null && CollectionUtils.isNotEmpty(productDTO.getImageCenterDTO().getProductInfoImagePaths())){
      Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = new HashMap<Long, Map<ImageScene, List<DataImageDetailDTO>>>();
      List<String> imageUrlList = productDTO.getImageCenterDTO().getProductInfoImagePaths();
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_SMALL);
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_BIG);

      List<DataImageDetailDTO> dataImageDetailDTOList = null;
      DataImageRelationDTO dataImageRelationDTO = null;
      ImageInfoDTO imageInfoDTO = null;
      for(int i=0;i<imageUrlList.size();i++){
        if(StringUtils.isNotBlank(imageUrlList.get(i))){
          for(ImageScene imageScene:imageSceneList){
            dataImageRelationDTO = new DataImageRelationDTO(shopId, productDTO.getProductLocalInfoId(), DataType.PRODUCT,i==0?ImageType.PRODUCT_MAIN_IMAGE:ImageType.PRODUCT_AUXILIARY_IMAGE,i);
            imageInfoDTO = new ImageInfoDTO(shopId,imageUrlList.get(i));
            if(ImageType.getImageTypeListByImageScene(imageScene).contains(dataImageRelationDTO.getImageType())){
              Map<ImageScene,List<DataImageDetailDTO>> sceneMap = imageMap.get(dataImageRelationDTO.getDataId());
              if(sceneMap==null){
                sceneMap = new HashMap<ImageScene,List<DataImageDetailDTO>>();
                dataImageDetailDTOList = new ArrayList<DataImageDetailDTO>();
              }else{
                dataImageDetailDTOList = sceneMap.get(imageScene);
                if(dataImageDetailDTOList==null){
                  dataImageDetailDTOList = new ArrayList<DataImageDetailDTO>();
                }
              }
              dataImageDetailDTOList.add(new DataImageDetailDTO(dataImageRelationDTO, ImageUtils.generateUpYunImagePath(imageInfoDTO.getPath(), imageScene.getImageVersion()),imageInfoDTO.getPath()));
              sceneMap.put(imageScene,dataImageDetailDTOList);
              imageMap.put(dataImageRelationDTO.getDataId(),sceneMap);
            }
          }
        }
      }
      productDTO.setImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(productDTO.getProductLocalInfoId()),false));
    }

    return "/autoaccessoryonline/previewShopProductDetail";
  }

  @RequestMapping(params = "method=toInSalingGoodsList")
  public String toInSalingGoodsList(ModelMap modelMap, HttpServletRequest request) {
    modelMap.put("paramShopId",WebUtil.getShopId(request));
    modelMap.put("promotionsTypeList",request.getParameter("promotionsTypeList") == null ? "" : request.getParameter("promotionsTypeList"));
    modelMap.put("shop",ServiceManager.getService(IShopService.class).getShopAreaInfo(WebUtil.getShopId(request)));
    return "/autoaccessoryonline/inSalingGoodsList";
  }

  @RequestMapping(params = "method=toUnInSalingGoodsList")
  public String toUnInSalingGoodsList(ModelMap modelMap, HttpServletRequest request) {
    return "/autoaccessoryonline/unInSalingGoodsList";
  }

  @RequestMapping(params = "method=toGoodsInSalesFinish")
  public String toGoodsInSalesFinish(ModelMap modelMap, HttpServletRequest request) {
    modelMap.put("paramShopId",WebUtil.getShopId(request));
    modelMap.put("productIds",request.getParameter("productIds"));
    modelMap.put("fromSource",request.getParameter("fromSource"));
    return "/autoaccessoryonline/goodsInSalesFinish";
  }

  @RequestMapping(params = "method=getStockProductStat")
  @ResponseBody
  public Object getStockProductStat(ModelMap model, HttpServletRequest request) {
    IProductService productService=ServiceManager.getService(IProductService.class);
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    Long shopId=WebUtil.getShopId(request);
    Map sockStatMap=new HashMap();
    int allStockProduct = productService.countAllStockProduct(shopId);
    int productInSales = productService.countProductInSales(shopId);
    int productUnInSale = allStockProduct - productInSales;
    //统计库存中所有商品
    sockStatMap.put("allStockProductNum",allStockProduct);
    //统计上架的商品
    sockStatMap.put("productInSalesNum",productInSales);
    //统计未上架的商品
    sockStatMap.put("productUnInSaleNum",productUnInSale);
    //统计正在进行中的商品
    PromotionSearchCondition condition=new PromotionSearchCondition();
    condition.setShopId(shopId);
    List<PromotionsEnum.PromotionStatus> promotionStatusList = new ArrayList<PromotionsEnum.PromotionStatus>();
    promotionStatusList.add(PromotionsEnum.PromotionStatus.USING);
    promotionStatusList.add(PromotionsEnum.PromotionStatus.UN_STARTED);
    promotionStatusList.add(PromotionsEnum.PromotionStatus.SUSPEND);
    condition.setPromotionStatusList(promotionStatusList);
    List<PromotionsProductDTO> promotionsProductDTOs=promotionsService.getPromotionsProductDTO(condition);
    if(CollectionUtil.isNotEmpty(promotionsProductDTOs)){
      Set<Long> productIds=new HashSet<Long>();
      for(PromotionsProductDTO dto:promotionsProductDTOs){
        if(dto.getProductLocalInfoId()==null){
          continue;
        }
        if(productIds.contains(dto.getProductLocalInfoId())){
          continue;
        }
        productIds.add(dto.getProductLocalInfoId());
      }
      sockStatMap.put("promotionsProductNum", productService.countProductByPromotions(shopId,ArrayUtil.toLongArr(productIds)));
    }
    return sockStatMap;
  }

  @RequestMapping(params = "method=validateGoodsInSales")
  @ResponseBody
  public Object validateGoodsInSales(HttpServletRequest request,String validateType,Long[] productLocalInfoId) {
    Long shopId=WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      if(VALIDATE_TYPE_ALL_GOODS_IN_SALES.equals(validateType)){
        IProductService productService = ServiceManager.getService(IProductService.class);
        List<ProductLocalInfoDTO> productLocalInfoDTOList = productService.getAllProductLocalInfoWithTradePriceAndNotInSales(shopId);
        if(CollectionUtils.isEmpty(productLocalInfoDTOList)){
          return new Result("对不起,没有可以上架的商品,请检查批发价!",false);
        }
        List<Long> productLocalInfoIdList = new ArrayList<Long>();
        for(ProductLocalInfoDTO productLocalInfoDTO : productLocalInfoDTOList){
          productLocalInfoIdList.add(productLocalInfoDTO.getId());
        }
        productLocalInfoId = productLocalInfoIdList.toArray(new Long[productLocalInfoIdList.size()]);
      }
      if(!inventoryService.checkProductTradePriceAndInventoryAveragePriceByProductLocalInfoId(shopId,productLocalInfoId)){
        return new Result(false,Result.Operation.CONFIRM);
      }
      return new Result();
    } catch (Exception e) {
      LOG.debug("/goodsInOffSales.do");
      LOG.debug("method=validateGoodsInSales");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getBcgogoRecommendPrice")
  @ResponseBody
  public Object getBcgogoRecommendPrice(HttpServletRequest request){
    Result result=new Result();
    try {
      NormalProductDTO productDTO=null;
      Long normalProductId= NumberUtil.longValue(request.getAttribute("normalProductId"));
      if(normalProductId==null){
        return result.LogErrorMsg("数据异常！");
      }
      productDTO= ServiceManager.getService(IProductService.class).getNormalProductById(normalProductId)  ;
      if(productDTO!=null||NumberUtil.doubleVal(productDTO.getPrice())>0){
        result.setData(NumberUtil.doubleVal(productDTO.getPrice()));
        result.setSuccess(true);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return result;
  }
}
