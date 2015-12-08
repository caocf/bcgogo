Ext.define('Ext.store.productMaintenance.SecondProductCategories', {
    extend:'Ext.data.Store',
    model:"Ext.model.productMaintenance.SecondProductCategory",
    autoLoad:false,
    autoSync:true,  //自动提交改变的数据
    pageSize:1000,
    remoteSort:false // If false, sorting is done locally on the client.
});
