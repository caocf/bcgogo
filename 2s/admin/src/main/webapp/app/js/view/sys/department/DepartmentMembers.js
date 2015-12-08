Ext.define('Ext.view.sys.department.DepartmentMembers', {
    extend:'Ext.grid.Panel',
    alias:'widget.departmentMembers',
    frame:true, //窗口化，即让界面变的饱满
    autoScroll:true,
    columnLines:true,
    stripeRows:true, //每列是否是斑马线分开
    forceFit:true, //自动填充，即让所有列填充满gird宽度
    autoHeight:true,
    title:'成员列表',
    emptyText:"该部门无成员！",
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
                    header:'操作',
                    dataIndex:'departmentResponsibility',
                    width:60,
                    id:"addDepartmentResponsibility",
                    items:[
                        {
                            text:'设置负责人',
                            tooltip:'设置负责人',
                            scope:me,
                            icon:'app/images/icons/add.png'/*,
                             iconCls:'icon-user',
                            getClass:function (val,meta,rec) {
                                if ("LEADER"==rec.get("departmentResponsibility")) {
                                    return "icon-save";
                                } else{
                                   return "icon-user";
                                }
                            }*/
                        }
                    ]
                }
            ]
        });
        this.callParent(arguments);
    }
});
