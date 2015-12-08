Ext.define('Ext.view.customerMange.RegisterType', {
    extend:'Ext.form.ComboBox',
    alias:'widget.registerType',
    emptyText:'全部状态',
    editable:false,
    store:Ext.create('Ext.store.customerMange.RegisterType'),
    queryMode:'local',
    displayField:'label',
    valueField:'value'
});