Ext.define('Ext.view.product.shopProduct.ShopProductList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.productShopProList',
    store: 'Ext.store.product.ShopProducts',
    autoScroll: true,
    columnLines: true,
    stripeRows: true,            //每列是否是斑马线分开
    multiSelect: true,           //可以多选
    autoHeight: true,
    layout: 'anchor',
    enableColumnResize: true,
    requires: [
        'Ext.ux.RowExpander', 'Ext.app.ActionTextColumn'
    ],
//    viewConfig: {
//        stripeRows: true,
//        listeners : {
//            beforerefresh : function(view) {
//                var store = view.getStore();
//                var model = view.getSelectionModel();
//                var s = [];
//                store.queryBy(function(record) {
//                    if (record.get('relevanceStatus') === 'YES') {
//                        s.push(record);
//                    }
//                });
//            }
//        }
//    },
    plugins: [
        {
            ptype: 'rowexpander',
            rowBodyTpl: [
                '<table class="tableFix" width="883">',
                '<tr>',
                '<td width="155"><span style="margin-left: 83px"><b>对应标准化产品</b></span></td>',
                '<td width="77" class="tdFix" title="{normalCommodityCode}">{normalCommodityCode}</td>',
                '<td width="87" class="tdFix" title="{normalProductName}">{normalProductName}</td>',
                '<td width="77" class="tdFix" title="{normalBrand}">{normalBrand}</td>',
                '<td width="77" class="tdFix" title="{normalSpec}">{normalSpec}</td>',
                '<td width="77" class="tdFix" title="{normalModel}">{normalModel}</td>',
                '<td width="154" class="tdFix" title="{normalVehicleBrandModelInfo}">{normalVehicleBrandModelInfo}</td>',
                '<td width="47" class="tdFix" title="{normalUnit}">{normalUnit}</td>',
                '</tr>',
                '</table>'
            ]
        }
    ],

    initComponent: function () {
        var me = this;
        var store = Ext.create("Ext.store.product.ShopProducts");
        this.store = store;
//        var permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        Ext.apply(me,
            {
                selModel: Ext.create('Ext.selection.CheckboxModel', {
                    listeners: {
                        beforeselect: function(grid, record, index, eOpts) {
//                            if (record.get('relevanceStatus') === 'YES') {
//                                return false;
//                            }
                        }
                    }
                }),
                columns: [
                    {
                        header: 'No',
                        xtype: 'rownumberer',
                        sortable: false,
                        width: 35
                    },
                    {
                        header: '所属店铺',
                        dataIndex: 'shopName',
                        width: 100,
                        renderer: function (value, metadata) {
                            metadata.tdAttr = 'data-qtip="' + value + '"';
                            return value;
                        }

                    },
                    {
                        header: '编码',
                        dataIndex: 'commodityCode',
                        width: 90,
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
                        width: 90,
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
                        width: 90,
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
                        width: 90,
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
                        header: '适合车型',
                        dataIndex: 'productVehicleModel',
                        width: 90,
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
                        header: '车辆品牌',
                        dataIndex: 'productVehicleBrand',
                        width: 90,
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
                        id: 'shopProductListGridAction',
                        width: 120,
                        items: [
                            {
                                getClass: function (v, meta, rec) {
                                    this.items[0].tooltip = '引用为标准商品';
                                    this.items[0].text = '引用';
                                    return '';
                                },
                                handler: function (gridview, rowIndex, colIndex) {
                                    this.fireEvent('copyNormalProductClick', gridview, colIndex, rowIndex);
                                }
                            },
                            {
                                getClass: function (v, meta, rec) {
                                    if (rec.get('relevanceStatus') === 'UN_CHECKED') {
                                        this.items[1].tooltip = '复核';
                                        this.items[1].text = '复核';
                                        return '';
                                    } else {
                                        this.items[1].tooltip = '';
                                        this.items[1].text = '';
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
                                        this.items[2].tooltip = '取消绑定';
                                        this.items[2].text = '取消绑定';
                                        return '';
                                    } else {
                                        this.items[2].tooltip = '';
                                        this.items[2].text = '';
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
                        xtype: 'toolbar',
                        dock: 'top',
                        items: [
                            {
                                xtype: 'label',
                                text: '店铺商品信息:',
                                width: 80
                            },
                            {
                                xtype: "combobox",
                                emptyText: "品名/品牌/规格/型号/适用车辆",
                                editable: true,
                                width: 200,
                                displayField: 'name',
                                valueField: 'name',
                                id: "productInfo",
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                })
                            },
                            {
                                xtype: "combobox",
                                emptyText: "品名",
                                editable: true,
                                width: 100,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                }),
                                id: "shopProductName"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "品牌",
                                editable: true,
                                width: 100,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                }),
                                id: "shopBrand"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "规格",
                                editable: true,
                                width: 100,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                }),
                                id: "shopSpec"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "型号",
                                editable: true,
                                width: 100,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                }),
                                id: "shopModel"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "车辆品牌",
                                editable: true,
                                width: 100,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                }),
                                id: "shopVehicleBrand"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "车型",
                                editable: true,
                                width: 100,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                }),
                                id: "shopVehicleModel"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "商品编号",
                                editable: true,
                                width: 100,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchProductInfo"
                                    }),
                                    fields: ["name", "jsonStr"],
                                    autoLoad: false
                                }),
                                id: "shopCommodityCode"
                            }
                        ]
                    },
                    {
                        xtype: 'toolbar',
                        dock: 'top',
                        items: [
                            {
                                xtype: 'label',
                                text: '所属店铺:',
                                width: 80
                            },
                            {
                                xtype: "combobox",
                                emptyText: "",
                                width: 150,
                                displayField: 'name',
                                valueField: 'name',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                queryParam: 'keyWord',  //过滤字
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=searchShop"
                                    }),
                                    fields: ["name"],
                                    autoLoad: true
                                }),
                                id: "searchShop"
                            },
                            {
                                xtype: 'checkboxgroup',
                                fieldLabel: '关联状态',
                                columns: 3,
                                labelAlign: 'right',
                                labelWidth: 60,
                                width: 240,
                                name: 'relevanceStatusCheckBoxGroup',
                                id: 'relevanceStatusCheckBoxGroup',
                                items: [
                                    { boxLabel: '未标准', name: 'relevanceStatus', checked: true, inputValue: 'NO', width: 60 },
                                    { boxLabel: '待复核', name: 'relevanceStatus', checked: true, inputValue: 'UN_CHECKED', width: 60 },
                                    { boxLabel: '已标准', name: 'relevanceStatus', inputValue: 'YES', width: 60 }
                                ]
                            },
                            "->",
                            {
                                text: "重置",
                                xtype: 'button',
                                action: 'reset',
                                iconCls: "icon-reset",
                                scope: me,
                                handler: function () {
                                    me.reset();
                                }
                            },
                            {
                                text: "查询",
                                xtype: 'button',
                                action: 'search',
                                iconCls: "icon-search",
                                tooltip: "根据条件查询产品信息",
                                scope: me,
                                handler: function () {
                                    me.onSearch();
                                }
                            },
                            {
                                text: '商品标准化',
                                xtype: 'button',
                                tooltip: '关联标准商品',
                                action: 'relevanceNormalProduct',
                                scope: this,
                                iconCls: 'icon-product-desc'
                            }
                        ]
                    },
                    {
                        dock: 'bottom',
                        xtype: 'pagingtoolbar',
                        store: store,
                        displayInfo: true
                    }
                ]
            });
        this.callParent(arguments);
    },
    reset: function () {
        Ext.getCmp("productInfo").setRawValue(null);
        Ext.getCmp("shopProductName").setRawValue(null);
        Ext.getCmp("shopBrand").setRawValue(null);
        Ext.getCmp("shopSpec").setRawValue(null);
        Ext.getCmp("shopModel").setRawValue(null);
        Ext.getCmp("shopVehicleBrand").setRawValue(null);
        Ext.getCmp("shopCommodityCode").setRawValue(null);
        Ext.getCmp("shopVehicleModel").setRawValue(null);
        Ext.getCmp("searchShop").setRawValue(null);
        Ext.getCmp("relevanceStatusCheckBoxGroup").items.each(function (item) {
            if(item.inputValue=="YES"){
                item.setValue(false);
            }else{
                item.setValue(true);
            }
        });
    },
    onSearch: function () {
        var params = {
            searchWord: Ext.getCmp("productInfo").getValue(),
            productName: Ext.getCmp("shopProductName").getValue(),
            productBrand: Ext.getCmp("shopBrand").getValue(),
            productSpec: Ext.getCmp("shopSpec").getValue(),
            productModel: Ext.getCmp("shopModel").getValue(),
            productVehicleBrand: Ext.getCmp("shopVehicleBrand").getValue(),
            productVehicleModel: Ext.getCmp("shopVehicleModel").getValue(),
            commodityCode: Ext.getCmp("shopCommodityCode").getValue(),
            shopName: Ext.getCmp("searchShop").getValue(),
            includeBasic: false,
            now: new Date()
        };
        var relevanceStatuses = Ext.getCmp("relevanceStatusCheckBoxGroup").getValue();
        if (!Ext.isEmpty(relevanceStatuses['relevanceStatus'])) {
            params['relevanceStatuses'] = relevanceStatuses['relevanceStatus'];
        }
        this.store.proxy.extraParams = params;
        this.store.loadPage(1);
        this.getSelectionModel().deselectAll();
    }
});
