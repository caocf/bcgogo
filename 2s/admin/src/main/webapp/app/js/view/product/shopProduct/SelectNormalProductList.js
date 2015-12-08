Ext.define('Ext.view.product.shopProduct.SelectNormalProductList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.selectNormalProductList',
    store: 'Ext.store.product.NormalProducts',
    autoScroll: true,
    columnLines: true,
    stripeRows: true,            //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
//    forceFit:true,              //自动填充，即让所有列填充满gird宽度
    multiSelect: false,           //可以多选
    autoHeight: true,
    layout: 'anchor',
    enableColumnResize: true,
    requires: [

    ],
    initComponent: function () {
        var me = this;
//        var permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        Ext.apply(me,
            {
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'top',
                        items: [
                            {
                                xtype: "combobox",
                                emptyText: "一级分类",
                                editable: false,
                                width: 100,
                                displayField: 'name',
                                valueField: 'id',
                                id: "normalSearchFirstCategory",
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=getFirstCategory"
                                    }),
                                    fields: ["name", "id"],
                                    autoLoad: false
                                })
                            },
                            {
                                xtype: "combobox",
                                emptyText: "二级分类",
                                editable: false,
                                width: 100,
                                id: "normalSearchSecondCategory",
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=getSecondCategory"
                                    }),
                                    fields: ["name", "id"],
                                    autoLoad: false
                                }),
                                displayField: 'name',
                                valueField: 'id'
                            },
                            {
                                xtype: "combobox",
                                emptyText: "品名",
                                editable: true,
                                width: 100,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=getThirdCategory"
                                    }),
                                    fields: ["name", "id"],
                                    autoLoad: false
                                }),
                                displayField: 'name',
                                valueField: 'id',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                id: "normalSearchProductName"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "品牌",
                                editable: true,
                                width: 100,
                                store: Ext.create("Ext.data.Store", {
                                    proxy: {
                                        type: 'ajax',
                                        api: {
                                            read: 'productManage.do?method=getDataByQueryBuilder'
                                        }
                                    },
                                    fields: ["key"],
                                    autoLoad: false
                                }),
                                displayField: 'key',
                                valueField: 'key',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                id: "normalSearchBrand"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "规格",
                                editable: true,
                                width: 100,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=getDataByQueryBuilder"
                                    }),
                                    fields: ["key"],
                                    autoLoad: false
                                }),
                                displayField: 'key',
                                valueField: 'key',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                id: "normalSearchSpec"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "型号",
                                editable: true,
                                width: 100,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=getDataByQueryBuilder"
                                    }),
                                    fields: ["key"],
                                    autoLoad: false
                                }),
                                displayField: 'key',
                                valueField: 'key',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                id: "normalSearchModel"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "车辆品牌",
                                editable: true,
                                width: 100,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=getDataByQueryBuilder"
                                    }),
                                    fields: ["key"],
                                    autoLoad: false
                                }),
                                displayField: 'key',
                                valueField: 'key',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                id: "normalSearchVehicleBrand"
                            },
                            {
                                xtype: "combobox",
                                emptyText: "车型",
                                editable: true,
                                width: 100,
                                store: new Ext.data.SimpleStore({
                                    proxy: new Ext.data.HttpProxy({
                                        url: "productManage.do?method=getDataByQueryBuilder"
                                    }),
                                    fields: ["key"],
                                    autoLoad: false
                                }),
                                displayField: 'key',
                                valueField: 'vehicleModel',
                                remoteFilter: true, //ajax过滤开关
                                queryMode: 'remote', //远程过滤
                                enableKeyEvents: true,
                                minChars: 1,
                                queryDelay: 500,
                                id: "normalSearchVehicleModel"
                            },
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
                                action: 'searchNormalProduct',
                                iconCls: "icon-search",
                                tooltip: "根据条件查询产品信息",
                                scope: me,
                                handler: function () {
                                    me.onSearch();
                                }
                            }
                        ]
                    },
                    {
                        dock: 'bottom',
                        xtype: 'pagingtoolbar',
                        store: 'Ext.store.product.NormalProducts',
                        displayInfo: true
                    }
                ],
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
                        width: 100
                    },
                    {
                        header: '商品分类',
                        dataIndex: 'productCategoryName',
                        width: 150,
                        renderer: function (value, metadata) {
                            metadata.tdAttr = 'data-qtip="' + value + '"';
                            return value;
                        }
                    },
                    {
                        header: '品名',
                        dataIndex: 'productName',
                        width: 100
                    },
                    {
                        header: '品牌',
                        dataIndex: 'brand',
                        width: 100
                    },
                    {
                        header: '规格',
                        dataIndex: 'spec',
                        width: 100
                    },
                    {
                        header: '型号',
                        dataIndex: 'model',
                        width: 100
                    },
                    {
                        header: '适用车型',
                        dataIndex: 'vehicleBrandModelInfo',
                        width: 200,
                        renderer: function (value, metadata) {
                            metadata.tdAttr = 'data-qtip="' + value + '"';
                            return value;
                        }

                    },
                    {
                        header: '单位',
                        dataIndex: 'unit',
                        width: 50
                    },
                    {
                        header: '绑定店铺商品',
                        dataIndex: 'bindingShopProductCount',
                        width: 80
                    },
                    {
                        xtype: 'actiontextcolumn',
                        header: '操作',
                        id:'selectNormalProductListGridAction',
                        width: 80,
                        items: [
                            {
                                getClass: function (v, meta, rec) {
                                    this.items[0].tooltip='引用查询';
                                    this.items[0].text='引用查询';
                                    return '';
                                },
                                handler: function(gridview, rowIndex, colIndex) {
                                    this.fireEvent('copyQueryConditionClick',gridview, colIndex,rowIndex);
                                }
                            }
                        ]
                    }
                ]
            });
        this.callParent(arguments);
    },
    reset: function () {
        Ext.getCmp("normalSearchFirstCategory").setRawValue(null);
        Ext.getCmp("normalSearchSecondCategory").setRawValue(null);
        Ext.getCmp("normalSearchProductName").setRawValue(null);
        Ext.getCmp("normalSearchProductName").setRawValue(null);
        Ext.getCmp("normalSearchProductName").setRawValue(null);
        Ext.getCmp("normalSearchBrand").setRawValue(null);
        Ext.getCmp("normalSearchSpec").setRawValue(null);
        Ext.getCmp("normalSearchModel").setRawValue(null);
        Ext.getCmp("normalSearchVehicleBrand").setRawValue(null);
        Ext.getCmp("normalSearchVehicleModel").setRawValue(null);
    },
    onSearch: function () {
        var firstSelect = Ext.getCmp("normalSearchFirstCategory");
        var secondSelect = Ext.getCmp("normalSearchSecondCategory");
        var firstCategoryId = firstSelect.getValue();
        var secondCategoryId = secondSelect.getValue();
        var productNameCmp = Ext.getCmp("normalSearchProductName");
        var productName = Ext.getCmp("normalSearchProductName").getRawValue();
        var thirdCategoryId = Ext.getCmp("normalSearchProductName").getValue();
        var brand = Ext.getCmp("normalSearchBrand").getValue();
        var spec = Ext.getCmp("normalSearchSpec").getValue();
        var model = Ext.getCmp("normalSearchModel").getValue();
        var vehicleBrand = Ext.getCmp("normalSearchVehicleBrand").getValue();
        var vehicleModel = Ext.getCmp("normalSearchVehicleModel").getValue();
        if (thirdCategoryId && isNaN(thirdCategoryId)) {
            thirdCategoryId = "";
        }
        var data = {
            productName: productName,
            brand: brand,
            spec: spec,
            model: model,
            vehicleBrand: vehicleBrand,
            vehicleModel: vehicleModel,
            firstCategoryId: firstCategoryId,
            secondCategoryId: secondCategoryId,
            thirdCategoryId: thirdCategoryId
        };
        this.store.proxy.extraParams = data; //防止 共用store的层 带入参数
        this.store.loadPage(1);
    }
});
