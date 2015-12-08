/**
 * 取消交易
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.hardware.HardwareOrderItemChangePriceWindow', {
    alias: 'widget.hardwareOrderItemChangePriceWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.hardware.HardwareOrderItemChangePriceForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('hardwareOrderItemChangePriceForm')
        });
        me.callParent();
    },
    title: '硬件修改价格',
    close: function () {
        this.commonUtils.unmask();
//        this.doClose();
        this.hide();
    }
});

