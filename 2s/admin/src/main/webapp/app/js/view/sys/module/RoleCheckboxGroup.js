/**
 * 尝试中 by zhangjuntao
 */
Ext.define("Ext.view.sys.module.RoleCheckboxGroup", {
    extend:'Ext.form.CheckboxGroup',
    alias:'widget.roleCheckboxGroup',
//    store:'Ext.store.sys.Roles',
    initComponent:function () {
        var me = this;
        this.addEvents('storeloaded', me);
        Ext.apply(me, {
            store: Ext.create('Ext.store.sys.Roles')
        });
        me.callParent();
    }
});