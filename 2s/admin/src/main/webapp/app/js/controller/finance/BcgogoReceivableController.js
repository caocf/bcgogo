Ext.define('Ext.controller.finance.BcgogoReceivableController', {
    extend: 'Ext.app.Controller',

    views: [
        'Ext.view.finance.payment.View'
    ],

    requires: [
        "Ext.view.finance.payment.OnlinePaymentAuditWindow",
        "Ext.view.finance.payment.OfflinePaymentAuditWindow",

        "Ext.view.finance.payment.AddSoftwareReceivedWindow",
        "Ext.view.finance.payment.AddSoftwareReceivableWindow",
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs: [
        {ref: 'financePaymentList', selector: 'financePaymentList'},
        {ref: 'financeView', selector: 'financeView'},
        {ref: 'financeToBePaidList', selector: 'financeToBePaidList'},
        {ref: 'financeHasBeenPaidList', selector: 'financeHasBeenPaidList'},
        {ref: 'financePendingReviewList', selector: 'financePendingReviewList'},
        //新增软件待支付
        {id: 'addSoftwareReceivableWindow', ref: 'addSoftwareReceivableWindow', selector: 'addSoftwareReceivableWindow', xtype: 'addSoftwareReceivableWindow', autoCreate: true},
        //增加软件已支付记录
        {id: 'addSoftwareReceivedWindow', ref: 'addSoftwareReceivedWindow', selector: 'addSoftwareReceivedWindow', xtype: 'addSoftwareReceivedWindow', autoCreate: true},
        //审核
        {id: 'offlinePaymentAuditWindow', ref: 'offlinePaymentAuditWindow', selector: 'offlinePaymentAuditWindow', xtype: 'offlinePaymentAuditWindow', autoCreate: true},
        {id: 'onlinePaymentAuditWindow', ref: 'onlinePaymentAuditWindow', selector: 'onlinePaymentAuditWindow', xtype: 'onlinePaymentAuditWindow', autoCreate: true}
    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.componentUtils.initController(this.application, "Ext.controller.sales.BcgogoReceivableHardwareOrderController");
        this.componentUtils.initController(this.application, "Ext.controller.sales.BcgogoReceivableSoftwareOrderController");
        this.componentUtils.initController(this.application, "Ext.controller.sales.BcgogoReceivableSmsOrderController");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'financeView': {
                beforerender: function (view, eOpts) {
                    var hasOneTab = false;
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.HAS_BEEN_PAID.LIST")) {
                        view.remove(view.down("financeHasBeenPaidList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.TO_BE_PAID.LIST")) {
                        view.remove(view.down("financeToBePaidList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.PENDING_REVIEW.LIST")) {
                        view.remove(view.down("financePendingReviewList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.LIST")) {
                        view.remove(view.down("financePaymentList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!hasOneTab) {
                        alert("权限配置异常!");
                    }
                }
            },
            //待支付
            'financeToBePaidList': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.ADD_HARDWARE_PAYABLE")) {
                        view.down('button[action=addHardware]').hide();
                    }
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.ADD_SOFTWARE_PAYABLE")) {
                        view.down('button[action=addSoftware]').hide();
                    }
                },
                afterrender: function () {
                    me.getFinanceToBePaidList().onSearch();
                    me.getFinanceToBePaidList().on('cellclick', me.onCellClick, me.getFinanceToBePaidList());
                }
            },
            //新增软件待支付
            'financeToBePaidList button[action=addSoftware]': {
                click: function () {
                    me.getAddSoftwareReceivableWindow().show();
                }
            },
            'addSoftwareReceivableWindow button[action=save]': {
                click: function () {
                    var form = me.getAddSoftwareReceivableWindow().down('form');
                    form.save(form, function () {
                        me.getAddSoftwareReceivableWindow().close();
                        me.getFinanceToBePaidList().onSearch();
                    })
                }
            },
            //待审核
            'financePendingReviewList': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.ADD_SOFTWARE_PAID")) {
                        view.down('button[action=addSoftware]').hide();
                    }
                },
                afterrender: function () {
                    me.getFinancePendingReviewList().on('cellclick', me.onCellClick, me.getFinancePendingReviewList());
                    me.getFinancePendingReviewList().onSearch();
                }
            },
            //新增软件已支付
            'financePendingReviewList button[action=addSoftware]': {
                click: function () {
                    me.getAddSoftwareReceivedWindow().show();
                }
            },
            'financePendingReviewList actioncolumn[action=audit]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.AUDIT")) {
                        view.hide();
                    }
                },
                click: me.audit
            },
            'financePaymentList actioncolumn[action=audit]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.AUDIT")) {
                        view.hide();
                    }
                },
                click: me.audit
            },
            //show审核window
            'offlinePaymentAuditWindow button[action=save]': {
                click: function () {
                    var win = me.getOfflinePaymentAuditWindow(), form = win.down('form');
                    form.save(form, function () {
                        win.close();
                        me.getFinancePaymentList().onSearch();
                    })
                }
            },
            'onlinePaymentAuditWindow button[action=save]': {
                click: function () {
                    var win = me.getOnlinePaymentAuditWindow(), form = win.down('form');
                    form.save(form, function () {
                        win.close();
                        me.getFinancePaymentList().onSearch();
                    })
                }
            },
            'addSoftwareReceivedWindow button[action=save]': {
                click: function () {
                    var form = me.getAddSoftwareReceivedWindow().down('form');
                    form.save(form, function () {
                        me.getAddSoftwareReceivedWindow().close();
                        me.getFinancePendingReviewList().onSearch();
                    })
                }
            },

            //已经支付
            'financeHasBeenPaidList': {
                afterrender: function () {
                    me.getFinanceHasBeenPaidList().on('cellclick', me.onCellClick, me.getFinanceHasBeenPaidList());
                    me.getFinanceHasBeenPaidList().onSearch();
                }
            },
            //支付列表
            'financePaymentList': {
                afterrender: function () {
                    me.getFinancePaymentList().on('cellclick', me.onCellClick, me.getFinancePaymentList());
                    me.getFinancePaymentList().onSearch();
                }
            }
        });
    },

    //待审核
    audit: function (grid, cell, row, col, e) {
        var me = this, rec = grid.getStore().getAt(row),
            win,
            form , title = "支付审核", receivableMethod = "";
        if (rec.get('status') === 'PENDING_REVIEW'){
            if(rec.get("paymentMethod") == 'ONLINE_PAYMENT'){
                win = me.getOnlinePaymentAuditWindow();
                form = win.down("form");
                form.loadRecord(rec);
                if (rec.get("orderPaymentType") === "HARDWARE") {
                    form.down("[name=receivableMethod]").setValue("全额付款&nbsp&nbsp在线支付");
                    title += "(硬件在线支付)";
                }else if (rec.get("orderPaymentType") === "SOFTWARE") {
                    if (rec.get("receivableMethod") == 'FULL') {
                        form.down("[name=receivableMethod]").setValue("全额付款&nbsp&nbsp在线支付");
                        title += "(软件全额在线支付)";
                    } else if (rec.get("receivableMethod") == 'INSTALLMENT') {
                        form.down("[name=receivableMethod]").setValue("分期付款&nbsp&nbsp在线支付");
                        title += "(软件分期在线支付)";
                    }else if (rec.get("receivableMethod") == 'UNCONSTRAINED') {
                        form.down("[name=receivableMethod]").setValue("其他付款&nbsp&nbsp在线支付");
                        title += "(软件在线其他支付)";
                    }
                } else if(rec.get("orderPaymentType") === "SMS_RECHARGE") {
                    form.down("[name=receivableMethod]").setValue("全额付款&nbsp&nbsp在线支付");
                    title += "(短信全额在线支付)";
                }
            }else if(rec.get("paymentMethod") == 'DOOR_CHARGE'){
                win = me.getOfflinePaymentAuditWindow();
                form = win.down("form");
                form.loadRecord(rec);
                if (rec.get("orderPaymentType") === "HARDWARE") {
                    form.down("[name=receivableMethod]").setValue("全额付款&nbsp&nbsp上门收取");
                    title += "(硬件线下支付)";
                }else if(rec.get("orderPaymentType") === "SOFTWARE"){
                     if (rec.get("receivableMethod") == 'FULL') {
                        form.down("[name=receivableMethod]").setValue("全额付款&nbsp&nbsp上门收取");
                        title += "(软件全额线下支付)";
                    } else if (rec.get("receivableMethod") == 'INSTALLMENT') {
                        form.down("[name=receivableMethod]").setValue("分期付款&nbsp&nbsp上门收取");
                        title += "(软件分期线下支付)";
                     }else if (rec.get("receivableMethod") == 'UNCONSTRAINED') {
                        form.down("[name=receivableMethod]").setValue("其他付款&nbsp&nbsp上门收取");
                        title += "(软件线下其他支付)";
                    }
                } else if(rec.get("orderPaymentType") === "SMS_RECHARGE") {
                    form.down("[name=receivableMethod]").setValue("全额付款&nbsp&nbsp上门收取");
                    title += "(短信全额线下支付)";
                }
            }

            win.setTitle(title);
            win.show();
        }
    },

    onCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        var me = this,rec = grid.getStore().getAt(rowIndex);
        if (grid.getHeaderAtIndex(cellIndex).dataIndex === "orderReceiptNo") {
            if(rec.get("orderPaymentType")==="HARDWARE"){
                me.fireEvent('showHardwareOrderDetail',rec.get("orderId"));
            }else if(rec.get("orderPaymentType")==="SOFTWARE"){
                me.fireEvent('showSoftwareOrderDetail',rec.get("orderId"));
            } else if(rec.get("orderPaymentType")==="SMS_RECHARGE") {
                me.fireEvent('showSmsOrderDetail',rec.get("smsRechargeId"));
            }

        }
    }
});