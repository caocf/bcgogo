Ext.define('Ext.model.customerMange.OperateHistoryList', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'operateType', type:'string'},
        {name:'operateShopId', type:'string'},
        {name:'operateUserId', type:'string'},
        {name:'operateTime', type:'string'},
        {name:'operateUserName', type:'string'},
        {name:'trialStartTime', type:'string'},   //试用起始时间
        {name:'trialEndTime', type:'string'},   //试用结束时间
        {name:'reason', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'shopManage.do?method=getLatestShopOperateHistoryList',
            update:'shopManage.do?method=createShopOperateHistory'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
        },
        writer:{
            writeAllFields:true,
            type:'json',
            root:"results"
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