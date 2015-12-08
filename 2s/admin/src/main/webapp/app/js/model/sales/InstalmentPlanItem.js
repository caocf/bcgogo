/**
 * BcgogoReceivableRecord model
 */
Ext.define('Ext.model.sales.InstalmentPlanItem', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'shopId', type: 'string'},
        {name: 'instalmentPlanId', type: 'string'},   //分期id
        {name: 'currentAmount', type: 'string'},  //本期金额
        {name: 'payableAmount', type: 'string'},  //应付金额
        {name: 'paidAmount', type: 'string'},       //已付金额
        {name: 'proportion', type: 'string'},       //所占比例
        {name: 'periodNumber', type: 'string'},     //第几期
        {name: 'endTime', type: 'string'},           //截止日期
        {name: 'endTimeStr', type: 'string'},           //截止日期
        {name: 'status', type: 'string'},            //状态
        {name: 'statusValue', type: 'string'},       //状态
        {name: 'paymentMethod', type: 'string'},    //支付方式
        {name: 'nextItemId', type: 'string'},
        {name: 'memo', type: 'string'}
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