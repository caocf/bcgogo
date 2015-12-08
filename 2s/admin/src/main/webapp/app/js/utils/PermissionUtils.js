/**
 * 权限工具类
 * author:zhangjuntao
 */
Ext.define('Ext.utils.PermissionUtils', {
    alias: 'widget.permissionUtils',
    extend: 'Ext.Component',
    hasPermission: function (key) {
//        var begin = new Date().getTime();
//        var end = new Date().getTime();
//        console.log("cost-time:" + (end - begin) + " times:" + count);
        var me = this;
        for (var i = 0, max = me.resources.length; i < max; i++) {
            if (me.resources[i]['name'] === key || me.resources[i]['value'] === key) {
                return true;
            }
        }
        return  false;
    },
    initComponent: function () {
        var me = this;
        this.commonUtils = Ext.create("Ext.utils.Common");
        Ext.Ajax.request({
            url: 'resource.do?method=getResources',
            async: false,
            success: function (response) {
                Ext.apply(me, {
                    resources: Ext.JSON.decode(response.responseText)
                });
            }
        });
        this.callParent();
    }
});
