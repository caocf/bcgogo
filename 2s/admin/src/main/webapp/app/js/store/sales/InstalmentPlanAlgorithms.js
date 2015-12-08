Ext.define('Ext.store.sales.InstalmentPlanAlgorithms', {
    extend:'Ext.data.Store',
    model:"Ext.model.sales.InstalmentPlanAlgorithm",
    pageSize:25,
    proxy:{
        type:'ajax',
        api:{
            read:'bcgogoReceivable.do?method=getInstalmentPlanAlgorithms'
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