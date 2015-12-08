/**
 * 新增费用
 * @author zhangjuntao
 */
Ext.define('Ext.view.finance.payment.OfflinePaymentAuditWindow', {
    alias: 'widget.offlinePaymentAuditWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.finance.payment.OfflinePaymentAuditForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('offlinePaymentAuditForm')
        });
        me.callParent();
    },
    title: '支付审核（硬件线下支付）',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

