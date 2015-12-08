Ext.define('Ext.view.product.normalProduct.Add', {
    alias:'widget.windowAddProduct',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.product.normalProduct.AddProductForm"],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:Ext.widget('formProduct')
        });
        me.callParent();
    },
    title:'新增标准产品',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.closeChildWin();
        this.doClose();
    },
    setChildWin: function (normalProductVehicleBrandmodelWindow) {
        this.normalProductVehicleBrandmodelWindow = normalProductVehicleBrandmodelWindow;
    },
    closeChildWin: function () {
        this.normalProductVehicleBrandmodelWindow.close();
    }
});