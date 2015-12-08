Ext.define('Ext.view.productMaintenance.updateProductCategory', {
    alias:'widget.updateProductCategoryWin',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.productMaintenance.ProductCategoryForm"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('productCategoryForm')
        });
        me.callParent();
    },
    title:'修改产品分类',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

