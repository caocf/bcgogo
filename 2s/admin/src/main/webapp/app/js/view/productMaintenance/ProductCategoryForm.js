Ext.define('Ext.view.productMaintenance.ProductCategoryForm', {
    extend:'Ext.form.Panel',
    bodyPadding:5,
    width:600,
    alias:'widget.productCategoryForm',
    store:[
      'Ext.store.productMaintenance.FirstProductCategories'
    ],
    requires:[
      "Ext.view.productMaintenance.FirstProductCategorySelect"
    ],

    layout:'anchor',
    defaults:{
        anchor:'100%'
    },
    fieldDefaults:{
        labelWidth:100,
        msgTarget:'side',
        autoFitErrors:false
    },

    // Reset and Submit buttons
    buttons:[
        {
            text:'保存',
            action:'updateCategoryForm'
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
                    items:[
                        {
                            columnWidth:.9,
                            border:false,
                            layout:'anchor',
                            defaultType:'textfield',
                            items:[
                                {
                                    id:"thirdCategoryName",
                                    fieldLabel:'品名',
                                    name:'thirdCategoryName',
                                    xtype:"textfield",
                                    maxLength:15,
                                    enforceMaxLength:true,
                                    width : 350,
                                    allowBlank:true
                                },
                                {
                                    id:"firstCategoryName",
                                    fieldLabel:'所属系统类别',
                                    name:'firstCategoryName',
                                    xtype:"firstProductCategorySelect",
                                    allowBlank:false,
                                    maxLength:15,
                                    enforceMaxLength:true,
                                    width : 350,
                                    validator:function (value) {
                                        return true;
                                    }
                                },
                                {
                                    id:"secondCategoryName",
                                    fieldLabel:'所属种类',
                                    name:'secondCategoryName',
                                    xtype:'combo',
                                    allowBlank:true,
                                    maxLength:15,
                                    enforceMaxLength:true,
                                    width : 350,
                                    store:new Ext.data.SimpleStore({
                                                        proxy : new Ext.data.HttpProxy({
                                                                    url : "productCategory.do?method=getSecondCategory"
                                                                }),
                                                        fields : ["name", "id"],
                                                        autoLoad : false
                                                    }),
                                    displayField:'name',
                                    valueField:'name'
                                },

                                {
                                    xtype:"hiddenfield",
                                    name:'id'
                                },
                                {
                                    id:"categoryType",
                                    xtype:"hiddenfield",
                                    name:'categoryType'
                                },
                                {
                                    id:"formType",
                                    xtype:"hiddenfield",
                                    name:'formType',
                                    value:"update"
                                }
                            ]
                        }
                    ]
                }
            ]
        })
        this.callParent();
    }
});
