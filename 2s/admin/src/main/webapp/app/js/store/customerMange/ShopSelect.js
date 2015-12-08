Ext.define('Ext.store.customerMange.ShopSelect', {
    extend:'Ext.data.Store',
    model:"Ext.model.customerMange.Shop",
    pageSize:15,
    proxy:{
        type:'ajax',
        api:{
            read:'shopManage.do?method=getShopSuggestionByName'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
        }
    },
    remoteSort:false // If false, sorting is done locally on the client.
});