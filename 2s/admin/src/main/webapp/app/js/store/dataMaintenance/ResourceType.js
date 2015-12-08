Ext.define('Ext.store.dataMaintenance.ResourceType', {
    extend:'Ext.data.Store',
    fields:['label', 'value'],
    data:[
        {"value":"request", "label":"request"},
        {"value":"render", "label":"render"},
        {"value":"logic", "label":"logic"},
        {"value":"menu", "label":"menu"}
    ]
});