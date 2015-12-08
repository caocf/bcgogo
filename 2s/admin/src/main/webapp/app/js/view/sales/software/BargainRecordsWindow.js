/**
 * 议价记录
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.software.BargainRecordsWindow', {
    alias: 'widget.bargainRecordsWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    maximizable: true,//是否可最大化窗口，默认为false。
    maxHeight: 500,
    autoScroll: true,
    requires: ["Ext.view.sales.software.BargainRecordsForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('shopBargainRecordsForm')
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
        this.down('form').down("[name=shopVersionName]").setValue(rec.get("shopVersionName") + "&nbsp&nbsp￥" + rec.get("softPrice"))
        this.down('form').freshShopBargainRecordPanel(rec.get("id"));
        this.show();
    }
});

