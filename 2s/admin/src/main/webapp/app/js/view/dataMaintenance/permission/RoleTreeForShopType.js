/**
 * @class Ext.view.dataMaintenance.permission.RoleTreeForShopType
 * @extends Ext.tree.Panel
 * @author ZhangJuntao
 * The role tree for shop view.  A tree that displays all of the role the shop has.
 */
Ext.define('Ext.view.dataMaintenance.permission.RoleTreeForShopType', {
    extend:'Ext.tree.Panel',
    xtype:'roleTreeForShopType',
    requires:[
        'Ext.ux.CheckColumn'
    ],
    autoScroll:true,
    rootVisible:true,
    store:'Ext.store.dataMaintenance.Roles',
    dockedItems:[
        {

            xtype:'toolbar',
            dock:'top',
            items:[
                { xtype:'tbspacer' },
                {

                    text:'保存',
                    xtype:'button',
                    action:'save',
                    scope:this,
                    iconCls:'icon-save'
                },
                {

                    text:'展开',
                    xtype:'button',
                    action:'expand',
                    scope:this
                }
            ]
        }
    ],
    initComponent:function () {
        var me = this;
        me.columns = [
            {
                header:'value',
                xtype:'treecolumn',
                width:350,
                dataIndex:'value'
            }/*,
            {
                xtype:'checkcolumn',
                header:'资源',
                dataIndex:'hasThisNode',
                width:40,
                stopSelection:false,
                renderer:function (value, metaData, record, rowIndex, colIndex, store, view) {
                    if (record.get("type") == "ROLE") {
                        //todo checkcolumn 中方法
                        var cssPrefix = Ext.baseCSSPrefix,
                            cls = [cssPrefix + 'grid-checkheader'];
                        if (value) {
                            cls.push(cssPrefix + 'grid-checkheader-checked');
                        }
                        return '<div class="' + cls.join(' ') + '">&#160;</div>';
                    } else {
                        return "";
                    }
                }
            }*/
        ];
        me.callParent(arguments);
    }
});