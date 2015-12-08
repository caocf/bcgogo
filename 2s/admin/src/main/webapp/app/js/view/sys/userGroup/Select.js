Ext.define('Ext.view.sys.userGroup.Select', {
    extend:'Ext.form.ComboBox',
    alias:'widget.userGroupSelect',
    emptyText:'所有角色',
    store:Ext.create('Ext.store.sys.UserGroups'),
    displayField:'name',
    remoteFilter:true, //ajax过滤开关
    queryMode:'remote', //远程过滤
    queryParam:'name', //过滤字
    queryDelay:500, //延迟
    editable:false,
    minChars:1, //The minimum number of characters the user must type before autocomplete defaults to 4 if queryMode = 'remote'
    enableKeyEvents:true,
    blurFn: function (fieldComp, comp, success, fail) {
//        if (fieldComp.getRawValue() === fieldComp.getValue()) {
//            comp.commonUtils.ajax({
//                url: 'userGroup.do?method=getActiveUserByName',
//                params: {name: fieldComp.getValue()},
//                success: function (result) {
//                    if (!result) {
//                        fail();
//                    } else {
//                        fieldComp.setValue(result);
//                        success();
//                    }
//                }
//            });
//        } else {
//            comp.down("button[action=save]").enable();
//        }
    }
});