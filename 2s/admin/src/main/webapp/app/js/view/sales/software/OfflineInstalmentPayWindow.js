/**
 * 新增费用
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.software.OfflineInstalmentPayWindow', {
    alias: 'widget.softwareOfflineInstalmentPayWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.software.OfflineInstalmentPayForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('softwareOfflineInstalmentPayForm')
        });
        me.callParent();
    },
    title: '线下支付（软件再次付款）',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

