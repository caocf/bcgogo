package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.dto.JoinSearchConditionDTO;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-8
 * Time: 上午9:20
 * To change this template use File | Settings | File Templates.
 */
public class CustomerExportDataIterator  extends BcgogoExportDataIterator{
    private static final Logger LOG = LoggerFactory.getLogger(InventoryExportDataIterator.class);
    private static final int PAGE_SIZE = 1000;

    private CustomerSupplierSearchConditionDTO searchConditionDTO;
    private JoinSearchConditionDTO joinSearchConditionDTO;

    public CustomerExportDataIterator(CustomerSupplierSearchConditionDTO searchConditionDTO, JoinSearchConditionDTO joinSearchConditionDTO) throws PageException {
        super(searchConditionDTO.getTotalExportNum(), PAGE_SIZE, Integer.valueOf(ServiceManager.getService(IConfigService.class).getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID)));
        this.searchConditionDTO = searchConditionDTO;
        this.joinSearchConditionDTO = joinSearchConditionDTO;
    }

    @Override
    protected int getTotalRows() {
        if(searchConditionDTO == null) {
            return 0;
        }
       return searchConditionDTO.getTotalExportNum();
    }

    @Override
    protected List<String> getHead() {
        return ExportCustomerConstant.fieldList;
    }

    @Override
    public Object next() {
        //取下一页数据
        getPage().gotoNextPage();
        //生成要导出的数据
        List<List<String>> rows = assembleCustomerMemberInfo(getCustomerSearchResult());
        return rows;
    }

  @Override
  protected List<String> getHeadShowInfo() {
    List<String> headShowInfo = new ArrayList<String>();
    headShowInfo.add("共" + getTotalRows() + "名会员");
    return headShowInfo;
  }

  @Override
  protected List<String> getTailShowInfo() {
    return null;
  }

  private List<CustomerSupplierSearchResultDTO> getCustomerSearchResult() {
        CustomerSupplierSearchResultListDTO searchResultListDTO = null;
        searchConditionDTO.setStart(getPage().getRowStart());
        searchConditionDTO.setRows(getPage().getPageSize());
        ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
        //排序
        if (StringUtils.isBlank(searchConditionDTO.getSearchWord()) && StringUtils.isBlank(searchConditionDTO.getSort())) {
            searchConditionDTO.setSort("created_time desc");
        }
        if (!joinSearchConditionDTO.isEmptyOfProductInfo()) {
            joinSearchConditionDTO.setShopId(searchConditionDTO.getShopId());
            joinSearchConditionDTO.setFromColumn("customer_or_supplier_id");
            joinSearchConditionDTO.setToColumn("id");
            joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.ORDER_ITEM_CORE.getValue());
            joinSearchConditionDTO.setItemTypes(ItemTypes.MATERIAL);
            joinSearchConditionDTO.setOrderTypes(new String[]{OrderTypes.SALE.toString(), OrderTypes.SALE_RETURN.toString(), OrderTypes.REPAIR.toString()});
            joinSearchConditionDTO.setOrderStatus(new String[]{OrderStatus.SALE_DONE.toString(), OrderStatus.SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString()});
            searchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
        }
        try {
            searchResultListDTO = searchService.queryCustomerWithUnknownField(searchConditionDTO);
            if(searchResultListDTO == null || CollectionUtil.isEmpty(searchResultListDTO.getCustomerSuppliers())) {
                return null;
            }
            for (CustomerSupplierSearchResultDTO resultDTO : searchResultListDTO.getCustomerSuppliers()) {
                MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(resultDTO.getShopId(), resultDTO.getId());
                resultDTO.setMemberDTO(memberDTO);
            }

        }  catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        return  searchResultListDTO.getCustomerSuppliers();
    }

    private List<List<String>> assembleCustomerMemberInfo(List<CustomerSupplierSearchResultDTO> customerSearchResultDTOs) {
         if(CollectionUtil.isEmpty(customerSearchResultDTOs)) {
             return null;
         }
         List<List<String>> rows = new ArrayList<List<String>>();
        for(CustomerSupplierSearchResultDTO customerSearchResultDTO : customerSearchResultDTOs) {
            List<String> row = new ArrayList<String>();
            row.add(customerSearchResultDTO.getName());
            row.add(customerSearchResultDTO.getContact());
            row.add(customerSearchResultDTO.getMobile());
            row.add(customerSearchResultDTO.getMemberNo());
            row.add(customerSearchResultDTO.getMemberType());
            row.add(customerSearchResultDTO.getMemberDTO() == null ? "0.0" : (customerSearchResultDTO.getMemberDTO().getBalance() == null ? "0.0" : customerSearchResultDTO.getMemberDTO().getBalance().toString()));
            row.add(customerSearchResultDTO.getTotalAmount() == null ? "0.0" : customerSearchResultDTO.getTotalAmount().toString());
            rows.add(row);
        }
        return rows;
    }
}
