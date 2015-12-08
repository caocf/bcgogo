Ext.define('Ext.view.finance.account.HardwareSoftwareAccountList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.hardwareSoftwareAccountList',
    store: 'Ext.store.finance.HardwareSoftwareAccounts',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: false, //可以多选
    autoHeight: true,
    collapsible: true,
    animCollapse: false,
    columns: [
        {
            header: 'No.',
            xtype: 'rownumberer',
            sortable: false,
            width: 25
        },
        {
            header: '店铺名',
            dataIndex: 'shopName'
        },
        {
            text: '硬件费用',
            columns: [
                {
                    header: '已收',
                    dataIndex: 'hardwareReceivedAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '挂账',
                    dataIndex: 'hardwareReceivableAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '小计',
                    dataIndex: 'hardwareTotalAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                }
            ]
        } ,
        {
            text: '软件费用',
            columns: [
                {
                    header: '已收',
                    dataIndex: 'softwareReceivedAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '挂账',
                    dataIndex: 'softwareReceivableAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '小计',
                    dataIndex: 'softwareTotalAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                }
            ]
        },
        {
            text: '费用合计',
            columns: [
                {
                    header: '已收',
                    dataIndex: 'totalReceivedAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '挂账',
                    dataIndex: 'totalReceivableAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '合计',
                    dataIndex: 'totalAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                }
            ]
        }
    ],
    plugins: [
        {
            ptype: 'rowexpander',
            rowBodyTpl: new Ext.XTemplate(
                '<table cellpadding="0" cellspacing="0" style="border:1px solid #bfbfbf; width:100%; table-layout:fixed; border-collapse: collapse;">',
                '<tpl for="orders">',
                '<tr style=" border:1px solid #bfbfbf; border-left:none;border-right:none;">',
                '<td style=" border:1px solid #bfbfbf;width:10%; border-left:none;">{buyingExpense}</td>',
                '<td style=" border:1px solid #bfbfbf;width:25%;">{receivableContent}</td>',
                '<td style=" border:1px solid #bfbfbf;width:5%;">￥{totalAmount}</td>',
                '<td style=" border:1px solid #bfbfbf;width:8%;">{receivableMethodDetail}</td>',
                '<td style=" border:1px solid #bfbfbf;width:28%;">{paymentDetailInfo}</td>',
                '<td style=" border:1px solid #bfbfbf;width:20%;border-right:none;">{auditDetailInfo}</td>',
                '</tr>',
                '<tpl for="records">',
                '<tr>',
                '<td colspan="2"></td>',
                '<td>{amountDetail}</td>',
                '<td>{receivableMethodDetail}</td>',
                '<td>{paymentDetailInfo}</td>',
                '<td>{auditDetailInfo}</td>',
                '</tr>',
                '</tpl>',
                '</tpl>',
                '</table>', {
                    isToBePaid: function (status) {
                        return status == "TO_BE_PAID";
                    }
                }
            )
        }
    ],
    enableColumnResize: true,
    requires: [
        'Ext.ux.RowExpander'
    ],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
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
                        { xtype: 'tbspacer', width: 20 },
                        {
                            xtype: 'radiogroup',
                            fieldLabel: '是否挂账',
                            columns: 3,
                            name: 'havePayable',
                            labelWidth: 60,
                            width: 300,
                            items: [
                                { boxLabel: '全部', name: 'paymentType', inputValue: null, width: 50 },
                                { boxLabel: '有挂账', name: 'paymentType', inputValue: true, width: 60 },
                                { boxLabel: '无挂账', name: 'paymentType', inputValue: false, width: 60 }
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
                            xtype: 'displayfield',
                            fieldLabel: '费用总计',
                            name: 'totalAmount',
                            value: '￥0.0',
                            labelWidth: 80,
                            margin: '0 20 0 0'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '已收',
                            name: 'totalReceivedAmount',
                            value: '￥0.0（现金￥0.0；银联￥0.0）',
                            labelWidth: 35,
                            margin: '0 20 0 0'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '挂账',
                            value: '￥0.0',
                            name: 'totalReceivableAmount',
                            labelWidth: 35
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '软件费用总计',
                            name: 'softwareTotalAmount',
                            value: '￥0.0',
                            labelWidth: 80,
                            margin: '0 20 0 0'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '已收',
                            name: 'softwareReceivedAmount',
                            value: '￥0.0（现金￥0.0；银联￥0.0）',
                            labelWidth: 35,
                            margin: '0 20 0 0'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '挂账',
                            name: 'softwareReceivableAmount',
                            value: '￥0.0',
                            labelWidth: 35,
                            margin: '0 20 0 0'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '硬件费用总计',
                            name: 'hardwareTotalAmount',
                            value: '￥0.0',
                            labelWidth: 80,
                            margin: '0 20 0 0'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '已收',
                            name: 'hardwareReceivedAmount',
                            value: '￥0.0（现金￥0.0；银联￥0.0）',
                            labelWidth: 35,
                            margin: '0 20 0 0'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '挂账',
                            name: 'hardwareReceivableAmount',
                            value: '￥0.0',
                            labelWidth: 35
                        }
                    ]
                },
                {
                    dock: 'bottom',
                    xtype: 'pagingtoolbar',
                    store: 'Ext.store.finance.HardwareSoftwareAccounts',
                    displayInfo: true
                }
            ]
        });
        this.callParent(arguments);
    },
    onSearch: function () {
        var list = this, params = {},
            havePayable = list.down("radiogroup").getValue();
        var shopName = list.down("[name=shopName]").getValue();
        params = {
            shopName: shopName,
            havePayable: havePayable
        };
        list.store.proxy.extraParams = params;
        list.store.loadPage(1);
        list.countHardwareSoftwareAccount(params);
    },
    countHardwareSoftwareAccount: function (params) {
        var list = this;
        list.commonUtils.ajax({
            params: params,
            url: 'bcgogoAccount.do?method=countHardwareSoftwareAccount',
            success: function (result) {
                list.down("[name=hardwareReceivedAmount]").setValue(result["hardwareReceivedAmount"]);
                list.down("[name=hardwareReceivableAmount]").setValue(result["hardwareReceivableAmount"]);
                list.down("[name=hardwareTotalAmount]").setValue(result["hardwareTotalAmount"]);
                list.down("[name=softwareReceivableAmount]").setValue(result["softwareReceivableAmount"]);
                list.down("[name=softwareReceivedAmount]").setValue(result["softwareReceivedAmount"]);
                list.down("[name=softwareTotalAmount]").setValue(result["softwareTotalAmount"]);
                list.down("[name=totalReceivedAmount]").setValue(result["totalReceivedAmount"]);
                list.down("[name=totalReceivableAmount]").setValue(result["totalReceivableAmount"]);
                list.down("[name=totalAmount]").setValue(result["totalAmount"]);
            }
        });
    }
});
