/**
 *禁用&启用操作
 * @author :zhangjuntao
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.OperateView', {
    alias: 'widget.existingCustomerManageOperateView',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    title: '操作',
    layout: 'border',
    collapsible: true,
    width: 630,
    minWidth: 350,
    height: 400,
    requires: [
        "Ext.view.customerMange.existingCustomerManage.OperateHistoryList",
        "Ext.view.customerMange.existingCustomerManage.OperatorForm"
    ],
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
            items: [
                {
                    region: 'north',
                    title: '店铺名：',
                    height: 150,
                    split: true,
                    xtype: 'existingCustomerManageOperatorForm'
                },
                {
                    region: 'center',
                    xtype: 'customerMangeOperateHistoryList'
                }
            ]
        });
        me.callParent();
    },
    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    },

    setTrialStartTime: function (trialStartTime) {
        this.trialStartTime = trialStartTime;
    },
    getTrialStartTime: function () {
      return this.trialStartTime;
    },

    setShopId: function (shopId) {
        this.shopId = shopId;
    },

    getShopId: function () {
        return this.shopId;
    }
});

