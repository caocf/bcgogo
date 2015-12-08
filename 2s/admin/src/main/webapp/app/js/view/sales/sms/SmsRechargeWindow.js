/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 14-1-8
 * Time: 下午2:17
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.sales.sms.SmsRechargeWindow', {
    alias: 'widget.smsRechargeWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    requires: ["Ext.view.sales.sms.SmsRechargeForm"],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: Ext.widget('smsRechargeForm')
        });

        me.callParent();
    },
    title: '新增短信充值',
    close: function () {
        this.commonUtils.unmask();
        this.doClose();
    }
});
