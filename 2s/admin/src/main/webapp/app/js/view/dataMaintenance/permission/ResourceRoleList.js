Ext.define('Ext.view.dataMaintenance.permission.ResourceRoleList', {
    extend:'Ext.grid.Panel',
    alias:'widget.resourceRoleList',
    autoScroll:true,
    columnLines:true,
    stripeRows:true,   //每列是否是斑马线分开
    forceFit:true,     //自动填充，即让所有列填充满gird宽度
    autoHeight:true,
    width:800,
    minHeight:100,
    maxHeight:400,
    emptyText:"该资源尚无角色使用！",
    dockedItems:[
        {
            xtype:'toolbar',
            dock:'top',
            items:[
                {
                    text:'新增角色',
                    xtype:'button',
                    action:'add',
                    scope:this,
                    iconCls:'icon-add'
                },
                '-',
                {
                    text:'删除角色',
                    xtype:'button',
                    action:'delRole',
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
            store:Ext.create('Ext.store.dataMaintenance.ResourceRoles'),
            columns:[
                {
                    header:'id',
                    dataIndex:'id',
                    width:170
                },
                {
                    header:'value',
                    dataIndex:'value',
                    width:100
                },
                {
                    header:'memo',
                    dataIndex:'memo',
                    width:330
                }, {
                    header:'name',
                    dataIndex:'name',
                    width:200
                }/*,
                {
                    header:'type',
                    dataIndex:'type'
                }*/
            ]
        });
        this.callParent(arguments);
    }
});
