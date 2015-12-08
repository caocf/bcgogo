Ext.define('Ext.view.sales.InstalmentPlanAlgorithmSelect', {
    extend: 'Ext.form.ComboBox',
    alias: 'widget.instalmentPlanAlgorithmSelect',
    displayField: 'name',
    valueField: 'id',
    editable: false,
    queryMode: 'remote',
    initComponent: function () {
        var me = this,
            store = Ext.create('Ext.store.sales.InstalmentPlanAlgorithms');
        Ext.apply(me, {
            store: store
        });
        me.callParent();
    }
});