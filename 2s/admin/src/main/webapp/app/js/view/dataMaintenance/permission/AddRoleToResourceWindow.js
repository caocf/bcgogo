/**
 * 权限维护 - 资源模块 - 详细（角色列表）-新增角色
 * 向资源中增加角色 窗口
 * @author:zhangjuntao
 */
Ext.define('Ext.view.dataMaintenance.permission.AddRoleToResourceWindow', {
    alias:'widget.addRoleToResourceWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.dataMaintenance.permission.ModelTreePicker"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:{
                xtype:'panel',
                buttons:[
                    {
                        text:'保存',
                        action:'save'
                    }
                ],
                items:[
                    {
                        xtype:'modelTreePicker',
                        name:'roleId',
                        fieldLabel:'角色',
                        allowBlank:false,
                        displayField:'text'
                    }
                ]
            }
        });
        me.callParent();
    },
    title:'增加角色到该资源',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

