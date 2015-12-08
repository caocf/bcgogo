/**
 * 新增费用
 * @author zhangjuntao
 */
Ext.define('Ext.view.finance.payment.AddSoftwareReceivableWindow', {
    alias: 'widget.addSoftwareReceivableWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.finance.payment.AddSoftwareReceivableForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('addSoftwareReceivableForm')
        });
        me.callParent();
    },
    title: '增加软件待支付记录',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

