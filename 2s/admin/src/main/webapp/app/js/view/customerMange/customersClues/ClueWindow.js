/**
 * 新增客户线索 窗口
 * @author :zhangjuntao
 */
Ext.define('Ext.view.customerMange.customersClues.ClueWindow', {
    alias:'widget.clueWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.customerMange.customersClues.ClueForm"],
    shopbusinessscopewindow: Ext.create("Ext.view.customerMange.ShopBusinessScopeWindow"),
    initComponent:function () {
        var me = this;
        me.shopbusinessscopewindow.setBusinessScopeTarget(me);
        Ext.apply(me, {
            items:Ext.widget('clueForm')
        });
        me.callParent();
    },
    title:'新增客户线索',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    } ,
    setProductCategoryIds: function (productCategoryIds) {
        this.productCategoryIds = productCategoryIds;
    },

    getProductCategoryIds: function () {
        return this.productCategoryIds;
    }
});

