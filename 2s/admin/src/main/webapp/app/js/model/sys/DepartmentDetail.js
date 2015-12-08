Ext.define('Ext.model.sys.DepartmentDetail', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'userNo', type:'string'},
        {name:'userName', type:'string'},
        {name:'name', type:'string'},
        {name:'mobile', type:'string'},
        {name:'email', type:'string'},
        {name:'departmentName', type:'string'},
        {name:'departmentId', type:'string'},
        {name:'occupationName', type:'string'},
        {name:'occupationId', type:'string'},
        {name:'userGroupName', type:'string'},
        {name:'userGroupId', type:'string'},
        {name:'createDateStr', type:'string'},
        {name:'departmentResponsibility', type:'string'},
        {name:'statusEnum', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'user.do?method=getUsersByDepartmentId',
            update:'user.do?method=updateUserDepartmentResponsibility'
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