//shop
Ext.define('Ext.view.dataMaintenance.permission.UserGroupList', {
    extend:'Ext.grid.Panel',
    alias:'widget.permissionUserGroupList',
    store:'Ext.store.dataMaintenance.UserGroups',
    autoScroll:true,
    columnLines:true,
    stripeRows:true, //每列是否是斑马线分开
    forceFit:true, //自动填充，即让所有列填充满gird宽度
//    multiSelect:true, //可以多选
    autoHeight:true,
    requires:[
        'Ext.view.dataMaintenance.permission.AddShopVersionWindow',
        "Ext.view.dataMaintenance.permission.ShopVersionForm"
    ],
    plugins: [
        Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToEdit: 2
        })
    ],
    dockedItems:[
        {
            xtype:'toolbar',
            dock:'top',
            items:[
                {
                    text:'新增',
                    xtype:'button',
                    action:'add',
                    scope:this,
                    iconCls:'icon-add',
                    disabled:true
                },
                '-',
                {
                    text:'删除',
                    xtype:'button',
                    action:'delete',
                    scope:this,
                    iconCls:'icon-del',
                    disabled:true
                }
            ]
        }
    ],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            columns:[
                {
                    header:'id',
                    dataIndex:'id'
                },
                {
                    header:'name',
                    dataIndex:'name',
                    editor: {
                        xtype :'textfield',
                        allowBlank: false
                    }
                },{
                    header:'userGroupNo',
                    dataIndex:'userGroupNo',
                    editor: {
                        xtype :'textfield',
                        allowBlank: false
                    }
                },
                {
                    header:'memo',
                    dataIndex:'memo',
                    editor: {
                        xtype :'textfield',
                        allowBlank: false
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});
