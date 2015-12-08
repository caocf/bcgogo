Ext.define('Ext.view.sys.user.UserSelect', {
    extend: 'Ext.form.ComboBox',
    alias: 'widget.userSelect',
    emptyText: '所有角色',
    store: Ext.create('Ext.store.sys.Users', {
        proxy: {
            type: 'ajax',
            api: {
                read: 'user.do?method=getUserSuggestionByName'
            },
            reader: {
                type: 'json',
                root: "results"
            }
        }
    }),
    displayField: 'name',
    remoteFilter: true,  //ajax过滤开关
    queryMode: 'remote', //远程过滤
    queryParam: 'name',  //过滤字
    queryDelay: 500,         //延迟
    minChars: 1,             //The minimum number of characters the user must type before autocomplete defaults to 4 if queryMode = 'remote'
    enableKeyEvents: true,
    valueField: 'id',
    blurFn: function (fieldComp, comp, success, fail) {
        if (fieldComp.getRawValue() === fieldComp.getValue()) {
            comp.commonUtils.ajax({
                url: 'user.do?method=getActiveUserByName',
                params: {name: fieldComp.getValue()},
                success: function (result) {
                    if (!result) {
                        fail();
                    } else {
                        fieldComp.setValue(result);
                        success();
                    }
                }
            });
        } else {
            comp.down("button[action=save]").enable();
        }
    }
});