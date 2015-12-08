/**
 * 店铺  客户短信账单
 */
Ext.define('Ext.model.finance.ShopSmsAccount', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'shopId', type: 'string'},
        {name: 'shopName', type: 'string'},
        {name: 'rechargeBalance', type: 'string'},
        {name: 'rechargeNumber', type: 'string'},
        {name: 'handSelBalance', type: 'string'},
        {name: 'handSelNumber', type: 'string'},
        {name: 'consumptionBalance', type: 'string'},
        {name: 'consumptionNumber', type: 'string'},
        {name: 'currentBalance', type: 'string'},
        {name: 'currentNumber', type: 'string'}

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