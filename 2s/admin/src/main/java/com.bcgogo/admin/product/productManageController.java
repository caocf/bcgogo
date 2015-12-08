package com.bcgogo.admin.product;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.NormalProductConstants;
import com.bcgogo.constant.ShopProductConstants;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.Product.SearchInputType;
import com.bcgogo.enums.ProductModifyFields;
import com.bcgogo.product.NormalProductModifyRecordDTO;
import com.bcgogo.product.NormalProductModifyScene;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.model.*;
import com.bcgogo.product.productManage.ProductInfoDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-24
 * Time: 下午3:55
 */
@Controller
@RequestMapping("/productManage.do")
public class productManageController {
  private static final Logger LOG = LoggerFactory.getLogger(productManageController.class);

  private IProductService productService;

  @RequestMapping(params = "method=getNormalProducts")
  @ResponseBody
  public Map getNormalProducts(HttpServletRequest request, HttpServletResponse response, ProductSearchCondition searchConditionDTO) {
    IProductService productService = ServiceManager.getService(IProductService.class);

    int count = productService.countNormalProductDTO(searchConditionDTO);
    List<NormalProductDTO> normalProductDTOList = productService.getNormalProductDTO(searchConditionDTO);

    Map map = new HashMap();

    map.put("result", normalProductDTOList);
    map.put("totalRows", count);
    return map;
  }

  @RequestMapping(params = "method=deleteNormalProduct")
  @ResponseBody
  public Object deleteNormalProduct(HttpServletRequest request, HttpServletResponse response) {
    String idStr = request.getParameter("id");
    Long userId = WebUtil.getUserId(request);
    Map map = new HashMap();

    if (StringUtils.isBlank(idStr)) {
      map.put("result", "error");
      map.put("errorMsg", NormalProductConstants.PRODUCT_NO_ID_ERROR);
      return map;
    }

    Long id = Long.valueOf(idStr);
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);

//    List<Product> productList = productService.getProductByNormalProductId(id);

//    if (CollectionUtils.isNotEmpty(productList)) {
//      map.put("result", "error");
//      map.put("errorMsg", NormalProductConstants.PRODUCT_DELETE_CHECK_SHOP);
//      return map;
//    }

    try {
      List<NormalProductModifyRecordDTO> normalProductModifyRecordDTOList = new ArrayList<NormalProductModifyRecordDTO>();
      List<Long> shopProductIdList = new ArrayList<Long>();
      productService.deleteNormalProduct(id, shopProductIdList);
      normalProductModifyRecordDTOList.add(new NormalProductModifyRecordDTO(userId, id, null, NormalProductModifyScene.DELETE));
      if (CollectionUtils.isNotEmpty(shopProductIdList)) {
        for (Long shopProductId : shopProductIdList) {
          normalProductModifyRecordDTOList.add(new NormalProductModifyRecordDTO(userId, id, shopProductId, NormalProductModifyScene.DELETE_RELEVANCE));
          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(shopProductId, null);
          if (null != productLocalInfoDTO) {
            productSolrWriterService.createProductSolrIndex(productLocalInfoDTO.getShopId(), productLocalInfoDTO.getId());
          }
        }
      }
      productService.saveNormalProductModifyRecord(normalProductModifyRecordDTOList.toArray(new NormalProductModifyRecordDTO[normalProductModifyRecordDTOList.size()]));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      map.put("result", "error");
      map.put("errorMsg", NormalProductConstants.PRODUCT_DELETE_ERROR);
    }
    map.put("result", "success");
    return map;
  }

  @RequestMapping(params = "method=updateNormalProduct")
  @ResponseBody
  public Map updateNormalProduct(HttpServletRequest request, HttpServletResponse response) {
    //
    String idStr = request.getParameter("id");

    Map map = new HashMap();

    if (StringUtils.isBlank(idStr)) {
      map.put("result", "error");
      return map;
    }

    Long id = Long.valueOf(idStr);

    IProductService productService = ServiceManager.getService(IProductService.class);
    List<Product> productList = productService.getProductByNormalProductId(id);

    if (CollectionUtils.isEmpty(productList)) {
      map.put("result", "success");
    } else {
      map.put("result", "error");
    }

    return map;
  }

  @RequestMapping(params = "method=checkNormalProductCondition")
  @ResponseBody
  public Map checkNormalProductCondition(HttpServletRequest request, HttpServletResponse response) {
    String idStr = request.getParameter("id");

    Map map = new HashMap();

    if (StringUtils.isBlank(idStr)) {
      map.put("result", "error");
      return map;
    }

    Long id = Long.valueOf(idStr);

    IProductService productService = ServiceManager.getService(IProductService.class);
    List<Product> productList = productService.getProductByNormalProductId(id);

    if (CollectionUtils.isEmpty(productList)) {
      map.put("result", "success");
    } else {
      map.put("result", "error");
    }

    return map;
  }


  @RequestMapping(params = "method=getDataByQueryBuilder")
  @ResponseBody
  public Object getDataByQueryBuilder(HttpServletRequest request, HttpServletResponse response, ProductSearchCondition searchCondition) {
    IProductService productService = ServiceManager.getService(IProductService.class);

    searchCondition.setLimit(15);
    if (SearchInputType.PRODUCT_NAME.toString().equals(searchCondition.getInputName())) {
      //如果前面有一级分类，没有二级分类，则返回null

      //如果前面有一级分类有二级分类，根据2级分类和productName去后太模糊匹配

      //如果没有一级分类，则根据productName去后太模糊匹配

      List<ProductCategoryDTO> productCategoryDTOList = productService.getThirdCategoryByCondition(searchCondition);
      return productCategoryDTOList;
    }
    searchCondition.clearByInputType();
    List<String> suggestionStringList = productService.getNormalProductByCondition(searchCondition);
    List<Map<String,String>> result = new ArrayList<Map<String, String>>();
    if(CollectionUtils.isNotEmpty(suggestionStringList)){
      for(String str:suggestionStringList){
        Map<String,String> map = new HashMap<String, String>();
        map.put("key",str);
        result.add(map);
      }
    }

    return result;
  }

  @RequestMapping(params = "method=checkNormalProductRepeat")
  @ResponseBody
  public Map checkNormalProductRepeat(HttpServletRequest request, HttpServletResponse response, NormalProductDTO normalProductDTO) {


    Map map = new HashMap();

    return map;
  }

  @RequestMapping(params = "method=saveOrUpdateNormalProduct")
  @ResponseBody
  public Object saveOrUpdateNormalProduct(HttpServletRequest request, HttpServletResponse response, NormalProductDTO normalProductDTO) {
    Long userId = WebUtil.getUserId(request);
    Map map = new HashMap();
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    //1,校验成品编码是否相同，
    if(StringUtils.isNotBlank(normalProductDTO.getCommodityCode())){
      String result = productService.checkNormalProductCommodityCodeRepeat(normalProductDTO.getId(), normalProductDTO.getCommodityCode());

      if (StringUtils.isNotBlank(result)) {
        map.put("result", "error");
        map.put("errorMsg", result);
        return map;
      }
    }

    //2，校验产品是否相同
    String result2 = productService.checkNormalProductRepeat(normalProductDTO);
    if (StringUtils.isNotBlank(result2)) {
      map.put("result", "error");
      map.put("errorMsg", result2);
      return map;
    }

    //3.保存产品
    try {
      List<NormalProductModifyRecordDTO> normalProductModifyRecordDTOList = new ArrayList<NormalProductModifyRecordDTO>();
      NormalProductModifyScene scene = NormalProductModifyScene.ADD;
      if(normalProductDTO.getId()!=null){
        scene = NormalProductModifyScene.MODIFY;
      }
      productService.saveOrUpdateNormalProduct(normalProductDTO);
      normalProductModifyRecordDTOList.add(new NormalProductModifyRecordDTO(userId, normalProductDTO.getId(), null, scene));
      if ("shopProduct".equals(request.getParameter("scene"))) {
        String ids = request.getParameter("shopProductIds");
        String[] idStrs = ids.split(",");
        List<Long> shopProductIds = new ArrayList<Long>();

        for (String s : idStrs) {
          if (StringUtils.isBlank(s)) {
            continue;
          }
          shopProductIds.add(Long.valueOf(s));
        }
        productService.relevanceProduct(shopProductIds, normalProductDTO.getId());

        if (CollectionUtils.isNotEmpty(shopProductIds)) {
          for (Long id : shopProductIds) {
            normalProductModifyRecordDTOList.add(new NormalProductModifyRecordDTO(userId,normalProductDTO.getId(),id,NormalProductModifyScene.RELEVANCE));
            ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(id, null);
            if (null != productLocalInfoDTO) {
              productSolrWriterService.createProductSolrIndex(productLocalInfoDTO.getShopId(), productLocalInfoDTO.getId());
            }
          }
        }
      }
      productService.saveNormalProductModifyRecord(normalProductModifyRecordDTOList.toArray(new NormalProductModifyRecordDTO[normalProductModifyRecordDTOList.size()]));
      map.put("result", "success");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      map.put("result", "error");
      map.put("errorMsg", NormalProductConstants.PRODUCT_SAVE_ERROR);
    }

    return map;
  }

  @RequestMapping(params = "method=getFirstCategory")
  @ResponseBody
  public Object getFirstCategory(HttpServletRequest request) {
    IProductService productService = ServiceManager.getService(IProductService.class);

    return productService.getFirstProductCategory();
  }

  @RequestMapping(params = "method=getSecondCategory")
  @ResponseBody
  public Object getSecondCategory(HttpServletRequest request) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    Long shopId = 0L;
    String idStr = request.getParameter("parentId");

    if (StringUtils.isBlank(idStr)) {
      return null;
    }
    return productCategoryService.getSecondCategoryDTOByParentId(shopId, Long.valueOf(idStr));

  }

  @RequestMapping(params = "method=getThirdCategory")
  @ResponseBody
  public Object getThirdCategory(HttpServletRequest request, HttpServletResponse response, ProductSearchCondition searchCondition) {
    IProductService productService = ServiceManager.getService(IProductService.class);


    List<ProductCategoryDTO> productCategoryDTOList = productService.getThirdCategoryByCondition(searchCondition);
    return productCategoryDTOList;

  }

  @RequestMapping(params = "method=getParentCategoryById")
  @ResponseBody
  public Object getParentCategoryById(HttpServletRequest request, HttpServletResponse response) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    Map map = new HashMap();

    String idStr = request.getParameter("id");

    if (StringUtils.isBlank(idStr)) {
      return map;
    }

    Long id = Long.valueOf(idStr);

    ProductCategoryDTO thirdCategoryDTO = productCategoryService.getProductCategoryDTOById(null, id);

    if (thirdCategoryDTO != null) {
      ProductCategoryDTO secondCategoryDTO = productCategoryService.getProductCategoryDTOById(null, thirdCategoryDTO.getParentId());
      if (null != secondCategoryDTO) {
        map.put("secondCategory", secondCategoryDTO);
        ProductCategoryDTO firstCategoryDTO = productCategoryService.getProductCategoryDTOById(null, secondCategoryDTO.getParentId());
        map.put("firstCategory", firstCategoryDTO);
      }
    }

    return map;
  }

  @RequestMapping(params = "method=getShopProducts")
  @ResponseBody
  public Object getShopProducts(HttpServletRequest request, HttpServletResponse response, SearchConditionDTO searchConditionDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    int totalRows = 0;
    Map map = new HashMap();
    List<ProductDTO> productDTOList = null;
    //标准化商品界面弹出的店铺产品信息
    if (null != searchConditionDTO && null != searchConditionDTO.getNormalProductId()) {
      totalRows = productService.countShopProductsByNormalProductId(searchConditionDTO.getNormalProductId());
      productDTOList = productService.getShopProductsByNormalProductId(searchConditionDTO.getNormalProductId(), searchConditionDTO.getPage(), searchConditionDTO.getLimit());
      if(CollectionUtils.isNotEmpty(productDTOList)){
        List<Long> productIdList = new ArrayList<Long>();
        for(ProductDTO productDTO:productDTOList){
          productIdList.add(productDTO.getProductLocalInfoId());
        }
        Map<Long,InventoryDTO> inventoryDTOMap = ServiceManager.getService(IInventoryService.class).getInventoryDTOMapByProductIds(productIdList.toArray(new Long[productIdList.size()]));
        for(ProductDTO productDTO:productDTOList){
          InventoryDTO inventoryDTO = inventoryDTOMap.get(productDTO.getProductLocalInfoId());
          productDTO.setInventoryAveragePrice(inventoryDTO==null?null:inventoryDTO.getInventoryAveragePrice());
        }
      }
    } else {
      searchConditionDTO.setIncludeBasic(false);
      searchConditionDTO.setRows(searchConditionDTO.getLimit());
      searchConditionDTO.setStart((searchConditionDTO.getPage() - 1) * searchConditionDTO.getLimit());
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_NORMAL_PRODUCT});
      if (StringUtils.isBlank(searchConditionDTO.getSort()) && searchConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
        searchConditionDTO.setSort(TxnConstant.sortCommandMap.get("nameAsc"));
      } else {
        searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSort()));
      }
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      //对应field库存查询
      //不知道field的情况下
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
      totalRows = Integer.valueOf(String.valueOf(productSearchResultListDTO!=null?productSearchResultListDTO.getNumFound():0));
      productDTOList = productSearchResultListDTO != null ? productSearchResultListDTO.getProducts() : null;
//      if (CollectionUtils.isNotEmpty(productDTOList)) {
//        productService.completeShopName(productDTOList);
//      }
    }

    if (CollectionUtils.isNotEmpty(productDTOList)) {
      List<Long> unCheckedProductIdList = new ArrayList<Long>();
      Set<Long> normalProductIdSet = new HashSet<Long>();
      for (ProductDTO productDTO : productDTOList) {
        productDTO.setHideExpander(true);
        if(ProductRelevanceStatus.UN_CHECKED.equals(productDTO.getRelevanceStatus())){
          unCheckedProductIdList.add(productDTO.getProductLocalInfoId());
        }
        if (productDTO.getNormalProductId()!=null) {
          normalProductIdSet.add(productDTO.getNormalProductId());
        }
      }
      Map<Long,List<ProductModifyFields>> productModifyFieldsMap = ServiceManager.getService(ITxnService.class).getRelevanceStatusUnCheckedProductModifiedFieldsMap(unCheckedProductIdList.toArray(new Long[unCheckedProductIdList.size()]));
      List<NormalProductDTO> normalProductDTOList = productService.getNormalProductDTOByIds(new ArrayList<Long>(normalProductIdSet));
      Map<Long, NormalProductDTO> normalProductDTOMap = NormalProductDTO.listToMap(normalProductDTOList);
      for (ProductDTO productDTO : productDTOList) {
        productDTO.setNormalProductProperty(normalProductDTOMap.get(productDTO.getNormalProductId()));
        productDTO.setProductModifyFieldsList(productModifyFieldsMap.get(productDTO.getProductLocalInfoId()));
      }
    }
    map.put("totalRows", totalRows);
    map.put("result", productDTOList);
    return map;
  }

  @RequestMapping(params = "method=checkRelevance")
  @ResponseBody
  public Object checkRelevance(HttpServletRequest request, HttpServletResponse response) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    Map map = new HashMap();
    String idStr = request.getParameter("id");
    if (StringUtils.isBlank(idStr)) {
      map.put("result", "error");
      map.put("errorMsg", NormalProductConstants.PRODUCT_NO_ID_ERROR);
      return map;
    }
    Long id = Long.valueOf(idStr);
    try {
      Product product = productService.getProductById(id);
      if(product.getNormalProductId()==null){
        productService.deleteRelevance(id);
        map.put("result", "error");
        map.put("errorMsg", NormalProductConstants.PRODUCT_NO_ID_ERROR);
        return map;
      }
      productService.checkRelevance(id);
      productService.saveNormalProductModifyRecord(WebUtil.getUserId(request),product.getNormalProductId(),product.getId(),NormalProductModifyScene.CONFIRM_UNCHECKED_RELEVANCE);
      map.put("result", "success");
      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(id, product.getShopId());
      if (null != productLocalInfoDTO) {
        ServiceManager.getService(ITxnService.class).updateProductModifyLogDTORelevanceStatus(productLocalInfoDTO.getId(),ProductRelevanceStatus.YES);
        productSolrWriterService.createProductSolrIndex(productLocalInfoDTO.getShopId(), productLocalInfoDTO.getId());
      }
    } catch (Exception e) {
      LOG.error(e.getMessage());
      LOG.debug("/productManage.do");
      LOG.debug("method=checkRelevance");
      map.put("result", "error");
      map.put("errorMsg", NormalProductConstants.PRODUCT_CHECK_RELEVANCE_ERROR);
    }
    return map;
  }
  @RequestMapping(params = "method=deleteRelevance")
  @ResponseBody
  public Object deleteRelevance(HttpServletRequest request, HttpServletResponse response) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    Map map = new HashMap();
    String idStr = request.getParameter("id");
    if (StringUtils.isBlank(idStr)) {
      map.put("result", "error");
      map.put("errorMsg", NormalProductConstants.PRODUCT_NO_ID_ERROR);
      return map;
    }

    Long id = Long.valueOf(idStr);

    try {
      Product product = productService.getProductById(id);
      Long normalProductId = product.getNormalProductId();
      productService.deleteRelevance(id);
      productService.saveNormalProductModifyRecord(WebUtil.getUserId(request),normalProductId,id,NormalProductModifyScene.DELETE_RELEVANCE);

      map.put("result", "success");

      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(id, null);
      if (null != productLocalInfoDTO) {
        productSolrWriterService.createProductSolrIndex(productLocalInfoDTO.getShopId(), productLocalInfoDTO.getId());
      }

    } catch (Exception e) {
      LOG.debug("/productManage.do");
      LOG.debug("method=deleteRelevance");
      LOG.error(e.getMessage());
      map.put("result", "error");
      map.put("errorMsg", NormalProductConstants.PRODUCT_DELETE_RELEVANCE_ERROR);
    }

    return map;
  }

  @RequestMapping(params = "method=getVehicleBrand")
  @ResponseBody
  public Object getVehicleBrand(HttpServletRequest request, HttpServletResponse response) {
    String keyWord = request.getParameter("keyWord");
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = StandardBrandModelCache.getShopVehicleBrandModelDTOList();
    List<NormalBrandDTO> normalBrandDTOList = new ArrayList<NormalBrandDTO>();
    List<Long> normalBrandIdList = new ArrayList<Long>();
    if(StringUtils.isNotBlank(keyWord)){
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:shopVehicleBrandModelDTOList){
        if(shopVehicleBrandModelDTO.getBrandName().contains(keyWord) && !normalBrandIdList.contains(shopVehicleBrandModelDTO.getBrandId())){
          normalBrandIdList.add(shopVehicleBrandModelDTO.getBrandId());
          normalBrandDTOList.add(new NormalBrandDTO(shopVehicleBrandModelDTO.getBrandId(),shopVehicleBrandModelDTO.getBrandName()));
        }
      }
    }else{
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:shopVehicleBrandModelDTOList){
        if(!normalBrandIdList.contains(shopVehicleBrandModelDTO.getBrandId())){
          normalBrandIdList.add(shopVehicleBrandModelDTO.getBrandId());
          normalBrandDTOList.add(new NormalBrandDTO(shopVehicleBrandModelDTO.getBrandId(),shopVehicleBrandModelDTO.getBrandName()));
        }
      }
    }

    return normalBrandDTOList;
  }

  @RequestMapping(params = "method=getVehicleModel")
  @ResponseBody
  public Object getVehicleModel(HttpServletRequest request, HttpServletResponse response) {
    String brandName = request.getParameter("brandName");
    String keyWord = request.getParameter("keyWord");
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = StandardBrandModelCache.getShopVehicleBrandModelDTOList();
    List<NormalModelDTO> normalModelDTOList = new ArrayList<NormalModelDTO>();
    if(StringUtils.isNotBlank(keyWord) && StringUtils.isNotBlank(brandName)){
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:shopVehicleBrandModelDTOList){
        if(shopVehicleBrandModelDTO.getBrandName().contains(brandName) && shopVehicleBrandModelDTO.getModelName().contains(keyWord)){
          normalModelDTOList.add(new NormalModelDTO(shopVehicleBrandModelDTO.getModelId(),shopVehicleBrandModelDTO.getModelName(),shopVehicleBrandModelDTO.getBrandId()));
        }
      }
    }else if(StringUtils.isNotBlank(keyWord) && StringUtils.isBlank(brandName)){
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:shopVehicleBrandModelDTOList){
        if(shopVehicleBrandModelDTO.getModelName().contains(keyWord)){
          normalModelDTOList.add(new NormalModelDTO(shopVehicleBrandModelDTO.getModelId(),shopVehicleBrandModelDTO.getModelName(),shopVehicleBrandModelDTO.getBrandId()));
        }
      }
    }else if(StringUtils.isBlank(keyWord) && StringUtils.isNotBlank(brandName)){
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:shopVehicleBrandModelDTOList){
        if(shopVehicleBrandModelDTO.getBrandName().contains(brandName)){
          normalModelDTOList.add(new NormalModelDTO(shopVehicleBrandModelDTO.getModelId(),shopVehicleBrandModelDTO.getModelName(),shopVehicleBrandModelDTO.getBrandId()));
        }
      }
    }else{
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:shopVehicleBrandModelDTOList){
        normalModelDTOList.add(new NormalModelDTO(shopVehicleBrandModelDTO.getModelId(),shopVehicleBrandModelDTO.getModelName(),shopVehicleBrandModelDTO.getBrandId()));
      }
    }
    return normalModelDTOList;
  }

  @RequestMapping(params = "method=searchProductInfo")
  @ResponseBody
  public Object searchProductInfo(HttpServletRequest request, HttpServletResponse response, SearchConditionDTO searchConditionDTO) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    List<SearchSuggestionDTO> searchSuggestionDTOList = null;
    try {
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_SUGGESTION, SearchConditionDTO.SEARCHSTRATEGY_NORMAL_PRODUCT});
      searchConditionDTO.setIncludeBasic(false);
      searchSuggestionDTOList = searchProductService.queryProductSuggestionWithDetails(searchConditionDTO).getSuggestionDTOs();
    } catch (Exception e) {
      LOG.debug("method=searchProductInfo");
      LOG.error(e.getMessage(), e);
    }
    List<ProductInfoDTO> productInfoDTOList = new ArrayList<ProductInfoDTO>();

    if (CollectionUtils.isEmpty(searchSuggestionDTOList)) {
      return productInfoDTOList;
    }

    for (SearchSuggestionDTO suggestionDTO : searchSuggestionDTOList) {
      ProductInfoDTO productInfoDTO = new ProductInfoDTO();
      StringBuffer name = new StringBuffer();
      Map map = new HashMap();
      for (int i = 0; i < suggestionDTO.getSuggestionEntry().size(); i++) {
        name.append(suggestionDTO.getSuggestionEntry().get(i)[1]).append(" ");
        map.put(suggestionDTO.getSuggestionEntry().get(i)[0], suggestionDTO.getSuggestionEntry().get(i)[1]);
      }
      productInfoDTO.setName(name.toString());
      productInfoDTO.setJsonStr(JsonUtil.mapToJson(map));
      productInfoDTOList.add(productInfoDTO);
    }

    return productInfoDTOList;
  }

  @RequestMapping(params = "method=searchShop")
  @ResponseBody
  public Object searchShop(HttpServletRequest request, HttpServletResponse response) {
    String keyWord = request.getParameter("keyWord");

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    List<ShopDTO> shopDTOList = configService.getShopByObscureName(keyWord);

    return shopDTOList;
  }

  @RequestMapping(params = "method=getRelevanceStatus")
  @ResponseBody
  public Object getRelevanceStatus(HttpServletRequest request, HttpServletResponse response) {
    List<Map> mapList = new ArrayList<Map>();
    for (ProductRelevanceStatus status : ProductRelevanceStatus.values()) {
      Map map = new HashMap();
      map.put("name", status.getStatus());
      mapList.add(map);
    }

    return mapList;
  }

  @RequestMapping(params = "method=verifyRelevanceProduct")
  @ResponseBody
  public Object verifyRelevanceProduct(HttpServletRequest request, HttpServletResponse response, Long normalProductId, Long... shopProductIds) {
    Result result = new Result(false);
    try {
      IProductService productService = ServiceManager.getService(IProductService.class);
      if (ArrayUtils.isEmpty(shopProductIds)) return result;
      List<ProductDTO> productDTOList = productService.getSimpleProductDTOListById(shopProductIds);
      if(CollectionUtils.isNotEmpty(productDTOList)){
        int count = 0;
        for (ProductDTO productDTO : productDTOList) {
          if(productDTO.getNormalProductId()!=null){
            count++;
          }
        }
        if(count>0){
          result.setSuccess(false);
          result.setData(count);
        }else{
          result.setSuccess(true);
        }
      }else{
        result.setSuccess(false);
        result.setMsg(ShopProductConstants.NO_SHOP_PRODUCT_IDS);
      }


    } catch (Exception e) {
      LOG.debug("/productManage.do");
      LOG.debug("method=verifyRelevanceProduct");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=relevanceProduct")
  @ResponseBody
  public Object relevanceProduct(HttpServletRequest request, HttpServletResponse response, Long... shopProductIds) {
    Long userId = WebUtil.getUserId(request);
    IProductService productService = ServiceManager.getService(IProductService.class);
    String normalProductIdStr = request.getParameter("normalProductId");
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    Map map = new HashMap();
    if (ArrayUtil.isEmpty(shopProductIds)) {
      map.put("result", "error");
      map.put("errorMsg", ShopProductConstants.NO_SHOP_PRODUCT_IDS);
      return map;
    }
    if (StringUtils.isBlank(normalProductIdStr)) {
      map.put("result", "error");
      map.put("errorMsg", ShopProductConstants.NO_NORMAL_PRODUCT_ID);
      return map;
    }
    Long normalProductId = Long.valueOf(normalProductIdStr);

    try {
      List<Long> shopProductIdList = new ArrayList<Long>(Arrays.asList(shopProductIds));
      productService.relevanceProduct(shopProductIdList, normalProductId);
      map.put("result", "success");
      if (!ArrayUtils.isEmpty(shopProductIds)) {
        List<NormalProductModifyRecordDTO> normalProductModifyRecordDTOList = new ArrayList<NormalProductModifyRecordDTO>();
        for (Long id : shopProductIdList) {
          normalProductModifyRecordDTOList.add(new NormalProductModifyRecordDTO(userId,normalProductId,id,NormalProductModifyScene.RELEVANCE));
          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(id, null);
          if (null != productLocalInfoDTO) {
            productSolrWriterService.createProductSolrIndex(productLocalInfoDTO.getShopId(), productLocalInfoDTO.getId());
          }
        }
        productService.saveNormalProductModifyRecord(normalProductModifyRecordDTOList.toArray(new NormalProductModifyRecordDTO[normalProductModifyRecordDTOList.size()]));
      }
    } catch (Exception e) {
      map.put("result", "error");
      map.put("errorMsg", ShopProductConstants.RELEVANCE_ERROR);
      LOG.error(e.getMessage(), e);
    }
    return map;
  }

  /**
   * 获得该标准商品的主营车型
   *
   * @param normalProductId
   * @return CheckNode
   */
  @RequestMapping(params = "method=getNormalProductVehicleBrandModelByNormalProductId")
  @ResponseBody
  public Object getNormalProductVehicleBrandModelByNormalProductId(HttpServletRequest request, Long normalProductId) {
    Node node = null;
    try {
      node = ServiceManager.getService(IProductService.class).getNormalProductVehicleBrandModelByNormalProductId(normalProductId);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }

  /**
   * 获得选中的主营车型
   *
   * @return CheckNode
   */
  @RequestMapping(params = "method=getCheckedNormalProductVehicleBrandModel")
  @ResponseBody
  public Object getCheckedNormalProductVehicleBrandModel(HttpServletRequest request, Long normalProductId) {
    Node node = null;
    try {
      Set<Long> ids = new HashSet<Long>(NumberUtil.parseLongValues(request.getParameter("ids")));
      if (CollectionUtil.isEmpty(ids)) return this.getNormalProductVehicleBrandModelByNormalProductId(request, normalProductId);
      node = ServiceManager.getService(IProductService.class).getCheckedVehicleBrandModel(ids);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }
}
