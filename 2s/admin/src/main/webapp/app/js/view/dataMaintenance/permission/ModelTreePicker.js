/**
 * @class Ext.view.dataMaintenance.permission.ModelTreePicker
 * @extends Ext.ux.TreePicker
 * @author ZhangJuntao
 * 模块与角色的下拉树
 */
Ext.define('Ext.view.dataMaintenance.permission.ModelTreePicker', {
    extend:'Ext.ux.TreePicker',
    xtype:'modelTreePicker',
    requires:[
        'Ext.ux.TreePicker'
    ],
    displayField:'text',
    rootVisible:false,
    initComponent:function () {
        var me = this;
        Ext.apply(me, {
            store:Ext.create('Ext.store.dataMaintenance.Modules')
        });
        this.callParent();
    }
});