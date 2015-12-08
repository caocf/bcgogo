/**
 * @author zhangjuntao
 * 客户管理-已注册客户
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.RegisteredList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.existingCustomerManageRegisteredList',
//    store:Ext.create('Ext.store.customerMange.Shops'),
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: false, //可以多选
    autoHeight: true,
    viewConfig: {
        forceFit: true,
        getRowClass: function (record, rowIndex, rowParams, store) {
            //禁用数据显示红色
            if (record.get("shopState") == 'IN_ACTIVE' || record.get("shopState") == 'ARREARS') {
                return 'x-grid-record-red';
            } else {
                return '';
            }
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
                    shadow: 'drop',
                    items: [
                        {
                            xtype: "shopSelect",
                            emptyText: "店铺名",
                            width: 100,
                            store: Ext.create('Ext.store.customerMange.Shops', {
                                pageSize: 15,
                                proxy: {
                                    extraParams: {
                                        shopStatuses: "REGISTERED_PAID"
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
                        userSelect,
//                        {
//                            xtype: "textfield",
//                            emptyText: "销售跟进人",
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
                            name: 'registrationDateStart'
                        },
                        "至",
                        {
                            xtype: "datefield",
                            emptyText: "结束",
                            width: 100,
                            format: 'Y-m-d',
                            name: 'registrationDateEnd'
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    items: [
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '缴费状态',
                            columns: 3,
                            labelWidth: 60,
                            name: 'paymentStatuses',
                            width: 230,
                            items: [
                                {boxLabel: '全额缴费', name: 'paymentStatus', checked:true,inputValue: 'FULL_PAYMENT', width: 80},
                                {boxLabel: '部分缴费', name: 'paymentStatus', checked:true,inputValue: 'PARTIAL_PAYMENT', width: 80}
                            ]
                        },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '账户状态',
                            columns: 3,
                            labelWidth: 60,
                            name: 'shopStates',
                            width: 300,
                            items: [
                                {boxLabel: '正常使用', name: 'shopState', inputValue: 'ACTIVE', width: 80},
                                {boxLabel: '欠费禁用', name: 'shopState', inputValue: 'ARREARS', width: 80},
                                {boxLabel: '强制禁用', name: 'shopState', inputValue: 'IN_ACTIVE', width: 80}
                            ]
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
                            fieldLabel: '定位状态',
                            columns: 2,
                            labelWidth: 55,
                            margin:'0 0 0 30',
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
                            tooltip: '编辑店铺',
                            action: 'edit',
                            scope: this,
                            iconCls: 'icon-edit',
                            disabled: true
                        },
                        {
                            text:'启用',
                            xtype:'button',
                            tooltip:'开启店铺',
                            iconCls:'icon-enable',
                            action:'enable',
                            scope:this,
                            disabled:true
                        },
                        '-',
                        {
                            text:'禁用',
                            xtype:'button',
                            tooltip:'禁用店铺',
                            iconCls:'icon-disable',
                            action:'disable',
                            disabled:true,
                            scope:this
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
                    ]
                });
            }
        });

        Ext.apply(me, {
            tbar: container,
            dockedItems: {
                dock: 'bottom',
                xtype: 'pagingtoolbar',
                store: store,
                displayInfo: true
            },
            store: store,
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
                    header: '缴费状态',
                    dataIndex: 'paymentStatus',
                    renderer: function (val, style, rec, index) {
                        if (val == "FULL_PAYMENT") {
                            return "全额缴费";
                        } else if (val == "PARTIAL_PAYMENT") {
                            return "部分缴费";
                        }
                        return "未缴费";
                    }
                },
                {
                    header: '注册时间',
                    dataIndex: 'registrationDate',
                    renderer: function (val, style, rec, index) {
                        return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "";
                    }
                },
                {
                    header: '账户状态',
                    dataIndex: 'shopState',
                    renderer: function (val, style, rec, index) {
                        if (val == "IN_ACTIVE") {
                            return "强制禁用";
                        } else if (val == "ARREARS") {
                            return "欠费禁用";
                        } else if (val == "OVERDUE") {
                            return "过期使用";
                        } else if (val == "ACTIVE") {
                            return "正常使用";
                        }
                        return "--";
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
                    header: '使用截止时间',
                    dataIndex: 'usingEndTime',
                    renderer: function (val, style, rec, index) {
                        return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "--";
                    }
                }
//                {
//                    xtype: 'actioncolumn',
//                    name: "edit",
//                    header: '编辑',
//                    width: 35,
//                    items: [
//                        {
//                            tooltip: '编辑',
//                            scope: me,
//                            icon: 'app/images/icons/edit.png'
//                        }
//                    ]
//                },
//                {
//                    xtype: 'actioncolumn',
//                    header: '操作',
//                    name: "disable",
//                    width: 35,
//                    items: [
//                        {
//                            tooltip: '',
//                            getClass: function (v, meta, rec) {          // Or return a class from a function
//                                if (rec.get('shopStatus') == "REGISTERED_PAID") {
//                                    if (rec.get('shopState') == 'IN_ACTIVE') {
//                                        this.items[0].tooltip = '已禁用';
//                                        this.items[0].src = 'app/images/icons/accept.gif';
//                                        return 'enable-col';
//                                    } else {
//                                        this.items[0].tooltip = '已启用';
//                                        this.items[0].src = 'app/images/icons/delete.png';
//                                        return 'disable-col';
//                                    }
//                                } else {
//                                    this.items[0].tooltip = '试用版';
//                                    this.items[0].src = 'app/images/icons/Ascending.png';
//                                    return 'updates-col';
//                                }
//                            }
//                        }
//                    ]
//                }
            ]
        });
        this.callParent(arguments);
    },
    onSearch: function () {
        var params, submitApplicationDateStart, submitApplicationDateEnd ,
            customerList = this, registerTypeArray = [], j = 0,
            shopStates = customerList.down("[name=shopStates]").getValue()['shopState'],
            locateStatuses = customerList.down("[name=locateStatuses]").getValue()['locateStatus'],
            registerTypes = customerList.down("[name=registerTypes]").getValue()['registerType'],
            paymentStatuses = customerList.down("[name=paymentStatuses]").getValue()['paymentStatus'],
            registrationDateStart = customerList.down("[name=registrationDateStart]").getValue(),
            registrationDateEnd = customerList.down("[name=registrationDateEnd]").getValue(),
            registerType = customerList.down("[name=registerType]").getValue(),
            province = customerList.down("[name=province]").getValue(),
            city = customerList.down("[name=city]").getValue(),
            region = customerList.down("[name=region]").getValue(),
            name = customerList.down("[name=searchName]").getValue(),
            owner = customerList.down("[name=searchOwner]").getValue(),
            agent = customerList.down("[name=searchAgent]").getValue();
        params = {
            region: region,
            city: city,
            province: province,
            name: name,
            followName: agent,
            owner: owner,
            scene: "REGISTERED",
            shopStatuses: ["REGISTERED_PAID"]
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
        if (paymentStatuses) {
            params['paymentStatus'] = paymentStatuses;
        }
        if (locateStatuses && locateStatuses.length > 0) {
            params['locateStatuses'] = locateStatuses;
        }
        if (customerList.down("[name=shopVersionNames]")) {
            var shopVersionNames = customerList.down("[name=shopVersionNames]").getValue().shopVersionName;
            if (shopVersionNames) {
                params['shopVersionName'] = shopVersionNames;
            }
        }
        if (submitApplicationDateStart) {
            params['submitApplicationDateStart'] = submitApplicationDateStart.getTime();
        }
        if (submitApplicationDateEnd) {
            params['submitApplicationDateEnd'] = submitApplicationDateEnd.getTime() + 1000 * 60 * 60 * 24 - 1;
        }
        if (registrationDateStart) {
            params['registrationDateStart'] = registrationDateStart.getTime();
        }
        if (registrationDateEnd) {
            params['registrationDateEnd'] = registrationDateEnd.getTime() + 1000 * 60 * 60 * 24 - 1;
        }
        if (shopStates) {
            params['shopStates'] = shopStates;
        }
        customerList.store.proxy.extraParams = params;
        customerList.store.loadPage(1);
//        customerList.statistics();
    },
    statistics: function () {
        var list = this;
        list.commonUtils.ajax({
            url: 'shopManage.do?method=shopRegisteredStatistics',
            params: {shopStatus: "REGISTERED_PAID"},
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
        this.down("[name=shopStates]").setValue(null);
        this.down("[name=registrationDateStart]").setValue(null);
        this.down("[name=registrationDateEnd]").setValue(null);
        this.down("[name=paymentStatuses]").setValue(null);
        this.down("[name=locateStatuses]").setValue(null);
        this.down("[name=searchAgent]").setValue(null);
//                this.down("[name=bargainStatuses]").setValue(null);
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
    }
});