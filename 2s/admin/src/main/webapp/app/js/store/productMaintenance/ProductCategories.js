Ext.define('Ext.store.productMaintenance.ProductCategories', {
    extend:'Ext.data.TreeStore',
    model:"Ext.model.productMaintenance.ProductCategory",
    folderSort:true,
    sorters: [{
        property: 'sort',
        direction: 'ASC'
    }],
    root:{
        text:'所有产品分类',
        leaf:false,
        iconCls:'icon-user-set',
        id:-1,
        expanded:true
    }
});
