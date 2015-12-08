/**
 * obd 库存导入窗口
 */
Ext.define("Ext.view.obdManager.ObdImportInventoryWin",{
   extend:"Ext.window.Window",
    alias:"widget.ObdImportInventoryWin",
    layout:'fit',
    width:350,
    height:100,
    collapsible:true,
    closeAction:'hide',
    title:"obd库存导入",
    items:{
        xtype:'form',
        frame:true,
        border:false,
        isAdd:false,
        enctype:'multipart/form-data',
        fileUpload:true,
        layout:'form',
        buttons:[
            {
                text: '上传',
                action: 'saveObdInventory',
                handler: function () {
                    this.up("ObdImportInventoryWin").saveObdInventory();
                }
            }
        ],
        items:[
            {
                xtype:'fieldset',
                layout: 'hbox',
                anchor: '100%',
                padding: 0,
                border:false,
                margin: "0 5 0 0",
                defaults: {
                    xtype: "textfield",
                    anchor: "100%",
                    margin: "0 10 5 0",
                    width: 200,
                    labelWidth: 50
                },
                items:[{
                    xtype:"filefield",
                    id:"obd-import-form-file",
                    emptyText: '请选择需要导入的OBD库存文件',
                    fieldLabel: '选择文件',
                    labelWidth:70,
                    name: 'file',
                    buttonText: '浏览...',
                    width: 300,
                    validator: function(value){
                        var array = ['xls'];
                        var arr = value.split('.');
                        if(!Ext.Array.contains(array,arr[arr.length-1].toLowerCase())){
                            return '请选择正确的模板导入';
                        }else{
                            return true;
                        }
                    }
                }]

            }
        ]
    },
    close: function () {
        Ext.create("Ext.utils.Common").unmask();
        this.doClose();
    },
    initComponent:function(){
        var self = this;

        self.callParent(arguments);
    },
    setParentTargetWin: function (parentTargetWin) {
        this.parentTargetWin = parentTargetWin;
    },

    getParentTargetWin: function () {
        return this.parentTargetWin;
    },
    saveObdInventory:function(){
        var win = this,
            form = win.down("form"),
            formEl = form.getEl(),
            baseForm = form.form;

        if (baseForm.isValid()) {
            baseForm.submit({
                clientValidation: true,
                waitMsg: '正在上传请稍候',
                waitTitle: '提示',
                url: "obdManage.do?method=uploadObdInventory",
                method: 'POST',

                success: function (form, action) {
                        var result = Ext.JSON.decode(action.response.responseText);
                    var importResult = result["importResult"];
                    var message;
                    if (importResult["failCount"] > 0) {
                        message = "导入【" + importResult["totalCount"] + "】条数据，其中成功导入【" + importResult["successCount"]
                            + "】条,失败【" + importResult["failCount"] + "】条";
                    } else {
                        message = "成功导入【" + importResult["successCount"] + "】条数据"
                    }
                    formEl.unmask();
                    Ext.MessageBox.show({
                        title: '导入完成',
                        msg:message,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.INFO
                    });
                    win.getParentTargetWin().resetSearch();
                    win.getParentTargetWin().onSearch();
                    Ext.create("Ext.utils.Common").unmask();

                },
                failure: function (form, action) {
                    var result = Ext.JSON.decode(action.response.responseText);
                    var message = result["importResult"]["message"];
                    formEl.unmask();
                    Ext.MessageBox.show({title: '失败', msg:message, buttons: Ext.MessageBox.OK, icon: Ext.MessageBox.ERROR});
                }
            });
        }
    }

});