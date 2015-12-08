package com.bcgogo.txn;

import com.bcgogo.common.*;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.product.ProductRelevanceHelper;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Category;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;

@Controller
@RequestMapping("/stockSearch.do")
public class StockSearchController {
  private static final Logger LOG = LoggerFactory.getLogger(StockSearchController.class);
	private IProductService productService;
	private IInventoryService inventoryService;
	private ITxnService txnService;
	private RFITxnService rfiTxnService;

  @Autowired
  private RemindEventStrategySelector remindEventStrategySelector;

	public IProductService getProductService() {
		if(productService == null){
			productService = ServiceManager.getService(IProductService.class);
		}
		return productService;
	}

	public IInventoryService getInventoryService() {
		if(inventoryService == null){
			inventoryService = ServiceManager.getService(IInventoryService.class);
		}
		return inventoryService;
	}

	public ITxnService getTxnService() {
		if(txnService == null){
			txnService = ServiceManager.getService(ITxnService.class);
		}
		return txnService;
	}

	public RFITxnService getRfiTxnService() {
		if(rfiTxnService == null){
			rfiTxnService = ServiceManager.getService(RFITxnService.class);
		}
		return rfiTxnService;
	}

	@RequestMapping(params = "method=getStockSearch")
  public String getStockSearch(ModelMap model, HttpServletRequest request,HttpServletResponse response,SearchConditionDTO searchConditionDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      return "/web";
    }
//    RemindEventStrategy txnRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.TXN);
//    int totalCount = txnRemindEventStrategy.countRemindEvent(shopId, null, null, getFlashTime());
//    model.addAttribute("count", totalCount);
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
    }
    String fuzzyMatchingFlag = request.getParameter("fuzzyMatchingFlag");
    String searchProductInfo = request.getParameter("productLocalInfoIds");
    String fromPage = request.getParameter("fromPage"); //从消息中心的消息中的商品而来
    if(StringUtils.isNotBlank(searchProductInfo)) {
        searchConditionDTO.setProductIds(searchProductInfo);
    }
    //判断是否是6字段查询
    request.setAttribute("fuzzyMatchingFlag", fuzzyMatchingFlag);
    request.setAttribute("searchWord", searchConditionDTO.getSearchWord());
    request.setAttribute("searchCommodityCode", searchConditionDTO.getCommodityCode());
    request.setAttribute("searchProductName", searchConditionDTO.getProductName());
    request.setAttribute("searchProductBrand", searchConditionDTO.getProductBrand());
    request.setAttribute("searchProductSpec", searchConditionDTO.getProductSpec());
    request.setAttribute("searchProductModel", searchConditionDTO.getProductModel());
    request.setAttribute("searchProductVehicleBrand", searchConditionDTO.getProductVehicleBrand());
    request.setAttribute("searchProductVehicleModel", searchConditionDTO.getProductVehicleModel());
    request.setAttribute("productIds", searchConditionDTO.getProductIds());
    request.setAttribute("fromPage", fromPage);
    request.setAttribute("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));
    return "/txn/stockSearch";
  }

  //邵磊   待入库
  @RequestMapping(params = "method=waitcoming")
  private void waitcoming(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                          Integer startPageNo, Integer maxRows) throws Exception {
    LOG.info("库存首页-待入库查询开始!");
    long begin = System.currentTimeMillis();
    long current = begin;
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
	  if(startPageNo == null){
		  startPageNo = 1;
	  }
	  if(maxRows == null){
		  maxRows = 10;
	  }
    List<InventoryRemindEventDTO> itemsList = txnService.getInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId, startPageNo - 1, maxRows);
    LOG.info("库存首页-待入库查询--阶段1。执行时间: {} ms", System.currentTimeMillis()-current);
    current = System.currentTimeMillis();
//    jsonStr = productService.getJsonWithList(itemsList);
    jsonStr = ServiceUtil.getJsonWithList(itemsList);
    int totalRows = txnService.countInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId);
//    int pageCount3 = count3 % maxRows == 0 ? count3 / maxRows : (count3 / maxRows + 1);

    LOG.info("库存首页-待入库查询--阶段2。执行时间: {} ms", System.currentTimeMillis()-current);
    current = System.currentTimeMillis();
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    Pager pager = new Pager(totalRows, NumberUtil.intValue(String.valueOf(startPageNo), 1));
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
        jsonStr = pager.toJson();
    }

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
      LOG.info("库存首页-待入库查询--阶段3。执行时间: {} ms", System.currentTimeMillis()-current);
      LOG.info("库存首页-待入库查询。总时间: {} ms", System.currentTimeMillis()-begin);
    } catch (Exception e) {
      LOG.debug("/stockSearch.do");
      LOG.debug("method=waitcoming");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows);
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 使用SOLR 库存查询（适用车款）
   *
   * @param model
   * @param request
   * @param response
   */
  @RequestMapping(params = "method=searchProductForStockSearch")
  public void searchProductForStockSearch(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                          SearchConditionDTO searchConditionDTO) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    searchConditionDTO.setShopId(shopId);
    try {
      if (StringUtils.isBlank(searchConditionDTO.getSort()) && searchConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
        searchConditionDTO.setSort("storage_time desc,inventory_amount desc");
      } else {
        searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSort()));
      }
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_STATS});
      if(searchConditionDTO.getStorehouseId()!=null){
        searchConditionDTO.setStatsFields(new String[]{searchConditionDTO.getStorehouseId()+"_storehouse_inventory_amount", searchConditionDTO.getStorehouseId()+"_storehouse_inventory_price"});
      }else{
        searchConditionDTO.setStatsFields(new String[]{"inventory_amount", "inventory_price"});
      }
          //配合ajaxPaging.tag 接口的数据封装
    searchConditionDTO.setRows(searchConditionDTO.getMaxRows());
    searchConditionDTO.setStart(searchConditionDTO.getMaxRows() * (searchConditionDTO.getStartPageNo() - 1));
      //对应field库存查询
      //不知道field的情况下
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
//      //合并solr延时提交memcach中的商品
//      ServiceManager.getService(ISolrMergeService.class).mergeCacheProductDTO(shopId, productSearchResultListDTO.getProducts());
      //添加库存告警信息
      if (productSearchResultListDTO != null) {
        ServiceManager.getService(IInventoryService.class).getLimitAndAchievementForProductDTOs(productSearchResultListDTO.getProducts(), shopId);
      }
      String jsonStr = JsonUtil.objectToJson(productSearchResultListDTO);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/stockSearch.do");
      LOG.debug("method=searchProductForStockSearch");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("searchConditionDTO:", searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getLimitProductInfo")
  public void getLimitProductInfo(HttpServletRequest request, HttpServletResponse response, String sortStatus,
                                  String searchCondition, Integer startPageNo, Integer maxRows) {
    Long shopId = null;
    String sortStr = null;
    String searchConditionStr = null;
    ISearchService iSearchService = ServiceManager.getService(ISearchService.class);
	  IProductService productService = ServiceManager.getService(IProductService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    String jsonStr = "";
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    try {
      if(startPageNo == null || startPageNo == 0){
        startPageNo = 1;
      }
      if(maxRows == null || maxRows == 0){
        maxRows = 25;
      }
      Pager pager = new Pager(startPageNo,maxRows,true);
      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        LOG.error("stockSearch.do?method=getLimitProductInfo ,shopId is null");
        return;
      }
      if (StringUtils.isNotBlank(sortStatus)) {
        sortStr = RfTxnConstant.sortCommandMap_DB.get(sortStatus);
      }
      if (StringUtils.isNotBlank(searchCondition)) {
        searchConditionStr = RfTxnConstant.sortCommandMap_DB.get(searchCondition);
      }
	    ProductSearchResultListDTO productSearchResultListDTO = new ProductSearchResultListDTO();
      List<InventorySearchIndexDTO> inventorySearchIndexDTOs = iSearchService.getInventorySearchIndexDTOLimit(shopId,
          pager, searchConditionStr, sortStr);
	    Set<Long> productIds = new HashSet<Long>();
	    if (CollectionUtils.isNotEmpty(inventorySearchIndexDTOs)) {
		    for (InventorySearchIndexDTO inventorySearchIndexDTO : inventorySearchIndexDTOs) {
			    if (inventorySearchIndexDTO == null || inventorySearchIndexDTO.getProductId() == null) {
				    continue;
			    }
			    productIds.add(inventorySearchIndexDTO.getProductId());
		    }
	    }

//	    Map<Long, List<ProductSupplierDTO>> productSupplierLimitMap = new HashMap<Long, List<ProductSupplierDTO>>();
//	    Map<Long, List<ProductSupplierDTO>> productSupplierUnLimitMap = new HashMap<Long, List<ProductSupplierDTO>>();
      Map<Long,List<SupplierInventoryDTO>> supplierInventoryMap = new HashMap<Long, List<SupplierInventoryDTO>>();
	    Map<Long,ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
	    if (productIds != null && !productIds.isEmpty()) {
        supplierInventoryMap =  productThroughService.getSupplierInventoryMap(shopId,productIds);
		    productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(productIds);
	    }
      if (CollectionUtils.isNotEmpty(inventorySearchIndexDTOs)) {
        for (InventorySearchIndexDTO inventorySearchIndexDTO : inventorySearchIndexDTOs) {
          if (inventorySearchIndexDTO == null) {
            continue;
          }
          ProductDTO productDTO = new ProductDTO(inventorySearchIndexDTO);
	        if (productDTOMap != null && !productDTOMap.isEmpty() && productDTOMap.get(inventorySearchIndexDTO.getId()) != null) {
		        productDTO.setTradePrice(productDTOMap.get(inventorySearchIndexDTO.getId()).getTradePrice());
            productDTO.setStorageBin(productDTOMap.get(inventorySearchIndexDTO.getId()).getStorageBin());
	        }
          //TODO 从inventorySearchIndexDTO不能获得商品分类的id，因为要根据productDTO的ID再去获得分类id和name
          if(productDTO.getKindId()!=null){
            String kindName = productService.getKindNameById(productDTO.getKindId());
            productDTO.setKindName(kindName);
          }
	        if(supplierInventoryMap!=null && !supplierInventoryMap.isEmpty()){
		         List<SupplierInventoryDTO> supplierInventoryDTOs =  supplierInventoryMap.get(inventorySearchIndexDTO.getProductId());
		         if(CollectionUtils.isNotEmpty(supplierInventoryDTOs)){
			          productDTO.setSupplierInventoryDTOs(supplierInventoryDTOs);
		         }
	        }
          productDTOList.add(productDTO);
        }
	      iSearchService.getLimitSearchCount(shopId, searchConditionStr, productSearchResultListDTO);
        productSearchResultListDTO.setProducts(productDTOList);
        }
      pager = new Pager(((Long) productSearchResultListDTO.getInventoryCount()).intValue(), startPageNo, maxRows);
      productSearchResultListDTO.setPager(pager);
      jsonStr = JsonUtil.objectToJson(productSearchResultListDTO);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 使用SOLR 库存查询 库存总量和总额
   *
   * @param model
   * @param request
   * @param response
   * @param productName
   */
  @RequestMapping(params = "method=getSearchInventoryCount")
  public void getSearchInventoryCount(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                          String productName, String productBrand, String productSpec, String productModel,
                                      String pvBrand, String pvModel, String pvYear, String pvEngine) {

//    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = null;
    MemcacheInventorySumDTO memcacheInventorySumDTO = new MemcacheInventorySumDTO();
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        LOG.error("shop id is null todo method=getSearchInventoryCount");
        return;
      }
      long statTime = System.currentTimeMillis();
      if (StringUtil.strArrayIsBlank(productName, productBrand, productSpec,
          productModel, pvBrand, pvModel, pvYear, pvEngine)) {
         memcacheInventorySumDTO = inventoryService.getInventorySum(shopId);
      } else {
        ProductDTO productDTO = new ProductDTO(productName, productBrand, productSpec,
            productModel, pvBrand, pvModel, pvYear, pvEngine);
        Long[] ids = productService.getVehicleIds(pvBrand, pvModel, pvYear, pvEngine);
        productDTO.setVehicleInfoIds(ids);
        productDTO.setShopId(shopId);
//        searchService.countSolrSearchService(productDTO, memcacheInventorySumDTO);
      }
      long endTime = System.currentTimeMillis();
      LOG.debug("shopId:{}进行一次库存信息统计，共耗时{}ms", shopId, endTime - statTime);
      if (memcacheInventorySumDTO != null) {
        List<MemcacheInventorySumDTO> memcacheInventorySumDTOs = new ArrayList<MemcacheInventorySumDTO>();
        memcacheInventorySumDTOs.add(memcacheInventorySumDTO);
        jsonStr = JsonUtil.listToJsonNoQuote(memcacheInventorySumDTOs);
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
      }
    } catch (Exception e) {
       LOG.debug("/stockSearch.do method=getSearchInventoryCount" +
      "shopId:" + shopId + ",userId:" + request.getAttribute("userId") +
      "productName:" + productName + ",productBrand:" + productBrand + ",productSpec:" + productSpec +
          ",productModel:" + productModel + "pvBrand" + pvBrand + ",pvModel:" + pvModel + ",pvEngine:" + pvEngine + ",pvYear:" + pvYear);
      LOG.error(e.getMessage(), e);
    }
  }

  //品名搜索框进 查询库存总额
  //邵磊
  @RequestMapping(params = "method=getSearchProductNameInventoryCount")
  public void getInventory(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                           String productName) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null) {
      return;
    }
    String jsonStr = "";
    try {
    MemcacheInventorySumDTO memcacheInventorySumDTO = new MemcacheInventorySumDTO();
      memcacheInventorySumDTO = inventoryService.getSearchProductNameInventoryCount(shopId, productName, memcacheInventorySumDTO);
      if (memcacheInventorySumDTO != null) {
        List<MemcacheInventorySumDTO> memcacheInventorySumDTOs = new ArrayList<MemcacheInventorySumDTO>();
        memcacheInventorySumDTOs.add(memcacheInventorySumDTO);
        jsonStr = JsonUtil.listToJsonNoQuote(memcacheInventorySumDTOs);
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
      }
    } catch (Exception e) {
      LOG.debug("/goodsindex.do");
      LOG.debug("method=getSearchProductNameInventoryCount");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=ajaxtogetproductbyid")
  @ResponseBody
  public InventorySearchIndexDTO ajaxToGetProductById(HttpServletRequest request,Long productId) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long shopVersionId = WebUtil.getShopVersionId(request);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);

    try {
      if (shopId == null || productId == null || shopVersionId == null) {
        LOG.error("获取需要更新的产品信息，shopId :{},productId:{}", shopId, productId);
        return null;
      }
      InventorySearchIndexDTO isiDTO = null;
      ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);
      InventoryDTO inventoryDTO = inventoryService.getInventoryDTOByProductId(productId);
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      imageService.addImageInfoToProductDTO(imageSceneList,false,productDTO);

      if (productDTO != null) {
        isiDTO = new InventorySearchIndexDTO();
        isiDTO.setProductDTO(productDTO);
        isiDTO.setInventoryDTO(inventoryDTO);
        if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
          Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByProductId(shopId,productId);
          List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
          if(CollectionUtils.isNotEmpty(storeHouseDTOList)){
            for(StoreHouseDTO storeHouseDTO : storeHouseDTOList){
              if(!storeHouseInventoryDTOMap.containsKey(storeHouseDTO.getId())){
                StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(storeHouseDTO.getId(),productId,0d);
                storeHouseInventoryDTO.setStoreHouseName(storeHouseDTO.getName());
                storeHouseInventoryDTOMap.put(storeHouseDTO.getId(),storeHouseInventoryDTO);
              }else {
                StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(storeHouseDTO.getId());
                storeHouseInventoryDTO.setStoreHouseName(storeHouseDTO.getName());
              }

            }
          }
          isiDTO.setStoreHouseInventoryDTOMap(storeHouseInventoryDTOMap);
        }
        Set<Long> productIds = new HashSet<Long>();
        productIds.add(productId);
        List<SupplierInventoryDTO> supplierInventoryDTOs = productThroughService.getSupplierInventoryMap(shopId,productIds).get(productId);
        productThroughService.sortSupplierInventoryDTOsByLastPurchaseTime(supplierInventoryDTOs);
        isiDTO.setSupplierInventoryDTOs(supplierInventoryDTOs);
        isiDTO.setMaxAndMinPurchasePrice(supplierInventoryDTOs);


        return isiDTO;
      } else {
        LOG.warn("获取需要更新的产品信息，shopId :{},productId:{}", shopId, productId);
        LOG.error("获取需要更新的产品信息InventorySearchIndexDTO：{},ProductLocalInfoDTO{}", isiDTO, productDTO);
      }
    } catch (Exception e) {
      LOG.debug("/stockSearch.do");
      LOG.debug("method=ajaxtogetproductbyid");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=ajaxToGetSupplierProductInfo")
  @ResponseBody
  public Object ajaxToGetSupplierProductInfo(HttpServletRequest request, SearchConditionDTO searchConditionDTO) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long supplierShopId = searchConditionDTO.getWholesalerShopId();
      InventorySearchIndexDTO returnInventorySearchIndexDTO = new InventorySearchIndexDTO();
      if (searchConditionDTO == null || supplierShopId == null || (searchConditionDTO.getProductId() == null && searchConditionDTO.isEmptyOfProductInfo())) {
        return null;
      }
      Set<Long> productSet = new HashSet<Long>();
      if(searchConditionDTO.getProductId() == null){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(searchConditionDTO.getProductName());
        productDTO.setBrand(searchConditionDTO.getProductBrand());
        productDTO.setSpec(searchConditionDTO.getProductSpec());
        productDTO.setModel(searchConditionDTO.getProductModel());
        productDTO.setProductVehicleBrand(searchConditionDTO.getProductVehicleBrand());
        productDTO.setProductVehicleModel(searchConditionDTO.getProductVehicleModel());
        productDTO.setCommodityCode(searchConditionDTO.getCommodityCode());
        List<ProductDTO> productDTOList = getProductService().getProductDTOsBy7P(supplierShopId,productDTO);
        if(CollectionUtils.isNotEmpty(productDTOList)){
          productDTO = getProductService().getProductById(productDTOList.get(0).getId(),supplierShopId);
          searchConditionDTO.setProductId(productDTO.getProductLocalInfoId());
        }else{
          return returnInventorySearchIndexDTO;
        }
      }
      productSet.add(searchConditionDTO.getProductId());
      Map<Long, ProductMappingDTO> productMappingDTOMap = getProductService().getSupplierProductMappingDTODetailMap(supplierShopId, shopId, productSet);
      ProductMappingDTO productMappingDTO = productMappingDTOMap.get(searchConditionDTO.getProductId());

      if (productMappingDTO != null && productMappingDTO.isProductMappingEnabled() && ProductStatus.InSales.equals(productMappingDTO.getSupplierProductDTO().getSalesStatus())) {
        returnInventorySearchIndexDTO.setSupplierProduct(productMappingDTO.getSupplierProductDTO());
        returnInventorySearchIndexDTO.setAmount(productMappingDTO.getSupplierProductDTO().getInSalesAmount());
        returnInventorySearchIndexDTO.setLocalProduct(productMappingDTO.getCustomerProductDTO());
        InventoryDTO customerInventoryDTO = getTxnService().getInventoryByShopIdAndProductId(shopId, productMappingDTO.getCustomerProductDTO().getProductLocalInfoId());
        if (customerInventoryDTO != null) {
          returnInventorySearchIndexDTO.setLowerLimit(customerInventoryDTO.getLowerLimit());
          returnInventorySearchIndexDTO.setUpperLimit(customerInventoryDTO.getUpperLimit());
        }
        if (productMappingDTO.getCustomerProductDTO().getBusinessCategoryId()!=null) {
          Category category = getRfiTxnService().getCategoryById(shopId, productMappingDTO.getCustomerProductDTO().getBusinessCategoryId());
          if (null != category) {
            returnInventorySearchIndexDTO.setBusinessCategoryIdStr(category.getId().toString());
            returnInventorySearchIndexDTO.setBusinessCategoryName(category.getCategoryName());
          }
        }
      }else {
        ProductDTO supplierProductDTO = getProductService().getProductByProductLocalInfoId(searchConditionDTO.getProductId(),supplierShopId);
        if(supplierProductDTO!=null && ProductStatus.InSales.equals(supplierProductDTO.getSalesStatus())){
          returnInventorySearchIndexDTO.setSupplierProduct(supplierProductDTO);
          returnInventorySearchIndexDTO.setAmount(supplierProductDTO.getInSalesAmount());
        }
      }
      if(returnInventorySearchIndexDTO.getSupplierProductId()!=null){
        IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
        List<PromotionsDTO> promotionsDTOs = promotionsService.getPromotionsDTODetailByProductLocalInfoId(supplierShopId,returnInventorySearchIndexDTO.getSupplierProductId());
        List<PromotionsDTO> promotionsDTOsTemp=new ArrayList<PromotionsDTO>();
        if(CollectionUtil.isNotEmpty(promotionsDTOs)){
          for(PromotionsDTO promotionsDTO:promotionsDTOs){
            if(promotionsDTO!=null){
              promotionsDTO.checkUnexpired();
              if(promotionsDTO.isUnexpired() && CollectionUtils.isNotEmpty(promotionsDTO.getPromotionsRuleDTOList())){
                Collections.sort(promotionsDTO.getPromotionsRuleDTOList(), PromotionsRuleDTO.SORT_BY_LEVEL);
                promotionsDTOsTemp.add(promotionsDTO);
              }
            }
          }
        }
        returnInventorySearchIndexDTO.setPromotionsDTOs(promotionsDTOsTemp);
      }
      return returnInventorySearchIndexDTO;
    } catch (Exception e) {
      LOG.debug("searchInventoryIndex.do?method=ajaxToGetSupplierProductInfo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId"));
      LOG.debug("searchConditionDTO:" + searchConditionDTO.toString());
      WebUtil.reThrow(LOG, e);
      return null;
    }
  }

  @RequestMapping(params = "method=ajaxtoupdateproduct")
  @ResponseBody
  public Map ajaxToUpdateProduct(HttpServletRequest request,ProductDTO formProductDTO) throws Exception{
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Map map = new HashMap();
    try {
	    if(formProductDTO==null || formProductDTO.getProductLocalInfoId() == null){
		    LOG.error("更新产品信息出错：method=ajaxtoupdateproduct,product_id is null");
		    return null;
	    }
      formProductDTO.setProductVehicleBrand(formProductDTO.getVehicleBrand());
      formProductDTO.setProductVehicleModel(formProductDTO.getVehicleModel());

      ProductDTO oldProductDTO = productService.getProductByProductLocalInfoId(formProductDTO.getProductLocalInfoId(), shopId);

      boolean basicPropSame = oldProductDTO.checkSameBasicProperties(formProductDTO);

      if(!basicPropSame){   //只有7属性不同时才校验是否有未结算单据
        List<OrderIndexDTO> inProgressOrders = getRfiTxnService().getUnsettledOrdersByProductId(shopId, formProductDTO.getProductLocalInfoId());
        if(CollectionUtils.isNotEmpty(inProgressOrders)){
          map.put("msg", "inProgressOrder");
          map.put("data", inProgressOrders);
          return map;
        }
      }

      ProductLocalInfoDTO oldProductLocalInfoDTO = productService.getProductLocalInfoById(formProductDTO.getProductLocalInfoId(),shopId);

      if(null!= oldProductLocalInfoDTO) {
        formProductDTO.setId(oldProductLocalInfoDTO.getProductId());
        if (!basicPropSame) {
          boolean result = productService.checkSameProduct(shopId, formProductDTO);
          if (!result) {
            map.put("msg", "has the same product");
            return map;
          }
        }
      }
      if (UnitUtil.isAddFirstUnit(oldProductLocalInfoDTO, formProductDTO)) {
        txnService.updateProductUnit(shopId, formProductDTO.getProductLocalInfoId(), formProductDTO.getSellUnit(),
            formProductDTO.getSellUnit(), formProductDTO.getRate());
      }

      //根据7个属性做同商品验证

      Long userId = WebUtil.getUserId(request);
      ProductModifyLogDTO oldLogDTO = new ProductModifyLogDTO();
      ProductModifyLogDTO newLogDTO = new ProductModifyLogDTO();

      InventorySearchIndexDTO isiDTO = new InventorySearchIndexDTO();
      isiDTO.setProductDTO(oldProductDTO);
      Long[] ids = productService.getVehicleIds(formProductDTO.getVehicleBrand(), formProductDTO.getVehicleModel(), null, null);
	    isiDTO.setCommodityCode(StringUtils.isNotBlank(formProductDTO.getCommodityCode()) ? formProductDTO.getCommodityCode() : null);
      isiDTO.setProductName(formProductDTO.getName());
      isiDTO.setProductBrand(formProductDTO.getBrand());
      isiDTO.setProductSpec(formProductDTO.getSpec());
      isiDTO.setProductModel(formProductDTO.getModel());
      isiDTO.setBrand(formProductDTO.getVehicleBrand());
      isiDTO.setModel(formProductDTO.getVehicleModel());
      isiDTO.setRecommendedPrice(formProductDTO.getRecommendedPrice());
      isiDTO.setUpperLimit(formProductDTO.getUpperLimit());
      isiDTO.setLowerLimit(formProductDTO.getLowerLimit());
      isiDTO.setBrandId(ids[0]);
      isiDTO.setModelId(ids[1]);
	    if(StringUtils.isNotBlank(formProductDTO.getSellUnit())){
		    isiDTO.setUnit(formProductDTO.getSellUnit());
	    }
      Long kindId = null;
      //根据商品分类名称，获取分类ID
      if(StringUtils.isNotBlank(formProductDTO.getKindName())){
        isiDTO.setKindName(formProductDTO.getKindName());
        kindId = productService.getProductKindId(shopId,formProductDTO.getKindName());
      }else{
        isiDTO.setKindName(null);
        kindId = -1l;//无奈
      }

      oldLogDTO.setProduct(oldProductDTO);
      formProductDTO.setKindId(kindId);
      formProductDTO.setDescription(oldProductDTO.getDescription());
      productService.updateProduct(shopId, formProductDTO);
      newLogDTO.setProduct(formProductDTO);

      oldLogDTO.setProductLocalInfo(oldProductLocalInfoDTO);
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        formProductDTO.setStorageBin(oldProductLocalInfoDTO.getStorageBin());//不更新productlocalinfo  上的仓位
        //更新仓库中仓位
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        if(!ArrayUtils.isEmpty(formProductDTO.getStoreHouseInventoryDTOs())){
          for(StoreHouseInventoryDTO storeHouseInventoryDTO : formProductDTO.getStoreHouseInventoryDTOs()){
            if(storeHouseInventoryDTO.getStorehouseId()!=null && storeHouseInventoryDTO.getProductLocalInfoId()!=null){
              storeHouseService.saveOrUpdateStoreHouseStorageBin(storeHouseInventoryDTO);
            }
          }
        }
      }
	    productService.updateProductLocalInfo(formProductDTO.getProductLocalInfoId(), null, null, formProductDTO.getStorageBin(), formProductDTO.getTradePrice(), formProductDTO.getStorageUnit(), formProductDTO.getSellUnit());
      ProductLocalInfoDTO newProductLocalInfoDTO = productService.getProductLocalInfoById(formProductDTO.getProductLocalInfoId(), shopId);
      newLogDTO.setProductLocalInfo(newProductLocalInfoDTO);

      InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
      InventoryDTO oldInventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, formProductDTO.getProductLocalInfoId());
      oldLogDTO.setInventory(oldInventoryDTO);

      InventoryDTO inventoryDTO = new  InventoryDTO();
      inventoryDTO.setId(formProductDTO.getProductLocalInfoId());
      inventoryDTO.setLowerLimit(formProductDTO.getLowerLimit());
      inventoryDTO.setUpperLimit(formProductDTO.getUpperLimit());
      inventoryDTO.setUnit(formProductDTO.getSellUnit());
      inventoryDTO.setSalesPrice(formProductDTO.getRecommendedPrice());

      InventoryDTO newInventoryDTO =inventoryService.updateInventoryInfo( shopId,inventoryDTO,inventoryLimitDTO);
      isiDTO.setAmount(newInventoryDTO.getAmount());
      newLogDTO.setInventory(newInventoryDTO);

      List<ProductModifyLogDTO> logResults = ProductModifyLogDTO.compare(oldLogDTO, newLogDTO);
      for(ProductModifyLogDTO logDTO : logResults){
        logDTO.setProductId(formProductDTO.getProductLocalInfoId());
        logDTO.setShopId(shopId);
        logDTO.setUserId(userId);
        logDTO.setOperationType(ProductModifyOperations.INVENTORY_INDEX_UPDATE);
        logDTO.setStatProcessStatus(StatProcessStatus.NEW);
      }
      if(ProductRelevanceHelper.existRelevancePropertyModify(oldProductDTO.getRelevanceStatus(),logResults.toArray(new ProductModifyLogDTO[logResults.size()]))){
        for(ProductModifyLogDTO modifyLogDTO:logResults){
          if(!ProductRelevanceHelper.existRelevanceProperty(modifyLogDTO)){
            continue;
          }
          modifyLogDTO.setRelevanceStatus(ProductRelevanceStatus.UN_CHECKED);
        }
      }
      txnService.batchCreateProductModifyLog(logResults);
      if(ProductRelevanceHelper.existRelevancePropertyModify(oldProductDTO.getRelevanceStatus(),logResults.toArray(new ProductModifyLogDTO[logResults.size()]))){
        List<ProductModifyFields> productModifyFieldsList = txnService.getRelevanceStatusUnCheckedProductModifiedFieldsMap(oldProductDTO.getProductLocalInfoId()).get(oldProductDTO.getProductLocalInfoId());
        if(CollectionUtils.isNotEmpty(productModifyFieldsList)){
          productService.updateProductRelevanceStatus(oldProductDTO.getId(),ProductRelevanceStatus.UN_CHECKED);
        }else{
          productService.updateProductRelevanceStatus(oldProductDTO.getId(),ProductRelevanceStatus.YES);
          txnService.updateProductModifyLogDTORelevanceStatus(oldProductDTO.getProductLocalInfoId(),ProductRelevanceStatus.YES);
        }
      }


      MemcacheLimitDTO memcacheLimitDTO = inventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, inventoryLimitDTO);
      searchService.updateInventorySearchIndex(isiDTO);
      //保存商品主图
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
      DataImageRelationDTO dataImageRelationDTO = null;
      if(formProductDTO.getImageCenterDTO()!=null && StringUtils.isNotBlank(formProductDTO.getImageCenterDTO().getProductMainImagePath())){
        dataImageRelationDTO = new DataImageRelationDTO(shopId,formProductDTO.getProductLocalInfoId(), DataType.PRODUCT,ImageType.PRODUCT_MAIN_IMAGE,0);
        dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,formProductDTO.getImageCenterDTO().getProductMainImagePath()));
      }
      ServiceManager.getService(IImageService.class).saveOrUpdateDataImageDTOs(shopId,imageTypeSet,DataType.PRODUCT,formProductDTO.getProductLocalInfoId(),dataImageRelationDTO);
	    //solr reindex
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, formProductDTO.getProductLocalInfoId());

	    try{
		   ServiceManager.getService(IConfigService.class).saveOrUpdateUnitSort(shopId,formProductDTO.getSellUnit(),formProductDTO.getStorageUnit());
	    }catch (Exception e){
		    LOG.error("更新商品单位顺序时出错：shopId:{},销售单位：{},库存单位:{},catch不影响下一步操作"+e.getMessage(),
				             new Object[]{shopId,formProductDTO.getSellUnit(),formProductDTO.getStorageUnit(),e});
	    }

      map.put("msg","success");
      map.put("memcacheLimitDTO",memcacheLimitDTO);
      return map;
    } catch (Exception e) {
      map.put("msg","updateError");
      LOG.debug("/stockSearch.do");
      LOG.debug("method=ajaxtoupdateproduct");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return map;
    }
  }



	@RequestMapping(params = "method=ajaxToGetProductByCommodityCode")
	@ResponseBody
	public Object ajaxToGetProductByCommodityCode(HttpServletRequest request, HttpServletResponse response,
	                                              String commodityCode,Long storehouseId) {
		Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long shopVersionId = WebUtil.getShopVersionId(request);
		IProductService productService = ServiceManager.getService(IProductService.class);
		List<InventorySearchIndexDTO> inventorySearchIndexDTOList = new ArrayList<InventorySearchIndexDTO>();
		try {
			if (StringUtils.isBlank(commodityCode) || shopId == null || shopVersionId==null) {
				LOG.error("shopId :{},commodityCode:{},shopVersionId"+shopVersionId, shopId, commodityCode);
				return inventorySearchIndexDTOList;
			}
      List<ProductDTO> productDTOs = productService.getProductDTOsByCommodityCodes(shopId, commodityCode);
			if (CollectionUtils.isNotEmpty(productDTOs)) {
				Set<Long> productIds = new HashSet<Long>((int) (productDTOs.size() / 0.75f + 1));
				for (ProductDTO productDTO : productDTOs) {
					productIds.add(productDTO.getProductLocalInfoId());
				}
        Map<Long, InventoryDTO> inventoryMap = getInventoryService().getInventoryDTOMap(shopId, productIds);

        Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long,StoreHouseInventoryDTO>();
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          storeHouseInventoryDTOMap.putAll(storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId, storehouseId, productIds.toArray(new Long[productIds.size()])));
        }
				for (ProductDTO productDTO : productDTOs) {
          InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
          inventorySearchIndexDTO.setProductDTO(productDTO);
          InventoryDTO inventoryDTO = inventoryMap.get(productDTO.getProductLocalInfoId());
          if (inventoryDTO != null) {
            inventorySearchIndexDTO.setInventoryDTO(inventoryDTO);
            if (productDTO.getBusinessCategoryId() != null) {
              Category category = getRfiTxnService().getCategoryById(shopId, productDTO.getBusinessCategoryId());
              inventorySearchIndexDTO.setBusinessCategoryName(category == null ? null : category.getCategoryName());
            }
          } else {
            LOG.warn("ajaxToGetProductByCommodityCode:根据productLocalInfoId ：{}, 找不到inventoryDTO信息", productDTO.getProductLocalInfoId());
          }
          if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {//没有仓库就不更新  使用总库存
            if (storehouseId != null) {
              StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(inventorySearchIndexDTO.getProductId());
              inventorySearchIndexDTO.setAmount(storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount());
              inventorySearchIndexDTO.setStorageBin(storeHouseInventoryDTO == null ? null : storeHouseInventoryDTO.getStorageBin());
            } else {//不用更新库存   用原来的总库存 但是 货位放空
              inventorySearchIndexDTO.setStorageBin(null);
            }
          }
          if(StringUtils.isBlank(inventorySearchIndexDTO.getSellUnit())){
            inventorySearchIndexDTO.setUnit(ProductUnitCache.getUnitByProductName(inventorySearchIndexDTO.getProductName()));
          }
          inventorySearchIndexDTOList.add(inventorySearchIndexDTO);
        }
      }
			return inventorySearchIndexDTOList;
		} catch (Exception e) {
			LOG.debug("/stockSearch.do");
			LOG.debug("method=ajaxToGetProductByCommodityCode");
			LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
			LOG.error(e.getMessage(), e);
			return inventorySearchIndexDTOList;
		}
	}

  @RequestMapping(params = "method=ajaxToGetSupplierProductByCommodityCode")
  @ResponseBody
  public Object ajaxToGetSupplierProductByCommodityCode(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                                        String commodityCode, Long supplierShopId) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<InventorySearchIndexDTO> inventorySearchIndexDTOList = new ArrayList<InventorySearchIndexDTO>();
    try {
      if (supplierShopId == null || StringUtils.isBlank(commodityCode) || shopId == null) {
        LOG.error("商品编号为空，shopId :{},commodityCode:{}", shopId, commodityCode);
        return inventorySearchIndexDTOList;
      }
      List<ProductDTO> productDTOs = productService.getProductDTOsByCommodityCodes(supplierShopId, commodityCode);
      Set<Long> supplierProductIds = new HashSet<Long>();
      Set<Long> customerProductIds = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(productDTOs)) {
        Iterator<ProductDTO> productDTOIterator = productDTOs.iterator();
        while(productDTOIterator.hasNext()){
          ProductDTO productDTO = productDTOIterator.next();
          if(ProductStatus.InSales.equals(productDTO.getSalesStatus())){
            supplierProductIds.add(productDTO.getProductLocalInfoId());
          }else{
            productDTOIterator.remove();
          }
        }
        Map<Long, ProductMappingDTO> productMappingDTOMap = getProductService().getSupplierProductMappingDTODetailMap(supplierShopId, shopId, supplierProductIds);
        List<ProductMappingDTO> productMappingDTOs = new ArrayList<ProductMappingDTO>(productMappingDTOMap.values());
        if (CollectionUtils.isNotEmpty(productMappingDTOs)) {
          for (ProductMappingDTO productMappingDTO : productMappingDTOs) {
            if (productMappingDTO.isProductMappingEnabled()) {
              customerProductIds.add(productMappingDTO.getCustomerProductId());
            }
          }
        }
        Map<Long, InventoryDTO> customerInventoryMap = getInventoryService().getInventoryDTOMap(shopId, customerProductIds);

        for (ProductDTO productDTO : productDTOs) {
          if (productDTO.getProductLocalInfoId() == null) {
            continue;
          }
          ProductMappingDTO productMappingDTO = productMappingDTOMap.get(productDTO.getProductLocalInfoId());
          InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
          inventorySearchIndexDTO.setSupplierProduct(productDTO);
          inventorySearchIndexDTO.setAmount(productDTO.getInSalesAmount());
          if (productMappingDTO != null && productMappingDTO.isProductMappingEnabled()) {
            inventorySearchIndexDTO.setLocalProduct(productMappingDTO.getCustomerProductDTO());
            InventoryDTO customerInventoryDTO = customerInventoryMap.get(productMappingDTO.getCustomerProductId());
            if (customerInventoryDTO != null) {
              inventorySearchIndexDTO.setLowerLimit(customerInventoryDTO.getLowerLimit());
              inventorySearchIndexDTO.setUpperLimit(customerInventoryDTO.getUpperLimit());
            }
            if (null != productMappingDTO.getCustomerProductDTO().getBusinessCategoryId()) {
              Category category = getRfiTxnService().getCategoryById(shopId, productMappingDTO.getCustomerProductDTO().getBusinessCategoryId());
              if (null != category) {
                inventorySearchIndexDTO.setBusinessCategoryIdStr(category.getId().toString());
                inventorySearchIndexDTO.setBusinessCategoryName(category.getCategoryName());
              }
            }
          }
          if(inventorySearchIndexDTO.getSupplierProductId()!=null){
            IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
            List<PromotionsDTO> promotionsDTOs = promotionsService.getPromotionsDTODetailByProductLocalInfoId(supplierShopId,inventorySearchIndexDTO.getSupplierProductId());
            List<PromotionsDTO> promotionsDTOsTemp=new ArrayList<PromotionsDTO>();
            if(CollectionUtil.isNotEmpty(productDTOs)){
              for(PromotionsDTO promotionsDTO:promotionsDTOs){
                if(promotionsDTO!=null){
                  promotionsDTO.checkUnexpired();
                  if(promotionsDTO.isUnexpired() && CollectionUtils.isNotEmpty(promotionsDTO.getPromotionsRuleDTOList())){
                    Collections.sort(promotionsDTO.getPromotionsRuleDTOList(), PromotionsRuleDTO.SORT_BY_LEVEL);
                    promotionsDTOsTemp.add(promotionsDTO);
                  }
                }
              }
            }
            inventorySearchIndexDTO.setPromotionsDTOs(promotionsDTOsTemp);
          }
          inventorySearchIndexDTOList.add(inventorySearchIndexDTO);
        }
      }
      return inventorySearchIndexDTOList;
    } catch (Exception e) {
      LOG.debug("/stockSearch.do");
      LOG.debug("method=ajaxToGetSupplierProductByCommodityCode");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return inventorySearchIndexDTOList;
    }
  }

	@RequestMapping(params = "method=ajaxToDeleteProductByProductLocalInfoId")
	@ResponseBody
	public Object ajaxToDeleteProductByProductLocalInfoId(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long productId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Long shopId = null;
		RFITxnService rfTxnService = ServiceManager.getService(RFITxnService.class);
		IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
		IProductService productService = ServiceManager.getService(IProductService.class);
		ISearchService searchService = ServiceManager.getService(ISearchService.class);
		IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
		try {
			shopId = WebUtil.getShopId(request);
			if (shopId == null || productId == null) {
				returnMap.put("result", "error");
				returnMap.put("resultMsg", "网络异常，请联系客服");
				LOG.error("method=ajaxToDeleteProductByProductLocalInfoId  ，shopId：{} productId:{},数据为空：", shopId, productId);
				return returnMap;
			}
			ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);
			InventorySearchIndexDTO inventorySearchIndexDTO = searchService.getInventorySearchIndexById(shopId, productId);
			InventoryDTO inventoryDTO = rfTxnService.getInventoryByShopIdAndProductId(shopId, productId);
			if (productDTO == null || inventorySearchIndexDTO == null || inventoryDTO == null) {
				returnMap.put("result", "error");
				returnMap.put("resultMsg", ValidatorConstant.REQUEST_ERROR_MSG);
				LOG.error("method=ajaxToDeleteProductByProductLocalInfoId  ，shopId：{} productId:{},对应的数据库数据为空：" +
						          "productDTO:{},inventorySearchIndexDTO:{},inventoryDTO:{}",
						         new Object[]{shopId, productId, productDTO, inventorySearchIndexDTO, inventoryDTO});
				return returnMap;
			}
			if (ProductStatus.DISABLED.equals(productDTO.getStatus())) {
				returnMap.put("result", "error");
				returnMap.put("resultMsg", ValidatorConstant.PRODUCT_HAVE_DELETED_MSG);
				LOG.info("method=ajaxToDeleteProductByProductLocalInfoId  ，shopId：{} productId:{},该商品已经删除，请勿重复操作" +
						         "productDTO:{},inventorySearchIndexDTO:{},inventoryDTO:{}",
						        new Object[]{shopId, productId, productDTO, inventorySearchIndexDTO, inventoryDTO});
				return returnMap;
			}
			//1校验库存是否为0
			if (!inventoryService.isInventoryEmpty(shopId, productId)) {
				returnMap.put("result", "error");
				returnMap.put("resultMsg", ValidatorConstant.PRODUCT_INVENTORY_NOT_EMPTY_MSG);
				return returnMap;
			}
      //1校验商品是否在上架状态
      if (productService.checkProductInSalesByProductLocalInfoId(shopId,productId)) {
        returnMap.put("result", "error");
        returnMap.put("resultMsg", ValidatorConstant.PRODUCT_IN_SALES_MSG);
        return returnMap;
      }
			//2，校验是否有未结算单据
			List<OrderIndexDTO> unsettledOrders = rfTxnService.getUnsettledOrdersByProductId(shopId, productId);
			if (CollectionUtils.isNotEmpty(unsettledOrders)) {
				returnMap.put("result", "unsettled");
				returnMap.put("resultMsg", ValidatorConstant.PRODUCT_HAVE_UNSETTLED_ORDER_MSG);
				returnMap.put("orderInfo", unsettledOrders);
				return returnMap;
			}
			//3，执行逻辑删除。（product,productLocalInfo,InventorySearchIndex)   productLocalInfo if need to disabled ?
			UserDTO user = new UserDTO();
			user.setShopId(shopId);
			user.setId(WebUtil.getUserId(request));
			productService.setProductStatus(shopId, ProductStatus.DISABLED,user, productId);

			//4,重做索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productId);
			//5，构建memcach
			InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
			inventoryService.caculateBeforeLimit(inventoryDTO,inventoryLimitDTO);
			inventoryLimitDTO.setAfterLowerLimitAmount(0);
			inventoryLimitDTO.setAfterUpperLimitAmount(0);
			MemcacheLimitDTO memcacheLimitDTO = inventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, inventoryLimitDTO);
			Map<Long,Long> productDeltedMap = new HashMap<Long, Long>();
			productDeltedMap.put(productId,System.currentTimeMillis());
			productCurrentUsedService.saveRecentDeletedProductInMemory(shopId,productDeltedMap);
			returnMap.put("result", "success");
			returnMap.put("resultMsg", "删除成功！");
			returnMap.put("memcacheLimitDTO", memcacheLimitDTO);
			return returnMap;
		} catch (Exception e) {
			LOG.error("method=ajaxToDeleteProductByProductLocalInfoId,shopId:{},productId:{}" + e.getMessage(), new Object[]{shopId, productId, e});
		}
		return returnMap;

	}

  //add by WLF 保存某条商品的分类
  @RequestMapping(params = "method=ajaxSaveProductKind")
  public void ajaxSaveProductKind(HttpServletRequest request,HttpServletResponse response){
    String kindName = request.getParameter("kindName");
    Long shopId = WebUtil.getShopId(request);
    Long isiId = Long.parseLong(request.getParameter("productId"));
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<String,String> resultMap = new HashMap<String,String>();
    try {
      //先将商品分类更新到Kind表
      Long kindId = productService.getProductKindId(shopId, kindName);
      //再将商品分类更新到Inventory_Search_Index表
      InventorySearchIndexDTO isiDTO = searchService.getInventorySearchIndexById(shopId,isiId);
      //最后将商品分类更新到Product表
      ProductDTO productDTO = productService.getProductById(isiDTO.getParentProductId()).toDTO();
      productDTO.setKindId(kindId);
      productService.updateProduct(shopId,productDTO);
      if(isiDTO!=null){
        isiDTO.setKindName(kindName);
        searchService.updateInventorySearchIndex(isiDTO);
      }
      //TODO 更新到solr
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, isiId);
	    //保存修改的productId到solr
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
	    recentChangedProductMap.put(isiId, new Pair(System.currentTimeMillis(), false));
	    ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(shopId, recentChangedProductMap);
      resultMap.put("result", "success");
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.objectToJson(resultMap));
      writer.close();
    } catch (Exception e) {
      LOG.error("txn.do?method=ajaxSaveProductKind\n" +
          "shopId:" + shopId + "\n" +
          "id:" + isiId + "\n" +
          "kindName:" + kindName + "\n" +
          e.getMessage(), e);
    }
  }

  //add by WLF ajax查询最近使用的15条商品分类
  @RequestMapping(params = "method=getProductKindsRecentlyUsed")
  public void getProductKindsRecentlyUsed(HttpServletRequest request,HttpServletResponse response){
    Long shopId = WebUtil.getShopId(request);
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<String> kindNameList = productService.getProductKindsRecentlyUsed(shopId);
    String uuid= request.getParameter("uuid");
    List<Map<String,String>> mapList = new ArrayList<Map<String, String>>();
    if(kindNameList!=null){
      for(int i=0;i<kindNameList.size();i++){
        Map<String,String> map = new HashMap<String,String>();
        map.put("label",kindNameList.get(i));
        mapList.add(map);
      }
    }
    Map<String,Object> returnMap = new HashMap<String,Object>();
    returnMap.put("uuid",uuid);
    returnMap.put("data",mapList);
    try{
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.objectToJson(returnMap));
      writer.close();
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
    }
  }

  //add by WLF 保存下拉区域的商品分类
  @RequestMapping(params = "method=saveOrUpdateProductKind")
  public void saveOrUpdateProductKind(HttpServletRequest request,HttpServletResponse response){
    Long shopId = WebUtil.getShopId(request);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    String oldKindName = request.getParameter("oldKindName");
    String newKindName = request.getParameter("newKindName");
    if(newKindName==null || "".equals(newKindName)){
      return;
    }
    try{
      Long oldKindId = productService.getKindIdByName(shopId, oldKindName);
      Long newKindId = productService.getKindIdByName(shopId, newKindName);
      PrintWriter writer = response.getWriter();
      Map<String,String> map = new HashMap<String, String>();
      //该分类存在
      if(newKindId!=null){
        map.put("flag","false");
      }else{
        productService.updateKind(oldKindId,newKindName);
        searchService.updateInventorySearchIndexKindInfo(shopId,oldKindName,newKindName);
        map.put("flag","true");
      }
      Long[] idList = searchService.getInventorySearchIndexIdListByProductKind(shopId,newKindName);
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, idList);
	    //保存修改的productId到solr
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      for(int i=0;i<idList.length;i++){
        recentChangedProductMap.put(idList[i], new Pair(System.currentTimeMillis(), false));
      }
	    ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(shopId, recentChangedProductMap);
      writer.write(JsonUtil.objectToJson(map));
      writer.close();
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
    }
  }

  //回车模糊查询商品分类
  @RequestMapping(params = "method=getProductKindsWithFuzzyQuery")
  public void getProductKindsWithFuzzyQuery(HttpServletRequest request, HttpServletResponse response){
    Long shopId = WebUtil.getShopId(request);
    IProductService productService = ServiceManager.getService(IProductService.class);
    String keyword = request.getParameter("keyword");
    String uuid = request.getParameter("uuid");
    List<String> kindNameList = productService.getProductKindsWithFuzzyQuery(shopId,keyword);
    List<Map<String,String>> mapList = new ArrayList<Map<String, String>>();
    if(kindNameList!=null){
      for(int i=0;i<kindNameList.size();i++){
        Map<String,String> map = new HashMap<String,String>();
        map.put("label",kindNameList.get(i));
        mapList.add(map);
      }
    }
    Map<String,Object> returnMap = new HashMap<String,Object>();
    returnMap.put("uuid",uuid);
    returnMap.put("data",mapList);
    try{
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.objectToJson(returnMap));
      writer.close();
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
    }
  }

  //逻辑删除商品分类，并同步更新InventorySearchIndex
  @RequestMapping(params = "method=deleteProductKind")
  public void deleteProductKind(HttpServletRequest request,HttpServletResponse response){
    Long shopId = WebUtil.getShopId(request);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    String kindName = request.getParameter("kindName");
    if(kindName==null || "".equals(kindName)){
      return;
    }
    try{
      PrintWriter writer = response.getWriter();
      Map<String,String> map = new HashMap<String, String>();
      //先在Kind表中加上删除标识
      productService.deleteProductKind(shopId,kindName);
      // 接着根据kindName查到InventorySearchIndex表的idList
      Long[] idList = searchService.getInventorySearchIndexIdListByProductKind(shopId,kindName);
      //再将InventorySearchIndex表的kindName字段设为null
      searchService.deleteMultipleInventoryKind(shopId,kindName);
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, idList);
	    //保存修改的InventorySearchIndex的Id到solr
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      for(int i=0;i<idList.length;i++){
        recentChangedProductMap.put(idList[i], new Pair(System.currentTimeMillis(), false));
      }
	    ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(shopId, recentChangedProductMap);
      map.put("flag","true");
      writer.write(JsonUtil.objectToJson(map));
      writer.close();
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }




    @RequestMapping(params = "method=ajaxtoupdateinventoryAmountAndAveragePrice")
  public void ajaxtoupdateinventoryAmountAndAveragePrice(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long productId,
                                 Double inventoryAmount,Double actualInventoryAmount,Double actualInventoryAveragePrice,Long storehouseId) throws Exception{
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IProductSolrService productSolrService = ServiceManager.getService(IProductSolrService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
    Map map = new HashMap();
    PrintWriter writer = response.getWriter();
    try {
	    if(productId == null){
		    LOG.error("更新产品信息出错：method=ajaxtoupdateinventoryAmountAndAveragePrice,product_id is null");
		    return;
	    }

      Long userId = WebUtil.getUserId(request);
      ProductModifyLogDTO oldLogDTO = new ProductModifyLogDTO();
      ProductModifyLogDTO newLogDTO = new ProductModifyLogDTO();

      InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
      InventoryDTO oldInventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productId);
      oldLogDTO.setInventory(oldInventoryDTO);

      InventorySearchIndexDTO isiDTO = searchService.getInventorySearchIndexById(shopId, productId);
      if (actualInventoryAveragePrice != null) {
        isiDTO.setInventoryAveragePrice(actualInventoryAveragePrice);
      }
      InventoryDTO inventoryDTO = new InventoryDTO();
      inventoryDTO.setId(productId);
      if (actualInventoryAveragePrice != null) {
        inventoryDTO.setInventoryAveragePrice(actualInventoryAveragePrice);
      }

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO();
        storeHouseInventoryDTO.setStorehouseId(storehouseId);
        storeHouseInventoryDTO.setProductLocalInfoId(productId);
        storeHouseInventoryDTO.setAmount(actualInventoryAmount);
        inventoryDTO.setStoreHouseInventoryDTO(storeHouseInventoryDTO);
        inventoryDTO = inventoryService.updateInventoryInfo(shopId,inventoryDTO,inventoryLimitDTO);
        isiDTO.setAmount(inventoryDTO.getAmount());
      }else{
        inventoryDTO.setAmount(actualInventoryAmount);
        inventoryDTO = inventoryService.updateInventoryInfo(shopId,inventoryDTO,inventoryLimitDTO);
        isiDTO.setAmount(inventoryDTO.getAmount());
      }
      searchService.updateInventorySearchIndex(isiDTO);

      InventoryDTO newInventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productId);
      if(newInventoryDTO != null && actualInventoryAveragePrice == null){
        actualInventoryAveragePrice = newInventoryDTO.getInventoryAveragePrice();
      }
      newLogDTO.setInventory(newInventoryDTO);

      List<ProductModifyLogDTO> logResults = ProductModifyLogDTO.compare(oldLogDTO, newLogDTO);
      for(ProductModifyLogDTO logDTO : logResults){
        logDTO.setProductId(productId);
        logDTO.setShopId(shopId);
        logDTO.setUserId(userId);
        logDTO.setOperationType(ProductModifyOperations.INVENTORY_INDEX_UPDATE);
        logDTO.setStatProcessStatus(StatProcessStatus.NEW);
      }
      txnService.batchCreateProductModifyLog(logResults);

      MemcacheLimitDTO memcacheLimitDTO = inventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, inventoryLimitDTO);

	    //solr reindex
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productId);

      map.put("msg","success");
      map.put("memcacheLimitDTO",memcacheLimitDTO);
      map.put("actualTotalInventoryNum",newInventoryDTO.getAmount());
      map.put("actualTotalInventoryAveragePrice",newInventoryDTO.getInventoryAveragePrice());
      //记录库存变更
      if (actualInventoryAmount != null && inventoryAmount != null) {
        if (actualInventoryAmount.doubleValue() != inventoryAmount.doubleValue()) {
          String userName = WebUtil.getUserName(request);
          IInventoryCheckService inventoryCheckService = ServiceManager.getService(IInventoryCheckService.class);
          InventoryCheckDTO inventoryCheckDTO = new InventoryCheckDTO();
          inventoryCheckDTO.setReceiptNo(txnService.getReceiptNo(WebUtil.getShopId(request), OrderTypes.INVENTORY_CHECK,null));
          if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
            inventoryCheckDTO.setStorehouseId(storehouseId);
            StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, storehouseId);
            inventoryCheckDTO.setStorehouseName(storeHouseDTO==null?null:storeHouseDTO.getName());
          }
          inventoryCheckDTO.setShopId(shopId);
          inventoryCheckDTO.setEditor(userName);
          inventoryCheckDTO.setEditorId(userId);
          inventoryCheckDTO.setEditDate(new Date().getTime());
          List<InventoryCheckItemDTO> itemDTOs = new ArrayList<InventoryCheckItemDTO>();
          InventoryCheckItemDTO itemDTO = new InventoryCheckItemDTO();
          itemDTO.setProductId(productId);
          itemDTO.setInventoryAmount(inventoryAmount);
          itemDTO.setActualInventoryAmount(actualInventoryAmount);
          itemDTO.setInventoryAdjustmentPrice(actualInventoryAveragePrice);
          if(newInventoryDTO != null){
            itemDTO.setUnit(newInventoryDTO.getUnit());
            itemDTO.setInventoryAveragePrice(newInventoryDTO.getInventoryAveragePrice());
          }
          itemDTOs.add(itemDTO);
          inventoryCheckDTO.setItemDTOs(itemDTOs.toArray(new InventoryCheckItemDTO[itemDTOs.size()]));
          Double adjustPriceTotal=NumberUtil.round((NumberUtil.doubleVal(actualInventoryAmount) - NumberUtil.doubleVal(inventoryAmount))
              *NumberUtil.doubleVal(actualInventoryAveragePrice), 2);
          inventoryCheckDTO.setAdjustPriceTotal(adjustPriceTotal);
          inventoryCheckService.saveInventoryCheckOrderWithoutUpdateInventoryInfo(inventoryCheckDTO);
          BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
          BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(inventoryCheckDTO,OrderTypes.INVENTORY_CHECK);
          bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
        }
      }

    } catch (Exception e) {
      map.put("msg","updateError");
      LOG.debug("/stockSearch.do");
      LOG.debug("method=ajaxtoupdateinventoryAmountAndAveragePrice");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }finally {
      writer.write(JsonUtil.mapToJson(map));
      writer.close();
    }
  }

   @RequestMapping(params = "method=ajaxToUpdateAveragePrice")
  public void ajaxToUpdateAveragePrice(ModelMap model, HttpServletRequest request, HttpServletResponse response, Long productId,
                                 Double actualInventoryAveragePrice) throws Exception{
    Long shopId = WebUtil.getShopId(request);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Map map = new HashMap();
    PrintWriter writer = response.getWriter();
    try {
	    if(productId == null){
		    LOG.error("更新产品信息出错：method=ajaxToUpdateAveragePrice,product_id is null");
		    return;
	    }

      Long userId = WebUtil.getUserId(request);
      ProductModifyLogDTO oldLogDTO = new ProductModifyLogDTO();
      ProductModifyLogDTO newLogDTO = new ProductModifyLogDTO();

      InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
      InventoryDTO oldInventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productId);
      oldLogDTO.setInventory(oldInventoryDTO);

      InventorySearchIndexDTO isiDTO = searchService.getInventorySearchIndexById(shopId, productId);
      isiDTO.setInventoryAveragePrice(actualInventoryAveragePrice);
      InventoryDTO inventoryDTO = new InventoryDTO();
      inventoryDTO.setId(productId);
      inventoryDTO.setInventoryAveragePrice(actualInventoryAveragePrice);


      inventoryDTO = inventoryService.updateInventoryInfo(shopId,inventoryDTO,inventoryLimitDTO);
      searchService.updateInventorySearchIndex(isiDTO);

      InventoryDTO newInventoryDTO = txnService.getInventoryByShopIdAndProductId(shopId, productId);
      newLogDTO.setInventory(newInventoryDTO);

      List<ProductModifyLogDTO> logResults = ProductModifyLogDTO.compare(oldLogDTO, newLogDTO);
      for(ProductModifyLogDTO logDTO : logResults){
        logDTO.setProductId(productId);
        logDTO.setShopId(shopId);
        logDTO.setUserId(userId);
        logDTO.setOperationType(ProductModifyOperations.INVENTORY_INDEX_UPDATE);
        logDTO.setStatProcessStatus(StatProcessStatus.NEW);
      }
      txnService.batchCreateProductModifyLog(logResults);

	    //solr reindex
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productId);

      map.put("msg","success");
      map.put("actualTotalInventoryAveragePrice",newInventoryDTO.getInventoryAveragePrice());
    } catch (Exception e) {
      map.put("msg","updateError");
      LOG.debug("/stockSearch.do");
      LOG.debug("method=ajaxToUpdateAveragePrice");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }finally {
      writer.write(JsonUtil.mapToJson(map));
      writer.close();
    }
  }

  @RequestMapping(params = "method=getProductDateToPrint")
  public void getProductDateToPrint(HttpServletRequest request, HttpServletResponse response, String jsonStr, String currentPage) {
    if (StringUtils.isBlank(jsonStr)) {
      return;
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    try {
      ProductSearchResultListDTO productSearchResultListDTO = new Gson().fromJson(jsonStr, new TypeToken<ProductSearchResultListDTO>() {
      }.getType());
      Long storehouseId = null;
      if (NumberUtil.isNumber(request.getParameter("storehouseId"))) {
        storehouseId = Long.parseLong(request.getParameter("storehouseId"));
      }
      DecimalFormat df = new DecimalFormat("#########.##");
      String s = df.format(productSearchResultListDTO.getTotalPurchasePrice());
      productSearchResultListDTO.setTotalPurchasePrice(Double.parseDouble(s));
      if (CollectionUtils.isNotEmpty(productSearchResultListDTO.getProducts())) {
        for (ProductDTO productDTO : productSearchResultListDTO.getProducts()) {
          if (StringUtils.isBlank(productDTO.getSpec())) {
            productDTO.setSpec("--");
          }
          if (StringUtils.isBlank(productDTO.getModel())) {
            productDTO.setModel("--");
          }
          if (StringUtils.isBlank(productDTO.getProductVehicleBrand())) {
            productDTO.setProductVehicleBrand("--");
          }
          if (StringUtils.isBlank(productDTO.getProductVehicleModel())) {
            productDTO.setProductVehicleModel("--");
          }
          if (null == productDTO.getInventoryNum()) {
            productDTO.setInventoryNum(0D);
            productDTO.setInventoryNum(NumberUtil.round(productDTO.getInventoryNum(), 2));
          }else{
            if(storehouseId != null && !productDTO.getStoreHouseInventoryDTOMap().isEmpty()){
              StoreHouseInventoryDTO storeHouseInventoryDTO = productDTO.getStoreHouseInventoryDTOMap().get(storehouseId);
              if(storeHouseInventoryDTO != null){
                productDTO.setInventoryNum(NumberUtil.round(storeHouseInventoryDTO.getAmount(), 2));
              }
            }
          }
        }
      }

      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.INVENTORY_PRINT);
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "stocksearchPrint" + String.valueOf(WebUtil.getShopId(request));

        String myTemplate = str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("currentPage", currentPage);
        context.put("productSearchResultListDTO", productSearchResultListDTO);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    } catch (Exception e) {
      LOG.error("method=getProductDateToPrint");
      LOG.error("jsonStr" + jsonStr);
      LOG.error("currentPage" + currentPage);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getSupplierInventory")
  @ResponseBody
  public Object getSupplierInventory(HttpServletRequest request,HttpServletResponse response,Integer startPageNo,Integer maxRows,Long productId){
     Long shopId = WebUtil.getShopId(request);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    Map<String,Object> result = new HashMap<String, Object>();
    try {
      if (startPageNo == null || startPageNo < 1) {
            startPageNo = 1;
          }
      if(maxRows == null || maxRows <1){
        maxRows = 10;
      }
      int totalResult = productThroughService.countSupplierInventory(shopId,productId);
      Pager pager = new Pager(totalResult,startPageNo,maxRows);
      List<SupplierInventoryDTO> supplierInventoryDTOs = productThroughService.getSupplierInventoryByPaging(shopId, productId, pager);
      result.put("supplierInventoryDTOs",supplierInventoryDTOs);
      result.put("pager",pager);
      result.put("result",new Result(true));
      return result;
    } catch (Exception e) {
      LOG.error("method=getSupplierInventory ;shopId :{}"+e.getMessage(),shopId,e);
      return  result.put("result",new Result(false));
    }
  }

  @RequestMapping(params = "method=getSupplierInventoryByStorehouseAndProductId")
  @ResponseBody
  public Object getSupplierInventoryByStorehouseAndProductId(HttpServletRequest request,HttpServletResponse response,
                                                             Long storehouseId,Long productId){
     Long shopId = WebUtil.getShopId(request);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    Map<String,Object> result = new HashMap<String, Object>();
    try {

      List<SupplierInventoryDTO> supplierInventoryDTOs = productThroughService.getSupplierInventoryDTOsWithOtherStorehouse(shopId,productId,storehouseId);
      result.put("supplierInventoryDTOs",supplierInventoryDTOs);
      result.put("result",new Result(true));
      return result;
    } catch (Exception e) {
      LOG.error("method=getSupplierInventory ;shopId :{}"+e.getMessage(),shopId,e);
      return  result.put("result",new Result(false));
    }
  }

   //闪动提醒判断的开始时间：昨天的23:59:59-999
  private Long getFlashTime() throws Exception {
    return DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()) - 1;
  }

}
