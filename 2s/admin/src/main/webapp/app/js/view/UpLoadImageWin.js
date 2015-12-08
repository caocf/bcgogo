/**
 * 线索转出窗口
 * @author :zhangjuntao
 */
Ext.define('Ext.view.UpLoadImageWin', {
    alias: 'widget.upLoadImageWin',
    extend: 'Ext.window.Window',
    layout: 'fit',
    width: 350,
    height: 100,
    collapsible: true,
    closeAction: 'hide',
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
            items: {
                xtype: 'form',
                frame: true,
                border: false,
                isAdd: false,
                enctype: 'multipart/form-data',
                fileUpload: true,
                layout: 'form',
                buttons: [
                    {
                        text: '上传',
                        action: 'saveImage',
                        handler: function () {
                            me.saveImage();
                        }
                    }
                ],
                items: [
                    {
                        xtype: 'fieldset',
                        layout: 'hbox',
                        anchor: '100%',
                        padding: 0,
                        margin: "0 5 0 0",
                        defaults: {
                            xtype: "textfield",
                            anchor: "100%",
                            margin: "0 10 5 0",
                            width: 200,
                            labelWidth: 50
                        },
                        border: false,
                        items: [
                            {
                                xtype: 'filefield',//文件选择框
                                id: 'form-file',
                                emptyText: '请选择图片',
                                fieldLabel: '图片',
                                name: 'file',
                                buttonText: '浏览...',
                                width: 300,
                                validator: function(value){
                                    var array = ['jpg','jpeg','png'];
                                    Ext.Array.contains(array,'1');  //返回true 检查数组内是否包含指定元素
                                    var arr = value.split('.');
                                    if(!Ext.Array.contains(array,arr[arr.length-1].toLowerCase())){
                                        return '请选择正确的图片格式(jpg,jpeg,png)';
                                    }else{
                                        return true;
                                    }
                                }
//                                        //按钮参数
//                                        buttonConfig: {
//                                            iconClsiconCls: 'upload-icon'//按钮的样式，upload-icon是html页面中定义的一个样式
//                                        }
                            },
                            {
                                xtype: "hiddenfield",
                                hidden: true,
                                name: 'imageShopId'
                            },
                            {
                                xtype: "hiddenfield",
                                hidden: true,
                                name: 'imageFieldItemId'
                            },
                            {
                                xtype: "hiddenfield",
                                hidden: true,
                                name: 'imageBrowseFieldId'
                            }
                        ]
                    }

                ]
            }
        });
        me.callParent();
    },
    title: '图片上传',
    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    },
    setParentTargetWin: function (parentTargetWin) {
        this.parentTargetWin = parentTargetWin;
    },

    getParentTargetWin: function () {
        return this.parentTargetWin;
    },
    saveImage: function (callback, shopOperateScene, shopStatus) {
        var win = this,
            form = win.down("form"),
            formEl = form.getEl(),
            baseForm = form.form;

        if (baseForm.isValid()) {
            form.submit({
                clientValidation: true,
                waitMsg: '正在上传请稍候',
                waitTitle: '提示',
                url: "upYun.do?method=saveImage",
                method: 'POST',
                success: function (form, action) {
                    var result = Ext.JSON.decode(action.response.responseText);
                    if(result.success){
                        win.getParentTargetWin().down("[itemId="+form.findField("imageFieldItemId").getValue()+"]").setValue(result.imagePath);
                        win.getParentTargetWin().down("[itemId="+form.findField("imageBrowseFieldId").getValue()+"]").getEl().dom.src = result.imageURL;
                        win.close();
                    }else{
                        formEl.unmask();
                        Ext.MessageBox.show({title: '失败', msg: '上传失败!', buttons: Ext.MessageBox.OK, icon: Ext.MessageBox.ERROR});
                    }
                },
                failure: function (form, action) {
                    formEl.unmask();
                    Ext.MessageBox.show({title: '失败', msg: '上传失败!', buttons: Ext.MessageBox.OK, icon: Ext.MessageBox.ERROR});
                }
            });
        }

    }
});

