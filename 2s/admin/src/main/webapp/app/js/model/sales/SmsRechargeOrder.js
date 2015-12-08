/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-26
 * Time: 下午12:54
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.model.sales.SmsRechargeOrder', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'payTime', type: 'string'},
        {name: 'rechargeAmount', type: 'string'},
        {name: 'presentAmount', type: 'string'},
        {name: 'shopId', type: 'string'},
        {name: 'shopName', type: 'string'},
        {name: 'receiptNo', type: 'string'},
        {name: 'rechargeMethod', type: 'string'},
        {name: 'status', type: 'string'},
        {name: 'paymentWay', type: 'string'}
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

