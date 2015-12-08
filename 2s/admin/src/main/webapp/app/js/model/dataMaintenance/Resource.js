Ext.define('Ext.model.dataMaintenance.Resource', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'resourceId', type: 'string'},
        {name: 'name', type: 'string'},
        {name: 'value', type: 'string'},
        {name: 'memo', type: 'string'},
        {name: 'status', type: 'string'},
        {name: 'systemType', type: 'string'},
        {name: 'type', type: 'string'},
        //menu
        {name: 'menuId', type: 'string'},
        {name: 'resourceId', type: 'string'},
        {name: 'parentId', type: 'string'},
        {name: 'label', type: 'string'},
        {name: 'href', type: 'string'},
        {name: 'grade', type: 'string'}
    ]
});