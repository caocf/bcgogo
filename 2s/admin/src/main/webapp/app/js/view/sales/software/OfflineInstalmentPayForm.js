Ext.define('Ext.view.sales.software.OfflineInstalmentPayForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 900,
    alias: 'widget.softwareOfflineInstalmentPayForm',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    fieldDefaults: {
        labelWidth: 125,
        msgTarget: 'side',
        autoFitErrors: false
    },

    buttons: [
        {
            text: '保存',
            action: 'save'
        }
    ],
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.sales.InstalmentPlanAlgorithmSelect",
        "Ext.view.customerMange.existingCustomerManage.Select"
    ],
    initComponent: function () {
        var me = this,
            userSelect = me.createUserSelect();
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: [
                {
                    xtype: 'fieldset',
                    layout: 'hbox',
                    anchor: '100%',
                    padding: 0,
                    margin: '10 0 5 0',
                    border: false,
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: '店铺名',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 300,
                            name: 'shopName'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '支付类型',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 200,
                            name: 'paymentType',
                            renderer: function (val, style, rec, index) {
                                return val == 'HARDWARE' ? "硬件购买费用" : "软件购买费用";
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '软件版本',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 200,
                            name: 'shopVersion'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '总价',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 200,
                            name: 'totalAmount'
                        }
                    ]
                },
                {
                    xtype: 'dataview',
                    name: 'instalmentPlanId',
                    store: Ext.create("Ext.store.sales.InstalmentPlanItems"),
                    tpl: Ext.create('Ext.XTemplate',
                        '<table cellpadding="0" cellspacing="0" class="software_receivable_form_table">',
                        '<tr>',
                        '<td style="width:70px;"><table cellpadding="0" cellspacing="0"><tr style="height: 18px"><td>期数</td></tr><tr style="height: 18px"><td>支付比例</td></tr><tr style="height: 18px"><td>应付金额</td></tr><tr style="height: 18px"><td>付款状态</td></tr><tr style="height: 32px"><td>付款截止<br></td></tr></table></td>',
                        '<tpl for="."><td><table cellpadding="0" cellspacing="0"><tr style="height: 18px"><td>{periodNumber}期</td></tr><tr style="height: 18px"><td>{proportion:this.radio()}</td></tr><tr style="height: 18px"><td>{currentAmount}</td></tr>',
                        '<tpl if="this.isPaid(statusValue)">',
                        '<tr style="height: 18px;color:green;"><td>{statusValue}</td></tr>',
                        '<tpl else>',
                        '<tr style="height: 18px;color:red;"><td>{statusValue}</td></tr>',
                        '</tpl>',
                        '<tpl if="this.isPaid(statusValue)">',
                        '<tr style="height: 32px"><td>--</td></tr>',
                        '<tpl else>',
                        '<tr style="height: 32px"><td>{endTimeStr}</td></tr>',
                        '</tpl>',

                        '</table></td></tpl>',
                        '</tr>',
                        '</table>', {
                            radio: function (proportion) {
                                return Number(proportion) * 100 + "%";
                            },
                            isPaid: function (statusValue) {
                                return statusValue == "已付";
                            }
                        })
                },
                {
                    xtype: 'fieldset',
                    title: '本次支付内容',
                    layout: 'anchor',
                    margin: "10 0 0 0",
                    collapsible: false,
                    collapsed: false,
                    items: [
                        userSelect,
                        {
                            xtype: 'textfield',
                            fieldLabel: '收取金额',
                            name: 'paidAmount',
                            labelAlign:'right',
                            vtype: 'money',
                            msgTarget: 'qtip',
                            allowBlank: false,
                            labelWidth: 70,
                            listeners: {
                                blur: function () {
                                    var receivableAmount = Ext.util.Format.number(me.down("[name=receivableAmount]").getValue(), '0'),
                                        paidAmount = Ext.util.Format.number(me.down("[name=paidAmount]").getValue(), '0'),
                                        mimPaidAmount = Ext.util.Format.number(me.down("[name=paidAmount]").mimPaidAmount, '0');
                                    if (Number(receivableAmount) < Number(paidAmount) && (Math.abs(Number(receivableAmount) - Number(paidAmount))).toFixed(0) > 0) {
                                        me.down("[name=paidAmount]").setValue(mimPaidAmount);
                                        Ext.MessageBox.show({
                                            title: '提示',
                                            msg: '账单应还金额￥' + receivableAmount + '，最低还款额￥' + mimPaidAmount + '！',
                                            buttons: Ext.MessageBox.OK,
                                            fn: function () {
                                                me.down("[name=paidAmount]").setValue(mimPaidAmount);
                                            },
                                            icon: Ext.MessageBox.WARNING
                                        });
                                    }
                                    if (Number(paidAmount) < Number(mimPaidAmount)) {
                                        Ext.MessageBox.show({
                                            title: '提示',
                                            msg: '最低还款额￥' + mimPaidAmount + '！',
                                            buttons: Ext.MessageBox.OK,
                                            fn: function () {
                                                me.down("[name=paidAmount]").setValue(mimPaidAmount);
                                            },
                                            icon: Ext.MessageBox.WARNING
                                        });
                                    }
                                }
                            }
                        },
                        {
                            fieldLabel: '收取时间',
                            xtype: "datefield",
                            labelAlign:'right',
                            name: 'paymentTime',
                            msgTarget: 'qtip',
                            format: 'Y-m-d H:i',
                            labelWidth: 70,
                            allowBlank: false,
                            listeners: {
                                beforerender: function (comp, e, eOpts) {
                                    comp.setMaxValue(new Date(new Date().getTime() + 24 * 60 * 60 * 1000));
                                    comp.setValue(new Date());
                                }
                            }
                        },
                        {
                            name: 'bcgogoReceivableOrderRecordRelationId',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'receivableAmount',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'orderId',
                            xtype: "hiddenfield"
                        }
                    ]
                }
            ]
        })
        ;
        this.callParent();
    },
    createUserSelect: function () {
        var me = this,
            userSelect = Ext.widget("userSelect", {
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                fieldLabel: '收取人',
                labelAlign:'right',
                labelWidth: 70,
                name: 'payeeId',
                xtype: "userSelect",
                valueField: 'id',
                msgTarget: 'qtip',
                allowBlank: false,
                listeners: {
                    blur: function (comp, e, eOpts) {
                        comp.blurFn(comp, me,
                            function () {
                                me.down("button[action=save]").enable();
                            },
                            function () {
                                Ext.Msg.alert('返回结果', " 您输入的销售人员名不正确！", function () {
                                    me.down("button[action=save]").disable();
                                });
                            });
                    }
                }
            });
        userSelect.store.proxy.extraParams = {
            operateScene: ""
        };
        return userSelect;

    },
    freshInstalmentPlanPanel: function (instalmentPlanId) {
        var instalmentPlanPanel = this.down("[name=instalmentPlanId]");
        instalmentPlanPanel.store.proxy.extraParams = {
            instalmentPlanId: instalmentPlanId
        };
        instalmentPlanPanel.store.load();
        instalmentPlanPanel.refresh();
    },
    save: function (form, callback) {
        var baseForm = form.form;
        if (baseForm.isValid() && form.validate(form)) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            params['paymentTime'] = params['paymentTime'] ? Date.parse(params['paymentTime'].replace(/-/g, "/")) : "";
            params['totalAmount'] = form.down("[name=totalAmount]").getValue();
            params['paymentMethod'] = "DOOR_CHARGE";
            form.commonUtils.ajax({
                url: 'bcgogoReceivable.do?method=instalmentReceivable',
                params: params,
                success: function (result) {
                    Ext.Msg.alert('返回结果', "保存成功！", function () {
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
    validate: function (form) {
        var baseForm = form.form,
            params = baseForm.getValues();
        if (!params['paymentTime']) {
            Ext.MessageBox.show({
                title: '提示',
                msg: '保存失败，收取时间不能为空！!',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING
            });
            return false;
        }
        return true;
    }
})
;