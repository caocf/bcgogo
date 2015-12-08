Ext.define('Ext.model.sys.Module', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'name', type:'string'},
        {name:'value', type:'string'},
        {name:'memo', type:'string'},
        {name:'status', type:'string'},
        {name:'type', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'module.do?method=getModulesBySystemType'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
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