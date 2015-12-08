/**
 * 审核窗口
 * @author zhangjuntao
 */
Ext.define('Ext.view.sales.hardware.SoftwareOrderDetailWindow', {
    alias: 'widget.softwareOrderDetailWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    width: 1000,
    height: 600,
    title: '软件销售信息',
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
                buttons: [
                    {
                        id:'bcgogoReceivableOrderBargainApplyBtn',
                        hidden:true,
                        disabled:true,
                        action:'bcgogoReceivableOrderBargainApply',
                        text: '议价申请',
                        tooltip: "议价申请"
                    },
                    {
                        id:'bcgogoReceivableOrderBargainAuditBtn',
                        hidden:true,
                        disabled:true,
                        action:'bcgogoReceivableOrderBargainAudit',
                        text: '议价审核',
                        tooltip: "议价审核"
                    },
                    {
                        id:'bcgogoReceivableOrderOfflinePayBtn',
                        hidden:true,
                        disabled:true,
                        action:'bcgogoReceivableOrderOfflinePay',
                        text: '线下支付',
                        tooltip: "线下支付"
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
                                        width: 180,
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
                                    padding: 0,
                                    margin: "0 5 0 0"
                                },
                                border: false,
                                items: [
                                    {
                                        width: 'auto',
                                        name: 'instalmentPlanDetail'
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
                    },
                    {
                        name: 'shopId',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'shopOwner',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'shopMobile',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'shopVersion',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'currentPayableAmount',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'instalmentPlanId',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'receivableMethod',
                        xtype: "hiddenfield"
                    },
                    {
                        name: 'status',
                        xtype: "hiddenfield"
                    }
                ]
            }
        });
        me.callParent();
    },

    setChildWin: function (bargainAuditWindow,bargainApplyWindow,softwareOfflineInstalmentPayWindow,softwareOfflinePayWindow,softwareOfflineUnconstrainedPayWindow) {
        this.bargainAuditWindow = bargainAuditWindow;
        this.bargainApplyWindow = bargainApplyWindow;
        this.softwareOfflineInstalmentPayWindow = softwareOfflineInstalmentPayWindow;
        this.softwareOfflinePayWindow = softwareOfflinePayWindow;
        this.softwareOfflineUnconstrainedPayWindow = softwareOfflineUnconstrainedPayWindow;
    },
    closeChildWin: function () {
        this.bargainAuditWindow.close();
        this.bargainApplyWindow.close();
        this.softwareOfflineInstalmentPayWindow.close();
        this.softwareOfflinePayWindow.close();
        this.softwareOfflineUnconstrainedPayWindow.close();
    },

    close: function () {
        this.commonUtils.unmask();
        this.closeChildWin();
        this.doClose();
    },

    drawBcgogoSoftwareOrderDetail: function (order) {
        var me = this, form = this.down('form'),
            baseForm = form.form;
        var bcgogoCustomerInfo = order["shopName"];
        if(!Ext.isEmpty(order["shopMobile"])){
            bcgogoCustomerInfo+="("+order["shopMobile"]+")";
        }
        form.down('[name=bcgogoCustomerInfo]').setValue(bcgogoCustomerInfo);
        form.down('[name=buyChannelsText]').setValue(order["buyChannels"] == "ONLINE_ORDERS" ? "在线订单" : "后台录入");
        var status = "";
        if(order['chargeType'] ==='ONE_TIME'){
            if(order["status"] === "FULL_PAYMENT"){
                status="已支付";
            }else if(order["status"] === "NON_PAYMENT" || order["status"] === "PARTIAL_PAYMENT"){
                status="待支付";
            }
        }else{
            status="第1年免费,第2年待付";
        }

        form.down('[name=statusText]').setValue(status);

        var orderItems = order['bcgogoReceivableOrderItemDTOList'];
        if (!Ext.isEmpty(orderItems)){
            this.drawBcgogoSoftwareOrderItemsList(order,orderItems);
        }
        if(order['chargeType'] ==='ONE_TIME'){
            var instalmentPlanDTO = order['instalmentPlanDTO'];
            if (!Ext.isEmpty(instalmentPlanDTO)){
                this.drawBcgogoSoftwareOrderInstalmentPlanDetail(order,instalmentPlanDTO);
            }

            //支付记录
            var recordDTOs = order['bcgogoReceivableOrderPaidRecordDTOList'];
            if (!Ext.isEmpty(recordDTOs)){
                this.drawBcgogoSoftwareOrderRecordList(recordDTOs);
            }
        }

        //操作记录
        var operationLogDTOs = order['operationLogDTOList'];
        if (!Ext.isEmpty(operationLogDTOs)){
            this.drawBcgogoSoftwareOrderOperationLogList(operationLogDTOs);
        }
        this.show();
    },
    drawBcgogoSoftwareOrderItemsList: function (order,orderItems) {
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
            var priceInfo = orderItem['price'];
            if(!Ext.isEmpty(order['bargainPrice']) && !Ext.isEmpty(order['bargainStatus']) ){
                if(order['bargainStatus']==='AUDIT_PASS'){
                    priceInfo= "<span style='text-decoration:line-through'>"+order['oldTotalAmount']+"</span> <span style='color: #FF6600;'>"+order['bargainPrice']+"</span><br><span style='color: #FF6600;'>议价通过</span>";
                }else if(order['bargainStatus']==='PENDING_REVIEW'){
                    priceInfo= "<span style='text-decoration:line-through'>"+orderItem['price']+"</span> <span style='color: #0000FF;'>"+order['bargainPrice']+"</span><br><span style='color: #0000FF;'>议价申请中</span>";
                }else if(order['bargainStatus']==='AUDIT_REFUSE'){
                    priceInfo= orderItem['price']+" <span style='text-decoration:line-through;color: #FF0000;'>"+order['bargainPrice']+"</span>"+"<br><span style='color: #FF0000;'>议价未通过</span>";
                }
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
            .setValue(table);
    },
    drawBcgogoSoftwareOrderInstalmentPlanDetail:function(order,instalmentPlanDTO){
        var colHtml ="",titleHtml="",payScaleHtml="",payAmountHtml="",payStatusHtml="",endTimeHtml="";
        var temp = 0,firstInstallmentPayment=0;
        var instalmentPlanItemDTOList = instalmentPlanDTO['instalmentPlanItemDTOList'];
        if(instalmentPlanItemDTOList){
            Ext.Array.forEach(instalmentPlanItemDTOList, function(instalmentPlanItemDTO,index,array){
                colHtml+='<col width="70">';
                titleHtml+='<td style="border:1px solid #999999">'+(index+1)+'期</td>';
                payScaleHtml+='<td style="border:1px solid #999999">'+ instalmentPlanItemDTO['proportion'] * 100 + '%</td>';
                if(index==instalmentPlanItemDTOList.length-1){
                    payAmountHtml+='<td style="border:1px solid #999999">￥'+(order['totalAmount']*1-temp)+'</td>';
                }else{
                    if(index==0) {
                        firstInstallmentPayment = Ext.util.Format.round(order['totalAmount'] * instalmentPlanItemDTO['proportion'],0);
                    }
                    temp += Ext.util.Format.round(order['totalAmount'] * instalmentPlanItemDTO['proportion'],0);
                    payAmountHtml+='<td style="border:1px solid #999999">￥'+Ext.util.Format.round(order['totalAmount'] * instalmentPlanItemDTO['proportion'],0)+'</td>';
                }
                if(instalmentPlanItemDTO['status']=="FULL_PAYMENT"){
                    payStatusHtml+='<td style="color:green;border:1px solid #999999">已付</td>';
                    endTimeHtml+='<td style="border:1px solid #999999">--</td>';
                }else if(instalmentPlanItemDTO['status']=="PARTIAL_PAYMENT"){
                    payStatusHtml+='<td style="color:red;border:1px solid #999999">已付￥'+instalmentPlanItemDTO['paidAmount']+'</td>';
                    endTimeHtml+='<td style="border:1px solid #999999">'+instalmentPlanItemDTO['endTimeStr']+'</td>';
                }else if(instalmentPlanItemDTO['status']=="NON_PAYMENT"){
                    payStatusHtml+='<td style="color:red;border:1px solid #999999">待付</td>';
                    endTimeHtml+='<td style="border:1px solid #999999">'+instalmentPlanItemDTO['endTimeStr']+'</td>';
                }
            });

            var instalmentPlanDetailHtml='<div>共<span style="font-weight:bold">'+instalmentPlanDTO['periods']+'</span>期,' +
                '已付总额:<span style="color:green;font-weight:bold">'+order['receivedAmount']+'</span>元,' +
                '未付总额:<span style="color:red;font-weight:bold">'+order['receivableAmount']+'</span>元.' +
                '本次应付第<span style="color:red;font-weight:bold">'+order['currentPeriodNumberInfo']+'</span>期,' +
                '共<span style="color:red;font-weight:bold">' + order['currentPayableAmount'] + '</span>元</div>';

            instalmentPlanDetailHtml+='<table cellpadding="0" cellspacing="0" class="software_receivable_form_table">';
            instalmentPlanDetailHtml+='  <colgroup>';
            instalmentPlanDetailHtml+='      <col width="70">';
            instalmentPlanDetailHtml+=colHtml;
            instalmentPlanDetailHtml+='      </colgroup>';
            instalmentPlanDetailHtml+='  <tr style="border:1px solid #999999">';
            instalmentPlanDetailHtml+='      <td style="border:1px solid #999999">期 数</td>';
            instalmentPlanDetailHtml+=titleHtml;
            instalmentPlanDetailHtml+='  </tr>';
            instalmentPlanDetailHtml+='  <tr style="border:1px solid #999999">';
            instalmentPlanDetailHtml+='      <td style="border:1px solid #999999">支付比例</td>';
            instalmentPlanDetailHtml+=payScaleHtml;
            instalmentPlanDetailHtml+='  </tr>';
            instalmentPlanDetailHtml+='  <tr style="border:1px solid #999999">';
            instalmentPlanDetailHtml+='      <td style="border:1px solid #999999">付款金额</td>';
            instalmentPlanDetailHtml+=payAmountHtml;
            instalmentPlanDetailHtml+='  </tr>';
            instalmentPlanDetailHtml+='  <tr style="border:1px solid #999999">';
            instalmentPlanDetailHtml+='      <td style="border:1px solid #999999">付款状态</td>';
            instalmentPlanDetailHtml+=payStatusHtml;
            instalmentPlanDetailHtml+='  </tr>';
            instalmentPlanDetailHtml+='  <tr style="border:1px solid #999999">';
            instalmentPlanDetailHtml+='      <td style="border:1px solid #999999">截止时间</td>';
            instalmentPlanDetailHtml+=endTimeHtml;
            instalmentPlanDetailHtml+='  </tr>';
            instalmentPlanDetailHtml+='  <tr style="font-weight: bold;border:1px solid #999999">';
            instalmentPlanDetailHtml+='      <td style="border:1px solid #999999">合计</td>';
            instalmentPlanDetailHtml+='      <td style="border:1px solid #999999" colspan="'+instalmentPlanItemDTOList.length+'">'+order['totalAmount']+'</td>';
            instalmentPlanDetailHtml+='  </tr>';
            instalmentPlanDetailHtml+='</table>';
        }
        this.down('[name=instalmentPlanDetail]')
            .setHeight(150)
            .setValue(instalmentPlanDetailHtml);
    },
    drawBcgogoSoftwareOrderRecordList: function (recordDTOs) {
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
    drawBcgogoSoftwareOrderOperationLogList: function (operationLogDTOs) {
        if (!operationLogDTOs || operationLogDTOs.length == 0) return;
        var i, rows = operationLogDTOs.length, operationLogDTO;
        var table = '<table cellspacing="0" cellpadding="0" style="margin-left:10px;border:1px solid #bfbfbf;width: 100%;background-color: #bfbfbf;">';
        for (i = 0; i < rows; i++) {
            operationLogDTO = operationLogDTOs[i];
            table += '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;">';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;">' + Ext.util.Format.date(new Date(Number(operationLogDTO['creationDate'])), 'Y-m-d H:i') + '</td>';
            table += '<td style=" border:1px solid #bfbfbf;padding-left:10px;white-space:normal;">' + operationLogDTO['content'] + '</td>';
            table += '</tr> ';
        }
        table += '</table>';
        this.down('[name=operationLogInfo]')
            .setHeight(20 * rows)
            .setValue(table);
    }
});

