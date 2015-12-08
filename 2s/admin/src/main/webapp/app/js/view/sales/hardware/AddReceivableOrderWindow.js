/**
 * 新增费用
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.hardware.AddReceivableOrderWindow', {
    alias: 'widget.addReceivableOrderWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: {
        type: 'fit',
        reserveScrollbar: true // There will be a gap even when there's no scrollbar
    },
    collapsible: true,
    requires: ["Ext.view.sales.hardware.AddReceivableOrderForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('addReceivableOrderForm')
        });
        me.callParent();
    },
    title: '新增硬件订单',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

