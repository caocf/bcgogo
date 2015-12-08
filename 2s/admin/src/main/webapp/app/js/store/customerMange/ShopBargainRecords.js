Ext.define('Ext.store.customerMange.ShopBargainRecords', {
    extend: 'Ext.data.Store',
    model: "Ext.model.customerMange.ShopBargainRecord",
    pageSize: 25,
    proxy: {
        type: 'ajax',
        api: {
            read: 'shopBargain.do?method=getShopBargainRecords'
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