Ext.define('Ext.view.customerMange.ShopProductWin', {
    extend: 'Ext.window.Window',
    alias: 'widget.shopproductwin',
    width: 800,
    iconCls: 'icon-user',
    layout: 'fit',
    requires: [
        'Ext.view.product.Unit'
    ],
    title: '主营产品（最多10条 最少5条）',
    collapsible: true,
    closeAction: 'hide',
    initComponent: function () {
        var me = this,
            edit = Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1}),
            store = Ext.create("Ext.data.Store", {
                extend: 'Ext.data.Store',
                fields: [
                    { name: 'id', type: "string"},
                    { name: 'name', type: "string"},
                    { name: 'commodityCode', type: "string"},
                    { name: 'brand', type: "string"},
                    { name: 'model', type: "string"},
                    { name: 'spec', type: "string"},
                    { name: 'productVehicleBrand', type: "string"},
                    { name: 'productVehicleModel', type: "string"},
                    { name: 'sellUnit', type: "string"},
                    { name: 'storageUnit', type: "string"}
                ],
                pageSize: 20,
                data: [],
                remoteSort: false
            });
        var grid = Ext.create('Ext.grid.Panel', {
            store: store,
            autoScroll: true,
            columnLines: true,
            stripeRows: true,   //每列是否是斑马线分开
            forceFit: true,     //自动填充，即让所有列填充满gird宽度
            autoHeight: true,
            height: 300,
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            text: '新增商品',
                            xtype: 'button',
                            action: 'add',
                            scope: this,
                            iconCls: 'icon-add',
                            handler: function () {
                                var grid = me.down("grid"),
                                    items = grid.getStore().data.items;
                                if (items.length >= 10) {
                                    Ext.MessageBox.show({
                                        title: '提示',
                                        msg: "最多只能添加10条主营产品!",
                                        icon: Ext.MessageBox.INFO,
                                        buttons: Ext.Msg.OK
                                    });
                                    return;
                                }
                                for (var i = 0; i < items.length; i++) {
                                    var item = items[i];
                                    if (!item.get("name")) {
                                        Ext.MessageBox.show({
                                            title: '提示',
                                            msg: "第" + (i + 1) + "行请输入品名!",
                                            icon: Ext.MessageBox.INFO,
                                            buttons: Ext.Msg.OK
                                        });
                                        return;
                                    }

                                    if (!item.get("storageUnit")) {
                                        Ext.MessageBox.show({
                                            title: '提示',
                                            msg: "第" + (i + 1) + "行请输入单位!",
                                            icon: Ext.MessageBox.INFO,
                                            buttons: Ext.Msg.OK
                                        });
                                        return;
                                    }
                                }
                                edit.cancelEdit();
                                var instance = Ext.create('Ext.model.product.ShopProduct', {
                                    sellUnit:""
                                });
                                store.insert(0, instance);
                                edit.startEditByPosition({
                                    row: 0,
                                    column: 0
                                });
                            }
                        },
                        {
                            text: '删除商品',
                            xtype: 'button',
                            action: 'delete',
                            scope: me,
                            iconCls: 'icon-del',
                            disabled: true,
                            handler: function () {
                                var sm = grid.getSelectionModel();
                                edit.cancelEdit();
                                store.remove(sm.getSelection());
                                if (store.getCount() > 0) {
                                    sm.select(0);
                                }
                            }
                        }
                    ]
                } ,
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            iconCls: 'icon-save',
                            text: '保存',
                            scope: me,
                            handler: me.saveProduct
                        }
                    ]
                }
            ],
            listeners: {
                'selectionchange': function (view, records) {
                    grid.down('[action=delete]').setDisabled(!records.length);
                }
            },
            plugins: [
                edit
            ],
            columns: [
                {
                    header: '商品编号',
                    dataIndex: 'commodityCode',
                    editor: {
                        allowBlank: true
                    }
                },
                {
                    header: '品名',
                    dataIndex: 'name',
                    editor: {
                        allowBlank: true,
                        emptyText: "请输入品名!"
                    }
                },
                {
                    header: '品牌',
                    dataIndex: 'brand',
                    editor: {
                        allowBlank: true
                    }
                },
                {
                    header: '规格',
                    dataIndex: 'spec',
                    editor: {
                        allowBlank: true
                    }
                },
                {
                    header: '型号',
                    dataIndex: 'model',
                    editor: {
                        allowBlank: true
                    }
                },
                {
                    header: '车牌',
                    dataIndex: 'productVehicleBrand',
                    editor: {
                        xtype: "combobox",
                        width: 150,
                        displayField: 'name',
                        valueField: 'name',
                        store: Ext.create("Ext.data.Store", {
                            proxy: new Ext.data.HttpProxy({
                                url: "productSuggestion.do?method=searchBrandSuggestion"
                            }),
                            fields: ["name"],
                            autoLoad: false
                        }),
                        listeners: {
                            'beforequery': function () {
                                this.store.proxy.extraParams = {
                                    searchField: "brand"
                                };
                                this.store.load();
                            },
                            "change": function (combo, newValue, oldValue, eOpts) {
                                if (newValue != oldValue) {
                                    this.up("grid").getSelectionModel().getSelection()[0].set("productVehicleModel", "");
                                }
                            }
                        },
                        queryParam: "searchWord",
                        queryMode: 'remote', //远程过滤
                        enableKeyEvents: true,
                        minChars: 1,
                        queryDelay: 200,
                        allowBlank: true
                    }
                },
                {
                    header: '车型',
                    dataIndex: 'productVehicleModel',
                    editor: {
                        xtype: "combobox",
                        width: 150,
                        displayField: 'name',
                        valueField: 'name',
                        store: Ext.create("Ext.data.Store", {
                            proxy: new Ext.data.HttpProxy({
                                url: "productSuggestion.do?method=searchBrandSuggestion"
                            }),
                            fields: ["name"],
                            autoLoad: false
                        }),
                        listeners: {
                            'beforequery': function () {
                                this.store.proxy.extraParams = {
                                    brandValue: this.up("grid").getSelectionModel().getSelection()[0].get("productVehicleBrand"),
                                    searchField: "model"
                                };
                                this.store.load();
                            }
                        },
                        queryParam: "searchWord",
                        queryMode: 'remote', //远程过滤
                        enableKeyEvents: true,
                        minChars: 1,
                        queryDelay: 200,
                        allowBlank: true
                    }
                },
                {
                    header: '单位',
                    dataIndex: 'storageUnit',
                    editor: {
                        xtype: 'product.unit',
                        allowBlank: true
                    }
                }
            ]
        });
        grid.on('edit', function (editor, e) {
            e.record.commit();
        });
        Ext.apply(me, {
            items: [grid]
        });
        me.callParent();
    },

    setProductTargetWin: function (productTargetWin) {
        this.productTargetWin = productTargetWin;
    },

    getProductTargetWin: function () {
        return this.productTargetWin;
    },

    saveProduct: function () {
        var grid = this.down("grid"),
            items = grid.getStore().data.items, i,
            item, commodityCode, name, brand, model, spec,
            productVehicleBrand, productVehicleModel,
            products = [], productsInfo = [];
        if (items.length < 5) {
            Ext.MessageBox.show({
                title: '提示',
                msg: " 最少要添加5条主营产品！",
                icon: Ext.MessageBox.INFO,
                buttons: Ext.Msg.OK
            });
            return;
        }
        for (i = 0; i < items.length; i++) {
            item = items[i];
            var product = {};
            commodityCode = item.get("commodityCode");
            product['commodityCode'] = item.get("commodityCode");
            commodityCode = commodityCode ? commodityCode : "commodityCode";

            name = item.get("name");
            product['name'] = item.get("name");
            name = name ? name : "name";

            if (!item.get("name")) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "第" + (i + 1) + "行请输入品名!",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }

            if (!item.get("storageUnit")) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "第" + (i + 1) + "行请输入单位!",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }

            brand = item.get("brand");
            product['brand'] = item.get("brand");
            brand = brand ? brand : "brand";

            model = item.get("model");
            product['model'] = item.get("model");
            model = model ? model : "model";

            spec = item.get("spec");
            product['spec'] = item.get("spec");
            spec = spec ? spec : "spec";

            productVehicleBrand = item.get("productVehicleBrand");
            product['productVehicleBrand'] = item.get("productVehicleBrand");
            productVehicleBrand = productVehicleBrand ? productVehicleBrand : "productVehicleBrand";

            productVehicleModel = item.get("productVehicleModel");
            product['productVehicleModel'] = item.get("productVehicleModel");
            productVehicleModel = productVehicleModel ? productVehicleModel : "productVehicleModel";

            product['storageUnit'] = item.get("storageUnit");

            products[i] = product;
            productsInfo[i] = commodityCode + "-" + name + "-" + brand + "-"
                + model + "-" + spec + "-" + productVehicleBrand + "-" + productVehicleModel;
        }
        var commodityCodeMsg="",commodityCode=0;
        for (i = 0; i < productsInfo.length; i++) {
            for (var j = i + 1; j < productsInfo.length && i != j; j++) {
                if (products[i]['commodityCode'] && products[j]['commodityCode'] && (products[i]['commodityCode'] == products[j]['commodityCode'])) {
                    if (commodityCode++ != 0)commodityCodeMsg += "<br>";
                    commodityCodeMsg += "第" + ( i + 1) + "行与第" + ( j + 1) + "行产品编号信息重复，请修改或删除。";
                }
                if (productsInfo[i] == productsInfo[j]) {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: "第" + ( i + 1) + "行与第" + ( j + 1) + "行产品信息重复，请修改或删除。",
                        icon: Ext.MessageBox.INFO,
                        buttons: Ext.Msg.OK
                    });
                    return;
                }
            }
        }
        if (commodityCodeMsg) {
            Ext.MessageBox.show({
                title: '提示',
                msg: commodityCodeMsg,
                icon: Ext.MessageBox.INFO,
                buttons: Ext.Msg.OK
            });
            return;
        }
        if (!this.getProductTargetWin()) {
            Ext.MessageBox.show({
                title: '异常',
                msg: "target window is null!",
                icon: Ext.MessageBox.ERROR,
                buttons: Ext.Msg.OK
            });
            return;
        }
        this.getProductTargetWin().setProducts(products);
        this.getProductTargetWin().drawShopRegisterProductList();
        this.close();
    }
});
