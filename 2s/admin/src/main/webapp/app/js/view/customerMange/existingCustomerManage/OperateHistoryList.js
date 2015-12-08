/**
 * 启用&禁用账号历史记录
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.OperateHistoryList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.customerMangeOperateHistoryList',
    title: '操作记录',
    store: Ext.create('Ext.store.customerMange.OperateHistoryList'),
    forceFit: true,
    autoHeight: true,
    autoScroll: true,
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
            columns: [
                {
                    header: 'No.',
                    xtype: 'rownumberer',
                    sortable: false,
                    width: 25
                },
                {
                    header: '操作时间',
                    dataIndex: 'operateTime',
                    renderer: function (val, style, rec, index) {
                        return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d') : "";
                    }
                },
                {
                    header: '操作人',
                    width: 100,
                    dataIndex: 'operateUserName'
                },
                {
                    header: '操作类型',
                    width: 100,
                    dataIndex: 'operateType',
                    renderer: function (val, style, rec, index) {
                        if (val == "DISABLE_REGISTERED_PAID_SHOP")return "禁用";
                        else if (val == "ENABLE_REGISTERED_PAID_SHOP")return "启用";
                        else if (val == "UPDATE_REGISTERED_TRIAL_SHOP")return "升级";
                        else if (val == "CONTINUE_TO_TRY")return "继续试用";
                        return "";
                    }
                },
                {
                    header: '试用期限',
                    width: 200,
                    dataIndex: 'trialStartTime',
                    renderer: function (val, style, rec, index) {
                        if (val) {
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d') + "至" + Ext.util.Format.date(new Date(Number(rec.get("trialEndTime"))), 'Y-m-d');
                        }
                        return "";
                    }
                },
                {
                    header: '操作原因',
                    dataIndex: 'reason',
                    width: 200
                }
            ]
        });
        this.callParent(arguments);
    }
});