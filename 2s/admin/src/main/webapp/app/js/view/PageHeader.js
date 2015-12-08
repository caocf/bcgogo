Ext.define('Ext.view.PageHeader', {
    extend:'Ext.panel.Panel',
    xtype:'pageheader',
    height:25,
//    html:'<img src="app/images/logo.png" /> ',
    region:'north',
    split:true,
    bbar:[
        {
            text:"统购后台CRM管理系统"
        },
        '-',
        {
            iconCls:'icon-user',
            text:Ext.getDom("userNameForHeader").value
        },
        '-',
        {
            text:Ext.Date.format(new Date(), 'Y年m月d日')
        },
        '->',
        {
            text:'退出',
            iconCls:'icon-logout',
            xtype:'button',
            action:'layout'
        }
    ]
});
