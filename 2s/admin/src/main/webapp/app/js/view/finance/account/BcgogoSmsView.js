/**
 * @author zhangjuntao
 * 公司短信账单 view
 */
Ext.define('Ext.view.finance.account.BcgogoSmsView', {
    extend: 'Ext.container.Container',
    alias: 'widget.bcgogoSmsView',
    forceFit: true,
    frame: true,
    autoHeight: true,
    autoScroll: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    requires: [
        'Ext.view.finance.account.BcgogoSmsAccountList',
        'Ext.view.finance.account.BcgogoSmsRecordList'
    ],
    items: [
        {
            title: '短信总账单',
            height: 92,
            xtype: "bcgogoSmsAccountList"
        },
        {
            flex: 8.6,
            xtype: 'bcgogoSmsRecordList'
        }
    ],
    initComponent: function () {
        var me = this;
        me.callParent();
    }

});