Ext.define('Ext.view.sales.software.BargainAuditForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 600,
    height:350,
    autoScroll:true,
    alias: 'widget.bargainAuditForm',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    buttons: [
        {
            text: '提交确认',
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
                    padding: 0,
                    anchor: '100%',
                    border: false,
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '店铺名',
                            labelAlign:'right',
                            name: 'shopName',
                            flex: 5,
                            labelWidth: 60
                        },
                        {
                            xtype: 'displayfield',
                            flex: 2,
                            fieldLabel: '店主',
                            labelAlign:'right',
                            name: 'shopOwner',
                            labelWidth: 50
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '联系方式',
                            labelAlign:'right',
                            flex: 3,
                            name: 'shopMobile',
                            labelWidth: 60
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
                            xtype: 'displayfield',
                            fieldLabel: '软件版本',
                            labelAlign:'right',
                            labelWidth: 60,
                            flex: 5,
                            name: 'shopVersion'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '原价',
                            labelAlign:'right',
                            labelWidth: 50,
                            flex: 5,
                            name: 'softPrice'
                        }
                    ]
                },
                {
                    xtype: 'dataview',
                    store: Ext.create("Ext.store.customerMange.ShopBargainRecords"),
                    name: 'shopBargainRecords',
                    tpl: Ext.create('Ext.XTemplate',
                        '<tpl for=".">',
                        '<table cellpadding="0" cellspacing="0" style="border:1px solid #bfbfbf; width:100%;">',
                        '<tr>',
                        '<td style="border:1px solid #bfbfbf;text-align:center;">申请</td>',
                        '<td>',
                        '<table cellpadding="0" cellspacing="0" style="border-bottom:1px solid #bfbfbf; width:100%;">' ,
                        '<tr>',
                        '<td style="width:40%;">申请提交时间：{applicationTime:this.formatDate()}</td>',
                        '<td style="width:30%;">提交人：{applicantName}</td>',
                        '<td style="width:30%;">申请价格：{applicationPrice}</td>',
                        '</tr>',
                        '<tr>',
                        '<td colspan="3">申请理由：{applicationReason}</td>',
                        '</tr>',
                        '</table>',
                        '</td>',
                        '</tr>',
                        '<tpl if="this.hasAudited(bargainStatus)">',
                        '<tr>',
                        '<td style="border:1px solid #bfbfbf;text-align:center;">审核</td>' +
                            '<td>' ,
                        '<table style="width:100%;">' ,
                        '<tr>',
                        '<td style="width:40%;">审核时间：{auditTime:this.formatDate()}</td>',
                        '<td style="width:30%;">审核人：{auditorName}</td>',
                        '<td style="width:30%;">审核结果：{bargainStatus:this.formatBargainStatus()}</td>',
                        '</tr>',
                        '<tr>',
                        '<td colspan="3">申请理由：{auditReason}</td>',
                        '</tr>',
                        '</table>',
                        '</td>',
                        '</tr>',
                        '</tpl>',
                        '</table>' ,
                        '</br>',
                        '</tpl>', {
                            formatDate: function (val) {
                                return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d H:i') : "";
                            },
                            formatBargainStatus: function (val) {
                                if (val == "PENDING_REVIEW") {
                                    return "待审核";
                                } else if (val == "AUDIT_REFUSE") {
                                    return "审核拒绝";
                                } else if (val == "AUDIT_PASS") {
                                    return "审核通过";
                                } else {
                                    return "无议价";
                                }
                            },
                            hasAudited: function (bargainStatus) {
                                return bargainStatus != "PENDING_REVIEW";
                            }
                        })
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '审核结果',
                    labelAlign:'right',
                    labelWidth: 60,
                    columns: 2,
                    items: [
                        {boxLabel: '审核通过', name: 'status', inputValue: 'AUDIT_PASS', width: 80},
                        {boxLabel: '审核拒绝', name: 'status', inputValue: 'AUDIT_REFUSE', checked: true, width: 80}
                    ]
                },
                {
                    xtype: 'textareafield',
                    name: 'reason',
                    labelAlign:'right',
                    fieldLabel: '理由',
                    labelWidth: 60,
                    allowBlank: false
                },
                {
                    name: 'shopId',
                    xtype: "hiddenfield"
                },
                {
                    name: 'orderId',
                    xtype: "hiddenfield"
                }
            ]
        });
        this.callParent();
    },
    save: function (form, callback) {
        var baseForm = form.form;
        if (baseForm.isValid()) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            form.commonUtils.ajax({
                url: 'shopBargain.do?method=auditShopBargainRecord',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', result.msg, function () {
                        baseForm.reset();
                        callback();
                    });
                },
                failure: function () {
                    form.unmask();
                }
            });
        }
    },
    freshShopBargainRecordPanel: function (shopId) {
        var panel = this.down("[name=shopBargainRecords]");
        panel.store.proxy.extraParams = {
            shopId: shopId
        };
        panel.store.load();
        panel.refresh();
    }
});