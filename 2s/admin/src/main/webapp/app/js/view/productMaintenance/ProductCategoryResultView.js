
Ext.define('Ext.view.productMaintenance.ProductCategoryResultView', {
    extend:'Ext.grid.Panel',
    alias:'widget.productCategoryResultView',
    store:'Ext.store.productMaintenance.ProductCategoryDetails',
    frame:true, //窗口化，即让界面变的饱满
    autoScroll:true,
    columnLines:true,
    stripeRows:true, //每列是否是斑马线分开
    forceFit:true, //自动填充，即让所有列填充满gird宽度
    autoHeight:true,
    title:'产品分类',
    emptyText:"--",
    dockedItems:[
        {
            xtype:'toolbar',
            dock:'top',
            id:'searchProductCategory',
            items:[
                {
                    xtype:"textfield",
                    emptyText:"产品分类",
                    width:300,
                    region:'center',
                    id:'productCategoryName'
                },

                "-",
                {
                    text:"查询",
                    xtype:'button',
                    action:'searchCategory',
                    iconCls:"icon-search",
                    tooltip:"根据条件查询用户信息",
                    scope:this
                },

                {
                    text:"新增分类",
                    xtype:'button',
                    action:'addCategory',
                    iconCls:"icon-add",
                    tooltip:"新增产品分类",
                    scope:this
                }

            ]
        },
        {
          dock:'bottom',
          xtype:'pagingtoolbar',
          store:'Ext.store.productMaintenance.ProductCategoryDetails',
//          store:'Ext.store.sys.Users',
          displayInfo:true

        }
    ],

    initComponent:function () {
        var me = this;
        Ext.apply(me, {
//            store:Ext.create('Ext.store.productMaintenance.ProductCategoryDetails'),
            columns:[
                {
                    header:'No',
                    xtype:'rownumberer',
                    sortable:false,
                    width:25
                },

                {
                    header:'所属系统类别',
                    dataIndex:'firstCategoryName',
                    width : 200
                },
                {
                    header:'所属种类',
                    dataIndex:'secondCategoryName',
                    width : 200
                },
                {
                    header:'品名',
                    dataIndex:'thirdCategoryName',
                    width : 200
                },
                {
                    xtype:'actioncolumn',
                    id:"modifyProductCategory",
                    header:'操作',
                    width:60,
                    items:[
                        {
                            text:'编辑',
                            tooltip:'编辑',
                            scope:me,
                            icon:'app/images/icons/edit.png'
                        }
                    ]
                }
            ]
        });
        this.callParent(arguments);
    }
});
