Ext.define('Ext.store.product.ShopProducts', {
    extend:'Ext.data.Store',
    model:"Ext.model.product.ShopProduct",
    pageSize:20,
    remoteSort:false // If false, sorting is done locally on the client.
});