/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-26
 * Time: 下午12:50
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.store.sales.SmsRechargeOrders', {
    extend:'Ext.data.Store',
    model:"Ext.model.sales.SmsRechargeOrder",
    pageSize:25,
    proxy:{
        type:'ajax',
        api:{
            read:'shopSmsAccount.do?method=searchSmsRechargeResult'
        },
        reader:{
            type:'json',
            root:"data",
            totalProperty:"total"
        },
        writer:{
            writeAllFields:true,
            type:'json'
        }
    },
    remoteSort:false // If false, sorting is done locally on the client.
});
