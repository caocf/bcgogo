package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.exception.PageException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.StoreHouseDTO;
import com.bcgogo.txn.dto.StoreHouseInventoryDTO;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-7-29
 * Time: 上午11:20
 * To change this template use File | Settings | File Templates.
 */

public class InventoryExportDataIterator extends BcgogoExportDataIterator {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryExportDataIterator.class);
    private static final int PAGE_SIZE = 1000;

    private SearchConditionDTO searchConditionDTO;
    private List<StoreHouseDTO> storeHouseDTOs;

    public InventoryExportDataIterator(SearchConditionDTO searchConditionDTO) throws PageException {
        super(searchConditionDTO.getTotalRows(), PAGE_SIZE, Integer.valueOf(ServiceManager.getService(IConfigService.class).getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID)));
        this.searchConditionDTO = searchConditionDTO;
    }

    @Override
    protected int getTotalRows() {
        if(searchConditionDTO == null) {
            return 0;
        }
       return searchConditionDTO.getTotalRows();
}

    @Override
    protected List<String> getHead() {
        List<String> head = new ArrayList<String>();
        head.addAll(ExportInventoryConstant.fieldList);
        try {
            //多仓库版本，显示仓库信息，否则不需要显示
            List<StoreHouseDTO>  storeHouseDTOList = ServiceManager.getService(IStoreHouseService.class).getAllStoreHousesByShopId(searchConditionDTO.getShopId());
            if(CollectionUtils.isEmpty(storeHouseDTOList)) {
                head.add(ExportInventoryConstant.STORAGE_BIN);
            } else {
                if(searchConditionDTO.getStorehouseId() != null) {
                    head.add(ExportInventoryConstant.STORE_HOUSE_NAME);
                    head.add(ExportInventoryConstant.STORAGE_BIN);
                } else {
                    for(int i = 1 ; i <= storeHouseDTOList.size(); i++) {
                        head.add(ExportInventoryConstant.STORE_HOUSE + i);
                        head.add("货位");
                        head.add("分库库存");
                    }
                }

            }
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }

        return head;
    }


  @Override
    public Object next() {
        //取下一页数据
        getPage().gotoNextPage();
        //生成要导出的数据
        List<List<String>> rows = assembleProductsInfo(getInventoryProducts());
        return rows;
    }

  @Override
  protected List<String> getHeadShowInfo() {
    List<String> headShowInfo = new ArrayList<String>();
    headShowInfo.add("共有" + getTotalRows() + "种");
    return headShowInfo;
  }

  @Override
  protected List<String> getTailShowInfo() {
    return null;
  }

    /**
     * 取数据
     * @return
     */
    private  List<ProductDTO> getInventoryProducts() {
        ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
        if (StringUtils.isBlank(searchConditionDTO.getSort()) && searchConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
            searchConditionDTO.setSort("inventory_amount desc,storage_time desc");
        } else {
          String generateSort = TxnConstant.sortCommandMap.get(searchConditionDTO.getSort());
          if(StringUtils.isNotBlank(generateSort)){
            searchConditionDTO.setSort(generateSort);
          }
//            searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSort()));
        }
        searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_STATS});
        if(searchConditionDTO.getStorehouseId()!=null){
            searchConditionDTO.setStatsFields(new String[]{searchConditionDTO.getStorehouseId()+"_storehouse_inventory_amount", searchConditionDTO.getStorehouseId()+"_storehouse_inventory_price"});
        }else{
            searchConditionDTO.setStatsFields(new String[]{"inventory_amount", "inventory_price"});
        }
        searchConditionDTO.setRows(getPage().getPageSize());
        searchConditionDTO.setStart(getPage().getRowStart());
        ProductSearchResultListDTO productSearchResultListDTO = null;
        try {
            //不知道field的情况下
            productSearchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        if(productSearchResultListDTO == null) return null;
        return productSearchResultListDTO.getProducts();
    }

    /**
     * 生成数据
     * @param productDTOs
     * @return
     */
    private  List<List<String>> assembleProductsInfo(List<ProductDTO> productDTOs) {
        if(CollectionUtils.isEmpty(productDTOs)) {
           return null;
        }
        List<List<String>> rows = new ArrayList<List<String>>();
        List<StoreHouseDTO> storeHouseDTOList = null;
        try {
            storeHouseDTOList = getStoreHouseDTOs();
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }

        for(ProductDTO productDTO : productDTOs) {
            //多仓库版本，补全仓库信息
            if(CollectionUtils.isNotEmpty(storeHouseDTOList)){
              if(productDTO.getStoreHouseInventoryDTOMap() != null && productDTO.getStoreHouseInventoryDTOMap().size() > 0){
                for(StoreHouseDTO storeHouseDTO : storeHouseDTOList){
                    if(!productDTO.getStoreHouseInventoryDTOMap().containsKey(storeHouseDTO.getId())){
                        StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(storeHouseDTO.getId(),productDTO.getProductLocalInfoId(),0d);
                        storeHouseInventoryDTO.setStoreHouseName(storeHouseDTO.getName());
                        productDTO.getStoreHouseInventoryDTOMap().put(storeHouseDTO.getId(),storeHouseInventoryDTO);
                    }else {
                        StoreHouseInventoryDTO storeHouseInventoryDTO = productDTO.getStoreHouseInventoryDTOMap().get(storeHouseDTO.getId());
                        storeHouseInventoryDTO.setStoreHouseName(storeHouseDTO.getName());
                    }
                }
              }else{
                Map<Long, StoreHouseInventoryDTO> inventoryMap = new HashMap<Long, StoreHouseInventoryDTO>();
                productDTO.setStoreHouseInventoryDTOMap(inventoryMap);
                for(StoreHouseDTO storeHouseDTO : storeHouseDTOList){
                  StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(storeHouseDTO.getId(),productDTO.getProductLocalInfoId(),0d);
                  storeHouseInventoryDTO.setStoreHouseName(storeHouseDTO.getName());
                  inventoryMap.put(storeHouseDTO.getId(),storeHouseInventoryDTO);
                }
              }
            }
            List<String> row = new ArrayList<String>();
            row.add(productDTO.getCommodityCode() == null ? "" : productDTO.getCommodityCode());
            row.add(productDTO.getName() == null ? "" : productDTO.getName());
            row.add(productDTO.getBrand() == null ? "" : productDTO.getBrand());
            row.add(productDTO.getSpec() == null ? "" : productDTO.getSpec());
            row.add(productDTO.getModel() == null ? "" : productDTO.getModel());
            row.add(productDTO.getProductVehicleBrand() == null ? "" : productDTO.getProductVehicleBrand());
            row.add(productDTO.getProductVehicleModel() == null ? "" : productDTO.getProductVehicleModel());
            row.add(productDTO.getKindName() == null ? "" : productDTO.getKindName());
            String inventoryNumStr = "";
            //如果查询条件有仓库，则库存量带该仓库的，否则为总库存量
            if(searchConditionDTO.getStorehouseId() != null) {
                Double inventoryNum = productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId()).getAmount();
                inventoryNumStr = inventoryNum == null ? "0.0" : inventoryNum.toString();
            } else {
                inventoryNumStr = productDTO.getInventoryNum() == null ? "0.0" : productDTO.getInventoryNum().toString();
            }

            String sellUnit = productDTO.getSellUnit() == null ? "" :productDTO.getSellUnit();
            row.add(inventoryNumStr);
            row.add(sellUnit);
            //多仓库版本
            if(productDTO.getStoreHouseInventoryDTOMap() != null && productDTO.getStoreHouseInventoryDTOMap().size() > 0) {
                //查询条件有仓库，则只显示该仓库信息，否则全部仓库信息都要导出
                if(searchConditionDTO.getStorehouseId() != null) {
                    row.add(productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId()).getStoreHouseName());
                    String storageBin = productDTO.getStoreHouseInventoryDTOMap().get(searchConditionDTO.getStorehouseId()).getStorageBin();
                    storageBin =  storageBin == null ? "" : storageBin;
                    row.add(storageBin);
                }  else {
                    for(Long storeHouseId : productDTO.getStoreHouseInventoryDTOMap().keySet()) {
                        StoreHouseInventoryDTO storeHouseInventoryDTO = productDTO.getStoreHouseInventoryDTOMap().get(storeHouseId);
                        String storeHouseName = storeHouseInventoryDTO.getStoreHouseName();
                        String storageBin = storeHouseInventoryDTO.getStorageBin() == null ? "" : storeHouseInventoryDTO.getStorageBin();
                        String singleInventoryNum = storeHouseInventoryDTO.getAmount() == null ? "0.0" : storeHouseInventoryDTO.getAmount().toString();
                        row.add(storeHouseName);
                        row.add(storageBin);
                        row.add(singleInventoryNum);
                    }
                }
            } else {
                row.add(productDTO.getStorageBin() == null ? "" : productDTO.getStorageBin());
            }
            rows.add(row);
        }
        return rows;
    }

    public void setSearchCondition(SearchConditionDTO searchCondition) {
        searchConditionDTO = searchCondition;
    }

    public List<StoreHouseDTO> getStoreHouseDTOs() throws Exception{
        if(storeHouseDTOs == null){
            storeHouseDTOs = ServiceManager.getService(IStoreHouseService.class).getAllStoreHousesByShopId(searchConditionDTO.getShopId());
        }
        return storeHouseDTOs;
    }
}
