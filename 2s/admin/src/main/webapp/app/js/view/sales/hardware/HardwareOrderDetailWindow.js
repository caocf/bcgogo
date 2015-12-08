/**
 * 审核窗口
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.hardware.HardwareOrderDetailWindow', {
    alias: 'widget.hardwareOrderDetailWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    width: 1000,
    height: 600,
    title: '硬件销售信息',
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        Ext.apply(me, {
            items: {
                xtype: 'form',
                bodyPadding: 5,
                width: 1000,
                height: 450,
                autoScroll: true,
                frame: false,
                border: false,
                buttons: [
                    {
                        id:'bcgogoReceivableOrderShipBtn',
                        hidden:true,
                        disabled:true,
                        action:'bcgogoReceivableOrderShip',
                        text: '发货',
                        tooltip: "发货"
                    },
                    {
                        id:'bcgogoReceivableOrderOfflinePayBtn',
                        hidden:true,
                        disabled:true,
                        action:'bcgogoReceivableOrderOfflinePay',
                        text: '线下支付',
                        tooltip: "线下支付"
                    },
                    {
                        id:'bcgogoReceivableOrderCancelBtn',
                        hidden:true,
                        disabled:true,
                        action:'bcgogoReceivableOrderCancel',
                        text: '取消交易',
                        tooltip: "取消交易"
                    },
//                    {
//                        text: '打印订单',
//                        tooltip: "打印订单",
//                        handler: function () {
//                            me.close();
//                        }
//                    },
                    {
                        text: '关闭',
                        tooltip: "关闭",
                        handler: function () {
                            me.close();
                        }
                    }
                ],
                items: [
                    {
                        xtype: 'fieldset',
                        title: '收货信息',
                        layout: 'anchor',
                        margin: "10 0 0 0",
                        defaults: {
                            anchor: '100%',
                            labelStyle: 'padding-left:4px;'
                        },
                        collapsible: true,
                        collapsed: false,
                        items: [
                            {
                                xtype: 'fieldset',
                                layout: 'hbox',
                                anchor: '100%',
                                padding: 0,
                                margin: "0 5 0 0",
                                defaults: {
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 180,
                                    labelAlign:'right',
                                    labelWidth: 60
                                },
                                border: false,
                                items: [
                                    {
                                        name: 'addressDetail',
                                        width: 300,
                                        fieldLabel: '详细地址'
                                    },
                                    {
                                        name: 'contact',
                                        width: 150,
                                        fieldLabel: '联系人'
                                    },
                                    {
                                        name: 'mobile',
                                        width: 150,
                                        fieldLabel: '联系电话'
                                    }

                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: '订单信息',
                        layout: 'anchor',
                        margin: "10 0 0 0",
                        defaults: {
                            anchor: '100%',
                            labelStyle: 'padding-left:4px;'
                        },
                        collapsible: true,
                        collapsed: false,
                        items: [
                            {
                                xtype: 'fieldset',
                                layout: 'hbox',
                                anchor: '100%',
                                padding: 0,
                                margin: "0 5 0 0",
                                defaults: {
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 5 5 0",
                                    width: 130,
                                    labelAlign:'right',
                                    labelWidth: 60
                                },
                                border: false,
                                items: [
                                    {
                                        name: 'receiptNo',
                                        width: 160,
                                        fieldLabel: '销售单号'
                                    },
                                    {
                                        name: 'buyChannelsText',
                                        fieldLabel: '购买渠道'
                                    },
                                    {
                                        name: 'createdTime',
                                        fieldLabel: '购买日期',
                                        dateFormat: 'Y-m-d'
                                    },
                                    {
                                        name: 'followName',
                                        labelWidth: 70,
                                        fieldLabel: '销售跟进人'
                                    },
                                    {
                                        name: 'statusText',
                                        fieldLabel: '状态'
                                    }
                                ]
                            },
                            {
                                xtype: 'fieldset',
                                layout: 'hbox',
                                anchor: '100%',
                                padding: 0,
                                margin: "0 5 0 0",
                                defaults: {
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 5 5 0",
                                    width: 130,
                                    labelAlign:'right',
                                    labelWidth: 60
                                },
                                border: false,
                                items: [
                                    {
                                        name: 'bcgogoCustomerInfo',
                                        width: 500,
                                        fieldLabel: '客户'
                                    }
                                ]
                            },
                            {
                                xtype: 'fieldset',
                                layout: 'hbox',
                                anchor: '100%',
                                padding: 0,
                                margin: "0 5 0 0",
                                defaults: {
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 180,
                                    labelAlign:'right',
                                    labelWidth: 60
                                },
                                border: false,
                                items: [
                                    {
                                        fieldLabel: '商品信息',
                                        width: 'auto',
                                        name: 'orderItemsInfo'
                                    }
                                ]
                            },
                            {
                                xtype: 'fieldset',
                                layout: 'hbox',
                                anchor: '100%',
                                padding: 0,
                                margin: "0 5 0 0",
                                defaults: {
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 160,
                                    labelAlign:'right',
                                    labelWidth: 60
                                },
                                border: false,
                                items: [
                                    {
                                        name: 'totalAmount',
                                        fieldLabel: '总计',
                                        numberFormat:'0.00'
                                    },
                                    {
                                        fieldLabel: '应付款',
                                        name:'receivableAmount',
                                        numberFormat:'0.00'
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: '支付记录',
                        layout: 'anchor',
                        margin: "10 0 0 0",
                        defaults: {
                            anchor: '100%',
                            labelStyle: 'padding-left:4px;'
                        },
                        collapsible: true,
                        collapsed: false,
                        items: [
                            {
                                xtype: 'fieldset',
                                layout: 'hbox',
                                anchor: '100%',
                                padding: 0,
                                margin: "0 5 0 0",
                                defaults: {
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 180,
                                    labelAlign:'right',
                                    labelWidth: 60
                                },
                                border: false,
                                items: [
                                    {
                                        width: 'auto',
                                        name: 'payRecordInfo',
                                        value:'无'
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: '操作记录',
                        layout: 'anchor',
                        margin: "10 0 0 0",
                        autoHeight: true,
                        defaults: {
                            anchor: '100%',
                            labelStyle: 'padding-left:4px;'
                        },
                        collapsible: true,
                        collapsed: false,
                        items: [
                            {
                                xtype: 'fieldset',
                                layout: 'hbox',
                                anchor: '100%',
                                padding: 0,
                                margin: "0 5 0 0",
                                defaults: {
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 180,
                                    labelAlign:'right',
                                    labelWidth: 60
                                },
                                border: false,
                                items: [
                                    {
                                        width: 'auto',
                                        name: 'operationLogInfo',
                                        value:'无'
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        name: 'id',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'bcgogoReceivableOrderToBePaidRecordRelationId',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'shopName',
                        xtype: "hiddenfield"
                    }
                ]
            }
        });
        me.callParent();
    },

    setChildWin: function (hardwareOfflinePayWindow,cancelHardwareOrderWindow,hardwareOrderItemChangePriceWindow) {
        this.hardwareOfflinePayWindow = hardwareOfflinePayWindow;
        this.cancelHardwareOrderWindow = cancelHardwareOrderWindow;
        this.hardwareOrderItemChangePriceWindow = hardwareOrderItemChangePriceWindow;
    },
    closeChildWin: function () {
        this.hardwareOfflinePayWindow.close();
        this.cancelHardwareOrderWindow.close();
        this.hardwareOrderItemChangePriceWindow.close();
    },

    close: function () {
        this.commonUtils.unmask();
        this.closeChildWin();
        this.doClose();
    },

    drawBcgogoHardwareOrderDetail: function (order) {
        var me = this, form = this.down('form'),
            baseForm = form.form;
        var bcgogoCustomerInfo = order["shopName"];
        if(!Ext.isEmpty(order["shopMobile"])){
            bcgogoCustomerInfo+="("+order["shopMobile"]+")";
        }
        form.down('[name=bcgogoCustomerInfo]').setValue(bcgogoCustomerInfo);
        form.down('[name=buyChannelsText]').setValue(order["buyChannels"] == "ONLINE_ORDERS" ? "在线订单" : "后台录入");
        var status = "";
        if(order["status"] === "FULL_PAYMENT"){
            status="待发货";
        }else if(order["status"] === "NON_PAYMENT"){
            status="待支付";
        }else if(order["status"] === "SHIPPED"){
            status="已发货";
        }else if(order["status"] === "CANCELED"){
            status="交易取消";
        }
        form.down('[name=statusText]').setValue(status);
        this.show();
    },
    drawBcgogoHardwareOrderItemsList: function (orderItems,orderStatus) {
        if (!orderItems || orderItems.length == 0) return;
        var i, rows = orderItems.length, orderItem,bcgogoProductProperty,priceInfo;
        var table = '<table cellspacing="0" cellpadding="0" style="margin-left:10px;border:1px solid #bfbfbf;width: 750px;">';
        table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px;border-left:none;border-right:none;height: 18px;background-color: #bfbfbf;">';
        table += '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">商品信息</th>';
        table += '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">商品类型</th>';
        table += '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">单价(元)</th>';
        table += '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">采购量</th>';
        table += '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">小计(元)</th>';
        table += '</tr> ';
        for (i = 0; i < rows; i++) {
            orderItem = orderItems[i];
            bcgogoProductProperty = orderItem['productKind'];
            if(!Ext.isEmpty(orderItem['productType'])){
                bcgogoProductProperty+="【"+orderItem['productType']+"】";
            }
            priceInfo = orderItem['price'];
            if(orderStatus=="NON_PAYMENT" && this.permissionUtils.hasPermission("CRM.SALES_MANAGER.HARDWARE.CHANGE_PRICE")){
                priceInfo+='&nbsp&nbsp<a id="form-change-price-'+orderItem['idStr']+'" data-orderitem-id="'+orderItem['idStr']+'" href="javascript:void(0);" style="cursor: pointer;">改价</a>';
            }
            table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;height: 70px;">';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;"><div style="float: left"><img style="width: 60px;height: 60px" src="'+orderItem['imageUrl']+'"></div><div style="float: left;margin-left: 10px;margin-top: 10px;">' + orderItem['productName'] + '</div></td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + bcgogoProductProperty + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + priceInfo + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + orderItem['amount'] + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + orderItem['total'] + '</td>';
            table += '</tr> ';
        }
        table += '</table>';
        this.down('[name=orderItemsInfo]')
            .setHeight(70 * rows+20)
            .setValue(table);
    },
    drawBcgogoHardwareOrderRecordList: function (recordDTOs) {
        if (!recordDTOs || recordDTOs.length == 0) return;
        var i, rows = recordDTOs.length, recordDTO,recordInfo;
        var table = '<table cellspacing="0" cellpadding="0" style="margin-left:10px;border:1px solid #bfbfbf;width: 100%;background-color: #bfbfbf;">';
        for (i = 0; i < rows; i++) {
            recordDTO = recordDTOs[i];
            if(recordDTO["paymentMethod"]=="DOOR_CHARGE"){
                recordInfo = recordDTO['payeeName']+'于'+Ext.util.Format.date(new Date(Number(recordDTO['recordPaymentTime'])), 'Y-m-d')+'上门收取￥'+Ext.util.Format.number(recordDTO['recordPaidAmount'], '0.00');
                recordInfo +='<span style="color: gray">（'+recordDTO['submitterName']+'录入此条操作信息';
                if(recordDTO["status"]=="HAS_BEEN_PAID"){
                    recordInfo+=','+recordDTO["auditorName"]+'于'+Ext.util.Format.date(new Date(Number(recordDTO['auditTime'])), 'Y-m-d H:i')+'审核入账';
                }
                recordInfo +='）</span>；';
            }else{
                recordInfo = '客户于'+Ext.util.Format.date(new Date(Number(recordDTO['recordPaymentTime'])), 'Y-m-d')+'通过银联在线支付￥'+Ext.util.Format.number(recordDTO['recordPaidAmount'], '0.00');
                if(recordDTO["status"]=="HAS_BEEN_PAID"){
                    recordInfo+='<span style="color: gray">（'+recordDTO["auditorName"]+'于'+Ext.util.Format.date(new Date(Number(recordDTO['auditTime'])), 'Y-m-d H:i')+'审核入账）</span>；';
                }
            }
            table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;">';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(recordDTO['submitTime'])), 'Y-m-d H:i') + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + recordInfo + '</td>';
            table += '</tr> ';
        }
        table += '</table>';
        this.down('[name=payRecordInfo]')
            .setHeight(20 * rows)
            .setValue(table);
    },
    drawBcgogoHardwareOrderOperationLogList: function (operationLogDTOs) {
        if (!operationLogDTOs || operationLogDTOs.length == 0) return;
        var i, rows = operationLogDTOs.length, operationLogDTO;
        var table = '<table id="_operationLogTable" cellspacing="0" cellpadding="0" style="margin-left:10px;border:1px solid #bfbfbf;width: 100%;background-color: #bfbfbf;">';
        for (i = 0; i < rows; i++) {
            operationLogDTO = operationLogDTOs[i];
            table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;">';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(operationLogDTO['creationDate'])), 'Y-m-d H:i') + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;white-space:normal;width: 800px">' + operationLogDTO['content'] + '</td>';
            table += '</tr> ';
        }
        table += '</table>';
        this.down('[name=operationLogInfo]').setValue(table);
        this.down('[name=operationLogInfo]').setHeight(Ext.get("_operationLogTable").getHeight());
    }
});

