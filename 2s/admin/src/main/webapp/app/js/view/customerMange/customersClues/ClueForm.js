/**
 * 新增客户线索 form
 * @author :zhangjuntao
 */
Ext.define('Ext.view.customerMange.customersClues.ClueForm', {
    extend:'Ext.form.Panel',
    alias:'widget.clueForm',
    bodyPadding:5,
    width:850,
    store:'Ext.store.customerMange.Shops',
    requires:[
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.ProvinceSelect"
    ],
    layout:'anchor',
    defaults:{
        anchor:'100%'
    },
    fieldDefaults:{
        labelWidth:125,
        msgTarget:'side',
        autoFitErrors:false
    },

    // Reset and Submit buttons
    buttons:[
        {
            text:'重置',
            tooltip:"重置",
            handler:function () {
                this.up("form").form.reset();
            }
        },
        {
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
                    xtype:"hiddenfield",
                    name:'id'
                },{
                    xtype:"hiddenfield",
                    name:'scene',
                    value:'ADD_INTENTION_CLIENT'
                },
                {
                    xtype:'fieldset',
                    layout:'hbox',
                    anchor:'100%',
                    margin:0,
                    padding:0,
                    border:false,
                    items:[
                        {
                            xtype:'textfield',
                            name:'name',
                            fieldLabel:'店铺名称',
                            enforceMaxLength:true,
                            maxLength:30,
                            margin:"0 10 0 0",
                            labelWidth:80,
                            emptyText:"【字母、中文或数字】",
                            vtype:'characters',
                            allowBlank:false,
                            validator:function (value) {        //validator 频繁触发 采用blur 触发validator
                                return  !this.duplicating ? true : this.duplicating;
                            }
                        },
                        {
                            xtype:'textfield',
                            name:'owner',
                            margin:"0 10 0 0",
                            fieldLabel:'联系人',
                            allowBlank:false,
                            enforceMaxLength:true,
                            maxLength:6,
                            labelWidth:70
                        },
                        {
                            xtype:'textfield',
                            name:'mobile',
                            vtype:"mobilePhone",
                            fieldLabel:'联系方式',
                            allowBlank:false,
                            enforceMaxLength:true,
                            maxLength:11,
                            labelWidth:60
                        }
                    ]
                },
                {
                    xtype:'fieldset',
                    layout:'hbox',
                    anchor:'100%',
                    margin:"10 0 0 0",
                    padding:0,
                    border:false,
                    items:[
                        {
                            fieldLabel:'所在区域',
                            labelWidth:80,
                            width:160,
                            margin:"0 10 0 0",
                            name:'province',
                            allowBlank:false,
                            xtype:"provinceSelect"
                        },
                        {
                            width:80,
                            margin:"0 10 0 0",
                            name:'city',
                            xtype:"citySelect"
                        },
                        {
                            width:80,
                            margin:"0 0 0 0",
                            xtype:"regionSelect",
                            name:'region'
                        }
                    ]
                },
                {
                    xtype:'textfield',
                    name:'address',
                    fieldLabel:'详细地址',
                    labelWidth:80,
                    enforceMaxLength:true,
                    maxLength:30,
                    margin:"10 0 0 0",
                    anchor:'90%'
                },
                {
                    xtype:'fieldset',
                    layout:'hbox',
                    anchor:'100%',
                    margin:"10 0 0 0",
                    padding:'0 50 0 0',
                    border:false,
                    items:[
                        {
                            xtype:'radiogroup',
                            fieldLabel:'是否有电脑',
                            labelWidth:80,
                            width:250,
                            items:[
                                {boxLabel:'无', name:'hasComputer', inputValue:"NO"},
                                {boxLabel:'有', name:'hasComputer', inputValue:"YES",checked:true}
                            ]
                        },
                        {
                            xtype:'radiogroup',
                            fieldLabel:'是否使用其他软件',
                            labelWidth:110,
                            margin:"0 0 0 100",
                            width:250,
                            items:[
                                {boxLabel:'无',name:"hasSoftware", id:"usedSoftware-no", inputValue:"NO",checked:true},
                                {boxLabel:'有', name:"hasSoftware", id:"usedSoftware-yes", inputValue:"YES"},
                                {
                                    xtype:'textfield',
                                    name:'usedSoftware',
                                    labelWidth:30,
                                    enforceMaxLength:true,
                                    maxLength:10,
                                    vtype:'characters',
                                    id:"softwareTest",
                                    margin:'0 0 0 0',
                                    disabled:true,
                                    editable:false
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype:'container',
                    flex:1,
                    margin:"10 0 0 0",
                    layout:'anchor',
                    items:[
                        {
                            xtype:'radiogroup',
                            fieldLabel:'网络类型',
                            labelWidth:80,
                            width:450,
                            items:[
                                {boxLabel:'电信', name:'networkType', inputValue:"TELECOM",checked:true},
                                {boxLabel:'联通', name:'networkType', inputValue:"UNICOM"},
                                {boxLabel:'移动', name:'networkType', inputValue:"MOBILE"},
                                {boxLabel:'铁通', name:'networkType', inputValue:"TIE_TONG"},
                                {boxLabel:'有线通', name:'networkType', inputValue:"CABLE"}
                            ]
                        }
                    ]
                },
                {
                    xtype:'container',
                    layout: 'hbox',
                    items:[
                        {
                            text: '经营产品',
//                            iconCls: 'icon-application-view-list',
                            action: 'showBusinessScope',
                            xtype: "button",
                            width:70
                        },
                        {
                            xtype: "displayfield",
                            width: 'auto',
                            name: 'showBusinessScope'
                        }
                       /* {
                            xtype:'checkboxgroup',
                            fieldLabel:'汽车维修',
                            labelWidth:70,
                            items:[
                                {boxLabel:'汽车保养', name:'businessScopes', inputValue:'汽车保养'},
                                {boxLabel:'板金喷漆', name:'businessScopes', inputValue:'板金喷漆'},
                                {boxLabel:'轮胎', name:'businessScopes', inputValue:'轮胎'},
                                {boxLabel:'汽车快修', name:'businessScopes', inputValue:'汽车快修'},
                                {boxLabel:'汽车小修', name:'businessScopes', inputValue:'汽车小修'},
                                {boxLabel:'汽车中修', name:'businessScopes', inputValue:'汽车中修'},
                                {boxLabel:'汽车大修', name:'businessScopes', inputValue:'汽车大修'}
                            ]
                        },
                        {
                            xtype:'checkboxgroup',
                            fieldLabel:'汽车装潢',
                            columns:7,
                            labelWidth:70,
                            items:[
                                {boxLabel:'电脑洗车', name:'businessScopes', inputValue:'电脑洗车'},
                                {boxLabel:'人工洗车', name:'businessScopes', inputValue:'人工洗车'},
                                {boxLabel:'车身贴彩', name:'businessScopes', inputValue:'车身贴彩'},
                                {boxLabel:'新车开蜡', name:'businessScopes', inputValue:'新车开蜡'},
                                {boxLabel:'封釉美容', name:'businessScopes', inputValue:'封釉美容'},
                                {boxLabel:'漆面打蜡', name:'businessScopes', inputValue:'漆面打蜡'},
                                {boxLabel:'漆面抛光', name:'businessScopes', inputValue:'漆面抛光'},
                                {boxLabel:'汽车装潢', name:'businessScopes', inputValue:'汽车装潢'},
                                {boxLabel:'真皮座椅', name:'businessScopes', inputValue:'真皮座椅'},
                                {boxLabel:'中央门锁', name:'businessScopes', inputValue:'中央门锁'},
                                {boxLabel:'DVD导航', name:'businessScopes', inputValue:'DVD导航'},
                                {boxLabel:'倒车雷达', name:'businessScopes', inputValue:'倒车雷达'},
                                {boxLabel:'汽车隔音', name:'businessScopes', inputValue:'汽车隔音'},
                                {boxLabel:'地盘装甲', name:'businessScopes', inputValue:'地盘装甲'},
                                {boxLabel:'轮胎翻新', name:'businessScopes', inputValue:'轮胎翻新'},
                                {boxLabel:'防盗器', name:'businessScopes', inputValue:'防盗器'},
                                {boxLabel:'防爆膜', name:'businessScopes', inputValue:'防爆膜'},
                                {boxLabel:'内部装饰', name:'businessScopes', inputValue:'内部装饰'},
                                {boxLabel:'其它', id:'otherBusinessScope'},
                                {
                                    xtype:'textfield',
                                    name:'otherBusinessScope',
                                    vtype:'characters',
                                    id:"otherBusinessScopeText",
                                    enforceMaxLength:true,
                                    maxLength:10,
                                    margin:'0 0 0 0',
                                    disabled:true,
                                    editable:false
                                }
                            ]
                        },
                        {
                            xtype:'fieldset',
                            layout:'hbox',
                            anchor:'100%',
                            padding:0,
                            margin:'0 0 5 0',
                            border:false,
                            items:[
                                {
                                    xtype:'checkbox',
                                    fieldLabel:'批发零售',
                                    labelWidth:73,
                                    id:'majorProduct',
                                    boxLabel:'主要产品',
                                    margin:'0 20 0 0'
                                },
                                {
                                    xtype:'textfield',
                                    name:'majorProduct',
                                    vtype:'characters',
                                    id:"majorProductText",
                                    maxLength:10,
                                    enforceMaxLength:true,
                                    margin:'0 0 0 0',
                                    disabled:true,
                                    editable:false
                                }
                            ]
                        }*/

                    ]
                },
                {
                    xtype:'fieldset',
                    layout:'hbox',
                    anchor:'100%',
                    margin:"10 0 0 0",
                    padding:0,
                    border:false,
                    items:[
                        {
                            xtype:'radiogroup',
                            fieldLabel:'意向情况',
                            labelWidth:80,
                            width:350,
                            items:[
                                {boxLabel:'无意向', name:'shopStatus', inputValue:"NO_INTENTION"},
                                {boxLabel:'潜在', name:'shopStatus', inputValue:"LATENT"},
                                {boxLabel:'意向', name:'shopStatus',checked:true, inputValue:"INTENTION"}
                            ]
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    fieldLabel:'录入时间',
                    labelWidth:80,
                    margin:"10 0 0 0",
                    padding:0,
                    border:false,
                    items:[
                        {
                            xtype:"datefield",
                            format: 'Y-m-d',
                            name:'clueInputDate',
                            value: new Date(),
                            editable :false,
                            activeError:'',
                            id:'clueInputDateInput'
                        }
                    ]
                }
            ]
        });
        this.callParent();
    },

    setFormType:function (type) {
        this.formType = type;
    },

    getFormType:function () {
        return this.formType;
    }
});