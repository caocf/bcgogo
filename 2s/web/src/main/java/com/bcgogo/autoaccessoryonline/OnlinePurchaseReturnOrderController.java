package com.bcgogo.autoaccessoryonline;


import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.ProductHistoryDTO;
import com.bcgogo.txn.dto.PurchaseReturnDTO;
import com.bcgogo.txn.dto.PurchaseReturnItemDTO;
import com.bcgogo.txn.dto.SalesOrderItemDTO;
import com.bcgogo.txn.dto.remind.OrderCenterDTO;
import com.bcgogo.txn.service.IProductHistoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.RFTxnService;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.OrderUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在线入库退货单搜索
 */
@Controller
@RequestMapping("/onlinePurchaseReturnOrder.do")
public class OnlinePurchaseReturnOrderController {

  private static final Logger LOG = LoggerFactory.getLogger(OnlinePurchaseReturnOrderController.class);

  /**
   * 跳转到在线入库退货单搜索页面
   *
   * @param request
   * @param modelMap
   * @return
   */
  @RequestMapping(params = "method=toOnlinePurchaseReturnOrder", method = RequestMethod.GET)
  public String toOnlinePurchaseReturnOrder(HttpServletRequest request, ModelMap modelMap) {

    Long shopId = WebUtil.getShopId(request);

    // 统计信息
    IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
    OrderCenterDTO orderCenterDTO = orderCenterService.getOrderCenterStatistics(shopId);
    modelMap.addAttribute("orderCenterDTO", orderCenterDTO);

    setDateToModel(modelMap);

    //待办入库退货单的状态
//    modelMap.addAttribute("statusList", OrderStatus.genPurchaseReturnOrderStatusList());

    return "autoaccessoryonline/ordercenter/onlinePurchaseReturnOrderSearch";

  }

  @RequestMapping(params = "method=onlinePurchaseReturnOrderSearch")
  @ResponseBody
  public PagingListResult onlinePurchaseReturnOrderSearch(HttpServletRequest request, ModelMap modelMap, OrderSearchConditionDTO orderSearchConditionDTO) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    PagingListResult<PurchaseReturnDTO> resultList = new PagingListResult<PurchaseReturnDTO>();
    resultList.setSuccess(true);
    List<PurchaseReturnDTO> purchaseReturnDTOList = new ArrayList<PurchaseReturnDTO>();
    try {
      //数据校验
      Long shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null) {
        orderSearchConditionDTO = new OrderSearchConditionDTO();
      }
      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      if (shopId == null) throw new Exception("shopId can't be null.");
      Set<Long> supplierShopIds =  ServiceManager.getService(IConfigService.class).getRelationWholesalerShopIds(shopId, RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_LIST);
      if (CollectionUtils.isEmpty(supplierShopIds)){
        return resultList;
      }
      String[] supplierShopIdsArray = new String[supplierShopIds.size()];
      int j = 0;
      for (Long id : supplierShopIds) {
        supplierShopIdsArray[j] = String.valueOf(id);
        j++;
      }
      orderSearchConditionDTO.setCustomerOrSupplierShopIds(supplierShopIdsArray);
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(shopId);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      orderSearchConditionDTO.setShopKind(shopDTO.getShopKind());
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.RETURN.toString()});
      if (StringUtils.endsWithIgnoreCase(orderSearchConditionDTO.getOrderStatus()[0], "all")) { // 查询全部
        List<OrderStatus> statusList = new ArrayList<OrderStatus>();
        statusList.add(OrderStatus.SELLER_PENDING);
        statusList.add(OrderStatus.SELLER_ACCEPTED);
        statusList.add(OrderStatus.SELLER_REFUSED);
        statusList.add(OrderStatus.SETTLED);

        String[] statusStrs = new String[statusList.size()];
        for (int i = 0; i < statusList.size(); i++) {
          statusStrs[i] = statusList.get(i).toString();
        }
        orderSearchConditionDTO.setOrderStatus(statusStrs);
      }
      orderSearchConditionDTO.setSort("created_time desc");
      orderSearchConditionDTO.setFacetFields(new String[]{"order_type"});
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_ONLINE_ORDERS});
      ISearchOrderService iSearchOrderService = ServiceManager.getService(ISearchOrderService.class);
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      OrderSearchResultListDTO orderSearchResultListDTO = iSearchOrderService.queryOrders(orderSearchConditionDTO);
      List<OrderSearchResultDTO> orderSearchResultDTOs = orderSearchResultListDTO.getOrders();
      if (!CollectionUtils.isEmpty(orderSearchResultDTOs)) {
        Map<String, ShopDTO> shopDTOMap = new HashMap<String, ShopDTO>();
        Map<String, Object> data = new HashMap<String, Object>();
        for (OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOs) {
          PurchaseReturnDTO purchaseReturnDTO = rfiTxnService.getPurchaseReturnDTOById(orderSearchResultDTO.getOrderId());

          Set<Long> productIds =  purchaseReturnDTO.getProductIdSet();
          Map<Long,ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(shopId,productIds);
          Map<Long,ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class).getProductHistoryDTOMapByProductHistoryIds(purchaseReturnDTO.getProductHistoryIds());
          for (PurchaseReturnItemDTO purchaseReturnItemDTO : purchaseReturnDTO.getItemDTOs()) {
            //组装销售产品信息
            ProductDTO productDTO = productDTOMap.get(purchaseReturnItemDTO.getProductId());
            ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(purchaseReturnItemDTO.getProductHistoryId());
            if (productHistoryDTO != null) {
              purchaseReturnItemDTO.setProductHistoryDTO(productHistoryDTO);
            } else {
              purchaseReturnItemDTO.setProductDTOWithOutUnit(productDTO);
            }
          }
          purchaseReturnDTOList.add(purchaseReturnDTO);

          if(shopDTOMap.get(orderSearchResultDTO.getCustomerOrSupplierShopId().toString()) == null){
            ShopDTO supplierShop = configService.getShopById(orderSearchResultDTO.getCustomerOrSupplierShopId());
            if(supplierShop != null){
              shopDTOMap.put(supplierShop.getId().toString(), supplierShop);
            }
          }
        }
        data.put("shopDTOs", shopDTOMap);
        resultList.setData(data);
      }
      imageService.addImageInfoHistoryToBcgogoItemDTO(imageSceneList, true, purchaseReturnDTOList.toArray(new PurchaseReturnDTO[purchaseReturnDTOList.size()]));
      int start = orderSearchConditionDTO.getStartPageNo()  ;
      int pageSize = orderSearchConditionDTO.getMaxRows();
      int totalCount = Integer.valueOf(orderSearchResultListDTO.getNumFound() + "");
      Pager pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);
      resultList.setResults(purchaseReturnDTOList);
    } catch (Exception e) {
      LOG.error("/onlinePurchaseReturnOrder.do?method=onlinePurchaseReturnOrderSearch", e);
    }
    return resultList;
  }

  public static void setDateToModel(ModelMap model) {
    model.addAttribute("todayDate", DateUtil.getTodayStr(DateUtil.DATE_STRING_FORMAT_DAY));
    model.addAttribute("yesterdayDate", DateUtil.format(DateUtil.DATE_STRING_FORMAT_DAY, DateUtil.getYesterday().getTime()));
  }
}
