/**
 * 议价记录 model
 */
Ext.define('Ext.model.customerMange.ShopExtensionLog', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'shopId', type: 'string'},
        {name: 'extensionDays', type: 'string'},
        {name: 'operatorId', type: 'string'},
        {name: 'operatorName', type: 'string'},
        {name: 'operateTime', type: 'string'},
        {name: 'trialEndTime', type: 'string'},
        {name: 'reason', type: 'string'}
    ]

});