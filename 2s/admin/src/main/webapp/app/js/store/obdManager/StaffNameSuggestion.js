Ext.define('Ext.store.obdManager.StaffNameSuggestion',{
    extend:'Ext.data.Store',
    fields:["name","idStr"],
    pageSize:10,
    remoteSort:false,
    proxy:{
        type:'ajax',
        api:{
            read:'obdManage.do?method=getStaffNameSelection'
        },
        reader:{
            root:'results',
            type:'json',
            totalProperty:"totals",
            successProperty : 'success'

        },
        successProperty : 'success'
    }
});