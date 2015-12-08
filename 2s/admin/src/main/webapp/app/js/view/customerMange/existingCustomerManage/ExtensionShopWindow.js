/**
 * 延期
 * @author zhangjuntao
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.ExtensionShopWindow', {
    alias: 'widget.shopExtensionShopWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.customerMange.existingCustomerManage.ExtensionShopForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('shopExtensionShopForm')
        });
        me.callParent();
    },
    title: '延期新增',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    },
    showWin: function (rec) {
        this.down('form').loadRecord(rec);
//        this.down('form').down("[name=shopVersionName]").setValue(rec.get("shopVersionName") + "&nbsp&nbsp￥" + rec.get("softPrice"))
        this.down('form').down("[name=shopId]").setValue(rec.get("id"));
        this.show();
    }
});

