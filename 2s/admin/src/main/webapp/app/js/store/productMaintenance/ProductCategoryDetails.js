Ext.define('Ext.store.productMaintenance.ProductCategoryDetails', {
    extend:'Ext.data.Store',
    model:"Ext.model.productMaintenance.ProductCategoryDetail",
    pageSize:20,
    remoteSort:false // If false, sorting is done locally on the client.
});