/**
 * 店铺  客户短信账单
 */
Ext.define('Ext.model.finance.BcgogoSmsRecord', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'operatorId', type: 'string'},
        {name: 'operateTime', type: 'string'},
        {name: 'smsCategory', type: 'string'},
        {name: 'balance', type: 'string'},
        {name: 'number', type: 'string'},
        {name: 'rechargeTime', type: 'string'}

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