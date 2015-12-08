/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 14-1-9
 * Time: 上午11:52
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.sales.sms.SmsOrderDetailWindow', {
    alias: 'widget.smsOrderDetailWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    width: 1000,
    height: 600,
    title: '短信销售信息',
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: {
                xtype: 'form',
                bodyPadding: 5,
                width: 1000,
                height: 450,
                autoScroll: true,
                frame: false,
                border: false,
                items: [
                    {
                        xtype: 'fieldset',
                        title: '订单信息',
                        layout: 'anchor',
                        margin: "0 0 0 0",
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
                                margin: "0 0 0 0",
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
                                        name: 'smsOrderInfo'
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
                                        name: 'operationRecordInfo',
                                        value:'无'
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
                    }
                ]
            }
        });
        me.callParent();
    },
    drawSmsOrderDetail: function(order){
        var me = this, form = this.down('form'),
            baseForm = form.form;
        me.drawSmsOrderInfo(order);
        me.drawSmsOperationRecord(order);
        me.drawPaymentRecord(order);
    },
    drawSmsOrderInfo: function(order){
        var me = this;
        var table = '<table cellspacing="0" cellpadding="0" style="margin-left:10px;border:1px solid #dddddd;width: 928px;">';
        table += '<tr style=" border:1px solid #dddddd;padding-left:10px; border-left:none;border-right:none;height: 20px;">';
        table += '<td style=" border:1px solid #dddddd;text-align:center;background-color:#bbbbbb;">' + '销售单号' + '</td>';
        if(order.rechargeMethod == 'CUSTOMER_RECHARGE') {
            table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + order["receiptNo"] + ' (客户充值)' + '</td>';
        } else if(order.rechargeMethod == 'CRM_RECHARGE') {
            table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + order["receiptNo"] + ' (后台充值)' + '</td>';
        }

        table += '</tr>'
        table += '<tr style=" border:1px solid #dddddd;padding-left:10px; border-left:none;border-right:none;height: 20px;">';
        table += '<td style=" border:1px solid #dddddd;text-align:center;background-color:#bbbbbb;">' + '充值时间' + '</td>';
        table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(order["payTime"])), 'Y-m-d H:i') + '</td>';
        table += '</tr>'
        table += '<tr style=" border:1px solid #dddddd;padding-left:10px; border-left:none;border-right:none;height: 20px;">';
        table += '<td style=" border:1px solid #dddddd;text-align:center;background-color:#bbbbbb;">' + '充值店铺' + '</td>';
        table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + order["shopName"] + '</td>';
        table += '</tr>'
        table += '<tr style=" border:1px solid #dddddd;padding-left:10px; border-left:none;border-right:none;height: 20px;">';
        table += '<td style=" border:1px solid #dddddd;text-align:center;background-color:#bbbbbb;">' + '充值金额' + '</td>';
        table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + order["rechargeAmount"] + '元' + '</td>';
        table += '</tr>'
        table += '<tr style=" border:1px solid #dddddd;padding-left:10px; border-left:none;border-right:none;height: 20px;">';
        table += '<td style=" border:1px solid #dddddd;text-align:center;background-color:#bbbbbb;">' + '享受优惠' + '</td>';
        table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + (order["presentAmount"] > 0?'赠送'+order["presentAmount"]+'元':'--')  + '</td>';
        table += '</tr>'
        table += '<tr style=" border:1px solid #dddddd;padding-left:10px; border-left:none;border-right:none;height: 20px;">';
        table += '<td style=" border:1px solid #dddddd;text-align:center;background-color:#bbbbbb;">' + '实付金额' + '</td>';
        table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + order["rechargeAmount"] + '元' + '</td>';
        table += '</tr>'
        table += '<tr style=" border:1px solid #dddddd;padding-left:10px; border-left:none;border-right:none;height: 20px;">';
        table += '<td style=" border:1px solid #dddddd;text-align:center;background-color:#bbbbbb;">' + '状态' + '</td>';
        table += '<td style=" border:1px solid #dddddd;padding-left:10px;">' + (order["status"] == 'PENDING_REVIEW' ? '待审核': '已入账') + '</td>';
        table += '</tr></table>';
        me.down("[name=smsOrderInfo]").setValue(table);
    },
    drawSmsOperationRecord: function(order){
        var me = this;
        var recordInfo;
        if(order["rechargeMethod"] === "CUSTOMER_RECHARGE") {
            recordInfo = '客户在线充值￥' + order["rechargeAmount"];
        } else if(order["rechargeMethod"] === "CRM_RECHARGE") {
            recordInfo = order["submitor"] + '录入客户现金充值￥' + order["rechargeAmount"];
        }
        var table = '<table cellspacing="0" cellpadding="0" style="margin-left:10px;border:1px solid #bfbfbf;width: 100%;background-color: #bfbfbf;">';
        table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;">';
        table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(order['payTime'])), 'Y-m-d H:i') + '</td>';
        table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + recordInfo + '</td>';
        table += '</tr> ';
        if(order["presentAmount"]*1 > 0) {
            table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;">';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(order['payTime'])), 'Y-m-d H:i') + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">系统自动赠送短信金额￥' + order["presentAmount"] + '</td>';
            table += '</tr> ';
        }
        table += '</table> ';
        me.down("[name=operationRecordInfo]").setValue(table);
    },
    drawPaymentRecord: function(order){
        var me = this;
        var recordInfo;
        if(order["rechargeMethod"] === "CUSTOMER_RECHARGE") {
            recordInfo = '客户通过银联支付￥' + order["rechargeAmount"];
        } else if(order["rechargeMethod"] === "CRM_RECHARGE") {
            recordInfo = order["payeeName"] + '上门收取￥' + order["rechargeAmount"];
            recordInfo +='<span style="color: gray">（'+order['submitor']+'录入此条操作信息）';
        }
        var table = '<table cellspacing="0" cellpadding="0" style="margin-left:10px;border:1px solid #bfbfbf;width: 100%;background-color: #bfbfbf;">';
        table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;">';
        table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(order['payTime'])), 'Y-m-d H:i') + '</td>';
        table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + recordInfo + '</td>';
        table += '</tr> ';
        if(order["operationLogDTO"]) {
            table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;">';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(order['auditTime'])), 'Y-m-d H:i') + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + order["operationLogDTO"]["userName"] + '审核入账￥'+ order["rechargeAmount"] + '</td>';
            table += '</tr> ';
        }
        table += '</table> ';
        me.down("[name=payRecordInfo]").setValue(table);
    }

});


