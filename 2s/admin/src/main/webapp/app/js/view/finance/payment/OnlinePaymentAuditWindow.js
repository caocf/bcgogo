/**
 * 新增费用
 * @author zhangjuntao
 */
Ext.define('Ext.view.finance.payment.OnlinePaymentAuditWindow', {
    alias: 'widget.onlinePaymentAuditWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.finance.payment.OnlinePaymentAuditForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('onlinePaymentAuditForm')
        });
        me.callParent();
    },
    title: '支付审核（硬件在线支付）',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

