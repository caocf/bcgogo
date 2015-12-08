/**
 * 区域
 */
Ext.define('Ext.store.customerMange.Area', {
    extend:'Ext.data.Store',
    model:"Ext.model.customerMange.Area",
    pageSize:25,
    autoSync:true,
    remoteSort:false // If false, sorting is done locally on the client.
});