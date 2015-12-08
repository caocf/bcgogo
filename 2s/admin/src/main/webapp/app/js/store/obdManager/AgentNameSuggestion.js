Ext.define('Ext.store.obdManager.AgentNameSuggestion',{
    extend:'Ext.data.Store',
    fields:["name","idStr"],
    pageSize:10,
    remoteSort:false,
    proxy:{
        type:'ajax',
        api:{
            read:'obdManage.do?method=getAgentNameSelection'
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