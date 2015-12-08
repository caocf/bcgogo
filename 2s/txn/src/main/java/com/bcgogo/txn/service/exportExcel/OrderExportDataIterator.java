package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-8
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public class OrderExportDataIterator extends BcgogoExportDataIterator {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryExportDataIterator.class);
    private static final int PAGE_SIZE = 1000;
    private OrderSearchConditionDTO orderSearchConditionDTO;

    public OrderExportDataIterator(OrderSearchConditionDTO orderSearchConditionDTO) throws PageException {
        super(orderSearchConditionDTO.getTotalExportNum(), PAGE_SIZE, Integer.valueOf(ServiceManager.getService(IConfigService.class).getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID)));
        this.orderSearchConditionDTO = orderSearchConditionDTO;
    }

    @Override
    protected int getTotalRows() {
        if(orderSearchConditionDTO == null) {
            return 0;
        }
        return orderSearchConditionDTO.getTotalExportNum();
    }

    @Override
    protected List<String> getHead() {
        List<String> head = new ArrayList<String>();
        head.addAll(ExportOrderConstant.fieldList);
        if(orderSearchConditionDTO.getWholesaler()) {
           head.remove(ExportOrderConstant.VEHICLE_NO);
        }
        return head;
    }

    @Override
    public Object next() {
        //取下一页数据
        getPage().gotoNextPage();
        //生成要导出的数据
        List<List<String>> rows = assembleOrderInfo(getOrderSearchResult());
        return rows;
    }

  @Override
  protected List<String> getHeadShowInfo() {
    List<String> headShowInfo = new ArrayList<String>();
    headShowInfo.add(StringUtils.trim(orderSearchConditionDTO.getStatisticsInfo()));
    return headShowInfo;
  }

  @Override
  protected List<String> getTailShowInfo() {
    return null;
  }

  private List<OrderSearchResultDTO> getOrderSearchResult() {
        OrderSearchResultListDTO orderSearchResultListDTO = null;
        orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS_FACET});
        if (ArrayUtils.isEmpty(orderSearchConditionDTO.getOrderType())) {
            orderSearchConditionDTO.setOrderType(OrderTypes.getInquiryCenterOrderTypes());
        }
        if(StringUtils.isBlank(orderSearchConditionDTO.getSort())) {
            orderSearchConditionDTO.setSort("created_time desc");
        }
        orderSearchConditionDTO.setPageRows(getPage().getPageSize());
        orderSearchConditionDTO.setRowStart(getPage().getRowStart());
        orderSearchConditionDTO.setStatsFields(new String[]{OrderSearchResultListDTO.ORDER_TOTAL_AMOUNT, OrderSearchResultListDTO.ORDER_DEBT_AMOUNT, OrderSearchResultListDTO.ORDER_SETTLED_AMOUNT});
        orderSearchConditionDTO.setFacetFields(new String[]{"order_type"});
        try {
            orderSearchResultListDTO = ServiceManager.getService(ISearchOrderService.class).queryOrders(orderSearchConditionDTO);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        if(orderSearchResultListDTO == null || CollectionUtil.isEmpty(orderSearchResultListDTO.getOrders())) {
            return null;
        }
        return  orderSearchResultListDTO.getOrders();
    }

    private List<List<String>> assembleOrderInfo(List<OrderSearchResultDTO> orderSearchResultDTOs) {
        if(CollectionUtil.isEmpty(orderSearchResultDTOs)) {
            return null;
        }
        List<List<String>> rows = new ArrayList<List<String>>();
        for(OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOs) {
            List<String> row = new ArrayList<String>();
            row.add(orderSearchResultDTO.getReceiptNo() == null ? "" : orderSearchResultDTO.getReceiptNo());
            row.add(orderSearchResultDTO.getCustomerOrSupplierName());
            row.add(orderSearchResultDTO.getVestDateStr() == null ? "" : orderSearchResultDTO.getVestDateStr());
            if(!orderSearchConditionDTO.getWholesaler()) {
                row.add(orderSearchResultDTO.getVehicle() == null ? "" : orderSearchResultDTO.getVehicle());
            }
            row.add(orderSearchResultDTO.getOrderTypeValue() == null ? "" : orderSearchResultDTO.getOrderTypeValue());
            row.add(orderSearchResultDTO.getOrderContent() == null ? "" : orderSearchResultDTO.getOrderContent());
            row.add(orderSearchResultDTO.getAmount() == null ? "0.0" :  Math.abs(NumberUtil.doubleVal(orderSearchResultDTO.getAmount())) + "");
            //实收实付
            if(ArrayUtils.contains(ExportOrderConstant.PayableOrderTypes, orderSearchResultDTO.getOrderType())) {
                row.add("");
                row.add(orderSearchResultDTO.getSettled() == null ? "0.0" : Math.abs(NumberUtil.doubleVal(orderSearchResultDTO.getSettled())) + "");
            } else {
                row.add(orderSearchResultDTO.getSettled() == null ? "0.0" : Math.abs(NumberUtil.doubleVal(orderSearchResultDTO.getSettled())) + "");
                row.add("");
            }

            row.add(orderSearchResultDTO.getDebt() == null ? "0.0" : Math.abs(NumberUtil.doubleVal(orderSearchResultDTO.getDebt())) + "");
            row.add(orderSearchResultDTO.getDiscount() == null ? "0.0" : Math.abs(NumberUtil.doubleVal(orderSearchResultDTO.getDiscount())) + "");
            String orderStatusValue = orderSearchResultDTO.getOrderStatusValue();
            if("已入库".equals(orderSearchResultDTO.getOrderStatusValue()) && orderSearchResultDTO.getDebt() > 0) {
                orderStatusValue = "欠款入库";
            }  else if("已结算".equals(orderSearchResultDTO.getOrderStatusValue()) && orderSearchResultDTO.getDebt() > 0) {
                orderStatusValue = "欠款结算";
            }
            if(!ArrayUtils.isEmpty(orderSearchResultDTO.getPayMethod()) && ArrayUtils.contains(orderSearchResultDTO.getPayMethod(),"STATEMENT_ACCOUNT")) {
                orderStatusValue = "已对账";
            }
            row.add(orderStatusValue == null ? "" : orderStatusValue);
            rows.add(row);
        }
        return rows;
    }
}
