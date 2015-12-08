Ext.define('Ext.store.finance.BcgogoSmsAccounts', {
    extend: 'Ext.data.Store',
    model: "Ext.model.finance.BcgogoSmsAccount",
    pageSize: 25,
    proxy: {
        type: 'ajax',
        api: {
            read: 'bcgogoSmsAccount.do?method=getBcgogoSmsTotalAccount'
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