/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 14-2-27
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.normalProductStat.ShopProductStatList', {
    extend:'Ext.grid.Panel',
    alias:'widget.shopProductStatList',
     store:'Ext.store.normalProductInventoryStat.ShopProductInventoryStats',
    autoScroll:true,
    columnLines:true,
    loadMask: true,
    stripeRows:true,            //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
//    forceFit:true,              //自动填充，即让所有列填充满gird宽度
    multiSelect:true,           //可以多选
    autoHeight:true,
    layout:'anchor',
    enableColumnResize:true,
    requires:[
        'Ext.view.dataMaintenance.permission.ShopVersionSelect',
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.ProvinceSelect"
    ],
    initComponent:function () {
        var me = this;

        Ext.apply(me,
            { dockedItems:[
                {
                    xtype:'toolbar',
                    dock:'top',
                    name:'shopProductStat',
                    items:[
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
                            name: "productInfo",
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
                            name: "shopProductName"
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
                            name: "shopBrand"
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
                            name: "shopSpec"
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
                            name: "shopModel"
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
                            name: "shopVehicleBrand"
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
                            name: "shopVehicleModel"
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
                            name: "shopCommodityCode"
                        }
                    ]
                },
                {
                    dock:'bottom',
                    xtype:'pagingtoolbar',
                    store:'Ext.store.normalProductInventoryStat.ShopProductInventoryStats',
                    displayInfo:true
                },
                {
                    xtype:'toolbar',
                    dock:'top',
                    items:[
                        {
                            xtype: "combobox",
                            emptyText: "软件版本",
                            fieldLabel: "软件版本",
                            labelWidth: 55,
                            margin: "0 0 0 -3",
                            displayField: 'name',
                            valueField: 'value',
                            enableKeyEvents: true,
                            minChars: 1,
                            store: new Ext.data.SimpleStore({
                                fields: ["name","value"],
                                data:[
                                    ["汽修版","REPAIR_VERSION"],
                                    ["汽配版","WHOLESALER_VERSION"]
                                ],
                                autoLoad: true
                            }),
                            name: 'shopVersion'

                        },
                        {
                            fieldLabel: '店铺区域',
                            labelWidth: 55,
                            width: 135,
                            xtype: "provinceSelect",
                            name: 'province',
                            margin: "0 0 0 50",
                            listeners: {
                                select: function (combo, records, eOpts) {
                                    me.down("citySelect").setRawValue("");
                                    me.down("citySelect").setValue(null);
                                    me.down("regionSelect").setRawValue(null);
                                    me.down("regionSelect").setValue(null);
                                    me.down("citySelect").setProvince(records[0]);
                                },
                                beforequery: function (queryEvent, eOpts) {
                                    queryEvent.combo.store.proxy.extraParams = {
                                        parentNo: "1"
                                    };
                                }
                            }
                        },
                        {
                            width: 80,
                            xtype: "citySelect",
                            name: 'city',
                            listeners: {
                                select: function (combo, records, eOpts) {
                                    me.down("regionSelect").setRawValue(null);
                                    me.down("regionSelect").setValue(null);
                                    me.down("regionSelect").setCity(records[0]);
                                },
                                beforequery: function (queryEvent, eOpts) {
                                    if (!queryEvent.combo.getProvince()) {
                                        return false;
                                    }
                                    queryEvent.combo.store.proxy.extraParams = {
                                        parentNo: queryEvent.combo.getProvince().get("no")
                                    };
                                    queryEvent.combo.store.load();
                                }
                            }
                        },
                        {
                            width: 80,
                            xtype: "regionSelect",
                            name: 'region',
                            listeners: {
                                beforequery: function (queryEvent, eOpts) {
                                    if (!queryEvent.combo.getCity()) {
                                        return false;
                                    }
                                    queryEvent.combo.store.proxy.extraParams = {
                                        parentNo: queryEvent.combo.getCity().get("no")
                                    };
                                    queryEvent.combo.store.load();
                                }
                            }
                        },
                        {
                            xtype: 'label',
                            text: '店铺名:',
                            width: 50,
                            margin:"0 0 0 30"
                        },
                        {
                            xtype: "combobox",
                            emptyText: "店铺名",
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
                            name:"shopName"
                        }
                    ]
                },
                {
                    xtype:'toolbar',
                    columns: 2,
                    items:[
                        {
                            xtype: 'radiogroup',
                            fieldLabel: '统计日期',
                            // Arrange radio buttons into two columns, distributed vertically
                            columns: 5,
                            width: 700,
                            vertical: false,
                            name: "statDate",
                            items: [
                                { boxLabel: '最近一周', name: 'normalProductStatType', inputValue: 'WEEK', checked: true, id:"shopWeek"},
                                { boxLabel: '最近一个月', name: 'normalProductStatType', inputValue: 'MONTH'},
                                { boxLabel: '最近三个月', name: 'normalProductStatType', inputValue: 'THREE_MONTH' },
                                { boxLabel: '最近六个月', name: 'normalProductStatType', inputValue: 'HALF_YEAR' },
                                { boxLabel: '最近一年', name: 'normalProductStatType', inputValue: 'YEAR' }
                            ]
                        },
                        '-',
                        {
                            text:"查询",
                            xtype:'button',
                            action:'shopProductSearch',
                            iconCls:"icon-search",
                            tooltip:"根据条件查询采购信息",
                            scope:this
                        },
                        {
                            text: "重置",
                            xtype: 'button',
                            iconCls: "icon-reset",
                            scope: me,
                            handler: function () {
                                me.reset();
                            }
                        }
                    ]
                }
            ],
                columns:[
                    {
                        header:'序号',
                        xtype:'rownumberer',
                        sortable:false,
                        width:35
                    },
                    {
                        header:'店铺名',
                        dataIndex:'shopName',
                        width:110
                    },
                    {
                        header:'软件版本',
                        dataIndex:'shopVersion',
                        width:80
                    },
                    {
                        header:'商品编码',
                        dataIndex:'commodityCode',
                        width:60
                    },
                    {
                        header:'品名/品牌',
                        dataIndex:'nameAndBrand',
                        width:100
                    },
                    {
                        header:'规格/型号',
                        dataIndex:'specAndModel',
                        width:100
                    },
                    {
                        header:'适用车型',
                        dataIndex:'productVehicleBrand',
                        width:90
                    },
                    {
                        header:'单位',
                        dataIndex:'unit',
                        width:40
                    },
                    {
                        header:'采购次数',
                        dataIndex:'times',
                        width:55
                    },
                    {
                        header:'采购总量',
                        dataIndex:'amount',
                        width:55
                    },
                    {
                        header:'采购总额',
                        dataIndex:'total',
                        width:60
                    },
                    {
                        header:'均价',
                        dataIndex:'averagePrice',
                        width:50
                    },
                    {
                        header:'库存',
                        dataIndex:'inventoryAmount',
                        width:40
                    },
                    {
                        header:'最近采购日期',
                        dataIndex:'lastInventoryDate',
                        width:80
                    },
                    {
                        header:'最高价/最低价',
                        dataIndex:'priceStr',
                        width:90
                    }
                ]
            });
        this.callParent(arguments);
    },
    reset: function () {
        this.down("[name=productInfo]").setValue(null);
        this.down("[name=shopProductName]").setValue(null);
        this.down("[name=shopBrand]").setValue(null);
        this.down("[name=shopSpec]").setValue(null);
        this.down("[name=shopModel]").setValue(null);
        this.down("[name=shopVehicleBrand]").setValue(null);
        this.down("[name=shopVehicleModel]").setValue(null);
        this.down("[name=shopCommodityCode]").setValue(null);
        this.down("[name=shopVersion]").setValue(null);
        this.down("[name=province]").setValue(null);
        this.down("[name=city]").setValue(null);
        this.down("[name=region]").setValue(null);
        this.down("[name=shopName]").setValue(null);
        this.down("[id=shopWeek]").setValue(true);
    }

});
