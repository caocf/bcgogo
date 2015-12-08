/**
 * author:zhangjuntao
 * description:role下的resources
 * role->resource
 */
Ext.define('Ext.view.dataMaintenance.permission.RoleResources', {
    extend:'Ext.grid.Panel',
    alias:'widget.permissionRoleResources',
    autoScroll:true,
    columnLines:true,
    stripeRows:true, //每列是否是斑马线分开
    forceFit:true, //自动填充，即让所有列填充满gird宽度
    autoHeight:true,
    title:'权限',
    emptyText:"该角色无权限！",
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            store:Ext.create('Ext.store.dataMaintenance.RoleResources'),
            columns:[
                {
                    header:'resourceId',
                    dataIndex:'resourceId'
                },
                {
                    header:'name',
                    dataIndex:'name'
                },
                {
                    header:'value',
                    dataIndex:'value'
                },
                {
                    header:'memo',
                    dataIndex:'memo'
                },
                {
                    header:'status',
                    dataIndex:'status'
                },
                {
                    header:'type',
                    dataIndex:'type'
                },
                {
                    xtype:'actioncolumn',
                    header:'操作',
                    width:60,
                    id:"deleteRoleResource",
                    items:[
                        {
                            tooltip:'从角色中删除此资源',
                            scope:me,
                            icon:'app/images/icons/delete.png'
                        }
                    ]
                }
            ]
        });
        this.callParent(arguments);
    }
});
