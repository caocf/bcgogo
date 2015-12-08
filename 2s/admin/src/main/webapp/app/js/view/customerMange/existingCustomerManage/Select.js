/**
 * zhangjuntao
 * shop name select
 * 需呀引入commonUtils
 */
Ext.define('Ext.view.customerMange.existingCustomerManage.Select', {
    extend: 'Ext.form.ComboBox',
    alias: 'widget.shopSelect',
    emptyText: '请输入店铺名',
    initComponent: function () {
        var me = this,
        store = Ext.create('Ext.store.customerMange.Shops', {
            pageSize: 15,
            proxy: {
                type: 'ajax',
                api: {
                    read: 'shopManage.do?method=getShopSuggestionByName'
                },
                reader: {
                    type: 'json',
                    root: "results",
                    totalProperty: "totalRows"
                }
            }});
        Ext.apply(me, {
            store: store
        });
        this.callParent(arguments);
    },
    displayField: 'name',
    remoteFilter: true, //ajax过滤开关
    queryMode: 'remote', //远程过滤
    queryParam: 'shopName', //过滤字
    queryDelay: 500, //延迟
    minChars: 1, //The minimum number of characters the user must type before autocomplete defaults to 4 if queryMode = 'remote'
    enableKeyEvents: true,
    valueField: 'id',
    blurFn: function (fieldComp, comp, success, fail) {
        if (fieldComp.getRawValue() === fieldComp.getValue()) {
            comp.commonUtils.ajax({
                url: 'shopManage.do?method=getActiveUsingShopByName',
                params: {shopName: fieldComp.getValue()},
                success: function (result) {
                    if (!result) {
                        fail();
                    } else {
                        fieldComp.store.loadData([
                            {name: fieldComp.getValue(), id: result['id']}
                        ]);
                        fieldComp.setValue(result['id']);
                        success();
                    }
                }
            });
        } else {
            comp.down("button[action=save]").enable();
        }
    }
});