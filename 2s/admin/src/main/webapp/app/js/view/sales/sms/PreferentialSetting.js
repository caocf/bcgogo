/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-30
 * Time: 上午11:57
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.view.sales.sms.PreferentialSetting', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.smsPreferentialSetting',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
    enableKeyNav: true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    requires: ['Ext.app.ActionTextColumn'],
    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        me.permissionUtils = Ext.create("Ext.utils.PermissionUtils");
        var edit = Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1});
        var store = Ext.create("Ext.data.Store", {
            extend: 'Ext.data.Store',
            fields: [
                { name: 'id', type: 'string'},
                { name: 'rechargeAmount', type: "string"},
                { name: 'presentAmount', type: "string"}
            ],
            proxy:{
                type:'ajax',
                api:{
                    read:'shopSmsAccount.do?method=getSmsPreferentialPolicy'
                },
                reader:{
                    type:'json',
                    root:"data",
                    totalProperty:"total"
                }
            },
            pageSize: 5,
            data: [],
            remoteSort: false
        });
        me.store = store;
        Ext.apply(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: "displayfield",
                            value: '优惠政策：一次性充值满一定金额，赠送充值金额！'
                        }
                        ,
                        "->",
                        {
                            text: "保存",
                            xtype: 'button',
                            action: 'save',
                            iconCls: "icon-search",
                            scope: me,
                            handler: function () {
                                me.savePreferentialSetting();
                            }
                        },
                        {
                            text: "重置",
                            xtype: 'button',
                            action: 'reset',
                            iconCls: "icon-reset",
                            scope: me,
                            handler: function () {
                                me.onSearch();
                            }
                        }
                    ]
                }
            ],
            plugins: [
                edit
            ],
            columns: [
                {
                    header: 'No.',
                    xtype: 'rownumberer',
                    sortable: false
                },
                {
                    header: '充值金额（元）',
                    dataIndex: 'rechargeAmount',
                    editor : {
                        vtype : 'integer'
                    }
                },
                {
                    header: '赠送金额（元）',
                    dataIndex: 'presentAmount',
                    editor : {
                        vtype : 'integer'
                    }
                }
            ]
        });
        me.on('edit', function (editor, e) {
            e.record.commit();
        });
        Ext.apply(me, {
            items: [me]
        });
        this.callParent(arguments);
    },
    onSearch: function () {
        var me = this;
        this.store.loadPage(1, {callback: function (record, operation, success) {
            var items = me.store.data.items,i;
            var length = items.length;
            for(i = 0; i < 5 - length; i++) {
                var instance = Ext.create('Ext.model.sales.SmsPreferentialPolicy', {
                    rechargeAmount:""
                });
                me.store.insert(items.length, instance);
            }
        }});

    },
    savePreferentialSetting: function () {
        var me = this;
        var items = me.store.data.items,i;
        for(i = 0; i < items.length; i++) {
           var item = items[i];
           if(item.get("rechargeAmount") && !item.get("presentAmount")) {
               Ext.MessageBox.show({
                   title: '提示',
                   msg: "第" + (i + 1) + "行请输入赠送金额!",
                   icon: Ext.MessageBox.INFO,
                   buttons: Ext.Msg.OK
               });
               return;
           }
            if(!item.get("rechargeAmount") && item.get("presentAmount")) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "第" + (i + 1) + "行请输入充值金额!",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }
            if(item.get("rechargeAmount") && item.get("rechargeAmount") < 50) {
                Ext.MessageBox.show({
                    title: '提示',
                    msg: "第" + (i + 1) + "行充值金额必须大于50!",
                    icon: Ext.MessageBox.INFO,
                    buttons: Ext.Msg.OK
                });
                return;
            }
        }
        for(i = 0; i < items.length; i++) {
            var item = items[i];
            for(var j = 0; j < items.length; j++) {
                var item2 = items[j];
                if(i != j && item.get("rechargeAmount") && item.get("rechargeAmount") == item2.get("rechargeAmount")) {
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: "第" + (i + 1) + "行与第" + (j + 1) + "行充值金额重复",
                        icon: Ext.MessageBox.INFO,
                        buttons: Ext.Msg.OK
                    });
                    return;
                }
            }
        }
        var ids = '',rechargeAmounts = '',presentAmounts = '';
        for(i = 0; i < items.length; i++) {
            var item = items[i];
            if(item.get("rechargeAmount") && item.get("presentAmount")) {
                if(item.get("id")) {
                    ids += item.get("id") + ',';
                } else {
                    ids += 'null,';
                }
                rechargeAmounts += item.get("rechargeAmount") + ',';
                presentAmounts += item.get("presentAmount") + ',';
            }
        }
        if(ids) {
            ids = ids.substring(0,ids.length - 1);
        }
        if(rechargeAmounts) {
            rechargeAmounts = rechargeAmounts.substring(0,rechargeAmounts.length - 1);
        }
        if(presentAmounts) {
            presentAmounts = presentAmounts.substring(0,presentAmounts.length - 1);
        }
        me.commonUtils.ajax({
            url: 'shopSmsAccount.do?method=savePreferentialSetting',
            params: {ids: ids,rechargeAmounts: rechargeAmounts,presentAmounts: presentAmounts},
            success: function (result) {
                Ext.Msg.alert('返回结果', "保存成功！", function () {
                    me.onSearch();
                });
            },
            failure: function () {
                Ext.Msg.alert('返回结果', "保存失败！", function () {});
            }
        });
    }
});
