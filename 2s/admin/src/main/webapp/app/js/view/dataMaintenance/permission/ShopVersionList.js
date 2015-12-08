Ext.define('Ext.view.dataMaintenance.permission.ShopVersionList', {
    extend:'Ext.grid.Panel',
    alias:'widget.permissionShopVersionList',
    store:'Ext.store.dataMaintenance.ShopVersions',
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
                    tooltip:'新增角色',
                    action:'add',
                    scope:this,
                    iconCls:'icon-add'
                },
                '-',
                {
                    text:'删除',
                    xtype:'button',
                    tooltip:'删除',
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
                    header:'No.',
                    xtype:'rownumberer',
                    sortable:false,
                    width:25
                },
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
                },
                {
                    header:'value',
                    dataIndex:'value',
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
