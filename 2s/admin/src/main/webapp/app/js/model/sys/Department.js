Ext.define('Ext.model.sys.Department', {
    extend:'Ext.data.Model',
    fields:[
        { name:'id'},
        { name:'text' },
        { name:'parentId' },
        { name:'leaf', type:'boolean' },
        { name:'sort', type:'int' },
        {name:"iconCls", type:"string"},
        { name:'type' }
    ],
    proxy:{
        type:'ajax',
        api:{
            create:'user.do?method=updateDepartment',
            read:'user.do?method=getDepartmentsAndOccupations',
            update:'user.do?method=updateDepartment',
            destroy:''
        },
        reader:{
//            root:'results',
            type:'json'
//            totalProperty:"totalRows"
            //  messageProperty:'message'
        }
    }

});