/**
 * 新增费用
 * @author zhangjuntao
 */
Ext.define('Ext.view.finance.account.BcgogoRechargeWindow', {
    alias: 'widget.bcgogoRechargeWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.finance.account.BcgogoRechargeForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('bcgogoRechargeForm')
        });
        me.callParent();
    },
    title: '公司充值录入',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

