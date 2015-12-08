/**
 * 其他支付情况
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.software.OfflineUnconstrainedPayWindow', {
    alias: 'widget.softwareOfflineUnconstrainedPayWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.software.OfflineUnconstrainedPayForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('softwareOfflineUnconstrainedPayForm')
        });
        me.callParent();
    },
    title: '其他支付情况',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});

