package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.RepairRemindEventTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryRemindEventDTO;
import com.bcgogo.txn.dto.LackMaterialDTO;
import com.bcgogo.txn.dto.StoreHouseDTO;
import com.bcgogo.txn.model.RepairRemindEvent;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ServiceUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/goodsindex.do")
public class GoodIndexController {
  private static final Logger LOG = LoggerFactory.getLogger(GoodIndexController.class);

  /**
   * 首页-商品-搜索框 页面跳转
   *
   * @param request
   */
  @RequestMapping(params = "method=creategoodsindex")
  public String createGoodsIndex(ModelMap model, HttpServletRequest request, SearchConditionDTO searchConditionDTO) throws BcgogoException, ParseException {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int count = 0;
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    try {
      //          缺料待修
      count1 = txnService.countRepairRemindEventByShopId(shopId, RepairRemindEventTypes.LACK, null);
      //         来料待修
      count2 = txnService.countRepairRemindEventByShopId(shopId, RepairRemindEventTypes.INCOMING, null);
      //        待入库
      count3 = txnService.countInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId);
      count = 0;//productService.countInventoryProduct(shopId, productName);

      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))){
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        request.setAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }

    } catch (Exception e) {
      LOG.debug("/goodsindex.do");
      LOG.debug("method=creategoodsindex");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    request.setAttribute("count", count);
    request.setAttribute("count1", count1);
    request.setAttribute("count2", count2);
    request.setAttribute("count3", count3);

    String fuzzyMatchingFlag = request.getParameter("fuzzyMatchingFlag");
    String searchProductInfo = "";
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

    LOG.debug(request.getParameter("fuzzyMatchingFlag") + " info:" + request.getParameter("searchWord") + " productName:" + searchConditionDTO.getProductName() + " productIds:" + searchConditionDTO.getProductIds());
    return "/txn/goodsIndex";
  }

  /**
   * 库存信息 6个字段全部匹配
   *
   * @param request
   */
  @RequestMapping(params = "method=inventory")
  private void getInventory(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                            SearchConditionDTO searchConditionDTO) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    LOG.info("库存首页-库存查询开始!,shopId:{}",shopId);
    long begin = System.currentTimeMillis();
    long current = begin;
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    searchConditionDTO.setShopId(shopId);
    if (StringUtils.isBlank(searchConditionDTO.getSort()) && searchConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      searchConditionDTO.setSort("inventory_amount desc,storage_time desc");
    } else {
      searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSort()));
    }
    searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_STATS});
    if(searchConditionDTO.getStorehouseId()!=null){
      searchConditionDTO.setStatsFields(new String[]{searchConditionDTO.getStorehouseId()+"_storehouse_inventory_amount", searchConditionDTO.getStorehouseId()+"_storehouse_inventory_price"});
    }else{
      searchConditionDTO.setStatsFields(new String[]{"inventory_amount", "inventory_price"});
    }
    //配合ajaxPaging.tag 接口的数据封装
    searchConditionDTO.setRows(searchConditionDTO.getMaxRows());
    searchConditionDTO.setStart(searchConditionDTO.getMaxRows() * (searchConditionDTO.getStartPageNo() - 1));
    //不知道field的情况下
    ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
    LOG.debug("库存首页-库存查询--阶段1。执行时间: {} ms", System.currentTimeMillis()-current);
    current = System.currentTimeMillis();

    if (productSearchResultListDTO != null) {
      ServiceManager.getService(IInventoryService.class).getLimitAndAchievementForProductDTOs(productSearchResultListDTO.getProducts(),shopId);
    }
    LOG.debug("库存首页-库存查询--阶段2。执行时间: {} ms", System.currentTimeMillis()-current);
    current = System.currentTimeMillis();
    String jsonStr = JsonUtil.objectToJson(productSearchResultListDTO);
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
      LOG.debug("库存首页-库存查询--阶段3。执行时间: {} ms", System.currentTimeMillis()-current);
      current = System.currentTimeMillis();
      LOG.debug("库存首页-库存查询。总时间: {} ms", System.currentTimeMillis()-begin);
    } catch (Exception e) {
      LOG.debug("/goodsindex.do");
      LOG.debug("method=inventory");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("productName:" + searchConditionDTO.getProductName() + ",startPageNo:" + searchConditionDTO.getStart() + ",maxRows:" + searchConditionDTO.getRows());
      LOG.error(e.getMessage(), e);
    }

  }



  /**
   * 来料分页
   *
   * @param request,response,startPageNo,maxRows,countStr
   *
   */
  @RequestMapping(params = "method=lack")
  private void lack(ModelMap model, HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String countStr) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (startPageNo == null || maxRows == null) {
      LOG.error("分页信息异常：startPageNo:{}，maxRows:{}",startPageNo,maxRows);
      return;
    }
    String jsonStr = "";
    List<RepairRemindEvent> repairRemindEvents = txnService.getRepairRemindEventByShopId(shopId,
        RepairRemindEventTypes.LACK, null, startPageNo - 1, maxRows);
    List<LackMaterialDTO> lackMaterialDTOList = new ArrayList<LackMaterialDTO>();
    for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
      lackMaterialDTOList.add(repairRemindEvent.toLackMaterialDTO());//todo bug 来料待修 zhangjuntao
    }
//    jsonStr = productService.getJsonWithList(lackMaterialDTOList);
    jsonStr = ServiceUtil.getJsonWithList(lackMaterialDTOList);
    int totalRows = txnService.countRepairRemindEventByShopId(shopId, RepairRemindEventTypes.LACK, null);
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
    } catch (Exception e) {
      LOG.debug("/goodsindex.do");
      LOG.debug("method=lack");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + totalRows);
      LOG.error(e.getMessage(), e);
    }

  }

  //邵磊  来料分页
  @RequestMapping(params = "method=incoming")
  private void incoming(ModelMap model, HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String countStr) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    if (startPageNo == null || maxRows == null) {
      LOG.error("分页信息异常：startPageNo:{}，maxRows:{}",startPageNo,maxRows);
      return;
    }
    List<RepairRemindEvent> repairRemindEvents = txnService.getRepairRemindEventByShopId(shopId,
        RepairRemindEventTypes.INCOMING, null, startPageNo - 1, maxRows);
    List<LackMaterialDTO> lackMaterialDTOList = new ArrayList<LackMaterialDTO>();
    for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
      lackMaterialDTOList.add(repairRemindEvent.toLackMaterialDTO());
    }
//    jsonStr = productService.getJsonWithList(lackMaterialDTOList);
    jsonStr = ServiceUtil.getJsonWithList(lackMaterialDTOList);
    //int count2=countStr==null?0:Integer.parseInt(countStr);
    int totalRows = txnService.countRepairRemindEventByShopId(shopId, RepairRemindEventTypes.INCOMING, null);
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
    } catch (Exception e) {
      LOG.debug("/goodsindex.do");
      LOG.debug("method=incoming");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + totalRows);
      LOG.error(e.getMessage(), e);
    }

  }

  //邵磊   待入库
  @RequestMapping(params = "method=waitcoming")
  private void waitcoming(ModelMap model, HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows, String countStr) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String jsonStr = "";
    try {
      List<InventoryRemindEventDTO> itemsList = txnService.getInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId, startPageNo - 1, maxRows);

//      jsonStr = productService.getJsonWithList(itemsList);
      jsonStr = ServiceUtil.getJsonWithList(itemsList);
      //int count3=countStr==null?0:Integer.parseInt(countStr);
      int totalRows = txnService.countInventoryRemindEventByShopIdAndPageNoAndPageSize(shopId);

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
      } catch (Exception e) {
        LOG.debug("/goodsindex.do");
        LOG.debug("method=waitcoming");
        LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + totalRows);
        LOG.error(e.getMessage(), e);
      }

    } catch (Exception e) {
      LOG.debug("/goodsindex.do");
      LOG.debug("method=waitcoming");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("startPageNo:" + startPageNo + ",maxRows:" + maxRows + ",countStr:" + countStr);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=dealproduct")
  public String dealProduct(ModelMap model, HttpServletRequest request) {
    Integer flag = Integer.parseInt(request.getParameter("flag"));
    String productIds = request.getParameter("productIds");
    String[] pId = productIds.split(",");
    List<Long> idList = new ArrayList();
    for (String ids : pId) {
      if (ids != null && !"".equals(ids)) {
        idList.add(Long.parseLong(ids));
      }
    }
    model.addAttribute("idList", idList);
    if (flag == 1) {
      return "";//  等待页面
    } else if (flag == 2) {
      return "/txn/goodsStorage";
    } else if (flag == 3) {
      return "/txn/goodsSale";
    } else {
      return "";//等待页面
    }
  }

}
