Ext.define('Ext.controller.sys.LogController', {
    extend:'Ext.app.Controller',

    stores:["Ext.store.sys.CRMOperationLogs"],

    models:["Ext.model.sys.CRMOperationLog"],

    views:[
        'Ext.view.sys.log.LogList'
    ],

    requires:[
        "Ext.utils.ComponentUtils",
        "Ext.utils.PermissionUtils",
        "Ext.utils.Common"
    ],

    refs:[
        {ref:'logList', selector:'sysLogList'}
    ],

    init:function () {
        var me = this;
        this.componentUtils = Ext.create("Ext.utils.ComponentUtils");
        this.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        this.commonUtils = Ext.create("Ext.utils.Common");
        this.control({
            'sysLogList':{
                afterrender:function () {
                    me.getLogList().store.loadPage(1);
                }
            },
            'sysLogList #searchLogComponent':{
                beforerender:function (view, eOpts) {
                    if (!me.permissionUtils.hasPermission("CRM_sys_log_search")) {
                        view.hide();
                    }
                }
            },
            'sysLogList button[action=search]':{
                beforerender:function (view, eOpts) {
                    return me.permissionUtils.hasPermission("CRM_sys_log_search");
                },
                click:me.searchNormalProduct
            }
        });
    },
    onSearchLogs:function () {
        var me = this,
            logList = me.getLogList(),
            module = logList.down("[name=module]").getValue(),
            ipAddress = logList.down("[name=ipAddress]").getValue(),
            type = logList.down("[name=type]").getValue(),
            content = logList.down("[name=content]").getValue(),
            operateTimeStartCmp = logList.down("[name=operateTimeStart]"),
            operateTimeEndCmp = logList.down("[name=operateTimeEnd]"),
            userNo = logList.down("[name=userNo]").getValue(),
            operateTimeStart = operateTimeStartCmp.getValue() ? new Date(operateTimeStartCmp.getValue()).getTime() : "",
            operateTimeEnd = operateTimeEndCmp.getValue() ? (new Date(operateTimeEndCmp.getValue()).getTime() + 24 * 60 * 60 * 1000 - 1) : "";   //一天多少秒
        logList.store.proxy.extraParams = {
            userNo:userNo,
            content:content,
            operateTimeStart:operateTimeStart,
            operateTimeEnd:operateTimeEnd,
            ipAddress:ipAddress,
            type:type,
            module:module
        };
        logList.store.loadPage(1);
    }

});