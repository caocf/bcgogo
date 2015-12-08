package com.bcgogo.search;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.service.suggestion.ISearchSuggestionService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.StoreHouseInventoryDTO;
import com.bcgogo.txn.model.Category;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.solr.ISolrMergeService;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequestMapping("/searchInventoryIndex.do")
public class SearchController {
  private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);
	private IProductService productService;

	public IProductService getProductService() {
		if(productService == null){
			productService = ServiceManager.getService(IProductService.class);
		}
		return productService;
	}

	private String vehicleInfo(String vehicleBrand, String vehicleModel, String vehicleYear, String vehicleEngine) {
    StringBuffer stringBuffer = new StringBuffer();
    if (StringUtils.isBlank(vehicleBrand)) {
      vehicleBrand = SearchConstant.PRODUCT_PRODUCTSTATUS_ALL_VALUE;
    }
    stringBuffer.append(vehicleBrand);
    if (StringUtils.isNotBlank(vehicleModel)) {
      stringBuffer.append(',');
      stringBuffer.append(vehicleModel);
    }
    if (StringUtils.isNotBlank(vehicleYear)) {
      stringBuffer.append(',');
      stringBuffer.append(vehicleYear);
    }
    if (StringUtils.isNotBlank(vehicleEngine)) {
      stringBuffer.append(',');
      stringBuffer.append(vehicleEngine);
    }
    return stringBuffer.toString();
  }

  private static boolean stringEquals(String str1, String str2) {
    if (StringUtils.isBlank(str1) && StringUtils.isBlank(str2)) {
      return true;
    }
    if (StringUtils.isBlank(str1) && StringUtils.isNotBlank(str2)) {
      return false;
    }
    if (StringUtils.isNotBlank(str1) && StringUtils.isBlank(str2)) {
      return false;
    }
    return str1.trim().equals(str2.trim());
  }

  private final static String ORDER_TYPE_VEHICLE = "vehicle";
  private final static String ORDER_TYPE_GOODSALE = "goodsale";


  @RequestMapping(params = "method=searchproduct")   //cuDetail.jsp 分页查询
  public String searchProduct(ModelMap model, HttpServletRequest request, InventorySearchIndexDTO inventorySearchIndexDTO) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Integer pageNo = productService.getInventoryIndexPageNo(request, inventorySearchIndexDTO.getPageStatus(), "pageNo");
    try {
      SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setShopId(shopId);
      if(inventorySearchIndexDTO.getMaxResult() == null){
        inventorySearchIndexDTO.setMaxResult(10);
      }
      searchConditionDTO.setStart((pageNo - 1) * inventorySearchIndexDTO.getMaxResult());
      searchConditionDTO.setRows(inventorySearchIndexDTO.getMaxResult());
      searchConditionDTO.setProductName(inventorySearchIndexDTO.getProductName());
      searchConditionDTO.setProductBrand(inventorySearchIndexDTO.getProductBrand());
      searchConditionDTO.setProductModel(inventorySearchIndexDTO.getProductModel());
      searchConditionDTO.setProductSpec(inventorySearchIndexDTO.getProductSpec());
      searchConditionDTO.setProductVehicleBrand(inventorySearchIndexDTO.getBrand());
      searchConditionDTO.setProductVehicleModel(inventorySearchIndexDTO.getModel());

      List<InventorySearchIndexDTO> result = new ArrayList<InventorySearchIndexDTO>();

      ProductSearchResultListDTO productSearchResultListDTO = ServiceManager.getService(ISearchProductService.class).queryProductWithStdQuery(searchConditionDTO);
      //合并solr延时提交memcach中的商品
	    ServiceManager.getService(ISolrMergeService.class).mergeCacheProductDTO(shopId, productSearchResultListDTO.getProducts());
      ServiceManager.getService(IInventoryService.class).getLimitAndAchievementForProductDTOs(productSearchResultListDTO.getProducts(),shopId);
      if (CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())) {
        for (ProductDTO productDTO : productSearchResultListDTO.getProducts()) {
          if(StringUtils.isBlank(productDTO.getSellUnit())){
            productDTO.setUnit(ProductUnitCache.getUnitByProductName(productDTO.getName()));
          }else{
            productDTO.setUnit(productDTO.getSellUnit());
          }
          result.add(productDTO.toInventorySearchIndexDTO());
        }
      }

      model.addAttribute("inventorySearchIndexDTOList", result);
      model.addAttribute("inventorySearchIndexDTO", inventorySearchIndexDTO);
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=searchproduct");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(inventorySearchIndexDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return "/search/cuDetail";
  }


  @RequestMapping(params = "method=searchInventorySearchIndexCountForVehicle")
  public void searchInventorySearchIndexCountForVehicle(HttpServletRequest request, HttpServletResponse response, String productName, String productBrand,
                                                        String productSpec, String productModel, String pvBrand, String pvModel,
                                                        String pvYear, String pvEngine) {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long count = searchService.searchInventorySearchIndexCountForVehicle(shopId, productName,
        productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine);
    PrintWriter printWriter = null;
    try {
      printWriter = response.getWriter();
    } catch (IOException e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=searchInventorySearchIndexCountForVehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("productName:" + productName + ",productBrand:" + productBrand + ",productSpec:" + productSpec + ",productModel:" + productModel);
      LOG.debug("pvBrand" + pvBrand + ",pvModel:" + pvModel + ",pvEngine:" + pvEngine + ",pvYear:" + pvYear);
      LOG.error(e.getMessage(), e);
    }
    printWriter.write("{\"count\":\"" + count + "\"}");
    printWriter.close();
  }

  @RequestMapping(params = "method=searchInventorySearchIndexCountForOneVehicle")
  public void searchInventorySearchIndexCountForOneVehicle(HttpServletRequest request, HttpServletResponse response, String productName, String productBrand,
                                                           String productSpec, String productModel, String pvBrand, String pvModel,
                                                           String pvYear, String pvEngine) {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long count = searchService.searchInventorySearchIndexCountForOneVehicle(shopId, productName,
          productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine);
      PrintWriter printWriter = null;
      printWriter = response.getWriter();
      printWriter.write("{\"count\":\"" + count + "\"}");
      printWriter.close();
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=searchInventorySearchIndexCountForOneVehicle");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("productName:" + productName + ",productBrand:" + productBrand + ",productSpec:" + productSpec + ",productModel:" + productModel);
      LOG.debug("pvBrand" + pvBrand + ",pvModel:" + pvModel + ",pvEngine:" + pvEngine + ",pvYear:" + pvYear);
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=searchInventorySearchIndexCount")
  public void searchInventorySearchIndexCount(HttpServletRequest request, HttpServletResponse response, String productName, String productBrand,
                                              String productSpec, String productModel, String pvBrand, String pvModel,
                                              String pvYear, String pvEngine) {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      Long count = searchService.searchInventorySearchIndexCount(shopId, productName,
          productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, null);
      PrintWriter printWriter = null;

      printWriter = response.getWriter();

      printWriter.write("{\"count\":\"" + count + "\"}");
      printWriter.close();
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=searchInventorySearchIndexCount");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("productName:" + productName + ",productBrand:" + productBrand + ",productSpec:" + productSpec + ",productModel:" + productModel);
      LOG.debug("pvBrand" + pvBrand + ",pvModel:" + pvModel + ",pvEngine:" + pvEngine + ",pvYear:" + pvYear);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=ajaxInventorySearchIndex")
  @ResponseBody
  public Object ajaxInventorySearchIndex(HttpServletRequest request, SearchConditionDTO searchConditionDTO) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      searchConditionDTO.setShopId(shopId);
      if (searchConditionDTO == null || shopVersionId == null || shopId == null || (searchConditionDTO.getProductId() == null && searchConditionDTO.isEmptyOfProductInfo())) {
        return null;
      }
      InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
      ProductDTO productDTO = null;
      if (searchConditionDTO.getProductId() == null) {
        productDTO = new ProductDTO();
        productDTO.setName(searchConditionDTO.getProductName());
        productDTO.setBrand(searchConditionDTO.getProductBrand());
        productDTO.setSpec(searchConditionDTO.getProductSpec());
        productDTO.setModel(searchConditionDTO.getProductModel());
        productDTO.setProductVehicleBrand(searchConditionDTO.getProductVehicleBrand());
        productDTO.setProductVehicleModel(searchConditionDTO.getProductVehicleModel());
        productDTO.setCommodityCode(searchConditionDTO.getCommodityCode());
        List<ProductDTO> productDTOList = getProductService().getProductDTOsBy7P(shopId, productDTO);
        if (CollectionUtils.isNotEmpty(productDTOList)) {
          productDTO = getProductService().getProductById(productDTOList.get(0).getId(), shopId);
        } else {
          if(StringUtils.isBlank(inventorySearchIndexDTO.getSellUnit())){
            inventorySearchIndexDTO.setUnit(ProductUnitCache.getUnitByProductName(productDTO.getName()));
          }
          return inventorySearchIndexDTO;
        }
      } else {
        productDTO = getProductService().getProductByProductLocalInfoId(searchConditionDTO.getProductId(), shopId);
      }
      //填充 营业分类
      if (productDTO != null && productDTO.getBusinessCategoryId() != null) {
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        Category category = rfiTxnService.getCategoryById(shopId, productDTO.getBusinessCategoryId());
        if (category != null) {
          productDTO.setBusinessCategoryName(category.getCategoryName());
        }
      }
      //填充图片
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,false,productDTO);
      inventorySearchIndexDTO.setProductDTO(productDTO);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      InventoryDTO inventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productDTO.getProductLocalInfoId());
      inventorySearchIndexDTO.setInventoryDTO(inventoryDTO);
      //如果是有仓库的版本 并且 有仓库id   校验仓库 库存//仓库为空就显示总库存
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
        if(searchConditionDTO.getStorehouseId()!=null){
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(searchConditionDTO.getStorehouseId(), productDTO.getProductLocalInfoId());
          if (storeHouseInventoryDTO != null) {
            inventorySearchIndexDTO.setAmount(storeHouseInventoryDTO.getAmount());
            inventorySearchIndexDTO.setStorageBin(storeHouseInventoryDTO.getStorageBin());
          }else {
            inventorySearchIndexDTO.setAmount(0d);
            inventorySearchIndexDTO.setStorageBin(null);
          }
        }else{ //不用更新库存   用原来的总库存 但是 货位放空
          inventorySearchIndexDTO.setStorageBin(null);
        }

      }
      if(StringUtils.isBlank(inventorySearchIndexDTO.getSellUnit())){
        inventorySearchIndexDTO.setUnit(ProductUnitCache.getUnitByProductName(inventorySearchIndexDTO.getProductName()));
      }
      return inventorySearchIndexDTO;
    } catch (Exception e) {
      LOG.debug("searchInventoryIndex.do?method=ajaxInventorySearchIndex");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId"));
      LOG.debug("searchConditionDTO:" + searchConditionDTO.toString());
      WebUtil.reThrow(LOG, e);
    }
    return null;
  }

  @RequestMapping(params = "method=createCuDeatil")
  public String multiFieldSearch(ModelMap model, HttpServletRequest request, SearchConditionDTO searchConditionDTO,
                                 String pageStatus, Integer maxResult, Integer indexNum) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setStart((ServiceManager.getService(IProductService.class).getInventoryIndexPageNo(request, pageStatus, "pageNo") - 1) * maxResult);
      searchConditionDTO.setRows(maxResult);

      InventorySearchIndexDTO isiDTO = new InventorySearchIndexDTO();
      isiDTO.setProductName(searchConditionDTO.getProductName());
      isiDTO.setProductBrand(searchConditionDTO.getProductBrand());
      isiDTO.setProductModel(searchConditionDTO.getProductModel());
      isiDTO.setProductSpec(searchConditionDTO.getProductSpec());
      isiDTO.setBrand(searchConditionDTO.getProductVehicleBrand());
      isiDTO.setModel(searchConditionDTO.getProductVehicleModel());
      isiDTO.setMaxResult(maxResult);
      isiDTO.setIndexNum(indexNum);
      isiDTO.setVehicleNull(StringUtils.isBlank(searchConditionDTO.getProductVehicleBrand())
          && StringUtils.isBlank(searchConditionDTO.getProductVehicleModel()));
      isiDTO.setOrderType(request.getParameter("orderType"));
      model.addAttribute("inventorySearchIndexDTO", isiDTO);


      //不知道field的情况下
      ProductSearchResultListDTO productSearchResultListDTO = ServiceManager.getService(ISearchProductService.class).queryProductWithStdQuery(searchConditionDTO);
      //合并solr延时提交memcach中的商品
	    ServiceManager.getService(ISolrMergeService.class).mergeCacheProductDTO(shopId, productSearchResultListDTO.getProducts());
	    ServiceManager.getService(IInventoryService.class).getLimitAndAchievementForProductDTOs(productSearchResultListDTO.getProducts(),shopId);

      List<InventorySearchIndexDTO> result = new ArrayList<InventorySearchIndexDTO>();
      if (CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())) {
        for (ProductDTO productDTO : productSearchResultListDTO.getProducts()) {
          result.add(productDTO.toInventorySearchIndexDTO());
          }
        }

      model.addAttribute("inventorySearchIndexDTOList", result);
    } catch (Exception e) {
      LOG.error("/searchInventoryIndex.do?method=createCuDeatil\n" +
          "shopId:" + shopId + "\n" +
          "productName:" + searchConditionDTO.getProductName() + "\n" +
          "productBrand:" + searchConditionDTO.getProductBrand() + "\n" +
          "productSpec:" + searchConditionDTO.getProductSpec() + "\n" +
          "productModel:" + searchConditionDTO.getProductModel() + "\n" +
          "vehicleBrand" + searchConditionDTO.getProductVehicleBrand() + "\n" +
          "vehicleModel:" + searchConditionDTO.getProductVehicleModel() + "\n" +
          "pageStatus:" + pageStatus + "\n" +
          "maxResult:" + maxResult + "\n" +
          "indexNum:" + indexNum + "\n", e);
    }
    return "/search/cuDetail";
  }

  /**
   * 根据条件查询在线客户供应商
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getCustomerSupplierOnlineSuggestion")
  @ResponseBody
  public Object getSupplierOrCustomerShopSuggestion(HttpServletRequest request,String searchWord,String uuid,
                                                    String customerOrSupplier,String shopRange) {
    try {
      //数据校验
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) throw new Exception("shopId can't be null.");
      if (customerOrSupplier == null) throw new Exception("customerOrSupplier can't be null.");
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IApplyService applyService = ServiceManager.getService(IApplyService.class);
      ShopDTO currentShopDTO = configService.getShopById(shopId);
      boolean isTestShop = applyService.isTestShop(currentShopDTO);

      Map<String, Object> result = new HashMap<String, Object>();
      List<Map> dropDownList = new ArrayList<Map>();

      List<ShopDTO> shopDTOList = applyService.getSupplierOrCustomerShopSuggestion(shopId,searchWord,isTestShop,
          customerOrSupplier,shopRange);
      if (CollectionUtils.isNotEmpty(shopDTOList)) {
        Map<String, Object> dropDownItem = null;
        for (ShopDTO shopDTO : shopDTOList) {
          dropDownItem = new HashMap<String, Object>();
          dropDownItem.put("label", shopDTO.getName());
          dropDownItem.put("type", "option");  //目前只使用   option  （category）暂时不用
          dropDownList.add(dropDownItem);
        }
      }
      result.put("uuid", uuid);
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getSupplierOrCustomerShopSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
  /**
   * 根据条件查询客户供应商
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getCustomerSupplierSuggestion")
  @ResponseBody
  public Object getCustomerSupplierSuggestion(HttpServletRequest request,CustomerSupplierSearchConditionDTO customerSupplierSearchConditionDTO,String[] titles) {
    StopWatch sw = new StopWatch();
//    sw.start("getcustomerSupplierSuggestion prepare");
    try {
      //数据校验
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) throw new Exception("shopId can't be null.");
      Map<String, Object> result = new HashMap<String, Object>();
      List<Map> dropDownList = new ArrayList<Map>();
      customerSupplierSearchConditionDTO.setShopId(shopId);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopByIdWithoutContacts(shopId);
      if("shop".equals(customerSupplierSearchConditionDTO.getCustomerOrSupplier())){
        customerSupplierSearchConditionDTO.setShopKind(shopDTO.getShopKind());
      }
      if (StringUtils.isBlank(customerSupplierSearchConditionDTO.getSort()) ) {
        if (CustomerSupplierSearchConditionDTO.SUPPLIER.equals(customerSupplierSearchConditionDTO.getCustomerOrSupplier())&& customerSupplierSearchConditionDTO.isEmptyOfSuggestionSupplierInfo()) customerSupplierSearchConditionDTO.setSort("last_inventory_time desc");
        else if (CustomerSupplierSearchConditionDTO.CUSTOMER.equals(customerSupplierSearchConditionDTO.getCustomerOrSupplier())&& customerSupplierSearchConditionDTO.isEmptyOfSuggestionCustomerInfo()) customerSupplierSearchConditionDTO.setSort("last_expense_time desc");
      }
//      sw.stop();
//      sw.start("getCustomerSupplierSuggestion query");
      List<SearchSuggestionDTO> searchSuggestionDTOList = ServiceManager.getService(ISearchCustomerSupplierService.class).queryCustomerSupplierSuggestion(customerSupplierSearchConditionDTO);
//      sw.stop();
//      sw.start("getCustomerSupplierSuggestion transform");
      if (CollectionUtils.isNotEmpty(searchSuggestionDTOList)) {
        for (SearchSuggestionDTO pssDTO : searchSuggestionDTOList)
          dropDownList.add(pssDTO.toSupplierCustomerDropDownItemMap(titles == null ? null : Arrays.asList(titles),StringUtils.isBlank(customerSupplierSearchConditionDTO.getCustomerOrSupplier())));
      }
      result.put("uuid", customerSupplierSearchConditionDTO.getUuid());
      result.put("data", dropDownList);
//      sw.stop();
//      LOG.warn(sw.toString());
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getCustomerSupplierSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 查询单据条目 详细
   * @param request
   * @param orderSearchConditionDTO
   * @return
   */
  @RequestMapping(params = "method=getOrderItemDetails")
  @ResponseBody
  public Object getOrderItemDetails(HttpServletRequest request, String uuid, OrderSearchConditionDTO orderSearchConditionDTO) {
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    try {
      //数据校验
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) throw new Exception("shopId can't be null.");
      Map<String, Object> result = new HashMap<String, Object>();
      List<Map<String,String>> orderItemList = new ArrayList<Map<String,String>>();
      orderSearchConditionDTO.setShopId(shopId);
      if(orderSearchConditionDTO.isEmptyOfProductInfo()){
        orderSearchConditionDTO.setSort("order_created_time desc");
      }
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setExcludeOnlineOrder(true);
      if (orderSearchConditionDTO.getRowStart() < 0) {
        orderSearchConditionDTO.setRowStart(0);
      }
      OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrderItems(orderSearchConditionDTO);
      if(CollectionUtils.isNotEmpty(orderSearchResultListDTO.getOrderItems())){
        for(OrderItemSearchResultDTO orderItemSearchResultDTO : orderSearchResultListDTO.getOrderItems()){
          orderItemList.add(orderItemSearchResultDTO.toOrderItemSuggestionMap());
        }
      }

      result.put("uuid", uuid);
      result.put("data", orderItemList);
      result.put("totalCount", orderSearchResultListDTO.getItemNumFound());
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getOrderItemDetails");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
  /**
   * @deprecated
   * 根据单据条目做的 suggestion
   * @param request
   * @param orderSearchConditionDTO
   * @return
   */
  @RequestMapping(params = "method=getOrderItemSuggestion")
  @ResponseBody
  public Object getOrderItemSuggestion(HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO) {
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);

    try {
      //数据校验
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) throw new Exception("shopId can't be null.");
      Map<String, Object> result = new HashMap<String, Object>();
      orderSearchConditionDTO.setShopId(shopId);
      List<SearchSuggestionDTO> searchSuggestionDTOList = searchOrderService.queryOrderItemSuggestion(orderSearchConditionDTO);

      List<Map> dropDownList = new ArrayList<Map>();
      if (CollectionUtils.isNotEmpty(searchSuggestionDTOList)) {
        for (SearchSuggestionDTO searchSuggestionDTO : searchSuggestionDTOList)
          dropDownList.add(searchSuggestionDTO.toStandardDropDownItemMap());
      }
      Map<String, Object> dropDownMap = new HashMap<String, Object>();
      dropDownMap.put("uuid", orderSearchConditionDTO.getUuid());
      dropDownMap.put("data", dropDownList);
      result.put("dropDown", dropDownMap);
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getOrderItemSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 车牌下拉
   *
   * @param request
   * @param searchWord
   * @param uuid
   * @return
   */
  @RequestMapping(params = "method=getVehicleLicenceNoSuggestion")
  @ResponseBody
  public Object getVehicleLicenceNoSuggestion(HttpServletRequest request, String uuid, String searchWord,String customerId) {
    try {
      //数据校验
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) throw new Exception("shopId can't be null.");
      Map<String, Object> result = new HashMap<String, Object>();
      List<Map> dropDownList = new ArrayList<Map>();
      List<CarDTO> carDTOList =null;
      if(StringUtils.isNotBlank(customerId)){
        IUserService userService = ServiceManager.getService(IUserService.class);
        carDTOList = userService.getVehiclesByCustomerId(shopId, Long.parseLong(customerId));
      }else{
        carDTOList = ServiceManager.getService(ISearchVehicleService.class).queryVehicleLicenseNoSuggestion(shopId, searchWord);
      }
      if (CollectionUtils.isNotEmpty(carDTOList)) {
        Map<String, Object> dropDownItem = null;
        Map<String, String> propertyMap = null;
        for (CarDTO carDTO : carDTOList) {
          dropDownItem = new HashMap<String, Object>();
          propertyMap = new HashMap<String, String>();
          propertyMap.put("id", carDTO.getId());
          propertyMap.put("licenceNo", carDTO.getLicenceNo());
          dropDownItem.put("label", carDTO.getLicenceNo());
          dropDownItem.put("details", propertyMap);
          dropDownItem.put("type", "option");  //目前只使用   option  （category）暂时不用
          dropDownList.add(dropDownItem);
        }
      }
      result.put("uuid", uuid);
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getVehicleLicenceNoSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getSaleManSuggestion")
  @ResponseBody
  public Object getSaleManSuggestion(HttpServletRequest request,String uuid, String keyword) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shopId can't be null.");
      List<SalesManDTO> salesManDTOs = membersService.searchSaleManByShopIdAndKeyword(shopId, keyword);
      Map<String, Object> result = new HashMap<String, Object>();
      List<Map> dropDownList = new ArrayList<Map>();
      if (CollectionUtils.isNotEmpty(salesManDTOs)) {
        Map<String, Object> dropDownItem = null;
        Map<String, String> propertyMap = null;
        for (SalesManDTO salesManDTO : salesManDTOs) {
          dropDownItem = new HashMap<String, Object>();
          propertyMap = new HashMap<String, String>();
          propertyMap.put("id", salesManDTO.getId()==null?"":salesManDTO.getId().toString());
          propertyMap.put("name", salesManDTO.getName());
          dropDownItem.put("label", salesManDTO.getName());
          dropDownItem.put("details", propertyMap);
          dropDownItem.put("type", "option");  //目前只使用   option  （category）暂时不用
          dropDownList.add(dropDownItem);
        }
      }
      result.put("uuid", uuid);
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getSaleManSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 商品分类
   *
   * @param request
   * @param searchWord
   * @param productCategoryType
   * @return
   */
  @RequestMapping(params = "method=getProductCategorySuggestion")
  @ResponseBody
  public Object getProductCategorySuggestion(HttpServletRequest request,String uuid,String searchWord,Long parentId,ProductCategoryType productCategoryType) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      Map<String, Object> result = new HashMap<String, Object>();
      List<Map> dropDownList = new ArrayList<Map>();
      List<SearchSuggestionDTO> searchSuggestionDTOList = ServiceManager.getService(ISearchSuggestionService.class).getProductCategorySuggestion(shopId,searchWord, productCategoryType,parentId);
      if (CollectionUtils.isNotEmpty(searchSuggestionDTOList)) {
        for (SearchSuggestionDTO searchSuggestionDTO : searchSuggestionDTOList){
          dropDownList.add(searchSuggestionDTO.toStandardDropDownItemMap());
        }
      }
      result.put("uuid", uuid);
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getProductCategorySuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 商品分类
   *
   * @param request
   * @param searchWord
   * @return
   */
  @RequestMapping(params = "method=getProductCategoryDetailList")
  @ResponseBody
  public Object getProductCategoryDetailList(HttpServletRequest request,String searchWord) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      List<ProductCategoryDTO> productCategoryDTOList= new ArrayList<ProductCategoryDTO>();
      List<ProductCategoryDTO> tempProductCategoryDTOList = ServiceManager.getService(ISearchSuggestionService.class).getProductCategoryDetailList(shopId,searchWord, new ProductCategoryType[]{ProductCategoryType.SECOND_CATEGORY, ProductCategoryType.THIRD_CATEGORY});
      if(CollectionUtils.isNotEmpty(tempProductCategoryDTOList)){
        Map<Long, ProductCategoryDTO> productCategoryDTOMap = ProductCategoryCache.getProductCategoryDTOMap();
        Node productCategoryNode = ProductCategoryCache.getNode();
        ProductCategoryDTO productCategoryDTO = null;
        //先二级
        for(ProductCategoryDTO temp:tempProductCategoryDTOList){
          ServiceManager.getService(IProductCategoryService.class).fillProductCategoryDTOInfo(temp);
          if(!productCategoryDTOList.contains(temp))
            productCategoryDTOList.add(temp);
          if(ProductCategoryType.SECOND_CATEGORY.equals(temp.getCategoryType())){
            Node currentNode = productCategoryNode.findNodeInTree(temp.getId());
            if(currentNode!=null){
              List<Node> childrenNodes = currentNode.getChildren();
              for(Node n:childrenNodes){
                productCategoryDTO = productCategoryDTOMap.get(n.getId());
                if(!productCategoryDTOList.contains(productCategoryDTO))
                  productCategoryDTOList.add(productCategoryDTO);
              }
            }
          }
        }
        for(ProductCategoryDTO temp:tempProductCategoryDTOList){
          if(ProductCategoryType.THIRD_CATEGORY.equals(temp.getCategoryType())){
            productCategoryDTO = productCategoryDTOMap.get(temp.getId());
            if(!productCategoryDTOList.contains(productCategoryDTO))
              productCategoryDTOList.add(productCategoryDTO);
          }
        }
      }
      return productCategoryDTOList;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getProductCategoryDetailList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 指定数据格式  跟前台组件 配套
   * [
   * {"level_1_name":"汽车","level_2_name":"火炮","level_3_name":"","level_1_id":"0001","level_2_id":"","level_3_id":""},
   * {"level_1_name":"汽车","level_2_name":"配件","level_3_name":"","level_1_id":"0001","level_2_id":"aaaaaaaaa","level_3_id":""},
   * {"level_1_name":"汽车","level_2_name":"配件","level_3_name":"火花塞","level_1_id":"0001","level_2_id":"aaaaaaaaa","level_3_id":"ususus-00dsf0df0d0d"},
   * {"level_1_name":"坦克","level_2_name":"火炮","level_3_name":"","level_1_id":"0002","level_2_id":"","level_3_id":""},
   * {"level_1_name":"坦克","level_2_name":"动力系统","level_3_name":"","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb","level_3_id":""},
   * {"level_1_name":"坦克","level_2_name":"动力系统","level_3_name":"水套","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb","level_3_id":"uiuiuiui-asdf9898df7a8sdf"},
   * {"level_1_name":"坦克","level_2_name":"悬挂系统","level_3_name":"","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb02","level_3_id":""},
   * {"level_1_name":"坦克","level_2_name":"悬挂系统","level_3_name":"扭力连杆","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb02","level_3_id":"jijijijij-dasf88f9d0a0sdf9"},
   * {"level_1_name":"坦克","level_2_name":"悬挂系统","level_3_name":"半扭力连杆","level_1_id":"0002","level_2_id":"bbbbbbbbbbbbb02","level_3_id":"yuyuyuyu-88dufuufjasd89"}
   * ];
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getAllProductCategorySelectorData")
  @ResponseBody
  public Object getAllProductCategoryData(HttpServletRequest request){
    List<Map<String,String>> result = new ArrayList<Map<String, String>>();
    try {
      Long shopId = WebUtil.getShopId(request);
      List<ProductCategoryDTO> productCategoryDTOList = ProductCategoryCache.getProductCategoryDTOListByType(ProductCategoryType.THIRD_CATEGORY);
      if(CollectionUtils.isNotEmpty(productCategoryDTOList)){
        Map<String,String> map = null;
        for(ProductCategoryDTO productCategoryDTO : productCategoryDTOList){
          map = new HashMap<String, String>();
          map.put("level_1_name",productCategoryDTO.getFirstCategoryName());
          map.put("level_2_name",productCategoryDTO.getSecondCategoryName());
          map.put("level_3_name",productCategoryDTO.getThirdCategoryName());
          map.put("level_1_id",productCategoryDTO.getFirstCategoryIdStr());
          map.put("level_2_id",productCategoryDTO.getSecondCategoryIdStr());
          map.put("level_3_id",productCategoryDTO.getThirdCategoryIdStr());
          result.add(map);
        }
      }
      //
      IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
      List<ProductCategoryDTO> customProductCategoryDTOList = productCategoryService.fillProductCategoryDTOListInfo(productCategoryService.getProductCategoryDTOByShopId(shopId));
      if(CollectionUtils.isNotEmpty(customProductCategoryDTOList)){
        Map<String,String> map = null;
        for(ProductCategoryDTO productCategoryDTO : customProductCategoryDTOList){
          map = new HashMap<String, String>();
          map.put("level_1_name",productCategoryDTO.getFirstCategoryName());
          map.put("level_2_name",productCategoryDTO.getSecondCategoryName());
          map.put("level_3_name","");
          map.put("level_1_id",productCategoryDTO.getFirstCategoryIdStr());
          map.put("level_2_id",productCategoryDTO.getSecondCategoryIdStr());
          map.put("level_3_id","");
          result.add(map);
        }
      }
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getAllProductCategorySelectorData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(),e);
    }
    return result;
  }

  @RequestMapping(params = "method=getProductCategoryExactByName")
  @ResponseBody
  public Object getProductCategoryExactByName(HttpServletRequest request,String name){
    List<Map<String,String>> result = new ArrayList<Map<String, String>>();
    try {
      Long shopId = WebUtil.getShopId(request);
      IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
      List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getProductCategoryDTOByName(shopId, name);
      if(CollectionUtils.isNotEmpty(productCategoryDTOList)){
        //先搜 三级
        for(ProductCategoryDTO productCategoryDTO:productCategoryDTOList){
          if(ProductCategoryType.THIRD_CATEGORY.equals(productCategoryDTO.getCategoryType())){
            return productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
          }
        }
        for(ProductCategoryDTO productCategoryDTO:productCategoryDTOList){
          if(ProductCategoryType.SECOND_CATEGORY.equals(productCategoryDTO.getCategoryType())){
            return productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
          }
        }
        for(ProductCategoryDTO productCategoryDTO:productCategoryDTOList){
          if(ProductCategoryType.FIRST_CATEGORY.equals(productCategoryDTO.getCategoryType())){
            return productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
          }
        }
      }
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do");
      LOG.debug("method=getProductCategoryExactByName");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(),e);
    }
    return result;
  }


  /**
   * 发动机号 车架号下拉
   *
   * @param request
   * @param uuid
   * @param searchWord
   * @param searchField
   * @return
   */
  @RequestMapping(params = "method=getVehicleEngineNoClassNoSuggestion")
  @ResponseBody
  public Object getVehicleEngineNoClassNoSuggestion(HttpServletRequest request, String uuid, String searchWord, String searchField) {
    try {
      //数据校验
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null || StringUtils.isEmpty(searchField) || !("gsm_obd_imei".equals(searchField) ||"gsm_obd_imei_mobile".equals(searchField) ||
          "engine_no".equals(searchField) || "chassis_number".equals(searchField)|| "licence_no".equals(searchField))) {
        return null;
      }
      Map<String, Object> result = new HashMap<String, Object>();
      ISearchVehicleService searchVehicleService = ServiceManager.getService(ISearchVehicleService.class);
      List<Map> dropDownList = searchVehicleService.getVehicleEngineNoClassNoSuggestion(shopId, searchWord, searchField);
      result.put("uuid", uuid);
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.debug("/searchInventoryIndex.do getVehicleEngineNoClassNoSuggestion searchWord:" + searchField + " searchField:" + searchField);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getShopSuggestion")
  @ResponseBody
  public Object getShopSuggestion(HttpServletRequest request, String name,String uuid) {
    try {
      int maxRows=10;
      ShopDTO shop = ServiceManager.getService(IConfigService.class).getShopById(WebUtil.getShopId(request));
      IConfigService configService=ServiceManager.getService(IConfigService.class);
      List<ShopDTO> shopDTOList=configService.getShopSuggestion(name,shop.getShopKind(),maxRows);
      List<Map> dropDownList=new ArrayList<Map>();
      if (CollectionUtils.isNotEmpty(shopDTOList)) {
        Map<String, Object> dropDownItem = null;
        for (ShopDTO shopDTO : shopDTOList) {
          dropDownItem = new HashMap<String, Object>();
          dropDownItem.put("label", shopDTO.getName());
          dropDownList.add(dropDownItem);
        }
      }
       Map<String, Object> result = new HashMap<String, Object>();
      result.put("uuid",uuid);
      result.put("data", dropDownList);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


}