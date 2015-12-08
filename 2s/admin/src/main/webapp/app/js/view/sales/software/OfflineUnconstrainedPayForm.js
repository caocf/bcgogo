Ext.define('Ext.view.sales.software.OfflineUnconstrainedPayForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 480,
    alias: 'widget.softwareOfflineUnconstrainedPayForm',
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },

    buttons: [
        {
            text: '保存',
            action: 'save'
        }
    ],
    requires: [
        "Ext.view.sys.user.UserSelect",
        "Ext.view.customerMange.existingCustomerManage.Select"
    ],
    initComponent: function () {
        var me = this,
            userSelect = Ext.widget("userSelect", {
                typeAhead: true,
                triggerAction: 'all',
                lazyRender:true,
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
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.addEvents('create');

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
                            width: 400,
                            margin: '0 10 0 0',
                            name: 'shopName'
                        }
                    ]
                },
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
                            fieldLabel: '支付类型',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 250,
                            margin: '0 10 0 0',
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
                        }
                    ]
                },
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
                            fieldLabel: '总价',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 250,
                            margin: '0 10 0 0',
                            name: 'totalAmount'
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: '付款情况',
                            labelAlign:'right',
                            labelWidth: 60,
                            width: 250,
                            margin: '0 10 0 0',
                            value: '试用期未付款'
                        }
                    ]
                },
                {
                    xtype: 'radiogroup',
                    fieldLabel: '付款方式',
                    labelAlign:'right',
                    margin: '0 10 0 0',
                    labelWidth: 60,
                    anchor:'70%',
                    disabled: true,
                    items: [
                        {boxLabel: '全额付款', name: "receivableMethod", inputValue: "FULL"},
                        {boxLabel: '分期付款', name: "receivableMethod", inputValue: "INSTALLMENT"},
                        {boxLabel: '其他付款', name: "receivableMethod", inputValue: "UNCONSTRAINED", checked: true}
                    ]
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
                            labelAlign:'right',
                            name: 'paidAmount',
                            readOnly: true,
                            labelWidth: 70
                        },
                        {
                            fieldLabel: '收取时间',
                            labelAlign:'right',
                            xtype: "datefield",
                            name: 'paymentTime',
                            msgTarget: 'qtip',
                            format: 'Y-m-d H:i',
                            labelWidth: 70,
                            allowBlank: false
                        },
                        {
                            name: 'bcgogoReceivableOrderRecordRelationId',
                            xtype: "hiddenfield"
                        },
                        {
                            name: 'orderId',
                            xtype: "hiddenfield"
                        }
                    ]
                }
            ]
        });
        this.callParent();
    },
    save: function (form, callback) {
        var baseForm = form.form;
        if (baseForm.isValid() && form.validate(form)) {
            form.mask('正在保存 . . .');
            var params = baseForm.getValues();
            params['paymentTime'] = params['paymentTime'] ? Date.parse(params['paymentTime'].replace(/-/g, "/")) : "";
            params['totalAmount'] = form.down("[name=totalAmount]").getValue();
            params['paymentMethod'] = "DOOR_CHARGE";
            params['receivableMethod'] = "UNCONSTRAINED";
            form.commonUtils.ajax({
                url: 'bcgogoReceivable.do?method=softwareOfflineReceivable',
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
            params = baseForm.getValues(),
            paidAmount = form.down("[name=paidAmount]");
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
});