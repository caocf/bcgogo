/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-3
 * Time: 上午10:43
 * To change this template use File | Settings | File Templates.
 */

Ext.define('Ext.view.normalProductStat.NormalProductStatDetail', {
  extend:'Ext.grid.Panel',
  alias:'widget.normalProductStatDetail',
  store:'Ext.store.normalProductInventoryStat.NormalProductStatDetails',
  autoScroll:true,
  loadMask: true,
  columnLines:true,
  stripeRows:true,            //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
//    forceFit:true,              //自动填充，即让所有列填充满gird宽度
  multiSelect:true,           //可以多选
  autoHeight:true,
  layout:'anchor',
  enableColumnResize:true,
  requires:[

  ],
  dockedItems:[
    {
      dock:'bottom',
      xtype:'pagingtoolbar',
      store:'Ext.store.normalProductInventoryStat.NormalProductStatDetails',
      displayInfo:true
    }
  ],
  initComponent:function () {
    var me = this;
    Ext.apply(me,
        {
          columns:[
            {
              header:'序号',
              xtype:'rownumberer',
              sortable:false,
              width:20
            },
            {
              header:'店铺名称',
              dataIndex:'shopName',
              width:150
            },

            {
              header: '所在区域',
              dataIndex: 'areaInfo',
              width: 150
            },
            {
              header:'软件版本',
              dataIndex:'shopVersion',
              width:60
            },
            {
              header:'采购次数',
              dataIndex:'times',
              width:50
            },
            {
              header:'采购量',
              dataIndex:'amount',
              width:50
            },
            {
              header: '单位',
              dataIndex: 'unit',
              width: 30
            },
            {
              header:'采购额',
              dataIndex:'total',
              width:70
            },
            {
              header: '均价',
              dataIndex: 'averagePrice',
              width: 50
            },

            {
              header: '库存',
              dataIndex: 'inventoryAmount',
              width: 50
            },
            {
              header: '最近采购日期',
              dataIndex: 'lastInventoryDate',
              width: 150
            },

            {
              header:'最低采购价',
              dataIndex:'bottomPrice',
              width:80
            },
            {
              header:'最高采购价',
              dataIndex:'topPrice',
              width:80
            }
          ]
        });
    this.callParent(arguments);
  }
});

