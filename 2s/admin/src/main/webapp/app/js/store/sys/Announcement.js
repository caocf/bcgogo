Ext.define('Ext.store.sys.Announcement', {
    extend:'Ext.data.Store',
    model:"Ext.model.sys.Announcement",
    pageSize:25,
    remoteSort:false // If false, sorting is done locally on the client.
});