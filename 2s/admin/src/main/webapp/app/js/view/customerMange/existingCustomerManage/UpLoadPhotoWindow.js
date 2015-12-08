/**
 * 线索转出窗口
 * @author :zhangjuntao
 */
Ext.define('Ext.view.customerMange.customersClues.UpLoadPhotoWindow', {
    alias:'widget.upLoadPhotoWindow',
    extend:'Ext.window.Window',
    iconCls:'icon-user',
    layout:'fit',
    collapsible:true,
    requires:["Ext.view.sys.user.UserSelect"],
    initComponent:function () {
        var me = this;
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
                    {
                        xtype:'userSelect',
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
                    }
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

