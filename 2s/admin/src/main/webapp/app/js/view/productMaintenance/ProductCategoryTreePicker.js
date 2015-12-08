/**
 * @class Ext.view.dataMaintenance.permission.ModelTreePicker
 * @extends Ext.ux.TreePicker
 * @author ZhangJuntao
 * 模块与角色的下拉树
 */
Ext.define('Ext.view.productMaintenance.ProductCategoryTreePicker', {
    extend:'Ext.ux.TreePicker',
    xtype:'productCategoryTreePicker',
    requires:[
        'Ext.ux.TreePicker'
    ],
    displayField:'text',
    rootVisible:false,
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            store:Ext.create('Ext.store.productMaintenance.ProductCategories')
        });
        this.callParent();
    }
});