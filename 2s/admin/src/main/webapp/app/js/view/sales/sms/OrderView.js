/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-24
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.sales.sms.OrderView', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.salesSmsOrderView',
    forceFit: true,
    frame: true,
    autoHeight: true,
    autoScroll: false,
    requires: [
        'Ext.view.sales.sms.OrderList',
        'Ext.view.sales.sms.PreferentialSetting'
    ],
    items: [
        {
            title: '短信销售单',
            xtype: "salesSmsOrderList"
        },
        {
            title: '优惠设置',
            xtype: "smsPreferentialSetting"
        }
    ],
    initComponent: function () {
        var me = this;
        me.callParent();
    }

});
