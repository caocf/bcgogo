
/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-24
 * Time: 上午10:58
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.controller.sales.BcgogoReceivableSmsOrderController', {
    extend: 'Ext.app.Controller',

    views: [
        'Ext.view.sales.sms.OrderView'
    ],

    requires: [
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common",
        "Ext.view.sales.sms.SmsRechargeWindow",
        "Ext.view.sales.sms.SmsOrderDetailWindow"
    ],

    refs: [
        {ref: 'salesView', selector: 'salesView'},
        {ref: 'salesSmsOrderList', selector: 'salesSmsOrderList'},
        {ref: 'financePaymentList', selector: 'financePaymentList'},
        {ref: 'smsPreferentialSetting', selector: 'smsPreferentialSetting'},
        {id: 'smsRechargeWindow', ref: 'smsRechargeWindow', selector: 'smsRechargeWindow', xtype: 'smsRechargeWindow', autoCreate: true},
        {id: 'smsOrderDetailWindow', ref: 'smsOrderDetailWindow', selector: 'smsOrderDetailWindow', xtype: 'smsOrderDetailWindow', autoCreate: true}
    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'financePaymentList': {
                showSmsOrderDetail: me.showSmsOrderDetail
            },
            'salesView': {
                beforerender: function (view, eOpts) {
                    var hasOneTab = false;
                    if (!me.permissionUtils.hasPermission("CRM.SALES_MANAGER.SMS.ORDER_LIST")) {
                        view.remove(view.down("salesSmsOrderList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!hasOneTab) {
                        alert("权限配置异常!");
                    }
                }
            },
            'salesSmsOrderList': {
                beforerender: function (view) {

                },
                afterrender: function () {
                   me.getSalesSmsOrderList().onSearch();
                   me.getSalesSmsOrderList().on('cellclick', me.onCellClick,me);
                }
            },
            'smsPreferentialSetting': {
                beforerender: function (view) {

                },
                afterrender: function () {
                    me.getSmsPreferentialSetting().onSearch();
                }
            },
            'salesSmsOrderList button[action=add]': {
                click: function(){
                    var me = this,
                        win = me.getSmsRechargeWindow();
                    win.show();
                }
            },
            'smsRechargeWindow button[action=close]': {
                click: function(){
                    var me = this,
                        win = me.getSmsRechargeWindow();
                    win.close();
                }
            },
            'smsRechargeWindow button[action=save]': {
                click: function(){
                    var me = this,
                        win = me.getSmsRechargeWindow();
                    var form = win.down("form"),
                        formEl = form.getEl(),
                        baseForm = form.form;
                    var params = baseForm.getValues();
                    if(!params["shopId"]) {
                        Ext.Msg.alert('返回结果', "充值店铺不能为空");
                        return;
                    }
                    if(!params["rechargeAmount"]) {
                        Ext.Msg.alert('返回结果', "充值金额不能为空");
                        return;
                    }
                    if(params["rechargeAmount"]*1 < 50) {
                        Ext.Msg.alert('返回结果', "充值金额不能小于50元");
                        return;
                    }
                    if(!params["payeeId"]) {
                        Ext.Msg.alert('返回结果', "收款人不能为空");
                        return;
                    }
                    win.mask();
                    Ext.get("contentPanel").mask('正在充值 . . .');
                    win.commonUtils.ajax({
                        url: 'shopSmsAccount.do?method=recharge',
                        params: params,
                        success: function (result) {
                            if(result["success"]) {
                                Ext.get("contentPanel").unmask();
                                Ext.Msg.alert('返回结果', "充值成功！", function () {
                                    baseForm.reset();
                                    win.unmask();
                                    win.close();
                                    me.getSalesSmsOrderList().onSearch();
                                });
                            } else {
                                Ext.get("contentPanel").unmask();
                                Ext.Msg.alert('返回结果', "充值失败", function () {
                                    win.unmask();
                                });
                            }
                        },
                        failure: function () {
                            win.unmask();
                        }
                    });
                }
            }
        });
    },
    showSmsOrderDetail: function(orderId) {
        var win, me = this;
        if (!Ext.isEmpty(orderId)) {
            //硬件
            win= me.getSmsOrderDetailWindow();
            var me = this, form = win.down('form'),
                baseForm = form.form;
            baseForm.load({
                params: {
                    smsRechargeId: orderId
                },
                url : 'shopSmsAccount.do?method=getSmsRechargeById',
                waitMsg : '正在载入数据...',
                success : function(form,action) {
                    win.commonUtils.mask();
                    var order = action.result.data;
                    win.drawSmsOrderDetail(order);
                    win.show();
                    win.commonUtils.unmask();
                },
                failure : function(form,action) {

                }
            });
        }
    },
    onCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        var me = this,rec = grid.getStore().getAt(rowIndex);
        if (grid.getHeaderAtIndex(cellIndex).dataIndex === "receiptNo") {
            me.showSmsOrderDetail(rec.get("id"));
        }
    }
});
