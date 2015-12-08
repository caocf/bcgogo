Ext.define('Ext.controller.Main', {
    extend:'Ext.app.Controller',
    views:[
        'Ext.view.Viewport',
        'Ext.view.PageHeader',
        'Ext.view.ContentPanel',
        'Ext.view.MenuPanel'
    ],
    init:function () {
        var me = this;
        me.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.control({
            'viewport menupanel':{
                afterrender:function () {
                    var me = this;
                    Ext.getBody().mask('正在加载系统菜单....');
                    me.commonUtils.ajax({
                        url:"view.do?method=getTreeMenuByParentId", // 获取面板的地址
                        success:function (data) {
                            var panels = Ext.getCmp("menuPanel");
                            Ext.getBody().unmask();
                            for (var i = 0; i < data.length; i++) {
                                panels.add(Ext.create("Ext.tree.Panel", {
                                    title:data[i].text,
                                    iconCls:data[i].iconCls,
                                    //useArrows: true,
                                    autoScroll:true,
                                    rootVisible:false,
                                    viewConfig:{
                                        loadingText:"正在加载..."
                                    },
                                    store:me.createStore(data[i].id),
                                    listeners:{
                                        itemclick:function (view, record) {
                                            var contentPanel = Ext.getCmp("contentPanel");
                                            var data = record.raw;
                                            if (record.isLeaf()) { //判断是否是根节点
                                                var currentTab = contentPanel.getComponent(data.id);
                                                if (!currentTab) {
                                                    if (data.type === 'URL') { //判断资源类型
                                                        currentTab = Ext.create('Ext.panel.Panel', {
                                                            autoScroll:true,
                                                            title:data.text,
                                                            closable:true,
                                                            id:data.id,
                                                            iconCls:'icon-activity',
                                                            html:'<iframe width="100%" height="100%" frameborder="0" src="' + data.component + '"></iframe>'
                                                        });
                                                    } else if (data.type === 'COMPONENT') {
                                                        //初始化 controller
                                                        var views = me.componentUtils.getViewsByController(
                                                            me.componentUtils.initController(me.application, data.component));
                                                        if (views) {
                                                            //初始化view
                                                            currentTab = Ext.create(views[0], {
                                                                autoScroll:true,
                                                                title:data.text,
                                                                closable:true,
                                                                id:data.id,
                                                                iconCls:'icon-activity'
                                                            });
                                                        }
                                                    }
                                                    contentPanel.add(currentTab);
                                                }
                                                contentPanel.setActiveTab(currentTab);
                                            }
                                        }
                                    }
                                }));
                                panels.doLayout();
                            }
                        }
                    });
                }
            },
            "viewport pageheader button[action=layout]":{
                click:function(){
                    Ext.MessageBox.confirm('确认',"确认退出系统？", function (btn) {
                        if (btn == "yes") {
                           window.location = me.commonUtils.getContextPath() + "/j_spring_security_logout";
                        }
                    });
                }
            }
        });
    },
    createStore:function (id) {
        return Ext.create("Ext.store.TreeMenu", {
            defaultRootId:id
        });
    }

});