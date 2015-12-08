Ext.define('Ext.view.finance.account.ShopSmsAccountList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.shopSmsAccountList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    initComponent: function () {
        var me = this;
        var store = Ext.create('Ext.store.finance.ShopSmsAccounts');
        me.store = store;
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
                            xtype: "displayfield",
                            name: 'statistics',
                            value: '充值总额：￥0.00；消费总条数：0；剩余总条数：0'
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
                    dataIndex: 'shopName'
                },
                {
                    text: '短信充值',
                    columns: [
                        {
                            header: '充值总额',
                            dataIndex: 'rechargeBalance',
                            renderer: Ext.util.Format.numberRenderer('0.00')
                        },
                        {
                            header: '充值条数',
                            dataIndex: 'rechargeNumber'
                        }
                    ]
                },
                {
                    text: '短信赠送',
                    columns: [
                        {
                            header: '赠送总额',
                            dataIndex: 'handSelBalance',
                            renderer: Ext.util.Format.numberRenderer('0.00')
                        },
                        {
                            header: '赠送条数',
                            dataIndex: 'handSelNumber'
                        }
                    ]
                },
                {
                    text: '短信消费',
                    columns: [
                        {
                            header: '消费总额',
                            dataIndex: 'consumptionBalance',
                            renderer: Ext.util.Format.numberRenderer('0.00')
                        },
                        {
                            header: '消费条数',
                            dataIndex: 'consumptionNumber'
                        }
                    ]
                },
                {
                    text: '当前账户剩余',
                    columns: [
                        {
                            header: '账户余额',
                            dataIndex: 'currentBalance',
                            renderer: Ext.util.Format.numberRenderer('0.00')
                        },
                        {
                            header: '剩余条数',
                            dataIndex: 'currentNumber'
                        }
                    ]
                },
                {
                    xtype: 'actioncolumn',
                    header: '详细',
                    width: 25,
                    name: "detail",
                    items: [
                        {
                            tooltip: '详细',
                            scope: me,
                            icon: 'app/images/icons/collapse-all.gif'
                        }
                    ]
                },
                {
                    xtype: 'actioncolumn',
                    header: '退费',
                    name: "refund",
                    width: 25,
                    items: [
                        {
                            tooltip: '退费',
                            scope: me,
                            icon: 'app/images/icons/no.png'
                        }
                    ]
                }
            ]
        });
        this.callParent(arguments);
    },
    onSearch: function () {
        var list = this,
            shopName = list.down("[name=shopName]").getValue();
        list.store.proxy.extraParams = {shopName: shopName};
        list.store.loadPage(1);
        list.statistics(shopName);
    },
    statistics: function (shopName) {
        var list = this;
        list.commonUtils.ajax({
            params: {shopName: shopName},
            url: 'shopSmsAccount.do?method=shopSmsAccountStatistics',
            success: function (result) {
                list.down("[name=statistics]").setValue(result["data"]);
            }
        });
    }
});
