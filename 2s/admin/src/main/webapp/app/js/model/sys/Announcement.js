Ext.define('Ext.model.sys.Announcement', {
    extend:'Ext.data.Model',
    fields:[
        {name:'idStr', type:'string'},
        {name:'title', type:'string'},
        {name:'content', type:'string'},
        {name:'createDate', type:'string'},
        {name:'releaseDate', type:'string'},
        {name:'releaseMan', type:'string'},
        {name:'status', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'sysReminder.do?method=getAnnouncements'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
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