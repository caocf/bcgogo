/**
 * @class Ext.view.dataMaintenance.permission.ModuleTree
 * @extends Ext.tree.Panel
 * @author ZhangJuntao
 * The module list view.  A tree that displays all of the Module lists.
 */
Ext.define('Ext.view.dataMaintenance.permission.ModuleTree', {
    extend:'Ext.tree.Panel',
    xtype:'moduleTree',
    requires:[
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.column.Action',
        'Ext.view.dataMaintenance.permission.ModuleTreeMenu'
    ],
    autoScroll:true,
    rootVisible:true,
    store:'Ext.store.dataMaintenance.Modules',
    initComponent:function () {
        var me = this;
        this.commonUtils = Ext.create("Ext.utils.Common");
        me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing')];
        me.viewConfig = {
            plugins:{
                ptype:'treeviewdragdrop',
                appendOnly:true,
                dragText:"only drag module or roles in bcgogo!"
            },
            listeners:{
                beforedrop:function (node, data, overModel, dropPosition, dropHandler, eOpts) {
                    if (overModel.get("id") == 0) {
                        Ext.Msg.alert('警告', "不能添加到<" + overModel.get("value") + ">根节点！",function () {
                            dropHandler.cancelDrop();
                        });
                    } else {
                        me.commonUtils.ajax({
                            url:'module.do?method=dropModuleTree',
                            params:{
                                id:data.records[0].get("id"),
                                parentId:overModel.get("id"),
                                type:data.records[0].get("type"),
                                systemType:overModel.get("systemType")
                            },
                            success:function (result) {
                                dropHandler.processDrop();
                                data.records[0].commit();
                            }
                        });
                    }
                },
                drop:function (node, data, overModel, dropPosition, eOpts) {
                }
            }
        };
        me.columns = [
            {
                header:'value',
                xtype:'treecolumn',
                dataIndex:'value',
                text:'value',
                width:245,
                flex:1,
                editor:{
                    xtype:'textfield',
                    selectOnFocus:true
                }
            },
            {
                text:'sort',
                editor:'textfield',
                flex:1,
                width:50,
                dataIndex:'sort'
            },
            {
                text:'name',
                editor:'textfield',
                flex:1,
                width:50,
                dataIndex:'name'
            }/*,
             {
             text: 'memo',
             editor: 'textfield',
             flex: 1,
             width:50,
             dataIndex: 'memo'
             }*/
        ];
        me.callParent(arguments);
    }
});