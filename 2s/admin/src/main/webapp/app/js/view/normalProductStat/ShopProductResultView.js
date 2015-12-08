/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 14-2-27
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.normalProductStat.ShopProductResultView', {
    extend:'Ext.tab.Panel',
    alias:'widget.shopProductResultView',
    forceFit:true,
    frame:true,
    autoHeight:true,
    autoScroll:true,
    requires:[
        'Ext.view.normalProductStat.ShopProductStatList'
    ],
    items:[
        {
            title: '当前店铺采购明细',
            xtype: "shopProductStatList"
        }

    ],
    initComponent:function () {
        var me = this;
        me.callParent();
    }

});
