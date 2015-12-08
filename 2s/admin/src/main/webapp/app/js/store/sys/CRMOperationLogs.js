Ext.define('Ext.store.sys.CRMOperationLogs', {
    extend:'Ext.data.Store',
    model:"Ext.model.sys.CRMOperationLog",
    pageSize:25,
    remoteSort:false // If false, sorting is done locally on the client.
});