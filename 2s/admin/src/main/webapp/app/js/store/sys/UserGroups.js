Ext.define('Ext.store.sys.UserGroups', {
    extend:'Ext.data.Store',
    model:"Ext.model.sys.UserGroup",
//    autoLoad:true,
//    autoSync:true,  //自动提交改变的数据
    pageSize:25,
    remoteSort:false // If false, sorting is done locally on the client.
});