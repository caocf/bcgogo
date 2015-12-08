/**
 * 车型 后台修改
 * 车型范围窗口
 * @author:zhangjuntao
 */
if (Ext.Bcgogo) {
    Ext.Bcgogo['ShopVehicleBrandModelWindow'] = {
        readUrl: null,
        params: null
    };
} else {
    Ext.Bcgogo = {
        ShopVehicleBrandModelWindow: {
            readUrl: null,
            params: null
        }
    };
}

Ext.define('Ext.view.customerMange.ShopVehicleBrandModelWindow', {
    alias: 'widget.shopvehiclebrandmodelwindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    listeners: {
        beforerender: function () {
            var me = this,
                readUrl = Ext.Bcgogo.ShopVehicleBrandModelWindow.readUrl,
                params = Ext.Bcgogo.ShopVehicleBrandModelWindow.params;
            if (!readUrl) {
                readUrl = "businessScope.do?method=getShopVehicleBrandModelByShopId";
            }
            var store = Ext.create('Ext.data.TreeStore', {
                proxy: {
                    type: 'ajax',
                    api: {
                        read: readUrl
                    },
                    reader: {
                        type: 'json'
                    },
                    extraParams: {
                        ids: params
                    }
                },
                fields: [
                    { name: 'id', type: 'string'},
                    { name: 'name', type: 'string'},
                    { name: 'text', type: 'string' },
                    { name: 'value', type: 'string' },
                    { name: 'parentId', type: 'string'},
                    { name: 'hasThisNode', type: 'boolean'},
                    { name: 'leaf', type: 'boolean' },
                    { name: 'sort', type: 'int' },
                    { name: 'iconCls', type: 'string'},
                    { name: 'type', type: 'string' }
                ],
                root: {
                    value: '统购平台',
                    name: 'BCGOGO',
                    leaf: false,
                    iconCls: 'icon-user-set',
                    id: -1,
                    expanded: true
                },
                sorters: [
                    {
                        property: 'sort',
                        direction: 'ASC'
                    }
                ]
            });
            me.removeAll(true);
            var tree = Ext.create('Ext.tree.Panel', {
                store: store,
                rootVisible: false,
                useArrows: true,
                frame: false,
                width: 300,
                height: 550,
                forceFit: true,
                header: false,
                hideHeaders: true,
                listeners: {
                    checkchange: me.checkChangeForTree
//                    afterrender: me.expandChecked
                },
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        items: [
                            {
                                xtype: "textfield",
                                emptyText: "车辆品牌/车型名称",
                                width: 100,
                                name: 'searchName'
                            },
                            {
                                text: "查询",
                                xtype: 'button',
                                iconCls: "icon-search",
                                handler: me.onSearch,
                                scope: me
                            },
                            '->',
                            {
                                text: '保存',
                                iconCls: 'icon-save',
                                action: 'close',
                                xtype: 'button',
                                handler: function () {
                                    me.save();
                                }
                            }
                        ]
                    }
                ],
                columns: [
                    {
                        xtype: 'treecolumn',
                        dataIndex: 'text'
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

    title: '主营车型',

    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        Ext.Bcgogo.ShopVehicleBrandModelWindow.readUrl = null;
        Ext.Bcgogo.ShopVehicleBrandModelWindow.params = null;
        this.hide();
//        this.doClose();
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
            vehicleModelIds = [];

        if (!openTarget) {
            Ext.MessageBox.show({
                title: 'Selected Nodes',
                msg: "主营车型保存失败！",
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        Ext.Array.each(records, function (rec) {
            if (rec.get('type') == 'SECOND_CATEGORY') {
                vehicleModelIds.push(rec.get('id'));
            }
        });

        me.drawShowTable(me.getNamesByExtTree(tree.getRootNode()));

        openTarget.setVehicleModelIds(vehicleModelIds);
        me.close();
    },

    //主营车型展示
    drawShowTable: function (names) {
        if(names.length == 0) return;
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
        this.getOpenTarget().down('[name=vehicleModelIds]')
            .setHeight(25 * rows)
            .setValue(table);
    },

    //保存 获得主营车型value
    getNamesByExtTree: function (root) {
        var names = [];

        function checkChildren(currNode) {
            if (currNode.hasChildNodes()) {
                currNode.eachChild(function (n) {
                    if (n.data.checked) {
                        names.push(n.get('value'));
                    } else {
                        checkChildren(n);
                    }
                });
            }
        }

        checkChildren(root);
        return names;
    },

    //显示 获得主营车型value
    getNames: function (root) {
        var names = [];

        function checkChildren(currNode) {
            if (currNode.children.length > 0) {
                for (var i = 0; i < currNode.children.length; i++) {
                    var n = currNode.children[i];
                    if (n.checked) {
                        names.push(n['value']);
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
//    expandChecked : function(){
//       console.info("==========")
//        function expand(node) {
//            if (node && node.get("id") != -1) {
//                node.expand();
//                expand(node.parentNode);
//            }
//        }
//
//        function find(node) {
//            if (!node.isLeaf()) {
//                var children = node.childNodes;
//                if (children.length == 0) return;
//                for (var i = 0; i < children.length; i++) {
//                    find(children[i]);
//                }
//            } else {
//               if(node.data.checked) {
//                   expand(node);
//               }
//            }
//        }
//        var me = this;
//        find(me.getStore().getRootNode());
//
//
//    }
    ,
    showWin: function () {

    }
})
;

