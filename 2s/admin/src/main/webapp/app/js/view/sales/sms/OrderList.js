/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-24
 * Time: 上午11:09
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.sales.sms.OrderList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.salesSmsOrderList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    requires: ['Ext.app.ActionTextColumn'],

    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        var store = Ext.create('Ext.store.sales.SmsRechargeOrders');
        this.store = store;
        Ext.apply(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            fieldLabel: '充值时间',
                            labelWidth: 60,
                            labelAlign:'right',
                            xtype: "datefield",
                            format: 'Y-m-d',
                            width: 160,
                            name: 'startTimeStr'
                        },
                        "至",
                        {
                            xtype: "datefield",
                            width: 100,
                            format: 'Y-m-d',
                            name: 'endTimeStr'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            fieldLabel: '店铺名',
                            labelAlign:'right',
                            labelWidth: 60,
                            xtype: "textfield",
                            width: 200,
                            name: 'shopName'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '销售来源',
                            columns: 2,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 170,
                            name:'rechargeMethodGroup',
                            items: [
                                { boxLabel: '客户充值', name: 'rechargeMethod', inputValue: 'CUSTOMER_RECHARGE', width: 80 },
                                { boxLabel: '后台充值', name: 'rechargeMethod', inputValue: 'CRM_RECHARGE', width: 80 }
                            ]
                        }
                    ]
                },{
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            fieldLabel: '销售单号',
                            labelAlign:'right',
                            labelWidth: 60,
                            xtype: "textfield",
                            width: 200,
                            name: 'receiptNo'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '状态',
                            columns: 2,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 180,
                            name:'paymentStatusCheckBoxGroup',
                            items: [
                                { boxLabel: '待审核', name: 'paymentStatus', inputValue: 'PENDING_REVIEW', width: 60 },
                                { boxLabel: '已入账', name: 'paymentStatus', inputValue: 'HAS_BEEN_PAID', width: 60 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 10 },
                        ,
                        "->",
                        {
                            text: "查询",
                            xtype: 'button',
                            action: 'search',
                            iconCls: "icon-search",
                            scope: me,
                            handler: function () {
                                me.onSearch();
                            }
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
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'displayfield',
                            name: 'paymentStat',
                            labelAlign:'right',
                            value: '共有记录<span style="color: #0000FF;"> 0 </span>条 共计<span style="color: #008000;">0.0</span>元(银联<span style="color: #000000;">0.0</span>元；现金<span style="color: #000000;">0.0</span>元)',
                            labelWidth: 40,
                            margin: '0 20 0 0'
                        },
                        "->",
                        {
                            text: "新增短信充值",
                            xtype: 'button',
                            action: 'add',
                            iconCls: "icon-add",
                            scope: this,
                             handler: function () {
                                 me.reset();
                             }
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
                    width: 30
                },
                {
                    header: '销售单号',
                    width: 120,
                    dataIndex: 'receiptNo',
                    renderer: function (val, style, rec, index) {
                        return '<span style="color: blue; cursor:pointer;">' + val + '</span>';
                    }

                },
                {
                    header: '店铺',
                    width: 180,
                    dataIndex: 'shopName',
                    renderer: function (val, style, rec, index) {
                        if(val){
                            return "<span style='white-space:normal;'>"+val+"</span>";
                        }
                    }
                },
                {
                    header: '充值时间',
                    width: 120,
                    dataIndex: 'payTime',
                    renderer: function (val, style, rec, index) {
                        if(val){
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d H:i');
                        }
                    }
                },
                {
                    header: '销售来源',
                    width: 80,
                    dataIndex: 'rechargeMethod',
                    renderer: function (val, style, rec, index) {
                        if(val === 'CUSTOMER_RECHARGE') {
                            return "客户充值";
                        } else if(val === 'CRM_RECHARGE') {
                            return "后台充值";
                        }
                    }
                },
                {
                    header: '销售内容',
                    width: 100,
                    dataIndex: 'rechargeAmount',
                    renderer: function (val, style, rec, index) {
                        return "充值" + val + '元';
                    }
                },
                {
                    header: '优惠',
                    width: 100,
                    dataIndex: 'presentAmount',
                    renderer: function (val, style, rec, index) {
                        if(val && val > 0) {
                            return "送" + val + '元';
                        } else {
                            return "--";
                        }

                    }
                },
                {
                    header: '应付金额（元）',
                    width: 80,
                    dataIndex: 'rechargeAmount'
                },
                {
                    header: '支付方式',
                    width: 80,
                    dataIndex: 'paymentWay',
                    renderer: function (val, style, rec, index) {
                        if(val === 'CHINA_PAY') {
                            return "银联";
                        } else if(val === 'CASH') {
                            return "现金";
                        }
                    }
                },
                {
                    header: '实付金额（元）',
                    width: 120,
                    dataIndex: 'rechargeAmount'
                },
                {
                    header: '状态',
                    width: 70,
                    dataIndex: 'status',
                    renderer: function (val, style, rec, index) {
                       if(val === 'PENDING_REVIEW') {
                           return "待审核";
                       } else if(val === 'HAS_BEEN_PAID'){
                          return "已入账";
                       }

                    }
                }
            ]
        });

        this.callParent(arguments);
    },

    onSearch: function () {
        var list = this, params = {},
            startTimeStrDom = list.down("[name=startTimeStr]"),
            endTimeStrDom = list.down("[name=endTimeStr]"),
            shopName = list.down("[name=shopName]").getValue(),
            receiptNo = list.down("[name=receiptNo]").getValue(),
            rechargeMethods = list.down("[name=rechargeMethodGroup]").getValue(),
            statuses = list.down("[name=paymentStatusCheckBoxGroup]").getValue();
        if (startTimeStrDom.isValid() && endTimeStrDom.isValid()) {
            params = {
                shopName: shopName,
                receiptNo: receiptNo,
                startTimeStr: startTimeStrDom.getValue(),
                endTimeStr: endTimeStrDom.getValue()
            };
            if(!Ext.isEmpty(rechargeMethods["rechargeMethod"])) {
                params["rechargeMethods"] = rechargeMethods["rechargeMethod"];
            }
            if(!Ext.isEmpty(statuses["paymentStatus"])) {
                params["statuses"] = statuses["paymentStatus"];
            }
            list.store.proxy.extraParams = params;
            list.store.loadPage(1, {callback: function (record, operation, success) {}});
            list.statBcgogoReceivableOrder(params);
        }
    },
    statBcgogoReceivableOrder: function (params) {
        var list = this;
        list.commonUtils.ajax({
            params: params,
            url: 'shopSmsAccount.do?method=statSmsRechargeByPaymentWay',
            success: function (result) {
               if(result && result.success && result.data && result.data["result"]) {
                  list.down("[name=paymentStat]").setValue(result.data["result"]);
               } else {
                   list.down("[name=paymentStat]").setValue(' 共有记录<span style="color: #0000FF;"> 0 </span>条 共计<span style="color: #008000;">0.0</span>元(银联<span style="color: #000000;">0.0</span>元；现金<span style="color: #000000;">0.0</span>元)');

               }
            }
        });
    },
    reset: function () {
        this.down("[name=startTimeStr]").setValue(null);
        this.down("[name=endTimeStr]").setValue(null);
        this.down("[name=shopName]").setValue(null);
        this.down("[name=receiptNo]").setValue(null);
        this.down("[name=rechargeMethodGroup]").setValue(null);
        this.down("[name=paymentStatusCheckBoxGroup]").setValue(null);
    }
});
