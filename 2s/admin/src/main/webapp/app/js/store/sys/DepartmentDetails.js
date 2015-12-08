Ext.define('Ext.store.sys.DepartmentDetails', {
    extend:'Ext.data.Store',
    model:"Ext.model.sys.DepartmentDetail",
    remoteSort:false // If false, sorting is done locally on the client.
});