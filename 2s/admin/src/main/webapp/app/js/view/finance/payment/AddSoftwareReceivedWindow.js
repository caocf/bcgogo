/**
 * 增加软件已支付记录
 * @author zhangjuntao
 */
Ext.define('Ext.view.finance.payment.AddSoftwareReceivedWindow', {
    alias: 'widget.addSoftwareReceivedWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.finance.payment.AddSoftwareReceivedForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('addSoftwareReceivedForm')
        });
        me.callParent();
    },
    title: '增加软件已支付记录',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

