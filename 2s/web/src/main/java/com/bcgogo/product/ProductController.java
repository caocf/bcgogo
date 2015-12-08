package com.bcgogo.product;

import com.bcgogo.common.*;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.model.ProductBarcode;
import com.bcgogo.product.model.Promotions;
import com.bcgogo.product.service.*;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.ProductSearchSuggestionListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.search.util.SuggestionHelper;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.txn.service.solr.ISolrMergeService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-30
 * Time: 上午9:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/product.do")
public class ProductController {
  private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

  @RequestMapping(params = "method=createkind")
  public String createKind(ModelMap model, HttpServletRequest request) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IConfigService configService = ServiceManager.getService(ConfigService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      String isUpdateData = request.getParameter("isUpdateData");
      configService.setConfig("listplateurl", "D:\\licenseplate.csv", ShopConstant.BC_SHOP_ID);
      configService.setConfig("vehicledataurl", "/Users/caiweili/dev/bc-trunk/product/src/test/resources/productAndVehicleData/车型数据简化.csv", 1L);
      configService.setConfig("productdataurl", "/Users/caiweili/dev/bc-trunk/product/src/test/resources/productAndVehicleData/轮胎数据第二版.csv", 1L);
      configService.setConfig("productvehicledataurl", "/Users/caiweili/dev/bc-trunk/product/src/test/resources/productAndVehicleData/车款用品表.csv", 1L);
      ServiceManager.getService(ISearchService.class).deleteByQuery("*:*", "vehicle");
      SolrClientHelper.getSuggestionClient().deleteByQuery("doc_type:" + SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
      productService.readFormFile(1l, "vehicledataurl", "0");
      productService.readFormFile(1l, "productdataurl", "1");
      productService.readFormFile(1l, "productvehicledataurl", "2");
      productService.readFormFile(1l, "listplateurl", "3");
      if (isUpdateData != null && "true".equals(isUpdateData)) {
        productService.updateAllVehicleFirstLetter();
      }
      KindDTO kindDTO = new KindDTO();
      model.addAttribute("command", kindDTO);
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=createkind");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/product/addkind";
  }

  @RequestMapping(params = "method=fwdinsert")
  public String forwardInsertProductData(ModelMap model, HttpServletRequest request, String clearFlag) {
    try {
      ProductDTO productDTO = new ProductDTO();
      productDTO.setProductFile(null);
      model.addAttribute("command", productDTO);
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=fwdinsert");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("clearFlag:" + clearFlag);
      LOG.error(e.getMessage(), e);
    }
    return "/product/insertProductData";
  }

  @RequestMapping(params = "method=insertproductdata", method = RequestMethod.POST)
  public String insertPradiobuttonroductData(ModelMap model, HttpServletRequest request, ProductDTO productDTO) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    try {
      MultipartFile typeFile = productDTO.getProductFile();
      productService.readFormFile(typeFile.getInputStream(), productDTO.getProductFileType());
      productDTO.setProductFile(null);
      model.addAttribute("command", productDTO);
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=insertproductdata");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(productDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return "/product/insertProductData";
  }

  @RequestMapping(params = "method=addkind", method = RequestMethod.POST)
  public String addKind(ModelMap model, HttpServletRequest request, KindDTO kindDTO) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    KindDTO kDTO = productService.createKind(kindDTO);
    model.addAttribute("command", kDTO);
    model.addAttribute("flag", "savesuccess");
    return "/product/addkind";
  }

  @RequestMapping(params = "method=searchbrandforvehicle")
  @ResponseBody
  public List searchBrandForVehicle(ModelMap model, HttpServletRequest request, HttpServletResponse response, String flInfo,
                                    String domtitle, String brandvalue) {
    List vehicleInfoList = null;
    try {
      IProductService productService = ServiceManager.getService(IProductService.class);
      if ("brand".equals(domtitle)) {
        vehicleInfoList = productService.getBrandWithFirstLetter(flInfo.toLowerCase());
      } else if ("model".equals(domtitle)) {
        Long brandId = null;
        if (StringUtils.isNotBlank(brandvalue)) {
          BrandDTO brandDTO = productService.getBrandByName(brandvalue);
          brandId = brandDTO != null ? brandDTO.getId() : null;
        }
        vehicleInfoList = productService.getModelWithFirstLetter(flInfo.toLowerCase(), brandId);
      }
      return vehicleInfoList;
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchbrandforvehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("flInfo:" + flInfo + ",domtitle:" + domtitle + ",brandvalue:" + brandvalue);
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=searchproductbyfirstletter")
  public void searchProductByFirstLetter(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                         String flInfo, String vehicleBrand, String vehicleModel, String vehicleYear, String vehicleEngine,
                                         Integer currentpage, Integer linesize, String orderType) {
    try {
      IProductService productService = ServiceManager.getService(IProductService.class);
      ISearchService searchService = ServiceManager.getService(ISearchService.class);

      boolean containBase = "salesOrder".equals(orderType) ? false : true;
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long[] ids = productService.getVehicleIds(vehicleBrand, vehicleModel, vehicleYear, vehicleEngine);
      List<String> result = searchService.queryProductSuggestionList(flInfo, "product_name",
          null, null, null, null,
          vehicleBrand, vehicleModel, vehicleYear, vehicleEngine,
          ids[0], ids[1], ids[2], ids[3],
          shopId, containBase, currentpage, linesize);

      StringBuffer sb = new StringBuffer("[]");
      if (result != null && result.size() > 0) {
        sb = new StringBuffer("[");
        for (String productname : result) {
          sb.append("{\"name\":\"" + productname + "\"},");
        }
        sb.replace(sb.length() - 1, sb.length(), "]");
      }
      PrintWriter out = response.getWriter();
      out.write(sb.toString());
      out.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchproductbyfirstletter");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("vehicleBrand" + vehicleBrand + ",vehicleModel:" + vehicleModel + ",vehicleEngine:" + vehicleEngine + ",vehicleYear:" + vehicleYear);
      LOG.debug("flInfo:" + flInfo + ",currentpage:" + currentpage + ",linesize:" + linesize + ",orderType:" + orderType);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=searchproductinfo")
  public String searchProductInfo(ModelMap model, HttpServletRequest request, String domtitle, Integer inputPosition, String orderType) {
    return "/product/searchProductAndVehicleInfo";
  }

  @RequestMapping(params = "method=createsearchvehicleinfo")
  public String createsearchVehicleInfo(ModelMap model, HttpServletRequest request, String fwdflag) {
    String domtitle = request.getParameter("domtitle");
    String brandvalue = request.getParameter("brandvalue");
    String inputPosition = request.getParameter("inputPosition");
    model.addAttribute("domtitle", domtitle);
    model.addAttribute("brandvalue", brandvalue);
    model.addAttribute("inputPosition", inputPosition);
    if (fwdflag != null && "0".equals(fwdflag)) {
      return "/product/searchVehicleInfoForAddCar";
    } else {
      return "/product/searchVehicleInfo";
    }
  }

  @RequestMapping(params = "method=createsearchvehicleinfoforaddclient")
  public String createsearchVehicleInfoForAddClient(ModelMap model, HttpServletRequest request) {
    String domtitle = request.getParameter("domtitle");
    String brandvalue = request.getParameter("brandvalue");
    model.addAttribute("domtitle", domtitle);
    model.addAttribute("brandvalue", brandvalue);
    return "/product/searchVehicleInfoForAddClients";
  }

  /**
   * 车辆信息
   * @Related to ProductSuggestionController searchBrandSuggestion
   */
  @RequestMapping(params = "method=searchBrandSuggestion")
  public void searchBrandSuggestion(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                    String searchWord, String searchField, String brandValue, String modelValue,
                                    String yearValue, String engineValue) {
    ISearchVehicleService searchVehicleService = ServiceManager.getService(ISearchVehicleService.class);
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    try {
        List<String> searchList = null;
        if(ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))){
            if("brand".equals(searchField)){
                searchList = StandardBrandModelCache.getVehicleBrandSuggestion(StringUtils.trim(searchWord), StringUtils.trim(modelValue));
            }else{
                searchList = StandardBrandModelCache.getVehicleModelSuggestion(StringUtils.trim(searchWord),StringUtils.trim(brandValue));
            }
        }else{
            SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
            searchConditionDTO.setSearchWord(StringUtils.trim(searchWord));
            searchConditionDTO.setSearchField(searchField);
            searchConditionDTO.setVehicleBrand(StringUtils.trim(brandValue));
            searchConditionDTO.setVehicleModel(StringUtils.trim(modelValue));
            searchConditionDTO.setVehicleYear(StringUtils.trim(yearValue));
            searchConditionDTO.setVehicleEngine(StringUtils.trim(engineValue));
            searchList = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
        }
        if (searchList != null && searchList.size() > 0) {
            for (int i = 0, len = searchList.size(); i < len; i++) {
                sb.append("{\"name\":\"").append(searchList.get(i)).append("\"},");
            }
            sb.replace(sb.length() - 1, sb.length(), "]");
            jsonStr = sb.toString();
        }

        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do?method=searchBrandSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("brandValue" + brandValue + ",modelValue:" + modelValue + ",engineValue:" + engineValue + ",yearValue:" + yearValue);
      LOG.debug("searchWord:" + searchWord + ",searchField:" + searchField);
      LOG.error(e.getMessage(), e);
    }
  }


  @RequestMapping(params = "method=searchProductForStockSearch")
  @Deprecated
  public void searchProductForStockSearch(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                          String searchWord, String searchField,
                                          String productValue, String brandValue, String modelValue, String yearValue, String engineValue) {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    ISearchVehicleService searchVehicleService = ServiceManager.getService(ISearchVehicleService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    Long shopId = (Long) request.getSession().getAttribute("shopId");

    List<String> searchList = null;
    try {
      if ("product_name".equals(searchField)) {
        Long[] ids = productService.getVehicleIds(brandValue, modelValue, yearValue, engineValue);
        searchList = searchService.queryProductSuggestionList(searchWord, searchField,
            null, null, null, null,
            brandValue, modelValue, yearValue, engineValue,
            ids[0], ids[1], ids[2], ids[3],
            shopId, false, 0, 10);
      } else {
        SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
        searchConditionDTO.setSearchWord(searchWord);
        searchConditionDTO.setSearchField(searchField);
        searchConditionDTO.setVehicleBrand(brandValue);
        searchConditionDTO.setVehicleModel(modelValue);
        searchConditionDTO.setVehicleYear(yearValue);
        searchConditionDTO.setVehicleEngine(engineValue);
        searchList = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
      }

      if (searchList != null && searchList.size() > 0) {
        for (int i = searchList.size() - 1; i >= 0; i--) {
          sb.append("{\"name\":\"" + searchList.get(i) + "\"},");

        }
        sb.replace(sb.length() - 1, sb.length(), "]");
        jsonStr = sb.toString();
      }

      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchProductForStockSearch");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("brandValue" + brandValue + ",modelValue:" + modelValue + ",engineValue:" + engineValue + ",yearValue:" + yearValue);
      LOG.debug("searchWord:" + searchWord + ",searchField:" + searchField + ",productValue:" + productValue);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=searchVehicleSuggestionForGoodsBuy")
  @ResponseBody
  public Object searchVehicleSuggestionForGoodsBuy(HttpServletRequest request, SearchConditionDTO searchConditionDTO,String searchType) {
    ISearchVehicleService searchVehicleService = ServiceManager.getService(ISearchVehicleService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) throw new Exception("shop Id is null");
        SuggestionHelper.adaptSearchConditionFromProduct(searchConditionDTO);
        if(ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))||"standard".equals(searchType)){
            List<String> searchResultList = null;
            if("brand".equals(searchConditionDTO.getSearchField())){
                searchResultList = StandardBrandModelCache.getVehicleBrandSuggestion(StringUtils.trim(searchConditionDTO.getSearchWord()), StringUtils.trim(searchConditionDTO.getVehicleModel()));
            }else{
                searchResultList = StandardBrandModelCache.getVehicleModelSuggestion(StringUtils.trim(searchConditionDTO.getSearchWord()),StringUtils.trim(searchConditionDTO.getVehicleBrand()));
            }
            return SuggestionHelper.generateVehicleDropDownMap(searchResultList,"name" ,searchConditionDTO.getUuid());
        }else{
            searchConditionDTO.setShopId(shopId);
            if (StringUtils.isNotBlank(searchConditionDTO.getUuid())) {
                List<String> searchResultList = searchVehicleService.getVehicleSuggestionList(searchConditionDTO);
                return SuggestionHelper.generateVehicleSuggestionWithCategoryDropDownMap(searchResultList, searchConditionDTO.getUuid());
            } else {
                return searchVehicleService.getVehicleSuggestionList(searchConditionDTO);
            }
        }
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchVehicleSuggestionForGoodsBuy");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("SearchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }



  /**
   * 库存/首页搜索框/head搜索框  下拉建议
   *
   * @param request
   * @param response
   * @param searchConditionDTO
   * @author zhangjuntao
   */
  @RequestMapping(params = "method=searchProductInfoForStockSearch")
  @ResponseBody
  public Object searchProductInfoForStockSearch(HttpServletRequest request, HttpServletResponse response, SearchConditionDTO searchConditionDTO) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    List<SearchSuggestionDTO> searchSuggestionDTOList = null;
    try {
      if(shopId==null) throw new Exception("shop Id is null");
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setIncludeBasic(false);
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_SUGGESTION});
      searchSuggestionDTOList = searchProductService.queryProductSuggestionWithDetails(searchConditionDTO).getSuggestionDTOs();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchProductInfoForStockSearch");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return searchSuggestionDTOList;
  }

  /**
   * 只查供应商的上架商品的下拉建议
   * @param request
   * @param searchConditionDTO
   * @return
   */
  @RequestMapping(params = "method=searchWholeSalerProductInfo")
  @ResponseBody
  public Object searchWholeSalerProductInfo(HttpServletRequest request,SearchConditionDTO searchConditionDTO) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(shopId == null) throw new Exception("shop Id is null");
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      searchConditionDTO.setShopKind(shopDTO.getShopKind());

      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_SUGGESTION,SearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
      searchConditionDTO.setIncludeBasic(false);
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      if(searchConditionDTO.getShopId() == null){       //从供应商详情-商品列表页面中发的请求，有可能是本店商品。
        searchConditionDTO.setExcludeShopIds(new Long[]{shopId});
      }
      List<SearchSuggestionDTO> searchSuggestionDTOList = searchProductService.queryProductSuggestionWithDetails(searchConditionDTO).getSuggestionDTOs();
      List<Map> dropDownList = new ArrayList<Map>();
      if (CollectionUtils.isNotEmpty(searchSuggestionDTOList)) {
        for (SearchSuggestionDTO pssDTO : searchSuggestionDTOList)
          dropDownList.add(pssDTO.toStandardDropDownItemMap());
      }
      result.put("uuid", searchConditionDTO.getUuid());
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchWholeSalerProductInfo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  /*
  *  商品字段建议
  */
  @RequestMapping(params = "method=getProductSuggestion")
  @ResponseBody
  public Object getProductSuggestion(HttpServletRequest request, SearchConditionDTO searchConditionDTO) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    IPromotionsService promotionsService=ServiceManager.getService(IPromotionsService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(shopId==null) throw new Exception("shop Id is null");
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setIncludeBasic(false);
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_SUGGESTION});
      if (PromotionsUtils.ADD_PROMOTIONS_PRODUCT_CURRENT.equals(searchConditionDTO.getPromotionsFilter())){
         Promotions promotions=CollectionUtil.getFirst(promotionsService.getPromotionsById(shopId, searchConditionDTO.getPromotionsId()));
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
      List<SearchSuggestionDTO> searchSuggestionDTOList = searchProductService.queryProductSuggestionWithDetails(searchConditionDTO).getSuggestionDTOs();
      List<Map> dropDownList = new ArrayList<Map>();
      if (CollectionUtils.isNotEmpty(searchSuggestionDTOList)) {
        for (SearchSuggestionDTO pssDTO : searchSuggestionDTOList)
          dropDownList.add(pssDTO.toStandardDropDownItemMap());
      }
      result.put("uuid", searchConditionDTO.getUuid());
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=getProductSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("SearchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 汽修汽配都可用
   * @param request
   * @param conditionDTO
   * @return
   */
  @RequestMapping(params = "method=getProducts")
  @ResponseBody
  public Object getProducts(HttpServletRequest request ,SearchConditionDTO conditionDTO) {
    Long shopId=WebUtil.getShopId(request);
    try {
      conditionDTO.setShopId(shopId);
      if(StringUtil.isEmpty(conditionDTO.getSort())&&conditionDTO.isEmptyOfProductInfo()){
        conditionDTO.setSort("last_in_sales_time desc");//默认上架时间排序
      }else {
        conditionDTO.setSort(TxnConstant.sortCommandMap.get(conditionDTO.getSort()));
      }
      PagingListResult<ProductDTO> result = new PagingListResult<ProductDTO>();
      ProductSearchResultListDTO productSearchResultListDTO = ServiceManager.getService(ISearchProductService.class).queryProductWithStdQuery(conditionDTO);

      if(productSearchResultListDTO!=null && CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())){
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,productSearchResultListDTO.getProducts().toArray(new ProductDTO[productSearchResultListDTO.getProducts().size()]));
        IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
        List<Long> productIdList = new ArrayList<Long>();
        for(ProductDTO productDTO : productSearchResultListDTO.getProducts()){
          productIdList.add(productDTO.getProductLocalInfoId());
        }
        Map<Long,Long> productCategoryRelationMap = productCategoryService.getProductCategoryRelationMap(shopId, productIdList.toArray(new Long[productIdList.size()]));
        if(MapUtils.isNotEmpty(productCategoryRelationMap)){
          List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(productCategoryRelationMap.values()));
          productCategoryService.fillProductCategoryDTOListInfo(productCategoryDTOList);
          Map<Long,ProductCategoryDTO> productCategoryDTOMap = new HashMap<Long, ProductCategoryDTO>();
          if(CollectionUtils.isNotEmpty(productCategoryDTOList)){
            for(ProductCategoryDTO productCategoryDTO : productCategoryDTOList){
              productCategoryDTOMap.put(productCategoryDTO.getId(),productCategoryDTO);
            }
          }
          for(ProductDTO productDTO : productSearchResultListDTO.getProducts()){
            Long productCategoryId = productCategoryRelationMap.get(productDTO.getProductLocalInfoId());
            if(productCategoryId!=null){
              productDTO.setProductCategoryDTO(productCategoryDTOMap.get(productCategoryId));
            }
          }
        }
      }

      Pager pager = new Pager(Integer.valueOf(productSearchResultListDTO.getNumFound() + ""), conditionDTO.getStartPageNo(), conditionDTO.getMaxRows());
      result.setResults(productSearchResultListDTO.getProducts());
      result.setPager(pager);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getInSalesProductForSupplyDemand")
  @ResponseBody
  public Object getInSalesProductForSupplyDemand(HttpServletRequest request ,SearchConditionDTO conditionDTO) {
    Long shopId=WebUtil.getShopId(request);
    try {
//      conditionDTO.setStart(0);
      conditionDTO.setShopId(shopId);
      conditionDTO.setSort("last_in_sales_time desc");//默认上架时间排序
      PagingListResult<ProductDTO> result = new PagingListResult<ProductDTO>();
      ProductSearchResultListDTO productSearchResultListDTO = ServiceManager.getService(ISearchProductService.class).queryProductWithStdQuery(conditionDTO);
      if(productSearchResultListDTO!=null && CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())){
        List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
        imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
        ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,productSearchResultListDTO.getProducts().toArray(new ProductDTO[productSearchResultListDTO.getProducts().size()]));
      }
      return null;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=searchVehicleIds")
  public void searchVehicleIds(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                               String brandValue, String modelValue, String yearValue, String engineValue) {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    try {
      List<Long> searchList = (List<Long>) searchService.getVehicleIdsByKeywords(brandValue, modelValue, yearValue, engineValue, true).get(false);
      if (searchList != null && searchList.size() == 4) {
        BrandDTO bDTO = productService.getBrand(searchList.get(0));
        String brandname = bDTO == null || bDTO.getName() == null ? "" : bDTO.getName();
        ModelDTO mDTO = productService.getModel(searchList.get(1));
        String modelname = mDTO == null || mDTO.getName() == null ? "" : mDTO.getName();
        YearDTO yDTO = productService.getYear(searchList.get(2));
        String yearname = yDTO == null || yDTO.getYear() == null ? "" : yDTO.getYear() + "";
        EngineDTO eDTO = productService.getEngine(searchList.get(3));
        String enginename = eDTO == null || eDTO.getEngine() == null ? "" : eDTO.getEngine();
        for (Long term : searchList) {
          sb.append("{\"id\":\"" + (term == null ? "" : term.toString()) + "\"},");
        }
        sb.append("{\"brandname\":\"" + brandname + "\"},");
        sb.append("{\"modelname\":\"" + modelname + "\"},");
        sb.append("{\"yearname\":\"" + yearname + "\"},");
        sb.append("{\"enginename\":\"" + enginename + "\"},");
        sb.replace(sb.length() - 1, sb.length(), "]");
        jsonStr = sb.toString();
      }
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchVehicleIds");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("brandValue" + brandValue + ",modelValue:" + modelValue + ",engineValue:" + engineValue + ",yearValue:" + yearValue);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=searchVehicleIdsForStockSearch")
  public void searchVehicleIdsForStockSearch(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                             String brandValue, String modelValue, String yearValue, String engineValue) {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    String jsonStr = null;
    try {
      brandValue = "车辆品牌".equals(brandValue) ? "" : brandValue;
      modelValue = "车型".equals(modelValue) ? "" : modelValue;
      yearValue = "年代".equals(yearValue) ? "" : yearValue;
      engineValue = "排量".equals(engineValue) ? "" : engineValue;
      QueryResponse rsp = (QueryResponse) searchService.getVehicleIdsByKeywords(brandValue, modelValue, yearValue, engineValue, false).get(true);
      SolrDocumentList documentList = rsp.getResults();
      if (documentList != null && documentList.size() > 0) {
        StringBuffer sb = new StringBuffer("[");
        for (SolrDocument doc : documentList) {
          Long brandId = (Long) doc.getFirstValue("pv_brand_id");
          Long modelId = (Long) doc.getFirstValue("pv_model_id");
          Long yearId = (Long) doc.getFirstValue("pv_year_id");
          Long engineId = (Long) doc.getFirstValue("pv_engine_id");
          sb.append("[{\"id\":\"").append(brandId).append("\"},");
          sb.append("{\"id\":\"").append(modelId).append("\"},");
          sb.append("{\"id\":\"").append(yearId).append("\"},");
          sb.append("{\"id\":\"").append(engineId).append("\"}],");
        }
        jsonStr = sb.replace(sb.length() - 1, sb.length(), "]").toString();
      } else {
        jsonStr = "[]";
      }
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchVehicleIdsForStockSearch");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("brandValue" + brandValue + ",modelValue:" + modelValue + ",engineValue:" + engineValue + ",yearValue:" + yearValue);
      LOG.error(e.getMessage(), e);
    }
  }

  /*
  *  1.商品字段建议
  *  2.商品历史建议
  */
  @RequestMapping(params = "method=getProductSuggestionAndHistory")
  @ResponseBody
  public Object getProductSuggestionAndHistory(HttpServletRequest request, SearchConditionDTO searchConditionDTO) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    ISolrMergeService solrMergeService = ServiceManager.getService(ISolrMergeService.class);
//    StopWatchUtil sw =  new StopWatchUtil("getProductSuggestionAndHistory","start");
    try {
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("uuid", searchConditionDTO.getUuid());
      String title = "本店商品列表";
      Long shopId = WebUtil.getShopId(request);
      searchConditionDTO.setShopId(shopId);
      ProductSearchSuggestionListDTO searchResult = searchProductService.queryProductSuggestionWithDetails(searchConditionDTO);
      solrMergeService.mergeCacheProductDTO(shopId,searchResult.getProductDetailResultDTOs());
      if(SuggestionHelper.isVehicleSuggestion(searchConditionDTO.getSearchField())){
        SuggestionHelper.adaptSearchConditionFromProduct(searchConditionDTO);
        result.put("dropDown", SuggestionHelper.generateVehicleDropDownMap(ServiceManager.getService(ISearchVehicleService.class).getVehicleSuggestionListByKeywords(searchConditionDTO),
          searchConditionDTO.getSearchField(),searchConditionDTO.getUuid()));
      }else if(searchResult!=null){
        result.put("dropDown", SuggestionHelper.generateProductDropDownMap(searchResult.getSuggestionDTOs(),searchConditionDTO.getUuid()));
      }
      List<Map> historyList = new ArrayList<Map>();
      List<ProductDTO> productDTOList = searchResult.getProductDetailResultDTOs();
      if (CollectionUtils.isNotEmpty(productDTOList)) {
        for (ProductDTO productDTO : productDTOList) {
          Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = ServiceManager.getService(IStoreHouseService.class).getStoreHouseInventoryDTOMapByProductId(shopId,productDTO.getProductLocalInfoId());
          productDTO.setStoreHouseInventoryDTOMap(storeHouseInventoryDTOMap);
          Map<String,String> productHistorySuggestionMap = productDTO.toProductHistorySuggestionMap();
          //加入分库存量
          if(productDTO.getStoreHouseInventoryDTOMap() != null && productDTO.getStoreHouseInventoryDTOMap().size() > 0
            && searchConditionDTO.getStorehouseId() != null) {
            if(productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId())!=null){
              productHistorySuggestionMap.put("singleInventoryNum",productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId()).getAmount() == null ? "0.0" : productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId()).getAmount().toString());
              productHistorySuggestionMap.put("storageBin",productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId()).getStorageBin() == null ? "" : productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId()).getStorageBin());
            }else{
              productHistorySuggestionMap.put("singleInventoryNum", "0.0");
              productHistorySuggestionMap.put("storageBin", "");
            }
          } else if(productDTO.getStoreHouseInventoryDTOMap() != null && productDTO.getStoreHouseInventoryDTOMap().size() > 0 && searchConditionDTO.getStorehouseId() == null) {
            productHistorySuggestionMap.put("singleInventoryNum","0.0");
            productHistorySuggestionMap.put("storageBin","");
          }
          historyList.add(productHistorySuggestionMap);
        }
      }
      Map<String, Object> historyMap = new HashMap<String, Object>();
      historyMap.put("uuid", searchConditionDTO.getUuid());
      historyMap.put("data", historyList);
      historyMap.put("totalCount", searchResult.getProductDetailTotalCount());
      historyMap.put("title", title);

      result.put("history", historyMap);
//      sw.stopAndPrintLog();
      return result;
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=getProductSuggestionAndHistory");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("SearchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

@RequestMapping(params = "method=searchVehicleSuggestion")
  @ResponseBody
  public void searchVehicleSuggestion(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                                 SearchConditionDTO searchConditionDTO) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if(shopId==null) throw new Exception("shop Id is null");
      searchConditionDTO.setShopId(shopId);
      List<String> result = ServiceManager.getService(ISearchVehicleService.class).getVehicleSuggestionList(searchConditionDTO);
//      List<String> result = ServiceManager.getService(ISearchService.class).getVehicleSuggestionListByKeywords(searchConditionDTO);
      String str = JsonUtil.listToJson(result);
      PrintWriter writer = response.getWriter();
      writer.write(str);
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  /*
  * 商品历史建议  for 更多
  */
  @RequestMapping(params = "method=getProductHistorySuggestion")
  @ResponseBody
  public Object getProductHistorySuggestion(HttpServletRequest request, SearchConditionDTO searchConditionDTO) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
	  ISolrMergeService solrMergeService = ServiceManager.getService(ISolrMergeService.class);
    try {
	    Long shopId = WebUtil.getShopId(request);
      searchConditionDTO.setShopId(shopId);
      List<Map> historyList = new ArrayList<Map>();
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithStdQuery(searchConditionDTO);
	    //合并solr延时提交memcach中的商品
	    solrMergeService.mergeCacheProductDTO(shopId,productSearchResultListDTO.getProducts());
      if (productSearchResultListDTO != null) {
        List<ProductDTO> productDTOList = productSearchResultListDTO.getProducts();
        for (ProductDTO productDTO : productDTOList) {
          historyList.add(productDTO.toProductHistorySuggestionMap());
        }
      }
      Map<String, Object> result = new HashMap<String, Object>();
      result.put("uuid", searchConditionDTO.getUuid());
      result.put("data", historyList);

      return result;
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=getProductHistorySuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("SearchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
  @Deprecated
  @RequestMapping(params = "method=searchmaterialforgoodsbuy")
  @ResponseBody
  public Object searchMaterialForGoodsBuy(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                          SearchConditionDTO searchConditionDTO) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    try {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
      searchConditionDTO.setShopId(shopId);
      return searchProductService.getProductSuggestion(searchConditionDTO);
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchmaterialforgoodsbuy");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("SearchConditionDTO:" + searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @Deprecated
  @RequestMapping(params = "method=searchmaterial")
  public void searchMaterial(ModelMap model, HttpServletRequest request, HttpServletResponse response, String searchWord,
                             String searchField, String productNameValue, String productBrandValue, String productSpecValue,
                             String productModelValue, String vehicleBrand, String vehicleModel, String vehicleYear,
                             String vehicleEngine) {

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
//      Long[] ids = productService.getVehicleIds(vehicleBrand, vehicleModel, vehicleYear, vehicleEngine);
      SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setSearchWord(searchWord);
      searchConditionDTO.setSearchField(searchField);
      searchConditionDTO.setProductName(productNameValue);
      searchConditionDTO.setProductBrand(productBrandValue);
      searchConditionDTO.setProductSpec(productSpecValue);
      searchConditionDTO.setProductModel(productModelValue);
//      searchConditionDTO.setVehicleIds(ids);
      searchConditionDTO.setVehicleBrand(vehicleBrand);
      searchConditionDTO.setVehicleModel(vehicleModel);
      searchConditionDTO.setVehicleYear(vehicleYear);
      searchConditionDTO.setVehicleEngine(vehicleEngine);
      searchConditionDTO.setShopId(shopId);
      List<String> result = searchService.getProductSuggestionList(searchConditionDTO);
      PrintWriter writer = response.getWriter();
      String jsonStr = "";
      if (CollectionUtils.isNotEmpty(result)) {
        jsonStr = JsonUtil.listToJson(result);
      }
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchmaterial");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("productNameValue" + productNameValue + ",productBrandValue:" + productBrandValue + ",productSpecValue:" + productSpecValue + ",productModelValue:" + productModelValue);
      LOG.debug("vehicleBrand" + vehicleBrand + "vehicleModel" + vehicleModel + ",vehicleEngine:" + vehicleEngine + ",vehicleYear:" + vehicleYear);
      LOG.debug("searchWord:" + searchWord + ",searchField:" + searchField);
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 新增车型
   *
   * @param model
   * @param request
   * @param response
   * @param vehicleBrand  需新增的车辆品牌
   * @param vehicleModel  需新增的车型
   * @param vehicleYear   需新增的年代
   * @param vehicleEngine 需新增的排量
   * @author wjl
   */
  @RequestMapping(params = "method=addnewvehicle")
  public void addNewVehicle(ModelMap model, HttpServletRequest request, HttpServletResponse response, String vehicleBrand,
                            String vehicleModel, String vehicleYear, String vehicleEngine) {
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    StringBuffer jsonStr = new StringBuffer("[");
    try {
      Long[] newIds = baseProductService.saveVehicle(null, null, null, null, vehicleBrand, vehicleModel, vehicleYear, vehicleEngine);
      for (Long newId : newIds) {
        jsonStr.append("{\"newId\":\"").append(newId).append("\"},");
      }
      jsonStr.replace(jsonStr.length() - 1, jsonStr.length(), "]");
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr.toString());
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=addnewvehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("vehicleBrand" + vehicleBrand + "vehicleModel" + vehicleModel + ",vehicleEngine:" + vehicleEngine + ",vehicleYear:" + vehicleYear);
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 查询库存
   *
   * @param model
   * @param request
   * @param response
   * @param productId
   */
  @RequestMapping(params = "method=searchInventory")
  public void searchInventory(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long productId) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Long shopID = (Long) request.getSession().getAttribute("shopId");
      InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopID, productId);
      String jsonStr = "[]";
      if (inventoryDTO != null) {
        jsonStr = "[{\"amount\":\"" + inventoryDTO.getAmount() + "\"}]";
      }
      PrintWriter out = response.getWriter();
      out.write(jsonStr);
      out.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchInventory");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("productId:" + productId);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=createaddproducts")
  public String createAddProducts(ModelMap model, HttpServletRequest request) {
    String flagId = request.getParameter("flagId");
    String productVehicleStatus = request.getParameter("productVehicleStatus");
    model.addAttribute("flagId", flagId);
    model.addAttribute("productVehicleStatus", productVehicleStatus);
    return "/product/addProducts";
  }

  @RequestMapping(params = "method=searchproductbyproductname")
  public void searchProductByProductName(ModelMap model, HttpServletRequest request, HttpServletResponse response, String searchWord) {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setRows(15);
      searchConditionDTO.setSearchWord(searchWord);
      searchConditionDTO.setIncludeBasic(false);
      List<String> results = searchProductService.queryProductSuggestionWithSimpleList(searchConditionDTO);
      StringBuffer sb = new StringBuffer("[");
      boolean started = false;
      if (results != null) {
        for (String result : results) {
          if (started) sb.append(",");
          started = true;
          sb.append("{\"name\":\"");
          sb.append(" " + result);
          sb.append("\"}");
        }
        sb.append("]");
        LOG.debug("searchproductbyproductname result: {}", sb.toString());
        PrintWriter writer = response.getWriter();
        writer.write(sb.toString());
        writer.close();
      }
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchproductbyproductname");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("searchWord:" + searchWord);
      LOG.error(e.getMessage(), e);
    }
  }


  //智能匹配查询
  @RequestMapping(params = "method=searchlicenseplate")
  @ResponseBody
  public Object searchlicenseplate(HttpServletRequest request,String plateValue) {
    Long shopId = null;
    if (request.getSession().getAttribute("shopId") != null) {
      shopId = (Long) request.getSession().getAttribute("shopId");
    }
    try {
      return ServiceManager.getService(ISearchVehicleService.class).queryVehicleLicenseNoSuggestion(shopId,plateValue);
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchlicenseplate");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("plateValue:" + plateValue);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  private String getCarNoJsonString(Long shopId,String plateValue) throws Exception{
    IProductService productService = ServiceManager.getService(IProductService.class);
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
      //根据shopId找地区
      String localArea = productService.getAreaByShopId(shopId);
      //根据地区找到车牌
      String localCarNo = productService.getCarNoByAreaName(localArea);
      char localCarNoChar = ' ';
      if (!localCarNo.equals("") && localCarNo != null) {
        localCarNoChar = localCarNo.charAt(0);
      }
      //  String localCarNo="苏B";
      //“苏B1”，“苏”，"S","SE","SE1"测试通过
      // String plateValue="SE1" ;
      //根据搜索关键字找到车牌
      //(1)关键字第一个字符是字母
      char c = ' ';
      if (plateValue != null && plateValue.trim().length() != 0) {
        c = plateValue.charAt(0);

        //如果是字母
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {

          if (plateValue.length() == 1) {
            //根据关键字英文字母查询所对应的车牌首汉字（S-->苏，陕）
            List<String> getCarNosByAreaFirstLetters = productService.getCarNosByAreaFirstLetters(plateValue.toUpperCase());
            //如果查不到就反向查
          if (CollectionUtils.isEmpty(getCarNosByAreaFirstLetters)) {
              //保存车牌号查询结果
              List<String> carNos = new ArrayList<String>();
              //对查询条件反向
              String plateValueReverse = new StringBuffer(plateValue).reverse().toString().toUpperCase();
              //对当前地区车牌反向
              String localCarNoReverse = new StringBuffer(localCarNo).reverse().toString().toUpperCase();
              //本地的所有车牌
              List<String> carsReverse1 = productService.getCarsByCarNosReverse(plateValueReverse, localCarNoReverse, shopId);
              //本地和非本地的
              if (carsReverse1 != null) {
                carNos.addAll(carsReverse1);
              }
              //将反向车牌正向
              List<String> carsForward = new ArrayList<String>();

              for (int i = 0; i < carNos.size(); i++) {
                carsForward.add(new StringBuffer(carNos.get(i)).reverse().toString().toUpperCase());
              }
              if (carsForward != null && carsForward.size() > 0) {
                if (carsForward.size() >= 10) {
                  for (int i = 0; i < 10; i++) {
                    if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                      sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                    }
                  }
                } else {
                  for (int i = 0; i < carsForward.size(); i++) {
                    if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                      sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                    }
                  }
                }
              }
              sb.replace(sb.length() - 1, sb.length(), "]");
              jsonStr = sb.toString();
            } else {
              //保存车牌号查询结果
              List<String> carNos = new ArrayList<String>();
              List<String> carLicensePlates = new ArrayList<String>();
              //将车牌从新组合，当前地区车牌放在顶部
              List<String> newCarNos = new ArrayList<String>();
              for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
//                //如果查询首汉字和当前地区车牌首字符相同
//            if (getCarNosByAreaFirstLetters.get(i).charAt(0) == (localCarNo.charAt(0))) {
//                carLicensePlates
                //首汉字列表
                List<String> carNo = productService.getCarNosByFirstLetters(getCarNosByAreaFirstLetters.get(i).toUpperCase());
                carLicensePlates.addAll(carNo);
              }
              for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
//                //如果查询首汉字和当前地区车牌首字符相同
                if (getCarNosByAreaFirstLetters.get(i).charAt(0) == (localCarNoChar)) {
                  //从关键字的车牌中删除当前地区车牌
                  for (int j = 0; j < carLicensePlates.size(); j++) {
                    if (carLicensePlates.get(i).equals(localCarNo))
                      carLicensePlates.remove(i);
                  }
                  //当前地区车牌放在顶部
                  newCarNos.add(localCarNo.toUpperCase());
                  //将不是当前地区的放在下面
                  for (int k = 0; k < carLicensePlates.size(); k++) {
                    newCarNos.add(carLicensePlates.get(i).toUpperCase());
                  }
                  break;
//              List<String> carNo = productService.getCarNosByFirstLetters(getCarNosByAreaFirstLetters.get(i).toUpperCase());
//              carLicensePlates.addAll(carNo);
                } else {
                  newCarNos.addAll(carLicensePlates);
                  break;
                }
              }

              for (int i = 0; i < newCarNos.size(); i++) {
                //  根据苏，陕查询所对应的车牌号
                List<String> cars = productService.getCarsByCarNos(newCarNos.get(i), shopId);
                if (cars != null) {
                  carNos.addAll(cars);
                }
              }
              //组成json
              if (carNos != null && carNos.size() > 0) {
                if (carNos.size() >= 10) {
                  for (int i = 0; i < 10; i++) {
                    if (!carNos.get(i).equals("") || carNos.get(i) != null) {
                      sb.append("{\"carno\":\"" + carNos.get(i) + "\"},");
                    }
                  }
                } else {
                  for (int i = 0; i < carNos.size(); i++) {
                    if (!carNos.get(i).equals("") || carNos.get(i) != null) {
                      sb.append("{\"carno\":\"" + carNos.get(i) + "\"},");
                    }
                  }
                }
                sb.replace(sb.length() - 1, sb.length(), "]");
                jsonStr = sb.toString();
              }
            }

          } else if (plateValue.length() > 1) {

            if (plateValue.length() <= 6 || plateValue.length() >= 9) {
              //   plateValue=SE
              String plateother = String.valueOf(plateValue.charAt(0));
              //根据关键字英文字母查询所对应的车牌首汉字（S-->苏，陕）
              List<String> getCarNosByAreaFirstLetters = productService.getCarNosByAreaFirstLetters(plateother.toUpperCase());
              //如果查不到就反向查
            if (CollectionUtils.isEmpty(getCarNosByAreaFirstLetters)) {
                List<String> carNos = new ArrayList<String>();
                //对查询条件反向
                String plateValueReverse = new StringBuffer(plateValue).reverse().toString().toUpperCase();
                //对当前地区车牌反向
                String localCarNoReverse = new StringBuffer(localCarNo).reverse().toString().toUpperCase();
                //本地的所有车牌
                List<String> carsReverse1 = productService.getCarsByCarNosReverse(plateValueReverse.toUpperCase(), localCarNoReverse, shopId);
                //本地和非本地的
                if (carsReverse1 != null) {
                  carNos.addAll(carsReverse1);
                }
                //将反向车牌正向
                List<String> carsForward = new ArrayList<String>();
                for (int i = 0; i < carNos.size(); i++) {
                  carsForward.add(new StringBuffer(carNos.get(i)).reverse().toString().toUpperCase());
                }

                if (carsForward != null && carsForward.size() > 0) {
                  if (carsForward.size() >= 10) {
                    for (int i = 0; i < 10; i++) {
                      if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                        sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                      }
                    }
                  } else {
                    for (int i = 0; i < carsForward.size(); i++) {
                      if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                        sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                      }
                    }
                  }
                  sb.replace(sb.length() - 1, sb.length(), "]");
                  jsonStr = sb.toString();
                }

              } //
              else {

                List<String> getCarNosByAreaFirstLettersOther = new ArrayList<String>();
                for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
                  //用于将SE 变为：苏E,陕E

                  StringBuffer buf = new StringBuffer(plateValue);
                  StringBuffer newPlateVale = buf.replace(0, 1, getCarNosByAreaFirstLetters.get(i));
//              String plateValue1 = plateValue.replace(plateValue.charAt(0), getCarNosByAreaFirstLetters.get(i).charAt(0));
                  getCarNosByAreaFirstLettersOther.add(newPlateVale.toString().toUpperCase());
                }
                //保存车牌号查询结果
                List<String> carNos = new ArrayList<String>();
                for (int i = 0; i < getCarNosByAreaFirstLettersOther.size(); i++) {
                  //  根据苏E，陕E查找对应的车牌号
                  List<String> cars = productService.getCarsByCarNos(getCarNosByAreaFirstLettersOther.get(i), shopId);
                  if (cars != null) {
                    carNos.addAll(cars);
                  }
                }
                if (carNos.size() == 0) {
                  List<String> carNosOhter = new ArrayList<String>();
                  //对查询条件反向
                  String plateValueReverse = new StringBuffer(plateValue).reverse().toString().toUpperCase();
                  //对当前地区车牌反向

                  String localCarNoReverse = new StringBuffer(localCarNo).reverse().toString().toUpperCase();
                  //本地的所有车牌
                  List<String> carsReverse1 = productService.getCarsByCarNosReverse(plateValueReverse.toUpperCase(), localCarNoReverse, shopId);
                  //本地和非本地的
                  if (carsReverse1 != null) {
                    carNosOhter.addAll(carsReverse1);
                  }
                  //将反向车牌正向
                  List<String> carsForward = new ArrayList<String>();
                  for (int i = 0; i < carNosOhter.size(); i++) {
                    carsForward.add(new StringBuffer(carNosOhter.get(i)).reverse().toString());
                  }
                  if (carsForward != null && carsForward.size() > 0) {
                    if (carsForward.size() >= 10) {
                      for (int i = 0; i < 10; i++)
                        if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                          sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                        }
                    } else {
                      for (int i = 0; i < carsForward.size(); i++)
                        if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                          sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                        }
                    }
                    sb.replace(sb.length() - 1, sb.length(), "]");
                    jsonStr = sb.toString();
                  }
                } else {
                  //组成json
                  if (carNos.size() >= 10) {
                    for (int i = 0; i < 10; i++)
                      if (!carNos.get(i).equals("") || carNos.get(i) != null) {
                        sb.append("{\"carno\":\"" + carNos.get(i) + "\"},");
                      }
                  } else {
                    for (int i = 0; i < carNos.size(); i++)
                      if (!carNos.get(i).equals("") || carNos.get(i) != null) {
                        sb.append("{\"carno\":\"" + carNos.get(i) + "\"},");
                      }
                  }
                  sb.replace(sb.length() - 1, sb.length(), "]");
                  jsonStr = sb.toString();
                }
              }  //
            } else {
              //如果输入关键字长度等于8
              if (plateValue.length() == 7) {
                if ((plateValue.charAt(1) >= 'a' && plateValue.charAt(1) <= 'z') || (plateValue.charAt(1) >= 'A' && plateValue.charAt(1) <= 'Z')) {
                  //先判断和本地车前缀是否相同
                  //如果相同就把本地车牌号放在前面下拉
                  //如果不相同就排序下拉
                  //先到数据库查找

                  List<String> carsLicenseNos = productService.getCarsByCarNos(plateValue, shopId);
                  if (carsLicenseNos != null) {

                    List<String> carNos = new ArrayList<String>();
                    //先将数据库数据放在前面
                    carNos.addAll(carsLicenseNos);
                    String plateother = String.valueOf(plateValue.charAt(0));
                    //根据关键字英文字母查询所对应的车牌首汉字（S-->苏，陕）
                    List<String> getCarNosByAreaFirstLetters = productService.getCarNosByAreaFirstLetters(plateother.toUpperCase());
                    //将本地车牌放在前面
                    for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
                      if (getCarNosByAreaFirstLetters.get(i).charAt(0) == localCarNoChar)
                        getCarNosByAreaFirstLetters.remove(i);
                    }
                    //将本地车牌放前
                    List<String> newCarNos = new ArrayList<String>();
                    newCarNos.add(String.valueOf(localCarNoChar));
                    for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
                      newCarNos.add(getCarNosByAreaFirstLetters.get(i));
                    }
                    List<String> getCarNosByAreaFirstLettersOther = new ArrayList<String>();
                    for (int i = 0; i < newCarNos.size(); i++) {
                      //用于将SE012355 变为：苏E012355,陕E012355
                      StringBuffer buf = new StringBuffer(plateValue);
                      StringBuffer newPlateVale = buf.replace(0, 1, newCarNos.get(i));
//                                String plateValue1 = plateValue.replace(plateValue.charAt(0), newCarNos.get(i).charAt(0));
                      getCarNosByAreaFirstLettersOther.add(newPlateVale.toString().toUpperCase());
                    }
                    //保存车牌号查询结果

                    for (int i = 0; i < getCarNosByAreaFirstLettersOther.size(); i++) {
                      //  根据苏E，陕E查找对应的车牌号
                      List<String> cars = productService.getCarsByCarNos(getCarNosByAreaFirstLettersOther.get(i), shopId);
                      if (cars != null) {
                        carNos.addAll(cars);
                      }
                    }
                    if (carNos.size() == 0) {
                      carNos.addAll(getCarNosByAreaFirstLettersOther);
                    }
                    //将输入字符加在最后面
                    carNos.add(plateValue.toUpperCase());
                    //将list变为set
                    Set<String> set = new HashSet(carNos);
                    List<String> listcarNos = new ArrayList(set);
                    if (listcarNos.size() == 1) {
                      //将前缀加上组成list,返回JSON
                      //组成json
                      if (listcarNos.size() >= 10) {
                        for (int i = 0; i < 10; i++)
                          if (!listcarNos.get(i).equals("") || listcarNos.get(i) != null) {
                            sb.append("{\"carno\":\"" + listcarNos.get(i) + "\"},");
                          }
                      } else {
                        for (int i = 0; i < listcarNos.size(); i++)
                          if (!listcarNos.get(i).equals("") || listcarNos.get(i) != null) {
                            sb.append("{\"carno\":\"" + listcarNos.get(i) + "\"},");
                          }
                      }
                      sb.replace(sb.length() - 1, sb.length(), "]");
                      jsonStr = sb.toString();
                      //
                    } else {
                      //组成json
                      if (listcarNos.size() >= 10) {
                        for (int i = 0; i < 10; i++)
                          if (!listcarNos.get(i).equals("") || listcarNos.get(i) != null) {
                            sb.append("{\"carno\":\"" + listcarNos.get(i) + "\"},");
                          }
                      } else {
                        for (int i = 0; i < listcarNos.size(); i++)
                          if (!listcarNos.get(i).equals("") || listcarNos.get(i) != null) {
                            sb.append("{\"carno\":\"" + listcarNos.get(i) + "\"},");
                          }
                      }
                      sb.replace(sb.length() - 1, sb.length(), "]");
                      jsonStr = sb.toString();
                    }   //
                  } else {
                    //如果输入关键字长度等于8
                    if (plateValue.length() == 7 || plateValue.length() == 8) {
                      if ((plateValue.charAt(1) >= 'a' && plateValue.charAt(1) <= 'z') || (plateValue.charAt(1) >= 'A' && plateValue.charAt(1) <= 'Z')) {
                        //先判断和本地车前缀是否相同
                        //如果相同就把本地车牌号放在前面下拉
                        //如果不相同就排序下拉
                        //先到数据库查找
                        //找不到
                        //根据关键字英文字母查询所对应的车牌首汉字（S-->苏，陕）
                        String plateother = String.valueOf(plateValue.charAt(0));
                        List<String> getCarNosByAreaFirstLetters = productService.getCarNosByAreaFirstLetters(plateother.toUpperCase());

                        //将本地车牌首汉字放从列表里删除
                        boolean hasLocal = false;
                        for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
                          if (getCarNosByAreaFirstLetters.get(i).charAt(0) == localCarNoChar) {
                            getCarNosByAreaFirstLetters.remove(i);
                            hasLocal = true;
                          }
                        }
                        //将本地车牌首汉字放前
                        List<String> newCarNos = new ArrayList<String>();
                        if (hasLocal) {
                          newCarNos.add(String.valueOf(localCarNoChar));
                        }
                        for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
                          newCarNos.add(getCarNosByAreaFirstLetters.get(i));
                        }
                        List<String> getCarNosByAreaFirstLettersOther = new ArrayList<String>();
                        for (int i = 0; i < newCarNos.size(); i++) {
                          //用于将SE 变为：苏E,陕E
                          StringBuffer buf = new StringBuffer(plateValue);
                          StringBuffer newPlateVale = buf.replace(0, 1, newCarNos.get(i));
                          getCarNosByAreaFirstLettersOther.add(newPlateVale.toString().toUpperCase());
                        }
                        //保存车牌号查询结果
                        List<String> carNos = new ArrayList<String>();
                        for (int i = 0; i < getCarNosByAreaFirstLettersOther.size(); i++) {
                          //  根据苏E，陕E查找对应的车牌号
                          List<String> cars = productService.getCarsByCarNos(getCarNosByAreaFirstLettersOther.get(i), shopId);
                          if (cars != null) {
                            carNos.addAll(cars);
                          }
                        }
                        if (carNos.size() == 0) {
                          carNos.addAll(getCarNosByAreaFirstLettersOther);
                        }
                        //将输入字符加在最后面
                        carNos.add(plateValue.toUpperCase());
                        if (carNos != null && carNos.size() > 0) {
                          if (carNos.size() >= 10) {
                            for (int i = 0; i < 10; i++)
                              if (!carNos.get(i).equals("") || carNos.get(i) != null) {
                                sb.append("{\"carno\":\"" + carNos.get(i) + "\"},");
                              }
                          } else {
                            for (int i = 0; i < carNos.size(); i++)
                              if (!carNos.get(i).equals("") || carNos.get(i) != null) {
                                sb.append("{\"carno\":\"" + carNos.get(i) + "\"},");
                              }
                          }
                          sb.replace(sb.length() - 1, sb.length(), "]");
                          jsonStr = sb.toString();
                        }
                      }
                    }
                  }
                  //找到就下拉
                }
              }
            }
          }

        }
        //如果是汉字
        else if (c >= 0x0391 && c <= 0xFFE5) {
          // 根据：苏--》苏A，苏B，苏C。。。。
          List<String> carNos = productService.getCarNosByFirstLetters(plateValue.toUpperCase());
          //如果找到车牌
          if (carNos.size() > 0) {
            //如果查询首汉字和当前地区车牌首字符相同
            if (plateValue.charAt(0) == (localCarNoChar)) {
              if (plateValue.length() == 1) {
                //从关键字的车牌中删除当前地区车牌
                for (int i = 0; i < carNos.size(); i++) {
                  if (carNos.get(i).equals(localCarNo))
                    carNos.remove(i);
                }
                //将车牌从新组合，当前地区车牌放在顶部
                List<String> newCarNos = new ArrayList<String>();
                //当前地区车牌放在顶部
                newCarNos.add(localCarNo);
                //将不是当前地区的放在下面
                for (int i = 0; i < carNos.size(); i++) {
                  newCarNos.add(carNos.get(i));
                }
                //车牌号码list
                List<String> carslist = new ArrayList<String>();
                for (int i = 0; i < newCarNos.size(); i++) {
                  List<String> cars = productService.getCarsByCarNos(newCarNos.get(i), shopId);
                  if (cars != null) {
                    carslist.addAll(cars);
                  }
                }
                //组成json
                if (carslist != null && carslist.size() > 0) {
                  if (carslist.size() > 10) {
                    for (int i = 0; i < 10; i++) {
                      if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                        sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                      }
                    }
                  } else {
                    for (int i = 0; i < carslist.size(); i++) {
                      if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                        sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                      }
                    }
                  }

                }

              } else {
                //车牌号码list
                List<String> carslist = new ArrayList<String>();
                for (int i = 0; i < carNos.size(); i++) {
                  List<String> cars = productService.getCarsByCarNos(carNos.get(i), shopId);
                  if (cars != null) {
                    carslist.addAll(cars);
                  }
                }
                //组成JSON
                if (carslist != null && carslist.size() > 0) {
                  if (carslist.size() >= 10) {
                    for (int i = 0; i < 10; i++)
                      if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                        sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                      }
                  } else {
                    for (int i = 0; i < carslist.size(); i++)
                      if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                        sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                      }
                  }
                }
              }
              sb.replace(sb.length() - 1, sb.length(), "]");
              jsonStr = sb.toString();
            } else {  //如果汉字和当前地区不同
              //车牌号码list
              List<String> carslist = new ArrayList<String>();
              for (int i = 0; i < carNos.size(); i++) {
                List<String> cars = productService.getCarsByCarNos(carNos.get(i), shopId);
                if (cars != null) {
                  carslist.addAll(cars);
                }
              }
              //组成JSON
              if (carslist != null && carslist.size() > 0) {
                if (carslist.size() >= 10) {
                  for (int i = 0; i < 10; i++)
                    if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                      sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                    }
                } else {
                  for (int i = 0; i < carslist.size(); i++)
                    if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                      sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                    }
                }
                sb.replace(sb.length() - 1, sb.length(), "]");
                jsonStr = sb.toString();
              }

            }

          } else {
            //车牌号码list
            List<String> carslist = new ArrayList<String>();
            List<String> cars = productService.getCarsByCarNos(plateValue.toUpperCase(), shopId);
            if (cars != null) {
              carslist.addAll(cars);
            }
            //组成JSON
            if (carslist != null && carslist.size() > 0) {
              if (carslist.size() >= 10) {
                for (int i = 0; i < 10; i++)
                  if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                    sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                  }
              } else {
                for (int i = 0; i < carslist.size(); i++)
                  if (!carslist.get(i).equals("") || carslist.get(i) != null) {
                    sb.append("{\"carno\":\"" + carslist.get(i) + "\"},");
                  }
              }
              sb.replace(sb.length() - 1, sb.length(), "]");
              jsonStr = sb.toString();
            }
          }
        } else //如果首关键字既不是字母也不是汉字  ，那么直接查找vehicle表。再反着查，同时本地车牌放前
        {
          //先正着查，
          List<String> cars = productService.getCarsByCarNos(plateValue.toUpperCase(), shopId);
          List<String> carsReverse = new ArrayList<String>();

          //（1）将请求字符串反向
          //  (2)查询反向字段
          //  (3)如果有数据就显示
          //正着没查到 ，反着查
          if (cars == null) {
            //对查询条件反向
            String plateValueReverse = new StringBuffer(plateValue).reverse().toString().toUpperCase();

            //对当前地区车牌反向
//
            String localCarNoReverse = new StringBuffer(localCarNo).reverse().toString().toUpperCase();

            //本地的所有车牌
            List<String> carsReverse1 = productService.getCarsByCarNosReverse(plateValueReverse, localCarNoReverse, shopId);
            if (carsReverse1 != null) {
              carsReverse.addAll(carsReverse1);
            }
            //将反向车牌正向
            List<String> carsForward = new ArrayList<String>();
            for (int i = 0; i < carsReverse.size(); i++) {
              carsForward.add(new StringBuffer(carsReverse.get(i)).reverse().toString().toUpperCase());
            }

            if (carsForward != null && carsForward.size() > 0) {
              if (carsForward.size() >= 10) {
                for (int i = 0; i < 10; i++)
                  if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                    sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                  }
              } else {
                for (int i = 0; i < carsForward.size(); i++)
                  if (!carsForward.get(i).equals("") || carsForward.get(i) != null) {
                    sb.append("{\"carno\":\"" + carsForward.get(i) + "\"},");
                  }
              }
              sb.replace(sb.length() - 1, sb.length(), "]");
              jsonStr = sb.toString();
            }

          } else {
            if (cars != null && cars.size() > 0) {
              if (cars.size() >= 10) {
                for (int i = 0; i < 10; i++)
                  if (!cars.get(i).equals("") || cars.get(i) != null) {
                    sb.append("{\"carno\":\"" + cars.get(i) + "\"},");
                  }
              } else {
                for (int i = 0; i < cars.size(); i++)
                  if (!cars.get(i).equals("") || cars.get(i) != null) {
                    sb.append("{\"carno\":\"" + cars.get(i) + "\"},");
                  }
              }
              sb.replace(sb.length() - 1, sb.length(), "]");
              jsonStr = sb.toString();
            }
          }
        }
      }
    return jsonStr;
    }

  //查询地区
  @RequestMapping(params = "method=searchlicenseNo")
  public void searchlicenseNo(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                              String localArea) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    try {
      if (jsonStr.equals("[]")) {
        //根据地区找到车牌
        String localCarNo = productService.getCarNoByAreaNo(Long.valueOf(localArea));
        if (localCarNo != null) {
          sb.append("{\"platecarno\":\"" + localCarNo + "\"},");
          sb.replace(sb.length() - 1, sb.length(), "]");
          jsonStr = sb.toString();
        }
      }
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchlicenseNo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("localArea:" + localArea);
      LOG.error(e.getMessage(), e);
    }
  }

  //返回当前用户的地区车牌号
  @RequestMapping(params = "method=userLicenseNo")
  public void userLicenseNo(ModelMap model, HttpServletRequest request, HttpServletResponse response
  ) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    //根据shopId找地区
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    try {
      ShopDTO shopDTO = configService.getShopById(shopId);
      String localCarNo = shopDTO.getLicencePlate();
      PrintWriter writer = response.getWriter();
      if (localCarNo != null && !"".equals(localCarNo)) {
        sb.append("{\"localCarNo\":\"" + localCarNo + "\"},");
        sb.replace(sb.length() - 1, sb.length(), "]");
        jsonStr = sb.toString();
      } else {
        sb.append("{\"localCarNo\":\"\"},");
        sb.replace(sb.length() - 1, sb.length(), "]");
        jsonStr = sb.toString();
      }
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=userLicenseNo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  //只能匹配7，8位车牌全为字母的车牌转换
  @RequestMapping(params = "method=sevenOrEightLicenseNo")
  public void sevenOrEightLicenseNo(ModelMap model, HttpServletRequest request, HttpServletResponse response
  ) {
    String plateValue = request.getParameter("plateValue");
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = null;
    if (request.getSession().getAttribute("shopId") != null) {
      shopId = (Long) request.getSession().getAttribute("shopId");
    }
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    //根据shopId找地区
    String localArea = productService.getAreaByShopId(shopId);
    //根据地区找到车牌
    String localCarNo = productService.getCarNoByAreaName(localArea);
    char localCarNoChar = ' ';
    if (!localCarNo.equals("") && localCarNo != null) {
      localCarNoChar = localCarNo.charAt(0);
    }
    //  String localCarNo="苏B";
    //“苏B1”，“苏”，"S","SE","SE1"测试通过
    // String plateValue="SE1" ;
    //根据搜索关键字找到车牌
    //(1)关键字第一个字符是字母
    char c = ' ';
    if (!plateValue.equals("") && plateValue != null) {
      c = plateValue.charAt(0);
    }
    //如果输入关键字长度等于8
    if (plateValue.length() == 7 || plateValue.length() == 8) {
      if ((plateValue.charAt(1) >= 'a' && plateValue.charAt(1) <= 'z') || (plateValue.charAt(1) >= 'A' && plateValue.charAt(1) <= 'Z')) {
        //先判断和本地车前缀是否相同
        //如果相同就把本地车牌号放在前面下拉
        //如果不相同就排序下拉
        //先到数据库查找

        List<String> carsLicenseNos = productService.getCarsByCarNos(plateValue, shopId);
        if (carsLicenseNos != null) {

          List<String> carNos = new ArrayList<String>();
          //先将数据库数据放在前面
          carNos.addAll(carsLicenseNos);
          String plateother = String.valueOf(plateValue.charAt(0));
          //根据关键字英文字母查询所对应的车牌首汉字（S-->苏，陕）
          List<String> getCarNosByAreaFirstLetters = productService.getCarNosByAreaFirstLetters(plateother.toUpperCase());
          //将本地车牌放在前面
          for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
            if (getCarNosByAreaFirstLetters.get(i).charAt(0) == localCarNoChar)
              getCarNosByAreaFirstLetters.remove(i);
          }
          //将本地车牌放前
          List<String> newCarNos = new ArrayList<String>();
          newCarNos.add(String.valueOf(localCarNoChar));
          for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
            newCarNos.add(getCarNosByAreaFirstLetters.get(i));
          }
          List<String> getCarNosByAreaFirstLettersOther = new ArrayList<String>();
          for (int i = 0; i < newCarNos.size(); i++) {
            //用于将SE012355 变为：苏E012355,陕E012355
            StringBuffer buf = new StringBuffer(plateValue);
            StringBuffer newPlateVale = buf.replace(0, 1, newCarNos.get(i));
//                                String plateValue1 = plateValue.replace(plateValue.charAt(0), newCarNos.get(i).charAt(0));
            getCarNosByAreaFirstLettersOther.add(newPlateVale.toString().toUpperCase());
          }
          //保存车牌号查询结果

          for (int i = 0; i < getCarNosByAreaFirstLettersOther.size(); i++) {
            //  根据苏E，陕E查找对应的车牌号
            List<String> cars = productService.getCarsByCarNos(getCarNosByAreaFirstLettersOther.get(i), shopId);
            if (cars != null) {
              carNos.addAll(cars);
            }
          }
          if (carNos.size() == 0) {
            carNos.addAll(getCarNosByAreaFirstLettersOther);
          }
          //将输入字符加在最后面
          carNos.add(plateValue.toUpperCase());
          //将list变为set
          Set<String> set = new HashSet(carNos);
          List<String> listcarNos = new ArrayList(set);
          //将前缀加上组成list,返回JSON
          //组成json
          if (!listcarNos.get(0).equals("") || listcarNos.get(0) != null) {
            sb.append("{\"carno\":\"" + listcarNos.get(0) + "\"},");
          }
          sb.replace(sb.length() - 1, sb.length(), "]");
          jsonStr = sb.toString();
        } else {
          //如果输入关键字长度等于8
          if (plateValue.length() == 7 || plateValue.length() == 8) {
            if ((plateValue.charAt(1) >= 'a' && plateValue.charAt(1) <= 'z') || (plateValue.charAt(1) >= 'A' && plateValue.charAt(1) <= 'Z')) {
              //先判断和本地车前缀是否相同
              //如果相同就把本地车牌号放在前面下拉
              //如果不相同就排序下拉
              //先到数据库查找
              //找不到
              //根据关键字英文字母查询所对应的车牌首汉字（S-->苏，陕）
              String plateother = String.valueOf(plateValue.charAt(0));
              List<String> getCarNosByAreaFirstLetters = productService.getCarNosByAreaFirstLetters(plateother.toUpperCase());

              //将本地车牌首汉字放从列表里删除
              boolean hasLocal = false;
              for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
                if (getCarNosByAreaFirstLetters.get(i).charAt(0) == localCarNoChar)
                  getCarNosByAreaFirstLetters.remove(i);
                hasLocal = true;
              }
              //将本地车牌首汉字放前
              List<String> newCarNos = new ArrayList<String>();
              if (hasLocal) {
                newCarNos.add(String.valueOf(localCarNoChar));
              }
              for (int i = 0; i < getCarNosByAreaFirstLetters.size(); i++) {
                newCarNos.add(getCarNosByAreaFirstLetters.get(i));
              }
              List<String> getCarNosByAreaFirstLettersOther = new ArrayList<String>();
              for (int i = 0; i < newCarNos.size(); i++) {
                //用于将SE 变为：苏E,陕E
                StringBuffer buf = new StringBuffer(plateValue);
                StringBuffer newPlateVale = buf.replace(0, 1, newCarNos.get(i));
                getCarNosByAreaFirstLettersOther.add(newPlateVale.toString().toUpperCase());
              }

              //保存车牌号查询结果
              List<String> carNos = new ArrayList<String>();
              for (int i = 0; i < getCarNosByAreaFirstLettersOther.size(); i++) {
                //  根据苏E，陕E查找对应的车牌号
                List<String> cars = productService.getCarsByCarNos(getCarNosByAreaFirstLettersOther.get(i), shopId);
                if (cars != null) {
                  carNos.addAll(cars);
                }
              }
              if (carNos.size() == 0) {
                carNos.addAll(getCarNosByAreaFirstLettersOther);
              }
              //将输入字符加在最后面
              carNos.add(plateValue.toUpperCase());
              if (!carNos.get(0).equals("") || carNos.get(0) != null) {
                sb.append("{\"carno\":\"" + carNos.get(0) + "\"},");
              }
              sb.replace(sb.length() - 1, sb.length(), "]");
              jsonStr = sb.toString();
            }

          }
        }

      }
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=sevenOrEightLicenseNo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=searchLicenceNoByCustomerId")
  public void searchLicenceNoByCustomerId(
      ModelMap model, HttpServletRequest request, HttpServletResponse response, Long customerId) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<CarDTO> carDTOs = userService.getVehiclesByCustomerId(shopId, customerId);

      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.listToJson(carDTOs));
      writer.close();
    } catch (IOException e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchLicenceNoByCustomerId");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
  }

  private String toJsonStr(ProductBarcode productBarcode) {
    if (productBarcode == null) return "";
    return "{\"barcode\":\"" + WebUtil.toJsonStr(productBarcode.getBarcode()) + "\"," +
        "\"productName\":\"" + WebUtil.toJsonStr(productBarcode.getName()) + "\"," +
        "\"productBrand\":\"" + WebUtil.toJsonStr(productBarcode.getBrand()) + "\"," +
        "\"productSpec\":\"" + WebUtil.toJsonStr(productBarcode.getSpec()) + "\"," +
        "\"productModel\":\"" + WebUtil.toJsonStr(productBarcode.getModel()) + "\"," +
        "\"productVehicleStatus\":\"" + WebUtil.toJsonStr(productBarcode.getProductVehicleStatus()) + "\"," +
        "\"vehicleBrand\":\"" + WebUtil.toJsonStr(productBarcode.getProductVehicleBrand()) + "\"," +
        "\"vehicleModel\":\"" + WebUtil.toJsonStr(productBarcode.getProductVehicleModel()) + "\"," +
        "\"vehicleYear\":\"" + WebUtil.toJsonStr(productBarcode.getProductVehicleYear()) + "\"," +
        "\"purchasePrice\":\"" + WebUtil.toJsonStr(productBarcode.getPrice()) + "\"," +
        "\"vehicleEngine\":\"" + WebUtil.toJsonStr(productBarcode.getProductVehicleEngine()) + "\"}";
  }

  //tagqxy to delete
  @RequestMapping(params = "method=ajaxSearchBarcode")
  public void ajaxSearchBarcode(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam("barcode") String barcode) throws Exception {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      PrintWriter writer = response.getWriter();
      IProductService productService = ServiceManager.getService(IProductService.class);
      String jsonStr = "";
      productService.searchBarcode(shopId, barcode);
      writer.write(toJsonStr(productService.searchBarcode(shopId, barcode)));
      writer.close();
    } catch (Exception e) {
      LOG.error("product.do?method=ajaxSearchBarcode\n" +
          "shopId:" + shopId + "\n" +
          "barcode:" + barcode + "\n" +
          e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getProductStorehouseInventoryByProductLocalInfoIds")
  @ResponseBody
  public Object getProductStorehouseInventoryByProductLocalInfoIds(HttpServletRequest request,Long storehouseId,Long[] productLocalInfoIds,String[] units) throws Exception {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      Map<String, Object> result = new HashMap<String, Object>();
      Map<Long, Double> storehouseInventoryMap = new HashMap<Long, Double>();
      Map<Long, String> storehouseStorageBinMap = new HashMap<Long, String>();
      if(!ArrayUtils.isEmpty(productLocalInfoIds)) {
        Set<Long> productLocalInfoIdSet = new HashSet<Long>();
        CollectionUtils.addAll(productLocalInfoIdSet, productLocalInfoIds);
        IProductService productService = ServiceManager.getService(IProductService.class);
        Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productLocalInfoIdSet);
        ProductDTO productDTO = null;
        if (storehouseId != null) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productLocalInfoIds);
          StoreHouseInventoryDTO storeHouseInventoryDTO = null;
          for (int i = 0; i < productLocalInfoIds.length; i++) {
            storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productLocalInfoIds[i]);
            if (storeHouseInventoryDTO != null) {
              productDTO = productDTOMap.get(productLocalInfoIds[i]);
              if (!ArrayUtils.isEmpty(units) && StringUtils.isNotBlank(units[i]) && UnitUtil.isStorageUnit(units[i], productDTO)) {      //入库单位是库存大单位
                storehouseInventoryMap.put(productLocalInfoIds[i], NumberUtil.round(storeHouseInventoryDTO.getAmount() / productDTO.getRate(), NumberUtil.MONEY_PRECISION));
              } else {
                storehouseInventoryMap.put(productLocalInfoIds[i], NumberUtil.round(storeHouseInventoryDTO.getAmount(), NumberUtil.MONEY_PRECISION));
              }
              storehouseStorageBinMap.put(productLocalInfoIds[i], storeHouseInventoryDTO.getStorageBin());
            } else {
              storehouseInventoryMap.put(productLocalInfoIds[i], 0d);
            }
          }
        } else {//用总库存  仓位为空
          IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
          Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productLocalInfoIdSet);
          InventoryDTO inventoryDTO = null;
          for (int i = 0; i < productLocalInfoIds.length; i++) {
            inventoryDTO = inventoryDTOMap.get(productLocalInfoIds[i]);
            if (inventoryDTO != null) {
              productDTO = productDTOMap.get(productLocalInfoIds[i]);
              if (!ArrayUtils.isEmpty(units) && StringUtils.isNotBlank(units[i]) && UnitUtil.isStorageUnit(units[i], productDTO)) {      //入库单位是库存大单位
                storehouseInventoryMap.put(productLocalInfoIds[i], NumberUtil.round(inventoryDTO.getAmount() / productDTO.getRate(), NumberUtil.MONEY_PRECISION));
              } else {
                storehouseInventoryMap.put(productLocalInfoIds[i], NumberUtil.round(inventoryDTO.getAmount(), NumberUtil.MONEY_PRECISION));
              }
            } else {
              storehouseInventoryMap.put(productLocalInfoIds[i], 0d);
            }
          }
        }
      }
      result.put("StoreHouseInventoryMap",storehouseInventoryMap);
      result.put("StorehouseStorageBinMap",storehouseStorageBinMap);
      return result;
    } catch (Exception e) {
      LOG.error("product.do?method=getProductStorehouseInventoryByProductLocalInfoIds\n" +
          "shopId:" + shopId + "\n" +
          "productLocalInfoIds:" + StringUtil.arrayToStr(",",productLocalInfoIds) + "\n" +
          "units:" + StringUtil.arrayToStr(",",units) + "\n" +
          e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getProductStorehouseStorageBinByProductLocalInfoIds")
  @ResponseBody
  public Object getProductStorehouseStorageBinByProductLocalInfoIds(HttpServletRequest request,Long storehouseId,Long[] productLocalInfoIds) throws Exception {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      Map<String, Object> result = new HashMap<String, Object>();
      Map<Long, String> storehouseStorageBinMap = new HashMap<Long, String>();
      if(!ArrayUtils.isEmpty(productLocalInfoIds)) {
        if (storehouseId != null) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productLocalInfoIds);
          StoreHouseInventoryDTO storeHouseInventoryDTO = null;
          for (int i = 0; i < productLocalInfoIds.length; i++) {
            storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productLocalInfoIds[i]);
            if (storeHouseInventoryDTO != null) {
              storehouseStorageBinMap.put(productLocalInfoIds[i], storeHouseInventoryDTO.getStorageBin());
            }
          }
        }
      }
      result.put("StorehouseStorageBinMap",storehouseStorageBinMap);
      return result;
    } catch (Exception e) {
      LOG.error("product.do?method=getProductStorehouseStorageBinByProductLocalInfoIds\n" +
          "shopId:" + shopId + "\n" +
          "productLocalInfoIds:" + StringUtil.arrayToStr(",",productLocalInfoIds) + "\n" +
          e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping (params = "method=updateSingleInSalesAmount")
  @ResponseBody
  public Object updateSingleInSalesAmount(HttpServletRequest request,Long productLocalInfoId, Double inSalesAmount) {

    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(productLocalInfoId == null){
        LOG.info("shopId:{}开始更新失败,productLocalInfoId为空:{},inSalesAmount:{}", new Object[]{shopId, productLocalInfoId, inSalesAmount});
        throw new Exception("productLocalInfoId is null!");
      }
      IProductService productService = ServiceManager.getService(IProductService.class);
      ProductDTO productDTO = new ProductDTO();
      productDTO.setProductLocalInfoId(productLocalInfoId);
      productDTO.setInSalesAmount(inSalesAmount);
      ProductDTO[] productDTOs = new ProductDTO[1];
      productDTOs[0]=productDTO;
      productService.updateProductInSalesAmount(shopId,productDTOs);
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productLocalInfoId);
      //保存修改的productId到solr
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      recentChangedProductMap.put(productDTO.getProductLocalInfoId(), new Pair(System.currentTimeMillis(), false));
      return new Result();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=updateSingleInSalesAmount");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("shopId:{}更新单个商品上架数量出错,productLocalInfoId:{},inSalesAmount:{}" + e.getMessage(), new Object[]{shopId, productLocalInfoId, inSalesAmount, e});
    }
    return null;
  }

  @RequestMapping (params = "method=updateMultipleInSalesAmount")
  @ResponseBody
  public Object updateMultipleInSalesAmount(HttpServletRequest request,InventoryLimitDTO inventoryLimitDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if(inventoryLimitDTO.getProductDTOs() == null||inventoryLimitDTO.getProductDTOs().length == 0){
        LOG.info("shopId:{}开始更新多个商品库存批发价失败,productIds信息为空:{}",shopId,inventoryLimitDTO.getProductDTOs());
        return new Result();
      }
      IProductService productService = ServiceManager.getService(IProductService.class);
      Long[] productLocalInfoIds = new Long[inventoryLimitDTO.getProductDTOs().length];
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      for (int i = 0, len = inventoryLimitDTO.getProductDTOs().length; i < len; i++) {
        if(inventoryLimitDTO.getProductDTOs()[i].getProductLocalInfoId()!=null){
          productLocalInfoIds[i]= inventoryLimitDTO.getProductDTOs()[i].getProductLocalInfoId();
          recentChangedProductMap.put(productLocalInfoIds[i], new Pair(System.currentTimeMillis(), false));
        }
      }
      productService.updateProductInSalesAmount(shopId,inventoryLimitDTO.getProductDTOs());
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productLocalInfoIds);
      return new Result();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=updateMultipleInSalesAmount");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("shopId:{}更新多个商品上架数量出错,"+e.getMessage(),new Object[]{shopId,e});
    }
    return null;
  }

  @RequestMapping (params ="method=updateMultipleGuaranteePeriod")
  @ResponseBody
  public Object updateMultipleGuaranteePeriod(HttpServletRequest request,HeavyProductDTO heavyProductDTO) {
    Result result=new Result();
    try {
      if(heavyProductDTO==null){
        return result.LogErrorMsg("参数异常。");
      }
      ServiceManager.getService(IProductService.class).updateProductGuaranteePeriod(result,WebUtil.getShopId(request),heavyProductDTO.getProductDTOs());
      if(result.isSuccess())
        ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(WebUtil.getShopId(request),ArrayUtil.toLongArr(result.getDataList()));
      return new Result();
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }

  @RequestMapping (params ="method=updateMultipleInSalesPrice")
  @ResponseBody
  public Object updateMultipleInSalesPrice(HttpServletRequest request,HeavyProductDTO heavyProductDTO) {
    Result result=new Result();
    try {
      if(heavyProductDTO==null){
        return result.LogErrorMsg("参数异常。");
      }
      ServiceManager.getService(IProductService.class).updateProductInSalesPrice(result,WebUtil.getShopId(request),heavyProductDTO.getProductDTOs());
      if(result.isSuccess())
        ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(WebUtil.getShopId(request),ArrayUtil.toLongArr(result.getDataList()));
      return new Result();
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }

  @RequestMapping (params = "method=validateSaveNewProduct")
  @ResponseBody
  public Object validateSaveNewProduct(HttpServletRequest request, HttpServletResponse response, ProductDTO productDTO) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
     try{
       productDTO.setShopId(shopId);
       prepareForSaveProduct(request,productDTO);
       return txnService.validateSaveNewProduct(productDTO);
     } catch (Exception e){
       LOG.error("method=validateSaveNewProduct出错,shopId:{},"+e.getMessage(),new Object[]{shopId,e});
       result.setMsg("商品保存校验，网络异常!");
       result.setSuccess(false);
       return result;
     }
  }


  @RequestMapping (params = "method=saveNewProduct")
  @ResponseBody
  public Object saveNewProduct(HttpServletRequest request,HttpServletResponse response,ProductDTO productDTO){
    Long shopId = WebUtil.getShopId(request);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IImageService imageService =ServiceManager.getService(IImageService.class);
    Result result = new Result();
    try{
      productDTO.setShopId(shopId);
      prepareForSaveProduct(request,productDTO);
      txnService.batchSaveProductWithReindex(shopId, null, new ProductDTO[]{productDTO});
      configService.saveOrUpdateUnitSort(productDTO.getShopId(),new ProductDTO[]{productDTO});
      //图片
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
      if(productDTO.getImageCenterDTO()!=null && StringUtils.isNotBlank(productDTO.getImageCenterDTO().getProductMainImagePath())){
        DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopId,productDTO.getProductLocalInfoId(), DataType.PRODUCT,ImageType.PRODUCT_MAIN_IMAGE,0);
        dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,productDTO.getImageCenterDTO().getProductMainImagePath()));
        imageService.saveOrUpdateDataImageDTOs(shopId,imageTypeSet,DataType.PRODUCT,productDTO.getProductLocalInfoId(),dataImageRelationDTO);
      }

      return result;
    } catch (Exception e){
      LOG.error("method=saveNewProduct,shopId:{},"+e.getMessage(),new Object[]{shopId,e});
      result.setMsg("商品保存校验，网络异常!");
      result.setSuccess(false);
      return result;
    }
   }

  private void prepareForSaveProduct(HttpServletRequest request,ProductDTO productDTO){
    if (productDTO != null) {
      productDTO.setStorageUnit(productDTO.getStorageUnit() == null ? null : productDTO.getStorageUnit().trim());
      productDTO.setSellUnit(productDTO.getSellUnit() == null ? null : productDTO.getSellUnit().trim());
      if (StringUtils.isNotBlank(productDTO.getStorageUnit()) && StringUtils.isEmpty(productDTO.getSellUnit())) {
        productDTO.setSellUnit(productDTO.getStorageUnit());
      } else if (StringUtils.isNotBlank(productDTO.getSellUnit()) && StringUtils.isEmpty(productDTO.getStorageUnit())) {
        productDTO.setStorageUnit(productDTO.getSellUnit());
      }
    }

  }

  @RequestMapping (params = "method=saveProductImageRelation")
  @ResponseBody
  public Object saveProductImageRelation(HttpServletRequest request,Long productLocalInfoId,String imagePath){
    Long shopId = WebUtil.getShopId(request);
    IImageService imageService = ServiceManager.getService(IImageService.class);
    Result result = new Result();
    try{
      if(shopId==null) throw new Exception("shopId is null!");
      if(productLocalInfoId==null) throw new Exception("productLocalInfoId is null!");
      if(StringUtils.isBlank(imagePath)) throw new Exception("imagePath is null!");
      DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopId, productLocalInfoId, DataType.PRODUCT, ImageType.PRODUCT_MAIN_IMAGE,0);
      dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,imagePath));
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
      imageService.saveOrUpdateDataImageDTOs(shopId,imageTypeSet,DataType.PRODUCT,productLocalInfoId,dataImageRelationDTO);
      result.setData(dataImageRelationDTO);
      return result;
    } catch (Exception e){
      LOG.error("product.do?method=saveProductImageRelation,shopId:{}," + e.getMessage(), new Object[]{shopId, e});
      result.setSuccess(false);
      return result;
    }
  }

  @RequestMapping (params = "method=deleteProductImageRelation")
  @ResponseBody
  public Object deleteProductImageRelation(HttpServletRequest request,Long dataImageRelationId){
    Long shopId = WebUtil.getShopId(request);
    IImageService imageService = ServiceManager.getService(IImageService.class);
    Result result = new Result();
    try{
      if(shopId==null) throw new Exception("shopId is null!");
      if(dataImageRelationId==null) throw new Exception("dataImageRelationId is null!");
      imageService.deleteDataImageRelationDTOById(shopId,dataImageRelationId);
      return result;
    } catch (Exception e){
      LOG.error("product.do?method=deleteProductImageRelation,shopId:{}," + e.getMessage(), new Object[]{shopId, e});
      result.setSuccess(false);
      return result;
    }
  }

    @RequestMapping(params = "method=checkVehicleBrandModel")
    @ResponseBody
    public Object checkVehicleBrandModel(HttpServletRequest request,String type,String value) {
        try {
            Long shopId = (Long) request.getSession().getAttribute("shopId");
            if (shopId == null) throw new Exception("shop Id is null");
            if(StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)){
                Set<String> searchSet = new HashSet<String>();
                searchSet.add(value);
                if(type.equals("model")){
                    return MapUtils.isNotEmpty(ServiceManager.getService(IStandardBrandModelService.class).getNameStandardVehicleModelMapByNames(searchSet));
                }else{
                    return MapUtils.isNotEmpty(ServiceManager.getService(IStandardBrandModelService.class).getNameStandardVehicleBrandMapByNames(searchSet));
                }
            }
        } catch (Exception e) {
            LOG.debug("/product.do?method=checkVehicleBrandModel");
            LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
            LOG.error(e.getMessage(), e);
        }
        return false;
    }
}
