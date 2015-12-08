/**
 * 车型 后台修改
 * 车型范围窗口
 * @author:zhangjuntao
 */
if (Ext.Bcgogo) {
    Ext.Bcgogo['NormalProductVehicleBrandModelWindow'] = {
        readUrl: null,
        params: null
    };
} else {
    Ext.Bcgogo = {
        NormalProductVehicleBrandModelWindow: {
            readUrl: null,
            params: null
        }
    };
}

Ext.define('Ext.view.product.NormalProductVehicleBrandModelWindow', {
    alias: 'widget.normalproductvehiclebrandmodelwindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    listeners: {
        beforerender: function () {
            var me = this,
                readUrl = Ext.Bcgogo.NormalProductVehicleBrandModelWindow.readUrl,
                extraParams = Ext.Bcgogo.NormalProductVehicleBrandModelWindow.extraParams;
            if (!readUrl) {
                readUrl = "productManager.do?method=getShopVehicleBrandModelByShopId";
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
                    extraParams: extraParams
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
                    type:'root',
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
                height: 450,
                forceFit: true,
                header: false,
                hideHeaders: true,
                listeners: {
                    checkchange: me.checkChangeForTree
                },
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        items: [
                            {
                                xtype: "textfield",
                                emptyText: "车辆品牌/车型名称",
                                width: 150,
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
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.callParent();
    },

    title: '主营车型',

    close: function () {
        this.commonUtils.unmask();
        Ext.Bcgogo.NormalProductVehicleBrandModelWindow.readUrl = null;
        Ext.Bcgogo.NormalProductVehicleBrandModelWindow.params = null;
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
            vehicleModelIds = [];

        if (!openTarget) {
            Ext.MessageBox.show({
                title: 'Selected Nodes',
                msg: "适用车型保存失败！",
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
        openTarget.down("[name=vehicleModelIds]").setValue(vehicleModelIds.join(","));
        me.close();
    },
    //主营车型展示
    drawShowTable: function (infoJson) {
        if(infoJson.length == 0) return;
        var vehicleBrandModelInfoDetail = "";
        for ( var key in infoJson ){
            if(Ext.isEmpty(vehicleBrandModelInfoDetail)){
                vehicleBrandModelInfoDetail = key;
            }else{
                vehicleBrandModelInfoDetail +=","+key;
            }
            if(!Ext.isEmpty(infoJson[key])){
                vehicleBrandModelInfoDetail += "("+infoJson[key]+")";
            }
        }

        this.getOpenTarget().down('[name=vehicleBrandModelInfo]')
            .setValue(vehicleBrandModelInfoDetail);
    },

    //保存 获得主营车型value
    getNamesByExtTree: function (root) {
        var info = {};

        function checkChildren(currNode) {
            if (currNode.hasChildNodes()) {
                if(currNode.data.checked){
                    info[currNode.get("value")] = "";
                }else{
                    currNode.eachChild(function (n) {
                        if (n.data.checked && "root"!=currNode.get("type")) {
                            var childInfo = info[currNode.get("value")];
                            if(Ext.isEmpty(childInfo)){
                                childInfo = n.get('value');
                            }else{
                                childInfo+="/"+n.get('value');
                            }
                            info[currNode.get("value")] = childInfo;
                        } else {
                            checkChildren(n);
                        }
                    });
                }
            }
        }

        checkChildren(root);
        return info;
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

})
;

