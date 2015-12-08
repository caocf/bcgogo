Ext.define('Ext.store.finance.ShopSmsRecords', {
    extend: 'Ext.data.Store',
    model: "Ext.model.finance.ShopSmsRecord",
    pageSize: 25,
    proxy: {
        type: 'ajax',
        api: {
            read: 'shopSmsAccount.do?method=searchShopSmsRecordResult'
        },
        reader: {
            type: 'json',
            root: "data",
            totalProperty: "total"
        },
        writer: {
            writeAllFields: true,
            type: 'json'
        }
    },
    remoteSort: false // If false, sorting is done locally on the client.
});