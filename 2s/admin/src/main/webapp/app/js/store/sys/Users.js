Ext.define('Ext.store.sys.Users', {
    extend:'Ext.data.Store',
    model:"Ext.model.sys.User",
    pageSize:15,
    proxy:{
        type:'ajax',
        api:{
            read:'user.do?method=getUsersByCondition',
            create:'user.do?method=saveOrUpdateUser',
            update:'user.do?method=saveOrUpdateUser'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
        },
        writer:{
            writeAllFields:true,
            type:'json'
        }
    },
    remoteSort:false // If false, sorting is done locally on the client.
});