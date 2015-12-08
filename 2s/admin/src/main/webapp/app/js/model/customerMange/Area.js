/**
 * 区域
 */
Ext.define('Ext.model.customerMange.Area', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'no', type:'string'},
        {name:'parentNo', type:'string'},
        {name:'name', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'shopManage.do?method=selectArea'
        },
        reader:{
            type:'json',
            root:"results"
        },
        writer:{
            writeAllFields:true,
            type:'json'
        }
    },
    listeners:{
        exception:function (proxy, response, operation) {
            Ext.MessageBox.show({
                title:'错误异常',
                msg:operation.getError(),
                icon:Ext.MessageBox.ERROR,
                buttons:Ext.Msg.OK
            });
        }
    }

})