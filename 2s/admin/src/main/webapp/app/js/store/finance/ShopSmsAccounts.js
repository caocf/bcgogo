Ext.define('Ext.store.finance.ShopSmsAccounts', {
    extend: 'Ext.data.Store',
    model: "Ext.model.finance.ShopSmsAccount",
    pageSize: 25,
    proxy: {
        type: 'ajax',
        api: {
            read: 'shopSmsAccount.do?method=searchShopSmsAccountResult'
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