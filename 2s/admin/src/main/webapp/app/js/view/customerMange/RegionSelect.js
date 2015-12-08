Ext.define('Ext.view.customerMange.RegionSelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.regionSelect',
    emptyText:'区',
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
    setCity:function (city) {
        this.city = city;
    },
    getCity:function () {
        return this.city;
    }
});