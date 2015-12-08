/**
 * CRM->店铺业务统计->当前采购统计controller.js
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 14-2-27
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.controller.normalProductInventoryStat.ShopProductStatController', {
    extend:'Ext.app.Controller',
    stores:[
        "Ext.store.normalProductInventoryStat.ShopProductInventoryStats"
    ],
    views:[
        'Ext.view.normalProductStat.ShopProductResultView'
    ],

    models: [
      "Ext.model.normalProductInventoryStat.ShopProductInventoryStat"
    ],
    requires:[
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs:[
        {ref: 'shopProductStatList', selector: 'shopProductStatList'}
    ],

    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({


            'shopProductStatList': {
              afterrender: function () {
                me.getShopProductStatList().store.proxy.extraParams = {
                  normalProductStatType: 'WEEK'
                }; //防止 共用store的层 带入参数
                me.getShopProductStatList().store.loadPage(1);
//                me.getShopProductStatList().hide();
              }
            },

            'shopProductStatList [name=productInfo]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductInfo();
                        me.getShopProductStatList().down("[name=productInfo]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchProductInfo();
                    me.getShopProductStatList().down("[name=productInfo]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductInfo();
                        me.getShopProductStatList().down("[name=productInfo]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("productInfo");
                    me.clearShopAndStatus();
                    me.initData(e);
                }
            },
            'shopProductStatList [name=shopProductName]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductName();
                        me.getShopProductStatList().down("[name=shopProductName]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchProductName();
                    me.getShopProductStatList().down("[name=shopProductName]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchProductName();
                        me.getShopProductStatList().down("[name=shopProductName]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopProductName");
                    me.initData(e);
                }
            },
            'shopProductStatList [name=shopBrand]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchBrand();
                        me.getShopProductStatList().down("[name=shopBrand]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchBrand();
                    me.getShopProductStatList().down("[name=shopBrand]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchBrand();
                        me.getShopProductStatList().down("[name=shopBrand]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopBrand");
                    me.initData(e);
                }
            },
            'shopProductStatList [name=shopSpec]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchSpec();
                        me.getShopProductStatList().down("[name=shopSpec]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchSpec();
                    me.getShopProductStatList().down("[name=shopSpec]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchSpec();
                        me.getShopProductStatList().down("[name=shopSpec]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopSpec");
                    me.initData(e);
                }
            },
            'shopProductStatList [name=shopModel]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchModel();
                        me.getShopProductStatList().down("[name=shopModel]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchModel();
                    me.getShopProductStatList().down("[name=shopModel]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchModel();
                        me.getShopProductStatList().down("[name=shopModel]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopModel");
                    me.initData(e);
                }
            },
            'shopProductStatList [name=shopVehicleBrand]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleBrand();
                        me.getShopProductStatList().down("[name=shopVehicleBrand]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchVehicleBrand();
                    me.getShopProductStatList().down("[name=shopVehicleBrand]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleBrand();
                        me.getShopProductStatList().down("[name=shopVehicleBrand]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopVehicleBrand");
                    me.initData(e);
                }
            },
            'shopProductStatList [name=shopVehicleModel]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleModel();
                        me.getShopProductStatList().down("[name=shopVehicleModel]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchVehicleModel();
                    me.getShopProductStatList().down("[name=shopVehicleModel]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchVehicleModel();
                        me.getShopProductStatList().down("[name=shopVehicleModel]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopVehicleModel");
                    me.initData(e);
                }
            },
            'shopProductStatList [name=shopCommodityCode]': {
                keyup: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchCommodityCode();
                        me.getShopProductStatList().down("[name=shopCommodityCode]").store.load();
                    }
                },
                expand: function (e, t, eOpts) {
                    me.searchCommodityCode();
                    me.getShopProductStatList().down("[name=shopCommodityCode]").store.load();
                },
                focus: function (e, t, eOpts) {
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.searchCommodityCode();
                        me.getShopProductStatList().down("[name=shopCommodityCode]").store.load();
                    }
                },
                select: function (e, t, eOpts) {
                    me.clearData("shopCommodityCode");
                    me.initData(e);
                }
            },
            'shopProductStatList button[action=shopProductSearch]':{
                click:function () {
                    var shopProductList = me.getShopProductStatList();
                    var productInfo = shopProductList.down("[name=productInfo]").getValue();
                    var shopProductName = shopProductList.down("[name=shopProductName]").getValue();
                    var shopBrand = shopProductList.down("[name=shopBrand]").getValue();
                    var shopSpec = shopProductList.down("[name=shopSpec]").getValue();
                    var shopModel = shopProductList.down("[name=shopModel]").getValue();
                    var shopVehicleBrand = shopProductList.down("[name=shopVehicleBrand]").getValue();
                    var shopVehicleModel = shopProductList.down("[name=shopVehicleModel]").getValue();
                    var shopCommodityCode = shopProductList.down("[name=shopCommodityCode]").getValue();
                    var shopVersion = shopProductList.down("[name=shopVersion]").getValue();
                    var provinceId = shopProductList.down("[name=province]").getValue();
                    var cityId = shopProductList.down("[name=city]").getValue();
                    var regionId = shopProductList.down("[name=region]").getValue();
                    var shopName = shopProductList.down("[name=shopName]").getValue();
                    var shopProductStatType = shopProductList.down("[name=statDate]").getValue();
                    var data = {
                        productInfo:productInfo,
                        productName:shopProductName,
                        brand:shopBrand,
                        spec:shopSpec,
                        model:shopModel,
                        vehicleBrand:shopVehicleBrand,
                        vehicleModel:shopVehicleModel,
                        commodityCode:shopCommodityCode,
                        normalProductStatType : shopProductStatType,
                        shopVersion:shopVersion,
                        provinceId:provinceId,
                        cityId:cityId,
                        regionId:regionId,
                        shopName:shopName
                    };
                    me.getShopProductStatList().store.proxy.extraParams = data;
                    me.getShopProductStatList().store.loadPage(1);
                }
            }

        });
    },
    searchProductInfo: function () {
        var list = this.getShopProductStatList();
        var data = {
            searchField: 'product_info',
            searchWord: list.down("[name=productInfo]").getValue(),
            now: new Date()
        };
        list.down("[name=productInfo]").getStore().proxy.extraParams = data;
    },
    searchProductName: function () {
        var list = this.getShopProductStatList();
        var data = {
            searchField: 'product_name',
            searchWord: list.down("[name=shopProductName]").getValue(),
            now: new Date()
        };
        list.down("[name=shopProductName]").getStore().proxy.extraParams = data;
    },
    searchBrand: function () {
        var list = this.getShopProductStatList();
        var data = {
            searchField: 'product_brand',
            searchWord: list.down("[name=shopBrand]").getValue(),
            productName: list.down("[name=productInfo]").getValue(),
            now: new Date()
        };
        list.down("[name=shopBrand]").getStore().proxy.extraParams = data;
    },
    searchSpec: function () {
        var list = this.getShopProductStatList();
        var data = {
            searchField: 'product_spec',
            searchWord: list.down("[name=shopSpec]").getValue(),
            productName: list.down("[name=productInfo]").getValue(),
            productBrand: list.down("[name=shopBrand]").getValue(),
            now: new Date()
        };
        list.down("[name=shopSpec]").getStore().proxy.extraParams = data;
    },
    searchModel: function () {
        var list = this.getShopProductStatList();
        var data = {
            searchField: 'product_model',
            searchWord: list.down("[name=shopModel]").getValue(),
            productName: list.down("[name=productInfo]").getValue(),
            productBrand: list.down("[name=shopBrand]").getValue(),
            productSpec: list.down("[name=shopSpec]").getValue(),
            now: new Date()
        };
        list.down("[name=shopModel]").getStore().proxy.extraParams = data;
    },
    searchVehicleBrand: function () {
    var list = this.getShopProductStatList();
    var data = {
        searchField: 'product_vehicle_brand',
        searchWord:  list.down("[name=shopVehicleBrand]").getValue(),
        productName: list.down("[name=productInfo]").getValue(),
        productBrand: list.down("[name=shopBrand]").getValue(),
        productSpec: list.down("[name=shopSpec]").getValue(),
        productModel: list.down("[name=shopModel]").getValue(),
        now: new Date()
    };
    list.down("[name=shopVehicleBrand]").getStore().proxy.extraParams = data;
    },
    searchVehicleModel: function () {
        var list = this.getShopProductStatList();
        var data = {
            searchField: 'product_vehicle_model',
            searchWord: list.down("[name=shopVehicleModel]").getValue(),
            productName: list.down("[name=productInfo]").getValue(),
            productBrand: list.down("[name=shopBrand]").getValue(),
            productSpec: list.down("[name=shopSpec]").getValue(),
            productModel: list.down("[name=shopModel]").getValue(),
            vehicleBrand: list.down("[name=shopVehicleBrand]").getValue(),
            now: new Date()
        };
        list.down("[name=shopVehicleModel]").getStore().proxy.extraParams = data;
    },
    searchCommodityCode: function () {
        var list = this.getShopProductStatList();
        var data = {
            searchField: 'commodity_code',
            searchWord: list.down("[name=shopCommodityCode]").getValue(),
            productName: list.down("[name=productInfo]").getValue(),
            productBrand: list.down("[name=shopBrand]").getValue(),
            productSpec: list.down("[name=shopSpec]").getValue(),
            productModel: list.down("[name=shopModel]").getValue(),
            vehicleBrand: list.down("[name=shopVehicleBrand]").getValue(),
            vehicleModel: list.down("[name=shopVehicleModel]").getValue(),
            now: new Date()
        };
        list.down("[name=shopCommodityCode]").getStore().proxy.extraParams = data;
    },
    clearData: function (inputName) {
        var list = this.getShopProductStatList();
        var productInfo = list.down("[name=productInfo]");
        var shopProductName = list.down("[name=shopProductName]");
        var shopBrand = list.down("[name=shopBrand]");
        var shopSpec = list.down("[name=shopSpec]");
        var shopModel = list.down("[name=shopModel]");
        var shopVehicleBrand = list.down("[name=shopVehicleBrand]");
        var shopVehicleModel = list.down("[name=shopVehicleModel]");
        var shopCommodityCode = list.down("[name=shopCommodityCode]");
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

    },
    initData: function (e) {
        var me = this;
        var list = me.getShopProductStatList();
        var jsonStr = e.displayTplData[0].jsonStr;
        var jsonObj = Ext.decode(jsonStr);
        var productInfo = list.down("[name=productInfo]");
        var shopProductName = list.down("[name=shopProductName]");
        var shopBrand = list.down("[name=shopBrand]");
        var shopSpec = list.down("[name=shopSpec]");
        var shopModel = list.down("[name=shopModel]");
        var shopVehicleBrand = list.down("[name=shopVehicleBrand]");
        var shopVehicleModel = list.down("[name=shopVehicleModel]");
        var shopCommodityCode = list.down("[name=shopCommodityCode]");

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

        var data = {
            commodityCode: shopCommodityCode.getValue(),
            includeBasic: false,
            searchWord: productInfo.getValue(),
            productBrand: shopBrand.getValue(),
            productModel: shopModel.getValue(),
            productName: shopProductName.getValue(),
            productSpec: shopSpec.getValue(),
            productVehicleBrand: shopVehicleBrand.getValue(),
            productVehicleModel: shopVehicleModel.getValue()
        };

        me.getShopProductStatList().store.proxy.extraParams = data;
        me.getShopProductStatList().store.loadPage(1);
    }
});
