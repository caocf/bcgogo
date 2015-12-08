/**
 * 短信退费
 * @author zhangjuntao
 */
Ext.define('Ext.view.finance.account.ShopRefundWindow', {
    alias: 'widget.shopRefundWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.finance.account.ShopRefundForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('shopRefundForm')
        });
        me.callParent();
    },
    title: '短信退费',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

