/**
 * 店面经营范围 后台修改
 * 经营范围窗口
 * @author:zhangjuntao
 */

if (Ext.Bcgogo) {
    Ext.Bcgogo['CustomerMange'] = {
        ShopBusinessScopeWindow: {
            readUrl: null,
            params: null
        }
    };
} else {
    Ext.Bcgogo = {
        CustomerMange: {
            ShopBusinessScopeWindow: {
                readUrl: null,
                params: null
            }
        }
    };
}
Ext.define('Ext.view.customerMange.ShopBusinessScopeWindow', {
    alias: 'widget.shopbusinessscopewindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    listeners: {
        beforerender: function () {
            var me = this,
                readUrl = Ext.Bcgogo.CustomerMange.ShopBusinessScopeWindow.readUrl,
                params = Ext.Bcgogo.CustomerMange.ShopBusinessScopeWindow.params;
            if (!readUrl) {
                readUrl = "businessScope.do?method=getBusinessScopeByShopId";
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
                    actionMethods: {
                        create: 'POST', read: 'POST', update: 'POST', destroy: 'POST'
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
                    checkchange: function (node, checked) {
                        me.checkChangeForTree(node, checked);
                    }
                },
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        items: [
                            {
                                xtype: "textfield",
                                emptyText: "经营产品名称",
                                width: 100,
                                name: 'searchName'
                            },
                            {
                                text: "查询",
                                xtype: 'button',
                                iconCls: "icon-search",
                                handler: me.onSearchScope,
                                scope: me
                            },
                            '->',
                            {
                                text: '保存',
                                tooltip: '保存经营产品',
                                iconCls: 'icon-save',
                                action: 'closeSaveBusinessScope',
                                xtype: 'button',
                                handler: function () {
                                    me.saveBusinessScope();
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
    title: '经营产品',
    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        Ext.Bcgogo.CustomerMange.ShopBusinessScopeWindow.readUrl = null;
        Ext.Bcgogo.CustomerMange.ShopBusinessScopeWindow.params = null;
        this.doClose();
    },
    setBusinessScopeTarget: function (businessScopeTarget) {
        this.businessScopeTarget = businessScopeTarget;
    },
    getBusinessScopeTarget: function () {
        return this.businessScopeTarget;
    },
    /**
     * 经营范围 保存
     */
    saveBusinessScope: function () {
        var me = this,
            openTarget = me.getBusinessScopeTarget(),
            tree = me.down('panel'),
            records = tree.getView().getChecked(),
            productCategoryIds = [];

        if (!openTarget) {
            Ext.MessageBox.show({
                title: 'Selected Nodes',
                msg: "经营产品保存失败！",
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        Ext.Array.each(records, function (rec) {
            if (rec.get('type') == 'THIRD_CATEGORY') {
                productCategoryIds.push(rec.get('id'));
            }
        });

        me.drawBusinessScopeTable(me.getSaveBusinessScopeNames(tree.getRootNode()));

        openTarget.setProductCategoryIds(productCategoryIds);
        me.close();
    },

    //经营范围展示
    drawBusinessScopeTable: function (names) {
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
        this.getBusinessScopeTarget().down('[name=showBusinessScope]')
            .setHeight(25 * rows)
            .setValue(table);
    },

    drawShopRegisterProductList: function (products) {
        if (!products || products.length == 0) return;
        var i, rows = products.length, product;
        var table = '<table cellspacing="0" cellpadding="0" style="width: 750px;">';
        table += '<tr>';
        table += '<td>商品编号</td>';
        table += '<td>品名</td>';
        table += '<td>品牌</td>';
        table += '<td>规格</td>';
        table += '<td>型号</td>';
        table += '<td>车牌</td>';
        table += '<td>车型</td>';
        table += '<td>单位</td>';
        table += '</tr> ';
        for (i = 0; i < rows; i++) {
            product = products[i];
            table += '<tr>';
            table += '<td>' + product['commodityCode'] + '</td>';
            table += '<td>' + product['name'] + '</td>';
            table += '<td>' + product['brand'] + '</td>';
            table += '<td>' + product['model'] + '</td>';
            table += '<td>' + product['spec'] + '</td>';
            table += '<td>' + product['productVehicleBrand'] + '</td>';
            table += '<td>' + product['productVehicleModel'] + '</td>';
            table += '<td>' + product['storageUnit'] + '</td>';
            table += '</tr> ';
        }
        table += '</table>';
        this.getBusinessScopeTarget().down('[name=productsInfo]')
            .setHeight(25 * rows)
            .setValue(table);
    },

    //保存 获得经营范围value
    getSaveBusinessScopeNames: function (root) {
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

    //显示 获得经营范围value
    getShowBusinessScopeNames: function (root) {
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

    onSearchScope: function () {
        var me = this,
            panel = me.down('panel'),
            value = me.down('[name=searchName]').getValue(),
            root = panel.getStore().getRootNode();
        root.collapseChildren(true);
        root.collapse();
        me.scopeFuzzyQuery(root, value);
        root.expand();
    },
    //精确查询
    scopeQuery: function (root, value) {
        function expand(node, value) {
            if (node && node.get("id") != -1) {
                node.expand();
                expand(node.parentNode, value);
            }
        }

        expand(root.findChild("value", value, true), value);
    },

    //模糊查询
    scopeFuzzyQuery: function (root, value) {
        var nodes = [], n = 0;

        function reset(node){
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
})
;

