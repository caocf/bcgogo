package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.ProductHistoryDTO;
import com.bcgogo.txn.dto.SalesReturnDTO;
import com.bcgogo.txn.dto.SalesReturnItemDTO;
import com.bcgogo.txn.dto.remind.OrderCenterDTO;
import com.bcgogo.txn.service.IProductHistoryService;
import com.bcgogo.txn.service.ISaleReturnOrderService;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.utils.DateUtil;
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
import java.util.*;

/**
 * 在线销售退货单
 * User: terry
 * Date: 13-8-20
 * Time: 上午11:28
 */
@Controller
@RequestMapping("/onlineSalesReturnOrder.do")
public class OnlineSalesReturnOrderController {

  private static final Logger LOG = LoggerFactory.getLogger(OnlineSalesReturnOrderController.class);

  /**
   * 与消息有关系 参数修改请斟酌 zhangjuntao
   * 跳转到在线销售退货单搜索页面
   *
   * @param request
   * @param modelMap
   * @return
   */
  @RequestMapping(params = "method=toOnlineSalesReturnOrder", method = RequestMethod.GET)
  public String toOnlineSalesReturnOrder(HttpServletRequest request, ModelMap modelMap) {

    Long shopId = WebUtil.getShopId(request);

    // 统计信息
    IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
    OrderCenterDTO orderCenterDTO = orderCenterService.getOrderCenterStatistics(shopId);
    modelMap.addAttribute("orderCenterDTO", orderCenterDTO);

    modelMap.addAttribute("todayDate", DateUtil.getTodayStr(DateUtil.DATE_STRING_FORMAT_DAY));
    modelMap.addAttribute("yesterdayDate", DateUtil.format(DateUtil.DATE_STRING_FORMAT_DAY, DateUtil.getYesterday().getTime()));
    return "autoaccessoryonline/ordercenter/onlineSalesReturnOrderSearch";

  }

  @RequestMapping(params = "method=onlineSalesReturnOrderSearch")
  @ResponseBody
  public PagingListResult onlineSalesReturnOrderSearch(HttpServletRequest request, ModelMap modelMap, OrderSearchConditionDTO orderSearchConditionDTO) {
    PagingListResult<SalesReturnDTO> resultList = new PagingListResult<SalesReturnDTO>();
    resultList.setSuccess(true);
    List<SalesReturnDTO> salesReturnDTOList = new ArrayList<SalesReturnDTO>();
    Map<String, Object> data = new HashMap<String, Object>();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ISaleReturnOrderService saleReturnOrderService = ServiceManager.getService(ISaleReturnOrderService.class);
    try {
      //数据校验
      Long shopId = WebUtil.getShopId(request);
      if (orderSearchConditionDTO == null) {
        orderSearchConditionDTO = new OrderSearchConditionDTO();
      }
      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      if (shopId == null) throw new Exception("shopId can't be null.");
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(shopId);
      ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      orderSearchConditionDTO.setShopKind(shopDTO.getShopKind());
      orderSearchConditionDTO.setOrderType(new String[]{OrderTypes.SALE_RETURN.toString()});
      if (StringUtils.endsWithIgnoreCase(orderSearchConditionDTO.getOrderStatus()[0], "all")) { // 查询全部
        List<OrderStatus> statusList = new ArrayList<OrderStatus>();
        statusList.add(OrderStatus.PENDING);
        statusList.add(OrderStatus.WAITING_STORAGE);
        statusList.add(OrderStatus.REFUSED);
        statusList.add(OrderStatus.SETTLED);
        statusList.add(OrderStatus.STOP);

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
      OrderSearchResultListDTO orderSearchResultListDTO = iSearchOrderService.queryOrders(orderSearchConditionDTO);
      List<OrderSearchResultDTO> orderSearchResultDTOs = orderSearchResultListDTO.getOrders();

      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);

      Map<String, ShopDTO> shopDTOMap = new HashMap<String, ShopDTO>();
      if (!CollectionUtils.isEmpty(orderSearchResultDTOs)) {
        for (OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOs) {
          SalesReturnDTO salesReturnDTO = saleReturnOrderService.getSalesReturnDTOById(shopId,orderSearchResultDTO.getOrderId());

          Set<Long> productIds =  salesReturnDTO.getProductIdSet();
          Map<Long,ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(shopId,productIds);
          Map<Long,ProductHistoryDTO> productHistoryDTOMap = ServiceManager.getService(IProductHistoryService.class).getProductHistoryDTOMapByProductHistoryIds(salesReturnDTO.getProductHistoryIds());
          for (SalesReturnItemDTO salesReturnItemDTO : salesReturnDTO.getItemDTOs()) {
            //组装销售产品信息
            ProductDTO productDTO = productDTOMap.get(salesReturnItemDTO.getProductId());
            ProductHistoryDTO productHistoryDTO = productHistoryDTOMap.get(salesReturnItemDTO.getProductHistoryId());
            if (productHistoryDTO != null) {
              salesReturnItemDTO.setProductHistoryDTO(productHistoryDTO);
            } else {
              salesReturnItemDTO.setProductDTOWithOutUnit(productDTO);
            }
          }
          salesReturnDTOList.add(salesReturnDTO);

          if(shopDTOMap.get(orderSearchResultDTO.getCustomerOrSupplierShopIdStr()) == null){
            ShopDTO customerShop = configService.getShopById(orderSearchResultDTO.getCustomerOrSupplierShopId());
            if(customerShop!=null){
              shopDTOMap.put(customerShop.getId().toString(), customerShop);
            }
          }
        }
      }
      data.put("shopDTOs", shopDTOMap);
      resultList.setData(data);
      imageService.addImageInfoHistoryToBcgogoItemDTO(imageSceneList, true, salesReturnDTOList.toArray(new SalesReturnDTO[salesReturnDTOList.size()]));
      int start = orderSearchConditionDTO.getStartPageNo()  ;
      int pageSize = orderSearchConditionDTO.getMaxRows();
      int totalCount = Integer.valueOf(orderSearchResultListDTO.getNumFound() + "");
      Pager pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);
      resultList.setResults(salesReturnDTOList);
    } catch (Exception e) {
      LOG.error("/onlineSalesReturnOrder.do?method=onlineSalesReturnOrderSearch", e);
    }
    return resultList;
  }

}
