package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-8
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
public class CustomerTransactionExportDataIterator extends BcgogoExportDataIterator {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryExportDataIterator.class);
    private static final int PAGE_SIZE = 1000;
    private OrderSearchConditionDTO orderSearchConditionDTO;

    public CustomerTransactionExportDataIterator(OrderSearchConditionDTO orderSearchConditionDTO) throws PageException {
        super(orderSearchConditionDTO.getTotalExportNum(),PAGE_SIZE,Integer.valueOf(ServiceManager.getService(IConfigService.class).getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID)));
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
        head.addAll(CustomerTransactionConstant.fieldList);
        if(!orderSearchConditionDTO.getMemberStoredValuePermission()) {
            head.remove(CustomerTransactionConstant.MEMBER_NAME);
        }
        if(!orderSearchConditionDTO.getVehicleConstructionPermission()) {
            head.remove(CustomerTransactionConstant.CONSUME_VEHICLE);
        }
        return head;
    }

    @Override
    public Object next() {
        //取下一页数据
        getPage().gotoNextPage();
        //生成要导出的数据
        List<List<String>> rows = assembleCustomerTransactionInfo(getOrderSearchResult());
        return rows;
    }

  @Override
  protected List<String> getHeadShowInfo() {
    List<String> headShowInfo = new ArrayList<String>();
    headShowInfo.add(orderSearchConditionDTO.getStatisticsInfo());
    return headShowInfo;
  }

  @Override
  protected List<String> getTailShowInfo() {
    return null;
  }

  private List<OrderSearchResultDTO> getOrderSearchResult() {
        OrderSearchResultListDTO orderSearchResultListDTO = null;
        try {
            ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
            if (orderSearchConditionDTO == null) throw new Exception("OrderSearchConditionDTO can't be null.");
            if (StringUtils.isBlank(orderSearchConditionDTO.getStatType())) throw new Exception("OrderSearchConditionDTO StatType can't be null.");
            //校验时间
            orderSearchConditionDTO.setRowStart(getPage().getRowStart());
            orderSearchConditionDTO.setPageRows(getPage().getPageSize());
            List<Object> result = new ArrayList<Object>();
            generateCustomerConditions(orderSearchConditionDTO);
            orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS});
            orderSearchConditionDTO.setOrderType(new String[]{"WASH_BEAUTY", "SALE", "REPAIR"});    // 单据类型
            orderSearchConditionDTO.setOrderStatus(new String[]{"WASH_SETTLED", "SALE_DONE", "SALE_DEBT_DONE", "REPAIR_SETTLED"});
            orderSearchConditionDTO.setStatsFields(new String[]{"after_member_discount_total", "order_settled_amount", "order_debt_amount", "discount"});
            orderSearchConditionDTO.setPageStatsFields(new String[]{"after_member_discount_total", "order_settled_amount", "order_debt_amount", "total_cost_price", "discount", "gross_profit"});
            orderSearchConditionDTO.setSort("created_time desc");
            orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        if(orderSearchResultListDTO == null || CollectionUtil.isEmpty(orderSearchResultListDTO.getOrders())) {
            return null;
        }
        return orderSearchResultListDTO.getOrders();
    }

    private void generateCustomerConditions(OrderSearchConditionDTO orderSearchConditionDTO) {
        orderSearchConditionDTO.setCustomerId(null);
        ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
        if (StringUtils.isNotBlank(orderSearchConditionDTO.getCustomerId())) {
            orderSearchConditionDTO.setCustomerOrSupplierIds(new String[]{orderSearchConditionDTO.getCustomerId()});
            orderSearchConditionDTO.setCustomerOrSupplierName(null);
        } else if (StringUtils.isNotBlank(orderSearchConditionDTO.getCustomerName()) || StringUtils.isNotBlank(orderSearchConditionDTO.getMobile())) {
            List<Customer> customerDTOList = null;
            customerDTOList = customerService.getAllCustomerByNameAndMobile(orderSearchConditionDTO.getShopId(),orderSearchConditionDTO.getCustomerName(),orderSearchConditionDTO.getMobile());
            Set<String> customerIdSet = new HashSet<String>();
            if (CollectionUtils.isNotEmpty(customerDTOList)) {
                for (Customer customerDTO : customerDTOList) {
                    customerIdSet.add(customerDTO.getId().toString());
                }
            } else {
                customerIdSet.add("-1");
            }
            orderSearchConditionDTO.setCustomerOrSupplierIds(customerIdSet.toArray(new String[customerIdSet.size()]));
            orderSearchConditionDTO.setCustomerOrSupplierName(null);
        }
    }

    private List<List<String>> assembleCustomerTransactionInfo(List<OrderSearchResultDTO> orderSearchResultDTOs) {
        if(CollectionUtil.isEmpty(orderSearchResultDTOs)) {
            return null;
        }
        List<List<String>> rows = new ArrayList<List<String>>();
        for(OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOs) {
            List<String> row = new ArrayList<String>();
            row.add(orderSearchResultDTO.getVestDateStr() == null ? "" : orderSearchResultDTO.getVestDateStr());
            row.add(orderSearchResultDTO.getCustomerOrSupplierName());
            if(orderSearchConditionDTO.getMemberStoredValuePermission()) {
                row.add(orderSearchResultDTO.getMemberNo());
            }
            row.add(orderSearchResultDTO.getContactNum() == null ? "" : orderSearchResultDTO.getContactNum());
            if(orderSearchConditionDTO.getVehicleConstructionPermission()) {
                row.add(orderSearchResultDTO.getVehicle() == null ? "" : orderSearchResultDTO.getVehicle());
            }
            row.add(orderSearchResultDTO.getOrderTypeValue() == null ? "" : orderSearchResultDTO.getOrderTypeValue());
            row.add(orderSearchResultDTO.getReceiptNo() == null ? "" : orderSearchResultDTO.getReceiptNo());
            row.add(orderSearchResultDTO.getAfterMemberDiscountTotal() == null ? "0.0" : orderSearchResultDTO.getAmount().toString());
            row.add(orderSearchResultDTO.getTotalCostPrice() == null ? "" : orderSearchResultDTO.getTotalCostPrice().toString());
            row.add(orderSearchResultDTO.getSettled() == null ? "0.0" : orderSearchResultDTO.getSettled().toString());

            row.add(orderSearchResultDTO.getDiscount() == null ? "0.0" : orderSearchResultDTO.getDiscount().toString());
            row.add(orderSearchResultDTO.getDebt() == null ? "0.0" : orderSearchResultDTO.getDebt().toString());
            row.add(orderSearchResultDTO.getGrossProfit() == null ? "0.0" : orderSearchResultDTO.getGrossProfit().toString());
            row.add(orderSearchResultDTO.getGrossProfitRate() == null ? "0%" : orderSearchResultDTO.getGrossProfitRate().toString() + "%");
            rows.add(row);
        }
        return rows;
    }
}
