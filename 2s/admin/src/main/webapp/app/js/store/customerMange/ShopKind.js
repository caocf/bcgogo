Ext.define('Ext.store.customerMange.ShopKind', {
    extend: 'Ext.data.Store',
    fields: ['label', 'value'],
    data: [
        {"value": "OFFICIAL", "label": "正式店"},
        {"value": "TEST", "label": "测试店"}
    ]
});