/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-3
 * Time: 上午10:36
 * To change this template use File | Settings | File Templates.
 */


Ext.define('Ext.view.normalProductStat.NormalProductResultView', {
    extend:'Ext.tab.Panel',
    alias:'widget.normalProductResultView',
    forceFit:true,
    frame:true,
    autoHeight:true,
    autoScroll:true,
    requires:[
        'Ext.view.normalProductStat.NormalProductStatList',
        'Ext.view.normalProductStat.NormalProductStatDetail'
    ],
    items:[
        {
            title:'店铺采购明细',
            layout:'border',
            bodyBorder:true,
            hideBorders:true,
            id:'normalProductStatDetailView',
            defaults:{
                collapsible:true,
                split:true,
                animFloat:false,
                autoHide:false,
                useSplitTips:true
            },
            items:[
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
                            xtype:"normalProductStatList",
                            region:'north',
                            height:280
                        },
                        {
                            title:'产品编码为 各个店铺的采购明细',
                            xtype:"normalProductStatDetail",
                            region:'north',
                            height:260
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


