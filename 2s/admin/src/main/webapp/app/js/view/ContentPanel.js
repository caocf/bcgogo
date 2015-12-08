Ext.define('Ext.view.ContentPanel', {
    extend:'Ext.tab.Panel',
    xtype:'contentpanel',
    region:"center",
    id:"contentPanel",
    requires:[ 'Ext.app.Portlet', 'Ext.app.PortalColumn', 'Ext.app.PortalPanel',
        'Ext.app.PortalDropZone', 'Ext.ux.TabReorderer',
        'Ext.ux.TabCloseMenu'],
    items:[
        {
            iconCls:'icon-activity',
            title:'首页',
            xtype:'panel',
            layout:'column',
            items:[
                {
                    xtype:'portalcolumn',
                    columnWidth:0.5,
                    items:[
                        { title:'热点客户', height:140, iconCls:'icon-news' },
                        {title:'待处理客户要投诉', height:140, iconCls:'icon-notice' },
                        {title:'公司业绩图', height:200, iconCls:'icon-chart'}
                    ]
                },
                {
                    xtype:'portalcolumn',
                    columnWidth:0.5,
                    items:[
                        { title:'需要您审批的流程', height:140, iconCls:'icon-link'},
                        {title:'回款情况', height:140, iconCls:'icon-note' },
                        {title:'销售员业绩', height:200, iconCls:'icon-email-list'}
                    ]
                }
            ]
        }
    ],
    plugins:[Ext.create('Ext.ux.TabReorderer'),
        Ext.create('Ext.ux.TabCloseMenu', {
            closeTabText:'关闭面板',
            closeOthersTabsText:'关闭其他',
            closeAllTabsText:'关闭所有'
        })]
});
