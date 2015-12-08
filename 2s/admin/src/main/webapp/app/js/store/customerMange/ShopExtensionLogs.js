Ext.define('Ext.store.customerMange.ShopExtensionLogs', {
    extend: 'Ext.data.Store',
    model: "Ext.model.customerMange.ShopExtensionLog",
    pageSize: 25,
    proxy: {
        type: 'ajax',
        api: {
            read: 'shopExtension.do?method=getShopExtensionLogs'
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