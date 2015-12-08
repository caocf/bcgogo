Ext.define("Ext.view.customerMange.RecommendTreeImgUpload",{
    extend:"Ext.window.Window",
    alias:"widget.RecommendTreeImgUpload",
    layout:'fit',
    width:350,
    height:100,
    collapsible:true,
    closeAction:'hide',
    title:"广告类目图标",
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
                action: 'saveRecommendTreeImg',
                handler: function () {
                    this.up("RecommendTreeImgUpload").saveRecommendTreeImg();
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
                    id:"recommend-tree-img",
                    emptyText: '请选择需要添加的图片',
                    fieldLabel: '选择文件',
                    labelWidth:70,
                    name: 'file',
                    buttonText: '浏览...',
                    width: 300,
                    validator: function(value){
                        var array = ['jpg','png'];
                        var arr = value.split('.');
                        if(!Ext.Array.contains(array,arr[arr.length-1].toLowerCase())){
                            return '请选择正确的图片';
                        }else{
                            return true;
                        }
                    }
                },{
                    xtype:"hiddenfield",
                    hidden: true,
                    name: 'nodeId'
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
    saveRecommendTreeImg:function(){
        var win = this,
            form = win.down("form"),
            formEl = form.getEl(),
            baseForm = form.form;

        if (baseForm.isValid()) {
            baseForm.submit({
                clientValidation: true,
                waitMsg: '正在上传请稍候',
                waitTitle: '提示',
                url: "shopAd.do?method=addRecommendImg",
                method: 'POST',

                success: function (form, action) {
                    formEl.unmask();
                    Ext.MessageBox.show({
                        title: '成功添加图片',
                        msg:'成功添加图片',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.INFO
                    });
                    win.close();
                },
                failure: function (form, action) {
                    var result = Ext.JSON.decode(action.response.responseText);
                    formEl.unmask();
                    Ext.MessageBox.show({
                        title: '失败',
                        msg: '成功添加图片',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR
                    });
                }
            });
        }
    }

});