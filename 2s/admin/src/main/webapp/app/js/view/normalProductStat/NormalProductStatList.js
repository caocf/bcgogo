/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-29
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.normalProductStat.NormalProductStatList', {
  extend:'Ext.grid.Panel',
  alias:'widget.normalProductStatList',
  store:'Ext.store.normalProductInventoryStat.NormalProductInventoryStats',
  autoScroll:true,
  columnLines:true,
  loadMask: true,
  stripeRows:true,            //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
//    forceFit:true,              //自动填充，即让所有列填充满gird宽度
  multiSelect:true,           //可以多选
  autoHeight:true,
  layout:'anchor',
  enableColumnResize:true,
  requires:[
      'Ext.view.dataMaintenance.permission.ShopVersionSelect',
      "Ext.view.customerMange.RegionSelect",
      "Ext.view.customerMange.CitySelect",
      "Ext.view.customerMange.ProvinceSelect"
  ],
  initComponent:function () {
    var me = this;

    Ext.apply(me,
        { dockedItems:[
            {
                xtype:'toolbar',
                dock:'top',
                id:'normalProductStat',
                items:[
                    {
                        xtype:"combobox",
                        emptyText:"一级分类",
                        editable:false,
                        width:100,
                        displayField:'name',
                        valueField:'id',
                        id:"firstCategorySearch",
                        store:new Ext.data.SimpleStore({
                            proxy : new Ext.data.HttpProxy({
                                url : "productManage.do?method=getFirstCategory"
                            }),
                            fields : ["name", "id"],
                            autoLoad : false
                        })
                    },
                    {
                        xtype:"combobox",
                        emptyText:"二级分类",
                        editable:false,
                        width:100,
                        id:"secondCategorySearch",
                        store:new Ext.data.SimpleStore({
                            proxy : new Ext.data.HttpProxy({
                                url : "productManage.do?method=getSecondCategory"
                            }),
                            fields : ["name", "id"],
                            autoLoad : false
                        }),
                        displayField:'name',
                        valueField:'id'
                    },
                    {
                        xtype:"combobox",
                        emptyText:"品名",
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
                        id:"productNameSearch"
                    },
                    {
                        xtype: 'label',
                        forId: 'myFieldId',
                        text: '其他查询条件:',
                        margins: '0 0 0 10'
                    },

                    {
                        xtype:"combobox",
                        emptyText:"品牌",
                        editable:true,
                        width:100,
                        store:Ext.create("Ext.data.Store", {
                            proxy:{
                                type:'ajax',
                                api:{
                                    read:'productManage.do?method=getDataByQueryBuilder'
                                }
                            },
                            /*proxy : new Ext.data.HttpProxy({
                             url : "productManage.do?method=getDataByQueryBuilder"
                             }),*/
                            fields : ["key"],
                            autoLoad : false
                        }),
                        displayField:'key',
                        valueField:'key',
                        remoteFilter:true, //ajax过滤开关
                        queryMode:'remote', //远程过滤
                        enableKeyEvents:true,
                        minChars:1,
                        queryDelay:500,
                        id:"brandSearch"
                    },
                    {
                        xtype:"combobox",
                        emptyText:"规格",
                        editable:true,
                        width:100,
                        store:new Ext.data.SimpleStore({
                            proxy : new Ext.data.HttpProxy({
                                url : "productManage.do?method=getDataByQueryBuilder"
                            }),
                            fields : ["key"],
                            autoLoad : false
                        }),
                        displayField:'key',
                        valueField:'key',
                        remoteFilter:true, //ajax过滤开关
                        queryMode:'remote', //远程过滤
                        enableKeyEvents:true,
                        minChars:1,
                        queryDelay:500,
                        id:"specSearch"
                    },
                    {
                        xtype:"combobox",
                        emptyText:"型号",
                        editable:true,
                        width:100,
                        store:new Ext.data.SimpleStore({
                            proxy : new Ext.data.HttpProxy({
                                url : "productManage.do?method=getDataByQueryBuilder"
                            }),
                            fields : ["key"],
                            autoLoad : false
                        }),
                        displayField:'key',
                        valueField:'key',
                        remoteFilter:true, //ajax过滤开关
                        queryMode:'remote', //远程过滤
                        enableKeyEvents:true,
                        minChars:1,
                        queryDelay:500,
                        id:"modelSearch"
                    },
                    {
                        xtype:"combobox",
                        emptyText:"车辆品牌",
                        editable:true,
                        width:100,
                        store:new Ext.data.SimpleStore({
                            proxy : new Ext.data.HttpProxy({
                                url : "productManage.do?method=getDataByQueryBuilder"
                            }),
                            fields : ["key"],
                            autoLoad : false
                        }),
                        displayField:'key',
                        valueField:'key',
                        remoteFilter:true, //ajax过滤开关
                        queryMode:'remote', //远程过滤
                        enableKeyEvents:true,
                        minChars:1,
                        queryDelay:500,
                        id:"vehicleBrandSearch"
                    },
                    {
                        xtype:"combobox",
                        emptyText:"车型",
                        editable:true,
                        width:100,
                        store:new Ext.data.SimpleStore({
                            proxy : new Ext.data.HttpProxy({
                                url : "productManage.do?method=getDataByQueryBuilder"
                            }),
                            fields : ["key"],
                            autoLoad : false
                        }),
                        displayField:'key',
                        valueField:'key',
                        remoteFilter:true, //ajax过滤开关
                        queryMode:'remote', //远程过滤
                        enableKeyEvents:true,
                        minChars:1,
                        queryDelay:500,
                        id:"vehicleModelSearch"
                    },
                    {
                        xtype:"combobox",
                        emptyText:"商品编码",
                        editable:true,
                        width:100,
                        store:new Ext.data.SimpleStore({
                            proxy : new Ext.data.HttpProxy({
                                url : "productManage.do?method=getDataByQueryBuilder"
                            }),
                            fields : ["key"],
                            autoLoad : false
                        }),
                        displayField:'key',
                        valueField:'key',
                        remoteFilter:true, //ajax过滤开关
                        queryMode:'remote', //远程过滤
                        enableKeyEvents:true,
                        minChars:1,
                        queryDelay:500,
                        id:"commodityCodeSearch"
                    }
                ]
            },
            {
                dock:'bottom',
                xtype:'pagingtoolbar',
                store:'Ext.store.normalProductInventoryStat.NormalProductInventoryStats',
                displayInfo:true
            },

//    {
//            xtype:'toolbar',
//            items:[
//                    {
//                      xtype: 'label',
//                      forId: 'myFieldId',
//                      text: 'My Awesome Field',
//                      margins: '0 0 0 10'
//                    },
//                    new Ext.form.Radio({
//                        name : "inBillType",
//                        inputValue : "2",
//                        boxLabel : "尽快的送货"
//
//                    }), new Ext.form.Radio({
//                        name : "inBillType",
//                        inputValue : "3",
//                        boxLabel : "星一至星期五"
//
//                    }),  new Ext.form.Radio({
//                        name : "inBillType",
//                        inputValue : "4",
//                        boxLabel : "星期六星期日"
//
//                    }),  new Ext.form.Radio({
//                        name : "inBillType",
//                        inputValue : "4",
//                        boxLabel : "三天之内到货"
//
//                    })]
//        },
            {
                xtype:'toolbar',
                dock:'top',
                items:[
                    {
                        xtype: "combobox",
                        emptyText: "软件版本",
                        fieldLabel: "软件版本",
                        labelWidth: 55,
                        margin: "0 0 0 -3",
                        displayField: 'name',
                        valueField: 'value',
                        enableKeyEvents: true,
                        minChars: 1,
                        store: new Ext.data.SimpleStore({
                            fields: ["name","value"],
                            data:[
                                ["汽修版","REPAIR_VERSION"],
                                ["汽配版","WHOLESALER_VERSION"]
                            ],
                            autoLoad: true
                        }),
                        name: 'shopVersion'

                    },
                    {
                        fieldLabel: '店铺区域',
                        labelWidth: 55,
                        width: 135,
                        xtype: "provinceSelect",
                        name: 'province',
                        margin: "0 0 0 50",
                        listeners: {
                            select: function (combo, records, eOpts) {
                                me.down("citySelect").setRawValue("");
                                me.down("citySelect").setValue(null);
                                me.down("regionSelect").setRawValue(null);
                                me.down("regionSelect").setValue(null);
                                me.down("citySelect").setProvince(records[0]);
                            },
                            beforequery: function (queryEvent, eOpts) {
                                queryEvent.combo.store.proxy.extraParams = {
                                    parentNo: "1"
                                };
                            }
                        }
                    },
                    {
                        width: 80,
                        xtype: "citySelect",
                        name: 'city',
                        listeners: {
                            select: function (combo, records, eOpts) {
                                me.down("regionSelect").setRawValue(null);
                                me.down("regionSelect").setValue(null);
                                me.down("regionSelect").setCity(records[0]);
                            },
                            beforequery: function (queryEvent, eOpts) {
                                if (!queryEvent.combo.getProvince()) {
                                    return false;
                                }
                                queryEvent.combo.store.proxy.extraParams = {
                                    parentNo: queryEvent.combo.getProvince().get("no")
                                };
                                queryEvent.combo.store.load();
                            }
                        }
                    },
                    {
                        width: 80,
                        xtype: "regionSelect",
                        name: 'region',
                        listeners: {
                            beforequery: function (queryEvent, eOpts) {
                                if (!queryEvent.combo.getCity()) {
                                    return false;
                                }
                                queryEvent.combo.store.proxy.extraParams = {
                                    parentNo: queryEvent.combo.getCity().get("no")
                                };
                                queryEvent.combo.store.load();
                            }
                        }
                    }
                ]
            },
            {
                xtype:'toolbar',
                columns: 2,
                items:[
                    {
                        xtype: 'radiogroup',
                        fieldLabel: '统计日期',
                        // Arrange radio buttons into two columns, distributed vertically
                        columns: 5,
                        width: 700,
                        vertical: false,
                        id: "statTypeId",
                        items: [
                            { boxLabel: '最近一周', name: 'normalProductStatType', inputValue: 'WEEK', checked: true, id:"week"},
                            { boxLabel: '最近一个月', name: 'normalProductStatType', inputValue: 'MONTH'},
                            { boxLabel: '最近三个月', name: 'normalProductStatType', inputValue: 'THREE_MONTH' },
                            { boxLabel: '最近六个月', name: 'normalProductStatType', inputValue: 'HALF_YEAR' },
                            { boxLabel: '最近一年', name: 'normalProductStatType', inputValue: 'YEAR' }
                        ]
                    },
                    '-',
                    {
                        text:"查询",
                        xtype:'button',
                        action:'normalProductSearch',
                        iconCls:"icon-search",
                        tooltip:"根据条件查询采购信息",
                        scope:this
                    },
                    {
                        text: "重置",
                        xtype: 'button',
                        iconCls: "icon-reset",
                        scope: me,
                        handler: function () {
                            me.reset();
                        }
                    }
                ]
            }
        ],
          columns:[
            {
              header:'序号',
              xtype:'rownumberer',
              sortable:false,
              width:20
            },
            {
              header:'商品编码',
              dataIndex:'commodityCode',
              width:120
            },
            {
              header:'品名/品牌',
              dataIndex:'nameAndBrand',
              width:180
            },
            {
              header:'规格/型号',
              dataIndex:'specAndModel',
              width:180
            },
            {
              header:'车型/车辆品牌',
              dataIndex:'productVehicleBrand',
              width:180
            },
            {
              header:'单位',
              dataIndex:'unit',
              width:30
            },
            {
              header: '采购次数',
              dataIndex: 'times',
              width: 60
            },
            {
              header:'采购总量',
              dataIndex:'amount',
              width:60
            },
            {
              header:'采购总额',
              dataIndex:'total',
              width:100
            },
            {
              header: '均价',
              dataIndex: 'averagePrice',
              width: 60
            },

            {
              header:'最高价/最低价',
              dataIndex:'priceStr',
              width:100
            }
//            {
//              xtype:'actioncolumn',
//              id:"statDateDetail",
//              header:'操作',
//              width:30,
//              items:[
//                {
//                  text:'查看详细',
//                  tooltip:'查看详细',
//                  scope:me,
//                  icon:'app/images/icons/edit.png'
//                }
//              ]
//            }
          ]
        });
    this.callParent(arguments);
  },
  reset: function () {
      this.down("[id=firstCategorySearch]").setValue(null);
      this.down("[id=secondCategorySearch]").setValue(null);
      this.down("[id=productNameSearch]").setValue(null);
      this.down("[id=brandSearch]").setValue(null);
      this.down("[id=specSearch]").setValue(null);
      this.down("[id=modelSearch]").setValue(null);
      this.down("[id=vehicleBrandSearch]").setValue(null);
      this.down("[id=vehicleModelSearch]").setValue(null);
      this.down("[id=commodityCodeSearch]").setValue(null);
      this.down("[name=shopVersion]").setValue(null);
      this.down("[name=province]").setValue(null);
      this.down("[name=city]").setValue(null);
      this.down("[name=region]").setValue(null);
      this.down("[id=week]").setValue(true);
  }
});
