/**
 * 现有客户
 * @author :zhangjuntao
 */
Ext.define('Ext.controller.customerMange.CustomerManagerController', {
    extend: 'Ext.app.Controller',

    stores: [
        "Ext.store.customerMange.Shops",
        "Ext.store.customerMange.RecommendShopStore"],

    models: [
        "Ext.model.customerMange.Shop",
        "Ext.model.customerMange.RecommendShop"
    ],

    views: [
        'Ext.view.customerMange.existingCustomerManage.View'
    ],

    requires: [
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common",
        'Ext.view.customerMange.existingCustomerManage.CustomerWindow',
        'Ext.view.customerMange.existingCustomerManage.AuditShopWindow',
        'Ext.view.customerMange.existingCustomerManage.ExtensionShopWindow',
        'Ext.view.customerMange.existingCustomerManage.ExtensionShopListWindow',
        'Ext.view.customerMange.ShopBusinessScopeWindow',
        'Ext.view.customerMange.ShopProductWin',
        'Ext.view.customerMange.existingCustomerManage.OperateView',
        'Ext.view.customerMange.ShopVehicleBrandModelWindow',
        "Ext.view.customerMange.AddressCoordinateWindow",
        "Ext.view.customerMange.ShopAdAreaScope",
        "Ext.view.customerMange.ShopRecommendScope",
        "Ext.view.customerMange.RecommendTreeMenu",
        "Ext.view.customerMange.RecommendTreeImgUpload"
    ],

    refs: [
        //延期form window
        {id: 'shopExtensionShopWindow', ref: 'shopExtensionShopWindow', selector: 'shopExtensionShopWindow', xtype: 'shopExtensionShopWindow', autoCreate: true},
        //延期List
        {id: 'shopExtensionShopListWindow', ref: 'shopExtensionShopListWindow', selector: 'shopExtensionShopListWindow', xtype: 'shopExtensionShopListWindow', autoCreate: true},
        //注册账户编辑
        {id: 'customerWindow', ref: 'customerWindow', selector: 'existingCustomerManageCustomerWindow', xtype: 'existingCustomerManageCustomerWindow', autoCreate: true},
        //试用客户编辑
        {id: 'trialCustomerWindow', ref: 'trialCustomerWindow', selector: 'existingCustomerManageRegisteredCustomerWindow', xtype: 'existingCustomerManageCustomerWindow', autoCreate: true},
        //线索客户 - 注册
        {id: 'registeredCustomerWindow', ref: 'registeredCustomerWindow', selector: 'existingCustomerManageRegisteredCustomerWindow', xtype: 'existingCustomerManageCustomerWindow', autoCreate: true},
        //地址修改
        {id: 'auditAddressCoordinateWindow', ref: 'auditAddressCoordinateWindow', selector: 'auditAddressCoordinateWindow', xtype: 'addresscoordinatewindow', autoCreate: true},
        {id: 'trialAddressCoordinateWindow', ref: 'trialAddressCoordinateWindow', selector: 'trialAddressCoordinateWindow', xtype: 'addresscoordinatewindow', autoCreate: true},
        {id: 'registeredAddressCoordinateWindow', ref: 'registeredAddressCoordinateWindow', selector: 'registeredAddressCoordinateWindow', xtype: 'addresscoordinatewindow', autoCreate: true},

//        //修改营业范围
        {id: 'shopbusinessscopewindow', ref: 'shopbusinessscopewindow', selector: 'shopbusinessscopewindow', xtype: 'shopbusinessscopewindow', autoCreate: true},
//      //修改主营车型
        {id: 'shopvehiclebrandmodelwindow', ref: 'shopvehiclebrandmodelwindow', selector: 'shopvehiclebrandmodelwindow', xtype: 'shopvehiclebrandmodelwindow', autoCreate: true},
//       广告区域
        {id: 'shopadareascope', ref: 'shopadareascope', selector: 'shopadareascope', xtype: 'shopadareascope', autoCreate: true},
//       广告类目
        {id: 'shoprecommendscope', ref: 'shoprecommendscope', selector: 'shoprecommendscope', xtype: 'shoprecommendscope', autoCreate: true},
        //广告类目的右键菜单
        {id: 'recommendTreeMenu', ref: 'recommendTreeMenu', selector: 'recommendTreeMenu', xtype: 'recommendTreeMenu', autoCreate: true},
//广告类目上传图片
        {id: 'RecommendTreeImgUpload', ref: 'RecommendTreeImgUpload', selector: 'RecommendTreeImgUpload', xtype: 'RecommendTreeImgUpload', autoCreate: true},
//         //店铺产品
//        {id: 'shopproductwin', ref: 'shopproductwin', selector: 'shopproductwin', xtype: 'shopproductwin', autoCreate: true},

        {ref: 'operateView', selector: 'existingCustomerManageOperateView', xtype: 'existingCustomerManageOperateView', autoCreate: true},
        //审核
        {id: 'auditShopWindow', ref: 'auditShopWindow', selector: 'auditShopWindow', xtype: 'auditShopWindow', autoCreate: true},
        //待审核
        {ref: 'customerMangeOperateHistoryList', selector: 'existingCustomerManageOperateView customerMangeOperateHistoryList'},

        {ref: 'existingCustomerManageOperatorForm', selector: 'existingCustomerManageOperateView existingCustomerManageOperatorForm'},
        //总view
        {ref: 'existingCustomerManageView', selector: 'existingCustomerManageView'},
        //待审核客户
        {ref: 'existingCustomerManageCheckPendingList', selector: 'existingCustomerManageCheckPendingList'},
        //试用客户
        {ref: 'shopRegisteredTrialList', selector: 'shopRegisteredTrialList'},
        //已注册
        {ref: 'existingCustomerManageRegisteredList', selector: 'existingCustomerManageRegisteredList'}

    ],

    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            //待审核 试用 正式
            'existingCustomerManageView': {
                beforerender: function (view, eOpts) {
                    var hasOneTab = false;
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED.LIST")) {
                        view.remove(view.down("existingCustomerManageRegisteredList"));

                    } else {
                        hasOneTab = true;
                    }
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED_TRIAL.LIST")) {
                        view.remove(view.down("shopRegisteredTrialList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CHECK_PENDING.LIST")) {
                        view.remove(view.down("existingCustomerManageCheckPendingList"));
                    } else {
                        hasOneTab = true;
                    }
                    if (!hasOneTab) {
                        alert("权限配置异常!");
                    }
                }
            },
            //待审核客户-grid panel
            'existingCustomerManageCheckPendingList': {
                beforerender: function (view) {
                    var list = me.getExistingCustomerManageCheckPendingList();
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CHECK_PENDING.ADD")) {
                        list.down("[action=add]").hide();
                    }
                },
                afterrender: function () {
                    me.getExistingCustomerManageCheckPendingList().onSearch();
                },
                selectionchange: function (view, records) {
                    var enable = !records.length, win = me.getExistingCustomerManageCheckPendingList();
                    var rec = win.getSelectionModel().getSelection()[0];
                    win.down('button[action=show]').setDisabled(enable);
                    win.down('button[action=register]').setDisabled(enable);
                    win.down('button[action=locate]').setDisabled(enable);
//                    win.down('button[action=register]').setDisabled(enable||rec.get("shopStatus")=="CHECK_PENDING_REJECTED");
                }
            },
            'existingCustomerManageCheckPendingList button[action=locate]': {
                click: me.showAuditAddressCoordinateWindow
            },
            //待审核客户-open window 编辑待审核客户
            'existingCustomerManageCheckPendingList button[action=register]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CHECK_PENDING.VERIFY")) {
                        view.hide();
                    }
                },
                click: function () {
                    var win = me.getAuditShopWindow(),
                        form = win.down("form"),
                        rec = me.getExistingCustomerManageCheckPendingList().getSelectionModel().getSelection()[0];
                    if(rec.get("shopStatus")=="CHECK_PENDING_REJECTED"){
                        win.down('[itemId=auditShopArea]').hide();
                        win.down('[action=save]').hide();
                    } else if(rec.get("shopStatus")=="CHECK_PENDING"){
                        win.down('[itemId=auditInfo]').hide();
                    }
                    win.down("[name=shopVersionId]").store.load();
                    win.showAuditShopDetail(rec);
                }
            },
            'existingCustomerManageCheckPendingList button[action=add]': {
                click: function () {
                    var win = me.getCustomerWindow();
                    win.setOperateType("add");
                    win.down('[name=managerUserNo]').hide();
                    win.down('[action=resetPwd]').hide();
                    win.down('[action=changeUserNo]').hide();
                    win.down('[action=save]').setText("注册");
                    win.down('[name=agent]').hide();
                    win.down('[action=addProducts]').setDisabled(false);
                    win.down('[name=softPriceStr]').hide();
                    win.down("[name=auditInfo]").hide();
                    win.down('[id=softPriceId]').name = 'softPrice2';
                    win.setTitle('客户信息新增');
                    win.showWin(null);
                }
            },
            //审核
            'auditShopWindow button[action=save]': {
                click: function () {
                    me.getAuditShopWindow().audit(function () {
                        me.getExistingCustomerManageCheckPendingList().onSearch();
                        me.getAuditShopWindow().close();
                    });
                }
            },

            //主营车型
            '#customerWindow [id=partVehicle]': {
                focus: function (radio,newValue) {
                    me.showVehicleBrandModel(me.getCustomerWindow());
                }
            },
            '#trialCustomerWindow [id=partVehicle]': {
                focus: function (radio,newValue) {
                        me.showVehicleBrandModel(me.getTrialCustomerWindow());
                }
            },
            '#registeredCustomerWindow [id=partVehicle]': {
                focus: function (radio,newValue) {
                        me.showVehicleBrandModel(me.getRegisteredCustomerWindow());
                }
            },
              //根据版本隐藏服务范围和代理产品
            '#customerWindow permissionShopVersionSelect': {
                "select": function(combo, records, eOpts){
                    var win = this.getCustomerWindow();
                    Ext.Array.each(records, function(record)
                    {
                        //过滤汽配版本
                       if(record.get('name').contains('WHOLESALER') ) {
                            win.down("[name=serviceCategoryFieldset]").hide();
                            win.down("[name=agentProduct]").hide();
                            win.down("[name=shopVersionStr]").setValue("WHOLESALER");
                       } else {
                           win.down("[name=serviceCategoryFieldset]").show();
                           win.down("[name=agentProduct]").show();
                       }
                    });
                }
            },

            '#customerWindow [action=modifyVehicleModel]' : {
               "click" : function(){
                   var win = this.getCustomerWindow();
                   win.down("[id=partVehicle]").setValue(true);
                   win.down("[id=allModel]").setValue(false);
                   win.down("[id=partVehicle]").focus();
               }
            },

            '#trialCustomerWindow [action=modifyVehicleModel]' : {
                "click" : function(){
                    var win = this.getTrialCustomerWindow();
                    win.down("[id=partVehicle]").setValue(true);
                    win.down("[id=allModel]").setValue(false);
                    win.down("[id=partVehicle]").focus();
                }
            },

            '#registeredCustomerWindow [action=modifyVehicleModel]' : {
                "click" : function(){
                    var win = this.getRegisteredCustomerWindow();
                    win.down("[id=partVehicle]").setValue(true);
                    win.down("[id=allModel]").setValue(false);
                    win.down("[id=partVehicle]").focus();
                }
            },

            //待审核客户-open window 编辑待审核客户
            'existingCustomerManageCheckPendingList button[action=register]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CHECK_PENDING.VERIFY")) {
                        view.hide();
                    }
                },
                click: function () {
                    var win = me.getAuditShopWindow(),
                        form = win.down("form"),
                        rec = me.getExistingCustomerManageCheckPendingList().getSelectionModel().getSelection()[0];
                    if(rec.get("shopStatus")=="CHECK_PENDING_REJECTED"){
                        win.down('[itemId=auditShopArea]').hide();
                        win.down('[action=save]').hide();
                    } else if(rec.get("shopStatus")=="CHECK_PENDING"){
                        win.down('[itemId=auditInfo]').hide();
                    }
                    win.down("[name=shopVersionId]").store.load();
                    win.down('permissionShopVersionSelect').setReadOnly(true);
                    win.showAuditShopDetail(rec);
                }
            },
            //修改经营范围
            '#customerWindow button[action=showBusinessScope]': {
                click: function () {
                    me.showBusinessScopeAction(me.getCustomerWindow());
                }
            },
            //修改经营范围
            '#trialCustomerWindow button[action=showBusinessScope]': {
                click: function () {
                    me.showBusinessScopeAction(me.getTrialCustomerWindow());
                }
            },
            //修改经营范围
            '#registeredCustomerWindow button[action=showBusinessScope]': {
                click: function () {
                    me.showBusinessScopeAction(me.getRegisteredCustomerWindow());
                }
            },
            //========================试用========================
            'shopRegisteredTrialList': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED_TRIAL.EXTENSION")) {
                        view.down("button[action=extension]").hide();
                    }
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CUSTOMER.EDIT")) {
                        view.down("button[action=edit]").hide();
                    }
                },
                afterrender: function () {
                    me.getShopRegisteredTrialList().onSearch();
                },
                selectionchange: function (view, records) {
                    var enable = !records.length, win = me.getShopRegisteredTrialList();
                    var rec = me.getShopRegisteredTrialList().getSelectionModel().getSelection()[0];

                    win.down('button[action=edit]').setDisabled(enable);
                    win.down('button[action=extension]').setDisabled(enable);
                    win.down('button[action=locate]').setDisabled(enable);
                    if(rec) {
                        if(rec.get("chargeType") == 'YEARLY') {
                            win.down('button[action=auditBargain]').setDisabled(true);
                            win.down('button[action=applyBargain]').setDisabled(true);
                        }
                    }
                    if(rec) {
                        if(rec.get("chargeType") == 'YEARLY') {
                            win.down('button[action=auditBargain]').setDisabled(true);
                            win.down('button[action=applyBargain]').setDisabled(true);
                        }
                    }
                }
            },
            'shopRegisteredTrialList button[action=locate]': {
                click: me.showTrialAddressCoordinateWindow
            },
            'shopRegisteredTrialList button[action=edit]': {
                click: function () {
                    var win = me.getTrialCustomerWindow();
                    win.setOperateType("update");
                    win.setTitle('客户信息编辑');
                    win.down('[name=softPrice]').setReadOnly(true);
                    win.down("permissionShopVersionSelect").setReadOnly(true);
                    win.down('[id=softPriceRadio]').hide();
                    win.down('[id=softPriceInput]').name = 'softPrice2';
                    win.showWin(me.getShopRegisteredTrialList().getSelectionModel().getSelection()[0]);
                }
            },
            'shopRegisteredTrialList button[action=extension]': {
                click: function () {
                    var rec = me.getShopRegisteredTrialList().getSelectionModel().getSelection()[0];
                    me.commonUtils.ajax({
                        url: 'shopExtension.do?method=hasShopExtensionLogs',
                        params: {shopId: rec.get("id")},
                        success: function (result) {
                            if (result) {
                                me.getShopExtensionShopListWindow().showWin(rec);
                            } else {
                                me.getShopExtensionShopWindow().showWin(rec);
                            }
                        }
                    });
                }
            },
            //已注册客户-save
            "#trialCustomerWindow button[action=save]": {
                click: function () {
                    me.getTrialCustomerWindow().saveCustomer(function () {
                        me.getShopRegisteredTrialList().onSearch();
                    })
                }
            },
            //试用客户-resetPwd
            "#trialCustomerWindow button[action=resetPwd]": {
                click: function () {
                    me.getTrialCustomerWindow().resetPwd();
                }
            },
            "#trialCustomerWindow button[action=changeUserNo]": {
                click: function () {
                    me.getTrialCustomerWindow().changeUserNo();
                }
            },
            "#shopExtensionShopListWindow button[action=extensionAgain]": {
                click: function () {
                    var rec = me.getShopRegisteredTrialList().getSelectionModel().getSelection()[0];
                    me.getShopExtensionShopListWindow().close();
                    me.getShopExtensionShopWindow().showWin(rec);
                }
            },

            //延期-win
            "#shopExtensionShopWindow button[action=save]": {
                click: function () {
                    me.getShopExtensionShopWindow().down('form').save(function () {
                        me.getShopRegisteredTrialList().onSearch();
                        me.getShopExtensionShopWindow().close();
                    });
                }
            },


            //=============已注册客户 -grid panel-搜索=============
            'existingCustomerManageRegisteredList': {
//                beforerender: function (view, eOpts) {
//                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CUSTOMER.EDIT")) {
//                        view.down("button[action=edit]").hide();
//                    }
//                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CUSTOMER.EDIT")) {
//                        view.down("button[action=edit]").hide();
//                    }
//                },
                selectionchange: function (view, records) {
                    var enable = !records.length, win = me.getExistingCustomerManageRegisteredList(),
                        rec=records[0];
                    win.down('button[action=edit]').setDisabled(enable);
                    win.down('button[action=locate]').setDisabled(enable);
                    win.down('button[action=disable]').setDisabled(enable || rec.get("shopState") == "IN_ACTIVE");
                    win.down('button[action=enable]').setDisabled(enable || rec.get("shopState") != "IN_ACTIVE");
                },
                afterrender: function () {
                    me.getExistingCustomerManageRegisteredList().onSearch();
                }
            },
            'existingCustomerManageRegisteredList button[action=locate]': {
                click: me.showRegisteredAddressCoordinateWindow
            },
            //编辑
            'existingCustomerManageRegisteredList button[action=edit]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.CUSTOMER.EDIT")) {
                        view.hide();
                    }
                },
                click: function () {
                    var win = me.getRegisteredCustomerWindow();
                    var rec = me.getExistingCustomerManageRegisteredList().getSelectionModel().getSelection()[0];
                    win.setOperateType("update");
                    win.down('[name=softPrice]').setReadOnly(true);
                    win.setTitle('客户信息编辑');
                    win.down('[id=softPriceRadio]').hide();
                    win.down('[id=softPriceInput]').name = 'softPrice2';
                    win.showWin(rec);
                    win.down("permissionShopVersionSelect").setReadOnly(true);
                }
            },
            //禁用 启用
            'existingCustomerManageRegisteredList button[action=enable]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED.DISABLE")) {
                        view.hide();
                    }
                },
                click: function () {
                    me.enableOrDisableRegisteredShop();
                }
            },
            'existingCustomerManageRegisteredList button[action=disable]': {
                beforerender: function (view) {
                    if (!me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED.DISABLE")) {
                        view.hide();
                    }
                },
                click: function () {
                    me.enableOrDisableRegisteredShop();
                }
            },
            //禁用
            'existingCustomerManageOperateView existingCustomerManageOperatorForm button[action=disable]': {
                click: function () {
                    me.commonUtils.mask();
                    var form = me.getExistingCustomerManageOperatorForm().form,
                        shopId = me.getOperateView().getShopId(),
                        shopOperateHistory = {
                            operateShopId: shopId,
                            reason: form.getFieldValues()["reason"],
                            operateType: 'DISABLE_REGISTERED_PAID_SHOP'
                        };
                    me.disableShop(shopOperateHistory);
                }
            },
            //启用
            'existingCustomerManageOperateView existingCustomerManageOperatorForm button[action=enable]': {
                click: function () {
                    var form = me.getExistingCustomerManageOperatorForm().form,
                        shopId = me.getOperateView().getShopId(),
                        shopOperateHistory = {
                            operateShopId: shopId,
                            reason: form.getFieldValues()["reason"],
                            operateType: 'ENABLE_REGISTERED_PAID_SHOP'
                        };
                    me.disableShop(shopOperateHistory);
                }
            },
            //升级
            'existingCustomerManageOperateView existingCustomerManageOperatorForm button[action=update]': {
                click: function () {
                    var form = me.getExistingCustomerManageOperatorForm().form,
                        shopId = me.getOperateView().getShopId();
                    var shopOperateHistory = {
                        operateShopId: shopId,
                        reason: form.getFieldValues()["reason"],
                        operateType: 'UPDATE_REGISTERED_TRIAL_SHOP'
                    };
                    me.disableShop(shopOperateHistory);
                }
            },
            //试用
            'existingCustomerManageOperateView existingCustomerManageOperatorForm button[action=trial]': {
                click: function () {
                    var form = me.getExistingCustomerManageOperatorForm().form,
                        trialTime = form.getFieldValues()["trialTime"],
                        shopId = me.getOperateView().getShopId(),
                        trialStartTime = me.getOperateView().getTrialStartTime();
                    if (trialTime) {
                        var shopOperateHistory = {
                            operateShopId: shopId,
                            reason: form.getFieldValues()["reason"],
                            operateType: 'CONTINUE_TO_TRY',
                            trialStartTime: trialStartTime,
                            trialEndTime: trialTime.getTime()
                        };
                        me.disableShop(shopOperateHistory);
                    } else {
                        Ext.Msg.alert('警告', "请填写试用期限！");
                    }
                }
            },
            //增加待审核客户-save
            "#customerWindow button[action=save]": {
                click: function (combo) {
                    me.getCustomerWindow().saveCustomer(function () {
                        me.getExistingCustomerManageCheckPendingList().onSearch();
                    }, "SUBMIT_CLIENT_APPLICATION");
                }
            },
            //已注册客户-save
            "#registeredCustomerWindow button[action=save]": {
                click: function () {
                    me.getRegisteredCustomerWindow().saveCustomer(function () {
                        me.getExistingCustomerManageRegisteredList().onSearch();
                    })
                }
            },
            //已注册客户-resetPwd
            "#registeredCustomerWindow button[action=resetPwd]": {
                click: function () {
                    me.getRegisteredCustomerWindow().resetPwd();
                }
            }  ,
            "#registeredCustomerWindow button[action=changeUserNo]": {
                click: function () {
                    me.getRegisteredCustomerWindow().changeUserNo();
                }
            }  ,
            //地址更新
            "#auditAddressCoordinateWindow button[action=save]": {
                click: function () {
                    me.getAuditAddressCoordinateWindow().updateAddressCoordinate(function () {
                        me.getExistingCustomerManageCheckPendingList().onSearch();
                    })
                }
            },
            "#trialAddressCoordinateWindow button[action=save]": {
                click: function () {
                    me.getTrialAddressCoordinateWindow().updateAddressCoordinate(function () {
                        me.getShopRegisteredTrialList().onSearch();
                    })
                }
            },
            "#registeredAddressCoordinateWindow button[action=save]": {
                click: function () {
                    me.getRegisteredAddressCoordinateWindow().updateAddressCoordinate(function () {
                        me.getExistingCustomerManageRegisteredList().onSearch();
                    })
                }
            },


            '#trialCustomerWindow [id=partAdArea]': {
                focus: function (radio,newValue) {
                    me.showAdArea(me.getTrialCustomerWindow());
                }
            },
            '#trialCustomerWindow [action=selectAdArea]' : {
                "click" : function(){
                    var win = this.getTrialCustomerWindow();
                    win.down("[id=partAdArea]").setValue(true);
                    win.down("[id=allAdArea]").setValue(false);
                    win.down("[id=partAdArea]").focus();
                }
            },
            '#customerWindow [id=partAdArea]': {
                focus: function (radio,newValue) {
                    me.showAdArea(me.getCustomerWindow());
                }
            },
            '#customerWindow [action=selectAdArea]' : {
                "click" : function(){
                    var win = this.getCustomerWindow();
                    win.down("[id=partAdArea]").setValue(true);
                    win.down("[id=allAdArea]").setValue(false);
                    win.down("[id=partAdArea]").focus();
                }
            },
            '#registeredCustomerWindow [id=partAdArea]': {
                focus: function (radio,newValue) {
                    me.showAdArea(me.getRegisteredCustomerWindow());
                }
            },
            '#registeredCustomerWindow [action=selectAdArea]' : {
                "click" : function(){
                    var win = this.getRegisteredCustomerWindow();
                    win.down("[id=partAdArea]").setValue(true);
                    win.down("[id=allAdArea]").setValue(false);
                    win.down("[id=partAdArea]").focus();
                }
            },
            //试用客户
            '#trialCustomerWindow [action=selectShopRecommend]' : {
                "click" : function(){
                    me.showShopRecommend(me.getTrialCustomerWindow(),
                        me.getShopRegisteredTrialList().getSelectionModel().getSelection()[0]);
                }
            },
//            '#customerWindow [action=selectShopRecommend]' : {
//                "click" : function(){
//                    me.showShopRecommend(me.getCustomerWindow());
//                }
//            },
            //正式客户
            '#registeredCustomerWindow [action=selectShopRecommend]' : {
                "click" : function(){
                    me.showShopRecommend(me.getRegisteredCustomerWindow(),
                        me.getExistingCustomerManageRegisteredList().getSelectionModel().getSelection()[0]);
                }
            },
            //广告类目右键菜单
            'shoprecommendscope treepanel':{
                itemcontextmenu:me.treeMenu,
                edit:me.updateCommendShop

            },
            'recommendTreeMenu [id=addFirstRecommendCategory]':{
                click:function(component, e){
                    var recordData = component.ownerCt.getRecordData();
                    var newModule = Ext.create('Ext.model.customerMange.RecommendShop', {
                        text:"新分类",
                        leaf:false,
                        checked:false,
                        expand:true,
                        type:"RECOMMEND_SHOP",
                        parentId:recordData.get("parentId"),
                        loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
                    });

                    var listTree = me.getShoprecommendscope().down("treepanel"),
                        cellEditingPlugin = listTree.editingPlugin,
                        selectionModel = listTree.getSelectionModel(),
                        selectedList = selectionModel.getSelection()[0],
                    parentList = listTree.getRootNode();
                    parentList.appendChild(newModule);
                    selectionModel.select(newModule);
                    cellEditingPlugin.startEdit(newModule, 0);

                }
            },
            'recommendTreeMenu [id=addSecondRecommendCategory]':{
                click:function(component, e){
                    var recordData = component.ownerCt.getRecordData();
                    var newModule = Ext.create('Ext.model.customerMange.RecommendShop', {
                        text:"新分类",
                        leaf:true,
                        checked:false,
                        expand:true,
                        type:"RECOMMEND_SHOP",
                        parentId:recordData.get("id"),
                        loaded:true // set loaded to true, so the tree won't try to dynamically load children for this node when expanded
                    });

                    var listTree = me.getShoprecommendscope().down("treepanel"),
                        cellEditingPlugin = listTree.editingPlugin,
                        selectionModel = listTree.getSelectionModel(),
                        selectedList = selectionModel.getSelection()[0],
                        parentList = selectedList,
                        expandAndEdit = function () {
                            if (parentList.isExpanded()) {
                                selectionModel.select(newModule);
                                cellEditingPlugin.startEdit(newModule, 0);
                            } else {
                                listTree.on('afteritemexpand', function startEdit(list) {
                                    if (list === parentList) {
                                        selectionModel.select(newModule);
                                        cellEditingPlugin.startEdit(newModule, 0);
                                        // remove the afterexpand event listener
                                        listTree.un('afteritemexpand', startEdit);
                                    }
                                });
                                parentList.expand();
                            }
                        };

                    parentList.appendChild(newModule);
                    if (listTree.getView().isVisible(true)) {
                        expandAndEdit();
                    } else {
                        listTree.on('expand', function onExpand() {
                            expandAndEdit();
                            listTree.un('expand', onExpand);
                        });
                        listTree.expand();
                    }
                }
            },
            'recommendTreeMenu [id=deleteFirstRecommendCategory]':{
                click:function(component, e){
                    var me = this;
                    var recordData = component.ownerCt.getRecordData();
                    var listTree = me.getShoprecommendscope().down("treepanel");
                    var selectionModel = listTree.getSelectionModel().getSelection()[0];
                    var nodeId = recordData.get("id");
                    if (selectionModel.hasChildNodes()) {
                        Ext.Msg.alert('警告', "请先删除子节点！");
                    } else {
                        Ext.MessageBox.confirm('确认', '确认删除?', function (btn) {
                            if (btn == "yes") {
                                me.commonUtils.ajax({
                                    url:'shopAd.do?method=deleteShopRecommend',
                                    params:{id:nodeId},
                                    success:function (result) {
                                        if(result && result.success){
                                            listTree.getStore().getNodeById(nodeId).remove();
                                        }else{
                                            Ext.Msg.alert('警告', result.msg);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            },
            'recommendTreeMenu [id=addRecommendImg]':{
                click:function(component, e){
                    var me = this;
                    var recordData = component.ownerCt.getRecordData();
                    var listTree = me.getShoprecommendscope().down("treepanel");
                    var selectionModel = listTree.getSelectionModel().getSelection()[0];
                    var nodeId = recordData.get("id");
                    me.commonUtils.mask();
                    me.getRecommendTreeImgUpload().setParentTargetWin(listTree);
                    me.getRecommendTreeImgUpload().down("[name=nodeId]").setValue(nodeId);
                    me.getRecommendTreeImgUpload().show();
                }
            },
            'recommendTreeMenu [id=showRecommendImg]':{
                click:function(component, e){
                    var me = this;
                    var recordData = component.ownerCt.getRecordData();
                    var listTree = me.getShoprecommendscope().down("treepanel");
                    var selectionModel = listTree.getSelectionModel().getSelection()[0];
                    window.open (recordData.get("value"),"","width=800,height=600")

                }
            },
            'recommendTreeMenu [id=refreshRecommendTree]':{
                click:function(component, e){
                    var me = this;
                    var recordData = component.ownerCt.getRecordData();
                    var listTree = me.getShoprecommendscope().down("treepanel");
                    listTree.store.load();
                }
            }
        });
    },

    //显示地图窗口
    showAuditAddressCoordinateWindow: function () {
        var me=this, win = me.getAuditAddressCoordinateWindow(),
            form = win.down("form"),
            rec = me.getExistingCustomerManageCheckPendingList().getSelectionModel().getSelection()[0];
        win.showAddressCoordinateWindow(rec);
    },

    showRegisteredAddressCoordinateWindow: function () {
        var me=this, win = me.getRegisteredAddressCoordinateWindow(),
            form = win.down("form"),
            rec = me.getExistingCustomerManageRegisteredList().getSelectionModel().getSelection()[0];
        win.showAddressCoordinateWindow(rec);
    },

    showTrialAddressCoordinateWindow: function () {
        var me=this, win = me.getTrialAddressCoordinateWindow(),
            form = win.down("form"),
            rec = me.getShopRegisteredTrialList().getSelectionModel().getSelection()[0];
        win.showAddressCoordinateWindow(rec);
    },

    showCustomerWindow: function (grid, row, col) {
        var win = this.getCustomerWindow();
        win.setOperateType("update");
        win.setTitle('客户信息编辑');
        win.down("permissionShopVersionSelect").setReadOnly(true);
        win.down('[id=softPriceRadio]').hide();
        win.down('[id=softPriceInput]').name = 'softPrice2';
        win.showWin(grid.getStore().getAt(row));
    },

    disableShop: function (shopOperateHistory) {
        var me = this;
        me.commonUtils.ajax({
            url: 'shopManage.do?method=createShopOperateHistory',
            params: shopOperateHistory,
            success: function (result) {
                if (result.success) {
                    Ext.Msg.alert('返回结果', "操作成功！", function () {
                        me.getExistingCustomerManageRegisteredList().onSearch();
                        me.getOperateView().close();
                    });
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
//        target.shopbusinessscopewindow.show();
        win.show();
    },

    //客户禁用 启用
    enableOrDisableRegisteredShop:function(){
        var me=this;
        me.commonUtils.mask();
        var rec =  me.getExistingCustomerManageRegisteredList().getSelectionModel().getSelection()[0],
            shopId = rec.get("id"),
            trialStartTime = rec.get("trialStartTime");
        me.getOperateView().down("customerMangeOperateHistoryList")
            .store.proxy.extraParams = {
            shopId: shopId
        };
        me.getOperateView().setShopId(shopId);
        me.getOperateView().setTrialStartTime(trialStartTime);
        me.getCustomerMangeOperateHistoryList().store.load();
        var form = me.getExistingCustomerManageOperatorForm();
        if (rec.get("shopStatus") == "REGISTERED_PAID") {
            if (rec.get("shopState") == "IN_ACTIVE") {
                form.down("[action=disable]").hide();
                form.down("[action=trial]").hide();
                form.down("[action=update]").hide();
                form.setTitle("店铺名：" + rec.get("name") + "&nbsp&nbsp" + "当前状态：缴费使用（禁）");
            } else if (rec.get("shopState") == "ACTIVE") {
                form.down("[action=enable]").hide();
                form.down("[action=update]").hide();
                form.down("[action=trial]").hide();
                form.setTitle("店铺名：" + rec.get("name") + "&nbsp&nbsp" + "当前状态：缴费使用");

            }
            form.down("[name=trialTime]").hide();
            form.setHeight(100);
        } else {
            form.down("[action=disable]").hide();
            form.down("[action=enable]").hide();
            form.setTitle("店铺名：" + rec.get("name") + "&nbsp&nbsp" + "当前状态：注册使用");
            form.setHeight(150);
        }
        me.getOperateView().show();
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
    },

    //广告区域
    showAdArea: function (target) {
        var me = this;
        var shopAdAreaIds = target.getShopAdAreaIds();
        if (!shopAdAreaIds) {
            Ext.Bcgogo.ShopAdAreaScope.readUrl = "shopAd.do?method=getShopAdAreaScopeByShopId";
        } else {
            Ext.Bcgogo.ShopAdAreaScope.readUrl = "shopAd.do?method=getShopAdAreaScope";
        }
        if (shopAdAreaIds)
            Ext.Bcgogo.ShopAdAreaScope.params = shopAdAreaIds.join(',');
        var win = me.getShopadareascope();
        win.setOpenTarget(target);
        if (win.down('treepanel')) {
            var rootTree = win.down('treepanel').getRootNode();
            me.commonUtils.ajax({
                url: Ext.Bcgogo.ShopAdAreaScope.readUrl,
                params: {
                    ids: Ext.Bcgogo.ShopAdAreaScope.params
                },
                success: function (result) {
                    var selectIds = [];
                    var expandedIds = [];
                    for(var i=0;i<result.children.length;i++){
                        var pData = result.children[i];
                        if(pData.checked){
                            selectIds.push(pData["id"]);
                        }
                        if(pData.expanded){
                            expandedIds.push(pData["id"]);
                        }
                        for(var j=0;j<pData.children.length;j++){
                            var childDate = pData.children[j];
                            if(childDate.checked){
                                selectIds.push(childDate["id"]);
                            }
                            if(childDate.expanded){
                                childDate.push(childDate["id"]);
                            }
                        }
                    }

                    for (var k = 0; k < rootTree.childNodes.length; k++) {
                        var parent = rootTree.childNodes[k];
                        parent.data.text = parent.data.value;
                        var checked = Ext.Array.contains(selectIds,parent.data.id);
                        parent.data.checked = checked;
                        parent.updateInfo({checked: checked});
                        var expanded = Ext.Array.contains(expandedIds,parent.data.id);
                        if (expanded) {
                            parent.expand();
                        } else {
                            parent.collapse();
                        }
                        for (var n = 0; n < parent.childNodes.length; n++) {
                            var child = parent.childNodes[n];
                            child.data.text = child.data.value;
                            var childChecked = Ext.Array.contains(selectIds,child.data.id);
                            child.data.checked = childChecked;
                            child.updateInfo({checked: childChecked});
                        }
                    }
                    win.show();
                }
            });


        } else {
            win.show();
        }
    },

    //广告类目
    showShopRecommend:function(target,selection){
        var me = this;
        var shopRecommendIds = target.getShopRecommendIds();
        Ext.Bcgogo.ShopRecommendScope.readUrl = "shopAd.do?method=getShopRecommend";
        if (shopRecommendIds){
            Ext.Bcgogo.ShopRecommendScope.params = shopRecommendIds.join(',');
        }
        var shopId = null;
        if(selection){
            shopId = selection.get("id");
        }
//        var rec =  me.getExistingCustomerManageRegisteredList().getSelectionModel().getSelection()[0];
//        var shopId = rec.get("id");
        Ext.Bcgogo.ShopRecommendScope.shopId = shopId;
        var win = me.getShoprecommendscope();
        win.setOpenTarget(target);
        if (win.down('treepanel')) {
            var rootTree = win.down('treepanel').getRootNode();
            win.down('treepanel').store.getProxy().setExtraParam("ids",Ext.Bcgogo.ShopRecommendScope.params);
            win.down('treepanel').store.getProxy().setExtraParam("shopId",Ext.Bcgogo.ShopRecommendScope.shopId);
            me.commonUtils.ajax({
                url: Ext.Bcgogo.ShopRecommendScope.readUrl,
                params: {
                    ids: Ext.Bcgogo.ShopRecommendScope.params,
                    shopId:Ext.Bcgogo.ShopRecommendScope.shopId
                },
                success: function (result) {
                    var selectIds = [];
                    var expandedIds = [];
                    for(var i=0;i<result.children.length;i++){
                        var pData = result.children[i];
                        if(pData.checked){
                            selectIds.push(pData["id"]);
                        }
                        if(pData.expanded){
                            expandedIds.push(pData["id"]);
                        }
                        for(var j=0;j<pData.children.length;j++){
                            var childDate = pData.children[j];
                            if(childDate.checked){
                                selectIds.push(childDate["id"]);
                            }
                            if(childDate.expanded){
                                expandedIds.push(childDate["id"]);
                            }
                        }
                    }

                    for (var k = 0; k < rootTree.childNodes.length; k++) {
                        var parent = rootTree.childNodes[k];
                        var checked = Ext.Array.contains(selectIds,parent.data.id);
                        parent.data.checked = checked;
                        parent.updateInfo({checked: checked});
                        var expanded = Ext.Array.contains(expandedIds,parent.data.id);
                        if (expanded) {
                            parent.expand();
                        } else {
                            parent.collapse();
                        }
                        for (var n = 0; n < parent.childNodes.length; n++) {
                            var child = parent.childNodes[n];
                            var childChecked = Ext.Array.contains(selectIds,child.data.id);
                            child.data.checked = childChecked;
                            child.updateInfo({checked: childChecked});
                        }
                    }
                    win.show();
                }
            });


        } else {
            win.show();
        }
    },
    treeMenu:function (view, record, item, rowIndex, e) {
        e.preventDefault();
        var contextMenu = this.getRecommendTreeMenu();
//        var addFirstRecommendCategory = contextMenu.down("#addFirstRecommendCategory");
        var addSecondRecommendCategory = contextMenu.down("#addSecondRecommendCategory");
        var deleteFirstRecommendCategory = contextMenu.down("#deleteFirstRecommendCategory");
        var deleteSecondRecommendCategory = contextMenu.down("#deleteSecondRecommendCategory");
        var showRecommendImg = contextMenu.down("#showRecommendImg");
        var addRecommendImg = contextMenu.down("#addRecommendImg");
//        var refreshRecommendTree = contextMenu.down("#refreshRecommendTree");
        if(record.get("parentId") == -1){
//            addFirstRecommendCategory.show();
            addSecondRecommendCategory.show();
            deleteFirstRecommendCategory.show();
            deleteSecondRecommendCategory.hide();
            showRecommendImg.hide();
            addRecommendImg.hide();
        }else{
//            addFirstRecommendCategory.hide();
            addSecondRecommendCategory.hide();
            deleteFirstRecommendCategory.show();
            deleteSecondRecommendCategory.hide();
            if(Ext.isEmpty(record.get("value"))){
                showRecommendImg.hide();
            }else{
                showRecommendImg.show();
            }
            addRecommendImg.show();
        }
        contextMenu.setRecordData(record);
        contextMenu.showAt(e.getX(), e.getY());

    },
    updateCommendShop:function(editor, e, eOpts){
        var me = this, url,
            rec = e.record, message,
            listTree = me.getShoprecommendscope().down("treepanel");
        url = "shopAd.do?method=saveOrUpdateShopRecommend";
        me.commonUtils.ajax({
            url:url,
            params:{
                id:rec.get("id"),
                parentId:rec.get("parentId"),
                name:rec.get("text"),
                sort:rec.get("sort")
            },
            success:function (result) {
                if(result && result.success){
                    rec.set("id", result.data.id);
                    rec.set("text", result.data.name);
                    rec.set("sort", result.data.sort);
                    rec.commit();
                    listTree.store.sort('sort', 'ASC');
                }else{
                    var cellEditingPlugin = listTree.editingPlugin;
                    Ext.Msg.alert('警告', result.msg, function () {
                        rec.set("text", rec.get("text") + "-新");
                        cellEditingPlugin.startEdit(rec, 0);
                    });
                }
//                if (result.duplicate) {
//                    var cellEditingPlugin = listTree.editingPlugin;
//                    var cellEditingPlugin = listTree.cellEditingPlugin;
//                    Ext.Msg.alert('警告', message, function () {
//                        rec.set("value", rec.get("value") + "-新");
//                        cellEditingPlugin.startEdit(rec, 0);
//                    });
//                } else {
//                    rec.set("id", result.node.id);
//                    rec.set("text", result.node.text);
//                    rec.set("sort", result.node.sort);
//                    rec.commit();
//                    listTree.store.sort('sort', 'ASC');
//                }
            }
        });
    }
});