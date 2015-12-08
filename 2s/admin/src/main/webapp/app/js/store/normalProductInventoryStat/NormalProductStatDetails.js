/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-3
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */

Ext.define('Ext.store.normalProductInventoryStat.NormalProductStatDetails', {
    extend:'Ext.data.Store',
    model:"Ext.model.normalProductInventoryStat.NormalProductStatDetail",
//    autoLoad:true,
//    autoSync:true,  //自动提交改变的数据
    pageSize:8,
    remoteSort:false // If false, sorting is done locally on the client.
});
