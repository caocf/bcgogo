/**
 * 店铺 广告投放范围后台修改
 * 店铺 广告投放范围窗口
 * @author:qiuxinyu
 */
if (Ext.Bcgogo) {
    Ext.Bcgogo['ShopRecommendScope'] = {
        readUrl: null,
        params: null,
        shopId:null
    };
} else {
    Ext.Bcgogo = {
        ShopRecommendScope: {
            readUrl: null,
            params: null,
            shopId:null
        }
    };
}

Ext.define('Ext.view.customerMange.ShopRecommendScope', {
    alias: 'widget.shoprecommendscope',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    title: '广告类目',
    store:"",
    collapsible: true,
    requires:[
        'Ext.grid.plugin.CellEditing',
        'Ext.grid.column.Action',
        'Ext.view.customerMange.RecommendTreeMenu'
    ],
    listeners: {
        beforerender: function () {
            var me = this,
                readUrl = Ext.Bcgogo.ShopRecommendScope.readUrl,
                params = Ext.Bcgogo.ShopRecommendScope.params,
                shopId = Ext.Bcgogo.ShopRecommendScope.shopId;
            if (!readUrl) {
                readUrl = "shopAd.do?method=getShopRecommend";
            }
            var store = Ext.create('Ext.store.customerMange.RecommendShopStore', {
                proxy: {
                    type: 'ajax',
                    api: {
                        read: readUrl
                    },
                    reader: {
                        type: 'json'
                    },
                    extraParams: {
                        ids: params,
                        shopId:shopId
                    }
                }
            });
            me.removeAll(true);

            var tree = Ext.create('Ext.tree.Panel', {
                store: store,
                rootVisible: false,
                frame: false,
                width: 300,
                height: 550,
                forceFit: true,
                checkModel:'childCascade',
                listeners: {
                    checkchange: me.checkChangeForTree

                },
                plugins:[
                    Ext.create('Ext.grid.plugin.CellEditing',{
                        clicksToEdit: 2
                    })
                ],
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        items: [{
                                text: '保存',
                                iconCls: 'icon-save',
                                action: 'close',
                                xtype: 'button',
                                handler: function () {
                                    me.save();
                                }
                            },
                            {
                                text: '增加一级分类',
                                iconCls: 'icon-add',
                                action: 'addFirstRecommendTreeCategory',
                                xtype: 'button',
                                handler: function () {
                                    me.addFirstRecommendTreeCategory();
                                }
                            },
                            {
                                text: '刷新',
                                iconCls: 'icon-refresh',
                                action: 'refreshRecommendTree',
                                xtype: 'button',
                                handler: function () {
                                    me.refreshRecommendTree();
                                }
                            }
                        ]
                    }
                ],
                columns: [
                    {
                        header:'name',
                        text: "value",
                        xtype: 'treecolumn',
                        dataIndex: 'text',
                        editor: {
                            xtype: 'textfield',
                            selectOnFocus: true,
                            allowBlank:false
                        }
                    },
                    {
                        text:'sort',
                        editor: {
                            xtype: 'textfield',
                            selectOnFocus: true,
                            allowBlank:false
                        },
                        flex:1,
                        width:50,
                        dataIndex:'sort'
                    }
                ]
            });
            me.add(tree);
        }

    },
    initComponent: function () {
        var me = this;
        me.callParent();
    },



    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        Ext.Bcgogo.ShopAdAreaScope.readUrl = null;
        Ext.Bcgogo.ShopAdAreaScope.params = null;
        Ext.Bcgogo.ShopAdAreaScope.shopId = null;
        this.hide();
    },

    setOpenTarget: function (openTarget) {
        this.openTarget = openTarget;
    },
    getOpenTarget: function () {
        return this.openTarget;
    },

    save: function () {
        var me = this,
            openTarget = me.getOpenTarget(),
            tree = me.down('panel'),
            records = tree.getView().getChecked(),
            recommendIds = [];

        if (!openTarget) {
            Ext.MessageBox.show({
                title: 'Selected Nodes',
                msg: "广告类目保存失败！",
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        Ext.Array.each(records, function (rec) {
            if (rec.get('parentId') != '-1') {
                recommendIds.push(rec.get('id'));
            }
        });

        me.drawShowTable(me.getNamesByExtTree(tree.getRootNode()));

        openTarget.setShopRecommendIds(recommendIds);
        me.close();
    },
    addFirstRecommendTreeCategory:function(){
        var me = this;
        var newModule = Ext.create('Ext.model.customerMange.RecommendShop', {
            text:"新分类",
            leaf:false,
            checked:false,
            expand:true,
            type:"RECOMMEND_SHOP",
            parentId:'-1',
            loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
        });

        var listTree = me.down("treepanel"),
            cellEditingPlugin = listTree.editingPlugin,
            selectionModel = listTree.getSelectionModel(),
            selectedList = selectionModel.getSelection()[0],
            parentList = listTree.getRootNode();
        parentList.appendChild(newModule);
        selectionModel.select(newModule);
        cellEditingPlugin.startEdit(newModule, 0);
    },
    refreshRecommendTree:function(){
        var me = this;
        var listTree = me.down("treepanel");
        listTree.store.load();
    },

    //选中的广告区域展示
    drawShowTable: function (names) {
        if(names.length == 0){
            this.getOpenTarget().down('[name=selectedShopRecommend]')
                .setValue("");
        }else{
            var width = names.length > 10 ? 750 : ( (names.length / 10) * 750);
            var table = '<table cellspacing="0" cellpadding="0" style="width: ' + width + 'px;">';
            var i, rows = 0;
            for (i = 0; i < names.length;) {
                if (i % 10 == 0) {
                    rows++;
                    table += '<tr>';
                }
                table += '<td>' + names[i] + '</td>';
                i++;
                if (i % 10 == 0) {
                    table += '</tr> ';
                }
            }
            table += '</table>';
            this.getOpenTarget().down('[name=selectedShopRecommend]')
                .setHeight(25 * rows)
                .setValue(table);
        }

    },

    //保存 获得广告区域的value
    getNamesByExtTree: function (root) {
        var names = [];

        function checkChildren(currNode) {
            if (currNode.hasChildNodes()) {
                currNode.eachChild(function (n) {
                    if (n.data.checked) {
                        names.push(n.get('text'));
                    } else {
                        checkChildren(n);
                    }
                });
            }
        }

        checkChildren(root);
        return names;
    },

    //显示 获得广告区域value
    getNames: function (root) {
        var names = [];

        function checkChildren(currNode) {
            if (currNode.children.length > 0) {
                for (var i = 0; i < currNode.children.length; i++) {
                    var n = currNode.children[i];
                    if (n.checked) {
                        names.push(n['text']);
                    } else {
                        checkChildren(n);
                    }
                }
            }
        }

        checkChildren(root);
        return names;
    },

    /**
     * shop business scope check
     * @param node
     * @param checked
     */
    checkChangeForTree: function (node, checked) {
        function checkChildren(currNode) {
            if (currNode.hasChildNodes()) {
                currNode.eachChild(function (n) {
                    n.data.checked = checked;
                    n.updateInfo({checked: checked});
                    checkChildren(n);
                });
            }
        }

        function checkParent(currNode, currChecked) {
            if (currNode.parentNode && currNode.parentNode.get("name") != "BCGOGO"
                && currNode.parentNode.get("name") != "Root") {
                if (!currChecked) {
                    currNode.parentNode.data.checked = false;
                    currNode.parentNode.updateInfo({checked: false});
                    checkParent(currNode.parentNode, false);
                } else {
                    if (traverse(currNode.parentNode)) {
                        currNode.parentNode.data.checked = true;
                        currNode.parentNode.updateInfo({checked: true});
                        checkParent(currNode.parentNode, true);
                    } else {
                        currNode.parentNode.data.checked = false;
                        currNode.parentNode.updateInfo({checked: false});
                        checkParent(currNode.parentNode, false);
                    }
                }
            }
        }

        /**
         * 检查子项是否checked
         * @param currNode 当前节点
         * @returns {boolean}
         */
        function traverse(currNode) {
            if (currNode.hasChildNodes()) {
                for (var i = 0, max = currNode.childNodes.length; i < max; i++) {
                    if (!traverse(currNode.childNodes[i]))
                        return false;
                }
            } else {
                if (!currNode.data.checked)
                    return false;
            }
            return true;
        }

        checkChildren(node);
        checkParent(node, checked);
    },

    onSearch: function () {
        var me = this,
            panel = me.down('panel'),
            value = me.down('[name=searchName]').getValue(),
            root = panel.getStore().getRootNode();
        root.collapseChildren(true);
        root.collapse();
        me.scopeFuzzyQuery(root, value);
        root.expand();
    },

    //模糊查询
    scopeFuzzyQuery: function (root, value) {
        var nodes = [], n = 0;

        function reset(node) {
            if (node) {
                if (!node.isLeaf()) {
                    var children = node.childNodes;
                    if (children.length == 0) return;
                    for (var i = 0; i < children.length; i++) {
                        node.data.text = node.data.value;
                        reset(children[i]);
                    }
                } else {
                    node.data.text = node.data.value;
                }
            }
        }

        function equal(node, value) {
            if (!node)return false;
            var result = node.data.value && node.data.value.indexOf(value) != -1;
            if (result) {
                node.data.text = '<span class="icon-tree-selected">' + node.data.value + '</font>';
                nodes[n++] = node;
            }
            return result;
        }

        function expand(node) {
            if (node && node.get("id") != -1) {
                node.expand();
                expand(node.parentNode);
            }
        }

        function find(node, value) {
            if (node && value) {
                if (!node.isLeaf()) {
                    var children = node.childNodes;
                    if (children.length == 0) return;
                    for (var i = 0; i < children.length; i++) {
                        equal(children[i], value);
                        find(children[i], value);
                    }
                } else {
                    equal(node, value);
                }
            }
        }

        reset(root);

        find(root, value);

        if (n > 0) {
            for (var i = 0; i < nodes.length; i++) {
                expand(nodes[i]);
            }
        }
    }
    ,
    showWin: function () {

    }
})
;

