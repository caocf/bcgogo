Ext.define('Ext.store.sys.Festival', {
    extend:'Ext.data.Store',
    model:"Ext.model.sys.Festival",
    pageSize:25,
    remoteSort:false // If false, sorting is done locally on the client.
});