/**
 * 后台CRM->店铺财务统计->采购统计专用controller.js
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-9
 * Time: 上午9:47
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.controller.normalProductInventoryStat.NormalProductStatController', {
    extend:'Ext.app.Controller',

    stores:[
      "Ext.store.normalProductInventoryStat.NormalProductInventoryStats",
      "Ext.store.normalProductInventoryStat.NormalProductStatDetails"
    ],

    models:[
      "Ext.model.normalProductInventoryStat.NormalProductInventoryStat",
      "Ext.model.normalProductInventoryStat.NormalProductStatDetail"
    ],

    views:[
        'Ext.view.normalProductStat.NormalProductResultView'
    ],

    requires:[
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs:[
        {ref:'normalProductResultView', selector:'normalProductResultView'},
        {ref:'normalProductStatDetail', selector:'normalProductStatDetail'},
        {ref:'normalProductStatList', selector:'normalProductStatList'}
    ],

    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({

            'normalProductStatList':{
                afterrender:function () {
                    me.getNormalProductStatList().store.proxy.extraParams = {
                      normalProductStatType:'WEEK'
                    }; //防止 共用store的层 带入参数
                    me.getNormalProductStatList().store.loadPage(1);
                    me.getNormalProductStatDetail().hide();
                },
                selectionchange: function (view, records) {
                    if (me.permissionUtils.hasPermission("CRM_normal_product_stat_search")) {
                        me.showStatDateDetails();
                    }

                }
            },

//            "normalProductStatList actioncolumn":{
//
//              beforerender:function (view, eOpts) {
//                if (!me.permissionUtils.hasPermission("CRM_normal_product_stat_search")) {
//                   view.hide();
//                }
//              },
//              click:function (grid, cell, row, col, e) {
//                    var rec = grid.getStore().getAt(row);
//
//                    var index = me.componentUtils.getActionColumnItemsIndex(e);
//                        me.showStatDateDetails(grid, row, col);
//                }
//            },

            'normalProductStatList #firstCategorySearch':{
                select:function() {
                    var firstSelect = Ext.getCmp("firstCategorySearch");
                    var secondSelect = Ext.getCmp("secondCategorySearch");
                    secondSelect.clearValue();
                    secondSelect.store.proxy.extraParams = {
                        parentId : firstSelect.getValue()
                    };
                    secondSelect.store.load();
                    //清空后面的
                    Ext.getCmp("productNameSearch").setValue("");
                    Ext.getCmp("specSearch").setValue("");
                    Ext.getCmp("modelSearch").setValue("");
                    Ext.getCmp("vehicleBrandSearch").setValue("");
                    Ext.getCmp("vehicleModelSearch").setValue("");
                }
            },

            'normalProductStatList #secondCategorySearch':{
                select:function() {
                    //清空后面的
                    Ext.getCmp("productNameSearch").setValue("");
                    Ext.getCmp("brandSearch").setValue("");
                    Ext.getCmp("specSearch").setValue("");
                    Ext.getCmp("modelSearch").setValue("");
                    Ext.getCmp("vehicleBrandSearch").setValue("");
                    Ext.getCmp("vehicleModelSearch").setValue("");
                }
            },

            'normalProductStatList #productNameSearch':{
                keyup:function(e,t,eOpts){
                    //暂时还没有产品分类，先模拟后台
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                },
                expand:function(e,t,eOpts){
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                },
                focus:function(e,t,eOpts){
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                }
            },
            'normalProductStatList #brandSearch':{
                keyup:function(e,t,eOpts){
                    //暂时还没有产品分类，先模拟后台
                    me.getDataByQueryBuilder('BRAND');
                },
                click:function(e,t,eOpts){
                    me.getDataByQueryBuilder('BRAND');
                }
            },
            'normalProductStatList #specSearch':{
                keyup:function(e,t,eOpts){
                    //暂时还没有产品分类，先模拟后台
                    me.getDataByQueryBuilder('SPEC');
                },
                click:function(e,t,eOpts){
                    me.getDataByQueryBuilder('SPEC');
                }
            },
            'normalProductStatList #modelSearch':{
                keyup:function(e,t,eOpts){
                    //暂时还没有产品分类，先模拟后台
                    me.getDataByQueryBuilder('MODEL');
                },
                click:function(e,t,eOpts){
                    me.getDataByQueryBuilder('MODEL');
                }
            },
            'normalProductStatList #vehicleBrandSearch':{
                keyup:function(e,t,eOpts){
                    //暂时还没有产品分类，先模拟后台
                    me.getDataByQueryBuilder('VEHICLE_BRAND');
                },
                click:function(e,t,eOpts){
                    me.getDataByQueryBuilder('VEHICLE_BRAND');
                }
            },
            'normalProductStatList #vehicleModelSearch':{
                keyup:function(e,t,eOpts){
                    me.getDataByQueryBuilder('VEHICLE_MODEL');
                },
                click:function(e,t,eOpts){
                    me.getDataByQueryBuilder('VEHICLE_MODEL');
                }

            },
            'normalProductStatList #commodityCodeSearch':{
                keyup:function(e,t,eOpts){
                    me.getDataByQueryBuilder('COMMODITY_CODE');
                },
                click:function(e,t,eOpts){
                    me.getDataByQueryBuilder('COMMODITY_CODE');
                }

            },

            'normalProductStatList button[action=normalProductSearch]':{
                click:function () {
                    var normalProductList = me.getNormalProductStatList();
                    var firstSelect = Ext.getCmp("firstCategorySearch");
                    var secondSelect = Ext.getCmp("secondCategorySearch");
                    var firstCategoryId = firstSelect.getValue();
                    var secondCategoryId = secondSelect.getValue();
                    var productNameCmp = Ext.getCmp("productNameSearch");
                    var productName = Ext.getCmp("productNameSearch").getRawValue();
                    var thirdCategoryId = Ext.getCmp("productNameSearch").getValue();
                    var brand = Ext.getCmp("brandSearch").getValue();
                    var spec = Ext.getCmp("specSearch").getValue();
                    var model = Ext.getCmp("modelSearch").getValue();
                    var vehicleBrand = Ext.getCmp("vehicleBrandSearch").getValue();
                    var vehicleModel = Ext.getCmp("vehicleModelSearch").getValue();
                    var commodityCode = Ext.getCmp("commodityCodeSearch").getValue();
                    var normalProductStatType = Ext.getCmp("statTypeId").getValue();
                    var shopVersion = normalProductList.down("[name=shopVersion]").getValue();
                    var provinceId = normalProductList.down("[name=province]").getValue();
                    var cityId = normalProductList.down("[name=city]").getValue();
                    var regionId = normalProductList.down("[name=region]").getValue();
                    if(thirdCategoryId && isNaN(thirdCategoryId))
                    {
                        thirdCategoryId = "";
                    }
                    var data = {
//                        inputName:inputName,
                        productName:productName,
                        brand:brand,
                        spec:spec,
                        model:model,
                        vehicleBrand:vehicleBrand,
                        vehicleModel:vehicleModel,
                        firstCategoryId:firstCategoryId,
                        secondCategoryId:secondCategoryId,
                        thirdCategoryId:thirdCategoryId,
                        commodityCode:commodityCode,
                        normalProductStatType : normalProductStatType,
                        shopVersion:shopVersion,
                        provinceId:provinceId,
                        cityId:cityId,
                        regionId:regionId
                    };
                    me.getNormalProductStatList().store.proxy.extraParams = data; //防止 共用store的层 带入参数
                    me.getNormalProductStatList().store.loadPage(1);
                    me.getNormalProductStatDetail().hide();
                }
            }
        });
    },

    changeStyle:function(val) {
      if (val > 0) {
        return '<span style="color:red;">' + val + '</span>';
      } else if (val < 0) {
        return '<span style="color:red;">' + val + '</span>';
      }
      return val;
    },

    /**
     * Custom function used for column renderer
     * @param {Object} val
     */
    pctChange:function(val) {
      if (val > 0) {
        return '<span style="color:green;">' + val + '%</span>';
      } else if (val < 0) {
        return '<span style="color:red;">' + val + '%</span>';
      }
      return val;
    },

//    showStatDateDetails:function (grid, rowIndex, colIndex) {
//        var me = this,
//            rec = grid.getStore().getAt(rowIndex),
//            view = me.getNormalProductStatDetail();
//
//        if(rec.raw.normalProductId == null || rec.raw.normalProductId == ""){
//          return;
//        }
//        var commodityCode = rec.raw.commodityCode;
//        view.store.proxy.extraParams = {
//          normalProductStatType:rec.raw.normalProductStatType,
//          normalProductIdStr:rec.raw.normalProductId
//        }; //防止 共用store的层 带入参数
//        view.store.loadPage(1);
//        view.setTitle("产品编码为"+ commodityCode +"各个店铺的采购明细");
//        view.show();
//
//    },
    showStatDateDetails:function () {
        var me = this,
            rec = me.getNormalProductStatList().getSelectionModel().getSelection()[0],
            view = me.getNormalProductStatDetail();
        if(rec) {
            if(rec.raw.normalProductId == null || rec.raw.normalProductId == ""){
                return;
            }
            var commodityCode = rec.raw.commodityCode;
            var normalProductList = me.getNormalProductStatList();

            view.store.proxy.extraParams = {
                normalProductStatType:rec.raw.normalProductStatType,
                normalProductIdStr:rec.raw.normalProductId,
                shopVersion : normalProductList.down("[name=shopVersion]").getValue(),
                provinceId : normalProductList.down("[name=province]").getValue(),
                cityId : normalProductList.down("[name=city]").getValue(),
                regionId : normalProductList.down("[name=region]").getValue()
            }; //防止 共用store的层 带入参数
            view.store.loadPage(1);
            view.setTitle("产品编码为"+ commodityCode +"各个店铺的采购明细");
            view.show();
        }
    },


    getDataByQueryBuilder:function(inputName){
        var firstSelect = Ext.getCmp("firstCategorySearch");
        var secondSelect = Ext.getCmp("secondCategorySearch");
        var firstCategoryId = firstSelect.getValue();
        var secondCategoryId = secondSelect.getValue();
        var productNameCmp = Ext.getCmp("productNameSearch");
        var productName = Ext.getCmp("productNameSearch").getRawValue();
        var thirdCategoryId = Ext.getCmp("productNameSearch").getValue();
        var brand = Ext.getCmp("brandSearch").getValue();
        var spec = Ext.getCmp("specSearch").getValue();
        var model = Ext.getCmp("modelSearch").getValue();
        var vehicleBrand = Ext.getCmp("vehicleBrandSearch").getValue();
        var vehicleModel = Ext.getCmp("vehicleModelSearch").getValue();
        var commodityCode = Ext.getCmp("commodityCodeSearch").getValue();
        if(thirdCategoryId && isNaN(thirdCategoryId))
        {
            thirdCategoryId = "";
        }
        var data = {
            inputName:inputName,
            productName:productName,
            brand:brand,
            spec:spec,
            model:model,
            vehicleBrand:vehicleBrand,
            vehicleModel:vehicleModel,
            firstCategoryId:firstCategoryId,
            secondCategoryId:secondCategoryId,
            thirdCategoryId:thirdCategoryId,
            commodityCode:commodityCode
        };

        var me = this;
        //这里要判断是哪个框
        if("PRODUCT_NAME"==inputName)
        {
            Ext.getCmp("productNameSearch").store.proxy.extraParams = data;
//            Ext.getCmp("searchProductName").store.load();
        }
        if("BRAND"==inputName)
        {
            Ext.getCmp("brandSearch").store.proxy.extraParams = data;
//            Ext.getCmp("searchBrand").store.load();
        }
        if("SPEC"==inputName)
        {
            Ext.getCmp("specSearch").store.proxy.extraParams = data;
//            Ext.getCmp("searchSpec").store.load();
        }
        if("MODEL"==inputName)
        {
            Ext.getCmp("modelSearch").store.proxy.extraParams = data;
//            Ext.getCmp("searchModel").store.load();
        }
        if("VEHICLE_BRAND"==inputName)
        {
            Ext.getCmp("vehicleBrandSearch").store.proxy.extraParams = data;
//            Ext.getCmp("searchVehicleBrand").store.load();
        }
        if("VEHICLE_MODEL"==inputName)
        {
            Ext.getCmp("vehicleModelSearch").store.proxy.extraParams = data;
//            Ext.getCmp("searchVehicleModel").store.load();
        }
        if("COMMODITY_CODE"==inputName)
        {
            Ext.getCmp("commodityCodeSearch").store.proxy.extraParams = data;
        }
    }
});