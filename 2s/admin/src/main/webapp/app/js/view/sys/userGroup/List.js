Ext.define('Ext.view.sys.userGroup.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.sysUserGroupList',
    store: 'Ext.store.sys.UserGroups',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    requires: ["Ext.view.sys.Status"],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            id: 'searchRoleComponent',
            items: [
                /* {
                 text:"清空查询条件",
                 iconCls:"close",
                 action:'clearSearchCondition',
                 tooltip:"清空查询条件",
                 scope:this
                 },
                 "-",*/
                {
                    xtype: "textfield",
                    emptyText: "角色名称",
                    maxLength: 10,
                    enforceMaxLength: true,
                    id: 'roleNameForSearch'
                },
                {
                    xtype: "textfield",
                    emptyText: "角色描述",
                    maxLength: 10,
                    enforceMaxLength: true,
                    id: 'roleDescriptionForSearch'
                },
                {
                    id: 'sysRoleStatusForSearch',
                    width: 80,
                    xtype: "sysstatus"
                },
                "-",
                {
                    text: "查询",
                    xtype: 'button',
                    action: 'search',
                    iconCls: "icon-search",
                    tooltip: "根据条件查询角色信息",
                    scope: this //指向list
                }
            ]
        },
        {
            dock: 'bottom',
            xtype: 'pagingtoolbar',
            store: 'Ext.store.sys.UserGroups',
            displayInfo: true
        },
        {
            xtype: 'toolbar',
            items: [
                {
                    text: '新增',
                    xtype: 'button',
                    tooltip: '新增角色',
                    action: 'addNewRole',
                    scope: this,
                    iconCls: 'icon-add'
                },
                '-',
                {
                    text: '启用',
                    xtype: 'button',
                    itemId: 'enableRoleButton',
                    tooltip: '开启角色',
                    iconCls: 'icon-edit',
                    action: 'enableRoles',
                    scope: this,
                    disabled: true
                },
                '-',
                {
                    itemId: 'forbiddenRoleButton',
                    text: '禁用',
                    xtype: 'button',
                    tooltip: '禁用角色',
                    iconCls: 'icon-del',
                    action: 'forbiddenRoles',
                    disabled: true,
                    scope: this
                }
            ]
        }
    ],
    common: Ext.create("Ext.utils.Common"),
    initComponent: function () {
        var me = this;
        var permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        Ext.apply(me, {
            selModel: Ext.create('Ext.selection.CheckboxModel', {}),
            columns: [
                {
                    header: 'No.',
                    xtype: 'rownumberer',
                    sortable: false,
                    width: 25
                },
                {
                    header: '角色名称',
                    dataIndex: 'name'
                },
                {
                    header: '角色描述',
                    width: 500,
                    title: 'memo',
                    qtip: 'memo',
                    dataIndex: 'memo',
                    renderer: function (value, metadata) {
                        metadata.tdAttr = 'data-qtip="' + value + '"';
                        return value;
                    }
                },
                {
                    header: '状态',
                    dataIndex: 'status',
                    renderer: function (val) {
                        return Ext.widget("sysstatus").getDisplayName(val)
                    },
                    sortable: true
                },
                {
                    xtype: 'actioncolumn',
                    header: '操作',
                    width: 60,
                    items: [
                        {
                            text: '编辑角色',
                            tooltip: '编辑角色',
                            scope: me,
                            icon: 'app/images/icons/edit.png',
                            renderer: function (view, eOpts) {
                                if (!permissionUtils.hasPermission("CRM_sys_role_update")) {
                                    return "";
                                }
                            }
                        },
                        {
                            text: '配置角色权限',
                            tooltip: '配置角色权限',
                            scope: me,
                            icon: 'app/images/icons/cog.png',
                            renderer: function (view, eOpts) {
                                if (!permissionUtils.hasPermission("CRM_sys_role_update")) {
                                    return "";
                                }
                            }
                        }
                    ]
                }
            ]
        });
//        me.addEvents('editRole');
//        me.addEvents('configurationRole');
        this.callParent(arguments);
    }
});
