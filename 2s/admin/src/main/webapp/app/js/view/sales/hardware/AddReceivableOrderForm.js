    Ext.define('Ext.view.sales.hardware.AddReceivableOrderForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 700,
    alias: 'widget.addReceivableOrderForm',
    layout: 'anchor',
    height:400,
    autoScroll: true,
    defaults: {
        anchor: '100%'
    },
    fieldDefaults: {
        labelWidth: 125,
        msgTarget: 'side',
        autoFitErrors: false
    },

    buttons: [
        {
            text: '重置',
            tooltip: "重置",
            handler: function () {
                this.up("form").form.reset();
            }
        },
        {
            text: '保存',
            action: 'save'
        }
    ],
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.customerMange.existingCustomerManage.Select",
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.ProvinceSelect"
    ],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.addEvents('create');
        Ext.apply(me, {
            items: [
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    padding: 0,
                    anchor: '100%',
                    border: false,
                    items: [
                        {
                            fieldLabel: '购买店铺',
                            labelWidth: 70,
                            labelAlign:'right',
                            width: 250,
                            anchor: '50%',
                            msgTarget: 'under',
                            xtype: "shopSelect",
                            allowBlank: false,
                            store: Ext.create('Ext.store.customerMange.Shops', {
                                pageSize: 15,
                                proxy: {
                                    extraParams: {
                                        shopStatuses: "REGISTERED_TRIAL,REGISTERED_PAID"
                                    },
                                    type: 'ajax',
                                    api: {
                                        read: 'shopManage.do?method=getShopSuggestionByName'
                                    },
                                    reader: {
                                        type: 'json',
                                        root: "results",
                                        totalProperty: "totalRows"
                                    }
                                }}),
                            name: 'shopId',
                            listeners: {
                                blur: function (comp, e, eOpts) {
                                    if (comp.getRawValue() === comp.getValue()) {
                                        me.commonUtils.ajax({
                                            url: 'shopManage.do?method=getActiveUsingShopByName',
                                            params: {shopName: comp.getValue()},
                                            success: function (result) {
                                                if (!result) {
                                                    Ext.Msg.alert('返回结果', " 您输入的店铺名不正确！", function () {
                                                        me.down("button[action=save]").disable();
                                                    });
                                                } else {
                                                    comp.store.loadData([
                                                        {name: comp.getValue(), id: result['id']}
                                                    ]);
                                                    comp.setValue(result['id']);
                                                    me.down("button[action=save]").enable();

                                                    me.down('[name=contact]').setValue(Ext.isEmpty(result['owner'])?'':result['owner']);
                                                    me.down('[name=mobile]').setValue(Ext.isEmpty(result['mobile'])?'':result['mobile']);
                                                    if(!Ext.isEmpty(result['province'])){
                                                        me.down("provinceSelect").store.load({params:{parentNo:'1'}});
                                                        me.down("provinceSelect").setValue(result['province']);
                                                        me.down("citySelect").store.load({params:{parentNo:result['province']}});
                                                        if(!Ext.isEmpty(result['city'])){
                                                            me.down("citySelect").setValue(result['city']);
                                                            me.down("regionSelect").store.load({params:{parentNo:result['city']}});
                                                        }else{
                                                            me.down("citySelect").setValue(null);
                                                            me.down("regionSelect").setValue(null);
                                                        }
                                                        me.down("regionSelect").setValue(Ext.isEmpty(result['region'])?'':result['region']);
                                                    }else{
                                                        me.down("provinceSelect").setValue(null);
                                                        me.down("citySelect").setValue(null);
                                                        me.down("regionSelect").setValue(null);
                                                    }
                                                    me.down('[name=address]').setValue(Ext.isEmpty(result['address'])?'':result['address']);
                                                }
                                            }
                                        });
                                    } else {
                                        me.down("button[action=save]").enable();
                                    }
                                },
                                select: function (combo, record, index) {
                                    me.down('[name=contact]').setValue(Ext.isEmpty(record[0].data['owner'])?'':record[0].data['owner']);
                                    me.down('[name=mobile]').setValue(Ext.isEmpty(record[0].data['mobile'])?'':record[0].data['mobile']);

                                    if(!Ext.isEmpty(record[0].data['province'])){
                                        me.down("provinceSelect").store.load({params:{parentNo:'1'}});
                                        me.down("provinceSelect").setValue(record[0].data['province']);
                                        me.down("citySelect").store.load({params:{parentNo:record[0].data['province']}});
                                        if(!Ext.isEmpty(record[0].data['city'])){
                                            me.down("citySelect").setValue(record[0].data['city']);
                                            me.down("regionSelect").store.load({params:{parentNo:record[0].data['city']}});
                                        }else{
                                            me.down("citySelect").setValue(null);
                                            me.down("regionSelect").setValue(null);
                                        }
                                        me.down("regionSelect").setValue(Ext.isEmpty(record[0].data['region'])?'':record[0].data['region']);
                                    }else{
                                        me.down("provinceSelect").setValue(null);
                                        me.down("citySelect").setValue(null);
                                        me.down("regionSelect").setValue(null);
                                    }
                                    me.down('[name=address]').setValue(Ext.isEmpty(record[0].data['address'])?'':record[0].data['address']);
                                }
                            }
                        },
                        {
                            fieldLabel: '购买日期',
                            labelAlign:'right',
                            labelWidth: 70,
                            xtype: "datefield",
                            maxValue : new Date(),
                            format: 'Y-m-d',
                            width: 200,
                            value: new Date(),
                            editable :false,
                            allowBlank: false,
                            name: 'createdTimeStr'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    padding: 0,
                    anchor: '100%',
                    border: false,
                    items: [
                        {
                            fieldLabel:'收货地址',
                            labelWidth:70,
                            labelAlign:'right',
                            width:200,
                            margin:"0 10 0 0",
                            name:'province',
                            allowBlank:false,
                            xtype:"provinceSelect"
                        },
                        {
                            width:120,
                            margin:"0 10 0 0",
                            name:'city',
                            allowBlank: false,
                            xtype:"citySelect"
                        },
                        {
                            width:120,
                            margin:"0 10 0 0",
                            xtype:"regionSelect",
                            allowBlank: false,
                            name:'region'
                        },
                        {
                            width:180,
                            margin:"0 0 0 0",
                            enforceMaxLength:true,
                            maxLength:30,
                            xtype:"textfield",
                            allowBlank: false,
                            name:'address'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    padding: 0,
                    anchor: '100%',
                    border: false,
                    items: [
                        {
                            fieldLabel: '收货人',
                            labelAlign:'right',
                            labelWidth: 70,
                            xtype: "textfield",
                            width: 200,
                            allowBlank: false,
                            name: 'contact'
                        },
                        {
                            fieldLabel: '手机号',
                            labelAlign:'right',
                            labelWidth: 70,
                            xtype: "textfield",
                            width: 200,
                            vtype:"mobilePhone",
                            allowBlank:false,
                            enforceMaxLength:true,
                            maxLength:11,
                            name: 'mobile'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    name: "bcgogoProductFieldset",
                    frame:false,
                    padding:0,
                    margin: "0 5 0 0",
                    border: false
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: '10 0 5 0',
                    border: false,
                    items: [
                        {
                            fieldLabel: '合计',
                            anchor: '60%',
                            labelWidth: 70,
                            width: 140,
                            xtype: 'textfield',
                            name: 'totalAmount',
                            readOnly: true
                        }
                    ]
                }
            ]
        });
        this.callParent();

    },
    listeners: {
        beforerender: function () {
            this.generateBcgogoProductDetailGroup();
        }
    },
    toggleReaderFields: function (checkbox, newValue) {
        var productPropertyId = checkbox.inputValue,
            amountField = this.down('[itemKey=amount'+productPropertyId+']');
        if (newValue) {
            amountField.enable();
        } else {
            amountField.setValue(0);
            amountField.disable();
        }
        this.calculateTotal(this,productPropertyId);
    },
    calculateTotal: function (me,productPropertyId) {
        me = me ? me : this;
        var amountField = me.down('[itemKey=amount'+productPropertyId+']'),
            priceField = me.down('[itemKey=price'+productPropertyId+']'),
            itemTotalField = me.down('[itemKey=itemTotal'+productPropertyId+']');
        itemTotalField.setValue((Ext.isNumber(amountField.getValue()*1)?amountField.getValue():0)*priceField.getValue());
        var total = 0.0;
        Ext.Array.forEach(me.query('[itemAllKey=itemTotal]'), function(domObject,index,array){
            total+=domObject.getValue()*1;
        });
        me.down('[name=totalAmount]').setValue(total);
    },
    save: function (form, callback) {
        var baseForm = form.form;
        if (baseForm.isValid()) {
            if(baseForm.findField('totalAmount').getValue()*1<=0){
                Ext.Msg.alert('返回结果', " 请选择购买商品！");
                return;
            }
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            form.commonUtils.ajax({
                url: 'bcgogoReceivable.do?method=createBcgogoHardwareReceivableDetail',
                params: params,
                success: function (result) {
                    if(result.success){
                        Ext.Msg.alert('返回结果', "保存成功！", function () {
                            baseForm.reset();
                            callback();
                        });
                    }else{
                        Ext.Msg.alert('返回结果', "保存失败！", function () {
                            baseForm.reset();
                            callback();
                        });
                    }

                },
                failure: function () {
                    form.unmask();
                }
            });
        }
    },
    generateBcgogoProductDetailGroup: function () {
        var list = this;
        list.commonUtils.ajax({
            async:false,
            url: 'bcgogoReceivable.do?method=getAllBcgogoProduct',
            params:{isSimple:false},
            success: function (result) {
                if(!result.data){
                    return;
                }
                var win = this,i=0;
                var container = {
                    xtype: 'fieldset',
                    layout: 'anchor',
                    anchor: '100%',
                    padding: '0px 0px 0px 10px',
                    title: '硬件明细',
                    defaults:{
                        anchor:'100%'
                    },
                    collapsible:false,
                    collapsed:false,
                    items: []
                };

                Ext.Array.forEach(result.data, function(bcgogoProductDTO,index,array){
                    var productContainer = {
                        xtype: 'fieldset',
                        layout: 'anchor',
                        anchor: '90%',
                        padding: '0px 0px 0px 10px',
                        margin: '10 10 5 0',
                        title: bcgogoProductDTO['name'],
                        defaults:{
                            anchor:'100%'
                        },
                        collapsible:false,
                        collapsed:false,
                        items: []
                    };
                    Ext.Array.forEach(bcgogoProductDTO.propertyDTOList, function(bcgogoProductPropertyDTO,index,array){
                        var boxLabelText = bcgogoProductPropertyDTO['kind'];
                        if(!Ext.isEmpty(bcgogoProductPropertyDTO['type'])){
                            boxLabelText +='【'+bcgogoProductPropertyDTO['type']+'】';
                        }
                        productContainer.items[index] = {
                            xtype: 'fieldset',
                            layout: 'hbox',
                            padding: '0',
                            margin: '0 0 2 20',
                            border: false,
                            items: [
                                {
                                    name: 'bcgogoReceivableOrderItemDTOList['+i+'].productPropertyId',
                                    value:bcgogoProductPropertyDTO['idStr'],
                                    xtype: "hiddenfield"
                                },
                                {
                                    name: 'bcgogoReceivableOrderItemDTOList['+i+'].productId',
                                    value:bcgogoProductDTO['idStr'],
                                    xtype: "hiddenfield"
                                },
                                {
                                    xtype: 'checkbox',
                                    labelWidth:200,
                                    width:200,
                                    name:'bcgogoProductPropertyCheckboxGroup',
                                    boxLabel: boxLabelText,
                                    inputValue:bcgogoProductPropertyDTO['idStr'],
                                    margin: '0 20 0 0',
                                    scope: list,
                                    handler: list.toggleReaderFields
                                },
                                {
                                    xtype: 'displayfield',
                                    name: 'bcgogoReceivableOrderItemDTOList['+i+'].price',
                                    itemKey:'price'+bcgogoProductPropertyDTO['idStr'],
                                    value: bcgogoProductPropertyDTO['price']
                                },
                                {
                                    xtype: 'displayfield',
                                    value: ' 元/'+bcgogoProductDTO['unit']+','
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'bcgogoReceivableOrderItemDTOList['+i+'].amount',
                                    itemKey:'amount'+bcgogoProductPropertyDTO['idStr'],
                                    vtype: 'positiveInteger',
                                    maxLength: 5,
                                    width:60,
                                    value:0,
                                    enforceMaxLength: true,
                                    margin: '0 0 0 0',
                                    disabled: true,
                                    listeners: {
                                        keyup: {
                                            element: 'el',
                                            fn: function (cmp, e, eOpts) {
                                                list.calculateTotal(list,bcgogoProductPropertyDTO['idStr']);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    value: bcgogoProductDTO['unit']+',小计'
                                },
                                {
                                    xtype: 'textfield',
                                    name:'bcgogoReceivableOrderItemDTOList['+i+'].itemTotal',
                                    itemKey:'itemTotal'+bcgogoProductPropertyDTO['idStr'],
                                    itemAllKey:'itemTotal',
                                    value:'0',
                                    width: 80,
                                    readOnly: true
                                },
                                {
                                    xtype: 'displayfield',
                                    value: '元'
                                }
                            ]
                        };
                        i++;
                    });
                    container.items[index] = productContainer;
                });
                list.down('[name=bcgogoProductFieldset]').add(container);
            }
        });
    }
});