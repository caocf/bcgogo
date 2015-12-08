/**
 * @class Ext.view.sys.department.Tree
 * @extends Ext.tree.Panel
 * @author ZhangJuntao
 * The department list view.  A tree that displays all of the department lists.
 */
Ext.define('Ext.view.sys.department.Tree', {
    extend:'Ext.tree.Panel',
    xtype:'departmentTree',
    requires:[
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.column.Action',
        'Ext.view.sys.department.TreeMenu'
    ],
    autoScroll:true,
    rootVisible:true,
    store:'Ext.store.sys.Departments',
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
                    selectOnFocus:true
                }/*,
             renderer: Ext.bind(me.renderName, me)*/
            }/*,
             {
             xtype: 'actioncolumn',
             width: 24,
             icon: 'resources/images/delete.png',
             iconCls: 'x-hidden',
             tooltip: 'Delete',
             handler: Ext.bind(me.handleDeleteClick, me)
             }*/
        ];
        me.callParent(arguments);
    }
});