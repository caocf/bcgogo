package com.bcgogo.search;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.InquiryCenterInitialDTO;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-11
 * Time: 下午1:13
 * 查询中心
 */
@Controller
@RequestMapping("/inquiryCenter.do")
public class InquiryCenterController {

  private static final Logger LOG = LoggerFactory.getLogger(InquiryCenterController.class);

  private ISearchOrderService searchOrderService;
  private IOrderIndexService orderIndexService;
  private IItemIndexService itemIndexService;
  private ITxnService txnService;
  private IOrderSolrWriterService orderSearchService;

  public ISearchOrderService getSearchOrderService() {
    if (searchOrderService == null) {
      searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    }
    return searchOrderService;
  }

  public IOrderIndexService getOrderIndexService() {
    if (orderIndexService == null) {
      orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    }
    return orderIndexService;
  }

  public IItemIndexService getItemIndexService() {
    if (itemIndexService == null) {
      itemIndexService = ServiceManager.getService(IItemIndexService.class);
    }
    return itemIndexService;
  }

  public ITxnService getTxnService() {
    if (txnService == null) {
      txnService = ServiceManager.getService(ITxnService.class);
    }
    return txnService;
  }

  public IOrderSolrWriterService getOrderSearchService() {
     if (orderSearchService == null) {
      orderSearchService = ServiceManager.getService(IOrderSolrWriterService.class);
    }
    return orderSearchService;
  }

  @RequestMapping(params = "method=inquiryCenterIndex")
  public String inquiryCenterIndex(ModelMap model, HttpServletRequest request, InquiryCenterInitialDTO inquiryCenterInitialDTO) {
    LOG.info("查询中心。shopId:{}",WebUtil.getShopId(request));
    try {
      if (StringUtils.isNotBlank(inquiryCenterInitialDTO.getPageType())) {
        model.addAttribute("inquiryCenterInitialDTO", inquiryCenterInitialDTO);
        LOG.info("pageType:{}", inquiryCenterInitialDTO.getPageType());
      } else {
        throw new Exception("unknownPage Exception");
      }
      //会员类型
      List<String> memberCardTypes = ServiceManager.getService(IMembersService.class).getMemberCardTypeByShopId(WebUtil.getShopId(request));
      model.addAttribute("memberCardTypes", memberCardTypes);
    } catch (Exception e) {
      LOG.debug("/inquiryCenter.do");
      LOG.debug("method=inquiryCenterIndex");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/search/inquirySystemOrder";
  }

  @RequestMapping(params = "method=toInquiryImportedOrder")
  public String toInquiryImportedOrder(ModelMap model, HttpServletRequest request, InquiryCenterInitialDTO inquiryCenterInitialDTO) {
    if(WebUtil.getShopId(request) == null){
      return "/login";
    }
    model.addAttribute("inquiryCenterInitialDTO", inquiryCenterInitialDTO);
    return "/search/inquiryImportedOrder2";
  }

  /**
   * 根据条件查询order
   *
   * @param request
   * @param orderSearchConditionDTO
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=inquiryCenterSearchOrderAction")
  public Object inquiryCenterSearchOrderAction(HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      //数据校验
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (orderSearchConditionDTO == null) throw new Exception("OrderSearchConditionDTO can't be null.");
      if (shopId == null) throw new Exception("shopId can't be null.");
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(shopId);
      orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET});
      if (ArrayUtils.isEmpty(orderSearchConditionDTO.getOrderType())) {
        orderSearchConditionDTO.setOrderType(OrderTypes.getInquiryCenterOrderTypes());
      }
      if(StringUtils.isBlank(orderSearchConditionDTO.getSort())) {
        orderSearchConditionDTO.setSort("created_time desc");
      }
      orderSearchConditionDTO.setPageRows(orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setRowStart((orderSearchConditionDTO.getStartPageNo() - 1) * orderSearchConditionDTO.getMaxRows());
      orderSearchConditionDTO.setStatsFields(new String[]{OrderSearchResultListDTO.ORDER_TOTAL_AMOUNT, OrderSearchResultListDTO.ORDER_DEBT_AMOUNT, OrderSearchResultListDTO.ORDER_SETTLED_AMOUNT});
      orderSearchConditionDTO.setFacetFields(new String[]{"order_type"});
      if(orderSearchConditionDTO.getOperatorId() != null) {
        orderSearchConditionDTO.setOperator(null);
      }
      OrderSearchResultListDTO orderSearchResultListDTO = getSearchOrderService().queryOrders(orderSearchConditionDTO);
      SupplierDTO supplierDTO=null;
      if (null != orderSearchResultListDTO && null != orderSearchResultListDTO.getOrders()) {
        for (OrderSearchResultDTO order : orderSearchResultListDTO.getOrders()) {
          if (null == order.getCustomerOrSupplierId()) {
            continue;
          }
          if (!OrderTypes.SALE.toString().equals(order.getOrderType()) &&
              !OrderTypes.REPAIR.toString().equals(order.getOrderType()) &&
              !OrderTypes.WASH_BEAUTY.toString().equals(order.getOrderType()) &&
              !OrderTypes.MEMBER_BUY_CARD.toString().equals(order.getOrderType()) &&
              !OrderTypes.MEMBER_RETURN_CARD.toString().endsWith(order.getOrderType())) {
            supplierDTO=userService.getSupplierById(order.getCustomerOrSupplierId());
            if (null != supplierDTO) {
              order.setCustomerStatus(supplierDTO.getStatus());
            }
          }else {
            Customer customer = userService.getCustomerByCustomerId(order.getCustomerOrSupplierId(), WebUtil.getShopId(request));
            if (null != customer) {
              order.setCustomerStatus(customer.getStatus());
            }
          }
        }
      }
      Map<String,Object> data = new HashMap<String, Object>();
      data.put("data",orderSearchResultListDTO);
      Pager pager = new Pager(Integer.valueOf(orderSearchResultListDTO.getNumFound() + ""), orderSearchConditionDTO.getStartPageNo(), orderSearchConditionDTO.getMaxRows());
      data.put("pager",pager);
      return data;
    } catch (Exception e) {
      LOG.debug("/inquiryCenter.do");
      LOG.debug("method=inquiryCenterSearchOrderAction");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @ResponseBody
  @RequestMapping(params = "method=inquiryImportedOrder")
  public Object inquiryImportedOrder(HttpServletRequest request,OrderSearchConditionDTO orderSearchConditionDTO) {
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    try {
      //校验时间
      orderSearchConditionDTO.verificationQueryTime();
      orderSearchConditionDTO.setShopId(WebUtil.getShopId(request));
      return  txnService.getImportedOrderByConditions(orderSearchConditionDTO);

    } catch (Exception e) {
      LOG.debug("/inquiryCenter.do?method=inquiryImportedOrder");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

}
