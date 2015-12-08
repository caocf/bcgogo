/**
 * 线下支付（软件首次付款）
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.software.OfflinePayWindow', {
    alias: 'widget.softwareOfflinePayWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.software.OfflinePayForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('softwareOfflinePayForm')
        });
        me.callParent();
    },
    title: '线下支付（软件首次付款）',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

