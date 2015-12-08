Ext.define('Ext.controller.product.NormalProductConfiguration', {
    extend:'Ext.app.Controller',

    stores:["Ext.store.product.NormalProducts",
        "Ext.store.product.ShopProducts"
    ],

    models:["Ext.model.product.NormalProduct",
        "Ext.model.product.ShopProduct"
    ],

    views:[
        'Ext.view.product.normalProduct.NormalProductView'
    ],

    requires:[
        "Ext.view.product.normalProduct.Add",
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common",
        'Ext.view.product.NormalProductVehicleBrandModelWindow',
        "Ext.view.product.normalProduct.ShowShopProductWin"
    ],

    refs:[
        {ref:'productList', selector:'productNormalProList'},
        {ref:'addAndUpdateProductForm', selector:'windowAddProduct formProduct'},
        //修改车型
        {id: 'normalproductvehiclebrandmodelwindow', ref: 'normalproductvehiclebrandmodelwindow', selector: 'normalproductvehiclebrandmodelwindow', xtype: 'normalproductvehiclebrandmodelwindow', autoCreate: true},

        //弹出新增标准产品框
        {ref:'windowAddProduct', selector:'windowAddProduct', autoCreate:true,xtype:'windowAddProduct'},
        //弹出关联商品列表
        {ref:'showProductWin',selector:'showProductWin',autoCreate:true,xtype:'showProductWin'},
        {ref:'searchRelevanceShopProduct', selector:'showProductWin searchRelevanceShopProduct'},
        {ref:'oneNormalProduct',selector:'showProductWin oneNormalProduct'}
    ],

    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({

            'productNormalProList':{
                afterrender:function () {
                    me.getProductList().store.proxy.extraParams = {}; //防止 共用store的层 带入参数
                    me.getProductList().store.loadPage(1);
//                    me.getProductList().store.on('load', function(){
//                        var keys = me.getProductList().store.data.keys;
//                        for(var i = 0; i < keys.length; i++){
//                            Ext.EventManager.addListener('list-modify-vehicle-'+keys[i],'click',me.showVehicleBrandModel,me);//绑定处理函数
//                        }
//                    },me);
                },
                selectionchange:function (view, records) {
                    if (!me.permissionUtils.hasPermission("CRM_product_normal_search_relevance")) {
                        view.hide();
                    }else{
                        if (records.length > 0) {
                            me.showShopProductList(records[0]);
                        }
                    }
                }
            },
            'productNormalProList [action=edit]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_product_normal_update")) {
                        view.hide();
                    }
                },
                click:function (grid, cell, row, col, e) {
                    var me = this,
                        rec = grid.getStore().getAt(row);
                    var bingingProductCount = rec.get("bindingShopProductCount")*1;
                    if(bingingProductCount>0){
                        Ext.MessageBox.confirm("信息提示", "该标准产品已经绑定了"+bingingProductCount+"个店铺商品，是否要继续编辑操作?", function (btn) {
                            if (btn == "yes") {
                                me.editNormalProduct(grid, row, col);
                            }
                        });
                    }else{
                        me.editNormalProduct(grid, row, col);
                    }
                }
            },
            'productNormalProList [action=delete]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_product_normal_delete")) {
                        view.hide();
                    }
                },
                click:function (grid, cell, row, col, e) {
                    var me = this,
                        rec = grid.getStore().getAt(row);
                    var bingingProductCount = rec.get("bindingShopProductCount")*1;
                    var msg = "是否确定要删除此标准商品？";
                    if(bingingProductCount>0){
                        msg="该标准产品已经绑定了"+bingingProductCount+"个店铺商品，是否要继续删除操作?";
                    }
                    Ext.MessageBox.confirm("信息提示",msg , function (btn) {
                        if (btn == "yes") {
                            me.deleteNormalProduct(grid, row, col);
                        }
                    });
                }
            },

            'productNormalProList #searchButton':{
                click:function (e,t,eOpts) {
                    me.getDataByQueryBuilder();
                }
            },

            'productNormalProList #searchFirstCategory':{
                select:function() {
                    var firstSelect = Ext.getCmp("searchFirstCategory");
                    var secondSelect = Ext.getCmp("searchSecondCategory");
                    secondSelect.clearValue();
                    secondSelect.store.proxy.extraParams = {
                        parentId : firstSelect.getValue()
                    };
                    secondSelect.store.load();
                    //清空后面的
                    Ext.getCmp("searchProductName").setValue("");
                    Ext.getCmp("searchSpec").setValue("");
                    Ext.getCmp("searchModel").setValue("");
                    Ext.getCmp("searchVehicleBrand").setValue("");
                    Ext.getCmp("searchVehicleModel").setValue("");
                    Ext.getCmp("searchCommodityCode").setValue("");
                }
            },

            'productNormalProList #searchSecondCategory':{
                select:function() {
                    //清空后面的
                    Ext.getCmp("searchProductName").setValue("");
                    Ext.getCmp("searchBrand").setValue("");
                    Ext.getCmp("searchSpec").setValue("");
                    Ext.getCmp("searchModel").setValue("");
                    Ext.getCmp("searchVehicleBrand").setValue("");
                    Ext.getCmp("searchVehicleModel").setValue("");
                    Ext.getCmp("searchCommodityCode").setValue("");
                }
            },

            'productNormalProList #searchProductName':{
                keyup:function(e,t,eOpts){
                    me.clearSearchInput('PRODUCT_NAME'),
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                },
                expand:function(e,t,eOpts){
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                },
                focus:function(e,t,eOpts){
                    me.getDataByQueryBuilder('PRODUCT_NAME');
                    Ext.getCmp("searchProductName").getStore().load();
                    e.expand();
                }
            },
            'productNormalProList #searchBrand':{
                keyup:function(e,t,eOpts){
                    me.clearSearchInput('BRAND'),
                    me.getDataByQueryBuilder('BRAND');
                },
                expand:function(e,t,eOpts){
                    me.getDataByQueryBuilder('BRAND');
                },
                focus:function(e,t,eOpts){
                    me.getDataByQueryBuilder('BRAND');
                    Ext.getCmp("searchBrand").getStore().load();
                    e.expand();
                }
            },
            'productNormalProList #searchSpec':{
                keyup:function(e,t,eOpts){
                    //暂时还没有产品分类，先模拟后台
                    me.clearSearchInput('SPEC');
                    me.getDataByQueryBuilder('SPEC');
                },
                expand:function(e,t,eOpts){
                    me.getDataByQueryBuilder('SPEC');
                },
                focus:function(e,t,eOpts){
                    me.getDataByQueryBuilder('SPEC');
                    Ext.getCmp("searchSpec").getStore().load();
                    e.expand();
                }
            },
            'productNormalProList #searchModel':{
                keyup:function(e,t,eOpts){
                    me.clearSearchInput('MODEL');
                    me.getDataByQueryBuilder('MODEL');
                },
                expand:function(e,t,eOpts){
                    me.getDataByQueryBuilder('MODEL');
                },
                focus:function(e,t,eOpts){
                    me.getDataByQueryBuilder('MODEL');
                    Ext.getCmp("searchModel").getStore().load();
                    e.expand();
                }
            },

            'productNormalProList #searchCommodityCode':{
                keyup:function(e,t,eOpts){
                    me.clearSearchInput('COMMODITY_CODE');
                    me.getDataByQueryBuilder('COMMODITY_CODE');
                },
                expand:function(e,t,eOpts){
                    me.getDataByQueryBuilder('COMMODITY_CODE');
                },
                focus:function(e,t,eOpts){
                    me.getDataByQueryBuilder('COMMODITY_CODE');
                    Ext.getCmp("searchCommodityCode").getStore().load();
                    e.expand();
                }
            },
            'productNormalProList #searchVehicleBrand':{
                select:function() {
                    var vehicleBrand = Ext.getCmp("searchVehicleBrand");
                    var vehicleModel = Ext.getCmp("searchVehicleModel");
                    vehicleModel.clearValue();
                    vehicleModel.store.proxy.extraParams = {
                        brandId : vehicleBrand.getValue()
                    };
                    vehicleModel.store.load();
                    //清空后面的
                    Ext.getCmp("searchVehicleModel").setValue("");
                },
                expand:function(e,t,eOpts){
                    var vehicleBrand = Ext.getCmp("searchVehicleBrand");
                    var keyWord = vehicleBrand.getRawValue();
                    vehicleBrand.getStore().proxy.extraParams={
                        keyWord:keyWord
                    }
                },
                focus:function(e,t,eOpts){
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        var vehicleBrand = Ext.getCmp("searchVehicleBrand");
                        var keyWord = vehicleBrand.getRawValue();
                        vehicleBrand.getStore().proxy.extraParams={
                            keyWord:keyWord
                        };
                        vehicleBrand.getStore().load();
                    }

                },
                keyup:function(e,t,eOpts){
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        var vehicleBrand = Ext.getCmp("searchVehicleBrand");
                        var keyWord = vehicleBrand.getRawValue();
                        vehicleBrand.getStore().proxy.extraParams={
                            keyWord:keyWord
                        };
                        vehicleBrand.getStore().load();
                    }
                }
            },
            'productNormalProList #searchVehicleModel':{
                expand:function(e,t,eOpts){
                    var vehicleBrand = Ext.getCmp("searchVehicleBrand");
                    var vehicleModel = Ext.getCmp("searchVehicleModel");
                    var keyWord = vehicleModel.getRawValue();
                    vehicleModel.store.proxy.extraParams = {
                        brandName : vehicleBrand.getValue(),
                        keyWord:keyWord
                    };
                    vehicleModel.getStore().load();
                },
                focus:function(e,t,eOpts){
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        var vehicleBrand = Ext.getCmp("searchVehicleBrand");
                        var vehicleModel = Ext.getCmp("searchVehicleModel");
                        var keyWord = vehicleModel.getRawValue();
                        vehicleModel.store.proxy.extraParams = {
                            brandName : vehicleBrand.getValue(),
                            keyWord:keyWord
                        };
                        vehicleModel.getStore().load();
                    }
                },
                keyup:function(e,t,eOpts){
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        var vehicleBrand = Ext.getCmp("searchVehicleBrand");
                        var vehicleModel = Ext.getCmp("searchVehicleModel");
                        var keyWord = vehicleModel.getRawValue();
                        vehicleModel.store.proxy.extraParams = {
                            brandName : vehicleBrand.getValue(),
                            keyWord:keyWord
                        };
                        vehicleModel.getStore().load();
                    }
                }
            },

            'productNormalProList button[action=search]':{
                click:function () {
                    var firstSelect = Ext.getCmp("searchFirstCategory");
                    var secondSelect = Ext.getCmp("searchSecondCategory");
                    var firstCategoryId = firstSelect.getValue();
                    var secondCategoryId = secondSelect.getValue();
                    var productNameCmp = Ext.getCmp("searchProductName");
                    var productName = Ext.getCmp("searchProductName").getRawValue();
                    var thirdCategoryId = Ext.getCmp("searchProductName").getValue();
                    var brand = Ext.getCmp("searchBrand").getValue();
                    var spec = Ext.getCmp("searchSpec").getValue();
                    var model = Ext.getCmp("searchModel").getValue();
                    var vehicleBrand = Ext.getCmp("searchVehicleBrand").getValue();
                    var vehicleModel = Ext.getCmp("searchVehicleModel").getValue();
                    var commodityCode = Ext.getCmp("searchCommodityCode").getValue();
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
                        commodityCode:commodityCode,
                        firstCategoryId:firstCategoryId,
                        secondCategoryId:secondCategoryId,
                        thirdCategoryId:thirdCategoryId
                    };
                    me.getProductList().store.proxy.extraParams = data; //防止 共用store的层 带入参数
                    me.getProductList().store.loadPage(1);
                }
            },

            'productNormalProList button[action=addNormalProduct]':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_product_normal_add")) {
                        view.hide();
                    }
                },
                click:function () {
                    me.commonUtils.mask();
                    var win = me.getWindowAddProduct();
                    win.down("[name=selectAllBrandModelRadiogroup]").setValue({selectAllBrandModel:true});
                    win.down("[name=vehicleBrandModelInfo]").setValue("所有车型");
                    win.down("[name=vehicleModelIds]").setValue("");
                    win.setChildWin(me.getNormalproductvehiclebrandmodelwindow());
                    win.show();
                }
            },

            'windowAddProduct formProduct button[action=save]':{
                click:me.addProduct
            },

            'windowAddProduct formProduct [name=addFormFirstCategorySelect]':{
                select:function() {
                    var form = me.getAddAndUpdateProductForm();
                    var secondSelect = form.down("[name=addFormSecondCategorySelect]");
                    secondSelect.clearValue();
                    form.down("[name=productCategoryId]").clearValue();
                }
            },

            'windowAddProduct formProduct [name=addFormSecondCategorySelect]':{
                expand:function(e,t,eOpts){
                    var form = me.getAddAndUpdateProductForm();
                    var firstSelect = form.down("[name=addFormFirstCategorySelect]");
                    var secondSelect = form.down("[name=addFormSecondCategorySelect]");
                    secondSelect.store.proxy.extraParams = {
                        parentId : firstSelect.getValue()
                    };
                    secondSelect.store.load();
                },
                select:function() {
                    var form = me.getAddAndUpdateProductForm();
                    form.down("[name=productCategoryId]").clearValue();
                    me.addFormProductSearch();
                }
            },

            'windowAddProduct formProduct [name=productCategoryId]':{
                keyup:function(e,t,eOpts){
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.addFormProductSearch();
                    }
                },
                expand:function(e,t,eOpts){
                    me.addFormProductSearch();
                },
                focus:function(e,t,eOpts){
                    if(!e.isExpanded){
                        e.expand(e,t,eOpts);
                    }else{
                        me.addFormProductSearch();
                    }
                },
                select:function(e,t,eOpts){
                    var form = me.getAddAndUpdateProductForm();
                    form.down("[name=productName]").setValue(e.getValue());
                    var firstSelect = form.down("[name=addFormFirstCategorySelect]");
                    var secondSelect = form.down("[name=addFormSecondCategorySelect]");
                    if(Ext.isEmpty(firstSelect.getValue()) || Ext.isEmpty(secondSelect.getValue())){
                        me.commonUtils.ajax({
                            url:'productManage.do?method=getParentCategoryById',
                            params:{id:e.getValue()},
                            success:function (result) {
                                var secondCategory = result.secondCategory;
                                var firstCategory = result.firstCategory;
                                firstSelect.clearValue();
                                secondSelect.clearValue();
                                firstSelect.getStore().load();
                                firstSelect.setValue(firstCategory.id);
                                secondSelect.getStore().loadData([{name:secondCategory.name,id:secondCategory.id}]);
                                secondSelect.setValue(secondCategory.id);
                            }
                        });
                    }
                }
            },
            'windowAddProduct formProduct [name=commodityCode]':{
              keyup:function(e,t,eOpts){
                    var value = e.getValue().replace(/[—]/g, "-")
                        .replace(/[×]/g, "*")
                        .replace(/(^\s+)|(\s+$)/g, "")
                        .replace(/[^0-9a-zA-Z\+\-\*\/\$\%]/g, "")
                        .toUpperCase();
                    e.setValue(value);
                }
            },

            'windowAddProduct formProduct [name=unit]':{
                keyup:function(e,t,eOpts){
                    var value = e.getValue().replace(" ", "");
                    e.setValue(value);
                }
            },
            'windowAddProduct formProduct [action=modifyVehicleModel]' : {
                "click" : function(){
                    var win = me.getWindowAddProduct();
                    if(win.down("[name=selectAllBrandModel]").getValue()){
                        win.down("[name=vehicleBrandModelInfo]").setValue("");
                        win.down("[name=vehicleModelIds]").setValue("");
                    }
                    win.down("[name=selectAllBrandModelRadiogroup]").setValue({selectAllBrandModel:false});
                    me.showVehicleBrandModel(win);
                }
            },
            'windowAddProduct formProduct [name=selectAllBrandModel]': {
                change: function (radio, newValue, oldValue, eOpts) {
                    if(radio.inputValue && newValue){
                        var win = me.getWindowAddProduct();
                        var treeWin = me.getNormalproductvehiclebrandmodelwindow();
                        if(treeWin.isVisible()){
                            treeWin.close();
                        }
                        win.down("[name=vehicleBrandModelInfo]").setValue("所有车型");
                        win.down("[name=vehicleModelIds]").setValue("");
                    }
                    if(!radio.inputValue && newValue){
                        var win = me.getWindowAddProduct();
                        if(win.down("[name=selectAllBrandModel]").getValue()){
                            win.down("[name=vehicleBrandModelInfo]").setValue("");
                            win.down("[name=vehicleModelIds]").setValue("");
                        }
//                        me.showVehicleBrandModel(win);
                    }
                }
            },
            'showProductWin searchRelevanceShopProduct actioncolumn#relevanceShopProductListGridAction': {
                examineBindingClick: me.examineBindingInGrid,
                cancelBindingClick: me.cancelBindingInGrid
            }
        });
    },
    searchNormalProduct:function () {
        //根据头上的条件查询
    },
    deleteNormalProduct:function (grid, rowIndex, colIndex) {
        var id = grid.getStore().getAt(rowIndex).data.id
        var me = this;
        Ext.getBody().mask('正在删除标准产品....');
        me.commonUtils.ajax({
            url:'productManage.do?method=deleteNormalProduct',
            params:{id:id},
            success:function (result) {
                Ext.getBody().unmask();
                if (result.result == "success") {
                    me.getProductList().getStore().load();
                    Ext.Msg.alert('返回结果', "删除成功");
                }
                else {
                    Ext.Msg.alert('返回结果', result.errorMsg);
                }
            }
        });
    },
    editNormalProduct:function (grid, rowIndex, colIndex) {
        var me = this,
            rec = grid.getStore().getAt(rowIndex),
            win = me.getWindowAddProduct(),
            form = win.down("form");
        var id = rec.id;
        me.commonUtils.mask();
        form.loadRecord(rec);
        me.commonUtils.unmask();
        win.setChildWin(me.getNormalproductvehiclebrandmodelwindow());
        win.show();
        var firstSelect = win.down("[name=addFormFirstCategorySelect]");
        var secondSelect = win.down("[name=addFormSecondCategorySelect]");
        if("PART_MODEL"==rec.data.selectBrandModel){
            form.down("[name=selectAllBrandModelRadiogroup]").setValue({selectAllBrandModel:false});
        }else{
            form.down("[name=selectAllBrandModelRadiogroup]").setValue({selectAllBrandModel:true});
        }
        win.down("[name=vehicleBrandModelInfo]").setValue(rec.data.vehicleBrandModelInfo);

        firstSelect.store.load();

        me.commonUtils.ajax({
            url:'productManage.do?method=getParentCategoryById',
            params:{id:rec.data.productCategoryId},
            success:function (result) {

                var secondCategory = result.secondCategory;
                var firstCategory = result.firstCategory;

                firstSelect.getStore().loadData([{name:firstCategory.name,id:firstCategory.id}]);
                firstSelect.setValue(firstCategory.id);

                secondSelect.getStore().loadData([{name:secondCategory.name,id:secondCategory.id}]);
                secondSelect.setValue(secondCategory.id);

                win.down("[name=productCategoryId]").getStore().loadData([{id:rec.data.productCategoryId,name:rec.data.productName}]);
                win.down("[name=productCategoryId]").setValue(rec.data.productCategoryId);
            }
        });
    },

    showShopProductList:function (rec) {
        var me = this,
            win = me.getShowProductWin();
        me.commonUtils.mask();
        var id = rec.data.id;
        me.getSearchRelevanceShopProduct().store.proxy.extraParams={
            normalProductId : id
        };
        me.getSearchRelevanceShopProduct().store.loadPage(1);
        me.commonUtils.unmask();
        win.expand(true);
    },

    getDataByQueryBuilder:function(inputName){
        var firstSelect = Ext.getCmp("searchFirstCategory");
        var secondSelect = Ext.getCmp("searchSecondCategory");
        var firstCategoryId = firstSelect.getValue();
        var secondCategoryId = secondSelect.getValue();
        var productNameCmp = Ext.getCmp("searchProductName");
        var productName = Ext.getCmp("searchProductName").getRawValue();
        var thirdCategoryId = Ext.getCmp("searchProductName").getValue();
        var commodityCode = Ext.getCmp("searchCommodityCode").getValue();
        var brand = Ext.getCmp("searchBrand").getValue();
        var spec = Ext.getCmp("searchSpec").getValue();
        var model = Ext.getCmp("searchModel").getValue();
        var vehicleBrand = Ext.getCmp("searchVehicleBrand").getValue();
        var vehicleModel = Ext.getCmp("searchVehicleModel").getValue();
        if(thirdCategoryId && isNaN(thirdCategoryId))
        {
            thirdCategoryId = "";
        }
        var data = {
            inputName:inputName,
            productName:productName,
            commodityCode:commodityCode,
            brand:brand,
            spec:spec,
            model:model,
            vehicleBrand:vehicleBrand,
            vehicleModel:vehicleModel,
            firstCategoryId:firstCategoryId,
            secondCategoryId:secondCategoryId,
            thirdCategoryId:thirdCategoryId,
            now:new Date()
        };

        var me = this;
        //这里要判断是哪个框
        if ("PRODUCT_NAME" == inputName) {
            Ext.getCmp("searchProductName").store.proxy.extraParams = data;
        }
        if ("BRAND" == inputName) {
            Ext.getCmp("searchBrand").store.proxy.extraParams = data;
        }
        if ("SPEC" == inputName) {
            Ext.getCmp("searchSpec").store.proxy.extraParams = data;
        }
        if ("MODEL" == inputName) {
            Ext.getCmp("searchModel").store.proxy.extraParams = data;
        }
        if ("COMMODITY_CODE" == inputName) {
            Ext.getCmp("searchCommodityCode").store.proxy.extraParams = data;
        }

    },
    addProduct:function(){
        var me = this,
            form = me.getAddAndUpdateProductForm(),
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
            me.commonUtils.ajax({
                url:'productManage.do?method=saveOrUpdateNormalProduct',
                params:baseForm.getFieldValues(),
                success:function (result) {
                    formEl.unmask();
                    if (result.result == "error") {
                        Ext.Msg.alert("返回结果", result.errorMsg);
                    }
                    else {
                        me.getProductList().store.loadPage(1);
                        Ext.Msg.alert('返回结果', "保存成功");
                        baseForm.reset();
                    }
                },
                failure:function (response) {
                    formEl.unmask();
                }
            });
        }
    },

    clearSearchInput:function(inputName){
        //根据inputName来清空数据
        if("PRODUCT_NAME"==inputName)
        {
            Ext.getCmp("searchBrand").setValue("");
            Ext.getCmp("searchSpec").setValue("");
            Ext.getCmp("searchModel").setValue("");
            Ext.getCmp("searchVehicleBrand").setValue("");
            Ext.getCmp("searchVehicleModel").setValue("");
            Ext.getCmp("searchCommodityCode").setValue("");
        }
        if("BRAND"==inputName)
        {
            Ext.getCmp("searchSpec").setValue("");
            Ext.getCmp("searchModel").setValue("");
            Ext.getCmp("searchVehicleBrand").setValue("");
            Ext.getCmp("searchVehicleModel").setValue("");
            Ext.getCmp("searchCommodityCode").setValue("");
        }
        if("SPEC"==inputName)
        {
            Ext.getCmp("searchModel").setValue("");
            Ext.getCmp("searchVehicleBrand").setValue("");
            Ext.getCmp("searchVehicleModel").setValue("");
            Ext.getCmp("searchCommodityCode").setValue("");
        }
        if("MODEL"==inputName)
        {
            Ext.getCmp("searchVehicleBrand").setValue("");
            Ext.getCmp("searchVehicleModel").setValue("");
            Ext.getCmp("searchCommodityCode").setValue("");
        }
        if("VEHICLE_BRAND"==inputName)
        {
            Ext.getCmp("searchVehicleModel").setValue("");
            Ext.getCmp("searchCommodityCode").setValue("");
        }
        if("VEHICLE_MODEL"==inputName)
        {
            Ext.getCmp("searchCommodityCode").setValue("");
        }
    },

    addFormProductSearch:function(){
        var me = this,
            form = me.getAddAndUpdateProductForm();
        var firstSelect =  form.down("[name=addFormFirstCategorySelect]");
        var secondSelect =  form.down("[name=addFormSecondCategorySelect]");
        var firstCategoryId = firstSelect.getValue();
        var secondCategoryId = secondSelect.getValue();
        var productName =  form.down("[name=productCategoryId]").getRawValue();

        var data = {
            inputName:"PRODUCT_NAME",
            productName:productName,
            firstCategoryId:firstCategoryId,
            secondCategoryId:secondCategoryId
        };
        form.down("[name=productCategoryId]").store.proxy.extraParams = data;
        form.down("[name=productCategoryId]").store.load();
    },
    examineBindingInGrid: function (grid, cell, row, col, e) {
        var rec = grid.getStore().getAt(row),me = this;
        Ext.MessageBox.confirm("信息提示", "复核通过后状态变为已标准，是否确定复核通过？", function (btn) {
            if (btn == "yes") {
                Ext.getBody().mask('正在复核标准化....');
                var id = rec.data.id;
                me.commonUtils.ajax({
                    url:'productManage.do?method=checkRelevance',
                    params:{id:id},
                    success:function (result) {
                        Ext.getBody().unmask();
                        if (result.result == "success") {
                            Ext.Msg.alert('返回结果', "标准化成功");
                            grid.getStore().load();
                            me.getProductList().getStore().load();
                        }
                        else {
                            Ext.Msg.alert('返回结果', result.errorMsg);
                        }
                    }
                });
            }
        });
    },
    cancelBindingInGrid:function(grid, cell, row, col, e){
        var rec = grid.getStore().getAt(row),me = this;
        Ext.MessageBox.confirm("信息提示", "是否确定要取消绑定？", function (btn) {
            if (btn == "yes") {
                Ext.getBody().mask('正在取消绑定....');
                var id = rec.data.id;
                me.commonUtils.ajax({
                    url:'productManage.do?method=deleteRelevance',
                    params:{id:id},
                    success:function (result) {
                        Ext.getBody().unmask();
                        if (result.result == "success") {
                            Ext.Msg.alert('返回结果', "取消成功");
                            grid.getStore().load();
                            me.getProductList().getStore().load();
                        }
                        else {
                            Ext.Msg.alert('返回结果', result.errorMsg);
                        }
                    }
                });
            }
        });
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