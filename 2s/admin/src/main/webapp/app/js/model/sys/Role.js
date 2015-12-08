Ext.define('Ext.model.sys.Role', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'moduleId', type:'string'},
        {name:'name', type:'string'},
        {name:'value', type:'string'},
        {name:'memo', type:'string'},
        {name:'status', type:'string'},
        {name:'hasCheckedByUserGroup', type:'boolean'},
        {name:'userGroupId', type:'string'},
        {name:'type', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'role.do?method=getAllRoles',
            update:'userGroup.do?method=updateUserGroupRoles'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
        },
        writer:{
            writeAllFields:true,
            type:'json',
            root:"results"
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