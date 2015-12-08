Ext.define('Ext.view.finance.account.BcgogoSmsAccountList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.bcgogoSmsAccountList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    initComponent: function () {
        var me = this;
        this.store = Ext.create('Ext.store.finance.BcgogoSmsAccounts');
        Ext.apply(me, {
            columns: [
                {
                    header: '所属类型',
                    dataIndex: 'type',
                    renderer: function (val, style, rec, index) {
                        if (rec.get("type") === "BCGOGO") {
                            return "公司  ";
                        } else {
                            return "客户";
                        }
                    }
                },
                {
                    header: '充值总额',
                    dataIndex: 'totalRechargeBalance',
                    renderer: function (val, style, rec, index) {
                        return "￥" + Ext.util.Format.number(val, '0.00');
                    }
                },
                {
                    header: '充值条数',
                    dataIndex: 'totalRechargeNumber'
                },
                {
                    header: '赠送客户条数',
                    dataIndex: 'handSelNumber',
                    renderer: function (val, style, rec, index) {
                        if (rec.get("type") === "BCGOGO") {
                            return val;
                        } else {
                            return "--";
                        }
                    }
                },
                {
                    header: '消费条数',
                    dataIndex: 'consumptionNumber'
                },
                {
                    header: '当前剩余条数',
                    dataIndex: 'surplusNumber'
                }
            ]
        });
        this.callParent(arguments);
    }
});
