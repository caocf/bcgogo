/**
 * BcgogoReceivableRecord model
 */
Ext.define('Ext.model.finance.BcgogoReceivableRecord', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'receivableContent', type: 'string'},
        {name: 'orderReceiptNo', type: 'string'},
        {name: 'orderId', type: 'string'},
        {name: 'time', type: 'string'},
        {name: 'orderStartTime', type: 'string'},
        {name: 'orderEndTime', type: 'string'},
        {name: 'currentInstalmentPlanEndTime', type: 'string'},
        {name: 'orderTotalAmount', type: 'string'},         //总金额
        {name: 'orderReceivableAmount', type: 'string'},   //应付总金额
        {name: 'orderReceivedAmount', type: 'string'},       //已付总金额

        {name: 'bcgogoReceivableOrderRecordRelationId', type: 'string'},
        {name: 'receivableMethod', type: 'string'},   //INSTALLMENT, FULL
        {name: 'paymentMethod', type: 'string'},
        {name: 'orderPaymentType', type: 'string'},
        {name: 'shopVersion', type: 'string'},
        {name: 'shopReviewDate', type: 'string'},
        {name: 'followName', type: 'string'},

        {name: 'shopId', type: 'string'},
        {name: 'shopName', type: 'string'},
        {name: 'recordPaymentTime', type: 'string'},//收款时间
        {name: 'recordPaymentAmount', type: 'string'},//record应支付金额
        {name: 'recordPaidAmount', type: 'string'},//已经支付金额
        {name: 'payeeId', type: 'string'},       //收款人（只有线下才会有）
        {name: 'payeeName', type: 'string'},       //收款人（只有线下才会有）
        {name: 'operatorId', type: 'string'},
        {name: 'operatorName', type: 'string'},
        {name: 'operatorTime', type: 'string'},
        {name: 'submitterId', type: 'string'},
        {name: 'submitterName', type: 'string'},
        {name: 'submitTime', type: 'string'},
        {name: 'auditorId', type: 'string'},
        {name: 'auditorName', type: 'string'},
        {name: 'auditTime', type: 'string'},
        {name: 'status', type: 'string'},
        {name: 'orderPaymentStatus', type: 'string'},

        {name: 'instalmentPlanId', type: 'string'},
        {name: 'periodNumber', type: 'string'},
        {name: 'periods', type: 'string'},
        {name: 'instalmentPlanItemId', type: 'string'},
        {name: 'smsRechargeId', type: 'string'}

    ],
    listeners: {
        exception: function (proxy, response, operation) {
            Ext.MessageBox.show({
                title: '错误异常',
                msg: operation.getError(),
                icon: Ext.MessageBox.ERROR,
                buttons: Ext.Msg.OK
            });
        }
    }
});