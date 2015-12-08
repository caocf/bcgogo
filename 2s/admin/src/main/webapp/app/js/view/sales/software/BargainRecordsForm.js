Ext.define('Ext.view.sales.software.BargainRecordsForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 540,
    alias: 'widget.shopBargainRecordsForm',
    layout: 'anchor',
    buttons: [
        {
            text: '再次议价申请',
            action: 'applyAgain'
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
                            name: 'name',
                            flex: 5,
                            labelWidth: 60
                        },
                        {
                            xtype: 'displayfield',
                            flex: 5,
                            fieldLabel: '店主',
                            name: 'owner',
                            labelWidth: 50
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
                            fieldLabel: '联系方式',
                            flex: 5,
                            name: 'mobile',
                            labelWidth: 60
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '软件版本',
                            labelWidth: 60,
                            flex: 5,
                            name: 'shopVersionName'
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
                }
            ]
        });
        this.callParent();
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