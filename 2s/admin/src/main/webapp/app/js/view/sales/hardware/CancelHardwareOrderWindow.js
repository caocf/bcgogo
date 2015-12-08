/**
 * 取消交易
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.hardware.CancelHardwareOrderWindow', {
    alias: 'widget.cancelHardwareOrderWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.hardware.CancelHardwareOrderForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('cancelHardwareOrderForm')
        });
        me.callParent();
    },
    title: '取消交易',
    close: function () {
        this.commonUtils.unmask();
//        this.doClose();
        this.hide();
    }
});

