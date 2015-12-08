/**
 * 新增费用
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.hardware.OfflinePayWindow', {
    alias: 'widget.hardwareOfflinePayWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.hardware.OfflinePayForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('hardwareOfflinePayForm')
        });
        me.callParent();
    },
    title: '线下支付（硬件付款）',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

