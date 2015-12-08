/**
 * 店铺  客户短信账单
 */
Ext.define('Ext.model.finance.BcgogoSmsAccount', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'type', type: 'string'},
        {name: 'totalRechargeBalance', type: 'string'},
        {name: 'totalRechargeNumber', type: 'string'},
        {name: 'handSelNumber', type: 'string'},
        {name: 'consumptionNumber', type: 'string'},
        {name: 'surplusNumber', type: 'string'}

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