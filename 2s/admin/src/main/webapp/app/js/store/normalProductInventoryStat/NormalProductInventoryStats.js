/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-29
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.store.normalProductInventoryStat.NormalProductInventoryStats', {
    extend:'Ext.data.Store',
    model:"Ext.model.normalProductInventoryStat.NormalProductInventoryStat",
//    autoLoad:true,
//    autoSync:true,  //自动提交改变的数据
    pageSize:8,
    remoteSort:false // If false, sorting is done locally on the client.
});