/**
 * 编辑客户 窗口
 * @author :zhangjuntao
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.CustomerWindow', {
    alias: 'widget.existingCustomerManageCustomerWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    autoScroll: true,
    maximizable: true,
    bodyStyle: "backgroundColor:#DFE8F6;",
    title: '新增客户线索',
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.ShopBusinessScopeWindow",
        'Ext.view.dataMaintenance.permission.ShopVersionSelect',
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.ShopKind",
        'Ext.view.customerMange.ShopProductWin',
        'Ext.view.customerMange.ShopBusinessScopeWindow',
        "Ext.view.customerMange.ProvinceSelect",
        "Ext.view.UpLoadImageWin",
        "Ext.view.customerMange.ShopVehicleBrandModelWindow"
    ],
    shopbusinessscopewindow: Ext.create("Ext.view.customerMange.ShopBusinessScopeWindow"),
    shopproductwin: Ext.create("Ext.view.customerMange.ShopProductWin"),
    uploadimagewin: Ext.create("Ext.view.UpLoadImageWin"),
    shopvehiclebrandmodelwindow: Ext.create("Ext.view.customerMange.ShopVehicleBrandModelWindow"),
    shopAdAreaScope: Ext.create("Ext.view.customerMange.ShopAdAreaScope"),
    shopRecommendTree: Ext.create("Ext.view.customerMange.ShopRecommendScope"),

    initComponent: function () {
        var me = this,
            userSelect = Ext.widget("userSelect", { //操作人下拉建议
                name: 'followName',
                displayField: 'name',
                valueField: 'name',
                fieldLabel: '销售跟进人',
                allowBlank: false,
                editable: false
            });
        userSelect.store.proxy.extraParams = {
            operateScene: "all"
        };
//        var container = {
//            xtype: 'checkboxgroup',
//            fieldLabel: '服务范围:',
//            padding: 0,
//            margin: "0 5 0 -5",
//            name: "serviceCategory",
//            anchor: '100%',
//            labelWidth: 80,
//            width: 400,
//            columns:11,
//            items: [
//
//            ]
//        };
        me.commonUtils = Ext.create("Ext.utils.Common");

        me.shopbusinessscopewindow.setBusinessScopeTarget(me);
        Ext.apply(me, {
            items: [
                {
                    xtype: 'form',
                    bodyPadding: 5,
                    width: 1000,
                    height: 450,
                    autoScroll: true,
                    frame: false,
                    border: false,
                    buttons: [
                        {
                            text: '保存',
                            action: 'save',
                            scope: me
                        },
                        {
                            text: '取消',
                            tooltip: "取消",
                            handler: function () {
                                me.close();
//                                this.up("form").form.reset();
                            }
                        }
                    ],
                    baseCls: "x-plain",
                    layout: 'anchor',
                    defaults: {
                        bodyStyle: "backgroundColor:#DFE8F6;",
                        anchor: '100%'
                    },
                    fieldDefaults: {
                        labelWidth: 70,
                        xtype: 'textfield',
                        frame: true,
                        width: 200,
                        height: 20
                    },
                    items: [
                        {
                            xtype: 'fieldset',
                            name:'shopBaseInfo',
                            title: '店铺基本信息',
                            layout: 'anchor',
                            margin: "10 0 0 0",
                            defaults: {
                                anchor: '100%',
                                labelStyle: 'padding-left:4px;'
                            },
                            collapsible: true,
                            collapsed: false,
                            items: [
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            name: 'name',
                                            fieldLabel: '单位名称',
                                            enforceMaxLength: true,
                                            maxLength: 25,
                                            emptyText: "最多不超过25个字",
//                                            vtype: 'shopCharacters',
                                            allowBlank: false,
                                            validator: function (value) {        //validator 频繁触发 采用blur 触发validator
                                                return  !this.duplicating ? true : this.duplicating;
                                            }
                                        },
                                        {
                                            name: 'shortname',
                                            fieldLabel: '店铺简称',
                                            enforceMaxLength: true,
                                            maxLength: 15,
                                            emptyText: "最多不超过15个字",
                                            vtype: 'characters',
                                            validator: function (value) {        //validator 频繁触发 采用blur 触发validator
                                                return  !this.duplicating ? true : this.duplicating;
                                            }
                                        },
                                        {
                                            name: 'licencePlate',
                                            maxLength: 3,
                                            enforceMaxLength: true,
                                            fieldLabel: "车牌前缀"
                                        },
                                        {
                                            name: 'landline',
                                            vtype: "phone",
                                            maxLength: 16,
                                            enforceMaxLength: true,
                                            fieldLabel: "固定电话"
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            vtype: "characters",
                                            name: 'storeManager',
                                            fieldLabel: '店面管理员',
                                            emptyText: "最多不超过10个字",
                                            allowBlank: false,
                                            enforceMaxLength: true,
                                            maxLength: 10
                                        },
                                        {
                                            vtype: "mobilePhone",
                                            fieldLabel: '联系电话',
                                            name: 'storeManagerMobile',
                                            allowBlank: false,
                                            enforceMaxLength: true,
                                            maxLength: 11
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    name: "area",
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    border: false,
                                    items: [
                                        {
                                            xtype: 'fieldset',
                                            layout: 'hbox',
                                            anchor: '100%',
                                            margin: "0 5 0 0",
                                            padding: 0,
                                            defaults: {
                                                anchor: "100%",
                                                margin: "0 5 5 0"
                                            },
                                            border: false,
                                            items: [
                                                {
                                                    fieldLabel: '所在区域',
                                                    labelWidth: 70,
                                                    width: 180,
                                                    name: 'province',
                                                    id: 'formProvinceSelect',
                                                    allowBlank: false,
                                                    xtype: "provinceSelect",
                                                    listeners: {
                                                        scope: me,
                                                        select: me.provinceSelectAction,
                                                        beforequery: me.provinceBeforeQuery,
                                                        beforerender: me.provinceBeforeRender
                                                    }
                                                },
                                                {
                                                    width: 100,
                                                    name: 'city',
                                                    allowBlank: false,
                                                    id: 'formCitySelect',
                                                    xtype: "citySelect",
                                                    listeners: {
                                                        scope: me,
                                                        select: me.citySelectAction,
                                                        beforequery: me.cityBeforeQuery,
                                                        beforerender: me.cityBeforeRender
                                                    }
                                                },
                                                {
                                                    width: 100,
                                                    id: 'formRegionSelect',
                                                    xtype: "regionSelect",
                                                    name: 'region',
                                                    listeners: {
                                                        scope: me,
                                                        select: me.regionSelectAction,
                                                        beforequery: me.regionBeforeQuery,
                                                        beforerender: me.regionBeforeRender
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            xtype: 'textfield',
                                            name: 'address',
                                            margin: "0 0 0 20",
                                            fieldLabel: '详细地址',
                                            labelWidth: 70,
                                            width: 410,
                                            enforceMaxLength: true,
                                            maxLength: 100
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    name: "serviceCategoryFieldset",
                                    frame:false,
                                    padding:0,
                                    margin: "0 5 0 0",
                                    border: false
//                                    xtype: 'checkboxgroup',
//                                    fieldLabel: '服务范围:',
//                                    padding: 0,
//                                    margin: "0 5 0 -5",
//                                    name: "serviceCategory",
//                                    anchor: '100%',
//                                    labelWidth: 80,
//                                    width: 400,
//                                    columns:11,
//                                    items: [
//
//                                    ]
                                },
                                {
                                    xtype: "hiddenfield",
                                    hidden: true,
                                    name: 'shopVersionStr'
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            xtype: 'radiogroup',
                                            fieldLabel: '主营车型',
                                            anchor: '60%',
                                            labelWidth: 80,
                                            width: 400,
                                            items: [
                                                {xtype: 'radio', boxLabel: '所有车型', name: 'selectAllBrandModel', inputValue:true,  id:'allModel'},
                                                {xtype: 'radio', name: 'selectAllBrandModel', inputValue:false, id: 'partVehicle'}
                                            ]
                                        },
                                        {
                                            text: '部分车型',
                                            action: 'modifyVehicleModel',
                                            xtype: "button",
                                            width: 60,
                                            margin: "0 5 0 -150"
                                        }
                                    ]

                                },
                                {
                                    xtype: "displayfield",
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        labelWidth: 70
                                    },
                                    border: false,
                                    width: 'auto',
                                    name: 'vehicleModelIds'
                                },

                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            text: '经营产品',
//                                            iconCls: 'icon-application-view-list',
                                            action: 'showBusinessScope',
                                            xtype: "button",
                                            width: 70
                                        },
                                        {
                                            xtype: "displayfield",
                                            width: 'auto',
                                            name: 'showBusinessScope'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            text: '主营产品',
//                                            iconCls: 'icon-application-view-list',
                                            action: 'addProducts',
                                            xtype: "button",
                                            disabled:true,
                                            width: 70,
                                            scope: me,
                                            handler: function () {
                                                me.shopproductwin.setProductTargetWin(me);
                                                me.shopproductwin.down("grid").getStore().removeAll();
                                                if (me.getProducts() && me.getProducts().length > 0) {
                                                    me.shopproductwin.down("grid").getStore().loadData(me.getProducts());
                                                }
                                                me.shopproductwin.show();
                                            }
                                        },
                                        {
                                            xtype: "displayfield",
                                            width: 'auto',
                                            name: 'productsInfo'
                                        }/*,
                                         {
                                         fieldLabel: '主营产品',
                                         xtype: "displayfield",
                                         width: 'auto',
                                         name: 'productsInfo'
                                         }*/
                                    ]
                                },
                                {
                                    xtype: 'checkboxgroup',
                                    fieldLabel: '代理产品',
                                    name: "agentProduct",
                                    anchor: '100%',
                                    labelWidth: 80,
                                    width: 400,
                                    columns:11,
                                    items: [
                                        {boxLabel: 'OBD', name: 'agentProductIds', inputValue:10000010001000000}
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            title: '店铺联系信息（负责人/店主的联系手机号将作为注册店铺的账号）',
                            layout: 'anchor',
                            margin: "10 0 0 0",
                            defaults: {
                                anchor: '100%',
                                labelStyle: 'padding-left:4px;'
                            },
                            collapsible: true,
                            collapsed: false,
                            items: [
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            fieldLabel: '负责人/店主',
                                            name: 'owner',
                                            enforceMaxLength: true,
                                            maxLength: 10,
                                            emptyText: "最多不超过10个字",
                                            vtype: 'characters',
                                            allowBlank: false
                                        },
                                        {
                                            name: 'mobile',
                                            vtype: "mobilePhone",
                                            fieldLabel: '联系手机',
                                            allowBlank: false,
                                            enforceMaxLength: true,
                                            maxLength: 11
                                        },
                                        {
                                            name: 'qq',
                                            vtype: "integer",
                                            enforceMaxLength: true,
                                            maxLength: 15,
                                            fieldLabel: "QQ"
                                        },
                                        {
                                            name: 'email',
                                            vtype: "email",
                                            enforceMaxLength: true,
                                            maxLength: 50,
                                            fieldLabel: "Email"
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            fieldLabel: '联系人',
                                            name: 'contacts[1].name',
                                            itemId: 'contacts1.name',
                                            enforceMaxLength: true,
                                            maxLength: 10,
                                            emptyText: "最多不超过10个字",
                                            vtype: 'characters'
                                        },
                                        {
                                            name: 'contacts[1].mobile',
                                            itemId: 'contacts1.mobile',
                                            vtype: "mobilePhone",
                                            fieldLabel: '联系手机',
                                            enforceMaxLength: true,
                                            maxLength: 11
                                        },
                                        {
                                            name: 'contacts[1].qq',
                                            itemId: 'contacts1.qq',
                                            vtype: "integer",
                                            enforceMaxLength: true,
                                            maxLength: 15,
                                            fieldLabel: "QQ"
                                        },
                                        {
                                            name: 'contacts[1].email',
                                            itemId: 'contacts1.email',
                                            vtype: "email",
                                            enforceMaxLength: true,
                                            maxLength: 50,
                                            fieldLabel: "Email"
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            fieldLabel: '联系人',
                                            name: 'contacts[2].name',
                                            itemId: 'contacts2.name',
                                            enforceMaxLength: true,
                                            maxLength: 10,
                                            emptyText: "最多不超过10个字",
                                            vtype: 'characters'
                                        },
                                        {
                                            name: 'contacts[2].mobile',
                                            itemId: 'contacts2.mobile',
                                            vtype: "mobilePhone",
                                            fieldLabel: '联系手机',
                                            enforceMaxLength: true,
                                            maxLength: 11
                                        },
                                        {
                                            name: 'contacts[2].qq',
                                            itemId: 'contacts2.qq',
                                            vtype: "integer",
                                            enforceMaxLength: true,
                                            maxLength: 15,
                                            fieldLabel: "QQ"
                                        },
                                        {
                                            name: 'contacts[2].email',
                                            itemId: 'contacts2.email',
                                            vtype: "email",
                                            enforceMaxLength: true,
                                            maxLength: 50,
                                            fieldLabel: "Email"
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            title: "店铺其他信息",
                            bodyStyle: "backgroundColor:#DFE8F6;",
//                            layout: "hbox",
                            collapsible: true,
                            collapsed: false,
                            defaults: {
                                anchor: '100%',
                                labelStyle: 'padding-left:4px;'
//                                columnWidth: .5
                            },
                            items: [
                                //*************
                                {
                                  xtype:'fieldset',
                                    layout: 'column',
                                    border:false,
                                    anchor: '100%',
                                    margin:'0 0 0 0',
                                    padding:'0 0 0 0',
                                    items:[
                                        {
                                            xtype: 'radiogroup',
                                            fieldLabel: '经营方式',
                                            anchor: '60%',
                                            labelWidth: 55,
                                            width: 500,
//                                            columnWidth:.6,
                                            items: [
                                                {boxLabel: '加盟连锁', name: 'operationModes', inputValue: "加盟连锁"},
                                                {boxLabel: '有限公司', name: 'operationModes', inputValue: "有限公司"},
                                                {boxLabel: '专卖店', name: 'operationModes', inputValue: "专卖店"},
                                                {boxLabel: '个体', name: 'operationModes', inputValue: "个体"} ,
                                                {boxLabel: '其他', name: 'operationModes', inputValue: null, id: 'otherOperationMode', scope: me, handler: me.toggleOtherOperationModeFields},
                                                {
                                                    xtype: 'textfield',
                                                    name: 'otherOperationMode',
                                                    labelWidth: 30,
                                                    enforceMaxLength: true,
                                                    maxLength: 10,
                                                    vtype: 'characters',
                                                    id: "otherOperationModeText",
                                                    margin: '0 0 0 0',
                                                    disabled: true,
                                                    editable: false
                                                }
                                            ]
                                        },
                                        {
                                            xtype: 'textfield',
//                                            columnWidth:.4,
                                            name: 'url',
                                            vtype: "url",
                                            margin: "0 0 0 20",
                                            fieldLabel: '网址',
                                            labelWidth: 70,
                                            width: 300,
                                            enforceMaxLength: true,
                                            maxLength: 30
                                        }
                                    ]
                                },
                                {
                                    "xtype":'fieldset',
                                    layout: 'column',
                                    border:false,
                                    margin:'0 0 0 0',
                                    padding:'0 0 0 0',
                                    anchor: '100%',
                                    items:[{
                                        xtype: 'textfield',
//                                        columnWidth:.3,
//                                            labelAlign: 'right',
//                                            labelSeparator: '',
                                        name: 'adPricePerMonth',
//                                            margin: "0 0 0 20",
                                        fieldLabel: '广告月费',
                                        vtype:'money',
                                        labelWidth: 55,
                                        width: 150,
                                        enforceMaxLength: true,
                                        maxLength: 5
                                    } ,{
                                        xtype:'displayfield',
                                        labelSeparator: '',
                                        fieldLabel: '元/月'

                                    } ,
                                        {
                                            xtype: 'datefield',
                                            format: 'Y-m-d',
                                            width: 180,
//                                            columnWidth:.4,
                                            name: 'adStartDateStr',
//                                            margin: "0 0 0 20",
                                            fieldLabel: '广告有效期',
                                            labelWidth: 70,
                                            enforceMaxLength: true,
                                            maxLength: 10
                                        },
                                        {
                                            xtype: 'datefield',
                                            format: 'Y-m-d',
                                            width: 130,
//                                            columnWidth:.3,
                                            name: 'adEndDateStr',
//                                            margin: "0 0 0 20",
                                            fieldLabel: '至',
                                            labelSeparator: '',
                                            labelWidth: 20,
                                            enforceMaxLength: true,
                                            maxLength: 10
                                        }]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'column',
                                    border: false,
                                    anchor: '100%',
                                    margin: '0 0 0 0',
                                    padding: '0 0 0 0',
                                    items: [
                                        {
                                            xtype: 'radiogroup',
                                            fieldLabel: '广告范围',
                                            anchor: '60%',
                                            labelWidth: 55,
                                            width: 260,
//                                            columnWidth:.6,
                                            items: [
                                                {boxLabel: '未开启广告', name: 'productAdType', inputValue: "DISABLED",labelWidth: 55,width:80,id:"disabledAdArea"},
                                                {boxLabel: '全部省市', name: 'productAdType', inputValue: "ALL",labelWidth: 50,width:70,id:"allAdArea"},
                                                {boxLabel: '', name: 'productAdType', inputValue: "PART",labelWidth: 5,width:10,id:"partAdArea"}
                                            ]
                                        },
                                        {
                                            text: '部分省市',
                                            action: 'selectAdArea',
                                            xtype: "button",
                                            width: 70,
                                            margin: "0 5 0 -15"
                                        },{
                                            xtype:'displayfield',
                                            labelSeparator: '',
                                            width: 'auto',
                                            name:'selectedAdAreaDisplay'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'column',
                                    border: false,
                                    anchor: '100%',
                                    margin: '0 0 0 0',
                                    padding: '0 0 0 0',
                                    items: [
                                        {
                                            fieldLabel: '店铺类目',
                                            xtype: 'displayfield',
                                            width: 'auto'
                                        },
                                        {
                                            text: '选择类目',
                                            action: 'selectShopRecommend',
                                            xtype: "button",
                                            width: 70,
                                            margin: "0 5 0 -15"
                                        },
                                        {
                                            xtype: 'displayfield',
                                            labelSeparator: '',
                                            width: 'auto',
                                            name: 'selectedShopRecommend'
                                        }
                                    ]
                                }
                            ]

                        },
                        {
                            xtype: 'fieldset',
                            title: '软件注册信息',
                            layout: 'anchor',
                            margin: "10 0 0 0",
                            defaults: {
                                anchor: '100%',
                                labelStyle: 'padding-left:4px;'
                            },
                            collapsible: true,
                            collapsed: false,
                            items: [
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            xtype: "permissionShopVersionSelect",
                                            name: 'shopVersionId',
                                            displayField: 'value',
                                            valueField: 'id',
                                            allowBlank: false,
                                            fieldLabel: "软件版本"
                                        },
                                        userSelect,
                                        {
                                            xtype: 'shopKind',
                                            name: 'shopKind',
                                            fieldLabel: '店铺种类',
                                            anchor: '30%',
                                            allowBlank: false,
                                            labelWidth: 70
                                        },

                                        {
                                            xtype: 'displayfield',
                                            name: 'agent',
                                            fieldLabel: '销售人',
                                            anchor: '30%'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [

                                        {
                                            xtype: "textfield",
                                            name: 'managerUserNo',
                                            fieldLabel: "软件账号",
                                            maxLength: 20,
                                            enforceMaxLength: true,
                                            vtype: "userNo"
                                        },
                                        {
                                            text: '重置密码',
                                            tooltip: "重置密码",
                                            action: 'resetPwd',
                                            autoShow: false,
                                            xtype: "button",
                                            width: 70,
                                            scope: me
                                        },
                                        {
                                            text: '修改客户账号',
                                            action: 'changeUserNo',
                                            xtype: "button",
                                            width: 90,
                                            scope: me
                                        },
                                        {
                                            xtype:"displayfield",
                                            name: 'softPriceStr',
                                            fieldLabel: "软件销售价",
                                            width:350
                                        },
                                        {
                                            xtype: "hiddenfield",
                                            hidden: true,
                                            name: 'softPrice',
                                            id:'softPriceId'
                                        },
                                        {
                                            xtype: 'radiogroup',
                                            fieldLabel: '软件销售价',
                                            layout: 'hbox',
                                            anchor: '100%',
                                            padding: 0,
                                            margin: "0 5 0 0",
                                            labelWidth: 70,
                                            width:650,
                                            defaults: {
                                                anchor: "100%",
                                                margin: "0 10 5 0",
                                                labelWidth: 40
                                            },
                                            id:'softPriceRadio',
                                            items: [
                                                {boxLabel: '一口价收费', name: 'chargeType', inputValue: "ONE_TIME", checked:true, id:'oneTime'},
                                                {
                                                    xtype: "textfield",
                                                    vtype:"money",
                                                    name: 'softPrice',
                                                    labelWidth: 30,
                                                    margin: '0 30 0 0',
                                                    id:'softPriceInput'
                                                },
                                                {boxLabel: '按年收费（第1年免费，之后每年年费1000元）', name: 'chargeType', inputValue: "YEARLY"}
                                            ]
                                        }
                                    ]
                                },
                                {
                                    xtype: 'container',
                                    items: [
                                        {
                                            xtype: "hiddenfield",
                                            hidden: true,
                                            name: 'scene',
                                            value: 'SUBMIT_CLIENT_APPLICATION'
                                        },
                                        {
                                            xtype: "hiddenfield",
                                            hidden: true,
                                            name: 'id'
                                        },
                                        {
                                            xtype: "hiddenfield",
                                            hidden: true,
                                            name: 'shopStatus'
                                        },
                                        {
                                            hidden: true,
                                            xtype: "hiddenfield",
                                            name: 'shopState'
                                        },
                                        {
                                            hidden: true,
                                            xtype: "hiddenfield",
                                            name: 'registerType'
                                        },
                                        {
                                            hidden: true,
                                            xtype: "hiddenfield",
                                            name: 'registrationDate'
                                        },
                                        {
                                            hidden: true,
                                            name: 'contacts[0].id',
                                            itemId: 'contacts0.id',
                                            xtype: "hiddenfield"
                                        },
                                        {
                                            hidden: true,
                                            name: 'contacts[1].id',
                                            itemId: 'contacts1.id',
                                            xtype: "hiddenfield"
                                        },
                                        {
                                            hidden: true,
                                            name: 'contacts[2].id',
                                            itemId: 'contacts2.id',
                                            xtype: "hiddenfield"
                                        }
                                    ]
                                }

                            ]
                        },
                        {
                            xtype: 'fieldset',
                            title: '审核信息',
                            itemId:"auditInfo",
                            layout: 'anchor',
                            name: "auditInfo",
                            margin: "10 0 0 0",
                            defaults: {
                                anchor: '100%',
                                labelStyle: 'padding-left:4px;'
                            },
                            collapsible: true,
                            collapsed: false,
                            items: [
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            title: '上传营业执照',
                            layout: 'anchor',
                            margin: "10 0 0 0",
                            defaults: {
                                anchor: '100%',
                                labelStyle: 'padding-left:4px;'
                            },
                            collapsible: true,
                            collapsed: false,
                            items: [
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            text: '点击上传',
//                                            iconCls: 'icon-application-view-list',
                                            xtype: "button",
                                            width: 70,
                                            scope: me,
                                            handler: function () {
                                                me.uploadimagewin.setParentTargetWin(me);
                                                var imageForm = me.uploadimagewin.down("form");
                                                imageForm.down('[name=imageShopId]').setValue(me.down('[name=id]').getValue());
                                                imageForm.down('[name=imageFieldItemId]').setValue("imageCenterDTO.shopBusinessLicenseImagePath");
                                                imageForm.down('[name=imageBrowseFieldId]').setValue("shopBusinessLicenseImageBrowse");
                                                me.uploadimagewin.show();
                                            }
                                        },
                                        {
                                            hidden: true,
                                            name: 'imageCenterDTO.shopBusinessLicenseImagePath',
                                            itemId: 'imageCenterDTO.shopBusinessLicenseImagePath',
                                            xtype: "hiddenfield"
                                        },
                                        {
                                            xtype: 'box', //或者xtype: 'component',
                                            width: 285, //图片宽度
                                            height: 180, //图片高度
                                            itemId: "shopBusinessLicenseImageBrowse",
                                            autoEl: {
                                                tag: 'img',    //指定为img标签
                                                src: Ext.BLANK_IMAGE_URL    //指定url路径
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            title: '上传店铺照片',
                            layout: 'anchor',
                            margin: "10 0 0 0",
                            defaults: {
                                anchor: '100%',
                                labelStyle: 'padding-left:4px;'
                            },
                            collapsible: true,
                            collapsed: false,
                            items: [
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            text: '点击上传',
//                                            iconCls: 'icon-application-view-list',
                                            xtype: "button",
                                            width: 70,
                                            scope: me,
                                            handler: function () {
                                                me.uploadimagewin.setParentTargetWin(me);
                                                var imageForm = me.uploadimagewin.down("form");
                                                imageForm.down('[name=imageShopId]').setValue(me.down('[name=id]').getValue());
                                                imageForm.down('[name=imageFieldItemId]').setValue("imageCenterDTO.shopImagePaths0");
                                                imageForm.down('[name=imageBrowseFieldId]').setValue("shopImageBrowse0");
                                                me.uploadimagewin.show();
                                            }
                                        },
                                        {
                                            hidden: true,
                                            name: 'imageCenterDTO.shopImagePaths[0]',
                                            itemId: 'imageCenterDTO.shopImagePaths0',
                                            xtype: "hiddenfield"
                                        },
                                        {
                                            xtype: 'box', //或者xtype: 'component',
                                            width: 360, //图片宽度
                                            height: 240, //图片高度
                                            itemId: "shopImageBrowse0",
                                            autoEl: {
                                                tag: 'img',    //指定为img标签
                                                src: Ext.BLANK_IMAGE_URL    //指定url路径
                                            }
                                        },
                                        {
                                            text: '点击上传',
//                                            iconCls: 'icon-application-view-list',
                                            xtype: "button",
                                            width: 70,
                                            scope: me,
                                            handler: function () {
                                                me.uploadimagewin.setParentTargetWin(me);
                                                var imageForm = me.uploadimagewin.down("form");
                                                imageForm.down('[name=imageShopId]').setValue(me.down('[name=id]').getValue());
                                                imageForm.down('[name=imageFieldItemId]').setValue("imageCenterDTO.shopImagePaths1");
                                                imageForm.down('[name=imageBrowseFieldId]').setValue("shopImageBrowse1");
                                                me.uploadimagewin.show();
                                            }
                                        },
                                        {
                                            hidden: true,
                                            name: 'imageCenterDTO.shopImagePaths[1]',
                                            itemId: 'imageCenterDTO.shopImagePaths1',
                                            xtype: "hiddenfield"
                                        },
                                        {
                                            xtype: 'box', //或者xtype: 'component',
                                            width: 360, //图片宽度
                                            height: 240, //图片高度
                                            itemId: "shopImageBrowse1",
                                            autoEl: {
                                                tag: 'img',    //指定为img标签
                                                src: Ext.BLANK_IMAGE_URL    //指定url路径
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldset',
                                    layout: 'hbox',
                                    anchor: '100%',
                                    padding: 0,
                                    margin: "0 5 0 0",
                                    defaults: {
                                        xtype: "textfield",
                                        anchor: "100%",
                                        margin: "0 10 5 0",
                                        width: 200,
                                        labelWidth: 70
                                    },
                                    border: false,
                                    items: [
                                        {
                                            text: '点击上传',
//                                            iconCls: 'icon-application-view-list',
                                            xtype: "button",
                                            width: 70,
                                            scope: me,
                                            handler: function () {
                                                me.uploadimagewin.setParentTargetWin(me);
                                                var imageForm = me.uploadimagewin.down("form");
                                                imageForm.down('[name=imageShopId]').setValue(me.down('[name=id]').getValue());
                                                imageForm.down('[name=imageFieldItemId]').setValue("imageCenterDTO.shopImagePaths2");
                                                imageForm.down('[name=imageBrowseFieldId]').setValue("shopImageBrowse2");
                                                me.uploadimagewin.show();
                                            }
                                        },
                                        {
                                            hidden: true,
                                            name: 'imageCenterDTO.shopImagePaths[2]',
                                            itemId: 'imageCenterDTO.shopImagePaths2',
                                            xtype: "hiddenfield"
                                        },
                                        {
                                            xtype: 'box', //或者xtype: 'component',
                                            width: 360, //图片宽度
                                            height: 240, //图片高度
                                            itemId: "shopImageBrowse2",
                                            autoEl: {
                                                tag: 'img',    //指定为img标签
                                                src: Ext.BLANK_IMAGE_URL    //指定url路径
                                            }
                                        },
                                        {
                                            text: '点击上传',
//                                            iconCls: 'icon-application-view-list',
                                            xtype: "button",
                                            width: 70,
                                            scope: me,
                                            handler: function () {
                                                me.uploadimagewin.setParentTargetWin(me);
                                                var imageForm = me.uploadimagewin.down("form");
                                                imageForm.down('[name=imageShopId]').setValue(me.down('[name=id]').getValue());
                                                imageForm.down('[name=imageFieldItemId]').setValue("imageCenterDTO.shopImagePaths3");
                                                imageForm.down('[name=imageBrowseFieldId]').setValue("shopImageBrowse3");
                                                me.uploadimagewin.show();
                                            }
                                        },
                                        {
                                            hidden: true,
                                            name: 'imageCenterDTO.shopImagePaths[3]',
                                            itemId: 'imageCenterDTO.shopImagePaths3',
                                            xtype: "hiddenfield"
                                        },
                                        {
                                            xtype: 'box', //或者xtype: 'component',
                                            width: 360, //图片宽度
                                            height: 240, //图片高度
                                            itemId: "shopImageBrowse3",
                                            autoEl: {
                                                tag: 'img',    //指定为img标签
                                                src: Ext.BLANK_IMAGE_URL    //指定url路径
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        });
        me.callParent();
    },

    setProductCategoryIds: function (productCategoryIds) {
        this.productCategoryIds = productCategoryIds;
    },

    getProductCategoryIds: function () {
        return this.productCategoryIds;
    },

    setProducts: function (products) {
        this.products = products
    },

    getProducts: function () {
        return this.products;
    },
    setVehicleModelIds: function (vehicleModelIds) {
        this.vehicleModelIds = vehicleModelIds;
    },

    getVehicleModelIds: function () {
        return this.vehicleModelIds;
    },

    setShopAdAreaIds:function(shopAdAreaIds){
        this.shopAdAreaIds = shopAdAreaIds;
    },
    getShopAdAreaIds: function () {
        return this.shopAdAreaIds;
    },
    setShopRecommendIds:function(shopRecommendIds){
        this.shopRecommendIds = shopRecommendIds;
    },
    getShopRecommendIds: function () {
        return this.shopRecommendIds;
    },

    drawShopRegisterProductList: function () {
        this.shopbusinessscopewindow.drawShopRegisterProductList(this.getProducts());
    },

    close: function () {
        Ext.create("Ext.utils.Common").unmask();
//        this.operateWin = null;
        this.shopbusinessscopewindow.close();
        this.shopproductwin.close();
        this.uploadimagewin.close();
        this.doClose();
    },

    //判断是新增还是更新
    setOperateType: function (type) {
        this.operateType = type;
    },

    getOperateType: function () {
        return this.operateType;
    },

    showWin: function (rec) {
        var form = this.down("form"),
            me = this,
            baseForm = form.form;

        me.shopbusinessscopewindow.setBusinessScopeTarget(me);
        me.shopvehiclebrandmodelwindow.setOpenTarget(me);
        me.shopAdAreaScope.setOpenTarget(me);
        me.shopRecommendTree.setOpenTarget(me);
        this.commonUtils.mask();
        if (rec) {
            this.down("form").loadRecord(rec);
//            if (rec.get("otherBusinessScope")) {
//                baseForm.findField('otherBusinessScope').setValue(true);
//                baseForm.findField('otherBusinessScopeText').enable();
//                baseForm.findField('otherBusinessScopeText').setValue(rec.get("otherBusinessScope"));
//            }
//            if (rec.get("majorProduct")) {
//                baseForm.findField('majorProduct').setValue(true);
//                baseForm.findField('majorProductText').enable();
//                baseForm.findField('majorProductText').setValue(rec.get("majorProduct"));
//            }
//            if (rec.get("otherFeature")) {
//                baseForm.findField('otherFeature').setValue(true);
//                baseForm.findField('otherFeatureText').enable();
//                baseForm.findField('otherFeatureText').setValue(rec.get("otherFeature"));
//            }
//            if (rec.get("otherVehicleBrand")) {
//                baseForm.findField('otherVehicleBrand').setValue(true);
//                baseForm.findField('otherVehicleBrandText').enable();
//                baseForm.findField('otherVehicleBrandText').setValue(rec.get("otherVehicleBrand"));
//            }
//            if (rec.get("otherRelatedBusiness")) {
//                baseForm.findField('otherRelatedBusiness').setValue(true);
//                baseForm.findField('otherRelatedBusinessText').enable();
//                baseForm.findField('otherRelatedBusinessText').setValue(rec.get("otherRelatedBusiness"));
//            }
            if (rec.get("otherOperationMode")) {
                baseForm.findField('otherOperationMode').setValue(true);
                baseForm.findField('otherOperationModeText').enable();
                baseForm.findField('otherOperationModeText').setValue(rec.get("otherOperationMode"));
            }
            baseForm.findField('shopVersionId').store.load();
//            baseForm.findField('shopVersionId').store.loadData([
//                {value: rec.get("shopVersionName"), id: rec.get("shopVersionId")}
//            ]);
            if (rec.get("id")) {
                me.commonUtils.ajax({
                    url: 'shopManage.do?method=getShopDetail',
                    params: {
                        shopId: rec.get("id")
                    },
                    success: function (result) {
                        if (result['success']) {
                            me.setVehicleBrandModel(result['data'][9]);
                            if(!result['data'][10]) {
                                me.showServiceCategory(result['data'][8]);
                                me.down("[name=agentProductIds]").setValue(result['data'][11]);
                            } else {
                               me.down("[name=agentProduct]").hide();
                            }
                            if(result['data'][13] == 'ONE_TIME') {
                                me.down("[name=softPriceStr]").setValue("一口价收费" + me.down("[name=softPrice]").value);
                            } else if(result['data'][13] == 'YEARLY') {
                                me.down("[name=softPriceStr]").setValue("按年收费（第1年免费，之后每年年费1000元）");
                            }
                            me.showShopAuditLogs(result['data'][14]);
                            me.showContactFields(result['data'][0]);
                            me.setProductCategoryIds(result['data'][1]);
                            var scopeWin = me.shopbusinessscopewindow;
                            var vehicleBrandModelWin = me.shopvehiclebrandmodelwindow;
                            scopeWin.drawBusinessScopeTable(scopeWin.getShowBusinessScopeNames(result['data'][2]));
                            scopeWin.drawShopRegisterProductList(result['data'][3]);
                            me.showAllShopImageFields(result['data'][4]);
                            vehicleBrandModelWin.drawShowTable(vehicleBrandModelWin.getNames(result['data'][7]));
                            me.setVehicleModelIds(result['data'][12]);

                            var shopAdAreaScope = me.shopAdAreaScope;
                            var selectAdAreaIds = [];
                            var shopAdAreaDTOs = result['data'][15];
                            if(!Ext.isEmpty(shopAdAreaDTOs)){
                                for(var i=0;i<shopAdAreaDTOs.length;i++){
                                    selectAdAreaIds.push(shopAdAreaDTOs[i]["areaId"]);
                                }
                            }
                            me.setShopAdAreaIds(selectAdAreaIds);
                            shopAdAreaScope.drawShowTable(shopAdAreaScope.getNames(result['data'][16]));

                            var recommendTree = me.shopRecommendTree;
                            var shopRecommendDTOs = result['data'][17];
                            var recommendIds = [];
                            if(!Ext.isEmpty(shopRecommendDTOs)){
                                for(var i=0;i<shopRecommendDTOs.length;i++){
                                    recommendIds.push(shopRecommendDTOs[i]["recommendId"]);
                                }
                            }
                            me.setShopRecommendIds(recommendIds);
                            recommendTree.drawShowTable(recommendTree.getNames(result['data'][18]))
                        }
                    }
                });
            }
        } else {
            me.commonUtils.ajax({
                url: 'shopManage.do?method=getServiceCatrgory',
                success: function(result){
                   if(result && result.success) {
                       me.showServiceCategory(result.data);
                   }
                }
            });
        }
        this.show();
        this.commonUtils.unmask();
    },

    saveCustomer: function (callback, shopOperateScene, shopStatus) {
        var win = this,
            form = win.down("form"),
            formEl = form.getEl(),
            baseForm = form.form,
            serviceCategoryItems = win.down("[name=serviceCategory]"),
            shopVersionStr = win.down("[name=shopVersionStr]").value;
        if (baseForm.isValid()) {
            if (!win.getProductCategoryIds() || win.getProductCategoryIds().length == 0) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "经营产品不能为空！",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }
            if(serviceCategoryItems!= null && (!serviceCategoryItems.getChecked() || serviceCategoryItems.getChecked().length == 0) && shopVersionStr != 'WHOLESALER') {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "服务范围不能为空！",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }
            if(win.down('[id=oneTime]').checked && win.down('[name=softPrice]').getValue() == '') {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "软件销售价不能为空！",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }
            if(win.down('[id=oneTime]').checked && win.down('[name=softPrice]').getValue() == 0) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "软件销售价不能为0！",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }


            var params = baseForm.getValues();
            var adPricePerMonth = params["adPricePerMonth"];
            var adStartDateStr = params["adStartDateStr"];
            var adEndDateStr = params["adEndDateStr"];
            var productAdType = params["productAdType"];
            var shopAdAreaIds = win.getShopAdAreaIds();
            var recommendIds = win.getShopRecommendIds();
            if(!Ext.isEmpty(adPricePerMonth) || !Ext.isEmpty(adStartDateStr) || !Ext.isEmpty(adEndDateStr)){
                if(Ext.isEmpty(adPricePerMonth) || adPricePerMonth*1 <=0 || Ext.isEmpty(adStartDateStr)
                    || Ext.isEmpty(adEndDateStr) || Ext.isEmpty(productAdType)
                    || (productAdType == 'PART' && Ext.isEmpty(shopAdAreaIds))){
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: "请正确填写广告相关字段，若不需要广告请清空相关值！",
                        icon: Ext.MessageBox.INFO,
                        buttons: Ext.Msg.OK
                    });
                    return;
                }
                if(new Date(adStartDateStr).getTime() >= new Date(adEndDateStr).getTime() ){
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: "广告开始时间不允许大于结束时间！",
                        icon: Ext.MessageBox.INFO,
                        buttons: Ext.Msg.OK
                    });
                    return;
                }
            }



            if (shopOperateScene)params['scene'] = shopOperateScene;
            if (shopStatus)params['shopStatus'] = shopStatus;
            params['productCategoryIds'] = win.getProductCategoryIds();
            params['vehicleModelIds'] = win.getVehicleModelIds();
            params['shopAdAreaIds'] = win.getShopAdAreaIds();
            params['recommendIds'] = win.getShopRecommendIds();
            params = win.fillingInContact(params);
            if (!win.checkContact(params)) {
                formEl.unmask();
                return;
            }
            if ("SUBMIT_CLIENT_APPLICATION" == shopOperateScene) {
                if ((!this.getProducts() || this.getProducts().length == 0)) {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: "主营产品不能为空！",
                        icon: Ext.MessageBox.INFO,
                        buttons: Ext.Msg.OK
                    });
                    formEl.unmask();
                    return;
                } else {
                    win.submitProducts(params);
                }
            }
            win.mask();
            Ext.get("contentPanel").mask('正在保存 . . .');
            win.commonUtils.ajax({
                url: 'shopManage.do?method=saveShop',
                params: params,
                success: function (result) {
                    if (result.duplicate) {
                        Ext.get("contentPanel").unmask();
                        Ext.Msg.alert('返回结果', "店铺名称重复！", function () {
                            formEl.unmask();
                        });
                    } else if (result['success'] == false) {
                        Ext.get("contentPanel").unmask();
                        Ext.Msg.alert('返回结果', result['message'], function () {
                            formEl.unmask();
                        });
                    } else {
                        Ext.get("contentPanel").unmask();
                        Ext.Msg.alert('返回结果', "保存成功！", function () {
                            callback();
                            baseForm.reset();
                            formEl.unmask();
                            win.close();
                        });
                    }
                },
                failure: function () {
                    formEl.unmask();
                }
            });
            formEl.unmask();
            win.setProductCategoryIds(null);
        }
    },

    resetPwd: function () {
        var win = this,
            form = win.down("form"),
            formEl = form.getEl(),
            baseForm = form.form;
        var params = baseForm.getValues();
        Ext.Msg.confirm("确认重置密码？", "是否确定要重置密码？", function (opt) {
            if (opt == 'yes') {
                win.commonUtils.ajax({
                    url: 'shopManage.do?method=resetPassword',
                    params: params,
                    success: function (result) {
                        if (result) {
                            Ext.Msg.alert('返回结果', result['msg']);
                        }
                    }
                });
            }
        });
    },

    changeUserNo: function () {
        var win = this,
            form = win.down("form"),
            formEl = form.getEl(),
            baseForm = form.form;
        var params = baseForm.getValues();

        if (!form.down('[name=managerUserNo]').getValue()) {
            Ext.Msg.alert('返回结果', "账号为空！");
            return;
        }
        Ext.Msg.confirm("提示", "确认修改账号？", function (opt) {
            if (opt == 'yes') {
                win.commonUtils.ajax({
                    url: 'shopManage.do?method=changeUserNo',
                    params: params,
                    success: function (result) {
                        if (result) {
                            Ext.Msg.alert('返回结果', result['msg']);
                        }
                    }
                });
            }
        });
    },

    provinceSelectAction: function (combo, records) {
        var win = this, form = win.down('form').getForm(),
            regionSelect = win.down("regionSelect"),
            citySelect = win.down("citySelect");
        citySelect.setRawValue("");
        citySelect.setValue(null);
        regionSelect.setRawValue(null);
        regionSelect.setValue(null);
        citySelect.setProvince(records[0]);
        form.findField('address').setValue(records[0].get("name"));
    },

    provinceBeforeQuery: function (queryEvent) {
        queryEvent.combo.store.proxy.extraParams = {
            parentNo: "1"
        };
    },

    provinceBeforeRender: function (combo) {
        var win = this;
        if (win.getOperateType() == "update") {
            combo.store.proxy.extraParams = {
                parentNo: "1"
            };
            combo.store.load();
        }
    },

    citySelectAction: function (combo, records) {
        var win = this, form = win.down('form').getForm(),
            regionSelect = win.down("regionSelect");
        regionSelect.setRawValue(null);
        regionSelect.setValue(null);
        regionSelect.setCity(records[0]);
        form.findField('address').setValue(win.down("provinceSelect").getRawValue() + records[0].get("name"));
        win.commonUtils.ajax({
            url: 'shopManage.do?method=searchLicenseNo',
            params: {
                localArea: records[0].get("no")
            },
            success: function (result) {
                if (result.success) {
                    form.findField('licencePlate').setValue(result.plateCarNo);
                }
            }
        });
    },

    cityBeforeRender: function (combo) {
        var win = this, form = win.down("form");
        if (win.getOperateType() == "update") {
            if (form.getRecord() && form.getRecord().get("province")) {
                combo.store.load({params: {parentNo: form.getRecord().get("province")}});
            }
        }
    },

    cityBeforeQuery: function (queryEvent) {
        var win = this, form = win.down('form').getForm(),
            parentNo, rec = form.getRecord();
        if (!queryEvent.combo.getProvince()) {
            if (win.getOperateType() == "add") {
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
            parentNo: parentNo
        };
        queryEvent.combo.store.load();
    },

    regionSelectAction: function (combo, records) {
        var form = this.down('form').getForm();
        form.findField('address').setValue(this.down("provinceSelect").getRawValue() + this.down("citySelect").getRawValue() + records[0].get("name"));
    },

    regionBeforeQuery: function (queryEvent) {
        var form = this.down("form"), parentNo,
            rec = form.getRecord();
        if (this.getOperateType() == "add") {
            if (!queryEvent.combo.getCity()) {
                return false;
            }
            parentNo = queryEvent.combo.getCity().get("no");
        } else {
            if (queryEvent.combo.getCity()) {
                parentNo = queryEvent.combo.getCity().get("no");
            }
            if (!parentNo) {
                parentNo = rec.get("city");
                if (!parentNo)  return false;
            }
        }
        queryEvent.combo.store.proxy.extraParams = {
            parentNo: parentNo
        };
        queryEvent.combo.store.load();
    },

    regionBeforeRender: function (combo) {
        var form = this.down("form");
        if (this.getOperateType() == "update") {
            if (form.getRecord().get("city")) {
                combo.store.load({params: {parentNo: form.getRecord().get("city")}});
            }
        }
    },

    //增加待审核客户-经营方式-其他
    toggleOtherOperationModeFields: function (checkbox, newValue) {
        var win = this,
            form = win.down('form').getForm(),
            otherField = form.findField('otherOperationModeText');
        if (newValue) {
            otherField.enable();
        } else {
            form.findField('otherOperationModeText').setValue(null);
            otherField.disable();
        }
    },

    //增加待审核客户-相关业务-其他
    toggleOtherRelatedBusinessFields: function (checkbox, newValue) {
        var win = this,
            form = win.down('form').getForm(),
            otherField = form.findField('otherRelatedBusinessText');
        if (newValue) {
            otherField.enable();
        } else {
            form.findField('otherRelatedBusinessText').setValue(null);
            otherField.disable();
        }
    },

    //增加待审核客户-店面特色-其他
    toggleOtherFeatureFields: function (checkbox, newValue) {
        var win = this,
            form = win.down('form').getForm(),
            otherField = form.findField('otherFeatureText');
        if (newValue) {
            otherField.enable();
        } else {
            form.findField('otherFeatureText').setValue(null);
            otherField.disable();
        }
    },

    //增加待审核客户-店面特色-车型
    toggleVehicleBrandFields: function (checkbox, newValue) {
        var win = this,
            form = win.down('form').getForm(),
            otherField = form.findField('otherVehicleBrandText');
        if (newValue) {
            otherField.enable();
        } else {
            form.findField('otherVehicleBrandText').setValue(null);
            otherField.disable();
        }
    },

    //增加待审核客户-汽车装潢-其他
    toggleOtherBusinessFields: function (checkbox, newValue) {
        var win = this,
            form = win.down('form').getForm(),
            otherField = form.findField('otherBusinessScopeText');
        if (newValue) {
            otherField.enable();
        } else {
            form.findField('otherBusinessScopeText').setValue(null);
            otherField.disable();
        }
    },

    //增加待审核客户-主要产品
    toggleMajorProductFields: function (checkbox, newValue) {
        var win = this,
            form = win.down('form').getForm(),
            majorProductField = form.findField('majorProductText');
        if (newValue) { // if the "majorProduct" checkbox was checked
            majorProductField.enable();
        } else { // if the "majorProduct" checkbox was unchecked
            form.findField('majorProductText').setValue(null);
            majorProductField.disable();
        }
    },

    //联系人
    fillingInContact: function (param) {
        var win = this,
            form = win.down('form');
        param['contacts[0].name'] = form.down('[name=owner"]').getValue();
        param['contacts[0].mobile'] = form.down('[name=mobile"]').getValue();
        param['contacts[0].qq'] = form.down('[name=qq"]').getValue();
        param['contacts[0].email'] = form.down('[name=email"]').getValue();
        param['contacts[0].contactType'] = 'SHOP';
        param['contacts[0].isMainContact'] = 1;
        param['contacts[0].isShopOwner'] = 1;
        param['contacts[0].disabled'] = 1;
        param['contacts[0].level'] = 0;
        param['contacts[1].contactType'] = 'SHOP';
        param['contacts[1].isMainContact'] = 0;
        param['contacts[1].isShopOwner'] = 0;
        param['contacts[1].disabled'] = 1;
        param['contacts[1].level'] = 1;
        param['contacts[2].contactType'] = 'SHOP';
        param['contacts[2].isMainContact'] = 0;
        param['contacts[2].isShopOwner'] = 0;
        param['contacts[2].disabled'] = 1;
        param['contacts[2].level'] = 2;
        return param;
    },

    submitProducts: function (param) {
        var win = this,
            products = this.getProducts();
        for (var i = 0; i < products.length; i++) {
            param['productDTOs[' + i + '].commodityCode'] = products[i]['commodityCode'];
            param['productDTOs[' + i + '].name'] = products[i]['name'];
            param['productDTOs[' + i + '].brand'] = products[i]['brand'];
            param['productDTOs[' + i + '].model'] = products[i]['model'];
            param['productDTOs[' + i + '].spec'] = products[i]['spec'];
            param['productDTOs[' + i + '].productVehicleBrand'] = products[i]['productVehicleBrand'];
            param['productDTOs[' + i + '].productVehicleModel'] = products[i]['productVehicleModel'];
            param['productDTOs[' + i + '].sellUnit'] = products[i]['storageUnit'];
        }
        return param;
    },

    //提交前 检查联系人
    checkContact: function (param) {
        if ((param['contacts[0].mobile'] && param['contacts[1].mobile'] && param['contacts[0].mobile'] == param['contacts[1].mobile']) ||
            (param['contacts[1].mobile'] && param['contacts[2].mobile'] && param['contacts[1].mobile'] == param['contacts[2].mobile']) ||
            (param['contacts[2].mobile'] && param['contacts[0].mobile'] && param['contacts[0].mobile'] == param['contacts[2].mobile'])) {
            Ext.MessageBox.show({
                title: '提示',
                msg: "联系人手机号重复！",
                icon: Ext.MessageBox.INFO,
                buttons: Ext.Msg.OK
            });
            return false;
        }
//        if ((param['contacts[0].name'] && param['contacts[1].name'] && param['contacts[0].name'] == param['contacts[1].name']) ||
//            (param['contacts[1].name'] && param['contacts[2].name'] && param['contacts[1].name'] == param['contacts[2].name']) ||
//            (param['contacts[2].name'] && param['contacts[0].name'] && param['contacts[0].name'] == param['contacts[2].name'])) {
//            Ext.MessageBox.show({
//                title: '提示',
//                msg: "联系人名称重复！",
//                icon: Ext.MessageBox.INFO,
//                buttons: Ext.Msg.OK
//            });
//            return false;
//        }
        return true;
    },

    //展示联系人
    showContactFields: function (contacts) {
        var win = this, contact,
            form = win.down('form');
        if (!contacts || contacts.length == 0) return;
        contact = contacts[0];
        form.down('[name="owner"]').setValue(contact['name']);
        form.down('[name="mobile"]').setValue(contact['mobile']);
        form.down('[name="qq"]').setValue(contact['qq']);
        form.down('[name="email"]').setValue(contact['email']);
        form.down('[itemId="contacts0.id"]').setValue(contact['id']);

        form.down('[itemId="contacts1.name"]').setValue(null);
        form.down('[itemId="contacts1.mobile"]').setValue(null);
        form.down('[itemId="contacts1.qq"]').setValue(null);
        form.down('[itemId="contacts1.email"]').setValue(null);
        form.down('[itemId="contacts1.id"]').setValue(null);

        form.down('[itemId="contacts2.name"]').setValue(null);
        form.down('[itemId="contacts2.mobile"]').setValue(null);
        form.down('[itemId="contacts2.qq"]').setValue(null);
        form.down('[itemId="contacts2.email"]').setValue(null);
        form.down('[itemId="contacts2.id"]').setValue(null);

        if (contacts.length > 1) {
            contact = contacts[1];
            form.down('[itemId="contacts1.name"]').setValue(contact['name']);
            form.down('[itemId="contacts1.mobile"]').setValue(contact['mobile']);
            form.down('[itemId="contacts1.qq"]').setValue(contact['qq']);
            form.down('[itemId="contacts1.email"]').setValue(contact['email']);
            form.down('[itemId="contacts1.id"]').setValue(contact['id']);
        }
        if (contacts.length > 2) {
            contact = contacts[2];
            form.down('[itemId="contacts2.name"]').setValue(contact['name']);
            form.down('[itemId="contacts2.mobile"]').setValue(contact['mobile']);
            form.down('[itemId="contacts2.qq"]').setValue(contact['qq']);
            form.down('[itemId="contacts2.email"]').setValue(contact['email']);
            form.down('[itemId="contacts2.id"]').setValue(contact['id']);
        }
    },
    //展示图片
    showAllShopImageFields: function (imageCenterDTO) {
        var win = this,
            form = win.down('form');
        if (!imageCenterDTO) return;
        if(imageCenterDTO.shopBusinessLicenseImageDetailDTO){
            form.down('[itemId="imageCenterDTO.shopBusinessLicenseImagePath"]').setValue(imageCenterDTO.shopBusinessLicenseImageDetailDTO['imagePath']);
            form.down('[itemId="shopBusinessLicenseImageBrowse"]').getEl().dom.src = imageCenterDTO.shopBusinessLicenseImageDetailDTO['imageURL'];
        }
        if(imageCenterDTO.shopImageDetailDTOs && imageCenterDTO.shopImageDetailDTOs.length>0){
            Ext.Array.forEach(imageCenterDTO.shopImageDetailDTOs,function(shopImageDetailDTO,index,array){ //单纯的遍历数组
                form.down('[itemId="imageCenterDTO.shopImagePaths'+index+'"]').setValue(shopImageDetailDTO['imagePath']);
                form.down('[itemId="shopImageBrowse'+index+'"]').getEl().dom.src = shopImageDetailDTO['imageURL'];
            });

        }
    },
    //服务范围
    showServiceCategory: function(nodes) {
      if(!nodes || nodes.length == 0)
          return;
      var win = this,i=0;
//      var container = win.down("[name=serviceCategory]");
        var container = {
            xtype: 'checkboxgroup',
            fieldLabel: '服务范围',
            padding: 0,
//            margin: "0 5 0 -5",
            margin: "0",
            name: "serviceCategory",
            anchor: '100%',
            labelWidth: 80,
            width: 400,
            columns:11,
            items: [

            ]
        };
      Ext.Array.forEach(nodes, function(node,index,array){
          if(node['hasThisNode'] != "") {
//              container.add({
//                  boxLabel: node['text'],
//                  name: 'serviceCategoryIds',
//                  inputValue: node['idStr'],
//                  checked:true
//              });
              container.items[i] = {
                  boxLabel: node['text'],
                  name: 'serviceCategoryIds',
                  inputValue: node['idStr'],
                  checked:true
              };
          } else {
//              container.add({
//                  boxLabel: node['text'],
//                  name: 'serviceCategoryIds',
//                  inputValue: node['idStr']
//              });
              container.items[i] = {
                  boxLabel: node['text'],
                  name: 'serviceCategoryIds',
                  inputValue: node['idStr'],
                  checked:false
              };
          }
          i++;
      });
//        container.doLayout();
//        win.down("form").down('[name=shopBaseInfo]').add(container);
        win.down("form").down('[name=serviceCategoryFieldset]').add(container);
//        win.down("[name=serviceCategory]").setValue(container);
    },

    //主营车型初始化
    setVehicleBrandModel: function(flag){
        var win = this,
            allModel = win.down("[id=allModel]"),
            partVehicle = win.down("[id=partVehicle]");
        if(flag == "ALL_MODEL") {
           allModel.setValue(true);
        } else if(flag == "PART_MODEL") {
            partVehicle.setValue(true);
        } else {
            allModel.setValue(false);
            partVehicle.setValue(false);
        }
    },
    //审核记录
    showShopAuditLogs: function (shopAuditLogList) {
        var win = this;
        if (!shopAuditLogList || shopAuditLogList.length == 0) return;
        var container = win.down("[name=auditInfo]");
        Ext.Array.forEach(shopAuditLogList, function(shopAuditLog,index,array){
            container.add({
                xtype: 'fieldset',
                layout: 'hbox',
                anchor: '100%',
                padding: 0,
                margin: "0 5 0 0",
                defaults: {
                    xtype: "displayfield",
                    anchor: "100%",
                    margin: "0 10 5 0",
                    width: 200,
                    labelWidth: 70
                },
                border: false,
                items: [
                    {
                        name: 'auditTimeStr',
                        value: (shopAuditLog['auditTimeStr'] ? shopAuditLog['auditTimeStr'] : "--")
                    },
                    {
                        name: 'auditorName',
                        value: shopAuditLog['auditorName'] ? shopAuditLog['auditorName'] : "--"
                    },
                    {
                        name: 'auditStatus',
                        value: "审核通过"
                    }
                ]
            });
            container.add({
                xtype: 'fieldset',
                layout: 'hbox',
                anchor: '100%',
                padding: 0,
                margin: "0 5 0 0",
                defaults: {
                    xtype: "displayfield",
                    anchor: "100%",
                    margin: "0 10 5 0",
                    labelWidth: 70
                },
                border: false,
                items: [
                    {
                        name: 'reason',
                        fieldLabel: "审核备注",
                        value: shopAuditLog['reason'] ? shopAuditLog['reason'] : "--"
                    }
                ]
            });
        });
    }
});