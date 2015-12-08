Ext.define('Ext.view.product.normalProduct.SearchRelevanceShopProduct', {
    extend:'Ext.grid.Panel',
    alias:'widget.searchRelevanceShopProduct',
    store:'Ext.store.product.ShopProducts',
    autoScroll:true,
    columnLines:true,
    stripeRows:true,            //每列是否是斑马线分开
    autoHeight:true,
    forceFit:true,              //自动填充，即让所有列填充满gird宽度
    multiSelect:false,           //可以多选
    layout:'anchor',
    enableColumnResize:true,
    requires:[
        'Ext.app.ActionTextColumn'
    ],
    initComponent: function () {
        var me = this;
//        var permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        Ext.apply(me,
            {
                columns: [
                    {
                        header: '序号',
                        xtype: 'rownumberer',
                        sortable: false,
                        width: 35
                    },
                    {
                        header: '商品编码',
                        dataIndex: 'commodityCode',
                        width: 100,
                        renderer: function (val, style, rec, index) {
                            style.tdAttr = 'data-qtip="' + val + '"';
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            if(productModifyFieldsList.indexOf("commodityCode")>-1){
                                return "<span style='color: #008000'>"+val+"</span>"
                            }else{
                                return val;
                            }
                        }
                    },
                    {
                        header: '品名',
                        dataIndex: 'name',
                        width: 100,
                        renderer: function (val, style, rec, index) {
                            style.tdAttr = 'data-qtip="' + val + '"';
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            if(productModifyFieldsList.indexOf("name")>-1){
                                return "<span style='color: #008000'>"+val+"</span>"
                            }else{
                                return val;
                            }
                        }
                    },
                    {
                        header: '品牌',
                        dataIndex: 'brand',
                        width: 100,
                        renderer: function (val, style, rec, index) {
                            style.tdAttr = 'data-qtip="' + val + '"';
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            if(productModifyFieldsList.indexOf("brand")>-1){
                                return "<span style='color: #008000'>"+val+"</span>"
                            }else{
                                return val;
                            }
                        }
                    },
                    {
                        header: '规格',
                        dataIndex: 'spec',
                        width: 100,
                        renderer: function (val, style, rec, index) {
                            style.tdAttr = 'data-qtip="' + val + '"';
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            if(productModifyFieldsList.indexOf("spec")>-1){
                                return "<span style='color: #008000'>"+val+"</span>"
                            }else{
                                return val;
                            }
                        }
                    },
                    {
                        header: '型号',
                        dataIndex: 'model',
                        width: 100,
                        renderer: function (val, style, rec, index) {
                            style.tdAttr = 'data-qtip="' + val + '"';
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            if(productModifyFieldsList.indexOf("model")>-1){
                                return "<span style='color: #008000'>"+val+"</span>"
                            }else{
                                return val;
                            }
                        }
                    },
                    {
                        header: '车辆品牌',
                        dataIndex: 'productVehicleBrand',
                        width: 100,
                        renderer: function (val, style, rec, index) {
                            style.tdAttr = 'data-qtip="' + val + '"';
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            if(productModifyFieldsList.indexOf("productVehicleBrand")>-1){
                                return "<span style='color: #008000'>"+val+"</span>"
                            }else{
                                return val;
                            }
                        }
                    },
                    {
                        header: '适合车型',
                        dataIndex: 'productVehicleModel',
                        width: 100,
                        renderer: function (val, style, rec, index) {
                            style.tdAttr = 'data-qtip="' + val + '"';
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            if(productModifyFieldsList.indexOf("productVehicleModel")>-1){
                                return "<span style='color: #008000'>"+val+"</span>"
                            }else{
                                return val;
                            }
                        }
                    },
                    {
                        header: '单位',
                        width: 60,
                        renderer: function (val, style, rec, index) {
                            var sellUnit = rec.get("sellUnit");
                            var storageUnit = rec.get("storageUnit");
                            var productModifyFieldsList = rec.get("productModifyFieldsList");
                            var content = "";
                            if (sellUnit === storageUnit) {
                                content= sellUnit;
                            } else{
                                content= sellUnit+"/"+storageUnit;
                            }
                            if(productModifyFieldsList.indexOf("storageUnit")>-1 || productModifyFieldsList.indexOf("sellUnit")>-1){
                                return "<span style='color: #008000'>"+content+"</span>"
                            }else{
                                return content;
                            }
                        }
                    },
                    {
                        header: '成本均价',
                        dataIndex: 'inventoryAveragePrice',
                        width: 80,
                        renderer: function (val, style, rec, index) {
                            if(!Ext.isEmpty(val)){
                                var sellUnit = rec.get("sellUnit");
                                if(!Ext.isEmpty(sellUnit)){
                                    return val+"元/"+sellUnit;
                                }else{
                                    return val+"元";
                                }
                            }else{
                                return "";
                            }
                        }
                    },
                    {
                        header: '店铺',
                        dataIndex: 'shopName',
                        width: 140,
                        renderer: function (value, metadata) {
                            metadata.tdAttr = 'data-qtip="' + value + '"';
                            return value;
                        }
                    },
                    {
                        header: '状态',
                        dataIndex: 'relevanceStatus',
                        width: 60,
                        renderer: function (val, style, rec, index) {
                            if (val === "YES") {
                                return "已标准";
                            } else if (val === "UN_CHECKED") {
                                return "<span style='color: #008000'>待复核</span>";
                            } else {
                                return "<span style='color: red'>未标准</span>";
                            }
                        }
                    },
                    {
                        xtype: 'actiontextcolumn',
                        header: '操作',
                        id: 'relevanceShopProductListGridAction',
                        width: 120,
                        items: [
                            {
                                getClass: function (v, meta, rec) {
                                    if (rec.get('relevanceStatus') === 'UN_CHECKED') {
                                        this.items[0].tooltip = '复核';
                                        this.items[0].text = '复核';
                                        return '';
                                    } else {
                                        this.items[0].tooltip = '';
                                        this.items[0].text = '';
                                        return '';
                                    }
                                },
                                handler: function (gridview, rowIndex, colIndex) {
                                    this.fireEvent('examineBindingClick', gridview, colIndex, rowIndex);
                                }
                            },
                            {
                                getClass: function (v, meta, rec) {
                                    if (rec.get('relevanceStatus') === 'YES' || rec.get('relevanceStatus') === 'UN_CHECKED') {
                                        this.items[1].tooltip = '取消绑定';
                                        this.items[1].text = '取消绑定';
                                        return '';
                                    } else {
                                        this.items[1].tooltip = '';
                                        this.items[1].text = '';
                                        return '';
                                    }
                                },
                                handler: function (gridview, rowIndex, colIndex) {
                                    this.fireEvent('cancelBindingClick', gridview, colIndex, rowIndex);
                                }
                            }
                        ]
                    }
                ],
                dockedItems: [
                    {
                        dock: 'bottom',
                        xtype: 'pagingtoolbar',
                        store:'Ext.store.product.ShopProducts',
                        displayInfo: true
                    }
                ]
            }
        );
        this.callParent(arguments);
    }
});
