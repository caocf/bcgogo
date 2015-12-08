/**
 * @author zhangjuntao
 * 客户支付管理 view
 */
Ext.define('Ext.view.finance.payment.View', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.financeView',
    forceFit: true,
    frame: true,
    autoHeight: true,
    autoScroll: false,
    requires: [
        'Ext.view.finance.payment.PaymentList'
//        ,
//        'Ext.view.finance.payment.HasBeenPaidList',
//        'Ext.view.finance.payment.ToBePaidList',
//        'Ext.view.finance.payment.PendingReviewList'
    ],
    items: [
        {
            title: '支付列表',
            xtype: "financePaymentList"
        }
//        ,
//        {
//            title: '待支付',
//            xtype: "financeToBePaidList"
//        },
//        {
//            title: '待审核',
//            xtype: 'financePendingReviewList'
//        },
//        {
//            title: '已支付',
//            xtype: "financeHasBeenPaidList"
//        }
    ],
    initComponent: function () {
        var me = this;
        me.callParent();
    }

});