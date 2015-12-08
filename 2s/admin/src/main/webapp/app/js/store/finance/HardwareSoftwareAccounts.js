Ext.define('Ext.store.finance.HardwareSoftwareAccounts', {
    extend:'Ext.data.Store',
    model:"Ext.model.finance.HardwareSoftwareAccount",
    pageSize:25,
    proxy:{
        type:'ajax',
        api:{
            read:'bcgogoAccount.do?method=searchHardwareSoftwareAccountResult'
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