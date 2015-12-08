/**
 * 审核窗口
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.software.BargainApplyWindow', {
    alias: 'widget.bargainApplyWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.software.BargainApplyForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('bargainApplyForm')
        });
        me.callParent();
    },
    title: '议价申请',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    },
    showWin: function (rec) {
        this.down('form').loadRecord(rec);
        this.down('form').down("[name=softPrice]").setValue("￥" + rec.get("totalAmount"));
        this.down('form').down("[name=orderId]").setValue(rec.get("id"));
        this.show();
    }
});

