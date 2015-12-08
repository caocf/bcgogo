Ext.define('Ext.view.sys.module.RoleList', {
    extend:'Ext.grid.Panel',
    alias:'widget.sysRoleList',
    requires:[
        'Ext.ux.CheckColumn'
    ],
    store:Ext.create('Ext.store.sys.Roles'),
    hideHeaders:true,
    preventHeader:true,
    border:false,
    forceFit:true,
    autoHeight:true,
    autoScroll:true,
    columns:[
        {
            dataIndex:'value',
            width:100
        },
        {
            xtype:'checkcolumn',
            dataIndex:'hasCheckedByUserGroup',
            width:55
        }
    ],
    initComponent:function () {
        var me = this;
        this.addEvents('storeloaded', me);
        me.callParent();
    }
});