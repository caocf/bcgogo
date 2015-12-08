/**
 * 线索转出窗口
 * @author :zhangjuntao
 */
Ext.define('Ext.view.customerMange.customersClues.RollOutWindow', {
    alias:'widget.rollOutWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.sys.user.UserSelect","Ext.utils.Common"],
    initComponent:function () {

        var me = this,
            userSelect = Ext.widget("userSelect", {
                name:'userId',
                displayField:'name',
                valueField:'id',
                fieldLabel:'销售人员',
                allowBlank:false,
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
            operateScene:"departmentFilter"
        };
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items:{
                xtype:'panel',
                buttons:[
                    {
                        text:'保存',
                        action:'save'
                    }
                ],
                items:[
                    userSelect
                ]
            }
        });
        me.callParent();
    },
    title:'转出线索客户',
    close:function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    }
});

