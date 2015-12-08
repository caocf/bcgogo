Ext.define('Ext.store.productMaintenance.FirstProductCategories', {
    extend:'Ext.data.Store',
    model:"Ext.model.productMaintenance.ProductCategory",
//    autoLoad:true,
//    autoSync:true,  //自动提交改变的数据
    pageSize:100,
    remoteSort:false // If false, sorting is done locally on the client.
});