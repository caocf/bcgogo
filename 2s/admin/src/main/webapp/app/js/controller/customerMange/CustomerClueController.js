/**
 * 线索客户
 * @author :zhangjuntao
 */
Ext.define('Ext.controller.customerMange.CustomerClueController', {
    extend:'Ext.app.Controller',

    stores:["Ext.store.customerMange.Shops"],

    models:["Ext.model.customerMange.Shop"],

    views:[
        'Ext.view.customerMange.customersClues.CustomerCluesList'
    ],

    requires:[
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common",
        "Ext.view.customerMange.customersClues.ClueWindow",
        'Ext.view.customerMange.existingCustomerManage.CustomerWindow',
        "Ext.view.customerMange.customersClues.RollOutWindow"
    ],

    refs:[
        {
            id:'registerWindow',
            ref:'registerWindow',
            selector:'existingCustomerManageCustomerWindow',
            xtype:'existingCustomerManageCustomerWindow',
            autoCreate:true
        },
        {
            ref:'clueWindow',
            selector:'clueWindow',
            xtype:'clueWindow',
            autoCreate:true
        },
        {
            ref:'rollOutWindow',
            selector:'rollOutWindow',
            xtype:'rollOutWindow',
            autoCreate:true
        },
        {ref:'userSelect', selector:'rollOutWindow userSelect'},
        {ref:'searchRegionSelect', selector:'customerMangeCustomerCluesList regionSelect'},
        {ref:'searchCitySelect', selector:'customerMangeCustomerCluesList citySelect'},
        {ref:'searchProvinceSelect', selector:'customerMangeCustomerCluesList provinceSelect'},
        {ref:'formRegionSelect', selector:'clueForm regionSelect'},
        {ref:'formCitySelect', selector:'clueForm citySelect'},
        {ref:'formProvinceSelect', selector:'clueForm provinceSelect'},
        {ref:'customerMangeCustomerCluesList', selector:'customerMangeCustomerCluesList'},
        //修改营业范围
        {id: 'shopbusinessscopewindow', ref: 'shopbusinessscopewindow', selector: 'shopbusinessscopewindow', xtype: 'shopbusinessscopewindow', autoCreate: true},
        {id: 'shopvehiclebrandmodelwindow', ref: 'shopvehiclebrandmodelwindow', selector: 'shopvehiclebrandmodelwindow', xtype: 'shopvehiclebrandmodelwindow', autoCreate: true}
    ],

    init:function () {
        var me = this;
//        this.registerWindow = Ext.create("Ext.view.customerMange.existingCustomerManage.CustomerWindow");
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            //线索客户-grid panel
            'customerMangeCustomerCluesList':{
                beforerender:function (view) {
                    var clueList = me.getCustomerMangeCustomerCluesList();
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CLUE.ADD")) {
                        clueList.down("[action=add]").hide();
                    }
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CLUE.DELETE")) {
                        clueList.down("[action=delete]").hide();
                    }
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CLUE.EDIT")) {
                        clueList.down("[action=edit]").hide();
                    }
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CLUE.ROLL_OUT")) {
                        clueList.down("[action=rollOut]").hide();
                    }
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CLUE.REGISTER")) {
                        clueList.down("[action=register]").hide();
                    }
                },
                afterrender:function () {
                    var clueList = me.getCustomerMangeCustomerCluesList();
                    clueList.store.proxy.extraParams = {
                        scene:'CLUE'
                    };
                    clueList.store.loadPage(1);
                },
                selectionchange:function (view, records) {
                    var enable = !records.length,
                        clueList = me.getCustomerMangeCustomerCluesList();
                    clueList.down("[action=delete]").setDisabled(enable);
                    clueList.down("[action=edit]").setDisabled(enable);
                    clueList.down("[action=rollOut]").setDisabled(enable);
                    clueList.down("[action=register]").setDisabled(enable);

                }
            },
            //线索客户-grid panel-搜索
            'customerMangeCustomerCluesList button[action=search]':{
                click:me.onSearchClueCustomer
            },
            //线索客户-grid panel-重置
            'customerMangeCustomerCluesList button[action=reset]':{
                click:function () {
                    me.getSearchProvinceSelect().setRawValue("");
                    me.getSearchProvinceSelect().setValue(null);
                    me.getSearchCitySelect().setRawValue("");
                    me.getSearchCitySelect().setValue(null);
                    me.getSearchRegionSelect().setRawValue(null);
                    me.getSearchRegionSelect().setValue(null);
                    var clueList = me.getCustomerMangeCustomerCluesList();
                    clueList.down("[id=shopStatuses]").setValue(null);
                    clueList.down("[name=searchName]").setValue(null);
                    clueList.down("[name=searchContact]").setValue(null);
                    clueList.down("[name=searchFollowName]").setValue(null);
                }
            },
            //线索客户-open window 增加线索客户
            "customerMangeCustomerCluesList button[action=add]":{
                click:function () {
                    var form = me.getClueWindow().down("form");
                    me.getClueWindow().show();
                    me.getClueWindow().setTitle('新增客户线索');
                    form.setFormType("add");
                }
            },
            "customerMangeCustomerCluesList button[action=rollOut]":{
                click:function () {
                    me.getRollOutWindow().show();

                }
            },
            "customerMangeCustomerCluesList button[action=register]":{
                click:function () {
                    var win = me.getRegisterWindow(),
                        rec = me.getCustomerMangeCustomerCluesList().getSelectionModel().getSelection()[0];
                    win.setOperateType("update");
                    win.setTitle('客户信息注册');
                    win.down('[name=managerUserNo]').hide();
                    win.down('[action=save]').setText("注册");
                    win.down('[action=resetPwd]').hide();
                    win.down('[action=changeUserNo]').hide();
                    win.down('[name=agent]').hide();
                    win.down('[action=addProducts]').setDisabled(false);
                    win.showWin(rec);
                }
            },
            //增加待审核客户-save
            "#registerWindow button[action=save]":{
                click:function () {
                    me.getRegisterWindow().saveCustomer(function () {
                        me.onSearchClueCustomer();
                    },"SUBMIT_CLIENT_APPLICATION","CHECK_PENDING")
                }
            },
            "customerMangeCustomerCluesList button[action=delete]":{
                click:function () {
                    var rec = me.getCustomerMangeCustomerCluesList().getSelectionModel().getSelection()[0];
                    me.commonUtils.ajax({
                        url:'shopManage.do?method=updateShopStatus',
                        params:{shopId:rec.get("id"), state:"DELETED"},
                        success:function (result) {
                            if (result.success) {
                                Ext.Msg.alert('返回结果', "删除成功！", function () {
                                    me.getCustomerMangeCustomerCluesList().store.load();
                                });
                            }
                        }
                    });
                }
            },
            //线索客户-open window 编辑线索客户
            "customerMangeCustomerCluesList button[action=edit]":{
                click:function () {
                    var rec = me.getCustomerMangeCustomerCluesList().getSelectionModel().getSelection()[0],
                        form = me.getClueWindow().down("form"),
                        win = me.getClueWindow();
                    form.setFormType("update");
                    win.setTitle('编辑客户线索');
                    me.commonUtils.mask();
                    win.down("form").loadRecord(rec);
                    if (rec.get("otherBusinessScope")) {
                        form.getForm().findField('otherBusinessScope').setValue(true);
                        form.getForm().findField('otherBusinessScopeText').enable();
                        form.getForm().findField('otherBusinessScopeText').setValue(rec.get("otherBusinessScope"));
                    }
                    if (rec.get("majorProduct")) {
                        form.getForm().findField('majorProduct').setValue(true);
                        form.getForm().findField('majorProductText').enable();
                        form.getForm().findField('majorProductText').setValue(rec.get("majorProduct"));
                    }
                    if (rec.get("usedSoftware")) {
                        form.getForm().findField('usedSoftware-yes').setValue(true);
                        form.getForm().findField('softwareTest').enable();
                        form.getForm().findField('softwareTest').setValue(rec.get("usedSoftware"));
                    } else {
                        form.getForm().findField('usedSoftware-no').setValue(true);
                        form.getForm().findField('softwareTest').disable();
                    }
                    if (rec.get("id")) {
                        me.commonUtils.ajax({
                            url: 'shopManage.do?method=getShopDetail',
                            params: {
                                shopId: rec.get("id")
                            },
                            success: function (result) {
                                if (result['success']) {
                                    win.setProductCategoryIds(result['data'][1]);
                                    var scopeWin = win.shopbusinessscopewindow;
                                    scopeWin.drawBusinessScopeTable(scopeWin.getShowBusinessScopeNames(result['data'][2]));
                                }
                            }
                        });
                    }
                    win.show();
                    me.commonUtils.unmask();
                }
            },
            //线索客户-搜索条件
            "customerMangeCustomerCluesList provinceSelect":{
                select:function (combo, records, eOpts) {
                    me.getSearchCitySelect().setRawValue("");
                    me.getSearchCitySelect().setValue(null);
                    me.getSearchRegionSelect().setRawValue(null);
                    me.getSearchRegionSelect().setValue(null);
                    me.getSearchCitySelect().setProvince(records[0]);
                },
                beforequery:function (queryEvent, eOpts) {
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:"1"
                    };
                }
            },
            "customerMangeCustomerCluesList citySelect":{
                select:function (combo, records, eOpts) {
                    me.getSearchRegionSelect().setRawValue(null);
                    me.getSearchRegionSelect().setValue(null);
                    me.getSearchRegionSelect().setCity(records[0]);
                },
                beforequery:function (queryEvent, eOpts) {
                    if (!queryEvent.combo.getProvince()) {
                        return false;
                    }
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:queryEvent.combo.getProvince().get("no")
                    };
                    queryEvent.combo.store.load();
                }
            },
            "customerMangeCustomerCluesList regionSelect":{
                beforequery:function (queryEvent, eOpts) {
                    if (!queryEvent.combo.getCity()) {
                        return false;
                    }
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:queryEvent.combo.getCity().get("no")
                    };
                    queryEvent.combo.store.load();
                }
            },
            //form
            "clueForm provinceSelect":{
                select:function (combo, records, eOpts) {
                    me.getFormCitySelect().setRawValue("");
                    me.getFormCitySelect().setValue(null);
                    me.getFormRegionSelect().setRawValue(null);
                    me.getFormRegionSelect().setValue(null);
                    me.getFormCitySelect().setProvince(records[0]);
                    var addClueWin = this.getClueWindow();
                    addClueWin.down('form').getForm().findField('address').setValue(records[0].get("name"));
                },
                beforequery:function (queryEvent, eOpts) {
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:"1"
                    };
                },
                beforerender:function (combo, eOpts) {
                    var form = me.getClueWindow().down("form");
                    if (form.getFormType() == "update") {
                        combo.store.proxy.extraParams = {
                            parentNo:"1"
                        };
                        combo.store.load();
                    }
                }
            },
            "clueForm citySelect":{
                select:function (combo, records, eOpts) {
                    me.getFormRegionSelect().setRawValue(null);
                    me.getFormRegionSelect().setValue(null);
                    me.getFormRegionSelect().setCity(records[0]);
                    var addClueWin = this.getClueWindow(),
                        form = addClueWin.down('form').getForm();
                    form.findField('address').setValue(addClueWin.down("provinceSelect").getRawValue() + records[0].get("name"));
                },
                beforequery:function (queryEvent, eOpts) {
                    var form = me.getClueWindow().down("form"), parentNo,
                        rec = me.getCustomerMangeCustomerCluesList().getSelectionModel().getSelection()[0];
                    if (!queryEvent.combo.getProvince()) {
                        if (form.getFormType() == "add") {
                            return false;
                        } else {
                            parentNo = rec.get("province");
                            if (!parentNo) {
                                return false;
                            }
                        }
                    } else {
                        parentNo = queryEvent.combo.getProvince().get("no");
                    }
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:parentNo
                    };
                    queryEvent.combo.store.load();
                },
                beforerender:function (combo, eOpts) {
                    var form = me.getClueWindow().down("form");
                    if (form.getFormType() == "update") {
                        if (form.getRecord().get("province")) {
                            combo.store.load({params:{parentNo:form.getRecord().get("province")}});
                        }
                    }
                }
            },
            "clueForm regionSelect":{
                select:function (combo, records, eOpts) {
                    var addClueWin = this.getClueWindow(),
                        form = addClueWin.down('form').getForm();
                    form.findField('address').setValue(addClueWin.down("provinceSelect").getRawValue() + addClueWin.down("citySelect").getRawValue() + records[0].get("name"));
                },
                beforequery:function (queryEvent, eOpts) {
                    var form = me.getClueWindow().down("form"), parentNo,
                        rec = me.getCustomerMangeCustomerCluesList().getSelectionModel().getSelection()[0];
                    if (form.getFormType() == "add") {
                        if (!queryEvent.combo.getCity()) {
                            return false;
                        }
                        parentNo = queryEvent.combo.getCity().get("no");
                    } else {
                        parentNo = rec.get("city");
                        if (!parentNo) {
                            if (!queryEvent.combo.getCity()) return false;
                            else parentNo = queryEvent.combo.getCity().get("no");
                        }
                    }
                    queryEvent.combo.store.proxy.extraParams = {
                        parentNo:parentNo
                    };
                    queryEvent.combo.store.load();
                },
                beforerender:function (combo, eOpts) {
                    var form = me.getClueWindow().down("form");
                    if (form.getFormType() == "update") {
                        if (form.getRecord().get("city")) {
                            combo.store.load({params:{parentNo:form.getRecord().get("city")}});
                        }
                    }
                }
            },
            //增加线索客户-主要产品
            'clueWindow [id=majorProduct]':{
                change:me.toggleMajorProductFields
            },
            //增加线索客户-汽车装潢-其他
            'clueWindow #otherBusinessScope':{
                change:me.toggleOtherBusinessFields
            },
            //增加线索客户-是否使用其他软件
            'clueForm [id=usedSoftware-yes]':{
                change:me.toggleSoftwareFields
            },
            //增加线索客户-save
            "clueWindow button[action=save]":{
                click:me.saveClue
            },
            //转出线索客户
            "rollOutWindow button[action=save]":{
                click:me.rollOutClue
            },
            //注册-修改经营范围
            'clueWindow button[action=showBusinessScope]': {
                click: function () {
                    me.showBusinessScopeAction(me.getClueWindow());
                }
            },
            //注册-修改经营范围
            '#registerWindow button[action=showBusinessScope]': {
                click: function () {
                    me.showBusinessScopeAction(me.getRegisterWindow());
                }
            },
            '#registerWindow [id=partVehicle]': {
                focus: function (radio,newValue) {
                    me.showVehicleBrandModel(me.getRegisterWindow());
                }
            },
            //根据版本隐藏服务范围和代理产品
            '#registerWindow permissionShopVersionSelect': {
                "select": function(combo, records, eOpts){
                    var win = this.getRegisterWindow();
                    Ext.Array.each(records, function(record)
                    {
                        //过滤汽配版本
                        if(record.get('name').contains('WHOLESALER') ) {
                            win.down("[name=serviceCategoryFieldset]").hide();
                            win.down("[name=agentProduct]").hide();
                        } else {
                            win.down("[name=serviceCategoryFieldset]").show();
                            win.down("[name=agentProduct]").show();
                        }
                    });
                }
            },

            '#registerWindow [action=modifyVehicleModel]' : {
                "click" : function(){
                    var win = this.getRegisterWindow();
                    win.down("[id=partVehicle]").setValue(true);
                    win.down("[id=allModel]").setValue(false);
                    win.down("[id=partVehicle]").focus();
                }
            }
        });
    },

    showBusinessScopeAction: function (target) {
        var me = this;
        var productCategoryIds = target.getProductCategoryIds();
        if (!productCategoryIds) {
            Ext.Bcgogo.CustomerMange.ShopBusinessScopeWindow.readUrl = "businessScope.do?method=getBusinessScopeByShopId";
        } else {
            Ext.Bcgogo.CustomerMange.ShopBusinessScopeWindow.readUrl = "businessScope.do?method=getCheckedBusinessScope";
        }
        if (productCategoryIds)
            Ext.Bcgogo.CustomerMange.ShopBusinessScopeWindow.params = productCategoryIds.join(',');
        var win = me.getShopbusinessscopewindow();
        win.setBusinessScopeTarget(target);
        win.show();
    },

    rollOutClue:function () {
        var me = this, select = me.getUserSelect(),
            rec = me.getCustomerMangeCustomerCluesList().getSelectionModel().getSelection()[0],
            id = rec.get("id"),
            userId = select.getValue();
        if (userId && id) {
            this.commonUtils.ajax({
                url:'shopManage.do?method=rollOutClue',
                params:{
                    id:id,
                    userId:userId
                },
                success:function (result) {
                    Ext.Msg.alert('提醒', "操作成功！", function () {
                        me.getCustomerMangeCustomerCluesList().store.load();
                        me.getRollOutWindow().close();
                    });
                }
            });
        }
    },

    saveClue:function () {
        var me = this, win = me.getClueWindow(),
            form = win.down("form"),
            formEl = form.getEl(),
            baseForm = form.form;
        if (!win.getProductCategoryIds() || win.getProductCategoryIds().length == 0) {
            Ext.MessageBox.show({
                title: '提示',
                msg: "经营产品不能为空！",
                icon: Ext.MessageBox.INFO,
                buttons: Ext.Msg.OK
            });
            return;
        }
        if (baseForm.isValid()) {
            formEl.mask('正在保存 . . .');
            var params = baseForm.getValues();
            params['productCategoryIds'] = win.getProductCategoryIds();
            me.commonUtils.ajax({
                url:'shopManage.do?method=saveShop',
                params:params,
                success:function (result) {
                    if (result['duplicate']) {
                        Ext.Msg.alert('返回结果', "店铺名称重复！", function () {
                            formEl.unmask();
                        });
                    } else {
                        me.getCustomerMangeCustomerCluesList().store.load();
                        Ext.Msg.alert('返回结果', "保存成功！", function () {
//                            baseForm.reset();
                            formEl.unmask();
                            win.close();
                        });
                    }
                },
                failure:function (response) {
                    formEl.unmask();
                }
            });
        }
    },

    toggleOtherBusinessFields:function (checkbox, newValue, oldValue) {
        var addClueWin = this.getClueWindow(),
            form = addClueWin.down('form').getForm(),
            otherField = form.findField('otherBusinessScopeText');
        if (newValue) {
            otherField.enable();
        } else {
            form.findField('otherBusinessScopeText').setValue(null);
            otherField.disable();
        }
    },

    toggleSoftwareFields:function (checkbox, newValue, oldValue) {
        var addClueWin = this.getClueWindow(),
            form = addClueWin.down('form').getForm(),
            softwareField = form.findField('softwareTest');
        if (newValue) {
            softwareField.enable();
        } else {
            form.findField('softwareTest').setValue(null);
            softwareField.disable();
        }
    },

    toggleMajorProductFields:function (checkbox, newValue, oldValue) {
        var addClueWin = this.getClueWindow(),
            form = addClueWin.down('form').getForm(),
            majorProductField = form.findField('majorProductText');
        if (newValue) { // if the "majorProduct" checkbox was checked
            majorProductField.enable();
        } else { // if the "majorProduct" checkbox was unchecked
            form.findField('majorProductText').setValue(null);
            majorProductField.disable();
        }
    },

    onSearchClueCustomer:function () {
        var me = this, params,
            clueList = me.getCustomerMangeCustomerCluesList(),
            province = clueList.down("[name=province]").getValue(),
            city = clueList.down("[name=city]").getValue(),
            region = clueList.down("[name=region]").getValue(),
            name = clueList.down("[name=searchName]").getValue(),
            contact = clueList.down("[name=searchContact]").getValue(),
            followName = clueList.down("[name=searchFollowName]").getValue(),
            shopStatuses = clueList.down("[id=shopStatuses]").getValue().shopStatuses;
        params = {
            region:region,
            city:city,
            province:province,
            name:name,
            followName:followName,
            contact:contact,
            scene:'CLUE'
        };
        if (shopStatuses) {
            params['shopStatuses'] = shopStatuses;
        }
//        clueList.store.load({params:params});
        clueList.store.proxy.extraParams = params;
        clueList.store.loadPage(1);
    },
    //主营车型
    showVehicleBrandModel: function(target){
        var me = this;
        var vehicleModelIds = target.getVehicleModelIds();
        if (!vehicleModelIds) {
            Ext.Bcgogo.ShopVehicleBrandModelWindow.readUrl = "businessScope.do?method=getShopVehicleBrandModelByShopId";
        } else {
            Ext.Bcgogo.ShopVehicleBrandModelWindow.readUrl = "businessScope.do?method=getCheckedVehicleBrandModel";
        }
        if (vehicleModelIds)
            Ext.Bcgogo.ShopVehicleBrandModelWindow.params = vehicleModelIds.join(',');
        var win = me.getShopvehiclebrandmodelwindow();
        win.setOpenTarget(target);

        if (win.down('treepanel')) {
            var rootTree = win.down('treepanel').getRootNode();
            me.commonUtils.ajax({
                url: Ext.Bcgogo.ShopVehicleBrandModelWindow.readUrl,
                params: {
                    ids: Ext.Bcgogo.ShopVehicleBrandModelWindow.params
                },
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