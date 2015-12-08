Ext.define('Ext.model.sys.UserGroup', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        'name',
        'value',
        'memo',
        'statusValue',
        'status'
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'userGroup.do?method=getUserGroupByCondition',
            create:'userGroup.do?method=updateUserGroup',
            update:'userGroup.do?method=updateUserGroup'
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