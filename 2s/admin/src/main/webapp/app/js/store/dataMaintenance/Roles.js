Ext.define('Ext.store.dataMaintenance.Roles', {
    extend:'Ext.data.TreeStore',
    model:"Ext.model.dataMaintenance.Module",
    folderSort:true,
    sorters: [{
        property: 'sort',
        direction: 'ASC'
    }],
    root:{
        value:'统购平台',
        name:'BCGOGO',
        leaf:false,
        iconCls:'icon-user-set',
        id:-1,
        expanded:true
    },
    proxy:{
        type:'ajax',
        api:{
            read:'shopVersion.do?method=getTreeModuleRolesForShopVersion'
        },
        reader:{
            type:'json'
        }
    }
});