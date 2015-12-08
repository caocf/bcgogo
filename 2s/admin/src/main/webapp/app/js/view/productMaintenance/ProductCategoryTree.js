
Ext.define('Ext.view.productMaintenance.ProductCategoryTree', {
    extend:'Ext.tree.Panel',
    xtype:'productCategoryTree',
    requires:[
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.column.Action',
        'Ext.view.productMaintenance.ProductCategoryTreeMenu'
    ],
    autoScroll:true,
    rootVisible:true,
    store:'Ext.store.productMaintenance.ProductCategories',
    hideHeaders:true,
    preventHeader:true,
    initComponent:function () {
        var me = this;
        me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing')];
        me.columns = [
            {
                xtype:'treecolumn',
                dataIndex:'text',
                flex:1,
                editor:{
                    xtype:'textfield',
                    maxLength:15,
                    enforceMaxLength:true,
                    selectOnFocus:true
                }
            }
        ];
        me.callParent(arguments);
    }
});