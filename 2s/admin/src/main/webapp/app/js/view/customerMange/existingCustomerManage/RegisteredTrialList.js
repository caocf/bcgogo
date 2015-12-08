/**
 * @author zhangjuntao
 * 客户管理-已注册客户
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.RegisteredTrialList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.shopRegisteredTrialList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    viewConfig: {
        forceFit: true,
        getRowClass: function (record, rowIndex, rowParams, store) {
            //禁用数据显示红色
            if (record.get("shopState") == "OVERDUE") {
                return 'x-grid-record-red';
            }
            return '';
        }
    },
    requires: [
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.ProvinceSelect",
        "Ext.view.customerMange.RegisterType"
    ],
    initComponent: function () {
        var me = this, store = Ext.create('Ext.store.customerMange.Shops'),
            container, shopVersions,
            userSelect = Ext.widget("userSelect", {
                name: 'searchAgent',
                emptyText: "销售跟进人",
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
                    dock: 'top',
                    items: [
                        {
                            xtype: "shopSelect",
                            emptyText: "店铺名",
                            width: 100,
                            store: Ext.create('Ext.store.customerMange.Shops', {
                                pageSize: 15,
                                proxy: {
                                    extraParams: {
                                        shopStatuses: "REGISTERED_TRIAL"
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
                            valueField: 'name',
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
                        userSelect
//                        ,
//                        {
//                            xtype: "textfield",
//                            emptyText: "销售跟进人",
//                            width: 100,
//                            name: 'searchAgent'
//                        },
//                        { xtype: 'tbspacer', width: 10 },
//                        {
//                            xtype: 'checkboxgroup',
//                            fieldLabel: '议价状态',
//                            columns: 4,
//                            labelWidth: 55,
//                            name: 'bargainStatuses',
//                            width: 370,
//                            items: [
//                                {boxLabel: '无议价', name: 'bargainStatus', inputValue: 'NO_BARGAIN', width: 60},
//                                {boxLabel: '待审核', name: 'bargainStatus', inputValue: 'PENDING_REVIEW', width: 60},
//                                {boxLabel: '审核拒绝', name: 'bargainStatus', inputValue: 'AUDIT_REFUSE', width: 90},
//                                {boxLabel: '审核通过', name: 'bargainStatus', inputValue: 'AUDIT_PASS', width: 90}
//                            ]
//                        }
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
                            fieldLabel: '账户状态',
                            columns: 2,
                            labelWidth: 55,
                            margin:'0 0 0 30',
                            name: 'shopStates',
                            width: 160,
                            items: [
                                {boxLabel: '试用中', name: 'shopState',checked:true, inputValue: 'ACTIVE', width: 60},
                                {boxLabel: '过期禁用', name: 'shopState', checked:true,inputValue: 'OVERDUE', width: 70}
                            ]
                        } ,
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '定位状态',
                            columns: 2,
                            labelWidth: 55,
                            margin:'0 0 0 60',
                            name: 'locateStatuses',
                            width: 170,
                            items: [
                                {boxLabel: '已定位', name: 'locateStatus', inputValue: 'ACTIVE', width: 55},
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
                    columns: 8,
                    labelWidth: 30,
                    name: 'shopVersionNames',
                    width: 730,
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
                        },
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
                            text: '编辑',
                            xtype: 'button',
                            tooltip: '编辑',
                            action: 'edit',
                            scope: this,
                            iconCls: 'icon-edit',
                            disabled: true
                        },
                        {
                            text: '延期',
                            xtype: 'button',
                            iconCls: 'icon-extension',
                            action: 'extension',
                            scope: this,
                            disabled: true
                        },
                        '-',
                        {
                            text: '定位',
                            xtype: 'button',
                            iconCls: 'icon-locate',
                            action: 'locate',
                            scope: this,
                            disabled: true
                        }
//                        ,
//                        {
//                            text: '议价审核',
//                            xtype: 'button',
//                            iconCls: 'icon-audit-bargain',
//                            action: 'auditBargain',
//                            disabled: true,
//                            scope: this
//                        },
//                        {
//                            text: '议价申请',
//                            xtype: 'button',
//                            iconCls: 'icon-apply-bargain',
//                            action: 'applyBargain',
//                            disabled: true,
//                            scope: this
//                        }
                    ]
                });
            }
        });
        Ext.apply(me, {
            store: store,
            tbar: container,
            dockedItems: {
                dock: 'bottom',
                xtype: 'pagingtoolbar',
                store: store,
                displayInfo: true
            },
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
                    header: '销售跟进人',
                    dataIndex: 'followName'/*,
                 renderer: function (val, style, rec, index) {
                 if (rec.get("registerType") == "SUPPLIER_INVITE" || rec.get("registerType") == "CUSTOMER_INVITE" || rec.get("registerType") == "SUPPLIER_REGISTER") {
                 return rec.get("agent");
                 }
                 return val;
                 }*/
                },
                {
                    header: '软件版本',
                    dataIndex: 'shopVersionName'
                },
                {
                    header: '账户状态',
                    dataIndex: 'shopState',
                    renderer: function (val, style, rec, index) {
                        if (val == "ACTIVE") {
                            return "试用中";
                        } else if (val == "ARREARS") {
                            return "欠费禁用";
                        } else if (val == "OVERDUE") {
                            return "过期禁用";
                        } else {
                            try {
                                console.log(val)
                            } catch (e) {
                                ;
                            }
                            return "--";
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
                    header: '试用截止时间',
                    dataIndex: 'trialEndTime',
                    renderer: function (val, style, rec, index) {
                        return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "";
                    }
                }
//                ,
//                {
//                    header: '议价状态',
//                    dataIndex: 'bargainStatus',
//                    renderer: function (val, style, rec, index) {
//                        if (val == "PENDING_REVIEW") {
//                            return "待审核";
//                        } else if (val == "AUDIT_REFUSE") {
//                            return "审核拒绝";
//                        } else if (val == "AUDIT_PASS") {
//                            return "审核通过";
//                        } else {
//                            return "无议价";
//                        }
//                    }
//                },
//                {
//                    header: '议价',
//                    dataIndex: 'bargainPrice',
//                    renderer: function (val, style, rec, index) {
//                        if (rec.get("bargainStatus") == "NO_BARGAIN" || !val) {
//                            return "--";
//                        }
//                        return "￥" + val;
//                    }
//                }
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
        var customerList = this, params, registerTypeArray = [],
            j = 0,
            registerTypes = customerList.down("[name=registerTypes]").getValue()['registerType'],
            locateStatuses = customerList.down("[name=locateStatuses]").getValue()['locateStatus'],
            shopStates = customerList.down("[name=shopStates]").getValue()['shopState'],
//            bargainStatuses = customerList.down("[name=bargainStatuses]").getValue()['bargainStatus'],
            province = customerList.down("[name=province]").getValue(),
            agent = customerList.down("[name=searchAgent]").getValue(),
            city = customerList.down("[name=city]").getValue(),
            region = customerList.down("[name=region]").getValue(),
            name = customerList.down("[name=searchName]").getValue(),
            owner = customerList.down("[name=searchOwner]").getValue();
        params = {
            region: region,
            city: city,
            province: province,
            followName: agent,
            name: name,
            owner: owner,
            scene: "REGISTERED_TRAIL",
            shopStatuses: ["REGISTERED_TRIAL"]
        };
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
//        if (bargainStatuses) {
//            params['bargainStatuses'] = bargainStatuses;
//        }
        if (locateStatuses && locateStatuses.length > 0) {
            params['locateStatuses'] = locateStatuses;
        }
        if (customerList.down("[name=shopVersionNames]")) {
            var shopVersionNames = customerList.down("[name=shopVersionNames]").getValue()['shopVersionName'];
            if (shopVersionNames) {
                params['shopVersionName'] = shopVersionNames;
            }
            if (shopStates) {
                params['shopStates'] = shopStates;
            }
        }
        customerList.store.proxy.extraParams = params;
        customerList.store.loadPage(1);
//        customerList.statistics();
    },
    statistics: function () {
        var list = this;
        list.commonUtils.ajax({
            url: 'shopManage.do?method=shopRegisteredStatistics',
            params: {shopStatus: "REGISTERED_TRIAL"},
            success: function (result) {
                if (result["success"]) {
                    list.down("[name=statistics]").setValue(result["data"]);
                } else {
                    list.down("[name=statistics]").setValue("");
                }
            }
        });
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
//        this.down("[name=bargainStatuses]").setValue(null);
        this.down("[name=shopStates]").setValue(null);
        this.down("[name=searchAgent]").setValue(null);
        this.down("[name=locateStatuses]").setValue(null);
    }
});