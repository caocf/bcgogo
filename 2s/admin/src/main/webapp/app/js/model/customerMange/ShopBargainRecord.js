/**
 * 议价记录 model
 */
Ext.define('Ext.model.customerMange.ShopBargainRecord', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'shopId', type: 'string'},
        {name: 'bargainStatus', type: 'string'},
        {name: 'originalPrice', type: 'string'},
        {name: 'auditPrice', type: 'string'},
        {name: 'applicantId', type: 'string'},
        {name: 'applicantName', type: 'string'},
        {name: 'applicationTime', type: 'string'},
        {name: 'applicationPrice', type: 'string'},
        {name: 'applicationReason', type: 'string'},
        {name: 'auditorId', type: 'string'},
        {name: 'auditorName', type: 'string'},
        {name: 'auditTime', type: 'string'},
        {name: 'auditReason', type: 'string'}
    ]

});