Ext.define('Ext.controller.sales.BcgogoReceivableHardwareOrderController', {
    extend: 'Ext.app.Controller',

    views: [
        'Ext.view.sales.hardware.OrderView'
    ],

    requires: [
        "Ext.view.sales.hardware.AddReceivableOrderWindow",
        "Ext.view.sales.hardware.OfflinePayWindow",
        "Ext.view.sales.hardware.CancelHardwareOrderWindow",
        "Ext.view.sales.hardware.HardwareOrderDetailWindow",
        "Ext.view.sales.hardware.HardwareOrderItemChangePriceWindow",
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs: [
        {ref: 'salesView', selector: 'salesView'},
        {ref: 'financePaymentList', selector: 'financePaymentList'},
        {ref: 'salesHardwareOrderList', selector: 'salesHardwareOrderList'},

        {ref:'regionSelect', selector:'addReceivableOrderForm regionSelect'},
        {ref:'citySelect', selector:'addReceivableOrderForm citySelect'},
        {ref:'provinceSelect', selector:'addReceivableOrderForm provinceSelect'},

        {id: 'addReceivableOrderWindow', ref: 'addReceivableOrderWindow', selector: 'addReceivableOrderWindow', xtype: 'addReceivableOrderWindow', autoCreate: true},
        {id: 'hardwareOfflinePayWindow', ref: 'hardwareOfflinePayWindow', selector: 'hardwareOfflinePayWindow', xtype: 'hardwareOfflinePayWindow', autoCreate: true},
        {id: 'cancelHardwareOrderWindow', ref: 'cancelHardwareOrderWindow', selector: 'cancelHardwareOrderWindow', xtype: 'cancelHardwareOrderWindow', autoCreate: true},
        {id: 'hardwareOrderItemChangePriceWindow', ref: 'hardwareOrderItemChangePriceWindow', selector: 'hardwareOrderItemChangePriceWindow', xtype: 'hardwareOrderItemChangePriceWindow', autoCreate: true},
        {id: 'hardwareOrderDetailWindow', ref: 'hardwareOrderDetailWindow', selector: 'hardwareOrderDetailWindow', xtype: 'hardwareOrderDetailWindow', autoCreate: true}

    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'financePaymentList': {
                showHardwareOrderDetail: me.showHardwareOrderDetail
            },
            'salesView': {
                beforerender: function (view, eOpts) {
                    var hasOneTab = false;
                    if (!me.permissionUtils.hasPermission("CRM.SALES_MANAGER.HARDWARE.ORDER_LIST")) {
                        view.remove(view.down("salesHardwareOrderList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!hasOneTab) {
                        alert("权限配置异常!");
                    }
                }
            },
            //新增硬件销售订单
            'salesHardwareOrderList button[action=addHardwareBcgogoReceivableOrder]': {
                click: function () {
                    me.getAddReceivableOrderWindow().show();
                }
            },
            'addReceivableOrderWindow button[action=save]': {
                click: function () {
                    var form = me.getAddReceivableOrderWindow().down('form');
                    form.save(form, function () {
                        me.getAddReceivableOrderWindow().close();
                        me.getSalesHardwareOrderList().onSearch();
                    })
                }
            },
            'hardwareOrderDetailWindow button[action=bcgogoReceivableOrderShip]': {
                click: function () {
                    var form = me.getHardwareOrderDetailWindow().down('form'),
                        orderId = form.down("[name=id]").getValue();
                    me.shipHardware(orderId);
                    me.showHardwareOrderDetail(orderId);
                }
            },
            'hardwareOrderDetailWindow button[action=bcgogoReceivableOrderCancel]': {
                click: function () {
                    var form = me.getHardwareOrderDetailWindow().down('form'),
                        orderId = form.down("[name=id]").getValue();
                    me.cancelHardwareOrder(orderId);
                }
            },
            'hardwareOrderDetailWindow button[action=bcgogoReceivableOrderOfflinePay]': {
                click: function () {
                    var form = me.getHardwareOrderDetailWindow().down('form'),
                        bcgogoReceivableOrderModel = Ext.ModelManager.create({
                            id : form.down("[name=id]").getValue(),
                            paymentType : 'HARDWARE',
                            bcgogoReceivableOrderToBePaidRecordRelationId : form.down("[name=bcgogoReceivableOrderToBePaidRecordRelationId]").getValue(),
                            receivableAmount : form.down("[name=receivableAmount]").getValue(),
                            totalAmount : form.down("[name=totalAmount]").getValue(),
                            shopName : form.down("[name=shopName]").getValue()
                    }, 'Ext.model.sales.BcgogoReceivableOrder');
                    me.hardwareOfflinePay(bcgogoReceivableOrderModel);
                }
            },
            'hardwareOrderItemChangePriceWindow button[action=save]': {
                click: function () {
                    var form = me.getHardwareOrderItemChangePriceWindow().down('form'),
                        orderId = form.down("[name=orderId]").getValue();
                    form.save(form, function () {
                        me.getHardwareOrderItemChangePriceWindow().close();
                        if (!me.getHardwareOrderDetailWindow().hidden) {
                            me.showHardwareOrderDetail(orderId);
                        }
                        me.getSalesHardwareOrderList().onSearch();
                    });
                }
            },
            'hardwareOfflinePayWindow button[action=save]': {
                click: function () {
                    var form = me.getHardwareOfflinePayWindow().down('form'),
                        orderId = form.down("[name=bcgogoReceivableOrderId]").getValue();
                    form.save(form, function () {
                        me.getHardwareOfflinePayWindow().close();
                        if(!me.getHardwareOrderDetailWindow().hidden){
                            me.showHardwareOrderDetail(orderId);
                        }
                        me.getSalesHardwareOrderList().onSearch();
                    });
                }
            },
            'cancelHardwareOrderWindow button[action=save]': {
                click: function () {
                    var form = me.getCancelHardwareOrderWindow().down('form'),
                        orderId = form.down("[name=bcgogoReceivableOrderId]").getValue();
                    form.save(form, function () {
                        me.getCancelHardwareOrderWindow().close();
                        if(!me.getHardwareOrderDetailWindow().hidden){
                            me.showHardwareOrderDetail(orderId);
                        }
                        me.getSalesHardwareOrderList().onSearch();
                    });
                }
            },
            'salesHardwareOrderList actioncolumn#salesHardwareOrderGridAction': {
                hardwareOfflinePayClick: me.hardwareOfflinePayInGrid,
                cancelHardwareOrderClick: me.cancelHardwareOrderInGrid,
                shipHardwareClick:me.shipHardwareInGrid

            },
            //form
            "addReceivableOrderForm provinceSelect":{
                select:function (combo, records, eOpts) {
                    me.getCitySelect().setRawValue("");
                    me.getCitySelect().setValue(null);
                    me.getRegionSelect().setRawValue(null);
                    me.getRegionSelect().setValue(null);
                    me.getCitySelect().setProvince(records[0]);
                },
                beforequery:function (queryEvent, eOpts) {
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:"1"
                    };
                }
            },
            "addReceivableOrderForm citySelect":{
                select:function (combo, records, eOpts) {
                    me.getRegionSelect().setRawValue(null);
                    me.getRegionSelect().setValue(null);
                    me.getRegionSelect().setCity(records[0]);
                },
                beforequery:function (queryEvent, eOpts) {
                    var form = me.getAddReceivableOrderWindow().down("form"),parentNo;
                    if (!queryEvent.combo.getProvince()) {
                        return false;
                    }
                    parentNo = queryEvent.combo.getProvince().get("no");
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:parentNo
                    };
                    queryEvent.combo.store.load();
                }
            },
            "addReceivableOrderForm regionSelect":{
                beforequery:function (queryEvent, eOpts) {
                    var form = me.getAddReceivableOrderWindow().down("form"), parentNo;
                    if (!queryEvent.combo.getCity()) {
                        return false;
                    }
                    parentNo = queryEvent.combo.getCity().get("no");
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:parentNo
                    };
                    queryEvent.combo.store.load();
                }
            },
            //硬件销售订单列表
            'salesHardwareOrderList': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.ADD_HARDWARE_PAYABLE")) {
                        view.down('button[action=addHardwareBcgogoReceivableOrder]').hide();
                    }
                },
                afterrender: function () {
                    me.getSalesHardwareOrderList().generateBcgogoProductCheckBoxGroup();
                    me.getSalesHardwareOrderList().onSearch();
                    me.getSalesHardwareOrderList().on('cellclick', me.onCellClick,me);
                    me.getSalesHardwareOrderList().store.on('load', function(){
                        var orders = me.getSalesHardwareOrderList().store.data.items;
                        for(var i = 0; i < orders.length; i++){
                            if(orders[i].data.status=="NON_PAYMENT"){
                                var orderItems = orders[i].data.bcgogoReceivableOrderItemDTOList;
                                for(var j = 0; j < orderItems.length; j++){
                                    if(!Ext.isEmpty(Ext.get('list-change-price-'+orderItems[j].idStr)))
                                        Ext.EventManager.addListener('list-change-price-'+orderItems[j].idStr,'click',me.hardwareOrderChangePrice,me);//绑定处理函数
                                }
                            }
                        }
                    },me);
                }
            }
        });
    },
    //改价
    hardwareOrderChangePrice: function (e,target) {
        e.preventDefault();
        var me = this,
            orderItemId = target.getAttribute("data-orderitem-id");
        if(!Ext.isEmpty(orderItemId)){
            var win= me.getHardwareOrderItemChangePriceWindow(),
                form = win.down("form"),
                baseForm = form.form;
            baseForm.load({
                params: {
                    orderItemId: orderItemId
                },
                url : 'bcgogoReceivable.do?method=getBcgogoReceivableOrderItemDetail',
                waitMsg : '正在载入数据...',
                success : function(form,action) {
                    win.commonUtils.mask();
                    var orderItem = action.result.data;
                    win.down("form").showBcgogoHardwareOrderItemDetail(orderItem);
                    win.show();
                    win.commonUtils.unmask();
                },
                failure : function(form,action) {

                }
            });
            win.show();
        }
    },
    //硬件线下支付
    hardwareOfflinePay: function (rec) {
        var win, form, me = this;
        var bcgogoReceivableOrderToBePaidRecordRelationId=rec.get("bcgogoReceivableOrderToBePaidRecordRelationId");
        if(!Ext.isEmpty(bcgogoReceivableOrderToBePaidRecordRelationId)){
            win = me.getHardwareOfflinePayWindow();
            form = win.down("form");
            form.down("[name=shopName]").setValue(rec.get("shopName"));
            form.down("[name=paymentType]").setValue(rec.get("paymentType") == 'HARDWARE' ? "硬件购买费用" : "软件购买费用");
            form.down("[name=bcgogoReceivableOrderId]").setValue(rec.get("id"));
            form.down("[name=bcgogoReceivableOrderRecordRelationId]").setValue(bcgogoReceivableOrderToBePaidRecordRelationId);
            form.down("[name=paidAmount]").setValue(rec.get("receivableAmount"));
            form.down("[name=totalAmount]").setValue(rec.get("totalAmount"));
            win.show();
        }
    },
    hardwareOfflinePayInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        me.hardwareOfflinePay(rec);
    },
    cancelHardwareOrder: function (orderId) {
        var me = this;
        if(!Ext.isEmpty(orderId)){
            var win = me.getCancelHardwareOrderWindow(),
                form = win.down("form");
            form.down("[name=bcgogoReceivableOrderId]").setValue(orderId);
            win.show();
        }
    },
    cancelHardwareOrderInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row), win, form, me = this;
        me.cancelHardwareOrder(rec.get("id"));
    },
    shipHardware: function (orderId) {
        var me = this;
        if(!Ext.isEmpty(orderId)){
            me.commonUtils.ajax({
                async:false,
                url: 'bcgogoReceivable.do?method=shipBcgogoReceivableOrder',
                params: {bcgogoReceivableOrderId: orderId},
                success: function (result) {
                    if (result.success) {
                        me.getSalesHardwareOrderList().onSearch();
                        Ext.Msg.alert('返回结果', "发货成功！");
                    } else {
                        Ext.Msg.alert('返回结果', "发货失败！");
                    }
                }
            });
        }
    },
    shipHardwareInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        me.shipHardware(rec.get("id"));
    },
    showHardwareOrderDetail: function (orderId) {
        var win, me = this;
        if (!Ext.isEmpty(orderId)) {
            //硬件
            win= me.getHardwareOrderDetailWindow();
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
                    win.drawBcgogoHardwareOrderDetail(order);
                    win.drawBcgogoHardwareOrderItemsList(order['bcgogoReceivableOrderItemDTOList'],order['status']);
                    win.drawBcgogoHardwareOrderRecordList(order['bcgogoReceivableOrderPaidRecordDTOList']);
                    win.drawBcgogoHardwareOrderOperationLogList(order['operationLogDTOList']);
                    win.down("[id=bcgogoReceivableOrderShipBtn]").hide().disable();
                    win.down("[id=bcgogoReceivableOrderOfflinePayBtn]").hide().disable();
                    win.down("[id=bcgogoReceivableOrderCancelBtn]").hide().disable();

                    if(order.status=="NON_PAYMENT"){
                        var orderItems = order['bcgogoReceivableOrderItemDTOList'];
                        for (var i = 0; i < orderItems.length; i++) {
                            if(!Ext.isEmpty(Ext.get('form-change-price-'+orderItems[i]['idStr'])))
                                Ext.EventManager.addListener('form-change-price-'+orderItems[i]['idStr'],'click',me.hardwareOrderChangePrice,me);
                        }
                        if(me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.SUBMIT_PAYABLE"))
                            win.down("[id=bcgogoReceivableOrderOfflinePayBtn]").show().enable();
                        if(me.permissionUtils.hasPermission("CRM.SALES_MANAGER.HARDWARE.CANCELED"))
                            win.down("[id=bcgogoReceivableOrderCancelBtn]").show().enable();
                    }else if(order.status=="FULL_PAYMENT" && me.permissionUtils.hasPermission("CRM.SALES_MANAGER.HARDWARE.SHIPPED")){
                        win.down("[id=bcgogoReceivableOrderShipBtn]").show().enable();
                    }
                    win.show();
                    win.setChildWin(me.getHardwareOfflinePayWindow(),me.getCancelHardwareOrderWindow(),me.getHardwareOrderItemChangePriceWindow());
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
            me.showHardwareOrderDetail(rec.get("id"));
        }
    }
});