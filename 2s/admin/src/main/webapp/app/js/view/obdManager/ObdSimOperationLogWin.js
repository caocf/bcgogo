/**
 * obd 库存导入窗口
 */
Ext.define("Ext.view.obdManager.ObdSimOperationLogWin",{
    extend:"Ext.window.Window",
    alias:"widget.ObdSimOperationLogWin",
    layout:'fit',
    width:900,
//    height:400,
    autoHeight: true,
    collapsible:true,
    closeAction:'hide',
    title:"操作日志",
    items:{
        xtype: 'grid',
        border: true,
        forceFit: true, //自动填充，即让所有列填充满gird宽度
        frame: true,
        autoHeight: true,
        autoScroll: true,
        columnLines: true,
        stripeRows: true, //每列是否是斑马线分开
        autoExpandColumn:'obdSimLogContent',
            columns: [
            {xtype: 'rownumberer',width:50,text:"No"},
            {text:"操作时间",dataIndex:"operationDateStr",width:90,xtype:'datecolumn',format:'Y-m-d H:i'},
            {text:"操作人/店",dataIndex:"userName",width:70},
            {text:"操作类型",dataIndex:"operationTypeStr",width:150},
            {text:"操作内容",dataIndex:"content",width:300,id : 'obdSimLogContent',
                renderer:function(value){
                    return value.replace("\r\n",'<br>');
                }}
        ],
        bbar : {
            xtype:'pagingtoolbar',
            id:'obdSimLog_page',
            store:'Ext.store.obdManager.ObdSimOperationLogStore',
            dock:'bottom',
            displayInfo:true
        },
        store: 'Ext.store.obdManager.ObdSimOperationLogStore'

    },
    close: function () {
        Ext.create("Ext.utils.Common").unmask();

        this.doClose();
    },
    initComponent:function(){
        var self = this;
        var _store = Ext.create('Ext.store.obdManager.ObdSimOperationLogStore');
        self.items.store = _store;
        self.items.bbar.store = _store;
        self.callParent(arguments);
    },
    setParentTargetWin: function (parentTargetWin) {
        this.parentTargetWin = parentTargetWin;
    },

    getParentTargetWin: function () {
        return this.parentTargetWin;
    }

});