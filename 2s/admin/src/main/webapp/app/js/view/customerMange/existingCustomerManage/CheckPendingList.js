/**
 * @author zhangjuntao
 * 客户管理-待审核客户
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.CheckPendingList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.existingCustomerManageCheckPendingList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: false, //可以多选
    autoHeight: true,
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.RegisterType",
        "Ext.view.customerMange.existingCustomerManage.Select",
        "Ext.view.customerMange.ProvinceSelect"
    ],
    initComponent: function () {
        var me = this, shopVersion,
            store = Ext.create('Ext.store.customerMange.Shops'),
            shopVersions = null, container,
            userSelect = Ext.widget("userSelect", {
                name: 'searchAgent',
                emptyText: "销售人",
                width: 100,
                valueField: 'name',
                xtype: "userSelect",
                anchor: '50%'
            });
        userSelect.store.proxy.extraParams = {
            operateScene: ""
        };

        me.commonUtils = Ext.create("Ext.utils.Common");

        container = Ext.widget({
            xtype: "container",
            border: false,
            layout: {
                overflowHandler: 'Menu'
            },
            items: [
                {
                    xtype: 'toolbar',
                    border: false,
                    items: [
//                        {
//                            xtype: "textfield",
//                            emptyText: "店铺名",
//                            width: 100,
//                            name: 'searchName'
//                        },
                        {
                            xtype: "shopSelect",
                            emptyText: "店铺名",
                            width: 100,
                            valueField: 'name',
                            store: Ext.create('Ext.store.customerMange.Shops', {
                                pageSize: 15,
                                proxy: {
                                    extraParams: {
                                        shopStatuses: "CHECK_PENDING,CHECK_PENDING_REJECTED"
                                    },
                                    type: 'ajax',
                                    api: {
                                        read: 'shopManage.do?method=getShopSuggestionByName'
                                    },
                                    reader: {
                                        type: 'json',
                                        root: "results",
                                        totalProperty: "totalRows"
                                    }
                                }}),
                            name: 'searchName'
                        },
                        {
                            xtype: "textfield",
                            emptyText: "店主",
                            width: 100,
                            name: 'searchOwner'
                        },
                        {
                            fieldLabel: '所在区域',
                            labelWidth: 55,
                            width: 135,
                            xtype: "provinceSelect",
                            name: 'province',
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
                        userSelect,
//                        {
//                            xtype: "textfield",
//                            emptyText: "销售人",
//                            width: 100,
//                            name: 'searchAgent'
//                        },
                        {
                            fieldLabel: '注册时间',
                            labelWidth: 70,
                            xtype: "datefield",
                            emptyText: "开始",
                            format: 'Y-m-d',
                            width: 170,
                            name: 'submitApplicationDateStart'
                        },
                        "至",
                        {
                            xtype: "datefield",
                            emptyText: "结束",
                            width: 100,
                            format: 'Y-m-d',
                            name: 'submitApplicationDateEnd'
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    items: [
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '客户来源',
                            columns: 6,
                            labelWidth: 55,
                            name: 'registerTypes',
                            width: 480,
                            items: [
                                {listeners: {change: me.toggleCheckBox}, boxLabel: '全部', name: 'registerType', inputValue: 'ALL', width: 50},
                                {listeners: {change: me.toggleCheckBox}, boxLabel: '业务员注册', name: 'registerType', inputValue: 'SALESMAN_REGISTER', width: 80},
                                {listeners: {change: me.toggleCheckBox}, boxLabel: '汽配注册', name: 'registerType', inputValue: 'SUPPLIER_REGISTER', width: 70},
                                {listeners: {change: me.toggleCheckBox}, boxLabel: '系统邀请', name: 'registerType', inputValue: 'SYSTEM_INVITE_CUSTOMER', width: 70},
                                {listeners: {change: me.toggleCheckBox}, boxLabel: '客户推荐', name: 'registerType', inputValue: 'CUSTOMER_INVITE', width: 70},
                                {listeners: {change: me.toggleCheckBox}, boxLabel: '供应商推荐', name: 'registerType', inputValue: 'SUPPLIER_INVITE', width: 80}
                            ]
                        },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '状态',
                            columns: 2,
                            labelWidth: 30,
                            name: 'shopStatuses',
                            margin:'0 0 0 30',
                            width: 160,
                            items: [
                                {boxLabel: '待审核', name: 'shopStatus', inputValue: 'CHECK_PENDING',checked:true, width: 60},
                                {boxLabel: '审核拒绝', name: 'shopStatus', inputValue: 'CHECK_PENDING_REJECTED', width: 70}
                            ]
                        },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '定位状态',
                            columns: 2,
                            labelWidth: 55,
                            margin:'0 0 0 30',
                            name: 'locateStatuses',
                            width: 170,
                            items: [
                                {boxLabel: '已定位', name: 'locateStatus', inputValue: 'ACTIVE',width: 55},
                                {boxLabel: '未定位', name: 'locateStatus', inputValue: 'IN_ACTIVE', width: 55}
                            ]
                        }
                    ]
                }
            ]
        });

        me.commonUtils.ajax({
            url: "shopVersion.do?method=getAllShopVersion", // 获取面板的地址
            success: function (data) {
                shopVersions = {
                    xtype: 'checkboxgroup',
                    fieldLabel: '版本',
                    columns: 9,
                    labelWidth: 30,
                    name: 'shopVersionNames',
                    width: 800,
                    items: [
                    ]
                };
                var i = 0;
                for (; i < data['results'].length; i++) {
                    shopVersion = data['results'][i];
                    shopVersions.items[i] = {
                        boxLabel: shopVersion['value'],
                        name: 'shopVersionName',
                        inputValue: shopVersion['name']
                    }
                }
                container.add({
                        xtype: 'toolbar',
                        items: [
                            shopVersions,
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
                            } ,
                            {
                                text: "查询",
                                xtype: 'button',
                                action: 'search',
                                iconCls: "icon-search",
                                tooltip: "根据条件查询用户信息",
                                scope: me,
                                handler: function () {
                                    me.onSearch();
                                }

                            }
                        ]
                    });
                container.add({
                        xtype: 'toolbar',
                        items: [
                            "->",
                            {
                                text: "新增注册",
                                xtype: 'button',
                                action: 'add',
                                iconCls: "icon-add",
                                scope: this
                            },
                            {
                                text: '查看',
                                xtype: 'button',
                                tooltip: '查看店铺',
                                action: 'show',
                                scope: this,
                                hidden:true,
                                iconCls: 'icon-grid-list',
                                disabled: true
                            },
                            {
                                text: '审核',
                                xtype: 'button',
                                iconCls: 'icon-check-pending',
                                action: 'register',
                                scope: this,
                                disabled: true
                            },
                            {
                                text: '定位',
                                xtype: 'button',
                                iconCls: 'icon-locate',
                                action: 'locate',
                                scope: this,
                                disabled: true
                            }
                        ]
                    });
            }
        });

        Ext.apply(me, {
            store: store,
            tbar: container,
            dockedItems: [
                {
                    dock: 'bottom',
                    xtype: 'pagingtoolbar',
                    store: store,
                    displayInfo: true
                }
            ],
            columns: [
                {
                    header: 'No.',
                    xtype: 'rownumberer',
                    sortable: false,
                    width: 25
                },
                {
                    header: '店铺名',
                    dataIndex: 'name',
                    renderer: function (value, metadata) {
                        metadata.tdAttr = 'data-qtip="' + value + '"';
                        return value;
                    }
                },
                {
                    header: '店主',
                    dataIndex: 'owner'
                },
                {
                    header: '联系方式',
                    dataIndex: 'mobile'
                },
                {
                    header: '所属区域',
                    dataIndex: 'areaName',
                    renderer: function (value, metadata) {
                        metadata.tdAttr = 'data-qtip="' + value + '"';
                        return value;
                    }
                },
                {
                    header: '客户来源',
                    dataIndex: 'registerType',
                    renderer: function (val, style, rec, index) {
                        if (val == "SALESMAN_REGISTER") {
                            return "业务员注册";
                        } else if (val == "SYSTEM_INVITE_CUSTOMER" || val == "SYSTEM_INVITE_SUPPLIER" || val == "SELF_REGISTER") {
                            return "系统邀请";
                        } else if (val == "CUSTOMER_INVITE") {
                            return "客户推荐";
                        } else if (val == "SUPPLIER_INVITE") {
                            return "供应商推荐";
                        } else if (val == "SUPPLIER_REGISTER") {
                            return "汽配注册";
                        } else {
                            return "--";
                        }
                    }
                },
                {
                    header: '销售人',
                    dataIndex: 'agent',
                    renderer: function (val, style, rec, index) {
                        if (rec.get("registerType") != "SELF_REGISTER") {
                            return rec.get("agent");
                        }
                        return "--";
//                        return "系统自动推荐";
                    }
                },
                {
                    header: '软件版本',
                    dataIndex: 'shopVersionName'
                },
                {
                    header: '状态',
                    dataIndex: 'shopStatus',
                    renderer: function (val, style, rec, index) {
                        if (val == "CHECK_PENDING") {
                            return "待审核";
                        } else if (val == "CHECK_PENDING_REJECTED") {
                            return "<span style='color:red;'>审核拒绝</span>";
                        }
                    }
                },
                {
                    header: '定位状态',
                    dataIndex: 'locateStatus',
                    renderer: function (val, style, rec, index) {
                        if (val == "ACTIVE") {
                            return "已定位";
                        } else {
                            return "<span style='color:red;'>未定位</span>";
                        }
                    }
                },
                {
                    header: '注册时间',
                    dataIndex: 'submitApplicationDate',
                    renderer: function (val, style, rec, index) {
                        return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d') : "";
                    }
                }
            ]
        });

        this.callParent(arguments);
    },
    toggleCheckBox: function (checkbox, newValue) {
        var items = this.ownerCt.items.items, i = 0;
        if (checkbox == items[0]) {
            Ext.each(items, function (item) {
                item.suspendEvents(false); // Stop all events.
                item.setValue(newValue);
                item.resumeEvents();
            }, this);
        } else {
            var isAllChecked = true;
            for (i = 1; i < items.length; i++) {
                if (!items[i].getValue()) {
                    isAllChecked = false;
                    break;
                }
            }
            if (isAllChecked) {
                items[0].suspendEvents(false);
                items[0].setValue(true);
                items[0].resumeEvents();
            } else {
                items[0].suspendEvents(false);
                items[0].setValue(false);
                items[0].resumeEvents();
            }
        }
    },
    onSearch: function () {
        var customerList = this, params, registrationDateStart, registrationDateEnd, registerTypeArray = [],
            j = 0,
            shopStatuses = customerList.down("[name=shopStatuses]").getValue()['shopStatus'],
            locateStatuses = customerList.down("[name=locateStatuses]").getValue()['locateStatus'],
            registerTypes = customerList.down("[name=registerTypes]").getValue()['registerType'],
            submitApplicationDateStart = customerList.down("[name=submitApplicationDateStart]"),
            submitApplicationDateEnd = customerList.down("[name=submitApplicationDateEnd]"),
            province = customerList.down("[name=province]").getValue(),
            city = customerList.down("[name=city]").getValue(),
            region = customerList.down("[name=region]").getValue(),
            name = customerList.down("[name=searchName]").getValue(),
            owner = customerList.down("[name=searchOwner]").getValue(),
            agent = customerList.down("[name=searchAgent]").getValue();
        if (submitApplicationDateStart.isValid() && submitApplicationDateEnd.isValid()) {
            params = {
                region: region,
                city: city,
                province: province,
                name: name,
                agent: agent,
                owner: owner,
                scene: "CHECK_PENDING",
                shopStatuses: ["CHECK_PENDING", "CHECK_PENDING_REJECTED"]
            };
            if (shopStatuses && shopStatuses.length > 0) {
                params['shopStatuses'] = shopStatuses;
            }
            if (locateStatuses && locateStatuses.length > 0) {
                params['locateStatuses'] = locateStatuses;
            }
            if (registerTypes) {
                if (registerTypes instanceof Array) {
                    for (var i = 0; i < registerTypes.length; i++) {
                        if (registerTypes[i] != "ALL") {
                            registerTypeArray[j++] = registerTypes[i];
                            if (registerTypes[i] == "SYSTEM_INVITE_CUSTOMER") {
                                registerTypeArray[j++] = "SELF_REGISTER";
                            }
                        }
                    }
                } else {
                    registerTypeArray[j++] = registerTypes;
                    if (registerTypes == "SYSTEM_INVITE_CUSTOMER") {
                        registerTypeArray[j++] = "SELF_REGISTER";
                    }
                }
                params['registerType'] = registerTypeArray;
            }
            if (customerList.down("[name=shopVersionNames]")) {
                var shopVersionNames = customerList.down("[name=shopVersionNames]").getValue()['shopVersionName'];
                if (shopVersionNames) {
                    params['shopVersionName'] = shopVersionNames;
                }
            }
            if (submitApplicationDateStart.getValue()) {
                params['submitApplicationDateStart'] = submitApplicationDateStart.getValue().getTime();
            }
            if (submitApplicationDateEnd.getValue()) {
                params['submitApplicationDateEnd'] = submitApplicationDateEnd.getValue().getTime() + 1000 * 60 * 60 * 24 - 1;
            }
            if (registrationDateStart) {
                params['registrationDateStart'] = registrationDateStart.getValue().getTime();
            }
            if (registrationDateEnd) {
                params['registrationDateEnd'] = registrationDateEnd.getTime() + 1000 * 60 * 60 * 24 - 1;
            }
            customerList.store.proxy.extraParams = params;
            customerList.store.loadPage(1);
        }
    },
    reset: function () {
        this.down("provinceSelect").setValue(null);
        this.down("citySelect").setValue(null);
        this.down("regionSelect").setValue(null);
        this.down("provinceSelect").setRawValue(null);
        this.down("citySelect").setRawValue(null);
        this.down("regionSelect").setRawValue(null);
        this.down("[name=searchName]").setValue(null);
        this.down("[name=searchOwner]").setValue(null);
        this.down("[name=shopVersionNames]").setValue(null);
        this.down("[name=registerTypes]").setValue(null);
        this.down("[name=submitApplicationDateStart]").setValue(null);
        this.down("[name=submitApplicationDateEnd]").setValue(null);
        this.down("[name=searchAgent]").setValue(null);
        this.down("[name=locateStatuses]").setValue(null);
    }
});