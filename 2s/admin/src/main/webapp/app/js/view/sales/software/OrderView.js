/**
 * @author zhangjuntao
 * 客户支付管理 view
 */
Ext.define('Ext.view.sales.software.OrderView', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.salesSoftwareOrderView',
    forceFit: true,
    frame: true,
    autoHeight: true,
    autoScroll: false,
    requires: [
        'Ext.view.sales.software.OrderList'
    ],
    items: [
        {
            title: '软件订单列表',
            xtype: "salesSoftwareOrderList"
        }
    ],
    initComponent: function () {
        var me = this;
        me.callParent();
    }

});