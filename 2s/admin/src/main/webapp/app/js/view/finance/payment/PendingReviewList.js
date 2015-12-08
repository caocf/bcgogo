Ext.define('Ext.view.finance.payment.PendingReviewList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.financePendingReviewList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true,            //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true,               //自动填充，即让所有列填充满gird宽度
    multiSelect: true,           //可以多选
    autoHeight: true,
    initComponent: function () {
        var me = this;
        var store = Ext.create('Ext.store.finance.BcgogoReceivableRecords');
        store.proxy.extraParams = {
            status: "PENDING_REVIEW"
        };
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
                            fieldLabel: '支付时间',
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
                            fieldLabel: '费用类型',
                            columns: 3,
                            labelWidth: 60,
                            width: 300,
                            items: [
                                { boxLabel: '软件购买', name: 'paymentType', inputValue: 'SOFTWARE', width: 80 },
                                { boxLabel: '硬件购买', name: 'paymentType', inputValue: 'HARDWARE', width: 80 },
                                { boxLabel: '短信充值', name: 'paymentType', inputValue: 'SMS_RECHARGE', width: 80 }
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
                        "->",
                        {
                            text: "新增软件已支付记录",
                            xtype: 'button',
                            action: 'addSoftware',
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
                    header: '店铺名',
                    dataIndex: 'shopName',
                    renderer: function (val, style, rec, index) {
                        return '<span style="color: blue; cursor:pointer;">' + val + '</span>';
                    }
                },
                {
                    header: '支付类型',
                    dataIndex: 'paymentType',
                    renderer: function (val, style, rec, index) {
                        return val == 'HARDWARE' ? "硬件购买费用" : "软件购买费用";
                    }
                },
                {
                    header: '支付方式',
                    dataIndex: 'paymentMethod',
                    renderer: function (val, style, rec, index) {
                        if (val === "DOOR_CHARGE") {
                            return "上门收取";
                        } else {
                            return "在线支付";
                        }
                    }
                },
                {
                    header: '支付金额',
                    dataIndex: 'recordPaidAmount',
                    renderer: function (val, style, rec, index) {
                        return "￥" + Ext.util.Format.number(val, '0.00');
                    }
                },
                {
                    header: '支付时间',
                    dataIndex: 'recordPaymentTime',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d H:i');
                        }
                        return "";
                    }
                },
                {
                    header: '业务员',//收取人
                    dataIndex: 'payeeName'
                },
                {
                    header: '状态',
                    dataIndex: 'status',
                    renderer: function (val, style, rec, index) {
                        if (val == "TO_BE_PAID") {
                            return "待支付";
                        } else if (val == "PENDING_REVIEW") {
                            return "待审核";
                        } else if (val == "HAS_BEEN_PAID") {
                            return "已支付";
                        }
                    }
                },
                {
                    xtype: 'actioncolumn',
                    header: '操作',
                    width: 25,
                    action: "audit",
                    items: [
                        {
                            tooltip: '审核',
                            scope: me,
                            icon: 'app/images/icons/user_edit.png'
                        }
                    ]
                }
            ]
        });
        this.callParent(arguments);
    },
    onSearch: function () {
        var list = this, params = {},
            operateTimeStartCmp = list.down("[name=operateTimeStart]"),
            operateTimeEndCmp = list.down("[name=operateTimeEnd]"),
            paymentTypes = list.down("checkboxgroup").getValue();
        if (operateTimeEndCmp.isValid() && operateTimeStartCmp.isValid()) {
            var shopName = list.down("[name=shopName]").getValue(),
                startTime = operateTimeStartCmp.getValue() ? new Date(operateTimeStartCmp.getValue()).getTime() : "",
                endTime = operateTimeEndCmp.getValue() ? (new Date(operateTimeEndCmp.getValue()).getTime() + 24 * 60 * 60 * 1000 - 1) : "";   //一天多少秒
            params = {
                shopName: shopName,
                startTime: startTime,
                status: "PENDING_REVIEW",
                endTime: endTime
            };
            if (!Ext.isEmpty(paymentTypes['paymentType'])) {
                params['paymentTypes'] = paymentTypes['paymentType'];
            }
            list.store.proxy.extraParams = params;

            list.store.loadPage(1);
        }
    }

});
