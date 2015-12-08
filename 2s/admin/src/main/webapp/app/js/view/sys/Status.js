Ext.define('Ext.view.sys.Status', {
    extend:'Ext.form.ComboBox',
    alias:'widget.sysstatus',
    emptyText:'全部状态',
    editable:false,
    store:Ext.create('Ext.store.sys.Status'),
    queryMode:'local',
    displayField:'label',
    valueField:'value',

    getDisplayName:function (value) {
        if (value == "active") return "启用";
        else if (value == "inActive") return "禁用";
        else return "全部状态";
    }
});