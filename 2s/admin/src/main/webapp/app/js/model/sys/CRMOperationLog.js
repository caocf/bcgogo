Ext.define('Ext.model.sys.CRMOperationLog', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'shopId', type:'string'},
        {name:'module', type:'string'},
        {name:'type', type:'string'},
        {name:'content', type:'string'},
        {name:'operateTime', type:'string'},
        {name:'ipAddress', type:'string'},
        {name:'userNo', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'CRMOperationLog.do?method=getCRMOperationLogsByCondition'
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
    listeners:{
        exception:function (proxy, response, operation) {
            Ext.MessageBox.show({
                title:'错误异常',
                msg:operation.getError(),
                icon:Ext.MessageBox.ERROR,
                buttons:Ext.Msg.OK
            });
        }
    }

})