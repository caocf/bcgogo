package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-7-31
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
public class ExportInventoryConstant {
        public static final String COMMODITY_CODE = "商品编码";
        public static final String NAME = "品名";
        public static final String BRAND = "品牌/产地";
        public static final String SPEC = "规格";
        public static final String MODEL = "型号";
        public static final String VEHICLE_BRAND = "车辆品牌";
        public static final String VEHICLE_MODEL = "车型";
        public static final String KIND_NAME = "商品分类";
        public static final String INVENTORY_NUM = "库存量";
        public static final String SELL_UNIT = "单位";
        public static final String STORE_HOUSE = "仓库";
        public static final String STORE_HOUSE_NAME = "仓库名";
        public static final String STORAGE_BIN = "货位";
        public static final String SINGLE_INVENTORY_NUM = "分库库存";
        public static final String FILE_NAME = "库存导出.xls";
        public static List<String> fieldList;
        static {
            fieldList = new ArrayList<String>();
            fieldList.add(COMMODITY_CODE);
            fieldList.add(NAME);
            fieldList.add(BRAND);
            fieldList.add(SPEC);
            fieldList.add(MODEL);
            fieldList.add(VEHICLE_BRAND);
            fieldList.add(VEHICLE_MODEL);
            fieldList.add(KIND_NAME);
            fieldList.add(INVENTORY_NUM);
            fieldList.add(SELL_UNIT);
        }
}
