Ext.define('Ext.view.Viewport', {
    extend:'Ext.container.Viewport',
    layout:'border',

    items:[
        {   //header
            xtype:'pageheader'
        },
        {   //menu
            xtype:'menupanel'
        },
        {   //content
            xtype:"contentpanel"
        },
        {   //footer
            height:20,
            xtype:"panel",
            region:'south',
            html:'版本3.0(CRM) | 版权所有(c) 2012 -苏州威尼尤至软件科技有限公司，保留所有权利。'

        }
    ]
});
