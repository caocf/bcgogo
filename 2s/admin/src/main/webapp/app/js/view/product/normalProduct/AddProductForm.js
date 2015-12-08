Ext.define('Ext.view.product.normalProduct.AddProductForm', {
    extend:'Ext.form.Panel',
    bodyPadding:5,
    width:800,
    alias:'widget.formProduct',
    store:'Ext.store.product.NormalProduct',
    requires:[

    ],
    layout:'anchor',
    defaults:{
        anchor:'100%'
    },
    fieldDefaults:{
        labelWidth:80,
        msgTarget:'side',
        autoFitErrors:false
    },

    // Reset and Submit buttons
    buttons:[
        {
            text:'重置',
            tooltip:"重置",
            handler:function () {
                var form =  this.up("form");
                form.form.reset();
                form.down("[name=selectAllBrandModel]").setValue(true);
                form.down("[name=vehicleBrandModelInfo]").setValue("所有车型");
            }
        },
        {
            name:"formProductSave",
            text:'保存',
            action:'save'
        }
    ],
    initComponent:function () {
        var me = this;
        me.addEvents('create');
        Ext.apply(me, {
            items:[
                {
                    layout:'column',
                    border:false,
                    height:105,
                    items:[
                        {
                            columnWidth:.5,
                            border:false,
                            layout:'anchor',
                            defaultType:'textfield',
                            items:[
                                {
                                    name:'commodityCode',
                                    fieldLabel:'商品编码',
                                    emptyText:"【英文字母、或是数字】",
                                    minLengthText:"商品编码不的超过xx位",
                                    enforceMaxLength:true,
                                    maxLength:30,
                                    enableKeyEvents:true,
                                    xtype:"textfield",
                                    width:385,
                                    allowBlank:true
                                },
                                {
                                    layout:'column',
                                    border:false,
                                    fieldLabel:'品名',
                                    xtype:"fieldcontainer",
                                    items:[
                                        {
                                            xtype:"combobox",
                                            emptyText:"一级分类",
                                            editable:false,
                                            width:100,
                                            displayField:'name',
                                            valueField:'id',
                                            store:new Ext.data.SimpleStore({
                                                proxy : new Ext.data.HttpProxy({
                                                    url : "productManage.do?method=getFirstCategory"
                                                }),
                                                fields : ["name", "id"],
                                                autoLoad : false
                                            }),
                                            name:"addFormFirstCategorySelect",
//                                            hiddenValue:'id',
                                            allowBlank:false
                                        },
                                        {
                                            xtype:"combobox",
                                            emptyText:"二级分类",
                                            editable:false,
                                            width:100,
                                            store:new Ext.data.SimpleStore({
                                                proxy : new Ext.data.HttpProxy({
                                                    url : "productManage.do?method=getSecondCategory"
                                                }),
                                                fields : ["name", "id"],
                                                autoLoad : false
                                            }),
                                            displayField:'name',
                                            valueField:'id',
                                            name:"addFormSecondCategorySelect",
                                            allowBlank:false
                                        },
                                        {
                                            xtype:"combobox",
                                            emptyText:"品名",
                                            maxLength:50,
                                            editable:true,
                                            width:100,
                                            store:new Ext.data.SimpleStore({
                                                proxy : new Ext.data.HttpProxy({
                                                    url : "productManage.do?method=getThirdCategory"
                                                }),
                                                fields : ["name", "id"],
                                                autoLoad : false
                                            }),
                                            displayField:'name',
                                            valueField:'id',
                                            remoteFilter:true, //ajax过滤开关
                                            queryMode:'remote', //远程过滤
                                            enableKeyEvents:true,
                                            minChars:1,
                                            queryDelay:500,
                                            name:"productCategoryId",
                                            allowBlank:false
                                        }
                                    ]
                                },
                                {
                                    fieldLabel:'规格',
                                    name:'spec',
                                    xtype:"textfield",
                                    width:385,
                                    maxLength:50,
                                    allowBlank:true
                                },
                                {
                                    layout:'column',
                                    border:false,
                                    fieldLabel:'适合车型',
                                    name:"selectAllBrandModelRadiogroup",
                                    xtype:"radiogroup",
                                    items:[
                                        {xtype: 'radio',width:'100px', boxLabel: '所有车型', name: 'selectAllBrandModel',dataType:'selectAllBrandModel', inputValue:true},
                                        {xtype: 'radio', name: 'selectAllBrandModel',dataType:'selectPartBrandModel', inputValue:false},
                                        {
                                            text: '部分车型',
                                            action: 'modifyVehicleModel',
                                            xtype: "button",
                                            width: 60
                                        }
                                    ]
                                },
                                {
                                    xtype:"hiddenfield",
                                    name:'id'
                                },
                                {
                                    xtype:"hiddenfield",
                                    name:'productName'
                                },
                                {
                                    xtype:"hiddenfield",
                                    name:'vehicleModelIds'
                                }
                            ]
                        },
                        {
                            columnWidth:.5,
                            border:false,
                            layout:'anchor',
                            defaultType:'textfield',
                            items:[
                                {
                                    fieldLabel:'单位',
                                    xtype:"textfield",
                                    maxLength:5,
                                    name:'unit',
                                    enableKeyEvents:true,
                                    allowBlank:false
                                },
                                {
                                    fieldLabel:'品牌',
                                    name:'brand',
                                    maxLength:50,
                                    xtype:"textfield",
                                    allowBlank:false
                                },
                                {
                                    fieldLabel:'型号',
                                    name:'model',
                                    maxLength:50,
                                    xtype:"textfield",
                                    allowBlank:true
                                }
                            ]
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
                        labelWidth: 70,
                        width:760
                    },
                    border: false,
                    items: [
                        {
                            name:'vehicleBrandModelInfo',
                            fieldLabel: "车型详细",
                            renderer: function (val, style, rec, index) {
                                return Ext.util.Format.ellipsis(val,100);
                            }
                        }
                    ]
                }
            ]
        })
        this.callParent();
    }
});