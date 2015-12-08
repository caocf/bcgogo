Ext.define('Ext.view.sales.hardware.OrderList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.salesHardwareOrderList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    requires: ['Ext.app.ActionTextColumn','Ext.ux.RowExpander'],
    plugins: [{
        ptype: 'rowexpander',
        enableCaching : false,
        rowBodyTpl: [
            '<table cellpadding="0" cellspacing="0" style="margin-left:10px;border:1px solid #bfbfbf; width:100%; table-layout:fixed; border-collapse: collapse;">',
            '<col width="200">',
            '<col width="120">',
            '<col width="120">',
            '<col width="120">',
            '<tr style=" border:1px solid #bfbfbf;padding-left:10px;border-left:none;border-right:none;height: 18px;background-color: #bfbfbf;">',
            '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">销售商品</th>',
            '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">单价(元)</th>',
            '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold;">数量</th>',
            '<th style=" border:1px solid #bfbfbf;padding-left:10px;font-weight: bold">小计(元)</th>',
            '</tr>',
            '<tpl for="bcgogoReceivableOrderItemDTOList">',
            '<tr style=" border:1px solid #bfbfbf;padding-left:10px; border-left:none;border-right:none;height: 18px;">',
            '<td style=" border:1px solid #bfbfbf;padding-left:10px;">{productName}</td>',
            '<td style=" border:1px solid #bfbfbf;padding-left:10px;">{price}<tpl if="this.canChangePrice(parent.status)">&nbsp&nbsp<a id="list-change-price-{idStr}" data-orderitem-id="{idStr}" href="javascript:void(0);">改价</a></tpl></td>',
            '<td style=" border:1px solid #bfbfbf;padding-left:10px;">{amount}</td>',
            '<td style=" border:1px solid #bfbfbf;padding-left:10px;">{total}</td>',
            '</tr>',
            '</tpl>',
            '</table>',
            {
                permissionUtils : Ext.create("Ext.utils.PermissionUtils"),
                canChangePrice: function (orderStatus) {
                    if(this.permissionUtils.hasPermission("CRM.SALES_MANAGER.HARDWARE.CHANGE_PRICE")){
                        return orderStatus=='NON_PAYMENT';
                    }else{
                        return false;
                    }

                }
            }
        ]
    }],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        var store = Ext.create('Ext.store.sales.BcgogoReceivableOrders');
        store.proxy.extraParams = {
            paymentTypes: "HARDWARE",
            paymentStatuses:["NON_PAYMENT","FULL_PAYMENT","SHIPPED","CANCELED"]
        };
        this.store = store;
        Ext.apply(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            fieldLabel: '销售日期',
                            labelWidth: 60,
                            labelAlign:'right',
                            xtype: "datefield",
                            format: 'Y-m-d',
                            width: 160,
                            name: 'startTimeStr'
                        },
                        "至",
                        {
                            xtype: "datefield",
                            width: 100,
                            format: 'Y-m-d',
                            name: 'endTimeStr'
                        },
                        { xtype: 'tbspacer', width: 20 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '销售渠道',
                            columns: 2,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 200,
                            name:'buyChannelCheckBoxGroup',
                            items: [
                                { boxLabel: '在线下单', name: 'buyChannel', inputValue: 'ONLINE_ORDERS', width: 70 },
                                { boxLabel: '后台录入', name: 'buyChannel', inputValue: 'BACKGROUND_ENTRY', width: 70 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 20 },
                        {
                            fieldLabel: '店铺名',
                            labelAlign:'right',
                            labelWidth: 50,
                            xtype: "textfield",
                            width: 200,
                            name: 'shopName'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            fieldLabel: '销售跟进人',
                            labelAlign:'right',
                            labelWidth: 70,
                            xtype: "textfield",
                            width: 200,
                            name: 'followName'
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            fieldLabel: '销售单号',
                            labelAlign:'right',
                            labelWidth: 60,
                            xtype: "textfield",
                            width: 200,
                            name: 'receiptNo'
                        },

                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '状态',
                            columns: 4,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 320,
                            name:'paymentStatusCheckBoxGroup',
                            items: [
                                { boxLabel: '待支付', name: 'paymentStatus', inputValue: 'NON_PAYMENT', width: 60 },
                                { boxLabel: '待发货', name: 'paymentStatus', inputValue: 'FULL_PAYMENT', width: 60 },
                                { boxLabel: '已发货', name: 'paymentStatus', inputValue: 'SHIPPED', width: 60 },
                                { boxLabel: '交易已取消', name: 'paymentStatus', inputValue: 'CANCELED', width: 80 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'fieldset',
                            name: "bcgogoProductFieldset",
                            frame:false,
                            padding:0,
                            margin: "0 5 0 0",
                            border: false
                        },
                        "->",
                        {
                            text: "查询",
                            xtype: 'button',
                            action: 'search',
                            iconCls: "icon-search",
                            scope: me,
                            handler: function () {
                                me.onSearch();
                            }
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '待支付',
                            name: 'nonPaymentStat',
                            labelAlign:'right',
                            value: '(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)',
                            labelWidth: 40,
                            margin: '0 20 0 0'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '待发货',
                            name: 'fullPaymentStat',
                            labelAlign:'right',
                            value: '(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)',
                            labelWidth: 40,
                            margin: '0 20 0 0'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '已发货',
                            value: '(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)',
                            labelAlign:'right',
                            name: 'shippedStat',
                            labelWidth: 40
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '交易已取消',
                            value: '(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)',
                            labelAlign:'right',
                            name: 'canceledStat',
                            labelWidth: 70
                        },
                        "->",
                        {
                            text: "新增硬件订单",
                            xtype: 'button',
                            action: 'addHardwareBcgogoReceivableOrder',
                            iconCls: "icon-add",
                            scope: this
                        }
                    ]
                },
                {
                    dock: 'bottom',
                    xtype: 'pagingtoolbar',
                    store: store,
                    displayInfo: true
                }
            ],
            columns: [
                {
                    header: 'No.',
                    xtype: 'rownumberer',
                    sortable: false,
                    width: 30
                },
                {
                    header: '订单号',
                    width: 140,
                    dataIndex: 'receiptNo',
                    renderer: function (val, style, rec, index) {
                        return '<span style="color: blue; cursor:pointer;">' + val + '</span>';
                    }
                },
//                {
//                    xtype: 'actiontextcolumn',
//                    header: '订单号',
//                    action:'showHardwareOrderDetail',
//                    width: 110,
//                    items: [
//                        {
//                            getClass: function (v, meta, rec) {
//                                this.items[0].tooltip=rec.get('receiptNo');
//                                this.items[0].text=rec.get('receiptNo');
//                                return '';
//                            }
//                        }
//                    ]
//                },
                {
                    header: '销售渠道',
                    width: 80,
                    dataIndex: 'buyChannels',
                    renderer: function (val, style, rec, index) {
                        if (val === "ONLINE_ORDERS") {
                            return "在线下单";
                        } else if (val === "BACKGROUND_ENTRY") {
                            return "后台录入";
                        }else{
                            return "--";
                        }
                    }
                },
                {
                    header: '销售时间',
                    width: 100,
                    dataIndex: 'createdTime',
                    renderer: function (val, style, rec, index) {
                        if(val){
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d');
                        }
                    }
                },
                {
                    header: '店铺名',
                    width: 180,
                    dataIndex: 'shopName'
                },
                {
                    header: '店铺手机',
                    width: 80,
                    dataIndex: 'shopMobile'
                },
                {
                    header: '销售跟进人',
                    width: 70,
                    dataIndex: 'followName'
                },
                {
                    header: '合计（元）',
                    width: 70,
                    dataIndex: 'totalAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '状态',
                    width: 70,
                    dataIndex: 'status',
                    renderer: function (val, style, rec, index) {
                        if (val === "PARTIAL_PAYMENT") {
                            return "部分付清";
                        } else if (val === "FULL_PAYMENT") {
                            return "<span style='color: #FF6600;font-weight:bold '>待发货</span>";
                        } else if (val === "NON_PAYMENT") {
                            return "<span style='color: #008000;font-weight:bold '>待支付</span>";
                        }else if (val === "SHIPPED") {
                            return "<span style='font-weight:bold '>已发货</span>";
                        }else if (val === "CANCELED") {
                            return "<span style='font-weight:bold;color: #666666 '>交易已取消</span>";
                        }
                    }
                },
                {
                    xtype: 'actiontextcolumn',
                    header: '操作',
                    id:'salesHardwareOrderGridAction',
                    width: 60,
                    items: [
                        {
                            getClass: function (v, meta, rec) {
                                if (me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.SUBMIT_PAYABLE") && rec.get('status') === 'NON_PAYMENT') {
                                    this.items[0].tooltip='线下支付';
                                    this.items[0].text='线下支付';
                                    return '';
                                }else{
                                    this.items[0].tooltip='';
                                    this.items[0].text='';
                                    return '';
                                }
                            },
                            handler: function(gridview, rowIndex, colIndex) {
                                this.fireEvent('hardwareOfflinePayClick',gridview, colIndex,rowIndex);
                            }
                        },
                        {
                            getClass: function (v, meta, rec) {
                                if (me.permissionUtils.hasPermission("CRM.SALES_MANAGER.HARDWARE.CANCELED") && rec.get('status') === 'NON_PAYMENT') {
                                    this.items[1].tooltip='取消交易';
                                    this.items[1].text='取消交易';
                                    return '';
                                }else{
                                    this.items[1].tooltip='';
                                    this.items[1].text='';
                                    return '';
                                }
                            },
                            handler: function(gridview, rowIndex, colIndex) {
                                this.fireEvent('cancelHardwareOrderClick',gridview, colIndex,rowIndex);
                            }
                        },
                        {
                            getClass: function (v, meta, rec) {
                                if (me.permissionUtils.hasPermission("CRM.SALES_MANAGER.HARDWARE.SHIPPED") && rec.get('status') === 'FULL_PAYMENT') {
                                    this.items[2].tooltip='发货';
                                    this.items[2].text='发货';
                                    return '';
                                }else{
                                    this.items[2].tooltip='';
                                    this.items[2].text='';
                                    return '';
                                }
                            },
                            handler: function(gridview, rowIndex, colIndex) {
                                this.fireEvent('shipHardwareClick',gridview, colIndex,rowIndex);
                            }
                        }
                    ]
                }
            ]
        });
        this.callParent(arguments);
    },
    onSearch: function () {
        var list = this, params = {},
            startTimeStrDom = list.down("[name=startTimeStr]"),
            endTimeStrDom = list.down("[name=endTimeStr]"),
            bcgogoProductDom = list.down("[name=bcgogoProductCheckBoxGroup]"),
            shopName = list.down("[name=shopName]").getValue(),
            followName = list.down("[name=followName]").getValue(),
            receiptNo = list.down("[name=receiptNo]").getValue(),
            paymentStatuses = list.down("[name=paymentStatusCheckBoxGroup]").getValue(),
            buyChannels = list.down("[name=buyChannelCheckBoxGroup]").getValue();
        if (startTimeStrDom.isValid() && endTimeStrDom.isValid()) {
            params = {
                paymentTypes:"HARDWARE",
                paymentStatuses:["NON_PAYMENT","FULL_PAYMENT","SHIPPED","CANCELED"],
                shopName: shopName,
                followName: followName,
                receiptNo: receiptNo,
                startTimeStr: startTimeStrDom.getValue(),
                endTimeStr: endTimeStrDom.getValue()
            };
            if (!Ext.isEmpty(paymentStatuses['paymentStatus'])) {
                params['paymentStatuses'] = paymentStatuses['paymentStatus'];
            }
            if(!Ext.isEmpty(bcgogoProductDom)){
                var bcgogoProducts = bcgogoProductDom.getValue();
                if (!Ext.isEmpty(bcgogoProducts['bcgogoProductId'])) {
                    params['bcgogoProductIds'] = bcgogoProducts['bcgogoProductId'];
                }
            }

            if (!Ext.isEmpty(buyChannels['buyChannel'])) {
                params['buyChannels'] = buyChannels['buyChannel'];
            }
            list.store.proxy.extraParams = params;
            list.store.loadPage(1, {callback: function (record, operation, success) {
//                var rowExpander = list.plugins[0];
//                for(var i=0;i<record.length;i++){
////                    rowExpander.toggleRow(i,record[i]);
//                }
                }
            });
            list.statBcgogoReceivableOrder(params);
        }
    },
    statBcgogoReceivableOrder: function (params) {
        var list = this;
        list.commonUtils.ajax({
            params: params,
            url: 'bcgogoReceivable.do?method=statBcgogoReceivableOrderByStatusResult',
            success: function (result) {
                if(result.data["NON_PAYMENT"]){
                    list.down("[name=nonPaymentStat]").setValue(result.data["NON_PAYMENT"]);
                }else{
                    list.down("[name=nonPaymentStat]").setValue('(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)');
                }
                if(result.data["FULL_PAYMENT"]){
                    list.down("[name=fullPaymentStat]").setValue(result.data["FULL_PAYMENT"]);
                }else{
                    list.down("[name=fullPaymentStat]").setValue('(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)');
                }
                if(result.data["SHIPPED"]){
                    list.down("[name=shippedStat]").setValue(result.data["SHIPPED"]);
                }else{
                    list.down("[name=shippedStat]").setValue('(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)');
                }
                if(result.data["CANCELED"]){
                    list.down("[name=canceledStat]").setValue(result.data["CANCELED"]);
                }else{
                    list.down("[name=canceledStat]").setValue('(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)');
                }
            }
        });
    },
    generateBcgogoProductCheckBoxGroup: function () {
        var list = this;
        list.commonUtils.ajax({
            async:false,
            url: 'bcgogoReceivable.do?method=getAllBcgogoProduct',
            params:{isSimple:true},
            success: function (result) {
                if(!result.data){
                    return;
                }
                var win = this,i=0;
                var container = {
                    xtype: 'checkboxgroup',
                    fieldLabel: '销售商品',
                    padding: 0,
                    margin: "0",
                    labelAlign:'right',
                    name: "bcgogoProductCheckBoxGroup",
                    anchor: '100%',
                    labelWidth: 80,
                    items: [

                    ]
                };
                Ext.Array.forEach(result.data, function(bcgogoProduct,index,array){
                    container.items[i] = {
                        boxLabel: bcgogoProduct['name'],
                        name: 'bcgogoProductId',
                        inputValue: bcgogoProduct['idStr'],
                        width:bcgogoProduct['name'].length*20,
                        checked:false
                    };
                    i++;
                });
                list.down('[name=bcgogoProductFieldset]').add(container);
            }
        });
    }

});