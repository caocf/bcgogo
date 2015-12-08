Ext.define('Ext.view.product.normalProduct.NormalProductView', {
    extend:'Ext.panel.Panel',
    alias:'widget.normalProductView',
    forceFit:true,
    frame:true,
    autoHeight:true,
    split:true,
    autoScroll:true,
    title: 'Results',
    layout:"border",
    requires:[
        "Ext.view.product.normalProduct.NormalProductList",
        "Ext.view.product.normalProduct.ShowProductWin"
    ],
    items:[
        {
            title:'标准商品信息',
            region:'center',
            autoHeight:true,
            xtype:'productNormalProList'
        } ,
        {
//            animCollapse:false,
            hideCollapseTool:true,
            collapseMode:"mini",
            header:false,
            collapsed:false,
            collapsible:true,
            region:'south',
            xtype:"showProductWin"
        }
    ],
    initComponent:function () {
        var me = this;

        me.callParent();
    }

});