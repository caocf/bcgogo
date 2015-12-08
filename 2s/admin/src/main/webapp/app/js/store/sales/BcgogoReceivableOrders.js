Ext.define('Ext.store.sales.BcgogoReceivableOrders', {
    extend:'Ext.data.Store',
    model:"Ext.model.sales.BcgogoReceivableOrder",
    pageSize:25,
    proxy:{
        type:'ajax',
        api:{
            read:'bcgogoReceivable.do?method=searchBcgogoReceivableOrderResult'
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