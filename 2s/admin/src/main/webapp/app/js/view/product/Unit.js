Ext.define('Ext.view.product.Unit', {
    extend: 'Ext.form.ComboBox',
    alias: 'widget.product.unit',
    editable: true,
    store: Ext.create('Ext.data.Store', {
        fields: ['value'],
        data: [
            {"value": "个"},
            {"value": "只"},
            {"value": "件"},
            {"value": "条"},
            {"value": "瓶"},
            {"value": "台"},
            {"value": "箱"},
            {"value": "包"},
            {"value": "捆"},
            {"value": "架"}
        ]
    }),
    queryMode: 'local',
    displayField: 'value',
    valueField: 'value'
});