Ext.define('Ext.view.sales.hardware.HardwareOrderItemChangePriceForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 350,
    alias: 'widget.hardwareOrderItemChangePriceForm',
    layout: 'anchor',
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
            text: '确定',
            tooltip: "确定",
            action: 'save'
        }
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
                    anchor: '100%',
                    padding: 0,
                    margin: 0,
                    border: false,
                    items: [
                        {
                            xtype: "displayfield",
                            anchor: "100%",
                            width: 300,
                            labelAlign:'right',
                            name: 'productName',
                            labelWidth: 70,
                            fieldLabel: '硬件信息'
                        },
                        {
                            name: 'amount',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'oldPrice',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'oldTotal',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'id',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'orderId',
                            xtype: "hiddenfield"
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: 0,
                    border: false,
                    items: [
                        {
                            xtype: "displayfield",
                            anchor: "100%",
                            width: 300,
                            labelAlign:'right',
                            name: 'productPropertyText',
                            labelWidth: 70,
                            fieldLabel: '硬件类型'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: 0,
                    border: false,
                    items: [
                        {
                            xtype: "displayfield",
                            anchor: "100%",
                            width: 300,
                            labelAlign:'right',
                            name: 'priceText',
                            labelWidth: 70,
                            fieldLabel: '购买单价'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: 0,
                    border: false,
                    items: [
                        {
                            xtype: "displayfield",
                            anchor: "100%",
                            width: 300,
                            labelAlign:'right',
                            name: 'amountText',
                            labelWidth: 70,
                            fieldLabel: '购买数量'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: 0,
                    border: false,
                    items: [
                        {
                            xtype: "displayfield",
                            anchor: "100%",
                            width: 300,
                            labelAlign:'right',
                            name: 'totalText',
                            labelWidth: 70,
                            fieldLabel: '合计金额'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: '修改价格',
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
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            items: [
                                {
                                    xtype: 'radiofield',
                                    boxLabel  : '修改单价',
                                    width:80,
                                    inputValue: 'item',
                                    name:'changePriceRadio',
                                    scope:me,
                                    handler: me.toggleReaderFields
                                },
                                {
                                    xtype:"combobox",
                                    editable:false,
                                    disabled:true,
                                    allowBlank : false,  //是否允许为空
                                    mode : 'local',
                                    value:'+',
                                    displayField : 'text',
                                    valueField : 'value',
                                    width:70,
                                    store : new Ext.data.SimpleStore({  //填充的数据
                                        fields : ['text', 'value'],
                                        data : [['加价', '+'], ['减价', '-']]
                                    }),
                                    itemKey:"itemChangePriceType",
                                    listeners: {
                                        select: {
                                            fn: function (cmp,newValue,oldValue,eOpts) {
                                                me.calculateItemTotal(me);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: "numberfield",
                                    disabled:true,
                                    width: 70,
                                    vtype: 'money',
                                    itemKey:"itemChangePrice",
                                    hideTrigger: true,
                                    listeners: {
                                        keyup : {
                                            element: 'el',
                                            fn: function (combo,record,index) {
                                                me.calculateItemTotal(me);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: "displayfield",
                                    value: '元'
                                }
                            ]
                        },
                        {
                            xtype : 'fieldcontainer',
                            layout: 'hbox',
                            items: [
                                {
                                    xtype: 'radiofield',
                                    boxLabel  : '修改总价',
                                    width:80,
                                    inputValue: 'total',
                                    name:'changePriceRadio',
                                    scope:me,
                                    handler: me.toggleReaderFields
                                },
                                {
                                    xtype:"combobox",
                                    editable:false,
                                    disabled:true,
                                    allowBlank : false,  //是否允许为空
                                    mode : 'local',
                                    value:'+',
                                    displayField : 'text',
                                    valueField : 'value',
                                    width:70,
                                    store : new Ext.data.SimpleStore({  //填充的数据
                                        fields : ['text', 'value'],
                                        data : [['加价', '+'], ['减价', '-']]
                                    }),
                                    itemKey:"totalChangePriceType",
                                    listeners: {
                                        select : {
                                            fn: function (combo,record,index) {
                                                me.calculateItemTotal(me);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: "numberfield",
                                    width: 70,
                                    disabled:true,
                                    allowBlank : false,
                                    hideTrigger: true,
                                    vtype: 'money',
                                    itemKey:"totalChangePrice",
                                    listeners: {
                                        keyup: {
                                            element: 'el',
                                            fn: function (cmp, e, eOpts) {
                                                me.calculateItemTotal(me);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: "displayfield",
                                    value: '元'
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            items: [
                                {
                                    xtype: 'radiofield',
                                    boxLabel  : '总价打折',
                                    width:80,
                                    inputValue: 'discount',
                                    name:'changePriceRadio',
                                    scope:me,
                                    handler: me.toggleReaderFields
                                },
                                {
                                    xtype: "numberfield",
                                    width: 140,
                                    disabled:true,
                                    allowBlank : false,  //是否允许为空
                                    hideTrigger: true,
                                    vtype: 'digital',
                                    itemKey: 'discountChangePrice',
                                    maxValue: 9.99,
                                    minValue: 0.01,
                                    listeners: {
                                        keyup: {
                                            element: 'el',
                                            fn: function (cmp, e, eOpts) {
                                                me.calculateItemTotal(me);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: "displayfield",
                                    value: '折'
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            items: [
                                {
                                    xtype: "textfield",
                                    fieldLabel:'改后单价',
                                    labelAlign:'right',
                                    labelWidth: 60,
                                    width:120,
                                    readOnly:true,
                                    name: 'price'
                                },
                                {
                                    xtype: "displayfield",
                                    value: '元'
                                },
                                {
                                    xtype: "textfield",
                                    fieldLabel:'改后合计',
                                    labelAlign:'right',
                                    labelWidth: 60,
                                    width:120,
                                    readOnly:true,
                                    name: 'total'
                                },
                                {
                                    xtype: "displayfield",
                                    value: '元'
                                }
                            ]
                        }
                    ]
                }
            ]
        });
        this.callParent();
    },
    save: function (form, callback) {
        var me = this,
        baseForm = form.form;
        if (baseForm.isValid()) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            form.commonUtils.ajax({
                async:false,
                url: 'bcgogoReceivable.do?method=bcgogoReceivableOrderItemChangePrice',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', "保存成功！", function () {
                        baseForm.reset();
                        form.unmask();
                        callback();
                    });
                },
                failure: function () {
                    form.unmask();
                }
            });
        }
    },
    toggleReaderFields: function (radio, newValue) {
        var me = this;
        if(newValue){
            me.down("[itemKey="+ radio.inputValue+"ChangePrice]").enable();
            var changePriceTypeField = me.down("[itemKey="+ radio.inputValue+"ChangePriceType]");
            if(changePriceTypeField){
                changePriceTypeField.enable();
            }
        }else{
            me.down("[itemKey="+ radio.inputValue+"ChangePrice]").disable().reset();
            var changePriceTypeField = me.down("[itemKey="+ radio.inputValue+"ChangePriceType]");
            if(changePriceTypeField){
                changePriceTypeField.disable().reset();
            }
        }
        me.calculateItemTotal(me);
    },
    calculateItemTotal: function (me) {
        me = me ? me : this;
        var checkedRadioInputValue;
        Ext.Array.forEach(me.query('[name=changePriceRadio]'), function(domObject,index,array){
            if(domObject.getValue()){
                checkedRadioInputValue = domObject.inputValue;
            }
        });
        if(checkedRadioInputValue=="item"){
            var temp = me.down("[itemKey="+ checkedRadioInputValue+"ChangePrice]").getValue();
            var changePriceType = me.down("[itemKey="+ checkedRadioInputValue+"ChangePriceType]").getValue();
            var oldPrice = me.down("[name=oldPrice]").getValue();
            var itemAmount = me.down("[name=amount]").getValue();
            var itemPriceField = me.down('[name=price]');
            if(changePriceType=="+"){
                itemPriceField.setValue(Ext.util.Format.number(temp*1+oldPrice*1,"0.00"));
            }else if(changePriceType=="-"){
                itemPriceField.setValue(Ext.util.Format.number(temp*-1+oldPrice*1,"0.00"));
            }
            me.down("[name=total]").setValue(Ext.util.Format.number(itemAmount*itemPriceField.getValue(), "0.00"));
        }else if(checkedRadioInputValue=="total"){
            me.down('[name=price]').setValue(me.down("[name=oldPrice]").getValue());
            var oldTotal = me.down("[name=oldTotal]").getValue();
            var temp = me.down("[itemKey="+ checkedRadioInputValue+"ChangePrice]").getValue();
            var changePriceType = me.down("[itemKey="+ checkedRadioInputValue+"ChangePriceType]").getValue();
            var itemTotalField = me.down('[name=total]');
            if(changePriceType=="+"){
                itemTotalField.setValue(Ext.util.Format.number(temp*1+oldTotal*1,"0.00"));
            }else if(changePriceType=="-"){
                itemTotalField.setValue(Ext.util.Format.number(temp*-1+oldTotal*1, "0.00"));
            }
        }else if(checkedRadioInputValue=="discount"){
            me.down('[name=price]').setValue(me.down("[name=oldPrice]").getValue());
            var oldTotal = me.down("[name=oldTotal]").getValue();
            var temp = me.down("[itemKey="+ checkedRadioInputValue+"ChangePrice]").getValue();
            me.down('[name=total]').setValue(Ext.util.Format.number((temp*oldTotal)/10,"0.00"));
        }
    },
    showBcgogoHardwareOrderItemDetail: function (orderItem) {
        var me = this;
        var productPropertyText = orderItem['productKind'];
        if(!Ext.isEmpty(orderItem['productType'])){
            productPropertyText+="【"+orderItem['productType']+"】";
        }
        me.down('[name=productPropertyText]').setValue(productPropertyText);
        var priceText = orderItem['price']+" 元";
        var amountText = orderItem['amount'];
        if(!Ext.isEmpty(orderItem['unit'])){
            priceText+="/"+orderItem['unit'];
            amountText+=" "+orderItem['unit'];
        }
        me.down('[name=oldPrice]').setValue(orderItem['price']);
        me.down('[name=oldTotal]').setValue(orderItem['total']);
        me.down('[name=priceText]').setValue(priceText);
        me.down('[name=amountText]').setValue(amountText);
        me.down('[name=totalText]').setValue(orderItem['total']+" 元");
        this.show();
    }
});