Ext.define('Ext.store.finance.BcgogoReceivableRecords', {
    extend:'Ext.data.Store',
    model:"Ext.model.finance.BcgogoReceivableRecord",
    pageSize:25,
    proxy:{
        type:'ajax',
        api:{
            read:'bcgogoReceivable.do?method=searchBcgogoReceivableResult'
        },
        reader:{
            type:'json',
            root:"data",
            totalProperty:"total"
        },
        writer:{
            writeAllFields:true,
            type:'json'
        }
    },
    remoteSort:false // If false, sorting is done locally on the client.
});