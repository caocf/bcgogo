/**
 * @author zhangjuntao
 * 注册客户 待审核客户 view
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.View', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.existingCustomerManageView',
    forceFit: true,
    frame: true,
    autoHeight: true,
    autoScroll: true,
    requires: [
        'Ext.view.customerMange.existingCustomerManage.CheckPendingList',
        'Ext.view.customerMange.existingCustomerManage.RegisteredTrialList',
        'Ext.view.customerMange.existingCustomerManage.RegisteredList'
    ],
    items: [
        {
            title: '待审核客户',
            xtype: "existingCustomerManageCheckPendingList"
        },
        {
            title: '试用客户',
            xtype: "shopRegisteredTrialList"
        },
        {
            title: '正式客户',
            xtype: "existingCustomerManageRegisteredList"
        }
    ],
    initComponent: function () {
        var me = this;
        me.callParent();
    }

});