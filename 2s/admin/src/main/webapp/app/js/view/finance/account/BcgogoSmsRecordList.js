Ext.define('Ext.view.finance.account.BcgogoSmsRecordList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.bcgogoSmsRecordList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    initComponent: function () {
        var me = this;
        var store = Ext.create('Ext.store.finance.BcgogoSmsRecords');
        this.store = store;
        Ext.apply(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            fieldLabel: '日期',
                            labelWidth: 60,
                            xtype: "datefield",
                            format: 'Y-m-d',
                            width: 160,
                            name: 'operateTimeStart'
                        },
                        "至",
                        {
                            xtype: "datefield",
                            width: 100,
                            format: 'Y-m-d',
                            name: 'operateTimeEnd'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '类型',
                            columns: 5,
                            labelWidth: 60,
                            width: 460,
                            items: [
                                { boxLabel: '公司充值', name: 'smsCategories', inputValue: 'BCGOGO_RECHARGE', width: 80 },
                                { boxLabel: '客户充值', name: 'smsCategories', inputValue: 'SHOP_RECHARGE', width: 80 },
                                { boxLabel: '公司消费', name: 'smsCategories', inputValue: 'BCGOGO_CONSUME', width: 80 },
                                { boxLabel: '客户消费', name: 'smsCategories', inputValue: 'SHOP_CONSUME', width: 80},
                                { boxLabel: '短信赠送', name: 'smsCategories', inputValue: 'HANDSEL', width: 80 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 10 },
                        "-",
                        {
                            text: "查询",
                            xtype: 'button',
                            action: 'search',
                            iconCls: "icon-search",
                            scope: me
                        },
                        "->",
                        {
                            text: "公司充值录入",
                            xtype: 'button',
                            action: 'bcgogoRecharge',
                            iconCls: "icon-add",
                            scope: this
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
                        if (rec.get("smsCategory") === "BCGOGO_RECHARGE" || rec.get("smsCategory") === "SHOP_RECHARGE") {
                            return Ext.util.Format.date(new Date(Number(rec.get("rechargeTime"))), 'Y-m-d H:i');
                        } else {
                            return Ext.util.Format.date(new Date(Number(rec.get("operateTime"))), 'Y-m-d H:i');
                        }
                    }
                },
                {
                    header: '分类',
                    dataIndex: 'smsCategory',
                    renderer: function (val, style, rec, index) {
                        if (val === "BCGOGO_RECHARGE") {
                            return "公司充值";
                        } else if (val === "SHOP_RECHARGE" || val === "CRM_RECHARGE") {
                            return "客户充值";
                        } else if (val === "BCGOGO_CONSUME") {
                            return "公司消费";
                        } else if (val === "SHOP_CONSUME") {
                            return "客户消费";
                        } else if (val === "REGISTER_HANDSEL") {
                            return "注册赠送";
                        } else if (val === "RECOMMEND_HANDSEL") {
                            return "推荐赠送";
                        } else if (val === "REFUND") {
                            return "短信退费";
                        } else if(val === "RECHARGE_HANDSEL") {
                            return "充值赠送";
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
    onSearch: function (callback) {
        var list = this, params = {},
            operateTimeStartCmp = list.down("[name=operateTimeStart]"),
            operateTimeEndCmp = list.down("[name=operateTimeEnd]"),
            smsCategories = list.down("checkboxgroup").getValue()['smsCategories'];
        if (operateTimeEndCmp.isValid() && operateTimeStartCmp.isValid()) {
            var startTime = operateTimeStartCmp.getValue() ? new Date(operateTimeStartCmp.getValue()).getTime() : "",
                endTime = operateTimeEndCmp.getValue() ? (new Date(operateTimeEndCmp.getValue()).getTime() + 24 * 60 * 60 * 1000 - 1) : "",
                smsCategoryArray = [], j = 0;   //一天多少秒
            params = {
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

                        } else if(smsCategories[i] == "SHOP_RECHARGE"){
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
                    } else if(smsCategories == "SHOP_RECHARGE"){
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
            callback();
        }
    }
});
