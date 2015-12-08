package com.bcgogo.search.service;

import com.bcgogo.enums.*;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.model.OrderIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-4-10
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OrderIndexService implements IOrderIndexService {
   private static final Logger LOG = LoggerFactory.getLogger(OrderIndexService.class);
  @Autowired
  private SearchDaoManager searchDaoManager;


  /**
   * clear order core
   *
   * @throws Exception
   */
  @Override
  public void deleteOrderFromSolr(OrderTypes orderType, Long shopId) throws Exception {
    String deleteQuery = null;
    deleteQuery = "shop_id:" + shopId;
    if(orderType!=null){
      deleteQuery+=" AND order_type:"+orderType;
    }
    SolrClientHelper.getOrderSolrClient().deleteByQuery(deleteQuery);
    SolrClientHelper.getOrderItemSolrClient().deleteByQuery(deleteQuery);
  }


  @Override
  public List<OrderIndexDTO> getOrderIndexForReindex(Long shopId, long startId, long endId) throws Exception {
    if (endId < startId) throw new Exception("endId should greater than startId");
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getOrderIndexForReindexByRange(shopId, startId, endId);
  }

  @Override
  public Long getOrderIndexMaxId(Long shopId, long startId, int pageSize) throws Exception {
    if (pageSize < 1) throw new Exception("pageSize should greater than 0");
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getOrderIndexMaxId(shopId, startId, pageSize);
  }

  /**
   * 将OrderIndex加入SOLR
   *
   * @param coll
   * @throws Exception
   * @author wjl
   */
  @Override
  public void addOrderIndexToSolr(Collection<OrderIndexDTO> coll) throws Exception {
    if (coll != null && coll.size() > 0) {
      Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
      for (OrderIndexDTO orderIndexDTO : coll) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", orderIndexDTO.getOrderId());
        doc.addField("shop_id", orderIndexDTO.getShopId());
        doc.addField("shop_name", orderIndexDTO.getShopName());
        doc.addField("shop_kind", orderIndexDTO.getShopKind());
        doc.addField("shop_area_info", orderIndexDTO.getShopAreaInfo());
        if (CollectionUtils.isNotEmpty(orderIndexDTO.getShopAreaIdList())) {
          for (Long areaNo : orderIndexDTO.getShopAreaIdList()) {
            if(areaNo!=null){
              doc.addField("shop_area_ids", areaNo);
            }
          }
        }
        doc.addField("editor", orderIndexDTO.getEditor());
        doc.addField("order_type", orderIndexDTO.getOrderType());
        doc.addField("order_status", orderIndexDTO.getOrderStatus());
        doc.addField("order_total_amount", NumberUtil.doubleVal(orderIndexDTO.getOrderTotalAmount()));
        doc.addField("order_settled_amount", NumberUtil.doubleVal(orderIndexDTO.getOrderSettled()));
        doc.addField("order_debt_amount", NumberUtil.doubleVal(orderIndexDTO.getOrderDebt()));
        doc.addField("total_cost_price", NumberUtil.doubleVal(orderIndexDTO.getOrderTotalCostPrice()));
        doc.addField("discount", NumberUtil.doubleVal(orderIndexDTO.getDiscount()));
        doc.addField("member_balance_pay",NumberUtil.doubleVal(orderIndexDTO.getMemberBalancePay()));
        if (NumberUtil.longValue(orderIndexDTO.getAccountMemberId()) > 0) {
          doc.addField("account_member_id", orderIndexDTO.getAccountMemberId());
        }
        if(StringUtil.isNotEmpty(orderIndexDTO.getAccountMemberNo())) {
          doc.addField("account_member_no", orderIndexDTO.getAccountMemberNo());
        }

        if(StringUtil.isNotEmpty(orderIndexDTO.getDebtType())) {
          doc.addField("debt_type", orderIndexDTO.getDebtType());
        }

        double grossProfit = NumberUtil.round((NumberUtil.doubleVal(orderIndexDTO.getOrderSettled()))
            +(NumberUtil.doubleVal(orderIndexDTO.getOrderDebt()))
            -(NumberUtil.doubleVal(orderIndexDTO.getOrderTotalCostPrice())),NumberUtil.MONEY_PRECISION);
        doc.addField("gross_profit",grossProfit);

        doc.addField("title", orderIndexDTO.getTitle());
        doc.addField("memo", orderIndexDTO.getMemo());
        doc.addField("member_last_buy_total", orderIndexDTO.getMemberLastBuyTotal());
        doc.addField("member_last_buy_date", orderIndexDTO.getMemberLastBuyDate());
        doc.addField("member_last_recharge", orderIndexDTO.getMemberLastRecharge());
        if (StringUtils.isNotBlank(orderIndexDTO.getMemberNo())) {
          doc.addField("member_no", orderIndexDTO.getMemberNo());
        }
        if (StringUtils.isNotBlank(orderIndexDTO.getMemberType())) {
          doc.addField("member_type", orderIndexDTO.getMemberType());
        }
        if (StringUtils.isNotBlank(orderIndexDTO.getMemberStatus())) {
          doc.addField("member_status", orderIndexDTO.getMemberStatus());
        }
        doc.addField("worth", NumberUtil.doubleVal(orderIndexDTO.getWorth()));
        doc.addField("member_balance", NumberUtil.doubleVal(orderIndexDTO.getMemberBalance()));
        if (StringUtils.isNotBlank(orderIndexDTO.getCustomerOrSupplierName())) {
          doc.addField("customer_or_supplier_name", orderIndexDTO.getCustomerOrSupplierName());
        }
        if (StringUtils.isNotBlank(orderIndexDTO.getContact())) {
          doc.addField("contact", orderIndexDTO.getContact());
        }
        if(StringUtils.isNotBlank(orderIndexDTO.getVehicleContact())) {
           doc.addField("contact", orderIndexDTO.getVehicleContact());
        }
        doc.addField("address", orderIndexDTO.getAddress());
        doc.addField("customer_or_supplier_id", orderIndexDTO.getCustomerOrSupplierId());
        if(orderIndexDTO.getCustomerOrSupplierShopId() != null){
          doc.addField("customer_or_supplier_shop_id", orderIndexDTO.getCustomerOrSupplierShopId());
        }
        doc.addField("vehicle", orderIndexDTO.getVehicle());

        doc.addField("vehicle_brand", orderIndexDTO.getVehicleBrand());
        doc.addField("vehicle_model", orderIndexDTO.getVehicleModel());
        doc.addField("vehicle_color", orderIndexDTO.getVehicleColor());
        doc.addField("contact_num", orderIndexDTO.getContactNum());
        doc.addField("contact_num", orderIndexDTO.getVehicleMobile());//车辆联系手机
        //归属时间不存在 使用create, 施工单一直使用进厂时间
        if (orderIndexDTO.getVestDate() != null && orderIndexDTO.getVestDate() >0 && orderIndexDTO.getOrderType() != OrderTypes.REPAIR) {
          doc.addField("created_time", orderIndexDTO.getVestDate());
        } else {
          //适配脏数据
          doc.addField("created_time", orderIndexDTO.getCreationDate());
        }
         doc.addField("end_time", orderIndexDTO.getEndDate());
        if (StringUtils.isNotBlank(orderIndexDTO.getOrderContent())) {
           doc.addField("order_content", orderIndexDTO.getOrderContent());
          }
        //施工人员
        if (StringUtils.isNotBlank(orderIndexDTO.getServiceWorker())) {
          String str = orderIndexDTO.getServiceWorker().replace('，', ',');
          for (String sw : str.split(",")) {
            if (StringUtils.isNotBlank(sw))
            doc.addField("service_worker", sw);
          }
        }/*else{
           doc.addField("service_worker", TxnConstant.ASSISTANT_NAME);
        }*/
        //计次收费项目
        if (CollectionUtils.isNotEmpty(orderIndexDTO.getPayPerProjects())) {
          for (String payPerProject : orderIndexDTO.getPayPerProjects()) {
            if (StringUtils.isNotBlank(payPerProject)) {
              doc.addField("pay_per_project", payPerProject);
            } else {
              LOG.warn("order[id:{}] service is empty.order type[{}]", new Object[]{orderIndexDTO.getOrderId(),orderIndexDTO.getOrderType()});
            }
          }
        }

        //消费券
        if(CollectionUtils.isNotEmpty(orderIndexDTO.getCouponTypes())){
          for(String couponType : orderIndexDTO.getCouponTypes()){
            if(StringUtils.isNotBlank(couponType)){
              doc.addField("coupon_type", couponType);
            }
          }
        }
        //客户或供应商的区域ID
        if (CollectionUtils.isNotEmpty(orderIndexDTO.getCustomerOrSupplierAreaIdList())) {
          for (Long areaNo : orderIndexDTO.getCustomerOrSupplierAreaIdList()) {
            if(areaNo!=null){
              doc.addField("customer_or_supplier_area_ids", areaNo);
            }
          }
        }
        if (CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())) {
          for (ItemIndexDTO itemIndexDTO : orderIndexDTO.getItemIndexDTOList()) {
            if (itemIndexDTO == null) continue;
            doc.addField("item_detail", itemIndexDTO.generateItemDetail());
            if (StringUtils.isNotBlank(itemIndexDTO.getItemName())) {
              if(ItemTypes.MATERIAL.equals(itemIndexDTO.getItemType())){
                doc.addField("product_name", itemIndexDTO.getItemName());
              }else{
                doc.addField("service", itemIndexDTO.getItemName());
                PingyinInfo pingyinInfo = PinyinUtil.getPingyinInfo(itemIndexDTO.getItemName());
                doc.addField("service_fl", pingyinInfo.firstLetters);
                doc.addField("service_py", pingyinInfo.pingyin);
              }
            }
            if (StringUtils.isNotBlank(itemIndexDTO.getCommodityCode())) {
              doc.addField("commodity_code", itemIndexDTO.getCommodityCode());
            }
            if (StringUtils.isNotBlank(itemIndexDTO.getItemBrand())) {
              doc.addField("product_brand", itemIndexDTO.getItemBrand());
            }
            if (StringUtils.isNotBlank(itemIndexDTO.getItemModel())) {
              doc.addField("product_model", itemIndexDTO.getItemModel());
            }
            if (StringUtils.isNotBlank(itemIndexDTO.getItemSpec())) {
              doc.addField("product_spec", itemIndexDTO.getItemSpec());
            }
            if (StringUtils.isNotBlank(itemIndexDTO.getVehicleBrand())) {
              doc.addField("product_vehicle_brand", itemIndexDTO.getVehicleBrand());
            }
            if (StringUtils.isNotBlank(itemIndexDTO.getVehicleModel())) {
              doc.addField("product_vehicle_model", itemIndexDTO.getVehicleModel());
            }
          }
        }
        //销售人员
        if (StringUtils.isNotBlank(orderIndexDTO.getSalesMans())) {
          for (String sm : orderIndexDTO.getSalesMans().split(",")) {
            doc.addField("sales_man", sm);
          }
        } /*else {
          doc.addField("sales_man", TxnConstant.ASSISTANT_NAME);
        }*/
        if (CollectionUtils.isNotEmpty(orderIndexDTO.getPayMethods())) {
          for (PayMethod pm : orderIndexDTO.getPayMethods()) {
            doc.addField("pay_method", pm.name());
          }
        }
        doc.addField("business_chance_type", orderIndexDTO.getBusinessChanceType());
        doc.addField("receipt_no", orderIndexDTO.getReceiptNo());
        doc.addField("unpaid", orderIndexDTO.hasArrears());
        if(CollectionUtils.isNotEmpty(orderIndexDTO.getOperators())) {
          for(String operator : orderIndexDTO.getOperators()) {
            doc.addField("operator", operator);
          }
        }
        if(CollectionUtils.isNotEmpty(orderIndexDTO.getOperatorIds())) {
          for(Long operatorId : orderIndexDTO.getOperatorIds()) {
            doc.addField("operator_id",operatorId);
          }
        }
        doc.addField("payment_time", orderIndexDTO.getPaymentTime());
        doc.addField("customer_status", orderIndexDTO.getCustomerStatus());
        doc.addField("member_discount_ratio",orderIndexDTO.getMemberDiscountRatio());
        doc.addField("after_member_discount_total",orderIndexDTO.getAfterMemberDiscountTotal());
        doc.addField("storehouse_name",orderIndexDTO.getStorehouseName());
        doc.addField("inventory_vest_time",orderIndexDTO.getInventoryVestDate());
        docs.add(doc);
//        LOG.debug("单个Order SolrInputDocument 字节："+ SolrUtil.getSolrInputDocumentSize(doc));
      }
      LOG.debug("start system time : " + System.currentTimeMillis());
      LOG.debug("docs.size : " + docs.size());
      LOG.debug("orderSolrClient.addDocs(docs) 开始");
      SolrClientHelper.getOrderSolrClient().addDocs(docs);
      LOG.debug("updateOrder 结束.");
      LOG.debug("end system time : " + System.currentTimeMillis());
    }
  }


  /**
   * 同时支持新旧数据, 对Solr中取得的orderType作类型判断
   * 旧数据抛弃  by jmc 2013-1-18
   *
   * @param toJudgeOrderType Solr中取得的orderType
   * @param orderType  待比对的OrderTypes Enum
   * @return
   */
  private boolean judgeOrderType(String toJudgeOrderType, OrderTypes orderType) {
//    OrderTypes type = ConstantEnumMapping.parseSearchConstantToOrderTypeEnum(toJudgeOrderType);
//    if (type == null) {
    return OrderTypes.valueOf(toJudgeOrderType).equals(orderType);
//    } else {
//      return type.equals(orderType);
//    }
  }

  /**
   * 同时支持新旧数据, 对Solr中取得的orderStatus作类型判断
   * 旧数据抛弃， by jmc 2013-1-18
   *
   * @param toJudgeOrderStatus   Solr中取得的orderStatus
   * @param orderStatus    待比对的OrderStatus Enum
   * @param orderType   前提OrderType
   * @return
   */
  private boolean judgeOrderStatus(String toJudgeOrderStatus, OrderStatus orderStatus, OrderTypes orderType) {
//    if (NumberUtil.isNumber(toJudgeOrderStatus)) {
//      OrderStatus status = ConstantEnumMapping.parseConstantToOrderStatusEnum(Long.parseLong(toJudgeOrderStatus), orderType);
//      if (status == null) {
//        return OrderStatus.valueOf(toJudgeOrderStatus).equals(orderStatus);
//      } else {
//        return status.equals(orderStatus);
//      }
//    } else {
    return OrderStatus.valueOf(toJudgeOrderStatus).equals(orderStatus);
//    }
  }

  @Override
  public List<OrderIndexDTO> getOrderIndexByServiceWork(Long shopId, String q, Long startTime, Long endTime, int start, int rows) throws Exception {

    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    QueryResponse rsp = searchOrderService.queryOrderByServiceWorker(shopId, q, startTime, endTime, start, rows);
    SolrDocumentList documents = rsp.getResults();
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    for (SolrDocument document : documents) {
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

      orderIndexDTO.setOrderId(Long.parseLong((String) document.getFirstValue("id")));
      orderIndexDTO.setShopId(Long.parseLong((String.valueOf(document.getFirstValue("shop_id")))));
      if (document.getFieldValue("customer_or_supplier_id") != null) {
        long customOrSupplierId = Long.parseLong(document.getFieldValue("customer_or_supplier_id").toString());
        orderIndexDTO.setCustomerOrSupplierId(customOrSupplierId);
      }

      List<Object> objectList = (List<Object>) document.getFieldValue("order_content");
      String orderContent = null;
      if (objectList != null && objectList.size() > 0) {
        orderContent = objectList.toString();
        orderContent = orderContent.substring(1, orderContent.length() - 1);
        orderIndexDTO.setOrderContent(orderContent);
      } else {
        orderIndexDTO.setOrderContent("");
      }


      if (document.getFieldValue("order_type") != null) {
        String orderType = (String) document.getFieldValue("order_type");
        if (!StringUtil.isEmpty(orderType)) {
          if (judgeOrderType(orderType, OrderTypes.PURCHASE)) {
            orderIndexDTO.setOrderType(OrderTypes.PURCHASE);
            //由于每个采购单入库了以后，要改变这个采购单的状态的，因此在这里加个判断
            if (document.getFieldValue("order_status") != null) {
              String orderStatus = (String) document.getFieldValue("order_status");
              if (!StringUtil.isEmpty(orderStatus)) {
                if (judgeOrderStatus(orderStatus, OrderStatus.PURCHASE_ORDER_WAITING, OrderTypes.PURCHASE)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_ORDER_WAITING);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.PURCHASE_ORDER_DONE, OrderTypes.PURCHASE)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_ORDER_DONE);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.PURCHASE_ORDER_REPEAL, OrderTypes.PURCHASE)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_ORDER_REPEAL);
                }
              }
            }
            orderIndexDTO.setWashingStr("");
            orderIndexDTO.setServiceStr("");
            orderIndexDTO.setSalesStr("");

          } else if (judgeOrderType(orderType, OrderTypes.INVENTORY)) {
            orderIndexDTO.setOrderType(OrderTypes.INVENTORY);
            String orderStatus = (String) document.getFieldValue("order_status");
            if (!StringUtil.isEmpty(orderStatus)) {
              if (judgeOrderStatus(orderStatus, OrderStatus.PURCHASE_INVENTORY_DONE, OrderTypes.INVENTORY)) {
                orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_INVENTORY_DONE);
              } else if (judgeOrderStatus(orderStatus, OrderStatus.PURCHASE_INVENTORY_REPEAL, OrderTypes.INVENTORY)) {
                orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_INVENTORY_REPEAL);
              }
            }

            orderIndexDTO.setWashingStr("");
            orderIndexDTO.setServiceStr("");
            orderIndexDTO.setSalesStr("");
          } else if (judgeOrderType(orderType, OrderTypes.SALE)) {
            orderIndexDTO.setOrderType(OrderTypes.SALE);
            orderIndexDTO.setWashingStr("");
            orderIndexDTO.setServiceStr("");
            String orderStatus = (String) document.getFieldValue("order_status");
            if (!StringUtil.isEmpty(orderStatus)) {
              if (judgeOrderStatus(orderStatus, OrderStatus.SALE_DONE, OrderTypes.SALE)) {
                orderIndexDTO.setOrderStatus(OrderStatus.SALE_DONE);
              } else if (judgeOrderStatus(orderStatus, OrderStatus.SALE_REPEAL, OrderTypes.SALE)) {
                orderIndexDTO.setOrderStatus(OrderStatus.SALE_REPEAL);
              }
              else if (judgeOrderStatus(orderStatus, OrderStatus.SALE_DEBT_DONE, OrderTypes.SALE)) {
                orderIndexDTO.setOrderStatus(OrderStatus.SALE_DONE);
              }
            }
             String orderContentStr = orderIndexDTO.getOrderContent();
            if (!StringUtil.isEmpty(orderContentStr) && orderContentStr.length() > 5) {
              orderIndexDTO.setSalesStr(orderContentStr.substring(5, orderContentStr.length()));
            }
          } else if (judgeOrderType(orderType, OrderTypes.REPAIR)) {
            orderIndexDTO.setOrderType(OrderTypes.REPAIR);
            orderIndexDTO.setWashingStr("");
            int length = orderIndexDTO.getOrderContent().length();
            String orderContentStr = orderIndexDTO.getOrderContent();
            if (!StringUtil.isEmpty(orderContentStr) && orderContentStr.length() > 5) {
              int indexSale = orderContentStr.indexOf("销售内容:");
              int indexService = orderContent.indexOf("施工内容:");
              if (indexService != -1) {
                if (indexSale == -1) {
                  orderIndexDTO.setServiceStr(orderContentStr.substring(5, length));
                  orderIndexDTO.setSalesStr("");
                } else {
                  orderIndexDTO.setServiceStr(orderContentStr.substring(5, indexSale));
                  orderIndexDTO.setSalesStr(orderContentStr.substring(indexSale + 5, length));
                }
              } else {
                orderIndexDTO.setSalesStr(orderContentStr.substring(5, length));
                orderIndexDTO.setServiceStr("");
              }
            }

            if (document.getFieldValue("order_status") != null) {
              String orderStatus = (String) document.getFieldValue("order_status");
              if (!StringUtil.isEmpty(orderStatus)) {
                if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_DISPATCH, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_DISPATCH);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_DONE, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_DONE);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_SETTLED, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_SETTLED);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_REPEAL, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_REPEAL);
                }
              }
            }
          } else if (judgeOrderType(orderType, OrderTypes.WASH)) {
            orderIndexDTO.setOrderType(OrderTypes.WASH);
            orderIndexDTO.setOrderStatus(OrderStatus.WASH_SETTLED);
            orderIndexDTO.setWashingStr(orderIndexDTO.getOrderContent());
            orderIndexDTO.setSalesStr("");
            orderIndexDTO.setServiceStr("");
          } else if (judgeOrderType(orderType, OrderTypes.WASH_BEAUTY)) {
            orderIndexDTO.setOrderType(OrderTypes.WASH_BEAUTY);
            String orderStatus = (String) document.getFieldValue("order_status");
             if (!StringUtil.isEmpty(orderStatus)) {
               if (judgeOrderStatus(orderStatus, OrderStatus.WASH_REPEAL, OrderTypes.WASH_BEAUTY)) {
                 orderIndexDTO.setOrderStatus(OrderStatus.WASH_REPEAL);
               } else {
            orderIndexDTO.setOrderStatus(OrderStatus.WASH_SETTLED);
               }
             }
            orderIndexDTO.setWashingStr(orderContent);
            orderIndexDTO.setSalesStr("");
            orderIndexDTO.setServiceStr("");
          } else if (judgeOrderType(orderType, OrderTypes.MEMBER_BUY_CARD)) {
            orderIndexDTO.setOrderType(OrderTypes.MEMBER_BUY_CARD);
            orderIndexDTO.setOrderStatus(OrderStatus.MEMBERCARD_ORDER_STATUS);
            orderIndexDTO.setWashingStr("");
            orderIndexDTO.setSalesStr("");
            orderIndexDTO.setServiceStr(orderContent);
          }
        }
      }
      orderIndexDTO.setOrderTotalAmount((Double) document.getFieldValue("order_total_amount"));
      if (document.getFieldValue("customer_or_supplier_name") != null) {
        String customerOrSupplierName = (String) document.getFieldValue("customer_or_supplier_name");
        //customerOrSupplierName = customerOrSupplierName.substring(1,customerOrSupplierName.length()-1);
        orderIndexDTO.setCustomerOrSupplierName(customerOrSupplierName);

      }

      if (document.getFieldValue("vehicle") != null) {
        String vehicle = (String) document.getFieldValue("vehicle");
        if (!StringUtil.isEmpty(vehicle)) {
          vehicle = vehicle.substring(1, vehicle.length() - 1);
          orderIndexDTO.setVehicle(vehicle);
        }
      }

      List<Object> serviceWorkerList = null;
      List<Object> salesManList = null;
      List<Object> contactNumList = null;
      if (document.getFieldValue("service_worker") != null) {
        serviceWorkerList = (List<Object>) document.getFieldValue("service_worker");
      }
      if (document.getFieldValue("sales_man") != null) {
        salesManList = (List<Object>) document.getFieldValue("sales_man");
      }
//      if(document.getFieldValue("contact_num") != null) {
//        contactNumList = (List<Object>) document.getFieldValue("contact_num");
//      }
      List<String> serviceWorker = new ArrayList<String>();
      List<String> contactNum = new ArrayList<String>();
      if (CollectionUtils.isNotEmpty(serviceWorkerList)) {
          for (Object object : serviceWorkerList) {
            String str = (String) object;
            if (!serviceWorker.contains(str)) {
              serviceWorker.add(str);
            }
          }
        }
      if (CollectionUtils.isNotEmpty(salesManList)) {
        for (Object object : salesManList) {
          String str = (String) object;
          if (!serviceWorker.contains(str)) {
            serviceWorker.add(str);
          }
        }
      }
      if(CollectionUtils.isNotEmpty(contactNumList)) {
        for(Object object : contactNumList) {
          String str = (String)object;
          if(!contactNum.contains(str)) {
            contactNum.add(str);
          }
        }
      }
          String serviceWorkerStr = serviceWorker.toString();
          serviceWorkerStr = serviceWorkerStr.substring(1, serviceWorkerStr.length() - 1);
          orderIndexDTO.setServiceWorker(serviceWorkerStr);

      String contactNumStr = contactNum.toString();
      contactNumStr = contactNumStr.substring(1,contactNumStr.length() - 1);
      orderIndexDTO.setContactNum(contactNumStr);
      orderIndexDTOList.add(orderIndexDTO);
    }
    return orderIndexDTOList;
  }


  @Override
  public List<OrderIndexDTO> getByOrderId(long shopId, Long orderId) throws Exception {
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    QueryResponse rsp = searchOrderService.queryOrderByServiceWorker(shopId, orderId);
    //QueryResponse rsp =queryOrderByServiceWorker(shopId,q,start,rows);
    SolrDocumentList documents = rsp.getResults();
    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    for (SolrDocument document : documents) {
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
      orderIndexDTO.setOrderId(Long.parseLong((String) document.getFirstValue("id")));
      orderIndexDTO.setShopId(Long.parseLong((String.valueOf(document.getFirstValue("shop_id")))));
      if (document.getFieldValue("customer_or_supplier_id") != null) {
        long customOrSupplierId = Long.parseLong(document.getFieldValue("customer_or_supplier_id").toString());
        orderIndexDTO.setCustomerOrSupplierId(customOrSupplierId);
      }

      String orderContent = "";
      List<Object> objectList = (List<Object>) document.getFieldValue("order_content");
      if (objectList != null && objectList.size() > 0) {
        orderContent = objectList.toString();
        if (orderContent.length() > 1) {
          orderContent = orderContent.substring(1, orderContent.length() - 1);
        }
        orderIndexDTO.setOrderContent(orderContent);
      }


      if (document.getFieldValue("order_type") != null) {
        String orderType = (String) document.getFieldValue("order_type");
        if (!StringUtil.isEmpty(orderType)) {
          if (judgeOrderType(orderType, OrderTypes.PURCHASE)) {
            orderIndexDTO.setOrderType(OrderTypes.PURCHASE);
            orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_ORDER_DONE);

          } else if (judgeOrderType(orderType, OrderTypes.INVENTORY)) {
            orderIndexDTO.setOrderType(OrderTypes.INVENTORY);
            orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_INVENTORY_DONE);
          } else if (judgeOrderType(orderType, OrderTypes.SALE)) {
            orderIndexDTO.setOrderType(OrderTypes.SALE);
            orderIndexDTO.setOrderStatus(OrderStatus.SALE_DONE);
            if (document.getFieldValue("order_status") != null) {
              String orderStatus = (String) document.getFieldValue("order_status");
              if (!StringUtil.isEmpty(orderStatus)) {
                if (judgeOrderStatus(orderStatus, OrderStatus.SALE_DONE, OrderTypes.SALE)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.SALE_DONE);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.SALE_REPEAL, OrderTypes.SALE)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.SALE_REPEAL);
                }
                else if (judgeOrderStatus(orderStatus, OrderStatus.SALE_DEBT_DONE, OrderTypes.SALE)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.SALE_DONE);
                }
              }
            }
            orderIndexDTO.setWashingStr("");
            orderIndexDTO.setServiceStr("");
            if (!StringUtil.isEmpty(orderIndexDTO.getOrderContent()) && orderIndexDTO.getOrderContent().length() > 5) {
              orderIndexDTO.setSalesStr(orderIndexDTO.getOrderContent().substring(5, orderIndexDTO.getOrderContent().length()));
            }

          } else if (judgeOrderType(orderType, OrderTypes.REPAIR)) {
            orderIndexDTO.setOrderType(OrderTypes.REPAIR);
            orderIndexDTO.setWashingStr("");
            int length = orderIndexDTO.getOrderContent().length();
            String orderContentStr = orderIndexDTO.getOrderContent();
            if (!StringUtil.isEmpty(orderContentStr) && orderContentStr.length() > 5) {
              int indexSale = orderContentStr.indexOf("销售内容:");
              int indexService = orderContent.indexOf("施工内容:");
              if (indexService != -1) {
                if (indexSale == -1) {
                  orderIndexDTO.setServiceStr(orderContentStr.substring(5, length));
                  orderIndexDTO.setSalesStr("");
                } else {
                  orderIndexDTO.setServiceStr(orderContentStr.substring(5, indexSale));
                  orderIndexDTO.setSalesStr(orderContentStr.substring(indexSale + 5, length));
                }
              } else {
                orderIndexDTO.setSalesStr(orderContentStr.substring(5, length));
                orderIndexDTO.setServiceStr("");
              }
            }


            if (document.getFieldValue("order_status") != null) {
              String orderStatus = (String) document.getFieldValue("order_status");
              if (!StringUtil.isEmpty(orderStatus)) {
                if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_DISPATCH, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_DISPATCH);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_DONE, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_DONE);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_SETTLED, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_SETTLED);
                } else if (judgeOrderStatus(orderStatus, OrderStatus.REPAIR_REPEAL, OrderTypes.REPAIR)) {
                  orderIndexDTO.setOrderStatus(OrderStatus.REPAIR_REPEAL);
                }
              }
            }
          } else if (judgeOrderType(orderType, OrderTypes.WASH)) {
            orderIndexDTO.setOrderType(OrderTypes.WASH);
            orderIndexDTO.setOrderStatus(OrderStatus.WASH_SETTLED);
            orderIndexDTO.setWashingStr(orderIndexDTO.getOrderContent());
            orderIndexDTO.setSalesStr("");
            orderIndexDTO.setServiceStr("");
          } else if (judgeOrderType(orderType, OrderTypes.WASH_BEAUTY)) {
            orderIndexDTO.setOrderType(OrderTypes.WASH_BEAUTY);
	          String orderStatus = (String) document.getFieldValue("order_status");
	          if (!StringUtil.isEmpty(orderStatus)) {
		          if (judgeOrderStatus(orderStatus, OrderStatus.WASH_SETTLED, OrderTypes.WASH_BEAUTY)) {
            orderIndexDTO.setOrderStatus(OrderStatus.WASH_SETTLED);
		          } else if (judgeOrderStatus(orderStatus, OrderStatus.WASH_REPEAL, OrderTypes.WASH_BEAUTY)) {
			          orderIndexDTO.setOrderStatus(OrderStatus.WASH_REPEAL);
		          }
	          }
            orderIndexDTO.setWashingStr(orderContent);
            orderIndexDTO.setSalesStr("");
            orderIndexDTO.setServiceStr("");
          } else if (judgeOrderType(orderType, OrderTypes.MEMBER_BUY_CARD)) {
            orderIndexDTO.setOrderType(OrderTypes.MEMBER_BUY_CARD);
            orderIndexDTO.setOrderStatus(OrderStatus.MEMBERCARD_ORDER_STATUS);
            orderIndexDTO.setWashingStr("");
            orderIndexDTO.setSalesStr("");
            orderIndexDTO.setServiceStr(orderContent);
          }
        }
      }


      orderIndexDTO.setOrderTotalAmount((Double) document.getFieldValue("order_total_amount"));


      if (document.getFieldValue("customer_or_supplier_name") != null) {
        String customerOrSupplierName = (String) document.getFieldValue("customer_or_supplier_name");
        orderIndexDTO.setCustomerOrSupplierName(customerOrSupplierName);
      }

      if (document.getFieldValue("vehicle") != null) {
        String vehicle = (String) document.getFieldValue("vehicle");
        if (!StringUtil.isEmpty(vehicle)) {
          vehicle = vehicle.substring(1, vehicle.length() - 1);
          orderIndexDTO.setVehicle(vehicle);
        }
      }
      if (document.getFieldValue("service_worker") != null) {
        List<Object> serviceWorkerList = (List<Object>) document.getFieldValue("service_worker");
        if (serviceWorkerList != null) {
          String serviceWorker = serviceWorkerList.toString();
          serviceWorker = serviceWorker.substring(1, serviceWorker.length() - 1);
          orderIndexDTO.setServiceWorker(serviceWorker);
        }
      }
      List<Object> contactNumList = null;
      if(document.getFieldValue("contact_num") != null) {
        contactNumList = (List<Object>) document.getFieldValue("contact_num");
      }
      List<String> contactNum = new ArrayList<String>();
      if(CollectionUtils.isNotEmpty(contactNumList)) {
        for(Object object : contactNumList) {
          String str = (String)object;
          if(!contactNum.contains(str)) {
            contactNum.add(str);
          }
        }
      }
      String contactNumStr = contactNum.toString();
      orderIndexDTO.setContactNum(contactNumStr);
      if (document.getFieldValue("payment_time") != null) {
        long paymentTime = NumberUtil.longValue((document.getFieldValue("payment_time")));
        if (paymentTime > 0) {
          orderIndexDTO.setPaymentTime(paymentTime);
        }
      }
      if (document.getFieldValue("arrears") != null) {
        double arrears = ((Double) (document.getFieldValue("arrears")));
        if (arrears > 0) {
          orderIndexDTO.setArrears(arrears);
        }
      }
      if (document.getFieldValue("created_time") != null) {
        orderIndexDTO.setCreationDate(NumberUtil.longValue(document.getFieldValue("created_time")));
      }
      orderIndexDTOList.add(orderIndexDTO);
    }
    return orderIndexDTOList;
  }

  @Override
  public int getOrderIndexSizeByServiceWorker(Long shopId, String assistantName, Long startTime, Long endTime) throws Exception {
    SolrQuery query = new SolrQuery();
    StringBuffer stringBuffer = new StringBuffer();


    if (startTime != null && endTime != null) {
      stringBuffer.append(" created_time:[").append(startTime).append(" TO ").append(endTime).append("]");
    }
    if(StringUtil.isEmpty(stringBuffer.toString())) {
    if (StringUtils.isBlank(assistantName)) {
        stringBuffer.append(" (service_worker:* OR sales_man:* )");
    } else {
        stringBuffer.append(" (service_worker:" + assistantName + " OR sales_man:" + assistantName + " ) ");
    }
    }else {
      if (StringUtils.isBlank(assistantName)) {
        stringBuffer.append(" AND (service_worker:* OR sales_man:* )");
      } else {
        stringBuffer.append(" AND (service_worker:" + assistantName + " OR sales_man:" + assistantName + " ) ");
    }
    }

    query.setQuery(stringBuffer.toString());

    query.setParam("q.op", "AND");
    StringBuffer fQueryString = new StringBuffer();
    //只查询销售。维修美容单，和洗车单
    fQueryString.append("(shop_id:").append(shopId.toString())
        .append(") AND (order_type:SALE OR order_type:REPAIR OR order_type:WASH OR order_type:WASH_BEAUTY OR order_type:MEMBER_BUY_CARD OR order_type:3 OR order_type:4 OR order_type:5)");
    fQueryString.append("AND(order_status:REPAIR_SETTLED OR order_status:WASH_SETTLED OR order_status:SALE_DONE OR order_status:MEMBERCARD_ORDER_STATUS OR order_status:SALE_DEBT_DONE OR order_status:SETTLED) ");
    String fqString = fQueryString.toString();
    query.setFilterQueries(fqString);
    query.setStart(0);
    query.setRows(10000);
    QueryResponse rsp = SolrClientHelper.getOrderSolrClient().query(query);
    SolrDocumentList documents = rsp.getResults();
    return documents == null ? 0 : documents.size();
  }


  @Override
  public OrderIndexDTO getOrderIndexByOrderId(Long shopId, Long orderId) throws Exception {
    if (orderId == null || shopId == null) throw new Exception("[shopId:" + shopId + ",orderId:" + orderId + " can't be null!");
    SearchWriter writer = searchDaoManager.getWriter();
    OrderIndex orderIndex = null;
    List<OrderIndex> orderIndexList = writer.getOrderIndexDTOByOrderId(shopId, orderId);
    if (CollectionUtils.isEmpty(orderIndexList)) {
      throw new Exception("orderIndex[orderId:" + orderId + "] can't be null!");
    } else if (orderIndexList.size() > 1) {
      throw new Exception("orderIndex[orderId:" + orderId + "] duplicated!");
    } else {
      orderIndex = orderIndexList.get(0);
    }
    OrderIndexDTO orderIndexDTO = orderIndex.toDTO();
    List<ItemIndexDTO> itemIndexDTOs = writer.getItemIndexDTOListByOrderId(shopId, orderId);
    if (CollectionUtils.isEmpty(itemIndexDTOs)) throw new Exception("[itemIndexDTOs[getByOrderId:" + orderId + "] can't be null!");
    orderIndexDTO.setItemIndexDTOList(itemIndexDTOs);
    return orderIndexDTO;
  }


  @Override
  public void saveOrderIndexAndItemIndexOfMemberCardOrder(MemberCardOrderDTO memberCardOrderDTO) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (null == memberCardOrderDTO || null == memberCardOrderDTO.getId()) return;
      OrderIndex orderIndex = new OrderIndex(memberCardOrderDTO);
      orderIndex.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
      orderIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
      writer.save(orderIndex);
      if (CollectionUtils.isNotEmpty(memberCardOrderDTO.getMemberCardOrderItemDTOs())) {
      for (MemberCardOrderItemDTO memberCardOrderItemDTO : memberCardOrderDTO.getMemberCardOrderItemDTOs()) {
          if (null == memberCardOrderItemDTO.getId()) continue;
        ItemIndex itemIndex = new ItemIndex();
        itemIndex.setOrderId(memberCardOrderDTO.getId());
        itemIndex.setOrderStatus(TxnConstant.OrderStatusInIntemIndex.ITEMINDEX_ORDERSTATUS_FINISH);
        itemIndex.setItemId(memberCardOrderItemDTO.getId());
        itemIndex.setShopId(memberCardOrderDTO.getShopId());
        itemIndex.setCustomerId(memberCardOrderDTO.getCustomerId());
        itemIndex.setCustomerOrSupplierName(memberCardOrderDTO.getCustomerName());
        itemIndex.setOrderTimeCreated(memberCardOrderDTO.getVestDate());
        itemIndex.setOrderType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);
        itemIndex.setItemType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);
        itemIndex.setItemName(memberCardOrderDTO.getMemberCardName());
          itemIndex.setItemCount(1d);
        itemIndex.setItemPrice(memberCardOrderDTO.getReceivableDTO().getTotal());
        itemIndex.setOrderTotalAmount(itemIndex.getItemPrice());
        itemIndex.setArrears(memberCardOrderDTO.getReceivableDTO().getDebt());
        itemIndex.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
        itemIndex.setItemTypeEnum(ItemTypes.SALE_MEMBER_CARD);
        itemIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
        if (null != itemIndex.getArrears() && itemIndex.getArrears() > 0) {
          itemIndex.setPaymentTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, memberCardOrderDTO.getRepayTime()));
        }
        itemIndex.setMemberCardId(memberCardOrderDTO.getId());
        writer.save(itemIndex);
      }
      }

      if (CollectionUtils.isNotEmpty(memberCardOrderDTO.getNewMemberCardOrderServiceDTOs())) {
      for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderDTO.getNewMemberCardOrderServiceDTOs()) {
        if (null != memberCardOrderServiceDTO.getId() && null != memberCardOrderServiceDTO.getServiceId()) {
          ItemIndex itemIndex = new ItemIndex();
          itemIndex.setOrderId(memberCardOrderDTO.getId());
          itemIndex.setItemId(memberCardOrderServiceDTO.getId());
          itemIndex.setShopId(memberCardOrderDTO.getShopId());
          itemIndex.setCustomerId(memberCardOrderDTO.getCustomerId());
          itemIndex.setCustomerOrSupplierName(memberCardOrderDTO.getCustomerName());
          itemIndex.setOrderTimeCreated(memberCardOrderDTO.getVestDate());
          itemIndex.setOrderType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);
          itemIndex.setItemType(TxnConstant.ITEM_TYPE_MEMBER_CARD_ORDER_SERVICE);
          itemIndex.setItemName(memberCardOrderServiceDTO.getServiceName());
          itemIndex.setServiceId(memberCardOrderServiceDTO.getServiceId());
          itemIndex.setIncreasedTimes(memberCardOrderServiceDTO.getIncreasedTimes());
          itemIndex.setIncreasedTimesLimitType(memberCardOrderServiceDTO.getIncreasedTimesLimitType());
          itemIndex.setVehicles(memberCardOrderServiceDTO.getVehicles());
          itemIndex.setDeadline(memberCardOrderServiceDTO.getDeadline());
          itemIndex.setCardTimes(memberCardOrderServiceDTO.getCardTimes());
          itemIndex.setCardTimesLimitType(memberCardOrderServiceDTO.getCardTimesLimitType());
          itemIndex.setOldTimes(memberCardOrderServiceDTO.getOldTimes());
          itemIndex.setOldTimesLimitType(memberCardOrderServiceDTO.getOldTimesLimitType());
          itemIndex.setBalanceTimes(memberCardOrderServiceDTO.getBalanceTimes());
          itemIndex.setBalanceTimesLimitType(memberCardOrderServiceDTO.getBalanceTimesLimitType());
          itemIndex.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
          itemIndex.setItemTypeEnum(ItemTypes.SALE_MEMBER_CARD_SERVICE);
          itemIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
          writer.save(itemIndex);
        }
      }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private IItemIndexService itemIndexService;

  public IItemIndexService getItemIndexService() {
    if (itemIndexService == null) {
      itemIndexService = ServiceManager.getService(IItemIndexService.class);
    }
    return itemIndexService;
  }


  @Override
  public Long getRepairOrderIndexMaxId(Long shopId, long startId, int pageSize) throws Exception {
    if (pageSize < 1) throw new Exception("pageSize should greater than 0");
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getRepairOrderIndexMaxId(shopId, startId, pageSize);
  }

  @Override
  public void saveOrderIndexAndItemIndexOfMemberCardReturn(MemberCardReturnDTO memberCardReturnDTO) throws Exception{
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    OrderIndex orderIndex = null;
    try {
      if (null == memberCardReturnDTO || null == memberCardReturnDTO.getId()) {
        return;
      }
      orderIndex = new OrderIndex(memberCardReturnDTO);
      writer.save(orderIndex);
      if (CollectionUtils.isNotEmpty(memberCardReturnDTO.getMemberCardReturnItemDTOs())) {
        for (MemberCardReturnItemDTO memberCardReturnItemDTO : memberCardReturnDTO.getMemberCardReturnItemDTOs()) {
          if (null == memberCardReturnItemDTO.getId()) {
            continue;
          }
          ItemIndex itemIndex = new ItemIndex();
          itemIndex.setOrderId(memberCardReturnDTO.getId());
          itemIndex.setOrderStatus(TxnConstant.OrderStatusInIntemIndex.ITEMINDEX_ORDERSTATUS_FINISH);
          itemIndex.setItemId(memberCardReturnItemDTO.getId());
          itemIndex.setShopId(memberCardReturnItemDTO.getShopId());
          itemIndex.setCustomerId(memberCardReturnDTO.getCustomerId());
          itemIndex.setCustomerOrSupplierName(memberCardReturnDTO.getCustomerName());
          itemIndex.setOrderTimeCreated(memberCardReturnDTO.getReturnDate());
          itemIndex.setItemName(memberCardReturnDTO.getMemberCardName());
          itemIndex.setItemCount(1d);
          itemIndex.setItemPrice(memberCardReturnDTO.getReceptionRecordDTO().getAmount());
          itemIndex.setOrderTotalAmount(itemIndex.getItemPrice());
          itemIndex.setOrderTypeEnum(OrderTypes.MEMBER_RETURN_CARD);
          itemIndex.setItemTypeEnum(ItemTypes.RETURN_MEMBER_CARD);
          itemIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
          itemIndex.setMemberCardId(memberCardReturnDTO.getId());
          writer.save(itemIndex);
        }
      }

      if (CollectionUtils.isNotEmpty(memberCardReturnDTO.getMemberCardReturnServiceDTOs())) {
        for (MemberCardReturnServiceDTO memberCardReturnServiceDTO : memberCardReturnDTO.getMemberCardReturnServiceDTOs()) {
          if (null != memberCardReturnServiceDTO.getId() && null != memberCardReturnServiceDTO.getServiceId()) {
            ItemIndex itemIndex = new ItemIndex();
            itemIndex.setOrderId(memberCardReturnDTO.getId());
            itemIndex.setItemId(memberCardReturnServiceDTO.getId());
            itemIndex.setItemCount(1d);
            itemIndex.setShopId(memberCardReturnDTO.getShopId());
            itemIndex.setCustomerId(memberCardReturnDTO.getCustomerId());
            itemIndex.setCustomerOrSupplierName(memberCardReturnDTO.getCustomerName());
            itemIndex.setOrderTimeCreated(memberCardReturnDTO.getReturnDate());
            itemIndex.setItemName(memberCardReturnServiceDTO.getServiceName());
            itemIndex.setServiceId(memberCardReturnServiceDTO.getServiceId());
            itemIndex.setBalanceTimes(memberCardReturnServiceDTO.getRemainTimes());   //退卡时剩余次数
            itemIndex.setItemPrice(memberCardReturnDTO.getReceptionRecordDTO().getAmount());
            itemIndex.setOrderTotalAmount(itemIndex.getItemPrice());
            itemIndex.setOrderTypeEnum(OrderTypes.MEMBER_RETURN_CARD);
            itemIndex.setItemTypeEnum(ItemTypes.RETURN_MEMBER_CARD_SERVICE);
            itemIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
            itemIndex.setOldTimes(memberCardReturnServiceDTO.getLastBuyTimes());    //上次购买次数
            writer.save(itemIndex);
          }
        }
      }

      writer.commit(status);
    } catch(Exception e){
      LOG.error("OrderIndexService.saveOrderIndexAndItemIndexOfMemberCardReturn出错。", e);
      LOG.error("memberCardReturnDTO:{}", memberCardReturnDTO.toString());
      throw new Exception(e);
    } finally{
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateOrderIndex(OrderIndexDTO orderIndexDTO) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    if (orderIndexDTO == null) {
      return;
    }
    Object status = writer.begin();
    try {
      OrderIndex orderIndex = null;
      if (orderIndexDTO.getId() != null) {
        orderIndex = writer.getById(OrderIndex.class, orderIndexDTO.getId());
      } else if (orderIndexDTO.getShopId() != null && orderIndexDTO.getOrderId() != null) {
        List<OrderIndex> orderIndexList = writer.getOrderIndexDTOByOrderId(orderIndexDTO.getShopId(), orderIndexDTO.getOrderId());
        if (CollectionUtils.isNotEmpty(orderIndexList)) {
          orderIndex = orderIndexList.get(0);
        }
      }
      if (orderIndex == null) {
        orderIndex = new OrderIndex();
      }
      orderIndex.fromDTO(orderIndexDTO, false);

      writer.saveOrUpdate(orderIndex);
      orderIndexDTO.setCreationDate(orderIndex.getCreationDate());

      List<ItemIndex> itemIndexs = writer.getItemIndexDTOByOrderId(orderIndexDTO.getShopId(),orderIndexDTO.getOrderId());
      Map<Long,ItemIndex> itemIndexMap = new HashMap<Long,ItemIndex>();
      for(ItemIndex itemIndex : itemIndexs){
        itemIndexMap.put(itemIndex.getId(),itemIndex);
      }
      if (CollectionUtils.isNotEmpty(orderIndexDTO.getItemIndexDTOList())) {
        for (ItemIndexDTO itemIndexDTO : orderIndexDTO.getItemIndexDTOList()) {
          ItemIndex itemIndex = null;
          if (itemIndexDTO.getId() != null) {
            itemIndex = writer.getById(ItemIndex.class, itemIndexDTO.getId());
          } else if (itemIndexDTO.getShopId() != null && orderIndexDTO.getOrderId() != null && itemIndexDTO.getItemId() != null) {
            List<ItemIndex> itemIndexList = writer.getItemIndexDTOByOrderItemId(orderIndexDTO.getShopId(), orderIndexDTO.getOrderId(), itemIndexDTO.getItemId());
            if (CollectionUtils.isNotEmpty(itemIndexList)) {
              itemIndex = itemIndexList.get(0);
            }
          }
          if (itemIndex == null) {
            itemIndex = new ItemIndex();
          }
          itemIndex = itemIndex.fromDTO(itemIndexDTO, false);
          itemIndexMap.remove(itemIndex.getId());
          writer.saveOrUpdate(itemIndex);
        }
      }
      if(MapUtils.isNotEmpty(itemIndexMap)){
        for(ItemIndex itemIndex : itemIndexMap.values()){
          writer.delete(itemIndex);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}