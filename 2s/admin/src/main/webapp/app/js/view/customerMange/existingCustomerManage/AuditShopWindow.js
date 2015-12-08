/**
 * 审核窗口
 * @author zhangjuntao
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.AuditShopWindow', {
    alias: 'widget.auditShopWindow',
    extend: 'Ext.window.Window',
    iconCls: 'icon-user',
    layout: 'fit',
    collapsible: true,
    width: 1000,
    height: 450,
    title: '注册审核',
    shopbusinessscopewindow: Ext.create("Ext.view.customerMange.ShopBusinessScopeWindow"),
    shopvehiclebrandmodelwindow: Ext.create("Ext.view.customerMange.ShopVehicleBrandModelWindow"),
    requires: [
        'Ext.view.dataMaintenance.permission.ShopVersionSelect'
    ],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.shopbusinessscopewindow.setBusinessScopeTarget(me);
        me.shopvehiclebrandmodelwindow.setOpenTarget(me);
        me.commonUtils = Ext.create("Ext.utils.Common");
        var container = Ext.widget({
            xtype: 'fieldset',
            title: '店铺联系信息（负责人/店主的联系手机号将作为注册店铺的账号）',
            layout: 'anchor',
            name: "contacts",
            margin: "10 0 0 0",
            defaults: {
                anchor: '100%',
                labelStyle: 'padding-left:4px;'
            },
            collapsible: true,
            collapsed: false,
            items: [
            ]
        });
        Ext.apply(me, {
            items: {
                xtype: 'form',
                bodyPadding: 5,
                width: 1000,
                height: 450,
                autoScroll: true,
                frame: false,
                border: false,
                buttons: [
                    {
                        text: '确定',
                        action: 'save'
                    },
                    {
                        text: '取消',
                        tooltip: "取消",
                        handler: function () {
                            me.close();
                        }
                    }
                ],
                items: [
                    {
                        xtype: 'fieldset',
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 200,
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        name: 'name',
                                        fieldLabel: '单位名称'
                                    },
                                    {
                                        name: 'shortname',
                                        fieldLabel: '店铺简称'
                                    },
                                    {
                                        name: 'licencePlate',
                                        fieldLabel: "车牌前缀"
                                    },
                                    {
                                        name: 'landline',
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 200,
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        name: 'storeManager',
                                        fieldLabel: '店面管理员'
                                    },
                                    {
                                        fieldLabel: '联系电话',
                                        name: 'storeManagerMobile'
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        name: 'address',
                                        fieldLabel: '详细地址',
                                        width: 410
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        fieldLabel: '服务范围',
                                        width: 'auto',
                                        name: 'serviceCategory'
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        fieldLabel: '主营车型',
                                        width: 'auto',
                                        name: 'vehicleModelIds'
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        fieldLabel: '代理产品',
                                        width: 'auto',
                                        name: 'agentProduct'
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        fieldLabel: '经营产品',
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                    {
                                        fieldLabel: '商品信息',
                                        width: 'auto',
                                        name: 'productsInfo'
                                    }
                                ]
                            }
                        ]
                    },
                    container,
                    {
                        xtype: 'fieldset',
                        title: "店铺其他信息",
                        bodyStyle: "backgroundColor:#DFE8F6;",
                        layout: "hbox",
                        collapsible: true,
                        collapsed: false,
                        defaults: {
                            anchor: '100%',
                            labelStyle: 'padding-left:4px;',
                            columnWidth: .5
                        },
                        items: [
                            {
                                xtype: 'displayfield',
                                name: 'operationMode',
                                fieldLabel: '经营方式',
                                labelWidth: 70,
                                width: 400
                            },
                            {
                                xtype: 'displayfield',
                                name: 'url',
                                margin: "0 0 0 20",
                                fieldLabel: '网址',
                                labelWidth: 70,
                                width: 300
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
                                    xtype: "displayfield",
                                    anchor: "100%",
                                    margin: "0 10 5 0",
                                    width: 200,
                                    labelWidth: 70
                                },
                                border: false,
                                items: [
                                   /* {
                                        name: 'shopVersionName',
                                        fieldLabel: "软件版本"
                                    },*/
                                    {
                                        xtype: "permissionShopVersionSelect",
                                        name: 'shopVersionId',
                                        displayField: 'value',
                                        valueField: 'id',
                                        allowBlank: false,
                                        fieldLabel: "软件版本"
                                    },

                                    {
                                        name: 'followName',
                                        fieldLabel: '跟进人'
                                    },
                                    {
                                        name: 'agent',
                                        fieldLabel: '销售人'
                                    },
                                    {
                                        name: 'shopKind',
                                        fieldLabel: '店铺种类'
                                    }
                                ]
                            },
                            {
                                xtype: 'radiogroup',
                                fieldLabel: '软件销售价',
                                layout: 'hbox',
                                anchor: '80%',
                                padding: 0,
                                margin: "0 0 0 -5",
                                border: false,
                                labelWidth: 70,
                                defaults: {
                                    anchor: "100%",
                                    margin: "0 0 5 0",
                                    labelWidth: 40
                                },
                                items: [
                                    {boxLabel: '一口价收费', name: 'chargeType', inputValue: "ONE_TIME",width:80,id:'oneTime'},
                                    {
                                        xtype: "displayfield",
                                        name: 'softPrice',
                                        labelWidth: 30,
                                        border:false,
                                        margin: '0 30 0 0',
                                        width:70
                                    },
                                    {boxLabel: '按年收费（第1年免费，之后每年年费1000元）', name: 'chargeType', inputValue: "YEARLY",id:'yearly'},
                                    {
                                        xtype: "hiddenfield",
                                        hidden: true,
                                        name: 'hiddenChargeType'
                                    }

                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: '营业执照',
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
                        title: '店铺照片',
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
                    },
                    {
                        xtype: 'fieldset',
                        title: "审核信息",
                        layout: "anchor",
                        collapsible: true,
                        itemId:"auditShopArea",
                        collapsed: false,
                        defaults: {
                            anchor: '100%',
                            labelStyle: 'padding-left:4px;',
                            columnWidth: 1
                        },
                        items: [
                            {   xtype: 'container',
                                layout: "anchor",
                                collapsible: true,
                                collapsed: false,
                                items: {
                                    xtype: 'radiogroup',
                                    fieldLabel: '审核结果',
                                    labelWidth: 70,
                                    width: 200,
                                    columns: 2,
                                    items: [
                                        {boxLabel: '通过', name: 'auditStatus', inputValue: 'AGREE', checked: true, width: 60},
                                        {boxLabel: '不通过', name: 'auditStatus', inputValue: 'DISAGREE', width: 80}
                                    ]
                                }
                            },
                            {
                                xtype: 'textareafield',
                                name: 'reason',
                                fieldLabel: '理由',
                                labelWidth: 70,
                                allowBlank: false
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
                        name: 'shopId',
                        xtype: "hiddenfield"
                    }
                ]
            }
        });
        me.callParent();
    },

    close: function () {
        this.commonUtils.unmask();
        this.shopbusinessscopewindow.close();
        this.shopvehiclebrandmodelwindow.close();
        this.doClose();
    },

    audit: function (callback) {
        var baseForm = this.down('form').form;
        if (baseForm.isValid()) {
            var params = baseForm.getValues();
            var me = this;
            if(Ext.isEmpty(params['chargeType'])){
                Ext.Msg.alert('警告', "请选择软件销售价！");
                return;
            }
            if(params['hiddenChargeType'] == 'YEARLY' && params['chargeType'] == 'ONE_TIME') {
                Ext.Msg.alert("友情提示",'按年收费已经改为一口价收费' + me.down("[name=softPrice]").value + '元，若需要修改收费金额，请进行议价操作！ ',function(){
                    Ext.getBody().mask('审核处理中....');
                    me.commonUtils.ajax({
                        url: 'shopManage.do?method=activateShop',
                        params: params,
                        success: function (result) {
                            if (result.success) {
                                Ext.Msg.alert('返回结果', result.message,function () {
                                    callback();
                                    Ext.getBody().unmask();
                                }).getEl().setStyle('z-index', '80000');
                            } else {
                                Ext.Msg.alert("警告", result.message,function () {
                                    Ext.getBody().unmask();
                                }).getEl().setStyle('z-index', '80000');
                            }
                        }
                    });
                });
                return;
            }
            Ext.getBody().mask('审核处理中....');
            me.commonUtils.ajax({
                url: 'shopManage.do?method=activateShop',
                params: params,
                success: function (result) {
                    if (result.success) {
                        Ext.Msg.alert('返回结果', result.message,function () {
                            callback();
                            Ext.getBody().unmask();
                        }).getEl().setStyle('z-index', '80000');
                    } else {
                        Ext.Msg.alert("警告", result.message,function () {
                            Ext.getBody().unmask();
                        }).getEl().setStyle('z-index', '80000');
                    }
                }
            });
        }
    },

    showAuditShopDetail: function (rec) {
        var me = this, form = this.down('form'),
            baseForm = form.form;
        this.commonUtils.mask();
        if (rec) {
            form.loadRecord(rec);
            form.down('[name=shopId]').setValue(rec.get("id"));
            form.down('[name=shopKind]').setValue(rec.get("shopKind") == "OFFICIAL" ? "正式店" : "测试店");
            if (rec.get("otherOperationMode")) {
                baseForm.findField('operationMode').setValue(rec.get("otherOperationMode"));
            }
            if (rec.get("id")) {
                me.commonUtils.ajax({
                    url: 'shopManage.do?method=getShopDetail',
                    params: {
                        shopId: rec.get("id")
                    },
                    success: function (result) {
                        if (result['success']) {
                            if(!result['data'][10]) {
                                me.showServiceCategory(result['data'][6]);
                                if(result['data'][11]) {
                                    me.down("[name=agentProduct]").setValue("OBD");
                                } else {
                                    me.down("[name=agentProduct]").setValue("--");
                                }
                            } else {
                                me.down("[name=agentProduct]").hide();
                                me.down("[name=serviceCategory]").hide();
                            }
                            if(result['data'][13] == 'ONE_TIME') {
                                me.down("[id=oneTime]").setValue(true);
                                me.down("[name=hiddenChargeType]").setValue('ONE_TIME');
                            } else if(result['data'][13] == 'YEARLY') {
                                me.down("[id=yearly]").setValue(true);
                                me.down("[name=hiddenChargeType]").setValue('YEARLY');
                            }
                            me.showContactFields(result['data'][0]);
                            var scopeWin = me.shopbusinessscopewindow;
                            var vehicleBrandModelWin = me.shopvehiclebrandmodelwindow;
                            scopeWin.drawBusinessScopeTable(scopeWin.getShowBusinessScopeNames(result['data'][2]));
                            scopeWin.drawShopRegisterProductList(result['data'][3]);
                            me.showAllShopImageFields(result['data'][4]);
                            me.showShopAuditLogs(result['data'][5]);
                            me.showVehicleModelName(vehicleBrandModelWin, result);
                            me.setDefaultValue();
                        }
                    }
                });
            }
        }

        this.show();
        this.commonUtils.unmask();
    },

    showContactFields: function (contacts) {
        var win = this, contact,
            form = win.down('form');
        if (!contacts || contacts.length == 0) return;
        var container = win.down("[name=contacts]");
        contact = contacts[0];
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
                    fieldLabel: '负责人/店主',
                    name: 'owner',
                    value: (contact['name'] ? contact['name'] : "--")
                },
                {
                    name: 'mobile',
                    fieldLabel: '联系手机',
                    value: contact['mobile'] ? contact['mobile'] : "--"
                },
                {
                    name: 'qq',
                    fieldLabel: "QQ",
                    value: contact['qq'] ? contact['qq'] : "--"
                },
                {
                    name: 'email',
                    maxLength: 50,
                    fieldLabel: "Email",
                    value: contact['email'] ? contact['email'] : "--"
                }
            ]
        });
        if (contacts.length > 1) {
            contact = contacts[1];
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
                    labelWidth: 70,
                    readOnly: true
                },
                border: false,
                items: [
                    {
                        fieldLabel: '联系人',
                        name: 'contacts[1].name',
                        itemId: 'contacts1.name',
                        value: contact['name'] ? contact['name'] : "--"
                    },
                    {
                        name: 'contacts[1].mobile',
                        itemId: 'contacts1.mobile',
                        fieldLabel: '联系手机',
                        value: contact['mobile'] ? contact['mobile'] : "--"
                    },
                    {
                        name: 'contacts[1].qq',
                        itemId: 'contacts1.qq',
                        fieldLabel: "QQ",
                        value: contact['qq'] ? contact['qq'] : "--"
                    },
                    {
                        name: 'contacts[1].email',
                        maxLength: 50,
                        itemId: 'contacts1.email',
                        fieldLabel: "Email",
                        value: contact['email'] ? contact['email'] : "--"
                    }
                ]
            });
        }
        if (contacts.length > 2) {
            contact = contacts[2];
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
                    labelWidth: 70,
                    readOnly: true
                },
                border: false,
                items: [
                    {
                        fieldLabel: '联系人',
                        name: 'contacts[2].name',
                        itemId: 'contacts2.name',
                        value: contact['name'] ? contact['name'] : "--"
                    },
                    {
                        name: 'contacts[2].mobile',
                        itemId: 'contacts2.mobile',
                        fieldLabel: '联系手机',
                        value: contact['mobile'] ? contact['mobile'] : "--"
                    },
                    {
                        name: 'contacts[2].qq',
                        itemId: 'contacts2.qq',
                        fieldLabel: "QQ",
                        value: contact['qq'] ? contact['qq'] : "--"
                    },
                    {
                        name: 'contacts[2].email',
                        maxLength: 50,
                        itemId: 'contacts2.email',
                        fieldLabel: "Email",
                        value: contact['email'] ? contact['email'] : "--"
                    }
                ]
            })
        }
    },

    setDefaultValue: function () {
        var fields = this.down('form').form.getFields().items;
        for (var i = 0; i < fields.length; i++) {
            if (!fields[i].getValue()&&"reason,".indexOf(fields[i].getName())==-1) {
                fields[i].setValue("--")
            }
        }
    },
    //展示图片
    showAllShopImageFields: function (imageCenterDTO) {
        var win = this,
            form = win.down('form');
        if (!imageCenterDTO) return;
        if(imageCenterDTO.shopBusinessLicenseImageDetailDTO){
            form.down('[itemId="shopBusinessLicenseImageBrowse"]').getEl().dom.src = imageCenterDTO.shopBusinessLicenseImageDetailDTO['imageURL'];
        }
        if(imageCenterDTO.shopImageDetailDTOs && imageCenterDTO.shopImageDetailDTOs.length>0){
            Ext.Array.forEach(imageCenterDTO.shopImageDetailDTOs,function(shopImageDetailDTO,index,array){ //单纯的遍历数组
                form.down('[itemId="shopImageBrowse'+index+'"]').getEl().dom.src = shopImageDetailDTO['imageURL'];
            });

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
                        value: "审核拒绝"
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
                        fieldLabel: "理由",
                        value: shopAuditLog['reason'] ? shopAuditLog['reason'] : "--"
                    }
                ]
            });
        });
    },
    //服务范围
    showServiceCategory: function(value){
        var win = this;
        var container = win.down("[name=serviceCategory]");
        container.setValue(value);
    },
    showVehicleModelName: function(target, result){
        var me = this;
        if("PART_MODEL" == result['data'][9]) {
            var names = target.getNames(result['data'][7]);
            if(names.length > 0) {
                target.drawShowTable(names);
            } else {
                me.down("[name=vehicleModelIds]").setValue("--");
            }

        } else if("ALL_MODEL" == result['data'][9]) {
            me.down("[name=vehicleModelIds]").setValue("全部车型");
        } else {
            me.down("[name=vehicleModelIds]").setValue("--");
        }
    }
});

