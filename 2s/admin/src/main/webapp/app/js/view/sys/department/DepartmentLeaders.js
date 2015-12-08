Ext.define('Ext.view.sys.department.DepartmentLeaders', {
    extend:'Ext.grid.Panel',
    alias:'widget.departmentLeaders',
    frame:true, //窗口化，即让界面变的饱满
    autoScroll:true,
    columnLines:true,
    stripeRows:true, //每列是否是斑马线分开
    forceFit:true, //自动填充，即让所有列填充满gird宽度
    autoHeight:true,
    title:'相关负责人',
    emptyText:"该部门无负责人！",
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            store:Ext.create('Ext.store.sys.DepartmentDetails'),
            columns:[
                {
                    header:'账号',
                    dataIndex:'userNo'
                },
                {
                    header:'姓名',
                    dataIndex:'name'
                },
                {
                    header:'部门',
                    dataIndex:'departmentName'
                },
                {
                    header:'职位',
                    dataIndex:'occupationName'
                },
                {
                    header:'角色',
                    dataIndex:'userGroupId',
                    renderer:function (val, style, rec, index) {
                        return  rec.data.userGroupName;
                    },
                    sortable:true
                },
                {
                    header:'状态',
                    dataIndex:'statusEnum',
                    renderer:function (val) {
                        return Ext.widget("sysstatus").getDisplayName(val)
                    },
                    sortable:true
                },
                {
                    xtype:'actioncolumn',
                    id:"removeDepartmentResponsibility",
                    header:'操作',
                    width:60,
                    items:[
                        {
                            text:'移除',
                            tooltip:'移除负责人',
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
