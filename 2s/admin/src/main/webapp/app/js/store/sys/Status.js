Ext.define('Ext.store.sys.Status', {
    extend:'Ext.data.Store',
    fields:['label', 'value'],
    data:[
        {"value":"all", "label":"全部状态"},
        {"value":"active", "label":"启用"},
        {"value":"inActive", "label":"禁用"}
    ]
});