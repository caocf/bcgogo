Ext.define('Ext.view.product.normalProduct.OneNormalProduct', {
    extend:'Ext.grid.Panel',
    alias:'widget.oneNormalProduct',
    store:[],
    autoScroll:true,
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
    initComponent:function () {
        var me = this;
//        var permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        Ext.apply(me,
            {
            columns:[
                {
                    header:'商品编码',
                    dataIndex:'commodityCode',
                    width:100
                },
                {
                    header:'商品分类',
                    dataIndex:'productCategoryName',
                    width:100
                },
                {
                    header:'品名',
                    dataIndex:'productName',
                    width:100
                },
                {
                    header:'品牌',
                    dataIndex:'brand',
                    width:100
                },
                {
                    header:'规格',
                    dataIndex:'spec',
                    width:100
                },
                {
                    header:'型号',
                    dataIndex:'model',
                    width:100
                },
                {
                   header:'车辆品牌',
                    dataIndex:'vehicleBrand',
                    width:100
                },
                {
                   header:'适合车型',
                   dataIndex:'vehicleModel',
                    width:100
                },
                {
                   header:'单位',
                   dataIndex:'unit',
                    width:100
                }
            ]
        });
        this.callParent(arguments);
    }
});
