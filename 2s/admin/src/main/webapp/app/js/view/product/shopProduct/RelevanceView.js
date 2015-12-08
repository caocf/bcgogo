Ext.define('Ext.view.product.shopProduct.RelevanceView', {
    extend:'Ext.tab.Panel',
    alias:'widget.relevanceView',
    forceFit:true,
    frame:true,
    autoHeight:true,
//    autoScroll:true,

    split:true,
    requires:[
        "Ext.view.product.shopProduct.SelectNormalProductList",
        'Ext.view.product.normalProduct.AddProductForm'
    ],
    items:[
        {

            title:'查询标准商品',
            layout:'border',
            items:[
                {
                    region:'center',
                    xtype:"selectNormalProductList"
                },
                {
                    region:'south',
//                    text:"关联",
                    buttons:[{
                        text: "绑定标准产品",
                        buttonAlign:"left",
                        style : 'margin-right:500px',
                        action:'relevance',
                        id: "bt1",
                        iconCls:'icon-product-relevance'
                    }]

                }
            ]

        },
        {
            title:'新增标准商品关联',
            xtype:"formProduct"
        }
    ],
    initComponent:function () {
        var me = this;

        me.callParent();
    }

});