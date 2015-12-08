Ext.define('Ext.store.finance.BcgogoSmsRecords', {
    extend: 'Ext.data.Store',
    model: "Ext.model.finance.BcgogoSmsRecord",
    pageSize: 25,
    proxy: {
        type: 'ajax',
        api: {
            read: 'bcgogoSmsAccount.do?method=searchBcgogoSmsRecordResult'
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