Ext.define('Ext.controller.sys.FestivalController', {
    extend: 'Ext.app.Controller',

    stores: [
        "Ext.store.sys.Festival"
    ],

    models: [
        "Ext.model.sys.Festival"
    ],

    views: [
        "Ext.view.sys.reminder.FestivalList"
    ],
    requires: [
        "Ext.view.sys.reminder.AddFestival",
        "Ext.view.sys.reminder.FestivalEditor"
    ],

    refs: [
        {
            ref: 'festivalList',
            selector: 'sysfestivalList'
        },
        {
            ref: 'addFestival',
            selector: 'addFestival',
            autoCreate: true,
            xtype: 'addFestival'
        },
        {
            ref: 'festivalEditor',
            selector: 'sysfestivalEditor'
        }
    ],
    init: function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'sysfestivalList': {
                afterrender: function () {
                    me.getFestivalList().store.proxy.extraParams = {};
                    me.getFestivalList().store.loadPage(1);
                }
            },
            'sysfestivalList button[action=addFestival]': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_festival_save")) {
                        view.hide();
                    }
                },
                click: function () {
                    me.getAddFestival().setTitle("新增节日");
                    Ext.getCmp('festivalTitleInput').setValue("");
                    Ext.getCmp('festivalPreDay').setValue("");
                    Ext.getCmp('festivalReleaseDateInput').setValue(new Date());
                    Ext.getCmp('festivalId').setValue("");
                    me.commonUtils.mask();
                    me.getAddFestival().show();
                }
            },
            'addFestival sysfestivalEditor button[action=saveOrUpdateFestival]': {
                click: me.saveOrUpdateFestival
            },
            'addFestival sysfestivalEditor button[action=cancellFestival]': {
                click: function () {
                    Ext.getCmp('festivalTitleInput').setValue("");
                    Ext.getCmp('festivalReleaseDateInput').setValue(new Date());
                    Ext.getCmp('festivalPreDay').setValue("");
                    Ext.getCmp('festivalId').setValue("");
                    me.commonUtils.unmask();
                    me.getAddFestival().hide();
                }
            },
            'sysfestivalList actioncolumn': {
                click: function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    var index = me.componentUtils.getActionColumnItemsIndex(e);
                    if (index === Ext.utils.ComponentUtils.ACTION_COLUMN_FIRST) {
                        me.editFestival(grid, row, col, rec);
                    } else if (index === Ext.utils.ComponentUtils.ACTION_COLUMN_SECOND) {
                        me.deleteFestival(grid, row, col, rec);
                    }
                }
            }
        });
    },
    saveOrUpdateFestival: function (combo, records, eOpts) {
        var me = this, form = combo.up("form"), formEl = form.getEl();
        var festival = form.getValues();
        var title = festival.title,
            releaseDate = festival.releaseDate;
        if (title == "" || Ext.util.Format.trim(title) == "") {
            alert("节日名不为空！");
            return;
        }
        if (title.length > 3) {
            alert("节日名过长！");
            return;
        }
        formEl.mask();
        me.commonUtils.ajax({
            url: "sysReminder.do?method=saveOrUpdateFestival",
            params: {
                idStr: festival.idStr,
                title: title,
                preDay: festival.preDay,
                releaseDate: releaseDate
            },
            success: function (result) {
                Ext.getCmp('festivalTitleInput').setValue("");
                Ext.getCmp('festivalReleaseDateInput').setValue(new Date());
                Ext.getCmp('festivalPreDay').setValue("");
                Ext.getCmp('festivalId').setValue("");
                Ext.Msg.alert('返回结果', "保存节日成功！");
                me.commonUtils.unmask();
                me.getAddFestival().hide();
                me.getFestivalList().store.loadPage(1);
                formEl.unmask();
            }
        });

    },
    deleteFestival: function (grid, row, col, rec) {
        var me = this;
        var festival = rec.data;
        Ext.MessageBox.confirm('确认', '确认删除此条节日?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url: 'sysReminder.do?method=deleteFestival',
                    params: {festivalId: festival.idStr},
                    success: function (result) {
                        if (!result.success) {
                            alert(result.msg);
                            return;
                        }
                        alert("删除成功！");
                        me.getFestivalList().store.loadPage(1);
                    }
                });
            }
        });
    },
    editFestival: function (grid, row, col, rec) {
        var me = this;
        var festival = rec.data;
        me.commonUtils.ajax({
            url: 'sysReminder.do?method=getFestivalById',
            params: {festivalId: festival.idStr},
            success: function (festival) {
                me.commonUtils.mask();
                me.getAddFestival().show();
                me.getAddFestival().setTitle("修改节日");
                Ext.getCmp('festivalTitleInput').setValue(festival.title);
                Ext.getCmp('festivalReleaseDateInput').setValue(festival.releaseDate);
                Ext.getCmp('festivalId').setValue(festival.idStr);
                Ext.getCmp('festivalPreDay').setValue(String(festival.preDay));
            }
        });
    }

});