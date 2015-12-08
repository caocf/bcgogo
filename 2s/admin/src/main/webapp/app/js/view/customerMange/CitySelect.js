Ext.define('Ext.view.customerMange.CitySelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.citySelect',
    emptyText:'市',
    displayField:'name',
    valueField:'no',
    editable:false,
    queryMode:'remote',
    initComponent:function () {
        var me = this,
            store = Ext.create('Ext.store.customerMange.Area');
        Ext.apply(me, {
            store:store
        });
        me.callParent();
    },
    setProvince:function (province) {
        this.province = province;
    },
    getProvince:function () {
        return this.province;
    }
});