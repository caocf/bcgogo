Ext.define('Ext.view.sys.module.View', {
    extend:'Ext.window.Window',
    alias:'widget.sysrmoduleview',
    title:'角色配置',
    closable:true,
    closeAction:'hide',
    iconCls:'icon-user',
    collapsible:true,
    width:630,
    minWidth:350,
    height:400,
    layout:'border',
    bodyStyle:'padding: 5px;',
    requires:["Ext.view.sys.module.ModuleList","Ext.view.sys.module.RoleList"],  /*"Ext.view.sys.module.RoleCheckboxGroup",*/
    buttons:[
        {
            text:'保存配置',
            xtype:'button',
            tooltip:'保存角色配置',
            action:'updateResourceConfig',
            scope:this,
            iconCls:'icon-save'
        }
    ],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:[
                {
                    region:'west',
                    title:'角色模块',
                    width:200,
                    split:true,
                    items:[
                        {
                            xtype:'modulelist'
                        }
                    ]
                },
                {
                    region:'center',
                    xtype:'panel',
                    autoScroll:true,
                    title:'资源',
                    items:[
                        {
//                            xtype:'roleCheckboxGroup',
//                            columns:[133, 133, 133]
                            xtype:'sysRoleList'
                        }
                    ]
                }
            ]
        });
        me.callParent();
    },
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});