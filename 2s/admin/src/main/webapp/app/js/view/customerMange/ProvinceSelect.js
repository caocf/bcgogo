Ext.define('Ext.view.customerMange.ProvinceSelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.provinceSelect',
    emptyText:'省',
    displayField:'name',
    valueField:'no',
    editable:false,
    queryMode: 'remote',
    initComponent:function () {
        var me = this,
            store = Ext.create('Ext.store.customerMange.Area');
        Ext.apply(me, {
            store:store
        });
        me.callParent();
    }
});