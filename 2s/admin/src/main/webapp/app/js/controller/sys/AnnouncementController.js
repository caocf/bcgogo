Ext.define('Ext.controller.sys.AnnouncementController', {
    extend: 'Ext.app.Controller',
    stores: [
        "Ext.store.sys.Announcement"
    ],

    models: [
        "Ext.model.sys.Announcement"
    ],

    views: [
        'Ext.view.sys.reminder.AnnouncementList'
    ],
    requires: [
        "Ext.view.sys.reminder.AddAnnouncement",
        "Ext.view.sys.reminder.AnnouncementEditor"
    ],
    refs: [
        {
            ref: 'announcementList',
            selector: 'sysannouncementList'
        },
        {
            ref: 'addAnnouncement',
            selector: 'addAnnouncement',
            autoCreate: true,
            xtype: 'addAnnouncement'
        },
        {
            ref: 'announcementEditor',
            selector: 'sysannouncementEditor'
        }
    ],
    init: function () {

        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'sysannouncementList': {
                afterrender: function () {
                    me.getAnnouncementList().store.proxy.extraParams = {};
                    me.getAnnouncementList().store.loadPage(1);
                }
            },
            'sysannouncementList button[action=addAnnouncement]': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_announcement_save")) {
                        view.hide();
                    }
                },
                click: function () {
                    me.commonUtils.mask();
                    me.getAddAnnouncement().show();
                    me.getAddAnnouncement().setTitle("新增公告");
                    Ext.getCmp('titleInput').setValue("");
                    Ext.getCmp('announceEditor').setValue("");
                    Ext.getCmp('releaseDateInput').setValue(new Date());
                    Ext.getCmp('announcementId').setValue("");

                }
            },
            'addAnnouncement sysannouncementEditor button[action=saveOrUpdateAnnouncement]': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_announcement_save")) {
                        view.hide();
                    }
                },
                click: me.saveOrUpdateAnnouncement
            },
            'addAnnouncement sysannouncementEditor button[action=cancellAnnouncement]': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_announcement_save")) {
                        view.hide();
                    }
                },
                click: function () {
                    Ext.getCmp('titleInput').setValue("");
                    Ext.getCmp('announceEditor').setValue("");
                    Ext.getCmp('releaseDateInput').setValue(new Date());
                    Ext.getCmp('announcementId').setValue("");
                    me.commonUtils.unmask();
                    me.getAddAnnouncement().hide();
                }
            },
            'sysannouncementList actioncolumn': {
                beforerender: function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_announcement_edit")) {
                        view.hide();
                    }
                },
                click: function (grid, cell, row, col, e) {
                    var rec = grid.getStore().getAt(row);
                    me.editAnnouncement(grid, row, col, rec);
                }
            }
        });
    },
    saveOrUpdateAnnouncement: function (combo, records, eOpts) {
        var me = this, form = combo.up("form"), formEl = form.getEl();
        var announcement = form.getValues();
        var title = announcement.title,
            content = announcement.content,
            releaseDate = announcement.releaseDate;
        if (title == "" || Ext.util.Format.trim(title) == "") {
            alert("标题不为空！");
            return;
        }
        if (title.length > 30) {
            alert("标题过长！");
            return;
        }
        if (content == "" || Ext.util.Format.trim(content) == "" || content == "<br>") {
            alert("内容不为空！");
            return;
        }
        formEl.mask();
        me.commonUtils.ajax({
            url: "sysReminder.do?method=saveOrUpdateAnnouncement",
            params: {
                idStr: announcement.idStr,
                title: title,
                content: content,
                releaseDate: releaseDate
            },
            success: function (result) {
                Ext.getCmp('titleInput').setValue("");
                Ext.getCmp('announceEditor').setValue("");
                Ext.getCmp('releaseDateInput').setValue(new Date());
                Ext.getCmp('announcementId').setValue("");
                Ext.Msg.alert('返回结果', "保存公告成功！");
                me.commonUtils.unmask();
                me.getAddAnnouncement().hide();
                me.getAnnouncementList().store.loadPage(1);
                formEl.unmask();
            }
        });

    },
    deleteAnnouncement: function (grid, row, col, rec) {
        var me = this;
        var announcement = rec.data;
        Ext.MessageBox.confirm('确认', '确认删除此条公告?', function (btn) {
            if (btn == "yes") {
                me.commonUtils.ajax({
                    url: 'sysReminder.do?method=deleteAnnouncement',
                    params: {announcementId: announcement.idStr},
                    success: function (result) {
                        if (!result.success) {
                            alert(result.msg);
                            return;
                        }
                        alert("删除成功！");
                        me.getAnnouncementList().store.loadPage(1);
                    }
                });
            }
        });
    },

    editAnnouncement: function (grid, row, col, rec) {
        var me = this;
        var announcement = rec.data;
        me.commonUtils.ajax({
            url: 'sysReminder.do?method=getAnnouncementById',
            params: {announcementId: announcement.idStr},
            success: function (announcement) {
//        if(!result.success){
//          alert(result.msg);
//          return;
//        }
                me.commonUtils.mask();
                me.getAddAnnouncement().show();
                me.getAddAnnouncement().setTitle("修改公告");
                Ext.getCmp('titleInput').setValue(announcement.title);
                Ext.getCmp('announceEditor').setValue(announcement.content);
                Ext.getCmp('releaseDateInput').setValue(announcement.releaseDate);
                Ext.getCmp('announcementId').setValue(announcement.idStr);
                Ext.getCmp('announceEditor').setHeight(300);

            }
        });
        var aPanel = me.getAnnouncementList().findParentByType('panel');
        aPanel.setActiveTab(1);

    }

});