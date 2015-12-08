Ext.define('Ext.store.customerMange.RegisterType', {
    extend: 'Ext.data.Store',
    fields: ['label', 'value'],
    data: [
        {"value": "ALL", "label": "全部状态"},
        {"value": "SALESMAN_REGISTER", "label": "业务员注册"},
        {"value": "SYSTEM_INVITE_SUPPLIER", "label": "系统邀请供应商"},
        {"value": "SYSTEM_INVITE_CUSTOMER", "label": "系统邀请客户"},
        {"value": "CUSTOMER_INVITE", "label": "客户邀请"},
        {"value": "SUPPLIER_INVITE", "label": "供应商邀请"},
        {"value": "SUPPLIER_REGISTER", "label": "供应商升级"}
    ]
});