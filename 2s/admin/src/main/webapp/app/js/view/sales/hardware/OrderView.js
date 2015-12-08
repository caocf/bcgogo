/**
 * @author zhangjuntao
 * 客户支付管理 view
 */
Ext.define('Ext.view.sales.hardware.OrderView', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.salesHardwareOrderView',
    forceFit: true,
    frame: true,
    autoHeight: true,
    autoScroll: false,
    requires: [
        'Ext.view.sales.hardware.OrderList'
    ],
    items: [
        {
            title: '硬件订单列表',
            xtype: "salesHardwareOrderList"
        }
    ],
    initComponent: function () {
        var me = this;
        me.callParent();
    }

});