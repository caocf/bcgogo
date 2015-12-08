Ext.define('Ext.view.product.normalProduct.ShowProductWin', {
    alias:'widget.showProductWin',
    extend:'Ext.panel.Panel',
    forceFit:true,
    frame:true,
    autoHeight:true,
    autoScroll:true,
    split:true,
    requires:[
        "Ext.view.product.normalProduct.SearchRelevanceShopProduct"
    ],
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            items:[
                {
                    region:'center',
                    title:'关联店铺列表',
                    items: Ext.widget('searchRelevanceShopProduct')
                }
            ]
        });
        me.callParent();
    },
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});