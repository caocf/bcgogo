Ext.define('Ext.view.dataMaintenance.permission.ResourceForm', {
    extend: 'Ext.form.Panel',
    bodyPadding: 5,
    width: 600,
    alias: 'widget.resourceForm',
    store: 'Ext.store.dataMaintenance.Resources',
    requires: [
        'Ext.view.dataMaintenance.permission.ModelTreePicker',
        'Ext.view.dataMaintenance.SystemType',
        'Ext.view.dataMaintenance.ResourceType'
    ],
    layout: 'anchor',
    defaults: {
        anchor: '100%'

    },
    fieldDefaults: {
        labelWidth: 125,
        msgTarget: 'under',
        autoFitErrors: false
    },

    // Reset and Submit buttons
    buttons: [
        {
            id: "resourceFormReset",
            text: '重置',
            tooltip: "重置",
            handler: function () {
                this.up("form").form.reset();
            }
        },
        {
            text: '保存',
            action: 'save'
        }
    ],
    initComponent: function () {
        var me = this;
        me.addEvents('create');
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: [
                {
                    fieldLabel: 'name',
                    name: 'name',
                    xtype: "textfield",
                    allowBlank: false
                },
                {
                    fieldLabel: 'value',
                    name: 'value',
                    xtype: "textfield",
                    allowBlank: false
                },
                {
                    fieldLabel: 'systemType',
                    name: 'systemType',
                    xtype: "systemType",
                    allowBlank: false
                },
                {
                    fieldLabel: 'memo',
                    name: 'memo',
                    xtype: "textfield",
                    allowBlank: false
                },
                {
                    fieldLabel: 'type',
                    name: 'type',
                    xtype: "resourceType",
//                    regex : /^[abc]{2,4}$/,
//                    regexText : "只能输入abc",
                    allowBlank: false,
                    value: 'render',
                    listeners: {
                        scope: this,
                        'select': function (combo, records, eOpts) {
//                            if (combo.getValue() === "menu") {
//                                me.down("[name=label]").setValue("").show();
//                                me.down("[name=href]").setValue("").show();
//                                me.down("[name=grade]").setValue("").show();
//                            } else {
//                                me.down("[name=label]").setValue("").hide();
//                                me.down("[name=href]").setValue("").hide();
//                                me.down("[name=grade]").setValue("").hide();
//                                me.down("[name=parentId]").setValue("").hide();
//                            }
                        }
                    }
                },
                {
                    xtype: "hiddenfield",
                    name: 'resourceId'
                }//,
                //menu
//                {
//                    fieldLabel: 'label',
//                    name: 'label',
//                    xtype: "textfield",
//                    hidden: true
//                },
//                {
//                    fieldLabel: 'href',
//                    name: 'href',
//                    xtype: "textfield",
//                    hidden: true
//                },
//                {
//                    fieldLabel: 'grade',
//                    name: 'grade',
//                    xtype: "textfield",
//                    hidden: true,
//                    listeners: {
//                        blur: function (combo, records, eOpts) {
//                            if (combo.getValue() != "1") {
//                                me.down("[name=parentId]").setValue("").show();
//                            } else {
//                                me.down("[name=parentId]").setValue("").hide();
//                            }
//                        }
//                    }
//                },
//                {
//                    fieldLabel: 'parentId',
//                    name: 'parentId',
//                    xtype: "textfield",
//                    hidden: true
//                },
//                {
//                    xtype: "hiddenfield",
//                    name: 'menuId'
//                }
            ]
        });
        this.callParent();
    },
    addAndUpdateResource: function (callback) {
        var form = this,
            formEl = form.getEl(),
            baseForm = form.form;
        if (baseForm.isValid()) {
            console.log(baseForm.getFieldValues());
            formEl.mask('正在保存 . . .');
            form.commonUtils.ajax({
                url: 'resource.do?method=saveOrUpdateResource',
                params: baseForm.getFieldValues(),
                success: function (result) {
                    Ext.Msg.alert('返回结果', result['msg'], function () {
                        callback();
                        baseForm.reset();
                        formEl.unmask();
                    });
                },
                failure: function (response) {
                    formEl.unmask();
                }
            });
        }
    }
});