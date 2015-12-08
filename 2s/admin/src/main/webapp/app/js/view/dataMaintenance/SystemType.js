Ext.define('Ext.view.dataMaintenance.SystemType', {
    extend:'Ext.form.ComboBox',
    alias:'widget.systemType',
    editable:false,
    store:Ext.create('Ext.store.dataMaintenance.SystemType'),
    queryMode:'local',
    displayField:'label',
    valueField:'value'
});