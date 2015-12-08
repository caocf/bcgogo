Ext.define('Ext.view.sales.software.OrderList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.salesSoftwareOrderList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    requires: ['Ext.app.ActionTextColumn'],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        var store = Ext.create('Ext.store.sales.BcgogoReceivableOrders');
        store.proxy.extraParams = {
            paymentTypes: "SOFTWARE",
            paymentStatuses:["NON_PAYMENT","PARTIAL_PAYMENT","FULL_PAYMENT"]
        };
        this.store = store;
        Ext.apply(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'fieldset',
                            name: "shopVersionFieldset",
                            frame:false,
                            padding:0,
                            margin: "0 5 0 0",
                            border: false
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            fieldLabel: '店铺名',
                            labelAlign:'right',
                            labelWidth: 60,
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
                        },
                        { xtype: 'tbspacer', width: 10 },
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
                        }
                    ]
                },{
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '议价状态',
                            columns: 4,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 340,
                            name:'bargainStatusCheckBoxGroup',
                            items: [
                                { boxLabel: '无议价', name: 'bargainStatus', inputValue: 'NO_BARGAIN', width: 60 },
                                { boxLabel: '待审核', name: 'bargainStatus', inputValue: 'PENDING_REVIEW', width: 60 },
                                { boxLabel: '审核拒绝', name: 'bargainStatus', inputValue: 'AUDIT_REFUSE', width: 80 },
                                { boxLabel: '审核通过', name: 'bargainStatus', inputValue: 'AUDIT_PASS', width: 80 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '状态',
                            columns: 2,
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 180,
                            name:'paymentStatusCheckBoxGroup',
                            items: [
                                { boxLabel: '待支付', name: 'paymentStatus', inputValue: 'NON_PAYMENT,PARTIAL_PAYMENT', width: 60 },
                                { boxLabel: '已支付', name: 'paymentStatus', inputValue: 'FULL_PAYMENT', width: 60 }
                            ]
                        },
                        { xtype: 'tbspacer', width: 10 },
                       ,
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
                            fieldLabel: '已支付',
                            name: 'fullPaymentStat',
                            labelAlign:'right',
                            value: '(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)',
                            labelWidth: 40,
                            margin: '0 20 0 0'
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
                    width: 120,
                    dataIndex: 'receiptNo',
                    renderer: function (val, style, rec, index) {
                        return '<span style="color: blue; cursor:pointer;">' + val + '</span>';
                    }
                },
                {
                    header: '店铺名',
                    width: 180,
                    dataIndex: 'shopName',
                    renderer: function (val, style, rec, index) {
                        if(val){
                            return "<span style='white-space:normal;'>"+val+"</span>";
                        }
                    }
                },
                {
                    header: '销售时间',
                    width: 80,
                    dataIndex: 'createdTime',
                    renderer: function (val, style, rec, index) {
                        if(val){
                            return Ext.util.Format.date(new Date(Number(val)), 'Y-m-d');
                        }
                    }
                },
                {
                    header: '销售版本',
                    width: 80,
                    dataIndex: 'shopVersion'
                },
                {
                    header: '销售跟进人',
                    width: 70,
                    dataIndex: 'followName'
                },
                {
                    header: '合计（元）',
                    width: 100,
                    dataIndex: 'totalAmount',
                    renderer: function (val, style, rec, index) {
                        if(rec.get('chargeType')==='ONE_TIME'){
                            if(rec.get('bargainStatus')==='AUDIT_PASS'){
                                return "<span style='text-decoration:line-through'>"+rec.get('oldTotalAmount')+"</span> <span style='color: #FF6600;'>"+rec.get('bargainPrice')+"</span><br><span style='color: #FF6600;'>议价通过</span>";
                            }else if(rec.get('bargainStatus')==='PENDING_REVIEW'){
                                return "<span style='text-decoration:line-through'>"+val+"</span> <span style='color: #0000FF;'>"+rec.get('bargainPrice')+"</span><br><span style='color: #0000FF;'>议价申请中</span>";
                            }else if(rec.get('bargainStatus')==='AUDIT_REFUSE'){
                                return val+" <span style='text-decoration:line-through;color: #FF0000;'>"+rec.get('bargainPrice')+"</span>"+"<br><span style='color: #FF0000;'>议价未通过</span>";
                            }else{
                                return val;
                            }
                        }else{
                            return val+"<br>（<span style='color: #FF6600;'>按年收费</span>）";
                        }

                    }
                },
                {
                    header: '已付总额（元）',
                    width: 80,
                    dataIndex: 'receivedAmount',
                    renderer: Ext.util.Format.numberRenderer('0.00')
                },
                {
                    header: '应付总额（元）',
                    width: 120,
                    dataIndex: 'receivableAmount',
                    renderer: function (val, style, rec, index) {
                        var content="<span style='color: #008000;font-weight:bold '>"+Ext.util.Format.number(val, '0.00')+"</span>";
                        if(rec.get('chargeType')==='ONE_TIME'){
                            var bcgogoReceivableOrderToBePaidRecordDTO = rec.get('bcgogoReceivableOrderToBePaidRecordDTO');
                            if(!Ext.isEmpty(bcgogoReceivableOrderToBePaidRecordDTO)){
                                if(bcgogoReceivableOrderToBePaidRecordDTO.receivableMethod=="INSTALLMENT"){
                                    content+="<br><span style='white-space:normal;'>（本次应付第"+rec.get('currentPeriodNumberInfo')+"期，共<span style='color: #008000;font-weight:bold '>"+rec.get('currentPayableAmount')+"</span>元</sapn>）"
                                }
                            }
                        }else{
                            content+="<br>(下一年应付）"
                        }
                        return content;
                    }

                },
                {
                    header: '状态',
                    width: 70,
                    dataIndex: 'status',
                    renderer: function (val, style, rec, index) {
                        if(rec.get('chargeType')==='ONE_TIME'){
                            if (val === "FULL_PAYMENT") {
                                return "<span style='color: #FF6600;font-weight:bold '>已支付</span>";
                            } else if (val === "NON_PAYMENT" || val === "PARTIAL_PAYMENT") {
                                return "<span style='color: #008000;font-weight:bold '>待支付</span>";
                            }
                        }else{
                            return "<span style='color: #008000;font-weight:bold '>第1年免费,<br></r>第2年待付</span>";
                        }

                    }
                },
                {
                    xtype: 'actiontextcolumn',
                    header: '操作',
                    id:'salesSoftwareOrderGridAction',
                    width: 80,
                    items: [
                        {
                            getClass: function (v, meta, rec) {
                                if (rec.get('chargeType')==='ONE_TIME' && me.permissionUtils.hasPermission("CRM.FINANCE_MANAGER.PAYMENT.SUBMIT_PAYABLE") && rec.get('bargainStatus')!='PENDING_REVIEW' && (rec.get('status') === 'NON_PAYMENT' || rec.get('status') === 'PARTIAL_PAYMENT')) {
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
                                this.fireEvent('softwareOfflinePayClick',gridview, colIndex,rowIndex);
                            }
                        },
                        {
                            getClass: function (v, meta, rec) {
                                if (rec.get('chargeType')==='ONE_TIME' && me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED_TRIAL.BARGAIN_APPLY") && (Ext.isEmpty(rec.get('bargainStatus')) || rec.get('bargainStatus')==="NO_BARGAIN" || rec.get('bargainStatus') === "AUDIT_REFUSE") && rec.get('status') === 'NON_PAYMENT') {
                                    if(rec.get('bargainStatus') === "AUDIT_REFUSE"){
                                        this.items[1].tooltip='再次申请议价';
                                        this.items[1].text='再次申请议价';
                                        return '';
                                    }else{
                                        this.items[1].tooltip='申请议价';
                                        this.items[1].text='申请议价';
                                        return '';
                                    }
                                }else{
                                    this.items[1].tooltip='';
                                    this.items[1].text='';
                                    return '';
                                }
                            },
                            handler: function(gridview, rowIndex, colIndex) {
                                this.fireEvent('softwareBargainApplyClick',gridview, colIndex,rowIndex);
                            }
                        },
                        {
                            getClass: function (v, meta, rec) {
                                if (rec.get('chargeType')==='ONE_TIME' && me.permissionUtils.hasPermission("CRM.CUSTOMER_MANAGER.REGISTERED_TRIAL.BARGAIN_AUDIT") && rec.get('bargainStatus')==='PENDING_REVIEW') {
                                    this.items[2].tooltip='议价审核';
                                    this.items[2].text='议价审核';
                                    return '';
                                }else{
                                    this.items[2].tooltip='';
                                    this.items[2].text='';
                                    return '';
                                }
                            },
                            handler: function(gridview, rowIndex, colIndex) {
                                this.fireEvent('softwareBargainAuditClick',gridview, colIndex,rowIndex);
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
            shopVersionDom = list.down("[name=shopVersionCheckBoxGroup]"),
            shopName = list.down("[name=shopName]").getValue(),
            followName = list.down("[name=followName]").getValue(),
            receiptNo = list.down("[name=receiptNo]").getValue(),
            paymentStatuses = list.down("[name=paymentStatusCheckBoxGroup]").getValue(),
            bargainStatuses = list.down("[name=bargainStatusCheckBoxGroup]").getValue();
        if (startTimeStrDom.isValid() && endTimeStrDom.isValid()) {
            params = {
                paymentTypes:"SOFTWARE",
                paymentStatuses:["NON_PAYMENT","PARTIAL_PAYMENT","FULL_PAYMENT"],
                shopName: shopName,
                followName: followName,
                receiptNo: receiptNo,
                startTimeStr: startTimeStrDom.getValue(),
                endTimeStr: endTimeStrDom.getValue()
            };
            if (!Ext.isEmpty(paymentStatuses['paymentStatus'])) {
                params['paymentStatuses'] = [];
                Ext.Array.forEach(Ext.Array.from(paymentStatuses['paymentStatus'],true), function(paymentStatus,index,array){
                    params['paymentStatuses'].push(paymentStatus.split(","));
                });
                params['paymentStatuses'] = Ext.Array.flatten(params['paymentStatuses']);
            }
            if(!Ext.isEmpty(shopVersionDom)){
                var shopVersions = shopVersionDom.getValue();
                if (!Ext.isEmpty(shopVersions['shopVersionId'])) {
                    params['shopVersionIds'] = shopVersions['shopVersionId'];
                }
            }

            if (!Ext.isEmpty(bargainStatuses['bargainStatus'])) {
                params['bargainStatuses'] = bargainStatuses['bargainStatus'];
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
                if(result.data["WAIT_PAYMENT"]){
                    list.down("[name=nonPaymentStat]").setValue(result.data["WAIT_PAYMENT"]);
                }else{
                    list.down("[name=nonPaymentStat]").setValue('(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)');
                }
                if(result.data["FULL_PAYMENT"]){
                    list.down("[name=fullPaymentStat]").setValue(result.data["FULL_PAYMENT"]);
                }else{
                    list.down("[name=fullPaymentStat]").setValue('(<span style="color: #0000FF;">0</span>条 <span style="color: #008000;">0.0</span>元)');
                }
            }
        });
    },
    generateShopVersionCheckBoxGroup: function () {
        var list = this;
        list.commonUtils.ajax({
            async:false,
            url: 'shopVersion.do?method=getAllShopVersion',
            params:{isSimple:true},
            success: function (json) {
                if(!json['results']){
                    return;
                }
                var win = this,i=0;
                var container = {
                    xtype: 'checkboxgroup',
                    fieldLabel: '销售版本',
                    padding: 0,
                    margin: "0",
                    labelAlign:'right',
                    name: "shopVersionCheckBoxGroup",
                    anchor: '100%',
                    labelWidth: 60,
                    items: [

                    ]
                };
                Ext.Array.forEach(json['results'], function(shopVersion,index,array){
                    container.items[i] = {
                        boxLabel: shopVersion['value'],
                        name: 'shopVersionId',
                        inputValue: shopVersion['idStr'],
                        width:shopVersion['value'].length*20,
                        checked:false
                    };
                    i++;
                });
                list.down('[name=shopVersionFieldset]').add(container);
            }
        });
    }

});