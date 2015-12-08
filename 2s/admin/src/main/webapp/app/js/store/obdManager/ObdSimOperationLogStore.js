Ext.define('Ext.store.obdManager.ObdSimOperationLogStore',{
    extend:'Ext.data.Store',
    model:'Ext.model.obdManager.ObdSimOperationLogModel',
    pageSize:10,
    remoteSort:false, // If false, sorting is done locally on the client.
    proxy:{
        type:'ajax',
        api:{
            read:'obdManage.do?method=getObdSimOperationLog'
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