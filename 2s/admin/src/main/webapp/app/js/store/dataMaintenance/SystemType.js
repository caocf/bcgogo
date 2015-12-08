Ext.define('Ext.store.dataMaintenance.SystemType', {
    extend:'Ext.data.Store',
    fields:['label', 'value'],
    data:[
        {"value":"CRM", "label":"CRM"},
        {"value":"SHOP", "label":"SHOP"}
    ]
});