Ext.define('Ext.store.dataMaintenance.UserGroups', {
    extend:'Ext.data.Store',
    fields:[
        {name:'id', type:'string'},
        {name:'name', type:'string'},
        {name:'type', type:'string'},
        {name:'status', type:'string'},
        {name:'userGroupNo', type:'string'},
        {name:'memo', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'userGroup.do?method=getUserGroupsByShopVersionId',
            update:'shopVersion.do?method=saveOrUpdateUserGroup'
        },
        reader:{
            type:'json',
            root:"results"
        }
    }
});