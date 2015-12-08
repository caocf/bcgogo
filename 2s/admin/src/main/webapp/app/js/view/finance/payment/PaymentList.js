Ext.define('Ext.view.finance.payment.PaymentList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.financePaymentList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    requires:[ 'Ext.app.ActionTextColumn'],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        var store = Ext.create('Ext.store.finance.BcgogoReceivableRecords');
        store.proxy.extraParams = {
            receivableStatuses: new Array("PENDING_REVIEW", "HAS_BEEN_PAID")
        };
        this.store = store;
        Ext.apply(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '销售类型',
                            columns: 3,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 270,
                            name:'paymentTypeCheckBoxGroup',
                            items: [
                                { boxLabel: '软件销售', name: 'paymentType', inputValue: 'SOFTWARE', width: 70 },
                                { boxLabel: '硬件销售', name: 'paymentType', inputValue: 'HARDWARE', width: 70 },
                                { boxLabel: '短信销售', name: 'paymentType', inputValue: 'SMS_RECHARGE', width: 70 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 20 },
                        {
                            fieldLabel: '店铺名',
                            labelAlign:'right',
                            labelWidth: 50,
                            xtype: "textfield",
                            width: 200,
                            name: 'shopName'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            fieldLabel: '销售跟进人',
                            labelAlign:'right',
                            labelWidth: 70,
                            xtype: "textfield",
                            width: 200,
                            name: 'followName'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            fieldLabel: '收款人',
                            labelAlign:'right',
                            labelWidth: 70,
                            xtype: "textfield",
                            width: 200,
                            name: 'payeeName'
                        }
                    ]
                },
                {
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
                        {
                            fieldLabel: '支付日期',
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
                            xtype: 'checkboxgroup',
                            fieldLabel: '状态',
                            columns: 3,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 200,
                            name:'receivableStatusCheckBoxGroup',
                            items: [
                                { boxLabel: '待审核', name: 'receivableStatus', inputValue: 'PENDING_REVIEW', width: 70 },
                                { boxLabel: '已入账', name: 'receivableStatus', inputValue: 'HAS_BEEN_PAID', width: 70 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '支付方式',
                            columns: 2,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 200,
                            name:'paymentMethodCheckBoxGroup',
                            items: [
                                { boxLabel: '银联', name: 'paymentMethod', inputValue: 'ONLINE_PAYMENT', width: 50 },
                                { boxLabel: '现金', name: 'paymentMethod', inputValue: 'DOOR_CHARGE', width: 50 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 50 },
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
                            xtype: 'displayfield',
                            fieldLabel: '待审核',
                            name: 'pendingReviewStat',
                            labelAlign:'right',
                            value: '<span style="color: #0000FF;">0</span>条 <span style="color: #0000FF;">0.0</span>元(银联：<span style="color: #FF6600;">0.0</span>元；现金：<span style="color: #FF6600;">0.0</span>元)',
                            labelWidth: 40,
                            margin: '0 20 0 0'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '已入账',
                            name: 'hasBeenPaidStat',
                            labelAlign:'right',
                            value: '<span style="color: #0000FF;">0</span>条 <span style="color: #0000FF;">0.0</span>元(银联：<span style="color: #FF6600;">0.0</span>元；现金：<span style="color: #FF6600;">0.0</span>元)',
                            labelWidth: 40,
                            margin: '0 20 0 0'
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
                    header: '支付日期',
                    dataIndex: 'recordPaymentTime',
                    width: 75,
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d');
                        }
                        return "";
                    }
                },
                {
                    header: '销售类型',
                    width: 60,
                    dataIndex: 'orderPaymentType',
                    renderer: function (val, style, rec, index) {
                        if (val === "SOFTWARE") {
                            return "软件销售";
                        } else if (val === "HARDWARE") {
                            return "硬件销售";
                        } else if (val === "SMS_RECHARGE") {
                            return "短信销售";
                        }
                    }
                },
                {
                    header: '费用类型',
                    width: 60,
                    dataIndex: 'receivableMethod',
                    renderer: function (val, style, rec, index) {
                        if (val === "FULL") {
                            return "全额付款";
                        } else if (val === "UNCONSTRAINED") {
                            return "其他";
                        } else if (val === "INSTALLMENT") {
                            return "分期付款";
                        }
                    }
                },
                {
                    header: '支付金额(元)',
                    width: 70,
                    dataIndex: 'recordPaidAmount',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return "￥" + Ext.util.Format.number(val, '0.00');
                        }
                    }
                },
                {
                    header: '支付方式',
                    width: 70,
                    dataIndex: 'paymentMethod',
                    renderer: function (val, style, rec, index) {
                        if (val === "DOOR_CHARGE") {
                            return "上门收取";
                        } else if (val === "ONLINE_PAYMENT") {
                            return "在线支付";
                        } else{
                            return "--";
                        }
                    }
                },
                {
                    header: '收款人',
                    width: 70,
                    dataIndex: 'payeeName',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return val;
                        } else {
                            return "--";
                        }
                    }
                },
                {
                    header: '销售单号',
                    dataIndex: 'orderReceiptNo',
                    width: 130,
                    renderer: function (val, style, rec, index) {
                        if(rec.get("orderPaymentType")==="HARDWARE"){
                            return '<div style="color: blue; cursor:pointer;white-space:normal;">' + val + '</div>';
                        }else if(rec.get("orderPaymentType")==="SOFTWARE"){
                            return '<div style="color: blue; cursor:pointer;white-space:normal;">' + val + '</div>';
                        }else if(rec.get("orderPaymentType")==="SMS_RECHARGE"){
                            return '<div style="color: blue; cursor:pointer;white-space:normal;">' + val + '</div>';
                        } else {
                            return '<div style="white-space:normal;">' + val + '</div>';
                        }

                    }
                },
                {
                    header: '销售店铺',
                    dataIndex: 'shopName',
                    width: 140,
                    renderer: function (val, style, rec, index) {
                        return '<div style="white-space:normal;">' + val + '</div>';
                    }
                },

                {
                    header: '销售跟进人',
                    width: 70,
                    dataIndex: 'followName',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return val;
                        } else {
                            return "--";
                        }
                    }

                },
                {
                    header: '财务审核',
                    width: 75,
                    dataIndex: 'auditorName',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return val;
                        } else {
                            return "--";
                        }
                    }
                },
                {
                    header: '审核日期',
                    width: 75,
                    dataIndex: 'auditTime',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d');
                        }else {
                            return "--";
                        }
                    }
                },

//                {
//                    header: '付款情况',
//                    dataIndex: 'receivableContent',
//                    width: 140,
//                    renderer: function (val, style, rec, index) {
//                        var text ="总额"+ "￥" + Ext.util.Format.number(rec.get("orderTotalAmount"), '0.00');
//                        if (rec.get("orderPaymentStatus") === "FULL_PAYMENT") {
//                            text+= "：付清";
//                        } else if (rec.get("orderPaymentStatus") === "NON_PAYMENT") {
//                            text+= "：未付";
//                        } else if (rec.get("orderPaymentStatus") === "PARTIAL_PAYMENT") {
//                            text+= "：已付￥" + Ext.util.Format.number(rec.get("orderReceivedAmount"), '0.00');
//                            text+= "；剩余￥" + Ext.util.Format.number(rec.get("orderReceivableAmount"), '0.00');
//                            text+= "<br>分期付款 第" + rec.get("periods") + "期 已付" + (Number(rec.get("periodNumber"))-1) + "期";
//                        }
//                        return '<div style="white-space:normal;">' + text + '</div>';
//                    }
//                },


                {
                    header: '状态',
                    width: 50,
                    dataIndex: 'status',
                    renderer: function (val, style, rec, index) {
                        if (val === "TO_BE_PAID") {
                            return "待支付";
                        } else if (val === "PENDING_REVIEW") {
                            return "待审核";
                        } else if (val === "HAS_BEEN_PAID") {
                            return "已入账";
                        }
                    }
                },
                {
                    xtype: 'actiontextcolumn',
                    header: '操作',
                    action:'audit',
                    width: 60,
                    items: [
                        {
                            getClass: function (v, meta, rec) {
                                if (rec.get('status') === 'PENDING_REVIEW') {
                                    this.items[0].tooltip='审核';
                                    this.items[0].text='审核';
                                    return 'payment-audit-col';
                                }else{
                                    this.items[0].tooltip='';
                                    this.items[0].text='';
                                    return '';
                                }
                            }
                        }
                    ]
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
            payeeName = list.down("[name=payeeName]").getValue(),
            followName = list.down("[name=followName]").getValue(),
            receiptNo = list.down("[name=receiptNo]").getValue(),
            paymentTypes = list.down("[name=paymentTypeCheckBoxGroup]").getValue(),
            paymentMethods = list.down("[name=paymentMethodCheckBoxGroup]").getValue(),
            receivableStatuses = list.down("[name=receivableStatusCheckBoxGroup]").getValue();
        if (startTimeStrDom.isValid() && endTimeStrDom.isValid()) {
            params = {
                shopName: shopName,
                payeeName: payeeName,
                followName: followName,
                receiptNo: receiptNo,
                receivableStatuses:new Array("PENDING_REVIEW", "HAS_BEEN_PAID"),
                startTimeStr: startTimeStrDom.getValue(),
                endTimeStr: endTimeStrDom.getValue()
            };
            if (!Ext.isEmpty(paymentTypes['paymentType'])) {
                params['paymentTypes'] = paymentTypes['paymentType'];
            }
            if (!Ext.isEmpty(paymentMethods['paymentMethod'])) {
                params['paymentMethods'] = paymentMethods['paymentMethod'];
            }
            if (!Ext.isEmpty(receivableStatuses['receivableStatus'])) {
                params['receivableStatuses'] = receivableStatuses['receivableStatus'];
            }
            list.store.proxy.extraParams = params;
            list.store.loadPage(1);
            list.statBcgogoReceivableOrderRecord(params);
        }
    },
    statBcgogoReceivableOrderRecord: function (params) {
        var list = this;
        list.commonUtils.ajax({
            params: params,
            url: 'bcgogoReceivable.do?method=statBcgogoReceivableOrderRecordByStatusResult',
            success: function (result) {
                if(result.data["PENDING_REVIEW"]){
                    list.down("[name=pendingReviewStat]").setValue(result.data["PENDING_REVIEW"]);
                }else{
                    list.down("[name=pendingReviewStat]").setValue('<span style="color: #0000FF;">0</span>条 <span style="color: #0000FF;">0.0</span>元(银联：<span style="color: #FF6600;">0.0</span>元；现金：<span style="color: #FF6600;">0.0</span>元)');
                }
                if(result.data["HAS_BEEN_PAID"]){
                    list.down("[name=hasBeenPaidStat]").setValue(result.data["HAS_BEEN_PAID"]);
                }else{
                    list.down("[name=hasBeenPaidStat]").setValue('<span style="color: #0000FF;">0</span>条 <span style="color: #0000FF;">0.0</span>元(银联：<span style="color: #FF6600;">0.0</span>元；现金：<span style="color: #FF6600;">0.0</span>元)');
                }
            }
        });
    }
});
