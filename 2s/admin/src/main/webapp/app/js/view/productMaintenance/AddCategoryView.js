Ext.define('Ext.view.productMaintenance.AddCategoryView', {
    alias:'widget.addCategoryView',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.productMaintenance.AddCategoryForm"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('addCategoryForm')
        });
        me.callParent();
    },
    title:'新增产品分类',
    close:function () {
      Ext.create("Ext.utils.Common").unmask();
      this.doClose();
    }
});

