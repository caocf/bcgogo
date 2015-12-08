Ext.define('Ext.view.dataMaintenance.ResourceType', {
    extend:'Ext.form.ComboBox',
    alias:'widget.resourceType',
    store:Ext.create('Ext.store.dataMaintenance.ResourceType'),
    queryMode:'local',
    displayField:'label',
    editable:false,
    emptyText:'type',
    valueField:'value'
});