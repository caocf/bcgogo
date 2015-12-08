Ext.define('Ext.store.sys.Departments', {
    extend:'Ext.data.TreeStore',
    model:"Ext.model.sys.Department",
//    defaultRootId:'1',
    folderSort :true,
    sorters: [{
        property: 'sort',
        direction: 'ASC'
    }],
    root:{
        text:'统购平台',
        leaf:false,
        iconCls:'icon-user-set',
        id:-1,
        expanded:true
    }
});