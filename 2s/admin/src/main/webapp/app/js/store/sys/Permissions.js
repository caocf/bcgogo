Ext.define('Ext.store.sys.Permissions', {
    extend:'Ext.data.Store',
    fields:[
        {name:'name', type:'string'}
    ],
    autoLoad:true,
    proxy:{
        type:'ajax',
        api:{
            read:'permission.do?method=getResources'
        },
        reader:{
            root:'results',
            type:'json'
        }
    }
});