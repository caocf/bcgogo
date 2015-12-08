Ext.define('Ext.store.customerMange.Shops', {
    extend:'Ext.data.Store',
    model:"Ext.model.customerMange.Shop",
    pageSize:25,
    proxy:{
        type:'ajax',
        api:{
            read:'shopManage.do?method=getShopByShopCondition'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totals"
        },
        writer:{
            writeAllFields:true,
            type:'json'
        }
    },
    remoteSort:false // If false, sorting is done locally on the client.
});