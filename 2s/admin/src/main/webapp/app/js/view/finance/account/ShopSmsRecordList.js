Ext.define('Ext.view.finance.account.ShopSmsRecordList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.shopSmsRecordList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    initComponent: function () {
        var me = this;
        var store = Ext.create('Ext.store.finance.ShopSmsRecords');
        me.commonUtils = Ext.create("Ext.utils.Common");
        this.store = store;
        Ext.apply(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            fieldLabel: '店铺名',
                            labelWidth: 50,
                            xtype: "textfield",
                            width: 200,
                            name: 'shopName'
                        },
                        {
                            fieldLabel: '日期',
                            labelWidth: 60,
                            xtype: "datefield",
                            format: 'Y-m-d',
                            width: 160,
                            name: 'startTime'
                        },
                        "至",
                        {
                            xtype: "datefield",
                            width: 100,
                            format: 'Y-m-d',
                            name: 'endTime'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '分类',
                            columns: 4,
                            labelWidth: 60,
                            width: 380,
                            items: [
                                { boxLabel: '短信充值', name: 'smsCategories', inputValue: 'SHOP_RECHARGE', width: 80 },
                                { boxLabel: '短信赠送', name: 'smsCategories', inputValue: 'HANDSEL', width: 80 },
                                { boxLabel: '短信消费', name: 'smsCategories', inputValue: 'SHOP_CONSUME', width: 80},
                                { boxLabel: '短信退费', name: 'smsCategories', inputValue: 'REFUND', width: 80 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 10 },
                        "-",
                        {
                            text: "查询",
                            xtype: 'button',
                            action: 'search',
                            iconCls: "icon-search",
                            scope: me,
                            handler: function () {
                                me.onSearch();
                            }
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            name: 'statistics',
                            xtype: "displayfield",
                            value: '短信充值0元/0条；短信赠送0元/0条；短信消费 0元/0条；'
                        }
                    ]
                },
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
                    header: '日期',
                    dataIndex: 'operateTime',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d H:i');
                        }
                        return "";
                    }
                },
                {
                    header: '店铺名',
                    dataIndex: 'shopName'
                },
                {
                    header: '分类',
                    dataIndex: 'smsCategory',
                    renderer: function (val, style, rec, index) {
                         if (val === "SHOP_RECHARGE" || val === "CRM_RECHARGE") {
                            return "短信充值";
                        }else if (val === "SHOP_CONSUME") {
                            return "短信消费";
                        } else if (val === "REGISTER_HANDSEL") {
                            return "注册赠送";
                        } else if (val === "RECOMMEND_HANDSEL") {
                            return "推荐赠送";
                        }else if (val === "REFUND") {
                            return "短信退费";
                        } else if(val === "RECHARGE_HANDSEL") {
                            return "充值赠送"
                        }
                        return "--";
                    }
                },
                {
                    header: '金额',
                    dataIndex: 'balance',
                    renderer: function (val, style, rec, index) {
                        return "￥" + Ext.util.Format.number(val, '0.00');
                    }
                },
                {
                    header: '条数',
                    dataIndex: 'number'
                }
            ]
        });
        this.callParent(arguments);
    },
    onSearch: function () {
        var list = this, params = {},
            startTimeComp = list.down("[name=startTime]"),
            endTimeComp = list.down("[name=endTime]"),
            shopName = list.down("[name=shopName]").getValue(),
            smsCategories = list.down("checkboxgroup").getValue()['smsCategories'];
        if (endTimeComp.isValid() && startTimeComp.isValid()) {
            var startTime = startTimeComp.getValue() ? new Date(startTimeComp.getValue()).getTime() : "",
                endTime = endTimeComp.getValue() ? (new Date(endTimeComp.getValue()).getTime() + 24 * 60 * 60 * 1000 - 1) : "",
                smsCategoryArray = [], j = 0;
            params = {
                shopName: shopName,
                startTime: startTime,
                endTime: endTime
            };
            if (smsCategories) {
                if (smsCategories instanceof Array) {
                    for (var i = 0; i < smsCategories.length; i++) {
                        if (smsCategories[i] == "HANDSEL") {
                            smsCategoryArray[j++] = "REGISTER_HANDSEL";
                            smsCategoryArray[j++] = "RECOMMEND_HANDSEL";
                            smsCategoryArray[j++] = "RECHARGE_HANDSEL";
                        } else if(smsCategories[i] == "SHOP_RECHARGE") {
                            smsCategoryArray[j++] = "SHOP_RECHARGE";
                            smsCategoryArray[j++] = "CRM_RECHARGE";
                        } else {
                            smsCategoryArray[j++] = smsCategories[i];
                        }
                    }
                } else {
                    if (smsCategories == "HANDSEL") {
                        smsCategoryArray[j++] = "REGISTER_HANDSEL";
                        smsCategoryArray[j++] = "RECOMMEND_HANDSEL";
                        smsCategoryArray[j++] = "RECHARGE_HANDSEL";
                    } else if(smsCategories == "SHOP_RECHARGE") {
                        smsCategoryArray[j++] = "SHOP_RECHARGE";
                        smsCategoryArray[j++] = "CRM_RECHARGE";
                    } else {
                        smsCategoryArray[j++] = smsCategories;
                    }
                }
                params['smsCategories'] = smsCategoryArray;
            }
            list.store.proxy.extraParams = params;
            list.store.loadPage(1);
            list.statistics(params);
        }
    },
    statistics: function (params) {
        var list = this;
        list.commonUtils.ajax({
            params: params,
            url: 'shopSmsAccount.do?method=shopSmsRecordStatistics',
            success: function (result) {
                list.down("[name=statistics]").setValue(result["data"]);
            }
        });
    }
});
