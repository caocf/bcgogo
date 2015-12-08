Ext.define('Ext.view.sys.module.ModuleSelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.moduleselect',
    emptyText:'全部状态',
    editable:false,
    store:Ext.create('Ext.store.sys.Modules'),
    remoteFilter:true,
    queryMode:'remote',
    queryParam:'name',
    displayField:'label',
    valueField:'value'
});