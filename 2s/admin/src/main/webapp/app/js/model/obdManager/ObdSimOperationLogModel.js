Ext.define('Ext.model.obdManager.ObdSimOperationLogModel',{
    extend:'Ext.data.Model',
    fields:[
        {name:'userName',type:'string'},
        {name:'operationDateStr',type:'date',dateFormat:'Y-m-d H:i'},
        {name:'content',type:'string'},
        {name:'operationTypeStr',type:'string'}
    ]
});


