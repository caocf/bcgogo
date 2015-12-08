Ext.define('Ext.store.obdManager.ObdSimBindStore',{
    extend:'Ext.data.Store',
    model:'Ext.model.obdManager.ObdSimBindModel',
    pageSize:20,
    remoteSort:false, // If false, sorting is done locally on the client.
    proxy:{
        type:'ajax',
        api:{
            read:'obdManage.do?method=getObdSimBySearchCondition'
        },
        reader:{
            root:'results',
            type:'json',
            totalProperty:"totals",
            successProperty : 'success'

        },
        successProperty : 'success',
        listeners:{
            exception:function( reader,  response, error,  eOpts){
                Ext.Msg.alert("网络异常","数据加载失败！");
            }
        }
    }
});