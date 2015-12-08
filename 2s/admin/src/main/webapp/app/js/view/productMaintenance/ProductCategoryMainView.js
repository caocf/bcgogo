/**
 * @author zhangjuntao
 * 权限维护模块 view 入口
 */
Ext.define('Ext.view.productMaintenance.ProductCategoryMainView', {
    extend:'Ext.tab.Panel',
    alias:'widget.productCategoryMainView',
    forceFit:true,
    frame:true,
    autoHeight:true,
    autoScroll:true,
    requires:[
        'Ext.view.productMaintenance.ProductCategoryResultView',
        'Ext.view.productMaintenance.ProductCategoryTree'
    ],
    items:[
        {
            title:'产品分类管理',
            layout:'border',
            bodyBorder:true,
            hideBorders:true,
            id:'productCategoryTreeView',
            defaults:{
                collapsible:true,
                split:true,
                animFloat:false,
                autoHide:false,
                useSplitTips:true
            },
            items:[
                {
                    region:'west',
                    xtype:'productCategoryTree',
                    width:400,
                    collapsible:true,
                    split:true
                },
                {
                    hideHeaders:true,
                    preventHeader:true,
                    region:'center',
                    layout:'border',
                    defaults:{
                        collapsible:false,
                        split:true,
                        animFloat:false,
                        autoHide:false,
                        useSplitTips:true
                    },
                    items:[
                        {
                            xtype:"productCategoryResultView",
                            region:'north',
                            height:420
                        }
                    ]
                }
            ]
        }/*,
        {
            title:'资源维护',
            hideBorders:true,
            xtype:'permissionResourceList'
        }*/
    ],
    initComponent:function () {
        var me = this;

        me.callParent();
    }

});
