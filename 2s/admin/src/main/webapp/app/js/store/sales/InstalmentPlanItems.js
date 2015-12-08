Ext.define('Ext.store.sales.InstalmentPlanItems', {
    extend:'Ext.data.Store',
    model:"Ext.model.sales.InstalmentPlanItem",
    pageSize:25,
    proxy:{
        type:'ajax',
        api:{
            read:'bcgogoReceivable.do?method=getInstalmentPlanDetails'
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