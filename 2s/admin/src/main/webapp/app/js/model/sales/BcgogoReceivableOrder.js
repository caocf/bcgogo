Ext.define('Ext.model.sales.BcgogoReceivableOrder', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'receiptNo', type: 'string'},
        {name: 'receivableContent', type: 'string'},
        {name: 'createdTime', type: 'string'},
        {name: 'currentInstalmentPlanEndTime', type: 'string'},
        {name: 'oldTotalAmount', type: 'string'},         //old总金额  议价后用的
        {name: 'totalAmount', type: 'string'},         //总金额
        {name: 'receivableAmount', type: 'string'},   //应付总金额
        {name: 'receivedAmount', type: 'string'},       //已付总金额

        {name: 'bcgogoReceivableOrderToBePaidRecordRelationId', type: 'string'},
        {name: 'paymentType', type: 'string'},
        {name: 'buyChannels', type: 'string'},
        {name: 'followName', type: 'string'},

        {name: 'shopId', type: 'string'},
        {name: 'shopName', type: 'string'},
        {name: 'shopOwner', type: 'string'},
        {name: 'shopMobile', type: 'string'},
        {name: 'shopVersion', type: 'string'},
        {name: 'bargainStatus', type: 'string'},
        {name: 'bargainPrice', type: 'string'},
        {name: 'status', type: 'string'},
        {name: 'instalmentPlanId', type: 'string'},
        {name: 'addressDetail', type: 'string'},
        {name: 'address', type: 'string'},
        {name: 'contact', type: 'string'},
        {name: 'mobile', type: 'string'},
        {name: 'chargeType', type: 'string'},
        {name: 'currentPayableAmount', type: 'string'},
        {name: 'currentPeriodNumberInfo', type: 'string'},
        {name: 'receivableMethod', type: 'string'},
        {name: 'bcgogoReceivableOrderItemDTOList'},
        {name: 'bcgogoReceivableOrderPaidRecordDTOList'},
        {name: 'bcgogoReceivableOrderToBePaidRecordDTO'}


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
