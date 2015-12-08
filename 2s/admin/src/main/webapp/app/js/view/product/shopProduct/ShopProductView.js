Ext.define('Ext.view.product.shopProduct.ShopProductView', {
    extend:'Ext.panel.Panel',
    alias:'widget.shopProductView',
    forceFit:true,
    frame:true,
    autoHeight:true,
    autoScroll:true,
    title: 'Results',
    layout:"border",
    requires:[
        "Ext.view.product.shopProduct.ShopProductList",
        "Ext.view.product.shopProduct.RelevanceView"
    ],
    items:[
        {
            collapsible:true,
            title:'店铺商品信息',
            region:'center',
            autoHeight:true,
            xtype:'productShopProList'
        } ,
        {
//            animCollapse:true,
            hideCollapseTool:false,
            collapseMode:"mini ",
            title:'标准商品信息',
            header:true,
            collapsed:true,
            collapsible:true,
            height:250,
            minHeight: 100,
            region:'south',
            xtype:"relevanceView"
        }
    ],
    initComponent:function () {
        var me = this;

        me.callParent();
    }

});