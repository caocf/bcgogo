/**
 * BcgogoReceivableRecord model
 */
Ext.define('Ext.model.finance.HardwareSoftwareAccount', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'shopId', type: 'string'},
        {name: 'shopName', type: 'string'},
        {name: 'hardwareReceivedAmount', type: 'string'},
        {name: 'hardwareReceivableAmount', type: 'string'},
        {name: 'hardwareTotalAmount', type: 'string'},
        {name: 'softwareReceivedAmount', type: 'string'},
        {name: 'softwareReceivableAmount', type: 'string'},
        {name: 'softwareTotalAmount', type: 'string'},
        {name: 'totalReceivedAmount', type: 'string'},
        {name: 'totalReceivableAmount', type: 'string'},
        {name: 'orders'},
        {name: 'totalAmount', type: 'string'}
    ],
    hasMany  : {model: 'Ext.model.finance.HardwareSoftwareOrder', name: 'orders'},
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