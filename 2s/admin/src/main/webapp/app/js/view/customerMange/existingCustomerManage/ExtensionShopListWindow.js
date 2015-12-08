/**
 * 延期记录
 * @author zhangjuntao
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.ExtensionShopListWindow', {
    alias: 'widget.shopExtensionShopListWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.customerMange.existingCustomerManage.ExtensionShopList"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('shopExtensionShopList')
        });
        me.callParent();
    },
    title: '延期记录',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    },
    showWin: function (rec) {
        this.down('form').loadRecord(rec);
//        this.down('form').down("[name=shopId]").setValue(rec.get("id"));
        this.down('form').freshShopExtensionLogPanel(rec.get("id"));
        this.show();
    }
});

