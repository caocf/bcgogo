Ext.define('Ext.view.MenuPanel', {
    extend:'Ext.panel.Panel',
    alias:'widget.menupanel',
    id:'menuPanel',
    region:'west',
    title:"系统菜单",
    width:166,
    iconCls:"icon-tree",
    autoScroll:false,
    layout:'accordion',
    collapsible:true,
    layoutConfig:{
        animate:true
    },
    split:true
   /* afterlayout:function () {
        if (this.getView().el) {
            var el = this.getView().el;
            var table = el.down("table.x-grid-table");
            if (table) {
                table.setWidth(el.getWidth());
            }
        }
    },
    itemclick:function (view, node) {
        var panel;
        if (node.isLeaf()) { //判断是否是根节点
            var contentPanel = Ext.getCmp("contentPanel");
            var mainPanel = Ext.getCmp("mainPanel");
            var currentTab = mainPanel.getComponent(node.data.id);
            if (!currentTab) {
                if (node.data.type === 'URL') { //判断资源类型
                    panel = Ext.create('Ext.panel.Panel', {
                        title:node.data.text,
                        closable:true,
                        id:node.data.id,
                        iconCls:'icon-activity',
                        html:'<iframe width="100%" height="100%" frameborder="0" src="' + node.data.component + '"></iframe>'
                    });
                } else if (node.data.type === 'COMPONENT') {
                    panel = Ext.create(node.data.component, {
                        title:node.data.text,
                        closable:true,
                        id:node.data.id,
                        iconCls:'icon-activity'
                    });
                }
                contentPanel.add(panel);
                contentPanel.setActiveTab(panel);
            } else {
                contentPanel.setActiveTab(currentTab);
            }
        }
    },*/

});