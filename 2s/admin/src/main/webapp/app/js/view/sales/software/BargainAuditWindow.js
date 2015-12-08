/**
 * 议价记录
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.software.BargainAuditWindow', {
    alias: 'widget.bargainAuditWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    maxHeight : 500,
    width: 600,
    maximizable: true,
    autoScroll:true,
    requires: ["Ext.view.sales.software.BargainAuditForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('bargainAuditForm')
        });
        me.callParent();
    },
    title: '议价记录',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    },
    showWin: function (rec) {
        this.down('form').loadRecord(rec);
        this.down('form').down("[name=softPrice]").setValue("￥" + rec.get("totalAmount"));
        this.down('form').down("[name=orderId]").setValue(rec.get("id"));
        this.down('form').freshShopBargainRecordPanel(rec.get("shopId"));
        this.show();
    }
});

