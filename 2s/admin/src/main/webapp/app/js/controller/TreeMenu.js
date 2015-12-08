Ext.define('Ext.controller.TreeMenu', {
    extend:'Ext.app.Controller',
    views:[
        'TreePanel'
    ],
    store:"TreeMenu",
    model:"TreeMenu",
    init:function () {
        this.control({
            'treemenu':{
                itemclick:function (view, node) {
                    var panel;
                    if (node.isLeaf()) { //判断是否是根节点
                        var contentPanel = Ext.getCmp("contentPanel");
                        var panels = Ext.getCmp("menuPanel");
                        var currentTab = panels.getComponent(node.data.id);
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
                }
            }
        })
    }
});