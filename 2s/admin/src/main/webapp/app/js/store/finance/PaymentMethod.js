Ext.define('Ext.store.finance.PaymentMethod', {
    extend: 'Ext.data.Store',
    fields: ['label', 'value'],
    data: [
        {"value": "CUP_TRANSFER", "label": "银联转账"},
        {"value": "DOOR_CHARGE", "label": "上门收取"}
    ]
});