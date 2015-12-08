Ext.define('Ext.store.product.NormalProducts', {
    extend:'Ext.data.Store',
    model:"Ext.model.product.NormalProduct",
    pageSize:20,
    remoteSort:false // If false, sorting is done locally on the client.
});