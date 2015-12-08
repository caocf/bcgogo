Ext.define('Ext.view.product.normalProduct.NormalProductList', {
    extend:'Ext.grid.Panel',
    alias:'widget.productNormalProList',
    store:'Ext.store.product.NormalProducts',
    autoScroll:true,
    columnLines:true,
    stripeRows:true,            //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
    forceFit:true,              //自动填充，即让所有列填充满gird宽度
    multiSelect:false,           //可以多选
    autoHeight:true,
    enableColumnResize:true,
    requires:[

    ],
    initComponent:function () {
        var me = this;
//        var permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me,
            {
            dockedItems:[
                    {
                        xtype:'toolbar',
                        dock:'top',
                        id:'productTableTop',
                        items:[
                            {
                                xtype:"combobox",
                                emptyText:"一级分类",
                                editable:false,
                                width:100,
                                displayField:'name',
                                valueField:'id',
                                id:"searchFirstCategory",
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getFirstCategory"
                                    }),
                                    fields : ["name", "id"],
                                    autoLoad : false
                                })
                            },
                            {
                                xtype:"combobox",
                                emptyText:"二级分类",
                                editable:false,
                                width:100,
                                id:"searchSecondCategory",
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getSecondCategory"
                                    }),
                                    fields : ["name", "id"],
                                    autoLoad : false
                                }),
                                displayField:'name',
                                valueField:'id'
                            },
                            {
                                xtype:"combobox",
                                emptyText:"品名",
                                editable:true,
                                width:100,
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getThirdCategory"
                                    }),
                                    fields : ["name", "id"],
                                    autoLoad : false
                                }),
                                displayField:'name',
                                valueField:'id',
                                remoteFilter:true, //ajax过滤开关
                                queryMode:'remote', //远程过滤
                                enableKeyEvents:true,
                                minChars:1,
                                queryDelay:500,
                                id:"searchProductName"
                            },
                            {
                                xtype:"combobox",
                                emptyText:"品牌",
                                editable:true,
                                width:100,
                                store:Ext.create("Ext.data.Store",{
                                    proxy:{
                                        type:'ajax',
                                        api:{
                                            read:'productManage.do?method=getDataByQueryBuilder'
                                        }
                                    },
                                    fields : ["key"],
                                    autoLoad : false
                                }),
                                displayField:'key',
                                valueField:'key',
                                remoteFilter:true, //ajax过滤开关
                                queryMode:'remote', //远程过滤
                                enableKeyEvents:true,
                                minChars:1,
                                queryDelay:500,
                                id:"searchBrand"
                            },
                            {
                                xtype:"combobox",
                                emptyText:"规格",
                                editable:true,
                                width:100,
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getDataByQueryBuilder"
                                    }),
                                    fields : ["key"],
                                    autoLoad : false
                                }),
                                displayField:'key',
                                valueField:'key',
                                remoteFilter:true, //ajax过滤开关
                                queryMode:'remote', //远程过滤
                                enableKeyEvents:true,
                                minChars:1,
                                queryDelay:500,
                                id:"searchSpec"
                            },
                            {
                                xtype:"combobox",
                                emptyText:"型号",
                                editable:true,
                                width:100,
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getDataByQueryBuilder"
                                    }),
                                    fields : ["key"],
                                    autoLoad : false
                                }),
                                displayField:'key',
                                valueField:'key',
                                remoteFilter:true, //ajax过滤开关
                                queryMode:'remote', //远程过滤
                                enableKeyEvents:true,
                                minChars:1,
                                queryDelay:500,
                                id:"searchModel"
                            },
                            {
                                xtype:"combobox",
                                emptyText:"车辆品牌",
//                                            editable:false,
                                width:100,
                                displayField:'name',
                                valueField:'name',
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getVehicleBrand"
                                    }),
                                    fields : ["name", "name"],
                                    autoLoad : false
                                }),
                                id:"searchVehicleBrand",
                                remoteFilter:true, //ajax过滤开关
                                queryMode:'remote', //远程过滤
                                enableKeyEvents:true,
                                minChars:1,
                                queryDelay:500
                            },
                            {
                                xtype:"combobox",
                                emptyText:"车型",
                                width:100,
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getVehicleModel"
                                    }),
                                    fields : ["name", "name"],
                                    autoLoad : false
                                }),
                                displayField:'name',
                                valueField:'name',                                                                                     remoteFilter:true, //ajax过滤开关
                                queryMode:'remote', //远程过滤
                                enableKeyEvents:true,
                                minChars:1,
                                queryDelay:500,
                                id:"searchVehicleModel"
                            },
                            {
                                xtype:"combobox",
                                emptyText:"商品编码",
                                editable:true,
                                width:100,
                                store:new Ext.data.SimpleStore({
                                    proxy : new Ext.data.HttpProxy({
                                        url : "productManage.do?method=getDataByQueryBuilder"
                                    }),
                                    fields : ["key"],
                                    autoLoad : false
                                }),
                                displayField:'key',
                                valueField:'key',
                                remoteFilter:true, //ajax过滤开关
                                queryMode:'remote', //远程过滤
                                enableKeyEvents:true,
                                minChars:1,
                                queryDelay:500,
                                id:"searchCommodityCode"
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
                                text:"查询",
                                xtype:'button',
                                action:'search',
                                iconCls:"icon-search",
                                tooltip:"根据条件查询产品信息",
                                scope:this
                            },
                            '->',
                            {
                                text:'新增',
                                xtype:'button',
                                tooltip:'新增标准商品',
                                action:'addNormalProduct',
                                scope:this,
                                iconCls:'icon-add'
                            }
                        ]
                    },
                    {
                        dock:'bottom',
                        xtype:'pagingtoolbar',
                        store:'Ext.store.product.NormalProducts',
                        displayInfo:true
                    }

                ],
            id:"normaProductGrid",
            columns:[
                {
                    header:'序号',
                    xtype:'rownumberer',
                    sortable:false,
                    width:40
                },
                {
                    header:'商品编码',
                    dataIndex:'commodityCode',
                    width:100
                },
                {
                    header:'一级分类',
                    dataIndex:'productFirstCategoryName',
                    width:100
                },
                {
                    header:'二级分类',
                    dataIndex:'productSecondCategoryName',
                    width:100
                },
                {
                    header:'品名',
                    dataIndex:'productName',
                    width:100
                },
                {
                    header:'品牌',
                    dataIndex:'brand',
                    width:100
                },
                {
                    header:'规格',
                    dataIndex:'spec',
                    width:100
                },
                {
                    header:'型号',
                    dataIndex:'model',
                    width:100
                },
                {
                    header: '适用车型',
                    dataIndex: 'vehicleBrandModelInfo',
                    width: 150,
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
                    xtype:'actioncolumn',
                    action:'edit',
                    header:'编辑',
                    width:30,
                    items:[
                        {
                            text:'编辑',
                            tooltip:'编辑产品',
                            scope:me,
                            width:30,
                            icon:'app/images/icons/edit.png'
                        }
                    ]
                },
                {
                    xtype:'actioncolumn',
                    action:'delete',
                    header:'删除',
                    width:30,
                    items:[
                        {
                            text:'删除',
                            tooltip:'删除产品',
                            scope:me,
                            width:30,
                            icon:'app/images/icons/delete.png'
                        }
                    ]
                }

            ]
        });
        this.callParent(arguments);
    },
    reset: function () {
        Ext.getCmp("searchFirstCategory").setRawValue(null);
        Ext.getCmp("searchSecondCategory").setRawValue(null);
        Ext.getCmp("searchProductName").setRawValue(null);
        Ext.getCmp("searchBrand").setRawValue(null);
        Ext.getCmp("searchSpec").setRawValue(null);
        Ext.getCmp("searchModel").setRawValue(null);
        Ext.getCmp("searchVehicleBrand").setRawValue(null);
        Ext.getCmp("searchVehicleModel").setRawValue(null);
    }
});
