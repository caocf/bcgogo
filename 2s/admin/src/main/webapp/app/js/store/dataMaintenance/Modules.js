Ext.define('Ext.store.dataMaintenance.Modules', {
    extend:'Ext.data.TreeStore',
    model:"Ext.model.dataMaintenance.Module",
    folderSort:true,
    sorters: [{
        property: 'sort',
        direction: 'ASC'
    }],
    root:{
        value:'统购平台',
        leaf:false,
        useArrows:true,
        iconCls:'icon-user-set',
        id:null,
        allowDrop:false,
        allowDrag:false,
        expanded:true
    },
    proxy:{
        type:'ajax',
        api:{
            read:'module.do?method=getTreeModules'
        },
        reader:{
            type:'json'
        }
    }
});