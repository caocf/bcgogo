Ext.define('Ext.controller.product.ShopProductConfiguration', {
    extend: 'Ext.app.Controller',

    stores: ["Ext.store.product.ShopProducts",
        "Ext.store.product.NormalProducts"
    ],

    models: [
        "Ext.model.product.ShopProduct",
        "Ext.model.product.NormalProduct"
    ],

    views: [
        "Ext.view.product.shopProduct.ShopProductView"
    ],

    requires: [
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        'Ext.view.product.NormalProductVehicleBrandModelWindow',
        "Ext.utils.Common"
    ],

    refs: [
        {ref: 'shopProductView', selector: 'shopProductView'},
        {ref: 'relevanceView', selector: 'relevanceView'},
        {ref: 'productList', selector: 'shopProductView productShopProList'},
        {ref: 'normalProductList', selector: 'shopProductView relevanceView selectNormalProductList'},
        //修改车型
        {id: 'normalproductvehiclebrandmodelwindow', ref: 'normalproductvehiclebrandmodelwindow', selector: 'normalproductvehiclebrandmodelwindow', xtype: 'normalproductvehiclebrandmodelwindow', autoCreate: true},

        //新增标准产品框
        {ref: 'addProductForm', selector: 'shopProductView relevanceView formProduct'}
    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'productShopProList': {
                afterrender: function () {
                    me.getProductList().onSearch();
                    me.getNormalProductList().onSearch();
                    var form = me.getAddProductForm();
                    form.down("[name=formProductSave]").setText("保存&关联");
                    form.down("[name=selectAllBrandModelRadiogroup]").setValue({selectAllBrandModel:true});
                    form.down("[name=vehicleBrandModelInfo]").setValue("所有车型");
                }
            },

            'productShopProList #productInfo': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductInfo();
                        Ext.getCmp("productInfo").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchProductInfo();
                    Ext.getCmp("productInfo").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductInfo();
                        Ext.getCmp("productInfo").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("productInfo");
                    me.clearShopAndStatus();
                    me.initData(e);
                }
            },
            'productShopProList #shopProductName': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductName();
                        Ext.getCmp("shopProductName").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchProductName();
                    Ext.getCmp("shopProductName").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductName();
                        Ext.getCmp("shopProductName").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopProductName");
                    me.initData(e);
                }
            },
            'productShopProList #shopBrand': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchBrand();
                        Ext.getCmp("shopBrand").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchBrand();
                    Ext.getCmp("shopBrand").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchBrand();
                        Ext.getCmp("shopBrand").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopBrand");
                    me.initData(e);
                }
            },
            'productShopProList #shopSpec': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchSpec();
                        Ext.getCmp("shopSpec").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchSpec();
                    Ext.getCmp("shopSpec").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchSpec();
                        Ext.getCmp("shopSpec").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopSpec");
                    me.initData(e);
                }
            },
            'productShopProList #shopModel': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchModel();
                        Ext.getCmp("shopModel").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchModel();
                    Ext.getCmp("shopModel").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchModel();
                        Ext.getCmp("shopModel").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopModel");
                    me.initData(e);
                }
            },
            'productShopProList #shopVehicleBrand': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleBrand();
                        Ext.getCmp("shopVehicleBrand").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchVehicleBrand();
                    Ext.getCmp("shopVehicleBrand").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleBrand();
                        Ext.getCmp("shopVehicleBrand").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopVehicleBrand");
                    me.initData(e);
                }
            },
            'productShopProList #shopVehicleModel': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleModel();
                        Ext.getCmp("shopVehicleModel").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchVehicleModel();
                    Ext.getCmp("shopVehicleModel").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleModel();
                        Ext.getCmp("shopVehicleModel").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopVehicleModel");
                    me.initData(e);
                }
            },
            'productShopProList #shopCommodityCode': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchCommodityCode();
                        Ext.getCmp("shopCommodityCode").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchCommodityCode();
                    Ext.getCmp("shopCommodityCode").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchCommodityCode();
                        Ext.getCmp("shopCommodityCode").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopCommodityCode");
                    me.initData(e);
                }
            },
            'productShopProList actioncolumn#shopProductListGridAction': {
                examineBindingClick: me.examineBindingInGrid,
                copyNormalProductClick: me.copyNormalProductInGrid,
                cancelBindingClick: me.cancelBindingInGrid

            },
            'productShopProList button[action=relevanceNormalProduct]': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_product_shop_search_normal")) {
                        view.hide();
                    }
                },
                click: function (e, t, eOpts) {
                    if (me.getRelevanceView().collapsed) {
                        me.getRelevanceView().expand(true);
                    }else {
                        me.getRelevanceView().collapse(true);
                    }
                }
            },
            'relevanceView':{
                expand:function( p, eOpts ){
                    me.getProductList().down("button[action=relevanceNormalProduct]").setIconCls('icon-product-asc');
                },
                collapse:function( p, eOpts){
                    me.getProductList().down("button[action=relevanceNormalProduct]").setIconCls('icon-product-desc');
                }
            },
            //店铺商品中的标准化商品列表
            'selectNormalProductList actioncolumn#selectNormalProductListGridAction': {
                copyQueryConditionClick: me.copyQueryConditionInGrid
            },
            'selectNormalProductList #normalSearchFirstCategory': {
                select: function () {
                    var firstSelect = Ext.getCmp("normalSearchFirstCategory");
                    var secondSelect = Ext.getCmp("normalSearchSecondCategory");
                    secondSelect.clearValue();
                    secondSelect.store.proxy.extraParams = {
                        parentId: firstSelect.getValue()
                    };
                    secondSelect.store.load();
                    //清空后面的
                    Ext.getCmp("normalSearchProductName").setValue("");
                    Ext.getCmp("normalSearchSpec").setValue("");
                    Ext.getCmp("normalSearchModel").setValue("");
                    Ext.getCmp("normalSearchVehicleBrand").setValue("");
                    Ext.getCmp("normalSearchVehicleModel").setValue("");
                }
            },

            'selectNormalProductList #normalSearchSecondCategory': {
                select: function () {
                    //清空后面的
                    Ext.getCmp("normalSearchProductName").setValue("");
                    Ext.getCmp("normalSearchBrand").setValue("");
                    Ext.getCmp("normalSearchSpec").setValue("");
                    Ext.getCmp("normalSearchModel").setValue("");
                    Ext.getCmp("normalSearchVehicleBrand").setValue("");
                    Ext.getCmp("normalSearchVehicleModel").setValue("");
                }
            },

            'selectNormalProductList #normalSearchProductName': {
                keyup: function (e, t, eOpts) {
                    me.clearSearchInput('PRODUCT_NAME');
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                },
                expand: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                },
                focus: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                    Ext.getCmp("normalSearchProductName").getStore().load();
                    e.expand();
                }
            },
            'selectNormalProductList #normalSearchBrand': {
                keyup: function (e, t, eOpts) {
                    me.clearSearchInput('BRAND');
                    me.getDataByQueryBuilder('BRAND');
                },
                expand: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('BRAND');
                },
                focus: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('BRAND');
                    Ext.getCmp("normalSearchBrand").getStore().load();
                    e.expand();
                }
            },
            'selectNormalProductList #normalSearchSpec': {
                keyup: function (e, t, eOpts) {
                    me.clearSearchInput('SPEC');
                    me.getDataByQueryBuilder('SPEC');
                },
                expand: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('SPEC');
                },
                click: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('SPEC');
                },
                focus: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('SPEC');
                    Ext.getCmp("normalSearchSpec").getStore().load();
                    e.expand();
                }
            },
            'selectNormalProductList #normalSearchModel': {
                keyup: function (e, t, eOpts) {
                    me.clearSearchInput('MODEL');
                    me.getDataByQueryBuilder('MODEL');
                },
                click: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('MODEL');
                },
                expand: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('MODEL');
                },
                focus: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('MODEL');
                    Ext.getCmp("normalSearchModel").getStore().load();
                    e.expand();
                }
            },
            'selectNormalProductList #normalSearchVehicleBrand': {
                keyup: function (e, t, eOpts) {
                    me.clearSearchInput('VEHICLE_BRAND');
                    me.getDataByQueryBuilder('VEHICLE_BRAND');
                },
                click: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('VEHICLE_BRAND');
                },
                expand: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('VEHICLE_BRAND');
                },
                focus: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('VEHICLE_BRAND');
                    Ext.getCmp("normalSearchVehicleBrand").getStore().load();
                    e.expand();
                }
            },
            'selectNormalProductList #normalSearchVehicleModel': {
                keyup: function (e, t, eOpts) {
                    me.clearSearchInput('VEHICLE_MODEL');
                    me.getDataByQueryBuilder('VEHICLE_MODEL');
                },
                click: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('VEHICLE_MODEL');
                },
                expand: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('VEHICLE_MODEL');
                },
                focus: function (e, t, eOpts) {
                    me.getDataByQueryBuilder('VEHICLE_MODEL');
                    Ext.getCmp("normalSearchVehicleModel").getStore().load();
                    e.expand();
                }

            },
            'relevanceView button[action=relevance]': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_product_shop_normal")) {
                        view.hide();
                    }
                },
                click:function(){
                    me.relevanceProduct();
                }
            },
            'shopProductView relevanceView formProduct button[action=save]': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_product_shop_add_normal")) {
                        view.hide();
                    }
                },
                click: me.addAndBingdingProduct
            },
            'shopProductView relevanceView formProduct [action=modifyVehicleModel]' : {
                "click" : function(){
                    var form = me.getAddProductForm();
                    if(form.down("[name=selectAllBrandModel]").getValue()){
                        form.down("[name=vehicleBrandModelInfo]").setValue("");
                    }
                    form.down("[name=selectAllBrandModelRadiogroup]").setValue({selectAllBrandModel:false});
                    me.showVehicleBrandModel(me.getRelevanceView());
                }
            },
            'shopProductView relevanceView formProduct [name=selectAllBrandModel]': {
                change: function (radio, newValue, oldValue, eOpts) {
                    if(radio.inputValue && newValue){
                        var form = me.getAddProductForm();
                        var treeWin = me.getNormalproductvehiclebrandmodelwindow();
                        if(treeWin.isVisible()){
                            treeWin.close();
                        }
                        form.down("[name=vehicleBrandModelInfo]").setValue("所有车型");
                    }
                    if(!radio.inputValue && newValue){
                        var form = me.getAddProductForm();
                        if(form.down("[name=selectAllBrandModel]").getValue()){
                            form.down("[name=vehicleBrandModelInfo]").setValue("");
                        }
                        me.showVehicleBrandModel(me.getRelevanceView());
                    }
                }
            },

            'shopProductView relevanceView formProduct [name=addFormFirstCategorySelect]': {
                select: function () {
                    var me = this,
                        form = me.getAddProductForm();
                    var secondSelect = form.down("[name=addFormSecondCategorySelect]");
                    secondSelect.clearValue();
                    form.down("[name=productCategoryId]").clearValue();
                }
            },

            'shopProductView relevanceView formProduct [name=addFormSecondCategorySelect]': {
                expand: function (e, t, eOpts) {
                    var me = this,
                        form = me.getAddProductForm();
                    var firstSelect = form.down("[name=addFormFirstCategorySelect]");
                    var secondSelect = form.down("[name=addFormSecondCategorySelect]");
                    secondSelect.store.proxy.extraParams = {
                        parentId: firstSelect.getValue()
                    };
                    secondSelect.store.load();
                },
                select: function () {
                    var form = me.getAddProductForm();
                    form.down("[name=productCategoryId]").clearValue();
                    me.addFormProductSearch();
                }
            },

            'shopProductView relevanceView formProduct [name=productCategoryId]': {
                keyup: function (e, t, eOpts) {
                    e.expand(e,t,eOpts);
                },
                expand: function (e, t, eOpts) {
                    me.addFormProductSearch();
                },
                focus: function (e, t, eOpts) {
                    e.expand(e,t,eOpts);
                },
                select: function (e, t, eOpts) {
                    var me = this,
                        form = me.getAddProductForm();
                    form.down("[name=productName]").setValue(e.getValue());
                    var firstSelect = form.down("[name=addFormFirstCategorySelect]");
                    var secondSelect = form.down("[name=addFormSecondCategorySelect]");
                    if(Ext.isEmpty(firstSelect.getValue()) || Ext.isEmpty(secondSelect.getValue())){
                        me.commonUtils.ajax({
                            url: 'productManage.do?method=getParentCategoryById',
                            params: {id: e.getValue()},
                            success: function (result) {
                                var secondCategory = result.secondCategory;
                                var firstCategory = result.firstCategory;
                                firstSelect.clearValue();
                                secondSelect.clearValue();
                                firstSelect.getStore().load();
                                firstSelect.setValue(firstCategory.id);
                                secondSelect.getStore().loadData([
                                    {name: secondCategory.name, id: secondCategory.id}
                                ]);
                                secondSelect.setValue(secondCategory.id);
                            }
                        });
                    }
                }
            },
            'shopProductView relevanceView formProduct [name=commodityCode]': {
                keyup: function (e, t, eOpts) {
                    var value = e.getValue().replace(/[—]/g, "-")
                        .replace(/[×]/g, "*")
                        .replace(/(^\s+)|(\s+$)/g, "")
                        .replace(/[^0-9a-zA-Z\+\-\*\/\$\%]/g, "")
                        .toUpperCase();
                    e.setValue(value);
                }
            },
            'shopProductView relevanceView formProduct [name=unit]': {
                keyup: function (e, t, eOpts) {
                    var value = e.getValue().replace(" ", "");
                    e.setValue(value);
                }
            }
        });
    },
//    searchShop: function () {
//        var data = {
//            keyWord: Ext.getCmp("searchShop").getValue(),
//            now: new Date()
//        };
//        Ext.getCmp("searchShop").getStore().proxy.extraParams = data;
//    },
    searchProductInfo: function () {
        var data = {
            searchField: 'product_info',
            searchWord: Ext.getCmp("productInfo").getValue(),
            now: new Date()
        };
        Ext.getCmp("productInfo").getStore().proxy.extraParams = data;
    },
    searchProductName: function () {
        var data = {
            searchField: 'product_name',
            searchWord: Ext.getCmp("shopProductName").getValue(),
            now: new Date()
        };
        Ext.getCmp("shopProductName").getStore().proxy.extraParams = data;
    },
    searchBrand: function () {
        var data = {
            searchField: 'product_brand',
            searchWord: Ext.getCmp("shopBrand").getValue(),
            productName: Ext.getCmp("productInfo").getValue(),
            now: new Date()
        };
        Ext.getCmp("shopBrand").getStore().proxy.extraParams = data;
    },
    searchSpec: function () {
        var data = {
            searchField: 'product_spec',
            searchWord: Ext.getCmp("shopSpec").getValue(),
            productName: Ext.getCmp("productInfo").getValue(),
            productBrand: Ext.getCmp("shopBrand").getValue(),
            now: new Date()
        };
        Ext.getCmp("shopSpec").getStore().proxy.extraParams = data;
    },
    searchModel: function () {
        var data = {
            searchField: 'product_model',
            searchWord: Ext.getCmp("shopModel").getValue(),
            productName: Ext.getCmp("productInfo").getValue(),
            productBrand: Ext.getCmp("shopBrand").getValue(),
            productSpec: Ext.getCmp("shopSpec").getValue(),
            now: new Date()
        };
        Ext.getCmp("shopModel").getStore().proxy.extraParams = data;
    },
    searchVehicleBrand: function () {
        var data = {
            searchField: 'product_vehicle_brand',
            searchWord: Ext.getCmp("shopVehicleBrand").getValue(),
            productName: Ext.getCmp("productInfo").getValue(),
            productBrand: Ext.getCmp("shopBrand").getValue(),
            productSpec: Ext.getCmp("shopSpec").getValue(),
            productModel: Ext.getCmp("shopModel").getValue(),
            now: new Date()
        };
        Ext.getCmp("shopVehicleBrand").getStore().proxy.extraParams = data;
    },
    searchVehicleModel: function () {
        var data = {
            searchField: 'product_vehicle_model',
            searchWord: Ext.getCmp("shopVehicleModel").getValue(),
            productName: Ext.getCmp("productInfo").getValue(),
            productBrand: Ext.getCmp("shopBrand").getValue(),
            productSpec: Ext.getCmp("shopSpec").getValue(),
            productModel: Ext.getCmp("shopModel").getValue(),
            vehicleBrand: Ext.getCmp("shopVehicleBrand").getValue(),
            now: new Date()
        };
        Ext.getCmp("shopVehicleModel").getStore().proxy.extraParams = data;
    },
    searchCommodityCode: function () {
        var data = {
            searchField: 'commodity_code',
            searchWord: Ext.getCmp("shopCommodityCode").getValue(),
            productName: Ext.getCmp("productInfo").getValue(),
            productBrand: Ext.getCmp("shopBrand").getValue(),
            productSpec: Ext.getCmp("shopSpec").getValue(),
            productModel: Ext.getCmp("shopModel").getValue(),
            vehicleBrand: Ext.getCmp("shopVehicleBrand").getValue(),
            vehicleModel: Ext.getCmp("shopVehicleModel").getValue(),
            now: new Date()
        };
        Ext.getCmp("shopCommodityCode").getStore().proxy.extraParams = data;
    },
    clearData: function (inputName) {
        var productInfo = Ext.getCmp("productInfo");
        var shopProductName = Ext.getCmp("shopProductName");
        var shopBrand = Ext.getCmp("shopBrand");
        var shopSpec = Ext.getCmp("shopSpec");
        var shopModel = Ext.getCmp("shopModel");
        var shopVehicleBrand = Ext.getCmp("shopVehicleBrand");
        var shopVehicleModel = Ext.getCmp("shopVehicleModel");
        var shopCommodityCode = Ext.getCmp("shopCommodityCode");
        if ("productInfo" == inputName) {
            shopProductName.clearValue();
            shopBrand.clearValue();
            shopSpec.clearValue();
            shopModel.clearValue();
            shopVehicleBrand.clearValue();
            shopVehicleModel.clearValue();
            shopCommodityCode.clearValue();
        }
        if ("shopProductName" == inputName) {
            shopBrand.clearValue();
            shopSpec.clearValue();
            shopModel.clearValue();
            shopVehicleBrand.clearValue();
            shopVehicleModel.clearValue();
            shopCommodityCode.clearValue();
        }
        if ("shopBrand" == inputName) {
            shopSpec.clearValue();
            shopModel.clearValue();
            shopVehicleBrand.clearValue();
            shopVehicleModel.clearValue();
            shopCommodityCode.clearValue();
        }
        if ("shopSpec" == inputName) {
            shopModel.clearValue();
            shopVehicleBrand.clearValue();
            shopVehicleModel.clearValue();
            shopCommodityCode.clearValue();
        }
        if ("shopModel" == inputName) {
            shopVehicleBrand.clearValue();
            shopVehicleModel.clearValue();
            shopCommodityCode.clearValue();
        }
        if ("shopVehicleBrand" == inputName) {
            shopVehicleModel.clearValue();
            shopCommodityCode.clearValue();
        }
        if ("shopVehicleModel" == inputName) {
            shopCommodityCode.clearValue();
        }
    },
    clearShopAndStatus: function () {
        var searchShop = Ext.getCmp("searchShop");
        searchShop.clearValue();
        Ext.getCmp("relevanceStatusCheckBoxGroup").items.each(function(item) {
            item.setValue(false);
        });
    },
    initData: function (e) {
        var me = this;
        me.getProductList().getSelectionModel().deselectAll();
        var jsonStr = e.displayTplData[0].jsonStr;
        var jsonObj = Ext.decode(jsonStr);
        var productInfo = Ext.getCmp("productInfo");
        var shopProductName = Ext.getCmp("shopProductName");
        var shopBrand = Ext.getCmp("shopBrand");
        var shopSpec = Ext.getCmp("shopSpec");
        var shopModel = Ext.getCmp("shopModel");
        var shopVehicleBrand = Ext.getCmp("shopVehicleBrand");
        var shopVehicleModel = Ext.getCmp("shopVehicleModel");
        var shopCommodityCode = Ext.getCmp("shopCommodityCode");

        if (jsonObj.product_name) {
            shopProductName.setValue(jsonObj.product_name);
        }
        if (jsonObj.product_model) {
            shopModel.setValue(jsonObj.product_model);
        }
        if (jsonObj.product_brand) {
            shopBrand.setValue(jsonObj.product_brand);
        }
        if (jsonObj.commodity_code) {
            shopCommodityCode.setValue(jsonObj.commodity_code)
        }
        if (jsonObj.product_vehicle_model) {
            shopVehicleModel.setValue(jsonObj.commodity_code)
        }
        if (jsonObj.product_vehicle_brand) {
            shopVehicleBrand.setValue(jsonObj.commodity_code)
        }

//        var data = {
//            commodityCode: shopCommodityCode.getValue(),
//            includeBasic: false,
//            searchWord: productInfo.getValue(),
//            productBrand: shopBrand.getValue(),
//            productModel: shopModel.getValue(),
//            productName: shopProductName.getValue(),
//            productSpec: shopSpec.getValue(),
//            productVehicleBrand: shopVehicleBrand.getValue(),
//            productVehicleModel: shopVehicleModel.getValue()
//        };

        me.getProductList().onSearch();
    },
    getDataByQueryBuilder: function (inputName) {
        var firstSelect = Ext.getCmp("normalSearchFirstCategory");
        var secondSelect = Ext.getCmp("normalSearchSecondCategory");
        var firstCategoryId = firstSelect.getValue();
        var secondCategoryId = secondSelect.getValue();
        var productNameCmp = Ext.getCmp("normalSearchProductName");
        var productName = Ext.getCmp("normalSearchProductName").getRawValue();
        var thirdCategoryId = Ext.getCmp("normalSearchProductName").getValue();
        var brand = Ext.getCmp("normalSearchBrand").getValue();
        var spec = Ext.getCmp("normalSearchSpec").getValue();
        var model = Ext.getCmp("normalSearchModel").getValue();
        var vehicleBrand = Ext.getCmp("normalSearchVehicleBrand").getValue();
        var vehicleModel = Ext.getCmp("normalSearchVehicleModel").getValue();
        if (thirdCategoryId && isNaN(thirdCategoryId)) {
            thirdCategoryId = "";
        }
        var data = {
            inputName: inputName,
            productName: productName,
            brand: brand,
            spec: spec,
            model: model,
            vehicleBrand: vehicleBrand,
            vehicleModel: vehicleModel,
            firstCategoryId: firstCategoryId,
            secondCategoryId: secondCategoryId,
            thirdCategoryId: thirdCategoryId,
            now: new Date()
        };

        var me = this;
        //这里要判断是哪个框
        if ("PRODUCT_NAME" == inputName) {
            Ext.getCmp("normalSearchProductName").store.proxy.extraParams = data;
        }
        if ("BRAND" == inputName) {
            Ext.getCmp("normalSearchBrand").store.proxy.extraParams = data;
        }
        if ("SPEC" == inputName) {
            Ext.getCmp("normalSearchSpec").store.proxy.extraParams = data;
        }
        if ("MODEL" == inputName) {
            Ext.getCmp("normalSearchModel").store.proxy.extraParams = data;
        }
        if ("VEHICLE_BRAND" == inputName) {
            Ext.getCmp("normalSearchVehicleBrand").store.proxy.extraParams = data;
        }
        if ("VEHICLE_MODEL" == inputName) {
            Ext.getCmp("normalSearchVehicleModel").store.proxy.extraParams = data;
        }

    },
    clearSearchInput: function (inputName) {
        //根据inputName来清空数据
        if ("PRODUCT_NAME" == inputName) {
            Ext.getCmp("normalSearchBrand").setValue("");
            Ext.getCmp("normalSearchSpec").setValue("");
            Ext.getCmp("normalSearchModel").setValue("");
            Ext.getCmp("normalSearchVehicleBrand").setValue("");
            Ext.getCmp("normalSearchVehicleModel").setValue("");
        }
        if ("BRAND" == inputName) {
            Ext.getCmp("normalSearchSpec").setValue("");
            Ext.getCmp("normalSearchModel").setValue("");
            Ext.getCmp("normalSearchVehicleBrand").setValue("");
            Ext.getCmp("normalSearchVehicleModel").setValue("");
        }
        if ("SPEC" == inputName) {
            Ext.getCmp("normalSearchModel").setValue("");
            Ext.getCmp("normalSearchVehicleBrand").setValue("");
            Ext.getCmp("normalSearchVehicleModel").setValue("");
        }
        if ("MODEL" == inputName) {
            Ext.getCmp("normalSearchVehicleBrand").setValue("");
            Ext.getCmp("normalSearchVehicleModel").setValue("");
        }
        if ("VEHICLE_BRAND" == inputName) {
            Ext.getCmp("normalSearchVehicleModel").setValue("");
        }
    },
    addAndBingdingProduct: function () {
        var me = this,
            form = me.getAddProductForm(),
            formEl = form.getEl(),
            baseForm = form.form;
        var productBox = form.down("[name=productCategoryId]");
        var name = productBox.getRawValue();

        if (null == productBox.getValue() || isNaN(productBox.getValue())) {
            Ext.Msg.alert("验证结果", "请选择品名下拉框中的数据，不要手输");
            return;
        }
        if(!form.down("[name=selectAllBrandModel]").getValue() && Ext.isEmpty(form.down("[name=vehicleModelIds]").getValue())){
            Ext.Msg.alert("验证结果", "请选择适用车型！");
            return;
        }
        if (baseForm.isValid()) {
            formEl.mask('正在保存 . . .');

            form.down("[name=productName]").setValue(name);
            var shopProductList = me.commonUtils.getSelectionIds(me.getProductList());

            if (shopProductList) {
                shopProductList = shopProductList.substring(0, shopProductList.length - 1);
            }

            var data = baseForm.getFieldValues();

            data.scene = 'shopProduct';
            data.shopProductIds = shopProductList;
            me.commonUtils.ajax({
                url: 'productManage.do?method=saveOrUpdateNormalProduct',
                params: data,
                success: function (result) {
                    formEl.unmask();
                    if (result.result == "error") {
                        Ext.Msg.alert("返回结果", result.errorMsg);
                    }
                    else {
                        me.getNormalProductList().store.loadPage(1);
                        if (shopProductList) {
                            me.getProductList().store.loadPage(1);
                            Ext.Msg.alert('返回结果', "保存并关联成功");
                        }
                        else {
                            Ext.Msg.alert('返回结果', "保存成功");
                        }
                        baseForm.reset();
                    }
                },
                failure: function (response) {
                    formEl.unmask();
                }
            });
        }
    },

    addFormProductSearch: function () {
        var me = this,
            form = me.getAddProductForm();
        var firstSelect = form.down("[name=addFormFirstCategorySelect]");
        var secondSelect = form.down("[name=addFormSecondCategorySelect]");
        var firstCategoryId = firstSelect.getValue();
        var secondCategoryId = secondSelect.getValue();
        var productName = form.down("[name=productCategoryId]").getRawValue();

        var data = {
            inputName: "PRODUCT_NAME",
            productName: productName,
            firstCategoryId: firstCategoryId,
            secondCategoryId: secondCategoryId
        };
        form.down("[name=productCategoryId]").store.proxy.extraParams = data;
        form.down("[name=productCategoryId]").store.load();
    },
    relevanceProduct: function () {
        var me=this, normalProduct = me.commonUtils.getSelectionIds(me.getNormalProductList());
        var shopProductList = me.commonUtils.getSelectionIds(me.getProductList());
        if (!shopProductList) {
            Ext.Msg.alert("信息", "请选择店铺商品");
            return;
        }
        if (!normalProduct) {
            Ext.Msg.alert("信息", "请选择标准产品");
            return;
        }
        var list = me.getProductList().getSelectionModel().getSelection();
        var status = true;
        for (var i = 0; i < list.length; i++) {
            if (list[i].data.relevanceStatus == "YES") {
                status = false;
                break;
            }
        }
        if (normalProduct) {
            normalProduct = normalProduct.substring(0, normalProduct.length - 1);
        }
        if (shopProductList) {
            shopProductList = shopProductList.substring(0, shopProductList.length - 1);
        }
        var data = {
            shopProductIds: shopProductList.split(","),
            normalProductId: normalProduct,
            now: new Date()
        };
        //检查是否该店铺其他商品已经与标准商品建立关联
        Ext.MessageBox.confirm("信息提示", "是否确定要标准化此"+list.length+"条店铺商品？", function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url: 'productManage.do?method=verifyRelevanceProduct',
                    params: data,
                    success: function (result) {
                        if (!result["success"]) {
                            if(result["data"]*1>0){
                                Ext.MessageBox.confirm("信息提示", "当前选择的店铺商品有"+result["data"]+"条是待复核/已标准，系统只会关联未标准的店铺商品，是否确定继续？", function (btn) {
                                    if (btn == "yes") {
                                        me.relevanceProductAction(data);
                                    }
                                });
                            }else{
                                Ext.Msg.alert('返回结果', result.msg);
                            }

                        } else {
//                            if (status) {
//                                Ext.getBody().mask('正在关联....');
//                                me.relevanceProductAction(data);
//                            } else {
//                                Ext.Msg.confirm('系统提示', '选中的店铺商品中有已经关联的商品，是否确定重新关联', function (btn) {
//                                    if (btn == 'yes') {
//                                        me.relevanceProductAction(data);
//                                    }
//                                }, this);
//                            }
                            me.relevanceProductAction(data);
                        }
                    },
                    failure: function () {
                    }
                });
            }
        });
    },

    relevanceProductAction:function(data){
      var me=this;
        Ext.getBody().mask('正在标准化....');
        me.commonUtils.ajax({
            url: 'productManage.do?method=relevanceProduct',
            params: data,
            success: function (result) {
                Ext.getBody().unmask();
                if (result.result == "success") {
                    Ext.Msg.alert('返回结果', "标准化成功");
                    me.getProductList().getStore().load();
                    me.getNormalProductList().getStore().load();
                }
                else {
                    Ext.Msg.alert('返回结果', result.errorMsg);
                }
            },
            failure: function () {
                Ext.getBody().unmask();
            }
        });
    },
    copyNormalProductInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        Ext.MessageBox.confirm("信息提示", "是否确定引用为标准商品？", function (btn) {
            if (btn == "yes") {
                var form = me.getAddProductForm();
                form.form.reset();
                form.down("[name=selectAllBrandModel]").setValue(true);
                form.down("[name=vehicleBrandModelInfo]").setValue("所有车型");
                form.down("[name=spec]").setValue(rec.data.spec);
                form.down("[name=unit]").setValue(rec.data.sellUnit);
                form.down("[name=brand]").setValue(rec.data.brand);
                form.down("[name=model]").setValue(rec.data.model);
                me.getRelevanceView().expand(true);
                form.show();
            }
        });
    },
    examineBindingInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        Ext.MessageBox.confirm("信息提示", "复核通过后状态变为已标准，是否确定复核通过？", function (btn) {
            if (btn == "yes") {
                Ext.getBody().mask('正在复核标准化....');
                var id = rec.data.id
                me.commonUtils.ajax({
                    url:'productManage.do?method=checkRelevance',
                    params:{id:id},
                    success:function (result) {
                        Ext.getBody().unmask();
                        if (result.result == "success") {
                            Ext.Msg.alert('返回结果', "标准化成功");
                            me.getProductList().getStore().load();
                            me.getNormalProductList().getStore().load();
                        }
                        else {
                            Ext.Msg.alert('返回结果', result.errorMsg);
                        }
                    }
                });
            }
        });
    },
    cancelBindingInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        Ext.MessageBox.confirm("信息提示", "是否确定要取消绑定？", function (btn) {
            if (btn == "yes") {
                Ext.getBody().mask('正在取消绑定....');
                var id = rec.data.id
                me.commonUtils.ajax({
                    url:'productManage.do?method=deleteRelevance',
                    params:{id:id},
                    success:function (result) {
                        Ext.getBody().unmask();
                        if (result.result == "success") {
                            Ext.Msg.alert('返回结果', "取消成功");
                            me.getProductList().getStore().load();
                            me.getNormalProductList().getStore().load();
                        }
                        else {
                            Ext.Msg.alert('返回结果', result.errorMsg);
                        }
                    }
                });
            }
        });
    },
    copyQueryConditionInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),
            me = this;
        Ext.getCmp("shopProductName").setValue(rec.get("productName"));
        Ext.getCmp("shopBrand").setValue(rec.get("brand"));
        Ext.getCmp("shopSpec").setValue(rec.get("spec"));
        Ext.getCmp("shopModel").setValue(rec.get("model"));
        me.getProductList().onSearch();
    },
    //主营车型
    showVehicleBrandModel: function(target){
        var me = this;
        var vehicleModelIds = target.down("[name=vehicleModelIds]").getValue();
        if (!vehicleModelIds) {
            Ext.Bcgogo.NormalProductVehicleBrandModelWindow.readUrl = "productManage.do?method=getNormalProductVehicleBrandModelByNormalProductId";
        } else {
            Ext.Bcgogo.NormalProductVehicleBrandModelWindow.readUrl = "productManage.do?method=getCheckedNormalProductVehicleBrandModel";
        }
        if (vehicleModelIds)
            Ext.Bcgogo.NormalProductVehicleBrandModelWindow.extraParams = {"ids":vehicleModelIds};
        var win = me.getNormalproductvehiclebrandmodelwindow();
        win.setOpenTarget(target);
        if (win.down('treepanel')) {
            var rootTree = win.down('treepanel').getRootNode();
            me.commonUtils.ajax({
                url: Ext.Bcgogo.NormalProductVehicleBrandModelWindow.readUrl,
                params:Ext.Bcgogo.NormalProductVehicleBrandModelWindow.extraParams,
                success: function (result) {
                    for (var i = 0; i < rootTree.childNodes.length; i++) {
                        var parent = rootTree.childNodes[i],
                            pData = result.children[i];
                        parent.data.text = pData.value;
                        parent.data.checked = pData.checked;
                        parent.updateInfo({checked: pData.checked});
                        if (pData.expanded) {
                            parent.expand();
                        } else {
                            parent.collapse();
                        }
                        for (var j = 0; j < parent.childNodes.length; j++) {
                            var child = parent.childNodes[j],
                                cData = pData.children[j];
                            child.data.text = cData.value;
                            child.data.checked = cData.checked;
                            child.updateInfo({checked: cData.checked});
                            if (cData.expanded) {
                                child.expand();
                            } else {
                                child.collapse();
                            }
                        }
                    }
                    win.show();
                }
            });
        } else {
            win.show();
        }
    }
});