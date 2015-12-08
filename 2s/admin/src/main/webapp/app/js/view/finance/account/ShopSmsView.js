/**
 * @author zhangjuntao
 * 客户短信账单 view
 */
Ext.define('Ext.view.finance.account.ShopSmsView', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.shopSmsView',
    forceFit: true,
    frame: true,
    autoHeight: true,
    autoScroll: false,
    requires: [
        'Ext.view.finance.account.ShopSmsAccountList',
        'Ext.view.finance.account.ShopSmsRecordList'
    ],
    items: [
        {
            title: '客户短信账单',
            xtype: 'shopSmsAccountList'
        },
        {
            title: '短信详细账目',
            xtype: "shopSmsRecordList"
        }
    ],
    initComponent: function () {
        var me = this;
        me.callParent();
    }

});