Ext.define('Ext.controller.sales.BcgogoReceivableSoftwareOrderController', {
    extend: 'Ext.app.Controller',

    views: [
        'Ext.view.sales.software.OrderView'
    ],

    requires: [
        "Ext.view.sales.software.OfflineInstalmentPayWindow",
        "Ext.view.sales.software.OfflinePayWindow",
        "Ext.view.sales.software.OfflineUnconstrainedPayWindow",
        "Ext.view.sales.software.SoftwareOrderDetailWindow",
        'Ext.view.sales.software.BargainApplyWindow',
        'Ext.view.sales.software.BargainAuditWindow',
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs: [
        {ref: 'salesView', selector: 'salesView'},
        {ref: 'salesSoftwareOrderList', selector: 'salesSoftwareOrderList'},

        {ref: 'financePaymentList', selector: 'financePaymentList'},
        {id: 'softwareOfflineInstalmentPayWindow', ref: 'softwareOfflineInstalmentPayWindow', selector: 'softwareOfflineInstalmentPayWindow', xtype: 'softwareOfflineInstalmentPayWindow', autoCreate: true},
        {id: 'softwareOfflinePayWindow', ref: 'softwareOfflinePayWindow', selector: 'softwareOfflinePayWindow', xtype: 'softwareOfflinePayWindow', autoCreate: true},
        {id: 'softwareOfflineUnconstrainedPayWindow', ref: 'softwareOfflineUnconstrainedPayWindow', selector: 'softwareOfflineUnconstrainedPayWindow', xtype: 'softwareOfflineUnconstrainedPayWindow', autoCreate: true},
        //议价-申请
        {id: 'bargainApplyWindow', ref: 'bargainApplyWindow', selector: 'bargainApplyWindow', xtype: 'bargainApplyWindow', autoCreate: true},
        //议价-审核
        {id: 'bargainAuditWindow', ref: 'bargainAuditWindow', selector: 'bargainAuditWindow', xtype: 'bargainAuditWindow', autoCreate: true },
        {id: 'softwareOrderDetailWindow', ref: 'softwareOrderDetailWindow', selector: 'softwareOrderDetailWindow', xtype: 'softwareOrderDetailWindow', autoCreate: true}

    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'financePaymentList': {
                showSoftwareOrderDetail: me.showSoftwareOrderDetail
            },
            'salesView': {
                beforerender: function (view, eOpts) {
                    var hasOneTab = false;
                    if (!me.permissionUtils.hasPermission("CRM.SALES_MANAGER.SOFTWARE.ORDER_LIST")) {
                        view.remove(view.down("salesSoftwareOrderList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!hasOneTab) {
                        alert("权限配置异常!");
                    }
                }
            },
            'softwareOrderDetailWindow button[action=bcgogoReceivableOrderBargainAudit]': {
                click: function () {
                    var form = me.getSoftwareOrderDetailWindow().down('form'),
                        bcgogoReceivableOrderModel = Ext.ModelManager.create({
                            id : form.down("[name=id]").getValue(),
                            paymentType : 'SOFTWARE',
                            totalAmount : form.down("[name=totalAmount]").getValue(),
                            shopName : form.down("[name=shopName]").getValue(),
                            shopOwner : form.down("[name=shopOwner]").getValue(),
                            shopMobile : form.down("[name=shopMobile]").getValue(),
                            shopVersion : form.down("[name=shopVersion]").getValue(),
                            shopId : form.down("[name=shopId]").getValue()
                        }, 'Ext.model.sales.BcgogoReceivableOrder');
                    me.shopBargainAudit(bcgogoReceivableOrderModel);
                }
            },
            'softwareOrderDetailWindow button[action=bcgogoReceivableOrderBargainApply]': {
                click: function () {
                    var form = me.getSoftwareOrderDetailWindow().down('form'),
                        bcgogoReceivableOrderModel = Ext.ModelManager.create({
                            id : form.down("[name=id]").getValue(),
                            paymentType : 'SOFTWARE',
                            totalAmount : form.down("[name=totalAmount]").getValue(),
                            shopName : form.down("[name=shopName]").getValue(),
                            shopOwner : form.down("[name=shopOwner]").getValue(),
                            shopMobile : form.down("[name=shopMobile]").getValue(),
                            shopVersion : form.down("[name=shopVersion]").getValue(),
                            shopId : form.down("[name=shopId]").getValue()
                        }, 'Ext.model.sales.BcgogoReceivableOrder');

                    me.softwareBargainApply(bcgogoReceivableOrderModel);
                }
            },
            'softwareOrderDetailWindow button[action=bcgogoReceivableOrderOfflinePay]': {
                click: function () {
                    var form = me.getSoftwareOrderDetailWindow().down('form'),
                        bcgogoReceivableOrderModel = Ext.ModelManager.create({
                            id : form.down("[name=id]").getValue(),
                            paymentType : 'SOFTWARE',
                            bcgogoReceivableOrderToBePaidRecordRelationId : form.down("[name=bcgogoReceivableOrderToBePaidRecordRelationId]").getValue(),
                            currentPayableAmount : form.down("[name=currentPayableAmount]").getValue(),
                            instalmentPlanId : form.down("[name=instalmentPlanId]").getValue(),
                            receivableAmount : form.down("[name=receivableAmount]").getValue(),
                            receivableMethod : form.down("[name=receivableMethod]").getValue(),
                            totalAmount : form.down("[name=totalAmount]").getValue(),
                            status : form.down("[name=status]").getValue(),
                            shopVersion : form.down("[name=shopVersion]").getValue(),
                            shopName : form.down("[name=shopName]").getValue()
                        }, 'Ext.model.sales.BcgogoReceivableOrder');
                    me.softwareOfflinePay(bcgogoReceivableOrderModel);
                }
            },
            'salesSoftwareOrderList actioncolumn#salesSoftwareOrderGridAction': {
                softwareOfflinePayClick: me.softwareOfflinePayInGrid,
                softwareBargainApplyClick: me.softwareBargainApplyInGrid,
                softwareBargainAuditClick:me.softwareBargainAuditInGrid
            },
            //议价提交审核
            "#bargainApplyWindow button[action=save]": {
                click: function () {
                    var form = me.getBargainApplyWindow().down('form'),
                        orderId = form.down("[name=orderId]").getValue();
                    form.save(form, function () {
                        me.getBargainApplyWindow().close();
                        if (!me.getSoftwareOrderDetailWindow().hidden) {
                            me.showSoftwareOrderDetail(orderId);
                        }
                        me.getSalesSoftwareOrderList().onSearch();
                    });
                }
            },
            //议价提交审核
            "#bargainAuditWindow button[action=save]": {
                click: function () {
                    var form = me.getBargainAuditWindow().down('form'),
                        orderId = form.down("[name=orderId]").getValue();
                    form.save(form, function () {
                        me.getBargainAuditWindow().close();
                        if (!me.getSoftwareOrderDetailWindow().hidden) {
                            me.showSoftwareOrderDetail(orderId);
                        }
                        me.getSalesSoftwareOrderList().onSearch();
                    });
                }
            },
            //软件线下支付--首次
            'softwareOfflinePayWindow button[action=save]': {
                click: function () {
                    var form = me.getSoftwareOfflinePayWindow().down('form'),
                        orderId = form.down("[name=orderId]").getValue();
                    form.save(form, function () {
                        me.getSoftwareOfflinePayWindow().close();
                        if (!me.getSoftwareOrderDetailWindow().hidden) {
                            me.showSoftwareOrderDetail(orderId);
                        }
                        me.getSalesSoftwareOrderList().onSearch();
                    });
                }
            },
            //软件线下支付--再次分期
            'softwareOfflineInstalmentPayWindow button[action=save]': {
                click: function () {
                    var form = me.getSoftwareOfflineInstalmentPayWindow().down('form'),
                        orderId = form.down("[name=orderId]").getValue();
                    form.save(form, function () {
                        me.getSoftwareOfflineInstalmentPayWindow().close();
                        if (!me.getSoftwareOrderDetailWindow().hidden) {
                            me.showSoftwareOrderDetail(orderId);
                        }
                        me.getSalesSoftwareOrderList().onSearch();
                    });
                }
            },
            //软件线下支付 --其他
            'softwareOfflineUnconstrainedPayWindow button[action=save]': {
                click: function () {
                    var form = me.getSoftwareOfflineUnconstrainedPayWindow().down('form'),
                        orderId = form.down("[name=orderId]").getValue();
                    form.save(form, function () {
                        me.getSoftwareOfflineUnconstrainedPayWindow().close();
                        if (!me.getSoftwareOrderDetailWindow().hidden) {
                            me.showSoftwareOrderDetail(orderId);
                        }
                        me.getSalesSoftwareOrderList().onSearch();
                    });
                }
            },
            //软件销售订单列表
            'salesSoftwareOrderList': {
                beforerender: function (view) {

                },
                afterrender: function () {
                    me.getSalesSoftwareOrderList().generateShopVersionCheckBoxGroup();
                    me.getSalesSoftwareOrderList().onSearch();
                    me.getSalesSoftwareOrderList().on('cellclick', me.onCellClick,me);
                }
            }
        });
    },
    softwareBargainAuditInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        me.shopBargainAudit(rec);
    },
    //软件议价审核
    shopBargainAudit: function(rec){
        var win, form, me = this;
        me.getBargainAuditWindow().showWin(rec);
    },
    softwareBargainApplyInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        me.softwareBargainApply(rec);
    },
    //软件议价申请
    softwareBargainApply: function(rec){
        var win, form, me = this;
        me.getBargainApplyWindow().showWin(rec);
    },
    softwareOfflinePayInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        me.softwareOfflinePay(rec);
    },
    //软件线下支付
    softwareOfflinePay: function(rec){
        var win, form, me = this;
        var bcgogoReceivableOrderToBePaidRecordRelationId=rec.get("bcgogoReceivableOrderToBePaidRecordRelationId");
        var status=rec.get("status");
        if(!Ext.isEmpty(bcgogoReceivableOrderToBePaidRecordRelationId) && (status==='PARTIAL_PAYMENT' || status==='NON_PAYMENT')){
            if (status==='PARTIAL_PAYMENT' && "INSTALLMENT" === rec.get("receivableMethod")) {
                //再次分期付款
                win = me.getSoftwareOfflineInstalmentPayWindow();
                form = win.down("form");
                form.loadRecord(rec);
                form.down("[name=orderId]").setValue(rec.get("id"));
                form.down("[name=bcgogoReceivableOrderRecordRelationId]").setValue(bcgogoReceivableOrderToBePaidRecordRelationId);
                form.down("[name=paidAmount]").setValue(rec.get("currentPayableAmount"));
                form.down("[name=paidAmount]").mimPaidAmount = rec.get("currentPayableAmount");
                form.down("[name=payeeId]").store.loadData([
                    {name: rec.get("payeeName"), id: rec.get("payeeId")}
                ]);
                form.down("[name=payeeId]").select(rec.get("payeeId"));
                form.freshInstalmentPlanPanel(rec.get("instalmentPlanId"));
                win.show();
            } else if (status==='NON_PAYMENT' && ("FULL" === rec.get("receivableMethod") || Ext.isEmpty(rec.get("receivableMethod")))) {
                //首次分期付款
                win = me.getSoftwareOfflinePayWindow();
                form = win.down("form");
                form.loadRecord(rec);
                form.down("[name=orderId]").setValue(rec.get("id"));
                form.down("[name=bcgogoReceivableOrderRecordRelationId]").setValue(bcgogoReceivableOrderToBePaidRecordRelationId);
                form.down("[name=paidAmount]").setValue(rec.get("totalAmount"));
                form.down("[name=payeeId]").store.loadData([
                    {name: rec.get("payeeName"), id: rec.get("payeeId")}
                ]);
                form.down("[name=payeeId]").select(rec.get("payeeId"));
                win.show();
            } else if (status==='PARTIAL_PAYMENT' && "UNCONSTRAINED" === rec.get("receivableMethod")) {
                //剩余分期付款
                win = me.getSoftwareOfflineUnconstrainedPayWindow();
                form = win.down("form");
                form.loadRecord(rec);
                form.down("[name=orderId]").setValue(rec.get("id"));
                form.down("[name=bcgogoReceivableOrderRecordRelationId]").setValue(bcgogoReceivableOrderToBePaidRecordRelationId);
                form.down("[name=paidAmount]").setValue(rec.get("currentPayableAmount"));
                form.down("[name=payeeId]").store.loadData([
                    {name: rec.get("payeeName"), id: rec.get("payeeId")}
                ]);
                form.down("[name=payeeId]").select(rec.get("payeeId"));
                win.show();
            }
        }
    },
    showSoftwareOrderDetail: function (orderId) {
        var win, me = this;
        if (!Ext.isEmpty(orderId)) {
            //硬件
            win= me.getSoftwareOrderDetailWindow();
            var me = this, form = win.down('form'),
                baseForm = form.form;
            baseForm.load({
                params: {
                    orderId: orderId
                },
                url : 'bcgogoReceivable.do?method=getBcgogoReceivableOrderDetail',
                waitMsg : '正在载入数据...',
                success : function(form,action) {
                    win.commonUtils.mask();
                    var order = action.result.data;
                    win.drawBcgogoSoftwareOrderDetail(order);
                    var bcgogoReceivableOrderBargainApplyBtn = win.down("[id=bcgogoReceivableOrderBargainApplyBtn]");
                    var bcgogoReceivableOrderOfflinePayBtn = win.down("[id=bcgogoReceivableOrderOfflinePayBtn]");
                    var bcgogoReceivableOrderBargainAuditBtn = win.down("[id=bcgogoReceivableOrderBargainAuditBtn]");
                    bcgogoReceivableOrderBargainApplyBtn.hide().disable();
                    bcgogoReceivableOrderOfflinePayBtn.hide().disable();
                    bcgogoReceivableOrderBargainAuditBtn.hide().disable();

                    if (order['chargeType'] ==='ONE_TIME' && me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED_TRIAL.BARGAIN_APPLY")
                        && (Ext.isEmpty(order['bargainStatus']) || order['bargainStatus']==="NO_BARGAIN" || order['bargainStatus'] === "AUDIT_REFUSE") && order['status'] === 'NON_PAYMENT') {
                        if(order['bargainStatus'] === "AUDIT_REFUSE"){
                            bcgogoReceivableOrderBargainApplyBtn.setTooltip('再次申请议价');
                            bcgogoReceivableOrderBargainApplyBtn.setText('再次申请议价');
                        }else{
                            bcgogoReceivableOrderBargainApplyBtn.setTooltip('申请议价');
                            bcgogoReceivableOrderBargainApplyBtn.setText('申请议价');
                        }
                        bcgogoReceivableOrderBargainApplyBtn.show().enable();
                    }

                    if (order['chargeType'] ==='ONE_TIME' && me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.SUBMIT_PAYABLE")
                        && order['bargainStatus']!='PENDING_REVIEW' && (order['status'] === 'NON_PAYMENT' || order['status'] === 'PARTIAL_PAYMENT')) {
                        bcgogoReceivableOrderOfflinePayBtn.show().enable();
                    }

                    if (order['chargeType'] ==='ONE_TIME' && me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED_TRIAL.BARGAIN_AUDIT") && order['bargainStatus']==='PENDING_REVIEW') {
                        bcgogoReceivableOrderBargainAuditBtn.show().enable();
                    }
                    win.show();
                    win.setChildWin(me.getBargainAuditWindow(),me.getBargainApplyWindow(),me.getSoftwareOfflineInstalmentPayWindow(),me.getSoftwareOfflinePayWindow(),me.getSoftwareOfflineUnconstrainedPayWindow());
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
            me.showSoftwareOrderDetail(rec.get("id"));
        }
    }
});